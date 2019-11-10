package com.bivgroup.services.b2bposws.facade.pos.userPost;

import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BinaryFile(objTableName = "SD_UserPost", objTablePKFieldName = "ID")
@BOName("B2BUserPostAttachment")
public class B2BUserPostAttachmentFacade extends BaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BUserPostAttachmentBrowseListByParam(Map<String, Object> params) throws Exception {
        return new HashMap<String, Object>();
    }

}
