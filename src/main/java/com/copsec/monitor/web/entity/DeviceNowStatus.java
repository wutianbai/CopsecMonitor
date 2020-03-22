package com.copsec.monitor.web.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "deviceNowStatus")
public class DeviceNowStatus {

    @Id
    private ObjectId id;

    private String deviceId;

    private double cpuUseRate;
    private double memoryUseRate;
    private double netSpeed;

    private String deviceStatus;
    private String warnMessage;
    private Date updateTime;
    private double maxNetSpeed;
    private long totalAddSize;
    private double menUseRate;
}
