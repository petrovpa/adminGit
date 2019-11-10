package com.bivgroup.services.b2bposws.facade.pos.custom.sberlife;

import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.currency.AmountUtils;
import ru.diasoft.utils.format.number.NumberFormatUtils;

/**
 * @author ilich
 */
@BOName("SberLifePrintCustom")
public class SberLifePrintCustomFacade extends ProductContractCustomFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    private final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    private static Map<String, List<Map<String, Object>>> hbCache;
    private static Map<String, Map<Long, Map<String, Object>>> hbMapsByHidCache;
    private static Map<String, Map<String, Object>> productCache;
    //private static Map<String, Map<Long, Map<String, Object>>> productMapsByIDCache;

    public SberLifePrintCustomFacade() {
        init();
    }

    private void init() {
        logger.debug("SberLifePrintCustomFacade init...");
        // протоколирование времени, затраченного на вызовы (влияет только на вызовы через callServiceTimeLogged)
        IS_CALLS_TIME_LOGGED = true;
        //
        hbCache = new HashMap<String, List<Map<String, Object>>>();
        hbMapsByHidCache = new HashMap<String, Map<Long, Map<String, Object>>>();
        productCache = new HashMap<String, Map<String, Object>>();
        //productMapsByIDCache = new HashMap<String, Map<Long, Map<String, Object>>>();
        logger.debug("SberLifePrintCustomFacade init finished!");
    }

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

    private Long getTermInYearsById(Map<String, Object> productMap, Long termId) {
        List<Map<String, Object>> prodTermList = (List<Map<String, Object>>) productMap.get("PRODTERMS");
        if (prodTermList != null) {
            for (Map<String, Object> bean : prodTermList) {
                if (bean.get("TERM") != null) {
                    Map<String, Object> termMap = (Map<String, Object>) bean.get("TERM");
                    if (getLongParam(termMap.get("TERMID")) != null) {
                        if (termId != null) {
                            if (getLongParam(termMap.get("TERMID")).longValue() == termId.longValue()) {
                                return getLongParam(termMap.get("YEARCOUNT"));
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private Map<String, Object> getTermMapById(Map<String, Object> productMap, Long termId) {
        List<Map<String, Object>> prodTermList = (List<Map<String, Object>>) productMap.get("PRODTERMS");
        if (prodTermList != null) {
            for (Map<String, Object> bean : prodTermList) {
                if (bean.get("TERM") != null) {
                    Map<String, Object> termMap = (Map<String, Object>) bean.get("TERM");
                    if (termId != null) {
                        if (getLongParam(termMap.get("TERMID")) != null) {
                            if (getLongParam(termMap.get("TERMID")).longValue() == termId.longValue()) {
                                return termMap;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getPaymentVariantNameById(Map<String, Object> productMap, Long paymentVariantId) {
        Map<String, Object> prodVerMap = (Map<String, Object>) productMap.get("PRODVER");
        if (prodVerMap != null) {
            List<Map<String, Object>> prodPayVarList = (List<Map<String, Object>>) prodVerMap.get("PRODPAYVARS");
            if (prodPayVarList != null) {
                for (Map<String, Object> bean : prodPayVarList) {
                    Map<String, Object> payVarMap = (Map<String, Object>) bean.get("PAYVAR");
                    if (paymentVariantId != null) {
                        if (getLongParam(payVarMap.get("PAYVARID")) != null) {
                            if (getLongParam(payVarMap.get("PAYVARID")).longValue() == paymentVariantId.longValue()) {
                                return getStringParam(payVarMap.get("NAME"));
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getProgramNameById(Map<String, Object> productMap, Long programId) {
        Map<String, Object> prodVerMap = (Map<String, Object>) productMap.get("PRODVER");
        List<Map<String, Object>> prodProgsList = (List<Map<String, Object>>) prodVerMap.get("PRODPROGS");
        if (prodProgsList != null) {
            for (Map<String, Object> bean : prodProgsList) {
                if (programId != null) {
                    if (getLongParam(bean.get("PRODPROGID")) != null) {
                        if (getLongParam(bean.get("PRODPROGID")).longValue() == programId.longValue()) {
                            return getStringParam(bean.get("NAME"));
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getCurrencyNameById(Map<String, Object> productMap, Long currencyID) {
        if ((productMap != null) && (currencyID != null)) {
            List<Map<String, Object>> prodInsAmCursList = (List<Map<String, Object>>) productMap.get("PRODINSAMCURS");
            if (prodInsAmCursList != null) {
                for (Map<String, Object> bean : prodInsAmCursList) {
                    Map<String, Object> currencyMap = (Map<String, Object>) bean.get("CURRENCY");
                    Long prodInfoCurrencyID = getLongParam(currencyMap, "CURRENCYID");
                    if (currencyID != null) {
                        if (prodInfoCurrencyID != null) {
                            if (prodInfoCurrencyID.equals(currencyID)) {
                                return getStringParam(currencyMap.get("CURRENCYNAME"));
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private Map<String, Object> getProdInsCoverMap(Long insCoverId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        params.put("INSCOVERID", insCoverId);
        return this.callServiceTimeLogged(Constants.B2BPOSWS, "dsB2BProductInsuranceCoverBrowseListByParam", params, login, password);
    }

    private String resolveHBByHid(String hbName, String valName, Long hid, String login, String password) throws Exception {
        Map<String, Object> hbRecord = getHandbookRecordByHid(hbName, hid, login, password);
        String resolveResult = getStringParam(hbRecord, valName);
        return resolveResult;
    }

    private String resolveHB(String hbName, String idName, String valName, Object idValue, String login, String password) throws Exception {
        String resolveResult;
        if ("hid".equals(idName)) {
            Long hid = getLongParam(idValue);
            if (hid != null) {
                resolveResult = resolveHBByHid(hbName, valName, hid, login, password);
            } else {
                resolveResult = "";
            }
        } else {
            List<Map<String, Object>> hbDataList = getHandbookDataList(hbName, login, password);
            resolveResult = resolveHB(hbDataList, idName, valName, idValue);
        }
        return resolveResult;
    }

    private String resolveHB(List<Map<String, Object>> list, String idName, String valName, Object idValue) {
        if (list != null) {
            for (Map<String, Object> bean : list) {
                if ((bean.get(idName) != null) && (getStringParam(bean.get(idName)).equals(idValue.toString()))) {
                    if (bean.get(valName) != null) {
                        return getStringParam(bean.get(valName));
                    }
                }
            }
        }
        return "";
    }

    private String getCitizenship(Map<String, Object> member, String login, String password) throws Exception {
        if (member.get("CITIZENSHIP") != null) {
            String citizenship = getStringParam(member.get("CITIZENSHIP"));
            if ("0".equals(citizenship)) {
                //Российская федерация
                return "Российская Федерация";
            }
            if ("1000".equals(citizenship)) {
                //Иностранный гражданин
                return "Иностранное государство";
            }
            return getCountryNameById(citizenship, login, password);
        }
        return "";
    }

    private String getCountryNameById(String citizenship, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("COUNTRYID", citizenship);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BCountryBrowseListByParam", param, login, password);
        if (res != null) {
            if (res.get("COUNTRYNAME") != null) {
                return res.get("COUNTRYNAME").toString();
            }
        }

        return "";
    }

    private void participantResolveParams(Map<String, Object> participantMap, /*Map<String, Object> participHBMap,*/ String login, String password) throws Exception {
        if (participantMap != null) {
            resolveGender(participantMap, "GENDER");

            Long citizenship = getLongParamLogged(participantMap, "CITIZENSHIP");
            String citizenshipStr = getCitizenship(participantMap, login, password);
            Long newCitizenShipIdForReport = 0L;
            if (citizenship != null) {
                if (citizenship.equals(0L) || citizenship.equals(1L)) {
                    newCitizenShipIdForReport = 0L;
                } else {
                    newCitizenShipIdForReport = 1000L;
                }
            }
            // нужно чтобы на всех отчетах правильно выводились документы.
            //т.к. там сделано по кодам 0 - рф 1000 - иностранный.
            participantMap.put("CITIZENSHIP", newCitizenShipIdForReport);
            participantMap.put("CITIZENSHIPSTR", citizenshipStr);
            List<Map<String, Object>> extAttributeList2 = (List<Map<String, Object>>) participantMap.get("extAttributeList2");
            if (extAttributeList2 != null) {
                for (Map<String, Object> bean : extAttributeList2) {
                    String extAttSysName = getStringParamLogged(bean, "EXTATT_SYSNAME");
                    String valueStr = null;
                    if (!extAttSysName.isEmpty()) {
                        if (extAttSysName.equalsIgnoreCase("education")) {
                            if (bean.get("EXTATTVAL_VALUE") != null) {
                                Long value = getLongParamLogged(bean, "EXTATTVAL_VALUE");
                                //valueStr = resolveHB((List<Map<String, Object>>) participHBMap.get("PROFLIST"), "hid", "name", value);
                                valueStr = resolveHBByHid("B2B.Life.Profession", "name", value, login, password);
                                bean.put("EXTATTVAL_VALUESTR", valueStr);
                            }
                        } else if (extAttSysName.equalsIgnoreCase("activityBusinessKind")) {
                            if (bean.get("EXTATTVAL_VALUE") != null) {
                                Long value = getLongParamLogged(bean, "EXTATTVAL_VALUE");
                                //valueStr = resolveHB((List<Map<String, Object>>) participHBMap.get("KOALIST"), "hid", "name", value);
                                valueStr = resolveHBByHid("B2B.Life.KindOfActivity", "name", value, login, password);
                            }
                        } else if (extAttSysName.equalsIgnoreCase("MaritalStatus")) {
                            // Семейное положение - MaritalStatus
                            // participant.js @ maritalStatusList: [
                            //{'NAME': 'Холост', 'SYSNAME': 'MARITAL01', GENDER: 0},
                            //{'NAME': 'Женат', 'SYSNAME': 'MARITAL02', GENDER: 0},
                            //{'NAME': 'Не замужем', 'SYSNAME': 'MARITAL03', GENDER: 1},
                            //{'NAME': 'Замужем', 'SYSNAME': 'MARITAL04', GENDER: 1}],
                            if (bean.get("EXTATTVAL_VALUE") != null) {
                                String sysNameValue = getStringParamLogged(bean, "EXTATTVAL_VALUE");
                                if (sysNameValue.equals("MARITAL01")) {
                                    valueStr = "Холост";
                                } else if (sysNameValue.equals("MARITAL02")) {
                                    valueStr = "Женат";
                                } else if (sysNameValue.equals("MARITAL03")) {
                                    valueStr = "Не замужем";
                                } else if (sysNameValue.equals("MARITAL04")) {
                                    valueStr = "Замужем";
                                }
                            }
                        } else if (extAttSysName.equalsIgnoreCase("unResident")) {
                            // Нерезидент - unResident
                            resolveFlag(bean, "EXTATTVAL_VALUE");
                        } else if (extAttSysName.equalsIgnoreCase("isTaxResidentUSA")) {
                            // Налоговый резидент США - isTaxResidentUSA
                            resolveFlag(bean, "EXTATTVAL_VALUE");
                        }

                        // todo: мб еще разыменовка параметров для экспорта?
                        if (valueStr != null) {
                            bean.put("EXTATTVAL_VALUESTR", valueStr);
                        }
                    }

                }
            }

            // Код подразделения: "123456" -> "123-456"
            List<Map<String, Object>> documentList = (List<Map<String, Object>>) participantMap.get("documentList");
            if (documentList != null) {
                for (Map<String, Object> document : documentList) {
                    getStringParamLogged(document, "DOCTYPESYSNAME"); // для протокола
                    String issuerCode = getStringParamLogged(document, "ISSUERCODE");
                    if (!issuerCode.isEmpty()) {
                        if (issuerCode.length() == 6) {
                            String issuerCodeWithHyphen = issuerCode.substring(0, 3) + "-" + issuerCode.substring(3, 6);
                            logger.debug("ISSUERCODE with hyphen = " + issuerCodeWithHyphen);
                            document.put("ISSUERCODE", issuerCodeWithHyphen);
                        }
                    }
                }
            }

        }
    }

    // формирование списка актуальных (не удаляемых) рисков из мапы договора
    protected ArrayList<Map<String, Object>> getAllRisksListFromContract(Map<String, Object> contract) {
        logger.debug("Getting actual (non deleted) risks list from contract map...");
        ArrayList<Map<String, Object>> allRisksList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> insobjGroupList = (List<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        if (insobjGroupList != null) {
            for (Map<String, Object> insObjGroup : insobjGroupList) {
                List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroup.get("OBJLIST");
                if (objList != null) {
                    for (Map<String, Object> obj : objList) {
                        Map<String, Object> contrObjMap = (Map<String, Object>) obj.get("CONTROBJMAP");
                        if (contrObjMap != null) {
                            List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                            if (contrRiskList != null) {
                                for (Map<String, Object> risk : contrRiskList) {
                                    String prodRiskSysName = getStringParamLogged(risk, "PRODRISKSYSNAME");
                                    Long riskRowStatus = getLongParamLogged(risk, "ROWSTATUS");
                                    // пропускаем проверку рисков, отмеченных как удаляемые
                                    boolean isRiskMarkedAsDeleted = ((riskRowStatus != null) && (riskRowStatus.intValue() == DELETED_ID));
                                    if (!isRiskMarkedAsDeleted) {
                                        // если риск не удаляемый - добавляем в результирующий список
                                        allRisksList.add(risk);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.debug("Getting actual (non deleted) risks list from contract map finished.");
        return allRisksList;
    }

    private void resolveFlag(Map<String, Object> map, String flagKeyName) {
        String flagValue = getStringParamLogged(map, flagKeyName);
        String flagStrValue = "";
        if (flagValue != null) {
            if (flagValue.equals("0")) {
                flagStrValue = "Нет";
            } else if (flagValue.equals("1")) {
                flagStrValue = "Да";
            } else if (flagValue.equalsIgnoreCase("true")) {
                flagStrValue = "Да";
            } else if (flagValue.equalsIgnoreCase("false")) {
                flagStrValue = "Нет";
            }
            map.put(flagKeyName + "STR", flagStrValue);
        }
    }
    private void resolveFlagBool(Map<String, Object> map, String flagKeyName) {
        String flagValue = getStringParamLogged(map, flagKeyName);
        String flagStrValue = "";
        if (flagValue != null) {
            if (flagValue.equals("0")) {
                flagStrValue = "FALSE";
            } else if (flagValue.equals("1")) {
                flagStrValue = "TRUE";
            } else if (flagValue.equalsIgnoreCase("true")) {
                flagStrValue = "TRUE";
            } else if (flagValue.equalsIgnoreCase("false")) {
                flagStrValue = "FALSE";
            }
            map.put(flagKeyName + "BOOL", flagStrValue);
        }
    }

    private void resolveGender(Map<String, Object> map, String genderKeyName) {
        Long genderLongValue = getLongParamLogged(map, genderKeyName);
        String flagStrValue = "";
        if (genderLongValue != null) {
            if (genderLongValue.equals(0L)) {
                flagStrValue = "Мужской";
            } else if (genderLongValue.equals(1L)) {
                flagStrValue = "Женский";
            }
        }
        map.put(genderKeyName + "STR", flagStrValue);
    }

    private String getHBLoadMethodNameByProdSysname(String prodSysName){
        String hbLoadMethodName;
        switch (prodSysName){
            case "SMART_POLICY":{
                hbLoadMethodName = "B2B.smartPolicy.AssuranceLevel";
                break;
            }
            case "SMART_POLICY_LIGHT":{
                hbLoadMethodName = "B2B.smartPolicyLight.AssuranceLevel";
                break;
            }
            case "LIGHTHOUSE":{
                hbLoadMethodName = "B2B.InvestNum1.AssuranceLevel";
                break;
            }
            default:{
                hbLoadMethodName = "B2B.InvestNum1.AssuranceLevel";
            }
        }
        return hbLoadMethodName;
    }

    private String getHBFundsLoadMethodNameByProdSysname(String prodSysName){
        String hbLoadMethodName;
        switch (prodSysName){
            case "SMART_POLICY":{
                hbLoadMethodName = "B2B.smartPolicy.Funds";
                break;
            }
            case "SMART_POLICY_LIGHT":{
                hbLoadMethodName = "B2B.smartPolicyLight.Funds";
                break;
            }
            case "LIGHTHOUSE":{
                hbLoadMethodName = "B2B.InvestNum1.Funds";
                break;
            }
            default:{
                hbLoadMethodName = "B2B.InvestNum1.Funds";
            }
        }
        return hbLoadMethodName;
    }

    private String getHBAutopilotsLoadMethodNameByProdSysname(String prodSysName){
        String hbLoadMethodName;
        switch (prodSysName){
            case "SMART_POLICY":{
                hbLoadMethodName = "B2B.SmartPolicy.AutopilotPercents";
                break;
            }
            case "LIGHTHOUSE":{
                hbLoadMethodName = "B2B.InvestNum1.AutopilotPercents";
                break;
            }
            default:{
                hbLoadMethodName = "B2B.InvestNum1.AutopilotPercents";
            }
        }
        return hbLoadMethodName;
    }



    /**
     * Метод подготовки данных для отчетов по продуктам Сбербанк страхование
     * жизни
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BSberLifePrintDocDataProvider(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        // загрузка данных договора базовой версией поставщика
        params.put(RETURN_AS_HASH_MAP, true);
        params.put("LOADALLDATA", 1L);
        Long contractID = getLongParamLogged(params, "CONTRID");
        Long prodConfID = getLongParamLogged(params, "PRODCONFID");
        if (prodConfID == null) {
            Map<String, Object> contrParams = new HashMap<String, Object>();
            contrParams.put("CONTRID", contractID);
            prodConfID = getLongParam(this.callServiceTimeLoggedAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamExShort", contrParams, login, password, "PRODCONFID"));
            logger.debug("PRODCONFID (from dsB2BContractBrowseListByParamExShort query) = " + prodConfID);
            params.put("PRODCONFID", prodConfID);
        }
        Map<String, Object> result = this.callServiceTimeLogged(B2BPOSWS_SERVICE_NAME, "dsB2BBasePrintDocDataProvider", params, login, password);
        // генерация строковых представлений для сумм
        //result.put(RETURN_AS_HASH_MAP, true);
        //result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractTextSums", result, login, password);
        //
        Map<String, Object> prodConfMap = (Map<String, Object>) result.get("PRODUCTMAP");
        Map<String, Object> prodVerMap = (Map<String, Object>) prodConfMap.get("PRODVER");
        Map<String, Object> prodMap = (Map<String, Object>) prodVerMap.get("PROD");
        Map<String, Object> contrExtMap = (Map<String, Object>) result.get("CONTREXTMAP");
        Map<String, Object> insuredMap = (Map<String, Object>) result.get("INSUREDMAP");
        String prodSysName = getStringParamLogged(prodMap, "SYSNAME");
        // для Инвестиции (Маяк) считаем вычисляем дату начала, окончания действия договора, по окнам траншей
        //
        //if (prodSysName.equalsIgnoreCase("B2B_INVEST_NUM1") || prodSysName.equalsIgnoreCase("B2B_INVEST_COUPON")) {
        if (prodSysName.equalsIgnoreCase("LIGHTHOUSE") || prodSysName.equalsIgnoreCase("SMART_POLICY_RB_ILIK")
                || prodSysName.equalsIgnoreCase("SMART_POLICY") || prodSysName.equalsIgnoreCase("SMART_POLICY_LIGHT")) {
            //если состояние договора не равно "B2B_CONTRACT_SG"
            // после подписания даты не пересчитываются, даже если происходит перепечать.
            if (!"B2B_CONTRACT_SG".equalsIgnoreCase(getStringParam(result.get("STATESYSNAME")))) {
                boolean isSkipDateReCalc = getBooleanParam(params.get("SKIPDATESRECALC"), false);
                if (isSkipDateReCalc) {
                    logger.debug("On print contract dates recalculation by tranche's windows info is skipped by SKIPDATESRECALC flag.");
                } else {
                    logger.debug("On print contract dates recalculation by tranche's windows info...");
                    // calcStartFinishDatesByTranche
                    // вычислить дату начала действия
                    Date resDate = getDateFromTrancheHB(new Date(), login, password);
                    if (resDate != null) {
                        result.put("STARTDATE", parseAnyDate(resDate, Double.class, "STARTDATE"));
                        // вычислить дату окончания действия
                        getFinishDateByStartDateAndTermId(result, login, password);
                        // по умолчанию считается, что договор корректный (и если это действительно так, то указанные ниже расширенные атрибуты штатно сохранятся в договор)
                        contrExtMap.put("isOnPrintValidationFailed", 0L);
                        contrExtMap.put("onPrintValidationErrorMsg", "");
                        contrExtMap.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID); // поскольку договор только что был загружен из БД на MODIFIED_ID всегда заменяется значение UNMODIFIED_ID
                        // сохранить изменения в договор.
                        // TODO: возможно стоит разнести дату начала, окончания по всей оргструктуре.
                        result.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID); // поскольку договор только что был загружен из БД на MODIFIED_ID всегда заменяется значение UNMODIFIED_ID
                        result.put(RETURN_AS_HASH_MAP, true);
                        Map<String, Object> saveResult = this.callService(Constants.B2BPOSWS, "dsB2BContrSave", result, login, password);
                        String saveResultErrorMsg = getStringParamLogged(saveResult, "Error");
                        if (saveResultErrorMsg.isEmpty()) {
                            // сохранять isOnPrintValidationFailed = 0L и onPrintValidationErrorMsg = "" здесь не требуется:
                            // если валидация прошла успешно - они уже сохранены в договор, т.к. были переданы в сохранение ранее
                        } else {
                            // валидация при сохранении договора с новыми датами провалилась, данные договора не сохранены
                            // необходимо обновить соответсвующие расширенные атрибуты договора (вместо сохранения некорректных сведений)
                            logger.debug("Validation on contract save is failed - updating contract extended value 'isOnPrintValidationFailed' to '1' (and 'onPrintValidationErrorMsg' to error description) instead of saving invalid contract data...");
                            // параметры расширенных атрибутов договора
                            Long contrExtID = getLongParamLogged(contrExtMap, "CONTREXTID");
                            Long contrExtHBDataVerID = getLongParamLogged(contrExtMap, "HBDATAVERID");
                            if (contrExtHBDataVerID == null) {
                                contrExtHBDataVerID = getLongParamLogged(prodConfMap, "HBDATAVERID");
                            }
                            if ((contrExtID != null) && (contrExtHBDataVerID != null)) {
                                // обновление расширенных атрибутов договора - isOnPrintValidationFailed и onPrintValidationErrorMsg
                                Map<String, Object> contrExtMapUpdateParams = new HashMap<String, Object>();
                                contrExtMapUpdateParams.put("CONTREXTID", contrExtID);
                                contrExtMapUpdateParams.put("HBDATAVERID", contrExtHBDataVerID);
                                contrExtMapUpdateParams.put("isOnPrintValidationFailed", 1L);
                                contrExtMapUpdateParams.put("onPrintValidationErrorMsg", saveResultErrorMsg);
                                updateContractValues(contrExtMapUpdateParams, login, password);
                                logger.debug("Updating contract extended value 'isOnPrintValidationFailed' to '1' (and 'onPrintValidationErrorMsg' to error description) finished.");
                                contrExtMap.put("isOnPrintValidationFailed", 1L);
                                contrExtMap.put("onPrintValidationErrorMsg", saveResultErrorMsg);
                            } else {
                                logger.error("Error during updating contract extended value - CONTREXTID or HBDATAVERID needed for update contract extended properties not found!");
                            }
                            result.put("Error", saveResultErrorMsg);

                            // досрочное завершение работы провайдера (возможно, не требуется?)
                            logger.debug("On print contract dates recalculation by tranche's windows info failed due to invalidating of contract data by new date's values.");
                            return result;
                        }
                        logger.debug("On print contract dates recalculation by tranche's windows info successfully finished.");
                    }
                }
            }
            // по дате начала действия договора (у маяка она вычисляется по окнам траншей.)
            // получаем дату окончания окна транша. эта дата = PAYMENTENDDATE в печатной форме.

            Date paymentEndDate = getEndPayDateByDateFromTrancheHB(getDateParam(result.get("STARTDATE")), login, password);
            result.put("PAYMENTENDDATE", paymentEndDate);
        }
        // для первого шага считаем возраст ребенка
        //if (prodSysName.equalsIgnoreCase("B2B_FIRST_STEP")) {
        if (prodSysName.equalsIgnoreCase("FIRSTCAPITAL_RB-FCC0")) {
            Date insuredBirthDate = (Date) parseAnyDate(insuredMap.get("BIRTHDATE"), Date.class, "BIRTHDATE");
            insuredMap.put("AGE", Long.valueOf(calcAge(insuredBirthDate)));
        }

        // текущая дата (дата печати документа)
        Date todayDate = new Date();
        result.put("TODAYDATE", todayDate);

        // ИД срока действия договора
        Long termID = getLongParam(result.get("TERMID"));
        // мапа со сведениями о сроке действия договора (из данных продукта по ИД срока действия)
        Map<String, Object> termMap = getTermMapById(prodConfMap, termID);
        // срок действия договора - строковое наименование
        String termNameStr = getStringParam(termMap, "NAME");
        // срок действия договора - количество лет
        Long termYearsCount = getLongParam(termMap, "YEARCOUNT");
        String termYearsCountStr = getStringParam(termMap, "YEARCOUNT");
        // срок действия договора - количество месяцев
        Long termMonthsCountCount = getLongParam(termMap, "MONTHCOUNT");
        String termMonthsCountStr = getStringParam(termMap, "MONTHCOUNT");

        // генерация дат для выкупных сумм
        //Long termInYears = getTermInYearsById(prodConfMap, termID);
        Long termInYears = termYearsCount; // срок действия договора - количество лет
        if (termInYears == null) {
            termInYears = 0L;
        }
        Date startDate = (Date) parseAnyDate(result.get("STARTDATE"), Date.class, "STARTDATE");
        if (startDate != null) {
            GregorianCalendar curSDate = new GregorianCalendar();
            curSDate.setTime(startDate);
            for (int i = 1; i <= termInYears; i++) {
                contrExtMap.put("redemptionSumYear" + String.valueOf(i) + "DateFrom", df.format(curSDate.getTime()));
                curSDate.add(Calendar.YEAR, 1);
                curSDate.add(Calendar.DAY_OF_YEAR, -1);
                contrExtMap.put("redemptionSumYear" + String.valueOf(i) + "DateTo", df.format(curSDate.getTime()));
                curSDate.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        // разыменовка партисипентов
        Map<String, Object> participHBMap = new HashMap<String, Object>();
        //participHBMap.put("PROFLIST", loadHandbookData(null, "B2B.Life.Profession", login, password));
        //participHBMap.put("KOALIST", loadHandbookData(null, "B2B.Life.KindOfActivity", login, password));
        participantResolveParams((Map<String, Object>) result.get("INSURERMAP")/*, participHBMap*/, login, password);
        participantResolveParams(insuredMap/*, participHBMap*/, login, password);
        // для выгодоприобретателей разыменовываем покрытия
        if (result.get("BENEFICIARYLIST") != null) {
            List<Map<String, Object>> benefList = (List<Map<String, Object>>) result.get("BENEFICIARYLIST");
            for (Map<String, Object> bean : benefList) {
                logger.debug("Resolving beneficiary...");
                if (bean.get("INSCOVERID") != null) {
                    Map<String, Object> prodInsCoverMap = getProdInsCoverMap(getLongParamLogged(bean, "INSCOVERID"), login, password);
                    bean.put("RISK", prodInsCoverMap.get("NAME"));
                    bean.put("RISKSYSNAME", prodInsCoverMap.get("SYSNAME"));
                }
                Long typeID = getLongParamLogged(bean, "TYPEID");
                String typeStr = "";
                if (typeID != null) {
                    if (typeID.equals(1L)) {
                        // TODO если в contrExtMap insurerIsInsured == 1 то
                        typeStr = "Страхователь (совпадает с ЗЛ)";
                        // TODO иначе typeStr = "Застрахованный";
                    } else if (typeID.equals(2L)) {
                        typeStr = "По закону";
                    } else if (typeID.equals(3L)) {
                        typeStr = "Новый";
                    } else if (typeID.equals(4L)) {
                        typeStr = "Страхователь";
                    }
                }
                bean.put("TYPESTR", typeStr);
                Map<String, Object> participMap = (Map<String, Object>) bean.get("PARTICIPANTMAP");
                if ((participMap != null) && (!participMap.isEmpty())) {
                    participantResolveParams(participMap/*, participHBMap*/, login, password);
                }
                logger.debug("Resolving beneficiary finihed.");
            }
        }
        // получение информации о сотруднике
        Long createUserId = getLongParam(result.get("CREATEUSERID"));
        Map<String, Object> emplParams = new HashMap<String, Object>();
        emplParams.put(RETURN_AS_HASH_MAP, "TRUE");
        emplParams.put("useraccountid", createUserId);
        Map<String, Object> emplRes = this.selectQuery("dsUserAccountGetInfoById", null, emplParams);
        Map<String, Object> createUserMap = null;
        if (emplRes != null) {
            List<Map<String, Object>> emplResList = (List<Map<String, Object>>) emplRes.get(RESULT);
            if ((emplResList != null) && emplResList.size() > 0) {
                createUserMap = emplResList.get(0);
            }
        }
        if (createUserMap == null) {
            logger.error("Can't get user account info from dsUserAccountGetInfoById query result!");
            createUserMap = new HashMap<String, Object>();
        }
        createUserMap.remove("PASSWORD"); // это лишнее в результатах, пароли возвращать здесь не требуется
        // возвращается всегда или дейстивтельный e-mail или пустая строка, никогда не возвращается null (см. п. 8 дока 'Список работ по ИСЖ, НСЖ (ПК), НСЖ (СА), Форсаж', закладка 'Тест041016')
        String eMail = getStringParamLogged(createUserMap, "EMAIL");
        createUserMap.put("EMAIL", eMail);
        // возвращается всегда или дейстивтельная должность или пустая строка, никогда не возвращается null (см. п. 9 дока 'Список работ по ИСЖ, НСЖ (ПК), НСЖ (СА), Форсаж', закладка 'Тест041016')
        String position = getStringParamLogged(createUserMap, "POSITION");
        createUserMap.put("POSITION", position);



        // ПАРТНЕР
        Long createUserDepartmentID = getLongParamLogged(createUserMap, "DEPARTMENTID");
        // получение наименование партнера для конкретного дочернего подразделения по ИД этого дочернего подразделения

        Map<String, String> departmentsNamesMap = getDepartmentsTree(createUserDepartmentID, login, password);

        createUserMap.put("VSP", departmentsNamesMap.get("VSP"));
        createUserMap.put("OSB", departmentsNamesMap.get("OSB"));
        createUserMap.put("TERRITORYBANK", departmentsNamesMap.get("TB"));

        result.put("CREATEUSERMAP", createUserMap);

        String partnerName = getPartnerNameByChildDepartmentID(createUserDepartmentID, "", login, password);
        createUserMap.put("PARTNERNAME", partnerName);
        result.put("CREATEUSERPARTNERNAME", partnerName);

        //получение информации о сотруднике, выполняющем печать
        Map<String, Object> updateUserParams = new HashMap<String, Object>();
        updateUserParams.put(RETURN_AS_HASH_MAP, "TRUE");
        updateUserParams.put("login", login);
        Map<String, Object> printUserRes = this.selectQuery("dsUserAccountGetInfoByLogin", null, updateUserParams);
        Map<String, Object> printUserMap = null;
        if (printUserRes != null) {
            List<Map<String, Object>> printUserResList = (List<Map<String, Object>>) printUserRes.get(RESULT);
            if ((printUserResList != null) && printUserResList.size() > 0) {
                printUserMap = printUserResList.get(0);
            }
        }
        if (printUserMap == null) {
            logger.error("Can't get user account info from dsUserAccountGetInfoById query result!");
            printUserMap = new HashMap<String, Object>();
        }
        printUserMap.remove("PASSWORD"); // это лишнее в результатах, пароли возвращать здесь не требуется
        // возвращается всегда или дейстивтельный e-mail или пустая строка, никогда не возвращается null (см. п. 8 дока 'Список работ по ИСЖ, НСЖ (ПК), НСЖ (СА), Форсаж', закладка 'Тест041016')
        String updateUserEmail = getStringParamLogged(printUserMap, "EMAIL");
        printUserMap.put("EMAIL", updateUserEmail);
        // возвращается всегда или дейстивтельная должность или пустая строка, никогда не возвращается null (см. п. 9 дока 'Список работ по ИСЖ, НСЖ (ПК), НСЖ (СА), Форсаж', закладка 'Тест041016')
        String updateUserPosition = getStringParamLogged(createUserMap, "POSITION");
        printUserMap.put("POSITION", updateUserPosition);

        result.put("PRINTUSERMAP", printUserMap);

        //
        result.put("PAYVARSTR", getPaymentVariantNameById(prodConfMap, getLongParam(result.get("PAYVARID"))));
        //result.put("TERMSTR", getTermMapById(prodConfMap, getLongParam(result.get("TERMID"))).get("NAME"));
        result.put("TERMSTR", termNameStr); // срок действия договора - строковое наименование
        result.put("TERMYEARCOUNT", termYearsCountStr); // срок действия договора - количество лет (строка)
        result.put("TERMMONTHCOUNT", termMonthsCountStr); // срок действия договора - количество месяцев (строка)
        result.put("PRODPROGSTR", getProgramNameById(prodConfMap, getLongParam(result.get("PRODPROGID"))));
        result.put("INSAMCURRENCYSTR", getCurrencyNameById(prodConfMap, getLongParam(result.get("INSAMCURRENCYID"))));
        //
        // разыменовка расширенных атрибутов
        if (contrExtMap.get("uwPolicy43Changes") != null) {
            if (contrExtMap.get("uwPolicy43Changes").toString().isEmpty()) {
                contrExtMap.put("uwPolicy43Changes", "Особые условия отсутствуют");
            }
        } else {
            contrExtMap.put("uwPolicy43Changes", "Особые условия отсутствуют");
        }

        // Пол застрахованного
        resolveGender(contrExtMap, "insuredGender");
        // Страхователь является застрахованным
        resolveFlag(contrExtMap, "insurerIsInsured");
        // Клиент соответствует декларации застрахованного
        resolveFlag(contrExtMap, "insuredDeclCompliance");
        // Опция АВТОПИЛОТ подключена
        resolveFlag(contrExtMap, "isAutopilot");
        // Автопилот ВВЕРХ (Take profit)
        resolveFlag(contrExtMap, "isAutopilotTakeProfit");
        // Автопилот ВНИЗ (Stop Loss)
        resolveFlag(contrExtMap, "isAutopilotStopLoss");
        // Повышенный уровень гарантии
        resolveFlag(contrExtMap, "assuranceLevelIncreased");
        // Клиент согласен на телеандеррайтинг
        resolveFlagBool(contrExtMap, "consentToTeleUnderwriting");
        // Клиент согласен на передачу данных в иностранный налоговый орган
        resolveFlagBool(contrExtMap, "insuredTransDataCompliance");

        // Уровень гарантии
        Long assuranceLevelHid = getLongParamLogged(contrExtMap, "assuranceLevel");
        if (assuranceLevelHid != null) {
            // используется справочник
            Map<String, Object> hbParams = new HashMap<String, Object>();
            String methodName = getHBLoadMethodNameByProdSysname(prodSysName);
            hbParams.put("hid", assuranceLevelHid);
            List<Map<String, Object>> filteredList = loadHandbookData(hbParams, methodName , login, password);
            if ((filteredList != null) && (filteredList.size() == 1)) {
                Map<String, Object> selectedItem = filteredList.get(0);
                contrExtMap.put("assuranceLevelSelectedItem", selectedItem);
                contrExtMap.put("assuranceLevelStr", getStringParamLogged(selectedItem, "name"));
            }
        }
        Map<String,Object> baseActiveMap = null;
        // Маяк - фонд (он же Базовый актив)
        Long fundHid = getLongParamLogged(contrExtMap, FUND_ID_PARAMNAME);
        if (fundHid != null) {
            Long fundRefTypeId = getLongParamLogged(contrExtMap, FUND_REF_TYPE_ID_PARAMNAME);

            if (FUND_REF_TYPE_ID_PRODUCT_STRATEGY_HANDBOOK.equals(fundRefTypeId)) {
                // Тип ссылки на фонд (fundRefTypeId): 2 - на новый справочник (B2B.SBSJ.RelationStrategyProduct)
                baseActiveMap = processProductInvestmentStrategy(fundHid, login, password);
                // Для продукта Маяк Классический необходимо добавить список доступных фондов на дату заключения договора
                String hbName = getHBFundsLoadMethodNameByProdSysname(prodSysName);
                if (!hbName.isEmpty()) {

                    Map<String, Object> productStrategyParams = new HashMap<String, Object>();
                    productStrategyParams.put("STRATEGYISFORSALE", 1L);
                    if (result.get("SIGNDATE") != null) {
                        productStrategyParams.put("CHECKDATE", parseAnyDate(result.get("SIGNDATE"), Double.class, "SIGNDATE"));
                        productStrategyParams.put("CHECKSTRATEGYDATE", parseAnyDate(result.get("SIGNDATE"), Double.class, "SIGNDATE"));
                    } else {
                        productStrategyParams.put("CHECKDATE", parseAnyDate(todayDate, Double.class, "TODAYDATE"));
                        productStrategyParams.put("CHECKSTRATEGYDATE", parseAnyDate(todayDate, Double.class, "TODAYDATE"));
                    }
                    productStrategyParams.put("PRODSYSNAME", prodSysName);
                    productStrategyParams.put("STRATEGYTERMIDNULLOREQUAL", termID);
                    productStrategyParams.put("STRATCURRENCYID", result.get("INSAMCURRENCYID"));
                    String methodName = "dsB2BProductInvestmentStrategyBrowseListByParamEx";
                    List<Map<String, Object>> productStrategyList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, methodName, productStrategyParams, login, password);
                    result.put("FUNDLIST", productStrategyList);
                }
            } else {
                // Тип ссылки на фонд (fundRefTypeId): null/1 - на старый справочник (B2B.InvestNum1.Funds)
        // пытаемся получить инвест стратегию привязанную к продукту, если не находим - выполняем старый код
        List<Map<String, Object>> isList = getProdInvestStrategyList(prodConfID, login, password);
                if ((isList != null) && (!isList.isEmpty())) {
            // если находим - новый.
            //новый код по хид получает стратегию, базовый актив, и наполнение базового актива - тикеры.
                    // в формате: BASEACTIVEMAP(NAME,CODE,SYSNAME,TICKERLIST,COUPONSIZE,TICKERCOUNTSTR, TICKERCOUNT)
//        TICKERLIST[{NAME,SYSNAME,CODE,BRIEFNAME,STOCKEXCHNAME}]

                    Long currencyId = getLongParam(result, "INSAMCURRENCYID");
                    baseActiveMap = processInvestStrategy(isList, fundHid, currencyId, login, password);
                    // Для продукта Маяк Классический необходимо добавить список доступных фондов на дату заключения договора
                    if (prodSysName.equalsIgnoreCase(SYSNAME_INVEST_NUM1)) {

                        Map<String, Object> productStrategyParams = new HashMap<String, Object>();
                        productStrategyParams.put("STRATEGYISFORSALE", 1L);
                        if (result.get("SIGNDATE") != null) {
                            productStrategyParams.put("CHECKDATE", parseAnyDate(result.get("SIGNDATE"), Double.class, "SIGNDATE"));
                            productStrategyParams.put("CHECKSTRATEGYDATE", parseAnyDate(result.get("SIGNDATE"), Double.class, "SIGNDATE"));
                        } else {
                            productStrategyParams.put("CHECKDATE", parseAnyDate(todayDate, Double.class, "TODAYDATE"));
                            productStrategyParams.put("CHECKSTRATEGYDATE", parseAnyDate(todayDate, Double.class, "TODAYDATE"));
                        }
                        productStrategyParams.put("PRODSYSNAME", SYSNAME_INVEST_NUM1);
                        productStrategyParams.put("STRATEGYTERMIDNULLOREQUAL", termID);
                        productStrategyParams.put("STRATCURRENCYID", result.get("INSAMCURRENCYID"));
                        String methodName = "dsB2BProductInvestmentStrategyBrowseListByParamEx";
                        List<Map<String, Object>> productStrategyList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, methodName, productStrategyParams, login, password);
                        result.put("FUNDLIST", productStrategyList);
                    }
                } else {
                String hbName = "B2B.InvestNum1.Funds";
                if (prodSysName.equalsIgnoreCase(SYSNAME_INVEST_COUPON)) {
                    hbName = "B2B.InvestCoupon.Funds";
                }
                if (prodSysName.equalsIgnoreCase(SYSNAME_INVEST_NUM1)) {
                    hbName = "B2B.InvestNum1.Funds";
                }
                // используется справочник
                Map<String, Object> hbParams = new HashMap<String, Object>();
                hbParams.put("hid", fundHid);
                List<Map<String, Object>> filteredList = loadHandbookData(hbParams, hbName, login, password);
                if ((filteredList != null) && (filteredList.size() == 1)) {
                    Map<String, Object> selectedItem = filteredList.get(0);
                    contrExtMap.put("fundSelectedItem", selectedItem);
                    contrExtMap.put("fundStr", getStringParamLogged(selectedItem, "name"));
                } else {
                    // если по какой то причине справочник будет недоступен или тп - оставлена реализованная ранее разыменовка по константам, но с протоколированием ошибки
                    logger.error("Can not get info from handbook 'B2B.InvestNum1.Funds' for CONTREXTMAP.fund resolving! Hardcoded values will be used instead!");
                    String fundStr;
                    // вести синхронно с $rootScope.baseActiveList из commonIns.js до создания справочника "B2B.InvestNum1.BaseActive"
                    switch (fundHid.intValue()) {
                        case 1:
                            fundStr = "Глобальный фонд облигаций";
                            break;
                        case 2:
                            fundStr = "Недвижимость";
                            break;
                        case 3:
                            fundStr = "Новые технологии";
                            break;
                        case 4:
                            fundStr = "Золото";
                            break;
                        case 5:
                            fundStr = "Глобальный нефтяной сектор";
                            break;
                        default:
                            fundStr = "Неизвестный фонд";
                            break;
                    }
                    contrExtMap.put("fundStr", fundStr);
                }
            }
            }
        }

        // Маяк - проценты по показателю 'Автопилот ВНИЗ (Stop Loss)'
        Long autopilotStopLossPercHid = getLongParamLogged(contrExtMap, "autopilotStopLossPerc");
        String methodName = getHBAutopilotsLoadMethodNameByProdSysname(prodSysName);
        if (autopilotStopLossPercHid != null) {
            // используется справочник
            Map<String, Object> hbParams = new HashMap<String, Object>();
            hbParams.put("hid", autopilotStopLossPercHid);
            hbParams.put("autopilotTypeSysName", "stopLoss");

            List<Map<String, Object>> filteredList = loadHandbookData(hbParams, methodName, login, password);
            if ((filteredList != null) && (filteredList.size() == 1)) {
                Map<String, Object> selectedItem = filteredList.get(0);
                contrExtMap.put("autopilotStopLossPercSelectedItem", selectedItem);
                String name = getStringParamLogged(selectedItem, "name");
                contrExtMap.put("autopilotStopLossPercValueWithPct", name);
                name = name.replace(" %", ""); // символ процента исключаем из результата, уже присутствует в печатных документах
                contrExtMap.put("autopilotStopLossPercValue", name);
            } else {
                // если по какой то причине справочник будет недоступен или тп - оставлена реализованная ранее разыменовка по константам, но с протоколированием ошибки
                logger.error("Can not get info from handbook "+ methodName +" for CONTREXTMAP.autopilotStopLossPerc resolving! Hardcoded values will be used instead!");
                String autopilotStopLossPercValue;
                // вести синхронно с $rootScope.autopilotStopLossPctList из commonIns.js до создания справочника "B2B.Life.InvestNum1.AutoPilot_"
                switch (autopilotStopLossPercHid.intValue()) {
                    case 1:
                        autopilotStopLossPercValue = "15";
                        break;
                    case 2:
                        autopilotStopLossPercValue = "30";
                        break;
                    case 3:
                        autopilotStopLossPercValue = "50";
                        break;
                    default:
                        autopilotStopLossPercValue = "00";
                        break;
                }
                contrExtMap.put("autopilotStopLossPercValue", autopilotStopLossPercValue);
            }
        }
        // Маяк - проценты по показателю 'Автопилот ВВЕРХ (Take profit)'
        Long autopilotTakeProfitPercHid = getLongParamLogged(contrExtMap, "autopilotTakeProfitPerc");
        if (autopilotTakeProfitPercHid != null) {
            // используется справочник
            Map<String, Object> hbParams = new HashMap<String, Object>();
            hbParams.put("hid", autopilotTakeProfitPercHid);
            hbParams.put("autopilotTypeSysName", "takeProfit");
            List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.InvestNum1.AutopilotPercents", login, password);
            if ((filteredList != null) && (filteredList.size() == 1)) {
                Map<String, Object> selectedItem = filteredList.get(0);
                contrExtMap.put("autopilotTakeProfitPercSelectedItem", selectedItem);
                String name = getStringParamLogged(selectedItem, "name");
                contrExtMap.put("autopilotTakeProfitPercValueWithPct", name);
                name = name.replace(" %", ""); // символ процента исключаем из результата, уже присутствует в печатных документах
                contrExtMap.put("autopilotTakeProfitPercValue", name);
            } else {
                // если по какой то причине справочник будет недоступен или тп - оставлена реализованная ранее разыменовка по константам, но с протоколированием ошибки
                logger.error("Can not get info from handbook 'B2B.InvestNum1.AutopilotPercents' for CONTREXTMAP.autopilotTakeProfitPerc resolving! Hardcoded values will be used instead!");
                String autopilotTakeProfitPercValue;
                // вести синхронно с $rootScope.autopilotTakeProfitPctList из commonIns.js до создания справочника "B2B.Life.InvestNum1.AutoPilot_"
                switch (autopilotTakeProfitPercHid.intValue()) {
                    case 1:
                        autopilotTakeProfitPercValue = "15";
                        break;
                    case 2:
                        autopilotTakeProfitPercValue = "30";
                        break;
                    case 3:
                        autopilotTakeProfitPercValue = "50";
                        break;
                    default:
                        autopilotTakeProfitPercValue = "00";
                        break;
                }
                contrExtMap.put("autopilotTakeProfitPercValue", autopilotTakeProfitPercValue);
            }
        }

        // полный список рисков
        ArrayList<Map<String, Object>> riskList = getAllRisksListFromContract(result);
        result.put("riskList", riskList);

        // страховая сумма для вычисления тарифа (если null, то вычисление тарифа не требуется для данного продукта)
        Double insAmValueForTariffCalc = null;
        // строковое окончание для тарифа (например, " %"), зависит от продукта
        String tariffValueUnitsStr = "";

        // определение страховой суммы для вычисления тарифа (если требуется для данного продукта)
        if (prodSysName.equalsIgnoreCase(SYSNAME_CAPITAL)) {
            // Капитал - страховая сумма для вычисления тарифа
            for (Map<String, Object> risk : riskList) {
                String riskSysName = getStringParamLogged(risk, "PRODRISKSYSNAME");
                if (riskSysName.equals("survivalDeath")) {
                    // главный риск с основной суммой по договору
                    insAmValueForTariffCalc = getDoubleParamLogged(risk, "INSAMVALUE");
                    break;
                }
            }
        } else if ((prodSysName.equalsIgnoreCase(SYSNAME_FIRST_STEP)) || (prodSysName.equalsIgnoreCase(SYSNAME_RIGHT_DECISION))) {
            // Первый шаг и Верное решение - страховая сумма для вычисления тарифа ("в качестве страховой суммы использовать ГСС")
            if (contrExtMap != null) {
                insAmValueForTariffCalc = getDoubleParamLogged(contrExtMap, "guarInsAmValNoRisk");
                tariffValueUnitsStr = " %"; // строковое окончание для тарифа
            }
        }

        // если указана значимая страховая сумма для вычисления тарифа, то требуется определить тариф для данного продукта согласно паспорту:
        // "Размер и порядок определения страхового тарифа: ...размер премии, подлежащей уплате Страхователем, определяется как произведение страховой суммы на страховой тариф."
        if ((insAmValueForTariffCalc != null) && (insAmValueForTariffCalc > 0) && (contrExtMap != null)) {
            // премия по договору
            Double premValue = getDoubleParamLogged(result, "PREMVALUE");
            // тариф
            Double insTariffValue = (premValue / insAmValueForTariffCalc) * 100;
            logger.debug("Insurance tariff = " + insTariffValue);
            String insTariffValueStr = String.format("%.2f%s", insTariffValue, tariffValueUnitsStr);
            insTariffValueStr = insTariffValueStr.replaceAll("\\.", ",");
            logger.debug("Insurance tariff string = " + insTariffValueStr);
            contrExtMap.put("insTariffValue", insTariffValueStr);
        }

        // генерация строковых представлений для всех дат
        genDateStrs(result, "*");

        genSumStr(result, "INSAMVALUE");
        genSumStr(result, "PREMVALUE");
        result.put("BASEACTIVEMAP", baseActiveMap);
        if (baseActiveMap != null) {
            if (baseActiveMap.get("NAME") != null) {
                result.put("BASEACTIVENAMESTR", baseActiveMap.get("NAME").toString());
             }
        }
        return result;
    }

    protected Map<String, Object> processInvestStrategy(List<Map<String, Object>> isList, Long fundHid, Long currencyId, String login, String password) throws Exception {
    //BASEACTIVEMAP(NAME,CODE,SYSNAME,TICKERLIST,COUPONS
        Map<String, Object>investStrategyMap = getInvestStrategyMap(fundHid, currencyId, login, password);
        if (investStrategyMap != null) {
            if (investStrategyMap.get("INVBASEACTIVEID") != null) {
                Long baseActiveId = getLongParam(investStrategyMap.get("INVBASEACTIVEID"));
                Map<String, Object>baseActiveMap = getBaseActiveMap(baseActiveId, login, password);
                baseActiveMap.put("COUPONSIZE", investStrategyMap.get("COUPONSIZE"));

                baseActiveMap.put("CURRENCYID", investStrategyMap.get("CURRENCYID"));
                List<Map<String,Object>> tickerList = getTickerList(baseActiveId, login, password);
                if (tickerList != null) {
                    int tickerCount = tickerList.size();
                    baseActiveMap.put("TICKERLIST", tickerList);
                    // todo сформировать количество тикеров в нужном падеже.

                    baseActiveMap.put("TICKERCOUNTSTR", getIntStringGenetive(tickerCount));
                    baseActiveMap.put("TICKERCOUNT", tickerCount);
                }
                return baseActiveMap;
            }
        }
        return null;
    }

    private List<Map<String,Object>> getTickerList(Long baseActiveId, String login, String password) throws Exception {
        //dsB2BInvestBaseActiveTickerBrowseListByParamEx
        Map<String, Object> param = new HashMap<>();
        param.put("INVBASEACTIVEID", baseActiveId);
        return this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BInvestBaseActiveTickerBrowseListByParamEx", param, login, password);

    }

    private Map<String,Object> getBaseActiveMap(Long baseActiveId, String login, String password)  throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("INVBASEACTIVEID", baseActiveId);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String,Object> baseActiveMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BInvestBaseActiveBrowseListByParam", param, login, password);
        return baseActiveMap;
    }

    private Map<String,Object> getInvestStrategyMap(Long fundHid, Long currencyId, String login, String password)  throws Exception {
        Map<String, Object> param = new HashMap<>();
        //TODO: как временное решение, пока фонды на редакторе продукта не фильтруются по валюте
        // в NOTE храним hid из справочника. на выходе - получим список по валюте.
        // и выбираем по валюте договора.
        //param.put("INVESTSTRATEGYID", fundHid);
        param.put("NOTE", fundHid);
        param.put("CURRENCYID", currencyId);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String,Object> investStrategyMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BInvestStrategyBrowseListByParam", param, login, password);
        return investStrategyMap;
    }

    protected List<Map<String, Object>> getProdInvestStrategyList(Long prodConfID, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("PRODCONFID", prodConfID);
        return this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BProductInvestBrowseListByParam", param, login, password);
    }

    /**
     * Метод подготовки данных для выгрузок по продуктам Сбербанк страхование
     * жизни
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BSberLifeExportReportDataProvider(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        // ИД договора
        Long contractID = getLongParamLogged(params, "CONTRID");
        // основные данных о продукте (ИД, сис. имя)
        Long prodConfID = getLongParamLogged(params, "PRODCONFID");
        String prodSysName = getStringParamLogged(params, "PRODSYSNAME");

        if ((prodConfID == null) || (prodSysName.isEmpty())) {
            // загрузка данных о продукте (ИД, сис. имя)
            Map<String, Object> contrParams = new HashMap<String, Object>();
            contrParams.put("CONTRID", contractID);
            contrParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> productMainInfo = this.callServiceTimeLogged(B2BPOSWS_SERVICE_NAME, "dsB2BProductGetMainInfoByContractID", contrParams, login, password);
            prodConfID = getLongParamLogged(productMainInfo, "PRODCONFID");
            prodSysName = getStringParamLogged(productMainInfo, "PRODSYSNAME");
        }

        // получение полной мапы продукта (если уже загружалась)
        Map<String, Object> productMap = productCache.get(prodSysName);
        if (productMap == null) {
            // загрузка полной мапы продукта из БД
            Map<String, Object> productParams = new HashMap<String, Object>();
            productParams.put("PRODCONFID", prodConfID);
            productParams.put("LOADALLDATA", 1L);
            productParams.put("HIERARCHY", false);
            productParams.put(RETURN_AS_HASH_MAP, true);
            productMap = this.callServiceTimeLogged(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);
            // полную мапу продукта следует запомнить для дальнейшего использования
            productCache.put(prodSysName, productMap);
        }

        // загрузка договора из БД
        Map<String, Object> contrParam = new HashMap<String, Object>();
        contrParam.put("CONTRID", contractID);
        contrParam.put("PRODUCTMAP", productMap);
        contrParam.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> contract = this.callServiceTimeLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractUniversalLoad", contrParam, login, password);

        //
        Map<String, Object> prodConfMap = (Map<String, Object>) productMap;
        //Map<String, Object> prodVerMap = (Map<String, Object>) prodConfMap.get("PRODVER");
        //Map<String, Object> prodMap = (Map<String, Object>) prodVerMap.get("PROD");
        Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
        Map<String, Object> insuredMap = (Map<String, Object>) contract.get("INSUREDMAP");

        // для первого шага считаем возраст ребенка
        //if (prodSysName.equalsIgnoreCase("B2B_FIRST_STEP")) {
        if (prodSysName.equalsIgnoreCase("FIRSTCAPITAL_RB-FCC0")) {
            if (insuredMap != null) {
                Date insuredBirthDate = (Date) parseAnyDate(insuredMap.get("BIRTHDATE"), Date.class, "BIRTHDATE");
                insuredMap.put("AGE", Long.valueOf(calcAge(insuredBirthDate)));
            }
        }

        // текущая дата (дата печати документа)
        Date todayDate = new Date();
        contract.put("TODAYDATE", todayDate);

        // генерация дат для выкупных сумм
        Long termInYears = getTermInYearsById(prodConfMap, getLongParam(contract.get("TERMID")));
        GregorianCalendar curSDate = new GregorianCalendar();
        curSDate.setTime((Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE"));
        if (termInYears != null) {
            for (int i = 1; i <= termInYears; i++) {
                contrExtMap.put("redemptionSumYear" + String.valueOf(i) + "DateFrom", df.format(curSDate.getTime()));
                curSDate.add(Calendar.YEAR, 1);
                curSDate.add(Calendar.DAY_OF_YEAR, -1);
                contrExtMap.put("redemptionSumYear" + String.valueOf(i) + "DateTo", df.format(curSDate.getTime()));
                curSDate.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        // разыменовка партисипентов
        //Map<String, Object> participHBMap = new HashMap<String, Object>();
        //participHBMap.put("PROFLIST", loadHandbookData(null, "B2B.Life.Profession", login, password));
        //participHBMap.put("KOALIST", loadHandbookData(null, "B2B.Life.KindOfActivity", login, password));
        participantResolveParams((Map<String, Object>) contract.get("INSURERMAP")/*, participHBMap*/, login, password);
        participantResolveParams(insuredMap/*, participHBMap*/, login, password);
        // для выгодоприобретателей разыменовываем покрытия
        if (contract.get("BENEFICIARYLIST") != null) {
            List<Map<String, Object>> benefList = (List<Map<String, Object>>) contract.get("BENEFICIARYLIST");
            for (Map<String, Object> bean : benefList) {
                logger.debug("Resolving beneficiary...");
                if (bean.get("INSCOVERID") != null) {
                    Map<String, Object> prodInsCoverMap = getProdInsCoverMap(getLongParamLogged(bean, "INSCOVERID"), login, password);
                    bean.put("RISK", prodInsCoverMap.get("NAME"));
                    bean.put("RISKSYSNAME", prodInsCoverMap.get("SYSNAME"));
                }
                Long typeID = getLongParamLogged(bean, "TYPEID");
                String typeStr = "";
                if (typeID != null) {
                    if (typeID.equals(1L)) {
                        // TODO если в contrExtMap insurerIsInsured == 1 то
                        typeStr = "Страхователь (совпадает с ЗЛ)";
                        // TODO иначе typeStr = "Застрахованный";
                    } else if (typeID.equals(2L)) {
                        typeStr = "По закону";
                    } else if (typeID.equals(3L)) {
                        typeStr = "Новый";
                    } else if (typeID.equals(4L)) {
                        typeStr = "Страхователь";
                    }
                }
                bean.put("TYPESTR", typeStr);
                Map<String, Object> participMap = (Map<String, Object>) bean.get("PARTICIPANTMAP");
                if ((participMap != null) && (!participMap.isEmpty())) {
                    participantResolveParams(participMap/*, participHBMap*/, login, password);
                }
                logger.debug("Resolving beneficiary finihed.");
            }
        }
        // получение информации о сотруднике
        Long createUserId = getLongParam(contract.get("CREATEUSERID"));
        Map<String, Object> emplParams = new HashMap<String, Object>();
        emplParams.put(RETURN_AS_HASH_MAP, "TRUE");
        emplParams.put("useraccountid", createUserId);
        Map<String, Object> emplRes = this.selectQuery("dsUserAccountGetInfoById", null, emplParams);
        Map<String, Object> createUserMap = null;
        if (emplRes != null) {
            List<Map<String, Object>> emplResList = (List<Map<String, Object>>) emplRes.get(RESULT);
            if ((emplResList != null) && emplResList.size() > 0) {
                createUserMap = emplResList.get(0);
            }
        }
        if (createUserMap == null) {
            logger.error("Can't get user account info from dsUserAccountGetInfoById query result!");
            createUserMap = new HashMap<String, Object>();
        }
        createUserMap.remove("PASSWORD"); // это лишнее в результатах, пароли возвращать здесь не требуется
        // возвращается всегда или дейстивтельный e-mail или пустая строка, никогда не возвращается null (см. п. 8 дока 'Список работ по ИСЖ, НСЖ (ПК), НСЖ (СА), Форсаж', закладка 'Тест041016')
        String eMail = getStringParamLogged(createUserMap, "EMAIL");
        createUserMap.put("EMAIL", eMail);
        // возвращается всегда или дейстивтельная должность или пустая строка, никогда не возвращается null (см. п. 9 дока 'Список работ по ИСЖ, НСЖ (ПК), НСЖ (СА), Форсаж', закладка 'Тест041016')
        String position = getStringParamLogged(createUserMap, "POSITION");
        createUserMap.put("POSITION", position);
        contract.put("CREATEUSERMAP", createUserMap);

        // ПАРТНЕР
        Long createUserDepartmentID = getLongParamLogged(createUserMap, "DEPARTMENTID");
        // получение наименование партнера для конкретного дочернего подразделения по ИД этого дочернего подразделения
        // необходимо выбрать следующую структуру
        //  
        //  текущее подразделение - наименование - VSP
        //  родительское подразделение подразделение - наименование - OSB
        //  партнер - REGIONBANK
        //  партнер - TERRITORYBANK
        Map<String, String> brokermap = getBrockerNamesMap(createUserDepartmentID, login, password);
        //
        //
        contract.put("VSP", brokermap.get("VSP"));
        contract.put("OSB", brokermap.get("OSB"));
        contract.put("REGIONBANK", brokermap.get("REGIONBANK"));
        contract.put("TERRITORYBANK", brokermap.get("TERRITORYBANK"));
        //        

        String partnerName = getPartnerNameByChildDepartmentID(createUserDepartmentID, "", login, password);
        createUserMap.put("PARTNERNAME", partnerName);
        contract.put("CREATEUSERPARTNERNAME", partnerName);

        //
        contract.put("PAYVARSTR", getPaymentVariantNameById(prodConfMap, getLongParam(contract.get("PAYVARID"))));
        if (getLongParam(contract.get("TERMID")) != null) {
            contract.put("TERMSTR", getTermMapById(prodConfMap, getLongParam(contract.get("TERMID"))).get("NAME"));
            contract.put("TERMYEARCOUNT", getTermMapById(prodConfMap, getLongParam(contract.get("TERMID"))).get("YEARCOUNT"));
        }
        contract.put("PRODPROGSTR", getProgramNameById(prodConfMap, getLongParam(contract.get("PRODPROGID"))));
        contract.put("INSAMCURRENCYSTR", getCurrencyNameById(prodConfMap, getLongParam(contract.get("INSAMCURRENCYID"))));
        //
        // разыменовка расширенных атрибутов

        // Пол застрахованного
        resolveGender(contrExtMap, "insuredGender");
        // Страхователь является застрахованным
        resolveFlag(contrExtMap, "insurerIsInsured");
        // Клиент соответствует декларации застрахованного
        resolveFlag(contrExtMap, "insuredDeclCompliance");
        // Опция АВТОПИЛОТ подключена
        resolveFlag(contrExtMap, "isAutopilot");
        // Автопилот ВВЕРХ (Take profit)
        resolveFlag(contrExtMap, "isAutopilotTakeProfit");
        // Автопилот ВНИЗ (Stop Loss)
        resolveFlag(contrExtMap, "isAutopilotStopLoss");
        // Повышенный уровень гарантии
        resolveFlag(contrExtMap, "assuranceLevelIncreased");

        // Маяк - Уровень гарантии
        Long assuranceLevelHid = getLongParamLogged(contrExtMap, "assuranceLevel");
        if (assuranceLevelHid != null) {
            // используется справочник
            //Map<String, Object> hbParams = new HashMap<String, Object>();
            //hbParams.put("hid", assuranceLevelHid);
            //List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.InvestNum1.AssuranceLevel", login, password);
            //if ((filteredList != null) && (filteredList.size() == 1)) {
            //    Map<String, Object> selectedItem = filteredList.get(0);
            //    contrExtMap.put("assuranceLevelSelectedItem", selectedItem);
            //    contrExtMap.put("assuranceLevelStr", getStringParamLogged(selectedItem, "name"));
            //}
            Map<String, Object> handbookRecord = getHandbookRecordByHid("B2B.InvestNum1.AssuranceLevel", assuranceLevelHid, login, password);
            contrExtMap.put("assuranceLevelSelectedItem", handbookRecord);
            contrExtMap.put("assuranceLevelStr", getStringParamLogged(handbookRecord, "name"));
        }

        // Маяк - фонд (он же Базовый актив)
        Long fundHid = getLongParamLogged(contrExtMap, FUND_ID_PARAMNAME);
        if (fundHid != null) {
            // используется справочник
            //Map<String, Object> hbParams = new HashMap<String, Object>();
            //hbParams.put("hid", fundHid);
            //List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.InvestNum1.Funds", login, password);
            //if ((filteredList != null) && (filteredList.size() == 1)) {
            //    Map<String, Object> selectedItem = filteredList.get(0);
            //    contrExtMap.put("fundSelectedItem", selectedItem);
            //    contrExtMap.put("fundStr", getStringParamLogged(selectedItem, "name"));
            //} else {
            //    // если по какой то причине справочник будет недоступен или тп - оставлена реализованная ранее разыменовка по константам, но с протоколированием ошибки
            //    logger.error("Can not get info from handbook 'B2B.InvestNum1.Funds' for CONTREXTMAP.fund resolving! Hardcoded values will be used instead!");
            //    String fundStr;
            //    // вести синхронно с $rootScope.baseActiveList из commonIns.js до создания справочника "B2B.InvestNum1.BaseActive"
            //    switch (fundHid.intValue()) {
            //        case 1:
            //            fundStr = "Глобальный фонд облигаций";
            //            break;
            //        case 2:
            //            fundStr = "Недвижимость";
            //            break;
            //        case 3:
            //            fundStr = "Новые технологии";
            //            break;
            //        case 4:
            //            fundStr = "Золото";
            //            break;
            //        case 5:
            //            fundStr = "Глобальный нефтяной сектор";
            //            break;
            //        default:
            //            fundStr = "Неизвестный фонд";
            //            break;
            //    }
            //    contrExtMap.put("fundStr", fundStr);
            //}
            Map<String, Object> handbookRecord;
            String fundStr;
            Long fundRefTypeId = getLongParamLogged(contrExtMap, FUND_REF_TYPE_ID_PARAMNAME);
            if (FUND_REF_TYPE_ID_PRODUCT_STRATEGY_HANDBOOK.equals(fundRefTypeId)) {
                // Тип ссылки на фонд (fundRefTypeId): 2 - на новый справочник (B2B.SBSJ.RelationStrategyProduct)
                handbookRecord = getProductStrategy(fundHid, login, password);
                fundStr = getStringParamLogged(handbookRecord, "STRATEGYNAME");
            } else {
                // Тип ссылки на фонд (fundRefTypeId): null/1 - на старый справочник (B2B.InvestNum1.Funds)
                handbookRecord = getHandbookRecordByHid("B2B.InvestNum1.Funds", fundHid, login, password);
                fundStr = getStringParamLogged(handbookRecord, "name");
            }
            contrExtMap.put("fundSelectedItem", handbookRecord);
            contrExtMap.put("fundStr", fundStr);
        }
        // проценты по показателю 'Автопилот ВНИЗ (Stop Loss)'
        Long autopilotStopLossPercHid = getLongParamLogged(contrExtMap, "autopilotStopLossPerc");
        if (autopilotStopLossPercHid != null) {
            // используется справочник
            Map<String, Object> hbParams = new HashMap<String, Object>();
            hbParams.put("hid", autopilotStopLossPercHid);
            hbParams.put("autopilotTypeSysName", "stopLoss");
            List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.InvestNum1.AutopilotPercents", login, password);
            if ((filteredList != null) && (filteredList.size() == 1)) {
                Map<String, Object> selectedItem = filteredList.get(0);
                contrExtMap.put("autopilotStopLossPercSelectedItem", selectedItem);
                String name = getStringParamLogged(selectedItem, "name");
                contrExtMap.put("autopilotStopLossPercValueWithPct", name);
                name = name.replace(" %", ""); // символ процента исключаем из результата, уже присутствует в печатных документах
                contrExtMap.put("autopilotStopLossPercValue", name);
            } else {
                // если по какой то причине справочник будет недоступен или тп - оставлена реализованная ранее разыменовка по константам, но с протоколированием ошибки
                logger.error("Can not get info from handbook 'B2B.InvestNum1.AutopilotPercents' for CONTREXTMAP.autopilotStopLossPerc resolving! Hardcoded values will be used instead!");
                String autopilotStopLossPercValue;
                // вести синхронно с $rootScope.autopilotStopLossPctList из commonIns.js до создания справочника "B2B.Life.InvestNum1.AutoPilot_"
                switch (autopilotStopLossPercHid.intValue()) {
                    case 1:
                        autopilotStopLossPercValue = "15";
                        break;
                    case 2:
                        autopilotStopLossPercValue = "30";
                        break;
                    case 3:
                        autopilotStopLossPercValue = "50";
                        break;
                    default:
                        autopilotStopLossPercValue = "00";
                        break;
                }
                contrExtMap.put("autopilotStopLossPercValue", autopilotStopLossPercValue);
            }
        }
        // проценты по показателю 'Автопилот ВВЕРХ (Take profit)'
        Long autopilotTakeProfitPercHid = getLongParamLogged(contrExtMap, "autopilotTakeProfitPerc");
        if (autopilotTakeProfitPercHid != null) {
            // используется справочник
            Map<String, Object> hbParams = new HashMap<String, Object>();
            hbParams.put("hid", autopilotTakeProfitPercHid);
            hbParams.put("autopilotTypeSysName", "takeProfit");
            List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.InvestNum1.AutopilotPercents", login, password);
            if ((filteredList != null) && (filteredList.size() == 1)) {
                Map<String, Object> selectedItem = filteredList.get(0);
                contrExtMap.put("autopilotTakeProfitPercSelectedItem", selectedItem);
                String name = getStringParamLogged(selectedItem, "name");
                contrExtMap.put("autopilotTakeProfitPercValueWithPct", name);
                name = name.replace(" %", ""); // символ процента исключаем из результата, уже присутствует в печатных документах
                contrExtMap.put("autopilotTakeProfitPercValue", name);
            } else {
                // если по какой то причине справочник будет недоступен или тп - оставлена реализованная ранее разыменовка по константам, но с протоколированием ошибки
                logger.error("Can not get info from handbook 'B2B.InvestNum1.AutopilotPercents' for CONTREXTMAP.autopilotTakeProfitPerc resolving! Hardcoded values will be used instead!");
                String autopilotTakeProfitPercValue;
                // вести синхронно с $rootScope.autopilotTakeProfitPctList из commonIns.js до создания справочника "B2B.Life.InvestNum1.AutoPilot_"
                switch (autopilotTakeProfitPercHid.intValue()) {
                    case 1:
                        autopilotTakeProfitPercValue = "15";
                        break;
                    case 2:
                        autopilotTakeProfitPercValue = "30";
                        break;
                    case 3:
                        autopilotTakeProfitPercValue = "50";
                        break;
                    default:
                        autopilotTakeProfitPercValue = "00";
                        break;
                }
                contrExtMap.put("autopilotTakeProfitPercValue", autopilotTakeProfitPercValue);
            }
        }

        // полный список рисков
        ArrayList<Map<String, Object>> riskList = getAllRisksListFromContract(contract);
        contract.put("riskList", riskList);

        // страховая сумма для вычисления тарифа (если null, то вычисление тарифа не требуется для данного продукта)
        Double insAmValueForTariffCalc = null;
        // строковое окончание для тарифа (например, " %"), зависит от продукта
        String tariffValueUnitsStr = "";

        // определение страховой суммы для вычисления тарифа (если требуется для данного продукта)
        if (prodSysName.equalsIgnoreCase(SYSNAME_CAPITAL)) {
            // Капитал - страховая сумма для вычисления тарифа
            for (Map<String, Object> risk : riskList) {
                String riskSysName = getStringParamLogged(risk, "PRODRISKSYSNAME");
                if (riskSysName.equals("survivalDeath")) {
                    // главный риск с основной суммой по договору
                    insAmValueForTariffCalc = getDoubleParamLogged(risk, "INSAMVALUE");
                    break;
                }
            }
        } else if ((prodSysName.equalsIgnoreCase(SYSNAME_FIRST_STEP)) || (prodSysName.equalsIgnoreCase(SYSNAME_RIGHT_DECISION))) {
            // Первый шаг и Верное решение - страховая сумма для вычисления тарифа ("в качестве страховой суммы использовать ГСС")
            if (contrExtMap != null) {
                insAmValueForTariffCalc = getDoubleParamLogged(contrExtMap, "guarInsAmValNoRisk");
                tariffValueUnitsStr = " %"; // строковое окончание для тарифа
            }
        }

        // если указана значимая страховая сумма для вычисления тарифа, то требуется определить тариф для данного продукта согласно паспорту:
        // "Размер и порядок определения страхового тарифа: ...размер премии, подлежащей уплате Страхователем, определяется как произведение страховой суммы на страховой тариф."
        if ((insAmValueForTariffCalc != null) && (insAmValueForTariffCalc > 0) && (contrExtMap != null)) {
            // премия по договору
            Double premValue = getDoubleParamLogged(contract, "PREMVALUE");
            // тариф
            Double insTariffValue = (premValue / insAmValueForTariffCalc) * 100;
            logger.debug("Insurance tariff = " + insTariffValue);
            String insTariffValueStr = String.format("%.2f%s", insTariffValue, tariffValueUnitsStr);
            insTariffValueStr = insTariffValueStr.replaceAll("\\.", ",");
            logger.debug("Insurance tariff string = " + insTariffValueStr);
            contrExtMap.put("insTariffValue", insTariffValueStr);
        }

        // генерация строковых представлений для всех дат
        //genDateStrs(contract, "*");
        return contract;
    }

    protected List<Map<String, Object>> loadHandbookData(Map<String, Object> params, String hbName, String login, String password) throws Exception {
        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("HANDBOOKNAME", hbName);
        hbParams.put("HANDBOOKDATAPARAMS", params);
        hbParams.put(RETURN_LIST_ONLY, true);
        Map<String, Object> resultMap = this.callServiceTimeLogged(B2BPOSWS_SERVICE_NAME, "dsB2BHandbookDataBrowseByHBName", hbParams, login, password);
        List<Map<String, Object>> resultList = WsUtils.getListFromResultMap(resultMap);
        return resultList;
    }

    protected Map<String, Object> getHandbookRecordByHid(String hbName, Long hid, String login, String password) throws Exception {
        Map<Long, Map<String, Object>> hbMapByHid = hbMapsByHidCache.get(hbName);
        if (hbMapByHid == null) {
            List<Map<String, Object>> hbDataList = getHandbookDataList(hbName, login, password);
            hbMapByHid = getMapByFieldLongValues(hbDataList, "hid");
            hbMapsByHidCache.put(hbName, hbMapByHid);
        }
        Map<String, Object> hbRecord = hbMapByHid.get(hid);
        return hbRecord;
    }

    protected Map<String, Object> getHandbookRecordByParams(String hbName, Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> hbRecord = null;
        if ((params == null) || (params.isEmpty())) {
            // не переданы параметры
            hbRecord = new HashMap<String, Object>();
        } else if ((params.size() == 1) && (params.containsKey("hid"))) {
            // параметры содержат только hid - можно использовать getHandbookRecordByHid
            Long hid = getLongParam(params, "hid");
            hbRecord = getHandbookRecordByHid(hbName, hid, login, password);
        } else {
            // параметры могут содержать сложные ограничения - в таких случаях целесообразнее поиск предоставить БД
            List<Map<String, Object>> hbDataFromDB = loadHandbookData(params, hbName, login, password);
            if ((hbDataFromDB != null) && (hbDataFromDB.size() > 0)) {
                hbRecord = hbDataFromDB.get(0);
            } else {
                hbRecord = new HashMap<String, Object>();
            }
        }
        return hbRecord;
    }

    protected List<Map<String, Object>> getHandbookDataList(String hbName, String login, String password) throws Exception {
        List<Map<String, Object>> hbDataList = hbCache.get(hbName);
        if (hbDataList == null) {
            Map<String, Object> hbParams = new HashMap<String, Object>();
            hbParams.put("HANDBOOKNAME", hbName);
            hbParams.put(RETURN_LIST_ONLY, true);
            Map<String, Object> resultMap = this.callServiceTimeLogged(B2BPOSWS_SERVICE_NAME, "dsB2BHandbookDataBrowseByHBName", hbParams, login, password);
            hbDataList = WsUtils.getListFromResultMap(resultMap);
            hbCache.put(hbName, hbDataList);
        }
        return hbDataList;
    }

    private Date getDateFromTrancheHB(Date date, String login, String password) throws Exception {
        Map<String, Object> trancheHBParams = new HashMap<String, Object>();
        trancheHBParams.put("HANDBOOKNAME", "B2B.InvestNum1.TrancheWindow");
        trancheHBParams.put(RETURN_LIST_ONLY, true);
        List<Map<String, Object>> trancheList = this.callServiceAndGetListFromResultMap(Constants.B2BPOSWS, "dsB2BHandbookDataBrowseByHBName", trancheHBParams, login, password);
        CopyUtils.sortByLongFieldName(trancheList, "hid");
        for (Map<String, Object> map : trancheList) {
            Date startTrancheDate = getDateParam(map.get("saleWindowStartDATE"));
            Date finishTrancheDate = getDateParam(map.get("saleWindowFinishDATE"));
            Date contrStartDate = getDateParam(map.get("contractStartDATE"));
            if ((startTrancheDate.getTime() <= date.getTime())
                    && ((finishTrancheDate.getTime() + (24 * 60 * 60 * 1000)) > date.getTime())) {
                return contrStartDate;
            }
        }
        return null;
    }

    private Date getEndPayDateByDateFromTrancheHB(Date date, String login, String password) throws Exception {
        Map<String, Object> trancheHBParams = new HashMap<String, Object>();
        trancheHBParams.put("HANDBOOKNAME", "B2B.InvestNum1.TrancheWindow");
        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("contractStartDATE", date);
        parseDates(hbParams, Double.class);
        trancheHBParams.put("HANDBOOKDATAPARAMS", hbParams);
        trancheHBParams.put(RETURN_LIST_ONLY, true);
        List<Map<String, Object>> trancheList = this.callServiceAndGetListFromResultMap(Constants.B2BPOSWS, "dsB2BHandbookDataBrowseByHBName", trancheHBParams, login, password);
        CopyUtils.sortByLongFieldName(trancheList, "hid");
        if (trancheList.isEmpty()) {
            logger.error("can`t find tranche with contractStartDATE = " + date.toString());
            return date;
        } else {
            Map<String, Object> map = trancheList.get(0);
            if (map != null) {
                if (map.get("saleWindowFinishDATE") != null) {
                    Date finishTrancheDate = getDateParam(map.get("saleWindowFinishDATE"));
                    return finishTrancheDate;
                }
            }
        }
        return null;
    }

    protected Map<String, Object> getTermDataByTermID(Long termID, String login, String password) throws Exception {
        // получение сведений о сроке страхования
        Map<String, Object> termParams = new HashMap<String, Object>();
        termParams.put("TERMID", termID);
        termParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> termInfo = this.callServiceTimeLogged(B2BPOSWS_SERVICE_NAME, "dsB2BTermBrowseListByParam", termParams, login, password);
        return termInfo;
    }

    protected void getFinishDateByStartDateAndTermId(Map<String, Object> contract, String login, String password) throws Exception {
        Long termID = getLongParamLogged(contract, "TERMID");
        Map<String, Object> termInfo = getTermDataByTermID(termID, login, password);
        getStringParamLogged(termInfo, "NAME"); // для протокола
        Long termYearCount = getLongParamLogged(termInfo, "YEARCOUNT");
        Long termMonthCount = getLongParamLogged(termInfo, "MONTHCOUNT");
        Long termDayCount = getLongParamLogged(termInfo, "DAYCOUNT");
        if (contract.get("STARTDATE") != null) {
            Date startDate = (Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE", true);

            GregorianCalendar finishDateGC = new GregorianCalendar();
            finishDateGC.setTime(startDate);
            if (termYearCount != null) {
                finishDateGC.add(Calendar.YEAR, termYearCount.intValue());
            }
            if (termMonthCount != null) {
                finishDateGC.add(Calendar.MONTH, termMonthCount.intValue());
            }
            if (termDayCount != null) {
                finishDateGC.add(Calendar.DAY_OF_YEAR, termDayCount.intValue());
            }
            contract.put("FINISHDATE", finishDateGC.getTime());
        }
    }

    private Map<String, String> getDepartmentsTree(Long createUserDepartmentID, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("DEPARTMENTID", createUserDepartmentID);
        String territorialBank = "";
        String vsp = "";
        String osb = "";
        Map<String, Object> res = this.selectQuery("dsB2BGetDepartmentsTreeByDepartmentId", param);
        Map<String, String> result = new HashMap<>();
        if (res != null) {
            if (res.get(RESULT) != null) {
                List<Map<String, Object>> resList = (List<Map<String, Object>>) res.get(RESULT);
                if (!resList.isEmpty()) {
                    Map<String, Object> resMap = (Map<String, Object>) resList.get(0);
                    for (Entry<String, Object> map : resMap.entrySet()) {
                        String key = map.getKey();
                        String val = getStringParam(map.getValue());
                        if (key.contains("DEPTLEVELSYSNAME")) {
                            // ТБ - территориальный банк
                            if ("territorialBank".equals(val)) {
                                String num = key.substring("DEPTLEVELSYSNAME".length());
                                territorialBank = getStringParam(resMap.get("DEPTFULLNAME" + num));
                            }
                            // ГОСБ/ОСБ - головное отделение сбербанка
                            if ("headBranch".equals(val)){
                                String num = key.substring("DEPTLEVELSYSNAME".length());
                                osb = getStringParam(resMap.get("DEPTFULLNAME" + num));
                            }
                        }
                    }
                    // ВСП - внутреннее структурное подразделение. Всегда первый уровень, поскольку КМ непосредственно
                    // принадлежит ВСП - его и нужно отображать
                    vsp = getStringParam(resMap.get("DEPTFULLNAME1"));
                }
            }
        }
        result.put("VSP", vsp);
        result.put("OSB", osb);
        result.put("TB", territorialBank);
        return result;
    }

    private Map<String, String> getBrockerNamesMap(Long createUserDepartmentID, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("DEPARTMENTID", createUserDepartmentID);
        Map<String, Object> res = this.selectQuery("dsB2BBrockerMapBrowseByDepartmentId", param);
        Map<String, String> result = new HashMap<>();
        String partnerName = "";
        String territoryBank = "";
        String regionBank = "";
        String vsp = "";
        String osb = "";
        if (res != null) {
            if (res.get(RESULT) != null) {
                List<Map<String, Object>> resList = (List<Map<String, Object>>) res.get(RESULT);
                if (!resList.isEmpty()) {
                    Map<String, Object> resMap = (Map<String, Object>) resList.get(0);
                    for (Entry<String, Object> map : resMap.entrySet()) {
                        String key = map.getKey();
                        String val = getStringParam(map.getValue());
                        if (key.contains("DEPTLEVEL")) {
                            if ("4".equals(val)) {
                                //партнер
                                String num = key.substring("DEPTLEVEL".length());
                                partnerName = getStringParam(resMap.get("DEPTFULLNAME" + num));
                            }
                            if ("6".equals(val)) {
                                //территория
                                String num = key.substring("DEPTLEVEL".length());
                                territoryBank = getStringParam(resMap.get("DEPTFULLNAME" + num));
                            }
                            if ("7".equals(val)) {
                                //территория
                                String num = key.substring("DEPTLEVEL".length());
                                regionBank = getStringParam(resMap.get("DEPTFULLNAME" + num));
                            }
                        }
                    }
                    vsp = getStringParam(resMap.get("DEPTFULLNAME1"));
                    osb = getStringParam(resMap.get("DEPTFULLNAME2"));

                }
            }
        }
        result.put("VSP", vsp);
        result.put("OSB", osb);
        result.put("REGIONBANK", regionBank);
        result.put("TERRITORYBANK", territoryBank);
        result.put("PARTNERNAME", partnerName);
        return result;
    }

}
