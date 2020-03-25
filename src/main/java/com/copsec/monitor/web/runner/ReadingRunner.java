package com.copsec.monitor.web.runner;

import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.fileReaders.DeviceFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order
public class ReadingRunner implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ReadingRunner.class);

	@Autowired
	private SystemConfig config;

	@Autowired
	private DeviceFileReader deviceReader;

	@Override
	public void run(String... args) throws Exception {
		try {
			deviceReader.getData(config.getBasePath()+ config.getDevicePath());
		}catch (Throwable t){
			logger.error(t.getMessage(),t);
		}
	}
}
