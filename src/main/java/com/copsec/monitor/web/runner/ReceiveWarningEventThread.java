package com.copsec.monitor.web.runner;

import com.copsec.monitor.SpringContext;
import com.copsec.monitor.web.entity.CacheEntity;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportBaseHandler;
import com.copsec.monitor.web.service.DeviceService;
import com.copsec.monitor.web.service.WarningService;
import com.copsec.monitor.web.utils.LocalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Component
@Order
//public class ReceiveWarningEventThread extends ReportBaseHandler implements CommandLineRunner {
public class ReceiveWarningEventThread extends ReportBaseHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveWarningEventThread.class);

    private static int MONITOR_DURATION = 1;

    private DeviceService deviceService = SpringContext.getBean(DeviceService.class);

    private WarningService warningService = SpringContext.getBean(WarningService.class);

    @Override
//    public void run(String... args) throws Exception {
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("receive warningEvent thread started");
        }
        //第一种方式
//        ExecutorService executor = Executors.newCachedThreadPool();
//            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, Integer.MAX_VALUE,
//                    60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
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
//            for (Iterator it = cache.entrySet().iterator(); it.hasNext(); ) {
//                Map.Entry entry = (Map.Entry) it.next();
//                CacheEntity cacheEntity = (CacheEntity) entry.getValue();
//                Report report = (Report) cacheEntity.getValue();//取出上报数据
//                LocalCache.remove(report.getDeviceId());//清除缓存
//
//                Device device = DevicePools.getInstance().getDevice(report.getDeviceId());//设备信息
//                //更新设备上报时间
//                device.getData().setReportTime(report.getReportTime());
//                deviceService.updateDevice(new UserBean(), "127.0.0.1", device);
//                UserInfoBean userInfo = UserInfoPools.getInstances().get(device.getData().getMonitorUserId());//运维用户信息
//
////                Multimap<String, Status> monitorTypeMap = LinkedHashMultimap.create();
//                ConcurrentHashMap<String, Status> monitorTypeMap = MonitorTypePools.getInstances().getMap();
////                ConcurrentHashMap<String, List<Status>> monitorItemListMap = MonitorItemListPools.getInstances().getMap();
////                for (MonitorItemEnum i : MonitorItemEnum.values()) {
////                    List<Status> list = new ArrayList<>();
////                    newMonitorItemListMap.putIfAbsent(i.name(), list);
////                }
////
////                for (MonitorTypeEnum e : MonitorTypeEnum.values()) {
////                    Status status = new Status();
////                    status.setMessage(newMonitorItemListMap);
////                    monitorTypeMap.put(e.name(),status);
////                }
//
//                List<ReportItem> reportItems = report.getReportItems();//获取上报项
//                if (!ObjectUtils.isEmpty(reportItems)) {
//                    reportItems.parallelStream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(reportItem -> {
////                        Status monitorType = monitorTypeMap.get(reportItem.getMonitorType().name());//主类型
//                        Status monitorType = MonitorTypePools.getInstances().get(reportItem.getMonitorType().name());//主类型
//                        if (ObjectUtils.isEmpty(monitorType)) {
//                            monitorType = new Status();
////                            monitorTypeMap.putIfAbsent(reportItem.getMonitorType().name(), monitorType);
//                            MonitorTypePools.getInstances().add(reportItem.getMonitorType().name(), monitorType);
//                        }
//
////                        String title = monitorType.getWarnMessage();//标题
////                        if (ObjectUtils.isEmpty(title)) {
////                            title = "";
////                        }
////                        title += "[" + reportItem.getMonitorItemType().name() + "]";
//
////                        final ConcurrentHashMap<String, List<Status>> monitorItemListMap = (ConcurrentHashMap<String, List<Status>>) monitorType.getMessage();
//                        ConcurrentHashMap<String, List<Status>> monitorItemListMap = (ConcurrentHashMap<String, List<Status>>) monitorType.getMessage();
//                        if (ObjectUtils.isEmpty(monitorItemListMap)) {
//                            monitorItemListMap = MonitorItemListPools.getInstances().getMap();
//                            monitorType.setMessage(monitorItemListMap);
//                        }
//
//                        List<Status> monitorItemList = monitorItemListMap.get(reportItem.getMonitorItemType().name());//子状态类型
//                        if (ObjectUtils.isEmpty(monitorItemList)) {
//                            monitorItemList = new ArrayList<>();
//                            monitorItemListMap.putIfAbsent(reportItem.getMonitorItemType().name(), monitorItemList);
//                        }
//
//                        final WarningEvent warningEvent = new WarningEvent();
//                        warningEvent.setEventSource(reportItem.getMonitorItemType());
//                        warningEvent.setEventType(WarningLevel.ERROR);//初始告警级别
//                        warningEvent.setEventTime(new Date());
//                        warningEvent.setDeviceId(report.getDeviceId());
//                        warningEvent.setDeviceName(device.getData().getDeviceHostname());
//                        warningEvent.setUserId(userInfo.getUserId());
//                        warningEvent.setUserName(userInfo.getUserName());
//                        warningEvent.setUserMobile(userInfo.getMobile());
//
//                        if (reportItem.getStatus() == 0) {//获取信息失败告警
////                        baseHandle(reportItem, warningEvent);
////                            if (!warningService.checkIsWarningByTime(reportItem.getMonitorId())) {
//                                warningEvent.setId(new ObjectId(reportItem.getMonitorId()));
//                                warningEvent.setEventDetail(reportItem.getItem() + "[" + reportItem.getResult().toString() + "]");
//                                warningService.insertWarningEvent(warningEvent);
////                            }
//                        } else {
//                            Optional<ReportHandler> optional = ReportHandlerPools.getInstance().getHandler(reportItem.getMonitorItemType());
//                            if (optional.isPresent()) {
//                                Status status = optional.get().handle(warningService, warningEvent, reportItem, monitorType);
//                                monitorItemList.add(status);
//                            }
//                        }
//
//                        //根据监控类型更新
//                        monitorType.setDeviceId(reportItem.getMonitorType().getName() + "状态");
////                        monitorType.setWarnMessage(title);
//                        monitorItemListMap.replace(reportItem.getMonitorItemType().name(), monitorItemList);
//                        monitorType.setMessage(monitorItemListMap);
//
////                        monitorTypeMap.put(reportItem.getMonitorType().name(), monitorType);
//                        MonitorTypePools.getInstances().update(reportItem.getMonitorType().name(), monitorType);
//                    });
//                }
//                DeviceStatusPools.getInstances().update(report.getDeviceId(), monitorTypeMap);//更新设备状态缓存池
//            }
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

