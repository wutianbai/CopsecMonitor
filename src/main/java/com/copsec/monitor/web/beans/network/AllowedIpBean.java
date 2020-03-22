package com.copsec.monitor.web.beans.network;

import java.util.UUID;

import com.copsec.monitor.web.config.Resources;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AllowedIpBean {

    private String id = UUID.randomUUID().toString();

    private String ip;

    @Override
    public boolean equals(Object obj) {

        if (null == obj) {

            return false;
        }
        if (!obj.getClass().isAssignableFrom(this.getClass())) {

            return false;
        }
        AllowedIpBean ip = (AllowedIpBean) obj;
        if (null == ip.getIp()) {

            return false;
        }
        if (ip.getIp().equals(this.ip)) {

            return true;
        }
        return false;
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append(this.id + Resources.SPLITER);
        buffer.append(this.ip);
        return buffer.toString();
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result + id.hashCode();
        result = 31 * result + ip.hashCode();
        return result;
    }
}