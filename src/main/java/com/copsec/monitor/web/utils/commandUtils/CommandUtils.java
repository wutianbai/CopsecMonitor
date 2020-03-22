package com.copsec.monitor.web.utils.commandUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandUtils {

	private static final Logger logger = LoggerFactory.getLogger(CommandUtils.class);

	public static String getVirusVersion4Clamav(){

		String str = null;
		String cmd = "clamscan -V";
		String[] commands = { "/bin/sh", "-c", cmd };
		Runtime runtime = Runtime.getRuntime();
		Process process;
		try {
			process = runtime.exec(commands);
			process.waitFor();
			BufferedReader bufferreader = new BufferedReader(new InputStreamReader(
					(process.exitValue() == 0) ? process.getInputStream() : process.getErrorStream()));
			String info = "";
			while (null != (info = bufferreader.readLine())) {
				if (info.startsWith("ClamAV")) {
					String[] s = info.replaceFirst("ClamAV", "").split("/");
					String[] ss = s[2].trim().split("\\s+");
					String newDate = ss[0] + " " + ss[1] + " " + ss[2] + " " + ss[3] + " CST " + ss[4];
					SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
					String d = "";
					try {
						Date date = sdf1.parse(newDate);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						d = sdf.format(date);
					} catch (ParseException e) {

						logger.error(e.getMessage(),e);
					}
					str = "引擎版本:" + s[0] + ";病毒库版本:" + s[1] + " " + d;
				}
			}
		} catch (Exception e) {

			logger.error(e.getMessage(),e);
		}

		return str;
	}
}
