package com.xmu.lxq.aiad.model;

import java.io.Serializable;

public class OperationLog implements Serializable {
    private Long operationId;

    private Long userId;

    private Integer productType;

    private Integer adStyle;

    private Integer adTime;

    private String videoUrl;

    private Byte isSatasfied;

    private static final long serialVersionUID = 1L;

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Integer getAdStyle() {
        return adStyle;
    }

    public void setAdStyle(Integer adStyle) {
        this.adStyle = adStyle;
    }

    public Integer getAdTime() {
        return adTime;
    }

    public void setAdTime(Integer adTime) {
        this.adTime = adTime;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl == null ? null : videoUrl.trim();
    }

    public Byte getIsSatasfied() {
        return isSatasfied;
    }

    public void setIsSatasfied(Byte isSatasfied) {
        this.isSatasfied = isSatasfied;
    }

    @Override
    public String toString() {
        return "OperationLog{" +
                "operationId=" + operationId +
                ", userId=" + userId +
                ", productType=" + productType +
                ", adStyle=" + adStyle +
                ", adTime=" + adTime +
                ", videoUrl='" + videoUrl + '\'' +
                ", isSatasfied=" + isSatasfied +
                '}';
    }
}