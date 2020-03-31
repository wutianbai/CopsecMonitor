<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
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
    <script type="text/javascript" src="<%=basePath %>static/js/copsec/warning/warningHistory.js"></script>
</head>

<body class="page-body">
<%@ include file="/WEB-INF/jsp/nav.jsp" %>
<div class="page-container">
    <%--<%@ include file="/WEB-INF/jsp/sidebar.jsp" %>--%>
    <div class="main-content">
        <div class="panel panel-default" id="warningHistoryPanel">
            <div class="panel-heading">
                <h3 class="panel-title">告警历史</h3>

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
                <div class="row">
                    <div class="col-sm-2 form-group">
                        <input id="eventSource" class="form-control" placeholder="来源">
                    </div>
                    <div class="col-sm-2 form-group">
                        <input id="eventType" class="form-control" placeholder="类型">
                    </div>
                    <div class="col-sm-2 form-group">
                        <input id="deviceName" class="form-control" placeholder="设备名称">
                    </div>
                    <div class="col-sm-2 form-group">
                        <input id="userName" class="form-control" placeholder="运维账户名称">
                    </div>
                    <div class="col-sm-4 form-group">
                        <input id="eventDetail" class="form-control" placeholder="详情">
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-5 form-group">
                        <input id="eventTime" type="text" class="form-control daterange" data-time-picker="true"
                               data-format="YYYY-MM-DD hh:mm:ss" placeholder="发生时间">
                    </div>
                    <div class="col-sm-5 form-group">
                        <input id="dealTime" type="text" class="form-control daterange" data-time-picker="true"
                               data-format="YYYY-MM-DD hh:mm:ss" placeholder="处理时间">
                    </div>
                    <div class="col-sm-2 form-group">
                        <select class="form-control" id="status">
                            <option value="">所有</option>
                            <option value="1">已处理</option>
                            <option value="0">未处理</option>
                        </select>
                    </div>
                </div>

                <div class="row">
                    <c:if test="${userInfo.role eq 'systemAdmin'}">
                        <div class="col-sm-10 form-group">
                            <button class="btn btn-success" onclick="checkAll('warningHistoryTable')"><i
                                    class="fa fa-circle"></i>
                            </button>
                            <button class="btn btn-success" onclick="uncheckAll('warningHistoryTable')"><i
                                    class="fa fa-circle-o"></i>
                            </button>
                            <button class="btn btn-red" onclick="deleteCheck()"><i class="fa fa-trash-o"></i></button>
                            <button class="btn btn-red" onclick="deleteAll()"><i class="fa fa-bank"></i></button>
                        </div>
                    </c:if>
                    <div class="col-sm-offset-11 form-group">
                        <button onclick="searchByCondition();" class="btn btn-success">
                            <i class="fa-search"></i>
                        </button>
                        <button onclick="clearCondition('warningHistoryPanel');" class="btn btn-gray">
                            <i class="fa-refresh"></i>
                        </button>
                    </div>
                </div>
                <table id="warningHistoryTable" class="table table-bordered table-striped"></table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
