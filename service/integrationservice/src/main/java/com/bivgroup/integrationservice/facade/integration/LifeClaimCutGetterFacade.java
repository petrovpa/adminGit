package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;
import com.bivgroup.integrationservice.system.Constants;

import java.io.PrintWriter;
import java.io.StringWriter;

import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.sberinsur.esb.partner.shema.*;

import java.math.BigInteger;
import java.util.*;
import javax.xml.bind.JAXBElement;

@BOName("LifeClaimCutGetter")
public class LifeClaimCutGetterFacade extends IntegrationBaseFacade {

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    // копия из com.bivgroup.services.b2bposws.facade.pos.lossB2BLossNoticeCustomFacade
    // todo: заменить на импорт

    @WsMethod()
    public Map<String, Object> dsLifeIntegrationGetClaimCutList(Map<String, Object> params) throws Exception {
        logger.debug("dsLifeIntegrationGetClaimCutList started with params: " + params);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        GetObjListType golt = new GetObjListType();
        int packSize = 1;
        if (params.get("PACKSIZE") != null) {
            packSize = Integer.valueOf(params.get("PACKSIZE").toString()).intValue();
        }
        
        // для отладки
        if (params.get("FROM") != null && params.get("TO") != null) {
            golt.setFrom(parseDate(getStringParam(params, "FROM")));
            golt.setTo(parseDate(getStringParam(params, "TO")));
        }
        
        golt.setRowCount(packSize);
        try {

            ClaimCutType resClaimList = callLifePartnerGetClaimsCut(golt);
            List<ClaimPolicyCutType> claimCutList = resClaimList.getClaimCut();
            AnswerImportListType ailt = new AnswerImportListType();
            List<AnswerImportType> aitList = ailt.getAnswerImport();

            for (ClaimPolicyCutType claimCut : claimCutList) {
                AnswerImportType ait = new AnswerImportType();
                ait.setSignDeleted(BigInteger.ZERO);

                ait.setClaimId(String.valueOf(claimCut.getClaimID()));
                try {
                    Map<String, Object> lossNotice = mapClaim(claimCut, login, password);
                    Map<String, Object> saveRes = saveLossNotice(lossNotice, params, login, password);
                    if (isCallResultOKAndContains(saveRes, LOSS_NOTICE_ID_PARAMNAME)) {
                        ait.setStatus(StatusIntType.SUCCESS);
                    } else {
                        // не удалось сохранить
                        throw new Exception("Saving new loss notice failed!");
                    }
                } catch (Exception ex) {
                    logger.error("Ошибка сохранения заявки: " + ex.getMessage() + " claimId: " + claimCut.getClaimID(), ex);
                    ait.setErr("Ошибка сохранения заявки: " + ex.getMessage() + " claimId: " + claimCut.getClaimID());
                }
                aitList.add(ait);

            }

            callLifeProcessResponseCut(ailt);

//            String goltXML = this.marshall(golt, GetObjListType.class);
            String goltXML = this.marshall(ailt, AnswerImportListType.class);
            String contractListRespXML = this.marshall(resClaimList, ClaimCutType.class);

            Map<String, Object> requestMap = new HashMap<String, Object>();
            b2bRequestQueueCreate(goltXML, contractListRespXML, GETCUTCLAIMINFO, 1000, login, password);
            result.put("requestStr", goltXML);
            result.put("responseStr", contractListRespXML);
            result.put("STATUS", "DONE");
        } catch (Exception e) {
            logger.error("Partner service GetContractsCut call error", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.toString(); // stack trace as a string
            result.put("responseStr", sw.toString());
            Map<String, Object> requestMap = new HashMap<String, Object>();
            String goltXML = this.marshall(golt, GetObjListType.class);
            b2bRequestQueueCreate(goltXML, sw.toString(), GETCUTCLAIMINFO, 404, login, password);

            result.put("STATUS", "outERROR");
        }
        logger.debug("dsLifeIntegrationGetClaimCutList finished.");
        return result;
    }


    private Long getClassifierRecordLongFieldValueByParams(String clsName, String fieldName, Map<String, Object> clsParams, String login, String password) throws Exception {
        Long hbRecordFieldValue = null;
        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("CLASSIFIERNAME", clsName);
        hbParams.put("CLASSIFIERDATAPARAMS", clsParams);
        hbParams.put(RETURN_LIST_ONLY, true);
        Map<String, Object> hbRecordRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BDictionaryClassifierDataLoadByName", hbParams, login, password);
        List<Map<String, Object>> hbRecordList = WsUtils.getListFromResultMap(hbRecordRes);
        if ((hbRecordList != null) && (hbRecordList.size() == 1)) {
            Map<String, Object> hbRecord = hbRecordList.get(0);
            hbRecordFieldValue = getLongParam(hbRecord, fieldName);
        } else {
            logger.warn(String.format(
                    "Method dsB2BDictionaryClassifierDataLoadByName unable to find record in classifier '%s' by params %s.",
                    clsName, clsParams
            ));
        }
        return hbRecordFieldValue;
    }

    private Long getClassifierRecordLongFieldValueBySysName(String clsName, String recordSysName, String fieldName, String login, String password) throws Exception {
        Map<String, Object> clsParams = new HashMap<String, Object>();
        clsParams.put("sysname", recordSysName);
        Long recordId = getClassifierRecordLongFieldValueByParams(clsName, fieldName, clsParams, login, password);
        return recordId;
    }

    private Long getClassifierRecordIdBySysName(String clsName, String recordSysName, String login, String password) throws Exception {
        Map<String, Object> clsParams = new HashMap<String, Object>();
        clsParams.put("sysname", recordSysName);
        Long recordId = getClassifierRecordLongFieldValueByParams(clsName, "id", clsParams, login, password);
        return recordId;
    }

    private String getJAXBElemetStringValue(JAXBElement element) {
        String stringValue = "";
        if (element != null) {
            Object objectValue = element.getValue();
            if (objectValue != null) {
                stringValue = objectValue.toString();
            }
        }
        return stringValue;
    }

    private Map<String, Object> mapClaim(ClaimPolicyCutType claimPolicyCut, String login, String password) throws Exception {
        logger.debug("mapClaim...");
        Map<String, Object> lossNotice = null;
        if (claimPolicyCut != null) {
            lossNotice = new HashMap<String, Object>();
            // определение наличия уже существующего lossNotice, в дальнейшем - чтоб в дальнейшем выполнялось обновление найденного существующего (вместо создания нового)
            long claimExternalId = claimPolicyCut.getClaimID();
            Map<String, Object> lossNoticeParams = new HashMap<String, Object>();
            lossNoticeParams.put(LOSS_NOTICE_EXTERNAL_ID_PARAMNAME, claimExternalId);
            lossNoticeParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> existedLossNotice = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BLossNoticeLoad", lossNoticeParams, login, password);
            Long lossEventId = null;
            Long damageCatId = null;
            if (isCallResultOK(existedLossNotice)) {
                Long lossNoticeId = getLongParam(existedLossNotice, LOSS_NOTICE_ID_PARAMNAME);
                if (lossNoticeId != null) {
                    // в дальнейшем требуется, чтоб выполнялось обновление найденного существующего (вместо создания нового)
                    logger.debug("Existing loss notice id = " + lossNoticeId);
                    lossNotice.putAll(existedLossNotice);
                    lossNotice.put(LOSS_NOTICE_ID_PARAMNAME, lossNoticeId);
                    lossEventId = getLongParam(existedLossNotice.get("insEventId"));
                    damageCatId = getLongParam(existedLossNotice.get("damageCatId"));
                    //lossNotice.put("damageCatId", existedLossNotice.get("damageCatId"));
                    //lossNotice.put("insEventId", existedLossNotice.get("insEventId"));
                    lossNotice.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                } else {
                    logger.debug("Existing loss notice was not found");
                }
            } else {
                // ошибка - не удалось проверить есть ли уже в БД данное lossNotice
                throw new Exception("Unable to check existing loss notice by external id! ClaimId = " + claimExternalId);
            }
            // спец. ИД
            lossNotice.put("externalId", claimExternalId);
            // #13035 оис в этом поле возвращает thirdparty застрахованного. мы его не используем.
            // а в этом поле у нас связь с профилем человека создавщего заявку.
            //lossNotice.put("thirdPartyId", claimPolicyCut.getThirdPartyId());
            // даты
            if (!lossNotice.containsKey("createDate")) {
                lossNotice.put("createDate", new Date());
            }
            Map<String, Object> accountFindParams = new HashMap<String, Object>();
            accountFindParams.put("LOGIN", login);
            accountFindParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> accountFindResult = this.callService(
                    Constants.ADMINWS, "admAccountFind",
                    accountFindParams, login, password
            );

            lossNotice.put("createUserId", accountFindResult.get("USERACCOUNTID"));
            lossNotice.put("eventDate", processDate(claimPolicyCut.getEventDate()));
            lossNotice.put("regDate", processDate(claimPolicyCut.getRegistryDate()));
            // страхователь - фио, др
            lossNotice.put("insuredName", claimPolicyCut.getLaFirstName());
            lossNotice.put("insuredMiddleName", claimPolicyCut.getLaPatromic());
            lossNotice.put("insuredSurname", claimPolicyCut.getLaLastName());
            lossNotice.put("insuredBirthDate", processDate(claimPolicyCut.getLaBirthDate()));
            // insEventId
            EventCodeType eventCode = claimPolicyCut.getEventCode();
            if (eventCode != null) {
                Long lossEventIdTmp = getClassifierRecordLongFieldValueBySysName(LOSS_EVENT_ENTITY_NAME, eventCode.value(), LOSS_EVENT_ID_FIELDNAME, login, password);
                if (lossEventIdTmp != null) {
                    lossEventId = lossEventIdTmp;
                }
            }
            if (lossEventId == null) {
                throw new Exception("Unable to select event for loss notice! ClaimId = " + claimExternalId);
            }
            lossNotice.put("insEventId", lossEventId);
            // damageCatId
            ReasonType reasonCode = claimPolicyCut.getReasonCode();
            if (reasonCode != null) {
               Long damageCatIdTmp = getClassifierRecordIdBySysName(LOSS_DAMAGE_CATEGORY_ENTITY_NAME, reasonCode.value(), login, password);
                if (damageCatIdTmp != null) {
                    damageCatId = damageCatIdTmp;
                }
            }
            if (damageCatId == null) {
                throw new Exception("Unable to select reason for loss notice! ClaimId = " + claimExternalId);
            }
            lossNotice.put("damageCatId", damageCatId);
            lossNotice.put("docFolder1C", claimPolicyCut.getDocFolder1C());
            if (claimPolicyCut.getStatus() != null) {
                if ("PROGRESS".equalsIgnoreCase(claimPolicyCut.getStatus())) {
                    lossNotice.put(LOSS_NOTICE_STATEID_PARAMNAME, B2B_LOSSNOTICE_INWORK);
                } else {
                    lossNotice.put(LOSS_NOTICE_STATEID_PARAMNAME, B2B_LOSSNOTICE_FINAL);
                }
            } else {
                lossNotice.put(LOSS_NOTICE_STATEID_PARAMNAME, B2B_LOSSNOTICE_INWORK);
            }
        }
        logger.debug("mapClaim finished with result (lossNotice): " + lossNotice);
        return lossNotice;
    }

    private Object getCurrencyIdBySysName(String currency) {
        return CURRENCYMAP.getKey(currency);
    }

    private Object processMember(ContractCut.ThirdPartyList thirdPartyList, String login, String password) {
        List<ContractCut.ThirdPartyList.ThirdParty> tpl = thirdPartyList.getThirdParty();
        List<Map<String, Object>> memberList = new ArrayList<>();
        for (ContractCut.ThirdPartyList.ThirdParty thirdParty : tpl) {
            thirdParty.getThirdPartyId();
            Map<String, Object> memberMap = new HashMap<>();
            memberMap.put("TYPESYSNAME", ROLEMAP.getKey(thirdParty.getRole()));
            memberMap.put("LONGFIELD00", Double.valueOf(thirdParty.getThirdPartyId()).longValue());
            memberList.add(memberMap);
        }
        return memberList;
    }

    private Map<String, Object> saveLossNotice(Map<String, Object> lossNotice, Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> lossNoticeSaved = null;
        if (lossNotice != null) {
            Map<String, Object> lossNoticeSaveParams = new HashMap<String, Object>();
            lossNoticeSaveParams.put(LOSS_NOTICE_MAP_PARAMNAME, lossNotice);
            lossNoticeSaveParams.put(RETURN_AS_HASH_MAP, true);
            lossNoticeSaved = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BLossNoticeSave", lossNoticeSaveParams, login, password);
        }
        return lossNoticeSaved;
    }

    private Map<String, Object> createContractNode(String login, String password) throws Exception {
        logger.debug("createContractNode begin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("RVERSION", 0L);
        params.put("LASTVERNUMBER", 0L);
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BContractNodeCreate", params, login, password);
        if (res.get(RESULT) != null) {
            return (Map<String, Object>) res.get(RESULT);
        }
        logger.debug("createContractNode end");
        return null;
    }

    private Object createContract(Map<String, Object> contrMap, String login, String password) throws Exception {
        logger.debug("createContractNode begin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(contrMap);
        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BContractCreate", params, login, password);
        if (res.get("CONTRID") != null) {
            return res.get("CONTRID");
        }
        logger.debug("createContractNode end");
        return null;
    }

    private Object updateContract(Map<String, Object> contrMap, String login, String password) throws Exception {
        logger.debug("createContractNode begin");
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(contrMap);
        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BContractUpdate", params, login, password);
        if (res.get("CONTRID") != null) {
            return res.get("CONTRID");
        }
        logger.debug("createContractNode end");
        return null;
    }

    private void updateContractNode(Map<String, Object> contrnodeMap, String login, String password) throws Exception {
        this.callService(B2BPOSWS, "dsB2BContractNodeUpdate", contrnodeMap, login, password);
    }

}
