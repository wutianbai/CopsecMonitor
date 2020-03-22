package com.copsec.monitor.web.runner;

import com.copsec.monitor.web.beans.flume.FlumeBean;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.fileReaders.*;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.flume.pools.FlumeBeanPool;
import com.copsec.monitor.web.flume.threads.FlumeRunner;
import com.copsec.monitor.web.flume.utils.FlumeCommandUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
@Order
public class CopsecRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CopsecRunner.class);

    @Autowired
    private SystemConfig systemConfig;

    @Autowired
    private SnmpDeviceReader reader;

    @Autowired
    private FlumePropertyReader propertyReader;

    @Autowired
    private FlumeBeanReader flumeBeanReader;

    @Autowired
    private CommonFileReader commonFileReader;

    @Autowired
    private UserFileReader userReader;

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

        userReader.getData(systemConfig.getBasePath() + systemConfig.getUserPath());

        reader.getData(systemConfig.getBasePath() + systemConfig.getSnmpDevicePath());

        commonFileReader.getData(systemConfig.getBasePath() + systemConfig.getListeningServicePath(),
                NetworkType.LISTENER);

        String sourcePath = systemConfig.getBasePath() + systemConfig.getFlumeSourcePath();
        ReaderThread sourceThread = new ReaderThread(sourcePath, propertyReader, systemConfig);
        new Thread(sourceThread, "source-thread").start();

        String channelPath = systemConfig.getBasePath() + systemConfig.getFlumeChannelPath();
        ReaderThread channelThread = new ReaderThread(channelPath, propertyReader, systemConfig);
        new Thread(channelThread, "channel-thread").start();

        String sinkPath = systemConfig.getBasePath() + systemConfig.getFlumeSinkPath();
        ReaderThread sinkThread = new ReaderThread(sinkPath, propertyReader, systemConfig);
        new Thread(sinkThread, "sink-thread").start();

        logger.debug("reading flume beans from file ");
        flumeBeanReader.getData(systemConfig.getBasePath() + systemConfig.getFlumeConfigPath());

        List<FlumeBean> flumeBeans = FlumeBeanPool.getInstances().getAll();
        flumeBeans.forEach(bean -> {

            if (bean.getStatus().equalsIgnoreCase("start")) {

                String fp = systemConfig.getBasePath() + systemConfig.getConfigFlume() + bean.getFileName();

                try {
                    String p = FlumeCommandUtils.getPid(fp);
                    if (!ObjectUtils.isEmpty(p) && p.equalsIgnoreCase(bean.getRunId())) {
                        logger.debug("agent already running");
                    } else {

                        logger.debug("agent not running now start flume agent");
                        FlumeRunner runner = new FlumeRunner(bean.getAgentId(),
                                systemConfig.getBasePath() + systemConfig.getConfigFlume(),
                                systemConfig.getBasePath() + systemConfig.getConfigFlume() + bean.getFileName(),
                                systemConfig.getFlumeStartUpShell());
                        Thread t = new Thread(runner, bean.getAgentId() + "-thread");
                        t.start();

                        Thread.sleep(2000);

                        String pid = FlumeCommandUtils.getPid(systemConfig.getBasePath() + systemConfig.getConfigFlume() + bean.getFileName());

                        bean.setRunId(pid);
                        bean.setStatus("start");
                        FlumeBeanPool.getInstances().update(bean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        flumeBeanReader.writeDate(FlumeBeanPool.getInstances().getAll(),
                systemConfig.getBasePath() + systemConfig.getFlumeConfigPath());


        monitorItemReader.getData(systemConfig.getBasePath() + systemConfig.getMonitorItemPath());
        warningItemReader.getData(systemConfig.getBasePath() + systemConfig.getWarningItemPath());
        monitorGroupReader.getData(systemConfig.getBasePath() + systemConfig.getMonitorGroupPath());
        monitorTaskReader.getData(systemConfig.getBasePath() + systemConfig.getMonitorTaskPath());
        TimeoutWarningEventThread timeoutWarningEventThread = new TimeoutWarningEventThread();
        new Thread(timeoutWarningEventThread, "timeoutWarningEvent-thread").start();
    }
}
