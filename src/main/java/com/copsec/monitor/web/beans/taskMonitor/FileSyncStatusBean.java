package com.copsec.monitor.web.beans.taskMonitor;

import java.util.Date;

import lombok.Data;

@Data
public class FileSyncStatusBean {

	private String taskId;
	private String taskName;
	private Date updateTime;
	private String message;
	private boolean status = true;
	private Date operateTime;
	private String operateUser;
	private String time;
	private String operateTimeStr;
}
