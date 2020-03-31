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
<head>
    <%@ include file="/WEB-INF/jsp/common.jsp" %>
    <script type="text/javascript" src="<%=basePath %>static/js/copsec/monitor/monitorTask.js"></script>
</head>

<body class="page-body">
<%@ include file="/WEB-INF/jsp/nav.jsp" %>
<div class="page-container">
    <%--<%@ include file="/WEB-INF/jsp/sidebar.jsp" %>--%>
    <div class="main-content">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">${system.monitorTask}</h3>

                <div class="panel-options">
                    <%--<a href="#">--%>
                        <%--<i class="linecons-cog"></i>--%>
                    <%--</a>--%>

                    <a href="#" data-toggle="panel">
                        <span class="collapse-icon">&ndash;</span>
                        <span class="expand-icon">+</span>
                    </a>

                    <a href="#" data-toggle="reload">
                        <i class="fa-rotate-right"></i>
                    </a>

                    <%--<a href="#" data-toggle="remove">--%>
                        <%--&times;--%>
                    <%--</a>--%>
                </div>
            </div>
            <div class="panel-body">
                <button id="add" class="btn btn-success" onclick="addData();"><i class="fa fa-plus"></i></button>
                <button class="btn btn-success" onclick="checkAll('monitorTaskTable')"><i class="fa fa-circle"></i></button>
                <button class="btn btn-success" onclick="uncheckAll('monitorTaskTable')"><i class="fa fa-circle-o"></i></button>
                <button class="btn btn-red" onclick="deleteCheck()"><i class="fa fa-trash-o"></i></button>
                <table id="monitorTaskTable" class="table table-bordered table-striped"></table>
            </div>
        </div>
    </div>
</div>

<%--监控任务--%>
<div class="modal fade" data-backdrop="static" id="monitorTaskModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title"></h4>
            </div>

            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">监控任务名称</label>
                            <input type="text" class="form-control" id="taskName">
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">设备</label>
                            <select class="form-control" id="taskDeviceId" multiple></select>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">监控组</label>
                            <select class="form-control" id="groupId"></select>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">告警项</label>
                            <select class="form-control" id="warningItems" multiple></select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <button class="btn btn-white" data-dismiss="modal">取消</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>
