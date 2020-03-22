<%--
Created by IntelliJ IDEA.
User: adam
Date: 2019/6/16
Time: 20:37
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
<meta charset=utf-8 />
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="format-detection" content="telephone=no, email=no">
<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<%--
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover">
--%>
<meta name="viewport" content="user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, minimal-ui">
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script type="text/javascript"  src="<%=basePath %>static/js/html5/html5shiv.min.js"></script>
<script type="text/javascript"  src="<%=basePath %>static/js/html5/respond.min.js"></script>
<![endif]-->
<script>
    var contextPath = "<%=basePath %>";
    document.addEventListener('DOMContentLoaded', function() {
        function handleCalc() {
            var dw = document.body.clientWidth;
            var minScale = dw / 1920;
            if (minScale < 1200 / 1920) {
                minScale = 1200 / 1920;
            } else if (minScale > 1) {
                minScale = 1;
            }
            document.documentElement.style.fontSize = (minScale * 100) + 'px';
        }
        handleCalc();
        window.addEventListener('resize', handleCalc);
    });
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
<link rel="stylesheet" href="<%=basePath %>static/js/cytoscape/cytoscape.js-panzoom.css">
<link rel="stylesheet" href="<%=basePath %>static/css/index.css">
<script type="text/javascript" src="<%=basePath %>static/js/es5/es5-shim.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/es5/es5-sham.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/es5/es6-shim.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/es5/es6-sham.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/jquery-1.9.1.js"></script>
<style>
    .cy {
        display: block;
        height: 95%;
    }
    body
    {
        background-image: url("<%=basePath %>static/images/server/bg.jpg");
        background-repeat:no-repeat;
        background-size:100% 100% contain;
        background-position:center
    }
</style>
</head>

