package com.copsec.monitor.web.beans.node;

import com.copsec.monitor.web.config.Resources;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

@Getter
@Setter
public class Device extends BaseNode {
    private String group = "nodes";//区分连接\节点标志
    private Position position;//坐标

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(data.toString() + Resources.SPLITER);
        if (!ObjectUtils.isEmpty(this.position)) {
            builder.append(this.position.toString());
        }
        return builder.toString();
    }
}
