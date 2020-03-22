package com.copsec.monitor.web.utils.zipUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.copsec.monitor.web.exception.CopsecException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopsecZipUtils {

	private static final Logger logger = LoggerFactory.getLogger(CopsecZipUtils.class);
	public static String toZip(String src, String passwd,String backupFileName) throws CopsecException {
		File srcFile = new File(src);

		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // 压缩方式
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); // 压缩级别
		if (passwd != null) {
			parameters.setEncryptFiles(true);
			parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD); // 加密方式
			parameters.setPassword(passwd.toCharArray());
		}
		try {
			ZipFile zipFile = new ZipFile(src + backupFileName);

			if (srcFile.isDirectory()) {

				zipFile.addFolder(srcFile, parameters);
			} else {
				zipFile.addFile(srcFile, parameters);

			}

		} catch (ZipException e) {

			throw new CopsecException(e.getMessage());
		}
		return null;
	}


	public static String writeZipFile(String path,String zipPassword) throws Exception{

		File file = new File(path);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

		// Set password
		parameters.setEncryptFiles(true);
		parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
		parameters.setPassword(zipPassword);

		ZipFile zipFile = new ZipFile(path + ".zip");
		zipFile.addFile(file,parameters);
		return path + ".zip";
	}

	public static ZipOutputStream writeZipFile2(String path,String zipPassword ,HttpServletResponse response) throws Exception{
		byte[] buf = new byte[1024];
		int len;
		ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
		File file = new File(path);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

		// Set password
		parameters.setEncryptFiles(true);
		parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
		parameters.setPassword(zipPassword);

		zos.putNextEntry(file, parameters);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		while ((len = bis.read(buf)) > 0) {
			zos.write(buf, 0, len);
		}
		bis.close();
		zos.closeEntry();
		return zos;
	}

}
