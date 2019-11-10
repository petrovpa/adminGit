package com.bivgroup.services.b2bposws.facade.pos.loss;


import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;

@Auth(onlyCreatorAccess = false)
@IdGen(entityName = "B2B_LOSSNOTICEDOC", idFieldName = "LOSSNOTICEDOCID")
@BinaryFile(objTableName = "B2B_LOSSNOTICEDOC", objTablePKFieldName = "LOSSNOTICEDOCID")
@State(idFieldName = "LOSSNOTICEDOCID", startStateName = "B2B_LOSSNOTICEDOC_NEW", typeSysName = "B2B_LOSSNOTICEDOC")
@BOName("B2BLossNoticeDoc")
public class B2BLossNoticeDocFacade extends BaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BLossNoticeDocCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BLossNoticeDocInsert", params);
        result.put("LOSSNOTICEDOCID", params.get("LOSSNOTICEDOCID"));
        return result;
    }

    @WsMethod(requiredParams = {"LOSSNOTICEDOCID"})
    public Map<String, Object> dsB2BLossNoticeDocInsert(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BLossNoticeDocInsert", params);
        result.put("LOSSNOTICEDOCID", params.get("LOSSNOTICEDOCID"));
        return result;
    }

    @WsMethod(requiredParams = {"LOSSNOTICEDOCID"})
    public Map<String, Object> dsB2BLossNoticeDocUpdate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BLossNoticeDocUpdate", params);
        result.put("LOSSNOTICEDOCID", params.get("LOSSNOTICEDOCID"));
        return result;
    }

    @WsMethod(requiredParams = {"LOSSNOTICEDOCID"})
    public Map<String, Object> dsB2BLossNoticeDocModify(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BLossNoticeDocUpdate", params);
        result.put("LOSSNOTICEDOCID", params.get("LOSSNOTICEDOCID"));
        return result;
    }

    @WsMethod(requiredParams = {"LOSSNOTICEDOCID"})
    public void dsB2BLossNoticeDocDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BLossNoticeDocDelete", params);
    }



}
