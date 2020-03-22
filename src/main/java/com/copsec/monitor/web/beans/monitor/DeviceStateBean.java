package com.copsec.monitor.web.beans.monitor;

import lombok.Setter;

@lombok.Getter
@Setter
public class DeviceStateBean {

	private String decviceName;
	private String deviceType;
	private String speed;
	private String duplex;
	private String mediaType;
	private String linkState;
}
