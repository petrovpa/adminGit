package com.bivgroup.services.b2bposws.facade.pos.unirest.protectedBorrowerLongTerm;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Фасад с для анализа, работы и сохранение рест договора
 * по продукту "Защищенный заемщик. Многолетний"
 */
@BOName("B2BUniRestProtectBorrowerLongTerm")
public class B2BUniRestProtectBorrowerLongTermFacade extends B2BLifeBaseFacade {

    /**
     * Метод валидации рест договора по продукту "Защищенный заемщик. Многолетний"
     *
     * @param params DATAMAP - договор страхования
     * @return возвращает в мапе VALIDATIONERROR - сообщение об ошибки
     */
    @WsMethod(requiredParams = {"DATAMAP"})
    public Map<String, Object> protectBorrowerLongTermDataMapValidation(Map<String, Object> params) {
        // TODO: Узнать какая нужна валидация, возможно использовать ту, которая используется в обычном продукте, но тогда потребуется ремаппинг
        return new HashMap<>();
    }

    /**
     * Метод расчета премии рест договора по продукту "Защищенный заемщик. Многолетний"
     *
     * @param params CONTRMAP - договор страхования
     * @return возвращает в мапе VALIDATIONERROR - сообщение об ошибки
     */
    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> protectBorrowerLongTermDataMapCalculation(Map<String, Object> params) {

        return new HashMap<>();
    }
}
