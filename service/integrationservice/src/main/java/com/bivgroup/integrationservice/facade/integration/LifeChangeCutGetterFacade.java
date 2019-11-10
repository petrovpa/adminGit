package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;
import com.bivgroup.integrationservice.system.IntegrationException;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.sberinsur.esb.partner.shema.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BOName("LifeChangeCutGetter")
public class LifeChangeCutGetterFacade extends IntegrationBaseFacade {


    @WsMethod()
    public Map<String, Object> dsLifeIntegrationGetCutChange(Map<String, Object> params) throws Exception {
        IS_CALLS_TIME_LOGGED = true;
        logger.debug("dsLifeIntegrationGetCutChange started with params: " + params);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        GetChangeList.ChangeList changeList = new GetChangeList.ChangeList();

        List<Map<String, Object>> kindChangeReasonList = getKindChangeReasonList(login, password);
        if (kindChangeReasonList != null) {

            GetChangeList gcl = new GetChangeList();
            int packSize = 1;
            if (params.get("PACKSIZE") != null) {
                packSize = Integer.valueOf(params.get("PACKSIZE").toString()).intValue();
            }
            gcl.setRowCount(packSize);
            List<String> changes = changeList.getChange();
            changes.clear();
            for (Map<String, Object> kindChangeReasonMap : kindChangeReasonList) {
                if (kindChangeReasonMap.get("externalId") != null) {
                    changes.add(kindChangeReasonMap.get("externalId").toString());
                }
            }
            gcl.setChangeList(changeList);
            try {
                long callTimerMs = System.currentTimeMillis();
                // вызов действительного метода

                ChangeCutListType resChangeList = callLifePartnerGetChangesCut(gcl);
                // протоколирование времени вызова
                callTimerMs = System.currentTimeMillis() - callTimerMs;
                logger.info(String.format("Method callLifePartnerGetChangesCut executed in %d milliseconds (approximately %.5f seconds).", callTimerMs, ((double) callTimerMs) / 1000.0));

                List<ChangeCutType> changeCutList = resChangeList.getChange();
                AnswerImportListType ailt = new AnswerImportListType();
                List<AnswerImportType> aitList = ailt.getAnswerImport();

                for (ChangeCutType changeCut : changeCutList) {
                    AnswerImportType ait = new AnswerImportType();
                    ait.setSignDeleted(BigInteger.ZERO);

                    ait.setChangeId(String.valueOf(changeCut.getChangeId()));
                    ait.setChangeType(changeCut.getChangeType());
                    ait.setChangeTypeId(String.valueOf(changeCut.getChangeTypeId()));
                    try {
                        //1. ищем договор по externalid,
                        Map<String, Object> contrMap = loadContrById(changeCut, login, password);
                        if (contrMap == null) {
                            // договор для данного допса отсутствует в базе.
                            // сохраняем ошибку, оставляем договор в очереди
                            logger.error("Error save declaration: contract not found " + changeCut.getPolicyNumber() + " externalid: " + changeCut.getPolicyId());
                            throw new IntegrationException("Ошибка сохранения допса, Отсутствует договор страхования: contract not found " + changeCut.getPolicyNumber() + " externalid: " + changeCut.getPolicyId(), "Change save Error, Contract unexist");

                        } else {
                            callTimerMs = System.currentTimeMillis();
                            // вызов действительного метода

                            Map<String, Object> changeMap = mapChange(changeCut, kindChangeReasonList, login, password);
                            // протоколирование времени вызова
                            callTimerMs = System.currentTimeMillis() - callTimerMs;
                            logger.info(String.format("Method mapChange executed in %d milliseconds (approximately %.5f seconds).", callTimerMs, ((double) callTimerMs) / 1000.0));
                            changeMap.put("contractId", contrMap.get("CONTRID"));
                            if (contrMap.get("DOCUMENTDATE") != null) {
                                changeMap.put("contractDate", contrMap.get("DOCUMENTDATE"));
                            } else {
                                GetObjType got = new GetObjType();
                                got.setPolicyId(Long.valueOf(changeCut.getPolicyId()));
                                ListContractType lct = callLifePartnerGetContracts(got);
                                changeMap.put("contractDate", processDate(lct.getContract().get(0).getPolicyDocDate()));
                            }

                            Map<String, Object> saveRes = saveChange(changeMap, params, login, password);
                            if (isCallResultOKAndContains(saveRes, CHANGE_ID_PARAMNAME)) {
                                ait.setStatus(StatusIntType.SUCCESS);
                            } else {
                                // не удалось сохранить
                                logger.error("Error save declaration: " + getStringParam(saveRes.get("Error")) + " changeId(thirdPartyPolicyId): " + changeCut.getChangeId());
                                throw new IntegrationException("Error save declaration: " + getStringParam(saveRes.get("Error")) + " changeId(thirdPartyPolicyId): " + changeCut.getChangeId(), "Error save declaration");
                            }
                        }
                    } catch (IntegrationException ex) {
                        logger.error("Ошибка сохранения заявки: " + ex.getRussianMessage(), ex);
                        ait.setErr("Ошибка сохранения заявки: " + ex.getRussianMessage());
                    } catch (Exception ex) {
                        logger.error("Ошибка сохранения заявки: " + ex.getMessage(), ex);
                        ait.setErr("Ошибка сохранения заявки: " + ex.getMessage());
                    }
                    aitList.add(ait);

                }

                callLifeProcessResponseCut(ailt);

//                String goltXML = this.marshall(gcl, GetChangeList.class);
                String goltXML = this.marshall(ailt, AnswerImportListType.class);
                String contractListRespXML = this.marshall(resChangeList, ChangeCutListType.class);

                Map<String, Object> requestMap = new HashMap<String, Object>();
                b2bRequestQueueCreate(goltXML, contractListRespXML, GETCUTCHANGEINFO, 1000, login, password);
                result.put("requestStr", goltXML);
                result.put("responseStr", contractListRespXML);
                result.put("STATUS", "DONE");
            } catch (Exception e) {
                logger.error("Partner service dsLifeIntegrationGetCutChange call error", e);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                sw.toString(); // stack trace as a string
                result.put("responseStr", sw.toString());
                Map<String, Object> requestMap = new HashMap<String, Object>();
                String goltXML = this.marshall(gcl, GetChangeList.class);
                b2bRequestQueueCreate(goltXML, sw.toString(), GETCUTCHANGEINFO, 404, login, password);

                result.put("STATUS", "outERROR");

            }
        }
        logger.debug("dsLifeIntegrationGetCutChange finished.");
        return result;
    }

