package com.copsec.monitor.web.controller;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.pools.UserPools;
import com.copsec.monitor.web.service.SystemService;
import com.copsec.monitor.web.utils.RondomCodeUtils;
import com.copsec.monitor.web.utils.logUtils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 登录 退出 验证码 菜单等操作
 */
@Controller
public class CommonController {

    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private SystemConfig config;

    @Autowired
    private SystemService systemService;

    @RequestMapping(value = {"/"})
    public String toLogin() {
        return "login";
    }

    @PostMapping(value = "/login")
    public String login(UserBean userInfo, HttpServletRequest request, Model model) {
        if (logger.isDebugEnabled()) {
            logger.debug("user is going to login {}", userInfo);
        }

        CopsecResult result = systemService.login(userInfo, request.getRemoteAddr());
        if (result.getCode() == CopsecResult.FALIED_CODE) {
            model.addAttribute("message", result.getMessage());
            return "login";
        }
        
        UserBean userBean = (UserBean) result.getData();
        HttpSession session = request.getSession();
        session.setAttribute("userInfo", userBean);

        switch (userBean.getRole())
        {
            case "systemAdmin":
                return "redirect:node/device";
            case "auditAdmin":
                return "redirect:system/operateLog";
            case "monitorAdmin":
                return "redirect:node/deviceMonitor";
        }
        return "redirect:node/device";
    }

    /**
     * 无会话操作，请求验证码
     *
     * @return
     */
    @PostMapping(value = "/createRandomCode")
    @ResponseBody
    public String getCode() {
        return RondomCodeUtils.getCode(4);
    }

    /**
     * 退出登录操作
     *
     * @param userInfo
     * @param status   session状态
     * @return 跳转到登录界面
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(@SessionAttribute UserBean userInfo, SessionStatus status, HttpServletRequest request) {
        LogUtils.sendSuccessLog(userInfo.getId(), request.getRemoteAddr(), "退出",
                config.getLogHost(), config.getLogPort(), config.getLogCollection(), "退出");
        status.setComplete();
        return "login";
    }

    @GetMapping(value = "/{id}")
    public String getMenu(@SessionAttribute UserBean userInfo, @PathVariable("id") String id, Model model) {
        model.addAttribute("userInfo", userInfo);
        return id;
    }

    @GetMapping(value = "/cms")
    public String gotoMonitorPage(String id, String key, HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("from another page to monitor page with id - {} and key - {}", id, key);
        }
        UserBean userBean = UserPools.getInstances().get(id);
        if (ObjectUtils.isEmpty(userBean)) {
            LogUtils.sendFailLog(id, request.getRemoteAddr(), "登录失败,用户不存在", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登录");
            return "error/403";
        }

        if (userBean.getPassword().equals(key)) {
            LogUtils.sendSuccessLog(id, request.getRemoteAddr(), "登录成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登录");
            HttpSession session = request.getSession();
            session.setAttribute("userInfo", userBean);
            return "redirect:system/systemCMS";
        }

        return "error/404";
    }
}
