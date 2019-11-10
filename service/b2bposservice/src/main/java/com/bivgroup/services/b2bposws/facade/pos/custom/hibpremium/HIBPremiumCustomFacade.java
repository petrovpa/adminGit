/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.hibpremium;

import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;

/**
 *
 * @author ilich
 */
@BOName("HIBPremiumCustom")
public class HIBPremiumCustomFacade extends ProductContractCustomFacade {

    private final Logger logger = Logger.getLogger(HIBPremiumCustomFacade.class);

    /**
     * Метод для сохранения договора по продукту.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BHIBPremiumContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BHIBPremiumContractPrepareToSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        boolean isDataValid = validateSaveParams(contract, login, password);
        Map<String, Object> result;
        if (isDataValid) {
            //genAdditionalSaveParams(contract, login, password);
            result = contract;
        } else {
            result = contract;
        }
        logger.debug("after dsB2BHIBPremiumContractPrepareToSave");
        return result;
    }
    
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BHIBPremiumContractPrepareToSaveFixContr(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BHIBPremiumContractPrepareToSaveFixContr");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        boolean isDataValid = true;//validateSaveParams(contract, login, password);
        Map<String, Object> result;
        if (isDataValid) {
            //genAdditionalSaveParams(contract, login, password);
            result = contract;
        } else {
            result = contract;
        }
        if ((null != params.get("is1CExported")) && ((Boolean) params.get("is1CExported"))) {
            if ((null != params.get("isCorrector")) && ((Boolean) params.get("isCorrector"))) {
                contract.remove("DURATION");
                contract.remove("FINISHDATETIME");
                contract.remove("FINISHDATE");
                contract.remove("STARTDATETIME");
                contract.remove("STARTDATE");
                contract.remove("CRMDOCLIST");
                contract.remove("INSURERID");
                contract.remove("INSURERMAP");
                result = contract;
            } else {
                // Если договор выгружен в 1С и у пользователя нет прав корректора запрещем что либо сохранять.
                result = new HashMap < String, Object > ();
            }
        } 
        logger.debug("after dsB2BHIBPremiumContractPrepareToSaveFixContr");
        return result;
    }

    private boolean validateSaveParams(Map<String, Object> contract, String login, String password) throws Exception {
        StringBuffer errorText = new StringBuffer();
        if ((contract.get("did") == null) || (contract.get("dpid") == null)) {
            errorText.append("Не указан премиальный промокод. ");
        } else {
            StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
            String decryptedDID = scu.decrypt(contract.get("did").toString());
            String decryptedDPID = scu.decrypt(contract.get("dpid").toString());
            if ((decryptedDID == null) || (decryptedDID.isEmpty()) || (decryptedDPID == null) || (decryptedDPID.isEmpty())) {
                errorText.append("Неправильный промокод. ");
            } else {
                Map<String, Object> discParams = new HashMap<String, Object>();
                discParams.put("PRODDISCID", decryptedDID);
                discParams.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> discRes = this.callService(Constants.B2BPOSWS, "dsB2BProductDiscountBrowseListByParamEx", discParams, login, password);
                Long promoCount = 0L;
                if ((discRes != null) && (discRes.get("PROMOCOUNT") != null)) {
                    promoCount = Long.valueOf(discRes.get("PROMOCOUNT").toString());
                }
                Map<String, Object> contrDiscParam = new HashMap<String, Object>();
                contrDiscParam.put("PRODDISCPROMOID", Long.valueOf(decryptedDPID));
                Map<String, Object> contrDiscRes = this.callService(Constants.B2BPOSWS, "dsB2BContractDiscountBrowseListByParam", contrDiscParam, login, password);
                if ((contrDiscRes.get(RESULT) != null) && ((List<Map<String, Object>>) contrDiscRes.get(RESULT)).size() >= promoCount) {
                    errorText.append("Неправильный промокод. ");
                }
            }
        }
        boolean isDataValid = errorText.length() == 0;
        if (!isDataValid) {
            errorText.append("Сведения договора не сохранены.");
            contract.put("Status", "Error");
            contract.put("Error", errorText.toString());
        }
        return isDataValid;
    }
}
