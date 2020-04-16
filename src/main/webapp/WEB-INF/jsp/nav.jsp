<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<div class="settings-pane">
    <a href="#" data-toggle="settings-pane" data-animate="true">
        &times;
    </a>
    <div class="settings-pane-inner">
        <div class="row">
            <div class="col-md-4">
                <div class="user-info">
                    <div class="user-image">
                        <a href="#">
                            <img src="<%=basePath %>static/assets/images/user-2.png" class="img-responsive img-circle"/>
                        </a>
                    </div>

                    <div class="user-details">
                        <h3>
                            <a href="#">${userInfo.name}</a>

                            <!-- Available statuses: is-online, is-idle, is-busy and is-offline -->
                            <span class="user-status is-online"></span>
                        </h3>

                        <p class="user-title" id="userRole">${userInfo.role}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- set fixed position by adding class "navbar-fixed-top" -->
<nav class="navbar horizontal-menu navbar-fixed-top">
    <div class="navbar-inner">

        <!-- Navbar Brand -->
        <div class="navbar-brand">
            <a href="#" class="logo">
                <strong alt="" class="hidden-xs">${system.title}</strong>
                <strong alt="" class="visible-xs" style="color:white;">${system.title}</strong>
            </a>
            <a href="#" data-toggle="settings-pane" data-animate="true">
                <i class="linecons-cog"></i>
            </a>
        </div>

        <!-- Mobile Toggles Link -->
        <div class="nav navbar-mobile">

            <!-- This will toggle the mobile menu and will be visible only on mobile devices -->
            <div class="mobile-menu-toggle">
                <!-- This will open the popup with user profile settings, you can use for any purpose, just be creative -->
                <a href="#" data-toggle="settings-pane" data-animate="true">
                    <i class="linecons-cog"></i>
                </a>

                <a href="#" data-toggle="user-info-menu-horizontal">
                    <i class="fa-bell-o"></i>
                    <span class="badge badge-success" id="mobile-menu-badge"></span>
                </a>

                <!-- data-toggle="mobile-menu-horizontal" will show horizontal menu link only -->
                <!-- data-toggle="mobile-menu" will show sidebar menu link only -->
                <!-- data-toggle="mobile-menu-both" will show sidebar and horizontal menu link -->
                <a href="#" data-toggle="mobile-menu-horizontal">
                    <i class="fa-bars"></i>
                </a>
            </div>
        </div>

        <div class="navbar-mobile-clear"></div>

        <!-- main menu -->
        <ul class="navbar-nav">
            <li>
                <a href="#">
                    <i class="fa fa-cogs"></i>
                    <span class="title">${system.systemMng}</span>
                </a>
                <ul>
                    <li>
                        <a href="<%=basePath%>system/systemReset">
                            <span class="title">${system.systemReset}</span>
                        </a>
                    </li>
                    <c:if test="${userInfo.role eq 'systemAdmin'}">
                        <li>
                            <a href="<%=basePath%>system/opsAccount">
                                <span class="title">${system.opsAccount}</span>
                            </a>
                        </li>
                    </c:if>
                </ul>
            </li>

            <c:if test="${userInfo.role eq 'systemAdmin'}">
                <li>
                    <a href="#">
                        <i class="linecons-desktop"></i>
                        <span class="title">${system.deviceTitle}</span>
                    </a>
                    <ul>
                        <li>
                            <a href="<%=basePath%>node/device">
                                <span class="title">${system.deviceManage}</span>
                            </a>
                        </li>
                    </ul>
                </li>
            </c:if>

            <c:if test="${userInfo.role ne 'auditAdmin'}">
                <li>
                    <a href="#">
                        <i class="el-laptop"></i>
                        <span class="title">监控</span>
                    </a>
                    <ul>
                        <li>
                            <a href="<%=basePath%>node/deviceMonitor">
                                <span class="title">设备拓扑</span>
                            </a>
                        </li>
                    </ul>
                </li>
            </c:if>

            <c:if test="${userInfo.role eq 'systemAdmin'}">
                <li>
                    <a href="#">
                        <i class="fa fa-bar-chart"></i>
                        <span class="title">${system.monitorConfTitle}</span>
                    </a>
                    <ul>
                        <li>
                            <a href="<%=basePath%>monitor/monitorItem">
                                <span class="title">${system.monitorItem}</span>
                            </a>
                        </li>
                        <li>
                            <a href="<%=basePath%>monitor/monitorGroup">
                                <span class="title">${system.monitorGroup}</span>
                            </a>
                        </li>
                        <li>
                            <a href="<%=basePath%>monitor/warningItem">
                                <span class="title">${system.warningItem}</span>
                            </a>
                        </li>
                        <li>
                            <a href="<%=basePath%>monitor/monitorTask">
                                <span class="title">${system.monitorTask}</span>
                            </a>
                        </li>
                    </ul>
                </li>
            </c:if>

            <c:if test="${userInfo.role ne 'auditAdmin'}">
                <li>
                    <a href="#">
                        <i class="fa-area-chart"></i>
                        <span class="title">告警历史</span>
                    </a>
                    <ul>
                        <li>
                            <a href="<%=basePath%>system/warningHistory">
                                <span class="title">告警历史信息</span>
                            </a>
                        </li>
                    </ul>
                </li>
            </c:if>

            <c:if test="${userInfo.role eq 'auditAdmin'}">
                <li>
                    <a href="#">
                        <i class="fa fa-file-code-o"></i>
                        <span class="title">操作审计</span>
                    </a>
                    <ul>
                        <li>
                            <a href="<%=basePath%>system/operateLog">
                                <span class="title">审计日志维护</span>
                            </a>
                        </li>
                    </ul>
                </li>
            </c:if>
        </ul>

        <!-- notifications and other link -->
        <ul class="nav nav-userinfo navbar-right">
            <c:if test="${userInfo.role ne 'auditAdmin'}">
                <li class="dropdown xs-left">
                    <a href="#" data-toggle="dropdown" class="notification-icon notification-icon-messages">
                        <i class="fa-bell-o"></i>
                        <span class="badge badge-success" id="badge"></span>
                    </a>

                    <ul class="dropdown-menu notifications">
                        <li class="top">
                            <p class="small">
                                <a href="#" onclick="handleAllWarningEvent();" class="pull-right">处理所有告警</a>
                                你有 <strong id="warningStrong">0</strong> 条告警未处理.
                            </p>
                        </li>

                        <li>
                            <ul class="dropdown-menu-list list-unstyled ps-scrollbar" id="scrollbar">
                                <%--<li class="active notification-success">--%>
                                    <%--<a href="#">--%>
                                        <%--<i class="fa-user"></i>--%>

                                        <%--<span class="line">--%>
												<%--<strong>New user registered</strong>--%>
											<%--</span>--%>

                                        <%--<span class="line small time">--%>
												<%--30 seconds ago--%>
											<%--</span>--%>
                                    <%--</a>--%>
                                <%--</li>--%>

                                <%--<li class="active notification-secondary">--%>
                                    <%--<a href="#">--%>
                                        <%--<i class="fa-lock"></i>--%>

                                        <%--<span class="line">--%>
												<%--<strong>Privacy settings have been changed</strong>--%>
											<%--</span>--%>

                                        <%--<span class="line small time">--%>
												<%--3 hours ago--%>
											<%--</span>--%>
                                    <%--</a>--%>
                                <%--</li>--%>

                                <%--<li class="notification-primary">--%>
                                    <%--<a href="#">--%>
                                        <%--<i class="fa-thumbs-up"></i>--%>

                                        <%--<span class="line">--%>
												<%--<strong>Someone special liked this</strong>--%>
											<%--</span>--%>

                                        <%--<span class="line small time">--%>
												<%--2 minutes ago--%>
											<%--</span>--%>
                                    <%--</a>--%>
                                <%--</li>--%>

                                <%--<li class="notification-danger">--%>
                                    <%--<a href="#">--%>
                                        <%--<i class="fa-calendar"></i>--%>

                                        <%--<span class="line">--%>
												<%--John cancelled the event--%>
											<%--</span>--%>

                                        <%--<span class="line small time">--%>
												<%--9 hours ago--%>
											<%--</span>--%>
                                    <%--</a>--%>
                                <%--</li>--%>

                                <%--<li class="notification-info">--%>
                                    <%--<a href="#">--%>
                                        <%--<i class="fa-database"></i>--%>

                                        <%--<span class="line">--%>
												<%--The server is status is stable--%>
											<%--</span>--%>

                                        <%--<span class="line small time">--%>
												<%--yesterday at 10:30am--%>
											<%--</span>--%>
                                    <%--</a>--%>
                                <%--</li>--%>

                                <%--<li class="notification-warning">--%>
                                    <%--<a href="#">--%>
                                        <%--<i class="fa-envelope-o"></i>--%>

                                        <%--<span class="line">--%>
												<%--New comments waiting approval--%>
											<%--</span>--%>

                                        <%--<span class="line small time">--%>
												<%--last week--%>
											<%--</span>--%>
                                    <%--</a>--%>
                                <%--</li>--%>
                            </ul>
                        </li>

                        <li class="external">
                            <a href="<%=basePath%>system/warningEvent">
                                <span>查看所有告警</span>
                                <i class="fa-link-ext"></i>
                            </a>
                        </li>
                    </ul>
                </li>
            </c:if>

            <li class="dropdown user-profile">
                <a href="#" data-toggle="dropdown">
                    <img src="<%=basePath %>static/assets/images/user-4.png" alt="user-image"
                         class="img-circle img-inline userpic-32"
                         width="28"/>
                    <span>
								${userInfo.name}
								<i class="fa-angle-down"></i>
							</span>
                </a>

                <ul class="dropdown-menu user-profile-menu list-unstyled">
                    <li class="last">
                        <a href="<%=basePath %>logout">
                            <i class="fa-lock"></i>
                            注销
                        </a>
                    </li>
                </ul>
            </li>
        </ul>
    </div>
</nav>