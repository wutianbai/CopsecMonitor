package com.copsec.monitor.web.entity;

import java.util.Date;

import lombok.Data;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "syslogMessage")
@Data
public class SyslogMessage {

	@Id
	private ObjectId id;

	private String host;

	private Date reportTime;

	private String message;
}
