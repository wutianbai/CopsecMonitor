package com.copsec.monitor.web.handler;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ReportHandlerPools {

    private static final Logger logger = LoggerFactory.getLogger(ReportHandlerPools.class);
    private static ReportHandlerPools pool = null;

    private static ConcurrentHashMap<MonitorItemEnum, ReportHandler> handlerMap = new ConcurrentHashMap<>();

    public static synchronized ReportHandlerPools getInstance() {
        if (pool == null) {
            synchronized (ReportHandlerPools.class) {
                if (pool == null) {
                    pool = new ReportHandlerPools();
                }
            }
        }
        return pool;
    }

    private ReportHandlerPools() {
    }

    public void registerHandler(MonitorItemEnum type, ReportHandler handler) {
        handlerMap.putIfAbsent(type, handler);
        if (logger.isDebugEnabled()) {
            logger.debug("register {} handler success", handler.getClass().getSimpleName());
        }
    }

    public Optional<ReportHandler> getHandler(MonitorItemEnum type) {
        Optional<ReportHandler> optionalReportHandler = Optional.ofNullable(handlerMap.get(type));
        return optionalReportHandler;
    }
}
