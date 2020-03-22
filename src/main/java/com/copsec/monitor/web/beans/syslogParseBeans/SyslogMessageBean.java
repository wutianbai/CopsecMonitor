package com.copsec.monitor.web.beans.syslogParseBeans;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SyslogMessageBean {

	private String deviceId;

	private Date updateTime;

	private List<String> properties;
}
