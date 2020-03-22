package com.copsec.monitor.web.beans.monitor.MonitorEnum;

import java.util.concurrent.ConcurrentHashMap;

public enum MonitorTypeEnum {
    SYSTEM("系统", 0),
    APPLICATION("通道应用", 1),
    NETWORK("网络", 2),
    INSTANCE("实例", 3),
    LOG("日志", 4),
    CERT("证书", 5),
    PROCESSOR("处理器", 6);

    private String name;
    private int number;

    MonitorTypeEnum(String name, int number) {
        this.name = name;
        this.number = number;
    }

    //重写toSting()
    @Override
    public String toString() {
        return "name:" + name + " & number:" + number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    //在枚举列类中定义一个自定义方法，但如果要想能够被外面访问，需要定义成static类型。
    public static ConcurrentHashMap<String, String> containVal() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (MonitorTypeEnum e : MonitorTypeEnum.values()) {
            map.putIfAbsent(e.name(), e.getName());
        }
        return map;
    }
}
