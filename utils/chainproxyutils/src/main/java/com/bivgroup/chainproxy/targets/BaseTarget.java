package com.bivgroup.chainproxy.targets;

import com.bivgroup.chainproxy.enums.TargetType;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;
import com.bivgroup.chainproxy.executertype.TargetConstant;
import com.bivgroup.chainproxy.executertype.TargetExecutor;
import com.bivgroup.chainproxy.executertype.TargetParam;

import java.util.*;

/**
 * @author Ivano Roman
 * <p>
 * Базовый класс для таргетов. Необходим для хранения маппинга параметров (и д.р.)
 */
public abstract class BaseTarget implements TargetExecutor, TargetConstant {

    // Ошибка по умолчанию
    protected String errorMessage = "Приносим извинения за временную недоступность сервиса. Работоспособность будет восстановлена в ближайшее время.";
    // Тип исполнителя
    protected TargetType targetType = TargetType.EXECUTOR_DEFAULT_TYPE;
    // Параметры исполнителя
    protected List<String> targetParam = new ArrayList<>();

    /**
     * Проверка, доступно ли выполнение на уровне данного модуля
     *
     * @param moduleName
     * @return true - доступно, false - недоступно
     */
    @Override
    public boolean isAvailableModule(String moduleName) {
        return moduleNameSet.contains(moduleName);
    }

    @Override
    public void setExecutorType(TargetType targetType) {
        this.targetType = targetType;
    }

    @Override
    public TargetType getExecutorType() {
        return targetType;
    }

    @Override
    public void createTargetParam(TargetParam executorParam) {
        this.targetParam = new ArrayList<>();
    }

    @Override
    public void addTargetParamByType(TargetType targetType, TargetParam param) throws ChainProxyTargetException {
        //
        if ((targetType == null)) {
            throw new ChainProxyTargetException("Target param with addTargetParamByType is null, please check with");
        }

        if (this.targetType.toString().equalsIgnoreCase(targetType.toString())) {
            if (targetParam == null) {
                targetParam = new ArrayList<>();
            } else {
                final Map<TargetType, List<String>> targetTypeListMap = param.getParam();
                // Ищем наши параметры по типу
                if (targetTypeListMap.containsKey(this.targetType)) {
                    // если нашли, то пребросим к себе
                    final List<String> paramList = targetTypeListMap.get(this.targetType);
                    targetParam.addAll(paramList);
                }
            }
        }
    }

    @Override
    public boolean isHaveErrorInCallResult(String moduleName, String methodName, Map<String, Object> callResult) {

        boolean isHaveError = false;

        // Для новых запросов
        if ((callResult == null) && (callResult.get(ERROR_PARAMNAME) != null)
                && (ERROR_PARAMNAME.equalsIgnoreCase(callResult.get(ERROR_PARAMNAME).toString()))) {
            isHaveError = true;
        }

        // Для старых запросов, типа ВЗР
        if (!isHaveError) {
            // Проверка запросов, которые возвращают в Result еще один Result ?!
            if ((callResult != null) && (callResult.containsKey(RESULT))) {
                if (callResult.get(RESULT) instanceof Map) {
                    Map<String, Object> result = (Map<String, Object>) callResult.get(RESULT);
                    if ((result != null) && (result.get(OLD_STATUS_PARAMNAME) != null)
                            && (OLD_ERROR_PARAMNAME.equalsIgnoreCase(result.get(OLD_STATUS_PARAMNAME).toString()))) {
                        isHaveError = true;
                    }
                }
            }
        }
        return isHaveError;
    }

    /**
     * Создание маппы с описание и статусом ошибки
     *
     * @param errorMessage
     * @return
     */
    @Override
    public Map<String, Object> createErrorNote(String errorMessage) {
        Map<String, Object> map = new HashMap<>();
        map.put(EXECUTOR_RESULT_STATUS, ERROR_PARAMNAME);

        // Если, вдруг, ошибка неизвестна, то считаем, что это наша ошибка
        if (((errorMessage == null) || (errorMessage.isEmpty()))) {
            errorMessage = EXECUTOR_OUR_ERROR_MESSAGE;
        }

        map.put(EXECUTOR_RESULT_MESSAGE, errorMessage);
        return map;
    }

    /**
     * Метод по получению ошибки, при ее возникновении
     *
     * @param callResult
     * @return
     */
    @Override
    public String getErrorMessageFromContainer(Map<String, Object> callResult) {
        String errorMessage = EXECUTOR_OUR_ERROR_MESSAGE;
        boolean isOldResult = false;

        // Проверка запросов, которые возвращают в Result еще один Result ?!
        if ((callResult != null) && (callResult.containsKey(RESULT))) {
            Map<String, Object> result = (Map<String, Object>) callResult.get(RESULT);
            if ((result != null) && (result.get(OLD_STATUS_PARAMNAME) != null)
                    && (OLD_ERROR_PARAMNAME.equalsIgnoreCase(result.get(OLD_STATUS_PARAMNAME).toString()))) {
                if (callResult.get(OLD_ERROR_PARAMNAME) != null) {
                    errorMessage = (String) callResult.get(OLD_ERROR_PARAMNAME);
                }
                isOldResult = true;
            }
        }

        // При условии, что это все-таки не старый вид ответа
        if (!isOldResult) {
            // Для новых запросов
            if ((callResult == null) && (callResult.get(ERROR_PARAMNAME) != null)
                    && (ERROR_PARAMNAME.equalsIgnoreCase(callResult.get(ERROR_PARAMNAME).toString()))) {
                if (callResult.get(RESULT) != null) {
                    errorMessage = (String) callResult.get(RESULT);
                }
            }
        }

        return errorMessage;
    }

    @Override
    public String getPreviousErrorMessageByType(Map<String, Object> container, String type) throws ChainProxyTargetException {
        String errorMessage = "";

        // Тип должен быть определен
        if ((type == null) || (type.isEmpty())) {
            throw new ChainProxyTargetException("Target not have type in method getPreviousErrorMessageByType");
        }

        if ((container != null) && (container.containsKey(type))) {
            final Map<String, Object> executorRes = (Map<String, Object>) container.get(type);
            if (((executorRes != null) && (executorRes.containsKey(EXECUTOR_RESULT_MESSAGE)))) {
                final String executorResultMessage = (String) executorRes.get(EXECUTOR_RESULT_MESSAGE);

                if (executorResultMessage != null) {
                    errorMessage = executorResultMessage;
                }
            }
        }

        return errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public List<String> getTargetParam() {
        return targetParam;
    }

    public void setTargetParam(List<String> targetParam) {
        this.targetParam = targetParam;
    }

    @Override
    public String toString() {
        return "BaseTarget{" +
                "targetType=" + targetType +
                ", targetParam=" + targetParam +
                '}';
    }
}
