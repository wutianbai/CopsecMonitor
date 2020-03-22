package com.copsec.monitor.web.flume.threads;

import com.copsec.monitor.web.flume.utils.FlumeCommandUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlumeRunner implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(FlumeRunner.class);

	private String agentName;
	private String configDir;
	private String configPath;
	private String commands;
	public FlumeRunner(String agentName,String configDir,String configPath,String commands){

		this.agentName = agentName;
		this.configDir = configDir;
		this.configPath = configPath;
		this.commands = commands;
	}
	@Override
	public void run() {

		if(logger.isDebugEnabled()){

			logger.debug(Thread.currentThread().getName() + " is running");
		}
		try {

			FlumeCommandUtils.startCommand(this.agentName,
					this.configDir,
					this.configPath,
					this.commands);
		}
		catch (Exception e) {

			logger.error(e.getMessage(),e);
		}
	}
}
