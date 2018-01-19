package com.xmu.lxq.aiad.model;

import java.io.Serializable;
import java.util.Date;

public class Material implements Serializable {
    private Long materialId;

    private Short style;

    private Integer brightness;

    private Integer rgb;

    private Integer useCount;

    private String fileUrl;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Short getStyle() {
        return style;
    }

    public void setStyle(Short style) {
        this.style = style;
    }

    public Integer getBrightness() {
        return brightness;
    }

    public void setBrightness(Integer brightness) {
        this.brightness = brightness;
    }

    public Integer getRgb() {
        return rgb;
    }

    public void setRgb(Integer rgb) {
        this.rgb = rgb;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl == null ? null : fileUrl.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Material{" +
                "materialId=" + materialId +
                ", style=" + style +
                ", brightness=" + brightness +
                ", rgb=" + rgb +
                ", useCount=" + useCount +
                ", fileUrl='" + fileUrl + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}