jQuery(function(){


    var DEVICE_NORMARL = contextPath+"/static/images/server/server-normal.svg",
        DEVICE_WARNING = contextPath+"/static/images/server/server-warning.svg",
        DEVICE_ERROR = contextPath+"/static/images/server/server-error.svg",
        START_MESSAGE = "信息未上报",
        ERROR_STATUS = "error",
        ERROR_COLOR = "#cc3f44",
        NORMAL_COLOR = "#8dc63f",
        WARNING_COLOR = "#ffba00";

    var opts = {
        "closeButton": true,
        "debug": false,
        "positionClass": "toast-bottom-right",
        "onclick": null,
        "showDuration": "10",
        "hideDuration": "500",
        "timeOut": "500",
        "extendedTimeOut": "100",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };


    var linkStyle2 = [
        {
            selector: 'node[zone="no"]',
            style:{

                "label": "data(name)",
                "text-valign":"bottom",
                "text-halign":"center",
                "width":48,
                "height":48,
                'background-image':DEVICE_WARNING,
                'background-color':'#fff',
                'border-color': '#fff',
                'border-width': 1,
                'border-opacity': 0.5,
                'background-opacity':0,
                'color':"#14d1da"
            }
        },
        {
            selector: 'edge.taxi',
            style: {
                'line-color': '#ffba00',
                "curve-style": "taxi",
                'target-arrow-shape': 'triangle',
                'target-arrow-color': '#ffba00',
                'taxi-direction':'auto'
            }
        },{
            selector: 'edge.taxi42',
            style: {
                'line-color': '#ffba00',
                "curve-style": "taxi",
                'target-arrow-shape': 'triangle',
                'source-arrow-shape': 'triangle',
                'target-arrow-color': '#ffba00',
                'source-arrow-color': '#ffba00',
                'taxi-direction':'auto'
            }
        },
        {
            selector: 'edge.straight',
            style: {
                "curve-style": "straight",
                'line-color': '#ffba00',
                'target-arrow-color': '#ffba00',
                'target-arrow-shape': 'triangle'
            }
        },
        {
            selector: 'edge.straight42',
            style: {
                "curve-style": "straight",
                'line-color': '#ffba00',
                'target-arrow-color': '#ffba00',
                'source-arrow-color': '#ffba00',
                'target-arrow-shape': 'triangle',
                'source-arrow-shape': 'triangle'
            }
        },
        {
            selector: 'edge.unbundled-bezier',
            style: {
                "curve-style": "unbundled-bezier",
                'line-color': '#ffba00',
                'target-arrow-color': '#ffba00',
                'target-arrow-shape': 'triangle'
            }
        },
        {
            selector: 'edge.unbundled-bezier42',
            style: {
                "curve-style": "unbundled-bezier",
                'line-color': '#ffba00',
                'target-arrow-color': '#ffba00',
                'source-arrow-color': '#ffba00',
                'target-arrow-shape': 'triangle',
                'source-arrow-shape': 'triangle'
            }
        },{

            selector: 'node[zone="yes"]',
            style:{
                "label": "data(name)",
                'shape': 'roundrectangle',
                'text-valign': 'top',
                'height': 'auto',
                'width': 'auto',
                'background-color': 'data(backgroundColor)',
                'background-opacity': 0.333,
                'color': '#14d1da',
                'text-outline-width':0,
                'font-size': 20
            }
        }
    ];

    /**
     * 初始化
     */
    var cy = cytoscape({
        container:document.getElementById('cy'),
        style:linkStyle2,
        layout: {
            name: 'preset'
        },
        userZoomingEnabled:true,
        userPanningEnabled:true,
        zoom:0.6,pan:{x:0,y:0},
        maxZoom:2,
        minZoom:0.5,
        pixelRatio:'1.0',
        touchTapThreshold: 8,
        pan:'fit'

    });
    function copsecStatus(k,v){

        this.key = k;
        this.v  = v;
    }
    var statusMap = new Array() ;

    /**
     * 初始化绘制拓扑
     */
    cy.ready(function(){

        $.ajax({

            url:contextPath+'node/get',
            method:'GET',
            dataType:'json',
            success:function(data){

                if(data.code == 200){
                    cy.add(data.data);
                    cy.nodes().each(function (e,i) {

                        if(e.isChild()){

                            showStatus(e,START_MESSAGE);
                        }
                    });

                }else{

                    toastr.error("加载设备失败", opts);
                }
            }
        });

    });


    /**
     * 连接选择样式设置
     */
    cy.on('click','edge',function(evt){

        var node = evt.target;
        node.style("line-color","#939002");
        node.style('target-arrow-color','#939002');
    });
    /**
     * 节点选择样式设置
     */
    cy.on('click','node',function(evt){
        var node = evt.target;
        node.style("border-color","#939002");
        node.style("border-width",5);
        var tip = getDevicePopperById(evt.target.data('id'));
        if(tip && !tip.state.isShown){

            tip.show();
        }
    });

    cy.on('unselect','edge',function(evt){
        evt.target.style("line-color","#ffba00");
        evt.target.style('target-arrow-color','#ffba00');
    });

    cy.on('unselect','node',function(evt){

        evt.target.style("border-color","#fff");
        evt.target.style("border-width",1);
        var tip = getDevicePopperById(evt.target.data('id'));
        if(tip){

            if(tip.state.isShown){

                tip.hide();
            }
        }

    });
    var p = new Object();
    cy.on('grabon','node',function(evt){

        var node = evt.target;
        p.x = node.position("x");
        p.y = node.position("y");
        var tip = getDevicePopperById(node.data('id'));
        if(tip){
            if(tip.state.isShown){

                tip.hide();
            }
        }
    })

    /*cy.on('dragfreeon','node',function(evt){

        var node = evt.target;
        if((node.position("x") < 0  || node.position("x") > cy.width()) ||
            (node.position("y") < 0 || node.position("y") > cy.height())){

            toastr.error("设备超出可见范围!");
            evt.target.position({x:p.x,y:p.y});
        }
    });*/

    $("body").on('click','.vertical-top button',function(){

        if($(this).parent().hasClass('open')){

            $(this).parent().removeClass('open');
        }else{

            $('.btn-group').removeClass('open');
            $(this).parent().addClass('open');
        }
    });


    function isEmpty(str){

        if(str == "" || !str){

            return true;
        }
        return false;
    }

    function isNumber(num){

        if($.isNumeric(num)){

            return true;
        }
        return false;
    }

    function getNode(){

        var node = cy.$(":selected");
        if(node.length == 0){

            toastr.error("请选择设备或网络区域",opts);
            return false;
        }
        return node;
    }

    function getEdges(){

        var links = cy.$(":selected");
        if(links.length == 0){

            toastr.error("请选择连接",opts);
            return false;
        }
        return links;
    }

    function addData(start,end,isAdd,link) {

        start.children().remove();
        end.children().remove();
        if (isAdd && !end.attr('multiple')) {

            end.attr('multiple', true);
        }
        if (!isAdd && end.attr('multiple')) {

            end.removeAttr('multiple');
        }
        if (isAdd) {

            cy.nodes().forEach(function (e, i) {

                start.append("<option value=" + e.data('id') + ">" + e.data('name') + "</option>");
                end.append("<option value=" + e.data('id') + ">" + e.data('name') + "</option>");
            });
        }else{

            cy.nodes().forEach(function(e,i){

                if (e.data('id') == link.data('source')){

                    start.append("<option value="+e.data('id')+" selected>"+e.data('name')+"</option>");
                    start.trigger('change');
                }
                if(e.data('id') == link.data('target')){

                    end.append("<option value="+e.data('id')+" selected>"+e.data('name')+"</option>");
                    end.trigger('change');
                }else{

                    end.append("<option value="+e.data('id')+">"+e.data('name')+"</option>");
                }

            });
        }
        start.select2(
            {
                placeholder: '选择起始设备',
                allowClear: true}
        ).on('select2-open',function(){

            $(this).data('select2').results.addClass('overflow-hidden').perfectScrollbar();
        });

        end.select2({
            placeholder: '选择终点设备',
            allowClear: true
        }).on('select2-open', function()
        {
            // Adding Custom Scrollbar
            $(this).data('select2').results.addClass('overflow-hidden').perfectScrollbar();
        });
    }

    /*
    *
    * 用于显示设备状态信息，并保存当前设备状态与tip之间的关系
    *
    * */
    function showStatus(node,text){

        var tip =  tippy( node.popperRef(), {
            content: function(){
                var div = document.createElement('div');
                div.innerHTML = text;
                return div;
            },
            trigger: 'manual',
            arrow: true,
            placement: 'top',
            hideOnClick: false,
            multiple: true,
            sticky: true,
            updateDuration:30,
        } );
        statusMap.push(new copsecStatus(node.data('id'),tip));
        tip.hide();
    }

    /**
     * 更新设备状态
     */
    function updateStatus(node,status_color,message){

        var x = getDevicePopperById(node.data('id'));
        if(x){

            $(x.popperChildren.tooltip).css('background-color',status_color);
            $(x.popperChildren.content).css('background-color',status_color);
            $(x.popperChildren.arrow).css('border-top','8px solid '+status_color);
            var $content = $(x.popperChildren.tooltip);
            $content.find('div').last().children().remove();
            $content.find('div').last().text("");
            $content.find('div').last().append(message);
            $content.find('div').last().find('table tr td').css('text-align','left');
            var ca = node.connectedEdges();
            if(ca.length > 0){

                for(var i=0;i< ca.length;i++){

                    var line = cy.$id(ca[i].data("id"));
                    line.style("line-color",status_color);
                    line.style("target-arrow-color",status_color);
                    line.style("source-arrow-color",status_color);
                }
            }
        }

    }

    function getDevicePopperById(id){

        for(var i=0;i< statusMap.length;i++){

            if(statusMap[i].key == id){

                return statusMap[i].v;
            }
        }
    }

    var getStatus = function getDeviceStatus(){

        $.ajax({

            url:contextPath+'node/status/'+$.now(),
            method:'GET',
            dataType:'json',
            success:function(data){

                if(data.code = 200){
                    var m = data.data;
                    for(var k in m){

                        var v = m[k];
                        if(v.list.length > 0){
                            var _text = "";
                            _text = getStatusText(v.list);
                            if(v.status == ERROR_STATUS){

                                if(cy.$id(k).data('name') != undefined){

                                    toastr.error(cy.$id(k).data('name')+" 设备状态异常!");
                                    updateStatus(cy.$id(k),ERROR_COLOR,_text);
                                    updateNode(cy.$id(k),DEVICE_ERROR);
                                }
                            }else{

                                updateStatus(cy.$id(k),NORMAL_COLOR,_text);
                                updateNode(cy.$id(k),DEVICE_NORMARL);
                            }
                        }
                    }
                }
            },
            error:function(ajq,status,error){

                toastr.error("请求错误或超时");
            }
        });
    };

    /**
     * 获取历史文件信息
     * @param str
     * @returns {boolean}
     */
    var getFileHistoryStatus =  function getFileHistory(){

        $.ajax({
            url:contextPath+"statistics/today/files",
            data:{"title":$.now()},
            method:"POST",
            dataType:"json",
            success:function(data){

                if(data.code == 200){

                    updateFileStatus(data.data);
                    return data.data;
                }else{

                    return null;
                }
            },
            error:function(jqx,status,error){

                toastr.error("请求错误或超时");
            }
        })
    }

    function updateFileStatus(data){

        var i = 0;
        var $table = $("table.table").first().find("tbody");
        $table.children().remove();
       $.each(data,function(k,v){
           if(i>=3){

              return false;
           }
           var tr = $("<tr class='dark'><td><a style='color:#14d1da ' href='"+contextPath+"statistics/fileHistoryDetail'>"+v.taskName+"</a></td><td>"+v.days+"</td><td>"+(v.gatherCountToday+v.storageCountToday)+"</td><td>"+(v.dataSizeTodayStr==null?0:v.dataSizeTodayStr)+"</td>" +
               "<td>"+(v.gatherCountTotal+v.storageCountTotal)+"</td><td>"+v.dataSizeTotalStr+"</td></tr>");
           $table.append(tr);
       });
    }

    function updateNode(node,device){

        node.style('background-image',device);
    }

    function getStatusText(status){

        var text = "<table class='table table-condensed'>";
        if(status.length == 1){

            text += "<tr><td>ID:</td><td>"+status[0].deviceId + "</td></tr>";
            text += "<tr><td>状态:</td><td>"+status[0].deviceStatus + "</td></tr>";
            text += "<tr><td>速率:</td><td>"+status[0].netSpeed + "</td></tr>";
            text += "<tr><td>cpu:</td><td>"+status[0].cpuUseRate + "</td></tr>";
            text += "<tr><td>内存:</td><td>"+status[0].memoryUseRate + "</td></tr>";
            text += "<tr><td>信息:</td><td>"+status[0].warnMessage + "</td></tr></table>";
        }else if(status.length == 2){

            if(status[0].deviceId.indexOf("-inner") >= 0 ){

                text += "<tr><td>ID:</td><td>"+status[1].deviceId + "</td><td>"+status[0].deviceId + "</td></tr>";
                text += "<tr><td>状态:</td><td>"+status[1].deviceStatus + "</td><td>"+status[0].deviceStatus + "</td></tr>";
                text += "<tr><td>速率:</td><td>"+status[1].netSpeed + "</td><td>"+status[0].netSpeed + "</td></tr>";
                text += "<tr><td>cpu:</td><td>"+status[1].cpuUseRate + "</td><td>"+status[0].cpuUseRate + "</td></tr>";
                text += "<tr><td>内存:</td><td>"+status[1].memoryUseRate + "</td><td>"+status[0].memoryUseRate + "</td></tr>";
                text += "<tr><td>信息:</td><td>"+status[1].warnMessage + "</td><td>"+status[0].warnMessage + "</td></tr></table>";
            }else{

                text += "<tr><td>ID:</td><td>"+status[0].deviceId + "</td><td>"+status[1].deviceId + "</td></tr>";
                text += "<tr><td>状态:</td><td>"+status[0].deviceStatus + "</td><td>"+status[1].deviceStatus + "</td></tr>";
                text += "<tr><td>速率:</td><td>"+status[0].netSpeed + "</td><td>"+status[1].netSpeed + "</td></tr>";
                text += "<tr><td>cpu:</td><td>"+status[0].cpuUseRate + "</td><td>"+status[1].cpuUseRate + "</td></tr>";
                text += "<tr><td>内存:</td><td>"+status[0].memoryUseRate + "</td><td>"+status[1].memoryUseRate + "</td></tr>";
                text += "<tr><td>信息:</td><td>"+status[0].warnMessage + "</td><td>"+status[1].warnMessage + "</td></tr></table>";
            }
        }
        return text;
    }

    function isId(str){

        var reg = /[a-zA-Z0-9]+/;
        return reg.test(str);
    }

    function formatSize( size, pointLength, units ) {
        var unit;

        units = units || [ 'B/s', 'K/s', 'M/s', 'G/s', 'TB/s' ];

        while ( (unit = units.shift()) && size > 1024 ) {
            size = size / 1024;
        }

        return (unit === 'B/s' ? Number(size).toFixed( pointLength || 2 ) : Number(size).toFixed( pointLength || 2 )) +
            unit;
    }

    var labelArrays = ["label-warning","label-secondary","label-userDefined11","label-danger",
        "label-purple","label-red","label-yellow","label-orange",
        "label-success","label-turquoise","label-userDefined10","label-userDefined1",
        "label-userDefined2","label-userDefined3","label-userDefined4","label-userDefined5",
        "label-userDefined6","label-userDefined7","label-userDefined8","label-userDefined9"];


    var _netParam = {"statistiscalType":"net"},_fileParam = {"statistiscalType":"file"},
        _proParam = {"statistiscalType":"protocol"},_dbParam = {"statistiscalType":"db"};

    var fileTotal = function fileTotal(){

        $.ajax({
            url:contextPath+ "statistics/total/day",
            method:'POST',
            dataType:'json',
            data:_fileParam,
            success:function(data){

                if(data.code ==200){

                    updateSum($($(".xe-widget")[0]),data.data);
                }else{

                    toastr.error(data.message);
                }

            }
        })
    }

    var totalNet = function totalNetInfo(){

        $.ajax({
            url:contextPath+ "statistics/total/day",
            method:'POST',
            dataType:'json',
            data:_netParam,
            success:function(data){

                if(data.code == 200){

                    updateSum($($(".xe-widget")[1]), data.data);
                }
            }
        })
    }

    var totalDb = function totalDbInfo(){

        $.ajax({
            url:contextPath+ "statistics/total/day",
            method:'POST',
            dataType:'json',
            data:_proParam,
            success:function(data){
                if(data.code == 200){

                    updateSum($($(".xe-widget")[3]), data.data);
                }else{

                    toastr.error(data.message);
                }
            }
        })
    }

    var totalProtocol = function totalProcotol(){

        $.ajax({
            url:contextPath+ "statistics/total/day",
            method:'POST',
            dataType:'json',
            data:_proParam,
            success:function(data){

                if(data.code == 200){

                    updateSum($($(".xe-widget")[2]), data.data);
                }
            }
        });
        $("#cy").css("height",$(".col-sm-2").css("height"));
    }

    setInterval(fileTotal,2000);

    setInterval(totalNet,2000);

    setInterval(totalDb,2000);

    setInterval(totalProtocol,2000);

    function formatSize2( size, pointLength, units ) {

        if(size == 0){

            return "0B";
        }
        var unit =  units || [ 'B', 'K', 'M', 'G', 'TB' ];

        while ( (unit = units.shift()) && size > 1024 ) {
            size = size / 1024;
        }

        return (unit === 'B' ? Number(size).toFixed( pointLength || 2 ) : Number(size).toFixed( pointLength || 2 )) +
            unit;
    }

    function updateSum(panel,data){

        panel.attr("data-to",data.dataSumTotal);
        panel.find("strong.num").text(formatSize2(data.dataSumTotal,2 ,['k','B','M', 'G', 'TB']));

        panel.find(".xe-lower strong").first().text(data.dataMax);
        panel.find(".xe-lower strong").last().text(data.dataMin);
    }

    setInterval(getStatus,2000);

    setInterval(getFileHistoryStatus,2000);
});

