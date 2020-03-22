jQuery(function(){

    var $transferService = $($('.panel')[0]);
    var $panel2 = $($('.panel')[1]);

    $.when($.ajax(contextPath + "system/runLog/get")).done(function(data){

        var $table = $("body").find("tbody");
        $.each(data.data,function(i,e){

            var tr = $("<tr><td>"+(i+1)+"</td>" +
                "<td>"+e.fileName+"</td>" +
                "<td>"+e.size+"</td>" +
                "<td><a href='"+contextPath+"system/download/log/"+e.uuid+"' class='btn btn-turquoise'>下载</a></td>" +
                "</tr>");
            $table.append(tr);
        });
    });


    function isAgentId(str){

        var reg = /[a-zA-Z0-9]+/;
        return reg.test(str);
    }

    function isStr(str){

        var reg = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
        return reg.test(str);
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