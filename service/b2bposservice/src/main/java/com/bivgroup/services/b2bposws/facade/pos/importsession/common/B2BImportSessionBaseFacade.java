package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;

import java.util.*;

import static com.bivgroup.services.b2bposws.facade.B2BPosServiceSessionController.B2B_USERACCOUNTID_PARAMNAME;
import static com.bivgroup.services.b2bposws.system.Constants.ADMINWS;

public class B2BImportSessionBaseFacade extends B2BDictionaryBaseFacade {

    /** B2B_IMPORTSESSION_INLOADQUEUE */
    protected static final String B2B_IMPORTSESSION_INLOADQUEUE = "B2B_IMPORTSESSION_INLOADQUEUE";
    /** B2B_IMPORTSESSION_INPROCESSQUEUE */
    protected static final String B2B_IMPORTSESSION_INPROCESSQUEUE = "B2B_IMPORTSESSION_INPROCESSQUEUE";

    /** B2B_IMPORTSESSION_from_INLOADQUEUE_to_INVALID */
    protected static final String B2B_IMPORTSESSION_FROM_INLOADQUEUE_TO_INVALID = "B2B_IMPORTSESSION_from_INLOADQUEUE_to_INVALID";
    /** B2B_IMPORTSESSION_from_INLOADQUEUE_to_INPROCESSQUEUE */
    protected static final String B2B_IMPORTSESSION_FROM_INLOADQUEUE_TO_INPROCESSQUEUE = "B2B_IMPORTSESSION_from_INLOADQUEUE_to_INPROCESSQUEUE";

    /** B2B_IMPORTSESSION_from_INPROCESSQUEUE_to_ERROR */
    protected static final String B2B_IMPORTSESSION_FROM_INPROCESSQUEUE_TO_ERROR = "B2B_IMPORTSESSION_from_INPROCESSQUEUE_to_ERROR";
    /** B2B_IMPORTSESSION_from_INPROCESSQUEUE_to_SUCCESS */
    protected static final String B2B_IMPORTSESSION_FROM_INPROCESSQUEUE_TO_SUCCESS = "B2B_IMPORTSESSION_from_INPROCESSQUEUE_to_SUCCESS";

    /** B2B_IMPORTSESSION_CNT_from_INVALID_to_INPROCESSQUEUE */
    protected static final String B2B_IMPORTSESSION_CNT_from_INVALID_to_INPROCESSQUEUE = "B2B_IMPORTSESSION_CNT_from_INVALID_to_INPROCESSQUEUE";
    /** B2B_IMPORTSESSION_CNT_from_INVALID_to_SYSBLOCK */
    protected static final String B2B_IMPORTSESSION_CNT_from_INVALID_to_SYSBLOCK = "B2B_IMPORTSESSION_CNT_from_INVALID_to_SYSBLOCK";

    public static final String IMPORT_SESSION_IS_LAST_PARAMNAME = "isLast";

    protected static final String THIS_SERVICE_NAME = B2BPOSWS_SERVICE_NAME;
    protected static final String ADMINWS_SERVICE_NAME = ADMINWS;

    /** DEP_EMPLOYEE.EMPLOYEEID */
    protected static final String EMPLOYEE_ID_PARAMNAME = "EMPLOYEEID";

    /** B2BCONTR.CONTRID */
    protected static final String CONTRACT_ID_PARAMNAME = "CONTRID";
    /** B2BCONTR.CONTRNUMBER */
    protected static final String CONTRACT_NUMBER_PARAMNAME = "CONTRNUMBER";

    protected void genSystemAttributesWithoutAspect(Map<String, Object> entity, Map<String, Object> params) {
        String entityIdFieldName = "id";
        genSystemAttributesWithoutAspect(entity, entityIdFieldName, params);
    }

    protected void genSystemAttributesWithoutAspect(Map<String, Object> entity, String entityIdFieldName, Map<String, Object> params) {
        Long id = getLongParamLogged(entity, entityIdFieldName);
        boolean isIdExisted = (id != null);
        Date nowDate = new Date();
        Long userAccountId = getLongParamLogged(params, B2B_USERACCOUNTID_PARAMNAME);
        if (isIdExisted) {
            entity.put("updateDate", nowDate);
            entity.put("updateUserId", userAccountId);
        } else {
            entity.put("createDate", nowDate);
            entity.put("createUserId", userAccountId);
        }
    }

    protected Set<Long> getLongValuesSetFromList(List<Map<String, Object>> list, String keyName) {
        Set<Long> longValuesSet = new HashSet<>();
        if ((list != null) && (keyName != null) && (!keyName.isEmpty())) {
            for (Map<String, Object> bean : list) {
                Long longValue = getLongParam(bean, keyName);
                if (longValue != null) {
                    longValuesSet.add(longValue);
                }
            }
        }
        return longValuesSet;
    }

    protected void processImportSessionLastFlag(String importSessionEntityName, Map<String, Object> importSessionCreated, Map<String, Object> params) throws Exception {
        Map<String, Object> importSessionParams = new HashMap<>();
        importSessionParams.put(IMPORT_SESSION_IS_LAST_PARAMNAME, BOOLEAN_FLAG_LONG_VALUE_TRUE);
        List<Map<String, Object>> importSessionList = dctFindByExample(importSessionEntityName, importSessionParams);
        for (Map<String, Object> importSessionExisted : importSessionList) {
            importSessionExisted.put(IMPORT_SESSION_IS_LAST_PARAMNAME, BOOLEAN_FLAG_LONG_VALUE_FALSE);
            markAsModified(importSessionExisted);
        }
        List<Map<String, Object>> resultList = dctCrudByHierarchy(importSessionEntityName, importSessionList);
        importSessionCreated.put(IMPORT_SESSION_IS_LAST_PARAMNAME, BOOLEAN_FLAG_LONG_VALUE_TRUE);
    }

    protected String getValidStringParam(Map<String, Object> map, String key, String regExp) {
        String value = getStringParam(map, key);
        if (checkIsValueInvalidByRegExp(value, regExp)) {
            value = "";
        }
        return value;
    }
}
