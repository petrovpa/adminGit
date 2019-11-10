/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.admin;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Фасад для сущности "Права на меню"
 *
 * @author Ivanovra
 */
@BOName("B2BMenuRight")
public class B2BMenuRightFacade extends B2BBaseFacade {

    /**
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminGetMenuRightsList(Map<String, Object> params) throws Exception {

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(params, Double.class);

        Map<String, Object> customParams = params;
        Map<String, Object> result = null;

        // добавляем ограничение по агентам только если пользователь не "Сотрудник страховой" и не "Робот"
        Object userTypeId = params.get(Constants.SESSIONPARAM_USERTYPEID);
        if ((userTypeId == null) || ((Long.valueOf(userTypeId.toString()) != 1L)
                && ((Long.valueOf(userTypeId.toString())) != 4L))) {
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

        result = this.selectQuery("dsAdminGetMenuRightsList", "dsAdminGetMenuRightsListCount", customParams);
        return result;
    }

    /**
     * @param params
     * @return возвращает маппу с флагом "rightIsExist", который обрабатывается
     * на клиенте. Этот флаг устанавливает в true, если "права на меню" для меню
     * к которому мы добавляем существуют, в другом случае false, если иначе
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BAdminCreateMenuRight(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> whereParam = new HashMap<String, Object>();
        Map<String, Object> menuRightRes = new HashMap<String, Object>();

        whereParam.put("MENUID", params.get("MENUID"));
        whereParam.put("ORGSTRUCTID", params.get("DEPARTMENT_FIELD"));

        Map<String, Object> result = new HashMap<String, Object>();

        // Если повторных прав на меню нет
        menuRightRes = selectQuery("dsAdminGetMenuRightsCheckList", "dsAdminGetMenuRightsCheckListCount", whereParam);
        if (menuRightRes.get("TOTALCOUNT").equals(0)) {
            //вставляем новые
            menuRightRes = this.callService(Constants.B2BPOSWS, "dsB2BMenuorgstructCreate", whereParam, login, password);
            result = menuRightRes;
        } else {
            Map<String, Object> ErrMap = new HashMap<String, Object>();
            ErrMap.put("StatusType", "ERROR");
            ErrMap.put("Error", "Данное право уже назначено на меню.");
            result = ErrMap;
        }
        return result;
    }

    /**
     * Удаляем права, для конкретного меню(по MENUID).
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"MENUID", "MENUORGSTRUCTID"})
    public Map<String, Object> dsB2BAdminDeleteMenuRight(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();

//        result = this.callService(Constants.B2BPOSWS, "dsB2BMenuorgstructDeleteByMenuID", params, login, password);
        this.deleteQuery("dsB2BMenuorgstructDeleteByMenuID", params);
        return result;
    }

}
