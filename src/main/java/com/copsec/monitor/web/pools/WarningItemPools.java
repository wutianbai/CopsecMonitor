package com.copsec.monitor.web.pools;

import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.entity.WarningItemEntity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WarningItemPools {
    private static WarningItemPools pool;
    private static ConcurrentHashMap<String, WarningItemBean> map = new ConcurrentHashMap<>();

    private WarningItemPools() {
    }

    public static synchronized WarningItemPools getInstances() {
        if (pool == null) {
            synchronized (WarningItemPools.class) {
                if (pool == null) {
                    pool = new WarningItemPools();
                }
            }
        }
        return pool;
    }

    public synchronized void add(WarningItemBean bean) {
        map.putIfAbsent(bean.getWarningId(), bean);
    }

    public synchronized void add(List<WarningItemBean> beanList) {
        beanList.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && !ObjectUtils.isEmpty(d.getWarningId()))
                .forEach((bean) -> map.putIfAbsent(bean.getWarningId(), bean));
    }

    public synchronized WarningItemBean get(String monitorId) {
        if (map.containsKey(monitorId)) {
            return map.get(monitorId);
        }
        return null;
    }

    public synchronized List<WarningItemBean> get(List<String> idArray) {
        List<WarningItemBean> list = new ArrayList<>();
        idArray.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && map.containsKey(d))
                .forEach((id) -> list.add(map.get(id)));

        return list;
    }

    public synchronized void update(WarningItemBean bean) {
        if (map.containsKey(bean.getWarningId())) {
            map.replace(bean.getWarningId(), bean);
        } else {
            map.put(bean.getWarningId(), bean);
        }
    }

    public synchronized void delete(String id) {
        if (map.containsKey(id)) {
            map.remove(id, map.get(id));
        }
    }

    public synchronized void delete(List<String> idArray) {
        idArray.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && map.containsKey(d))
                .forEach((id) -> map.remove(id, map.get(id)));
    }

    public synchronized List<WarningItemBean> getAll() {
        ArrayList<WarningItemBean> list = new ArrayList<>();
        map.forEach((k, v) -> list.add(v));
        return list;
    }

    public synchronized void clean() {
        map.clear();
    }

    public void save(MongoRepository repository){

    	repository.deleteAll();
    	getAll().stream().forEach(warningItemBean ->  {

			WarningItemEntity warningItemEntity = new WarningItemEntity();
			warningItemEntity.setWarningItemInfo(warningItemBean.toString());
			repository.save(warningItemEntity);
		});
	}
}
