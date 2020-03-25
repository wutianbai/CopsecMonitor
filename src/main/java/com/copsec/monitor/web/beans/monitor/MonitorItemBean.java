package com.copsec.monitor.web.beans.monitor;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorTypeEnum;
import com.copsec.monitor.web.beans.warning.CertConfig;
import com.copsec.monitor.web.config.Resources;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.UUID;

@Data
public class MonitorItemBean {
    private String monitorId = UUID.randomUUID().toString();//监控项标识
    private String monitorName;//监控项名称
    private MonitorItemEnum monitorItemType;//监控信息的类型
    private MonitorTypeEnum monitorType;//监控项类别
    private Object item = "N/A";//monitorItemType不同类型对应不同值，默认值为“N/A”
    private CertConfig certConfig;
    private LogConfig logConfig;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.monitorId + Resources.SPLITER);
        builder.append(this.monitorName + Resources.SPLITER);
        if (!ObjectUtils.isEmpty(this.monitorItemType)) {
            builder.append(this.monitorItemType.name() + Resources.SPLITER);
        }
        if (!ObjectUtils.isEmpty(this.monitorType)) {
            builder.append(this.monitorType.name() + Resources.SPLITER);
        }
        if (!ObjectUtils.isEmpty(this.certConfig) && !"".equals(this.certConfig.getInstanceName())) {
            builder.append(this.certConfig.toString());
        } else if (!ObjectUtils.isEmpty(this.logConfig) && !"".equals(this.logConfig.getLogPath())) {
            builder.append(this.logConfig.toString());
        } else {
            builder.append(this.item);
        }
        return builder.toString();
    }
}
