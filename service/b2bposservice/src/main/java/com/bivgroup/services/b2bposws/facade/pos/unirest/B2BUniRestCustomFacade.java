package com.bivgroup.services.b2bposws.facade.pos.unirest;

import com.bivgroup.services.b2bposws.facade.pos.mappers.KeyToKeyRemapper;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * @author ilich
 */
public class B2BUniRestCustomFacade extends B2BUniRestBaseFacade {
    public static final String SYSNAME_VZR_CHEREHAPA = "B2B_TRAVEL_CHEREHAPA"; // Страхование ВЗР Черехапа
    public static final String SYSNAME_CIB = "00010"; // Защита банковской карты Онлайн
    public static final String SYSNAME_CIBY = "B2B_CIB_FOR_YOUTH"; // Защита банковской карты Онлайн для молодежи
    public static final String SYSNAME_HIB = "00009"; // Защита дома Онлайн
    public static final String SYSNAME_VZR = "00018"; // Страхование путешественников Онлайн (по ФТ от 06.10.2015)
    public static final String SYSNAME_MORTGAGE = "00029"; // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России»

    private static final Logger logger = Logger.getLogger(B2BUniRestCustomFacade.class);

    protected static final String contractRootKeys = "DATAMAP";
    protected static final String REGEX_SUM2 = "^[0-9]*+([.]{1}[0-9]{0,2})?$";

    private boolean isError = false;

    protected Map<String, Object> b2bContractRemap(Map<String, Object> params, String b2bProductSysName, Long requestId, Map<String, Object> product) throws Exception {
        return b2bContractRemap(params, b2bProductSysName, requestId, product, true);
    }

