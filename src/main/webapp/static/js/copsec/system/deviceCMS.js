jQuery(function(){

    var DEVICE_NORMARL = contextPath+"static/images/server/kebo/server-32n1.svg",
        DEVICE_WARNING = contextPath+"static/images/server/kebo/server-32n1.svg",
        DEVICE_ERROR = contextPath+"/static/images/server/kebo/server-32e1.svg",
        START_MESSAGE = "信息未上报",
        NORMAL_STATUS = "normal",
        ERROR_STATUS = "error",
        ERROR_COLOR = "#cb5353",
        NORMAL_COLOR = "#5ab95d",
        WARNING_COLOR = "#c4b500",
        RUNNING_COLOR = "rgb(0, 209, 255)";

    var SWITCH_NORMAL = contextPath+"static/images/server/switch/switch-32-n1.svg",
        SWITCH_WARNING = contextPath+"static/images/server/switch/switch-32-n1.svg",
        SWITCH_ERROR = contextPath+"static/images/server/switch/switch-32-e1.svg",
        WIN_NORMAL = contextPath+"static/images/server/windows/win-32-n1.svg",
        WIN_WARNING = contextPath+"static/images/server/windows/win-32-n1.svg",
        WIN_ERROR = contextPath+"static/images/server/windows/win-32-e1.svg",
        NET_NORMAL = contextPath+"static/images/server/others/net-32-n1.svg",
        NET_WARNING = contextPath+"static/images/server/others/net-32-n1.svg",
        NET_ERROR = contextPath+"static/images/server/others/net-32-e1.svg",
        FIREWALL_NORMAL =  contextPath+"static/images/server/firewall/firewall-32n1.svg",
        FIREWALL_WARNING =  contextPath+"static/images/server/firewall/firewall-32n1.svg",
        FIREWALL_ERROR =  contextPath+"static/images/server/firewall/firewall-32e1.svg",
        DX_NORMAL = contextPath+"static/images/server/kebo/dx-32n.svg",
        DX_ERROR = contextPath+"static/images/server/kebo/dx-32e.svg";
    var numberUtils = new FormatNumber();
    numberUtils.init({decimal:0});


    var opts = {
        "closeButton": true,
        "debug": false,
        "positionClass": "toast-bottom-right",
        "onclick": null,
        "showDuration": "150",
        "hideDuration": "800",
        "timeOut": "1000",
        "extendedTimeOut": "800",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };


    var linkStyle2 = [
        {
            selector: 'node[zone="no"][serverType="kebo"]',
            style:{

                "label": "data(name)",
                "text-valign":"bottom",
                "text-halign":"center",
                "width":50,
                "height":50,
                'background-image':DEVICE_WARNING,
                'background-color':'#ffffff',
                'border-color': '#ffffff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity':0,
                'color':"#ffffff",
                'shape':'rectangle',
                'font-size':12
            }
        },
        {
            selector: 'node[zone="no"][serverType="switch"]',
            style:{

                "label": "data(name)",
                "text-valign":"bottom",
                "text-halign":"center",
                "width":50,
                "height":40,
                'background-image':SWITCH_WARNING,
                'background-color':'#ffffff',
                'border-color': '#ffffff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity':0,
                'color':"#ffffff",
                'shape':'rectangle',
                'font-size':12
            }
        },
        {
            selector: 'node[zone="no"][serverType="win"]',
            style:{

                "label": "data(name)",
                "text-valign":"bottom",
                "text-halign":"center",
                "width":50,
                "height":50,
                'background-image':WIN_WARNING,
                'background-color':'#ffffff',
                'border-color': '#ffffff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity':0,
                'color':"#ffffff",
                'shape':'rectangle',
                'font-size':12
            }
        },
        {
            selector: 'node[zone="no"][serverType="net"]',
            style:{

                "label": "data(name)",
                "text-valign":"bottom",
                "text-halign":"center",
                "width":50,
                "height":50,
                'background-image':NET_WARNING,
                'background-color':'#ffffff',
                'border-color': '#ffffff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity':0,
                'color':"#ffffff",
                'shape':'rectangle',
                'font-size':12
            }
        },
        {
            selector: 'node[zone="no"][serverType="firewall"]',
            style:{

                "label": "data(name)",
                "text-valign":"bottom",
                "text-halign":"center",
                "width":50,
                "height":50,
                'background-image':FIREWALL_WARNING,
                'background-color':'#ffffff',
                'border-color': '#ffffff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity':0,
                'color':"#ffffff",
                'shape':'rectangle',
                'font-size':12
            }
        },
        {
            selector: 'node[zone="no"][serverType="dx"]',
            style:{

                "label": "data(name)",
                "text-valign":"bottom",
                "text-halign":"center",
                "width":50,
                "height":50,
                'background-image':DX_NORMAL,
                'background-color':'#ffffff',
                'border-color': '#ffffff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity':0,
                'color':"#ffffff",
                'shape':'rectangle',
                'font-size':12
            }
        },
        {
            selector: '.point',
            style:{
                "width":6,
                "height":6,
                'background-color':'#3fff07',
                'border-color': '#3eff10',
                'border-width': 1,
                'border-opacity': 1,
                'background-opacity':1,
                'shape':'ellipse',
            }
        },{
            selector: '.pointError',
            style:{
                "width":6,
                "height":6,
                'background-color':'#ff1908',
                'border-color': '#ff100a',
                'border-width': 1,
                'border-opacity': 1,
                'background-opacity':1,
                'shape':'ellipse',
            }
        },
        {
            selector: 'edge.taxi',
            style: {
                'background-color': '#120dfd',
                'line-style':'dashed',
                'curve-style':'haystack',
                'width':2,
                'target-arrow-color': '#a4e30a',
                'line-fill':'linear-gradient',
                'line-dash-pattern':[8,3],
                'line-dash-offset':-64,
                'line-cap':'round',
                'line-gradient-stop-colors':'cyan',
                'line-gradient-stop-positions':'100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function':'ease'
            }
        },{
            selector: 'edge.taxi42',
            style: {
                'line-color': '#fff000',
                "curve-style": "taxi",
                'line-style':'dashed',
                'width':2,
                'target-arrow-color': '#a4e30a',
                'line-fill':'linear-gradient',
                'line-dash-pattern':[8,3],
                'line-dash-offset':-64,
                'line-cap':'round',
                'line-gradient-stop-colors':'cyan',
                'line-gradient-stop-positions':'100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function':'ease'
            }
        },
        {
            selector: 'edge.straight',
            style: {
                'background-color': '#120dfd',
                'curve-style':'haystack',
                'line-style':'dashed',
                'width':2,
                'target-arrow-color': '#a4e30a',
                'line-fill':'linear-gradient',
                'line-dash-pattern':[8,3],
                'line-dash-offset':-64,
                'line-cap':'round',
                'line-gradient-stop-colors':'cyan',
                'line-gradient-stop-positions':'100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function':'ease'
            }
        },
        {
            selector: 'edge.straight42',
            style: {
                'curve-style':'haystack',
                'line-style':'dashed',
                'width':2,
                'target-arrow-color': '#a4e30a',
                'line-fill':'linear-gradient',
                'line-dash-pattern':[8,3],
                'line-dash-offset':-64,
                'line-cap':'round',
                'line-gradient-stop-colors':'cyan',
                'line-gradient-stop-positions':'100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function':'ease'
            }
        },
        {
            selector: 'edge.unbundled-bezier',
            style: {
                'curve-style':'haystack',
                'line-style':'dashed',
                'width':2,
                'target-arrow-color': '#a4e30a',
                'line-fill':'linear-gradient',
                'line-dash-pattern':[8,3],
                'line-dash-offset':-64,
                'line-cap':'round',
                'line-gradient-stop-colors':'cyan',
                'line-gradient-stop-positions':'100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function':'ease'
            }
        },
        {
            selector: 'edge.unbundled-bezier42',
            style: {
                'curve-style':'haystack',
                'line-style':'dashed',
                'width':2,
                'target-arrow-color': '#a4e30a',
                'line-fill':'linear-gradient',
                'line-dash-pattern':[8,3],
                'line-dash-offset':-64,
                'line-cap':'round',
                'line-gradient-stop-colors':'cyan',
                'line-gradient-stop-positions':'100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function':'ease'
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
                'color': '#ffffff',
                'text-outline-width':0,
                'font-size': 12
            }
        },{
            selector:('.highlightedIn'),
            style:{
                'background-color': '#120dfd',
                'line-style':'dashed',
                'curve-style':'haystack',
                'width':2,
                'line-height':2,
                'target-arrow-color': '#120dfd',
                'line-fill':'linear-gradient',
                'line-dash-pattern':[6,3],
                'line-dash-offset':-48,
                'line-cap':'round',
                'line-gradient-stop-colors':'cyan',
                'line-gradient-stop-positions':'100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function':'ease-in'
            }
        },{
            selector:('.highlightedError'),
            style:{
                'background-color': '#fc270c',
                'line-style':'dashed',
                'curve-style':'haystack',
                'width':2,
                'target-arrow-color': '#fc270c',
                'line-fill':'linear-gradient',
                'line-dash-pattern':[6,3],
                'line-dash-offset':-48,
                'line-cap':'round',
                'line-gradient-stop-colors':'#fc270c',
                'line-gradient-stop-positions':'100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function':'ease-in'
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
            name: 'grid',
            fit:true,
        },
        userZoomingEnabled:false,
        userPanningEnabled:false,
        zoom:1,pan:{x:0,y:0},
        maxZoom:5,
        minZoom:0.5,
        pixelRatio:'1.0',
        touchTapThreshold: 8,
    });

   /* cy.panzoom();*/

    function copsecStatus(k,v){

        this.key = k;
        this.v  = v;
    }
    var statusMap = new Array() ;

    $.when($.ajax({

        url:contextPath+'node/get',
        method:'GET',
        dataType:'json',
        syn:true
    })).done(function(data){

        if(data.code == 200){
            cy.add(data.data);
            cy.nodes().each(function (e,i) {

                if(e.data("zone")=="no"){

                    showStatus(e,START_MESSAGE);
                }
            });

        }else{

            toastr.error("加载设备失败","系统提示",opts);
        }
    });

    $.when($.ajax({
        url:contextPath + "node/typeNames/get",
        dataType:'json',
        method:'GET'})).done(function(data){
        $.each(data.data,function(index,e){
            var option = $("<option value='"+index+"'>"+e+"</option>");
            $selectType.append(option);

        });
    });

    var $typeSelectRow = $($("#deviceModal").find(".modal-body").find(".row:nth-child(6) div.row")[4]);
    var $selectType = $typeSelectRow.find("select");

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
        node.style("border-width",3);
        var tip = getDevicePopperById(evt.target.data('id'));
        if(tip && !tip.state.isShown){

            //tip.show();
        }
    });

    cy.on('unselect','edge',function(evt){
        evt.target.style("line-color","#fff000");
        evt.target.style('target-arrow-color','#fff000');
    });

    cy.on('unselect','node',function(evt){

        evt.target.style("border-color","#fff");
        evt.target.style("border-width",0);
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

    cy.on('dragfreeon','node',function(evt){

        var node = evt.target;
        if((node.position("x") < 0  || node.position("x") > cy.width()) ||
            (node.position("y") < 0 || node.position("y") > cy.height())){

            toastr.error("设备超出可见范围!");
            evt.target.position({x:p.x,y:p.y});
        }else{

            updateTopology();
        }
    });

    $("body").on('click','.col-sm-10 vertical-top button',function(){

        if($(this).parent().hasClass('open')){

            $(this).parent().removeClass('open');
        }else{

            $('.btn-group').removeClass('open');
            $(this).parent().addClass('open');
        }
    });
    $linkType = $("#linkModal").find(".row:nth-child(3)");
    $linkType.on('change',function () {

        var x = $linkType.find('option:selected').val();
        $linkType.children().each(function (e,i) {

            if ($(this).val() == x){

                $(this).attr('selected',true);
            }else{

                $(this).removeAttr('selected');
            }
        });
    });

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

                                    //toastr.error(cy.$id(k).data('name')+" 设备状态异常!","系统提示",opts);
                                    updateStatus(cy.$id(k),ERROR_COLOR,_text);
                                    updateNode(cy.$id(k),ERROR_STATUS);
                                    updateEdeges(cy.$id(k),"highlightedError");
                                }
                            }else{

                                updateStatus(cy.$id(k),NORMAL_COLOR,_text);
                                updateNode(cy.$id(k),NORMAL_STATUS);
                                updateEdeges(cy.$id(k),"highlightedIn");
                            }
                        }
                    }
                }
            },
            error:function(ajq,status,error){

                toastr.error("请求错误或超时","系统提示",opts);
            }
        });
    }

    function updateNode(node,status){

        if(node.data("serverType") == "kebo"){

            if(status == NORMAL_STATUS){

                node.style('background-image',DEVICE_NORMARL);
            }else if(status == ERROR_STATUS){

                node.style('background-image',DEVICE_ERROR);
            }
        }else if(node.data("serverType") == "switch"){

            if(status == NORMAL_STATUS){

                node.style('background-image',SWITCH_NORMAL);
            }else if(status == ERROR_STATUS){

                node.style('background-image',SWITCH_ERROR);
            }
        }else if(node.data("serverType") == "win"){

            if(status == NORMAL_STATUS){

                node.style('background-image',WIN_NORMAL);
            }else if(status == ERROR_STATUS){

                node.style('background-image',WIN_ERROR);
            }
        }else if(node.data("serverType") == "net"){

            if(status == NORMAL_STATUS){

                node.style('background-image',NET_NORMAL);
            }else if(status == ERROR_STATUS){

                node.style('background-image',NET_ERROR);
            }
        }else if(node.data("serverType") == "firewall"){

            if(status == NORMAL_STATUS){

                node.style('background-image',FIREWALL_NORMAL);
            }else if(status == ERROR_STATUS){

                node.style('background-image',FIREWALL_ERROR);
            }
        }else if(node.data("serverType") == "dx"){

            if(status == NORMAL_STATUS){

                node.style('background-image',DX_NORMAL);
            }else if(status == ERROR_STATUS){

                node.style('background-image',DX_ERROR);
            }
        }

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

    function formatSize( size, pointLength, units ) {

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

    setInterval(getStatus,2000);

    $("span.num-box").text(moment().format("YYYY-MM-DD"));
    if($("div.content_center")){

        $("#cy").css("height",$("div.content_center").css("height"));
    }
    var config = {

        type:'line',
        data:{
            labels:"",
            datasets:[{
                label: '今日网络流量',
                backgroundColor: Samples.utils.transparentize(window.chartColors.blue),
                borderColor:'#ffffff',
                fill: true,
                cubicInterpolationMode: 'monotone',
                data: []
            }]
        },
        options:{
            responsive: true,
            legend:{
                labels: {
                    boxWidth: 12,
                    fontColor:'#ffffff',
                }
            },
            tooltips: {
                mode: 'point',
                intersect:false,
                callbacks:{

                    label: function(tooltipItem, data) {

                        var label = data.datasets[tooltipItem.datasetIndex].label || '';
                        if (label) {
                            label += '总流量: ';
                        }
                        console.log(tooltipItem.yLabel);
                        label += formatSize(tooltipItem.yLabel,2,['k','B','M', 'G', 'TB']);
                        return label;
                    }
                }
            },
            scales: {
                xAxes: [{
                    display:true,
                    type:'realtime',
                    realtime: {
                        duration: 5000,
                        refresh: 1000,
                        delay: 500,
                        displayFormats:"second",
                        onRefresh: onRefresh
                    },
                    ticks: {
                        fontColor: "#ffffff",
                    }
                }],
                yAxes: [{
                    display: true,
                    fontColor:'#ffffff',
                    ticks:{
                        callback: function(value, index, values){

                            var v = 0;
                            if(value <= 1000*1000){//B
                                return null;
                            }else if(value >= 1000*1000 && value < 1000*1000*1000){
                                v = (value /(1000 * 1000));
                                return  v % 500 == 0 ? v + "M": null ;
                            }else if(value >= 1000 * 1000 * 1000  && value < 1000*1000*1000*1000){
                                v = (value /(1000 * 1000 *1000));
                                return v % 500 == 0 ? v + "G":null;
                            }else if(value >= 1000* 1000* 1000 * 1000 && value < 1000*1000*1000*1000 * 1000){
                                v = (value /(1000 * 1000 * 1000 * 1000));
                                return v / 10 == 2 ? v+ "T" : null;
                            }
                            return value;
                        },
                        fontColor:'#ffffff'
                    },
                    scaleLabel: {
                        display: true,
                        labelString: '流量值',
                        fontColor:'#ffffff'
                    },
                    gridLines:{

                        drawBorder: false,
                        color:'rgb(0, 209, 255)'
                    }
                }]
            }
        }
    };
    var configForHours = {

        type:'line',
        data:{
            labels:"",
            datasets:""
        },
        options:{
            responsive: true,
            legend:{
                labels: {
                    boxWidth: 12,
                    fontColor:'#ffffff',
                }
            },
            tooltips: {
                mode: 'point',
                intersect:false,
                callbacks:{

                    label: function(tooltipItem, data) {
                        var label = data.datasets[tooltipItem.datasetIndex].label || '';

                        if (label) {
                            label += '总流量: ';
                        }
                        label += formatSize(tooltipItem.yLabel,2,['k','B','M', 'G', 'TB']);
                        return label;
                    }
                }
            },
            scales: {
                xAxes: [{
                    display: true,
                    scaleLabel: {
                        display: true
                    },
                    ticks: {
                        fontColor: "#ffffff",
                    }
                }],
                yAxes: [{
                    display: true,
                    type:'logarithmic',
                    ticks:{
                        callback: function(value, index, values){

                            var v = 0;
                            if(value <= 1000*1000){//B
                                return null;
                            }else if(value >= 1000*1000 && value < 1000*1000*1000){
                                v = (value /(1000 * 1000));
                                return  v % 500 == 0 ? v + "M": null ;
                            }else if(value >= 1000 * 1000 * 1000  && value < 1000*1000*1000*1000){
                                v = (value /(1000 * 1000 *1000));
                                return v % 500 == 0 ? v + "G":null;
                            }else if(value >= 1000* 1000* 1000 * 1000 && value < 1000*1000*1000*1000 * 1000){
                                v = (value /(1000 * 1000 * 1000 * 1000));
                                return v / 10 == 2 ? v+ "T" : null;
                            }
                            return value;
                        },
                        fontColor:'#ffffff',
                        fontSize:12
                    },
                    scaleLabel: {
                        display: false,
                        labelString: '流量值',
                        fontColor:"#ffffff",
                        fontSize:10
                    },
                    gridLines:{

                        drawBorder: false,
                        color:'rgb(0, 209, 255)'
                    }
                }]
            }
        }
    };
    var leftConfig ={

        type:'pie',
        data:"",
        labels:"",
        options: {
            legend:{
                display:false,
            },
            tooltips: {
                enabled: false,
            },
            responsive: true
        }
    }
    var rightConfig ={

        type:'pie',
        data:"",
        labels:"",
        options: {
            legend:{
                display:false,
            },
            tooltips: {
                enabled: false,
            },
            responsive: true
        }
    }
    var storageFileOptions = {
        title: {
            show:false,
            text: '',
            textStyle: {
                color: '#ffffff'
            }
        },
        tooltip : {
            show:false,
        },
        calculable : true,
        color:['#FD6035','#82C730','#FACE2F','#36B6DD'],
        series : [
            {
                type:'pie',
                radius : "100%",
                center: ['50%', '50%'],
                data:[],
                sort : 'ascending',
                roseType: 'radius',
                label: {
                    normal: {
                        show: false
                    },
                    emphasis: {
                        show: false
                    }
                },
                lableLine: {
                    normal: {
                        show: false
                    },
                    emphasis: {
                        show: false
                    }
                },
                itemStyle: {
                    normal: {
                        shadowBlur: 200,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                },
                animationType: 'scale',
                animationEasing: 'elasticOut',
                animationDelay: function (idx) {
                    return Math.random() * 200;
                }
            }
        ]
    };

    function onRefresh(chart) {
        $.ajax({
            url:contextPath+ "statistics/currentNet/data",
            method:'POST',
            dataType:"json",
            success:function(data){

                if(data.code == 200){

                    chart.config.data.datasets.forEach(function(dataset) {
                        dataset.data.push({
                            x: Date.now(),
                            y: data.data
                        });
                    });
                }
            }
        });
    }

    var lineCanvas = document.getElementById("netCanvas");
    if($(document).height() > 1000){

        lineCanvas.height = $("div.content_left_bototm").height()-125;
    }else{

        lineCanvas.height = $("div.content_left_bototm").height()-25;
    }

    var lineChar = new Chart(lineCanvas,configForHours),
        storagePie = echarts.init(document.getElementById("leftCanvas"));

        storagePie.setOption(storageFileOptions);
    $.when($.ajax({

        url:contextPath+"statistics/total/net",
        data:{"type":"hour"},
        method:'POST',
        dateType:'json'
    })).done(function(data){

        updateChar(lineChar,data.data);
    });

    var getNetTotal = function getNetTotalFunc(){

        $.ajax({

            url:contextPath+"statistics/total/net",
            data:{"type":"hour"},
            method:'POST',
            dateType:'json',
            success:function(data){

                if(data.code == 200){

                    updateChar(lineChar,data.data);
                }
            }
        })
    }

    setInterval(getNetTotal,1000 * 60 * 30);

    $.when($.ajax({

        url:contextPath + "statistics/currentFileSum/data",
        method:'POST',
        dataType:'json',
    })).done(function(data){

        if(data.code == 200){

            fillValues(data);
        }
    });

    $.when($.ajax({

        url:contextPath + "statistics/gatherValue/data",
        method:'POST',
        dataType:'json',
    })).done(function(data){

        if(data.code ==200){

            var $ul = $("ul.content_left_data_ul:nth-child(2)");
            filePrecents($ul,data,true);
        }
    });

    $.when($.ajax({

        url:contextPath + "statistics/storageValue/data",
        method:'POST',
        dataType:'json',
    })).done(function(data){

        if(data.code ==200){

            var $ul = $("ul.content_left_data_ul:nth-child(1)");
            filePrecents($ul,data,false);
        }
    });

    var taskNamesArray = new Array();
    $.when($.ajax(contextPath + "monitor/taskNames/get")).done(function(data){

        if(data.code == 200){

            $.each(data.data,function(index,e){

                var taskMap = new Object();
                taskMap.k = index;
                taskMap.v = e;
                taskNamesArray.push(taskMap);
            })
        }
    });

    function updateEchart(char,data){

        char.showLoading();
        char.hideLoading();
        char.setOption({

            series:[{data:data}]
        });
    }

    function updateChar(char,data){

        char.data = {labels:data.labels,datasets:data.datasets};
        char.update({duration: 2000,
            easing: 'easeInOutBounce'});
    }

    function filePrecents(ul,data,isGather){

        ul.find("li").remove();
        $.each(data.data,function(index,e){
            var name = "";
            if(e.taskName.length >= 20){

                name = e.taskName.substring(0,15)+".";
            }else{

                name = e.taskName;
            }
            var li = "";
            if(isGather){

                li = $("<li class='content_left_data_li'>" +
                    "<span class='content_left_data_span1' style='background-color:"+e.backGroudColor+"'></span>" +
                    "<span class='content_left_data_span2'>"+name+"</span>" +
                    "<span class='content_left_data_span3'>"+numberFormat(e.gatherCount)+"</span>" +
                    "<span class='content_left_data_span3'>"+e.precentGatherCount+"</span>" +
                    "</li>");
            }else{

                li = $("<li class='content_left_data_li'>" +
                    "<span class='content_left_data_span1' style='background-color:"+e.backGroudColor+"'></span>" +
                    "<span class='content_left_data_span2'>"+name+"</span>" +
                    "<span class='content_left_data_span3'>"+numberFormat(e.storageCount)+"</span>" +
                    "<span class='content_left_data_span3'>"+e.precentStorageCount+"</span>" +
                    "</li>");
            }

            li.hover(function(){

                li.find("span.content_left_data_span2").text(e.taskName);
            },function(){

                li.find("span.content_left_data_span2").text(name);
            });

            ul.append(li);
        });
    }

    function fillValues(data){

        var left = $("div.content_left_text").find("dl:nth-child(1)");
        var rigth = $("div.content_left_text").find("dl:nth-child(2)");
        left.find("dd").remove();
        rigth.find("dd").remove();
        left.append("<dd class='content_left_text_dd'>"+numberUtils.doFormat(data.data.storageCount) + "<span class='content_left_text_span'>个</span>");
        rigth.append("<dd class='content_left_text_dd'>"+numberUtils.doFormat(data.data.gatherCount) + "<span class='content_left_text_span'>个</span>");

    }

    var getRigthDate = function refreshPie2(){

        $.ajax({
            url:contextPath + "statistics/storagePie/data",
            method:'POST',
            dataType:'json',
            success:function(data){

                if(data.data.datasets.length > 0){

                    updateEchart(storagePie,data.data.datasets[0].data);
                }
            }
        });
    }

    var getCountdata = function refreshData(){

        $.ajax({
            url:contextPath + "statistics/currentFileSum/data",
            method:'POST',
            dataType:'json',
            success:function(data){

                if(data.code == 200){

                    fillValues(data);
                }
            }
        });
    }

    var getLeftValuas = function getValuesLeft(){

        $.ajax({
            url:contextPath + "statistics/gatherValue/data",
            method:'POST',
            dataType:'json',
            success:function(data){

                if(data.code == 200){

                    var $ul = $("ul.content_left_data_ul:nth-child(2)");
                    filePrecents($ul,data,true);
                }
            }
        });
    }

    var getRigthValuas = function getValuesRigth(){

        $.ajax({
            url:contextPath + "statistics/storageValue/data",
            method:'POST',
            dataType:'json',
            success:function(data){

                if(data.code == 200){

                    var $ul = $("ul.content_left_data_ul:nth-child(1)");
                    filePrecents($ul,data,false);
                }
            }
        });
    }

    var tasknames = new Array();
    var getAlarmData = function getAlarm(){



        $.ajax({
            url:contextPath +"statistics/alarm/search",
            method:"POST",
            data:{'size':19,'page':0},
            dataType:"json",
            success:function(data){
                var content_right = $("div.content_right_bototm");
                content_right.find("div.content_right_text").remove();
                if(data.data.length > 0){

                    tasknames = new Array()
                    $.each(data.data,function(index,e){

                        var v = getTaskNames(e);
                        tasknames.push(e);
                        var right_text = $("<div class='content_right_text'>" +
                            "<p class='content_right_text1' style='color:#ffffff'>"+v+"</p>" +
                            "<p class='content_right_text2' style='color:#ffffff'>告警</p>" +
                            "</div>");

                        content_right.append(right_text);
                    });
                }else{

                    var m = $("<div class='content_right_text'>" +
                        "<p class='content_right_text1' style='color:#ffffff'>无告警</p>" +
                        "<p class='content_right_text2' style='color:#ffffff'></p>" +
                        "</div>")
                    content_right.append(m);
                }
            }
        })
    }

    var getDeviceIds = function getDeviceIds(){

        if(tasknames.length > 0){

            $.ajax({

                url:contextPath + "statistics/alarm/devices",
                method:'POST',
                dataType:'json',
                contentType:"application/json;charset=utf-8",
                data:JSON.stringify(tasknames),
                success:function(data){

                    if(data.code == 200){

                        var a = new Array();
                        $.each(data.data,function(index,e){

                            var node = cy.nodes("node[id='"+e+"']");
                            node.connectedEdges().each(function(e,index){

                                e.flashClass('highlightedError',1000);
                            });
                        });
                    }
                }
            })
        }
    }

    function updateEdeges(node,className){

        node.connectedEdges().each(function(e,index){

            e.flashClass(className,1900);
        });
    }

    function updateTopology(){

        var positions = new Array();
        cy.nodes().forEach(function(e,i){

            var position = new Object();
            position.id = e.data("id");
            position.x = e.position("x");
            position.y = e.position("y");

            positions.push(position);
        });
        if(positions.length >0){

            $.ajax({

                url:contextPath+"node/topology/update",
                method:'POST',
                dataType:'json',
                contentType:"application/json;charset=utf-8",
                data:JSON.stringify(positions),
                success:function(data){

                    if(data.code != 200){

                        toastr.error(data.message);
                    }
                },
                error:function(jqx,status,error){

                    toastr.error(status + error);
                }
            });
        }
    }

    getAlarmData();

    setInterval(getDeviceIds,8000);

    setInterval(getAlarmData,8000);

    setInterval(getLeftValuas,8000);

    setInterval(getRigthValuas,8000);

    setInterval(getCountdata,8000);

    setInterval(getRigthDate,8000);

    function numberFormat(v){

        if(v == 0 || String(v).length < 3){

            return v;
        }else if(String(v).length == 3){

            return v;
        }else if(String(v).length == 4){

            v = v / 1000;
            return parseInt(v) + "千"
        }else if(String(v).length >=5){

            return parseInt(v/10000) + "万";
        }
    }

    var movePoint = function test(){

        cy.edges().each(function(e,index){

            var sourcePosition = e.sourceEndpoint();
            var targetPositio = e.targetEndpoint();

            var eNode;
            var id = e.data("id") + index;
            if(cy.$("#"+id).group()
                != "nodes" && e.hasClass("highlightedError")){
                eNode = {
                    group: 'nodes',
                    data: { id: id } ,
                    position:sourcePosition,
                    classes:"pointError"
                };
                cy.add(eNode);
            }else if(cy.$("#"+id).group()
                != "nodes" && !e.hasClass("highlightedError")){

                eNode = {
                    group: 'nodes',
                    data: { id: id } ,
                    position:sourcePosition,
                    classes:"point"
                };
                cy.add(eNode);
            }
            cy.$("#"+id).animate({
                    position: sourcePosition,
                    renderedPosition:targetPositio},
                {
                    easing:"ease-in-out-quart",
                    duration:3000,
                    complete:function(){

                        cy.remove("#"+e.data("id")+index);
                    }
                });
        });
    }
    setInterval(movePoint,3050);

    var getCpu = function cpu(){

        $.ajax({

            url:contextPath+"statistics/resources/cpu",
            dataType:"json",
            method:'GET',
            success:function(data){

                updateCpuAndMemory(data,$("div.content_rigth_title_right"),true);
            }
        });
    }

    getCpu();
    setInterval(getCpu,5000);
    var getMemory = function memory(){

        $.ajax({

            url:contextPath+"statistics/resources/memory",
            dataType:"json",
            method:'GET',
            success:function(data){

                updateCpuAndMemory(data,$("div.content_right_data2"),false);
            }
        });
    }
    getMemory();
    setInterval(getMemory,5000);

    function updateCpuAndMemory(data,parent,isCpu){

        $.each(data.data,function(index,e){

            var $li = $(parent.find("li.content_right_data_li")[index]);
            var deviceId = e.deviceId;
            var name = "";
            if(e.deviceId.indexOf("-inner") > 0 ){

                deviceId = e.deviceId.replace("-inner","");
                name = cy.$id(deviceId).data('name') + "-内端";
            }else{

                deviceId = e.deviceId;
                name = cy.$id(deviceId).data('name');
            }
            $($li.find("span")[1]).text(name);
            if(isCpu){

                $($li.find("span")[2]).text(parseInt(e.cpuUseRate) + "%");
                $li.find("div.progress-bar").css("width",parseInt(e.cpuUseRate)+ "%");
            }else{

                $($li.find("span")[2]).text(parseInt(e.memoryUseRate) + "%");
                $li.find("div.progress-bar").css("width",parseInt(e.memoryUseRate)+ "%");
            }
        });
    }

    function getTaskNames(k){

        var name = "";
        $.each(taskNamesArray,function(index,e){

           if(e.k == k){

               name = e.v;
               return false;
           }
        });
        if(name == ""){

            name = k;
        }
        return name;
    }
});

