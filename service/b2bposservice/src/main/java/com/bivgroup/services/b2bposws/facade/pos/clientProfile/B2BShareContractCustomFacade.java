package com.bivgroup.services.b2bposws.facade.pos.clientProfile;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import com.bivgroup.services.b2bposws.system.SmsSender;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BShareContractCustom")
public class B2BShareContractCustomFacade extends B2BDictionaryBaseFacade {

    private Logger logger = Logger.getLogger(this.getClass());
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    private static Map<String, Map<String, Object>> shareRoleBySysName = null;
    private static Map<String, Long> shareRoleEIDBySysName = null;

    private void initShareRoleMaps() throws Exception {
        logger.debug("Analysing share roles classifer data...");
        // обращение к словарной системе для получения данных классификатора
        String clsName = KIND_SHARE_ROLE_ENTITY_NAME;
        Map<String, Object> clsDataParams = new HashMap<String, Object>();
        boolean isCallFromGate = false;
        List<Map<String, Object>> hbDataList = dctFindByExample(clsName, clsDataParams, isCallFromGate);
        shareRoleBySysName = getMapByFieldStringValues(hbDataList, "sysname");
        shareRoleEIDBySysName = new HashMap<String, Long>();
        for (Map.Entry<String, Map<String, Object>> shareRole : shareRoleBySysName.entrySet()) {
            String shareRoleSysName = shareRole.getKey();
            Map<String, Object> shareRoleMap = shareRole.getValue();
            Long shareRoleEID = getLongParam(shareRoleMap, "eId");
            shareRoleEIDBySysName.put(shareRoleSysName, shareRoleEID);
            logger.debug(String.format("Share role with system name (Sysname) of '%s' got id (EID) = %d.", shareRoleSysName, shareRoleEID));
        }
        logger.debug("Analysing share roles classifer data finshed.");
    }

    private Map<String, Object> getShareRoleBySysName(String shareRoleSysName) throws Exception {
        logger.debug(String.format("Getting share role map by event system name (Sysname) = %s...", shareRoleSysName));
        if (shareRoleBySysName == null) {
            initShareRoleMaps();
        }
        Map<String, Object> shareRole = shareRoleBySysName.get(shareRoleSysName);
        logger.debug(String.format("Getting share role map finished - share role with system name (Sysname) of '%s' = ", shareRoleSysName) + shareRole);
        return shareRole;
    }

    private Long getShareRoleEIDBySysName(String shareRoleSysName) throws Exception {
        logger.debug(String.format("Getting share role id (EID) by share role system name (Sysname) = %s...", shareRoleSysName));
        if (shareRoleBySysName == null) {
            initShareRoleMaps();
        }
        Long shareRoleEID = shareRoleEIDBySysName.get(shareRoleSysName);
        logger.debug(String.format("Getting share role id (EID) finished - share role with system name (Sysname) of '%s' got id (EID) = %d.", shareRoleSysName, shareRoleEID));
        return shareRoleEID;
    }

    @WsMethod(requiredParams = {"contractId"})
    public Map<String, Object> dsB2BContractUnattach(Map<String, Object> params) throws Exception {
        Map<String,Object> delParams = new HashMap<>();
        delParams.put("contractId", params.get("contractId"));
        List<Map<String,Object>> sharedContrList = dctFindByExample(SHARE_CONTRACT_INS_ENTITY_NAME, delParams);
        for (Map<String,Object> sharedContr :sharedContrList) {
            sharedContr.put(ROWSTATUS_PARAM_NAME,RowStatus.DELETED.getId());
        }
        List<Map<String,Object>> delResList = dctCrudByHierarchy(SHARE_CONTRACT_INS_ENTITY_NAME, sharedContrList);
        Map<String,Object> result = new HashMap<>();
        result.put("DELRESLIST", delResList);
        return result;
    }

    @WsMethod(requiredParams = {""})
    public Map<String, Object> dsB2BContractAttachBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<>();
        List<Map<String,Object>> sharedContrList = dctFindByExample(SHARE_CONTRACT_INS_ENTITY_NAME, params);
        markAllMapsByKeyValue(sharedContrList, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        result.put(RESULT, sharedContrList);
        return result;
    }

    @WsMethod(requiredParams = {"clientId", "contractId"})
    public Map<String, Object> dsB2BContractAttachOnCreate(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BContractAttachOnCreate...");

        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);

