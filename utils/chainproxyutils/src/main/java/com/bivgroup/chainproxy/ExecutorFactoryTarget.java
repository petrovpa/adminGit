package com.bivgroup.chainproxy;

import com.bivgroup.chainproxy.enums.TargetType;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetException;
import com.bivgroup.chainproxy.executertype.BaseExecutor;
import com.bivgroup.chainproxy.executertype.TargetExecutor;
import com.bivgroup.chainproxy.executertype.TargetParam;
import com.bivgroup.chainproxy.targets.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ivanov Roman
 * <p>
 * Класс-фабрика по созданию исполняемых модулей (исполнителей)
 **/
public abstract class ExecutorFactoryTarget {

    // Список обработчиков
    private static List<TargetExecutor> executorList = new ArrayList<>();

    // Метод по созданию "Исполнителей" согласно переданному списку типов исполнителей
    public static List<TargetExecutor> createExecutors(List<TargetType> targetTypeList, TargetParam targetParam) throws ChainProxyTargetException {

        // Первым делом создадим обязательный исполнитель по умолчанию
        createDefaultExecuter();

        // Если передали что-то в типах, то возвращаем инстансы согласно типам, которые передали,
        // иначе пустой список
        if (!targetTypeList.isEmpty()) {
            for (TargetType executorType : targetTypeList) {

                TargetExecutor targetExecutor = null;
                Class<? extends BaseExecutor> clazz = null;

                // Создаем нужный нам инстанс
                switch (executorType) {
                    case EXECUTOR_OIS_ERROR_CALLRESULT_TYPE: {
                        targetExecutor = new OisErrorCheckerTarget();
                        break;
                    }
                    case EXECUTOR_OIS_RESULTPARAMM_CANT_BE_EMPTY_TYPE: {
                        targetExecutor = new CantBeEmptyOisParamTarget();
                        break;
                    }
                }

                if (targetExecutor != null) {
                    clazz = targetExecutor.getClass();
                }

                // Обновляем параметры каждый раз, потому что они могут быть сформированы в разных сервисах
                // по-разному, поэтому нужно это учитывать
                if (isExistExecutor(clazz)) {
                    addParamWithExecutorTarget(targetExecutor, targetParam);
                }

                if ((clazz != null) && (!isExistExecutor(clazz))) {
                    targetExecutor.setExecutorType(executorType);
                    // Добавим необходимые параметры, если необходимо (зависит от типа)
                    addParamWithExecutorTarget(targetExecutor, targetParam);
                    executorList.add(targetExecutor);
                }
            }
        }

        return executorList;
    }

    /**
     * Функция по добавлению параметров, согласно типу
     *
     * @param targetExecutor
     * @param targetParam
     */
    private static void addParamWithExecutorTarget(TargetExecutor targetExecutor, TargetParam targetParam) throws ChainProxyTargetException {
        if ((targetExecutor != null) && (targetParam != null)) {

            final Map<TargetType, List<String>> paramMap = targetParam.getParam();

            for (Map.Entry param : paramMap.entrySet()) {
                final TargetType key = (TargetType) param.getKey();
                final TargetType executorType = targetExecutor.getExecutorType();

                // Если нашли параметр нужного нам типа, то добавляем его
                if (key.equals(executorType)) {
                    targetExecutor.addTargetParamByType(executorType, targetParam);
                }
            }
        }
    }

    /**
     * Функция по созданию Исполнителя по умолчанию с типом EXECUTOR_DEFAULT_TYPE
     */
    private static void createDefaultExecuter() {
        // Если список исполнителей еще не создан, тогда сначала создадим
        if (executorList == null) {
            executorList = new ArrayList<>();
        }

        // Добваляем только если такого еще нет
        if (!isExistExecutor(DefaultTarget.class)) {
            executorList.add(new DefaultTarget());
        }
    }

    /**
     * Проверка на существование такого испонителя в списке
     *
     * @param clazz
     * @return
     */
    private static boolean isExistExecutor(Class<? extends BaseExecutor> clazz) {

        boolean isExistExecutor = false;

        if (clazz != null) {
            for (BaseExecutor target : executorList) {
                // Как только нашли исполнителя такого типа выходим
                if (target.getClass().equals(clazz)) {
                    isExistExecutor = true;
                    break;
                }
            }
        }

        return isExistExecutor;
    }


}
