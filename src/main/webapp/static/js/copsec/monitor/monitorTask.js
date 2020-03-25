let oTable,//定义变量名，用于存放dataTable对象，一般定义为全局的比较好
    allDevice,
    allGroup,
    allItems,
    $select = $("#warningItems");

function page(id, url, newId) {
    $.ajax({//使用ajax的方式获取
        url: url,//异步请求的接口地址
        method: "POST",//请求方式
        dataType: "json",//期待的数据返回类型
        async: true,//是否异步
        data: {},//请求参数，如果有可添加，键值对的形式或者JSON字符串都可以，根据后台来
        success: function (data) {//服务器响应成功后执行的回调
            allDevice = data.data[1];
            allGroup = data.data[2];
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
                    "sInfo": "显示第 _START_ 至 _END_ 项，共_TOTAL_ 项",
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
                        "mData": "taskId",
                        "sClass": "text-center",
                        "sTitle": '选择',
                        "width": "7%",
                        "mRender": function (d, type, full, meta) {
                            return '<input type="checkbox" class="cbr" value="' + d + '">';
                        }
                    },
                    {
                        "mData": "taskId",//读取数组的对象中的id属性
                        "sTitle": "序号",//表头
                        "sClass": "text-center",
                        "width": "5%",
                        "mRender": function (d, type, full, meta) {//如果需要显示的内容需根据数据封装加工的就写这个属性，0
                            //回调中有4个参数，d：对应mData中的属性的值；type：对应值的类型；full：对应当前这一行的数据，meta对应dataTable的配置
                            //如果不清楚可以使用console.log();打印出来看看
                            return meta.row + 1 + meta.settings._iDisplayStart;
                        }
                    },
                    {
                        "mData": "taskName",
                        "sTitle": "名称",
                        "width": "10%",
                        "mRender": function (d, type, full, meta) {
                            let str = d;
                            if (full.taskId === newId) {
                                str = d + '<span class="label label-success">New</span>';
                            }
                            return str;
                        }
                    },
                    {
                        "mData": "deviceId",
                        "sTitle": "设备",
                        "mRender": function (d, type, full, meta) {
                            // let name;
                            // $.each(allDevice, function (index, value) {
                            //     if (d === value.data.deviceId) {
                            //         name = value.data.deviceHostname;
                            //     }
                            // });

                            let newArr = new Array();
                            let arr1 = new Array();
                            let arr2 = d.split(',');
                            $.each(allDevice, function (index, value) {
                                arr1.push(value.data.deviceId);
                            });
                            if (!isEmpty(arr2)) {
                                $.each(arr1, function (index, value) {
                                    if ($.inArray(arr1[index], arr2) > -1) {
                                        newArr.push(allDevice[index].data.deviceHostname);
                                    }
                                });
                            }
                            return newArr.toString();
                        }
                    },
                    {
                        "mData": "groupId",
                        "sTitle": "监控组",
                        "mRender": function (d, type, full, meta) {
                            let name;
                            $.each(allGroup, function (index, value) {
                                if (d === value.id) {
                                    name = value.name;
                                }
                            });
                            return name;
                        }
                    },
                    {
                        "mData": "warningItems",
                        "sTitle": "告警项",
                        "mRender": function (d, type, full, meta) {
                            let names = new Array();
                            for (let i in d) {
                                names.push(d[i].warningName);
                            }
                            return names.toString();
                        }
                    },
                    {
                        "mData": "taskId",
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

            $("#taskDeviceId").empty();
            reloadSelect("taskDeviceId", "选择设备...");
            $.each(allDevice, function (index, value) {
                $("#taskDeviceId").append("<option value=" + value.data.deviceId + ">" + value.data.deviceHostname + "</option>");
            });

            $("#groupId").empty();
            $.each(allGroup, function (index, value) {
                $("#groupId").append("<option value=" + value.id + ">" + value.name + "</option>");
            });
            reloadSelect("groupId", "选择监控组...");

            $("#warningItems").empty();
            reloadSelect("warningItems", "选择告警项...");
            $.each(allItems, function (index, value) {
                $("#warningItems").append("<option value=" + value.warningId + ">" + value.warningName + "</option>");
            });
        }
    });
}

jQuery(function () {
    page("monitorTaskTable", contextPath + "monitor/monitorTask/get");

    // Replace checkboxes when they appear
    let $state = $("#monitorTaskTable thead input[type='checkbox']");
    $("#monitorTaskTable").on('draw.dt', function () {
        cbr_replace();
        $state.trigger('change');
    });
});

