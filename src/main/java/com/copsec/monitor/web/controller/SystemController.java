package com.copsec.monitor.web.controller;

import com.alibaba.fastjson.JSON;
import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.service.SystemService;
import com.copsec.monitor.web.utils.ExportUtils;
import com.copsec.monitor.web.utils.PageUtils;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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
    public CopsecResult updatePassword(@SessionAttribute UserBean userInfo,
                                       UserBean bean, HttpServletRequest request) {

        return systemService.passwordReset(userInfo, bean.getPassword(),
                bean.getNewCode(), request.getRemoteHost());
    }

    @PostMapping("/userInfo/get")
    @ResponseBody
    public CopsecResult gatAllUserInfo() {
        if (logger.isDebugEnabled()) {
            logger.debug("get all userInfo");
        }

        return systemService.getAllUserInfo();
    }

    @NonNull
    @PostMapping("/userInfo/add")
    @ResponseBody
    public CopsecResult addUserInfo(@SessionAttribute UserBean userInfo, HttpServletRequest request, UserInfoBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("add userInfo config {}", bean);
        }

        String filePath = config.getBasePath() + config.getUserInfoPath();
        return systemService.addUserInfo(userInfo, request.getRemoteHost(), bean, filePath);
    }

    @NonNull
    @PostMapping("/userInfo/update")
    @ResponseBody
    public CopsecResult updateUserInfo(@SessionAttribute UserBean userInfo, HttpServletRequest request, UserInfoBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("update userInfo config {}", bean);
        }

        String filePath = config.getBasePath() + config.getUserInfoPath();
        return systemService.updateUserInfo(userInfo, request.getRemoteHost(), bean, filePath);
    }

    @NonNull
    @PostMapping("/userInfo/delete")
    @ResponseBody
    public CopsecResult deleteUserInfo(@SessionAttribute UserBean userInfo, HttpServletRequest request, UserInfoBean bean) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete userInfo config {}", bean);
        }

        String filePath = config.getBasePath() + config.getUserInfoPath();
        return systemService.deleteUserInfo(userInfo, request.getRemoteHost(), bean.getUserId(), filePath);
    }

    @NonNull
    @PostMapping("/userInfo/deleteCheck")
    @ResponseBody
    public CopsecResult deleteUserInfoList(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestParam("jsonStr") String jsonStr) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete userInfoList config {}", jsonStr);
        }
        List<String> idArray = JSON.parseArray(jsonStr, String.class);

        String filePath = config.getBasePath() + config.getUserInfoPath();
        return systemService.deleteUserInfoList(userInfo, request.getRemoteHost(), idArray, filePath);
    }

    @PostMapping("/operateLog/search")
    @ResponseBody
    public Object searchOperateLog(LogConditionBean condition) {
        if (logger.isDebugEnabled()) {
            logger.debug("search operateLog by condition {}", condition);
        }

        return PageUtils.returnResult(condition, systemService.searchOperateLog(PageUtils.returnPageable(condition), condition));
    }

    @PostMapping(value = "/operateLog/deleteCheck")
    @ResponseBody
    public CopsecResult deleteCheckOperateLog(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestBody List<String> idArray) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteCheck operateLog by {}", idArray);
        }
        return systemService.deleteCheckOperateLog(userInfo, request.getRemoteHost(), idArray);
    }

    @PostMapping("/operateLog/deleteAll")
    @ResponseBody
    public CopsecResult deleteAllLog(@SessionAttribute UserBean userInfo, HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete all audit log by user {}", userInfo.getId());
        }
        return systemService.deleteAllLog(userInfo, request.getRemoteHost());
    }

    @PostMapping("/operateLog/export")
    @ResponseBody
    public ResponseEntity<Resource> exportAllLogs(LogConditionBean condition, HttpServletResponse response) {
        if (logger.isDebugEnabled()) {
            logger.debug("export current operateLog");
        }

        HttpStatus status = HttpStatus.CREATED;
        Page<LogConditionBean> logs = systemService.searchOperateLog(PageUtils.returnPageable(condition), condition);
        String fileName = config.getBackupFilePath() + File.separator + "logs-" + condition.getFileName() + ".xls";
        ExportUtils.makeExcel(logs.getContent(), fileName, response);

        File file = new File(fileName);
        FileSystemResource fileSource = new FileSystemResource(file);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.setContentDispositionFormData("attachement", System.currentTimeMillis() + "auditLog.xls");
        header.setContentLength(file.length());
        header.setExpires(0);
        header.set("Set-Cookie", "fileDownload=true; path=/");

        return new ResponseEntity<>(fileSource, header, status);
    }

    @GetMapping("/operateLog/file/{id}")
    @ResponseBody
    public CopsecResult deleteExportFile(@PathVariable("id") String id) {
        String fileName = config.getBackupFilePath() + File.separator + "logs-" + id + ".xls";
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        return CopsecResult.success();
    }

    @PostMapping(value = "/server/logs")
    @ResponseBody
    public Page<LogConditionBean> getServerMessage(@SessionAttribute UserBean userInfo, LogConditionBean condition) {
        if (logger.isDebugEnabled()) {
            logger.debug("search server logs by condition {}", condition);
        }
        Pageable pageable = null;
        if (!ObjectUtils.isEmpty(condition.getPage()) && !ObjectUtils.isEmpty(condition.getSize())) {
            pageable = PageRequest.of(condition.getPage(), condition.getSize());
        } else {
            pageable = PageRequest.of(0, 10);
        }
        return systemService.getServerMessage(condition, pageable);
    }
}
