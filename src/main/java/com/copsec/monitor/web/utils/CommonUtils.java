package com.copsec.monitor.web.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.copsec.monitor.web.config.Resources;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class CommonUtils {

    public static String getUpgradeFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            if (file.listFiles().length == 0) {
                return "";
            }
            String tmp = "";
            for (File item : file.listFiles()) {
                if (item.isFile() && !item.getName().equalsIgnoreCase("copAap_update.sh")) {
                    tmp += item.getName() + Resources.SPLITER;
                }
            }
            return tmp;
        }
        return null;
    }

    public static boolean exists(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.length() > 0) {
            return true;
        }
        return false;
    }

    public static void deleteFiles(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
        } else if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.exists()) {
                    f.delete();
                }
            }
            file.delete();
        }
    }

    /**
     * 暴力解析:Alibaba fastjson
     *
     * @param test
     * @return
     */
    public final static boolean isJSONValid(String test) {
        try {
            JSONObject.parseObject(test);
        } catch (JSONException ex) {
            try {
                JSONObject.parseArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String numberChar = "0123456789";

    public static String generateString(int length) //参数为返回随机数的长度
    {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(allChar.charAt(random.nextInt(allChar.length())));
        }
        return sb.toString();
    }

    /**
     * 使用guava工具类来取List集合的差集--专业轮子谷歌造
     *
     * @param big   大集合
     * @param small 小集合
     * @return 两个集合的差集
     */
    private static List<String> getDifferenceSetByGuava(List<String> big, List<String> small) {
        Set<String> differenceSet = Sets.difference(Sets.newHashSet(big), Sets.newHashSet(small));
        return Lists.newArrayList(differenceSet);
    }

    /**
     * 自己实现取List集合的差集--自制轮子大师兄造
     *
     * @param big   大集合
     * @param small 小集合
     * @return 两个集合的差集
     */
    private static List<String> getDifferenceSetByMyself(List<String> big, List<String> small) {
        Set<String> sameString = Sets.newHashSet();
        for (String s : small) {
            sameString.add(s);
        }
        List<String> result = Lists.newArrayList();
        for (String s : big) {
            if (sameString.add(s)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * 自己实现取Map集合的差集--站在巨人的肩膀上造轮子
     *
     * @param bigMap   大集合
     * @param smallMap 小集合
     * @return 两个集合的差集
     */
    private static Map<String, String> getDifferenceSetByGuava(Map<String, String> bigMap, Map<String, String> smallMap) {
        Set<String> bigMapKey = bigMap.keySet();
        Set<String> smallMapKey = smallMap.keySet();
        Set<String> differenceSet = Sets.difference(bigMapKey, smallMapKey);
        Map<String, String> result = Maps.newConcurrentMap();
        for (String key : differenceSet) {
            result.put(key, bigMap.get(key));
        }
        return result;
    }
}
