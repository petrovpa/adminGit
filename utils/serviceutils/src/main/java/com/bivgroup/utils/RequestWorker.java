package com.bivgroup.utils;

import com.bivgroup.pojo.JsonResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;

public class RequestWorker {
    private Logger logger;

    public RequestWorker() {
        logger = Logger.getLogger(this.getClass());
    }

    public RequestWorker(Class<?> clazz) {
        logger = Logger.getLogger(clazz);
    }

    public void serializeJSON(Object value, JsonResult result) {
        logger.debug("Begin serializeJSON ");
        ObjectMapper mapper = new ObjectMapper();
        if (value != null) {
            boolean error = true;
            try {
                result.setResultJson(mapper.writeValueAsString(value));
                error = false;
                result.setResultStatus(JsonResult.RESULT_OK);
            } catch (IOException ex1) {
                logger.error(String.format("Can't deserialize Object (class [%s]) to json ", value.getClass().getName()), ex1);
            }
            if (error) {
                result.setResultStatus(JsonResult.RESULT_ERROR);
                logger.error("Error serialize object ");
            }
        } else {
            result.setResultStatus(JsonResult.RESULT_ERROR);
            logger.error("Value for serialization is null");
        }

        logger.debug("End serializeJSON");
    }

    public <T> T deserializeJSON(String json, Class<T> deserializeClass) {
        logger.debug("Begin deserializeJSON");
        T result = null;
        if ((json != null) && !json.isEmpty() && (deserializeClass != null)) {
            logger.debug(String.format("Try deserialize json value [%s] to class [%s]", json, deserializeClass.getName()));
            ObjectMapper mapper = new ObjectMapper();
            boolean error = true;
            try {
                result = mapper.readValue(json, deserializeClass);
                error = false;
            } catch (IOException ex) {
                logger.error(String.format("Can't deserialize json value [%s] to class [%s]", json, deserializeClass.getName()), ex);
            }
            if (error) {
                logger.error(String.format("Can't deserialize json value [%s] to class [%s]", json, deserializeClass.getName()));
                try {
                    result = deserializeClass.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    logger.error(String.format("Can't instantiate new object from class [%s]", deserializeClass.getName()), ex);
                }
            }
        } else {
            if ((json != null) && !json.isEmpty()) {
                logger.info("Json is null or empty");
            }
            if (deserializeClass == null) {
                logger.info("deserializeClass is null");
            } else {
                try {
                    result = deserializeClass.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    logger.error(String.format("Can't instantiate new object from class [%s]", deserializeClass.getName()), ex);
                }
            }
        }
        logger.debug("End deserializeJSON ");
        return result;
    }
}
