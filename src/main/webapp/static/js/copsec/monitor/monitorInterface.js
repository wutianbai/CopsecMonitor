jQuery(function(){

    var $body = $('body');

    var $panel = $body.find('.panel');
    $.when($.ajax(contextPath+"monitor/address/get")).done(function(data){

        if(data.data){

            for(var i=0;i<data.data.length;i++){

                add(i+1,data.data[i]);
            }
        }
    });

    function add(index,data){

        var status = data.linkState=="1"?"<label style='color:#68b828'>连接</label>":"<label style='color:red'>断开</label>";
        var ip = data.ipAddress==null|| data.ipAddress == "" ?"Unknown!":data.ipAddress;
        var mask = data.subnetMask==null || data.subnetMask == "" ?"Unknown!":data.subnetMask;
        var $tr = $("<tr>" +
            "<td>"+index+"</td>" +
            "<td>"+data.decviceName+"</td>" +
            "<td>"+ip+"</td>" +
            "<td>"+mask+"</td>" +
            "<td>"+status+"</td>" +
            "</tr>");

        $panel.find("table tbody").append($tr);
    }






})
