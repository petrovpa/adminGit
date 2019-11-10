package com.bivgroup.services.b2bposws.facade.pos.other.custom;

import com.bivgroup.services.b2bposws.facade.pos.contract.custom.B2BContractWithRightsCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.io.File;
import com.bivgroup.services.b2bposws.system.JenkinsHash;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

/**
 *
 * @author deathstalker
 */
@Auth(onlyCreatorAccess = false)
@BOName("MessageCustom")
public class MessageCustomFacade extends ProductContractCustomFacade {
    private static final JenkinsHash JENKINS_HASH = new JenkinsHash();

    public static final String[] avaliableTableAliasesLocal = B2BContractWithRightsCustomFacade.avaliableTableAliases;
    public static final String[] avaliableFieldsLocal = B2BContractWithRightsCustomFacade.avaliableFields;

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    public static final String SERVICE_NAME = Constants.B2BPOSWS;



    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @param params <UL>
     *               <LI>CHNOTIFICATIONID - ИД заяления по страховому событию</LI>
     *               <LI>CREATEDATE - Дата создания</LI>
     *               <LI>CREATEUSERID - Пользователь создавший сообщение</LI>
     *               <LI>EID - ИД сущности</LI>
     *               <LI>ID - ИД сообщения</LI>
     *               <LI>ISFAVORITE - Признак Избранный</LI>
     *               <LI>ISUNREAD - Признак Прочитан</LI>
     *               <LI>NOTE - Описание</LI>
     *               <LI>PDDECLARATIONID - ИД заявления по договору</LI>
     *               <LI>SENDERID - ИД отправителя</LI>
     *               <LI>TYPEMESSAGE - Тип сообщения</LI>
     *               <LI>UPDATEDATE - Дата изменения</LI>
     *               <LI>UPDATEUSERID - Пользователь изменивший сообщение</LI>
     *               <LI>TITLE - Заголовок письма</LI>
     *               <LI>IDENTIFIERMSG - Идентификатор письма</LI>
     *               <LI>TITLEHASH - хэш-код темы сообщения</LI>
     *               </UL>
     * @return <UL>
     * <LI>CHNOTIFICATIONID - ИД заяления по страховому событию</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший сообщение</LI>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД сообщения</LI>
     * <LI>ISFAVORITE - Признак Избранный</LI>
     * <LI>ISUNREAD - Признак Прочитан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PDDECLARATIONID - ИД заявления по договору</LI>
     * <LI>SENDERID - ИД отправителя</LI>
     * <LI>TYPEMESSAGE - Тип сообщения</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший сообщение</LI>
     * <LI>TITLE - Заголовок письма</LI>
     * <LI>IDENTIFIERMSG - Идентификатор письма</LI>
     * <LI>TITLEHASH - хэш-код темы сообщения</LI>
     * </UL>
     * @throws java.lang.Exception
     * @author reson
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMessageBrowseListByParamEx(Map<String, Object> params) throws Exception {
        // генерируем хэш код для фильтрации
        boolean isNeedFilterByHashCode = getBooleanParam(params, "filterByHashCode", false);
        String title = getStringParam(params, "TITLE");
        if (isNeedFilterByHashCode && !title.isEmpty()) {
            params.put("TITLEHASH", JENKINS_HASH.hash64(title.getBytes("UTF-8")));
            // удаляем тему из параметров запроса, т.к. фильтровать будем по хэш-коду
            params.remove("TITLE");
        }
        Map<String, Object> result = this.selectQuery("dsMessageBrowseListByParamEx", null, params);
        setImportantTypeMessage(result);
        // дополнительно отбрасываем те темы, которые не равны по значению, но равны по хэшу
        if (isNeedFilterByHashCode && !title.isEmpty()) {
            filterMessageListByTitle(title, result);
        }
        return result;
    }

    /**
     * Метод фильтрации результата запроса по теме
     *
     * @param filteredTitle Тема, по которой требуется отфильтровать
     * @param resultMap     результирующая мапа запроса, в которой лежит список сообщений
     */
    private void filterMessageListByTitle(String filteredTitle, Map<String, Object> resultMap) {
        List<Map<String, Object>> setterImportantMap = getListFromResultMap(resultMap);
        if (setterImportantMap != null) {
            String title;
            List<Map<String, Object>> filteredList = new ArrayList<>();
            for (Map<String, Object> item : setterImportantMap) {
                title = getStringParam(item, "TITLE");
                if (title.equals(filteredTitle)) {
                    filteredList.add(item);
                }
            }
            resultMap.put(RESULT, filteredList);
        }
    }

