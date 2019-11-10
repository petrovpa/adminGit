package com.bivgroup.ldap.authorization;

import com.bivgroup.config.Config;
import com.bivgroup.config.ConfigUtils;
import com.bivgroup.ldap.exception.ServiceException;
import com.bivgroup.ldap.pojo.ActiveDirectoryAuthUserInfo;
import com.bivgroup.ldap.pojo.ActiveDirectoryUserInfo;
import com.bivgroup.ldap.pojo.SearchActiveDirectoryUserCondition;
import com.bivgroup.ldap.pojo.SearchActiveDirectoryUserConditionList;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;
import java.util.stream.Collectors;

public class LdapUserService {
    private static final String SECURITY_AUTHENTICATION = "simple";
    private static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final String EMPTY_PASS_ERROR = "Не передан пароль пользователя active directory!";
    private static final String[] BASE_USER_PARAMETERS = new String[]{"memberof", "name", "userPrincipalName", "sAMAccountName", "givenName", "sn", "userAccountControl"};
    private Logger logger = Logger.getLogger(this.getClass());
    private Config config;

    public LdapUserService() {
        String useServiceName = ConfigUtils.getProjectProperty("ru.diasoft.services.insurance.ServiceName", "ldap-service");
        config = Config.getConfig(useServiceName);
    }

