package com.bivgroup.services.b2bposws.facade.pos.unirest;

import com.bivgroup.services.b2bposws.facade.pos.custom.ws.B2BContractWsBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.util.TranslitStringConverter;

import java.util.*;

import static com.bivgroup.services.b2bposws.facade.pos.mappers.RemapUtils.findProdProgSysNameInProdConfByProgramCode;

/**
 * @author sambucus
 */
public class B2BUniRestBaseFacade extends B2BContractWsBaseFacade {

    private static final Logger logger = Logger.getLogger(B2BUniRestBaseFacade.class);

    protected static final String THIS_SERVICE_NAME = Constants.B2BPOSWS;

    public static String[][] common20KeysRelations = {
            //<editor-fold defaultstate="collapsed" desc="основные данные договора">
            // Регион страхования (оформления полиса)
            {"INSREGIONCODE", "insRegion"},
            // Идентификатор договора во внешней системе ("Это ID в системе банка, необходимо сохранить на договор, по данному ключу будет загружаться оплата по договору")
            {"EXTERNALID", "externalID"},
            {"INSURERMAP.PARTICIPANTTYPE", "", "1"},
            {"INSURERMAP.ISBUSINESSMAN", "", "0"},
            {"INSURERMAP.ISCLIENT", "", "1"},
            {"INSURERMAP.LASTNAME", "insurer.surname"},
            {"INSURERMAP.FIRSTNAME", "insurer.name"},
            {"INSURERMAP.MIDDLENAME", "insurer.patronymic"},
            {"INSURERMAP.BIRTHDATE", "insurer.dateOfBirth"},
            {"INSURERMAP.CITIZENSHIP", "insurer.country", "0", "643 > 0; 000 > 1000"}, // преобразование (643/000 -> 0/1000; 0 - гражданин РФ, 1000 - иностранный гражданин), (согласно письму с уточнениями от 08.09.2015)
            {"INSURERMAP.GENDER", "insurer.sex", "0", "male > 0; female > 1"}, // преобразование (male/female -> 0/1)


            //<editor-fold defaultstate="collapsed" desc="документ страхователя">
            {"INSURERMAP.documentList.PassportRF.DOCTYPESYSNAME", "insurer.document.Kind", "PassportRF", "21 > PassportRF; 10 > ForeignPassport"}, // (согласно письму с уточнениями от 08.09.2015)
            //{"INSURERMAP.documentList.PassportRF.CODE", ""}, // код типа документа, если есть системное имя (DOCTYPESYSNAME) - избыточен
            {"INSURERMAP.documentList.PassportRF.DOCSERIES", "insurer.document.series"},
            {"INSURERMAP.documentList.PassportRF.DOCNUMBER", "insurer.document.no"},
            {"INSURERMAP.documentList.PassportRF.ISSUEDATE", "insurer.document.dateOfIssue"},
            {"INSURERMAP.documentList.PassportRF.ISSUEDBY", "insurer.document.authority"},
            {"INSURERMAP.documentList.PassportRF.ISSUERCODE", "insurer.document.issuerCode"},
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="контактные данные страхователя">
            {"INSURERMAP.contactList.MobilePhone.VALUE", "insurer.mobileTel"}, // телефон
            {"INSURERMAP.contactList.PersonalEmail.VALUE", "insurer.email"}, // почта
            {"EMAIL", "insurer.email"},
    };

