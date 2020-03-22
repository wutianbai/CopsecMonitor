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
<link rel="stylesheet" href="<%=basePath %>static/assets/js/daterangepicker/daterangepicker-bs3.css">
<script type="text/javascript" src="<%=basePath %>static/js/jquery-1.9.1.js"></script>
</head>

<body class="page-body">
<div class="navbar horizontal-menu navbar-fixed-top">

</div>

<div class="page-container">

    <div class="main-content">

        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">设备日志转发</h3>
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
                                <label class="radio-inline">
                                    状态：
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <label class="radio-inline">
                                    <input type="radio" name="enableTransfer" class="cbr cbr-turquoise" value="start">
                                    启用
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <label class="radio-inline">
                                    <input type="radio" name="enableTransfer" class="cbr cbr-turquoise" value="forbidden">
                                    禁用
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">

                                <button class="btn btn-turquoise btn-single btn-icon btn-icon-standalone">
                                    <i class="fa-cog"></i>
                                    <span>设置</span>
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
                                <label class="radio-inline">
                                   SYSLOG服务地址:
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-4">
                                <label class="radio-inline">
                                    <input type="text" class="form-control" placeholder="SYSLOG服务地址">
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-4"></div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2"></div>
                        </div>
                    </div></div>
                <div class="row">
                    <div role="form">
                        <div class="form-group">
                            <div class="col-sm-2"></div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <label class="radio-inline">
                                    SYSLOG服务端口:
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-4">
                                <label class="radio-inline">
                                    <input type="text" class="form-control" placeholder="SYSLOG服务端口">
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-4"></div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2"></div>
                        </div>
                    </div></div>
            </div>
        </div>
        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">本机日志转发</h3>
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
                                <label class="radio-inline">
                                    状态：
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <label class="radio-inline">
                                    <input type="radio" name="enableLocalTransfer" class="cbr cbr-turquoise" value="yes">
                                    启用
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <label class="radio-inline">
                                    <input type="radio" name="enableLocalTransfer" class="cbr cbr-turquoise" value="no">
                                    禁用
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">

                                <button class="btn btn-turquoise btn-single btn-icon btn-icon-standalone">
                                    <i class="fa-cog"></i>
                                    <span>设置</span>
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
                                <label class="radio-inline">
                                   主机名:
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-4">
                                <label class="radio-inline">
                                    <input type="text" class="form-control" placeholder="主机名">
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-4"></div>
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
                            <div class="col-sm-8">
                                <table class="table table-bordered table-model-2 table-striped">
                                    <thead>
                                        <tr>
                                            <th>序号</th>
                                            <th>服务器IP</th>
                                            <th>服务器端口</th>
                                            <th>操作</th>
                                        </tr>
                                    </thead>
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
                <div class="row">
                    <div class="col-sm-2"></div>
                    <div class="col-sm-8">
                        <table class="table">
                            <tbody>
                            <tr>
                                <td>服务器IP:</td>
                                <td><input class="form-control" type="text"></td>
                            </tr>
                            <tr>
                                <td>服务器端口:</td>
                                <td><input class="form-control" type="text"></td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <div role="form">
                                        <div class="form-group">
                                            <div class="col-sm-2">
                                                <button type="button" class="btn btn-turquoise btn-single btn-icon btn-icon-standalone">
                                                    <i class="fa fa-plus"></i>
                                                    <span>添加</span>
                                                </button>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-sm-2">
                                                <button type="button" class="btn btn-info btn-single btn-icon btn-icon-standalone" style="display:none">
                                                    <i class="fa fa-refresh"></i>
                                                    <span>保存</span>
                                                </button>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-sm-2">
                                                <button type="button" class="btn btn-gray btn-single btn-icon btn-icon-standalone" style="display:none">
                                                    <i class="fa fa-remove"></i>
                                                    <span>取消</span>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="col-sm-2"></div>
                </div>
            </div>
        </div>
        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">日志审计存储配置</h3>
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
                                <label class="radio-inline">
                                    存储天数：
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <input class="form-control" type="text">
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <button class="btn btn-turquoise btn-single btn-icon btn-icon-standalone">
                                    <i class="fa-cog"></i>
                                    <span>设置</span>
                                </button>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">日志审计配置</h3>
                <div class="panel-options">
                    <a href="#" data-toggle="panel">
                        <span class="collapse-icon">&ndash;</span>
                        <span class="expand-icon">+</span>
                    </a>
                </div>
            </div>
            <div class="panel-body">
                <div class="form-horizontal" role="form">
                    <div class="form-group">
                        <div class="col-sm-2"></div>
                        <div class="col-sm-2"></div>
                        <div class="col-sm-2"></div>
                        <div class="col-sm-2"></div>
                        <div class="col-sm-2">
                            <button class="btn btn-turquoise btn-single btn-icon btn-icon-standalone">
                                <i class="fa-cog"></i>
                                <span>设置</span>
                            </button>
                        </div>
                    </div>
                    <div class="form-group-separator"></div>
                    <div class="form-group">
                        <div class="col-sm-2"></div>
                        <div class="col-sm-10">
                            <span>如果未设置任何值则默认记录所有日志信息</span>
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
<script type="text/javascript" src="<%=basePath %>static/assets/js/moment.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/daterangepicker/daterangepicker.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/timepicker/bootstrap-timepicker.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/colorpicker/bootstrap-colorpicker.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/fileDownload1.4.6/jquery.fileDownload.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/copsec/system/logConf.js"></script>
</body>
</html>
