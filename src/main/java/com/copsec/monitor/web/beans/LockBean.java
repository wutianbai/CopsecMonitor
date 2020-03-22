package com.copsec.monitor.web.beans;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LockBean {

    private int totalNum; // 总尝试次数

    private int attemptNum;// 已经尝试次数

    private int leaveAttNum;// 可尝试次数

    private long lockTime; // 锁定日期

    private long lockCurrentTime;

    private int InvalidTime;

    private boolean isLocked = false;

    public int getTryTime() {
        return this.totalNum - this.attemptNum;
    }

    public LockBean(int totalNum) {
        this.totalNum = totalNum;
        this.attemptNum = 1;
        this.lockCurrentTime = (new Date()).getTime();
        this.leaveAttNum = this.totalNum - this.attemptNum;
    }

    public void updateAttemTime() {
        this.attemptNum += 1;
    }
}
