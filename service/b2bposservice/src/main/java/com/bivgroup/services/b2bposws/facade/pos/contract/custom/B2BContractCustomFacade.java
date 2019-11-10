/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.contract.custom;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.diasoft.fa.commons.sm.StateMachineManagerNew;
import ru.diasoft.services.inscore.aspect.impl.ownerright.OwnerRightView;
import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.aspect.impl.orgstruct.OrgStruct;
import ru.diasoft.services.inscore.aspect.impl.version.NodeVersion;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsConstants;

import static com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade.*;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade.*;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * @author ilich
 */
@ProfileRights({
        @PRight(sysName = "RPAccessPOS_Branch",
                name = "Доступ по подразделению",
                joinStr = "  inner join B2B_CONTRORGSTRUCT COS on COS.OBJLEVEL is null and t.CONTRID = COS.CONTRID and (COS.ISBLOCKED != 1 or COS.ISBLOCKED is null) inner join INS_DEPLVL DEPLVL on (COS.ORGSTRUCTID = DEPLVL.OBJECTID) ",
                restrictionFieldName = "DEPLVL.PARENTID",
                paramName = "DEPARTMENTID")})
@OwnerRightView(accessByUserRole = true,
        joinStr = " left join B2B_CONTRORGSTRUCT ORGSTR on (T.CONTRID = ORGSTR.CONTRID) ")
@NodeVersion(nodeTableName = "B2B_CONTRNODE", nodeTableIdFieldName = "CONTRNODEID", versionNumberParamName = "VERNUMBER", nodeLastVersionNumberFieldName = "LASTVERNUMBER", nodeRVersionFieldName = "RVERSION")
@OrgStruct(tableName = "B2B_CONTRORGSTRUCT", orgStructPKFieldName = "CONTRORGSTRUCTID", objTablePKFieldName = "CONTRID")
@BOName("B2BContractCustom")
public class B2BContractCustomFacade extends B2BLifeBaseFacade {


    /**
     * Список частей названий ключей, указывающих на участников из договора
     * (полные названия: ... + MAP = данные участника; ... + ID = идентификатор
     * участника в договоре)
     */
    public static final String[] participantNodes = {
            "INSURER", // страхователь
            "INSURERREP", // представитель страхователя
    };
    public static final String SYSNAME_BORROWER_PROTECT = ProductContractCustomFacade.SYSNAME_BORROWER_PROTECT;
    private static final String B2BPOSWS = Constants.B2BPOSWS;
    private static final String INSTARIFICATORWS = Constants.INSTARIFICATORWS;
    private static final String B2BCORRECTOR_ROLE_SYSNAME = "b2bCorrector";
    /**
     * Переходы состояний, которые может выполнять только пользователь с ролью
     * MAN
     */
//    private static final Map<String, String> ONLY_MAN_ALLOWED_STATE_MAP = new HashMap<String, String>() {
//        {
//            put("B2B_CONTRACT_CANCEL", "B2B_CONTRACT_UW");
//            put("B2B_CONTRACT_PREPARE", "B2B_CONTRACT_PREPRINTING");
//            put("B2B_CONTRACT_UW", "Аннулирован");
//            put("B2B_CONTRACT_CANCEL", "Аннулирован");
//            put("B2B_CONTRACT_SG", "Аннулирован");
//        }
//    };

    private static final List<Map<String, String>> ONLY_MAN_ALLOWED_TRANS_LIST = new ArrayList<Map<String, String>>() {
        {
            add(new HashMap<String, String>() {
                {
                    put("B2B_CONTRACT_CANCEL", "B2B_CONTRACT_UW");
                }
            });
            add(new HashMap<String, String>() {
                {
                    put("B2B_CONTRACT_PREPARE", "B2B_CONTRACT_PREPRINTING");
                }
            });
            add(new HashMap<String, String>() {
                {
                    put("B2B_CONTRACT_UW", "B2B_CONTRACT_REPEAL");
                }
            });
            add(new HashMap<String, String>() {
                {
                    put("B2B_CONTRACT_CANCEL", "B2B_CONTRACT_REPEAL");
                }
            });
            add(new HashMap<String, String>() {
                {
                    put("B2B_CONTRACT_SG", "B2B_CONTRACT_REPEAL");
                }
            });
        }
    };
    private static final String BENEFICIARY_LIST_KEY_NAME = "BENEFICIARYLIST";
    // Типы выгодоприобретателей
    // 'Застрахованный' (когда CONTREXTMAP.insurerIsInsured == 0) или 'Страхователь (совпадает с ЗЛ)' (когда CONTREXTMAP.insurerIsInsured == 1)
    private static final Long BENEFICIARY_INSURED_TYPEID = 1L;
    // 'По закону'
    private static final Long BENEFICIARY_BY_LAW_TYPEID = 2L;
    // 'Новый'
    private static final Long BENEFICIARY_NORMAL_TYPEID = 3L;
    // 'Страхователь' (когда CONTREXTMAP.insurerIsInsured == 0)
    private static final Long BENEFICIARY_INSURER_TYPEID = 4L;
    // флаг подробного протоколирования операций с выгодоприобретателями
    // (рекомендуется отключить после завершения отладки и проверки)
    private static final boolean IS_BEN_CALLS_VERBOSE_LOGGING = true;
    protected static Map<String, Long> orgStructIDsByName = new HashMap<String, Long>();
    private static Map<String, List<Map<String, Object>>> hbCache;
    private static Map<String, Map<Long, Map<String, Object>>> hbMapsByHidCache;
    private static Map<String, Map<String, Object>> productCache;
    private static String CONTRACTSECTION_EMULATE = "__CONTRSECTION_EMULATE__";
    private final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    private StateMachineManagerNew sm = null;

    // получение из списка последнего элемента у которого в attrName храниться значение attrValue
    public static Object getLastElementByAtrrValue(List<Map<String, Object>> list, String attrName, String attrValue) {
        for (int i = list.size() - 1; i >= 0; i--) {
            Map element = list.get(i);
            if (attrValue.equalsIgnoreCase(element.get(attrName).toString())) {
                return element;
            }
        }
        return null;
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

    private Map<String, Object> contractMakeTrans(Map<String, Object> exportData, String toStateSysName, String login, String password) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String idFieldName = "CONTRID";
        String methodNamePrefix = "dsB2BContract";
        String typeSysName = "B2B_CONTRACT";
        // проверка
        String fromStateSysname = getStringParam(exportData.get("STATESYSNAME"));
        // если происходит переход из статуса "Черновик" в статус "Предв. печать", необходимо генерировать номер договора
        if (fromStateSysname.equals("B2B_CONTRACT_DRAFT") && toStateSysName.equals("B2B_CONTRACT_PREPRINTING")) {
            // договор
            Map<String, Object> contract = getMapParam(exportData, "CONTRMAP");
            // сис. наименование продукта
            String prodSysName = getStringParamLogged(contract, PRODUCT_SYSNAME_PARAMNAME);

            if (SYSNAME_BORROWER_PROTECT_LONGTERM.equals(prodSysName)) {
                // продукт "Защищенный заемщик многолетний" - упрощенный вариант генерации номера договора
                // todo: исправить, когда/если будет найдено более адекватное решение по проблеме перевода состояния договора
                Map<String, Object> contractParams = new HashMap<>(contract);
                // todo: если оставлять текущее решение, то сделать универсальный вызов метода с именем PREPARETOSAVEMETHOD.replace("PrepareToSave", "GenerateNumber") или т.п.
                String generateNumberMethodName = "dsB2BBorrowerProtectLongTermContractGenerateNumber";
                Map<String, Object> contractWithNumber = callServiceLogged(B2BPOSWS, generateNumberMethodName, contractParams, login, password);
                copyParamsIfNotNull(contract, contractWithNumber, "CONTRPOLSER", "CONTRPOLNUM");
            } else {
                // иначе - полный "проблемный" вариант генерации номера договора
                if (contract.get("PRODVERID") != null) {
                    Long prodVerId = getLongParam(contract.get("PRODVERID"));
                    if (prodVerId != null) {
                        // Получим настройки продукта
                        Map<String, Object> prodConfMap = getProductConfigByProdVerId(prodVerId, login, password);
                        // Вызовем метод подготовки сохранения тек. продукта для генерации нормальной серии и номера для договра,
                        String prepareToSaveMethodName = getStringParam(prodConfMap.get("PREPARETOSAVEMETHOD"));
                        // на СБСЖ не все продукты, видимо, имеют метод *PrepareToSave, поэтому следует добавить доп. проверку на его наличие чтоб не возникало исключения
                        if (!prepareToSaveMethodName.isEmpty()) {
                            Map<String, Object> prepareToSaveMethodParam = new HashMap<>();
                            prepareToSaveMethodParam.put(IS_GEN_SER_DRAFT_PARAM_NAME, IS_GEN_SER_DRAFT_VALUE);
                            prepareToSaveMethodParam.putAll(contract);
                            prepareToSaveMethodParam.put(RETURN_AS_HASH_MAP, true);
                            // Обновим договор
                            contract = callService(B2BPOSWS, prepareToSaveMethodName, prepareToSaveMethodParam, login, password);
                        }
                    }
                }
            }

            Map<String, Object> updateMap = new HashMap<>();
            String series = getStringParam(contract.get("CONTRPOLSER"));
            String number = getStringParam(contract.get("CONTRPOLNUM"));
            updateMap.put("CONTRPOLSER", series);
            updateMap.put("CONTRPOLNUM", number);
            updateMap.put("CONTRNUMBER", series + number);
            updateMap.put("CONTRID", contract.get("CONTRID"));
            this.callService(Constants.B2BPOSWS, "dsB2BContractUpdate", updateMap, login, password);
        }

        if (isNeedCheckForCorrector(fromStateSysname, toStateSysName)) {
            if (checkForCorrector(exportData)) {
                result = recordMakeTrans(exportData, toStateSysName, idFieldName, methodNamePrefix, typeSysName, login, password);
            } else {
                result.put(ERROR, "У пользователя недостаточно прав на осуществление перехода состояния.");
            }
        } else {
            result = recordMakeTrans(exportData, toStateSysName, idFieldName, methodNamePrefix, typeSysName, login, password);
        }
        return result;
    }

