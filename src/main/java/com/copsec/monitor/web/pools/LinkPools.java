package com.copsec.monitor.web.pools;

import com.copsec.monitor.web.beans.node.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

public class LinkPools {

    private static final Logger logger = LoggerFactory.getLogger(LinkPools.class);
    private static LinkPools pools;

    private LinkPools() {
    }

    private Map<String, Link> map = Collections.synchronizedMap(new HashMap<>());

    public static synchronized LinkPools getInstance() {
        if (pools == null) {
            synchronized (LinkPools.class) {
                if (pools == null) {
                    pools = new LinkPools();
                }
            }
        }
        return pools;
    }

    public synchronized Link add(Link link) {
        if (!map.containsValue(link)) {
            map.put(link.getData().getId(), link);
            return link;
        }
        return null;
    }

    public synchronized void add(List<Link> beanList) {
        beanList.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && !ObjectUtils.isEmpty(d.getData().getId()))
                .forEach((bean) -> map.putIfAbsent(bean.getData().getId(), bean));
    }

    public synchronized Link get(String linkId) {
        if (map.containsKey(linkId)) {
            return map.get(linkId);
        }
        return null;
    }

    public synchronized Link update(Link link) {
        if (map.containsKey(link.getData().getId())) {
            map.replace(link.getData().getId(), link);
            return link;
        }
        return null;
    }

    public synchronized void update(List<Link> linkList) {
        linkList.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && map.containsKey(d.getData().getId()))
                .forEach((bean) -> map.replace(bean.getData().getId(), bean));
    }

    public synchronized void delete(String linkId) {
        Link link = map.get(linkId);
        map.remove(linkId, link);
    }

    public synchronized void delete(List<Link> linkList) {
        linkList.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && map.containsKey(d.getData().getId()))
                .forEach((bean) -> map.remove(bean.getData().getId()));
    }

    public synchronized List<Link> getAll() {
        ArrayList<Link> list = new ArrayList<>();
        map.forEach((k, v) -> list.add(v));
        return list;
    }

    /**
     * 根据设备id删除该设备对应的所有连接关系
     *
     * @param deviceId
     */
    public synchronized void deleteLinksByDeviceId(final String deviceId) {
        Iterator<Map.Entry<String, Link>> iterator = map.entrySet().iterator();
        iterator.forEachRemaining(entry -> {
            Link link = entry.getValue();
            if (link.getData().getSource().equalsIgnoreCase(deviceId) || link.getData().getTarget().equalsIgnoreCase(deviceId)) {
                iterator.remove();
            }
        });
        if (logger.isDebugEnabled()) {
            logger.debug("delete all connections {} success", deviceId);
        }
    }

    /**
     * 清空
     */
    public synchronized void clean() {
        map.clear();
    }
}
