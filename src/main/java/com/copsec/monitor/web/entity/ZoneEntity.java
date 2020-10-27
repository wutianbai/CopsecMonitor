package com.copsec.monitor.web.entity;

import lombok.Data;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "zone")
public class ZoneEntity {

	@Id
	private ObjectId id;
	private String zoneInfo;
}
