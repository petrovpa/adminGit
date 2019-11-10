/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.api.system;

/**
 * @author mmamaev
 */
public class ParamConstants {

    public static final String WS_PRODUCT_SYSNAME_PARAMNAME = "product";
    public static final String WS_DATA_SYSNAME_PARAMNAME = "data";
    public static final String WS_CONTRACT_PARAMNAME = "contract";
    public static final String WS_PAY_DOC_PARAMNAME = "payDocument";
    public static final String WS_PRODUCT_URL_PARAMNAME = "url";

    public static final String HASH_INFO_MAP_PARAMNAME_ENDING = "HASHMAP";
    public static final String HASH_INFO_MAP_ID_PARAMNAME = "id";
    public static final String HASH_INFO_MAP_VALID_PARAMNAME = "isCorrect";

    /** имя параметра, содержащего хеш договора (по новому алгоритму, из openapi), при вызовах из онлайн интерфейсов */
    public static final String ONLINE_CONTRACT_ID_PARAMNAME = "contractId";

    /** имя параметра, содержащего хеш договора (по старому алгоритму, который используется во всех стандартных онлайн-методах), при вызовах из онлайн интерфейсов */
    public static final String ONLINE_CONTRACT_OLD_HASH_PARAMNAME = "HASH";

    /** имя параметра, содержащего хеш расчета, при вызовах из онлайн интерфейсов */
    public static final String ONLINE_CALCULATE_ID_PARAMNAME = "calculateId";

    public static final String WS_CONTRACT_HASH_INFO_MAP_PARAMNAME = "CONTRACT" + HASH_INFO_MAP_PARAMNAME_ENDING;
    public static final String WS_CONTRACT_HASH_INFO_MAP_ID_PARAMNAME = HASH_INFO_MAP_ID_PARAMNAME;
    public static final String WS_CONTRACT_HASH_INFO_MAP_VALID_PARAMNAME = HASH_INFO_MAP_VALID_PARAMNAME;

    public static final String WS_OBJECT_HASH_INFO_MAP_PARAMNAME = "OBJECT" + HASH_INFO_MAP_PARAMNAME_ENDING;
    public static final String WS_OBJECT_HASH_INFO_MAP_ID_PARAMNAME = HASH_INFO_MAP_ID_PARAMNAME;
    public static final String WS_OBJECT_HASH_INFO_MAP_VALID_PARAMNAME = HASH_INFO_MAP_VALID_PARAMNAME;

    public static final String WS_CALCULATE_HASH_INFO_MAP_PARAMNAME = "CALCULATE" + HASH_INFO_MAP_PARAMNAME_ENDING;
    public static final String WS_CALCULATE_HASH_INFO_MAP_ID_PARAMNAME = HASH_INFO_MAP_ID_PARAMNAME;
    public static final String WS_CALCULATE_HASH_INFO_MAP_VALID_PARAMNAME = HASH_INFO_MAP_VALID_PARAMNAME;

    public static final String WS_REQUEST_ENTITY_PARAMNAME = "entity";

    public static final String WS_AUTH_LOGIN_PARAMNAME = "WS_AUTH_LOGIN";
    public static final String WS_AUTH_USERID_PARAMNAME = "WS_AUTH_USERID";
    public static final String WS_AUTH_NAME_PARAMNAME = "WS_AUTH_NAME";
    public static final String WS_AUTH_PWDEXPDATE_PARAMNAME = "WS_AUTH_PWDEXPDATE";
    public static final String WS_AUTH_METHODNAME_PARAMNAME = "WS_AUTH_METHODNAME";

    public static final String WS_AUTH_REQUESTQUEUEID_PARAMNAME = "WS_AUTH_REQUESTQUEUEID";

    public static final String WS_AUTH_RAW_LOGIN_PARAMNAME = "WS_AUTH_RAW_LOGIN";
    public static final String WS_AUTH_RAW_TOKEN_PARAMNAME = "WS_AUTH_RAW_TOKEN";

    public static final String WS_RESULT_PARAMNAME = "result";

    public static final String WS_REQUEST_QUEUE_TYPESYSNAME_OPENAPI = "OPENAPICOMMON";
    public static final String WS_REQUEST_QUEUE_TYPESYSNAME_UNIREST = "UNIRESTCOMMON";
    public static final String WS_REQUEST_QUEUE_ID_FIELDNAME = "REQUESTQUEUEID";
    public static final String WS_REQUEST_QUERY_STATEID_FIELDNAME = "REQUESTSTATEID";

    public static final String WS_UNIREST_TEMPLATEID_PARAMNAME = "TEMPLATEID";
    public static final String WS_UNIREST_SUBTEMPLATE_PARAMNAME = "SUBTEMPLATE";

    public static final String CONRACT_REQUEST_MAP_PARAMNAME = "REQUESTMAP";
    public static final String CONRACT_REQUEST_REF_PARAMNAME = WS_REQUEST_QUEUE_ID_FIELDNAME;

    public static final String FILE_LINK_CREATE_TIME_MS_PARAMNAME = "ms";
    public static final String FILE_LINK_FILE_PATH_PARAMNAME = "fp";
    public static final String FILE_LINK_USER_DOC_NAME_PARAMNAME = "udn";
    public static final String FILE_LINK_FILE_SYSTEM_ID_PARAMNAME = "fi";
    public static final String FILE_LINK_FILE_SYSTEM_TYPE_PARAMNAME = "ft";
    public static final String FILE_LINK_DATABASE_ID_PARAMNAME = "dbi";
    public static final String FILE_LINK_REPORT_DATABASE_ID_PARAMNAME = "rdbi";
    /** Тип ссылки */
    public static final String FILE_LINK_TYPE_PARAMNAME = "t";
    /** Тип ссылки - действует только 48 часов */
    public static final Long FILE_LINK_TYPE_HOURS_48 = 1L;
    /** Тип ссылки - постоянная */
    public static final Long FILE_LINK_TYPE_PERMANENT = 2L;
    /** Версия набора сведений для сформированной ссылки */
    public static final String FILE_LINK_VERSION_PARAMNAME = "v";

}
