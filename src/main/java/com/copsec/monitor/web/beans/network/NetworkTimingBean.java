package com.copsec.monitor.web.beans.network;

import com.copsec.monitor.web.config.Resources;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NetworkTimingBean {

	private String ip;
	private String protocol = "TCP";
	private String frequency;
	private String status;

	@Override
	public String toString(){

		StringBuffer buffer = new StringBuffer();
		buffer.append(this.ip+ Resources.SPLITER);
		buffer.append(this.protocol+Resources.SPLITER);
		buffer.append(this.frequency+Resources.SPLITER);
		buffer.append(this.status);
		return buffer.toString();
	}

}
