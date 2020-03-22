package com.copsec.monitor.web.beans.monitor.MonitorEnum;

import java.util.concurrent.ConcurrentHashMap;

public enum MonitorItemEnum {
    CPU("中央处理机", 0, "请输入阈值"),
    DISK("磁盘", 1, "请输入阈值"),
    MEMORY("内存", 2, "请输入阈值"),
    USER("用户", 3, "使用\",\"分隔的用户id标识"),
    SYSTEMTYPE("系统类型", 4, "默认值"),
    SYSTEMVERSION("系统版本", 5, "默认值"),
    SYSTEMPATCH("系统补丁", 6, "默认值"),
    APPLICATION("应用程序", 7, "请输入代理通道访问路径"),
    INSTANCES_WEB70("实例_WEB70", 8, "(请输入实例路径"),
    INSTANCES_WEBPROXY40("实例_WEB代理40", 9, "请输入实例路径"),
    INSTANCES_CONFIG("实例_配置", 10, "请输入实例路径"),
    INSTANCES_USER("实例_用户", 11, "请输入实例路径"),
    NETWORK("网络", 12, "请输入网络IP地址"),
    ACCESSLOG("访问日志", 13, "例{\"logPath\":\"/usr/sjes/am11/access/log\",\"threadHold\":50}"),
    PROXYLOG("代理日志", 14, "例{\"logPath\":\"/usr/sjes/am11/access/log\",\"threadHold\":50}"),
    CERT70("证书70", 15, ""),
    CERT40("证书40", 16, ""),
    IMSERVICE("IM服务", 17, "多个进程名称之间使用\",\"分隔"),
    RAID("磁盘阵列", 18, "默认值");

    private String name;
    private int number;
    private String message;

    MonitorItemEnum(String name, int number, String message) {
        this.name = name;
        this.number = number;
        this.message = message;
    }

    //重写toSting()
    @Override
    public String toString() {
        return "name:" + name + " & number:" + number + " & message:" + message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //在枚举列类中定义一个自定义方法，但如果要想能够被外面访问，需要定义成static类型。
    public static ConcurrentHashMap<String, String> containVal() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (MonitorItemEnum e : MonitorItemEnum.values()) {
            map.putIfAbsent(e.name(), e.getName());
        }
        return map;
    }

    public static ConcurrentHashMap<String, String> containMessage() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (MonitorItemEnum e : MonitorItemEnum.values()) {
            map.putIfAbsent(e.name(), e.getMessage());
        }
        return map;
    }

    public static String getValueByName(String name) {
        for (MonitorItemEnum m : MonitorItemEnum.values()) {
            if (name.equals(m.getName())) {
                return m.name();
            }
        }
        return null;
    }
}
