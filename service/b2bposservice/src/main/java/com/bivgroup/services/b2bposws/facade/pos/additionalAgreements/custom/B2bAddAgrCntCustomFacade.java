/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.custom;


import com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.*;
import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2bAddAgrCnt
 *
 * @author reson
 */
@IdGen(entityName="B2B_ADDAGRCNT",idFieldName="ADDAGRCNTID")
@BOName("B2bAddAgrCntCustom")
public class B2bAddAgrCntCustomFacade extends BaseFacade {


   /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - ИД причины изменения</LI>
     * <LI>ADDAGRCNTID - ИД</LI>
     * <LI>ADDAGRID - ИД заголовка заявки на допс</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCAUSEID - ИД причины изменения</LI>
     * <LI>ADDAGRCNTID - ИД</LI>
     * <LI>ADDAGRID - ИД заголовка заявки на допс</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2bAddAgrCntBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2bAddAgrCntBrowseListByParamEx", "dsB2bAddAgrCntBrowseListByParamExCount", params);
        return result;
    }





}
