/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom;

import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * Кастомный фасад для работы с состояниями
 *
 * @author averichevsm
 */
@BOName("B2BStateCustom")
public class B2BStateCustomFacade extends BaseFacade {

    // получение списка со сведениями о доступных состояниях (в том числе с ограничениями по типу объекта)
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BStateBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BStateBrowseListByParamEx", "dsB2BStateBrowseListByParamExCount", params);
        return result;
    }
    
    @WsMethod(requiredParams = {"USERACCOUNTID"})
    public Map<String, Object> dsGetRoleListByUserAccountId(Map<String, Object> params) throws Exception {
        return this.selectQuery("dsGetRoleListByUserAccountId", "dsGetRoleListByUserAccountIdCount", params);
    }

}
