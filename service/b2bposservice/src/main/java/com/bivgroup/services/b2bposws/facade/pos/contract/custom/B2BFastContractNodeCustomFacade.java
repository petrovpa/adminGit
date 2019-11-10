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
@IdGen(entityName="B2B_CONTRNODE",idFieldName="CONTRNODEID")
@BOName("B2BFastContractNode")
public class B2BFastContractNodeCustomFacade extends BaseFacade {

    @WsMethod(requiredParams = {"LASTVERNUMBER", "RVERSION"})
    public Map<String,Object> dsB2BFastContractNodeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractNodeInsert", params);
        result.put("CONTRNODEID", params.get("CONTRNODEID"));
        return result;
    }


}