/**
 * 添加监控任务
 */
function addData() {
    //清空数据
    let $modal = $("#monitorTaskModal").find(".modal-body");
    $modal.find('input').removeAttr('disabled').val('');
    $select.val("").trigger('change');
    $("#taskDeviceId").val("").trigger('change');

    let $confirmButton = $("<button class='btn btn-success'>保存</button>");
    addButton("monitorTaskModal", "添加监控任务", $confirmButton);

    $confirmButton.on("click", function () {
        let taskId = Math.uuidFast(),
            taskName = $("#taskName").val(),
            deviceId = $("#taskDeviceId").val(),
            groupId = $("#groupId").val(),
            warningItems = $select.val();

        if (isEmpty(taskName)) {
            toastr.error("请输入监控任务名称");
            return false;
        }

        if (isEmpty(deviceId)) {
            toastr.error("请选择设备");
            return false;
        }

        if (isEmpty(groupId)) {
            toastr.error("请选择监控组");
            return false;
        }

        if (isEmpty(warningItems)) {
            toastr.error("请选择告警项");
            return false;
        }

        $.ajax({
            url: contextPath + 'monitor/monitorTask/add',
            data: {
                'taskId': taskId,
                'taskName': taskName,
                'deviceId': deviceId.toString(),
                'groupId': groupId,
                'jsonStr': JSON.stringify(warningItems)
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("monitorTaskTable", contextPath + "monitor/monitorTask/get", taskId);
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#monitorTaskModal").modal('hide');
            }
        });
    });
}

/**
 * 更新监控任务
 */
function updateData(bean) {
    $("#taskName").val(bean.taskName);

    let deviceIdArr = new Array();
    $.each(bean.deviceId.split(","), function (index, value) {
        deviceIdArr.push(value);
    });
    $("#taskDeviceId").val(deviceIdArr).trigger('change');
    // $("#taskDeviceId").val(bean.deviceId).trigger("change");

    $("#groupId").val(bean.groupId).trigger("change");

    let arr = new Array();
    $.each(bean.warningItems, function (index, value) {
        arr.push(value.warningId);
    });
    $select.val(arr).trigger('change');

    let $confirmButton = $("<button class='btn btn-success'>保存</button>");
    addButton("monitorTaskModal", "更新监控任务", $confirmButton);

    $confirmButton.on('click', function () {
        let taskName = $("#taskName").val(),
            deviceId = $("#taskDeviceId").val(),
            groupId = $("#groupId").val(),
            warningItems = $select.val();

        if (isEmpty(taskName)) {
            toastr.error("请输入监控任务名称");
            return false;
        }

        if (isEmpty(deviceId)) {
            toastr.error("请选择设备");
            return false;
        }

        if (isEmpty(groupId)) {
            toastr.error("请选择监控组");
            return false;
        }

        if (isEmpty(warningItems)) {
            toastr.error("请选择告警项");
            return false;
        }

        $.ajax({
            url: contextPath + 'monitor/monitorTask/update',
            data: {
                'taskId': bean.taskId,
                'taskName': taskName,
                'deviceId': deviceId.toString(),
                'groupId': groupId,
                'jsonStr': JSON.stringify(warningItems)
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("monitorTaskTable", contextPath + "monitor/monitorTask/get");
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#monitorTaskModal").modal('hide');
            }
        });
    });
}

/**
 * 删除监控任务
 */
function deleteData(bean) {
    let $confirmButton = $("<button class='btn btn-success'>确认</button>");

    let $message = $('<h2 style="text-align: center">确认删除监控任务[' + bean.taskName + ']？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + 'monitor/monitorTask/delete',
            data: {
                'taskId': bean.taskId,
                'taskName': bean.taskName
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("monitorTaskTable", contextPath + "monitor/monitorTask/get");
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#message").modal('hide');
            }
        });
    });
}

/**
 * 删除所选监控任务
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

    let $message = $('<h2 style="text-align: center">确认删除所选监控任务？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + 'monitor/monitorTask/deleteCheck',
            data: {
                'jsonStr': JSON.stringify(idArray)
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    page("monitorTaskTable", contextPath + "monitor/monitorTask/get");
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#message").modal('hide');
            }
        });
    });
}