package com.bivgroup.rest.common;

public class Constants {
    public static final String THIS_SERVICE_NAME = "admrestws";
    public static final String ADMINWS = "adminws";
    public static final String COREWS = "corews";
    public static final String B2BPOSWS = "b2bposws";


    public static final String SESSIONPARAM_USERACCOUNTID = "SESSION_USERACCOUNTID";
    public static final String SESSIONPARAM_USERTYPEID = "SESSION_USERTYPEID";
    public static final String SESSIONPARAM_DEPARTMENTID = "SESSION_DEPARTMENTID";
    public static final String SESSION_HASH_PARAMNAME = "sessionHash";

    public static final String ERROR = "Error";
    public static final String ADMIN_CALL_ERROR = "admCallError";
    public static final String STATUS = "Status";

    // копия из WsConstants
    public static final String RESULT = "Result"; // WsConstants.RESULT
    public static final String SYSTEM_LOGIN = "INSSYSTEMLOGIN"; // WsConstants.SYSTEM_LOGIN
    public static final String SYSTEM_PASS = "INSSYSTEMPASSWORD"; // WsConstants.SYSTEM_PASS
    public static final String RETURN_AS_HASH_MAP = "ReturnAsHashMap"; // WsConstants.RETURN_AS_HASH_MAP

    public static final String USER_ACCOUNT_ID_PARAM_NAME = "userAccountId";

    static final String SESSION_TYPE_PARAMNAME = "sessionType";
    static final String USERGROUPS_PARAMNAME = "SESSION_USERGROUPS";

    /**
     * Наименования парамера в котором находится цепочка IP адресов вызова
     */
    public static final String IP_CHAIN_HEADER_NAME = "X-Forwarded-For";
    /**
     * IP по умолчанию, если цепочка не передано
     */
    public static final String DEFAULT_IP_CHAIN = "127.0.0.1";

    /**
     * Значения статуса успешного вызова
     */
    public static final String RESULT_STATUS_OK = "OK";

    private Constants() {

    }

}
