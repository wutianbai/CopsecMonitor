<%--
  Created by IntelliJ IDEA.
  User: Copsec
  Date: 2019/6/27
  Time: 14:30
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<html>
<title>${system.title}</title>
<script>
    var contextPath = "<%=basePath %>";
</script>
<meta name="viewport" content="user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, minimal-ui">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/fonts/linecons/css/linecons.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/fonts/fontawesome/css/font-awesome.min.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/bootstrap.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/xenon-core.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/xenon-forms.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/xenon-components.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/xenon-skins.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/custom.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/datatables/dataTables.bootstrap.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/multiselect/css/multi-select.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/select2/select2.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/select2/select2-bootstrap.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/daterangepicker/daterangepicker-bs3.css">
<script type="text/javascript" src="<%=basePath %>static/js/jquery-1.9.1.js"></script>
<script type="text/javascript">
    $(function () {

        var url = "<%=basePath%>system/task/search";

        var copsec = new copsec_search();

        copsec.create(url, 'POST', 'fileSendTb', 'fileSendList', 'pageInfo', 'pages', 'searchBtn');

        copsec.bindPageLink(getData, fillTable);

        copsec.searchBind(getData, fillTable);

        copsec.put(getData(1));

        copsec.getData(fillTable);

        var $state = $("#fileSendTb thead input[type='checkbox']");
        $state.on('change', function (ev) {
            var $chcks = $("#fileSendTb tbody input[type='checkbox']");

            if ($state.is(':checked')) {
                $chcks.prop('checked', true).trigger('change');
            } else {
                $chcks.prop('checked', false).trigger('change');
            }
        });
        $("#updateAll").on('click', function () {
            var ids = new Array();
            var $chcks = $("#fileSendTb tbody input[type='checkbox']:checked");
            $.each($chcks, function (i, e) {
                var name = $(this).parent().parent().attr("name");
                if (name) {
                    ids.push(name);
                }
            });
            if (ids.length > 0) {
                $.ajax({
                    url: contextPath + "system/task/update",
                    data: JSON.stringify(ids),
                    dataType: 'json',
                    method: 'POST',
                    contentType: "application/json;charset=utf-8",
                    success: function (data) {
                        if (data.code === 200) {
                            toastr.info(data.message);
                            $(".pagination").find("li.active").find("a").trigger('click');
                        } else {
                            toastr.error(data.message);
                        }
                    },
                    error: function (jqx, status, error) {
                        toastr.error("请求错误或超时");
                    }
                });
            }
        });

        $("#removeAll").on('click', function () {
            var ids = new Array();
            var $chcks = $("#fileSendTb tbody input[type='checkbox']:checked");
            $.each($chcks, function (i, e) {
                var name = $(this).parent().parent().attr("name");
                if (name) {
                    ids.push(name);
                }
            });
            if (ids.length > 0) {
                $.ajax({
                    url: contextPath + "system/task/delete",
                    data: JSON.stringify(ids),
                    dataType: 'json',
                    method: 'POST',
                    contentType: "application/json;charset=utf-8",
                    success: function (data) {
                        if (data.code === 200) {
                            toastr.info(data.message);
                            $(".pagination").find("li.active").find("a").trigger('click');
                        } else {
                            toastr.error(data.message);
                        }
                    },
                    error: function (jqx, status, error) {
                        toastr.error("请求错误或超时");
                    }
                });
            }
        });
    });

    function getData(currenPage) {
        var size = 10, $pageSize = $("#pageInfo");
        if ($pageSize.find('select').length === 2) {
            size = $pageSize.find("select:nth-child(2)").val();
        }
        return {'size': parseInt(size), 'page': parseInt(currenPage) - 1};
    }

    function fillTable(data) {
        $("#fileSendList").children().remove();
        $.each(data.content, function (index, item) {
            var $tr;
            if (!item.status) {
                $tr = $("<tr name=" + item.taskId + " style='color:#ff1408'>" +
                    "<td><input type='checkbox' class='cbr'></td>" +
                    "<td>" + (index + 1) + "</td>" +
                    "<td>" + item.taskName + "</td>" +
                    "<td>" + item.message + "</td>" +
                    "<td>" + item.time + "</td>" +
                    "<td>" + (item.operateUser == null ? "N/A" : item.operateUser) + "</td>" +
                    "<td>" + (item.operateTimeStr == null ? "N/A" : item.operateTimeStr) + "</td>" +
                    "<td>未处理</td>" +
                    "<td><button class='btn btn-turquoise'>处理</button></td></tr>");
            } else {
                $tr = $("<tr name=" + item.taskId + ">" +
                    "<td><input type='checkbox' class='cbr'></td>" +
                    "<td>" + (index + 1) + "</td>" +
                    "<td>" + item.taskName + "</td>" +
                    "<td>" + item.message + "</td>" +
                    "<td>" + item.time + "</td>" +
                    "<td>" + (item.operateUser == null ? "N/A" : item.operateUser) + "</td>" +
                    "<td>" + (item.operateTimeStr == null ? "N/A" : item.operateTimeStr) + "</td>" +
                    "<td>已处理</td>" +
                    "<td></td></tr>");
            }
            $("#fileSendList").append($tr);

            $tr.on("click", "button", function () {
                var ids = new Array();
                ids.push($tr.attr("name"));
                $.ajax({
                    url: contextPath + "system/task/update",
                    data: JSON.stringify(ids),
                    dataType: 'json',
                    method: 'POST',
                    contentType: "application/json;charset=utf-8",
                    success: function (data) {
                        if (data.code === 200) {
                            toastr.info(data.message);
                            $(".pagination").find("li.active").find("a").trigger('click');
                        } else {
                            toastr.error(data.message);
                        }
                    },
                    error: function (jqx, status, error) {
                        toastr.error("请求错误或超时");
                    }
                });
            });
        });
    }
