/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * Кастомный фасад для сущности RefundProductReport
 *
 * @author averichevsm
 */
@BOName("LossesProductReportCustom")
public class LossesProductReportCustomFacade extends BaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsLossesProductReportBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsLossesProductReportBrowseListByParamEx", "dsLossesProductReportBrowseListByParamExCount", params);
        return result;
    }
}
