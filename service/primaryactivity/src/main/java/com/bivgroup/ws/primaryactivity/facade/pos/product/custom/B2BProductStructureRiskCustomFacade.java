/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.primaryactivity.facade.pos.product.custom;

import com.bivgroup.ws.primaryactivity.facade.B2BPrimaryActivityBaseFacade;
import com.bivgroup.ws.primaryactivity.system.Constants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("B2BProductStructureRiskCustom")
public class B2BProductStructureRiskCustomFacade extends B2BPrimaryActivityBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    // Имя сервиса для вызова методов из b2bposservice
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    // флаг подробного протоколирования вызовов методов веб-сервисов
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_CALLS_LOGGING = logger.isDebugEnabled();
    
    @WsMethod(requiredParams = {"PRODVERID"})
    public Map<String, Object> dsB2BProductStructureRiskBrowseListByProdVerID(Map<String, Object> params) throws Exception {
        
        logger.debug("Getting risks list...");
        
        // логин и пароль для вызова методов веб-сервисов
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // версия продукта
        Long prodVerID = getLongParam(params, "PRODVERID");
        logger.debug("Product version id (PRODVERID) = " + prodVerID);
        
        // параметры запроса
        Map<String, Object> riskParams = new HashMap<String, Object>();
        riskParams.put("PRODVERID", prodVerID);
        riskParams.put("DISCRIMINATOR", 4L);
        
        // выполнение базовой версии запроса (dsB2BProductStructureRiskBrowseListByParam неприменим - отсутсвует ограничение по PRODVERID)
        List<Map<String, Object>> riskList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BProductStructureBaseBrowseListByParam", riskParams, IS_VERBOSE_CALLS_LOGGING, login, password);        
        //logger.debug("Risks list: " + riskList);
        
        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();        
        result.put("RISKLIST", riskList);
        result.put("PRODVERID", prodVerID);
        
        logger.debug("Getting risks list finished.");
        
        return result;
    }
    
}
