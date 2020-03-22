package com.copsec.monitor.web.service;

import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.beans.UploadFileBean;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.flume.FlumeServiceStatus;
import com.copsec.monitor.web.beans.network.NetConfigBean;
import com.copsec.monitor.web.beans.network.NetPortBondBean;
import com.copsec.monitor.web.beans.network.NetworkTimingBean;
import com.copsec.monitor.web.beans.network.RouterBean;
import com.copsec.monitor.web.beans.syslogConf.SyslogConfigBean;
import com.copsec.monitor.web.beans.taskMonitor.FileSyncMonitorBean;
import com.copsec.monitor.web.beans.taskMonitor.FileSyncStatusBean;
import com.copsec.monitor.web.beans.taskMonitor.FileTaskSyncPolicyBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import net.lingala.zip4j.io.ZipOutputStream;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface SystemService {

    CopsecResult login(UserBean userInfo, String ip);

    CopsecResult passwordReset(UserBean userInfo, String orig, String newCode, String ip);

    CopsecResult getAllUserInfo();

    CopsecResult addUserInfo(UserBean userInfo, String ip, UserInfoBean bean, String filePath);

    CopsecResult updateUserInfo(UserBean userInfo, String ip, UserInfoBean bean, String filePath);

    CopsecResult deleteUserInfo(UserBean userInfo, String ip, String userId, String filePath);

    CopsecResult deleteUserInfoList(UserBean userInfo, String ip, List<String> idArray, String filePath);

    CopsecResult setNetworkConfig(UserBean userInfo, String ip, String filePath, String status, NetworkType type);

    CopsecResult systemControl(UserBean userInfo, String ip, String type);

    CopsecResult getNetworkConfig(UserBean userInfo, String ip);

    CopsecResult addIpConfig(UserBean userInfo, String ip, String removeIp);

    CopsecResult deleteIpConfig(UserBean userInfo, String ip, String id);

    CopsecResult setNetworkTiming(UserBean userInfo, String ip, NetworkTimingBean bean);

    FileSystemResource backupConfig(UserBean userInfo, String ip);

    /**
     * 上传分片大文件
     *
     * @param userInfo
     * @param ip
     * @param file
     * @param type     执行配置文件加载还是系统更新
     * @return
     */
    CopsecResult fileUpload(UserBean userInfo, String ip, MultipartFile file, String type, UploadFileBean bean);

    /**
     * 上传小文件
     *
     * @param userInfo
     * @param ip
     * @param file
     * @param type     执行配置文件加载还是系统更新
     * @return
     */
    CopsecResult fileUploadSm(UserBean userInfo, String ip, MultipartFile file, String type, UploadFileBean bean);

    CopsecResult deletePackage(UserBean userInfo, String ip, String pakcageName);

    CopsecResult addNetConfig(UserBean userInfo, String ip, NetConfigBean bean, String filePath, NetworkType type);

    CopsecResult updateNetConfig(UserBean userInfo, String ip, NetConfigBean bean, String filePath, NetworkType type);

    CopsecResult deleteNetConfig(UserBean userInfo, String ip, String id, String filePath, NetworkType type);

    CopsecResult addRouter(UserBean userInfo, String ip, RouterBean bean, String filePath, NetworkType type);

    CopsecResult updateRouter(UserBean userInfo, String ip, RouterBean bean, String filePath, NetworkType type);

    CopsecResult deleteRouter(UserBean userInfo, String ip, String id, String filePath, NetworkType type);

    CopsecResult updateBondConfig(UserBean userInfo, String ip, NetPortBondBean bean);

    CopsecResult deleteBondConfig(UserBean userInfo, String ip, String name);

    CopsecResult addBondConfig(UserBean userInfo, String ip, NetPortBondBean bean);

    Page<LogConditionBean> searchOperateLog(Pageable pageable, LogConditionBean condition);

    CopsecResult deleteCheckOperateLog(UserBean userInfo, String ip, List<String> ids);

    CopsecResult deleteAllLog(UserBean userInfo, String ip);

    Page<LogConditionBean> getServerMessage(LogConditionBean condition, Pageable pageable);

    CopsecResult setFlumeService(UserBean userInfo, String ip, FlumeServiceStatus status);

    CopsecResult setTaskMonitor(UserBean userInfo, String ip, FileSyncMonitorBean configBean);

    Page<FileSyncStatusBean> findWithPageable(Pageable pageable);

    CopsecResult findWithStatus();

    CopsecResult updateTaskStatus(UserBean userInfo, String ip, List<String> ids);

    CopsecResult setTransferConfig(UserBean userInfo, String ip, SyslogConfigBean configBean);

    CopsecResult getServiceRunLog(UserBean userInfo);

    FileSystemResource downloadServiceRunLog(UserBean userInfo, String ip, String fileName);

    CopsecResult deleteTaskStatusRecords(UserBean userInfo, String ip, List<String> ids);

    ZipOutputStream downloadServiceRunlogWithOutputStream(UserBean userInfo, String ip, String fileName, HttpServletResponse response);

    CopsecResult getFileTaskNames();

    CopsecResult updateFileTaskMonitorPolicy(UserBean userInfo, String ip, FileTaskSyncPolicyBean bean);

    CopsecResult deleteFileTaskMonitorPolicy(UserBean userInfo, String ip, String taskName);

    /**
     * 登陆远程设备
     *
     * @param userInfo
     * @param ip
     * @param userId
     * @param password
     * @param deviceId
     * @param methodType
     * @return 对应设备菜单连接地址
     */
    CopsecResult loginRemoteDevice(UserBean userInfo, String ip, String userId, String password,
                                   String deviceId, String methodType);
}
