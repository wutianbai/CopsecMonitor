jQuery(function(){

    var $body = $('body');

    $.when($.ajax(contextPath+"system/get")).done(function(data){

        var $sshPanel = $($('.panel')[0]);
        var $snmpPanle = $($('.panel')[1]);
        if(data.code == 200){

            $sshPanel.find("input[type=radio]").each(function(e,i){

                if(data.data.SSH){

                    if($(this).val() == data.data.SSH){

                        $(this).attr('checked','checked');
                        $(this).parent().parent().addClass("cbr-checked");
                    }else{

                        $(this).parent().parent().removeClass("cbr-checked");
                    }
                }

            });

            $snmpPanle.find("input[type=radio]").each(function(e,i){

                if(data.data.SNMP){

                    if($(this).val() == data.data.SNMP){

                        $(this).attr('checked','checked');
                        $(this).parent().parent().addClass("cbr-checked");
                    }else{

                        $(this).parent().parent().removeClass("cbr-checked");
                    }
                }
            });
        }
    });

    var $sshBtn = $($body.find('button')[0]);
    $sshBtn.on('click',function(){

        var $panel = $($('.panel')[0]);
        if(!$panel){

            return false;
        }
        var status = $panel.find("input[type=radio]:checked").val();
        if(status){

            $.ajax({

                url:contextPath+'system/ssh/set',
                method:'POST',
                data:{'status':status},
                dataType:'json',
                success:function(data,st,axq){

                    if(data.code == 200){

                        toastr.info(data.message);
                    }else{

                        toastr.error(data.message);
                    }
                }
            });
        }
    });

    var $snmpBtn = $($body.find('button')[1]);
    $snmpBtn.on('click',function(){
        var $panel = $($('.panel')[1]);
        if(!$panel){

            return false;
        }

        var status = $panel.find("input[type=radio]:checked").val();
        if(status){

            $.ajax({

                url:contextPath+'system/snmp/set',
                method:'POST',
                data:{'status':status},
                dataType:'json',
                success:function(data,st,axq){

                    if(data.code == 200){

                        toastr.info(data.message);
                    }else{

                        toastr.error(data.message);
                    }
                }
            });
        }
    });

    /**
     * 关机
     */
    var $shutdownBtn = $($body.find('button')[2]);
    $shutdownBtn.on('click',function(){

        toastr.info("命令已发送请稍等");
        $.ajax({

            url:contextPath+'system/cmd/shutdown',
            method:'POST',
            dataType:'json',
            success:function(data,st,axq)
            {
                if(data.code == 200){

                    toastr.info(data.message);
                }else{

                    toastr.error(data.message);
                }
            }
        });
    });
    var $restartBtn = $($body.find('button')[3]);
    $restartBtn.on('click',function(){

        toastr.info("命令已发送请稍等");
        $.ajax({

            url:contextPath+'system/cmd/restart',
            method:'POST',
            dataType:'json',
            success:function(data,st,axq)
            {
                if(data.code == 200){

                    toastr.info(data.message);
                }else{

                    toastr.error(data.message);
                }
            }
        });
    });
})