package com.bivgroup.rest.api.system.crypto;

import com.bivgroup.rest.api.pojo.common.EntityHash;

import java.util.Date;
import java.util.Map;

public class EntityHashCrypter extends HashCrypter {

    public String makeEntityEncryptedHash(EntityHash entityHash) {
        String entityEncryptedId = null;
        if (entityHash != null) {
            Long id = entityHash.getId();
            if (id != null) {
                Long encryptDateMs = entityHash.getEncryptDateMs();
                if (encryptDateMs == null) {
                    Date nowDate = new Date();
                    Long nowDateMs = nowDate.getTime();
                    encryptDateMs = nowDateMs;
                }
                entityEncryptedId = makeCustomEncryptedHash(
                        encryptDateMs,
                        id /*,
                        contractHash.getUpdateDateMs()
                        */
                );
            }
        }
        return entityEncryptedId;
    }

    public String makeEntityEncryptedHash(Long id) {
        EntityHash entityHash = new EntityHash(id);
        String entityEncryptedId = makeEntityEncryptedHash(entityHash);
        return entityEncryptedId;
    }

    public EntityHash makeEntityDecryptedHash(String entityEncryptedHash) {
        EntityHash entityHash = new EntityHash();
        Map<String, Object> entityHashMap = null;
        try {
            entityHashMap = unmakeCustomEncryptedHash(entityEncryptedHash, "encryptDateMs", "id"/*, "updateDateMs"*/);
        } catch (SecurityException ex) {
            // todo: обработка, возможно формирование и выброс нового другого типа и пр
            //throw ex;
            entityHashMap = null;
            entityHash.setIsCorrect(Boolean.FALSE);
        }
        if (entityHashMap != null) {
            Long encryptDateMs = getLongParam(entityHashMap, "encryptDateMs");
            entityHash.setEncryptDateMs(encryptDateMs);
            Long id = getLongParam(entityHashMap, "id");
            entityHash.setId(id);
            /*
            Long updateDateMs = getLongParam(entityHashMap, "updateDateMs");
            entityHash.setUpdateDateMs(updateDateMs);
            */
            entityHash.setIsCorrect(Boolean.TRUE);
        }
        return entityHash;
    }

}
