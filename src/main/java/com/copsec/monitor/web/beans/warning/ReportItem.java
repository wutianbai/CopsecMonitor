package com.copsec.monitor.web.beans.warning;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportItem {
    private String monitorId;
    private MonitorItemEnum monitorItemType;
    private MonitorTypeEnum monitorType;
    private String item = "N/A";
    private Object result = "N/A";
    private int status = 1;
}
