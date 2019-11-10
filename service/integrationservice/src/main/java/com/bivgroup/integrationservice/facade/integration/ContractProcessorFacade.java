/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;
import com.bivgroup.integrationservice.system.Constants;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.sberinsur.esb.partner.shema.ListContractType;
import ru.sberinsur.esb.partner.shema.GetObjListType;
import ru.sberinsur.esb.partner.shema.ListContractCutType;

/**
 * // сервисы для вызова из планировщика, с защитой от запуска второй копии.
 *
 * @author sambucus
 */
@BOName("ContractProcessor")
public class ContractProcessorFacade extends IntegrationBaseFacade {

    private static volatile int partnerServiceThreadCount = 0;
    private static volatile int partnerServiceContractCutThreadCount = 0;
    private static volatile int partnerServiceClaimCutThreadCount = 0;
    private static volatile int partnerServiceClaimFileThreadCount = 0;
    private static volatile int partnerServiceClaimFileAssignThreadCount = 0;
    private static volatile int partnerServiceChangeFileThreadCount = 0;
    private static volatile int partnerServiceChangeFileAssignThreadCount = 0;
    private static volatile int partnerServiceClaimPutThreadCount = 0;
    private static volatile int partnerServiceChangePutThreadCount = 0;
    private static volatile int partnerServiceChangeCutGetThreadCount = 0;
    private static volatile int partnerServiceDeleteGetThreadCount = 0;
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS; // todo: заменить на импорт
    private static final Long OUTCONTRACTPACKSINGLE = 10L; // todo: заменить на импорт
    private static final Long GETCUTCONTRACTINFO = 20L; // todo: заменить на импорт

