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
    <script type="text/javascript" src="<%=basePath %>static/js/copsec/log/operateLog.js"></script>
</head>

<body class="page-body">
<%@ include file="/WEB-INF/jsp/nav.jsp" %>
<div class="page-container">
    <%@ include file="/WEB-INF/jsp/sidebar.jsp" %>
    <div class="main-content">
        <div class="panel panel-default" id="operateLogPanel">
            <div class="panel-heading">
                <h3 class="panel-title">系统操作日志</h3>

                <div class="panel-options">
                    <a href="#">
                        <i class="linecons-cog"></i>
                    </a>

                    <a href="#" data-toggle="panel">
                        <span class="collapse-icon">&ndash;</span>
                        <span class="expand-icon">+</span>
                    </a>

                    <a href="#" data-toggle="reload">
                        <i class="fa-rotate-right"></i>
                    </a>

                    <a href="#" data-toggle="remove">
                        &times;
                    </a>
                </div>
            </div>

            <div class="panel-body">
                <div class="row">
                    <div class="col-sm-3 form-group">
                        <input id="operateUser" class="form-control" placeholder="操作用户">
                    </div>
                    <div class="col-sm-3 form-group">
                        <input id="ip" class="form-control" placeholder="地址">
                    </div>
                    <div class="col-sm-3 form-group">
                        <input id="operateType" class="form-control" placeholder="操作类型">
                    </div>
                    <div class="col-sm-3 form-group">
                        <input id="desc" class="form-control" placeholder="详情">
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-2 form-group">
                        <select class="form-control" id="result">
                            <option value="">所有</option>
                            <option value="1">成功</option>
                            <option value="0">失败</option>
                        </select>
                    </div>
                    <div class="col-sm-6 form-group">
                        <input id="date" type="text" class="form-control daterange" data-time-picker="true"
                               data-format="YYYY-MM-DD hh:mm:ss" placeholder="时间">
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-10 form-group">
                        <button class="btn btn-success" onclick="checkAll('operateLogTable')">
                            <i class="fa fa-circle"></i>
                        </button>
                        <button class="btn btn-success" onclick="uncheckAll('operateLogTable')">
                            <i class="fa fa-circle-o"></i>
                        </button>
                        <button class="btn btn-danger" onclick="deleteCheck()">
                            <i class="fa fa-trash"></i>
                        </button>
                        <button class="btn btn-red" onclick="deleteAll();">
                            <i class="fa fa-trash-o"></i>
                        </button>
                        <button class="btn btn-success" onclick="exportLog();">
                            <i class="fa fa-print"></i>
                        </button>
                    </div>
                    <div class="col-sm-offset-11 form-group">
                        <button onclick="searchByCondition();" class="btn btn-success">
                            <i class="fa-search"></i>
                        </button>
                        <button onclick="clearCondition('operateLogPanel');" class="btn btn-gray">
                            <i class="fa-refresh"></i>
                        </button>
                    </div>
                </div>
                <table id="operateLogTable" class="table table-bordered table-striped"></table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
