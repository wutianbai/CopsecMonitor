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
    <script type="text/javascript" src="<%=basePath %>static/js/copsec/monitor/warningItem.js"></script>
</head>

<body class="page-body">
<%@ include file="/WEB-INF/jsp/nav.jsp" %>
<div class="page-container">
    <%@ include file="/WEB-INF/jsp/sidebar.jsp" %>
    <div class="main-content">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">${system.warningItem}</h3>

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
                <button class="btn btn-success" onclick="checkAll('warningItemTable')"><i class="fa fa-circle"></i></button>
                <button class="btn btn-success" onclick="uncheckAll('warningItemTable')"><i class="fa fa-circle-o"></i></button>
                <button class="btn btn-red" onclick="deleteCheck()"><i class="fa fa-trash-o"></i></button>
                <table id="warningItemTable" class="table table-bordered table-striped"></table>
            </div>
        </div>
    </div>
</div>

<%--告警项--%>
<div class="modal fade" data-backdrop="static" id="warningItemModal">
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
                            <label class="control-label">告警项名称</label>
                            <input type="text" class="form-control" id="warningName">
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">监控信息类型</label>
                            <select class="form-control" id="wMonitorItemType"></select>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">告警级别</label>
                            <select class="form-control" id="warningLevel"></select>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <%--<input class="form-control" id="threadHold">--%>
                            <label class="control-label">告警阀值</label>
                            <div class="input-group spinner col-sm-12" data-step="1" data-min="0">
                                <span class="input-group-btn"><button class="btn btn-info btn-single"
                                                                      data-type="decrement">-</button></span>
                                <input id="threadHold" type="text" class="form-control text-center no-left-border"
                                       value="0" readonly/>
                                <span class="input-group-btn"><button class="btn btn-info btn-single"
                                                                      data-type="increment">+</button></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">监控项</label>
                            <select class="form-control" id="monitorIds" multiple></select>
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
