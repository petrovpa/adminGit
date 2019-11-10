/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.api.system.crypto;

import com.bivgroup.rest.api.pojo.common.ContractHash;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author mmamaev
 */
public class ContractHashCrypter extends HashCrypter {

    public String makeContractEncryptedHash(ContractHash contractHash) {
        String contractEncryptedId = null;
        if (contractHash != null) {
            Long id = contractHash.getId();
            if (id != null) {
                Long encryptDateMs = contractHash.getEncryptDateMs();
                if (encryptDateMs == null) {
                    Date nowDate = new Date();
                    Long nowDateMs = nowDate.getTime();
                    encryptDateMs = nowDateMs;
                }
                contractEncryptedId = makeCustomEncryptedHash(
                        encryptDateMs,
                        id,
                        contractHash.getUpdateDateMs()
                );
            }
        }
        return contractEncryptedId;
    }

    public ContractHash makeContractDecryptedHash(String contractEncryptedHash) {
        ContractHash contractHash = new ContractHash();
        Map<String, Object> contractHashMap = null;
        try {
            contractHashMap = unmakeCustomEncryptedHash(contractEncryptedHash, "encryptDateMs", "id", "updateDateMs");
        } catch (SecurityException ex) {
            // todo: обработка, возможно формирование и выброс нового другого типа и пр
            //throw ex;
            contractHashMap = null;
            contractHash.setIsCorrect(Boolean.FALSE);
        }
        if (contractHashMap != null) {
            Long encryptDateMs = getLongParam(contractHashMap, "encryptDateMs");
            contractHash.setEncryptDateMs(encryptDateMs);
            Long id = getLongParam(contractHashMap, "id");
            contractHash.setId(id);
            Long updateDateMs = getLongParam(contractHashMap, "updateDateMs");
            contractHash.setUpdateDateMs(updateDateMs);
            contractHash.setIsCorrect(Boolean.TRUE);
        }
        return contractHash;
    }

}
