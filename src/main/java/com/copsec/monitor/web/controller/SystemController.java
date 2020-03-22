package com.copsec.monitor.web.controller;

import com.alibaba.fastjson.JSON;
import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.beans.UploadFileBean;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.flume.FlumeServiceStatus;
import com.copsec.monitor.web.beans.network.NetConfigBean;
import com.copsec.monitor.web.beans.network.NetPortBondBean;
import com.copsec.monitor.web.beans.network.NetworkTimingBean;
import com.copsec.monitor.web.beans.network.RouterBean;
import com.copsec.monitor.web.beans.remote.RemoteDeviceBean;
import com.copsec.monitor.web.beans.remote.RemoteSessionBean;
import com.copsec.monitor.web.beans.statistics.ConditionBean;
import com.copsec.monitor.web.beans.syslogConf.SyslogConfigBean;
import com.copsec.monitor.web.beans.taskMonitor.FileSyncMonitorBean;
import com.copsec.monitor.web.beans.taskMonitor.FileSyncStatusBean;
import com.copsec.monitor.web.beans.taskMonitor.FileTaskSyncPolicyBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.CommonFileReader;
import com.copsec.monitor.web.fileReaders.LogSettingBeanReader;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.LogSettingPools;
import com.copsec.monitor.web.pools.RemoteDevicePools;
import com.copsec.monitor.web.pools.TaskPolicyPool;
import com.copsec.monitor.web.service.DeviceService;
import com.copsec.monitor.web.service.SystemService;
import com.copsec.monitor.web.utils.CopsecFileUtils;
import com.copsec.monitor.web.utils.ExportUtils;
import com.copsec.monitor.web.utils.PageUtils;
import com.google.common.collect.Lists;
import lombok.NonNull;
import net.lingala.zip4j.io.ZipOutputStream;
import org.apache.http.cookie.Cookie;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(value = {"/system"})
@SessionAttributes(value = {"userInfo"})
public class SystemController {
    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private SystemService systemService;

    @Autowired
    private SystemConfig config;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private LogSettingBeanReader logSettingReader;

    @Autowired
    private CommonFileReader commonFileReader;

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

    /**
     * 设置ssh 远程登录
     *
     * @param userInfo
     * @param status   start or forbidden
     * @param request
     * @return
     */
    @PostMapping(value = {"/ssh/set"})
    @ResponseBody
    public CopsecResult setSSHConfig(@SessionAttribute UserBean userInfo, String status, HttpServletRequest request) {
        String filePath = config.getBasePath() + config.getNetworkSSHPath();
        return systemService.setNetworkConfig(userInfo, request.getRemoteHost(), filePath, status, NetworkType.SSH);
    }

    /**
     * @param userInfo
     * @param status
     * @param request
     * @return
     */
    @PostMapping(value = {"/snmp/set"})
    @ResponseBody
    public CopsecResult setSNMPConfig(@SessionAttribute UserBean userInfo, String status, HttpServletRequest request) {
        String filePath = config.getBasePath() + config.getNetworkSNMPPath();
        return systemService.setNetworkConfig(userInfo, request.getRemoteHost(), filePath, status, NetworkType.SNMP);
    }

    /**
     * 执行restart 命令
     *
     * @param userInfo
     * @param request
     * @return
     */
    @PostMapping(value = {"/cmd/restart"})
    @ResponseBody
    public CopsecResult systemRestart(@SessionAttribute UserBean userInfo, HttpServletRequest request) {
        return systemService.systemControl(userInfo, request.getRemoteHost(), Resources.RESTART);
    }

    /**
     * 执行shutdown命令
     *
     * @param userInfo
     * @param request
     * @return
     */
    @PostMapping(value = {"/cmd/shutdown"})
    @ResponseBody
    public CopsecResult systemShutdown(@SessionAttribute UserBean userInfo, HttpServletRequest request) {
        return systemService.systemControl(userInfo, request.getRemoteHost(), Resources.SHUTDOWN);
    }

