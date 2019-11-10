package com.bivgroup.services.b2bposws.facade.pos.clientProfile;

import com.bivgroup.core.dictionary.dao.jpa.HierarchyDAO;
import com.bivgroup.core.dictionary.dao.jpa.JPADAOFactory;
import com.bivgroup.crm.Crm2;
import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryCaller;
import com.bivgroup.services.b2bposws.facade.pos.validation.ValidationParameterB2BCustom;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import org.hibernate.tuple.DynamicMapInstantiator;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;

import java.util.*;

import ru.diasoft.services.inscore.facade.RowStatus;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BClientProfileCustom")
public class B2BClientProfileCustomFacade extends B2BDictionaryBaseFacade {

	private final Long TYPE_HOLDER=1L;

    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
     
    protected static final Map<String, String> CLIENTPROFILE_STATUS_TRANSITION_SYSNAME_MAP = new HashMap<String, String>() {
        {
            put("FROM_BlOCKED_TO_ACTIVE", "SD_CLIENTPROFILE_from_BLOCKED_to_ACTIVE");
            put("FROM_ACTIVE_TO_BLOCKED", "SD_CLIENTPROFILE_from_ACTIVE_to_BLOCKED");
        }
    };
    
    private final Logger logger = Logger.getLogger(this.getClass());

