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
    <title>${system.title}</title>

    <meta name="viewport"
          content="user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, minimal-ui">
    <meta name="description" content="CopSec Monitor System"/>
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
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script type="text/javascript" src="<%=basePath %>static/js/html5/html5shiv.min.js"></script>
    <script type="text/javascript" src="<%=basePath %>static/js/html5/respond.min.js"></script>
    <![endif]-->
    <script src="<%=basePath %>static/js/copsec/jquery.idcode.js"></script>
    <script type="text/javascript">
        $(function () {
            $.idcode.setCode();
            let top = getTopWindow(); //获取当前页面的顶层窗口对象
            if (top !== window) {
                top.location.href = '<%=basePath %>/login'; //跳转到登录页面
            }
        });
        function getTopWindow() {
            let p = window;
            if (p !== p.parent) {
                p = p.parent;
            }
            return p;
        }

        document.onkeydown = keyListener;
        function keyListener(e) {
            e = e ? e : event;
            if (e.keyCode === 13) {
                //兼容火狐和IE的，回车按键，表单提交进行验证
                check_login();
            }
        }

        function check_login() {
            $("#login_msg").html("");
            let userName = $.trim($("#userName").val());
            let password = $.trim($("#password").val());
            let validateJson = $.idcode.validateCode();

            if (userName === "" || password === "") {
                $("#login_msg").html("用户名或密码不能为空");
                toastr.error("用户名或密码不能为空");
                return;
            } else if (!validateJson) {
                $("#login_msg").html("验证码错误");
                toastr.error("验证码错误");
                return;
            } else {
                show_loading_bar({
                    delay: .5,
                    pct: 90,
                    finish: function () {
                        $("#inputCode").val(validateJson);
                        $("#loginForm").submit();
                    }
                });
            }
        }

        function resetForm() {
            $("#login_msg").html("");
            $("#userName").val("");
            $("#password").val("");
        }
    </script>
    <style>
        .form-horizontal {
            background: #fff;
            padding-bottom: 40px;
            border-radius: 10px;
            text-align: center;
        }

        .form-horizontal .heading {
            display: block;
            font-size: 26px;
            padding: 35px 0;
            border-bottom: 1px solid #f0f0f0;
            margin-bottom: 20px;
            color: #000000;
        }

        .form-horizontal .form-group {
            padding: 0 40px;
            margin: 0 0 25px 0;
            position: relative;
        }

        .form-horizontal .form-control {
            background: #f0f0f0;
            border: none;
            border-radius: 0px;
            box-shadow: none;
            padding: 0 20px 0 45px;
            height: 40px;
            transition: all 0.3s ease 0s;
        }

        .form-horizontal .form-control:focus {
            background: #e0e0e0;
            box-shadow: none;
            outline: 0 none;
        }

        .form-horizontal .form-group i {
            position: absolute;
            top: 12px;
            left: 60px;
            font-size: 17px;
            color: #c8c8c8;
            transition: all 0.5s ease 0s;
        }

        .form-horizontal .form-control:focus + i {
            color: #14173e;
        }

        .form-horizontal .fa-question-circle {
            display: inline-block;
            position: absolute;
            top: 12px;
            right: 60px;
            font-size: 20px;
            color: #808080;
            transition: all 0.5s ease 0s;
        }

        .form-horizontal .fa-question-circle:hover {
            color: #000;
        }

        .form-horizontal .main-checkbox {
            float: left;
            width: 20px;
            height: 20px;
            background: #68b828;
            border-radius: 50%;
            position: relative;
            margin: 5px 0 0 5px;
            border: 1px solid #68b828;
        }

        .form-horizontal .main-checkbox label {
            width: 20px;
            height: 20px;
            position: absolute;
            top: 0;
            left: 0;
            cursor: pointer;
        }

        .form-horizontal .main-checkbox label:after {
            content: "";
            width: 10px;
            height: 5px;
            position: absolute;
            top: 5px;
            left: 4px;
            border: 3px solid #fff;
            border-top: none;
            border-right: none;
            background: transparent;
            opacity: 0;
            -webkit-transform: rotate(-45deg);
            transform: rotate(-45deg);
        }

        .form-horizontal .main-checkbox input[type=checkbox] {
            visibility: hidden;
        }

        .form-horizontal .main-checkbox input[type=checkbox]:checked + label:after {
            opacity: 1;
        }

        .form-horizontal .text {
            float: left;
            margin-left: 7px;
            line-height: 20px;
            padding-top: 5px;
            text-transform: capitalize;
        }

        .form-horizontal .btn {
            display: inline-block;
            margin-bottom: 0;
            font-weight: normal;
            text-align: center;
            vertical-align: middle;
            cursor: pointer;
            background-image: none;
            border: 1px solid transparent;
            white-space: nowrap;
            padding: 6px 12px;
            font-size: 15px;
            line-height: 1.42857143;
            border-radius: 0px;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
            float: right;
            background: #009886;
            color: #fff;
        }

        @media only screen and (max-width: 479px) {
            .form-horizontal .form-group {
                padding: 0 25px;
            }

            .form-horizontal .form-group i {
                left: 45px;
            }

            .form-horizontal .btn {
                padding: 10px 20px;
            }
        }
    </style>
</head>

<body class="page-body login-page">

<input type="hidden" id="inputCode" name="inputCode" value=""/>
<div class="login-container">
    <div class="row">
        <div class="col-md-offset-3 col-md-6">
            <form class="form-horizontal" method="post" id="loginForm" action="<%=basePath%>login">
                <div class="login-header" style="text-align: center">
                    <span class="heading">${system.title}</span>
                </div>

                <div class="form-group">
                    <input class="form-control" id="userName" name="id" placeholder="用户名">
                    <i class="fa fa-user"></i>
                </div>
                <div class="form-group help">
                    <input type="password" class="form-control" id="password" name="password" placeholder="密　码">
                    <i class="fa fa-lock"></i>
                    <a href="#" class="fa fa-question-circle"></a>
                </div>
                <div class="form-group">
                    <span id="idcode"></span>
                </div>
                <div class="form-group">
                    <span id="login_msg"
                          style="color: red; font-size: 12px; font-weight: bold; text-align: left;">${message}</span>
                    <input type="button" class="btn btn-turquoise" onclick="resetForm()" value="重置"/>
                    <input type="button" class="btn btn-turquoise" onclick="check_login()" value="登录"/>
                </div>
            </form>
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
<script type="text/javascript" src="<%=basePath %>static/assets/js/typeahead.bundle.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/handlebars.min.js"></script>
<script type="text/javascript" src="<%=basePath %>static/assets/js/xenon-custom.js"></script>

</body>
</html>
