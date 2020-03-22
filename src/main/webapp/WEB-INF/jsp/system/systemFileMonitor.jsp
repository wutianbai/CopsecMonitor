<%--
  Created by IntelliJ IDEA.
  User: Copsec
  Date: 2019/6/27
  Time: 14:30
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<html>
<title>${system.title}</title>
<script>
    var contextPath = "<%=basePath %>";
</script>
<meta name="viewport" content="user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, minimal-ui">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/fonts/linecons/css/linecons.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/fonts/fontawesome/css/font-awesome.min.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/bootstrap.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/xenon-core.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/xenon-forms.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/xenon-components.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/xenon-skins.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/css/custom.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/datatables/dataTables.bootstrap.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/multiselect/css/multi-select.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/select2/select2.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/select2/select2-bootstrap.css">
<script type="text/javascript" src="<%=basePath %>static/js/jquery-1.9.1.js"></script>

</head>

<body class="page-body">
<div class="navbar horizontal-menu navbar-fixed-top">

</div>

<div class="page-container">

    <div class="main-content">

        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">文件同步告警策略配置</h3>
                <div class="panel-options">
                    <a href="#" data-toggle="panel">
                        <span class="collapse-icon">&ndash;</span>
                        <span class="expand-icon">+</span>
                    </a>
                </div>
            </div>
            <div class="panel-body">
                <div class="row">
                    <div role="form">
                        <div class="form-group">
                            <div class="col-sm-2"></div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-8">
                                <table class="table table-model-2 table-bordered table-striped">
                                    <thead>
                                        <tr>
                                            <th>同步任务</th>
                                            <th>流量控制值</th>
                                            <th>告警间隔</th>
                                            <th>操作</th>
                                        </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2"></div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div role="form" class="form-horizontal">
                        <div class="form-group">
                            <label class="col-sm-4 control-label">同步任务:</label>
                            <div class="col-sm-4">
                                <select class="form-control"></select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label">总量检测:</label>
                            <div class="col-sm-4">
                                <input type="checkbox" class="cbr"><label>未启用</label>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label">文件总量值</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control"/>
                            </div>
                        </div>
                        <div class="form-group">
                              <label class="col-sm-4 control-label">总量单位</label>
                            <div class="col-sm-4">
                                <select class="form-control">
                                    <option value="M">M</option>
                                    <option value="G">G</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label">任务实时监控:</label>
                            <div class="col-sm-4">
                                <input type="checkbox" class="cbr"/><label>未启用</label>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label">报警间隔:</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label">时间单位:</label>
                            <div class="col-sm-4">
                                <select class="form-control">
                                    <option value="m">分钟</option>
                                    <option value="h">小时</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label"></label>
                            <div class="col-sm-4">
                                <label class="control-label"></label>
                                <button class="btn btn-turquoise btn-single btn-icon btn-icon-standalone">
                                    <i class="fa-cog"></i>
                                    <span>设置</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">文件任务名称映射</h3>
                <div class="panel-options">
                    <a href="#" data-toggle="panel">
                        <span class="collapse-icon">&ndash;</span>
                        <span class="expand-icon">+</span>
                    </a>
                </div>
            </div>
            <div class="panel-body">
                <div class="row">
                    <div role="form">
                        <div class="form-group">
                            <div class="col-sm-2"></div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-8">
                                <table class="table table-model-2 table-bordered table-striped">
                                    <thead>
                                    <tr>
                                        <th>同步任务</th>
                                        <th>任务名称</th>
                                        <th>操作</th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2"></div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div role="form" class="form-horizontal">
                        <div class="form-group">
                            <label class="col-sm-4 control-label">同步任务:</label>
                            <div class="col-sm-4">
                                <select class="form-control"></select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label">任务名称</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label"></label>
                            <div class="col-sm-4">
                                <label class="control-label"></label>
                                <button class="btn btn-turquoise btn-single btn-icon btn-icon-standalone">
                                    <i class="fa-cog"></i>
                                    <span>设置</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="<%=basePath %>static/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/TweenMax.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/resizeable.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/joinable.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-api.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-toggles.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/jquery-ui/jquery-ui.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/datatables/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/datatables/dataTables.bootstrap.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/datatables/yadcf/jquery.dataTables.yadcf.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/datatables/tabletools/dataTables.tableTools.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/jquery-validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/inputmask/jquery.inputmask.bundle.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/formwizard/jquery.bootstrap.wizard.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/datepicker/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/multiselect/js/jquery.multi-select.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/selectboxit/jquery.selectBoxIt.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/toastr/toastr.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/dropzone/dropzone.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/select2/select2.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/multiselect/js/jquery.multi-select.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/selectboxit/jquery.selectBoxIt.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/tagsinput/bootstrap-tagsinput.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/typeahead.bundle.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/handlebars.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-custom.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/copsec/common.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/copsec/system/systemFileMonitor.js"></script>

</body>
</html>
