package com.bivgroup.config.common;

public class ConfigException extends Exception {
    public ConfigException(Exception e) {
        super(e);
    }

    public ConfigException(String message, Exception e) {
        super(message, e);
    }

    public ConfigException(String message) {
        super(message);
    }
}
