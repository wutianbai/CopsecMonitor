package com.copsec.monitor.web.service;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.warning.Report;
import com.copsec.monitor.web.beans.warning.WarningEventBean;
import com.copsec.monitor.web.beans.warning.WarningHistoryBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.entity.WarningHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarningService {
    Page<WarningEventBean> searchWarningEvent(UserBean userInfo, String ip, Pageable pageable, WarningEventBean condition);

    List<WarningEvent> findWarningEventByCondition(WarningEventBean condition);

    CopsecResult handleWarningEvent(UserBean userInfo, String ip, WarningEventBean bean);

    CopsecResult deleteWarningEvent(UserBean userInfo, String ip, WarningEventBean bean);

    CopsecResult handleCheckWarningEvent(UserBean userInfo, String ip, List<String> ids);

    CopsecResult deleteCheckWarningEvent(UserBean userInfo, String ip, List<String> ids);

    CopsecResult handleAllWarningEvent(UserBean userInfo, String ip);

    void insertWarningEvent(WarningEvent bean);

    boolean checkIsWarningByTime(String id);

    boolean deleteWarningEvent(WarningEvent bean);

    boolean handleDeviceOutTimeWarning(String deviceId);

    Page<WarningHistoryBean> searchWarningHistory(UserBean userInfo, String ip, Pageable pageable, WarningHistoryBean condition);

    CopsecResult deleteWarningHistory(UserBean userInfo, String ip, WarningHistoryBean bean);

    CopsecResult deleteCheckWarningHistory(UserBean userInfo, String ip, List<String> ids);

    CopsecResult deleteAllWarningHistory(UserBean userInfo, String ip);

    void insertWarningHistory(WarningHistory bean);

    void receiveWarningEvent(Report report);
}
