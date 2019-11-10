package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;
import com.sun.xml.bind.v2.model.core.ID;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;
import ru.sberinsur.esb.partner.shema.*;
import ru.sberinsur.esb.partner.shema.Coverage.LifeAssureds;
import ru.sberinsur.esb.partner.shema.Coverage.LifeAssureds.LifeAssured;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;

@BOName("LifeChangePutter")
public class LifeChangePutterFacade extends IntegrationBaseFacade {

    public static final int PD_DECLARATION_SENDING = 7502;
    public static final int PD_DECLARATION_SENDED = 7503;
    private static final String CHANGE_MAP_PARAMNAME = "DECLARATIONMAP";
    private static final String CHANGE_ID_PARAMNAME = "id";
    private static final String CHANGE_STATEID_PARAMNAME = "stateId";
    private static final String CHANGE_DOCFOLDER1C_PARAMNAME = "docFolder1C";
    private static final String CHANGE_EXTERNAL_ID_PARAMNAME = "externalId";
    private static final String CHANGE_EXTERNALTYPE_ID_PARAMNAME = "externalTypeId";

    private static final int CHANGE_REQUEST_QUEUE_STATUS_SUCCESS = 1000;
    private static final int CHANGE_REQUEST_QUEUE_STATUS_ERROR = 404;

    private void processChange(Map<String, Object> changeMap, Map<String, Object> params, String login, String password) throws Exception {
        logger.debug("processChange...");
        // todo: сохранить уведомление о событии в журнал уведомлений.
        ChangeApplicationListType calt = mappingChange(changeMap, params, login, password);
        if (calt != null) {
            int requestQueueStatus = CHANGE_REQUEST_QUEUE_STATUS_SUCCESS;
            try {
                Map<String, Object> requestQueueMap = (Map<String, Object>) changeMap.remove("REQUESTQUEUEMAP");
                String request = marshall(calt, ChangeApplicationListType.class);
                /*
                if (true) {
                    // !только для отладки!
                    logger.debug("requestQueueMap: " + requestQueueMap);
                    logger.debug("request: " + request);
                    throw new Exception("Artifical exception: debug mode - integration call was aborting!");
                }
                */
                // костыль, пока от ОИС не начнет возвращатся changeType и changeTypeId. (иначе не определить по какому изменению успех, а по какому провал.)
                List<ChangeApplicationType> catList = calt.getApplicationChange();
//                Map<String, Object> lossNoticeSaveParams = new HashMap<String, Object>();
//                String response = "";
//                for (ChangeApplicationType cat : catList) {
//                    ChangeApplicationListType caltTMP = new ChangeApplicationListType();
//                    caltTMP.getApplicationChange().add(cat);
//                    AnswerImportListType resContrList = callLifePartnerPutChanges(calt);
//                    List<AnswerImportType> ccList = resContrList.getAnswerImport();
//                    String responseTmp = marshall(resContrList, AnswerImportListType.class);
//                    response = response + responseTmp;
//                    for (AnswerImportType item : ccList) {
//                        if (StatusIntType.SUCCESS.equals(item.getStatus())) {
//                            // makeTrans to 8502 state
//                            changeMap.put(CHANGE_STATEID_PARAMNAME, PD_DECLARATION_SENDED);
//                            changeMap.put(CHANGE_EXTERNAL_ID_PARAMNAME, item.getChangeId());
//                            changeMap.put(CHANGE_DOCFOLDER1C_PARAMNAME, item.getDocFolder1C());
//                            changeMap.remove("stateId_EN");
//                            changeMap.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
//
//                            List<Map<String, Object>> reasons = (List<Map<String, Object>>) changeMap.get("reasons");
//                            for (Map<String, Object> reason : reasons) {
//                                Map<String, Object> kindReason = (Map<String, Object>) reason.get("kindChangeReasonId_EN");
//                                if (cat.getChangeType().equals(getStringParam(kindReason.get("externalId")))) {
//                                    reason.put(CHANGE_STATEID_PARAMNAME, PD_DECLARATION_SENDED);
//                                    reason.put(CHANGE_EXTERNAL_ID_PARAMNAME, item.getChangeId());
//                                    reason.put(CHANGE_EXTERNALTYPE_ID_PARAMNAME, item.getChangeTypeId());
//                                    reason.put(CHANGE_DOCFOLDER1C_PARAMNAME, item.getDocFolder1C());
//                                    reason.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
//                                }
//                            }
//
//                        } else {
//                            requestQueueStatus = CHANGE_REQUEST_QUEUE_STATUS_ERROR;
//                        }
//                    }
//
//                }
//                lossNoticeSaveParams.put(CHANGE_MAP_PARAMNAME, changeMap);
//                lossNoticeSaveParams.put(RETURN_AS_HASH_MAP, true);
//                Map<String, Object> lossNoticeSaved = this.callServiceLogged(B2BPOSWS, "dsB2BDeclarationOfChangeSaveWithoutChecks", lossNoticeSaveParams, login, password);


                // TODO: когда ОИС начнет возвращать типы = пробегатсяф по ризонам, и по их типам определить кому какой changeId сохранить.
                //
                AnswerImportListType resContrList = callLifePartnerPutChanges(calt);
                List<AnswerImportType> ccList = resContrList.getAnswerImport();
                String response = marshall(resContrList, AnswerImportListType.class);
                for (AnswerImportType item : ccList) {
                    if (StatusIntType.SUCCESS.equals(item.getStatus())) {
                        // makeTrans to 8502 state
                        Map<String, Object> declarationSaveParams = new HashMap<String, Object>();
                        changeMap.put(CHANGE_STATEID_PARAMNAME, PD_DECLARATION_SENDED);
                        // changeId -- на самом деле ид заявления из ОИС
                        changeMap.put(CHANGE_EXTERNAL_ID_PARAMNAME, item.getChangeId());
                        changeMap.put(CHANGE_DOCFOLDER1C_PARAMNAME, item.getDocFolder1C());
                        changeMap.remove("stateId_EN");
                        changeMap.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                        declarationSaveParams.put(CHANGE_MAP_PARAMNAME, changeMap);
                        declarationSaveParams.put(RETURN_AS_HASH_MAP, true);
                        Map<String, Object> declarationSaved = this.callServiceLogged(B2BPOSWS, "dsB2BDeclarationOfChangeSaveWithoutChecks", declarationSaveParams, login, password);
                        break;
                    } else {
                        requestQueueStatus = CHANGE_REQUEST_QUEUE_STATUS_ERROR;
                    }
                }

                if (requestQueueMap == null || requestQueueMap.isEmpty()) {
                    b2bRequestQueueCreate(request, response, PUTCUTCHANGEINFO, requestQueueStatus, getLongParam(changeMap, CHANGE_ID_PARAMNAME), login, password);
                } else {
                    // увеличим счетчик попыток
                    Long tryCount = getLongParam(requestQueueMap.getOrDefault("TRYCOUNT", 0L));
                    requestQueueMap.put("TRYCOUNT", tryCount);
                    b2bRequestQueueUpdate(requestQueueMap, request, response, requestQueueStatus, login, password);
                }
            } catch (Exception ex) {
                String request = marshall(calt, ChangeApplicationListType.class);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                b2bRequestQueueCreate(request, sw.toString(), PUTCUTCHANGEINFO, CHANGE_REQUEST_QUEUE_STATUS_ERROR, login, password);
            }
        } else {
            logger.error("pdDeclaration not contain reasons");
        }
        logger.debug("processChange finished.");
    }

    @WsMethod()
    public Map<String, Object> dsLifeIntegrationPutChange(Map<String, Object> params) throws Exception {
        logger.debug("dsLifeIntegrationPutChange...");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            // выбор списка готовых к отправке допсов.
            List<Map<String, Object>> changeList = getChangeList(params, login, password);
            Long packSize = getLongParam(params.get("PACKSIZE"));
            int ps = packSize.intValue();

            int count = 0;

            // цикл по отправляемым допсам
            for (Map<String, Object> changeMap : changeList) {
                if (count >= ps) {
                    break;
                }
                // уточняем, был ли запрос по данному допсу ранее. если был, и был неуспешен, то переходим к следующему,
                // чтобы при вызове сперва обрабатывались ранее не отправленные, а после них - пытались отправится - ошибочные.
                if (changeMap.get("REQUESTQUEUEMAP") == null) {
                    Map<String, Object> requestMap = new HashMap<>();
                    requestMap.put("ReturnAsHashMap", "TRUE");
                    //requestMap.put("OBJID", changeMap.get(CHANGE_ID_PARAMNAME));
                    requestMap.put("OBJID", changeMap.get("ID"));
                    requestMap.put("REQUESTTYPEID", PUTCUTCHANGEINFO);
                    Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BFastRequestQueueBrowseListByParam", requestMap, login, password);
                    if (res != null) {
                        if (res.get("REQUESTSTATEID") != null) {
                            // возможно стоит дергать conginue независимо от статуса, т.к.
                            // если тут статус ОК = 1000 то у допса не перевелось состояние в "отправлен в ОИС" это ошибка.
                            if ("404".equalsIgnoreCase(getStringParam(res.get("REQUESTSTATEID")))) {
                                changeMap.put("REQUESTQUEUEMAP", res);
                                continue;
                            } else {
                                if ("1000".equalsIgnoreCase(getStringParam(res.get("REQUESTSTATEID")))) {
                                    changeMap.put("REQUESTQUEUEMAP", res);
                                    continue;
                                }
                            }
                        }
                    }
                }
                Map<String, Object> changeParam = new HashMap<>();
                changeParam.put(CHANGE_ID_PARAMNAME, changeMap.get("ID"));
                changeParam.put(RETURN_AS_HASH_MAP, "TRUE");
                Map<String, Object> changeRes = this.callService(B2BPOSWS, "dsB2BDeclarationOfChangeLoadByParams", changeParam, login, password);


                List<Map<String, Object>> reasonList = (List<Map<String, Object>>) changeRes.get("reasons");
                if (reasonList != null && reasonList.size() > 0) {

                    processChange(changeRes, params, login, password);
                    count++;
                } else {
                    logger.error("pdDeclaration not contain reasons");
                }
            }
            // среди заявок что до этого были отправлены с ошибками сначала отправим те, у которых TRYCOUNT наименьший
            changeList.sort(Comparator.comparing((Map changeMap) -> {
                Map requestQueueMap = getMapParam(changeMap, "REQUESTQUEUEMAP");
                Long tryCount = getLongParam(requestQueueMap, "TRYCOUNT");
                if (tryCount == null) {
                    return -1L;
                }
                return tryCount;
            }));
            // перепосылаем ошибки
            for (Map<String, Object> changeMap : changeList) {
                Map requestQueueMap = getMapParam(changeMap, "REQUESTQUEUEMAP");
                // если кол-во допсов в пакете хватает, то пытаемся отправить ранее отправлявшиеся с ошибкой.
                if ("404".equalsIgnoreCase(getStringParam(requestQueueMap, "REQUESTSTATEID"))) {
                    if (count >= ps) {
                        break;
                    }
                    Map<String, Object> changeParam = new HashMap<>();
                    changeParam.put(CHANGE_ID_PARAMNAME, changeMap.get("ID"));
                    changeParam.put(RETURN_AS_HASH_MAP, "TRUE");
                    Map<String, Object> changeRes = this.callService(B2BPOSWS, "dsB2BDeclarationOfChangeLoadByParams", changeParam, login, password);

                    List<Map<String, Object>> reasonList = (List<Map<String, Object>>) changeRes.get("reasons");
                    if (reasonList != null && reasonList.size() > 0) {
                        processChange(changeRes, params, login, password);
                        count++;
                    } else {
                        logger.error("pdDeclaration not contain reasons");
                    }
                }
            }

            result.put("STATUS", "DONE");
        } catch (Exception e) {
            logger.error("Partner service GetContractsCut call error", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String responseStr = sw.toString(); // stack trace as a string
            result.put("responseStr", responseStr);
            result.put("STATUS", "outERROR");
        }
        logger.debug("dsLifeIntegrationPutChange finished.");
        return result;
    }

