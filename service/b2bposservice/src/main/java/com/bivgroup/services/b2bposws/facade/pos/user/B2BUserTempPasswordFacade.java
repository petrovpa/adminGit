package com.bivgroup.services.b2bposws.facade.pos.user;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;

@BOName("B2BUserTempPasswordFacade")
@IdGen(entityName = "CORE_USERTEMPPASSWORD", idFieldName = "USERTEMPPASSWORDID")
public class B2BUserTempPasswordFacade extends B2BDictionaryBaseFacade {

    /**
     * Создание временного пароля c генерацией ИД
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod
    public Map<String, Object> dsUserTempPasswordCreate(final Map<String, Object> params) throws Exception {
        this.insertQuery("dsUserTempPasswordInsert", params);
        return new HashMap<String, Object>() {{ put("USERTEMPPASSWORDID", params.get("USERTEMPPASSWORDID")); }};
    }


    /**
     * Создание временного пароля без генерации ИД
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"USERTEMPPASSWORDID"})
    public Map<String, Object> dsUserTempPasswordInsert(final Map<String, Object> params) throws Exception {
        this.insertQuery("dsUserTempPasswordInsert", params);
        return new HashMap<String, Object>() {{ put("USERTEMPPASSWORDID", params.get("USERTEMPPASSWORDID")); }};
    }


    /**
     * Обновление временного пароля пользователя
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"USERTEMPPASSWORDID"})
    public Map<String, Object> dsUserTempPasswordUpdate(final Map<String, Object> params) throws Exception {
        this.updateQuery("dsUserTempPasswordUpdate", params);
        return new HashMap<String, Object>() {{ put("USERTEMPPASSWORDID", params.get("USERTEMPPASSWORDID")); }};
    }

    /**
     * Обновление временного пароля пользователя
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"USERTEMPPASSWORDID"})
    public Map<String, Object> dsUserTempPasswordModify(final Map<String, Object> params) throws Exception {
        this.updateQuery("dsUserTempPasswordUpdate", params);
        return new HashMap<String, Object>() {{ put("USERTEMPPASSWORDID", params.get("USERTEMPPASSWORDID")); }};
    }

    /**
     * Удаление временного пароля пользователя
     *
     * @param params
     * @throws Exception
     */
    @WsMethod(requiredParams = {"USERTEMPPASSWORDID"})
    public void dsUserTempPasswordDelete(final Map<String, Object> params) throws Exception {
        this.deleteQuery("dsUserTempPasswordDelete", params);
    }


    /**
     * Получить временные пароли пользователей по ограничениям
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod
    public Map<String, Object> dsUserTempPasswordBrowseListByParam(final Map<String, Object> params) throws Exception {
        return this.selectQuery("dsUserTempPasswordBrowseListByParam", "dsUserTempPasswordBrowseListByParamCount", params);
    }

}
