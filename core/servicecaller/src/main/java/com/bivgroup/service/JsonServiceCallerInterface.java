package com.bivgroup.service;

import com.bivgroup.request.Request;
import com.bivgroup.service.common.IsNeedLogging;

import java.util.Map;

public interface JsonServiceCallerInterface extends IsNeedLogging {
    Map<String, Object> callExternalService(String moduleName, String methodName, Request params, String[] ignorableFieldNames);
}
