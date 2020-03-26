package com.copsec.monitor.web.executers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DeviceStatusMonitorScheduled {

    private static final Logger logger = LoggerFactory.getLogger(DeviceStatusMonitorScheduled.class);

    /**
     * 1分钟执行一次,监控设备是否断开连接
     */
//    @Scheduled(fixedRate = 6000)
//    public void monitor() {
//        List<String> storageList = CommonPools.getInstances().getNetowkConfig(NetworkType.LOGSTORAGE);
//        int days = 30;
//        if (storageList.size() == 1) {
//            days = Integer.valueOf(storageList.get(0));
//        }
//        Calendar cal = Calendar.getInstance();
//        cal.roll(Calendar.DAY_OF_YEAR, -days);
//        Date reportTime = cal.getTime();
//        auditSyslogMessageRepository.deleteAllByReportTimeBefore(reportTime);
//    }
}
