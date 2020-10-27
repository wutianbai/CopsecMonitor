package com.copsec.monitor.web.fileReaders;

import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.utils.CopsecFileUtils;
import com.copsec.monitor.web.utils.CopsecReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BaseFileReader<T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseFileReader.class);

    /**
     * 读取文件内容
     *
     * @param filePath
     * @return
     */
    public ArrayList<String> readContent(String filePath) throws CopsecException {

        if (!CopsecFileUtils.isExist(filePath)) {
            logger.warn("file {} do not exists", filePath);
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"))) {

            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!ObjectUtils.isEmpty(line) && !line.startsWith("#")) {
                    list.add(line);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new CopsecException(e.getMessage());
        }
        return list;
    }

    public void getData(String filePath) throws CopsecException {


    }

    public void getData(String filePath, final NetworkType type) throws CopsecException {


    }

    public void getDataByInfos(String info){


	}

    public void writeDate(List<T> list, String filePath) throws CopsecException {
        if (!CopsecFileUtils.isExist(filePath)) {
            CopsecFileUtils.mkFile(filePath);
        }
        BufferedWriter writer = null;
        try {
            //对原文件内容进行拷贝
            File orig = new File(filePath);
            File newFile = new File(filePath + ".bak");
            if (newFile.exists()) {
                newFile.delete();
                newFile.createNewFile();
            }
            FileCopyUtils.copy(new FileInputStream(orig), new FileOutputStream(newFile));

            writer = new BufferedWriter(new FileWriter(orig));
            for (T t : list) {
                String tmp = (String) CopsecReflectionUtils.getInvoke(t, "toString");
                writer.write(tmp + "\r\n");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new CopsecException(e.getMessage());
        } finally {
            if (!ObjectUtils.isEmpty(writer)) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
