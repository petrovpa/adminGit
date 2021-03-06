package com.bivgroup.services.b2bposws.facade.pos.importsession.content;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;

import static com.bivgroup.services.b2bposws.facade.pos.importsession.common.ImportSessionType.IMPORT_SESSION_CONTENT_MANAGER_DEPARTMENT;

/**
 * Журнал (датапровайдер) содержимого сессии импорта КМ-ВСП
 *
 * @author Ivanov Roman
 **/
@BOName("ClientManagerImportSessionKmVsp")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class ClientManagerImportSessionKmVspFacade extends B2BDictionaryBaseFacade {

    /**
     * Функция для получения списка сессий импорта 'КМ-ВСП'
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsClientManagerBrowseImportSessionKmVspListByParams(Map<String, Object> params) throws Exception {
        params.put("DISCRIMINATOR", IMPORT_SESSION_CONTENT_MANAGER_DEPARTMENT.getType());
        Map<String, Object> result = selectQuery(
                "dsClientManagerBrowseImportSessionKmVspListByParams",
                "dsClientManagerBrowseImportSessionKmVspListByParamsCount",
                params);
        return result;
    }
}