    /**
     * Асторизация пользователя взависимости от переданных параметров.
     * #{@link ActiveDirectoryAuthUserInfo} password является обязательным параметром
     * Так же нужно передать одно из полей activeDirectoryLogin (sAMAccountName) или userPrincipalName,
     * для авторизации пользователя
     *
     * @param user информация о ldap пользователе, под которым нужно авторизоваться
     * @return информацию о пользователе, если авторизациия прошла успешно
     * @throws ServiceException ошибка авторизации
     */
    public ActiveDirectoryUserInfo authorizationActiveDirectoryUser(ActiveDirectoryAuthUserInfo user) throws ServiceException {
        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_PASS_ERROR);
        }
        String activeDirectoryLogin = user.getActiveDirectoryLogin();
        String userPrincipalName = user.getUserPrincipalName();
        ActiveDirectoryUserInfo userInfo = null;
        if (!userPrincipalName.isEmpty() && activeDirectoryLogin.isEmpty()) {
            userInfo = this.authorizationUserByPrincipalName(user);
        }
        if (userInfo == null && userPrincipalName.isEmpty() && !activeDirectoryLogin.isEmpty()) {
            userInfo = this.authorizationByActiveDirectoryAccount(user);
        }
        return userInfo;
    }

    /**
     * Метод авторизации пользователя по логину active directory
     * Обязательные параметры activeDirectoryLogin (sAMAccountName) и password #{@link ActiveDirectoryAuthUserInfo}
     *
     * @param user информация о ldap пользователе, под которым нужно авторизоваться.
     * @return информацию о пользователе, если авторизациия прошла успешно
     * @throws ServiceException ошибка авторизации
     */
    public ActiveDirectoryUserInfo authorizationByActiveDirectoryAccount(ActiveDirectoryAuthUserInfo user) throws ServiceException {
        String login = user.getActiveDirectoryLogin();
        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("Не передан логин пользователя active directory");
        }
        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_PASS_ERROR);
        }
        ActiveDirectoryUserInfo userInfo = null;
        try {
            SearchActiveDirectoryUserCondition condition = new SearchActiveDirectoryUserCondition();
            condition.setActiveDirectoryLogin(user.getActiveDirectoryLogin());
            userInfo = this.searchUserByCondition(condition);
            if (userInfo == null) {
                throw new ServiceException("Пользователь не найден!");
            } else {
                // если пользователь заблокирован, то и не надо пытаться авторизоваться через него
                if (userInfo.isBlocked()) {
                    Properties properties = new Properties();
                    properties.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
                    properties.put(Context.PROVIDER_URL, getActiveDirectoryUrl());
                    properties.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
                    properties.put(Context.SECURITY_PRINCIPAL, userInfo.getUserPrincipalName());
                    properties.put(Context.SECURITY_CREDENTIALS, password);
                    InitialDirContext context = new InitialDirContext(properties);
                    context.close();
                }
            }
        } catch (NamingException e) {
            String message = e.getMessage();
            logger.error("User authorization", e);
            throw new ServiceException(getAuthError(message), e);
        }
        return userInfo;
    }

    /**
     * Метод авторизации пользователя на ldap сервере.
     * Обязательные параметры userPrincipalName и password #{@link ActiveDirectoryAuthUserInfo}
     *
     * @param user информация о ldap пользователе, под которым нужно авторизоваться.
     * @return информацию о пользователе, если авторизациия прошла успешно
     * @throws ServiceException ошибка авторизации
     */
    public ActiveDirectoryUserInfo authorizationUserByPrincipalName(ActiveDirectoryAuthUserInfo user) throws ServiceException {
        String login = user.getUserPrincipalName();
        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("Не передано основное имя пользователя active directory");
        }
        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_PASS_ERROR);
        }
        // настрока параметров для подключения к домену
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        properties.put(Context.PROVIDER_URL, getActiveDirectoryUrl());
        properties.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
        properties.put(Context.SECURITY_PRINCIPAL, login);
        properties.put(Context.SECURITY_CREDENTIALS, password);
        ActiveDirectoryUserInfo userInfo = null;
        try {
            InitialDirContext context = new InitialDirContext(properties);
            SearchActiveDirectoryUserCondition condition = new SearchActiveDirectoryUserCondition();
            condition.setUserPrincipalName(login);
            userInfo = this.searchUserByCondition(context, condition);
            context.close();
        } catch (NamingException e) {
            String message = e.getMessage();
            logger.error("User authorization", e);
            throw new ServiceException(getAuthError(message), e);
        }
        return userInfo;
    }

    /**
     * Получить текст ошибки по коду
     *
     * @param message код ошибки
     * @return текст ошибки
     */
    private String getAuthError(String message) {
        String result = "Ошибка авторизации пользователя AD. За подробностями обратитесь к журналу сервера!";
        try {
            String errorCode = message.substring(message.indexOf("data"), message.lastIndexOf(','));
            switch (errorCode) {
                case "data 525":
                    result = "Пользователь AD не найден!";
                    break;
                case "data 52e":
                    result = "Неверный логин или пароль пользователя AD!";
                    break;
                case "data 530":
                    result = "Пользователю AD в данное время вход запрещен!";
                    break;
                case "data 531":
                    result = "Пользователю AD запрещен вход на данную рабочую станцию!";
                    break;
                case "data 532":
                    result = "Истекло время действия пароля пользователя AD!";
                    break;
                case "data 533":
                    result = "Аккаунт пользователя AD отключен!";
                    break;
                case "data 701":
                    result = "Истекло время действия аккаунт пользователя AD!";
                    break;
                case "data 773":
                    result = "Требуется сброс пароля пользователя AD!";
                    break;
                case "data 775":
                    result = "Учетная запись пользователя AD заблокирована!";
                    break;
                default:
                    result = "Ошибка авторизации пользователя AD.";
            }
        } catch (StringIndexOutOfBoundsException ex) {
            logger.error("Error parsed authorization exception message ", ex);
        }
        return result;
    }

    /**
     * Метод получения списка активных пользователей в active directory
     *
     * @return список активных пользователей
     * @throws ServiceException ошибка получения списка активных пользователей
     */
    public List<ActiveDirectoryUserInfo> getActiveUsers() throws ServiceException {
        List<ActiveDirectoryUserInfo> result = this.getUsers();
        if (!result.isEmpty()) {
            result = result.stream().filter(it -> !it.isBlocked()).collect(Collectors.toList());
        }
        return result;
    }

    /**
     * Метод получения списка пользователей в active directory
     *
     * @return список пользователей
     * @throws ServiceException ошибка получения списка пользователей
     */
    public List<ActiveDirectoryUserInfo> getUsers() throws ServiceException {
        List<ActiveDirectoryUserInfo> ldapUsers = new ArrayList<>();
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        properties.put(Context.PROVIDER_URL, getActiveDirectoryUrl());
        properties.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
        properties.put(Context.SECURITY_PRINCIPAL, getDefaultActiveDirectoryLogin());
        properties.put(Context.SECURITY_CREDENTIALS, getDefaultActiveDirectoryPassword());
        try {
            InitialDirContext context = new InitialDirContext(properties);
            SearchControls searchControls = new SearchControls();
            searchControls.setReturningAttributes(BASE_USER_PARAMETERS);
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchBase = getUsersSearchBase();
            String filter = "(&(objectClass=User)(objectCategory=Person))";
            NamingEnumeration<SearchResult> answers = context.search(searchBase, filter, searchControls);
            while (answers.hasMore()) {
                SearchResult answerElement = answers.nextElement();
                Attributes answerElementAttributes = answerElement.getAttributes();
                ActiveDirectoryUserInfo userInfo = createUserInfoByAttributes(answerElementAttributes);
                ldapUsers.add(userInfo);
            }
            answers.close();
            context.close();
        } catch (NamingException e) {
            logger.error("Error get ldap user list", e);
            throw new ServiceException("Ошибка получения списка пользователей!", e);
        }
        return ldapUsers;
    }

    /**
     * Получения списка пользователей в виде списка "словарей" по огранчениям.
     * Для удобной работы из B2B
     *
     * @param condition значения параметров для задания ограничений #{@link SearchActiveDirectoryUserConditionList}
     * @return список пользователей active directory
     * @throws ServiceException ошибка поиска пользователя по ограничениям
     */
    public List<Map<String, Object>> getUsersByCondition(Map<String, Object> condition) throws ServiceException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SearchActiveDirectoryUserConditionList conditionList = mapper.convertValue(condition, SearchActiveDirectoryUserConditionList.class);
        List<ActiveDirectoryUserInfo> users = this.getUsersByCondition(conditionList);
        TypeFactory factory = mapper.getTypeFactory();
        JavaType javaType = factory.constructCollectionType(List.class, factory.constructMapLikeType(Map.class, String.class, Object.class));
        return mapper.convertValue(users, javaType);
    }

    /**
     * Получения списка пользователей по огранчениям
     *
     * @param condition значения параметров для задания ограничений #{@link SearchActiveDirectoryUserConditionList}
     * @return список пользователей active directory
     * @throws ServiceException ошибка поиска пользователя по ограничениям
     */
    public List<ActiveDirectoryUserInfo> getUsersByCondition(SearchActiveDirectoryUserConditionList condition) throws ServiceException {
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        properties.put(Context.PROVIDER_URL, getActiveDirectoryUrl());
        properties.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
        properties.put(Context.SECURITY_PRINCIPAL, getDefaultActiveDirectoryLogin());
        properties.put(Context.SECURITY_CREDENTIALS, getDefaultActiveDirectoryPassword());
        List<ActiveDirectoryUserInfo> users = new ArrayList<>();
        try {
            InitialDirContext context = new InitialDirContext(properties);
            SearchControls searchControls = new SearchControls();
            searchControls.setReturningAttributes(BASE_USER_PARAMETERS);
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchBase = getUsersSearchBase();
            String filter = condition.createCondition(getBaseUserFilter());
            NamingEnumeration<SearchResult> answers = context.search(searchBase, filter, searchControls);
            while (answers.hasMore()) {
                SearchResult result = answers.nextElement();
                Attributes attributes = result.getAttributes();
                ActiveDirectoryUserInfo userInfo = createUserInfoByAttributes(attributes);
                if (userInfo != null && (condition.isSearchBlocked() || !userInfo.isBlocked())) {
                    users.add(userInfo);
                }
            }
            answers.close();
            context.close();
        } catch (NamingException e) {
            logger.error(String.format("Error get active directory user list by condition %s", condition), e);
            throw new ServiceException("Ошибка получения списка пользователей по ограничениям!", e);
        }
        return users;
    }

    /**
     * Метод поиска пользователя по ограничениям
     *
     * @param condition значения параметров для задания ограничений #{@link SearchActiveDirectoryUserCondition}
     * @return информацию о найденом пользователе
     * @throws ServiceException ошибка поиска пользователя по ограничениям
     */
    public ActiveDirectoryUserInfo searchUserByCondition(SearchActiveDirectoryUserCondition condition) throws ServiceException {
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        properties.put(Context.PROVIDER_URL, getActiveDirectoryUrl());
        properties.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
        properties.put(Context.SECURITY_PRINCIPAL, getDefaultActiveDirectoryLogin());
        properties.put(Context.SECURITY_CREDENTIALS, getDefaultActiveDirectoryPassword());
        ActiveDirectoryUserInfo userInfo = null;
        try {
            InitialDirContext context = new InitialDirContext(properties);
            userInfo = this.searchUserByCondition(context, condition);
            context.close();
        } catch (NamingException e) {
            logger.error("Error search ldap user ", e);
            throw new ServiceException("Ошибка поиска пользователя по ограничениям!", e);
        }
        return userInfo;
    }

    /**
     * Поиск пользователя по ограничения и переданому контексту
     *
     * @param context   контекст - подкючение к active directory
     * @param condition значения параметров для задания ограничений #{@link SearchActiveDirectoryUserCondition}
     * @return информацию о найденом пользователе
     * @throws ServiceException ошибка поиска пользователя по ограничениям
     */
    private ActiveDirectoryUserInfo searchUserByCondition(InitialDirContext context, SearchActiveDirectoryUserCondition condition) throws ServiceException {
        ActiveDirectoryUserInfo userInfo = null;
        try {
            SearchControls searchControls = new SearchControls();
            searchControls.setReturningAttributes(BASE_USER_PARAMETERS);
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchBase = getUsersSearchBase();
            String filter = condition.createCondition(getBaseUserFilter());
            NamingEnumeration<SearchResult> answers = context.search(searchBase, filter, searchControls);
            if (answers.hasMore()) {
                SearchResult answerElement = answers.nextElement();
                Attributes answerElementAttributes = answerElement.getAttributes();
                userInfo = createUserInfoByAttributes(answerElementAttributes);
            }
            answers.close();
        } catch (NamingException e) {
            logger.error("Error search ldap user ", e);
            throw new ServiceException("Ошибка поиска пользователя по ограничениям!", e);
        }
        return userInfo;
    }

    /**
     * Метод получения базового фильтра для поиска пользователей в Active Directory из конфига
     *
     * @return базовый фильтр поиска пользователей
     */
    private String getBaseUserFilter() {
        return config.getParam("filterSearchBase", "(objectClass=User)(objectCategory=Person)(userPrincipalName=*)");
    }

    /**
     * Метод получить базы для поиска пользователей в Active Directory из конфига
     *
     * @return строка базы поиска пользователя
     */
    private String getUsersSearchBase() {
        return config.getParam("usersSearchBase", "OU=Departments,DC=it,DC=bivgroup,DC=com");
    }

    /**
     * Метод создания #{@link ActiveDirectoryUserInfo} по атрибутам из Active Directory
     *
     * @param resultAttributes атрибуты пользователя из Active Directory
     * @return информация о пользователя Active Directory
     */
    private ActiveDirectoryUserInfo createUserInfoByAttributes(Attributes resultAttributes) {
        ActiveDirectoryUserInfo userInfo = new ActiveDirectoryUserInfo();
        try {
            Attribute attribute = resultAttributes.get("givenName");
            if (attribute != null && attribute.size() > 0) {
                userInfo.setGivenName(attribute.get().toString());
            }
            attribute = resultAttributes.get("sn");
            if (attribute != null && attribute.size() > 0) {
                userInfo.setSurname(attribute.get().toString());
            }
            attribute = resultAttributes.get("name");
            if (attribute != null && attribute.size() > 0) {
                userInfo.setFullName(attribute.get().toString());
            } else {
                userInfo.setFullName(userInfo.getSurname() + " " + userInfo.getGivenName());
            }
            attribute = resultAttributes.get("userPrincipalName");
            if (attribute != null && attribute.size() > 0) {
                userInfo.setUserPrincipalName(attribute.get().toString());
            }
            attribute = resultAttributes.get("sAMAccountName");
            if (attribute != null && attribute.size() > 0) {
                userInfo.setActiveDirectoryLogin(attribute.get().toString());
            }
            attribute = resultAttributes.get("userAccountControl");
            if (attribute != null && attribute.size() > 0) {
                userInfo.calculateIsBlocked(attribute.get().toString());
            }
            // проверка вхождения пользователя в допустимую группу для входа в систему
            analyzeMemberOf(userInfo, resultAttributes);
            // оказывается проверки по memberOf недостаточно, проверяем по уникальному имени листа не входит ли
            // пользователь в хотя бы одну запрещенную группу для входа
            allowedGroupToSignIn(userInfo, resultAttributes);
        } catch (NamingException e) {
            userInfo = null;
            logger.error("Error get attribute value ", e);
        }
        return userInfo;
    }

    /**
     * Метод анализа группы пользователя в AD (атрибут memberOf).
     * Пользователю достаточно находиться хотя бы в одной группе,
     * которая указана в конфига и получается с помощью #{@link #getValidUserGroups}
     *
     * @param userInfo         информация о пользователе
     * @param resultAttributes атрибуты из AD
     * @throws NamingException ошибка получения атрибута
     */
    private void analyzeMemberOf(ActiveDirectoryUserInfo userInfo, Attributes resultAttributes) throws NamingException {
        Attribute attribute = resultAttributes.get("memberOf");
        userInfo.setAccessUserIsAvailable(false);
        if (attribute != null && attribute.size() > 0) {
            List<String> validUserGroups = getValidUserGroups();
            if (validUserGroups.isEmpty()) {
                userInfo.setAccessUserIsAvailable(true);
            } else {
                NamingEnumeration allUserGroup = attribute.getAll();
                iterateAllUserGroup(userInfo, validUserGroups, allUserGroup);
                allUserGroup.close();
            }
        }
    }

    /**
     * Проитись по всем группам в которые входит пользователь
     *
     * @param userInfo        информация о пользователе, которая выдается наружу
     * @param validUserGroups список допустимых групп для входа
     * @param allUserGroup    список значений атрибута memberOf
     * @throws NamingException ошибка получения атрибута
     */
    private void iterateAllUserGroup(ActiveDirectoryUserInfo userInfo, List<String> validUserGroups, NamingEnumeration allUserGroup) throws NamingException {
        while (allUserGroup.hasMore()) {
            StringBuilder sb = new StringBuilder(allUserGroup.next().toString());
            if (sb.length() != 0) {
                String userGroup = sb.subSequence(sb.indexOf("=") + 1, sb.indexOf(",")).toString();
                userInfo.getUserGroup().add(userGroup);
                if (!userInfo.isAccessUserIsAvailable()) {
                    userInfo.setAccessUserIsAvailable(validUserGroups.contains(userGroup));
                }
                sb.setLength(0);
            }
        }
    }

    /**
     * Метод получить список групп Active Directory из конфига для которых доступен вход в б2б
     *
     * @return список групп Active Directory для которых доступен вход в б2б
     */
    private List<String> getValidUserGroups() {
        String validUserGroups = config.getParam("validUserGroups", null);
        return validUserGroups == null ? Collections.emptyList() : Arrays.asList(validUserGroups.split(";"));
    }

    /**
     * Метод проверки разрешенности входа пользователя по уникальному имени узла Active Directory.
     * Уникальному имени узла достаточно включать в себя одно имя из списка получаемого из конфига
     * #{@link #getExcludeDistinguishedName}
     *
     * @param userInfo   - информация о пользователе AD, которая выдается наружу
     * @param attributes - атрибуты из AD для анализа
     */
    private void allowedGroupToSignIn(ActiveDirectoryUserInfo userInfo, Attributes attributes) throws NamingException {
        Attribute distinguishedName = attributes.get("distinguishedName");
        if (distinguishedName != null && distinguishedName.size() > 0) {
            List<String> filterDistinguishedName = getExcludeDistinguishedName();
            NamingEnumeration allDistinguishedName = distinguishedName.getAll();
            while (allDistinguishedName.hasMore() && !userInfo.isInForbiddenGroup()) {
                String distinguishedNameStr = allDistinguishedName.next().toString();
                for (String element : filterDistinguishedName) {
                    if (distinguishedNameStr.contains(element)) {
                        userInfo.setInForbiddenGroup(true);
                        userInfo.setForbiddenGroupName(element);
                        break;
                    }
                }
            }
            allDistinguishedName.close();
        }
    }

    /**
     * Метод получает список групп в дереве Active Directory из конфига для которых недоступен вход в б2б
     *
     * @return список групп в дереве Active Directory для которых недоступен вход в б2б
     */
    private List<String> getExcludeDistinguishedName() {
        final String validGroups = config.getParam("excludeDistinguishedName", null);
        return validGroups == null ? Collections.emptyList() : Arrays.asList(validGroups.split(";"));
    }

    private Map<String, Object> createMapByAttributes(Attributes resultAttributes, String... returnAttributes)
            throws NamingException {
        Map<String, Object> result = new HashMap<>();
        for (String returnAttribute : returnAttributes) {
            Attribute attribute = resultAttributes.get(returnAttribute);
            if (attribute != null && attribute.size() > 0) {
                result.put(returnAttribute, attribute.get().toString());
            }
        }
        return result;
    }

    /**
     * Метод получения url до Active Directory из конфига
     *
     * @return url Active Directory
     */
    private String getActiveDirectoryUrl() {
        return config.getParam("ldapUrl", "ldap://10.16.100.210:389");
    }

    /**
     * Метод получения логина пользователя для выполнения запросов в Active Directory из конфига
     *
     * @return userPrincipalName пользователя Active Directory
     */
    private String getDefaultActiveDirectoryLogin() {
        return config.getParam("defaultLogin", "aivashin@it.bivgroup.com");
    }

    /**
     * Метод полечения пароля пользователя для выполнения запросов в Active Directory из конфига
     *
     * @return пароль пользователя Active Directory
     */
    private String getDefaultActiveDirectoryPassword() {
        return config.getParam("defaultPwd", "123qweQWE");
    }

}