    /**
     * загрузка договора по EXTERNALID договора (policyid)
     *
     * @return
     * @throws Exception
     */
    private Map<String, Object> loadContrById(ChangeCutType changeCut, String login, String password) throws Exception {
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("EXTERNALID", changeCut.getPolicyId());
        searchParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callServiceTimeLogged(B2BPOSWS, "dsB2BContractBrowseListByParam", searchParams, login, password);
        if (res != null) {
            if (res.get("CONTRID") != null) {
                return res;
            }
        }
        return null;
    }

    private Map<String, Object> loadChangeById(ChangeCutType changeCut, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("externalId", changeCut.getChangeId());
        Map<String, Object> res = this.callServiceTimeLogged(B2BPOSWS, "dsB2BDeclarationOfChangeLoadByParams", params, login, password);
        if (res != null) {
            if (res.get(RESULT) != null) {
                List<Map<String, Object>> changeList = (List<Map<String, Object>>) res.get(RESULT);
                if (!changeList.isEmpty()) {
                    return changeList.get(0);
                }
            }
        }
        return null;
    }

    private List<Map<String, Object>> getKindChangeReasonList(String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("CLASSIFIERNAME", KIND_CHANGE_REASON_SYSNAME);
        Map<String, Object> res = this.callServiceTimeLogged(B2BPOSWS, "dsB2BDictionaryClassifierDataLoadByName", params, login, password);
        if (res != null) {
            if (res.get(RESULT) != null) {
                Map<String, Object> resMap = (Map<String, Object>) res.get(RESULT);
                if (resMap.get(KIND_CHANGE_REASON_SYSNAME) != null) {
                    return (List<Map<String, Object>>) resMap.get(KIND_CHANGE_REASON_SYSNAME);
                }
            }
        }
        return null;
    }

    private Map<String, Object> saveChange(Map<String, Object> changeMap, Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> lossNoticeSaveParams = new HashMap<String, Object>();
        changeMap.remove("stateId_EN");
        lossNoticeSaveParams.put(CHANGE_MAP_PARAMNAME, changeMap);
        lossNoticeSaveParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> lossNoticeSaved = this.callServiceTimeLogged(B2BPOSWS, "dsB2BDeclarationOfChangeSaveWithoutChecks", lossNoticeSaveParams, login, password);
        return lossNoticeSaved;
    }

