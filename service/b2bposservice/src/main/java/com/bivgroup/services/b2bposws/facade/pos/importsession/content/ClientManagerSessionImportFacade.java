package com.bivgroup.services.b2bposws.facade.pos.importsession.content;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bivgroup.services.b2bposws.facade.pos.importsession.common.ImportSessionType.*;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("ClientManagerImportStructure")
public class ClientManagerSessionImportFacade extends B2BDictionaryBaseFacade {

    private static final Long IMPORT_STATEMAP_ID = 9400L;

    @WsMethod()
    public Map<String, Object> dsB2BclientManagerGetStructureImportList(Map<String, Object> params) throws Exception {
        // тип сессии определяет гибернетовский дискриминатор
        params.put("DISCRIMINATOR", IMPORT_SESSION_DEPARTMENT.getType());
        String login = (String) params.get(LOGIN);
        String password = (String) params.get(PASSWORD);
        return this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BclientManagerGetImportList", params, login, password);
    }

    @WsMethod()
    public Map<String, Object> dsB2BclientManagerGetContractsImportList(Map<String, Object>  params) throws Exception {
        // тип сессии определяет гибернетовский дискриминатор
        params.put("DISCRIMINATOR", IMPORT_SESSION_MANAGER_CONTRACT.getType());
        String login = (String) params.get(LOGIN);
        String password = (String) params.get(PASSWORD);
        return this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BclientManagerGetImportList", params, login, password);
    }

    @WsMethod()
    public Map<String, Object> dsB2BclientManagerGetVSPImportList(Map<String, Object>  params) throws Exception {
        // тип сессии определяет гибернетовский дискриминатор
        params.put("DISCRIMINATOR", IMPORT_SESSION_MANAGER_DEPARTMENT.getType());
        String login = (String) params.get(LOGIN);
        String password = (String) params.get(PASSWORD);
        return this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BclientManagerGetImportList", params, login, password);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BclientManagerGetImportStates(Map<String, Object> params) throws Exception {

        Map<String, Object> example = new HashMap<>();
        example.put("typeId", IMPORT_STATEMAP_ID);
        List<Map<String, Object>> list = dctFindByExample(SM_STATE_ENTITY_NAME, example);
        return B2BResult.ok(list);
    }

    @WsMethod()
    public Map<String, Object> dsB2BclientManagerGetImportList(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsClientManagerBrowseImportSessionList", params);
        return result;
    }

}
