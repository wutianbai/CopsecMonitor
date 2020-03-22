package com.copsec.monitor.web.beans.monitor;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.WarningLevel;
import com.copsec.monitor.web.config.Resources;
import lombok.Data;

import java.util.UUID;

@Data
public class WarningItemBean {
    private String warningId = UUID.randomUUID().toString();//告警项标识
    private String warningName;//告警项名称
    private MonitorItemEnum monitorItemType;//监控信息的类型
    private WarningLevel warningLevel;//告警级别
    private int threadHold = 0;//告警阈值，默认值为0
    private String monitorIds = null;//监控项标识，默认值为null

    @Override
    public String toString(){

        StringBuilder builder = new StringBuilder();
        builder.append(this.warningId+ Resources.SPLITER);
        builder.append(this.warningName+Resources.SPLITER);
        builder.append(this.monitorItemType.name()+Resources.SPLITER);
        builder.append(this.warningLevel.name()+Resources.SPLITER);
        builder.append(this.threadHold+Resources.SPLITER);
        builder.append(this.monitorIds);
        return builder.toString();
    }
}