    /**
     * 获取系统控制模块配置信息
     *
     * @param userInfo
     * @param request
     * @return
     */
    @GetMapping(value = {"/get"})
    @ResponseBody
    public CopsecResult getSystemControlConfig(@SessionAttribute UserBean userInfo, HttpServletRequest request) {
        return systemService.getNetworkConfig(userInfo, request.getRemoteHost());
    }

    /**
     * @param userInfo
     * @param status
     * @param request
     * @return
     */
    @PostMapping("/retry/set")
    @ResponseBody
    public CopsecResult setRetryTimeConfig(@SessionAttribute UserBean userInfo, String status, HttpServletRequest request) {
        String filePath = config.getBasePath() + config.getRetryTimePath();
        return systemService.setNetworkConfig(userInfo, request.getRemoteHost(), filePath, status, NetworkType.LOGINTRYTIME);
    }

    /**
     * @param userInfo
     * @param status
     * @param request
     * @return
     */
    @PostMapping("/lock/set")
    @ResponseBody
    public CopsecResult setLockTimeConfig(@SessionAttribute UserBean userInfo, String status, HttpServletRequest request) {
        String filePath = config.getBasePath() + config.getLockTimePath();
        return systemService.setNetworkConfig(userInfo, request.getRemoteHost(), filePath, status, NetworkType.LOGINLOCKTIME);
    }

    @PostMapping("/ip/add")
    @ResponseBody
    public CopsecResult addIpConfig(@SessionAttribute UserBean userInfo, String ip, HttpServletRequest request) {
        return systemService.addIpConfig(userInfo, request.getRemoteHost(), ip);
    }

    @PostMapping("/ip/delete")
    @ResponseBody
    public CopsecResult deleteIpConfig(@SessionAttribute UserBean userInfo, String id, HttpServletRequest request) {
        return systemService.deleteIpConfig(userInfo, request.getRemoteHost(), id);
    }

    @PostMapping("/timing/server")
    @ResponseBody
    public CopsecResult timingSystemTime(@SessionAttribute UserBean userInfo, HttpServletRequest request, String currentTime) {
        String cmd = Resources.SETTIME + currentTime + "\"";
        if (logger.isDebugEnabled()) {
            logger.debug("change server date to {}", cmd);
        }
        return systemService.systemControl(userInfo, request.getRemoteHost(), cmd);
    }

    @PostMapping("/timing/network")
    @ResponseBody
    public CopsecResult setNetworkTiming(@SessionAttribute UserBean userInfo, HttpServletRequest request, NetworkTimingBean bean) {
        return systemService.setNetworkTiming(userInfo, request.getRemoteHost(), bean);
    }

    @PostMapping("/timing/local")
    @ResponseBody
    public CopsecResult setLocalTiming(@SessionAttribute UserBean userInfo, HttpServletRequest request, String status) {

        String filePath = config.getBasePath() + config.getTimingLocalPath();
        return systemService
                .setNetworkConfig(userInfo, request.getRemoteHost(),
                        filePath, status, NetworkType.LOCALTIMING);
    }

    @GetMapping("/config/backup")
    @ResponseBody
    public ResponseEntity<Resource> backupConfig(@SessionAttribute UserBean userInfo,
                                                 HttpServletRequest request) throws CopsecException {

        FileSystemResource resource = systemService.backupConfig(userInfo, request.getRemoteHost());
        HttpStatus status = HttpStatus.CREATED;
        if (ObjectUtils.isEmpty(resource)) {

            status = HttpStatus.BAD_REQUEST;
            throw new CopsecException("file not found");
        }
        String fileName = UriUtils.encode(resource.getFile().getName(), Charset.forName("UTF-8"));
        String headers = request.getHeader("User-Agent").toUpperCase();
        if (headers.contains("MSIE") || headers.contains("TRIDENT") || headers.contains("EDGE")) {

            fileName = fileName.replace("+", "%20");
            status = HttpStatus.OK;
        }
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.setContentDispositionFormData("attachement", fileName);
        header.setContentLength(resource.getFile().length());

        return new ResponseEntity<Resource>(resource, header, status);
    }

