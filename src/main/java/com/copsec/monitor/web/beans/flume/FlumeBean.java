package com.copsec.monitor.web.beans.flume;

import javax.annotation.Resource;

import com.copsec.monitor.web.config.Resources;
import lombok.Data;


@Data
public class FlumeBean {

	private String agentId;

	private String source;

	private String channel;

	private String sink;

	private String fileName;

	private String status;

	private String runId = "";

	@Override
	public String toString(){

		StringBuilder builder = new StringBuilder();
		builder.append(agentId + Resources.SPLITER);
		builder.append(source +  Resources.SPLITER);
		builder.append(channel + Resources.SPLITER);
		builder.append(sink + Resources.SPLITER);
		builder.append(fileName + Resources.SPLITER);
		builder.append(runId + Resources.SPLITER);
		builder.append(status);
		return builder.toString();
	}
}
