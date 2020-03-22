package com.copsec.monitor.web.beans.statistics;

import lombok.Data;

@Data
public class DBSumResultBean {

	private String type;
	private double percent;
	private long sum;
	private String background;

	public DBSumResultBean(String type){

		this.type = type;
		this.percent = 0;
		this.sum = 0;
	}

	public DBSumResultBean(){}
}
