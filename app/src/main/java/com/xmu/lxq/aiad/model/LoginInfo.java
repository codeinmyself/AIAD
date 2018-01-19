package com.xmu.lxq.aiad.model;

import java.io.Serializable;
import java.util.Date;

public class LoginInfo implements Serializable {
    private Long loginId;

    private String userId;

    private Date loginTime;

    private Integer elapseTime;

    private static final long serialVersionUID = 1L;

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Integer getElapseTime() {
        return elapseTime;
    }

    public void setElapseTime(Integer elapseTime) {
        this.elapseTime = elapseTime;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "loginId=" + loginId +
                ", userId='" + userId + '\'' +
                ", loginTime=" + loginTime +
                ", elapseTime=" + elapseTime +
                '}';
    }
}