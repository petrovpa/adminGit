package com.bivgroup.services.b2bposws.facade.pos.importsession.content;

import com.bivgroup.services.b2bposws.facade.pos.importsession.common.B2BImportSessionBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;

@BOName("B2BImportSessionContentCustom")
public class B2BImportSessionContentCustomFacade extends B2BImportSessionBaseFacade {

    /** Получение идентификаторов обрабатываемых записей содержимого по состоянию и родителю (для регламентных заданий) */
    @WsMethod(requiredParams = {"IMPORTSESSIONID", "STATEID"})
    public Map<String, Object> dsB2BImportSessionContentBrowseListByParamForProcessing(Map<String, Object> params) throws Exception {
        Map<String, Object> result = selectQuery("dsB2BImportSessionContentBrowseListByParamForProcessing", params);
        return result;
    }

    /** Получение количества записей содержимого по состоянию и родителю (для регламентных заданий) */
    @WsMethod(requiredParams = {"IMPORTSESSIONID", "STATEID"})
    public Map<String, Object> dsB2BImportSessionContentExistsByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = selectQuery("dsB2BImportSessionContentExistsByParam", null, params);
        return result;
    }

}
