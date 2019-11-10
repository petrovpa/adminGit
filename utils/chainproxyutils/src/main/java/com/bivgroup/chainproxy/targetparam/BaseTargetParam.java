package com.bivgroup.chainproxy.targetparam;

import com.bivgroup.chainproxy.enums.TargetType;
import com.bivgroup.chainproxy.exceptions.ChainProxyTargetParamException;
import com.bivgroup.chainproxy.executertype.TargetParam;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ivanov Roman
 * <p>
 * Базовый класс параметров для исполняемых модулей
 */
public class BaseTargetParam implements TargetParam {

    Map<TargetType, List<String>> param;

    public BaseTargetParam() {
        param = new HashMap<>();
    }

    public BaseTargetParam(Map<TargetType, List<String>> param) {
        this.param = param;
    }

    public Map<TargetType, List<String>> getParam() {
        return param;
    }

    @Override
    public boolean isEmptyOrNullParamByTypeTargetAndParamName(TargetType type, String paramName) throws ChainProxyTargetParamException {
        boolean isEmptyOrNull = true;

        // Находим параметры нужного нам типа
        if (param.containsKey(type)) {
            final List<String> list = param.get(type);

            // Если в проверяемом списке все-таки находиться наш параметр
            if ((list != null) || ((!list.isEmpty()) && (list.contains(paramName)))) {
                // Проверим дополнительно непосредственно сам параметр
                final List<String> collect = list.stream()
                        .filter(item -> item.equalsIgnoreCase(paramName))
                        .collect(Collectors.toList());
                // В нашей мапе должен быть только один такой параметр или отсутствовать совсем
                if (collect.size() == 1) {
                    isEmptyOrNull = true;
                } else {
                    throw new ChainProxyTargetParamException(String.format("Param with name %s count > 1", paramName));
                }
            }
        }

        return isEmptyOrNull;
    }

    @Override
    public void addByTargetType(TargetType type, List<String> list) {
        // safe for NPE
        if (type != null) {
            if (!param.containsKey(type)) {
                // Если такой тип еще не добавляли, добавляем (при условии, что передали список и он не пустой)
                if ((list != null) && (!list.isEmpty())) {
                    param.put(type, list);
                }
            } else {
                // Если такой тип уже добавляли
                List<String> stringList = param.get(type);

                if (stringList == null) {
                    stringList = new ArrayList<>();
                }

                // Добавляем
                stringList.addAll(list);
                param.put(type, stringList);
            }
        }
    }

    @Override
    public void addParamNameByTargetType(TargetType type, String paramName) {
        if ((paramName != null) && (!paramName.isEmpty())) {
            List<String> list = new ArrayList<>(Arrays.asList(paramName));
            addByTargetType(type, list);
        }
    }

    @Override
    public boolean isAvailableModule(String moduleName) {
        return moduleNameSet.contains(moduleName);
    }
}
