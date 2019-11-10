/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.dictionary;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import static com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryConstants.DCT_MODULE_PREFIX_MAP;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author kiryanov_as
 */
@BOName("DictionaryBinaryFileCustom")
public class DictionaryBinaryFileCustomFacade extends B2BDictionaryBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    @WsMethod(requiredParams = {"HIBERNATEENTITY", "OBJID"})
    public Map<String, Object> dsB2BDictionaryCreateBinaryFileInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDictionaryCreateBinaryFileInfo begin");
        Map<String, Object> result = createBinaryFileInfo(params);
        logger.debug("dsB2BDictionaryCreateBinaryFileInfo end");
        return result;
    }

    @WsMethod(requiredParams = {"HIBERNATEENTITY", "OBJID"})
    public Map<String, Object> dsB2BDictionaryUpdateBinaryFileInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDictionaryUpdateBinaryFileInfo begin");
        Map<String, Object> result = updateBinaryFileInfo(params);
        logger.debug("dsB2BDictionaryUpdateBinaryFileInfo end");
        return result;
    }

    @WsMethod(requiredParams = {"HIBERNATEENTITY", "OBJID"})
    public Map<String, Object> dsB2BDictionaryDeleteBinaryFileInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDictionaryDeleteBinaryFileInfo begin");
        Map<String, Object> result = deleteBinaryFileInfo(params);
        logger.debug("dsB2BDictionaryDeleteBinaryFileInfo end");
        return result;
    }

    @WsMethod(requiredParams = {"HIBERNATEENTITY", "OBJID"})
    public Map<String, Object> dsB2BDictionaryGetBinaryFileInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDictionaryGetBinaryFileInfo begin");
        Map<String, Object> result = dctGetBinaryFileInfo(params);
        logger.debug("dsB2BDictionaryGetBinaryFileInfo end");
        return result;
    }
}
