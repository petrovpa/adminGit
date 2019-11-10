/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import com.bivgroup.services.bivsberposws.system.Constants;
import com.bivgroup.services.bivsberposws.system.SmsSender;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;
import ru.diasoft.utils.date.DSDateUtil;

/**
 *
 * @author 1
 */
@BOName("AngularContractCustomBase")
public class AngularContractCustomBaseFacade extends BaseFacade {

    protected static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    protected static final String CRMWS_SERVICE_NAME = Constants.CRMWS;
    protected static final String PROJECT_PARAM_NAME = "project";
    protected static final String ADMINWS_SERVICE_NAME = Constants.ADMINWS;
    protected static final String INSPRODUCTWS_SERVICE_NAME = Constants.INSPRODUCTWS;
    protected static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;
    protected static final String SIGNBIVSBERPOSWS_SERVICE_NAME = Constants.SIGNBIVSBERPOSWS;
    protected static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    protected static final String INSPRODUCTWSWS_SERVICE_NAME = Constants.INSPRODUCTWS;
    protected static final String BIVPOSWS_SERVICE_NAME = Constants.BIVPOSWS;
    protected static final String WEBSMSWS_SERVICE_NAME = Constants.SIGNWEBSMSWS;
    protected static final String REFWS_SERVICE_NAME = Constants.REFWS;
    public static final String SERVICE_NAME = "bivsberposws";
    protected static final String COREWS_SERVICE_NAME = Constants.COREWS;
    protected static final String B2BPOSWS_SERVICE_NAME = "b2bposws"; // todo: заменить на импорт
    protected static final String SIGNB2BPOSWS_SERVICE_NAME = "signb2bposws"; // todo: заменить на импорт

    public static final String ORDERID_PARAM_NAME = "ORDERID";
    public static final String ORDERNUMBER_PARAM_NAME = "ORDERNUMBER";

    protected static final String USEB2B_PARAM_NAME = "USEB2B";

    protected final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    protected final SimpleDateFormat dateFormatterNoDots = new SimpleDateFormat("ddMMyyyy");

    protected static final int PRODCONFID_HIB = 1050;
    protected static final int PRODCONFID_PHIB = 2100;
    protected static final int PRODCONFID_CIB = 1060;
    protected static final int PRODCONFID_CIBY = 6112;
    protected static final int PRODCONFID_VZR = 1070;
    protected static final int PRODCONFID_SIS = 1080;
    protected static final int PRODCONFID_MORTGAGE = 1090;
    protected static final int PRODCONFID_MULTI = 4004;
    //protected static final int PRODCONFID_MORTGAGE = 1090;

    public static final String[][] hibRequiredFields = {
        {"insSurname", "Фамилия"},
        {"insName", "Имя"},
        {"insBirthdate", "Дата рождения"},
        {"insPhone", "Телефон"},
        {"insEmail", "Электронная почта"},
        {"insEmailValid", "Повтор электронной почты"},
        {"insPassDocType", "Вид документа"},
        {"insPassSeries", "Серия паспорта"},
        {"insPassNumber", "Номер паспорта"},
        {"insPassIssueDate", "Дата выдачи"},
        {"insPassIssuePlace", "Выдан"},
        {"insAdrRegNAME", "Регион"},
        {"insAdrRegCODE", "Код региона"},
        {"insAdrCityNAME", "Населенный пункт"},
        {"insAdrCityCODE", "Код населенного пункта"},
        //        {"insAdrStrNAME", "Улица"},
        //        {"insAdrStrCODE", "Код улицы"},
        {"insAdrHouse", "Дом"},
        //        {"insAdrStrPOSTALCODE", "Индекс"},
        {"objAdrRegNAME", "Регион"},
        {"objAdrRegCODE", "Код региона"},
        {"objAdrCityNAME", "Населенный пункт"},
        {"objAdrCityCODE", "Код населенного пункта"},
        //        {"objAdrStrNAME", "Улица"},
        //        {"objAdrStrCODE", "Код улицы"},
        {"objAdrHouse", "Дом"}/*,
     {"objAdrStrPOSTALCODE", "Индекс"}*/

    };
    public static final String[][] cibRequiredFields = {
        {"insSurname", "Фамилия"},
        {"insName", "Имя"},
        {"insBirthdate", "Дата рождения"},
        {"insPhone", "Телефон"},
        {"insEmail", "Электронная почта"},
        {"insEmailValid", "Повтор электронной почты"},
        {"insPassDocType", "Вид документа"},
        {"insPassSeries", "Серия паспорта"},
        {"insPassNumber", "Номер паспорта"},
        {"insPassIssueDate", "Дата выдачи"},
        {"insPassIssuePlace", "Выдан"},
        {"insAdrRegNAME", "Регион"},
        {"insAdrRegCODE", "Код региона"},
        {"insAdrCityNAME", "Населенный пункт"},
        {"insAdrCityCODE", "Код населенного пункта"},
        //       {"insAdrStrNAME", "Улица"},
        //       {"insAdrStrCODE", "Код улицы"},
        {"insAdrHouse", "Дом"}/*,
     {"insAdrStrPOSTALCODE", "Индекс"}*/

    };
    public static final String[][] vzrRequiredFields = {
        {"startDate.date-dateStr", "Дата начала"},
        {"finishDate.date-dateStr", "Дата окончания"},
        {"duration", "Полис действует"},
        {"countries.SYSNAME", "Регион действия"},
        {"insurer.surname", "Фамилия страхователя"},
        {"insurer.name", "Имя страхователя"},
        {"insurer.birthDate.date-dateStr", "Дата рождения страхователя"},
        {"series", "Серия паспорта"},
        {"number", "Номер паспорта"},
        {"issueDate.date-dateStr", "Дата выдачи "},
        {"issuePlace", "Место выдачи"},
        {"phone", "Телефон"},
        {"email", "Электронная почта"},
        {"emailValid", "Повтор электронной почты"},
        {"insuredCount70", "кол-во застрахованных 61-70"},
        {"insuredCount60", "кол-во застрахованных 3-60"},
        {"insuredCount2", "кол-во застрахованных 0-2"},
        {"currency", "Валюта договора"},
        {"insuredList.name", "Имя застрахованного"},
        {"insuredList.surname", "Фамилия застрахованного"},
        {"insuredList.birthDate.date-dateStr", "Дата рождения застрахованного"}
    };
    public static final String[][] sisRequiredFields = {
        {"insurer.surname", "Фамилия страхователя"},
        {"insurer.name", "Имя страхователя"},
        //{"insurer.birthDate.date-dateStr", "Дата рождения страхователя"},
        {"series", "Серия паспорта"},
        {"number", "Номер паспорта"},
        //{"issueDate.date-dateStr", "Дата выдачи "},
        {"issuePlace", "Место выдачи"},
        {"phone", "Телефон"},
        {"email", "Электронная почта"}
    };

    // перечень обязательных полей для проверки при работе со страхованием ипотеки
    public static final String[][] mortgageRequiredFields = {
        {"startDate.date", "Дата начала действия полиса"},
        {"finishDate.date", "Дата окончания действия полиса"},
        {"insuredSum", "Страховая сумма"},
        {"insurerContrNum", "Номер кредитного договора"},
        {"contrDate.dateStr", "Дата начала действия кредитного договора"},
        {"insurer.surname", "Фамилия страхователя"},
        {"insurer.name", "Имя страхователя"},
        {"insurer.birthDateStr", "Дата рождения страхователя"},
        {"insurer.passport.series", "Серия документа"},
        {"insurer.passport.number", "Номер документа"},
        {"insurer.passport.issueDateStr", "Дата выдачи документа"},
        {"insurer.passport.issuePlace", "Место выдачи документа"},
        {"insurer.contacts.phone", "Телефон"},
        {"insurer.contacts.email", "Электронная почта"},
        {"insurer.address.region", "Регион"},
        {"insurer.address.city", "Город/Населенный пункт"},
        //    {"insurer.address.street", "Улица"},
        {"insurer.address.house", "Дом"}, // {"", ""},
    };

