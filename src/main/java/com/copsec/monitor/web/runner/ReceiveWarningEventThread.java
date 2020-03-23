package com.copsec.monitor.web.runner;

import com.copsec.monitor.SpringContext;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.Report;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.entity.CacheEntity;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.handler.ReportHandlerPools;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportBaseHandler;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.pools.DevicePools;
import com.copsec.monitor.web.pools.UserInfoPools;
import com.copsec.monitor.web.pools.WarningItemPools;
import com.copsec.monitor.web.pools.deviceStatus.DeviceStatusPools;
import com.copsec.monitor.web.pools.deviceStatus.MonitorItemListPools;
import com.copsec.monitor.web.pools.deviceStatus.ObjectListPools;
import com.copsec.monitor.web.service.DeviceService;
import com.copsec.monitor.web.utils.LocalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.*;

@Component
@Order
public class ReceiveWarningEventThread extends ReportBaseHandler implements CommandLineRunner {
//public class ReceiveWarningEventThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveWarningEventThread.class);

    private static int MONITOR_DURATION = 1;

    private DeviceService deviceService = SpringContext.getBean(DeviceService.class);

    @Override
    public void run(String... args) throws Exception {
//    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("receive warningEvent thread started");
        }
        //第一种方式
//        ExecutorService executor = Executors.newCachedThreadPool();
//            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, Integer.MAX_VALUE,
//                    60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        while (true) {
            Task task = new Task();
            FutureTask<ConcurrentMap<String, CacheEntity>> futureTask = new FutureTask<>(task);
//                executor.submit(futureTask);
            executor.scheduleAtFixedRate(futureTask, 30, 10, TimeUnit.MILLISECONDS);

            try {
                TimeUnit.SECONDS.sleep(MONITOR_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//        try {
//            logger.debug("task运行结果" + futureTask.get());
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
        }
//        executor.shutdown();
    }

    // 异步任务
    class Task implements Callable<ConcurrentMap<String, CacheEntity>> {
        // 返回异步任务的执行结果
        @Override
        public ConcurrentMap<String, CacheEntity> call() throws Exception {
            ConcurrentMap<String, CacheEntity> cache = LocalCache.getCache();//获取本地缓存
            for (Iterator it = cache.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                CacheEntity cacheEntity = (CacheEntity) entry.getValue();
                Report report = (Report) cacheEntity.getValue();//取出上报数据
                LocalCache.remove(report.getDeviceId());//清除缓存
                final Device device = DevicePools.getInstance().getDevice(report.getDeviceId());//设备信息
                //更新设备上报时间
                device.getData().setReportTime(report.getReportTime());
                deviceService.updateDevice(new UserBean(), "127.0.0.1", device);
                final UserInfoBean userInfo = UserInfoPools.getInstances().get(device.getData().getMonitorUserId());//运维用户信息

                ConcurrentHashMap<String, Status> map = ObjectListPools.getInstances().getMap();//新建设备类型Map

                Status monitorType = new Status();//类型中文名称
                StringBuffer str = new StringBuffer();//页面子项提示 [CPU] [DISK]...
//                final Multimap<String, Status> monitorTypeMap = LinkedHashMultimap.create();
                ConcurrentHashMap<String, List<Status>> monitorTypeMap = new ConcurrentHashMap<>();

                List<ReportItem> reportItems = castList(report.getReportItems(), ReportItem.class);//获取上报项
                if (!ObjectUtils.isEmpty(reportItems)) {
                    reportItems.parallelStream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(reportItem -> {

                        final WarningEvent warningEvent = new WarningEvent();
                        warningEvent.setEventSource(reportItem.getMonitorItemType());
                        warningEvent.setEventTime(new Date());
                        warningEvent.setDeviceId(report.getDeviceId());
                        warningEvent.setDeviceName(device.getData().getDeviceHostname());
                        warningEvent.setUserId(userInfo.getUserId());
                        warningEvent.setUserName(userInfo.getUserName());
                        warningEvent.setUserMobile(userInfo.getMobile());

                        List<Status> monitorItemList = MonitorItemListPools.getInstances().getMap().get(reportItem.getMonitorItemType().name());//状态类型的List
                        if (reportItem.getStatus() == 0) {//获取信息失败告警
                            handle(reportItem, warningEvent);
                        } else {
                            List<WarningItemBean> warningItemList = WarningItemPools.getInstances().getAll();//所有告警项
                            warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                                warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                                String[] monitorIds = warningItem.getMonitorIds().split(Resources.SPLITE, -1);
                                if (new ArrayList<>(Arrays.asList(monitorIds)).contains(reportItem.getMonitorId())) {
                                    str.append("[" + reportItem.getMonitorItemType().name() + "]");

                                    Optional<ReportHandler> optional = ReportHandlerPools.getInstance().getHandler(reportItem.getMonitorItemType());
                                    if (optional.isPresent()) {
                                        Status status = optional.get().handle(warningItem, warningEvent, reportItem, monitorType);
                                        monitorItemList.add(status);
                                    }
                                }
                            });
                        }
                        //根据监控类型更新
                        monitorType.setDeviceId(reportItem.getMonitorType().getName());
                        monitorType.setWarnMessage(str.toString().length()==0?reportItem.getResult().toString():str.toString());
                        monitorTypeMap.put(reportItem.getMonitorItemType().name(), monitorItemList);
                        monitorType.setMessage(monitorTypeMap);
                        map.put(reportItem.getMonitorType().name(), monitorType);
                        System.err.println("map :" + Objects.toString(map));
                        DeviceStatusPools.getInstances().update(report.getDeviceId(), map);//更新设备状态缓存池
                    });
                }
            }
            try {
//                if (logger.isDebugEnabled()) {
//                    logger.debug("receive warningEvent thread-" + Thread.currentThread().getName() + "-读取[" + cache + "]");
//                }
                TimeUnit.SECONDS.sleep(MONITOR_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return cache;
        }
    }

    private static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
}

