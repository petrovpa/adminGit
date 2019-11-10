/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.export;

import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author kkulkov
 */
@ProfileRights({
    @PRight(sysName = "RPAccessPOS_Branch",
            name = "Доступ по подразделению",
            joinStr = "  inner join B2B_EXPORTDATAORGSTRUCT COS on (t.EXPORTDATAID = COS.EXPORTDATAID) inner join INS_DEPLVL DEPLVL on (COS.ORGSTRUCTID = DEPLVL.OBJECTID) ",
            restrictionFieldName = "DEPLVL.PARENTID",
            paramName = "DEPARTMENTID")})
@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("ExportQueryCustom")
public class ExportQueryCustomFacade  extends ProductContractCustomFacade {
    private final Logger logger = Logger.getLogger(this.getClass());
    
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractExportDataContentBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BContractExportDataContentBrowseListByParamEx", "dsB2BContractExportDataContentBrowseListByParamCountEx", params);
        return result;
    }
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BExportDataBrowseListByParamCustomeWhereEx(Map<String, Object> params) throws Exception {
        logger.debug("Start export data records browse...");

        String idFieldName = "EXPORTDATAID";
        String customWhereQueryName = "dsB2BExportDataBrowseListByParamCustomeWhereEx";
        Map<String, Object> result = doCustomWhereQuery(customWhereQueryName, idFieldName, params);

        logger.debug("Export data records browse finish.");

        return result;
    }
    
    
}
