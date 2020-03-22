package com.copsec.monitor.web.beans.warning;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Report {
    private String deviceId;
    private Date reportTime;
    private List<ReportItem> reportItems;
}
