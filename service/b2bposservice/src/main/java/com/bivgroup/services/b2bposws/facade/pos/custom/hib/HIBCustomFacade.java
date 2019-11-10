/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.hib;

import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author kkulkov
 */
@BOName("HIBCustom")
public class HIBCustomFacade extends ProductContractCustomFacade {

    private final Logger cLogger = Logger.getLogger(HIBCustomFacade.class);

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BHIBContractPrepareToSave(Map<String, Object> params) throws Exception {

        cLogger.debug("before dsB2BHIBContractPrepareToSave");

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        cLogger.debug("after dsB2BHIBContractPrepareToSave");

        return contract;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BHIBContractPrepareToSaveFixContr(Map<String, Object> params) throws Exception {

        cLogger.debug("before dsB2BHIBContractPrepareToSaveFixContr");

        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }

        if ((null != params.get("is1CExported")) && ((Boolean) params.get("is1CExported"))) {
            if ((null != params.get("b2bCorrector1C")) && ((Boolean) params.get("b2bCorrector1C"))) {
                return contract;
            } else if ((null != params.get("isCorrector")) && ((Boolean) params.get("isCorrector"))) {
                contract.remove("DURATION");
                contract.remove("FINISHDATETIME");
                contract.remove("FINISHDATE");
                contract.remove("STARTDATETIME");
                contract.remove("STARTDATE");
                contract.remove("CRMDOCLIST");
                contract.remove("INSURERID");
                contract.remove("INSURERMAP");
                cLogger.debug("after dsB2BHIBContractPrepareToSaveFixContr");
                return contract;
            } else {
                // Если договор выгружен в 1С и у пользователя нет прав корректора запрещем что либо сохранять.
                cLogger.debug("after dsB2BHIBContractPrepareToSaveFixContr");
                return new HashMap<String, Object>();
            }
        } else {
            cLogger.debug("after dsB2BHIBContractPrepareToSaveFixContr");
            return contract;
        }
    }

}
