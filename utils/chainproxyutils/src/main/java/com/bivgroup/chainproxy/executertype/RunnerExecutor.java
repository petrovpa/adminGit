package com.bivgroup.chainproxy.executertype;

import com.bivgroup.chainproxy.enums.TargetType;
import com.bivgroup.chainproxy.exceptions.ChainProxyRunnerException;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;

import java.util.List;
import java.util.Map;

/**
 * @author Ivanov Roman
 *
 * Точка входа для прокси обработки результата
 */
public interface RunnerExecutor extends BaseExecutor {

    void execute(String moduleName, String methodName, Map<String, Object> callResult,
                 List<TargetType> targetList, TargetParam targetParam) throws ChainProxyRunnerException, ChainProxyTargetException;
}
