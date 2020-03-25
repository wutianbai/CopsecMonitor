package com.copsec.monitor.web.controller;

import com.alibaba.fastjson.JSON;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.monitor.MonitorGroupBean;
import com.copsec.monitor.web.beans.monitor.MonitorItemBean;
import com.copsec.monitor.web.beans.monitor.MonitorTaskBean;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.service.MonitorService;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/monitor")
@SessionAttributes(value = {"userInfo"})
public class MonitorController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorController.class);

    @Autowired
    private SystemConfig config;

    @Autowired
    private MonitorService monitorService;

    @GetMapping(value = {"/{pageId}"})
    public String toPage(@SessionAttribute UserBean userInfo, @PathVariable("pageId") String pageId) {

        return "monitor/" + pageId;
    }

    @PostMapping("/monitorItem/get")
    @ResponseBody
    public CopsecResult gatAllMonitorItem() {
        if (logger.isDebugEnabled()) {
            logger.debug("get all monitorItem");
        }
        return monitorService.getAllMonitorItem();
    }

    @GetMapping("/monitorItem/get/{deviceId}")
    @ResponseBody
    public CopsecResult getMonitorItemByDeviceId(@PathVariable("deviceId") String deviceId) {
        return monitorService.getMonitorItemByDeviceId(deviceId);
    }

    @NonNull
    @PostMapping("/monitorItem/add")
    @ResponseBody
    public CopsecResult addMonitorItem(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorItemBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("add monitorItem config {}", bean);
        }

        String filePath = config.getBasePath() + config.getMonitorItemPath();
        return monitorService.addMonitorItem(userInfo, request.getRemoteHost(), bean, filePath);
    }

    @NonNull
    @PostMapping("/monitorItem/update")
    @ResponseBody
    public CopsecResult updateMonitorItem(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorItemBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("update monitorItem config {}", bean);
        }

        String filePath = config.getBasePath() + config.getMonitorItemPath();
        return monitorService.updateMonitorItem(userInfo, request.getRemoteHost(), bean, filePath);
    }

    @NonNull
    @PostMapping("/monitorItem/delete")
    @ResponseBody
    public CopsecResult deleteMonitorItem(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorItemBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete monitorItem config {}", bean);
        }

        String filePath = config.getBasePath() + config.getMonitorItemPath();
        return monitorService.deleteMonitorItem(userInfo, request.getRemoteHost(), bean.getMonitorId(), filePath);
    }

    @NonNull
    @PostMapping("/monitorItem/deleteCheck")
    @ResponseBody
    public CopsecResult deleteMonitorItemList(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestParam("jsonStr") String jsonStr) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete monitorItemList config {}", jsonStr);
        }
        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        String filePath = config.getBasePath() + config.getMonitorItemPath();
        return monitorService.deleteMonitorItemList(userInfo, request.getRemoteHost(), idArray, filePath);
    }

    @PostMapping("/monitorGroup/get")
    @ResponseBody
    public CopsecResult gatAllMonitorGroup() {
        if (logger.isDebugEnabled()) {
            logger.debug("get all monitorGroup");
        }
        return monitorService.getAllMonitorGroup();
    }

    @NonNull
    @PostMapping("/monitorGroup/add")
    @ResponseBody
    public CopsecResult addMonitorGroup(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorGroupBean bean, @RequestParam("jsonStr") String jsonStr) {
        if (logger.isDebugEnabled()) {
            logger.debug("add monitorGroup config {}", bean);
        }
        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        String filePath = config.getBasePath() + config.getMonitorGroupPath();
        return monitorService.addMonitorGroup(userInfo, request.getRemoteHost(), bean, idArray, filePath);
    }

    @NonNull
    @PostMapping("/monitorGroup/update")
    @ResponseBody
    public CopsecResult updateMonitorGroup(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorGroupBean bean, @RequestParam("jsonStr") String jsonStr) {
        if (logger.isDebugEnabled()) {
            logger.debug("update monitorGroup config {}", bean);
        }
        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        String filePath = config.getBasePath() + config.getMonitorGroupPath();
        return monitorService.updateMonitorGroup(userInfo, request.getRemoteHost(), bean, idArray, filePath);
    }

    @NonNull
    @PostMapping("/monitorGroup/delete")
    @ResponseBody
    public CopsecResult deleteMonitorGroup(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorGroupBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete monitorGroup config {}", bean);
        }

        String filePath = config.getBasePath() + config.getMonitorGroupPath();
        return monitorService.deleteMonitorGroup(userInfo, request.getRemoteHost(), bean.getId(), filePath);
    }

    @NonNull
    @PostMapping("/monitorGroup/deleteCheck")
    @ResponseBody
    public CopsecResult deleteMonitorGroupList(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestParam("jsonStr") String jsonStr) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete monitorGroupList config {}", jsonStr);
        }
        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        String filePath = config.getBasePath() + config.getMonitorGroupPath();
        return monitorService.deleteMonitorGroupList(userInfo, request.getRemoteHost(), idArray, filePath);
    }

    @PostMapping("/warningItem/get")
    @ResponseBody
    public CopsecResult gatAllWarningItem() {
        if (logger.isDebugEnabled()) {
            logger.debug("get all warningItem");
        }
        return monitorService.getAllWarningItem();
    }

    @NonNull
    @PostMapping("/warningItem/add")
    @ResponseBody
    public CopsecResult addWarningItem(@SessionAttribute UserBean userInfo, HttpServletRequest request, WarningItemBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("add warningItem config {}", bean);
        }

        String filePath = config.getBasePath() + config.getWarningItemPath();
        return monitorService.addWarningItem(userInfo, request.getRemoteHost(), bean, filePath);
    }

    @NonNull
    @PostMapping("/warningItem/update")
    @ResponseBody
    public CopsecResult updateWarningItem(@SessionAttribute UserBean userInfo, HttpServletRequest request, WarningItemBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("update warningItem config {}", bean);
        }

        String filePath = config.getBasePath() + config.getWarningItemPath();
        return monitorService.updateWarningItem(userInfo, request.getRemoteHost(), bean, filePath);
    }

    @NonNull
    @PostMapping("/warningItem/delete")
    @ResponseBody
    public CopsecResult deleteWarningItem(@SessionAttribute UserBean userInfo, HttpServletRequest request, WarningItemBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete warningItem config {}", bean);
        }

        String filePath = config.getBasePath() + config.getWarningItemPath();
        return monitorService.deleteWarningItem(userInfo, request.getRemoteHost(), bean.getWarningId(), filePath);
    }

    @NonNull
    @PostMapping("/warningItem/deleteCheck")
    @ResponseBody
    public CopsecResult deleteWarningItemList(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestParam("jsonStr") String jsonStr) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete warningItemList config {}", jsonStr);
        }
        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        String filePath = config.getBasePath() + config.getWarningItemPath();
        return monitorService.deleteWarningItemList(userInfo, request.getRemoteHost(), idArray, filePath);
    }

    @PostMapping("/monitorTask/get")
    @ResponseBody
    public CopsecResult gatAllMonitorTask() {
        if (logger.isDebugEnabled()) {
            logger.debug("get all monitorTask");
        }
        return monitorService.getAllMonitorTask();
    }

    @NonNull
    @PostMapping("/monitorTask/add")
    @ResponseBody
    public CopsecResult addMonitorTask(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorTaskBean bean, @RequestParam("jsonStr") String jsonStr) {
        if (logger.isDebugEnabled()) {
            logger.debug("add monitorTask config {}", bean);
        }
        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        String filePath = config.getBasePath() + config.getMonitorTaskPath();
        return monitorService.addMonitorTask(userInfo, request.getRemoteHost(), bean, idArray, filePath);
    }

    @NonNull
    @PostMapping("/monitorTask/update")
    @ResponseBody
    public CopsecResult updateMonitorTask(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorTaskBean bean, @RequestParam("jsonStr") String jsonStr) {
        if (logger.isDebugEnabled()) {
            logger.debug("update monitorTask config {}", bean);
        }
        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        String filePath = config.getBasePath() + config.getMonitorTaskPath();
        return monitorService.updateMonitorTask(userInfo, request.getRemoteHost(), bean, idArray, filePath);
    }

    @NonNull
    @PostMapping("/monitorTask/delete")
    @ResponseBody
    public CopsecResult deleteMonitorTask(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorTaskBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete monitorTask config {}", bean);
        }

        String filePath = config.getBasePath() + config.getMonitorTaskPath();
        return monitorService.deleteMonitorTask(userInfo, request.getRemoteHost(), bean.getTaskId(), filePath);
    }

    @NonNull
    @PostMapping("/monitorTask/deleteCheck")
    @ResponseBody
    public CopsecResult deleteMonitorTaskList(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestParam("jsonStr") String jsonStr) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete monitorTaskList config {}", jsonStr);
        }
        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        String filePath = config.getBasePath() + config.getMonitorTaskPath();
        return monitorService.deleteMonitorTaskList(userInfo, request.getRemoteHost(), idArray, filePath);
    }
}