    final private byte[] Salt = {
            (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
            (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};
    public static final String SERVICE_NAME = Constants.B2BPOSWS;

    /*
    // здесь более не требуется - будет доступно за счет B2BBaseFacade implements SeaweedsGetters
    protected String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }
    */

    private List<Map<String, Object>> makeDualMapsList(List<Map<String, Object>> resultList) {
        List<Map<String, Object>> dualMapResultList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> bean : resultList) {
            Map<String, Object> dualMap = makeDualMap(bean);
            dualMapResultList.add(dualMap);
        }
        return dualMapResultList;
    }

    private Map<String, Object> makeDualMap(Map<String, Object> accountMap) {
        Map<String, Object> dualMap = new HashMap<String, Object>();
        Map<String, Object> clientMap = (Map<String, Object>) accountMap.remove("clientId_EN");
        accountMap.put("clientId", clientMap.get("id"));
        dualMap.put("CLIENTMAP", clientMap);
        dualMap.put("ACCOUNTMAP", accountMap);
        return dualMap;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BClientProfileContractBrowseListByParamEx(Map<String, Object> params) throws Exception {
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = this.selectQuery("dsB2BClientProfileContractBrowseListByParamEx", "dsB2BClientProfileContractBrowseListByParamExCount", params);
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс
            parseDates(result, String.class);
        }
        return result;
    }

    @WsMethod(requiredParams = {"CLIENTMAP"})
    public Map<String, Object> dsB2BClientProfileClientSave(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileClientSave begin");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> clientMap = (Map<String, Object>) params.get("CLIENTMAP");
        if (isCallFromGate) {
            // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
            parseDatesBeforeDictionaryCalls(clientMap);
        }
        clientMap.putAll(getClientNamesMap(clientMap));
        Map<String, Object> result = dctCrudByHierarchy(PCLIENT_VER_ENTITY_NAME, clientMap, isCallFromGate);
        markAllMapsByKeyValue(result, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        logger.debug("dsB2BClientProfileClientSave end");
        return result;
    }

    private boolean validateSaveParams(Map<String, Object> clientMap) {
        ValidationParameterB2BCustom validationClientProfileParameter = new B2BClientProfileParameterValidation(clientMap, null);
        boolean result = validationClientProfileParameter.validationParametersBeforeSaving();
        return result;
    }

    @WsMethod(requiredParams = {"id"})
    public Map<String, Object> dsB2BClientProfileClientLoad(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileClientLoad begin");
        boolean isCallFromGate = isCallFromGate(params);
        Long clientId = Long.valueOf(params.get("id").toString());
        Map<String, Object> clientMap = dctFindById(PCLIENT_VER_ENTITY_NAME, clientId, isCallFromGate);
        markAllMapsByKeyValue(clientMap, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        logger.debug("dsB2BClientProfileClientLoad end");
        return clientMap;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BClientProfileBrowseListByParamEx(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileBrowseListByParamEx begin");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> clientMap = dctFindByExample(CLIENT_PROFILE_ENTITY_NAME, params, isCallFromGate);
        markAllMapsByKeyValue(clientMap, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        result.put(RESULT, clientMap);
        logger.debug("dsB2BClientProfileBrowseListByParamEx end");
        return result;
    }

    @WsMethod(requiredParams = {"PHONENUMBER"})
    public Map<String, Object> dsB2BClientProfileAccountFindByPhone(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileAccountFindByPhone begin");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = new HashMap<String, Object>();
        String phoneNumber = params.get("PHONENUMBER").toString();

        Map<String, Object> clientProfileParams = new HashMap<String, Object>();
        clientProfileParams.put("tel", phoneNumber);
        List<Map<String, Object>> clientProfilesList = dctFindByExample(CLIENT_PROFILE_ENTITY_NAME, clientProfileParams, isCallFromGate);

        List<Map<String, Object>> dualMapsList = makeDualMapsList(clientProfilesList);
        result.put(RESULT, dualMapsList);
        markAllMapsByKeyValue(result, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        logger.debug("dsB2BClientProfileAccountFindByPhone end");
        return result;
    }

    // todo: переписать на dctCrudByHierarchy; try catch c rollback всё равно не имеет смысла - внутри блока неоднократно вызывается commit
    @WsMethod(requiredParams = {"ACCOUNTMAP"})
    public Map<String, Object> dsB2BClientProfileAccountSave(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileAccountSave begin");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> accountMap = (Map<String, Object>) params.get("ACCOUNTMAP");
        Map<String, Object> clientMap = (Map<String, Object>) params.get("CLIENTMAP");
        Boolean toValidate =  getBooleanParam(params,"TOVALIDATE",false);
        Map<String, Object> savedAccountMap = null;
        Map<String, Object> savedClientMap = null;
        Long savedClientID = null;
        Boolean isNewAccount = false;
        boolean isDataValid = toValidate ? validateSaveParams(clientMap) : true;
            if (clientMap != null && isDataValid) {
                if (isCallFromGate) {
                    // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
                    parseDatesBeforeDictionaryCalls(clientMap);
                }
        	savedClientMap = saveClientMap(isCallFromGate, clientMap);
                savedClientID = getLongParam(savedClientMap, "id");
            }
            if (accountMap != null && isDataValid) {
                // "3) Необходимо при создании нового «Аккаунт клиента») выполнять поиск по «Мобильный телефон» существующего «Персональное предложение» и подключение связи (заполнения ссылки «Аккаунт клиента»)"
                // требуется определить выполняется ли создание нового аккаунта
                RowStatus accountRowStatus = getRowStatusLogged(accountMap);
                isNewAccount = INSERTED.equals(accountRowStatus);
                //
            //#14403
            if (isNewAccount) {
            	Date now=new Date();//to midnight?
            	accountMap.put("createDate", now);
            	//if ((accountMap.get("typeId")==null) && (accountMap.get("typeId_EN")==null)) {
            		accountMap.put("typeId",TYPE_HOLDER);
            	//}
            }
            associateAccountMapWithClientMap(savedClientID, accountMap);
            savedAccountMap = saveAccountMap(savedClientID, accountMap);
            }
            if (!isDataValid) {
                savedClientMap = clientMap;
                savedAccountMap = accountMap;
            }
        if (isDataValid && isNewAccount) {
            // "3) Необходимо при создании нового «Аккаунт клиента») выполнять поиск по «Мобильный телефон» существующего «Персональное предложение» и подключение связи (заполнения ссылки «Аккаунт клиента»)"
            updateOfferClientProfileLink(savedAccountMap);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("ACCOUNTMAP", savedAccountMap);
        result.put("CLIENTMAP", savedClientMap);
        // если CLIENTMAP валиден, то добавляем ROWSTATUS
        if (isDataValid) {
            markAllMapsByKeyValue(result, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        }

        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        logger.debug("dsB2BClientProfileAccountSave end");
        return result;
    }

    private Map<String, Object> updateOfferClientProfileLink(Map<String, Object> clientProfile) throws Exception {
        logger.debug("Updating sales campaign offers (if needed)...");
        // "3) Необходимо при создании нового «Аккаунт клиента») выполнять поиск по «Мобильный телефон» существующего «Персональное предложение» и подключение связи (заполнения ссылки «Аккаунт клиента»)"
        Long clientProfileID = getLongParamLogged(clientProfile, "id", "ClientProfile.id");
        String phoneNumber = getStringParamLogged(clientProfile, "tel", "ClientProfile.tel");
        Map<String, Object> salesCampaignOfferParams = new HashMap<String, Object>();
        salesCampaignOfferParams.put("tel", phoneNumber);
        salesCampaignOfferParams.put("clientProfileId", null);
        // "поиск по «Мобильный телефон» существующего «Персональное предложение»"
        List<Map<String, Object>> salesCampaignOfferList = dctFindByExample(SALES_CAMPAIGN_OFFER_ENTITY_NAME, salesCampaignOfferParams);
        if (salesCampaignOfferList != null) {
            logger.debug(String.format("Was found %d sales campaign offer(s) for updating.", salesCampaignOfferList.size()));
            for (Map<String, Object> salesCampaignOffer : salesCampaignOfferList) {
                if (salesCampaignOffer != null) {
                    Long salesCampaignOfferClientProfileID = getLongParam(salesCampaignOffer, "clientProfileId");
                    if (salesCampaignOfferClientProfileID == null) {
                        // не требуется обновлять кампанию продаж
                        Long salesCampaignID = getLongParam(salesCampaignOffer, "salesCampaignId");
                        if (salesCampaignID != null) {
                            //setLinkParamWTF(salesCampaignOffer, "salesCampaignId", salesCampaignID);
                            salesCampaignOffer.put("salesCampaignId", salesCampaignID);
                        }
                        // "подключение связи (заполнения ссылки «Аккаунт клиента»)"
                        //setLinkParamWTF(salesCampaignOffer, "clientProfileId", clientProfileID);
                        salesCampaignOffer.put("clientProfileId", clientProfileID);
                        logger.debug("Updated sales campaign offer: " + salesCampaignOffer);
                        dctUpdate(SALES_CAMPAIGN_OFFER_ENTITY_NAME, salesCampaignOffer);
                    } else if (!salesCampaignOfferClientProfileID.equals(clientProfileID)) {
                        logger.error(String.format(
                                "For phone number '%s' was found sales campaign offer (with id = %d) already linked with some another client profile (with id = %d)! Details (found sales campaign offer): %s",
                                phoneNumber, getLongParam(salesCampaignOffer, "id"), salesCampaignOfferClientProfileID, salesCampaignOffer.toString()
                        ));
                    }
                }
            }
        }
        logger.debug("Updating sales campaign offers finished.");
        return clientProfile;
    }

    @WsMethod(requiredParams = {"ACCOUNTMAP"})
    public Map<String, Object> dsB2BClientProfileAccountSaveWithoutValidation(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileAccountSaveWithoutValidation begin");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> accountMap = (Map<String, Object>) params.get("ACCOUNTMAP");
        Map<String, Object> clientMap = (Map<String, Object>) params.get("CLIENTMAP");
        Map<String, Object> savedAccountMap = null;
        Map<String, Object> savedClientMap = null;
        Long savedClientID = null;
        Boolean isNewAccount = false;
        if (clientMap != null) {
            savedClientMap = saveClientMap(isCallFromGate, clientMap);
            savedClientID = getLongParam(savedClientMap, "id");
        }
        if (accountMap != null) {
            // "3) Необходимо при создании нового «Аккаунт клиента») выполнять поиск по «Мобильный телефон» существующего «Персональное предложение» и подключение связи (заполнения ссылки «Аккаунт клиента»)"
            // требуется определить выполняется ли создание нового аккаунта
            RowStatus accountRowStatus = getRowStatusLogged(accountMap);
            isNewAccount = INSERTED.equals(accountRowStatus);
            // 
            associateAccountMapWithClientMap(savedClientID, accountMap);
            savedAccountMap = saveAccountMap(savedClientID, accountMap);
        }
        if (isNewAccount) {
            // "3) Необходимо при создании нового «Аккаунт клиента») выполнять поиск по «Мобильный телефон» существующего «Персональное предложение» и подключение связи (заполнения ссылки «Аккаунт клиента»)"
            updateOfferClientProfileLink(savedAccountMap);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("ACCOUNTMAP", savedAccountMap);
        result.put("CLIENTMAP", savedClientMap);
        markAllMapsByKeyValue(result, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);

        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        logger.debug("dsB2BClientProfileAccountSaveWithoutValidation end");
        return result;
    }

    private Map<String, Object> saveClientMap(boolean isCallFromGate, Map<String, Object> clientMap) throws Exception {
        clientMap.putAll(getClientNamesMap(clientMap));
        Map<String, Object> savedClientMap = dctCrudByHierarchy(PCLIENT_VER_ENTITY_NAME, clientMap, isCallFromGate);
        markAllMapsByKeyValue(savedClientMap, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        return savedClientMap;
    }

    private Map<String, Object> saveAccountMap(Long savedClientID, Map<String, Object> accountMap) throws Exception {
        Map<String, Object> savedAccountMap = dctCrudByHierarchy(CLIENT_PROFILE_ENTITY_NAME, accountMap);
        savedAccountMap.put("clientId", savedClientID);
        savedAccountMap.remove("clientId_EN");
        markAllMapsByKeyValue(savedAccountMap, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        return savedAccountMap;
    }

    private void associateAccountMapWithClientMap(Long savedClientID, Map<String, Object> accountMap) {
        if (savedClientID == null) {
            return;
        }
        Long accountMapClientID = getLongParamLogged(accountMap, "clientId");
        if ((accountMapClientID == null) || (!accountMapClientID.equals(savedClientID))) {
            Map<String, Object> pseudoClientMap = new HashMap<String, Object>();
            pseudoClientMap.put("id", savedClientID);
            pseudoClientMap.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
            pseudoClientMap.put(DynamicMapInstantiator.KEY, PCLIENT_VER_ENTITY_NAME);
            accountMap.put("clientId_EN", pseudoClientMap);
            markAsModified(accountMap);
        }
    }

    @WsMethod(requiredParams = {"id"})
    public Map<String, Object> dsB2BClientProfileAccountLoad(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileAccountLoad begin");
        boolean isCallFromGate = isCallFromGate(params);
        Long accountId = getLongParamLogged(params, "id");
        Map<String, Object> clientProfile = dctFindById(CLIENT_PROFILE_ENTITY_NAME, accountId, isCallFromGate);
        markAllMapsByKeyValue(clientProfile, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        Map<String, Object> result = makeDualMap(clientProfile);
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        logger.debug("dsB2BClientProfileAccountLoad end");
        return result;
    }

    @WsMethod(requiredParams = {"CLIENTID"})
    public Map<String, Object> dsB2BClientProfileAttachmentDocBrowse(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> browseParams = new HashMap<String, Object>();
        browseParams.put("OBJID", params.get("CLIENTID"));
        Map<String, Object> result = this.callService(Constants.B2BPOSWS, "dsB2BClientProfileAttachment_BinaryFile_BinaryFileBrowseListByParam", browseParams, login, password);
        List<Map<String, Object>> docList = (List<Map<String, Object>>) result.get(RESULT);
        processDocListForUpload(docList, params, login, password);
        return result;
    }

//    private void processDocListForUpload(List<Map<String, Object>> docList, Map<String, Object> params) {
//        if ((params.get("URLPATH") != null) && (params.get("SESSIONIDFORCALL") != null)) {
//            String pathPrefix = params.get("URLPATH").toString() + "?pasid=" + params.get("SESSIONIDFORCALL").toString() + "&fn=";
//            for (Map<String, Object> docItem : docList) {
//                if (docItem.get("FILEPATH") != null) {
//                    String docPath = docItem.get("FILEPATH").toString();
//                    String docName = docPath;
//                    if (docPath.contains("\\")) {
//                        docName = docPath.substring(docPath.indexOf("\\") + 1);
//                    }
//                    if (docName.contains("/")) {
//                        docName = docPath.substring(docPath.indexOf("/") + 1);
//                    }
//                    StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
//
//                    String userDocName = docItem.get("FILENAME").toString();
//                    String fileNameStr;
//                    if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (docItem.get("FSID") != null)) {
//                        fileNameStr = Constants.FS_EXTERNAL + "@" + docItem.get("FSID").toString() + "@" + userDocName;
//                    } else {
//                        fileNameStr = Constants.FS_HARDDRIVE + "@" + docName + "@" + userDocName;
//                    }
//
//                    String filenameEncript = scu.encrypt(fileNameStr + "@" + UUID.randomUUID());
//
//                    docItem.put("DOWNLOADPATH", pathPrefix + filenameEncript);
//                    docItem.remove("FILEPATH");
//                }
//            }
//        }
//    }

    @WsMethod(requiredParams = {"BINFILEID"})
    public void dsB2BClientProfileAttachmentDelete(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("BINFILEID", params.get("BINFILEID"));
        this.callService(Constants.B2BPOSWS, "dsB2BClientProfileAttachment_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
    }

    @WsMethod(requiredParams = {CONTRACT_MAP_PARAMNAME})
    public Map<String, Object> dsB2BClientProfileAccountLoadOrCreateByContractInsurerInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileAccountLoadOrCreateByContractInsurerInfo begin");

        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);

        String errorNote = "";
        Long clientId = null;
        Map<String, Object> clientProfileAccount = null;
        Map<String, Object> insurerMap = null;
        String mobilePhone = "";

        Map<String, Object> contract = getMapParam(params, CONTRACT_MAP_PARAMNAME);
        if (contract != null) {
            // todo: поддержка для INSURERCDMMAP (когда потребуется)
            insurerMap = getMapParam(contract, "INSURERMAP");
            if (insurerMap != null) {
                List<Map<String, Object>> contactList = (List<Map<String, Object>>) insurerMap.get("contactList");
                Map<String, Object> mobilePhoneContact = getItemByFieldStringValues(contactList, "CONTACTTYPESYSNAME", "MobilePhone");
                mobilePhone = getStringParamLogged(mobilePhoneContact, "VALUE", "Mobile phone contact value from CONTRMAP.INSURERMAP.contactList");
                if (mobilePhone.length() > 10) {
                    mobilePhone = mobilePhone.substring(mobilePhone.length() - 10, mobilePhone.length());
                }
            }
        }

        if (mobilePhone.isEmpty()) {
            logger.debug("No mobile phone was found in insurer data from contact - client profile search or create will be skipped!");
        } else {
            List<Map<String, Object>> accountList;
            Map<String, Object> profileParams = new HashMap<String, Object>();
            profileParams.put("PHONENUMBER", mobilePhone);
            accountList = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, "dsB2BClientProfileAccountFindByPhone", profileParams, login, password);
            if (accountList == null) {
                logger.error(String.format(
                        "Unable to get exisiting client profiles by calling dsB2BClientProfileAccountFindByPhone for mobile phone '%s' (see previously logged errors for detals)!",
                        mobilePhone
                ));
                errorNote = "Не удалось проверить наличие существующих профилей клиента - новый профиль не будет создан во избежание дублирования!";
            } else if (accountList.isEmpty()) {
                // профилей не найдено - требуется создание нового
                Map<String, Object> accountMap = new HashMap<String, Object>();
                accountMap.put("tel", mobilePhone);
                // todo: поддержка для INSURERCDMMAP (когда потребуется)
                Map<String, Object> clientMap = getClientMapByCrmParticipantMap(insurerMap, mobilePhone);
                Map<String, Object> newProfileParams = new HashMap<String, Object>();
                newProfileParams.put("CLIENTMAP", clientMap);
                newProfileParams.put("ACCOUNTMAP", accountMap);
                newProfileParams.put("ISCALLFROMGATE", true);
                newProfileParams.put(RETURN_AS_HASH_MAP, true);
                clientProfileAccount = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BClientProfileAccountSaveWithoutValidation", newProfileParams, login, password);
                if (isCallResultOK(clientProfileAccount)) {
                    Map<String, Object> createdClientMap = (Map<String, Object>) clientProfileAccount.get("CLIENTMAP");
                    clientId = getLongParamLogged(createdClientMap, "id", "Created client id");
                }
                if (clientId == null) {
                    logger.error(String.format(
                            "Unable create new client profile by calling dsB2BClientProfileAccountSaveWithoutValidation for mobile phone '%s'!",
                            mobilePhone
                    ));
                    errorNote = "Не удалось создать новый профиль клиента!";
                }
            } else if (accountList.size() == 1) {
                // найден единственный профиль - следует вернуть его данные
                clientProfileAccount = accountList.get(0);
                Map<String, Object> clientMap = (Map<String, Object>) clientProfileAccount.get("CLIENTMAP");
                clientId = getLongParamLogged(clientMap, "id", "Existed client id");
                if (clientId == null) {
                    logger.error(String.format(
                            "Unable create get existing client profile data by calling dsB2BClientProfileAccountFindByPhone for mobile phone '%s'!",
                            mobilePhone
                    ));
                    errorNote = "Не получить сведения найденного существующего профиля клиента!";
                }
            } else {
                // найдено несколько профилей по указанному номеру телефона
                logger.error(String.format(
                        "Multiple client profiles was found by calling dsB2BClientProfileAccountFindByPhone for mobile phone '%s' (see previously logged errors for detals)!",
                        mobilePhone
                ));
                logger.error("Multiple client profiles was found by calling dsB2BClientProfileAccountFindByPhone for mobile phone '%s'!");
                errorNote = "Не удалось проверить наличие существующих профилей клиента - новый профиль не будет создан во избежание дублирования!";
            }
        }

        if ((errorNote.isEmpty()) && ((clientId == null) || (clientProfileAccount == null))) {
            // текст ошибки не указан, однако ИД или мапа аккаунта пусты - непредусмотренная ошибка
            logger.error("Unable to find (or create) client profile by insurer data from contract!");
            errorNote = "Не удалось найти (или создать) профиль клиента по сведениям о страхователе, указанным в договоре!";
        }

        Map<String, Object> result = new HashMap<String, Object>();
        if (errorNote.isEmpty()) {
            result.putAll(clientProfileAccount);
            result.put("PCLIENTID", clientId); // отдельный возврат ИД для прикрепления договора в ЛК
            result.put("CLIENTPROFILEPHONENUMBER", mobilePhone); // отдельный возврат номера телефона для прикрепления договора в ЛК
        } else {
            // возврат ошибки
            result.put(ERROR, errorNote);
        }

        logger.debug("dsB2BClientProfileAccountLoadOrCreateByContractInsurerInfo end");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BClientProfileRawSave(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileRawSave begin");
        Map<String, Object> result = dctCrudByHierarchy(CLIENT_PROFILE_ENTITY_NAME, params);
        markAllMapsByKeyValue(result, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        logger.debug("dsB2BClientProfileRawSave end");
        return result;
    }

    @WsMethod(requiredParams = {"externalId"})
    public Map<String, Object> dsB2BClientProfileFindByExternalId(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileFindByExternalId begin");
        // boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> clientList = null;
        String externalId = getStringParam(params, "externalId");
        if (!externalId.isEmpty()) {
            /*
            // параметры
            Map<String, Object> clientProfileParams = new HashMap<String, Object>();
            Map<String, Object> clientParams = new HashMap<String, Object>();
            clientProfileParams.put("clientId_EN", clientParams);
            clientParams.put("externalId", externalId);
            // вызов поиска
            List<Map<String, Object>> clientProfilesList = dctFindByExample(CLIENT_PROFILE_ENTITY_NAME, clientProfileParams);
            */
            // параметры клиента
            Map<String, Object> clientParams = new HashMap<String, Object>();
            clientParams.put("externalId", externalId);
            // вызов поиска клиента
            clientList = dctFindByExample(PCLIENT_VER_ENTITY_NAME, clientParams);
        }
        // анализ результата - ИД клиента
        Long clientId = null;
        if ((clientList != null) && (!clientList.isEmpty())) {
            Map<String, Object> client = clientList.get(0);
            clientId = getLongParam(client, "id");
        }
        // поиск профиля по ИД клиента
        List<Map<String, Object>> clientProfilesList = null;
        List<Map<String, Object>> contracts = null;
        if (clientId != null) {
            Map<String, Object> clientProfileParams = new HashMap<String, Object>();
            clientProfileParams.put("clientId", clientId);
            clientProfilesList = dctFindByExample(CLIENT_PROFILE_ENTITY_NAME, clientProfileParams);
        }
        // анализ результата
        if ((clientProfilesList != null) && (!clientProfilesList.isEmpty())) {
            Map<String, Object> clientProfile = clientProfilesList.get(0);
            boolean isContracts = getBooleanParam(params, "contracts", false);
            if (isContracts) {
                Map<String, Object> client = getMapParam(clientProfile, "clientId_EN");
                clientId = getLongParam(client, "id");
                if (clientId != null) {
                    HashMap<String, Object> shareContractParams = new HashMap<String, Object>();
                    shareContractParams.put("clientId", clientId);
                    List<Map<String, Object>> shareContractList = dctFindByExample(SHARE_CONTRACT_ENTITY_NAME, shareContractParams);
                    client.put("contracts", shareContractList);
                }
            }
            boolean isAgreements = getBooleanParam(params, "agreements", false);
            if (isAgreements) {
                Long clientProfileId = getLongParam(clientProfile, "id");
                if (clientProfileId != null) {
                    HashMap<String, Object> agreementsParams = new HashMap<String, Object>();
                    // "сохранение соглашений и проверку на их наличие и актуальность следует выполнять с учетом изменения связи соглашений"
                    agreementsParams.put("clientId", clientId);
                    List<Map<String, Object>> agreementsList = dctFindByExample(CLIENT_AGREEMENT_ENTITY_NAME, agreementsParams);
                    clientProfile.put("agreements", agreementsList);
                }
            }
            result.putAll(clientProfile);
        }

        markAllMapsByKeyValue(result, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        /*
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        */
        logger.debug("dsB2BClientProfileFindByExternalId end");
        return result;
    }

    /*
    @WsMethod(requiredParams = {"externalId"})
    public Map<String, Object> dsB2BClientProfileFindByExternalId(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileFindByExternalId begin");
        // boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> clientList = null;
        String externalId = getStringParam(params, "externalId");
        if (!externalId.isEmpty()) {
            // параметры клиента
            Map<String, Object> clientParams = new HashMap<String, Object>();
            clientParams.put("externalId", externalId);
            // вызов поиска клиента
            clientList = dctFindByExample(PCLIENT_VER_ENTITY_NAME, clientParams);
        }
        // анализ результата - ИД клиента
        Long clientId = null;
        if ((clientList != null) && (!clientList.isEmpty())) {
            Map<String, Object> client = clientList.get(0);
            clientId = getLongParam(client, "id");
        }
        // поиск профиля по ИД клиента
        List<Map<String, Object>> clientProfilesList = null;
        List<Map<String, Object>> contracts = null;
        if (clientId != null) {
            Map<String, Object> clientProfileParams = new HashMap<String, Object>();
            clientProfileParams.put("clientId", clientId);
            clientProfilesList = dctFindByExample(CLIENT_PROFILE_ENTITY_NAME, clientProfileParams);
        }
        // анализ результата
        if ((clientProfilesList != null) && (!clientProfilesList.isEmpty())) {
            Map<String, Object> clientProfile = clientProfilesList.get(0);
            boolean isContracts = getBooleanParam(params, "contracts", false);
            if (isContracts) {
                Map<String, Object> client = getMapParam(clientProfile, "clientId_EN");
                clientId = getLongParam(client, "id");
                if (clientId != null) {
                    HashMap<String, Object> shareContractParams = new HashMap<String, Object>();
                    shareContractParams.put("clientId", clientId);
                    List<Map<String, Object>> shareContractList = dctFindByExample(SHARE_CONTRACT_ENTITY_NAME, shareContractParams);
                    client.put("contracts", shareContractList);
                }
            }
            boolean isAgreements = getBooleanParam(params, "agreements", false);
            if (isAgreements) {
                Long clientProfileId = getLongParam(clientProfile, "id");
                if (clientProfileId != null) {
                    HashMap<String, Object> agreementsParams = new HashMap<String, Object>();
                    agreementsParams.put("clientProfileId", clientProfileId);
                    List<Map<String, Object>> agreementsList = dctFindByExample(CLIENT_PROFILE_AGREEMENT_ENTITY_NAME, agreementsParams);
                    clientProfile.put("agreements", agreementsList);
                }
            }
            result.putAll(clientProfile);
        }

        logger.debug("dsB2BClientProfileFindByExternalId end");
        return result;
    }
    */

    @WsMethod(requiredParams = {"CLIENTID"})
    public Map<String, Object> dsB2BClientContractWithExtMapBrowseListByParamsEx(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientContractWithExtMapBrowseListByParamsEx begin");
        // boolean isCallFromGate = isCallFromGate(params);
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> result = new HashMap<String, Object>();

        // !только для отладки!
        // params.put("CLIENTID", 20994L); // testUser
        // !только для отладки!
        // params.put("CLIENTID", 17392L); // NKirgizov

        List<Map<String, Object>> contractList = callServiceAndGetListFromResultMapLogged(
                B2BPOSWS_SERVICE_NAME, "dsB2BClientContractBrowseListByParamEx", params, login, password
        );

        if (contractList != null) {
            for (Map<String, Object> contract : contractList) {
                // загрузка расш. атрибутов
                loadContractExtMap(contract, login, password);
            }
        }

        result.put(RESULT, contractList);
        logger.debug("dsB2BClientContractWithExtMapBrowseListByParamsEx end");
        return result;
    }

    private Map<String, Object> loadContractExtMap(Map<String, Object> contract, String login, String password) {
        Map<String, Object> contractExtMap = null;
        Long contractId = getLongParam(contract, "CONTRID");
        Long hbDataVerId = getLongParam(contract, "PRODCONFHBDATAVERID");
        if ((contractId != null) && (hbDataVerId != null)) {
            Map<String, Object> hbParams = new HashMap<String, Object>();
            hbParams.put("CONTRID", contractId);
            List<Long> hbDataVerIdList = new ArrayList();
            hbDataVerIdList.add(hbDataVerId);
            hbParams.put("HBDATAVERIDLIST", hbDataVerIdList);
            hbParams.put(RETURN_AS_HASH_MAP, true);
            try {
                contractExtMap = callService(
                        Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParamByHBDVIdList", hbParams, logger.isDebugEnabled(), login, password
                );
            } catch (Exception ex) {
                contractExtMap = null;
                logger.error(String.format(
                        "dsHandbookRecordBrowseListByParamByHBDVIdList call with params (%s) caused exception:",
                        hbParams
                ), ex);
            }
        }
        contract.put("CONTREXTMAP", contractExtMap);
        return contractExtMap;
    }

    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BClientContractBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BClientContractBrowseListByParamEx", params);
        return result;
    }

    @WsMethod(requiredParams = {"clientId"})
    public Map<String,Object> dsB2BClientAgreementSave(Map<String, Object> params) throws Exception {
        String agreementSysName = getStringParam(params, "typeAgreementSysName");
        if (!agreementSysName.isEmpty()) {
            Map<String, Object> agreementTypeParams = new HashMap<String, Object>();
            agreementTypeParams.put("sysname", agreementSysName);
            List<Map<String, Object>> agreementTypeList = dctFindByExample(KIND_AGREEMENT_CLIENT_ENTITY_NAME, agreementTypeParams);
            if ((agreementTypeList != null) && (!agreementTypeList.isEmpty())) {
                Map<String, Object> agreementType = agreementTypeList.get(0);
                params.put("typeAgreementId", agreementType.get("id"));
            }
        }
        Object agreementDateObj = params.get("agreementDate");
        if (agreementDateObj != null) {
            Date agreementDate = (Date) parseAnyDate(agreementDateObj, Date.class, "agreementDate");
            params.put("agreementDate", agreementDate);
        }
        Map<String, Object> result = dctCrudByHierarchy(CLIENT_AGREEMENT_ENTITY_NAME, params);
        return result;
    }

    /*@WsMethod(requiredParams = {"clientProfileId"})
    public Map<String,Object> dsB2BClientProfileAgreementSave(Map<String, Object> params) throws Exception {
        String agreementSysName = getStringParam(params, "typeAgreementSysName");
        if (!agreementSysName.isEmpty()) {
            Map<String, Object> agreementTypeParams = new HashMap<String, Object>();
            agreementTypeParams.put("sysname", agreementSysName);
            List<Map<String, Object>> agreementTypeList = dctFindByExample(KIND_AGREEMENT_CLIENT_PROFILE_ENTITY_NAME, agreementTypeParams);
            if ((agreementTypeList != null) && (!agreementTypeList.isEmpty())) {
                Map<String, Object> agreementType = agreementTypeList.get(0);
                params.put("typeAgreementId", agreementType.get("id"));
            }
        }
        Object agreementDateObj = params.get("agreementDate");
        if (agreementDateObj != null) {
            Date agreementDate = (Date) parseAnyDate(agreementDateObj, Date.class, "agreementDate");
            params.put("agreementDate", agreementDate);
        }
        Map<String, Object> result = dctCrudByHierarchy(CLIENT_AGREEMENT_ENTITY_NAME, params);
        return result;
    }*/

/*    @WsMethod(requiredParams = {"clientProfileId"})
    public Map<String,Object> dsB2BClientProfileAgreementLoad(Map<String, Object> params) throws Exception {
        String agreementSysName = getStringParam(params, "typeAgreementSysName");
        if (!agreementSysName.isEmpty()) {
            Map<String, Object> agreementTypeParams = new HashMap<String, Object>();
            agreementTypeParams.put("sysname", agreementSysName);
            List<Map<String, Object>> agreementTypeList = dctFindByExample(KIND_AGREEMENT_CLIENT_PROFILE_ENTITY_NAME, agreementTypeParams);
            if ((agreementTypeList != null) && (!agreementTypeList.isEmpty())) {
                Map<String, Object> agreementType = agreementTypeList.get(0);
                params.put("typeAgreementId", agreementType.get("id"));
            }
        }
        List<Map<String, Object>> agreementList = dctFindByExample(CLIENT_AGREEMENT_ENTITY_NAME, params);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, agreementList);
        return result;
    }*/
    
    @WsMethod(requiredParams =  {"clientProfileId"})
    public void dsClientprofileMakeTransitionFromBlockedToActive(Map<String, Object> params) throws Exception {
        String entityName = CLIENT_PROFILE_ENTITY_NAME;
        Long clientProfileId = getLongParam(params, "clientProfileId"); 
        String transitionSysName =  CLIENTPROFILE_STATUS_TRANSITION_SYSNAME_MAP.get("FROM_BlOCKED_TO_ACTIVE");
        try {
           dctMakeTransition(entityName, clientProfileId, transitionSysName);
        } catch (Exception ex) {
            logger.error("Method makeTransition caused exception: ", ex);
            throw ex;
        }
    }
    
    @WsMethod(requiredParams =  {"clientProfileId"})
    public void dsClientprofileMakeTransitionFromActiveToBlocked(Map<String, Object> params) throws Exception {
        String entityName = CLIENT_PROFILE_ENTITY_NAME;
        Long clientProfileId = getLongParam(params, "clientProfileId"); 
        String transitionSysName =  CLIENTPROFILE_STATUS_TRANSITION_SYSNAME_MAP.get("FROM_ACTIVE_TO_BLOCKED");
        try {
           dctMakeTransition(entityName, clientProfileId,transitionSysName);
        } catch (Exception ex) {
            logger.error("Method makeTransition caused exception: ", ex);
            throw ex;
        }
    }


}
