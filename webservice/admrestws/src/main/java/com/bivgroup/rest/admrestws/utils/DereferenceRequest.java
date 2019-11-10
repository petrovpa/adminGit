package com.bivgroup.rest.admrestws.utils;

import com.bivgroup.service.SoapServiceCaller;
import com.bivgroup.utils.serviceloader.DefaultServiceLoader;

import java.util.HashMap;
import java.util.Map;

import static com.bivgroup.rest.common.Constants.*;
import static com.bivgroup.utils.ParamGetter.getObjectParam;
import static com.bivgroup.utils.ParamGetter.getStringParam;

/**
 * Класс для разыменования объекта
 */
public class DereferenceRequest {
    private SoapServiceCaller soapServiceCaller;

    public DereferenceRequest() {
        this.soapServiceCaller = DefaultServiceLoader.loadServiceAny(SoapServiceCaller.class);
    }

    /**
     * Метод разыменовки объекта по идентификатору.
     * Метод вызывает сервис загрузки информации объекта по идентификатору и
     * вытаскивает из него требуемы поля
     *
     * @param moduleName    модуль, в котором находится сервис
     * @param serviceName   имя сервис, который надо вызвать для получения даннхы
     * @param objectId      идентификатор объекта
     * @param idParamName   имя идентификатора объекта для вызова
     * @param needParamName массив имен параметров, которые надо переложить из запроса
     * @return "словарь" требуемых для разыменовки данных по обекту
     */
    public Map<String, Object> dereferenceByObjectId(String moduleName, String serviceName, Long objectId, String idParamName, String... needParamName) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(idParamName, objectId);
        queryParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> queryResult = soapServiceCaller.callExternalService(moduleName, serviceName, queryParams);
        String error = getStringParam(queryResult, ADMIN_CALL_ERROR);
        if (!error.isEmpty()) {
            result.put(ERROR, "Ошибка разыменования.");
            return result;
        }
        for (String paramName : needParamName) {
            result.put(paramName, getObjectParam(queryResult, paramName));
        }
        return result;
    }
}
