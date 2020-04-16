package com.copsec.monitor.web.service;

import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.commons.CopsecResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SystemService {

    CopsecResult login(UserBean userInfo, String ip);

    CopsecResult passwordReset(UserBean userInfo, String orig, String newCode, String ip);

    CopsecResult getAllUserInfo();

    CopsecResult addUserInfo(UserBean userInfo, String ip, UserInfoBean bean, String filePath);

    CopsecResult updateUserInfo(UserBean userInfo, String ip, UserInfoBean bean, String filePath);

    CopsecResult deleteUserInfo(UserBean userInfo, String ip, String userId, String filePath);

    CopsecResult deleteUserInfoList(UserBean userInfo, String ip, List<String> idArray, String filePath);

    Page<LogConditionBean> searchOperateLog(Pageable pageable, LogConditionBean condition);

    CopsecResult deleteCheckOperateLog(UserBean userInfo, String ip, List<String> ids);

    CopsecResult deleteAllLog(UserBean userInfo, String ip);
}
