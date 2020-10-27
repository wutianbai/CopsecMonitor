package com.copsec.monitor.web.runner;

import java.io.File;

import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.fileReaders.*;
import com.copsec.monitor.web.pools.MonitorItemPools;
import com.copsec.monitor.web.pools.UserPools;
import com.copsec.monitor.web.repository.DeviceRepository;
import com.copsec.monitor.web.repository.MonitorGroupRepository;
import com.copsec.monitor.web.repository.MonitorItemRepository;
import com.copsec.monitor.web.repository.MonitorTaskRepository;
import com.copsec.monitor.web.repository.UserInfoEntityRepository;
import com.copsec.monitor.web.repository.UserRepository;
import com.copsec.monitor.web.repository.WarningItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order
public class CopsecRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CopsecRunner.class);

    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private DeviceFileReader deviceReader;
    @Autowired
    private UserFileReader userReader;
    @Autowired
    private UserInfoReader userInfoReader;
    @Autowired
    private MonitorItemReader monitorItemReader;
    @Autowired
    private MonitorGroupReader monitorGroupReader;
    @Autowired
    private MonitorTaskReader monitorTaskReader;
    @Autowired
    private WarningItemReader warningItemReader;

    @Autowired
	private DeviceRepository deviceRepository;
    @Autowired
	private UserRepository userRepository;
    @Autowired
	private UserInfoEntityRepository userInfoEntityRepository;
    @Autowired
	private MonitorItemRepository monitorItemRepository;
    @Autowired
	private MonitorTaskRepository monitorTaskRepository;
    @Autowired
	private MonitorGroupRepository monitorGroupRepository;
    @Autowired
	private WarningItemRepository warningItemRepository;


    @Override
    public void run(String... args) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug("reading configuration files");
        }

		if(userRepository.findAll().size()==0){

			userReader.getData(systemConfig.getBasePath() + File.separator + systemConfig.getUserPath());
			UserPools.getInstances().save(userRepository);
		}

		if(monitorItemRepository.findAll().size() == 0){

			monitorItemReader.getData(systemConfig.getBasePath() + File.separator + systemConfig.getMonitorItemPath());
			MonitorItemPools.getInstances().save(monitorItemRepository);
		}
        deviceRepository.findAll().stream().forEach(d -> deviceReader.getDataByInfos(d.getDeviceInfo()));
        userRepository.findAll().stream().forEach(u -> userReader.getDataByInfos(u.getUserInfo()));
        userInfoEntityRepository.findAll().stream().forEach(u -> userInfoReader.getDataByInfos(u.getUserInfo()));
        monitorGroupRepository.findAll().stream().forEach(m -> monitorGroupReader.getDataByInfos(m.getMonitorGroupInfo()));
        monitorItemRepository.findAll().stream().forEach((m -> monitorItemReader.getDataByInfos(m.getMonitorItemInfo())));
		monitorTaskRepository.findAll().stream().forEach(m -> monitorTaskReader.getDataByInfos(m.getMonitorTaskInfo()));
        warningItemRepository.findAll().stream().forEach(m -> warningItemReader.getDataByInfos(m.getWarningItemInfo()));
		TimeoutWarningEventThread timeoutWarningEventThread = new TimeoutWarningEventThread();
        new Thread(timeoutWarningEventThread, "timeoutWarningEvent-thread").start();
    }
}
