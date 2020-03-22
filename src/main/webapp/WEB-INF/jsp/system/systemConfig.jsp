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
<link rel="stylesheet" href="<%=basePath %>static/js/cytoscape/cytoscape.js-panzoom.css">
<link rel="stylesheet" href="<%=basePath %>static/css/webuploader/webuploader.css">
<script type="text/javascript" src="<%=basePath %>static/js/jquery-1.9.1.js"></script>

</head>

<body class="page-body">
<div class="navbar horizontal-menu navbar-fixed-top">

</div>

<div class="page-container">

    <div class="main-content">

        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">配置备份</h3>
                <div class="panel-options">
                    <a href="#" data-toggle="panel">
                        <span class="collapse-icon">&ndash;</span>
                        <span class="expand-icon">+</span>
                    </a>
                </div>
            </div>
            <div class="panel-body">
                <div role="form">
                    <div class="form-group">
                        <div class="col-sm-2"></div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-2">
                                备份系统配置：
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-4">
                            <a href="/system/config/backup" target='new' class="btn btn-turquoise btn-single btn-icon btn-icon-standalone">
                                <i class="fa-cog"></i>
                                <span>配置备份</span>
                            </a>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-2">
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-2">
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">配置加载</h3>
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
                            <div class="col-sm-2">
                                    加载：
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-4">
                                <div id="uploadBtn" class="btn btn-turquoise btn-single btn-icon-standalone"><i class="fa-cog"></i><span>选择文件</span></div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <button class="btn btn-turquoise btn-single btn-icon btn-icon-standalone">
                                    <i class="fa-cog"></i>
                                    <span>加载</span>
                                </button>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2"></div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div role="form">
                        <div class="form-group">
                            <div class="col-sm-2"></div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                已选择文件：
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-6">
                               <table class="table table-bordered table-striped table-condensed table-model-2 table-hover">
                                   <thead><tr><th style='text-align:center;'>文件名</th><th style='text-align:center;'>操作</th></tr></thead>
                                   <tbody></tbody>
                               </table>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">恢复出厂设置</h3>
                <div class="panel-options">
                    <a href="#" data-toggle="panel">
                        <span class="collapse-icon">&ndash;</span>
                        <span class="expand-icon">+</span>
                    </a>
                </div>
            </div>
            <div class="panel-body">
                <div role="form">
                    <div class="form-group">
                        <div class="col-sm-2"></div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-2">
                                恢复出厂设置：
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-4">
                            <button class="btn btn-turquoise btn-single btn-icon btn-icon-standalone">
                                <i class="fa-cog"></i>
                                <span>恢复出厂设置</span>
                            </button>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-sm-2">
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-2"></div>
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
<script type="text/javascript" src="<%=basePath %>static/js/webuploader/webuploader.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/copsec/system/systemConfig.js"></script>

</body>
</html>