        Map<String, Object> shareContractRes;
        Long clientID = getLongParamLogged(params, "clientId");
        Long contractID = getLongParamLogged(params, "contractId");
        Long shareRoleID = getLongParamLogged(params, SHARE_ROLE_ID_PARAMNAME);

        // проверка для исключения повторного прикрепления
        Map<String, Object> existedShareContractParams = new HashMap<String, Object>();
        existedShareContractParams.put("contractId", contractID);
        List<Map<String, Object>> existedShareContractRes = dctFindByExample(SHARE_CONTRACT_INS_ENTITY_NAME, existedShareContractParams, isCallFromGate);

        if ((existedShareContractRes != null) && (existedShareContractRes.isEmpty())) {

            String phoneNumber = getStringParamLogged(params, "tel");

            if (phoneNumber.isEmpty()) {
                logger.debug("Phone number is not specified - searching in client account...");
                Map<String, Object> clientAccountParams = new HashMap<String, Object>();
                clientAccountParams.put("clientId", clientID);
                //Map<String, Object> clientParams = new HashMap<String, Object>();
                //clientParams.put("ID", clientID);
                //clientAccountParams.put("ClientID_EN", clientParams);
                List<Map<String, Object>> clientAccountsList = dctFindByExample(CLIENT_PROFILE_ENTITY_NAME, clientAccountParams, isCallFromGate);
                logger.debug("clientAccountsList = " + clientAccountsList);

                if ((clientAccountsList != null) && (clientAccountsList.size() == 1)) {
                    //Map<String, Object> client = clientAccountsList.get(0);
                    //Map<String, Object> clientAccount = (Map<String, Object>) client.get("ClientProfileID_EN");
                    Map<String, Object> clientAccount = clientAccountsList.get(0);
                    phoneNumber = getStringParamLogged(clientAccount, "tel");
                } else {
                    logger.error("Error occured during searching phone number in client account or multiple accounts was found!");
                }
            }

            if (shareRoleID == null) {
                logger.debug("Share role id (" + SHARE_ROLE_ID_PARAMNAME + ") is not specified - checking share role system name (" + SHARE_ROLE_SYSNAME_PARAMNAME + ")...");
                String shareRoleSysName = getStringParamLogged(params, SHARE_ROLE_SYSNAME_PARAMNAME);
                if (shareRoleSysName.isEmpty()) {
                    logger.warn("Share role id (" + SHARE_ROLE_ID_PARAMNAME + ") and system name (" + SHARE_ROLE_SYSNAME_PARAMNAME + ") is not specified - will be used default share role.");
                    shareRoleSysName = SHARE_ROLE_SYSNAME_DEFAULT;
                }
                shareRoleID = getShareRoleEIDBySysName(shareRoleSysName);
            } else {
                logger.debug("Share role id (" + SHARE_ROLE_ID_PARAMNAME + ") is specified directly.");
            }

            if (shareRoleID == null) {
                // ошибка - не удалось выбрать роль, даже дефолтную
                logger.error("Unable to select share role id, required for attaching contract! Details (dsB2BContractAttachOnCreate params): " + params);
                throw new Exception("Unable to select share role id, required for attaching contract!");
            } else {
                Map<String, Object> shareContractParams = new HashMap<String, Object>();
                //shareContractParams.put("ClientID", clientID);
                //setLinkParamWTF(shareContractParams, "clientId", clientID);
                shareContractParams.put("clientId", clientID);
                shareContractParams.put("contractId", contractID);
                shareContractParams.put("tel", phoneNumber);
                //shareContractParams.put(SHARE_ROLE_ID_FIELDNAME, shareRoleID);
                //setLinkParamWTF(shareContractParams, SHARE_ROLE_ID_FIELDNAME, shareRoleID);
                shareContractParams.put(SHARE_ROLE_ID_FIELDNAME, shareRoleID);
                shareContractRes = dctOnlySave(SHARE_CONTRACT_INS_ENTITY_NAME, shareContractParams, isCallFromGate);
                if ((shareContractRes != null) && (shareContractRes.get("id") != null)) {
                    // успешное прикрепление
                    boolean isAttachNotifyBySms = getBooleanParamLogged(params, IS_ATTACH_NOTIFY_BY_SMS_PARAMNAME, Boolean.FALSE);
                    String prodSysName = getStringParamLogged(params, PRODUCT_SYSNAME_PARAMNAME);
                    if (isAttachNotifyBySms && !prodSysName.isEmpty()) {
                        Map<String, Object> prodDefValParams = new HashMap<String, Object>();
                        prodDefValParams.put(PRODUCT_SYSNAME_PARAMNAME, prodSysName);
                        prodDefValParams.put("NAME", ATTACH_NOTIFY_SMS_TEXT_PARAMNAME);
                        prodDefValParams.put(RETURN_AS_HASH_MAP, true);
                        Map<String, Object> prodDefVal = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueBrowseListByNameNote", prodDefValParams, login, password);
                        if (isCallResultOKAndContains(prodDefVal, "VALUE")) {
                            String smsNotifyMessage = getStringParamLogged(prodDefVal, "VALUE", String.format(
                                    "Value of '%s' product constant for product with system name = '%s'", ATTACH_NOTIFY_SMS_TEXT_PARAMNAME, PRODUCT_SYSNAME_PARAMNAME
                            ));
                            if (!smsNotifyMessage.isEmpty()) {
                                SmsSender smsSender = new SmsSender();
                                Map<String, Object> sendRes = smsSender.sendSms(phoneNumber, smsNotifyMessage);
                                logger.debug(String.format(
                                        "Send sms on number '%s' with message '%s' finished with result: %s",
                                        phoneNumber, smsNotifyMessage, sendRes
                                ));
                                shareContractRes.put("SENDRES", sendRes);
                            }
                        }
                    }
                }
            }
        } else {
            logger.warn("This contract is already attached - attaching skipped!");
            if ((existedShareContractRes != null) && (existedShareContractRes.size() == 1)) {
                shareContractRes = existedShareContractRes.get(0);
            } else {
                shareContractRes = new HashMap<String, Object>();
                shareContractRes.put(RESULT, existedShareContractRes);
            }
        }

