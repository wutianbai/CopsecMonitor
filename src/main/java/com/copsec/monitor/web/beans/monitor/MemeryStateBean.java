package com.copsec.monitor.web.beans.monitor;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemeryStateBean {

	private String free;
	private String total;
	private String useRate;
}
