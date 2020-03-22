package com.copsec.monitor.web.utils;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtils {

    private static final Logger logger = LoggerFactory.getLogger(FormatUtils.class);
    private static final long oneS = 1000;
    private static final long oneMin = 1000 * 60;
    private static final long oneH = 1000 * 60 * 60;
    public static final long oneD = 24 * 60 * 60 * 1000;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static final DecimalFormat numberFormat = new DecimalFormat("#");

    public static String getFormatTimeMillis(long timeMillis) {
        if (timeMillis < oneS) { // 1 s
            return timeMillis + " ms";
        } else if (timeMillis < oneMin) { // 1 min
            return timeMillis / oneS + " s " + getFormatTimeMillis(timeMillis % oneS);
        } else if (timeMillis < oneH) { // 1 h
            return timeMillis / oneMin + " min " + getFormatTimeMillis(timeMillis % oneMin);
        } else if (timeMillis < oneD) { // 1 d
            return timeMillis / oneH + " h " + getFormatTimeMillis(timeMillis % oneH);
        } else {
            return timeMillis / oneD + " d " + getFormatTimeMillis(timeMillis % oneD);
        }

    }

    private static final double KB = 1024;
    private static final double MB = 1024 * 1024;
    private static final double GB = 1024 * 1024 * 1024;
    private static final double TB = 1024 * 1024 * 1024 * 1024;

    public static String getFormatSpeedBS(double speedBS) {

        if (speedBS < KB) { // KB
            return convert(speedBS) + " B/s";
        } else if (speedBS < MB) { // MB
            return convert(speedBS / KB) + " KB/s";
        } else if (speedBS < GB) { // GB
            return convert(speedBS / MB) + " MB/s";
        } else { // GB
            return convert(speedBS / GB) + " GB/s";
        }
    }

    public static String getSpeed(long size, long time) {

        if (time < 1000) {

            time = 1000;
        }
        if (ObjectUtils.isEmpty(size) || ObjectUtils.isEmpty(time) || time <= 0 || size <= 0) {

            return getFormatSpeedBS(0.0);
        }
        BigDecimal speed = new BigDecimal(size).divide(new BigDecimal(time / 1000), 10, BigDecimal.ROUND_HALF_DOWN);
        return getFormatSpeedBS(speed.doubleValue());
    }

    public static String getFormatSizeByte(long size) {

        if (size < KB) { // KB
            return convert(size) + " Byte";
        } else if (size < MB) { // MB
            return convert(size / KB) + " KB";
        } else if (size < GB) { // GB
            return convert(size / MB) + " MB";
        } else { // GB
            return convert(size / GB) + " GB";
        }
    }

    public static String getFormatSizeByteForCeil(long size) {

        if (size < KB) { // KB
            return Math.ceil(size) + " Byte";
        } else if (size < MB) { // MB
            return Math.ceil(size / KB) + " KB";
        } else if (size < GB) { // GB
            return Math.ceil(size / MB) + " MB";
        } else { // GB
            return Math.ceil(size / GB) + " GB";
        }
    }

    static double convert(double value) {
        long l1 = Math.round(value * 100);
        double ret = l1 / 100.0;
        return ret;
    }

    public static String getFormatDate(Date date) {
        if (date == null) {
            return "N/A";
        } else {
            return simpleDateFormat.format(date);
        }
    }

    public static Date getDate(String datetime) {
        if (datetime == null || datetime.equals("")) {
            return null;
        } else {
            try {
                return simpleDateFormat.parse(datetime);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static Date getDate(Date now) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);// date 换成已经已知的Date对象
        cal.add(Calendar.HOUR_OF_DAY, +0);// before 8 hour
        return cal.getTime();
    }

    public static Date parseDate(String dateTime) {
        if (dateTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return sdf.parse(dateTime);
            } catch (ParseException e) {
                //myLogger.error(e.getMessage(), e);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @param startStr 例  2017-11-28
     * @param endStr   例 2017-12-04
     * @return [2017-11-28, 2017-11-29, 2017-11-30, 2017-12-01, 2017-12-02, 2017-12-03,2017-12-04]
     */
    public static ArrayList<String> getDaySequenceWith(String startStr, String endStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        Date endDate;
        ArrayList<String> result = new ArrayList<String>();
        try {
            startDate = sdf.parse(startStr);
            endDate = sdf.parse(endStr);
            long lastTime = startDate.getTime();
            result.add(sdf.format(startDate));
            int dayNum = (int) ((endDate.getTime() - startDate.getTime()) / oneD);
            for (int i = 0; i < dayNum; i++) {
                startDate.setTime(lastTime + oneD);
                result.add(sdf.format(startDate));
                lastTime = startDate.getTime();
            }
            return result;
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /***
     * 给定开始年月和结束年月，返回中间的月份
     * @param startStr 例 ：2017-01
     * @param endStr  例：2017-11
     * @return [2017-01, 2017-02, 2017-03, 2017-04, 2017-05, 2017-06, 2017-07, 2017-08, 2017-09, 2017-10, 2017-11]
     */
    public static ArrayList<String> getMonthSequenceWith(String startStr, String endStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date startDate;
        Date endDate;
        ArrayList<String> result = new ArrayList<String>();
        try {
            Calendar cal = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal2.clear();
            cal.clear();
            startDate = sdf.parse(startStr);
            cal.setTime(startDate);
            endDate = sdf.parse(endStr);
            cal2.setTime(endDate);
            cal2.add(Calendar.MONTH, 1);

            while (cal.before(cal2)) {
                result.add(sdf.format(cal.getTime()));
                cal.add(Calendar.MONTH, 1);
            }
            return result;
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /***
     * 给定开始年和结束年，返回中间的年份
     * @param startStr 例： 2017
     * @param endStr 例：2018
     * @return [2017, 2018]
     */
    public static ArrayList<String> getYearSequenceWith(String startStr, String endStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date startDate;
        Date endDate;
        ArrayList<String> result = new ArrayList<String>();
        try {
            Calendar cal = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal2.clear();
            cal.clear();
            startDate = sdf.parse(startStr);
            cal.setTime(startDate);
            endDate = sdf.parse(endStr);
            cal2.setTime(endDate);
            cal2.add(Calendar.YEAR, 1);
            while (cal.before(cal2)) {
                result.add(sdf.format(cal.getTime()));
                cal.add(Calendar.YEAR, 1);
            }
            return result;
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static ArrayList<String> getHoursLables() {
        ArrayList<String> hours = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        if (currentHour <= 7) {

            currentHour = 7;
        }
        for (int i = 0; i <= currentHour; i++) {

            hours.add(i + "");
        }
        return hours;
    }

    public static String getDateBefore(String pattern, int type, int month) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar c = Calendar.getInstance();
        c.roll(type, month);
        return sdf.format(c.getTime());
    }

    public static Date parseDateByPattern(String pattern, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getDateByPattern(String pattern) {

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar c = Calendar.getInstance();
        return sdf.format(c.getTime());
    }


    public static long getValue(String desc, int position, boolean countSize) {

        Pattern pattern = Pattern.compile("(\\[[^\\]]*\\])");
        Matcher m = pattern.matcher(desc);
        List<String> lists = Lists.newArrayList();
        while (m.find()) {

            int count = m.groupCount();
            for (int i = 0; i < count; i++) {
                lists.add(m.group(i));
            }
        }
        String result = null;
        if (lists.size() >= position) {

            String value = null;
            value = lists.get(position - 1);
            if (countSize) {

                result = value.replace("[", "").replace("]", "").replace("Byte", "").trim();
            } else { //计算时间

                result = generateTime(value, 0);
            }
        }
        try {

            return Long.valueOf(result);
        } catch (Throwable t) {

            return 0;
        }
    }

    /**
     * 解析1 d 2 h 3 min 40 s 23 ms
     *
     * @param v
     * @param time
     * @return
     */
    public static String generateTime(String v, long time) {

        if (ObjectUtils.isEmpty(v)) {

            logger.error("message to be parsed is null {}", v);
            return "0";
        }
        v = v.replace("[", "").replace("]", "");
        if (v.contains("d")) {

            String[] strs = v.split("d");
            long msTime = Integer.valueOf(strs[0].trim()) * oneD + time;
            return generateTime(strs[1], msTime);
        } else if (v.contains("h")) {

            String[] strs = v.split("h");
            long msTime = Integer.valueOf(strs[0].trim()) * oneH + time;
            return generateTime(strs[1], msTime);
        } else if (v.contains("min")) {

            String[] strs = v.split("min");
            long msTime = Integer.valueOf(strs[0].trim()) * oneMin + time;
            return generateTime(strs[1], msTime);
        } else if (v.contains(" s ")) {

            String[] strs = v.split(" s ");
            long msTime = Integer.valueOf(strs[0].trim()) * oneS + time;
            return generateTime(strs[1], msTime);
        } else {

            String[] strs = v.split("ms");
            return (Long.valueOf(strs[0].trim()) + time) + "";
        }
    }


    public static double formatePecent(double percent) {

        return Double.valueOf(numberFormat.format(percent));
    }

    public static void main(String[] args) {

        long count = 49052;
        long total = 52009;
        System.err.println((count * 100 / total));
    }
}
