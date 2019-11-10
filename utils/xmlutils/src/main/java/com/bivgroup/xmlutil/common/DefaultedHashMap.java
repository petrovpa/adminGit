package com.bivgroup.xmlutil.common;

import java.util.HashMap;

public class DefaultedHashMap<K, T> extends HashMap<K, T> {
    public DefaultedHashMap() {
    }

    public T getOrReturnDefaultValue(K key, T defaultValue) {
        T value = super.get(key);
        return value == null ? defaultValue : value;
    }

    public T removeOrReturnDefaultValue(K key, T defaultValue) {
        T value = super.remove(key);
        return value == null ? defaultValue : value;
    }
}
