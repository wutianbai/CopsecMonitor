package com.copsec.monitor.web.flume.parse;

import java.util.List;
import java.util.Objects;
import com.copsec.monitor.web.beans.flume.FlumeAgent;
import com.copsec.monitor.web.beans.flume.FlumeBean;
import com.copsec.monitor.web.beans.flume.FlumeProperty;
import com.copsec.monitor.web.beans.flume.Property4Edit;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.flume.pools.FlumeAgentPool;
import com.copsec.monitor.web.flume.pools.FlumeBeanPool;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ObjectUtils;

public class FlumePropertyParseUtils {

	private static final Logger logger = LoggerFactory.getLogger(FlumePropertyParseUtils.class);

	public static CopsecResult updateFlumeAgentProp(Property4Edit property4Edit){

		if(logger.isDebugEnabled()){

			logger.debug("going to update flume agent properties");
		}
		FlumeAgent fa = FlumeAgentPool.getInstances().get(property4Edit.getAgentId());
		if(ObjectUtils.isEmpty(fa)){

			logger.error("agent {} do not exists ",property4Edit.getAgentId());
			return CopsecResult.failed("更新配置失败，未知错误");
		}

		List<FlumeProperty> propertyList = Lists.newArrayList();
		property4Edit.getPropList().stream().forEach(item -> {

			FlumeProperty prop = new FlumeProperty();
			prop.setKey(item.getKey());
			prop.setV(item.getV());
			prop.setMust(item.isMust());
			propertyList.add(prop);

		});

		switch (property4Edit.getPrefix()){

			case Resources.FLUME_CHANNEL:
				if(fa.getChannelMap().containsKey(property4Edit.getTypeId())){
					fa.getChannelMap().replace(property4Edit.getTypeId(),propertyList);
				}else{

					fa.getChannelMap().putIfAbsent(property4Edit.getTypeId(),propertyList);
				}
				break;
			case Resources.FLUME_SOURCE:
				if(fa.getSourcesMap().containsKey(property4Edit.getTypeId())){

					fa.getSourcesMap().replace(property4Edit.getTypeId(),propertyList);
				}else{

					fa.getSourcesMap().putIfAbsent(property4Edit.getTypeId(),propertyList);
				}
				break;
			case Resources.FLUME_SINKS:
				if(fa.getSinkMap().containsKey(property4Edit.getTypeId())){

					fa.getSinkMap().replace(property4Edit.getTypeId(),propertyList);
				}else{

					fa.getSinkMap().putIfAbsent(property4Edit.getTypeId(),propertyList);
				}
				break;
		}
		FlumeAgentPool.getInstances().update(fa);

		return CopsecResult.success("更新配置成功");
	}


	public static List<String> parseFlumeProperty(String agentId){

		if(logger.isDebugEnabled()){

			logger.debug("start to parse flume properties");
		}
		FlumeAgent fa = FlumeAgentPool.getInstances().get(agentId);
		if(ObjectUtils.isEmpty(fa)){

			logger.error("agent {} do not exists ",agentId);
			return null;
		}
		List<String> propertyList = Lists.newArrayList();
		String fp = "";
		//从flumeBean中读取配置信息
		FlumeBean fb = FlumeBeanPool.getInstances().getFlumeConfig(agentId);
		propertyList.add(agentId + Resources.FLUME_SPERATOR + Resources.FLUME_SOURCE + "="+fb.getSource());
		propertyList.add(agentId + Resources.FLUME_SPERATOR + Resources.FLUME_CHANNEL + "="+fb.getChannel());
		propertyList.add(agentId + Resources.FLUME_SPERATOR + Resources.FLUME_SINKS + "="+fb.getSink());
		fa.getSourcesMap().entrySet().stream().forEach(entry ->{

			String key = entry.getKey();
			entry.getValue().stream().forEach(v -> {

				String propLine = agentId + Resources.FLUME_SPERATOR + Resources.FLUME_SOURCE
						+ Resources.FLUME_SPERATOR + key + Resources.FLUME_SPERATOR + v.getKey() + "="+v.getV();
				propertyList.add(propLine);
			});
		});

		fa.getChannelMap().entrySet().stream().forEach(entry ->{

			String key = entry.getKey();
			entry.getValue().stream().forEach(v -> {

				String propLine = agentId + Resources.FLUME_SPERATOR + Resources.FLUME_CHANNEL
						+ Resources.FLUME_SPERATOR + key + Resources.FLUME_SPERATOR + v.getKey() + "="+v.getV();
				propertyList.add(propLine);
			});
		});

		fa.getSinkMap().entrySet().stream().forEach(entry ->{

			String key = entry.getKey();
			entry.getValue().stream().forEach(v -> {

				String propLine = agentId + Resources.FLUME_SPERATOR + Resources.FLUME_SINKS
						+ Resources.FLUME_SPERATOR + key + Resources.FLUME_SPERATOR + v.getKey() + "="+v.getV();
				propertyList.add(propLine);
			});
		});


		return propertyList;
	}
}
