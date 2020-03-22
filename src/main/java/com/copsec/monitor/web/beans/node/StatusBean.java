package com.copsec.monitor.web.beans.node;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Data
public class StatusBean {

    private boolean hasSub = false;

    private String innerDeviceId = "N/A";

    private String deviceId;

    private List<Status> list = new ArrayList<>();

    private String status;
}
