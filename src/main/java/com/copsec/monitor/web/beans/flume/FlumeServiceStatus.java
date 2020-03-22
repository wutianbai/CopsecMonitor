package com.copsec.monitor.web.beans.flume;

import com.copsec.monitor.web.config.Resources;
import lombok.Data;

@Data
public class FlumeServiceStatus {

	private String status;

	private String ip;

	private int port;

	private String type;

	@Override
	public String toString(){

		StringBuffer buffer = new StringBuffer();
		buffer.append(this.status+ Resources.SPLITER);
		buffer.append(this.ip + Resources.SPLITER);
		buffer.append(this.port);
		return buffer.toString();
	}
}
