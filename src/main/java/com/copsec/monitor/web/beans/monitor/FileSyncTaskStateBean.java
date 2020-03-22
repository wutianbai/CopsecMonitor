package com.copsec.monitor.web.beans.monitor;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileSyncTaskStateBean {

	private HashMap<String,FileSyncTaskChildStateBean> fileSyncTaskMap;
	private String downFileCount;
	private String downFileSizeCount;
	private String upFileCount;
	private String upFileSizeCount;
	private List<FileSyncTaskChildStateBean> list;
}