    private String smsText = "";
    private String smsUser = "";
    private String smsPwd = "";
    private String smsFrom = "";

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AngularContractCustomBaseFacade.class);

    // системные имена состояний договора в B2B
    /** Системное наименование состояния 'Черновик' для договора */
    protected static final String B2B_CONTRACT_DRAFT = "B2B_CONTRACT_DRAFT";
    /** Системное наименование состояния 'Предварительная печать (Образец)' для договора */
    protected static final String B2B_CONTRACT_PREPRINTING = "B2B_CONTRACT_PREPRINTING";
    /** Системное наименование состояния 'Подписан' для договора */
    protected static final String B2B_CONTRACT_SG = "B2B_CONTRACT_SG";
    /** Системное наименование состояния 'Выгружен успешно' для договора */
    protected static final String B2B_CONTRACT_UPLOADED_SUCCESFULLY = "B2B_CONTRACT_UPLOADED_SUCCESFULLY";
    // todo: системные имена для остальных состояний договора (по мере необходимости)

    /** Перечень состояний для которых доступна пролонгация.
     * Строка вида "'B2B_CONTRACT_SG','B2B_CONTRACT_UPLOADED_SUCCESFULLY'" для SQL-запросов с ограничением по перечню состояний.
     * Инициализируется - в init(). */
    protected static String B2B_CONTRACT_PROLONGABLE_STATE_LIST_STR;

    public AngularContractCustomBaseFacade() {
        super();
        init();
    }

    private void init() {
        logger.debug("AngularContractCustomBaseFacade init...");
        B2B_CONTRACT_PROLONGABLE_STATE_LIST_STR = genStringListForSQLQuery(B2B_CONTRACT_SG, B2B_CONTRACT_UPLOADED_SUCCESFULLY);
        logger.debug(String.format("B2B_CONTRACT_PROLONGABLE_STATE_LIST_STR = %s", B2B_CONTRACT_PROLONGABLE_STATE_LIST_STR));
        logger.debug("AngularContractCustomBaseFacade init finished.");
    }
    private boolean isContractNotPaid(Long contrId, String login, String password) throws Exception {
        Map<String, Object> checkPayParam = new HashMap<String, Object>();
        checkPayParam.put("CONTRID", contrId);
        Map<String, Object> checkPayRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsCallCheckPaymentServiceById", checkPayParam, login, password);
        Map<String, Object> res1 = null;
        if (checkPayRes.get(RESULT) != null) {
            res1 = (Map<String, Object>) checkPayRes.get(RESULT);
        } else {
            res1 = checkPayRes;
        }
        logger.debug(res1.toString());
        if ((res1.get("ORDERID") != null)
                && (res1.get("REFERENCENUMBER") != null)
                && (res1.get("ORDERSTATUS") != null)
                && ("2".equals(res1.get("ORDERSTATUS").toString()))) {
            return false;
        }
        return true;
    }

    // направление для преобразования данных (старые структуры / новые, B2B)
    public static enum Direction {

        TO_SAVE("Сохранение в B2B: старая структура > новая структура", 0, 1),
        TO_LOAD("Загрузка из B2B: новая структура > старая структура", 1, 0);

        public final int fromIndex;
        public final int toIndex;
        public final String name;

        private Direction(String name, int fromIndex, int toIndex) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    // соответствие старых идентификаторов продуктов новым в B2B
    // done: генерировать, т.к. идентификаторы в B2B могут быть не фиксированные
    //public static final String prodConfIDConvertRules = "1050 > 2011; 1060 > 2012; 1070 > 2013";
    // доступ - только через getProdConfIDConvertRules()
    private static String prodConfIDConvertRules = "";

    // соответствие старых идентификаторов версий продуктов новым в B2B
    // done: генерировать, т.к. идентификаторы в B2B могут быть не фиксированные
    //public static String prodVerIDConvertRules = "1050 > 2014; 1060 > 2015; 1070 > 2016";
    // доступ - только через getProdVerIDConvertRules()
    private static String prodVerIDConvertRules = "";

    // перечень развертываемых списков в карты вида 'имяСписка.системноеИмяЭлементаСписка' для преобразования сведений при сохранении договора в B2B
    // (имя списка - имя атрибута, хранящего системное имя элемента)
    // (при загрузке сведений будет выполнена обратная операция - сворачивание карт в списки)
    public static final String[][] contractExpandedLists = {
        {"PRODCONF.PRODVER.PRODSTRUCTS", "SYSNAME"}, // элементы структуры объекта
        {"PRODCONF.PRODVER.PRODPROGS", "SYSNAME"}, // программы страхования
        //{"partRegDocList", "REGDOCTYPESYSNAME"}, // регистрационные документы
        //{"documentList", "DOCTYPESYSNAME"}, // личные документы
        //{"contactList", "CONTACTTYPESYSNAME"}, // контакты
        {"RISKLIST", "PRODRISKSYSNAME"}, // Страхование путешественников Онлайн - список рисков с суммами по рискам
        {"INSUREDLIST", "ALTNAME"}, // Страхование путешественников Онлайн - список застрахованных

        // 1080 - Защита имущества сотрудников сбербанка Онлайн
        {"CONTROBJLIST", "OBJTYPESYSNAME"}, // список объектов
        {"CONTROBJLIST.flat.RISKLIST", "PRODRISKSYSNAME"}, // список рисков
        {"CONTROBJLIST.house.RISKLIST", "PRODRISKSYSNAME"}, // список рисков
        {"CONTROBJLIST.house2.RISKLIST", "PRODRISKSYSNAME"}, // список рисков
        {"CONTROBJLIST.sauna.RISKLIST", "PRODRISKSYSNAME"}, // список рисков
    // иных объектов для продукта 'Защита имущества сотрудников сбербанка Онлайн' может быть разное количество - их обработка в genAdditionalSisSumSaveParams
    //{"CONTROBJLIST.other.RISKLIST", "PRODRISKSYSNAME"}, // список рисков
    //{"riskLimitsList", "PRODRISKSYSNAME"}, // Страхование путешественников Онлайн - лимиты по элементарным рискам
    };

    // перечень сворачиваемых в списки карт вида 'имяСписка_системноеИмяЭлементаСписка' для преобразования сведений при сохранении договора в B2B
    // (имя списка - имя атрибута, хранящего системное имя элемента)
    // (при загрузке сведений будет выполнена обратная операция - развертывание списков в карты)
    public static final String[][] contractCollapsedMaps = {
        {"PRODCONF.PRODVER.PRODSTRUCTS", "SYSNAME"}, // элементы структуры объекта
        {"PRODCONF.PRODVER.PRODPROGS", "SYSNAME"}, // программы страхования
        {"INSURERMAP.addressList", "ADDRESSTYPESYSNAME"}, // адреса
        {"INSURERMAP.partRegDocList", "REGDOCTYPESYSNAME"}, // регистрационные документы
        {"INSURERMAP.documentList", "DOCTYPESYSNAME"}, // личные документы
        {"INSURERMAP.contactList", "CONTACTTYPESYSNAME"}, // контакты
        // риски - Защита банковской карты Онлайн
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        // риски - Ипотека Онлайн
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        // риски - Защита дома Онлайн
        {"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        // риски - Страхование путешественников Онлайн
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRvzrObject.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRgoObject.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRaccidentObject.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRlootObject.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRsporttoolsObject.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        // риски - Защита имущества сотрудников сбербанка Онлайн
        {"INSOBJGROUPLIST.flat.OBJLIST.constructive.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.flat.OBJLIST.interiorFinish.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.flat.OBJLIST.flatMovableProperty.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.flat.OBJLIST.flatGo.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.mainHouse.OBJLIST.mainHouseConstructInterior.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.secondHouse.OBJLIST.secondHouseConstructInterior.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.go.OBJLIST.goObject.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.movableProperty.OBJLIST.movablePropertyObject.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.banya.OBJLIST.banyaConstructInterior.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        // иных объектов для продукта 'Защита имущества сотрудников сбербанка Онлайн' может быть разное количество - их обработка в genAdditionalSisSumSaveParams
        //{"INSOBJGROUPLIST.other.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.other1.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.other2.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.other3.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.other4.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.other5.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST", "SYSNAME"},
        // объекты - Защита имущества сотрудников сбербанка Онлайн
        {"INSOBJGROUPLIST.flat.OBJLIST", "SYSNAME"}, //
        {"INSOBJGROUPLIST.mainHouse.OBJLIST", "SYSNAME"}, //
        {"INSOBJGROUPLIST.secondHouse.OBJLIST", "SYSNAME"}, //
        {"INSOBJGROUPLIST.go.OBJLIST", "SYSNAME"}, //
        {"INSOBJGROUPLIST.movableProperty.OBJLIST", "SYSNAME"}, //
        {"INSOBJGROUPLIST.banya.OBJLIST", "SYSNAME"}, //
        // иных объектов для продукта 'Защита имущества сотрудников сбербанка Онлайн' может быть разное количество - их обработка в genAdditionalSisSumSaveParams
        //{"INSOBJGROUPLIST.other.OBJLIST", "SYSNAME"},
        {"INSOBJGROUPLIST.other1.OBJLIST", "SYNTHETICSYSNAME"},
        {"INSOBJGROUPLIST.other2.OBJLIST", "SYNTHETICSYSNAME"},
        {"INSOBJGROUPLIST.other3.OBJLIST", "SYNTHETICSYSNAME"},
        {"INSOBJGROUPLIST.other4.OBJLIST", "SYNTHETICSYSNAME"},
        {"INSOBJGROUPLIST.other5.OBJLIST", "SYNTHETICSYSNAME"},
        // объекты - Защита банковской карты Онлайн, Защита дома Онлайн, Ипотека Онлайн
        {"INSOBJGROUPLIST.00000.OBJLIST", "SYSNAME"}, //
        // объекты - Страхование путешественников Онлайн
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST", "SYSNAME"},
        // типы - Защита банковской карты Онлайн, Защита дома Онлайн, Ипотека Онлайн, Страхование путешественников Онлайн, Защита имущества сотрудников сбербанка Онлайн
        {"INSOBJGROUPLIST", "SYSNAME"},
        //
        {"MEMBERLIST", "ALTNAME"}, // Страхование путешественников Онлайн - список застрахованных
    };

    // объединенный перечень развертываемых списков и сворачиваемых в списки карт (для выбора по направлению преобразования)
    public static final String[][][] contractListMapConversions = {contractExpandedLists, contractCollapsedMaps};

    // список наследуемых и дублирующихся значений, используется для заполнения дубликатов полей при сохранении договора 
    // (ключ основного значения, ключ значения повторяющего основное)
    public static final String[][] contractDuplicatedKeys = {
        //<editor-fold defaultstate="collapsed" desc="Cписок наследуемых и дублирующихся значений, используется для заполнения дубликатов полей при сохранении договора ">
        // Защита банковской карты Онлайн
        // 1.1 и 1.1.1
        {"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.STARTDATE"},
        {"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.STARTDATE"},
        {"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.FINISHDATE"},
        {"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.FINISHDATE"},
        {"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.DURATION"},
        {"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.DURATION"},
        {"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.INSAMCURRENCYID"},
        {"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.INSAMCURRENCYID"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.INSAMCURRENCYID",
            "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.INSAMCURRENCYID",
            "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
        {"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.PREMCURRENCYID"},
        {"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.PREMCURRENCYID"},
        // Защита дома Онлайн
        // 1.1 и 1.1.1
        {"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.STARTDATE"},
        {"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.STARTDATE"},
        {"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.FINISHDATE"},
        {"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.FINISHDATE"},
        {"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.DURATION"},
        {"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.DURATION"},
        {"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.INSAMCURRENCYID"},
        {"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.INSAMCURRENCYID"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.INSAMCURRENCYID",
            "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
        {"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.INSAMCURRENCYID",
            "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
        {"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.PREMCURRENCYID"},
        {"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.PREMCURRENCYID"},
        // 1.2 и 1.2.1
        {"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.STARTDATE"},
        {"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.STARTDATE"},
        {"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.FINISHDATE"},
        {"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.FINISHDATE"},
        {"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.DURATION"},
        {"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.DURATION"},
        {"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.INSAMCURRENCYID"},
        {"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.INSAMCURRENCYID"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.INSAMCURRENCYID",
            "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
        {"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.INSAMCURRENCYID",
            "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
        {"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.PREMCURRENCYID"},
        {"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.PREMCURRENCYID"},
        // 1.3 и 1.3.1
        {"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.STARTDATE"},
        {"STARTDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.STARTDATE"},
        {"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.FINISHDATE"},
        {"FINISHDATE", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.FINISHDATE"},
        {"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.DURATION"},
        {"DURATION", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.DURATION"},
        {"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.INSAMCURRENCYID"},
        {"INSAMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.INSAMCURRENCYID"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.INSAMCURRENCYID",
            "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
        {"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.INSAMCURRENCYID",
            "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.CURRENCYID"}, // по сведениям из документа с картой договора CURRENCYID = INSAMCURRENCYID
        {"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.PREMCURRENCYID"},
        {"PREMCURRENCYID", "INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.PREMCURRENCYID"}, //</editor-fold>
    };

    // основные списки соответствий ключей
    // (аналогичны commonKeysRelations, но для конкретного продукта)
    public static final String[][] hibKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="Защита дома Онлайн">
        // Защита дома Онлайн - структура тип/объект/риск
        // {"INSOBJGROUPLIST.00000", "PRODCONF.PRODVER.PRODSTRUCTS.00000"}, // целиком копировать этот узел нельзя - будут потеряны уже добавленные значения вида INSOBJGROUPLIST.00000.*
        {"INSOBJGROUPLIST.00000.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.00000.SYSNAME"},
        {"INSOBJGROUPLIST.00000.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.00000.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.HBDATAVERID"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000002.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.000000002"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119", "PRODCONF.PRODVER.PRODSTRUCTS.100000119"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000004.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.000000004"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119", "PRODCONF.PRODVER.PRODSTRUCTS.000000119"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000012.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.000000012"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116", "PRODCONF.PRODVER.PRODSTRUCTS.000000116"},
        // Защита дома Онлайн - свойства объектов
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.PREMVALUE", "insPremVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.PREMVALUE", "insPremVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.PREMVALUE", "insPremVal"},
        // Защита дома Онлайн - свойства рисков
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000002.CONTROBJMAP.CONTRRISKLIST.100000119.PREMVALUE", "insPremVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000004.CONTROBJMAP.CONTRRISKLIST.000000119.PREMVALUE", "insPremVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.INSAMVALUE", "insAmVal"},
        //{"INSOBJGROUPLIST.00000.OBJLIST.000000012.CONTROBJMAP.CONTRRISKLIST.000000116.PREMVALUE", "insPremVal"},
        // Защита дома Онлайн - тип страхуемого имущества (показатель договора)
        {"CONTREXTMAP.insObject", "objTypeId", "0", "1 > 0; 2 > 1"}, // 1 - дом, 2 - квартира; преобразование по сведениям из документа "Выгрузка договоров (карта, дом, взр).docx"
        // Защита дома Онлайн - строковый адрес страхуемого имущества (показатель договора)
        {"CONTREXTMAP.propertyAddress", "objAdrAddressText"},
        // Защита дома Онлайн - адрес страхуемого имущества (показатель типа объекта)
        {"INSOBJGROUPLIST.00000.PROPERTYSYSNAME", "objTypeId", "", "1 > house; 2 > flat"}, // 1 - дом, 2 - квартира
        {"INSOBJGROUPLIST.00000.ADDRESSTEXT1", "objAdrAddressText"},
        {"INSOBJGROUPLIST.00000.BUILDING", "objAdrBuilding"},
        {"INSOBJGROUPLIST.00000.FLAT", "objAdrFlat"},
        {"INSOBJGROUPLIST.00000.HOUSE", "objAdrHouse"},
        {"INSOBJGROUPLIST.00000.HOUSING", "objAdrHousing"},
        {"INSOBJGROUPLIST.00000.CITY", "objAdrCityNAME"},
        {"INSOBJGROUPLIST.00000.CITYKLADR", "objAdrCityCODE"},
        {"INSOBJGROUPLIST.00000.REGION", "objAdrRegNAME"},
        {"INSOBJGROUPLIST.00000.REGIONKLADR", "objAdrRegCODE"},
        {"INSOBJGROUPLIST.00000.STREET", "objAdrStrNAME"},
        {"INSOBJGROUPLIST.00000.STREETKLADR", "objAdrStrCODE"},
        {"INSOBJGROUPLIST.00000.POSTALCODE", "objAdrStrPOSTALCODE"}, //</editor-fold>
    };
    public static final String[][] cibKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="Защита банковской карты Онлайн">
        // Защита банковской карты Онлайн - структура тип/объект/риск
        // {"INSOBJGROUPLIST.00000", "PRODCONF.PRODVER.PRODSTRUCTS.00000"}, // целиком копировать этот узел нельзя - будут потеряны уже добавленные значения вида INSOBJGROUPLIST.00000.*
        {"INSOBJGROUPLIST.00000.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.00000.SYSNAME"},
        {"INSOBJGROUPLIST.00000.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.00000.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.HBDATAVERID"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.000000014"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124", "PRODCONF.PRODVER.PRODSTRUCTS.000000124"},
        // Защита банковской карты Онлайн - свойства объекта
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.INSAMVALUE", "insAmVal"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.INSAMVALUE", "INSAMVALUE"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.PREMVALUE", "insPremVal"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.PREMVALUE", "PREMVALUE"},
        // Защита банковской карты Онлайн - свойства риска
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.INSAMVALUE", "insAmVal"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.INSAMVALUE", "INSAMVALUE"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.PREMVALUE", "insPremVal"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000014.CONTROBJMAP.CONTRRISKLIST.000000124.PREMVALUE", "PREMVALUE"}, //</editor-fold>
    };
    public static final String[][] mortgageKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России»">
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - расширенные атрибуты договора
        {"CONTREXTMAP.creditStartDATE", "master.contrDate.dateStr"},
        {"CONTREXTMAP.creditNumber", "master.insurerContrNum"},
        {"CONTREXTMAP.buildYear", "master.insObj.buildYear", "0", "before1955 > 0; after1955 > 1"},
        {"CONTREXTMAP.propertyAddress", "master.insObj.addressText"},
        {"CONTREXTMAP.woodInCeilings", "master.insObj.woodInWal", "0", "1 > 0; 0 > 1"},
        {"CONTREXTMAP.insObject", "master.insObj.typeSysName", "0", "house > 0; flat > 1"},
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - структура тип/объект/риск
        // {"INSOBJGROUPLIST.00000", "PRODCONF.PRODVER.PRODSTRUCTS.00000"}, // целиком копировать этот узел нельзя - будут потеряны уже добавленные значения вида INSOBJGROUPLIST.00000.*
        {"INSOBJGROUPLIST.00000.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.00000.SYSNAME"},
        {"INSOBJGROUPLIST.00000.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.00000.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.HBDATAVERID"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.000000001"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125", "PRODCONF.PRODVER.PRODSTRUCTS.000000125"},
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - свойства объекта
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CURRENCYID", ""}, // todo: генерировать при отсутствии
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.DURATION", "DURATION"}, // todo: генерировать при отсутствии
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.FINISHDATE", "master.finishDate.dateStr"}, // todo: генерировать при отсутствии
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.INSAMCURRENCYID", ""}, // todo: генерировать при отсутствии
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.INSAMVALUE", "master.insuredSum"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.PAYPREMVALUE", ""},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.PREMCURRENCYID", ""}, // todo: генерировать при отсутствии
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.PREMVALUE", "master.contrPremium"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.STARTDATE", "master.startDate.dateStr"}, // todo: генерировать при отсутствии
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - свойства риска
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.CURRENCYID", ""}, // todo: генерировать при отсутствии
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.DURATION", "DURATION"}, // todo: генерировать при отсутствии
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.FINISHDATE", "master.finishDate.dateStr"}, // todo: генерировать при отсутствии
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.INSAMCURRENCYID", ""}, // todo: генерировать при отсутствии
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.INSAMVALUE", "master.insuredSum"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.PAYPREMVALUE", ""},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.PREMCURRENCYID", ""}, // todo: генерировать при отсутствии
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.PREMVALUE", "master.contrPremium"},
        {"INSOBJGROUPLIST.00000.OBJLIST.000000001.CONTROBJMAP.CONTRRISKLIST.000000125.STARTDATE", "master.startDate.dateStr"}, // todo: генерировать при отсутствии
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - показатели по группе объектов
        {"INSOBJGROUPLIST.00000.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.HBDATAVERID"},
        {"INSOBJGROUPLIST.00000.PROPERTYSYSNAME", "master.insObj.typeSysName"},
        {"INSOBJGROUPLIST.00000.ADDRESSTEXT1", "master.insObj.address.addressText"},
        {"INSOBJGROUPLIST.00000.BUILDING", "master.insObj.address.building"},
        {"INSOBJGROUPLIST.00000.FLAT", "master.insObj.address.flat"},
        {"INSOBJGROUPLIST.00000.HOUSE", "master.insObj.address.house"},
        {"INSOBJGROUPLIST.00000.HOUSING", "master.insObj.address.housing"},
        {"INSOBJGROUPLIST.00000.CITY", "master.insObj.address.city.NAME"},
        {"INSOBJGROUPLIST.00000.CITYKLADR", "master.insObj.address.city.CODE"},
        {"INSOBJGROUPLIST.00000.REGION", "master.insObj.address.region.NAME"},
        {"INSOBJGROUPLIST.00000.REGIONKLADR", "master.insObj.address.region.CODE"},
        {"INSOBJGROUPLIST.00000.STREET", "master.insObj.address.street.NAME"},
        {"INSOBJGROUPLIST.00000.STREETKLADR", "master.insObj.address.street.CODE"},
        {"INSOBJGROUPLIST.00000.POSTALCODE", "master.insObj.address.street.POSTALCODE"},
        // for report printing
        {"INSURERMAP.contactList.MobilePhone.VALUE", "INSPHONE"},
        {"INSURERMAP.contactList.PersonalEmail.VALUE", "INSEMAIL"},
        {"INSURERMAP.documentList.PassportRF.DOCSERIES", "INSDOCSERIES"},
        {"INSURERMAP.documentList.PassportRF.DOCNUMBER", "INSDOCNUMBER"},
        {"INSURERMAP.documentList.PassportRF.ISSUEDBY", "INSISSUEDBY"},
        {"INSURERMAP.documentList.ForeignPassport.DOCSERIES", "INSDOCSERIES"},
        {"INSURERMAP.documentList.ForeignPassport.DOCNUMBER", "INSDOCNUMBER"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUEDBY", "INSISSUEDBY"},
        {"CONTREXTMAP.creditNumber", "CREDCONTRNUM"},
        {"CONTREXTMAP.creditStartDATE", "credDate"},
        {"INSOBJGROUPLIST.00000.ADDRESSTEXT1", "INSOBJADDRESSTEXT1"},
        {"INSOBJGROUPLIST.00000.PROPERTYSYSNAME", "INSOBJTYPESYSNAME"}, //</editor-fold>
    };

    // правило преобразования значений для типа внутренней отделки (старый договор > новый b2b-договор)
    // (по ФТ для разных построек могут быть разные числовые значения для однинаковых типов отделки)
    public static final String sisFlatFacingTypeConvertRules = "standart > 0; eurostd > 1; exclusive > 2";
    public static final String sisMainHouseFacingTypeConvertRules = "base > 0; standart > 1; eurostd > 2; exclusive > 3";
    public static final String sisSecondHouseFacingTypeConvertRules = "base > 0; standart > 1; eurostd > 2; exclusive > 3";
    public static final String sisBanyaFacingTypeConvertRules = "base > 0; standart > 1; eurostd > 2; exclusive > 3"; // 'Эксклюзив' отсутствует в ФТ, но доступен для бани в angular-интерфейсе
    public static final String sisOtherFacingTypeConvertRules = "base > 0; standart > 1; eurostd > 2";

    public static final String[][] sisKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="Защита имущества сотрудников сбербанка Онлайн">

        {"CONTREXTMAP.insObject", "CONTROBJLIST.house.OBJTYPESYSNAME", "", "house > 0"}, // Застрахованный объект
        {"CONTREXTMAP.insObject", "CONTROBJLIST.flat.OBJTYPESYSNAME", "", "flat > 1"}, // Застрахованный объект
        {"CONTREXTMAP.paymentWithoutSublimits", "protectTypeList", "0", ",noSubLimits > 1; ,dopRisks > 0; ,noSubLimits,dopRisks > 1; ,dopRisks,noSubLimits > 1"}, // Выплата без подлимитов
        {"CONTREXTMAP.addRisks", "protectTypeList", "0", ",noSubLimits > 0; ,dopRisks > 1; ,noSubLimits,dopRisks > 1; ,dopRisks,noSubLimits > 1"}, // Дополнительные риски
        {"CONTREXTMAP.propertyAddressSt", "CONTROBJLIST.flat.ADDRESSTEXT1"}, // Адрес имущества
        {"CONTREXTMAP.propertyAddressSt", "CONTROBJLIST.house.ADDRESSTEXT1"}, // Адрес имущества
        //{"CONTREXTMAP.", ""}, //

        // КВАРТИРА

        {"INSOBJGROUPLIST.flat.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.flat.SYSNAME"},
        {"INSOBJGROUPLIST.flat.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.flat.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.flat.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.flat.HBDATAVERID"},
        {"INSOBJGROUPLIST.flat.area", "CONTROBJLIST.flat.OBJAREA"}, // Площадь
        {"INSOBJGROUPLIST.flat.interiorDecorationType", "CONTROBJLIST.flat.FACINGTYPE", "", sisFlatFacingTypeConvertRules}, // Тип внутренней отделки
        {"INSOBJGROUPLIST.flat.productionYear", "CONTROBJLIST.flat.PRODYEARSYSNAME", "", "before1955 > 0; before1974 > 1; after1974 > 2; dontKnow > 3"}, // Год постройки
        {"INSOBJGROUPLIST.flat.woodInCeiling", "CONTROBJLIST.flat.WALMATERIAL", "", "1 > 0; 0 > 1; 2 > 2"}, // Есть дерево в перекрытиях

        {"INSOBJGROUPLIST.flat.OBJLIST.constructive.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.constructive"},
        {"INSOBJGROUPLIST.flat.OBJLIST.constructive.CONTROBJMAP.CONTRRISKLIST.constructiveRisk", "PRODCONF.PRODVER.PRODSTRUCTS.constructiveRisk"},
        {"INSOBJGROUPLIST.flat.OBJLIST.constructive.CONTROBJMAP.CONTRRISKLIST.constructiveRisk.PREMVALUE", "CONTROBJLIST.flat.RISKLIST.construct.PREMVALUE"},
        {"INSOBJGROUPLIST.flat.OBJLIST.constructive.CONTROBJMAP.CONTRRISKLIST.constructiveRisk.INSAMVALUE", "CONTROBJLIST.flat.RISKLIST.construct.INSAMVALUE"},
        {"INSOBJGROUPLIST.flat.OBJLIST.interiorFinish.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.interiorFinish"},
        {"INSOBJGROUPLIST.flat.OBJLIST.interiorFinish.CONTROBJMAP.CONTRRISKLIST.interiorFinishRisk", "PRODCONF.PRODVER.PRODSTRUCTS.interiorFinishRisk"},
        {"INSOBJGROUPLIST.flat.OBJLIST.interiorFinish.CONTROBJMAP.CONTRRISKLIST.interiorFinishRisk.PREMVALUE", "CONTROBJLIST.flat.RISKLIST.insideFacing.PREMVALUE"},
        {"INSOBJGROUPLIST.flat.OBJLIST.interiorFinish.CONTROBJMAP.CONTRRISKLIST.interiorFinishRisk.INSAMVALUE", "CONTROBJLIST.flat.RISKLIST.insideFacing.INSAMVALUE"},
        {"INSOBJGROUPLIST.flat.OBJLIST.flatMovableProperty.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.flatMovableProperty"},
        {"INSOBJGROUPLIST.flat.OBJLIST.flatMovableProperty.CONTROBJMAP.CONTRRISKLIST.flatMovablePropertyRisk", "PRODCONF.PRODVER.PRODSTRUCTS.flatMovablePropertyRisk"},
        {"INSOBJGROUPLIST.flat.OBJLIST.flatMovableProperty.CONTROBJMAP.CONTRRISKLIST.flatMovablePropertyRisk.PREMVALUE", "CONTROBJLIST.flat.RISKLIST.movables.PREMVALUE"},
        {"INSOBJGROUPLIST.flat.OBJLIST.flatMovableProperty.CONTROBJMAP.CONTRRISKLIST.flatMovablePropertyRisk.INSAMVALUE", "CONTROBJLIST.flat.RISKLIST.movables.INSAMVALUE"},
        {"INSOBJGROUPLIST.flat.OBJLIST.flatGo.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.flatGo"},
        {"INSOBJGROUPLIST.flat.OBJLIST.flatGo.CONTROBJMAP.CONTRRISKLIST.flatGoRisk", "PRODCONF.PRODVER.PRODSTRUCTS.flatGoRisk"},
        {"INSOBJGROUPLIST.flat.OBJLIST.flatGo.CONTROBJMAP.CONTRRISKLIST.flatGoRisk.PREMVALUE", "CONTROBJLIST.flat.RISKLIST.go.PREMVALUE"},
        {"INSOBJGROUPLIST.flat.OBJLIST.flatGo.CONTROBJMAP.CONTRRISKLIST.flatGoRisk.INSAMVALUE", "CONTROBJLIST.flat.RISKLIST.go.INSAMVALUE"},
        // ДОМ

        {"INSOBJGROUPLIST.mainHouse.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.mainHouse.SYSNAME"},
        {"INSOBJGROUPLIST.mainHouse.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.mainHouse.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.mainHouse.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.mainHouse.HBDATAVERID"},
        {"INSOBJGROUPLIST.mainHouse.area", "CONTROBJLIST.house.OBJAREA"}, // Площадь
        {"INSOBJGROUPLIST.mainHouse.interiorDecorationType", "CONTROBJLIST.house.FACINGTYPE", "", sisMainHouseFacingTypeConvertRules}, // Тип внутренней отделки
        {"INSOBJGROUPLIST.mainHouse.banyaSaunaFireplaceGas", "CONTROBJLIST.house.HOUSEHAS"}, // В доме есть баня/сауна/камин/газ
        {"INSOBJGROUPLIST.mainHouse.woodenHouse", "CONTROBJLIST.house.WALMATERIAL", "", "1 > 0; 3 > 1; 0 > 2; 2 > 3"}, // Дом деревянный

        {"INSOBJGROUPLIST.mainHouse.OBJLIST.mainHouseConstructInterior.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.mainHouseConstructInterior"},
        {"INSOBJGROUPLIST.mainHouse.OBJLIST.mainHouseConstructInterior.CONTROBJMAP.CONTRRISKLIST.mainHouseConstructInteriorRisk", "PRODCONF.PRODVER.PRODSTRUCTS.mainHouseConstructInteriorRisk"},
        {"INSOBJGROUPLIST.mainHouse.OBJLIST.mainHouseConstructInterior.CONTROBJMAP.CONTRRISKLIST.mainHouseConstructInteriorRisk.PREMVALUE", "CONTROBJLIST.house.RISKLIST.construct.PREMVALUE"},
        {"INSOBJGROUPLIST.mainHouse.OBJLIST.mainHouseConstructInterior.CONTROBJMAP.CONTRRISKLIST.mainHouseConstructInteriorRisk.INSAMVALUE", "CONTROBJLIST.house.RISKLIST.construct.INSAMVALUE"},
        // ВТОРОЙ ДОМ

        {"INSOBJGROUPLIST.secondHouse.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.secondHouse.SYSNAME"},
        {"INSOBJGROUPLIST.secondHouse.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.secondHouse.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.secondHouse.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.secondHouse.HBDATAVERID"},
        {"INSOBJGROUPLIST.secondHouse.area", "CONTROBJLIST.house2.OBJAREA"}, // Площадь
        {"INSOBJGROUPLIST.secondHouse.interiorDecorationType", "CONTROBJLIST.house2.FACINGTYPE", "", sisSecondHouseFacingTypeConvertRules}, // Тип внутренней отделки
        {"INSOBJGROUPLIST.secondHouse.banyaSaunaFireplaceGas", "CONTROBJLIST.house2.HOUSEHAS"}, // В доме есть баня/сауна/камин/газ
        {"INSOBJGROUPLIST.secondHouse.woodenHouse", "CONTROBJLIST.house2.WALMATERIAL", "", "1 > 0; 3 > 1; 0 > 2; 2 > 3"}, // Дом деревянный

        {"INSOBJGROUPLIST.secondHouse.OBJLIST.secondHouseConstructInterior.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.secondHouseConstructInterior"},
        {"INSOBJGROUPLIST.secondHouse.OBJLIST.secondHouseConstructInterior.CONTROBJMAP.CONTRRISKLIST.secondHouseConstructInteriorRisk", "PRODCONF.PRODVER.PRODSTRUCTS.secondHouseConstructInteriorRisk"},
        {"INSOBJGROUPLIST.secondHouse.OBJLIST.secondHouseConstructInterior.CONTROBJMAP.CONTRRISKLIST.secondHouseConstructInteriorRisk.PREMVALUE", "CONTROBJLIST.house2.RISKLIST.construct.PREMVALUE"},
        {"INSOBJGROUPLIST.secondHouse.OBJLIST.secondHouseConstructInterior.CONTROBJMAP.CONTRRISKLIST.secondHouseConstructInteriorRisk.INSAMVALUE", "CONTROBJLIST.house2.RISKLIST.construct.INSAMVALUE"},
        // БАНЯ

        {"INSOBJGROUPLIST.banya.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.banya.SYSNAME"},
        {"INSOBJGROUPLIST.banya.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.banya.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.banya.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.banya.HBDATAVERID"},
        {"INSOBJGROUPLIST.banya.area", "CONTROBJLIST.sauna.OBJAREA"}, // Площадь
        {"INSOBJGROUPLIST.banya.interiorDecorationType", "CONTROBJLIST.sauna.FACINGTYPE", "", sisBanyaFacingTypeConvertRules}, // Тип внутренней отделки

        {"INSOBJGROUPLIST.banya.OBJLIST.banyaConstructInterior.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.banyaConstructInterior"},
        {"INSOBJGROUPLIST.banya.OBJLIST.banyaConstructInterior.CONTROBJMAP.CONTRRISKLIST.banyaConstructInteriorRisk", "PRODCONF.PRODVER.PRODSTRUCTS.banyaConstructInteriorRisk"},
        {"INSOBJGROUPLIST.banya.OBJLIST.banyaConstructInterior.CONTROBJMAP.CONTRRISKLIST.banyaConstructInteriorRisk.PREMVALUE", "CONTROBJLIST.sauna.RISKLIST.construct.PREMVALUE"},
        {"INSOBJGROUPLIST.banya.OBJLIST.banyaConstructInterior.CONTROBJMAP.CONTRRISKLIST.banyaConstructInteriorRisk.INSAMVALUE", "CONTROBJLIST.sauna.RISKLIST.construct.INSAMVALUE"},
        // ГРАЖДАНСКАЯ ОТВЕТСТВЕННОСТЬ (в b2b - отдельная группа)

        {"INSOBJGROUPLIST.go.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.go.SYSNAME"},
        {"INSOBJGROUPLIST.go.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.go.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.go.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.go.HBDATAVERID"},
        {"INSOBJGROUPLIST.go.OBJLIST.goObject.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.goObject"},
        {"INSOBJGROUPLIST.go.OBJLIST.goObject.CONTROBJMAP.CONTRRISKLIST.goRisk", "PRODCONF.PRODVER.PRODSTRUCTS.goRisk"},
        {"INSOBJGROUPLIST.go.OBJLIST.goObject.CONTROBJMAP.CONTRRISKLIST.goRisk.PREMVALUE", "CONTROBJLIST.house.RISKLIST.go.PREMVALUE"},
        {"INSOBJGROUPLIST.go.OBJLIST.goObject.CONTROBJMAP.CONTRRISKLIST.goRisk.INSAMVALUE", "CONTROBJLIST.house.RISKLIST.go.INSAMVALUE"},
        // для квартиры по ФТ предусмотрены отдельный объект и риск по гражданской ответственности - в группе 'Квартира' (flat)
        //{"INSOBJGROUPLIST.go.OBJLIST.goObject.CONTROBJMAP.CONTRRISKLIST.goRisk.PREMVALUE", "CONTROBJLIST.flat.RISKLIST.go.PREMVALUE"},
        //{"INSOBJGROUPLIST.go.OBJLIST.goObject.CONTROBJMAP.CONTRRISKLIST.goRisk.INSAMVALUE", "CONTROBJLIST.flat.RISKLIST.go.INSAMVALUE"},

        // ДВИЖИМОЕ ИМУЩЕСТВО (в b2b - отдельная группа)

        {"INSOBJGROUPLIST.movableProperty.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.movableProperty.SYSNAME"},
        {"INSOBJGROUPLIST.movableProperty.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.movableProperty.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.movableProperty.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.movableProperty.HBDATAVERID"},
        {"INSOBJGROUPLIST.movableProperty.OBJLIST.movablePropertyObject.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.movablePropertyObject"},
        {"INSOBJGROUPLIST.movableProperty.OBJLIST.movablePropertyObject.CONTROBJMAP.CONTRRISKLIST.movablePropertyRisk", "PRODCONF.PRODVER.PRODSTRUCTS.movablePropertyRisk"},
        {"INSOBJGROUPLIST.movableProperty.OBJLIST.movablePropertyObject.CONTROBJMAP.CONTRRISKLIST.movablePropertyRisk.PREMVALUE", "CONTROBJLIST.house.RISKLIST.movables.PREMVALUE"},
        {"INSOBJGROUPLIST.movableProperty.OBJLIST.movablePropertyObject.CONTROBJMAP.CONTRRISKLIST.movablePropertyRisk.INSAMVALUE", "CONTROBJLIST.house.RISKLIST.movables.INSAMVALUE"}, // для квартиры по ФТ предусмотрены отдельный объект и риск по движимому имуществу - в группе 'Квартира' (flat)
    //{"INSOBJGROUPLIST.movableProperty.OBJLIST.movablePropertyObject.CONTROBJMAP.CONTRRISKLIST.movablePropertyRisk.PREMVALUE", "CONTROBJLIST.flat.RISKLIST.movables.PREMVALUE"},
    //{"INSOBJGROUPLIST.movableProperty.OBJLIST.movablePropertyObject.CONTROBJMAP.CONTRRISKLIST.movablePropertyRisk.INSAMVALUE", "CONTROBJLIST.flat.RISKLIST.movables.INSAMVALUE"},
    // иных объектов для продукта 'Защита имущества сотрудников сбербанка Онлайн' может быть разное количество - их обработка в genAdditionalSisSumSaveParams
    //{"INSOBJGROUPLIST.other.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.other.SYSNAME"},
    //{"INSOBJGROUPLIST.other.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.other.PRODSTRUCTID"},
    //{"INSOBJGROUPLIST.other.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.other.HBDATAVERID"},
    //{"INSOBJGROUPLIST.other.OBJLIST.otherConstructInterior.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.otherConstructInterior"},
    //{"INSOBJGROUPLIST.other.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST.otherConstructInteriorRisk", "PRODCONF.PRODVER.PRODSTRUCTS.otherConstructInteriorRisk"},
    //{"INSOBJGROUPLIST.other.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST.otherConstructInteriorRisk.PREMVALUE", "CONTROBJLIST.other.RISKLIST.construct.PREMVALUE"},
    //{"INSOBJGROUPLIST.other.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST.otherConstructInteriorRisk.INSAMVALUE", "CONTROBJLIST.other.RISKLIST.construct.INSAMVALUE"},
    //</editor-fold>
    };
    public static final String[][] vzrKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="Страхование путешественников Онлайн">
        {"INSOBJGROUPLIST.VZRinsuredPersons.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.VZRinsuredPersons.SYSNAME"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.VZRinsuredPersons.PRODSTRUCTID"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.VZRinsuredPersons.HBDATAVERID"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRvzrObject.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.VZRvzrObject"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRvzrObject.CONTROBJMAP.CONTRRISKLIST.VZRmedical", "PRODCONF.PRODVER.PRODSTRUCTS.VZRmedical"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRvzrObject.CONTROBJMAP.CONTRRISKLIST.VZRmedical.PREMVALUE", "RISKLIST.VZRmedical.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRvzrObject.CONTROBJMAP.CONTRRISKLIST.VZRmedical.PREMCURRENCYID", "RISKLIST.VZRmedical.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRvzrObject.CONTROBJMAP.CONTRRISKLIST.VZRmedical.PAYPREMVALUE", "RISKLIST.VZRmedical.PAYPREMVALUE"},

        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.VZRfinObject"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedKidsEvac", "PRODCONF.PRODVER.PRODSTRUCTS.VZRmedKidsEvac"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedVisit", "PRODCONF.PRODVER.PRODSTRUCTS.VZRmedVisit"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedMessages", "PRODCONF.PRODVER.PRODSTRUCTS.VZRmedMessages"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedTransCosts", "PRODCONF.PRODVER.PRODSTRUCTS.VZRmedTransCosts"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedDocLoss", "PRODCONF.PRODVER.PRODSTRUCTS.VZRmedDocLoss"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedSearchRescue", "PRODCONF.PRODVER.PRODSTRUCTS.VZRmedSearchRescue"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedHotel", "PRODCONF.PRODVER.PRODSTRUCTS.VZRmedHotel"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedTranslator", "PRODCONF.PRODVER.PRODSTRUCTS.VZRmedTranslator"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRjuridical", "PRODCONF.PRODVER.PRODSTRUCTS.VZRjuridical"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRtripstop", "PRODCONF.PRODVER.PRODSTRUCTS.VZRtripstop"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRflightdelay", "PRODCONF.PRODVER.PRODSTRUCTS.VZRflightdelay"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRskypass", "PRODCONF.PRODVER.PRODSTRUCTS.VZRskypass"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRtripcancel", "PRODCONF.PRODVER.PRODSTRUCTS.VZRtripcancel"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedKidsEvac.PREMVALUE", "RISKLIST.VZRmedKidsEvac.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedKidsEvac.PREMCURRENCYID", "RISKLIST.VZRmedKidsEvac.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedKidsEvac.PAYPREMVALUE", "RISKLIST.VZRmedKidsEvac.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedVisit.PREMVALUE", "RISKLIST.VZRmedVisit.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedVisit.PREMCURRENCYID", "RISKLIST.VZRmedVisit.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedVisit.PAYPREMVALUE", "RISKLIST.VZRmedVisit.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedMessages.PREMVALUE", "RISKLIST.VZRmedMessages.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedMessages.PREMCURRENCYID", "RISKLIST.VZRmedMessages.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedMessages.PAYPREMVALUE", "RISKLIST.VZRmedMessages.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedTransCosts.PREMVALUE", "RISKLIST.VZRmedTransCosts.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedTransCosts.PREMCURRENCYID", "RISKLIST.VZRmedTransCosts.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedTransCosts.PAYPREMVALUE", "RISKLIST.VZRmedTransCosts.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedDocLoss.PREMVALUE", "RISKLIST.VZRmedDocLoss.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedDocLoss.PREMCURRENCYID", "RISKLIST.VZRmedDocLoss.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedDocLoss.PAYPREMVALUE", "RISKLIST.VZRmedDocLoss.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedSearchRescue.PREMVALUE", "RISKLIST.VZRmedSearchRescue.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedSearchRescue.PREMCURRENCYID", "RISKLIST.VZRmedSearchRescue.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedSearchRescue.PAYPREMVALUE", "RISKLIST.VZRmedSearchRescue.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedHotel.PREMVALUE", "RISKLIST.VZRmedHotel.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedHotel.PREMCURRENCYID", "RISKLIST.VZRmedHotel.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedHotel.PAYPREMVALUE", "RISKLIST.VZRmedHotel.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedTranslator.PREMVALUE", "RISKLIST.VZRmedTranslator.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedTranslator.PREMCURRENCYID", "RISKLIST.VZRmedTranslator.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRmedTranslator.PAYPREMVALUE", "RISKLIST.VZRmedTranslator.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRjuridical.PREMVALUE", "RISKLIST.VZRjuridical.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRjuridical.PREMCURRENCYID", "RISKLIST.VZRjuridical.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRjuridical.PAYPREMVALUE", "RISKLIST.VZRjuridical.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRtripstop.PREMVALUE", "RISKLIST.VZRtripstop.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRtripstop.PREMCURRENCYID", "RISKLIST.VZRtripstop.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRtripstop.PAYPREMVALUE", "RISKLIST.VZRtripstop.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRflightdelay.PREMVALUE", "RISKLIST.VZRflightdelay.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRflightdelay.PREMCURRENCYID", "RISKLIST.VZRflightdelay.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRflightdelay.PAYPREMVALUE", "RISKLIST.VZRflightdelay.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRskypass.PREMVALUE", "RISKLIST.VZRskypass.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRskypass.PREMCURRENCYID", "RISKLIST.VZRskypass.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRskypass.PAYPREMVALUE", "RISKLIST.VZRskypass.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRtripcancel.PREMVALUE", "RISKLIST.VZRtripcancel.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRtripcancel.PREMCURRENCYID", "RISKLIST.VZRtripcancel.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRfinObject.CONTROBJMAP.CONTRRISKLIST.VZRtripcancel.PAYPREMVALUE", "RISKLIST.VZRtripcancel.PAYPREMVALUE"},

        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRgoObject.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.VZRgoObject"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRgoObject.CONTROBJMAP.CONTRRISKLIST.VZRgo", "PRODCONF.PRODVER.PRODSTRUCTS.VZRgo"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRgoObject.CONTROBJMAP.CONTRRISKLIST.VZRgo.PREMVALUE", "RISKLIST.VZRgo.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRgoObject.CONTROBJMAP.CONTRRISKLIST.VZRgo.PREMCURRENCYID", "RISKLIST.VZRgo.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRgoObject.CONTROBJMAP.CONTRRISKLIST.VZRgo.PAYPREMVALUE", "RISKLIST.VZRgo.PAYPREMVALUE"},

        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRaccidentObject.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.VZRaccidentObject"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRaccidentObject.CONTROBJMAP.CONTRRISKLIST.VZRns", "PRODCONF.PRODVER.PRODSTRUCTS.VZRns"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRaccidentObject.CONTROBJMAP.CONTRRISKLIST.VZRns.PREMVALUE", "RISKLIST.VZRns.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRaccidentObject.CONTROBJMAP.CONTRRISKLIST.VZRns.PREMCURRENCYID", "RISKLIST.VZRns.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRaccidentObject.CONTROBJMAP.CONTRRISKLIST.VZRns.PAYPREMVALUE", "RISKLIST.VZRns.PAYPREMVALUE"},

        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRlootObject.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.VZRlootObject"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRlootObject.CONTROBJMAP.CONTRRISKLIST.VZRlootlost", "PRODCONF.PRODVER.PRODSTRUCTS.VZRlootlost"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRlootObject.CONTROBJMAP.CONTRRISKLIST.VZRlootdelay", "PRODCONF.PRODVER.PRODSTRUCTS.VZRlootdelay"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRlootObject.CONTROBJMAP.CONTRRISKLIST.VZRlootlost.PREMVALUE", "RISKLIST.VZRlootlost.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRlootObject.CONTROBJMAP.CONTRRISKLIST.VZRlootlost.PREMCURRENCYID", "RISKLIST.VZRlootlost.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRlootObject.CONTROBJMAP.CONTRRISKLIST.VZRlootlost.PAYPREMVALUE", "RISKLIST.VZRlootlost.PAYPREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRlootObject.CONTROBJMAP.CONTRRISKLIST.VZRlootdelay.PREMVALUE", "RISKLIST.VZRlootdelay.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRlootObject.CONTROBJMAP.CONTRRISKLIST.VZRlootdelay.PREMCURRENCYID", "RISKLIST.VZRlootdelay.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRlootObject.CONTROBJMAP.CONTRRISKLIST.VZRlootdelay.PAYPREMVALUE", "RISKLIST.VZRlootdelay.PAYPREMVALUE"},

        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRsporttoolsObject.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.VZRsporttoolsObject"},
        {"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRsporttoolsObject.CONTROBJMAP.CONTRRISKLIST.VZRsporttools", "PRODCONF.PRODVER.PRODSTRUCTS.VZRsporttools"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRsporttoolsObject.CONTROBJMAP.CONTRRISKLIST.VZRsporttools.PREMVALUE", "RISKLIST.VZRsporttools.PREMVALUE"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRsporttoolsObject.CONTROBJMAP.CONTRRISKLIST.VZRsporttools.PREMCURRENCYID", "RISKLIST.VZRsporttools.PREMCURRENCYID"},
        //{"INSOBJGROUPLIST.VZRinsuredPersons.OBJLIST.VZRsporttoolsObject.CONTROBJMAP.CONTRRISKLIST.VZRsporttools.PAYPREMVALUE", "RISKLIST.VZRsporttools.PAYPREMVALUE"},

        //{"CONTREXTMAP.DOPPACKLIST", "CONTREXTRATTR.dopPackageList"},
        //{"CONTREXTMAP.RISKSYSNAMES", "CONTREXTRATTR.riskSysNames"},
        {"CONTREXTMAP.adultsAndChildren3_60", "CONTREXTRATTR.insuredCount60"},
        {"CONTREXTMAP.dayCount", "DURATION"},
        {"CONTREXTMAP.babes", "CONTREXTRATTR.insuredCount2"},
        {"CONTREXTMAP.optionSport", "CONTREXTRATTR.isSportEnabled"},
        {"CONTREXTMAP.old61_70", "CONTREXTRATTR.insuredCount70"},
        {"CONTREXTMAP.insuranceTerritory", "CONTREXTRATTR.territoty", "", "NoUSARF > 0; NoRF > 1; RFSNG > 2; NoUSA > 3"},
        // TRAVELTYPE - вид полиса (0 - не годовой; 1 - годовой на год; 2 - годовой на 90 дней)

        // annualPolicy - годовой полис (0 - нет; 1 - да)
        {"CONTREXTMAP.annualPolicy", "TRAVELTYPE", "0", "0 > 0; 1 > 1; 2 > 1"},
        {"CONTREXTMAP.annualPolicy", "CONTREXTRATTR.travelType", "0", "0 > 0; 1 > 1; 2 > 1"},
        // annualPolicyType - вариант годового полиса (0 - 90 дней; 1 - 365 дней)
        {"CONTREXTMAP.annualPolicyType", "TRAVELTYPE", "0", "1 > 1; 2 > 0"},
        {"CONTREXTMAP.annualPolicyType", "CONTREXTRATTR.travelType", "0", "1 > 1; 2 > 0"},
        //
        {"MEMBERLIST.0.NAME_ENG", "INSUREDLIST.insured0.FIRSTNAME"},
        {"MEMBERLIST.0.SURNAME_ENG", "INSUREDLIST.insured0.LASTNAME"},
        //{"MEMBERLIST.0.MIDDLENAME", "INSUREDLIST.insured0.MIDDLENAME"},
        {"MEMBERLIST.0.BIRTHDATE", "INSUREDLIST.insured0.BIRTHDATE"},
        {"MEMBERLIST.1.NAME_ENG", "INSUREDLIST.insured1.FIRSTNAME"},
        {"MEMBERLIST.1.SURNAME_ENG", "INSUREDLIST.insured1.LASTNAME"},
        //{"MEMBERLIST.1.MIDDLENAME", "INSUREDLIST.insured1.MIDDLENAME"},
        {"MEMBERLIST.1.BIRTHDATE", "INSUREDLIST.insured1.BIRTHDATE"},
        {"MEMBERLIST.2.NAME_ENG", "INSUREDLIST.insured2.FIRSTNAME"},
        {"MEMBERLIST.2.SURNAME_ENG", "INSUREDLIST.insured2.LASTNAME"},
        //{"MEMBERLIST.2.MIDDLENAME", "INSUREDLIST.insured2.MIDDLENAME"},
        {"MEMBERLIST.2.BIRTHDATE", "INSUREDLIST.insured2.BIRTHDATE"},
        {"MEMBERLIST.3.NAME_ENG", "INSUREDLIST.insured3.FIRSTNAME"},
        {"MEMBERLIST.3.SURNAME_ENG", "INSUREDLIST.insured3.LASTNAME"},
        //{"MEMBERLIST.3.MIDDLENAME", "INSUREDLIST.insured3.MIDDLENAME"},
        {"MEMBERLIST.3.BIRTHDATE", "INSUREDLIST.insured3.BIRTHDATE"},
        {"MEMBERLIST.4.NAME_ENG", "INSUREDLIST.insured4.FIRSTNAME"},
        {"MEMBERLIST.4.SURNAME_ENG", "INSUREDLIST.insured4.LASTNAME"},
        //{"MEMBERLIST.4.MIDDLENAME", "INSUREDLIST.insured4.MIDDLENAME"},
        {"MEMBERLIST.4.BIRTHDATE", "INSUREDLIST.insured4.BIRTHDATE"},
        {"MEMBERLIST.5.NAME_ENG", "INSUREDLIST.insured5.FIRSTNAME"},
        {"MEMBERLIST.5.SURNAME_ENG", "INSUREDLIST.insured5.LASTNAME"},
        //{"MEMBERLIST.5.MIDDLENAME", "INSUREDLIST.insured5.MIDDLENAME"},
        {"MEMBERLIST.5.BIRTHDATE", "INSUREDLIST.insured5.BIRTHDATE"},
        {"MEMBERLIST.6.NAME_ENG", "INSUREDLIST.insured6.FIRSTNAME"},
        {"MEMBERLIST.6.SURNAME_ENG", "INSUREDLIST.insured6.LASTNAME"},
        //{"MEMBERLIST.6.MIDDLENAME", "INSUREDLIST.insured6.MIDDLENAME"},
        {"MEMBERLIST.6.BIRTHDATE", "INSUREDLIST.insured6.BIRTHDATE"},
        {"MEMBERLIST.7.NAME_ENG", "INSUREDLIST.insured7.FIRSTNAME"},
        {"MEMBERLIST.7.SURNAME_ENG", "INSUREDLIST.insured7.LASTNAME"},
        //{"MEMBERLIST.7.MIDDLENAME", "INSUREDLIST.insured7.MIDDLENAME"},
        {"MEMBERLIST.7.BIRTHDATE", "INSUREDLIST.insured7.BIRTHDATE"},
        {"MEMBERLIST.8.NAME_ENG", "INSUREDLIST.insured8.FIRSTNAME"},
        {"MEMBERLIST.8.SURNAME_ENG", "INSUREDLIST.insured8.LASTNAME"},
        //{"MEMBERLIST.8.MIDDLENAME", "INSUREDLIST.insured8.MIDDLENAME"},
        {"MEMBERLIST.8.BIRTHDATE", "INSUREDLIST.insured8.BIRTHDATE"},
        {"MEMBERLIST.9.NAME_ENG", "INSUREDLIST.insured9.FIRSTNAME"},
        {"MEMBERLIST.9.SURNAME_ENG", "INSUREDLIST.insured9.LASTNAME"},
        //{"MEMBERLIST.9.MIDDLENAME", "INSUREDLIST.insured9.MIDDLENAME"},
        {"MEMBERLIST.9.BIRTHDATE", "INSUREDLIST.insured9.BIRTHDATE"},
        // параметры для вызова калькулятора
        // (новый калькулятор принимает набор параметров в старом формате, поскольку был клонирован со старого)
        {"CALCPARAMS.CALCVERID", "PRODCONF.CALCVERID"},
        {"CALCPARAMS.daysCount", "DURATION"},
        {"CALCPARAMS.travelKind", "TRAVELTYPE"},
        {"CALCPARAMS.travelKind", "CONTREXTRATTR.travelType"},
        {"CALCPARAMS.isSportEnabled", "CONTREXTRATTR.isSportEnabled"},
        {"CALCPARAMS.territorySysName", "CONTREXTRATTR.territoty"},
        {"CALCPARAMS.programSysName", "PRODPROGSYSNAME"},
        {"CALCPARAMS.programSysName", "CONTREXTRATTR.prodProgSysName"},
        //{"CALCPARAMS.CURRENCYID", "INSAMCURRENCYID"}, // не используется калькулятором
        {"CALCPARAMS.RISKSYSNAMES", "RISKSYSNAMES"}, // список рисков строкой через запятую
        {"CALCPARAMS.babesCount", "CONTREXTRATTR.insuredCount2"},
        {"CALCPARAMS.adultsAndChildren3_60Count", "CONTREXTRATTR.insuredCount60"},
        {"CALCPARAMS.old61_70Count", "CONTREXTRATTR.insuredCount70"}, //</editor-fold>
    };

    // основной список соответствий ключей
    // (имя ключа в B2B, имя ключа в старых договорах, значение по умолчанию, строка с правилом преобразования)
    // (используется как для прямого преобразования структуры при сохранении, так и для обратного при загрузке)
    public static String[][] commonKeysRelations = {
        //<editor-fold defaultstate="collapsed" desc="страхователь">

        {"INSURERMAP.addressList.RegisterAddress.REGION", "insAdrRegNAME"},
        {"INSURERMAP.addressList.RegisterAddress.REGIONKLADR", "insAdrRegCODE"},
        {"INSURERMAP.addressList.RegisterAddress.CITY", "insAdrCityNAME"},
        {"INSURERMAP.addressList.RegisterAddress.CITYKLADR", "insAdrCityCODE"},
        {"INSURERMAP.addressList.RegisterAddress.STREET", "insAdrStrNAME"},
        {"INSURERMAP.addressList.RegisterAddress.STREETKLADR", "insAdrStrCODE"},
        {"INSURERMAP.addressList.RegisterAddress.POSTALCODE", "insAdrStrPOSTALCODE"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSE", "insAdrHouse"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSING", "insAdrHousing"},
        {"INSURERMAP.addressList.RegisterAddress.BUILDING", "insAdrBuilding"},
        {"INSURERMAP.addressList.RegisterAddress.FLAT", "insAdrFlat"},
        {"INSURERMAP.contactList.MobilePhone.VALUE", "insPhone"},
        {"INSURERMAP.contactList.PersonalEmail.VALUE", "insEmail"},
        {"INSURERMAP.contactList.PersonalEmail.VALUE", "insEmailValid"},
        {"INSURERMAP.documentList.PassportRF.DOCSERIES", "insPassSeries"},
        {"INSURERMAP.documentList.PassportRF.DOCNUMBER", "insPassNumber"},
        {"INSURERMAP.documentList.PassportRF.ISSUEDATE", "insPassIssueDate"},
        {"INSURERMAP.documentList.PassportRF.ISSUEDBY", "insPassIssuePlace"},
        {"INSURERMAP.documentList.PassportRF.DOCTYPESYSNAME", "insPassDocType"},
        {"INSURERMAP.documentList.PassportRF.CODE", "insPassDocCode"}, // код типа документа, если есть системное имя (DOCTYPESYSNAME) - избыточен
        {"INSURERMAP.documentList.ForeignPassport.DOCSERIES", "insPassSeries"},
        {"INSURERMAP.documentList.ForeignPassport.DOCNUMBER", "insPassNumber"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUEDATE", "insPassIssueDate"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUEDBY", "insPassIssuePlace"},
        {"INSURERMAP.documentList.ForeignPassport.DOCTYPESYSNAME", "insPassDocType"},
        {"INSURERMAP.documentList.ForeignPassport.CODE", "insPassDocCode"}, // код типа документа, если есть системное имя (DOCTYPESYSNAME) - избыточен
        {"INSURERMAP.PARTICIPANTTYPE", "PARTICIPANTTYPE", "1"},
        {"INSURERMAP.ISBUSINESSMAN", "ISBUSINESSMAN", "0"},
        {"INSURERMAP.ISCLIENT", "ISCLIENT", "1"},
        {"INSURERMAP.LASTNAME", "insSurname"},
        {"INSURERMAP.FIRSTNAME", "insName"},
        {"INSURERMAP.MIDDLENAME", "insMiddlename"},
        {"INSURERMAP.BIRTHDATE", "insBirthdate"},
        {"INSURERMAP.CITIZENSHIP", "insCitizenship", "0", "1 > 0; 3 > 1000"}, // преобразование (1/3 -> 0/1000)
        {"INSURERMAP.GENDER", "insGender", "0", "male > 0; female > 1"}, // преобразование (male/female -> 0/1)

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="страхователь для ипотеки и имущества сотрудников">
        // основные сведения о страхователе
        {"INSURERMAP.PARTICIPANTTYPE", "master.insurer.PARTICIPANTTYPE", "1"},
        {"INSURERMAP.ISBUSINESSMAN", "master.insurer.ISBUSINESSMAN", "0"},
        {"INSURERMAP.ISCLIENT", "master.insurer.ISCLIENT", "1"},
        {"INSURERMAP.LASTNAME", "master.insurer.surname"},
        {"INSURERMAP.FIRSTNAME", "master.insurer.name"},
        {"INSURERMAP.MIDDLENAME", "master.insurer.middlename"},
        {"INSURERMAP.BIRTHDATE", "master.insurer.birthDateStr"},
        {"INSURERMAP.CITIZENSHIP", "master.insurer.citizenship", "0", "1 > 0; 3 > 1000"}, // преобразование (1/3 -> 0/1000)
        {"INSURERMAP.GENDER", "master.insurer.gender", "0", "male > 0; female > 1"}, // преобразование (male/female -> 0/1)
        // адрес страхователя
        {"INSURERMAP.addressList.RegisterAddress.REGION", "master.insurer.address.region.NAME"},
        {"INSURERMAP.addressList.RegisterAddress.REGIONKLADR", "master.insurer.address.region.CODE"},
        {"INSURERMAP.addressList.RegisterAddress.CITY", "master.insurer.address.city.NAME"},
        {"INSURERMAP.addressList.RegisterAddress.CITYKLADR", "master.insurer.address.city.CODE"},
        {"INSURERMAP.addressList.RegisterAddress.STREET", "master.insurer.address.street.NAME"},
        {"INSURERMAP.addressList.RegisterAddress.STREETKLADR", "master.insurer.address.street.CODE"},
        {"INSURERMAP.addressList.RegisterAddress.POSTALCODE", "master.insurer.address.street.POSTALCODE"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSE", "master.insurer.address.house"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSING", "master.insurer.address.housing"},
        {"INSURERMAP.addressList.RegisterAddress.BUILDING", "master.insurer.address.building"},
        {"INSURERMAP.addressList.RegisterAddress.FLAT", "master.insurer.address.flat"},
        // контакты страхователя
        {"INSURERMAP.contactList.MobilePhone.VALUE", "master.insurer.contacts.phone"},
        {"INSURERMAP.contactList.PersonalEmail.VALUE", "master.insurer.contacts.email"},
        {"INSURERMAP.contactList.PersonalEmail.VALUE", "master.insurer.contacts.emailValid"},
        // документы страхователя
        {"INSURERMAP.documentList.PassportRF.DOCSERIES", "master.insurer.passport.series"},
        {"INSURERMAP.documentList.PassportRF.DOCNUMBER", "master.insurer.passport.number"},
        {"INSURERMAP.documentList.PassportRF.ISSUEDATE", "master.insurer.passport.issueDateStr"},
        {"INSURERMAP.documentList.PassportRF.ISSUEDBY", "master.insurer.passport.issuePlace"},
        {"INSURERMAP.documentList.PassportRF.DOCTYPESYSNAME", "master.insurer.passport.typeId"},
        {"INSURERMAP.documentList.PassportRF.CODE", "master.insurer.passport.code"}, // код типа документа, если есть системное имя (DOCTYPESYSNAME) - избыточен
        {"INSURERMAP.documentList.ForeignPassport.DOCSERIES", "master.insurer.passport.series"},
        {"INSURERMAP.documentList.ForeignPassport.DOCNUMBER", "master.insurer.passport.number"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUEDATE", "master.insurer.passport.issueDateStr"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUEDBY", "master.insurer.passport.issuePlace"},
        {"INSURERMAP.documentList.ForeignPassport.DOCTYPESYSNAME", "master.insurer.passport.typeId"},
        {"INSURERMAP.documentList.ForeignPassport.CODE", "master.insurer.passport.code"}, // код типа документа, если есть системное имя (DOCTYPESYSNAME) - избыточен

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="страхователь - путешествия">

        // основные сведения о страхователе
        {"INSURERMAP.LASTNAME", "insurer.surname"},
        {"INSURERMAP.FIRSTNAME", "insurer.name"},
        {"INSURERMAP.MIDDLENAME", "insurer.middlename"},
        {"INSURERMAP.BIRTHDATE", "insurer.birthDate.dateStr"},
        {"INSURERMAP.CITIZENSHIP", "insurer.citizenship", "0", "1 > 0; 3 > 1000"}, // преобразование (1/3 -> 0/1000)
        {"INSURERMAP.GENDER", "insurer.gender", "0", "male > 0; female > 1"}, // преобразование (male/female -> 0/1)
        {"INSURERMAP.PARTICIPANTTYPE", "insurer.PARTICIPANTTYPE", "1"},
        {"INSURERMAP.ISBUSINESSMAN", "insurer.ISBUSINESSMAN", "0"},
        {"INSURERMAP.ISCLIENT", "insurer.ISCLIENT", "1"},
        // контакты страхователя
        {"INSURERMAP.contactList.MobilePhone.VALUE", "insurer.contacts.phone"},
        {"INSURERMAP.contactList.PersonalEmail.VALUE", "insurer.contacts.email"},
        {"INSURERMAP.contactList.PersonalEmail.VALUE", "insurer.contacts.emailValid"},
        // документы страхователя
        {"INSURERMAP.documentList.PassportRF.DOCSERIES", "insurer.passport.series"},
        {"INSURERMAP.documentList.PassportRF.DOCNUMBER", "insurer.passport.number"},
        {"INSURERMAP.documentList.PassportRF.ISSUEDATE", "insurer.passport.issueDate.dateStr"},
        {"INSURERMAP.documentList.PassportRF.ISSUEDBY", "insurer.passport.issuePlace"},
        {"INSURERMAP.documentList.PassportRF.DOCTYPESYSNAME", "insurer.passport.typeId"},
        {"INSURERMAP.documentList.PassportRF.CODE", "insurer.passport.code"}, // код типа документа, если есть системное имя (DOCTYPESYSNAME) - избыточен

        {"INSURERMAP.documentList.ForeignPassport.DOCSERIES", "insurer.passport.series"},
        {"INSURERMAP.documentList.ForeignPassport.DOCNUMBER", "insurer.passport.number"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUEDATE", "insurer.passport.issueDate.dateStr"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUEDBY", "insurer.passport.issuePlace"},
        {"INSURERMAP.documentList.ForeignPassport.DOCTYPESYSNAME", "insurer.passport.typeId"},
        {"INSURERMAP.documentList.ForeignPassport.CODE", "insurer.passport.code"}, // код типа документа, если есть системное имя (DOCTYPESYSNAME) - избыточен

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="основные сведения договора - Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России»">
        {"STARTDATE", "master.startDate.dateStr"},
        {"FINISHDATE", "master.finishDate.dateStr"},
        // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» - расширенные атрибуты договора перенесены в mortgageKeysRelations
        // (во избежание конфликта с Защита имущества сотрудников сбербанка Онлайн)
        //{"CONTREXTMAP.buildYear", "master.insObj.buildYear", "0", "before1955 > 0; after1955 > 1"},
        //{"CONTREXTMAP.propertyAddress", "master.insObj.addressText"},
        //{"CONTREXTMAP.creditStartDATE", "master.contrDate.dateStr"},
        //{"CONTREXTMAP.creditNumber", "master.insurerContrNum"},
        //{"CONTREXTMAP.woodInCeilings", "master.insObj.woodInWal", "0", "1 > 0; 0 > 1"},
        //{"CONTREXTMAP.insObject", "master.insObj.typeSysName", "0", "house > 0; flat > 1"},

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="основные сведения договора - Защита банковской карты Онлайн">
        {"INSAMVALUE", "insAmVal"},
        {"PREMVALUE", "insPremVal"},
        {"PRODPROGID", "prodProgId"}, // todo: преобразование?
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="общие для всех продуктов данные, включая служебные">
        // программа договора - в показателях договора (insuranceProgram)
        {"STARTDATE", "STARTDATE"},
        {"FINISHDATE", "FINISHDATE"},
        {"DURATION", "DURATION"},
        {"INSAMVALUE", "INSAMVALUE"},
        {"PREMVALUE", "PREMVALUE"},
        {"PAYPREMVALUE", "PAYPREMVALUE"},
        {"REFERRAL", "REFERRAL"},
        {"REFERRALBACK", "REFERRALBACK"},
        {"B2BPROMOCODE", "B2BPROMOCODE"},
        {"B2BPROMOCODE", "b2bPromoCode"},
        {"B2BPROMOAPPLIED", "B2BPROMOAPPLIED"},
        {"B2BPROMOAPPLIED", "b2bPromoApplied"},
        {"INFORMSUPPORT", "INFORMSUPPORT"},
        {"INFORMSUPPORT", "informSupport"},
        {"did", "did"},
        {"dpid", "dpid"},
        {"CONTREXTMAP.HBDATAVERID", "PRODCONF.HBDATAVERID"}, // идентификатор для показателей договора
        //{"CONTREXTMAP.insuranceProgram", "prodProgId"}, // код программы страховани в показателях договора в B2B отличается от идентификатора (согласно документу 'Выгрузка договоров (карта, дом, взр).docx')
        {"EXTERNALID", "EXTERNALID"}, // копируется в явном виде, поскольку требуется в genAdditionalLoadParams
        {"SESSIONID", "sessionToken"},
        // коэффициент скидки
        // (на данный момент в B2B не хранится - конечные ключи предполагаемые, но используются при перерасчет сумм, например, при миграции)
        {"CONTREXTMAP.shareValue", "CONTREXTRATTR.shareValue"},
        {"CONTREXTMAP.shareValue", "promoValue"},
        {"SHAREVALUE", "CONTREXTRATTR.shareValue"},
        {"SHAREVALUE", "promoValue"},
        {"SESSIONID", "sessionId"}, // в b2b присутствует в полях договора

        {"INSREGIONCODE", "?", "77"}, // по сведениям из документов по выгрузке в 1С - константа (77)

        // валюты, по умолчанию - рубль
        {"INSAMCURRENCYID", "?", "1"}, // 1 = идентификатор рубля в REF_CURRENCY
        {"PREMCURRENCYID", "?", "1"}, // 1 = идентификатор рубля в REF_CURRENCY

        {"PRODCONFID", "prodConfId", "", "" /*prodConfIDConvertRules*/}, // prodConfIDConvertRules записывается сюда при генерирации в generateNewIDsByOld
        {"PRODVERID", "prodVerId", "", "" /*prodVerIDConvertRules*/}, // prodVerIDConvertRules записывается сюда при генерирации в generateNewIDsByOld

        // для обратной обработки при загрузке договора
        {"EXTERNALID", "GUID"},
        {"PRODCONFID", "PRODCONFID", "", "" /*prodConfIDConvertRules*/}, // для обратного преобразования при загрузке по правилам из prodConfIDConvertStr; prodConfIDConvertRules записывается сюда при генерирации в generateNewIDsByOld

        //{"INSURERMAP.BIRTHDATE", "INSBIRTHDATE"}, // для dsPrintReport - см ниже
        //{"INSURERMAP.documentList.ForeignPassport.ISSUEDATE", "INSISSUEDATE"}, // для dsPrintReport - см ниже
        //{"INSURERMAP.documentList.PassportRF.ISSUEDATE", "INSISSUEDATE"}, // для dsPrintReport - см ниже
        //{"?", "INSUREDLIST"}, // список застрахованных? для dsPrintReport

        //{"INSURERID", "INSUREDID"}, // для getIsurerEmail
        {"INSURERMAP.PARTICIPANTID", "INSUREDID"}, // для getIsurerEmail
        {"INSURERMAP.contactList.MobilePhone.VALUE", "MobilePhone"}, // для getIsurerEmail
        {"INSURERMAP.contactList.PersonalEmail.VALUE", "PersonalEmail"}, // для getIsurerEmail

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="показатели не используемые при создании, но требующиеся для переноса специальным методом веб-сервиса">
        // показатели не используемые при создании с интерфеса, но требующиеся для переноса специальным методом веб-сервиса
        // даты
        {"CANCELDATE", "CANCELDATE"},
        //
        {"PREMDELTA", "PREMDELTA"},
        {"SALESOFFICE", "SALESOFFICE"},
        // серия, номер, версия и пр. договора
        {"VERNUMBER", "VERNUMBER"},
        //
        {"PRODCONF", "PRODCONF"},
        //
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="для поддержки подмены запроса dsHabContractBrowseListParamEx">

        // для поддержки подмены запроса dsHabContractBrowseListParamEx
        // (todo: определить соответсвие для ключей помеченных "?")
        //
        //{"", "CONTRID"}, // совпадает с B2B
        //{"", "DURATION"}, // совпадает с B2B
        //{"", "CONTRNODEID"}, // совпадает с B2B
        //{"", "EXTERNALID"}, // совпадает с B2B
        {"CONTRNUMBER", "CONTRNUMBER"}, // совпадает с B2B
        {"CONTREXTMAP.creditNumber", "CREDCONTRNUM"}, // номер кредитного договора (для ипотеки)?
        {"CONTRPOLNUM", "CONTRPOLNUM"}, // совпадает с B2B
        {"CONTRPOLSER", "CONTRPOLSER"}, // совпадает с B2B
        {"PRODPROGID", "PRODPROGID"}, // совпадает с B2B
        {"CURRENCYRATE", "CURRENCYRATE"}, // курс (отношение) валют договора ?
        {"NOTE", "NOTE"}, // заметка к договору
        //{"?", "ORGSTRUCTID"}, // ?
        //
        // сведения о страхователе
        {"INSURERID", "INSUREDID"},
        {"INSURERMAP.BRIEFNAME", "INSBRIEFNAME"},
        {"INSURERMAP.PERSONID", "INSPERSONID"}, // идентификатор лица, отличный от идентификатора участника?
        {"INSURERMAP.CITIZENSHIP", "INSCITIZENSHIP"},
        {"INSURERMAP.FIRSTNAME", "INSFIRSTNAME"},
        {"INSURERMAP.MIDDLENAME", "INSMIDDLENAME"},
        {"INSURERMAP.LASTNAME", "INSLASTNAME"},
        {"INSURERMAP.BIRTHDATE", "INSBIRTHDATE"},
        {"INSURERMAP.BIRTHPLACE", "INSBIRTHPLACE"},
        {"INSURERMAP.GENDER", "INSGENDER"},
        // документы страхователя
        {"INSURERMAP.documentList.PassportRF.DOCSERIES", "INSDOCSERIES"},
        {"INSURERMAP.documentList.ForeignPassport.DOCSERIES", "INSDOCSERIES"},
        {"INSURERMAP.documentList.PassportRF.DOCNUMBER", "INSDOCNUMBER"},
        {"INSURERMAP.documentList.ForeignPassport.DOCNUMBER", "INSDOCNUMBER"},
        {"INSURERMAP.documentList.PassportRF.ISSUEDBY", "INSISSUEDBY"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUEDBY", "INSISSUEDBY"},
        {"INSURERMAP.documentList.PassportRF.ISSUERCODE", "INSISSUERCODE"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUERCODE", "INSISSUERCODE"},
        {"INSURERMAP.documentList.PassportRF.ISSUEDATE", "INSISSUEDATE"},
        {"INSURERMAP.documentList.ForeignPassport.ISSUEDATE", "INSISSUEDATE"},
        {"INSURERMAP.documentList.PassportRF.DOCTYPESYSNAME", "INSDOCTYPE"},
        {"INSURERMAP.documentList.ForeignPassport.DOCTYPESYSNAME", "INSDOCTYPE"},
        {"INSURERMAP.documentList.PassportRF.CODE", "INSDOCCODE"}, // код типа документа, если есть системное имя (DOCTYPESYSNAME) - избыточен
        {"INSURERMAP.documentList.ForeignPassport.CODE", "INSDOCCODE"}, // код типа документа, если есть системное имя (DOCTYPESYSNAME) - избыточен
        //
        // адрес страхователя
        {"INSURERMAP.RESCOUNTRY", "INSCOUNTRY"}, // не точно, возможно потребуется исправить
        {"INSURERMAP.addressList.RegisterAddress.POSTALCODE", "INSPOSTALCODE"},
        {"INSURERMAP.addressList.RegisterAddress.REGIONTYPE", "INSREGIONTYPE"},
        {"INSURERMAP.addressList.RegisterAddress.REGION", "INSREGION"},
        {"INSURERMAP.addressList.RegisterAddress.REGIONKLADR", "INSREGIONKLADR"},
        {"INSURERMAP.addressList.RegisterAddress.DISTRICTTYPE", "INSDISTRICTTYPE"},
        //{"INSURERMAP.addressList.RegisterAddress.?", "INSDISTRICT"}, // ?
        //{"INSURERMAP.addressList.RegisterAddress.?", "INSDISTRICTKLADR"}, // ?
        {"INSURERMAP.addressList.RegisterAddress.DISTRICTTYPE", "INSCITYTYPE"},
        {"INSURERMAP.addressList.RegisterAddress.CITY", "INSCITY"},
        {"INSURERMAP.addressList.RegisterAddress.CITYKLADR", "INSCITYKLADR"},
        {"INSURERMAP.addressList.RegisterAddress.VILLAGETYPE", "INSVILLAGETYPE"},
        //{"INSURERMAP.addressList.RegisterAddress.?", "INSVILLAGE"}, // ?
        //{"INSURERMAP.addressList.RegisterAddress.?", "INSVILLAGEKLADR"}, // ?
        {"INSURERMAP.addressList.RegisterAddress.STREETTYPE", "INSSTREETTYPE"},
        {"INSURERMAP.addressList.RegisterAddress.STREET", "INSSTREET"},
        {"INSURERMAP.addressList.RegisterAddress.STREETKLADR", "INSSTREETKLADR"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSE", "INSHOUSE"},
        {"INSURERMAP.addressList.RegisterAddress.HOUSING", "INSHOUSING"},
        {"INSURERMAP.addressList.RegisterAddress.BUILDING", "INSBUILDING"},
        {"INSURERMAP.addressList.RegisterAddress.FLATTYPE", "INSFLATTYPE"},
        {"INSURERMAP.addressList.RegisterAddress.FLAT", "INSFLAT"},
        {"INSURERMAP.addressList.RegisterAddress.ADDRESSTEXT1", "INSADDRESSTEXT1"},
        {"INSURERMAP.addressList.RegisterAddress.ADDRESSTEXT2", "INSADDRESSTEXT2"},
        {"INSURERMAP.addressList.RegisterAddress.ADDRESSTEXT3", "INSADDRESSTEXT3"},
        //
        // объект страхования
        // todo: дополнить при необходимости
        {"INSOBJGROUPLIST.00000.PROPERTYSYSNAME", "OBJTYPEID", "", "1 > house; 2 > flat"}, // 1 - дом, 2 - квартира
        //{"", "OBJNAME"},
        {"INSOBJGROUPLIST.00000.POSTALCODE", "OBJPOSTALCODE"},
        //{"", "OBJREGIONTYPE"},
        {"INSOBJGROUPLIST.00000.REGION", "OBJREGION"},
        {"INSOBJGROUPLIST.00000.REGIONKLADR", "OBJREGIONKLADR"},
        //{"", "OBJDISTRICTTYPE"},
        //{"", "OBJDISTRICT"},
        //{"", "OBJDISTRICTKLADR"},
        //{"", "OBJCITYTYPE"},
        {"INSOBJGROUPLIST.00000.CITY", "OBJCITY"},
        {"INSOBJGROUPLIST.00000.CITYKLADR", "OBJCITYKLADR"},
        //{"", "OBJVILLAGETYPE"},
        //{"", "OBJVILLAGE"},
        //{"", "OBJVILLAGEKLADR"},
        //{"", "OBJSTREETTYPE"},
        {"INSOBJGROUPLIST.00000.STREET", "OBJSTREET"},
        {"INSOBJGROUPLIST.00000.STREETKLADR", "OBJSTREETKLADR"},
        {"INSOBJGROUPLIST.00000.HOUSE", "OBJHOUSE"},
        {"INSOBJGROUPLIST.00000.HOUSING", "OBJHOUSING"},
        {"INSOBJGROUPLIST.00000.BUILDING", "OBJBUILDING"},
        //{"", "OBJFLATTYPE"},
        {"INSOBJGROUPLIST.00000.FLAT", "OBJFLAT"},
        {"CONTREXTMAP.propertyAddress", "OBJADDRESSTEXT1"},
        {"INSOBJGROUPLIST.00000.ADDRESSTEXT1", "OBJADDRESSTEXT1"},
        //
        // состояния?
        //{"", "STATEID"}, // совпадает с B2B
        //{"?", "STATESYSNAME"}, // ?
        //{"?", "STATENAME"}, // ?
        //
        //{"", "PRODVERID"}, // совпадает с B2B
        //{"?", "PRODUCTVERSIONNAME"}, // ?
        //{"", "PRODID"}, // совпадает с B2B
        //{"?", "PRODUCTNAME"}, // ?
        //{"?", "PRODUCTSYSNAME"}, // ?
        //
        {"CURRENCYID", "CURRENCYID"}, // общая валюта договора?
        {"PAYPREMVALUE", "PAYPREMVALUE"}, // ?
        {"PREMVALUE", "PREMIUM"}, // = PREMVALUE
        {"INSAMVALUE", "INSAMVALUE"}, // совпадает с B2B
        {"PREMCURRENCYID", "PREMIUMCURRENCYID"}, // = PREMCURRENCYID
        {"PREMCURRENCYID", "PREMCURRENCYID"}, // = PREMIUMCURRENCYID
        //{"?", "PREMIUMCURRNUMCODE"}, // ?
        //{"?", "INSAMCURRNAME"}, // ?
        //{"?", "PREMIUMCURRISO"}, // ?
        //{"?", "INSAMCURRISO"}, // ?
        {"PREMVALUE", "PREMVALUE"}, // совпадает с B2B, = PREMIUM
        {"PREMCURRENCYID", "PREMCURRENCYID"}, // совпадает с B2B
        {"INSAMCURRENCYID", "INSAMCURRENCYID"}, // совпадает с B2B
        //{"", "CREATEUSERID"}, // совпадает с B2B
        {"CREATEDATE", "CREATEDATE"}, // совпадает с B2B
        //{"", "UPDATEUSERID"}, // совпадает с B2B
        {"UPDATEDATE", "UPDATEDATE"}, // совпадает с B2B
        //{"", "SELLERID"}, // совпадает с B2B
        {"SIGNDATE", "SIGNDATE"}, // дата подписания договора?
        {"STARTDATE", "STARTDATE"}, // совпадает с B2B
        {"FINISHDATE", "FINISHDATE"}, // совпадает с B2B
        //{"", "PRODCONFID"}, // совпадает с B2B
        //{"", "PAYVARID"},  // совпадает с B2B
        //
        // todo: дополнительные свойства контрактов конкретных продуктов
        //{"", "INSGOAMVALUE"},
        //{"", "INSCOUNT60"},
        //{"", "INSCOUNT70"},
        //{"", "INSCOUNT2"},
        //{"", "ISSPORTENABLED"},
        //{"", "LOCALTZOFFSET"},
        //{"", "TRAVELTYPE"},
        //{"", "DOPPACKLIST"},
        {"PROGSYSNAME", "PRODPROGSYSNAME"}, // системное имя программы страхования?
        //{"", "TERRITORY"},
        //{"", "RISKSYSNAMES"},
        //{"", "sessionId"},
        //{"", "SELLERFIO"},
        {"PROGSYSNAME", "PROGSYSNAME"}, // системное имя программы страхования
        //{"", "SBERPRODCODE"},
        //
        {"DOCUMENTDATE", "DOCUMENTDATE"}, // совпадает с B2B
    //</editor-fold>
    };

    // (0 - старый PRODCONFID, 1 - старый PRODVERID, 2 - новый B2B_PROD.SYSNAME, 3 - новый B2B_PRODVER.PRODCODE, 4 - новый PRODCONFID, 5 - новый PRODVERID)
    public static String[][] versionTableForIDs = {
        {"1050", "1050", "00009", "001", "?", "?"}, // Защита дома Онлайн (старый PRODCONFID = 1050)
        {"1060", "1060", "00010", "002", "?", "?"}, // Защита банковской карты Онлайн (старый PRODCONFID = 1060)
        {"6112", "6117", "B2B_CIB_FOR_YOUTH", "002", "?", "?"}, // Защита банковской карты Онлайн для молодежы (старый PRODCONFID = нет, новый PRODCONFID = 6112)
        {"1070", "1070", "00018", "004", "?", "?"}, // Страхование путешественников Онлайн (старый PRODCONFID = 1070)
        {"1080", "1080", "00039", "001", "?", "?"}, // Защита имущества сотрудников сбербанка Онлайн (старый PRODCONFID = 1080)
        {"1090", "1090", "00029", "001", "?", "?"}, // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» (старый PRODCONFID = 1090)
        {"2100", "2100", "00099", "001", "?", "?"}, // Защита дома премиум Онлайн (старый PRODCONFID = 1100)
            {"50000", "50000", "FAMALYASSETS_RB-FCC0", "122", "?", "?"}, // СБСЖ Верное решение prodconf = 50000
            {"55000", "55000", "SBELT_RTBOX", "111", "?", "?"}, // СБСЖ Ремень безопасности prodconf = 55000
            {"56000", "56000", "RIGHT_CHOICE_RTBOX", "112", "?", "?"}, // Верный выбор
    };

    //public static Map<String, Object> programIDsBySysName = null;
    //public static Map<String, Object> programB2BCodesBySysName = null;
    // список ключей, не требующих преобразования (используются только для дополнительного протоколирования)...
    // ... как при загрузке так и при сохранении (ключ, причина пропуска)
    public static final int nonValueableKeysKeyIndex = 0; // ключ
    public static final int nonValueableKeysReasonIndex = 1; // причина пропуска
    public static final String[][] commonNonValueableKeys = {
        //
        {"INSSYSTEMPASSWORD", "системное поле для вызова веб-сервиса"},
        {"INSSYSTEMLOGIN", "системное поле для вызова веб-сервиса"},
        {"MODULENAME", "системное поле для вызова веб-сервиса"},
        {"needTransaction", "системное поле для вызова веб-сервиса"},
        {"VERBOSELOG", "системное поле для вызова веб-сервиса"},
        {"ISMIGRATION", "системное поле для вызова веб-сервиса"},};
    // ... только при сохранении (ключ, причина пропуска)
    public static final String[][] saveNonValueableKeys = {
        //
        {"PAYMENTSCHEDULELIST", "плановый график платежей - будет создан или перенесен отдельно"},
        {"PAYMENTLIST", "фактический график платежей - будет создан или перенесен отдельно"},
        {"STATESYSNAME", "системное имя текущего состояния договора - будет создано или перенесено отдельно"},
        //
        {"CONTRID", "идентификатор - генерируется при сохранении"},
        {"STATEID", "идентификатор - генерируется при сохранении"},
        {"SELLERID", "идентификатор - генерируется при сохранении"},
        {"CONTRNODEID", "идентификатор - генерируется при сохранении"},
        //
        {"SELLERFIO", "создатель/продавец для всех онлайн-продуктов одинаков, генерируется при сохранении"},
        {"CREATEUSERID", "создатель/продавец для всех онлайн-продуктов одинаков, генерируется при сохранении"},
        {"UPDATEUSERID", "создатель/продавец для всех онлайн-продуктов одинаков, генерируется при сохранении"},
        {"ORGSTRUCTID", "создатель/продавец для всех онлайн-продуктов одинаков, генерируется при сохранении"},
        //
        {"STATENAME", "имя состояния - вторичное свойство действительного состояния"},
        {"PRODUCTNAME", "имя продукта - вторичное свойство конфигурации продукта"},
        {"PRODID", "идентификатор продукта - вторичное свойство конфигурации продукта"},
        {"PRODUCTSYSNAME", "системное имя продукта - вторичное свойство конфигурации продукта"},
        {"PRODUCTVERSIONNAME", "имя версии продукта - вторичное свойство версии продукта"},
        //
        {"PREMIUMCURRNUMCODE", "код валюты премии - вторичное свойство действительной валюты премии"},
        {"PREMIUMCURRISO", "код валюты премии - вторичное свойство действительной валюты премии"},
        {"INSAMCURRNAME", "код валюты суммы - вторичное свойство действительной валюты суммы"},
        {"INSAMCURRISO", "код валюты суммы - вторичное свойство действительной валюты суммы"},
        //
        {"SBERPRODCODE", "код продукта - вторичное свойство конфигурации продукта"},
        //
        {"OBJREGIONTYPE", "тип региона - не предусмотрено сохранение в текущие показатели объекта"},
        {"OBJCITYTYPE", "тип города - не предусмотрено сохранение в текущие показатели объекта"},
        {"OBJDISTRICTTYPE", "тип района - не предусмотрено сохранение в текущие показатели объекта"},
        {"OBJSTREETTYPE", "тип улицы - не предусмотрено сохранение в текущие показатели объекта"},
        {"OBJVILLAGETYPE", "тип деревни - не предусмотрено сохранение в текущие показатели объекта"},
        //
        {"OBJNAME", "имя объекта - вторичное свойство типа объекта"},
        //
        {"LOCALTZOFFSET", "часовой пояс - не предусмотрено сохранение в новую структуру договора"}, //
    };

    protected static double roundSum(Double sum) {
        return ((new BigDecimal(sum.toString())).setScale(2, RoundingMode.HALF_UP)).doubleValue();
    }

    protected static double roundCurrencyRate(Double sum) {
        return ((new BigDecimal(sum.toString())).setScale(4, RoundingMode.HALF_UP)).doubleValue();
    }

    protected String getProdConfIDConvertRules() {
        generateNewIDsByOld();
        return prodConfIDConvertRules;
    }

    protected String getProdVerIDConvertRules() {
        generateNewIDsByOld();
        return prodVerIDConvertRules;
    }

    protected void generateNewIDsByOld() {

        // идентификаторы конфигурации и версии продуктов
        if (prodConfIDConvertRules.isEmpty() || prodVerIDConvertRules.isEmpty()) {

            prodConfIDConvertRules = "";
            prodVerIDConvertRules = "";

            for (int i = 0; i < versionTableForIDs.length; i++) {
                Map<String, Object> params = newMap();
                params.put("SYSNAME", versionTableForIDs[i][2]);
                params.put("PRODCODE", versionTableForIDs[i][3]);
                logger.debug("generateNewIDsByOldParam: " + params.toString());
                try {
                    Map<String, Object> queryResult = this.selectQuery("dsGetProdConfAndVerIDsByProdSysNameAndVerCode", "dsGetProdConfAndVerIDsByProdSysNameAndVerCodeCount", params);
                    logger.debug("generateNewIDsByOldResult: " + queryResult.toString());
                    Map<String, Object> productIDs = WsUtils.getFirstItemFromResultMap(queryResult);
                    if (productIDs != null) {
                        versionTableForIDs[i][4] = getStringParam(productIDs.get("PRODCONFID"));
                        versionTableForIDs[i][5] = getStringParam(productIDs.get("PRODVERID"));
                        prodConfIDConvertRules = prodConfIDConvertRules + versionTableForIDs[i][0] + " > " + versionTableForIDs[i][4] + "; ";
                        prodVerIDConvertRules = prodVerIDConvertRules + versionTableForIDs[i][1] + " > " + versionTableForIDs[i][5] + "; ";
                    } else {
                        logger.debug("generateNewIDsByOldResult: product not support B2B mode");
                    }
                } catch (Exception ex) {
                    logger.error("Исключение при выполнении запроса dsGetProdConfAndVerIDsByProdSysNameAndVerCode(Count):\n", ex);
                }
            }
            prodConfIDConvertRules = prodConfIDConvertRules.substring(0, prodConfIDConvertRules.length() - 2);
            prodVerIDConvertRules = prodVerIDConvertRules.substring(0, prodVerIDConvertRules.length() - 2);

            for (int с = 0; с < commonKeysRelations.length; с++) {
                if ("PRODCONFID".equals(commonKeysRelations[с][0])) {
                    commonKeysRelations[с][3] = prodConfIDConvertRules;
                }
                if ("PRODVERID".equals(commonKeysRelations[с][0])) {
                    commonKeysRelations[с][3] = prodVerIDConvertRules;
                }
            }

        }

        // программы страхования
        //if ((programIDsBySysName == null) || (programB2BCodesBySysName == null)) {
        //    Map<String, Object> params = newMap();
        //    programIDsBySysName = newMap();
        //    programB2BCodesBySysName = newMap();
        //    try {
        //        Map<String, Object> queryResult = this.selectQuery("dsB2BProductProgramBrowseListByParam", "dsB2BProductProgramBrowseListByParamCount", params);
        //        List<Map<String, Object>> programsList = WsUtils.getListFromResultMap(queryResult);
        //        for (Map<String, Object> program : programsList) {
        //            Object sysName = program.get("SYSNAME");
        //            if (sysName != null) {
        //                programIDsBySysName.put(sysName.toString(), program.get("PRODPROGID"));
        //                programB2BCodesBySysName.put(sysName.toString(), program.get("PROGCODE"));
        //            }
        //        }
        //    } catch (Exception ex) {
        //        logger.error("Исключение при выполнении запроса dsB2BProductProgramBrowseListByParam(Count):\n", ex);
        //    }
        //}
    }

    protected void userLogActionCreateEx(String sessionId, String contrId, String action, String note, String value, String param1, String param2, String param3, String login, String password) throws Exception {
        Map<String, Object> qParam = new HashMap<String, Object>();
        qParam.put("ACTION", action);
        qParam.put("NOTE", note);
        qParam.put("CONTRID", contrId);
        // проверяем, если в ид сессии нет "-", значит оно закодировано, декодируем
        if ((sessionId != null) && (!sessionId.contains("-"))) {
            sessionId = base64Decode(sessionId);
        }
        qParam.put("SESSIONID", sessionId);
        qParam.put("VALUE", value);
        // свободно
        qParam.put("PARAM1", param1);
        // url
        qParam.put("PARAM2", param2);
        // prodverid
        qParam.put("PARAM3", param3);
        Map<String, Object> res = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsClientActionLogCreate", qParam, login, password);
        logger.debug("clientActLog " + res.toString());
    }

    protected void getSmsInit() {
        Config config = Config.getConfig(SERVICE_NAME);
        this.smsText = config.getParam("SMSTEXT", "Уважаемый%20клиент,%20Ваш%20пароль%20для%20подтверждения%20введенных%20данных:");
        this.smsUser = config.getParam("SMSUSER", "sberinsur");
        this.smsPwd = config.getParam("SMSPWD", "KD9zVoeR123");
        this.smsFrom = config.getParam("SMSFROM", "SberbankIns");
    }

    protected Integer getIntegerParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Integer.valueOf(bean.toString());
        } else {
            return 0;
        }
    }

    protected boolean getBooleanParam(Object bean, Boolean defVal) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Boolean.valueOf(bean.toString()).booleanValue();
        } else {
            return defVal;
        }
    }

    protected int getSimpleIntParam(Object bean) {
        Integer res = getIntegerParam(bean);
        return res == null ? -1 : res.intValue();
    }

    protected BigInteger getBigIntegerParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigInteger.valueOf(Long.valueOf(bean.toString()).longValue());
        } else {
            return null;
        }
    }

    protected BigDecimal getBigDecimalParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigDecimal.valueOf(Double.valueOf(bean.toString()));
        } else {
            return null;
        }
    }

    protected Long getLongParam(Object bean) {
        if ((bean != null) && (bean instanceof Long)) {
            return (Long) bean;
        } else if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected Date getDateParam(Object date) {
        if (date != null) {
            if (date instanceof Double) {
                return XMLUtil.convertDate((Double) date);
            } else if (date instanceof BigDecimal) {
                return XMLUtil.convertDate((BigDecimal) date);
            }
            return (Date) date;
        } else {
            return null;
        }
    }

    protected String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else {
            return bean.toString();
        }
    }

    protected String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }

    // аналог getStringParam, но с протоколировнием полученного значения
    protected String getStringParamLogged(Map<String, Object> map, String keyName) {
        String paramValue = getStringParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    // аналог getStringParamLogged, но с установкой значения по умолчанию и протоколированием
    protected String getStringOptionalParamLogged(Map<String, Object> map, String keyName, String defaultValue) {
        String paramValue = getStringParam(map, keyName);
        if (paramValue.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Optional parameter %s is empty, default value ('%s') will be used.", keyName, defaultValue));
            }
            paramValue = defaultValue;
        } else if (logger.isDebugEnabled()) {
            logger.debug(keyName + " = " + paramValue);
        }
        return paramValue;
    }

    protected static boolean isCallResultOK(Map<String, Object> callResult) {
        return (callResult != null) && (callResult.get("Status") != null) && ("OK".equalsIgnoreCase(callResult.get("Status").toString()));
    }

    /*@WsMethod()
     public Map<String, Object> dsHabContractBrowseListParamEx(Map<String, Object> params) throws Exception {
     String login = params.get(WsConstants.LOGIN).toString();
     String password = params.get(WsConstants.PASSWORD).toString();
     Map<String, Object> result = null;
     result = this.selectQuery("dsHabContractBrowseListParamEx", "dsHabContractBrowseListParamExCount", params);
     return result;
     }*/
    protected BigDecimal getPromoValueByCodeAndProdVerId(Object promoCode, Long prodVerId, String login, String password) throws Exception {
        BigDecimal result = BigDecimal.ONE;
        if (promoCode != null) {
            if (!promoCode.toString().isEmpty()) {
                Map<String, Object> promoParams = new HashMap<String, Object>();
                Map<String, Object> promoMap = new HashMap<String, Object>();
                promoMap.put("promoCode", promoCode);
                promoParams.put("PROMOMAP", promoMap);
                promoParams.put("PRODVERID", prodVerId);

                Map<String, Object> promoRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsPromoBrowseEx", promoParams, login, password);
                if (promoRes.get("VALUE") == null) {
                    if (promoRes.get(RESULT) != null) {
                        promoRes = (Map<String, Object>) promoRes.get(RESULT);
                    }
                }
                if (promoRes.get("VALUE") != null) {
                    result = getBigDecimalParam(promoRes.get("VALUE"));
                }
            }
        }
        return result;
    }

    private String serializeJSON(Object value) {
        String result = "";
        ObjectMapper mapper = new ObjectMapper();
        if (value != null) {
            boolean error = true;
            try {
                result = mapper.writeValueAsString(value);
            } catch (IOException ex1) {
                logger.error(String.format("Can't deserialize Object (class [%s]) to json ", value.getClass().getName()), ex1);
            }
            if (error) {
                result = "Error serialize object";
                logger.error("Error serialize object ");
            }
        } else {
            result = "Value for serialization is null";
            logger.error("Value for serialization is null");
        }
        return result;
    }

    protected Map<String, Object> saveErrorInLog(String serviceName, String methodName, String contrId, Map<String, Object> params, Map<String, Object> rawres, String login, String password) throws Exception {

        Map<String, Object> logParam = new HashMap<String, Object>();
        Map<String, Object> objMap = new HashMap<String, Object>();
        Map<String, Object> obj = new HashMap<String, Object>();

        obj.put("ACTION", "ERROR");
        obj.put("NOTE", "Сохранение текста ошибки");
        //obj.put("SESSIONTOKEN", sessionId);
        // obj.put("VALUE", obj);
        obj.put("PARAM1", serviceName);
        obj.put("PARAM2", methodName);
        obj.put("PARAM4", login);
        obj.put("PARAM5", password);
        obj.put("CONTRID", contrId);
        // obj.put("PARAM3", obj);
        String rawresStr = "";
        if (params != null) {
            String callParam = serializeJSON(params);
            String resParam = serializeJSON(rawres);
            rawresStr = "params: " + callParam + "\\r\\n\\t result:" + resParam;
        }

        obj.put("RAWDATA", rawresStr);

        objMap.put("obj", obj);
        Map<String, Object> res = this.callExternalService(BIVSBERPOSWS_SERVICE_NAME, "dsAngularUserActionLogCreate", objMap, login, password);

        logParam.put("OBJMAP", objMap);
        return res;
    }

    protected Map<String, Object> doStateTrans(String fromState, String toState, Long contrId, boolean isB2BModeFlag, String login, String password) throws Exception {
        Map<String, Object> transQParam = new HashMap<String, Object>();
        transQParam.put("FROMSTATESYSNAME", fromState);
        transQParam.put("TOSTATESYSNAME", toState);
        String typeSysName = isB2BModeFlag ? "B2B_CONTRACT" : "INS_CONTRACT";
        transQParam.put("TYPESYSNAME", typeSysName);
        transQParam.put("JOIN_TO_SMTYPE", "TRUE");
        transQParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> transres = null;
        Map<String, Object> transQres = this.callService(INSPOSWS_SERVICE_NAME, "dsTransitionsBrowseByParamEx", transQParam, login, password);
        if (transQres.get("SYSNAME") != null) {
            Map<String, Object> transParam = new HashMap<String, Object>();
            transParam.put("TRANSITIONSYSNAME", transQres.get("SYSNAME"));
            transParam.put("TYPESYSNAME", typeSysName);
            transParam.put("DOCUMENTID", contrId);
            transParam.put("CONTRID", contrId);
            if (isB2BModeFlag) {
                transres = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContract_State_MakeTrans", transParam, login, password);
            } else {
                transres = this.callService(INSPOSWS_SERVICE_NAME, "dsContract_State_MakeTrans", transParam, login, password);
            }

        }
        return transres;
    }

    protected Map<String, Object> selectQueryHabContractBrowseListParamEx(Map<String, Object> queryParams, boolean isB2BModeFlag, String login, String password) throws Exception {

        Map<String, Object> contract = null;

        if (isB2BModeFlag) {
            Map<String, Object> methodParams = new HashMap<String, Object>();
            Object externalID = queryParams.get("EXTERNALID");
            if (externalID != null) {
                queryParams.put("orderNum", base64Encode(externalID.toString()));
            }
            queryParams.put("contrId", queryParams.get("CONTRID"));
            methodParams.put("CONTRMAP", queryParams);
            methodParams.put(USEB2B_PARAM_NAME, isB2BModeFlag);
            methodParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> methodResult = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsContractBrowseEx", methodParams, login, password);
            if (methodResult != null) {
                contract = (Map<String, Object>) methodResult.get("CONTRMAP");
            } else {
                saveErrorInLog(BIVSBERPOSWS_SERVICE_NAME, "dsContractBrowseEx", "", methodParams, methodResult, login, password);
                // todo: протоколирование неудачи
            }
        } else {
            queryParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> queryResult = this.selectQuery("dsHabContractBrowseListParamEx", "dsHabContractBrowseListParamExCount", queryParams);
            contract = WsUtils.getFirstItemFromResultMap(queryResult);
        }
        return contract;
    }

    protected Map<String, Object> contractToPaymentState(Map<String, Object> params) throws Exception {
        logger.debug("dsContractToPaymentState start");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> dataMap = (Map<String, Object>) params.get("DATAMAP");
        Map<String, Object> result = new HashMap<String, Object>();

        String hash = dataMap.get("hash").toString();
        String guid = base64Decode(hash);
        Map<String, Object> contrQParam = new HashMap<String, Object>();
        contrQParam.put("EXTERNALID", guid);
        boolean isB2BModeFlag = isB2BMode(params);
        Map<String, Object> contrQRes = selectQueryHabContractBrowseListParamEx(contrQParam, isB2BModeFlag, login, password);
        if ((contrQRes == null) || (contrQRes.get("STATESYSNAME") == null)) {
            //ошибка чтения договора
            saveErrorInLog(BIVSBERPOSWS_SERVICE_NAME, "selectQueryHabContractBrowseListParamEx", "", contrQParam, contrQRes, login, password);
            return null;
        }
        //проверяем возможность перехода
        String fromStateSysName = contrQRes.get("STATESYSNAME").toString();
        String toStateSysName = isB2BModeFlag ? "B2B_CONTRACT_PREPRINTING" : "INS_CONTRACT_TO_PAYMENT";
        Long contrId = Long.valueOf(contrQRes.get("CONTRID").toString());
        logger.debug("makeTrans contrid: " + contrId + " from: " + fromStateSysName + " to: " + toStateSysName);
        result.put("TRANSRES", doStateTrans(fromStateSysName, toStateSysName, contrId, isB2BModeFlag, login, password));
        if (result.get("TRANSRES") != null) {
            logger.debug("TRANSRES: " + result.get("TRANSRES").toString());
        } else if (!fromStateSysName.equals(toStateSysName)) {
            Map<String, Object> callParam = new HashMap<String, Object>();
            callParam.put("fromStateSysName", fromStateSysName);
            callParam.put("toStateSysName", toStateSysName);
            saveErrorInLog(BIVSBERPOSWS_SERVICE_NAME, "doStateTrans", contrQRes.get("CONTRID").toString(), callParam, result, login, password);
            logger.debug("TRANS fail transition not exist");
            return null;
        }

        // производим смену состояния договора
        result.put("CONTRMAP", contrQRes);
        logger.debug("dsContractToPaymentState finish");
        return result;
    }

    protected Map<String, Object> contractToPaidState(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> dataMap = (Map<String, Object>) params.get("DATAMAP");
        Map<String, Object> result = new HashMap<String, Object>();
        logger.debug("dsContractToPaidState start");
        String hash = dataMap.get("hash").toString();
        String guid = base64Decode(hash);
        Map<String, Object> contrQParam = new HashMap<String, Object>();
        contrQParam.put("EXTERNALID", guid);
        logger.debug("EXTERNALID: " + guid);
        boolean isB2BModeFlag = isB2BMode(params);
        Map<String, Object> contrQRes = selectQueryHabContractBrowseListParamEx(contrQParam, isB2BModeFlag, login, password);
        //проверяем возможность перехода
        String fromStateSysName = contrQRes.get("STATESYSNAME").toString();
        String toStateSysName = isB2BModeFlag ? "B2B_CONTRACT_SG" : "INS_CONTRACT_PAID";
        Long contrId = Long.valueOf(contrQRes.get("CONTRID").toString());
        logger.debug("CONTRID: " + contrId);
        logger.debug("Number: " + contrQRes.get("CONTRNUMBER").toString());
        Long contrNodeId = Long.valueOf(contrQRes.get("CONTRNODEID").toString());

        Map<String, Object> paymentRes = (Map<String, Object>) dataMap.get("PAYMENTRES");
        if (paymentRes.get("Result") != null) {
            // если платеж не прошел - переводить договор не нужно
            logger.debug("make trans from: " + fromStateSysName + " to: " + toStateSysName);
            if (!fromStateSysName.equalsIgnoreCase(toStateSysName)) {
                result.put("TRANSRES", doStateTrans(fromStateSysName, toStateSysName, contrId, isB2BModeFlag, login, password));
                logger.debug("TRANSRES: " + result.get("TRANSRES").toString());
            }
            Map<String, Object> paymentResult = (Map<String, Object>) paymentRes.get("Result");
            // сохраним платежный документ
            Map<String, Object> payParams = new HashMap<String, Object>();
            payParams.put("CONTRNODEID", contrNodeId);
            payParams.put("PAYFACTNUMBER", paymentResult.get("REFERENCENUMBER"));//ORDERID_PARAM_NAME));// guid);
            payParams.put("PAYFACTTYPE", 1);
            payParams.put("PAYFACTDATE", new Date());
            payParams.put("AMCURRENCYID", contrQRes.get("PREMIUMCURRENCYID"));
            payParams.put("AMVALUE", contrQRes.get("PREMVALUE"));
            payParams.put("NAME", contrQRes.get("PRODUCTNAME"));
            payParams.put("SERIES", paymentResult.get(ORDERNUMBER_PARAM_NAME));
            payParams.put("NOTE", "Покупка онлайн");

            logger.debug("Create payFact: " + payParams.toString());
            this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentFactCreate", payParams, login, password);

            Map<String, Object> par = new HashMap<String, Object>();

            par.put("SIGNDATE", new Date());
            par.put("CONTRID", contrId);
            this.callService(INSPOSWS_SERVICE_NAME, "dsContractUpdate", par, login, password);
        }

        //создаем запись о фактической оплате                
        if (contrQRes.get("PRODVERID") != null) {
            //если HAB или CIB
            //отправляем по почте
            if (("1050".equals(contrQRes.get("PRODVERID").toString()))
                    || ("1060".equals(contrQRes.get("PRODVERID").toString()))
                    || ("1080".equals(contrQRes.get("PRODVERID").toString()))
                    || ("2100".equals(contrQRes.get("PRODVERID").toString()))) {
                logger.debug("Send email");
                Map<String, Object> printParams = new HashMap<String, Object>();
                printParams.put("CONTRID", contrId);
                dataMap.put("action", "sendEmail");
                dataMap.put("url", "");
                printParams.put("DATAMAP", dataMap);
                Map<String, Object> printRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsCallSendEmail", printParams, login, password);
            }
            //если ВЗР
            //печатаем доки и отправляем по почте
            if ("1070".equals(contrQRes.get("PRODVERID").toString())
                    || "1090".equals(contrQRes.get("PRODVERID").toString())) {
                logger.debug("VZR print Docs and send email");
                Map<String, Object> printParams = new HashMap<String, Object>();
                printParams.put("CONTRID", contrId);
                dataMap.put("action", "sendEmail");
                dataMap.put("url", "");
                printParams.put("DATAMAP", dataMap);
                Map<String, Object> printRes = this.callService(SIGNBIVSBERPOSWS_SERVICE_NAME, "dsCallPringAndSendEx", printParams, login, password);
            }
        }

        logger.debug("dsContractToPaidState finish");

        // производим смену состояния договора
        result.put("CONTRMAP", contrQRes);
        return result;
    }

    protected Map<String, Object> сontractReject(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> dataMap = (Map<String, Object>) params.get("DATAMAP");
        Map<String, Object> result = new HashMap<String, Object>();
        if (dataMap.get("hash") != null) {
            String hash = dataMap.get("hash").toString();
            String guid = base64Decode(hash);
            Map<String, Object> contrQParam = new HashMap<String, Object>();
            contrQParam.put("EXTERNALID", guid);
            boolean isB2BModeFlag = isB2BMode(params);
            Map<String, Object> contrQRes = selectQueryHabContractBrowseListParamEx(contrQParam, isB2BModeFlag, login, password);
            //проверяем возможность перехода

            String fromStateSysName = contrQRes.get("STATESYSNAME").toString();
            String toStateSysName = isB2BModeFlag ? "B2B_CONTRACT_REJECT" : "INS_CONTRACT_REJECT";
            Long contrId = Long.valueOf(contrQRes.get("CONTRID").toString());
            // проверка оплаты в эквайринге\
            if (isContractNotPaid(contrId, login, password)) {


            result.put("TRANSRES", doStateTrans(fromStateSysName, toStateSysName, contrId, isB2BModeFlag, login, password));
            // TODO: добавить запрос в эквайринг, чтобы предотвратить отказ от уже оплаченного договора
            // производим смену состояния договора
                contrQRes.put("REJECTRES", "Покупка полиса отменена");
            result.put("CONTRMAP", contrQRes);
            } else {
                contrQRes.put("REJECTRES", "Отмена покупки не удалась. Платеж уже произведен.");
                result.put("CONTRMAP", contrQRes);
                result.put("TRANSRES", "Contract paid");
            }
        } else {
            logger.debug("try reject but not hash");
        }
        return result;
    }

    public static String base64Decode(String input) {
        Base64 decoder = new Base64(true);
        return bytesToString(decoder.decode(input));
    }

    public static String base64Encode(String input) {
        Base64 encoder = new Base64(true);
        String result = bytesToString(encoder.encode(stringToBytes(input)));
        return result.substring(0, result.length() - 2);
    }

    public static byte[] stringToBytes(String value) {
        byte[] result = null;
        try {
            result = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return result;
    }

    public static String bytesToString(byte[] value) {
        String result = "";
        try {
            result = new String(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return result;
    }

    protected Long сontrNodeСreate(Long rVersion, Long lastVerNumber, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("RVERSION", rVersion);
        params.put("LASTVERNUMBER", lastVerNumber);
        Map<String, Object> res = this.callService(INSPOSWS_SERVICE_NAME, "dsContractNodeCreate", params, login, password);
        return getLongParam(res.get("CONTRNODEID"));

    }

    protected Long contrCreate(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("PRODVERID", contrMap.get("PRODVERID"));
        params.put("CONTRNODEID", contrMap.get("CONTRNODEID"));
        params.put("VERNUMBER", 0L);
        params.put("DOCUMENTDATE", contrMap.get("DOCUMENTDATE"));
        params.put("DECLDATE", contrMap.get("DECLDATE"));
        params.put("PRODPROGID", contrMap.get("PRODPROGID"));
        //страхователь
        params.put("INSUREDID", contrMap.get("PARTICIPANTID"));
        params.put("INSAMVALUE", contrMap.get("INSAMVALUE"));
        params.put("PREMVALUE", contrMap.get("PREMVALUE"));
        params.put("SELLERID", contrMap.get("SELLERID"));

        params.put("ORGSTRUCTID", contrMap.get("ORGSTRUCTID"));
        params.put("SELFORGSTRUCTID", contrMap.get("SELFORGSTRUCTID"));

        params.put("STARTDATE", contrMap.get("STARTDATE"));
        params.put("FINISHDATE", contrMap.get("FINISHDATE"));
        params.put("DURATION", contrMap.get("DURATION"));

        // номер кредитного договора (для страхования ипотеки)
        if (contrMap.get("CREDCONTRNUM") != null) {
            params.put("CREDCONTRNUM", contrMap.get("CREDCONTRNUM"));
        }
        // номера договора страхования
        // (для страхования ипотеки - известны на момент сохранения договора)
        if (contrMap.get("CONTRNUMBER") != null) {
            params.put("CONTRNUMBER", contrMap.get("CONTRNUMBER"));
        }
        if (contrMap.get("CONTRPOLSER") != null) {
            params.put("CONTRPOLSER", contrMap.get("CONTRPOLSER"));
        }
        if (contrMap.get("CONTRPOLNUM") != null) {
            params.put("CONTRPOLNUM", contrMap.get("CONTRPOLNUM"));
        }

        // валюта - Рубль
        if (contrMap.get("PREMCURRENCYID") == null) {
            params.put("PREMCURRENCYID", 1L);
        } else {
            params.put("PREMCURRENCYID", contrMap.get("PREMCURRENCYID"));
        }
        if (contrMap.get("INSAMCURRENCYID") == null) {
            params.put("INSAMCURRENCYID", 1L);
        } else {
            params.put("INSAMCURRENCYID", contrMap.get("INSAMCURRENCYID"));
        }
        if (contrMap.get("INSAMCURRENCYID") == null) {
            params.put("INSAMCURRENCYID", 1L);
        } else {
            params.put("INSAMCURRENCYID", contrMap.get("INSAMCURRENCYID"));
        }
        params.put("CURRENCYRATE", getExchangeCourceByCurID((Long) contrMap.get("INSAMCURRENCYID"), new Date(), login, password));
        params.put("REFERRAL", contrMap.get("REFERRAL"));
        params.put("REFERRALBACK", contrMap.get("REFERRALBACK"));
        params.put("INFORMSUPPORT", contrMap.get("INFORMSUPPORT"));
        Map<String, Object> res = this.callService(INSPOSWS_SERVICE_NAME, "dsContractCreate", params, login, password);
        return getLongParam(res.get("CONTRID"));
    }

    protected void сontrNodeUpdate(Long contrNodeId, Long contrId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONTRNODEID", contrNodeId);
        params.put("CONTRID", contrId);
        this.callService(INSPOSWS_SERVICE_NAME, "dsContractNodeUpdate", params, login, password);
    }

    protected void сontrUpdate(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONTRNUMBER", contrMap.get("CONTRNUMBER"));
        params.put("CONTRPOLSER", contrMap.get("CONTRPOLSER"));
        params.put("CONTRPOLNUM", contrMap.get("CONTRPOLNUM"));
        params.put("PREMVALUE", contrMap.get("PREMVALUE"));
        params.put("INSAMVALUE", contrMap.get("INSAMVALUE"));

        params.put("CONTRID", contrMap.get("CONTRID"));
        this.callService(INSPOSWS_SERVICE_NAME, "dsContractUpdate", params, login, password);
    }

    protected Map<String, Object> participantCreate(Map<String, Object> insurer, String login, String password) throws Exception {
        Map<String, Object> result = null;
        result = participantCustomCreate(insurer, login, password);
        return result;
    }

    private Long findPersonByParams(Map<String, Object> contrMap, String login, String password) throws Exception {
        Long participantId = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("FIRSTNAME", contrMap.get("FIRSTNAME"));
        params.put("MIDDLENAME", contrMap.get("MIDDLENAME"));
        params.put("LASTNAME", contrMap.get("LASTNAME"));
        params.put("BIRTHDATE", contrMap.get("BIRTHDATE"));
        Map<String, Object> res = this.callService(CRMWS_SERVICE_NAME, "personGetListByParams", params, login, password);
        List<Map<String, Object>> exList = WsUtils.getListFromResultMap(res);
        if (exList != null) {
            if (!exList.isEmpty()) {
                if (exList.get(0).get("PARTICIPANTID") != null) {
                    participantId = getLongParam(exList.get(0).get("PARTICIPANTID"));
                }
            }
        }
        return participantId;
    }

    private Map<String, Object> participantCustomCreate(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        // пока продукты только для физ лиц. поэтому тут 0
        params.put("INSUREDTYPE", 0);
        params.put("CITIZENSHIP", contrMap.get("CITIZENSHIP"));
        //имя
        params.put("NAME", contrMap.get("FIRSTNAME"));
        //Отчество
        params.put("MIDDLENAME", contrMap.get("MIDDLENAME"));
        //фамилия
        params.put("SURNAME", contrMap.get("LASTNAME"));
        params.put("BIRTHDATE", contrMap.get("BIRTHDATE"));
        // 0 - male, 1 - female
        params.put("SEX", contrMap.get("SEX"));
        params.put("BIRTHPLACE", contrMap.get("BIRTHPLACE"));
        // документ - всегда пасспорт РФ
        params.put("DOCTYPESYSNAME", contrMap.get("DOCTYPESYSNAME"));//"PassportRF");
        params.put("DOCSERIES", contrMap.get("DOCSERIES"));
        params.put("DOCNUMBER", contrMap.get("DOCNUMBER"));
        params.put("ISSUEDATE", contrMap.get("ISSUEDATE"));
        params.put("ISSUEDBY", contrMap.get("ISSUEDBY"));
        params.put("ISSUERCODE", contrMap.get("ISSUERCODE"));
        params.put("CONTACTPHONEMOBILE", contrMap.get("CONTACTPHONEMOBILE"));
        params.put("CONTACTEMAIL", contrMap.get("CONTACTEMAIL"));

        params.put("ADDRESSDATA", contrMap.get("INSADDRESSDATA"));
        params.put("PROCESSADDRESSPOST", false);
        params.put("PROCESSDL", false);
        params.put("PROCESSREGDOC", false);
        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> result = this.callService(INSPOSWS_SERVICE_NAME, "dsParticipantCustomCreate", params, login, password);
        return result;
    }

    private Map<String, Object> participantCustomModify(Map<String, Object> contrMap, String login, String password) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Map<String, Object> insObjSave(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("ADDRESSDATA", contrMap.get("OBJADDRESSDATA"));
        params.put("OBJTYPEID", contrMap.get("OBJTYPE"));
        if (contrMap.get("OBJTYPE") != null) {
            if (contrMap.get("OBJTYPE").toString().equalsIgnoreCase("1")) {
                params.put("NAME", "Квартира");
            }
            if (contrMap.get("OBJTYPE").toString().equalsIgnoreCase("2")) {
                params.put("NAME", "Дом");
            }
        }
        params.put("CONTRID", contrMap.get("CONTRID"));
        params.put("ROWSTATUS", 1L);

        Map<String, Object> result = this.callService(INSPOSWS_SERVICE_NAME, "dsContractPropertySave", params, login, password);
        return result;
    }

    protected Map<String, Object> findDepByLogin(String login, String password) throws Exception {
        Map<String, Object> qres1 = null;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("LOGIN", login);
        Map<String, Object> qres = this.callService(ADMINWS_SERVICE_NAME, "admAccountFind", queryParams, login, password);
        if (qres.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) qres.get(RESULT);
            if (!resList.isEmpty()) {
                if (resList.get(0).get("USERID") != null) {
                    Long userid = getLongParam(resList.get(0).get("USERID"));
                    Map<String, Object> userInfoParam = new HashMap<String, Object>();
                    userInfoParam.put("USERID", userid);
                    userInfoParam.put("ReturnAsHashMap", "TRUE");
                    qres1 = this.callService(ADMINWS_SERVICE_NAME, "admparticipantbyid", userInfoParam, login, password);
                    qres.putAll(qres1);
                }
            }
        }
        return qres;
    }

    private Map<String, Object> findOrgStructByUser(Long userId, String login, String password) throws Exception {
        Map<String, Object> userInfoParam = new HashMap<String, Object>();
        userInfoParam.put("USERID", userId);
        userInfoParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(ADMINWS_SERVICE_NAME, "admparticipantbyid", userInfoParam, login, password);
        return qres;
    }

    private Map<String, Object> travelRemapFromGate(Map<String, Object> mapIn, String login, String password) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Map<String, Object> obj = (Map<String, Object>) mapIn.get("obj");
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("protocol", obj.get("protocol"));
        result.put("host", obj.get("host"));
        result.put("port", obj.get("port"));
        result.put("localTZOffset", obj.get("localTZOffset"));
        result.put("PRODCONFID", obj.get("prodConfId"));
        result.put("PRODVERID", obj.get("prodVerId"));
        Map<String, Object> master = (Map<String, Object>) obj.get("master");
        String prodProgSysName = master.get("prodProgSysName").toString();
        Map<String, Object> prodProg = getProdProgBySysName(obj, prodProgSysName, login, password);

        result.put("PRODPROGID", prodProg.get("PRODPROGID"));
        result.put("INSAMVALUE", prodProg.get("insAmValue"));

        result.put("PREMVALUE", master.get("premium"));
        boolean travelType = Boolean.valueOf(master.get("travelType").toString()).booleanValue();
        if (travelType) {
            result.put("TRAVELTYPE", 1);
        } else {
            result.put("TRAVELTYPE", 0);
        }
        String currency = master.get("currency").toString();
        result.put("CURRENCYID", getCurrIdByCODE(currency, login, password));

        result.put("STARTDATE", parseDate(master.get("startDate")));
        result.put("FINISHDATE", parseDate(master.get("finishDate")));
        result.put("DURATION", master.get("duration"));
        result.put("insuredCount60", master.get("insuredCount60"));
        result.put("insuredCount70", master.get("insuredCount70"));
        result.put("insuredCount2", master.get("insuredCount2"));
        result.put("isSportEnabled", master.get("isSportEnabled"));
        result.put("dopPackageList", master.get("dopPackageList"));
        result.put("prodProgSysName", master.get("prodProgSysName"));

        Map<String, Object> territory = (Map<String, Object>) master.get("countries");
        result.put("TERRITORYSYSNAME", territory.get("SYSNAME"));

        Map<String, Object> pers = (Map<String, Object>) master.get("persons");
        List<Map<String, Object>> insuredList = (List<Map<String, Object>>) pers.get("insuredList");

        return result;
    }

    private Map<String, Object> remapFromGate(Map<String, Object> contrMapIn) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("protocol", contrMapIn.get("protocol"));
        result.put("host", contrMapIn.get("host"));
        result.put("port", contrMapIn.get("port"));
        result.put("localTZOffset", contrMapIn.get("localTZOffset"));
        result.put("sessionToken", contrMapIn.get("sessionToken"));

        result.put("PRODPROGID", contrMapIn.get("prodProgId"));
        result.put("PRODCONFID", contrMapIn.get("prodConfId"));
        result.put("PRODVERID", contrMapIn.get("prodVerId"));
        result.put("PREMVALUE", contrMapIn.get("insPremVal"));
        result.put("INSAMVALUE", contrMapIn.get("insAmVal"));
        result.put("insGOAmVal", contrMapIn.get("insGOAmVal"));

        result.put("CITIZENSHIP", contrMapIn.get("insCitizenship"));
        result.put("LASTNAME", contrMapIn.get("insSurname"));
        result.put("FIRSTNAME", contrMapIn.get("insName"));
        result.put("MIDDLENAME", contrMapIn.get("insMiddlename"));
        result.put("BIRTHPLACE", contrMapIn.get("insBirthplace"));
        if (contrMapIn.get("insBirthdate") != null) {
            try {
                Date d1 = df.parse(contrMapIn.get("insBirthdate").toString());
                if (d1 == null) {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    d1 = df1.parse(contrMapIn.get("insBirthdate").toString());
                }
                result.put("BIRTHDATE", d1);
            } catch (ParseException ex) {
                try {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    Date d1 = df1.parse(contrMapIn.get("insBirthdate").toString());
                    result.put("BIRTHDATE", d1);

                } catch (ParseException ex1) {
                    try {
                        DateFormat df1 = new SimpleDateFormat("ddMMyyyy");
                        Date d1 = df1.parse(contrMapIn.get("insBirthdate").toString());
                        result.put("BIRTHDATE", d1);

                    } catch (ParseException ex2) {
                        Logger.getLogger(AngularContractCustomFacade.class.getName()).log(Level.SEVERE, null, ex2);
                    }
                }
            }
        }
        result.put("SEX", 0);
        if (contrMapIn.get("insGender") != null) {
            if ("female".equalsIgnoreCase(contrMapIn.get("insGender").toString())) {
                result.put("SEX", 1);
            }
        }

        result.put("CONTACTPHONEMOBILE", contrMapIn.get("insPhone"));
        result.put("PREVCONTACTEMAIL", contrMapIn.get("insEmail"));
        result.put("CONTACTEMAIL", contrMapIn.get("insEmailValid"));

        result.put("DOCTYPESYSNAME", contrMapIn.get("insPassDocType"));
        result.put("DOCSERIES", contrMapIn.get("insPassSeries"));
        result.put("DOCNUMBER", contrMapIn.get("insPassNumber"));

        if (contrMapIn.get("insPassIssueDate") != null) {
            try {
                Date d1 = df.parse(contrMapIn.get("insPassIssueDate").toString());
                if (d1 == null) {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    d1 = df1.parse(contrMapIn.get("insPassIssueDate").toString());
                }
                result.put("ISSUEDATE", d1);
            } catch (ParseException ex) {
                try {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    Date d1 = df1.parse(contrMapIn.get("insPassIssueDate").toString());
                    result.put("ISSUEDATE", d1);

                } catch (ParseException ex1) {
                    try {
                        DateFormat df1 = new SimpleDateFormat("ddMMyyyy");
                        Date d1 = df1.parse(contrMapIn.get("insPassIssueDate").toString());
                        result.put("ISSUEDATE", d1);

                    } catch (ParseException ex2) {
                        Logger.getLogger(AngularContractCustomFacade.class.getName()).log(Level.SEVERE, null, ex2);
                    }
                }
            }
        }

        result.put("ISSUEDBY", contrMapIn.get("insPassIssuePlace"));
        result.put("ISSUERCODE", contrMapIn.get("insPassIssueCode"));

        Map<String, Object> insAddressMap = new HashMap<String, Object>();

        insAddressMap.put("addrSysName", "RegisterAddress");
        insAddressMap.put("eRegion", contrMapIn.get("insAdrRegNAME"));
        insAddressMap.put("regionCode", contrMapIn.get("insAdrRegCODE"));

        insAddressMap.put("eCity", contrMapIn.get("insAdrCityNAME"));
        insAddressMap.put("cityCode", contrMapIn.get("insAdrCityCODE"));

        insAddressMap.put("eStreet", contrMapIn.get("insAdrStrNAME"));
        insAddressMap.put("streetCode", contrMapIn.get("insAdrStrCODE"));
        insAddressMap.put("eIndex", contrMapIn.get("insAdrStrPOSTALCODE"));

        insAddressMap.put("eHouse", contrMapIn.get("insAdrHouse"));
        insAddressMap.put("eCorpus", contrMapIn.get("insAdrHousing"));
        insAddressMap.put("eBuilding", contrMapIn.get("insAdrBuilding"));
        insAddressMap.put("eFlat", contrMapIn.get("insAdrFlat"));
        List<Map<String, Object>> addressList = new ArrayList<Map<String, Object>>();
        addressList.add(insAddressMap);
        result.put("INSADDRESSDATA", addressList);

        Map<String, Object> objAddressMap = new HashMap<String, Object>();
        objAddressMap.put("eRegion", contrMapIn.get("objAdrRegNAME"));
        objAddressMap.put("regionCode", contrMapIn.get("objAdrRegCODE"));

        objAddressMap.put("eCity", contrMapIn.get("objAdrCityNAME"));
        objAddressMap.put("cityCode", contrMapIn.get("objAdrCityCODE"));

        objAddressMap.put("eStreet", contrMapIn.get("objAdrStrNAME"));
        objAddressMap.put("streetCode", contrMapIn.get("objAdrStrCODE"));
        objAddressMap.put("eIndex", contrMapIn.get("objAdrStrPOSTALCODE"));

        objAddressMap.put("eHouse", contrMapIn.get("objAdrHouse"));
        objAddressMap.put("eCorpus", contrMapIn.get("objAdrHousing"));
        objAddressMap.put("eBuilding", contrMapIn.get("objAdrBuilding"));
        objAddressMap.put("eFlat", contrMapIn.get("objAdrFlat"));
        result.put("OBJADDRESSDATA", objAddressMap);
        result.put("OBJTYPE", contrMapIn.get("objTypeId"));

        result.put("validate", contrMapIn.get("validate"));
        XMLUtil.convertDateToFloat(result);
        return result;
    }

    protected Map<String, Object> setRightsOnContr(Map<String, Object> orgMap, Long contrId, String login, String password) throws Exception {
        Long depId = getLongParam(orgMap.get("DEPARTMENTID"));
        Long userAccountId = getLongParam(orgMap.get("USERACCOUNTID"));

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("CONTRID", contrId);
        param.put("ORGSTRUCTID", depId);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsContractOrgStructCreate", param, login, password);
        //CONTRORGSTRUCTID
        Map<String, Object> paramHist = new HashMap<String, Object>();
        paramHist.put("CONTRID", contrId);
        paramHist.put("OLDUSERID", userAccountId);
        paramHist.put("NEWUSERID", userAccountId);
        paramHist.put("OLDORGSTRUCTID", depId);
        paramHist.put("NEWORGSTRUCTID", depId);
        paramHist.put("UPDATETEXT", "Создан договор");
        paramHist.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qresHist = this.callService(INSPOSWS_SERVICE_NAME, "dsContractOrgHistCreate", paramHist, login, password);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("orgRes", qres);
        result.put("orgHistRes", qresHist);
        return result;
    }

    protected Map<String, Object> contrExtCreate(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> result = null;
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("PRODCONFID", contrMap.get("PRODCONFID"));
        param.put("NAME", "CONTREXTDATAVERID");
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductDefaultValueBrowseListByParam", param, login, password);
        //[insproductws]?dsProductDefaultValueBrowseListByParam
        if (qres.get("VALUE") != null) {
            Long contrExtHbDataVerId = getLongParam(qres.get("VALUE"));
            Map<String, Object> paramhb = new HashMap<String, Object>();
            paramhb.put("HBDATAVERID", contrExtHbDataVerId);
            paramhb.put("CONTRID", contrMap.get("CONTRID"));

            paramhb.put("localTZOffset", contrMap.get("localTZOffset"));
            if (contrMap.get("sessionToken") != null) {
                String token = contrMap.get("sessionToken").toString();
                paramhb.put("sessionId", base64Decode(token));
            }

            paramhb.put("promocode", contrMap.get("promoCode"));
            paramhb.put("shareValue", contrMap.get("promoValue"));
            // vzr
            paramhb.put("insuredCount60", contrMap.get("insuredCount60"));
            paramhb.put("insuredCount70", contrMap.get("insuredCount70"));
            paramhb.put("insuredCount2", contrMap.get("insuredCount2"));
            paramhb.put("isSportEnabled", contrMap.get("isSportEnabled"));
            paramhb.put("dopPackageList", contrMap.get("dopPackageList"));
            paramhb.put("prodProgSysName", contrMap.get("prodProgSysName"));
            paramhb.put("territoty", contrMap.get("territoty"));
            paramhb.put("riskSysNames", contrMap.get("riskSysNames"));
            paramhb.put("travelType", contrMap.get("TRAVELTYPE"));

            // sis
            paramhb.put("movDetailAmValSum", contrMap.get("movDetailAmValSum"));
            paramhb.put("movDetailAmValOst", contrMap.get("movDetailAmValOst"));
            paramhb.put("protectTypeList", contrMap.get("protectTypeList"));

            // mortgage
            // дата кредитного договора (для страхования ипотеки)
            paramhb.put("credDate", contrMap.get("credDate"));

            paramhb.put("ReturnAsHashMap", "TRUE");
            result = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordCreate", paramhb, login, password);
            //[instarificatorws]?dsHandbookRecordCreate
        }
        return result;
    }

    private Map<String, Object> contrExtUpdate(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> findRes = dsContrExtBrowse(contrMap, login, password);
        if (findRes.get("CONTREXTID") != null) {
            findRes.put("smsCode", contrMap.get("smsCode"));

            //findRes.put("hid", hid);
            Map<String, Object> updateRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordUpdate", findRes, login, password);
            result.put("updateRes", updateRes);
        }
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsContrExtBrowse(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> result = null;
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("PRODCONFID", contrMap.get("PRODCONFID"));
        param.put("NAME", "CONTREXTDATAVERID");
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductDefaultValueBrowseListByParam", param, login, password);
        if (qres.get("VALUE") != null) {
            Long contrExtHbDataVerId = getLongParam(qres.get("VALUE"));
            Map<String, Object> paramhb = new HashMap<String, Object>();
            paramhb.put("HBDATAVERID", contrExtHbDataVerId);
            paramhb.put("CONTRID", contrMap.get("CONTRID"));

            paramhb.put("ReturnAsHashMap", "TRUE");
            result = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordBrowseListByParam", paramhb, login, password);
            result.put("HBDATAVERID", contrExtHbDataVerId);
        }
        return result;
    }

    protected void readContrExtId(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> paramhb = new HashMap<String, Object>();
        paramhb.put("CONTRID", contrMap.get("CONTRID"));
        paramhb.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> result = this.callService(INSPOSWS_SERVICE_NAME, "dsContractBrowseListByParam", paramhb, login, password);
        contrMap.put("GUID", result.get("EXTERNALID"));
    }

    private boolean checkPayment(Map<String, Object> result, String login, String password) throws Exception {
        if (result.get("payRes") != null) {
            Map<String, Object> paramsLog = new HashMap<String, Object>();
            Map<String, Object> contrMap = (Map<String, Object>) result.get("CONTRMAP");
            paramsLog.put("MERCHANTORDERNUM", result.get("orderGuid"));
            paramsLog.put("OBJECTID", contrMap.get("CONTRID"));
            paramsLog.put("ORDERID", result.get("orderId"));
            paramsLog.put("ReturnAsHashMap", "TRUE");

            Map<String, Object> logRes = this.callService(BIVPOSWS_SERVICE_NAME, "dsPayLogBrowseListByParam", paramsLog, login, password);
            if (logRes.get("PAYLOGID") != null) {
                paramsLog.put("PAYLOGID", logRes.get("PAYLOGID"));
                paramsLog.put("ERRORTEXT", result.get("payRes").toString() + " " + logRes.get("ERRORTEXT").toString());
                this.callService(BIVPOSWS_SERVICE_NAME, "dsPayLogUpdate", paramsLog, login, password);
                return true;
            }
        }
        return false;
    }

    private void saveFactPay(Map<String, Object> result, String login, String password) {
        // фактический платеж должен сохранятся, после поступления средств на счет, т.е. после синхронизации с платежной системой.
    }

    protected void saveEmailFailStatusToContract(Long contrId, String message, boolean isB2BModeFlag, String login, String password) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (contrId != null) {
            params.put("CONTRID", contrId);
            params.put("NOTE", message);
            String serviceName;
            String methodName;
            if (isB2BModeFlag) {
                serviceName = B2BPOSWS_SERVICE_NAME;
                methodName = "dsB2BContrUpdate";
            } else {
                serviceName = INSPOSWS_SERVICE_NAME;
                methodName = "dsContractModify";
            }
            try {
                this.callService(serviceName, methodName, params, login, password);
            } catch (Exception ex) {
                logger.error("saveEmailFailStatusToContract fail", ex);
            }
        }
    }

    protected Map<String, Object> sendReportByEmailInCreate(Map<String, Object> attachmentMap, Map<String, Object> contrMap, String emailText, String email, String sessionId, boolean isB2BModeFlag, String login, String password) throws Exception {
        String allEMail = email;
        String productName = contrMap.get("PRODUCTNAME").toString();
        if (contrMap.get("PRODVERID") != null) {
            Map<String, Object> callParams = new HashMap<String, Object>();
            callParams.put("PRODVERID", contrMap.get("PRODVERID"));
            callParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> productInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductVersionBrowseListByParamEx", contrMap, login, password);
            if (productInfo.get("PRODSYSNAME") != null) {
                Config config = Config.getConfig(SERVICE_NAME);
                String emailList = config.getParam(productInfo.get("PRODSYSNAME").toString() + "EMAILS", "");
                if (!emailList.isEmpty()) {
                    allEMail += "," + emailList;
                }
            }
        }
        Map<String, Object> sendRes = null;
        if (isAllEmailValid(allEMail)) {

            String contrNum = contrMap.get("CONTRNUMBER").toString();
            Map<String, Object> sendParams = new HashMap<String, Object>();
            sendParams.put("SMTPSubject", "Договор страхования №" + contrNum);
            sendParams.put("SMTPMESSAGE", "В приложении договор страхования №" + contrNum + ", правила страхования и памятка страхователя.");
            sendParams.put("SMTPReceipt", allEMail);
            //sendParams.put("SMTPReceipt", "sambucusfehu@gmail.com");
            if (attachmentMap != null) {
                if (attachmentMap.get(RESULT) != null) {
                    Map<String, Object> attachList = (Map<String, Object>) attachmentMap.get(RESULT);
                    if (attachList.get("REPORTDATALIST") != null) {
                        attachList.remove("REPORTDATALIST");
                    }
                    sendParams.put("ATTACHMENTMAP", attachList);
                } else {
                    sendParams.put("ATTACHMENTMAP", attachmentMap);
                }
            }
            logger.debug("sendParams = " + sendParams.toString());
            sendParams.put("HTMLTEXT", emailText);

            saveEmailFailStatusToContract(getLongParam(contrMap.get("CONTRID")), " ", isB2BModeFlag, login, password);
            userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Попытка отправки почты", "Договор страхования №" + contrNum, "", allEMail, "", "", login, password);
            try {
                boolean isError = false;
                saveEmailInFile(sendParams, login, password);
                //   sendRes = this.callService("emailws", "dsSendEmailMessage", sendParams, login, password);
                sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                    sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                    if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                        sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                        if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                            sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                            if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                                sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                                if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                                    //отправка письма не удалась, сохранить в файл, если проставлен соответсвтующий параметр
                                    // saveEmailInFile(sendParams, login, password);
                                    isError = true;
                                    saveEmailFailStatusToContract(getLongParam(contrMap.get("CONTRID")), "EMAIL SEND FAIL", isB2BModeFlag, login, password);
                                    userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Почта не отправлена", "Договор страхования №" + contrNum, "", allEMail, "", "", login, password);
                                    logger.debug("mailSendFail");
                                }
                            }
                        }
                    }
                }
                if (!isError) {
                    logger.debug("mailSendSuccess");
                    userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Почта отправлена", "Договор страхования №" + contrNum, "", allEMail, "", "", login, password);
                }
            } catch (Exception e) {
                logger.debug("mailSendException: ", e);
                userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Почта не отправлена", "Договор страхования №" + contrNum, "", allEMail, "", "", login, password);
                //отправка письма не удалась, сохранить в файл, если проставлен соответсвтующий параметр
                saveEmailInFile(sendParams, login, password);
                saveEmailFailStatusToContract(getLongParam(contrMap.get("CONTRID")), "EMAIL SEND FAIL", isB2BModeFlag, login, password);
            }
        }
        return sendRes;
    }

    protected Map<String, Object> sendReportByEmail(Map<String, Object> attachmentMap, Map<String, Object> resMap, String email, boolean isB2BModeFlag, String login, String password) throws Exception {
        String respect = "Уважаемый";
        String fio = "клиент";
        String productName = "";
        String contrNum = "";
        Long contrId = null;
        if (resMap.get("CONTRMAP") != null) {
            Map<String, Object> contrMap = (Map<String, Object>) resMap.get("CONTRMAP");
            contrId = getLongParam(contrMap.get("CONTRID"));
            if ("1".equals(contrMap.get("INSGENDER").toString())) {
                respect = "Уважаемая";
            }
            fio = contrMap.get("INSBRIEFNAME").toString();
            productName = contrMap.get("PRODUCTNAME").toString();
            contrNum = contrMap.get("CONTRNUMBER").toString();
        }
        Map<String, Object> sendParams = new HashMap<String, Object>();
        sendParams.put("SMTPSubject", "Договор страхования №" + contrNum);
        sendParams.put("SMTPMESSAGE", "В приложении договор страхования №" + contrNum + ", правила страхования и памятка страхователя.");
        sendParams.put("SMTPReceipt", email);
        Map<String, Object> sendRes = null;
        if (isAllEmailValid(email)) {

            if (attachmentMap.get(RESULT) != null) {
                sendParams.put("ATTACHMENTMAP", attachmentMap.get(RESULT));
            }
            String htmlStr = "<html><body><h1>" + respect + " " + fio + "</h1><div>Поздравляем вас "
                    + "с приобретением страхового продукта " + productName + ", в приложенных файлах вы найдете ваш "
                    + "договор страхования №" + contrNum + ", памятку страхователя и правила страхования</div></body></html>";
            sendParams.put("HTMLTEXT", htmlStr);

            saveEmailInFile(sendParams, login, password);
            sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
            if (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString())) {
                sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                if (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString())) {
                    sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                    if (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString())) {
                        sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                        if (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString())) {
                            sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                            saveEmailFailStatusToContract(contrId, "EMAIL SEND FAIL", isB2BModeFlag, login, password);
                        }
                    }
                }
            }
        }
        return sendRes;
    }

    protected String getIsurerEmail(Map<String, Object> contrObj, String login, String password) throws Exception {
        if (contrObj.get("INSUREDID") != null) {
            Map<String, Object> qParam = new HashMap<String, Object>();
            qParam.put("PARTICIPANTID", contrObj.get("INSUREDID"));
            Map<String, Object> qRes = this.callService(CRMWS_SERVICE_NAME, "contactGetListByParticipantId", qParam, login, password);
            List<Map<String, Object>> contactList = WsUtils.getListFromResultMap(qRes);
            for (Map<String, Object> contact : contactList) {
                if ("PersonalEmail".equalsIgnoreCase(contact.get("CONTACTTYPESYSNAME").toString())) {
                    contrObj.put("PersonalEmail", contact.get("VALUE").toString());
                }
                if ("MobilePhone".equalsIgnoreCase(contact.get("CONTACTTYPESYSNAME").toString())) {
                    contrObj.put("MobilePhone", contact.get("VALUE").toString());
                }
            }

//            logger.debug("empty email for insuredid = " + contrObj.get("INSUREDID").toString());
        }
        return "";
    }

    protected String getMetadataURL(String login, String password, String project) throws Exception {
        Map args = new HashMap();
        args.put("fileType", "REPORTS");
        args.put(PROJECT_PARAM_NAME, project);
        String adminwsURL = Config.getConfig().getParam("adminws", "http://localhost:8080/adminws/adminws");
        String COREWSURL = Config.getConfig().getParam("corews", "http://localhost:8080/corews/corews");
        try {
            XMLUtil xmlutil = new XMLUtil(login, password);
            args.put("PROJECTSYSNAME", project);
            Map resultMap = xmlutil.doURL(adminwsURL, "admProjectBySysname", xmlutil.createXML(args), null);

            List result = (List) resultMap.get("Result");
            if (logger.isDebugEnabled()) {
                logger.debug("projectBySysname result = " + result);
            }
            if ((result != null) && (result.size() == 1)
                    && (((Map) result.get(0)).containsKey("METADATAURL")) && ((((Map) result.get(0)).get("METADATAURL") instanceof String))
                    && (!((Map) result.get(0)).get("METADATAURL").toString().equals(""))) {
                return ((Map) result.get(0)).get("METADATAURL").toString();
            }

            resultMap = xmlutil.doURL(COREWSURL, "getSystemMetadataURL", xmlutil.createXML(args), null);
            if (logger.isDebugEnabled()) {
                logger.debug("getMetadataURL result = " + resultMap);
            }
            if (resultMap.containsKey("MetadataURL")) {
                return resultMap.get("MetadataURL").toString();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
        return null;
    }

    protected String getProjectName(String login, String password) throws Exception {
        String result = null;
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> namesList = new ArrayList();
        namesList.add("DEFAULT_PROJECT_SYSNAME");
        params.put("NAMES", namesList);
        params.put("LOGIN", login);
        Map<String, Object> res = callService(WsConstants.COREWS, "dsAccountSettingFindByLoginAndName", params, login, password);
        if ((res != null) && (res.get("Status") != null) && (res.get("Status").equals("OK"))) {
            result = (String) res.get("DEFAULT_PROJECT_SYSNAME");
        }
        return result;
    }

    protected String generateEmailText(String url, String hash, Map<String, Object> contrMap, String login, String password) throws Exception {
        return generateEmailTextEx(url, hash, contrMap, "HTMLMAILPATH", login, password);
    }

    protected Map<String, Object> getProductDefaultValueByProdConfId(Object prodConfId, boolean isB2BModeFlag, String login, String password) throws Exception {
        Map<String, Object> result;
        Map<String, Object> productConfQueryParams = new HashMap<String, Object>();
        productConfQueryParams.put(RETURN_AS_HASH_MAP, true);
        if (isB2BModeFlag) {
            Object b2bProdConfID = convertValue(prodConfId, getProdConfIDConvertRules(), Direction.TO_SAVE);
            productConfQueryParams.put("PRODCONFID", b2bProdConfID);
            result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueByProdConfId", productConfQueryParams, login, password);
        } else {
            productConfQueryParams.put("PRODCONFID", prodConfId);
            result = this.callService(INSPRODUCTWSWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", productConfQueryParams, login, password);
        }
        return result;
    }

    protected static String getRejectURLFromURL(String url) {
        String indexHTMLStr = "/index.html#/";
        String regExp = Pattern.quote(indexHTMLStr) + ".*";
        String result = url.replaceFirst(regExp, indexHTMLStr + "reject");
        return result;
    }

    protected static String getURLWithParam(String url, String paramName, String paramValue) {
        String result;
        if (url.indexOf("#top") > 0) {
            url = url.replace("#top", "");
        }
        if (url.contains("?")) {
            result = url + "&" + paramName + "=" + paramValue;
        } else {
            result = url + "?" + paramName + "=" + paramValue;
        }
        return result;
    }

    protected String generateEmailTextEx(String url, String hash, Map<String, Object> contrMap, String mailName, String login, String password) throws Exception {
        String project = this.getProjectName(login, password);
        String metadataURL = getMetadataURL(login, password, project);

        String fName;

        // загрузка url из конфига продукта.
        //Map<String, Object> productConfigRes = this.callService(INSPRODUCTWSWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", productConfQueryParams, login, password);
        Map<String, Object> productConfigRes = getProductDefaultValueByProdConfId(contrMap.get("PRODCONFID"), isB2BMode(contrMap), login, password);
        String prodConfId = "null";
        if (contrMap.get("PRODCONFID") != null) {
            prodConfId = contrMap.get("PRODCONFID").toString();
        }
        logger.debug("emailText getting: prodCondId - " + prodConfId + " mainName - " + mailName + " defValRes:" + productConfigRes.toString());
        if ((productConfigRes != null) && (productConfigRes.get(mailName) != null)) {
            fName = (String) productConfigRes.get(mailName);
        } else {
            fName = "";
        }

        String fullPath = metadataURL + fName;
        File input = new File(fullPath);
        if (input.exists() && !fName.isEmpty() && input.getCanonicalPath().startsWith(metadataURL)) {
            logger.debug("emailText fullPath: " + fullPath + " isB2B: " + String.valueOf(isB2BMode(contrMap)));
            //BufferedReader reader = new BufferedReader(new FileReader(fullPath));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fullPath), "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            if ("1".equals(contrMap.get("INSGENDER").toString())) {
                contrMap.put("GENDERNAME", "Уважаемая");
            } else {
                contrMap.put("GENDERNAME", "Уважаемый");
            }
            String fio = contrMap.get("INSFIRSTNAME").toString();
            if (contrMap.get("INSMIDDLENAME") != null) {
                fio = fio + " " + contrMap.get("INSMIDDLENAME").toString();
            }
            contrMap.put("FIO", fio);
            contrMap.put("URL", getURLWithParam(url, "hash", hash));
            String urlReject = getRejectURLFromURL(url);
            contrMap.put("URLREJECT", getURLWithParam(urlReject, "hash", hash));
            //получить из настроек системы пути к сервисам
            Map<String, Object> coreParams = new HashMap<String, Object>();
            Map<String, Object> coreSettings = this.callService(COREWS, "getSysSettings", coreParams, login, password);
            if (coreSettings != null) {
                if (coreSettings.get(RESULT) != null) {
                    List<Map<String, Object>> coreSettingList = (List<Map<String, Object>>) coreSettings.get(RESULT);
                    if (!coreSettingList.isEmpty()) {
                        for (Map<String, Object> coreSetting : coreSettingList) {
                            String sysName = getStringParam(coreSetting.get("SETTINGSYSNAME"));
                            if (sysName.indexOf("URL") == 0) {
                                // системное имя начинается с URL, добавляем значения в мапу для замен в шаблоне письма
                                contrMap.put(sysName, getURLWithParam(getStringParam(coreSetting.get("SETTINGVALUE")), "hash", hash));
                            }
                        }
                    }
                }
            }

            String resString = sb.toString();
            for (Map.Entry<String, Object> entry : contrMap.entrySet()) {
                String string = entry.getKey();
                Object object = entry.getValue();
                if (object != null) {
                    resString = resString.replaceAll("\\Q!" + string + "!\\E", object.toString());
                }
            }
            return resString;
        } else {
            String respect = "Уважаемый";
            String fio = "клиент";
            String productName = "";
            String contrNum = "";
            if ("1".equals(contrMap.get("INSGENDER").toString())) {
                respect = "Уважаемая";
            }//contrMap.get("INSLASTNAME").toString() + " " + 
            fio = contrMap.get("INSFIRSTNAME").toString();
            if (contrMap.get("INSMIDDLENAME") != null) {
                fio = fio + " " + contrMap.get("INSMIDDLENAME").toString();
            }
            // productName = contrMap.get("PRODUCTNAME").toString();
            // contrNum = contrMap.get("CONTRNUMBER").toString();

            StringBuilder sb = new StringBuilder();
            sb.append("<html><body>");
            sb.append("<span style=\"color:black\">");
            sb.append(respect + " " + fio + ",");
            sb.append("<br>");
            sb.append("<br>");
            sb.append("Мы знаем, что у Вас есть выбор. Спасибо, что выбрали нас!");
            sb.append("<br>");
            sb.append("<br>");
            sb.append("Вы - в одном шаге от обеспечения себя надежной защитой «Сбербанк Страхование».");
            sb.append("<span class=\"im\">");
            sb.append("<span>");
            sb.append("<br>");
            sb.append("<br>");
            sb.append("Во вложении – Ваш Страховой полис и Правила страхования.");
            sb.append("<br>");
            sb.append("Перед оплатой просим Вас ознакомиться с направленными документами.");
            sb.append("<br>");
            sb.append("</span>");
            sb.append("</span>");
            sb.append("Если Вы согласны с условиями страхования, ");
            sb.append("</span>");
            sb.append("<span>");
            sb.append("<a href=\"" + getURLWithParam(url, "hash", hash) + "\">");
            sb.append("перейдите, пожалуйста, к оплате");
            sb.append("</a>");
            sb.append("<span><span style=\"color:windowtext;text-decoration:none\">.</span></span>");
            sb.append("</span>");
            sb.append("<span style=\"color:black\">");
            sb.append("<br>");
            sb.append("Если данное письмо адресовано не Вам, ");
            sb.append("</span>");
            sb.append("<span>");
            String urlReject = getRejectURLFromURL(url);
            sb.append("<a href=\"" + getURLWithParam(urlReject, "hash", hash) + "\">");
            sb.append("отказаться от покупки можно здесь");
            sb.append("</a>");
            sb.append("</span>");
            sb.append("<span style=\"color:black\">");
            sb.append(". Надеемся, Вы вернетесь к нам.");
            sb.append("<span class=\"im\">");
            sb.append("<span>");
            sb.append("<br>");
            sb.append("<br>");
            sb.append("Обращаем внимание, для вступления договора/полиса страхования в действие необходимо его оплатить.");
            sb.append("<br>");
            sb.append("</span>");
            sb.append("</span>");
            sb.append("Если у Вас возникли вопросы или трудности, пожалуйста, обращайтесь к нам по телефону: ");
            sb.append("<a href=\"tel:8-800-555-55-50\" target=\"_blank\">8-800-555-55-50</a>");
            sb.append("&nbsp;(<wbr>бесплатно для звонков по России).");
            sb.append("<span class=\"im\">");
            sb.append("<span>");
            sb.append("<br>");
            sb.append("<br>");
            sb.append("С уважением и наилучшими пожеланиями,");
            sb.append("<br>");
            sb.append("</span>");
            sb.append("</span>");
            sb.append("Команда ООО СК «Сбербанк страхование»");
            sb.append("</span>");
            sb.append("</body></html>");

            return sb.toString();
        }

    }

    /* protected Map<String, Object> sendSms(String phone, Map<String, Object> contrQRes, String sessionId, String login, String password) throws Exception {
     String code = generateRandomCode();
     logger.debug("sms code: " + code);

     contrQRes.put("smsCode", code);
     contrExtUpdate(contrQRes, login, password);
     getSmsInit();
     String message = this.smsText + " " + code;
     if (phone.length() == 10) {
     phone = "7" + phone;
     }
     SmsSender smssender = new SmsSender();
     logger.debug("sms message: " + message);
     userLogActionCreateEx(sessionId, getStringParam(contrQRes.get("CONTRID")), "Отправка СМС", message, code, phone, "", "", login, password);
     Map<String, Object> sendRes = smssender.doGet(this.smsUser, this.smsPwd, this.smsFrom, phone, message);
     //Map<String, Object> sendRes = smssender.sendSms(phone, message);
     Map<String, Object> result = new HashMap<String, Object>();
     result.put("SENDRES", sendRes);
     result.put("STATUS", "OK");
     return result;
     }*/
    protected Map<String, Object> sendSms(String phone, Map<String, Object> contrQRes, String sessionId, String login, String password) throws Exception {
        String code = generateRandomCode();
        logger.debug("sms code: " + code);
        contrQRes.put("smsCode", code);

        // сохранение в договор сгенерированного СМС-кода
        if (isB2BMode(contrQRes)) {
            Map<String, Object> contrB2BQRes = newMap();
            contrB2BQRes.put("CONTRID", contrQRes.get("CONTRID"));
            contrB2BQRes.put("EXTERNALID", contrQRes.get("EXTERNALID"));
            contrB2BQRes.put("SMSCODE", code);
            this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrUpdate", contrB2BQRes, login, password);
        } else {
            contrExtUpdate(contrQRes, login, password);
        }

        getSmsInit();
        String message = this.smsText + " " + code;
        if (phone.length() == 10) {
            phone = "7" + phone;
        }
        SmsSender smssender = new SmsSender();
        logger.debug("sms message: " + message);
        userLogActionCreateEx(sessionId, getStringParam(contrQRes.get("CONTRID")), "Отправка СМС", message, code, phone, "", "", login, password);
        Map<String, Object> sendRes = smssender.doGet(this.smsUser, this.smsPwd, this.smsFrom, phone, message);
        //Map<String, Object> sendRes = smssender.sendSms(phone, message);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("SENDRES", sendRes);
        result.put("STATUS", "OK");
        return result;
    }

    private String generateRandomCode() {
        Random r = new Random();
        int code = r.nextInt(9999);
        NumberFormat nf = new DecimalFormat("0000");
        String result = nf.format(code);

        //String result = String.valueOf(code);
        return result;//"1122";
    }

    protected Long getSellerId(Map<String, Object> userInfo, String login, String password) throws Exception {
        Map<String, Object> sellerParam = new HashMap<String, Object>();
        sellerParam.put("EMPLOYEEID", userInfo.get("EMPLOYEEID"));
        sellerParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> sellerRes = this.callService(INSPOSWS_SERVICE_NAME, "dsEmployeeSellerBrowseListByParam", sellerParam, login, password);
        Long result = null;
        if (sellerRes.get("SELLERID") != null) {
            result = Long.valueOf(sellerRes.get("SELLERID").toString());
        }
        return result;
    }

    private Long updateTravelInsured(Map<String, Object> params, String login, String password) throws Exception {
        Long result = null;

        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.putAll(params);
        createParams.put("ReturnAsHashMap", "TRUE");
        createParams.remove("INSOBJID");
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsTravelInsuredCreate", createParams, login, password);

        // апдейтим ноду, устанавливая текущим застрахованным только что вставленный
        if (qres != null) {
            result = Long.valueOf(qres.get("INSOBJID").toString());

            Long travelInsuredNodeId = null;
            if (null != params.get("INSOBJNODEID")) {
                travelInsuredNodeId = Long.valueOf(params.get("INSOBJNODEID").toString());
            }
            // updatим ноду на текущий дривер            
            Map<String, Object> updateParams = new HashMap<String, Object>();
            updateParams.put("INSOBJNODEID", travelInsuredNodeId);
            updateParams.put("INSOBJID", result);
            params.put("INSOBJID", result);
            this.callService(INSPOSWS_SERVICE_NAME, "dsInsuranceObjectNodeUpdate", updateParams, login, password);
        }
        return result;
    }

    private Long insertTravelInsured(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> nodeParams = new HashMap<String, Object>();
        Long result = null;
        nodeParams.put("ReturnAsHashMap", "TRUE");
        nodeParams.put("LASTVERNUMBER", 0);
        nodeParams.put("RVERSION", 0);
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsInsuranceObjectNodeCreate", nodeParams, login, password);
        if (qres != null) {
            result = Long.valueOf(qres.get("INSOBJNODEID").toString());
            params.put("INSOBJNODEID", result);
            params.put("VERNUMBER", 0);
            result = this.updateTravelInsured(params, login, password);
        }
        return result;
    }

    private Long insertContractObject(Map<String, Object> params, String login, String password) throws Exception {
        Long result = null;
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.putAll(params);
        createParams.put("ReturnAsHashMap", "TRUE");
        createParams.remove("CONTROBJID");
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsContractObjectCreate", createParams, login, password);
        if (qres != null) {
            result = Long.valueOf(qres.get("CONTROBJID").toString());
        }
        return result;
    }

    protected void saveRisk(Map<String, Object> risk, String login, String password) throws Exception {
        this.callService(INSPOSWS_SERVICE_NAME, "dsContractRiskCreate", risk, login, password);
    }


    private Map<String, Object> getProdProgBySysName(Map<String, Object> hbMapIn, String prodProgSysName, String login, String password) throws Exception {
        Map<String, Object> qRes = null;
        if ((hbMapIn.get("prodConfId") != null) && (hbMapIn.get("prodVerId") != null)) {
            Map<String, Object> qParam = new HashMap<String, Object>();
            qParam.put("PRODCONFID", hbMapIn.get("prodConfId"));
            qParam.put("PRODVERID", hbMapIn.get("prodVerId"));
            qParam.put("ReturnAsHashMap", "TRUE");
            qParam.put("SYSNAME", prodProgSysName);
            qRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductProgramBrowseListByParamWithExtProp", qParam, login, password);
        }
        return qRes;
    }

    protected Date parseDate(Object map) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date result = null;
        if (map != null) {
            Map<String, Object> mapIn = (Map<String, Object>) map;
            if (mapIn.get("dateLocaleStr") != null) {
                try {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    String date = mapIn.get("dateLocaleStr").toString();
                    if (date.indexOf(".") < 0) {
                        date = date.substring(0, 2) + "." + date.substring(2, 4) + "." + date.substring(4);
                    }
                    result = df1.parse(date);
                } catch (ParseException ex) {
                    if (mapIn.get("date") != null) {
                        try {
                            result = df.parse(mapIn.get("date").toString());
                        } catch (ParseException ex1) {
                            Logger.getLogger(AngularContractCustomFacade.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                }
            } else if (mapIn.get("dateStr") != null) {
                try {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    String date = mapIn.get("dateStr").toString();
                    if (date.indexOf(".") < 0) {
                        date = date.substring(0, 2) + "." + date.substring(2, 4) + "." + date.substring(4);
                    }
                    result = df1.parse(date);
                } catch (ParseException ex) {
                    if (mapIn.get("date") != null) {
                        try {
                            result = df.parse(mapIn.get("date").toString());
                        } catch (ParseException ex1) {
                            Logger.getLogger(AngularContractCustomFacade.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                }
            } else if (mapIn.get("date") != null) {
                try {
                    result = df.parse(mapIn.get("date").toString());
                } catch (ParseException ex) {
                    Logger.getLogger(AngularContractCustomFacade.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    private Long getCurrIdByCODE(String currency, String login, String password) {
        Long result = 2L;
        if ("USD".equalsIgnoreCase(currency)) {
            result = 2L;
        }
        if ("EUR".equalsIgnoreCase(currency)) {
            result = 3L;
        }
        return result;
    }

    protected void remapInsurer(Map<String, Object> insurer) {
        insurer.put("CITIZENSHIP", insurer.get("citizenship"));
        insurer.put("LASTNAME", insurer.get("surname"));
        insurer.put("FIRSTNAME", insurer.get("name"));
        insurer.put("MIDDLENAME", insurer.get("middlename"));
        insurer.put("BIRTHDATE", parseDate(insurer.get("birthDate")));
        insurer.put("SEX", 0);
        if (insurer.get("gender") != null) {
            if ("female".equalsIgnoreCase(insurer.get("gender").toString())) {
                insurer.put("SEX", 1);
            }
        }

        Map<String, Object> cont = (Map<String, Object>) insurer.get("contacts");

        insurer.put("CONTACTPHONEMOBILE", cont.get("phone"));

        insurer.put("PREVCONTACTEMAIL", cont.get("email"));
        insurer.put("CONTACTEMAIL", cont.get("emailValid"));

        Map<String, Object> pass = (Map<String, Object>) insurer.get("passport");

        insurer.put("DOCTYPESYSNAME", pass.get("typeId"));
        insurer.put("DOCSERIES", pass.get("series"));
        insurer.put("DOCNUMBER", pass.get("number"));
        insurer.put("ISSUEDATE", parseDate(pass.get("issueDate")));

        insurer.put("ISSUEDBY", pass.get("issuePlace"));
        // insurer.put("ISSUERCODE", pass.get("insPassIssueCode"));
    }

    private void sendEmailCurrencyFail(String email, Date date, boolean usdExist, boolean euroExist, String login, String password) {
        Map<String, Object> sendParams = new HashMap<String, Object>();
        sendParams.put("SMTPSubject", "Ошибка сервиса обновления курсов валют");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateStr = sdf.format(date);
        String currNames = "";
        if (!usdExist) {
            currNames = "USD ";
        }
        if (!euroExist) {
            currNames = currNames + "EUR ";
        }
        sendParams.put("SMTPMESSAGE", "На дату " + dateStr + " отсутствует курс валют: " + currNames + ". Проверьте работу сервиса получения курсов валют от центробанка.");
        sendParams.put("SMTPReceipt", email);
        if (isAllEmailValid(email)) {
            logger.debug("sendParams = " + sendParams.toString());
            Map<String, Object> sendRes = null;

            try {
                saveEmailInFile(sendParams, login, password);

                sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                    sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                    if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                        sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                        if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                            sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                            if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                                sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                            }
                        }
                    }
                }
                logger.debug("mailSendSuccess");
            } catch (Exception e) {
                saveEmailInFile(sendParams, login, password);
                logger.debug("mailSendException: ", e);
            }
            //return sendRes;        
        }
    }

    protected Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }

    protected Double getDoubleParam(Map<String, Object> map, String keyName) {
        Double doubleParam = 0.0;
        if (map != null) {
            doubleParam = getDoubleParam(map.get(keyName));
        }
        return doubleParam;
    }

    // аналог getDoubleParam, но с протоколировнием полученного значения
    protected Double getDoubleParamLogged(Map<String, Object> map, String keyName) {
        Double paramValue = getDoubleParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }



    protected Double getExchangeCourceByCurID(Long currencyId, Date date, String login, String password) throws Exception {
        Double result = 1.0;

        Map<String, Object> curParams = new HashMap<String, Object>();
        curParams.put("CurrencyID", currencyId);
        Map<String, Object> curRes = this.callService(REFWS_SERVICE_NAME, "getCurrencyByParams", curParams, login, password);
        List<Map<String, Object>> curList = WsUtils.getListFromResultMap(curRes);
        if (curList != null) {
            if (!curList.isEmpty()) {
                String curCode = getStringParam(curList.get(0).get("Brief"));
                Map<String, Object> exParams = new HashMap<String, Object>();
                exParams.put("natCurCode", curCode);
                exParams.put("QuotedCurrencyID", 1);
                exParams.put("Date", date);
                Map<String, Object> exRes = this.callService(REFWS_SERVICE_NAME, "getCurrencyPairForCrossCourseByParams", exParams, login, password);
                List<Map<String, Object>> exList = WsUtils.getListFromResultMap(exRes);
                if (exList != null) {
                    if (!exList.isEmpty()) {
                        if (exList.get(0).get("COURSEVALUE") != null) {
                            Date cd = getDateParam(exList.get(0).get("COURSEDATE"));
                            GregorianCalendar gc = new GregorianCalendar();
                            gc.setTime(cd);
                            gc.set(Calendar.HOUR_OF_DAY, 0);
                            gc.set(Calendar.MINUTE, 0);
                            gc.set(Calendar.SECOND, 0);
                            gc.set(Calendar.MILLISECOND, 0);
                            GregorianCalendar cgc = new GregorianCalendar();
                            cgc.setTime(date);
                            cgc.set(Calendar.HOUR_OF_DAY, 0);
                            cgc.set(Calendar.MINUTE, 0);
                            cgc.set(Calendar.SECOND, 0);
                            cgc.set(Calendar.MILLISECOND, 0);
                            result = getDoubleParam(exList.get(0).get("COURSEVALUE"));
                            if (gc.getTime().compareTo(cgc.getTime()) != 0) {

                                //даты не равны отправляем сообщение об ошибке
                                Map<String, Object> sysParam = new HashMap<String, Object>();
                                sysParam.put("SETTINGSYSNAME", "NOTIFICATIONEMAILS");
                                sysParam.put("ReturnAsHashMap", "TRUE");
                                Map<String, Object> sysRes = this.callService(COREWS_SERVICE_NAME, "getSysSettingBySysName", sysParam, login, password);
                                if (sysRes.get("SETTINGVALUE") != null) {
                                    String emails = sysRes.get("SETTINGVALUE").toString();
                                    String[] emailList = emails.split(",");
                                    boolean usdExist = true;
                                    boolean euroExist = true;
                                    if ("EUR".equals(curCode)) {
                                        euroExist = false;
                                    }
                                    if ("USD".equals(curCode)) {
                                        usdExist = false;
                                    }
                                    for (String email : emailList) {
                                        //4. отправляем на указанные адреса уведомления о отсутсвии курса валюты на заданную дату.
                                        sendEmailCurrencyFail(email, date, usdExist, euroExist, login, password);
                                    }
                                }
                            }

                        }

                    }
                }
            }
        }
        return result;
    }

    private void remapContr(Map<String, Object> contrMap, Map<String, Object> master, String login, String password) throws Exception {
        String prodProgSysName = master.get("prodProgSysName").toString();
        Map<String, Object> prodProg = getProdProgBySysName(contrMap, prodProgSysName, login, password);

        contrMap.put("PRODPROGID", prodProg.get("PRODPROGID"));
        contrMap.put("INSAMVALUE", prodProg.get("insAmValue"));

        contrMap.put("PREMVALUE", master.get("premium"));
        boolean travelType = Boolean.valueOf(master.get("travelType").toString()).booleanValue();
        if (travelType) {
            contrMap.put("TRAVELTYPE", 1);
        } else {
            contrMap.put("TRAVELTYPE", 0);
        }
        String currency = master.get("currency").toString();
        Long currId = getCurrIdByCODE(currency, login, password);
        contrMap.put("INSAMCURRENCYID", currId);
        // валюта премии - Рубль
        contrMap.put("PREMCURRENCYID", 1);
        Double exchangeRate = getExchangeCourceByCurID(currId, new Date(), login, password);
        contrMap.put("CURRENCYRATE", exchangeRate);

        contrMap.put("STARTDATE", parseDate(master.get("startDate")));
        contrMap.put("FINISHDATE", parseDate(master.get("finishDate")));
        // костыль от заказчика. если территория NoUSARF NoRF дюратион + 15 дней не должен превышать год.
        Long duration = Long.valueOf(master.get("duration").toString());
        Map<String, Object> countries = (Map<String, Object>) master.get("countries");
        String countrySysName = countries.get("SYSNAME").toString();
        contrMap.put("territoty", countrySysName);

        if ("NoUSARF".equalsIgnoreCase(countrySysName) || "NoRF".equalsIgnoreCase(countrySysName)) {
            if ((contrMap.get("STARTDATE") != null) && (contrMap.get("FINISHDATE") != null)) {
                Date sd = (Date) contrMap.get("STARTDATE");
                Date fd = (Date) contrMap.get("FINISHDATE");
                Date fdShengen = new Date();
                fdShengen.setTime(fd.getTime() + 15 * 24 * 60 * 60 * 1000);

                GregorianCalendar sdgc = new GregorianCalendar();
                sdgc.setTime(sd);
                sdgc.set(Calendar.YEAR, sdgc.get(Calendar.YEAR) + 1);
                sdgc.set(Calendar.DATE, sdgc.get(Calendar.DATE) - 1);

                if (fdShengen.getTime() > sdgc.getTimeInMillis()) {
                    fdShengen.setTime(sdgc.getTimeInMillis());
                }
                contrMap.put("FINISHDATE", fdShengen);
            }
        }

        /*    if ("NoUSARF".equalsIgnoreCase(countrySysName) || "NoRF".equalsIgnoreCase(countrySysName)) {
         Date sd = parseDate(master.get("startDate"));
         Date maxfd = new Date(sd.getTime());
         GregorianCalendar gcfd = new GregorianCalendar();
         gcfd.setTime(maxfd);
         gcfd.set(Calendar.YEAR, gcfd.get(Calendar.YEAR) + 1);
         gcfd.set(Calendar.DATE, gcfd.get(Calendar.DATE) - 1);
         Long yearDayCount = (gcfd.getTimeInMillis() - sd.getTime()) * 24 * 60 * 60 * 1000;
         duration = duration + 15;
         if (duration > yearDayCount) {
         duration = yearDayCount;
         }
         }*/
        contrMap.put("DURATION", duration);

        contrMap.put("insuredCount60", master.get("insuredCount60"));
        contrMap.put("insuredCount70", master.get("insuredCount70"));
        contrMap.put("insuredCount2", master.get("insuredCount2"));
        contrMap.put("isSportEnabled", master.get("isSportEnabled"));
        contrMap.put("dopPackageList", master.get("dopPackageList"));
        contrMap.put("prodProgSysName", master.get("prodProgSysName"));
        contrMap.put("riskSysNames", master.get("riskSysNames"));

        Map<String, Object> territory = (Map<String, Object>) master.get("countries");
        contrMap.put("TERRITORYSYSNAME", territory.get("SYSNAME"));
        contrMap.put("PRODPROGID", contrMap.get("PRODPROGID"));

    }

    private void remapInsured(Map<String, Object> insured) {
        insured.put("LASTNAME", insured.get("surname"));
        insured.put("FIRSTNAME", insured.get("name"));
        insured.put("BIRTHDATE", parseDate(insured.get("birthDate")));
        insured.put("ALTNAME", insured.get("namestr"));

    }

    protected String generateContrNum(Long prodConfId, String login, String password) throws Exception {
        String result = "";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ReturnAsHashMap", "TRUE");
        param.put("NAME", "CONTRAUTONUMBERSYSNAME");
        param.put("PRODCONFID", prodConfId);
        logger.debug("getting autoNumSysName");
        Map<String, Object> res = this.callService(INSPRODUCTWSWS_SERVICE_NAME, "dsProductDefaultValueBrowseListByParam", param, login, password);
        if (res.get("VALUE") != null) {
            String autoNumSysName = res.get("VALUE").toString();
            logger.debug("autoNumSysName = " + autoNumSysName);
            Map<String, Object> param1 = new HashMap<String, Object>();
            param1.put("SYSTEMBRIEF", autoNumSysName);
            Map<String, Object> res1 = this.callService(COREWS, "dsNumberFindByMask", param1, login, password);
            if (res1.get("Result") != null) {
                result = res1.get("Result").toString();
            }
        }

        return result;
    }

    protected boolean checkRequiredFields(Map<String, Object> contrMapIn, Map<String, String> contrEmptyRequiredFields, String[][] requiredFields) {
        boolean result = true;
        for (String[] requiredField : requiredFields) {
            String sysName = requiredField[0];
            String name = requiredField[1];
            if (!checkMap(contrMapIn, sysName)) {
                contrEmptyRequiredFields.put(sysName, name);
                result = false;
            }
        }
        return result;
    }

    /**
     * рекурсивная функция проверки наличия поля в мапе.
     *
     * @param contrMapIn - проверяемый массив
     * @param sysName - имя или неймспейс обязательного поля.
     *
     * @return
     */
    private boolean checkMap(Map<String, Object> contrMapIn, String sysName) {
        return checkMap(contrMapIn, sysName, true);
    }

    /**
     *
     * @param contrMapIn - проверяемый массив
     * @param sysName - имя или неймспейс обязательного поля.
     * @param serchInSubMap - флаг необходимо ли пытаться найти поле в подмапах
     *
     * @return
     */
    private boolean checkMap(Map<String, Object> contrMapIn, String sysName, boolean serchInSubMap) {
        boolean result = false;
        // цикл по содержимому мапы
        for (Map.Entry<String, Object> entry : contrMapIn.entrySet()) {
            String entryName = entry.getKey();
            Object entryValue = entry.getValue();
            String firstSysName = sysName;
            String lastSysName = "";
            // если в сиснэйм есть точка, то ищем первую часть, 
            // если находим - то она должна содержать мапу, в которой надо 
            // поискать вторую часть.
            if (sysName.indexOf(".") > 0) {
                firstSysName = sysName.substring(0, sysName.indexOf("."));
                lastSysName = sysName.substring(sysName.indexOf(".") + 1);
            }

            //if (sysName.indexOf(".") < 0) {
            // простая проверка.
            // если имя содержит | то надо проверить на наличие хотябы одно из свойств перечисленных через |
            boolean compareSysNameRes = false;
            if (firstSysName.indexOf("-") > 0) {
                String[] firstSysNameArr = firstSysName.split("-");
                List<String> firstList = new ArrayList<String>(Arrays.asList(firstSysNameArr));
                compareSysNameRes = firstList.contains(entryName);
            } else {
                compareSysNameRes = entryName.equals(firstSysName);
            }

            if (compareSysNameRes) {
                if (entryValue != null) {
                    if (lastSysName.isEmpty()) {
                        // если точки нет - то lastSysName пустое, значит иерархию проверять дальше не надо - проверяем соответствие мапы.
                        if (!entryValue.toString().isEmpty()) {
                            result = true;
                            return result;
                        }
                    } else {
                        if (entryValue instanceof Map) {
                            // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                            if (checkMap((Map<String, Object>) entryValue, lastSysName, false)) {
                                result = true;
                                return result;
                            }
                        }
                        if (entryValue instanceof List) {
                            // например застрахованные
                            List<Object> entryAsList = (List) entryValue;
                            for (Object entryMap : entryAsList) {
                                // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                                if (checkMap((Map<String, Object>) entryMap, lastSysName, false)) {
                                    result = true;
                                } else {
                                    // если ошибка хотя бы в одном элементе массива - то вся проверяемая иерархия ошибочна выводим ошибку
                                    result = false;
                                    return result;
                                }
                            }
                            return result;
                        }
                    }
                }
            } else if (entryValue instanceof Map) {
                if (serchInSubMap) {
                    if (checkMap((Map<String, Object>) entryValue, sysName)) {
                        result = true;
                        return result;
                    }
                }
            }
        }
        return result;
    }

    private String getUploadFilePath() {

        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();

        return result;
    }

    private void saveEmailInFile(Map<String, Object> sendParams, String login, String password) {
        // получаем значение флага сохранения из конфига bivsberposws
        Config config = Config.getConfig(SERVICE_NAME);
        String saveEmailInFile = config.getParam("SAVEEMAILINFILE", "FALSE");
        if ("true".equalsIgnoreCase(saveEmailInFile)) {
            // если флаг взведен, делаем файл по пути uploadpath 
            // получаем путь к uploadpath
            String upPath = getUploadFilePath();
            // сохраняем в данный файл текст письма
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");
            String email = getStringParam(sendParams.get("SMTPReceipt"));
            String htmlText = getStringParam(sendParams.get("HTMLTEXT"));
            if (htmlText.isEmpty()) {
                htmlText = getStringParam(sendParams.get("SMTPMESSAGE"));
            }
            email.replace("@", "_");

            BufferedOutputStream bufferedOutput = null;
            FileOutputStream fileOutputStream = null;
            try {
                String fileName = sdf.format(now) + "_" + email + ".html";
                fileOutputStream = new FileOutputStream(upPath + fileName);//".txt"
                bufferedOutput = new BufferedOutputStream(fileOutputStream);
                //Start writing to the output stream
                byte[] ba = null;
                //String codePage = "";
                    ba = htmlText.getBytes();
//                } else {
//                    ba = htmlText.getBytes(codePage);
//                }
                bufferedOutput.write(ba);
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            } finally {
                //Close the BufferedOutputStream
                try {
                    if (bufferedOutput != null) {
                        bufferedOutput.flush();
                        bufferedOutput.close();
                    }
                } catch (IOException ex) {
                }
            }
        }
    }

    Long createNode(String login, String password) throws Exception {
        Map<String, Object> nodeParams = new HashMap<String, Object>();
        nodeParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        nodeParams.put("RVERSION", 0L);
        nodeParams.put("LASTVERNUMBER", 0L);
        Map<String, Object> nodeRes = this.callService(Constants.INSPOSWS, "dsInsuranceObjectNodeCreate", nodeParams, login, password);
        return Long.valueOf(nodeRes.get("INSOBJNODEID").toString());
    }

    void updateNodeActiveVersion(Long insObjNodeId, Long insObjId, String login, String password) throws Exception {
        Map<String, Object> insObjNodeUpdParams = new HashMap<String, Object>();
        insObjNodeUpdParams.put("INSOBJNODEID", insObjNodeId);
        insObjNodeUpdParams.put("INSOBJID", insObjId);
        this.callService(Constants.INSPOSWS, "dsInsuranceObjectNodeUpdate", insObjNodeUpdParams, login, password);
    }

    protected void mapInsObjAddress(Map<String, Object> insObjAddress) {
        //Map<String, Object> objAddressMap = new HashMap<String, Object>();
        Map<String, Object> region = (Map<String, Object>) insObjAddress.get("region");
        insObjAddress.put("eRegion", region.get("NAME"));
        insObjAddress.put("regionCode", region.get("CODE"));

        Map<String, Object> city = (Map<String, Object>) insObjAddress.get("city");
        insObjAddress.put("eCity", city.get("NAME"));
        insObjAddress.put("cityCode", city.get("CODE"));

        Map<String, Object> street = (Map<String, Object>) insObjAddress.get("street");
        insObjAddress.put("eStreet", street.get("NAME"));
        insObjAddress.put("streetCode", street.get("CODE"));
        insObjAddress.put("eIndex", street.get("POSTALCODE"));

        insObjAddress.put("eHouse", insObjAddress.get("house"));
        insObjAddress.put("eCorpus", insObjAddress.get("housing"));
        insObjAddress.put("eBuilding", insObjAddress.get("building"));
        insObjAddress.put("eFlat", insObjAddress.get("flat"));
    }

    private Map<String, Object> getRiskFromObj(Map<String, Object> insObj, String construct) {
        Map<String, Object> result = null;
        List<Map<String, Object>> riskList = (List<Map<String, Object>>) insObj.get("protectLevelList");
        CopyUtils.sortByStringFieldName(riskList, "SYSNAME");
        List<Map<String, Object>> riskListSorted = CopyUtils.filterSortedListByStringFieldName(riskList, "SYSNAME", construct);
        if (!riskListSorted.isEmpty()) {
            result = riskListSorted.get(0);
        }
        return result;
    }

    protected Map<String, Object> newMap() {
        return new HashMap<String, Object>();
    }

    protected List<Map<String, Object>> newList() {
        return new ArrayList<Map<String, Object>>();
    }

    protected Object chainedGet(Map<String, Object> map, String[] keys) {
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

    protected Object chainedCreativeGet(Map<String, Object> map, String[] keys) {
        Object element = map;
        for (int i = 0; i < keys.length; i++) {
            if (element instanceof Map) {
                Map elementAsMap = (Map) element;
                Object nextElement = elementAsMap.get(keys[i]);
                if (nextElement == null) {
                    Map<String, Object> createdMap = newMap();
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

    protected Object chainedGet(Map<String, Object> map, String keysChain) {
        String[] keys = keysChain.split("\\.");
        return chainedGet(map, keys);
    }

    // аналог chainedGet, но с проверкой двух вариантов регистра конечного ключа - как был передан и все заглавные (*.someKeyName и *.SOMEKEYNAME)
    protected Object chainedGetIgnoreCase(Map<String, Object> map, String keysChain) {
        String[] keys = keysChain.split("\\.");
        Object result = chainedGet(map, keys);
        if (result == null) {
            result = chainedGet(map, getKeysChainWithLastKeyUpperCase(keys));
        }
        return result;
    }

    protected String[] getKeysChainWithLastKeyUpperCase(String[] keys) {
        String[] result = keys;
        int lastKeyIndex = result.length - 1;
        result[lastKeyIndex] = result[lastKeyIndex].toUpperCase();
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
        if ((updatedMap != null) && (updatedMap instanceof Map)/*&& (((Map) updatedMap).get(putKey) == null)*/) {
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

    // получение из списка последнего элемента у которого в attrName храниться значение attrValue
    protected Object getLastElementByAtrrValue(List<Map<String, Object>> list, String attrName, String attrValue) {
        for (int i = list.size() - 1; i >= 0; i--) {
            Map element = list.get(i);
            if (attrValue.equalsIgnoreCase(element.get(attrName).toString())) {
                return element;
            }
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

        List<Map<String, Object>> list = (List<Map<String, Object>>) listObj;
        List<String> sysNames = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            Object newSysName = list.get(i).get(sysNameKey);
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
            expandMap = newMap();
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

        Object newListObj = chainedCreativePut(source, oldMapKeys, newList());

        //logger.debug("mapKeysChain = " + mapKeysChain);
        //logger.debug("sysNameKey = " + sysNameKey);
        Map<String, Object> oldMap = ((Map<String, Object>) oldMapObj);
        List<Map<String, Object>> newList = (List<Map<String, Object>>) newListObj;
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

    protected Object convertValue(Object convertedValueObj, String convertRulesStr, Direction direction) {
        if (convertedValueObj == null) {
            return null;
        }
        String convertedValueStr = convertedValueObj.toString();
        String[] convertRules = convertRulesStr.split("; ");
        String[][] convertTable = new String[convertRules.length][2];
        for (int i = 0; i < convertRules.length; i++) {
            convertTable[i] = convertRules[i].split(" > ");
        }

        String defaultValue = null;
        for (int j = 0; j < convertTable.length; j++) {
            if (convertedValueStr.equalsIgnoreCase(convertTable[j][direction.fromIndex])) {
                return convertTable[j][direction.toIndex];
            } else if ("*".equals(convertTable[j][direction.fromIndex])) {
                defaultValue = convertTable[j][direction.toIndex];
            }
        }

        if (defaultValue != null) {
            return defaultValue;
        }

        return convertedValueObj;
    }

    protected Map<String, Object> getB2Bproduct(String prodConfId, Direction direction, String login, String password) throws Exception {

        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        String methodName = "getB2Bproduct";
        logger.debug("Вызван метод " + methodName + " с параметрами:\n\n" + "prodConfId: " + prodConfId + "; direction: " + direction.toString() + "\n");

        Map<String, Object> productParams = new HashMap<String, Object>();
        generateNewIDsByOld();
        productParams.put("PRODCONFID", convertValue(prodConfId, getProdConfIDConvertRules(), direction));
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByID", productParams, login, password);

        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        logger.debug("Метод " + methodName + " выполнился за " + callTimer + " мс. и вернул результат:\n\n" + product.toString() + "\n");

        return product;
    }

    // генерация дополнительных вычисляемых параметров
    protected void genAdditionalParams(Map<String, Object> contract, Map<String, Object> product, int oldProdConfId, Direction direction, boolean isMigration, boolean isVerboseLog, String login, String password) throws Exception {
    }

    // генерация дополнительных вычисляемых параметров, если не были возвращены в явном виде при загрузке
    protected void genAdditionalLoadParams(Map<String, Object> contract, Map<String, Object> product, int oldProdConfId) {
        // хеш
        Object hash = contract.get("HASH");
        if (hash == null) {
            String externalID = getStringParam(contract.get("EXTERNALID"));
            if (!externalID.isEmpty()) {
                contract.put("HASH", base64Encode(externalID));
            }
        }
    }

    protected void setOverridedParam(Map<String, Object> paramParent, String paramName, Object newValue, boolean isVerboseLog) {
        if (isVerboseLog) {
            Object oldValue = paramParent.get(paramName);
            logParamOverriding(paramName, newValue, oldValue);
        }
        paramParent.put(paramName, newValue);
    }

    protected void setGeneratedParam(Map<String, Object> paramParent, String paramName, Object newValue, boolean isVerboseLog) {
        paramParent.put(paramName, newValue);
        if (isVerboseLog) {
            logParamGeneration(paramName, newValue);
        }
    }

    private void logParamGeneration(String paramName, Object newValue) {
        logger.debug("");
        if (newValue != null) {
            logger.debug("Значение параметра '" + paramName + "' не найдено во входных данных. Для указанного параметра сгенерировано новое значение: " + newValue + " (" + newValue.getClass().getSimpleName() + ").");
        } else {
            logger.debug("Значение параметра '" + paramName + "' не найдено во входных данных. Для указанного параметра сгенерировано новое значение: " + newValue + ".");
        }
    }

    private void logParamOverriding(String paramName, Object newValue, Object oldValue) {
        logger.debug("");
        StringBuilder logStr = new StringBuilder();
        logStr.append("Значение параметра '");
        logStr.append(paramName);
        logStr.append("' из входных данных (");
        logStr.append(oldValue);
        logStr.append(") проигонорировано. Для указанного параметра сгенерировано новое значение: ");
        logStr.append(newValue);
        if (newValue != null) {
            logStr.append(" (");
            logStr.append(newValue.getClass().getSimpleName());
            logStr.append(")");
        }
        logStr.append(".");
        logger.debug(logStr.toString());
    }



    private String genAdditionalProgramSaveParams(Map<String, Object> contract, Map<String, Object> product, boolean isVerboseLog) {
        // если передано системное имя программы - переопределение идентификатора программы и её кода в параметрах договора
        Object programSysNameObj = contract.get("PROGSYSNAME");
        String programSysName = "";
        if ((programSysNameObj != null) && (product != null)) {
            programSysName = programSysNameObj.toString();
            Object programID = chainedGet(product, "PRODVER.PRODPROGS." + programSysName + ".PRODPROGID");
            //contract.put("PRODPROGID", programIDsBySysName.get(programSysName));
            //contract.put("PRODPROGID", programID);
            setOverridedParam(contract, "PRODPROGID", programID, isVerboseLog);
            Object programB2BCode = chainedGet(product, "PRODVER.PRODPROGS." + programSysName + ".PROGCODE");
            //chainedCreativePut(contract, "CONTREXTMAP.insuranceProgram", programB2BCodesBySysName.get(programSysName));
            if (isVerboseLog) {
                logParamOverriding("CONTREXTMAP.insuranceProgram", programB2BCode, chainedGet(contract, "CONTREXTMAP.insuranceProgram"));
            }
            chainedCreativePut(contract, "CONTREXTMAP.insuranceProgram", programB2BCode);
        }
        return programSysName;
    }

    private List<Map<String, Object>> prepareRiskList(String riskSysNames) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (!riskSysNames.isEmpty()) {
            String[] riskArray = riskSysNames.split(",");
            if ((riskArray != null) && (riskArray.length > 0)) {
                for (String bean : riskArray) {
                    Map<String, Object> riskMap = new HashMap<String, Object>();
                    riskMap.put("PRODRISKSYSNAME", bean);
                    result.add(riskMap);
                }
            }
        }
        return result;
    }

    private void addInsured(List<Map<String, Object>> insuredList, int insuredCount, int ageId, List<Map<String, Object>> preparedRiskSysNames) {
        if (insuredCount > 0) {
            Map<String, Object> insuredMap = new HashMap<String, Object>();
            insuredMap.put("AGEID", ageId);
            insuredMap.put("riskList", preparedRiskSysNames);
            insuredList.add(insuredMap);
        }
    }

    // генерация дополнительных вычисляемых сумм для продукта 'Страхование путешественников Онлайн'
    private void genAdditionalTravelSumSaveParams(Map<String, Object> contract, boolean isVerboseLog, String login, String password) throws Exception {

        //Long calcVerId = getLongParam(chainedGet(product, "CALCVERID"));
        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.putAll((Map<String, Object>) contract.get("CALCPARAMS"));

        // переопределение валют страховых суммы и премии по территории страхования
        Long newCurrencyID;
        String territorySysName = getStringParam(calcParams.get("territorySysName"));
        if (("NoRF".equals(territorySysName)) || ("RFSNG".equals(territorySysName))) {
            newCurrencyID = 2L; // для территорий 'РФ и страны СНГ' и 'Весь мир кроме РФ' валюта страховых суммы и премии - доллары
        } else {
            newCurrencyID = 3L; // для всех остальныех програм валюта страховых суммы и премии - евро
        }
        setOverridedParam(contract, "INSAMCURRENCYID", newCurrencyID, isVerboseLog);
        setOverridedParam(contract, "PREMCURRENCYID", newCurrencyID, isVerboseLog);

        // список выбранных переменных рисков
        String riskSysNames = getStringParam(calcParams.remove("RISKSYSNAMES"));
        // + базовые риски, не возвращаемые оригинальным методом загрузки договора, но всегда включаемые в договор (так называемые базовые опции)
        riskSysNames = riskSysNames + ",VZRmedKidsEvac,VZRmedTransCosts,VZRmedDocLoss,VZRmedTranslator,VZRmedVisit,VZRmedSearchRescue,VZRmedMessages,VZRmedHotel";
        List<Map<String, Object>> preparedRiskList = prepareRiskList(riskSysNames);
        // в новой версии калькулятора insuredList всегда состоит из одного элемента, содержащего только список с рисками
        // (а количество застрахованных по возрасатам передается через обычные параметры калькулятора)
        List<Map<String, Object>> insuredList = new ArrayList();
        Map<String, Object> insuredMap = new HashMap<String, Object>();
        insuredMap.put("riskList", preparedRiskList);
        insuredList.add(insuredMap);
        calcParams.put("insuredList", insuredList);
        // вызов калькулятора
        calcParams.put(RETURN_AS_HASH_MAP, true);
        //logger.debug("calcParams: " + calcParams);
        Map<String, Object> calcRes = this.callService(WsConstants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcParams, login, password);
        //logger.debug("calcRes (calculateByCalculatorVersionID): " + calcRes);
        //contract.put("DEBUGCALCRES", calcRes);

        // получение и округление перед сохранением курса валют
        Double currencyRate = roundCurrencyRate(getDoubleParam(contract.get("CURRENCYRATE")));
        setOverridedParam(contract, "CURRENCYRATE", currencyRate, isVerboseLog);

        // определение коэффициента скидки
        Double shareValue = null;
        Object shareValueObj = contract.get("SHAREVALUE");
        if (shareValueObj != null) {
            shareValue = getDoubleParam(shareValueObj);
            logger.debug("Во входных параметрах обнаружен коэффициент скидки, имеющий значение: " + shareValue + "; все суммы страховых премий будут вычислены с его учетом.");
        }

        Map<String, Double> premListAsMapBySysName = new HashMap<String, Double>();
        Double roundedRisksTotalSum = 0.0D;
        if (isCallResultOK(calcRes)) {
            List<Map<String, Object>> insuredCalculatedList = (List<Map<String, Object>>) calcRes.get("insuredList");
            if ((insuredCalculatedList != null) && (insuredCalculatedList.size() == 1)) {
                Map<String, Object> insured = insuredCalculatedList.get(0);
                List<Map<String, Object>> riskList = (List<Map<String, Object>>) insured.get("riskList");
                for (Map<String, Object> risk : riskList) {
                    String riskSysName = getStringParam(risk.get("PRODRISKSYSNAME"));
                    Double riskRoundedSum = getDoubleParam(risk.get("PREMIUM")); // округление не требуется, выполнено в калькуляторе
                    if (shareValue != null) {
                        riskRoundedSum = roundSum(riskRoundedSum * shareValue); // округление, если применен коэффициент скидки
                    }
                    roundedRisksTotalSum += riskRoundedSum;
                    premListAsMapBySysName.put(riskSysName, riskRoundedSum);
                }
            }
        }

        // Исправленние суммы по определенному корректируемому риску
        // (для устранения расхождения итоговых страховых премий, вызванного погрешностями при округленях сумм по рискам и при переводах по курсам валют)
        Double oldContractTotalSum = getDoubleParam(contract.get("PREMVALUE"));
        if (oldContractTotalSum > 0) {
            logger.debug("Cтраховая премия (PREMVALUE) из старого договора в рублях = " + oldContractTotalSum);
            String medCorrectedRiskSysName = "VZRmedical";
            Double medCorrectedRiskSum = premListAsMapBySysName.get(medCorrectedRiskSysName);
            if (medCorrectedRiskSum != null) {
                Double oldContractTotalSumInNewCurrency = roundSum(oldContractTotalSum / currencyRate);
                logger.debug("Cтраховая премия (PREMVALUE) из старого договора в валюте нового договора = " + oldContractTotalSumInNewCurrency);
                roundedRisksTotalSum = roundSum(roundedRisksTotalSum);
                logger.debug("Итоговая сумма по всем рискам для нового договора в валюте нового договора = " + roundedRisksTotalSum);
                Double correctionValue = oldContractTotalSumInNewCurrency - roundedRisksTotalSum; // округление для correctionValue не требуется - определяется разность уже округленных сумм
                logger.debug("Значение коррекции (величина расхождения итоговых страховых премий, вызванная погрешностями при округленях сумм по рискам и при переводах по курсам валют) = " + correctionValue);
                logger.debug("Исходная сумма по корректируемому риску (c системным наименованием '" + medCorrectedRiskSysName + "') в валюте нового договора = " + medCorrectedRiskSum);
                medCorrectedRiskSum = medCorrectedRiskSum + correctionValue; // округление для medCorrectedRiskSum не требуется - определяется сумма/разность уже округленных сумм
                logger.debug("Исправленная сумма по корректируемому риску (c системным наименованием '" + medCorrectedRiskSysName + "') = " + medCorrectedRiskSum);
                premListAsMapBySysName.put(medCorrectedRiskSysName, medCorrectedRiskSum);
            }
        }

        Map<String, Object> сalculatorHBParams = new HashMap<String, Object>();
        сalculatorHBParams.put("CALCVERID", calcParams.get("CALCVERID"));
        сalculatorHBParams.put("NAME", "Ins.Vzr.Risk.Limits");
        Map<String, Object> hbRecordsParams = new HashMap<String, Object>();
        hbRecordsParams.put("territorySysName", calcParams.get("territorySysName"));
        hbRecordsParams.put("programSysName", calcParams.get("programSysName"));
        hbRecordsParams.put("travelKind", calcParams.get("travelKind"));
        сalculatorHBParams.put("PARAMS", hbRecordsParams);
        //logger.debug("сalculatorHBParams: " + сalculatorHBParams);
        Map<String, Object> qRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", сalculatorHBParams, login, password);
        //logger.debug("qRes (dsGetCalculatorHandbookData): " + qRes);
        //contract.put("DEBUGLIMITSHBRES", qRes);

        Map<String, Double> limitListAsMapBySysName = new HashMap<String, Double>();
        if (isCallResultOK(qRes)) {
            List<Map<String, Object>> limitList = WsUtils.getListFromResultMap(qRes);
            if (!limitList.isEmpty()) {
                for (Map<String, Object> limit : limitList) {
                    Double sum = getDoubleParam(limit.get("limit"));
                    String riskSysName = getStringParam(limit.get("elRiskSysName"));
                    if (riskSysName.isEmpty()) {
                        riskSysName = getStringParam(limit.get("riskSysName"));
                    }
                    limitListAsMapBySysName.put(riskSysName, sum);
                }
            }
        }
        limitListAsMapBySysName.put("VZRmedical", limitListAsMapBySysName.get("VZRmedExpenses"));

        Double totalContractLimit = 0D;
        Double totalContractPremium = 0D;
        //Double totalContractPayPremium = 0D;

        logger.debug("");
        Map<String, Map<String, Object>> insObjGroupListAsMapBySysName = (Map<String, Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        for (Map<String, Object> insObjGroup : insObjGroupListAsMapBySysName.values()) {
            Map<String, Map<String, Object>> objListAsMapBySysName = (Map<String, Map<String, Object>>) insObjGroup.get("OBJLIST");
            Map<String, Map<String, Object>> objListAsMapBySysNameWithSums = new HashMap<String, Map<String, Object>>();
            for (Map<String, Object> obj : objListAsMapBySysName.values()) {
                Map<String, Object> contrObjMap = (Map<String, Object>) obj.get("CONTROBJMAP");
                Map<String, Object> insObjMap = (Map<String, Object>) obj.get("INSOBJMAP");

                String insObjSysName = getStringParam(insObjMap.get("INSOBJSYSNAME"));
                if (insObjSysName.isEmpty()) {
                    insObjSysName = getStringParam(insObjMap.get("SYSNAME"));
                }
                logger.debug("insObjSysName = " + insObjSysName + "...");

                Double objPayPremium = 0D;
                Double objPremium = 0D;
                Double objLimit = 0D;
                Map<String, Map<String, Object>> contrRiskListAsMapBySysName = (Map<String, Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                Map<String, Map<String, Object>> contrRiskListAsMapBySysNameWithSums = new HashMap<String, Map<String, Object>>();
                for (Map<String, Object> contrRisk : contrRiskListAsMapBySysName.values()) {
                    String riskSysName = getStringParam(contrRisk.get("PRODRISKSYSNAME"));
                    if (riskSysName.isEmpty()) {
                        riskSysName = getStringParam(contrRisk.get("SYSNAME"));
                    }
                    logger.debug("    riskSysName = " + riskSysName + "...");

                    Double roundedPremium = getDoubleParam(premListAsMapBySysName.get(riskSysName));

                    if (roundedPremium > 0) {

                        totalContractPremium += roundedPremium;
                        contrRisk.put("PREMVALUE", roundedPremium);
                        objPremium += roundedPremium;
                        Double payPremium = roundedPremium * currencyRate;
                        Double roundedPayPremium = roundSum(payPremium);
                        //totalContractPayPremium += roundedPayPremium;
                        contrRisk.put("PAYPREMVALUE", roundedPayPremium);
                        objPayPremium += roundedPayPremium;

                        Double limit = getDoubleParam(limitListAsMapBySysName.get(riskSysName));
                        if (totalContractLimit < limit) {
                            totalContractLimit = limit;
                        }
                        if (objLimit < limit) {
                            objLimit = limit;
                        }
                        contrRisk.put("INSAMVALUE", limit);

                        logger.debug("        Страховая сумма / лимит (INSAMVALUE) = " + limit + ";");
                        logger.debug("        Страховая премия в валюте договора (PREMVALUE) = " + roundedPremium + ";");
                        logger.debug("        Страховая премия в рублях (PAYPREMVALUE) = " + roundedPayPremium + ";");

                        contrRiskListAsMapBySysNameWithSums.put(riskSysName, contrRisk);

                    } else {
                        logger.debug("        Страховая премия в валюте договора (PREMVALUE) = 0;");
                        logger.debug("        Риск исключен.");
                    }
                }

                contrObjMap.put("CONTRRISKLIST", contrRiskListAsMapBySysNameWithSums);

                if (objPremium > 0) {
                    Double roundedObjPremium = roundSum(objPremium);
                    contrObjMap.put("PREMVALUE", roundedObjPremium);
                    Double roundedObjPayPremium = roundSum(objPayPremium);
                    contrObjMap.put("PAYPREMVALUE", roundedObjPayPremium);
                    Double roundedObjLimit = roundSum(objLimit);
                    contrObjMap.put("INSAMVALUE", roundedObjLimit);
                    objListAsMapBySysNameWithSums.put(insObjSysName, obj);
                } else {
                    logger.debug("    Объект не содержит ни одного риска - объект исключен.");
                }

            }

            insObjGroup.put("OBJLIST", objListAsMapBySysNameWithSums);

        }

        Double roundedTotalContractLimit = roundSum(totalContractLimit);
        Double roundedTotalContractPremium = roundSum(totalContractPremium);
        //Double roundedTotalContractPayPremium = roundSum(totalContractPayPremium);

        setOverridedParam(contract, "INSAMVALUE", roundedTotalContractLimit, isVerboseLog);
        setOverridedParam(contract, "PREMVALUE", roundedTotalContractPremium, isVerboseLog);
        //setOverridedParam(contract, "PAYPREMVALUE", roundedTotalContractPayPremium, isVerboseLog);
        //setOverridedParam(contract, "PREMVALUE", roundedTotalContractPayPremium, isVerboseLog);

    }

    // генерация дополнительных вычисляемых сумм для 'Защита имущества сотрудников сбербанка Онлайн', очистка от "пустых" груп/объектов/рисков
    private void genAdditionalSisSumSaveParams(Map<String, Object> contract, Map<String, Object> product) throws Exception {

        //{"INSOBJGROUPLIST.other.INSOBJGROUPSYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.other.SYSNAME"},
        //{"INSOBJGROUPLIST.other.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.other.PRODSTRUCTID"},
        //{"INSOBJGROUPLIST.other.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.other.HBDATAVERID"},
        //{"INSOBJGROUPLIST.other.OBJLIST.otherConstructInterior.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.otherConstructInterior"},
        //{"INSOBJGROUPLIST.other.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST.otherConstructInteriorRisk", "PRODCONF.PRODVER.PRODSTRUCTS.otherConstructInteriorRisk"},        
        //{"INSOBJGROUPLIST.other.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST.otherConstructInteriorRisk.PREMVALUE", "CONTROBJLIST.other.RISKLIST.construct.PREMVALUE"},
        //{"INSOBJGROUPLIST.other.OBJLIST.otherConstructInterior.CONTROBJMAP.CONTRRISKLIST.otherConstructInteriorRisk.INSAMVALUE", "CONTROBJLIST.other.RISKLIST.construct.INSAMVALUE"},
        // значение OBJTYPESYSNAME для объекта 'Иная постройка' в списке объектов из старого запроса сведений договора
        String otherContrObjTypeSysName = "other";

        // наименование по-умолчанию для объекта 'Иная постройка', если не указано в старом объекте
        String otherDefaultBuildingName = "Иная постройка";

        // список объектов из старого запроса сведений договора
        List<Map<String, Object>> oldContrObjList = (List<Map<String, Object>>) contract.get("CONTROBJLIST");

        // атрибуты для типа/группы 'Иная постройка'
        Map<String, Object> otherInsObjGroupAttrs = newMap();
        String otherInsObjGroupSysName = getStringParam(chainedGet(product, "PRODVER.PRODSTRUCTS.other.SYSNAME"));
        otherInsObjGroupAttrs.put("INSOBJGROUPSYSNAME", otherInsObjGroupSysName);
        otherInsObjGroupAttrs.put("PRODSTRUCTID", chainedGet(product, "PRODVER.PRODSTRUCTS.other.PRODSTRUCTID"));
        otherInsObjGroupAttrs.put("HBDATAVERID", chainedGet(product, "PRODVER.PRODSTRUCTS.other.HBDATAVERID"));

        // атрибуты для объекта 'Иная постройка'
        Map<String, Object> otherInsObjMapAttrs = (Map<String, Object>) chainedGet(product, "PRODVER.PRODSTRUCTS.otherConstructInterior");
        String otherInsObjSysName = getStringParam(otherInsObjMapAttrs.get("SYSNAME"));

        // атрибуты для риска 'Иная постройка'
        Map<String, Object> otherContrRiskAttrs = (Map<String, Object>) chainedGet(product, "PRODVER.PRODSTRUCTS.otherConstructInteriorRisk");
        String otherContrRiskSysName = getStringParam(otherContrRiskAttrs.get("SYSNAME"));

        Integer otherCount = 0;
        for (Map<String, Object> oldContrObj : oldContrObjList) {
            String objTypeSysName = getStringParam(oldContrObj.get("OBJTYPESYSNAME"));

            if (otherContrObjTypeSysName.equalsIgnoreCase(objTypeSysName)) {

                Map<String, Map<String, Object>> insObjGroupListAsMapBySysName = (Map<String, Map<String, Object>>) contract.get("INSOBJGROUPLIST");

                // список рисков из старого объекта
                List<Map<String, Object>> oldRiskList = (List<Map<String, Object>>) oldContrObj.get("RISKLIST");
                // у старого объекта 'Иная постройка' всегда один риск - construct
                Map<String, Object> oldRisk = oldRiskList.get(0);

                // тип/группа
                Map<String, Object> insObjGroup = newMap();
                insObjGroup.putAll(otherInsObjGroupAttrs);
                // показатели для типа/группы
                insObjGroup.put("area", oldContrObj.get("OBJAREA"));
                String buildingName = getStringParam(oldContrObj.get("NAME"));
                if (buildingName.isEmpty()) {
                    buildingName = otherDefaultBuildingName;
                }
                insObjGroup.put("buildingName", buildingName);
                Object facingTypeSysName = oldContrObj.get("FACINGTYPE");
                Long interiorDecorationType = getLongParam(convertValue(facingTypeSysName, sisOtherFacingTypeConvertRules, Direction.TO_SAVE));
                insObjGroup.put("interiorDecorationType", interiorDecorationType);

                // риск
                Map<String, Object> contrRisk = newMap();
                contrRisk.putAll(otherContrRiskAttrs);
                contrRisk.put("PREMVALUE", oldRisk.get("PREMVALUE"));
                contrRisk.put("INSAMVALUE", oldRisk.get("INSAMVALUE"));

                // объект
                Map<String, Object> insObjMap = newMap();
                insObjMap.putAll(otherInsObjMapAttrs);

                Map<String, Object> obj = newMap();
                obj.put("INSOBJMAP", insObjMap);

                Map<String, Object> contrRiskListAsMapBySysName = newMap();
                contrRiskListAsMapBySysName.put(otherContrRiskSysName, contrRisk);

                Map<String, Object> contrObjMap = newMap();
                contrObjMap.put("CONTRRISKLIST", contrRiskListAsMapBySysName);

                obj.put("INSOBJMAP", insObjMap);
                obj.put("CONTROBJMAP", contrObjMap);

                Map<String, Object> objListAsMapBySysName = newMap();
                objListAsMapBySysName.put(otherInsObjSysName, obj);

                insObjGroup.put("OBJLIST", objListAsMapBySysName);

                otherCount += 1;
                insObjGroupListAsMapBySysName.put(otherInsObjGroupSysName + otherCount.toString(), insObjGroup);

            }

        }

        //Double currencyRate = getDoubleParam(contract.get("CURRENCYRATE"));
        //Double totalContractLimit = 0D;
        //Double totalContractPremium = 0D;
        Map<String, Map<String, Object>> insObjGroupListAsMapBySysName = (Map<String, Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        Map<String, Map<String, Object>> insObjGroupListAsMapBySysNameWithSums = new HashMap<String, Map<String, Object>>();
        //for (Map<String, Object> insObjGroup : insObjGroupListAsMapBySysName.values()) {
        for (Map.Entry<String, Map<String, Object>> entrySet : insObjGroupListAsMapBySysName.entrySet()) {
            String insObjGroupSysName = entrySet.getKey();
            Map<String, Object> insObjGroup = entrySet.getValue();

            Map<String, Map<String, Object>> objListAsMapBySysName = (Map<String, Map<String, Object>>) insObjGroup.get("OBJLIST");
            Map<String, Map<String, Object>> objListAsMapBySysNameWithSums = new HashMap<String, Map<String, Object>>();

            //String insObjGroupSysName = getStringParam(insObjGroup.get("INSOBJGROUPSYSNAME"));
            //if (insObjGroupSysName.isEmpty()) {
            //    insObjGroupSysName = getStringParam(insObjGroup.get("SYSNAME"));
            //}
            Double objGroupPremium = 0D;
            Double objGroupLimit = 0D;

            for (Map<String, Object> obj : objListAsMapBySysName.values()) {
                Map<String, Object> contrObjMap = (Map<String, Object>) obj.get("CONTROBJMAP");
                Map<String, Object> insObjMap = (Map<String, Object>) obj.get("INSOBJMAP");

                String insObjSysName = getStringParam(insObjMap.get("INSOBJSYSNAME"));
                if (insObjSysName.isEmpty()) {
                    insObjSysName = getStringParam(insObjMap.get("SYSNAME"));
                }
                logger.debug("insObjSysName = " + insObjSysName + "...");

                //Double objPayPremium = 0D;
                Double objPremium = 0D;
                Double objLimit = 0D;
                Map<String, Map<String, Object>> contrRiskListAsMapBySysName = (Map<String, Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                Map<String, Map<String, Object>> contrRiskListAsMapBySysNameWithSums = new HashMap<String, Map<String, Object>>();
                for (Map<String, Object> contrRisk : contrRiskListAsMapBySysName.values()) {
                    String riskSysName = getStringParam(contrRisk.get("PRODRISKSYSNAME"));
                    if (riskSysName.isEmpty()) {
                        riskSysName = getStringParam(contrRisk.get("SYSNAME"));
                    }
                    logger.debug("riskSysName = " + riskSysName + "...");

                    Double premium = getDoubleParam(contrRisk.get("PREMVALUE"));

                    if (premium > 0) {

                        //totalContractPremium += premium;
                        //contrRisk.put("PREMVALUE", premium);
                        objPremium += premium;
                        //Double payPremium = premium * currencyRate;
                        //Double roundedPayPremium = roundSum(payPremium);
                        //totalContractPayPremium += roundedPayPremium;
                        //contrRisk.put("PAYPREMVALUE", roundedPayPremium);
                        //objPayPremium += roundedPayPremium;

                        Double limit = getDoubleParam(contrRisk.get("INSAMVALUE"));
                        //if (totalContractLimit < limit) {
                        //    totalContractLimit = limit;
                        //}
                        if (objLimit < limit) {
                            objLimit = limit;
                        }
                        contrRisk.put("INSAMVALUE", limit);

                        logger.debug("              limit = " + limit + ";");
                        logger.debug("              premium = " + premium + ";");
                        //logger.debug("              payPremium = " + roundedPayPremium + ";");

                        contrRiskListAsMapBySysNameWithSums.put(riskSysName, contrRisk);

                    }
                }

                contrObjMap.put("CONTRRISKLIST", contrRiskListAsMapBySysNameWithSums);

                if (objPremium > 0) {
                    Double roundedObjPremium = roundSum(objPremium);
                    contrObjMap.put("PREMVALUE", roundedObjPremium);
                    //Double roundedObjPayPremium = roundSum(objPayPremium);
                    //contrObjMap.put("PAYPREMVALUE", roundedObjPayPremium);
                    Double roundedObjLimit = roundSum(objLimit);
                    contrObjMap.put("INSAMVALUE", roundedObjLimit);
                    objListAsMapBySysNameWithSums.put(insObjSysName, obj);
                    objGroupPremium += objPremium;
                    if (objGroupLimit < roundedObjLimit) {
                        objGroupLimit = roundedObjLimit;
                    }

                }

            }

            insObjGroup.put("OBJLIST", objListAsMapBySysNameWithSums);

            if (objGroupPremium > 0) {
                insObjGroupListAsMapBySysNameWithSums.put(insObjGroupSysName, insObjGroup);
            }

        }

        contract.put("INSOBJGROUPLIST", insObjGroupListAsMapBySysNameWithSums);

        //Double roundedTotalContractLimit = roundSum(totalContractLimit);
        //Double roundedTotalContractPremium = roundSum(totalContractPremium);
        //setOverridedParam(contract, "INSAMVALUE", roundedTotalContractLimit, isVerboseLog);
        //setOverridedParam(contract, "PREMVALUE", roundedTotalContractPremium, isVerboseLog);
    }
    //hard code
    private String getSysName(Map<String, Object>  contract){
        Map<String, Object> contrExtMap = (Map<String, Object>) chainedGetIgnoreCase(contract, "CONTREXTMAP");
        String insObject = (String) chainedGetIgnoreCase(contrExtMap, "insObject");
        switch(insObject){
            case "0":{
                return "house";
            }
            case "1":{
                return "flat";
            }
            default:{
                return insObject;
            }
        }
    }

    private String findProdDefValByName(List<Map<String, Object>> prodDefValList, String name) {
        for (Map<String, Object> bean : prodDefValList) {
            if ((bean.get("NAME") != null) && (bean.get("NAME").toString().equalsIgnoreCase(name))) {
                if (bean.get("VALUE") != null) {
                    return bean.get("VALUE").toString();
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    // генерация дополнительных вычисляемых дат и срока действия, если не были переданы в явном виде для сохранения
    private void genAdditionalDateSaveParams(Map<String, Object> contract, Map<String, Object> product, int oldProdConfId, boolean isVerboseLog, String login, String password) throws NumberFormatException, Exception {
        // дата оформления
        Object docDateObj = contract.get("DOCUMENTDATE");
        Date docDate;
        if (docDateObj == null) {
            docDate = new Date();
            //contract.put("DOCUMENTDATE", docDate);
            setGeneratedParam(contract, "DOCUMENTDATE", docDate, isVerboseLog);
        } else {
            docDate = (Date) parseAnyDate(docDateObj, Date.class, "DOCUMENTDATE");
        }

        // дата начала договора
        Object startDateObj = contract.get("STARTDATE");
        Date startDate = null;
        GregorianCalendar startDateGC = new GregorianCalendar();
        if (startDateObj == null) {
            // пробуем определить дату начала действия договора через B2B метод определения
            List<Map<String, Object>> prodDefValList = (List<Map<String, Object>>) product.get("PRODDEFVALS");
            if (prodDefValList != null) {
                String sdCalcMethod = findProdDefValByName(prodDefValList, "SDCALCMETHOD");
                if (sdCalcMethod != null) {
                    Map<String, Object> calcParams = new HashMap<String, Object>();
                    calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
                    calcParams.put("DOCUMENTDATE", parseAnyDate(docDate, Double.class, "DOCUMENTDATE"));
                    calcParams.put("SDCALCMETHOD", sdCalcMethod);
                    calcParams.put("SDLAG", findProdDefValByName(prodDefValList, "SDLAG"));
                    calcParams.put("SDCALENDARTYPE", findProdDefValByName(prodDefValList, "SDCALENDARTYPE"));
                    Map<String, Object> qRes = this.callService(Constants.B2BPOSWS, "dsB2BContractCalcStartDate", calcParams, login, password);
                    if ((qRes != null) && (qRes.get("STARTDATE") != null)) {
                        startDate = (Date) parseAnyDate(qRes.get("STARTDATE"), Date.class, "STARTDATE");
                        startDateGC.setTime(startDate);
                    }
                }
            }
            // если не определили через B2B метод, тогда определяем по старому
            if (startDate == null) {
                startDateGC.setTime(docDate); // "Дата начала договора должна быть «Дата оформления» + ...
                int startDateDaysShift = 1; // ... + 1 день" (в обычных договорах, например, для ипотеки)
                if ((oldProdConfId == PRODCONFID_CIB) || (oldProdConfId == PRODCONFID_CIBY) || (oldProdConfId == PRODCONFID_HIB) || (oldProdConfId == PRODCONFID_PHIB)) {
                    startDateDaysShift = 14; // ... + 14 дней" (для карты и дома)
                }
                startDateGC.add(Calendar.DATE, startDateDaysShift); // ... + X дней"
                startDate = startDateGC.getTime();
            }
            setGeneratedParam(contract, "STARTDATE", startDate, isVerboseLog);
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
        if (finishDateObj == null) {
            finishDateGC.setTime(startDate); // "соответственно дата окончания должна быть «Дата начала» + ...
            finishDateGC.add(Calendar.YEAR, 1); // ... + 1 страховой год ...
            int finishDateDaysShift = 0; // ... + 0 дней" (в обычных договорах, например, для ипотеки)
            if ((oldProdConfId == PRODCONFID_CIB) || (oldProdConfId == PRODCONFID_CIBY) || (oldProdConfId == PRODCONFID_HIB) || (oldProdConfId == PRODCONFID_PHIB)) {
                finishDateDaysShift = -1; // ... - 1 день" (для карты и дома)
            }
            finishDateGC.add(Calendar.DATE, finishDateDaysShift); // ... - X дней"
            finishDate = finishDateGC.getTime();
            //contract.put("FINISHDATE", finishDate);
            setGeneratedParam(contract, "FINISHDATE", finishDate, isVerboseLog);
        } else {
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
            //contract.put("DURATION", duration);
            setGeneratedParam(contract, "DURATION", duration, isVerboseLog);
        }
        /*else {
         duration = Integer.parseInt(durationObj.toString());
         }*/

    }

    // копирование наследуемых и дублирующихся значений по списку из contractDuplicatedKeys
    protected void genDuplicatedSaveParams(Map<String, Object> contract) {

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

    protected boolean isB2BMode(Map<String, Object> params) {

        generateNewIDsByOld();

        String isB2BUseParamValue;
        boolean isB2BUse;
        StringBuilder logB2BUse = new StringBuilder();
        Object isB2BUseOverrideParam = null;
        if (params != null) {
            isB2BUseOverrideParam = params.get(USEB2B_PARAM_NAME);
        }
        if (isB2BUseOverrideParam != null) {
            isB2BUseParamValue = isB2BUseOverrideParam.toString();
            logB2BUse.append("Согласно переданному через параметры значению ключа ").append(USEB2B_PARAM_NAME).append(" ('").append(isB2BUseParamValue).append("')");
        } else {
            Config config = Config.getConfig(BIVSBERPOSWS_SERVICE_NAME);
            isB2BUseParamValue = config.getParam(USEB2B_PARAM_NAME, "false");
            logB2BUse.append("Согласно настройкам службы ").append(BIVSBERPOSWS_SERVICE_NAME);
        }
        isB2BUse = "true".equalsIgnoreCase(isB2BUseParamValue) || "yes".equalsIgnoreCase(isB2BUseParamValue) || "1".equalsIgnoreCase(isB2BUseParamValue);
        logB2BUse.append(isB2BUse ? "" : " не").append(" будет использован режим работы с B2B...");
        logger.debug(logB2BUse.toString());
        return isB2BUse;
    }

    // done: возможно, перенести в BaseFacade, т.к. используется более чем в одном фасаде (PaymentCustomFacade, AngularContractCustomFacade, ...) 
    // ! использовать isB2BMode(Map<String, Object> params) !
    //protected boolean isB2BMode() {
    //    return isB2BMode(null);
    //}
    protected Map<String, Object> prepareB2BParams(Map<String, Object> rawParams, Direction direction) throws Exception {

        // протоколирование вызова
        //long callTimer = System.currentTimeMillis();
        //String methodName = "prepareB2BParams";
        //logger.debug("Вызван метод " + methodName + " с параметрами:\n\n" + rawParams.toString() + "\n");
        // проверка на ключ подробного протоколирования
        boolean isVerboseLog = getBooleanParam(rawParams.get("VERBOSELOG"), false);
        if (isVerboseLog) {
            logger.debug("Включен режим подробного протоколирования...");
        }

        // флаг миграции (если выставлен в true, то не будет выполнятся безусловное перевычисление сумм, дат и пр. перед сохранением договора)
        boolean isMigration = getBooleanParam(rawParams.get("ISMIGRATION"), false);
        if (isMigration) {
            logger.debug("Включен режим миграции - безусловное перевычисление сумм, дат и ряда других параметров перед сохранением договора выполнятся не будет.");
        }

        // подготовка структуры для размещения данных
        Map<String, Object> contract = newMap();
        contract.put("CONTRSRCPARAMLIST", rawParams.get("CONTRSRCPARAMLIST"));

        // общий для всех продуктов список преобразований
        List<String[]> contractKeysRelations = new ArrayList<String[]>();
        contractKeysRelations.addAll(Arrays.asList(commonKeysRelations));

        // дополнение общего для всех продуктов списка преобразований персональным для конкретного продукта списком
        Object rawProdConfId = chainedGetIgnoreCase(rawParams, "prodConfId");
        int oldProdConfId = 0;
        if (direction == Direction.TO_SAVE) {
            oldProdConfId = getIntegerParam(rawProdConfId);
        } else if (direction == Direction.TO_LOAD) {
            oldProdConfId = getIntegerParam(convertValue(rawProdConfId, getProdConfIDConvertRules(), direction));
        }
        if (isVerboseLog) {
            logger.debug("Переданный в параметрах идентификатор конфигурации продукта: " + rawProdConfId);
            logger.debug("Учитывая направление преобразования структуры (" + direction.toString() + ") определен старый идентификатор конфигурации продукта: " + oldProdConfId);
        }
        switch (oldProdConfId) {
            case PRODCONFID_HIB:
            case PRODCONFID_PHIB:
                contractKeysRelations.addAll(Arrays.asList(hibKeysRelations));
                break;
            case PRODCONFID_CIB:
            case PRODCONFID_CIBY:
                contractKeysRelations.addAll(Arrays.asList(cibKeysRelations));
                break;
            case PRODCONFID_VZR:
                contractKeysRelations.addAll(Arrays.asList(vzrKeysRelations));
                break;
            case PRODCONFID_SIS:
                if (direction == Direction.TO_SAVE) {
                    if (isVerboseLog) {
                        logger.debug("Учитывая направление преобразования структуры (" + direction.toString() + ") будет выполнена дополнительная обработка списка объектов страхования (CONTROBJLIST) для продукта 'Защита имущества сотрудников сбербанка Онлайн'...");
                    }
                    List<Map<String, Object>> oldContrObjList = (List<Map<String, Object>>) rawParams.get("CONTROBJLIST");
                    if (oldContrObjList != null) {
                        for (Map<String, Object> oldContrObj : oldContrObjList) {
                            String note = getStringParam(oldContrObj.get("NOTE"));
                            if (isVerboseLog) {
                                logger.debug("");
                                logger.debug("Объект страхования:");
                                logger.debug("  Системное имя типа объекта (OBJTYPESYSNAME) = " + getStringParam(oldContrObj.get("OBJTYPESYSNAME")));
                                logger.debug("  Заметка к объекту (NOTE) = " + note);
                            }
                            if ("secondHouse".equalsIgnoreCase(note)) {
                                oldContrObj.put("OBJTYPESYSNAME", "house2");
                                if (isVerboseLog) {
                                    logger.debug("  Системное имя типа объекта (OBJTYPESYSNAME) временно переопределено новым значением - 'house2'.");
                                }
                            }
                        }
                    }

                    // отдельный исходный список объектов - для обработки в genAdditionalSisSumSaveParams
                    contract.put("CONTROBJLIST", oldContrObjList);
                    if (isVerboseLog) {
                        logger.debug("");
                        logger.debug("Исходный ключ: CONTROBJLIST");
                        logger.debug("Конечный ключ: CONTROBJLIST");
                        logger.debug("Установлено значение: " + oldContrObjList);
                    }
                }
                contractKeysRelations.addAll(Arrays.asList(sisKeysRelations));
                break;
            case PRODCONFID_MORTGAGE:
                contractKeysRelations.addAll(Arrays.asList(mortgageKeysRelations));
                break;
            default:
                logger.debug("Не удалось определить продукт - специфические для конкретных продуктов параметры преобразования структуры сведений договора применены не будут!");
        }

        // развертывание списков в карты вида 'имяСписка_системноеИмяЭлементаСписка'
        String[][] expanded = contractListMapConversions[direction.fromIndex];
        for (int e = 0; e < expanded.length; e++) {
            String listName = expanded[e][0];
            String sysAttrName = expanded[e][1];
            expandListToMapBySysName(rawParams, listName, sysAttrName);
        }

        Map<String, Object> rawParamsCopyForLog = null;
        if (isVerboseLog) {
            // клонирование rawParams через поток
            try {
                // создание исходящего потока
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream ous = new ObjectOutputStream(baos);
                // сохранение состояние rawParams в поток
                ous.writeObject(rawParams);
                // закрытие потока
                ous.close();
                // создание входящего потока
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bais);
                // чтение состояние rawParams в rawParamsCopyForLog
                rawParamsCopyForLog = (Map<String, Object>) ois.readObject();
                // закрытие потока
                ois.close();

                List<String[]> nonValueableKeys = new ArrayList<String[]>(Arrays.asList(commonNonValueableKeys));
                //nonValueableKeys.addAll(Arrays.asList(commonNonValueableKeys));
                if (direction == Direction.TO_SAVE) {
                    nonValueableKeys.addAll(Arrays.asList(saveNonValueableKeys));
                }

                for (String[] nonValueableKey : nonValueableKeys) {
                    String skippedKey = nonValueableKey[nonValueableKeysKeyIndex];
                    String skipReason = nonValueableKey[nonValueableKeysReasonIndex];
                    chainedCreativePut(rawParamsCopyForLog, skippedKey, "'НЕ ТРЕБУЕТ ПЕРЕНОСА (причина: " + skipReason + ")'");
                }

                //for (int i = 0; i < commonNonValueableKeys.length; i++) {
                //    String[] nonValueableKey = commonNonValueableKeys[i];
                //    chainedCreativePut(rawParamsCopyForLog, nonValueableKey[nonValueableKeysKeyIndex], "'НЕ ТРЕБУЕТ ПЕРЕНОСА (причина: " + nonValueableKey[nonValueableKeysReasonIndex] + ")'");
                //}
                //if (direction == Direction.TO_SAVE) {
                //    for (int i = 0; i < saveNonValueableKeys.length; i++) {
                //        String[] saveNonValueableKey = saveNonValueableKeys[i];
                //        chainedCreativePut(rawParamsCopyForLog, saveNonValueableKey[nonValueableKeysKeyIndex], "'НЕ ТРЕБУЕТ ПЕРЕНОСА ПРИ СОХРАНЕНИИ (причина: " + saveNonValueableKey[nonValueableKeysReasonIndex] + ")'");
                //    }
                //}
                rawParamsCopyForLog.remove("PRODCONF"); // не фиксировать операции с конфигурацией продукта

            } catch (IOException ex) {
                logger.debug("Исключение при клонировании rawParams: ", ex);
            } catch (ClassNotFoundException ex) {
                logger.debug("Исключение при клонировании rawParams: ", ex);
            }
        }

        // копирование сведений в новую структуру
        for (int i = 0; i < contractKeysRelations.size(); i++) {
            String[] contractKeyRelation = contractKeysRelations.get(i);
            String newKey = contractKeyRelation[direction.fromIndex];
            String oldKey = contractKeyRelation[direction.toIndex];
            // todo: управлять доп. протоколированием через константу
            //logger.debug(oldKey + " > " + newKey);
            Boolean isCreativePut = true;
            /*if ((contractKeysRelations.get(i).length > 2) && (!contractKeysRelations.get(i)[2].isEmpty())) {
             isCreativePut = !("-".equalsIgnoreCase(contractKeysRelations.get(i)[2]));
             }*/
            String convertRulesStr = null;
            if ((contractKeyRelation.length > 3) && (!contractKeyRelation[3].isEmpty())) {
                convertRulesStr = contractKeyRelation[3];
            }

            Object rawValue = chainedGet(rawParams, oldKey);
            if (rawValue == null) {
                oldKey = getKeysChainWithLastKeyUpperCase(oldKey);
                rawValue = chainedGet(rawParams, oldKey);
            }
            Object value = null;
            if (rawValue instanceof List) {
                List listValue = (List) rawValue;
                value = listValue.subList(listValue.size() - 1, listValue.size());
            } else {
                value = rawValue;
            }
            if (value != null) {
                if (convertRulesStr != null) {
                    value = convertValue(value, convertRulesStr, direction);
                }
                if (isVerboseLog) {
                    logger.debug("");
                    logger.debug("Исходный ключ: " + oldKey);
                    logger.debug("Конечный ключ: " + newKey);
                }
                chainedCreativePut(contract, newKey, value, isCreativePut);
                if (isVerboseLog) {
                    logger.debug("Установлено значение: " + value + ((convertRulesStr == null) ? "" : " (полученное из '" + rawValue + "' с применением правила '" + convertRulesStr + "')"));
                    chainedCreativePut(rawParamsCopyForLog, oldKey, "'ЗНАЧЕНИЕ ПЕРЕНЕСЕНО'");
                }

            }
        }

        // установка значний по-умолчанию (только при сохранении)
        if (direction == Direction.TO_SAVE) {
            for (int i = 0; i < contractKeysRelations.size(); i++) {
                String[] contractKeyRelation = contractKeysRelations.get(i);
                String newKey = contractKeyRelation[direction.fromIndex];
                Object newKeyValue = chainedGet(contract, newKey);
                Boolean isCreativePut = true;
                String defaultValue = "";
                if ((contractKeyRelation.length > 2) && (!contractKeyRelation[2].isEmpty())) {
                    defaultValue = contractKeyRelation[2];
                }
                if ((newKeyValue == null) && (!defaultValue.isEmpty())) {
                    if (isVerboseLog) {
                        logger.debug("");
                        logger.debug("Конечный ключ: " + newKey);
                        logger.debug("Установлено значение по-умолчанию: " + defaultValue);
                    }
                    chainedCreativePut(contract, newKey, defaultValue, isCreativePut);
                }
            }
        }

        // генерация дополнительных вычисляемых параметров
        String login = getStringParam(rawParams.get(WsConstants.LOGIN));
        String password = getStringParam(rawParams.get(WsConstants.PASSWORD));
        Map<String, Object> product = (Map<String, Object>) rawParams.get("PRODCONF");
        genAdditionalParams(contract, product, oldProdConfId, direction, isMigration, isVerboseLog, login, password);

        // свертывание карт вида 'имяСписка.системноеИмяЭлементаСписка' в списки
        String[][] collapsed = contractListMapConversions[direction.toIndex];
        for (int e = 0; e < collapsed.length; e++) {
            String listName = collapsed[e][0];
            String sysAttrName = collapsed[e][1];
            //logger.debug(listName + " / " + sysAttrName);
            collapseMapToListBySysName(contract, listName, sysAttrName);
        }

        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        if (direction == Direction.TO_SAVE) {
            // передача только обработанных параметров
            result = contract;
        } else if (direction == Direction.TO_LOAD) {
            // передача всех входных параметров без изменений
            // todo: возможно, заменить на выборочную передачу входных данных в результат?
            result.putAll(rawParams);
            // дополнение обработанными параметрами с переопределением совпадающих по ключам
            result.putAll(contract);
        }

        // протоколирование вызова
        //callTimer = System.currentTimeMillis() - callTimer;
        //logger.debug("Метод " + methodName + " выполнился за " + callTimer + " мс. и вернул результат:\n\n" + result.toString() + "\n");
        if (isVerboseLog) {
            logger.debug("... в том числе, структуру объекта (INSOBJGROUPLIST):\n\n" + result.get("INSOBJGROUPLIST") + "\n");
            logger.debug("... в том числе, показатели договора (CONTREXTMAP):\n\n" + result.get("CONTREXTMAP") + "\n");
            logger.debug("... в том числе, сформирована карта переноса (rawParamsCopyForLog):\n\n" + rawParamsCopyForLog + "\n");
            result.put("PREPARINGPROCESSLOG", rawParamsCopyForLog);
        }

        return result;
    }

    protected List<Map<String, Object>> filterProductStructureList(List<Map<String, Object>> prodStructList, Long discriminator) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> bean : prodStructList) {
            boolean fAdd = true;
            if (discriminator != null) {
                if (!((bean.get("DISCRIMINATOR") != null) && (Long.valueOf(bean.get("DISCRIMINATOR").toString()).longValue() == discriminator.longValue()))) {
                    fAdd = false;
                }
            }
            if (fAdd) {
                resultList.add(bean);
            }
        }
        return resultList;
    }

    protected List<Map<String, Object>> filterProductProgramList(List<Map<String, Object>> prodProgramList, String sysName) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> bean : prodProgramList) {
            boolean fAdd = true;
            if (sysName != null) {
                if (!((bean.get("SYSNAME") != null) && (bean.get("SYSNAME").toString().equalsIgnoreCase(sysName)))) {
                    fAdd = false;
                }
            }
            if (fAdd) {
                resultList.add(bean);
            }
        }
        return resultList;
    }

    // копия из B2BBaseFacade
    protected void logDateParseEx(String keyName, Object dateValue, String conversion, Exception ex) {
        logger.error("Parsing key's '" + keyName + "' value '" + dateValue + "' (using " + conversion + ") caused error:", ex);
    }

    /**
     * Преобразует дату из/в любой из перечисленных типов - String, Date,
     * Double. Для преобразований, использующих тип String, будет происходить
     * потеря точности (до дня).
     *
     * @param dateValue объект, содержащий дату в одном из перечисленных выше
     * типов
     * @param targetClass целевой тип преобразования (любой из перечисленных
     * выше)
     * @param keyName наименование ключа, указывающего на обрабатываемую дату
     * (только для протоколирования)
     *
     * @return преобразованную в указанный тип дату
     */
    // копия из B2BBaseFacade
    protected Object parseAnyDate(Object dateValue, Class targetClass, String keyName) {

        // String -> Date
        if (dateValue instanceof String) {
            if (String.class.equals(targetClass)) {
                return dateValue.toString();
            }
            // попытка преобразовать строку в дату
            try {
                dateValue = dateFormatter.parse(dateValue.toString());
            } catch (ParseException ex1) {
                try {
                    dateValue = dateFormatterNoDots.parse(dateValue.toString());
                } catch (ParseException ex2) {
                    logDateParseEx(keyName, dateValue, "format " + dateFormatter.toPattern(), ex1);
                    logDateParseEx(keyName, dateValue, "format " + dateFormatterNoDots.toPattern(), ex2);
                }
            }
        }
        // Double -> Date
        if (dateValue instanceof Double) {
            if (Double.class.equals(targetClass)) {
                return (Double) dateValue;
            }
            // попытка преобразовать число из БД в дату
            try {
                Double dateDouble = (Double) dateValue;
                dateValue = DSDateUtil.convertDate(dateDouble);
            } catch (Exception ex) {
                logDateParseEx(keyName, dateValue, "Double to Date conversion", ex);
            }
        }
        // Date -> String || Double
        if (dateValue instanceof Date) {
            if (Date.class.equals(targetClass)) {
                return (Date) dateValue;
            }
            // Date -> String
            if (String.class.equals(targetClass)) {
                // попытка преобразовать дату в строку
                try {
                    Date dateDate = (Date) dateValue;
                    dateValue = dateFormatter.format(dateDate);
                } catch (Exception ex) {
                    logDateParseEx(keyName, dateValue, "Date to String conversion", ex);
                }
                // немедленный возврат значения, поскольку была проверка на целевой тип
                return dateValue;
            } else // Date -> Double
            {
                if (Double.class.equals(targetClass)) {
                    // попытка преобразовать дату в число для БД
                    try {
                        Date dateDate = (Date) dateValue;
                        dateValue = DSDateUtil.convertDate(dateDate).doubleValue();
                    } catch (Exception ex) {
                        logDateParseEx(keyName, dateValue, "Date to Double conversion", ex);
                    }
                    // немедленный возврат значения, поскольку была проверка на целевой тип
                    return dateValue;
                }
            }
        }
        // возврат значения, вероятнее всего не преобразованного вовсе (например, из-за исключения)
        return dateValue;
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public boolean isAllEmailValid(String allEMail) {
        boolean result = true;
        String newEmailList = "";
        if (allEMail == null) {
            return false;
        }
        if (allEMail.isEmpty()) {
            return false;
        }
        String[] emailList = allEMail.split(",");
        if (emailList.length == 0) {
            return false;
        }
        for (String email : emailList) {
            if (isValidEmailAddress(email)) {
                if (newEmailList.isEmpty()) {
                    newEmailList = email;
                } else {
                    newEmailList = newEmailList + "," + email;
                }
            }
        }
        if (newEmailList.isEmpty()) {
            return false;
        } else {
            allEMail = newEmailList;
            return true;
        }
    }

    protected Long getLongParam(Map<String, Object> map, String keyName) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParam(map.get(keyName));
        }
        return longParam;
    }

    // аналог getLongParam, но с протоколировнием полученного значения
    protected Long getLongParamLogged(Map<String, Object> map, String keyName) {
        Long paramValue = getLongParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    // callExternalService, но с протоколированием
    protected Map<String, Object> callExternalServiceLogged(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        logger.debug("Call external service method [" + serviceName + "] " + methodName + " with parameters:\n\n" + params + "\n");
        // вызов действительного метода
        Map<String, Object> callResult = this.callExternalService(serviceName, methodName, params, login, password);
        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        logger.debug("External service method method [" + serviceName + "] " + methodName + " executed in " + callTimer + " ms and returned result:\n\n" + callResult + "\n");
        // возврат результата
        return callResult;
    }

    // callExternalService для получения списка, но с сокращенным протоколированием
    protected List<Map<String, Object>> callExternalServiceAndGetListFromResultMapLogged(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        logger.debug(String.format(
                "Call external service method [%s] %s with parameters:\n\n%s\n",
                serviceName, methodName, params.toString())
        );
        // вызов действительного метода
        Map<String, Object> callResult = this.callExternalService(serviceName, methodName, params, login, password);
        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        List<Map<String, Object>> callResultList = null;
        // анализ результата
        if (isCallResultOK(callResult)) {
            callResultList = getListFromResultMap(callResult);
        }
        if (callResultList == null) {
            // ошибка - не удалось получить список из мапы результа вызова
            logger.error(String.format(
                    "External service method method [%s] %s executed in %d ms and no list was returned as expected, but this call result instead:\n\n%s\n",
                    serviceName, methodName, callTimer, callResult == null ? "null" : callResult.toString())
            );
        } else {
            logger.debug(String.format(
                    "External service method method [%s] %s executed in %d ms and returned expected list contained %d record(s).",
                    serviceName, methodName, callTimer, callResultList.size())
            );
        }
        // возврат результата
        return callResultList;
    }

    // callService, но с протоколированием
    protected Map<String, Object> callServiceLogged(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        logger.debug("Call service method [" + serviceName + "] " + methodName + " with parameters:\n\n" + params + "\n");
        // вызов действительного метода
        Map<String, Object> callResult = this.callService(serviceName, methodName, params, login, password);
        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        logger.debug("External service method method [" + serviceName + "] " + methodName + " executed in " + callTimer + " ms and returned result:\n\n" + callResult + "\n");
        // возврат результата
        return callResult;
    }

    protected static List<Map<String, Object>> getListFromResultMap(Map<String, Object> resultMap) {
        List<Map<String, Object>> list = WsUtils.getListFromResultMap(resultMap);
        return list;
    }

    protected static boolean isContractInProlongableState(String stateSysName) {
        boolean isContractInProlongableState = B2B_CONTRACT_SG.equals(stateSysName) || B2B_CONTRACT_UPLOADED_SUCCESFULLY.equals(stateSysName);
        return isContractInProlongableState;
    }

    protected String genStringListForSQLQuery(String... itemStringArray) {
        StringBuilder sb = new StringBuilder("");
        for (String itemString : itemStringArray) {
            if ((itemString != null) && (!itemString.isEmpty())) {
                sb.append("'").append(itemString).append("',");
            }
        }
        int sbLength = sb.length();
        if (sbLength > 0) {
            sb.setLength(sbLength - 1);
        }
        return sb.toString();
    }

}
