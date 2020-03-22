package com.copsec.monitor.web.beans.monitor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CpuMemeryStateBean {

	private String cpuUseRate;
	private String memeryUseRate;
	private String currentUseRate;
	private String totalMemery;
}
