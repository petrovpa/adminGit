package com.bivgroup.rest.admrestws.pojo.response.base;

import com.bivgroup.rest.admrestws.pojo.common.ResponseFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static com.bivgroup.rest.common.Constants.RESULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class DefaultResponse<T> extends BaseResponse {
    private T result;

    public DefaultResponse() {
    }

    public DefaultResponse(Map<String, Object> resultMap, Class<T> clazz) {
        ResponseFactory.setStatusToResponse(this);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.result = mapper.convertValue(resultMap, clazz);
    }

    public DefaultResponse(T result) {
        this.result = result;
    }

    public DefaultResponse(String status, String error, T result) {
        super(status, error);
        this.result = result;
    }

    @JsonProperty(RESULT)
    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "DefaultResponse{" +
                "result='" + result + '\''
                + ", " + super.toString() +
                '}';
    }
}
