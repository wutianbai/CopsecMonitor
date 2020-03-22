jQuery(function(){

    var $body = $('body');

    var $uploadPanel = $($('.panel')[1]);
    var $restorePanel = $($('.panel')[2]);

    var uploader;
    if(isIE()){//如果为ie9一下浏览器，则使用flash上床，切不开启分块上传。

        uploader = WebUploader.create({

            // swf文件路径
            swf: contextPath+'/static/js/webuploader/Uploader.swf',

            // 文件接收服务端。
            server: contextPath+'/system/config/upload',

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
            swf: contextPath+'/static/js/webuploader/Uploader.swf',

            // 文件接收服务端。
            server: contextPath+'/system/config/upload',

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

        if (uploader.getFiles().length > 1){

            return false;
        }
        return true;
    });

    uploader.on( 'fileQueued', function( file ) {

        var $table = $uploadPanel.find('table tbody');
        var $delete = $('<tr>' +
            '<td style=\'text-align:center;\'>' + file.name + '</td>' +
            '<td style=\'text-align:center;\'><button class="btn btn-icon btn-red">删除</button></td>'+
            '</tr>' ).appendTo($table);

        $delete.on('click','button',function(){

            $(this).parent().parent().remove();

            uploader.removeFile( file,true);

        });

    });

    var $uploadkBtn = $($body.find('button')[0]);
    $uploadkBtn.on('click',function(){

        if( $uploadPanel.find('table tbody').children().length == 1){
            toastr.info("正在上传，请稍等");
            uploader.upload();
        }else{

            toastr.error("请选择配置文件");
            return false;
        }
    });

    //上传返回错误信息时不能正确处理
    uploader.on( 'uploadComplete', function( file ) {

        $uploadPanel.find('table tbody button').trigger('click');
        uploader.removeFile( file,true);
    });

    uploader.on( 'uploadSuccess', function( file ) {

        toastr.info("文件上传成功");
    });

    uploader.on( 'uploadAccept', function( object,ret ) {

        if(ret.code == 0){

            toastr.error(ret.message+ " 已终止上传");
            $uploadPanel.find('table tbody button').trigger('click');
            uploader.stop(object.file);
        }
    });

    uploader.on( 'error', function( file ,reason) {

        toastr.info("文件上传出错 "+reason);
        $uploadPanel.find('table tbody button').trigger('click');
    });

    uploader.on('uploadBeforeSend',function(block,data){

        var file = block.file;
        data.fileId = file.source.uid;
        data.fileName = file.name;
    });


    var $restoreBtn = $($body.find('button').last());
    $restoreBtn.on('click',function(){

        toastr.info("命令已发送，请稍等");
        $.ajax({

            url:contextPath+'system//config/restore',
            dataType:'json',
            method:'POST',
            success:function (data,sts,axqh) {}
        });

    });

    function isIE(){

        var ua = navigator.userAgent.toLowerCase();
        if(window.ActiveXObject){

            if(ua.match(/msie ([\d.]+)/)[1] <= "9.0")
                return true;

            return false;
        }
    }
})
