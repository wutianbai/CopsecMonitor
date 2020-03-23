package com.copsec.monitor.web.handler.ReportItemHandler;

import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.entity.WarningEvent;

public interface ReportHandler {
    Status handle(WarningItemBean warningItem, WarningEvent warningEvent, ReportItem reportItem, Status monitorType);
}
