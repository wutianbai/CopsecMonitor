package com.copsec.monitor.web.beans.flume;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import lombok.Data;

@Data
public class FlumeAgent {

	/**
	 * flume agent id 不能重复
	 */
	private String agentId;

	/**
	 * flume properties配置
	 */
	private Map<String,List<FlumeProperty>> sourcesMap = Maps.newHashMap();

	private Map<String,List<FlumeProperty>> channelMap = Maps.newHashMap();

	private Map<String,List<FlumeProperty>> sinkMap = Maps.newHashMap();
}
