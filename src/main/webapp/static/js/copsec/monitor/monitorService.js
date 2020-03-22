jQuery(function(){

    var $body = $('body');

    $.when($.ajax(contextPath+"monitor/service/get")).done(function(data){

        if(data.data){

            for(var i=0;i< data.data.length;i++){

                addTr(i+1,data.data[i]);
            }
        }
    });

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
