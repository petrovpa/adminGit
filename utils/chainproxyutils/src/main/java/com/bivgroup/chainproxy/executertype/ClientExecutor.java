package com.bivgroup.chainproxy.executertype;

import com.bivgroup.chainproxy.enums.TargetType;
import com.bivgroup.chainproxy.exceptions.ChainProxyRunnerException;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;

import java.util.List;
import java.util.Map;

/**
 * @author Ivanov Roman
 *
 * Интерфейс для сущности клиента исполнителя
 */
public interface ClientExecutor extends BaseExecutor {

    /**
     * Точка исполнения клиентов
     *
     * @param moduleName - имя модуля
     * @param methodName - имя метода
     * @param callResult - мапа результата веб-сервиса
     * @param targetList - типы исполняемых модулей
     * @param targetParam - параметры для исполняемых модулей (параметры не обязательны)
     *
     */
    void execute(String moduleName, String methodName, Map<String, Object> callResult,
                 List<TargetType> targetList, TargetParam targetParam) throws ChainProxyRunnerException, ChainProxyTargetException;


}
