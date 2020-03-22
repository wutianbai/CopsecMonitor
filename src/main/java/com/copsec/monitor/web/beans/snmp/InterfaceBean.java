package com.copsec.monitor.web.beans.snmp;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class InterfaceBean {

	private long ifInOctets = 0;

	private long ifOutOctets = 0;

	private long speed = 0;

	private int id;

	private String name;

	private String status;

	private boolean use64 = false;

	private String message;
}
