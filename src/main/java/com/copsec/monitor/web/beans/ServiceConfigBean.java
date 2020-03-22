package com.copsec.monitor.web.beans;

import javax.annotation.Resource;

import com.copsec.monitor.web.config.Resources;
import lombok.Data;

@Data
public class ServiceConfigBean {

	private String status = "forbidden";

	private String ip;

	private int port = 0;

	public String toString(){

		StringBuffer buffer = new StringBuffer();
		buffer.append(this.status + Resources.SPLITER);
		buffer.append(this.ip + Resources.SPLITER);
		buffer.append(this.port + Resources.SPLITER);
		return buffer.toString();
	}
}
