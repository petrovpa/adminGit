package com.bivgroup.querybuilder.common.idcache;

public class IdObtainedException extends Exception {
    public IdObtainedException(String message) {
        super(message);
    }

    public IdObtainedException(String message, Exception e) {
        super(message, e);
    }
}
