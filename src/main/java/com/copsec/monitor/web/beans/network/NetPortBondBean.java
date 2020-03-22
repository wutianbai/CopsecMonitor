package com.copsec.monitor.web.beans.network;

import javax.annotation.Resource;

import com.copsec.monitor.web.config.Resources;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NetPortBondBean {

	private String boundName;
	private String port;
	private String mode;

	@Override
	public String toString(){

		StringBuilder builder = new StringBuilder();
		builder.append(this.boundName+ Resources.SPLITER);
		builder.append(this.port+Resources.SPLITER);
		builder.append(this.mode);
		return builder.toString();
	}
}
