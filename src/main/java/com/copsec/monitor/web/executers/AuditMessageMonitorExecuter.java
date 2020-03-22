package com.copsec.monitor.web.executers;

import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.CommonPools;
import com.copsec.monitor.web.repository.AuditSyslogMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class AuditMessageMonitorExecuter {

    private static final Logger logger = LoggerFactory.getLogger(AuditMessageMonitorExecuter.class);

    @Autowired
    private AuditSyslogMessageRepository auditSyslogMessageRepository;

    private static final long totalCount = 8000000L;

    /**
     * 10分钟执行一次，删除指定记录天数的日志
     */
    @Scheduled(fixedRate = 6000 * 10)
    public void monitor() {
        List<String> storageList = CommonPools.getInstances().getNetowkConfig(NetworkType.LOGSTORAGE);
        int days = 30;
        if (storageList.size() == 1) {
            days = Integer.valueOf(storageList.get(0));
        }
        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.DAY_OF_YEAR, -days);
        Date reportTime = cal.getTime();
        auditSyslogMessageRepository.deleteAllByReportTimeBefore(reportTime);
    }
}
