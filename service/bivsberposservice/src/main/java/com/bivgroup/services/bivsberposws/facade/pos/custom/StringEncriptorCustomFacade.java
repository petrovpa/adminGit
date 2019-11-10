/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;

/**
 *
 * @author averichevsm
 */
@BOName("StringEncriptorCustom")
public class StringEncriptorCustomFacade extends BaseFacade {
    
    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final private byte[] Salt = {
        (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
        (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};    
    
@WsMethod(requiredParams = {"INPUTSTR"})
    public Map<String, Object> dsEncriptString(Map<String, Object> params) throws Exception {
        StringCryptUtils scu = new StringCryptUtils(EncryptionPassword,Salt);
        Map<String, Object> result = new HashMap<String,Object>();
        String input = params.get("INPUTSTR").toString();
        String output = scu.encrypt(input);
        result.put("OUTPUTSTR", output);
        return result;
    }    
    
}
