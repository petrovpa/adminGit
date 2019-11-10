package com.bivgroup.services.b2bposws.facade.pos.menu;

import com.bivgroup.services.b2bposws.facade.pos.menu.base.MenuBaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pzabaluev
 */
@BOName("MenuFacade")
public class MenuFacade extends MenuBaseFacade {

    /**
     * Полное дерево меню
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsLoadFullMenu(Map<String, Object> params) throws Exception {
        logger.debug("before dsLoadFullMenu");

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

        logger.debug("after dsLoadFullMenu");
        return result;
    }
}
