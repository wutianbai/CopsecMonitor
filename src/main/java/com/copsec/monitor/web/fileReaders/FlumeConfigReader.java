package com.copsec.monitor.web.fileReaders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.copsec.monitor.web.beans.flume.FlumeAgent;
import com.copsec.monitor.web.beans.flume.FlumeBean;
import com.copsec.monitor.web.beans.flume.FlumeProperty;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.flume.parse.FlumePropertyParseUtils;
import com.copsec.monitor.web.flume.pools.FlumeAgentPool;
import com.copsec.monitor.web.flume.pools.FlumeAliasPool;
import com.copsec.monitor.web.flume.pools.FlumePropertyPool;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 读取和写入flume配置文件
 */
@Component
public class FlumeConfigReader extends BaseFileReader<String> {

	private static final Logger logger = LoggerFactory.getLogger(FlumeConfigReader.class);

	public void parseFlumeConf(String filePath,FlumeBean fb) throws CopsecException{

		if(logger.isDebugEnabled()){

			logger.debug("parse flume conf file by path {} ",filePath);
		}
		List<String> lines = super.readContent(filePath);
		FlumeAgent fa = new FlumeAgent();
		fa.setAgentId(fb.getAgentId());

		if(FlumeAgentPool.getInstances().isExist(fb.getAgentId())){
			FlumeAgentPool.getInstances().add(fa);
			return ;
		}

		if(ObjectUtils.isEmpty(lines)){

			logger.warn("agent not configuration");
			FlumeAgentPool.getInstances().add(fa);
			return ;
		}

		List<String> sourcesKey = Arrays.asList(fb.getSource().split(" "));
		sourcesKey.stream().forEach( key -> {

			List<FlumeProperty> ps = getProperties(key,fb,lines,Resources.FLUME_SOURCE);
			fa.getSourcesMap().putIfAbsent(key,ps);
		});

		List<String> channelsKeys = Arrays.asList(fb.getChannel().split(" "));
		channelsKeys.stream().forEach(key -> {

			List<FlumeProperty> ps = getProperties(key,fb,lines,Resources.FLUME_CHANNEL);
			fa.getChannelMap().putIfAbsent(key,ps);
		});

		List<String> sinksKeys = Arrays.asList(fb.getSink().split(" "));
		sinksKeys.stream().forEach(key -> {

			List<FlumeProperty> ps = getProperties(key,fb,lines,Resources.FLUME_SINKS);
			fa.getSinkMap().putIfAbsent(key,ps);
		});

		FlumeAgentPool.getInstances().add(fa);
		logger.debug("add flume agent success");
	}

	public List<FlumeProperty> getProperties(String key,FlumeBean fb,List<String> lines,String propType){

		List<FlumeProperty> propList = Lists.newArrayList();
		String prefix = fb.getAgentId() + Resources.FLUME_SPERATOR + propType + Resources.FLUME_SPERATOR + key;
		String type = getType(lines,prefix + ".type");
		String aliasKey = FlumeAliasPool.getInstances().get(propType,type);
		List<FlumeProperty> props = FlumePropertyPool.getInstances().get(aliasKey);
		lines.stream().filter(d -> d.startsWith(prefix)).forEach(line -> {

			String[] strs = line.split("=");
			String k = strs[0].trim().substring(prefix.length()+1);
			String v = strs[1].trim();
			boolean isMust = attrIsMust(props,k);
			FlumeProperty property = new FlumeProperty();
			property.setKey(k);
			property.setV(v);
			property.setMust(isMust);
			propList.add(property);
		});
		return propList;
	}

	public String getType(List<String> lines,String prefix){

		for(String line : lines){

			String[] strs = line.split("=");
			if(strs[0].equals(prefix)){

				return strs[1];
			}
		}
		return null;
	}

	public boolean attrIsMust(List<FlumeProperty> props,String propName){

		for(FlumeProperty fp:props){

			if(fp.getKey().equalsIgnoreCase(propName)){

				return fp.isMust();
			}
		}
		return false;
	}

}
