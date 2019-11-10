/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.other;


import org.apache.regexp.RE;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;


/**
 * Фасад для сущности B2BMenuType
 *
 * @author reson
 */
@IdGen(entityName = "B2B_BINDOCTYPE", idFieldName = "BINDOCTYPEID")
@BOName("B2BBinDocType")
public class B2BBinDocTypeFacade extends BaseFacade {

    // для result.put("Error", "описание ошибки для вывода в интерфейсе")
    protected static final String ERROR = "Error";

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBinDocTypeBrowseListByParam(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap();

        // Ожидаем список системных имен
        if (params.containsKey("ENTITYSYSNAMELIST")) {
            // Получаем все что есть
            Map<String, Object> binDocTypePrepareMap = this.selectQuery("dsB2BBinDocTypeBrowseListByParam", "dsB2BBinDocTypeBrowseListByParamCount", params);

            List<Map<String, Object>> binDocTypePrepareList = null;
            // Если вернулся результат
            if (binDocTypePrepareMap.containsKey(RESULT)) {
                binDocTypePrepareList = (List<Map<String, Object>>) binDocTypePrepareMap.get(RESULT);

                // И выбираем нужные без повторений
                List entitySysNameList = (List) params.get("ENTITYSYSNAMELIST");
                List binDocTypeResList = new ArrayList();
                // Пробегаемся по листу и выбираем нужные
                for (Map item : binDocTypePrepareList) {
                    if (item.containsKey("ENTITYSYSNAME")) {
                        if (entitySysNameList.contains(item.get("ENTITYSYSNAME")) && (!binDocTypeResList.contains(item))) {
                            binDocTypeResList.add(item);
                        }
                    }
                }
                
                result.put(RESULT, binDocTypeResList);
            }
        }

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBinDocTypeBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BBinDocTypeBrowseListByParamEx", "dsB2BBinDocTypeBrowseListByParamExCount", params);
        return result;
    }

}
