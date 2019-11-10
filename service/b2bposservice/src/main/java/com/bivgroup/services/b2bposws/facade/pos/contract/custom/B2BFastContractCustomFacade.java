/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract.custom;


import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.guididgen.GUIDIdGen;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.aspect.impl.ownerright.OwnerRightView;
import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.aspect.impl.version.NodeVersion;
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
@IdGen(entityName="B2B_CONTR",idFieldName="CONTRID")
@BOName("B2BContract")
public class B2BFastContractCustomFacade extends BaseFacade {


    @WsMethod(requiredParams = {"CONTRNODEID"})
    public Map<String,Object> dsB2BFastContractCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BFastContractInsert", params);
        result.put("CONTRID", params.get("CONTRID"));
        return result;
    }


    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String,Object> dsB2BFastContractUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();

        this.updateQuery("dsB2BFastContractUpdate", params);
        result.put("CONTRID", params.get("CONTRID"));
        return result;
    }

}
