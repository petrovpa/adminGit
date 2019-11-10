/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.clientProfile;

import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author 199
 */
@BinaryFile(objTableName = "CDM_CLIENT", objTablePKFieldName = "CLIENTID")
@BOName("B2BClientProfileAttachment")
public class B2BClientProfileAttachmentFacade extends BaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BClientProfileAttachmentBrowseListByParam(Map<String, Object> params) throws Exception {
        return new HashMap<String, Object>();
    }

}
