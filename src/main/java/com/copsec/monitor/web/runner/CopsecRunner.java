package com.copsec.monitor.web.runner;

import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.fileReaders.*;
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

    @Override
    public void run(String... args) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug("reading configuration files");
        }

        deviceReader.getData(systemConfig.getBasePath() + systemConfig.getDevicePath());
        userReader.getData(systemConfig.getBasePath() + systemConfig.getUserPath());

        userInfoReader.getData(systemConfig.getBasePath() + systemConfig.getUserInfoPath());
        monitorItemReader.getData(systemConfig.getBasePath() + systemConfig.getMonitorItemPath());
        warningItemReader.getData(systemConfig.getBasePath() + systemConfig.getWarningItemPath());
        monitorGroupReader.getData(systemConfig.getBasePath() + systemConfig.getMonitorGroupPath());
        monitorTaskReader.getData(systemConfig.getBasePath() + systemConfig.getMonitorTaskPath());
        TimeoutWarningEventThread timeoutWarningEventThread = new TimeoutWarningEventThread();
        new Thread(timeoutWarningEventThread, "timeoutWarningEvent-thread").start();
    }
}
