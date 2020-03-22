jQuery(function(){

  var $confPanel = $("body").find("div.panel").first(),
      $table = $confPanel.find("table"),
      $addBtn = $confPanel.find("button.btn-turquoise");

  $.when($.ajax(contextPath + "system/remote/get/all")).done(function(data){

      if(data.code ==200){

          $.each(data.data,function(index,e){

              appendData(e);
          });
      }
  });

$addBtn.click(function(){

    var deviceId = $($confPanel.find("input[type=text]")[0]).val();
    var deviceName = $($confPanel.find("input[type=text]")[1]).val();
    var deviceType = "";
    var deviceIp = $($confPanel.find("input[type=text]")[2]).val();
    var devicePort = $($confPanel.find("input[type=text]")[3]).val();
    var protocol = "";

    if(deviceId == ""){

        toastr.error("请输入设备ID");
        return false;
    }
    if(deviceName == ""){

        toastr.error("请输入设备名称");
        return false;
    }
    $.each($confPanel.find("input[name=type]"),function(index,e){

       if($(e).parent().parent().hasClass("cbr-checked")){

           deviceType = $(e).val();
       }
    });
    if(deviceType == ""){

        toastr.error("请选择设备类型");
        return false;
    }
    if(deviceIp == "" || !ipTest(deviceIp)){

        toastr.error("请输入设备IP");
        return false;
    }
    if(!$.isNumeric(devicePort) ||  0>devicePort || devicePort >= 65535){

        toastr.error("请输入设备端口");
        return false;
    }
    $.each($confPanel.find("input[name=protocolType]"),function(index,e){

        if($(e).parent().parent().hasClass("cbr-checked")){

            protocol = $(e).val();
        }
    });
    if(protocol == ""){

        toastr.error("请选择通信协议");
        return false;
    }
    $.ajax({

        url:contextPath + "system/remote/device/add",
        method:"POST",
        dataType:"json",
        data:{"deviceId":deviceId,"deviceName":deviceName,"deviceType":deviceType,
            "deviceIp":deviceIp,"devicePort":devicePort,"deviceProtocol":protocol},
        success:function(data){

            if(data.code == 200){

                appendData(data.data);
                toastr.info(data.message);
                $addBtn.find("span").text("设置");
            }else{

                toastr.error(data.message);
            }
        },
        error:function(jqx,status,error){

            toastr.error("请求错误或超时");
        }
    });
});

    function appendData(e){

        var _tr = $table.find("tr[name="+e.deviceId+"]");
        if(_tr.length == 0){ //无记录
            var type = e.deviceType;
            var tr = $("<tr name='"+e.deviceId+"'>" +
                "<td>"+e.deviceId+"</td>" +
                "<td>"+e.deviceName+"</td>" +
                "<td>"+(e.deviceType == 'wangzha'?'网闸':'防火墙')+"</td>" +
                "<td>"+e.deviceIp+"</td>" +
                "<td>"+e.devicePort+"</td>" +
                "<td>"+e.deviceProtocol+"</td>" +
                "<td><button class='btn btn-blue'>更新</button><button class='btn btn-red'>删除</button></td>" +
                "</tr>");

            tr.on("click","button:nth-child(1)",function(){
                $confPanel.find("select").find("option").removeAttr("selected");
                $($confPanel.find("input[type=text]")[0]).val(e.deviceId);
                $($confPanel.find("input[type=text]")[1]).val(e.deviceName);
                $.each($confPanel.find("input[name=type]"),function(index,e){
                   if($(e).val() == type) {

                       $(e).parent().parent().addClass("cbr-checked");
                       $(e).attr("checked",true);
                   }else{

                       $(e).parent().parent().removeClass("cbr-checked");
                       $(e).removeAttr("checked");
                   }
                });
                $($confPanel.find("input[type=text]")[2]).val(e.deviceIp);
                $($confPanel.find("input[type=text]")[3]).val(e.devicePort);
                var protocol =  e.deviceProtocol;
                $.each($confPanel.find("input[name=protocolType]"),function(index,e){

                    if($(e).val() == protocol){

                        $(e).parent().parent().addClass("cbr-checked");
                        $(e).attr("checked",true);
                    }else{

                        $(e).parent().parent().removeClass("cbr-checked");
                        $(e).removeAttr("checked");
                    }
                });
                $addBtn.find("span").text("更新");
            });

            tr.on("click","button:nth-child(2)",function(){

                $.ajax({

                    url:contextPath + "system/remote/device/delete",
                    data:{"deviceId":e.deviceId},
                    dataType:"json",
                    method:"POST",
                    success:function(data){

                        if(data.code == 200){

                            tr.remove();
                            toastr.info(data.message);
                        }else{

                            toastr.error(data.message);
                        }
                    },
                });
            });

            $table.append(tr);
        }else{

            _tr.find("td:nth-child(2)").text(e.deviceName);
            _tr.find("td:nth-child(4)").text(e.deviceIp);
            _tr.find("td:nth-child(5)").text(e.devicePort);
            _tr.find("td:nth-child(6)").text(e.deviceProtocol);
        }
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