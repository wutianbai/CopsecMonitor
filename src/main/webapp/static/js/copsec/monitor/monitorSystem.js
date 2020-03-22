jQuery(function(){

    var $body = $('body');
    var $cpu = $body.find('.panel:nth-child(1)'),
        $disk = $body.find('.panel:nth-child(2)'),
        $memery = $body.find('.panel:nth-child(3)'),
        $network = $body.find('.panel:nth-child(4)'),
        $file = $body.find('.panel:nth-child(5)'),
        $db = $body.find('.panel:nth-child(6)');

    $.when($.ajax(contextPath+"monitor/system/get")).done(function(data){

        var status = $.parseJSON(data.data);
        refresh(status);

    });

    setInterval(function(){

        $.ajax({

            url:contextPath+'monitor/system/get',
            method:'GET',
            success:function(data){

                var status = $.parseJSON(data.data);
                refresh(status);
            }
        })
    },5000);

    function refresh(status){

        if(status.cpuStatus){

            $($cpu.find("label")[0]).text("cpu使用率："+status.cpuStatus.cpuUseRate);
            $($cpu.find("label")[1]).text("处理器："+status.cpuStatus.processorName);
            $($cpu.find("label")[2]).text("处理器数量："+status.cpuStatus.processorCount);
        }
        if(status.hardDisk){

            $disk.find('.form-group:nth-child(2) label').text('磁盘总量：'+status.hardDisk.total);
            $disk.find('.form-group:nth-child(3) label').text('已使用量：'+status.hardDisk.totalUsed);
            $disk.find('.form-group:nth-child(4) label').text('使用率：'+status.hardDisk.totalUseRate);

            if (status.hardDisk.list){

                $disk.find('table tbody').children().remove();
                for (var i=0;i< status.hardDisk.list.length;i++){

                    addDisk(status.hardDisk.list[i]);
                }
            }
        }
        if (status.memeryStatus) {

            $($memery.find('label')[0]).html('内存总量：'+status.memeryStatus.total);
            $($memery.find('label')[1]).html('剩余：'+status.memeryStatus.free);
            $($memery.find('label')[2]).html('内存使用率：'+status.memeryStatus.useRate);

        }
        if (status.ethStatus) {

            $network.find('.form-group:nth-child(2) label').first().html('接收总量：'+status.ethStatus.totalReceive);
            $network.find('.form-group:nth-child(2) label').last().html('发送总量：'+status.ethStatus.totalSend);


            $network.find('.form-group:nth-child(3) label').first().html('当前接收速率：'+status.ethStatus.receiveSpeed);
            $network.find('.form-group:nth-child(3) label').last().html('当前发送速率：'+status.ethStatus.sendSpeed);

            if(status.ethStatus.list){

                $network.find('table tbody').children().remove();
                for (var i=0;i<status.ethStatus.list.length;i++ ){

                    addNetwork(status.ethStatus.list[i]);
                }
            }
        }
        /*if (status.fileSyncTaskStaus) {

            $file.find('.form-group:nth-child(2) label').first().html('下载文件数量:'+status.fileSyncTaskStaus.downFileCount);
            $file.find('.form-group:nth-child(2) label').last().html('上传文件数量:'+status.fileSyncTaskStaus.upFileCount);


            $file.find('.form-group:nth-child(3) label').first().html('下载文件总大小:'+status.fileSyncTaskStaus.downFileSizeCount);
            $file.find('.form-group:nth-child(3) label').last().html('上传文件总大小:'+status.fileSyncTaskStaus.upFileSizeCount);

            if(status.fileSyncTaskStaus.list){

                $file.find('table tbody').children().remove();
                for (var i=0;i<status.fileSyncTaskStaus.list.length;i++ ){

                    addFile(status.fileSyncTaskStaus.list[i]);
                }
            }
        }*/

        /*if (status.dbSyncTaskStatus){

            $db.find(".row:nth-child(1)").find(".form-group:nth-child(2) label").html('采集变更数:'+status.dbSyncTaskStatus.gatherCount);
            $db.find(".row:nth-child(1)").find(".form-group:nth-child(5) label").html('删除:'+status.dbSyncTaskStatus.gatherDeleteCount);
            $db.find(".row:nth-child(1)").find(".form-group:nth-child(3) label").html('增加:'+status.dbSyncTaskStatus.gatherInsertCount);
            $db.find(".row:nth-child(1)").find(".form-group:nth-child(4) label").html('修改:'+status.dbSyncTaskStatus.gatherUpdateCount);

            $db.find(".row:nth-child(2)").find(".form-group:nth-child(2) label").html('上传变更数:'+status.dbSyncTaskStatus.storageCount);
            $db.find(".row:nth-child(2)").find(".form-group:nth-child(5) label").html('删除:'+status.dbSyncTaskStatus.storageDeleteCount);
            $db.find(".row:nth-child(2)").find(".form-group:nth-child(3) label").html('增加:'+status.dbSyncTaskStatus.storageInsertCount);
            $db.find(".row:nth-child(2)").find(".form-group:nth-child(4) label").html('修改:'+status.dbSyncTaskStatus.storageUpdateCount);

            if (status.dbSyncTaskStatus.list){

                $db.find('table tbody').children().remove();
                for (var i=0;i<status.dbSyncTaskStatus.list.length;i++ ){

                    addDb(status.dbSyncTaskStatus.list[i]);
                }

            }
        }*/
    }

    $disk.on('click','button',function(){

        if($disk.find('.row:nth-child(2)').css('display') == 'none'){

            $disk.find('.row:nth-child(2)').css('display','block');
            $(this).text("隐藏")
        }else{

            $disk.find('.row:nth-child(2)').css('display','none');
            $(this).text("详情");
        }
    });
    $network.on('click','button',function (){

       if ($network.find('.row:nth-child(2)').css('display') == 'none'){

           $network.find('.row:nth-child(2)').css('display','block');
           $(this).text("隐藏");
       }  else{
           $(this).text("详情");
           $network.find('.row:nth-child(2)').css('display','none');
       }
    });

    $file.on('click','button',function (){

        if ($file.find('.row:nth-child(2)').css('display') == 'none'){

            $file.find('.row:nth-child(2)').css('display','block');
            $(this).text("隐藏");
        }  else{
            $(this).text("详情");
            $file.find('.row:nth-child(2)').css('display','none');
        }

    });

    $db.on('click','button',function () {

        if ($db.find('.row:nth-child(3)').css('display') == 'none'){

            $db.find('.row:nth-child(3)').css('display','block');
            $(this).text("隐藏");
        }  else{
            $(this).text("详情");
            $db.find('.row:nth-child(3)').css('display','none');
        }

    })

    function addDb(db){

        var tr = $("<tr>" +
            "<td>"+db.taskName+"</td>" +
            "<td>"+db.gatherDesc+"</td>" +
            "<td>"+db.storageDesc+"</td>" +
            "</tr>");
        $db.find('table tbody').append(tr);
    }

    function addDisk(detail){

        var tr = $("<tr>" +
            "<td>"+detail.partitionName+"</td>" +
            "<td>"+detail.total+"</td>" +
            "<td>"+detail.used+"</td>" +
            "<td>"+detail.useRate+"</td>" +
            "</tr>");
        $disk.find('table tbody').append(tr);
    }

    function addNetwork(net){

        var x = net.connected=="true"?"<label style='color:#68b828'>连接</label>":"<label style='color:#f90606'>未连接</label>";
        var $tr = $("<tr>" +
            "<td>"+net.ethName+"</td>" +
            "<td>"+x+"</td>" +
            "<td>"+net.totalReceive+"</td>" +
            "<td>"+net.totalSend+"</td>" +
            "<td>"+net.receiveSpeed+"</td>" +
            "<td>"+net.sendSpeed+"</td>" +
            "</tr>");
        $network.find('table tbody').append($tr);
    }

    function addFile(file){

        var $tr = $("<tr>" +
            "<td>"+file.taskName+"</td>" +
            "<td>"+file.downFileCount+"</td>" +
            "<td>"+file.downFileSizeCount+"</td>" +
            "<td>"+file.upFileCount+"</td>" +
            "<td>"+file.upFileSizeCount+"</td>" +
            "</tr>");
        $file.find('table tbody').append($tr);
    }

    function addTr(index,data){

        var $tr = $("<tr>" +
            "<td>"+index+"</td>" +
            "<td>"+data.processId+"</td>" +
            "<td>"+data.serverName+"</td>" +
            "<td>"+data.cupOccupancy+"</td>" +
            "<td>"+data.memoryOccupancy+"</td>" +
            "<td>"+data.virtualMemory+"</td>" +
            "<td>"+data.physicalMemory+"</td>" +
            "</tr>");
        $('body').find('table tbody').append($tr);
    }
})
