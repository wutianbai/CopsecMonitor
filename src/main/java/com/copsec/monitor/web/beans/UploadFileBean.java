package com.copsec.monitor.web.beans;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UploadFileBean {

	private boolean isChunked;
	private String chunks;
	private String chunk;
	private String fileId;
	private String fileName;
}