    public static final String[][] vzrCherehapaKeysRelations = {
            //<editor-fold defaultstate="collapsed" desc="ВЗР Черехапа. рест">
            // ВЗР Черехапа. рест 2.0 - структура (только тип)
            // {"INSOBJGROUPLIST.00000", "PRODCONF.PRODVER.PRODSTRUCTS.00000"}, // целиком копировать этот узел нельзя - будут потеряны уже добавленные значения вида INSOBJGROUPLIST.00000.*
            {"INSOBJGROUPLIST.00000.SYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.00000.SYSNAME"},
            {"INSOBJGROUPLIST.00000.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.PRODSTRUCTID"},
            {"INSOBJGROUPLIST.00000.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.HBDATAVERID"},
            //<editor-fold defaultstate="collapsed" desc=" Страхование путешественников СБОЛ 2.0 - застрахованные">
            //  Страхование путешественников СБОЛ 2.0 - застрахованные
            // если в sbersbolintegrationtypes используется globalBindings.xjb (для <xjc:simple/>), то мемберы будут вида Members.Members.X
            // если в sbersbolintegrationtypes не используется globalBindings.xjb, то мемберы будут вида Members.Member.X
            {"MEMBERLIST.0.NAME_ENG", "members.0.name"},
            {"MEMBERLIST.0.SURNAME_ENG", "members.0.surname"},
            {"MEMBERLIST.0.BIRTHDATE", "members.0.dateOfBirth"},

            {"MEMBERLIST.1.NAME_ENG", "members.1.name"},
            {"MEMBERLIST.1.SURNAME_ENG", "members.1.surname"},
            {"MEMBERLIST.1.BIRTHDATE", "members.1.dateOfBirth"},

            {"MEMBERLIST.2.NAME_ENG", "members.2.name"},
            {"MEMBERLIST.2.SURNAME_ENG", "members.2.surname"},
            {"MEMBERLIST.2.BIRTHDATE", "members.2.dateOfBirth"},

            {"MEMBERLIST.3.NAME_ENG", "members.3.name"},
            {"MEMBERLIST.3.SURNAME_ENG", "members.3.surname"},
            {"MEMBERLIST.3.BIRTHDATE", "members.3.dateOfBirth"},

            {"MEMBERLIST.4.NAME_ENG", "members.4.name"},
            {"MEMBERLIST.4.SURNAME_ENG", "members.4.surname"},
            {"MEMBERLIST.4.BIRTHDATE", "members.4.dateOfBirth"},

            {"MEMBERLIST.5.NAME_ENG", "members.5.name"},
            {"MEMBERLIST.5.SURNAME_ENG", "members.5.surname"},
            {"MEMBERLIST.5.BIRTHDATE", "members.5.dateOfBirth"},
            //</editor-fold>
            // Страхование путешественников СБОЛ 2.0 - расш. атрибуты договора
            {"CONTREXTMAP.beginTravelDATE", "DateBegin"},
            {"CONTREXTMAP.signingDATE", "DateSigning"}, // копия из "Страхование путешественников СБОЛ", todo: проверить, используется ли где-либо
            {"CONTREXTMAP.insuranceTerritory", "insTerritory"}, // без конвертации, согласно клиенту - значения такие же как в B2B продукте 'Страхование путешественников Онлайн'
            {"CONTREXTMAP.dayCount", "countOfDays"},
            {"CONTREXTMAP.babes", "insuredGroup2"}, // "Кол-во застрахованных: 0-2 лет"
            {"CONTREXTMAP.adultsAndChildren3_60", "insuredGroup1"}, // "Кол-во застрахованных: 3-60 лет"
            {"CONTREXTMAP.old61_70", "insuredGroup3"}, // "Кол-во застрахованных: 61-70 лет"
            {"CONTREXTMAP.optionSport", "isOptionSport", "0", "false > 0; true > 1"},
            {"CONTREXTMAP.annualPolicy", "isYearContract", "0", "false > 0; true > 1"},
            {"CONTREXTMAP.annualPolicyType", "typeYearContract"}, // без конвертации, согласно клиенту - значения такие же как в B2B продукте 'Страхование путешественников Онлайн'
            // Страхование путешественников СБОЛ 2.0 - осн. данные договора
            {"STARTDATE", "dateBeginTravel"},
            {"FINISHDATE", "dateEndTravel"},
            {"DURATION", "countOfDays"}, //</editor-fold>
    };

    public static String[][] commonCalc20KeysRelations = {
            //<editor-fold defaultstate="collapsed" desc="основные данные договора">
            // Регион страхования (оформления полиса)
            {"PROGCODE", "InsProgram"},
            // Идентификатор договора во внешней системе ("Это ID в системе банка, необходимо сохранить на договор, по данному ключу будет загружаться оплата по договору")
            //<editor-fold defaultstate="collapsed" desc="контактные данные страхователя">

    };

