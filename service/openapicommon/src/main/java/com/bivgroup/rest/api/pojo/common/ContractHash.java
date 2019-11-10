/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.api.pojo.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 *
 * @author mmamaev
 */
@JsonSerialize(using = ContractHashSerializer.class)
@JsonDeserialize(using = ContractHashDeserializer.class)
public class ContractHash {

    private String hash;

    private Long id;
    private Long updateDateMs;
    private Long encryptDateMs;
    private Boolean isCorrect = Boolean.FALSE;

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

    public Long getUpdateDateMs() {
        return updateDateMs;
    }

    public void setUpdateDateMs(Long updateDateMs) {
        this.updateDateMs = updateDateMs;
    }

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
