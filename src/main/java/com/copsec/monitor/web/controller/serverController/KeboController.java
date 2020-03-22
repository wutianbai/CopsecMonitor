package com.copsec.monitor.web.controller.serverController;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.remote.RemoteOperateBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.service.serverService.KeboService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Objects;

/**
 * 操作科博设备
 */
@Controller
@RequestMapping("/kebo")
@SessionAttributes(value = {"userInfo"})
public class KeboController {

    private static final Logger logger = LoggerFactory.getLogger(KeboController.class);

    @Autowired
    private KeboService keboService;

    @GetMapping("/path")
    public String getPath(@SessionAttribute UserBean userInfo, String name) {

        if (logger.isDebugEnabled()) {

            logger.debug(" get path of kebo page {}", name);
        }
        if (name.contains("&_")) {

            name = name.substring(0, name.indexOf("&"));
        }
        return "kebo/" + name;
    }

    @PostMapping(value = "/execute/method")
    @ResponseBody
    public CopsecResult executeRemoteMethod(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestBody RemoteOperateBean operateBean) {

        if (logger.isDebugEnabled()) {

            logger.debug("execute remote method with params {}", Objects.toString(operateBean));
        }
        HashMap<String, String> params = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(operateBean.getParams())) {

            operateBean.getParams().stream().filter(d -> {

                if (d.split("=").length == 2) {

                    return true;
                }
                return false;
            }).forEach(param -> {

                params.put(param.split("=")[0], param.split("=")[1]);
            });
        }
        if (!ObjectUtils.isEmpty(operateBean.getTaskName())) {

            params.put("taskName", operateBean.getTaskName());
        }
        CopsecResult result = keboService.executeRemoteMethod(userInfo, request.getRemoteHost(), operateBean, params);

        return result;
    }
}
