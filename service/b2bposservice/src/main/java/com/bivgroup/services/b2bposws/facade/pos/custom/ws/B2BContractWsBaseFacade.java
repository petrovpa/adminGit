package com.bivgroup.services.b2bposws.facade.pos.custom.ws;

import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

/**
 * @author sambucus
 */
public class B2BContractWsBaseFacade extends ProductContractCustomFacade {

    private static final Logger logger = Logger.getLogger(B2BContractWsBaseFacade.class);

    // имя параметра, указывающего запрошен ли пропуск генерации первой полной строки адреса
    protected static final String CRM_SKIP_ADDRTXT1_GEN_PARAMNAME = "SKIPADDRESSTEXT1GENERATION";
    // имя параметра, указывающего запрошен ли пропуск генерации второй полной строки адреса
    protected static final String CRM_SKIP_ADDRTXT2_GEN_PARAMNAME = "SKIPADDRESSTEXT2GENERATION";

    // Продукты Онлайн -- рест2
    /**
     * код программы Защита карт Онлайн
     */
    public static final String PRODUCT_SYSNAME_REST2_CIB_ONLINE = "00010";
    /**
     * код программы Защита от клеща Онлайн
     */
    public static final String PRODUCT_SYSNAME_REST2_ANTIMITE_ONLINE = "B2B_ANTIMITE";
    /**
     * код программы Защита ипотеки Онлайн
     */
    public static final String PRODUCT_SYSNAME_REST2_MORTGAGE_ONLINE = "00029";
    /**
     * код программы защита дома Онлайн
     */
    public static final String PRODUCT_SYSNAME_REST2_HIB_ONLINE = "00009";
    /**
     * код программы Страхование путешественников Онлайн
     */
    public static final String PRODUCT_SYSNAME_REST2_VZR_ONLINE = "00018";
    /**
     * код программы Защита карт для молодежи
     */
    public static final String PRODUCT_SYSNAME_REST2_CIB_YOUTH = "B2B_CIB_FOR_YOUTH";

    // Продукты СБОЛ - коды программ СБОЛ
    /**
     * код программы СБОЛ для продуктов типа 'Защита карт СБОЛ'
     */
    private static final String PRODUCT_CODE_SBOL_CIB = "99001";
    /**
     * код программы СБОЛ для продуктов типа 'Защита дома СБОЛ'
     */
    private static final String PRODUCT_CODE_SBOL_HIB = "99002";
    /**
     * код программы СБОЛ для продуктов типа 'Страхование путешественников СБОЛ'
     */
    private static final String PRODUCT_CODE_SBOL_TRAVEL = "99003";
    /**
     * код программы СБОЛ для продуктов типа 'Страхование ипотеки СБОЛ'
     */
    private static final String PRODUCT_CODE_SBOL_MORTGAGE = "99004";
    /**
     * код программы СБОЛ для продуктов типа 'Защита от клеща (Ростелеком)'
     */
    private static final String PRODUCT_CODE_SBOL_ANTIMITE_ROS = "00008";
    /**
     * код программы СБОЛ для продуктов типа 'Страхование ЖКХ СБОЛ'
     */
    private static final String PRODUCT_CODE_SBOL_HOME_JKH = "99005";

    // Продукты СБОЛ - ИД шаблонов
    /**
     * ИД шаблонов обычных СБОЛ-продуктов
     */
    private static final Long TEMPLATE_ID_PRODUCT_SBOL = 1L;
    /**
     * ИД шаблонов новых СБОЛ-продуктов (версии 2.0)
     */
    private static final Long TEMPLATE_ID_PRODUCT_SBOL_20 = 2L;
    /**
     * ИД шаблонов данных по оплате для новых СБОЛ-продуктов (версии 2.0)
     */
    private static final Long TEMPLATE_ID_PRODUCT_SBOL_20_PAYMENT = 4L;

