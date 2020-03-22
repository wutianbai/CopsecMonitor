package com.copsec.monitor.web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Objects;

import org.bson.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ObjectUtils;

public class PositionUtils {

	private static final Logger logger = LoggerFactory.getLogger(PositionUtils.class);

	public static void writePosition(String position,String filePath){

		File file = new File(filePath);
		if(!file.exists()){

			try {
				file.createNewFile();
			}
			catch (IOException e) {

				logger.error(e.getMessage(),e);
			}
		}

		if(ObjectUtils.isEmpty(position)){

			return ;
		}

		FileLock lock = null;
		FileChannel channel = null;
		FileOutputStream fileOutputStream = null;
		try{

			for(;;){

				fileOutputStream = new FileOutputStream(file,false);
				channel = fileOutputStream.getChannel();
				lock = channel.tryLock();
				if(lock != null){

					break;
				}
				Thread.sleep(2000);
			}
			ByteBuffer byteBuffer = ByteBuffer.wrap(position.getBytes());
			byteBuffer.put(position.getBytes());
			byteBuffer.flip();
			channel.write(byteBuffer);
			fileOutputStream.flush();
		}
		catch (FileNotFoundException e) {

			logger.warn("file not found in path {}",filePath);
			try {
				new File(filePath).createNewFile();
			}
			catch (IOException e1) {

				logger.error(e1.getMessage(),e1);
			}
		}
		catch (IOException e) {

			logger.error(e.getMessage(),e);
		}
		catch (InterruptedException e) {

			logger.error(e.getMessage(),e);
		}
		finally{

			if(lock != null){

				try {

					lock.release();
				}
				catch (IOException e) {

					logger.error(e.getMessage(),e);
				}
			}
			if(channel != null){

				try {
					channel.close();
				}
				catch (IOException e) {

					logger.error(e.getMessage(),e);
				}
			}
			if(fileOutputStream != null){

				try {
					fileOutputStream.close();
				}
				catch (IOException e) {

					logger.error(e.getMessage(),e);
				}
			}
		}
	}

	public static String readPosition(String filePath){

		FileLock lock = null;
		FileChannel channel = null;
		try{

			for(;;){

				channel = new RandomAccessFile(new File(filePath),"rw").getChannel();
				lock = channel.tryLock();
				if(lock != null){

					break;
				}
				Thread.sleep(2000);
			}

			ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
			StringBuffer sb = new StringBuffer();
			while(channel.read(byteBuffer) != -1 ){

				sb.append(new String(byteBuffer.array()));
			}

			if(sb.length() > 0){

				return sb.toString().trim();
			}
			return null;
		}
		catch (FileNotFoundException e) {

			logger.warn("file not fount in path {},will be created",filePath);
			try {
				new File(filePath).createNewFile();
			}
			catch (IOException e1) {

				logger.error(e1.getMessage(),e1);
			}
			return null;
		}
		catch (IOException e) {

			logger.error(e.getMessage(),e);
		}
		catch (InterruptedException e) {

			logger.error(e.getMessage(),e);
		}
		finally{

			if(lock != null){

				try {
					lock.release();
				}
				catch (IOException e) {

					logger.error(e.getMessage(),e);
				}
			}

			if(channel != null){

				try {
					channel.close();
				}
				catch (IOException e) {

					logger.error(e.getMessage(),e);
				}
			}
		}
		return null;
	}
}
