package com.copsec.monitor.web.entity;

import java.util.Date;

import lombok.Data;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "memoryHistoryStatus")
public class MemoryHistoryStatus {

	@Id
	private ObjectId id;

	private String deviceId;
	private int year;
	private int month;
	private int day;
	private int hour;

	private double maxUseRate;
	private Date maxUseTime;
}
