package com.copsec.monitor.web.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.copsec.monitor.web.beans.remote.RemoteDeviceBean;
import com.copsec.monitor.web.beans.remote.RemoteUriBean;
import com.copsec.monitor.web.beans.taskMonitor.FileSyncMonitorBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.BondModeReader;
import com.copsec.monitor.web.fileReaders.CommonFileReader;
import com.copsec.monitor.web.fileReaders.FileReaderFactory;
import com.copsec.monitor.web.fileReaders.IpConfigFileReader;
import com.copsec.monitor.web.fileReaders.LogSettingBeanReader;
import com.copsec.monitor.web.fileReaders.NetConfigReader;
import com.copsec.monitor.web.fileReaders.NetworkTimeReader;
import com.copsec.monitor.web.fileReaders.RouterBeanReader;
import com.copsec.monitor.web.fileReaders.SnmpMibReader;
import com.copsec.monitor.web.fileReaders.TaskPolicyReader;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.FileReaderType;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.CommonPools;
import com.copsec.monitor.web.pools.FileSyncStatusPools;
import com.copsec.monitor.web.pools.RemoteDevicePools;
import com.copsec.monitor.web.pools.RemoteUriPools;
import com.copsec.monitor.web.pools.SnmpMibPools;
import com.copsec.monitor.web.pools.TaskNamePools;
import com.google.common.graph.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