    public static final String[][] vzrCalcCherehapaKeysRelations = {
            //<editor-fold defaultstate="collapsed" desc="ВЗР Черехапа. рест">
            // ВЗР Черехапа. рест 2.0 - структура (только тип)
            // {"INSOBJGROUPLIST.00000", "PRODCONF.PRODVER.PRODSTRUCTS.00000"}, // целиком копировать этот узел нельзя - будут потеряны уже добавленные значения вида INSOBJGROUPLIST.00000.*
            {"INSOBJGROUPLIST.00000.SYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.00000.SYSNAME"},
            {"INSOBJGROUPLIST.00000.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.PRODSTRUCTID"},
            {"INSOBJGROUPLIST.00000.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.HBDATAVERID"},
            // Страхование путешественников СБОЛ 2.0 - расш. атрибуты договора
            {"CONTREXTMAP.beginTravelDATE", "DateBegin"},
            {"CONTREXTMAP.signingDATE", "DateSigning"}, // копия из "Страхование путешественников СБОЛ", todo: проверить, используется ли где-либо
            {"CONTREXTMAP.insuranceTerritory", "insTerritory"}, // без конвертации, согласно клиенту - значения такие же как в B2B продукте 'Страхование путешественников Онлайн'
            {"CONTREXTMAP.dayCount", "countOfDays"},
            {"CONTREXTMAP.babes", "insuredGroup2"}, // "Кол-во застрахованных: 0-2 лет"
            {"CONTREXTMAP.adultsAndChildren3_60", "insuredGroup1"}, // "Кол-во застрахованных: 3-60 лет"
            {"CONTREXTMAP.old61_70", "insuredGroup3"}, // "Кол-во застрахованных: 61-70 лет"
            {"CONTREXTMAP.optionSport", "isOptionSport", "0", "false > 0; true > 1"},
            {"CONTREXTMAP.annualPolicy", "isYearContract", "0", "false > 0; true > 1"},
            {"CONTREXTMAP.annualPolicyType", "typeYearContract"}, // без конвертации, согласно клиенту - значения такие же как в B2B продукте 'Страхование путешественников Онлайн'
            // Страхование путешественников СБОЛ 2.0 - осн. данные договора
            {"STARTDATE", "dateBeginTravel"},
            {"FINISHDATE", "dateEndTravel"},
            {"DURATION", "countOfDays"}, //</editor-fold>
    };

    public static final String[][] rest2CibKeysRelations = {
            {"PROGCODE", "insProgram", "", "00001 > 3; 00002 > 0; 00003 > 1; 00004 > 4; 00005 > 5"},
            {"B2BPROMOCODE", "promo"},
            {"CURRENCYRATE", "", "1", ""}
    };

    public static final String[][] rest2CibYouthKeysRelations = {
            {"PROGCODE", "insProgram", "", "00001 > 0; 00002 > 1; 00003 > 2; 00004 > 3"},
            {"B2BPROMOCODE", "promo"},
            {"CURRENCYRATE", "", "1", ""}
    };

    public static final String[][] rest2HibKeysRelations = {
            {"INSOBJGROUPLIST.00000.SYSNAME", "PRODCONF.PRODVER.PRODSTRUCTS.00000.SYSNAME"},
            {"INSOBJGROUPLIST.00000.PRODSTRUCTID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.PRODSTRUCTID"},
            {"INSOBJGROUPLIST.00000.HBDATAVERID", "PRODCONF.PRODVER.PRODSTRUCTS.00000.HBDATAVERID"},

            {"B2BPROMOCODE", "promo"},
            {"CONTREXTMAP.insObject", "typeProperty", "", "house > 0; flat > 1"},
            // ремаппинг из кодов ФТ в коды базы
            {"PROGCODE", "insProgram", "", "00001 > 000001; 00002 > 000002; 00003 > 000003; 00004 > 000004"},
            {"CURRENCYRATE", "", "1", ""}
    };

    public static final String[][] rest2VzrKeysRelations = {
            //<editor-fold defaultstate="collapsed" desc="ВЗР Черехапа. рест">
            // ВЗР Черехапа. рест 2.0 - структура (только тип)
            // {"INSOBJGROUPLIST.00000", "PRODCONF.PRODVER.PRODSTRUCTS.00000"}, // целиком копировать этот узел нельзя - будут потеряны уже добавленные значения вида INSOBJGROUPLIST.00000.*
            {"CONTREXTMAP.beginTravelDATE", "DateBegin"},
            {"CONTREXTMAP.signingDATE", "DateSigning"}, // копия из "Страхование путешественников СБОЛ", todo: проверить, используется ли где-либо
            //{"CONTREXTMAP.insuranceTerritory", "insTerritory"},
            {"CONTREXTMAP.insuranceTerritory", "insTerritory", "", "00001 > 0; 00002 > 1; 00003 > 2"},
            // без конвертации, согласно клиенту - значения такие же как в B2B продукте 'Страхование путешественников Онлайн'
            {"CONTREXTMAP.dayCount", "countOfDays"},
            {"CONTREXTMAP.babes", "insuredGroup2"}, // "Кол-во застрахованных: 0-2 лет"
            {"CONTREXTMAP.adultsAndChildren3_60", "insuredGroup1"}, // "Кол-во застрахованных: 3-60 лет"
            {"CONTREXTMAP.old61_70", "insuredGroup3"}, // "Кол-во застрахованных: 61-70 лет"
            {"CONTREXTMAP.optionSport", "isOptionSport", "0", "false > 0; true > 1"},
            {"CONTREXTMAP.annualPolicy", "isYearContract", "0", "false > 0; true > 1"},
            {"CONTREXTMAP.annualPolicyType", "typeYearContract"}, // без конвертации, согласно клиенту - значения такие же как в B2B продукте 'Страхование путешественников Онлайн'

            {"CONTREXTMAP.insuranceProgram", "insProgram", "", "00001 > 0; 00002 > 1; 00003 > 2"},
            /*{"PRODPROGCODE", "insProgram", },*/
            {"PROGCODE", "insProgram", "", "00001 > 0; 00002 > 1; 00003 > 2"},

            {"B2BPROMOCODE", "promo"},

            // Страхование путешественников СБОЛ 2.0 - осн. данные договора
            {"STARTDATE", "dateBeginTravel"},
            {"FINISHDATE", "dateEndTravel"},
            {"DURATION", "countOfDays"}
            //</editor-fold>
    };

