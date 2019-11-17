package com.bivgroup.rest.common;

/**
 * Класс для хранения константных значений
 */
public class Constants {
    /**
     * имя сервиса b2bposws
     */
    public static final String B2BPOSWS = "b2bposws";
    /**
     * имя сервиса bivsberposws
     */
    public static final String BIVSBERPOSWS = "bivsberposws";
    /**
     * имя сервиса adminws
     */
    public static final String ADMINWS = "adminws";
    /**
     * имя сервиса corews
     */
    public static final String COREWS = "corews";
    /**
     * наименование параметра статуса
     */
    public static final String STATUS = "Status";
    /**
     * наименование параметра статуса
     */
    public static final String ERROR = "Error";
    /**
     * наименование параметра ошибки старных сервисов
     */
    public static final String UPPERCASE_ERROR = "ERROR";
    /**
     * наименование параметра сессии
     */
    public static final String SESSION_ID_PARAM_NAME = "SESSIONID";
    /**
     * наименование параметра сессии в lower case
     */
    public static final String SESSION_ID_LOWERCASE_PARAM_NAME = "sessionid";
    /**
     * наименование параметра списка ролей пользователя
     */
    public static final String ROLE_LIST_PARAM_NAME = "ROLELIST";
    /**
     * наименование параметра пароля пользователя
     */
    public static final String PASS_PARAM_NAME = "password";
    /**
     * наименование параметра идентификатора аккаунта пользователя
     */
    public static final String USER_ACCOUNT_ID_PARAM_NAME = "USERACCOUNTID";
    /**
     * наименование параметра токена сессии
     */
    public static final String SESSION_TOKEN_PARAM_NAME = "sessionToken";
    /**
     * наименование параметра результата вызова сервиса
     */
    public static final String RESULT = "Result";
    /**
     * наименование параметра системного логина для вызова сервисов
     */
    public static final String SYSTEM_PARAM_NAME_LOGIN = "INSSYSTEMLOGIN";
    /**
     * наименование параметра системного пароля для вызова сервисов
     */
    public static final String SYSTEM_PARAM_NAME_PASS = "INSSYSTEMPASSWORD";
    /**
     * наименование параметра который сообщит сервису, что результат надо вернуть в "словаре"
     */
    public static final String RETURN_AS_HASH_MAP = "ReturnAsHashMap";
    /**
     * наименование параметра который приходят с интерфейса
     */
    public static final String FORM_PARAM_NAME = "params";
    /**
     * наименование параметра главного имени пользователя
     */
    public static final String ACTIVE_DIRECTORY_PRINCIPAL_NAME_PARAM_NAME = "ADUSERPRINCIPALNAME";
    /**
     * наименование метода поиска связи б2б пользователя с active directory
     */
    public static final String SEARCH_LINK_AD_METHOD_NAME = "searchUserAccountByActiveDirectoryLinkFields";
    public static final String B2B_DEPARTMENTID_PARAMNAME = "SESSION_DEPARTMENTID";
    public static final String B2B_USERGROUPS_PARAMNAME = "SESSION_USERGROUPS";
    public static final String B2B_USERTYPEID_PARAMNAME = "SESSION_USERTYPEID";
    public static final String B2B_USERACCOUNTID_PARAMNAME = "SESSION_USERACCOUNTID";

    /**
     * Приватный конструктор.
     * Т.к. класс используется только для хранения констант,
     * то нужно запретить его создавать из вне
     */
    private Constants() {

    }
}
