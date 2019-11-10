/*
* Copyright (c) Diasoft 2004-2013
 */
package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import ru.diasoft.services.inscore.facade.BaseFacade;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.system.annotations.BOName;

/**
 * Custom-фасад для сущности B2BProductPossibleValue
 *
 * @author reson
 */
@BOName("B2BProductPossibleValueCustom")
public class B2BProductPossibleValueCustomFacade extends B2BBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());
    public static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    // мапа соответствия доступнтых идентификаторов типа данных показателя окончаниям имен полей, используемых для хранения допустимых значений строковым описаниям (ключ - DATATYPEID, значение - окончание имени поля)
    private Map<Long, String> possValueFieldNameEndingMap = null;

    public B2BProductPossibleValueCustomFacade() {
        super();
        initRuleMaps();
    }

    private void initRuleMaps() {
        if (possValueFieldNameEndingMap == null) {
            initPossValueFieldNameEndingMap();
        }
    }

    private void initPossValueFieldNameEndingMap() {
        // инициализация мапы соответствия доступнтых идентификаторов типа данных показателя их строковым описаниям (ключ - DATATYPEID, значение - DATATYPESTR)
        // (если конкретного значения DATATYPEID нет в мапе, значит это некорректный/неподдерживаемый идентификатор типа данных)
        possValueFieldNameEndingMap = new HashMap<Long, String>();
        possValueFieldNameEndingMap.put(1L, "STRING"); // String
        possValueFieldNameEndingMap.put(2L, "LONG"); // Long
        possValueFieldNameEndingMap.put(3L, "DOUBLE"); // Double
        possValueFieldNameEndingMap.put(4L, "DOUBLE"); // Date
        possValueFieldNameEndingMap.put(5L, "LONG"); // Boolean
        possValueFieldNameEndingMap.put(6L, "LONG"); // ссылка
        logger.debug("Values data types rule map: " + possValueFieldNameEndingMap);
    }

    private void copyValuesByKeys(Map<String, Object> toMap, Map<String, Object> fromMap, String... keyNames) {
        if ((toMap != null) && (fromMap != null) && (keyNames != null)) {
            for (String keyName : keyNames) {
                Object value = fromMap.get(keyName);
                toMap.put(keyName, value);
            }
        }
    }

    private Map<String, Object> newMapWithCopiedValuesByKeys(Map<String, Object> fromMap, String... keyNames) {
        Map<String, Object> newMap = new HashMap<String, Object>();
        copyValuesByKeys(newMap, fromMap, keyNames);
        return newMap;
    }

    /**
     * Создать объект с выбором записываемых полей по типу данных показателя
     *
     * @author reson
     * @param params
     *               <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * <LI>ISDEFAULT - Признак значение по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVALUEID - ИД показателя</LI>
     * <LI>DATATYPEID - Тип данных показателя</LI>
     * <LI>VALUE2 - Значение 2 (если указан DATATYPEID)</LI>
     * <LI>VALUE2DOUBLE - Дробное значение 2</LI>
     * <LI>VALUE2LONG - Целое значение 2</LI>
     * <LI>VALUE2STRING - Строковое значение 2</LI>
     * <LI>VALUE - Значение 1 (если указан DATATYPEID)</LI>
     * <LI>VALUEDOUBLE - Дробное значение 1</LI>
     * <LI>VALUELONG - Целое значение 1</LI>
     * <LI>VALUESTRING - Строковое значение 1</LI>
     * </UL>
     *
     * @return
     *         <UL>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductPossibleValueCreateEx(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> possValueParams = preparePossValueParams(params);
        possValueParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductPossibleValueCreate", possValueParams, login, password);
        return result;
    }

    private Map<String, Object> preparePossValueParams(Map<String, Object> params) {
        Map<String, Object> possValueParams = newMapWithCopiedValuesByKeys(params, "HINT", "PRODPOSSVALUEID", "ISDEFAULT", "NAME", "PRODVALUEID");
        Long dataTypeID = getLongParam(params, "DATATYPEID");
        if (dataTypeID == null) {
            Long prodValueID = getLongParam(params, "PRODVALUEID");
            if (prodValueID != null) {
                // todo: запрос типа данных показателя из БД (в дальнейшем, возможно, может потребоваться в некоторых случах)
            }
        }
        Object value = params.get("VALUE");
        Object value2 = params.get("VALUE2");
        boolean isSingleValuesPairMode = false;
        if ((dataTypeID != null) && ((value != null) || (value2 != null))) {
            String possValueFieldNameEnding = possValueFieldNameEndingMap.get(dataTypeID);
            if (possValueFieldNameEnding != null) {
                possValueParams.put("VALUE" + possValueFieldNameEnding, value);
                possValueParams.put("VALUE2" + possValueFieldNameEnding, value2);
                isSingleValuesPairMode = true;
            }
        }
        if (!isSingleValuesPairMode) {
            copyValuesByKeys(possValueParams, params, "VALUEDOUBLE", "VALUELONG", "VALUESTRING", "VALUE2DOUBLE", "VALUE2LONG", "VALUE2STRING");
        }        
        return possValueParams;
    }

    /**
     * Изменить объект с выбором записываемых полей по типу данных показателя
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * <LI>ISDEFAULT - Признак значение по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVALUEID - ИД показателя</LI>
     * <LI>DATATYPEID - Тип данных показателя</LI>
     * <LI>VALUE2 - Значение 2 (если указан DATATYPEID)</LI>
     * <LI>VALUE2DOUBLE - Дробное значение 2</LI>
     * <LI>VALUE2LONG - Целое значение 2</LI>
     * <LI>VALUE2STRING - Строковое значение 2</LI>
     * <LI>VALUE - Значение 1 (если указан DATATYPEID)</LI>
     * <LI>VALUEDOUBLE - Дробное значение 1</LI>
     * <LI>VALUELONG - Целое значение 1</LI>
     * <LI>VALUESTRING - Строковое значение 1</LI>
     * </UL>
     *
     * @return
     * <UL>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPOSSVALUEID"})
    public Map<String, Object> dsB2BProductPossibleValueUpdateEx(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> possValueParams = preparePossValueParams(params);
        possValueParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductPossibleValueUpdate", possValueParams, login, password);
        return result;

    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * <LI>ISDEFAULT - Признак значение по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVALUEID - ИД показателя</LI>
     * <LI>VALUE2DOUBLE - Дробное значение</LI>
     * <LI>VALUE2LONG - Целое значение</LI>
     * <LI>VALUE2STRING - Строковое значение</LI>
     * <LI>VALUEDOUBLE - Дробное значение</LI>
     * <LI>VALUELONG - Целое значение</LI>
     * <LI>VALUESTRING - Строковое значение</LI>
     * </UL>
     *
     * @return
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * <LI>ISDEFAULT - Признак значение по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVALUEID - ИД показателя</LI>
     * <LI>VALUE2DOUBLE - Дробное значение</LI>
     * <LI>VALUE2LONG - Целое значение</LI>
     * <LI>VALUE2STRING - Строковое значение</LI>
     * <LI>VALUEDOUBLE - Дробное значение</LI>
     * <LI>VALUELONG - Целое значение</LI>
     * <LI>VALUESTRING - Строковое значение</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductPossibleValueBrowseListByParamEx(Map<String, Object> params) throws Exception {
        
        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Long dataTypeID = getLongParam(params, "DATATYPEID");

        Map<String, Object> possValueParams = preparePossValueParams(params);
        List<Map<String, Object>> possValuesList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BProductPossibleValueBrowseListByParam", possValueParams, login, password);

        if ((possValuesList != null) && (possValuesList.size() > 0) && (dataTypeID != null)) {
            String possValueFieldNameEnding = possValueFieldNameEndingMap.get(dataTypeID);
            if (possValueFieldNameEnding != null) {
                for (Map<String, Object> possValue : possValuesList) {
                    Object value = possValue.get("VALUE" + possValueFieldNameEnding);
                    Object value2 = possValue.get("VALUE2" + possValueFieldNameEnding);
                    possValue.put("VALUE", value);
                    possValue.put("VALUE2", value2);
                    possValue.put("DATATYPEID", dataTypeID);
                }
            }
        }
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, possValuesList);
        return result;
    }

}
