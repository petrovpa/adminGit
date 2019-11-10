package com.bivgroup.rest.admrestws.pojo.common;

import com.bivgroup.rest.admrestws.pojo.response.ErrorResponse;
import com.bivgroup.rest.admrestws.pojo.response.base.BaseResponse;
import com.bivgroup.rest.admrestws.pojo.response.base.DefaultResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.List;
import java.util.Map;

import static com.bivgroup.rest.common.Constants.RESULT;
import static com.bivgroup.utils.ParamGetter.getListParamName;

public class ResponseFactory {
    private static final String SUCCESS_STRING = "Ok";
    private static final String ERROR_STRING = "ERROR";

    public static BaseResponse createEmptyResponse() {
        return new BaseResponse(SUCCESS_STRING);
    }

    public static <T> DefaultResponse<T> createEmptyGenericResponse() {
        DefaultResponse<T> response = new DefaultResponse<>();
        response.setStatus(SUCCESS_STRING);
        return response;
    }

    public static void setStatusToResponse(BaseResponse response) {
        response.setStatus(SUCCESS_STRING);
    }

    public static BaseResponse createErrorResponse(String text) {
        return new ErrorResponse(ERROR_STRING, text);

    }

    public static <T> DefaultResponse<T> createErrorGenericResponse(String text) {
        DefaultResponse<T> response = new DefaultResponse<>();
        response.setStatus(ERROR_STRING);
        response.setError(text);
        return response;
    }

    public static <T> DefaultResponse<T> createResponseByMap(Map<String, Object> map, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DefaultResponse<T> defaultResponse = new DefaultResponse<>();
        defaultResponse.setStatus(SUCCESS_STRING);
        defaultResponse.setResult(mapper.convertValue(map, clazz));
        return defaultResponse;
    }

    public static <T> DefaultResponse<List<T>> createResponseListClassByResultMap(Map<String, Object> resultMap, Class<T> clazz) {
        return createResponseListClassByKeyName(resultMap, RESULT, clazz);
    }

    public static <T> DefaultResponse<List<T>> createResponseListClassByKeyName(Map<String, Object> resultMap, String keyName, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory factory = mapper.getTypeFactory();
        JavaType javaType = factory.constructCollectionType(List.class, clazz);
        DefaultResponse<List<T>> defaultResponse = new DefaultResponse<>();
        defaultResponse.setStatus(SUCCESS_STRING);
        defaultResponse.setResult(mapper.convertValue(getListParamName(resultMap, keyName), javaType));
        return defaultResponse;
    }

    public static DefaultResponse<List<Map<String, Object>>> createResponseListMapByResultMap(Map<String, Object> resultMap) {
        return createResponseListMapByKeyName(resultMap, RESULT);
    }

    public static DefaultResponse<List<Map<String, Object>>> createResponseListMapByKeyName(Map<String, Object> resultMap, String keyName) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory factory = mapper.getTypeFactory();
        JavaType javaType = factory.constructCollectionType(List.class, factory.constructMapLikeType(Map.class, String.class, Object.class));
        DefaultResponse<List<Map<String, Object>>> defaultResponse = new DefaultResponse<>();
        defaultResponse.setStatus(SUCCESS_STRING);
        defaultResponse.setResult(mapper.convertValue(getListParamName(resultMap, keyName), javaType));
        return defaultResponse;
    }

    public static <T> DefaultResponse<T> createResponseByResultMap(Map<String, Object> resultMap, Class<T> clazz) {
        return createResponseByKeyName(resultMap, RESULT, clazz);
    }

    public static <T> DefaultResponse<T> createResponseByKeyName(Map<String, Object> resultMap, String keyName, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DefaultResponse<T> defaultResponse = new DefaultResponse<>();
        defaultResponse.setStatus(SUCCESS_STRING);
        defaultResponse.setResult(mapper.convertValue(resultMap.get(keyName), clazz));
        return defaultResponse;
    }

    public static <T> T createClassByMap(Map<String, Object> map, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.convertValue(map, clazz);
    }

    private ResponseFactory() {

    }

}
