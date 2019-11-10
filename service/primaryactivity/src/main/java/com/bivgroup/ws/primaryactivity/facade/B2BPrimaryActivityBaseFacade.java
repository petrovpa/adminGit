/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.primaryactivity.facade;

import com.bivgroup.ws.primaryactivity.system.Constants;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author averichevsm
 */
@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("B2BPrimaryActivityBase")
public class B2BPrimaryActivityBaseFacade extends BaseFacade {

    protected static final String[] DEFAULT_CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES = {
        "T",
        "T2"
    };
    protected static final String[] DEFAULT_CUSTOM_WHERE_SUPPORTED_FIELDS = {
        "CONTRNDNUMBER",
        "CONTRNUMBER",
        "DISCRIMINATOR",
        "DOCUMENTDATE",
        "FINISHDATE",
        "MAINACTCONTRID",
        "MAINACTCONTRNODEID",
        "ORGSTRUCTID",
        "STARTDATE",
        "VERNUMBER",
        "DEPTSHORTNAME"
    };
    
    protected static final String CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES_KEY_NAME = "CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES";
    protected static final String CUSTOM_WHERE_SUPPORTED_FIELDS_KEY_NAME = "CUSTOM_WHERE_SUPPORTED_FIELDS";
    
    protected static final String FOR_EXPORT = "FOR_EXPORT";
    
    //<editor-fold defaultstate="collapsed" desc="из B2BBaseFacade">
    // синонимы для констант из RowStatus
    protected static final int ROWSTATUS_UNMODIFIED_ID = RowStatus.UNMODIFIED.getId();
    protected static final int ROWSTATUS_INSERTED_ID = RowStatus.INSERTED.getId();
    protected static final int ROWSTATUS_MODIFIED_ID = RowStatus.MODIFIED.getId();
    protected static final int ROWSTATUS_DELETED_ID = RowStatus.DELETED.getId();
    protected static final String ROWSTATUS_PARAM_NAME = RowStatus.ROWSTATUS_PARAM_NAME;
    
    // Строковый код отдела "Партнеры" в орг. структуре предприятия (все контрагенты являются его дочерними элементами)
    protected static final String PARTNERS_DEPARTMENT_CODE_LIKE = "agencyNetwork";
    // Имя ключа параметра для передачи строкового кода отдела "Партнеры" (PARTNERS_DEPARTMENT_CODE_LIKE) в запросы
    protected static final String PARTNERS_DEPARTMENT_CODE_LIKE_KEY_NAME = "PARENTDEPARTMENTCODELIKE";
    
    /**
     * Рекурсивно устанавливает всем картам объекта marked (включая вложенные)
     * для ключа keyName значение keyValue (например, для массовой установки
     * сущностям статусов ROWSTATUS и FLAG)
     *
     * @param marked   обрабатываемый объект
     * @param keyName  имя ключа, указывающего на устанавливаемое значение
     * @param keyValue устанавливаемое значение
     */
    protected void markAllMapsByKeyValue(Object marked, String keyName, Object keyValue) {
        if (marked == null) {
            return;
        }
        if (marked instanceof List) {
            List<Object> markedList = (List<Object>) marked;
            for (Object row : markedList) {
                markAllMapsByKeyValue(row, keyName, keyValue);
            }
        }
        if (marked instanceof Map) {
            Map<String, Object> markedMap = (Map<String, Object>) marked;
            markedMap.put(keyName, keyValue);
            for (String key : markedMap.keySet()) {
                markAllMapsByKeyValue(markedMap.get(key), keyName, keyValue);
            }
        }
    }
    
