jQuery(function(){

    var $body = $('body');

    var $panel = $body.find('.panel');
    $.when($.ajax(contextPath+"monitor/device/get")).done(function(data){

        if(data.data){

            for(var i=0;i<data.data.length;i++){

                add(i+1,data.data[i]);
            }
        }
    });

    function add(index,data){

        var speed = data.speed=="unknown!"?"未知":data.speed,
            duplex = data.duplex=="Full"?"全双工":data.duplex=="Half"?"半双工":"未知",
            mediatype = data.mediaType == "Twisted Pair"?"双绞线":data.mediaType=="FIBRE"?"光纤线":"未知",
            status = data.linkState=="1"?"<label style='color:#68b828'>连接</label>":"<label style='color:red'>断开</label>";
        var $tr = $("<tr>" +
            "<td>"+index+"</td>" +
            "<td>"+data.decviceName+"</td>" +
            "<td>"+speed+"</td>" +
            "<td>"+duplex+"</td>" +
            "<td>"+mediatype+"</td>" +
            "<td>"+status+"</td>" +
            "</tr>");

        $panel.find("table tbody").append($tr);
    }






})
