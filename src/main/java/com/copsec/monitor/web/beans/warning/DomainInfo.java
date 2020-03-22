package com.copsec.monitor.web.beans.warning;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomainInfo {
    private String name;//域名
    private String state;//状态
    private String flags;//标识
    private String conns;//接口
    private String ncpu;//CPU数
    private String mem;//内存
    private String util;//使用率
    private String normutil;//正常使用率
    private String uptime;//活动时间
}
