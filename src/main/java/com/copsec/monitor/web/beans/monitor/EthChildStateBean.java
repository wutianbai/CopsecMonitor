package com.copsec.monitor.web.beans.monitor;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EthChildStateBean {

	private String connected;
	private String ethName;
	private String receiveSpeed;
	private String sendSpeed;
	private String time;
	private String totalReceive;
	private String totalSend;
}
