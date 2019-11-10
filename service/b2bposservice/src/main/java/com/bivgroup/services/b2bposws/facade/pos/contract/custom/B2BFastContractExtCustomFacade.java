/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract.custom;


import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;


/**
 * Фасад для сущности B2BContract
 *
 * @author reson
 */
@IdGen(entityName="B2B_CONTREXT",idFieldName="CONTREXTID")
@BOName("B2BFastContractExt")
public class B2BFastContractExtCustomFacade extends BaseFacade {


    @WsMethod(requiredParams = {"CONTRID", "HBDATAVERID"})
    public Map<String,Object> dsB2BFastContractExtensionCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractExtensionInsert", params);
        result.put("CONTREXTID", params.get("CONTREXTID"));
        return result;
    }

}
