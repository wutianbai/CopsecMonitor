package com.copsec.monitor.web.utils.logUtils;

import com.copsec.monitor.web.config.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SysLogUtil {
    private static Logger logger = LoggerFactory.getLogger(SysLogUtil.class);

    @Autowired
    private static SystemConfig config;

    private final static String encoding = "UTF-8";

    private static DatagramSocket datagramSocket = null;
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final String deviceVendorPre = "deviceVendor:";
    private static final String deviceProductTypePre = "deviceProductType:";
    private static final String deviceSendProductNamePre = "deviceSendProductName:";
    private static final String deviceProtocolPre = "deviceProtocol";
    private static final String deviceAddressPre = "deviceAddress:";
    private static final String deviceHostnamePre = "deviceHostname:";
    private static final String rawEventPre = "rawEvent:";
    private static final String messagePre = "message:";
    private static final String collectorReceiptTimePre = "collectorReceiptTime:";
    private static final String resultPre = "result:";

    private final static String separator = "\r\n";
    private final static String exSeparator = "\r\n\r\n";

    public final static String na = "N/A";

    public final static String result_success = "成功";
    public final static String result_failure = "失败";

    //    private static void sendLog(String address, String hostname, String rawEvent, String message, String result) {
    public static void sendLog(String address, String hostname, String rawEvent, String message) {
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
            sb.append(deviceVendorPre).append(config.getDeviceVendor()).append(exSeparator);
            sb.append(deviceProductTypePre).append(config.getDeviceProductType()).append(separator);
            sb.append(deviceSendProductNamePre).append(config.getDeviceSendProductName()).append(separator);
            sb.append(deviceProtocolPre).append("syslog").append(separator);
            sb.append(deviceAddressPre).append(address).append(separator);
            sb.append(deviceHostnamePre).append(hostname).append(separator);
            sb.append(rawEventPre).append(rawEvent).append(separator);
            sb.append(messagePre).append(message).append(separator);
            sb.append(collectorReceiptTimePre).append(format.format(new Date())).append(separator);
//            sb.append(resultPre).append(result).append(separator);

            byte[] bytes;
            try {
                bytes = sb.toString().getBytes(encoding);
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
                return;
            }
            try {
                datagramSocket.send(new DatagramPacket(bytes, bytes.length, InetAddress.getByName(config.getSysLogHost()), config.getSysLogPort()));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

//    /**
//     * 发送成功日志信息
//     */
//    public static void sendSuccessLog(String address, String hostname, String rawEvent, String message) {
//        sendLog(address, hostname, rawEvent, message, result_success);
//    }
//
//    /**
//     * 发送失败日志信息
//     */
//    public static void sendFailLog(String address, String hostname, String rawEvent, String message) {
//        sendLog(address, hostname, rawEvent, message, result_failure);
//    }

    private static void closeSocket() {
        if (null != datagramSocket) {
            datagramSocket.close();
            datagramSocket = null;
        }
    }
}
