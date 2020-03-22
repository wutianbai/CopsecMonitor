package com.copsec.monitor.web.runner;

import com.copsec.monitor.SpringContext;
import com.copsec.monitor.web.beans.warning.WarningEventBean;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.entity.WarningHistory;
import com.copsec.monitor.web.service.WarningService;
import com.copsec.monitor.web.utils.DateUtils;
import com.copsec.monitor.web.utils.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Order
public class TimeoutWarningEventThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TimeoutWarningEventThread.class);

    // 检查的频率
    private static int MONITOR_DURATION = 2;

    private WarningService warningService = SpringContext.getBean(WarningService.class);

    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("handle timeout warningEvent thread started");
        }
//        while (true) {
            try {
                TimeUnit.MINUTES.sleep(MONITOR_DURATION);
                checkTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
    }

    /**
     * 过期事件的具体处理方法
     *
     * @throws Exception
     */
    private void checkTime() throws Exception {
        WarningEventBean bean = new WarningEventBean();
        bean.setEnd(FormatUtils.getFormatDate(DateUtils.beforeDay(new Date(), 7)));

        List<WarningEvent> list = warningService.findWarningEventByCondition(bean);
        list.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningEvent -> {
            if (logger.isDebugEnabled()) {
                logger.debug("handle timeout warningEvent[" + warningEvent + "]");
            }
            WarningHistory warningHistory = new WarningHistory();
            warningHistory.setId(warningEvent.getId());
            warningHistory.setEventSource(warningEvent.getEventSource());
            warningHistory.setEventTime(warningEvent.getEventTime());
            warningHistory.setEventDetail(warningEvent.getEventDetail());
            warningHistory.setEventType(warningEvent.getEventType());
            warningHistory.setDeviceId(warningEvent.getDeviceId());
            warningHistory.setDeviceName(warningEvent.getDeviceName());
            warningHistory.setStatus(0);
            warningService.insertWarningHistory(warningHistory);
            warningService.deleteWarningEvent(warningEvent);
        });
    }
}
