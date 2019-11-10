package com.bivgroup.service;

import com.bivgroup.service.common.IsNeedLogging;

import javax.ws.rs.core.Response;

public interface UrlencodedServiceCallerInterface extends IsNeedLogging {
    Response callExternalService(String paramsStr, String serviceName, String methodName,
                                 String... passedParamNames);
}
