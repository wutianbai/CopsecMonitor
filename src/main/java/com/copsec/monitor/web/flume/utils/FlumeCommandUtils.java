package com.copsec.monitor.web.flume.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlumeCommandUtils {

    private static final Logger logger = LoggerFactory.getLogger(FlumeCommandUtils.class);

    public static String startCommand(String agentName, String configDir, String configPath, String command) throws Exception {

        String[] cmds = {command, "agent", "-n", agentName, "-c", configDir, "-f", configPath};
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb.redirectErrorStream(true);
        if (logger.isDebugEnabled()) {
            logger.debug("start commands is {}", cmds.toString());
        }
        Process process = pb.start();
        BufferedReader bufferreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = bufferreader.readLine()) != null) {
            logger.warn("out put line {} ", line);
        }
        process.waitFor();
        return null;
    }

    public static String getPid(String configPath) throws Exception {
        String[] commands = {"/bin/sh", "-c", "ps -ef |grep java |grep " + configPath + " | awk '{print($2);}'"};
        Process process = Runtime.getRuntime().exec(commands);
        process.waitFor();

        BufferedReader bufferreader = new BufferedReader(new InputStreamReader(
                (process.exitValue() == 0) ? process.getInputStream() : process.getErrorStream()));

        String line = null;
        while ((line = bufferreader.readLine()) != null) {
            logger.warn("get pid of {}", line);
            break;
        }

        return line;
    }

    public static void stopFlumeAgent(String pid) throws Exception {
        String command = "kill -9 " + pid;
        String[] commands = {"/bin/sh", "-c", command};
        Process process = Runtime.getRuntime().exec(commands);
        process.waitFor();

        BufferedReader bufferreader = new BufferedReader(new InputStreamReader(
                (process.exitValue() == 0) ? process.getInputStream() : process.getErrorStream()));

        String line = "";
        while ((line = bufferreader.readLine()) != null) {

            logger.warn(line);
        }
    }
}
