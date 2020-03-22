package com.copsec.monitor.web.beans.monitor;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FileSyncTaskChildStateBean {

	private String taskName;
	private String downFileCount;
	private String downFileSizeCount;
	private String upFileCount;
	private String upFileSizeCount;
}
