package com.bivgroup.rest.active_directory.service;

import com.bivgroup.ldap.authorization.LdapUserService;
import com.bivgroup.ldap.exception.ServiceException;
import com.bivgroup.ldap.pojo.ActiveDirectoryUserInfo;
import com.bivgroup.rest.active_directory.pojo.ActiveDirectoryUserRequest;
import com.bivgroup.sessionutils.SessionController;
import com.bivgroup.utils.ParamGetter;
import com.bivgroup.utils.serviceloader.DefaultServiceLoader;
import org.apache.log4j.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bivgroup.rest.active_directory.common.Constants.ERROR;
import static com.bivgroup.rest.active_directory.common.Constants.RESULT;
import static com.bivgroup.rest.active_directory.common.Constants.STATUS;

@Path("/rest/active-directory")
public class ActiveDirectoryWebServiceGate {
    private Logger logger;

    public ActiveDirectoryWebServiceGate() {
        this.logger = Logger.getLogger(this.getClass());
    }

    /**
     * Сервис получения списка пользователей из active directory.
     * Для фильтрации типа "начиная с" к концу значения параметра требуется добавлять '*'.
     * Для фильтрации типа "оканчивается на" к началу значения параметра требуется добавлять '*'.
     * Для фильтрации типа "включает" к началу и концу значения параметра требуется добавлять '*'.
     *
     * @param params ограничения для поиска пользователя
     *               <UL>
     *               <LI>LASTNAME - фамилия пользователя</LI>
     *               <LI>FIRSTNAME - имя пользователя</LI>
     *               <LI>FULLNAME - полное имя пользователя</LI>
     *               <LI>ADUSERPRINCIPALNAME - главный логин пользователя</LI>
     *               <LI>ADUSERLOGIN - логин пользователя</LI>
     *               <LI>ISNEEDBLOCKED - требуются ли возвращать заблокированых пользователей</LI>
     *               </UL>
     * @return список словарей с информацией о пользователе active directory.
     * Каждый элемент содержит следующие параметры:
     * <UL>
     * <LI>LASTNAME - фамилия пользователя</LI>
     * <LI>FIRSTNAME - имя пользователя</LI>
     * <LI>FULLNAME - полное имя пользователя</LI>
     * <LI>ADUSERPRINCIPALNAME - главный логин пользователя</LI>
     * <LI>ADUSERLOGIN - логин пользователя</LI>
     * <LI>ISBLOCKED - заблокирован ли пользователь</LI>
     * </UL>
     */
    @POST
    @Path("/getActiveDirectoryUserList")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> dsB2BUserChangePass(ActiveDirectoryUserRequest params) {
        SessionController sessionController = DefaultServiceLoader.loadServiceAny(SessionController.class);
        Map<String, Object> sessionCheckResult = sessionController.checkSession(params.getSessionId());
        String error = ParamGetter.getStringParam(sessionCheckResult, ERROR);
        String newSessionId = ParamGetter.getStringParam(sessionCheckResult, "SESSIONID");
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            LdapUserService ldapUserService = new LdapUserService();
            try {
                List<ActiveDirectoryUserInfo> activeDirectoryUserList = ldapUserService.getUsersByCondition(params.getParams());
                result.put(RESULT, activeDirectoryUserList);
            } catch (ServiceException e) {
                logger.error("Error get list user from active directory", e);
                error = e.getMessage();
            }
        }
        if (!error.isEmpty()) {
            result.put(STATUS, "ERROR");
            result.put(ERROR, error);
        } else {
            if (!newSessionId.isEmpty()) {
                result.put("SESSIONID", newSessionId);
            }
        }
        return result;
    }
}
