package com.bivgroup.chainproxy.executertype;


import com.bivgroup.chainproxy.enums.TargetType;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetParamException;

import java.util.List;
import java.util.Map;

/**
 * @author Ivanov Roman
 * <p>
 * Параметр исполняемого модуля
 */
public interface TargetParam extends BaseExecutor {

    /**
     * Добавление списка параметров по типу {@link TargetType}
     * @param type
     * @param list
     */
    void addByTargetType(TargetType type, List<String> list);

    /**
     * Добавить один параметр для необходимого типа
     * @param type
     * @param paramName
     */
    void addParamNameByTargetType(TargetType type, String paramName);

    /**
     * Получение (контейнера) параметров
     * @return
     */
    Map<TargetType, List<String>> getParam();

    /**
     * Проверка параметра согласно типу
     * @param type
     * @param paramName
     * @return
     */
    boolean isEmptyOrNullParamByTypeTargetAndParamName(TargetType type, String paramName) throws ChainProxyTargetParamException;
}
