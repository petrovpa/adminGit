/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesPerson
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="LOSS_PERSON",idFieldName="LOSSPERSONID")
@BOName("LossesPerson")
public class LossesPersonFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>BIRTHPLACE - Место рождения</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCISSUEDATE - Дата выдачи паспорта</LI>
     * <LI>DOCISSUEPLACE - Место выдачи паспорта</LI>
     * <LI>DOCISSUERCODE - Код подразделения</LI>
     * <LI>DOCNUM - Серия номер паспорта</LI>
     * <LI>EMAIL - Адрес электронной почты</LI>
     * <LI>GENDER - пол 1 м 2 ж</LI>
     * <LI>LOSSPERSONID - ИД страхователя</LI>
     * <LI>INN - ИНН</LI>
     * <LI>ISBENEFICIAR - Выгодопреобретатель</LI>
     * <LI>ISINSURER - Страхователь</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>MOBILEPHONENUM - Номер мобильного телефона</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARTICIPANTID - ИД партиципанта</LI>
     * <LI>PHONENUM - Номер телефона</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>WORKPHONENUM - Номер рабочего телефона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>LOSSPERSONID - ИД страхователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesPersonCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesPersonInsert", params);
        result.put("LOSSPERSONID", params.get("LOSSPERSONID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>BIRTHPLACE - Место рождения</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCISSUEDATE - Дата выдачи паспорта</LI>
     * <LI>DOCISSUEPLACE - Место выдачи паспорта</LI>
     * <LI>DOCISSUERCODE - Код подразделения</LI>
     * <LI>DOCNUM - Серия номер паспорта</LI>
     * <LI>EMAIL - Адрес электронной почты</LI>
     * <LI>GENDER - пол 1 м 2 ж</LI>
     * <LI>LOSSPERSONID - ИД страхователя</LI>
     * <LI>INN - ИНН</LI>
     * <LI>ISBENEFICIAR - Выгодопреобретатель</LI>
     * <LI>ISINSURER - Страхователь</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>MOBILEPHONENUM - Номер мобильного телефона</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARTICIPANTID - ИД партиципанта</LI>
     * <LI>PHONENUM - Номер телефона</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>WORKPHONENUM - Номер рабочего телефона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>LOSSPERSONID - ИД страхователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"LOSSPERSONID"})
    public Map<String,Object> dsLossesPersonInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesPersonInsert", params);
        result.put("LOSSPERSONID", params.get("LOSSPERSONID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>BIRTHPLACE - Место рождения</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCISSUEDATE - Дата выдачи паспорта</LI>
     * <LI>DOCISSUEPLACE - Место выдачи паспорта</LI>
     * <LI>DOCISSUERCODE - Код подразделения</LI>
     * <LI>DOCNUM - Серия номер паспорта</LI>
     * <LI>EMAIL - Адрес электронной почты</LI>
     * <LI>GENDER - пол 1 м 2 ж</LI>
     * <LI>LOSSPERSONID - ИД страхователя</LI>
     * <LI>INN - ИНН</LI>
     * <LI>ISBENEFICIAR - Выгодопреобретатель</LI>
     * <LI>ISINSURER - Страхователь</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>MOBILEPHONENUM - Номер мобильного телефона</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARTICIPANTID - ИД партиципанта</LI>
     * <LI>PHONENUM - Номер телефона</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>WORKPHONENUM - Номер рабочего телефона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>LOSSPERSONID - ИД страхователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"LOSSPERSONID"})
    public Map<String,Object> dsLossesPersonUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesPersonUpdate", params);
        result.put("LOSSPERSONID", params.get("LOSSPERSONID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>BIRTHPLACE - Место рождения</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCISSUEDATE - Дата выдачи паспорта</LI>
     * <LI>DOCISSUEPLACE - Место выдачи паспорта</LI>
     * <LI>DOCISSUERCODE - Код подразделения</LI>
     * <LI>DOCNUM - Серия номер паспорта</LI>
     * <LI>EMAIL - Адрес электронной почты</LI>
     * <LI>GENDER - пол 1 м 2 ж</LI>
     * <LI>LOSSPERSONID - ИД страхователя</LI>
     * <LI>INN - ИНН</LI>
     * <LI>ISBENEFICIAR - Выгодопреобретатель</LI>
     * <LI>ISINSURER - Страхователь</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>MOBILEPHONENUM - Номер мобильного телефона</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARTICIPANTID - ИД партиципанта</LI>
     * <LI>PHONENUM - Номер телефона</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>WORKPHONENUM - Номер рабочего телефона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>LOSSPERSONID - ИД страхователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"LOSSPERSONID"})
    public Map<String,Object> dsLossesPersonModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesPersonUpdate", params);
        result.put("LOSSPERSONID", params.get("LOSSPERSONID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>LOSSPERSONID - ИД страхователя</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"LOSSPERSONID"})
    public void dsLossesPersonDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesPersonDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>BIRTHPLACE - Место рождения</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCISSUEDATE - Дата выдачи паспорта</LI>
     * <LI>DOCISSUEPLACE - Место выдачи паспорта</LI>
     * <LI>DOCISSUERCODE - Код подразделения</LI>
     * <LI>DOCNUM - Серия номер паспорта</LI>
     * <LI>EMAIL - Адрес электронной почты</LI>
     * <LI>GENDER - пол 1 м 2 ж</LI>
     * <LI>LOSSPERSONID - ИД страхователя</LI>
     * <LI>INN - ИНН</LI>
     * <LI>ISBENEFICIAR - Выгодопреобретатель</LI>
     * <LI>ISINSURER - Страхователь</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>MOBILEPHONENUM - Номер мобильного телефона</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARTICIPANTID - ИД партиципанта</LI>
     * <LI>PHONENUM - Номер телефона</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>WORKPHONENUM - Номер рабочего телефона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>BIRTHPLACE - Место рождения</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCISSUEDATE - Дата выдачи паспорта</LI>
     * <LI>DOCISSUEPLACE - Место выдачи паспорта</LI>
     * <LI>DOCISSUERCODE - Код подразделения</LI>
     * <LI>DOCNUM - Серия номер паспорта</LI>
     * <LI>EMAIL - Адрес электронной почты</LI>
     * <LI>GENDER - пол 1 м 2 ж</LI>
     * <LI>LOSSPERSONID - ИД страхователя</LI>
     * <LI>INN - ИНН</LI>
     * <LI>ISBENEFICIAR - Выгодопреобретатель</LI>
     * <LI>ISINSURER - Страхователь</LI>
     * <LI>MIDDLENAME - Отчество</LI>
     * <LI>MOBILEPHONENUM - Номер мобильного телефона</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARTICIPANTID - ИД партиципанта</LI>
     * <LI>PHONENUM - Номер телефона</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>WORKPHONENUM - Номер рабочего телефона</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesPersonBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesPersonBrowseListByParam", "dsLossesPersonBrowseListByParamCount", params);
        return result;
    }





}
