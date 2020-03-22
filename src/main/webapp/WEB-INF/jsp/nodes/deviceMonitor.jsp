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
    <script type="text/javascript" src="<%=basePath %>static/js/copsec/node/device.js"></script>
    <style>
        .cy {
            display: block;
            height: 93%;
        }
    </style>
</head>

<body class="page-body" style="background-color: #2d2e30;">
<%@ include file="/WEB-INF/jsp/nav.jsp" %>
<div class="page-container">
    <%@ include file="/WEB-INF/jsp/sidebar.jsp" %>
    <div class="main-content" style="height: 100%">
        <div id="cy" class="cy"></div>
    </div>
</div>
</body>
</html>