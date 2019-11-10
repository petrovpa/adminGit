/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BContractDiscount
 *
 * @author reson
 */
@Discriminator(2)
@IdGen(entityName="B2B_CONTRDISC",idFieldName="CONTRDISCID")
@BOName("B2BContractDiscount")
public class B2BContractDiscountFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>DISCRIMINATOR - Дискриминатор сущности</LI>
     * <LI>CONTRDISCID - ИД</LI>
     * <LI>PRODDISCPROMOID - ИД промокода</LI>
     * <LI>PRODDISKID - ИД скидки по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractDiscountCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractDiscountInsert", params);
        result.put("CONTRDISCID", params.get("CONTRDISCID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>DISCRIMINATOR - Дискриминатор сущности</LI>
     * <LI>CONTRDISCID - ИД</LI>
     * <LI>PRODDISCPROMOID - ИД промокода</LI>
     * <LI>PRODDISKID - ИД скидки по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRDISCID"})
    public Map<String,Object> dsB2BContractDiscountInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractDiscountInsert", params);
        result.put("CONTRDISCID", params.get("CONTRDISCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>DISCRIMINATOR - Дискриминатор сущности</LI>
     * <LI>CONTRDISCID - ИД</LI>
     * <LI>PRODDISCPROMOID - ИД промокода</LI>
     * <LI>PRODDISKID - ИД скидки по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRDISCID"})
    public Map<String,Object> dsB2BContractDiscountUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractDiscountUpdate", params);
        result.put("CONTRDISCID", params.get("CONTRDISCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>DISCRIMINATOR - Дискриминатор сущности</LI>
     * <LI>CONTRDISCID - ИД</LI>
     * <LI>PRODDISCPROMOID - ИД промокода</LI>
     * <LI>PRODDISKID - ИД скидки по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRDISCID"})
    public Map<String,Object> dsB2BContractDiscountModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractDiscountUpdate", params);
        result.put("CONTRDISCID", params.get("CONTRDISCID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRDISCID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRDISCID"})
    public void dsB2BContractDiscountDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractDiscountDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>DISCRIMINATOR - Дискриминатор сущности</LI>
     * <LI>CONTRDISCID - ИД</LI>
     * <LI>PRODDISCPROMOID - ИД промокода</LI>
     * <LI>PRODDISKID - ИД скидки по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>DISCRIMINATOR - Дискриминатор сущности</LI>
     * <LI>CONTRDISCID - ИД</LI>
     * <LI>PRODDISCPROMOID - ИД промокода</LI>
     * <LI>PRODDISKID - ИД скидки по продукту</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractDiscountBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractDiscountBrowseListByParam", "dsB2BContractDiscountBrowseListByParamCount", params);
        return result;
    }





}
