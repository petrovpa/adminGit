package com.bivgroup.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Map;
import java.util.Objects;

public final class ConvertUtils {

    private ConvertUtils() {

    }

    public static Map<String, Object> convertObjectToMapWithoutNull(final Object object) {

        final ObjectMapper mapper = new ObjectMapper();
        FilterProvider filters = new SimpleFilterProvider().addFilter("ignorableFilter",
                SimpleBeanPropertyFilter.serializeAllExcept());
        mapper.setFilters(filters);
        final TypeFactory factory = mapper.getTypeFactory();
        final JavaType mapType = factory.constructMapLikeType(Map.class, String.class, Object.class);

        final Map<String, Object> map = mapper.convertValue(object, mapType);

        map.values().removeIf(Objects::isNull);

        return map;
    }
}
