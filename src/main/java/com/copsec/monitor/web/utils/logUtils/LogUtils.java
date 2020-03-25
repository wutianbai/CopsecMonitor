package com.copsec.monitor.web.utils.logUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {
    private static Logger logger = LoggerFactory.getLogger(LogUtils.class);

    private final static String encoding = "UTF-8";

    private static DatagramSocket datagramSocket = null;
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final String instanceNamePre = "instanceName:";
    private static final String userPre = "user:";
    private static final String clientIpPre = "clientIp:";
    private static final String descPre = "desc:";
    private static final String resultPre = "result:";
    private static final String datePre = "date:";
    private static final String operateTypePre = "operateType:";

    private final static String separator = "\r\n";
    private final static String exSeparator = "\r\n\r\n";

    public final static String na = "N/A";

    public final static String result_success = "成功";
    public final static String result_failure = "失败";

    public static String instanceName = "设备集中监控系统";

    private static void sendLog(String user, String clientIp, String desc, String result, String logHost, int logPort, String collectionName, String otp) {
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
            sb.append(collectionName).append(exSeparator);
            sb.append(userPre).append(user).append(separator);
            sb.append(clientIpPre).append(clientIp).append(separator);
            sb.append(operateTypePre).append(otp).append(separator);
            sb.append(descPre).append(desc.replaceAll("\r\n", "\r")).append(separator);
            sb.append(resultPre).append(result).append(separator);
            sb.append(datePre).append(format.format(new Date()));

            byte[] bytes = null;
            try {
                bytes = sb.toString().getBytes(encoding);
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
                return;
            }
            /*try {
                //datagramSocket.send(new DatagramPacket(bytes, bytes.length, InetAddress.getByName(logHost), logPort));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return;
            }*/
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    /**
     * 发送成功日志信息
     *
     * @param user
     * @param clientIp
     * @param desc
     */
    public static void sendSuccessLog(String user, String clientIp, String desc, String logHost, int logPort, String collectionName, String operateTypePre) {
        sendLog(user, clientIp, desc, result_success, logHost, logPort, collectionName, operateTypePre);
    }

    /**
     * 发送失败日志信息
     *
     * @param user
     * @param clientIp
     * @param desc
     */
    public static void sendFailLog(String user, String clientIp, String desc, String logHost, int logPort, String collectionName, String operateTypePre) {
        sendLog(user, clientIp, desc, result_failure, logHost, logPort, collectionName, operateTypePre);
    }

    private static void closeSocket() {
        if (null != datagramSocket) {
            datagramSocket.close();
            datagramSocket = null;
        }
    }
}
