package com.copsec.monitor.web.beans.snmp;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;

@Data
public class SnmpConfigBean implements Serializable {


    private String deviceId;
    /**
     * 目的端设备使用的snmp协议版本
     */
    private String version;

    /**
     * 目的端设备ip地址
     */
    private String ip;

    /**
     * 目的端设备使用端口，默认161，report端口162
     */
    private int port = 161;

    /**
     * 针对v1和v2版本，使用团体名进行认证
     */
    private String community = "public";

    /**
     * v3认证用户名
     */
    private String userName;


    /**
     * 与username相关的认证协议，如果为null，只支持未授权的信息
     */
    private String authenticationProtocol;

    /**
     * Auth 密码
     */
    private String authPassphrase;

    /**
     * 消息加密协议，如果null，只支持未加密的消息
     */
    private String privatyProtocol;

    /**
     * 加密协议对应密码
     */
    private String privacyPassphrase;

    /**
     * 配置是否更新
     */
    private boolean update = false;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
