package com.bivgroup.rest.api.pojo.common;

public class EntityHash {

    private String hash;

    private Long id;
    // private Long updateDateMs;
    private Long encryptDateMs;
    private Boolean isCorrect = Boolean.FALSE;

    public EntityHash() {
    }

    public EntityHash(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*
    public Long getUpdateDateMs() {
        return updateDateMs;
    }

    public void setUpdateDateMs(Long updateDateMs) {
        this.updateDateMs = updateDateMs;
    }
    */

    public Long getEncryptDateMs() {
        return encryptDateMs;
    }

    public void setEncryptDateMs(Long encryptDateMs) {
        this.encryptDateMs = encryptDateMs;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

}