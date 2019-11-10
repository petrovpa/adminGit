/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.contract.migration;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@IdGen(entityName="INS_OBJSTATEH",idFieldName="ID")
@BOName("B2BContractStateHistoryMigration")
public class B2BContractStateHistoryMigrationFacade extends B2BBaseFacade {

    // для массовых вставок @IdGen не предназначен?
    //@WsMethod(requiredParams = {"ROWS"})
    //public Map<String, Object> dsB2BContractStateHistoryMassCreate(Map<String, Object> params) throws Exception {
    //    Map<String, Object> result = new HashMap<String, Object>();
    //    int[] updateCounts = this.insertMassQuery("dsStateHistoryMassInsert", (List<Map<String, Object>>) params.get("ROWS"));
    //    result.put("UPDATECOUNTS", updateCounts);
    //    return result;
    //}

    @WsMethod(requiredParams = {"OBJID", "STARTDATE", "STATEID", "STATENAME", "TYPEID", "TYPENAME", "USERNAME"})
    public Map<String, Object> dsB2BContractStateHistoryRecordCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        int[] updateCounts = this.insertQuery("dsStateHistoryInsert", params);
        result.put("UPDATECOUNTS", updateCounts);
        result.put("ID", params.get("ID"));
        return result;
    }

}
