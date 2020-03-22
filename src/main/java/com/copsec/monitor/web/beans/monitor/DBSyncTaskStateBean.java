package com.copsec.monitor.web.beans.monitor;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DBSyncTaskStateBean {

	private String gatherCount;
	private String gatherDeleteCount;
	private String gatherInsertCount;
	private String gatherUpdateCount;
	private String storageCount;
	private String storageDeleteCount;
	private String storageInsertCount;
	private String storageUpdateCount;
	private HashMap<String,DBSyncTaskChildStateBean> dbSyncTaskMap;
	private List<DBSyncTaskChildStateBean> list;
}
