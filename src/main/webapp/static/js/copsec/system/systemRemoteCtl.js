jQuery(function(){

    $.when($.ajax(contextPath + "system/remote/get/all")).done(function(data){

        if(data.code ==200){

            $.each(data.data,function(index,e){

                addRemoteDevice(e);
            });
        }
    });

    $("body").on("click","li",function(){

        if($(this).hasClass("sub"))
        {
            return false;
        }
        if($(this).hasClass("has-sub") && !$(this).hasClass("active")){

            $(this).addClass("opened active expanded");
        }else{

            $(this).removeClass("opened active expanded");
        }
    });
    $("#main-menu").on("click","a",function(){

        var path = $(this).attr("name");
        if(path){
            $("iframe[name=mainFrame]").attr("src",contextPath + "kebo/path?name="+path);
        }
    });

    var $select = $("body").find("header.logo-env").find("select");
    $.each($select.children(),function(index,e){

        if($(e).val() != "_defaults"){

            $(e).remove();
        }
    });
    function addRemoteDevice(device){
       var option = $("<option value='"+device.deviceId+"'>"+device.deviceName+"</option>");
       $select.append(option);
    }

    /**
     * 当服务器变更时，查询登陆是否存在登陆信息，如果不存在，则提示用户登录，存在则继续操作；
     */
    $select.change(function(){

        var deviceId = $select.find("option:selected").val();
        if(deviceId == "_defaults"){

            return false;
        }
        $.ajax({
           url:contextPath + "system/remote/session/"+deviceId,
           dataType:"json",
           success:function(data){

                if(data.code ==200 && data.data){

                    needLogin();
                }else{

                    $("#main-menu").load(contextPath + "kebo/path?name="+data.message);
                }
           },
           error:function(jqx,status,error){

                toastr.error("请求错误或超时");
           }
        });
    });

    /**
     * 弹出登录框，执行登陆程序
     */
    var modal = $("body").find("div.modal");
    function needLogin(){

        modal.modal('show', {backdrop: 'static'});
    }

    var loginBtn = modal.find("button.btn-turquoise");
    loginBtn.click(function(){

        var deviceId = $select.find("option:selected").val();
        var methodType = "login";
        if(deviceId == "_defaults"){

            return false;
        }
        var userId = modal.find("input[type=text]").first().val();
        var pwd = modal.find("input[type=text]").last().val();

        if(userId =="" || pwd == ""){

            toastr.error("请输入用户名或密码");
            return false;
        }
        $("#main-menu").load(contextPath + "kebo/path?name=menu");
        return false;

        $.ajax({
            url:contextPath + "system/remote/login",
            data:{"userId":userId,"password":pwd,"deviceId":deviceId,"methodType":methodType},
            dataType:"json",
            method:"POST",
            success:function(data){

                console.log(data);
                if(data.code == 200){

                    modal.modal('hide');
                    /**
                     * 获取菜单操作 ,login返回值值对应菜单连接
                     *  loadMenu(连接)
                     */
                    $("#main-menu").load(contextPath + "kebo/path?name="+data.message);
                }else{

                    toastr.error(data.message);
                }
            },
            error:function(jqx,status,error){

                toastr.error("请求错误或超时");
            }
        });
    });


})