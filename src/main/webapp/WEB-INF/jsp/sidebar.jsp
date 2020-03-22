<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<div class="sidebar-menu toggle-others collapsed">
    <div class="sidebar-menu-inner">
        <ul id="main-menu" class="main-menu">
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
    </div>
</div>