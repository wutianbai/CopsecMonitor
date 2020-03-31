<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<div class="sidebar-menu toggle-others collapsed">
    <div class="sidebar-menu-inner">
        <ul id="main-menu" class="main-menu">
            <li>
                <a href="#">
                    <i class="el-desktop"></i>
                    <span class="title">${system.deviceTitle}</span>
                </a>
                <ul>
                    <li>
                        <a href="#" id="addDevice"><i class="fa-plus"></i>${system.deviceAdd}</a>
                    </li>
                    <li>
                        <a href="#" id="editDevice"><i class="fa-pencil"></i>${system.deviceEdit}</a>
                    </li>
                    <li>
                        <a href="#" id="deleteDevice"><i class="fa-trash"></i>${system.deviceDelete}</a>
                    </li>
                </ul>
            </li>

            <li>
                <a href="#">
                    <i class="fa fa-chain"></i>
                    <span class="title">${system.linkTitle}</span>
                </a>
                <ul>
                    <li>
                        <a href="#" id="addLink"><i class="fa-plus"></i>${system.linkAdd}</a>
                    </li>
                    <li>
                        <a href="#" id="editLink"><i class="fa-pencil"></i>${system.linkEdit}</a>
                    </li>
                    <li>
                        <a href="#" id="deleteLink"><i class="fa-trash"></i>${system.linkDelete}</a>
                    </li>
                </ul>
            </li>

            <li>
                <a href="#">
                    <i class="fa fa-pie-chart"></i>
                    <span class="title">${system.zoneTitle}</span>
                </a>
                <ul>
                    <li>
                        <a href="#" id="addZone"><i class="fa-plus"></i>${system.zoneAdd}</a>
                    </li>
                    <li>
                        <a href="#" id="editZone"><i class="fa-pencil"></i>${system.zoneUpdate}</a>
                    </li>
                    <li>
                        <a href="#" id="deleteZone"><i class="fa-trash"></i>${system.zoneDelete}</a>
                    </li>
                </ul>
            </li>

            <li id="topologyUpdate">
                <a href="#">
                    <i class=""><span class="fa fa-spin fa-circle-o-notch"></span></i>
                    <span class="title">${system.topologyUpdate}</span>
                </a>
            </li>
        </ul>

        <%--<div class="vertical-top">--%>
        <%--<div class="btn-group left-dropdown">--%>
        <%--&lt;%&ndash;<button type="button" class="btn btn-success">${system.deviceTitle}</button>&ndash;%&gt;--%>
        <%--<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">--%>
        <%--<li class="fa-chevron-down"></li>--%>
        <%--</button>--%>
        <%--<ul class="dropdown-menu dropdown-success" role="menu">--%>
        <%--<li>--%>
        <%--<a href="#" id="addDevice"><i class="fa-plus"></i>${system.deviceAdd}</a>--%>
        <%--</li>--%>
        <%--<li>--%>
        <%--<a href="#" id="editDevice"><i class="fa-pencil"></i>${system.deviceEdit}</a>--%>
        <%--</li>--%>
        <%--<li>--%>
        <%--<a href="#" id="deleteDevice"><i class="fa-trash"></i>${system.deviceDelete}</a>--%>
        <%--</li>--%>
        <%--</ul>--%>
        <%--</div>--%>
        <%--<div class="btn-group left-dropdown">--%>
        <%--&lt;%&ndash;<button type="button" class="btn btn-success">${system.linkTitle}</button>&ndash;%&gt;--%>
        <%--<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">--%>
        <%--<li class="fa-chevron-down"></li>--%>
        <%--</button>--%>
        <%--<ul class="dropdown-menu dropdown-success" role="menu">--%>
        <%--<li>--%>
        <%--<a href="#" id="addLink"><i class="fa-plus"></i>${system.linkAdd}</a>--%>
        <%--</li>--%>
        <%--<li>--%>
        <%--<a href="#" id="editLink"><i class="fa-pencil"></i>${system.linkEdit}</a>--%>
        <%--</li>--%>
        <%--<li>--%>
        <%--<a href="#" id="deleteLink"><i class="fa-trash"></i>${system.linkDelete}</a>--%>
        <%--</li>--%>
        <%--</ul>--%>
        <%--</div>--%>
        <%--<div class="btn-group left-dropdown">--%>
        <%--&lt;%&ndash;<button type="button" class="btn btn-success">${system.zoneTitle}</button>&ndash;%&gt;--%>
        <%--<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">--%>
        <%--<li class="fa-chevron-down"></li>--%>
        <%--</button>--%>
        <%--<ul class="dropdown-menu dropdown-success" role="menu">--%>
        <%--<li>--%>
        <%--<a href="#" id="addZone"><i class="fa-plus"></i>${system.zoneAdd}</a>--%>
        <%--</li>--%>
        <%--<li>--%>
        <%--<a href="#" id="editZone"><i class="fa-pencil"></i>${system.zoneUpdate}</a>--%>
        <%--</li>--%>
        <%--<li>--%>
        <%--<a href="#" id="deleteZone"><i class="fa-trash"></i>${system.zoneDelete}</a>--%>
        <%--</li>--%>
        <%--</ul>--%>
        <%--</div>--%>
        <%--<div id="topologyUpdate" class="btn-group left-dropdown">--%>
        <%--&lt;%&ndash;<button type="button" class="btn btn-success">${system.topologyUpdate}</button>&ndash;%&gt;--%>
        <%--<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">--%>
        <%--<i class=""><span class="fa fa-spin fa-circle-o-notch"></span></i>--%>
        <%--</button>--%>
        <%--</div>--%>
        <%--</div>--%>
    </div>
</div>