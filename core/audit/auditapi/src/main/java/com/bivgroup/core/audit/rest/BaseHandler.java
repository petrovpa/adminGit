package com.bivgroup.core.audit.rest;

import com.bivgroup.core.audit.annotation.AuditUserInfo;
import com.bivgroup.core.audit.annotation.result.AuditResultOperationHandler;
import com.bivgroup.utils.ParamGetter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.ws.rs.container.ResourceInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseHandler implements AuditResponseAndRequestHandler {
    protected static final Marker WORK_ERROR_MARKER = MarkerManager.getMarker("work_error");
    protected Logger logger = LogManager.getLogger(this.getClass().getName());

    protected Map<String, Object> getMapFormString(String value, String auditParamValue) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String[] auditParamValues = auditParamValue.split("=");
        String saveName = auditParamValues[0];
        Object saveValue = value;
        if (auditParamValues.length > 1) {
            String[] pathValues = auditParamValues[1].split("\\.");
            Map<String, Object> valueMap = convertJsonStringToMap(value);
            String path;
            int pathValuesLength = pathValues.length;
            for (int i = 0; i < pathValuesLength; i++) {
                path = pathValues[i];
                if (i + 1 == pathValuesLength) {
                    saveValue = valueMap.get(path);
                } else {
                    valueMap = ParamGetter.getMapParamName(valueMap, path);
                }
            }
        }
        result.put(saveName, saveValue);
        return result;
    }

    protected String getUserInfo(String value, String path) throws IOException {
        String result = value;
        String[] paths = path.split("\\.");
        Map<String, Object> valueMap = convertJsonStringToMap(value);
        int pathLength = paths.length;
        String item;
        for (int i = 0; i < pathLength; i++) {
            item = paths[i];
            if (i + 1 == pathLength) {
                result = ParamGetter.getStringParam(valueMap, item);
            } else {
                valueMap = ParamGetter.getMapParamName(valueMap, item);
            }
        }
        return result;
    }

    protected Map<String, Object> getParameterObjectValue(String value, String auditParamObjValue) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String[] auditParamValues = auditParamObjValue.split("=");
        Map<String, Object> valueMap = convertJsonStringToMap(value);
        String saveName = auditParamValues[0];
        if (auditParamValues.length > 1) {
            String[] pathValues = auditParamValues[1].split("\\.");
            for (String pathValue : pathValues) {
                valueMap = ParamGetter.getMapParamName(valueMap, pathValue);
            }
        }
        result.put(saveName, valueMap);
        result.put("saveName", saveName);
        return result;
    }

    protected Object getObjectParamByPath(Map<String, Object> map, String path) {
        String[] pathValues = path.split("\\.");
        Object result = null;
        for (int i = 0; i < pathValues.length; i++) {
            String value = pathValues[i];
            if (i + 1 == pathValues.length) {
                result = map.get(value);
            } else {
                map = getMapByKey(map, value);
            }
        }
        return result;
    }

    protected Map<String, Object> getMapParamByPath(Map<String, Object> map, String path) {
        String[] pathValues = path.split("\\.");
        for (String pathValue : pathValues) {
            map = getMapByKey(map, pathValue);
        }
        return map;
    }

    private Map<String, Object> getMapByKey(Map<String, Object> map, String key) {
        Object innerValue = map.get(key);
        if (innerValue instanceof String) {
            try {
                map = convertJsonStringToMap((String) innerValue);
            } catch (IOException e) {
                logger.error(WORK_ERROR_MARKER, "error parse json string value {}", innerValue);
                logger.error(WORK_ERROR_MARKER, "error parse json string", e);
            }
        }
        if (innerValue instanceof Map) {
            map = (Map<String, Object>) innerValue;
        }
        return map;
    }

    protected AuditUserInfo getAuditUserInfo(ResourceInfo resourceInfo) {
        AuditUserInfo auditUserInfo = resourceInfo.getResourceMethod().getAnnotation(AuditUserInfo.class);
        if (auditUserInfo == null) {
            auditUserInfo = resourceInfo.getResourceClass().getAnnotation(AuditUserInfo.class);
        }
        return auditUserInfo;
    }

    protected AuditResultOperationHandler getAuditResultOperationHandler(ResourceInfo resourceInfo) {
        AuditResultOperationHandler auditUserInfo = resourceInfo.getResourceMethod().getAnnotation(AuditResultOperationHandler.class);
        if (auditUserInfo == null) {
            auditUserInfo = resourceInfo.getResourceClass().getAnnotation(AuditResultOperationHandler.class);
        }
        return auditUserInfo;
    }

    protected Map<String, Object> convertJsonStringToMap(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory factory = mapper.getTypeFactory();
        JavaType mapType = factory.constructMapLikeType(Map.class, String.class, Object.class);
        return mapper.readValue(jsonString, mapType);
    }

    protected Map<String, Object> convertObjectToMap(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory factory = mapper.getTypeFactory();
        JavaType mapType = factory.constructMapLikeType(Map.class, String.class, Object.class);
        return mapper.convertValue(object, mapType);
    }
}
