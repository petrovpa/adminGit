package com.bivgroup.services.b2bposws.facade.pos.subsystem;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;

@BOName("B2bSubSystemCustom")
public class B2bSubSystemCustomFacade extends BaseFacade {
    //b2bSubSystemByParamEx

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @param params <UL>
     *               <LI>SUBSYSTEMID - ИД подсистемы</LI>
     *               <LI>NAME - Имя подсистемы</LI>
     *               <LI>SYSNAME - Системное имя подсистемы</LI>
     *               <LI>EXTERNALID - Внешний идентификатор подсистемы</LI>
     *               </UL>
     * @return <UL>
     * <LI>SUBSYSTEMID - ИД подсистемы</LI>
     * <LI>NAME - Имя подсистемы</LI>
     * <LI>SYSNAME - Системное имя подсистемы</LI>
     * <LI>EXTERNALID - Внешний идентификатор подсистемы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> b2bSubSystemByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("b2bSubSystemByParamEx", "b2bSubSystemByParamExCount", params);
        return result;
    }
}
