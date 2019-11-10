package com.bivgroup.interceptorfilter.targets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivanov Roman
 * <p>
 * Класс-сущность, представление цели выполнения
 **/
public class UpgradeParamTarget {

    private static final String RESULT = "Result";
    private static final String OLD_STATUS_PARAMNAME = "STATUS";
    private static final String ERROR_PARAMNAME = "Error";
    private static final String OLD_ERROR_PARAMNAME = "outERROR";
    private static final String EXECUTER_RESULT = "EXECUTERRESULT";


    private static final String EXECUTER_RESULT_STATUS = "ExecuterResultStatus";
    // Ключ-значение
    private static final String EXECUTER_RESULT_MESSAGE = "ExecuterResultMessage";
    // Сообщение описывающее сообщение при ошибке при вызове интеграции
    private static final String INTEGRATION_ERROR_MESSAGE = "Приносим извинения за временную недоступность сервиса. Ведутся технические работы. Сервис станет доступен в скором времени.";
    // Список имен сервисов, которые непосредственно дергают SOAP вызов интеграции
    private static final Set<String> integrationServiceNameSet = new HashSet<>();

    static {
        integrationServiceNameSet.add("dsLifeIntegrationGetContractList");
        integrationServiceNameSet.add("dsLifeIntegrationGetContractsData");
        integrationServiceNameSet.add("dsLifeIntegrationGetContractCutList");
        integrationServiceNameSet.add("dsLifeIntegrationGetCutChange");
        integrationServiceNameSet.add("dsLifeIntegrationPutChange");
        integrationServiceNameSet.add("dsLifeUpdateUserByExternalId");
        integrationServiceNameSet.add("dsLifeIntegrationGetClaimCutList");
        integrationServiceNameSet.add("dsLifeIntegrationGetClaimList");
        integrationServiceNameSet.add("dsLifeIntegrationPutClaim");
        integrationServiceNameSet.add("dsLifeIntegrationGetChange");
        integrationServiceNameSet.add("dsLifeIntegrationPutChangeFiles");
        integrationServiceNameSet.add("dsLifeIntegrationPutClaimFiles");
        integrationServiceNameSet.add("dsLifeIntegrationAssignChangeFiles");
        integrationServiceNameSet.add("dsLifeIntegrationAssignClaimFiles");
        integrationServiceNameSet.add("dsLifeRegisterUser");
        integrationServiceNameSet.add("dsLifeShortExternalIdRequest");
        integrationServiceNameSet.add("dsLifeIntegrationGetDeleted");
    }

    private String currentErrorMessage;
    private String moduleName;
    private String methodName;
    private Map<String, Object> callResult = new HashMap<>();
    private String login;
    private String password;
    private boolean isWasError = false;
    private boolean isOldCallRes = false;

    public UpgradeParamTarget(String methodName, Map<String, Object> callResult) {
        this.methodName = methodName;
        this.callResult = callResult;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getCallResult() {
        return callResult;
    }

    public void setCallResult(Map<String, Object> callResult) {
        this.callResult = callResult;
    }

    public String getCurrentErrorMessage() {
        return currentErrorMessage;
    }

    public void setCurrentErrorMessage(String currentErrorMessage) {
        this.currentErrorMessage = currentErrorMessage;
    }

    public boolean isWasError() {
        return isWasError;
    }

    public void setWasError(boolean wasError) {
        isWasError = wasError;
    }

    private void updateExecutedValue(Map<String, Object> callResult) {
        this.callResult.putAll(callResult);
    }

    // Устанавливаем ошибку в результат
    private void setErrorMessageForCallResult(Map<String, Object> callResult) {

        if (callResult == null) {
            callResult = new HashMap<>();
        }

        Map<String, Object> resultExternal = new HashMap<>();
        resultExternal.put(EXECUTER_RESULT_STATUS, ERROR_PARAMNAME);
        resultExternal.put(EXECUTER_RESULT_MESSAGE, this.currentErrorMessage);

        // Поддержка для старых запросов, которые возвращают в Result еще один Result ?!
        if ((isOldCallRes) && (callResult.containsKey(RESULT))) {
            if (callResult.get(RESULT) instanceof Map) {
                Map<String, Object> result = (Map<String, Object>) callResult.get(RESULT);
                result.put(EXECUTER_RESULT, resultExternal);
            }
        } else {
            callResult.put(EXECUTER_RESULT, resultExternal);
        }
    }

    private void updateCurrentMessage(String methodName) {
        if ((methodName != null) && (integrationServiceNameSet.contains(methodName))) {
            this.currentErrorMessage = INTEGRATION_ERROR_MESSAGE;
        }
    }

    // Проверяем, есть произошла ли у нас ошибка
    private void checkCallResultByErrorStatus(String methodName, Map<String, Object> callResult) {
        if ((callResult == null) && (callResult.get(ERROR_PARAMNAME) != null)
                && (ERROR_PARAMNAME.equalsIgnoreCase(callResult.get(ERROR_PARAMNAME).toString())) && (integrationServiceNameSet.contains(methodName))) {
            this.isWasError = true;
        }

        // Проверка запросов, которые возвращают в Result еще один Result ?!
        if ((callResult != null) && (callResult.containsKey(RESULT)) && (integrationServiceNameSet.contains(methodName))) {
            if (callResult.get(RESULT) instanceof Map) {
                Map<String, Object> result = (Map<String, Object>) callResult.get(RESULT);
                if ((result != null) && (result.get(OLD_STATUS_PARAMNAME) != null) && (OLD_ERROR_PARAMNAME.equalsIgnoreCase(result.get(OLD_STATUS_PARAMNAME).toString()))) {
                    isWasError = true;
                    isOldCallRes = true;
                }
            }
        }

    }

    private void handlerCallResult(String methodName, Map<String, Object> callResult) {
        checkCallResultByErrorStatus(methodName, callResult);
        // Если ошибку уже поймали, то прокидываем ее дальше
        if (isWasError) {
            updateCurrentMessage(methodName);
            setErrorMessageForCallResult(callResult);
        }
    }

    /**
     * Функция обработчик, формирует сообщение ошибки
     *
     * @param callResult
     */
    public void execute(String methodName, Map<String, Object> callResult) {
        this.handlerCallResult(methodName, callResult);
        this.updateExecutedValue(callResult);
    }


    @Override
    public String toString() {
        return "UpgradeParamTarget{" +
                "currentErrorMessage='" + currentErrorMessage + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", callResult=" + callResult +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", isWasError=" + isWasError +
                '}';
    }
}
