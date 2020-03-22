package com.copsec.monitor.web.fileReaders;

import com.copsec.monitor.web.fileReaders.fileReaderEnum.FileReaderType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FileReaderFactory implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    public static BaseFileReader getFileReader(FileReaderType type) {

        if (type == FileReaderType.COMMON) {

            return (CommonFileReader) applicationContext.getBean("commonFileReader");
        } else if (type == FileReaderType.DEVICE) {

            return (DeviceFileReader) applicationContext.getBean("deviceFileReader");
        } else if (type == FileReaderType.LINK) {

            return (LinkFileReader) applicationContext.getBean("linkFileReader");
        } else if (type == FileReaderType.ZONE) {

            return (ZoneFileReader) applicationContext.getBean("zoneFileReader");
        } else if (type == FileReaderType.USER) {

            return (UserFileReader) applicationContext.getBean("userFileReader");
        } else if (type == FileReaderType.USERINFO) {

            return (UserInfoReader) applicationContext.getBean("userInfoReader");
        } else if (type == FileReaderType.IPREADER) {

            return (IpConfigFileReader) applicationContext.getBean("ipConfigFileReader");
        } else if (type == FileReaderType.NETWORKTIMING) {

            return (NetworkTimeReader) applicationContext.getBean("networkTimeReader");
        } else if (type == FileReaderType.NETCONFIG) {

            return (NetConfigReader) applicationContext.getBean("netConfigReader");
        } else if (type == FileReaderType.ROUTER) {

            return (RouterBeanReader) applicationContext.getBean("routerBeanReader");
        } else if (type == FileReaderType.BONDMODE) {

            return (BondModeReader) applicationContext.getBean("bondModeReader");
        } else if (type == FileReaderType.TASKPOLICY) {

            return (TaskPolicyReader) applicationContext.getBean("taskPolicyReader");
        } else if (type == FileReaderType.MIB) {

            return (SnmpMibReader) applicationContext.getBean("snmpMibReader");
        } else if (type == FileReaderType.LOGSETTING) {

            return (LogSettingBeanReader) applicationContext.getBean("logSettingBeanReader");
        } else if (type == FileReaderType.MONITORITEM) {

            return (MonitorItemReader) applicationContext.getBean("monitorItemReader");
        } else if (type == FileReaderType.MONITORGROUP) {

            return (MonitorGroupReader) applicationContext.getBean("monitorGroupReader");
        } else if (type == FileReaderType.MONITORTASK) {

            return (MonitorTaskReader) applicationContext.getBean("monitorTaskReader");
        } else if (type == FileReaderType.WARNINGITEM) {

            return (WarningItemReader) applicationContext.getBean("warningItemReader");
        } else {
            return null;
        }
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        FileReaderFactory.applicationContext = applicationContext;
    }
}
