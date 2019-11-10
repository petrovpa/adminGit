package com.bivgroup.services.b2bposws.facade.pos.loss;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;

@BOName("B2BLossNoticeAttachment")
@BinaryFile(objTableName = "B2B_LOSSNOTICE", objTablePKFieldName = "LOSSNOTICEID")
public class B2BLossNoticeAttachmentFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BLossNoticeAttachmentBrowseListByParam(Map<String, Object> params) throws Exception {
        return new HashMap<String, Object>();
    }

    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BLossNoticeReportDataProvider(Map<String,Object> params) {
        return new HashMap<String, Object>();
    }

}
