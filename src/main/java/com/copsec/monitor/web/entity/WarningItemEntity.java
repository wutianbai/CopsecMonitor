package com.copsec.monitor.web.entity;

import java.util.UUID;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.WarningLevel;
import lombok.Data;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "warningItem")
public class WarningItemEntity {

	@Id
	private ObjectId id;
	private String warningItemId;
	private String warningItemInfo;
}
