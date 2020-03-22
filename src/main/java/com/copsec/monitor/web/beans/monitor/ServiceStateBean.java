package com.copsec.monitor.web.beans.monitor;

import lombok.Setter;
import lombok.Getter;


@Setter @Getter
public class ServiceStateBean {

	private String processId;
	private String serverName;
	private String cupOccupancy;//cpu占用率
	private String memoryOccupancy;//内存占用率
	private String virtualMemory;//虚拟内存
	private String physicalMemory;//物理内存

	@Override
	public String toString() {
		return "ServiceStateBean [cupOccupancy=" + cupOccupancy +", memoryOccupancy=" + memoryOccupancy + ", physicalMemory=" + physicalMemory + ", processId="
				+ processId + ", serverName=" + serverName + ", virtualMemory=" + virtualMemory + "]";
	}
}
