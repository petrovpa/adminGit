package com.bivgroup.services.b2bposws.facade.pos.importsession.content;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;

import static com.bivgroup.services.b2bposws.facade.pos.importsession.common.ImportSessionType.IMPORT_SESSION_CONTENT_MANAGER_CONTRACT;

/**
 * Журнал (датапровайдер) содержимого сессии импорта КМ-договор
 *
 * @author Ivanov Roman
 **/
@BOName("ClientManagerImportSessionKmContract")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class ClientManagerImportSessionKmContractFacade extends B2BDictionaryBaseFacade {
    /**
     * Функция для получения списка сесий импорта 'КМ-договор'
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsClientManagerBrowseImportSessionKmContractListByParams(Map<String, Object> params) throws Exception {
        params.put("DISCRIMINATOR", IMPORT_SESSION_CONTENT_MANAGER_CONTRACT.getType());
        Map<String, Object> result = selectQuery(
                "dsClientManagerBrowseImportSessionKmContractListByParams",
                "dsClientManagerBrowseImportSessionKmContractListByParamsCount",
                params);
        return result;
    }
}
