/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.sbol;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author ilich
 */
@BOName("B2BContractSBOLCustom")
public class B2BContractSBOLCustomFacade extends B2BBaseFacade {

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    private static final String PRODUCT_CODE_SBOL_CIB = "99001";
    private static final String PRODUCT_CODE_SBOL_HIB = "99002";
    private static final String PRODUCT_CODE_SBOL_VZR = "99003";
    private static final String PRODUCT_CODE_SBOL_MORTGAGE = "99004";
    private static final String PRODUCT_CODE_SBOL_ANTIMITE_ROS = "00008";

    private static final String RISKSUMSHBDATAVERID_PARAMNAME = "RISKSUMSHBDATAVERID";

    // правило преобразования кодов программ СБОЛ в идентификаторы конфигураций продуктов
    private static final String prodConfIDConvertRules = "99001 > 3000; 99002 > 3500; 99003 > 2050; 99004 > 4000; 00008 > 13000";

    public static final double XML_MORTGAGE_PREM_MULT = 0.00225;

    private static final String contractRootKeys = "Contract";
    private static final String contractRootKeysWithDot = contractRootKeys + ".";

    // флаг включения/отключения блоков отладочных опреаций, которые могут изменять и/или подменять данные, в том числе с целью расширенного протоколирования
    // (рекомендуется отключать по завершению отладочных проверок)
    private static final boolean isDebugCodeActive = false;
    private boolean isError = false;

    public static final String[][] miteRosKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="Защита от клеща (Ростелеком)">
        {"DISABLE_VALIDATION", "", "1"}, // отключение валидации данных для СБОЛ продукта
        {"INSREGIONCODE", "InsRegion"},
        {"INSURERMAP.PARTICIPANTTYPE", "", "1"},
        {"INSURERMAP.ISBUSINESSMAN", "", "0"},
        {"INSURERMAP.ISCLIENT", "", "1"},
        {"INSURERMAP.LASTNAME", "Insurer.Surname"},
        {"INSURERMAP.FIRSTNAME", "Insurer.Name"},
        {"INSURERMAP.MIDDLENAME", "Insurer.Patronymic"},
        {"INSURERMAP.BIRTHDATE", "Insurer.DateOfBirth"},
        {"INSURERMAP.CITIZENSHIP", "Insurer.Contry", "0", "643 > 0; 000 > 1000"}, // преобразование (643/000 -> 0/1000; 0 - гражданин РФ, 1000 - иностранный гражданин), (согласно письму с уточнениями от 08.09.2015)
        {"INSURERMAP.GENDER", "Insurer.Sex", "0", "male > 0; female > 1"}, // преобразование (male/female -> 0/1)        

