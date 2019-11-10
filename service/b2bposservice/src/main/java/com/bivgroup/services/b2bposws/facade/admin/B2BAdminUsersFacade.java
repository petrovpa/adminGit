package com.bivgroup.services.b2bposws.facade.admin;

import com.bivgroup.password.PasswordStrengthVerifier;
import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.facade.B2BPosServiceSessionController;
import com.bivgroup.services.b2bposws.system.Constants;
import com.bivgroup.services.b2bposws.system.SmsSender;
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.commons.codec.digest.DigestUtils;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;
import ru.diasoft.utils.exception.SoftServiceErrorException;

import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.bivgroup.password.PasswordStrengthVerifier.*;
import static com.bivgroup.password.PasswordStrengthVerifier.PWD_GROUPS_ADDITIONAL_ALLOWED;
import static com.bivgroup.services.b2bposws.system.Constants.ADMINWS;

/**
 * Фасад для работы с Пользователями
 *
 * @author Alex Ivashin
 */
@BOName("B2BAdminUsers")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class B2BAdminUsersFacade extends B2BBaseFacade {

    private static final String PWD_DEF_SYMBOLS_LIST = "~ ! @ # $ % ^ & * ( ) _ + ` - = { } [ ] : ; < > . / \\";
    private static final String EMAIL_METHOD_NAME = "mailmessage";
    private TimeBasedOneTimePasswordGenerator totp;

    public B2BAdminUsersFacade() {
        try {
            this.totp = new TimeBasedOneTimePasswordGenerator();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Create TimeBasedOneTimePasswordGenerator is failed", e);
        }
    }

    /**
     * Проверяем есть ли в параметрах фильтрации параметр ISNEEDSHOWDELETEDUSERS
     * если есть и его значение 0 (Нет) или отсутствует, тогда возвращаем false,
     * иначе, если значение равно 1 (Да), тогда возвращаем true. Если параметр
     * присутсвует, то после обработки его следует удалить
     *
     * @param filterParams Map. Параметров фильтрации
     * @return булевского значение, говорящее о том следует ли показывать
     * удаленных пользователей или нет
     */
    private boolean isNeedShowDeletedUser(Map<String, Object> filterParams) {
        // по умолчанию считаем, что удаленных пользователей показывать не надо
        boolean result = false;
        boolean isFoundOption = false;
        if (filterParams != null) {
            for (Map.Entry<String, Object> entry : filterParams.entrySet()) {
                if (entry.getValue() != null) {
                    Map<String, Object> filterMap = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entryFilter : filterMap.entrySet()) {
                        Map<String, Object> parameterMap = (Map<String, Object>) entryFilter.getValue();
                        for (Map.Entry<String, Object> entryParameter : parameterMap.entrySet()) {
                            if (entryParameter.getKey().equals("ISNEEDSHOWDELETEDUSERS")) {
                                String parameterValue = entryParameter.getValue().toString();
                                parameterValue = parameterValue.replaceAll("[()]", "");
                                if (parameterValue.length() < 2) {
                                    result = Long.parseLong(parameterValue) == 1;
                                }
                                parameterMap.remove("ISNEEDSHOWDELETEDUSERS");
                                filterMap.remove(entryFilter.getKey());
                                filterParams.put(entry.getKey(), filterMap);
                                isFoundOption = true;
                                break;
                            }
                        }
                        if (isFoundOption) {
                            break;
                        }
                    }
                    if (isFoundOption) {
                        break;
                    }
                }
            }

            result = isFoundOption ? result : isFoundOption;
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminGetUsersList(Map<String, Object> params)
            throws Exception {
        // возможно, здесь не потребуется, когда преобразование дат
        // во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(params, Double.class);

        Map<String, Object> customParams = params;
        Map<String, Object> filterParams = (Map<String, Object>) params.get("FILTERPARAMS");

        /**
         * Если пришло это свойство TREEDEPARTMENTOF, которое предполагает выбор
         * всех сотрудников начиная с номера TREEDEPARTMENTOF и заканчивая
         * последним.
         */
        if (params.get("ROOTDEPARTMENTID") != null) {
            customParams.put("ROOTDEPARTMENTID", params.get("ROOTDEPARTMENTID"));

            String customRangeDepartment = "T2.DEPARTMENTID IN ("
                    + "SELECT T3.DEPARTMENTID"
                    + " FROM DEP_DEPARTMENT T3"
                    + " START WITH T3.DEPARTMENTID       =  " + customParams.get("ROOTDEPARTMENTID").toString()
                    + "CONNECT BY PRIOR T3.DEPARTMENTID =  T3.PARENTDEPARTMENT)";

            customParams.put("CUSTOMROOTDEPARTMENTID", customRangeDepartment);
        } else {
            // Иначе выбираем всех, начиная с ROOT подразделения
            customParams.put("ROOTDEPARTMENTID", 0);
        }

        /**
         * Проверяем параметры фильтрации на присутсвие там параметра
         * ISNEEDSHOWDELETEDUSERS Если процедура isNeedShowDeletedUser
         * возвращает false, тогда в customParams кладем параметр
         * GETACTIVEUSERS, чтобы запрос вернул лишь активных пользоватлей
         * системы. Иначе не кладем ничего и запрос вернет всех пользователей
         * системы.
         */
        if (!isNeedShowDeletedUser(filterParams)) {
            customParams.put("GETACTIVEUSERS", "TRUE");
        }
        Map<String, Object> result = null;


        // добавляем ограничение по агентам только если пользователь не "Сотрудник страховой" и не "Робот"
        Object userTypeId = params.get(Constants.SESSIONPARAM_USERTYPEID);
        if ((userTypeId == null) || ((Long.parseLong(userTypeId.toString()) != 1)
                && ((Long.parseLong(userTypeId.toString())) != 4))) {
            customParams.put("CP_DEPARTMENTID", params.get(Constants.SESSIONPARAM_DEPARTMENTID));
        }
        customParams.put("HIDECHILDCONTR", "TRUE");
        XMLUtil.convertDateToFloat(customParams);
        // Ограничение по орг структуре.
        if (null == params.get("ROOTID")) {
            customParams.put("DEPRIGHT", params.get("SESSION_DEPARTMENTID"));
            if (null != params.get("USE_SESSION_DEPARTMENTID")) {
                if (params.get("USE_SESSION_DEPARTMENTID").equals(true)) {
                    customParams.put("DEPRIGHT", params.get("SESSION_DEPARTMENTID"));
                }
            }
        }
        result = this.selectQuery(
                "dsAdminUsersList", "dsAdminGetUsersCount", customParams
        );
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminGetUsersPartnerList(Map<String, Object> params)
            throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String customWhere;
        // возможно, здесь не потребуется, когда преобразование дат 
        // во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(params, Double.class);

        Map<String, Object> customParams = params;

        //filter field
        StringBuilder sb = new StringBuilder();
        if (params.get("sortModel") != null) {
            ArrayList<Object> sortModel = (ArrayList<Object>) params.get("sortModel");
            if (!sortModel.isEmpty()) {
                for (Iterator iterator = sortModel.iterator(); iterator.hasNext(); ) {
                    Map<String, String> sModel = (Map<String, String>) iterator.next();
                    if ((sModel.get("colId") != null)
                            && (sModel.get("sort") != null)) {
                        sb.append(sModel.get("colId"))
                                .append(" ")
                                .append(sModel.get("sort"));
                        sb.append(", ");
                    }
                }
                if (sb.length() > 1) {
                    sb.delete(sb.length() - 2, sb.length());
                    customParams.put("ORDERBY", sb.toString());
                }
            }
        }

        Map<String, Object> filterParams = (Map<String, Object>) params.get("FILTERPARAMS");

        /**
         * Если пришло это свойство TREEDEPARTMENTOF, которое предполагает выбор
         * всех сотрудников начиная с номера TREEDEPARTMENTOF и заканчивая
         * последним.
         */
        if (params.get("ROOTDEPARTMENTID") != null) {
            customParams.put("ROOTDEPARTMENTID", params.get("ROOTDEPARTMENTID"));

            String customRangeDepartment = "T2.DEPARTMENTID IN ("
                    + "SELECT T3.DEPARTMENTID"
                    + " FROM DEP_DEPARTMENT T3"
                    + " START WITH T3.DEPARTMENTID       =  " + customParams.get("ROOTDEPARTMENTID").toString()
                    + "CONNECT BY PRIOR T3.DEPARTMENTID =  T3.PARENTDEPARTMENT)";

            customParams.put("CUSTOMROOTDEPARTMENTID", customRangeDepartment);
        } else {
            // Иначе выбираем всех, начиная с ROOT подразделения
            customParams.put("ROOTDEPARTMENTID", 0);
        }

        /**
         * Проверяем параметры фильтрации на присутсвие там параметра
         * ISNEEDSHOWDELETEDUSERS Если процедура isNeedShowDeletedUser
         * возвращает false, тогда в customParams кладем параметр
         * GETACTIVEUSERS, чтобы запрос вернул лишь активных пользоватлей
         * системы. Иначе не кладем ничего и запрос вернет всех пользователей
         * системы.
         */
        if (!isNeedShowDeletedUser(filterParams)) {
            customParams.put("GETACTIVEUSERS", "TRUE");
        }
        Map<String, Object> result = null;


        // добавляем ограничение по агентам только если пользователь не "Сотрудник страховой" и не "Робот"
        Object userTypeId = params.get(Constants.SESSIONPARAM_USERTYPEID);
        if ((userTypeId == null) || ((Long.parseLong(userTypeId.toString()) != 1)
                && ((Long.parseLong(userTypeId.toString())) != 4))) {
            customParams.put("CP_DEPARTMENTID", params.get(Constants.SESSIONPARAM_DEPARTMENTID));
        }
        XMLUtil.convertDateToFloat(customParams);
        result = this.selectQuery(
                "dsAdminUsersPartnerList", "dsAdminUsersPartnerCount", customParams
        );
        return result;
    }

    /**
     * Заменить параметр с null значением на значение по умолчанию
     *
     * @param params       map'а в которой следует заменить значение
     * @param paramName    параметр, значение которого, возмножно, следует заменить
     * @param defaultValue значение по умолчанию для параметра
     */
    private void replacementNullParamOnStandartValue(Map<String, Object> params,
                                                     String paramName, Object defaultValue) {
        if (params.get(paramName) == null) {
            params.put(paramName, defaultValue);
        }
    }

    /**
     * Получение списка пользователей по логину
     *
     * @param findLogin логин, который нужно найти
     * @param login     логин, для выполнения сервиса
     * @param password  пароль, для выполнения сервиса
     * @return список Map с информацией о пользователях с похожими логинами
     * @throws Exception
     */
    private List<Map<String, Object>> getUserByLogin(String findLogin, String login, String password)
            throws Exception {
        Map<String, Object> accountFindParams = new HashMap<String, Object>();
        accountFindParams.put("LOGIN", findLogin);
        accountFindParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> accountFindResult = this.callService(
                ADMINWS, "admAccountFind",
                accountFindParams, login, password
        );
        return (List<Map<String, Object>>) accountFindResult.get("Result");
    }

    private boolean isCanEditUser(List<Map<String, Object>> findList, String findLogin, long findUserAccounId) {
        boolean result = false;
        if (findList != null && !findList.isEmpty()) {
            if (findList.size() > 1) {
                for (Map<String, Object> item : findList) {
                    long userAccountId = Long.parseLong(item.get("USERACCOUNTID").toString());
                    String userLogin = item.get("LOGIN").toString();
                    if (userAccountId == findUserAccounId && userLogin.equals(findLogin)) {
                        result = true;
                        break;
                    }
                }
            } else {
                Map<String, Object> user = findList.get(0);
                long userAccountId = Long.parseLong(user.get("USERACCOUNTID").toString());
                result = userAccountId == findUserAccounId;
            }
        } else {
            result = true;
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BAdminCreateUser(Map<String, Object> params)
            throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> accountAddOrUpdateRes = new HashMap<String, Object>();
        // возможно, здесь не потребуется, когда преобразование дат 
        // во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(params, Double.class);
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        String findUserLogin = params.get("LOGIN").toString();
        List<Map<String, Object>> userFoundList = getUserByLogin(findUserLogin, login, password);

        if (params.get("USERID") == null) {
            Result checkPasswordResult = createPasswordVerifier(login, password)
                    .isPasswordValid(params.get("PASSWORD").toString());

            if (checkPasswordResult.equals(Result.OK)) {
                params.put("BLOCKIFINACTIVE", 0);
                params.put("ISCONCURRENT", 1);
                params.put("OBJECTTYPE", 1);
                replacementNullParamOnStandartValue(params, "EMAIL", "");
                replacementNullParamOnStandartValue(params, "PHONE1", "");

                String firstName = params.get("FIRSTNAME").toString().replaceAll(" ", "");
                String lastName = params.get("LASTNAME").toString().replaceAll(" ", "");
                String middleName = params.get("MIDDLENAME").toString().replaceAll(" ", "");
                params.put("FIRSTNAME", firstName);
                params.put("LASTNAME", lastName);
                params.put("MIDDLENAME", middleName);

                if (userFoundList == null
                        || userFoundList.isEmpty()) {
                    accountAddOrUpdateRes = this.callService(
                            ADMINWS, "dsAccountCreate",
                            params, login, password
                    );
                } else {
                    accountAddOrUpdateRes.put("StatusType", "ERROR");
                    accountAddOrUpdateRes.put("Error", "Пользователь с логином " + findUserLogin + " уже существует.");
                    result.put(RESULT, accountAddOrUpdateRes);
                }
            } else {
                accountAddOrUpdateRes.put("StatusType", "ERROR");
                accountAddOrUpdateRes.put("Error", checkPasswordResult.getDescription());
            }

        } else {
            long userEditAccounId = Long.parseLong(params.get("USERACCOUNTID").toString());
            if (isCanEditUser(userFoundList, findUserLogin, userEditAccounId)) {
                params.put("ISCONCURRENT", 1L);
                if (null == params.get("OBJECTTYPE")) {
                    params.put("OBJECTTYPE", 1);
                }
                accountAddOrUpdateRes = this.callService(ADMINWS, "dsAccountUpdate", params, login, password);
            } else {
                accountAddOrUpdateRes.put("StatusType", "ERROR");
                accountAddOrUpdateRes.put("Error", "Пользователь с логином " + findUserLogin + " уже существует.");
                result.put(RESULT, accountAddOrUpdateRes);
            }
        }

        result.put(RESULT, accountAddOrUpdateRes);
        return result;
    }

    @WsMethod(requiredParams = {"STATUS", "QUERYSTATUS"})
    public Map<String, Object> dsB2BAdminUpdateStatusUser(Map<String, Object> params)
            throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String queryStatus = params.get("QUERYSTATUS").toString();
        params.put("ISCONCURRENT", 1);
        String status = params.get("STATUS").toString();
        if (queryStatus.equals("DELETED")) {
            if (status.equalsIgnoreCase("ACTIVE")) {
                params.put("STATUS", "DELETED");
            } else {
                params.put("STATUS", "ACTIVE");
            }
        } else if (status.equalsIgnoreCase("ACTIVE")) {
            params.put("STATUS", "BLOCKED");
        } else {
            params.put("STATUS", "ACTIVE");
        }
        Map<String, Object> accountDelRes = this.callService(
                ADMINWS, "dsAccountUpdateStatus",
                params, login, password
        );
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, accountDelRes);
        return accountDelRes;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BAdminUserChangePasswordAccount(Map<String, Object> params)
            throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> queryResult = new HashMap<String, Object>();
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);

        String OLDPASS = params.get("OLDPASS").toString();
        params.put("OLDPASS", DigestUtils.shaHex(OLDPASS));
        String NEWPASS = params.get("NEWPASS").toString();
        params.put("NEWPASS", DigestUtils.shaHex(NEWPASS));

        Result checkPasswordResult = createPasswordVerifier(login, password)
                .isPasswordValid(NEWPASS);

        if (checkPasswordResult.equals(Result.OK)) {
            queryResult = this.selectQuery(
                    "dsAdminAccountChangePass", params
            );
            result.put(RESULT, queryResult);
        } else {
            queryResult.put("StatusType", "ERROR");
            queryResult.put("Error", checkPasswordResult.getDescription());
        }

        result.put(RESULT, queryResult);
        return result;
    }

    @WsMethod(requiredParams = {"NEWPASS", "ACCID"})
    public Map<String, Object> dsB2BAdminUserChangePasswordAccountWithoutOldPass(Map<String, Object> params)
            throws Exception {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DATE, 30);
        return new HashMap<String, Object>() {{
            put(RESULT, selectQuery("dsAdminAccountChangePassWithoutOldPass", new HashMap<String, Object>() {{
                put("ACCID", params.get("ACCID"));
                put("NEWPASS", DigestUtils.sha512Hex(((String) params.get("NEWPASS"))));
                put("PWDEXPDATE", XMLUtil.convertDate(calendar.getTime()));
            }}));
        }};
    }

    private Map<String, Object> addUserRole(String roleSysName, Long userId) {
        Map<String, Object> Result = new HashMap<String, Object>();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        return Result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BAdminCommonCreateUser(Map<String, Object> params)
            throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> accountAddOrUpdateRes = new HashMap<String, Object>();
        // возможно, здесь не потребуется, когда преобразование дат 
        // во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(params, Double.class);
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        String findUserLogin = params.get("LOGIN").toString();
        List<Map<String, Object>> userFoundList = getUserByLogin(findUserLogin, login, password);

        if (params.get("USERID") == null) {
            Result checkPasswordResult = createPasswordVerifier(login, password)
                    .isPasswordValid(params.get("PASSWORD").toString());

            if (checkPasswordResult.equals(Result.OK)) {
                params.put("BLOCKIFINACTIVE", 0);
                params.put("ISCONCURRENT", 1);
                params.put("OBJECTTYPE", 1);
                replacementNullParamOnStandartValue(params, "EMAIL", "");
                replacementNullParamOnStandartValue(params, "PHONE1", "");

                String firstName = params.get("FIRSTNAME").toString().replaceAll(" ", "");
                String lastName = params.get("LASTNAME").toString().replaceAll(" ", "");
                String middleName = params.get("MIDDLENAME").toString().replaceAll(" ", "");
                params.put("FIRSTNAME", firstName);
                params.put("LASTNAME", lastName);
                params.put("MIDDLENAME", middleName);

                if (userFoundList == null
                        || userFoundList.isEmpty()) {
                    accountAddOrUpdateRes = this.callService(
                            ADMINWS, "dsAccountCreate",
                            params, login, password
                    );
                } else {
                    accountAddOrUpdateRes.put("StatusType", "ERROR");
                    accountAddOrUpdateRes.put("Error", "Пользователь с логином " + findUserLogin + " уже существует.");
                    result.put(RESULT, accountAddOrUpdateRes);
                }
            } else {
                accountAddOrUpdateRes.put("StatusType", "ERROR");
                accountAddOrUpdateRes.put("Error", checkPasswordResult.getDescription());
            }

        } else {
            long userEditAccounId = Long.parseLong(params.get("USERACCOUNTID").toString());
            if (isCanEditUser(userFoundList, findUserLogin, userEditAccounId)) {
                params.put("ISCONCURRENT", 1L);
                params.putIfAbsent("OBJECTTYPE", 1);
                accountAddOrUpdateRes = this.callService(ADMINWS, "dsAccountUpdate", params, login, password);
            } else {
                accountAddOrUpdateRes.put("StatusType", "ERROR");
                accountAddOrUpdateRes.put("Error", "Пользователь с логином " + findUserLogin + " уже существует.");
                result.put(RESULT, accountAddOrUpdateRes);
            }
        }

        result.put(RESULT, accountAddOrUpdateRes);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BAdminCommonResetPassword(Map<String, Object> params)
            throws Exception {
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> queryResult = new HashMap<>();
        StringBuilder log = new StringBuilder("попытка сброса пароля, ");
        log.append(params.get(B2BPosServiceSessionController.B2B_USERLOGIN_PARAMNAME)).append("'. ");

        String OLDPASS = params.get("OLDPASS").toString();
        params.put("OLDPASS", DigestUtils.shaHex(OLDPASS));
        PasswordStrengthVerifier passwordStrengthVerifier = createPasswordVerifier(login, password);
        String NEWPASS = passwordStrengthVerifier.generatePassword();

        Result checkPasswordResult = passwordStrengthVerifier
                .isPasswordValid(NEWPASS);

        if (checkPasswordResult.equals(Result.OK)) {
            log.append("сгенерированный пароль прошел проверку надежности и был сменен.");
            queryResult = this.selectQuery("dsAdminAccountChangePass", params);
            result.put(RESULT, queryResult);
        } else {
            queryResult.put("StatusType", "ERROR");
            queryResult.put("Error", checkPasswordResult.getDescription());
            log.append("сгенерированный пароль не прошел проверку надежности.");
        }
        logToAuditForGroups(params, log, new String[]{"SB1"});
        result.put(RESULT, queryResult);
        return result;
    }

    /**
     * Метод востановления пароля.
     * Ищем пользовтаеля по логину и email и генерим новый пароль отправляя его на почту
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"LOGIN", "EMAIL"})
    public Map<String, Object> dsResetPassword(Map<String, Object> params) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        String login = getStringParam(params, "LOGIN");
        queryParams.put("LOGIN", login);
        String email = getStringParam(params, "EMAIL");
        queryParams.put("EMAIL", email.toUpperCase());
        // ищем пользователя с этим мэйлом и логином
        Map<String, Object> queryResult = this.selectQuery("findUserByLoginAndEmail", queryParams);
        List<Map<String, Object>> resultList = getListFromResultMap(queryResult);
        logger.info("checking logins");
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> foundUser = null;
        if (resultList != null && !resultList.isEmpty()) {
            foundUser = resultList.get(0);
        }
        if (foundUser != null && (login.equalsIgnoreCase(getStringParam(foundUser, "LOGIN")))) {
            logger.info("logins are equal");
            foundUser.put("NEWMESSAGE", true);
            // если пользователь есть, то меняем ему пароль
            logger.info("changing passwords and sending sms");
            foundUser.put(WsConstants.LOGIN, params.get(WsConstants.LOGIN));
            foundUser.put(WsConstants.PASSWORD, params.get(WsConstants.PASSWORD));
            result = dsB2BAdminCommonResetPasswordEx(foundUser);
        } else {
            logger.info("logins are not correct");
            result.put("Status", "ERROR");
            result.put("Error", "По заданным параметрам пользователь не найден.");
        }
        return result;
    }

    /**
     * Метод менящий пользователю пароль и отправляющий новый пароль на пусту или телефон.
     * Параметр passwordSendType: 1 - почта; 2 - телефон.
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"USERACCOUNTID"})
    public Map<String, Object> dsB2BAdminCommonResetPasswordEx(Map<String, Object> params)
            throws Exception {
        Map<String, Object> qParams = new HashMap<>();
        String loginForCall = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        Long passwordSendType = getLongParamWithDefaultValue(params, "passwordSendType", 1L);
        qParams.put("USERACCOUNTID", params.get("USERACCOUNTID"));
        qParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> queryResult = this.callExternalService(ADMINWS, "admaccountbyid", qParams, loginForCall, password);
        if ((null != queryResult) && (null != queryResult.get("PASSHASH"))) {
            params.put("OLDPASS", queryResult.get("PASSHASH"));
            params.put("ACCID", params.get("USERACCOUNTID"));
        } else {
            queryResult = new HashMap<>();
            queryResult.put("StatusType", "ERROR");
            queryResult.put("Error", "Не возможно сбросить пароль");
        }

        PasswordStrengthVerifier passwordStrengthVerifier = createPasswordVerifier(loginForCall, password);
        String newPass = passwordStrengthVerifier.generatePassword();
        Result checkPasswordResult = passwordStrengthVerifier
                .isPasswordValid(newPass);

        if (checkPasswordResult.equals(Result.OK)) {
            String login = getStringParam(params, "LOGIN");
            String totpSalt = "";
            try {
                Date dateToSalt = getDateParam(queryResult.get("CREATIONDATE"));
                //keyGenerator.
                if (totp != null) {
                    totpSalt = totp.generateSaltBaseOnLoginAndTotp(login, dateToSalt);
                }
            } catch (Exception e) {
                throw new SoftServiceErrorException(
                        "dsB2BAdminCommonResetPasswordEx", "500", "Ошибка генерации соли пароля.", e.getMessage());
            }
            params.put("NEWPASS", DigestUtils.sha512Hex(newPass + totpSalt));
            params.put("PWDEXPDATE", new Date());
            XMLUtil.convertDateToFloat(params);
            queryResult = this.selectQuery("dsAdminAccountChangePass", params);
            if (1 == passwordSendType) {
                String email = getStringParam(params, "EMAIL");
                if (getBooleanParam(params, "NEWMESSAGE", false)) {
                    sendNewEmail(email, login, newPass, loginForCall, password);
                } else {
                    sendEmail(email, login, newPass, loginForCall, password);
                }
            } else if (2 == passwordSendType) {
                Config config = Config.getConfig(Constants.B2BPOSWS);
                String smsText = config.getParam("PASSWORDCHANGETEXT", "Уважаемый пользователь! Ваш пароль: ");
                String smsToNumber = getStringParam(params, "PHONE1");
                if (smsToNumber.length() == 10) {
                    smsToNumber = "7".concat(smsToNumber);
                }
                SmsSender smsSender = new SmsSender();
                Map<String, Object> sendResult = smsSender.sendSms(smsToNumber, (smsText + newPass));
                logger.debug("Sending informational SMS about contract attachment finished with result: " + sendResult);
            }
        } else {
            queryResult.put("StatusType", "ERROR");
            queryResult.put("Error", checkPasswordResult.getDescription());
        }
        Map<String, Object> result = new HashMap<>();
        result.put(RESULT, queryResult);
        return result;
    }

    private void sendEmail(String email, String ulogin, String newPassword, String login, String password) {
        Map<String, Object> sendParams = new HashMap<>();
        sendParams.put("SMTPSubject", "Доступ к b2b");
        Config config = Config.getConfig(Constants.B2BPOSWS);
        String emailText = config.getParam("PASSWORDCHANGETEXT", "Уважаемый пользователь!\n");
        String projectName = config.getParam("projectNameForChangePass", "b2blifecore");
        sendParams.put("SMTPMESSAGE", emailText + "Ваш логин: " + ulogin + "\nВаш пароль: " + newPassword
                + "\nОсуществить вход Вы можете по ссылке " + projectName);
        sendParams.put("SMTPReceipt", email);
        if (isAllEmailValid(email)) {
            logger.debug("sendParams = " + sendParams.toString());
            try {
                sendEmail(login, password, sendParams);
                logger.debug("mailSendSuccess");
            } catch (Exception e) {
                logger.debug("mailSendException: ", e);
            }
        }
    }

    private void sendNewEmail(String email, String ulogin, String newPassword, String login, String password) {
        Map<String, Object> sendParams = new HashMap<>();
        logger.info("password changed, sending email");
        Config config = Config.getConfig(Constants.B2BPOSWS);
        String projectName = config.getParam("projectNameForChangePass", "b2blifecore");
        sendParams.put("SMTPSubject", "Восстановление пароля в проекте: " + projectName);
        String urlProject = config.getParam("urlProjectForChangePass", "b2blifecore.ru");
        String emailHeader = String.format("Уважаемый пользователь!%nНа сайте %1$s был сделан запрос восстановить пароль "
                + "к Вашему аккаунту.%nПроследуйте по ссылке %1$s и введите следующие данные:%n", urlProject);
        String adminEmail = config.getParam("adminEmailForChangePass", "какая_то_почта@домен.ru");
        String emailFooter = String.format("%nЕсли Вы не делали такой запрос, необходимо проинформировать "
                + "службу поддержки, написав письмо на адрес %s%n", adminEmail);
        sendParams.put("SMTPMESSAGE", emailHeader + "Ваш логин: " + ulogin + "\nВаш пароль: " + newPassword + emailFooter);
        sendParams.put("SMTPReceipt", email);
        if (isAllEmailValid(email)) {
            logger.debug("sendParams = " + sendParams.toString());
            try {
                sendEmail(login, password, sendParams);
                logger.info("email was sent");
                logger.debug("mailSendSuccess");
            } catch (Exception e) {
                logger.debug("mailSendException: ", e);
            }
        }
    }

    private void sendEmail(String login, String password, Map<String, Object> sendParams) throws Exception {
        Map<String, Object> sendRes = this.callService(Constants.WEBSMSWS, EMAIL_METHOD_NAME, sendParams, login, password);
        if (!RET_STATUS_OK.equalsIgnoreCase(getStringParam(sendRes, RESULT))) {
            sendRes = this.callService(Constants.WEBSMSWS, EMAIL_METHOD_NAME, sendParams, login, password);
            if (!RET_STATUS_OK.equalsIgnoreCase(getStringParam(sendRes, RESULT))) {
                sendRes = this.callService(Constants.WEBSMSWS, EMAIL_METHOD_NAME, sendParams, login, password);
                if (!RET_STATUS_OK.equalsIgnoreCase(getStringParam(sendRes, RESULT))) {
                    sendRes = this.callService(Constants.WEBSMSWS, EMAIL_METHOD_NAME, sendParams, login, password);
                    if (!RET_STATUS_OK.equalsIgnoreCase(getStringParam(sendRes, RESULT))) {
                        sendRes = this.callService(Constants.WEBSMSWS, EMAIL_METHOD_NAME, sendParams, login, password);
                    }
                }
            }
        }
    }

    @WsMethod(requiredParams = {"password"})
    public Map<String, Object> dsB2BCheckPasswordStrength(Map<String, Object> params) {
        String login = getStringParam(params, WsConstants.LOGIN);
        String passwordForCall = getStringParam(params, WsConstants.PASSWORD);
        String password = getStringParam(params, "password");
        PasswordStrengthDescriptor passwordStrengthErrorCollector = new PasswordStrengthDescriptor(createPasswordVerifier(login, passwordForCall));
        List<Result> checkPasswordResult = passwordStrengthErrorCollector.checkErrors(password);
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        for (Result error : checkPasswordResult) {
            errors.add(error.getDescription());
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("errorList", errors);
        result.put(RESULT, resultMap);
        return result;
    }

    @WsMethod(requiredParams = {"USERACCOUNTID", "PWDEXPDATE"})
    public Map<String, Object> dsB2BAdminAccountChangePasswordExpDate(Map<String, Object> params) throws Exception {
        return new HashMap<String, Object>() {{
            put(RESULT, selectQuery("dsAdminAccountChangePasswordExpDate", params));
        }};
    }

    /**
     * Сервис поиска пользователя по ADUSERPRINCIPALNAME - логин пользователя для авторизации в Active Directory
     * в формате login@domen и ADUSERLOGIN - логин пользователя в Active Directory
     *
     * @param params <UL>
     *               <LI>ADUSERPRINCIPALNAME - логин пользователя для авторизации в Active Directory</LI>
     *               <LI>DUSERLOGIN - логин пользователя в Active Directory</LI>
     *               </UL>
     * @return список пользователей из таблицы CORE_USERACCOUNT
     * каждый элемент списка содержит следующие значения
     * <UL>
     * <LI>USERACCOUNTID - идентификатор пользователя</LI>
     * <LI>LOGIN - b2b логин пользователя</LI>
     * <LI>ADUSERPRINCIPALNAME - логин пользователя для авторизации в Active Directory</LI>
     * <LI>ADUSERLOGIN - логин пользователя в Active Directory</LI>
     * </UL>
     * @throws Exception ошибка выполнения запроса
     */
    @WsMethod()
    public Map<String, Object> searchUserAccountByActiveDirectoryLinkFields(Map<String, Object> params) throws Exception {
        logger.debug(String.format("Begin method searchUserAccountByActiveDirectoryLinkFields with params: %s", params));
        Map<String, Object> result = this.selectQuery("searchUserAccountByActiveDirectoryLinkFields", params);
        logger.debug(String.format("End method searchUserAccountByActiveDirectoryLinkFields result: %s", result));
        return result;
    }

    private PasswordStrengthVerifier createPasswordVerifier(String login, String password) {
        try {
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> passwordSetting = callExternalService(ADMINWS, "getPwdAcctSettings", params, login, password);
            int minLen = getIntegerParam(passwordSetting, PWD_MIN_LEN);
            if (minLen == 0) {
                minLen = 6;
            }
            int maxLen = getIntegerParam(passwordSetting, PWD_MAX_LEN);
            if (maxLen == 0) {
                maxLen = 40;
            }
            int groupsCount = getIntegerParam(passwordSetting, PWD_GROUPS_COUNT);
            if (groupsCount == 0) {
                groupsCount = 40;
            }
            int idtSymCount = getIntegerParam(passwordSetting, PWD_IDT_SYM_COUNT);
            if (idtSymCount == 0) {
                idtSymCount = 40;
            }
            boolean additionalAllowed = getBooleanParam(passwordSetting, PWD_GROUPS_ADDITIONAL_ALLOWED, false);
            return new PasswordStrengthVerifier(minLen, maxLen, idtSymCount, groupsCount,
                    additionalAllowed ? PasswordStrengthVerifier.SET_ADDITIONAL
                            : PasswordStrengthVerifier.SET_ASCII_ALPHANUM);
        } catch (Exception ex) {
            logger.error("Error while creating PasswordStrengthVerifier: " + ex.getMessage(), ex);
            return new PasswordStrengthVerifier(6, 40, 2, 3, PasswordStrengthVerifier.SET_ADDITIONAL, PWD_DEF_SYMBOLS_LIST);
        }
    }
}
