let oTable,//定义变量名，用于存放dataTable对象，一般定义为全局的比较好
    allItems,
    $select = $("#monitorIds");

function page(id, url, newId) {
    $.ajax({//使用ajax的方式获取
        url: url,//异步请求的接口地址
        method: "POST",//请求方式
        dataType: "json",//期待的数据返回类型
        async: true,//是否异步
        data: {},//请求参数，如果有可添加，键值对的形式或者JSON字符串都可以，根据后台来
        success: function (data) {//服务器响应成功后执行的回调
            allItems = data.data[3];

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
                        "sFirst": "首页",
                        "sPrevious": "前一页",
                        "sNext": "后一页",
                        "sLast": "尾页"
                    },
                    "oAria": {
                        "sSortAscending": ": 以升序排列此列",
                        "sSortDescending": ": 以降序排列此列"
                    }
                },
                "data": data.data[0],//若使用客户端分页，则将表格的数据填写到data属性中，需要数组,数组里面要求是对象
                "aoColumns": [//渲染每一列，其实就是配置表头和数据对应显示到哪一列中
                    {
                        "mData": "warningId",
                        "sClass": "text-center",
                        "sTitle": '选择',
                        "width": "7%",
                        "mRender": function (d, type, full, meta) {
                            return '<input type="checkbox" class="cbr" value="' + d + '">';
                        }
                    },
                    {
                        "mData": "warningId",
                        "sTitle": "序号",
                        "sClass": "text-center",
                        "width": "5%",
                        "mRender": function (d, type, full, meta) {
                            return meta.row + 1 + meta.settings._iDisplayStart;
                        }
                    },
                    {
                        "mData": "warningName",
                        "sTitle": "名称",
                        "width": "10%",
                        "mRender": function (d, type, full, meta) {
                            let str = d;
                            if (full.warningId === newId) {
                                str = d + '<span class="label label-success">New</span>';
                            }
                            return str;
                        }
                    },
                    {
                        "mData": "monitorItemType",
                        "sTitle": "监控项信息类型",
                        "width": "20%",
                        "mRender": function (d, type, full, meta) {
                            return data.data[1][d];
                        }
                    },
                    {
                        "mData": "warningLevel",
                        "sTitle": "级别",
                        "width": "8%",
                        "mRender": function (d, type, full, meta) {
                            return data.data[2][d];
                        }
                    },
                    {
                        "mData": "threadHold",
                        "sTitle": "阈值",
                        "width": "7%"
                    },
                    {
                        "mData": "monitorIds",
                        "sTitle": "监控项",
                        "mRender": function (d, type, full, meta) {
                            let newArr = new Array();
                            let arr1 = new Array();
                            let arr2 = d.split(',');
                            $.each(allItems, function (index, value) {
                                arr1.push(value.monitorId);
                            });
                            if (!isEmpty(arr2)) {
                                $.each(arr1, function (index, value) {
                                    if ($.inArray(arr1[index], arr2) > -1) {
                                        newArr.push(allItems[index].monitorName);
                                    }
                                });
                            }

                            return newArr.toString();
                        }
                    },
                    {
                        "mData": "warningId",
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

            $("#wMonitorItemType").empty();
            $.each(data.data[1], function (index, value) {
                $("#wMonitorItemType").append("<option value=" + index + ">" + value + "</option>");
            });
            reloadSelect("wMonitorItemType","选择监控信息类型...");

            $("#warningLevel").empty();
            $.each(data.data[2], function (index, value) {
                $("#warningLevel").append("<option value=" + index + ">" + value + "</option>");
            });
            reloadSelect("warningLevel","选择告警级别...");

            $("#monitorIds").empty();
            reloadSelect("monitorIds","选择监控项...");
            $.each(allItems, function (index, value) {
                $select.append("<option value=" + value.monitorId + ">" + value.monitorName + "</option>");
            });
        }
    });
}

jQuery(function () {
    page("warningItemTable", contextPath + "monitor/warningItem/get");

    // Replace checkboxes when they appear
    let $state = $("#warningItemTable thead input[type='checkbox']");
    $("#warningItemTable").on('draw.dt', function () {
        cbr_replace();
        $state.trigger('change');
    });
});

/**
 * 添加告警项
 */
