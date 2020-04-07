package com.copsec.monitor.web.pools;

import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

/**
 * 存放配置信息
 */
public class CommonPools {

//    private static final Logger logger = LoggerFactory.getLogger(CommonPools.class);

    private static ConcurrentHashMap<NetworkType, String> map = new ConcurrentHashMap<>();
//    private static ConcurrentHashMap<String, AllowedIpBean> ipMap = new ConcurrentHashMap<>();
    private static CommonPools commonPools;

    private CommonPools() {
    }

    public synchronized static CommonPools getInstances() {
        if (commonPools == null) {
            synchronized (CommonPools.class) {
                if (commonPools == null) {
                    commonPools = new CommonPools();
                }
            }
        }
        return commonPools;
    }

    public synchronized void add(NetworkType k, String v) {
        map.putIfAbsent(k, v);
    }

    public synchronized void update(NetworkType k, String v) {
        if (map.containsKey(k)) {
            map.replace(k, v);
        } else {
            add(k, v);
        }
    }

    public synchronized void delete(String k, String v) {
        if (map.containsKey(k)) {
            map.remove(k, v);
        }
    }

    public synchronized List<String> getAll() {
        List<String> list = map.entrySet().stream().map(Map.Entry::getValue).collect(toList());
        return list;
    }

//    public synchronized Map get() {
//        if (ipMap.entrySet().size() > 0) {
//            update(NetworkType.ALLOWEDIP, getAllIps().toString());
//        }
//        return map;
//    }

//    public synchronized void add(AllowedIpBean ip) {
//        ipMap.putIfAbsent(ip.getId(), ip);
//    }
//
//    public synchronized void updateAllowedIp(AllowedIpBean ip) {
//        if (ipMap.containsKey(ip.getId())) {
//            ipMap.replace(ip.getId(), ip);
//        } else {
//            ipMap.put(ip.getId(), ip);
//        }
//    }

//    public synchronized void delete(String id) {
//        if (ipMap.containsKey(id)) {
//            ipMap.remove(id, ipMap.get(id));
//        }
//    }
//
//    public synchronized List<AllowedIpBean> getAllIps() {
//        List<AllowedIpBean> list = ipMap.entrySet().stream()
//                .map(Map.Entry::getValue).collect(toList());
//        return list;
//    }

//    public synchronized AllowedIpBean getIp(String id) {
//        return ipMap.get(id);
//    }

    public synchronized List<String> getNetworkConfig(NetworkType type) {
        List<String> list = new ArrayList<>();
        if (ObjectUtils.isEmpty(map.get(type))) {
            return list;
        }
        list.add(map.get(type));
        return list;
    }
}
