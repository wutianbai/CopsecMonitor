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
        <%--<div class="vertical-top">--%>
            <%--<div class="btn-group left-dropdown">--%>
                <%--<button type="button" class="btn btn-success">${system.deviceTitle}</button>--%>
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
                <%--<button type="button" class="btn btn-success">${system.linkTitle}</button>--%>
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
                <%--<button type="button" class="btn btn-success">${system.zoneTitle}</button>--%>
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
                <%--<button type="button" class="btn btn-success">${system.topologyUpdate}</button>--%>
                <%--<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">--%>
                    <%--<i class=""><span class="fa fa-spin fa-circle-o-notch"></span></i>--%>
                <%--</button>--%>
            <%--</div>--%>
        <%--</div>--%>
        <div id="cy" class="cy"></div>
    </div>
</div>

<%--设备管理--%>
<div class="modal fade" data-backdrop="static" id="deviceModal">
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
                            <label class="control-label">设备ID</label>
                            <input type="text" class="form-control" id="deviceId">
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">设备主机名</label>
                            <input type="text" class="form-control" id="deviceHostname">
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">设备类型</label>
                            <select class="form-control" id="deviceType">
                                <option value="dx">网闸</option>
                                <option value="kebo">服务器</option>
                                <%--<option value="win">windows设备</option>--%>
                                <%--<option value="switch">交换机设备</option>--%>
                                <%--<option value="firewall">防火墙设备</option>--%>
                                <%--<option value="net">其他网络设备</option>--%>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">设备IP地址</label>
                            <input type="text" class="form-control" id="deviceIP">
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">设备运维负责人</label>
                            <select class="form-control" id="monitorUserId"></select>
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">网络区域</label>
                            <select class="form-control" id="zone"></select>
                        </div>
                    </div>
                </div>
                <div class="row" style="display:none">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label">横坐标</label>
                            <input type="text" class="form-control">
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label">纵坐标</label>
                            <input type="text" class="form-control">
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
<div class="modal fade" data-backdrop="static" id="linkModal">
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
                            <label class="control-label">起始设备</label>
                            <select class="form-control" id="linkStart"></select>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">终点设备</label>
                            <select class="form-control" id="linkEnd" multiple></select>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">连线样式</label>
                            <script type="text/javascript">
                                jQuery(document).ready(function ($) {
                                    $("#linkStyle").select2({
                                        allowClear: true
                                    }).on('select2-open', function () {
                                        $(this).data('select2').results.addClass('overflow-hidden').perfectScrollbar();
                                    });
                                });
                            </script>
                            <select class="form-control" id="linkStyle">
                                <option value="taxi">单箭头-折线</option>
                                <option value="unbundled-bezier">单箭头-曲线</option>
                                <option value="straight">单箭头-直线</option>
                                <option value="taxi42">双箭头-折线</option>
                                <option value="unbundled-bezier42">双箭头-曲线</option>
                                <option value="straight42">双箭头-直线</option>
                            </select>
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
<div class="modal fade" data-backdrop="static" id="zoneModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title"></h4>
            </div>

            <div class="modal-body">
                <%--<div class="row">--%>
                <%--<div class="col-md-12">--%>
                <%--<div class="form-group">--%>
                <%--<label class="control-label">网络区域ID</label>--%>
                <%--<input type="text" class="form-control" id="zoneId"/>--%>
                <%--</div>--%>
                <%--</div>--%>
                <%--</div>--%>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">网络区域名称</label>
                            <input type="text" class="form-control" id="zoneName"/>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">区域颜色</label>
                            <div class="input-group">
                                <input type="text" class="form-control colorpicker" data-format="hex" value="#5a3d3d"
                                       id="zoneColor"/>
                                <div class="input-group-addon">
                                    <i class="color-preview"></i>
                                </div>
                            </div>
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