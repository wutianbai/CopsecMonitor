package com.copsec.monitor.web.beans.remote;

import lombok.Data;

@Data
public class RemoteUriBean {

	/**
	 * uri指定操作
	 */
	private String uriType;

	private String url;

	/**
	 * post or get
	 */
	private String uriMethod;

}
