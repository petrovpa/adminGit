package com.bivgroup.chainproxy;

import com.bivgroup.chainproxy.clients.BaseExecutorClient;
import com.bivgroup.chainproxy.enums.TargetType;
import com.bivgroup.chainproxy.exceptions.ChainProxyRunnerException;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;
import com.bivgroup.chainproxy.executertype.ClientExecutor;
import com.bivgroup.chainproxy.executertype.RunnerExecutor;
import com.bivgroup.chainproxy.executertype.TargetParam;

import java.util.List;
import java.util.Map;


/**
 * @author Ivanov Roman
 * <p>
 * Класс-обработчик (запускалка)
 *
 **/
public class ChainProxyRunner implements RunnerExecutor {

    static ChainProxyRunner chainProxyRunner = null;

    private ChainProxyRunner() {
    }

    public static ChainProxyRunner createInstance() {
        if (chainProxyRunner == null) {
            chainProxyRunner = new ChainProxyRunner();
        }

        return chainProxyRunner;
    }

    // Функция запуска нужных обработчиков

    public void execute(String moduleName, String methodName, Map<String, Object> callResult,
                        List<TargetType> targetList, TargetParam targetParam) throws ChainProxyRunnerException, ChainProxyTargetException {

        // Самое главное, чтобы было известно имя метода, для которого мы это будем выполнять и поддерживался только нашими
        // доступен модулями
        if (((methodName != null) && (!methodName.isEmpty())) && isAvailableModule(moduleName)) {
            // Создаем клиента для запуска
            ClientExecutor clientExecutor = new BaseExecutorClient();
            // Запускаем
            clientExecutor.execute(moduleName, methodName, callResult, targetList,targetParam);
        }
    }

    @Override
    public boolean isAvailableModule(String moduleName) {
        return moduleNameSet.contains(moduleName);
    }
}
