package com.copsec.monitor.web.beans.monitor;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class AddressInfoBean {

	private String decviceName;
	private String deviceType;
	private String ipAddress;
	private String subnetMask;
	private String linkState;
}
