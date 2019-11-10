package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.HashMap;
import java.util.Map;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.annotations.WsMethod;


public class B2BProductPremiumCurrencyCustomFacade extends B2BBaseFacade {

    protected static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductPremiumCurrencyBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BProductPremiumCurrencyBrowseListByParamEx", params);
        return result;
    }

    @WsMethod(requiredParams = {"PRODSYSNAME"})
    public Map<String, Object> dsB2BProductPremiumCurrencyBrowseListByProductSysName(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        String productSysName = getStringParamLogged(params, "PRODSYSNAME");
        Map<String, Object> currencyParams = new HashMap<String, Object>();
        currencyParams.put("PRODSYSNAME", productSysName);

        Map<String, Object> result = callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductPremiumCurrencyBrowseListByParamEx", currencyParams, login, password);
        
        return result;
    }
}