    public static final String[][] rest2MortgageKeysRelations = {
            {"INSAMVALUE", "insAmount"},
            {"B2BPROMOCODE", "promo"},
            {"CURRENCYRATE", "", "1", ""},
            // #17933 в прекалькулятор добавлены новые параметры yearOfConstruction, isWood
            // #16834 калькулятор учитывает новые параметры
            {"CONTREXTMAP.insObject", "typeProperty", "", "house > 0; flat > 1"},
            {"CONTREXTMAP.buildYear", "yearOfConstruction", ""},
            {"CONTREXTMAP.woodInCeilings", "isWood", "", "true > 1; false > 0"},
            //{"CONTREXTMAP.creditStartDATE", "dateBegin"},
            //{"INSOBJGROUPLIST.00000.PROPERTYSYSNAME", "typeProperty"}
    };

    public static final String[][] rest2AntiMiteKeysRelations = {
            {"B2BPROMOCODE", "promo"},
            {"CURRENCYRATE", "", "1", ""}
            //{"INSOBJGROUPLIST.00000.OBJLIST.000000014.INSOBJMAP", "PRODCONF.PRODVER.PRODSTRUCTS.000000014"},
    };

    public static final String[][] restProtectBorrowerLongTermKeysRelations = {

    };

    public static final String[][] restProtectBorrowerLongTermCalcKeysRelations = {

    };

    public B2BUniRestBaseFacade() {
        super();
        init();
    }

    static {
        PRODUCT_KEYS_RELATIONS.put(PRODUCT_SYSNAME_VZR_CHEREHAPA, vzrCherehapaKeysRelations);
        PRODUCT_KEYS_RELATIONS.put(PRODUCT_SYSNAME_VZR_CHEREHAPA + "_CALC", vzrCalcCherehapaKeysRelations);
        PRODUCT_KEYS_RELATIONS.put("MORTGAGE_CLPBM", restProtectBorrowerLongTermKeysRelations);
        PRODUCT_KEYS_RELATIONS.put("MORTGAGE_CLPBM_CALC", restProtectBorrowerLongTermCalcKeysRelations);

        PRODUCT_KEYS_RELATIONS.put(PRODUCT_SYSNAME_REST2_CIB_ONLINE, rest2CibKeysRelations);
        PRODUCT_KEYS_RELATIONS.put(PRODUCT_SYSNAME_REST2_HIB_ONLINE, rest2HibKeysRelations);
        PRODUCT_KEYS_RELATIONS.put(PRODUCT_SYSNAME_REST2_VZR_ONLINE, rest2VzrKeysRelations);
        PRODUCT_KEYS_RELATIONS.put(PRODUCT_SYSNAME_REST2_MORTGAGE_ONLINE, rest2MortgageKeysRelations);
        PRODUCT_KEYS_RELATIONS.put(PRODUCT_SYSNAME_REST2_ANTIMITE_ONLINE, rest2AntiMiteKeysRelations);
        PRODUCT_KEYS_RELATIONS.put(PRODUCT_SYSNAME_REST2_CIB_YOUTH, rest2CibYouthKeysRelations);
        // Спортивный
        TRAVEL_CALC_OPTIONAL_RISKS.put("isOptionSport", newCalcRiskMapsListBySysNames("VZRsport", "VZRsporttools", "VZRskypass"));
        // Защита багажа
        TRAVEL_CALC_OPTIONAL_RISKS.put("isOptionBaggage", newCalcRiskMapsListBySysNames("VZRlootlost", "VZRlootdelay", "VZRflightdelay"));
        // Особый случай
        TRAVEL_CALC_OPTIONAL_RISKS.put("isOptionSpecialCase", newCalcRiskMapsListBySysNames("VZRtripstop", "VZRns"));
        // Личный адвокат
        TRAVEL_CALC_OPTIONAL_RISKS.put("isOptionLawyer", newCalcRiskMapsListBySysNames("VZRjuridical", "VZRgo"));
        // Предусмотрительный
        TRAVEL_CALC_OPTIONAL_RISKS.put("isOptionPrudent", newCalcRiskMapsListBySysNames("VZRtripcancel"));
    }