public class FileReaderUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileReaderUtils.class);

    private static final CommonFileReader reader = (CommonFileReader) FileReaderFactory.
            getFileReader(FileReaderType.COMMON);

    private static final IpConfigFileReader ipReader = (IpConfigFileReader) FileReaderFactory.
            getFileReader(FileReaderType.IPREADER);

    private static final NetworkTimeReader networkTimeReader = (NetworkTimeReader) FileReaderFactory.
            getFileReader(FileReaderType.NETWORKTIMING);

    private static final NetConfigReader netConfigReader = (NetConfigReader) FileReaderFactory.
            getFileReader(FileReaderType.NETCONFIG);

    private static final RouterBeanReader routerBeanReader = (RouterBeanReader) FileReaderFactory.
            getFileReader(FileReaderType.ROUTER);

    private static final BondModeReader bondReade = (BondModeReader) FileReaderFactory.getFileReader(FileReaderType.BONDMODE);

    private static final TaskPolicyReader taskPolicyReader = (TaskPolicyReader) FileReaderFactory.getFileReader(FileReaderType.TASKPOLICY);

    private static final SnmpMibReader snmpMibReader = (SnmpMibReader) FileReaderFactory.getFileReader(FileReaderType.MIB);

    private static final LogSettingBeanReader logSettingReader = (LogSettingBeanReader) FileReaderFactory.getFileReader(FileReaderType.LOGSETTING);

    public static void getData(SystemConfig config) {

        List<NetworkType> types = Arrays.asList(NetworkType.values());
        types.stream().forEach(type -> {

            try {

                String filePath = "";
                if (type == NetworkType.SSH) {

                    filePath = config.getBasePath() + config.getNetworkSSHPath();
                    if (!CommonUtils.exists(filePath)) {

                        CommonPools.getInstances().add(type, Resources.DISABLED);
                    }
                    reader.getData(filePath, type);
                } else if (type == NetworkType.SNMP) {

                    filePath = config.getBasePath() + config.getNetworkSNMPPath();
                    if (!CommonUtils.exists(filePath)) {

                        CommonPools.getInstances().add(type, Resources.DISABLED);
                    }
                    reader.getData(filePath, type);
                } else if (type == NetworkType.LOGINLOCKTIME) {

                    filePath = config.getBasePath() + config.getLockTimePath();
                    if (!CommonUtils.exists(filePath)) {

                        CommonPools.getInstances().add(type, Resources.DEFAUTL_LOCK_TIME);
                    }
                    reader.getData(filePath, type);
                } else if (type == NetworkType.LOGINTRYTIME) {

                    filePath = config.getBasePath() + config.getRetryTimePath();
                    if (!CommonUtils.exists(filePath)) {

                        CommonPools.getInstances().add(type, Resources.DEFAULT_TRY_TIME);
                    }
                    reader.getData(filePath, type);
                } else if (type == NetworkType.LOCALTIMING) {

                    filePath = config.getBasePath() + config.getTimingLocalPath();
                    if (!CommonUtils.exists(filePath)) {

                        CommonPools.getInstances().add(type, Resources.DISABLED);
                    }
                    reader.getData(filePath, type);
                } else if (type == NetworkType.ALLOWEDIP) {

                    filePath = config.getBasePath() + config.getAllowIpPath();
                    ipReader.getData(filePath);
                } else if (type == NetworkType.NETWORKTIMESERVICE) {

                    filePath = config.getBasePath() + config.getTimingNetworkPath();
                    networkTimeReader.getData(filePath);
                } else if (type == NetworkType.LOCALTIMING) {

                    filePath = config.getBasePath() + config.getTimingLocalPath();
                    reader.getData(filePath, type);

                } else if (type == NetworkType.SYSTEMTIME) {

                    CommonPools.getInstances().update(type, TimeFormatUtils.getServerTime());
                } else if (type == NetworkType.CURRENTVERSION) {

                    reader.getData(config.getCurrentVersion(), type);
                } else if (type == NetworkType.UPGRADEVERSION) {

                    reader.getData(config.getUpgradeVersion(), type);
                } else if (type == NetworkType.CURRENTUPGRADEPACKAGE) {

                    CommonPools.getInstances().add(type, CommonUtils.getUpgradeFile(config.getUpgradeFilePath()));
                } else if (type == NetworkType.DNS) {

                    filePath = config.getBasePath() + config.getDnsPath();
                    reader.getData(filePath, type);
                } else if (type == NetworkType.GATEWAY) {

                    filePath = config.getBasePath() + config.getGatewayPath();
                    reader.getData(filePath, type);
                } else if (type == NetworkType.MANAGERIP) {

                    filePath = config.getBasePath() + config.getManagerIpPath();
                    reader.getData(filePath, type);
                } else if (type == NetworkType.INTERFACELIST) {

                    filePath = config.getInterfacePath();
                    reader.getData(filePath, type);
                } else if (type == NetworkType.ROUTER) {

                    filePath = config.getBasePath() + config.getRouterPath();
                    routerBeanReader.getData(filePath, type);
                } else if (type == NetworkType.NETCONFIG) {

                    filePath = config.getBasePath() + config.getInterfaceConfigPath();
                    netConfigReader.getData(filePath, type);
                } else if (type == NetworkType.NETCONFIG6) {

                    filePath = config.getBasePath() + config.getInterfaceConfigPathv6();
                    netConfigReader.getData(filePath, type);
                } else if (type == NetworkType.ROUTER6) {

                    filePath = config.getBasePath() + config.getRouterPathv6();
                    routerBeanReader.getData(filePath, type);
                } else if (type == NetworkType.DNSV6) {

                    filePath = config.getBasePath() + config.getDnsPathv6();
                    reader.getData(filePath, type);
                } else if (type == NetworkType.GATEWAY6) {

                    filePath = config.getBasePath() + config.getGatewayPathv6();
                    reader.getData(filePath, type);
                } else if (type == NetworkType.MANAGER6) {

                    filePath = config.getBasePath() + config.getManagerIpPathv6();
                    reader.getData(filePath, type);
                } else if (type == NetworkType.BONDMODE) {

                    filePath = config.getBasePath() + config.getNetPortBondConfigPath();
                    bondReade.getData(filePath, type);
                } else if (type == NetworkType.TRANSFER) {

                    filePath = config.getBasePath() + config.getTransferServicePath();
                    reader.getData(filePath, type);
                } else if (type == NetworkType.LISTENER) {

                    filePath = config.getBasePath() + config.getListeningServicePath();
                    reader.getData(filePath, type);
                } else if (type == NetworkType.DEVICEINFO) {

                    filePath = config.getBasePath() + config.getDeviceServicePath();
                    reader.getData(filePath, NetworkType.DEVICEINFO);
                } else if (type == NetworkType.SYSLOGINFO) {

                    filePath = config.getBasePath() + config.getSyslogConfigPath();
                    reader.getData(filePath, NetworkType.SYSLOGINFO);
                } else if (type == NetworkType.FILESYNCMONITOR) {

                    filePath = config.getBasePath() + config.getMonitorFileSyncPath();
                    reader.getData(filePath, NetworkType.FILESYNCMONITOR);

                    List<String> list = CommonPools.getInstances().getNetowkConfig(NetworkType.FILESYNCMONITOR);
                    if (list.size() > 0) {

                        String str = list.get(0);
                        if (str.split(Resources.SPLITER).length == 3) {

                            String[] attrs = str.split(Resources.SPLITER);
                            FileSyncMonitorBean monitorConfig = new FileSyncMonitorBean();
                            monitorConfig.setStatus(attrs[0]);
                            monitorConfig.setInterval(Integer.valueOf(attrs[1]));
                            monitorConfig.setUnit(attrs[2]);

                            FileSyncStatusPools.getInstances().setConfig(monitorConfig);
                        }
                    }
                } else if (type == NetworkType.TRANSFERSYSLOG) {

                    filePath = config.getTransferSyslogConfigPath();
                    reader.getData(filePath, NetworkType.TRANSFERSYSLOG);
                } else if (type == NetworkType.TASKPOLICY) {

                    filePath = config.getBasePath() + config.getFileSyncPolicyPath();
                    taskPolicyReader.getData(filePath);
                } else if (type == NetworkType.MIB) {

                    filePath = config.getBasePath() + config.getSnmpDeviceOidPath();
                    File dir = new File(filePath);
                    if (dir.exists()) {

                        List<File> files = Arrays.asList(dir.listFiles());
                        files.stream().forEach(file -> {

                            try {

                                if (file.getName().equals("linux.conf")) {

                                    snmpMibReader.getData(file.getAbsolutePath(), NetworkType.LINUX);
                                    SnmpMibPools.getInstances().addTypeNames("linux", "单向设备");

                                } else if (file.getName().equals("firewall.conf")) {

                                    snmpMibReader.getData(file.getAbsolutePath(), NetworkType.FIREWALL);
                                    SnmpMibPools.getInstances().addTypeNames("firewall", "防火墙设备");
                                } else if (file.getName().equals("networkAudit.conf")) {

                                    snmpMibReader.getData(file.getAbsolutePath(), NetworkType.NETWORKAUDIT);
                                    SnmpMibPools.getInstances().addTypeNames("networkAudit", "网络审计设备");
                                } else if (file.getName().equals("detection.conf")) {

                                    snmpMibReader.getData(file.getAbsolutePath(), NetworkType.DETECTION);
                                    SnmpMibPools.getInstances().addTypeNames("detection", "入侵检测设备");
                                }
                            } catch (Throwable t) {

                                logger.error(t.getMessage(), t);
                            }
                        });
                    }
                } else if (type == NetworkType.TASKNAMES) {

                    filePath = config.getBasePath() + config.getTaskNamesPath();
                    List<String> list = reader.readContent(filePath);
                    TaskNamePools.getInstances().addAll(list);
                } else if (type == NetworkType.LOGSETTING) {

                    filePath = config.getBasePath() + config.getLogSettingPath();
                    logSettingReader.getData(filePath);
                } else if (type == NetworkType.LOGSTORAGE) {

                    filePath = config.getBasePath() + config.getLogStoragePath();
                    reader.getData(filePath, NetworkType.LOGSTORAGE);
                } else if (type == NetworkType.REMOTEDEVICE) {

                    filePath = config.getBasePath() + config.getRemoteDevicePath();
                    List<String> list = reader.readContent(filePath);
                    list.stream().filter(d -> {

                        if (d.split(Resources.SPLITER).length == 6) {

                            return true;
                        }
                        return false;
                    }).forEach(item -> {

                        String[] attrs = item.split(Resources.SPLITER);
                        RemoteDeviceBean bean = new RemoteDeviceBean();
                        bean.setDeviceId(attrs[0]);
                        bean.setDeviceName(attrs[1]);
                        bean.setDeviceType(attrs[2]);
                        bean.setDeviceIp(attrs[3]);
                        bean.setDevicePort(Integer.valueOf(attrs[4]));
                        bean.setDeviceProtocol(attrs[5]);
                        RemoteDevicePools.getInstances().update(bean);
                    });

                } else if (type == NetworkType.REMOTEURI) {

                    filePath = config.getBasePath() + config.getRemoteUriPath();
                    File file = new File(filePath);
                    if (file.isDirectory()) {

                        Stream<File> files = Arrays.stream(file.listFiles());
                        files.forEach(f -> {

                            String key = f.getName().replace(".conf", "");

                            try {
                                List<String> lines = reader.readContent(f.getAbsolutePath());
                                lines.stream().filter(d -> {

                                    if (d.split(Resources.SPLITER).length == 3) {

                                        return true;
                                    }
                                    return false;
                                }).forEach(line -> {

                                    String[] attrs = line.split(Resources.SPLITER);
                                    RemoteUriBean bean = new RemoteUriBean();
                                    bean.setUriType(attrs[0]);
                                    bean.setUrl(attrs[1]);
                                    bean.setUriMethod(attrs[2]);
                                    RemoteUriPools.getInstances().add(key, bean);
                                });
                            } catch (CopsecException e) {

                                logger.error(e.getMessage(), e);
                            }
                        });
                    }

                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        });
    }
}
