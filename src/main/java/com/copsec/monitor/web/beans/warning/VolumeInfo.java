package com.copsec.monitor.web.beans.warning;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VolumeInfo {
    private String id;//分区号
    private String name;//分区名
    private String device;//装置
    private String status;//状态
    private String numDisks;//磁盘数
    private String level;//等级
    private String size;//容量
}
