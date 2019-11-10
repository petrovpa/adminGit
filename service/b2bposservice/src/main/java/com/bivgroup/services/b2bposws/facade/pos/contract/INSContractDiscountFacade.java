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
 * Фасад для сущности INSContractDiscount
 *
 * @author reson
 */
@Discriminator(1)
@IdGen(entityName="B2B_CONTRDISC",idFieldName="CONTRDISCID")
@BOName("INSContractDiscount")
public class INSContractDiscountFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>DISCRIMINATOR - Дискриминатор сущности</LI>
     * <LI>CONTRDISCID - ИД</LI>
     * <LI>PRODDISCPROMOID - ИД промо кода</LI>
     * <LI>PRODDISKID - ИД акции по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsINSContractDiscountCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsINSContractDiscountInsert", params);
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
     * <LI>PRODDISCPROMOID - ИД промо кода</LI>
     * <LI>PRODDISKID - ИД акции по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRDISCID"})
    public Map<String,Object> dsINSContractDiscountInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsINSContractDiscountInsert", params);
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
     * <LI>PRODDISCPROMOID - ИД промо кода</LI>
     * <LI>PRODDISKID - ИД акции по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRDISCID"})
    public Map<String,Object> dsINSContractDiscountUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsINSContractDiscountUpdate", params);
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
     * <LI>PRODDISCPROMOID - ИД промо кода</LI>
     * <LI>PRODDISKID - ИД акции по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRDISCID"})
    public Map<String,Object> dsINSContractDiscountModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsINSContractDiscountUpdate", params);
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
    public void dsINSContractDiscountDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsINSContractDiscountDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>DISCRIMINATOR - Дискриминатор сущности</LI>
     * <LI>CONTRDISCID - ИД</LI>
     * <LI>PRODDISCPROMOID - ИД промо кода</LI>
     * <LI>PRODDISKID - ИД акции по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>DISCRIMINATOR - Дискриминатор сущности</LI>
     * <LI>CONTRDISCID - ИД</LI>
     * <LI>PRODDISCPROMOID - ИД промо кода</LI>
     * <LI>PRODDISKID - ИД акции по продукту</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsINSContractDiscountBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsINSContractDiscountBrowseListByParam", "dsINSContractDiscountBrowseListByParamCount", params);
        return result;
    }





}
