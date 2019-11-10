/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("B2BProductDamageCategoryCustom")
public class B2BProductDamageCategoryCustomFacade extends B2BBaseFacade{

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductDamageCategoryStructBrowseByParamEx(Map<String, Object> params) {
        //1. по продвер ид получаем перечень категорий ущербов по продукту.
        //2. из перечня формируем список ИД категорий ущербов
        //3. по перечню ид категорий получаем перечень содержимого категорий.
        //4. ходим по категориям, и раскладываем в них содержимое. попутно формируем перечень доступных видов событий событий
        //5. пл перечню категорий по продукту получаем перечень категорий ущербов. в них есть связи с видами событий,
        // и hbdataverid справочников показателей и детализации
        //
        return null;
    }

}
