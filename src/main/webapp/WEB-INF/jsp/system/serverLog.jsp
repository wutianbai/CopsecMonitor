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

        var url = "<%=basePath%>system/server/logs";

        var copsec = new copsec_search();

        copsec.create(url, 'POST', 'fileSendTb', 'fileSendList', 'pageInfo', 'pages', 'searchBtn');

        copsec.bindPageLink(getData, fillTable);

        copsec.searchBind(getData, fillTable);

        copsec.put(getData(1));

        copsec.getData(fillTable);

        $("#deleteAll").click(function () {
            if (confirm("确定删除所有数据，操作不可回退？")) {
                $.ajax({
                    url: "<%=basePath%>system/audit/delete",
                    method: 'POST',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code === 200) {
                            toastr.info(data.message);
                        } else {
                            toastr.error(data.message);
                        }
                    }, error: function (ajq, status, error) {
                        toastr.error(status + error);
                    }
                });
            }
        });

        $("#export").click(function () {
            var page = $("#pages li[class~='active']").children().first().text();
            var data = getData(page);
            $.fileDownload("<%=basePath%>system/audit/export", {

                data: data,
                httpMethod: 'POST',
                successCallback: function (url) {
                    $.ajax('<%=basePath%>system/audito/file/' + data.fileName);
                }
            })
        });
    });

    function getData(currenPage) {
        var _time = $("#time").val(),
            ip = $("#ip").val(), desc = $("#desc").val(), size = 10, $pageSize = $("#pageInfo");
        if ($pageSize.find('select').length == 2) {
            size = $pageSize.find("select:nth-child(2)").val();
        }
        if (_time != "") {
            _time = _time.split("&");
        }
        return {
            'size': parseInt(size),
            'ip': ip, 'start': _time[0], "end": _time[1], "desc": desc,
            'page': parseInt(currenPage) - 1
        };
    }

    function fillTable(data) {
        $("#fileSendList").children().remove();
        $.each(data.content, function (index, item) {
            var $tr = $("<tr>" +
                "<td>" + (index + 1) + "</td>" +
                "<td>" + item.ip + "</td>" +
                "<td>" + item.time + "</td>" +
                "<td>" + item.desc + "</td></tr>");
            $("#fileSendList").append($tr);
        });
    }
</script>
</head>

<body class="page-body">
<div class="navbar horizontal-menu navbar-fixed-top">

</div>

<div class="page-container">

    <div class="main-content">

        <div class="panel default-panel">
            <!-- 搜索 -->
            <div class="form-inline">
                <div class="form-group">
                    时间：<input id="time" type="text" class="form-control daterange active" data-time-picker="true"
                              data-format="YYYY-MM-DD hh:mm:ss">
                </div>
                <div class="form-group">
                    服务器：<input id="ip" class="form-control">
                </div>
                <div class="form-group">
                    上报信息：<input id="desc" class="form-control">
                </div>
                <div class="form-group">
                    <button id="searchBtn" type="button" class="btn btn-turquoise btn-single">搜索</button>
                </div>
            </div>

            <div class="panel-body">
                <div class="row">
                    <table id="fileSendTb" class="table table-model-2 table-bordered table-striped" role="grid">
                        <thead>
                        <th width="5%">编号</th>
                        <th width="8%">服务器</th>
                        <th width="12%">上报时间</th>
                        <th>上报信息</th>
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
