;(function (global, undefined) {

    "use strict";
    var _global;
    var defaultMethods = {
        getRandomCode: "getRandomCode",
        getDeviceBasicInfo: "getDeviceBasicInfo",
        getManageIP: "getManageIP",
        getDeviceIfConfig: "getDeviceIfConfig",
        login: "login",
        getNetworkSetting: "getNetworkSetting",
        getNetworkSettingV6: "getNetworkSettingV6",
        getNetworkBound: "getNetworkBound",
        getSystemCtlInfo: "getSystemCtlInfo",
        getProxyForHttp: "getProxyForHttp",
        getProxyHttpByTaskName: "getProxyHttpByTaskName",
        getProxyHttpSecurityByTaskName: "getProxyHttpSecurityByTaskName",
        getProxyForWebservice: "getProxyForWebservice",
        getProxyWebserviceByTaskName: "getProxyWebserviceByTaskName",
        getProxyWebserviceSecurityByTaskName: "getProxyWebserviceSecurityByTaskName",
        getProxyForTcp: "getProxyForTcp",
        getProxyTcpByTaskName: "getProxyTcpByTaskName",
        getProxyTcpSecurityByTaskName: "getProxyTcpSecurityByTaskName",
        getProxyForUdp: "getProxyForUdp",
        getProxyUdpByTaskName: "getProxyUdpByTaskName",
        getProxyUdpSecurityByTaskName: "getProxyUdpSecurityByTaskName",
        getProxyForSmtp: "getProxyForSmtp",
        getProxySmtpByTaskName: "getProxySmtpByTaskName",
        getProxySmtpSecurityByTaskName: "getProxySmtpSecurityByTaskName",
        getProxyForPop3: "getProxyForPop3",
        getProxyPop3ByTaskName: "getProxyPop3ByTaskName",
        getProxyPop3SecurityByTaskName: "getProxyPop3SecurityByTaskName",
        getProxyForFtp: "getProxyForFtp",
        getProxyFtpByTaskName: "getProxyFtpByTaskName",
        getProxyFtpSecurityByTaskName: "getProxyFtpSecurityByTaskName",
        getProxyForMulticast: "getProxyForMulticast",
        getProxyMulticastByTaskName: "getProxyMulticastByTaskName",
        getProxyMulticastSecurityByTaskName: "getProxyMulticastSecurityByTaskName",
        getProxyForBroadcast: "getProxyForBroadcast",
        getProxyBroadcastByTaskName: "getProxyBroadcastByTaskName",
        getProxyBroadcastSecurityByTaskName: "getProxyBroadcastSecurityByTaskName",
        getProxyForSIP: "getProxyForSIP",
        getProxySIPByTaskName: "getProxySIPByTaskName",
        getProxySIPSecurityByTaskName: "getProxySIPSecurityByTaskName",
        getProxyForRtsp: "getProxyForRtsp",
        getProxyRtspByTaskName: "getProxyRtspByTaskName",
        getProxyRtspSecurityByTaskName: "getProxyRtspSecurityByTaskName",
        getProxyForGB28181: "getProxyForGB28181",
        getProxyGB28181ByTaskName: "getProxyGB28181ByTaskName",
        getProxyGB28181SecurityByTaskName: "getProxyGB28181SecurityByTaskName",
        getProxyForRtmp: "getProxyForRtmp",
        getProxyRtmpByTaskName: "getProxyRtmpByTaskName",
        getProxyRtmpSecurityByTaskName: "getProxyRtmpSecurityByTaskName",
        getProxyForHls: "getProxyForHls",
        getProxyHlsByTaskName: "getProxyHlsByTaskName",
        getProxyHlsSecurityByTaskName: "getProxyHlsSecurityByTaskName",
        getProxyForH225: "getProxyForH225",
        getProxyH225ByTaskName: "getProxyH225ByTaskName",
        getProxyH225SecurityByTaskName: "getProxyH225SecurityByTaskName",
        addBackNetExportAjax: "addBackNetExportAjax",
        addNetExportAjax: "addNetExportAjax",
        modifyNetExportAjax: "modifyNetExportAjax",
        modifyBackNetExportAjax: "modifyBackNetExportAjax",
        delExportAjax: "delExportAjax",
        delBackExportAjax: "delBackExportAjax",
        setDNSAjax: "setDNSAjax",
        setBackDNSAjax: "setBackDNSAjax",
        setGatewayAjax: "setGatewayAjax",
        setBackGatewayAjax: "setBackGatewayAjax",
        setGapAjax: "setGapAjax",
        setBackGapAjax: "setBackGapAjax",
        setManageIpAjax: "setManageIpAjax",
        setBackManageIpAjax: "setBackManageIpAjax",
        addRouteAjax: "addRouteAjax",
        addBackRouteAjax: "addBackRouteAjax",
        modifyRouteAjax: "modifyRouteAjax",
        modifyBackRouteAjax: "modifyBackRouteAjax",
        delBackRouteAjax: "delBackRouteAjax",
        delRouteAjax: "delRouteAjax"
    };


    var CopsecPlugin = {

        execute: function (path, param, successCall, errorCall) {

            if (!param) {

                console.exception("params should not be null");
                return;
            }
            $.ajax({
                url: path + "kebo/execute/method",
                method: "POST",
                dataType: "json",
                contentType: "application/json;charset=utf-8",
                data: JSON.stringify(param),
                success: function (data) {

                    successCall(data);
                },
                error: function (jqx, status, error) {

                    errorCall(jqx, status, error);
                }
            })
        },
        getMethodType: function () {

            return defaultMethods;
        }

    }

    _global = (function () {
        return this || (0, eval)('this');
    }());
    if (typeof module !== "undefined" && module.exports) {

        module.exports = CopsecPlugin;
    } else if (typeof define === "function" && define.amd) {

        define(function () {
            return CopsecPlugin;
        });
    } else {

        !('CopsecPlugin' in _global) && (_global.CopsecPlugin = CopsecPlugin);
    }
}());