</script>
</head>

<body class="page-body">
<div class="navbar horizontal-menu navbar-fixed-top">

</div>

<div class="page-container">

    <div class="main-content">

        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">文件同步告警</h3>
                <div class="panel-options">
                    <a href="#" data-toggle="panel">
                        <span class="collapse-icon">&ndash;</span>
                        <span class="expand-icon">+</span>
                    </a>
                </div>
            </div>

            <!-- table -->
            <div class="panel-body">
                <div class="row">
                    <button id="updateAll" class="btn btn-turquoise">批量处理</button>
                    <button id="removeAll" class="btn btn-turquoise">删除记录</button>
                    <table id="fileSendTb" class="table table-bordered table-model-2 table-striped" role="grid">
                        <thead>
                        <th><input type="checkbox" class="cbr"></th>
                        <th>序号</th>
                        <th>同步任务</th>
                        <th>描述</th>
                        <th>上报时间</th>
                        <th>处理用户</th>
                        <th>处理时间</th>
                        <th>状态</th>
                        <th>操作</th>
                        </thead>
                        <tbody id="fileSendList">

                        </tbody>
                    </table>
                    <div class="row">
                        <div class="col-xs-6">
                            <div class="dataTables_info" id="pageInfo" role="status"></div>
                        </div>
                        <div class="col-xs-6">
                            <div class="dataTables_paginate paging_simple_numbers">
                                <ul id="pages" class="pagination">
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="<%=basePath %>static/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/TweenMax.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/resizeable.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/joinable.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-api.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-toggles.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/jquery-ui/jquery-ui.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/datatables/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/datatables/dataTables.bootstrap.js"></script>
<script type="text/javascript"
        src="<%=basePath %>static/assets/js/datatables/yadcf/jquery.dataTables.yadcf.js"></script>
<script type="text/javascript"
        src="<%=basePath %>static/assets/js/datatables/tabletools/dataTables.tableTools.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/jquery-validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/inputmask/jquery.inputmask.bundle.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/formwizard/jquery.bootstrap.wizard.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/datepicker/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/multiselect/js/jquery.multi-select.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/selectboxit/jquery.selectBoxIt.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/toastr/toastr.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/dropzone/dropzone.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/select2/select2.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/multiselect/js/jquery.multi-select.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/selectboxit/jquery.selectBoxIt.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/tagsinput/bootstrap-tagsinput.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/typeahead.bundle.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/handlebars.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-custom.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/copsec/common.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/moment.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/daterangepicker/daterangepicker.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/timepicker/bootstrap-timepicker.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/colorpicker/bootstrap-colorpicker.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/fileDownload1.4.6/jquery.fileDownload.js"></script>
</body>
</html>
