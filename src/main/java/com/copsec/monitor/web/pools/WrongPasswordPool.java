package com.copsec.monitor.web.pools;

import com.copsec.monitor.web.beans.LockBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存错误密码的用户信息
 */
public class WrongPasswordPool {

    private static final Logger logger = LoggerFactory.getLogger(WrongPasswordPool.class);
    private static WrongPasswordPool pool;
    private static ConcurrentHashMap<String, LockBean> map = new ConcurrentHashMap<>();

    private WrongPasswordPool() {
    }

    public synchronized static WrongPasswordPool getInstances() {
        if (pool == null) {
            synchronized (WrongPasswordPool.class) {
                if (pool == null) {
                    pool = new WrongPasswordPool();
                }
            }
        }
        return pool;
    }

    public void add(String id, LockBean time) {
        if (!map.containsKey(id)) {
            map.put(id, time);
        }
    }

    public LockBean get(String id) {
        if (map.containsKey(id)) {
            return map.get(id);
        }
        return null;
    }

    public void remove(String id) {
        if (map.containsKey(id)) {
            LockBean time = map.get(id);
            map.remove(id, time);
        }
    }

    public void update(String id, LockBean bean) {
        map.replace(id, bean);
    }

}
