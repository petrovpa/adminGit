package com.bivgroup.chainproxy.targets;

import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;

import java.util.*;

public class OisErrorCheckerTarget extends BaseTarget {

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

    /**
     * Функция обработчик, формирует сообщение ошибки
     *
     * @param callResult
     */
    @Override
    public void execute(String moduleName, String methodName, Map<String, Object> callResult) throws ChainProxyTargetException {

        if (isAvailableModule(moduleName) && (integrationServiceNameSet.contains(methodName))) {

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

                callResult.put(moduleTypeName, createErrorNote(INTEGRATION_ERROR_MESSAGE));
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
                callResult.put(moduleTypeName, createErrorNote(INTEGRATION_ERROR_MESSAGE));
            }
        }
    }
}
