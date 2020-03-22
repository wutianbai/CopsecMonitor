package com.copsec.monitor.web.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "operate")
@Getter
@Setter
@Data
public class OperateLog {

    @Id
    private ObjectId id;
    private String operateUser;
    private String ip;
    private String operateType;
    private String desc;
    private int result;
    private Date date;
}
