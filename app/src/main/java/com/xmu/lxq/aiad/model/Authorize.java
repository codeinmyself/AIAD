package com.xmu.lxq.aiad.model;

import java.io.Serializable;

public class Authorize implements Serializable {
    private Long authId;

    private Long userId;

    private Byte loginChannel;

    private String identify;

    private String token;

    private static final long serialVersionUID = 1L;

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Byte getLoginChannel() {
        return loginChannel;
    }

    public void setLoginChannel(Byte loginChannel) {
        this.loginChannel = loginChannel;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify == null ? null : identify.trim();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    @Override
    public String toString() {
        return "Authorize{" +
                "authId=" + authId +
                ", userId=" + userId +
                ", loginChannel=" + loginChannel +
                ", identify='" + identify + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}