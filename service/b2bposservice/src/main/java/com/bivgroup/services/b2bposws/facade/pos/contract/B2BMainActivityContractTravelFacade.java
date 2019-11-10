/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.aspect.impl.version.NodeVersion;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BMainActivityContractTravel
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@Discriminator(3)
@BinaryFile(objTableName = "B2B_MAINACTCONTR", objTablePKFieldName = "MAINACTCONTRID")
@NodeVersion(nodeTableName="B2B_MAINACTCONTRNODE",nodeTableIdFieldName="MAINACTCONTRNODEID",versionNumberParamName="VERNUMBER",nodeLastVersionNumberFieldName="LASTVERNUMBER",nodeRVersionFieldName="RVERSION")
@IdGen(entityName="B2B_MAINACTCONTR",idFieldName="MAINACTCONTRID")
@BOName("B2BMainActivityContractTravel")
public class B2BMainActivityContractTravelFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRNUMBER - Номер договора</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>DISCRIMINATOR - Тип сущности (1 - Договор по основной деятельности, 2 - Агентский договор, 3 - Договор с ассистанс ВЗР)</LI>
     * <LI>DOCUMENTDATE - Дата оформления договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>MAINACTCONTRID - ИД записи</LI>
     * <LI>ORGSTRUCTID - Лицо</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MAINACTCONTRID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MAINACTCONTRNODEID"})
    public Map<String,Object> dsB2BMainActivityContractTravelCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractTravelInsert", params);
        result.put("MAINACTCONTRID", params.get("MAINACTCONTRID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRNUMBER - Номер договора</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>DISCRIMINATOR - Тип сущности (1 - Договор по основной деятельности, 2 - Агентский договор, 3 - Договор с ассистанс ВЗР)</LI>
     * <LI>DOCUMENTDATE - Дата оформления договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>MAINACTCONTRID - ИД записи</LI>
     * <LI>ORGSTRUCTID - Лицо</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MAINACTCONTRID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MAINACTCONTRNODEID", "MAINACTCONTRID"})
    public Map<String,Object> dsB2BMainActivityContractTravelInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractTravelInsert", params);
        result.put("MAINACTCONTRID", params.get("MAINACTCONTRID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRNUMBER - Номер договора</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>DISCRIMINATOR - Тип сущности (1 - Договор по основной деятельности, 2 - Агентский договор, 3 - Договор с ассистанс ВЗР)</LI>
     * <LI>DOCUMENTDATE - Дата оформления договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>MAINACTCONTRID - ИД записи</LI>
     * <LI>ORGSTRUCTID - Лицо</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MAINACTCONTRID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MAINACTCONTRID"})
    public Map<String,Object> dsB2BMainActivityContractTravelUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractTravelUpdate", params);
        result.put("MAINACTCONTRID", params.get("MAINACTCONTRID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRNUMBER - Номер договора</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>DISCRIMINATOR - Тип сущности (1 - Договор по основной деятельности, 2 - Агентский договор, 3 - Договор с ассистанс ВЗР)</LI>
     * <LI>DOCUMENTDATE - Дата оформления договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>MAINACTCONTRID - ИД записи</LI>
     * <LI>ORGSTRUCTID - Лицо</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MAINACTCONTRID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MAINACTCONTRID"})
    public Map<String,Object> dsB2BMainActivityContractTravelModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractTravelUpdate", params);
        result.put("MAINACTCONTRID", params.get("MAINACTCONTRID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MAINACTCONTRID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MAINACTCONTRID"})
    public void dsB2BMainActivityContractTravelDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMainActivityContractTravelDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRNUMBER - Номер договора</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>DISCRIMINATOR - Тип сущности (1 - Договор по основной деятельности, 2 - Агентский договор, 3 - Договор с ассистанс ВЗР)</LI>
     * <LI>DOCUMENTDATE - Дата оформления договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>MAINACTCONTRID - ИД записи</LI>
     * <LI>ORGSTRUCTID - Лицо</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRNUMBER - Номер договора</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>DISCRIMINATOR - Тип сущности (1 - Договор по основной деятельности, 2 - Агентский договор, 3 - Договор с ассистанс ВЗР)</LI>
     * <LI>DOCUMENTDATE - Дата оформления договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>MAINACTCONTRID - ИД записи</LI>
     * <LI>ORGSTRUCTID - Лицо</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractTravelBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMainActivityContractTravelBrowseListByParam", "dsB2BMainActivityContractTravelBrowseListByParamCount", params);
        return result;
    }





}
