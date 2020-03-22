jQuery(function(){

    $.when($.ajax(contextPath + "inspector/config/get")).done(function(data){

        if(data.data.length > 0){

            for(var i in data.data){

                var _data = data.data[i];
                addData2Table(_data.agentId,_data.source,_data.channel,_data.sink,_data.status);
            }
        }
    });
    var $addModel = $("body").find(".modal-dialog").first(),
        $updateModel = $($("body").find(".modal-dialog")[1]),
        $preViewModel =  $("body").find(".modal-dialog").last()
        $addBtn = $("body").find(".btn-group button"),
        $configTb = $("body").find("table").first().find("tbody"),
        $select = $updateModel.find(".modal-body").find(".row").first().find("select"),
        $addTrBtn = $updateModel.find(".modal-footer").find("button:nth-child(1)"),
        $saveBtn = $updateModel.find(".modal-footer").find("button:nth-child(2)");

    $addBtn.click(function(){

        $addModel.find("input[type=text]").val("");
        var $confirmBtn = $("<button class='btn btn btn-turquoise'>添加配置</button>");
        $addModel.find(".modal-title").text("创建探针配置");
        if($addModel.find(".modal-footer button").length == 2){

            $addModel.find(".modal-footer button:nth-child(1)").remove();
        }
        $addModel.find(".modal-footer button").before($confirmBtn);
        $addModel.parent().modal('show', {backdrop: 'static'});

        $confirmBtn.click(function(){

            var agentId = $addModel.find(".row:nth-child(1) input").val(),
                sources = agentId+"_s1",
                channel = agentId+"_c1",
                sink = agentId+"_k1";
            if(!isAgentId(agentId)){

                toastr.error("AgentId含有特殊字符，请重新输入!");
                return false;
            }

            if(sources == "" || isStr(sources)){

                toastr.error("sources含有特殊字符，请重新输入!");
                return false;
            }
            if(channel == "" || isStr(channel)){

                toastr.error("channel含有特殊字符，请重新输入!");
                return false;
            }
            if(sink=="" || isStr(sink)){

                toastr.error("sink含有特殊字符，请重新输入!");
                return false;
            }

            $.ajax({

                url:contextPath+"inspector/config/add",
                dataType:'json',
                method:'POST',
                data:{
                    "agentId":agentId,
                    "source":sources,
                    "channel":channel,
                    "sink":sink
                },
                success:function(data){

                    if(data.code == 200){

                        toastr.info(data.message);
                        addData2Table(agentId,sources,channel,sink,"stop");

                    }else{

                        toastr.error(data.message);
                    }
                },
                error:function(jqx,status,error){

                    toastr.error(status + error );
                }
            });
            $addModel.parent().modal("hide");
        });

    });

    /**
     * select 框选择对应的类别时出发事件
     */
    $select.change(function(){

        var type = $select.find("option:selected").val();
        if(type != ""){
            var $t = $("body").find("table").last().find("tbody");
            var propList = getProps(type);
            $t.children().remove();
            for(var i in propList){
                var $prop ;
                if(propList[i].key=="type"){

                    $prop = $("<tr><td>"+propList[i].key+"</td><td><input class='form-control' type='text' value='"+propList[i].defaultV+"' disabled></td><td>"+propList[i].defaultV+"</td></tr>");
                }else{

                    $prop = $("<tr><td>"+propList[i].key+"</td><td><input class='form-control' type='text'></td><td>"+propList[i].defaultV+"</td></tr>");
                }
                if(propList[i].must){

                    $prop.find("input").attr("isMust","true");
                }
                $t.append($prop);
            }
        }
    });

    /**
     * 添加其他配置触发事件
     */
    $addTrBtn.click(function(){

        var $t = $("body").find("table").last().find("tbody");
        var $prop = $("<tr><td><input class='form-control'></td><td><input class='form-control'></td><td><button class='btn'>删除</button></td></tr>");
        $prop.on('click',"button",function(){

            $(this).parent().parent().remove();
        });
        $t.append($prop);
    });

    /**
     * 保存当前配置信息 要获取modal name agent-typeId
     */
    $saveBtn.click(function(){

        var _propList = new Array();
        var nameV = $updateModel.attr("name");
        if(nameV == ""){

           toastr.error("未获取到任何配置信息");
           return ;
        }else{
           nameV = nameV.split("-");
           var $propTable = $updateModel.find("table tbody");
           if($propTable.children().length == 0){

               return false;
           }else{

               var canSave = true;
               $propTable.children().each(function(e,i){

                   var $tr = $(this);
                   if($tr.find("input").length == 2){ //包括其他属性

                        var k = $tr.find("input").first().val();
                        var _v = $tr.find("input").last().val();
                        if(k!="" && _v != ""){

                            var prop = new Object();
                            prop.key = k;
                            prop.v = _v;
                            prop.must = false;
                            prop.defaultV = "";

                            _propList.push(prop);
                        }else{

                            toastr.error("键和值均不能为空");
                            canSave = false;
                            return false;
                        }

                   }else{//全是默认属性

                       var k = $tr.find("td").first().text();
                       var $v = $tr.find("input").first();
                       if($v.attr("isMust") && $v.val() == ""){

                           toastr.error(k + ":为必填属性，不能为空");
                           canSave = false;
                           return false;

                       }else{

                           if($v.val() != ""){

                               var prop = new Object();
                               prop.key = k;
                               prop.v = $v.val();
                               prop.must = $v.attr("isMust");
                               prop.defaultV = "";
                               _propList.push(prop);
                           }
                       }
                   }
               });
               if(canSave){

                   //发送请求到后台
                    $.ajax({
                        url:contextPath + "inspector/config/update",
                        method:"POST",
                        contentType:"application/json;charset=utf-8",
                        data:JSON.stringify({"agentId":nameV[0],
                        "typeId":nameV[1],"prefix":nameV[2],"propList":_propList}),
                        success:function(data){

                            if(data.code == 200){

                                toastr.info(data.message);
                                $updateModel.parent().modal("hide");
                            }else {

                                toastr.error(data.message);
                            }
                        },
                        error:function(jqx,status,error){

                            toastr.error(status+ " " +error );
                        }
                    });
               }
           }
        }
    });

    function showProps(propList){

        var $t = $("body").find("table").last().find("tbody");
        for(var i in propList){
            var $prop ;
            if(propList[i].key=="type"){

                $prop = $("<tr><td>"+propList[i].key+"</td><td><input class='form-control' type='text' value='"+propList[i].v+"' disabled></td><td></td></tr>");
            }else{

                $prop = $("<tr><td>"+propList[i].key+"</td><td><input class='form-control' type='text' value='"+propList[i].v+"'></td><td></td></tr>");
            }
            if(propList[i].must){

                $prop.find("input").attr("isMust","true");
            }
            $t.append($prop);
        }
    }

    /**
     *  点击对应的配置source channel，sink获取对应typeList，弹出窗口
     * @param item 绑定的对象
     * @param type 指定编辑的类型
     */
    function bindClick4Edit(item,type,agentId,typeId){

        item.click(function(){
            var title = "编辑"+typeId+"属性";
            $updateModel.find(".modal-title").text(title);
           $.ajax({
               url:contextPath+ "inspector/config/edit",
               method:"POST",
               data:{
                   "agentId":agentId,
                   "typeId":typeId,
                   "prefix":type
               },
               dataType:"json",
               success:function(data){

                   if(data.code == 200){
                       $select.children().remove();
                       var type = data.data.types;
                       for(var i in type){

                           var t = type[i].split("-")[1];
                           var $options = $("<option value="+type[i]+">"+t+"</option>");
                           $select.append($options);
                       }
                       $("body").find("table").last().find("tbody").children().remove();
                       if(data.data.props && data.data.props.length > 0){//显示改配置对应属性

                           showProps(data.data.props);
                       }
                       $updateModel.attr("name",agentId+ "-"+typeId+"-"+type);
                       $updateModel.parent().modal("show");

                   }else{

                       toastr.error(data.message);
                   }
               },
               error:function(jqx,status,error){

                    toastr.error(status + error);
               }
           }) ;
        });
    }

    /**
     * 选择类别后调用方法，返回该类别对应的属性值
     * @param propsId
     * @returns {string}
     */
    function getProps(propsId){

        var result = "";
        if(propsId == ""){
            return "";
        }else{

            $.ajax({
                url:contextPath+"inspector/config/props/"+propsId,
                method:'GET',
                dataType:'json',
                async:false,
                success:function(data){

                    if(data.code == 200){

                        result = data.data;
                    }

                },
                error:function(jqx,status,error){

                    toastr.error(status + error);
                }
            });
        }
        return result;
    }


    /**
     * 状态按钮，启动停止
     * @param btn
     * @param agentId
     * @param status
     */
    function bindStatusBtn(btn,agentId,status){

        btn.on("click","button:nth-child(1)",function(){

            $.ajax({

                url:contextPath+'inspector/config/delete/'+agentId,
                dataType:"json",
                success:function(data){

                    if(data.code == 200){

                        toastr.info(data.message);
                        btn.parent().remove();
                    }else{

                        toastr.error(data.message);
                    }

                } ,
                error:function(jqx,status,error){

                    toastr.error(status + error );
                }
            });
        });

        btn.on('click','button:nth-child(2)',function(){

            $.ajax({
                url:contextPath+'inspector/view/'+agentId,
                method:'GET',
                dataType:'json',
                success:function(data){

                    if(data.code == 200){

                        $preViewModel.find('.modal-body').children().remove();
                        $preViewModel.find('.modal-body').append("<textarea class='form-control' style='overflow: hidden; overflow-wrap: break-word; resize: vertical; height: 200px;'>"+data.message+"</textarea>");
                        $preViewModel.parent().modal("show");
                    }else{

                        toastr.error(data.message);
                    }

                } ,
                error:function(jqx,status,error){

                    toastr.error(status + error );
                }

            });
        });
        var $startBtn = btn.find("button:nth-child(3)"),$stopBtn = btn.find("button:nth-child(4)");
        if(status == "stop"){

            $stopBtn.hide();
            $startBtn.show();
        }else{

            $stopBtn.show();
            $startBtn.hide();
        }

        $startBtn.on('click',function(){

            $.ajax({
                url:contextPath + "inspector/opt/start/"+agentId,
                method:'GET',
                dataType:'json',
                success:function(data){

                    if(data.code == 200){

                        toastr.info(data.message);
                        $startBtn.parent().parent().find("td:nth-child(3)").text("已启动");
                        $startBtn.hide();
                        $stopBtn.show();
                    }else{

                        toastr.error(data.message);
                    }
                },
                error:function(jqx,status,error){

                    toastr.error(error);
                }
            });
        });

        $stopBtn.on('click',function(){

            $.ajax({
                url:contextPath + "inspector/opt/stop/"+agentId,
                method:'GET',
                dataType:'json',
                success:function(data){

                    if(data.code == 200){

                        toastr.info(data.message);
                        $startBtn.parent().parent().find("td:nth-child(3)").text("未运行");
                        $startBtn.show();
                        $stopBtn.hide();
                    }else{

                        toastr.error(data.message);
                    }
                },
                error:function(jqx,status,error){

                    toastr.error(error);
                }
            });
        });
    }

    /**
     * 绑定数据到table
     * @param agentId
     * @param sources
     * @param channel
     * @param sink
     * @param status
     */
    function addData2Table(agentId,sources,channel,sink,status){

        var $tr = $("<tr><td>"+agentId+"</td></tr>");
        var _sources = sources.split(/\s/);
        var $sourceTd = $("<td></td>");
        for(var i in _sources){
            if($.trim(_sources[i]) != ""){

                var $l = $("<button class='btn btn-icon btn-turquoise'><i class='fa-pencil'></i>"+_sources[i]+"</button>");
                $sourceTd.append($l);
                bindClick4Edit($l,"sources",agentId,_sources[0]);
            }
        }
        $tr.append($sourceTd);

        /*var _channel = channel.split(/\s/);
        var $channelTd = $("<td></td>");
        for(var i in _channel){

            if($.trim(_channel[i]) != "") {
                var $l = $("<button class='btn btn-icon btn-success'><i class='fa-pencil'></i>" + _channel[i] + "</button>");
                $channelTd.append($l);
                bindClick4Edit($l, "channels", agentId, _channel[i]);
            }
        }
        $tr.append($channelTd);

        var _sink = sink.split(/\s/);
        var $sinkTd = $("<td></td>");
        for(var i in _sink){

            if($.trim(_sink[i])){
                var $l = $("<button class='btn btn-icon btn-success'><i class='fa-pencil'></i>"+_sink[i]+"</button>");
                $sinkTd.append($l);
                bindClick4Edit($l,"sinks",agentId,_sink[i]);
            }
        }
        $tr.append($sinkTd);*/
        var $opt ;
        if(status == "stop")
        {
            $opt = $("<td>未运行</td>");
        }else{

            $opt = $("<td>已启动</td>");
        }
        $tr.append($opt);

        var $status = $("<td><button class='btn btn-icon btn-danger'><i class='fa-remove'></i>删除</button><button class='btn btn-icon btn-orange'><i class='fa-plane'></i>预览</button><button class='btn btn-icon btn-success'><i class='fa fa-toggle-on'></i>启动</button><button class='btn btn-icon btn-danger'><i class='fa fa-toggle-off'></i>停止</button></td>");
        bindStatusBtn($status,agentId,status);
        $tr.append($status);
        $configTb.append($tr);
    }

    function isAgentId(str){

        var reg = /[a-zA-Z0-9]+/;
        return reg.test(str);
    }

    function isStr(str){

        var reg = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
        return reg.test(str);
    }
});