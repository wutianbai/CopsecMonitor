package com.copsec.monitor.web.beans.remote;

import com.copsec.monitor.web.config.Resources;
import lombok.Data;
import org.apache.http.cookie.Cookie;

@Data
public class RemoteSessionBean {

    private String deviceId;

    private String userId;

    private String userPwd;

    private Cookie cookie;

    public String getKey() {
        return this.deviceId + Resources.SPLITER + this.userId;
    }

    public RemoteSessionBean(String deviceId, String userId, String userPwd, Cookie cookie) {

        this.deviceId = deviceId;
        this.userId = userId;
        this.userPwd = userPwd;
        this.cookie = cookie;
    }

    public RemoteSessionBean() {
    }
}
