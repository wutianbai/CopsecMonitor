package com.copsec.monitor.web.beans.monitor;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DBSyncTaskChildStateBean {

	private String taskName;
	private String gatherCount;
	private String gatherDeleteCount;
	private String gatherInsertCount;
	private String gatherUpdateCount;
	private String gatherDesc;
	private String storageCount;
	private String storageDeleteCount;
	private String storageInsertCount;
	private String storageUpdateCount;
	private String storageDesc;
}
