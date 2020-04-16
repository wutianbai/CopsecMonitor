jQuery(function () {
    let DEVICE_NORMARL = contextPath + "static/images/server/kebo/server-32n1.svg",
        DEVICE_WARNING = contextPath + "static/images/server/kebo/server-32n1.svg",
        DEVICE_ERROR = contextPath + "/static/images/server/kebo/server-32e1.svg",
        START_MESSAGE = "信息未上报",
        WARNING_STATUS = 2,
        NORMAL_STATUS = 1,
        ERROR_STATUS = 0,
        ERROR_COLOR = "#cb5353",
        NORMAL_COLOR = "#5ab95d",
        WARNING_COLOR = "#ffff00";

    let SWITCH_NORMAL = contextPath + "static/images/server/switch/switch-32-n1.svg",
        SWITCH_WARNING = contextPath + "static/images/server/switch/switch-32-n1.svg",
        SWITCH_ERROR = contextPath + "static/images/server/switch/switch-32-e1.svg",
        WIN_NORMAL = contextPath + "static/images/server/windows/win-32-n1.svg",
        WIN_WARNING = contextPath + "static/images/server/windows/win-32-n1.svg",
        WIN_ERROR = contextPath + "static/images/server/windows/win-32-e1.svg",
        NET_NORMAL = contextPath + "static/images/server/others/net-32-n1.svg",
        NET_WARNING = contextPath + "static/images/server/others/net-32-n1.svg",
        NET_ERROR = contextPath + "static/images/server/others/net-32-e1.svg",
        FIREWALL_NORMAL = contextPath + "static/images/server/firewall/firewall-32n1.svg",
        FIREWALL_WARNING = contextPath + "static/images/server/firewall/firewall-32n1.svg",
        FIREWALL_ERROR = contextPath + "static/images/server/firewall/firewall-32e1.svg",
        DX_NORMAL = contextPath + "static/images/server/kebo/dx-32n.svg",
        DX_WARNING = contextPath + "static/images/server/kebo/dx-32n.svg",
        DX_ERROR = contextPath + "static/images/server/kebo/dx-32e.svg";

    let opts = {
        "closeButton": true,
        "debug": false,
        "positionClass": "toast-bottom-right",
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "2000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };

    let linkStyle = [
        {
            selector: 'node[zone="no"][deviceType="kebo"]',
            style: {
                "label": "data(deviceHostname)",
                "text-valign": "bottom",
                "text-halign": "center",
                "width": 50,
                "height": 50,
                'background-image': DEVICE_WARNING,
                'background-color': '#fff',
                'border-color': '#fff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity': 0,
                'color': "#14d1da",
                'shape': 'rectangle',
                'font-size': 12
            }
        },
        {
            selector: 'node[zone="no"][deviceType="switch"]',
            style: {
                "label": "data(deviceHostname)",
                "text-valign": "bottom",
                "text-halign": "center",
                "width": 50,
                "height": 50,
                'background-image': SWITCH_WARNING,
                'background-color': '#fff',
                'border-color': '#fff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity': 0,
                'color': "#14d1da",
                'shape': 'rectangle',
                'font-size': 12
            }
        },
        {
            selector: 'node[zone="no"][deviceType="win"]',
            style: {
                "label": "data(deviceHostname)",
                "text-valign": "bottom",
                "text-halign": "center",
                "width": 50,
                "height": 50,
                'background-image': WIN_WARNING,
                'background-color': '#fff',
                'border-color': '#fff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity': 0,
                'color': "#14d1da",
                'shape': 'rectangle',
                'font-size': 12
            }
        },
        {
            selector: 'node[zone="no"][deviceType="net"]',
            style: {
                "label": "data(deviceHostname)",
                "text-valign": "bottom",
                "text-halign": "center",
                "width": 50,
                "height": 50,
                'background-image': NET_WARNING,
                'background-color': '#fff',
                'border-color': '#fff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity': 0,
                'color': "#14d1da",
                'shape': 'rectangle',
                'font-size': 12
            }
        },
        {
            selector: 'node[zone="no"][deviceType="firewall"]',
            style: {
                "label": "data(deviceHostname)",
                "text-valign": "bottom",
                "text-halign": "center",
                "width": 50,
                "height": 50,
                'background-image': FIREWALL_WARNING,
                'background-color': '#fff',
                'border-color': '#fff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity': 0,
                'color': "#14d1da",
                'shape': 'rectangle',
                'font-size': 12
            }
        },
        {
            selector: 'node[zone="no"][deviceType="dx"]',
            style: {
                "label": "data(deviceHostname)",
                "text-valign": "bottom",
                "text-halign": "center",
                "width": 50,
                "height": 50,
                'background-image': DX_WARNING,
                'background-color': '#fff',
                'border-color': '#fff',
                'border-width': 0,
                'border-opacity': 0.5,
                'background-opacity': 0,
                'color': "#14d1da",
                'shape': 'rectangle',
                'font-size': 12
            }
        },
        {
            selector: '.point',
            style: {
                "width": 6,
                "height": 6,
                'background-color': '#3fff07',
                'border-color': '#3eff10',
                'border-width': 1,
                'border-opacity': 1,
                'background-opacity': 1,
                'shape': 'ellipse'
            }
        },
        {
            selector: '.pointWarning',
            style: {
                "width": 6,
                "height": 6,
                'background-color': '#FFFF00',
                'border-color': '#FFFF00',
                'border-width': 1,
                'border-opacity': 1,
                'background-opacity': 1,
                'shape': 'ellipse'
            }
        },
        {
            selector: '.pointError',
            style: {
                "width": 6,
                "height": 6,
                'background-color': '#ff1908',
                'border-color': '#ff100a',
                'border-width': 1,
                'border-opacity': 1,
                'background-opacity': 1,
                'shape': 'ellipse'
            }
        },
        {
            selector: 'edge.taxi',
            style: {
                'background-color': '#120dfd',
                'line-style': 'dashed',
                'curve-style': 'haystack',
                'width': 2,
                'target-arrow-color': '#a4e30a',
                'line-fill': 'linear-gradient',
                'line-dash-pattern': [8, 3],
                'line-dash-offset': -64,
                'line-cap': 'round',
                'line-gradient-stop-colors': 'cyan',
                'line-gradient-stop-positions': '100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function': 'ease'
            }
        }, {
            selector: 'edge.taxi42',
            style: {
                'line-color': '#fff000',
                "curve-style": "taxi",
                'line-style': 'dashed',
                'width': 2,
                'target-arrow-color': '#a4e30a',
                'line-fill': 'linear-gradient',
                'line-dash-pattern': [8, 3],
                'line-dash-offset': -64,
                'line-cap': 'round',
                'line-gradient-stop-colors': 'cyan',
                'line-gradient-stop-positions': '100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function': 'ease'
            }
        },
        {
            selector: 'edge.straight',
            style: {
                'background-color': '#120dfd',
                'curve-style': 'haystack',
                'line-style': 'dashed',
                'width': 2,
                'target-arrow-color': '#a4e30a',
                'line-fill': 'linear-gradient',
                'line-dash-pattern': [8, 3],
                'line-dash-offset': -64,
                'line-cap': 'round',
                'line-gradient-stop-colors': 'cyan',
                'line-gradient-stop-positions': '100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function': 'ease'
            }
        },
        {
            selector: 'edge.straight42',
            style: {
                'curve-style': 'haystack',
                'line-style': 'dashed',
                'width': 2,
                'target-arrow-color': '#a4e30a',
                'line-fill': 'linear-gradient',
                'line-dash-pattern': [8, 3],
                'line-dash-offset': -64,
                'line-cap': 'round',
                'line-gradient-stop-colors': 'cyan',
                'line-gradient-stop-positions': '100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function': 'ease'
            }
        },
        {
            selector: 'edge.unbundled-bezier',
            style: {
                'curve-style': 'haystack',
                'line-style': 'dashed',
                'width': 2,
                'target-arrow-color': '#a4e30a',
                'line-fill': 'linear-gradient',
                'line-dash-pattern': [8, 3],
                'line-dash-offset': -64,
                'line-cap': 'round',
                'line-gradient-stop-colors': 'cyan',
                'line-gradient-stop-positions': '100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function': 'ease'
            }
        },
        {
            selector: 'edge.unbundled-bezier42',
            style: {
                'curve-style': 'haystack',
                'line-style': 'dashed',
                'width': 2,
                'target-arrow-color': '#a4e30a',
                'line-fill': 'linear-gradient',
                'line-dash-pattern': [8, 3],
                'line-dash-offset': -64,
                'line-cap': 'round',
                'line-gradient-stop-colors': 'cyan',
                'line-gradient-stop-positions': '100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function': 'ease'
            }
        }, {
            selector: 'node[zone="yes"]',
            style: {
                "label": "data(name)",
                'shape': 'roundrectangle',
                'text-valign': 'top',
                'height': 'auto',
                'width': 'auto',
                'background-color': 'data(backgroundColor)',
                'background-opacity': 0.333,
                'color': '#14d1da',
                'text-outline-width': 0,
                'font-size': 12
            }
        }, {
            selector: ('.highlightedIn'),
            style: {
                'background-color': '#120dfd',
                'line-style': 'dashed',
                'curve-style': 'haystack',
                'width': 2,
                'line-height': 2,
                'target-arrow-color': '#120dfd',
                'line-fill': 'linear-gradient',
                'line-dash-pattern': [6, 3],
                'line-dash-offset': -48,
                'line-cap': 'round',
                'line-gradient-stop-colors': '#120dfd',
                'line-gradient-stop-positions': '100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function': 'ease-in'
            }
        },
        {
            selector: ('.highlightedWarning'),
            style: {
                'background-color': '#ffff00',
                'line-style': 'dashed',
                'curve-style': 'haystack',
                'width': 2,
                'target-arrow-color': '#ffff00',
                'line-fill': 'linear-gradient',
                'line-dash-pattern': [6, 3],
                'line-dash-offset': -48,
                'line-cap': 'round',
                'line-gradient-stop-colors': '#ffff00',
                'line-gradient-stop-positions': '100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function': 'ease-in'
            }
        },
        {
            selector: ('.highlightedError'),
            style: {
                'background-color': '#fc270c',
                'line-style': 'dashed',
                'curve-style': 'haystack',
                'width': 2,
                'target-arrow-color': '#fc270c',
                'line-fill': 'linear-gradient',
                'line-dash-pattern': [6, 3],
                'line-dash-offset': -48,
                'line-cap': 'round',
                'line-gradient-stop-colors': '#fc270c',
                'line-gradient-stop-positions': '100%',
                'transition-property': 'background-color,target-arrow-color,line-dash-pattern,line-fill,line-dash-offset,line-cap,line-style',
                'transition-duration': '0.9s',
                'transition-timing-function': 'ease-in'
            }
        }
    ];

    /**
     * 初始化
     */
    let cy = cytoscape({
        container: document.getElementById('cy'),
        style: linkStyle,
        layout: {
            name: 'grid',
            fit: true
        },
        userZoomingEnabled: false,
        userPanningEnabled: false,
        zoom: 1, pan: {x: 0, y: 0},
        maxZoom: 5,
        minZoom: 0.5,
        pixelRatio: '1.0',
        touchTapThreshold: 8
    });

    /*cy.panzoom();*/
    function copsecStatus(k, v) {
        this.key = k;
        this.v = v;
    }

    let statusMap = new Array();
    $.when($.ajax({
        url: contextPath + 'node/get',
        method: 'GET',
        dataType: 'json',
        syn: true
    })).done(function (data) {
        if (data.code === 200) {
            cy.add(data.data[2]);
            cy.add(data.data[0]);
            cy.add(data.data[1]);

            $("#monitorUserId").append('<option value="">无</option>');
            $.each(data.data[3], function (index, value) {
                $("#monitorUserId").append("<option value=" + value.userId + ">" + value.userName + "</option>");
            });
            reloadSelect("monitorUserId", "选择运维负责人...");

            cy.nodes().each(function (ele) {
                if (ele.data("zone") === "no") {
                    showStatus(ele, START_MESSAGE);

                    //设备初始状态都标红
                    updateNode(ele, ERROR_STATUS);
                }
            });
        } else {
            toastr.error("加载设备失败", "系统提示", opts);
        }
    });

    /**
     * 连接选择样式设置
     */
    cy.on('click', 'edge', function (evt) {
        let node = evt.target;
        node.style("line-color", "#939002");
        node.style('target-arrow-color', '#939002');
    });

    /**
     * 节点选择样式设置
     */
    cy.on('click', 'node', function (evt) {
        let node = evt.target;
        node.style("border-color", "#939002");
        node.style("border-width", 3);
        let tip = getDevicePopperById(evt.target.data('id'));
        if (tip && !tip.state.isShown) {
            tip.show();
        }
    });

    cy.on('unselect', 'edge', function (evt) {
        evt.target.style("line-color", "#fff000");
        evt.target.style('target-arrow-color', '#fff000');
    });

    cy.on('unselect', 'node', function (evt) {
        evt.target.style("border-color", "#fff");
        evt.target.style("border-width", 0);
        let tip = getDevicePopperById(evt.target.data('id'));
        if (tip) {
            if (tip.state.isShown) {
                tip.hide();
            }
        }
    });

    let p = new Object();
    cy.on('grabon', 'node', function (evt) {
        let node = evt.target;
        p.x = node.position("x");
        p.y = node.position("y");
        let tip = getDevicePopperById(node.data('id'));
        if (tip) {
            if (tip.state.isShown) {
                tip.hide();
            }
        }
    });

    cy.on('dragfreeon', 'node', function (evt) {
        let node = evt.target;
        if ((node.position("x") < 0 || node.position("x") > cy.width()) ||
            (node.position("y") < 0 || node.position("y") > cy.height())) {

            toastr.error("设备超出可见范围!");
            evt.target.position({x: p.x, y: p.y});
        }
    });

    $("body").on('click', '.col-sm-10 vertical-top button', function () {
        if ($(this).parent().hasClass('open')) {
            $(this).parent().removeClass('open');
        } else {
            $('.btn-group').removeClass('open');
            $(this).parent().addClass('open');
        }
    });

    $("#deviceType").select2({
        placeholder: "选择设备类型...",
        allowClear: true
    }).on('select2-open', function () {
        // Adding Custom Scrollbar
        $(this).data('select2').results.addClass('overflow-hidden').perfectScrollbar();
    });

    let $zones = $("#zone");

    function reloadZone() {
        let zones = getNetworkZone();
        $zones.empty();
        $zones.append("<option value=''>无</option>");
        if (zones != null) {
            zones.forEach(function (e, i) {
                $zones.append("<option value=" + e.data.id + ">" + e.data.name + "</option>");
            });
        }
        reloadSelect("zone", "选择网络区域...");
    }

    /**
     * 添加设备
     */
    $("#addDevice").on('click', function () {
        reloadZone();
        clearCondition("deviceModal");//清空数据

        let $confirmButton = $("<button class='btn btn-success'>保存</button>");
        addButton("deviceModal", "添加设备", $confirmButton);
        $confirmButton.on('click', '', function () {
            // let deviceId = Math.uuidFast(),
            let deviceId = $("#deviceId").val(),
                deviceHostname = $("#deviceHostname").val(),
                deviceType = $("#deviceType").val(),
                deviceIP = $("#deviceIP").val(),
                monitorUserId = $("#monitorUserId").val(),
                parentId = $zones.val(),
                x = 300,
                y = 300;

            if (isEmpty(deviceId)) {
                toastr.error("请输入设备ID", opts);
                return false;
            } else {
                if (!isId(deviceId)) {
                    toastr.error("请输入正确的设备ID", opts);
                    return false;
                }
            }

            if (isEmpty(deviceHostname)) {
                toastr.error("请输入设备主机名", opts);
                return false;
            }

            if (isEmpty(deviceType)) {
                toastr.error("请选择设备类型", opts);
                return false;
            }

            if (isEmpty(deviceIP)) {
                toastr.error("请输入设备IP地址", opts);
                return false;
            } else {
                if (!ipTest(deviceIP)) {
                    toastr.error("请输入正确的IP地址", opts);
                    return false;
                }
            }

            // if (isEmpty(monitorUserId)) {
            //     toastr.error("请选择运维负责人", opts);
            //     return false;
            // }

            if (!isNumber(x) || !isNumber(y)) {
                toastr.error("请输入数字坐标", opts);
                return false;
            }

            $.ajax({
                url: contextPath + "node/device/add",
                data: {
                    'data.id': deviceId,
                    'data.deviceId': deviceId,
                    'data.deviceHostname': deviceHostname,
                    'data.deviceType': deviceType,
                    'data.deviceIP': deviceIP,
                    'data.monitorUserId': monitorUserId,
                    'data.parent': parentId,
                    'position.x': parseInt(x),
                    'position.y': parseInt(y)
                },
                method: 'POST',
                dataType: 'json',
                success: function (data) {
                    if (data.code === 200) {
                        cy.add(data.data);
                        toastr.info("添加设备成功");
                        showStatus(cy.$id(deviceId), START_MESSAGE);
                        updateNode(cy.$id(deviceId), ERROR_STATUS);
                        $("#deviceModal").modal('hide');
                    } else {
                        toastr.error(data.message, opts);
                    }
                }
            });
        });
    });

    /**
     * 更新设备
     */
    $("#editDevice").on('click', function () {
        let node = getNode();
        if (!node) {
            return false;
        }

        if (node.data("zone") === "yes" || node.group() === "edges") {
            toastr.error("请选择设备");
            return false;
        }

        reloadZone();

        $("#deviceId").val(node.data('id')).attr('disabled', 'true');
        $("#deviceHostname").val(node.data('deviceHostname'));
        $("#deviceType").val(node.data('deviceType')).trigger('change');
        $("#deviceIP").val(node.data('deviceIP'));
        $("#monitorUserId").val(node.data('monitorUserId')).trigger('change');
        $zones.val(node.data('parent')).trigger('change');

        let $updateButton = $("<button class='btn btn-success'>保存</button>");
        addButton("deviceModal", "更新设备", $updateButton);
        $updateButton.on('click', function () {
            let deviceHostname = $("#deviceHostname").val(),
                deviceType = $("#deviceType").val(),
                deviceIP = $("#deviceIP").val(),
                monitorUserId = $("#monitorUserId").val(),
                parentId = $zones.val();

            if (isEmpty(deviceHostname)) {
                toastr.error("请输入设备主机名", opts);
                return false;
            }

            if (isEmpty(deviceType)) {
                toastr.error("请选择设备类型", opts);
                return false;
            }

            if (isEmpty(deviceIP)) {
                toastr.error("请输入设备IP地址", opts);
                return false;
            } else {
                if (!ipTest(deviceIP)) {
                    toastr.error("请输入正确的IP地址", opts);
                    return false;
                }
            }

            // if (isEmpty(monitorUserId)) {
            //     toastr.error("请选择运维负责人", opts);
            //     return false;
            // }

            $.ajax({
                url: contextPath + 'node/device/update',
                data: {
                    'data.id': node.data('id'),
                    'data.deviceId': node.data('id'),
                    'data.deviceHostname': deviceHostname,
                    'data.deviceType': deviceType,
                    'data.deviceIP': deviceIP,
                    'data.monitorUserId': monitorUserId,
                    'data.parent': parentId,
                    'position.x': node.position('x'),
                    'position.y': node.position('y')
                },
                method: 'POST',
                dataType: 'json',
                success: function (data) {
                    if (data.code === 200) {
                        toastr.info("更新设备成功");
                        node.data("deviceHostname", data.data.data.deviceHostname);
                        node.data("deviceType", data.data.data.deviceType);
                        node.data("deviceIP", data.data.data.deviceIP);
                        node.data("monitorUserId", data.data.data.monitorUserId);
                        node.position(data.data.position);
                        if (data.data.data.parent === "") {
                            node.move({parent: null});
                        } else {
                            node.move({parent: data.data.data.parent});
                        }
                    } else {
                        toastr.error("更新设备失败");
                    }

                    $("#deviceModal").modal('hide');
                }
            });
        });
    });

    /**
     * 删除设备
     */
    $("#deleteDevice").on('click', function () {
        let node = getNode();
        if (!node && !node.isChild()) {
            return false;
        }

        if (node.data("zone") === "yes" || node.group() === "edges") {
            toastr.error("请选择设备");
            return false;
        }

        let $confirmButton = $("<button class='btn btn-success'>确认</button>");
        let $message = $('<h2 style="text-align: center">确认删除设备[' + node.data('deviceHostname') + ']？</h2>');
        $("#message").find(".modal-body").html($message);
        addButton("message", "系统提示", $confirmButton);
        $confirmButton.on('click', function () {
            $.ajax({
                url: contextPath + 'node/device/delete',
                data: {
                    'data.deviceId': node.data('id')
                },
                method: 'POST',
                dataType: 'json',
                success: function (data) {
                    if (data.code === 200) {
                        toastr.info("删除设备成功");
                        cy.remove(node);
                        let s = getDevicePopperById(node.data('id'));
                        s.destroy();
                    } else {
                        toastr.error("删除设备失败");
                    }

                }
            });
            $("#message").modal('hide');
        });
    });

    let $start = $("#linkStart"),
        $end = $("#linkEnd"),
        $linkStyle = $("#linkStyle");
    /**
     *添加连接
     */
    $("#addLink").on('click', function () {
        if (cy.nodes().length === 0) {
            toastr.error("无设备信息，请添加设备", opts);
            return false;
        }

        addData($start, $end, true, null);

        let $confirmButton = $("<button class='btn btn-success'>保存</button>");
        addButton("linkModal", "添加连接", $confirmButton);
        $confirmButton.on("click", function () {
            if (isEmpty($start.val())) {
                toastr.error("请选择起始设备", opts);
                return false;
            }

            if (isEmpty($end.val())) {
                toastr.error("请选择终点设备", opts);
                return false;
            }

            // if (isEmpty($linkStyle.val())) {
            //     toastr.error("请选择连线样式", opts);
            //     return false;
            // }

            $.ajax({
                url: contextPath + 'node/link/add',
                data: JSON.stringify({
                    'source': $start.val(),
                    'targets': $end.val(),
                    // 'classes': $linkStyle.val()
                    'classes': 'straight'
                }),
                method: 'POST',
                dataType: "json",
                contentType: "application/json;charset=utf-8",
                success: function (data) {
                    if (data.code === 200 && data.data.length > 0) {
                        cy.add(data.data);
                        toastr.info("添加连接成功");
                    } else {
                        toastr.error("添加连接失败");
                    }
                    $("#linkModal").modal('hide');
                }
            });
        });
    });

    /**
     * 更新连接
     */
    $("#editLink").on('click', function () {
        let node = getNode();
        if (!node) {
            return false;
        }

        if (node.group() !== "edges") {
            toastr.error("请选择连接");
            return false;
        }

        addData($start, $end, false, node);
        // $("#linkStyle").val(node.style('curve-style')).trigger('change');

        let $confirmButton = $("<button class='btn btn-success'>保存</button>");
        addButton("linkModal", "更新连接", $confirmButton);
        $confirmButton.on('click', function () {
            if (isEmpty($start.val())) {
                toastr.error("请选择起始设备", opts);
                return false;
            }

            if (isEmpty($end.val())) {
                toastr.error("请选择终点设备", opts);
                return false;
            }

            // if (isEmpty($linkStyle.val())) {
            //     toastr.error("请选择连线样式", opts);
            //     return false;
            // }

            $.ajax({
                url: contextPath + 'node/link/update',
                data: JSON.stringify({
                    'id': node.data('id'),
                    'source': $start.val(),
                    'targets': $end.val(),
                    // 'classes': $linkStyle.val()
                    'classes': 'straight'
                }),
                method: 'POST',
                dataType: "json",
                contentType: "application/json;charset=utf-8",
                success: function (data) {
                    if (data.code === 200) {
                        cy.remove(node);
                        cy.add(data.data);
                        toastr.info("更新连接成功");
                    } else {
                        toastr.error("更新连接失败");
                    }
                    $("#linkModal").modal('hide');
                }
            });
        });
    });

    /**
     * 删除连接
     */
    $("#deleteLink").on('click', function () {
        let node = getNode();
        if (!node) {
            return false;
        }

        if (node.group() !== "edges") {
            toastr.error("请选择连接");
            return false;
        }

        let $confirmButton = $("<button class='btn btn-success'>确认</button>");
        let $message = $('<h2 style="text-align: center">确认删除此连接？</h2>');
        $("#message").find(".modal-body").html($message);
        addButton("message", "系统提示", $confirmButton);
        $confirmButton.on('click', function () {
            $.ajax({
                url: contextPath + 'node/link/delete',
                data: {
                    'data.id': node.data('id')
                },
                method: 'POST',
                dataType: "json",
                success: function (data) {
                    if (data.code === 200) {
                        cy.remove(node);
                        toastr.info("删除连接成功");
                    } else {
                        toastr.error("删除连接失败");
                    }
                    $("#message").modal('hide');
                }
            });
        });
    });

    /**
     * 添加网络区域
     */
    $("#addZone").on('click', function () {
        let $confirmButton = $("<button class='btn btn-success'>保存</button>");
        $("#zoneModal").find("input[type=text]").val("");
        addButton("zoneModal", "添加网络区域", $confirmButton);
        $confirmButton.on('click', function () {
            let zoneId = Math.uuidFast(),
                zoneName = $("#zoneName").val(),
                zoneColor = $("#zoneColor").val();
            $("#zoneId").removeAttrs("disabled");

            if (isEmpty(zoneId)) {
                toastr.error("请输入网络区域ID");
                return false;
            }

            if (isEmpty(zoneName)) {
                toastr.error("请输入网络区域名称");
                return false;
            }

            $.ajax({
                url: contextPath + 'node/zone/add',
                data: {
                    'data.zone': "yes",
                    'data.id': zoneId,
                    'data.name': zoneName,
                    'data.backgroundColor': zoneColor,
                    'position.x': 300,
                    'position.y': 300
                },
                method: 'POST',
                dataType: 'json',
                success: function (data) {
                    if (data.code === 200) {
                        toastr.info("添加网络区域成功");
                        cy.add(data.data);
                        cy.$("#" + zoneId).style("background-color", color);
                    } else {
                        toastr.error(data.message);
                    }
                },
                error: function (jqx, status, err) {
                    toastr.error(err);
                }
            });
            $("#zoneModal").modal("hide");
        });
    });

    /**
     * 更新网络区域
     */
    $("#editZone").on('click', function () {
        let node = getNode();
        if (!node) {
            return false;
        }

        if (node.data("zone") === "no") {
            toastr.error("请选择网络区域");
            return false;
        }

        $("#zoneName").val(node.data("name"));
        $("#zoneColor").val(node.data("backgroundColor"));

        let $confirmButton = $("<button class='btn btn-success'>保存</button>");
        addButton("zoneModal", "更新网络区域", $confirmButton);
        $confirmButton.on('click', function () {
            let zoneName = $("#zoneName").val(),
                zoneColor = $("#zoneColor").val();

            if (isEmpty(zoneName)) {
                toastr.error("请输入网络区域名称");
                return false;
            }

            $.ajax({
                url: contextPath + 'node/zone/update',
                data: {
                    'data.id': node.data("id"),
                    'data.name': zoneName,
                    'data.backgroundColor': zoneColor,
                    'position.x': node.position('x'),
                    'position.y': node.position('y')
                },
                method: 'POST',
                dataType: 'json',
                success: function (data) {
                    if (data.code === 200) {
                        toastr.info("更新网络区域成功");
                        node.data("name", data.data.data.name);
                        node.position(data.data.position);
                        cy.$("#" + node.data("id")).style("background-color", zoneColor);
                    } else {
                        toastr.error(data.message);
                    }
                    $("#zoneModal").modal("hide");
                },
                error: function (jqx, status, err) {
                    toastr.error(err);
                }
            });
        });
    });

    /**
     * 删除网络区域
     */
    $("#deleteZone").on('click', function () {
        let node = getNode();
        if (!node) {
            return false;
        }

        if (node.data("zone") === "no") {
            toastr.error("请选择网络区域");
            return false;
        }

        if (node.isParent() && !node.isChildless()) {
            toastr.error("该区域包含设备，不能删除!");
            return false;
        }

        let $confirmButton = $("<button class='btn btn-success'>确认</button>");
        let $message = $('<h2 style="text-align: center">确认删除网络区域[' + node.data('name') + ']？</h2>');
        $("#message").find(".modal-body").html($message);
        addButton("message", "系统提示", $confirmButton);
        $confirmButton.on('click', function () {
            $.ajax({
                url: contextPath + 'node/zone/delete',
                data: {
                    'data.id': node.data('id')
                },
                method: 'POST',
                dataType: 'json',
                success: function (data) {
                    if (data.code === 200) {
                        toastr.info("删除网络区域成功");
                        let s = getDevicePopperById(node.data('id'));
                        cy.remove(node);
                        if (null != s && s) {
                            s.destroy();
                        }
                    } else {
                        toastr.error("删除网络区域失败");
                    }
                    $("#message").modal('hide');
                }
            });
        });
    });

    /**
     * 更新拓扑结构
     */
    $("#topologyUpdate").on('click', function () {
        let positions = new Array();
        cy.nodes().forEach(function (e, i) {
            let position = new Object();
            position.id = e.data("id");
            position.x = e.position("x");
            position.y = e.position("y");
            positions.push(position);
        });

        if (positions.length > 0) {
            $.ajax({
                url: contextPath + "node/topology/update",
                method: 'POST',
                dataType: 'json',
                contentType: "application/json;charset=utf-8",
                data: JSON.stringify(positions),
                success: function (data) {
                    if (data.code === 200) {
                        toastr.info(data.message);
                    } else {
                        toastr.error(data.message);
                    }
                },
                error: function (jqx, status, error) {
                    toastr.error(status + error);
                }
            });
        }
    });

    /**
     * 获取所有网络区域
     * @returns {*}
     */
    function getNetworkZone() {
        let zones;
        $.ajax({
            url: contextPath + "node/zone/get",
            method: 'GET',
            dataType: 'json',
            async: false,
            success: function (data) {
                if (data.code === 200) {
                    zones = data.data;
                } else {
                    zones = null;
                }
            },
            error: function (jqx, status, error) {
                toastr.error(error);
            }
        });
        return zones;
    }

    function getNode() {
        let node = cy.$(":selected");
        if (node.length === 0) {
            toastr.error("请选择节点", opts);
            return false;
        }
        return node;
    }

    function addData(start, end, isAdd, node) {
        start.empty();
        end.empty();

        cy.nodes().forEach(function (e, i) {
            if (typeof e.data('deviceHostname') != "undefined" && e.data('zone') === "no") {
                start.append("<option value=" + e.data('id') + ">" + e.data('deviceHostname') + "</option>");
                end.append("<option value=" + e.data('id') + ">" + e.data('deviceHostname') + "</option>");
            }
        });

        if (!isAdd) {
            $start.val(node.data('source')).trigger("change");
            $end.val(node.data('target')).trigger("change");
        }

        start.select2(
            {
                placeholder: '选择起始设备...',
                allowClear: true
            }
        ).on('select2-open', function () {
            $(this).data('select2').results.addClass('overflow-hidden').perfectScrollbar();
        });

        end.select2({
            placeholder: '选择终点设备...',
            allowClear: true
        }).on('select2-open', function () {
            // Adding Custom Scrollbar
            $(this).data('select2').results.addClass('overflow-hidden').perfectScrollbar();
        });
    }

    /*
    *
    * 用于显示设备状态信息，并保存当前设备状态与tip之间的关系
    *
    * */
    function showStatus(node, text) {
        let tip = tippy(node.popperRef(), {
            content: function () {
                let div = document.createElement('div');
                div.innerHTML = text;
                return div;
            },
            trigger: 'manual',
            arrow: true,
            placement: 'top',
            hideOnClick: false,
            multiple: true,
            sticky: true,
            updateDuration: 30,
            interactive: true,
        });
        statusMap.push(new copsecStatus(node.data('id'), tip));
        tip.hide();
    }

    function getDeviceStatus() {
        $.ajax({
            url: contextPath + 'node/status/' + $.now(),
            method: 'GET',
            dataType: 'json',
            success: function (data) {
                if (data.code === 200) {
                    let m = data.data;//statusMap
                    for (let k in m) {
                        let v = m[k];//deviceStatusBean
                        let _text = "";
                        _text = getStatusText(v);
                        if (v.status === ERROR_STATUS) {
                            if (typeof (cy.$id(k).data('deviceHostname')) !== "undefined") {
                                // toastr.error(cy.$id(k).data('deviceHostname') + " 设备状态异常!", "系统提示", opts);
                                updateStatus(cy.$id(k), ERROR_COLOR, _text);
                                updateNode(cy.$id(k), ERROR_STATUS);
                                updateEdges(cy.$id(k), "highlightedError");
                            }
                        } else if (v.status === WARNING_STATUS) {
                            if (typeof (cy.$id(k).data('deviceHostname')) !== "undefined") {
                                // toastr.error(cy.$id(k).data('deviceHostname') + " 设备上报超时!", "系统提示", opts);
                                // updateStatus(cy.$id(k), WARNING_COLOR, _text);
                                // updateNode(cy.$id(k), WARNING_STATUS);
                                // updateEdges(cy.$id(k), "highlightedWarning");
                                updateStatus(cy.$id(k), ERROR_COLOR, _text);
                                updateNode(cy.$id(k), ERROR_STATUS);
                                updateEdges(cy.$id(k), "highlightedError");
                            }
                        } else if (v.status === NORMAL_STATUS) {
                            updateStatus(cy.$id(k), NORMAL_COLOR, _text);
                            updateNode(cy.$id(k), NORMAL_STATUS);
                            updateEdges(cy.$id(k), "highlightedIn");
                        }
                    }
                }
            },
            error: function (ajq, status, error) {
                toastr.error("请求错误或超时");
            }
        });
    }
    getDeviceStatus();
    setInterval(getDeviceStatus, 20000);

    function getStatusText(status) {
        let str = '<table class="table table-condensed">';
        str += "<tr><td>ID:</td><td>" + status.deviceId + "</td></tr>";
        if (!isEmpty(status.message)) {
            $.each(status.message, function (index, value) {
                let s = '';
                if (!isEmpty(value.message)) {
                    if (value.status === 0) {
                        s += '<tr><td name="list" type=' + JSON.stringify(value) + '>' + value.deviceId + '</td><td style="color: yellow">异常</td>';
                    } else {
                        s += '<tr><td name="list" type=' + JSON.stringify(value) + '>' + value.deviceId + '</td><td>正常</td>';
                    }
                    s += '</tr>';
                }
                str += s;
            });
        }
        str += '<tr><td>状态:</td><td>' + status.warnMessage + '</td></tr>';
        str += '<tr><td>更新时间:</td><td>' + status.updateTime + '</td></tr>';
        str += '</table>';

        let div = $(str);
        div.on('click', 'td[name="list"]', function () {
            let title = $(this).text();
            let message = $(this).attr("type");
            $("#showTable").find(".modal-title").text(title);

            let body = showTable($.parseJSON(message));
            $("#showTable").find(".modal-body").html(body);

            $("#showTable").modal('show', {backdrop: 'static'});
        });
        return div;
    }

    /**
     * 更新设备状态
     */
    function updateStatus(node, status_color, message) {
        let x = getDevicePopperById(node.data('id'));
        if (x) {
            $(x.popperChildren.tooltip).css('background-color', status_color);
            $(x.popperChildren.content).css('background-color', status_color);
            $(x.popperChildren.arrow).css('border-top', '8px solid ' + status_color);
            let $content = $(x.popperChildren.tooltip);
            $content.find('div').last().children().remove();
            $content.find('div').last().text("");
            $content.find('div').last().append(message);
            $content.find('div').last().find('table tr td').css('text-align', 'left');
            let ca = node.connectedEdges();
            if (ca.length > 0) {
                for (let i = 0; i < ca.length; i++) {
                    let line = cy.$id(ca[i].data("id"));
                    line.style("line-color", status_color);
                    line.style("target-arrow-color", status_color);
                    line.style("source-arrow-color", status_color);
                }
            }
        }
    }

    function getDevicePopperById(id) {
        for (let i = 0; i < statusMap.length; i++) {
            if (statusMap[i].key === id) {
                return statusMap[i].v;
            }
        }
    }

    function updateNode(node, status) {
        if (node.data("deviceType") === "kebo") {
            if (status === NORMAL_STATUS) {
                node.style('background-image', DEVICE_NORMARL);
            } else if (status === ERROR_STATUS) {
                node.style('background-image', DEVICE_ERROR);
            } else {
                node.style('background-image', DEVICE_WARNING);
            }
        } else if (node.data("deviceType") === "switch") {
            if (status === NORMAL_STATUS) {
                node.style('background-image', SWITCH_NORMAL);
            } else if (status === ERROR_STATUS) {
                node.style('background-image', SWITCH_ERROR);
            } else {
                node.style('background-image', SWITCH_WARNING);
            }
        } else if (node.data("deviceType") === "win") {
            if (status === NORMAL_STATUS) {
                node.style('background-image', WIN_NORMAL);
            } else if (status === ERROR_STATUS) {
                node.style('background-image', WIN_ERROR);
            } else {
                node.style('background-image', WIN_WARNING);
            }
        } else if (node.data("deviceType") === "net") {
            if (status === NORMAL_STATUS) {
                node.style('background-image', NET_NORMAL);
            } else if (status === ERROR_STATUS) {
                node.style('background-image', NET_ERROR);
            } else {
                node.style('background-image', NET_WARNING);
            }
        } else if (node.data("deviceType") === "firewall") {
            if (status === NORMAL_STATUS) {
                node.style('background-image', FIREWALL_NORMAL);
            } else if (status === ERROR_STATUS) {
                node.style('background-image', FIREWALL_ERROR);
            } else {
                node.style('background-image', FIREWALL_WARNING);
            }
        } else if (node.data("deviceType") === "dx") {
            if (status === NORMAL_STATUS) {
                node.style('background-image', DX_NORMAL);
            } else if (status === ERROR_STATUS) {
                node.style('background-image', DX_ERROR);
            } else {
                node.style('background-image', DX_WARNING);
            }
        }
    }

    function updateEdges(node, className) {
        node.connectedEdges().each(function (e, index) {
            e.flashClass(className, 1800);
        });
    }

    function movePoint() {
        cy.edges().each(function (e, index) {
            let sourcePosition = e.sourceEndpoint();
            let targetPosition = e.targetEndpoint();

            let eNode;
            let id = e.data("id") + index;
            if (cy.$("#" + id).group() !== "nodes" && e.hasClass("highlightedError")) {
                eNode = {
                    group: 'nodes',
                    data: {id: id},
                    position: sourcePosition,
                    classes: "pointError"
                };
                cy.add(eNode);
            } else if (cy.$("#" + id).group() !== "nodes" && e.hasClass("highlightedWarning")) {
                eNode = {
                    group: 'nodes',
                    data: {id: id},
                    position: sourcePosition,
                    classes: "pointWarning"
                };
                cy.add(eNode);
            } else if (cy.$("#" + id).group() !== "nodes" && e.hasClass("highlightedIn")) {
                eNode = {
                    group: 'nodes',
                    data: {id: id},
                    position: sourcePosition,
                    classes: "point"
                };
                cy.add(eNode);
            }
            cy.$("#" + id).animate({
                    position: sourcePosition,
                    renderedPosition: targetPosition
                },
                {
                    easing: "ease-in-out-quart",
                    duration: 2000,
                    complete: function () {
                        cy.remove("#" + e.data("id") + index);
                    }
                });
        });
    }
    movePoint();
    setInterval(movePoint, 5000);
});

function addOne(str, index, value) {
    if (value.length > 0) {
        str += '<strong>' + index + '</strong>';
        str += '<table class="table table-bordered table-striped"><tbody>';
        $.each(value, function (index, value) {
            if (!isEmpty(value.message)) {
                if (value.status === 0) {
                    str += '<tr style="text-align: center;color: red;"><td>' + value.message + '</td><td>' + value.result + '</td><td>异常</td></tr>';
                } else {
                    str += '<tr style="text-align: center;"><td>' + value.message + '</td><td>' + value.result + '</td><td>正常</td></tr>';
                }
            }
        });
        str += '</tbody></table>';
    }
    return str;
}

function addApplication(str, index, value) {
    if (value.length > 0) {
        str += '<strong>' + index + '</strong>';
        str += '<table class="table table-bordered table-striped"><tbody>';
        $.each(value, function (index, value) {
            if (!isEmpty(value.message)) {
                if (value.status === 0) {
                    str += '<tr style="text-align: center;color: red;"><td>' + value.message + '</td><td>' + value.result + '</td></tr>';
                } else {
                    str += '<tr style="text-align: center;"><td>' + value.message + '</td><td>' + value.result + '</td></tr>';
                }
            }
        });
        str += '</tbody></table>';
    }
    return str;
}

function addInstances(str, index, value) {
    if (value.length > 0) {
        str += '<strong>' + index + '</strong>';
        str += '<table class="table table-bordered table-striped"><tbody>';
        $.each(value, function (index, value) {
            if (!isEmpty(value.message)) {
                if (value.status === 0) {
                    str += '<tr style="text-align: center;color: red;"><td>' + value.message + '</td><td>' + value.result + '</td><td>已停止</td></tr>';
                } else {
                    str += '<tr style="text-align: center;"><td>' + value.message + '</td><td>' + value.result + '</td><td>正在运行</td></tr>';
                }
            }
        });
        str += '</tbody></table>';
    }
    return str;
}

function addSystem(str, index, value) {
    if (value.length > 0) {
        str += '<strong>' + index + '</strong>';
        str += '<table class="table table-bordered table-striped"><tbody>';
        $.each(value, function (index, value) {
            if (!isEmpty(value.message)) {
                str += '<tr style="text-align: center;"><td>' + value.message + '</td></tr>';
            }
        });
        str += '</tbody></table>';
    }
    return str;
}

function addDisk(str, index, value) {
    if (value.length > 0) {
        str += '<strong>' + index + '</strong>';
        str += '<table class="table table-bordered table-striped"><tbody>';
        str += '<tr style="text-align: center"><td>盘符</td><td>总量</td><td>使用率</td></tr>';
        $.each(value, function (index, value) {
            if (!isEmpty(value.message)) {
                str += '<tr style="text-align: center"><td colspan="' + Object.keys(value.message).length + '">磁盘[' + (index + 1) + ']</td></tr>';
                $.each(value.message, function (index, value) {
                    if (value.status === 0) {
                        str += '<tr style="text-align: center;color: red;"><td>' + value.message + '</td>';
                    } else {
                        str += '<tr style="text-align: center;"><td>' + value.message + '</td>';
                    }
                    if (!isEmpty(value.result)) {
                        str += '<td>' + value.result + '</td>';
                    }
                    if (!isEmpty(value.state)) {
                        str += '<td>' + value.state + '</td>';
                    }
                    str += '</tr>';
                });
            }
        });
        str += '</tbody></table>';
    }
    return str;
}

function addTwo(str, index, value) {
    if (value.length > 0) {
        str += '<strong>' + index + '</strong>';
        str += '<table class="table table-bordered table-striped"><tbody>';
        $.each(value, function (index, value) {
            if (!isEmpty(value.message)) {
                $.each(value.message, function (index, value) {
                    if (value.status === 0) {
                        str += '<tr style="text-align: center;color: red"><td>' + value.message + '</td>';
                    } else {
                        str += '<tr style="text-align: center"><td>' + value.message + '</td>';
                    }
                    if (!isEmpty(value.result)) {
                        str += '<td>' + value.result + '</td>';
                    }
                    if (value.status === 0) {
                        if (index === "USER") {
                            str += '<td>已锁定</td>';
                        } else {
                            str += '<td>异常</td>';
                        }
                    } else {
                        str += '<td>正常</td>';
                    }
                    str += '</tr>';
                });
            }
        });
        str += '</tbody></table>';
    }
    return str;
}

function addCert(str, index, value) {
    if (value.length > 0) {
        str += '<strong>' + index + '</strong>';
        str += '<table class="table table-bordered table-striped"><tbody>';
        // str += '<tr style="text-align: center"><td colspan="12">' + index + '</td></tr>';
        str += '<tr style="text-align: center;"><td>名称</td><td>颁发机构</td><td>起始时间</td><td>有效期时间</td><td>状态</td></tr>';
        $.each(value, function (index, value) {
            if (!isEmpty(value.message)) {
                $.each(value.message, function (index, value) {
                    if (value.status === 0) {
                        str += '<tr style="text-align: center;color: red"><td>' + value.message + '</td>';
                    } else {
                        str += '<tr style="text-align: center"><td>' + value.message + '</td>';
                    }
                    // str += '<td>' + value.subject + '</td>';
                    str += '<td>' + value.issuer + '</td>';
                    str += '<td>' + value.starTime + '</td>';
                    str += '<td>' + value.endTime + '</td>';
                    // str += '<td data-format="YYYY-MM-DD hh:mm:ss">' + value.starTime + '</td>';
                    // str += '<td data-format="YYYY-MM-DD hh:mm:ss">' + value.endTime + '</td>';
                    // str += '<td>' + value.result + '</td>';
                    if (value.status === 0) {
                        str += '<td>异常</td>';
                    } else {
                        str += '<td>正常</td>';
                    }
                    str += '</tr>';
                });
            }
        });
        str += '</tbody></table>';
    }
    return str;
}

function addThree(str, index, value) {
    if (value.length > 0) {
        str += '<strong>' + index + '</strong>';
        str += '<table class="table table-bordered table-striped"><tbody>';
        $.each(value, function (index, value) {
            if (!isEmpty(value.message)) {
                $.each(value.message, function (index, value) {
                    let s = '';
                    s += addOne(str, index, value);
                    str += s;
                });
            }
        });
        str += '</tbody></table>';
    }
    return str;
}

function showTable(monitorType) {
    let body = '<div class="main-content"><div class="panel panel-default"><div class="panel-body"><div class="row"><div class="col-md-12">';
    // if (monitorType.deviceId === "实例状态") {
    //     body += '<table class="table table-bordered table-striped">';
    // }

    if (!isEmpty(monitorType.message)) {
        $.each(monitorType.message, function (index, value) {
            let s = '';
            switch (index) {
                case "CPU":
                    s += addOne(s, index, value);
                    break;
                case "DISK":
                    s += addDisk(s, index, value);
                    break;
                case "MEMORY":
                    s += addOne(s, index, value);
                    break;
                case "USER":
                    s += addTwo(s, index, value);
                    break;
                case "RAID":
                    s += addThree(s, index, value);
                    break;
                case "SYSTEMTYPE":
                    s += addSystem(s, index, value);
                    break;
                case "SYSTEMVERSION":
                    s += addSystem(s, index, value);
                    break;
                case "SYSTEMPATCH":
                    s += addSystem(s, index, value);
                    break;
                case "APPLICATION":
                    s += addApplication(s, index, value);
                    break;
                case "INSTANCES_WEB70":
                    s += addInstances(s, index, value);
                    break;
                case "INSTANCES_WEBPROXY40":
                    s += addInstances(s, index, value);
                    break;
                case "INSTANCES_CONFIG":
                    s += addInstances(s, index, value);
                    break;
                case "INSTANCES_USER":
                    s += addInstances(s, index, value);
                    break;
                case "NETWORK":
                    s += addApplication(s, index, value);
                    break;
                case "ACCESSLOG":
                    s += addOne(s, index, value);
                    break;
                case "PROXYLOG":
                    s += addOne(s, index, value);
                    break;
                case "CERT70":
                    s += addCert(s, index, value);
                    break;
                case "CERT40":
                    s += addCert(s, index, value);
                    break;
                case "IMSERVICE":
                    s += addTwo(s, index, value);
                    break;
            }
            body += s;
        });
    }
    // if (message.deviceId === "实例状态") {
    //     body += '</table>';
    // }
    body += '</div></div></div></div></div>';
    return body;
}