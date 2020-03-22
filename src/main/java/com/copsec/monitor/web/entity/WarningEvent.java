package com.copsec.monitor.web.entity;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.WarningLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "warningEvent")
@Getter
@Setter
@Data
public class WarningEvent {

	@Id
	private ObjectId id;
//	private String eventId;
    private MonitorItemEnum eventSource;
    private Date eventTime;
    private String eventDetail;
    private WarningLevel eventType;
    private String deviceId;
    private String deviceName;
    private String userId;
    private String userName;
    private String userMobile;
}
