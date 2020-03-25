package com.copsec.monitor.web.service.serviceImpl;

import com.copsec.monitor.web.beans.LockBean;
import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.entity.AuditSyslogMessage;
import com.copsec.monitor.web.entity.OperateLog;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.CommonFileReader;
import com.copsec.monitor.web.fileReaders.FileReaderFactory;
import com.copsec.monitor.web.fileReaders.UserFileReader;
import com.copsec.monitor.web.fileReaders.UserInfoReader;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.FileReaderType;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.CommonPools;
import com.copsec.monitor.web.pools.UserInfoPools;
import com.copsec.monitor.web.pools.UserPools;
import com.copsec.monitor.web.pools.WrongPasswordPool;
import com.copsec.monitor.web.repository.AuditSyslogMessageRepository;
import com.copsec.monitor.web.repository.LogRepository;
import com.copsec.monitor.web.service.SystemService;
import com.copsec.monitor.web.utils.FormatUtils;
import com.copsec.monitor.web.utils.MD5Utils.MD5Util;
import com.copsec.monitor.web.utils.logUtils.LogUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
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
    private AuditSyslogMessageRepository auditSyslogMessageRepository;

    @Override
    public CopsecResult login(UserBean userInfo, String ip) {

        UserBean userBean = UserPools.getInstances().get(userInfo.getId());
        if (ObjectUtils.isEmpty(userBean)) {
            LogUtils.sendFailLog(userInfo.getId(), ip, "登录失败,用户不存在", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登录");
            return CopsecResult.failed("用户不存在");
        }
//        boolean access = false;
//        List<AllowedIpBean> list = CommonPools.getInstances().getAllIps();
//        if (ObjectUtils.isEmpty(list) || list.size() == 0) {
//            access = true;
//        } else {
//            for (AllowedIpBean item : list) {
//                if (item.getIp().equals(ip)) {
//                    access = true;
//                    break;
//                }
//            }
//        }
//        if (!access) {
//            LogUtils.sendFailLog(userInfo.getId(), ip, "登录失败,此主机不在远程管理IP列表中", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "登录");
//            return CopsecResult.failed("此主机不在远程管理IP列表中，不允许登录!");
//        }

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
}
