package com.copsec.monitor.web.entity;

import java.util.Date;

import lombok.Data;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "auditMessage")
@Data
public class AuditSyslogMessage {

	@Id
	private ObjectId id = new ObjectId();

	private String host;

	private Date reportTime;

	private String message;
}
