package com.copsec.monitor.web.beans.syslogParseBeans;

import java.util.Date;

import lombok.Data;

@Data
public class DBSynLogBean {

	private String taskName;
	private String deviceId;
	private Date updateTime;

	private long insertCount;
	private long updateCount;
	private long deleteCount;

	private boolean gather;

	private long sum;
}
