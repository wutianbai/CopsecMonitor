package com.copsec.monitor.web.handler.ReportItemHandler;

import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.service.WarningService;

public interface ReportHandler {
    Status handle(Status deviceStatus, Device device, UserInfoBean userInfo, WarningService warningService, ReportItem reportItem, Status monitorType);
}
