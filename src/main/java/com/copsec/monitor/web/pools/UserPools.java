package com.copsec.monitor.web.pools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.copsec.monitor.web.beans.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserPools {

    private static final Logger logger = LoggerFactory.getLogger(UserPools.class);

    private static UserPools pool;
    private static ConcurrentHashMap<String, UserBean> map = new ConcurrentHashMap<>();

    private UserPools() {
    }

    public static synchronized UserPools getInstances() {
        if (pool == null) {
            synchronized (UserPools.class) {
                if (pool == null) {
                    pool = new UserPools();
                }
            }
        }
        return pool;
    }

    public synchronized void add(UserBean userBean) {

        map.putIfAbsent(userBean.getId(), userBean);
    }

    public synchronized UserBean get(String id) {

        if (map.containsKey(id)) {

            return map.get(id);
        }
        return null;
    }

    public synchronized void update(UserBean userBean) {
        if (map.containsKey(userBean.getId())) {
            map.replace(userBean.getId(), userBean);
        } else {
            map.put(userBean.getId(), userBean);
        }
    }

    public synchronized List<UserBean> getAll() {
        ArrayList<UserBean> list = new ArrayList<>();
        map.forEach((k, v) -> {
            list.add(v);
        });
        return list;
    }
}
