package com.copsec.monitor.web.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class SocketUtils {

    private static final Logger logger = LoggerFactory.getLogger(SocketUtils.class);
    private static SocketUtils util = new SocketUtils();

    private SocketUtils() {
    }

    public synchronized static SocketUtils getUtil() {
        if (util == null) {
            util = new SocketUtils();
        }
        return util;
    }

    /**
     * @param @param  ip
     * @param @param  port
     * @param @return
     * @param @throws IOException
     * @return String
     * @throws
     * @Title: ipv4ServiceTesting
     * @Description: ipv4服务检测
     */
    public synchronized String ipv4ServiceTesting(String ip, String port) throws IOException {
        logger.debug("SocketUtil  ipv4ServiceTesting");
        logger.debug("ip:" + ip);
        logger.debug("port" + port);

        String string = "";
        final int length = 10;
        Socket[] socket = new Socket[length];
        for (int i = 0; i < length; i++) {
            socket[i] = new Socket(ip, Integer.parseInt(port));
            string = "[检测10次]" + (i + 1) + "次连接成功！";
            logger.debug("ipv4ServiceTesting message:" + "第" + (i + 1) + "次连接成功！");
        }
        for (int i = 0; i < length; i++) {
            socket[i].close();
        }
        return string;
    }

    /**
     * @param @param  ipv6Ip
     * @param @param  ipv6Port
     * @param @return
     * @param @throws IOException
     * @return String
     * @throws
     * @Title: ipv6ServiceTesting
     * @Description: ipv6服务检测
     */
    public synchronized String ipv6ServiceTesting(String ipv6Ip, String ipv6Port) throws IOException {
        logger.debug("SocketUtil  ipv6ServiceTesting");
        logger.debug("ipv6Ip:" + ipv6Ip);
        logger.debug("ipv6Port:" + ipv6Port);

        String string = "";
        final int length = 10;
        Socket[] socket = new Socket[length];
        for (int i = 0; i < length; i++) {
            socket[i] = new Socket(ipv6Ip, Integer.parseInt(ipv6Port));
            string = "[检测10次]" + (i + 1) + "次连接成功！";
            logger.debug("ipv6ServiceTesting message:" + "第" + (i + 1) + "次连接成功！");
        }
        for (int i = 0; i < length; i++) {
            socket[i].close();
        }
        return string;
    }
}
