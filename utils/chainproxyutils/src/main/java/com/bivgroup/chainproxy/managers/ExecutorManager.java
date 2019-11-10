package com.bivgroup.chainproxy.managers;

import com.bivgroup.chainproxy.BaseChain;
import com.bivgroup.chainproxy.exceptions.ChainProxyRunnerException;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;
import com.bivgroup.chainproxy.executertype.ManagerExecutor;
import com.bivgroup.chainproxy.executertype.TargetExecutor;

import java.util.Map;

/**
 * @author Ivanov Roman
 * <p>
 * Класс-управление цепочками вызовов
 **/
public class ExecutorManager implements ManagerExecutor {

    BaseChain baseChain;

    public void addExecutor(TargetExecutor executor) {
        if (baseChain == null) {
            baseChain = new BaseChain();
    }
        baseChain.addExecutor(executor);
    }

    @Override
    public void execute(String moduleName, String methodName, Map<String, Object> callResult) throws ChainProxyRunnerException, ChainProxyTargetException {
        if ((baseChain != null) && (isAvailableModule(moduleName))) {
            baseChain.execute(moduleName, methodName, callResult);
        }
    }

    @Override
    public boolean isAvailableModule(String moduleName) {
        return moduleNameSet.contains(moduleName);
    }

    @Override
    public String toString() {
        return "ExecutorManager{" +
                "baseChain=" + baseChain +
                '}';
    }
}
