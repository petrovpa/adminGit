/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.journals;

import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import com.bivgroup.services.validators.impl.ValidatorSessionImpl;
import com.bivgroup.services.validators.interfaces.ServiceCaller;
import com.bivgroup.services.validators.interfaces.ValidatorSession;
import org.apache.cayenne.access.DataContext;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

/**
 * @author kkulkov
 */
@BOName("JournalCustom")
public class B2B_JournalCustomFacade extends ProductContractCustomFacade {

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    public static final String DATAPROVIDERID_FIELDNAME = "DATAPROVID";
    private final DataContext context = null;
    private final Logger logger = Logger.getLogger(this.getClass());

    protected static final String JOURNAL_ERROR_LOGGED = "Подробные сведения о возникшей ошибке сохранены в серверный протокол.";
    protected static final String JOURNAL_ERROR_NO_DATA_PROVIDER_ID = "Не удалось определить метод по получению данных для отображения! " + JOURNAL_ERROR_LOGGED;
    protected static final String JOURNAL_ERROR_CONTENT_BROWSE_FAILED = "Не удалось получить данные для отображения! " + JOURNAL_ERROR_LOGGED;

    @WsMethod(requiredParams = {"SYSNAME"})
    public Map<String, Object> dsB2B_JournalCustomBrowseListByParam(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String journalSysName = params.get("SYSNAME").toString();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("SYSNAME", journalSysName);
        queryParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2B_JournalBrowseListByParam", queryParams, login, password);
        Long journalId = (Long) result.get("ID");
        queryParams.clear();
        queryParams.put("JOURNALID", journalId);
        Map<String, Object> queryResult = this.selectQuery("dsB2B_JournalParamCustomBrowseListByParam", "dsB2B_JournalParamCustomBrowseListByParamCount", queryParams);
        if ((queryResult != null) && (queryResult.get(RESULT) != null) && (((List<Map<String, Object>>) queryResult.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> paramList = (List<Map<String, Object>>) queryResult.get(RESULT);
            linkJournalParamTree(paramList);
            loadHandbookForParam(params, paramList, login, password);
            List<Map<String, Object>> journalParamsTree = buildJournalParamTree(0, 1, paramList);
            result.put("PARAMS", journalParamsTree);
        } else {
            result.put("PARAMS", new ArrayList<Map<String, Object>>());
        }

        queryParams.clear();
        queryParams.put("JOURNALID", journalId);
        queryParams.put(ORDERBY, "T.GROUPNAME, T.SEQUENCE");
        queryResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2B_JournalButtonCustomBrowseListByParam", queryParams, login, password);
        if ((queryResult != null) && (queryResult.get(RESULT) != null) && (((List<Map<String, Object>>) queryResult.get(RESULT)).size() > 0)) {
            // Раскладываем кнопки по мапам и отделяем обработчики событий.
            List<Map<String, Object>> bList = (List<Map<String, Object>>) queryResult.get(RESULT);
            Map<String, Object> events = (Map<String, Object>) result.get("EVENTS");
            if (null == events) {
                events = new HashMap<String, Object>();
                result.put("EVENTS", events);
            }
            Map<String, Object> cButtonMap = (Map<String, Object>) result.get("BUTTONS");
            if (null == cButtonMap) {
                cButtonMap = new HashMap<String, Object>();
                result.put("BUTTONS", cButtonMap);
            }

            for (Map<String, Object> button : bList) {
                if (null != button.get("TYPEBUTTON")) {
                    Long tButton = (Long) button.get("TYPEBUTTON");
                    // раскладываем кнопки как нам надо.
                    switch (tButton.intValue()) {
                        // Тип 100 - обработчики событий, в поле name  имя события.
                        case 100: {
                            if (button.get("NAME") != null) {
                                events.put(button.get("NAME").toString(), button);
                            }

                        }
                        break;
                        default: {
                            String buttonGroupName = (String) button.get("GROUPNAME");
                            if (null != buttonGroupName) {
                                List<Map<String, Object>> buttonGroup = (List<Map<String, Object>>) cButtonMap.get(buttonGroupName);
                                if (null == buttonGroup) {
                                    buttonGroup = new ArrayList<Map<String, Object>>();
                                    cButtonMap.put(buttonGroupName, buttonGroup);
                                }
                                buttonGroup.add(button);
                            }
                        }

                    }
                }
            }
        } else {
            result.put("BUTTONS", new ArrayList<Map<String, Object>>());
            result.put("EVENTS", new HashMap<String, Object>());
        }
        return result;
    }

    private void loadHandbookForParam(Map<String, Object> params, List<Map<String, Object>> paramList, String login, String password) throws Exception {
        for (Map<String, Object> param : paramList) {
            if (null != param.get("HANDBOOKID")) {
                if (null != param.get("HBDATAVERSIONID")) {
                    Long hbDataverId = Long.parseLong(param.get("HBDATAVERSIONID").toString());
                    Map<String, Object> queryParams = new HashMap<String, Object>();
                    queryParams.put("HBDATAVERID", hbDataverId);
                    Map<String, Object> dataRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", queryParams, login, password);
                    if ((dataRes != null) && (dataRes.get(RESULT) != null) && (((List<Map<String, Object>>) dataRes.get(RESULT)).size() > 0)) {
                        param.put("ITEMLIST", dataRes.get(RESULT));
                    } else {
                        param.put("ITEMLIST", new ArrayList<Map<String, Object>>());

                    }
                } else {
                    // заглушка для поля sqldata
                    param.put("ITEMLIST", new ArrayList<Map<String, Object>>());
                }

            }
            if (null != param.get("TYPESYSNAME") && ((param.get("TYPESYSNAME").toString().equalsIgnoreCase("handbook")) || (param.get("TYPESYSNAME").toString().equalsIgnoreCase("autocomplete")))) {
                if (null != param.get("DATAPROVIDERID")) {
                    Long dataProvId = (Long) param.get("DATAPROVIDERID");
                    // Обманываем компилятор.
                    final ProductContractCustomFacade productFacade = this;
                    final String l = login;
                    final String p = password;
                    ValidatorSession session = new ValidatorSessionImpl(this.getDataContext(), new ServiceCaller() {
                        @Override
                        public Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params) throws Exception {
                            // Вызов сервиса с логированием.
                            return productFacade.callService(serviceName, methodName, params, true, l, p);
                        }
                    });
                    try {
                        params.remove("SYSNAME");
                        Map<String, Object> dataProviderResult = session.getMetadataReader().getDataProviderById(dataProvId);
                        logger.debug("dataProviderResult: " + dataProviderResult.toString());
                        Map<String, Object> providerMethodResult = this.callService(dataProviderResult.get("SERVICENAME").toString(),
                                dataProviderResult.get("METHODNAME").toString(), params, login, password);
                        param.put("ITEMLIST", providerMethodResult.get(RESULT));

                    } catch (Exception e) {
                        logger.error(String.format(
                                "[loadHandbookForParam] param handbook dataprovider error {dataProvId: %d, param: %s}: %s",
                                dataProvId, getStringParam(param, "SYSNAME"), e.getLocalizedMessage()
                        ), e);
                    }
                }
            }
        }
    }

    private List<Map<String, Object>> buildJournalParamTree(int itemIndex, int level, List<Map<String, Object>> paramList) throws Exception {
        List<Map<String, Object>> result = new java.util.ArrayList<Map<String, Object>>();
        for (int i = itemIndex; i < paramList.size(); i++) {
            Map<String, Object> journalParam = paramList.get(i);
            int journalParamLevel = Long.valueOf(journalParam.get("LEVEL").toString()).intValue();
            if (journalParamLevel == level) {
                List<Map<String, Object>> subItems = buildJournalParamTree(i + 1, level + 1, paramList);
                if (!subItems.isEmpty()) {
                    journalParam.put("PARAMS", subItems);
                    journalParam.put("hasSubItems", Boolean.TRUE);
                } else {
                    journalParam.put("hasSubItems", Boolean.FALSE);
                }
                result.add(journalParam);
            } else if (journalParamLevel < level) {
                return result;
            }
        }
        return result;
    }

    private void linkJournalParamTree(List<Map<String, Object>> paramList) throws Exception {
        for (Map<String, Object> param : paramList) {
            if (null != param.get("MAINPARAMID")) {
                Long mId = Long.parseLong(param.get("MAINPARAMID").toString());
                Iterator<Map<String, Object>> finderIt = paramList.iterator();
                while (finderIt.hasNext()) {
                    Map<String, Object> findParam = finderIt.next();
                    Long fId = (Long) findParam.get("ID");
                    if (mId.compareTo(fId) == 0) {
                        param.put("MAINPARAMID", findParam);
                    }
                }
            }
        }
    }

    /**
     * // Проверка алиасов и неймспейсов реализованно через данные в базе
     * (во всех фасадах, где используется проверка через статические поля - убрать другую проверку)
     *
     * @param filterParams - параметры фильтрации с интерфейса
     * @param journalId    - номер журнала
     * @return true - если все параметры с интерфейса соответствую параметрам, хранящимся в базе, в противном случае false
     * @throws Exception
     */
    private boolean isValidFilterForJournalParams(Map<String, Object> filterParams, Long journalId) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("JOURNALID", journalId);
        Map<String, Object> queryResult = this.selectQuery("dsB2B_JournalParamCustomBrowseListByParam", "dsB2B_JournalParamCustomBrowseListByParamCount", queryParams);
        List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
        if ((queryResult != null) && (queryResult.get(RESULT) != null) && (((List<Map<String, Object>>) queryResult.get(RESULT)).size() > 0)) {
            paramList = (List<Map<String, Object>>) queryResult.get(RESULT);
        }
        List<String> avaliableJournalTableAliases = new ArrayList<String>();
        List<String> avaliableJournalFields = new ArrayList<String>();
        for (Map<String, Object> param : paramList) {
            if (param.get("TABLEALIAS") != null) {
                String key = param.get("TABLEALIAS").toString();
                if (!avaliableJournalTableAliases.contains(key)) {
                    avaliableJournalTableAliases.add(key);
                }
            }
            if (param.get("NAMESPACE") != null) {
                String key = param.get("NAMESPACE").toString();
                if (!avaliableJournalFields.contains(key)) {
                    avaliableJournalFields.add(key);
                }

            }
        }
        boolean result = true;
        if (filterParams != null) {
            for (Map.Entry<String, Object> entry : filterParams.entrySet()) {
                String key = entry.getKey();
                if (!avaliableJournalTableAliases.contains(key)) {
                    return false;
                } else if (entry.getValue() != null) {
                    Map<String, Object> filterMap = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entryFilterMap : filterMap.entrySet()) {
                        if (entryFilterMap.getValue() != null) {
                            Map<String, Object> fieldMap = (Map<String, Object>) entryFilterMap.getValue();
                            for (Map.Entry<String, Object> entryFieldMap : fieldMap.entrySet()) {
                                String fieldName = entryFieldMap.getKey();
                                if (!avaliableJournalFields.contains(fieldName)) {
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

    // генерация CUSTOMWHERE и др. доп параметров для использования провайдером, если он использует соответствующий SQL запрос
    private Map<String, Object> genCustomParams(Map<String, Object> params, String login, String password) throws Exception {
        parseDates(params, Double.class);
        Map<String, Object> filterParams = getMapParam(params, "FILTERPARAMS");
        if (filterParams.containsKey("EXTERNALALIAS")) {
            params.put("EXTERNALALIAS", filterParams.remove("EXTERNALALIAS"));
        }
        if (isValidFilterForJournalParams(filterParams, getLongParam(params, "JOURNALID"))) {
            Map<String, Object> whereRes = this.callService(Constants.INSTARIFICATORWS,
                    "dsWhereRistrictionGenerate", filterParams, login, password);
            whereRes = getMapParam(whereRes, RESULT);
            String customWhere = getStringParam(whereRes, "customWhereStr");
            // если customWhere не сгенераться, а пирчин этому может быть масса,
            // то запросы будут падать, по этому делаем вот так
            if (customWhere.isEmpty()) {
                customWhere = "1=1";
            }
            Map<String, Object> customWhereParams = getMapParam(whereRes, "customWhereParams");
            if (customWhereParams != null) {
                params.putAll(customWhereParams);
            }
            //Sort field
            if (params.get("sortModel") != null) {
                StringBuilder sb = new StringBuilder();
                List<Map<String, Object>> sortModel = getListParam(params, "sortModel");
                if (!sortModel.isEmpty()) {
                    for (Map<String, Object> aSortModel : sortModel) {
                        if ((aSortModel != null) && (aSortModel.get("colId") != null)
                                && (aSortModel.get("sort") != null)) {
                            sb.append(getStringParam(aSortModel, "colId"))
                                    .append(" ").append(getStringParam(aSortModel, "sort"))
                                    .append(", ");
                        }
                    }
                    if (sb.length() > 1) {
                        sb.delete(sb.length() - 2, sb.length());
                        params.put("ORDERBY", sb.toString());
                    }
                }
            }
            params.put("CUSTOMWHERE", customWhere);
            params.put("CP_TODAYDATE", new Date());
            // добавляем ограничение по агентам только если пользователь не "Сотрудник страховой" и не "Робот"
            Long userTypeId = getLongParam(params, Constants.SESSIONPARAM_USERTYPEID);
            if ((userTypeId == null) || ((userTypeId != 1) && (userTypeId != 4))) {
                params.put("CP_DEPARTMENTID", params.get(Constants.SESSIONPARAM_DEPARTMENTID));
            }
            params.put(WsConstants.LOGIN, login);
            params.put(WsConstants.PASSWORD, password);
            params.put("HIDECHILDCONTR", "TRUE");
            XMLUtil.convertDateToFloat(params);
        }
        return params;
    }

    @WsMethod(requiredParams = {"JOURNALID"})
    public Map<String, Object> dsB2B_JournalButtonCustomBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2B_JournalButtonCustomBrowseListByParam", "dsB2B_JournalButtonCustomBrowseListByParamCount", params);
        return result;
    }

    @WsMethod(requiredParams = {"JOURNALID"})
    public Map<String, Object> dsB2B_JournalDataBrowseListByParamWrapper(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> journalResult = this.dsB2B_JournalDataBrowseListByParam(params);

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> wrapper = new HashMap<>();
        String error = getStringParam(journalResult, "Error");
        if (error.isEmpty()) {
            Long totalCount = getLongParam(journalResult, TOTALCOUNT);
            List<Map<String, Object>> journalListResult = getListParam(journalResult, RESULT);
            wrapper.put("LISTRESULT", journalListResult);
            wrapper.put(TOTALCOUNT, totalCount);
            result.put(RESULT, wrapper);
        } else {
            wrapper.put("Error", error);
            result.put(RESULT, wrapper);
        }

        return result;
    }

    @WsMethod(requiredParams = {"JOURNALID"})
    public Map<String, Object> dsB2B_JournalDataBrowseListByParam(Map<String, Object> params) throws Exception {
        final String login = params.get(WsConstants.LOGIN).toString();
        final String password = params.get(WsConstants.PASSWORD).toString();
        Long journalId = Long.parseLong(params.get("JOURNALID").toString());
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("ID", journalId);
        queryParams.put("ReturnAsHashMap", "TRUE");
        // всегда вычисляем ид Дата провайдера заново.
        Map<String, Object> journal = null;
        if (getLongParam(queryParams.get("ID")) != -1) {
            journal = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2B_JournalBrowseListByParam", queryParams, login, password);
        } else {
            Map<String, Object> sysParam = new HashMap<String, Object>();
            sysParam.put("SETTINGSYSNAME", "HANDBOOKDATAPROVID");
            sysParam.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> sysRes = this.callService(Constants.COREWS, "getSysSettingBySysName", sysParam, login, password);
            if (sysRes.get("SETTINGVALUE") != null) {
                Long dataProvId = getLongParam(sysRes.get("SETTINGVALUE"));
                journal = new HashMap<String, Object>();
                journal.put("DATAPROVIDERID", dataProvId);
            }
        }
        Map<String, Object> result;
        Long dataProvId = getLongParam(journal, "DATAPROVIDERID");
        if (dataProvId == null) {
            /*
            Map<String, Object> result = new HashMap<String, Object>();
            result.put(RESULT, new ArrayList<Map<String, Object>>());
            result.put(TOTALCOUNT, 0L);
            */
            logger.error("[dsB2B_JournalDataBrowseListByParam] Unable to get data provider id (DATAPROVIDERID) for journal content browsing by dsB2B_JournalBrowseListByParam or getSysSettingBySysName! Details (call result): " + journal);
            result = makeErrorResult(JOURNAL_ERROR_NO_DATA_PROVIDER_ID);
        } else {
            queryParams.clear();
            queryParams.put("DATAPROVID", dataProvId);
            queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
            // Обманываем компилятор.
            final ProductContractCustomFacade productFacade = this;
            ValidatorSession session = new ValidatorSessionImpl(this.getDataContext(), new ServiceCaller() {
                @Override
                public Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params) throws Exception {
                    // Вызов сервиса с логированием.
                    return productFacade.callService(serviceName, methodName, params, true, login, password);
                }
            });

            try {
                // генерация CUSTOMWHERE и др. доп параметров для использования провайдером, если он применяет соответствующий SQL запрос
                genCustomParams(params, login, password);
                Map<String, Object> dataProviderResult = session.getMetadataReader().getDataProviderById(dataProvId);
                logger.debug("dataProviderResult: " + dataProviderResult.toString());
                result = this.callService(dataProviderResult.get("SERVICENAME").toString(),
                        dataProviderResult.get("METHODNAME").toString(), params, login, password);
                Object resultObj = result.get(RESULT);
                if (resultObj instanceof Map) {
                    result = (Map<String, Object>) resultObj;
                }
            } catch (Exception e) {
                logger.error(String.format(
                        "[dsB2B_JournalDataBrowseListByParam] journal content dataprovider error {dataProvId: %d, journalID: %s}: %s",
                        dataProvId, journalId, e.getLocalizedMessage()
                ), e);
                // todo: возможно возвращать ошибку, а не пустой список
                /*
                result = new HashMap<String, Object>();
                result.put(RESULT, new ArrayList<Map<String, Object>>());
                result.put(TOTALCOUNT, 0L);
                */
                result = makeErrorResult(JOURNAL_ERROR_CONTENT_BROWSE_FAILED);
            }

            if (isCallFromGate(params)) {
                // преобразование всех дат в String для возврата в интерфейс после выполнения операций через cayenne
                parseDates(result, String.class);
                // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
                parseDatesAfterDictionaryCalls(result);
            }

        }
        return result;
    }

    @WsMethod(requiredParams = {"JOURNALID"})
    public Map<String, Object> dsB2B_JournalDataBrowseListByParamEx(Map<String, Object> params) throws Exception {
        final String login = params.get(WsConstants.LOGIN).toString();
        final String password = params.get(WsConstants.PASSWORD).toString();
        Long journalId = Long.parseLong(params.get("JOURNALID").toString());
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("ID", journalId);
        queryParams.put("ReturnAsHashMap", "TRUE");
        // всегда вычисляем ид Дата провайдера заново.
        Map<String, Object> journal = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2B_JournalBrowseListByParam", queryParams, login, password);
        Map<String, Object> result;
        Long dataProvId = getLongParam(journal, "DATAPROVIDERID");
        if (dataProvId == null) {
            logger.error("[dsB2B_JournalDataBrowseListByParamEx] Unable to get data provider id (DATAPROVIDERID) for journal content browsing by dsB2B_JournalBrowseListByParam! Details (call result): " + journal);
            result = makeErrorResult(JOURNAL_ERROR_NO_DATA_PROVIDER_ID);
        } else {
            queryParams.clear();
            queryParams.put("DATAPROVID", dataProvId);
            queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
            // Обманываем компилятор.
            final ProductContractCustomFacade productFacade = this;
            ValidatorSession session = new ValidatorSessionImpl(this.getDataContext(), new ServiceCaller() {
                @Override
                public Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params) throws Exception {
                    // Вызов сервиса с логированием.
                    return productFacade.callService(serviceName, methodName, params, true, login, password);
                }
            });
            try {
                Map<String, Object> dataProviderResult = session.getMetadataReader().getDataProviderById(dataProvId);
                logger.debug("dataProviderResult: " + dataProviderResult.toString());
                Map<String, Object> providerMethodResult = this.callService(dataProviderResult.get("SERVICENAME").toString(),
                        dataProviderResult.get("METHODNAME").toString(), params, login, password);
                result = providerMethodResult;
            } catch (Exception e) {
                logger.error(String.format(
                        "[dsB2B_JournalDataBrowseListByParamEx] journal content dataprovider error {dataProvId: %d, journalID: %s}: %s",
                        dataProvId, journalId, e.getLocalizedMessage()
                ), e);
                result = makeErrorResult(JOURNAL_ERROR_CONTENT_BROWSE_FAILED);
            }
            /*
            Map<String, Object> result = new HashMap<String, Object>();
            result.put(RESULT, new ArrayList<Map<String, Object>>());
            result.put(TOTALCOUNT, 0L);
            return result;
            */
        }
        return result;
    }

    // Формирование списка возможных состояний.
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2B_ContractJournalLifeGetStateList(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> stateList = new ArrayList<Map<String, Object>>();
        Map<String, Object> cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Черновик");
        cState.put("SYSNAME", "'B2B_CONTRACT_DRAFT'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Предварительная печать (Образец)");
        cState.put("SYSNAME", "'B2B_CONTRACT_PREPRINTING'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Подписан");
        cState.put("SYSNAME", "'B2B_CONTRACT_SG'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Андеррайтинг");
        cState.put("SYSNAME", "'B2B_CONTRACT_UW'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "На подписании");
        cState.put("SYSNAME", "'B2B_CONTRACT_PREPARE'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Отказ");
        cState.put("SYSNAME", "'B2B_CONTRACT_CANCEL'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "На андеррайтинг");
        cState.put("SYSNAME", "'B2B_CONTRACT_PREDUW'");
        stateList.add(cState);
        /*cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "На подписании (После андеррайтинга)");
        cState.put("SYSNAME", "'B2B_CONTRACT_PREPAREFROMUW'");
        stateList.add(cState);     */

        result.put(RESULT, stateList);
        return result;
    }

    // Формирование списка возможных состояний.
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2B_ContractJournalLifeGetStateListForKMSB1(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> stateList = new ArrayList<Map<String, Object>>();
        Map<String, Object> cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Черновик");
        cState.put("SYSNAME", "'B2B_CONTRACT_DRAFT'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Предварительная печать (Образец)");
        cState.put("SYSNAME", "'B2B_CONTRACT_PREPRINTING'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Подписан");
        cState.put("SYSNAME", "'B2B_CONTRACT_SG'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Андеррайтинг");
        cState.put("SYSNAME", "'B2B_CONTRACT_UW'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "На подписании");
        cState.put("SYSNAME", "'B2B_CONTRACT_PREPARE'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Отказ");
        cState.put("SYSNAME", "'B2B_CONTRACT_CANCEL'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "На андеррайтинг");
        cState.put("SYSNAME", "'B2B_CONTRACT_PREDUW'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Действует");
        cState.put("SYSNAME", "'Действует'");
        stateList.add(cState);
        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Проект");
        cState.put("SYSNAME", "'Проект'");
        stateList.add(cState);

        result.put(RESULT, stateList);
        return result;
    }

    // Формирование списка возможных состояний.
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2B_ContractJournalGetStateList(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> stateList = new ArrayList<Map<String, Object>>();
        Map<String, Object> cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Черновик");
        cState.put("SYSNAME", "'B2B_CONTRACT_DRAFT'");
        stateList.add(cState);

        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Подписан");
        cState.put("SYSNAME", "'B2B_CONTRACT_SG'");
        stateList.add(cState);

        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Истек период ожидания оплаты");
        cState.put("SYSNAME", "'B2B_CONTRACT_PAYTIMEOUT'");
        stateList.add(cState);

        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Предварительная печать");
        cState.put("SYSNAME", "'B2B_CONTRACT_PREPRINTING'");
        stateList.add(cState);

        cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Отказ от страхования");
        cState.put("SYSNAME", "'B2B_CONTRACT_REJECT'");
        stateList.add(cState);

        /*cState = new HashMap<String, Object>();
        cState.put("PUBLICNAME", "Выгружен успешно");
        cState.put("SYSNAME", "'B2B_CONTRACT_UPLOADED_SUCCESFULLY'");
        stateList.add(cState);*/
        result.put(RESULT, stateList);
        return result;
    }

}
