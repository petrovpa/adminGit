/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.api.system.crypto;

import com.bivgroup.stringutils.StringCryptUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mmamaev
 */
public class HashCrypter {

    private final Logger logger = Logger.getLogger(this.getClass());

    private static final String ENCRYPTION_DIVIDER = "__div__";
    private static final String ENCRYPTION_FILE_NAMES_STR_DIVIDER = "@";

    /** строкове обозначение для пустых/неуказанных и пр. компонентов шифруемого ИД */
    protected static final String ENCRYPTED_NULL_VALUE = "null";

    // шифровальщик - новый (будет дорабатываться согласно требованиям по безопасности)
    private static final StringCryptUtils scu = new StringCryptUtils();

    protected Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected Long getLongParam(Map<String, Object> map, String keyName) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParam(map.get(keyName));
        }
        return longParam;
    }

    private String encrypt(String str) throws SecurityException {
        return scu.encrypt(str);
    }

    private String decrypt(String str) throws SecurityException {
        return scu.decrypt(str);
    }

    private String decryptURL(String str) throws SecurityException {
        return scu.decryptURL(str);
    }

    protected Map<String, Object> unmakeCustomEncryptedHash(String customHash, String... dataKeyNames) throws SecurityException {
        Map<String, Object> unmakedCustomRowHashMap = new HashMap<>();
        String customHashDecrypted;
        try {
            // для случаев, когда передано через rest
            customHashDecrypted = decrypt(customHash);
        } catch (SecurityException ex1) {
            try {
                // для случаев, когда передано из онлайн-интерфейса (а в онлайн-интерфейс - через параметры роута, например, "...?calculateId=...")
                customHashDecrypted = decryptURL(customHash);
            } catch (SecurityException ex2) {
                logger.error("Decryption (decrypt) in unmakeCustomEncryptedHash caused exception: ", ex1);
                logger.error("Decryption (decryptURL) in unmakeCustomEncryptedHash caused exception: ", ex2);
                throw ex2;
            }
        }
        String[] customHashDecryptedArr = customHashDecrypted.split(ENCRYPTION_DIVIDER);
        try {
            for (int i = 0; i < dataKeyNames.length; i++) {
                String dataKeyName = dataKeyNames[i];
                String dataValue = customHashDecryptedArr[i];
                if (ENCRYPTED_NULL_VALUE.equalsIgnoreCase(dataValue)) {
                    dataValue = null;
                }
                unmakedCustomRowHashMap.put(dataKeyName, dataValue);
            }
        } catch (Exception ex) {
            logger.error("Decrypted data analyse in unmakeCustomEncryptedHash caused exception: ", ex);
            throw ex;
        }
        return unmakedCustomRowHashMap;
    }

    private String makeCustomEncryptedHashStr(Object[] dataItems, String divider) throws SecurityException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dataItems.length; i++) {
            Object dataItemObj = dataItems[i];
            String dataItemStr = (dataItemObj == null) ? ENCRYPTED_NULL_VALUE : dataItemObj.toString();
            sb.append(dataItemStr);
            sb.append(divider);
        }
        sb.setLength(sb.length() - divider.length());
        String customEncryptedStr = encrypt(sb.toString());
        return customEncryptedStr;
    }

    protected String makeCustomEncryptedHash(Object... dataItems) {
        String divider = ENCRYPTION_DIVIDER;
        String customHash = makeCustomEncryptedHashStr(dataItems, divider);
        return customHash;
    }

    protected String makeCustomEncryptedFileNameHash(Object... dataItems) {
        String divider = ENCRYPTION_FILE_NAMES_STR_DIVIDER;
        String customHash = makeCustomEncryptedHashStr(dataItems, divider);
        return customHash;
    }

}
