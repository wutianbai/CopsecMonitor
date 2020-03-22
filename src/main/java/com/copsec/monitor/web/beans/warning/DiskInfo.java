package com.copsec.monitor.web.beans.warning;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiskInfo {
    private String id;//磁盘号
    private String chassis;//底架
    private String slot;//插槽
    private String raidId;//分区号
    private String status;//状态
    private String type;//类型
    private String media;//媒体
    private String spare;//剩余空间
    private String size;//容量
}
