package com.copsec.monitor.web.controller;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Link;
import com.copsec.monitor.web.beans.node.LinkBean;
import com.copsec.monitor.web.beans.node.PositionBeans;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.service.DeviceService;
import com.copsec.monitor.web.utils.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;

@Controller
@RequestMapping("/node")
@SessionAttributes(value = {"userInfo"})
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;

    @GetMapping(value = {"/{pageId}"})
    public String toPage(@SessionAttribute UserBean userInfo, @PathVariable("pageId") String pageId) {
        return "nodes/" + pageId;
    }

    /**
     * 获取所有节点数据
     */
    @GetMapping("/get")
    @ResponseBody
    public CopsecResult getData() {
        return deviceService.getData();
    }

    /**
     * 添加设备
     *
     * @param device
     * @param userInfo
     * @return
     */
    @PostMapping("/device/add")
    @ResponseBody
    public CopsecResult addDevice(Device device, @SessionAttribute UserBean userInfo, HttpServletRequest request) {

        return deviceService.addDevice(userInfo, request.getRemoteHost(), device);
    }

    /**
     * @param device
     * @param userInfo
     * @return
     */
    @PostMapping("/device/update")
    @ResponseBody
    public CopsecResult updateDevice(Device device, @SessionAttribute UserBean userInfo, HttpServletRequest request) {

        return deviceService.updateDevice(userInfo, request.getRemoteHost(), device);
    }

    /**
     * @param device
     * @param userInfo
     * @return
     */
    @PostMapping("/device/delete")
    @ResponseBody
    public CopsecResult deleteDevice(Device device, @SessionAttribute UserBean userInfo, HttpServletRequest request) {

        return deviceService.deleteDevice(userInfo, request.getRemoteHost(), device.getData().getDeviceId());
    }

    @PostMapping("/link/add")
    @ResponseBody
    public CopsecResult addLink(@RequestBody LinkBean link, @SessionAttribute UserBean userInfo, HttpServletRequest request) {

        return deviceService.addLink(userInfo, request.getRemoteHost(), link);
    }

    /**
     * @param link
     * @param userInfo
     * @return
     */
    @PostMapping("/link/update")
    @ResponseBody
    public CopsecResult updateLink(@RequestBody LinkBean link, @SessionAttribute UserBean userInfo, HttpServletRequest request) {

        return deviceService.updateLink(userInfo, request.getRemoteHost(), link);
    }

    /**
     * @param link
     * @param userInfo
     * @return
     */
    @PostMapping("/link/delete")
    @ResponseBody
    public CopsecResult deleteLink(Link link, @SessionAttribute UserBean userInfo, HttpServletRequest request) {

        return deviceService.deleteLink(userInfo, request.getRemoteHost(), link.getData().getId());
    }

    /**
     * 获取网络所有区域
     *
     * @return
     */
    @GetMapping(value = "/zone/get")
    @ResponseBody
    public CopsecResult getAllZone() {

        return deviceService.getAllZone();
    }

    /**
     * 添加网络区域
     *
     * @param userInfo
     * @param request
     * @param zone
     * @return
     */
    @PostMapping(value = "/zone/add")
    @ResponseBody
    public CopsecResult addZone(@SessionAttribute UserBean userInfo, HttpServletRequest request, Device zone) {

        return deviceService.addZone(userInfo, request.getRemoteHost(), zone);
    }

    /**
     * 删除网络区域
     *
     * @param zone
     * @param userInfo
     * @param request
     * @return
     */
    @PostMapping("/zone/delete")
    @ResponseBody
    public CopsecResult deleteZone(Device zone, @SessionAttribute UserBean userInfo, HttpServletRequest request) {

        return deviceService.deleteZone(userInfo, request.getRemoteHost(), zone.getData().getId());
    }

    /**
     * 更新网络区域
     *
     * @param zone
     * @param userInfo
     * @param request
     * @return
     */
    @PostMapping("/zone/update")
    @ResponseBody
    public CopsecResult updateZone(Device zone, @SessionAttribute UserBean userInfo, HttpServletRequest request) {

        return deviceService.updateZone(userInfo, request.getRemoteHost(), zone);
    }

    @PostMapping("/topology/update")
    @ResponseBody
    public CopsecResult updateTopology(@SessionAttribute UserBean userInfo, @RequestBody ArrayList<PositionBeans> positions) {

        if (!userInfo.getRole().equals("monitor")) {
            return deviceService.updateTopology(positions);
        }
        return CopsecResult.success();
    }

    @GetMapping("/status/{time}")
    @ResponseBody
    public CopsecResult getStatusInfo(@PathVariable("time") String time) {

        return deviceService.getDeviceStatus();
    }
}
