package com.bivgroup.xmlutil.interfaces;

import java.util.List;
import java.util.Map;

public interface ObjectCreator {
    Map<String, Object> createMap();

    List<Object> createList();
}
