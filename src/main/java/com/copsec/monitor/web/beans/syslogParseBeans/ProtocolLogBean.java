package com.copsec.monitor.web.beans.syslogParseBeans;

import java.util.Date;

import lombok.Data;

@Data
public class ProtocolLogBean {

	private String taskName;
	private String deviceId;
	private Date updateTime;

	private boolean error;

	private long handleConnectionCount;
	private long handleNetDataSize;

	private boolean canSave;
}
