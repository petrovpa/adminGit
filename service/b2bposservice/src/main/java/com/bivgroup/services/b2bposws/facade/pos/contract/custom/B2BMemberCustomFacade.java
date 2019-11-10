package com.bivgroup.services.b2bposws.facade.pos.contract.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;

import java.util.HashMap;
import java.util.Map;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author arazumovsky
 */
@BOName("B2BMemberCustom")
public class B2BMemberCustomFacade extends B2BDictionaryBaseFacade {

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author arazumovsky
     * @param params
     * <UL>
     * <LI>CLIENTID - ID КЛИЕНТА</LI>
     * <LI>PROGEXTIDLIST - Список программ</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>LOGIN - login клиента</LI>
     * <LI>CLIENTID - ID КЛИЕНТА</LI>
     * <LI>CONTRACTID - ID КОНТРАКТА</LI>
     * </UL>
     */

    // todo: добавить поддержу передачи clientExternalId из гейта ЛК2 и учета этого тут (не загружать клиента и пр. если clientExternalId уже передан из гейта)
    private String getClientExternalIdByClientId(Long clientId) throws Exception {
        Map<String, Object> client = dctFindById(CLIENT_ENTITY_NAME, clientId);
        String clientExternalId = getStringParamLogged(client, "externalId");
        return clientExternalId;
    }

    @WsMethod(requiredParams = {"CLIENTID", "PROGEXTIDLIST"})
    public Map<String, Object> dsB2BGetNotAddedContracts(Map<String, Object> params) throws Exception {
        Long clientId = getLongParam(params.get("CLIENTID"));
        String externalId = getClientExternalIdByClientId(clientId);
        Long extId = getLongParam(externalId);
        params.put("EXTID", extId);
        return this.selectQuery("dsB2BGetNotAddedContracts", null, params);
    }

    @WsMethod(requiredParams = {"CLIENTID", "PROGEXTIDLIST"})
    public Map<String, Object> dsB2BMemberGetNotAddedContractListEx(Map<String, Object> params) throws Exception {
        Long clientId = getLongParam(params.get("CLIENTID"));
        String externalId = getClientExternalIdByClientId(clientId);
        Long extId = getLongParam(externalId);
        params.put("EXTID", extId);


        return this.selectQuery("dsB2BMemberGetNotAddedContractListEx", null, params);
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BMemberDeleteByContrId(Map<String, Object> params) throws Exception {
        int[] delRes = this.deleteQuery("dsB2BMemberDeleteByContrId",params);
        Map<String,Object> res = new HashMap<>();
        res.put("DELRESULT", delRes);
        return res;
    }


}
