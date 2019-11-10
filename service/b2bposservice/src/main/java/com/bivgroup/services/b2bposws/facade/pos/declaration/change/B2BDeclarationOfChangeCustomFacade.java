package com.bivgroup.services.b2bposws.facade.pos.declaration.change;

import com.bivgroup.services.b2bposws.facade.pos.declaration.B2BDeclarationBaseFacade;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.system.dispatcher.ServiceInvocationException;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BDeclarationOfChangeCustom")
public class B2BDeclarationOfChangeCustomFacade extends B2BDeclarationBaseFacade {

    private static final String BLOCK_DID_MSG = "По договору $CONTRACT на указанную дату транша, Вами ранее уже было направлено Заявление на $DOING1 Дополнительного инвестиционного дохода. Оформление второй Опции на $DOING2 на эту же дату исполнения невозможно.";

    public static final String DECLARATION_OF_CHANGE_ID_PARAMNAME = "id";

    public static final String DECLARATION_OF_CHANGE_CONTRACT_ID_PARAMNAME = "contractId";

    private static final Long KIND_CHANGE_REASON_FIXATION_TYPE_ID = 218002L;
    private static final String KIND_CHANGE_REASON_SYSNAME_HCHANGEPASSPORT = "ReasonChangeForContract_HChangePassport";
    private static final String KIND_CHANGE_REASON_SYSNAME_HCHANGECONTINFO = "ReasonChangeForContract_HChangeContInfo";
    private static final String KIND_CHANGE_REASON_SYSNAME_HCHANGEADDRESS = "ReasonChangeForContract_HChangeAddress";
    private static final String KIND_CHANGE_REASON_SYSNAME_HCHANGEPERSDATA = "ReasonChangeForContract_HChangePersData";
    private static final String KIND_CHANGE_REASON_SYSNAME_HCHANGESURNAME = "ReasonChangeForContract_HChangeSurname";
    private static final String KIND_CHANGE_REASON_ID_PARAM_NAME = "kindChangeReasonId";
    private static final String IS_FROM_B2BPPO = "some";
    private static final Long DECLARATION_DRAFT_STATUS_ID_PARAM_NAME = 7501L;
    private static final Long CONTRACT_ACTIVE_STATUS_ID_PARAM_NAME = 2101L;
    private static final List<String> CONTRACT_EXISTS_CHANGE_ACTION = Arrays.asList("1", "2", "CL", "PROTECTED_DEPOSITOR_S175", "RIGHT_CHOICE_RTBOX", "SBELT_RTBOX");

    private final Logger logger = Logger.getLogger(this.getClass());

    // получение системного наименования текущего состояния заявления (если не сохранялось, то вернет соответствующий черновику)
    private String getDeclarationCurrentStateSysName(Map<String, Object> declarationMap) throws Exception {
        String currentStateSysName;
        Long declarationId = getLongParamLogged(declarationMap, DECLARATION_OF_CHANGE_ID_PARAMNAME);
        if (declarationId == null) {
            // заявление не сохранялось, что в данном случае аналогично черновику
            currentStateSysName = "PD_DECLARATIONOFCHANGE_DRAFT";
        } else {
            Map<String, Object> declaration = dctFindById(DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME, declarationId);
            Map<String, Object> currentState = getMapParam(declaration, "stated_EN");
            currentStateSysName = getStringParamLogged(currentState, "sysname");
        }
        return currentStateSysName;
    }

