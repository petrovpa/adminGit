package com.bivgroup.services.b2bposws.facade.pos.menu.custom;

import com.bivgroup.services.b2bposws.facade.pos.menu.base.MenuBaseFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * @author pzabaluev
 */
@ProfileRights({
        // Профильное право для доступа к пунктам меню в зависимости от роли
        @PRight(sysName = "RPAccessMenuRole",
                name = "Доступ по роли",
                restrictionFieldName = "T.MENUID",
                paramName = "MENUID")
})
@BOName("MenuCustomFacade")
public class MenuCustomFacade extends MenuBaseFacade {

    /**
     * Список меню с учетом профильного права
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMenuBrowseListByParam(Map<String, Object> params) throws Exception {
        logger.debug("before dsMenuBrowseListByParam");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Map<String, Object>> outList = new java.util.ArrayList<Map<String, Object>>();
        Map<String, Object> menuRes = this.selectQuery("dsMenuBrowseListByParam", "dsMenuBrowseListByParamCount", params);
        if (menuRes.get(RESULT) != null) {
            if (menuRes.get(RESULT) instanceof List) {
                List<Map<String, Object>> resList = WsUtils.getListFromResultMap(menuRes);
                logger.debug(resList);
                outList = buildMenuList(0, 1, resList);
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("MENU", outList);
        logger.debug("after dsMenuBrowseListByParam");
        return result;
    }
}
