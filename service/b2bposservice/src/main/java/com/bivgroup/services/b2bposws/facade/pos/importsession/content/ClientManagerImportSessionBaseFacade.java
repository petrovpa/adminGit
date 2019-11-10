package com.bivgroup.services.b2bposws.facade.pos.importsession.content;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Общий функционал для фасадов типа "Содержимое"
 *
 * @author Ivanov Roman
 **/
@BOName("ClientManagerImportSessionBase")
public class ClientManagerImportSessionBaseFacade extends B2BDictionaryBaseFacade {

    private static String TYPE_ID_PARAM_NAME = "typeId";
    private static Long TYPE_ID_PARAM_VALUE = 9500L;

    /**
     * Получить список статусов для фильтра "Статус"
     *
     * @param params
     * @return - список статусов
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BClientManagerGetImportSessionContentStates(Map<String, Object> params) throws Exception {

        Map<String, Object> example = new HashMap<>();
        example.put(TYPE_ID_PARAM_NAME, TYPE_ID_PARAM_VALUE);
        List<Map<String, Object>> list = dctFindByExample(SM_STATE_ENTITY_NAME, example);
        return B2BResult.ok(list);

    }

}
