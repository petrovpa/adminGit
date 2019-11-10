package com.bivgroup.services.b2bposws.facade.pos.custom.sberlife;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("BorrowerProtectCustom")
public class BorrowerProtectCustomFacade extends B2BLifeBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    public static Integer calcYears(Date from, Date to) {
        Integer result = 0;
        if ((from != null) && (to != null)) {
            GregorianCalendar fromG = new GregorianCalendar();
            GregorianCalendar toG = new GregorianCalendar();
            fromG.setTime(from);
            toG.setTime(to);
            result = WsUtils.calcYears(fromG, toG);
        }
        return result;
    }

    private long calcAge(Date birthDate) {
        return calcYears(birthDate, new Date()).longValue();
    }

    @WsMethod(requiredParams = {"CALCVERID", "insAmValue", "gender", "insBirthDATE"})
    public Map<String, Object> dsB2BBorrowerProtectCalc(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BBorrowerProtectCalc");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.putAll(params);
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Date insBirthDate = (Date) parseAnyDate(params.get("insBirthDATE"), Date.class, "insBirthDATE");
        long insAge = calcAge(insBirthDate);
        calcParams.put("age", insAge);
        logger.debug("calcParams: " + calcParams);
        Map<String, Object> calcRes = this.callService(Constants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcParams, login, password);
        logger.debug("calcRes: " + calcRes);

        logger.debug("after dsB2BBorrowerProtectCalc");
        return calcRes;
    }
    
    private void setResCalcRiskMapping(List<Map<String, Object>> contrRiskList, String riskProdStructSysName,
            Map<String, Object> calcResMap, String insAmValueParamName, String premValueParamName, boolean checkIsSelected) {
        for (Map<String, Object> bean : contrRiskList) {
            if ((bean.get("PRODRISKSYSNAME").toString().equalsIgnoreCase(riskProdStructSysName))
                    && ((!checkIsSelected) || (checkIsSelected && (bean.get("ISSELECTED") != null) && (Long.valueOf(bean.get("ISSELECTED").toString()).longValue() == 1)))) {
                if (insAmValueParamName != null) {
                    bean.put("INSAMVALUE", calcResMap.get(insAmValueParamName));
                }
                if (premValueParamName != null) {
                    bean.put("PREMVALUE", calcResMap.get(premValueParamName));
                }
                break;
            }
        }
    }

    @WsMethod(requiredParams = {"CONTRMAP", "FULLPRODMAP"})
    public Map<String, Object> dsB2BBorrowerProtectCalcByContrMap(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BBorrowerProtectCalcByContrMap");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contractMap = (Map<String, Object>) params.get("CONTRMAP");
        // предварительная проверка на корректность введенных данных
        // (вычисления выполнять не нужно, если переданы заведомо не походящие для создания договора данные)
        boolean isPreCalcCheck = true; // проверка перед вызовом калькулятора (часть атрибутов, например, вычисляемые суммы, не будут проверены)
        boolean isDataValid = this.validateContractSaveParams(contractMap, false, isPreCalcCheck, login, password);
        if (!isDataValid) {
            // данные не корректны - досрочный возврат мапы договора
            logger.debug("Contract is not valid - calculation skipped!");
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("CONTRMAP", contractMap);
            logger.debug("after dsB2BBorrowerProtectCalcByContrMap");
            return result;
        }
        // данные договора корректны - можно выполнять вычисления
        Map<String, Object> contrExtMap = (Map<String, Object>) contractMap.get("CONTREXTMAP");
        Map<String, Object> productMap = (Map<String, Object>) params.get("FULLPRODMAP");
        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        calcParams.put("CALCVERID", productMap.get("CALCVERID"));
        calcParams.put("gender", contrExtMap.get("insuredGender"));
        calcParams.put("insBirthDATE", contrExtMap.get("insuredBirthDATE"));
        calcParams.put("insAmValue", Double.valueOf(contractMap.get("INSAMVALUE").toString()));
        Map<String, Object> resMap = this.callService(Constants.B2BPOSWS, "dsB2BBorrowerProtectCalc", calcParams, login, password);
        // обработка результата калькулятора
        if (resMap.get("premium") != null) {
            contractMap.put("PREMVALUE", resMap.get("premium"));
            contrExtMap.put("redemptionSum", resMap.get("redemptionSum"));
            contrExtMap.put("tariff", resMap.get("tariff"));
            // записываем страховую сумму в риски
            List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contractMap.get("INSOBJGROUPLIST");
            List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupList.get(0).get("OBJLIST");
            Map<String, Object> contrObjMap = (Map<String, Object>) objList.get(0).get("CONTROBJMAP");
            List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
            resMap.put("insAmValue", Double.valueOf(contractMap.get("INSAMVALUE").toString()));
            setResCalcRiskMapping(contrRiskList, "death", resMap, "insAmValue",
                    null, false);
            setResCalcRiskMapping(contrRiskList, "disability12Group", resMap, "insAmValue",
                    null, false);
        }
        //
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRMAP", contractMap);
        logger.debug("after dsB2BBorrowerProtectCalcByContrMap");
        return result;
    }

    // проверка наличия премии по договору
    // метод переопределен в данном фасаде, поскольку перед расчетом не требуется подобная проверка
    @Override
    protected boolean checkContractPremValue(Map<String, Object> contract, boolean isPreCalcCheck, StringBuffer errorText) {
        boolean isPremValueExists;
        if (isPreCalcCheck) {
            isPremValueExists = checkBeanValueExist(contract, "PREMVALUE");
        } else {
            isPremValueExists = checkContractValueExist(contract, "PREMVALUE", "Страховой взнос", errorText);
        }
        return isPremValueExists;
    }

    // проверка наличия обязательных расширенных атрибутов договора
    // метод переопределен в текущем фасаде, поскольку для данного продукта требуется подобная проверка
    @Override
    protected void validateContractExtValues(Map<String, Object> contract, Map<String, Object> contractExtValues, boolean isFixContr, StringBuffer errorText) {
        checkContractExtValueExist(contractExtValues, "insuredGender", "Пол застрахованного", errorText);
        checkContractExtValueExist(contractExtValues, "insuredBirthDATE", "Дата рождения застрахованного", errorText);
        checkContractExtValueExist(contractExtValues, "insuredDeclCompliance", "Клиент соответствует декларации", errorText);
        checkContractExtValueExist(contractExtValues, "insurerIsInsured", "Страхователь является застрахованным", errorText);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBorrowerProtectContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BBorrowerProtectContractPrepareToSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        boolean isDataValid = this.validateContractSaveParams(contract, false, login, password);
        Map<String, Object> result;
        if (isDataValid) {
            result = genAdditionalSaveParams(contract, login, password);
        } else {
            result = contract;
        }
        logger.debug("after dsB2BBorrowerProtectContractPrepareToSave");
        return result;
    }

    // согласно письму от клиента от 30.09.2016:
    // "при невозможности подписать анкету происходит направление на андеррайтинг,
    // на текущий момент процесс андеррайтинга не финализирован,
    // вернемся с конечной информацией по данному вопросу в течении следующей недели"
    //
    // TODO: после получения от клиента окончательных критериев по андеррайтингу - не реализовывать это всё в этом фасаде, а по возможности использовать B2BLifeBaseFacade.underwritingCheck (универсальная, параметризованная реализация)
    @Override
    protected Long underwritingCheck(Map<String, Object> contract, String login, String password) throws Exception {

        logger.debug("Check contract underwriting...");

        // требуется ли андеррайтинг
        // 0L - нет, андеррайтинг не требуется
        // 1L - да, андеррайтинг требуется
        // 2L - недостаточно сведений, чтоб определить однозначно (например, не известна валюта и, как следствие, не проверить лимиты и пр.)
        Long UW = UW_DO_NOT_NEEDED;

        // проверка 'Наличие положительных ответов на вопросы Декларации застрахованного лица'
        Long insuredDeclCompliance = null;
        Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (contrExtMap != null) {
            insuredDeclCompliance = getLongParamLogged(contrExtMap, "insuredDeclCompliance");
        }
        if (insuredDeclCompliance == null) {
            // не найдена галка 'Клиент соответствует декларации застрахованного'
            logger.error(String.format("No client declaration check was found in this contract - underwriting check is failed!"));
            UW = UW_UNKNOWN;
        } else if (insuredDeclCompliance.intValue() == 0) {
            // галка 'Клиент соответствует декларации застрахованного' снята
            UW = UW_NEEDED;
        }

        logger.debug("UW = " + UW);
        contract.put("UW", UW);

        logger.debug("Check contract underwriting finished.");

        return UW;

    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBorrowerProtectContractUnderwritingCheck(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BBorrowerProtectContractUnderwritingCheck");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        this.underwritingCheck(contract, login, password);
        Map<String, Object> result = contract;
        logger.debug("after dsB2BBorrowerProtectContractUnderwritingCheck");
        return result;
    }

}
