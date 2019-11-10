/*
 * Copyright (c) Diasoft 2004-2014
 */
package com.bivgroup.services.b2bposws.facade.pos.custom;

import com.bivgroup.services.b2bposws.system.Constants;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

/**
 * Кастомный фасад для работы с аккаунтами пользователей системы
 *
 * @author averichevsm
 */
@BOName("UserAccountCustom")
public class B2BUserAccountCustomFacade extends BaseFacade {

    public static void joinStackTrace(Throwable e, StringWriter writer) {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(writer);

            while (e != null) {

                printer.println(e);
                StackTraceElement[] trace = e.getStackTrace();
                for (int i = 0; i < trace.length; i++) {
                    printer.println("\tat " + trace[i]);
                }

                e = e.getCause();
                if (e != null) {
                    printer.println("Caused by:\r\n");
                }
            }
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }

    public static String joinStackTrace(Throwable e) {
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            joinStackTrace(e, writer);
            return writer.toString();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                    // ignore
                }
            }
        }
    }

    private void addError(List<Map<String, Object>> errorsList, String errorMessage) throws Exception {
        Map<String, Object> errorMap = new HashMap<String, Object>();
        errorMap.put("ERRORMESSAGE", errorMessage);
        errorsList.add(errorMap);
    }

    private Date getDate(Object _date) throws Exception {
        if ((_date != null) && (!_date.toString().isEmpty()) && (!_date.toString().equalsIgnoreCase(" "))) {
            if (_date.getClass().getName().equals("java.util.Date")) {
                return (Date) _date;
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    return formatter.parse(_date.toString());
                } catch (Exception e) {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    Long getLongValue(Object value) {
        if ((value != null) && (!value.toString().isEmpty()) && (!value.toString().equalsIgnoreCase(" "))) {
            try {
                double val = Double.valueOf(value.toString()).doubleValue();
                int valInt = (int) val;
                return Long.valueOf(valInt);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    Long getLongValueFromBoolean(Object value) {
        if (value != null) {
            if (value.toString().equalsIgnoreCase("Да")) {
                return 1L;
            } else if (value.toString().equalsIgnoreCase("Нет")) {
                return 0L;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private Long findDepartment(String deptCode, String login, String password) throws Exception {
        Map<String, Object> deptParams = new HashMap<String, Object>();
        deptParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        deptParams.put("DEPTCODE", deptCode);
        Map<String, Object> accountRes = this.callService(WsConstants.INSPOSWS, "dsBranchBrowseListByParamEx", deptParams, login, password);
        if (accountRes.get("DEPARTMENTID") != null) {
            return Long.valueOf(accountRes.get("DEPARTMENTID").toString());
        } else {
            return null;
        }
    }

    private String getDeptCode(Object deptValue) throws Exception {
        if ((deptValue != null) && (!deptValue.toString().isEmpty()) && (!deptValue.toString().equals(" "))) {
            if (deptValue.getClass().getName().equals("java.lang.Double")) {
                return getLongValue(deptValue).toString();
            } else {
                return deptValue.toString();
            }
        } else {
            return null;
        }
    }

    private void addRole(Long userAccountId, Long roleId, String login, String password) throws Exception {
        Map<String, Object> roleParams = new HashMap<String, Object>();
        roleParams.put("USERACCOUNTID", userAccountId);
        roleParams.put("ROLEID", roleId);
        try {
            Map<String, Object> roleRes = this.callService(WsConstants.ADMINWS, "admroleuseradd", roleParams, login, password);
        } catch (Exception e) {
        }
    }

    private Map<String, Object> getRoleBySysName(String roleSysName, String login, String password) throws Exception {
        Map<String, Object> roleParams = new HashMap<String, Object>();
        roleParams.put("SEARCHTEXT", roleSysName);
        roleParams.put("SEARCHCOLUMN", "ROLESYSNAME");
        Map<String, Object> result = this.callService(WsConstants.ADMINWS, "admUserRole", roleParams, login, password);
        return result;
    }

    private boolean isPartner(Long branchId, String login, String password) throws Exception {
        Long agencyNetworkId = findDepartment("agencyNetwork", login, password);
        if (agencyNetworkId != null) {
            Map<String, Object> depLvlParams = new HashMap<String, Object>();
            depLvlParams.put(RETURN_AS_HASH_MAP, "TRUE");
            depLvlParams.put("OBJECTID", branchId);
            depLvlParams.put("PARENTID", agencyNetworkId);
            Map<String, Object> qRes = this.callService(WsConstants.INSPOSWS, "dsInsuranceDepartmentLevelBrowseListByParamEx", depLvlParams, login, password);
            if (qRes.get("OBJECTID") != null) {
                return true;
            }
        }
        return false;
    }

    private void importAccount(Map<String, Object> accountMap, Long sellerTypeId, List<Map<String, Object>> errorsList, String login, String password) throws Exception {
        Long departmentId = null;
        String departmentCode = getDeptCode(accountMap.get("departmentcode"));
        if (departmentCode != null) {
            departmentId = findDepartment(departmentCode, login, password);
        }
        Long branchId = null;
        String branchCode = getDeptCode(accountMap.get("branchcode"));
        if (branchCode != null) {
            branchId = findDepartment(branchCode, login, password);
        }
        String fioFull = accountMap.get("lastname").toString();
        String fioFullOld = accountMap.get("lastname").toString();
        fioFull = fioFull.trim();
        if (accountMap.get("firstname") != null) {
            fioFull = fioFull + " " + accountMap.get("firstname").toString();
            fioFullOld = fioFullOld + " " + accountMap.get("firstname").toString();
            fioFull = fioFull.trim();
        }
        if (accountMap.get("middlename") != null) {
            fioFull = fioFull + " " + accountMap.get("middlename").toString();
            fioFullOld = fioFullOld + " " + accountMap.get("middlename").toString();
            fioFull = fioFull.trim();
        }
        if (departmentId == null) {
            addError(errorsList, String.format("Не найдено подразделение с кодом %s для пользователя %s", departmentCode, fioFull));
        } else if (branchId == null) {
            addError(errorsList, String.format("Не найден филиал с кодом %s для пользователя %s", branchCode, fioFull));
        } else {
            // перед созданием пробуем найти
            Map<String, Object> accountFindParams = new HashMap<String, Object>();
            String ulogin = accountMap.get("userLogin").toString();
            String uloginOld = accountMap.get("userLogin").toString();

            ulogin = ulogin.trim();
            Long userAccountIdDeleted = null;
            if (!ulogin.equals(uloginOld)) {
                //если трим убил висячий пробел, то надо проверить наличие уже созданного аккаунта с таким логином. и грохнуть его.
                accountFindParams.put("LOGIN", uloginOld);
                accountFindParams.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> accountFindRes1 = this.callService(WsConstants.ADMINWS, "admAccountFind", accountFindParams, login, password);
                if (accountFindRes1.get(RESULT) != null) {
                    if (!((List<Map<String, Object>>) accountFindRes1.get(RESULT)).isEmpty()) {
                        accountFindRes1 = ((List<Map<String, Object>>) accountFindRes1.get(RESULT)).get(0);
                        if (accountFindRes1.get("USERACCOUNTID") != null) {
                            Map<String, Object> accDelParam = new HashMap<String, Object>();
                            accDelParam.put("USERACCOUNTID", accountFindRes1.get("USERACCOUNTID"));
                            userAccountIdDeleted = Long.valueOf(accountFindRes1.get("USERACCOUNTID").toString());
                            this.callService(WsConstants.ADMINWS, "admAccountRemove", accDelParam, login, password);
                        }
                    }
                }

            }
            accountFindParams.put("LOGIN", ulogin);
            accountFindParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> accountFindRes1 = this.callService(WsConstants.ADMINWS, "admAccountFind", accountFindParams, login, password);
            if (accountFindRes1.get(RESULT) != null) {
                if (!((List<Map<String, Object>>) accountFindRes1.get(RESULT)).isEmpty()) {
                    accountFindRes1 = ((List<Map<String, Object>>) accountFindRes1.get(RESULT)).get(0);
                    if (accountFindRes1.get("USERACCOUNTID") != null) {
                        Map<String, Object> accDelParam = new HashMap<String, Object>();
                        accDelParam.put("USERACCOUNTID", accountFindRes1.get("USERACCOUNTID"));
                        userAccountIdDeleted = Long.valueOf(accountFindRes1.get("USERACCOUNTID").toString());
                        this.callService(WsConstants.ADMINWS, "admAccountRemove", accDelParam, login, password);
                    }
                }
            }
            accountFindParams.put("LOGIN", ulogin);
            accountFindParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> accountFindRes = this.callService(WsConstants.ADMINWS, "admAccountFind", accountFindParams, login, password);
            if (accountFindRes.get(RESULT) != null) {
                if (!((List<Map<String, Object>>) accountFindRes.get(RESULT)).isEmpty()) {
                    accountFindRes = ((List<Map<String, Object>>) accountFindRes.get(RESULT)).get(0);
                }
            }
            Map<String, Object> accountParams = new HashMap<String, Object>();
            String accountMethodName = "dsAccountCreate";
            if (accountFindRes.get("USERACCOUNTID") != null) {
                // аккаунт уже существует. необходимо проверить и обновить связи
                accountParams.put("USERACCOUNTID", accountFindRes.get("USERACCOUNTID"));
                accountMethodName = "dsAccountUpdate";
            }

            String upass = accountMap.get("userPassword").toString();
            upass = upass.trim();
            accountParams.put("NOTINLIST", Boolean.TRUE);
            accountParams.put("LOGIN", ulogin);
            accountParams.put("PASSWORD", upass);
            accountParams.put("USERTYPE", "employee");
            boolean partner = isPartner(branchId, login, password);
            if (!partner) {
                accountParams.put("OBJECTTYPE", 1L);
            } else {
                accountParams.put("OBJECTTYPE", 2L);
            }
            accountParams.put("FIRSTNAME", accountMap.get("firstname"));
            accountParams.put("MIDDLENAME", accountMap.get("middlename"));
            accountParams.put("LASTNAME", accountMap.get("lastname"));
            accountParams.put("EMAIL", accountMap.get("email"));
            accountParams.put("PHONE1", accountMap.get("phone"));
            accountParams.put("ISCONCURRENT", Integer.valueOf(1));
            accountParams.put("BLOCKED", Boolean.FALSE);
            accountParams.put("BLOCKIFINACTIVE", Boolean.FALSE);
            accountParams.put("LOCALE", "ru");
            accountParams.put("DEFAULT_PROJECT_SYSNAME", "insurance");
            accountParams.put("DEPARTMENTID", departmentId);
            accountParams.put("USERLOGIN", ulogin);
            accountParams.put("TZTYPE", Integer.valueOf(1));
            Map<String, Object> accountRes = this.callService(WsConstants.ADMINWS, accountMethodName, accountParams, login, password);
            if ((accountRes.get("USERACCOUNTID") != null) || (accountFindRes.get("USERACCOUNTID") != null)) {
                Long userAccountId = null;
                if (accountFindRes.get("USERACCOUNTID") != null) {
                    userAccountId = Long.valueOf(accountFindRes.get("USERACCOUNTID").toString());
                } else {
                    userAccountId = Long.valueOf(accountRes.get("USERACCOUNTID").toString());
                }
                Long userId = null;
                if (accountFindRes.get("USERID") != null) {
                    userId = Long.valueOf(accountFindRes.get("USERID").toString());
                } else {
                    userId = Long.valueOf(accountRes.get("USERID").toString());
                }
                Long employeeId = null;
                if (accountFindRes.get("USERACCOUNTID") != null) {
                    employeeId = Long.valueOf(accountFindRes.get("OBJECTID").toString());
                } else {
                    employeeId = Long.valueOf(accountRes.get("EMPLOYEEID").toString());
                }
                if (accountMap.get("userRoles") != null) {
                    addRolesBySysNameList(userAccountId, accountMap.get("userRoles").toString(), errorsList, fioFull, login, password);
                }
                // костыль, т.к. админский сервис не сохраняет OBJECTTYPE
                if (partner) {
                    Map<String, Object> temp = new HashMap<String, Object>();
                    temp.put("USERID", userId);
                    temp.put("OBJECTTYPE", 2L);
                    this.callService(Constants.B2BPOSWS, "dsCoreUserUpdate", temp, login, password);
                }
                /*  if (employeeId != null) {
                        Map<String, Object> empParam = new HashMap<String, Object>();
                        empParam.put(PAGE, upass)
                        depEmployeeUpdate(accountParams);
                    }
                    if (userId != null) {
                        coreUserUpdate(accountParams);
                    }
                    if (userAccountId != null) {
                        coreUserAccountUpdate(accountParams);
                    }
                 */
 /*                    addRole(userAccountId, 1005L, login, password);
                     Long isInsPosManager = getLongValueFromBoolean(accountMap.get("isInsPosManager"));
                     if ((isInsPosManager != null) && (isInsPosManager.longValue() == 1)) {
                     addRole(userAccountId, 1019L, login, password);
                     }*/

 /*Map<String, Object> sellerFindParams = new HashMap<String, Object>();
                    // находим селлера по фамилии, потом бегаем по списку однофамильцев, проверяем их имя отчество.
                    // при совпадении, удаляем, после пробуем найти по fullFio. если все ок то не находим, и создаем нового продавца.
                    sellerFindParams.put("NAME", accountMap.get("lastname").toString().trim());
                    Map<String, Object> sellerFindRes1 = this.callService(WsConstants.INSPOSWS, "dsSellerBrowseListByParamEx", sellerFindParams, login, password);
                    if (sellerFindRes1.get(RESULT) != null) {
                        List<Map<String, Object>> sellerList = (List<Map<String, Object>>) sellerFindRes1.get(RESULT);
                        for (Map<String, Object> seller : sellerList) {
                            if (seller.get("NAME").toString().toUpperCase().indexOf(accountMap.get("firstname").toString().toUpperCase().trim()) >= 0) {
                                if (accountMap.get("middlename") != null) {
                                    if (!accountMap.get("middlename").toString().trim().isEmpty()) {
                                        if (seller.get("NAME").toString().toUpperCase().indexOf(accountMap.get("middlename").toString().toUpperCase().trim()) >= 0) {
                                            // совпадение - надо удалять
                                            Map<String, Object> selDelPar = new HashMap<String, Object>();
                                            selDelPar.put("SELLERID", seller.get("SELLERID"));
                                            this.callService(WsConstants.INSPOSWS, "dsSellerDelete", selDelPar, login, password);
                                            Map<String, Object> empsellerFindParams = new HashMap<String, Object>();
                                            empsellerFindParams.put("SELLERID", seller.get("SELLERID"));
                                            Map<String, Object> empsellerFindRes = this.callService(WsConstants.INSPOSWS, "dsEmployeeSellerBrowseListByParam", empsellerFindParams, login, password);
                                            if (empsellerFindRes.get(RESULT) != null) {
                                                List<Map<String, Object>> empSelList = (List<Map<String, Object>>) empsellerFindRes.get(RESULT);
                                                for (Map<String, Object> empSel : empSelList) {
                                                    Map<String, Object> empselDelPar = new HashMap<String, Object>();
                                                    empselDelPar.put("EMPSELLERID", empSel.get("EMPSELLERID"));
                                                    this.callService(WsConstants.INSPOSWS, "dsEmployeeSellerDelete", empselDelPar, login, password);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    //отчества нет удаляем если у продавца тоже нет - удаляем.
                                    String n = seller.get("NAME").toString();
                                    String[] nArr = n.split(" ");
                                    String[] nArrnoEmpty;
                                    int i = 0;
                                    for (String nArr1 : nArr) {
                                        if (!nArr1.isEmpty()) {
                                            i++;
                                        }
                                    }
                                    if (i == 2) {
                                        // у продавца только имя и фамилия. удаляем его.
                                        Map<String, Object> selDelPar = new HashMap<String, Object>();
                                        selDelPar.put("SELLERID", seller.get("SELLERID"));
                                        this.callService(WsConstants.INSPOSWS, "dsSellerDelete", selDelPar, login, password);
                                        Map<String, Object> empsellerFindParams = new HashMap<String, Object>();
                                        empsellerFindParams.put("SELLERID", seller.get("SELLERID"));
                                        Map<String, Object> empsellerFindRes = this.callService(WsConstants.INSPOSWS, "dsEmployeeSellerBrowseListByParam", empsellerFindParams, login, password);
                                        if (empsellerFindRes.get(RESULT) != null) {
                                            List<Map<String, Object>> empSelList = (List<Map<String, Object>>) empsellerFindRes.get(RESULT);
                                            for (Map<String, Object> empSel : empSelList) {
                                                Map<String, Object> empselDelPar = new HashMap<String, Object>();
                                                empselDelPar.put("EMPSELLERID", empSel.get("EMPSELLERID"));
                                                this.callService(WsConstants.INSPOSWS, "dsEmployeeSellerDelete", empselDelPar, login, password);
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }

                    sellerFindParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
                    sellerFindParams.put("NAME", fioFull);
                    Map<String, Object> sellerFindRes = this.callService(WsConstants.INSPOSWS, "dsSellerBrowseListByParamEx", sellerFindParams, login, password);
                    Map<String, Object> sellerParams = new HashMap<String, Object>();
                    String sellerMethodName = "dsSellerCreate";
                    if (sellerFindRes.get("SELLERID") != null) {
                        sellerParams.put("SELLERID", sellerFindRes.get("SELLERID"));
                        sellerMethodName = "dsSellerUpdate";
                    }
                    sellerParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
                    sellerParams.put("NAME", fioFull);
                    sellerParams.put("SELLERTYPEID", sellerTypeId);
                    sellerParams.put("ISEMPLOYEE", 1L);
                    sellerParams.put("SELFORGSTRUCTID", departmentId);
                    sellerParams.put("BRANCHID", branchId);
                    sellerParams.put("ISSIGNATORY", Boolean.FALSE);
                    Map<String, Object> sellerRes = this.callService(WsConstants.INSPOSWS, sellerMethodName, sellerParams, login, password);
                    if (sellerRes.get("SELLERID") != null) {
                        Map<String, Object> empsellerFindParams = new HashMap<String, Object>();
                        empsellerFindParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
                        empsellerFindParams.put("SELLERID", sellerRes.get("SELLERID"));
                        Map<String, Object> empsellerFindRes = this.callService(WsConstants.INSPOSWS, "dsEmployeeSellerBrowseListByParam", empsellerFindParams, login, password);
                        Map<String, Object> sellerEmpParams = new HashMap<String, Object>();
                        String empsellerMethodName = "dsEmployeeSellerCreate";
                        if (empsellerFindRes.get("EMPSELLERID") != null) {
                            sellerEmpParams.put("EMPSELLERID", empsellerFindRes.get("EMPSELLERID"));
                            empsellerMethodName = "dsEmployeeSellerUpdate";
                        }

                        sellerEmpParams.put("EMPLOYEEID", accountRes.get("EMPLOYEEID"));
                        sellerEmpParams.put("SELLERID", sellerRes.get("SELLERID"));
                        sellerEmpParams.put("SELFORGSTRUCTID", departmentId);
                        sellerEmpParams.put("BRANCHID", branchId);
                        Map<String, Object> sellerEmpRes = this.callService(WsConstants.INSPOSWS, empsellerMethodName, sellerEmpParams, login, password);
                        Map<String, Object> distribFindParams = new HashMap<String, Object>();
                        distribFindParams.put("SELLERID", sellerRes.get("SELLERID"));
                        Map<String, Object> distribFindRes = this.callService(WsConstants.INSPRODUCTWS, "dsProductDistributionBrowseListByParam", distribFindParams, login, password);
                        if (distribFindRes.get(RESULT) != null) {
                            List<Map<String, Object>> distrList = (List<Map<String, Object>>) distribFindRes.get(RESULT);
                            for (Map<String, Object> distr : distrList) {
                                Map<String, Object> delParam = new HashMap<String, Object>();
                                delParam.put("PRODDISTRID", distr.get("PRODDISTRID"));
                                this.callService(WsConstants.INSPRODUCTWS, "dsProductDistributionDelete", delParam, login, password);
                            }
                        }
                        //createProductDistribution(accountMap, sellerRes, "1", errorsList, fioFull, login, password);
                        //createProductDistribution(accountMap, sellerRes, "2", errorsList, fioFull, login, password);

                    } else {
                        addError(errorsList, String.format("Ошибка создания продавца для пользователя %s: %s", fioFull, sellerRes.get("Error").toString()));
                    }*/
            } else {
                addError(errorsList, String.format("Ошибка создания аккаунта пользователя %s: %s", fioFull, accountRes.get("Error").toString()));
            }

        }
    }

    // обновить должность
    private Map<String, Object> depEmployeeUpdate(Map<String, Object> params) throws Exception {
        this.updateQuery("", params);
        return null;
    }

    // обновить дату истечения пароля на 1год от текущей даты
    private Map<String, Object> coreUserAccountUpdate(Map<String, Object> params) throws Exception {

        return null;
    }

    // обновить тип пользователя на клиента для партнеров
    private Map<String, Object> coreUserUpdate(Map<String, Object> params) throws Exception {

        return null;
    }

    /*
     * Импорт аккаунтов пользователей
     * @author ilich
     * @param params
     * <UL>
     * <LI>TEMPLATEFILENAME - Наименование файла шаблона импорта</LI>
     * <LI>DATAFILENAME - Наименование файла с данными аккаунтов</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ERRORSLIST - Список ошибок</LI>
     * <LI>ERRORSCOUNT - Количество ошибок</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEFILENAME", "DATAFILENAME"})
    public Map<String, Object> dsB2BUserAccountImport(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Map<String, Object>> errorsList = new ArrayList<Map<String, Object>>();
        Map<String, Object> importParams = new HashMap<String, Object>();
        importParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        importParams.put("TEMPLATEFILENAME", params.get("TEMPLATEFILENAME"));
        importParams.put("DATAFILENAME", params.get("DATAFILENAME"));
        try {
            Map<String, Object> importRes = this.callService("insposws", "excelImport", importParams, login, password);
            if ((importRes != null) && (importRes.get("ACCOUNTLIST") != null) && (((List) importRes.get("ACCOUNTLIST")).size() > 0)) {
                List<Map<String, Object>> accountsList = (List<Map<String, Object>>) importRes.get("ACCOUNTLIST");
                Map<String, Object> sellerTypeParams = new HashMap<String, Object>();
                sellerTypeParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
                sellerTypeParams.put("SYSNAME", "EMPLOYEE");
                Map<String, Object> sellerTypeRes = this.callService(WsConstants.INSPOSWS, "dsSellerTypeBrowseListByParam", sellerTypeParams, login, password);
                if (sellerTypeRes.get("SELLERTYPEID") != null) {
                    Long sellerTypeId = Long.valueOf(sellerTypeRes.get("SELLERTYPEID").toString());
                    for (Map<String, Object> bean : accountsList) {
                        if ((bean.get("lastname") != null) && (!bean.get("lastname").toString().isEmpty())
                                && (!bean.get("lastname").toString().equals(" "))) {
                            importAccount(bean, sellerTypeId, errorsList, login, password);
                        }
                    }
                } else {
                    addError(errorsList, "Не найден тип продавца EMPLOYEE");
                }
            } else {
                addError(errorsList, "Нет данных для импорта");
            }
        } catch (Exception e) {
            addError(errorsList, String.format("Ошибка при обработке входного файла: %s", joinStackTrace(e)));
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("ERRORSLIST", errorsList);
        result.put("ERRORSCOUNT", errorsList.size());
        return result;
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

    private void createProductDistribution(Map<String, Object> accountMap, Map<String, Object> sellerRes, String postfix, List<Map<String, Object>> errorsList, String fioFull, String login, String password) throws Exception {
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        // xmlUtil.convertFloatToDate(accountMap);
        productParams.put("SYSNAME", accountMap.get("product" + postfix));
        Map<String, Object> productRes = this.callService(WsConstants.INSPRODUCTWS, "dsProductBrowseListByParam", productParams, login, password);
        if (productRes.get("PRODID") != null) {
            Map<String, Object> productVersionParams = new HashMap<String, Object>();
            productVersionParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
            productVersionParams.put("PRODID", productRes.get("PRODID"));
            productVersionParams.put("NAME", accountMap.get("productVersion" + postfix));
            Map<String, Object> productVersionRes = this.callService(WsConstants.INSPRODUCTWS, "dsProductVersionBrowseListByParam", productVersionParams, login, password);
            if (productVersionRes.get("PRODVERID") != null) {

                Map<String, Object> distribParams = new HashMap<String, Object>();
                distribParams.put("SELLERID", sellerRes.get("SELLERID"));
                distribParams.put("PRODVERID", productVersionRes.get("PRODVERID"));
                GregorianCalendar gc = new GregorianCalendar(1900, 0, 1);
                distribParams.put("STARTDATE", getDate(getDateParam(accountMap.get("productStartDate" + postfix))));
                distribParams.put("FINISHDATE", getDate(getDateParam(accountMap.get("productFinishDate" + postfix))));
                Map<String, Object> distribRes = this.callService(WsConstants.INSPRODUCTWS, "dsProductDistributionCreate", distribParams, login, password);
            } else {
                addError(errorsList, String.format("Не найдена версия продукта %s для пользователя %s", accountMap.get("productVerison").toString(), fioFull));
            }
        } else {
            addError(errorsList, String.format("Не найден продукт %s для пользователя %s", accountMap.get("product").toString(), fioFull));
        }
    }

    private void addRolesBySysNameList(Long userAccountId, String roleListStr, List<Map<String, Object>> errorsList, String fioFull, String login, String password) throws Exception {
        String[] roleList = roleListStr.split(",");
        for (String roleSysName : roleList) {
            Map<String, Object> roleRes = getRoleBySysName(roleSysName, login, password);
            Long roleId = null;
            if (roleRes != null) {
                if (roleRes.get(RESULT) != null) {
                    List<Map<String, Object>> roleListRes = (List<Map<String, Object>>) roleRes.get(RESULT);
                    if (!roleListRes.isEmpty()) {
                        if (roleListRes.get(0).get("ROLEID") != null) {
                            roleId = Long.valueOf(roleListRes.get(0).get("ROLEID").toString());
                            addRole(userAccountId, roleId, login, password);
                        }
                    }
                }
            }
            if (roleId == null) {
                addError(errorsList, String.format("найдена роль %s для пользователя %s", roleSysName, fioFull));
            }
        }
    }

    //1. сервис смены пароля.
    @WsMethod(requiredParams = {"NEWPASS", "OLDPASS"})
    public Map<String, Object> dsB2BAccountChangePass(Map<String, Object> params) throws Exception {
        //1. проверка правильности старого пароля.
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        // есть 2 готовые функции по смене пароля.
        // corews changepass 
        // adminws admAccountChangePass
        Map<String, Object> passParams = new HashMap<String, Object>();
        passParams.put("PASSWORD", params.get("OLDPASS"));
        passParams.put("NEWPASSWORD", params.get("NEWPASS"));
        passParams.put("checkCurrentPassword", 1);
        Map<String, Object> res = this.callService(COREWS, "changepass", passParams, login, password);

        //2. кодирование sha1 нового пароля
        //3. сохранение в аккаунт нового пароля
        //штатный сервис не умеет ругатся иначе чем эксепшном 
        //переделываем в норм сообщение.
        if (res.get("Status") != null) {
            if ("ERROR".equalsIgnoreCase(res.get("Status").toString())) {
                res.put("Status", "OK");
                String error = res.get("Error").toString();
                int beginind = res.get("Error").toString().indexOf("FaultMessage=");
                int endInd = res.get("Error").toString().indexOf("ErrorStack");
                String message = error.substring(beginind + 14, endInd - 2);
                res.put("Error", message);
            }
        }

        return res;
    }

    //2. сервис смены ФИО        
    @WsMethod(requiredParams = {"LASTNAME", "FIRSTNAME"})
    public Map<String, Object> dsB2BUserChangeFIO(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        // 1. сохранение фио в аккаунт
        Map<String, Object> getparams = new HashMap<String, Object>();
        getparams.put("username", login);
        getparams.put("passwordSha", password);
        // фамилия
        getparams.put("LASTNAME", params.get("LASTNAME"));
        // отчество
        if (params.get("MIDDLENAME") != null) {
            if (params.get("MIDDLENAME").toString().isEmpty()) {
                getparams.put("MIDDLENAME", " ");
            } else {
                getparams.put("MIDDLENAME", params.get("MIDDLENAME"));
            }
        } else {
            getparams.put("MIDDLENAME", " ");
        }
        // имя
        getparams.put("FIRSTNAME", params.get("FIRSTNAME"));

        this.updateQuery("depEmployeeUpdate", getparams);
        return null;
    }

    //3. сервис получения данных пользователя
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BGetCurrentUserData(Map<String, Object> params) throws Exception {
        // запрос по логину и паролю данных пользователя.
        Map<String, Object> getparams = new HashMap<String, Object>();
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        getparams.put("username", login);
        getparams.put("passwordSha", password);
        Map<String, Object> res = this.selectQuery("dsCheckLogin", null, getparams);
        if (res != null) {
            if (res.get(RESULT) != null) {
                List<Map<String, Object>> resList = (List<Map<String, Object>>) res.get(RESULT);
                if (!resList.isEmpty()) {
                    resList.get(0).remove("PASSWORD");
                    resList.get(0).remove("PREFLANGUAGE");
                    resList.get(0).remove("USERACCOUNTID");
                    resList.get(0).remove("USERID");
                    resList.get(0).remove("OBJECTTYPE");
                    resList.get(0).remove("ISCONCURRENT");
                    resList.get(0).remove("DEPARTMENTID");
                    resList.get(0).remove("CREATIONDATE");
                    resList.get(0).remove("AUTHMETHOD");
                }

            }
        }
        return res;
    }

    @WsMethod(requiredParams = {"EMPLOYEEID", "PHONE1"})
    public Map<String, Object> dsB2BUserChangePhoneNumber(Map<String, Object> params) throws Exception {
        Map<String, Object> filteredParams = new HashMap<String, Object>();
        filteredParams.put("EMPLOYEEID", params.get("EMPLOYEEID"));
        filteredParams.put("PHONE1", params.get("PHONE1"));
        this.updateQuery("depEmployeeUpdate", filteredParams);
        return new HashMap<String, Object>() {{ put("EMPLOYEEID", filteredParams.get("EMPLOYEEID")); }};
    }

    @WsMethod(requiredParams = {"LOGIN"})
    public Map<String, Object> dsUserAccountCreationDateByLogin(Map<String, Object> params)
            throws Exception {
        Map<String, Object> result = this.selectQuery(
                "dsUserAccountCreationDateByLogin", null, params
        );
        return result;
    }

}
