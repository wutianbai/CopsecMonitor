package com.copsec.monitor.web.beans.taskMonitor;

import com.copsec.monitor.web.config.Resources;
import lombok.Data;

@Data
public class FileTaskSyncPolicyBean {

	private String taskName;

	private String totalEnable = "no";

	private int total;

	private String unit;

	private String intervalEnable = "no";

	private int interval;

	private String intervalUnit;

	@Override
	public String toString(){

		StringBuilder builder = new StringBuilder();
		builder.append(this.taskName+ Resources.SPLITER);
		builder.append(this.total + Resources.SPLITER);
		builder.append(this.unit + Resources.SPLITER);
		builder.append(this.interval + Resources.SPLITER);
		builder.append(this.intervalUnit + Resources.SPLITER);
		builder.append(this.totalEnable + Resources.SPLITER);
		builder.append(this.intervalEnable);

		return builder.toString();
	}
}
