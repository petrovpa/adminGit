/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BContractExtension
 *
 * @author reson
 */
@IdGen(entityName="B2B_CONTREXT",idFieldName="CONTREXTID")
@BOName("B2BContractExtension")
public class B2BContractExtensionFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД контракта</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>DOUBLEFIELD06 - Дробное поле</LI>
     * <LI>DOUBLEFIELD07 - Дробное поле</LI>
     * <LI>DOUBLEFIELD08 - Дробное поле</LI>
     * <LI>DOUBLEFIELD09 - Дробное поле</LI>
     * <LI>DOUBLEFIELD10 - Дробное поле</LI>
     * <LI>DOUBLEFIELD11 - Дробное поле</LI>
     * <LI>DOUBLEFIELD12 - Дробное поле</LI>
     * <LI>DOUBLEFIELD13 - Дробное поле</LI>
     * <LI>DOUBLEFIELD14 - Дробное поле</LI>
     * <LI>DOUBLEFIELD15 - Дробное поле</LI>
     * <LI>DOUBLEFIELD16 - Дробное поле</LI>
     * <LI>DOUBLEFIELD17 - Дробное поле</LI>
     * <LI>DOUBLEFIELD18 - Дробное поле</LI>
     * <LI>DOUBLEFIELD19 - Дробное поле</LI>
     * <LI>DOUBLEFIELD20 - Дробное поле</LI>
     * <LI>DOUBLEFIELD21 - Дробное поле</LI>
     * <LI>DOUBLEFIELD22 - Дробное поле</LI>
     * <LI>DOUBLEFIELD23 - Дробное поле</LI>
     * <LI>DOUBLEFIELD24 - Дробное поле</LI>
     * <LI>DOUBLEFIELD25 - Дробное поле</LI>
     * <LI>DOUBLEFIELD26 - Дробное поле</LI>
     * <LI>DOUBLEFIELD27 - Дробное поле</LI>
     * <LI>DOUBLEFIELD28 - Дробное поле</LI>
     * <LI>DOUBLEFIELD29 - Дробное поле</LI>
     * <LI>DOUBLEFIELD30 - Дробное поле</LI>
     * <LI>DOUBLEFIELD31 - Дробное поле</LI>
     * <LI>DOUBLEFIELD32 - Дробное поле</LI>
     * <LI>DOUBLEFIELD33 - Дробное поле</LI>
     * <LI>DOUBLEFIELD34 - Дробное поле</LI>
     * <LI>DOUBLEFIELD35 - Дробное поле</LI>
     * <LI>DOUBLEFIELD36 - Дробное поле</LI>
     * <LI>DOUBLEFIELD37 - Дробное поле</LI>
     * <LI>DOUBLEFIELD38 - Дробное поле</LI>
     * <LI>DOUBLEFIELD39 - Дробное поле</LI>
     * <LI>DOUBLEFIELD40 - Дробное поле</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>CONTREXTID - ИД записи</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>LONGFIELD06 - Целое поле</LI>
     * <LI>LONGFIELD07 - Целое поле</LI>
     * <LI>LONGFIELD08 - Целое поле</LI>
     * <LI>LONGFIELD09 - Целое поле</LI>
     * <LI>LONGFIELD10 - Целое поле</LI>
     * <LI>LONGFIELD11 - Целое поле</LI>
     * <LI>LONGFIELD12 - Целое поле</LI>
     * <LI>LONGFIELD13 - Целое поле</LI>
     * <LI>LONGFIELD14 - Целое поле</LI>
     * <LI>LONGFIELD15 - Целое поле</LI>
     * <LI>LONGFIELD16 - Целое поле</LI>
     * <LI>LONGFIELD17 - Целое поле</LI>
     * <LI>LONGFIELD18 - Целое поле</LI>
     * <LI>LONGFIELD19 - Целое поле</LI>
     * <LI>LONGFIELD20 - Целое поле</LI>
     * <LI>LONGFIELD21 - Целое поле</LI>
     * <LI>LONGFIELD22 - Целое поле</LI>
     * <LI>LONGFIELD23 - Целое поле</LI>
     * <LI>LONGFIELD24 - Целое поле</LI>
     * <LI>LONGFIELD25 - Целое поле</LI>
     * <LI>LONGFIELD26 - Целое поле</LI>
     * <LI>LONGFIELD27 - Целое поле</LI>
     * <LI>LONGFIELD28 - Целое поле</LI>
     * <LI>LONGFIELD29 - Целое поле</LI>
     * <LI>LONGFIELD30 - Целое поле</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>STRINGFIELD06 - Строковое поле</LI>
     * <LI>STRINGFIELD07 - Строковое поле</LI>
     * <LI>STRINGFIELD08 - Строковое поле</LI>
     * <LI>STRINGFIELD09 - Строковое поле</LI>
     * <LI>STRINGFIELD10 - Строковое поле</LI>
     * <LI>STRINGFIELD11 - Строковое поле</LI>
     * <LI>STRINGFIELD12 - Строковое поле</LI>
     * <LI>STRINGFIELD13 - Строковое поле</LI>
     * <LI>STRINGFIELD14 - Строковое поле</LI>
     * <LI>STRINGFIELD15 - Строковое поле</LI>
     * <LI>STRINGFIELD16 - Строковое поле</LI>
     * <LI>STRINGFIELD17 - Строковое поле</LI>
     * <LI>STRINGFIELD18 - Строковое поле</LI>
     * <LI>STRINGFIELD19 - Строковое поле</LI>
     * <LI>STRINGFIELD20 - Строковое поле</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTREXTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID", "HBDATAVERID"})
    public Map<String,Object> dsB2BContractExtensionCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractExtensionInsert", params);
        result.put("CONTREXTID", params.get("CONTREXTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД контракта</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>DOUBLEFIELD06 - Дробное поле</LI>
     * <LI>DOUBLEFIELD07 - Дробное поле</LI>
     * <LI>DOUBLEFIELD08 - Дробное поле</LI>
     * <LI>DOUBLEFIELD09 - Дробное поле</LI>
     * <LI>DOUBLEFIELD10 - Дробное поле</LI>
     * <LI>DOUBLEFIELD11 - Дробное поле</LI>
     * <LI>DOUBLEFIELD12 - Дробное поле</LI>
     * <LI>DOUBLEFIELD13 - Дробное поле</LI>
     * <LI>DOUBLEFIELD14 - Дробное поле</LI>
     * <LI>DOUBLEFIELD15 - Дробное поле</LI>
     * <LI>DOUBLEFIELD16 - Дробное поле</LI>
     * <LI>DOUBLEFIELD17 - Дробное поле</LI>
     * <LI>DOUBLEFIELD18 - Дробное поле</LI>
     * <LI>DOUBLEFIELD19 - Дробное поле</LI>
     * <LI>DOUBLEFIELD20 - Дробное поле</LI>
     * <LI>DOUBLEFIELD21 - Дробное поле</LI>
     * <LI>DOUBLEFIELD22 - Дробное поле</LI>
     * <LI>DOUBLEFIELD23 - Дробное поле</LI>
     * <LI>DOUBLEFIELD24 - Дробное поле</LI>
     * <LI>DOUBLEFIELD25 - Дробное поле</LI>
     * <LI>DOUBLEFIELD26 - Дробное поле</LI>
     * <LI>DOUBLEFIELD27 - Дробное поле</LI>
     * <LI>DOUBLEFIELD28 - Дробное поле</LI>
     * <LI>DOUBLEFIELD29 - Дробное поле</LI>
     * <LI>DOUBLEFIELD30 - Дробное поле</LI>
     * <LI>DOUBLEFIELD31 - Дробное поле</LI>
     * <LI>DOUBLEFIELD32 - Дробное поле</LI>
     * <LI>DOUBLEFIELD33 - Дробное поле</LI>
     * <LI>DOUBLEFIELD34 - Дробное поле</LI>
     * <LI>DOUBLEFIELD35 - Дробное поле</LI>
     * <LI>DOUBLEFIELD36 - Дробное поле</LI>
     * <LI>DOUBLEFIELD37 - Дробное поле</LI>
     * <LI>DOUBLEFIELD38 - Дробное поле</LI>
     * <LI>DOUBLEFIELD39 - Дробное поле</LI>
     * <LI>DOUBLEFIELD40 - Дробное поле</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>CONTREXTID - ИД записи</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>LONGFIELD06 - Целое поле</LI>
     * <LI>LONGFIELD07 - Целое поле</LI>
     * <LI>LONGFIELD08 - Целое поле</LI>
     * <LI>LONGFIELD09 - Целое поле</LI>
     * <LI>LONGFIELD10 - Целое поле</LI>
     * <LI>LONGFIELD11 - Целое поле</LI>
     * <LI>LONGFIELD12 - Целое поле</LI>
     * <LI>LONGFIELD13 - Целое поле</LI>
     * <LI>LONGFIELD14 - Целое поле</LI>
     * <LI>LONGFIELD15 - Целое поле</LI>
     * <LI>LONGFIELD16 - Целое поле</LI>
     * <LI>LONGFIELD17 - Целое поле</LI>
     * <LI>LONGFIELD18 - Целое поле</LI>
     * <LI>LONGFIELD19 - Целое поле</LI>
     * <LI>LONGFIELD20 - Целое поле</LI>
     * <LI>LONGFIELD21 - Целое поле</LI>
     * <LI>LONGFIELD22 - Целое поле</LI>
     * <LI>LONGFIELD23 - Целое поле</LI>
     * <LI>LONGFIELD24 - Целое поле</LI>
     * <LI>LONGFIELD25 - Целое поле</LI>
     * <LI>LONGFIELD26 - Целое поле</LI>
     * <LI>LONGFIELD27 - Целое поле</LI>
     * <LI>LONGFIELD28 - Целое поле</LI>
     * <LI>LONGFIELD29 - Целое поле</LI>
     * <LI>LONGFIELD30 - Целое поле</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>STRINGFIELD06 - Строковое поле</LI>
     * <LI>STRINGFIELD07 - Строковое поле</LI>
     * <LI>STRINGFIELD08 - Строковое поле</LI>
     * <LI>STRINGFIELD09 - Строковое поле</LI>
     * <LI>STRINGFIELD10 - Строковое поле</LI>
     * <LI>STRINGFIELD11 - Строковое поле</LI>
     * <LI>STRINGFIELD12 - Строковое поле</LI>
     * <LI>STRINGFIELD13 - Строковое поле</LI>
     * <LI>STRINGFIELD14 - Строковое поле</LI>
     * <LI>STRINGFIELD15 - Строковое поле</LI>
     * <LI>STRINGFIELD16 - Строковое поле</LI>
     * <LI>STRINGFIELD17 - Строковое поле</LI>
     * <LI>STRINGFIELD18 - Строковое поле</LI>
     * <LI>STRINGFIELD19 - Строковое поле</LI>
     * <LI>STRINGFIELD20 - Строковое поле</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTREXTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID", "HBDATAVERID", "CONTREXTID"})
    public Map<String,Object> dsB2BContractExtensionInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractExtensionInsert", params);
        result.put("CONTREXTID", params.get("CONTREXTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД контракта</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>DOUBLEFIELD06 - Дробное поле</LI>
     * <LI>DOUBLEFIELD07 - Дробное поле</LI>
     * <LI>DOUBLEFIELD08 - Дробное поле</LI>
     * <LI>DOUBLEFIELD09 - Дробное поле</LI>
     * <LI>DOUBLEFIELD10 - Дробное поле</LI>
     * <LI>DOUBLEFIELD11 - Дробное поле</LI>
     * <LI>DOUBLEFIELD12 - Дробное поле</LI>
     * <LI>DOUBLEFIELD13 - Дробное поле</LI>
     * <LI>DOUBLEFIELD14 - Дробное поле</LI>
     * <LI>DOUBLEFIELD15 - Дробное поле</LI>
     * <LI>DOUBLEFIELD16 - Дробное поле</LI>
     * <LI>DOUBLEFIELD17 - Дробное поле</LI>
     * <LI>DOUBLEFIELD18 - Дробное поле</LI>
     * <LI>DOUBLEFIELD19 - Дробное поле</LI>
     * <LI>DOUBLEFIELD20 - Дробное поле</LI>
     * <LI>DOUBLEFIELD21 - Дробное поле</LI>
     * <LI>DOUBLEFIELD22 - Дробное поле</LI>
     * <LI>DOUBLEFIELD23 - Дробное поле</LI>
     * <LI>DOUBLEFIELD24 - Дробное поле</LI>
     * <LI>DOUBLEFIELD25 - Дробное поле</LI>
     * <LI>DOUBLEFIELD26 - Дробное поле</LI>
     * <LI>DOUBLEFIELD27 - Дробное поле</LI>
     * <LI>DOUBLEFIELD28 - Дробное поле</LI>
     * <LI>DOUBLEFIELD29 - Дробное поле</LI>
     * <LI>DOUBLEFIELD30 - Дробное поле</LI>
     * <LI>DOUBLEFIELD31 - Дробное поле</LI>
     * <LI>DOUBLEFIELD32 - Дробное поле</LI>
     * <LI>DOUBLEFIELD33 - Дробное поле</LI>
     * <LI>DOUBLEFIELD34 - Дробное поле</LI>
     * <LI>DOUBLEFIELD35 - Дробное поле</LI>
     * <LI>DOUBLEFIELD36 - Дробное поле</LI>
     * <LI>DOUBLEFIELD37 - Дробное поле</LI>
     * <LI>DOUBLEFIELD38 - Дробное поле</LI>
     * <LI>DOUBLEFIELD39 - Дробное поле</LI>
     * <LI>DOUBLEFIELD40 - Дробное поле</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>CONTREXTID - ИД записи</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>LONGFIELD06 - Целое поле</LI>
     * <LI>LONGFIELD07 - Целое поле</LI>
     * <LI>LONGFIELD08 - Целое поле</LI>
     * <LI>LONGFIELD09 - Целое поле</LI>
     * <LI>LONGFIELD10 - Целое поле</LI>
     * <LI>LONGFIELD11 - Целое поле</LI>
     * <LI>LONGFIELD12 - Целое поле</LI>
     * <LI>LONGFIELD13 - Целое поле</LI>
     * <LI>LONGFIELD14 - Целое поле</LI>
     * <LI>LONGFIELD15 - Целое поле</LI>
     * <LI>LONGFIELD16 - Целое поле</LI>
     * <LI>LONGFIELD17 - Целое поле</LI>
     * <LI>LONGFIELD18 - Целое поле</LI>
     * <LI>LONGFIELD19 - Целое поле</LI>
     * <LI>LONGFIELD20 - Целое поле</LI>
     * <LI>LONGFIELD21 - Целое поле</LI>
     * <LI>LONGFIELD22 - Целое поле</LI>
     * <LI>LONGFIELD23 - Целое поле</LI>
     * <LI>LONGFIELD24 - Целое поле</LI>
     * <LI>LONGFIELD25 - Целое поле</LI>
     * <LI>LONGFIELD26 - Целое поле</LI>
     * <LI>LONGFIELD27 - Целое поле</LI>
     * <LI>LONGFIELD28 - Целое поле</LI>
     * <LI>LONGFIELD29 - Целое поле</LI>
     * <LI>LONGFIELD30 - Целое поле</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>STRINGFIELD06 - Строковое поле</LI>
     * <LI>STRINGFIELD07 - Строковое поле</LI>
     * <LI>STRINGFIELD08 - Строковое поле</LI>
     * <LI>STRINGFIELD09 - Строковое поле</LI>
     * <LI>STRINGFIELD10 - Строковое поле</LI>
     * <LI>STRINGFIELD11 - Строковое поле</LI>
     * <LI>STRINGFIELD12 - Строковое поле</LI>
     * <LI>STRINGFIELD13 - Строковое поле</LI>
     * <LI>STRINGFIELD14 - Строковое поле</LI>
     * <LI>STRINGFIELD15 - Строковое поле</LI>
     * <LI>STRINGFIELD16 - Строковое поле</LI>
     * <LI>STRINGFIELD17 - Строковое поле</LI>
     * <LI>STRINGFIELD18 - Строковое поле</LI>
     * <LI>STRINGFIELD19 - Строковое поле</LI>
     * <LI>STRINGFIELD20 - Строковое поле</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTREXTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTREXTID"})
    public Map<String,Object> dsB2BContractExtensionUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractExtensionUpdate", params);
        result.put("CONTREXTID", params.get("CONTREXTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД контракта</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>DOUBLEFIELD06 - Дробное поле</LI>
     * <LI>DOUBLEFIELD07 - Дробное поле</LI>
     * <LI>DOUBLEFIELD08 - Дробное поле</LI>
     * <LI>DOUBLEFIELD09 - Дробное поле</LI>
     * <LI>DOUBLEFIELD10 - Дробное поле</LI>
     * <LI>DOUBLEFIELD11 - Дробное поле</LI>
     * <LI>DOUBLEFIELD12 - Дробное поле</LI>
     * <LI>DOUBLEFIELD13 - Дробное поле</LI>
     * <LI>DOUBLEFIELD14 - Дробное поле</LI>
     * <LI>DOUBLEFIELD15 - Дробное поле</LI>
     * <LI>DOUBLEFIELD16 - Дробное поле</LI>
     * <LI>DOUBLEFIELD17 - Дробное поле</LI>
     * <LI>DOUBLEFIELD18 - Дробное поле</LI>
     * <LI>DOUBLEFIELD19 - Дробное поле</LI>
     * <LI>DOUBLEFIELD20 - Дробное поле</LI>
     * <LI>DOUBLEFIELD21 - Дробное поле</LI>
     * <LI>DOUBLEFIELD22 - Дробное поле</LI>
     * <LI>DOUBLEFIELD23 - Дробное поле</LI>
     * <LI>DOUBLEFIELD24 - Дробное поле</LI>
     * <LI>DOUBLEFIELD25 - Дробное поле</LI>
     * <LI>DOUBLEFIELD26 - Дробное поле</LI>
     * <LI>DOUBLEFIELD27 - Дробное поле</LI>
     * <LI>DOUBLEFIELD28 - Дробное поле</LI>
     * <LI>DOUBLEFIELD29 - Дробное поле</LI>
     * <LI>DOUBLEFIELD30 - Дробное поле</LI>
     * <LI>DOUBLEFIELD31 - Дробное поле</LI>
     * <LI>DOUBLEFIELD32 - Дробное поле</LI>
     * <LI>DOUBLEFIELD33 - Дробное поле</LI>
     * <LI>DOUBLEFIELD34 - Дробное поле</LI>
     * <LI>DOUBLEFIELD35 - Дробное поле</LI>
     * <LI>DOUBLEFIELD36 - Дробное поле</LI>
     * <LI>DOUBLEFIELD37 - Дробное поле</LI>
     * <LI>DOUBLEFIELD38 - Дробное поле</LI>
     * <LI>DOUBLEFIELD39 - Дробное поле</LI>
     * <LI>DOUBLEFIELD40 - Дробное поле</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>CONTREXTID - ИД записи</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>LONGFIELD06 - Целое поле</LI>
     * <LI>LONGFIELD07 - Целое поле</LI>
     * <LI>LONGFIELD08 - Целое поле</LI>
     * <LI>LONGFIELD09 - Целое поле</LI>
     * <LI>LONGFIELD10 - Целое поле</LI>
     * <LI>LONGFIELD11 - Целое поле</LI>
     * <LI>LONGFIELD12 - Целое поле</LI>
     * <LI>LONGFIELD13 - Целое поле</LI>
     * <LI>LONGFIELD14 - Целое поле</LI>
     * <LI>LONGFIELD15 - Целое поле</LI>
     * <LI>LONGFIELD16 - Целое поле</LI>
     * <LI>LONGFIELD17 - Целое поле</LI>
     * <LI>LONGFIELD18 - Целое поле</LI>
     * <LI>LONGFIELD19 - Целое поле</LI>
     * <LI>LONGFIELD20 - Целое поле</LI>
     * <LI>LONGFIELD21 - Целое поле</LI>
     * <LI>LONGFIELD22 - Целое поле</LI>
     * <LI>LONGFIELD23 - Целое поле</LI>
     * <LI>LONGFIELD24 - Целое поле</LI>
     * <LI>LONGFIELD25 - Целое поле</LI>
     * <LI>LONGFIELD26 - Целое поле</LI>
     * <LI>LONGFIELD27 - Целое поле</LI>
     * <LI>LONGFIELD28 - Целое поле</LI>
     * <LI>LONGFIELD29 - Целое поле</LI>
     * <LI>LONGFIELD30 - Целое поле</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>STRINGFIELD06 - Строковое поле</LI>
     * <LI>STRINGFIELD07 - Строковое поле</LI>
     * <LI>STRINGFIELD08 - Строковое поле</LI>
     * <LI>STRINGFIELD09 - Строковое поле</LI>
     * <LI>STRINGFIELD10 - Строковое поле</LI>
     * <LI>STRINGFIELD11 - Строковое поле</LI>
     * <LI>STRINGFIELD12 - Строковое поле</LI>
     * <LI>STRINGFIELD13 - Строковое поле</LI>
     * <LI>STRINGFIELD14 - Строковое поле</LI>
     * <LI>STRINGFIELD15 - Строковое поле</LI>
     * <LI>STRINGFIELD16 - Строковое поле</LI>
     * <LI>STRINGFIELD17 - Строковое поле</LI>
     * <LI>STRINGFIELD18 - Строковое поле</LI>
     * <LI>STRINGFIELD19 - Строковое поле</LI>
     * <LI>STRINGFIELD20 - Строковое поле</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTREXTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTREXTID"})
    public Map<String,Object> dsB2BContractExtensionModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractExtensionUpdate", params);
        result.put("CONTREXTID", params.get("CONTREXTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTREXTID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTREXTID"})
    public void dsB2BContractExtensionDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractExtensionDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД контракта</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>DOUBLEFIELD06 - Дробное поле</LI>
     * <LI>DOUBLEFIELD07 - Дробное поле</LI>
     * <LI>DOUBLEFIELD08 - Дробное поле</LI>
     * <LI>DOUBLEFIELD09 - Дробное поле</LI>
     * <LI>DOUBLEFIELD10 - Дробное поле</LI>
     * <LI>DOUBLEFIELD11 - Дробное поле</LI>
     * <LI>DOUBLEFIELD12 - Дробное поле</LI>
     * <LI>DOUBLEFIELD13 - Дробное поле</LI>
     * <LI>DOUBLEFIELD14 - Дробное поле</LI>
     * <LI>DOUBLEFIELD15 - Дробное поле</LI>
     * <LI>DOUBLEFIELD16 - Дробное поле</LI>
     * <LI>DOUBLEFIELD17 - Дробное поле</LI>
     * <LI>DOUBLEFIELD18 - Дробное поле</LI>
     * <LI>DOUBLEFIELD19 - Дробное поле</LI>
     * <LI>DOUBLEFIELD20 - Дробное поле</LI>
     * <LI>DOUBLEFIELD21 - Дробное поле</LI>
     * <LI>DOUBLEFIELD22 - Дробное поле</LI>
     * <LI>DOUBLEFIELD23 - Дробное поле</LI>
     * <LI>DOUBLEFIELD24 - Дробное поле</LI>
     * <LI>DOUBLEFIELD25 - Дробное поле</LI>
     * <LI>DOUBLEFIELD26 - Дробное поле</LI>
     * <LI>DOUBLEFIELD27 - Дробное поле</LI>
     * <LI>DOUBLEFIELD28 - Дробное поле</LI>
     * <LI>DOUBLEFIELD29 - Дробное поле</LI>
     * <LI>DOUBLEFIELD30 - Дробное поле</LI>
     * <LI>DOUBLEFIELD31 - Дробное поле</LI>
     * <LI>DOUBLEFIELD32 - Дробное поле</LI>
     * <LI>DOUBLEFIELD33 - Дробное поле</LI>
     * <LI>DOUBLEFIELD34 - Дробное поле</LI>
     * <LI>DOUBLEFIELD35 - Дробное поле</LI>
     * <LI>DOUBLEFIELD36 - Дробное поле</LI>
     * <LI>DOUBLEFIELD37 - Дробное поле</LI>
     * <LI>DOUBLEFIELD38 - Дробное поле</LI>
     * <LI>DOUBLEFIELD39 - Дробное поле</LI>
     * <LI>DOUBLEFIELD40 - Дробное поле</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>CONTREXTID - ИД записи</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>LONGFIELD06 - Целое поле</LI>
     * <LI>LONGFIELD07 - Целое поле</LI>
     * <LI>LONGFIELD08 - Целое поле</LI>
     * <LI>LONGFIELD09 - Целое поле</LI>
     * <LI>LONGFIELD10 - Целое поле</LI>
     * <LI>LONGFIELD11 - Целое поле</LI>
     * <LI>LONGFIELD12 - Целое поле</LI>
     * <LI>LONGFIELD13 - Целое поле</LI>
     * <LI>LONGFIELD14 - Целое поле</LI>
     * <LI>LONGFIELD15 - Целое поле</LI>
     * <LI>LONGFIELD16 - Целое поле</LI>
     * <LI>LONGFIELD17 - Целое поле</LI>
     * <LI>LONGFIELD18 - Целое поле</LI>
     * <LI>LONGFIELD19 - Целое поле</LI>
     * <LI>LONGFIELD20 - Целое поле</LI>
     * <LI>LONGFIELD21 - Целое поле</LI>
     * <LI>LONGFIELD22 - Целое поле</LI>
     * <LI>LONGFIELD23 - Целое поле</LI>
     * <LI>LONGFIELD24 - Целое поле</LI>
     * <LI>LONGFIELD25 - Целое поле</LI>
     * <LI>LONGFIELD26 - Целое поле</LI>
     * <LI>LONGFIELD27 - Целое поле</LI>
     * <LI>LONGFIELD28 - Целое поле</LI>
     * <LI>LONGFIELD29 - Целое поле</LI>
     * <LI>LONGFIELD30 - Целое поле</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>STRINGFIELD06 - Строковое поле</LI>
     * <LI>STRINGFIELD07 - Строковое поле</LI>
     * <LI>STRINGFIELD08 - Строковое поле</LI>
     * <LI>STRINGFIELD09 - Строковое поле</LI>
     * <LI>STRINGFIELD10 - Строковое поле</LI>
     * <LI>STRINGFIELD11 - Строковое поле</LI>
     * <LI>STRINGFIELD12 - Строковое поле</LI>
     * <LI>STRINGFIELD13 - Строковое поле</LI>
     * <LI>STRINGFIELD14 - Строковое поле</LI>
     * <LI>STRINGFIELD15 - Строковое поле</LI>
     * <LI>STRINGFIELD16 - Строковое поле</LI>
     * <LI>STRINGFIELD17 - Строковое поле</LI>
     * <LI>STRINGFIELD18 - Строковое поле</LI>
     * <LI>STRINGFIELD19 - Строковое поле</LI>
     * <LI>STRINGFIELD20 - Строковое поле</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД контракта</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>DOUBLEFIELD06 - Дробное поле</LI>
     * <LI>DOUBLEFIELD07 - Дробное поле</LI>
     * <LI>DOUBLEFIELD08 - Дробное поле</LI>
     * <LI>DOUBLEFIELD09 - Дробное поле</LI>
     * <LI>DOUBLEFIELD10 - Дробное поле</LI>
     * <LI>DOUBLEFIELD11 - Дробное поле</LI>
     * <LI>DOUBLEFIELD12 - Дробное поле</LI>
     * <LI>DOUBLEFIELD13 - Дробное поле</LI>
     * <LI>DOUBLEFIELD14 - Дробное поле</LI>
     * <LI>DOUBLEFIELD15 - Дробное поле</LI>
     * <LI>DOUBLEFIELD16 - Дробное поле</LI>
     * <LI>DOUBLEFIELD17 - Дробное поле</LI>
     * <LI>DOUBLEFIELD18 - Дробное поле</LI>
     * <LI>DOUBLEFIELD19 - Дробное поле</LI>
     * <LI>DOUBLEFIELD20 - Дробное поле</LI>
     * <LI>DOUBLEFIELD21 - Дробное поле</LI>
     * <LI>DOUBLEFIELD22 - Дробное поле</LI>
     * <LI>DOUBLEFIELD23 - Дробное поле</LI>
     * <LI>DOUBLEFIELD24 - Дробное поле</LI>
     * <LI>DOUBLEFIELD25 - Дробное поле</LI>
     * <LI>DOUBLEFIELD26 - Дробное поле</LI>
     * <LI>DOUBLEFIELD27 - Дробное поле</LI>
     * <LI>DOUBLEFIELD28 - Дробное поле</LI>
     * <LI>DOUBLEFIELD29 - Дробное поле</LI>
     * <LI>DOUBLEFIELD30 - Дробное поле</LI>
     * <LI>DOUBLEFIELD31 - Дробное поле</LI>
     * <LI>DOUBLEFIELD32 - Дробное поле</LI>
     * <LI>DOUBLEFIELD33 - Дробное поле</LI>
     * <LI>DOUBLEFIELD34 - Дробное поле</LI>
     * <LI>DOUBLEFIELD35 - Дробное поле</LI>
     * <LI>DOUBLEFIELD36 - Дробное поле</LI>
     * <LI>DOUBLEFIELD37 - Дробное поле</LI>
     * <LI>DOUBLEFIELD38 - Дробное поле</LI>
     * <LI>DOUBLEFIELD39 - Дробное поле</LI>
     * <LI>DOUBLEFIELD40 - Дробное поле</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>CONTREXTID - ИД записи</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>LONGFIELD06 - Целое поле</LI>
     * <LI>LONGFIELD07 - Целое поле</LI>
     * <LI>LONGFIELD08 - Целое поле</LI>
     * <LI>LONGFIELD09 - Целое поле</LI>
     * <LI>LONGFIELD10 - Целое поле</LI>
     * <LI>LONGFIELD11 - Целое поле</LI>
     * <LI>LONGFIELD12 - Целое поле</LI>
     * <LI>LONGFIELD13 - Целое поле</LI>
     * <LI>LONGFIELD14 - Целое поле</LI>
     * <LI>LONGFIELD15 - Целое поле</LI>
     * <LI>LONGFIELD16 - Целое поле</LI>
     * <LI>LONGFIELD17 - Целое поле</LI>
     * <LI>LONGFIELD18 - Целое поле</LI>
     * <LI>LONGFIELD19 - Целое поле</LI>
     * <LI>LONGFIELD20 - Целое поле</LI>
     * <LI>LONGFIELD21 - Целое поле</LI>
     * <LI>LONGFIELD22 - Целое поле</LI>
     * <LI>LONGFIELD23 - Целое поле</LI>
     * <LI>LONGFIELD24 - Целое поле</LI>
     * <LI>LONGFIELD25 - Целое поле</LI>
     * <LI>LONGFIELD26 - Целое поле</LI>
     * <LI>LONGFIELD27 - Целое поле</LI>
     * <LI>LONGFIELD28 - Целое поле</LI>
     * <LI>LONGFIELD29 - Целое поле</LI>
     * <LI>LONGFIELD30 - Целое поле</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>STRINGFIELD06 - Строковое поле</LI>
     * <LI>STRINGFIELD07 - Строковое поле</LI>
     * <LI>STRINGFIELD08 - Строковое поле</LI>
     * <LI>STRINGFIELD09 - Строковое поле</LI>
     * <LI>STRINGFIELD10 - Строковое поле</LI>
     * <LI>STRINGFIELD11 - Строковое поле</LI>
     * <LI>STRINGFIELD12 - Строковое поле</LI>
     * <LI>STRINGFIELD13 - Строковое поле</LI>
     * <LI>STRINGFIELD14 - Строковое поле</LI>
     * <LI>STRINGFIELD15 - Строковое поле</LI>
     * <LI>STRINGFIELD16 - Строковое поле</LI>
     * <LI>STRINGFIELD17 - Строковое поле</LI>
     * <LI>STRINGFIELD18 - Строковое поле</LI>
     * <LI>STRINGFIELD19 - Строковое поле</LI>
     * <LI>STRINGFIELD20 - Строковое поле</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractExtensionBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractExtensionBrowseListByParam", "dsB2BContractExtensionBrowseListByParamCount", params);
        return result;
    }





}
