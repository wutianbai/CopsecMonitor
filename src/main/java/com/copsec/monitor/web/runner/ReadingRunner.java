package com.copsec.monitor.web.runner;

import com.copsec.monitor.web.beans.taskMonitor.FileSyncStatusBean;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.fileReaders.DeviceFileReader;
import com.copsec.monitor.web.pools.FileSyncStatusPools;
import com.copsec.monitor.web.repository.FileStatusRepository;
import com.copsec.monitor.web.utils.FileReaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Order
public class ReadingRunner implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ReadingRunner.class);

	@Autowired
	private SystemConfig config;

	@Autowired
	private DeviceFileReader deviceReader;

	@Autowired
	private FileStatusRepository fileStatusRepository;

	@Override
	public void run(String... args) throws Exception {
		FileReaderUtils.getData(config);
		try {
			deviceReader.getData(config.getBasePath()+ config.getDevicePath());
		}catch (Throwable t){
			logger.error(t.getMessage(),t);
		}

		List<String> taskNames = fileStatusRepository.getAllFileTaskName();
		taskNames.forEach(taskName -> {
			FileSyncStatusBean tmp = new FileSyncStatusBean();
			tmp.setTaskName(taskName);
			tmp.setUpdateTime(new Date());
			tmp.setStatus(true);
			FileSyncStatusPools.getInstances().update(tmp);
		});
	}
}
