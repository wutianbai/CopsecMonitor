package com.copsec.monitor.web.handler.ReportItemHandler;

import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.service.WarningService;

public interface ReportHandler {
    Status handle(WarningService warningService, WarningEvent warningEvent, ReportItem reportItem, Status monitorType);
}
