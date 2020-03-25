package com.copsec.monitor.web.beans.warning;

import lombok.Data;

import java.util.Date;

@Data
public class CertInfoBean {
    private String nickname;//证书名称
    private String subject;//证书主体
    private String issuer;//颁发机构
    private Date starTime;//起始时间
    private Date endTime;//有效期时间
    private String message = "";//获取证书信息
    private int status;//1正常；0异常，对应message有异常信息
}
