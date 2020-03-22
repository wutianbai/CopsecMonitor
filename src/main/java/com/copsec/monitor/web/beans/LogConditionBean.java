package com.copsec.monitor.web.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogConditionBean extends PageInfo{

    private String id;
    private String operateUser;
    private String ip;
    private String operateType;
    private String desc;
    private String result;
    private String date;

    private String start;
    private String end;



    private int page;

    private int size;

    private String fileName;
}
