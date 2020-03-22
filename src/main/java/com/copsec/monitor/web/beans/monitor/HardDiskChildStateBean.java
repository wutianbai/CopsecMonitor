package com.copsec.monitor.web.beans.monitor;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HardDiskChildStateBean {

	private String  partitionName;
	private String total;
	private String useRate;
	private String  used;
}