    protected Map<String, Object> b2bContractRemap(Map<String, Object> params, String b2bProductSysName, Long requestId, Map<String, Object> product, boolean isNeedCheck) throws Exception {
        isError = false;
        boolean isVerboseLog = true;
        Map<String, Object> result = new HashMap<>();
        logger.debug("begin!!! dsB2BContractSBOLSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> b2bContract = new HashMap<>();
        Map<String, Object> inputContract = (Map<String, Object>) chainedGetIgnoreCase(params, contractRootKeys);
        //String b2bProductSysName = getProductSysNameByParams(params);
        logger.debug("b2bProductSysName = " + b2bProductSysName);
        b2bContract.put("PRODSYSNAME", b2bProductSysName);
        b2bContract.put("REQUESTQUEUEID", params.get("REQUESTQUEUEID"));

        String programCode = getStringParam(chainedGetIgnoreCase(inputContract, "InsProgram"));

        //sbolContract.put("PRODCONF", product);
        inputContract.put("PRODCONF", product);
        // развертывание списков в карты вида 'имяСписка_системноеИмяЭлементаСписка'
        for (String[] anExpanded : contractExpandedLists) {
            String listName = anExpanded[0];
            String sysAttrName = anExpanded[1];
            expandListToMapBySysName(inputContract, listName, sysAttrName);
        }
        List<String[]> contractKeysRelations = new ArrayList<String[]>();
        // todo: возможно common20KeysRelations добавлять условно (по xmlTemplateID)?
        if (isNeedCheck) {
            contractKeysRelations.addAll(Arrays.asList(commonKeysRelations));
            contractKeysRelations.addAll(Arrays.asList(common20KeysRelations));

            String[][] productKeysRelations = PRODUCT_KEYS_RELATIONS.get(b2bProductSysName);
            if (productKeysRelations == null) {
                logger.error(String.format("Unable to get XML-to-contract keys relation rules for product with system name = %s!", b2bProductSysName));
                returnErrorAndStopSaving(result, "Не удалось получить сведения о соответствии полей XML B2B-структуре договора для данного продукта", requestId, login, password);
                return result;
            } else {
                contractKeysRelations.addAll(Arrays.asList(productKeysRelations));
            }
        } else {
            contractKeysRelations.addAll(Arrays.asList(commonCalc20KeysRelations));

            String[][] productKeysRelations = PRODUCT_KEYS_RELATIONS.get(b2bProductSysName + "_CALC");
            if (productKeysRelations == null) {
                logger.error(String.format("Unable to get XML-to-contract keys relation rules for product with system name = %s!", b2bProductSysName));
                returnErrorAndStopSaving(result, "Не удалось получить сведения о соответствии полей XML B2B-структуре договора для данного продукта", requestId, login, password);
                return result;
            } else {
                contractKeysRelations.addAll(Arrays.asList(productKeysRelations));
            }
        }

        int fromIndex = 0;
        int toIndex = 1;
        // копирование сведений в новую структуру
        for (String[] contractKeyRelation : contractKeysRelations) {
            String newKey = contractKeyRelation[fromIndex];
            String oldKey = contractKeyRelation[toIndex];
            Boolean isCreativePut = true;
            String convertRulesStr = null;
            if ((contractKeyRelation.length > 3) && (!contractKeyRelation[3].isEmpty())) {
                convertRulesStr = contractKeyRelation[3];
            }
            Object rawValue = chainedGetIgnoreCase(inputContract, oldKey);
            //if (rawValue == null) {
            //    oldKey = getKeysChainWithLastKeyUpperCase(oldKey);
            //    rawValue = chainedGet(rawParams, oldKey);
            //}
            Object value = null;
            if (rawValue instanceof List) {
                List listValue = (List) rawValue;
                value = listValue.subList(listValue.size() - 1, listValue.size());
            } else {
                value = rawValue;
            }
            if (value != null) {
                if (convertRulesStr != null) {
                    value = convertValue(value, convertRulesStr);
                }

                if (isVerboseLog) {
                    logger.debug("");
                    logger.debug("Source key: " + oldKey);
                    logger.debug("Target key: " + newKey);
                }
                chainedCreativePut(b2bContract, newKey, value, isCreativePut);
                if (isVerboseLog) {
                    logger.debug("Setted value: " + value + ((convertRulesStr == null) ? "" : " (converted from '" + rawValue + "' by using rule '" + convertRulesStr + "')"));
                    //chainedCreativePut(rawParamsCopyForLog, oldKey, "'ЗНАЧЕНИЕ ПЕРЕНЕСЕНО'");
                }
            }
        }

        //genAdditionalSaveParams(result, b2bContract, product, xmlProductCode, requestId, login, password);
        genAdditionalSaveParams(result, b2bContract, product, b2bProductSysName, requestId, login, password);

        // установка значний по-умолчанию
        for (String[] contractKeyRelation : contractKeysRelations) {
            String newKey = contractKeyRelation[fromIndex];
            Object newKeyValue = chainedGet(b2bContract, newKey);
            Boolean isCreativePut = true;
            String defaultValue = "";
            if ((contractKeyRelation.length > 2) && (!contractKeyRelation[2].isEmpty())) {
                defaultValue = contractKeyRelation[2];
            }
            if ((newKeyValue == null) && (!defaultValue.isEmpty())) {
                if (isVerboseLog) {
                    logger.debug("");
                    logger.debug("Target key: " + newKey);
                    logger.debug("Setted default value: " + defaultValue);
                }
                chainedCreativePut(b2bContract, newKey, defaultValue, isCreativePut);
            }
        }

        // свертывание карт вида 'имяСписка.системноеИмяЭлементаСписка' в списки
        String[][] collapsed = contractCollapsedMaps;
        for (String[] aCollapsed : collapsed) {
            String listName = aCollapsed[0];
            String sysAttrName = aCollapsed[1];
            collapseMapToListBySysName(b2bContract, listName, sysAttrName);
        }

        // свертывание карт вида 'имяСписка.системноеИмяЭлементаСписка' в списки для возрата продукта в начальное состояние 
        collapsed = contractExpandedLists;
        for (String[] aCollapsed : collapsed) {
            String listName = aCollapsed[0];
            String sysAttrName = aCollapsed[1];
            collapseMapToListBySysName(inputContract, listName, sysAttrName);
        }
        // вычисление даты окончания действия договора для ВЗР
        //if (xmlProductCode.equals(PRODUCT_CODE_SBOL_TRAVEL)) {
        // создание объектов и рисков с копированием сумм из справочника в зависимости от программы
        //createObjectsAndRisksByHB(result, b2bContract, product, programCode, requestId, xmlProductCode, login, password);
        createObjectsAndRisksByHB(result, b2bContract, product, programCode, requestId, b2bProductSysName, login, password);
        if (isError) {
            // при ошибке выходим и возвращаем результат. сохранять дальше не нужно.
            return result;
        }

        // применить правило расчета курса оплаты премии (если задано)
        applyCalcRateRule(b2bContract, product, login, password);

        if (isDebugCodeActive) {
            //!только для отладки!
            b2bContract.put("NOTE", "СБОЛ - проверка сохранения (" + (new Date()).toString() + ")");
        }

        // проверка параметров договора СБОЛ
        //String checkRes = checkB2BContractSBOLParams(xmlProductCode, b2bContract);
        if (isNeedCheck) {
            String checkRes = checkB2BContractSBOLParams(b2bContract, b2bProductSysName);
            if ((checkRes != null) && (!checkRes.isEmpty())) {
                updateRequest(requestId, 1, login, password);
                result.put("Error", checkRes);
                logger.debug("end!!! dsB2BContractSBOLSave");
                return result;
            }
        }
        return b2bContract;
    }

    protected Map<String, Object> b2bContractRemapRest2(Map<String, Object> params, String b2bProductSysName, Map<String, Object> product) throws Exception {
        Map<String, Object> inputContract = (Map<String, Object>) chainedGetIgnoreCase(params, contractRootKeys);
        List<String[]> productKeyRelations = Arrays.asList(PRODUCT_KEYS_RELATIONS.get(b2bProductSysName));
        KeyToKeyRemapper remapper = new KeyToKeyRemapper(productKeyRelations, new ArrayList<String[]>(), new ArrayList<String[]>());
        Map<String, Object> result = remapper.remap(inputContract);
        result.put("PRODSYSNAME", b2bProductSysName);
        result.put("PRODCONF", product);
        return result;
    }

    @WsMethod(requiredParams = {"WS_AUTH_REQUESTQUEUEID", "TEMPLATEID", "DATAMAP"})
    public Map<String, Object> dsB2BContractRemapAndSave(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = new HashMap<>();
        String b2bProductSysName = getProductSysNameByParams(params);

        Map<String, Object> product = null;
        Map<String, Object> b2bProductParams = new HashMap<>();
        Long requestId = getLongParam(params.get("WS_AUTH_REQUESTQUEUEID"));

        if (params.get("REQUESTQUEUEID") == null) {
            params.put("REQUESTQUEUEID", params.get("WS_AUTH_REQUESTQUEUEID"));
        }
        b2bProductParams.put("PRODSYSNAME", b2bProductSysName);
        b2bProductParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> productResult = this.callService(Constants.B2BPOSWS, "dsB2BProductBrowseBySysName", b2bProductParams, login, password);
        if (productResult != null) {
            product = getMapParam(productResult, "PRODMAP");
        }
        if (product == null) {
            logger.error(String.format("Unable to get product info for product with system name = %s!", b2bProductSysName));
            returnErrorAndStopSaving(result, "Не удалось получить сведения о продукте", requestId, login, password);
            return result;
        }
        Long b2bProdConfID = getLongParamLogged(product, "PRODCONFID");

        // ремапинг рест договора в структуру б2б
        Map<String, Object> b2bContract = b2bContractRemap(params, b2bProductSysName, requestId, product);
        // определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения
        // todo: возможно, заменить на использование updateSessionParamsIfNullByCallingUserCreds в dsB2BContrSave
        Map<String, Object> saveResult = new HashMap<>();
        if (b2bContract.get(ERROR) == null) {
            Map<String, Object> saveParams = new HashMap<>();
            Map<String, Object> checkLoginParams = new HashMap<>();
            checkLoginParams.put("username", XMLUtil.getUserName(login));
            checkLoginParams.put("passwordSha", password);
            Map<String, Object> checkLoginResult = WsUtils.getFirstItemFromResultMap(this.selectQuery("dsCheckLogin", checkLoginParams));
            if (checkLoginResult != null) {
                saveParams.put(Constants.SESSIONPARAM_USERACCOUNTID, checkLoginResult.get("USERACCOUNTID"));
                saveParams.put(Constants.SESSIONPARAM_DEPARTMENTID, checkLoginResult.get("DEPARTMENTID"));
            }
            saveParams.putAll(b2bContract);
            saveParams.put("PRODSYSNAME", b2bProductSysName);
            saveParams.put(RETURN_AS_HASH_MAP, true);
            saveResult = this.callService(Constants.B2BPOSWS, "dsB2BContrSave", saveParams, login, password);
            // дата оформления - будет использована для создания факта и плана оплаты (если таковые требуются)
            Date docDate = null;
            Object docDateObj = saveResult.get("DOCUMENTDATE");
            if (docDateObj != null) {
                docDate = (Date) parseAnyDate(docDateObj, Date.class, "DOCUMENTDATE", true);
            }

            if (!PRODUCTS_NO_PAY_PLAN.contains(b2bProductSysName)) {
                // создать плановый платеж
                Map<String, Object> payParams = new HashMap<String, Object>();
                payParams.put("AMOUNT", saveResult.get("PREMVALUE"));
                payParams.put("CONTRID", saveResult.get("CONTRID"));
                if (docDate != null) {
                    payParams.put("PAYDATE", docDate);
                }
                Map<String, Object> payRes = this.callService(Constants.B2BPOSWS, "dsB2BPaymentCreate", payParams, login, password);
            }

            if (isDebugCodeActive) {
                //!только для отладки!
                chainedCreativePut(saveResult, "INSURERMAP.extAttributeList", "=== Удалено для выполнения отладки ===");
                chainedCreativePut(saveResult, "INSURERMAP.extAttributeList2", "=== Удалено для выполнения отладки ===");
            }
        }
        Map<String, Object> loadResult = null;
        if (saveResult.get("CONTRID") != null) {
            long savedContractID = getLongParam(saveResult.get("CONTRID"));
            //успех
            updateRequest(requestId, 0, login, password);
            result.putAll(saveResult);
            //result.put("PRODCONFID", sbolProdConfID); // идентификатор продукта для печати документов, используется для печати+отправки в dsSendDocumentsPackage и etc
            result.put("PRODCONFID", b2bProdConfID); // идентификатор продукта для печати документов, используется для печати+отправки в dsSendDocumentsPackage и etc

            if (isDebugCodeActive) {
                //!только для отладки!
                Map<String, Object> loadParams = new HashMap<String, Object>();
                loadParams.put("CONTRID", savedContractID);
                loadParams.put(RETURN_AS_HASH_MAP, true);
                loadResult = this.callService(Constants.B2BPOSWS, "dsB2BContrLoad", loadParams, login, password);
                chainedCreativePut(loadResult, "INSURERMAP.extAttributeList", "=== Удалено для выполнения отладки ===");
                chainedCreativePut(loadResult, "INSURERMAP.extAttributeList2", "=== Удалено для выполнения отладки ===");
            }
        } else {
            //неуспех
            updateRequest(requestId, 1, login, password);
            //result = new HashMap<String, Object>();
            result.put("Error", "Не удалось сохранить договор.");
            //todo: вернуть причины по которым договор не был сохранен
            //result.put("Reason", "...");
        }

        if (isDebugCodeActive) {
            //!только для отладки!
            //result = new HashMap<String, Object>();
            //result.put("b2bContract", b2bContract);
            //result.put("saveResult", saveResult);
            //result.put("loadResult", loadResult);
            logger.debug("b2bContract:\n\n" + b2bContract + "\n");
            logger.debug("saveResult:\n\n" + saveResult + "\n");
            logger.debug("loadResult:\n\n" + loadResult + "\n");
        }
        logger.debug("end!!! dsB2BContractRemapAndSave");
        return result;

    }

    @WsMethod(requiredParams = {"DATAMAP", "PRODSYSNAME"})
    public Map<String, Object> dsB2BValidateCalcObjectDataByProductSysName(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        String prodSysName = getStringParam(params, "PRODSYSNAME");
        Map<String, Object> dataMap = (Map<String, Object>) params.get("DATAMAP");

        String validationResult = validateCalcObjectDataByProductSysName(prodSysName, dataMap, login, password);

        Map<String, Object> result = new HashMap<String, Object>();

        if (validationResult != null && !validationResult.isEmpty()) {
            result.put("VALIDATIONERROR", validationResult);
        }

        return result;
    }

    private String validateLoadObjectDataByProductSysName(String productSysName, Map<String, Object> dataMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        params.put("DATAMAP", dataMap);

        Map<String, Object> b2bProductParams = new HashMap<>();
        b2bProductParams.put("PRODSYSNAME", productSysName);
        b2bProductParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> productResult = this.callService(Constants.B2BPOSWS, "dsB2BProductVersionBrowseListByParamEx", b2bProductParams, login, password);
        Long prodConfId = getLongParam(productResult, "PRODCONFID");
        Map<String, Object> productDefValueMap = getProductDefaultValueByProdConfId(prodConfId, login, password);
        String methodName = "";
        methodName = getStringProdDefValueFromDefValuesMap(productDefValueMap, "UniRestValidationLoadDataMapMethodName", "defaultUniRestLoadDataMapValidation");

        if (!methodName.isEmpty()) {
            Map<String, Object> qRes = this.callService(Constants.B2BPOSWS, methodName, params, login, password);
            if ((qRes != null) && (qRes.get("VALIDATIONERROR") != null)) {
                return qRes.get("VALIDATIONERROR").toString();
            }
        }
        return "";
    }

    private String validateCalcObjectDataByProductSysName(String productSysName, Map<String, Object> dataMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        params.put("DATAMAP", dataMap);
        String methodName = "";
        if (productSysName.equalsIgnoreCase(SYSNAME_VZR_CHEREHAPA)) {
            methodName = "dsB2BTravelCherehapaCalcDataMapValidate";
        } else if (productSysName.equalsIgnoreCase(SYSNAME_CIB)) {
            methodName = "dsB2BRest2CibCalcDataMapValidate";
        } else if (productSysName.equalsIgnoreCase(SYSNAME_HIB)) {
            methodName = "dsB2BRest2HibValidate";
        } else if (productSysName.equalsIgnoreCase(SYSNAME_VZR)) {
            methodName = "dsB2BRest2VzrValidate";
        } else if (productSysName.equalsIgnoreCase(SYSNAME_MORTGAGE)) {
            methodName = "dsB2BRest2ValidateMortgage";
        } else if (productSysName.equalsIgnoreCase(SYSNAME_ANTIMITE)) {
            methodName = "dsB2BRest2AntiMiteCalcDataMapValidate";
            // no need to validate Antimite
            return "";
        } else if (productSysName.equalsIgnoreCase(SYSNAME_CIBY)) {
            methodName = "dsB2BRest2CibYouthCalcDataMapValidate";
        }
        if (!methodName.isEmpty()) {
            Map<String, Object> qRes = this.callService(Constants.B2BPOSWS, methodName, params, login, password);
            if ((qRes != null) && (qRes.get("VALIDATIONERROR") != null)) {
                return qRes.get("VALIDATIONERROR").toString();
            }
        }
        return "";
    }

    @WsMethod(requiredParams = {"DATAMAP"})
    public Map<String, Object> dsB2BUniRestLoadObject(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BUniRestLoadObject begin");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        String productSysName = getProductSysNameByParams(params);
        String validateLoadObjectRes = validateLoadObjectDataByProductSysName(productSysName,
                (Map<String, Object>) params.get("DATAMAP"), login, password);
        if ((validateLoadObjectRes == null) || (validateLoadObjectRes.isEmpty())) {
            result = this.callService(Constants.B2BPOSWS, "dsB2BContractRemapAndSave", params, login, password);
        } else {
            result.put("VALIDATIONERROR", validateLoadObjectRes);
        }
        logger.debug("dsB2BUniRestLoadObject end");
        return result;
    }

    private Map<String, Object> calcPremiumByProductSysName(String productSysName, Map<String, Object> contrMap,
                                                            String login, String password) throws Exception {
        Map<String, Object> b2bProductParams = new HashMap<>();
        b2bProductParams.put("PRODSYSNAME", productSysName);
        b2bProductParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> productResult = this.callService(Constants.B2BPOSWS, "dsB2BProductVersionBrowseListByParamEx", b2bProductParams, login, password);
        Long prodConfId = getLongParam(productResult, "PRODCONFID");
        Map<String, Object> productDefValueMap = getProductDefaultValueByProdConfId(prodConfId, login, password);
        String methodName = getStringProdDefValueFromDefValuesMap(productDefValueMap, "UniRestCalculationMethodName");

        Map<String, Object> params = new HashMap<>();
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        params.put("CONTRMAP", contrMap);
        Map<String, Object> result = new HashMap<>();
        if (!methodName.isEmpty()) {
            result.putAll(this.callService(Constants.B2BPOSWS, methodName, params, login, password));
        }
        return result;
    }

    @WsMethod(requiredParams = {"TEMPLATEID", "DATAMAP"})
    public Map<String, Object> dsB2BUniRestCalcObject(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BUniRestCalcObject begin");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = new HashMap<>();
        // получаем сиснейм продукта по шаблону и подшаблону
        String b2bProductSysName = getProductSysNameByParams(params);

        Map<String, Object> dataMap = getMapParam(params, "DATAMAP");
        String validateLoadObjectRes = validateCalcObjectDataByProductSysName(b2bProductSysName,
                dataMap, login, password);
        boolean validationPassed = (validateLoadObjectRes == null) || validateLoadObjectRes.isEmpty();
        if (validationPassed) {
            // производим конвертацию datamap в мапу договора общим сервисом конвертации
            Map<String, Object> product = null;
            Map<String, Object> b2bProductParams = new HashMap<>();
            Long requestId = getLongParam(params.get("WS_AUTH_REQUESTQUEUEID"));

            if (params.get("REQUESTQUEUEID") == null) {
                if (params.get("REQUESTID") != null) {
                    params.put("REQUESTQUEUEID", params.get("WS_AUTH_REQUESTQUEUEID"));
                }
            }
            b2bProductParams.put("PRODSYSNAME", b2bProductSysName);
            b2bProductParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> productResult = this.callService(Constants.B2BPOSWS, "dsB2BProductBrowseBySysName", b2bProductParams, login, password);
            if (productResult != null) {
                product = getMapParam(productResult, "PRODMAP");
            }
            if (product == null) {
                logger.error(String.format("Unable to get product info for product with system name = %s!", b2bProductSysName));
                returnErrorAndStopSaving(result, "Не удалось получить сведения о продукте", requestId, login, password);
                return result;
            }
            Long b2bProdConfID = getLongParamLogged(product, "PRODCONFID");

            //
            Map<String, Object> convertRes;
            // FIXME: Переделать на вызов сервиса, получение имени которого будет происходить из PRODDEFVAL
            if (b2bProductSysName.equalsIgnoreCase(PRODUCT_SYSNAME_VZR_CHEREHAPA)) {
                convertRes = b2bContractRemap(params, b2bProductSysName, requestId, product, false);
            } else {
                convertRes = b2bContractRemapRest2(params, b2bProductSysName, product);
                // оригинальный датамап. для взр
                convertRes.put("DATAMAP", dataMap);
            }
            //Map<String, Object> convertRes = this.callService(Constants.B2BPOSWS, "CONVERT", convertParams, login, password);
            if (convertRes != null) {
                // тут возможно сама мапа договора будет лежать в каком то ключе convertRes
                Map<String, Object> contrMap = convertRes;
                // выполняем расчет в зависимости от продукта
                Map<String, Object> calcRes = calcPremiumByProductSysName(b2bProductSysName, contrMap, login, password);
                if (isCallResultNotOK(calcRes) || calcRes.containsKey(ERROR)) {
                    // гарантировано обрабатываем ошибку
                    logger.error("calcPremiumByProductSysName returned result with error:\n" + calcRes);
                    throw new Exception(getStringParam(calcRes, ERROR));
                }
                Double premValue = roundSum(getDoubleParam(calcRes.get("PREMVALUE")));
                Double currencyRate = roundCurrencyRate(getDoubleParam(calcRes.get("CURRENCYRATE")));
                Map<String, Object> currencyMap = getCurrencyById(getLongParam(calcRes.get("PREMCURRENCYID")), login, password);
                result.put("currency", getLongParam(currencyMap.get("ISONumber")));
                result.put("currencyRate", currencyRate);
                result.put("total", premValue);
                result.put("totalRUR", roundSum(premValue * currencyRate));
                if (calcRes.get("DATAMAP") != null) {
                    result.put("data", calcRes.get("DATAMAP"));
                }
            }
        } else {
            result.put("VALIDATIONERROR", validateLoadObjectRes);
        }
        //
        logger.debug("dsB2BUniRestCalcObject end");
        return result;
    }

    private Map<String, Object> getContractPaymentFact(Long contrNodeId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RETURN_AS_HASH_MAP, true);
        params.put("CONTRNODEID", contrNodeId);
        Map<String, Object> qRes = this.callService(Constants.B2BPOSWS, "dsB2BPaymentFactBrowseListByParam", params, login, password);
        if ((qRes != null) && (qRes.get("PAYFACTID") != null)) {
            return qRes;
        } else {
            return null;
        }
    }

