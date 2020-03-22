package com.copsec.monitor.web.beans.flume;

import java.util.List;

import lombok.Data;

@Data
public class Property4Edit {

	private String agentId;

	private String typeId;

	private String prefix;

	private List<FlumeProperty> propList;
}