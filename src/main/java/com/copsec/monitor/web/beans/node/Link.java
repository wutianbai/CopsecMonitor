package com.copsec.monitor.web.beans.node;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Link extends BaseNode {
    private String group = "edges";
    private String classes;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

