package com.copsec.monitor.web.beans.remote;

import com.copsec.monitor.web.config.Resources;
import lombok.Data;

@Data
public class RemoteDeviceBean {

	private String deviceId;

	private String deviceName;

	private String deviceType;

	private String deviceIp;

	private int devicePort;

	private String deviceProtocol;

	@Override
	public String toString(){

		StringBuilder builder = new StringBuilder();
		builder.append(this.deviceId + Resources.SPLITER);
		builder.append(this.deviceName + Resources.SPLITER);
		builder.append(this.deviceType + Resources.SPLITER);
		builder.append(this.deviceIp + Resources.SPLITER);
		builder.append(this.devicePort + Resources.SPLITER);
		builder.append(this.deviceProtocol);

		return builder.toString();
	}
}
