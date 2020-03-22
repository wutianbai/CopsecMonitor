<%@page pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme() + "://"
		+ request.getServerName() + ":" + request.getServerPort()
		+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>${system.title} 错误页面-403</title>
        <link rel="stylesheet" type="text/css" href="<%=basePath %>css/bluedream.css">
        <link rel="shortcut icon" type="image/x-icon" href="<%=basePath %>image/favicon.ico" />
        <style>
        	 a:hover{
        	 	background-color:white;
        	 	color:#3c6e31;
        	 }
        	 a {color:#008AE0;text-decoration: underline;}
        	 .errorSpan{
			     font-size:30px;
				 color:#008AE0;
				-webkit-text-stroke: 1px #008AE0;
				letter-spacing: 0.04em;
			}
        </style>
        <script language="javascript" type="text/javascript">
            var timer;
            //启动跳转的定时器
            function startTimes() {
                timer = window.setInterval(showSeconds,1000);
            }

            var i = 5;
            function showSeconds() {
                if (i > 0) {
                    i--;
                    document.getElementById("seconds").innerHTML = i;
                }
                else {
                    window.clearInterval(timer);
                    //location.href = "login.html";
                    window.history.back();
                }
            }

            //取消跳转
            function resetTimer() {
                if (timer != null && typeof timer != 'undefined') {
                    window.clearInterval(timer);
                    window.history.back();
                }
            }
        </script> 
        
    </head>
    <body class="error_page" onload="startTimes();" style="text-align: center;">
    	<div style="margin-top: 200px;"><img src="<%=basePath %>image/error.png"/>
    		<span class="errorSpan">400 Bad Request</span>
    	</div>
    	<br/>
    	<br/>
    	<br/>
	       <span style="font-size: 20px;font-weight: bold;">${message }&nbsp;<span id="seconds" style="font-size: 20px;font-weight: bold;">5</span>&nbsp;秒后将自动跳转，立即跳转请点击&nbsp;
           <a  class="aa"  href="javascript:resetTimer();" ><span style="font-size: 16px;font-weight: bold;">返回</span></a></span>
    </body>
</html>