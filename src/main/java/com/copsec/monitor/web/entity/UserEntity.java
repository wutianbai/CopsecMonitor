package com.copsec.monitor.web.entity;

import lombok.Data;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user")
public class UserEntity {

	@Id
	private ObjectId id;
	private String userInfo;
}