    @PostMapping("/config/upload")
    @ResponseBody
    public ResponseEntity<CopsecResult> uploadConfig(@SessionAttribute UserBean userInfo,
                                                     HttpServletRequest request, MultipartRequest fileRequest, UploadFileBean fileBean) throws CopsecException {

        logger.debug(fileBean.toString());

        CopsecResult result = null;
        try {

            Iterator<String> iterator = fileRequest.getFileNames();
            while (iterator.hasNext()) {

                String fileName = iterator.next();
                MultipartFile file = fileRequest.getFile(fileName);

                if (file.getBytes().length > 0) {

                    //小文件
                    if (ObjectUtils.isEmpty(fileBean.getChunk()) && ObjectUtils.isEmpty(fileBean.getChunks())) {


                        result = systemService.fileUploadSm(userInfo
                                , request.getRemoteHost(), file, Resources.LOADCONFIGFILE, fileBean);
                    } else { //大文件

                        result = systemService.fileUpload(userInfo
                                , request.getRemoteHost(), file, Resources.LOADCONFIGFILE, fileBean);

                    }
                }

            }
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            throw new CopsecException(e.getMessage());
        }
        if (result.getCode() == CopsecResult.FALIED_CODE) {

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
            return new ResponseEntity<CopsecResult>(result, header, status);
        }
        return null;
    }

    @PostMapping("/config/restore")
    public CopsecResult systemRestore(@SessionAttribute UserBean userInfo,
                                      HttpServletRequest request) {

        CopsecResult result = systemService.systemControl(userInfo
                , request.getRemoteHost(), config.getResetFilePath());
        if (result.getCode() == CopsecResult.SUCCESS_CODE) {

            systemService.getNetworkConfig(null, null);
        }
        return result;
    }

    @PostMapping("/upgrade/upload")
    @ResponseBody
    public ResponseEntity<CopsecResult> uploadPackage(@SessionAttribute UserBean userInfo,
                                                      HttpServletRequest request, MultipartRequest fileRequest, UploadFileBean fileBean) throws CopsecException {


        CopsecResult result = null;
        try {

            Iterator<String> iterator = fileRequest.getFileNames();
            while (iterator.hasNext()) {

                String fileName = iterator.next();
                MultipartFile file = fileRequest.getFile(fileName);

                if (file.getBytes().length > 0) {

                    //小文件
                    if (ObjectUtils.isEmpty(fileBean.getChunk()) && ObjectUtils.isEmpty(fileBean.getChunks())) {


                        result = systemService.fileUploadSm(userInfo
                                , request.getRemoteHost(), file, Resources.UPDATEFILE, fileBean);
                    } else { //大文件

                        result = systemService.fileUpload(userInfo
                                , request.getRemoteHost(), file, Resources.UPDATEFILE, fileBean);

                    }
                }

            }
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            throw new CopsecException(e.getMessage());
        }
        if (result.getCode() == CopsecResult.FALIED_CODE) {

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
            return new ResponseEntity<CopsecResult>(result, header, status);
        }
        return null;
    }

    @PostMapping("/upgrade/update")
    @ResponseBody
    public CopsecResult executeSystemUpgrade(@SessionAttribute UserBean userInfo,
                                             HttpServletRequest request, String packagename) {

        String cmd = config.getUpgradeFilePath() + config.getUpgradeShellPath() + " " + packagename;
        if (logger.isDebugEnabled()) {

            logger.debug("update command is {}", cmd);
        }
        return systemService.systemControl(userInfo, request.getRemoteHost(), cmd);
    }