    // Продукты СБОЛ
    /**
     * 'Защита карт СБОЛ' - системное наименование продукта в B2B
     */
    public static final String PRODUCT_SYSNAME_SBOL_CIB = "99001";
    /**
     * 'Защита дома СБОЛ' - системное наименование продукта в B2B
     */
    public static final String PRODUCT_SYSNAME_SBOL_HIB = "99002";
    /**
     * 'Страхование путешественников СБОЛ' - системное наименование продукта в B2B
     */
    public static final String PRODUCT_SYSNAME_SBOL_TRAVEL = "99003";
    /**
     * 'Страхование ипотеки СБОЛ' - системное наименование продукта в B2B
     */
    public static final String PRODUCT_SYSNAME_SBOL_MORTGAGE = "99004";
    /**
     * 'Защита от клеща (Ростелеком)' - системное наименование продукта в B2B
     */
    public static final String PRODUCT_SYSNAME_SBOL_ANTIMITE_ROS = "B2B_SBOL_ANTIMITE_ROS";
    /**
     * 'Страхование ЖКХ СБОЛ' - системное наименование продукта в B2B
     */
    public static final String PRODUCT_SYSNAME_SBOL_HOME_JKH = "B2B_SBOL_HOME_JKH";

    // Продукты СБОЛ 2.0
    /**
     * 'Защита карт СБОЛ 2.0' - системное наименование продукта в B2B
     */
    private static final String PRODUCT_SYSNAME_SBOL_CIB_20 = "B2B_SBOL_CIB_20";
    /**
     * 'Защита дома СБОЛ 2.0' - системное наименование продукта в B2B
     */
    public static final String PRODUCT_SYSNAME_SBOL_HIB_20 = "B2B_SBOL_HIB_20";
    /**
     * 'Страхование ипотеки СБОЛ 2.0' - системное наименование продукта в B2B
     */
    public static final String PRODUCT_SYSNAME_SBOL_MORTGAGE_20 = "B2B_SBOL_MORT_20";
    /**
     * 'Страхование путешественников СБОЛ 2.0' - системное наименование продукта в B2B
     */
    public static final String PRODUCT_SYSNAME_SBOL_TRAVEL_20 = "B2B_SBOL_TRAVEL_20";
    /**
     * 'Страхование путешественников СБОЛ 2.0' - системное наименование продукта в B2B
     */
    public static final String PRODUCT_SYSNAME_VZR_CHEREHAPA = "B2B_TRAVEL_CHEREHAPA";

    //private static final String RISKSUMSHBDATAVERID_PARAMNAME = "RISKSUMSHBDATAVERID";
    // правило преобразования кодов программ СБОЛ в идентификаторы конфигураций продуктов
    private static final String prodConfIDConvertRules = "99001 > 3000; 99002 > 3500; 99003 > 2050; 99004 > 4000; 00008 > 13000";

    // мапа для получения системного наименования продукта по ИД шаблона и коду продукта СБОЛ из XML
    private static final Map<Long, Map<String, String>> PRODUCT_SYSNAMES;

    // мапа для получения массива продуктозависимых соответствий ключей по системному имени продукта
    public static final Map<String, String[][]> PRODUCT_KEYS_RELATIONS;

    // наборы сиснеймов продуктов для условного функционала (заполняются в статическом инициализаторе)
    /**
     * продукты, для которых НЕ требуется генерация полной строки адреса регистрации страхователя
     * (для этих продуктов полная строка адреса передается в поле Region самого адреса, вместо только региона)
     */
    public static final Set<String> PRODUCTS_FULL_ADDRESS_IN_REGION;
    /**
     * продукты, для которых требуется генерация полной строки адреса регистрации страхователя
     * (для этих продуктов полная строка адреса не передается в поле Region самого адреса,
     * вместо этого требуется "Слепить все поля через запятую")
     */
    public static final Set<String> PRODUCTS_FULL_ADDRESS_GEN;
    /**
     * продукты, для которых договор создается без факта оплаты, сам факт оплата будет создаваться отдельно
     */
    public static final Set<String> PRODUCTS_NO_PAY_FACT;
    /**
     * продукты для которых договор создается без плана оплаты, сам план оплаты будет создаваться отдельно
     */
    public static final Set<String> PRODUCTS_NO_PAY_PLAN;
    /**
     * продукты для которых премия вычисляется по простой формуле, без использования калькуляторов и справочников
     */
    public static final Set<String> PRODUCTS_SIMPLE_CALC;
    /**
     * продукты, для которых суммы вычисляются по программе страхования
     */
    public static final Set<String> PRODUCTS_BY_PROGRAM_CALC;
    /**
     * продукты, для которых треубется вычитать один день при вычислении даты окончания действия договора
     */
    public static final Set<String> PRODUCTS_FINISH_MINUS_DAY;

