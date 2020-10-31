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

        String filePath = config.getBasePath() + config.getMonitorItemPath();
        return monitorService.addMonitorItem(userInfo, request.getRemoteHost(), bean, filePath);
    }

    @NonNull
    @PostMapping("/monitorItem/update")
    @ResponseBody
    public CopsecResult updateMonitorItem(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorItemBean bean) {

        String filePath = config.getBasePath() + config.getMonitorItemPath();
        return monitorService.updateMonitorItem(userInfo, request.getRemoteHost(), bean, filePath);
    }

    @NonNull
    @PostMapping("/monitorItem/delete")
    @ResponseBody
    public CopsecResult deleteMonitorItem(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorItemBean bean) {

        String filePath = config.getBasePath() + config.getMonitorItemPath();
        return monitorService.deleteMonitorItem(userInfo, request.getRemoteHost(), bean.getMonitorId(), filePath);
    }

    @NonNull
    @PostMapping("/monitorItem/deleteCheck")
    @ResponseBody
    public CopsecResult deleteMonitorItemList(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestParam("jsonStr") String jsonStr) {
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
    public CopsecResult addMonitorGroup(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorGroupBean bean) {
        return monitorService.addMonitorGroup(userInfo, request.getRemoteHost(), bean, "");
    }

    @NonNull
    @PostMapping("/monitorGroup/update")
    @ResponseBody
    public CopsecResult updateMonitorGroup(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorGroupBean bean) {

        return monitorService.updateMonitorGroup(userInfo, request.getRemoteHost(), bean, "");
    }

    @NonNull
    @PostMapping("/monitorGroup/delete")
    @ResponseBody
    public CopsecResult deleteMonitorGroup(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorGroupBean bean) {

        return monitorService.deleteMonitorGroup(userInfo, request.getRemoteHost(), bean.getId(), "");
    }

    @NonNull
    @PostMapping("/monitorGroup/deleteCheck")
    @ResponseBody
    public CopsecResult deleteMonitorGroupList(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestParam("jsonStr") String jsonStr) {

        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        return monitorService.deleteMonitorGroupList(userInfo, request.getRemoteHost(), idArray, "");
    }

    @PostMapping("/warningItem/get")
    @ResponseBody
    public CopsecResult gatAllWarningItem() {

        return monitorService.getAllWarningItem();
    }

    @NonNull
    @PostMapping("/warningItem/add")
    @ResponseBody
    public CopsecResult addWarningItem(@SessionAttribute UserBean userInfo, HttpServletRequest request, WarningItemBean bean) {

        return monitorService.addWarningItem(userInfo, request.getRemoteHost(), bean, "");
    }

    @NonNull
    @PostMapping("/warningItem/update")
    @ResponseBody
    public CopsecResult updateWarningItem(@SessionAttribute UserBean userInfo, HttpServletRequest request, WarningItemBean bean) {

        return monitorService.updateWarningItem(userInfo, request.getRemoteHost(), bean, "");
    }

    @NonNull
    @PostMapping("/warningItem/delete")
    @ResponseBody
    public CopsecResult deleteWarningItem(@SessionAttribute UserBean userInfo, HttpServletRequest request, WarningItemBean bean) {

        return monitorService.deleteWarningItem(userInfo, request.getRemoteHost(), bean.getWarningId(), "");
    }

    @NonNull
    @PostMapping("/warningItem/deleteCheck")
    @ResponseBody
    public CopsecResult deleteWarningItemList(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestParam("jsonStr") String jsonStr) {

        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        return monitorService.deleteWarningItemList(userInfo, request.getRemoteHost(), idArray, "");
    }

    @PostMapping("/monitorTask/get")
    @ResponseBody
    public CopsecResult gatAllMonitorTask() {

        return monitorService.getAllMonitorTask();
    }

    @NonNull
    @PostMapping("/monitorTask/add")
    @ResponseBody
    public CopsecResult addMonitorTask(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorTaskBean bean) {

        return monitorService.addMonitorTask(userInfo, request.getRemoteHost(), bean, "");
    }

    @NonNull
    @PostMapping("/monitorTask/update")
    @ResponseBody
    public CopsecResult updateMonitorTask(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorTaskBean bean) {

        return monitorService.updateMonitorTask(userInfo, request.getRemoteHost(), bean, "");
    }

    @NonNull
    @PostMapping("/monitorTask/delete")
    @ResponseBody
    public CopsecResult deleteMonitorTask(@SessionAttribute UserBean userInfo, HttpServletRequest request, MonitorTaskBean bean) {

        return monitorService.deleteMonitorTask(userInfo, request.getRemoteHost(), bean.getTaskId(), "");
    }

    @NonNull
    @PostMapping("/monitorTask/deleteCheck")
    @ResponseBody
    public CopsecResult deleteMonitorTaskList(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestParam("jsonStr") String jsonStr) {

        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        return monitorService.deleteMonitorTaskList(userInfo, request.getRemoteHost(), idArray, "");
    }
}
