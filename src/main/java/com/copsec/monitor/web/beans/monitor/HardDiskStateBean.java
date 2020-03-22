package com.copsec.monitor.web.beans.monitor;

import java.util.HashMap;
import java.util.List;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class HardDiskStateBean {

	private HashMap<String,HardDiskChildStateBean> partitionMap;
	private String total;
	private String totalUseRate;
	private String totalUsed;
	private List<HardDiskChildStateBean> list;
}
