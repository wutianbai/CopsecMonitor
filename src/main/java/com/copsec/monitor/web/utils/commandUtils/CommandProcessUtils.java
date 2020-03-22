package com.copsec.monitor.web.utils.commandUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.copsec.monitor.web.beans.monitor.AddressInfoBean;
import com.copsec.monitor.web.beans.monitor.CpuMemeryStateBean;
import com.copsec.monitor.web.beans.monitor.DeviceStateBean;
import com.copsec.monitor.web.beans.monitor.DiskStateBean;
import com.copsec.monitor.web.beans.monitor.ServiceStateBean;
import com.copsec.monitor.web.utils.FormatUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public List<DiskStateBean> getDiskProcessList() {
		logger.debug("CommandProcessUtil getDiskProcessList");
		List<DiskStateBean> beanList = new ArrayList<DiskStateBean>();
		String cmd = "df -h";
		String[] commands = { "/bin/sh", "-c", cmd };
		logger.debug("CommandProcessUtil cmd:" + cmd);
		Runtime runtime = Runtime.getRuntime();
		Process process;
		try {
			process = runtime.exec(commands);
			process.waitFor();
			BufferedReader bufferreader = new BufferedReader(new InputStreamReader(
					(process.exitValue() == 0) ? process.getInputStream() : process.getErrorStream()));
			String buff;
			while (null != (buff = bufferreader.readLine())) {
				logger.debug("CommandProcessUtil>> " + buff);
				// Filesystem Size Used Avail Use% Mounted on
				if (buff.startsWith("Filesystem")) {
					continue;
				}
				String[] ary = buff.trim().split("\\s+");
				if (ary.length < 6) {
					String nextLine = bufferreader.readLine();
					String[] ary2 = nextLine.trim().split("\\s+");
					DiskStateBean bean = new DiskStateBean(ary[0], ary2[0], ary2[1], ary2[2], ary2[3], ary2[4]);
					beanList.add(bean);
				} else {
					DiskStateBean bean = new DiskStateBean(ary[0], ary[1], ary[2], ary[3], ary[4], ary[5]);
					beanList.add(bean);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return beanList;
	}

	public List<ServiceStateBean> parseProcessList() {
		logger.debug("CommandProcessUtil parseProcessList");
		List<ServiceStateBean> beanList = new ArrayList<ServiceStateBean>();
		String javaCmd = "ps aux | grep /jdk18/bin/java | awk '{print($2\" \"$3\" \"$4\" \"$5\" \"$6\" \"$11\" \"$12);}'";
		String mongoCmd = "ps aux | grep /bin/mongod | awk '{print($2\" \"$3\" \"$4\" \"$5\" \"$6\" \" $11\" \"$12\" \"$13);}'";
		String serverCmd = "ps aux | grep webservd | awk '{print($2\" \"$3\" \"$4\" \"$5\" \"$6\" \" $11\" \"$12\" \"$13);}'";
		try {
			List<ServiceStateBean> beanList1 = getProcessList(javaCmd);
			for (ServiceStateBean bean1 : beanList1) {
				beanList.add(bean1);
			}
			List<ServiceStateBean> beanList2 = getProcessList(mongoCmd);
			for (ServiceStateBean bean2 : beanList2) {
				beanList.add(bean2);
			}
			List<ServiceStateBean> beanList3 = getProcessList(serverCmd);
			for (int i = 0; i < beanList3.size(); i += 3) {
				String name = beanList3.get(i).getServerName();
				beanList3.get(i).setServerName(name + "-进程1");
				beanList3.get(i + 1).setServerName(name + "-进程2");
				beanList3.get(i + 2).setServerName(name + "-进程3");
				beanList.add(beanList3.get(i));
				beanList.add(beanList3.get(i + 1));
				beanList.add(beanList3.get(i + 2));
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return beanList;
	}

	public List<ServiceStateBean> getProcessList(String cmd) {
		logger.debug("CommandProcessUtil getProcessList");
		logger.debug("cmd:" + cmd);
		List<ServiceStateBean> beanList = new ArrayList<ServiceStateBean>();
		String[] commands = { "/bin/sh", "-c", cmd };
		Runtime runtime = Runtime.getRuntime();
		Process process;
		try {
			process = runtime.exec(commands);
			process.waitFor();
			BufferedReader bufferreader = new BufferedReader(new InputStreamReader(
					(process.exitValue() == 0) ? process.getInputStream() : process.getErrorStream()));
			String buff;
			while (null != (buff = bufferreader.readLine())) {
				logger.debug("CommandProcessUtil>> " + buff);
				if (buff.contains("java") && buff.contains("-Dname")) {
					logger.debug("CommandProcessUtil  java>>");
					beanList.add(parseBuffer(buff));
				}

				if (buff.contains("webservd") && buff.contains("-d")) {
					logger.debug("CommandProcessUtil  webservd>>");
					beanList.add(parseBuffer(buff));
				}

				if (buff.contains("mongod") && buff.contains("-f")) {
					logger.debug("CommandProcessUtil  mongod>>");
					beanList.add(parseBuffer(buff));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return beanList;
	}

	public ServiceStateBean parseBuffer(String buff) {
		logger.debug("parseBuffer " + buff);
		ServiceStateBean serviceStateBean = new ServiceStateBean();
		// String
		// str="9192 0.0 2.5 6100440 418548 /usr/is/jdk18/bin/java
		// -Dname=FileReceive";
		String[] ary = buff.trim().split("\\s+");
		// 判断命令 截取服务名
		String serverName = "";
		if (buff.contains("java") && buff.contains("-Dname")) {
			logger.debug("parse  java>>");
			int index = ary[6].indexOf("=");
			serverName = ary[6].substring(index + 1, ary[6].length());
			logger.debug("serverName:" + serverName);
			if (serverName.equalsIgnoreCase("FileReceive")) {
				serviceStateBean.setServerName("文件传输接收服务");
			} else if (serverName.equalsIgnoreCase("FileSend")) {
				serviceStateBean.setServerName("文件传输发送服务");
			} else if (serverName.equalsIgnoreCase("TcpProxy")) {
				serviceStateBean.setServerName("TCP代理服务");
			} else if (serverName.equalsIgnoreCase("LogProxy")) {
				serviceStateBean.setServerName("日志代理服务");
			} else if (serverName.equalsIgnoreCase("SystemManage")) {
				serviceStateBean.setServerName("系统管理服务");
			} else if (serverName.equalsIgnoreCase("ISMamangeSystem")) {
				serviceStateBean.setServerName("管理平台服务");
			} else if (serverName.equalsIgnoreCase("LogMonitor")) {
				serviceStateBean.setServerName("日志监控服务");
			} else if (serverName.equalsIgnoreCase("DBSyncTaskManage")) {
				serviceStateBean.setServerName("数据库同步任务管理服务");
			} else if (serverName.equalsIgnoreCase("GetDBInfoWSServer")) {
				serviceStateBean.setServerName("数据库信息获取服务");
			} else if (serverName.equalsIgnoreCase("FileSyncTaskManage")) {
				serviceStateBean.setServerName("文件同步任务管理服务");
			} else if (serverName.equalsIgnoreCase("HttpProxy")) {
				serviceStateBean.setServerName("HTTP代理服务");
			} else if (serverName.equalsIgnoreCase("HttpProxyTransfer")) {
				serviceStateBean.setServerName("HTTP通用转发服务");
			} else if (serverName.equalsIgnoreCase("NettyWebServiceProxy")) {
				serviceStateBean.setServerName("WebService代理服务");
			} else if (serverName.equalsIgnoreCase("NettyWebServiceProxyTransfer")) {
				serviceStateBean.setServerName("WebService通用转发服务");
			} else if (serverName.equalsIgnoreCase("ISTcpTransfer")) {
				serviceStateBean.setServerName("TCP通用转发服务");
			} else if (serverName.equalsIgnoreCase("UdpProxyLogConvert")) {
				serviceStateBean.setServerName("UDP代理日志服务");
			} else if (serverName.equalsIgnoreCase("UdpProxyConfigConvert")) {
				serviceStateBean.setServerName("UDP代理配置服务");
			} else if (serverName.equalsIgnoreCase("SmtpProxy")) {
				serviceStateBean.setServerName("SMTP代理服务");
			} else if (serverName.equalsIgnoreCase("Pop3Proxy")) {
				serviceStateBean.setServerName("POP3代理服务");
			} else if (serverName.equalsIgnoreCase("FtpProxy")) {
				serviceStateBean.setServerName("FTP代理服务");
			} else if (serverName.equalsIgnoreCase("PFTServer")) {
				serviceStateBean.setServerName("个人文件传输服务");
			} else if (serverName.equalsIgnoreCase("PersonalFileTransferDownFileDistribute")) {
				serviceStateBean.setServerName("个人文件下载分发服务");
			} else if (serverName.equalsIgnoreCase("PFTFileSendTaskNoticeWebServiceClient")) {
				serviceStateBean.setServerName("个人文件通知发送服务");
			} else if (serverName.equalsIgnoreCase("PFTFileSendTaskNoticeWebServiceServer")) {
				serviceStateBean.setServerName("个人文件通知接收服务");
			} else if (serverName.equalsIgnoreCase("PFTUserInfoSyncClient")) {
				serviceStateBean.setServerName("个人文件用户同步发送服务");
			} else if (serverName.equalsIgnoreCase("PFTUserInfoSyncServer")) {
				serviceStateBean.setServerName("个人文件用户同步接收服务");
			} else if (serverName.equalsIgnoreCase("TcpLoadBalancingLogConvert")) {
				serviceStateBean.setServerName("TCP负载均衡日志服务");
			} else if (serverName.equalsIgnoreCase("TcpLoadBalancingConfigConvert")) {
				serviceStateBean.setServerName("TCP负载均衡配置服务");
			} else if (serverName.equalsIgnoreCase("HotStandby")) {
				serviceStateBean.setServerName("双机热备服务");
			} else if (serverName.equalsIgnoreCase("ConfigManage")) {
				serviceStateBean.setServerName("配置同步服务");
			} else {
				serviceStateBean.setServerName(serverName);
			}
		} else if (buff.contains("webservd") && buff.contains("-d")) {
			logger.debug("parse  webservd>>");
			int begin = ary[7].indexOf("web70/") + "web70".length();
			int end = ary[7].indexOf("/config");
			serverName = ary[7].substring(begin + 1, end);
			logger.debug("serverName:" + serverName);
			if (serverName.equalsIgnoreCase("admin-server")) {
				serviceStateBean.setServerName("web容器管理服务");
			} else if (serverName.equalsIgnoreCase("https-HttpProxy-Second")) {
				serviceStateBean.setServerName("Http代理服务");
			} else if (serverName.equalsIgnoreCase("https-PlatformManageSystem")) {
				serviceStateBean.setServerName("管理平台服务");
			} else {
				serviceStateBean.setServerName(serverName);
			}
		} else if (buff.contains("mongod") && buff.contains("-f")) {
			logger.debug("CommandProcessUtil  mongod>>");
			serverName = ary[7];
			logger.debug("serverName:" + serverName);
			if (serverName.contains("logdb.mongod.conf")) {
				serviceStateBean.setServerName("日志数据库服务");
			} else if (serverName.contains("mongod.conf")) {
				serviceStateBean.setServerName("系统数据库服务");
			} else {
				serviceStateBean.setServerName(serverName);
			}
		}
		cpuCount += Double.parseDouble(ary[1]);
		memoryCount += Double.parseDouble(ary[2]);
		virtualMemoryCount += Long.parseLong(ary[3]) * 1024;
		physicalMemoryCount += Long.parseLong(ary[4]) * 1024;
		serviceStateBean.setProcessId(ary[0]);
		serviceStateBean.setCupOccupancy(ary[1] + "%");
		serviceStateBean.setMemoryOccupancy(ary[2] + "%");
		serviceStateBean.setVirtualMemory(FormatUtils.getFormatSizeByte(Long.parseLong(ary[3]) * 1024));
		serviceStateBean.setPhysicalMemory(FormatUtils.getFormatSizeByte(Long.parseLong(ary[4]) * 1024));
		logger.debug("serviceStateBean:" + serviceStateBean);
		return serviceStateBean;
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

	/**
	 * 获取设备状态信息
	 *
	 * @param list
	 * @return
	 *
	 */
	private List<DeviceStateBean> getDevice(List<DeviceStateBean> list) {
		for (DeviceStateBean device : list) {
			String cmd = "ethtool " + device.getDecviceName();
			String[] commands = { "/bin/sh", "-c", cmd };
			Runtime runtime = Runtime.getRuntime();
			Process process;
			String buff;

			try {
				process = runtime.exec(commands);
				process.waitFor();
				BufferedReader bufferreader = new BufferedReader(new InputStreamReader(
						(process.exitValue() == 0) ? process.getInputStream() : process.getErrorStream()));
				// BufferedReader bufferreader = new BufferedReader(new
				// FileReader("D://bb.txt"));
				boolean bool = false;
				while (null != (buff = bufferreader.readLine())) {
					String[] arry = buff.split(":");
					if (arry[0].contains(device.getDecviceName())) {
						bool = true;
					}

					if (bool) {
						if (arry[0].contains("Speed")) {
							device.setSpeed(arry[1].trim());
						}

						if (arry[0].contains("Duplex")) {
							device.setDuplex(arry[1].trim());
						}

						if (arry[0].contains("Port")) {
							device.setMediaType(arry[1].trim());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	private DeviceStateBean getDeviceInfo(List<String> list) {
		DeviceStateBean device = new DeviceStateBean();
		boolean bool = false;
		for (String buff : list) {
			String[] arry = buff.split(" ");

			if (arry[0].contains("eth")) {
				// device = new DeviceStateBean();
				device.setDecviceName(arry[0]);

				if ("eth0".equals(arry[0])) {

					device.setDeviceType("管理口");
				} else {
					device.setDeviceType("业务口");
				}
			}
			if (buff.contains("RUNNING")) {
				bool = true;
			}
		}
		if (bool) {
			device.setLinkState("1");
			bool = false;
		} else if (!bool) {
			device.setLinkState("0");
			bool = false;
		}
		return device;
	}

	/**
	 * 获取设备状态信息
	 *
	 * @return
	 */
	public List<DeviceStateBean> getEthList(String filePath) {
		logger.debug("CommandProcessUtil getDeviceList");
		String cmd = "ifconfig -a";
		String[] commands = { "/bin/sh", "-c", cmd };
		logger.debug("CommandProcessUtil cmd:" + cmd);
		Runtime runtime = Runtime.getRuntime();
		Process process;
		List<DeviceStateBean> devlist = new ArrayList<DeviceStateBean>();
		String buff;
		try {
			process = runtime.exec(commands);
			process.waitFor();
			BufferedReader bufferreader = new BufferedReader(new InputStreamReader(
					(process.exitValue() == 0) ? process.getInputStream() : process.getErrorStream()));

			boolean bool = false;
			List<String> list = new ArrayList<String>();
			DeviceStateBean device = null;

			while (null != (buff = bufferreader.readLine())) {

				String[] arry = buff.split(" ");
				if (arry[0].contains("eth") && !bool && !arry[0].contains(":")) {
					list.add(buff);
					bool = true;
					continue;
				}

				if (bool) {
					if (arry[0].equals("")) {
						list.add(buff);
					} else {
						device = getDeviceInfo(list);
						// System.out.println("device:"+device.getDecviceName());
						devlist.add(device);
						bool = false;
						list = new ArrayList<String>();
						if (arry[0].contains("eth") && !arry[0].contains(":")) {
							list.add(buff);
							bool = true;
							continue;
						}
					}
				}
			}

			devlist = getDevice(devlist);

			Iterator<DeviceStateBean> it = devlist.iterator();

			while (it.hasNext()) {
				DeviceStateBean dev = it.next();
				System.out.println("dev:" + dev.getDecviceName());
				DeviceStateBean deviceInfo = isExistDevice(dev,filePath);

				if (deviceInfo != null) {
					// it.remove();
					// list.add(addr);

				} else {
					it.remove();
				}
			}
			logger.debug("deviceList:" + devlist);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return devlist;
	}

	/**
	 * 获取地址信息
	 *
	 * @return
	 */
	@SuppressWarnings("unused")
	public List<AddressInfoBean> getAddressList(String filePath) {
		logger.debug("CommandProcessUtil getAddressList");
		String cmd = "ifconfig -a";
		String[] commands = { "/bin/sh", "-c", cmd };
		logger.debug("CommandProcessUtil cmd:" + cmd);
		Runtime runtime = Runtime.getRuntime();
		Process process;
		List<AddressInfoBean> addrList = new ArrayList<AddressInfoBean>();
		String buff;
		try {
			process = runtime.exec(commands);
			process.waitFor();
			BufferedReader bufferreader = new BufferedReader(new InputStreamReader(
					(process.exitValue() == 0) ? process.getInputStream() : process.getErrorStream()));

			String line = "";

			boolean bool = false;

			AddressInfoBean address = null;

			List<String> list = new ArrayList<String>();

			while (null != (buff = bufferreader.readLine())) {

				String[] arry = buff.split(" ");
				if (arry[0].contains("bond") && !bool) {
					list.add(buff);
					bool = true;
					continue;
				}
				if (arry[0].contains("eth") && !bool) {
					list.add(buff);
					bool = true;
					continue;
				}

				if (bool) {
					if (arry[0].equals("")) {
						list.add(buff);
					} else {
						address = getAddress(list);
						addrList.add(address);
						bool = false;
						list = new ArrayList<String>();
						if (arry[0].contains("eth")) {
							list.add(buff);
							bool = true;
							continue;
						} else if (arry[0].contains("bond")) {
							list.add(buff);
							bool = true;
							continue;
						}
					}
				}

			}

			Iterator<AddressInfoBean> it = addrList.iterator();

			while (it.hasNext()) {

				AddressInfoBean addr = getAllEths(it.next(),filePath);
				// System.out.println("addr:"+addr.getDecviceName());
				if (addr != null) {
					// it.remove();
					// list.add(addr);

				} else {
					it.remove();
				}
			}
			logger.debug("addressList:" + addrList);
		} catch (Exception e) {
		}
		return addrList;

	}


	private AddressInfoBean getAddress(List<String> list) {

		AddressInfoBean address = new AddressInfoBean();
		boolean bool = false;
		for (String buff : list) {
			String[] arry = buff.split(" ");

			if (arry[0].contains("eth")) {

				address.setDecviceName(arry[0]);

				if ("eth0".equals(arry[0])) {

					address.setDeviceType("管理口");
				} else {
					address.setDeviceType("业务口");
				}
			} else if (arry[0].contains("bond")) {
				address.setDecviceName(arry[0]);
				address.setDeviceType(arry[0]);
			}
			// bool = true;

			if (buff.contains("inet addr")) {
				for (int i = 0; i < arry.length; i++) {
					if (arry[i].contains("addr:")) {
						String[] addrArray = arry[i].split(":");
						address.setIpAddress(addrArray[1].trim());
						break;
					}
				}

			}

			if (buff.contains("inet addr")) {
				for (int i = 0; i < arry.length; i++) {
					if (arry[i].contains("Mask:")) {

						String[] addrArray = arry[i].split(":");
						address.setSubnetMask(addrArray[1].trim());
						break;
					}
				}
			}

			if (buff.contains("RUNNING")) {
				bool = true;

			}
		}
		if (bool) {
			address.setLinkState("1");

			bool = false;
		} else if (!bool) {

			address.setLinkState("0");

			bool = false;
		}

		return address;
	}

	// 读取所有网口
	@SuppressWarnings("resource")
	private AddressInfoBean getAllEths(AddressInfoBean address,String filePath) {
		String eths = "";
		try {
			String line = "";
			BufferedReader bufferreader = new BufferedReader(new FileReader(filePath));
			while ((line = bufferreader.readLine()) != null) {
				eths = line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println("deviceName:"+deviceName);
		String deviceName1 = address.getDecviceName();
		if (deviceName1.contains("bond")) {
			address.setDecviceName(deviceName1.substring(0, 5));
			return address;
		}
		String deviceName = address.getDecviceName().substring(0, 4);
		if (eths.contains(deviceName)) {

			String[] ethArry = eths.split(",");

			for (int i = 0; i < ethArry.length; i++) {

				if (ethArry[i].indexOf(deviceName) != -1) {

					address.setDecviceName(ethArry[i].split(":")[1]);
				}
			}

			return address;

		} else {
			return null;
		}
	}

	// 读取所有网口
	@SuppressWarnings("resource")
	private DeviceStateBean isExistDevice(DeviceStateBean device,String filePath) {
		String eths = "";
		try {
			String line = "";
			BufferedReader bufferreader = new BufferedReader(new FileReader(filePath));
			while ((line = bufferreader.readLine()) != null) {
				eths = line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String deviceName = device.getDecviceName();
		if (eths.contains(deviceName)) {

			String[] ethArry = eths.split(",");

			for (int i = 0; i < ethArry.length; i++) {

				if (ethArry[i].indexOf(deviceName) != -1) {

					device.setDecviceName(ethArry[i].split(":")[1]);
				}
			}

			return device;

		} else {

			return null;
		}
	}

	public CpuMemeryStateBean getCpuMemeryState() {
		logger.debug("CommandProcessUtil getDeviceList");
		String cmd = "top -bn 1";
		String[] commands = { "/bin/sh", "-c", cmd };
		logger.debug("CommandProcessUtil cmd:" + cmd);
		Runtime runtime = Runtime.getRuntime();
		Process process;

		CpuMemeryStateBean cpu = new CpuMemeryStateBean();

		String buff;
		try {
			process = runtime.exec(commands);
			process.waitFor();
			BufferedReader bufferreader = new BufferedReader(new InputStreamReader(
					(process.exitValue() == 0) ? process.getInputStream() : process.getErrorStream()));

			// BufferedReader bufferreader = new BufferedReader(new
			// InputStreamReader(new FileInputStream("E://text.txt")));
			Pattern p = Pattern.compile("[0-9\\.]+");

			while (null != (buff = bufferreader.readLine())) {
				if (buff.contains("Cpu(s):")) {
					String[] cpuArray = buff.split(",");

					for (int i = 0; i < cpuArray.length; i++) {
						if (cpuArray[i].contains("%id")) {
							Matcher m = p.matcher(cpuArray[i]);
							String cpuRate = "";
							while (m.find()) {
								cpuRate += m.group();
							}
							float rate = (float) (100 - Double.parseDouble(cpuRate));
							cpuRate = String.valueOf(rate);
							cpu.setCpuUseRate(cpuRate + "%");
						}
					}
				}

				if (buff.contains("Mem:")) {
					String[] memArray = buff.split(",");
					for (int i = 0; i < memArray.length; i++) {

						String cpuUse = "";
						if (memArray[i].contains("Mem:")) {
							Matcher m = p.matcher(memArray[i]);
							while (m.find()) {
								cpuUse += m.group();
							}
							cpu.setTotalMemery(cpuUse);
							double userStr = Double.parseDouble(cpuUse);
							userStr = userStr / 1024 / 1024;
							userStr = Math.ceil(userStr);
							String str = String.valueOf(userStr);
							if (str.indexOf(".") > 0) {

								str = str.substring(0, str.indexOf("."));
							}
							cpu.setMemeryUseRate(str + "G");
						}

						String used = "";
						if (memArray[i].contains("used")) {

							Matcher m = p.matcher(memArray[i]);
							while (m.find()) {
								used += m.group();
							}

							cpuUse = cpu.getTotalMemery();
							if (cpuUse != null && !"".equals(cpuUse) && !"0".equals(cpuUse)) {

								int currMem = Integer.parseInt(used);
								int mem = Integer.parseInt(cpuUse);
								float percent = (float) currMem / (float) mem;

								// 获取格式化对象
								NumberFormat nt = NumberFormat.getPercentInstance();
								// 设置百分数精确度2即保留两位小数
								nt.setMinimumFractionDigits(2);
								cpu.setCurrentUseRate(nt.format(percent));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return cpu;
	}
}
