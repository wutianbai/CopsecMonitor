package com.copsec.monitor.web.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatUtils {

    public static String getServerTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        return sdf.format(new Date());
    }
}
