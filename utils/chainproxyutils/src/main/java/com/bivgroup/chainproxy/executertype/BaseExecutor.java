package com.bivgroup.chainproxy.executertype;

import java.util.*;

/**
 * @author Ivanov Roman
 * <p>
 * Базовый интерфейс, для поддержки констант, которые используются
 * (или могут использоваться) во всех "Executor" одновременно
 */
public interface BaseExecutor {

    // Общий параметр возвращения любого типа при отработке
    String EXECUTER_RESULT = "EXECUTERRESULT";

    String RESULT = "Result";
    @Deprecated
    String OLD_STATUS_PARAMNAME = "STATUS";

    // Статус ошибки для новых запросов
    String ERROR_PARAMNAME = "Error";
    // Статус ошибки для старых запросов типа VZR
    @Deprecated
    String OLD_ERROR_PARAMNAME = "outERROR";

    int FIRST_INDEX = 0;
    String NEW_LINE = "\n";

    // При необходимости добавить поддержку новых( нужных ) модулей
    String B2BPOSWS = "b2bposws";
    String PA2WS = "pa2ws";

    // Список модулей, для которых доступны Исполнители
    Set<String> moduleNameSet = new HashSet<>(Arrays.asList(
            B2BPOSWS,
            PA2WS
    ));

    boolean isAvailableModule(String moduleName);
}
