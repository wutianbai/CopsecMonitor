jQuery(function () {
    var $body = $('body');
    var $transferService = $($('.panel')[0]);
    $.when($.ajax(contextPath + "system/get")).done(function (data) {
        var s = "<;ws;>";
        if (data.data.SYSLOGINFO) {
            var strs = data.data.SYSLOGINFO.split(s);
            $transferService.find('input[type=radio]').each(function (e, i) {
                if ($(this).val() === strs[0]) {
                    $(this).attr('checked', 'checked');
                    $(this).parent().parent().addClass("cbr-checked");
                } else {
                    $(this).parent().parent().removeClass("cbr-checked");
                }
            });
            $transferService.find("input[type=text]").first().val(strs[1]);
            $transferService.find("input[type=text]").last().val(strs[2]);
        }
    });

    var $transferBtn = $transferService.find("button");
    $transferBtn.on('click', function () {
        var transferIp = $transferService.find("input[type=text]").first().val(),
            transferPort = $transferService.find("input[type=text]").last().val(),
            status = $transferService.find('input[type=radio]:checked').val();

        if (transferIp && transferPort && status) {
            var regStr = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/;
            if (!transferIp.match(regStr)) {
                toastr.error("IP格式不正确，请填写正确的IP格式!");
                return false;
            }

            if (!$.isNumeric(transferPort) && (0 >= transferPort || transferPort > 65535)) {
                toastr.error("监听端口不正确!");
                return false;
            }

            $.ajax({
                url: contextPath + 'system/inspector/set',
                method: 'POST',
                data: {'status': status, 'ip': transferIp, 'port': transferPort, 'type': 's'},
                success: function (data) {
                    if (data.code === 200) {
                        toastr.info(data.message);
                    } else {
                        toastr.error(data.message);
                    }
                },
                error: function (jqx, status, error) {
                    toastr.error(status);
                }
            });
        } else {
            toastr.error("请填写配置项");
            return false;
        }
    });

    function isAgentId(str) {
        var reg = /[a-zA-Z0-9]+/;
        return reg.test(str);
    }

    function isStr(str) {
        var reg = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
        return reg.test(str);
    }
});