    /**
     * коэффициент для вычисления премии по продукту 'Страхование ипотеки СБОЛ'
     */
    public static final double XML_MORTGAGE_PREM_MULT = 0.00225;
    /**
     * коэффициент для вычисления премии по продукту 'Страхование ипотеки СБОЛ 2.0'
     */
    public static final double XML_MORTGAGE20_PREM_MULT = 0.00225;

    /**
     * 'Страхование путешественников СБОЛ 2.0' - список системных имен рисков калькулятора для обязательных рисков
     */
    public static final List<Map<String, Object>> TRAVEL_CALC_FIXED_RISKS;

    /**
     * 'Страхование путешественников СБОЛ 2.0' - списки системных имен рисков калькулятора для опциональных рисков (ключ - наименование опции в XML)
     */
    public static final Map<String, List<Map<String, Object>>> TRAVEL_CALC_OPTIONAL_RISKS;

    protected static final String contractRootKeys = "Contract";
    private static final String contractRootKeysWithDot = contractRootKeys + ".";

    // флаг включения/отключения блоков отладочных опреаций, которые могут изменять и/или подменять данные, в том числе с целью расширенного протоколирования
    // (рекомендуется отключать по завершению отладочных проверок)
    public static final boolean isDebugCodeActive = false;
    private boolean isError = false;

