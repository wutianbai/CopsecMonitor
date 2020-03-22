jQuery(function(){

    var $body = $('body');
    var separator = ' - ';
    var _lables = "",_datasets = "";
    var DAY_TITLE = "近期数据汇总",MONTH_TILTE = "月度统计汇总",YEAR_TITLE = "年度统计汇总",HOUR_TITLE = "今日各时段";
    var CONN4DEVICE = "采集与上传文件总量[设备]",NET4DEVICE = "采集与上传文件总大小[设备]",CONN4TASK = "采集与上传文件总量[子任务]",NET4TASK="采集与上传文件总大小[子任务]";
    var dateType = ["YYYY-MM-DD","YYYY-MM","YYYY"];
    var $panel4dc = $body.find(".panel:nth-child(1)"),
        $panel4tc = $body.find(".panel:nth-child(2)"),
        $panel4dn = $body.find(".panel:nth-child(3)"),
        $panel4tn = $body.find(".panel:nth-child(4)");
    var DURL = contextPath+"/statistics/file/device",NURL = contextPath+"/statistics/file/task";
    $.when(
        $.ajax({
            url:NURL,
            data:{'count':true,"type":"hour"},
            method:'POST',
            dateType:'json'}),
        $.ajax({
            url:NURL,
            data:{'count':false,"type":"hour"},
            method:'POST',
            dataType:'json'
        }),
        $.ajax({

            url:NURL,
            data:{"count":false,"type":"hour"},
            method:"POST",
            dataType:'json'
        }),
        $.ajax({

            url:NURL,
            data:{"count":true,"type":"hour"},
            method:"POST",
            dataType:'json'
        })).
    done(function(d1,d2,d3,d4){
                if(d1[0].code == 200){

                    updateChartTitle(device_conn_char,HOUR_TITLE + "-"+  NET4DEVICE);
                    updateChart(device_conn_char,d1[0].data);
                }
                if(d2[0].code == 200){

                    console.log(d2[0].data);
                    updateChartTitle(task_conn_chart,HOUR_TITLE + "-"+ CONN4TASK);
                    updateChart(task_conn_chart,d2[0].data);
                }
               /* if(d3[0].code = 200){

                    updateChartTitle(task_conn_chart,HOUR_TITLE + "-"+ CONN4TASK);
                    updateChart(task_conn_chart,d3[0].data);
                }
                if(d4[0].code == 200){

                    updateChartTitle(task_net_chart,HOUR_TITLE + "-"+ NET4TASK);
                    updateChart(task_net_chart,d4[0].data);
                }*/
    });
    var device_Connection_config = {

        type:'bar',
        data:{

            labels:"",
            datasets:""
        },
        options:{
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
                                v = value % 100;
                                if( v == 0){

                                    return value/100;
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
            },
            animation: {
                onComplete: function(animation){
                    /*if ($exportBtn2){

                        $exportBtn2.attr("href",this.toBase64Image());
                    }*/
                }
            }
        }
    };
    var task_Connection_config = {

        type:'bar',
        data:{

            labels:"",
            datasets:""
        },
        options:{
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
                        labelString: '文件数量'
                    }
                }]
            }/*,
            animation: {
                onComplete: function(animation){
                    if ($exportBtn2){

                        $exportBtn2.attr("href",this.toBase64Image());
                    }
                }
            }*/
        }
    };
    var device_NetData_Config = {

        type:'bar',
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
                                return v % 5 != 0 ? null: v + "G";
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
            },
            animation: {
                onComplete: function(animation){
                    /*if ($exportBtn){

                        $exportBtn.attr("href",this.toBase64Image());
                    }*/
                }
            }
        }
    };
    var task_NetData_Config = {

        type:'bar',
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
                        labelString:'文件大小'
                    }
                }]
            }/*,
            animation: {
                onComplete: function(animation){
                    if ($exportBtn){

                        $exportBtn.attr("href",this.toBase64Image());
                    }
                }
            }*/
        }
    };

    var device_conn_char = new Chart($panel4dc.find("canvas"), task_Connection_config),
        task_conn_chart = new Chart($panel4tc.find("canvas"),task_NetData_Config)
        /*device_net_chart = new Chart($panel4dn.find("canvas"),device_NetData_Config),
        task_net_chart = new Chart($panel4tn.find("canvas"),task_NetData_Config)*/;

    var $swtichBtn_1 = $panel4dc.find("button:nth-child(1)"),
        $hour_1 = $panel4dc.find("button:nth-child(2)"),
        $day_1 = $panel4dc.find("button:nth-child(3)"),
        $month_1 = $panel4dc.find("button:nth-child(4)"),
        $year_1 = $panel4dc.find("button:nth-child(5)"),
        $search_1 = $panel4dc.find("button:nth-child(6)");

    var $swtichBtn_2 = $panel4tc.find("button:nth-child(1)"),
        $hour_2 = $panel4tc.find("button:nth-child(2)"),
        $day_2 = $panel4tc.find("button:nth-child(3)"),
        $month_2 = $panel4tc.find("button:nth-child(4)"),
        $year_2 = $panel4tc.find("button:nth-child(5)"),
        $search_2 = $panel4tc.find("button:nth-child(6)");

    var $swtichBtn_3 = $panel4dn.find("button:nth-child(1)"),
        $hour_3 = $panel4dn.find("button:nth-child(2)"),
        $day_3 = $panel4dn.find("button:nth-child(3)"),
        $month_3 = $panel4dn.find("button:nth-child(4)"),
        $year_3 = $panel4dn.find("button:nth-child(5)"),
        $search_3 = $panel4dn.find("button:nth-child(6)");

    var $swtichBtn_4 = $panel4tn.find("button:nth-child(1)"),
        $hour_4 = $panel4tn.find("button:nth-child(2)"),
        $day_4 = $panel4tn.find("button:nth-child(3)"),
        $month_4 = $panel4tn.find("button:nth-child(4)"),
        $year_4 = $panel4tn.find("button:nth-child(5)"),
        $search_4 = $panel4tn.find("button:nth-child(6)");

    function updateChart(char,data){

        char.data = {labels:data.labels,datasets:data.datasets};
        char.update();
    }

    $swtichBtn_1.click(function(){

        chartSwitch(device_conn_char);
    });

    $swtichBtn_2.click(function () {

        chartSwitch(task_conn_chart);
    });

    $swtichBtn_3.click(function(){

        chartSwitch(device_net_chart);
    });

    $swtichBtn_4.click(function(){

        chartSwitch(task_net_chart);
    });

    function chartSwitch(chart){

        var type = chart.config.type;
        type = type == "line"? "bar":"line";
        chart.config.data.datasets.forEach(function(data){

            data.type = type;
        });
        chart.config.type = type;
        chart.update();
    }

    function getData(url,data,chart,dataRange,index,title){

        $.ajax({
            url:url,
            data:data,
            method:'POST',
            dataType:'json',
            success:function(data){

                if(data.code == 200){

                    updateChartTitle(chart,title);
                    dataRange.attr('data-format',dateType[index]);
                    updateChart(chart,data.data);
                }
            },
            error:function(data,status,error){

                toastr.error(status + error);
            }
        });
    }

    $hour_1.click(function(){

        var data = {"count":true,"type":"hour"};
        var $range = $($body.find(".daterangepicker")[0]);
        getData(NURL,data,device_conn_char,$range,0,HOUR_TITLE +"-"+ CONN4TASK);
    });

    $day_1.click(function(){

        var data = {"count":true,"type":"day"};
        var $range = $($body.find(".daterangepicker")[0]);
        getData(NURL,data,device_conn_char,$range,0,DAY_TITLE +"-"+ CONN4TASK);
    });

    $hour_2.click(function(){

        var data = {"count":false,"type":"hour"};
        var $range = $($body.find(".daterangepicker")[1]);
        getData(NURL,data,task_conn_chart,$range,0,HOUR_TITLE+"-"+ NET4TASK);
    });

    $day_2.click(function(){

        var data = {"count":false,"type":"day"};
        var $range = $($body.find(".daterangepicker")[1]);
        getData(NURL,data,task_conn_chart,$range,0,DAY_TITLE+"-"+ NET4TASK);
    });

    $hour_3.click(function(){

        var data = {"count":false,"type":"hour"};
        var $range = $($body.find(".daterangepicker")[2]);
        getData(DURL,data,device_net_chart,$range,0,HOUR_TITLE+"-"+ NET4DEVICE);
    });

    $day_3.click(function(){

        var data = {"count":false,"type":"day"};
        var $range = $($body.find(".daterangepicker")[2]);
        getData(DURL,data,device_net_chart,$range,0,DAY_TITLE+"-"+ NET4DEVICE);
    });

    $hour_4.click(function(){

        var data = {"count":false,"type":"hour"};
        var $range = $($body.find(".daterangepicker")[3]);
        getData(NURL,data,task_net_chart,$range,0,HOUR_TITLE+"-"+ NET4TASK);
    });
    $day_4.click(function(){

        var data = {"count":false,"type":"day"};
        var $range = $($body.find(".daterangepicker")[3]);
        getData(NURL,data,task_net_chart,$range,0,DAY_TITLE+"-"+ NET4TASK);
    });
    // month
    $month_1.click(function(){

        var data = {"count":true,"type":"month"};
        var $range = $($body.find(".daterangepicker")[0]);
        getData(NURL,data,device_conn_char,$range,1,MONTH_TILTE +"-"+ CONN4TASK);
    });

    $month_2.click(function(){

        var data = {"count":false,"type":"month"};
        var $range = $($body.find(".daterangepicker")[1]);
        getData(NURL,data,task_conn_chart,$range,1,MONTH_TILTE +"-"+ NET4TASK);
    });

    $month_3.click(function(){

        var data = {"count":false,"type":"month"};
        var $range = $($body.find(".daterangepicker")[2]);
        getData(DURL,data,device_net_chart,$range,1,MONTH_TILTE+"-"+ NET4DEVICE);
    });

    $month_4.click(function(){

        var data = {"count":false,"type":"month"};
        var $range = $($body.find(".daterangepicker")[3]);
        getData(NURL,data,task_net_chart,$range,1,MONTH_TILTE+"-"+ NET4TASK);
    });

    //year
    $year_1.click(function(){

        var data = {"count":true,"type":"year"};
        var $range = $($body.find(".daterangepicker")[0]);
        getData(NURL,data,device_conn_char,$range,2,YEAR_TITLE+"-"+ CONN4TASK);
    });

    $year_2.click(function(){

        var data = {"count":false,"type":"year"};
        var $range = $($body.find(".daterangepicker")[1]);
        getData(NURL,data,task_conn_chart,$range,2,YEAR_TITLE+"-"+ NET4TASK);
    });

    $year_3.click(function(){

        var data = {"count":false,"type":"year"};
        var $range = $($body.find(".daterangepicker")[2]);
        getData(DURL,data,device_net_chart,$range,2,YEAR_TITLE+"-"+ NET4DEVICE);
    });

    $year_4.click(function(){

        var data = {"count":false,"type":"year"};
        var $range = $($body.find(".daterangepicker")[3]);
        getData(NURL,data,task_net_chart,$range,2,YEAR_TITLE+"-"+ NET4TASK);
    });

    //search
    $search_1.click(function(){

        var time = $panel4dc.find(".daterange span").text();
        if (time && time == "请选择日期"){

            return false;
        }
        var times = time.split(separator);
        var type = getSearchType(times[0]);
        var data = {"count":true,"start":times[0],"end":times[1],"type":type};
        var $range = $($body.find(".daterangepicker")[0]);
        getData(NURL,data,device_conn_char,$range,getIndex(times[0]),time + "统计信息如下");
    });

    $search_2.click(function(){

        var time = $panel4tc.find(".daterange span").text();
        if (time && time == "请选择日期"){

            return false;
        }
        var times = time.split(separator);
        var type = getSearchType(times[0]);
        var data = {"count":false,"start":times[0],"end":times[1],"type":type};
        var $range = $($body.find(".daterangepicker")[1]);
        getData(NURL,data,task_conn_chart,$range,getIndex(times[0]),time + "统计信息如下");
    });

    $search_3.click(function(){

        var time = $panel4dn.find(".daterange span").text();
        if (time && time == "请选择日期"){

            return false;
        }
        var times = time.split(separator);
        var type = getSearchType(times[0]);
        var data = {"count":false,"start":times[0],"end":times[1],"type":type};
        var $range = $($body.find(".daterangepicker")[2]);
        getData(DURL,data,device_net_chart,$range,getIndex(times[0]),time + "统计信息如下");
    });

    $search_4.click(function(){

        var time = $panel4tn.find(".daterange span").text();
        if (time && time == "请选择日期"){

            return false;
        }
        var times = time.split(separator);
        var type = getSearchType(times[0]);
        var data = {"count":false,"start":times[0],"end":times[1],"type":type};
        var $range = $($body.find(".daterangepicker")[3]);
        getData(DURL,data,task_net_chart,$range,getIndex(times[0]),time + "统计信息如下");
    });

    /**
     * 获取日志格式
     * @param time
     * @returns {number}
     */
    function getIndex(x){

        return x.split("-").length == 3 ? 0 :x.split("-").length == 2? 1:2;
    }

    function updateChartTitle(chart,title){

        chart.options.title.text = title;
    }

    function getSearchType(time){

        return time.split("-").length == 3 ? "day":time.split("-").length == 2 ?"month":"year";
    }
});


function formatSize( size, pointLength, units ) {
    var unit;

    units = units || [ 'B', 'K', 'M', 'G', 'TB' ];

    while ( (unit = units.shift()) && size > 1024 ) {
        size = size / 1024;
    }

    return (unit === 'B' ? Number(size).toFixed( pointLength || 2 ) : Number(size).toFixed( pointLength || 2 )) +
        unit;
}