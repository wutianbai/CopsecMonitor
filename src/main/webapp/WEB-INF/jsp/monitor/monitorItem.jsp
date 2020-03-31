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
    <script type="text/javascript" src="<%=basePath %>static/js/copsec/monitor/monitorItem.js"></script>
</head>

<body class="page-body">
<%@ include file="/WEB-INF/jsp/nav.jsp" %>
<div class="page-container">
    <%--<%@ include file="/WEB-INF/jsp/sidebar.jsp" %>--%>
    <div class="main-content">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">${system.monitorItem}</h3>

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
                <button class="btn btn-success" onclick="checkAll('monitorItemTable')"><i class="fa fa-circle"></i></button>
                <button class="btn btn-success" onclick="uncheckAll('monitorItemTable')"><i class="fa fa-circle-o"></i></button>
                <button class="btn btn-red" onclick="deleteCheck()"><i class="fa fa-trash-o"></i></button>
                <table id="monitorItemTable" class="table table-bordered table-striped"></table>
            </div>
        </div>
    </div>
</div>

<%--监控项--%>
<div class="modal fade" data-backdrop="static" id="monitorItemModal">
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
                            <label class="control-label">监控项名称</label>
                            <input type="text" class="form-control" id="monitorName">
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">监控信息类型</label>
                            <select class="form-control" id="monitorItemType" onchange="changeItemLabel()"></select>
                        </div>
                    </div>
                </div>
                <%--<div class="row">--%>
                    <%--<div class="col-md-12">--%>
                        <%--<div class="form-group">--%>
                            <%--<label class="control-label">监控项类别</label>--%>
                            <%--<select class="form-control" id="monitorType"></select>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <%--<div class="row">--%>
                    <%--<div class="col-md-6">--%>
                        <%--<div class="form-group">--%>
                            <%--<label class="control-label">是否为证书</label>--%>
                            <%--&lt;%&ndash;<input type="checkbox" class="iswitch iswitch-success" id="isCert" onclick="isCert()">&ndash;%&gt;--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <%--<div class="col-md-6">--%>
                        <%--<div class="form-group">--%>
                            <%--<label class="control-label">是否为日志</label>--%>
                            <%--<input type="checkbox" class="iswitch iswitch-success" id="isLog" onclick="isLog()">--%>
                        <%--</div>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <div class="row" id="itemRow">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label" id="itemLabel">监控数据</label>
                            <input type="text" class="form-control" id="item">
                        </div>
                    </div>
                </div>
                <div class="row" style="display: none" id="certRow">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">证书对应实例安装路径名称(WebProxy40实例配置格式为:实例名称-端口号)</label>
                            <input type="text" class="form-control" id="instanceName">
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">证书名称,多个值使用","分隔</label>
                            <input type="text" class="form-control" id="nickname">
                        </div>
                    </div>
                </div>
                <div class="row" style="display: none" id="logRow">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">日志路径</label>
                            <input type="text" class="form-control" id="logPath">
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">阈值</label>
                            <input type="text" class="form-control" id="threadHold">
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
