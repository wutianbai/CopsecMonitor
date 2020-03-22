package com.copsec.monitor.web.beans.syslogConf;

import com.copsec.monitor.web.config.Resources;
import lombok.Data;

@Data
public class LogSettingBean {

	private String logType;
	private String logName;
	private boolean enable;

	@Override
	public String toString(){

		StringBuilder builder = new StringBuilder();
		builder.append(this.logType + Resources.SPLITER);
		builder.append(this.logName + Resources.SPLITER);
		builder.append(enable?"yes":"no");
		return builder.toString();
	}
}