    /**
     * Изменить объект
     *
     * @param params <UL>
     *               <LI>CHNOTIFICATIONID - ИД заяления по страховому событию</LI>
     *               <LI>CREATEDATE - Дата создания</LI>
     *               <LI>CREATEUSERID - Пользователь создавший сообщение</LI>
     *               <LI>EID - ИД сущности</LI>
     *               <LI>ID - ИД сообщения</LI>
     *               <LI>ISFAVORITE - Признак Избранный</LI>
     *               <LI>ISUNREAD - Признак Прочитан</LI>
     *               <LI>NOTE - Описание</LI>
     *               <LI>PDDECLARATIONID - ИД заявления по договору</LI>
     *               <LI>SENDERID - ИД отправителя</LI>
     *               <LI>TYPEMESSAGE - Тип сообщения</LI>
     *               <LI>UPDATEDATE - Дата изменения</LI>
     *               <LI>UPDATEUSERID - Пользователь изменивший сообщение</LI>
     *               <LI>TITLEHASH - хэш-код темы сообщения</LI>
     *               </UL>
     * @return <UL>
     * <LI>ID - ИД сообщения</LI>
     * </UL>
     * @author ershov
     */
    @WsMethod()
    public Map<String, Object> dsMessageUpdateEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsMessageUpdateEx", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Создать объект с генерацией id
     *
     * @param params <UL>
     *               <LI>CHNOTIFICATIONID - ИД заяления по страховому событию</LI>
     *               <LI>CREATEDATE - Дата создания</LI>
     *               <LI>CREATEUSERID - Пользователь создавший сообщение</LI>
     *               <LI>EID - ИД сущности</LI>
     *               <LI>ID - ИД сообщения</LI>
     *               <LI>ISFAVORITE - Признак Избранный</LI>
     *               <LI>ISUNREAD - Признак Прочитан</LI>
     *               <LI>NOTE - Описание</LI>
     *               <LI>TITLE - Заголовок</LI>
     *               <LI>IDENTIFIERMSG - Идентификатор сообщения</LI>
     *               <LI>PDDECLARATIONID - ИД заявления по договору</LI>
     *               <LI>SENDERID - ИД отправителя</LI>
     *               <LI>TYPEMESSAGE - Тип сообщения</LI>
     *               <LI>UPDATEDATE - Дата изменения</LI>
     *               <LI>UPDATEUSERID - Пользователь изменивший сообщение</LI>
     *               <LI>TITLEHASH - хэш-код темы сообщения</LI>
     *               </UL>
     * @return <UL>
     * <LI>ID - ИД сообщения</LI>
     * </UL>
     * @author ARazumovskiy
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMessageExCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsMessageExInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }

  /**
     * Метод установки сообщению признака "Сообщение прочитано"
     *
     * @param params Ожидается свойство "CLIENTPROFILEID" для чтения сообщений
     * клиентов личного кабинета.
     * @return Возвращает статус и количество прочитанных сообщений
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CLIENTPROFILEID"})
    public Map<String, Object> dsB2BMessageSetIsRead(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();

        // Параметры сообщения
        Map<String, Object> messageParams = new HashMap<>();
        messageParams.putAll(params);
        
        int countUpdateRows[] = this.updateQuery("dsB2BMessageSetIsRead", messageParams);

        result.put("totalCountUpdateRows", countUpdateRows[0]);
        result.put("STATUS", "OK");
        
        return result;
    }

    /**
     * Пока в базе сообщения не хранят тип важно или нет, делаем заглушку.
     * Пробегаемся по всем сообщениям и говорим, что они не важные.
     *
     * @param result Ответ. Структура сообщений из базы.
     */
    private void setImportantTypeMessage(Map<String, Object> result) {
        List<Map<String, Object>> setterImportantMap = (List<Map<String, Object>>) result.get("Result");
        for (Map<String, Object> list : setterImportantMap) {
            list.put("isImportantMessage", false);
        }
    }

    private boolean isValidFilterParams(Map<String, Object> filterParams) {
        boolean result = true;
        if (filterParams != null) {
            for (Map.Entry<String, Object> entry : filterParams.entrySet()) {
                String key = entry.getKey();
                if (!Arrays.asList(avaliableTableAliasesLocal).contains(key)) {
                    return false;
                } else if (entry.getValue() != null) {
                    Map<String, Object> filterMap = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entryFilterMap : filterMap.entrySet()) {
                        if (entryFilterMap.getValue() != null) {
                            Map<String, Object> fieldMap = (Map<String, Object>) entryFilterMap.getValue();
                            for (Map.Entry<String, Object> entryFieldMap : fieldMap.entrySet()) {
                                String fieldName = entryFieldMap.getKey();
                                if (!Arrays.asList(avaliableFieldsLocal).contains(fieldName)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMessageLastBrowseListByParamEx(Map<String, Object> params) throws Exception {

        Map<String, Object> customParams = params;

        Map<String, Object> result = null;
        parseDates(params, Double.class);

            XMLUtil.convertDateToFloat(customParams);
            result = this.selectQuery("dsMessageLastBrowseListByParamEx", "dsMessageLastBrowseListByParamExCount", customParams);

        return result;
    }

    @WsMethod(requiredParams = {"NOTE"})
    public Map<String, Object> dsMessageUserCreate(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();

        // создать senderid
        Map<String, Object> senderParams = new HashMap<String, Object>();
        senderParams.put("USERID", params.get("SESSION_USERACCOUNTID")); //SESSIONIDFORCALL
        senderParams.put("CLIENTPROFILEID", params.get("CLIENTPROFILEID")); //SESSIONIDFORCALL
        senderParams.put("CHANNELID", 1);
        Long senderId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsMessageCorrespondentCreate", senderParams, login, password, "ID"));
        if (senderId == null) {
            result.put("Error", "message: error create sender");
            return result;
        }

        // получить идентификатор сообщения
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("SYSTEMBRIEF", "messageIdentifierAutoNum");
        String identifierMsg = null;
        Map res1 = this.callService(COREWS, "dsNumberFindByMask", reqParams, login, password);
        if (res1 != null && res1.containsKey(RESULT)) {
            identifierMsg = (String) res1.get(RESULT);
        }

        // создать сообщение
        Map<String, Object> messageParams = new HashMap<String, Object>();
        messageParams.putAll(params);
        messageParams.put("SENDERID", senderId);
        messageParams.put("TYPEMESSAGE", 0);
        messageParams.put("IDENTIFIERMSG", identifierMsg);
        // генерим хэш-код темы сообщения
        String title = getStringParam(messageParams, "TITLE");
        // если тема пуста, тогда хэш не генерим
        if (!title.isEmpty()) {
            messageParams.put("TITLEHASH", JENKINS_HASH.hash64(title.getBytes("UTF-8")));
        }

        Long messageId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsMessageCreate", messageParams, login, password, "ID"));
        if (messageId == null) {
            result.put("Error", "message: error create message");
            return result;
        }
        // проверка получателя сообщения
        Long recipientClientProfileId = getLongParam(params.get("RECIPIENTCLIENTPROFILEID"));
        if (recipientClientProfileId == null) {
            result.put("ID", messageId);
            return result;
        }
        // создать получателя сообщения
        Map<String, Object> recipientParams = new HashMap<String, Object>();
        recipientParams.put("CLIENTPROFILEID", recipientClientProfileId);
        recipientParams.put("CHANNELID", 1);
        recipientParams.put("MESSAGEID", messageId);
        Long recipientId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsMessageRecipientCreate", recipientParams, login, password, "ID"));
        if (recipientId == null) {
            result.put("Error", "message: error create recipient message");
            return result;
        }
        result.put("ID", messageId);
        result.put("PDDECLARATIONID", params.get("PDDECLARATIONID")); // для получения ссылки на заявление при отказе в расторжении
        return result;
    }

    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageDocumentBrowseListByParam(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> browseParams = new HashMap<String, Object>();
        browseParams.put("OBJID", params.get("ID"));
        Map<String, Object> result = this.callService(Constants.B2BPOSWS, "dsMessage_BinaryFile_BinaryFileBrowseListByParam", browseParams, login, password);
        List<Map<String, Object>> docList = (List<Map<String, Object>>) result.get(RESULT);
        processDocListForUpload(docList, params, login, password);
        return result;
    }

    /*
    // здесь более не требуется - будет доступно за счет B2BBaseFacade implements SeaweedsGetters
    protected String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }
    */

}