    @PostMapping("/upgrade/delete")
    @ResponseBody
    public CopsecResult deleteSystemPackage(@SessionAttribute UserBean userInfo,
                                            HttpServletRequest request, String packagename) {

        return systemService.deletePackage(userInfo
                , request.getRemoteHost(), packagename);
    }

    @PostMapping("/network/add")
    @ResponseBody
    public CopsecResult addInterfaceConfig(@SessionAttribute UserBean userInfo,
                                           HttpServletRequest request, NetConfigBean bean) {

        if (logger.isDebugEnabled()) {
            logger.debug("add net config {}", bean.toString());
        }

        String filePath = config.getBasePath() + config.getInterfaceConfigPath();
        return systemService.addNetConfig(userInfo, request.getRemoteHost(), bean, filePath, NetworkType.NETCONFIG);
    }

    @PostMapping("/network/update")
    @ResponseBody
    public CopsecResult updateInterfaceConfig(@SessionAttribute UserBean userInfo,
                                              HttpServletRequest request, NetConfigBean bean) {

        String filePath = config.getBasePath() + config.getInterfaceConfigPath();
        return systemService
                .updateNetConfig(userInfo, request.getRemoteHost(), bean, filePath, NetworkType.NETCONFIG);
    }

    @PostMapping("/network/delete")
    @ResponseBody
    public CopsecResult deleteInterfaceConfig(@SessionAttribute UserBean userInfo,
                                              HttpServletRequest request, String id) {

        String filePath = config.getBasePath() + config.getInterfaceConfigPath();
        return systemService.deleteNetConfig(userInfo, request.getRemoteHost(), id, filePath, NetworkType.NETCONFIG);
    }

    @PostMapping("/network/gateway")
    @ResponseBody
    public CopsecResult setGateway(@SessionAttribute UserBean userInfo, HttpServletRequest request, String ip) {

        String filePath = config.getBasePath() + config.getGatewayPath();
        return systemService.setNetworkConfig(userInfo, request.getRemoteHost(), filePath, ip, NetworkType.GATEWAY);
    }

    @PostMapping("/network/dns")
    @ResponseBody
    public CopsecResult setDNS(@SessionAttribute UserBean userInfo, HttpServletRequest request, String ip) {

        String filePath = config.getBasePath() + config.getDnsPath();
        return systemService.setNetworkConfig(userInfo, request.getRemoteHost(), filePath, ip, NetworkType.DNS);
    }

    @PostMapping("/network/manageIp")
    @ResponseBody
    public CopsecResult setManagerIp(@SessionAttribute UserBean userInfo,
                                     HttpServletRequest request, String ip) {

        String filePath = config.getBasePath() + config.getManagerIpPath();
        return systemService.setNetworkConfig(userInfo, request.getRemoteHost(), filePath, ip, NetworkType.MANAGERIP);
    }

    @PostMapping("/router/add")
    @ResponseBody
    public CopsecResult addRouter(@SessionAttribute UserBean userInfo,
                                  HttpServletRequest request, RouterBean bean) {

        String filePath = config.getBasePath() + config.getRouterPath();
        return systemService.addRouter(userInfo, request.getRemoteHost(), bean, filePath, NetworkType.ROUTER);
    }

    @PostMapping("/router/update")
    @ResponseBody
    public CopsecResult updateRouter(@SessionAttribute UserBean userInfo,
                                     HttpServletRequest request, RouterBean bean) {

        String filePath = config.getBasePath() + config.getRouterPath();
        return systemService.updateRouter(userInfo, request.getRemoteHost(), bean, filePath, NetworkType.ROUTER);
    }

    @PostMapping("/router/delete")
    @ResponseBody
    public CopsecResult deleteRouter(@SessionAttribute UserBean userInfo,
                                     HttpServletRequest request, String id) {

        String filePath = config.getBasePath() + config.getRouterPath();
        return systemService.deleteRouter(userInfo, request.getRemoteHost(), id, filePath, NetworkType.ROUTER);
    }

