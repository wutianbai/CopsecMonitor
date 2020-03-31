package com.copsec.monitor.web.utils;

import com.copsec.monitor.web.beans.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;

public class PageUtils {
    public static Pageable returnPageable(PageInfo condition) {
        Pageable pageable;
        if (!ObjectUtils.isEmpty(condition.getSEcho()) && !ObjectUtils.isEmpty(condition.getIDisplayStart()) && !ObjectUtils.isEmpty(condition.getIDisplayLength())) {
            pageable = PageRequest.of((condition.getIDisplayStart()) / 20, condition.getIDisplayLength());
        } else {
            pageable = PageRequest.of(0, 20);
        }
        return pageable;
    }

    public static ConcurrentHashMap<String, Object> returnResult(PageInfo bean, Page page) {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        map.put("data", page.getContent());
        map.put("sEcho", bean.getSEcho());
        map.put("iTotalRecords", page.getTotalPages());
        map.put("iTotalDisplayRecords", page.getTotalElements());
        return map;
    }
}
