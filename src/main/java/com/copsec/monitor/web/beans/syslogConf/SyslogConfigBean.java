package com.copsec.monitor.web.beans.syslogConf;

import java.util.List;

import com.copsec.monitor.web.config.Resources;
import lombok.Data;

@Data
public class SyslogConfigBean {

	private String status = "no";

	private String host;

	private List<SyslogAddressBean> list;

	@Override
	public String toString(){

		StringBuilder builder = new StringBuilder();
		builder.append(this.status + Resources.SPLITER);
		builder.append(this.host + Resources.SPLITER);
		builder.append(this.list.toString());
		return builder.toString();
	}
}
