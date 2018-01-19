package com.xmu.lxq.aiad.model;

import java.io.Serializable;
import java.util.Date;

public class Practice implements Serializable {
    private Long practiceId;

    private String sourceFileUrl;

    private String destinationFileUrl;

    private Integer fileNumber;

    private Date practiceTime;

    private static final long serialVersionUID = 1L;

    public Long getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Long practiceId) {
        this.practiceId = practiceId;
    }

    public String getSourceFileUrl() {
        return sourceFileUrl;
    }

    public void setSourceFileUrl(String sourceFileUrl) {
        this.sourceFileUrl = sourceFileUrl == null ? null : sourceFileUrl.trim();
    }

    public String getDestinationFileUrl() {
        return destinationFileUrl;
    }

    public void setDestinationFileUrl(String destinationFileUrl) {
        this.destinationFileUrl = destinationFileUrl == null ? null : destinationFileUrl.trim();
    }

    public Integer getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(Integer fileNumber) {
        this.fileNumber = fileNumber;
    }

    public Date getPracticeTime() {
        return practiceTime;
    }

    public void setPracticeTime(Date practiceTime) {
        this.practiceTime = practiceTime;
    }

    @Override
    public String toString() {
        return "Practice{" +
                "practiceId=" + practiceId +
                ", sourceFileUrl='" + sourceFileUrl + '\'' +
                ", destinationFileUrl='" + destinationFileUrl + '\'' +
                ", fileNumber=" + fileNumber +
                ", practiceTime=" + practiceTime +
                '}';
    }
}