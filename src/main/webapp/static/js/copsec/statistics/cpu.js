jQuery(function(){

    var $body = $('body');
    var _lables = "",_datasets = "";
    $.when($.ajax(contextPath+"statistics/cpu/get")).done(function(data){

       if(data.code == 200){

           _lables = data.data.labels;
           _datasets = data.data.datasets;
           cpuChart.data = {labels:_lables,datasets:_datasets};
           cpuChart.update();
       }
    });
    var config = {

        type:'line',
        data:{

            labels:_lables,
            datasets:_datasets
        },
        options:{
            responsive: true,
            title: {
                display: true,
                text: 'cpu当日上报数据'
            },
            tooltips: {
                mode: 'index'
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
                    scaleLabel: {
                        display: true,
                        labelString: '使用率(%)'
                    },
                    ticks: {
                        suggestedMin:0,
                        suggestedMax: 3
                    }
                }]
            }
        }
    };
    var ctx = $("canvas"),cpuChart = new Chart(ctx,config);
});