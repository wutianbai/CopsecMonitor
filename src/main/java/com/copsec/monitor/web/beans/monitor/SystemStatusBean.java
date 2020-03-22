package com.copsec.monitor.web.beans.monitor;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SystemStatusBean {

	private CpuStateBean cpuStatus;
	private HardDiskStateBean hardDisk;
	private MemeryStateBean memeryStatus;
	private EthStateBean ethStatus;
	private FileSyncTaskStateBean fileSyncTaskStaus;
	private DBSyncTaskStateBean dbSyncTaskStatus;
}
