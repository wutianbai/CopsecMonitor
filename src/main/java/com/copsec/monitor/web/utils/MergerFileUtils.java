package com.copsec.monitor.web.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import ch.qos.logback.core.util.FileUtil;
import com.copsec.monitor.web.beans.UploadFileBean;
import com.copsec.monitor.web.commons.CopsecResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.FileCopyUtils;

public class MergerFileUtils {

	private static final Logger logger = LoggerFactory.getLogger(MergerFileUtils.class);

	private static UploadFileBean bean;

	public static CopsecResult mergerFile(UploadFileBean cmdbean,String uploadPath){

		bean = cmdbean;
		FileOutputStream outputStream = null;
		SequenceInputStream sequenceInputStream = null;

		try
		{

			sequenceInputStream = new SequenceInputStream(readFiles(uploadPath));

			outputStream = new FileOutputStream(uploadPath+File.separator+bean.getFileName());

			copyFile(sequenceInputStream, outputStream);

			if(logger.isDebugEnabled()){

				logger.debug("merger file success");
			}
			return CopsecResult.success();
		}catch (Throwable e){

			logger.error(e.getMessage(),e);

			return CopsecResult.failed();

		}finally{


			if(null != outputStream){

				try{
					outputStream.close();
				}catch(Throwable e){

					logger.error(e.getMessage(),e);
				}
			}

			if(null != sequenceInputStream){

				try{

					sequenceInputStream.close();
				}catch(Throwable e){

					logger.error(e.getMessage(),e);
				}
			}
		}
	}

	private static void copyFile(InputStream inputStream, FileOutputStream outputStream) throws IOException {

		FileCopyUtils.copy(inputStream,
				outputStream);
	}

	private static Enumeration<FileInputStream> readFiles(String uploadPath) throws IOException{


		File parentDir = new File(uploadPath+ File.separator+bean.getFileId());
		Vector<FileInputStream> v = new Vector<FileInputStream>();
		Enumeration<FileInputStream> e = null;
		if(parentDir.isDirectory()){

			logger.debug("start reading file chunks");
			while(parentDir.list().length != Integer.valueOf(bean.getChunks())){

				try {

					if(logger.isDebugEnabled()){

						logger.debug("waiting file to write complete....");
					}
					Thread.sleep(5000);
				} catch (InterruptedException e1) {

					logger.error(e1.getMessage(),e1);
				}
			}
			HashMap< String, File> map = getFileMaps(parentDir.listFiles());
			for(int i=0;i<Integer.valueOf(bean.getChunks());i++){

				String key = bean.getFileId()+"_"+i;

				File file = map.get(key);

				v.add(new FileInputStream(file));
			}
			e = v.elements();
		}
		return e;
	}

	private static HashMap< String, File> getFileMaps(File[] files){

		HashMap<String, File> map = new HashMap<>();
		for(File file:files){

			if(logger.isDebugEnabled()){

				logger.debug("file exists {} and filePath is {}",file.exists(),file.getAbsolutePath());
			}
			if(file.exists()){

				map.put(file.getName(), file);
			}
		}
		return map;
	}
}
