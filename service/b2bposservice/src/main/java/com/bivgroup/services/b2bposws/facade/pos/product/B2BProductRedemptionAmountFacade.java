/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.external.ExternalService;


/**
 * Фасад для сущности B2BProductRedemptionAmount
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_PRODREDEMPTIONAMOUNT",idFieldName="REDEMPTIONAMOUNTID")
@BOName("B2BProductRedemptionAmount")
public class B2BProductRedemptionAmountFacade extends BaseFacade {

    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>PERCENT - Коэффициент ВС</LI>
     * <LI>PRODCONFID - Ссылка на конфигурацию продукта</LI>
     * <LI>PRODINSAMCURID - Валюта</LI>
     * <LI>PRODINVESTID - Ссылка на инвестиционную стратегию по продукту</LI>
     * <LI>PRODPAYVARID - Периодичность оплаты</LI>
     * <LI>PRODTERMID - Срок страхования по продукту</LI>
     * <LI>REDEMPTIONAMOUNTID - ИД Выкупной суммы</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>YEAR - Номер года</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REDEMPTIONAMOUNTID - ИД Выкупной суммы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REDEMPTIONAMOUNTID"})
    public Map<String,Object> dsB2BProductRedemptionAmountInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        assurLevelFix(params);
        this.insertQuery("dsB2BProductRedemptionAmountInsert", params);
        result.put("REDEMPTIONAMOUNTID", params.get("REDEMPTIONAMOUNTID"));
        return result;
    }

    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>PERCENT - Коэффициент ВС</LI>
     * <LI>PRODCONFID - Ссылка на конфигурацию продукта</LI>
     * <LI>PRODINSAMCURID - Валюта</LI>
     * <LI>PRODINVESTID - Ссылка на инвестиционную стратегию по продукту</LI>
     * <LI>PRODPAYVARID - Периодичность оплаты</LI>
     * <LI>PRODTERMID - Срок страхования по продукту</LI>
     * <LI>REDEMPTIONAMOUNTID - ИД Выкупной суммы</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>YEARNUMBER - Номер года</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REDEMPTIONAMOUNTID - ИД Выкупной суммы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductRedemptionAmountCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        assurLevelFix(params);
        this.insertQuery("dsB2BProductRedemptionAmountInsert", params);
        result.put("REDEMPTIONAMOUNTID", params.get("REDEMPTIONAMOUNTID"));
        return result;
    }

    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>PERCENT - Коэффициент ВС</LI>
     * <LI>PRODCONFID - Ссылка на конфигурацию продукта</LI>
     * <LI>PRODINSAMCURID - Валюта</LI>
     * <LI>PRODINVESTID - Ссылка на инвестиционную стратегию по продукту</LI>
     * <LI>PRODPAYVARID - Периодичность оплаты</LI>
     * <LI>PRODTERMID - Срок страхования по продукту</LI>
     * <LI>REDEMPTIONAMOUNTID - ИД Выкупной суммы</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>YEARNUMBER - Номер года</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REDEMPTIONAMOUNTID - ИД Выкупной суммы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REDEMPTIONAMOUNTID"})
    public Map<String,Object> dsB2BProductRedemptionAmountUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        assurLevelFix(params);
        this.updateQuery("dsB2BProductRedemptionAmountUpdate", params);
        result.put("REDEMPTIONAMOUNTID", params.get("REDEMPTIONAMOUNTID"));
        return result;
    }

    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>PERCENT - Коэффициент ВС</LI>
     * <LI>PRODCONFID - Ссылка на конфигурацию продукта</LI>
     * <LI>PRODINSAMCURID - Валюта</LI>
     * <LI>PRODINVESTID - Ссылка на инвестиционную стратегию по продукту</LI>
     * <LI>PRODPAYVARID - Периодичность оплаты</LI>
     * <LI>PRODTERMID - Срок страхования по продукту</LI>
     * <LI>REDEMPTIONAMOUNTID - ИД Выкупной суммы</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>YEARNUMBER - Номер года</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REDEMPTIONAMOUNTID - ИД Выкупной суммы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REDEMPTIONAMOUNTID"})
    public Map<String,Object> dsB2BProductRedemptionAmountModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        assurLevelFix(params);
        this.updateQuery("dsB2BProductRedemptionAmountUpdate", params);
        result.put("REDEMPTIONAMOUNTID", params.get("REDEMPTIONAMOUNTID"));
        return result;
    }

    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>REDEMPTIONAMOUNTID - ИД Выкупной суммы</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"REDEMPTIONAMOUNTID"})
    public void dsB2BProductRedemptionAmountDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductRedemptionAmountDelete", params);
    }

    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODVERID - Ссылка на версию продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>PERCENT - Коэффициент ВС</LI>
     * <LI>PRODCONFID - Ссылка на конфигурацию продукта</LI>
     * <LI>PRODINSAMCURID - Валюта</LI>
     * <LI>PRODINVESTID - Ссылка на инвестиционную стратегию по продукту</LI>
     * <LI>PRODPAYVARID - Периодичность оплаты</LI>
     * <LI>PRODTERMID - Срок страхования по продукту</LI>
     * <LI>REDEMPTIONAMOUNTID - ИД Выкупной суммы</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>YEARNUMBER - Номер года</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductRedemptionAmountBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductRedemptionAmountBrowseListByParam", "dsB2BProductRedemptionAmountBrowseListByParamCount", params);
        return result;
    }


    /**
     * Создать объекты с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>PERCENT - Коэффициент ВС</LI>
     * <LI>PRODCONFID - Ссылка на конфигурацию продукта</LI>
     * <LI>PRODINSAMCURID - Валюта</LI>
     * <LI>PRODINVESTID - Ссылка на инвестиционную стратегию по продукту</LI>
     * <LI>PRODPAYVARID - Периодичность оплаты</LI>
     * <LI>PRODTERMID - Срок страхования по продукту</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>YEARNUMBER - Номер года</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>Заменить</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductRedemptionAmountMassCreateOrUpdate(Map<String, Object> params) throws Exception {

        assurLevelFix(params);
        List<Integer> percents = (List<Integer>) params.remove("PERCENT");
        int size = percents.size();
        for (int i = 0; i < size ; i++) {
            params.put("YEARNUMBER",i+1);
            params.put("PERCENT",percents.get(i));
            ExternalService externalService = this.getExternalService();
            Long generatedContendID = getLongParam(externalService.getNewId("B2B_PRODREDEMPTIONAMOUNT"));
            if (generatedContendID != null) {
                params.put("REDEMPTIONAMOUNTID", generatedContendID);
                this.insertQuery("dsB2BProductRedemptionAmountInsert", params);
            }
        }
        return new HashMap<>();
    }

    protected static Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    private static void assurLevelFix(Map<String, Object> params){
        //с интерфейса не должно приходить null в этом параметре - если пофикшено, можно удалять метод
        if (!params.containsKey("ASSURLEVEL"))
            params.put("ASSURLEVEL",0);
    }

}
