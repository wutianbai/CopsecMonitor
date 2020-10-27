package com.copsec.monitor.web.entity;

import org.bson.types.ObjectId;
import com.copsec.monitor.web.beans.node.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@lombok.Data
@Document(collection = "link")
public class LinkEntity{

	@Id
	private ObjectId id;
	private String linkInfo;
}
