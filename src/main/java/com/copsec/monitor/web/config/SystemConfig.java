package com.copsec.monitor.web.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:system.conf"},encoding = "UTF-8")
@Getter @Setter
public class SystemConfig {

	@Value("${production.title}")
	private String title;

	@Value("${mongo.db.log.listen.host}")
	private String logHost;

	@Value("${mongo.db.log.listen.port}")
	private int logPort;

	@Value("${mongo.db.log.listen.collection}")
	private String logCollection;

	@Value("${mongo.db.sysLog.listen.host}")
	private String sysLogHost;

	@Value("${mongo.db.sysLog.listen.port}")
	private int sysLogPort;

	@Value("${mongo.db.sysLog.listen.deviceVendor}")
	private String deviceVendor;

	@Value("${mongo.db.sysLog.listen.deviceProductType}")
	private String deviceProductType;

	@Value("${mongo.db.sysLog.listen.deviceSendProductName}")
	private String deviceSendProductName;

	@Value("${production.system.device.update.time}")
	private int deviceUpdateTime;

	//菜单操作相关
	@Value("${production.menu.device.title}")
	private String deviceTitle;

	@Value("${production.menu.device.manage}")
	private String deviceManage;

	@Value("${production.menu.device.add}")
	private String deviceAdd;

	@Value("${production.menu.device.edit}")
	private String deviceEdit;

	@Value("${production.menu.device.delete}")
	private String deviceDelete;

	@Value("${production.menu.link.title}")
	private String linkTitle;

	@Value("${production.menu.link.add}")
	private String linkAdd;

	@Value("${production.menu.link.edit}")
	private String linkEdit;

	@Value("${production.menu.link.delete}")
	private String linkDelete;

	@Value("${production.menu.topology.update}")
	private String topologyUpdate;

	@Value("${production.menu.network.title}")
	private String zoneTitle;

	@Value("${production.menu.network.add}")
	private String zoneAdd;

	@Value("${production.menu.network.update}")
	private String zoneUpdate;

	@Value("${production.menu.network.delete}")
	private String zoneDelete;

	@Value("${production.menu.system.manager.title}")
	private String systemMng;

	@Value("${production.menu.system.manager.resetPass}")
	private String systemReset;

	@Value("${production.menu.system.manager.opsAccount}")
	private String opsAccount;

	@Value("${production.menu.log.audit.title}")
	private String audit;

	@Value("${production.menu.log.audit.operate}")
	private String auditOperate;

	/**
	 * 一下配置文件和系统设置相关
	 */
	@Value("${system.config.base.path}")
	private String basePath;

	@Value("${system.user.path}")
	private String userPath;

	@Value("${system.userInfo.path}")
	private String userInfoPath;

	@Value("${system.node.device.path}")
	private String devicePath;

	@Value("${system.node.link.path}")
	private String linkPath;

	@Value("${system.node.zone.path}")
	private String zonePath;

	@Value("${mongo.db.host}")
	private String host;

	@Value("${mongo.db.port}")
	private int port;

	/**
	监控配置菜单与文件路径
	 */
	@Value("${production.menu.monitor.title}")
	private String monitorConfTitle;

	@Value("${production.menu.monitor.monitorItem}")
	private String monitorItem;

	@Value("${production.menu.monitor.monitorGroup}")
	private String monitorGroup;

	@Value("${production.menu.monitor.monitorTask}")
	private String monitorTask;

	@Value("${production.menu.monitor.warningItem}")
	private String warningItem;

	@Value("${production.menu.monitor.noWarningItem}")
	private String noWarningItem;

	@Value("${monitor.monitorItem.path}")
	private String monitorItemPath;

	@Value("${monitor.monitorGroup.path}")
	private String monitorGroupPath;

	@Value("${monitor.monitorTask.path}")
	private String monitorTaskPath;

	@Value("${monitor.warningItem.path}")
	private String warningItemPath;

	@Value("${monitor.noWarningItem.path}")
	private String noWarningItemPath;


}
