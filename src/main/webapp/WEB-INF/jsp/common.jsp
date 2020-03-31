<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<title>${system.title}</title>
<meta charset=utf-8/>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta http-equiv="X-UA-Compatible" content="IE=9">
<meta name="viewport"
      content="user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, minimal-ui">
<meta name="description" content="CopSec Monitor System"/>
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

<link rel="stylesheet" href="<%=basePath %>static/assets/css/fonts/elusive/css/elusive.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/datatables/dataTables.bootstrap.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/daterangepicker/daterangepicker-bs3.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/select2/select2.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/select2/select2-bootstrap.css">
<link rel="stylesheet" href="<%=basePath %>static/assets/js/multiselect/css/multi-select.css">
<link rel="stylesheet" href="<%=basePath %>static/js/cytoscape/cytoscape.js-panzoom.css">
<script type="text/javascript" src="<%=basePath %>static/js/es5/es5-shim.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/es5/es5-sham.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/es5/es6-shim.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/es5/es6-sham.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/jquery-1.9.1.js"></script>


<!-- Bottom Scripts -->
<script type="text/javascript" src="<%=basePath %>static/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/TweenMax.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/resizeable.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/joinable.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-api.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-toggles.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/moment.min.js"></script>

<script type="text/javascript" src="<%=basePath %>static/assets/js/daterangepicker/daterangepicker.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/datepicker/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/timepicker/bootstrap-timepicker.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/colorpicker/bootstrap-colorpicker.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/select2/select2.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/jquery-ui/jquery-ui.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/selectboxit/jquery.selectBoxIt.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/tagsinput/bootstrap-tagsinput.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/typeahead.bundle.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/handlebars.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/multiselect/js/jquery.multi-select.js"></script>
<!-- JavaScripts initializations and stuff -->
<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-custom.js"></script>


<script type="text/javascript" src="<%=basePath %>static/assets/js/datatables/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/datatables/dataTables.bootstrap.js"></script>
<script type="text/javascript"
        src="<%=basePath %>static/assets/js/datatables/yadcf/jquery.dataTables.yadcf.js"></script>
<script type="text/javascript"
        src="<%=basePath %>static/assets/js/datatables/tabletools/dataTables.tableTools.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/jquery-validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/inputmask/jquery.inputmask.bundle.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/formwizard/jquery.bootstrap.wizard.min.js"></script>

<script type="text/javascript" src="<%=basePath %>static/assets/js/toastr/toastr.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/dropzone/dropzone.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/cytoscape.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/cytoscape-panzoom.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/copsec/common.js"></script>

<script type="text/javascript" src="<%=basePath %>static/js/fileDownload1.4.6/jquery.fileDownload.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/popper.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/index.all.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/js/cytoscape/cytoscape-popper.js"></script>

<script type="text/javascript" src="<%=basePath %>static/js/tools/Math.uuid.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/tocify/jquery.tocify.min.js"></script>

<div class="modal fade" id="message">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">系统提示</h4>
            </div>

            <div class="modal-body">
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade custom-width" id="showTable">
    <div class="modal-dialog" style="width: 70%">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">信息</h4>
            </div>

            <div class="modal-body">
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>