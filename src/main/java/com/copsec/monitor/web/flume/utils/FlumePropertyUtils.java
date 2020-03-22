package com.copsec.monitor.web.flume.utils;

import java.util.List;

import com.copsec.monitor.web.beans.flume.FlumeBean;
import com.copsec.monitor.web.beans.flume.FlumeProperty;
import com.copsec.monitor.web.beans.flume.Property4Edit;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlumePropertyUtils {

	private static final Logger logger = LoggerFactory.getLogger(FlumePropertyUtils.class);

	public static void addChannels(Property4Edit property4Edit,FlumeBean bean){

		if(logger.isDebugEnabled()){

			logger.debug("update properties of agent {} ",property4Edit.getAgentId());
		}

		FlumeProperty property = new FlumeProperty();
		property.setKey("channels");
		property.setV(bean.getChannel());
		if(!property4Edit.getPropList().contains(property)){

			property4Edit.getPropList().add(property);
		}
	}


	public static Property4Edit getChannelProp(FlumeBean bean){

		if(logger.isDebugEnabled()){

			logger.debug("create channel properties for agent {}",bean.getAgentId());
		}

		Property4Edit pe = new Property4Edit();
		pe.setAgentId(bean.getAgentId());
		pe.setPrefix(Resources.FLUME_CHANNEL);
		pe.setTypeId(bean.getChannel());
		List<FlumeProperty> props = Lists.newArrayList();
		FlumeProperty p = new FlumeProperty();
		p.setKey("type");
		p.setV("memory");
		p.setMust(true);
		props.add(p);
		pe.setPropList(props);

		return pe;
	}

	public static Property4Edit getSinkProps(FlumeBean bean,SystemConfig config){

		if(logger.isDebugEnabled()){

			logger.debug("create sink properties for agent {}",bean.getAgentId());
		}
		Property4Edit pe = new Property4Edit();
		pe.setAgentId(bean.getAgentId());
		pe.setPrefix(Resources.FLUME_SINKS);
		pe.setTypeId(bean.getSink());
		List<FlumeProperty> props = Lists.newArrayList();
		FlumeProperty type = new FlumeProperty();
		type.setKey("type");
		type.setV(config.getMongoSinkTypes());
		type.setMust(true);
		props.add(type);

		FlumeProperty channel = new FlumeProperty();
		channel.setKey("channel");
		channel.setV(bean.getChannel());
		channel.setMust(true);
		props.add(channel);

		FlumeProperty host = new FlumeProperty();
		host.setKey("host");
		host.setV(config.getHost());
		host.setMust(true);
		props.add(host);

		FlumeProperty port = new FlumeProperty();
		port.setKey("port");
		port.setV(config.getPort()+"");
		port.setMust(true);
		props.add(port);

		pe.setPropList(props);
		return pe;

	}
}