        {"MEMBERLIST.0.NAME", "Member.Name"},
        {"MEMBERLIST.0.SURNAME", "Member.Surname"},
        {"MEMBERLIST.0.MIDDLENAME", "Member.Patronymic"},
        {"MEMBERLIST.0.BIRTHDATE", "Member.DateOfBirth"}, //</editor-fold>
    };

    public static final String[][] cibKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="Защита банковской карты Онлайн">
        // Защита банковской карты Онлайн - структура тип/объект/риск
        // {"INSOBJGROUPLIST.00000", "PRODCONF.PRODVER.PRODSTRUCTS.00000"}, // целиком копировать этот узел нельзя - будут потеряны уже добавленные значения вида INSOBJGROUPLIST.00000.*
        {"INSOBJGROUPLIST.00000.SYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.00000.SYSNAME"},
        {"INSOBJGROUPLIST.00000.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.00000.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.HBDATAVERID"}, //{"INSOBJGROUPLIST.00000.OBJLIST.000000014.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.000000014"},
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124", "PRODCONF.PRODVER.PRODSTRUCTS.000000124"}, // Защита банковской карты Онлайн - свойства объекта (суммы не передаются, всегда вычисляются по программе страхования - заполнение по дереву структуры перенесено в contractDuplicatedKeys)
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.INSAMVALUE", "insAmVal"},
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.PREMVALUE", "insPremVal"},
    // Защита банковской карты Онлайн - свойства риска (суммы не передаются, всегда вычисляются по программе страхования - заполнение по дереву структуры перенесено в contractDuplicatedKeys)
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.INSAMVALUE", "insAmVal"},
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.PREMVALUE", "insPremVal"}
    //</editor-fold>
    };

    public static final String[][] mortgageKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России»">
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - сумма долга по ипотеке, она же страховая сумма
        {"INSAMVALUE", "CreditContract.Debt"},
        {"DateBeginTravel", "DateBeginTravel"},
        
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - структура тип/объект/риск
        // {"INSOBJGROUPLIST.00000", "PRODCONF.PRODVER.PRODSTRUCTS.00000"}, // целиком копировать этот узел нельзя - будут потеряны уже добавленные значения вида INSOBJGROUPLIST.00000.*
        {"INSOBJGROUPLIST.00000.SYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.00000.SYSNAME"},
        {"INSOBJGROUPLIST.00000.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.00000.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.HBDATAVERID"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000001.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.000000001"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125", "PRODCONF.PRODVER.PRODSTRUCTS.000000125"},
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - свойства объекта
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.INSAMVALUE", ""},
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - свойства риска
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.INSAMVALUE", ""},
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - атрибуты договора ипотечного кредитования (показатель договора)
        {"CONTREXTMAP.buildYear", "", "1"},
        {"CONTREXTMAP.woodInCeilings", "", "1"},
        {"CONTREXTMAP.creditNumber", "CreditContract.Number"},
        {"CONTREXTMAP.creditStartDATE", "CreditContract.DateBegin"},
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - строковый адрес страхуемого имущества (показатель договора)
        {"CONTREXTMAP.propertyAddress", "", "todo: генерировать"}, // todo: генерировать
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - адрес страхуемого имущества (показатель типа объекта)
        {"INSOBJGROUPLIST.00000.PROPERTYSYSNAME", "Options", "", "Дом > house; Квартира > flat"}, // house - дом, flat - квартира
        {"INSOBJGROUPLIST.00000.ADDRESSTEXT1", "", "todo: генерировать"}, // todo: генерировать
        {"INSOBJGROUPLIST.00000.REGION", "Address.Region"},
        //{"INSOBJGROUPLIST.00000.REGIONKLADR", "Address."},
        {"INSOBJGROUPLIST.00000.CITY", "Address.City"},
        //{"INSOBJGROUPLIST.00000.CITYKLADR", "Address."},
        {"INSOBJGROUPLIST.00000.STREET", "Address.Street"},
        {"INSOBJGROUPLIST.00000.STREETKLADR", "Address.LocalityCode"},
        {"INSOBJGROUPLIST.00000.HOUSE", "Address.House"},
        {"INSOBJGROUPLIST.00000.HOUSING", "Address.Housing"},
        {"INSOBJGROUPLIST.00000.BUILDING", "Address.Building"},
        {"INSOBJGROUPLIST.00000.FLAT", "Address.Flat"},
        {"INSOBJGROUPLIST.00000.POSTALCODE", "Address.ZipCode"},
        //
        // Адрес регистрации страхователя для «Защита дома» и «Ипотека» формируем из адреса имущества (согласно письма клиента от 10.09.2015)
        {"INSURERMAP.addressList.RegisterAddress.REGION", "Address.Region"},
        //{"INSURERMAP.addressList.RegisterAddress.REGIONKLADR", "Insurer.Address."},
        {"INSURERMAP.addressList.RegisterAddress.CITY", "Address.City"},
        //{"INSURERMAP.addressList.RegisterAddress.CITYKLADR", "Insurer.Address."},
        {"INSURERMAP.addressList.RegisterAddress.STREET", "Address.Street"},
        {"INSURERMAP.addressList.RegisterAddress.STREETKLADR", "Address.LocalityCode"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSE", "Address.House"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSING", "Address.Housing"},
        {"INSURERMAP.addressList.RegisterAddress.BUILDING", "Address.Building"},
        {"INSURERMAP.addressList.RegisterAddress.FLAT", "Address.Flat"},
        {"INSURERMAP.addressList.RegisterAddress.POSTALCODE", "Address.ZipCode"}, //
    //</editor-fold>
    };

    public static final String[][] hibKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="Защита дома Онлайн">
        // Защита дома Онлайн - структура тип/объект/риск
        // {"INSOBJGROUPLIST.00000", "PRODCONF.PRODVER.PRODSTRUCTS.00000"}, // целиком копировать этот узел нельзя - будут потеряны уже добавленные значения вида INSOBJGROUPLIST.00000.*
        {"INSOBJGROUPLIST.00000.SYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.00000.SYSNAME"},
        {"INSOBJGROUPLIST.00000.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.00000.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.HBDATAVERID"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.000000002"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119", "PRODCONF.PRODVER.PRODSTRUCTS.100000119"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.000000004"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119", "PRODCONF.PRODVER.PRODSTRUCTS.000000119"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.000000012"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116", "PRODCONF.PRODVER.PRODSTRUCTS.000000116"},
        // Защита дома Онлайн - свойства объектов (суммы не передаются, всегда вычисляются по программе страхования - заполнение по дереву структуры перенесено в contractDuplicatedKeys)
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.PREMVALUE", "insPremVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.PREMVALUE", "insPremVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.PREMVALUE", "insPremVal"},
        // Защита дома Онлайн - свойства рисков (суммы не передаются, всегда вычисляются по программе страхования - заполнение по дереву структуры перенесено в contractDuplicatedKeys)
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.PREMVALUE", "insPremVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.PREMVALUE", "insPremVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.PREMVALUE", "insPremVal"},
        // Защита дома Онлайн - тип страхуемого имущества (показатель договора)
        {"CONTREXTMAP.insObject", "Options", "0", "Дом > 0; Квартира > 1"}, // 0 - дом, 1 - квартира; преобразование по сведениям из документа "Выгрузка договоров (карта, дом, взр).docx"
        // Защита дома Онлайн - строковый адрес страхуемого имущества (показатель договора)
        {"CONTREXTMAP.propertyAddress", "", "todo: генерировать"}, // todo: генерировать
        // Защита дома Онлайн - адрес страхуемого имущества (показатель типа объекта)
        {"INSOBJGROUPLIST.00000.PROPERTYSYSNAME", "Options", "", "Дом > house; Квартира > flat"}, // house - дом, flat - квартира
        {"INSOBJGROUPLIST.00000.ADDRESSTEXT1", "", "todo: генерировать"}, // todo: генерировать
        {"INSOBJGROUPLIST.00000.REGION", "Address.Region"},
        //{"INSOBJGROUPLIST.00000.REGIONKLADR", "Address."},
        {"INSOBJGROUPLIST.00000.CITY", "Address.City"},
        //{"INSOBJGROUPLIST.00000.CITYKLADR", "Address."},
        {"INSOBJGROUPLIST.00000.STREET", "Address.Street"},
        {"INSOBJGROUPLIST.00000.STREETKLADR", "Address.LocalityCode"},
        {"INSOBJGROUPLIST.00000.HOUSE", "Address.House"},
        {"INSOBJGROUPLIST.00000.HOUSING", "Address.Housing"},
        {"INSOBJGROUPLIST.00000.BUILDING", "Address.Building"},
        {"INSOBJGROUPLIST.00000.FLAT", "Address.Flat"},
        {"INSOBJGROUPLIST.00000.POSTALCODE", "Address.ZipCode"},
        //
        // Адрес регистрации страхователя для «Защита дома» и «Ипотека» формируем из адреса имущества (согласно письма клиента от 10.09.2015)
        {"INSURERMAP.addressList.RegisterAddress.REGION", "Address.Region"},
        //{"INSURERMAP.addressList.RegisterAddress.REGIONKLADR", "Insurer.Address."},
        {"INSURERMAP.addressList.RegisterAddress.CITY", "Address.City"},
        //{"INSURERMAP.addressList.RegisterAddress.CITYKLADR", "Insurer.Address."},
        {"INSURERMAP.addressList.RegisterAddress.STREET", "Address.Street"},
        {"INSURERMAP.addressList.RegisterAddress.STREETKLADR", "Address.LocalityCode"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSE", "Address.House"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSING", "Address.Housing"},
        {"INSURERMAP.addressList.RegisterAddress.BUILDING", "Address.Building"},
        {"INSURERMAP.addressList.RegisterAddress.FLAT", "Address.Flat"},
        {"INSURERMAP.addressList.RegisterAddress.POSTALCODE", "Address.ZipCode"}, //
    //</editor-fold>
    };

    public static final String[][] vzrKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="Страхование путешественников Онлайн">
        // Страхование путешественников Онлайн - структура (только тип)
        // {"INSOBJGROUPLIST.00000", "PRODCONF.PRODVER.PRODSTRUCTS.00000"}, // целиком копировать этот узел нельзя - будут потеряны уже добавленные значения вида INSOBJGROUPLIST.00000.*
        {"INSOBJGROUPLIST.00000.SYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.00000.SYSNAME"},
        {"INSOBJGROUPLIST.00000.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.00000.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.HBDATAVERID"},
        {"MEMBERLIST.0.NAME_ENG", "members.member.0.name"},
        {"MEMBERLIST.0.SURNAME_ENG", "members.member.0.surname"},
        {"MEMBERLIST.0.BIRTHDATE", "members.member.0.dateOfBirth"},
        {"MEMBERLIST.1.NAME_ENG", "members.member.1.name"},
        {"MEMBERLIST.1.SURNAME_ENG", "members.member.1.surname"},
        {"MEMBERLIST.1.BIRTHDATE", "members.member.1.dateOfBirth"},
        {"MEMBERLIST.2.NAME_ENG", "members.member.2.name"},
        {"MEMBERLIST.2.SURNAME_ENG", "members.member.2.surname"},
        {"MEMBERLIST.2.BIRTHDATE", "members.member.2.dateOfBirth"},
        {"MEMBERLIST.3.NAME_ENG", "members.member.3.name"},
        {"MEMBERLIST.3.SURNAME_ENG", "members.member.3.surname"},
        {"MEMBERLIST.3.BIRTHDATE", "members.member.3.dateOfBirth"},
        {"MEMBERLIST.4.NAME_ENG", "members.member.4.name"},
        {"MEMBERLIST.4.SURNAME_ENG", "members.member.4.surname"},
        {"MEMBERLIST.4.BIRTHDATE", "members.member.4.dateOfBirth"},
        {"MEMBERLIST.5.NAME_ENG", "members.member.5.name"},
        {"MEMBERLIST.5.SURNAME_ENG", "members.member.5.surname"},
        {"MEMBERLIST.5.BIRTHDATE", "members.member.5.dateOfBirth"},
        {"MEMBERLIST.6.NAME_ENG", "members.member.6.name"},
        {"MEMBERLIST.6.SURNAME_ENG", "members.member.6.surname"},
        {"MEMBERLIST.6.BIRTHDATE", "members.member.6.dateOfBirth"},
        {"MEMBERLIST.7.NAME_ENG", "members.member.7.name"},
        {"MEMBERLIST.7.SURNAME_ENG", "members.member.7.surname"},
        {"MEMBERLIST.7.BIRTHDATE", "members.member.7.dateOfBirth"},
        {"MEMBERLIST.8.NAME_ENG", "members.member.8.name"},
        {"MEMBERLIST.8.SURNAME_ENG", "members.member.8.surname"},
        {"MEMBERLIST.8.BIRTHDATE", "members.member.8.dateOfBirth"},
        {"MEMBERLIST.9.NAME_ENG", "members.member.9.name"},
        {"MEMBERLIST.9.SURNAME_ENG", "members.member.9.surname"},
        {"MEMBERLIST.9.BIRTHDATE", "members.member.9.dateOfBirth"},
        {"CONREXTMAP.beginTravelDATE", "dateBeginTravel"},
        {"CONREXTMAP.signingDATE", "dateSigning"},
        {"STARTDATE", "dateBeginTravel"}, //</editor-fold>
    };

    public static String[][] commonKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="основные данные договора">
        {"DOCUMENTDATE", "DateSigning"},
        {"SIGNDATE", "DateSigning"},
        {"PROGCODE", "InsProgram"},
        {"PRODCONFID", "PRODCONF.PRODCONFID"},
        {"PRODVERID", "PRODCONF.PRODVER.PRODVERID"},
        {"CONTREXTMAP.HBDATAVERID", "PRODCONF.HBDATAVERID"}, // идентификатор для показателей договора

        // валюты зависят от продукта:
        // рубль для всех продуктов, но для страхования путешественников всегда евро (согласно письму с уточнениями от 08.09.2015)
        {"INSAMCURRENCYID", "InsProduct", "1", "99001 > 1; 99002 > 1; 99003 > 3; 99004 > 1; 00008 > 1"}, // 1 = идентификатор рубля в REF_CURRENCY, 3 = идентификатор евро в REF_CURRENCY
        {"PREMCURRENCYID", "InsProduct", "1", "99001 > 1; 99002 > 1; 99003 > 3; 99004 > 1; 00008 > 1"}, // 1 = идентификатор рубля в REF_CURRENCY, 3 = идентификатор евро в REF_CURRENCY
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="страхователь">
        {"INSURERMAP.PARTICIPANTTYPE", "", "1"},
        {"INSURERMAP.ISBUSINESSMAN", "", "0"},
        {"INSURERMAP.ISCLIENT", "", "1"},
        {"INSURERMAP.LASTNAME", "Insurer.Surname"},
        {"INSURERMAP.FIRSTNAME", "Insurer.Name"},
        {"INSURERMAP.MIDDLENAME", "Insurer.Patronymic"},
        {"INSURERMAP.BIRTHDATE", "Insurer.DateOfBirth"},
        {"INSURERMAP.CITIZENSHIP", "Insurer.Contry", "0", "643 > 0; 000 > 1000"}, // преобразование (643/000 -> 0/1000; 0 - гражданин РФ, 1000 - иностранный гражданин), (согласно письму с уточнениями от 08.09.2015)
        {"INSURERMAP.GENDER", "Insurer.Sex", "0", "male > 0; female > 1"}, // преобразование (male/female -> 0/1)        
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="адрес страхователя">
        {"INSURERMAP.addressList.RegisterAddress.REGION", "Insurer.Address.Region"},
        //{"INSURERMAP.addressList.RegisterAddress.REGIONKLADR", "Insurer.Address."},
        {"INSURERMAP.addressList.RegisterAddress.CITY", "Insurer.Address.City"},
        //{"INSURERMAP.addressList.RegisterAddress.CITYKLADR", "Insurer.Address."},
        {"INSURERMAP.addressList.RegisterAddress.STREET", "Insurer.Address.Street"},
        {"INSURERMAP.addressList.RegisterAddress.STREETKLADR", "Insurer.Address.LocalityCode"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSE", "Insurer.Address.House"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSING", "Insurer.Address.Housing"},
        {"INSURERMAP.addressList.RegisterAddress.BUILDING", "Insurer.Address.Building"},
        {"INSURERMAP.addressList.RegisterAddress.FLAT", "Insurer.Address.Flat"},
        {"INSURERMAP.addressList.RegisterAddress.POSTALCODE", "Insurer.Address.ZipCode"},
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="документ страхователя">
        {"INSURERMAP.documentList.PassportRF.DOCTYPESYSNAME", "Insurer.Document.Kind", "PassportRF", "21 > PassportRF; 10 > ForeignPassport"}, // (согласно письму с уточнениями от 08.09.2015)
        //{"INSURERMAP.documentList.PassportRF.CODE", ""}, // код типа документа, если есть системное имя (DOCTYPESYSNAME) - избыточен
        {"INSURERMAP.documentList.PassportRF.DOCSERIES", "Insurer.Document.Series"},
        {"INSURERMAP.documentList.PassportRF.DOCNUMBER", "Insurer.Document.Number"},
        {"INSURERMAP.documentList.PassportRF.ISSUEDATE", "Insurer.Document.DateOfIssue"},
        {"INSURERMAP.documentList.PassportRF.ISSUEDBY", "Insurer.Document.Authority"},
        {"INSURERMAP.documentList.PassportRF.ISSUERCODE", "Insurer.Document.Code"},
        {"INSURERMAP.documentList.ForeignPassport.DOCTYPESYSNAME", "Insurer.Document.Kind", "", "21 > PassportRF; 10 > ForeignPassport"}, // (согласно письму с уточнениями от 08.09.2015)
        //{"INSURERMAP.documentList.ForeignPassport.CODE", ""}, // код типа документа, если есть системное имя (DOCTYPESYSNAME) - избыточен
        {"INSURERMAP.documentList.ForeignPassport.DOCSERIES", "Insurer.Document.Series"},
        {"INSURERMAP.documentList.ForeignPassport.DOCNUMBER", "Insurer.Document.Number"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUEDATE", "Insurer.Document.DateOfIssue"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUEDBY", "Insurer.Document.Authority"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUERCODE", "Insurer.Document.Code"},
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="контактные данные страхователя">
        {"INSURERMAP.contactList.MobilePhone.VALUE", "Insurer.MobilePhone"}, // телефон
        {"INSURERMAP.contactList.PersonalEmail.VALUE", "Insurer.Email"}, // почта
        {"EMAIL", "Insurer.Email"}, // почта для отправки документов, используется для печати+отправки в dsSendDocumentsPackage и etc
    //</editor-fold>
    };

    // перечень развертываемых списков в карты вида 'имяСписка.системноеИмяЭлементаСписка' для преобразования сведений при сохранении договора в B2B
    // (имя списка - имя атрибута, хранящего системное имя элемента)
    public static final String[][] contractExpandedLists = {
        {"members.member", "SYNTHETICKEY"}, // застрахованные путешественники
        {"PRODCONF.PRODVER.PRODSTRUCTS", "SYSNAME"}, // элементы структуры объекта
        {"PRODCONF.PRODVER.PRODPROGS", "PROGCODE"}, // программы страхования
    };

    // список наследуемых и дублирующихся значений, используется для заполнения дубликатов полей при сохранении договора 
    // (ключ основного значения, ключ значения повторяющего основное)
    public static final String[][] contractDuplicatedKeys = { //<editor-fold defaultstate="collapsed" desc="Защита банковской карты Онлайн">
    // Защита банковской карты Онлайн
    // 1.1 и 1.1.1
    //{"INSAMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.INSAMVALUE"},
    //{"INSAMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.INSAMVALUE"},
    //{"PREMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.PREMVALUE"},
    //{"PREMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.PREMVALUE"},
    //{"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.STARTDATE"},
    //{"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.STARTDATE"},
    //{"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.FINISHDATE"},
    //{"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.FINISHDATE"},
    //{"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.DURATION"},
    //{"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.DURATION"},
    //{"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.INSAMCURRENCYID"},
    //{"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.INSAMCURRENCYID"},
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.INSAMCURRENCYID",
    //    "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.INSAMCURRENCYID",
    //    "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
    //{"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.PREMCURRENCYID"},
    //{"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.PREMCURRENCYID"}, //
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Защита дома Онлайн">
    // Защита дома Онлайн
    // 1.1 и 1.1.1
    //{"INSAMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.INSAMVALUE"},
    //{"INSAMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.INSAMVALUE"},
    //{"PREMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.PREMVALUE"},
    //{"PREMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.PREMVALUE"},
    //{"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.STARTDATE"},
    //{"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.STARTDATE"},
    //{"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.FINISHDATE"},
    //{"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.FINISHDATE"},
    //{"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.DURATION"},
    //{"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.DURATION"},
    //{"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.INSAMCURRENCYID"},
    //{"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.INSAMCURRENCYID"},
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.INSAMCURRENCYID",
    //    "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.INSAMCURRENCYID",
    //    "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
    //{"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.PREMCURRENCYID"},
    //{"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.PREMCURRENCYID"},
    // 1.2 и 1.2.1
    //{"INSAMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.INSAMVALUE"},
    //{"INSAMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.INSAMVALUE"},
    //{"PREMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.PREMVALUE"},
    //{"PREMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.PREMVALUE"},
    //{"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.STARTDATE"},
    //{"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.STARTDATE"},
    //{"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.FINISHDATE"},
    //{"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.FINISHDATE"},
    //{"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.DURATION"},
    //{"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.DURATION"},
    //{"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.INSAMCURRENCYID"},
    //{"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.INSAMCURRENCYID"},
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.INSAMCURRENCYID",
    //    "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.INSAMCURRENCYID",
    //    "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
    //{"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.PREMCURRENCYID"},
    //{"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.PREMCURRENCYID"},
    // 1.3 и 1.3.1
    //{"INSAMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.INSAMVALUE"},
    //{"INSAMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.INSAMVALUE"},
    //{"PREMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.PREMVALUE"},
    //{"PREMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.PREMVALUE"},
    //{"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.STARTDATE"},
    //{"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.STARTDATE"},
    //{"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.FINISHDATE"},
    //{"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.FINISHDATE"},
    //{"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.DURATION"},
    //{"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.DURATION"},
    //{"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.INSAMCURRENCYID"},
    //{"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.INSAMCURRENCYID"},
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.INSAMCURRENCYID",
    //    "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.INSAMCURRENCYID",
    //    "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
    //{"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.PREMCURRENCYID"},
    //{"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.PREMCURRENCYID"}, //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России»">
    // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России»
    // 1.1 и 1.1.1
    //{"INSAMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.INSAMVALUE"},
    //{"INSAMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.INSAMVALUE"},
    //{"PREMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.PREMVALUE"},
    //{"PREMVALUE", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.PREMVALUE"},
    //{"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.STARTDATE"},
    //{"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.STARTDATE"},
    //{"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.FINISHDATE"},
    //{"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.FINISHDATE"},
    //{"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.DURATION"},
    //{"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.DURATION"},
    //{"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.INSAMCURRENCYID"},
    //{"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.INSAMCURRENCYID"},
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.INSAMCURRENCYID",
    //    "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
    //{"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.INSAMCURRENCYID",
    //    "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
    //{"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.PREMCURRENCYID"},
    //{"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.PREMCURRENCYID"}, //
    //</editor-fold>
    };

    // перечень сворачиваемых в списки карт вида 'имяСписка_системноеИмяЭлементаСписка' для преобразования сведений при сохранении договора в B2B
    // (имя списка - имя атрибута, хранящего системное имя элемента)
    public static final String[][] contractCollapsedMaps = {
        {"PRODCONF.PRODVER.PRODSTRUCTS", "SYSNAME"}, // элементы структуры объекта
        {"PRODCONF.PRODVER.PRODPROGS", "SYSNAME"}, // программы страхования
        {"INSURERMAP.addressList", "ADDRESSTYPESYSNAME"}, // адреса
        {"INSURERMAP.partRegDocList", "REGDOCTYPESYSNAME"}, // регистрационные документы
        {"INSURERMAP.documentList", "DOCTYPESYSNAME"}, // личные документы
        {"INSURERMAP.contactList", "CONTACTTYPESYSNAME"}, // контакты
        // риски - Защита банковской карты Онлайн
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        // риски - Ипотека Онлайн
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        // риски - Защита дома Онлайн
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        // объекты - Защита банковской карты Онлайн, Защита дома Онлайн, Ипотека Онлайн, Страхование путешественников Онлайн
        //{"INSOBJGROUPLIST.00000.OBJLIST", "SYSNAME"}, //  
        // типы - Защита банковской карты Онлайн, Защита дома Онлайн, Ипотека Онлайн, Страхование путешественников Онлайн
        {"INSOBJGROUPLIST", "SYSNAME"}, //
        {"MEMBERLIST", "SYNTHETICKEY"}, // застрахованные путешественники
    };

    protected Object chainedGet(Map<String, Object> map, String[] keys) {
        if (keys.length == 0) {
            return null;
        }
        Object element = map;
        for (int i = 0; i < keys.length; i++) {
            if (element instanceof Map) {
                Object nextElement = ((Map) element).get(keys[i]);
                if (nextElement == null) {
                    return null;
                } else if (nextElement instanceof List) {
                    element = null;
                    if (((List) nextElement).size() > 0) {
                        element = ((List) nextElement).get(0);
                    }
                    if (element == null) {
                        return null;
                    }
                } else {
                    element = nextElement;
                }
            } else {
                // todo: управлять доп. протоколированием через константу
                //logger.debug("Промежуточный ключ '" + keys[i-1] + "' (в чепочке ключей '" + Arrays.toString(keys) + "') не указывает на карту, определить значение окончательного элемента невозможно.");
                return null;
            }
        }
        return element;
    }

    protected Object chainedGet(Map<String, Object> map, String keysChain) {
        if (keysChain.isEmpty()) {
            return null;
        }
        String[] keys = keysChain.split("\\.");
        return chainedGet(map, keys);
    }

    // аналог chainedGet, но 
    // 1 - с проверкой двух вариантов регистра первых символво всех ключей - как был передан и первые символы малые (*.SomeParentKeyName.SomeChildKeyName и *.someParentKeyName.someChildKeyName)
    // 2 - с проверкой двух вариантов регистра конечного ключа - как был передан и все заглавные (*.someKeyName и *.SOMEKEYNAME)
    protected Object chainedGetIgnoreCase(Map<String, Object> map, String keysChain) {
        if (keysChain.isEmpty()) {
            return null;
        }
        String[] keys = keysChain.split("\\.");
        Object result = chainedGet(map, keys);
        if (result == null) {
            String[] keysChainWithAllKeysFirstLetterLowerCase = getKeysChainWithAllKeysFirstLetterLowerCase(keys);
            result = chainedGet(map, keysChainWithAllKeysFirstLetterLowerCase);
        }
        if (result == null) {
            String[] keysChainWithLastKeyUpperCase = getKeysChainWithLastKeyUpperCase(keys);
            result = chainedGet(map, keysChainWithLastKeyUpperCase);
        }
        return result;
    }

    protected String[] getKeysChainWithLastKeyUpperCase(String[] keys) {
        String[] result = keys;
        int lastKeyIndex = result.length - 1;
        result[lastKeyIndex] = result[lastKeyIndex].toUpperCase();
        return result;
    }

    protected String[] getKeysChainWithAllKeysFirstLetterLowerCase(String[] keys) {
        String[] result = keys;
        for (int keyIndex = 0; keyIndex < result.length; keyIndex++) {
            String lastKeyFirstLetter = result[keyIndex].substring(0, 1);
            String lastKeyOtherLetters = result[keyIndex].substring(1);
            result[keyIndex] = lastKeyFirstLetter.toLowerCase() + lastKeyOtherLetters;
        }
        return result;
    }

    protected String getKeysChainWithLastKeyUpperCase(String keysChain) {
        int lastDotIndex = keysChain.lastIndexOf(".");
        String result;
        if (lastDotIndex < 0) {
            result = keysChain.toUpperCase();
        } else {
            String mainPart = keysChain.substring(0, lastDotIndex); // '*' без '.someKeyName'
            String upperPart = keysChain.substring(lastDotIndex).toUpperCase(); // '.SOMEKEYNAME'
            result = mainPart + upperPart; // '*.SOMEKEYNAME'
        }
        return result;
    }

    protected Object chainedPut(Map<String, Object> map, String keysChain, Object value) {
        String[] keys = keysChain.split("\\.");
        String[] getKeys;
        String putKey;
        getKeys = Arrays.copyOfRange(keys, 0, keys.length - 1);
        putKey = keys[keys.length - 1];
        Object updatedMap = chainedGet(map, getKeys);
        if ((updatedMap != null) && (updatedMap instanceof Map) && (((Map) updatedMap).get(putKey) == null)) {
            if (value instanceof Map) {
                Map<String, Object> puttedMap = new HashMap<String, Object>();
                puttedMap.putAll((Map) value);
                ((Map) updatedMap).put(putKey, puttedMap);
            } else {
                ((Map) updatedMap).put(putKey, value);
            }
            return value;
        }
        return null;
    }

    protected Object chainedCreativePut(Map<String, Object> map, String[] keys, Object value) {
        String[] getKeys = Arrays.copyOfRange(keys, 0, keys.length - 1);
        String putKey = keys[keys.length - 1];
        Object updatedMap = chainedCreativeGet(map, getKeys);
        if ((updatedMap != null) && (updatedMap instanceof Map) /*&& (((Map) updatedMap).get(putKey) == null)*/) {
            if (value instanceof Map) {
                Map<String, Object> puttedMap = new HashMap<String, Object>();
                puttedMap.putAll((Map) value);
                ((Map) updatedMap).put(putKey, puttedMap);
            } else {
                ((Map) updatedMap).put(putKey, value);
            }
            return value;
        }
        return null;
    }

    protected Object chainedCreativeGet(Map<String, Object> map, String[] keys) {
        Object element = map;
        for (int i = 0; i < keys.length; i++) {
            if (element instanceof Map) {
                Map elementAsMap = (Map) element;
                Object nextElement = elementAsMap.get(keys[i]);
                if (nextElement == null) {
                    Map<String, Object> createdMap = new HashMap<String, Object>();
                    elementAsMap.put(keys[i], createdMap);
                    element = createdMap;
                    //return null;
                } else if (nextElement instanceof List) {
                    element = null;
                    if (((List) nextElement).size() > 0) {
                        element = ((List) nextElement).get(0);
                    }
                    if (element == null) {
                        return null;
                    }
                } else {
                    element = nextElement;
                }
            } else {
                // todo: управлять доп. протоколированием через константу
                //logger.debug("Промежуточный ключ '" + keys[i - 1] + "' (в чепочке ключей '" + Arrays.toString(keys) + "') не указывает на карту, установить значение окончательного элемента невозможно.");
                return null;
            }
        }
        return element;
    }

    protected Object chainedCreativePut(Map<String, Object> map, String keysChain, Object value) {
        String[] keys = keysChain.split("\\.");
        return chainedCreativePut(map, keys, value);
    }

    protected Object chainedCreativePut(Map<String, Object> map, String keysChain, Object value, boolean isCreative) {
        if (isCreative) {
            String[] keys = keysChain.split("\\.");
            return chainedCreativePut(map, keys, value);
        } else {
            return chainedPut(map, keysChain, value);
        }
    }

    protected Object convertValue(Object convertedValueObj, String convertRulesStr) {
        if (convertedValueObj == null) {
            return null;
        }
        int fromIndex = 0;
        int toIndex = 1;
        String convertedValueStr = convertedValueObj.toString();
        String[] convertRules = convertRulesStr.split("; ");
        String[][] convertTable = new String[convertRules.length][2];
        for (int i = 0; i < convertRules.length; i++) {
            convertTable[i] = convertRules[i].split(" > ");
        }

        for (int j = 0; j < convertTable.length; j++) {
            if (convertedValueStr.equalsIgnoreCase(convertTable[j][fromIndex])) {
                return convertTable[j][toIndex];
            }
        }

        return convertedValueObj;
    }

    // получение из списка последнего элемента у которого в attrName храниться значение attrValue
    protected Object getLastElementByAtrrValue(ArrayList<Map<String, Object>> list, String attrName, String attrValue) {
        for (int i = list.size() - 1; i >= 0; i--) {
            Map element = list.get(i);
            Object elementAttrValue = element.get(attrName);
            if ((elementAttrValue != null) && (attrValue.equalsIgnoreCase(elementAttrValue.toString()))) {
                return element;
            }
        }
        try {
            int attrValueInt = Integer.parseInt(attrValue);
            return list.get(attrValueInt);
        } catch (NumberFormatException e) {
        }
        return null;
    }

    // у полученной карты "разворачивает" указанный список в карты вида 'имяСписка_системноеИмяЭлементаСписка'
    protected void expandListToMapBySysName(Map<String, Object> source, String listChainKeys, String sysNameKey) {
        //Object listObj = source.get(listKey);
        String[] keys = listChainKeys.split("\\.");
        String[] parentMapKeys = Arrays.copyOfRange(keys, 0, keys.length - 1);
        String listKey = keys[keys.length - 1];

        Object parentMapObj = chainedGet(source, parentMapKeys);
        Object listObj = null;
        Map<String, Object> parentMap = null;
        if (parentMapObj != null) {
            parentMap = (Map<String, Object>) parentMapObj;
            listObj = parentMap.get(listKey);
        }

        if (listObj == null) {
            return;
        }

        ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) listObj;
        ArrayList<String> sysNames = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            Object newSysName = list.get(i).get(sysNameKey);
            if (newSysName == null) {
                newSysName = i;
            }
            if (newSysName != null) {
                for (int j = 0; j < sysNames.size(); j++) {
                    if (sysNames.get(j).equalsIgnoreCase(newSysName.toString())) {
                        newSysName = null;
                        break;
                    }
                }
                if (newSysName != null) {
                    sysNames.add(newSysName.toString());
                }
            }
        }

        Map<String, Object> expandMap = null;
        if (!sysNames.isEmpty()) {
            expandMap = new HashMap<String, Object>();
            parentMap.put(listKey, expandMap);
        }

        for (int k = 0; k < sysNames.size(); k++) {
            String expandedSysName = sysNames.get(k);
            Object expandedElement = getLastElementByAtrrValue(list, sysNameKey, expandedSysName);
            expandMap.put(expandedSysName, expandedElement);
        }

    }

    // у полученной карты "cворачивает" указанную карту вида 'имяСписка.системноеИмяЭлементаСписка' в элемент списка
    protected void collapseMapToListBySysName(Map<String, Object> source, String mapKeysChain, String sysNameKey) {

        String[] oldMapKeys = mapKeysChain.split("\\.");
        Object oldMapObj = chainedGet(source, oldMapKeys);

        if (oldMapObj == null) {
            return;
        }

        Object newListObj = chainedCreativePut(source, oldMapKeys, new ArrayList<Map<String, Object>>());

        Map<String, Object> oldMap = ((Map<String, Object>) oldMapObj);
        ArrayList<Map<String, Object>> newList = (ArrayList<Map<String, Object>>) newListObj;
        for (Map.Entry entry : oldMap.entrySet()) {
            Map<String, Object> subMap = (Map<String, Object>) entry.getValue();
            String sysNameValue = entry.getKey().toString();
            String sysNameValueDirect = getStringParam(subMap.get(sysNameKey));
            if ((sysNameValueDirect.isEmpty()) || (sysNameValue.equalsIgnoreCase(sysNameValueDirect))) {
                subMap.put(sysNameKey, sysNameValue);
                newList.add(subMap);
            }
        }

    }

    // генерация дополнительных вычисляемых параметров, если не были переданы в явном виде для сохранения
    protected void genAdditionalSaveParams(Map<String, Object> result, Map<String, Object> contract, Map<String, Object> product, String xmlProductCode, Long requestId, String login, String password) throws Exception {

        // дата оформления
        Object docDateObj = contract.get("DOCUMENTDATE");
        Date docDate;
        if (docDateObj == null) {
            docDate = new Date();
            contract.put("DOCUMENTDATE", docDate);
        } else {
            docDate = (Date) parseAnyDate(docDateObj, Date.class, "DOCUMENTDATE");
        }

        // дата начала договора
        Object startDateObj = contract.get("STARTDATE");
        Date startDate; // = null;
        GregorianCalendar startDateGC = new GregorianCalendar();
        if (startDateObj == null) {
            if (xmlProductCode.equalsIgnoreCase(PRODUCT_CODE_SBOL_MORTGAGE)) {
                Date resStartDate = docDate;
                Object docOldDocFinishObj = contract.get("DateBeginTravel");
                Date docOldDocFinish;
                if (docOldDocFinishObj != null) {
                    docOldDocFinish = (Date) parseAnyDate(docOldDocFinishObj, Date.class, "DateBeginTravel");
                    if (docOldDocFinish.after(docDate)) {
                        resStartDate = docOldDocFinish;
                    }
                }
                startDateGC.setTime(resStartDate);
            } else {
                startDateGC.setTime(docDate); // "Дата начала договора должна быть «Дата оформления» + ...
            }
            int startDateDaysShift = 1; // ... + 1 день" (в обычных договорах, например, для ипотеки)
            if (xmlProductCode.equals(PRODUCT_CODE_SBOL_CIB) || xmlProductCode.equals(PRODUCT_CODE_SBOL_HIB)) {
                startDateDaysShift = 14;  // ... + 14 дней" (для карты и дома)
            }
            if (xmlProductCode.equalsIgnoreCase(PRODUCT_CODE_SBOL_ANTIMITE_ROS)) {
                startDateDaysShift = 7;  // ... + 7 дней" (для клеща)
            }
            startDateGC.add(Calendar.DATE, startDateDaysShift); // ... + X дней"
            startDate = startDateGC.getTime();
            contract.put("STARTDATE", startDate);
        } else {
            startDate = (Date) parseAnyDate(startDateObj, Date.class, "STARTDATE");
            if (startDate != null) {
                startDateGC.setTime(startDate);
            }
        }

        // дата окончания договора
        Object finishDateObj = contract.get("FINISHDATE");
        Date finishDate; // = null;
        GregorianCalendar finishDateGC = new GregorianCalendar();
        logger.debug("finishdate = " + finishDateObj);
        if (finishDateObj == null) {
            finishDateGC.setTime(startDate); // "соответственно дата окончания должна быть «Дата начала» + ...
            finishDateGC.add(Calendar.YEAR, 1); // ... + 1 страховой год ...
            int finishDateDaysShift = 0; // ... + 0 дней" (в обычных договорах, например, для ипотеки)
            if (xmlProductCode.equals(PRODUCT_CODE_SBOL_CIB) || xmlProductCode.equals(PRODUCT_CODE_SBOL_HIB)) {
                finishDateDaysShift = -1;  // ... - 1 день" (для карты и дома)
            }
            if (xmlProductCode.equals(PRODUCT_CODE_SBOL_MORTGAGE)) {
                //finishDateGC.add(Calendar.MONTH, 1); // ... + 1 страховой месяц ...
                finishDateGC.add(Calendar.DATE, -1); // ... + 1 страховой месяц ...
                logger.debug("mortgage = " + finishDateGC);
            }
            finishDateGC.add(Calendar.DATE, finishDateDaysShift); // ... - X дней"
            finishDate = finishDateGC.getTime();
            contract.put("FINISHDATE", finishDate);
        } else {
            logger.debug("finish date dont calc = " + finishDateObj);

            finishDate = (Date) parseAnyDate(finishDateObj, Date.class, "FINISHDATE");
            if (finishDate != null) {
                finishDateGC.setTime(finishDate);
            }
        }

        // срок действия договора
        Object durationObj = contract.get("DURATION");
        int duration;
        if (durationObj == null) {
            long startDateInMillis = startDateGC.getTimeInMillis();
            long finishDateInMillis = finishDateGC.getTimeInMillis();
            // в сутках (24*60*60*1000) милисекунд
            duration = (int) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000));
            // duration -= 1; // вычитать один день не требуется - уже учтено при отбросе дробной части выше
            contract.put("DURATION", duration);
        } else {
            duration = Integer.parseInt(durationObj.toString());
        }

        // если передано системное имя программы - переопределение идентификатора программы и её кода в параметрах котракта
        //Object programSysNameObj = contract.get("PROGCODE");\
        Object programCodeObj = contract.get("PROGCODE");
        String programCode = "";
        if ((programCodeObj != null) && (product != null)) {
            programCode = programCodeObj.toString();
            Object programID = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".PRODPROGID");
            if (programID == null) {
                returnErrorAndStopSaving(result, "Указана несуществующая программа страхования", requestId, login, password);
            }
            //contract.put("PRODPROGID", programIDsBySysName.get(programSysName));
            contract.put("PRODPROGID", programID);
            Object programB2BCode = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".PROGCODE");
            //chainedCreativePut(contract, "CONTREXTMAP.insuranceProgram", programB2BCodesBySysName.get(programSysName));
            chainedCreativePut(contract, "CONTREXTMAP.insuranceProgram", programB2BCode);
        }

        if (xmlProductCode.equals(PRODUCT_CODE_SBOL_HIB) || xmlProductCode.equals(PRODUCT_CODE_SBOL_MORTGAGE)) {
            Map<String, Object> address = (Map<String, Object>) chainedGetIgnoreCase(contract, "INSURERMAP.addressList.RegisterAddress");
            String addressSt = "";
            addressSt = addressSt + address.get("REGION").toString();
            /*if ((address.get("REGION") != null) && (!address.get("REGION").toString().isEmpty())) {
                addressSt = addressSt + address.get("REGION").toString() + ", ";
            }
            if ((address.get("CITY") != null) && (!address.get("CITY").toString().isEmpty())) {
                addressSt = addressSt + address.get("CITY").toString() + ", ";
            }
            if ((address.get("STREET") != null) && (!address.get("STREET").toString().isEmpty())) {
                addressSt = addressSt + address.get("STREET").toString() + ", ";
            }
            if ((address.get("HOUSE") != null) && (!address.get("HOUSE").toString().isEmpty())) {
                addressSt = addressSt + "д. " + address.get("HOUSE").toString() + ", ";
            }
            if ((address.get("BUILDING") != null) && (!address.get("BUILDING").toString().isEmpty())) {
                addressSt = addressSt + "корп. " + address.get("BUILDING").toString() + ", ";
            }
            if ((address.get("HOUSING") != null) && (!address.get("HOUSING").toString().isEmpty())) {
                addressSt = addressSt + "стр. " + address.get("HOUSING").toString() + ", ";
            }
            if ((address.get("FLAT") != null) && (!address.get("FLAT").toString().isEmpty())) {
                addressSt = addressSt + "кв. " + address.get("FLAT").toString() + ", ";
            }
            if (addressSt.length() >= 2) {
                if (addressSt.charAt(addressSt.length() - 2) == ',') {
                    addressSt = addressSt.substring(0, addressSt.length() - 2);
                }
            }*/
            chainedCreativePut(contract, "CONTREXTMAP.propertyAddress", addressSt);
            chainedCreativePut(contract, "INSOBJGROUPLIST.00000.ADDRESSTEXT1", addressSt);
        }
        // безусловное перевычисление сумм
        if (xmlProductCode.equals(PRODUCT_CODE_SBOL_HIB) || xmlProductCode.equals(PRODUCT_CODE_SBOL_CIB) || xmlProductCode.equals(PRODUCT_CODE_SBOL_VZR)) {
            if (!programCode.isEmpty()) {
                logger.debug("");
                logger.debug("Opredelenie strahovy`kh summy` i premii po programme strahovaniia...");
                Double insAmValue = getDoubleParam(chainedGetIgnoreCase(contract, "insAmValue"));
                logger.debug("Strahovaia summa, peredannaia v iavnom vide = " + insAmValue);
                Double premValue = getDoubleParam(chainedGetIgnoreCase(contract, "premValue"));
                logger.debug("Strahovaia premiia, peredannaia v iavnom vide = " + premValue);
                Object programInsAmValue = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".INSAMVALUE");
                if (programInsAmValue != null) {
                    contract.put("INSAMVALUE", programInsAmValue);
                    logger.debug("Strahovaia summa, opredelennaia po programme = " + programInsAmValue);
                } else {
                    logger.debug("Opredelit` po programme strahovuiu summu ne udalos`, budet ispol`zovana peredannaia v iavnom vide.");
                }
                Object programPremValue = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".PREMVALUE");
                if (programPremValue != null) {
                    contract.put("PREMVALUE", programPremValue);
                    logger.debug("Strahovaia premiia, opredelennaia po programme = " + programPremValue);
                } else {
                    logger.debug("Opredelit` po programme strahovuiu premiiu ne udalos`, budet ispol`zovana peredannaia v iavnom vide.");
                }
            } else {
                logger.debug("Ne udalos` opredelit` sistemnoe imia programmy` strahovaniia - spetcificheskie dlia konkretny`kh produktov pereraschety` summ vy`polneny` ne budut!");
            }

        } else if (xmlProductCode.equals(PRODUCT_CODE_SBOL_MORTGAGE)) {
            logger.debug("");
            logger.debug("Raschet strahovoi` premii...");
            // страховая сумма
            Double insAmValue = getDoubleParam(chainedGetIgnoreCase(contract, "insAmValue"));
            logger.debug("Strahovaia summa, peredannaia v iavnom vide = " + insAmValue);
            // страховая премия
            Double premValue = getDoubleParam(chainedGetIgnoreCase(contract, "premValue"));
            logger.debug("Strahovaia premiia, peredannaia v iavnom vide = " + premValue);
            // пересчет премии - без использования калькулятора, т. к. формула простая
            premValue = calcMortgageSBOLPremValue(insAmValue);
            logger.debug("Vy`chislennaia strahovaia premiia = " + premValue);
            contract.put("PREMVALUE", premValue);
        } else if (xmlProductCode.equals(PRODUCT_CODE_SBOL_ANTIMITE_ROS)) {
            if (!programCode.isEmpty()) {
                logger.debug("");
                logger.debug("Opredelenie strahovy`kh summy` i premii po programme strahovaniia...");
                Double insAmValue = getDoubleParam(chainedGetIgnoreCase(contract, "insAmValue"));
                logger.debug("Strahovaia summa, peredannaia v iavnom vide = " + insAmValue);
                Double premValue = getDoubleParam(chainedGetIgnoreCase(contract, "premValue"));
                logger.debug("Strahovaia premiia, peredannaia v iavnom vide = " + premValue);
                Object programInsAmValue = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".INSAMVALUE");
                if (programInsAmValue != null) {
                    contract.put("INSAMVALUE", programInsAmValue);
                    logger.debug("Strahovaia summa, opredelennaia po programme = " + programInsAmValue);
                } else {
                    logger.debug("Opredelit` po programme strahovuiu summu ne udalos`, budet ispol`zovana peredannaia v iavnom vide.");
                }
                Object programPremValue = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".PREMVALUE");
                if (programPremValue != null) {
                    contract.put("PREMVALUE", programPremValue);
                    logger.debug("Strahovaia premiia, opredelennaia po programme = " + programPremValue);
                } else {
                    logger.debug("Opredelit` po programme strahovuiu premiiu ne udalos`, budet ispol`zovana peredannaia v iavnom vide.");
                }
            } else {
                logger.debug("Ne udalos` opredelit` sistemnoe imia programmy` strahovaniia - spetcificheskie dlia konkretny`kh produktov pereraschety` summ vy`polneny` ne budut!");
            }
        }
        /*switch (xmlProductCode) {
            case PRODUCT_CODE_SBOL_HIB:
            case PRODUCT_CODE_SBOL_CIB:
            case PRODUCT_CODE_SBOL_VZR:
                if (!programCode.isEmpty()) {
                    logger.debug("");
                    logger.debug("Opredelenie strahovy`kh summy` i premii po programme strahovaniia...");
                    Double insAmValue = getDoubleParam(chainedGetIgnoreCase(contract, "insAmValue"));
                    logger.debug("Strahovaia summa, peredannaia v iavnom vide = " + insAmValue);
                    Double premValue = getDoubleParam(chainedGetIgnoreCase(contract, "premValue"));
                    logger.debug("Strahovaia premiia, peredannaia v iavnom vide = " + premValue);
                    Object programInsAmValue = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".INSAMVALUE");
                    if (programInsAmValue != null) {
                        contract.put("INSAMVALUE", programInsAmValue);
                        logger.debug("Strahovaia summa, opredelennaia po programme = " + programInsAmValue);
                    } else {
                        logger.debug("Opredelit` po programme strahovuiu summu ne udalos`, budet ispol`zovana peredannaia v iavnom vide.");
                    }
                    Object programPremValue = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".PREMVALUE");
                    if (programPremValue != null) {
                        contract.put("PREMVALUE", programPremValue);
                        logger.debug("Strahovaia premiia, opredelennaia po programme = " + programPremValue);
                    } else {
                        logger.debug("Opredelit` po programme strahovuiu premiiu ne udalos`, budet ispol`zovana peredannaia v iavnom vide.");
                    }
                } else {
                    logger.debug("Ne udalos` opredelit` sistemnoe imia programmy` strahovaniia - spetcificheskie dlia konkretny`kh produktov pereraschety` summ vy`polneny` ne budut!");
                }
                break;
            //case PRODUCT_CODE_SBOL_SIS:
            //    // todo: сабж
            //    break;
            case PRODUCT_CODE_SBOL_MORTGAGE:
                logger.debug("");
                logger.debug("Raschet strahovoi` premii...");
                // страховая сумма
                Double insAmValue = getDoubleParam(chainedGetIgnoreCase(contract, "insAmValue"));
                logger.debug("Strahovaia summa, peredannaia v iavnom vide = " + insAmValue);
                // страховая премия
                Double premValue = getDoubleParam(chainedGetIgnoreCase(contract, "premValue"));
                logger.debug("Strahovaia premiia, peredannaia v iavnom vide = " + premValue);
                // пересчет премии - без использования калькулятора, т. к. формула простая
                premValue = calcMortgageSBOLPremValue(insAmValue);
                logger.debug("Vy`chislennaia strahovaia premiia = " + premValue);
                contract.put("PREMVALUE", premValue);
                break;
            default:
                logger.debug("Ne udalos` opredelit` produkt - spetcificheskie dlia konkretny`kh produktov pereraschety` summ vy`polneny` ne budut!");
        }*/
        logger.debug("");

        // копирование наследуемых и дублирующихся значений по списку из contractDuplicatedKeys
        for (int d = 0; d < contractDuplicatedKeys.length; d++) {
            String mainKey = contractDuplicatedKeys[d][0];
            String duplicateKey = contractDuplicatedKeys[d][1];
            Object mainValue = chainedGet(contract, mainKey);
            Object duplicateValue = chainedGet(contract, duplicateKey);
            if ((mainValue != null) && (duplicateValue == null)) {
                chainedPut(contract, duplicateKey, mainValue);
            }
        }
    }

    private List<Map<String, Object>> filterProdStructs(List<Map<String, Object>> prodStructs, Long parentStructId, Long discriminator) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (prodStructs != null) {
            for (Map<String, Object> bean : prodStructs) {
                boolean fAdd = true;
                if (parentStructId != null) {
                    if ((bean.get("PARENTSTRUCTID") == null) || (Long.valueOf(bean.get("PARENTSTRUCTID").toString()).longValue() != parentStructId.longValue())) {
                        fAdd = false;
                    }
                }
                if (discriminator != null) {
                    if ((bean.get("DISCRIMINATOR") == null) || (Long.valueOf(bean.get("DISCRIMINATOR").toString()).longValue() != discriminator.longValue())) {
                        fAdd = false;
                    }
                }
                if (fAdd) {
                    result.add(bean);
                }
            }
        }
        return result;
    }

    private Long getProdProgramIdByProgCode(String programCode, Long productVersionId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        params.put("PRODVERID", productVersionId);
        params.put("PROGCODE", programCode);
        Map<String, Object> qRes = this.callService(Constants.B2BPOSWS, "dsB2BProductProgramBrowseListByParam", params, login, password);
        if (qRes.get("PRODPROGID") != null) {
            return Long.valueOf(qRes.get("PRODPROGID").toString());
        } else {
            return null;
        }
    }

    private void vzrCalcContractFinishDate(Map<String, Object> b2bContract, Map<String, Object> product, String programCode,
            String login, String password) throws Exception {
        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) b2bContract.get("INSOBJGROUPLIST");
        if (insObjGroupList != null) {
            Map<String, Object> prodVerMap = (Map<String, Object>) product.get("PRODVER");
            if (prodVerMap != null) {
                List<Map<String, Object>> prodStructs = (List<Map<String, Object>>) prodVerMap.get("PRODSTRUCTS");
                if (prodStructs != null) {
                    Map<String, Object> prodDefRes = null;
                    if (product.get("PRODDEFVALS") != null) {
                        List<Map<String, Object>> prodDefList = (List<Map<String, Object>>) product.get("PRODDEFVALS");
                        CopyUtils.sortByStringFieldName(prodDefList, "NAME");
                        List<Map<String, Object>> prodDefListFiltered = CopyUtils.filterSortedListByStringFieldName(prodDefList, "NAME", RISKSUMSHBDATAVERID_PARAMNAME);
                        if (prodDefListFiltered != null) {
                            if (!prodDefListFiltered.isEmpty()) {
                                prodDefRes = prodDefListFiltered.get(0);
                            }
                        }
                    }
                    if (prodDefRes == null) {

                        Long productConfigId = Long.valueOf(product.get("PRODCONFID").toString());
                        Map<String, Object> prodDefParams = new HashMap<String, Object>();
                        prodDefParams.put(RETURN_AS_HASH_MAP, "TRUE");
                        prodDefParams.put("PRODCONFID", productConfigId);
                        prodDefParams.put("NAME", RISKSUMSHBDATAVERID_PARAMNAME);
                        prodDefRes = this.callService(Constants.B2BPOSWS, "dsB2BProductDefaultValueBrowseListByParam", prodDefParams, login, password);
                    }
                    Map<String, Object> hbRes = null;
                    // если задан справочник с суммами по рискам, нужно его обработать
                    if (prodDefRes.get("VALUE") != null) {
                        Long sumsHBDataVerId = Long.valueOf(prodDefRes.get("VALUE").toString());
                        Map<String, Object> hbParams = new HashMap<String, Object>();
                        hbParams.put(RETURN_AS_HASH_MAP, "TRUE");
                        hbParams.put("HBDATAVERID", sumsHBDataVerId);
                        hbParams.put("insuranceProgram", programCode);
                        hbRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", hbParams, login, password);
                        // нашли справочник?
                        if (hbRes.get("HBSTOREID") != null) {
                            if (hbRes.get("dayCount") != null) {
                                Long dayCount = Long.valueOf(hbRes.get("dayCount").toString());
                                Date startDate = (Date) parseAnyDate(b2bContract.get("STARTDATE"), Date.class, "STARTDATE");
                                GregorianCalendar gcFinishDate = new GregorianCalendar();
                                gcFinishDate.setTime(startDate);
                                gcFinishDate.add(Calendar.DAY_OF_YEAR, dayCount.intValue() - 1);
                                gcFinishDate.set(Calendar.HOUR_OF_DAY, 23);
                                gcFinishDate.set(Calendar.MINUTE, 59);
                                gcFinishDate.set(Calendar.SECOND, 59);
                                gcFinishDate.set(Calendar.MILLISECOND, 0);
                                b2bContract.put("DURATION", dayCount);
                                if (hbRes.get("annualPolicy") != null) {
                                    if ("1".equals(hbRes.get("annualPolicy").toString())) {
                                        // если годовой полис - дюратион по справочнику, а дата окончаания - через год.
                                        gcFinishDate.setTime(startDate);
                                        gcFinishDate.add(Calendar.YEAR, 1);
                                        gcFinishDate.add(Calendar.DATE, 0);                                        
                                    }
                                }
                                
                                b2bContract.put("FINISHDATE", gcFinishDate.getTime());
                            }
                        }
                    }
                }
            }
        }
    }

    private void createObjectsAndRisksByHB(Map<String, Object> result, Map<String, Object> b2bContract, Map<String, Object> product, String programCode,
            Long requestId, String xmlProductCode, String login, String password) throws Exception {

        logger.debug("");
        logger.debug("createObjectsAndRisksByHB begin");
        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) b2bContract.get("INSOBJGROUPLIST");
        if (insObjGroupList != null) {
            Map<String, Object> prodVerMap = (Map<String, Object>) product.get("PRODVER");
            if (prodVerMap != null) {
                List<Map<String, Object>> prodStructs = (List<Map<String, Object>>) prodVerMap.get("PRODSTRUCTS");
                if (prodStructs != null) {
                    Map<String, Object> prodDefRes = null;
                    if (product.get("PRODDEFVALS") != null) {
                        List<Map<String, Object>> prodDefList = (List<Map<String, Object>>) product.get("PRODDEFVALS");
                        CopyUtils.sortByStringFieldName(prodDefList, "NAME");
                        List<Map<String, Object>> prodDefListFiltered = CopyUtils.filterSortedListByStringFieldName(prodDefList, "NAME", RISKSUMSHBDATAVERID_PARAMNAME);
                        if (prodDefListFiltered != null) {
                            if (!prodDefListFiltered.isEmpty()) {
                                prodDefRes = prodDefListFiltered.get(0);
                            }
                        }
                    }
                    Long productConfigId = Long.valueOf(product.get("PRODCONFID").toString());
                    if (prodDefRes == null) {
                        logger.debug("productConfigId = " + productConfigId);
                        Map<String, Object> prodDefParams = new HashMap<String, Object>();
                        prodDefParams.put(RETURN_AS_HASH_MAP, "TRUE");
                        prodDefParams.put("PRODCONFID", productConfigId);
                        prodDefParams.put("NAME", RISKSUMSHBDATAVERID_PARAMNAME);
                        prodDefRes = this.callService(Constants.B2BPOSWS, "dsB2BProductDefaultValueBrowseListByParam", prodDefParams, login, password);
                    }
                    Map<String, Object> hbRes = null;
                    // если задан справочник с суммами по рискам, нужно его обработать
                    if (prodDefRes.get("VALUE") != null) {
                        Long sumsHBDataVerId = Long.valueOf(prodDefRes.get("VALUE").toString());
                        logger.debug("sumsHBDataVerId = " + sumsHBDataVerId);
                        Map<String, Object> hbParams = new HashMap<String, Object>();
                        hbParams.put(RETURN_AS_HASH_MAP, "TRUE");
                        hbParams.put("HBDATAVERID", sumsHBDataVerId);
                        hbParams.put("insuranceProgram", programCode);
                        logger.debug("programCode = " + programCode);
                        hbRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", hbParams, login, password);
                        if (xmlProductCode.equals(PRODUCT_CODE_SBOL_VZR)) {
                            //необходимо проверить соответствие списка застрахованных программе страхования.
                            if (b2bContract.get("MEMBERLIST") != null) {
                                List<Map<String, Object>> memberList = ((List<Map<String, Object>>) b2bContract.get("MEMBERLIST"));
                                if (!memberList.isEmpty()) {
                                    if (hbRes.get("babes") != null) {
                                        if (hbRes.get("adultsAndChildren3_60") != null) {
                                            if (hbRes.get("old61_70") != null) {
                                                int babes = 0;
                                                if (hbRes.get("babes") != null) {
                                                    babes = Integer.valueOf(hbRes.get("babes").toString()).intValue();
                                                }
                                                int adultsAndChildren3_60 = 0;
                                                if (hbRes.get("adultsAndChildren3_60") != null) {
                                                    adultsAndChildren3_60 = Integer.valueOf(hbRes.get("adultsAndChildren3_60").toString()).intValue();
                                                }
                                                int old61_70 = 0;
                                                if (hbRes.get("old61_70") != null) {
                                                    old61_70 = Integer.valueOf(hbRes.get("old61_70").toString()).intValue();
                                                }
                                                int babesIn = 0;
                                                int adultsAndChildren3_60In = 0;
                                                int old61_70In = 0;
                                                Date signDate = getDateParam(b2bContract.get("SIGNDATE"));
                                                for (Map<String, Object> memberMap : memberList) {
                                                    Date birthDate = getDateParam(memberMap.get("BIRTHDATE"));
                                                    int age = getAge(signDate, birthDate);
                                                    if (age <= 2) {
                                                        babesIn++;
                                                    }
                                                    if ((age >= 3) && (age <= 60)) {
                                                        adultsAndChildren3_60In++;
                                                    }
                                                    if ((age >= 61) && (age <= 70)) {
                                                        old61_70In++;
                                                    }
                                                }
                                                if ((babesIn == babes) && (adultsAndChildren3_60In == adultsAndChildren3_60) && (old61_70In == old61_70)) {
                                                    // все ок. возраста застрахованных на дату подписания совпадают с возрастами допустимыми по программе страхования
                                                } else {
                                                    returnErrorAndStopSaving(result, "Возраст застрахованных не попадает в выбранную программу страхования", requestId, login, password);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    returnErrorAndStopSaving(result, "Пустой MEMBERLIST", requestId, login, password);
                                }
                            } else {
                                returnErrorAndStopSaving(result, "Отсутствует MEMBERLIST", requestId, login, password);
                            }
                        }
                        // если не нашли запись справочника по указанной программе, зануляем результат
                        if (hbRes.get("HBSTOREID") == null) {
                            hbRes = null;
                        }
                    }
                    if (isError) {
                        // при ошибке дальнейшие действия не производим.
                        return;
                    }
                    logger.debug("createObjectsAndRisksByHB bad code");
                    Double contractPremValue = null;
                    if (hbRes != null) {
                        contractPremValue = 0.0;
                    }
                    for (Map<String, Object> objGroupBean : insObjGroupList) {
                        Long objGroupStructId = Long.valueOf(objGroupBean.get("PRODSTRUCTID").toString());
                        List<Map<String, Object>> objList = filterProdStructs(prodStructs, objGroupStructId, 3L);
                        if ((objList != null) && (objList.size() > 0)) {
                            List<Map<String, Object>> resObjList = new ArrayList<Map<String, Object>>();
                            for (Map<String, Object> objBean : objList) {
                                Map<String, Object> objMap = new HashMap<String, Object>();
                                Map<String, Object> insObjMap = new HashMap<String, Object>();
                                insObjMap.put("HBDATAVERID", objBean.get("HBDATAVERID"));
                                insObjMap.put("PRODSTRUCTID", objBean.get("PRODSTRUCTID"));
                                Map<String, Object> contrObjMap = new HashMap<String, Object>();
                                contrObjMap.put("CURRENCYID", b2bContract.get("INSAMCURRENCYID"));
                                contrObjMap.put("DURATION", b2bContract.get("DURATION"));
                                contrObjMap.put("STARTDATE", b2bContract.get("STARTDATE"));
                                contrObjMap.put("FINISHDATE", b2bContract.get("FINISHDATE"));
                                contrObjMap.put("PREMCURRENCYID", b2bContract.get("PREMCURRENCYID"));
                                Long objProdStructId = Long.valueOf(objBean.get("PRODSTRUCTID").toString());
                                List<Map<String, Object>> riskList = filterProdStructs(prodStructs, objProdStructId, 4L);
                                Double contrObjPremValue = 0.0;
                                Double contrObjInsAmValue = 0.0;
                                if (hbRes == null) {
                                    if (b2bContract.get("PREMVALUE") != null) {
                                        contrObjPremValue = Double.valueOf(b2bContract.get("PREMVALUE").toString());
                                    }
                                    if (b2bContract.get("INSAMVALUE") != null) {
                                        contrObjInsAmValue = Double.valueOf(b2bContract.get("INSAMVALUE").toString());
                                    }
                                }
                                if ((riskList != null) && (riskList.size() > 0)) {
                                    List<Map<String, Object>> resRiskList = new ArrayList<Map<String, Object>>();
                                    for (Map<String, Object> riskBean : riskList) {
                                        boolean fAddRisk = false;
                                        Double insAmValue = null;
                                        Double premValue = null;
                                        if (hbRes != null) {
                                            String riskSysName = riskBean.get("SYSNAME").toString();
                                            String insAmParamName = "INSAM_" + riskSysName;
                                            String premParamName = "PREM_" + riskSysName;
                                            logger.debug("riskSysName = " + riskSysName + " (" + riskBean.get("NAME") + ")");
                                            if ((hbRes.get(insAmParamName) != null) || ((hbRes.get(premParamName) != null))) {
                                                if (hbRes.get(insAmParamName) != null) {
                                                    insAmValue = Double.valueOf(hbRes.get(insAmParamName).toString());
                                                    if (insAmValue.doubleValue() > contrObjInsAmValue.doubleValue()) {
                                                        contrObjInsAmValue = insAmValue;
                                                    }
                                                }
                                                if (hbRes.get(premParamName) != null) {
                                                    premValue = Double.valueOf(hbRes.get(premParamName).toString());
                                                    contractPremValue += premValue;
                                                    contrObjPremValue += premValue;
                                                }
                                                fAddRisk = true;
                                            } else {
                                                logger.debug("              INSAMVALUE / PREMVALUE = znacheniia ne nai`deny`, risk budet iscliuchen");
                                            }
                                        } else {
                                            if (b2bContract.get("INSAMVALUE") != null) {
                                                insAmValue = Double.valueOf(b2bContract.get("INSAMVALUE").toString());
                                            }
                                            if (b2bContract.get("PREMVALUE") != null) {
                                                premValue = Double.valueOf(b2bContract.get("PREMVALUE").toString());
                                            }
                                            fAddRisk = true;
                                        }
                                        if (fAddRisk) {
                                            Map<String, Object> riskMap = new HashMap<String, Object>();
                                            riskMap.put("PRODSTRUCTID", riskBean.get("PRODSTRUCTID"));
                                            riskMap.put("CURRENCYID", b2bContract.get("INSAMCURRENCYID"));
                                            riskMap.put("DURATION", b2bContract.get("DURATION"));
                                            riskMap.put("STARTDATE", b2bContract.get("STARTDATE"));
                                            riskMap.put("FINISHDATE", b2bContract.get("FINISHDATE"));
                                            riskMap.put("PREMCURRENCYID", b2bContract.get("PREMCURRENCYID"));
                                            riskMap.put("INSAMVALUE", insAmValue);
                                            riskMap.put("PREMVALUE", premValue);
                                            logger.debug("              INSAMVALUE = " + insAmValue);
                                            logger.debug("              PREMVALUE = " + premValue);
                                            Map<String, Object> riskMapExt = new HashMap<String, Object>();
                                            riskMapExt.put("HBDATAVERID", riskBean.get("HBDATAVERID"));
                                            riskMap.put("CONTRRISKEXTMAP", riskMapExt);
                                            resRiskList.add(riskMap);
                                        }
                                    }
                                    contrObjMap.put("CONTRRISKLIST", resRiskList);
                                }
                                if ((contrObjMap.get("CONTRRISKLIST") != null) && ((List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST")).size() > 0) {
                                    contrObjMap.put("PREMVALUE", contrObjPremValue);
                                    contrObjMap.put("INSAMVALUE", contrObjInsAmValue);
                                    objMap.put("INSOBJMAP", insObjMap);
                                    objMap.put("CONTROBJMAP", contrObjMap);
                                    resObjList.add(objMap);
                                }
                            }
                            objGroupBean.put("OBJLIST", resObjList);
                        }
                    }
                    // если задан справочник сумм, пытаемся из него загрузить показатели по договору
                    if (hbRes != null) {
                        List<Map<String, Object>> prodValues = (List<Map<String, Object>>) product.get("PRODVALUES");
                        if ((prodValues != null) && (prodValues.size() > 0)) {
                            Map<String, Object> contrExtMap = (Map<String, Object>) b2bContract.get("CONTREXTMAP");
                            if (contrExtMap != null) {
                                for (Map<String, Object> bean : prodValues) {
                                    if ((bean.get("PRODCONFID") != null) && (Long.valueOf(bean.get("PRODCONFID").toString()).longValue() == productConfigId.longValue())) {
                                        String valueName = bean.get("NAME").toString();
                                        if (!valueName.equalsIgnoreCase("insuranceProgram")) {
                                            if (hbRes.get(valueName) != null) {
                                                contrExtMap.put(valueName, hbRes.get(valueName));
                                            }
                                        } else {
                                            // не требуется, код и идентифкатор программы страхования уже определен ранее - в genAdditionalSaveParams
                                            // contrExtMap.put(valueName, getProdProgramIdByProgCode(programCode, Long.valueOf(b2bContract.get("PRODVERID").toString()), login, password));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (contractPremValue != null) {
                        b2bContract.put("PREMVALUE", contractPremValue);
                    }
                }
            }
        }
        logger.debug("createObjectsAndRisksByHB end");
        logger.debug("");
    }

    private void applyCalcRateRule(Map<String, Object> b2bContract, Map<String, Object> product) {
        if ((b2bContract.get("DOCUMENTDATE") != null) && (product.get("PRODCALCRATERULES") != null)) {
            Date documentDate = (Date) parseAnyDate(b2bContract.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
            Long premCurrencyId = Long.valueOf(b2bContract.get("PREMCURRENCYID").toString());
            List<Map<String, Object>> calcRateRuleList = (List<Map<String, Object>>) product.get("PRODCALCRATERULES");
            // список должен быть отсортирован по возрастанию поля RULEDATE
            for (Map<String, Object> bean : calcRateRuleList) {
                if ((bean.get("RULEDATE") != null) && (bean.get("CURRENCYID") != null)) {
                    Date ruleDate = (Date) parseAnyDate(bean.get("RULEDATE"), Date.class, "RULEDATE");
                    Long currencyId = Long.valueOf(bean.get("CURRENCYID").toString());
                    // нашли правило, подходящее под дату и валюту договора
                    if ((documentDate.getTime() < ruleDate.getTime()) && (premCurrencyId.longValue() == currencyId.longValue())) {
                        Long calcVariantId = Long.valueOf(bean.get("CALCVARIANTID").toString());
                        switch (calcVariantId.intValue()) {
                            case 4:
                                b2bContract.put("CURRENCYRATE", bean.get("RATEVALUE"));
                                break;
                        }
                    }
                }
            }
        }
    }

    private void returnErrorAndStopSaving(Map<String, Object> result, String ErrorMessage, Long requestId, String login, String password) throws Exception {
        isError = true;
        logger.debug("Ne udalos` sokhranit` dogovor. " + ErrorMessage);
        updateRequest(requestId, 1, login, password);
        result.put("Error", "Не удалось сохранить договор.");
        result.put("Reason", ErrorMessage);
    }

    private String checkB2BContractSBOLParams(String xmlProductCode, Map<String, Object> b2bContract) {
        String result = null;
        if (xmlProductCode.equalsIgnoreCase(PRODUCT_CODE_SBOL_ANTIMITE_ROS)) {
            if (b2bContract.get("INSURERMAP") != null) {
                Map<String, Object> insurerMap = (Map<String, Object>) b2bContract.get("INSURERMAP");
                if (insurerMap.get("BIRTHDATE") != null) {
                    Date birthDate = (Date) parseAnyDate(insurerMap.get("BIRTHDATE"), Date.class, "BIRTHDATE");
                    GregorianCalendar gcBirthDate = new GregorianCalendar();
                    gcBirthDate.setTime(birthDate);
                    GregorianCalendar gcTodayMinus18 = new GregorianCalendar();
                    gcTodayMinus18.setTime(new Date());
                    gcTodayMinus18.add(Calendar.YEAR, -18);
                    gcTodayMinus18.set(Calendar.HOUR_OF_DAY, 0);
                    gcTodayMinus18.set(Calendar.MINUTE, 0);
                    gcTodayMinus18.set(Calendar.SECOND, 0);
                    if (gcBirthDate.getTimeInMillis() > gcTodayMinus18.getTimeInMillis()) {
                        return "Дата рождения страхователя должна быть от 18 лет";
                    }
                }
            }
            List<Map<String, Object>> memberList = (List<Map<String, Object>>) b2bContract.get("MEMBERLIST");
            if ((memberList != null) && (memberList.size() > 0)) {
                Map<String, Object> memberMap = memberList.get(0);
                if (memberMap.get("BIRTHDATE") != null) {
                    Date birthDate = (Date) parseAnyDate(memberMap.get("BIRTHDATE"), Date.class, "BIRTHDATE");
                    GregorianCalendar gcBirthDate = new GregorianCalendar();
                    gcBirthDate.setTime(birthDate);
                    GregorianCalendar gcTodayMinus3 = new GregorianCalendar();
                    gcTodayMinus3.setTime(new Date());
                    gcTodayMinus3.add(Calendar.YEAR, -3);
                    gcTodayMinus3.set(Calendar.HOUR_OF_DAY, 0);
                    gcTodayMinus3.set(Calendar.MINUTE, 0);
                    gcTodayMinus3.set(Calendar.SECOND, 0);
                    GregorianCalendar gcTodayMinus65 = new GregorianCalendar();
                    gcTodayMinus65.setTime(new Date());
                    gcTodayMinus65.add(Calendar.YEAR, -65);
                    gcTodayMinus65.set(Calendar.HOUR_OF_DAY, 0);
                    gcTodayMinus65.set(Calendar.MINUTE, 0);
                    gcTodayMinus65.set(Calendar.SECOND, 0);
                    if (!((gcTodayMinus3.getTimeInMillis() >= gcBirthDate.getTimeInMillis()) && (gcBirthDate.getTimeInMillis() >= gcTodayMinus65.getTimeInMillis()))) {
                        return "Дата рождения застрахованного должна быть от 3 до 65 лет";
                    }
                }
            }
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractSBOLSave(Map<String, Object> params) throws Exception {
        isError = false;
        boolean isVerboseLog = true;
        Map<String, Object> result = new HashMap<String, Object>();
        Long requestId = getLongParam(params.get("REQUESTID"));

        if (params.get("REQUESTQUEUEID") == null) {
            if (params.get("REQUESTID") != null) {
                params.put("REQUESTQUEUEID", params.get("REQUESTID"));
            }
        }
        logger.debug("begin!!! dsB2BContractSBOLSave");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> b2bContract = new HashMap<String, Object>();
        Map<String, Object> sbolContract = (Map<String, Object>) chainedGetIgnoreCase(params, contractRootKeys);
        String xmlProductCode = getStringParam(chainedGetIgnoreCase(sbolContract, "InsProduct"));
        String payFactNumber = getStringParam(chainedGetIgnoreCase(sbolContract, "ID"));

        if ((xmlProductCode.equalsIgnoreCase(PRODUCT_CODE_SBOL_ANTIMITE_ROS))) {
            sbolContract.put("InsProgram", "00001");
        }

        String programCode = getStringParam(chainedGetIgnoreCase(sbolContract, "InsProgram"));

        Map<String, Object> productParams = new HashMap<String, Object>();
        Object sbolProdConfID = convertValue(xmlProductCode, prodConfIDConvertRules);
        productParams.put("PRODCONFID", sbolProdConfID);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByID", productParams, login, password);
        sbolContract.put("PRODCONF", product);
        // развертывание списков в карты вида 'имяСписка_системноеИмяЭлементаСписка'
        String[][] expanded = contractExpandedLists;
        for (int e = 0; e < expanded.length; e++) {
            String listName = expanded[e][0];
            String sysAttrName = expanded[e][1];
            expandListToMapBySysName(sbolContract, listName, sysAttrName);
        }
        List<String[]> contractKeysRelations = new ArrayList<String[]>();
        contractKeysRelations.addAll(Arrays.asList(commonKeysRelations));

        if (xmlProductCode.equals(PRODUCT_CODE_SBOL_CIB)) {
            contractKeysRelations.addAll(Arrays.asList(cibKeysRelations));
        } else if (xmlProductCode.equals(PRODUCT_CODE_SBOL_HIB)) {
            contractKeysRelations.addAll(Arrays.asList(hibKeysRelations));
        } else if (xmlProductCode.equals(PRODUCT_CODE_SBOL_VZR)) {
            contractKeysRelations.addAll(Arrays.asList(vzrKeysRelations));
        } else if (xmlProductCode.equals(PRODUCT_CODE_SBOL_MORTGAGE)) {
            contractKeysRelations.addAll(Arrays.asList(mortgageKeysRelations));
        } else if (xmlProductCode.equals(PRODUCT_CODE_SBOL_ANTIMITE_ROS)) {
            contractKeysRelations.addAll(Arrays.asList(miteRosKeysRelations));
        }

        int fromIndex = 0;
        int toIndex = 1;
        // копирование сведений в новую структуру
        for (int i = 0; i < contractKeysRelations.size(); i++) {
            String[] contractKeyRelation = contractKeysRelations.get(i);
            String newKey = contractKeyRelation[fromIndex];
            String oldKey = contractKeyRelation[toIndex];
            Boolean isCreativePut = true;
            String convertRulesStr = null;
            if ((contractKeyRelation.length > 3) && (!contractKeyRelation[3].isEmpty())) {
                convertRulesStr = contractKeyRelation[3];
            }
            Object rawValue = chainedGetIgnoreCase(sbolContract, oldKey);
            //if (rawValue == null) {
            //    oldKey = getKeysChainWithLastKeyUpperCase(oldKey);
            //    rawValue = chainedGet(rawParams, oldKey);
            //}
            Object value = null;
            if (rawValue instanceof List) {
                List listValue = (List) rawValue;
                value = listValue.subList(listValue.size() - 1, listValue.size());
            } else {
                value = rawValue;
            }
            if (value != null) {
                if (convertRulesStr != null) {
                    value = convertValue(value, convertRulesStr);
                }

                if (isVerboseLog) {
                    logger.debug("");
                    logger.debug("Ishodny`i` cliuch: " + oldKey);
                    logger.debug("Konechny`i` cliuch: " + newKey);
                }
                chainedCreativePut(b2bContract, newKey, value, isCreativePut);
                if (isVerboseLog) {
                    logger.debug("Ustanovleno znachenie: " + value + ((convertRulesStr == null) ? "" : " (poluchennoe iz '" + rawValue + "' s primeneniem pravila '" + convertRulesStr + "')"));
                    //chainedCreativePut(rawParamsCopyForLog, oldKey, "'ЗНАЧЕНИЕ ПЕРЕНЕСЕНО'");
                }
            }
        }

        genAdditionalSaveParams(result, b2bContract, product, xmlProductCode, requestId, login, password);

        // установка значний по-умолчанию
        for (int i = 0; i < contractKeysRelations.size(); i++) {
            String[] contractKeyRelation = contractKeysRelations.get(i);
            String newKey = contractKeyRelation[fromIndex];
            Object newKeyValue = chainedGet(b2bContract, newKey);
            Boolean isCreativePut = true;
            String defaultValue = "";
            if ((contractKeyRelation.length > 2) && (!contractKeyRelation[2].isEmpty())) {
                defaultValue = contractKeyRelation[2];
            }
            if ((newKeyValue == null) && (!defaultValue.isEmpty())) {
                if (isVerboseLog) {
                    logger.debug("");
                    logger.debug("Konechny`i` cliuch: " + newKey);
                    logger.debug("Ustanovleno znachenie po-umolchaniiu: " + defaultValue);
                }
                chainedCreativePut(b2bContract, newKey, defaultValue, isCreativePut);
            }
        }

        // свертывание карт вида 'имяСписка.системноеИмяЭлементаСписка' в списки
        String[][] collapsed = contractCollapsedMaps;
        for (int e = 0; e < collapsed.length; e++) {
            String listName = collapsed[e][0];
            String sysAttrName = collapsed[e][1];
            collapseMapToListBySysName(b2bContract, listName, sysAttrName);
        }

        // свертывание карт вида 'имяСписка.системноеИмяЭлементаСписка' в списки для возрата продукта в начальное состояние 
        collapsed = contractExpandedLists;
        for (int e = 0; e < collapsed.length; e++) {
            String listName = collapsed[e][0];
            String sysAttrName = collapsed[e][1];
            collapseMapToListBySysName(sbolContract, listName, sysAttrName);
        }
        // вычисление даты окончания действия договора для ВЗР
        if (xmlProductCode.equals(PRODUCT_CODE_SBOL_VZR)) {
            vzrCalcContractFinishDate(b2bContract, product, programCode, login, password);
        }
        // создание объектов и рисков с копированием сумм из справочника в зависимости от программы
        createObjectsAndRisksByHB(result, b2bContract, product, programCode, requestId, xmlProductCode, login, password);
        if (isError) {
            // при ошибке выходим и возвращаем результат. сохранять дальше не нужно.
            return result;
        }
        // 
        if (xmlProductCode.equals(PRODUCT_CODE_SBOL_VZR)) {
            Map<String, Object> contrExtMap = (Map<String, Object>) b2bContract.get("CONTREXTMAP");
            Long territory = Long.valueOf(contrExtMap.get("insuranceTerritory").toString());
            if ((territory.longValue() == 0) || (territory.longValue() == 1)) {
                if ((b2bContract.get("STARTDATE") != null) && (b2bContract.get("FINISHDATE") != null)) {
                    Date sd = (Date) parseAnyDate(b2bContract.get("STARTDATE"), Date.class, "STARTDATE");
                    Date fd = (Date) parseAnyDate(b2bContract.get("FINISHDATE"), Date.class, "FINISHDATE");
                    Date fdShengen = new Date();
                    fdShengen.setTime(fd.getTime() + 15 * 24 * 60 * 60 * 1000);
                    GregorianCalendar sdgc = new GregorianCalendar();
                    sdgc.setTime(sd);
                    sdgc.set(Calendar.YEAR, sdgc.get(Calendar.YEAR) + 1);
                    sdgc.set(Calendar.DATE, sdgc.get(Calendar.DATE) - 1);
                    if (fdShengen.getTime() > sdgc.getTimeInMillis()) {
                        fdShengen.setTime(sdgc.getTimeInMillis());
                    }
                    b2bContract.put("FINISHDATE", fdShengen);
                }
            }
        }
        // применить правило расчета курса оплаты премии (если задано)
        applyCalcRateRule(b2bContract, product);

        if (isDebugCodeActive) {
            //!только для отладки!
            b2bContract.put("NOTE", "СБОЛ - проверка сохранения (" + (new Date()).toString() + ")");
        }

        // проверка параметров договора СБОЛ
        String checkRes = checkB2BContractSBOLParams(xmlProductCode, b2bContract);
        if ((checkRes != null) && (!checkRes.isEmpty())) {
            updateRequest(requestId, 1, login, password);
            result.put("Error", checkRes);
            logger.debug("end!!! dsB2BContractSBOLSave");
            return result;
        }
        //
        Map<String, Object> saveParams = new HashMap<String, Object>();

        // определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения
        // todo: возможно, заменить на использование updateSessionParamsIfNullByCallingUserCreds в dsB2BContrSave
        Map<String, Object> checkLoginParams = new HashMap<String, Object>();
        checkLoginParams.put("username", XMLUtil.getUserName(login));
        checkLoginParams.put("passwordSha", password);
        Map<String, Object> checkLoginResult = WsUtils.getFirstItemFromResultMap(this.selectQuery("dsCheckLogin", checkLoginParams));
        if (checkLoginResult != null) {
            saveParams.put(Constants.SESSIONPARAM_USERACCOUNTID, checkLoginResult.get("USERACCOUNTID"));
            saveParams.put(Constants.SESSIONPARAM_DEPARTMENTID, checkLoginResult.get("DEPARTMENTID"));
        }

        saveParams.putAll(b2bContract);
        saveParams.put(RETURN_AS_HASH_MAP, true);
        saveParams.put("REQUESTQUEUEID", params.get("REQUESTID"));
        saveParams.put(DEPARTMENTS_KEY_NAME, sbolContract.get(DEPARTMENTS_KEY_NAME));
        Map<String, Object> saveResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrSave", saveParams, login, password);

        //создать фактический платеж по данным договора.
        Map<String, Object> payFactParams = new HashMap<String, Object>();
        payFactParams.put("CONTRNODEID", saveResult.get("CONTRNODEID"));
        payFactParams.put("AMCURRENCYID", saveResult.get("PREMCURRENCYID"));
        payFactParams.put("AMVALUE", saveResult.get("PREMVALUE"));
        if (saveResult.get("PREMCURRENCYID") != null) {
            logger.debug("sbol premcurrencyid = " + saveResult.get("PREMCURRENCYID").toString());
            if ("3".equals(saveResult.get("PREMCURRENCYID").toString()) || "2".equals(saveResult.get("PREMCURRENCYID").toString())) {
                if (saveResult.get("CURRENCYRATE") != null) {
                    BigDecimal curRate = BigDecimal.valueOf(Double.valueOf(saveResult.get("CURRENCYRATE").toString()));
                    if (saveResult.get("PREMVALUE") != null) {
                        BigDecimal amvalue = BigDecimal.valueOf(Double.valueOf(saveResult.get("PREMVALUE").toString()));
                        BigDecimal amValueRub = amvalue.multiply(curRate).setScale(2, RoundingMode.HALF_UP);
                        payFactParams.put("AMVALUERUB", amValueRub.doubleValue());
                    }
                }
                payFactParams.put("AMVALUE", saveResult.get("PREMVALUE"));
            } else {
                if (saveResult.get("CURRENCYRATE") != null) {
                    BigDecimal curRate = BigDecimal.valueOf(Double.valueOf(saveResult.get("CURRENCYRATE").toString()));
                    if (saveResult.get("PREMVALUE") != null) {
                        BigDecimal amvalueRub = BigDecimal.valueOf(Double.valueOf(saveResult.get("PREMVALUE").toString()));
                        BigDecimal amValue = amvalueRub.divide(curRate, 2, RoundingMode.HALF_UP);
                        payFactParams.put("AMVALUE", amValue.doubleValue());
                    }
                }
                payFactParams.put("AMVALUERUB", saveResult.get("PREMVALUE"));
            }
        }
        payFactParams.put("NAME", saveResult.get("CONTRNUMBER"));
        payFactParams.put("NAME", "СБОЛ");
        payFactParams.put("PAYFACTNUMBER", payFactNumber);
        payFactParams.put("PAYFACTTYPE", 2);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        if (saveResult.get("DOCUMENTDATE") != null) {
            Date docDate = sdf.parse(saveResult.get("DOCUMENTDATE").toString());
            payFactParams.put("PAYFACTDATE", docDate);
        }
        XMLUtil.convertDateToFloat(payFactParams);
        if (!xmlProductCode.equals(PRODUCT_CODE_SBOL_ANTIMITE_ROS)) {
            Map<String, Object> payFactRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactCreate", payFactParams, login, password);
        }
        // создать плановый платеж
        Map<String, Object> payParams = new HashMap<String, Object>();
        payParams.put("AMOUNT", saveResult.get("PREMVALUE"));
        payParams.put("PAYDATE", payFactParams.get("PAYFACTDATE"));
        payParams.put("CONTRID", saveResult.get("CONTRID"));
        Map<String, Object> payRes = this.callService(Constants.B2BPOSWS, "dsB2BPaymentCreate", payParams, login, password);
        if (isDebugCodeActive) {
            //!только для отладки!
            chainedCreativePut(saveResult, "INSURERMAP.extAttributeList", "=== Удалено для выполнения отладки ===");
            chainedCreativePut(saveResult, "INSURERMAP.extAttributeList2", "=== Удалено для выполнения отладки ===");
        }

        long savedContractID = getLongParam(saveResult.get("CONTRID"));
        Map<String, Object> loadResult = null;
        if (savedContractID != 0) {
            //успех
            updateRequest(requestId, 0, login, password);
            result.putAll(saveResult);
            result.put("PRODCONFID", sbolProdConfID); // идентификатор продукта для печати документов, используется для печати+отправки в dsSendDocumentsPackage и etc

            if (isDebugCodeActive) {
                //!только для отладки!
                Map<String, Object> loadParams = new HashMap<String, Object>();
                loadParams.put("CONTRID", savedContractID);
                loadParams.put(RETURN_AS_HASH_MAP, true);
                loadResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", loadParams, login, password);
                chainedCreativePut(loadResult, "INSURERMAP.extAttributeList", "=== Удалено для выполнения отладки ===");
                chainedCreativePut(loadResult, "INSURERMAP.extAttributeList2", "=== Удалено для выполнения отладки ===");
            }
        } else {
            //неуспех
            updateRequest(requestId, 1, login, password);
            //result = new HashMap<String, Object>();
            result.put("Error", "Не удалось сохранить договор.");
            //todo: вернуть причины по которым договор не был сохранен
            //result.put("Reason", "...");
        }

        if (isDebugCodeActive) {
            //!только для отладки!
            //result = new HashMap<String, Object>();
            //result.put("b2bContract", b2bContract);
            //result.put("saveResult", saveResult);
            //result.put("loadResult", loadResult);
            logger.debug("b2bContract:\n\n" + b2bContract + "\n");
            logger.debug("saveResult:\n\n" + saveResult + "\n");
            logger.debug("loadResult:\n\n" + loadResult + "\n");
        }
        logger.debug("end!!! dsB2BContractSBOLSave");
        return result;
    }

    public static Double calcMortgageSBOLPremValue(Double insAmValue) {
        return roundSum(XML_MORTGAGE_PREM_MULT * insAmValue);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractSBOLSaveTest(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        String sbolProductCode = getStringParam(params.get("SBOLPRODUCTCODE"));
        if (sbolProductCode.equals(PRODUCT_CODE_SBOL_CIB)) {
            // карта
            chainedCreativePut(params, contractRootKeysWithDot + "ID", "39012312318");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProduct", "99001");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProgram", "000002");
            chainedCreativePut(params, contractRootKeysWithDot + "DateSigning", "03.02.2015");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Contry", "643");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Surname", "Петров");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Name", "Иван");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.DateOfBirth", "01.01.1980");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Sex", "male");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Email", "mail@mail.ru");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.MobilePhone", "(916)111-2233");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Kind", "21");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Series", "4500");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Number", "789456");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.DateOfIssue", "01.01.2010");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Authority", "ОВД района Вешняки");
        } else if (sbolProductCode.equals(PRODUCT_CODE_SBOL_HIB)) {
            // дом
            chainedCreativePut(params, contractRootKeysWithDot + "ID", "39015390818");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProduct", "99002");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProgram", "000002");
            chainedCreativePut(params, contractRootKeysWithDot + "DateSigning", "03.02.2015");
            chainedCreativePut(params, contractRootKeysWithDot + "Options", "Квартира");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Contry", "643");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Surname", "Иванов");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Name", "Петр");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.DateOfBirth", "01.01.1980");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Sex", "male");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Email", "mail@mail.ru");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.MobilePhone", "(916)111-2233");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Kind", "21");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Series", "4500");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Number", "789456");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.DateOfIssue", "01.01.2010");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Authority", "ОВД района Вешняки");

            chainedCreativePut(params, contractRootKeysWithDot + "Address.Region", "Москва");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.City", "Москва");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.Street", "Реутовская");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.House", "16");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.Flat", "19");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.ZipCode", "111539*");
        } else if (sbolProductCode.equals(PRODUCT_CODE_SBOL_MORTGAGE)) {
// ипотека
            chainedCreativePut(params, contractRootKeysWithDot + "ID", "999999999999999999");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProduct", "99004");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProgram", "000001");
            chainedCreativePut(params, contractRootKeysWithDot + "DateSigning", "03.02.2015");
            chainedCreativePut(params, contractRootKeysWithDot + "Options", "Квартира");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Contry", "643");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Surname", "Иванов");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Name", "Петр");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.DateOfBirth", "01.01.1980");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Sex", "male");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Email", "mail@mail.ru");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.MobilePhone", "(916)111-2233");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Kind", "21");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Series", "4500");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Number", "789456");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.DateOfIssue", "01.01.2010");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Authority", "ОВД района Вешняки");

            chainedCreativePut(params, contractRootKeysWithDot + "Address.Region", "Москва");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.City", "Москва");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.Street", "Реутовская");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.House", "16");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.Flat", "19");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.ZipCode", "111539");

            chainedCreativePut(params, contractRootKeysWithDot + "CreditContract.DateBegin", "01.01.2010");
            chainedCreativePut(params, contractRootKeysWithDot + "CreditContract.Number", "252522");
            chainedCreativePut(params, contractRootKeysWithDot + "CreditContract.Debt", 1234.0);
        } else if (sbolProductCode.equals(PRODUCT_CODE_SBOL_VZR)) {
            chainedCreativePut(params, contractRootKeysWithDot + "ID", "999999999999999999");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProduct", "99003");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProgram", "000004");
            chainedCreativePut(params, contractRootKeysWithDot + "DateSigning", "03.02.2015");
            chainedCreativePut(params, contractRootKeysWithDot + "DateBeginTravel", "03.04.2015");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Contry", "643");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Surname", "Иванов");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Name", "Петр");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.DateOfBirth", "01.01.1980");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Sex", "male");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Email", "mail@mail.ru");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.MobilePhone", "(916)111-2233");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Kind", "21");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Series", "4500");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Number", "789456");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.DateOfIssue", "01.01.2010");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Authority", "ОВД района Вешняки");

            List<Map<String, Object>> membersList = new ArrayList<Map<String, Object>>();
            for (int i = 1; i <= 3; i++) {
                Map<String, Object> newMap = new HashMap<String, Object>();
                newMap.put("surname", "Sur " + String.valueOf(i));
                newMap.put("name", "Name " + String.valueOf(i));
                newMap.put("dateOfBirth", "01.01.1980");
                membersList.add(newMap);
            }
            chainedCreativePut(params, contractRootKeysWithDot + "members.member", membersList);
        } else if (sbolProductCode.equals(PRODUCT_CODE_SBOL_ANTIMITE_ROS)) {
            chainedCreativePut(params, contractRootKeysWithDot + "ID", "3325674610");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProduct", "00008");
            chainedCreativePut(params, contractRootKeysWithDot + "InsRegion", "76");

            chainedCreativePut(params, contractRootKeysWithDot + "DateSigning", "01.01.2016");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Surname", "Иванов");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Name", "Петр");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.DateOfBirth", "01.01.1980");

            chainedCreativePut(params, contractRootKeysWithDot + "Member.Surname", "Иванов");
            chainedCreativePut(params, contractRootKeysWithDot + "Member.Name", "Петр");
            chainedCreativePut(params, contractRootKeysWithDot + "Member.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Member.DateOfBirth", "01.01.1980");

        }

        params.put(RETURN_AS_HASH_MAP,
                true);
        Map<String, Object> result = (Map<String, Object>) this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractSBOLSave", params, login, password);

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractSBOLModifyTest(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        String sbolProductCode = getStringParam(params.get("SBOLPRODUCTCODE"));
        if (sbolProductCode.equals(PRODUCT_CODE_SBOL_CIB)) {
            // карта
            chainedCreativePut(params, contractRootKeysWithDot + "ID", "39012312318");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProduct", "99001");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProgram", "000002");
            chainedCreativePut(params, contractRootKeysWithDot + "DateSigning", "03.02.2015");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Contry", "643");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Surname", "Петров");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Name", "Иван");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.DateOfBirth", "01.01.1980");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Sex", "male");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Email", "mail@mail.ru");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.MobilePhone", "(916)111-2233");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Kind", "21");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Series", "4500");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Number", "789456");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.DateOfIssue", "01.01.2010");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Authority", "ОВД района Вешняки");
        } else if (sbolProductCode.equals(PRODUCT_CODE_SBOL_HIB)) {
            // дом
            chainedCreativePut(params, contractRootKeysWithDot + "ID", "39015390818");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProduct", "99002");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProgram", "000002");
            chainedCreativePut(params, contractRootKeysWithDot + "DateSigning", "03.02.2015");
            chainedCreativePut(params, contractRootKeysWithDot + "Options", "Квартира");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Contry", "643");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Surname", "Иванов");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Name", "Петр");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.DateOfBirth", "01.01.1980");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Sex", "male");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Email", "mail@mail.ru");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.MobilePhone", "(916)111-2233");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Kind", "21");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Series", "4500");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Number", "789456");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.DateOfIssue", "01.01.2010");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Authority", "ОВД района Вешняки");

            chainedCreativePut(params, contractRootKeysWithDot + "Address.Region", "Москва");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.City", "Москва");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.Street", "Реутовская");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.House", "16");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.Flat", "19");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.ZipCode", "111539*");
        } else if (sbolProductCode.equals(PRODUCT_CODE_SBOL_MORTGAGE)) {
// ипотека
            chainedCreativePut(params, contractRootKeysWithDot + "ID", "999999999999999999");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProduct", "99004");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProgram", "000001");
            chainedCreativePut(params, contractRootKeysWithDot + "DateSigning", "03.02.2015");
            chainedCreativePut(params, contractRootKeysWithDot + "Options", "Квартира");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Contry", "643");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Surname", "Иванов");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Name", "Петр");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.DateOfBirth", "01.01.1980");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Sex", "male");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Email", "mail@mail.ru");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.MobilePhone", "(916)111-2233");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Kind", "21");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Series", "4500");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Number", "789456");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.DateOfIssue", "01.01.2010");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Authority", "ОВД района Вешняки");

            chainedCreativePut(params, contractRootKeysWithDot + "Address.Region", "Москва");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.City", "Москва");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.Street", "Реутовская");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.House", "16");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.Flat", "19");
            chainedCreativePut(params, contractRootKeysWithDot + "Address.ZipCode", "111539");

            chainedCreativePut(params, contractRootKeysWithDot + "CreditContract.DateBegin", "01.01.2010");
            chainedCreativePut(params, contractRootKeysWithDot + "CreditContract.Number", "252522");
            chainedCreativePut(params, contractRootKeysWithDot + "CreditContract.Debt", 1234.0);
        } else if (sbolProductCode.equals(PRODUCT_CODE_SBOL_VZR)) {
            chainedCreativePut(params, contractRootKeysWithDot + "ID", "999999999999999999");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProduct", "99003");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProgram", "000004");
            chainedCreativePut(params, contractRootKeysWithDot + "DateSigning", "03.02.2015");
            chainedCreativePut(params, contractRootKeysWithDot + "DateBeginTravel", "03.04.2015");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Contry", "643");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Surname", "Иванов");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Name", "Петр");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.DateOfBirth", "01.01.1980");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Sex", "male");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Email", "mail@mail.ru");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.MobilePhone", "(916)111-2233");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Kind", "21");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Series", "4500");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Number", "789456");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.DateOfIssue", "01.01.2010");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Document.Authority", "ОВД района Вешняки");

            List<Map<String, Object>> membersList = new ArrayList<Map<String, Object>>();
            for (int i = 1; i <= 3; i++) {
                Map<String, Object> newMap = new HashMap<String, Object>();
                newMap.put("surname", "Sur " + String.valueOf(i));
                newMap.put("name", "Name " + String.valueOf(i));
                newMap.put("dateOfBirth", "01.01.1980");
                membersList.add(newMap);
            }
            chainedCreativePut(params, contractRootKeysWithDot + "members.member", membersList);
        } else if (sbolProductCode.equals(PRODUCT_CODE_SBOL_ANTIMITE_ROS)) {
            chainedCreativePut(params, contractRootKeysWithDot + "ID", "3325674610");
            chainedCreativePut(params, contractRootKeysWithDot + "objectID", "555007");
            chainedCreativePut(params, contractRootKeysWithDot + "InsProduct", "00008");
            chainedCreativePut(params, contractRootKeysWithDot + "InsRegion", "76");

            chainedCreativePut(params, contractRootKeysWithDot + "DateSigning", "01.01.2016");

            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Surname", "Иванов");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Name", "Петр");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Insurer.DateOfBirth", "01.01.1980");

            chainedCreativePut(params, contractRootKeysWithDot + "Member.Surname", "Иванов");
            chainedCreativePut(params, contractRootKeysWithDot + "Member.Name", "Петр");
            chainedCreativePut(params, contractRootKeysWithDot + "Member.Patronymic", "Леонидович");
            chainedCreativePut(params, contractRootKeysWithDot + "Member.DateOfBirth", "01.01.1980");

        }

        params.put(RETURN_AS_HASH_MAP,
                true);
        Map<String, Object> result = (Map<String, Object>) this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractSBOLSave", params, login, password);

        return result;
    }

    private void updateRequest(Long requestId, int stateId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("REQUESTSTATEID", stateId);
        params.put("REQUESTQUEUEID", requestId);
        params.put("PROCESSDATE", new Date());
        XMLUtil.convertDateToFloat(params);
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BRequestQueueUpdate", params, login, password);
    }

    // перенесено в BaseFacade
    /*
    private int getAge(Date signDate, Date birthDate) {
        Calendar dob = Calendar.getInstance();
        dob.setTime(birthDate);
        Calendar today = Calendar.getInstance();
        today.setTime(signDate);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
    */

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractSBOLSpecify(Map<String, Object> params) throws Exception {
        logger.debug("begin!!! dsB2BContractSBOLSpecify");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Long objectId = Long.valueOf(params.get("objectID").toString());
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> browseParams = new HashMap<String, Object>();
        browseParams.put("ReturnAsHashMap", "TRUE");
        browseParams.put("CONTRID", objectId);
        Map<String, Object> qRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamExShort", browseParams, login, password);

        //создать фактический платеж по данным договора.
        Map<String, Object> payFactParams = new HashMap<String, Object>();
        payFactParams.put("ReturnAsHashMap", "TRUE");
        payFactParams.put("CONTRNODEID", qRes.get("CONTRNODEID"));
        payFactParams.put("AMCURRENCYID", qRes.get("PREMCURRENCYID"));
        payFactParams.put("AMVALUE", qRes.get("PREMVALUE"));
        if (qRes.get("PREMCURRENCYID") != null) {
            logger.debug("sbol premcurrencyid = " + qRes.get("PREMCURRENCYID").toString());
            if ("3".equals(qRes.get("PREMCURRENCYID").toString()) || "2".equals(qRes.get("PREMCURRENCYID").toString())) {
                if (qRes.get("CURRENCYRATE") != null) {
                    BigDecimal curRate = BigDecimal.valueOf(Double.valueOf(qRes.get("CURRENCYRATE").toString()));
                    if (qRes.get("PREMVALUE") != null) {
                        BigDecimal amvalue = BigDecimal.valueOf(Double.valueOf(qRes.get("PREMVALUE").toString()));
                        BigDecimal amValueRub = amvalue.multiply(curRate).setScale(2, RoundingMode.HALF_UP);
                        payFactParams.put("AMVALUERUB", amValueRub.doubleValue());
                    }
                }
                payFactParams.put("AMVALUE", qRes.get("PREMVALUE"));
            } else {
                if (qRes.get("CURRENCYRATE") != null) {
                    BigDecimal curRate = BigDecimal.valueOf(Double.valueOf(qRes.get("CURRENCYRATE").toString()));
                    if (qRes.get("PREMVALUE") != null) {
                        BigDecimal amvalueRub = BigDecimal.valueOf(Double.valueOf(qRes.get("PREMVALUE").toString()));
                        BigDecimal amValue = amvalueRub.divide(curRate, 2, RoundingMode.HALF_UP);
                        payFactParams.put("AMVALUE", amValue.doubleValue());
                    }
                }
                payFactParams.put("AMVALUERUB", qRes.get("PREMVALUE"));
            }
        }
        payFactParams.put("NAME", qRes.get("CONTRNUMBER"));
        payFactParams.put("NAME", "СБОЛ");
        payFactParams.put("PAYFACTNUMBER", params.get("number"));
        payFactParams.put("PAYFACTTYPE", 2);
        payFactParams.put("PAYFACTDATE", params.get("date"));
        XMLUtil.convertDateToFloat(payFactParams);
        Map<String, Object> payFactRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactCreate", payFactParams, login, password);
        if ((payFactRes == null) || (payFactRes.get("PAYFACTID") == null)) {
            result.put("Error", "Не удалось создать платеж.");
        }
        logger.debug("end!!! dsB2BContractSBOLSpecify");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractSBOLModify(Map<String, Object> params) throws Exception {
        logger.debug("begin!!! dsB2BContractSBOLModify");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Long objectId = Long.valueOf(params.get("objectID").toString());
        Map<String, Object> newContractMap = (Map<String, Object>) params.get("contract");
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> browseParams = new HashMap<String, Object>();
        browseParams.put("ReturnAsHashMap", "TRUE");
        browseParams.put("CONTRID", objectId);
        Map<String, Object> updContract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", browseParams, login, password);
        if ((updContract != null) && (updContract.get("CONTRID") != null)) {
            updContract.put("ROWSTATUS", 2L);
            updContract.put("INSREGIONCODE", newContractMap.get("insRegion"));
            Map<String, Object> insurerMap = (Map<String, Object>) updContract.get("INSURERMAP");
            if (insurerMap != null) {
                Map<String, Object> newInsurerMap = (Map<String, Object>) newContractMap.get("insurer");
                insurerMap.put("LASTNAME", newInsurerMap.get("surname"));
                insurerMap.put("FIRSTNAME", newInsurerMap.get("name"));
                insurerMap.put("MIDDLENAME", newInsurerMap.get("patronymic"));
                insurerMap.put("BIRTHDATE", parseAnyDate(newInsurerMap.get("dateOfBirth"), Date.class, "dateOfBirth"));
                insurerMap.put("ROWSTATUS", 2L);
            }
            List<Map<String, Object>> memberList = (List<Map<String, Object>>) updContract.get("MEMBERLIST");
            if ((memberList != null) && (memberList.size() > 0)) {
                Map<String, Object> memberMap = memberList.get(0);
                Map<String, Object> newMemberMap = (Map<String, Object>) newContractMap.get("member");
                memberMap.put("SURNAME", newMemberMap.get("surname"));
                memberMap.put("NAME", newMemberMap.get("name"));
                memberMap.put("MIDDLENAME", newMemberMap.get("patronymic"));
                memberMap.put("BIRTHDATE", parseAnyDate(newMemberMap.get("dateOfBirth"), Date.class, "dateOfBirth"));
                memberMap.put("ROWSTATUS", 2L);
            }
            updContract.put("DISABLE_VALIDATION", 1L);
            // проверка параметров договора СБОЛ
            Map<String, Object> prodParams = new HashMap<String, Object>();
            prodParams.put(RETURN_AS_HASH_MAP, "TRUE");
            prodParams.put("SYSNAME", updContract.get("PRODSYSNAME"));
            Map<String, Object> prodRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductBrowseListByParam", prodParams, login, password);
            String checkRes = checkB2BContractSBOLParams(prodRes.get("EXTERNALCODE").toString(), updContract);
            if ((checkRes != null) && (!checkRes.isEmpty())) {
                Long requestId = Long.valueOf(params.get("REQUESTID").toString());
                updateRequest(requestId, 1, login, password);
                result.put("Error", checkRes);
                logger.debug("end!!! dsB2BContractSBOLModify");
                return result;
            }
            XMLUtil.convertDateToFloat(updContract);
            Map<String, Object> updParams = new HashMap<String, Object>();
            updParams.put("CONTRMAP", updContract);
            this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrSave", updParams, login, password);
        } else {
            result.put("Error", "Не удалось загрузить договор.");
        }
        logger.debug("end!!! dsB2BContractSBOLModify");
        return result;
    }

}
