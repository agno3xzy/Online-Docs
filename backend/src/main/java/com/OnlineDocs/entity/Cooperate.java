package com.OnlineDocs.entity;

import java.io.Serializable;

public class Cooperate implements Serializable {
    private String permission;

    private Integer userIduser;

    private Integer documentIddocument;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission == null ? null : permission.trim();
    }

    public Integer getUserIduser() {
        return userIduser;
    }

    public void setUserIduser(Integer userIduser) {
        this.userIduser = userIduser;
    }

    public Integer getDocumentIddocument() {
        return documentIddocument;
    }

    public void setDocumentIddocument(Integer documentIddocument) {
        this.documentIddocument = documentIddocument;
    }
}