package com.bivgroup.chainproxy.clients;

import com.bivgroup.chainproxy.ExecutorFactoryTarget;
import com.bivgroup.chainproxy.enums.TargetType;
import com.bivgroup.chainproxy.exceptions.ChainProxyRunnerException;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;
import com.bivgroup.chainproxy.executertype.ClientExecutor;
import com.bivgroup.chainproxy.executertype.TargetExecutor;
import com.bivgroup.chainproxy.executertype.TargetParam;
import com.bivgroup.chainproxy.managers.ExecutorManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Ivanov Roman
 * <p>
 * Класс, обслуживающий внешний вызов
 **/
public class BaseExecutorClient implements ClientExecutor {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private List<ExecutorManager> executorManagerList;

    public BaseExecutorClient() {
        logger.info("Was instanced BaseExecutorClient by default");
    }

    public BaseExecutorClient(List<ExecutorManager> executorManagerList) {
        this.executorManagerList = executorManagerList;
        logger.info(String.format("Was instanced BaseExecutorClient by executorManager %s", this.executorManagerList));
    }

    @Override
    public void execute(String moduleName, String methodName, Map<String, Object> callResult, List<TargetType> targetList,
                        TargetParam targetParam) throws ChainProxyRunnerException, ChainProxyTargetException {

        if ((executorManagerList == null) || (executorManagerList.isEmpty())) {
            logger.info("Executor manager null or empty. Try setting managerList...");
            // Пытаемся настроить менеджеров, построив их цепочки с нужными нам параметрами
            final boolean isSuccessfulSetting = settingManagerListByParam(targetList, targetParam);

            // Если настроить не удалось, то кидаем исключение
            if (!isSuccessfulSetting) {
                throw new ChainProxyRunnerException("An exception occurred when setting up managers. Could not create chain of artists");
            }
            // иначе считаем, что все нормально
        }

        for (ExecutorManager manager : executorManagerList) {
            logger.info(String.format("Manager %s start execute with %s by callResult", manager, methodName, callResult));
            manager.execute(moduleName, methodName, callResult);
        }
    }

    /**
     * Настройка менеджеров
     *
     * @param targetTypeList - типы исполнителей
     * @param targetParam - параметры для исполнителей
     * @return
     */
    private boolean settingManagerListByParam(List<TargetType> targetTypeList, TargetParam targetParam) throws ChainProxyTargetException {
        logger.info(String.format("Start settingManagerListByParam with params %s and %s", targetTypeList, targetParam));
        // Передача типов обязательны
        if ((targetTypeList == null) || (targetTypeList.isEmpty())) {
            return false;
        }

        ExecutorManager executorManager = new ExecutorManager();

        if (executorManagerList == null) {
            executorManagerList = new ArrayList<>();
        }

        final List<TargetExecutor> executors = ExecutorFactoryTarget.createExecutors(targetTypeList, targetParam);

        for (TargetExecutor executor: executors){
            executorManager.addExecutor(executor);
        }

        executorManagerList.add(executorManager);

        logger.info(String.format("ExecutorList is %s ", executorManagerList));

        return true;
    }

    @Override
    public boolean isAvailableModule(String moduleName) {
        return moduleNameSet.contains(moduleName);
    }

    @Override
    public String toString() {
        return "BaseExecutorClient{" +
                "executorManagerList=" + executorManagerList +
                '}';
    }
}
