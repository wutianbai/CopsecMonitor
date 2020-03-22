package com.copsec.monitor.web.utils.logUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.copsec.monitor.web.utils.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.applet.Main;

import org.springframework.data.mongodb.core.aggregation.DateOperators.Timezone;

public class SnmpSyslogUtils {

	private static Logger logger = (Logger) LoggerFactory.getLogger(SnmpSyslogUtils.class);

	private final static String encoding = "UTF-8";

	private static DatagramSocket datagramSocket = null;

	private final static String separator = "\r\n";

	public static void sendLog(String message,String logHost,int logPort) {

		try {
			if (datagramSocket == null) {
				try {
					datagramSocket = new DatagramSocket();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					closeSocket();
					return;
				}
			}
			StringBuffer sb = new StringBuffer();
			sb.append(message);

			byte[] bytes = null;
			try {
				if(logger.isDebugEnabled()){

					logger.debug("use {} and {} send message is {}",logHost,logPort,sb.toString());
				}
				bytes = sb.toString().getBytes(encoding);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
				return;
			}
			try {

				datagramSocket.send(new DatagramPacket(bytes, bytes.length,
						InetAddress.getByName(logHost),logPort));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return;
			}
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	private static void closeSocket() {
		if (null != datagramSocket) {
			datagramSocket.close();
			datagramSocket = null;
		}
	}

}
