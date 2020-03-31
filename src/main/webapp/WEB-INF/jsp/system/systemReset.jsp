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
    <script type="text/javascript" src="<%=basePath %>static/js/copsec/system/systemReset.js"></script>
</head>

<body class="page-body">
<%@ include file="/WEB-INF/jsp/nav.jsp" %>
<div class="page-container">
    <%--<%@ include file="/WEB-INF/jsp/sidebar.jsp" %>--%>
    <div class="main-content">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">修改密码</h3>
                <div class="panel-options">
                    <a href="#" data-toggle="panel">
                        <span class="collapse-icon">&ndash;</span>
                        <span class="expand-icon">+</span>
                    </a>
                </div>
            </div>
            <div class="panel-body">
                <div role="form" class="form-horizontal">
                    <div class="form-group">
                        <label class="col-sm-4 control-label">原密码</label>
                        <div class="col-sm-4">
                            <input type="password" class="form-control" placeholder="输入原密码">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">新密码</label>
                        <div class="col-sm-4">
                            <input type="password" class="form-control" placeholder="输入新密码">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">确认密码</label>
                        <div class="col-sm-4">
                            <input type="password" class="form-control" placeholder="输入确认密码">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label"></label>
                        <div class="col-sm-4">
                            <button id="save" type="button"
                                    class="btn btn-success btn-single btn-icon btn-icon-standalone">
                                <i class="fa-cog"></i>
                                <span>保存</span>
                            </button>
                            <button id="empty" type="button"
                                    class="btn btn-gray btn-single btn-icon btn-icon-standalone">
                                <i class="fa fa-rotate-left"></i>
                                <span>清空</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