    private void init() {
        // соответствие сис. наименований продуктов (json - b2b)
        // todo: мб перенести в справочник или в PRODDEFVAL-ы
      /*  if (productSysNames == null) {
            productSysNames = new HashMap<String, String>();
        }
        productSysNames.put("cherehapa", SYSNAME_VZR_CHEREHAPA); // "Защита дома Онлайн"*/
        // todo: и т.д.
    }

    protected String getProductSysNameByParams(Map<String, Object> params) {
        if (params.get("DATAMAP") != null) {
            Map<String, Object> dataMap = (Map<String, Object>) params.get("DATAMAP");
            if (dataMap.get("PRODSYSNAME") != null) {
                return dataMap.get("PRODSYSNAME").toString();
            }
        }
        return "unknown";
    }

    // генерация дополнительных вычисляемых параметров, если не были переданы в явном виде для сохранения
    protected void genAdditionalSaveParams(Map<String, Object> result, Map<String, Object> contract, Map<String, Object> product, String b2bProductSysName, Long requestId, String login, String password) throws Exception {

        logger.debug("");

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
        logger.debug("Contract start date (STARTDATE) from request = " + startDateObj);
        if (startDateObj == null) {
            logger.debug("Contract start date will be calculated...");
            //if (xmlProductCode.equalsIgnoreCase(PRODUCT_CODE_SBOL_MORTGAGE)) {
            if (b2bProductSysName.equalsIgnoreCase(PRODUCT_SYSNAME_SBOL_MORTGAGE)) {
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
            //if (xmlProductCode.equals(PRODUCT_CODE_SBOL_CIB) || xmlProductCode.equals(PRODUCT_CODE_SBOL_HIB)) {
            if (b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_CIB) || b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_HIB) ||
                    b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_HOME_JKH)) {
                startDateDaysShift = 14;  // ... + 14 дней" (для карты и дома)
            }
            //if (xmlProductCode.equalsIgnoreCase(PRODUCT_CODE_SBOL_ANTIMITE_ROS)) {
            if (b2bProductSysName.equalsIgnoreCase(PRODUCT_SYSNAME_SBOL_ANTIMITE_ROS)) {
                startDateDaysShift = 7;  // ... + 7 дней" (для клеща)
            }
            startDateGC.add(Calendar.DATE, startDateDaysShift); // ... + X дней"
            startDate = startDateGC.getTime();
            contract.put("STARTDATE", startDate);
            logger.debug("Calculated contract start date (STARTDATE) = " + startDate);
        } else {
            // Для СБОЛ 2.0 дата начала действия договора будет передана в явном виде и вычислять её (как это происходит для реализованных ранее продуктов СБОЛ) не требуется
            startDate = (Date) parseAnyDate(startDateObj, Date.class, "STARTDATE");
            if (startDate != null) {
                startDateGC.setTime(startDate);
                logger.debug("Contract start date from request is used.");
            }
        }

        // дата окончания договора
        Object finishDateObj = contract.get("FINISHDATE");
        Date finishDate; // = null;
        GregorianCalendar finishDateGC = new GregorianCalendar();
        logger.debug("Contract finish date (FINISHDATE) from request = " + finishDateObj);
        if (finishDateObj == null) {
            logger.debug("Contract finish date will be calculated...");
            finishDateGC.setTime(startDate); // "соответственно дата окончания должна быть «Дата начала» + ...
            if (b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_HOME_JKH)) {
                finishDateGC.add(Calendar.MONTH, 1);
            } else {
                finishDateGC.add(Calendar.YEAR, 1); // ... + 1 страховой год ...
            }
            int finishDateDaysShift = 0; // ... + 0 дней" (в обычных договорах)
            //if (xmlProductCode.equals(PRODUCT_CODE_SBOL_CIB) || xmlProductCode.equals(PRODUCT_CODE_SBOL_HIB)) {
            //if (b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_CIB) || b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_HIB) || b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_MORTGAGE)) {
            if (PRODUCTS_FINISH_MINUS_DAY.contains(b2bProductSysName)) {
                finishDateDaysShift = -1;  // ... - 1 день" (для карты, дома и др.)
            }
            ////else if (xmlProductCode.equals(PRODUCT_CODE_SBOL_MORTGAGE)) {
            //else if (b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_MORTGAGE)) {
            //    //finishDateGC.add(Calendar.MONTH, 1); // ... + 1 страховой месяц ...
            //}
            logger.debug("Contract finish date shift (days) = " + finishDateDaysShift);
            finishDateGC.add(Calendar.DATE, finishDateDaysShift); // ... - X дней"
            finishDate = finishDateGC.getTime();
            contract.put("FINISHDATE", finishDate);
            logger.debug("Calculated contract finish date (FINISHDATE) = " + finishDate);
        } else {
            finishDate = (Date) parseAnyDate(finishDateObj, Date.class, "FINISHDATE");
            if (finishDate != null) {
                finishDateGC.setTime(finishDate);
                logger.debug("Contract finish date from request is used.");
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
            Long progCodeLong = getLongParam(programCodeObj);
            contract.put("PROGCODE", progCodeLong);
            Object programID = chainedGet(product, "PRODVER.PRODPROGS." + progCodeLong.toString() + ".PRODPROGID");
            if (programID == null) {
                returnErrorAndStopSaving(result, "Указана несуществующая программа страхования", requestId, login, password);
            }
            //contract.put("PRODPROGID", programIDsBySysName.get(programSysName));
            contract.put("PRODPROGID", programID);
            Object programB2BCode = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".PROGCODE");
            //chainedCreativePut(contract, "CONTREXTMAP.insuranceProgram", programB2BCodesBySysName.get(programSysName));
            chainedCreativePut(contract, "CONTREXTMAP.insuranceProgram", progCodeLong.toString());
        }

        //if (xmlProductCode.equals(PRODUCT_CODE_SBOL_HIB) || xmlProductCode.equals(PRODUCT_CODE_SBOL_MORTGAGE)) {
        //if (b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_HIB) || b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_MORTGAGE)) {
        if (PRODUCTS_FULL_ADDRESS_IN_REGION.contains(b2bProductSysName)) {
            Map<String, Object> address = (Map<String, Object>) chainedGetIgnoreCase(contract, "INSURERMAP.addressList.RegisterAddress");
            String addressSt = getStringParamLogged(address, "REGION");
            /*
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
            String translitAdr = TranslitStringConverter.toTranslit(addressSt);
            chainedCreativePut(contract, "CONTREXTMAP.propertyAddress", addressSt);
            chainedCreativePut(contract, "INSOBJGROUPLIST.00000.ADDRESSTEXT1", addressSt);
            chainedCreativePut(contract, "INSOBJGROUPLIST.00000.ADDRESSTEXT2", addressSt);
            chainedCreativePut(contract, "INSOBJGROUPLIST.00000.ADDRESSTEXT3", translitAdr);
            chainedCreativePut(contract, "INSURERMAP.addressList.RegisterAddress.ADDRESSTEXT1", addressSt);
            chainedCreativePut(contract, "INSURERMAP.addressList.RegisterAddress.ADDRESSTEXT2", addressSt);
            chainedCreativePut(contract, "INSURERMAP.addressList.RegisterAddress.ADDRESSTEXT3", translitAdr);
        }

        // продукты, для которых требуется генерация полной строки адреса регистрации страхователя
        // (для этих продуктов полная строка адреса не передается в поле Region самого адреса,
        // вместо этого требуется "Слепить все поля через запятую")
        if (PRODUCTS_FULL_ADDRESS_GEN.contains(b2bProductSysName)) {
            logger.debug("");
            Map<String, Object> address = (Map<String, Object>) chainedGetIgnoreCase(contract, "INSURERMAP.addressList.RegisterAddress");
            String addressFullStr = concatFieldsWithCommaSpace(address, "REGION", "CITY", "STREET", "HOUSE", "BUILDING", "HOUSING", "FLAT");
            address.put("ADDRESSTEXT1", addressFullStr);
            logger.debug("Generated insurer register address full text string (ADDRESSTEXT1): " + addressFullStr);
            address.put("ADDRESSTEXT2", addressFullStr);
            logger.debug("Generated insurer register address full text string (ADDRESSTEXT2): " + addressFullStr);
        }

        // генерация полных строк адреса имущества - зависит от продукта
        if (b2bProductSysName.equalsIgnoreCase(PRODUCT_SYSNAME_SBOL_HIB_20)) {
            // 'Защита дома СБОЛ 2.0'
            Map<String, Object> propertyAddress = (Map<String, Object>) chainedGetIgnoreCase(contract, "INSOBJGROUPLIST.00000");
            String propertyAddressFullStr = concatFieldsWithCommaSpace(propertyAddress, "REGION", "CITY", "STREET", "HOUSE", "BUILDING", "HOUSING", "FLAT");
            propertyAddress.put("ADDRESSTEXT1", propertyAddressFullStr);
            logger.debug("Generated property address full text string (INSOBJGROUPLIST.00000.ADDRESSTEXT1): " + propertyAddressFullStr);
            propertyAddress.put("ADDRESSTEXT2", propertyAddressFullStr);
            logger.debug("Generated property address full text string (INSOBJGROUPLIST.00000.ADDRESSTEXT2): " + propertyAddressFullStr);
            String propertyAddressFullStrTransliterated = TranslitStringConverter.toTranslit(propertyAddressFullStr);
            propertyAddress.put("ADDRESSTEXT3", propertyAddressFullStrTransliterated);
            logger.debug("Generated property address full text string (INSOBJGROUPLIST.00000.ADDRESSTEXT3): " + propertyAddressFullStrTransliterated);
            chainedCreativePut(contract, "CONTREXTMAP.propertyAddress", propertyAddressFullStr);
            logger.debug("Generated property address full text string (CONTREXTMAP.propertyAddress): " + propertyAddressFullStr);
        } else if (b2bProductSysName.equalsIgnoreCase(PRODUCT_SYSNAME_SBOL_MORTGAGE_20)) {
            // 'Страхование ипотеки СБОЛ 2.0'
            Map<String, Object> propertyAddress = (Map<String, Object>) chainedGetIgnoreCase(contract, "INSOBJGROUPLIST.00000");
            String propertyAddressFullStr = concatFieldsWithCommaSpace(propertyAddress, "REGION", "CITY", "STREET", "HOUSE", "BUILDING", "HOUSING", "FLAT");
            propertyAddress.put("ADDRESSTEXT1", propertyAddressFullStr);
            logger.debug("Generated property address full text string (INSOBJGROUPLIST.00000.ADDRESSTEXT1): " + propertyAddressFullStr);
            propertyAddress.put("ADDRESSTEXT2", propertyAddressFullStr);
            logger.debug("Generated property address full text string (INSOBJGROUPLIST.00000.ADDRESSTEXT2): " + propertyAddressFullStr);
            String propertyAddressFullStrTransliterated = TranslitStringConverter.toTranslit(propertyAddressFullStr);
            propertyAddress.put("ADDRESSTEXT3", propertyAddressFullStrTransliterated);
            logger.debug("Generated property address full text string (INSOBJGROUPLIST.00000.ADDRESSTEXT3): " + propertyAddressFullStrTransliterated);
            chainedCreativePut(contract, "CONTREXTMAP.propertyAddress", propertyAddressFullStr);
            logger.debug("Generated property address full text string (CONTREXTMAP.propertyAddress): " + propertyAddressFullStr);
        }

        // безусловное перевычисление сумм
        logger.debug("");
        logger.debug("Selecting calculation rules for premium and insurance values recalculating...");
        //if (xmlProductCode.equals(PRODUCT_CODE_SBOL_HIB) || xmlProductCode.equals(PRODUCT_CODE_SBOL_CIB) || xmlProductCode.equals(PRODUCT_CODE_SBOL_TRAVEL)) {
        //if (b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_HIB) || b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_CIB) || b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_TRAVEL)) {
        if (PRODUCTS_BY_PROGRAM_CALC.contains(b2bProductSysName)) {
            logger.debug("Selected calculation rule - by sums from program.");
            if (!programCode.isEmpty()) {
                logger.debug("");
                logger.debug("Selecting insurance sum value and premium by insurance program...");
                Double insAmValue = getDoubleParam(chainedGetIgnoreCase(contract, "insAmValue"));
                logger.debug("Insurance sum value, passed directly by request = " + insAmValue);
                Double premValue = getDoubleParam(chainedGetIgnoreCase(contract, "premValue"));
                logger.debug("Premium value, passed directly by request = " + premValue);
                Object programInsAmValue = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".INSAMVALUE");
                if (programInsAmValue != null) {
                    contract.put("INSAMVALUE", programInsAmValue);
                    logger.debug("Insurance sum value, opredelennaia po programme = " + programInsAmValue);
                } else {
                    logger.debug("Unable to select insurance sum value by program - will be used value, passed directly by request.");
                }
                Object programPremValue = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".PREMVALUE");
                if (programPremValue != null) {
                    contract.put("PREMVALUE", programPremValue);
                    logger.debug("Premium value, opredelennaia po programme = " + programPremValue);
                } else {
                    logger.debug("Unable to select premium by program - will be used value, passed directly by request.");
                }
            } else {
                logger.warn("Unable to get insurance program system name - product dependent sum calculation will be skipped!");
            }

            //} else if (xmlProductCode.equals(PRODUCT_CODE_SBOL_MORTGAGE)) {
            //} else if (b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_MORTGAGE)) {
        } else if (PRODUCTS_SIMPLE_CALC.contains(b2bProductSysName)) {
            logger.debug("Selected calculation rule - by simple formula.");
            // премия вычисляется по простой формуле, без использования калькуляторов и справочников
            logger.debug("");
            logger.debug("Calculating premium value...");
            // страховая сумма
            Double insAmValue = getDoubleParam(chainedGetIgnoreCase(contract, "insAmValue"));
            logger.debug("Insurance sum value, passed directly by request = " + insAmValue);
            // страховая премия
            Double premValue = getDoubleParam(chainedGetIgnoreCase(contract, "premValue"));
            logger.debug("Premium value, passed directly by request = " + premValue);
            // пересчет премии - без использования калькулятора, т. к. формула простая
            if (b2bProductSysName.equalsIgnoreCase(PRODUCT_SYSNAME_SBOL_MORTGAGE)) {
                // вычисление премии по продукту 'Страхование ипотеки СБОЛ'
                premValue = calcMortgageSBOLPremValue(insAmValue);
            } else if (b2bProductSysName.equalsIgnoreCase(PRODUCT_SYSNAME_SBOL_MORTGAGE_20)) {
                // вычисление премии по продукту 'Страхование ипотеки СБОЛ 2.0'
                premValue = calcMortgageSBOL20PremValue(insAmValue);
            } else {
                logger.warn("Unable to calculate premium - will be used value, passed directly by request.");
            }
            logger.debug("Calculated premium value = " + premValue);
            contract.put("PREMVALUE", premValue);
            //} else if (xmlProductCode.equals(PRODUCT_CODE_SBOL_ANTIMITE_ROS)) {
        } else if (b2bProductSysName.equals(PRODUCT_SYSNAME_SBOL_ANTIMITE_ROS)) {
            logger.debug("Selected calculation rule - sbol antimite special mechanics.");
            // вероятно, какой-то особый продукт - перевычисление сумм оставлено в отдельной ветке if
            if (!programCode.isEmpty()) {
                logger.debug("");
                logger.debug("Selecting insurance sum value and premium by insurance program...");
                Double insAmValue = getDoubleParam(chainedGetIgnoreCase(contract, "insAmValue"));
                logger.debug("Insurance sum value, passed directly by request = " + insAmValue);
                Double premValue = getDoubleParam(chainedGetIgnoreCase(contract, "premValue"));
                logger.debug("Premium value, passed directly by request = " + premValue);
                Object programInsAmValue = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".INSAMVALUE");
                if (programInsAmValue != null) {
                    contract.put("INSAMVALUE", programInsAmValue);
                    logger.debug("Insurance sum value, opredelennaia po programme = " + programInsAmValue);
                } else {
                    logger.debug("Unable to select insurance sum value by program - will be used value, passed directly by request.");
                }
                Object programPremValue = chainedGet(product, "PRODVER.PRODPROGS." + programCode + ".PREMVALUE");
                if (programPremValue != null) {
                    contract.put("PREMVALUE", programPremValue);
                    logger.debug("Premium value, opredelennaia po programme = " + programPremValue);
                } else {
                    logger.debug("Unable to select premium by program - will be used value, passed directly by request.");
                }
            } else {
                logger.warn("Unable to get insurance program system name - product dependent sum calculation will be skipped!");
            }
        } else {
            logger.warn("No calculation rules for premium and insurance values was found - recalculating this values was skipped!");
        }
        logger.debug("");
    }

    protected void updateContractStructureForRest2(Map<String, Object> contrMap, String login, String password) throws Exception {
        String programCode = (String) contrMap.get("PROGCODE");
        Map<String, Object> prodConf = (Map<String, Object>) contrMap.get("PRODCONF");
        String prodProgSysName = findProdProgSysNameInProdConfByProgramCode(prodConf, programCode);
        contrMap.put("PRODPROGSYSNAME", prodProgSysName);
        contrMap.put("PRODPROGCODE", programCode);
        // параметр используется при применении промокода
        contrMap.put("PRODCONFID", prodConf.get("PRODCONFID"));
        updateContractInsuranceProductStructure(contrMap, prodConf, false, programCode, login, password);
    }

    protected Map<String, Object> discountApplicationResult(Map<String, Object> contrMapAppliedDiscount) {
        // false by default
        Boolean promoApplied = getBooleanParam(contrMapAppliedDiscount, "B2BPROMOAPPLIED", Boolean.FALSE);

        Map<String, Object> dataMap = new HashMap<String, Object>();
        if (contrMapAppliedDiscount.get("B2BPROMOCODE") == null) {
            dataMap.put("promoState", "no");
        } else if (promoApplied) {
            dataMap.put("promoState", "success");
        } else {
            dataMap.put("promoState", "fail");
        }

        //contrMapAppliedDiscount.put("DATAMAP", dataMap);
        return dataMap;
    }

}
