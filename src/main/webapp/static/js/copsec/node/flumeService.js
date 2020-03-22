jQuery(function(){

    var $body = $('body');

    var $transferService = $($('.panel')[0]);
    var $listenService = $($('.panel')[1]);
    var $deviceInfo = $($(".panel")[2]);

    $.when($.ajax(contextPath + "system/get")).done(function(data){

        var s = "<;ws;>";
        if(data.data.TRANSFER){

            var strs = data.data.TRANSFER.split(s);
            $transferService.find('input[type=radio]').each(function (e,i){

                if ($(this).val() == strs[0]) {

                    $(this).attr('checked','checked');
                    $(this).parent().parent().addClass("cbr-checked");
                }else {

                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });

            $transferService.find("input[type=text]").first().val(strs[1]);
            $transferService.find("input[type=text]").last().val(strs[2]);
        }else{

            $transferService.find('input[type=radio]').each(function (e,i){

                if ($(this).val() == "forbidden") {

                    $(this).attr('checked','checked');
                    $(this).parent().parent().addClass("cbr-checked");
                }else {

                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });
        }
        if(data.data.LISTENER)
        {
            var strs = data.data.LISTENER.split(s);
            $listenService.find('input[type=radio]').each(function (e,i){

                if ($(this).val() == strs[0]) {

                    $(this).attr('checked','checked');
                    $(this).parent().parent().addClass("cbr-checked");
                }else {

                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });

            $listenService.find("input[type=text]").first().val(strs[1]);
            $listenService.find("input[type=text]").last().val(strs[2]);
        }else{

            $listenService.find('input[type=radio]').each(function (e,i){

                if ($(this).val() == "forbidden") {

                    $(this).attr('checked','checked');
                    $(this).parent().parent().addClass("cbr-checked");
                }else {

                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });

        }

        if(data.data.DEVICEINFO)
        {
            var strs = data.data.DEVICEINFO.split(s);
            $deviceInfo.find('input[type=radio]').each(function (e,i){

                if ($(this).val() == strs[0]) {

                    $(this).attr('checked','checked');
                    $(this).parent().parent().addClass("cbr-checked");
                }else {

                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });

            $deviceInfo.find("input[type=text]").first().val(strs[1]);
            $deviceInfo.find("input[type=text]").last().val(strs[2]);
        }else{

            $deviceInfo.find('input[type=radio]').each(function (e,i){

                if ($(this).val() == "forbidden") {

                    $(this).attr('checked','checked');
                    $(this).parent().parent().addClass("cbr-checked");
                }else {

                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });
        }

    });

    var $transferBtn = $transferService.find("button");
    $transferBtn.on('click',function(){

        var transferIp = $transferService.find("input[type=text]").first().val(),
            transferPort = $transferService.find("input[type=text]").last().val(),
            status = $transferService.find('input[type=radio]:checked').val();

        if(transferIp && transferPort && status){

            var regStr = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/;
            if (!transferIp.match(regStr)){

                toastr.error("IP格式不正确，请填写正确的IP格式!");
                return false;
            }
            if (!$.isNumeric(transferPort) && (0 >= transferPort || transferPort > 65535)){

                toastr.error("监听端口不正确!");
                return false;
            }
            $.ajax({

                url:contextPath+'system/inspector/set',
                method:'POST',
                data:{'status':status,'ip':transferIp,'port':transferPort,'type':'t'},
                success:function(data){

                    if(data.code == 200){

                        toastr.info(data.message);
                    }else{

                        toastr.error(data.message);
                    }
                },
                error:function(jqx,status,error){

                    toastr.error(status);
                }
            });
        }else{

           toastr.error("请填写配置项");
           return false;
        }
    });

    var $listenerBtn = $listenService.find("button");
    $listenerBtn.on("click",function(){

        var transferIp = $listenService.find("input[type=text]").first().val(),
            transferPort = $listenService.find("input[type=text]").last().val(),
            status = $listenService.find('input[type=radio]:checked').val();

        if(transferIp && transferPort && status){

            var regStr = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/;
            if (!transferIp.match(regStr)){

                toastr.error("IP格式不正确，请填写正确的IP格式!");
                return false;
            }
            if (!$.isNumeric(transferPort) && (0 >= transferPort || transferPort > 65535)){

                toastr.error("监听端口不正确!");
                return false;
            }
            $.ajax({

                url:contextPath+'system/inspector/set',
                method:'POST',
                data:{'status':status,'ip':transferIp,'port':transferPort,'type':'l'},
                success:function(data){

                    if(data.code == 200){

                        toastr.info(data.message);
                    }else{

                        toastr.error(data.message);
                    }
                },
                error:function(jqx,status,error){

                    toastr.error(status);
                }
            });
        }else{

            toastr.error("请填写配置项");
            return false;
        }
    });

    var $deviceServiceBtn = $deviceInfo.find("button");
    $deviceServiceBtn.on("click",function(){

        var transferIp = $deviceInfo.find("input[type=text]").first().val(),
            transferPort = $deviceInfo.find("input[type=text]").last().val(),
            status = $deviceInfo.find('input[type=radio]:checked').val();

        if(transferIp && transferPort && status){

            var regStr = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/;
            if (!transferIp.match(regStr)){

                toastr.error("IP格式不正确，请填写正确的IP格式!");
                return false;
            }
            if (!$.isNumeric(transferPort) && (0 >= transferPort || transferPort > 65535)){

                toastr.error("监听端口不正确!");
                return false;
            }
            $.ajax({

                url:contextPath+'system/inspector/set',
                method:'POST',
                data:{'status':status,'ip':transferIp,'port':transferPort,'type':'d'},
                success:function(data){

                    if(data.code == 200){

                        toastr.info(data.message);
                    }else{

                        toastr.error(data.message);
                    }
                },
                error:function(jqx,status,error){

                    toastr.error(status);
                }
            });
        }else{

            toastr.error("请填写配置项");
            return false;
        }
    });

    function isAgentId(str){

        var reg = /[a-zA-Z0-9]+/;
        return reg.test(str);
    }

    function isStr(str){

        var reg = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
        return reg.test(str);
    }
});