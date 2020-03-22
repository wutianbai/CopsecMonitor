package com.copsec.monitor.web.beans.syslogConf;

import lombok.Data;

@Data
public class ServiceLogFileBean {

	private String fileName;

	private String size;

	private String path;

	private String uuid;
}
