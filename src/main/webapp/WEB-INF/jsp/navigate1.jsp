<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
         contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>

<div class="navbar-inner">
    <div class="navbar-brand">
        <a href="#" class="logo" style="font-size:25px">${system.title }
        </a>
    </div>

    <ul class="navbar-nav">
        <!-- 管理admin -->
        <c:if test="${userInfo.role eq 'systemAdmin'}">
            <li class="has-sub"><!-- 设备管理-->
                <a href="#">
                    <i class="linecons-desktop"></i>
                    <span class="title">${system.deviceTitle}</span>
                </a>
                <ul>
                    <li>
                        <a href="<%=basePath%>node/device">
                            <span class="title">${system.deviceTitle }</span>
                        </a>
                    </li>
                </ul>
            </li>
        </c:if>
    </ul>

    <!-- notifications and other links -->
    <ul class="nav nav-userinfo navbar-right">
        <c:if test="${userInfo.role eq 'systemAdmin'}">
            <li class="dropdown xs-left"> <!-- 待审核 -->
                <a href="<%=basePath %>system/systemAlarm" class="notification-icon notification-icon-messages">
                    <i class="fa-bell-o"></i>
                    <span class="badge badge-turquoise">告警信息</span>
                </a>
            </li>

            <li class="dropdown xs-left"> <!-- 待审核 -->
                <a href="<%=basePath %>system/serverLog" class="notification-icon notification-icon-messages">
                    <i class="fa-bell-o"></i>
                    <span class="badge badge-turquoise">服务日志</span>
                </a>
            </li>

            <li class="dropdown xs-left"> <!-- 待审核 -->
                <a href="<%=basePath %>system/operateLog1" class="notification-icon notification-icon-messages">
                    <i class="fa-bell-o"></i>
                    <span class="badge badge-turquoise">操作日志</span>
                </a>
            </li>
        </c:if>
        <li class="dropdown user-profile">
            <a href="#" data-toggle="dropdown">
                <span style="font-size:15px">${userInfo.id}
                    <i class="fa-angle-down"></i>
                </span>
            </a>
            <ul class="dropdown-menu user-profile-menu list-unstyled">
                <li>
                    <a href="<%=basePath %>logout" target="_self">
                         <span>
                            <i id="logoutBtn" class="fa-lock"></i>退出
                         </span>
                    </a>
                </li>
            </ul>
        </li>
    </ul>
</div>
