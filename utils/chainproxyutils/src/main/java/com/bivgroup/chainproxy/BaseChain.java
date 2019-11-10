package com.bivgroup.chainproxy;

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
public class BaseChain {

    private List<TargetExecutor> executorList = new ArrayList<>();

    public void addExecutor(TargetExecutor executor) {
        executorList.add(executor);
    }

    public void execute(String moduleName, String methodName, Map<String, Object> callresult) throws ChainProxyRunnerException, ChainProxyTargetException {
        for (TargetExecutor executor : executorList) {
            executor.execute(moduleName, methodName, callresult);
        }
    }
}