    private List<Map<String, Object>> getChangeList(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put("STATEID", PD_DECLARATION_SENDING);
        searchParam.put("ID", params.get(CHANGE_ID_PARAMNAME));
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BDeclarationOfChangeLoadByParamsEx", searchParam, login, password);
        //Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BDeclarationOfChangeLoadByStateId", searchParam, login, password);
        if (res.get(RESULT) != null) {
            return (List<Map<String, Object>>) res.get(RESULT);
        }
        return null;
    }

    private ChangeApplicationListType mappingChange(Map<String, Object> changeMap, Map<String, Object> params, String login, String password) throws Exception {
        ChangeApplicationListType calt = null;
        List<Map<String, Object>> reasonList = (List<Map<String, Object>>) changeMap.get("reasons");
        if (reasonList != null && !reasonList.isEmpty()) {
            // группировка причин изменения по типу

            Map<String, List<Map<String, Object>>> groupedReasonsByType = new HashMap<>();
            for (Map<String, Object> reason : reasonList) {
                Map<String, Object> kindChangeReasonMap = (Map<String, Object>) reason.get("kindChangeReasonId_EN");
                if (kindChangeReasonMap != null) {
                    if (kindChangeReasonMap.get("externalId") != null) {
                        String changeReasonSysName = getStringParam(kindChangeReasonMap.get("externalId"));
                        if (groupedReasonsByType.containsKey(changeReasonSysName)) {
                            List<Map<String, Object>> group = groupedReasonsByType.get(changeReasonSysName);
                            group.add(reason);
                        } else {
                            List<Map<String, Object>> group = new ArrayList<>();
                            group.add(reason);
                            groupedReasonsByType.put(changeReasonSysName, group);
                        }
                    }
                }
            }

            calt = new ChangeApplicationListType();
            List<ChangeApplicationType> changeTypeList = calt.getApplicationChange();


            // цикд по группам изменений
            // в одном и том же допсе не может быть на стороне оис несколько изменений одного типа,
            // на нашей стороне мы храним в нескольких записях ризонов одного типа например массив изменившихся покрытий
            // или выгодопреобретателей.
            // группировка и цикл по группам нужны, чтобы собрать из разрозненых причин на нашей стороне
            // общий объект изменения, для отправки в оис.
            for (Map.Entry<String, List<Map<String, Object>>> groupEntry : groupedReasonsByType.entrySet()) {
                ChangeApplicationType change = new ChangeApplicationType();
                change.setApplicationDate(getFormattedDate(getDateParam(changeMap.get("createDate"))));
                List<Map<String, Object>> reasonListTyped = groupEntry.getValue();
                String changeReasonSysName = groupEntry.getKey();

                change.setChangeType(changeReasonSysName);
                // базовые поля причины изменения должны заполнятся одинаково,
                change.setEndorsementDate(change.getApplicationDate());
//                    if ((change.getEndorsementDate() == null) || (change.getEndorsementDate().isEmpty())) {
//                        change.setEndorsementDate(getFormattedDate(getCloserTrancheStartDate(calcTrahcheStartDate(), login, password)));
//                    }
                Map<String, Object> searchParams = new HashMap<>();
                if (changeMap.get("contractId") != null) {
                    searchParams.put("CONTRID", changeMap.get("contractId"));
                    searchParams.put("ReturnAsHashMap", "TRUE");
                    Map<String, Object> searchContrResult = this.callService(B2BPOSWS, "dsB2BIntegrationContractBrowseListByParam", searchParams, login, password);
                    if (searchContrResult != null) {
                        if (searchContrResult.get("EXTERNALID") != null) {
                            change.setPolicyId(getStringParam(searchContrResult.get("EXTERNALID")));
                        }
                        if (searchContrResult.get("CONTRNUMBER") != null) {
                            change.setPolicyNumber(getStringParam(searchContrResult.get("CONTRNUMBER")));
                        }
                    }
                }
                change.setComment("");

                // установка специфичных для различных допсов полей.
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format(
                            "Mapping additional reason kind dependent fields for change with system name '%s'...",
                            changeReasonSysName
                    ));
                }
                switch (changeReasonSysName) {
                    // опции
                    case "CHANGE_ASSET": {// Смена фонда
                        mapChangeAsset(change, reasonListTyped.get(0), login, password);
                        break;
                    }
                    case "FIXATION_ROI": {// Фиксация инвестиционного дохода
                        mapFixationRoi(change, reasonListTyped.get(0), login, password);
                        break;
                    }
                    case "EXTRA_PREMIUM": {// Внесение дополнительного страхового взноса
                        mapExtraPremium(change, reasonListTyped.get(0), login, password);
                        break;
                    }
                    case "EXTRA_ROI": {//Выплата дополнительного инвестиционного дохода
                        mapExtraRoi(change, reasonListTyped.get(0), login, password);
                        break;
                    }
                    //допсы
                    case "DECREASE_PERIOD": {// Изменение срока страхования//Сокращение срока страхования
                        mapDecreasePeriod(change, reasonListTyped.get(0));
                        break;
                    }
                    case "INCREASE_PERIOD": {// Изменение срока страхования//Увеличение срока страхования
                        mapIncreasePeriod(change, reasonListTyped.get(0));
                        break;
                    }
                    case "INSTALMENTS": {// Изменение периодичности оплаты страховой премии//Изменение периодичности оплаты
                        mapInstalments(change, reasonListTyped.get(0), login, password);
                        break;
                    }
                    case "INCREASE_INS_SUM": {
                        // Изменение размера страхового взноса/страховой суммы - Увеличение взноса / СС
                        // маппинг одинаковый и для увеличения и для уменьшения, разница только в системном имени допса
                        mapIncreaseDecreaseInsSum(change, reasonListTyped);
                        break;
                    }
                    case "DECREASE_INS_SUM": {
                        // Изменение размера страхового взноса/страховой суммы - Уменьшение взноса / СС
                        // маппинг одинаковый и для увеличения и для уменьшения, разница только в системном имени допса
                        mapIncreaseDecreaseInsSum(change, reasonListTyped);
                        break;
                    }
                    case "INCLUDE_PROGRAMS": {
                        // Изменение списка программ по договору - Включение программ
                        mapIncludePrograms(change, reasonListTyped);
                        break;
                    }
                    case "EXCLUDE_PROGRAMS": {
                        // Изменение списка программ по договору - Исключение программ
                        mapExcludePrograms(change, reasonListTyped);
                        break;
                    }
                    case "TRANSFER_TO_PAID": {// Перевод полиса в полностью оплаченный//Перевод в оплаченный
                        mapTransferToPaid(change, reasonListTyped.get(0));
                        break;
                    }
                    case "EXIT_FINANCIAL_VACATION": {// Финансовые каникулы//Выход из финансовых каникул
                        mapExitFinancialVacation(change, reasonListTyped.get(0), login, password);
                        break;
                    }
                    case "FINANCIAL_VACATION": {// Финансовые каникулы//Финансовые каникулы
                        mapFinancialVacation(change, reasonListTyped.get(0), login, password);
                        break;
                    }
                    case "DUBLICATE_DOCUMENT": {// Заявка на изготовление дубликата документа//Заявление на изготовление дубликата документа
                        // не участвует в интеграции
                        continue;
                        //mapDuplicateDocument(change, reason);
                        //break;
                    }
                    case "HOLDER_CHANGE": {// Изменение Страхователя//Изменение страхователя
                        mapHolderChange(change, reasonListTyped.get(0), login, password);
                        break;
                    }
                    case "BEN_CHANGE": {// Изменение списка Выгодоприобретателей//Изменение выгодоприобретателя
                        mapBenChange(change, reasonListTyped, login, password);
                        break;
                    }
                    case "LA_CHANGE": {// Изменение сведений о профессиональной деятельности Застрахованного лица//Изменение застрахованого
                        mapLaChange(change, reasonListTyped.get(0));
                        break;
                    }
                    case "H_CHANGE_CONTINFO": {// Изменение персональных данных участников договора//Изменение конт.информации страхователя

                        mapHChangeContInfo(change, reasonListTyped.get(0));
                        break;
                    }
                    case "B_CHANGE_PASSPORT": {// Изменение персональных данных участников договора//Изменение паспортных данных выгодоприобретателя
                        for (Map<String, Object> reason : reasonListTyped) {
                            mapBChangePassport(change, reason);
                        }
                        break;
                    }
                    case "LA_CHANGE_PASSPORT": {// Изменение персональных данных участников договора//Изменение паспортных данных застрахованного
                        for (Map<String, Object> reason : reasonListTyped) {
                            mapLaChangePassport(change, reason);
                        }
                        break;
                    }
                    case "H_CHANGE_PASSPORT": {// Изменение персональных данных участников договора//Изменение паспортных данных страхователя
                        for (Map<String, Object> reason : reasonListTyped) {
                            mapHChangePassport(change, reason);
                        }
                        break;
                    }
                    case "B_CHANGE_CONTINFO": {// Изменение персональных данных участников договора//Изменение конт.информации выгодоприобретателя
                        mapBChangeContInfo(change, reasonListTyped.get(0));
                        break;
                    }
                    case "H_CHANGE_PERSDATA": {// Изменение персональных данных участников договора//Изменение перс.данных страхователя
                        mapHChangePersData(change, reasonListTyped.get(0));
                        break;
                    }
                    case "LA_CHANGE_PERSDATA": {// Изменение персональных данных участников договора//Изменение перс.данных застрахованного
                        mapLaChangePersData(change, reasonListTyped.get(0));
                        break;
                    }
                    case "B_CHANGE_PERSDATA": {// Изменение персональных данных участников договора//Изменение перс.данных выгодоприобретателя
                        mapBChangePersData(change, reasonListTyped.get(0));
                        break;
                    }
                    case "B_CHANGE_SURNAME": {// Изменение персональных данных участников договора//Изменение фамилии выгодоприобретателя
                        mapBChangeSurname(change, reasonListTyped.get(0));
                        break;
                    }
                    case "H_CHANGE_ADDRESS": {// Изменение персональных данных участников договора//Изменение адреса страхователя
                        mapHChangeAddress(change, reasonListTyped.get(0));
                        break;
                    }
                    case "LA_CHANGE_SURNAME": {// Изменение персональных данных участников договора//Изменение фамилии застрахованного
                        mapLaChangeSurname(change, reasonListTyped.get(0));
                        break;
                    }
                    case "B_CHANGE_ADDRESS": {// Изменение персональных данных участников договора//Изменение адреса выгодоприобретателя
                        mapBChangeAddress(change, reasonListTyped.get(0));
                        break;
                    }
                    case "LA_CHANGE_ADDRESS": {// Изменение персональных данных участников договора//Изменение адреса застрахованного
                        mapLaChangeAddress(change, reasonListTyped.get(0));
                        break;
                    }
                    case "H_CHANGE_SURNAME": {// Изменение персональных данных участников договора//Изменение фамилии страхователя
                        mapHChangeSurname(change, reasonListTyped.get(0));
                        break;
                    }
                    case "LA_CHANGE_CONTINFO": {// Изменение персональных данных участников договора//Изменение конт.информации застрахованного
                        mapLaChangeContInfo(change, reasonListTyped.get(0));
                        break;
                    }

                    // Расторжение/Аннулирование
                    case "CANCELLATION": {
                        // Расторжение/Аннулирование - Расторжение (CANCELLATION)
                        mapCancellationChange(change, reasonListTyped, changeMap);
                        break;
                    }
                    case "ANNULMENT": {
                        // Расторжение/Аннулирование - Аннулирование (ANNULMENT)
                        mapAnnulmentChange(change, reasonListTyped, changeMap);
                        break;
                    }

                    case "H_CHANGE_E_AFTER_SAL_SERV": {
                        // Подключение опции ППО-Онлайн
                        mapPPOOnlineChange(change, reasonListTyped, changeMap);
                        break;
                    }

                }
                changeTypeList.add(change);

            }


        }
        return calt;
    }

    protected Map<String, Object> getPayVarDataByPayVarID(Long payVarID, String login, String password) throws Exception {
        // получение сведений о периодичности оплаты
        Map<String, Object> payVarParams = new HashMap<String, Object>();
        payVarParams.put("PAYVARID", payVarID);
        payVarParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> payVarInfo = this.callService(B2BPOSWS, "dsB2BPaymentVariantBrowseListByParam", payVarParams, login, password);
        return payVarInfo;
    }

    // допсы
    //Изменение срока страхования//Сокращение срока страхования
    private void mapDecreasePeriod(ChangeApplicationType change, Map<String, Object> reason) throws Exception {
        // сейчас не сохраняется!! и поля в ентити нет.
        change.setPolicyEndDate(getFormattedDate(getDateParam(reason.get("newFinishDate"))));

        String contrExtId = change.getPolicyId();
        GetObjType got = new GetObjType();
        // TODO: убрать тестовый ид, при пустом ид - не дергать клиентский севрис
        got.setPolicyId(getLongParam(contrExtId));

        ListContractType resContrList = callLifePartnerGetContracts(got);
        // add coverage list
        List<ContractType> ccList = resContrList.getContract();

        XMLGregorianCalendar newFinishDate = getFormattedDate(getDateParam(reason.get("newFinishDate")));

        for (ContractType contr : ccList) {
            ListCoverageType listCoverageType = contr.getCoverageList();
            List<Coverage> coverageList = listCoverageType.getCoverage();

            for (Coverage coverage : coverageList) {
                if (reason.get("newFinishDate") != null && coverage.getEndDate() != null) {
                    if (newFinishDate.compare(coverage.getEndDate()) == DatatypeConstants.LESSER) {
                        coverage.setEndDate(newFinishDate);
                    }
                }
            }
            change.setCoverageList(listCoverageType);
        }

    }

    //Изменение срока страхования//Увеличение срока страхования
    private void mapIncreasePeriod(ChangeApplicationType change, Map<String, Object> reason) throws Exception {
        // сейчас не сохраняется!! и поля в ентити нет.
        change.setPolicyEndDate(getFormattedDate(getDateParam(reason.get("newFinishDate"))));

        String contrExtId = change.getPolicyId();
        GetObjType got = new GetObjType();
        got.setPolicyId(getLongParam(contrExtId));

        ListContractType resContrList = callLifePartnerGetContracts(got);
        // add coverage list
        List<ContractType> ccList = resContrList.getContract();

        XMLGregorianCalendar newFinishDate = getFormattedDate(getDateParam(reason.get("newFinishDate")));

        for (ContractType contr : ccList) {
            ListCoverageType listCoverageType = contr.getCoverageList();
            List<Coverage> coverageList = listCoverageType.getCoverage();

            for (Coverage coverage : coverageList) {
                if (reason.get("newFinishDate") != null && coverage.getEndDate() != null) {
                    if (newFinishDate.compare(coverage.getEndDate()) == DatatypeConstants.GREATER) {
                        coverage.setEndDate(newFinishDate);
                    }
                }
            }
            change.setCoverageList(listCoverageType);
        }

    }

    //Изменение периодичности оплаты страховой премии//Изменение периодичности оплаты
    private void mapInstalments(ChangeApplicationType change, Map<String, Object> reason, String login, String password) throws Exception {
        Map<String, Object> payVarMap = getPayVarDataByPayVarID(getLongParam(reason.get("newPayVarId")), login, password);
        if (payVarMap.get("SYSNAME") != null)
            change.setPaymentPeriodicity(PERIODICITYMAP.get(getStringParam(payVarMap.get("SYSNAME"))));
    }

    //Изменение размера страхового взноса/страховой суммы//Увеличение взноса / СС
    private void mapIncreaseDecreaseInsSum(ChangeApplicationType change, List<Map<String, Object>> reasonList) {
        // ждем описание от сбсж куда сохранять.
        String contrExtId = change.getPolicyId();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("POLICYID", contrExtId);
        GetObjType got = new GetObjType();

        // TODO: убрать тестовый ид, при пустом ид - не дергать клиентский севрис

        got.setPolicyId(getLongParam(contrExtId));

        ListContractType resContrList = callLifePartnerGetContracts(got);
        List<ContractType> ccList = resContrList.getContract();
        for (ContractType contr : ccList) {
            ListCoverageType listCoverageType = contr.getCoverageList();
            List<Coverage> coverageList = listCoverageType.getCoverage();

            for (Coverage coverage : coverageList) {
                for (Map<String, Object> reason : reasonList) {
                    if (coverage.getCoverageName().equalsIgnoreCase(getStringParam(reason.get("riskSysName")))) {
                        ListCoverageDetType lcdt = coverage.getCoverageDetList();
                        List<CoverageDet> covDetList = lcdt.getCoverageDet();
                        for (CoverageDet covDet : covDetList) {
                            if (reason.get("newInsAmValue") != null) {
                                covDet.setAmountAssured(getBigDecimalParam(reason.get("newInsAmValue")));
                            }
                            if (reason.get("newPremValue") != null) {
                                covDet.setAmountPrem(getBigDecimalParam(reason.get("newPremValue")));
                            }
                        }
                    }
                }
            }
            change.setCoverageList(listCoverageType);
        }
    }

    // Изменение списка программ по договору - Включение программ
    private void mapIncludePrograms(ChangeApplicationType change, List<Map<String, Object>> reasonList) {
        mapIncludeExcludePrograms(change, reasonList);
    }

    // Изменение списка программ по договору - Исключение программ
    private void mapExcludePrograms(ChangeApplicationType change, List<Map<String, Object>> reasonList) {
        mapIncludeExcludePrograms(change, reasonList);
    }

    // Изменение списка программ по договору - Включение программ | Исключение программ
    private void mapIncludeExcludePrograms(ChangeApplicationType change, List<Map<String, Object>> reasonList) {
        List<ContractType> contractList = getContractListForChange(change);
        if ((contractList != null) && (reasonList != null)) {
            for (ContractType contract : contractList) {
                if (contract == null) {
                    // недостаточно обязательных параметров - следует перейти к следующему элементу списка
                    continue;
                }
                // список покрытий из текущего договора
                List<Coverage> coverageList = getContractCoverageList(contract);

                for (Map<String, Object> reason : reasonList) {
                    // сис. наименование риска
                    String riskSysName = getStringParam(reason, "riskSysName");
                    // внешнее сис. наименование причины изменения
                    String kindChangeReasonExternalId = getKindChangeReasonExternalId(reason);
                    // thirdPartyId/externalId застрахованного лица, выбранного в интерфейсе
                    Long insuredThirdPartyId = getLongParam(reason, "thirdPartyId");

                    if (riskSysName.isEmpty() || kindChangeReasonExternalId.isEmpty() || (insuredThirdPartyId == null)) {
                        // недостаточно обязательных параметров - следует перейти к следующему элементу списка
                        logger.warn(String.format(
                                "[mapIncludeExcludePrograms] Not all required parameters are supplied - reason processing will be skipped. Details (reason map): %s.",
                                reason
                        ));
                        continue;
                    }

                    // Выбор типа изменения: Удаление/Добавление
                    // tns:ChangeImport/tns:ApplicationChange/tns:ChangeType = "...";
                    // Удалене: 'EXCLUDE_PROGRAMS'; Добавление: 'INCLUDE_PROGRAMS'
                    change.setChangeType(kindChangeReasonExternalId);

                    // Застрахованное лицо: Полное имя
                    // tns:ChangeImport/tns:ApplicationChange/tns:ThirdPartys/tns:ThirdParty/tns:FullName

                    // полное ФИО застрахованного лица, выбранного в интерфейсе (определяется позднее)
                    String fullName = "";
                    // получение полного ФИО застрахованного лица, выбранного в интерфейсе, из договора по thirdPartyId/externalId
                    List<ThirdParty> contractThirdPartyList = contract.getThirdPartyList().getThirdParty();
                    for (ThirdParty thirdParty : contractThirdPartyList) {
                        Long thirdPartyId = thirdParty.getThirdPartyId();
                        if (insuredThirdPartyId.equals(thirdPartyId)) {
                            fullName = thirdParty.getFullName();
                            break;
                        }
                    }
                    // получение и/или создание записи thirdParty в допсе для установки полного ФИО застрахованного лица
                    ThirdParty changeThirdParty = getOrCreateChangeThirdPartyRecord(change);
                    // установка полного ФИО застрахованного лица в допсе
                    changeThirdParty.setFullName(fullName);

                    if ("INCLUDE_PROGRAMS".equals(kindChangeReasonExternalId)) {
                        // INCLUDE_PROGRAMS - добавление программы
                        //
                        // проверка наличия данного покрытия для данного лица в текущем списке покрытий
                        boolean isCoverageNotIncludedYet = true;
                        for (Coverage coverage : coverageList) {
                            if (coverage != null) {
                                if (riskSysName.equals(coverage.getCoverageName())) {
                                    LifeAssureds lifeAssureds = coverage.getLifeAssureds();
                                    if (lifeAssureds != null) {
                                        List<LifeAssured> lifeAssuredList = lifeAssureds.getLifeAssured();
                                        // список застрахованных по покрытию не пуст - следует найти в нем лицо, указанное в интерфейсе при регистрации допса
                                        for (LifeAssured lifeAssured : lifeAssuredList) {
                                            Long lifeAssuredThirdPartyId = lifeAssured.getThirdPartyId();
                                            if (insuredThirdPartyId.equals(lifeAssuredThirdPartyId)) {
                                                // найдено лицо, указанное в интерфейсе при регистрации допса
                                                isCoverageNotIncludedYet = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (isCoverageNotIncludedYet) {
                            // формирование нового экзепляра покрытия
                            Coverage includedCoverage = new Coverage();
                            ListCoverageDetType listCoverageDetType = new ListCoverageDetType();
                            CoverageDet coverageDet = new CoverageDet();
                            listCoverageDetType.getCoverageDet().add(coverageDet);
                            includedCoverage.setCoverageDetList(listCoverageDetType);
                            // Программа
                            // tns:ChangeImport/tns:ApplicationChange/tns:CoverageList/tns:Coverage/tns:CoverageName
                            includedCoverage.setCoverageName(riskSysName);
                            // Величина страховой суммы
                            // tns:ChangeImport/tns:ApplicationChange/tns:CoverageList/tns:Coverage/tns:CoverageDetList/tns:CoverageDet/tns:AmountAssured
                            BigDecimal amountAssured = getBigDecimalParam(reason, "insAmValue");
                            coverageDet.setAmountAssured(amountAssured);
                            // Валюта
                            // tns:ChangeImport/tns:ApplicationChange/tns:CoverageList/tns:Coverage/tns:CoverageDetList/tns:CoverageDet/tns:Currency
                            CurrencyType contractCurrency = CurrencyType.fromValue(contract.getCurrency());
                            coverageDet.setCurrency(contractCurrency);
                            // Страховая премия
                            // tns:ChangeImport/tns:ApplicationChange/tns:CoverageList/tns:Coverage/tns:CoverageDetList/tns:CoverageDet/tns:AmountPrem
                            // Глеб Д. (07.11.2017): "Уточню этот момент."
                            coverageDet.setAmountPrem(BigDecimal.ZERO);
                            // Начало действия покрытия
                            // tns:ChangeImport/tns:ApplicationChange/tns:CoverageList/tns:Coverage/tns:StartDate
                            // Маппинг_ДОП_CXSD_v1 (10): "В старых программах оставляем пришедшее из ОИС значение, в новых дату внесения изменения"
                            // "Дата внесения изменения (транша) - EndorsementDate"
                            includedCoverage.setStartDate(change.getEndorsementDate());
                            // Конец действия покрытия
                            // tns:ChangeImport/tns:ApplicationChange/tns:CoverageList/tns:Coverage/tns:EndDate
                            // Маппинг_ДОП_CXSD_v1 (10): "дату окончания полиса"
                            includedCoverage.setEndDate(contract.getPolicyEndDate());
                            // новое покрытие следует добавить в список
                            coverageList.add(includedCoverage);
                        }
                    } else if ("EXCLUDE_PROGRAMS".equals(kindChangeReasonExternalId)) {
                        // EXCLUDE_PROGRAMS - исключение программы
                        //
                        Coverage excludedCoverage = null;
                        for (Coverage coverage : coverageList) {
                            if ((coverage == null) || !riskSysName.equals(coverage.getCoverageName())) {
                                // недостаточно обязательных параметров - следует перейти к следующему элементу списка
                                logger.warn(String.format(
                                        "[mapIncludeExcludePrograms] Not all required parameters are supplied - this coverage will be skipped. Details (coverage): %s",
                                        coverage
                                ));
                                continue;
                            }
                            // системное имя покрытия совпадает с искомым - следует проверить застрахованных
                            // список застрахованных по покрытию
                            LifeAssureds lifeAssureds = coverage.getLifeAssureds();
                            if (lifeAssureds != null) {
                                List<LifeAssured> lifeAssuredList = lifeAssureds.getLifeAssured();
                                if (!lifeAssuredList.isEmpty()) {
                                    // список застрахованных по покрытию не пуст - следует найти в нем лицо, указанное в интерфейсе при регистрации допса
                                    LifeAssured excludedLifeAssured = null;
                                    for (LifeAssured lifeAssured : lifeAssuredList) {
                                        Long lifeAssuredThirdPartyId = lifeAssured.getThirdPartyId();
                                        if (insuredThirdPartyId.equals(lifeAssuredThirdPartyId)) {
                                            // найдено лицо, указанное в интерфейсе при регистрации допса
                                            excludedLifeAssured = lifeAssured;
                                            break;
                                        }
                                    }
                                    if (excludedLifeAssured != null) {
                                        // лицо, указанное в интерфейсе при регистрации допса, было найденое в списке застрахованных по покрытию
                                        // следует исключть его из застрахованных
                                        lifeAssuredList.remove(excludedLifeAssured);
                                    }
                                }
                                if (lifeAssuredList.isEmpty()) {
                                    // если по покрытию нет застрахованных (или единсвенный из них был исключен из списка застрахованных)
                                    // то следует исключить и само покрытие
                                    excludedCoverage = coverage;
                                    break;
                                }
                            }
                        }
                        if (excludedCoverage != null) {
                            coverageList.remove(excludedCoverage);
                        }
                    }

                }
                ListCoverageType changelistCoverageType = new ListCoverageType();
                changelistCoverageType.getCoverage().addAll(coverageList);
                change.setCoverageList(changelistCoverageType);
            }
        }
    }

    // внешнее сис. наименование причины изменения
    private String getKindChangeReasonExternalId(Map<String, Object> reason) {
        // причина изменения
        Map<String, Object> kindChangeReason = getMapParam(reason, "kindChangeReasonId_EN");
        // внешнее сис. наименование причины изменения
        String kindChangeReasonExternalId = getStringParam(kindChangeReason, "externalId");
        return kindChangeReasonExternalId;
    }

    // получение и/или создание записи thirdParty в допсе
    private ThirdParty getOrCreateChangeThirdPartyRecord(ChangeApplicationType change) {
        ThirdPartyListType changeThirdPartyListType = change.getThirdPartys();
        if (changeThirdPartyListType == null) {
            changeThirdPartyListType = new ThirdPartyListType();
            change.setThirdPartys(changeThirdPartyListType);
        }
        List<ThirdParty> changeThirdPartyList = changeThirdPartyListType.getThirdParty();
        ThirdParty changeThirdParty = null;
        if (!changeThirdPartyList.isEmpty()) {
            changeThirdParty = changeThirdPartyList.get(0);
        }
        if (changeThirdParty == null) {
            changeThirdParty = new ThirdParty();
            changeThirdPartyList.add(changeThirdParty);
        }
        return changeThirdParty;
    }

    //
    private List<Coverage> getContractCoverageList(ContractType contract) {
        ListCoverageType listCoverageType = null;
        if (contract != null) {
            listCoverageType = contract.getCoverageList();
        }
        if (listCoverageType == null) {
            listCoverageType = new ListCoverageType();
        }
        List<Coverage> coverageList = listCoverageType.getCoverage();
        return coverageList;
    }

    private List<ContractType> getContractListForChange(ChangeApplicationType change) {
        List<ContractType> contractList = null;
        if (change != null) {
            // ИД договора в ОИС
            String policyIdStr = change.getPolicyId();
            Long policyId = getLongParam(policyIdStr);
            if (policyId != null) {
                GetObjType got = new GetObjType();
                got.setPolicyId(policyId);
                ListContractType contractListResult = callLifePartnerGetContracts(got);
                if (contractListResult != null) {
                    contractList = contractListResult.getContract();
                }
            }
        }
        return contractList;
    }

    //Перевод полиса в полностью оплаченный//Перевод в оплаченный
    private void mapTransferToPaid(ChangeApplicationType change, Map<String, Object> reason) {
        // не описан
    }

    //Финансовые каникулы//Выход из финансовых каникул
    private void mapExitFinancialVacation(ChangeApplicationType change, Map<String, Object> reason, String login, String password) throws Exception {
        // ждем описание от сбсж куда сохранять. пока и в ентити пусто, и описания нет.
        //mapFinancialVacation(change,reason,login,password);
    }

    //Финансовые каникулы//Финансовые каникулы
    private void mapFinancialVacation(ChangeApplicationType change, Map<String, Object> reason, String login, String password) throws Exception {
        // ждем описание от сбсж куда сохранять.
        String contrExtId = change.getPolicyId();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("POLICYID", contrExtId);
        GetObjType got = new GetObjType();

        // TODO: убрать тестовый ид, при пустом ид - не дергать клиентский севрис

        got.setPolicyId(getLongParam(contrExtId));

        ListContractType resContrList = callLifePartnerGetContracts(got);
        List<ContractType> ccList = resContrList.getContract();
        for (ContractType contr : ccList) {
            ListCoverageType listCoverageType = contr.getCoverageList();
            List<Coverage> coverageList = listCoverageType.getCoverage();

            for (Coverage coverage : coverageList) {
                if (reason.get("startFinHolidayDate") != null) {
                    coverage.setStartDate(getFormattedDate(getDateParam(reason.get("startFinHolidayDate"))));
                }
                if (reason.get("endFinHolidayDate") != null) {
                    coverage.setEndDate(getFormattedDate(getDateParam(reason.get("endFinHolidayDate"))));
                }
            }
            change.setCoverageList(listCoverageType);

        }
    }

    //Заявка на изготовление дубликата документа//Заявление на изготовление дубликата документа
    private void mapDuplicateDocument(ChangeApplicationType change, Map<String, Object> reason) {
        // не участвует в интеграции. нужно ли исключать на этапе обхода?
    }

    //Изменение Страхователя//Изменение страхователя
    private void mapHolderChange(ChangeApplicationType change, Map<String, Object> reason, String login, String password) throws Exception {
        ThirdPartyListType tplt = new ThirdPartyListType();
        ThirdParty tp = new ThirdParty();
        Map<String, Object> insurantMap = (Map<String, Object>) reason.get("insurantId_EN");

        tp.setLastName(getStringParam(insurantMap.get("surname")));
        tp.setFirstName(getStringParam(insurantMap.get("name")));
        tp.setPatronymic(getStringParam(insurantMap.get("patronymic")));
        tp.setBirthDate(getFormattedDate(getDateParam(insurantMap.get("dateOfBirth"))));
        //гражданство не делаем т.к. оис принять не сможет.
        //tp.setCitizenship(getCountryNameCodeById(getStringParam(insurantMap.get("countryId")),login, password));
        tp.setTin(getStringParam(insurantMap.get("inn")));
        tp.setBirthPlace(getStringParam(insurantMap.get("placeOfBirth")));
        // контакты
        List<Map<String, Object>> contactList = (List<Map<String, Object>>) insurantMap.get("contacts");
        for (Map<String, Object> contactMap : contactList) {
            Map<String, Object> contType = (Map<String, Object>) contactMap.get("typeId_EN");
            if ("MobilePhone".equals(getStringParam(contType.get("sysname")))) {
                tp.setPhoneMobile(getStringParam(contactMap.get("value")));
            }
            if ("WorkAddressPhone".equals(getStringParam(contType.get("sysname")))) {
                tp.setPhoneWorking(getStringParam(contactMap.get("value")));
            }
            if ("FactAddressPhone".equals(getStringParam(contType.get("sysname")))) {
                tp.setPhoneHome(getStringParam(contactMap.get("value")));
            }
            if ("PersonalEmail".equals(getStringParam(contType.get("sysname")))) {
                tp.setEmail(getStringParam(contactMap.get("value")));
            }
        }
        // доки
        List<Map<String, Object>> documentList = (List<Map<String, Object>>) insurantMap.get("documents");
        DocumentsListType docListType = new DocumentsListType();
        List<DocumentsType> docTypeList = docListType.getDocument();
        for (Map<String, Object> documentMap : documentList) {
            docTypeList.add(getDocByMap(documentMap));
        }
        tp.setDocumentsList(docListType);

        ListAddressType listAddressType = new ListAddressType();
        List<Address> addresses = listAddressType.getAddress();
        List<Map<String, Object>> addressList = (List<Map<String, Object>>) insurantMap.get("addresses");
        for (Map<String, Object> addressMap : addressList) {
            addresses.add(getAddressByMap(addressMap));
        }

        tp.setListAddress(listAddressType);

        tp.setRole(RolesType.HOLDER);
        tp.setThirdPartyId(getLongParam(insurantMap.get("thirdPartyId")));
        List<ThirdParty> tpList = tplt.getThirdParty();
        tpList.add(tp);
        change.setThirdPartys(tplt);
    }

    //Изменение списка Выгодоприобретателей//Изменение выгодоприобретателя
    private void mapBenChange(ChangeApplicationType change, List<Map<String, Object>> reasonList, String login, String password) throws Exception {
        // пока на обсуждении, возможно будет передаватся всегда полный итоговый список. чтобы в оис все удалить, и заменить
        // данными от нас.
        // 1. запрашиваем fullInfo договора из ОИС.
        // если оис не отвечаем - пропускаем данный допс. он попытается обработаться позднее. возможно оис ответит.
        // если оис ответил. накладываем на список выгодопреобретателей из оис дельту сохраненную в reasonList
        // итоговый список бенефициаров маппим в thirdpartyList в change
        String contrExtId = change.getPolicyId();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("POLICYID", contrExtId);
        //queryParams.put(RETURN_AS_HASH_MAP, true);
        // вызов сортировки договоров по типу продуктов
        Map<String, Object> fullMap = this.callService(B2BPOSWS, "dsLifeIntegrationGetContractList", queryParams, login, password);
        logger.debug(fullMap.toString());

        List<Map<String, Object>> oldBenList = null;
        List<Map<String, Object>> newBenList = new ArrayList<>();
        Map<String, Object> contrMap = null;
        if (fullMap.get(RESULT) != null) {
            Map<String, Object> fullMapRes = (Map<String, Object>) fullMap.get(RESULT);
            if (fullMapRes.get("CONTRMAP") != null) {
                contrMap = (Map<String, Object>) fullMapRes.get("CONTRMAP");
                if (contrMap.get("BENEFICIARYLIST") != null) {
                    oldBenList = (List<Map<String, Object>>) contrMap.get("BENEFICIARYLIST");
                }
            }
        }
        if (oldBenList == null) {
            oldBenList = new ArrayList<>();
        }
        newBenList.addAll(oldBenList);
        for (Map<String, Object> reason : reasonList) {
            // удаление выгодопреобретателя. найти такового в списке, и удалить / не добавлять в актуальный список.
            if (reason.get("oldThirdPartyId") != null) {
                deleteBenFromList(newBenList, oldBenList, getStringParam(reason.get("oldThirdPartyId")), getStringParam(reason.get("riskCode")));
            }
            // изменение выгодопреобретателя
            if ((reason.get("newThirdPartyId") != null) || (reason.get("personId") != null)) {
                //добавить выгодопреобретателя
                addBenToList(newBenList, getStringParam(reason.get("riskCode")), reason.get("part"), reason, contrMap);
            }

        }

        ThirdPartyListType tplt = new ThirdPartyListType();
        List<ThirdParty> tpList = tplt.getThirdParty();

        for (Map<String, Object> benMap : newBenList) {
            if ("1".equalsIgnoreCase(getStringParam(benMap.get("isDeleted")))) {
                continue;
            } else {
                ThirdParty tp = new ThirdParty();
                tp.setRole(RolesType.BEN);
                if (benMap.get("PART") != null) {
                    tp.setSplit(getLongParam(benMap.get("PART")).intValue());
                }
                if (benMap.get("RISKCODE") != null) {
                    tp.setRiskCode(getStringParam(benMap.get("RISKCODE")));
                }
                if (benMap.get("PERSONID_EN") != null) {
                    Map<String, Object> personMap = (Map<String, Object>) benMap.get("PERSONID_EN");
                    mapPersonFromHibernatePPersonMap(tp, personMap);

                } else {
                    if (benMap.get("PARTICIPANTMAP") != null) {
                        Map<String, Object> personMap = (Map<String, Object>) benMap.get("PARTICIPANTMAP");
                        mapPersonFromCRMParticipantMap(tp, personMap);
                    }
                }
                tpList.add(tp);
            }
        }
        change.setThirdPartys(tplt);

        //change.set
    }

    private void mapPersonFromCRMParticipantMap(ThirdParty tp, Map<String, Object> personMap) throws Exception {
        if (personMap.get("THIRDPARTYID") != null) {
            tp.setThirdPartyId(getLongParam(personMap.get("THIRDPARTYID")));
        } else {
            tp.setLastName(getStringParam(personMap.get("LASTNAME")));
            tp.setFirstName(getStringParam(personMap.get("FIRSTNAME")));
            tp.setPatronymic(getStringParam(personMap.get("MIDDLENAME")));

            tp.setBirthDate(getFormattedDate(getDateParam(personMap.get("BIRTHDATE"))));
            tp.setGender(GENDERMAP.get(getStringParam(personMap.get("GENDER"))));
            //гражданство не делаем т.к. оис принять не сможет.
            //tp.setCitizenship(getCountryNameCodeById(getStringParam(insurantMap.get("countryId")),login, password));
            tp.setTin(getStringParam(personMap.get("INN")));
            tp.setBirthPlace(getStringParam(personMap.get("BIRTHPLACE")));
            // контакты
            List<Map<String, Object>> contactList = (List<Map<String, Object>>) personMap.get("contactList");
            for (Map<String, Object> contactMap : contactList) {
                if ("MobilePhone".equals(getStringParam(contactMap.get("CONTACTTYPESYSNAME")))) {
                    tp.setPhoneMobile(getStringParam(contactMap.get("value")));
                }
                if ("WorkAddressPhone".equals(getStringParam(contactMap.get("CONTACTTYPESYSNAME")))) {
                    tp.setPhoneWorking(getStringParam(contactMap.get("value")));
                }
                if ("FactAddressPhone".equals(getStringParam(contactMap.get("CONTACTTYPESYSNAME")))) {
                    tp.setPhoneHome(getStringParam(contactMap.get("value")));
                }
                if ("PersonalEmail".equals(getStringParam(contactMap.get("CONTACTTYPESYSNAME")))) {
                    tp.setEmail(getStringParam(contactMap.get("value")));
                }
            }
            // доки
            List<Map<String, Object>> documentList = (List<Map<String, Object>>) personMap.get("documentList");
            DocumentsListType docListType = new DocumentsListType();
            List<DocumentsType> docTypeList = docListType.getDocument();
            for (Map<String, Object> documentMap : documentList) {
                docTypeList.add(getDocByPartMap(documentMap));
            }
            tp.setDocumentsList(docListType);

            ListAddressType listAddressType = new ListAddressType();
            List<Address> addresses = listAddressType.getAddress();
            List<Map<String, Object>> addressList = (List<Map<String, Object>>) personMap.get("addressList");
            for (Map<String, Object> addressMap : addressList) {
                addresses.add(getAddressByPartMap(addressMap));
            }

            tp.setListAddress(listAddressType);

        }

    }

    private void mapPersonFromHibernatePPersonMap(ThirdParty tp, Map<String, Object> personMap) throws Exception {
        if (personMap.get("thirdPartyId") != null) {
            tp.setThirdPartyId(getLongParam(personMap.get("thirdPartyId")));
        } else {

            tp.setLastName(getStringParam(personMap.get("surname")));
            tp.setFirstName(getStringParam(personMap.get("name")));
            tp.setPatronymic(getStringParam(personMap.get("patronymic")));
            tp.setBirthDate(getFormattedDate(getDateParam(personMap.get("dateOfBirth"))));
            //гражданство не делаем т.к. оис принять не сможет.
            //tp.setCitizenship(getCountryNameCodeById(getStringParam(insurantMap.get("countryId")),login, password));
            tp.setTin(getStringParam(personMap.get("inn")));
            tp.setBirthPlace(getStringParam(personMap.get("placeOfBirth")));
            // контакты
            List<Map<String, Object>> contactList = (List<Map<String, Object>>) personMap.get("contacts");
            for (Map<String, Object> contactMap : contactList) {
                Map<String, Object> contType = (Map<String, Object>) contactMap.get("typeId_EN");
                if ("MobilePhone".equals(getStringParam(contType.get("sysname")))) {
                    tp.setPhoneMobile(getStringParam(contactMap.get("value")));
                }
                if ("WorkAddressPhone".equals(getStringParam(contType.get("sysname")))) {
                    tp.setPhoneWorking(getStringParam(contactMap.get("value")));
                }
                if ("FactAddressPhone".equals(getStringParam(contType.get("sysname")))) {
                    tp.setPhoneHome(getStringParam(contactMap.get("value")));
                }
                if ("PersonalEmail".equals(getStringParam(contType.get("sysname")))) {
                    tp.setEmail(getStringParam(contactMap.get("value")));
                }
            }
            // доки
            List<Map<String, Object>> documentList = (List<Map<String, Object>>) personMap.get("documents");
            DocumentsListType docListType = new DocumentsListType();
            List<DocumentsType> docTypeList = docListType.getDocument();
            for (Map<String, Object> documentMap : documentList) {
                docTypeList.add(getDocByMap(documentMap));
            }
            tp.setDocumentsList(docListType);

            ListAddressType listAddressType = new ListAddressType();
            List<Address> addresses = listAddressType.getAddress();
            List<Map<String, Object>> addressList = (List<Map<String, Object>>) personMap.get("addresses");
            for (Map<String, Object> addressMap : addressList) {
                addresses.add(getAddressByMap(addressMap));
            }

            tp.setListAddress(listAddressType);

        }

    }

    private void addBenToList(List<Map<String, Object>> newBenList, String riskCode, Object part, Map<String, Object> reason
            , Map<String, Object> contrMap) {
        Map<String, Object> newBenMap = new HashMap<>();
        newBenMap.put("isNew", "1");
        newBenMap.put("PART", part);
        newBenMap.put("RISKCODE", riskCode);
        if (reason.get("personId") != null) {
            newBenMap.put("PERSONID_EN", reason.get("personId_EN"));
        } else {
            if (reason.get("newThirdPartyId") != null) {
                if (contrMap != null) {
                    if (contrMap.get("MEMBERLIST") != null) {
                        List<Map<String, Object>> memberList = (List<Map<String, Object>>) contrMap.get("MEMBERLIST");
                        for (Map<String, Object> member : memberList) {
                            if (member.get("PARTICIPANTMAP") != null) {
                                Map<String, Object> partMap = (Map<String, Object>) member.get("PARTICIPANTMAP");
                                if (partMap.get("THIRDPARTYID") != null) {
                                    String thirdPartyId = getStringParam(reason.get("newThirdPartyId"));
                                    if (thirdPartyId.equalsIgnoreCase(getStringParam(partMap.get("THIRDPARTYID")))) {
                                        newBenMap.put("PARTICIPANTMAP", partMap);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        newBenList.add(newBenMap);
    }

    private void deleteBenFromList(List<Map<String, Object>> newBenList, List<Map<String, Object>> oldBenList, String oldThirdPartyId, String riskCode) {
        for (Map<String, Object> benMap : newBenList) {
            String riskCodeInList = getStringParam(benMap.get("RISKCODE"));
            if (riskCodeInList.equalsIgnoreCase(riskCode)) {
                if (benMap.get("PARTICIPANTMAP") != null) {
                    Map<String, Object> partMap = (Map<String, Object>) benMap.get("PARTICIPANTMAP");
                    if (partMap.get("THIRDPARTYID") != null) {
                        String thirdPartyIdInList = getStringParam(partMap.get("THIRDPARTYID"));
                        if (thirdPartyIdInList.equalsIgnoreCase(oldThirdPartyId)) {
                            if (!benMap.containsKey("isNew")) {
                                benMap.put("isDeleted", "1");
                            }
                        }
                    }
                }
            }
        }
    }

    //Изменение сведений о профессиональной деятельности Застрахованного лица//Изменение застрахованого
    private void mapLaChange(ChangeApplicationType change, Map<String, Object> reason) {
        // не делаем т.к. нет в оис.
    }

    //Изменение контактных данных
    //Изменение персональных данных участников договора//Изменение конт.информации страхователя
    private void mapHChangeContInfo(ChangeApplicationType change, Map<String, Object> reason) {
        changeContInfo(change, reason, RolesType.HOLDER);
    }

    //Изменение персональных данных участников договора//Изменение конт.информации выгодоприобретателя
    private void mapBChangeContInfo(ChangeApplicationType change, Map<String, Object> reason) {
        changeContInfo(change, reason, RolesType.BEN);
    }

    //Изменение персональных данных участников договора//Изменение конт.информации застрахованного
    private void mapLaChangeContInfo(ChangeApplicationType change, Map<String, Object> reason) {
        changeContInfo(change, reason, RolesType.LIFE_ASSURED);
    }

    private void changeContInfo(ChangeApplicationType change, Map<String, Object> reason, RolesType role) {
        ThirdPartyListType tplt = new ThirdPartyListType();
        ThirdParty tp = new ThirdParty();

        tp.setPhoneMobile(getStringParam(reason.get("phone")));
        tp.setEmail(getStringParam(reason.get("email")));

        tp.setRole(role);
        tp.setThirdPartyId(getLongParam(reason.get("thirdPartyId")));
        List<ThirdParty> tpList = tplt.getThirdParty();
        tpList.add(tp);
        change.setThirdPartys(tplt);
    }

    //Изменение паспорта
    //Изменение персональных данных участников договора//Изменение паспортных данных выгодоприобретателя
    private void mapBChangePassport(ChangeApplicationType change, Map<String, Object> reason) throws Exception {
        changePassport(change, reason, RolesType.BEN);
    }

    //Изменение персональных данных участников договора//Изменение паспортных данных застрахованного
    private void mapLaChangePassport(ChangeApplicationType change, Map<String, Object> reason) throws Exception {
        changePassport(change, reason, RolesType.LIFE_ASSURED);
    }

    //Изменение персональных данных участников договора//Изменение паспортных данных страхователя
    private void mapHChangePassport(ChangeApplicationType change, Map<String, Object> reason) throws Exception {
        changePassport(change, reason, RolesType.HOLDER);
    }

    private void changePassport(ChangeApplicationType change, Map<String, Object> reason, RolesType role) throws Exception {
        ThirdPartyListType tplt = new ThirdPartyListType();
        ThirdParty tp = new ThirdParty();
        Map<String, Object> documentMap = (Map<String, Object>) reason.get("documentId_EN");

        // доки
        DocumentsListType docListType = new DocumentsListType();
        List<DocumentsType> docTypeList = docListType.getDocument();
        docTypeList.add(getDocByMap(documentMap));
        tp.setDocumentsList(docListType);
        tp.setRole(role);
        tp.setThirdPartyId(getLongParam(reason.get("thirdPartyId")));
        List<ThirdParty> tpList = tplt.getThirdParty();
        tpList.add(tp);
        change.setThirdPartys(tplt);        // не описано
    }

    private DocumentsType getDocByPartMap(Map<String, Object> documentMap) {
        DocumentsType docType = new DocumentsType();
        if (documentMap != null) {
            docType.setDocumentType(DOCUMENTTYPEMAP.get(getStringParam(documentMap.get("DOCTYPESYSNAME"))));
            docType.setDocumentSeries(getStringParam(documentMap.get("DOCSERIES")));
            docType.setDocumentNumber(getStringParam(documentMap.get("DOCNUMBER")));
            docType.setDocumentFull(getStringParam(documentMap.get("04 02 № 499125 выдан 12.10.2002 увд канска")));
            try {
                docType.setDocumentDate(getFormattedDate(getDateParam(documentMap.get("ISSUEDATE"))));
            } catch (Exception e) {
                logger.error("Error process document issue date from doc" + docType.getDocumentSeries() + " " + docType.getDocumentNumber(), e);
            }
            docType.setDocumentInstitution(getStringParam(documentMap.get("ISSUEDBY")));
            docType.setDocumentCodeIns(getStringParam(documentMap.get("ISSUERCODE")));
        }
        return docType;
    }

    private DocumentsType getDocByMap(Map<String, Object> documentMap) {
        DocumentsType docType = new DocumentsType();
        if (documentMap != null) {
            Map<String, Object> type = (Map<String, Object>) documentMap.get("typeId_EN");
            if (type != null) {
                docType.setDocumentType(DOCUMENTTYPEMAP.get(getStringParam(type.get("sysname"))));
                docType.setDocumentSeries(getStringParam(documentMap.get("series")));
                docType.setDocumentNumber(getStringParam(documentMap.get("no")));
                try {
                    docType.setDocumentDate(getFormattedDate(getDateParam(documentMap.get("dateOfIssue"))));
                } catch (Exception e) {
                    logger.error("Error process document issue date from doc" + docType.getDocumentSeries() + " " + docType.getDocumentNumber(), e);
                }
                docType.setDocumentInstitution(getStringParam(documentMap.get("authority")));
                docType.setDocumentCodeIns(getStringParam(documentMap.get("issuerCode")));
            }
        }
        return docType;
    }

    //изменение персональных данных
    //Изменение персональных данных участников договора//Изменение перс.данных страхователя
    private void mapHChangePersData(ChangeApplicationType change, Map<String, Object> reason) throws Exception {
        changePersData(change, reason, RolesType.HOLDER);
    }

    //Изменение персональных данных участников договора//Изменение перс.данных застрахованного
    private void mapLaChangePersData(ChangeApplicationType change, Map<String, Object> reason) throws Exception {
        changePersData(change, reason, RolesType.LIFE_ASSURED);
    }

    //Изменение персональных данных участников договора//Изменение перс.данных выгодоприобретателя
    private void mapBChangePersData(ChangeApplicationType change, Map<String, Object> reason) throws Exception {
        changePersData(change, reason, RolesType.BEN);
    }

    private void changePersData(ChangeApplicationType change, Map<String, Object> reason, RolesType role) throws Exception {
        ThirdPartyListType tplt = new ThirdPartyListType();
        ThirdParty tp = new ThirdParty();
        tp.setFirstName(getStringParam(reason.get("name")));
        tp.setPatronymic(getStringParam(reason.get("middleName")));
        tp.setBirthDate(getFormattedDate(getDateParam(reason.get("birthDate"))));
        tp.setRole(role);
        tp.setThirdPartyId(getLongParam(reason.get("thirdPartyId")));
        List<ThirdParty> tpList = tplt.getThirdParty();
        tpList.add(tp);
        change.setThirdPartys(tplt);
    }

    //изменения фамилии
    //Изменение персональных данных участников договора//Изменение фамилии выгодоприобретателя
    private void mapBChangeSurname(ChangeApplicationType change, Map<String, Object> reason) {
        changeSurname(change, reason, RolesType.BEN);
    }

    //Изменение персональных данных участников договора//Изменение фамилии страхователя
    private void mapHChangeSurname(ChangeApplicationType change, Map<String, Object> reason) {
        changeSurname(change, reason, RolesType.HOLDER);
    }

    //Изменение персональных данных участников договора//Изменение фамилии застрахованного
    private void mapLaChangeSurname(ChangeApplicationType change, Map<String, Object> reason) {
        changeSurname(change, reason, RolesType.LIFE_ASSURED);
    }

    private void changeSurname(ChangeApplicationType change, Map<String, Object> reason, RolesType role) {
        ThirdPartyListType tplt = new ThirdPartyListType();
        ThirdParty tp = new ThirdParty();
        tp.setLastName(getStringParam(reason.get("surName")));
        tp.setRole(role);
        tp.setThirdPartyId(getLongParam(reason.get("thirdPartyId")));
        List<ThirdParty> tpList = tplt.getThirdParty();
        tpList.add(tp);
        change.setThirdPartys(tplt);
    }

    //изменения адресов
    //Изменение персональных данных участников договора//Изменение адреса страхователя
    private void mapHChangeAddress(ChangeApplicationType change, Map<String, Object> reason) {
        changeAddress(change, reason, RolesType.HOLDER);
    }

    //Изменение персональных данных участников договора//Изменение адреса выгодоприобретателя
    private void mapBChangeAddress(ChangeApplicationType change, Map<String, Object> reason) {
        changeAddress(change, reason, RolesType.BEN);
    }

    //Изменение персональных данных участников договора//Изменение адреса застрахованного
    private void mapLaChangeAddress(ChangeApplicationType change, Map<String, Object> reason) {
        changeAddress(change, reason, RolesType.LIFE_ASSURED);
    }

    private void changeAddress(ChangeApplicationType change, Map<String, Object> reason, RolesType role) {
        ThirdPartyListType tplt = new ThirdPartyListType();
        ThirdParty tp = new ThirdParty();
        Map<String, Object> addressMap = (Map<String, Object>) reason.get("addressId_EN");
        ListAddressType listAddressType = new ListAddressType();
        List<Address> addresses = listAddressType.getAddress();
        addresses.add(getAddressByMap(addressMap));
        tp.setListAddress(listAddressType);
        tp.setRole(role);
        tp.setThirdPartyId(getLongParam(reason.get("thirdPartyId")));
        List<ThirdParty> tpList = tplt.getThirdParty();
        tpList.add(tp);
        change.setThirdPartys(tplt);
    }

    private Address getAddressByPartMap(Map<String, Object> addressMap) {
        Address address = new Address();
        if (addressMap != null) {
            address.setAddressType(ADDRESSTYPEMAP.get(getStringParam(addressMap.get("ADDRESSTYPESYSNAME"))));
            address.setDistrict(getStringParam(addressMap.get("REGION")));
            address.setTown(getStringParam(addressMap.get("CITY")));
            address.setStreet(getStringParam(addressMap.get("STREET")));
            address.setStreetNr(getStringParam(addressMap.get("HOUSE")));
            address.setStreetFlat(getStringParam(addressMap.get("FLAT")));
            address.setAddressFull(getStringParam(addressMap.get("ADDRESSTEXT2")));
            address.setPostCode(getStringParam(addressMap.get("POSTALCODE")));
        }
        return address;
    }

    private Address getAddressByMap(Map<String, Object> addressMap) {
        Address address = new Address();
        if (addressMap != null) {
            Map<String, Object> type = (Map<String, Object>) addressMap.get("typeId_EN");
            address.setAddressType(ADDRESSTYPEMAP.get(getStringParam(type.get("sysname"))));
            address.setDistrict(getStringParam(addressMap.get("region")));
            address.setTown(getStringParam(addressMap.get("city")));
            address.setStreet(getStringParam(addressMap.get("street")));
            address.setStreetNr(getStringParam(addressMap.get("house")));
            address.setStreetFlat(getStringParam(addressMap.get("flat")));
            address.setAddressFull(getStringParam(addressMap.get("address")));
            address.setPostCode(getStringParam(addressMap.get("postcode")));
        }
        return address;
    }

    // опции

    private void mapExtraRoi(ChangeApplicationType change, Map<String, Object> reason, String login, String password) throws Exception {
        InvestCoverageType investCoverage = new InvestCoverageType();
        //на первом этапе не переносится в ОИС, просмотр только через ПФ
        Map<String, Object> bankDetail = (Map<String, Object>) reason.get("bankDetailsId_EN");
        if (bankDetail != null) {
            BankAccount ba = new BankAccount();
            ba.setBIK(getStringParam(bankDetail.get("bankBIK")));
            ba.setAccountCorresp(getStringParam(bankDetail.get("bankAccount")));
            ba.setAccountNumber(getStringParam(bankDetail.get("account")));
            change.setBankAccount(ba);

        }
        // даже если есть несколько изменений одного типа
        investCoverage.setInvestDate(getFormattedDate(getDateParam(reason.get("changeDate"))));
        //if ((investCoverage.getInvestDate() == null) || (investCoverage.getInvestDate().isEmpty())) {
        if (investCoverage.getInvestDate() == null) {
            investCoverage.setInvestDate(getFormattedDate(getCloserTrancheStartDate(calcTrahcheStartDate(), login, password)));
        }
        change.setInvestCoverage(investCoverage);
    }

    private void mapExtraPremium(ChangeApplicationType change, Map<String, Object> reason, String login, String password) throws Exception {
        InvestCoverageType investCoverage = new InvestCoverageType();
        investCoverage.setAmountCur1(getBigDecimalParam(reason.get("dopPremVal")));
        //todo: для мультивалютных договоров, выяснить как вычислять.
        //investCoverage.setAmountCur2();
        // даже если есть несколько изменений одного типа
        investCoverage.setInvestDate(getFormattedDate(getDateParam(reason.get("changeDate"))));
        //if ((investCoverage.getInvestDate() == null) || (investCoverage.getInvestDate().isEmpty())) {
        if (investCoverage.getInvestDate() == null) {
            investCoverage.setInvestDate(getFormattedDate(getCloserTrancheStartDate(calcTrahcheStartDate(), login, password)));
        }
        change.setInvestCoverage(investCoverage);
    }

    private void mapFixationRoi(ChangeApplicationType change, Map<String, Object> reason, String login, String password) throws Exception {
        InvestCoverageType investCoverage = new InvestCoverageType();
        // тип фиксации
        investCoverage.setFix(FIXTYPEMAP.get(getStringParam(reason.get("fixTypeId"))));
        // верхний лимит
        // Анна Михалева 02.10.2017 15:11 Skype:
        //Коллеги BIV, при получении опций от вас в параметрах лимитов мы ждем от вас значения 30 и 15, а не 0,3 и 0,15
        try {
            // процент вида 12,34% в БД хранится как 0.1234, поэтому обычный getBigDecimalParam использовать нельзя
            // (установит точность по умолчанию - два знака после запятой)
            investCoverage.setTakeProfit(getBigDecimalParam(reason.get("fixMaxLimit"), 4).multiply(BigDecimal.valueOf(100L)));
        } catch (Exception ex) {
            logger.info("fixMaxLimit is empty", ex);
        }
        try {
            // процент вида 12,34% в БД хранится как 0.1234, поэтому обычный getBigDecimalParam использовать нельзя
            // (установит точность по умолчанию - два знака после запятой)
            investCoverage.setStopLoss(getBigDecimalParam(reason.get("fixMinLimit"), 4).multiply(BigDecimal.valueOf(100L)));
        } catch (Exception ex) {
            logger.info("fixMinLimit is empty", ex);
        }
        // нижний лимит (на интерфейсе допса отсутствует)
        //investCoverage.setStopLoss();
        // даже если есть несколько изменений одного типа
        investCoverage.setInvestDate(getFormattedDate(getDateParam(reason.get("changeDate"))));
        //if ((investCoverage.getInvestDate() == null) || (investCoverage.getInvestDate().isEmpty())) {
        if (investCoverage.getInvestDate() == null) {
            investCoverage.setInvestDate(getFormattedDate(getCloserTrancheStartDate(calcTrahcheStartDate(), login, password)));
        }
        change.setInvestCoverage(investCoverage);
    }

    private void mapChangeAsset(ChangeApplicationType change, Map<String, Object> reason, String login, String password) throws Exception {
        InvestCoverageType investCoverage = new InvestCoverageType();
        Map<String, Object> fundMap = (Map<String, Object>) reason.get("fundId_EN");
        if ((fundMap != null) && (fundMap.get("sysName") != null)) {
            // уточнить соласно фт - надо в baseActive писать новый фонд. в код ничего писать не нужно.
            investCoverage.setBaseActive(getStringParam(fundMap.get("code")));
            investCoverage.setBaseActiveCode(getStringParam(fundMap.get("code")));
        }
        // даже если есть несколько изменений одного типа
        investCoverage.setInvestDate(getFormattedDate(getDateParam(reason.get("changeDate"))));
        //if ((investCoverage.getInvestDate() == null) || (investCoverage.getInvestDate().isEmpty())) {
        if (investCoverage.getInvestDate() == null) {
            investCoverage.setInvestDate(getFormattedDate(getCloserTrancheStartDate(calcTrahcheStartDate(), login, password)));
        }
        change.setInvestCoverage(investCoverage);

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

    // Расторжение/Аннулирование - Расторжение (CANCELLATION)
    private void mapCancellationChange(ChangeApplicationType change, List<Map<String, Object>> reasonList, Map<String, Object> changeMap) {
        mapCancellationAnnulmentChange(change, reasonList, changeMap);
    }

    // Расторжение/Аннулирование - Аннулирование (ANNULMENT)
    private void mapAnnulmentChange(ChangeApplicationType change, List<Map<String, Object>> reasonList, Map<String, Object> changeMap) {
        mapCancellationAnnulmentChange(change, reasonList, changeMap);
    }

    // Расторжение/Аннулирование - Расторжение (CANCELLATION) | Аннулирование (ANNULMENT)
    private void mapCancellationAnnulmentChange(ChangeApplicationType change, List<Map<String, Object>> reasonList, Map<String, Object> changeMap) {
        if (reasonList != null) {
            for (Map<String, Object> reason : reasonList) {
                // внешнее сис. наименование причины изменения
                String kindChangeReasonExternalId = getKindChangeReasonExternalId(reason);
                // данные страхователя, указанные в интерфейсе при вводе допса
                Map<String, Object> insurant = getMapParam(reason, "insurantId_EN");
                // мапа заявителя, они же данные из профиля клиента на момент формирования заявления на допс
                Map<String, Object> applicant = getMapParam(changeMap, "applicantId_EN");
                // банковские реквизиты, указанные в интерфейсе при вводе допса
                Map<String, Object> bankDetails = getMapParam(reason, "bankDetailsId_EN");
                if ((reason == null) || (kindChangeReasonExternalId.isEmpty()) || (insurant == null) || (applicant == null) || (bankDetails == null)) {
                    // недостаточно обязательных параметров - следует перейти к следующему элементу списка
                    logger.warn(String.format(
                            "[mapCancellationAnnulmentChange] Not all required parameters are supplied - reason processing will be skipped. Details (reason map): %s.",
                            reason
                    ));
                    continue;
                }
                // получение и/или создание записи thirdParty в допсе для установки данных лица
                ThirdParty changeThirdParty = getOrCreateChangeThirdPartyRecord(change);
                // создание списка из одного документа у thirdParty в допсе для установки данных документа лица
                DocumentsListType documentsListType = new DocumentsListType();
                changeThirdParty.setDocumentsList(documentsListType);
                List<DocumentsType> documentList = documentsListType.getDocument();
                DocumentsType document = new DocumentsType();
                documentList.add(document);
                // банк. реквизиты
                BankAccount bankAccount = new BankAccount();
                change.setBankAccount(bankAccount);
                // список контактов страхователя, указанные в интерфейсе при вводе допса
                List<Map<String, Object>> contactList = getListParam(insurant, "contacts");
                // мапа контактов страхователя, указанные в интерфейсе при вводе допса
                Map<String, Map<String, Object>> contactMap = getMapByFieldStringValues(contactList, "typeId_EN", "sysname");

                // Тип изменения
                // ttns:ChangeCutList/tns:Change/tns:ChangeType = "..."
                // Расторжение: CANCELLATION; Аннулирование: ANNULMENT
                change.setChangeType(kindChangeReasonExternalId);

                // Место рождения
                // tns:ChangeImport/tns:ApplicationChange/tns:ThirdPartys/tns:ThirdParty/tns:BirthPlace
                String birthPlace = getStringParam(insurant, "placeOfBirth");
                changeThirdParty.setBirthPlace(birthPlace);
                // Мобильный телефон
                // tns:ChangeImport/tns:ApplicationChange/tns:ThirdPartys/tns:ThirdParty/tns:PhoneMobile
                Map<String, Object> mobilePhoneContact = contactMap.get("MobilePhone");
                String mobilePhone = getStringParam(mobilePhoneContact, "value");
                changeThirdParty.setPhoneMobile(mobilePhone);
                // Дополнительный телефон
                // tns:ChangeImport/tns:ApplicationChange/tns:ThirdPartys/tns:ThirdParty/tns:PhoneHome
                Map<String, Object> homePhoneContact = contactMap.get("FactAddressPhone");
                String homePhone = getStringParam(homePhoneContact, "value");
                changeThirdParty.setPhoneHome(homePhone);

                if (applicant != null) {
                    // список документов заявителя (из профиля клиента на момент формирования заявления на допс)
                    List<Map<String, Object>> applicantDocumentList = getListParam(applicant, "documents");
                    if (applicantDocumentList != null && !applicantDocumentList.isEmpty()) {
                        // мапа документов заявителя (из профиля клиента на момент формирования заявления на допс)
                        Map<String, Map<String, Object>> applicantDocumentMap = getMapByFieldStringValues(applicantDocumentList, "typeId_EN", "sysname");
                        if (applicantDocumentMap != null) {
                            // гражданство заявителя (нужно для определения типа документа)
                            Map<String, Object> country = getMapParam(applicant, "countryId_EN");
                            String countryCode = getStringParam(country, "alphaCode3");
                            // сис. наименование главного документа
                            String mainDocSysName = ("RUS".equals(countryCode) || (countryCode.isEmpty())) ? "PassportRF" : "ForeignPassport";
                            Map<String, Object> applicantMainDocument = applicantDocumentMap.get(mainDocSysName);

                            if (applicantMainDocument != null) {
                                // Тип документа
                                // tns:ChangeImport/tns:ApplicationChange/tns:ThirdPartys/tns:ThirdParty/tns:DocumentsList/tns:Document/tns:DocumentType = "PASSPORT"
                                document.setDocumentType(DocumentType.PASSPORT);
                                // Дата выдачи
                                // tns:ChangeImport/tns:ApplicationChange/tns:ThirdPartys/tns:ThirdParty/tns:DocumentsList/tns:Document/tns:DocumentDate
                                XMLGregorianCalendar applicantMainDocumentDateOfIssueGC = null;
                                Object applicantMainDocumentDateOfIssueObj = null;
                                try {
                                    // todo: получение XMLGregorianCalendar вынести в отдельный метод getXMLGCParam или т.п.
                                    applicantMainDocumentDateOfIssueObj = applicantMainDocument.get("dateOfIssue");
                                    applicantMainDocumentDateOfIssueGC = dateToXMLGC(getDateParam(applicantMainDocumentDateOfIssueObj));
                                } catch (Exception ex) {
                                    logger.error(String.format(
                                            "[mapCancellationAnnulmentChange] Parsing date '%s' from key '%s' caused exception:",
                                            applicantMainDocumentDateOfIssueObj, "dateOfIssue"
                                    ), ex);
                                }
                                document.setDocumentDate(applicantMainDocumentDateOfIssueGC);
                                // Серия
                                // tns:ChangeImport/tns:ApplicationChange/tns:ThirdPartys/tns:ThirdParty/tns:DocumentsList/tns:Document/tns:DocumentSeries
                                String applicantMainDocumentSeries = getStringParam(applicantMainDocument, "series");
                                document.setDocumentSeries(applicantMainDocumentSeries);
                                // Номер
                                // tns:ChangeImport/tns:ApplicationChange/tns:ThirdPartys/tns:ThirdParty/tns:DocumentsList/tns:Document/tns:DocumentNumber
                                String applicantMainDocumentNo = getStringParam(applicantMainDocument, "no");
                                document.setDocumentNumber(applicantMainDocumentNo);
                                // Кем выдан
                                // tns:ChangeImport/tns:ApplicationChange/tns:ThirdPartys/tns:ThirdParty/tns:DocumentsList/tns:Document/tns:DocumentInstitution
                                String applicantMainDocumentAuthority = getStringParam(applicantMainDocument, "authority");
                                document.setDocumentInstitution(applicantMainDocumentAuthority);
                                // Код подразделения
                                // tns:ChangeImport/tns:ApplicationChange/tns:ThirdPartys/tns:ThirdParty/tns:DocumentsList/tns:Document/tns:DocumentCodeIns
                                String applicantMainDocumentIssuerCode = getStringParam(applicantMainDocument, "issuerCode");
                                document.setDocumentCodeIns(applicantMainDocumentIssuerCode);
                                // Данные паспорта одной строкой
                                // tns:ChangeImport/tns:ApplicationChange/tns:ThirdPartys/tns:ThirdParty/tns:DocumentsList/tns:Document/tns:DocumentFull
                                Map<String, Object> applicantMainDocumentType = getMapParam(applicantMainDocument, "typeId_EN");
                                String applicantMainDocumentDateOfIssueStr = "";
                                try {
                                    applicantMainDocumentDateOfIssueStr = getFormattedDateStr(getDateParam(applicantMainDocument.get("dateOfIssue")));
                                } catch (Exception ex) {
                                    logger.error(String.format(
                                            "[mapCancellationAnnulmentChange] Parsing date '%s' from key '%s' caused exception:",
                                            applicantMainDocument.get("dateOfIssue"), "dateOfIssue"
                                    ), ex);
                                }
                                String applicantMainDocumentFullStr = String.format(
                                        "%s %s %s от %s выдан %s",
                                        getStringParam(applicantMainDocumentType, "name"),
                                        applicantMainDocumentSeries,
                                        applicantMainDocumentNo,
                                        applicantMainDocumentDateOfIssueStr,
                                        applicantMainDocumentAuthority
                                );
                                document.setDocumentFull(applicantMainDocumentFullStr);

                            }
                        }
                    }
                }
                // банк. реквизиты
                // Лицевой счет получателя
                // tns:ChangeImport/tns:ApplicationChange/tns:BankAccount/tns:AccountNumber
                String bankDetailsAccount = getStringParam(bankDetails, "account");
                bankAccount.setAccountNumber(bankDetailsAccount);
                // БИК
                // tns:ChangeImport/tns:ApplicationChange/tns:BankAccount/tns:BIK
                String bankDetailsBankBIK = getStringParam(bankDetails, "bankBIK");
                bankAccount.setBIK(bankDetailsBankBIK);
                // Корреспонденский счет банка
                // tns:ChangeImport/tns:ApplicationChange/tns:BankAccount/tns:AccountCorresp
                String bankDetailsBankAccount = getStringParam(bankDetails, "bankAccount");
                bankAccount.setBIK(bankDetailsBankAccount);

            }
        }
    }

    private void mapPPOOnlineChange(ChangeApplicationType change, List<Map<String, Object>> reasonList, Map<String, Object> changeMap) {

        if (reasonList != null) {
            for (Map<String, Object> reason : reasonList) {
                String kindChangeReasonExternalId = getKindChangeReasonExternalId(reason);
                String ppoOnlineStatus = (String) reason.get("ppoStatus");
                Long applicandId = (Long) reason.get("thirdPartyId");
                if (ppoOnlineStatus == null || kindChangeReasonExternalId == null) {
                    // недостаточно обязательных параметров - следует перейти к следующему элементу списка
                    logger.warn(String.format(
                            "[mapPPOOnlinetChange] Not all required parameters are supplied - reason processing will be skipped. Details (reason map): %s.",
                            reason
                    ));
                    continue;
                }
                // получение и/или создание записи thirdParty в допсе
                ThirdParty changeThirdParty = getOrCreateChangeThirdPartyRecord(change);
                changeThirdParty.setThirdPartyId(applicandId);
                // задаем флаг операции
                ASalesServOnlineType salesType;
                try {
                    salesType = ASalesServOnlineType.fromValue(ppoOnlineStatus.trim().toUpperCase());
                } catch (IllegalArgumentException ex) {
                    continue;
                }

                changeThirdParty.setASalesServOnline(salesType);
            }
        }
    }

    @WsMethod
    public Map dsB2BIntegrationContractBrowseListByParam(Map params) throws Exception {
        return this.selectQuery("dsB2BIntegrationContractBrowseListByParam", "", params);
    }

    @WsMethod
    public Map dsB2BFastRequestQueueBrowseListByParam(Map params) throws Exception {
        return this.selectQuery("dsB2BFastRequestQueueBrowseListByParam", "", params);
    }

}
