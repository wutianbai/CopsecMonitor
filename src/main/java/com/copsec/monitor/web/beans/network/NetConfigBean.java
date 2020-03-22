package com.copsec.monitor.web.beans.network;

import java.util.UUID;

import com.copsec.monitor.web.config.Resources;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetConfigBean {

	private String uuid = UUID.randomUUID().toString();
	private String ethName;
	private String ip;
	private String subnet;
	private String gateway = "N/A";

	@Override
	public String toString(){

		StringBuilder builder = new StringBuilder();
		builder.append(this.uuid+ Resources.SPLITER);
		builder.append(this.ethName+Resources.SPLITER);
		builder.append(this.ip+Resources.SPLITER);
		builder.append(this.subnet+Resources.SPLITER);
		builder.append(this.gateway);
		return builder.toString();
	}
}