    @PostMapping("/network/ipv6/add")
    @ResponseBody
    public CopsecResult addNetConfig4Ip6(@SessionAttribute UserBean userInfo,
                                         HttpServletRequest request, NetConfigBean bean) {

        if (logger.isDebugEnabled()) {

            logger.debug("add net config 4 ipv6");
        }
        String filePath = config.getBasePath() + config.getInterfaceConfigPathv6();
        return systemService.addNetConfig(userInfo, request.getRemoteHost(), bean, filePath, NetworkType.NETCONFIG6);
    }

    @PostMapping("/network/ipv6/update")
    @ResponseBody
    public CopsecResult updateNetConfig4Ipv6(@SessionAttribute UserBean userInfo,
                                             HttpServletRequest request, NetConfigBean bean) {

        if (logger.isDebugEnabled()) {

            logger.debug("update net config 4 ipv6");
        }
        String filePath = config.getBasePath() + config.getInterfaceConfigPathv6();
        return systemService.updateNetConfig(userInfo, request.getRemoteHost(), bean, filePath, NetworkType.NETCONFIG6);
    }

    @PostMapping("/network/ipv6/delete")
    @ResponseBody
    public CopsecResult deleteNetConfig4Ipv6(@SessionAttribute UserBean userInfo,
                                             HttpServletRequest request, String id) {

        if (logger.isDebugEnabled()) {

            logger.debug("delete net config 4 ipv6");
        }
        String filePath = config.getBasePath() + config.getInterfaceConfigPathv6();
        return systemService.deleteNetConfig(userInfo, request.getRemoteHost(), id, filePath, NetworkType.NETCONFIG6);
    }

    @PostMapping("/network/ipv6/dns")
    @ResponseBody
    public CopsecResult setDNS4Ipv6(@SessionAttribute UserBean userInfo,
                                    HttpServletRequest request, String ip) {

        if (logger.isDebugEnabled()) {

            logger.debug("set dns 4 ipv6");
        }
        String filePath = config.getBasePath() + config.getDnsPathv6();
        return systemService.setNetworkConfig(userInfo, request.getRemoteHost(), filePath, ip, NetworkType.DNSV6);
    }

    @PostMapping("/network/ipv6/gateway")
    @ResponseBody
    public CopsecResult setGateway4Ipv6(@SessionAttribute UserBean userInfo,
                                        HttpServletRequest request, String ip) {

        if (logger.isDebugEnabled()) {

            logger.debug("set gateway 4 ipv6");
        }
        String filePath = config.getBasePath() + config.getGatewayPathv6();
        return systemService.setNetworkConfig(userInfo, request.getRemoteHost(), filePath, ip, NetworkType.GATEWAY6);
    }

    @PostMapping("/network/ipv6/managerip")
    @ResponseBody
    public CopsecResult setManagerIp4Ipv6(@SessionAttribute UserBean userInfo,
                                          HttpServletRequest request, String ip) {

        if (logger.isDebugEnabled()) {

            logger.debug("set manager 4 ipv6");
        }
        String filePath = config.getBasePath() + config.getManagerIpPathv6();
        return systemService.setNetworkConfig(userInfo, request.getRemoteHost(), filePath, ip, NetworkType.MANAGER6);
    }

    @PostMapping("/router/ipv6/add")
    @ResponseBody
    public CopsecResult addRouter4Ipv6(@SessionAttribute UserBean userInfo,
                                       HttpServletRequest request, RouterBean bean) {

        if (logger.isDebugEnabled()) {

            logger.debug("add router 4 ipv6");
        }
        String filePath = config.getBasePath() + config.getRouterPathv6();
        return systemService.addRouter(userInfo, request.getRemoteHost(), bean, filePath, NetworkType.ROUTER6);
    }

