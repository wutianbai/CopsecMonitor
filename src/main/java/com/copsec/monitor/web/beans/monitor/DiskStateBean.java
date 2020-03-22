package com.copsec.monitor.web.beans.monitor;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class DiskStateBean {

	private String fileSystem;
	private String size;//总大小
	private String usedSize;//已用大小
	private String availSize;//可用大小
	private String usedPercent;//已用百分比
	private String mountedOn;//安装路径

	public DiskStateBean(String fileSystem, String size, String usedSize, String availSize, String usedPercent, String mountedOn) {
		super();
		this.fileSystem = fileSystem;
		this.size = size;
		this.usedSize = usedSize;
		this.availSize = availSize;
		this.usedPercent = usedPercent;
		this.mountedOn = mountedOn;
	}
}
