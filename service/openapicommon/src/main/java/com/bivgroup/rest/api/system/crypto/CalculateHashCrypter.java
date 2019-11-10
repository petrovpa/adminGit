/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.api.system.crypto;

import com.bivgroup.rest.api.pojo.common.CalculateHash;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author mmamaev
 */
public class CalculateHashCrypter extends HashCrypter {

    public String makeCalculateEncryptedHash(CalculateHash calculateHash) {
        String calculateEncryptedId = null;
        if (calculateHash != null) {
            Long id = calculateHash.getId();
            if (id != null) {
                Long encryptDateMs = calculateHash.getEncryptDateMs();
                if (encryptDateMs == null) {
                    Date nowDate = new Date();
                    Long nowDateMs = nowDate.getTime();
                    encryptDateMs = nowDateMs;
                }
                calculateEncryptedId = makeCustomEncryptedHash(
                        encryptDateMs,
                        id,
                        calculateHash.getRequestDateMs()
                );
            }
        }
        return calculateEncryptedId;
    }

    public CalculateHash makeCalculateDecryptedHash(String calculateEncryptedHash) {
        CalculateHash calculateHash = new CalculateHash();
        Map<String, Object> calculateHashMap = null;
        try {
            calculateHashMap = unmakeCustomEncryptedHash(calculateEncryptedHash, "encryptDateMs", "id", "requestDateMs");
        } catch (SecurityException ex) {
            // todo: обработка, возможно формирование и выброс нового другого типа и пр
            //throw ex;
            calculateHashMap = null;
            calculateHash.setIsCorrect(Boolean.FALSE);
        }
        if (calculateHashMap != null) {
            Long encryptDateMs = getLongParam(calculateHashMap, "encryptDateMs");
            calculateHash.setEncryptDateMs(encryptDateMs);
            Long id = getLongParam(calculateHashMap, "id");
            calculateHash.setId(id);
            Long requestDateMs = getLongParam(calculateHashMap, "requestDateMs");
            calculateHash.setRequestDateMs(requestDateMs);
            calculateHash.setIsCorrect(Boolean.TRUE);
        }
        return calculateHash;
    }

}