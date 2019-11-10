/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности InsParticipantBinFile
 *
 * @author reson
 */
@BinaryFile(objTableName = "INS_PARTICIPANTBINFILE", objTablePKFieldName = "PARTICIPANTBINFILEID")
@IdGen(entityName="INS_PARTICIPANTBINFILE",idFieldName="PARTICIPANTBINFILEID")
@BOName("InsParticipantBinFile")
public class InsParticipantBinFileFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>PARTICIPANTBINFILEID - null</LI>
     * <LI>PARTICIPANTID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PARTICIPANTBINFILEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsInsParticipantBinFileCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsParticipantBinFileInsert", params);
        result.put("PARTICIPANTBINFILEID", params.get("PARTICIPANTBINFILEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>PARTICIPANTBINFILEID - null</LI>
     * <LI>PARTICIPANTID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PARTICIPANTBINFILEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PARTICIPANTBINFILEID"})
    public Map<String,Object> dsInsParticipantBinFileInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsParticipantBinFileInsert", params);
        result.put("PARTICIPANTBINFILEID", params.get("PARTICIPANTBINFILEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PARTICIPANTBINFILEID - null</LI>
     * <LI>PARTICIPANTID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PARTICIPANTBINFILEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PARTICIPANTBINFILEID"})
    public Map<String,Object> dsInsParticipantBinFileUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsParticipantBinFileUpdate", params);
        result.put("PARTICIPANTBINFILEID", params.get("PARTICIPANTBINFILEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PARTICIPANTBINFILEID - null</LI>
     * <LI>PARTICIPANTID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PARTICIPANTBINFILEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PARTICIPANTBINFILEID"})
    public Map<String,Object> dsInsParticipantBinFileModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsParticipantBinFileUpdate", params);
        result.put("PARTICIPANTBINFILEID", params.get("PARTICIPANTBINFILEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PARTICIPANTBINFILEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PARTICIPANTBINFILEID"})
    public void dsInsParticipantBinFileDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsInsParticipantBinFileDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>PARTICIPANTBINFILEID - null</LI>
     * <LI>PARTICIPANTID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PARTICIPANTBINFILEID - null</LI>
     * <LI>PARTICIPANTID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsInsParticipantBinFileBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsInsParticipantBinFileBrowseListByParam", "dsInsParticipantBinFileBrowseListByParamCount", params);
        return result;
    }





}
