package ru.diasoft.services.utils;

public class Constants {
    /**
     * наименование параметра ошибки старных сервисов
     */
    public static final String UPPERCASE_ERROR = "ERROR";
    /**
     * наименование параметра идентификатора аккаунта пользователя
     */
    public static final String USER_ACCOUNT_ID_PARAM_NAME = "USERACCOUNTID";
    /**
     * наименование параметра списка ролей пользователя
     */
    public static final String ROLE_LIST_PARAM_NAME = "ROLELIST";
    /**
     * наименование параметра пароля пользователя
     */
    public static final String PASS_PARAM_NAME = "password";
    /**
     * наименование параметра статуса
     */
    public static final String ERROR = "Error";
    /**
     * наименование параметра сессии в upper case
     */
    public static final String SESSION_ID_PARAM_NAME = "SESSIONID";
    /**
     * наименование параметра сессии в lower case
     */
    public static final String SESSION_ID_LOWERCASE_PARAM_NAME = "sessionid";
    /**
     * наименование параметра токена сессии
     */
    public static final String SESSION_TOKEN_PARAM_NAME = "sessionToken";
    /**
     * наименование параметра результата вызова сервиса
     */
    public static final String RESULT = "Result";
    /**
     * наименование параметра который приходят с интерфейса
     */
    public static final String FORM_PARAM_NAME = "params";
    /**
     * Наименование параметра типа файла
     */
    public static final String DCT_FILE_TYPE_NAME_PARAMNAME = "FILETYPENAME";
    /**
     * имя сервиса bivsberposws для вызова данного сервиса с помощью #{@link ru.diasoft.services.bivsberlossws.BoxPropertyGate#callExternalService}
     */
    public static final String BIVSBERPOSWS = "bivsberposws";
    /**
     * наименование параметра статуса вызова сервиса
     */
    public static final String STATUS = "Status";
    /**
     * Идентификатор аккаунта пользователя в сессии
     */
    public static final String SESSIONPARAM_USERACCOUNTID = "SESSION_USERACCOUNTID";
    /**
     * Идентификатор типа пользователя в сессии
     */
    public static final String SESSIONPARAM_USERTYPEID = "SESSION_USERTYPEID";
    /**
     * Идентификатор подразделения пользователя в сессии
     */
    public static final String SESSIONPARAM_DEPARTMENTID = "SESSION_DEPARTMENTID";
    /**
     * наименование параметра системного логина для вызова сервисов
     */
    public static final String SYSTEM_PARAM_NAME_LOGIN = "INSSYSTEMLOGIN";
    /**
     * наименование параметра системного пароля для вызова сервисов
     */
    public static final String SYSTEM_PARAM_NAME_PASS = "INSSYSTEMPASSWORD";
    /**
     * имя сервиса bivsberlossws для вызова данного сервиса с помощью #{@link ru.diasoft.services.bivsberlossws.BoxPropertyGate#callExternalService}
     */
    public static final String SERVICE_NAME = "bivsberlossws";
    /**
     * имя сервиса crmws для вызова данного сервиса с помощью #{@link ru.diasoft.services.bivsberlossws.BoxPropertyGate#callExternalService}
     */
    public static final String CRMWS = "crmws";
    /**
     * имя сервиса b2bposws для вызова данного сервиса с помощью #{@link ru.diasoft.services.bivsberlossws.BoxPropertyGate#callExternalService}
     */
    public static final String B2BPOSWS = "b2bposws";
    /**
     * имя сервиса paws для вызова данного сервиса с помощью #{@link ru.diasoft.services.bivsberlossws.BoxPropertyGate#callExternalService}
     */
    public static final String PAWS = "paws";
    /**
     * Имя параметра хибернейт сущности
     */
    public static final String DCT_ENTITY_PARAM_NAME = "HIBERNATEENTITY";
    /**
     * имя сервиса signb2bposws для вызова данного сервиса с помощью #{@link ru.diasoft.services.bivsberlossws.BoxPropertyGate#callExternalService}
     */
    public static final String SIGNB2BPOSWS = "signb2bposws";
    /**
     * имя сервиса signbivsberposws для вызова данного сервиса с помощью #{@link ru.diasoft.services.bivsberlossws.BoxPropertyGate#callExternalService}
     */
    public static final String SIGNBIVSBERPOSWS = "signbivsberposws";
    /**
     * наименование параметра главного имени пользователя
     */
    public static final String ACTIVE_DIRECTORY_PRINCIPAL_NAME_PARAM_NAME = "ADUSERPRINCIPALNAME";
    /**
     * наименование метода поиска связи б2б пользователя с active directory
     */
    public static final String SEARCH_LINK_AD_METHOD_NAME = "searchUserAccountByActiveDirectoryLinkFields";

    /**
     * Приватный конструктор.
     * Т.к. класс используется только для хранения констант,
     * то нужно запретить его создавать из вне
     */
    private Constants() {

    }
}
