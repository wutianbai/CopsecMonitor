package com.copsec.monitor.web.beans.taskMonitor;

import com.copsec.monitor.web.config.Resources;
import lombok.Data;

@Data
public class FileSyncMonitorBean {

	private String status = "forbidden";

	private int interval;

	/**
	 * minute or hour
	 */
	private String unit;

	@Override
	public String toString(){

		StringBuilder builder = new StringBuilder();
		builder.append(this.status + Resources.SPLITER);
		builder.append(this.interval + Resources.SPLITER);
		builder.append(this.unit);
		return builder.toString();
	}
}
