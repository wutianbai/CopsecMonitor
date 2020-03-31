let oTable;//定义变量名，用于存放dataTable对象，一般定义为全局的比较好
function searchByCondition() {
    page("warningEventTable", contextPath + "warning/event/search");
}
function page(id, url) {
    //初始化datatable
    if (typeof oTable != "undefined") {
        //如果已经被实例化，则销毁再实例化
        oTable.fnDestroy();
    }

    oTable = $("#" + id).dataTable({//注意#infoTable是需创建为dataTable的表格,使用jQuery选择器
        "processing": true,//数据加载时显示进度条
        "searching": false,//启用搜索功能
        "serverSide": true,//是否开启服务端分页(不开就是客户端分页)
        "sServerMethod": "POST",//若使用服务端分页，则设置请求方式为“POST”，可改
        "sAjaxSource": url,
        "fnServerParams": function (aoData) {
            aoData.push({
                "name": "eventSource",
                "value": $("#eventSource").val()
            });
            aoData.push({
                "name": "eventType",
                "value": $("#eventType").val()
            });
            aoData.push({
                "name": "deviceName",
                "value": $("#deviceName").val()
            });
            aoData.push({
                "name": "userName",
                "value": $("#userName").val()
            });
            aoData.push({
                "name": "eventDetail",
                "value": $("#eventDetail").val()
            });
            let time = $("#eventTime").val().split("&");
            aoData.push({
                "name": "start",
                "value": time[0]
            });
            aoData.push({
                "name": "end",
                "value": time[1]
            });
        },
        "info": true, //分页信息提示等等
        "paging": true,//是否分页
        "pagingType": "full_numbers",//分页时会有首页，尾页，上一页和下一页四个按钮,加上数字按钮
        "bLengthChange": true, //开关，是否显示每页显示多少条数据的下拉框
        "aLengthMenu": [20, 50, 100, 200],
        'iDisplayLength': 20, //每页初始显示5条记录
        'bFilter': false,  //是否使用内置的过滤功能
        "bInfo": true, //开关，是否显示表格的一些信息(当前显示XX-XX条数据，共XX条)
        "bPaginate": true, //开关，是否显示分页器
        "bSort": false, //是否可排序
        "oLanguage": {  //语言转换
            "sProcessing": "正在加载数据...",
            "sInfo": "显示第 _START_ 至 _END_ 项，共 _TOTAL_ 项",
            "sLengthMenu": "每页显示 _MENU_ 项",
            "sInfoEmpty": "没有数据",
            "sInfoFiltered": "",
            "sLoadingRecords": "检索中...",
            "sZeroRecords": "没有检索到数据",
            "sSearch": "搜索",
            "oPaginate": {
                "sFirst": "《",
                "sPrevious": "<",
                "sNext": ">",
                "sLast": "》"
            }
        },
        // "aoColumnDefs":[
        //     {"sClass":"col_class","aTargets":[2]},
        //     {"sClass":"col_class","aTargets":[3]},
        //     {"sClass":"col_class","aTargets":[4]},
        //     {"sClass":"col_class","aTargets":[5]},
        //     {"sClass":"col_class","aTargets":[6]},
        //     {"sClass":"col_class","aTargets":[7]},
        //     {"sClass":"col_class","aTargets":[8]}
        // ],
        "aoColumns": [//渲染每一列，其实就是配置表头和数据对应显示到哪一列中
            {
                "mData": "eventId",
                "sClass": "text-center",
                "sTitle": '选择',
                "width": "7%",
                "mRender": function (d, type, full, meta) {
                    return '<input type="checkbox" class="cbr" value="' + d + '">';
                }
            },
            {
                "mData": "eventId",//读取数组的对象中的id属性
                "sTitle": "序号",//表头
                "sClass": "text-center col_class",
                "width": "6%",//设置宽度,不设置的话就是自动分配
                "mRender": function (d, type, full, meta) {//如果需要显示的内容需根据数据封装加工的就写这个属性，0
                    //回调中有4个参数，d：对应mData中的属性的值；type：对应值的类型；full：对应当前这一行的数据，meta对应dataTable的配置
                    //如果不清楚可以使用console.log();打印出来看看
                    return meta.row + 1 + meta.settings._iDisplayStart;
                }
            },
            {
                "mData": "eventSource",
                "sTitle": "来源"
            },
            {
                "mData": "eventTime",
                "sTitle": "发生时间",
                "width": "8%"
            },
            {
                "mData": "eventDetail",
                "sTitle": "详情"
            },
            {
                "mData": "eventType",
                "sTitle": "类型",
                "width": "8%"
            },
            // {
            //     "mData": "deviceId",
            //     "sTitle": "设备ID"
            // },
            {
                "mData": "deviceName",
                "sTitle": "设备名",
                "width": "7%"
            },
            // {
            //     "mData": "userId",
            //     "sTitle": "运维账户ID"
            // },
            {
                "mData": "userName",
                "sTitle": "运维账户",
                "width": "8%"
            },
            {
                "mData": "userMobile",
                "sTitle": "联系方式",
                "width": "8%"
            },
            {
                "mData": "eventId",
                "sTitle": "操作",
                "sClass": "text-center",
                "width": "10%",
                "mRender": function (d, type, full) {
                    let str = '<a class="btn btn-secondary btn-sm btn-icon icon-left" onclick=\'updateData(' + JSON.stringify(full) + ')\'><i class="fa-minus-square-o"></i></a>';
                    str += '<a class="btn btn-danger btn-sm btn-icon icon-left" onclick=\'deleteData(' + JSON.stringify(full) + ')\'><i class="fa-trash"></i></a>';
                    return str;
                }
            }
        ]
    });
}

