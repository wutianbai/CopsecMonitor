package com.copsec.monitor.web.beans.network;

import java.util.UUID;

import com.copsec.monitor.web.config.Resources;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RouterBean {

	private String uuid = UUID.randomUUID().toString();
	private String ip;
	private String subnet;
	private String gateway;
	private String interfaceName;

	@Override
	public String toString(){

		StringBuilder builder = new StringBuilder();
		builder.append(this.uuid+ Resources.SPLITER);
		builder.append(this.ip+ Resources.SPLITER);
		builder.append(this.subnet+ Resources.SPLITER);
		builder.append(this.gateway+ Resources.SPLITER);
		builder.append(this.interfaceName);
		return builder.toString();
	}
}