function addData() {
    //清空数据
    let $modal = $("#warningItemModal").find(".modal-body");
    $modal.find('input').removeAttr('disabled').val('');
    $("#threadHold").val("0");
    $select.val("").trigger('change');

    let $confirmButton = $("<button class='btn btn-success'>保存</button>");
    addButton("warningItemModal", "添加告警项", $confirmButton);

    $confirmButton.on("click", function () {
        let warningId = Math.uuidFast(),
            warningName = $("#warningName").val(),
            monitorItemType = $("#wMonitorItemType").val(),
            warningLevel = $("#warningLevel").val(),
            threadHold = $("#threadHold").val(),
            monitorIds = $select.val();

        if (isEmpty(warningName)) {
            toastr.error("请输入告警项名称");
            return false;
        }

        if (isEmpty(monitorItemType)) {
            toastr.error("请选择监控信息类型");
            return false;
        }

        if (isEmpty(warningLevel)) {
            toastr.error("请选择监控级别");
            return false;
        }

        if (isEmpty(threadHold)) {
            toastr.error("请输入告警阀值");
            return false;
        }else if (!isEmpty(threadHold) && !isNumber(threadHold)){
            toastr.error("告警阀值请输入数字");
            return false;
        }

        $.ajax({
            url: contextPath + 'monitor/warningItem/add',
            data: {
                'warningId': warningId,
                'warningName': warningName,
                'monitorItemType': monitorItemType,
                'warningLevel': warningLevel,
                'threadHold': threadHold,
                'monitorIds': monitorIds.toString()
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("warningItemTable", contextPath + "monitor/warningItem/get", warningId);
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#warningItemModal").modal('hide');
            }
        });
    });
}

/**
 * 更新告警项
 */
function updateData(bean) {
    $("#warningName").val(bean.warningName);
    $("#wMonitorItemType").val(bean.monitorItemType).trigger("change");
    $("#warningLevel").val(bean.warningLevel).trigger("change");
    $("#threadHold").val(bean.threadHold);

    $select.val(bean.monitorIds.split(',')).trigger('change');

    let $confirmButton = $("<button class='btn btn-success'>保存</button>");
    addButton("warningItemModal", "更新告警项", $confirmButton);

    $confirmButton.on('click', function () {
        let warningName = $("#warningName").val(),
            monitorItemType = $("#wMonitorItemType").val(),
            warningLevel = $("#warningLevel").val(),
            threadHold = $("#threadHold").val(),
            monitorIds = $select.val();

        if (isEmpty(warningName)) {
            toastr.error("请输入告警项名称");
            return false;
        }

        if (isEmpty(monitorItemType)) {
            toastr.error("请选择监控信息类型");
            return false;
        }

        if (isEmpty(warningLevel)) {
            toastr.error("请选择监控级别");
            return false;
        }

        if (isEmpty(threadHold)) {
            toastr.error("请输入告警阀值");
            return false;
        }else if (!isEmpty(threadHold) && !isNumber(threadHold)){
            toastr.error("告警阀值请输入数字");
            return false;
        }

        $.ajax({
            url: contextPath + 'monitor/warningItem/update',
            data: {
                'warningId': bean.warningId,
                'warningName': warningName,
                'monitorItemType': monitorItemType,
                'warningLevel': warningLevel,
                'threadHold': threadHold,
                'monitorIds': monitorIds.toString()
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("warningItemTable", contextPath + "monitor/warningItem/get");
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#warningItemModal").modal('hide');
            }
        });
    });
}

/**
 * 删除告警项
 */
function deleteData(bean) {
    let $confirmButton = $("<button class='btn btn-success'>确认</button>");

    let $message = $('<h2 style="text-align: center">确认删除告警项[' + bean.warningName + ']？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + 'monitor/warningItem/delete',
            data: {
                'warningId': bean.warningId,
                'warningName': bean.warningName
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("warningItemTable", contextPath + "monitor/warningItem/get");
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#message").modal('hide');
            }
        });
    });
}

/**
 * 删除所选告警项
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

    let $message = $('<h2 style="text-align: center">确认删除所选告警项？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + 'monitor/warningItem/deleteCheck',
            data: {
                'jsonStr': JSON.stringify(idArray)
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("warningItemTable", contextPath + "monitor/warningItem/get");
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#message").modal('hide');
            }
        });
    });
}