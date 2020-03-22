package com.copsec.monitor.web.beans.snmp;

import java.util.Date;

import lombok.Data;

@Data

public class SnmpDeviceStatus {

	private String deviceId;

	private double cpuUseRate;
	private double netSpeed;

	private	String deviceStatus;
	private String warnMessage;
	private Date updateTime;
	private double maxNetSpeed;
	private long totalAddSize;
	private double menUseRate;


	public void setNetSpeed(double speed){

		this.netSpeed = speed;
		if(this.maxNetSpeed < speed){

			this.maxNetSpeed = speed;
		}
	}


}
