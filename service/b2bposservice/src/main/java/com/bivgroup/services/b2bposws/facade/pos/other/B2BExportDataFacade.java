/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.other;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.aspect.impl.autonumber.AutoNumber;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BExportData
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@State(idFieldName = "EXPORTDATAID", startStateName = "B2B_EXPORTDATA_NEW", typeSysName = "B2B_EXPORTDATA")
@BinaryFile(objTableName = "B2B_EXPORTDATA", objTablePKFieldName = "EXPORTDATAID")
@ProfileRights({
        @PRight(sysName="RPAccessPOS_Branch",
                name="Доступ по подразделению",
                joinStr="  inner join B2B_EXPORTDATAORGSTRUCT AOS on (t.EXPORTDATAID = AOS.EXPORTDATAID) inner join INS_DEPLVL DEPLVL on (AOS.ORGSTRUCTID = DEPLVL.OBJECTID) ",
                restrictionFieldName="DEPLVL.PARENTID",
                paramName="DEPARTMENTID")})
@AutoNumber(autoNumberFieldName = "DATANUMBER",dataParamName = "CREATEDATE")
@IdGen(entityName="B2B_EXPORTDATA",idFieldName="EXPORTDATAID")
@BOName("B2BExportData")
public class B2BExportDataFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EXPORTDATAID - Ид</LI>
     * <LI>FINISHDATE - Дата по</LI>
     * <LI>STARTDATE - Дата с</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TEMPLATEID - Ссылка на объект учета «Шаблон выгрузки»</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>EXPORTDATAID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BExportDataCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BExportDataInsert", params);
        result.put("EXPORTDATAID", params.get("EXPORTDATAID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EXPORTDATAID - Ид</LI>
     * <LI>FINISHDATE - Дата по</LI>
     * <LI>STARTDATE - Дата с</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TEMPLATEID - Ссылка на объект учета «Шаблон выгрузки»</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>EXPORTDATAID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"EXPORTDATAID"})
    public Map<String,Object> dsB2BExportDataInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BExportDataInsert", params);
        result.put("EXPORTDATAID", params.get("EXPORTDATAID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EXPORTDATAID - Ид</LI>
     * <LI>FINISHDATE - Дата по</LI>
     * <LI>STARTDATE - Дата с</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TEMPLATEID - Ссылка на объект учета «Шаблон выгрузки»</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>EXPORTDATAID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"EXPORTDATAID"})
    public Map<String,Object> dsB2BExportDataUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BExportDataUpdate", params);
        result.put("EXPORTDATAID", params.get("EXPORTDATAID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EXPORTDATAID - Ид</LI>
     * <LI>FINISHDATE - Дата по</LI>
     * <LI>STARTDATE - Дата с</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TEMPLATEID - Ссылка на объект учета «Шаблон выгрузки»</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>EXPORTDATAID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"EXPORTDATAID"})
    public Map<String,Object> dsB2BExportDataModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BExportDataUpdate", params);
        result.put("EXPORTDATAID", params.get("EXPORTDATAID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>EXPORTDATAID - Ид</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"EXPORTDATAID"})
    public void dsB2BExportDataDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BExportDataDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EXPORTDATAID - Ид</LI>
     * <LI>FINISHDATE - Дата по</LI>
     * <LI>STARTDATE - Дата с</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TEMPLATEID - Ссылка на объект учета «Шаблон выгрузки»</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EXPORTDATAID - Ид</LI>
     * <LI>FINISHDATE - Дата по</LI>
     * <LI>STARTDATE - Дата с</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TEMPLATEID - Ссылка на объект учета «Шаблон выгрузки»</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BExportDataBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BExportDataBrowseListByParam", "dsB2BExportDataBrowseListByParamCount", params);
        return result;
    }





}
