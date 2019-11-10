/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.sberlife;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author sambucus
 */
@BOName("LifeAgentCommissCalculatorMethodsCustom")
public class LifeAgentCommissCalculatorMethodsCustomFacade extends B2BLifeBaseFacade {
    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Метод, для вызова калькулятора, под текущие правила расчета агентских
     * вознаграждений должен по договору выполнить вызов калькулятора для
     * расчета комиссии и вызвать метод сохранения данных в структуру хранения
     * комиссий.
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRMAP", "CALCVERID"})
    public Map<String, Object> dsLifeAgentCalcPrems(Map<String, Object> params) throws Exception {
        logger.debug("before dsLifeAgentCalcPrems");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        //1. из contrMap получаем calcverid
        //2. получаем параметры для калькулятора TERMID, PAYVARID, CURRENCYID, страховая сумма, 
        //3. вызываем calcBy
        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        calcParams.put("CALCVERID", params.get("CALCVERID"));
        calcParams.put("TERMID", contrMap.get("TERMID"));
        calcParams.put("PAYVARID", contrMap.get("PAYVARID"));
        calcParams.put("CURRENCYID", contrMap.get("INSAMCURRENCYID"));
        calcParams.put("INSAMVALUE", contrMap.get("INSAMVALUE"));
        calcParams.put("PREMVALUE", contrMap.get("PREMVALUE"));

        Map<String, Object> calcRes = this.callService(Constants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcParams, login, password);

        saveCalcRes(calcRes, contrMap, params, login, password);

        logger.debug("after dsLifeAgentCalcPrems");
        return calcRes;
    }

    private void saveCalcRes(Map<String, Object> calcRes, Map<String, Object> contrMap, Map<String, Object> params, String login, String password) throws Exception {
        removeOldCalcData(contrMap, login, password);

        Map<String, Object> createParam = new HashMap<String, Object>();

        createParam.put("CONTRID", contrMap.get("CONTRID"));
        createParam.put("MAINACTCONTRID", params.get("MAINACTCONTRID"));
        createParam.put("PREMVALUE", calcRes.get("Result"));
        createParam.put("PROCESSDATE", new Date());
        createParam.put("TARIFF", calcRes.get("tariff"));
        parseDates(createParam, Double.class);
        Map<String, Object> saveRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAgentCommissCreate", createParam, login, password);
    }

    private void removeOldCalcData(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> browseParam = new HashMap<String, Object>();
        browseParam.put("CONTRID", contrMap.get("CONTRID"));
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAgentCommissBrowseListByParam", browseParam, login, password);
        if (res.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) res.get(RESULT);
            for (Map<String, Object> map : resList) {
                Map<String, Object> delParam = new HashMap<String, Object>();
                delParam.put("AGENTCOMMISSID", map.get("AGENTCOMMISSID"));
                Map<String, Object> delRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAgentCommissDelete", delParam, login, password);

            }
        }

    }
}