    @WsMethod(requiredParams = {DECLARATION_OF_CHANGE_ID_PARAMNAME, CLIENT_PROFILE_ID_PARAMNAME})
    public Map<String, Object> dsB2BDeclarationOfChangeLoad(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDeclarationOfChangeLoad begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        String error = "";

        Long declarationId = getLongParamLogged(params, DECLARATION_OF_CHANGE_ID_PARAMNAME);
        if (declarationId == null) {
            error = "Не указан идентификатор загружаемого заявления на изменение условий страхования!";
        }

        Map<String, Object> result;
        if (error.isEmpty()) {
            result = dctFindById(DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME, declarationId, isCallFromGate);
            markAllMapsByKeyValue(result, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        } else {
            result = new HashMap<>();
            result.put(ERROR, error);
        }

        logger.debug("dsB2BDeclarationOfChangeLoad end");
        return result;
    }

    @WsMethod(requiredParams = {STATEID_PARAMNAME})
    public Map<String, Object> dsB2BDeclarationOfChangeLoadByStateId(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDeclarationOfChangeLoadByStateId begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        String error = "";

        List<Map<String, Object>> declarationList = null;
        if (error.isEmpty()) {
            Map<String, Object> declarationParams = new HashMap<String, Object>();
            // для ограничения обрабатываемых допсов по состоянию
            declarationParams.put(STATEID_PARAMNAME, params.get(STATEID_PARAMNAME));
            // для ограничения обрабатываемых допсов по ИД конкретного допса в ходе отладки
            Long id = getLongParam(params, DECLARATION_ID_PARAMNAME);
            if (id != null) {
                logger.debug("dsB2BDeclarationOfChangeLoadByStateId id = " + id);
                declarationParams.put(DECLARATION_ID_PARAMNAME, id);
                // уберем фильтрацию по состоянию (только для отладки, когда явно задан id заявления)
                declarationParams.remove(STATEID_PARAMNAME);
            }
            // запрос списка допсов
            declarationList = dctFindByExample(DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME, declarationParams, isCallFromGate);
            markAllMapsByKeyValue(declarationList, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        } else {
        }

        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        if (error.isEmpty()) {
            result.put(RESULT, declarationList);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BDeclarationOfChangeLoadByStateId finish");

        return result;
    }

    @WsMethod()
    public Map<String, Object> dsB2BDeclarationOfChangeLoadByParams(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDeclarationOfChangeLoad begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        String error = "";


        List<Map<String, Object>> declarationList = null;
        if (error.isEmpty()) {
            Map<String, Object> declarationParams = new HashMap<String, Object>(params);
            declarationList = dctFindByExample(DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME, declarationParams, isCallFromGate);
            markAllMapsByKeyValue(declarationList, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        } else {
        }

        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        if (error.isEmpty()) {
            result.put(RESULT, declarationList);
        } else {
            result.put(ERROR, error);
        }

        return result;
    }

    @WsMethod()
    public Map<String, Object> dsB2BDeclarationOfChangeLoadByParamsEx(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDeclarationOfChangeLoadByParamsEx begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        String error = "";


        List<Map<String, Object>> declarationList = null;
        if (error.isEmpty()) {
            Map<String, Object> declarationParams = new HashMap<String, Object>(params);
            Map<String, Object> res = this.selectQuery("dsB2BDeclarationForContractCustomBrowseListByParamEx", params);
            if (res.get(RESULT) != null) {
                declarationList = (List<Map<String,Object>>) res.get(RESULT);
            }
            //markAllMapsByKeyValue(declarationList, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        } else {
        }

        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        if (error.isEmpty()) {
            result.put(RESULT, declarationList);
        } else {
            result.put(ERROR, error);
        }

        return result;
    }
    // получение списка всех заявлении на изменение условий конкретного договора страхования (по ИД договора) или списка из одного заявления (по ИД заявления)
    // заявления в целях безопасности фильтруются по связи с профилем через контрагента
    @WsMethod(requiredParams = {CLIENT_PROFILE_ID_PARAMNAME})
    public Map<String, Object> dsB2BDeclarationOfChangeLoadList(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDeclarationOfChangeLoadList begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        String error = "";
        List<Map<String, Object>> declarationList = null;
        Long declarationId = getLongParamLogged(params, DECLARATION_OF_CHANGE_ID_PARAMNAME);
        if (declarationId == null) {
            Long contractId = getLongParamLogged(params, DECLARATION_OF_CHANGE_CONTRACT_ID_PARAMNAME);
            if (contractId == null) {
                error = "Не указан ни договор (заявления по которому следует загрузить) ни конкретное заявление для загрузки!";
            } else {
                Map<String, Object> declarationParams = new HashMap<String, Object>();
                declarationParams.put(DECLARATION_OF_CHANGE_CONTRACT_ID_PARAMNAME, contractId);
                declarationList = dctFindByExample(DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME, declarationParams, isCallFromGate);
                if (declarationList == null) {
                    error = "Не удалось получить сведения о заявлениях на изменение условий страхования";
                }
                // уберем заявления которые не содержат ни одной причины
                declarationList.removeIf(declarationId_EN -> {
                    List<Map> reasons = (List<Map>) declarationId_EN.get("reasons");
                    return reasons == null || reasons.size() == 0;
                });
            }
        } else {
            declarationList = new ArrayList<Map<String, Object>>();
            Map<String, Object> declaration = dctFindById(DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME, declarationId, isCallFromGate);
            if (declaration == null) {
                error = "Не удалось получить сведения о заявлениях на изменение условий страхования!";
            } else if (!declaration.isEmpty()) {
                declarationList.add(declaration);
            }
        }
        // фильтр по клиенту (в целях безопасности)
//        List<Map<String, Object>> declarationClientList = new ArrayList<Map<String, Object>>();
//        if (error.isEmpty()) {
//            if (declarationList != null) {
//                Long clientProfileId = getLongParamLogged(params, CLIENT_PROFILE_ID_PARAMNAME);
//                Map<String, Object> clientProfile = dctFindById(CLIENT_PROFILE_ENTITY_NAME, clientProfileId);
//                Long clientId = getLongParamLogged(clientProfile, "clientId");
//                if (clientId == null) {
//                    error = "Не удалось получить сведения из профиля клиента, необходимые для загрузки данных о заявлениях на изменение условий страхования!";
//                } else {
//                    for (Map<String, Object> declaration : declarationList) {
//                        Map<String, Object> applicant = getMapParam(declaration, "applicantId_EN");
//                        Long applicantClientId = getLongParamLogged(applicant, "clientId");
//                        if (clientId.equals(applicantClientId)) {
//                            declarationClientList.add(declaration);
//                        } else {
//                            String methodName = "dsB2BDeclarationOfChangeLoadList";
//                            logger.warn(String.format(
//                                    "Result item of %s method call was filtered out as not related to specified client profile (with id = %d)! "
//                                    + "Details (filtered out item): %s.",
//                                    methodName, clientProfileId, declaration
//                            ));
//                        }
//                    }
//                }
//            }
//        }
        // формирование описания для колонки "Информация" журнала "История изменений по договору"
        // todo: мб сделать опциональным по входному параметру?
        logger.debug("Generating info note texts...");
        List<Map<String, Object>> declarationClientList = declarationList;
        for (Map<String, Object> declaration : declarationClientList) {
            List<Map<String, Object>> reasonList = getListParam(declaration, "reasons");
            if (reasonList != null) {
                for (Map<String, Object> reason : reasonList) {
                    Map<String, Object> kindChangeReason = getMapParam(reason, "kindChangeReasonId_EN");
                    String infoNoteTemplate = getStringParamLogged(kindChangeReason, "infoNoteTemplate"); // INFONOTETEMPLATE
                    //infoNoteTemplate = "Смена фонда с '%prevFundId_EN/name%' на '%fundId_EN/name%'."; // !только для отладки!
                    String infoNote = infoNoteTemplate;
                    if (!infoNoteTemplate.isEmpty()) {
                        Pattern pattern = Pattern.compile("(%)(.*?)(%)");
                        Matcher matcher = pattern.matcher(infoNoteTemplate);
                        Set<String> keySet = new HashSet<String>();
                        while (matcher.find()) {
                            keySet.add(matcher.group(2));
                        }
                        logger.debug("keySet: " + keySet);
                        JXPathContext context = JXPathContext.newContext(reason);
                        context.setLenient(true);
                        for (String key : keySet) {
                            String keyValue = null;
                            try {
                                keyValue = getStringParam(context.getValue(key));
                            } catch (Exception ex) {
                                logger.error(String.format(
                                        "Unable to resolve x-path '%s' in map '%s'. Details (exception):",
                                        key, reason
                                ), ex);
                            }
                            logger.debug("key = " + key + "value = " + keyValue);
                            if (keyValue == null) {
                                keyValue = "";
                            }
                            infoNote = infoNote.replaceAll("%" + key + "%", keyValue);
                        }
                        logger.debug("infoNote = " + infoNote);
                        reason.put("infoNote", infoNote);
                    }
                }
            }
            // доп. анализ успеха передачи в ОИС (https://rybinsk.bivgroup.com/redmine/issues/11805)
            Long declId = getLongParamLogged(declaration, DECLARATION_OF_CHANGE_ID_PARAMNAME);
            if (declId != null) {
                HashMap<String, Object> requestQueueParams = new HashMap<String, Object>();
                requestQueueParams.put("OBJID", declId);
                requestQueueParams.put("ORDERBYREQDATE", "just only not null value");
                List<Map<String, Object>> requestQueueList = callServiceAndGetListFromResultMapLogged(
                        B2BPOSWS_SERVICE_NAME, "dsB2BRequestQueueBrowseListByParam", requestQueueParams, login, password
                );
                if ((requestQueueList != null) && (!requestQueueList.isEmpty())) {
                    Map<String, Object> request = requestQueueList.get(requestQueueList.size() - 1);
                    Long requestStateId = getLongParamLogged(request, "REQUESTSTATEID");
                    declaration.put("requestStateId", requestStateId);
                    /*
                    if (!REQUEST_QUEUE_STATUS_SUCCESS.equals(requestState)) {
                        declaration.put("processState", );
                    }
                    */
                }
            }
        }
        logger.debug("Generating info note texts finished.");
        // очистка от мап контрагента (опционально, по параметру)
        if (error.isEmpty()) {
            Set<String> clearedParamNames = new HashSet<String>();
            for (String paramName : params.keySet()) {
                if (paramName.endsWith("$trim")) {
                    boolean paramValue = getBooleanParamLogged(params, paramName, false);
                    if (paramValue) {
                        String clearedParamName = paramName.substring(0, paramName.length() - "$trim".length());
                        clearedParamNames.add(clearedParamName);
                    }
                }
            }
            if (!clearedParamNames.isEmpty()) {
                logger.debug(String.format("Requested clearing result from keys (%s) by call params...", clearedParamNames));
                clearAllListFromKeysAndEns(declarationClientList, false, false, clearedParamNames);
                logger.debug("Clearing result from keys finished.");
            }
        }

        //фильтрация не удаленных объектов
        List<Map<String,Object>> declarationClientListfiltered = declarationClientList.stream().filter(new Predicate<Map<String, Object>>() {
            @Override
            public boolean test(Map<String, Object> historyItem) {
               try {
                   Map<String,Object> state = (Map<String, Object>) historyItem.get("stateId_EN");
                   String stateSysname = (String) state.get("sysname");
                   return !stateSysname.equalsIgnoreCase("PD_DECLARATION_DELETED");
               } catch (NullPointerException ex) {
                   return true;
               }
            }
        }).collect(Collectors.toList());
        // формирование резльтата
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            markAllMapsByKeyValue(declarationClientListfiltered, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
            result.put(RESULT, declarationClientListfiltered);
        } else {
            result.put(ERROR, error);
        }

        logger.debug("dsB2BDeclarationOfChangeLoadList end");
        return result;
    }

    @WsMethod(requiredParams = {DECLARATION_MAP_PARAMNAME})
    public Map<String, Object> dsB2BDeclarationOfChangeSaveB2BPPO(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        params.put(IS_FROM_B2BPPO, "some string");
        return this.callService(THIS_SERVICE_NAME, "dsB2BDeclarationOfChangeSave", params, login, password);
    }

    private Map<String, Object> getKindDeclarationByIdForDID(Long id) throws Exception {
        // тяне сиснейм типа текущего заявления на изменение
        Map<String, Object> reqMap = new HashMap<String, Object>();
        reqMap.put("id", id);
        Map<String, Object> res = dctFindByExample(KIND_CHANGE_REASON_ENTITY_NAME, reqMap).get(0);
        String sysname = getStringParam(res, "sysname");
        // выбираем требуемый сиснейм, если он DID
        String newSysname;
        switch (sysname) {
            case "ReasonChangeForContract_FixIncome": {
                newSysname = "ReasonChangeForContract_WithdrawIncome";
                break;
            }
            case "ReasonChangeForContract_WithdrawIncome": {
                newSysname = "ReasonChangeForContract_FixIncome";
                break;
            }
            default: {
                newSysname = null;
            }
        }
        // тянем ID KindDeclaration, если требуется
        if (newSysname != null) {
            Map<String, Object> resultParams = new HashMap<>();
            resultParams.put("sysname", newSysname);
            Map<String, Object> result = dctFindByExample(KIND_CHANGE_REASON_ENTITY_NAME, resultParams).get(0);
            return result;
        } else {
            return null;
        }
    }
    
    private String getMapProductState(String sysname) {
        String result = new String(BLOCK_DID_MSG);
        switch (sysname) {
            case "ReasonChangeForContract_FixIncome": {
                result = result.replace("$DOING1", "фиксацию");
                result = result.replace("$DOING2", "выплату");
                break;
            }
            case "ReasonChangeForContract_WithdrawIncome": {
                result = result.replace("$DOING1", "выплату");
                result = result.replace("$DOING2", "фиксацию");
                break;
            }
        }
        return result;
    }

    private String getContructNumber(Long id, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("CONTRID", id);
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> contract = callExternalServiceLogged(THIS_SERVICE_NAME, "dsB2BContractBrowseListByParam", params, login, password);
        return getStringParam(contract, "CONTRNUMBER");
    }
    
    @WsMethod(requiredParams = {DECLARATION_MAP_PARAMNAME})
    public Map<String, Object> dsB2BCheckAccessibleDID(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> resultData = new HashMap<>();
        result.put(RESULT, resultData);
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean findDidReason = false;

        Map<String, Object> declarationMap = (Map<String, Object>) params.get("DECLARATIONMAP");
        Object reasonsList = declarationMap.get("reasons");
        Object dateObj = declarationMap.get("supposedDateOfEntry$date");
        if (reasonsList != null && !((List) reasonsList).isEmpty() && dateObj != null) {

            Long contractId = getLongParam(declarationMap, "contractId");
            String supposeDateStringVal =  new SimpleDateFormat("yyyy-MM-dd").format(parseAnyDate(dateObj, Date.class, "", false));;

            List<Map<String, Object>> reasons = (List<Map<String, Object>>) reasonsList;
            boolean contains = false;
            for (int i = 0; i < reasons.size() && !contains; i++) {
                Map<String, Object> reason = reasons.get(i);
                Long kindReasonId = getLongParam(reason, "kindChangeReasonId");

                // тянем ID смежного kindReasonChange
                Map<String, Object> kindDeclaration = getKindDeclarationByIdForDID(kindReasonId);
                Long kindDeclarationID = getLongParam(kindDeclaration, "id");
                if (kindDeclarationID != null) {
                    findDidReason = true;
                    resultData.put("type", "DID");
                    // проверяем, есть ли для договора смежные заявления, которые блокируют текущие.
                    Map<String, Object> reqParams = new HashMap<>();
                    reqParams.put("KINDCHANGEREASONID", kindDeclarationID);
                    reqParams.put("CONTRACTID", contractId);
                    reqParams.put("SUPPOSEDDATEOFENTRY_START", supposeDateStringVal + MASK_START_DAY_TIME24);
                    reqParams.put("SUPPOSEDDATEOFENTRY_END", supposeDateStringVal + MASK_END_DAY_TIME24);
                    reqParams.put("CHECKDATE", true);
                    Map<String, Object> res = this.selectQuery("dsPDDeclarationAndReasonGetSameDeclarationExist", "dsPDDeclarationAndReasonGetSameDeclarationExistCount", reqParams);
                    List<Map<String, Object>> declarationList = getListParam(res, RESULT);
                    // отсекаем те, что в состоянии "Отказ в рассмотрении"
                    declarationList = declarationList.stream().filter(el -> !el.get("STATESYSNAME").equals("Отказ в рассмотрении")).collect(Collectors.toList());

                    // есть ли для договора смежные заявления
                    if (declarationList.size() > 0) {
                        String kindDeclarationSysname = getStringParam(kindDeclaration, "sysname");
                        String contructNumber = getContructNumber(contractId, login, password);
                        String errMsg = getMapProductState(kindDeclarationSysname);
                        errMsg = errMsg.replace("$CONTRACT", contructNumber);
                        result.put(RET_STATUS, RET_STATUS_OK);
                        resultData.put("accessible", false);
                        resultData.put("notAccessibleMsg", errMsg);
                        contains = true;
                    }
                }
            }
            // их нет?
            if (!contains) {
                result.put(RET_STATUS, RET_STATUS_OK);
                resultData.put("accessible", true);
            }
        }
        if (!findDidReason) {
            // не did опция
            result.put(RET_STATUS, RET_STATUS_OK);
            resultData.put("accessible", true);
            resultData.put("type", "other");
        }
        return result;
    }
    
    @WsMethod(requiredParams = {DECLARATION_MAP_PARAMNAME})
    public Map<String, Object> dsB2BDeclarationOfChangeSave(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDeclarationOfChangeSave begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        String error = "";
        // ИД профиля клиента
        Long clientProfileId = getLongParamLogged(params, CLIENT_PROFILE_ID_PARAMNAME);
        Map<String, Object> declarationMap = getMapParam(params, DECLARATION_MAP_PARAMNAME);
        Map<String, Object> applicantMap = (Map<String, Object>) declarationMap.get("applicantId_EN");
        // ИД договора
        Long contractId = getLongParamLogged(declarationMap, "contractId");
        Map<String, Object> contract = null;
        Map<String, Object> fullContract = null;
        if (contractId == null) {
            error = "В заявлении на изменение условий страхования не указан договор!";
        } else {
            Map<String, Object> contractParams = new HashMap<>();
            contractParams.put("CONTRID", contractId);
            contractParams.put(RETURN_AS_HASH_MAP, true);
            contract = callExternalServiceLogged(THIS_SERVICE_NAME, "dsB2BContractBrowseListByParam", contractParams, login, password);
            if (!isCallResultOKAndContainsLongValue(contract, "CONTRID", contractId)) {
                error = "Не найден договор, указанный в заявлении на изменение условий страхования!";
            }
        }

        if (error.isEmpty()) {
            Object contractDate = getDateDctValue(declarationMap, DECLARATION_CONTRACT_DATE_PARAMNAME);
            if (contractDate == null) {
                // по общей договоренности дата оформления договора теперь обязательна для создания заявления
                // (чтобы из-за одной даты не вызвать еще раз запрос полных данных по договору из интеграции - уже вызывается на интерфейсе)
                error = "В заявлении на изменение условий страхования не указана дата оформления договора!";
            }
        }

        Object supposedDateOfEntry = declarationMap.get("supposedDateOfEntry" + (isCallFromGate ? "$date" : ""));
        // не для всех причин дата изменения является обязательной к указанию на интерфейсе (подробнее - в доке с ответами)
        /*
        if (supposedDateOfEntry.isEmpty()) {
            error = "Не указана дата внесения изменений в договор!";
        }
         */

        boolean isFixationType = false;
        boolean isChangeInsurerData = false;
        boolean isNeedCheckDuplicate = getBooleanParam(params.get("isNeedCheckDuplicate"), true);
        boolean existSameReason = false;
        Date nearestAnniversaryDate = null;
        Date closerTrancheDate = null;
        List<Map<String, Object>> changeInsurerReasonList = new ArrayList<>();
        // получаем идентификатор версии продукта
        Long contractProdVerId = getLongParamLogged(contract, "PRODVERID");
        Long contractProdProgId = getLongParamLogged(contract, "PRODPROGID");
        String kindDeclarationSysName = "";
        if (error.isEmpty()) {
            List<Map<String, Object>> reasonList = getListParam(declarationMap, "reasons");
            // костыль #18468, удаляем DECREASE_INS_SUM их не должно быть по ФТ вообще.
            // todo разделить заявления
            reasonList.removeIf(reason -> {
                String decl = getStringParam(reason, "kindChangeReasonId");
                return decl.equals("230018");
            });
            if ((reasonList == null) || (reasonList.isEmpty())) {
                error = "Не указано ни одной причины изменения договора страхования!";
            } else {
                for (Map<String, Object> reason : reasonList) {
                    // проверяем указан ли идентификатор рода изменений
                    Long kindDeclarationId = getLongParamLogged(reason, "kindDeclarationId");
                    if (kindDeclarationId == null) {
                        error = "Не по всем причинам изменения договора страхования указан род изменения!";
                        break;
                    }
                    // проверяем указан ли идентификатор вида изменений
                    Long kindChangeReasonId = getLongParamLogged(reason, "kindChangeReasonId");
                    if (kindChangeReasonId == null) {
                        error = "Не по всем причинам изменения договора страхования указан вид изменения!";
                        break;
                    }

                    // проверяем существует ли данный род изменений для продукта
                    Map<String, Object> findProdDeclarationParams = new HashMap<>();
                    findProdDeclarationParams.put("kindDeclarationId", kindDeclarationId);
                    findProdDeclarationParams.put("prodVerId", contractProdVerId);
                    findProdDeclarationParams.put("prodProgId", contractProdProgId);
                    List<Map<String, Object>> prodKindDeclarationReasons = dctFindByExample(PROD_KIND_DECLARATION_ENTITY_NAME, findProdDeclarationParams);
                    if (prodKindDeclarationReasons == null || prodKindDeclarationReasons.isEmpty()) {
                        error = "Для продукта указанного в договоре невозможен данный род причины изменения.";
                        break;
                    }

                    // если подобных родов для продукта более одного то ругаемся в лог (для разработчиков)
                    if (prodKindDeclarationReasons.size() > 1) {
                        logger.error("dsB2BDeclarationOfChangeSave for product with prodVerId=" + contractProdVerId
                                + " use more than one kindDeclaration with id=" + kindDeclarationId);
                    }

                    // получаем первый род из списка, т.к. такой может быть только один для продукта
                    Map<String, Object> kindDeclarationId_EN = (Map<String, Object>) prodKindDeclarationReasons.get(0).get("kindDeclarationId_EN");
                    kindDeclarationSysName = getStringParam(kindDeclarationId_EN, "sysname");
                    List<Map<String, Object>> kindReasons = (List<Map<String, Object>>) kindDeclarationId_EN.get("kindReasons");
                    if (kindReasons == null || kindReasons.isEmpty()) {
                        error = "Для данного рода изменений отсутствует хотя бы один вид изменений";
                        logger.error("dsB2BDeclarationOfChangeSave for kindDeclaration with id=" + kindDeclarationId
                                + " not exist at least one kindChangeReason!");
                        break;
                    }
                    // фильтруем полученный список по указанному виду причины изменения
                    kindReasons = kindReasons.stream().filter(new Predicate<Map<String, Object>>() {
                        @Override
                        public boolean test(Map<String, Object> stringObjectMap) {
                            return kindChangeReasonId.equals(getLongParam(stringObjectMap.get("id")));
                        }
                    }).collect(Collectors.toList());
                    // если после фильтрации список пуст, тогда считаем что указана не верный вид причины изменения
                    // или он отсуствует в базе
                    if (kindReasons == null || kindReasons.isEmpty()) {
                        error = "Для данного рода изменений отсутствует указанный вид изменений";
                        break;
                    }

                    // если подобных видов для рода более одной то ругаемся в лог (для разработчиков)
                    if (kindReasons.size() > 1) {
                        logger.error("dsB2BDeclarationOfChangeSave for kindDeclaration with id=" + kindDeclarationId
                                + " use more than one kindChangeReason with id=" + kindChangeReasonId);
                    }

                    // получаем первый элемет из списка отфильтрованых kindChangeReasons т.к. он такой должен быть только 1
                    Map<String, Object> kindChangeReason = kindReasons.get(0);
                    String changeReasonEntityName = getStringParamLogged(kindChangeReason, "sysname");
                    String kindChangeReasonName = getStringParamLogged(kindChangeReason, "name");
                    String kindChangeReasonExtSysName = getStringParamLogged(kindChangeReason, "externalId");
                    String changeReasonValidateMethodName = String.format(
                            "dsB2BReasonChangeValidate%s",
                            changeReasonEntityName.replace("ReasonChangeForContract_", "")
                    );
                    if (changeReasonEntityName.isEmpty() || kindChangeReasonName.isEmpty() || changeReasonValidateMethodName.isEmpty()) {
                        error = "Указан некорректный вид причины изменения договора!";
                        break;
                    }

                    if (error.isEmpty()) {
                        reason.remove("kindChangeReasonId_EN");
                        reason.put(ENTITY_TYPE_NAME_FIELD_NAME, changeReasonEntityName);
                        reason.put("changeDate" + (isCallFromGate ? "$date" : ""), supposedDateOfEntry);
                        declarationMap.put("DuplicateExist", false);
                        if (isNeedCheckDuplicate) {
                            // TODO: ограничить список проверяемых типов изменений. исключив например создание дубликата договора
                            if (!"DUBLICATE_DOCUMENT".equalsIgnoreCase(kindChangeReasonExtSysName)) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                                Date chDate = null;
                                boolean isOption = false;
                                if ("FIXATION_ROI".equalsIgnoreCase(kindChangeReasonExtSysName) ||
                                        "EXTRA_PREMIUM".equalsIgnoreCase(kindChangeReasonExtSysName) ||
                                        "EXTRA_ROI".equalsIgnoreCase(kindChangeReasonExtSysName) ||
                                        "CHANGE_ASSET".equalsIgnoreCase(kindChangeReasonExtSysName)) {
                                    if (closerTrancheDate == null) {
                                        closerTrancheDate = getCloserTrancheStartDate(calcTrahcheStartDate(), login, password);
                                    }
                                    isOption = true;
                                    try {
                                        chDate = sdf.parse(getStringParam(reason.get("changeDate$date")));
                                    } catch (Exception e) {
                                        logger.debug("changeDate is empty");
                                    }
                                    // подозреваю что надо было бы именно так. сохранять в дату применения изменения ближайшую дату
                                    // транша, а не генерить ее в интеграции при отправке в ОИС.
                                    // иначе при создании опции через год например, в данном месте будет генерится уже новая дата транша, по ранее сохраненным заявкам.
                                    if (chDate == null) {
                                        String closerTrancheDateStr = sdf.format(closerTrancheDate);
                                        reason.put("changeDate$date",closerTrancheDateStr);
                                    }
                                } else {
                                    if (nearestAnniversaryDate == null) {
                                        Map<String, Object> anniversaryParams = new HashMap<>();
                                        anniversaryParams.put("CONTRID",contractId);
                                        anniversaryParams.put("ReturnAsHashMap","TRUE");
                                        Map<String,Object> anniRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BGetDateOfNearestAnniversary", anniversaryParams, login, password);
                                        if (anniRes.get("anniversaryContractDate") != null) {
                                            String anniDateStr = getStringParam(anniRes.get("anniversaryContractDate"));
                                            nearestAnniversaryDate = sdf.parse(anniDateStr);
                                        }
                                    }
                                    chDate = nearestAnniversaryDate;
                                    //todo возможно, если в данный момент changeDate$date пустая
                                    // стоит в нее сохранить anniDateStr.
                                    // а может эта дата здесь пуста только у тех изменений
                                }
                                existSameReason = checkExistSameReasonByContrAndChangeDate(contractId, clientProfileId, getStringParam(reason.get("kindChangeReasonId")),
                                        chDate, isOption, closerTrancheDate, nearestAnniversaryDate, login, password);

                                if (existSameReason) {
                                    declarationMap.put("DuplicateExist", true);
                                    error = "Уважаемый клиент, обращаем Ваше внимание, что создаваемая операция уже существует с такой же датой исполнения. Вы действительно хотите продолжить?";
                                    declarationMap.put("DuplicateMessage", error);

                                    //declarationMap.put(ERROR, error);
                                    break;
                                }
                            }
                        }
                        markAsModified(reason);
                        if (KIND_CHANGE_REASON_FIXATION_TYPE_ID.equals(getLongParam(reason.get(KIND_CHANGE_REASON_ID_PARAM_NAME)))) {
                            isFixationType = true;
                        }
                        if ((KIND_CHANGE_REASON_SYSNAME_HCHANGEPASSPORT.equalsIgnoreCase(changeReasonEntityName)) ||
                                (KIND_CHANGE_REASON_SYSNAME_HCHANGECONTINFO.equalsIgnoreCase(changeReasonEntityName)) ||
                                (KIND_CHANGE_REASON_SYSNAME_HCHANGEADDRESS.equalsIgnoreCase(changeReasonEntityName)) ||
                                (KIND_CHANGE_REASON_SYSNAME_HCHANGEPERSDATA.equalsIgnoreCase(changeReasonEntityName)) ||
                                (KIND_CHANGE_REASON_SYSNAME_HCHANGESURNAME.equalsIgnoreCase(changeReasonEntityName))) {
                            //требуется найти все договора данного страхователя
                            // по каждому создать заявление.
                            // вызвать печать для каждого заявления.
                            isChangeInsurerData = true;
                            changeInsurerReasonList.add(reason);
                        }
                        //
                        HashMap<String, Object> reasonParams = new HashMap<>();
                        reasonParams.putAll(reason);
                        reasonParams.put("PREMCURRENCYID", contract.get("PREMCURRENCYID")); // валюта договора требуется для валидации сумм
                        reasonParams.put(IS_CALL_FROM_GATE_PARAMNAME, isCallFromGate);
                        reasonParams.put(RETURN_AS_HASH_MAP, true);
                        //
                        Map<String, Object> validationResult = null;
                        try {
                            validationResult = callServiceLogged(THIS_SERVICE_NAME, changeReasonValidateMethodName, reasonParams, login, password);
                        } catch (ServiceInvocationException ex) {
                            // валидация - обязательный метод, но может быть не объявлен разработчиком для новых опций
                            // (в этом случае следует отобразить и запротоколировать отдельную ошибку)
                            String exMsg = ex.getMessage();
                            if ((exMsg != null) && (exMsg.contains("Unable to locate service implementation"))) {
                                validationResult = new HashMap<>();
                                validationResult.put(RET_STATUS, RET_STATUS_OK);
                                validationResult.put(ERROR, String.format(
                                        "Не удалось выполнить проверку параметров опции '%s' - метод по валидации сведений для опций данного вида ('%s') не реализован!",
                                        kindChangeReasonName, changeReasonValidateMethodName
                                ));
                                logger.error(String.format(
                                        "Method '%s' required for validaton change reason (with entity name '%s') not found! Details: ",
                                        changeReasonValidateMethodName, changeReasonEntityName
                                ), ex);
                            } else {
                                String exNote = String.format(
                                        "Calling method '%s' for validaton change reason (with entity name '%s') exception! Details: ",
                                        changeReasonValidateMethodName, changeReasonEntityName
                                );
                                logger.error(exNote, ex);
                                throw ex;
                            }
                        }
                        //
                        if (isCallResultOK(validationResult)) {
                            error = getStringParamLogged(validationResult, ERROR);
                        } else {
                            error = String.format("Не удалось выполнить проверку параметров опции '%s'!", kindChangeReasonName);
                        }
                        if (!error.isEmpty()) {
                            break;
                        }
                    }
                }
            }
        }

        // текущее состояние заявления (по сведениям из БД)
        String currentStateSysName = null;

        // при сохранении без указания состояния следует установить его соответствующим черновику
        if (error.isEmpty()) {
            Long stateId = getLongParamLogged(declarationMap, "stateId");
            if (stateId == null) {
                stateId = getEntityRecordIdBySysName(KIND_STATUS_ENTITY_NAME, "PD_DECLARATIONOFCHANGE_DRAFT");
                declarationMap.put("stateId", stateId);
                markAsModified(declarationMap);
                // первое сохранение - текущее состояние (для ряда проверок) считается соответствующим черновику
                currentStateSysName = "PD_DECLARATIONOFCHANGE_DRAFT";
            }
        }

        // требуется ли формирование ПФ, по умолчанию - нет, не требуется
        boolean isPrintRequired = false;
        // требуется ли обновление сведений заявителя, по умолчанию - нет, не требуется
        boolean isApplicantUpdateRequired = false;
        if (error.isEmpty()) {
            // следует проверить требуется ли формирование ПФ
            Long newStateId = getLongParamLogged(declarationMap, "stateId");
            Map<String, Object> newState = dctFindById(KIND_STATUS_ENTITY_NAME, newStateId);
            String newStateSysName = getStringParamLogged(newState, "sysname");
            if ("PD_DECLARATIONOFCHANGE_SENDING".equals(newStateSysName)) {
                // сохраняемое новое состояние - на отправку, следует проверить текущее состояние
                if (currentStateSysName == null) {
                    // следует получить текущее состояние (еще не было получено ранее)
                    currentStateSysName = getDeclarationCurrentStateSysName(declarationMap);
                }
                if ("PD_DECLARATIONOFCHANGE_DRAFT".equals(currentStateSysName)) {
                    // текущее состояние черновик или заявление еще не создано, а новое состояние - на отправку
                    // требуется печать ПФ
                    isPrintRequired = true;
                }
            }
            // обновлять данные заявителя сведениями из профиля можно и нужно только если сохраняется черновик (или же отсутствует заявитель)
            // во всех остальных случаях для изменения данных заявителя следует передавать applicantId_EN с нужными ROWSTATUS и пр.
            Long applicantId = getLongParamLogged(declarationMap, "applicantId");
            if (applicantId == null) {
                isApplicantUpdateRequired = true;
            } else {
                // следует проверить текущее состояние
                if (currentStateSysName == null) {
                    // следует получить текущее состояние (еще не было получено ранее)
                    currentStateSysName = getDeclarationCurrentStateSysName(declarationMap);
                }
                isApplicantUpdateRequired = "PD_DECLARATIONOFCHANGE_DRAFT".equals(currentStateSysName);
            }
            if (kindDeclarationSysName.equalsIgnoreCase("claimForDuplicateDoc")) {
                isPrintRequired = true;
            }
        }
        logger.debug("isPrintRequired = " + isPrintRequired);
        logger.debug("isApplicantUpdateRequired = " + isApplicantUpdateRequired);

        //applicantMap - если есть, то  запрос из ППО по ФРОНТе
        // если он есть, то надо перемапить имена полей для печати

        if (applicantMap == null) {
        if (error.isEmpty() && isApplicantUpdateRequired) {
            if (clientProfileId == null) {
                error = "Не указан профиль клиента, сохраняющего заявление на изменение условий страхования!";
            } else {
                Map<String, Object> clientProfile = dctFindById(CLIENT_PROFILE_ENTITY_NAME, clientProfileId);
                Map<String, Object> client = getMapParam(clientProfile, "clientId_EN");
                if (client == null) {
                    error = "Не удалось получить данные профиля клиента, сохраняющего заявление на изменение условий страхования!";
                } else {
                    Map<String, Object> applicant = makeContragentFromClient(client);
                    declarationMap.remove("applicantId");
                    declarationMap.put("applicantId_EN", applicant);
                    markAsModified(declarationMap);
                }
            }
        }
        } 

        if (error.isEmpty()) {
            updateEntitySystemDates(declarationMap);
        }

        // Проверка требуется ли печатать заявление на отмену опции фиксации
        // если у нас есть хотя бы одно заявление на Фиксацию не в статусе черновик
        // тогда печатаем заявление на отмену и заявление на изменение условий
        // иначе напечатается только заявление на изменение условий
        boolean isNeedPrintCancelClaim = false; // по умолчанию считаем что печатать заявление на отмену не нужно
        if (error.isEmpty() && isFixationType) {
            Map<String, Object> contractParams = new HashMap<>();
            contractParams.put(DECLARATION_OF_CHANGE_CONTRACT_ID_PARAMNAME, contractId);
            contractParams.put(CLIENT_PROFILE_ID_PARAMNAME, clientProfileId);
            // получаем список всех заявлений на изменение договора
            List<Map<String, Object>> declarationClientList = callServiceAndGetListFromResultMapLogged(THIS_SERVICE_NAME,
                    "dsB2BDeclarationOfChangeLoadList", contractParams, login, password);
            if (declarationClientList != null && !declarationClientList.isEmpty()) {
                // фильтруем список отбрасывая заявление в статусе черновик
                // и все которые не являются заявлениями на фиксацию
                declarationClientList = declarationClientList.stream().filter(new Predicate<Map<String, Object>>() {
                    @Override
                    public boolean test(Map<String, Object> stringObjectMap) {
                        boolean result = false;
                        if (!DECLARATION_DRAFT_STATUS_ID_PARAM_NAME.equals(getLongParam(stringObjectMap.get("stateId")))) {
                            List<Map<String, Object>> reasons = (List<Map<String, Object>>) stringObjectMap.get("reasons");
                            if (reasons != null && !reasons.isEmpty()) {
                                reasons = reasons.stream().filter(new Predicate<Map<String, Object>>() {
                                    @Override
                                    public boolean test(Map<String, Object> stringObjectMap1) {
                                        return KIND_CHANGE_REASON_FIXATION_TYPE_ID.equals(getLongParam(stringObjectMap1.get(KIND_CHANGE_REASON_ID_PARAM_NAME)));
                                    }
                                }).collect(Collectors.toList());
                                result = reasons != null && !reasons.isEmpty();
                            }
                        }
                        return result;
                    }
                }).collect(Collectors.toList());
                // если есть хотя бы одно заявление на измение фиксации, тогда печатаем заявление на отмену
                isNeedPrintCancelClaim = declarationClientList != null && !declarationClientList.isEmpty();
            }
        }

        Map<String, Object> saveResult = null;
        List<Map<String, Object>> saveResList = new ArrayList<>();
        if (error.isEmpty()) {
            declarationMap.remove("stateId_EN"); // для корректной смены состояния
            // мапу аппликанта нельзя передавать в сохранение, так как она только для печати
            applicantMap = (Map<String, Object>) declarationMap.get("applicantId_EN");
            //declarationMap.remove("applicantId_EN");
            declarationMap.put("applicantId_EN", applicantMap);
            saveResult = dctCrudByHierarchy(DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME, declarationMap, isCallFromGate);
            if (isChangeInsurerData) {
                Map<String, Object> declMapTmp = new HashMap<>();
                declMapTmp.putAll(declarationMap);
                Long declarationId = getLongParamLogged(saveResult, DECLARATION_OF_CHANGE_ID_PARAMNAME);
                declMapTmp.put("parentId", declarationId);

                //создаем новую declarationMap, в ней проходим по списку причин,
                // и исключаем все не относящиеся к изменению данных страхователя
                declMapTmp.put("reasons", changeInsurerReasonList);

                Long thirdPartyId = null;
                for (Map<String, Object> reason : changeInsurerReasonList) {
                    if (reason.get("thirdPartyId") != null) {
                        thirdPartyId = getLongParam(reason.get("thirdPartyId"));
                        break;
                    }
                }

                if (thirdPartyId != null) {
                    //получаем список договоров данного страхователя
                    List<Map<String, Object>> contrMapList = getContractIdList(thirdPartyId, contractId, login, password);
                    if (contrMapList != null && !contrMapList.isEmpty()) {
                        // фильтруем список отбрасывая заявление не в статусе действует
                        contrMapList = contrMapList.stream().filter(new Predicate<Map<String, Object>>() {
                            @Override
                            public boolean test(Map<String, Object> item) {
                                return CONTRACT_ACTIVE_STATUS_ID_PARAM_NAME.equals(getLongParam(item.get("STATEID"))) &&
                                       !CONTRACT_EXISTS_CHANGE_ACTION.contains(getStringParam(item.get("SYSNAME")));
                            }
                        }).collect(Collectors.toList());
                    }

                    for (Map<String, Object> contrMap : contrMapList) {
                        //в цикле по договорам вызываем создание заявки,
                        // предварительно меняя в declarationMap ссылку на договор
                        Long contrId = getLongParam(contrMap.get("CONTRID"));
                        Long prodConfId = getLongParam(contrMap.get("PRODCONFID"));
                        declMapTmp.put("contractId", contrId);
                        declMapTmp.put("contractDate", getDateParam(contrMap.get("DOCUMENTDATE")));
                        declMapTmp.put("contractDate$date", getDateParam(contrMap.get("DOCUMENTDATE")));

                        Map<String, Object> saveResTmp = dctCrudByHierarchy(DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME, declMapTmp, isCallFromGate);
                        //сохраняем список заявок в отдельный лист.
                        saveResTmp.put("PRODCONFID", prodConfId);
                        saveResTmp.put("CONTRID", contrId);
                        saveResList.add(saveResTmp);
                    }
                }
            }
            markAllMapsByKeyValue(saveResult, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        }

        Map<String, Object> printResult = null;
        Map<String, Object> printCancelResult = null;
        if (error.isEmpty() && isPrintRequired) {
            Long declarationId = getLongParamLogged(saveResult, DECLARATION_OF_CHANGE_ID_PARAMNAME);
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", contractProdVerId);
            Long prodConfId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME,
                    "dsB2BProductConfigBrowseListByParam", configParams,
                    login, password, "PRODCONFID")
            );
            if (declarationId != null) {
                Map<String, Object> printParams = new HashMap<>();
                // если вызов из ППО во фронте
                if (params.get("IS_FROM_B2BPPO") != null) {
                    printParams.put(DECLARATION_MAP_PARAMNAME, saveResult);
                } else {
                printParams.put(DECLARATION_OF_CHANGE_ID_PARAMNAME, declarationId);
                }
                printParams.put("PRODCONFID", prodConfId);
                printParams.put(CLIENT_PROFILE_ID_PARAMNAME, params.get(CLIENT_PROFILE_ID_PARAMNAME));
                printParams.put(RETURN_AS_HASH_MAP, true);
                printParams.put(DECLARATION_MAP_PARAMNAME, declarationMap);
                printResult = callService(SIGNB2BPOSWS_SERVICE_NAME, "dsB2BDeclarationOfChangePrint", printParams, login, password);
                error = getStringParamLogged(printResult, ERROR);
                if ((!isCallResultOK(printResult)) && error.isEmpty()) {
                    error = "Не удалось сформировать печатные документы для прикрепления к заявлению!";
                }
                if (isChangeInsurerData) {
                    //пройтись по списку заявок, и для каждой вызвать печать
                    for (Map<String, Object> saveRes : saveResList) {
                        Map<String, Object> pParams = new HashMap<>();
                        pParams.put(DECLARATION_OF_CHANGE_ID_PARAMNAME, getLongParam(saveRes.get(DECLARATION_OF_CHANGE_ID_PARAMNAME)));
                        pParams.put("PRODCONFID", saveRes.get("PRODCONFID"));
                        pParams.put(CLIENT_PROFILE_ID_PARAMNAME, params.get(CLIENT_PROFILE_ID_PARAMNAME));
                        pParams.put(DECLARATION_MAP_PARAMNAME, declarationMap);
                        pParams.put(RETURN_AS_HASH_MAP, true);
                        printResult = callService(SIGNB2BPOSWS_SERVICE_NAME, "dsB2BDeclarationOfChangePrint", pParams, login, password);
                    }
                }
                if (error.isEmpty() && isFixationType && isNeedPrintCancelClaim) {
                    printParams.put("ISPRINTCANCELDOC", true);
                    printCancelResult = callService(SIGNB2BPOSWS_SERVICE_NAME, "dsB2BDeclarationOfChangePrint", printParams, login, password);
                    error = getStringParamLogged(printCancelResult, ERROR);
                    if ((!isCallResultOK(printCancelResult)) && error.isEmpty()) {
                        error = "Не удалось сформировать печатные документы для прикрепления к заявлению!";
                    }
                }
            }
        }

        Map<String, Object> result;
        if (error.isEmpty()) {
            result = saveResult;
            if (printResult != null) {
                result.put("printResult", printResult);
            }
            if (printCancelResult != null) {
                result.put("printCancelResult", printCancelResult);
            }
        } else {
            result = new HashMap<>();
            result.putAll(declarationMap);
            if (!existSameReason) {
            result.put(ERROR, error);
        }
        }

        logger.debug("dsB2BDeclarationOfChangeSave end");
        return result;
    }

    private Date getCloserTrancheStartDate(Date startDate, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("TRANCHESTARTDATE", startDate);
        XMLUtil.convertDateToFloat(params);
        Map<String, Object> result = this.selectQuery("dsB2BInvestTrancheBrowseListByParamToDataProv", "dsB2BInvestTrancheBrowseListByParamToDataProvCount", params);
        if (result.get(RESULT) != null) {
            XMLUtil.convertFloatToDate(result);
            List<Map<String, Object>> trancheList = (List<Map<String, Object>>) result.get(RESULT);
            CopyUtils.sortByDateFieldName(trancheList, "SALESTARTDATE");
            Map<String, Object> pay = (Map<String, Object>) trancheList.get(0);
            if (pay.get("SALESTARTDATE") != null) {
                return getDateParam(pay.get("SALESTARTDATE"));
            }
        }
        return null;
    }

    private Date calcTrahcheStartDate() {
        int trancheLag = 14;
        GregorianCalendar gcDate = new GregorianCalendar();
        gcDate.setTime(new Date());
        gcDate.set(Calendar.HOUR_OF_DAY, 0);
        gcDate.set(Calendar.MINUTE, 0);
        gcDate.set(Calendar.SECOND, 0);
        gcDate.set(Calendar.MILLISECOND, 0);
        gcDate.add(Calendar.DAY_OF_YEAR, trancheLag);
        return gcDate.getTime();
    }

    private boolean checkExistSameReasonByContrAndChangeDate(Long contractId, Long clientProfileId, String kindchangereasonid, Date changeDate, boolean isOption, Date closerTrancheDate, Date nearestAnniversaryDate, String login, String password) throws Exception {
        Map<String, Object> contractParams = new HashMap<>();
        contractParams.put(DECLARATION_OF_CHANGE_CONTRACT_ID_PARAMNAME, contractId);
        contractParams.put(CLIENT_PROFILE_ID_PARAMNAME, clientProfileId);
        List<Map<String, Object>> reasons = new ArrayList<>();
        Map<String, Object> reasonSearch = new HashMap<>();
        reasonSearch.put("CONTRACTID", contractId);
        reasonSearch.put("KINDCHANGEREASONID", kindchangereasonid);
        Map<String, Object> res = this.selectQuery("dsPDDeclarationAndReasonGetSameDeclarationExist", reasonSearch);
        if (res != null) {
            if (res.get(RESULT) != null) {
                reasons = (List<Map<String, Object>>) res.get(RESULT);
                for (Map<String, Object> reason : reasons) {
                    Date changeDateExisted = getDateParam(reason.get("CHANGEDATE"));
                    if (isOption) {
                        if (changeDateExisted == null) {
                            changeDateExisted = closerTrancheDate;
                        }
                        if (changeDate == null) {
                            changeDate = closerTrancheDate;
                        }
                    } else {
                        if (changeDate == null) {
                            changeDate = nearestAnniversaryDate;
                        }
                    }
                    if (changeDateExisted != null) {
                        GregorianCalendar cd = new GregorianCalendar();
                        cd.setTime(changeDate);
                        GregorianCalendar cdE = new GregorianCalendar();
                        cdE.setTime(changeDateExisted);
                        if (cd.get(Calendar.YEAR) == cdE.get(Calendar.YEAR)) {
                            if (cd.get(Calendar.MONTH) == cdE.get(Calendar.MONTH)) {
                                if (cd.get(Calendar.DATE) == cdE.get(Calendar.DATE)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        // нет аналогичных заявлений.
        return false;
        // если есть хотя бы одно заявление на измение фиксации, тогда печатаем заявление на отмену
        //   isNeedPrintCancelClaim = declarationClientList != null && !declarationClientList.isEmpty();

    }

    private List<Map<String, Object>> getContractIdList(Long thirdPartyId, Long contractId, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("CONTRID", contractId);
        param.put("THIRDPARTYID", thirdPartyId);
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParam4InsurerChangeDops", param, login, password);
        List<Long> contrIdList = new ArrayList<>();
        if (res.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) res.get(RESULT);
            return resList;
        }

        return null;
    }
    @WsMethod(requiredParams = {DECLARATION_MAP_PARAMNAME})
    public Map<String, Object> dsB2BDeclarationOfChangeSaveWithoutChecks(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDeclarationOfChangeSaveWithoutChecks begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        String error = "";
        // ИД профиля клиента
        Long clientProfileId = getLongParamLogged(params, CLIENT_PROFILE_ID_PARAMNAME);
        Map<String, Object> declarationMap = getMapParam(params, DECLARATION_MAP_PARAMNAME);
        // ИД договора
        Long contractId = getLongParamLogged(declarationMap, "contractId");
        Map<String, Object> contract = null;
        if (contractId == null) {
            error = "В заявлении на изменение условий страхования не указан договор!";
        } else {
            Map<String, Object> contractParams = new HashMap<>();
            contractParams.put("CONTRID", contractId);
            contractParams.put(RETURN_AS_HASH_MAP, true);
            contract = callExternalServiceLogged(THIS_SERVICE_NAME, "dsB2BContractBrowseListByParam", contractParams, login, password);
            if (!isCallResultOKAndContainsLongValue(contract, "CONTRID", contractId)) {
                error = "Не найден договор, указанный в заявлении на изменение условий страхования!";
            }
        }

        if (error.isEmpty()) {
            Object contractDate = getDateDctValue(declarationMap, DECLARATION_CONTRACT_DATE_PARAMNAME);
            if (contractDate == null) {
                // по общей договоренности дата оформления договора теперь обязательна для создания заявления
                // (чтобы из-за одной даты не вызвать еще раз запрос полных данных по договору из интеграции - уже вызывается на интерфейсе)
                error = "В заявлении на изменение условий страхования не указана дата оформления договора!";
            }
        }

        Object supposedDateOfEntry = declarationMap.get("supposedDateOfEntry" + (isCallFromGate ? "$date" : ""));
        // не для всех причин дата изменения является обязательной к указанию на интерфейсе (подробнее - в доке с ответами)
        /*
        if (supposedDateOfEntry.isEmpty()) {
            error = "Не указана дата внесения изменений в договор!";
        }
         */

        Long contractProdVerId = getLongParamLogged(contract, "PRODVERID");
        Long contractProdProgId = getLongParamLogged(contract, "PRODPROGID");
        if (error.isEmpty()) {
            List<Map<String, Object>> reasonList = getListParam(declarationMap, "reasons");
            if ((reasonList == null) || (reasonList.isEmpty())) {
                error = "Не указано ни одной причины изменения договора страхования!";
            } else {
                for (Map<String, Object> reason : reasonList) {
                    Long kindChangeReasonId = getLongParamLogged(reason, "kindChangeReasonId");
                    if (kindChangeReasonId == null) {
                        error = "Не по всем причинам изменения договора страхования указан вид!";
                        break;
                    }
                    Map<String, Object> findReasonParams = new HashMap<>();
                      findReasonParams.put("id", kindChangeReasonId);
                    //findReasonParams.put("kindChangeReasonId", kindChangeReasonId);
                    List<Map<String, Object>> kindChangeReasons = dctFindByExample(KIND_CHANGE_REASON_ENTITY_NAME, findReasonParams);
                    if (kindChangeReasons == null || kindChangeReasons.size() == 0) {
                        error = "Не найден указанный вид изменения";
                        break;
                    }
                    if (kindChangeReasons.size() > 1) {
                        logger.error("Specified view change reason exist in an amount more one");
                    }
                    Map<String,Object> kindChangeReason = kindChangeReasons.get(0);

                    Map<String, Object> findProdKindDeclarationParams = new HashMap<>();
                    findProdKindDeclarationParams.put("prodVerId", contractProdVerId);
                    boolean isNeedSearchByKindDecl = false;
                    if (reason.get("kindDeclarationId") == null) {
                        isNeedSearchByKindDecl = true;
                    } else {
                        findProdKindDeclarationParams.put("kindDeclarationId", reason.get("kindDeclarationId"));
                    }
                    findProdKindDeclarationParams.put("prodProgId", contractProdProgId);
                    List<Map<String, Object>> prodKindDeclarations = dctFindByExample(PROD_KIND_DECLARATION_ENTITY_NAME, findProdKindDeclarationParams);

                    if (isNeedSearchByKindDecl) {
                        boolean successSearch = false;
                        for (Map<String,Object> prodKindDecl: prodKindDeclarations) {
                            Long kindDeclId = getLongParam(prodKindDecl.get("kindDeclarationId"));
                            if (kindDeclId != null) {
                                for (Map<String, Object> kindChangeReas : kindChangeReasons) {
                                    Long kindReasonsDeclId = getLongParam(kindChangeReas.get("kindDeclarationId"));
                                    if (kindReasonsDeclId != null) {
                                        if (kindDeclId.compareTo(kindReasonsDeclId) == 0) {
                                            kindChangeReason = kindChangeReas;
                                            successSearch = true;
                                            reason.put("kindDeclarationId", kindChangeReason.get("kindDeclarationId"));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (!successSearch) {
                            error = "Для данного договора недоступен указанный род заявления. Параметры рода заявления: " + findProdKindDeclarationParams;
                            break;
                        }
                    } else {
                        if (prodKindDeclarations == null || prodKindDeclarations.size() == 0) {
                            error = "Для данного договора недоступен указанный род заявления. Параметры рода заявления: " + findProdKindDeclarationParams;
                            break;
                        }
                    }

                    if (prodKindDeclarations.size() > 1) {
                        logger.error("Specified view product kind and declaration exist in an amount more one");
                    }

//                    reason.put("kindDeclarationId", kindDeclarationId);
                    String changeReasonEntityName = getStringParamLogged(kindChangeReason, "sysname");
                    String kindChangeReasonName = getStringParamLogged(kindChangeReason, "name");
                    String changeReasonValidateMethodName = String.format(
                            "dsB2BReasonChangeValidate%s",
                            changeReasonEntityName.replace("ReasonChangeForContract_", "")
                    );
                    if (changeReasonEntityName.isEmpty() || kindChangeReasonName.isEmpty() || changeReasonValidateMethodName.isEmpty()) {
                        error = "Указан некорректный вид причины изменения договора!";
                        break;
                    }
                }
            }
        }


        if (error.isEmpty()) {
            updateEntitySystemDates(declarationMap);
        }

        Map<String, Object> saveResult = null;
        if (error.isEmpty()) {
            declarationMap.remove("stateId_EN"); // для корректной смены состояния
            saveResult = dctCrudByHierarchy(DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME, declarationMap, isCallFromGate);
            markAllMapsByKeyValue(saveResult, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        }


        Map<String, Object> result;
        if (error.isEmpty()) {
            result = saveResult;
        } else {
            result = new HashMap<>();
            result.putAll(declarationMap);
            result.put(ERROR, error);
        }

        logger.debug("dsB2BDeclarationOfChangeSaveWithoutChecks end");
        return result;
    }

    private Object getDateDctValue(Map<String, Object> sourceMap, String dateKeyName) {
        Object dateObj = null;
        if (sourceMap != null) {
            dateObj = sourceMap.get(dateKeyName);
            if (dateObj == null) {
                dateObj = sourceMap.get(dateKeyName + "$date");
            }
        }
        return dateObj;
    }

    private static class Remapper {

        public static Map<String, Object> remapInsurer(Map<String, Object> insurerMap) {
            Map<String, Object> result = new HashMap<>();
            if (insurerMap != null) {
                result.put("name", insurerMap.get("firstName"));
                result.put("surname", insurerMap.get("lastName"));
                result.put("patronymic", insurerMap.get("patronymic"));
                result.put("dateOfBirth", insurerMap.get("birthDate"));
                result.put("Patronymic", insurerMap.get(""));
                String sex = (String) insurerMap.get("sex");
                if (sex != null) {
                    result.put("sex", sex.equalsIgnoreCase("1") ? 1 : 2);
                }
            }
            return result;

        }
    }

}
