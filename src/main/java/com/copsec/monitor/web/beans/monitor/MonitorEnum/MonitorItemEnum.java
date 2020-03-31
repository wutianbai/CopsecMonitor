package com.copsec.monitor.web.beans.monitor.MonitorEnum;

import java.util.concurrent.ConcurrentHashMap;

public enum MonitorItemEnum {
    CPU("中央处理机", 0, "请输入阈值", MonitorTypeEnum.SYSTEM),
    DISK("磁盘", 1, "请输入阈值", MonitorTypeEnum.SYSTEM),
    MEMORY("内存", 2, "请输入阈值", MonitorTypeEnum.SYSTEM),
    USER("用户", 3, "使用\",\"分隔的用户id标识", MonitorTypeEnum.SYSTEM),
    SYSTEMTYPE("系统类型", 4, "默认值", MonitorTypeEnum.SYSTEM),
    SYSTEMVERSION("系统版本", 5, "默认值", MonitorTypeEnum.SYSTEM),
    SYSTEMPATCH("系统补丁", 6, "默认值", MonitorTypeEnum.SYSTEM),
    APPLICATION("应用程序", 7, "请输入代理通道访问路径", MonitorTypeEnum.APPLICATION),
    INSTANCES_WEB70("Web70实例", 8, "请输入实例路径", MonitorTypeEnum.INSTANCE),
    INSTANCES_WEBPROXY40("WebProxy40实例", 9, "请输入实例路径", MonitorTypeEnum.INSTANCE),
    INSTANCES_CONFIG("配置储存库", 10, "请输入实例路径", MonitorTypeEnum.INSTANCE),
    INSTANCES_USER("用户储存库", 11, "请输入实例路径", MonitorTypeEnum.INSTANCE),
    NETWORK("网络", 12, "请输入网络IP地址", MonitorTypeEnum.NETWORK),
    ACCESSLOG("授权日志", 13, "例{\"logPath\":\"/usr/sjes/am11/access/log\",\"threadHold\":50}", MonitorTypeEnum.LOG),
    PROXYLOG("代理日志", 14, "例{\"logPath\":\"/usr/sjes/am11/access/log\",\"threadHold\":50}", MonitorTypeEnum.LOG),
    CERT70("Web70证书", 15, "", MonitorTypeEnum.CERT),
    CERT40("WebProxy40证书", 16, "", MonitorTypeEnum.CERT),
    IMSERVICE("IM服务", 17, "多个进程名称之间使用\",\"分隔", MonitorTypeEnum.PROCESSOR),
    RAID("磁盘阵列", 18, "默认值", MonitorTypeEnum.SYSTEM);

    private String name;
    private int number;
    private String message;
    private MonitorTypeEnum type;

    MonitorItemEnum(String name, int number, String message, MonitorTypeEnum type) {
        this.name = name;
        this.number = number;
        this.message = message;
        this.type = type;
    }

    //重写toSting()
    @Override
    public String toString() {
        return "name:" + name + " & number:" + number + " & message:" + message + " & type:" + type;
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

    public MonitorTypeEnum getType() {
        return type;
    }

    public void setType(MonitorTypeEnum type) {
        this.type = type;
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

//    public static MonitorTypeEnum getTypeByName(MonitorItemEnum name) {
//        for (MonitorItemEnum m : MonitorItemEnum.values()) {
//            if (name.name().equals(m.name)) {
//                return MonitorTypeEnum.valueOf(m.getType());
//            }
//        }
//        return null;
//    }
}
