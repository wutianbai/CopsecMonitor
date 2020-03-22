package com.copsec.monitor.web.handler;

import java.util.function.Consumer;

import com.copsec.monitor.web.beans.syslogParseBeans.SyslogMessageBean;

import org.springframework.stereotype.Component;

@Component
public class ProtocolLogsParse{

	public void parse(SyslogMessageBean syslogMessageBean ,Consumer<SyslogMessageBean> c){

		c.accept(syslogMessageBean);
	}
}
