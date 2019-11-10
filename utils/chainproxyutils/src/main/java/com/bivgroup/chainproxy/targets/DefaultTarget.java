package com.bivgroup.chainproxy.targets;

import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivanov Roman
 * <p>
 * Исполняемая часть по умолчанию
 */
public class DefaultTarget extends BaseTarget {

    final String DEFAULT_MESSAGE_VALUE = EXECUTOR_OUR_ERROR_MESSAGE;

    /**
     * Метод выполнения по умолчанию
     *
     * @param moduleName
     * @param methodName
     * @param callResult
     */
    public void execute(String moduleName, String methodName, Map<String, Object> callResult) throws ChainProxyTargetException {
        if (isAvailableModule(moduleName)) {

            // Тип нашего исполняемого модуля
            final String moduleTypeName = this.getExecutorType().toString();

            // Если пришел нул, ошибка !
            if (callResult == null) {
                callResult = new HashMap<>();

                /**
                 * Тип всегда должен соответствовать тем, что описаны в {@link com.bivgroup.chainproxy.enums.TargetType}
                 */
                if ((this.getExecutorType() == null) || (this.getExecutorType().toString().isEmpty())) {
                    throw new ChainProxyTargetException(String.format("Executor type with class %s null or empty!", this.getClass().getName()));
                }

                callResult.put(moduleTypeName, createErrorNote(DEFAULT_MESSAGE_VALUE));
            }

            // Если в маппе есть Error - ошибка!
            if (isHaveErrorInCallResult(moduleName, methodName, callResult)) {

                String previousErrorMessage = "";

                // Вызов может быть уже повторный, будем склеивать (Имя метода: ошибка)
                if (callResult.containsKey(moduleTypeName)) {
                    previousErrorMessage = getPreviousErrorMessageByType(callResult, moduleTypeName);
                }

                // Тогда вытащим эту ошибку и вернем ее дополнительно
                final String errorMessage = getErrorMessageFromContainer(callResult);
                callResult.put(moduleTypeName, createErrorNote(previousErrorMessage +
                        NEW_LINE + methodName + " : " + errorMessage));
            }
        }
    }
}