    protected Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params, boolean isVerboseLog, String login, String password) throws Exception {
        if (isVerboseLog) {
            return callServiceLogged(serviceName, methodName, params, login, password);
        } else {
            return this.callService(serviceName, methodName, params, login, password);
        }
    }

    protected Map<String, Object> callServiceLogged(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        logger.debug("Вызван метод " + methodName + " с параметрами:\n\n" + params.toString() + "\n");
        // вызов действительного метода
        Map<String, Object> callResult = this.callService(serviceName, methodName, params, login, password);
        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        logger.debug("Метод " + methodName + " выполнился за " + callTimer + " мс. и вернул результат:\n\n" + callResult.toString() + "\n");
        // возврат результата
        return callResult;
    }
    
    protected Object callServiceAndGetOneValue(String serviceName, String methodName, Map<String, Object> params, boolean isVerboseLog, String login, String password, String keyName) throws Exception {
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> rawResult = this.callService(serviceName, methodName, params, isVerboseLog, login, password);
        if (rawResult != null) {
            Object oneValue = rawResult.get(keyName);
            if (oneValue != null) {
                return oneValue;
            }
        }
        // todo: протоколирование неудачи при получении значения по переданному ключу
        return null;        
    }    
    
    protected Object callServiceAndGetOneValue(String serviceName, String methodName, Map<String, Object> params, String login, String password, String keyName) throws Exception {
        boolean isVerboseLog = false;
        return callServiceAndGetOneValue(serviceName, methodName, params, isVerboseLog, login, password, keyName);
    }


    protected static boolean isCallResultOK(Map<String, Object> callResult) {
        return (callResult != null) && (callResult.get("Status") != null) && ("OK".equalsIgnoreCase(callResult.get("Status").toString()));
    }

    protected boolean getBooleanParam(Object bean, Boolean defVal) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Boolean.valueOf(bean.toString()).booleanValue();
        } else {
            return defVal;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="из ProductContractCustomFacade / B2BCustomFacade">
    protected Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else {
            return bean.toString();
        }
    }

    protected Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }

    protected Date getDateParam(Object date) {
        if (date != null) {
            if (date instanceof Double) {
                return XMLUtil.convertDate((Double) date);
            } else if (date instanceof BigDecimal) {
                return XMLUtil.convertDate((BigDecimal) date);
            }
            return (Date) date;
        } else {
            return null;
        }
    }

    /**
     * Вызывает обычную версию selectQuery, передавая в качестве имени второго
     * "количественного" запроса имя фактического запроса прибавив к нему
     * "Count".
     *
     * @param queryName    имя фактического запроса
     * @param selectParams параметры выполнения запроса
     *
     * @return результат, аналогичный получаемому при вызове обычной версии
     *         selectQuery
     *
     * @throws Exception
     */
    protected Map<String, Object> selectQuery(String queryName, Map<String, Object> selectParams) throws Exception {
        return this.selectQuery(queryName, queryName + "Count", selectParams);
    }

    protected void setOverridedParam(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        if (isLogged) {
            Object oldValue = paramParent.get(paramName);
            logParamOverriding(paramName, newValue, oldValue);
        }
        paramParent.put(paramName, newValue);
    }

    protected void setGeneratedParam(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        paramParent.put(paramName, newValue);
        if (isLogged) {
            logParamGeneration(paramName, newValue);
        }
    }

    protected void setGeneratedParamIfNull(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        if (paramParent.get(paramName) == null) {
            setGeneratedParam(paramParent, paramName, newValue, isLogged);
        }
    }

    protected void logParamGeneration(String paramName, Object newValue) {
        StringBuilder logStr = new StringBuilder();
        logStr.append("Значение параметра '");
        logStr.append(paramName);
        logStr.append("' не найдено во входных данных. Для указанного параметра сгенерировано новое значение: ");
        logStr.append(newValue);
        if (newValue != null) {
            logStr.append(" (");
            logStr.append(newValue.getClass().getSimpleName());
            logStr.append(").");
        } else {
            logStr.append(".");
        }
        logger.debug(logStr.toString());
    }

    protected void logParamOverriding(String paramName, Object newValue, Object oldValue) {
        StringBuilder logStr = new StringBuilder();
        logStr.append("Значение параметра '");
        logStr.append(paramName);
        logStr.append("' из входных данных (");
        logStr.append(oldValue);
        logStr.append(") проигонорировано. Для указанного параметра сгенерировано новое значение: ");
        logStr.append(newValue);
        if (newValue != null) {
            logStr.append(" (");
            logStr.append(newValue.getClass().getSimpleName());
            logStr.append(")");
        }
        logStr.append(".");
        logger.debug(logStr.toString());
    }
    
    protected Map<String, Object> recordMakeTrans(Map<String, Object> record, String toStateSysName, String idFieldName, String methodNamePrefix, String typeSysName, String login, String password) throws Exception {
        String toStateNote = null;
        return recordMakeTrans(record, toStateSysName, toStateNote, idFieldName, methodNamePrefix, typeSysName, login, password);
    }
    
    protected Map<String, Object> recordMakeTrans(Map<String, Object> record, String toStateSysName, String toStateNote, String idFieldName, String methodNamePrefix, String typeSysName, String login, String password) throws Exception {
        logger.debug("State transition started...");
        Map<String, Object> result = null;
        logger.debug("    Record type: " + typeSysName);
        Long recordID = getLongParam(record, idFieldName);
        logger.debug("    Record ID: " + recordID);
        String fromStateSysName = getStringParam(record, "STATESYSNAME");
        logger.debug("    Initial state: " + fromStateSysName);
        logger.debug("    Destination state: " + toStateSysName);
        if (toStateNote != null) {
            logger.debug("    Destination state note (optional): " + toStateNote);
        }
        if ((!fromStateSysName.isEmpty()) && (recordID != null)) {
            Map<String, Object> getTransParams = new HashMap<String, Object>();
            getTransParams.put("TYPESYSNAME", typeSysName);
            getTransParams.put("FROMSTATESYSNAME", fromStateSysName);
            getTransParams.put("TOSTATESYSNAME", toStateSysName);
            getTransParams.put("JOIN_TO_SMTYPE", true);
            getTransParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> qTransRes = this.callService(Constants.INSPOSWS, "dsTransitionsBrowseByParamEx", getTransParams, login, password);
            if (qTransRes.get("SYSNAME") != null) {
                String transSysName = getStringParam(qTransRes.get("SYSNAME"));
                if (!transSysName.isEmpty()) {
                    logger.debug("    Selected transition: " + transSysName);
                    Map<String, Object> makeTransParams = new HashMap<String, Object>();
                    makeTransParams.put(idFieldName, recordID);
                    makeTransParams.put("DOCUMENTID", recordID);
                    makeTransParams.put("TYPESYSNAME", typeSysName);
                    makeTransParams.put("TRANSITIONSYSNAME", transSysName);
                    if (toStateNote != null) {
                        makeTransParams.put("NOTE", toStateNote);
                    }
                    makeTransParams.put(RETURN_AS_HASH_MAP, true);
                    result = this.callService(Constants.B2BPOSWS, methodNamePrefix + "_State_MakeTrans", makeTransParams, login, password);
                } else {
                    logger.debug("     No transition selected by this transition params.");
                }
            }
        }
        logger.debug("State transition finished with result: " + result);
        return result;
    }

    protected String getUserUploadFilePath() {
        String result = Config.getConfig("webclient").getParam("userFilePath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();
        return result;
    }

    private boolean isValidFilterParams(Map<String, Object> filterParams, String[] avaliableTableAliases, String[] avaliableFields) {
        logger.debug("Checking table aliases and field names from filter parameters...");
        boolean result = true;
        if (filterParams != null) {
            for (Map.Entry<String, Object> entry : filterParams.entrySet()) {
                String tableAlias = entry.getKey();
                if (!Arrays.asList(avaliableTableAliases).contains(tableAlias)) {
                    logger.error(String.format("Found unsupported table alias - '%s', no data will be queried from DB!", tableAlias));
                    return false;
                } else if (entry.getValue() != null) {
                    Map<String, Object> filterMap = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entryFilterMap : filterMap.entrySet()) {
                        if (entryFilterMap.getValue() != null) {
                            Map<String, Object> fieldMap = (Map<String, Object>) entryFilterMap.getValue();
                            for (Map.Entry<String, Object> entryFieldMap : fieldMap.entrySet()) {
                                String fieldName = entryFieldMap.getKey();
                                if (!Arrays.asList(avaliableFields).contains(fieldName)) {
                                    logger.error(String.format("Found unsupported field name - '%s', no data will be queried from DB!", fieldName));
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.debug("Checking table aliases and field names from filter parameters finished - no unsupported values found.");
        return result;
    }

    // выполнение запроса сведений из БД с генерацией особых условий по переданным из angular-грида параметрам фильтров
    
    protected Map<String, Object> doCustomWhereQuery(String customWhereQueryName, String idFieldName, Map<String, Object> params) throws Exception {

        logger.debug("Generating custom restrictions...");

        // логин и пароль для выполнения запроса и вызова генерации особого условия должны быть переданы после вызова ds-метода через params без изменений
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = null;
        // формирование особого условия на основании переданных параметров фильтров angular-грида
        Map<String, Object> filterParams = (Map<String, Object>) params.get("FILTERPARAMS");
        logger.debug("Filter parameters form interface (FILTERPARAMS): " + filterParams);
        String customWhere = "";
        Map<String, Object> customWhereParams = null;
        
        // возможна передача списков верных алиасов таблиц и имен полей в параметрах вызова
        String[] avaliableTableAliases = (String[]) params.get(CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES_KEY_NAME);
        if ((avaliableTableAliases == null) || (avaliableTableAliases.length == 0)) {
            avaliableTableAliases = DEFAULT_CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES;
        }
        String[] avaliableFields = (String[]) params.get(CUSTOM_WHERE_SUPPORTED_FIELDS_KEY_NAME);
        if ((avaliableFields == null) || (avaliableFields.length == 0)) {
            avaliableFields = DEFAULT_CUSTOM_WHERE_SUPPORTED_FIELDS;
        }
        
        if (isValidFilterParams(filterParams, avaliableTableAliases, avaliableFields)) {
            if ((filterParams != null) && (!filterParams.isEmpty())) {
                Map<String, Object> whereRes = this.callService(Constants.INSTARIFICATORWS, "dsWhereRistrictionGenerate", filterParams, login, password);
                if (whereRes != null) {
                    if (whereRes.get(RESULT) != null) {
                        whereRes = (Map<String, Object>) whereRes.get(RESULT);
                    }
//                    customWhere = getStringParam(whereRes.get("customWhere"));
                    customWhere = getStringParam(whereRes.get("customWhereStr"));
                    customWhereParams = (Map<String, Object>) whereRes.get("customWhereParams");
                    logger.debug("Generated custom 'WHERE' restriction: " + customWhere);
                }
            }

            // параметры для выполнения запроса данных
            Map<String, Object> customParams = new HashMap<String, Object>();
            if (!customWhere.isEmpty()) {
                // дополнение параметров особым сформировнным при вызове dsWhereRistrictionGenerate условием
                customParams.put("CUSTOMWHERE", customWhere);
                if (customWhereParams != null) {
                    customParams.putAll(customWhereParams);
                }                
            } else {
                // если особое условие пустое - это вызов для получения сведений одной записи (при двойном клике на строке грида) по идентификатору
                customParams.put(idFieldName, params.get(idFieldName));
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("No custom 'WHERE' restriction generated - direct id restriction will be used (%s): %d", idFieldName, getLongParam(params.get(idFieldName))));
                }
            }

            // если параметры запроса содержат сложные условия сортировки, то формируется особая строка для параметра ORDERBY
            ArrayList<Map<String, Object>> orderParams = (ArrayList<Map<String, Object>>) params.get("sortModel");
            logger.debug("Sort parameters form interface (sortModel): " + orderParams);
            if ((orderParams != null) && (!orderParams.isEmpty())) {
                StringBuilder customOrder = new StringBuilder();
                for (Map<String, Object> orderParam : orderParams) {
                    String fieldName = getStringParam(orderParam.get("field"));
                    if (fieldName.isEmpty()) {
                        fieldName = getStringParam(orderParam.get("colId"));
                    }
                    if (!fieldName.isEmpty()) {
                        String sortOrder = getStringParam(orderParam.get("sort"));
                        if (sortOrder.isEmpty()) {
                            sortOrder = "ASC";
                        }
                        customOrder.append(fieldName);
                        customOrder.append(" ");
                        customOrder.append(sortOrder);
                        customOrder.append(", ");
                    }
                }
                customOrder.setLength(customOrder.length() - 2);
                String customOrderStr = customOrder.toString();
                customParams.put(ORDERBY, customOrderStr);
                logger.debug("Generated custom 'ORDER BY' rule: " + customOrderStr);
            }

            // параметры для постраничных запросов, фомируются angular-гридом
            customParams.put("PAGE", params.get("PAGE"));
            customParams.put("ROWSCOUNT", params.get("ROWSCOUNT"));
            customParams.put("CP_TODAYDATE", new Date());

            // если параметры запроса еще не содержат особых условий сортировки, то используется переданное в явном виде условие
            if (customParams.get(ORDERBY) == null) {
                String customOrderStr = getStringParam(params.get(ORDERBY));
                if (customOrderStr.isEmpty()) {
                    // по умолчанию - сортировка по дате создания записей основной таблицы
                    customOrderStr = "T.CREATEDATE DESC";
                }
                customParams.put(ORDERBY, customOrderStr);
                logger.debug("Used simple 'ORDER BY' rule: " + customOrderStr);
            }

            logger.debug("Generating custom restrictions finished.");

            logger.debug("Preparing full set of query parameters...");
            // преобразовани дат в Double для работы корректной работы selectQuery
            XMLUtil.convertDateToFloat(customParams);
            // при выполнении запроса требуются также логин и пароль (в selectQuery для работы с аспектами могут вызываться дополнительные методы)
            customParams.put(WsConstants.LOGIN, login);
            customParams.put(WsConstants.PASSWORD, password);
            // выполнение запроса
            logger.debug("Prepared full set of query parameters: " + customParams);
            logger.debug("Performing " + customWhereQueryName + " query...");
            result = this.selectQuery(customWhereQueryName, customParams);
            logger.debug("Query result: " + result);
        }

        // возврат результата запроса
        return result;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="созданы в B2BPrimaryActivityBaseFacade">
    protected Long getLongParam(Map<String, Object> map, String keyName) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParam(map.get(keyName));
        }
        return longParam;
    }
    
    protected String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }
    
    protected Integer getIntegerParam(Object bean, Integer defaultValue) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Integer.valueOf(bean.toString());
        } else {
            return defaultValue;
        }
    }

    protected Integer getIntegerParam(Object bean) {
        return getIntegerParam(bean, 0);
    }
    
    protected boolean getBooleanParam(Map<String, Object> map, String keyName, Boolean defaultValue) {
        Boolean booleanParam = defaultValue;
        if (map != null) {
            booleanParam = getBooleanParam(map.get(keyName), defaultValue);
        }
        return booleanParam;
    }
    
    // аналог getLongParam, но с протоколировнием полученного значения
    protected Long getLongParamLogged(Map<String, Object> map, String keyName) {
        Long paramValue = getLongParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    // аналог getStringParam, но с протоколировнием полученного значения
    protected String getStringParamLogged(Map<String, Object> map, String keyName) {
        String paramValue = getStringParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    // Определение типа модификации сущности entity (если тип модификации не указан - считается, что сущность создается)
    protected Integer getRowStatusOrSetToInsertedIfNull(Map<String, Object> entity) {
        Object rowStatusObj = entity.get(ROWSTATUS_PARAM_NAME);
        Integer rowStatus;
        if (rowStatusObj != null) {
            rowStatus = getIntegerParam(rowStatusObj);
        } else {
            entity.put(ROWSTATUS_PARAM_NAME, ROWSTATUS_INSERTED_ID);
            rowStatus = ROWSTATUS_INSERTED_ID;
        }
        return rowStatus;
    }
    
    public List<Map<String, Object>> callServiceAndGetListFromResultMap(String serviceName, String methodName, Map<String, Object> params, boolean isVerboseLog, String login, String password) throws Exception {
        return WsUtils.getListFromResultMap(this.callService(serviceName, methodName, params, isVerboseLog, login, password));
    }
    
    public List<Map<String, Object>> callServiceAndGetListFromResultMap(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        boolean isVerboseLog = false;
        return callServiceAndGetListFromResultMap(serviceName, methodName, params, isVerboseLog, login, password);
    }
    
    // получение из CORE_SETTINGS значения конкретного параметра по его системному имени
    protected String getCoreSettingBySysName(String settingSysName, String login, String password) throws Exception {
        logger.debug(String.format("Getting core setting by system name [%s]...", settingSysName));
        Map<String, Object> coreSettingParams = new HashMap<String, Object>();
        coreSettingParams.put("SETTINGSYSNAME", settingSysName);
        coreSettingParams.put(RETURN_AS_HASH_MAP, "TRUE"); // getSysSettingBySysName работает только со строковыми значениями RETURN_AS_HASH_MAP
        String coreSettingValue = "";
        Map<String, Object> coreSetting = this.callService(COREWS, "getSysSettingBySysName", coreSettingParams, login, password);
        if (coreSetting != null) {
            coreSettingValue = getStringParam(coreSetting.get("SETTINGVALUE"));
            if (coreSettingValue.isEmpty()) {
                logger.debug(String.format("Core setting with system name [%s] does not exist or contain no value.", settingSysName));
            } else {
                logger.debug(String.format("Core setting with system name [%s] contain value [%s].", settingSysName, coreSettingValue));
            }
        } else {
            logger.debug("Method getSysSettingBySysName return no result.");
        }
        return coreSettingValue;
    }
    
    //</editor-fold>
    
}
