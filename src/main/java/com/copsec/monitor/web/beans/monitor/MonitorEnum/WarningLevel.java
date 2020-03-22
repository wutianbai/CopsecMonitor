package com.copsec.monitor.web.beans.monitor.MonitorEnum;

import java.util.concurrent.ConcurrentHashMap;

public enum WarningLevel {
    WARNING("一般告警", 1),
    ERROR("严重告警", 2),
    NORMAL("非告警", 3);

    private String name;
    private int number;

    WarningLevel(String name, int number) {
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
        WarningLevel[] l = WarningLevel.values();
        for (WarningLevel e : l) {
            map.putIfAbsent(e.name(), e.getName());
        }
        return map;
    }

    public static String getValueByName(String name) {
        for (WarningLevel w : WarningLevel.values()) {
            if (name.equals(w.getName())) {
                return w.name();
            }
        }
        return null;
    }
}