    private static final String CHANGE_ID_PARAMNAME = "id";

    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ContractProcessorFacade.class);

    @WsMethod()
    public Map<String, Object> dsCallPartnerService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (partnerServiceThreadCount == 0) {
            partnerServiceThreadCount = 1;
            try {
                logger.debug("dsCallPartnerService start");
                result = dsTryCallPartnerProcess(params);
            } finally {
                partnerServiceThreadCount = 0;
            }
        } else {
            logger.debug("dsCallPartnerService alreadyRun");
        }
        return result;
    };

    @WsMethod()
    public Map<String, Object> dsCallContractCutGetterPartnerService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (partnerServiceContractCutThreadCount < 3  ) {
            partnerServiceContractCutThreadCount++;
            try {
                logger.debug("dsCallContractCutGetterPartnerService start");
                 result = dsTryCallContractCutGetterPartnerProcess(params);
            } finally {
                
                partnerServiceContractCutThreadCount--;
            }
        } else {
            logger.debug("dsCallContractCutGetterPartnerService alreadyRun");
        }
        return result;
    };

    @WsMethod()
    public Map<String, Object> dsCallClaimCutGetterPartnerService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (partnerServiceClaimCutThreadCount == 0) {
            partnerServiceClaimCutThreadCount = 1;
            try {
                logger.debug("dsTryCallClaimCutGetterPartnerProcess start");
                result = dsTryCallClaimCutGetterPartnerProcess(params);
            } finally {
                partnerServiceClaimCutThreadCount = 0;
            }
        } else {
            logger.debug("dsTryCallClaimCutGetterPartnerProcess alreadyRun");
        }
        return result;
    };
    

    @WsMethod()
    public Map<String, Object> dsCallClaimFileAddPartnerService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (partnerServiceClaimFileThreadCount == 0) {
            partnerServiceClaimFileThreadCount = 1;
            try {
                logger.debug("dsTryCallClaimFileAddPartnerProcess start");
                result = dsTryCallClaimFileAddPartnerProcess(params);
            } finally {
                partnerServiceClaimFileThreadCount = 0;
            }
        } else {
            logger.debug("dsTryCallClaimFileAddPartnerProcess alreadyRun");
        }
        return result;
    };

    @WsMethod()
    public Map<String, Object> dsCallClaimFileAssignPartnerService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (partnerServiceClaimFileAssignThreadCount == 0) {
            partnerServiceClaimFileAssignThreadCount = 1;
            try {
                logger.debug("dsTryCallClaimFileAssignPartnerProcess start");
                result = dsTryCallClaimFileAssignPartnerProcess(params);
            } finally {
                partnerServiceClaimFileAssignThreadCount = 0;
            }
        } else {
            logger.debug("dsTryCallClaimFileAssignPartnerProcess alreadyRun");
        }
        return result;
    };

    @WsMethod()
    public Map<String, Object> dsCallChangeFileAddPartnerService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (partnerServiceChangeFileThreadCount == 0) {
            partnerServiceChangeFileThreadCount = 1;
            try {
                logger.debug("dsTryCallChangeFileAddPartnerProcess start");
                result = dsTryCallChangeFileAddPartnerProcess(params);
            } finally {
                partnerServiceChangeFileThreadCount = 0;
            }
        } else {
            logger.debug("dsTryCallChangeFileAddPartnerProcess alreadyRun");
        }
        return result;
    };

    @WsMethod()
    public Map<String, Object> dsCallChangeFileAssignPartnerService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (partnerServiceChangeFileAssignThreadCount == 0) {
            partnerServiceChangeFileAssignThreadCount = 1;
            try {
                logger.debug("dsTryCallChangeFileAssignPartnerProcess start");
                result = dsTryCallChangeFileAssignPartnerProcess(params);
            } finally {
                partnerServiceChangeFileAssignThreadCount = 0;
            }
        } else {
            logger.debug("dsTryCallChangeFileAssignPartnerProcess alreadyRun");
        }
        return result;
    };

    @WsMethod()
    public Map<String, Object> dsCallClaimPutPartnerService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (partnerServiceClaimPutThreadCount == 0) {
            partnerServiceClaimPutThreadCount = 1;
            try {
                logger.debug("dsCallClaimPutPartnerService start");
                result = dsTryCallClaimPutPartnerProcess(params);
            } finally {
                partnerServiceClaimPutThreadCount = 0;
            }
        } else {
            logger.debug("dsCallClaimPutPartnerService alreadyRun");
        }
        return result;
    };

    static final AtomicInteger partnerServiceChangePutThreadCount2 = new AtomicInteger();

    @WsMethod()
    public Map<String, Object> dsCallChangePutPartnerService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (partnerServiceChangePutThreadCount2.compareAndSet(0, 1)) {
            try {
                logger.debug("dsCallChangePutPartnerService start");
                result = dsTryCallChangePutPartnerProcess(params);
            } finally {
                partnerServiceChangePutThreadCount2.set(0);
            }
        } else {
            logger.debug("dsCallChangePutPartnerService alreadyRun");
        }
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsCallDeleteObjPartnerService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (partnerServiceDeleteGetThreadCount == 0) {
            partnerServiceDeleteGetThreadCount = 1;
            try {
                logger.debug("dsCallDeleteObjPartnerService start");
                result = dsTryCallDeletePartnerProcess(params);
            } finally {
                partnerServiceDeleteGetThreadCount = 0;
            }
        } else {
            logger.debug("dsCallDeleteObjPartnerService alreadyRun");
        }
        return result;
    }

    private Map<String,Object> dsTryCallDeletePartnerProcess(Map<String, Object> params) throws Exception {
        logger.debug("dsTryCallDeletePartnerProcess");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String deletePackSize = getCoreSettingBySysName("DeletePackSize", login, password);
        Map<String, Object> processParams = new HashMap<String, Object>();
        if (deletePackSize.isEmpty()) {
            deletePackSize = "1";
        }
        // размер пакета
        processParams.put("PACKSIZE", deletePackSize);
        // вызов обработки
        Map<String, Object> cutListRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationGetDeleted", processParams, login, password);
        logger.debug("dsTryCallChangePutPartnerProcess finished.");
        return cutListRes;
    }

    private Map<String,Object> dsTryCallChangePutPartnerProcess(Map<String, Object> params) throws Exception {
        logger.debug("dsTryCallChangePutPartnerProcess...");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String contractCutPackSize = getCoreSettingBySysName("ChangePutPackSize", login, password);
        Map<String, Object> processParams = new HashMap<String, Object>();
        if (contractCutPackSize.isEmpty()) {
            contractCutPackSize = "1";
        }
        // размер пакета
        processParams.put("PACKSIZE", contractCutPackSize);
        // для ограничения обрабатываемых допсов по ИД конкретного допса в ходе отладки
        processParams.put(CHANGE_ID_PARAMNAME, params.get(CHANGE_ID_PARAMNAME));
        // вызов обработки
        Map<String, Object> cutListRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationPutChange", processParams, login, password);
        logger.debug("dsTryCallChangePutPartnerProcess finished.");
        return cutListRes;
    };

    @WsMethod()
    public Map<String, Object> dsCallChangeCutGetterPartnerService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (partnerServiceChangeCutGetThreadCount == 0) {
            partnerServiceChangeCutGetThreadCount = 1;
            try {
                logger.debug("dsCallChangeCutGetterPartnerService start");
                result = dsTryCallChangeCutGetterPartnerProcess(params);
            } finally {
                partnerServiceChangeCutGetThreadCount = 0;
            }
        } else {
            logger.debug("dsCallChangeCutGetterPartnerService alreadyRun");
        }
        return result;
    }

    private Map<String,Object> dsTryCallChangeCutGetterPartnerProcess(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String contractCutPackSize = getCoreSettingBySysName("ChangeGetPackSize", login, password);
        Map<String, Object> getparams = new HashMap<String, Object>();
        if (contractCutPackSize.isEmpty()) {
            contractCutPackSize = "1";
        }
        getparams.put("PACKSIZE", contractCutPackSize);

        Map<String, Object> cutListRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationGetCutChange", getparams, login, password);

        return cutListRes;
    };


    private Map<String, Object> dsTryCallPartnerProcess(Map<String, Object> params) throws Exception {
        //1. выбрать договора подлежащие отправке.
        List<Map<String, Object>> contrIdList = getUnprocessedSignedContractIdList(params);
        //2. вызвать сервис для каждого из договоров в цикле.
        logger.info("partner start process " + contrIdList.size() + " contracts");
        int count = 0;
        for (Map<String, Object> contrIdMap : contrIdList) {
            callPartnerService4Contract(contrIdMap, params);
            count++;
            // для тестирования
            if (count > 10) {
                break;
            }
            logger.info("partner process " + count + " from " + contrIdList.size() + " contracts");
        }
        logger.info("partner finish process " + contrIdList.size() + " contracts");
        //3. сохранить запрос и реквест в очередь запросов. 
        return null;
    }

    private List<Map<String, Object>> getUnprocessedSignedContractIdList(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // информации о способе формирования перечня подлежащих отправке договоров нет,
        // поэтому пока выбираем все подписанные договора, которые ранее не были отправлены.
        // с целью упрощения повторной попытки отправить договор опрашиваем requeststatusid у запроса, по договору, если 
        // 1000 - запрос выполнен
        // 0 запрос необходимо выполнить.
        Map<String, Object> contrParams = new HashMap<String, Object>();
        // не пытатся повторно отправить неуспешные договора.
        //contrParams.put("NORETRYFAIL", "TRUE");
        String prodSysNameList = getCoreSettingBySysName("PARTNERSERVICEPRODSYSNAME", login, password);
        if (prodSysNameList.isEmpty()) {
            //prodSysNameList = "'B2B_INVEST_NUM1','B2B_INVEST_COUPON','B2B_NEW_HORIZONS'";
            prodSysNameList = "'LIGHTHOUSE','SMART_POLICY_RB_ILIK','B2B_NEW_HORIZONS'";
        }
        contrParams.put("PRODSYSNAMELIST", prodSysNameList);
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseList4PartnerService", contrParams, login, password);
        List<Map<String, Object>> result = null;
        if (res.get(RESULT) != null) {
            result = (List<Map<String, Object>>) res.get(RESULT);
        }
        return result;
    }

    private void callPartnerService4Contract(Map<String, Object> contrIdMap, Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contrParams = new HashMap<String, Object>();
        List<Object> contrIdList = new ArrayList<Object>();
        if (contrIdMap.get("CONTRID") != null) {

            contrIdList.add(contrIdMap.get("CONTRID"));
            // 1. пытаемся получить запрос по данному договору из очереди.
            Map<String, Object> queueMap = getRequestQueue(contrIdMap, OUTCONTRACTPACKSINGLE, login, password);
            // если запроса нет - создаем новый.
            int tryCount = 0;
            if (queueMap.get("TRYCOUNT") != null) {
                tryCount = Integer.valueOf(queueMap.get("TRYCOUNT").toString()).intValue();
            }
            // не выполнять повторную попытку отправки сбойного договора.
            if (tryCount == 0) {
                // если есть, берем из него tryCount делаем +1 
                contrParams.put("CONTRIDLIST", contrIdList);
                Map<String, Object> partnerRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationGetContractsData", contrParams, login, password);
                if (partnerRes.get(RESULT) != null) {
                    partnerRes = (Map<String, Object>) partnerRes.get(RESULT);
                    if (partnerRes.get("STATUS") != null) {
                        if ("DONE".equalsIgnoreCase(partnerRes.get("STATUS").toString())) {
                            // вызов успех. сохраняем в реквест данные.
                            updRequestQueue(partnerRes, queueMap, tryCount, 1000, login, password);
                        } else {
                            updRequestQueue(partnerRes, queueMap, tryCount, 404, login, password);
                        }
                    }
                }
            }
        }
    }

    private Map<String, Object> getRequestQueue(Map<String, Object> contrIdMap, Long requestType, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("CONTRID", contrIdMap.get("CONTRID"));
//        param.put("REQUESTTYPEID", requestType);
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BRequestQueueBrowseListByParam", param, login, password);
        if (res.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) res.get(RESULT);
            if (!resList.isEmpty()) {
                for (Map<String, Object> map : resList) {
                    if (map.get("REQUESTTYPEID") != null) {
                        if ("10".equals(map.get("REQUESTTYPEID").toString())) {
                            return map;
                        }
                    } else {
                        // проставить 10 тип
                        // todo когда не останется нуловых типов - убрать эту ветку.
                        Map<String, Object> queueMap = new HashMap<String, Object>();
                        queueMap.putAll(map);
                        queueMap.put("REQUESTTYPEID", requestType);
                        Map<String, Object> resUpd = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BRequestQueueUpdate", queueMap, login, password);
                        return queueMap;
                    }
                }
            }
            // если дошли до сюда, значит не нашли нужного типа запрос, создаем.
            param.put("REQUESTTYPEID", requestType);
            param.put("TRYCOUNT", 0);
            param.put("REQUESTDATE", new Date());
            param.put("REQUESTSTATEID", 0); //(состояние до выполнения. после выполнения успешного запроса меняется на 1000)
            res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BRequestQueueCreate", param, login, password);
            if (res.get(RESULT) != null) {
                Map<String, Object> createRes = (Map<String, Object>) res.get(RESULT);
                param.put("REQUESTQUEUEID", createRes.get("REQUESTQUEUEID"));
            }
            return param;

        }
        return null;
    }

    private Map<String, Object> updRequestQueue(Map<String, Object> partnerRes, Map<String, Object> queueMap, int tryCount, int stateid, String login, String password) throws Exception {
        tryCount++;
        queueMap.put("TRYCOUNT", tryCount);
        queueMap.put("REQUESTSTATEID", stateid);
        queueMap.put("PROCESSDATE", new Date());
        queueMap.put("XMLREQUEST", partnerRes.get("requestStr"));
        queueMap.put("XMLRESPONSE", partnerRes.get("responseStr"));

        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BRequestQueueUpdate", queueMap, login, password);
        return res;
    }

    private Map<String, Object> dsTryCallContractCutGetterPartnerProcess(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String contractCutPackSize = getCoreSettingBySysName("ContractCutPackSize", login, password);
        Map<String, Object> processParams = new HashMap<String, Object>();
        if (contractCutPackSize.isEmpty()) {
            contractCutPackSize = "1";
        }
        processParams.put("PACKSIZE", contractCutPackSize);
        if (isIntegrationInDebugMode()) {
            String fromDateStr = getStringParam(params, "FROMDATESTR");
            String toDateStr = getStringParam(params, "TODATESTR");
            if (!fromDateStr.isEmpty() && !toDateStr.isEmpty()) {
                // !только для отладки!
                processParams.put("FROMDATESTR", fromDateStr);
                processParams.put("TODATESTR", toDateStr);
            }
            String programListStr = getStringParam(params, "PROGRAMLISTSTR");
            if (!programListStr.isEmpty()) {
                // !только для отладки!
                processParams.put("PROGRAMLISTSTR", programListStr);
            }
            String contractCutPackSizeStr = getStringParam(params, "PACKSIZESTR");
            if (!contractCutPackSizeStr.isEmpty()) {
                // !только для отладки!
                processParams.put("PACKSIZE", contractCutPackSizeStr);
            }
        }
        Map<String, Object> cutListRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationGetContractCutList", processParams, login, password);

        return cutListRes;
    }

    private Map<String, Object> dsTryCallClaimCutGetterPartnerProcess(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String contractCutPackSize = getCoreSettingBySysName("ClaimCutPackSize", login, password);
        Map<String, Object> getparams = new HashMap<String, Object>();
        if (contractCutPackSize.isEmpty()) {
            contractCutPackSize = "1";
        }
        getparams.put("PACKSIZE", contractCutPackSize);
        Map<String, Object> cutListRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationGetClaimCutList", getparams, login, password);

        return cutListRes;
    }

    private Map<String, Object> dsTryCallClaimFileAddPartnerProcess(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String contractCutPackSize = getCoreSettingBySysName("ClaimFileAddPackSize", login, password);
        Map<String, Object> getparams = new HashMap<String, Object>();
        if (contractCutPackSize.isEmpty()) {
            contractCutPackSize = "1";
        }
        getparams.put("PACKSIZE", contractCutPackSize);
        Map<String, Object> cutListRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationPutClaimFiles", getparams, login, password);

        return cutListRes;
    }

    private Map<String, Object> dsTryCallClaimFileAssignPartnerProcess(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String contractCutPackSize = getCoreSettingBySysName("ClaimFileAddPackSize", login, password);
        Map<String, Object> getparams = new HashMap<String, Object>();
        if (contractCutPackSize.isEmpty()) {
            contractCutPackSize = "1";
        }
        getparams.put("PACKSIZE", contractCutPackSize);
        Map<String, Object> cutListRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationAssignClaimFiles", getparams, login, password);

        return cutListRes;
    }

    private Map<String, Object> dsTryCallChangeFileAddPartnerProcess(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String contractCutPackSize = getCoreSettingBySysName("ChangeFileAddPackSize", login, password);
        Map<String, Object> getparams = new HashMap<String, Object>();
        if (contractCutPackSize.isEmpty()) {
            contractCutPackSize = "1";
        }
        getparams.put("PACKSIZE", contractCutPackSize);
        Map<String, Object> cutListRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationPutChangeFiles", getparams, login, password);

        return cutListRes;
    }

    private Map<String, Object> dsTryCallChangeFileAssignPartnerProcess(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String contractCutPackSize = getCoreSettingBySysName("ChangeFileAddPackSize", login, password);
        Map<String, Object> getparams = new HashMap<String, Object>();
        if (contractCutPackSize.isEmpty()) {
            contractCutPackSize = "1";
        }
        getparams.put("PACKSIZE", contractCutPackSize);
        Map<String, Object> cutListRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationAssignChangeFiles", getparams, login, password);

        return cutListRes;
    }

    private Map<String, Object> dsTryCallClaimPutPartnerProcess(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String contractCutPackSize = getCoreSettingBySysName("ClaimPutPackSize", login, password);
        Map<String, Object> getparams = new HashMap<String, Object>();
        if (contractCutPackSize.isEmpty()) {
            contractCutPackSize = "1";
        }
        getparams.put("PACKSIZE", contractCutPackSize);
        Map<String, Object> cutListRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationPutClaim", getparams, login, password);

        return cutListRes;
    }

    public boolean greaterThanCompareAndSet(AtomicInteger atomicCounter, int newValue) {
        return atomicCounter.updateAndGet(x -> x < newValue ? newValue : x) == newValue;
    }

}
