/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesBinfile
 *
 * @author reson
 */
@IdGen(entityName="LOSS_BINFILE",idFieldName="BINFILEID")
@BOName("LossesBinfile")
public class LossesBinfileFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания файла</LI>
     * <LI>CREATEUSERID - ИД пользователя добавившего файл</LI>
     * <LI>FILENAME - Имя файла</LI>
     * <LI>FILEPATH - Путь до файла</LI>
     * <LI>FILESIZE - Размер файла</LI>
     * <LI>FILETYPEID - Тип файла</LI>
     * <LI>FILETYPENAME - Наименование типа</LI>
     * <LI>BINFILEID - ИД файла</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJTABLENAME - Наименование таблицы объекта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINFILEID - ИД файла</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesBinfileCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesBinfileInsert", params);
        result.put("BINFILEID", params.get("BINFILEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания файла</LI>
     * <LI>CREATEUSERID - ИД пользователя добавившего файл</LI>
     * <LI>FILENAME - Имя файла</LI>
     * <LI>FILEPATH - Путь до файла</LI>
     * <LI>FILESIZE - Размер файла</LI>
     * <LI>FILETYPEID - Тип файла</LI>
     * <LI>FILETYPENAME - Наименование типа</LI>
     * <LI>BINFILEID - ИД файла</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJTABLENAME - Наименование таблицы объекта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINFILEID - ИД файла</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINFILEID"})
    public Map<String,Object> dsLossesBinfileInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesBinfileInsert", params);
        result.put("BINFILEID", params.get("BINFILEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания файла</LI>
     * <LI>CREATEUSERID - ИД пользователя добавившего файл</LI>
     * <LI>FILENAME - Имя файла</LI>
     * <LI>FILEPATH - Путь до файла</LI>
     * <LI>FILESIZE - Размер файла</LI>
     * <LI>FILETYPEID - Тип файла</LI>
     * <LI>FILETYPENAME - Наименование типа</LI>
     * <LI>BINFILEID - ИД файла</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJTABLENAME - Наименование таблицы объекта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINFILEID - ИД файла</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINFILEID"})
    public Map<String,Object> dsLossesBinfileUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesBinfileUpdate", params);
        result.put("BINFILEID", params.get("BINFILEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания файла</LI>
     * <LI>CREATEUSERID - ИД пользователя добавившего файл</LI>
     * <LI>FILENAME - Имя файла</LI>
     * <LI>FILEPATH - Путь до файла</LI>
     * <LI>FILESIZE - Размер файла</LI>
     * <LI>FILETYPEID - Тип файла</LI>
     * <LI>FILETYPENAME - Наименование типа</LI>
     * <LI>BINFILEID - ИД файла</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJTABLENAME - Наименование таблицы объекта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINFILEID - ИД файла</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINFILEID"})
    public Map<String,Object> dsLossesBinfileModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesBinfileUpdate", params);
        result.put("BINFILEID", params.get("BINFILEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>BINFILEID - ИД файла</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINFILEID"})
    public void dsLossesBinfileDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesBinfileDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания файла</LI>
     * <LI>CREATEUSERID - ИД пользователя добавившего файл</LI>
     * <LI>FILENAME - Имя файла</LI>
     * <LI>FILEPATH - Путь до файла</LI>
     * <LI>FILESIZE - Размер файла</LI>
     * <LI>FILETYPEID - Тип файла</LI>
     * <LI>FILETYPENAME - Наименование типа</LI>
     * <LI>BINFILEID - ИД файла</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJTABLENAME - Наименование таблицы объекта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания файла</LI>
     * <LI>CREATEUSERID - ИД пользователя добавившего файл</LI>
     * <LI>FILENAME - Имя файла</LI>
     * <LI>FILEPATH - Путь до файла</LI>
     * <LI>FILESIZE - Размер файла</LI>
     * <LI>FILETYPEID - Тип файла</LI>
     * <LI>FILETYPENAME - Наименование типа</LI>
     * <LI>BINFILEID - ИД файла</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJTABLENAME - Наименование таблицы объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesBinfileBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesBinfileBrowseListByParam", "dsLossesBinfileBrowseListByParamCount", params);
        return result;
    }





}
