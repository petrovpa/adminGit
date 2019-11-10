package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BImportSessionTaskStarterCustom")
public class B2BImportSessionTaskStarterCustomFacade extends B2BImportSessionBaseFacade {

    /** Количество потоков обработки (не должно превышать одного потока) */
    private static volatile int threadCount = 0;

    protected static final String[] TASKS_METHOD_NAMES = {
            // огрструктура - файл
            "dsB2BImportSessionDepartmentTaskProcessFile",
            // огрструктура - содержимое
            "dsB2BImportSessionDepartmentTaskProcessContent",
            // КМ-ВСП - файл
            "dsB2BImportSessionManagerDepartmentTaskProcessFile",
            // КМ-ВСП - содержимое
            "dsB2BImportSessionManagerDepartmentTaskProcessContent",
            // КМ-Договор - файл
            "dsB2BImportSessionManagerContractTaskProcessFile",
            // КМ-Договор - содержимое
            "dsB2BImportSessionManagerContractTaskProcessContent"
    };

    private Map<String, Object> doImportSessionStartAllTasks(Map<String, Object> params) throws Exception {
        logger.debug("doImportSessionStartAllTasks...");
        loggerDebugPretty(logger, "doImportSessionStartAllTasks params", params);

        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        Map<String, Object> result = new HashMap<String, Object>();

        for (String taskMethodName : TASKS_METHOD_NAMES) {
            Map<String, Object> taskParams = new HashMap<String, Object>();
            taskParams.putAll(params);
            Map<String, Object> taskResult = this.callService(THIS_SERVICE_NAME, taskMethodName, taskParams, login, password);
            result.put(taskMethodName, taskResult);
        }

        loggerDebugPretty(logger, "doImportSessionStartAllTasks result", result);
        logger.debug("doImportSessionStartAllTasks finished.");
        return result;
    }

    /** Запуск всех регламентных заданий (проверка на выполнение находится также и в методах каждого конкретного задания) */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BImportSessionStartAllTasks(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionStartAllTasks start...");
        Map<String, Object> result = new HashMap<String, Object>();
        // Количество потоков группового запуска обработки не должно превышать одного потока
        // (проверка на выполнение находится также и в методах каждого конкретного задания)
        if (threadCount == 0) {
            threadCount = 1;
            // taskDetails.clear();
            try {
                result = doImportSessionStartAllTasks(params);
            } finally {
                threadCount = 0;
                // taskDetails.markFinish();
            }
        } else {
            logger.debug("dsB2BImportSessionStartAllTasks already run!");
        }
        logger.debug("dsB2BImportSessionStartAllTasks finished.");
        return result;
    }

}
