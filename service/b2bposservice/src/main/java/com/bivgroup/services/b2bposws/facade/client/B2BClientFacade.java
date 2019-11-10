package com.bivgroup.services.b2bposws.facade.client;

import com.bivgroup.core.dictionary.dao.jpa.RowStatus;
import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * фасад назван криво
 * не нужно добавлять сюда методы по работе с сущностью клиента
 * в основном собраны разные методы вызываемые из lifesbolws
 */
public class B2BClientFacade extends B2BDictionaryBaseFacade {

    private static final Logger logger = Logger.getLogger(B2BClientFacade.class);

    @WsMethod(requiredParams = {"$type2$"})
    public Map dctFindByExampleRemote(Map params) throws Exception {
        // вызов поиска клиента
        List<Map<String, Object>> entities = dctFindByExample(getStringParam(params, "$type2$"), params);
        Map result = new HashMap();
        result.put("Result", entities);
        return result;
    }

    @WsMethod(requiredParams = {"$type2$"})
    public Map dctCrudByHierarchyRemote(Map params) throws Exception {
            // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
        parseDatesBeforeDictionaryCalls(params);
        return dctCrudByHierarchy(getStringParam(params, "$type2$"), params);
    }

    @WsMethod(requiredParams = {"externalId"})
    public Map<String, Object> dsB2BFindClientByExternalId(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BFindClientByExternalId begin");

        String externalId = getStringParam(params, "externalId");
        if (externalId.isEmpty()) {
            logger.error("externalId.isEmpty");
            throw new IllegalArgumentException("externalId empty");
        }

        // параметры клиента
        Map<String, Object> filterByExternalId = new HashMap<String, Object>();
        filterByExternalId.put("externalId", externalId);
        // вызов поиска клиента
        List<Map<String, Object>> clientId_ENs = dctFindByExample(PCLIENT_VER_ENTITY_NAME, filterByExternalId);
        if (clientId_ENs.isEmpty()) {
            logger.debug("clientId_ENs.isEmpty");
            return null;
        }
        if (clientId_ENs.size() > 1) {
            logger.debug("clientId_ENs.size() > 1 too many clients");
            throw new IllegalStateException("too many clients with same externalid");
        }
        Map<String, Object> clientId_EN = clientId_ENs.get(0);

        // найдем соглашения клиента
        HashMap<String, Object> findByClientId = new HashMap<String, Object>();
        findByClientId.put("clientId", clientId_EN.get("id"));
        // запросим в бд соглашения клиента
        List<Map<String, Object>> clientAgreementId_ENs = dctFindByExample(CLIENT_AGREEMENT_ENTITY_NAME, findByClientId);
        clientId_EN.put("agreements", clientAgreementId_ENs);

        Map<String, Object> result = new HashMap<>();
        result.putAll(clientId_EN);
        markAllMapsByKeyValue(result, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        logger.debug("dsB2BFindClientByExternalId end");
        return result;
    }

    /**
     * безусловное создание клиента
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BCreateClientEntityAndAttachContracts(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BCreateClientEntityAndAttachContracts start...");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        // найдем внешний ид клиента по
        Long externalIdFromPartner = getExternalIdFromPartner(params);

        Map<String, Object> newClientId_EN = new HashMap<>();
        newClientId_EN.put("externalId", externalIdFromPartner);

        // создадим нового клиента
        newClientId_EN = saveClientId_EN(newClientId_EN);

        // прикрепим к нему все контракты
        attachContracts((Long) newClientId_EN.get("id"), login, password);

        logger.debug("dsB2BCreateClientEntityAndAttachContracts end.");
        Map<String, Object> result = new HashMap<String, Object>();
        return result;
    }

    Long getExternalIdFromPartner(Map<String, Object> params) throws Exception {
        Long result = -1L;

        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        Map<String, Object> clientProfileMap = (Map<String, Object>) params.get("CLIENTPROFILEMAP");
        Map<String, Object> clientId_EN = (Map<String, Object>) clientProfileMap.get("clientId_EN");

        String fio = getStringParam(clientId_EN, "surname") + " " + getStringParam(clientId_EN, "name") + " " + getStringParam(clientId_EN, "patronymic");

        HashMap<String, Object> reqPar = new HashMap<>();

        reqPar.put("FIO", fio);
        reqPar.put("BIRTHDATE", parseAnyDate(clientId_EN.get("dateOfBirth$date"), Date.class, "dateOfBirth$date", true));
        reqPar.put("POLICYNUMBER", getStringParam(params, "CONTRNUMBER"));

        Map<String, Object> clientProfileAccount = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsLifeShortExternalIdRequest", reqPar, login, password);
        if (clientProfileAccount != null && clientProfileAccount.get(RESULT) != null) {
            Map<String, Object> client = (Map<String, Object>) clientProfileAccount.get(RESULT);
            if (client.containsKey("EXTPARTICIPANTID")) {
                result = (Long) client.get("EXTPARTICIPANTID");
            }
        }

        return result;
    }

    @WsMethod(requiredParams = {"CLIENTID"})
    public Map<String, Object> dsB2BAttachContracts(Map<String, Object> params) throws Exception {
        try {
            String login = (String) params.get(LOGIN);
            String password = (String) params.get(PASSWORD);
            attachContracts((Long) params.get("CLIENTID"), login, password);
        } catch (Exception e) {
            logger.error("failed to attach contracts", e);
            throw e;
        }
        return null;
    }

    void attachContracts(Long clientID, String login, String password) throws Exception {
        Map<String, Object> paramReq = new HashMap<String, Object>();
        paramReq.put("CLIENTID", clientID);
        paramReq.put("PROGEXTIDLIST", "'CL','1', '2', 'PROTECTED_DEPOSITOR_S175', 'NPR2V3'");
        Map<String, Object> notAttachedContractList = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BGetNotAddedContracts", paramReq, login, password);
        List<Map<String, Object>> listContract = (List<Map<String, Object>>) notAttachedContractList.get("Result");
        for (Map<String, Object> contract : listContract) {
            Map<String, Object> reqestMap = new HashMap<String, Object>();
            reqestMap.put("clientId", contract.get("CLIENTID"));
            reqestMap.put("contractId", contract.get("CONTRID"));
            Map<String, Object> dsB2BContractAttachOnCreate = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractAttachOnCreate", reqestMap, login, password);
        }
    }

    Map<String, Object> getExternalIdFromContract(Long contrId, String login, String password) throws Exception {
        Map<String, Object> reqPar = new HashMap<>();
        reqPar.put("CONTRID", contrId);
        Map<String, Object> member = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BMemberBrowseListByParam", reqPar, login, password);
        return member;
    }

    Map<String, Object> saveClientId_EN(Map<String, Object> clientMap) throws Exception {
        clientMap.putAll(getClientNamesMap(clientMap));
        Map<String, Object> savedClientMap = dctCrudByHierarchy(PCLIENT_VER_ENTITY_NAME, clientMap);
        markAllMapsByKeyValue(savedClientMap, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        return savedClientMap;
    }

}
