package com.bivgroup.rest.api.system.crypto;

import com.bivgroup.stringutils.StringCryptUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class JsonMapCrypter {

    private final Logger logger = Logger.getLogger(this.getClass());

    // шифровальщик - новый (будет дорабатываться согласно требованиям по безопасности)
    private static final StringCryptUtils scu = new StringCryptUtils();

    // objectMapper
    private ObjectMapper objectMapper = initObjectMapper();

    private ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    public String crypt(Map<String, Object> map) {
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException ex) {
            logger.error(String.format(
                    "Trying to writeValueAsString of map = %s caused exception - '%s'. Details (exception):",
                    map, ex.getMessage()
            ), ex);
        }
        String jsonStrEncrypted = scu.encrypt(jsonStr);
        return jsonStrEncrypted;
    }

    public Map<String, Object> decrypt(String jsonStrEncrypted) {
        String jsonStr = scu.decryptURL(jsonStrEncrypted);
        Map<String, Object> map = null;
        try {
            map = objectMapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
        } catch (IOException ex) {
            logger.error(String.format(
                    "Trying to readValue of string = '%s' caused exception - '%s'. Details (exception):",
                    jsonStr, ex.getLocalizedMessage()
            ), ex);
        }
        return map;
    }

}
