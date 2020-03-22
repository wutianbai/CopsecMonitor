package com.copsec.monitor.web.beans.statistics;

import java.util.Date;

import lombok.Data;

@Data
public class StatisticalResultBean {

	private String deviceId;

	private int year;

	private int month;

	private int day;

	private long total;

	private long max;

	private long min;

	private String maxDeviceId;

	private String minDeviceId;

	private Date updateTime;

	private long gatherSizeTotal;
	private long gatherCountTotal;

	private long storageCountTotal;
	private long storageSizeTotal;

	private long gatherMax;
	private String gatherMaxDeviceId;

	private long gatherMin;
	private String gatherMinDeviceId;

	private long storageMax;
	private String storageMaxDeviceId;

	private long storageMin;
	private String storageMinDeviceId;


	private long connectionCount;
	private long connectionSize;

	private long connectionMax;
	private String connectionMaxDeviceId;

	private long connectionMin;
	private String connectionMinDeviceId;

	private long sizeMax;
	private String sizeMaxDeviceId;

	private long sizeMin;
	private String sizeMinDeviceId;

	public String getDate(){

		StringBuffer sbf = new StringBuffer(this.year + "");
		if (this.month > 0) {

			if (this.month < 10) {
				sbf.append("-" + "0" + this.month);
			} else {
				sbf.append("-" + this.month);
			}
		}
		if (this.day >0) {

			if (this.day < 10) {
				sbf.append("-" + "0" + this.day);
			} else {
				sbf.append("-" + this.day);
			}
		}
		return sbf.toString();
	}
}
