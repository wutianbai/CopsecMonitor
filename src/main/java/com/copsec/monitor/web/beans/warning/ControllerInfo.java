package com.copsec.monitor.web.beans.warning;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ControllerInfo {
    private String manufacturer;//厂商
    private String model;//型号
    private String version;//硬盘版本
    private String volumes;//分区数
    private String disks;//磁盘数
}
