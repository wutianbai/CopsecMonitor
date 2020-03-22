package com.copsec.monitor.web.handler.impl;

import com.copsec.monitor.web.beans.syslogParseBeans.ProtocolLogBean;
import com.copsec.monitor.web.beans.syslogParseBeans.SyslogMessageBean;
import com.copsec.monitor.web.entity.ProtocolHistoryStatus;
import com.copsec.monitor.web.handler.TaskHistoryStatusHandler;
import com.copsec.monitor.web.pools.TaskStatusHandlerPools;
import com.copsec.monitor.web.repository.ProtocolStatusRepository;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Component
public class ProtocolTaskStatusHandler implements TaskHistoryStatusHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolTaskStatusHandler.class);

    private List<String> logTypes = Lists.newArrayList("tcpProxy", "gb28181Proxy",
            "hlsProxy", "rtmpProxy", "broadcastProxy", "modbusTcpProxy", "multicastProxy", "smtpProxy",
            "mysqlProxy", "sipProxy", "rtspProxy", "modbusUdpProxy", "h225Proxy", "httpProxy2", "udpProxy", "pop3Proxy");

    private static final String LOG_ERROR_LEVEL = "(3) 错误";

    private static final String IGNORE_TASK_NAME = "N/A";

    @Autowired
    private ProtocolStatusRepository protocolRepository;

    @PostConstruct
    public void init() {

        TaskStatusHandlerPools.getInstance().registerHandler(this);
    }

    @Override
    public void handler(SyslogMessageBean syslogMessageBean) {

        final String type = getSyslogTypes(syslogMessageBean);
        ProtocolLogBean protocolBean = new ProtocolLogBean();
        protocolBean.setDeviceId(syslogMessageBean.getDeviceId());
        protocolBean.setUpdateTime(syslogMessageBean.getUpdateTime());
        syslogMessageBean.getProperties().stream().filter(d -> {

            if (d.split("=").length == 2) {

                return true;
            }
            return false;
        }).forEach(attrs -> {

            String[] attr = attrs.split("=");
            switch (attr[0]) {

                case "taskName": {

                    if (attr[1].equals(IGNORE_TASK_NAME)) {

                        protocolBean.setCanSave(false);
                    } else {

                        protocolBean.setTaskName(attr[1]);
                        protocolBean.setCanSave(true);
                    }
                    break;
                }
                case "logLevel": {

                    protocolBean.setError(attr[1].equalsIgnoreCase(LOG_ERROR_LEVEL));
                    break;
                }
                case "desc": {

                    switch (type) {

                        case "tcpProxy": {

                            if (attr[1].startsWith("新连接接入")) {

                                protocolBean.setHandleConnectionCount(1);
                            }
                            break;
                        }
                        case "gb28181Proxy": {

                            if (attr[1].startsWith("新建RTCP-udp会话通道")) {

                                protocolBean.setHandleConnectionCount(1);
                            }
                            break;
                        }
                        case "hlsProxy": {

                            break;
                        }
                        case "rtmpProxy": {

                            break;
                        }
                        case "broadcastProxy": {

                            break;
                        }
                        case "modbusTcpProxy": {
                            if (attr[1].startsWith("响应消息")) {

                                protocolBean.setHandleConnectionCount(1);
                            }
                            break;
                        }
                        case "multicastProxy": {

                            break;
                        }
                        case "smtpProxy": {

                            break;
                        }
                        case "mysqlProxy": {

                            break;
                        }
                        case "sipProxy": {

                            break;
                        }
                        case "rtspProxy": {

                            break;
                        }
                        case "modbusUdpProxy": {
                            if (attr[1].startsWith("响应消息")) {

                                protocolBean.setHandleConnectionCount(1);
                            }
                            break;
                        }
                        case "h225Proxy": {

                            break;
                        }
                        case "httpProxy2": {

                            break;
                        }
                        case "udpProxy": {

                            break;
                        }
                        case "pop3Proxy": {

                            break;
                        }

                    }
                    break;
                }
            }
        });

        if (protocolBean.isCanSave()) {

            updateProtocolSyslog(protocolBean);
        }
    }


    private void updateProtocolSyslog(ProtocolLogBean bean) {

        ProtocolHistoryStatus status = new ProtocolHistoryStatus();
        status.setTaskName(bean.getTaskName());
        status.setDeviceId(bean.getDeviceId());
        status.setUpdateTime(bean.getUpdateTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(bean.getUpdateTime());
        status.setYear(calendar.get(Calendar.YEAR));
        status.setMonth((calendar.get(Calendar.MONTH) + 1));
        status.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        status.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        if (bean.isError()) {

            status.setHandleErrorConnectCount(bean.getHandleConnectionCount());
        } else {

            status.setHandleConnectCount(bean.getHandleConnectionCount());
            status.setHandleNetDataSize(bean.getHandleNetDataSize());
        }
        protocolRepository.updateProtocolHistoryStatus(status);
    }

    private String getSyslogTypes(SyslogMessageBean syslogMessageBean) {

        Optional<String> optionalS = syslogMessageBean.getProperties().stream().filter(d -> {

            if (d.startsWith("type")) {
                return true;
            }
            return false;
        }).findFirst();
        if (optionalS.isPresent()) {

            String type = optionalS.get().split("=")[1];
            if (logTypes.contains(type)) {

                return type;
            }
        }
        return "";
    }
}

