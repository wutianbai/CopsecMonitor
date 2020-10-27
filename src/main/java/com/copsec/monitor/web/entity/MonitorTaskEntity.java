package com.copsec.monitor.web.entity;

import java.util.UUID;

import lombok.Data;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "monitorTask")
public class MonitorTaskEntity {

	@Id
	private ObjectId id;
	private String monitorTaskInfo;
}