jQuery(function () {
    searchByCondition();

    // Replace checkboxes when they appear
    let $state = $("#warningEventTable thead input[type='checkbox']");
    $("#warningEventTable").on('draw.dt', function () {
        cbr_replace();
        $state.trigger('change');
    });
});

/**
 * 处理事件
 */
function updateData(bean) {
    let $confirmButton = $("<button class='btn btn-success'>确认</button>");

    let $message = $('<h2 style="text-align: center">确认处理[' + bean.deviceName + ']此条事件？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + 'warning/event/handle',
            data: {
                'eventId': bean.eventId
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    searchByCondition();
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#message").modal('hide');
            }
        });
    });
}

/**
 * 删除事件
 */
function deleteData(bean) {
    let $confirmButton = $("<button class='btn btn-success'>确认</button>");

    let $message = $('<h2 style="text-align: center">确认删除[' + bean.deviceName + ']此条事件？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + 'warning/event/delete',
            data: {
                'eventId': bean.eventId
            },
            method: 'POST',
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    searchByCondition();
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#message").modal('hide');
            }
        });
    });
}

/**
 * 处理所选事件
 */
function handleCheck() {
    let idArray = new Array();
    $("input:checkbox:checked").each(function () {
        idArray.push($(this).val());
    });
    if (idArray.length < 1) {
        toastr.error("未选择");
        return;
    }

    let $confirmButton = $("<button class='btn btn-success'>确认</button>");

    let $message = $('<h2 style="text-align: center">确认处理所选事件？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + 'warning/event/handleCheck',
            data: JSON.stringify(idArray),
            method: 'POST',
            dataType: "json",
            contentType: "application/json;charset=utf-8",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    searchByCondition();
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#message").modal('hide');
            }
        });
    });
}

/**
 * 删除所选事件
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

    let $message = $('<h2 style="text-align: center">确认删除所选事件？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + 'warning/event/deleteCheck',
            data: JSON.stringify(idArray),
            method: 'POST',
            dataType: "json",
            contentType: "application/json;charset=utf-8",
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    searchByCondition();
                } else {
                    toastr.error(data.message + data.data);
                }

                $("#message").modal('hide');
            }
        });
    });
}