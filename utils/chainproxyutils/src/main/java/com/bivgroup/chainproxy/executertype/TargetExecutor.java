package com.bivgroup.chainproxy.executertype;

import com.bivgroup.chainproxy.enums.TargetType;
import com.bivgroup.chainproxy.exceptions.ChainProxyRunnerException;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;

import java.util.Map;

public interface TargetExecutor extends BaseExecutor {

    // Кастомная ошибка, которая возвращается при возникновении ошибок с нашей стороны
    String EXECUTOR_OUR_ERROR_MESSAGE = "Приносим извинения за временную недоступность сервиса. " +
            "Работоспособность будет восстановлена в ближайшее время.";

    String EXECUTOR_RESULT_MESSAGE = "ExecutorResultMessage";
    String EXECUTOR_RESULT_STATUS = "ExecutorResultStatus";


    void execute(String moduleName, String methodName, Map<String, Object> callResult) throws ChainProxyRunnerException, ChainProxyTargetException;

    void setExecutorType(TargetType executorType);

    TargetType getExecutorType();

    /**
     * Создание параметров для исполняемого модуля
     *
     * @param executorParam
     */
    void createTargetParam(TargetParam executorParam);

    /**
     * Добавление параметров согласно своему типу (если тип не совпадает, то просто игнорируем добавление)
     * @param targetType
     * @param param
     */
    void addTargetParamByType(TargetType targetType, TargetParam param) throws ChainProxyTargetException;

    /**
     * Метод проверки возникновения ошибки
     *
     * @param moduleName
     * @param methodName
     * @param callResult
     * @return
     */
    boolean isHaveErrorInCallResult(String moduleName, String methodName, Map<String, Object> callResult);

    /**
     * Создание маппы с описанием ошибки таргета
     *
     * @param errorMessage
     * @return - маппа со статусом и сообщением
     */
    Map<String, Object> createErrorNote(String errorMessage);

    /**
     * Получить сообщение об ошибке, при ее возникновении
     *
     * @param container
     * @return
     */
    String getErrorMessageFromContainer(Map<String, Object> container);

    /**
     * Метод получения предыдущего сообщения об ошибке (например, для склеивания)
     * @param container
     * @param type
     * @return
     */
    String getPreviousErrorMessageByType(Map<String, Object> container, String type) throws ChainProxyTargetException;

}
