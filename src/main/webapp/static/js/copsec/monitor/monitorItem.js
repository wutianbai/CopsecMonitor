let oTable;//定义变量名，用于存放dataTable对象，一般定义为全局的比较好
let monitorItemType;

function page(id, url, newId) {
    $.ajax({//使用ajax的方式获取
        url: url,//异步请求的接口地址
        method: "POST",//请求方式
        dataType: "json",//期待的数据返回类型
        async: true,//是否异步
        data: {},//请求参数，如果有可添加，键值对的形式或者JSON字符串都可以，根据后台来
        success: function (data) {//服务器响应成功后执行的回调
            //初始化datatable
            if (typeof oTable != "undefined") {
                //如果已经被实例化，则销毁再实例化
                oTable.fnDestroy();
            }

            oTable = $("#" + id).dataTable({//注意#infoTable是需创建为dataTable的表格,使用jQuery选择器
                "processing": true,//数据加载时显示进度条
                "searching": true,//启用搜索功能
                "serverSide": false,//是否开启服务端分页(不开就是客户端分页)
                "sServerMethod": "POST",//若使用服务端分页，则设置请求方式为“POST”，可改
                "info": true, //分页信息提示等等
                "paging": true,//是否分页
                "pagingType": "full_numbers",//分页时会有首页，尾页，上一页和下一页四个按钮,加上数字按钮
                "bLengthChange": true, //开关，是否显示每页显示多少条数据的下拉框
                "aLengthMenu": [10, 25, 50, 100],
                'iDisplayLength': 10, //每页初始显示5条记录
                'bFilter': true,  //是否使用内置的过滤功能
                "bInfo": true, //开关，是否显示表格的一些信息(当前显示XX-XX条数据，共XX条)
                "bPaginate": true, //开关，是否显示分页器
                "bSort": true, //是否可排序
                "oLanguage": {  //语言转换
                    "sProcessing": "正在加载数据...",
                    "sInfo": "显示第 _START_ 至 _END_ 项，共 _TOTAL_ 项",
                    "sLengthMenu": "每页显示 _MENU_ 项",
                    "sInfoEmpty": "没有数据",
                    "sInfoFiltered": "(从 _MAX_ 条数据中检索)",
                    "sLoadingRecords": "检索中...",
                    "sZeroRecords": "没有检索到数据",
                    "sSearch": "搜索",
                    "oPaginate": {
                        "sFirst": "《",
                        "sPrevious": "<",
                        "sNext": ">",
                        "sLast": "》"
                    },
                    "oAria": {
                        "sSortAscending": ": 以升序排列此列",
                        "sSortDescending": ": 以降序排列此列"
                    }
                },
                "data": data.data[0],//若使用客户端分页，则将表格的数据填写到data属性中，需要数组,数组里面要求是对象
                "aoColumns": [//渲染每一列，其实就是配置表头和数据对应显示到哪一列中
                    {
                        "mData": "monitorId",
                        "sClass": "text-center",
                        "sTitle": '选择',
                        "width": "7%",
                        "mRender": function (d, type, full, meta) {
                            return '<input type="checkbox" class="cbr" value="' + d + '">';
                        }
                    },
                    {
                        "mData": "monitorId",//读取数组的对象中的id属性
                        "sTitle": "序号",//表头
                        "sClass": "text-center",
                        "width": "7%",
                        "mRender": function (d, type, full, meta) {//如果需要显示的内容需根据数据封装加工的就写这个属性，0
                            //回调中有4个参数，d：对应mData中的属性的值；type：对应值的类型；full：对应当前这一行的数据，meta对应dataTable的配置
                            //如果不清楚可以使用console.log();打印出来看看
                            return meta.row + 1 + meta.settings._iDisplayStart;
                        }
                    },
                    {
                        "mData": "monitorName",
                        "sTitle": "名称",
                        "mRender": function (d, type, full, meta) {
                            let str = d;
                            if (full.monitorId === newId) {
                                str = d + '<span class="label label-success">New</span>';
                            }
                            return str;
                        }
                    },
                    {
                        "mData": "monitorItemType",
                        "sTitle": "信息类型",
                        "mRender": function (d, type, full, meta) {
                            monitorItemType = data.data[2];
                            return data.data[1][d];
                        }
                    },
                    {
                        "mData": "monitorType",
                        "sTitle": "类别",
                        "width": "8%",
                        "mRender": function (d, type, full, meta) {
                            return data.data[3][d];
                        }
                    },
                    {
                        "mData": "item",
                        "sTitle": "监控数据",
                        "mRender": function (d, type, full, meta) {
                            let str = '';
                            if (!isEmpty(d)) {
                                if (!isEmpty(d.instanceName) && d.instanceName !== "") {
                                    str = "路径[" + d.instanceName + "]" + "名称[" + d.nickname + "]";
                                } else if (!isEmpty(d.logPath) && d.logPath !== "") {
                                    str = "路径[" + d.logPath + "]" + "阈值[" + d.threadHold + "]";
                                } else {
                                    str = d;
                                }
                            }
                            return str;
                        }
                    },
                    {
                        "mData": "monitorId",
                        "sTitle": "操作",
                        "sClass": "text-center",
                        "width": "10%",
                        "mRender": function (d, type, full) {
                            let str = '<a class="btn btn-secondary btn-sm btn-icon icon-left" onclick=\'updateData(' + JSON.stringify(full) + ')\'><i class="fa-pencil"></i></a>';
                            str += '<a class="btn btn-danger btn-sm btn-icon icon-left" onclick=\'deleteData(' + JSON.stringify(full) + ')\'><i class="fa-trash"></i></a>';
                            return str;
                        }
                    }
                ]
            });

            $("#monitorItemType").empty();
            $.each(data.data[1], function (index, value) {
                $("#monitorItemType").append("<option value=" + index + ">" + value + "</option>");
            });
            reloadSelect("monitorItemType", "选择监控信息类型...");
            changeItemLabel();

            // $("#monitorType").empty();
            // $.each(data.data[3], function (index, value) {
            //     $("#monitorType").append("<option value=" + index + ">" + value + "</option>");
            // });
            // reloadSelect("monitorType", "选择监控类别...");
        }
    });
}

