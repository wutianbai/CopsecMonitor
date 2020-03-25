package com.copsec.monitor.web.utils.commandUtils;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Getter
@Setter
public class CommandProcessUtils {
	private static final Logger logger = LoggerFactory.getLogger(CommandProcessUtils.class);
	private static CommandProcessUtils util = new CommandProcessUtils();
	private static double cpuCount = 0.0;
	private static double memoryCount = 0.0;
	private static long virtualMemoryCount = 0;
	private static long physicalMemoryCount = 0;


	public static CommandProcessUtils getUtil() {
		cpuCount = 0.0;
		memoryCount = 0.0;
		virtualMemoryCount = 0;
		physicalMemoryCount = 0;
		if (util == null) {
			util = new CommandProcessUtils();
		}
		return util;
	}

	@SuppressWarnings("unused")
	public static void stopProcess(String pid) throws IOException {
		logger.debug("CommandProcessUtil  stopProcess");
		logger.debug("pid:" + pid);
		String[] commands = { "/bin/sh", "-c", "kill -9 " + pid };
		try {
			Process p = Runtime.getRuntime().exec(commands);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				;
			}
		} catch (IOException e) {
			throw e;
		}
	}

	public synchronized String pingTesting(String ip) throws IOException {
		logger.debug("CommandProcessUtil  pingTesting");
		logger.debug("ip:" + ip);
		String[] commands = { "/bin/sh", "-c", "ping -c 5 " + ip + " 2>&1" };
		try {
			Process p = Runtime.getRuntime().exec(commands);
			BufferedReader bufferreader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String buff = null;
			StringBuilder sb = new StringBuilder();
			while (null != (buff = bufferreader.readLine())) {
				logger.debug("pingTesting message:" + buff.toString());
				sb.append(buff.toString() + "\n");
			}
			return sb.toString();
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * @Title: ipv6PingTesting @Description: Ipv6地址ping测试 @param @param
	 *         ipv6Ip @param @return @param @throws IOException @return
	 *         String @throws
	 */
	public synchronized String ipv6PingTesting(String ipv6Ip) throws IOException {
		logger.debug("CommandProcessUtil  pingTesting");
		logger.debug("ipv6Ip:" + ipv6Ip);
		String[] commands = { "/bin/sh", "-c", "ping6 -c 5 " + ipv6Ip + " 2>&1" };
		try {
			Process p = Runtime.getRuntime().exec(commands);
			BufferedReader bufferreader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String buff = null;
			StringBuilder sb = new StringBuilder();
			while (null != (buff = bufferreader.readLine())) {
				logger.debug("pingTesting message:" + buff.toString());
				sb.append(buff.toString() + "\n");
			}
			return sb.toString();
		} catch (IOException e) {
			throw e;
		}
	}

	public static String routeTracking(String ip) throws IOException {
		logger.debug("CommandProcessUtil  routeTracking");
		logger.debug("ip:" + ip);
		String[] commands = { "/bin/sh", "-c", "traceroute " + ip };
		try {
			Process p = Runtime.getRuntime().exec(commands);
			BufferedReader bufferreader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String buff = null;
			StringBuilder sb = new StringBuilder();
			while (null != (buff = bufferreader.readLine())) {
				logger.debug("routeTracking message:" + buff.toString());
				sb.append(buff.toString() + "\n");
			}
			return sb.toString();
		} catch (IOException e) {
			throw e;
		}

	}

	public String executeCommand(String commands) {
		Process process = null;
		String result = "";
		try {
			process = Runtime.getRuntime().exec(commands);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = input.readLine()) != null) {
				result = line;
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
