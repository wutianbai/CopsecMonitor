package com.copsec.monitor.web.snmp.mibs;

/**
 * linux 操作系统下相关oid信息
 */
public class LinuxMibs {

	/**
	 * 主机名
	 */
	public static final String DEVICE_NAME = "1.3.6.1.2.1.1.5.0";
	/**
	 * 一分钟内的cpu负载
	 */
	//public static final String CPU_USE_RATE = "1.3.6.1.4.1.2021.10.1.3.1";
	public static final String CPU_USE_RATE = ".1.3.6.1.4.1.2021.11.11.0";

	/**
	 * 内存总量
	 */
	public static final String MEMORY_TOTAL = "1.3.6.1.4.1.2021.4.5.0";
	/**
	 * 已使用内存
	 */
	public static final String MEMORY_FREE = "1.3.6.1.4.1.2021.4.6.0";

	/**
	 * 已缓存大小
	 */
	public static final String MEMORY_BUFFER = "1.3.6.1.4.1.2021.4.14.0";
	/**
	 * 缓存区大小
	 */
	public static final String MEMORY_CACHED = "1.3.6.1.4.1.2021.4.15.0";
	/**
	 * 当前设备网络接口数量
	 */
	public static final String INTERFACE_COUNT  = "1.3.6.1.2.1.2.1.0";

	/**
	 * 所有网络接口
	 * 某个网路接口发送或则接收的oid  INTERFACE_TABLE + index
	 */
	public static final String INTERFACE_TABLE = "1.3.6.1.2.1.2.2";


	/**
	 * 接收字节数 32位
	 */
	public static final String INTERFACE_IN_32 = "1.3.6.1.2.1.2.2.1.10";
	/**
	 * 发送字节数 32位
	 */
	public static final String INTERFACE_OUT_32 = "1.3.6.1.2.1.2.2.1.16";
	/**
	 * 接收字节数 64位
	 */
	public static final String INTERFACE_IN_64 = "1.3.6.1.2.1.31.1.1.1.6";
	/**
	 * 发送字节数 64位
	 */
	public static final String INTERFACE_OUT_64 = "1.3.6.1.2.1.31.1.1.1.10";

	/**
	 * speed
	 */
	public static final String INTERFACE_SPEED = "1.3.6.1.2.1.2.2.1.5";

	/**
	 * operStatus
	 */
	public static final String INTERFACE_STATUS = "1.3.6.1.2.1.2.2.1.8";

	/**
	 * interface name
	 */
	public static final String INTERFACE_NAME = "1.3.6.1.2.1.2.2.1.2";
}
