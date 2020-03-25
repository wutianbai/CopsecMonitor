package com.copsec.monitor.web.beans;

import com.copsec.monitor.web.config.Resources;
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
}
