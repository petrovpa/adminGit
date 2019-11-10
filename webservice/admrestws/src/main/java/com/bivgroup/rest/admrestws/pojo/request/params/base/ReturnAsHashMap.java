package com.bivgroup.rest.admrestws.pojo.request.params.base;

import com.fasterxml.jackson.annotation.JsonGetter;

import static com.bivgroup.rest.common.Constants.RETURN_AS_HASH_MAP;

public interface ReturnAsHashMap {

    @JsonGetter(RETURN_AS_HASH_MAP)
    default boolean getReturnAsHashMap() {
        return true;
    }

}
