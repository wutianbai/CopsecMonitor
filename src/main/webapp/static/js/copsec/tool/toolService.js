jQuery(function(){

    var $body = $('body');

    var $panel = $body.find('.panel');

    $body.on('click','button',function(){
        var ip = $body.find('input[type=text]').val();
        if(ip.indexOf(":") == -1){

            toastr.error("请输入IP:PORT 格式信息");
            return false;
        }else if(ip.split(":").length != 2){

            toastr.error("请输入IP:PORT 格式信息");
            return false;
        }else if(isIp4(ip.split(":")[0])){

            $panel.find('textarea').text("");
            toastr.info("命令已发送，请稍等!");
            $panel.find('textarea').text("");
            if($.isNumeric(ip.split(":")[1]) && 0 < $.isNumeric(ip.split(":")[1]) < 65535){

                $.ajax({
                    url:contextPath+'tool/service/4',
                    data:{'ip':ip.split(":")[0],'port':ip.split(":")[1]},
                    dataType:'json',
                    method:'POST',
                    success:function(data){

                        if(data.code == 200){

                            $panel.find('textarea').text(data.message);
                        }else{

                            toastr.error(data.message+data.data);
                        }
                    } ,
                    error:function(jqx,status,error){

                        toastr.error(error);
                    }
                });
            }else{

                toastr.error("无效端口");
                return false;
            }

        }else if(isIp6(ip)){

            toastr.info("命令已发送，请稍等!");
            if($.isNumeric(ip.split(":")[1]) && 0 < $.isNumeric(ip.split(":")[1]) < 65535){

                $.ajax({
                    url:contextPath+'tool/service/6',
                    data:{'ip':ip.split(":")[0],'port':ip.split(":")[1]},
                    dataType:'json',
                    method:'POST',
                    success:function(data){

                        if(data.code == 200){

                            $panel.find('textarea').text(data.message);
                        }else{

                            toastr.error(data.message+data.data);
                        }
                    } ,
                    error:function(jqx,status,error){

                        toastr.error(error);
                    }
                });
            }else{

                toastr.error("无效端口");
                return false;
            }

        }else{

            toastr.error("请输入有效IP地址");
            return false;
        }
    });

    function isIp4(ip){

        var regStr = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/;
        if(!ip){

            return false;
        }else if($.trim(ip) == "0.0.0.0"){

            return false;
        }else{

            return ip.match(regStr);
        }
    }

    function isIp6(ip){

        var regStr = /^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/;
        if(!ip){

            return false;
        }else if($.trim(ip) == "0.0.0.0"){

            return false;
        }else{

            return ip.match(regStr);
        }
    }





})
