package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;
import com.bivgroup.integrationservice.system.IntegrationException;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.sberinsur.esb.partner.shema.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BOName("LifeDeletedGetter")
public class LifeDeletedGetterFacade extends IntegrationBaseFacade {


    @WsMethod()
    public Map<String, Object> dsLifeIntegrationGetDeleted(Map<String, Object> params) throws Exception {
        IS_CALLS_TIME_LOGGED = true;
        logger.debug("dsLifeIntegrationGetDeleted started with params: " + params);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        GetDeletedObjects gdo = new GetDeletedObjects();
        int packSize = 1;
        if (params.get("PACKSIZE") != null) {
            packSize = Integer.valueOf(params.get("PACKSIZE").toString()).intValue();
        }
        gdo.setRowCount(packSize);
        try {
            long callTimerMs = System.currentTimeMillis();
            // вызов действительного метода

            DeleteObjectListType resDeletedList = callLifePartnerGetDeleted(gdo);
            // протоколирование времени вызова
            callTimerMs = System.currentTimeMillis() - callTimerMs;
            logger.info(String.format("Method callLifePartnerGetChangesCut executed in %d milliseconds (approximately %.5f seconds).", callTimerMs, ((double) callTimerMs) / 1000.0));

            List<DeleteObjectListType.DeletedObject> deletedList = resDeletedList.getDeletedObject();
            AnswerImportListType ailt = new AnswerImportListType();
            List<AnswerImportType> aitList = ailt.getAnswerImport();

            for (DeleteObjectListType.DeletedObject deletedObj : deletedList) {
                AnswerImportType ait = new AnswerImportType();
                ait.setSignDeleted(BigInteger.ONE);
                ait.setStatus(StatusIntType.FAIL);
                try {
                    if (deletedObj.getPolicyId() != null && !deletedObj.getPolicyId().isEmpty()) {
                        //удаление договора
                        ait.setPolicyId(deletedObj.getPolicyId());
                        String contrNumber = moveContractToDeletedState(deletedObj.getPolicyId(), login, password);
                        ait.setStatus(StatusIntType.SUCCESS);
                        ait.setPolicyNumber(contrNumber);

                    } else {
                        if (deletedObj.getClaimId() != null && !deletedObj.getClaimId().isEmpty()) {
                            //удаление заявления на убыток
                            ait.setClaimId(deletedObj.getClaimId());
                            moveClaimToDeletedState(deletedObj.getClaimId(), login, password);
                            ait.setStatus(StatusIntType.SUCCESS);
                        } else {
                            if (deletedObj.getChangeId() != null && !deletedObj.getChangeId().isEmpty()) {
                                //удаление заявления на изменение
                                ait.setChangeId(deletedObj.getChangeId());
                                //ait.setChangeTypeId(getStringParam(deletedObj.getChangeTypeId()));
                                moveChangeToDeletedState(deletedObj.getChangeId(), deletedObj.getChangeTypeId(), login, password);
                                ait.setStatus(StatusIntType.SUCCESS);
                            } else {
                                throw new IntegrationException("Обхъект к удалению пришедший от ОИС не содержит идентификаторов", "deleted object not contain id");
                            }
                        }
                    }
                } catch (IntegrationException ex) {
                    if (deletedObj.getPolicyId() != null && !deletedObj.getPolicyId().isEmpty()) {
                        deletedObj.setPolicyId(deletedObj.getPolicyId() + ": " + ex.getRussianMessage());
                    }
                    if (deletedObj.getClaimId() != null && !deletedObj.getClaimId().isEmpty()) {
                        deletedObj.setClaimId(deletedObj.getClaimId() + ": " + ex.getRussianMessage());
                    }
                    if (deletedObj.getChangeId() != null && !deletedObj.getChangeId().isEmpty()) {
                        deletedObj.setChangeId(deletedObj.getChangeId() + ": " + ex.getRussianMessage());
                    }
                    logger.error("Ошибка сохранения заявки: " + deletedObj.getPolicyId() + ", "
                            + deletedObj.getClaimId() + ", "
                            + deletedObj.getChangeId() + ", "
                            + ex.getRussianMessage(), ex);
                    ait.setErr("Ошибка сохранения заявки: " + ex.getRussianMessage());
                } catch (Exception ex) {
                    if (deletedObj.getPolicyId() != null && !deletedObj.getPolicyId().isEmpty()) {
                        deletedObj.setPolicyId(deletedObj.getPolicyId() + ": " + ex.getMessage());
                    }
                    if (deletedObj.getClaimId() != null && !deletedObj.getClaimId().isEmpty()) {
                        deletedObj.setClaimId(deletedObj.getClaimId() + ": " + ex.getMessage());
                    }
                    if (deletedObj.getChangeId() != null && !deletedObj.getChangeId().isEmpty()) {
                        deletedObj.setChangeId(deletedObj.getChangeId() + ": " + ex.getMessage());
                    }
                    logger.error("Ошибка сохранения заявки: " + deletedObj.getPolicyId() + ", "
                            + deletedObj.getClaimId() + ", "
                            + deletedObj.getChangeId() + ", "
                            + ex.getMessage(), ex);
                    ait.setErr("Ошибка сохранения заявки: " + ex.getMessage());
                }
                aitList.add(ait);

            }

            callLifeProcessResponseCut(ailt);

            String goltXML = this.marshall(ailt, AnswerImportListType.class);
            String contractListRespXML = this.marshall(resDeletedList, DeleteObjectListType.class);

            Map<String, Object> requestMap = new HashMap<String, Object>();
            b2bRequestQueueCreate(goltXML, contractListRespXML, GETDELETEDINFO, 1000, login, password);
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
            String goltXML = this.marshall(gdo, GetDeletedObjects.class);
            b2bRequestQueueCreate(goltXML, sw.toString(), GETDELETEDINFO, 404, login, password);

            result.put("STATUS", "outERROR");

        }
        logger.debug("dsLifeIntegrationGetCutChange finished.");
        return result;
    }

