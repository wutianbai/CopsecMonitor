jQuery(function(){

    var $fileTaskPanel = $($("body").find("div.panel")[0]),
        $dbTaskPanel = $($("body").find("div.panel")[1]),
        $protocolTaskPanel = $($("body").find("div.panel")[2]);

    var nf = new FormatNumber();
    nf.init({decimal:0});
    /**
     * 获取历史文件信息
     * @param str
     * @returns {boolean}
     */
    var getTaskStatus =  function getFileHistory(){

        $.ajax({
            url:contextPath+"statistics/today/files",
            data:{"title":$.now()},
            method:"POST",
            dataType:"json",
            success:function(data){

                if(data.code == 200){

                    updateFileStatus(data);
                    return data.data;
                }else{

                    return null;
                }
            },
            error:function(jqx,status,error){

                toastr.error("请求错误或超时");
            }
        });

        $.ajax({
            url:contextPath+"statistics/dbTask/detail/"+$.now(),
            method:"GET",
            dataType:"json",
            success:function(data){

                if(data.code == 200){

                    dbTaskData(data);
                    return data.data;
                }else{

                    return null;
                }
            },
            error:function(jqx,status,error){

                toastr.error("请求错误或超时");
            }
        });


    }

    function updateFileStatus(data){

        var $table = $("table.table").first().find("tbody");
        $table.children().remove();
        if(data.code == 200){

            if(data.data.length == 0){

                $fileTaskPanel.css("display","none");
            }else{

                $fileTaskPanel.css("display","block");
                $.each(data.data,function(k,v){

                    var  tr = $("<tr><td>"+v.taskName+"</td>" +
                        "<td>"+v.days+"</td>" +
                        "<td>"+v.gatherCountToday+"</td>" +
                        "<td>"+(v.gatherSumToday==null?0:v.gatherSumToday)+"</td>" +
                        "<td>"+(v.gatherSpeedToday==null?0:v.gatherSpeedToday)+"</td>" +
                        "<td>"+v.storageCountToday+"</td>" +
                        "<td>"+(v.storageSumToday==null?0:v.storageSumToday)+"</td>" +
                        "<td>"+(v.storageSpeedToday == null?0:v.storageSpeedToday)+"</td>" +
                        "<td>"+v.gatherCountTotal+"</td>" +
                        "<td>"+v.gatherSumTotal+"</td>" +
                        "<td>"+v.gatherSpeedTotal+"</td>" +
                        "<td>"+v.storageCountTotal+"</td>" +
                        "<td>"+v.storageSumTotal+"</td>" +
                        "<td>"+v.storageSpeedTotal+"</td>" +
                        "</tr>");
                    $table.append(tr);
                });
            }
        }
    }

    function dbTaskData(data){

        var $table = $dbTaskPanel.find("table.table").find("tbody");
        $table.children().remove();
        if(data.code == 200){

            if(data.data.length == 0){

                $dbTaskPanel.css("display","none");
            }else{

                $dbTaskPanel.css("display","block");
                $.each(data.data,function(index,e){

                    var tr = $("<tr>" +
                        "<td>"+e.taskName+"</td>" +
                        "<td>"+e.days+"</td>" +
                        "<td>"+nf.doFormat(e.gatherToday)+"</td>" +
                        "<td>"+nf.doFormat(e.storageToday)+"</td>" +
                        "<td>"+nf.doFormat(e.gatherTotal)+"</td>" +
                        "<td>"+nf.doFormat(e.storageTotal)+"</td>" +
                        "</tr>");
                    $table.append(tr);
                });
            }
        }
    }



    function isId(str){

        var reg = /[a-zA-Z0-9]+/;
        return reg.test(str);
    }


    setInterval(getTaskStatus,3000);
});

