package com.copsec.monitor.web.runner;

import com.copsec.monitor.web.beans.warning.Report;
import com.copsec.monitor.web.entity.CacheEntity;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.utils.LocalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

@Component
@Order
public class ReceiveWarningEventThread implements CommandLineRunner {
//public class ReceiveWarningEventThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveWarningEventThread.class);

    private static int MONITOR_DURATION = 1;

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

                ReportHandler.handle(report);
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
}