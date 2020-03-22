package com.copsec.monitor.web.commons;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.copsec.monitor.web.beans.UploadFileBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMap {

	private static final Logger logger = LoggerFactory.getLogger(FileMap.class);
	private static volatile FileMap fileMap;
	private FileMap(){}
	private static Map<String, Integer> map = new HashMap<String,Integer>();

	public static synchronized FileMap getInstances(){

		if(null == fileMap){

			synchronized (FileMap.class) {

				if(fileMap == null){

					fileMap = new FileMap();
				}
			}
		}
		return fileMap;
	}

	public synchronized void add(UploadFileBean bean){

		if(!map.containsKey(bean.getFileId())){

			map.put(bean.getFileId(), Integer.valueOf(bean.getChunks()));
		}
	}

	public synchronized void remove(UploadFileBean bean){

		if(map.containsKey(bean.getFileId())){

			map.remove(bean);
			logger.debug("remove bean from map succcess {} ",Thread.currentThread().getName());
		}
	}

	public synchronized boolean isAllUploaded(UploadFileBean bean,String parentDir){

		File file = new File(parentDir);

		if(map.containsKey(bean.getFileId())){

			synchronized(map){

				if(map.containsKey(bean.getFileId())){

					Integer tmp = map.get(bean.getFileId());
					if(file.isDirectory()){

						File[] files = file.listFiles();
						if(tmp.intValue()== files.length){

							map.remove(bean.getFileId(),tmp);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