    @PostMapping("/router/ipv6/update")
    @ResponseBody
    public CopsecResult updateRouter4Ipv6(@SessionAttribute UserBean userInfo,
                                          HttpServletRequest request, RouterBean bean) {

        if (logger.isDebugEnabled()) {

            logger.debug("update router 4 ipv6");
        }
        String filePath = config.getBasePath() + config.getRouterPathv6();
        return systemService.updateRouter(userInfo, request.getRemoteHost(), bean, filePath, NetworkType.ROUTER6);
    }

    @PostMapping("/router/ipv6/delete")
    @ResponseBody
    public CopsecResult deleteRouter4Ipv6(@SessionAttribute UserBean userInfo,
                                          HttpServletRequest request, String id) {

        if (logger.isDebugEnabled()) {

            logger.debug("delete router 4 ipv6");
        }
        String filePath = config.getBasePath() + config.getRouterPathv6();

        return systemService.deleteRouter(userInfo, request.getRemoteHost(), id, filePath, NetworkType.ROUTER6);
    }

    @PostMapping("/bond/add")
    @ResponseBody
    public CopsecResult addBondConfig(@SessionAttribute UserBean userInfo,
                                      HttpServletRequest request, NetPortBondBean bean) {

        if (logger.isDebugEnabled()) {

            logger.debug("add bond config");
        }
        return systemService.addBondConfig(userInfo, request.getRemoteHost(), bean);
    }

    @PostMapping("/bond/update")
    @ResponseBody
    public CopsecResult updateBondConfig(@SessionAttribute UserBean userInfo,
                                         HttpServletRequest request, NetPortBondBean bean) {

        if (logger.isDebugEnabled()) {

            logger.debug("update bond config");
        }
        return systemService.updateBondConfig(userInfo, request.getRemoteHost(), bean);
    }

