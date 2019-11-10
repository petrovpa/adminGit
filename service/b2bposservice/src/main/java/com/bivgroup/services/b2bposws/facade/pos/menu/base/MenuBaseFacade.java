package com.bivgroup.services.b2bposws.facade.pos.menu.base;

import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.facade.BaseFacade;

import java.util.List;
import java.util.Map;

/**
 * @author pzabaluev
 */
public class MenuBaseFacade extends BaseFacade {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MenuBaseFacade.class);
    protected static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    protected List<Map<String, Object>> buildMenuList(int itemIndex, int level, List<Map<String, Object>> menuList) {
        List<Map<String, Object>> result = new java.util.ArrayList<Map<String, Object>>();

        for (int i = itemIndex; i < menuList.size(); i++) {
            Map<String, Object> menuItem = menuList.get(i);
            int menuItemLevel = Long.valueOf(menuItem.get("LEVEL").toString()).intValue();
            if (menuItemLevel == level) {
                menuItem.put("isView", Boolean.TRUE);
                menuItem.put("isEnabled", Boolean.TRUE);
                List<Map<String, Object>> subItems = buildMenuList(i + 1, level + 1, menuList);
                if (!subItems.isEmpty()) {
                    menuItem.put("SUBITEMS", subItems);
                    menuItem.put("hasSubItems", true);
                } else {
                    menuItem.put("hasSubItems", Boolean.FALSE);
                }
                result.add(menuItem);
            } else if (menuItemLevel < level) {
                return result;
            }
        }
        return result;
    }
}
