package com.copsec.monitor.web.beans.flume;

import java.util.List;

import lombok.Data;

@Data
public class FlumePropBean {

	private List<String> types;
	private List<FlumeProperty> props;
	private String typeId;
}
