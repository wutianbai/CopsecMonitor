package com.copsec.monitor.web.beans.monitor;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class CpuStateBean {

	private String cpuUseRate;
	private String processorCount;
	private String processorName;
}
