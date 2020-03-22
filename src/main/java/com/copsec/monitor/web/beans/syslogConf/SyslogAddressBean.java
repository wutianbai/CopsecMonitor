package com.copsec.monitor.web.beans.syslogConf;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class SyslogAddressBean {

	private String logIp;

	private int logPort;

	@Override
	public String toString(){

		return JSON.toJSONString(this);
	}
}
