jQuery(function () {

    var $body = $('body');
    var $interfacePanel = $($body.find('.panel')[0]),
        $gatewayPanel = $($body.find('.panel')[1]),
        $dnsPanel = $($body.find('.panel')[2]),
        $managerPanel = $($body.find('.panel')[3]),
        $routerPanel = $($body.find('.panel')[4]),
        $interTable = $interfacePanel.find('table').last(),
        $plusBtn = $interTable.find('td').last().find('button.btn-turquoise'),
        $updateBtn = $interTable.find('td').last().find('button.btn-info'),
        $cancleBtn = $interTable.find('td').last().find('button.btn-gray'),

        $gatewayBtn = $gatewayPanel.find('button').first(),
        $dnsBtn = $dnsPanel.find('button').first(),
        $managerBtn = $managerPanel.find("button").first(),

        $routerTable = $routerPanel.find('table').last(),
        $routePlugBtn = $routerTable.find('td').last().find('button.btn-turquoise'),
        $routeUpdateBtn = $routerTable.find('td').last().find('button.btn-info'),
        $routeCancleBtn = $routerTable.find('td').last().find('button.btn-gray');

    var ethMap = new Map();

    $.when($.ajax(contextPath + "system/get")).done(function (data) {
        if (data.data.INTERFACELIST) {
            var arrays = new String(data.data.INTERFACELIST).split(",");
            for (var i = 0; i < arrays.length; i++) {

                var datas = new String(arrays[i]).split(":");
                addOptions(datas[0], datas[1], $interTable);
                addOptions(datas[0], datas[1], $routerTable);
                ethMap.set(datas[0], datas[1]);
            }
        }

        if (data.data.NETCONFIG) {
            var array = $.parseJSON(data.data.NETCONFIG);
            for (var i = 0; i < array.length; i++) {
                addInterface(array[i]);
            }
        }

        if (data.data.ROUTER) {
            var array = $.parseJSON(data.data.ROUTER);
            for (var i = 0; i < array.length; i++) {
                addRouter(array[i]);
            }
        }

        if (data.data.GATEWAY) {
            $gatewayPanel.find('input[type=text]').val(data.data.GATEWAY);
        }

        if (data.data.DNS) {
            $dnsPanel.find('input[type=text]').val(data.data.DNS);
        }

        if (data.data.MANAGERIP) {
            $managerPanel.find('input[type=text]').val(data.data.MANAGERIP);
        }
    });

    var $interface = $interTable.find("select"),
        $ip = $interTable.find("tr:nth-child(2) input"),
        $sub = $interTable.find("tr:nth-child(3) input"),
        $g = $interTable.find("tr:nth-child(4) input"),

        $interface4r = $routerTable.find("select"),
        $ip4r = $routerTable.find("tr:nth-child(1) input"),
        $sub4r = $routerTable.find("tr:nth-child(2) input"),
        $g4r = $routerTable.find("tr:nth-child(3) input");


    $interface.on('change', function () {

        var value = $interface.find('option:selected').val();
        $interface.children().each(function (i, e) {

            if ($(this).val() == value) {

                $(this).attr('selected', true);
            } else {

                $(this).removeAttr('selected');
            }
        });
    });

    $interface4r.on('change', function () {

        var value = $interface4r.find('option:selected').val();
        $interface4r.children().each(function (i, e) {

            if ($(this).val() == value) {

                $(this).attr('selected', true);
            } else {

                $(this).removeAttr('selected');
            }
        });
    });

    //接口添加按钮
    $plusBtn.click(function () {
        if (!ipTest($ip.val()) || !ipTest($sub.val())) {
            toastr.error("请输入有效IP地址");
            return false;
        }

        if ($g.val() != "") {
            if (!ipTest($g.val())) {
                toastr.error("请输入有效IP地址");
                return false;
            }
        }

        var interface = {
            uuid: "",
            ethName: $interface.val(),
            subnet: $sub.val(),
            gateway: $g.val(),
            ip: $ip.val()
        };

        $.ajax({
            url: contextPath + 'system/network/add',
            data: {
                'ethName': $interface.val(),
                'subnet': $sub.val(),
                'gateway': $g.val(),
                'ip': $ip.val()
            },
            method: 'POST',
            dataType: 'json',
            success: function (data) {
                if (data.code == 200) {
                    toastr.info(data.message);
                    interface.uuid = data.data.uuid;
                    addInterface(interface);
                } else {
                    toastr.error(data.message + data.data);
                }
            }
        });
    });

    //接口更新按钮
    $updateBtn.click(function (id) {

        if (!ipTest($ip.val()) || !ipTest($sub.val())) {

            toastr.error("请输入有效IP地址");
            return false;
        }
        if ($g.val() != "") {

            if (!ipTest($g.val())) {

                toastr.error("请输入有效IP地址");
                return false;
            }
        }
        var uuid = $ip.attr('name');
        $.ajax({

            url: contextPath + 'system/network/update',
            data: {
                'uuid': uuid,
                'ethName': $interface.val(),
                'subnet': $sub.val(),
                'gateway': $g.val(),
                'ip': $ip.val()
            },
            method: 'POST',
            dataType: 'json',
            success: function (data) {

                if (data.code == 200) {

                    toastr.info(data.message);
                    var $tr = $("tr[name=" + uuid + "]");
                    $tr.find('td:nth-child(1)').html(ethMap.get($interface.val()));
                    $tr.find('td:nth-child(2)').html($ip.val());
                    $tr.find('td:nth-child(3)').html($sub.val());
                    $tr.find('td:nth-child(4)').html($g.val());

                    $cancleBtn.trigger('click');
                } else {

                    toastr.error(data.messsage);
                }
            },
            error: function (data) {

                toastr.error(data);
            }
        });

    });

    //取消按钮
    $cancleBtn.click(function () {

        $interTable.find("input[type=text]").val("");
        swichBtn($plusBtn);
        swichBtn($updateBtn);
        swichBtn($cancleBtn);
    });

    //管理口按钮
    $managerBtn.click(function () {

        var ip = $managerPanel.find("input[type=text]").val();
        if (!ipTest(ip)) {

            toastr.error('请输入有效IP地址');
            return false;
        }
        $.ajax({

            url: contextPath + 'system/network/manageIp',
            data: {'ip': ip},
            method: 'POST',
            dataType: 'json',
            success: function (data) {

                if (data.code == 200) {

                    toastr.info(data.message);
                    $managerPanel.find("input[type=text]").val(ip);
                } else {

                    toastr.error(data.message);
                }
            },
            error: function (data) {

                toastr.error(data);
            }
        });
    });

    //dns
    $dnsBtn.click(function () {

        var ip = $dnsPanel.find("input[type=text]").val();
        if (!ipTest(ip)) {

            toastr.error('请输入有效IP地址');
            return false;
        }
        $.ajax({

            url: contextPath + 'system/network/dns',
            data: {'ip': ip},
            method: 'POST',
            dataType: 'json',
            success: function (data) {

                if (data.code == 200) {

                    toastr.info(data.message);
                    $dnsPanel.find("input[type=text]").val(ip);
                } else {

                    toastr.error(data.message);
                }
            },
            error: function (data) {

                toastr.error(data);
            }
        });
    });

    $gatewayBtn.click(function () {

        var ip = $gatewayPanel.find("input[type=text]").val();
        if (!ipTest(ip)) {

            toastr.error('请输入有效IP地址');
            return false;
        }
        $.ajax({

            url: contextPath + 'system/network/gateway',
            data: {'ip': ip},
            method: 'POST',
            dataType: 'json',
            success: function (data) {

                if (data.code == 200) {

                    toastr.info(data.message);
                    $gatewayPanel.find("input[type=text]").val(ip);
                } else {

                    toastr.error(data.message);
                }
            },
            error: function (data) {

                toastr.error(data);
            }
        });
    });


    $routePlugBtn.click(function () {

        if (!ipTest($ip4r.val()) || !ipTest($sub4r.val())) {

            toastr.error("请输入有效IP地址");
            return false;
        }
        if ($g4r.val() != "") {

            if (!ipTest($g.val())) {

                toastr.error("请输入有效IP地址");
                return false;
            }
        }

        var router = {
            uuid: "",
            interfaceName: $interface4r.val(),
            subnet: $sub4r.val(),
            gateway: $g4r.val(),
            ip: $ip4r.val()
        }

        $.ajax({

            url: contextPath + 'system/router/add',
            data: {
                'interfaceName': $interface4r.val(),
                'subnet': $sub4r.val(),
                'gateway': $g4r.val(),
                'ip': $ip4r.val()
            },
            method: 'POST',
            dataType: 'json',
            success: function (data) {

                if (data.code == 200) {

                    toastr.info(data.message);
                    router.uuid = data.data.uuid;
                    addRouter(router);
                } else {

                    toastr.error(data.message + data.data);
                }
            }
        });
    });

    $routeUpdateBtn.click(function () {

        if (!ipTest($ip4r.val()) || !ipTest($sub4r.val())) {

            toastr.error("请输入有效IP地址");
            return false;
        }
        if ($g4r.val() != "") {

            if (!ipTest($g.val())) {

                toastr.error("请输入有效IP地址");
                return false;
            }
        }
        var uuid = $ip4r.attr('name');
        $.ajax({

            url: contextPath + 'system/router/update',
            data: {
                'uuid': uuid,
                'interfaceName': $interface4r.val(),
                'subnet': $sub4r.val(),
                'gateway': $g4r.val(),
                'ip': $ip4r.val()
            },
            method: 'POST',
            dataType: 'json',
            success: function (data) {

                if (data.code == 200) {

                    toastr.info(data.message);
                    var $tr = $("tr[name=" + uuid + "]");
                    $tr.find('td:nth-child(1)').html(ethMap.get($interface4r.val()));
                    $tr.find('td:nth-child(2)').html($ip4r.val());
                    $tr.find('td:nth-child(3)').html($sub4r.val());
                    $tr.find('td:nth-child(4)').html($g4r.val());

                    $routeCancleBtn.trigger('click');
                } else {

                    toastr.error(data);
                }
            },
            error: function (data) {

                toastr.error(data);
            }
        });
    });

    $routeCancleBtn.click(function () {

        $routerTable.find("input[type=text]").val("");
        swichBtn($routePlugBtn);
        swichBtn($routeUpdateBtn);
        swichBtn($routeCancleBtn);
    });

    function addInterface(interface) {


        var $tr = $("<tr name=" + interface.uuid + ">" +
            "<td>" + ethMap.get(interface.ethName) + "</td>" +
            "<td>" + interface.ip + "</td>" +
            "<td>" + interface.subnet + "</td>" +
            "<td>" + interface.gateway + "</td>" +
            "<td>" +
            "<button class='btn btn-info'>修改</button>" +
            "<button class='btn btn-red'>删除</button>" +
            "</td>" +
            "</tr>");
        $tr.on('click', "td button:nth-child(1)", function () {

            if ($plusBtn.css('display') != 'none') {

                swichBtn($updateBtn);
                swichBtn($cancleBtn);
                swichBtn($plusBtn);
            }
            $ip.val(interface.ip);
            $sub.val(interface.subnet);
            $g.val(interface.gateway);
            $ip.attr('name', interface.uuid);

            $interface.children().each(function (i, e) {

                if (interface.ethName == $(this).val()) {

                    $(this).attr('selected', true);
                } else {

                    $(this).removeAttr('selected');
                }
            });
        });

        $tr.on('click', "td button:nth-child(2)", function () {


            $.ajax({

                url: contextPath + 'system/network/delete',
                data: {'id': interface.uuid},
                method: 'POST',
                dataType: 'json',
                success: function (data) {

                    if (data.code == 200) {

                        toastr.info(data.message);
                        $tr.remove();
                    } else {

                        toastr.error(data.message);
                    }
                }
            });

        });
        $interfacePanel.find('table').first().append($tr);
    }

    function addRouter(router) {


        var $tr = $("<tr name=" + router.uuid + ">" +
            "<td>" + ethMap.get(router.interfaceName) + "</td>" +
            "<td>" + router.ip + "</td>" +
            "<td>" + router.subnet + "</td>" +
            "<td>" + router.gateway + "</td>" +
            "<td>" +
            "<button class='btn btn-info'>修改</button>" +
            "<button class='btn btn-red'>删除</button>" +
            "</td>" +
            "</tr>");
        $routerPanel.find('table').first().append($tr);
        $tr.on('click', 'button:nth-child(1)', function () {

            if ($routePlugBtn.css('display') != 'none') {

                swichBtn($routePlugBtn);
                swichBtn($routeUpdateBtn);
                swichBtn($routeCancleBtn);
            }

            $ip4r.val(router.ip);
            $sub4r.val(router.subnet);
            $g4r.val(router.gateway);
            $ip4r.attr('name', router.uuid);

            $interface4r.children().each(function (i, e) {

                if (router.interfaceName == $(this).val()) {

                    $(this).attr('selected', true);
                }
            });


        });

        $tr.on('click', 'button:nth-child(2)', function () {

            $.ajax({

                url: contextPath + 'system/router/delete',
                data: {'id': router.uuid},
                method: 'POST',
                dataType: 'json',
                success: function (data) {

                    if (data.code == 200) {

                        toastr.info(data.message);
                        $tr.remove();
                    } else {

                        toastr.error(data.message);
                    }
                }
            });

        });
    }

    function swichBtn(btn) {
        if (btn.css('display') == 'none') {
            btn.css('display', 'block');
        } else {
            btn.css('display', 'none');
        }
    }

    function addOptions(k, v, t) {
        var $option = $("<option value=" + k + ">" + v + "</option>");
        t.find("select.form-control").append($option);
    }

    function ipTest(ip) {
        var regStr = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/;
        if (!ip) {
            return false;
        } else if ($.trim(ip) == "0.0.0.0") {
            return false;
        } else {
            return ip.match(regStr);
        }
    }
});
