package com.bivgroup.services.b2bposws.facade.pos.contract.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author arazumovsky
 */
@BOName("B2BDocumentTypeCustom")
public class B2BDocumentTypeCustomFacade extends B2BBaseFacade {

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author arazumovsky
     * @param params
     * <UL>
     * <LI>TYPEID - ID КЛИЕНТА</LI>
     * <LI>EID - ID СУЩНОСТИ</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TYPEID - ID КЛИЕНТА</LI>
     * <LI>EID - ID СУЩНОСТИ</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     */
    @WsMethod()
    public Map<String, Object> dsB2BDocumentTypeBrowseListByParamEx(Map<String, Object> params) throws Exception {
        return this.selectQuery("dsB2BDocumentTypeBrowseListByParamEx", "dsB2BDocumentTypeBrowseListByParamExCount", params);
    }
}
