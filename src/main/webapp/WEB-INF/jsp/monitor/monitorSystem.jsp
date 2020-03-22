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
<script type="text/javascript" src="<%=basePath %>static/js/jquery-1.9.1.js"></script>

</head>

<body class="page-body">
<div class="navbar horizontal-menu navbar-fixed-top">

</div>

<div class="page-container">

    <div class="main-content">

        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">CPU</h3>
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
                        <div class="col-sm-2">
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-4">
                            <label></label><br/>
                            <label></label><br/>
                            <label></label>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-6">
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel panel-color panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">磁盘</h3>
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
                            <div class="col-sm-2">
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <label ></label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <label ></label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <label ></label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <button class="btn btn-turquoise">详情</button>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row" style="display:none">
                    <div role="form">
                        <div class="form-group">
                            <div class="col-sm-2">
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-8">
                                <table class="table table-model-2 table-bordered table-striped">
                                    <thead>
                                        <tr>
                                            <th>分区</th>
                                            <th>容量</th>
                                            <th>已用大小</th>
                                            <th>已用百分比</th>
                                        </tr>
                                        <tbody></tbody>
                                    </thead>
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
                <h3 class="panel-title">内存</h3>
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
                        <div class="col-sm-2">
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-2">
                            <label></label>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-2">
                            <label></label>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-2">
                            <label></label>
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
                <h3 class="panel-title">网络</h3>
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
                            <div class="col-sm-2">
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-3">
                                <label></label><br/>
                                <label></label>
                            </div>
                        </div>
                       <%-- <div class="form-group">
                            <div class="col-sm-2">
                            </div>
                        </div>--%>
                        <div class="form-group">
                            <div class="col-sm-3">
                                <label></label><br/>
                                <label></label>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-2">
                                <button class="btn btn-turquoise">详情</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row" style="display: none">
                    <div role="form">
                        <div class="form-group">
                            <div class="col-sm-2"></div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-8">
                               <table class="table table-model-2 table-bordered table-striped">
                                   <thead>
                                        <tr>
                                            <td>网卡</td>
                                            <td>连接状态</td>
                                            <td>接收总量</td>
                                            <td>发送总量</td>
                                            <td>当前接收速率</td>
                                            <td>当前发送速率</td>
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
<script type="text/javascript" src="<%=basePath %>static/js/copsec/monitor/monitorSystem.js"></script>

</body>
</html>
