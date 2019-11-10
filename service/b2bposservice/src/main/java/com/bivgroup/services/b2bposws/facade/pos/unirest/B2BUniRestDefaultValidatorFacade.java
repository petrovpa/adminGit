package com.bivgroup.services.b2bposws.facade.pos.unirest;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;

@BOName("B2BUniRestDefaultValidator")
public class B2BUniRestDefaultValidatorFacade extends B2BLifeBaseFacade {

    /**
     * Метод валидации по умолчанию рест договора
     * @param params DATAMAP - договор страхования
     * @return возвращает в мапе VALIDATIONERROR - сообщение об ошибки
     */
    @WsMethod(requiredParams = {"DATAMAP"})
    public Map<String, Object> defaultUniRestLoadDataMapValidation(Map<String, Object> params) {
        // TODO: Реализовать стандартные для всех проверки
        return new HashMap<>();
    }
}
