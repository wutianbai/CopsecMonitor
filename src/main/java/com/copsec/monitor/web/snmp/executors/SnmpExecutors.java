package com.copsec.monitor.web.snmp.executors;

import com.copsec.monitor.web.beans.ServiceConfigBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.snmp.InterfaceBean;
import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.copsec.monitor.web.beans.snmp.SnmpDeviceStatus;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.CommonPools;
import com.copsec.monitor.web.pools.DevicePools;
import com.copsec.monitor.web.pools.SnmpDeviceStatusPools;
import com.copsec.monitor.web.snmp.pools.SnmpDevicePool;
import com.copsec.monitor.web.utils.logUtils.SnmpSyslogUtils;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Component
public class SnmpExecutors {

    private static final Logger logger = LoggerFactory.getLogger(SnmpExecutors.class);

    @Autowired
    private SystemConfig systemConfig;

    private static final long period = 5000;
    /**
     * 存放设备和接口之间的对应关系
     */
    private static Map<String, List<InterfaceBean>> ifMap = Maps.newConcurrentMap();

    /**
     * 定期执行fatch任务
     */
    @Scheduled(fixedRate = 5000)
    public void fetchDeviceInfo() {
        ServiceConfigBean serviceConfig = runTest();
        if (ObjectUtils.isEmpty(serviceConfig)) {
            return;
        }

        /**
         * 获取所有snmp设备
         */
        Map<String, SnmpConfigBean> map = SnmpDevicePool.getInstance().getAll();
        map.entrySet().stream().forEach(entry -> {

            SnmpDeviceStatus status = SnmpDeviceStatusPools.getInstances().get(entry.getKey());
            if (ObjectUtils.isEmpty(status)) {
                status = new SnmpDeviceStatus();
            }

            /**
             * 根据设备信息获取设备状态信息，并通过syslog发送
             */
            String deviceId = entry.getKey();
            SnmpConfigBean snmpConfig = entry.getValue();
            Device device = DevicePools.getInstance().getDevice(deviceId);
            try {

                Map<String, String> statusMap = Maps.newConcurrentMap();
//                if (device.getData().getDeviceType().equals("linux")) {
//                    List<InterfaceBean> preValues = ifMap.get(deviceId);
//                    if (ObjectUtils.isEmpty(preValues)) {
//                        ifMap.putIfAbsent(deviceId, new LinkedList<InterfaceBean>());
//                    }
//                    BaseSnmpComponent linuxComponent = new SnmpForDX(snmpConfig, SnmpMibPools.getInstances().getMibsByType(NetworkType.LINUX), systemConfig);
//
//                    status = linuxComponent.generateDeviceStatus(preValues, period, statusMap);
//
//                    List<InterfaceBean> currentValues = linuxComponent.getInterfaceCurrentValues(statusMap);
//                    ifMap.replace(deviceId, currentValues);
//                } else if (device.getData().getDeviceType().equals("firewall")) {
//
//                    BaseSnmpComponent snmpForFirewall = new SnmpForFirewall(snmpConfig, SnmpMibPools.getInstances().getMibsByType(NetworkType.LINUX), systemConfig);
//
//                    status = snmpForFirewall.generateDeviceStatus(null, period, statusMap);
//
//                    status.setWarnMessage(status.getDeviceStatus());
//                } else if (device.getData().getDeviceType().equals("networkAudit")) {
//
//                    BaseSnmpComponent snmpForNetworkAudit = new SnmpForNetworkAudit(snmpConfig, SnmpMibPools.getInstances().getMibsByType(NetworkType.NETWORKAUDIT), systemConfig);
//
//                    status = snmpForNetworkAudit.generateDeviceStatus(null, period, statusMap);
//
//                    status.setWarnMessage(status.getDeviceStatus());
//                } else if (device.getData().getDeviceType().equals("detection")) {
//
//                    BaseSnmpComponent detectionComponent = new SnmpForDetection(snmpConfig, SnmpMibPools.getInstances().getMibsByType(NetworkType.DETECTION), systemConfig);
//
//                    status = detectionComponent.generateDeviceStatus(null, period, statusMap);
//
//                    status.setWarnMessage(status.getDeviceStatus());
//                }

                SnmpDeviceStatusPools.getInstances().update(status);
                SnmpSyslogUtils.sendLog(SnmpDeviceStatusPools.getInstances().getStatusReport(status), serviceConfig.getIp(), serviceConfig.getPort());

            } catch (Throwable e) {

                logger.error(e.getMessage(), e);
            }
        });
    }

    /**
     * 使用ping命令检测设备是否正常
     */
    @Scheduled(fixedRate = 5000)
    public void checkPingDeviceStatus() {

        ServiceConfigBean serviceConfig = runTest();
        if (ObjectUtils.isEmpty(serviceConfig)) {

            return;
        }

        List<Device> devicesList = DevicePools.getInstance().getAll();
        devicesList.stream().forEach(device -> {

//            if (device.getData().isPingCheck()) {
//
//                String ip = device.getData().getCheckIp();
//                if (!ObjectUtils.isEmpty(ip)) {
//
//                    try {
//                        String pingResult = CommandProcessUtils.getUtil().pingTesting(ip);
//                        SnmpDeviceStatus status = new SnmpDeviceStatus();
//                        status.setDeviceId(device.getData().getId());
//                        status.setUpdateTime(new Date());
//                        status.setCpuUseRate(0.0);
//                        status.setMenUseRate(0.0);
//                        status.setNetSpeed(0.0);
//                        status.setTotalAddSize(0);
//                        if (!pingResult.contains("100% packet loss")) {
//
//                            status.setDeviceStatus("健康");
//                            status.setWarnMessage("网络接口正常");
//                        } else {
//
//                            status.setDeviceStatus("故障");
//                            status.setWarnMessage("远程主机连接失败");
//                        }
//
//                        SnmpSyslogUtils.sendLog(SnmpDeviceStatusPools.getInstances().getStatusReport(status), serviceConfig.getIp(), serviceConfig.getPort());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
        });
    }


    private ServiceConfigBean runTest() {

        ServiceConfigBean serviceConfig = new ServiceConfigBean();
        List<String> _list = CommonPools.getInstances().getNetowkConfig(NetworkType.LISTENER);
        if (ObjectUtils.isEmpty(_list)) {

            ServiceConfigBean config = new ServiceConfigBean();
            CommonPools.getInstances().add(NetworkType.LISTENER, config.toString());
        }
        String _status = CommonPools.getInstances().getNetowkConfig(NetworkType.LISTENER).get(0);
        if (!ObjectUtils.isEmpty(_status) && _status.split(Resources.SPLITER).length == 3) {

            String[] attrs = _status.split(Resources.SPLITER);
            serviceConfig.setStatus(attrs[0]);
            serviceConfig.setIp(attrs[1]);
            serviceConfig.setPort(Integer.valueOf(attrs[2]));
            return serviceConfig;
        } else {

            logger.warn("no Listener info set {} will return ", Thread.currentThread().getName());
            return null;
        }
    }
}
