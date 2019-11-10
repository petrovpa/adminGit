package com.bivgroup.services.b2bposws.facade.pos.declaration.utils;

import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingHelper {

    private static String FALSE_STR_VALUE = "FALSE";
    private static String TRUE_STR_VALUE = "TRUE";
    private static String EMPTY_STRING = "";
    protected static final Long BOOLEAN_FLAG_LONG_VALUE_TRUE = 1L;

    public static Map<String, Object> bankDetailsMapping(Map<String, Object> bankDetails, Boolean isNotExistContract) {
        HashMap<String, Object> bankMap = new HashMap<>();
        if (bankDetails != null && !bankDetails.isEmpty()) {
            // "REPORTDATA.DIDBANKMAP.bankName - наименование банка (и его отделения)
            bankMap.put("bankName", bankDetails.get("bankName"));
            // REPORTDATA.DIDBANKMAP.bankAddr - адрес банка
            bankMap.put("bankAddr", bankDetails.get("bankAddress"));
            // REPORTDATA.DIDBANKMAP.bankPhone - телефон банка
            bankMap.put("bankPhone", bankDetails.get("bankPhone"));
            // REPORTDATA.DIDBANKMAP.bankAddrPhone - адрес, телефон банка
            bankMap.put("bankAddrPhone", bankDetails.get("bankAddressPhone"));
            // REPORTDATA.DIDBANKMAP.bankBIK - бик банка
            bankMap.put("bankBIK", bankDetails.get("bankBIK"));
            // REPORTDATA.DIDBANKMAP.bankINN - инн банка
            bankMap.put("bankINN", bankDetails.get("bankINN"));
            // REPORTDATA.DIDBANKMAP.checkAcc - расчетный счет банка
            bankMap.put("checkAcc", bankDetails.get("bankSettlementAccount"));
            // REPORTDATA.DIDBANKMAP.corrAcc - корр. счет банка
            bankMap.put("corrAcc", bankDetails.get("bankAccount"));
            // REPORTDATA.DIDBANKMAP.facAcc - ЛС получателя
            bankMap.put("facAcc", bankDetails.get("account"));
            // REPORTDATA.DIDBANKMAP.cardNum - № карты получателя"
            bankMap.put("cardNum", bankDetails.get("cardNumber"));
        } else {
            if (isNotExistContract) {
                // "REPORTDATA.DIDBANKMAP.bankName - наименование банка (и его отделения)
                bankMap.put("bankName", EMPTY_STRING);
                // REPORTDATA.DIDBANKMAP.bankAddr - адрес банка
                bankMap.put("bankAddr", EMPTY_STRING);
                // REPORTDATA.DIDBANKMAP.bankPhone - телефон банка
                bankMap.put("bankPhone", EMPTY_STRING);
                // REPORTDATA.DIDBANKMAP.bankAddrPhone - адрес, телефон банка
                bankMap.put("bankAddrPhone", EMPTY_STRING);
                // REPORTDATA.DIDBANKMAP.bankBIK - бик банка
                bankMap.put("bankBIK", EMPTY_STRING);
                // REPORTDATA.DIDBANKMAP.bankINN - инн банка
                bankMap.put("bankINN", EMPTY_STRING);
                // REPORTDATA.DIDBANKMAP.checkAcc - расчетный счет банка
                bankMap.put("checkAcc", EMPTY_STRING);
                // REPORTDATA.DIDBANKMAP.corrAcc - корр. счет банка
                bankMap.put("corrAcc", EMPTY_STRING);
                // REPORTDATA.DIDBANKMAP.facAcc - ЛС получателя
                bankMap.put("facAcc", EMPTY_STRING);
                // REPORTDATA.DIDBANKMAP.cardNum - № карты получателя"
                bankMap.put("cardNum", EMPTY_STRING);
                // Банковские реквизиты для выплаты ДИД ("REPORTDATA.DIDBANKMAP)
            }
        }
        return bankMap;
    }

    /**
     * ключи для ПФ по расторжению/annulment
     *
     * @param bankDetails
     * @return
     */
    public static Map<String, Object> bankDetailsMapping2(Map<String, Object> bankDetails, Boolean isNotExistContract) {
        HashMap<String, Object> bankMap = new HashMap<>();
        if (bankDetails != null && !bankDetails.isEmpty()) {
            // "REPORTDATA.DIDBANKMAP.bankName - наименование банка (и его отделения)
            bankMap.put("NAME", bankDetails.get("bankName"));
            bankMap.put("ADDRESS", bankDetails.get("bankAddress"));
            bankMap.put("PHONE", bankDetails.get("bankPhone"));
            //bankMap.put("bankAddrPhone", bankDetails.get("bankAddressPhone"));
            bankMap.put("BIK", bankDetails.get("bankBIK"));
            bankMap.put("INN", bankDetails.get("bankINN"));
            bankMap.put("CHECKACC", bankDetails.get("bankSettlementAccount"));
            bankMap.put("KORRACC", bankDetails.get("bankAccount"));
            bankMap.put("FACACC", bankDetails.get("account"));
            bankMap.put("CARDNUMBER", bankDetails.get("cardNumber"));
        } else {
            if (isNotExistContract) {
                bankMap.put("NAME", EMPTY_STRING);
                bankMap.put("ADDRESS", EMPTY_STRING);
                bankMap.put("PHONE", EMPTY_STRING);
                bankMap.put("BIK", EMPTY_STRING);
                bankMap.put("INN", EMPTY_STRING);
                bankMap.put("CHECKACC", EMPTY_STRING);
                bankMap.put("KORRACC", EMPTY_STRING);
                bankMap.put("FACACC", EMPTY_STRING);
                bankMap.put("CARDNUMBER", EMPTY_STRING);
            }
        }
        return bankMap;
    }

    public static void requisitesMapping(Map<String, Object> reportPerson, Map<String, Object> client,
                                         boolean isNotExistContract) {
        // Место рождения лица
        reportPerson.put("BIRTHPLACE", getStringParam(client, "placeOfBirth"));
        // Страна рождения лица
        reportPerson.put("BIRTHCOUNTRY", getStringParam(client, "countryOfBirth"));
        // ИНН персоны
        reportPerson.put("INN", getStringParam(client, "inn"));
        // Вид на жительство заявителя Флаг
        if ("1".equalsIgnoreCase(getStringParam(client, "isResidencePermit"))) {
            reportPerson.put("RESIDENCESTR", getStringParam(client, "residencePermit"));
        } else {
            if ("0".equalsIgnoreCase(getStringParam(client, "isResidencePermit")) || !isNotExistContract) {
                reportPerson.put("RESIDENCESTR", FALSE_STR_VALUE);
            } else {
                reportPerson.put("RESIDENCESTR", EMPTY_STRING);
            }
        }

        // Статус налогового резидента другой страны
        if ("1".equalsIgnoreCase(getStringParam(client, "isTaxResidentOther"))) {
            reportPerson.put("RESIDENTOTHER", getStringParam(client, "taxResidentCountry"));
            // ИНН Другая страна персоны
            reportPerson.put("INNOTHER", getStringParam(client, "innOther"));
        } else {
            if ("0".equalsIgnoreCase(getStringParam(client, "isTaxResidentOther")) || !isNotExistContract) {
                reportPerson.put("RESIDENTOTHER", FALSE_STR_VALUE);
            } else {
                reportPerson.put("RESIDENTOTHER", EMPTY_STRING);
            }
        }

        // Статус налогового резидента США персоны
        if ("1".equalsIgnoreCase(getStringParam(client, "isTaxResidentUsa"))) {
            reportPerson.put("RESIDENTUSA", TRUE_STR_VALUE);
            // ИНН США персоны
            reportPerson.put("INNUSA", getStringParam(client, "innUsa"));
        } else {
            if ("0".equalsIgnoreCase(getStringParam(client, "isTaxResidentUsa")) || !isNotExistContract) {
                reportPerson.put("RESIDENTUSA", FALSE_STR_VALUE);
            } else {
                reportPerson.put("RESIDENTUSA", EMPTY_STRING);
            }
        }
    }

    private static String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }

    private static String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else if (bean instanceof Double) {
            DecimalFormat df = new DecimalFormat("0");
            df.setMaximumFractionDigits(2);
            return df.format(Double.valueOf(bean.toString()).doubleValue());
        } else {
            return bean.toString();
        }
    }
}
