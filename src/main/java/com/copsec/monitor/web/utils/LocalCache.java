package com.copsec.monitor.web.utils;

import com.copsec.monitor.web.entity.CacheEntity;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class LocalCache {
    // 默认的缓存容量
    private static int DEFAULT_CAPACITY = 10240;
    // 最大容量
    private static int MAX_CAPACITY = 1000000;
    // 刷新缓存的频率
    private static int MONITOR_DURATION = 2;

    // 启动监控线程
//    static {
//        new Thread(new TimeoutTimerThread()).start();
//    }

    // 使用默认容量创建一个Map
    private static ConcurrentMap<String, CacheEntity> cache = new ConcurrentHashMap<>(DEFAULT_CAPACITY);

    private LocalCache() {

    }

    /**
     * 将key-value 保存到本地缓存并制定该缓存的过期时间
     *
     * @param key
     * @param value
     * @param expireTime 过期时间，如果是-1 则表示永不过期
     * @return
     */
    public static synchronized boolean putValue(String key, Object value, int expireTime) {
        return putCloneValue(key, value, expireTime);
    }

    /**
     * 将值通过序列化clone 处理后保存到缓存中，可以解决值引用的问题
     *
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    private static boolean putCloneValue(String key, Object value, int expireTime) {
        try {
            if (cache.size() >= MAX_CAPACITY) {
                return false;
            }
            // 序列化赋值
            CacheEntity entityClone = new CacheEntity(value, System.nanoTime(), expireTime);
            cache.put(key, entityClone);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 序列化 克隆处理
     *
     * @param object
     * @return
     */
    private static <T extends Serializable> T clone(T object) {
        T cloneObject = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            cloneObject = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObject;
    }

    /**
     * 从本地缓存中获取key对应的值，如果该值不存则则返回null
     *
     * @param key
     * @return
     */
    public synchronized static Object getValue(String key) {
        return cache.get(key).getValue();
    }

    public synchronized static ConcurrentMap<String, CacheEntity> getCache() {
        return cache;
    }

    /**
     * 清空所有
     */
    public synchronized static void clear() {
        cache.clear();
    }

    public synchronized static void remove(String key) {
        cache.remove(key);
    }

    /**
     * 过期处理线程
     *
     * @author Lenovo
     * @version $Id: LocalCache.java, v 0.1 2014年9月6日 下午1:34:23 Lenovo Exp $
     */
    static class TimeoutTimerThread implements Runnable {
        public void run() {
            while (true) {
                try {
                    System.out.println("Cache monitor");
                    TimeUnit.SECONDS.sleep(MONITOR_DURATION);
                    checkTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 过期缓存的具体处理方法
         *
         * @throws Exception
         */
        private void checkTime() throws Exception {
            for (String key : cache.keySet()) {
                CacheEntity tce = cache.get(key);
                long timoutTime = TimeUnit.NANOSECONDS.toSeconds(System
                        .nanoTime() - tce.getGmtModify());
                // " 过期时间 : "+timoutTime);
                if (tce.getExpire() > timoutTime) {
                    continue;
                }
                System.out.println(" 清除过期缓存 ： " + key);
                // 清除过期缓存和删除对应的缓存队列
                cache.remove(key);
            }
        }
    }
}
