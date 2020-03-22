package com.copsec.monitor.web.beans.warning;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertConfig {
    private String instanceName = "";//证书对应实例安装路径名称(WebProxy40实例配置格式为：实例名称-端口号)
    private String nickname = "";//证书名称，多个值使用“,”分隔

    @Override
    public String toString() {
        return JSON.toJSONString(this, false);
    }
}
