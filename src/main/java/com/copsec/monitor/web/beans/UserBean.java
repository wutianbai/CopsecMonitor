package com.copsec.monitor.web.beans;

import com.copsec.monitor.web.beans.remote.RemoteSessionBean;
import com.copsec.monitor.web.config.Resources;
import com.google.common.collect.ArrayListMultimap;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserBean {

    private String id;

    private String name;

    private String password;

    private String status;

    private String role;

    private String newCode;

    private ArrayListMultimap<String, RemoteSessionBean> cookieMaps = ArrayListMultimap.create();

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.id + Resources.SPLITER);
        buffer.append(this.password + Resources.SPLITER);
        buffer.append(this.name + Resources.SPLITER);
        buffer.append(this.role + Resources.SPLITER);
        buffer.append(this.status);
        return buffer.toString();
    }

    public void addCookie(String deviceId, RemoteSessionBean cookie) {
        cookieMaps.put(deviceId, cookie);
    }

    public boolean isLogined(String deviceId) {
        return cookieMaps.get(deviceId).isEmpty();
    }
}
