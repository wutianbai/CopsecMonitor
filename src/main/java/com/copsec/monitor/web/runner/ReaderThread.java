package com.copsec.monitor.web.runner;

import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.FlumePropertyReader;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ReaderThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReaderThread.class);
    private String filePath;
    private FlumePropertyReader reader;
    private SystemConfig config;

    public ReaderThread(String filePath, FlumePropertyReader reader, SystemConfig config) {
        this.filePath = filePath;
        this.reader = reader;
        this.config = config;
    }

    @Override
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("reading properties from file {}", this.filePath);
        }
        File file = new File(this.filePath);
        if (file.exists() && file.isDirectory()) {
            List<File> fileList = Lists.newArrayList(file.listFiles());
            fileList.forEach(fileItem -> {
                try {
                    this.reader.getData(fileItem.getAbsolutePath());
                } catch (CopsecException e) {
                    logger.error(e.getMessage(), e);
                }
            });
        } else {
            logger.error("no configuration file found on this path {}", this.filePath);
//            return;
        }
    }
}
