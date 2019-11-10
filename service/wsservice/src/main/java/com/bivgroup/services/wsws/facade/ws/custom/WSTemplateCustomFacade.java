/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.wsws.facade.ws.custom;

import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("WSTemplateCustom")
public class WSTemplateCustomFacade extends BaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsWSTemplateBrowseListByParamWithMethodConstraint(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsWSTemplateBrowseListByParamWithMethodConstraint", "dsWSTemplateBrowseListByParamWithMethodConstraintCount", params);
        return result;
    }
}
