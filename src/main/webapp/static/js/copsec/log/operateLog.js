let oTable;//定义变量名，用于存放dataTable对象，一般定义为全局的比较好
let iDisplayStart;
let iDisplayLength;

function searchByCondition() {
    page("operateLogTable", contextPath + "system/operateLog/search");
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
                "name": "operateUser",
                "value": $("#operateUser").val()
            });
            aoData.push({
                "name": "ip",
                "value": $("#ip").val()
            });
            aoData.push({
                "name": "operateType",
                "value": $("#operateType").val()
            });
            aoData.push({
                "name": "desc",
                "value": $("#desc").val()
            });
            aoData.push({
                "name": "result",
                "value": $("#result").val()
            });
            let time = $("#date").val().split("&");
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
        "aoColumns": [//渲染每一列，其实就是配置表头和数据对应显示到哪一列中
            {
                "mData": "id",
                "sClass": "text-center",
                "sTitle": '选择',
                "width": "5%",
                "mRender": function (d, type, full, meta) {
                    return '<input type="checkbox" class="cbr" value="' + d + '">';
                }
            },
            {
                "mData": "id",//读取数组的对象中的id属性
                "sTitle": "序号",//表头
                "sClass": "text-center",
                "width": "6%",//设置宽度,不设置的话就是自动分配
                "mRender": function (d, type, full, meta) {//如果需要显示的内容需根据数据封装加工的就写这个属性，0
                    //回调中有4个参数，d：对应mData中的属性的值；type：对应值的类型；full：对应当前这一行的数据，meta对应dataTable的配置
                    //如果不清楚可以使用console.log();打印出来看看
                    iDisplayStart = meta.settings._iDisplayStart;
                    iDisplayLength = meta.settings._iDisplayLength;
                    return meta.row + 1 + meta.settings._iDisplayStart;
                }
            },
            {
                "mData": "operateUser",
                "width": "7%",
                "sTitle": "操作用户"
            },
            {
                "mData": "ip",
                "width": "10%",
                "sTitle": "IP地址"
            },
            {
                "mData": "operateType",
                "sTitle": "操作类型"
            },
            {
                "mData": "desc",
                "sTitle": "描述"
            },
            {
                "mData": "result",
                "sTitle": "结果",
                "sClass": "text-center",
                "width": "5%",
                "mRender": function (d, type, full) {
                    return d === "1" ? "<span>成功</span>" : "<span style='color: red'>失败</span>";
                }
            },
            {
                "mData": "date",
                "width": "11%",
                "sTitle": "时间"
            }
        ]
    });
}

jQuery(function () {
    searchByCondition();
    $("#result").select2({
        placeholder: "请选择结果...",
        allowClear: true
    }).on('select2-open', function () {
        // Adding Custom Scrollbar
        $(this).data('select2').results.addClass('overflow-hidden').perfectScrollbar();
    });
    // Replace checkboxes when they appear
    let $state = $("#operateLogTable thead input[type='checkbox']");
    $("#operateLogTable").on('draw.dt', function () {
        cbr_replace();
        $state.trigger('change');
    });
});

/**
 * 删除所选日志
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

    let $message = $('<h2 style="text-align: center">确认删除所选日志？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + 'system/operateLog/deleteCheck',
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
            }
        });
        $("#message").modal('hide');
    });
}

function deleteAll() {
    let $confirmButton = $("<button class='btn btn-success'>确认</button>");

    let $message = $('<h2 style="text-align: center">确认清空操作日志？</h2>');
    $("#message").find(".modal-body").html($message);
    addButton("message", "系统提示", $confirmButton);

    $confirmButton.on('click', function () {
        $.ajax({
            url: contextPath + "system/operateLog/deleteAll",
            method: 'POST',
            dataType: 'json',
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    searchByCondition();
                } else {
                    toastr.error(data.message);
                }
            }, error: function (ajq, status, error) {
                toastr.error(status + error);
            }
        });
        $("#message").modal('hide');
    });
}

function exportLog() {
    let time = $("#date").val().split("&");
    $.fileDownload(contextPath + "system/operateLog/export", {
        data: {
            "fileName": "operateLog",
            "iDisplayStart": iDisplayStart,
            "iDisplayLength": iDisplayLength,
            "operateUser": $("#operateUser").val(),
            "ip": $("#ip").val(),
            "operateType": $("#operateType").val(),
            "desc": $("#desc").val(),
            "result": $("#result").val(),
            "start": time[0],
            "end": time[1],
        },
        httpMethod: 'POST',
        successCallback: function () {
            $.ajax({
                url: contextPath + 'system/operateLog/file',
                data: {
                    "fileName": "operateLog"
                },
                method: 'POST',
                dataType: "json",
                success: function (data) {
                    // if (data.code === 200) {
                    //     toastr.info(data.message);
                    // } else {
                    //     toastr.error(data.message + data.data);
                    // }
                }
            });
        }
    })
}