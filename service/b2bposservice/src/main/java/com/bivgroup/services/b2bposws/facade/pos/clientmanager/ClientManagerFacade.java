package com.bivgroup.services.b2bposws.facade.pos.clientmanager;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Фасад для работы с клиентским менеджером (кастомное получение данных)
 *
 * @author Ivanov Roman
 **/
@BOName("ClientManager")
public class ClientManagerFacade extends B2BDictionaryBaseFacade {
    /**
     * Константы
     */
    // Имена параметров
    protected static final String CLIENT_MANAGER_ID_PARAM_NAME = "clientManagerId";
    protected static final String CLIENT_MANAGER_FIO_PARAM_NAME = "clientManagerFIO";
    protected static final String CLIENT_MANAGER_ROLE_PARAM_NAME = "ROLESYSNAME";
    protected static final String ACTIVE_STATUS_PARAM_NAME = "STATUS";
    protected static final String USERACCOUNTID_PARAM_NAME = "USERACCOUNTID";
    protected static final String CLIENTMANGERFIO_PARAM_NAME = "CLIENTMANAGERFIO";
    // Значения параметров
    protected static final String CLIENT_MANAGER_ROLE_PARAM_VALUE = "ClientManagerSBOne";
    protected static final String ACTIVE_STATUS_PARAM_VALUE = "ACTIVE";


    /**
     * Метод получения информации об клиентском менеджере по табельному номеру
     *
     * @param params - обязательный параметр "Табельный номер"
     * @return - возвращает уникальный номер клиентского менеджера и его ФИО
     * @throws Exception
     */
    @WsMethod(requiredParams = {"TABNUMBER"})
    public Map<String, Object> dsB2BGetClientManagerFIO(Map<String, Object> params) throws Exception {
        // Ищем только активных пользователей
        params.put(ACTIVE_STATUS_PARAM_NAME, ACTIVE_STATUS_PARAM_VALUE);
        params.put(CLIENT_MANAGER_ROLE_PARAM_NAME, CLIENT_MANAGER_ROLE_PARAM_VALUE);
        // Получим данные по параметрам
        Map<String, Object> resultQuery = this.selectQuery("dsClientManagerBrowseByParamEx", "dsClientManagerBrowseByParamExCount", params);

        Map<String, Object> result = new HashMap<>();

        if (!getStringParam(resultQuery.get(RESULT)).isEmpty()) {
            final List<Map<String, Object>> listFromResultMap = getListFromResultMap(resultQuery);
            Map<String, Object> remappedList = new HashMap<>();
            listFromResultMap.stream().forEach(item -> remappedList.putAll(item));

            if (!getStringParam(remappedList.get(USERACCOUNTID_PARAM_NAME)).isEmpty()
                    && !getStringParam(remappedList.get(CLIENTMANGERFIO_PARAM_NAME)).isEmpty()) {
                result.put(CLIENT_MANAGER_ID_PARAM_NAME, remappedList.get(USERACCOUNTID_PARAM_NAME));
                result.put(CLIENT_MANAGER_FIO_PARAM_NAME, remappedList.get(CLIENTMANGERFIO_PARAM_NAME));
            }

        }
        result.put(RET_STATUS, "OK");
        return result;
    }
}

