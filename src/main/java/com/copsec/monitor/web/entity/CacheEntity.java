package com.copsec.monitor.web.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//public class CacheEntity implements Serializable {
public class CacheEntity {
//    private static final long serialVersionUID = 7172649826282703560L;
    /**
     * 值
     */
    private Object value;
    /**
     * 保存的时间戳
     */
    private long gmtModify;
    /**
     * 过期时间
     */
    private int expire;

    public CacheEntity(Object value, long gmtModify, int expire) {
        super();
        this.value = value;
        this.gmtModify = gmtModify;
        this.expire = expire;
    }
}
