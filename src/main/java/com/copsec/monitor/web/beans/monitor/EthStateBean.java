package com.copsec.monitor.web.beans.monitor;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EthStateBean {

	private HashMap<String,EthChildStateBean> ethMap;
	private String receiveSpeed;
	private String sendSpeed;
	private String totalReceive;
	private String totalSend;
	private List<EthChildStateBean> list;
}
