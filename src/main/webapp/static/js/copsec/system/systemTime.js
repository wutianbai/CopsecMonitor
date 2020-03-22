jQuery(function(){

    var $body = $('body');

    var $serverPanel = $($('.panel')[0]);
    var $networkTimingPanel = $($('.panel')[1]);
    var $localPanel = $($('.panel')[2]);
    $.when($.ajax(contextPath+"system/get")).done(function(data){

        if (data.data.SYSTEMTIME) {

            $serverPanel.find('p').first().html(data.data.SYSTEMTIME);
        }
        if (data.data.NETWORKTIMESERVICE){


            var tmp = $.parseJSON(data.data.NETWORKTIMESERVICE);
            $networkTimingPanel.find('input[type=radio]').each(function (e,i){

                if ($(this).val() == tmp.status) {

                    $(this).attr('checked','checked');
                    $(this).parent().parent().addClass("cbr-checked");
                }else {

                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });
            $networkTimingPanel.find('input[type=text]').first().val((tmp.ip=='null'?'':tmp.ip));
            $networkTimingPanel.find('input[type=text]').last().val((tmp.frequency=='null'?'':tmp.frequency));
        }else{

            $networkTimingPanel.find('input[type=radio]').each(function (e,i){

                if ($(this).val() == "forbidden") {

                    $(this).attr('checked','checked');
                    $(this).parent().parent().addClass("cbr-checked");
                }else {

                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });
        }
        if (data.data.LOCALTIMING) {

            $localPanel.find('input[type=radio]').each(function (e,i) {

                if ($(this).val() == data.data.LOCALTIMING) {

                    $(this).attr('checked','checked');
                    $(this).parent().parent().addClass("cbr-checked");
                }else {

                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });
        }else{

            $localPanel.find('input[type=radio]').each(function (e,i) {

                if ($(this).val() == "forbidden") {

                    $(this).attr('checked','checked');
                    $(this).parent().parent().addClass("cbr-checked");
                }else {

                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });
        }

    });

    var $serverBtn = $($body.find('button')[0]);
    $serverBtn.on('click',function(){

        var time =  y.toString() + m.toString() + d.toString() + " "+ h.toString() + ":" + min.toString() + ":" + s.toString();
        var t = y + "年" + m + "月" + d + "日" + h + "时" + min + "分" + s + "秒";
        $.ajax({

            url:contextPath+'system/timing/server',
            method:'POST',
            data:{'currentTime':time},
            dataType:'json',
            success:function(data,st,axq){

                if(data.code == 200){

                    toastr.info(data.message);
                    $serverPanel.find("p").first().html(t);
                }else{

                    toastr.error(data.data);
                }
            }
        });
    });

    var $networkBtn = $($body.find('button')[1]);
    $networkBtn.on('click',function(){
        if(!$networkTimingPanel){

            return false;
        }
        var ip = $networkTimingPanel.find('input[type=text]').first().val(),
            frequency = $networkTimingPanel.find('input[type=text]').last().val(),
            status = $networkTimingPanel.find('input[type=radio]:checked').val();
        if(status && ip && frequency){

            var regStr = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/;
            if (!ip.match(regStr)){

                toastr.error("IP格式不正确，请填写正确的IP格式！");
                return false;
            }
            if (!$.isNumeric(frequency)){

                toastr.error("不能输入除数字外其他字符");
                return false;
            }

            $.ajax({

                url:contextPath+'system/timing/network',
                method:'POST',
                data:{'status':status,'frequency':frequency,'ip':ip},
                dataType:'json',
                success:function(data,st,axq){

                    if(data.code == 200){

                        toastr.info(data.message);
                    }else{

                        toastr.error(data.message);
                    }
                }
            });
        }else{

            toastr.error("请填写配置项");
            return false;
        }
    });


    var $localBtn = $($body.find('button')[2]);
    $localBtn.on('click',function(){

        var status = $localPanel.find("input[type=radio]:checked").val();
        if (status){

            $.ajax({

                url:contextPath+'system/timing/local',
                data:{'status':status},
                dataType:'json',
                method:'POST',
                success:function (data,sts,axqh) {

                    if (data.code == 200){

                        toastr.info(data.message);
                    } else{

                        toastr.error(data.message);
                    }
                }
            });
        }
    });

    var y,m,d,h,min,s;
    function time () {

        clearTimeout(t);
        var dt = new Date();
        y = dt.getFullYear();
        m = (dt.getMonth() + 1) < 10 ? ("0" + (dt.getMonth() + 1)) : dt.getMonth() + 1;
        d = dt.getDate() < 10 ? ("0" + dt.getDate()) : dt.getDate();
        h = dt.getHours() < 10 ? ("0" + dt.getHours()) : dt.getHours();
        min = dt.getMinutes() < 10 ? ("0" + dt.getMinutes()) : dt.getMinutes();
        s = dt.getSeconds() < 10 ? ("0" + dt.getSeconds()) : dt.getSeconds();
        $serverPanel.find('p').last().html(y + "年" + m + "月" + d + "日" + h + "时" + min + "分" + s + "秒");
    }
    var t = setTimeout(time(), 1);
})