    private Map<String, Object> mapChange(ChangeCutType changeCut, List<Map<String, Object>> kindChangeReasonList, String login, String password) throws Exception {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> changeMap = loadChangeById(changeCut, login, password);
        boolean isUpdate = false;
        if (changeMap != null) {
            Object declarationId = changeMap.get(CHANGE_ID_PARAMNAME);
            if (declarationId != null) {
                logger.debug("Existing declaration id = " + declarationId);
                result.putAll(changeMap);
                result.put(CHANGE_ID_PARAMNAME, declarationId);
                result.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);

                if (changeMap.get("reasons") != null) {
                    List<Map<String, Object>> reasonList = (List<Map<String, Object>>) changeMap.get("reasons");
                    if (reasonList != null && reasonList.size() > 0) {
                        for (Map<String, Object> reason : reasonList) {
                            Map<String, Object> kindChangeReason = (Map<String, Object>) reason.get("kindChangeReasonId_EN");
                            if (kindChangeReason != null) {
                                if (kindChangeReason.get("externalId") != null) {
                                    String changeReasonSysName = getStringParam(kindChangeReason.get("externalId"));
                                    if (changeCut.getChangeType().equalsIgnoreCase(changeReasonSysName)) {
                                        isUpdate = true;
                                        reason.put("reasonComment", changeCut.getComment());
                                        reason.put("changeDate", processDate(changeCut.getEndorsementDate()));
                                        result.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                                    }
                                }
                            }
                        }
                    }

                    result.put("reasons", reasonList);
                }
            } else {
                logger.debug("Existing declaration was not found");
            }
        }
        result.put(CHANGE_EXTERNAL_ID_PARAMNAME, changeCut.getChangeId());
        try {
            result.put(CHANGE_STATEID_PARAMNAME, getStateIdByName(changeCut.getStatus().getStatus(), "PD_DECLARATION", PD_DECLARATION_INWORK));
        } catch (Exception ex) {
            logger.error("changeCutGetter input status problem in " + changeCut.getChangeId(), ex);
            result.put(CHANGE_STATEID_PARAMNAME, PD_DECLARATION_INWORK);
        }
        result.put(CHANGE_DOCFOLDER1C_PARAMNAME, changeCut.getDocFolder1C());
        result.put("note", changeCut.getComment());
        if (!isUpdate) {
            List<Map<String, Object>> reasonList = new ArrayList<>();
            Map<String, Object> reason = new HashMap<>();
            reason.put("changeDate", processDate(changeCut.getEndorsementDate()));
            String kindChangeReasonSysName = changeCut.getChangeType();
            if ("H_CHANGE_PASSPORT".equalsIgnoreCase(kindChangeReasonSysName)) {
                // костыль, из за дубления типа изменения в разных типах заявлений.
                // по факту такой тип изменения всегда должен быть в заявлении "изменении персональных данных"
                // так что безусловно выдаем его.
                for (Map<String, Object> kindChangeReasonMap : kindChangeReasonList) {
                    if (kindChangeReasonMap.get("kindDeclarationId_EN") != null) {
                        Map<String, Object> kindDeclMap = (Map<String, Object>) kindChangeReasonMap.get("kindDeclarationId_EN");
                        if (kindDeclMap.get("sysname") != null) {
                            if ("changePersonalData".equalsIgnoreCase(getStringParam(kindDeclMap.get("sysname")))) {
                                if (kindChangeReasonSysName.equalsIgnoreCase(getStringParam(kindChangeReasonMap.get("externalId")))) {
                                    reason.put("kindChangeReasonId", kindChangeReasonMap.get("id"));
                                    reason.put("kindDeclarationId", kindDeclMap.get("id"));
                                    break;
                                }

                            }
                        }

                    }
                }
            } else {
                for (Map<String, Object> kindChangeReasonMap : kindChangeReasonList) {
                    if (kindChangeReasonSysName.equalsIgnoreCase(getStringParam(kindChangeReasonMap.get("externalId")))) {
                        reason.put("kindChangeReasonId", kindChangeReasonMap.get("id"));
                        if (kindChangeReasonMap.get("kindDeclarationId_EN") != null) {
                            Map<String, Object> kindDeclMap = (Map<String, Object>) kindChangeReasonMap.get("kindDeclarationId_EN");
                            reason.put("kindDeclarationId", kindDeclMap.get("id"));
                        }
                        break;
                    }
                }
            }
            reasonList.add(reason);
            result.put("reasons", reasonList);
        }

        return result;
    }

}
