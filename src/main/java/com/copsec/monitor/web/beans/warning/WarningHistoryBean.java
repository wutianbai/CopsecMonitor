package com.copsec.monitor.web.beans.warning;

import com.copsec.monitor.web.beans.PageInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarningHistoryBean extends PageInfo {
    private String eventId;
    private String eventSource;
    private String eventTime;
    private String eventDetail;
    private String eventType;
    private String deviceId;
    private String deviceName;
    private String userId;
    private String userName;
    private String dealTime;
    private String status;

    private String start;
    private String end;

    private String dStart;
    private String dEnd;
}
