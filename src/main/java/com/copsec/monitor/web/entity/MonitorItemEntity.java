package com.copsec.monitor.web.entity;

import java.util.UUID;

import com.copsec.monitor.web.beans.monitor.LogConfig;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorTypeEnum;
import com.copsec.monitor.web.beans.warning.CertConfig;
import lombok.Data;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "monitorItem")
public class MonitorItemEntity {

	@Id
	private ObjectId id;
	private String monitorItemInfo;
}