    private boolean checkPaymentSum(Object premValue, Object premCurrencyId,
                                    Object currencyRate, Object paymentAmRUB) {
        if (premCurrencyId == null) {
            premCurrencyId = 1L;
        }
        if (currencyRate == null) {
            currencyRate = 1L;
        }
        Double premValueD = roundSum(Double.valueOf(premValue.toString()));
        Long premCurrencyIdL = Long.valueOf(premCurrencyId.toString());
        Double currencyRateD = roundCurrencyRate(Double.valueOf(currencyRate.toString()));
        Double paymentAmRUBD = Double.valueOf(paymentAmRUB.toString());
        if (premCurrencyIdL.longValue() == 1) {
            return Math.abs(premValueD.doubleValue() - paymentAmRUBD.doubleValue()) < 0.00001;
        } else {
            return Math.abs(roundSum(premValueD.doubleValue() * currencyRateD.doubleValue()) - paymentAmRUBD.doubleValue()) < 0.00001;
        }
    }

    @WsMethod(requiredParams = {"DATAMAP"})
    public Map<String, Object> dsB2BUniRestPayObject(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BUniRestPayObject begin");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        params = (Map<String, Object>) params.get("DATAMAP");
        Map<String, Object> result = new HashMap<String, Object>();
        String decodedContractId = getStringParam(params, "contractID");
        if ((decodedContractId == null) || (decodedContractId.isEmpty())) {
            result.put("VALIDATIONERROR", "Не заполнен идентификатор договора");
            return result;
        }
        if (getDoubleParam(params, "payAmount") < 0.0001) {
            result.put("VALIDATIONERROR", "Не заполнена сумма оплаты");
            return result;
        }
        if (!checkIsValueValidByRegExp(params.get("payAmount").toString(), REGEX_SUM2, false)) {
            result.put("VALIDATIONERROR", "Указано недопустимое значение суммы оплаты, более 2-х знаков в дробной части");
            return result;
        }
        if (getDateParam(params.get("payDate")) == null) {
            result.put("VALIDATIONERROR", "Не заполнена дата оплаты");
            return result;
        }
        String[] splittedContractId = decodedContractId.split(";");
        if (splittedContractId.length > 2) {
            String contrIdStr = splittedContractId[1];
            Long contractId = getLongParam(contrIdStr);

            // необходимо раскодировать ИД договора
            // читаем и обрабатываем договор
            Map<String, Object> browseParams = new HashMap<String, Object>();
            browseParams.put(RETURN_AS_HASH_MAP, "TRUE");
            browseParams.put("CONTRID", contractId);
            Map<String, Object> contractMap = this.callService(Constants.B2BPOSWS, "dsB2BContractBrowseListByParamExShort", browseParams, login, password);
            if ((contractMap != null) && (contractMap.get("STATESYSNAME") != null)) {
                String contractState = getStringParam(contractMap, "STATESYSNAME");
                if ((!contractState.equalsIgnoreCase("B2B_CONTRACT_SG"))
                        && (!contractState.equalsIgnoreCase("B2B_CONTRACT_UPLOADED_SUCCESFULLY"))) {
                    if (getDateParam(params.get("payDate")).getTime() <
                            ((Date) parseAnyDate(contractMap.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE")).getTime()) {
                        result.put("VALIDATIONERROR", "Указано недопустимое значение даты оплаты, ранее даты оформления договора");
                        return result;
                    }
                    if (getDateParam(params.get("payDate")).getTime() >
                            ((Date) parseAnyDate(contractMap.get("STARTDATE"), Date.class, "STARTDATE")).getTime()) {
                        result.put("VALIDATIONERROR", "Указано недопустимое значение даты оплаты, позднее даты начала договора");
                        return result;
                    }
                    Map<String, Object> curPayFactMap = getContractPaymentFact(getLongParam(contractMap, "CONTRNODEID"), login, password);
                    // если факт оплаты по данному договору не обнаружен, тогда производим оплату.
                    if (curPayFactMap == null) {
                        Double payAmount = getDoubleParam(params, "payAmount");
                        Double payDate = (Double) parseAnyDate(params.get("payDate"), Double.class, "payDate");
                        // проверяем сумму платежа
                        if (contractMap.get("PREMVALUE") != null) {
                            if (checkPaymentSum(contractMap.get("PREMVALUE"), contractMap.get("PREMCURRENCYID"),
                                    contractMap.get("CURRENCYRATE"), payAmount)) {
                                //создать фактический платеж
                                Map<String, Object> payFactParams = new HashMap<String, Object>();
                                payFactParams.put(RETURN_AS_HASH_MAP, "TRUE");
                                payFactParams.put("CONTRNODEID", contractMap.get("CONTRNODEID"));
                                payFactParams.put("AMCURRENCYID", contractMap.get("PREMCURRENCYID"));
                                payFactParams.put("AMVALUE", contractMap.get("PREMVALUE"));
                                payFactParams.put("AMVALUERUB", payAmount);
                                payFactParams.put("NAME", "UNIREST");
                                payFactParams.put("PAYFACTNUMBER", contractMap.get("CONTRNODEID"));
                                payFactParams.put("PAYFACTTYPE", 2);
                                payFactParams.put("PAYFACTDATE", payDate);
                                XMLUtil.convertDateToFloat(payFactParams);
                                Map<String, Object> payFactRes = this.callService(Constants.B2BPOSWS, "dsB2BPaymentFactCreate", payFactParams, login, password);
                                if ((payFactRes == null) || (payFactRes.get("PAYFACTID") == null)) {
                                    result.put("Error", "Не удалось создать фактический платеж.");
                                } else {
                                    result.put("PAYFACTID", payFactRes.get("PAYFACTID"));
                                    // создать плановый платеж
                                    Map<String, Object> payParams = new HashMap<String, Object>();
                                    payParams.put(RETURN_AS_HASH_MAP, "TRUE");
                                    payParams.put("AMOUNT", payAmount);
                                    payParams.put("CONTRID", contractId);
                                    payParams.put("PAYDATE", payDate);
                                    XMLUtil.convertDateToFloat(payParams);
                                    Map<String, Object> payRes = this.callService(Constants.B2BPOSWS, "dsB2BPaymentCreate", payParams, login, password);
                                    if ((payRes == null) || (payRes.get("PAYID") == null)) {
                                        result.put("Error", "Не удалось создать плановый платеж.");
                                    } else {
                                        Map<String, Object> contractUpdParams = new HashMap<String, Object>();
                                        contractUpdParams.put("CONTRID", contractMap.get("CONTRID"));
                                        contractUpdParams.put("SIGNDATE", payDate);
                                        this.callService(Constants.B2BPOSWS, "dsB2BContractUpdate", contractUpdParams, login, password);
                                    }
                                }
                            } else {
                                result.put("VALIDATIONERROR", "Указанная сумма оплаты не соответствует премии по договору");
                            }
                        } else {
                            result.put("Error", "Ошибка проверки соответствия суммы платежа.");
                        }
                    } else {
                        result.put("Error", "По договору существует платеж, повторная оплата невозможна.");
                    }
                } else {
                    result.put("Error", "Договор находится в статусе отличном от «Черновик» или «Предварительная печать», оплата невозможна.");
                }
            } else {
                result.put("VALIDATIONERROR", "По указанному идентификатору договора не определен объект");
            }
        } else {
            result.put("VALIDATIONERROR", "Указано недопустимое значение идентификатора договора");
        }
        //
        logger.debug("dsB2BUniRestPayObject end");
        return result;
    }
}
