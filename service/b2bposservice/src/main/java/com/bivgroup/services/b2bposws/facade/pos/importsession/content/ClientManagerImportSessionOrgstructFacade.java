package com.bivgroup.services.b2bposws.facade.pos.importsession.content;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;

import static com.bivgroup.services.b2bposws.facade.pos.importsession.common.ImportSessionType.IMPORT_SESSION_CONTENT_DEPARTMENT;

/**
 * Журнал (датапровайдер) содержимого сессии импорта Орструктуры
 *
 * @author Ivanov Roman
 **/
@BOName("ClientManagerImportSessionOrgstruct")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class ClientManagerImportSessionOrgstructFacade extends B2BDictionaryBaseFacade {

    /**
     * Функция для получения списка сессий импорта оргструктуры
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsClientManagerBrowseImportSessionOrgstructListByParams(Map<String, Object> params) throws Exception {
        params.put("DISCRIMINATOR", IMPORT_SESSION_CONTENT_DEPARTMENT.getType());
        Map<String, Object> result = selectQuery(
                "dsClientManagerBrowseImportSessionOrgstructListByParams",
                "dsClientManagerBrowseImportSessionOrgstructListByParamsCount",
                params);
        return result;
    }

}