<body>
<div class="page-wrap">
    <div class="time-bar">
        <div class="btn-box">${userInfo.id}</div>
        <div class="time-box">
            <!-- <span class="num-box">09:35:26</span> -->
            <!-- <span class="line">|</span> -->
            <span class="num-box"></span>
        </div>
    </div>
    <div class="header-box">
        <ul class="item-box left-box">
            <li class="text-box"></li>
            <li class="text-box"></li>
            <li class="text-box"></li>
        </ul>
        <div class="center-box">${system.title }</div>
        <ul class="item-box right-box">
            <li class="text-box"></li>
            <c:if test="${userInfo.role != 'monitor'}">
                <a class="text-box" style="color:#ffffff" href="<%=basePath %>system/systemAlarm">告警信息</a>
                <a class="text-box" style="color:#ffffff" href="<%=basePath%>node/device">返回</a>
            </c:if>
            <c:if test="${userInfo.role eq 'monitor'}">
                <li class="text-box"></li>
                <li class="text-box"></li>
            </c:if>
        </ul>
    </div>
    <div class="content_warp">
        <div class="content">
            <div class="content_left">
                <div class="content_left_top">
                    <div class="content_title">
                        <p class="content_title_icon"></p>
                        <span>任务统计</span>
                    </div>
                    <div class="content_left_text">
                        <dl class="content_left_text_dl">
                            <dt style="font-family: 'Microsoft YaHei';color: #ffffff">上传总量</dt>
                            <%--<dd class="content_left_text_dd"></dd>--%>
                        </dl>
                        <%--<dl class="content_left_text_dl">
                            <dt style="font-family: 'Microsoft YaHei';color: #ffffff">采集总量</dt>
                            &lt;%&ndash;<dd class="content_left_text_dd"></dd>&ndash;%&gt;
                        </dl>--%>
                    </div>
                    <div class="content_left_pie">
                        <div class="content_left_pie1"><div id="leftCanvas" style="width:100%;height: 100%"></div></div>
                        <%--<div class="content_left_pie1"><canvas id="rightCanvas"></canvas></div>--%>
                    </div>
                    <div class="content_left_data">
                        <ul class="content_left_data_ul">
                        </ul>
                        <%--<ul class="content_left_data_ul">
                        </ul>--%>
                    </div>
                </div>
                <div class="content_left_bototm">
                    <div class="content_title">
                        <p class="content_title_icon"></p>
                        <span>网络数据流量</span>
                    </div>
                    <canvas id="netCanvas"></canvas>
                </div>
            </div>
            <div class="content_center">
                <p class="content_center_title">网络拓扑结构</p>
                <div id="cy"></div>
            </div>
            <div class="content_right">
                <div class="content_right_top">
                    <div class="content_title">
                        <p class="content_title_icon"></p>
                        <span style="color:#ffffff">资源概况</span>
                    </div>
                    <div class="content_right_data1">
                        <div class="content_rigth_title_left">
                            <span class="content_right_title_top">cpu</span>
                        </div>
                        <div class="content_rigth_title_right">
                            <ul>
                                <li class="content_right_data_li">
                                    <span class="content_right_data_span1">1</span>
                                    <div class="processor_right">
                                        <span class="span_device_name"></span>
                                        <div class="progress">
                                            <div class="progress-bar progress-first-success" role="progressbar"
                                                 aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: 100%;">
                                            </div>
                                        </div>
                                    </div>
                                    <span class="content_right_data_span2"></span>
                                </li>
                                <li class="content_right_data_li">
                                    <span class="content_right_data_span1">2</span>
                                    <div class="processor_right">
                                        <span class="span_device_name"></span>
                                        <div class="progress">
                                            <div class="progress-bar progress-second-success" role="progressbar"
                                                 aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: 100%;">
                                            </div>
                                        </div>
                                    </div>
                                    <span class="content_right_data_span2"></span>
                                </li>
                                <li class="content_right_data_li">
                                    <span class="content_right_data_span1">3</span>
                                    <div class="processor_right">
                                        <span class="span_device_name"></span>
                                        <div class="progress">
                                            <div class="progress-bar progress-third-success" role="progressbar"
                                                 aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: 100%;">
                                            </div>
                                        </div>
                                    </div>
                                    <span class="content_right_data_span2"></span>
                                </li>
                                <li class="content_right_data_li">
                                    <span class="content_right_data_span1">4</span>
                                    <div class="processor_right">
                                        <span class="span_device_name"></span>
                                        <div class="progress">
                                            <div class="progress-bar progress-four-success" role="progressbar"
                                                 aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: 100%;">
                                            </div>
                                        </div>
                                    </div>
                                    <span class="content_right_data_span2"></span>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div class="content_right_data2">
                        <div class="content_rigth_title_left">
                            <span class="content_right_title_top">内存</span>
                        </div>
                        <div class="content_rigth_title_right">
                            <ul>
                                <li class="content_right_data_li">
                                    <span class="content_right_data_span1">1</span>
                                    <div class="processor_right">
                                        <span class="span_device_name"></span>
                                        <div class="progress">
                                            <div class="progress-bar progress-first-success" role="progressbar"
                                                 aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: 100%;">
                                            </div>
                                        </div>
                                    </div>
                                    <span class="content_right_data_span2"></span>
                                </li>
                                <li class="content_right_data_li">
                                    <span class="content_right_data_span1">2</span>
                                    <div class="processor_right">
                                        <span class="span_device_name"></span>
                                        <div class="progress">
                                            <div class="progress-bar progress-second-success" role="progressbar"
                                                 aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: 100%;">
                                            </div>
                                        </div>
                                    </div>
                                    <span class="content_right_data_span2"></span>
                                </li>
                                <li class="content_right_data_li">
                                    <span class="content_right_data_span1">3</span>
                                    <div class="processor_right">
                                        <span class="span_device_name"></span>
                                        <div class="progress">
                                            <div class="progress-bar progress-third-success" role="progressbar"
                                                 aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: 100%;">
                                            </div>
                                        </div>
                                    </div>
                                    <span class="content_right_data_span2"></span>
                                </li>
                                <li class="content_right_data_li">
                                    <span class="content_right_data_span1">4</span>
                                    <div class="processor_right">
                                        <span class="span_device_name"></span>
                                        <div class="progress">
                                            <div class="progress-bar progress-four-success" role="progressbar"
                                                 aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: 100%;">
                                            </div>
                                        </div>
                                    </div>
                                    <span class="content_right_data_span2"></span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="content_right_bototm">
                    <div class="content_title">
                        <p class="content_title_icon"></p>
                        <span style="color:#ffffff">任务告警</span>
                    </div>
                    <div class="content_right_title">
                        <p class="content_right_title1"  style="color:#ffffff">任务</p>
                        <p class="content_right_title2" style="color:#ffffff">告警信息</p>
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
<script type="text/javascript" src="<%=basePath %>static/assets/js/toastr/toastr.min.js"></script>

<script type="text/javascript" src="<%=basePath %>static/js/chart2.8/moment.2.24.0.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/chart2.8/Chart.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/chart2.8/chartjs-plugin-streaming.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/chart2.8/utils.js"></script>

<script type="text/javascript" src="<%=basePath %>static/js/echart/echarts.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/echart/echarts.common.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/echart/dataTool.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/echart/echarts.simple.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/echart/bmap.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/echart/china.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/echart/world.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/echart/ecStat.min.js"></script>

<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/cytoscape.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/cytoscape-panzoom.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/popper.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/index.all.min.bak.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/cytoscape-popper.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/copsec/system/deviceCMS.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/numberFormat/format-number.js"></script>


</body>
</html>
