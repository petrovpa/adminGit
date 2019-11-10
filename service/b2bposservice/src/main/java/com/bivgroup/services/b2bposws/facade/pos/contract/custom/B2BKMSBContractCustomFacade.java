package com.bivgroup.services.b2bposws.facade.pos.contract.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;

@BOName("B2BKMSBContractCustom")
public class B2BKMSBContractCustomFacade extends B2BBaseFacade {
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BKMSBContractBrowseListByParam(Map<String, Object> params) throws Exception {
        //дополняем параметры кодом канала продаж КМСБ
        logJournalSearchParams(params);
        String login = params.get(LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        //TODO фильтр по каналу продаж возможно не потребуется, когда будут настроены группы на пользователях,
        //params.put("SALECHANNELID", 50000L);
        //return this.callService(Constants.B2BPOSWS, "dsB2BContractBrowseListByParamCustomCoreWhereEx", params, login, password);
        return this.callService(Constants.B2BPOSWS, "dsB2BContractBrowseListByParamNewRightCustomCoreWhereEx", params, login, password);
    }
}