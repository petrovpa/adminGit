package com.bivgroup.chainproxy.executorchain;

import com.bivgroup.chainproxy.exceptions.ChainProxyRunnerException;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;
import com.bivgroup.chainproxy.executertype.TargetExecutor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ivanov Roman
 * Класс, который хранит цепочку вызовов executor`а
 **/
public class ExecutorChain {

    private List<TargetExecutor> executors = new ArrayList<>();

    public void addExecutor(TargetExecutor executor) {
        executors.add(executor);
    }

    public void execute(String moduleName ,String methodName, Map<String, Object> callResult) throws ChainProxyRunnerException, ChainProxyTargetException {
        for (TargetExecutor executor : executors) {
            executor.execute(moduleName,methodName, callResult);
        }
    }
}
