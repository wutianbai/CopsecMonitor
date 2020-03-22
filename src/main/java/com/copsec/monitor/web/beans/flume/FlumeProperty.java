package com.copsec.monitor.web.beans.flume;

import lombok.Data;

@Data
public class FlumeProperty {

	/**
	 * property's key
	 */
	private String key;

	/**
	 * property's value
	 */
	private String v;

	/**
	 * must not be null value property
	 */
	private boolean must;

	/**
	 * default value of this property
	 */
	private String defaultV = "";



}