jQuery(function () {
    page("monitorItemTable", contextPath + "monitor/monitorItem/get");

    // Replace checkboxes when they appear
    let $state = $("#monitorItemTable thead input[type='checkbox']");
    $("#monitorItemTable").on('draw.dt', function () {
        cbr_replace();
        $state.trigger('change');
    });
});

function changeItemLabel() {
    let m = $("#monitorItemType").val();
    if (m === "CERT70" || m === "CERT40") {
        $("#itemLabel").text("监控数据");
        $('#itemRow').css("display", 'none');
        $('#certRow').css("display", 'block');
        $('#logRow').css("display", 'none');
        $("#item").val("");
        $("#logPath").val("");
        $("#threadHold").val("");
    } else if (m === "ACCESSLOG" || m === "PROXYLOG") {
        $("#itemLabel").text("监控数据");
        $('#itemRow').css("display", 'none');
        $('#certRow').css("display", 'none');
        $('#logRow').css("display", 'block');
        $("#item").val("");
        $("#instanceName").val("");
        $("#nickname").val("");
    } else {
        $("#itemLabel").text("监控数据(" + monitorItemType[m] + ")");
        $('#itemRow').css("display", 'block');
        $('#certRow').css("display", 'none');
        $('#logRow').css("display", 'none');
        $("#instanceName").val("");
        $("#nickname").val("");
        $("#logPath").val("");
        $("#threadHold").val("");
    }
}

/**
 * 添加监控项
 */
function addData() {
    //清空数据
    let $modal = $("#monitorItemModal").find(".modal-body");
    $modal.find('input').removeAttr('disabled').val('');
    changeItemLabel();

    let $confirmButton = $("<button class='btn btn-success'>保存</button>");
    addButton("monitorItemModal", "添加监控项", $confirmButton);

    $confirmButton.on("click", function () {
        let monitorId = Math.uuidFast(),
            monitorName = $("#monitorName").val(),
            monitorItemType = $("#monitorItemType").val(),
            // monitorType = $("#monitorType").val(),
            item = $("#item").val(),
            instanceName = $("#instanceName").val(),
            nickname = $("#nickname").val(),
            logPath = $("#logPath").val(),
            threadHold = $("#threadHold").val();

        if (isEmpty(monitorName)) {
            toastr.error("请输入监控项名称");
            return false;
        }

        if (isEmpty(monitorItemType)) {
            toastr.error("请选择监控信息类型");
            return false;
        }

        // if (isEmpty(monitorType)) {
        //     toastr.error("请选择监控项类别");
        //     return false;
        // }

        if(monitorItemType === "NETWORK"){
            if (!ipTest(item)) {
                toastr.error("请输入正确的IP地址");
                return false;
            }
        }

        if (monitorItemType === "CERT70" || monitorItemType === "CERT40") {
            if (isEmpty(instanceName)) {
                toastr.error("请输入证书路径");
                return false;
            }else{
                if (!isLinuxPath(instanceName)) {
                    toastr.error("请填写正确的路径");
                    return false;
                }
            }

            if (isEmpty(nickname)) {
                toastr.error("请输入证书名称");
                return false;
            }
        } else if (monitorItemType === "ACCESSLOG" || monitorItemType === "PROXYLOG") {
            if (isEmpty(logPath)) {
                toastr.error("请输入日志路径");
                return false;
            }else {
                if (!isLinuxPath(logPath)) {
                    toastr.error("请填写正确的路径");
                    return false;
                }
            }

            if (isEmpty(threadHold)) {
                toastr.error("请输入日志阈值");
                return false;
            }
        }

        $.ajax({
            url: contextPath + 'monitor/monitorItem/add',
            data: {
                'monitorId': monitorId,
                'monitorName': monitorName,
                'monitorItemType': monitorItemType,
                // 'monitorType': "SYSTEM",
                'item': item,
                'certConfig.instanceName': instanceName,
                'certConfig.nickname': nickname,
                'logConfig.logPath': logPath,
                'logConfig.threadHold': threadHold
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("monitorItemTable", contextPath + "monitor/monitorItem/get", monitorId);
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#monitorItemModal").modal('hide');
            }
        });
    });
}

