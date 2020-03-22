jQuery(function(){

    var $body = $('body');


    var $retryPanel = $($('.panel')[0]);
    var $lockPanel = $($('.panel')[1]);
    var $ipPanel = $($('.panel')[2]);
    var $table = $ipPanel.find("table tbody");
    $.when($.ajax(contextPath+"system/get")).done(function(data){

        console.log(data);
        if (data.data.LOGINLOCKTIME) {

            $lockPanel.find("input[type=text]").val(data.data.LOGINLOCKTIME);
        }else{

            $lockPanel.find("input[type=text]").val(1);
        }
        if (data.data.LOGINTRYTIME) {

            $retryPanel.find("input[type=text]").val(data.data.LOGINTRYTIME);

        }else{
            $retryPanel.find("input[type=text]").val(5);

        }
        $table.children().remove();
        if (data.data.ALLOWEDIP) {

            var tmp = new String(data.data.ALLOWEDIP);
            var sub = tmp.substring(1,tmp.length-1).split(",");

            for (var i =0;i<sub.length ;i++){

                var ips = $.trim(sub[i]).split("<;ws;>");
                addBtb(ips[0],ips[1]);
            }

        }
    });

    var $retryBtn = $($body.find('button')[0]);
    $retryBtn.on('click',function(){


        if(!$retryPanel){

            return false;
        }
        var status = $retryPanel.find("input[type=text]").val();
        if($.isNumeric(status)){

            $.ajax({

                url:contextPath+'system/retry/set',
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
        }else{

            toastr.error("不能输入除数字外其他字符");
        }
    });

    var $lockBtn = $($body.find('button')[1]);
    $lockBtn.on('click',function(){

        if(!$lockPanel){

            return false;
        }

        var status = $lockPanel.find("input[type=text]").val();
        if($.isNumeric(status)){

            $.ajax({

                url:contextPath+'system//lock/set',
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
        }else{

            toastr.error("不能输入除数字外其他字符");
        }
    });

    /**
     * 添加
     */
    var $addBtn = $($body.find('button')[2]);
    $addBtn.on('click',function(){


        if(!$ipPanel){

            return false;
        }
        var ip = $ipPanel.find("input[type=text]").val();
        var regStr = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/;
        if(!ip || !ip.match(regStr)){

            toastr.error("IP格式不正确，请填写正确的IP格式！");
            return false;
        }

        $.ajax({

            url:contextPath+'system//ip/add',
            method:'POST',
            data:{'ip':ip},
            dataType:'json',
            success:function(data,st,axq)
            {
                if(data.code == 200){

                    toastr.info(data.message);
                    addBtb(data.data.id,ip);

                }
            }
        });
    });

    function addBtb(id,ip){

        var $tr = $("<tr><td style='text-align:center;'>"+ip+"</td><td style='text-align:center;'><button class='btn btn-danger btn-single btn-icon btn-icon-standalone'><i class='fa-remove'></i><span>删除</span></button></td></tr>");
        $tr.on('click','td button',function(){

            $.ajax({

                url:contextPath+'system/ip/delete',
                method:'POST',
                data:{'id':id},
                dataType:'json',
                success:function(result){

                    if(result.code == 200){

                        toastr.info(result.message);
                        $tr.remove();
                    }
                }
            });
        });
        $table.append($tr);
    }

})