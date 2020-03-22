package com.copsec.monitor.web.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.copsec.monitor.web.config.Resources;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

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

    public final static boolean isJSONValid2(String jsonInString ) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
