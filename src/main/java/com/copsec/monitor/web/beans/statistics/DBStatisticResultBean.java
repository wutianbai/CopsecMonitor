package com.copsec.monitor.web.beans.statistics;

import java.util.Date;

import lombok.Data;

@Data
public class DBStatisticResultBean {

	private String taskName;
	private String days;

	private long gatherToday;
	private long storageToday;

	private long gatherTotal;
	private long storageTotal;

	private long gatherCount;
	private long storageCount;

	private Date oldTime;
	private Date lastTime;
}
