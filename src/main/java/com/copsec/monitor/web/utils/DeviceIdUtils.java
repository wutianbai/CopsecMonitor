package com.copsec.monitor.web.utils;

import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.entity.DeviceNowStatus;
import org.springframework.util.ObjectUtils;

import java.text.DecimalFormat;
import java.util.UUID;

public class DeviceIdUtils {

    private static final String[] colors = new String[20];

    static {
        colors[0] = "#FD6035";
        colors[1] = "#82C730";
        colors[2] = "#FACE2F";
        colors[3] = "#36B6DD";
        colors[4] = "#E5A90B";
        colors[5] = "#D89455";
        colors[6] = "#DF1D7F";
        colors[7] = "#EF33D6";
        colors[8] = "#6716DC";
        colors[9] = "#CE6AFD";
        colors[10] = "#57A9E3";
        colors[11] = "#366AD1";
        colors[12] = "#2D9CE3";
        colors[13] = "#0CCAE7";
        colors[14] = "#12CB8C";
        colors[15] = "#42D4BB";
        colors[16] = "#0FE630";
        colors[17] = "#54BF42";
        colors[18] = "#519C43";
        colors[19] = "#EEA316";
    }

    public static String getUniqueId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static Status get(DeviceNowStatus status) {
        DecimalFormat df = new DecimalFormat("#.00");
        Status bean = new Status();
        bean.setDeviceId(status.getDeviceId());
//        bean.setCpuUseRate(status.getCpuUseRate() + Resources.PERCENTAGE);
//        bean.setMemoryUseRate(df.format(status.getMemoryUseRate()) + Resources.PERCENTAGE);
//        bean.setNetSpeed(FormatUtils.getFormatSpeedBS(status.getNetSpeed()));
//        bean.setDeviceStatus(status.getDeviceStatus());
        bean.setWarnMessage(status.getWarnMessage());

        return bean;
    }

    private static int getHashValue(String name) {

        int h = 0;
        if (!ObjectUtils.isEmpty(name) && name.length() > 0) {
            char val[] = name.toCharArray();
            for (int i = 0; i < val.length; i++) {

                h = 31 * h + val[i];
            }

            String num = String.valueOf(h);
            num = num.substring(num.length() - 2);
            if (Integer.valueOf(num) < 20) {

                return Integer.valueOf(num);
            } else {

                return Integer.valueOf(num.substring(num.length() - 1));
            }
        }
        return 0;
    }

    public static String getColor(String id) {
        return colors[getHashValue(id)];
    }

    public static String getColor(int index) {
        return colors[index];
    }

//    public static String getDeviceName(String deviceId) {
//        if (deviceId.endsWith(Resources.MUTTIPlEDEVICE)) {
//
//            return DevicePools.getInstance().getDevice(deviceId) == null ? deviceId :
//                    DevicePools.getInstance().getDevice(deviceId).getData().getName() + "-内端";
//        } else {
//
//            return DevicePools.getInstance().getDevice(deviceId) == null ? deviceId :
//                    DevicePools.getInstance().getDevice(deviceId).getData().getName();
//        }
//    }
}
