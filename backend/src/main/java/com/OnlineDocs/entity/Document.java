package com.OnlineDocs.entity;

import java.io.Serializable;
import java.util.Date;

public class Document implements Serializable {
    private Integer iddocument;

    private String documentName;

    private Date createTime;

    private Date lastModifyTime;

    private String textPath;

    private String logPath;

    public Integer getIddocument() {
        return iddocument;
    }

    public void setIddocument(Integer iddocument) {
        this.iddocument = iddocument;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName == null ? null : documentName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getTextPath() {
        return textPath;
    }

    public void setTextPath(String textPath) {
        this.textPath = textPath == null ? null : textPath.trim();
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath == null ? null : logPath.trim();
    }
}