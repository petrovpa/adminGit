package com.bivgroup.services.b2bposws.facade.pos.declaration.change;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;

// Допсы - Прикрепление файлов напрямую к заявлению на изменение условий договора страхования
@BOName("B2BDeclarationOfChangeAttachment")
@BinaryFile(objTableName = "PD_DECLARATIONOFCHANGE", objTablePKFieldName = "ID")
public class B2BDeclarationOfChangeAttachmentFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BDeclarationOfChangeAttachmentBrowseListByParam(Map<String, Object> params) throws Exception {
        return new HashMap<String, Object>();
    }

}
