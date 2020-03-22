jQuery(function(){

    var $body = $('body');
    var separator = ' - ';
    var _lables = "",_datasets = "";
    var DAY_TITLE = "近期数据汇总-",MONTH_TILTE = "月度统计汇总-",YEAR_TITLE = "年度统计汇总-",HOUR_TITLE = "今日各时段-";
    var dateType = ["YYYY-MM-DD","YYYY-MM","YYYY"];
    var $sumPanel = $body.find(".panel:nth-child(1)"),
        speedPanel = $body.find(".panel:nth-child(2)");

    var $switchBtn = $sumPanel.find("button:nth-child(1)"),
        $hourBtn = $sumPanel.find("button:nth-child(2)"),
        $dayBtn = $sumPanel.find("button:nth-child(3)"),
        $monthBtn = $sumPanel.find("button:nth-child(4)"),
        $yearBtn = $sumPanel.find("button:nth-child(5)");
        $searchBtn1 = $sumPanel.find("button").last(),
        $dataRange = $body.find(".daterangepicker").first(),
        $selectOne = $sumPanel.find("select").first();


    var $switchBtn2 = speedPanel.find("button:nth-child(1)"),
        $hourBtn2 = speedPanel.find("button:nth-child(2)"),
        $dayBtn2 = speedPanel.find("button:nth-child(3)"),
        $monthBtn2 = speedPanel.find("button:nth-child(4)"),
        $yearBtn2 = speedPanel.find("button:nth-child(5)"),
        $searchBtn2 = speedPanel.find("button").last(),
        $dataRange2 = $body.find(".daterangepicker").last(),
        $selectTwo = speedPanel.find("select").first();

    $.when($.ajax({
            url:contextPath+"statistics/total/net",
            data:{"type":"hour"},
            method:'POST',
            dateType:'json'}),

        $.ajax({
            url:contextPath+"/statistics/net/info",
            data:JSON.stringify({'sum':false,"type":"hour"}),
            method:'POST',
            dataType:'json',
            contentType:"application/json;charset=utf-8"
        })).done(function(d1,d2){
                if(d1[0].code == 200){

                    updateChartTitle(sumChart,HOUR_TITLE + "流量");
                    updateChart(sumChart,d1[0].data);
                }
                if(d2[0].code == 200){

                    updateChartTitle(speedChart,HOUR_TITLE + "速率");
                    updateChart(speedChart,d2[0].data);
                }
    });

    $.when($.ajax(contextPath + "/statistics/device/get")).done(function(data){

        if(data.code == 200){

            $.each(data.data,function(index,e){
               var option = $("<option value='"+e.data.id+"'>"+e.data.name+"</option>");
               var option2 = $("<option value='"+e.data.id+"'>"+e.data.name+"</option>");
               $selectOne.append(option);
               $selectTwo.append(option2);
            });
        }
    });

    var speedConfig = {

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

                            label += '速率: ';
                            var x = tooltipItem.yLabel.toFixed(2);
                            if(x > 0){

                                label += formatSize(x,2,['B','k','M', 'G', 'TB']) + "/s";
                                return label;
                            }else{

                                return label + "0.00MB/s";
                            }

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
                            if(value <= 1000){//B
                                return null;
                            }else if(value >= 1000 && value < 1000*1000){
                                v = (value /(1000 ));
                                return  v % 500 == 0 ? v + "k/s": null ;
                            }else if(value >= 1000 * 1000   && value < 1000*1000*1000){
                                v = (value /(1000 * 1000 ));
                                return v / 100 == 5 ?  v + "MB/s":null;
                            }else if(value >= 1000* 1000* 1000 && value < 1000*1000*1000*1000){
                                v = (value /(1000 * 1000 * 1000 ));
                                return v  / 5 == 0 ? v+ "GB/s" : null;
                            }
                            return value;
                        }
                    },
                    scaleLabel: {
                        display: true,
                        labelString: '速率'
                    }
                }]
            }
        }
    };
    var config = {

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

    /*$exportBtn.click(function(){
        var url = $(this).attr("href");
        window.open().document.write("<iframe src=" + url  + " frameborder='0' style='border:0; top:0px; left:0px; bottom:0px; right:0px; width:100%; height:100%;' allowfullscreen></iframe>");
    });

    $exportBtn2.click(function(){
        var url =$(this).attr("href");
        window.open().document.write("<iframe src=" + url  + " frameborder='0' style='border:0; top:0px; left:0px; bottom:0px; right:0px; width:100%; height:100%;' allowfullscreen></iframe>");
    });*/

    var sumctx = $(".panel:nth-child(1) canvas"),sumChart = new Chart(sumctx,config),
    speedCtx = $(".panel:nth-child(2) canvas"),speedChart =new Chart(speedCtx,speedConfig) ;

    function updateChart(char,data){

        char.data = {labels:data.labels,datasets:data.datasets};
        char.update();
    }

    $switchBtn.click(function(){

        chartSwitch(sumChart);
    });

    $switchBtn2.click(function () {

        chartSwitch(speedChart);
    })

    function chartSwitch(chart){

        var type = chart.config.type;
        type = type == "line"? "bar":"line";
        chart.config.data.datasets.forEach(function(data){

            data.type = type;
        });
        chart.config.type = type;
        chart.update();
    }

    function getDeviceIds(select){

        var deviceIds = new Array();
        if(select.val() != null){

            $.each(select.val(),function(index,e){

                deviceIds.push(e);
            });
        }
        return deviceIds;
    }

    $hourBtn.click(function(){

        $.ajax({
            url:contextPath+"statistics/net/info",
            data:JSON.stringify({'sum':true,'type':'hour',"deviceIds":getDeviceIds($selectOne)}),
            method:'POST',
            dataType:'json',
            contentType:"application/json;charset=utf-8",
            success:function(data){

                if(data.code == 200){

                    updateChartTitle(sumChart,HOUR_TITLE+"流量");
                    $dataRange.attr('data-format',dateType[0]);
                    updateChart(sumChart,data.data);
                }
            },
            error:function(data,status,error){

                toastr.error(status + error);
            }
        });
    })

    $hourBtn2.click(function(){

        $.ajax({
            url:contextPath+"/statistics/net/info",
            data:JSON.stringify({'sum':false,'type':'hour',"deviceIds":getDeviceIds($selectTwo)}),
            method:'POST',
            dataType:'json',
            contentType:"application/json;charset=utf-8",
            success:function(data){

                if(data.code == 200){

                    updateChartTitle(speedChart,HOUR_TITLE+"速率");
                    $dataRange2.attr('data-format',dateType[0]);
                    updateChart(speedChart,data.data);
                }
            },
            error:function(data,status,error){

                toastr.error(status + error);
            }
        });
    });
    $dayBtn.click(function(){

        $.ajax({
            url:contextPath+"/statistics/net/info",
            data:JSON.stringify({'sum':true,'type':'day',"deviceIds":getDeviceIds($selectOne)}),
            method:'POST',
            dataType:'json',
            contentType:"application/json;charset=utf-8",
            success:function(data){

                if(data.code == 200){

                    updateChartTitle(sumChart,DAY_TITLE+"流量");
                    $dataRange.attr('data-format',dateType[0]);
                    updateChart(sumChart,data.data);
                }
            },
            error:function(data,status,error){

                toastr.error(status + error);
            }
        });
    });

    $dayBtn2.click(function () {

        $.ajax({
            url:contextPath+"/statistics/net/info",
            data:JSON.stringify({'sum':false,'type':'day',"deviceIds":getDeviceIds($selectTwo)}),
            method:'POST',
            dataType:'json',
            contentType:"application/json;charset=utf-8",
            success:function(data){

                if(data.code == 200){

                    updateChartTitle(speedChart,DAY_TITLE+"速率");
                    $dataRange2.attr('data-format',dateType[0]);
                    updateChart(speedChart,data.data);
                }
            },
            error:function(data,status,error){

                toastr.error(status + error);
            }
        });
    });

    $monthBtn.click(function(){

        $.ajax({
            url:contextPath+"/statistics/net/info",
            data:JSON.stringify({'sum':true,'type':'month',"deviceIds":getDeviceIds($selectOne)}),
            method:'POST',
            dataType:'json',
            contentType:"application/json;charset=utf-8",
            success:function(data){

                if(data.code == 200){

                    $dataRange.attr('data-format',dateType[1]);
                    updateChartTitle(sumChart,MONTH_TILTE+"流量");
                    updateChart(sumChart,data.data);
                }
            },
            error:function(data,status,error){

                toastr.error(status + error);
            }
        });
    });

    $monthBtn2.click(function () {

        $.ajax({
            url:contextPath+"/statistics/net/info",
            data:JSON.stringify({'sum':false,'type':'month',"deviceIds":getDeviceIds($selectTwo)}),
            method:'POST',
            dataType:'json',
            contentType:"application/json;charset=utf-8",
            success:function(data){

                if(data.code == 200){

                    $dataRange2.attr('data-format',dateType[1]);
                    updateChartTitle(speedChart,MONTH_TILTE+"速率");
                    updateChart(speedChart,data.data);
                }
            },
            error:function(data,status,error){

                toastr.error(status + error);
            }
        });
    });

    $yearBtn.click(function(){

        $.ajax({
            url:contextPath+"/statistics/net/info",
            data:JSON.stringify({'sum':true,'type':'year',"deviceIds":getDeviceIds($selectOne)}),
            method:'POST',
            dataType:'json',
            contentType:"application/json;charset=utf-8",
            success:function(data){

                if(data.code == 200){
                    $dataRange.attr('data-format',dateType[2]);
                    updateChartTitle(sumChart,YEAR_TITLE+"流量");
                    updateChart(sumChart,data.data);
                }
            },
            error:function(data,status,error){

                toastr.error(status + error);
            }
        });
    });

    $yearBtn2.click(function () {

        $.ajax({
            url:contextPath+"/statistics/net/info",
            data:JSON.stringify({'sum':false,'type':'year',"deviceIds":getDeviceIds($selectTwo)}),
            method:'POST',
            dataType:'json',
            contentType:"application/json;charset=utf-8",
            success:function(data){

                if(data.code == 200){
                    $dataRange2.attr('data-format',dateType[2]);
                    updateChartTitle(speedChart,YEAR_TITLE+"速率");
                    updateChart(speedChart,data.data);
                }
            },
            error:function(data,status,error){

                toastr.error(status + error);
            }
        });
    });


    $searchBtn1.click(function(){

        var time = $(".daterange").first().find("span").text().split(separator);
        searchDateByTime(time, true,sumChart,$selectOne);
    });

    $searchBtn2.click(function(){

        var time = $(".daterange").last().find("span").text().split(separator);
        searchDateByTime(time, false,speedChart,$selectTwo);
    });

    function searchDateByTime (timeSpan,isSum,chart,select) {

        if (timeSpan && timeSpan == "请选择日期"){

            return false;
        }
        var type = getSearchType(timeSpan[0]);
        $.ajax({
            url:contextPath+"/statistics/net/info",
            data:JSON.stringify({'sum':isSum,"type":type,"start":timeSpan[0],"end":timeSpan[1],"deviceIds":getDeviceIds(select)}),
            method:'POST',
            dateType:'json',
            contentType:"application/json;charset=utf-8",
            success:function (data) {

                if (data.code == 200){

                    updateChartTitle(chart,timeSpan + "统计信息如下");
                    updateChart(chart,data.data);
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
        var unit;

        units = units || [ 'B', 'K', 'M', 'G', 'TB' ];

        while ( (unit = units.shift()) && size > 1024 ) {
            size = size / 1024;
        }
    return (unit === 'B' ? Number(size).toFixed( pointLength || 2 ) : Number(size).toFixed( pointLength || 2 )) +
        unit;
    }
