jQuery(function(){

    var $body = $('body');
    var $versionPanel = $($body.find('.panel')[0]),
        $virusPanel = $($body.find('.panel')[3]),
        $upgradePanel = $($body.find('.panel')[1]),
        $packagePanel = $($body.find('.panel')[2]);

    $.when($.ajax(contextPath+"system/get")).done(function(data){

        if(data.data.CURRENTVERSION){

            $versionPanel.find('p').html(data.data.CURRENTVERSION);
        }
        if(data.data.VIRUSVERSION){

            $virusPanel.find('p').html(data.data.VIRUSVERSION);
        }
        if(data.data.CURRENTUPGRADEPACKAGE && data.data.CURRENTUPGRADEPACKAGE != ""){

            //调用函数生成tr添加到table当中
            var packages = new String(data.data.CURRENTUPGRADEPACKAGE).split("<;ws;>");
            for(var i=0;i< packages.length ;i++){

                var tmp = packages[i];
                if(tmp && tmp != ""){

                    addPackage(i+1,tmp);
                }
            }
        }
        if(data.data.UPGRADEVERSION && data.data.UPGRADEVERSION != null){

            //调用生成tr函数添加到table当中
            var infos = new String(data.data.UPGRADEVERSION).split("<;ws;>");
            for(var i=0;i<infos.length;i++){

                var t = new String(infos[i]).split(" ");
                if(t != ""){
                    addPackageInfo(i+1,t[0],t[1]);
                }

            }
        }
    });

    var uploader;
    if(isIE()){//如果为ie9一下浏览器，则使用flash上床，切不开启分块上传。

        uploader = WebUploader.create({

            // swf文件路径
            swf: contextPath+'/static/js/webuploader/Uploader.swf',

            // 文件接收服务端。
            server: contextPath+'/system/upgrade/upload',

            // 选择文件的按钮。可选。
            // 内部根据当前运行是创建，可能是input元素，也可能是flash.
            pick: '#uploadBtn',

            // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
            resize: false,
            //分片上传
            chunked: false,
            //出现网络错误，重试次数
            chunkRetry:5,
            //上传并发数
            threads:5,
            //设置上传方法
            method:'POST',
            //文件去重
            duplicate:false,
            //分片大小
            //10485760
            chunkSize:5242880,
            runtimeOrder:'flash',
            prepareNextFile:true,
            accept:null,
            compress:false
        });
    }else{

        uploader = WebUploader.create({

            // swf文件路径
            swf: contextPath+'static/js/webuploader/Uploader.swf',

            // 文件接收服务端。
            server: contextPath+'system/upgrade/upload',

            // 选择文件的按钮。可选。
            // 内部根据当前运行是创建，可能是input元素，也可能是flash.
            pick: '#uploadBtn',

            // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
            resize: false,
            //分片上传
            chunked: true,
            //出现网络错误，重试次数
            chunkRetry:5,
            //上传并发数
            threads:5,
            //设置上传方法
            method:'POST',
            //文件去重
            duplicate:false,
            //分片大小
            //10485760
            chunkSize:10485760,
            runtimeOrder:'html5',
            prepareNextFile:true,
            accept:null,
            compress:false
        });
    }

    uploader.on('beforeFileQueued',function(file){

        uploader.reset();
        return true;
    });

    uploader.on( 'fileQueued', function( file ) {

        $upgradePanel.find('p').html(file.name);
    });

    //上传返回错误信息时不能正确处理
    uploader.on( 'uploadComplete', function( file ) {

        $('p').last().html("");
        uploader.removeFile( file,true);
    });

    uploader.on( 'uploadSuccess', function( file ) {

        toastr.info("文件上传成功");
        if($upgradePanel.find('table tbody').children().length == 0){

            addPackage(1,file.name);
        }else{

            addPackage($upgradePanel.find('table tbody').children().length+1,file.name);
        }
    });

    uploader.on( 'uploadAccept', function( object,ret ) {

        if(ret.code == 0){

            toastr.error(ret.data+ " 已终止上传");
            $('p').last().html("");
            uploader.stop(object.file);
        }
    });

    uploader.on( 'error', function( file ,reason) {

        toastr.info("文件上传出错 "+reason);
        $('p').last().html("");
    });

    uploader.on('uploadBeforeSend',function(block,data){

        var file = block.file;
        data.fileId = file.source.uid;
        data.fileName = file.name;
    });

    var $uploadkBtn = $upgradePanel.find('button').first();
    $uploadkBtn.on('click',function(){

        $('p').last().text();
        if( $('p').last().text().length != 0){
            toastr.info("正在上传，请稍等");
            uploader.upload();
        }else{

            toastr.error("请选择配置文件");
            return false;
        }
    });

    function addPackage(index,packageName){

        var $tr = $("<tr>" +
            "<td style='text-align: center'>"+index+"</td>" +
            "<td style='text-align: center'>"+packageName+"</td>" +
            "<td style='text-align: center'>" +
            "<button class='btn btn-turquoise btn-single'>升级</button>" +
            "<button class='btn btn-red btn-single'>删除</button>" +
            "</td>" +
            "</tr>");
        $upgradePanel.find("table tbody").append($tr);
        $tr.on('click','button:nth-child(1)',function(){

            var name =  $tr.find("td:nth-child(2)").text();

            if(name){

                $.ajax({

                    url:contextPath+'system/upgrade/update',
                    data:{'packagename':name},
                    dataType:'json',
                    method:'POST',
                    success:function(data){

                        if(data.code == 200){

                            toastr.info(data.message);
                            $tr.remove();
                        }else{

                            toastr.error(data.message);
                        }
                    }
                });
            }
        });


        $tr.on('click','button:nth-child(2)',function(){

           var name =  $tr.find("td:nth-child(2)").text();

           if(name){

               $.ajax({

                   url:contextPath+'system//upgrade/delete',
                   data:{'packagename':name},
                   dataType:'json',
                   method:'POST',
                   success:function(data){

                       if(data.code == 200){

                           toastr.info(data.message);
                           $tr.remove();
                       }else{

                           toastr.error(data.message);
                       }
                   }
               })
           }
        });
    }

    function addPackageInfo(index,packageName,time){

        var $tr = $("<tr>" +
            "<td style='text-align: center'>"+index+"</td>" +
            "<td style='text-align: center'>"+packageName+"</td>" +
            "<td style='text-align: center'>"+time+"</td>" +
            "</tr>");

        $packagePanel.find('table tbody').append($tr);
    }

    function isIE(){

        var ua = navigator.userAgent.toLowerCase();
        if(window.ActiveXObject){

            if(ua.match(/msie ([\d.]+)/)[1] <= "9.0")
                return true;

            return false;
        }
    }
})
