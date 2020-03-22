package com.copsec.monitor.web.beans.remote;

import java.util.List;

import lombok.Data;

@Data
public class RemoteOperateBean {

	private String deviceId;

	private String methodType;

	private String taskName;

	private List<String> params;
}
