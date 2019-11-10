/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.export;

import java.util.HashMap;
import java.util.Map;
//import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author mmamaev
 */
//@IdGen(entityName = "B2B_EXPORTDATA_CONTENT", idFieldName = "CONTENTID")
@BOName("B2BExportDataContentCustom")
public class B2BExportDataContentCustomFacade extends BaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BExportDataContentMassCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BExportDataContentMassCreate", params);
        result.put("EXPORTDATACONTENTLIST", params.get("rows"));
        return result;
    }

}