        logger.debug("dsB2BContractAttachOnCreate result: " + shareContractRes);
        logger.debug("dsB2BContractAttachOnCreate end.");
        return shareContractRes;
    }

    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> dsB2BContractAttachToInsurerWithCreateProfile(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        logger.debug("Sbol processing. begin profile create, attach contract, sms send");
        Map<String, Object> attachRes = null;

        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
        if (contrMap != null) {
            String insurerId = getStringParam(contrMap.get("INSURERID"));
            Map<String, Object> insurerMap = null; //(Map<String, Object>) contrMap.get("INSURERMAP");
            if (!insurerId.isEmpty()) {
                Map<String, Object> partParams = new HashMap<String, Object>();
                partParams.put("PARTICIPANTID", insurerId);
                partParams.put("ReturnAsHashMap", "TRUE");
                insurerMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BParticipantLoad", partParams, login, password);
            }
            if (insurerMap != null) {
                if (insurerMap.get("contactList") != null) {
                    List<Map<String, Object>> contactList = (List<Map<String, Object>>) insurerMap.get("contactList");
                    if (!contactList.isEmpty()) {
                        Map<String, Object> phoneMap = null;
                        phoneMap = (Map<String, Object>) contactList.get(0);
                        for (Map<String, Object> contactMap : contactList) {
                            if ("MobilePhone".equalsIgnoreCase(getStringParam(contactMap.get("CONTACTTYPESYSNAME")))) {
                                phoneMap = contactMap;
                            }
                        }
                        if (phoneMap != null) {
                            // получить телефон из договора.
                            String mobilePhone = getStringParam(phoneMap.get("VALUE"));
                            if (!mobilePhone.isEmpty()) {
                                if (mobilePhone.length() > 10) {
                                    mobilePhone = mobilePhone.substring(mobilePhone.length() - 10, mobilePhone.length());
                                }
                                logger.debug("Sbol processing. phone exist: " + mobilePhone);
                                Map<String, Object> searchParams = new HashMap<String, Object>();
                                searchParams.put("PHONENUMBER", mobilePhone);
                                String clientId = null;
                                Map<String, Object> profileRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BClientProfileAccountFindByPhone", searchParams, login, password);
                                if (profileRes.get(RESULT) != null) {
                                    List<Map<String, Object>> accountList = (List<Map<String, Object>>) profileRes.get(RESULT);
                                    if (!accountList.isEmpty()) {
                                        if (accountList.size() == 1) {
                                            // штатная ситуация, аккаунт существует в 1 экземпляре.

                                            Map<String, Object> clientMap = (Map<String, Object>) accountList.get(0).get("CLIENTMAP");
                                            if (clientMap != null) {
                                                clientId = getStringParam(clientMap.get("id"));
                                                logger.debug("Sbol processing. profile exist: " + clientId);
                                            }
                                        } else {
                                            for (Map<String, Object> map : accountList) {
                                                logger.error("Sbol processing. many profile exist: " + accountList.size() + " count");
                                                // по номеру телефона найдено несколько аккаунтов. ситуация не штатная
                                                // пока - прерываем выполнение, позже 
                                                // возможно стоит добавить фильтр аккаунтов по другим полям страхователя (ФИО, паспорт)
                                            }
                                        }
                                    }

                                }
                                if ((clientId == null) || (clientId.isEmpty())) {
                                    //лк страхователя не найден - создать новый.
                                    Map<String, Object> dualMap = new HashMap<String, Object>();
                                    Map<String, Object> clientMap = new HashMap<String, Object>();

                                    Map<String, Object> accountMap = new HashMap<String, Object>();
                                    accountMap.put("tel", mobilePhone);

                                    clientMap.putAll(getClientMapByCrmParticipantMap(insurerMap, mobilePhone));

                                    dualMap.put("CLIENTMAP", clientMap);
                                    dualMap.put("ACCOUNTMAP", accountMap);
                                    dualMap.put("ISCALLFROMGATE", true);

                                    Map<String, Object> createRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BClientProfileAccountSave", dualMap, login, password);
                                    if (createRes.get(RESULT) != null) {
                                        Map<String, Object> resDualMap = (Map<String, Object>) createRes.get(RESULT);
                                        Map<String, Object> resClientMap = (Map<String, Object>) resDualMap.get("CLIENTMAP");
                                        if (resClientMap != null) {
                                            clientId = getStringParam(resClientMap.get("id"));
                                            logger.debug("Sbol processing. profile create: " + clientId);

                                        }
                                    }
                                }
                                if ((clientId != null) && (!clientId.isEmpty())) {
                                    // если профиль клиента не найден и не создался - дальше продолжать смысла нет. ошибка.
                                    Map<String, Object> attachParams = new HashMap<String, Object>();
                                    attachParams.put("clientId", clientId);
                                    String contrId = getStringParam(contrMap.get("CONTRID"));
                                    attachParams.put("contractId", contrMap.get("CONTRID"));
                                    attachParams.put("tel", mobilePhone);
                                    logger.debug("Sbol processing. attach contract: " + contrId + " to profile:" + clientId);

                                    attachRes = dsB2BContractAttachOnCreate(attachParams);
                                    if (attachRes.get("id") != null) {
                                        // по результату - нужно понять успех прикрепления или нет.
                                        // если успех - отправить смс.
                                        String smsText = null;
                                        Map<String, Object> proddefvalParams = new HashMap<String, Object>();
                                        if (contrMap.get("PRODCONFID") != null) {
                                            proddefvalParams.put("PRODCONFID", contrMap.get("PRODCONFID"));
                                            proddefvalParams.put("ReturnAsHashMap", "TRUE");
                                            Map<String, Object> proddefvalRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueByProdConfId", proddefvalParams, login, password);
                                            if (proddefvalRes.get(ATTACH_NOTIFY_SMS_TEXT_PARAMNAME) != null) {
                                                String lkNotifyText = getStringParam(proddefvalRes.get(ATTACH_NOTIFY_SMS_TEXT_PARAMNAME));
                                                if (!lkNotifyText.isEmpty()) {
                                                    smsText = lkNotifyText;
                                                }
                                            }
                                        }
                                        if (smsText != null) {
                                            SmsSender ss = new SmsSender();
                                            logger.debug("Sbol processing. send sms: " + mobilePhone + " text:" + smsText);
                                            ss.sendSms(mobilePhone, smsText);
                                            // если не успех - писать в лог.
                                        }
                                    } else {
                                        logger.error("Sbol processing. error attach contr: " + getStringParam(contrMap.get("CONTRID")) + " to account: " + mobilePhone);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.debug("Sbol processing. finish");
        return attachRes;
    }

}
