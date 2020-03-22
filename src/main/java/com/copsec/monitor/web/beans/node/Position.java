package com.copsec.monitor.web.beans.node;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class Position {

    private double x;

    private double y;

    @Override
    public String toString() {
        return JSON.toJSONString(this, false);
    }
}
