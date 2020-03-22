package com.copsec.monitor.web.beans.node;

import lombok.Data;

@Data
public class LinkBean {
    private String id;
    private String source;
    private String[] targets;
    private String classes;
}
