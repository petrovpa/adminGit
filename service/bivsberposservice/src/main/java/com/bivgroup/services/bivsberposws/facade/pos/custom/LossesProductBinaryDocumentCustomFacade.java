/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import java.util.Map;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * Кастомный фасад для сущности RefundProductBinaryDocument
 *
 * @author ilich
 */
@IdGen(entityName = "LOSS_PRODBINDOC", idFieldName = "PRODBINDOCID")
@BOName("LossesProductBinaryDocumentCustom")
public class LossesProductBinaryDocumentCustomFacade extends BaseFacade {

    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>BINDOCTYPE - Тип документа по причине возврата</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>PRODID - ИД конфигурации продукта</LI>
     * <LI>PRODKINDID - ИД группы проэктов</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>BINDOCTYPE - Тип документа по причине возврата</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>PRODID - ИД конфигурации продукта</LI>
     * <LI>PRODKINDID - ИД группы проэктов</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsLossesProductBinaryDocumentBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsLossesProductBinaryDocumentBrowseListByParamEx", "dsLossesProductBinaryDocumentBrowseListByParamExCount", params);
        return result;
    }
}
