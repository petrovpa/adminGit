package com.bivgroup.services.b2bposws.facade.pos.other;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.facade.RowStatus;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import static com.bivgroup.services.b2bposws.system.Constants.B2BPOSWS;
import ru.diasoft.services.inscore.system.annotations.WsMethod;


/**
 * @author adanilov
 */
public class MessageCorrespondentDictionaryFacade extends B2BDictionaryBaseFacade {

    /**
     * Метод для создания нового корреспондента сообщения
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsDCTCreateNewMessageCorrespondent(Map<String, Object> params) throws Exception {
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> newMessage = new HashMap<>();
        newMessage.putAll(params);
        Map<String, Object> result = new HashMap<>();
        result = dctCrudByHierarchy(MESSAGE_CORRESPONDENT_ENTITY_NAME, newMessage, isCallFromGate);
        return result;
    }

}
