package com.copsec.monitor.web.controller;

import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.exception.CopsecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
/**
 * 系统配置，全局异常处理
 */
public class BaseController {
    @Autowired
    private SystemConfig config;

    @ModelAttribute(name = "system")
    public SystemConfig getConfig() {
        return config;
    }

    @ExceptionHandler(value = {ServletRequestBindingException.class, HttpSessionRequiredException.class})
    public String noSession(final RedirectAttributes attr) {

        attr.addFlashAttribute("error", "yes");
        attr.addFlashAttribute("message", "会话已过期，请重新登录");
        return "redirect:/";
    }

    @ExceptionHandler(value = {CopsecException.class})
    public String fileNotFound() {
        return "/404";
    }
}
