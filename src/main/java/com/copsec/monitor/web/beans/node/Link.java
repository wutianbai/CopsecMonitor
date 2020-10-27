package com.copsec.monitor.web.beans.node;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Link{
    private String group = "edges";
    private String classes;
	private Data data;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

