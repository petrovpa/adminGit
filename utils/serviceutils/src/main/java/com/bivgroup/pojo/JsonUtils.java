package com.bivgroup.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class JsonUtils {

    public static void main(String[] args) {
        System.out.println(
        toJson(new HashMap() {
            {
                put("1", "2");
            }
        }));
    }
    
    public static final String toJson(Object object) {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
    
}
