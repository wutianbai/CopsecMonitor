package com.copsec.monitor.web.controller;

import com.alibaba.fastjson.JSON;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.warning.Report;
import com.copsec.monitor.web.beans.warning.WarningEventBean;
import com.copsec.monitor.web.beans.warning.WarningHistoryBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.service.WarningService;
import com.copsec.monitor.web.utils.PageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = {"/warning"})
@SessionAttributes(value = {"userInfo"})
public class WarningController {
    private static final Logger logger = LoggerFactory.getLogger(WarningController.class);

    @Autowired
    private WarningService warningService;

    @PostMapping(value = "/event/search")
    @ResponseBody
    public Object searchWarningEvent(@SessionAttribute UserBean userInfo, HttpServletRequest request, WarningEventBean condition) {
        if (logger.isDebugEnabled()) {
            logger.debug("search warningEvent by {}", condition);
        }

        return PageUtils.returnResult(condition, warningService.searchWarningEvent(userInfo, request.getRemoteHost(), PageUtils.returnPageable(condition), condition));
    }

    @PostMapping(value = "/event/handle")
    @ResponseBody
    public CopsecResult handleWarningEvent(@SessionAttribute UserBean userInfo, HttpServletRequest request, WarningEventBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("handle warningEvent by {}", bean);
        }
        return warningService.handleWarningEvent(userInfo, request.getRemoteHost(), bean);
    }

    @PostMapping(value = "/event/delete")
    @ResponseBody
    public CopsecResult deleteWarningEvent(@SessionAttribute UserBean userInfo, HttpServletRequest request, WarningEventBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete warningEvent by {}", bean);
        }
        return warningService.deleteWarningEvent(userInfo, request.getRemoteHost(), bean);
    }

    @PostMapping(value = "/event/handleCheck")
    @ResponseBody
    public CopsecResult handleCheckWarningEvent(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestBody List<String> idArray) {
        if (logger.isDebugEnabled()) {
            logger.debug("handleCheck warningEvent by {}", idArray);
        }
        return warningService.handleCheckWarningEvent(userInfo, request.getRemoteHost(), idArray);
    }

    @PostMapping(value = "/event/deleteCheck")
    @ResponseBody
    public CopsecResult deleteCheckWarningEvent(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestBody List<String> idArray) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteCheck warningEvent by {}", idArray);
        }
        return warningService.deleteCheckWarningEvent(userInfo, request.getRemoteHost(), idArray);
    }

    @PostMapping(value = "/event/handleAll")
    @ResponseBody
    public CopsecResult handleAllWarningEvent(@SessionAttribute UserBean userInfo, HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("handle all warningEvent");
        }
        return warningService.handleAllWarningEvent(userInfo, request.getRemoteHost());
    }

    @PostMapping(value = "/history/search")
    @ResponseBody
    public Object searchWarningHistory(@SessionAttribute UserBean userInfo, HttpServletRequest request, WarningHistoryBean condition) {
        if (logger.isDebugEnabled()) {
            logger.debug("search warningHistory by {}", condition);
        }

        return PageUtils.returnResult(condition, warningService.searchWarningHistory(userInfo, request.getRemoteHost(), PageUtils.returnPageable(condition), condition));
    }

    @PostMapping(value = "/history/delete")
    @ResponseBody
    public CopsecResult deleteWarningHistory(@SessionAttribute UserBean userInfo, HttpServletRequest request, WarningHistoryBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete warningHistory by {}", bean);
        }
        return warningService.deleteWarningHistory(userInfo, request.getRemoteHost(), bean);
    }

    @PostMapping(value = "/history/deleteCheck")
    @ResponseBody
    public CopsecResult deleteCheckWarningHistory(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestBody List<String> idArray) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteCheck warningHistory by {}", idArray);
        }
        return warningService.deleteCheckWarningHistory(userInfo, request.getRemoteHost(), idArray);
    }

    @PostMapping(value = "/event/receive")
    @ResponseBody
    public CopsecResult receiveWarningEvent(String report) {
        System.err.println("receive warningEvent by {}" + report);
        Report reports = JSON.parseObject(report, Report.class);
        if (logger.isDebugEnabled()) {
            logger.debug("receive warningEvent by {}", report);
        }
        warningService.receiveWarningEvent(reports);
        return CopsecResult.success();
    }
}
