jQuery(function(){

    var $body = $('body');

    var $policyPanel = $($('.panel-body')[0]);
    var $taskNamePanel = $($('.panel-body')[1]);

    $body.find("select").select2({

        placeholder: '选择策略配置属性',
        allowClear: true
    }).on('select2-open', function()
    {
        // Adding Custom Scrollbar
        $(this).data('select2').results.addClass('overflow-hidden').perfectScrollbar();
    });

    $body.find("input[type=checkbox]").on("change",function(){

       if($(this).parent().parent().parent().find("label").text() == "未启用"){

           $(this).parent().parent().parent().find("label").text("启用");
       }else{

           $(this).parent().parent().parent().find("label").text("未启用");
       }
    });

    $.when($.ajax(contextPath + "system/task/filename/get")).done(function(data){

       if(data.code == 200){

            $.each(data.data,function(index,e){
                var option = $("<option value='"+e+"'>"+e+"</option>");
                var option2 = $("<option value='"+e+"'>"+e+"</option>");
                $taskSelect2.append(option2);
                $task.append(option);
            });
       }
    });

    $.when($.ajax(contextPath + "system/policy/getAll")).done(function(data){

        if(data.code == 200){

            $.each(data.data,function(index,e){
                appendData(e);
            });
        }
    });

    $.when($.ajax(contextPath + "monitor/taskNames/get")).done(function(data){

        if(data.code == 200){

            $.each(data.data,function(index,e){

                appendTaskNames(index,e);
            })
        }
    });

    var $task = $($policyPanel.find("select")[0]);

    var $updateUpBtn = $($policyPanel.find('button.btn-turquoise')[0]);
    $updateUpBtn.click(function(){

        var taskName = $task.find("option:selected").val();
        var num = $policyPanel.find("input.form-control").first().val();
        var unit = $($policyPanel.find("select")[1]).find("option:selected").val();
        var interval =$policyPanel.find("input.form-control").last().val();
        var intervalUnit = $($policyPanel.find("select")[2]).find("option:selected").val();

        var totalCheck = "";
        if($($policyPanel.find("input[type=checkbox]")[0]).parent().parent().hasClass("cbr-checked")){

            totalCheck = "yes";
        }else{

            totalCheck = "no";
        }
        var intervalCheck = "";
        if($($policyPanel.find("input[type=checkbox]")[1]).parent().parent().hasClass("cbr-checked")){

            intervalCheck = "yes";
        }else{

            intervalCheck = "no";
        }

        if(taskName == ""){

            toastr.error("请选择任务");
            return false;
        }
        if(totalCheck=="yes"){

            if(!$.isNumeric(num)){

                toastr.error("请填写总量控制值");
                return false;
            }
            if(unit == ""){

                toastr.error("请选择流量单位");
                return false;
            }
        }else{

            num = 0;
            unit= "M";
        }
        if(intervalCheck == "yes"){

            if(!$.isNumeric(interval)){

                toastr.error("请填写报警间隔");
                return false;
            }

            if(intervalUnit == ""){

                toastr.error("请选择时间单位");
                return false;
            }
        }else{

            interval = 0;
            intervalUnit = "m";
        }

        if(totalCheck == "no" && intervalCheck == "no"){

            return false;
        }

        $.ajax({
            url:contextPath + "system/policy/update",
            method:"POST",
            data:{"taskName":taskName,"total":num,"unit":unit,"interval":interval,"intervalUnit":intervalUnit,"totalEnable":totalCheck,"intervalEnable":intervalCheck},
            dataType:"json",
            success:function(data){

                 if(data.code == 200){

                     toastr.info(data.message);
                     var o = new Object();
                     o.taskName = taskName;
                     o.total = num;
                     o.unit = unit;
                     o.interval = interval;
                     o.intervalUnit = intervalUnit;
                     o.totalEnable = totalCheck;
                     o.intervalEnable = intervalCheck;
                     appendData(o);
                 }  else{

                     toastr.error(data.message);
                 }
            } ,
            error:function(jqx,status,err){

                toastr.error("请求错误或超时");
             }

        });
    });

    var $policyTable = $policyPanel.find("table");

    var $taskSelect2 = $($taskNamePanel.find("select")[0]);
    var $updateBtnForName = $($taskNamePanel.find('button.btn-turquoise'));
    $updateBtnForName.click(function(){

        var task = $taskSelect2.find("option:selected").val();
        var name = $taskNamePanel.find("input.form-control").val();
        if(task == ""){

            toastr.error("请选择任务");
            return false;
        }
        if(name == ""){

            toastr.error("请填写任务名称");
            return false;
        }
        $.ajax({
            url: contextPath + "monitor/taskNames/add",
            method: "POST",
            data: {"task":task,"name":name},
            dataType: "json",
            success: function (data) {

                if(data.code == 200){

                    appendTaskNames(task,name);
                }
            },
            error: function (jqx, status, error) {

                toastr.error("请求错误或超时");
            }
        });
    });
    var $taskNameTb = $taskNamePanel.find("table");
    function appendTaskNames(k,v){

        var _tr = $taskNameTb.find("tr[name="+k+"]");
        if(_tr.length == 1){

            $(_tr.find("td")[1]).text(v);
        }else{
            var $tr = $("<tr name='"+k+"'>" +
                "<td>"+k+"</td>" +
                "<td>"+v+"</td>" +
                "<td><button class='btn btn-red'>删除</button></td>" +
                "</tr>");
            $tr.on("click","button",function(){

                $.ajax({
                    url:contextPath + "monitor/taskNames/delete" ,
                    data:{"task":k},
                    method:"POST",
                    dataType:"json",
                    success:function(data){

                        if(data.code == 200){

                            toastr.info(data.message);
                            $tr.remove();
                        }else{

                            toastr.error(data.message);
                        }
                    },
                    error:function(jqx,status,error){

                        toastr.error("请求错误或超时");
                    }
                });
            });
            $taskNameTb.append($tr);
        }
    }

    function appendData(policy){

        var _tr = policyIsExist(policy.taskName);
        if(_tr.length ==1 ){

            $(_tr.find("td")[1]).text((policy.unit == 'M'?policy.total+'M':policy.total+'G'));
            $(_tr.find("td")[2]).text((policy.intervalUnit == 'm'?policy.interval+'分钟':policy.interval+'小时'));
        }else{

            var alarmInterval = "",alarmTotal = "";
            if(policy.intervalEnable == "no"){

                alarmInterval = "未启用";
            }else{

                alarmInterval = policy.intervalUnit == 'm'?policy.interval+'分钟':policy.interval+'小时';
            }
            if(policy.totalEnable== "no"){

                alarmTotal= "未启用";
            }else{

                alarmTotal = policy.unit == 'M'?policy.total+'M':policy.total+'G';
            }

            var $tr = $("<tr name='"+policy.taskName+"'>" +
                "<td>"+policy.taskName+"</td>" +
                "<td>"+alarmTotal+"</td>" +
                "<td>"+alarmInterval+"</td>" +
                "<td><button class='btn btn-red'>删除</button></td>" +
                "</tr>");

            $tr.on("click","button",function(){

                $.ajax({
                    url:contextPath + "system/policy/delete" ,
                    data:{"taskName":policy.taskName},
                    method:"POST",
                    dataType:"json",
                    success:function(data){

                        if(data.code == 200){

                            toastr.info(data.message);
                            $tr.remove();
                        }else{

                            toastr.error(data.message);
                        }
                    },
                    error:function(jqx,status,error){

                        toastr.error("请求错误或超时");
                    }
                });
            });
            $policyTable.append($tr);
        }
    }

    function policyIsExist(taskName){

        return $policyTable.find("tr[name="+taskName+"]");
    }

    function updateSelects( selects,value){

        selects.children().removeAttr("selected").removeClass("select2-chosen");
        $.each(selects.children(),function(index,e){

            if($(this).val() == value){

                $(this).attr("selected",true).addClass("select2-chosen");
                selects.trigger("change");
            }
        });
    }

})