/**
 * 更新监控项
 */
function updateData(bean) {
    $("#monitorName").val(bean.monitorName);
    $("#monitorItemType").val(bean.monitorItemType).trigger("change");
    // $("#monitorType").val(bean.monitorType).trigger("change");

    if (!isEmpty(bean.item.instanceName)) {
        // $("#isCert").prop("checked", false);
        $("#instanceName").val(bean.item.instanceName);
        $("#nickname").val(bean.item.nickname);
    }
    if (!isEmpty(bean.item.logPath)) {
        $("#logPath").val(bean.item.logPath);
        $("#threadHold").val(bean.item.threadHold);
    } else {
        $("#item").val(bean.item);
    }

    let $confirmButton = $("<button class='btn btn-success'>保存</button>");
    addButton("monitorItemModal", "更新监控项", $confirmButton);

    $confirmButton.on('click', function () {
        let monitorName = $("#monitorName").val(),
            monitorItemType = $("#monitorItemType").val(),
            // monitorType = $("#monitorType").val(),
            item = $("#item").val(),
            instanceName = $("#instanceName").val(),
            nickname = $("#nickname").val(),
            logPath = $("#logPath").val(),
            threadHold = $("#threadHold").val();

        if (isEmpty(monitorName)) {
            toastr.error("请输入监控项名称");
            return false;
        }

        if (isEmpty(monitorItemType)) {
            toastr.error("请选择监控信息类型");
            return false;
        }

        // if (isEmpty(monitorType)) {
        //     toastr.error("请选择监控项类别");
        //     return false;
        // }

        if(monitorItemType === "NETWORK"){
            if (!ipTest(item)) {
                toastr.error("请输入正确的IP地址");
                return false;
            }
        }

        if (monitorItemType === "CERT70" || monitorItemType === "CERT40") {
            if (isEmpty(instanceName)) {
                toastr.error("请输入证书路径");
                return false;
            }else{
                if (!isLinuxPath(instanceName)) {
                    toastr.error("请填写正确的路径");
                    return false;
                }
            }

            if (isEmpty(nickname)) {
                toastr.error("请输入证书名称");
                return false;
            }
        } else if (monitorItemType === "ACCESSLOG" || monitorItemType === "PROXYLOG") {
            if (isEmpty(logPath)) {
                toastr.error("请输入日志路径");
                return false;
            }else {
                if (!isLinuxPath(logPath)) {
                    toastr.error("请填写正确的路径");
                    return false;
                }
            }

            if (isEmpty(threadHold)) {
                toastr.error("请输入日志阈值");
                return false;
            }
        }

        $.ajax({
            url: contextPath + 'monitor/monitorItem/update',
            data: {
                'monitorId': bean.monitorId,
                'monitorName': monitorName,
                'monitorItemType': monitorItemType,
                // 'monitorType': "SYSTEM",
                'item': item,
                'certConfig.instanceName': instanceName,
                'certConfig.nickname': nickname,
                'logConfig.logPath': logPath,
                'logConfig.threadHold': threadHold
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("monitorItemTable", contextPath + "monitor/monitorItem/get");
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#monitorItemModal").modal('hide');
            }
        });
    });
}

/**
 * 删除监控项
 */
function deleteData(bean) {
    let $confirmButton = $("<button class='btn btn-success'>确认</button>");

    let $message = $('<h2 style="text-align: center">确认删除监控项[' + bean.monitorName + ']？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + 'monitor/monitorItem/delete',
            data: {
                'monitorId': bean.monitorId,
                'monitorName': bean.monitorName
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("monitorItemTable", contextPath + "monitor/monitorItem/get");
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#message").modal('hide');
            }
        });
    });
}

/**
 * 删除所选监控项
 */
function deleteCheck() {
    let idArray = new Array();
    $("input:checkbox:checked").each(function () {
        idArray.push($(this).val());
    });
    if (idArray.length < 1) {
        toastr.error("未选择");
        return;
    }

    let $confirmButton = $("<button class='btn btn-success'>确认</button>");

    let $message = $('<h2 style="text-align: center">确认删除所选监控项？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + 'monitor/monitorItem/deleteCheck',
            data: {
                'jsonStr': JSON.stringify(idArray)
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("monitorItemTable", contextPath + "monitor/monitorItem/get");
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#message").modal('hide');
            }
        });
    });
}