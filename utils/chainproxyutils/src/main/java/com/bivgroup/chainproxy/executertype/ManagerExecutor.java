package com.bivgroup.chainproxy.executertype;

import com.bivgroup.chainproxy.exceptions.ChainProxyRunnerException;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;

import java.util.Map;

/**
 * @author Ivanov Roman
 * <p>
 * Описание поведения для менеджера исполнителей
 */
public interface ManagerExecutor extends BaseExecutor {

    /**
     * Точка исполнения клиентов
     *
     * @param moduleName - имя модуля
     * @param methodName - имя метода
     * @param callResult - мапа результата веб-сервиса
     *
     */
    void execute(String moduleName, String methodName, Map<String, Object> callResult) throws ChainProxyRunnerException, ChainProxyTargetException;
}
