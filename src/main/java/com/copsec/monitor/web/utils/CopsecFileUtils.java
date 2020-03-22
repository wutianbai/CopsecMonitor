package com.copsec.monitor.web.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.utils.MD5Utils.MD5Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ObjectUtils;

public class CopsecFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(CopsecFileUtils.class);

    public static boolean isExist(String filePath) {

        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return true;
        }
        return false;
    }

    public static void mkFile(String filePath) {

        File file = new File(filePath);
        try {
            File parent = file.getParentFile();
            if (!parent.exists()) {

                parent.mkdirs();
            }
            file.createNewFile();
        } catch (IOException e) {

            logger.error(e.getMessage(), e);
        }
    }

    public static void mkDirs(String filePath) {

        File file = new File(filePath);

        File parent = file.getParentFile();
        if (!parent.exists()) {

            parent.mkdirs();
        }
        file.mkdir();
    }

    public static void delete(String path) {

        File file = new File(path);
        if (file.exists()) {

            file.delete();
        }
    }

    public static List<File> getAllFiles(String path, final List<File> list) {

        File file = new File(path);
        if (file.isDirectory()) {

            List<File> files = Arrays.asList(file.listFiles());
            files.stream().forEach(item -> {

                if (file.isFile() && file.length() > 0) {

                    list.add(item);
                } else {

                    getAllFiles(item.getAbsolutePath(), list);
                }
            });
        } else {

            list.add(file);
        }

        return list;
    }

    public static File getFile(String rootPath, String uuid) {

        File _file = null;
        List<File> files = Lists.newArrayList();
        files = CopsecFileUtils.getAllFiles(rootPath, files);
        for (File file : files) {

            String _uuid = MD5Util.encryptMD5(file.getAbsolutePath().substring(rootPath.length()));
            if (_uuid.equals(uuid)) {

                _file = file;
                break;
            }
        }

        return _file;
    }
}