    public static String[][] commonKeysRelations = {
            //<editor-fold defaultstate="collapsed" desc="основные данные договора">
            {"DOCUMENTDATE", "DateSigning"},
            {"SIGNDATE", "DateSigning"},
            {"PROGCODE", "InsProgram"},
            {"PRODCONFID", "PRODCONF.PRODCONFID"},
            {"PRODVERID", "PRODCONF.PRODVER.PRODVERID"},
            {"PRODSYSNAME", "PRODCONF.PRODVER.PROD.SYSNAME"},
            {"CONTREXTMAP.HBDATAVERID", "PRODCONF.HBDATAVERID"}, // идентификатор для показателей договора

            // валюты зависят от продукта:
            // рубль для всех продуктов, но для страхования путешественников всегда евро (согласно письму с уточнениями от 08.09.2015)
            {"INSAMCURRENCYID", "InsProduct", "1", "99001 > 1; 99002 > 1; 99003 > 3; 99004 > 1; 00008 > 1; 99005 > 1"}, // 1 = идентификатор рубля в REF_CURRENCY, 3 = идентификатор евро в REF_CURRENCY
            {"PREMCURRENCYID", "InsProduct", "1", "99001 > 1; 99002 > 1; 99003 > 3; 99004 > 1; 00008 > 1; 99005 > 1"}, // 1 = идентификатор рубля в REF_CURRENCY, 3 = идентификатор евро в REF_CURRENCY
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
            // не генерировать две первые полные строки адреса партисипанта в ParticipantCustomFacade при его создании - эти строки формируются в данном фасаде по особым (зависящим от продукта) правилам
            // (третья строка адреса будет сформирована при создании партисипанта в ParticipantCustomFacade)
            {"INSURERMAP.addressList.RegisterAddress." + CRM_SKIP_ADDRTXT1_GEN_PARAMNAME, "", "true"},
            {"INSURERMAP.addressList.RegisterAddress." + CRM_SKIP_ADDRTXT2_GEN_PARAMNAME, "", "true"},
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
            {"members", "SYNTHETICKEY"}, // застрахованные путешественники черехапа
            {"members.member", "SYNTHETICKEY"}, // застрахованные путешественники
            {"members.members", "SYNTHETICKEY"}, // застрахованные путешественники для СБОЛ 2.0
            {"PRODCONF.PRODVER.PRODSTRUCTS", "SYSNAME"}, // элементы структуры объекта
            {"PRODCONF.PRODVER.PRODPROGS", "PROGCODE"}, // программы страхования
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

    /**
     * Соответствие валюты региону страхования (только для ВЗР)
     */
    protected static final Map<Long, Long> travelCurrencyByInsuranceTerritory;
    // Регион действия полиса - значение показателя insuranceTerritory в расш. атрибутах договора (только для ВЗР)
    protected static final Long TRAVEL_INS_REGIONS_NO_USA_RF = 0L; // "Весь мир, кроме США и РФ"
    protected static final Long TRAVEL_INS_REGIONS_NO_RF = 1L; // "США"
    protected static final Long TRAVEL_INS_REGIONS_RF_SNG = 2L; // "РФ"

    static {
        // мапа для получения системного наименования продукта по ИД шаблона и коду продукта СБОЛ из XML
        PRODUCT_SYSNAMES = new HashMap<Long, Map<String, String>>();

        // Продукты СБОЛ
        // 'Защита карт СБОЛ'
        addProductSysNameRule(TEMPLATE_ID_PRODUCT_SBOL, PRODUCT_CODE_SBOL_CIB, PRODUCT_SYSNAME_SBOL_CIB);
        // 'Защита дома СБОЛ'
        addProductSysNameRule(TEMPLATE_ID_PRODUCT_SBOL, PRODUCT_CODE_SBOL_HIB, PRODUCT_SYSNAME_SBOL_HIB);
        // 'Страхование путешественников СБОЛ'
        addProductSysNameRule(TEMPLATE_ID_PRODUCT_SBOL, PRODUCT_CODE_SBOL_TRAVEL, PRODUCT_SYSNAME_SBOL_TRAVEL);
        // 'Страхование ипотеки СБОЛ'
        addProductSysNameRule(TEMPLATE_ID_PRODUCT_SBOL, PRODUCT_CODE_SBOL_MORTGAGE, PRODUCT_SYSNAME_SBOL_MORTGAGE);
        // 'Защита от клеща (Ростелеком)'
        addProductSysNameRule(TEMPLATE_ID_PRODUCT_SBOL, PRODUCT_CODE_SBOL_ANTIMITE_ROS, PRODUCT_SYSNAME_SBOL_ANTIMITE_ROS);
        // 'Защита от клеща (Ростелеком)'
        addProductSysNameRule(TEMPLATE_ID_PRODUCT_SBOL, PRODUCT_CODE_SBOL_HOME_JKH, PRODUCT_SYSNAME_SBOL_HOME_JKH);

        // Продукты СБОЛ 2.0
        // 'Защита карт СБОЛ 2.0'
        addProductSysNameRule(TEMPLATE_ID_PRODUCT_SBOL_20, PRODUCT_CODE_SBOL_CIB, PRODUCT_SYSNAME_SBOL_CIB_20);
        // 'Защита дома СБОЛ 2.0'
        addProductSysNameRule(TEMPLATE_ID_PRODUCT_SBOL_20, PRODUCT_CODE_SBOL_HIB, PRODUCT_SYSNAME_SBOL_HIB_20);
        // 'Страхование ипотеки СБОЛ 2.0'
        addProductSysNameRule(TEMPLATE_ID_PRODUCT_SBOL_20, PRODUCT_CODE_SBOL_MORTGAGE, PRODUCT_SYSNAME_SBOL_MORTGAGE_20);
        // 'Страхование путешественников СБОЛ 2.0'
        addProductSysNameRule(TEMPLATE_ID_PRODUCT_SBOL_20, PRODUCT_CODE_SBOL_TRAVEL, PRODUCT_SYSNAME_SBOL_TRAVEL_20);

        // мапа для получения массива продуктозависимых соответствий ключей по системному имени продукта
        PRODUCT_KEYS_RELATIONS = new HashMap<>();
        //PRODUCT_KEYS_RELATIONS.put(PRODUCT_SYSNAME_CIB_ONLINE, cibKeysRelations);

        // НАБОРЫ ПРОДУКТОВ ДЛЯ УСЛОВНОГО ФУНКЦИОНАЛА
        // продукты, для которых договор создается без факта оплаты, сам факт оплаты будет создаваться отдельно
        PRODUCTS_NO_PAY_FACT = initProductSet(
                PRODUCT_SYSNAME_SBOL_ANTIMITE_ROS,
                PRODUCT_SYSNAME_SBOL_CIB_20,
                PRODUCT_SYSNAME_SBOL_HIB_20,
                PRODUCT_SYSNAME_SBOL_TRAVEL_20,
                PRODUCT_SYSNAME_SBOL_MORTGAGE_20
        );
        // продукты, для которых договор создается без плана оплаты, сам план оплаты будет создаваться отдельно
        PRODUCTS_NO_PAY_PLAN = initProductSet(
                //PRODUCT_SYSNAME_SBOL_ANTIMITE_ROS, // для клеща план создается, а факт - не создается
                PRODUCT_SYSNAME_SBOL_CIB_20,
                PRODUCT_SYSNAME_SBOL_HIB_20,
                PRODUCT_SYSNAME_SBOL_TRAVEL_20,
                PRODUCT_SYSNAME_SBOL_MORTGAGE_20
        );
        // продукты, для которых НЕ требуется генерация полной строки адреса регистрации страхователя
        // (для этих продуктов полной строки адреса передается в поле Region самого адреса, вместо только региона)
        PRODUCTS_FULL_ADDRESS_IN_REGION = initProductSet(
                PRODUCT_SYSNAME_SBOL_HIB,
                PRODUCT_SYSNAME_SBOL_MORTGAGE,
                PRODUCT_SYSNAME_SBOL_HOME_JKH,
                PRODUCT_SYSNAME_SBOL_CIB_20,
                PRODUCT_SYSNAME_SBOL_HIB_20,
                PRODUCT_SYSNAME_SBOL_MORTGAGE_20
        );
        // продукты, для которых требуется генерация полной строки адреса регистрации страхователя
        // (для этих продуктов полная строка адреса не передается в поле Region самого адреса,
        // вместо этого требуется "Слепить все поля через запятую")
        PRODUCTS_FULL_ADDRESS_GEN = initProductSet(
        );
        // продукты, для которых суммы вычисляются по программе страхования
        PRODUCTS_BY_PROGRAM_CALC = initProductSet(
                PRODUCT_SYSNAME_SBOL_HIB,
                PRODUCT_SYSNAME_SBOL_CIB,
                PRODUCT_SYSNAME_SBOL_TRAVEL,
                PRODUCT_SYSNAME_SBOL_HOME_JKH,
                PRODUCT_SYSNAME_SBOL_HIB_20,
                PRODUCT_SYSNAME_SBOL_CIB_20
        );
        // продукты, для которых премия вычисляется по простой формуле, без использования калькуляторов и справочников
        PRODUCTS_SIMPLE_CALC = initProductSet(
                PRODUCT_SYSNAME_SBOL_MORTGAGE,
                PRODUCT_SYSNAME_SBOL_MORTGAGE_20
        );
        // продукты, для которых треубется вычитать один день при вычислении даты окончания действия договора
        PRODUCTS_FINISH_MINUS_DAY = initProductSet(
                PRODUCT_SYSNAME_SBOL_CIB,
                PRODUCT_SYSNAME_SBOL_HIB,
                PRODUCT_SYSNAME_SBOL_MORTGAGE,
                PRODUCT_SYSNAME_SBOL_HOME_JKH,
                PRODUCT_SYSNAME_SBOL_CIB_20,
                PRODUCT_SYSNAME_SBOL_HIB_20,
                PRODUCT_SYSNAME_SBOL_MORTGAGE_20
        );

        //<editor-fold defaultstate="collapsed" desc="'Страхование путешественников СБОЛ 2.0' - списки и соотношения для системных имен рисков калькулятора">
        // 'Страхование путешественников СБОЛ 2.0' - список системных имен рисков калькулятора для обязательных рисков
        TRAVEL_CALC_FIXED_RISKS = newCalcRiskMapsListBySysNames("VZRmedical", "VZRmedKidsEvac", "VZRmedVisit", "VZRmedMessages", "VZRmedTransCosts", "VZRmedDocLoss", "VZRmedSearchRescue", "VZRmedHotel", "VZRmedTranslator");
        // 'Страхование путешественников СБОЛ 2.0' - списки системных имен рисков калькулятора для опциональных рисков (ключ - наименование опции в XML)
        // по данным из интерфейса онлайн-продукта (см. dopPackList в app.js), согласно клиенту - соотношение опций и рисков для онлайн и сбол 2.0 совпадает
        TRAVEL_CALC_OPTIONAL_RISKS = new HashMap<String, List<Map<String, Object>>>();
        // Спортивный
        TRAVEL_CALC_OPTIONAL_RISKS.put("IsOptionSport", newCalcRiskMapsListBySysNames("VZRsport", "VZRsporttools", "VZRskypass"));
        // Защита багажа
        TRAVEL_CALC_OPTIONAL_RISKS.put("IsOptionBaggage", newCalcRiskMapsListBySysNames("VZRlootlost", "VZRlootdelay", "VZRflightdelay"));
        // Особый случай
        TRAVEL_CALC_OPTIONAL_RISKS.put("IsOptionSpecialCase", newCalcRiskMapsListBySysNames("VZRtripstop", "VZRns"));
        // Личный адвокат
        TRAVEL_CALC_OPTIONAL_RISKS.put("IsOptionLawyer", newCalcRiskMapsListBySysNames("VZRjuridical", "VZRgo"));
        // Предусмотрительный
        TRAVEL_CALC_OPTIONAL_RISKS.put("IsOptionPrudent", newCalcRiskMapsListBySysNames("VZRtripcancel"));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="'Страхование путешественников СБОЛ 2.0' - соответствие валюты региону страхования">
        /* Соответствие валюты региону страхования (только для ВЗР) */
        travelCurrencyByInsuranceTerritory = new HashMap<Long, Long>();
        // "Весь мир, кроме США и РФ" (CONTREXTMAP.insuranceTerritory = 0) -> Евро (3)
        travelCurrencyByInsuranceTerritory.put(TRAVEL_INS_REGIONS_NO_USA_RF, CURRENCY_ID_EUR);
        // "США" (CONTREXTMAP.insuranceTerritory = 1) -> Доллары (2)
        travelCurrencyByInsuranceTerritory.put(TRAVEL_INS_REGIONS_NO_RF, CURRENCY_ID_USD);
        // "РФ" (CONTREXTMAP.insuranceTerritory = 2) -> Доллары (2)
        travelCurrencyByInsuranceTerritory.put(TRAVEL_INS_REGIONS_RF_SNG, CURRENCY_ID_USD);
        //</editor-fold>        

    }

    // добавление в PRODUCT_SYSNAMES правила определение сис. имени продукта по ИД шаблона и коду продукта СБОЛ из XML
    private static void addProductSysNameRule(Long templateID, String productXMLCode, String productB2BSysName) {
        if ((templateID != null) && (productXMLCode != null) && (productB2BSysName != null) && (!productXMLCode.isEmpty()) && (!productB2BSysName.isEmpty())) {
            Map<String, String> sysNameByCode = PRODUCT_SYSNAMES.get(templateID);
            if (sysNameByCode == null) {
                sysNameByCode = new HashMap<String, String>();
                PRODUCT_SYSNAMES.put(templateID, sysNameByCode);
            }
            sysNameByCode.put(productXMLCode, productB2BSysName);
        }
    }

    // инициализация набора системных имен продуктов для условного функционала
    private static Set<String> initProductSet(String... productSysNames) {
        Set<String> productSet = new HashSet<String>();
        productSet.addAll(Arrays.asList(productSysNames));
        return productSet;
    }

    protected static Map<String, Object> newCalcRiskMapBySysName(String riskCalcSysName) {
        Map<String, Object> calcRiskMap = new HashMap<String, Object>();
        calcRiskMap.put("PRODRISKSYSNAME", riskCalcSysName);
        return calcRiskMap;
    }

    protected static List<Map<String, Object>> newCalcRiskMapsListBySysNames(String... riskCalcSysNames) {
        List<Map<String, Object>> calcRiskMapsList = new ArrayList<Map<String, Object>>();
        for (String riskCalcSysName : riskCalcSysNames) {
            Map<String, Object> calcRiskMap = newCalcRiskMapBySysName(riskCalcSysName);
            calcRiskMapsList.add(calcRiskMap);
        }
        return calcRiskMapsList;
    }

    protected String getProductSysName(Long templateID, String productXMLCode) {
        String productB2BSysName = null;
        if (PRODUCT_SYSNAMES != null) {
            Map<String, String> sysNameByCode = PRODUCT_SYSNAMES.get(templateID);
            if (sysNameByCode == null) {
                logger.error(String.format("Unable to find any B2B product system names for template with id = %d!", templateID));
            } else {
                productB2BSysName = sysNameByCode.get(productXMLCode);
                if (productB2BSysName == null) {
                    logger.error(String.format("Unable to find any B2B product system names for template with id = %d and product XML code = %s!", templateID, productXMLCode));
                }
            }
        } else {
            logger.error("Unable to find any B2B product system names rules!");
        }
        return productB2BSysName;
    }

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
    protected Object getLastElementByAtrrValue(List<Map<String, Object>> list, String attrName, String attrValue) {
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
        } else {
            parentMap = source;
            listObj = source.get(listKey);
        }

        if (listObj == null) {
            return;
        }

        List<Map<String, Object>> list = (List<Map<String, Object>>) listObj;
        List<String> sysNames = new ArrayList<String>();
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

    protected String concatFieldsWithCommaSpace(Map<String, Object> map, String... fieldNames) {
        String separatorStr = ", ";
        return concatFieldsWithSeparator(map, separatorStr, fieldNames);
    }

    protected String concatFieldsWithSeparator(Map<String, Object> map, String separatorStr, String... fieldNames) {
        StringBuilder sb = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValueStr = getStringParam(map, fieldName);
            if (!fieldValueStr.isEmpty()) {
                sb.append(fieldValueStr).append(separatorStr);
            }
        }
        int sbLength = sb.length();
        int separatorLength = separatorStr.length();
        if (sbLength >= separatorLength) {
            sb.setLength(sbLength - separatorLength);
        }
        return sb.toString();
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

    // вычисление даты окончания действия договора для ВЗР по справочнику программ
    protected void vzrCalcContractFinishDate(Map<String, Object> b2bContract, Map<String, Object> product, String programCode,
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

    protected void createObjectsAndRisksByHB(Map<String, Object> result, Map<String, Object> b2bContract, Map<String, Object> product, String programCode,
                                             Long requestId, String b2bProductSysName, String login, String password) throws Exception {

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
                        //if (xmlProductCode.equals(PRODUCT_CODE_SBOL_TRAVEL)) {
                        if ((b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_TRAVEL)) || (b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_TRAVEL_20))) {
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

    protected void applyCalcRateRule(Map<String, Object> b2bContract, Map<String, Object> product, String login, String password) throws Exception {
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
                            case 1:
                            case 2:
                                Double currencyRate = roundCurrencyRate(getExchangeRateByCurrencyID(currencyId, documentDate, login, password));
                                b2bContract.put("CURRENCYRATE", currencyRate);
                                break;
                            case 4:
                                b2bContract.put("CURRENCYRATE", bean.get("RATEVALUE"));
                                break;
                        }
                    }
                }
            }
        }
    }

    protected void returnErrorAndStopSaving(Map<String, Object> result, String ErrorMessage, Long requestId, String login, String password) throws Exception {
        isError = true;
        logger.debug("Ne udalos` sokhranit` dogovor. " + ErrorMessage);
        updateRequest(requestId, 1, login, password);
        result.put("Error", "Не удалось сохранить договор.");
        result.put("Reason", ErrorMessage);
    }

    protected String checkB2BContractSBOLParams(Map<String, Object> b2bContract, String b2bProductSysName) {
        String result = null;
        //if (xmlProductCode.equalsIgnoreCase(PRODUCT_CODE_SBOL_ANTIMITE_ROS)) {
        if (b2bProductSysName.equalsIgnoreCase(PRODUCT_SYSNAME_SBOL_ANTIMITE_ROS)) {
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

    protected void makeInternalTravel20Mapping(Map<String, Object> contract) {
        Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (contrExtMap != null) {
            if ((contract.get("PROGCODE") != null) && (contract.get("PROGCODE").toString().equals("00001"))
                    && (contrExtMap.get("insuranceTerritory") != null)) {
                if (contrExtMap.get("insuranceTerritory").toString().equalsIgnoreCase("00003")) {
                    contract.put("PROGCODE", "00004");
                }
            }
            if (contrExtMap.get("insuranceTerritory") != null) {
                Long terr = Long.valueOf(contrExtMap.get("insuranceTerritory").toString());
                contrExtMap.put("insuranceTerritory", terr - 1);
            }
            if (contrExtMap.get("annualPolicyType") != null) {
                Long policyType = Long.valueOf(contrExtMap.get("annualPolicyType").toString());
                contrExtMap.put("annualPolicyType", policyType - 1);
            }
        }
        // определение валюты по территории страхования (CONTREXTMAP.insuranceTerritory > PREMCURRENCYID / INSAMCURRENCYID)
        Long insuranceTerritory = getLongParamLogged(contrExtMap, "insuranceTerritory");
        Long currencyId = travelCurrencyByInsuranceTerritory.get(insuranceTerritory);
        // валюта по умолчанию
        if (currencyId == null) {
            currencyId = CURRENCY_ID_EUR;
        }
        // установка валюты в мапе договора
        contract.put("PREMCURRENCYID", currencyId);
        contract.put("INSAMCURRENCYID", currencyId);
    }

    // вычисление премии по продукту 'Страхование ипотеки СБОЛ'
    public static Double calcMortgageSBOLPremValue(Double insAmValue) {
        return roundSum(XML_MORTGAGE_PREM_MULT * insAmValue);
    }

    // вычисление премии по продукту 'Страхование ипотеки СБОЛ 2.0'
    // вычисление премии по продукту 'Пролонгация ипотеки через Телемаркетинг 2.0'
    public static Double calcMortgageSBOL20PremValue(Double insAmValue) {
        return roundSum(XML_MORTGAGE20_PREM_MULT * insAmValue);
    }

    protected void updateRequest(Long requestId, int stateId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("REQUESTSTATEID", stateId);
        params.put("REQUESTQUEUEID", requestId);
        params.put("PROCESSDATE", new Date());
        XMLUtil.convertDateToFloat(params);
        Map<String, Object> res = this.callService(Constants.B2BPOSWS, "dsB2BRequestQueueUpdate", params, login, password);
    }

    protected int getAge(Date signDate, Date birthDate) {
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

}
