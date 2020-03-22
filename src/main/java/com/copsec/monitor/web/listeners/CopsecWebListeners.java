package com.copsec.monitor.web.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class CopsecWebListeners implements ApplicationListener<ContextClosedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(CopsecWebListeners.class);

	@Autowired
	private ThreadPoolTaskScheduler scheduler;


	@Override
	public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {

		this.scheduler.shutdown();
		logger.debug("schedulers is going to shutdown ");
	}
}
