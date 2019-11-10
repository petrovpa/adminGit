/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.i900.system;

import com.bivgroup.ws.i900.Mort900Exception;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author mmamaev
 */
public class CommonPaymentPurposeProcessor {

    private final Logger logger = Logger.getLogger(this.getClass());
    private static DatesParser datesParser;

    // флаг подробного протоколирования операций с датами
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

    private static final String TOTALCOUNT = "TOTALCOUNT";

    public CommonPaymentPurposeProcessor() {
        super();
        init();
    }

    private void init() {
        datesParser = new DatesParser();
        // протоколирование операций с датами
        datesParser.setVerboseLogging(IS_VERBOSE_LOGGING);
    }

    //<editor-fold defaultstate="collapsed" desc="из ProductContractCustomFacade / B2BCustomFacade">
    protected Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    private String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else {
            return bean.toString();
        }
    }

    private Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="созданы в Mort900BaseFacade">
    private String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }

    private Integer getIntegerParam(Object bean, Integer defaultValue) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Integer.valueOf(bean.toString());
        } else {
            return defaultValue;
        }
    }

    private Integer getIntegerParam(Object bean) {
        return getIntegerParam(bean, 0);
    }

    private Integer getIntegerParam(Map<String, Object> map, String keyName, Integer defaultValue) {
        Integer integerParam = defaultValue;
        if (map != null) {
            integerParam = getIntegerParam(map.get(keyName), defaultValue);
        }
        return integerParam;
    }

    private Integer getIntegerParam(Map<String, Object> map, String keyName) {
        return getIntegerParam(map, keyName, 0);
    }
    //</editor-fold>

    private String getPurposeTemplateDetailNameBySysName(Map<String, Object> purposeTemplateDetailsMap, String sysName) throws Exception {
        String name = "<Имя детали не указано>";
        Map<String, Object> purposeTemplateDetail = (Map<String, Object>) purposeTemplateDetailsMap.get(sysName);
        if (purposeTemplateDetail != null) {
            name = getStringParam(purposeTemplateDetail, "NAME");
        }
        return name;
    }

    private Map<String, Integer> getPurposeTemplateDetailsNumsMap(Map<String, Object> purposeTemplateDetailsMap) throws Exception {
        // формирование мапы соответсвия системных имен деталей номерам блоков в строке с назначением платежа
        Map<String, Integer> purposeTemplateDetailsNumsMap = new HashMap<String, Integer>();
        for (Map.Entry<String, Object> purposeTemplateDetail : purposeTemplateDetailsMap.entrySet()) {
            String key = purposeTemplateDetail.getKey();
            logger.debug("Purpose template detail system name: " + key);
            Object value = purposeTemplateDetail.getValue();
            if (value instanceof Map) {
                Integer num = getIntegerParam((Map<String, Object>) value, "NUM");
                logger.debug("Purpose detail block number: " + num);
                purposeTemplateDetailsNumsMap.put(key, num - 1); // - 1 для получения из массива (в массиве zero-based индекс)
            }
        }
        return purposeTemplateDetailsNumsMap;
    }

    private String[] getPurposeArrayFromPurposeDetails(List<Map<String, Object>> bankPurposeDetails) throws Exception {

        int maxNum = 0;
        for (Map<String, Object> bankPurposeDetail : bankPurposeDetails) {
            int num = getLongParam(bankPurposeDetail.get("NUM")).intValue();
            if (num > maxNum) {
                maxNum = num;
            }
        }

        String[] purpose = new String[maxNum];
        for (int i = 0; i < purpose.length; i++) {
            purpose[i] = "";
        }

        for (Map<String, Object> bankPurposeDetail : bankPurposeDetails) {
            int num = getLongParam(bankPurposeDetail.get("NUM")).intValue();
            String value = getStringParam(bankPurposeDetail.get("VALUE"));
            logger.debug(num + " = " + value);
            purpose[num - 1] = value;
        }

        return purpose;

    }

    private Map<String, Object> getPurposeTemplateDetailsMap(List<Map<String, Object>> purposeTemplateDetails) throws Exception {

        logger.debug("Analyzing payment purpose details template...");

        // запрос из шаблона обработки банковской выписки списка деталей назначения платежа
        //List<Map<String, Object>> purposeTemplateDetails = getPurposeTemplateDetailsByBankStatementTemplateID(bankStatementTemplateID, login, password);
        // количество блоков (деталей) в назначении платежа согласно шаблону
        int purposeTemplateDetailsCount = purposeTemplateDetails.size();
        if (purposeTemplateDetailsCount == 0) {
            throw new Mort900Exception(
                    "Некорректный шаблон обработки банковской выписки: отсутствует описание блоков строки назначения платежа",
                    "Errors in bank statement processing tepmlate: purpose details templates not found"
            );
        }

        int purposeTemlateDetailMaxNum = 0;

        // формирование мапы из списка шаблонов деталей назначения платежа (ключ - системные имена шаблонов деталей)
        Map<String, Object> purposeTemplateDetailsMap = new HashMap<String, Object>();
        for (Map<String, Object> purposeTemlateDetail : purposeTemplateDetails) {
            // todo: возможно, доп. проверка на отсутствие дублей по порядковым номерам и общего количества?
            String purposeTemlateDetailSysName = getStringParam(purposeTemlateDetail, "SYSNAME");
            Integer purposeTemlateDetailNum = getIntegerParam(purposeTemlateDetail, "NUM", null);
            logger.debug("Payment purpose detail template info:");
            logger.debug("   System name (SYSNAME): " + purposeTemlateDetailSysName);
            logger.debug("   Block number (NUM): " + purposeTemlateDetailNum);
            // детали назначения платежа без указанного блока игнорируются - таковые не могут быть обнаружены в строке назначения платежа
            if (purposeTemlateDetailNum == null) {
                logger.debug("   May block will be useful for contract creation: no");
            } else {
                // детали назначения платежа без системных имен игнорируются - таковые не потребуются для создания договора
                if (!purposeTemlateDetailSysName.isEmpty()) {
                    // todo: возможно, доп. проверка на отсутствие дублей по системным именам?
                    purposeTemplateDetailsMap.put(purposeTemlateDetailSysName, purposeTemlateDetail);
                    logger.debug("   May block will be useful for contract creation: yes");
                } else {
                    logger.debug("   May block will be useful for contract creation: no");
                }
                // хотя обращения к блокам строки назначения платежа будут только по индексам, тем не менее максимальный номер может иметь блок без системного имени (содержащий не используемые для создания договора сведения)
                // кроме того, eMail является необязательным, поэтому его номер не требуется учитывать
                if ((purposeTemlateDetailMaxNum < purposeTemlateDetailNum) && (!"eMail".equals(purposeTemlateDetailSysName))) {
                    purposeTemlateDetailMaxNum = purposeTemlateDetailNum;
                }
            }
        }

        // проверка мапы
        if (purposeTemplateDetailsMap.isEmpty()) {
            throw new Mort900Exception(
                    "Некорректный шаблон обработки банковской выписки: отсутствуют описания блоков строки назначения платежа с заполненными системным именем и номером блока",
                    "Errors in bank statement processing tepmlate: purpose details templates with system name and block number not found"
            );
        }

        // дополнение сформированной мапы временной информацией - количеством блоков (деталей) в назначении платежа согласно шаблону
        //purposeTemplateDetailsMap.put(TOTALCOUNT, purposeTemplateDetailsCount);
        // дополнение сформированной мапы временной информацией - максимальным номером блока (детали) в назначении платежа согласно шаблону
        logger.debug("Maximum block number from template = " + purposeTemlateDetailMaxNum);
        purposeTemplateDetailsMap.put(TOTALCOUNT, purposeTemlateDetailMaxNum);

        logger.debug("Analyzing payment purpose details template finished.");
        return purposeTemplateDetailsMap;
    }

    protected boolean checkIsValueInvalidByRegExp(Object value, String regExp) {
        boolean isValueInvalid;
        Pattern pattern = Pattern.compile(regExp);
        String checkedString = getStringParam(value);
        Matcher matcher = pattern.matcher(checkedString);
        isValueInvalid = !matcher.matches();
        logger.debug("Value '" + checkedString + "' checked by regular expression '" + regExp + "' with result '" + isValueInvalid + "'.");
        return isValueInvalid;
    }

    public Map<String, Object> universalProcessPaymentPurpose(List<Map<String, Object>> purposeDetails, List<Map<String, Object>> purposeTemplateDetails, Map<String, Object> bankCashFlow) throws Exception, Mort900Exception {

        String[] purpose = getPurposeArrayFromPurposeDetails(purposeDetails);

        Map<String, Object> purposeTemplateDetailsMap = getPurposeTemplateDetailsMap(purposeTemplateDetails);

        // проверка количества элементов
        //int purposeMinLength = 13;
        int purposeMinLength = getIntegerParam(purposeTemplateDetailsMap.remove(TOTALCOUNT)); // количество блоков (деталей) в назначении платежа согласно шаблону
        if (purpose.length < purposeMinLength) {
            throw new Mort900Exception(
                    String.format("Назначение платежа не содержит достаточно данных для создания договора - требуется как минимум %d значений разделенных точкой с запятой, обнаружено только %d", purposeMinLength, purpose.length),
                    String.format("Рayment purpose does not contain enough records - required minimum [%d] values divided by semicolons, but found [%d]", purposeMinLength, purpose.length)
            );
        }

        // проверка констант
        for (Map<String, Object> purposeTemplateDetail : purposeTemplateDetails) {
            String constValue = getStringParam(purposeTemplateDetail, "CONSTANTVALUESTR");
            int constNum = getIntegerParam(purposeTemplateDetail, "NUM");
            if (!constValue.isEmpty()) {
                String value = purpose[constNum - 1];
                if (!constValue.equals(value)) {
                    throw new Mort900Exception(
                            String.format("Значение '%s' из блока номер %d назначения платежа не соответствует значению константы, установленной для данного блока описанием шаблона обработки банковской выписки", value, constNum),
                            String.format("Value [%s] from payment purpose block number [%d] is not equal to constant value, provided to this block by bank statement's processing template details", value, constNum)
                    );
                }
            }
        }

        // формирование мапы соответсвия системных имен деталей номерам блоков в строке с назначением платежа
        Map<String, Integer> purposeTemplateDetailsNumsMap = getPurposeTemplateDetailsNumsMap(purposeTemplateDetailsMap);

        // индекс текущего блока назначения платежа 
        Integer purposeDetailNum;
        // системное имя текущего блока назначения платежа 
        String purposeDetailSysName;
        // имя текущего блока назначения платежа 
        String purposeDetailName;

        List<String> emptyDetailsNames = new ArrayList<String>();

        // ФИО клиента - "Иванов Иван Иванович"
        String insurerLastName = null;
        String insurerFirstName = null;
        String insurerMiddleName = null;
        purposeDetailNum = purposeTemplateDetailsNumsMap.get("insurerFIO");
        if (purposeDetailNum != null) {
            String[] insurerFIO = purpose[purposeDetailNum].split(" ");
            //int insurerFIOMinLength = 3;
            int insurerFIOMinLength = 2; // (прим. от 07.04.2016: отчество - необязательно)
            if (insurerFIO.length < insurerFIOMinLength) {
                throw new Mort900Exception(
                        String.format("ФИО страхователя из назначения платежа не содержит достаточно данных для создания договора - требуется как минимум %d значения разделенных пробелом, обнаружено только %d", insurerFIOMinLength, insurerFIO.length),
                        String.format("Insurer fullname from payment purpose does not contain enough data - required minimum [%d] values divided by spaces, but found [%d]", insurerFIOMinLength, insurerFIO.length)
                );
            }
            insurerLastName = insurerFIO[0];
            if (insurerLastName.isEmpty()) {
                emptyDetailsNames.add("Фамилия страхователя");
            }
            insurerFirstName = insurerFIO[1];
            if (insurerFirstName.isEmpty()) {
                emptyDetailsNames.add("Имя страхователя");
            }
            if (insurerFIO.length > 2) {
                insurerMiddleName = insurerFIO[2];
                // (прим. от 07.04.2016: отчество - необязательно)
                //if (insurerMiddleName.isEmpty()) {
                //    emptyDetailsNames.add("Отчество страхователя");
                //}
            }

        }

        // Дата рождения - "01.01.2015"
        purposeDetailSysName = "insurerBirthDate";
        purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
        Date insurerBirthDate = null;
        if (purposeDetailNum != null) {
            String insurerBirthDateStr = purpose[purposeDetailNum];
            if (insurerBirthDateStr.isEmpty()) {
                purposeDetailName = getPurposeTemplateDetailNameBySysName(purposeTemplateDetailsMap, purposeDetailSysName);
                emptyDetailsNames.add(purposeDetailName);
            } else {
                insurerBirthDate = (Date) datesParser.parseAnyDate(insurerBirthDateStr, Date.class, "Дата рождения страхователя", true);
                if (insurerBirthDate == null) {
                    throw new Mort900Exception(
                            "Дата рождения страхователя из назначения платежа указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)",
                            "Insurer birth date from payment purpose is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)"
                    );
                }
            }
        }

        // Серия и номер паспорта - "12 34 567890"
        //String[] passportInfo = purpose[5].split(" ");
        String passportSeries = null;
        String passportNumber = null;
        passportSeries = "passportInfo";
        purposeDetailNum = purposeTemplateDetailsNumsMap.get(passportSeries);
        if (purposeDetailNum != null) {
            String[] passportInfo = purpose[purposeDetailNum].split(" ");
            int passportInfoMinLength = 3;
            if (passportInfo.length < passportInfoMinLength) {
                throw new Mort900Exception(
                        String.format("Серия и номер паспорта плательщика из назначения платежа не содержит достаточно данных для создания договора - требуется как минимум %d значения разделенных пробелом, обнаружено только %d", passportInfoMinLength, passportInfo.length),
                        String.format("Payer's passport series and number from payment purpose does not contain enough data - required minimum [%d] values divided by spaces, but found [%d]", passportInfoMinLength, passportInfo.length)
                );
            }
            passportSeries = passportInfo[0] + passportInfo[1];
            if (passportSeries.isEmpty()) {
                emptyDetailsNames.add("Серия паспорта плательщика");
            }
            passportNumber = passportInfo[2];
            if (passportNumber.isEmpty()) {
                emptyDetailsNames.add("Номер паспорта плательщика");
            }
        } else {

            // Серия паспорта отдельно - "12 34"
            purposeDetailSysName = "passportSeries";
            purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
            if (purposeDetailNum != null) {
                passportSeries = purpose[purposeDetailNum].replaceAll(" ", "");
                if (passportSeries.isEmpty()) {
                    purposeDetailName = getPurposeTemplateDetailNameBySysName(purposeTemplateDetailsMap, purposeDetailSysName);
                    emptyDetailsNames.add(purposeDetailName);
                } else if (checkIsValueInvalidByRegExp(passportSeries, "\\d{4}")) {
                    throw new Mort900Exception(
                            "Серия паспорта плательщика из назначения платежа не корректна - требуются четыре цифры",
                            "Payer's passport series from payment purpose is invalid - required four digits"
                    );
                }
            }

            // Номер паспорта отдельно - "567890"
            purposeDetailSysName = "passportNumber";
            purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
            if (purposeDetailNum != null) {
                passportNumber = purpose[purposeDetailNum];
                if (passportNumber.isEmpty()) {
                    purposeDetailName = getPurposeTemplateDetailNameBySysName(purposeTemplateDetailsMap, purposeDetailSysName);
                    emptyDetailsNames.add(purposeDetailName);
                } else if (checkIsValueInvalidByRegExp(passportNumber, "\\d{6}")) {
                    throw new Mort900Exception(
                            "Номер паспорта плательщика из назначения платежа не корректен - требуются шесть цифр",
                            "Payer's passport number from payment purpose is invalid - required six digits"
                    );
                }
            }

        }

        // Мобильный телефон - "9263829683"
        //String MobilePhoneNumber = purpose[6];
        purposeDetailSysName = "mobilePhone";
        purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
        String mobilePhone = "";
        if (purposeDetailNum != null) {
            mobilePhone = purpose[purposeDetailNum];
            if (mobilePhone.isEmpty()) {
                purposeDetailName = getPurposeTemplateDetailNameBySysName(purposeTemplateDetailsMap, purposeDetailSysName);
                emptyDetailsNames.add(purposeDetailName);
            } else if (checkIsValueInvalidByRegExp(mobilePhone, "\\d{10}")) {
                throw new Mort900Exception(
                        "Номер мобильного телефона плательщика из назначения платежа не корректен - требуются десять цифр без разделителей",
                        "Payer's mobile phone number from payment purpose is invalid - required ten digits without dividers"
                );
            }
        }
        String insurerAddressText = null;
        purposeDetailSysName = "insurerAddressText";
        purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
        if (purposeDetailNum != null) {
            insurerAddressText = purpose[purposeDetailNum];
            if (insurerAddressText.isEmpty()) {
                purposeDetailName = getPurposeTemplateDetailNameBySysName(purposeTemplateDetailsMap, purposeDetailSysName);
                emptyDetailsNames.add(purposeDetailName);
            }
        }

        // Адрес имущества - "г. Москва, ул. Победы, д. 6"
        //String propertyAddressText = purpose[7];
        String propertyAddressText = null;
        purposeDetailSysName = "propertyAddressText";
        purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
        if (purposeDetailNum != null) {
            propertyAddressText = purpose[purposeDetailNum];
            if (propertyAddressText.isEmpty()) {
                purposeDetailName = getPurposeTemplateDetailNameBySysName(purposeTemplateDetailsMap, purposeDetailSysName);
                emptyDetailsNames.add(purposeDetailName);
            }
        }

        // Тип имущества ("к" – квартира, "д" – дом)
        //String propertyType = purpose[8];
        String propertyType = "";
        purposeDetailSysName = "propertyType";
        purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
        if (purposeDetailNum != null) {
            propertyType = purpose[purposeDetailNum];
            if (propertyType.isEmpty()) {
                purposeDetailName = getPurposeTemplateDetailNameBySysName(purposeTemplateDetailsMap, purposeDetailSysName);
                emptyDetailsNames.add(purposeDetailName);
                //} else if (!(("к".equalsIgnoreCase(propertyType)) || ("д".equalsIgnoreCase(propertyType)))) {
                // "Должны работать большие и малые буквы." (05.04.2016)
            } else if (checkIsValueInvalidByRegExp(propertyType, "к|д|К|Д")) {
                throw new Mort900Exception(
                        "Тип имущества из назначения платежа не корректен - требуется одна буква ('к' или 'К' для квартиры, 'д' или 'Д' для дома)",
                        "Property type from payment purpose is invalid - required one letter ('к' or 'К' for flat, 'д' or 'Д' for house)"
                );
            }
        }

        // Номер и дата кредитного договора - "1234567890 01.01.2014"
        //String[] creditContractInfo = purpose[9].split(" ");
        String creditContractNumber = null;
        Date creditContractDate = null;
        purposeDetailSysName = "creditContractInfo";
        purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
        if (purposeDetailNum != null) {
            String[] creditContractInfo = purpose[purposeDetailNum].split(" ");
            int creditContractInfoMinLength = 2;
            if (creditContractInfo.length < creditContractInfoMinLength) {
                throw new Mort900Exception(
                        String.format("Сведения о кредитном договоре из назначения платежа не содержит достаточно данных для создания договора - требуется как минимум %d значения разделенных пробелом, обнаружено только %d", creditContractInfoMinLength, creditContractInfo.length),
                        String.format("Credit contract info from payment purpose does not contain enough data - required minimum [%d] values divided by spaces, but found [%d]", creditContractInfoMinLength, creditContractInfo.length)
                );
            }
            creditContractNumber = creditContractInfo[0];
            if (creditContractNumber.isEmpty()) {
                emptyDetailsNames.add("Номер кредитного договора");
            }
            String creditContractDateStr = creditContractInfo[1];
            if (creditContractDateStr.isEmpty()) {
                emptyDetailsNames.add("Дата кредитного договора");
            } else {
                creditContractDate = (Date) datesParser.parseAnyDate(creditContractDateStr, Date.class, "Дата кредитного договора", true);
                if (creditContractDate == null) {
                    throw new Mort900Exception(
                            "Дата кредитного договора из назначения платежа указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)",
                            "Credit contract date from payment purpose is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)"
                    );
                }

            }
        }

        // Дата окончания предыдущего договора страхования - "01.10.2015"
        //String previousInsContractFinishDateStr = purpose[10];
        purposeDetailSysName = "previousInsContractFinishDate";
        purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
        Date previousInsContractFinishDate = null;
        if (purposeDetailNum != null) {
            String previousInsContractFinishDateStr = purpose[purposeDetailNum];
            if (previousInsContractFinishDateStr.isEmpty() || previousInsContractFinishDateStr.equalsIgnoreCase("N")) {
                purposeDetailName = getPurposeTemplateDetailNameBySysName(purposeTemplateDetailsMap, purposeDetailSysName);
                emptyDetailsNames.add(purposeDetailName);
            } else {
                previousInsContractFinishDate = (Date) datesParser.parseAnyDate(previousInsContractFinishDateStr, Date.class, "Дата окончания предыдущего договора страхования", true);
                if (previousInsContractFinishDate == null) {
                    throw new Mort900Exception(
                            "Дата окончания предыдущего договора страхования из назначения платежа указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)",
                            "Previous insurance contract finish date from payment purpose is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)"
                    );
                }
            }
        } else {

            // Дата окончания предыдущего договора страхования - "25.07.2016" или "N" (если у клиента нет предыдущего договора) 
            purposeDetailSysName = "previousInsContractFinishDateOrN";
            purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
            if (purposeDetailNum != null) {
                String previousInsContractFinishDateStr = purpose[purposeDetailNum];
                if (previousInsContractFinishDateStr.isEmpty()) {
                    // не указана дата - требуется включить её в перечнь недостающих сведений, выписка будет считаться не корректной
                    purposeDetailName = getPurposeTemplateDetailNameBySysName(purposeTemplateDetailsMap, purposeDetailSysName);
                    emptyDetailsNames.add(purposeDetailName);
                } else if (previousInsContractFinishDateStr.equalsIgnoreCase("N")) {
                    // в явном виде указано, что у клиента нет предыдущего договора - вся работа с датами должна быть выполнена с учетом этого
                    previousInsContractFinishDate = null;
                } else {
                    previousInsContractFinishDate = (Date) datesParser.parseAnyDate(previousInsContractFinishDateStr, Date.class, "Дата окончания предыдущего договора страхования", true);
                    if (previousInsContractFinishDate == null) {
                        throw new Mort900Exception(
                                "Дата окончания предыдущего договора страхования из назначения платежа указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)",
                                "Previous insurance contract finish date from payment purpose is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)"
                        );
                    }
                }
            }

        }

        // Площадь имущества - "100"
        //String propertyAreaStr = purpose[11];
        purposeDetailSysName = "propertyArea";
        purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
        Double propertyArea = null;
        if (purposeDetailNum != null) {
            String propertyAreaStr = purpose[purposeDetailNum].replaceAll(" ", "");
            if (propertyAreaStr.isEmpty()) {
                purposeDetailName = getPurposeTemplateDetailNameBySysName(purposeTemplateDetailsMap, purposeDetailSysName);
                emptyDetailsNames.add(purposeDetailName);
            } else {
                propertyArea = getDoubleParam(propertyAreaStr.replaceAll(",", "."));
                if (propertyArea == 0.0) {
                    throw new Mort900Exception(
                            "Площадь имущества из назначения платежа некорректа или указана в неподдерживаемом формате",
                            "Property area from payment purpose is invalid or in unsupported format"
                    );
                }
            }
        }

        // E-mail (вероятно, страхователя) - "mail@mail.com"
        // (прим. от 07.04.2016: e-mail - необязательно)
        purposeDetailSysName = "eMail";
        purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
        String eMail = "";
        if ((purposeDetailNum != null) && (purposeDetailNum < purpose.length)) {
            eMail = purpose[purposeDetailNum];
            //if (eMail.isEmpty()) {
            //    purposeDetailName = getPurposeTemplateDetailNameBySysName(purposeTemplateDetailsMap, purposeDetailSysName);
            //    emptyDetailsNames.add(purposeDetailName);
            //}
        }

        // E-mail (вероятно, страхователя) - "mail@mail.com" или "N" (если у клиента нет электронного адреса)
        // (прим. от 07.04.2016: e-mail - необязательно)
        purposeDetailSysName = "eMailOrN";
        purposeDetailNum = purposeTemplateDetailsNumsMap.get(purposeDetailSysName);
        //String eMail = "";
        if ((purposeDetailNum != null) && (purposeDetailNum < purpose.length)) {
            eMail = purpose[purposeDetailNum];
            if (eMail.equalsIgnoreCase("N")) {
                // "Если у клиента нет предыдущего договора, то будет проставлено значение N" - такое значение не нужно сохранять в контактные данные страхователя
                eMail = "";
            }
        }

        if (!emptyDetailsNames.isEmpty()) {
            StringBuilder emptyDetailsNamesSB = new StringBuilder();
            emptyDetailsNamesSB.append("В назначении платежа отсутствует ряд сведений, требующихся для создания договора (");
            for (String emptyDetailName : emptyDetailsNames) {
                emptyDetailsNamesSB.append(emptyDetailName).append(", ");
            }
            emptyDetailsNamesSB.setLength(emptyDetailsNamesSB.length() - 2);
            emptyDetailsNamesSB.append(")");

            throw new Mort900Exception(
                    emptyDetailsNamesSB.toString(),
                    "Some of values in payment purpose details, requiried for contract creation, is empty"
            );

        }

        // подготовка стркуктуры договора
        Map<String, Object> contract = new HashMap<String, Object>();
        Map<String, Object> insurer = new HashMap<String, Object>();
        contract.put("INSURERMAP", insurer);
        Map<String, Object> document = new HashMap<String, Object>();
        ArrayList<Map<String, Object>> documentList = new ArrayList<Map<String, Object>>();
        documentList.add(document);
        insurer.put("documentList", documentList);
        Map<String, Object> insurerContactEMail = new HashMap<String, Object>();
        ArrayList<Map<String, Object>> contactList = new ArrayList<Map<String, Object>>();
        Map<String, Object> insurerContactMobilePhone = new HashMap<String, Object>();
        contactList.add(insurerContactMobilePhone);
        contactList.add(insurerContactEMail);
        insurer.put("contactList", contactList);
        Map<String, Object> contrExtMap = new HashMap<String, Object>();
        contract.put("CONTREXTMAP", contrExtMap);

        // заполнение договора
        insurer.put("FIRSTNAME", insurerFirstName);
        insurer.put("MIDDLENAME", insurerMiddleName);
        insurer.put("LASTNAME", insurerLastName);
        insurer.put("BIRTHDATE", insurerBirthDate);
        document.put("DOCTYPESYSNAME", "PassportRF");
        document.put("DOCSERIES", passportSeries);
        document.put("DOCNUMBER", passportNumber);
        insurerContactMobilePhone.put("CONTACTTYPESYSNAME", "MobilePhone");
        insurerContactMobilePhone.put("VALUE", mobilePhone);
        insurerContactEMail.put("CONTACTTYPESYSNAME", "PersonalEmail");
        insurerContactEMail.put("VALUE", eMail);

        // Тип имущества ("к" – квартира, "д" – дом)
        contrExtMap.put("INSOBJTYPE", "Д".equalsIgnoreCase(propertyType.toUpperCase()) ? 0L : 1L);

        contrExtMap.put("CREDITCONTRNUM", creditContractNumber);
        contrExtMap.put("CREDITCONTRDATE", creditContractDate);
        contrExtMap.put("AREA", propertyArea);
        contrExtMap.put("ADDRESSTEXT", propertyAddressText);
        contrExtMap.put("propertyAddress", propertyAddressText);
        contrExtMap.put("insurerAddress", insurerAddressText);

        // Входящая дата
        Date bankCashFlowInputDate = (Date) datesParser.parseAnyDate(bankCashFlow.get("INPUTDATE"), Date.class, "INPUTDATE", true);

        // Дата оформления договора соответствует «Дата окончания предыдущего договора страхования» (Назначение платежа 11) 
        // или «Входящая дата» из движения средств если «Дата окончания предыдущего договора страхования» (Назначение платежа 11) ранее «Входящая дата» из движения средств.
        // доработка по письму от 12 мая 2016. дата оформления - всегда входящая дата, а дата начала действия - уже по условию.
        Date documentDate;
        if ((previousInsContractFinishDate == null) || (bankCashFlowInputDate.after(previousInsContractFinishDate))) {
            documentDate = bankCashFlowInputDate;
        } else {
            documentDate = previousInsContractFinishDate;
        }
        contract.put("DOCUMENTDATE", bankCashFlowInputDate);
        contract.put("SIGNDATE", bankCashFlowInputDate);

        // старт дата установлена по правилу и сдвинута на 1 день.
        GregorianCalendar startDateGC = new GregorianCalendar();
        startDateGC.setTime(documentDate);
        startDateGC.add(Calendar.DATE, 1);
        Date startDate = startDateGC.getTime();
        contract.put("STARTDATE", startDate);

        // «Сумма» из движения средств
        Double bankCashFlowAmValue = getDoubleParam(bankCashFlow.get("AMVALUE"));

        // Страховая премия соответствует «Сумма» из движения средств.
        contract.put("PREMVALUE", bankCashFlowAmValue);

        // «Сумма» из движения средств (она же сумма платежа, она жа сумма взноса при поэтапной оплате договора)
        // (потребуется позднее, если необходимо будет создавать план платежей из нескольких записей)
        contract.put("BANKCASHFLOWAMVALUE", bankCashFlowAmValue);
        // Входящая дата движения средств
        // (потребуется позднее, если необходимо будет выполнять поиск существующего договора с учетом даты оплаты из текущей записи о движении средств)
        contract.put("BANKCASHFLOWINPUTDATE", bankCashFlow.get("INPUTDATE"));

        // Страховая сумма определяется как «Страховая премия» (она же «Сумма» из движения средств) разделить на 0.25%
        contract.put("INSAMVALUE", bankCashFlowAmValue / 0.0025);

        return contract;
    }

    public Map<String, Object> processRow(List<Map<String, Object>> purposeDetails, List<Map<String, Object>> purposeTemplateDetails, Map<String, Object> bankCashFlow) throws Exception, Mort900Exception {

        String[] purpose = getPurposeArrayFromPurposeDetails(purposeDetails);

        Map<String, Object> purposeTemplateDetailsMap = getPurposeTemplateDetailsMap(purposeTemplateDetails);

        // проверка количества элементов
        //int purposeMinLength = 13;
        int purposeMinLength = getIntegerParam(purposeTemplateDetailsMap.remove(TOTALCOUNT)); // количество блоков (деталей) в назначении платежа согласно шаблону
        if (purpose.length < purposeMinLength) {
            throw new Mort900Exception(
                    String.format("Не достаточно данных для создания объекта - требуется как минимум %d значений разделенных точкой с запятой, обнаружено только %d", purposeMinLength, purpose.length),
                    String.format("Does not contain enough records - required minimum [%d] values divided by semicolons, but found [%d]", purposeMinLength, purpose.length)
            );
        }

        // проверка констант
        for (Map<String, Object> purposeTemplateDetail : purposeTemplateDetails) {
            String constValue = getStringParam(purposeTemplateDetail, "CONSTANTVALUESTR");
            int constNum = getIntegerParam(purposeTemplateDetail, "NUM");
            if (!constValue.isEmpty()) {
                String value = purpose[constNum - 1];
                if (!constValue.equals(value)) {
                    throw new Mort900Exception(
                            String.format("Значение '%s' из блока номер %d назначения платежа не соответствует значению константы, установленной для данного блока описанием шаблона обработки", value, constNum),
                            String.format("Value [%s] from block number [%d] is not equal to constant value, provided to this block by template details", value, constNum)
                    );
                }
            }
        }

        Map<String, Object> rowData = new HashMap<String, Object>();
        for (Map<String, Object> purposeTemplateDetail : purposeTemplateDetails) {
            String dataSysName = getStringParam(purposeTemplateDetail, "SYSNAME");
            int dataNum = getIntegerParam(purposeTemplateDetail, "NUM");
            if (!dataSysName.isEmpty()) {
                String value = purpose[dataNum - 1];
                rowData.put(dataSysName, value);
            }
        }
        return rowData;
    }
}