    /**
     * Метод, определяющий, нужна ли проверка роли при выплнении перехода
     * состояния
     *
     * @param fromStateSysName начальное состояние
     * @param toStateSysName   конечное состояние
     * @return
     */
    private boolean isNeedCheckForCorrector(String fromStateSysName, String toStateSysName) {
        boolean result = false;
        if (fromStateSysName != null) {
            for (Map<String, String> allowedTrans : ONLY_MAN_ALLOWED_TRANS_LIST) {
                if (allowedTrans.get(fromStateSysName) != null) {
                    if (allowedTrans.get(fromStateSysName).equals(toStateSysName)) {
                        return true;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Проверка, имеет ли пользователь роль b2bCorrector
     *
     * @param params Map<String, Object>
     * @return Boolean
     * @throws Exception
     */
    private boolean checkForCorrector(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        Long userAccountId = getLongParam(params, Constants.SESSIONPARAM_USERACCOUNTID);
        Map<String, Object> callParams = new HashMap<>();
        callParams.put("USERACCOUNTID", userAccountId);
        List<Map<String, Object>> callResult = this.callServiceAndGetListFromResultMap(Constants.B2BPOSWS, "dsGetRoleListByUserAccountId", callParams, login, password);
        for (Map<String, Object> role : callResult) {
            String roleSysname = getStringParam(role, "ROLESYSNAME");
            if (roleSysname.equals(B2BCORRECTOR_ROLE_SYSNAME)) {
                return true;
            }
        }
        return false;
    }

    @WsMethod(requiredParams = {"CONTRID", "STATESYSNAME", "TOSTATESYSNAME"})
    public Map<String, Object> dsB2BcontractMakeTrans(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String toStateSysName = getStringParam(params.get("TOSTATESYSNAME"));
        Map<String, Object> result = contractMakeTrans(params, toStateSysName, login, password);
        return result;
    }

    /**
     * Метод для проверки перевода статус уникальный для продукта
     *
     * @param params   Map<String, Object>
     * @param login    String
     * @param password String
     * @return Map
     * @throws Exception
     */
    private Map<String, Object> triggerBeforeStateChange(Map<String, Object> params, String login, String password) throws Exception {
        logger.debug("triggerBeforeStateChange start...");

        Map<String, Object> result = new HashMap<String, Object>();

        // загружаем договор
        Map<String, Object> contrMap = getOrLoadContract(params, login, password);
        Long prodConfId = getLongParamLogged(contrMap, "PRODCONFID");

        if (prodConfId != null) {

            // получаем имя метода из продукта
            Map<String, Object> prodDefValParams = new HashMap<String, Object>();
            prodDefValParams.put("NAME", "TRIGGER_BEFORE_STATE_CHANGE_METHOD_NAME");
            prodDefValParams.put("PRODCONFID", prodConfId);
            prodDefValParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> prodDefVal = callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueBrowseListByNameNote", prodDefValParams, login, password);
            String methodName = getStringParamLogged(prodDefVal, "VALUE");

            // если он зарегистрирован
            if (!methodName.isEmpty()) {
                // формируем данные для метода
                Map<String, Object> methodParams = new HashMap<String, Object>();
                methodParams.put("params", params);
                methodParams.put("contract", contrMap);

                // вызываем зарегистрированную функцию
                Map<String, Object> methodResult = callService(B2BPOSWS_SERVICE_NAME, methodName, methodParams, login, password);
                logger.debug("triggerBeforeStateChange result: " + methodResult);

                // успешно? -> возвращаем данные
                if (methodResult.get("Status").toString().equalsIgnoreCase("OK")) {
                    result = getMapParam(methodResult, "Result");
                }
            }
        }

        logger.debug("triggerBeforeStateChange finished.");
        return result;
    }

    /**
     * Перевод состояния с проверками
     *
     * @param params Map<String, Object>
     * @return Map
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID", "STATESYSNAME", "TOSTATESYSNAME"})
    public Map<String, Object> dsB2BcontractMakeTransUWCheck(Map<String, Object> params) throws Exception {
        //1 из черновика в предпечать
        // - проверка, и если требуется uw - то взведение флага возврат наружу инфы о том что флаг взведен
        // - плановый переход
        //2 из предпечати в на подписание
        // - проверка, и если требуется uw - то взведение флага возврат наружу инфы о том что флаг взведен
        // - переход в пред андеррайтинг
        //3 из пред андеррайтинга в андеррайтинг
        // - отправка уведомления андеррайтерам
        //в остальных случаях - плановый переход.

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String fromStateSysName = getStringParam(params.get("STATESYSNAME"));
        String toStateSysName = getStringParam(params.get("TOSTATESYSNAME"));
        boolean isNeedUw = false;
        boolean isToUwBranch = false;
        Map<String, Object> contrMap = null;
        Map<String, Object> transRes = null;

        // обрабатываем триггер проверки состояния
        Map<String, Object> triggerResult = triggerBeforeStateChange(params, login, password);
        if (!triggerResult.isEmpty()) {
            // возвращаем данные и ошибку перехода
            triggerResult.put("Status", "Error");

            // берем сообщение данных полученных от сервиса
            String message = triggerResult.get("Error").toString();
            triggerResult.put("Error", message != null ? message : "Error trigger before state change");

            return triggerResult;
        }
        boolean isToSigned = "B2B_CONTRACT_SG".equalsIgnoreCase(toStateSysName);
        logger.debug("checks before maketrans from " + fromStateSysName + " to " + toStateSysName);
        // проверки перед переводом состояния (например, фонда)
        StringBuffer errorText = new StringBuffer();
        if (isToSigned) {
            logger.debug("Checking investment strategy...");
            // подписание договора - требуется проверка фонда (если предусмотрена продуктом)
            // для проверок потребуется полная мапа договора.
            contrMap = getOrLoadContract(params, login, password);
            // ни ниже при смене состояния ни ранее перед сменой состояния дата подписания в договор не сохраняется
            // (дата подписания будет установлена позднее, по дате смены состояния из истории состояний уже после перевода состояния)
            // поэтому датой подписания при проверке выбранной стратегии инвестирования будет считаться текущая серверная дата
            Map<String, Object> validInvestmentStrategy = validateInvestmentStrategy(contrMap, errorText, login, password);
            // проверка на 'Минимальный размер взноса' (с учетом наличия проверенной и корректной стратегии инвестирования)
            validateMinimumPaymentValue(contrMap, validInvestmentStrategy, errorText, login, password);
            logger.debug("Checking investment finished.");
        }
        boolean isDataValid = (errorText.length() == 0);
        if (!isDataValid) {
            errorText.append("Смена состояния договора не возможна.");
            String errorTextStr = errorText.toString();
            transRes = new HashMap<String, Object>();
            transRes.put("Status", "Error");
            transRes.put("Error", errorTextStr);
            logger.error(String.format(
                    "Trying to change contract state resulted in validation error with message '%s'!",
                    errorTextStr
            ));
            return transRes;
        }
        logger.debug("checks finished");
        logger.debug("begin maketrans from " + fromStateSysName + " to " + toStateSysName);
        if (("B2B_CONTRACT_DRAFT".equalsIgnoreCase(fromStateSysName)
                && ("B2B_CONTRACT_PREPRINTING".equalsIgnoreCase(toStateSysName)))
                || ("B2B_CONTRACT_PREPRINTING".equalsIgnoreCase(fromStateSysName)
                && ("B2B_CONTRACT_PREPARE".equalsIgnoreCase(toStateSysName)))) {
            // для проверок потребуется полная мапа договора.
            contrMap = getOrLoadContract(params, login, password);
            Map<String, Object> checkParams = new HashMap<String, Object>();
            checkParams.putAll(params);
            isNeedUw = checkNeedUw(checkParams);
            if (contrMap != null) {
                if (contrMap.get("CONTREXTMAP") != null) {
                    Map<String, Object> contrExtMap = getContrExtMap(contrMap);
                    Map<String, Object> contrExtMapUpd = new HashMap<String, Object>();
                    contrExtMapUpd.put("CONTRID", contrExtMap.get("CONTRID"));
                    contrExtMapUpd.put("HBDATAVERID", contrExtMap.get("HBDATAVERID"));
                    contrExtMapUpd.put("CONTREXTID", contrExtMap.get("CONTREXTID"));
                    if (isNeedUw) {
                        contrExtMapUpd.put("isUW", 1);
                        if ("B2B_CONTRACT_PREPRINTING".equalsIgnoreCase(fromStateSysName)
                                && ("B2B_CONTRACT_PREPARE".equalsIgnoreCase(toStateSysName))) {
                            isToUwBranch = true;
                        }

                    } else {
                        contrExtMapUpd.put("isUW", 0);
                    }
                    //parseDates(contrExtMap, Date.class);
                    updateContractValues(contrExtMapUpd, login, password);
                }
            }
        }
        if (isNeedUw && isToUwBranch) {
            logger.debug("isNeedUW && isToUWBranch");
            logger.debug("makeTrans");
            transRes = contractMakeTrans(params, "B2B_CONTRACT_PREDUW", login, password);
        } else {
            logger.debug("makeTrans");
            transRes = contractMakeTrans(params, toStateSysName, login, password);
            if (transRes != null) {
                if (transRes.get("Status") != null) {
                    logger.debug("makeTrans status " + transRes.get("Status").toString());
                    if (!"ERROR".equalsIgnoreCase(transRes.get("Status").toString())) {
                        if ("B2B_CONTRACT_PREDUW".equalsIgnoreCase(fromStateSysName)
                                && ("B2B_CONTRACT_UW".equalsIgnoreCase(toStateSysName))) {
                            //перевод состояния в андеррайтинг состоялся. отправляем уведомление на почту
                            contrMap = getOrLoadContract(params, login, password);
                            logger.debug("need send uw notification");
                            sendNotificationToUW(params);
                        }
                        if ("B2B_CONTRACT_UW".equalsIgnoreCase(fromStateSysName)
                                && ("B2B_CONTRACT_PREPARE".equalsIgnoreCase(toStateSysName))) {
                            //перевод из состояния андеррайтинг состоялся. отправляем уведомление продавцу на почту
                            contrMap = getOrLoadContract(params, login, password);
                            logger.debug("need send seller notification");
                            params.put("MAILSUBJECTMAINTEXT", "Договор акцептован");
                            params.put("HTMLMAILPATHKEYNAME", "HTMLMAILPATHSELLERACCEPT");
                            sendNotificationToSeller(params);
                        }
                        if ("B2B_CONTRACT_UW".equalsIgnoreCase(fromStateSysName)
                                && ("B2B_CONTRACT_CANCEL".equalsIgnoreCase(toStateSysName))) {
                            //перевод из состояния андеррайтинг состоялся. отправляем уведомление продавцу на почту
                            contrMap = getOrLoadContract(params, login, password);
                            logger.debug("need send seller notification");
                            params.put("MAILSUBJECTMAINTEXT", "Отказ в оформлении договора");
                            params.put("HTMLMAILPATHKEYNAME", "HTMLMAILPATHSELLERCANCEL");
                            sendNotificationToSeller(params);
                        }
                        if (isToSigned) {
                            //перевод в подписан - вызываем расчет агентских комиссий
                            Map<String, Object> calcParam = new HashMap<String, Object>();
                            calcParam.put("CONTRID", params.get("CONTRID"));
                            // для проверок потребуется полная мапа договора.
                            contrMap = this.callService(Constants.B2BPOSWS, "dsB2BcallCalcAgentCommissPrem", calcParam, login, password);
                        }
                    }
                }
            }
        }
        transRes.put("isUW", isNeedUw);
        return transRes;
    }

    private Map<String, Object> getOrLoadContract(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> contract = getMapParam(params, "CONTRMAP");
        if ((contract == null) || (contract.isEmpty())) {
            Long contractId = getLongParamLogged(params, "CONTRID");
            if (contractId != null) {
                Map<String, Object> contractParam = new HashMap<String, Object>();
                contractParam.put("CONTRID", contractId);
                contractParam.put(RETURN_AS_HASH_MAP, true);
                // для проверок потребуется полная мапа договора.
                contract = this.callService(Constants.B2BPOSWS, "dsB2BContrLoad", contractParam, login, password);
                params.put("CONTRMAP", contract);
            }
        }
        return contract;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BcallCalcAgentCommissPrem(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        if (params.get("CONTRMAP") == null) {
            Map<String, Object> contrParam = new HashMap<String, Object>();
            contrParam.put("CONTRID", params.get("CONTRID"));
            contrParam.put("ReturnAsHashMap", "TRUE");
            // для проверок потребуется полная мапа договора.
            Map<String, Object> contrMap = this.callService(Constants.B2BPOSWS, "dsB2BContrLoad", contrParam, login, password);
            params.put("CONTRMAP", contrMap);
        }
        return callCalcAgentCommissPrem(params);
    }

    // done: перенести callServiceAndGetOneValue в B2BBaseFacade - используется более, чем в одном custom-фасаде
    private void sortByRowStatus(List<Map<String, Object>> source,
                                 List<Map<String, Object>> inserted,
                                 List<Map<String, Object>> modified,
                                 List<Map<String, Object>> deleted,
                                 List<Map<String, Object>> unModified) {
        logger.debug("sortByRowStatus begin");
        for (Map<String, Object> bean : source) {
            // по умолчанию - данные добавляются
            RowStatus rowStatus = RowStatus.INSERTED;
            // определение типа модификации, если он указан в явном виде в передаваемых данных
            Object rowStatusObj = bean.get(ROWSTATUS_PARAM_NAME);
            if (rowStatusObj != null) {
                rowStatus = RowStatus.getRowStatusById(Integer.parseInt(rowStatusObj.toString()));
            }
            // выбор дополняемого списка по типу модификации
            List<Map<String, Object>> chosenList = null;
            if (rowStatus.equals(RowStatus.INSERTED)) {
                chosenList = inserted;
            } else if (rowStatus.equals(RowStatus.MODIFIED)) {
                chosenList = modified;
            } else if (rowStatus.equals(RowStatus.DELETED)) {
                chosenList = deleted;
            } else if (rowStatus.equals(RowStatus.UNMODIFIED)) {
                chosenList = unModified;
            }
            // дополнение выбранного списка (если был передан)
            if (chosenList != null) {
                chosenList.add(bean);
            }
        }

        logger.debug(String.format(
                "inserted: %d; modified: %d; deleted: %d; unModified: %d (where -1 means no such list provided for results)",
                inserted == null ? -1 : inserted.size(),
                modified == null ? -1 : modified.size(),
                deleted == null ? -1 : deleted.size(),
                unModified == null ? -1 : unModified.size()));
        logger.debug("sortByRowStatus end");
    }

    /*
    private void createContractValues(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        logger.debug("createContractValues begin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(contrExtMap);
        Object contrExtID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", params, login, password, "CONTREXTID");
        if (contrExtID != null) {
            contrExtMap.put("CONTREXTID", contrExtID);
            contrExtMap.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        }
        logger.debug("createContractValues end");
    }

    private void updateContractValues(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        logger.debug("updateContractValues begin");
        Map<String, Object> params = new HashMap<String, Object>();
        if (contrExtMap != null) {
            params.putAll(contrExtMap);
            Object contrExtID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", params, login, password, "CONTREXTID");
            if (contrExtID != null) {
                contrExtMap.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
            }
        }
        logger.debug("updateContractValues end");
    }
    */

    private Object createContractNode(String login, String password) throws Exception {
        logger.debug("createContractNode begin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("RVERSION", 0L);
        params.put("LASTVERNUMBER", 0L);
        Object contrNodeID = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BContractNodeCreate", params, login, password, "CONTRNODEID");
        logger.debug("createContractNode end");
        return contrNodeID;
    }

    private Object updateContractNode(Long contrNodeid, Long contrId, String login, String password) throws Exception {
        logger.debug("updateContractNode begin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONTRNODEID", contrNodeid);
        params.put("CONTRID", contrId);
        Object contrNodeID = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BContractNodeUpdate", params, login, password, "CONTRNODEID");
        logger.debug("updateContractNode end");
        return contrNodeID;
    }

    private void createContractValues(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        logger.debug("createContractValues begin");
        Map<String, Object> callParams = new HashMap<String, Object>();
        callParams.putAll(contrExtMap);
        callParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> callResult = callService(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", callParams, login, password);
        Long contrExtId = getLongParam(callResult, "CONTREXTID");
        if (isCallResultOK(callResult) && (contrExtId != null)) {
            contrExtMap.put("CONTREXTID", contrExtId);
            contrExtMap.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
            logger.debug("createContractValues end");
        } else {
            String error = String.format(
                    "Error on creating contract values (CONTREXTMAP) by calling dsHandbookRecordCreate (with params = %s)! Details (call result):\n\n%s",
                    callParams, callResult
            );
            logger.error(error);
            throw new Exception(error);
        }
        logger.debug("createContractValues end");
    }

    public void updateContractValues(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        logger.debug("updateContractValues begin");
        if (contrExtMap != null) {
            Map<String, Object> callParams = new HashMap<String, Object>();
            callParams.putAll(contrExtMap);
            callParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> callResult = callService(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", callParams, login, password);
            Long contrExtId = getLongParam(callResult, "CONTREXTID");
            if (isCallResultOK(callResult) && (contrExtId != null)) {
                contrExtMap.put("CONTREXTID", contrExtId);
                contrExtMap.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                logger.debug("createContractValues end");
            } else {
                String error = String.format(
                        "Error on updating contract values (CONTREXTMAP) by calling dsHandbookRecordUpdate (with params = %s)! Details (call result):\n\n%s",
                        callParams, callResult
                );
                logger.error(error);
                throw new Exception(error);
            }
        }
        logger.debug("updateContractValues end");
    }

    private void createContractObjectExtProp(Map<String, Object> contrObjExtMap, String login, String password) throws Exception {
        logger.debug("createContractObjectExtProp begin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(contrObjExtMap);
        Object contrObjExtID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", params, login, password, "CONTROBJEXTID");
        if (contrObjExtID != null) {
            contrObjExtMap.put("CONTROBJEXTID", contrObjExtID);
        }
        logger.debug("createContractObjectExtProp end");
    }

    private void updateContractObjectExtProp(Map<String, Object> contrObjExtMap, String login, String password) throws Exception {
        logger.debug("updateContractObjectExtProp begin");
        if (contrObjExtMap != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.putAll(contrObjExtMap);
            this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", params, login, password);
        }
        logger.debug("updateContractObjectExtProp end");
    }

    private void deleteContractObjectExtProp(Map<String, Object> contrObjExtMap, String login, String password) throws Exception {
        if (contrObjExtMap != null) {
            logger.debug("deleteContractObjectExtProp begin");
            Map<String, Object> params = new HashMap<String, Object>();
            params.putAll(contrObjExtMap);
            this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordDelete", params, login, password);
            logger.debug("deleteContractObjectExtProp end");
        }
    }

    // создание или обновление участника (конкретная операция выбирается в dsB2BParticipantSave по наличию идентификатора участника)
    private Object saveParticipant(Map<String, Object> participant, String login, String password) throws Exception {
        logger.debug("saveParticipant begin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(participant);
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        //Object savedParticipant = this.callServiceLogged(Constants.B2BPOSWS, "dsB2BParticipantSave", params, login, password); // todo: управляемое протоколирование
        Object savedParticipant = this.callService(Constants.B2BPOSWS, "dsB2BParticipantSave", params, login, password);
        logger.debug("saveParticipant end");
        return savedParticipant;
    }

    private void processContractData(Map<String, Object> contrBean, String login, String password) throws Exception {
        logger.debug("processContractData begin");
        if (isNewMemberModel(contrBean)) {
            // 1. сохранить страхователя
            processInsurer(contrBean, login, password);
            // 2. сохранить застрахованного
            processInsured(contrBean, login, password);
            // 3. сохранить выгодоприобретателей.
            processBeneficiaryList(contrBean, login, password);
            // 4. удалить дубли из мембер листа.
            removeDublicateMember(contrBean, login, password);

            // 5. Выкупные суммы и график накоплений для новых горизонтов
            String prodSysName = getStringParam(contrBean.get("PRODSYSNAME"));
            if ("B2B_NEW_HORIZONS".equalsIgnoreCase(prodSysName)) {
                processRedemptionSum(contrBean, login, password);
                processSavingSchedule(contrBean, login, password);
            }
        } else {
            // создание или обновление участников
            Boolean isNeedUpdateContr = false; // потребуется ли обновление договора
            for (String participantNode : participantNodes) {
                String dataKeyName = participantNode + "MAP";
                String idKeyName = participantNode + "ID";
                Object participant = contrBean.get(dataKeyName);
                if (participant != null) {
                    Object savedParticipant = saveParticipant((Map<String, Object>) participant, login, password);
                    if (contrBean.get(idKeyName) == null) {
                        // обновление идентификатора участника в самом договоре
                        contrBean.put(idKeyName, ((Map<String, Object>) savedParticipant).get("PARTICIPANTID"));
                        isNeedUpdateContr = true;
                    }
                    // полная замена данных участника - savedParticipant возвращает их все целиком
                    contrBean.put(dataKeyName, savedParticipant);
                }
            }
            // обновление договора (при изменении идентификаторов участников в самом договоре)
            if (isNeedUpdateContr) {
                this.callService(Constants.B2BPOSWS, "dsB2BContractUpdate", contrBean, login, password);
            }
            // обработка списка застрахованных
            processMemberListData(contrBean, login, password);
        }
        // сохранение групп объектов, объектов и тд
        Long contractId = Long.valueOf(contrBean.get("CONTRID").toString());
        Object contrSectionList = contrBean.get("CONTRSECTIONLIST");
        if (contrSectionList != null) {
            processContractSectionList(contractId, (List<Map<String, Object>>) contrSectionList, login, password);
        }
        // обработка подчиненных договоров
        if (contrBean.get("CHILDCONTRLIST") != null) {
            List<Map<String, Object>> childContrList = (List<Map<String, Object>>) contrBean.get("CHILDCONTRLIST");
            if (childContrList.size() > 0) {
                processChildContracts(contractId, contrBean, childContrList, login, password);
            }
        }
        // обработка параметров источников по договору
        if (contrBean.get("CONTRSRCPARAMLIST") != null) {
            List<Map<String, Object>> contrSrcParamList = (List<Map<String, Object>>) contrBean.get("CONTRSRCPARAMLIST");
            if (contrSrcParamList.size() > 0) {
                processContractSrcParams(contractId, contrSrcParamList, login, password);
            }
        }
        //
        logger.debug("processContractData end");
    }

    // обработка списка застрахованных
    private void processMemberListData(Map<String, Object> contrBean, String login, String password) throws Exception {
        Object memberListObj = contrBean.get("MEMBERLIST");
        if ((memberListObj != null) && (memberListObj instanceof List)) {
            List<Map<String, Object>> memberList = (List<Map<String, Object>>) memberListObj;
            for (Map<String, Object> member : memberList) {
                logger.debug("Processing member: " + member);
                member.put("CONTRID", contrBean.get("CONTRID"));
                String serviceName;
                String methodName;
                if (member.get("HBDATAVERID") != null) {
                    // если указан идентифиактор справочника - сохранение/обновление застрахованного будет выполнятся с поддержкой дополнительных полей, указанных в справочнике
                    serviceName = Constants.INSTARIFICATORWS;
                    methodName = "dsHandbookRecord";
                } else {
                    // если не указан идентифиактор справочника - сохранение/обновление застрахованного будет выполнятся только по основным полям таблицы B2B_MEMBER
                    serviceName = Constants.B2BPOSWS;
                    methodName = "dsB2BMember";
                }
                if (member.get("MEMBERID") != null) {
                    // если уже имеется идентифиактор застрахованного - требуется обновление сведений
                    // а вот и х%3 еще возможно запись подлежит удалению
                    String methodNameTmp = methodName + "Update";
                    Object rowStatusObj = member.get(ROWSTATUS_PARAM_NAME);
                    if (rowStatusObj != null) {
                        RowStatus rowStatus = RowStatus.getRowStatusById(Integer.parseInt(rowStatusObj.toString()));
                        if (rowStatus.equals(RowStatus.DELETED)) {
                            methodNameTmp = methodName + "Delete";
                        }
                    }
                    methodName = methodNameTmp;
                } else {
                    // если идентифиактор застрахованного отсутствует - требуется создание новой записи
                    methodName = methodName + "Create";
                }
                logger.debug("Member process service name: " + serviceName);
                logger.debug("Member process method name: " + methodName);
                Map<String, Object> memberParams = new HashMap<String, Object>();
                memberParams.putAll(member);
                memberParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> memberProcessResult = this.callService(serviceName, methodName, memberParams, login, password);
                logger.debug("Member processed with result: " + memberProcessResult);
                // дополнение застрахованного идентификатором (в случае создания)
                if (memberProcessResult != null) {
                    Long memberID = getLongParam(memberProcessResult.get("MEMBERID"));
                    if (memberID != null) {
                        member.put("MEMBERID", memberID);
                    }
                }
            }
        }
    }

    private void processChildContracts(Long contractId, Map<String, Object> contrBean, List<Map<String, Object>> childContrList, String login, String password) throws Exception {
        for (Map<String, Object> bean : childContrList) {
            // обрабатываем каждый договор
            Map<String, Object> contrParams = new HashMap<String, Object>();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            Map<String, Object> contrMap = new HashMap<String, Object>(bean);
            contrMap.put(Constants.SESSIONPARAM_USERACCOUNTID, contrBean.get(Constants.SESSIONPARAM_USERACCOUNTID));
            contrMap.put(Constants.SESSIONPARAM_USERTYPEID, contrBean.get(Constants.SESSIONPARAM_USERTYPEID));
            contrMap.put(Constants.SESSIONPARAM_DEPARTMENTID, contrBean.get(Constants.SESSIONPARAM_DEPARTMENTID));
            contrMap.put(DEPARTMENTS_KEY_NAME, contrBean.get(DEPARTMENTS_KEY_NAME));
            list.add(contrMap);
            contrParams.put("CONTRLIST", list);
            contrParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> resMap = this.callService(Constants.B2BPOSWS, "dsB2BContractUniversalSave", contrParams, login, password);
            list = (List<Map<String, Object>>) resMap.get("CONTRLIST");
            list.get(0).remove("ROWSTATUS");
            bean.putAll(list.get(0));
        }
        List<Map<String, Object>> inserted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> modified = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> deleted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> unModified = new ArrayList<Map<String, Object>>();
        this.sortByRowStatus(childContrList, inserted, modified, deleted, unModified);
        logger.debug("processChildContracts inserted begin");
        for (Map<String, Object> bean : inserted) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("CONTRID", contractId);
            params.put("CHILDID", bean.get("CONTRID"));
            this.callService(Constants.B2BPOSWS, "dsB2BContrChildCreate", params, login, password);
            bean.put("ROWSTATUS", UNMODIFIED_ID);
        }
        logger.debug("processChildContracts inserted end");
        logger.debug("processChildContracts deleted begin");
        for (Map<String, Object> bean : deleted) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(RETURN_AS_HASH_MAP, "TRUE");
            params.put("CONTRID", contractId);
            params.put("CHILDID", bean.get("CONTRID"));
            Map<String, Object> qRes = this.callService(Constants.B2BPOSWS, "dsB2BContrChildBrowseListByParam", params, login, password);
            if (qRes.get("CONTRCHILDID") != null) {
                params.clear();
                params.put("CONTRCHILDID", qRes.get("CONTRCHILDID"));
                this.callService(Constants.B2BPOSWS, "dsB2BContrChildDelete", params, login, password);
            }
        }
        logger.debug("processChildContracts deleted end");
    }

    private void processContractSrcParams(Long contractId, List<Map<String, Object>> contrSrcParamList, String login, String password) throws Exception {
        List<Map<String, Object>> inserted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> modified = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> deleted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> unModified = new ArrayList<Map<String, Object>>();
        this.sortByRowStatus(contrSrcParamList, inserted, modified, deleted, unModified);
        logger.debug("processContractSrcParams inserted begin");
        for (Map<String, Object> bean : inserted) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("CONTRID", contractId);
            params.putAll(bean);
            this.callService(Constants.B2BPOSWS, "dsB2BContractSourceParamCreate", params, login, password);
        }
        logger.debug("processContractSrcParams inserted end");
        logger.debug("processContractSrcParams modified begin");
        for (Map<String, Object> bean : modified) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.putAll(bean);
            this.callService(Constants.B2BPOSWS, "dsB2BContractSourceParamUpdate", params, login, password);
        }
        logger.debug("processContractSrcParams modified end");
        logger.debug("processContractSrcParams deleted begin");
        for (Map<String, Object> bean : deleted) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("CONTRSRCPARAMID", bean.get("CONTRSRCPARAMID"));
            this.callService(Constants.B2BPOSWS, "dsB2BContractSourceParamDelete", params, login, password);
        }
        logger.debug("processContractSrcParams deleted end");
    }

    private void processContractSectionList(Long contractId, List<Map<String, Object>> contrSectionList, String login, String password) throws Exception {
        logger.debug("processContractSectionList begin");
        List<Map<String, Object>> inserted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> modified = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> deleted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> unModified = new ArrayList<Map<String, Object>>();
        this.sortByRowStatus(contrSectionList, inserted, modified, deleted, unModified);
        logger.debug("processContractSectionList inserted begin");
        for (Map<String, Object> bean : inserted) {
            bean.put("CONTRID", contractId);
            Map<String, Object> contrSectionMap = new HashMap<String, Object>();
            contrSectionMap.putAll(bean);
            contrSectionMap.remove("INSOBJGROUPLIST");
            Object contrSectionID = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BContractSectionCreate", contrSectionMap, login, password, "CONTRSECTIONID");
            if ((contrSectionID != null)) {
                bean.put("CONTRSECTIONID", contrSectionID);
                // обработка CONTRSECTIONEXTMAP
                Map<String, Object> contrSectionExtMap = (Map<String, Object>) bean.get("CONTRSECTIONEXTMAP");
                if (contrSectionExtMap != null) {
                    contrSectionExtMap.put("CONTRSECTIONID", contrSectionID);
                    Map<String, Object> contrSectionExtParamMap = new HashMap<String, Object>();
                    contrSectionExtParamMap.putAll(contrSectionExtMap);
                    Object contrSectionExtID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", contrSectionExtParamMap, login, password, "CONTRSECTIONEXTID");
                    if (contrSectionExtID != null) {
                        contrSectionExtMap.put("CONTRSECTIONEXTID", contrSectionExtID);
                    }
                }
                //
                bean.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                if (bean.get("INSOBJGROUPLIST") != null) {
                    processInsObjGroupList(contractId, Long.valueOf(contrSectionID.toString()), (List<Map<String, Object>>) bean.get("INSOBJGROUPLIST"), login, password);
                }
            }
        }
        logger.debug("processContractSectionList inserted end");
        logger.debug("processContractSectionList modified begin");
        for (Map<String, Object> bean : modified) {
            Map<String, Object> contrSectionMap = new HashMap<String, Object>();
            contrSectionMap.putAll(bean);
            contrSectionMap.remove("INSOBJGROUPLIST");
            Object contrSectionID = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BContractSectionUpdate", contrSectionMap, login, password, "CONTRSECTIONID");
            if ((contrSectionID != null)) {
                // обработка CONTRSECTIONEXTMAP
                Map<String, Object> contrSectionExtMap = (Map<String, Object>) bean.get("CONTRSECTIONEXTMAP");
                if (contrSectionExtMap != null) {
                    Map<String, Object> contrSectionExtParamMap = new HashMap<String, Object>();
                    contrSectionExtParamMap.putAll(contrSectionExtMap);
                    this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", contrSectionExtParamMap, login, password);
                }
                //
                bean.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                if (bean.get("INSOBJGROUPLIST") != null) {
                    processInsObjGroupList(contractId, Long.valueOf(contrSectionID.toString()), (List<Map<String, Object>>) bean.get("INSOBJGROUPLIST"), login, password);
                }
            }
        }
        logger.debug("processContractSectionList modified end");
        logger.debug("processContractSectionList deleted begin");
        for (Map<String, Object> bean : deleted) {
            Map<String, Object> contrSectionMap = new HashMap<String, Object>();
            contrSectionMap.putAll(bean);
            contrSectionMap.remove("INSOBJGROUPLIST");
            this.callService(Constants.B2BPOSWS, "B2BContractSection", contrSectionMap, login, password);
            Long contrSectionId = Long.valueOf(bean.get("CONTRSECTIONID").toString());
            // обработка CONTRSECTIONEXTMAP
            Map<String, Object> contrSectionExtMap = (Map<String, Object>) bean.get("CONTRSECTIONEXTMAP");
            Map<String, Object> contrSectionExtParamMap = new HashMap<String, Object>();
            contrSectionExtParamMap.putAll(contrSectionExtMap);
            this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordDelete", contrSectionExtParamMap, login, password);
            //
            if (bean.get("INSOBJGROUPLIST") != null) {
                processInsObjGroupList(contractId, contrSectionId, (List<Map<String, Object>>) bean.get("INSOBJGROUPLIST"), login, password);
                // удаленная секция исключается из результатов, но только при условии отсутствия дочерних объектов после их обработки
                // (предполагается, что в штатной ситуации в ходе обработки существующих дочерних объектов удаляемой секции они также будут удалены или "откреплены")
                List<Map<String, Object>> processedList = (List<Map<String, Object>>) bean.get("INSOBJGROUPLIST");
                if ((processedList == null) || (((List) processedList).isEmpty())) {
                    contrSectionList.remove(bean);
                }
            }
        }
        logger.debug("processContractSectionList deleted end");
        logger.debug("processContractSectionList unModified begin");
        for (Map<String, Object> bean : unModified) {
            getAndProcessObjectList(contractId, bean, login, password);
        }
        logger.debug("processContractSectionList unModified end");
        logger.debug("processContractSectionList end");
    }

    private void checkForContractSectionId(Map<String, Object> entityParamMap) {
        if (entityParamMap.get("CONTRSECTIONID") == null) {
            logger.error("No CONTRSECTIONID found in save parameters map (" + entityParamMap + ") - entity will be saved without connection with contract section!");
        }
    }

    private void processInsObjGroupList(Long contractId, Long contractSectionId, List<Map<String, Object>> insObjGroupList, String login, String password) throws Exception {
        logger.debug("processInsObjGroupList begin");
        List<Map<String, Object>> inserted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> modified = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> deleted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> unModified = new ArrayList<Map<String, Object>>();
        this.sortByRowStatus(insObjGroupList, inserted, modified, deleted, unModified);
        logger.debug("processInsObjGroupList inserted begin");
        for (Map<String, Object> bean : inserted) {
            // добавление идентификатора секции договора в создаваемую группу объектов (должен присутствовать в параметрах на момент создания)
            bean.put("CONTRSECTIONID", contractSectionId);
            Map<String, Object> insObjGroupMap = new HashMap<String, Object>();
            insObjGroupMap.putAll(bean);
            insObjGroupMap.remove("OBJLIST");
            checkForContractSectionId(insObjGroupMap);
            Object insObjGroupID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", insObjGroupMap, login, password, "INSOBJGROUPID");
            if ((insObjGroupID != null)) {
                bean.put("INSOBJGROUPID", insObjGroupID);
                bean.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                getAndProcessObjectList(contractId, bean, login, password);
            }
        }
        logger.debug("processInsObjGroupList inserted end");
        logger.debug("processInsObjGroupList modified begin");
        for (Map<String, Object> bean : modified) {
            Map<String, Object> insObjGroupMap = new HashMap<String, Object>();
            insObjGroupMap.putAll(bean);
            insObjGroupMap.remove("OBJLIST");
            checkForContractSectionId(insObjGroupMap);
            Object insObjGroupID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", insObjGroupMap, login, password, "INSOBJGROUPID");
            if ((insObjGroupID != null)) {
                bean.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
            }
            getAndProcessObjectList(contractId, bean, login, password);
        }
        logger.debug("processInsObjGroupList modified end");
        logger.debug("processInsObjGroupList deleted begin");
        for (Map<String, Object> bean : deleted) {
            Map<String, Object> insObjGroupMap = new HashMap<String, Object>();
            insObjGroupMap.putAll(bean);
            insObjGroupMap.remove("OBJLIST");
            this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordDelete", insObjGroupMap, login, password);
            Object processedObjectList = getAndProcessObjectList(contractId, bean, login, password);
            // удаленная группа исключается из результатов, но только при условии отсутствия дочерних объектов после их обработки
            // (предполагается, что в штатной ситуации в ходе обработки существующих дочерних объектов удаляемой группы они также будут удалены или "откреплены" от группы)
            if ((processedObjectList == null) || (((List) processedObjectList).isEmpty())) {
                insObjGroupList.remove(bean);
            }
        }
        logger.debug("processInsObjGroupList deleted end");
        logger.debug("processInsObjGroupList unModified begin");
        for (Map<String, Object> bean : unModified) {
            getAndProcessObjectList(contractId, bean, login, password);
        }
        logger.debug("processInsObjGroupList unModified end");
        logger.debug("processInsObjGroupList end");
    }

    private Object getAndProcessObjectList(Long contractId, Map<String, Object> objListSource, String login, String password) throws Exception {
        logger.debug("getAndProcessObjectList begin");
        Object objList = objListSource.get("OBJLIST");
        if (objList != null) {
            Long insObjGroupIDValue = Long.valueOf(objListSource.get("INSOBJGROUPID").toString());
            processObjectList(contractId, insObjGroupIDValue, (List<Map<String, Object>>) objList, login, password);
        }
        logger.debug("getAndProcessObjectList end");
        // возврат ссылки на список обработанных объектов
        return objList;
    }

    private void processObjectList(Long contractId, Long insObjGroupIDValue, List<Map<String, Object>> objList, String login, String password) throws Exception {
        logger.debug("processObjectList begin");
        List<Map<String, Object>> inserted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> modified = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> deleted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> unModified = new ArrayList<Map<String, Object>>();
        this.sortByRowStatus(objList, inserted, modified, deleted, unModified);
        logger.debug("processObjectList inserted begin");
        for (Map<String, Object> bean : inserted) {
            // обработка CONTROBJMAP
            Map<String, Object> contrObjMap = (Map<String, Object>) bean.get("CONTROBJMAP");
            contrObjMap.put("CONTRID", contractId);
            Map<String, Object> contrObjParamMap = new HashMap<String, Object>();
            contrObjParamMap.putAll(contrObjMap);
            contrObjParamMap.remove("CONTROBJEXTMAP");
            contrObjParamMap.remove("CONTRRISKLIST");
            Object contrObjID = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BContractObjectCreate", contrObjParamMap, login, password, "CONTROBJID");
            if (contrObjID != null) {
                contrObjMap.put("CONTROBJID", contrObjID); // обновление CONTROBJMAP идентификатором созданного объекта
                // обработка расш. атрибутов объекта договора
                Map<String, Object> contrObjExtMap = (Map<String, Object>) contrObjMap.get("CONTROBJEXTMAP");
                if (contrObjExtMap != null) {
                    contrObjExtMap.put("CONTROBJID", contrObjID);
                    createContractObjectExtProp(contrObjExtMap, login, password);
                }
                // обработка INSOBJMAP
                Map<String, Object> insObjMap = (Map<String, Object>) bean.get("INSOBJMAP");
                insObjMap.put("CONTROBJID", contrObjID); // обновление INSOBJMAP идентификатором созданного объекта
                insObjMap.put("INSOBJGROUPID", insObjGroupIDValue);
                Map<String, Object> insObjParamMap = new HashMap<String, Object>();
                insObjParamMap.putAll(insObjMap);
                Object insObjID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", insObjParamMap, login, password, "INSOBJID");
                if (insObjID != null) {
                    insObjMap.put("INSOBJID", insObjID);
                    bean.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID); // оба идентификатора (contrObjID и insObjID) не пусты - объект успешно сохранен в базу
                }
                // обработка рисков объекта договора
                Object contrRisks = contrObjMap.get("CONTRRISKLIST");
                if (contrRisks != null) {
                    Long contrObjIDValue = Long.valueOf(contrObjID.toString());
                    processContractRiskList(contrObjIDValue, (List<Map<String, Object>>) contrRisks, login, password);
                }
            }
        }
        logger.debug("processObjectList inserted end");
        logger.debug("processObjectList modified begin");
        for (Map<String, Object> bean : modified) {
            // обработка CONTROBJMAP
            Map<String, Object> contrObjMap = (Map<String, Object>) bean.get("CONTROBJMAP");
            Map<String, Object> contrObjParamMap = new HashMap<String, Object>();
            contrObjParamMap.putAll(contrObjMap);
            contrObjParamMap.remove("CONTROBJEXTMAP");
            contrObjParamMap.remove("CONTRRISKLIST");
            this.callService(Constants.B2BPOSWS, "dsB2BContractObjectUpdate", contrObjParamMap, login, password);
            // обработка расш. атрибутов объекта договора
            Map<String, Object> contrObjExtMap = (Map<String, Object>) contrObjMap.get("CONTROBJEXTMAP");
            updateContractObjectExtProp(contrObjExtMap, login, password);
            // обработка INSOBJMAP
            Map<String, Object> insObjMap = (Map<String, Object>) bean.get("INSOBJMAP");
            Map<String, Object> insObjParamMap = new HashMap<String, Object>();
            insObjParamMap.putAll(insObjMap);
            this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", insObjParamMap, login, password);
            bean.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID); // изменения объекта страхования сохранены в БД
            // обработка рисков объекта договора
            Object contrRisks = contrObjMap.get("CONTRRISKLIST");
            if (contrRisks != null) {
                Long contrObjIDValue = Long.valueOf(contrObjMap.get("CONTROBJID").toString());
                processContractRiskList(contrObjIDValue, (List<Map<String, Object>>) contrRisks, login, password);
            }
        }
        logger.debug("processObjectList modified end");
        logger.debug("processObjectList deleted begin");
        for (Map<String, Object> bean : deleted) {
            // обработка CONTROBJMAP
            Map<String, Object> contrObjMap = (Map<String, Object>) bean.get("CONTROBJMAP");
            Map<String, Object> contrObjParamMap = new HashMap<String, Object>();
            contrObjParamMap.putAll(contrObjMap);
            contrObjParamMap.remove("CONTROBJEXTMAP");
            contrObjParamMap.remove("CONTRRISKLIST");
            this.callService(Constants.B2BPOSWS, "dsB2BContractObjectDelete", contrObjParamMap, login, password);
            // обработка расш. атрибутов объекта договора
            Map<String, Object> contrObjExtMap = (Map<String, Object>) contrObjMap.get("CONTROBJEXTMAP");
            deleteContractObjectExtProp(contrObjExtMap, login, password);
            // обработка INSOBJMAP
            Map<String, Object> insObjMap = (Map<String, Object>) bean.get("INSOBJMAP");
            Map<String, Object> insObjParamMap = new HashMap<String, Object>();
            insObjParamMap.putAll(insObjMap);
            this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordDelete", insObjParamMap, login, password);
            // обработка рисков объекта договора
            Object contrRisks = contrObjMap.get("CONTRRISKLIST");
            if (contrRisks != null) {
                Long contrObjIDValue = Long.valueOf(contrObjMap.get("CONTROBJID").toString());
                processContractRiskList(contrObjIDValue, (List<Map<String, Object>>) contrRisks, login, password);
            }
        }
        logger.debug("processObjectList deleted end");
        logger.debug("processObjectList unModified begin");
        for (Map<String, Object> bean : unModified) {
            Map<String, Object> contrObjMap = (Map<String, Object>) bean.get("CONTROBJMAP");
            // обработка рисков объекта договора
            Object contrRisks = contrObjMap.get("CONTRRISKLIST");
            if (contrRisks != null) {
                Long contrObjIDValue = Long.valueOf(contrObjMap.get("CONTROBJID").toString());
                processContractRiskList(contrObjIDValue, (List<Map<String, Object>>) contrRisks, login, password);
            }
        }
        logger.debug("processObjectList unModified end");
        logger.debug("processObjectList end");
    }

    private void processContractRiskList(Long contrObjId, List<Map<String, Object>> contrRiskList, String login, String password) throws Exception {
        logger.debug("processContractRiskList begin");
        List<Map<String, Object>> inserted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> modified = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> deleted = new ArrayList<Map<String, Object>>();
        this.sortByRowStatus(contrRiskList, inserted, modified, deleted, null);
        logger.debug("processContractRiskList inserted begin");
        for (Map<String, Object> bean : inserted) {
            Map<String, Object> contrRiskMap = new HashMap<String, Object>();
            contrRiskMap.put("CONTROBJID", contrObjId);
            contrRiskMap.putAll(bean);
            contrRiskMap.remove("CONTRRISKEXTMAP");
            Object contrRiskID = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BContractRiskCreate", contrRiskMap, login, password, "CONTRRISKID");
            if (contrRiskID != null) {
                bean.put("CONTRRISKID", contrRiskID);
                bean.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID); // риск сохранен в БД
                Map<String, Object> contrRiskExtMap = (Map<String, Object>) bean.get("CONTRRISKEXTMAP");
                if (contrRiskExtMap != null) {
                    contrRiskExtMap.put("CONTRRISKID", contrRiskID);
                    Map<String, Object> contrRiskExtParamMap = new HashMap<String, Object>();
                    contrRiskExtParamMap.putAll(contrRiskExtMap);
                    Object contrRiskExtID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", contrRiskExtParamMap, login, password, "CONTRRISKEXTID");
                    if (contrRiskExtID != null) {
                        contrRiskExtMap.put("CONTRRISKEXTID", contrRiskExtID);
                    }
                }
            }
        }
        logger.debug("processContractRiskList inserted end");
        logger.debug("processContractRiskList modified begin");
        for (Map<String, Object> bean : modified) {
            Map<String, Object> contrRiskMap = new HashMap<String, Object>();
            contrRiskMap.putAll(bean);
            contrRiskMap.remove("CONTRRISKEXTMAP");
            this.callService(Constants.B2BPOSWS, "dsB2BContractRiskUpdate", contrRiskMap, login, password);
            Map<String, Object> contrRiskExtMap = (Map<String, Object>) bean.get("CONTRRISKEXTMAP");
            if (contrRiskExtMap != null) {
                // обновляем расширенные атрибуты риска, только если у риска имеется мапа с ними
                Map<String, Object> contrRiskExtParamMap = new HashMap<String, Object>();
                contrRiskExtParamMap.putAll(contrRiskExtMap);
                this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", contrRiskExtParamMap, login, password);
            }
        }
        logger.debug("processContractRiskList modified end");
        logger.debug("processContractRiskList deleted begin");
        for (Map<String, Object> bean : deleted) {
            Map<String, Object> contrRiskMap = new HashMap<String, Object>();
            contrRiskMap.putAll(bean);
            contrRiskMap.remove("CONTRRISKEXTMAP");
            this.callService(Constants.B2BPOSWS, "dsB2BContractRiskDelete", contrRiskMap, login, password);
            Map<String, Object> contrRiskExtMap = (Map<String, Object>) bean.get("CONTRRISKEXTMAP");
            if (contrRiskExtMap != null) {
                // удаляем расширенные атрибуты риска, только если у риска имеется мапа с ними
                Map<String, Object> contrRiskExtParamMap = new HashMap<String, Object>();
                contrRiskExtParamMap.putAll(contrRiskExtMap);
                this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordDelete", contrRiskExtParamMap, login, password);
            }
        }
        logger.debug("processContractRiskList deleted end");
        logger.debug("processContractRiskList end");
    }

    private void convertNonSectionContract(Map<String, Object> contractBean, String login, String password) throws Exception {
        logger.debug("convertNonSectionContract begin");
        if (contractBean.get("CONTRSECTIONLIST") == null) {
            RowStatus rowStatus = RowStatus.INSERTED;
            Object rowStatusObj = contractBean.get(ROWSTATUS_PARAM_NAME);
            if (rowStatusObj != null) {
                rowStatus = RowStatus.getRowStatusById(Integer.parseInt(rowStatusObj.toString()));
            }
            Map<String, Object> contractSectionMap = new HashMap<String, Object>();
            if (rowStatus == RowStatus.MODIFIED) {
                // если договор обновляется (например, в B2B), но секция при этом не указана, необходимо считать секцию из БД и обновить ее данные
                // важно понимать, что секция при этом должна быть одна (автоматически сгенерированная при создании договора, т.к. маппинга секции нет)
                Map<String, Object> sectionParams = new HashMap<String, Object>();
                sectionParams.put(RETURN_AS_HASH_MAP, "TRUE");
                sectionParams.put("CONTRID", contractBean.get("CONTRID"));
                contractSectionMap = this.callService(Constants.B2BPOSWS, "dsB2BContractSectionBrowseListByParam", sectionParams, login, password);
                contractSectionMap.put(ROWSTATUS_PARAM_NAME, 2L);
            }
            contractSectionMap.put("CONTRID", contractBean.get("CONTRID"));
            contractSectionMap.put("STARTDATE", contractBean.get("STARTDATE"));
            contractSectionMap.put("FINISHDATE", contractBean.get("FINISHDATE"));
            contractSectionMap.put("INSAMCURRENCYID", contractBean.get("INSAMCURRENCYID"));
            contractSectionMap.put("INSAMVALUE", contractBean.get("INSAMVALUE"));
            contractSectionMap.put("PREMCURRENCYID", contractBean.get("PREMCURRENCYID"));
            contractSectionMap.put("PREMVALUE", contractBean.get("PREMVALUE"));
            // заполняем ссылку на структуру секции
            Map<String, Object> prodStructParams = new HashMap<String, Object>();
            prodStructParams.put(RETURN_AS_HASH_MAP, "TRUE");
            prodStructParams.put("PRODVERID", contractBean.get("PRODVERID"));
            Map<String, Object> prodStructRes = this.callService(Constants.B2BPOSWS, "dsB2BProductStructureSectionBrowseListByParam", prodStructParams, login, password);
            if (prodStructRes != null) {
                contractSectionMap.put("PRODSTRUCTID", prodStructRes.get("PRODSTRUCTID"));
            }
            //
            contractSectionMap.put("INSOBJGROUPLIST", contractBean.get("INSOBJGROUPLIST"));
            contractBean.remove("INSOBJGROUPLIST");
            List<Map<String, Object>> contrSectionList = new ArrayList<Map<String, Object>>();
            contrSectionList.add(contractSectionMap);
            contractBean.put("CONTRSECTIONLIST", contrSectionList);
            contractBean.put(CONTRACTSECTION_EMULATE, 1L);
        }
        logger.debug("convertNonSectionContract end");
    }

    @WsMethod(requiredParams = {"CONTRLIST"})
    public Map<String, Object> dsB2BContractUniversalSave(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BContractUniversalSave begin");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> contractList = (List<Map<String, Object>>) params.get("CONTRLIST");
        // универсальное массовое сохранение договоров
        List<Map<String, Object>> inserted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> modified = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> unModified = new ArrayList<Map<String, Object>>();
        this.sortByRowStatus(contractList, inserted, modified, null, unModified);
        // при сохранении договора необходимо поддержать возможность сохранения данных, где нет информации о секции договора,
        // тогда секция автоматически с параметрами из мапы договора, также считается, что у секции отсутствуют показатели,
        // при этом нужно сохранять указатель на структуру, чтобы потом не было проблем с выгрузкой договоров и т.д.
        for (Map<String, Object> bean : inserted) {
            Map<String, Object> contrMap = new HashMap<String, Object>();
            contrMap.putAll(bean);
            contrMap.remove("CONTRSECTIONLIST");
            contrMap.remove("INSOBJGROUPLIST");
            contrMap.remove("INSURERMAP");
            contrMap.remove("INSURERREPMAP");
            contrMap.remove("MEMBERLIST");
            contrMap.remove(BENEFICIARY_LIST_KEY_NAME);
            contrMap.remove("CHILDCONTRLIST");
            contrMap.remove("CONTRSRCPARAMLIST");
            // Считаем если нода есть в параметрах создания договора,  то создаем версию договора.
            if (null == contrMap.get("CONTRNODEID")) {
                contrMap.put("CONTRNODEID", createContractNode(login, password));
            }
            //contrMap.put(RETURN_AS_HASH_MAP, "TRUE");
            Object contrID = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BContractCreate", contrMap, login, password, "CONTRID");
            updateContractNode(getLongParam(contrMap.get("CONTRNODEID")), getLongParam(contrID), login, password);
            if (contrID != null) {
                bean.put("CONTRID", contrID);
                convertNonSectionContract(bean, login, password);
                bean.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                Long contractId = Long.valueOf(contrID.toString());

                // создание прав на договор по пользоватеню производится в аспекте orgStruct при вызове dsB2BContractCreate
                // здесь вызывать нет необходимости.
                //createContractRights(contractId, contrMap, login, password);
                Map<String, Object> contrExtMap = (Map<String, Object>) bean.get("CONTREXTMAP");
                if (contrExtMap != null) {
                    contrExtMap.put("CONTRID", contrID);
                    createContractValues(contrExtMap, login, password);
                }
                processContractData(bean, login, password);
            } else {
                result.put("Error", "Ошибка при создании договора");
                return result;
            }
        }
        for (Map<String, Object> bean : modified) {
            convertNonSectionContract(bean, login, password);
            Map<String, Object> contrMap = new HashMap<String, Object>();
            contrMap.putAll(bean);
            contrMap.remove("CONTRSECTIONLIST");
            contrMap.remove("INSOBJGROUPLIST");
            contrMap.remove("INSURERMAP");
            contrMap.remove("INSURERREPMAP");
            contrMap.remove("MEMBERLIST");
            contrMap.remove(BENEFICIARY_LIST_KEY_NAME);
            contrMap.remove("CHILDCONTRLIST");
            contrMap.remove("CONTRSRCPARAMLIST");
            this.callService(Constants.B2BPOSWS, "dsB2BContractUpdate", contrMap, login, password);
            Map<String, Object> contrExtMap = (Map<String, Object>) bean.get("CONTREXTMAP");
            if (contrExtMap != null) {
                Object contrExtID = contrExtMap.get("CONTREXTID");
                // договор мог быть создан без указания расширенных атрибутов
                if (contrExtID != null) {
                    updateContractValues(contrExtMap, login, password);
                } else {
                    // создание расширенных атрибутов, если добавлены в договор уже после его создания
                    contrExtMap.put("CONTRID", bean.get("CONTRID"));
                    createContractValues(contrExtMap, login, password);
                }
            }
            processContractData(bean, login, password);
        }
        for (Map<String, Object> bean : unModified) {
            convertNonSectionContract(bean, login, password);
            processContractData(bean, login, password);
        }

        for (Map<String, Object> contrBean : contractList) {
            //// выше не загружается состояние договора - надо загрузить его здесь.
            Map<String, Object> paramState = new HashMap<String, Object>();
            paramState.put("CONTRID", contrBean.get("CONTRID"));
            paramState.put("ReturnAsHashMap", "TRUE");

            Map<String, Object> paramStateRes = this.callService(Constants.B2BPOSWS, "dsB2BContractBrowseListByParam", paramState, login, password);
            contrBean.put("STATEID", paramStateRes.get("STATEID"));
            contrBean.put("CONTRNODEID", paramStateRes.get("CONTRNODEID"));
            contrBean.put("CURRENCYRATE", paramStateRes.get("CURRENCYRATE"));
            contrBean.put("DOCUMENTDATE", paramStateRes.get("DOCUMENTDATE"));
            contrBean.put("VERNUMBER", paramStateRes.get("VERNUMBER"));
            contrBean.put("STATENAME", paramStateRes.get("STATENAME"));
            contrBean.put("STATESYSNAME", paramStateRes.get("STATESYSNAME"));
            contrBean.put("EXTERNALID", paramStateRes.get("EXTERNALID")); // + внешний идентификатор для договора, требуется для онлайн-продуктов
            //contrbean.put("GUID", paramStateRes.get("EXTERNALID")); // + внешний идентификатор для договора, требуется для онлайн-продуктов
            //contrbean.put("HASH", base64Encode(getStringParam(paramStateRes.get("EXTERNALID")))); // + хеш внешнего идентификатор для договора, требуется для онлайн-продуктов

            // если шла эмуляция сохранения секции, обратно возвращаем без нее для совместимости
            if (contrBean.get(CONTRACTSECTION_EMULATE) != null) {
                List<Map<String, Object>> contrSectionList = (List<Map<String, Object>>) contrBean.get("CONTRSECTIONLIST");
                if ((contrSectionList != null) && (contrSectionList.size() > 0)) {
                    List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contrSectionList.get(0).get("INSOBJGROUPLIST");
                    contrBean.remove("CONTRSECTIONLIST");
                    contrBean.put("INSOBJGROUPLIST", insObjGroupList);
                }
            }
        }
        //STATENAME
        result.put("CONTRLIST", contractList);
        logger.debug("dsB2BContractUniversalSave end");
        return result;
    }

    @WsMethod
    public Map<String, Object> testOrgStructAspectCrt(Map<String, Object> params) throws Exception {
        params.put("CONTRID", 3104017);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractCustom_OrgStruct_createOrgStructInfo", params, login, password);
        return res;
    }

    @WsMethod
    public Map<String, Object> testOrgStructAspectBlock(Map<String, Object> params) throws Exception {
        params.put("CONTRID", 3104017);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractCustom_OrgStruct_blockOrgStructInfo", params, login, password);
        return res;
    }

    @WsMethod
    public Map<String, Object> testOrgStructAspectDelete(Map<String, Object> params) throws Exception {
        params.put("CONTRID", 3104017);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractCustom_OrgStruct_deleteOrgStructInfo", params, login, password);
        return res;
    }

    @WsMethod
    public Map<String, Object> testOrgStructAspectBrowse(Map<String, Object> params) throws Exception {
        params.put("CONTRID", 3104017);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractCustom_OrgStruct_orgStructBrowseListByParam", params, login, password);
        return res;
    }

    @WsMethod
    public Map<String, Object> testOrgStructAspectBrowseActive(Map<String, Object> params) throws Exception {
        params.put("CONTRID", 3104017);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractCustom_OrgStruct_activeOrgStructBrowseListByParam", params, login, password);
        return res;
    }


    protected Map<String, Object> createContractRights(Long contractId, Map<String, Object> params, String login, String password) throws Exception {

        logger.debug("Назначение прав для созданного договора...");


        Map<String, Object> orgStructParam = new HashMap<>();
        orgStructParam.put("CONTRID", contractId);
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractCustom_OrgStruct_createOrgStructInfo", orgStructParam, login, password);
        return res;
//        String rightsMethodName;
//        Map<String, Object> rightsParams = new HashMap<String, Object>();
//        rightsParams.put("CONTRID", contractId);
//        rightsParams.put("USERACCOUNTID", params.get(Constants.SESSIONPARAM_USERACCOUNTID));
//        rightsParams.put("UPDATETEXT", "Создан договор");
//
//        if (params.get(DEPARTMENTS_KEY_NAME) != null) {
//            rightsParams.put("ORGSTRUCTNAMESLIST", params.get(DEPARTMENTS_KEY_NAME));
//            rightsMethodName = "dsB2BContractCreateOrgStructsByNamesAndHist";
//        } else {
//            rightsParams.put("ORGSTRUCTID", params.get(Constants.SESSIONPARAM_DEPARTMENTID));
//            rightsMethodName = "dsB2BContractCreateOrgStructAndHist";
//        }
//        Map<String, Object> result = null;
//        try {
//            result = this.callService(Constants.B2BPOSWS, rightsMethodName, rightsParams, login, password);
//            if (isCallResultOK(result)) {
//                logger.debug("Назначение прав для созданного договора завершено без возникновения ошибок.");
//            } else {
//                logger.debug("При назначении прав для созданного договора возникла ошибка: " + result);
//            }
//        } catch (Exception e) {
//            logger.debug("Произошло исключение при назначении прав для созданного договора:", e);
//        }
//        return result;
    }

    private Map<String, Object> loadContractData(Long contractId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        params.put("CONTRID", contractId);
        Map<String, Object> result = this.callService(Constants.B2BPOSWS, "dsB2BContractBrowseListByParam", params, login, password);
        // доп. проверка - загрузка договора по ИД должна вернуть этот же ид
        // если этого не произошло, значит договор с таким ИД не найден в БД
        // и следует прервать выполнение загрузки с отображением ошибки
        if (!contractId.equals(getLongParam(result, "CONTRID"))) {
            logger.error(String.format(
                    "Unable to load contract with id (CONTRID) = %d from DB by dsB2BContractBrowseListByParam! Details (call result): %s",
                    contractId, result
            ));
            if (result == null) {
                result = new HashMap<String, Object>();
            }
            result.put(ERROR, "Не удалось загрузить договор!");
        }
        return result;
    }

    private Map<String, Object> loadContractValues(Long contractId, Map<String, Object> productMap, String login, String password) throws Exception {
        if (productMap.get("HBDATAVERID") != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(RETURN_AS_HASH_MAP, "TRUE");
            params.put("CONTRID", contractId);
            List<Long> hbDataVerIdList = new ArrayList();
            hbDataVerIdList.add(Long.valueOf(productMap.get("HBDATAVERID").toString()));
            params.put("HBDATAVERIDLIST", hbDataVerIdList);
            return this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParamByHBDVIdList", params, login, password);
        } else {
            return null;
        }
    }

    private List<Long> getHBDataVerIdListFromProdStruct(Map<String, Object> productMap, Long discriminator) {
        Map<String, Object> prodVerMap = (Map<String, Object>) productMap.get("PRODVER");
        if (prodVerMap != null) {
            List<Map<String, Object>> prodStructs = (List<Map<String, Object>>) prodVerMap.get("PRODSTRUCTS");
            if (prodStructs != null) {
                List<Long> result = new ArrayList<Long>();
                for (Map<String, Object> bean : prodStructs) {
                    if (Long.valueOf(bean.get("DISCRIMINATOR").toString()).longValue() == discriminator.longValue()) {
                        if (bean.get("HBDATAVERID") != null) {
                            Long hbDataVerId = Long.valueOf(bean.get("HBDATAVERID").toString());
                            boolean fInList = false;
                            for (Long ver : result) {
                                if (hbDataVerId.longValue() == ver.longValue()) {
                                    fInList = true;
                                    break;
                                }
                            }
                            if (!fInList) {
                                result.add(hbDataVerId);
                            }
                        }
                    }
                }
                return result;
            } else {
                return new ArrayList<Long>();
            }
        } else {
            return new ArrayList<Long>();
        }
    }

    private Map<String, Object> getProdStructMapById(Map<String, Object> productMap, Object productStructureIdObj) {
        Long productStructureId = getLongParam(productStructureIdObj);
        if (productStructureId != null) {
            Map<String, Object> prodVerMap = (Map<String, Object>) productMap.get("PRODVER");
            if (prodVerMap != null) {
                List<Map<String, Object>> prodStructs = (List<Map<String, Object>>) prodVerMap.get("PRODSTRUCTS");
                if (prodStructs != null) {
                    for (Map<String, Object> bean : prodStructs) {
                        if (Long.valueOf(bean.get("PRODSTRUCTID").toString()).longValue() == productStructureId.longValue()) {
                            return bean;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Сервис загрузки списка секции по идентификатору договора идентификатор конфигурации продукта.
     * PRODCONFID - требуется для загрузки информации по продукту, которая используется
     * для добавления системных имен и др. в структуру секции
     *
     * @param params <UL>
     *               <LI>CONTRID - идентификатор договора</LI>
     *               <LI>PRODCONFID - идентификатор конфигурации продукта</LI>
     *               </UL>
     * @return CONTRSECTIONLIST - список секци по договору
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID"})
    public Map<String, Object> loadContractSectionListService(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        Map<String, Object> prodParam = new HashMap<>();
        prodParam.put("PRODCONFID", getLongParam(params, "PRODCONFID"));
        prodParam.put("HIERARCHY", true);
        prodParam.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> productMap = this.callService(Constants.B2BPOSWS, "dsProductBrowseByParams", prodParam, login, password);
        if (productMap.get(RESULT) != null) {
            productMap = (Map<String, Object>) productMap.get(RESULT);
        }
        Long contractId = getLongParam(params, "CONTRID");
        List<Map<String, Object>> contractSections = loadContractSectionList(contractId, productMap, login, password);
        Map<String, Object> result = new HashMap<>();
        if (contractSections == null) {
            result.put(ERROR, "Неудалось загрузить список секции договора");
        } else {
            result.put("CONTRSECTIONLIST", contractSections);
        }
        return result;
    }

    private List<Map<String, Object>> loadContractSectionList(Long contractId, Map<String, Object> productMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONTRID", contractId);
        Map<String, Object> qRes = this.callService(Constants.B2BPOSWS, "dsB2BContractSectionBrowseListByParam", params, login, password);
        List<Map<String, Object>> contractSectionList = WsUtils.getListFromResultMap(qRes);
        if (contractSectionList != null) {
            if (!contractSectionList.isEmpty()) {
                for (Map<String, Object> bean : contractSectionList) {
                    Map<String, Object> contrSectionStructMap = getProdStructMapById(productMap, bean.get("PRODSTRUCTID"));
                    if (contrSectionStructMap != null) {
                        bean.put("CONTRSECTIONSYSNAME", contrSectionStructMap.get("SYSNAME"));
                    }
                    bean.put("INSOBJGROUPLIST", loadInsObjGroupList(Long.valueOf(bean.get("CONTRSECTIONID").toString()), productMap, login, password));
                    if (bean.get("INSOBJGROUPLIST") != null) {
                        loadObjLists(contractId, (List<Map<String, Object>>) bean.get("INSOBJGROUPLIST"), productMap, login, password);
                    }
                }
                return contractSectionList;
            }
        }
        return null;
    }

    private List<Map<String, Object>> loadInsObjGroupList(Long contractSectionId, Map<String, Object> productMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONTRSECTIONID", contractSectionId);
        params.put("HBDATAVERIDLIST", getHBDataVerIdListFromProdStruct(productMap, 2L));
        Map<String, Object> qRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParamByHBDVIdList", params, login, password);
        List<Map<String, Object>> insObjGroupList = WsUtils.getListFromResultMap(qRes);
        if (insObjGroupList != null) {
            if (!insObjGroupList.isEmpty()) {
                for (Map<String, Object> bean : insObjGroupList) {
                    Map<String, Object> insObjGroupStructMap = getProdStructMapById(productMap, bean.get("PRODSTRUCTID"));
                    if (insObjGroupStructMap != null) {
                        bean.put("INSOBJGROUPSYSNAME", insObjGroupStructMap.get("SYSNAME"));
                    }
                }
                return insObjGroupList;
            }
        }
        return null;
    }

    private List<Map<String, Object>> loadContractObjectList(Long contractId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONTRID", contractId);
        Map<String, Object> qRes = this.callService(Constants.B2BPOSWS, "dsB2BContractObjectBrowseListByParam", params, login, password);
        List<Map<String, Object>> contractObjectList = WsUtils.getListFromResultMap(qRes);
        if (contractObjectList != null) {
            if (!contractObjectList.isEmpty()) {
                return contractObjectList;
            }
        }
        return null;
    }

    private List<Map<String, Object>> loadInsObjList(Long insObjGroupId, Map<String, Object> productMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("INSOBJGROUPID", insObjGroupId);
        params.put("HBDATAVERIDLIST", getHBDataVerIdListFromProdStruct(productMap, 3L));
        Map<String, Object> qRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParamByHBDVIdList", params, login, password);
        List<Map<String, Object>> insObjList = WsUtils.getListFromResultMap(qRes);
        if (insObjList != null) {
            if (!insObjList.isEmpty()) {
                return insObjList;
            }
        }
        return null;
    }

    private void loadObjLists(Long contractId, List<Map<String, Object>> insObjGroupList, Map<String, Object> productMap, String login, String password) throws Exception {
        List<Map<String, Object>> allContractObjects = loadContractObjectList(contractId, login, password);
        for (Map<String, Object> bean : insObjGroupList) {
            Long insObjGroupId = Long.valueOf(bean.get("INSOBJGROUPID").toString());
            List<Map<String, Object>> insObjList = loadInsObjList(insObjGroupId, productMap, login, password);
            List<Map<String, Object>> objList = new ArrayList<Map<String, Object>>();
            if (insObjList != null) {
                for (Map<String, Object> insObjBean : insObjList) {
                    Map<String, Object> insObjStructMap = getProdStructMapById(productMap, insObjBean.get("PRODSTRUCTID"));
                    if (insObjStructMap != null) {
                        insObjBean.put("INSOBJSYSNAME", insObjStructMap.get("SYSNAME"));
                    }
                    Map<String, Object> objMap = new HashMap<String, Object>();
                    objMap.put("INSOBJMAP", insObjBean);
                    for (Map<String, Object> contrObjBean : allContractObjects) {
                        if (insObjBean.get("CONTROBJID").toString().equals(contrObjBean.get("CONTROBJID").toString())) {
                            objMap.put("CONTROBJMAP", contrObjBean);
                            break;
                        }
                    }
                    if (objMap.get("CONTROBJMAP") != null) {
                        Map<String, Object> contrObjMap = (Map<String, Object>) objMap.get("CONTROBJMAP");
                        Long contractObjectId = Long.valueOf(contrObjMap.get("CONTROBJID").toString());
                        /*Map<String, Object> paramsContrObjExt = new HashMap<String, Object>();
                         paramsContrObjExt.put(RETURN_AS_HASH_MAP, "TRUE");
                         paramsContrObjExt.put("CONTROBJID", contractObjectId);
                         Map<String, Object> qRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParamEx", paramsContrObjExt, login, password);
                         if (qRes != null) {
                         contrObjMap.put("CONTROBJEXTMAP", qRes);
                         }*/
                        contrObjMap.put("CONTRRISKLIST", loadContractRiskList(contractObjectId, productMap, login, password));
                    }
                    objList.add(objMap);
                }
            }
            if (objList.size() > 0) {
                bean.put("OBJLIST", objList);
            }
        }
    }

    private List<Map<String, Object>> loadContractRiskList(Long contractObjectId, Map<String, Object> productMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONTROBJID", contractObjectId);
        Map<String, Object> qRes = this.callService(Constants.B2BPOSWS, "dsB2BContractRiskBrowseListByParamEx", params, login, password);
        if ((qRes != null) && (qRes.get(RESULT) != null) && (((List<Map<String, Object>>) qRes.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> contractRiskList = (List<Map<String, Object>>) qRes.get(RESULT);
            for (Map<String, Object> bean : contractRiskList) {
                Map<String, Object> riskStructMap = getProdStructMapById(productMap, bean.get("PRODSTRUCTID"));
                if (riskStructMap != null) {
                    bean.put("PRODRISKSYSNAME", riskStructMap.get("SYSNAME"));
                    bean.put("PRODRISKHINT", riskStructMap.get("HINT"));
                }
                Long contractRiskId = Long.valueOf(bean.get("CONTRRISKID").toString());
                Map<String, Object> paramsContrRiskExt = new HashMap<String, Object>();
                paramsContrRiskExt.put(RETURN_AS_HASH_MAP, "TRUE");
                paramsContrRiskExt.put("CONTRRISKID", contractRiskId);
                paramsContrRiskExt.put("HBDATAVERIDLIST", getHBDataVerIdListFromProdStruct(productMap, 4L));
                Map<String, Object> qContrRiskExtRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParamByHBDVIdList", paramsContrRiskExt, login, password);
                if (qContrRiskExtRes != null) {
                    bean.put("CONTRRISKEXTMAP", qContrRiskExtRes);
                }
            }
            return contractRiskList;
        } else {
            return null;
        }
    }

    private List<Map<String, Object>> loadChildContractList(Long contractId, String login, String password) throws Exception {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONTRID", contractId);
        Map<String, Object> qRes = this.callService(Constants.B2BPOSWS, "dsB2BContrChildBrowseListByParam", params, login, password);
        if ((qRes != null) && (qRes.get(RESULT) != null) && (((List<Map<String, Object>>) qRes.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> childContractList = (List<Map<String, Object>>) qRes.get(RESULT);
            for (Map<String, Object> bean : childContractList) {
                if (bean.get("CHILDID") != null) {
                    Map<String, Object> childParams = new HashMap<String, Object>();
                    childParams.put("CONTRID", bean.get("CHILDID"));
                    Map<String, Object> childMap = this.callService(Constants.B2BPOSWS, "dsB2BContractUniversalLoad", childParams, login, password);
                    result.add((Map<String, Object>) childMap.get(RESULT));
                }
            }
        }
        return result;
    }

    private Map<String, Object> loadContractValuesByContrId(Long contractId, String login, String password) throws Exception {
        Map<String, Object> result = null;

        Map<String, Object> getVerConfIDParams = new HashMap<String, Object>();
        getVerConfIDParams.put("CONTRID", contractId);
        Object prodVerId = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BContractBrowseListByParam", getVerConfIDParams, login, password, "PRODVERID");
        Map<String, Object> getProdConfIDParams = new HashMap<String, Object>();
        getProdConfIDParams.put("PRODVERID", prodVerId);
        Long prodConfId = (Long) this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductConfigBrowseListByParam", getProdConfIDParams, login, password, "PRODCONFID");
        Map<String, Object> prodParam = new HashMap<String, Object>();
        prodParam.put("PRODCONFID", prodConfId);
        prodParam.put("HIERARCHY", true);
        prodParam.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> prodMap = this.callService(Constants.B2BPOSWS, "dsProductBrowseByParams", prodParam, login, password);
        if (prodMap.get(RESULT) != null) {
            prodMap = (Map<String, Object>) prodMap.get(RESULT);
            // полная загрузка договора
            result.putAll(loadContractData(contractId, login, password));
            result.put("CONTREXTMAP", loadContractValues(contractId, prodMap, login, password));
        }
        return result;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BContractUniversalLoad(Map<String, Object> params) throws Exception {
        // протоколирование вызова
        //long callTimer = System.currentTimeMillis();
        //String methodName = "dsB2BContractUniversalLoad";
        //logger.debug("Вызван метод " + methodName + " с параметрами:\n\n" + params.toString() + "\n");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Long contractId = Long.valueOf(params.get("CONTRID").toString());

        if (params.get("PRODUCTMAP") == null) {
            Map<String, Object> getVerConfIDParams = new HashMap<String, Object>();
            getVerConfIDParams.putAll(params);
            getVerConfIDParams.put("CONTRID", contractId);
            Object prodVerId = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BContractBrowseListByParam", getVerConfIDParams, login, password, "PRODVERID");
            Map<String, Object> getProdConfIDParams = new HashMap<String, Object>();
            getProdConfIDParams.put("PRODVERID", prodVerId);
            Long prodConfId = (Long) this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductConfigBrowseListByParam", getProdConfIDParams, login, password, "PRODCONFID");
            Map<String, Object> prodParam = new HashMap<String, Object>();
            prodParam.put("PRODCONFID", prodConfId);
            prodParam.put("HIERARCHY", true);
            prodParam.put(RETURN_AS_HASH_MAP, "TRUE");
            Map<String, Object> prodMap = this.callService(Constants.B2BPOSWS, "dsProductBrowseByParams", prodParam, login, password);
            if (prodMap.get(RESULT) != null) {
                prodMap = (Map<String, Object>) prodMap.get(RESULT);
            }
            params.put("PRODUCTMAP", prodMap);
        }
        Map<String, Object> productMap = (Map<String, Object>) params.get("PRODUCTMAP");
        Map<String, Object> result = new HashMap<String, Object>();
        // полная загрузка договора
        Map<String, Object> contract = loadContractData(contractId, login, password);
        result.putAll(contract);
        String error = getStringParam(contract, ERROR);
        if (!error.isEmpty()) {
            // загрузка договора сообщает об ошибке
            // следует прервать выполнение загрузки с отображением ошибки
            return result;
        }
        result.put("CONTREXTMAP", loadContractValues(contractId, productMap, login, password));
        if (result.get("INSURERID") != null) {
            result.put("INSURERMAP", loadParticipant(Long.valueOf(result.get("INSURERID").toString()), login, password));
        }
        List<Map<String, Object>> memberList = loadMembers(contractId, productMap, login, password);
        result.put("MEMBERLIST", memberList);
        Map<String, Object> memberbyPartId = new HashMap<String, Object>();
        Long insurerID = getLongParamLogged(result, "INSURERID"); // страхователь из договора
        for (Map<String, Object> memberMap : memberList) {
            Long memberParticipantID = getLongParamLogged(memberMap, "PARTICIPANTID"); // ИД партисипанта, если мембер имеет
            if (memberParticipantID != null) {
                String memberTypeSysName = getStringParamLogged(memberMap, "TYPESYSNAME"); // тип мембера
                if (memberParticipantID.equals(insurerID) && (!"insured".equalsIgnoreCase(memberTypeSysName))) {
                    memberbyPartId.put(memberParticipantID.toString(), result.get("INSURERMAP"));
                    memberMap.put("PARTICIPANTMAP", result.get("INSURERMAP"));
                } else {
                    Map<String, Object> partMap = loadParticipant(memberParticipantID, login, password);
                    memberbyPartId.put(memberParticipantID.toString(), partMap);
                    memberMap.put("PARTICIPANTMAP", partMap);
                    if ("insured".equalsIgnoreCase(memberTypeSysName)) {
                        logger.debug("This is insured - put participant info in INSUREDMAP.");
                        result.put("INSUREDMAP", partMap);
                    }
                }
            }
        }
        List<Map<String, Object>> beneficiaryList = loadBeneficiary(contractId, productMap, login, password);
        for (Map<String, Object> benefMap : beneficiaryList) {
            if ((benefMap.get("PARTICIPANTID") != null) && (Long.valueOf(benefMap.get("PARTICIPANTID").toString()) > 0)) {
                if (memberbyPartId.get(benefMap.get("PARTICIPANTID").toString()) != null) {
                    Map<String, Object> partMap = (Map<String, Object>) memberbyPartId.get(benefMap.get("PARTICIPANTID").toString());
                    benefMap.put("PARTICIPANTMAP", partMap);
                } else {
                    Map<String, Object> partMap = loadParticipant(Long.valueOf(benefMap.get("PARTICIPANTID").toString()), login, password);
                    benefMap.put("PARTICIPANTMAP", partMap);
                }
            }
        }

        result.put(BENEFICIARY_LIST_KEY_NAME, beneficiaryList);

        if (result.get("INSURERREPID") != null) {
            result.put("INSURERREPMAP", loadParticipant(Long.valueOf(result.get("INSURERREPID").toString()), login, password));
        }
        // реализовано для совместимости со всеми старыми продуктами (чтобы не нужно было переделывать маппинг в сервисах и на интерфейсах)
        // загружаем данные о секции только в случае, если указан флаг (продукты в новом формате)
        if ((params.get("LOADCONTRSECTION") != null) && (Long.valueOf(params.get("LOADCONTRSECTION").toString())).longValue() == 1) {
            result.put("CONTRSECTIONLIST", loadContractSectionList(contractId, productMap, login, password));
        } else {
            // старый формат, читаем одну секцию и грузим как раньше
            Map<String, Object> sectionParams = new HashMap<String, Object>();
            sectionParams.put(RETURN_AS_HASH_MAP, "TRUE");
            sectionParams.put("CONTRID", contractId);
            Map<String, Object> sectionRes = this.callService(Constants.B2BPOSWS, "dsB2BContractSectionBrowseListByParam", sectionParams, login, password);
            if (sectionRes.get("CONTRSECTIONID") != null) {
                result.put("INSOBJGROUPLIST", loadInsObjGroupList(Long.valueOf(sectionRes.get("CONTRSECTIONID").toString()), productMap, login, password));
                if (result.get("INSOBJGROUPLIST") != null) {
                    // проставляем ссылку на договор руками (т.к. там будет ссылка только на секцию)
                    List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) result.get("INSOBJGROUPLIST");
                    for (Map<String, Object> bean : insObjGroupList) {
                        bean.put("CONTRID", contractId);
                    }
                    //
                    loadObjLists(contractId, (List<Map<String, Object>>) result.get("INSOBJGROUPLIST"), productMap, login, password);
                }
            }
        }
        result.put("CHILDCONTRLIST", loadChildContractList(contractId, login, password));

        List<Map<String, Object>> redemptionList = loadRedemptions(contractId, productMap, login, password);
        result.put(REDEMPTION_SUM_LIST_PARAMNAME, redemptionList);
        List<Map<String, Object>> savingSchedule = loadSavingSchedule(contractId, productMap, login, password);
        result.put(SAVINGSSCHEDULE_SUM_LIST_PARAMNAME, savingSchedule);

        // все возвращаемые сущности помечаются статусом UNMODIFIED
        markAllMapsByKeyValue(result, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);

        // преобразование дат из возможных форматов в строки для передачи в Angular-интерфейс
        // здесь требуется всегда, поскольку загрузка договоров может вызываться при печати полисов (где даты ожидаются в строковом представлении) вместо использования отдельных дата-провайдеров
        parseDates(result, String.class);

        result.put("PRODUCTMAP", params.get("PRODUCTMAP"));
        // протоколирование вызова
        //callTimer = System.currentTimeMillis() - callTimer;
        //logger.debug("Метод " + methodName + " выполнился за " + callTimer + " мс. и вернул результат:\n\n" + result.toString() + "\n");
        //
        return result;
    }

    private void clearEmptyValue(Map<String, Object> params, String paramName) {
        if ((params.get(paramName) != null) && params.get(paramName).toString().equalsIgnoreCase("")) {
            params.put(paramName, null);
        }
    }

    @WsMethod()
    public Map<String, Object> dsGetB2BContractsStateDate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = null;
        result = this.selectQuery("dsGetB2BContractsStateDate", "dsGetB2BContractsStateDateCount", params);
        return result;
    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @param params <UL>
     *               <LI>CANCELDATE - Дата расторжения договора</LI>
     *               <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     *               <LI>CONTRNDNUMBER - Номер договора</LI>
     *               <LI>CONTRNUMBER - Полный номер версии договора</LI>
     *               <LI>CONTRPOLNUM - Номер полиса (отдельно от серии)</LI>
     *               <LI>CONTRPOLSER - Серия полиса</LI>
     *               <LI>CREATEDATE - Дата создания договора</LI>
     *               <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     *               <LI>DOCUMENTDATE - Дата оформления</LI>
     *               <LI>DURATION - Срок действия договора</LI>
     *               <LI>EXTERNALID - Внешний ИД</LI>
     *               <LI>FINISHDATE - Дата окончания действия договора</LI>
     *               <LI>CONTRID - ИД договора</LI>
     *               <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     *               <LI>INSAMVALUE - Размер страховой суммы</LI>
     *               <LI>INSREGIONCODE - Регион страхования</LI>
     *               <LI>INSURERID - ИД страхователя</LI>
     *               <LI>INSURERREPID - ИД представителя страхователя</LI>
     *               <LI>NUMMETHODID - Метод нумерации</LI>
     *               <LI>PAYVARID - Вариант выплат по договору</LI>
     *               <LI>PREMCURRENCYID - Валюта ответственности</LI>
     *               <LI>PREMDELTA - Изменение премии</LI>
     *               <LI>PREMVALUE - Премия в валюте ответственности</LI>
     *               <LI>PRODPROGID - ИД программы по продукту</LI>
     *               <LI>PRODVERID - ИД версии продукта, по которой заключается договор</LI>
     *               <LI>SALESOFFICE - Офис продаж</LI>
     *               <LI>SELLERID - ИД продавца</LI>
     *               <LI>STARTDATE - Дата начала действия договора</LI>
     *               <LI>STATEID - ИД состояния договора</LI>
     *               <LI>UPDATEDATE - Дата редактирования договора</LI>
     *               <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     *               <LI>VERNUMBER - Номер версии договора</LI>
     *               </UL>
     * @return <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CONTRNUMBER - Полный номер версии договора</LI>
     * <LI>CONTRPOLNUM - Номер полиса (отдельно от серии)</LI>
     * <LI>CONTRPOLSER - Серия полиса</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>DOCUMENTDATE - Дата оформления</LI>
     * <LI>DURATION - Срок действия договора</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>INSREGIONCODE - Регион страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>INSURERREPID - ИД представителя страхователя</LI>
     * <LI>NUMMETHODID - Метод нумерации</LI>
     * <LI>PAYVARID - Вариант выплат по договору</LI>
     * <LI>PREMCURRENCYID - Валюта ответственности</LI>
     * <LI>PREMDELTA - Изменение премии</LI>
     * <LI>PREMVALUE - Премия в валюте ответственности</LI>
     * <LI>PRODPROGID - ИД программы по продукту</LI>
     * <LI>PRODVERID - ИД версии продукта, по которой заключается договор</LI>
     * <LI>SALESOFFICE - Офис продаж</LI>
     * <LI>SELLERID - ИД продавца</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>STATEID - ИД состояния договора</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     * @author reson
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListByParamEx(Map<String, Object> params) throws Exception {
        // clear fields
        clearEmptyValue(params, "CONTRPOLSER");
        clearEmptyValue(params, "CONTRPOLNUM");
        clearEmptyValue(params, "CREDCONTRNUM");
        clearEmptyValue(params, "STARTSTARTDATE");
        clearEmptyValue(params, "STARTFINISHDATE");

        clearEmptyValue(params, "CONTRNUMBER");
        clearEmptyValue(params, "INSUREDNAME");
        clearEmptyValue(params, "NOTE");
        clearEmptyValue(params, "STATESYSNAME");
        clearEmptyValue(params, "STATESYSNAMELIST");
        clearEmptyValue(params, "SELLERFIO");
        clearEmptyValue(params, "UWSYSNAME");
        // даты с форм приходят в виде строк. надо их переделать в даты.
        //parseDateFromMap(params);
        //XMLUtil.convertDateToFloat(params);
        if (params.get(ORDERBY) == null) {
            params.put(ORDERBY, "T.CREATEDATE DESC");
        }
        parseDates(params, Double.class);
        Map<String, Object> result = this.selectQuery("dsB2BContractBrowseListByParamEx", "dsB2BContractBrowseListByParamExCount", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListByParam4InsurerChangeDops(Map<String, Object> params) throws Exception {
        // clear fields

        Map<String, Object> result = this.selectQuery("dsB2BContractBrowseListByParam4InsurerChangeDops", null, params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListByParamToPa2Register(Map<String, Object> params) throws Exception {
        // clear fields

        Map<String, Object> result = this.selectQuery("dsB2BContractBrowseListByParamToPa2Register", null, params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListByParamExShort(Map<String, Object> params) throws Exception {
        // clear fields
        clearEmptyValue(params, "CONTRPOLSER");
        clearEmptyValue(params, "CONTRPOLNUM");
        clearEmptyValue(params, "CREDCONTRNUM");
        clearEmptyValue(params, "STARTSTARTDATE");
        clearEmptyValue(params, "STARTFINISHDATE");

        clearEmptyValue(params, "CONTRNUMBER");
        clearEmptyValue(params, "INSUREDNAME");
        clearEmptyValue(params, "NOTE");
        clearEmptyValue(params, "STATESYSNAME");
        clearEmptyValue(params, "STATESYSNAMELIST");
        clearEmptyValue(params, "SELLERFIO");
        clearEmptyValue(params, "UWSYSNAME");
        // даты с форм приходят в виде строк. надо их переделать в даты.
        //parseDateFromMap(params);
        //XMLUtil.convertDateToFloat(params);
        if (params.get(ORDERBY) == null) {
            params.put(ORDERBY, "T.CREATEDATE DESC");
        }
        // по умолчанию скрываем договора помеченные у которых ISHIDDEN=1
        if (getBooleanParam(params, "ISHIDDENCONTRACT", true)) {
            params.put("ISHIDDENCONTRACT", true);
        } else {
            params.put("ISHIDDENCONTRACT", null);
        }
        parseDates(params, Double.class);
        Map<String, Object> result = this.selectQuery("dsB2BContractBrowseListByParamExShort", "dsB2BContractBrowseListByParamExShortCount", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseList4PartnerService(Map<String, Object> params) throws Exception {
        // даты с форм приходят в виде строк. надо их переделать в даты.
        //parseDateFromMap(params);
        //XMLUtil.convertDateToFloat(params);
        parseDates(params, Double.class);
        Map<String, Object> result = this.selectQuery("dsB2BContractBrowseList4PartnerService", "dsB2BContractBrowseList4PartnerServiceCount", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListByParamExPA2(Map<String, Object> params) throws Exception {
        if (params.get(ORDERBY) == null) {
            params.put(ORDERBY, "T.CREATEDATE DESC");
        }
        parseDates(params, Double.class);
        Map<String, Object> result = this.selectQuery("dsB2BContractBrowseListByParamExPA2", "dsB2BContractBrowseListByParamExPA2Count", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseByContrIdForIntegration(Map<String, Object> params) throws Exception {
        // clear fields
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = this.selectQuery("dsB2BContractBrowseByContrIdForIntegration", null, params);
        if (result.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) result.get(RESULT);
            if (!resList.isEmpty()) {
                result = resList.get(0);
                if (result != null) {
                    Long contractId = getLongParam(result.get("CONTRID"));
                    Long prodConfId = getLongParam(result.get("PRODCONFID"));

                    Map<String, Object> prodParam = new HashMap<String, Object>();
                    prodParam.put("PRODCONFID", prodConfId);
                    prodParam.put("HIERARCHY", true);
                    prodParam.put(RETURN_AS_HASH_MAP, "TRUE");
                    Map<String, Object> productMap = this.callService(Constants.B2BPOSWS, "dsProductBrowseByParams", prodParam, login, password);
                    if (productMap.get(RESULT) != null) {
                        productMap = (Map<String, Object>) productMap.get(RESULT);
                    }
                    result.put("PRODUCTMAP", productMap);

                    result.put("CONTREXTMAP", loadContractValues(contractId, productMap, login, password));
                    if (result.get("INSURERID") != null) {
                        result.put("INSURERMAP", loadParticipant(Long.valueOf(result.get("INSURERID").toString()), login, password));
                    }
                    List<Map<String, Object>> memberList = loadMembers(contractId, productMap, login, password);
                    result.put("MEMBERLIST", memberList);
                    Map<String, Object> memberbyPartId = new HashMap<String, Object>();
                    Long insurerID = getLongParamLogged(result, "INSURERID"); // страхователь из договора
                    for (Map<String, Object> memberMap : memberList) {
                        Long memberParticipantID = getLongParamLogged(memberMap, "PARTICIPANTID"); // ИД партисипанта, если мембер имеет
                        if (memberParticipantID != null) {
                            String memberTypeSysName = getStringParamLogged(memberMap, "TYPESYSNAME"); // тип мембера
                            if (memberParticipantID.equals(insurerID) && (!"insured".equalsIgnoreCase(memberTypeSysName))) {
                                memberbyPartId.put(memberParticipantID.toString(), result.get("INSURERMAP"));
                                memberMap.put("PARTICIPANTMAP", result.get("INSURERMAP"));
                            } else {
                                Map<String, Object> partMap = loadParticipant(memberParticipantID, login, password);
                                memberbyPartId.put(memberParticipantID.toString(), partMap);
                                memberMap.put("PARTICIPANTMAP", partMap);
                                if ("insured".equalsIgnoreCase(memberTypeSysName)) {
                                    logger.debug("This is insured - put participant info in INSUREDMAP.");
                                    result.put("INSUREDMAP", partMap);
                                }
                            }
                        }
                    }

                    // здесь необходимо догрузить сущности не поддержанные универсальной загрузкой.
                    // платежи
                    if (result.get("CONTRNODEID") != null) {
                        Long contrNodeId = (Long) result.get("CONTRNODEID");
                        Map<String, Object> planParams = new HashMap<String, Object>();
                        planParams.put("CONTRNODEID", contrNodeId);
                        //planParams.put("ReturnAsHashMap", "TRUE");
                        Map<String, Object> qPlanRes = this.callService(B2BPOSWS, "dsB2BPaymentFactBrowseListByParamEx", planParams, login, password);
                        result.put("PAYMENTLIST", WsUtils.getListFromResultMap(qPlanRes));
                    }
                    // получение графика оплаты
                    if (result.get("CONTRID") != null) {
                        Map<String, Object> factParams = new HashMap<String, Object>();
                        Long contrId = Long.valueOf(result.get("CONTRID").toString());
                        factParams.put("CONTRID", contrId);
                        Map<String, Object> qFactRes = this.callService(B2BPOSWS, "dsB2BPaymentBrowseListByParam", factParams, login, password);
                        result.put("PAYMENTSCHEDULELIST", qFactRes.get(RESULT));

                    }

                    // gолучить перечень рисков.
                    // загружаем данные о секции только в случае, если указан флаг (продукты в новом формате)
                    if ((params.get("LOADCONTRSECTION") != null) && (Long.valueOf(params.get("LOADCONTRSECTION").toString())).longValue() == 1) {
                        result.put("CONTRSECTIONLIST", loadContractSectionList(contractId, productMap, login, password));
                    } else {
                        // старый формат, читаем одну секцию и грузим как раньше
                        Map<String, Object> sectionParams = new HashMap<String, Object>();
                        sectionParams.put(RETURN_AS_HASH_MAP, "TRUE");
                        sectionParams.put("CONTRID", contractId);
                        Map<String, Object> sectionRes = this.callService(Constants.B2BPOSWS, "dsB2BContractSectionBrowseListByParam", sectionParams, login, password);
                        if (sectionRes.get("CONTRSECTIONID") != null) {
                            result.put("INSOBJGROUPLIST", loadInsObjGroupList(Long.valueOf(sectionRes.get("CONTRSECTIONID").toString()), productMap, login, password));
                            if (result.get("INSOBJGROUPLIST") != null) {
                                // проставляем ссылку на договор руками (т.к. там будет ссылка только на секцию)
                                List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) result.get("INSOBJGROUPLIST");
                                for (Map<String, Object> bean : insObjGroupList) {
                                    bean.put("CONTRID", contractId);
                                }
                                //
                                loadObjLists(contractId, (List<Map<String, Object>>) result.get("INSOBJGROUPLIST"), productMap, login, password);
                            }
                        }
                    }

                    String prodSysName = getStringParam(result.get("PRODSYSNAME"));
                    Map<String, Object> contrExtMap = (Map<String, Object>) result.get("CONTREXTMAP");
                    // текущая дата (дата печати документа)
                    Date todayDate = new Date();
                    result.put("TODAYDATE", todayDate);

                    // генерация дат для выкупных сумм
                    /* Long termInYears = getTermInYearsById(productMap, getLongParam(result.get("TERMID")));
                    if (termInYears != null) {
                        GregorianCalendar curSDate = new GregorianCalendar();
                        curSDate.setTime((Date) parseAnyDate(result.get("STARTDATE"), Date.class, "STARTDATE"));
                        for (int i = 1; i <= termInYears; i++) {
                            contrExtMap.put("redemptionSumYear" + String.valueOf(i) + "DateFrom", df.format(curSDate.getTime()));
                            curSDate.add(java.util.Calendar.YEAR, 1);
                            curSDate.add(java.util.Calendar.DAY_OF_YEAR, -1);
                            contrExtMap.put("redemptionSumYear" + String.valueOf(i) + "DateTo", df.format(curSDate.getTime()));
                            curSDate.add(java.util.Calendar.DAY_OF_YEAR, 1);
                        }
                    }*/
                    // разыменовка партисипентов
                    Map<String, Object> participHBMap = new HashMap<String, Object>();
                    //participHBMap.put("PROFLIST", loadHandbookData(null, "B2B.Life.Profession", login, password));
                    //participHBMap.put("KOALIST", loadHandbookData(null, "B2B.Life.KindOfActivity", login, password));
                    //participantResolveParams((Map<String, Object>) result.get("INSURERMAP")/*, participHBMap*/, login, password);
                    //participantResolveParams((Map<String, Object>) result.get("INSUREDMAP")/*, participHBMap*/, login, password);
                    // для выгодоприобретателей разыменовываем покрытия 
                    /*if (result.get("BENEFICIARYLIST") != null) {
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
                                participantResolveParams(participMap, login, password);
                            }
                            logger.debug("Resolving beneficiary finihed.");
                        }
                    }*/
                    // получение информации о сотруднике
                    /*Long createUserId = getLongParam(result.get("CREATEUSERID"));
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
                    result.put("CREATEUSERMAP", createUserMap);

                    // ПАРТНЕР
                    Long createUserDepartmentID = getLongParamLogged(createUserMap, "DEPARTMENTID");
                    // получение наименование партнера для конкретного дочернего подразделения по ИД этого дочернего подразделения
                    String partnerName = getPartnerNameByChildDepartmentID(createUserDepartmentID, "", login, password);
                    createUserMap.put("PARTNERNAME", partnerName);
                    result.put("CREATEUSERPARTNERNAME", partnerName);
                     */
                    //
                    //result.put("PAYVARSTR", getPaymentVariantNameById(prodConfMap, getLongParam(result.get("PAYVARID"))));
                    //result.put("TERMSTR", getTermMapById(prodConfMap, getLongParam(result.get("TERMID"))).get("NAME"));
                    //result.put("PRODPROGSTR", getProgramNameById(prodConfMap, getLongParam(result.get("PRODPROGID"))));
                    //result.put("INSAMCURRENCYSTR", getCurrencyNameById(prodConfMap, getLongParam(result.get("INSAMCURRENCYID"))));
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
                        String hbNameal = "B2B.InvestNum1.AssuranceLevel";
                        if (prodSysName.equalsIgnoreCase(SYSNAME_SMARTPOLICY)) {
                            hbNameal = "B2B.smartPolicy.AssuranceLevel";
                        }
                        if (prodSysName.equalsIgnoreCase(SYSNAME_SMARTPOLICY_LIGHT)) {
                            hbNameal = "B2B.smartPolicyLight.AssuranceLevel";
                        }


                        Map<String, Object> hbParams = new HashMap<String, Object>();
                        hbParams.put("hid", assuranceLevelHid);

                        List<Map<String, Object>> filteredList = loadHandbookData(hbParams, hbNameal, login, password);
                        if ((filteredList != null) && (filteredList.size() == 1)) {
                            Map<String, Object> selectedItem = filteredList.get(0);
                            contrExtMap.put("assuranceLevelSelectedItem", selectedItem);
                            contrExtMap.put("assuranceLevelStr", getStringParamLogged(selectedItem, "name"));
                        }
                    }

                    // Маяк - фонд (он же Базовый актив)
                    Map<String, Object> baseActiveMap = null;
                    Long fundHid = getLongParamLogged(contrExtMap, FUND_ID_PARAMNAME);
                    if (fundHid != null) {
                        Long fundRefTypeId = getLongParamLogged(contrExtMap, FUND_REF_TYPE_ID_PARAMNAME);
                        if (FUND_REF_TYPE_ID_PRODUCT_STRATEGY_HANDBOOK.equals(fundRefTypeId)) {
                            // Тип ссылки на фонд (fundRefTypeId): 2 - на новый справочник (B2B.SBSJ.RelationStrategyProduct)
                            baseActiveMap = processProductInvestmentStrategy(fundHid, login, password);
                            contrExtMap.put("fundStr", getStringParamLogged(baseActiveMap, "NAME"));
                        } else {
                            // Тип ссылки на фонд (fundRefTypeId): null/1 - на старый справочник (B2B.InvestNum1.Funds)
                            // пытаемся получить инвест стратегию привязанную к продукту, если не находим - выполняем старый код
                            List<Map<String, Object>> isList = getProdInvestStrategyList(prodConfId, login, password);
                            // if ((isList != null) && (!isList.isEmpty())) {
                            // если находим - новый.
                            // новый код по хид получает стратегию, базовый актив, и наполнение базового актива - тикеры.
                            // в формате: BASEACTIVEMAP(NAME,CODE,SYSNAME,TICKERLIST,COUPONSIZE,TICKERCOUNTSTR, TICKERCOUNT)
                            //            TICKERLIST[{NAME,SYSNAME,CODE,BRIEFNAME,STOCKEXCHNAME}]
                            //      Long currencyId = getLongParam(result, "INSAMCURRENCYID");
                            //     baseActiveMap = processInvestStrategy(isList, fundHid, currencyId, login, password);
                            //     contrExtMap.put("fundStr", getStringParamLogged(baseActiveMap, "NAME"));

                            //   } else {
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
                            //}
                        }
                    }
                    result.put("BASEACTIVEMAP", baseActiveMap);

                    // Маяк - проценты по показателю 'Автопилот ВНИЗ (Stop Loss)'
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

                    // генерация строковых представлений для всех дат
                    //genDateStrs(result, "*");
                }
            }
        }

        //TODO MEMBERLIST
        //TODO PARTICIPANT in MEMBERLIST
        //TODO payfactlist
        //todo paylist
        //
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListForBorderoByParamEx(Map<String, Object> params) throws Exception {
        // clear fields
        //params.remove("PRODID");
        //params.remove("PRODVERID");
        Map<String, Object> result = this.selectQuery("dsB2BContractBrowseListForBorderoByParamEx", "dsB2BContractBrowseListForBorderoByParamExCount", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractSetSignDate(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        // 1. по ид договора получаем состояние текущего договора, и дату перевода его в это состояние.
        Map<String, Object> stateParam = new HashMap<String, Object>();
        stateParam.put("CONTRID", params.get("CONTRID"));

        stateParam.put(WsConstants.LOGIN, params.get(WsConstants.LOGIN));
        stateParam.put(WsConstants.PASSWORD, params.get(WsConstants.PASSWORD));
        Map<String, Object> stateRes = this.selectQuery("dsB2BGetContractsState", "dsB2BGetContractsStateCount", stateParam);
        List<Map<String, Object>> stateResList = (List<Map<String, Object>>) stateRes.get(RESULT);
        if (stateResList != null) {
            if (stateResList.get(0) != null) {
                stateRes = stateResList.get(0);
                if (stateRes != null) {
                    if (stateRes.get("STATESYSNAME") != null) {
                        if ("B2B_CONTRACT_SG".equalsIgnoreCase(stateRes.get("STATESYSNAME").toString())) {
                            // 2. если состояние "Подписан" а дата подписания договора пустая, то проставляем ее по дате состояния.
                            if ((stateRes.get("SIGNDATE") == null) || (stateRes.get("SIGNDATE").toString().isEmpty())) {
                                Map<String, Object> contrParam = new HashMap<String, Object>();
                                contrParam.put("CONTRID", params.get("CONTRID"));
                                contrParam.put("SIGNDATE", stateRes.get("STATEDATE"));
                                contrParam.put("CURRENCYRATE", params.get("CURRENCYRATE"));
                                contrParam.put(WsConstants.LOGIN, login);
                                contrParam.put(WsConstants.PASSWORD, password);
                                result = this.callService(Constants.B2BPOSWS, "dsB2BContractUpdate", contrParam, login, password);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBorderoContentBrowseListByParamEx(Map<String, Object> params) throws Exception {
        if ((params.get("SELLERNAME") != null) && ((String) params.get("SELLERNAME")).equalsIgnoreCase("")) {
            params.put("SELLERNAME", null);
        }
        if ((params.get("CONTRNUMBER") != null) && ((String) params.get("CONTRNUMBER")).equalsIgnoreCase("")) {
            params.put("CONTRNUMBER", null);
        }
        if ((params.get("SIGNSTARTDATE") != null) && ((String) params.get("SIGNSTARTDATE").toString()).equalsIgnoreCase("")) {
            params.put("SIGNSTARTDATE", null);
        }
        if ((params.get("SIGNFINISHDATE") != null) && ((String) params.get("SIGNFINISHDATE").toString()).equalsIgnoreCase("")) {
            params.put("SIGNFINISHDATE", null);
        }
        if ((params.get("ORGSTRUCTID") != null) && ((String) params.get("ORGSTRUCTID")).equalsIgnoreCase("")) {
            params.put("ORGSTRUCTID", null);
        }
        Map<String, Object> result = this.selectQuery("dsB2BBorderoContentBrowseListByParamEx", "dsB2BBorderoContentBrowseListByParamExCount", params);
        return result;
    }

    @WsMethod(requiredParams = {"CONTRID", "USERACCOUNTID", "ORGSTRUCTNAMESLIST"})
    public Map<String, Object> dsB2BContractCreateOrgStructsByNamesAndHist(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BContractCreateOrgStructsByNamesAndHist begin");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Long contractID = getLongParam(params.get("CONTRID"));
        Long userAccountID = Long.valueOf(params.get("USERACCOUNTID").toString());
        String updateText = getStringParam(params.get("UPDATETEXT"));
        List<String> orgStructNamesList = (List<String>) params.get("ORGSTRUCTNAMESLIST");

        List<Map<String, Object>> orgStructs = new ArrayList<Map<String, Object>>();

        for (String orgStructName : orgStructNamesList) {
            Long orgStructID = null;
            orgStructID = getLongParam(orgStructIDsByName.get(orgStructName));
            if (orgStructID == null) {
                Map<String, Object> depParams = new HashMap<String, Object>();
                depParams.put(RETURN_AS_HASH_MAP, true);
                depParams.put("DEPTFULLNAME", orgStructName);
                //depParams.put("DEPTSHORTNAME", orgStructName);
                orgStructID = getLongParam(this.callServiceAndGetOneValue(Constants.ADMINWS, "admDepartment", depParams, login, password, "DEPARTMENTID"));
                orgStructIDsByName.put(orgStructName, orgStructID);
                logger.debug("Идентифкатор подразделения '" + orgStructName + "' определен, будет использоваться значение - '" + orgStructID + "'.");
            } else {
                logger.debug("Идентифкатор подразделения '" + orgStructName + "' уже был определен, будет использоваться полученное ранее значение - '" + orgStructID + "'.");
            }
            Map<String, Object> structRes = contractOrgStructCreate(contractID, orgStructID, login, password);
            if (isCallResultOK(structRes)) {
                logger.debug("Успешно назначены права на созданный договор для подразделения '" + orgStructName + "' (с идентификаторм '" + orgStructID + "').");
            } else {
                logger.debug("При назначении прав на созданный договор для подразделения '" + orgStructName + "' (с идентификаторм '" + orgStructID + "') произошла ошибка: " + structRes);
            }
            orgStructs.add(structRes);
        }

        Map<String, Object> histRes = contractOrgHistCreate(contractID, userAccountID, null, updateText, login, password);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("ORGSTRUCTLIST", orgStructs);
        result.put("CONTRORGHIST", histRes);
        //result.put("CONTRORGSTRUCTID", structRes.get("CONTRORGSTRUCTID"));
        logger.debug("dsB2BContractCreateOrgStructsByNamesAndHist end");
        return result;
    }

    @WsMethod(requiredParams = {"CONTRID", "USERACCOUNTID", "ORGSTRUCTID"})
    public Map<String, Object> dsB2BContractCreateOrgStructAndHist(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BContractCreateOrgStructAndHist begin");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Long contractId = Long.valueOf(params.get("CONTRID").toString());
        Long userAccountId = Long.valueOf(params.get("USERACCOUNTID").toString());
        Long orgStructId = Long.valueOf(params.get("ORGSTRUCTID").toString());
        String updateText = getStringParam(params.get("UPDATETEXT"));

        Map<String, Object> structRes = contractOrgStructCreate(contractId, orgStructId, login, password);
        if (isCallResultOK(structRes)) {
            logger.debug("Успешно назначены права на созданный договор для подразделения с идентификаторм '" + orgStructId + "'.");
        } else {
            logger.error("При назначении прав на созданный договор для подразделения с идентификаторм '" + orgStructId + "' произошла ошибка: " + structRes);
        }

        Map<String, Object> histRes = contractOrgHistCreate(contractId, userAccountId, orgStructId, updateText, login, password);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRORGSTRUCTID", structRes.get("CONTRORGSTRUCTID"));
        result.put("CONTRORGHISTID", histRes.get("CONTRORGHISTID"));
        logger.debug("dsB2BContractCreateOrgStructAndHist end");
        return result;
    }

    private Map<String, Object> contractOrgHistCreate(Long contractId, Long userAccountId, Long orgStructId, String updateText, String login, String password) throws Exception {
        Map<String, Object> histParams = new HashMap<String, Object>();
        histParams.put(RETURN_AS_HASH_MAP, true);
        histParams.put("CONTRID", contractId);
        histParams.put("OLDUSERID", userAccountId);
        histParams.put("NEWUSERID", userAccountId);
        histParams.put("NEWORGSTRUCTID", orgStructId);
        histParams.put("OLDORGSTRUCTID", orgStructId);
        if (!updateText.isEmpty()) {
            histParams.put("UPDATETEXT", updateText);
        } else {
            histParams.put("UPDATETEXT", "Создан договор");
        }
        Map<String, Object> histRes = this.callService(Constants.B2BPOSWS, "dsB2BContractOrgHistCreate", histParams, login, password);
        return histRes;
    }

    private Map<String, Object> contractOrgStructCreate(Long contractId, Long orgStructId, String login, String password) throws Exception {
        Map<String, Object> structParams = new HashMap<String, Object>();
        structParams.put(RETURN_AS_HASH_MAP, true);
        structParams.put("CONTRID", contractId);
        structParams.put("ORGSTRUCTID", orgStructId);
        Map<String, Object> structRes = this.callService(Constants.B2BPOSWS, "dsB2BContractOrgStructCreate", structParams, login, password);
        return structRes;
    }

    private List<Map<String, Object>> loadMembers(Long contractID, Map<String, Object> productMap, String login, String password) throws Exception {

        logger.debug("Loading insured list (MEMBERLIST)...");

        // основные параметры для получения списка застрахованных
        Map<String, Object> membersParams = new HashMap<String, Object>();
        membersParams.put("CONTRID", contractID);

        logger.debug(String.format("Looking for insured handook version in product contants (PRODDEFVALS) by value name (NAME) = '%s'...", MEMBERHBDATAVERID_PARAMNAME));
        // получение списка констант (таблица B2B_PRODDEFVAL) из запрошенных ранее сведений продукта
        List<Map<String, Object>> prodDefValList = (List<Map<String, Object>>) productMap.get("PRODDEFVALS");

        // определение идентификатора версии справочника атирбутов застрахованного
        Long memberHandbookDataVerID = null;
        if ((prodDefValList != null) && (prodDefValList.size() > 0)) {
            Map<String, Object> memberHandbookProdDefValue = (Map<String, Object>) getLastElementByAtrrValue(prodDefValList, "NAME", MEMBERHBDATAVERID_PARAMNAME);
            if (memberHandbookProdDefValue != null) {
                memberHandbookDataVerID = getLongParam(memberHandbookProdDefValue.get("VALUE"));
                logger.debug("In product contants (PRODDEFVALS) found insured handook version (HBDATAVERID): " + memberHandbookDataVerID);
                logger.debug("Insured list (MEMBERLIST) will be loaded with handbook support.");
            } else {
                logger.debug("No insured handook version (HBDATAVERID) found in product contants (PRODDEFVALS) - insured list (MEMBERLIST) will be loaded without handbook support.");
            }
        }

        String serviceName;
        String methodName;
        if (memberHandbookDataVerID != null) {
            // если указан идентифиактор справочника - загрузка списка застрахованных будет выполнятся с поддержкой дополнительных полей, указанных в справочнике
            serviceName = Constants.INSTARIFICATORWS;
            methodName = "dsHandbookRecordBrowseListByParam";
            membersParams.put("HBDATAVERID", memberHandbookDataVerID);
        } else {
            // если не указан идентифиактор справочника - загрузка списка застрахованных будет выполнятся только по основным полям таблицы B2B_MEMBER
            serviceName = Constants.B2BPOSWS;
            methodName = "dsB2BMemberBrowseListByParam";
        }

        Map<String, Object> res = this.callService(serviceName, methodName, membersParams, login, password);

        logger.debug("Loading insured list (MEMBERLIST) finished with result: " + res + "\n");

        if ((res != null) && (res.get(RESULT) != null)) {
            if (res.get(RESULT) instanceof List) {
                return (List<Map<String, Object>>) res.get(RESULT);
            }
        }

        return new ArrayList<Map<String, Object>>();

    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListByParamForReprintAndResend(Map<String, Object> params) throws Exception {
        Map<String, Object> result = null;
        result = this.selectQuery("dsB2BContractBrowseListByParamForReprintAndResend", "dsB2BContractBrowseListByParamForReprintAndResendCount", params);
        return result;
    }

    @WsMethod(requiredParams = {"SDCALCMETHOD"})
    public Map<String, Object> dsB2BContractCalcStartDate(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BContractCalcStartDate begin");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String sdCalcMethod = params.get("SDCALCMETHOD").toString();
        GregorianCalendar gcStartDate = null;
        if (sdCalcMethod.equals("2")) {
            if (params.get("DOCUMENTDATE") != null) {
                gcStartDate = new GregorianCalendar();
                gcStartDate.setTime((Date) parseAnyDate(params.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE"));
            }
        } else if (sdCalcMethod.equals("3")) {
            if (params.get("SIGNDATE") != null) {
                gcStartDate = new GregorianCalendar();
                gcStartDate.setTime((Date) parseAnyDate(params.get("SIGNDATE"), Date.class, "SIGNDATE"));
            }
        } else if (sdCalcMethod.equals("4")) {
            if (params.get("DOCUMENTDATE") != null) {
                Date resDate = addLagDaysToDate((Date) parseAnyDate(params.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE"),
                        params.get("SDLAG"), params.get("SDCALENDARTYPE"), login, password);
                if (resDate != null) {
                    gcStartDate = new GregorianCalendar();
                    gcStartDate.setTime(resDate);
                }
            }
        } else if (sdCalcMethod.equals("5")) {
            if (params.get("SIGNDATE") != null) {
                Date resDate = addLagDaysToDate((Date) parseAnyDate(params.get("SIGNDATE"), Date.class, "SIGNDATE"),
                        params.get("SDLAG"), params.get("SDCALENDARTYPE"), login, password);
                if (resDate != null) {
                    gcStartDate = new GregorianCalendar();
                    gcStartDate.setTime(resDate);
                }
            }
        } else if (sdCalcMethod.equals("6")) {
            Date resDate = getDateFromTrancheHB(new Date(), login, password);
            if (resDate != null) {
                gcStartDate = new GregorianCalendar();
                gcStartDate.setTime(resDate);
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        if (gcStartDate != null) {
            result.put("STARTDATE", parseAnyDate(gcStartDate.getTime(), Double.class, "STARTDATE"));
        }
        logger.debug("dsB2BContractCalcStartDate end");
        return result;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BContractCopyDatesToChilds(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BContractCopyDatesToChilds begin");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contrParams = new HashMap<String, Object>();
        contrParams.put(RETURN_AS_HASH_MAP, "TRUE");
        contrParams.put("CONTRID", params.get("CONTRID"));
        contrParams.put("LOADCONTRSECTION", 1L);
        Map<String, Object> contrMap = this.callService(Constants.B2BPOSWS, "dsB2BContractUniversalLoad", contrParams, login, password);
        if (contrMap != null) {
            Double startDate = null;
            if (contrMap.get("STARTDATE") != null) {
                startDate = (Double) parseAnyDate(contrMap.get("STARTDATE"), Double.class, "STARTDATE");
            }
            Double finishDate = null;
            if (contrMap.get("FINISHDATE") != null) {
                finishDate = (Double) parseAnyDate(contrMap.get("FINISHDATE"), Double.class, "FINISHDATE");
            }
            if (contrMap.get("CONTRSECTIONLIST") != null) {
                List<Map<String, Object>> contrSectionList = (List<Map<String, Object>>) contrMap.get("CONTRSECTIONLIST");
                for (Map<String, Object> sectionBean : contrSectionList) {
                    sectionBean.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                    sectionBean.put("STARTDATE", startDate);
                    sectionBean.put("FINISHDATE", finishDate);
                    if (sectionBean.get("INSOBJGROUPLIST") != null) {
                        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) sectionBean.get("INSOBJGROUPLIST");
                        for (Map<String, Object> insObjGroupBean : insObjGroupList) {
                            if (insObjGroupBean.get("OBJLIST") != null) {
                                List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupBean.get("OBJLIST");
                                for (Map<String, Object> objMap : objList) {
                                    objMap.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                                    if (objMap.get("CONTROBJMAP") != null) {
                                        Map<String, Object> contrObjMap = (Map<String, Object>) objMap.get("CONTROBJMAP");
                                        contrObjMap.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                                        contrObjMap.put("STARTDATE", startDate);
                                        contrObjMap.put("FINISHDATE", finishDate);
                                        contrObjMap.put("DURATION", contrMap.get("DURATION"));
                                        if (contrObjMap.get("CONTRRISKLIST") != null) {
                                            List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                                            for (Map<String, Object> contrRiskBean : contrRiskList) {
                                                contrRiskBean.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                                                contrRiskBean.put("STARTDATE", startDate);
                                                contrRiskBean.put("FINISHDATE", finishDate);
                                                contrRiskBean.put("DURATION", contrMap.get("DURATION"));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            List<Map<String, Object>> contrList = new ArrayList<Map<String, Object>>();
            contrList.add(contrMap);
            Map<String, Object> saveParams = new HashMap<String, Object>();
            saveParams.put("CONTRLIST", contrList);
            this.callService(Constants.B2BPOSWS, "dsB2BContractUniversalSave", saveParams, login, password);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        logger.debug("dsB2BContractCopyDatesToChilds end");
        return result;
    }

    private boolean isNewMemberModel(Map<String, Object> contrBean) {
        String prodSysName = getStringParam(contrBean.get("PRODSYSNAME"));
        if ("B2B_INVEST_NUM1".equalsIgnoreCase(prodSysName)
                || "LIGHTHOUSE".equalsIgnoreCase(prodSysName)
                || "SMART_POLICY".equalsIgnoreCase(prodSysName)
                || "SMART_POLICY_LIGHT".equalsIgnoreCase(prodSysName)
                || "B2B_INVEST_COUPON".equalsIgnoreCase(prodSysName)
                || "SMART_POLICY_RB_ILIK".equalsIgnoreCase(prodSysName)
                || "B2B_CAPITAL".equalsIgnoreCase(prodSysName)
                || "ACCELERATION_RB-FL".equalsIgnoreCase(prodSysName)
                || SYSNAME_BORROWER_PROTECT.equalsIgnoreCase(prodSysName) // "Защищенный заемщик"
                || "B2B_BORROWER_PROTECT_LONGTERM".equalsIgnoreCase(prodSysName) // "Защищенный заемщик многолетний"
                || "MORTGAGE_CLPBM".equalsIgnoreCase(prodSysName) // "Защищенный заемщик многолетний"
                || "B2B_FIRST_STEP".equalsIgnoreCase(prodSysName)
                || "FIRSTCAPITAL_RB-FCC0".equalsIgnoreCase(prodSysName)
                || "B2B_RIGHT_DECISION".equalsIgnoreCase(prodSysName)
                || "FAMALYASSETS_RB-FCC0".equalsIgnoreCase(prodSysName)
                || "B2B_NEW_HORIZONS".equalsIgnoreCase(prodSysName)
                || "BOX_RIGHT_CHOICE".equalsIgnoreCase(prodSysName)
                || "BOX_SEAT_BELT".equalsIgnoreCase(prodSysName)
                || "B2B_CARING_PARENTS".equalsIgnoreCase(prodSysName)
                || "RIGHT_CHOICE_RTBOX".equalsIgnoreCase(prodSysName)
                || "SBELT_RTBOX".equalsIgnoreCase(prodSysName)) {
            return true;
        }
        return false;
    }

    private void processInsurer(Map<String, Object> contrBean, String login, String password) throws Exception {
        // сохранить INSURERMAP в CRM
        boolean isNeedUpdateContr = false;
        String dataKeyName = "INSURERMAP";
        String idKeyName = "INSURERID";
        Object participant = contrBean.get(dataKeyName);
        if (participant != null) {
            Object savedParticipant = saveParticipant((Map<String, Object>) participant, login, password);
            if (contrBean.get(idKeyName) == null) {
                // обновление идентификатора участника в самом договоре
                contrBean.put(idKeyName, ((Map<String, Object>) savedParticipant).get("PARTICIPANTID"));
                isNeedUpdateContr = true;
            }
            // полная замена данных участника - savedParticipant возвращает их все целиком
            contrBean.put(dataKeyName, savedParticipant);
        }
        // обновить insurerid в договоре.
        // обновление договора (при изменении идентификаторов участников в самом договоре)
        if (isNeedUpdateContr) {
            this.callService(Constants.B2BPOSWS, "dsB2BContractUpdate", contrBean, login, password);
        }
        // добавить запись в мембер листе (если в договоре стоит флаг "Страхователь является застрахованным", то проставить сиснейм соответствующий.
        String memberTypeSysName = "insurer";
        processMember(contrBean, getLongParam(contrBean.get(idKeyName)), memberTypeSysName, login, password);
    }

    private void processInsured(Map<String, Object> contrBean, String login, String password) throws Exception {
        // сохранить INSURERMAP в CRM
        String dataKeyName = "INSUREDMAP";
        String idKeyName = "INSUREDID";

        if (contrBean.get(dataKeyName) != null) {
            boolean insurerIsInsured = false;
            Map<String, Object> participant = (Map<String, Object>) contrBean.get(dataKeyName);
            Map<String, Object> contrExtMap = (Map<String, Object>) contrBean.get("CONTREXTMAP");
            // если contrBean.CONTREXTMAP.insurerIsInsured = 1 а в insuredMap отсутствует participantid снова проставить мапу застрахованного
            if (contrExtMap != null) {
                if (contrExtMap.get("insurerIsInsured") != null) {
                    if ("1".equals(getStringParam(contrExtMap.get("insurerIsInsured")))) {
                        insurerIsInsured = true;
                        if (getLongParam(participant, "PARTICIPANTID") == null) {
                            participant = (Map<String, Object>) contrBean.get("INSURERMAP");
                        }
                    }
                }
            }
            Object savedParticipant = participant;
            if (!insurerIsInsured) {
                savedParticipant = saveParticipant(participant, login, password);
            }
            if (contrBean.get(idKeyName) == null) {
                // обновление идентификатора участника в самом договоре
                contrBean.put(idKeyName, ((Map<String, Object>) savedParticipant).get("PARTICIPANTID"));
            }
            // полная замена данных участника - savedParticipant возвращает их все целиком
            contrBean.put(dataKeyName, savedParticipant);
        }
        // добавить запись в мембер листе (если в договоре стоит флаг "Страхователь является застрахованным", то проставить сиснейм соответствующий.
        String memberTypeSysName = "insured";
        processMember(contrBean, getLongParam(contrBean, idKeyName), memberTypeSysName, login, password);
    }

    private void processBeneficiaryList(Map<String, Object> contrBean, String login, String password) throws Exception {
        Object beneficiaryList = contrBean.get(BENEFICIARY_LIST_KEY_NAME);
        if ((beneficiaryList != null) && (beneficiaryList instanceof List)) {
            List<Map<String, Object>> benefList = (List<Map<String, Object>>) beneficiaryList;

            if (IS_BEN_CALLS_VERBOSE_LOGGING) {
                logger.debug("Beneficiaries list: ");
                for (Map<String, Object> ben : benefList) {
                    logger.debug("Beneficiary params: ");
                    for (Map.Entry<String, Object> entry : ben.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        logger.debug("     " + key + " = " + value);
                    }
                }
            }

            List<Map<String, Object>> processedBenefList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> benefMap : benefList) {

                Long benefID = getLongParamLogged(benefMap, "BENEFICIARYID");
                Long benefTypeID = getLongParamLogged(benefMap, "TYPEID");

                // установка флагов по ROWSTATUS
                boolean isNeedSave = false; // флаг сохранения (создание или обновление - в зависимости от наличия ИД)
                boolean isNeedRemove = false; // флаг удаления (при наличии ИД - удаление из БД и затем из списка, при отсутствии - только из списка)
                Long rowStatus = getLongParamLogged(benefMap, "ROWSTATUS");
                if (rowStatus != null) {
                    if (DELETED_ID == rowStatus.intValue()) {
                        // удаление
                        isNeedRemove = true;
                    } else if ((MODIFIED_ID == rowStatus.intValue()) && (benefID != null)) {
                        // передан флаг обновления и есть ИД - обновление
                        isNeedSave = true;
                    } else if ((INSERTED_ID == rowStatus.intValue()) && (benefID == null)) {
                        // флаг создания, ИД отсутствует - создание
                        isNeedSave = true;
                    }
                } else {
                    // ROWSTATUS отсутствует, но есть ИД - обновление
                    // ROWSTATUS отсутствует, ИД отсутствует - создание
                    isNeedSave = true;
                }

                if (!BENEFICIARY_BY_LAW_TYPEID.equals(benefTypeID)) {
                    // обработка партисипанта (crm) - требуется для всех случаев, кроме 'По закону'
                    String dataKeyName = "PARTICIPANTMAP";
                    String idKeyName = "PARTICIPANTID";
                    if (benefMap.get(dataKeyName) == null) {
                        if (BENEFICIARY_INSURED_TYPEID.equals(benefTypeID)) {
                            benefMap.put(dataKeyName, contrBean.get("INSUREDMAP"));
                        }
                        if (BENEFICIARY_INSURER_TYPEID.equals(benefTypeID)) {
                            benefMap.put(dataKeyName, contrBean.get("INSURERMAP"));
                        }
                    }
                    if (benefMap.get(dataKeyName) != null) {
                        Map<String, Object> participant = (Map<String, Object>) benefMap.get(dataKeyName);
                        if (BENEFICIARY_INSURED_TYPEID.equals(benefTypeID)) {
                            if (participant.get("PARTICIPANTID") == null) {
                                participant = (Map<String, Object>) contrBean.get("INSUREDMAP");
                            } else {
                                Map<String, Object> tmpPart = (Map<String, Object>) contrBean.get("INSUREDMAP");
                                if (getStringParam(participant, "PARTICIPANTID").equalsIgnoreCase(getStringParam(tmpPart, "PARTICIPANTID"))) {
                                    participant = tmpPart;
                                }
                            }
                        }
                        if (BENEFICIARY_INSURER_TYPEID.equals(benefTypeID)) {
                            if (participant.get("PARTICIPANTID") == null) {
                                participant = (Map<String, Object>) contrBean.get("INSURERMAP");
                            } else {
                                Map<String, Object> tmpPart = (Map<String, Object>) contrBean.get("INSUREDMAP");
                                if (getStringParam(participant, "PARTICIPANTID").equalsIgnoreCase(getStringParam(tmpPart, "PARTICIPANTID"))) {
                                    participant = tmpPart;
                                }
                            }
                        }

                        Object savedParticipant = saveParticipant(participant, login, password);
                        if (benefMap.get(idKeyName) == null) {
                            // обновление идентификатора партисипанта в самом выгодоприобретателе
                            benefMap.put(idKeyName, ((Map<String, Object>) savedParticipant).get("PARTICIPANTID"));
                            isNeedSave = true;
                        }
                        // полная замена данных партисипанта - savedParticipant возвращает их все целиком
                        benefMap.put(dataKeyName, savedParticipant);
                    }

                    // обработка мембера - требуется только если имеется ИД crm-лица (партисипанта)
                    Long participantID = getLongParamLogged(benefMap, idKeyName);
                    if (participantID != null) {
                        // добавить запись в мембер листе (если в договоре стоит флаг "Страхователь является застрахованным", то проставить сиснейм соответствующий.
                        String memberTypeSysName = "beneficiary";
                        String memberTypeSysNameNew = "beneficiary";
                        if (isNeedRemove) {
                            // затираем
                            memberTypeSysNameNew = "";
                        }
                        Object memberIDObj = processMemberEx(contrBean, participantID, memberTypeSysName, memberTypeSysNameNew, login, password);
                        Long memberID = getLongParam(memberIDObj); // ИД мембера из обработки
                        Long benefMapMemberID = getLongParam(benefMap, "MEMBERID"); // ИД мембера из выгодоприобретателя
                        if ((benefMapMemberID == null) || (!benefMapMemberID.equals(memberID))) {
                            // если ИД изменился - обновление идентификатора мембера в самом выгодоприобретателе
                            benefMap.put("MEMBERID", memberID);
                            isNeedSave = true;
                        }
                    }

                }

                if (isNeedRemove) {
                    if (benefID != null) {
                        this.callService(Constants.B2BPOSWS, "dsB2BBeneficiaryDelete", benefMap, IS_BEN_CALLS_VERBOSE_LOGGING, login, password);
                    } else {
                        // если на этом этапе ид нет, то бенефициар в базе отсутствует. и был помечен как удаленный на интерфейсе.
                        // значит его просто пропускаем.
                    }
                } else {
                    if (isNeedSave) {
                        if (benefID == null) {
                            benefMap.put("CONTRID", contrBean.get("CONTRID"));
                            Object benefId = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BBeneficiaryCreate", benefMap, IS_BEN_CALLS_VERBOSE_LOGGING, login, password, "BENEFICIARYID");
                            if (benefId != null) {
                                benefMap.put("BENEFICIARYID", benefId);
                                benefMap.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID); // запись обработана и теперь совпадает с содержимым в БД
                            }
                        } else {
                            this.callService(Constants.B2BPOSWS, "dsB2BBeneficiaryUpdate", benefMap, IS_BEN_CALLS_VERBOSE_LOGGING, login, password);
                            benefMap.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID); // запись обработана и теперь совпадает с содержимым в БД
                        }
                    }
                    processedBenefList.add(benefMap);
                }

            }

            contrBean.put(BENEFICIARY_LIST_KEY_NAME, processedBenefList);

        }
    }

    private void removeDublicateMember(Map<String, Object> contrBean, String login, String password) throws Exception {
        Object members = contrBean.get("MEMBERLIST");
        if ((members != null) && (members instanceof List)) {
            List<Map<String, Object>> memberList = (List<Map<String, Object>>) members;
            List<Map<String, Object>> delMemList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> memMap : memberList) {
                String type = getStringParam(memMap.get("TYPESYSNAME"));
                Long partId = getLongParam(memMap.get("PARTICIPANTID"));
                Long memberid = getLongParam(memMap.get("MEMBERID"));
                if ((memMap.get("ROWSTATUS") == null) || (!memMap.get("ROWSTATUS").toString().equals("3"))) {
                    for (Map<String, Object> curMemMap : memberList) {
                        String typeCur = getStringParam(curMemMap.get("TYPESYSNAME"));
                        Long partIdCur = getLongParam(curMemMap.get("PARTICIPANTID"));
                        Long memberidCur = getLongParam(memMap.get("MEMBERID"));
                        if (!memberid.equals(memberidCur)) {
                            if ((typeCur.equals(type)) && (partIdCur.equals(partId))) {
                                if ((curMemMap.get("ROWSTATUS") == null) || (!curMemMap.get("ROWSTATUS").toString().equals("3"))) {
                                    curMemMap.put("ROWSTATUS", DELETED_ID);
                                }
                            }
                        }
                    }
                }
            }

            sortByRowStatus(memberList, null, null, delMemList, null);
            for (Map<String, Object> memMap : delMemList) {
                this.callService(Constants.B2BPOSWS, "dsB2BMemberDelete", memMap, login, password);
                memberList.remove(memMap);
            }
        }
    }

    private Object processMember(Map<String, Object> contrBean, Long participantId, String memberTypeSysName, String login, String password) throws Exception {
        return processMemberEx(contrBean, participantId, memberTypeSysName, memberTypeSysName, login, password);
    }

    private Object processMemberEx(Map<String, Object> contrBean, Long participantId, String memberTypeSysName, String memberTypeSysNameNew, String login, String password) throws Exception {
        Object result = null;
        if (participantId != null) {
            Object members = contrBean.get("MEMBERLIST");
            if (members == null) {
                members = new ArrayList<Map<String, Object>>();
                contrBean.put("MEMBERLIST", members); // для возврата MEMBERLIST в составе мапы договора из сохранения
            }
            if ((members != null) && (members instanceof List)) {
                List<Map<String, Object>> memberList = (List<Map<String, Object>>) members;
                boolean isNeedCreate = true;
                for (Map<String, Object> memberMap : memberList) {
                    String memberType = getStringParam(memberMap.get("TYPESYSNAME"));
                    Long partId = getLongParam(memberMap.get("PARTICIPANTID"));
                    if ((partId.equals(participantId)) && memberType.equalsIgnoreCase(memberTypeSysName)) {
                        if (isNeedCreate) {
                            if (!"3".equals(getStringParam(memberMap.get("ROWSTATUS")))) {
                                isNeedCreate = false;
                                if (memberMap.get("MEMBERID") != null) {
                                    result = memberMap.get("MEMBERID");
                                    if (!memberTypeSysName.equals(memberTypeSysNameNew)) {
                                        memberMap.put("CONTRID", contrBean.get("CONTRID"));
                                        memberMap.put("TYPESYSNAME", memberTypeSysNameNew);
                                        this.callService(Constants.B2BPOSWS, "dsB2BMemberUpdate", memberMap, login, password);
                                    }
                                } else {
                                    memberMap.put("CONTRID", contrBean.get("CONTRID"));
                                    memberMap.put("TYPESYSNAME", memberTypeSysNameNew);

                                    result = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BMemberCreate", memberMap, login, password, "MEMBERID");
                                }
                            }
                        } else {
                            // если мы тут, то по данной паре partID typesysname найден дубликат. помечаем его к удалению.
                            memberMap.put("ROWSTATUS", DELETED_ID);
                        }
                    }
                }
                if (isNeedCreate) {
                    Map<String, Object> memberMap = new HashMap<String, Object>();
                    memberMap.put("CONTRID", contrBean.get("CONTRID"));
                    memberMap.put("PARTICIPANTID", participantId);
                    memberMap.put("TYPESYSNAME", memberTypeSysNameNew);
                    result = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BMemberCreate", memberMap, login, password, "MEMBERID");
                    memberMap.put("MEMBERID", result);
                    // member создан в базе и поскольку он существует проставляаем ROWSTATUS
                    memberMap.put("ROWSTATUS", UNMODIFIED_ID);
                    memberList.add(memberMap);
                }
            }
        }
        return result;
    }

    private List<Map<String, Object>> loadBeneficiary(Long contractId, Map<String, Object> productMap, String login, String password) throws Exception {
        logger.debug("Loading beneficiaryList ...");

        // основные параметры для получения списка застрахованных
        Map<String, Object> membersParams = new HashMap<String, Object>();
        membersParams.put("CONTRID", contractId);
        Map<String, Object> res = this.callService(Constants.B2BPOSWS, "dsB2BBeneficiaryBrowseListByParam", membersParams, login, password);

        logger.debug("Loading beneficiaryList finished with result: " + res + "\n");

        if ((res != null) && (res.get(RESULT) != null)) {
            if (res.get(RESULT) instanceof List) {
                return (List<Map<String, Object>>) res.get(RESULT);
            }
        }

        return new ArrayList<Map<String, Object>>();
    }

    private boolean checkNeedUw(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        // для теста всегда требуем андеррайтинг. позже сделать проверку по contrid
        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
        //получаем имя препаре метода
        Map<String, Object> prodMap = (Map<String, Object>) contrMap.get("PRODUCTMAP");
        String prepMethodName = getStringParam(prodMap.get("PREPARETOSAVEMETHOD"));
        String uwCheckMethodName = "";
        if (prepMethodName.isEmpty()) {
            // сервисы не зареганы.
            //для теста условие по prodsysname
            if ("B2B_RIGHT_DECISION".equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BRightDecisionContractUnderwritingCheck";
            }
            if ("FAMALYASSETS_RB-FCC0".equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BRightDecisionContractUnderwritingCheck";
            }
            if ("B2B_FIRST_STEP".equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BFirstStepContractUnderwritingCheck";
            }
            if ("FIRSTCAPITAL_RB-FCC0".equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BFirstStepContractUnderwritingCheck";
            }
            if ("B2B_CAPITAL".equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BCapitalContractUnderwritingCheck";
            }
            if ("ACCELERATION_RB-FL".equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BCapitalContractUnderwritingCheck";
            }
            if ("B2B_INVEST_NUM1".equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BInvestNum1ContractUnderwritingCheck";
            }
            if ("LIGHTHOUSE".equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BInvestNum1ContractUnderwritingCheck";
            }
            if ("B2B_INVEST_COUPON".equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BInvestCouponContractUnderwritingCheck";
            }
            if ("SMART_POLICY_RB_ILIK".equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BInvestCouponContractUnderwritingCheck";
            }
            // "Защищенный заемщик"
            if (SYSNAME_BORROWER_PROTECT.equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BBorrowerProtectContractUnderwritingCheck";
            }
            // "Защищенный заемщик многолетний"
            if (SYSNAME_BORROWER_PROTECT_LONGTERM.equalsIgnoreCase(getStringParam(contrMap.get("PRODSYSNAME")))) {
                uwCheckMethodName = "dsB2BBorrowerProtectLongTermContractUnderwritingCheck";
            }

        } else if (prepMethodName.indexOf("PrepareToSave") > 0) {
            String prepMehtodPrefix = prepMethodName.substring(0, prepMethodName.indexOf("PrepareToSave"));
            uwCheckMethodName = prepMehtodPrefix + "UnderwritingCheck";
        } else if (prepMethodName.indexOf("UnderwritingCheck") > 0) {
            uwCheckMethodName = prepMethodName;
        }
        if (uwCheckMethodName.isEmpty()) {
            //
            logger.debug("UnderwritingCheck method not fount");
            return false;
        } else {
            params.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> checkRes = this.callService(Constants.B2BPOSWS, uwCheckMethodName, params, login, password);
            if (checkRes.get("UW") != null) {
                if ("0".equals(checkRes.get("UW").toString())) {
                    logger.debug("uw not need contrid = " + contrMap.get("CONTRID").toString());
                    return false;
                } else {
                    logger.debug("NEED UW; contrid = " + contrMap.get("CONTRID").toString());
                    return true;
                }
            }
            return false;
        }

    }

    private void sendNotificationToUW(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
        Map<String, Object> prodMap = (Map<String, Object>) contrMap.get("PRODUCTMAP");
        contrMap.put("PRODCONFID", prodMap.get("PRODCONFID"));
        contrMap.put("PRODUCTNAME", prodMap.get("NAME"));
        // имя ключа для PRODDEFVAL-а, хранящего путь до html-шаблона письма
        //contrMap.put("HTMLMAILPATHKEYNAME", "HTMLMAILPATHUW"); // необязательный параметр, значение совпадает с используемым по умолчанию
        // основной фрагмент темы письма
        //contrMap.put("MAILSUBJECTMAINTEXT", ""); // необязательный параметр, значение совпадает с используемым по умолчанию
        /* ExternalService external = ExternalService.createInstance();
        try {
            logger.debug("call dsB2BSendUWNotificationEMail async");
            external.callExternalServiceAsync(Constants.SIGNB2BPOSWS, "dsB2BSendUWNotificationEMail", contrMap, login, password);
        } catch (Exception ex) {
            logger.error("Error calldsB2BSendUWNotificationEMail", ex);
        }*/
        Map<String, Object> res = this.callService(Constants.SIGNB2BPOSWS, "dsB2BSendUWNotificationEMail", contrMap, login, password);

//        Map<String, Object> res = this.callService(Constants.SIGNB2BPOSWS, "dsB2BSendUWNotificationEMail", contrMap, login, password);
    }

    private void sendNotificationToSeller(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
        Map<String, Object> prodMap = (Map<String, Object>) contrMap.get("PRODUCTMAP");
        contrMap.put("PRODCONFID", prodMap.get("PRODCONFID"));
        contrMap.put("PRODUCTNAME", prodMap.get("NAME"));
        // имя ключа для PRODDEFVAL-а, хранящего путь до html-шаблона письма
        contrMap.put("HTMLMAILPATHKEYNAME", params.get("HTMLMAILPATHKEYNAME")); // необязательный параметр, значение совпадает с используемым по умолчанию
        // основной фрагмент темы письма
        contrMap.put("MAILSUBJECTMAINTEXT", params.get("MAILSUBJECTMAINTEXT")); // необязательный параметр, значение совпадает с используемым по умолчанию
        Map<String, Object> res = this.callService(Constants.SIGNB2BPOSWS, "dsB2BSendSellerNotificationEMail", contrMap, login, password);

//        Map<String, Object> res = this.callService(Constants.SIGNB2BPOSWS, "dsB2BSendUWNotificationEMail", contrMap, login, password);
    }

    @WsMethod(requiredParams = {"CONTRID", "CONTREXTID", "CONTREXTHBDATAVERID"})
    public Map<String, Object> dsB2BContractUWNotifyAdditionalDocsAttached(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BContractUWNotifyAdditionalDocsAttached...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();

        logger.debug("Sending notification about additional docs attach finish to underwriter...");
        Long contractID = getLongParamLogged(params, "CONTRID");
        Map<String, Object> notificationParams = new HashMap<String, Object>();
        // ИД договора
        notificationParams.put("CONTRID", contractID);
        // имя ключа для PRODDEFVAL-а, хранящего путь до html-шаблона письма
        notificationParams.put("HTMLMAILPATHKEYNAME", "HTMLMAILPATHUWADDDOCS");
        // основной фрагмент темы письма
        //notificationParams.put("MAILSUBJECTMAINTEXT", "- завершено прикрепление дополнительных документов");
        notificationParams.put("MAILSUBJECTMAINTEXT", "ДОСЫЛ_"); // по документу "Описание бизнес-процесса обработки заявки из Front"
        notificationParams.put(RETURN_AS_HASH_MAP, true);

        Map<String, Object> sendResult = this.callService(Constants.SIGNB2BPOSWS, "dsB2BSendUWNotificationEMail", notificationParams, true, login, password);
        //Map<String, Object> sendResult = this.callService(Constants.B2BPOSWS, "dsB2BSendUWNotificationEMail", notificationParams, true, login, password); // !только для отладки!
        logger.debug("Sending notification about additional docs to underwriter finished with result: " + sendResult);
        if (sendResult != null) {
            Map<String, Object> sendRes = (Map<String, Object>) sendResult.get("EMAILRES");
            String sendResStr = getStringParamLogged(sendRes, "Result");
            if ("Ok".equals(sendResStr)) {
                logger.debug("Sending notification about additional docs to underwriter finished successfully - updating contract extended value 'isUWRequiredAdditionalDocs' to '0'...");
                Map<String, Object> contrExtMap = new HashMap<String, Object>();
                contrExtMap.put("CONTREXTID", getLongParamLogged(params, "CONTREXTID"));
                contrExtMap.put("HBDATAVERID", getLongParamLogged(params, "CONTREXTHBDATAVERID"));
                contrExtMap.put("CONTRID", contractID);
                contrExtMap.put("isUWRequiredAdditionalDocs", 0L);
                updateContractValues(contrExtMap, login, password);
                logger.debug("Updating contract extended value 'isUWRequiredAdditionalDocs' to '0' finished.");
                result.put("EMAILRES", sendRes);
            }
        }

        logger.debug("dsB2BContractUWNotifyAdditionalDocsAttached finished with result: " + result);
        return result;

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

    @WsMethod(requiredParams = {"CALCMETHOD", "DATEKEYNAME"})
    public Map<String, Object> dsB2BContractCalcLaggedDate(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BContractCalcLaggedDate begin");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String dateKeyName = getStringParamLogged(params, "DATEKEYNAME");
        String sdCalcMethod = params.get("CALCMETHOD").toString();
        GregorianCalendar gcStartDate = null;
        if (sdCalcMethod.equals("2")) {
            if (params.get("DOCUMENTDATE") != null) {
                gcStartDate = new GregorianCalendar();
                gcStartDate.setTime((Date) parseAnyDate(params.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE"));
            }
        } else if (sdCalcMethod.equals("3")) {
            if (params.get("SIGNDATE") != null) {
                gcStartDate = new GregorianCalendar();
                gcStartDate.setTime((Date) parseAnyDate(params.get("SIGNDATE"), Date.class, "SIGNDATE"));
            }
        } else if (sdCalcMethod.equals("4")) {
            if (params.get("DOCUMENTDATE") != null) {
                Date resDate = addLagDaysToDate((Date) parseAnyDate(params.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE"),
                        params.get("LAG"), params.get("CALENDARTYPE"), login, password);
                if (resDate != null) {
                    gcStartDate = new GregorianCalendar();
                    gcStartDate.setTime(resDate);
                }
            }
        } else if (sdCalcMethod.equals("5")) {
            if (params.get("SIGNDATE") != null) {
                Date resDate = addLagDaysToDate((Date) parseAnyDate(params.get("SIGNDATE"), Date.class, "SIGNDATE"),
                        params.get("LAG"), params.get("CALENDARTYPE"), login, password);
                if (resDate != null) {
                    gcStartDate = new GregorianCalendar();
                    gcStartDate.setTime(resDate);
                }
            }
        } else if (sdCalcMethod.equals("6")) {
            Date resDate = getDateFromTrancheHB(new Date(), login, password);
            if (resDate != null) {
                gcStartDate = new GregorianCalendar();
                gcStartDate.setTime(resDate);
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        if (gcStartDate != null) {
            result.put(dateKeyName, parseAnyDate(gcStartDate.getTime(), Double.class, dateKeyName));
        }
        logger.debug("dsB2BContractCalcLaggedDate end");
        return result;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BGetPartnerFromContract(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BGetPartnerFromContract begin");
        Map<String, Object> result = this.selectQuery("dsB2BGetPartnerFromContract", "dsB2BGetPartnerFromContractCount", params);
        logger.debug("dsB2BGetPartnerFromContract end");
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
                    if (getLongParam(termMap.get("TERMID")).longValue() == termId.longValue()) {
                        return getLongParam(termMap.get("YEARCOUNT"));
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
                    if (getLongParam(termMap.get("TERMID")).longValue() == termId.longValue()) {
                        return termMap;
                    }
                }
            }
        }
        return null;
    }

    private String getPaymentVariantNameById(Map<String, Object> productMap, Long paymentVariantId) {
        Map<String, Object> prodVerMap = (Map<String, Object>) productMap.get("PRODVER");
        List<Map<String, Object>> prodPayVarList = (List<Map<String, Object>>) prodVerMap.get("PRODPAYVARS");
        if (prodPayVarList != null) {
            for (Map<String, Object> bean : prodPayVarList) {
                Map<String, Object> payVarMap = (Map<String, Object>) bean.get("PAYVAR");
                if (getLongParam(payVarMap.get("PAYVARID")).longValue() == paymentVariantId.longValue()) {
                    return getStringParam(payVarMap.get("NAME"));
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
                if (getLongParam(bean.get("PRODPROGID")).longValue() == programId.longValue()) {
                    return getStringParam(bean.get("NAME"));
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
                    if (prodInfoCurrencyID != null) {
                        if (prodInfoCurrencyID.equals(currencyID)) {
                            return getStringParam(currencyMap.get("CURRENCYNAME"));
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

    protected List<Map<String, Object>> loadHandbookData(Map<String, Object> params, String hbName, String login, String password) throws Exception {
        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("HANDBOOKNAME", hbName);
        hbParams.put("HANDBOOKDATAPARAMS", params);
        hbParams.put(RETURN_LIST_ONLY, true);
        Map<String, Object> resultMap = this.callServiceTimeLogged(B2BPOSWS, "dsB2BHandbookDataBrowseByHBName", hbParams, login, password);
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
            Map<String, Object> resultMap = this.callServiceTimeLogged(B2BPOSWS, "dsB2BHandbookDataBrowseByHBName", hbParams, login, password);
            hbDataList = WsUtils.getListFromResultMap(resultMap);
            hbCache.put(hbName, hbDataList);
        }
        return hbDataList;
    }

    protected Map<String, Object> getTermDataByTermID(Long termID, String login, String password) throws Exception {
        // получение сведений о сроке страхования
        Map<String, Object> termParams = new HashMap<String, Object>();
        termParams.put("TERMID", termID);
        termParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> termInfo = this.callServiceTimeLogged(B2BPOSWS, "dsB2BTermBrowseListByParam", termParams, login, password);
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
                finishDateGC.add(java.util.Calendar.YEAR, termYearCount.intValue());
            }
            if (termMonthCount != null) {
                finishDateGC.add(java.util.Calendar.MONTH, termMonthCount.intValue());
            }
            if (termDayCount != null) {
                finishDateGC.add(java.util.Calendar.DAY_OF_YEAR, termDayCount.intValue());
            }
            contract.put("FINISHDATE", finishDateGC.getTime());
        }
    }

    private void participantResolveParams(Map<String, Object> participantMap, /*Map<String, Object> participHBMap,*/ String login, String password) throws Exception {
        if (participantMap != null) {
            resolveGender(participantMap, "GENDER");
            Long citizenship = getLongParamLogged(participantMap, "CITIZENSHIP");
            String citizenshipStr = "";
            if (citizenship != null) {
                if (citizenship.equals(0L)) {
                    citizenshipStr = "Российская федерация";
                } else if (citizenship.equals(1000L)) {
                    citizenshipStr = "Иностранный гражданин";
                }
            }
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

    private Map<String, Object> callCalcAgentCommissPrem(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
        //1. получить ид агентского договора, ид калькулятора комиссии, имя метода вызова калькулятора
        Map<String, Object> agentCalcParams = new HashMap<String, Object>();
        agentCalcParams.put("CONTRID", contrMap.get("CONTRID"));
        Date todayDate = new Date();
        agentCalcParams.put("SIGNDATE", todayDate);
        parseDates(agentCalcParams, Double.class);
        // todo: убедится, что сигн дате к этому моменту проставлен.
        //agentCalcParams.put("SIGNDATE", contrMap.get("SIGNDATE"));
        //
        Map<String, Object> agentCalcRes = this.callService(B2BPOSWS, "dsB2BMainActContrBrowseByContr", agentCalcParams, login, password);
        if (agentCalcRes != null) {
            if (agentCalcRes.get(RESULT) != null) {
                List<Map<String, Object>> agentResList = (List<Map<String, Object>>) agentCalcRes.get(RESULT);
                if (!agentResList.isEmpty()) {
                    // договор страхования продан по агентскому договору. нескольких агентских договоров одновременно действующих не может быть на одном агенте.
                    Map<String, Object> agentRes = agentResList.get(0);
                    String calcMethodName = getAgentCalcMethodName(agentRes, login, password);
                    params.put("CALCVERID", agentRes.get("CALCVERID"));
                    params.put("MAINACTCONTRID", agentRes.get("MAINACTCONTRID"));
                    Map<String, Object> calcRes = this.callService(B2BPOSWS, calcMethodName, params, login, password);
                    return calcRes;
                }
            }
        }
        return null;
    }

    private String getAgentCalcMethodName(Map<String, Object> agentRes, String login, String password) throws Exception {
        if (agentRes.get("CALCVERID") != null) {
            Map<String, Object> calcConstParams = new HashMap<String, Object>();
            calcConstParams.put("CALCVERID", agentRes.get("CALCVERID"));
            calcConstParams.put("NAME", "calcMethod");
            calcConstParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> calcConstRes = this.callService(INSTARIFICATORWS, "dsCalculatorConstBrowseListByParam", calcConstParams, login, password);
            if (calcConstRes.get("STRINGVALUE") != null) {
                return calcConstRes.get("STRINGVALUE").toString();
            }

        }
        return "";
    }

    protected void processContractRedemptionSum(Long contractId, List<Map<String, Object>> redemptionSumList, String login, String password) throws Exception {
        //delete all redemptiondata by contrId
        Map<String, Object> deleteParams = new HashMap<>();
        deleteParams.put("CONTRID", contractId);
        deleteParams.put("DISCRIMINATOR", 10L);
        this.callService(B2BPOSWS, "dsB2BContractAmountPremiumDeleteByContrID", deleteParams, login, password);

        List<Map<String, Object>> inserted = new ArrayList<>();
        List<Map<String, Object>> modified = new ArrayList<>();
        List<Map<String, Object>> deleted = new ArrayList<>();
        List<Map<String, Object>> unModified = new ArrayList<>();
        this.sortByRowStatus(redemptionSumList, inserted, modified, deleted, unModified);
        logger.debug("processContractRedemtionSum inserted begin");
        for (Map<String, Object> bean : inserted) {
            Map<String, Object> params = new HashMap<>();
            params.put("CONTRID", contractId);
            params.putAll(bean);
            this.callService(B2BPOSWS, "dsB2BContractRedemptionCreate", params, login, password);
        }
        logger.debug("processContractRedemtionSum inserted end");
        logger.debug("processContractRedemtionSum modified begin");
        for (Map<String, Object> bean : modified) {
            Map<String, Object> params = new HashMap<>();
            params.putAll(bean);
            this.callService(B2BPOSWS, "dsB2BContractRedemptionUpdate", params, login, password);
        }
        logger.debug("processContractRedemtionSum modified end");
        logger.debug("processContractRedemtionSum deleted begin");
        for (Map<String, Object> bean : deleted) {
            Map<String, Object> params = new HashMap<>();
            params.put("CONTRAMPREMID", bean.get("CONTRAMPREMID"));
            this.callService(B2BPOSWS, "dsB2BContractRedemptionDelete", params, login, password);
        }
        logger.debug("processContractRedemtionSum deleted end");
    }

    protected void processContractSavingSchedule(Long contractId, List<Map<String, Object>> savingScheduleList, String login, String password) throws Exception {
        //delete all redemptiondata by contrId
        Map<String, Object> deleteParams = new HashMap<>();
        deleteParams.put("CONTRID", contractId);
        deleteParams.put("DISCRIMINATOR", 20L);
        this.callService(B2BPOSWS, "dsB2BContractAmountPremiumDeleteByContrID", deleteParams, login, password);

        List<Map<String, Object>> inserted = new ArrayList<>();
        List<Map<String, Object>> modified = new ArrayList<>();
        List<Map<String, Object>> deleted = new ArrayList<>();
        List<Map<String, Object>> unModified = new ArrayList<>();
        this.sortByRowStatus(savingScheduleList, inserted, modified, deleted, unModified);
        logger.debug("processContractSavingSchedule inserted begin");
        for (Map<String, Object> bean : inserted) {
            Map<String, Object> params = new HashMap<>();
            params.put("CONTRID", contractId);
            params.putAll(bean);
            this.callService(B2BPOSWS, "dsB2BSavingsScheduleCreate", params, login, password);
        }
        logger.debug("processContractSavingSchedule inserted end");
        logger.debug("processContractSavingSchedule modified begin");
        for (Map<String, Object> bean : modified) {
            Map<String, Object> params = new HashMap<>();
            params.putAll(bean);
            this.callService(B2BPOSWS, "dsB2BSavingsScheduleUpdate", params, login, password);
        }
        logger.debug("processContractSavingSchedule modified end");
        logger.debug("processContractSavingSchedule deleted begin");
        for (Map<String, Object> bean : deleted) {
            Map<String, Object> params = new HashMap<>();
            params.put("CONTRAMPREMID", bean.get("CONTRAMPREMID"));
            this.callService(B2BPOSWS, "dsB2BSavingsScheduleDelete", params, login, password);
        }
        logger.debug("processContractSavingSchedule deleted end");
    }

    private List<Map<String, Object>> loadRedemptions(Long contractId, Map<String, Object> productMap, String login, String password) throws Exception {
        logger.debug("Loading insured list (loadRedemptions)...");
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("CONTRID", contractId);
        String serviceName = Constants.B2BPOSWS;
        String methodName = "dsB2BContractRedemptionBrowseListByParam";
        Map<String, Object> res = this.callService(serviceName, methodName, queryParams, login, password);
        logger.debug("Loading list (RedemptionsList) finished with result: " + res + "\n");
        if ((res != null) && (res.get(RESULT) != null)) {
            if (res.get(RESULT) instanceof List) {
                return (List<Map<String, Object>>) res.get(RESULT);
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    private List<Map<String, Object>> loadSavingSchedule(Long contractId, Map<String, Object> productMap, String login, String password) throws Exception {
        logger.debug("Loading insured list (MEMBERLIST)...");
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("CONTRID", contractId);
        String serviceName = Constants.B2BPOSWS;
        String methodName = "dsB2BSavingsScheduleBrowseListByParam";
        Map<String, Object> res = this.callService(serviceName, methodName, queryParams, login, password);
        logger.debug("Loading list (SavingsScheduleList) finished with result: " + res + "\n");
        if ((res != null) && (res.get(RESULT) != null)) {
            if (res.get(RESULT) instanceof List) {
                return (List<Map<String, Object>>) res.get(RESULT);
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    private void processRedemptionSum(Map<String, Object> contrBean, String login, String password) throws Exception {
        // Обработка выкупных сумм
        Long contractId = Long.valueOf(contrBean.get("CONTRID").toString());
        boolean isRedemptReCalc = getBooleanParamLogged(contrBean, REDEMPTION_SUM_LIST_UPDATE_PARAMNAME, false);
        if ((isRedemptReCalc) && (contrBean.get(REDEMPTION_SUM_LIST_PARAMNAME) != null)) {
            List<Map<String, Object>> redemptionSumList = (List<Map<String, Object>>) contrBean.get(REDEMPTION_SUM_LIST_PARAMNAME);
            if (redemptionSumList.size() > 0) {
                processContractRedemptionSum(contractId, redemptionSumList, login, password);
                contrBean.put(REDEMPTION_SUM_LIST_UPDATE_PARAMNAME, false);
            }
        }
    }

    private void processSavingSchedule(Map<String, Object> contrBean, String login, String password) throws Exception {
        // Обработка графика накоплений
        Long contractId = Long.valueOf(contrBean.get("CONTRID").toString());
        boolean isRedemptReCalc = getBooleanParamLogged(contrBean, SAVINGSSCHEDULE_SUM_LIST_UPDATE_PARAMNAME, false);
        if ((isRedemptReCalc) && (contrBean.get(SAVINGSSCHEDULE_SUM_LIST_PARAMNAME) != null)) {
            List<Map<String, Object>> savingScheduleList = (List<Map<String, Object>>) contrBean.get(SAVINGSSCHEDULE_SUM_LIST_PARAMNAME);
            if (savingScheduleList.size() > 0) {
                processContractSavingSchedule(contractId, savingScheduleList, login, password);
                contrBean.put(SAVINGSSCHEDULE_SUM_LIST_UPDATE_PARAMNAME, false);
            }
        }
    }

}
