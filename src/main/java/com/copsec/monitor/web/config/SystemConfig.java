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

	@Value("${production.config.backup.file.name}")
	private String backupFileName;

	@Value("${production.config.backup.file.path}")
	private String backupFilePath;

	@Value("{production.system.password}")
	private String systemPassword;

	@Value("${production.system.file.upload.path}")
	private String uploadFilePath;

	@Value("${production.system.type}")
	private String systemType;

	@Value("${production.system.current.version}")
	private String currentVersion;

	@Value("${production.system.upgrade.version}")
	private String upgradeVersion;

	@Value("${mongo.db.log.listen.host}")
	private String logHost;

	@Value("${mongo.db.log.listen.port}")
	private int logPort;

	@Value("${mongo.db.log.listen.collection}")
	private String logCollection;

	//升级包上传路径
	@Value("${production.system.upgrade.file.path}")
	private String upgradeFilePath;

	@Value("${production.system.reset.file.path}")
	private String resetFilePath;

	@Value("${production.system.upgrade.sh.path}")
	private String upgradeShellPath;

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

	@Value("${production.menu.device.deviceInfo}")
	private String deviceInfo;

	@Value("${production.menu.flume.title}")
	private String flumeTitle;

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

	@Value("${production.menu.statistics.title}")
	private String statisticsTitle;

	@Value("${production.menu.statistics.cpu}")
	private String statisticsCpu;

	@Value("${production.menu.statistics.memory}")
	private String statisticsMemory;

	@Value("${production.menu.statistics.traffic}")
	private String statisticsTraffic;

	@Value("${production.menu.statistics.protocol}")
	private String statisticsProtocol;

	@Value("${production.menu.statistics.database}")
	private String statisticsDatabase;

	@Value("${production.menu.statistics.file}")
	private String statisticsFile;

	@Value("${production.menu.statistics.total}")
	private String statisticsTotal;

	@Value("${production.menu.system.manager.title}")
	private String systemMng;

	@Value("${production.menu.system.manager.control}")
	private String systemCtl;

	@Value("${production.menu.system.manager.resetPass}")
	private String systemReset;

	@Value("${production.menu.system.manager.opsAccount}")
	private String opsAccount;

	@Value("${production.menu.system.manager.config}")
	private String systemConfig;

	@Value("${production.menu.system.manager.login}")
	private String systemLogin;

	@Value("${production.menu.system.manager.time}")
	private String systemTime;

	@Value("${production.menu.system.manager.update}")
	private String systemUpdate;

	@Value("${production.menu.system.manager.network}")
	private String systemNetwork;

	@Value("${production.menu.system.manager.networkIpv6}")
	private String systemNetworkIpv6;

	@Value("${production.menu.system.manager.networkInterface}")
	private String systemNetworkInterface;

	@Value("${production.system.status.file.path}")
	private String systemStatusPath;

	@Value("${production.menu.flume.service}")
	private String inspectorService;

	@Value("${production.menu.log.audit.title}")
	private String audit;

	@Value("${production.menu.log.audit.operate}")
	private String auditOperate;

	@Value("${production.menu.log.audit.server}")
	private String serverLog;

	@Value("${production.menu.system.monitor.title}")
	private String monitorTitle;

	@Value("${production.menu.system.monitor.service}")
	private String monitorService;

	@Value("${production.menu.system.monitor.system}")
	private String monitorSystem;

	@Value("${production.menu.system.monitor.device}")
	private String monitorDevice;

	@Value("${production.menu.system.monitor.interface}")
	private String monitorInterface;

	@Value("${production.menu.system.tool.title}")
	private String toolTitle;

	@Value("${production.menu.system.tool.ping}")
	private String toolPing;

	@Value("${production.menu.system.tool.router}")
	private String toolRouter;

	@Value("${production.menu.system.tool.service}")
	private String toolService;

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

	@Value("${system.network.ssh.path}")
	private String networkSSHPath;

	@Value("${system.network.snmp.path}")
	private String networkSNMPPath;

	@Value("${system.login.retry.time.path}")
	private String retryTimePath;

	@Value("${system.login.lock.time.path}")
	private String lockTimePath;

	@Value("${system.login.allowed.ip.path}")
	private String allowIpPath;

	@Value("${system.timing.network.path}")
	private String timingNetworkPath;


	@Value("${system.timing.local.path}")
	private String timingLocalPath;

	@Value("${production.system.network.interface.path}")
	private String interfacePath;

	@Value("${production.system.network.interface.Msg.path}")
	private String interfacePath4Msg;

	@Value("${production.system.network.interface.config.path}")
	private String interfaceConfigPath;

	@Value("${produciotn.system.netowrk.dns.path}")
	private String dnsPath;

	@Value("${production.system.network.manager.path}")
	private String managerIpPath;

	@Value("${production.system.network.gatway.path}")
	private String gatewayPath;

	@Value("${production.system.network.router.path}")
	private String routerPath;


	@Value("${produciotn.system.netowrk.dnsv6.path}")
	private String dnsPathv6;

	@Value("${production.system.network.managerv6.path}")
	private String managerIpPathv6;

	@Value("${production.system.network.gatwayv6.path}")
	private String gatewayPathv6;

	@Value("${production.system.network.routerv6.path}")
	private String routerPathv6;

	@Value("${production.system.network.interfacev6.config.path}")
	private String interfaceConfigPathv6;

	@Value("${production.system.network.bond.config.path}")
	private String netPortBondConfigPath;

	/**
	 * 探针相关配置信息
	 */
	@Value("${inspection.snmp.default.timeout}")
	private long snmpTimeout;

	@Value("${inspection.snmp.default.retry}")
	private int snmpRetryTimes;


	// 配置文件相关项
	@Value("${inspection.flume.base.dir}")
	private String flumeStartUpShell;

	@Value("${inspection.config.flume.dir}")
	private String configFlume;

	@Value("${system.snmp.device.path}")
	private String snmpDevicePath;

	@Value("${inspection.config.flume.source}")
	private String flumeSourcePath;

	@Value("${inspection.config.flume.channel}")
	private String flumeChannelPath;

	@Value("${inspection.config.flume.sink}")
	private String flumeSinkPath;

	@Value("${inspection.config.flume.path}")
	private String flumeConfigPath;

	@Value("${inspection.config.flume.alias.path}")
	private String flumeAliasePath;

	@Value("${inspection.mongo.sinks.type}")
	private String mongoSinkTypes;

	@Value("${mongo.db.host}")
	private String host;

	@Value("${mongo.db.port}")
	private int port;

	@Value("${inspection.transfer.service.path}")
	private String transferServicePath;

	@Value("${inspection.transfer.listener.path}")
	private String listeningServicePath;

	@Value("${inspection.transfer.device.path}")
	private String deviceServicePath;

	@Value("${production.syslog.service.path}")
	private String syslogConfigPath;

	@Value("${production.file.sync.monitor.path}")
	private String monitorFileSyncPath;

	@Value("${production.file.sync.monitor.title}")
	private String monitorFileSyncTitle;

	@Value("${production.menu.log.audit.config}")
	private String auditLogConfig;

	@Value("${production.menu.log.audit.runLog}")
	private String runLog;

	@Value("${production.menu.log.syslog.configPath}")
	private String transferSyslogConfigPath;

	@Value("${system.logs.base.path}")
	private String logRootPath;

	@Value("${production.system.zip.password}")
	private String zipPassword;

	@Value("${inspection.config.flume.env.path}")
	private String flumeEnvPath;

	@Value("${production.system.task.file.path}")
	private String fileSyncPolicyPath;

	@Value("${production.system.snmp.oid.path}")
	private String snmpDeviceOidPath;

	@Value("${production.system.monitor.taskNames}")
	private String taskNamesPath;

	@Value("${production.system.log.setting.path}")
	private String logSettingPath;

	@Value("${production.system.log.storage.path}")
	private String logStoragePath;

	@Value("${production.system.remote.device.path}")
	private String remoteDevicePath;

	@Value("${production.system.remote.uri.path}")
	private String remoteUriPath;

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