    @PostMapping("/bond/delete")
    @ResponseBody
    public CopsecResult deleteBondConfig(@SessionAttribute UserBean userInfo,
                                         HttpServletRequest request, String name) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete bond config");
        }
        return systemService.deleteBondConfig(userInfo, request.getRemoteHost(), name);
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

    @PostMapping(value = "/inspector/set")
    @ResponseBody
    public CopsecResult setFlumeServiceStatus(@SessionAttribute UserBean userInfo, FlumeServiceStatus status, HttpServletRequest request) {

        if (logger.isDebugEnabled()) {
            logger.debug("set flume service {}", Objects.toString(status));
        }

        return systemService.setFlumeService(userInfo, request.getRemoteHost(), status);
    }

    @PostMapping(value = "/task/file")
    @ResponseBody
    public CopsecResult setTaskMonitorConfig(@SessionAttribute UserBean userInfo, FileSyncMonitorBean configBean, HttpServletRequest request) {
        return systemService.setTaskMonitor(userInfo, request.getRemoteHost(), configBean);
    }

    @GetMapping(value = "/task/status/{time}")
    @ResponseBody
    public CopsecResult getTaskStatus(@SessionAttribute UserBean userInfo, @PathVariable(name = "time") String time) {

        return systemService.findWithStatus();
    }

    @PostMapping(value = "/task/search")
    @ResponseBody
    public Page<FileSyncStatusBean> getTaskStatusWithPageable(@SessionAttribute UserBean userInfo, LogConditionBean condition) {
        Pageable pageable = null;
        if (!ObjectUtils.isEmpty(condition.getPage()) && !ObjectUtils.isEmpty(condition.getSize())) {
            pageable = PageRequest.of(condition.getPage(), condition.getSize());
        } else {
            pageable = PageRequest.of(0, 10);
        }
        return systemService.findWithPageable(pageable);
    }

    @PostMapping(value = "/task/update")
    @ResponseBody
    public CopsecResult updateTaskStatus(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestBody List<String> ids) {

        return systemService.updateTaskStatus(userInfo, request.getRemoteHost(), ids);
    }

    @PostMapping(value = "/task/delete")
    @ResponseBody
    public CopsecResult deleteTaskStatus(@SessionAttribute UserBean userInfo, HttpServletRequest request, @RequestBody List<String> ids) {

        return systemService.deleteTaskStatusRecords(userInfo, request.getRemoteHost(), ids);
    }

    @PostMapping(value = "/device/status")
    @ResponseBody
    public CopsecResult getDeviceStatusInfo(@SessionAttribute UserBean userInfo, ConditionBean conditionBean) {

        return deviceService.countDeviceNowStatus(conditionBean);
    }

    @PostMapping(value = "/syslog/set")
    @ResponseBody
    public CopsecResult setTransferConfig(@SessionAttribute UserBean userInfo, @RequestBody SyslogConfigBean configBean, HttpServletRequest request) {

        if (logger.isDebugEnabled()) {

            logger.debug("transfer config is {}", configBean);
        }
        return systemService.setTransferConfig(userInfo, request.getRemoteHost(), configBean);
    }

    @GetMapping(value = "/runLog/get")
    @ResponseBody
    public CopsecResult getServiceRunLogs(@SessionAttribute UserBean userInfo) {

        return systemService.getServiceRunLog(userInfo);
    }

    @GetMapping(value = "/download/log/{fileName}")
    @ResponseBody
    public ResponseEntity<ZipOutputStream> downloadServiceLog(@SessionAttribute UserBean userInfo,
                                                              @PathVariable(name = "fileName") String fileName, HttpServletRequest request, HttpServletResponse response) throws CopsecException {

        ZipOutputStream resource = systemService.downloadServiceRunlogWithOutputStream(userInfo, request.getRemoteHost(), fileName, response);
        HttpStatus status = HttpStatus.CREATED;
        if (ObjectUtils.isEmpty(resource)) {

            status = HttpStatus.BAD_REQUEST;
            throw new CopsecException("file not found");
        }
        File _file = CopsecFileUtils.getFile(config.getLogRootPath(), fileName);
        String _fileName = UriUtils.encode(_file.getName(), Charset.forName("UTF-8"));
        String headers = request.getHeader("User-Agent").toUpperCase();
        if (headers.contains("MSIE") || headers.contains("TRIDENT") || headers.contains("EDGE")) {

            _fileName = fileName.replace("+", "%20");
            status = HttpStatus.OK;
        }
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.setContentDispositionFormData("attachement", _fileName);
        header.setContentLength(_file.length());

        return new ResponseEntity<ZipOutputStream>(resource, header, status);
    }

    @GetMapping(value = "/task/filename/get")
    @ResponseBody
    public CopsecResult getFileTaskNames(@SessionAttribute UserBean userInfo) {

        return systemService.getFileTaskNames();
    }

    @PostMapping(value = "/policy/update")
    @ResponseBody
    public CopsecResult updateTaskPolicy(@SessionAttribute UserBean userInfo, HttpServletRequest request, FileTaskSyncPolicyBean bean) {

        return systemService.updateFileTaskMonitorPolicy(userInfo, request.getRemoteHost(), bean);
    }

    @PostMapping(value = "/policy/delete")
    @ResponseBody
    public CopsecResult deleteTaskPolicy(@SessionAttribute UserBean userInfo, HttpServletRequest request, String taskName) {

        return systemService.deleteFileTaskMonitorPolicy(userInfo, request.getRemoteHost(), taskName);
    }

    @GetMapping(value = "/policy/getAll")
    @ResponseBody
    public CopsecResult getAllPolicy(@SessionAttribute UserBean userInfo) {

        return CopsecResult.success(TaskPolicyPool.getInstance().getAll());
    }

    @GetMapping(value = "/logSettings/get")
    @ResponseBody
    public CopsecResult getAllLogSettings(@SessionAttribute UserBean userInfo) {

        return CopsecResult.success(LogSettingPools.getInstances().getAllSetting());
    }

    @PostMapping(value = "/logSetting/update")
    @ResponseBody
    public CopsecResult updateLogSettings(@SessionAttribute UserBean userInfo, @RequestBody List<String> logTypes) {

        LogSettingPools.getInstances().update(logTypes);
        try {

            String filePath = config.getBasePath() + config.getLogSettingPath();
            logSettingReader.writeDate(LogSettingPools.getInstances().getAllLogSettings(), filePath);
        } catch (Throwable t) {

            logger.error(t.getMessage(), t);
            return CopsecResult.failed("系统异常，请稍后重试");
        }
        return CopsecResult.success("设置成功");
    }

    @PostMapping(value = "/logSetting/storage/set")
    @ResponseBody
    public CopsecResult setAuditLogStorage(@SessionAttribute UserBean userInfo, int days, HttpServletRequest request) {

        String filePath = config.getBasePath() + config.getLogStoragePath();
        return systemService.setNetworkConfig(userInfo, request.getRemoteHost(), filePath, days + "", NetworkType.LOGSTORAGE);
    }


    @PostMapping(value = "/remote/device/add")
    @ResponseBody
    public CopsecResult addRemoteDevice(@SessionAttribute UserBean userInfo, RemoteDeviceBean deviceBean) {

        String filePath = config.getBasePath() + config.getRemoteDevicePath();
        RemoteDevicePools.getInstances().update(deviceBean);
        try {
            List<String> list = Lists.newArrayList();
            RemoteDevicePools.getInstances().getAll().stream().forEach(item -> {
                list.add(item.toString());
            });
            commonFileReader.writeDate(list, filePath);
            return CopsecResult.success("操作成功", deviceBean);
        } catch (Throwable t) {

            logger.error(t.getMessage(), t);
            return CopsecResult.failed("系统异常，请稍后重试");
        }
    }

    @PostMapping(value = "/remote/device/delete")
    @ResponseBody
    public CopsecResult deleteRemoteDevice(@SessionAttribute UserBean userInfo, String deviceId) {

        String filePath = config.getBasePath() + config.getRemoteDevicePath();
        RemoteDevicePools.getInstances().remove(deviceId);
        try {
            List<String> list = Lists.newArrayList();
            RemoteDevicePools.getInstances().getAll().stream().forEach(item -> {
                list.add(item.toString());
            });
            commonFileReader.writeDate(list, filePath);
        } catch (Throwable t) {

            logger.error(t.getMessage(), t);
            return CopsecResult.failed("系统异常，请稍后重试");
        }
        return CopsecResult.success("删除成功");
    }

    @GetMapping(value = "/remote/get/all")
    @ResponseBody
    public CopsecResult getAllRemoteDevice(@SessionAttribute UserBean userInfo) {

        return CopsecResult.success(RemoteDevicePools.getInstances().getAll());
    }

    @GetMapping(value = "/remote/session/{deviceId}")
    @ResponseBody
    public CopsecResult getRemoteLoginSession(@SessionAttribute UserBean userInfo, @PathVariable(name = "deviceId") String deviceId) {

        return CopsecResult.success(Resources.KEBOMENU, userInfo.isLogined(deviceId));
    }

    /**
     * 登陆远程主机
     *
     * @param userInfo
     * @param userId
     * @param password
     * @return
     */
    @PostMapping(value = "/remote/login")
    @ResponseBody
    public CopsecResult remoteLogin(@SessionAttribute UserBean userInfo, String userId, String password,
                                    String deviceId, String methodType, HttpServletRequest request) {

        CopsecResult result = systemService.loginRemoteDevice(userInfo, request.getRemoteHost(), userId, password, deviceId, methodType);

        if (result.getCode() == CopsecResult.FALIED_CODE) {

            return result;
        }
        Cookie cookie = (Cookie) result.getData();
        RemoteSessionBean sessionBean = new RemoteSessionBean(deviceId, userId, password, cookie);
        userInfo.addCookie(deviceId, sessionBean);
        HttpSession session = request.getSession();
        session.setAttribute("userInfo", userInfo);
        return result;
    }
}
