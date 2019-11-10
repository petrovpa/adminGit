/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.other.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author sambucus
 */
@BOName("B2BExportDataTemplateCustom")
public class B2BExportDataTemplateCustomFacade extends B2BBaseFacade {
    
    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ISRECORDDISABLE - null</LI>
     * <LI>SQL - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TEMPLATEID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ISRECORDDISABLE - null</LI>
     * <LI>SQL - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TEMPLATEID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BExportDataTemplateBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BExportDataTemplateBrowseListByParamEx", "dsB2BExportDataTemplateBrowseListByParamExCount", params);
        return result;
    }
    
}
