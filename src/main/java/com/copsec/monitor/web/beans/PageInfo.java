package com.copsec.monitor.web.beans;

import lombok.Data;

@Data
public class PageInfo {
    private int sEcho;
    private int iDisplayStart;
    private int iDisplayLength;
}
