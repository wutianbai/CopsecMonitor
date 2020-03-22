jQuery(function(){

    var $transferService = $($('.panel')[0]);
    var $panel2 = $($('.panel')[1]);
    var $auditLogSavePanel = $($('.panel')[2]);
    var $logSettingPanel = $($('.panel')[3]);

    $.when($.ajax(contextPath + "system/get")).done(function(data){

        var s = "<;ws;>";
        if(data.data.SYSLOGINFO){

            var strs = data.data.SYSLOGINFO.split(s);
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
        }else if(data.data.LOGSTORAGE){

            $auditLogSavePanel.find("input[type=text]").val(data.data.LOGSTORAGE);
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
        if(data.data.TRANSFERSYSLOG){

            var attr = data.data.TRANSFERSYSLOG.split(s);

            if(attr.length == 3){

                $panel2.find('input[type=radio]').each(function (e,i){

                    if ($(this).val() == attr[0]) {

                        $(this).attr('checked','checked');
                        $(this).parent().parent().addClass("cbr-checked");
                    }else {

                        $(this).parent().parent().removeClass("cbr-checked");
                    }
                });
                $panel2.find("div.col-sm-4 input").val(attr[1]);
                var ips = JSON.parse(attr[2]);
                $.each(ips,function(i,e){

                    addTableData($dataTable,e.logIp,e.logPort);
                });
            }
        }else{

            $panel2.find('input[type=radio]').each(function (e,i){

                if ($(this).val() == "no") {

                    $(this).attr('checked','checked');
                    $(this).parent().parent().addClass("cbr-checked");
                }else {

                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });
        }
    });


    $.when($.ajax(contextPath + "system/logSettings/get")).done(function(data){

       if(data.code == 200){

           var count = 0;
           $.each(data.data,function(index,e){

               if(count < 9){

                   appendLogSettings($($logSettingPanel.find("div.col-sm-2")[1]),e);
               }else if(count < 18){

                   appendLogSettings($($logSettingPanel.find("div.col-sm-2")[2]),e);
               }else{

                   appendLogSettings($($logSettingPanel.find("div.col-sm-2")[3]),e);
               }
               count++;
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
                data:{'status':status,'ip':transferIp,'port':transferPort,'type':'s'},
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

    var $btn1 = $($panel2.find("button")[0]),$addbtn = $($panel2.find("button")[1]),
        $saveBtn = $($panel2.find("button")[2]),$cancleBtn = $($panel2.find("button")[3]);



    $btn1.click(function(){

        var status = $panel2.find("input[type=radio]:checked").val();
        var hostname = $panel2.find("div.col-sm-4 input").val();
        var ipArrays = new Array();
        $.each($dataTable.find("tbody").children(),function(i,e){

            var ip = new Object();
            ip.logIp = $(e).find("td:nth-child(2)").text();
            ip.logPort = $(e).find("td:nth-child(3)").text();
            ipArrays.push(ip);
        });
       if(status == "start"){

           if(!hostname){

               toastr.error("请输入主机名");
               return false;
           }
           if(ipArrays.length==0){

               toastr.error("请添加服务器IP地址和端口");
               return false;
           }
       }
       $.ajax({

           url:contextPath+"system/syslog/set",
           method:"POST",
           dataType:"json",
           contentType:"application/json;charset=utf-8",
           data:JSON.stringify({"status":status,"host":hostname,"list":ipArrays}),
           success:function(data){

               if(data.code == 200){

                   toastr.info(data.message);
               }else{

                   toastr.error(data.message);
               }
           },
           error:function(jqx,status,error){

               toastr.error("请求错误或超时");
           }
       });

    });
    var $dataTable = $("div.col-sm-8 table").first();
    var $table = $("div.col-sm-8 table").last();
    var $host = $($table.find("input[type=text]")[0]);
    var $port = $($table.find("input[type=text]")[1]);
    $addbtn.click(function(){

        var host = $host.val();
        var port = $port.val();

        if(!ipTest(host)){

            toastr.error("请输入有效IP地址");
            return false;
        }
        if(!port || (port<=0 || port>65535)){

            toastr.error("请输入有效ip端口");
            return false;
        }

        addTableData($dataTable,host,port);
    });

    $saveBtn.click(function(){

        var host = $host.val();
        var port = $port.val();
        var index = $host.attr("name");
        if(!index){

            return false;
        }
        if(!ipTest(host)){

            toastr.error("请输入有效IP地址");
            return false;
        }
        if(!port || (port<=0 || port>65535)){

            toastr.error("请输入有效ip端口");
            return false;
        }
        var $tr = $dataTable.find("tr[name="+index+"]");
        $tr.find("td:nth-child(2)").text($host.val());
        $tr.find("td:nth-child(3)").text($port.val());
        $table.find("input[type=text]").val("");
        swichBtn($addbtn);
        swichBtn($saveBtn);
        swichBtn($cancleBtn);
    });

    $cancleBtn.click(function(){

        $table.find("input[type=text]").val("");
        swichBtn($addbtn);
        swichBtn($saveBtn);
        swichBtn($cancleBtn);
    });

    function addTableData(table,host,port){

        var index = table.find("tbody").children().length +1;
        var $tr =$("<tr name="+index+">" +
            "<td>"+index+"</td>" +
            "<td>"+host+"</td>" +
            "<td>"+port+"</td>" +
            "<td><button class='btn btn-info'>修改</button>" +
            "<button class='btn btn-red'>删除</button>" +
            "</td>" +
            "</tr>");
        $tr.on('click',"td button:nth-child(1)",function() {

            if ($addbtn.css('display') != 'none') {

                swichBtn($addbtn);
                swichBtn($saveBtn);
                swichBtn($cancleBtn);
            }
            $host.val($tr.find("td:nth-child(2)").text());
            $port.val($tr.find("td:nth-child(3)").text());
            $host.attr('name', index);
        });

        $tr.on('click',"td button:nth-child(2)",function(){

            $tr.remove();
        });
        $table.find("input[type=text]").val("");
        table.find("tbody").append($tr);
    }

    function appendLogSettings(parent,logSetting){

        var checkBox;
        if(logSetting.enable){

            checkBox = $("<lablel><input type='checkbox' class='cbr' value='"+
                logSetting.logType+"' checked>"+logSetting.logName+"</lablel><br/>");
        }else{

            checkBox = $("<lablel><input type='checkbox' class='cbr' value='"+
                logSetting.logType+"'>"+logSetting.logName+"</lablel><br/>");
        }
        parent.append(checkBox);
    }

    var logSettingBtn = $logSettingPanel.find("button.btn");
    logSettingBtn.click(function(){

        var checkboxes = $logSettingPanel.find("input[type=checkbox]:checked");
        var array = new Array();
        $.each(checkboxes,function(index,e){
           array.push($(this).val());
        });
        if(array.length > 0){

            $.ajax({

                url:contextPath + "system/logSetting/update",
                method:"POST",
                data:JSON.stringify(array),
                dataType:"json",
                contentType:"application/json;charset=utf-8",
                success:function(data){

                    if(data.code == 200){

                        toastr.info(data.message);
                    }else{
                        toastr.error(data.message);
                    }
                }
                ,
                error:function(jqx,status,error){

                    toastr.error("请求错误或超时");
                }
            });
        }
    });

    var $saveDaysBtn = $auditLogSavePanel.find("button.btn");
    $saveDaysBtn.click(function(){

        var days = $auditLogSavePanel.find("input[type=text]").val();
        if(days == "" || !$.isNumeric(days)){

            toastr.error("请输入审计日志保留天数");
            return;
        }
        $.ajax({
            url:contextPath+"system/logSetting/storage/set",
            method:"POST",
            data:{"days":days},
            success:function(data){

                if(data.code == 200){

                    toastr.info(data.message);
                }else{

                    toastr.error(data.message);
                }
            }
            ,
            error:function(jqx,status,error){

                toastr.error("请求错误或超时");
            }
        })

    });

    function swichBtn(btn){

        if(btn.css('display') == 'none'){

            btn.css('display','block');
        }else{

            btn.css('display','none');
        }
    }

    function isAgentId(str){

        var reg = /[a-zA-Z0-9]+/;
        return reg.test(str);
    }

    function isStr(str){

        var reg = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
        return reg.test(str);
    }

    function ipTest(ip){

        var regStr = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/;
        if(!ip){

            return false;
        }else if($.trim(ip) == "0.0.0.0"){

            return false;
        }else{

            return ip.match(regStr);
        }
    }
});