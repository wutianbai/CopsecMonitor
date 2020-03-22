package com.copsec.monitor.web.beans.syslogParseBeans;

import java.util.Date;

import lombok.Data;

@Data
public class FileSynLogBean {

	private String deviceId;
	private String taskName;
	private Date updateTime;
	private String fileName;

	private boolean gather;
	private boolean canSave = false;

	/**
	 * 上传和采集时发生错误标志位
	 */
	private boolean error = false;

	private boolean start;
	private boolean finish;

	private long fileSize;
	private long time;
}
