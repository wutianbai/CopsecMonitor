<%--
  Created by IntelliJ IDEA.
  User: Copsec
  Date: 2019/7/9
  Time: 13:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script type="text/javascript"  src="<%=basePath %>static/js/html5/html5shiv.min.js"></script>
<script type="text/javascript"  src="<%=basePath %>static/js/html5/respond.min.js"></script>
<![endif]-->
<meta name="viewport" content="user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, minimal-ui">
<meta http-equiv="X-UA-Compatible" content="IE=Edge">
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
<link rel="stylesheet" href="<%=basePath %>static/assets/js/daterangepicker/daterangepicker-bs3.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/datatables/dataTables.bootstrap.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/multiselect/css/multi-select.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/select2/select2.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/select2/select2-bootstrap.css">
<link rel="stylesheet" href="<%=basePath %>static/js/cytoscape/cytoscape.js-panzoom.css">
<link rel="stylesheet" href="<%=basePath %>static/js/chart2.8/css/Chart.min.css">
<script type="text/javascript" src="<%=basePath %>static/js/chart2.8/moment.2.24.0.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/chart2.8/Chart.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/jquery-1.9.1.js"></script>
</head>

<body class="page-body">
<div class="navbar horizontal-menu navbar-fixed-top">

</div>

<div class="page-container">

    <div class="main-content">
        <div class="row">
            <div class="col-sm-3">
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
            </div>
            <div class="col-sm-3">
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
            </div>
            <div class="col-sm-3">
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
            </div>
            <div class="col-sm-3">
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
        </div>
        <div class="panel panel-color panel-success">
            <div class="panel-body" >
                <div class="row">
                    <div class="col-sm-2">
                        <div class="vertical-top">
                            <button class="btn btn-turquoise btn-block">网络流量汇总</button>
                            <button class="btn btn-turquoise btn-block">文件总量汇总</button>
                            <button class="btn btn-turquoise btn-block">文件大小汇总</button>
                            <button class="btn btn-turquoise btn-block">数据库采集总量汇总</button>
                            <button class="btn btn-turquoise btn-block">数据库上传总量汇总</button>
                            <button class="btn btn-turquoise btn-block">协议连接总量汇总</button>
                            <button class="btn btn-turquoise btn-block">协议流量总量汇总</button>
                            <button class="btn btn-turquoise btn-block">查询</button>
                            <div class="daterange daterange-inline add-ranges" data-format="YYYY-MM-DD">
                                <i class="linecons-clock"></i><span>请选择日期</span></div>
                        </div>
                    </div>
                    <div class="col-sm-10"><canvas></canvas></div>
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
<script type="text/javascript" src="<%=basePath %>static/assets/js/moment.min.js"></script>

<script type="text/javascript" src="<%=basePath %>static/assets/js/daterangepicker/daterangepicker.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/jquery-ui/jquery-ui.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/jquery-validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/inputmask/jquery.inputmask.bundle.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/formwizard/jquery.bootstrap.wizard.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/datepicker/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/timepicker/bootstrap-timepicker.min.js"></script>
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

<!-- Imported scripts on this page -->
<script type="text/javascript" src="<%=basePath %>static/assets/js/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/jvectormap/regions/jquery-jvectormap-world-mill-en.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-widgets.js"></script>

<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-custom.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/copsec/common.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/copsec/statistics/total.js"></script>
</body>
</html>
