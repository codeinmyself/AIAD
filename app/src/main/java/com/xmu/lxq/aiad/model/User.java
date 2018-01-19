package com.xmu.lxq.aiad.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private Integer userId;

    private Long telephone;

    private String password;

    private String email;

    private String nickname;

    private String avatar;

    private Date registerTime;

    private Integer registerIp;

    private static final long serialVersionUID = 1L;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getTelephone() {
        return telephone;
    }

    public void setTelephone(Long telephone) {
        this.telephone = telephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Integer getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(Integer registerIp) {
        this.registerIp = registerIp;
    }

    public User(long telephone,String password){
        this.telephone=telephone;
        this.password=password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", telephone=" + telephone +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", registerTime=" + registerTime +
                ", registerIp=" + registerIp +
                '}';
    }
}