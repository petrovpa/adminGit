/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BMember
 *
 * @author reson
 */
@IdGen(entityName="B2B_MEMBER",idFieldName="MEMBERID")
@BOName("B2BMember")
public class B2BMemberFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>FIO - ФИО</LI>
     * <LI>FIO_ENG - ФИО англ</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>MEMBERID - ИД</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NAME_ENG - Имя англ</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо в CRM</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>SURNAME_ENG - Фамилия англ</LI>
     * <LI>TYPESYSNAME - Тип лица (Застрахованный)</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MEMBERID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String,Object> dsB2BMemberCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMemberInsert", params);
        result.put("MEMBERID", params.get("MEMBERID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>FIO - ФИО</LI>
     * <LI>FIO_ENG - ФИО англ</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>MEMBERID - ИД</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NAME_ENG - Имя англ</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо в CRM</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>SURNAME_ENG - Фамилия англ</LI>
     * <LI>TYPESYSNAME - Тип лица (Застрахованный)</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MEMBERID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID", "MEMBERID"})
    public Map<String,Object> dsB2BMemberInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMemberInsert", params);
        result.put("MEMBERID", params.get("MEMBERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>FIO - ФИО</LI>
     * <LI>FIO_ENG - ФИО англ</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>MEMBERID - ИД</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NAME_ENG - Имя англ</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо в CRM</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>SURNAME_ENG - Фамилия англ</LI>
     * <LI>TYPESYSNAME - Тип лица (Застрахованный)</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MEMBERID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MEMBERID"})
    public Map<String,Object> dsB2BMemberUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMemberUpdate", params);
        result.put("MEMBERID", params.get("MEMBERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>FIO - ФИО</LI>
     * <LI>FIO_ENG - ФИО англ</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>MEMBERID - ИД</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NAME_ENG - Имя англ</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо в CRM</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>SURNAME_ENG - Фамилия англ</LI>
     * <LI>TYPESYSNAME - Тип лица (Застрахованный)</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MEMBERID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MEMBERID"})
    public Map<String,Object> dsB2BMemberModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMemberUpdate", params);
        result.put("MEMBERID", params.get("MEMBERID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MEMBERID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MEMBERID"})
    public void dsB2BMemberDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMemberDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>FIO - ФИО</LI>
     * <LI>FIO_ENG - ФИО англ</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>MEMBERID - ИД</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NAME_ENG - Имя англ</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо в CRM</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>SURNAME_ENG - Фамилия англ</LI>
     * <LI>TYPESYSNAME - Тип лица (Застрахованный)</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>DOUBLEFIELD00 - Дробное поле</LI>
     * <LI>DOUBLEFIELD01 - Дробное поле</LI>
     * <LI>DOUBLEFIELD02 - Дробное поле</LI>
     * <LI>DOUBLEFIELD03 - Дробное поле</LI>
     * <LI>DOUBLEFIELD04 - Дробное поле</LI>
     * <LI>DOUBLEFIELD05 - Дробное поле</LI>
     * <LI>FIO - ФИО</LI>
     * <LI>FIO_ENG - ФИО англ</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника</LI>
     * <LI>LONGFIELD00 - Целое поле</LI>
     * <LI>LONGFIELD01 - Целое поле</LI>
     * <LI>LONGFIELD02 - Целое поле</LI>
     * <LI>LONGFIELD03 - Целое поле</LI>
     * <LI>LONGFIELD04 - Целое поле</LI>
     * <LI>LONGFIELD05 - Целое поле</LI>
     * <LI>MEMBERID - ИД</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NAME_ENG - Имя англ</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо в CRM</LI>
     * <LI>STRINGFIELD00 - Строковое поле</LI>
     * <LI>STRINGFIELD01 - Строковое поле</LI>
     * <LI>STRINGFIELD02 - Строковое поле</LI>
     * <LI>STRINGFIELD03 - Строковое поле</LI>
     * <LI>STRINGFIELD04 - Строковое поле</LI>
     * <LI>STRINGFIELD05 - Строковое поле</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>SURNAME_ENG - Фамилия англ</LI>
     * <LI>TYPESYSNAME - Тип лица (Застрахованный)</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMemberBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMemberBrowseListByParam", "dsB2BMemberBrowseListByParamCount", params);
        return result;
    }





}
