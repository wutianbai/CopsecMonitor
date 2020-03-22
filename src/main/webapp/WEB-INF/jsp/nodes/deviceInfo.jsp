<%--
Created by IntelliJ IDEA.
User: adam
Date: 2019/6/16
Time: 20:37
To change this template use File | Settings | File Templates.
--%>
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
<title>${system.title}</title>
<meta charset=utf-8/>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta http-equiv="X-UA-Compatible" content="IE=9">
<meta name="viewport" content="user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, minimal-ui">
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script type="text/javascript" src="<%=basePath %>static/js/html5/html5shiv.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/html5/respond.min.js"></script>
<![endif]-->
<script>
    var contextPath = "<%=basePath %>";
</script>
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
<script type="text/javascript" src="<%=basePath %>static/js/es5/es5-shim.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/es5/es5-sham.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/es5/es6-shim.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/es5/es6-sham.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/jquery-1.9.1.js"></script>

<style>
    .cy {
        display: block;
        background-color: #14173e;
    }


</style>
</head>

<body class="page-body">
<nav class="navbar horizontal-menu navbar-fixed-top">
</nav>

<div class="page-container">
    <div class="main-content">

        <div class="row">
            <div class="col-sm-2">
                <div class="xe-widget xe-counter-block xe-counter-block-pink">
                    <div class="xe-upper">
                        <div class="xe-icon">
                            <i class="fa-file-text"></i>
                        </div>
                        <div class="xe-label">
                            <strong class="num">0</strong>
                            <span>文件总量</span>
                        </div>
                    </div>
                    <div class="xe-lower">
                        <div class="border"></div>
                        <span>概要</span>
                        <strong></strong>
                        <strong></strong>
                    </div>
                </div>
                <div class="xe-widget xe-counter-block xe-counter-block-success">
                    <div class="xe-upper">
                        <div class="xe-icon">
                            <i class="linecons-globe"></i>
                        </div>
                        <div class="xe-label">
                            <strong class="num">0</strong>
                            <span>网络流量</span>
                        </div>
                    </div>
                    <div class="xe-lower">
                        <div class="border"></div>
                        <span>概要</span>
                        <strong></strong>
                        <strong></strong>
                    </div>
                </div>
                <div class="xe-widget xe-counter-block xe-counter-block-orange">
                    <div class="xe-upper">
                        <div class="xe-icon">
                            <i class="fa-table"></i>
                        </div>
                        <div class="xe-label">
                            <strong class="num">0</strong>
                            <span>数据库总量</span>
                        </div>
                    </div>
                    <div class="xe-lower">
                        <div class="border"></div>
                        <span>概要</span>
                        <strong></strong>
                        <strong></strong>
                    </div>
                </div>
                <div class="xe-widget xe-counter-block xe-counter-block-danger">
                    <div class="xe-upper">
                        <div class="xe-icon">
                            <i class="fa-chain"></i>
                        </div>
                        <div class="xe-label">
                            <strong class="num">0</strong>
                            <span>协议数据</span>
                        </div>
                    </div>
                    <div class="xe-lower">
                        <div class="border"></div>
                        <span>概要</span>
                        <strong></strong>
                        <strong></strong>
                    </div>
                </div>
            </div>
            <div class="col-sm-10">
                <div class="cy" id="cy"></div>
            </div>
        </div>
        <div class="row">
            <table class="table table-model-2 table-bordered table-striped">
                <thead>
                <tr>
                    <th>任务名称</th>
                    <th>运行时间</th>
                    <th>当日总量</th>
                    <th>当日总流量</th>
                    <th>任务总量</th>
                    <th>任务总流量</th>
                </tr>
                </thead>
                <tbody>

                </tbody>
            </table>
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
<script type="text/javascript"
        src="<%=basePath %>static/assets/js/datatables/yadcf/jquery.dataTables.yadcf.js"></script>
<script type="text/javascript"
        src="<%=basePath %>static/assets/js/datatables/tabletools/dataTables.tableTools.min.js"></script>
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
<script type="text/javascript" src="<%=basePath %>static/assets/js/colorpicker/bootstrap-colorpicker.min.js"></script>

<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/cytoscape.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/cytoscape-panzoom.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/copsec/common.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/popper.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/index.all.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/cytoscape-popper.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/copsec/node/deviceInfo.js"></script>


</body>
</html>
