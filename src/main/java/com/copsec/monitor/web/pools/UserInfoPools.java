package com.copsec.monitor.web.pools;

import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.entity.UserInfoEntity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserInfoPools {
    private static UserInfoPools pool;
    private static ConcurrentHashMap<String, UserInfoBean> map = new ConcurrentHashMap<>();

    private UserInfoPools() {
    }

    public static synchronized UserInfoPools getInstances() {
        if (pool == null) {
            synchronized (UserInfoPools.class) {
                if (pool == null) {
                    pool = new UserInfoPools();
                }
            }
        }
        return pool;
    }

    public synchronized void add(UserInfoBean bean) {
        map.putIfAbsent(bean.getUserId(), bean);
    }

    public synchronized void add(List<UserInfoBean> beanList) {
        beanList.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && !ObjectUtils.isEmpty(d.getUserId()))
                .forEach((bean) -> map.putIfAbsent(bean.getUserId(), bean));
    }

    public synchronized UserInfoBean get(String userId) {
        if (map.containsKey(userId)) {
            return map.get(userId);
        }
        return null;
    }

    public synchronized List<UserInfoBean> get(List<String> idArray) {
        List<UserInfoBean> list = new ArrayList<>();
        idArray.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && map.containsKey(d))
                .forEach((id) -> list.add(map.get(id)));
        return list;
    }

    public synchronized void update(UserInfoBean bean) {
        if (map.containsKey(bean.getUserId())) {
            map.replace(bean.getUserId(), bean);
        } else {
            map.put(bean.getUserId(), bean);
        }
    }

    public synchronized void delete(String userId) {
        if (map.containsKey(userId)) {
            map.remove(userId, map.get(userId));
        }
    }

    public synchronized void delete(List<String> idArray) {
        idArray.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && map.containsKey(d))
                .forEach((id) -> map.remove(id, map.get(id)));
    }

    public synchronized List<UserInfoBean> getAll() {
        ArrayList<UserInfoBean> list = new ArrayList<>();
        map.forEach((k, v) -> list.add(v));
        return list;
    }

    public synchronized void clean() {
        map.clear();
    }

    public void save(MongoRepository repository){

    	repository.deleteAll();
    	getAll().stream().forEach(userInfoBean -> {

			UserInfoEntity userInfoEntity = new UserInfoEntity();
			userInfoEntity.setUserInfo(userInfoBean.toString());
			repository.save(userInfoEntity);
		});
	}
}
