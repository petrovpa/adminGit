/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomBaseFacade.base64Decode;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author kkulkov
 */
@BOName("InsClientActionLogCustom")
public class InsClientActionLogCustomFacade extends BaseFacade {

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTION - совершенное действие</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * <LI>NOTE - Описание действия</LI>
     * <LI>PARAM1 - null</LI>
     * <LI>PARAM2 - null</LI>
     * <LI>PARAM3 - null</LI>
     * <LI>SESSIONID - ид сессии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ACTION - совершенное действие</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * <LI>NOTE - Описание действия</LI>
     * <LI>PARAM1 - null</LI>
     * <LI>PARAM2 - null</LI>
     * <LI>PARAM3 - null</LI>
     * <LI>SESSIONID - ид сессии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsInsClientActionLogBrowseListByParamEx(Map<String, Object> params) throws Exception {
        if (params.get(ORDERBY) == null) {
            params.put(ORDERBY, "T.CREATEDATE");
        }
        if ((params.get("ACTION") != null) && (params.get("ACTION").toString().isEmpty())) {
            params.remove("ACTION");
        }
        if (params.get("SESSIONTOKEN") != null) {
            String token = params.get("SESSIONTOKEN").toString();
            params.put("SESSIONID", base64Decode(token));
        }
        Map<String, Object> result = this.selectQuery("dsInsClientActionLogBrowseListByParamEx", "dsInsClientActionLogBrowseListByParamExCount", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsInsClientActionLogTypeBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsInsClientActionLogTypeBrowseListByParamEx", "dsInsClientActionLogTypeBrowseListByParamExCount", params);
        return result;
    }
}
