package com.copsec.monitor.web.entity;

import java.util.Date;

import lombok.Data;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "preFileSynStatus")
@Data
public class PreFileSynHistoryStatus {

	@Id
	private ObjectId id;
	private String deviceId;
	private String taskName;
	private long gatherSizeSum;
	private long storageSizeSum;
	private Date updateTime;
	private long gatherTime;
	private long storageTime;
	private String fileName;
	private boolean error;
}
