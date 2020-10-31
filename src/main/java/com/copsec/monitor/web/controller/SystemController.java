package com.copsec.monitor.web.controller;

import com.alibaba.fastjson.JSON;
import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.service.SystemService;
import com.copsec.monitor.web.utils.ExportUtils;
import com.copsec.monitor.web.utils.FormatUtils;
import com.copsec.monitor.web.utils.PageUtils;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = {"/system"})
@SessionAttributes(value = {"userInfo"})
public class SystemController {
    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private SystemService systemService;

    @Autowired
    private SystemConfig config;


    @GetMapping(value = {"/{pageId}"})
    public String toPage(@SessionAttribute UserBean userInfo, @PathVariable("pageId") String pageId) {
        return "system/" + pageId;
    }

    /**
     * 更新管理员密码
     *
     * @param userInfo
     * @param bean
     * @param request
     * @return
     */
    @PostMapping(value = {"code/update"})
    @ResponseBody
    public CopsecResult updatePassword(@SessionAttribute UserBean userInfo, SessionStatus status, UserBean bean, HttpServletRequest request) {
        CopsecResult result = systemService.passwordReset(userInfo, bean.getPassword(), bean.getNewCode(), request.getRemoteHost());
        if (result.getCode() == 200) {
            status.setComplete();
        }
        return result;
    }

    @PostMapping("/userInfo/get")
    @ResponseBody
    public CopsecResult gatAllUserInfo() {

        return systemService.getAllUserInfo();
    }

    @NonNull
    @PostMapping("/userInfo/add")
    @ResponseBody
    public CopsecResult addUserInfo(@SessionAttribute UserBean userInfo, HttpServletRequest request, UserInfoBean bean) {

        return systemService.addUserInfo(userInfo, request.getRemoteHost(), bean, "");
    }

    @NonNull
    @PostMapping("/userInfo/update")
    @ResponseBody
    public CopsecResult updateUserInfo(@SessionAttribute UserBean userInfo, HttpServletRequest request, UserInfoBean bean) {

        return systemService.updateUserInfo(userInfo, request.getRemoteHost(), bean, "");
    }

    @NonNull
    @PostMapping("/userInfo/delete")
    @ResponseBody
    public CopsecResult deleteUserInfo(@SessionAttribute UserBean userInfo, HttpServletRequest request, UserInfoBean bean) {

        return systemService.deleteUserInfo(userInfo, request.getRemoteHost(), bean.getUserId(), "");
    }

    @NonNull
    @PostMapping("/userInfo/deleteCheck")
    @ResponseBody
    public CopsecResult deleteUserInfoList(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestParam("jsonStr") String jsonStr) {

        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        return systemService.deleteUserInfoList(userInfo, request.getRemoteHost(), idArray, "");
    }

    @PostMapping("/operateLog/search")
    @ResponseBody
    public Object searchOperateLog(LogConditionBean condition) {

        return PageUtils.returnResult(condition, systemService.searchOperateLog(PageUtils.returnPageable(condition), condition));
    }

    @PostMapping(value = "/operateLog/deleteCheck")
    @ResponseBody
    public CopsecResult deleteCheckOperateLog(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestBody List<String> idArray) {

        return systemService.deleteCheckOperateLog(userInfo, request.getRemoteHost(), idArray);
    }

    @PostMapping("/operateLog/deleteAll")
    @ResponseBody
    public CopsecResult deleteAllLog(@SessionAttribute UserBean userInfo, HttpServletRequest request) {

        return systemService.deleteAllLog(userInfo, request.getRemoteHost());
    }

    @PostMapping("/operateLog/export")
    @ResponseBody
    public ResponseEntity<Resource> exportAllLogs(LogConditionBean condition) {

        HttpStatus status = HttpStatus.CREATED;
        Page<LogConditionBean> logs = systemService.searchOperateLog(PageUtils.returnPageable(condition), condition);
        String fileName = config.getBasePath() + File.separator + "operateLog.xls";
        ExportUtils.makeExcel(logs.getContent(), fileName);

        File file = new File(fileName);
        FileSystemResource fileSource = new FileSystemResource(file);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.setContentDispositionFormData("attachement", "operateLog[" + FormatUtils.getFormatDate(new Date()) + "].xls");
        header.setContentLength(file.length());
        header.setExpires(0);
        header.set("Set-Cookie", "fileDownload=true; path=/");

        return new ResponseEntity<>(fileSource, header, status);
    }

    @PostMapping("/operateLog/file")
    @ResponseBody
    public CopsecResult deleteExportFile(LogConditionBean condition) {
        String fileName = config.getBasePath() + File.separator + condition.getFileName() + ".xls";
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        return CopsecResult.success();
    }
}
