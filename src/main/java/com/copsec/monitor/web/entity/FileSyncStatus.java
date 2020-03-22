package com.copsec.monitor.web.entity;

import java.util.Date;

import lombok.Data;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "fileSyncStatus")
public class FileSyncStatus {

	@Id
	private ObjectId id;
	private String taskName;
	private Date updateTime;
	private String message;
	private Date operateTime;
	private String operateUser;
	private boolean status;
}
