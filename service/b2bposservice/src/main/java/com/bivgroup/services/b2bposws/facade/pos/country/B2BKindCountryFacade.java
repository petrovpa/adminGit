package com.bivgroup.services.b2bposws.facade.pos.country;


import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * Фасад для сущности B2BKindCountryFacade справочная структура
 *
 * @author Ivanov Roman
 */
public class B2BKindCountryFacade extends B2BDictionaryBaseFacade {

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BCountryBrowseListByParamEx(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        params.put("ISNOTUSE", 0L);
        params.put("ORDERBY", "COUNTRYID");
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BCountryBrowseListByParam", params, login, password);
        return result;
    }

    @WsMethod(requiredParams = "COUNTRYID")
    public Map<String, Object> dsB2BKindCountryLoadById(Map<String, Object> params) throws Exception {

        logger.debug("dsB2BKindCountryLoadById begin");

        boolean isCallFromGate = isCallFromGate(params);
        Long countryid = Long.valueOf(params.get("COUNTRYID").toString());
        Map<String, Object> KindCountryMap = dctFindById(KIND_COUNTRY_ENTITY_NAME, countryid, isCallFromGate);
        markAllMapsByKeyValue(KindCountryMap, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);

        logger.debug("dsB2BKindCountryLoadById end");

        return KindCountryMap;
    }

}
