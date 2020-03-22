jQuery(function(){

    var $body = $('body');
    var separator = ' - ';
    var _lables = "",_datasets = "";
    var DAY_TITLE = "近期数据汇总",MONTH_TILTE = "月度统计汇总",YEAR_TITLE = "年度统计汇总";
    var dateType = ["YYYY-MM-DD","YYYY-MM","YYYY"];
    var $sumPanel = $body.find(".panel");

    var $netBtn = $sumPanel.find("button:nth-child(1)"),
        $fileSumBtn = $sumPanel.find("button:nth-child(2)"),
        $fileTotalBtn = $sumPanel.find("button:nth-child(3)"),
        $dbSumBtn = $sumPanel.find("button:nth-child(4)"),
        $dbTotalBtn = $sumPanel.find("button:nth-child(5)"),
        $protocolSumBtn = $sumPanel.find("button:nth-child(6)"),
        $protocolTotalBtn = $sumPanel.find("button:nth-child(7)"),
        $searchBtn = $sumPanel.find("button").last(),
        $dataRange = $body.find(".daterangepicker").first();

    $.when($.ajax({
            url:contextPath+"/statistics/total/net",
            data:{'sum':true,"type":"day"},
            method:'POST',
            dateType:'json'}))
        .done(function(d1){
            if(d1.code == 200){
                updateChart(sumChart,d1.data);
            }
            $searchBtn.on('click',function(){

                var time = $(".daterange").first().find("span").text();
                if(time && time == "请选择日期"){

                    return false;
                }
                var timeStamp = time.split(separator);
                var _param = {"type":"day","start":timeStamp[0],"end":timeStamp[1]};
                searchDateByTime("/statistics/total/net",_param,"近期",sumChart,netOptions);
            });
        });

    var config = {

        type:'line',
        data:{

            labels:"",
            datasets:""
        },
        options:{
            responsive: true,
            title: {
                display: true,
                text: '近期数据流量汇总',
                fontSize:18
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
                        }
                    },
                    scaleLabel: {
                        display: true,
                        labelString: '流量值'
                    }
                }]
            }
        }
    };
    var netOptions = {
        responsive: true,
        title: {
            display: true,
            text: '近期数据流量汇总',
            fontSize:18
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
                    }
                },
                scaleLabel: {
                    display: true,
                    labelString: '流量值'
                }
            }]
        }
    };
    var fileSumOptions = {
        responsive: true,
        title: {
            display: true,
            text: '近期速率汇总',
            fontSize:18
        },
        tooltips: {
            mode: 'point',
            intersect:false,
            callbacks:{

                label: function(tooltipItem, data) {

                    var label = data.datasets[tooltipItem.datasetIndex].label || '';
                    if (label) {

                        label += '文件: ';
                        return label + tooltipItem.yLabel +"个";
                    }
                }
            }
        },
        scales: {
            xAxes: [{
                display: true,
                scaleLabel: {
                    display: true
                }
            }],
            yAxes: [{
                display: true,
                type:'logarithmic',
                ticks:{
                    callback: function(value, index, values){

                        var v = 0;
                        if(value < 100 ){

                            return null;
                        }else{
                            v = value / 100;
                            if( v == 1 || v == 50 || v == 100 || v == 10000 || v == 100000){

                                return value;
                            }else{

                                return null;
                            }
                        }
                        return value;
                    }
                },
                scaleLabel: {
                    display: true,
                    labelString: '文件数量(百)'
                }
            }]
        }
    };
    var fileTotalOptions = {

        responsive: true,
        title: {
            display: true,
            text: '近期数据流量汇总',
            fontSize:18
        },
        tooltips: {
            mode: 'point',
            intersect:false,
            callbacks:{

                label: function(tooltipItem, data) {
                    var label = data.datasets[tooltipItem.datasetIndex].label || '';

                    if (label) {
                        label += '共: ';
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
                            return v /100 != 5 ? null: v + "G";
                        }else if(value >= 1000* 1000* 1000 * 1000 && value < 1000*1000*1000*1000 * 1000){
                            v = (value /(1000 * 1000 * 1000 * 1000));
                            return v <5 ? null : v+ "T";
                        }
                        return value;
                    }
                },
                scaleLabel: {
                    display: true,
                    labelString: '文件大小'
                }
            }]
        }
    };
    var dbOptions = {
        responsive: true,
        title: {
            display: true,
            text: '近期速率汇总',
            fontSize:18
        },
        tooltips: {
            mode: 'point',
            intersect:false,
            callbacks:{

                label: function(tooltipItem, data) {
                    var label = data.datasets[tooltipItem.datasetIndex].label || '';

                    if (label) {
                        label += '共: ';
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
                            return v /100 != 5 ? null: v + "G";
                        }else if(value >= 1000* 1000* 1000 * 1000 && value < 1000*1000*1000*1000 * 1000){
                            v = (value /(1000 * 1000 * 1000 * 1000));
                            return v <5 ? null : v+ "T";
                        }
                        return value;
                    }
                },
                scaleLabel: {
                    display: true,
                    labelString: '大小'
                }
            }]
        }
    };
    var protocolSumOptions = {

        responsive: true,
        title: {
            display: true,
            text: '近期速率汇总',
            fontSize:18
        },
        tooltips: {
            mode: 'point',
            intersect:false,
            callbacks:{

                label: function(tooltipItem, data) {

                    var label = data.datasets[tooltipItem.datasetIndex].label || '';
                    if (label) {

                        label += '连接: ';
                        return label + tooltipItem.yLabel +"个";
                    }
                }
            }
        },
        scales: {
            xAxes: [{
                display: true,
                scaleLabel: {
                    display: true
                }
            }],
            yAxes: [{
                display: true,
                type:'logarithmic',
                ticks:{
                    callback: function(value, index, values){

                        var v = 0;
                        if(value < 100 ){

                            return null;
                        }else{
                            v = value / 100;
                            if( v == 1 || v == 50 || v == 100 || v == 10000 || v == 100000){

                                return value;
                            }else{

                                return null;
                            }
                        }
                        return value;
                    }
                },
                scaleLabel: {
                    display: true,
                    labelString: '连接数'
                }
            }]
        }
    };
    var protocolTotalOptions = {

        responsive: true,
        title: {
            display: true,
            text: '近期数据流量汇总',
            fontSize:18
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
                            return v /100 != 5 ? null: v + "G";
                        }else if(value >= 1000* 1000* 1000 * 1000 && value < 1000*1000*1000*1000 * 1000){
                            v = (value /(1000 * 1000 * 1000 * 1000));
                            return v <5 ? null : v+ "T";
                        }
                        return value;
                    }
                },
                scaleLabel: {
                    display: true,
                    labelString: '流量值'
                }
            }]
        }
    };
    var sumctx = $(".panel canvas"),sumChart = new Chart(sumctx,config);

    var _netParam = {"statistiscalType":"net"},_fileParam = {"statistiscalType":"file"},
        _proParam = {"statistiscalType":"protocol"},_dbParam = {"statistiscalType":"db"};

    $.when($.ajax({
        url:contextPath+ "statistics/total/day",
        method:'POST',
        dataType:'json',
        data:_fileParam
    }),$.ajax({
        url:contextPath+ "statistics/total/day",
        method:'POST',
        dataType:'json',
        data:_netParam
    }),$.ajax({
        url:contextPath+ "statistics/total/day",
        method:'POST',
        dataType:'json',
        data:_proParam
    }),$.ajax({
        url:contextPath+ "statistics/total/day",
        method:'POST',
        dataType:'json',
        data:_dbParam
    })).done(function(d1,d2,d3,d4){

       if(d1[0].code == 200){

           updateSum($($(".xe-widget")[1]),d1[0].data);
       }
        if(d2[0].code == 200){

            updateSum($($(".xe-widget")[0]),d2[0].data);
        }
        if(d3[0].code == 200){

            updateSum($($(".xe-widget")[3]),d3[0].data);
        }
        if(d4[0].code == 200){

            updateSum($($(".xe-widget")[2]),d4[0].data);
        }
    });

    function updateSum(panel,data){

        panel.attr("data-to",data.dataSumTotal);
        panel.find("strong.num").text(formatSize(data.dataSumTotal,2 ,['k','B','M', 'G', 'TB']));

        panel.find(".xe-lower strong").first().text(data.dataMax);
        panel.find(".xe-lower strong").last().text(data.dataMin);
    }

    function updateChart(char,data,options,title){

        char.data = {labels:data.labels,datasets:data.datasets};
        if(options){

            char.options = options;
            char.options.title.text = title;
        }
        char.update();
    }


    function chartSwitch(chart){

        var type = chart.config.type;
        type = type == "line"? "bar":"line";
        chart.config.data.datasets.forEach(function(data){

            data.type = type;
        });
        chart.config.type = type;
        chart.update();
    }

    function updateSearchBtn(url,param,title,char,options){

        $searchBtn.off("click");
        $searchBtn.on('click',function(){

            var time = $(".daterange").first().find("span").text();
            if(time && time == "请选择日期"){

                return false;
            }
            var timeStamp = time.split(separator);
            param.start = timeStamp[0];
            param.end = timeStamp[1];
            searchDateByTime(url,param,title,char,options);
        });
    }

    $netBtn.on('click',function(){

        var param = {"type":"day"};
        searchDateByTime("statistics/total/net",param,"近期",sumChart,netOptions);
        updateSearchBtn("statistics/total/net",param,"近期",sumChart,netOptions);
    });
    $fileSumBtn.on('click',function() {

        var param = {"count":true,"type":"day","title":"单日文件总量"};
        searchDateByTime("statistics/total/file",param,"文件总量",sumChart,fileSumOptions);
        updateSearchBtn("statistics/total/file",param,"文件总量",sumChart,fileSumOptions);
    });

    $fileTotalBtn.on('click',function(){

        var param = {"count":false,"type":"day","title":"单日文件大小"};
        searchDateByTime("statistics/total/file",param,"文件大小",sumChart,fileTotalOptions);
        updateSearchBtn("statistics/total/file",param,"文件大小",sumChart,fileTotalOptions);
    });

    $dbSumBtn.on('click',function(){

        var param = {"gather":true,"type":"day","title":"单日数据库采集总量"};
        searchDateByTime("statistics/total/db",param,"数据采集",sumChart,dbOptions);
        updateSearchBtn("statistics/total/db",param,"数据采集",sumChart,dbOptions);
    });

    $dbTotalBtn.on('click',function(){

        var param = {"gather":false,"type":"day","title":"单日数据库上传总量"};
        searchDateByTime("statistics/total/db",param,"数据库上传",sumChart,dbOptions);
        updateSearchBtn("statistics/total/db",param,"数据库上传",sumChart,dbOptions);
    });

    $protocolSumBtn.on('click',function(){

        var param = {"connection":true,"type":"day","title":"单日协议连接总量"};
        searchDateByTime("statistics/total/protocol",param,"协议连接",sumChart,protocolSumOptions);
        updateSearchBtn("statistics/total/protocol",param,"协议连接",sumChart,protocolSumOptions);
    });

    $protocolTotalBtn.on('click',function(){

        var param = {"connection":false,"type":"day","title":"单日协议流量总量"};
        searchDateByTime("statistics/total/protocol",param,"协议流量",sumChart,protocolTotalOptions);
        updateSearchBtn("statistics/total/protocol",param,"协议流量",sumChart,protocolTotalOptions);
    });

    function searchDateByTime (urls,param,title,chart,options) {

        $.ajax({
            url:contextPath+urls,
            data:param,
            method:'POST',
            dateType:'json',
            success:function (data) {

                if (data.code == 200){

                    //updateChartTitle(chart,title + "统计信息如下");
                    updateChart(chart,data.data,options,title + "统计信息如下");
                }
            }
        });

    }

    function updateChartTitle(chart,title){

        chart.options.title.text = title;
    }

    function getSearchType(time){

        return time.split("-").length == 3 ? "day":time.split("-").length == 2 ?"month":"year";
    }
});


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