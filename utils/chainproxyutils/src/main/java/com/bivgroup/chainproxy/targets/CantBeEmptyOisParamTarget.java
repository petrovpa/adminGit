package com.bivgroup.chainproxy.targets;

import com.bivgroup.chainproxy.exceptions.ChainProxyRunnerException;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;

import java.util.*;

/**
 * @author Ivanov Roman
 * <p>
 * Исполнитель проверяющий параметры, которые не должны быть null пустыми
 */
public class CantBeEmptyOisParamTarget extends BaseTarget {

    private String moduleName;
    private String methodName;
    private Map<String, Object> callResult;

    // Сообщение описывающее сообщение при ошибке при вызове интеграции
    private static final String INTEGRATION_ERROR_MESSAGE = "Приносим извинения за временную недоступность сервиса. Ведутся технические работы. Сервис станет доступен в скором времени.";
    // Список имен сервисов, которые обязывают что нужный нам параметр прийдет не пустым
    private static final Set<String> methodNameSet = new HashSet<>();

    static {
        methodNameSet.add("dsLifeIntegrationGetContractList");
        methodNameSet.add("dsPA2ContractBrowseFullMapEx");
    }

    @Override
    public void execute(String moduleName, String methodName, Map<String, Object> callResult) throws ChainProxyRunnerException, ChainProxyTargetException {

        // Проверяем только для зарегистрированных методов
        if ((isAvailableModule(moduleName)) && (methodNameSet.contains(methodName))) {

            setModuleName(moduleName);
            setMethodName(methodName);
            setCallResult(callResult);

            // Тип нашего исполняемого модуля
            final String moduleTypeName = this.getExecutorType().toString();

            // Если пришел нул, ошибка !
            if (callResult == null) {
                callResult = new HashMap<>();

                /**
                 * Тип всегда должен соответствовать тем, что описаны в {@link com.bivgroup.chainproxy.enums.TargetType}
                 */
                if ((this.getExecutorType() == null) || (this.getExecutorType().toString().isEmpty())) {
                    throw new ChainProxyTargetException(String.format("Executor type with class %s null or empty!",
                            this.getClass().getName()));
                }

                // Если вдруг резалт пришел как нул, то это ошибка наших сервисов
                callResult.put(moduleTypeName, createErrorNote(EXECUTOR_OUR_ERROR_MESSAGE));
            }


            // Если в маппе есть Error - ошибка!
            if (isHaveErrorInCallResult(moduleName, methodName, callResult)) {
                setError(methodName, callResult, moduleTypeName);
            } else {
                // Если ошибки нет, то поищем, и если найдем, то установим
                checkIsNullOrEmptyParam(callResult);
            }
        }
    }

    /**
     * Метод проверки параметров, которые не должны быть пустыми или нулом
     *
     * @param callResult
     */
    private void checkIsNullOrEmptyParam(Map<String, Object> callResult) throws ChainProxyTargetException {
        // Получим наши параметры, если параметров нет вообще, то все ок
        if (targetParam == null || targetParam.isEmpty()) {
            return;
        }

        // Сюда складываем отсутствующие параметры
        List<String> missedParam = new ArrayList<>();
        // иначе проверяем, соберем нужные нам, а потом отобразим в ошибку
        targetParam.stream()
                .forEach(paramName -> checkMissedParamInContainerAndAddThisInMissedContainer(callResult,
                        missedParam, paramName));
        if (!missedParam.isEmpty()) {
            setError(getCallResult(), this.getExecutorType().toString(), INTEGRATION_ERROR_MESSAGE);
        }
    }

    /**
     * Метод проверки отсутствующих параметров
     *
     * @param container            - результат вызова веб метода
     * @param missedParamContainer - спиское
     * @param paramName            - имя параметра
     * @return
     */
    private void checkMissedParamInContainerAndAddThisInMissedContainer(Map<String, Object> container,
                                                                        List<String> missedParamContainer, String paramName) {

        if (container == null) {
            return;
        }

        if (missedParamContainer == null) {
            missedParamContainer = new ArrayList<>();
        }

        Map<String, Object> result = null;

        // Для поддержки старых вызовов провери содержание Result в Result ?!
        if (container.containsKey(RESULT)) {
            // Получаем внешний
            result = (Map<String, Object>) container.get(RESULT);
            // Пытаемся получить внутренний
            if (result.containsKey(paramName)) {
                // Если получили, то смотрим там
                final Object abstractResult = result.get(paramName);

                if (abstractResult instanceof Map) {
                    if (((Map) abstractResult).isEmpty()) {
                        missedParamContainer.add(paramName);
                        return;
                    }
                }

                if (abstractResult instanceof List) {
                    if (((List) abstractResult).isEmpty()) {
                        missedParamContainer.add(paramName);
                        return;
                    }
                }

            }
        }

        // Если резалт пустой или такого параметра нет или пустой список или мапа
        if ((result == null) || (result.size() < 1) || (!result.containsKey(paramName))) {
            missedParamContainer.add(paramName);
        }
    }

    /**
     * @param callResult
     * @param moduleTypeName
     * @param overrideErrorMessage
     * @param overrideErrorMessage - если хотите перекрыть то сообщение, которое хранится,
     *                             то передаем его, иначе оно просто игнорируется
     */
    private void setError(Map<String, Object> callResult, String moduleTypeName, String overrideErrorMessage) {
        callResult.put(moduleTypeName, createErrorNote(overrideErrorMessage));
    }


    private void setError(String methodName, Map<String, Object> callResult, String moduleTypeName) throws ChainProxyTargetException {
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

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, Object> getCallResult() {
        return callResult;
    }

    public void setCallResult(Map<String, Object> callResult) {
        this.callResult = callResult;
    }
}

