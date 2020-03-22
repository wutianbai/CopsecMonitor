package com.copsec.monitor.web.beans.warning;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VmInfoBean {//Solaris操作系统对应的虚拟机信息
    List<DiskInfo> diskInfos;//磁盘信息
    List<DomainInfo> domainInfos;//域信息
    List<ControllerInfo> controllerInfos;//硬件信息
    List<VolumeInfo> volumeInfos;//卷信息
}
