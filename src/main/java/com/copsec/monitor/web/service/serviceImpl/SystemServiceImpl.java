package com.copsec.monitor.web.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.copsec.monitor.web.beans.*;
import com.copsec.monitor.web.beans.flume.FlumeServiceStatus;
import com.copsec.monitor.web.beans.network.*;
import com.copsec.monitor.web.beans.remote.RemoteDeviceBean;
import com.copsec.monitor.web.beans.remote.RemoteUriBean;
import com.copsec.monitor.web.beans.syslogConf.ServiceLogFileBean;
import com.copsec.monitor.web.beans.syslogConf.SyslogConfigBean;
import com.copsec.monitor.web.beans.taskMonitor.FileSyncMonitorBean;
import com.copsec.monitor.web.beans.taskMonitor.FileSyncStatusBean;
import com.copsec.monitor.web.beans.taskMonitor.FileTaskSyncPolicyBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.commons.FileMap;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.entity.AuditSyslogMessage;
import com.copsec.monitor.web.entity.FileSyncStatus;
import com.copsec.monitor.web.entity.OperateLog;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.*;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.FileReaderType;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.*;
import com.copsec.monitor.web.repository.AuditSyslogMessageRepository;
import com.copsec.monitor.web.repository.FileStatusRepository;
import com.copsec.monitor.web.repository.FileSyncStatusRepository;
import com.copsec.monitor.web.repository.LogRepository;
import com.copsec.monitor.web.service.SystemService;
import com.copsec.monitor.web.utils.*;
import com.copsec.monitor.web.utils.MD5Utils.MD5Util;
import com.copsec.monitor.web.utils.encryptionUtils.DESCryptoTools;
import com.copsec.monitor.web.utils.logUtils.LogUtils;
import com.copsec.monitor.web.utils.zipUtils.CopsecZipUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.FileHeader;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class SystemServiceImpl implements SystemService {

    private static final Logger logger = LoggerFactory.getLogger(SystemServiceImpl.class);

    @Autowired
    private UserFileReader userFileReader;

    @Autowired
    private UserInfoReader userInfoReader;

    @Autowired
    private SystemConfig config;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private CommonFileReader commonFileReader;

    @Autowired
    private TaskPolicyReader taskPolicyReader;

    @Autowired
    private AuditSyslogMessageRepository auditSyslogMessageRepository;

    @Autowired
    private FileSyncStatusRepository fileSyncRepository;

    @Autowired
    private FileStatusRepository fileSyncHistoryStatusRepository;

    @Override
    public CopsecResult login(UserBean userInfo, String ip) {

        UserBean userBean = UserPools.getInstances().get(userInfo.getId());
        if (ObjectUtils.isEmpty(userBean)) {
            LogUtils.sendFailLog(userInfo.getId(), ip, "登录失败,用户不存在", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登录");
            return CopsecResult.failed("用户不存在");
        }
        boolean access = false;
        List<AllowedIpBean> list = CommonPools.getInstances().getAllIps();
        if (ObjectUtils.isEmpty(list) || list.size() == 0) {
            access = true;
        } else {
            for (AllowedIpBean item : list) {
                if (item.getIp().equals(ip)) {
                    access = true;
                    break;
                }
            }
        }
        if (!access) {
            LogUtils.sendFailLog(userInfo.getId(), ip, "登录失败,此主机不在远程管理IP列表中", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登录");
            return CopsecResult.failed("此主机不在远程管理IP列表中，不允许登录!");
        }

        LockBean lock = WrongPasswordPool.getInstances().get(userBean.getId());
        List<String> _list = CommonPools.getInstances().getNetowkConfig(NetworkType.LOGINLOCKTIME);
        int time = 0;
        if (ObjectUtils.isEmpty(_list)) {
            time = 1;
        } else {
            time = Integer.valueOf(CommonPools.getInstances().getNetowkConfig(NetworkType.LOGINLOCKTIME).get(0));
        }

        if (!userBean.getPassword().equals(MD5Util.encryptMD5(userInfo.getPassword()))) {
            String message = "";
            /**
             * 密码错误记录
             */
            if (ObjectUtils.isEmpty(lock)) {
                int total = Integer.valueOf(CommonPools.getInstances().getNetowkConfig(NetworkType.LOGINTRYTIME).get(0));
                lock = new LockBean(total);
                WrongPasswordPool.getInstances().add(userBean.getId(), lock);
                message = "密码错误，已尝试" + lock.getAttemptNum() + "次，还可以尝试" + lock.getTryTime() + "次!";
            } else {
                /**
                 * 已锁定
                 */
                if (lock.isLocked()) {
                    if ((System.currentTimeMillis() - lock.getLockCurrentTime()) >= time * 1000) {
                        lock.setAttemptNum(1);
                    } else {
                        lock.updateAttemTime();
                    }
                    message = "密码错误，已尝试" + lock.getAttemptNum() + "次，还可以尝试" + lock.getTryTime() + "次!";
                } else {
                    lock.updateAttemTime();
                    if (lock.getAttemptNum() == lock.getTotalNum()) {
                        lock.setLocked(true);
                        message = "密码错误，账号已锁定,请" + time + "分钟后重试";
                    } else {
                        message = "密码错误，已尝试" + lock.getAttemptNum() + "次，还可以尝试" + lock.getTryTime() + "次!";
                    }
                }
            }
            LogUtils.sendFailLog(userInfo.getId(), ip, message, config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登录");

            return CopsecResult.failed(message);
        } else {
            if (!ObjectUtils.isEmpty(lock) && lock.isLocked()) {
                return CopsecResult.failed("账号已锁定,请" + time + "分钟后重试");
            }
            WrongPasswordPool.getInstances().remove(userBean.getId());
        }
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "登录成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登录");

        return CopsecResult.success(userBean);
    }

    @Override
    public CopsecResult passwordReset(UserBean userInfo, String orig, String newCode, String ip) {

        if (!userInfo.getPassword().equalsIgnoreCase(MD5Util.encryptMD5(orig))) {

            LogUtils.sendFailLog(userInfo.getId(), ip, "重置密码失败，原密码错误", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "重置密码");
            return CopsecResult.failed("原密码错误");
        }

        userInfo.setPassword(MD5Util.encryptMD5(newCode));

        UserPools.getInstances().update(userInfo);

        try {
            userFileReader.writeDate(UserPools.getInstances().getAll(),
                    config.getBasePath() + config.getUserPath());
        } catch (CopsecException e) {
            logger.error(e.getMessage(), e);
        }
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "密码重置成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "重置密码");
        return CopsecResult.success("密码修改成功");
    }

    @Override
    public CopsecResult getAllUserInfo() {
        UserInfoPools.getInstances().clean();
        try {
            userInfoReader.getData(config.getBasePath() + config.getUserInfoPath());
        } catch (CopsecException e) {
            logger.error(e.getMessage(), e);
            return CopsecResult.failed(e.getMessage());
        }

        List<UserInfoBean> userInfoList = UserInfoPools.getInstances().getAll();

        return CopsecResult.success(userInfoList);
    }

    @Override
    public CopsecResult addUserInfo(UserBean userInfo, String ip, UserInfoBean bean, String filePath) {
        try {
            UserInfoPools.getInstances().add(bean);

            UserInfoReader reader = (UserInfoReader) FileReaderFactory.getFileReader(FileReaderType.USERINFO);

            reader.writeDate(UserInfoPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加运维用户成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.USERINFO_ADD);

            return CopsecResult.success("添加成功", bean);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            UserInfoPools.getInstances().delete(bean.getUserId());
            LogUtils.sendFailLog(userInfo.getId(), ip, "添加运维用户失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.USERINFO_ADD);
            return CopsecResult.failed("添加失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult updateUserInfo(UserBean userInfo, String ip, UserInfoBean bean, String filePath) {
        UserInfoBean oldBean = UserInfoPools.getInstances().get(bean.getUserId());
        try {
            UserInfoPools.getInstances().update(bean);

            UserInfoReader reader = (UserInfoReader) FileReaderFactory.getFileReader(FileReaderType.USERINFO);

            reader.writeDate(UserInfoPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新运维用户成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.USERINFO_UPDATE);

            return CopsecResult.success("更新成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            UserInfoPools.getInstances().update(oldBean);
            LogUtils.sendFailLog(userInfo.getId(), ip, "更新运维用户失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.USERINFO_UPDATE);
            return CopsecResult.failed("更新失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult deleteUserInfo(UserBean userInfo, String ip, String userId, String filePath) {
        UserInfoBean oldBean = UserInfoPools.getInstances().get(userId);
        try {
            UserInfoPools.getInstances().delete(userId);

            UserInfoReader reader = (UserInfoReader) FileReaderFactory.getFileReader(FileReaderType.USERINFO);

            reader.writeDate(UserInfoPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除运维用户成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.USERINFO_DELETE);

            return CopsecResult.success("删除成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            UserInfoPools.getInstances().add(oldBean);
            LogUtils.sendFailLog(userInfo.getId(), ip, "删除运维用户失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.USERINFO_DELETE);
            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult deleteUserInfoList(UserBean monitorItem, String ip, List<String> idArray, String filePath) {
        List<UserInfoBean> oldBeanList = UserInfoPools.getInstances().get(idArray);
        UserInfoPools.getInstances().delete(idArray);

        try {
            UserInfoReader reader = (UserInfoReader) FileReaderFactory.getFileReader(FileReaderType.USERINFO);
            reader.writeDate(UserInfoPools.getInstances().getAll(), filePath);
            LogUtils.sendSuccessLog(monitorItem.getId(), ip, "删除所选用户成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选用户");
            return CopsecResult.success("删除成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            UserInfoPools.getInstances().add(oldBeanList);
            LogUtils.sendFailLog(monitorItem.getId(), ip, "删除所选用户失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选用户");
            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult setNetworkConfig(UserBean userInfo, String ip, String filePath, String status, NetworkType type) {

        CommonFileReader reader = (CommonFileReader) FileReaderFactory.getFileReader(FileReaderType.COMMON);

        CommonPools.getInstances().update(type, status);

        try {
            reader.writeDate(CommonPools.getInstances().getNetowkConfig(type), filePath);
        } catch (CopsecException e) {
            logger.error(e.getMessage(), e);
            LogUtils.sendFailLog(userInfo.getId(), ip, "设置失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);
            return CopsecResult.failed("设置失败", e.getMessage());
        }
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "设置成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("设置成功");
    }

    @Override
    public CopsecResult systemControl(UserBean userInfo, String ip, String type) {

        String[] command = {"/bin/sh", "-c", type};
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {

            process = runtime.exec(command);
            process.waitFor();
            if (process.exitValue() == 0) {

                LogUtils.sendSuccessLog(userInfo.getId(), ip, "执行命令[" + type + "]成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_COMMAND);

                return CopsecResult.success("命令执行成功");
            } else {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                LogUtils.sendFailLog(userInfo.getId(), ip, "执行命令[" + type + "]失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_COMMAND);
                return CopsecResult.failed(bufferedReader.lines());
            }
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
        }
        return CopsecResult.failed();
    }


    /**
     * 需要对读取操作进行抽象化处理
     *
     * @param userInfo
     * @param ip
     * @return
     */
    @Override
    public CopsecResult getNetworkConfig(UserBean userInfo, String ip) {

        if (logger.isDebugEnabled()) {

            logger.debug("reading config files....");
        }
        FileReaderUtils.getData(config);
        return CopsecResult.success(CommonPools.getInstances().get());
    }

    @Override
    public CopsecResult addIpConfig(UserBean userInfo, String ip, String removeIp) {

        AllowedIpBean bean = new AllowedIpBean();
        bean.setIp(removeIp);
        CommonPools.getInstances().add(bean);
        IpConfigFileReader ipConfigFileReader = (IpConfigFileReader) FileReaderFactory.
                getFileReader(FileReaderType.IPREADER);
        try {

            ipConfigFileReader.writeDate(CommonPools.getInstances().getAllIps(),
                    config.getBasePath() + config.getAllowIpPath());
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            CommonPools.getInstances().delete(bean.getId());
            LogUtils.sendFailLog(userInfo.getId(), ip, "添加IP地址失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("添加失败", e.getMessage());
        }
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加IP地址成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("添加成功", bean);
    }

    @Override
    public CopsecResult deleteIpConfig(UserBean userInfo, String ip, String id) {

        AllowedIpBean bean = CommonPools.getInstances().getIp(id);
        CommonPools.getInstances().delete(id);
        IpConfigFileReader ipConfigFileReader = (IpConfigFileReader) FileReaderFactory.
                getFileReader(FileReaderType.IPREADER);
        try {

            ipConfigFileReader.writeDate(CommonPools.getInstances().getAllIps(),
                    config.getBasePath() + config.getAllowIpPath());
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            CommonPools.getInstances().add(bean);
            LogUtils.sendFailLog(userInfo.getId(), ip, "删除IP地址失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("删除失败", e.getMessage());
        }
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除IP地址成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("删除成功");
    }

    @Override
    public CopsecResult setNetworkTiming(UserBean userInfo, String ip, NetworkTimingBean bean) {

        if (ObjectUtils.isEmpty(bean)) {

            return CopsecResult.failed("设备失败，");
        }
        String str = JSON.toJSONString(bean);
        CommonPools.getInstances().update(NetworkType.NETWORKTIMESERVICE, str);

        NetworkTimeReader reader = (NetworkTimeReader) FileReaderFactory.getFileReader(FileReaderType.NETWORKTIMING);

        try {

            List<NetworkTimingBean> list = new ArrayList<>();
            list.add(bean);
            reader.writeDate(list,
                    config.getBasePath() + config.getTimingNetworkPath());

        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            LogUtils.sendFailLog(userInfo.getId(), ip, "设置网络校时失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("设置失败", e.getMessage());
        }

        LogUtils.sendSuccessLog(userInfo.getId(), ip, "设置网络校时成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);
        return CopsecResult.success("设备成功");
    }

    @Override
    public FileSystemResource backupConfig(UserBean userInfo, String ip) {

        try {

            CopsecZipUtils.toZip(config.getBackupFilePath(),
                    config.getSystemPassword(), config.getBackupFileName());

            DESCryptoTools des = new DESCryptoTools();

            String fileName = config.getBackupFilePath() + config.getBackupFileName();
            des.encode(fileName,
                    fileName + Resources.ZIPFILESUFFIX);

            File file = new File(fileName + Resources.ZIPFILESUFFIX);
            if (file.exists() && file.isFile()) {

                FileSystemResource resource = new FileSystemResource(file);
                LogUtils.sendSuccessLog(userInfo.getId(), ip, "配置文件备份成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "备份");
                return resource;
            }
            LogUtils.sendFailLog(userInfo.getId(), ip, "配置文件备份失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "备份");
            return null;
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            LogUtils.sendFailLog(userInfo.getId(), ip, "配置文件备份失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "备份");
            return null;
        }
    }

    @Override
    public CopsecResult fileUpload(UserBean userInfo, String ip, MultipartFile file, String type, UploadFileBean bean) {

        FileMap.getInstances().add(bean);
        String tmpDir = null;
        String _dir = null;
        try {
            if (type == Resources.LOADCONFIGFILE) {

                tmpDir = config.getUploadFilePath() + File.separator + bean.getFileId();
                _dir = config.getUploadFilePath();
            } else {

                tmpDir = config.getUpgradeFilePath() + File.separator + bean.getFileId();
                _dir = config.getUpgradeFilePath();
            }

            CopsecFileUtils.mkDirs(tmpDir);

            String tmpFilePath = tmpDir + File.separator + bean.getFileId() + "_" + bean.getChunk();

            FileCopyUtils.copy(file.getBytes(), new FileOutputStream(new File(tmpFilePath)));

            if (FileMap.getInstances().isAllUploaded(bean, tmpDir)) {

                CopsecResult result = MergerFileUtils.mergerFile(bean, _dir);
                if (result.getCode() == CopsecResult.FALIED_CODE) {

                    LogUtils.sendFailLog(userInfo.getId(), ip, "文件上传失败[文件上传错误]", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "加载配置文件");

                    return CopsecResult.failed("文件上传错误");
                }
                CommonUtils.deleteFiles(tmpDir);
                if (type.equals(Resources.LOADCONFIGFILE)) {

                    //进行文件校验判断操作
                    DESCryptoTools des = new DESCryptoTools();
                    des.decrypt(_dir + File.separator + bean.getFileName(), config.getUploadFilePath() + Resources.ORIGUPLOADFILE);

                    ZipFile zipFile3 = new ZipFile(config.getUploadFilePath() + Resources.ORIGUPLOADFILE);

                    if (zipFile3.isEncrypted()) {
                        zipFile3.setPassword(config.getSystemPassword());
                    }

                    List<?> fileHeaderList = zipFile3.getFileHeaders();

                    if (fileHeaderList.size() > 0) {

                        CommonUtils.deleteFiles(config.getBackupFilePath());
                        File currentFile = new File(config.getBackupFilePath());
                        String parent = currentFile.getParent();
                        for (int i = 0; i < fileHeaderList.size(); i++) {

                            FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                            zipFile3.extractFile(fileHeader, parent);
                        }
                        FileUtils.forceDelete(new File(config.getBackupFilePath() + config.getSystemType()));
                    } else {

                        LogUtils.sendFailLog(userInfo.getId(), ip, "配置文件加载失败[文件格式不正确]", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "加载配置文件");

                        return CopsecResult.failed("文件格式不正确");
                    }
                }
            }

        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            LogUtils.sendFailLog(userInfo.getId(), ip, "配置文件加载失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "加载配置文件");

            CommonUtils.deleteFiles(tmpDir);

            CommonUtils.deleteFiles(_dir + File.separator + bean.getFileName());

            CommonUtils.deleteFiles(_dir + File.separator + Resources.ORIGUPLOADFILE);

            if (e instanceof ZipException) {

                return CopsecResult.failed("文件上传错误，文件不是压缩文件");
            }
            return CopsecResult.failed("文件上传失败，未知错误");

        }
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "配置文件加载成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "加载配置文件");
        return CopsecResult.success();
    }

    @Override
    public CopsecResult fileUploadSm(UserBean userInfo, String ip, MultipartFile file, String type, UploadFileBean bean) {

        FileOutputStream outputStream = null;

        String filePath = "";
        try {

            if (type.equals(Resources.LOADCONFIGFILE)) {

                filePath = config.getUploadFilePath() +
                        file.getOriginalFilename();
            } else {

                filePath = config.getUpgradeFilePath() + file.getOriginalFilename();
            }

            File parentFile = new File(config.getUpgradeFilePath());
            if (!parentFile.exists()) {

                parentFile.mkdirs();
            }
            File _file = new File(filePath);
            if (!_file.exists()) {

                _file.createNewFile();
            }
            outputStream = new FileOutputStream(_file);

            FileCopyUtils.copy(file.getBytes(), outputStream);

            if (type.equals(Resources.LOADCONFIGFILE)) {

                //进行文件校验判断操作
                DESCryptoTools des = new DESCryptoTools();
                des.decrypt(filePath, config.getUploadFilePath() + Resources.ORIGUPLOADFILE);

                ZipFile zipFile3 = new ZipFile(config.getUploadFilePath() + Resources.ORIGUPLOADFILE);

                if (zipFile3.isEncrypted()) {
                    zipFile3.setPassword(config.getSystemPassword());
                }
                List<?> fileHeaderList = zipFile3.getFileHeaders();

                if (fileHeaderList.size() > 0) {

                    CommonUtils.deleteFiles(config.getBackupFilePath());
                    File currentFile = new File(config.getBackupFilePath());
                    String parent = currentFile.getParent();
                    for (int i = 0; i < fileHeaderList.size(); i++) {

                        FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                        zipFile3.extractFile(fileHeader, parent);
                    }
                    CommonUtils.deleteFiles(config.getBackupFilePath() + config.getSystemType());
                } else {

                    LogUtils.sendFailLog(userInfo.getId(), ip, "配置文件加载失败[文件格式不正确]", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "加载配置文件");
                    return CopsecResult.failed("文件格式不正确");
                }
            } else {

                LogUtils.sendSuccessLog(userInfo.getId(), ip, "配置文件加载成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "加载配置文件");
                return CopsecResult.success();
            }
        } catch (Throwable e) {

            CommonUtils.deleteFiles(filePath);

            CommonUtils.deleteFiles(config.getUploadFilePath() + Resources.ORIGUPLOADFILE);
            logger.error(e.getMessage(), e);
            if (e instanceof ZipException) {

                LogUtils.sendFailLog(userInfo.getId(), ip, "配置文件加载失败[文件不是压缩文件]", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "加载配置文件");
                return CopsecResult.failed("文件上传出错,文件不是压缩文件");
            }
            LogUtils.sendFailLog(userInfo.getId(), ip, "配置文件加载失败[未知错误]", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "加载配置文件");

            return CopsecResult.failed("文件上传出错", "未知错误");
        } finally {

            try {

                if (outputStream != null) {

                    outputStream.close();
                }
            } catch (Throwable th) {

                logger.error(th.getMessage(), th);
            }
        }

        return CopsecResult.success();
    }

    @Override
    public CopsecResult deletePackage(UserBean userInfo, String ip, String pakcageName) {

        String filePath = config.getUpgradeFilePath() + pakcageName;

        File file = new File(filePath);
        try {
            FileUtils.forceDelete(file);

            LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除升级包成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "系统升级");

            return CopsecResult.success("删除成功");
        } catch (IOException e) {

            logger.error(e.getMessage(), e);
            LogUtils.sendFailLog(userInfo.getId(), ip, "删除升级包失败[文件不存在或已删除]", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "系统升级");
            return CopsecResult.failed("删除失败", "文件不存在或已删除");
        }
    }

    @Override
    public CopsecResult addNetConfig(UserBean userInfo, String ip, NetConfigBean bean, String filePath, NetworkType type) {

        List<String> list = CommonPools.getInstances().getNetowkConfig(type);

        NetConfigReader reader = (NetConfigReader) FileReaderFactory.getFileReader(FileReaderType.NETCONFIG);

        List<NetConfigBean> netList = new ArrayList<>();

        if (ObjectUtils.isEmpty(list)) {

            netList = new ArrayList<>();
            netList.add(bean);
        } else {

            String jsonStr = list.get(0);
            if (!ObjectUtils.isEmpty(jsonStr)) {

                netList = JSON.parseArray(jsonStr, NetConfigBean.class);
            }

            netList.add(bean);
        }
        try {

            reader.writeDate(netList, filePath);
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            netList.remove(bean);
            LogUtils.sendFailLog(userInfo.getId(), ip, "添加网络设置失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);
            return CopsecResult.failed("添加失败", "系统异常");
        }
        CommonPools.getInstances().update(type, JSON.toJSONString(netList));

        LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加网络设置成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("添加成功", bean);
    }

    @Override
    public CopsecResult updateNetConfig(UserBean userInfo, String ip, NetConfigBean bean, String filePath, NetworkType type) {

        List<String> list = CommonPools.getInstances().getNetowkConfig(type);

        NetConfigReader reader = (NetConfigReader) FileReaderFactory.getFileReader(FileReaderType.NETCONFIG);

        List<NetConfigBean> netList = null;

        String jsonStr = list.get(0);
        netList = JSON.parseArray(jsonStr, NetConfigBean.class);
        Iterator<NetConfigBean> iterator = netList.iterator();
        while (iterator.hasNext()) {

            NetConfigBean tmp = iterator.next();
            if (tmp.getUuid().equals(bean.getUuid())) {

                iterator.remove();
                break;
            }
        }
        netList.add(bean);

        try {

            reader.writeDate(netList, filePath);
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            netList.remove(bean);
            LogUtils.sendFailLog(userInfo.getId(), ip, "更新网络设置失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("更新失败", "文件写入异常，请稍后重试");
        }
        CommonPools.getInstances().update(type, JSON.toJSONString(netList));
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新网络设置成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("更新成功");
    }

    @Override
    public CopsecResult deleteNetConfig(UserBean userInfo, String ip, String id, String filePath, NetworkType type) {

        List<String> list = CommonPools.getInstances().getNetowkConfig(type);

        NetConfigReader reader = (NetConfigReader) FileReaderFactory.getFileReader(FileReaderType.NETCONFIG);

        String jsonStr = list.get(0);
        List<NetConfigBean> netList = JSON.parseArray(jsonStr, NetConfigBean.class);
        Iterator<NetConfigBean> iterator = netList.iterator();
        NetConfigBean tmp = null;
        while (iterator.hasNext()) {

            tmp = iterator.next();
            if (tmp.getUuid().equals(id)) {

                iterator.remove();
                break;
            }
        }

        try {

            reader.writeDate(netList, filePath);
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            netList.add(tmp);
            LogUtils.sendFailLog(userInfo.getId(), ip, "删除网络设置失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
        CommonPools.getInstances().update(type, JSON.toJSONString(netList));
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除网络设置成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("删除成功");
    }

    @Override
    public CopsecResult addRouter(UserBean userInfo, String ip, RouterBean bean, String filePath, NetworkType type) {

        List<String> list = CommonPools.getInstances().getNetowkConfig(type);

        RouterBeanReader reader = (RouterBeanReader) FileReaderFactory.getFileReader(FileReaderType.ROUTER);

        List<RouterBean> routerList = new ArrayList<>();

        if (ObjectUtils.isEmpty(list)) {

            routerList = new ArrayList<>();
            routerList.add(bean);
        } else {

            String jsonStr = list.get(0);
            if (!ObjectUtils.isEmpty(jsonStr)) {

                routerList = JSON.parseArray(jsonStr, RouterBean.class);
            }
            routerList.add(bean);
        }
        try {

            reader.writeDate(routerList, filePath);

        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            routerList.remove(bean);
            LogUtils.sendFailLog(userInfo.getId(), ip, "添加路由失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("添加失败", "系统异常");
        }
        CommonPools.getInstances().update(type, JSON.toJSONString(routerList));
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加路由成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("添加成功", bean);
    }

    @Override
    public CopsecResult updateRouter(UserBean userInfo, String ip, RouterBean bean, String filePath, NetworkType type) {

        List<String> list = CommonPools.getInstances().getNetowkConfig(type);

        RouterBeanReader reader = (RouterBeanReader) FileReaderFactory.getFileReader(FileReaderType.ROUTER);

        List<RouterBean> netList = null;

        String jsonStr = list.get(0);
        netList = JSON.parseArray(jsonStr, RouterBean.class);
        Iterator<RouterBean> iterator = netList.iterator();
        while (iterator.hasNext()) {

            RouterBean tmp = iterator.next();
            if (tmp.getUuid().equals(bean.getUuid())) {

                iterator.remove();
                break;
            }
        }
        netList.add(bean);

        try {

            reader.writeDate(netList, filePath);
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            netList.remove(bean);
            LogUtils.sendFailLog(userInfo.getId(), ip, "更新路由失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("更新失败", "文件写入异常，请稍后重试");
        }
        CommonPools.getInstances().update(type, JSON.toJSONString(netList));
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新路由成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("更新成功");
    }

    @Override
    public CopsecResult deleteRouter(UserBean userInfo, String ip, String id, String filePath, NetworkType type) {

        List<String> list = CommonPools.getInstances().getNetowkConfig(type);

        RouterBeanReader reader = (RouterBeanReader) FileReaderFactory.getFileReader(FileReaderType.ROUTER);

        String jsonStr = list.get(0);
        List<RouterBean> netList = JSON.parseArray(jsonStr, RouterBean.class);
        Iterator<RouterBean> iterator = netList.iterator();
        RouterBean tmp = null;
        while (iterator.hasNext()) {

            tmp = iterator.next();
            if (tmp.getUuid().equals(id)) {

                iterator.remove();
                break;
            }
        }

        try {

            reader.writeDate(netList, filePath);
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            netList.add(tmp);
            LogUtils.sendFailLog(userInfo.getId(), ip, "删除路由失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
        CommonPools.getInstances().update(type, JSON.toJSONString(netList));
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除路由成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("删除成功");
    }

    @Override
    public CopsecResult updateBondConfig(UserBean userInfo, String ip, NetPortBondBean bean) {

        List<String> list = CommonPools.getInstances().getNetowkConfig(NetworkType.BONDMODE);

        BondModeReader reader = (BondModeReader) FileReaderFactory.getFileReader(FileReaderType.BONDMODE);

        String jsonStr = list.get(0);
        NetPortBondBean tmp = null;

        List<NetPortBondBean> bondList = JSON.parseArray(jsonStr, NetPortBondBean.class);
        Iterator<NetPortBondBean> iterator = bondList.iterator();
        while (iterator.hasNext()) {

            tmp = iterator.next();
            if (tmp.getBoundName().equals(bean.getBoundName())) {

                iterator.remove();
                break;
            }
        }

        bondList.add(bean);
        try {

            reader.writeDate(bondList, config.getBasePath() + config.getNetPortBondConfigPath());
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            bondList.remove(tmp);
            LogUtils.sendFailLog(userInfo.getId(), ip, "更新网口聚合失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("更新失败", "文件写入异常，请稍后重试");
        }
        CommonPools.getInstances().update(NetworkType.BONDMODE, JSON.toJSONString(bondList));
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新网口聚合成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("更新成功");
    }

    @Override
    public CopsecResult deleteBondConfig(UserBean userInfo, String ip, String name) {
        List<String> list = CommonPools.getInstances().getNetowkConfig(NetworkType.BONDMODE);

        BondModeReader reader = (BondModeReader) FileReaderFactory.getFileReader(FileReaderType.BONDMODE);

        String jsonStr = list.get(0);
        List<NetPortBondBean> bondList = JSON.parseArray(jsonStr, NetPortBondBean.class);
        Iterator<NetPortBondBean> iterator = bondList.iterator();
        NetPortBondBean tmp = null;
        while (iterator.hasNext()) {

            tmp = iterator.next();
            if (tmp.getBoundName().equals(name)) {

                iterator.remove();
                break;
            }
        }
        try {

            reader.writeDate(bondList, config.getBasePath() + config.getNetPortBondConfigPath());
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            bondList.add(tmp);
            LogUtils.sendFailLog(userInfo.getId(), ip, "删除网口聚合失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除网口聚合成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        CommonPools.getInstances().update(NetworkType.BONDMODE, JSON.toJSONString(bondList));
        return CopsecResult.success("删除成功");
    }

    @Override
    public CopsecResult addBondConfig(UserBean userInfo, String ip, NetPortBondBean bean) {

        List<String> list = CommonPools.getInstances().getNetowkConfig(NetworkType.BONDMODE);

        BondModeReader reader = (BondModeReader) FileReaderFactory.getFileReader(FileReaderType.BONDMODE);

        String jsonStr = null;
        if (!ObjectUtils.isEmpty(list)) {

            jsonStr = list.get(0);
        }
        List<NetPortBondBean> bondList = new ArrayList<>();
        NetPortBondBean tmp = null;
        if (!ObjectUtils.isEmpty(jsonStr)) {

            bondList = JSON.parseArray(jsonStr, NetPortBondBean.class);
        }
        Iterator<NetPortBondBean> iterator = bondList.iterator();
        while (iterator.hasNext()) {

            NetPortBondBean bond = iterator.next();
            String[] _port = bean.getPort().split(",");
            for (int i = 0; i < _port.length; i++) {
                if (bond.getPort().indexOf(_port[i]) != -1) {
                    LogUtils.sendFailLog(userInfo.getId(), ip, "添加网口聚合失败[绑定业务口重复]", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);
                    return CopsecResult.failed("添加失败，绑定业务口重复");
                }
            }
        }

        List<String> netConfigList = CommonPools.getInstances().getNetowkConfig(NetworkType.NETCONFIG);
        if (!ObjectUtils.isEmpty(list) && !ObjectUtils.isEmpty(netConfigList)) {

            Iterator<NetConfigBean> configIterator = JSON.parseArray(netConfigList.get(0), NetConfigBean.class).iterator();
            while (configIterator.hasNext()) {

                NetConfigBean config = configIterator.next();
                String[] _port = bean.getPort().split(",");
                for (int i = 0; i < _port.length; i++) {
                    if (config.getEthName().indexOf(_port[i]) != -1) {
                        return CopsecResult.failed("添加失败，业务口已被占用");
                    }
                }
            }
        }

        bondList.add(bean);
        try {

            reader.writeDate(bondList, config.getBasePath() + config.getNetPortBondConfigPath());
        } catch (Throwable e) {

            logger.error(e.getMessage(), e);
            bondList.remove(tmp);
            LogUtils.sendFailLog(userInfo.getId(), ip, "添加网口聚合失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("添加失败", "文件写入异常，请稍后重试");
        }
        CommonPools.getInstances().update(NetworkType.BONDMODE, JSON.toJSONString(bondList));

        LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加网口聚合成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("添加成功");
    }

    @Override
    public Page<LogConditionBean> searchOperateLog(Pageable pageable, LogConditionBean condition) {
        Page<OperateLog> pages = logRepository.findOperateLogByCondition(pageable, condition);

        List<OperateLog> content = pages.getContent();
        List<LogConditionBean> lists = Lists.newArrayList();
        content.forEach(item -> {
            LogConditionBean bean = new LogConditionBean();
            bean.setId(item.getId().toHexString());
            bean.setOperateUser(item.getOperateUser());
            bean.setIp(item.getIp());
            bean.setOperateType(item.getOperateType());
            bean.setDesc(item.getDesc());
            bean.setResult(String.valueOf(item.getResult()));
            bean.setDate(FormatUtils.getFormatDate(item.getDate()));
            lists.add(bean);
        });
        return new PageImpl<>(lists, pages.getPageable(), pages.getTotalElements());
    }

    @Override
    public CopsecResult deleteCheckOperateLog(UserBean userInfo, String ip, List<String> ids) {
//        ids.forEach(id -> {
//            Optional<OperateLog> bean = logRepository.findById(new ObjectId((id)));
//            if (bean.isPresent()) {
//                logRepository.deleteById(new ObjectId(id));
//            }
//        });
        logRepository.deleteCheckOperateLog(ids);
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除所选系统操作日志", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选系统操作日志");
        return CopsecResult.success("删除所选系统操作日志成功");
    }

    @Override
    public CopsecResult deleteAllLog(UserBean userInfo, String ip) {
        logRepository.deleteAllOperateLog();
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "清空系统操作日志", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "清空系统操作日志");
        return CopsecResult.success("清空系统操作日志成功");
    }

    @Override
    public Page<LogConditionBean> getServerMessage(LogConditionBean condition, Pageable pageable) {
        if (logger.isDebugEnabled()) {
            logger.debug("get server log by condition {}", condition);
        }
        Query query = new Query();
        Criteria criteria = new Criteria("host");
        criteria.exists(true);
        if (!ObjectUtils.isEmpty(condition.getIp())) {
            criteria.and("host").is(condition.getIp());
        } else if (!ObjectUtils.isEmpty(condition.getDesc())) {
            Pattern pattern = Pattern.compile("^.*" + condition.getDesc() + ".*$", Pattern.CASE_INSENSITIVE);
            criteria.and("message").regex(pattern);
        } else if (!ObjectUtils.isEmpty(condition.getStart()) && !ObjectUtils.isEmpty(condition.getEnd())) {
            Date start = FormatUtils.getDate(condition.getStart());
            Date end = FormatUtils.getDate(condition.getEnd());
            criteria.and("reportTime").gte(start).lte(end);
        }
        query.addCriteria(criteria);
        query.with(new Sort(Direction.DESC, "reportTime"));
        Page<AuditSyslogMessage> page = auditSyslogMessageRepository.getServerMessage(query, pageable);

        List<LogConditionBean> list = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(page)) {
            page.get().forEach(item -> {
                LogConditionBean bean = new LogConditionBean();
                bean.setIp(item.getHost());
                bean.setDate(FormatUtils.getFormatDate(item.getReportTime()));
                bean.setDesc(item.getMessage());
                list.add(bean);
            });
        }

        return new PageImpl<>(list, page.getPageable(), page.getTotalElements());
    }

    @Override
    public CopsecResult setFlumeService(UserBean userInfo, String ip, FlumeServiceStatus status) {
        if (logger.isDebugEnabled()) {
            logger.debug("set flume service with status {}", status);
        }

        try {
            switch (status.getType()) {
                case "t": {
                    CommonPools.getInstances().update(NetworkType.TRANSFER, status.toString());
                    commonFileReader.writeDate(CommonPools.getInstances().getNetowkConfig(NetworkType.TRANSFER),
                            config.getBasePath() + config.getTransferServicePath());
                    LogUtils.sendSuccessLog(userInfo.getId(), ip, "设置探针转发服务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);
                    break;
                }
                case "l": {
                    CommonPools.getInstances().update(NetworkType.LISTENER, status.toString());
                    commonFileReader.writeDate(CommonPools.getInstances().getNetowkConfig(NetworkType.LISTENER),
                            config.getBasePath() + config.getListeningServicePath());
                    LogUtils.sendSuccessLog(userInfo.getId(), ip, "设置单项设备转发服务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);
                    break;
                }
                case "d": {
                    CommonPools.getInstances().update(NetworkType.DEVICEINFO, status.toString());
                    commonFileReader.writeDate(CommonPools.getInstances().getNetowkConfig(NetworkType.DEVICEINFO),
                            config.getBasePath() + config.getDeviceServicePath());
                    LogUtils.sendSuccessLog(userInfo.getId(), ip, "设置状态转发服务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);
                    break;
                }
                case "s": {
                    CommonPools.getInstances().update(NetworkType.SYSLOGINFO, status.toString());
                    commonFileReader.writeDate(CommonPools.getInstances().getNetowkConfig(NetworkType.SYSLOGINFO),
                            config.getBasePath() + config.getSyslogConfigPath());
                    LogUtils.sendSuccessLog(userInfo.getId(), ip, "设置syslog服务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);
                    break;
                }
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            LogUtils.sendFailLog(userInfo.getId(), ip, "设置服务失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);
            return CopsecResult.failed("配置设置异常,请稍后重试");
        }
        return CopsecResult.success("配置设置成功!");
    }

    @Override
    public CopsecResult setTaskMonitor(UserBean userInfo, String ip, FileSyncMonitorBean configBean) {

        try {
            CommonPools.getInstances().update(NetworkType.FILESYNCMONITOR, configBean.toString());

            commonFileReader.writeDate(CommonPools.getInstances().getNetowkConfig(NetworkType.FILESYNCMONITOR),
                    config.getBasePath() + config.getMonitorFileSyncPath());

            FileSyncStatusPools.getInstances().setConfig(configBean);

            LogUtils.sendSuccessLog(userInfo.getId(), ip, "设置文件检查成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        } catch (Throwable t) {

            logger.error(t.getMessage(), t);
            LogUtils.sendFailLog(userInfo.getId(), ip, "设置文件检查失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("配置设置异常,请稍后重试");
        }

        return CopsecResult.success("设置成功");
    }

    @Override
    public Page<FileSyncStatusBean> findWithPageable(Pageable pageable) {

        Page<FileSyncStatus> pages = fileSyncRepository.findFileTaskByCondition(pageable);

        List<FileSyncStatus> content = pages.getContent();
        List<FileSyncStatusBean> lists = Lists.newArrayList();
        content.stream().forEach(item -> {

            FileSyncStatusBean bean = new FileSyncStatusBean();
            bean.setTaskId(item.getId().toHexString());
            bean.setTaskName(item.getTaskName());
            bean.setTime(FormatUtils.getFormatDate(item.getUpdateTime()));
            bean.setMessage(item.getMessage());
            bean.setStatus(item.isStatus());
            bean.setOperateUser(item.getOperateUser());
            bean.setOperateTimeStr(FormatUtils.getFormatDate(item.getOperateTime()));
            lists.add(bean);
        });
        return new PageImpl<>(lists, pages.getPageable(), pages.getTotalElements());
    }

    @Override
    public CopsecResult findWithStatus() {
        List<FileSyncStatus> list = fileSyncRepository.findFileTaskByStatus();
        return CopsecResult.success(list);
    }

    @Override
    public CopsecResult updateTaskStatus(UserBean userInfo, String ip, List<String> ids) {

        ids.stream().forEach(id -> {

            Optional<FileSyncStatus> optional = fileSyncRepository.findById(new ObjectId(id));
            if (optional.isPresent()) {

                FileSyncStatus status = optional.get();
                status.setOperateUser(userInfo.getName());
                status.setOperateTime(new Date());
                fileSyncRepository.updateTaskStatus(status);
            }
        });
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新任务状态成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        return CopsecResult.success("操作成功");
    }

    @Override
    public CopsecResult setTransferConfig(UserBean userInfo, String ip, SyslogConfigBean configBean) {

        try {
            CommonPools.getInstances().update(NetworkType.TRANSFERSYSLOG, configBean.toString());

            commonFileReader.writeDate(CommonPools.getInstances().getNetowkConfig(NetworkType.TRANSFERSYSLOG),
                    config.getTransferSyslogConfigPath());

            LogUtils.sendSuccessLog(userInfo.getId(), ip, "设置本地日志转发成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

        } catch (Throwable t) {

            logger.error(t.getMessage(), t);
            LogUtils.sendFailLog(userInfo.getId(), ip, "设置本地日志转发失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);

            return CopsecResult.failed("配置设置异常,请稍后重试");
        }

        return CopsecResult.success("设置成功");
    }

    @Override
    public CopsecResult getServiceRunLog(UserBean userInfo) {

        List<File> files = Lists.newArrayList();
        files = CopsecFileUtils.getAllFiles(config.getLogRootPath(), files);

        List<ServiceLogFileBean> list = Lists.newArrayList();
        files.stream().forEach(file -> {

            ServiceLogFileBean bean = new ServiceLogFileBean();
            if (file.length() > 0 && !file.getName().contains("-")) {

                bean.setFileName(file.getAbsolutePath().substring(config.getLogRootPath().length()));
                bean.setUuid(MD5Util.encryptMD5(bean.getFileName()));
                bean.setSize(FormatUtils.getFormatSizeByte(file.length()));
                bean.setPath(file.getAbsolutePath());
                list.add(bean);
            }
        });
        return CopsecResult.success(list);
    }

    @Override
    public FileSystemResource downloadServiceRunLog(UserBean userInfo, String ip, String fileName) {

        try {

            List<File> files = Lists.newArrayList();
            files = CopsecFileUtils.getAllFiles(config.getLogRootPath(), files);
            String _fileName = "";
            for (File file : files) {

                String uuid = MD5Util.encryptMD5(file.getAbsolutePath().substring(config.getLogRootPath().length()));
                if (uuid.equals(fileName)) {

                    _fileName = file.getAbsolutePath();
                    break;
                }
            }
            String _filePath = CopsecZipUtils.writeZipFile(_fileName,
                    config.getZipPassword());
            File logZipFile = new File(_filePath);
            if (logZipFile.exists()) {

                FileSystemResource resource = new FileSystemResource(logZipFile);
                LogUtils.sendSuccessLog(userInfo.getId(), ip, "下载日志文件成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "下载");
                return resource;
            }
            LogUtils.sendFailLog(userInfo.getId(), ip, "下载配置文件失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "下载");
            return null;
        } catch (Throwable t) {

            logger.error(t.getMessage(), t);
            LogUtils.sendFailLog(userInfo.getId(), ip, "下载配置文件失败" + t.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "下载");
        }
        return null;
    }

    @Override
    public CopsecResult deleteTaskStatusRecords(UserBean userInfo, String ip, List<String> ids) {

        ids.stream().forEach(id -> {

            Optional<FileSyncStatus> status = fileSyncRepository.findById(new ObjectId((id)));
            if (status.isPresent() && status.get().isStatus()) {

                fileSyncRepository.deleteById(new ObjectId(id));
            }
        });
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除任务记录成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_SYSTEM);
        return CopsecResult.success("操作成功");
    }

    @Override
    public ZipOutputStream downloadServiceRunlogWithOutputStream(UserBean userInfo, String ip, String fileName, HttpServletResponse response) {

        try {

            File _file = CopsecFileUtils.getFile(config.getLogRootPath(), fileName);
            if (ObjectUtils.isEmpty(_file)) {

                LogUtils.sendFailLog(userInfo.getId(), ip, "下载配置文件失败,未知错误", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "下载");
                return null;
            }
            ZipOutputStream outputStream = CopsecZipUtils.writeZipFile2(_file.getAbsolutePath(),
                    config.getZipPassword(), response);

            if (!ObjectUtils.isEmpty(outputStream)) {

                LogUtils.sendSuccessLog(userInfo.getId(), ip, "下载日志文件成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "下载");
                return outputStream;
            }
            LogUtils.sendFailLog(userInfo.getId(), ip, "下载配置文件失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "下载");
            return null;
        } catch (Throwable t) {

            logger.error(t.getMessage(), t);
            LogUtils.sendFailLog(userInfo.getId(), ip, "下载配置文件失败" + t.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "下载");
        }
        return null;
    }

    @Override
    public CopsecResult getFileTaskNames() {

        return CopsecResult.success(fileSyncHistoryStatusRepository.getAllFileTaskName());
    }

    @Override
    public CopsecResult updateFileTaskMonitorPolicy(UserBean userInfo, String ip, FileTaskSyncPolicyBean bean) {

        String filePath = config.getBasePath() + config.getFileSyncPolicyPath();
        try {

            TaskPolicyPool.getInstance().update(bean);

            taskPolicyReader.writeDate(TaskPolicyPool.getInstance().getAll(), filePath);

            LogUtils.sendSuccessLog(userInfo.getId(), ip, "配置同步任务监控策略", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "配置");

            return CopsecResult.success("配置成功");
        } catch (Throwable t) {

            LogUtils.sendFailLog(userInfo.getId(), ip, "配置同步任务监控策略", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "配置");

            TaskPolicyPool.getInstance().delete(bean.getTaskName());
        }

        return CopsecResult.success("配置失败，请稍后重试");
    }

    @Override
    public CopsecResult deleteFileTaskMonitorPolicy(UserBean userInfo, String ip, String taskName) {

        String filePath = config.getBasePath() + config.getFileSyncPolicyPath();

        FileTaskSyncPolicyBean bean = TaskPolicyPool.getInstance().get(taskName);

        try {

            TaskPolicyPool.getInstance().delete(taskName);

            taskPolicyReader.writeDate(TaskPolicyPool.getInstance().getAll(), filePath);

            LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除配置同步任务监控策略", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "配置");

            return CopsecResult.success("删除成功");
        } catch (Throwable t) {

            LogUtils.sendFailLog(userInfo.getId(), ip, "删除配置同步任务监控策略", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "配置");

            TaskPolicyPool.getInstance().update(bean);
        }

        return CopsecResult.success("删除失败，请稍后重试");
    }

    @Override
    public CopsecResult loginRemoteDevice(UserBean userInfo, String ip, String userId, String password,
                                          String deviceId, String methodType) {

        if (logger.isDebugEnabled()) {

            logger.debug("login remote device {} with username and password", deviceId);

        }
        Optional<RemoteDeviceBean> remoteDevice = RemoteDevicePools.getInstances().getDeviceById(deviceId);
        if (remoteDevice.isPresent()) {

            if (remoteDevice.get().getDeviceType().equals(Resources.WANGZHA)) {

                /**
                 * 获取验证码，登陆
                 */
                Optional<RemoteUriBean> uriBean = RemoteUriPools.getInstances().get(Resources.WANGZHA, "getRandomCode");
                if (uriBean.isPresent()) {

                    CopsecResult randomCode = ExecuteRemoteMethodUtils.execute(remoteDevice.get(), uriBean.get(), null, null, response -> {

                        try {

                            String content = EntityUtils.toString(response.getEntity(), HttpClientUtils.ENCODING);
                            JSONObject jsonObject = JSON.parseObject(content);
                            return CopsecResult.success(jsonObject.get("randomCode"));
                        } catch (IOException e) {

                            logger.error(e.getMessage(), e);
                        }
                        return CopsecResult.failed("");
                    });

                    if (randomCode.getCode() == CopsecResult.FALIED_CODE) {

                        LogUtils.sendFailLog(userInfo.getId(), ip, "远程登陆失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登陆");

                        return CopsecResult.failed("远程登陆失败，" + randomCode.getMessage());
                    }
                    Map<String, String> params = Maps.newHashMap();
                    params.put("userName", userId);
                    params.put("password", MD5Util.encryptMD5(password));
                    params.put("inputCode", randomCode.getData().toString());
                    params.put("module", "password");

                    Optional<RemoteUriBean> loginUri = RemoteUriPools.getInstances().get(Resources.WANGZHA, "login");
                    if (loginUri.isPresent()) {

                        CopsecResult loginResult = ExecuteRemoteMethodUtils.execute(remoteDevice.get(), loginUri.get(), params, null, response -> {

                            Optional<Cookie> cookieOptional = HttpClientUtils.cookieStore.getCookies().stream().filter(d -> {

                                if (d.getName().equals("JSESSIONID")) {

                                    return true;
                                }
                                return false;
                            }).findFirst();
                            HttpClientUtils.cookieStore.clear();
                            if (cookieOptional.isPresent()) {

                                LogUtils.sendSuccessLog(userInfo.getId(), ip, "远程登陆", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登陆");

                                return CopsecResult.success(Resources.KEBOMENU, cookieOptional.get());
                            } else {

                                LogUtils.sendFailLog(userInfo.getId(), ip, "远程登陆失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登陆");

                                return CopsecResult.failed("远程登陆失败");
                            }
                        });

                        return loginResult;
                    } else {

                        LogUtils.sendFailLog(userInfo.getId(), ip, "远程登陆失败-远程方法信息不存在", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登陆");

                        return CopsecResult.failed("远程方法信息不存在");
                    }

                } else {

                    LogUtils.sendFailLog(userInfo.getId(), ip, "远程登陆失败-远程方法信息不存在", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登陆");

                    return CopsecResult.failed("远程方法信息不存在");
                }

            } else if (remoteDevice.get().getDeviceType().equals(Resources.FIREWALL)) {


            } else {
                LogUtils.sendFailLog(userInfo.getId(), ip, "远程登陆失败-远程设别类型不存在", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登陆");

                return CopsecResult.failed("远程设别类型不存在，请稍后重试");
            }

        } else {

            LogUtils.sendFailLog(userInfo.getId(), ip, "远程登陆失败-所选设备信息不存在", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登陆");

            return CopsecResult.failed("所选设备信息不存在，请稍后重试");
        }
        return null;
    }
}
