package com.bivgroup.services.b2bposws.facade.pos.importsession;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryConstants;
import com.bivgroup.services.b2bposws.facade.pos.importsession.common.ImportSessionType;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ivanov Roman
 * <p>
 * Базовый фасад для всех сущностей импорта КМ (Клиентского менеджера)
 * <p>
 * Добавлять только общий функционал или общие константы!!!
 **/
@BOName("ClientManagerImportBaseFacade")
public abstract class ClientManagerImportBaseFacade extends B2BDictionaryBaseFacade implements DictionaryConstants {

    protected static ImportSessionType importSessionType;

    public ImportSessionType getImportSessionType() {
        return importSessionType;
    }

    public void setImportSessionType(ImportSessionType importSessionType) {
        this.importSessionType = importSessionType;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsGetTypeImportClientManagerContract(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();

        logger.debug("Start dsGetTypeImportClientManagerContract with param = " + params.toString());
        List<Map<String, Object>> typeImportList = dctFindByExample(KIND_KM_IMPORT_ENTITY_NAME, params, isCallFromGate(params));

        if (typeImportList == null) {
            logger.error("Handle dsGetTypeImportClientManagerContract. typeImportList null or .");
            result.put(ERROR, ERROR);
        }

        logger.debug("dsGetTypeImportClientManagerContract returned typeImportList = " + typeImportList);
        result.put(RESULT, typeImportList);

        logger.debug("End dsGetTypeImportClientManagerContract with result = " + result.toString());
        return result;
    }
}
