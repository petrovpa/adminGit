/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product.custom;


import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;


/**
 * Фасад для сущности B2BInvestBaseActiveTickerCustom
 *
 * @author averichevsm
 */
@IdGen(entityName="B2B_INVBATICKER",idFieldName="INVBATICKERID")
@BOName("B2BInvestBaseActiveTickerCustom")
public class B2BInvestBaseActiveTickerCustomFacade extends BaseFacade {

    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestBaseActiveTickerBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BInvestBaseActiveTickerBrowseListByParamEx", "dsB2BInvestBaseActiveTickerBrowseListByParamExCount", params);
        return result;
    }





}