    private void moveChangeToDeletedState(String changeId, Long changeType, String login, String password) throws Exception {
        //получить хибернейт сущность убытка.
        Map<String, Object> changeMap = loadChangeById(changeId, login, password);
        if (changeMap != null) {
            changeMap.put("stateId", PD_DECLARATION_DELETED);
            Map<String, Object> saveRes = saveChange(changeMap, login, password);
            if (saveRes == null) {
                throw new IntegrationException("ChangeMakeTransError changeId: " + changeId, "ChangeMakeTransError changeId: " + changeId);
            }
        }
        //проставить новое состояние.
        //сохранить.
    }

    private void moveClaimToDeletedState(String claimId, String login, String password) throws Exception {
        //получить хибернейт сущность убытка.
        Map<String, Object> changeMap = loadClaimById(claimId, login, password);
        if (changeMap != null) {
            changeMap.put("stateId", B2B_LOSSNOTICE_DELETED);
            Map<String, Object> saveRes = saveLossNotice(changeMap, login, password);
            if (saveRes == null) {
                throw new IntegrationException("ClaimMakeTransError claimId: " + claimId, "ClaimMakeTransError claimId: " + claimId);
            }
        }
        //проставить новое состояние.
        //сохранить.
    }

    private String moveContractToDeletedState(String policyId, String login, String password) throws Exception {
        //по экстернал ид договора получить ид, текущее состояние. номер договора
        Map<String, Object> contrMap = loadContrById(policyId, login, password);
        if (contrMap != null) {
            contractMakeTrans(contrMap, "B2B_CONTRACT_DELETED", login, password);

            return getStringParam(contrMap.get("CONTRNUMBER"));
        } else {
            // throw new IntegrationException("Не найден подлежащий удалению договор с ИД:" + policyId,
            //         "Contract to delete not found. ID: " + policyId);
            //Согласно требованию  Александра Клевцова в случае если не нашли объект к удалению - возвращать в ОИС успех.
            return "";
        }
    }

    private Map<String, Object> loadContrById(String externalId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("EXTERNALID", externalId);
        searchParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callServiceTimeLogged(B2BPOSWS, "dsB2BContractBrowseListByParam", searchParams, login, password);
        if (res != null) {
            if (res.get("CONTRID") != null) {
                return res;
            }
        }
        return null;
    }

    private Map<String, Object> loadChangeById(String changeId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("externalId", changeId);
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

    private Map<String, Object> loadClaimById(String claimId, String login, String password) throws Exception {
        Map<String, Object> lossNoticeParams = new HashMap<String, Object>();
        lossNoticeParams.put(LOSS_NOTICE_EXTERNAL_ID_PARAMNAME, claimId);
        lossNoticeParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> existedLossNotice = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BLossNoticeLoad", lossNoticeParams, login, password);
        if (isCallResultOK(existedLossNotice)) {
            return existedLossNotice;
//            if (existedLossNotice.get(RESULT) != null) {
//                List<Map<String, Object>> changeList = (List<Map<String, Object>>) existedLossNotice.get(RESULT);
//                if (!changeList.isEmpty()) {
//                    return changeList.get(0);
//                }
//            }
        }
        return null;
    }

    private Map<String, Object> saveChange(Map<String, Object> changeMap, String login, String password) throws Exception {
        Map<String, Object> lossNoticeSaveParams = new HashMap<String, Object>();
        changeMap.remove("stateId_EN");
        changeMap.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED.getId());
        lossNoticeSaveParams.put(CHANGE_MAP_PARAMNAME, changeMap);
        lossNoticeSaveParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> lossNoticeSaved = this.callServiceTimeLogged(B2BPOSWS, "dsB2BDeclarationOfChangeSaveWithoutChecks", lossNoticeSaveParams, login, password);
        return lossNoticeSaved;
    }

    private Map<String, Object> saveLossNotice(Map<String, Object> lossNotice, String login, String password) throws Exception {
        Map<String, Object> lossNoticeSaved = null;
        if (lossNotice != null) {
            Map<String, Object> lossNoticeSaveParams = new HashMap<String, Object>();
            lossNotice.remove("stateId_EN");
            lossNotice.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED.getId());
            lossNoticeSaveParams.put(LOSS_NOTICE_MAP_PARAMNAME, lossNotice);
            lossNoticeSaveParams.put(RETURN_AS_HASH_MAP, true);
            lossNoticeSaved = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BLossNoticeSave", lossNoticeSaveParams, login, password);
        }
        return lossNoticeSaved;
    }


}
