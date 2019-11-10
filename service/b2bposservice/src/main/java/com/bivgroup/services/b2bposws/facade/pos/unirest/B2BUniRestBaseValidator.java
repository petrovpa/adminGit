package com.bivgroup.services.b2bposws.facade.pos.unirest;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ilich on 09.08.17.
 */
public class B2BUniRestBaseValidator {

    public static final String REGEX_FIO_RU = "^[А-ЯЁ]+[а-яёА-ЯЁ\\ \\-\\`]*$";
    public static final String REGEX_DOC_SERIES = "\\d{4}";
    public static final String REGEX_DOC_NUMBER = "\\d{6}";
    public static final String REGEX_MOBILEPHONE = "\\d{10}";
    public static final String REGEX_MOBILEPHONEPLUS7 = "\\+7\\d{10}";
    public static final String REGEX_MOBILEPHONEPLUSNOT7 = "\\+[12345689]\\d{5,25}";
    public static final String REGEX_EMAIL = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

    public static final String REGEX_KLADR = "\\d{13}";
    public static final String REGEX_FIO_EN = "^[A-Z]+[a-zA-Z\\ \\-\\`]*$";
    public static final String REGEX_FIO_RU_EN = "^[A-ZА-ЯЁ]+[a-zA-Zа-яёА-ЯЁ\\ \\-\\`]*$";

    protected String validationError = "";

    public static boolean isNullOrEmpty(Object value) {
        return (value == null) || (value.toString().isEmpty());
    }

    public static Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    public static GregorianCalendar getGCParam(Object value) {
        if (value != null) {
            GregorianCalendar gcResult = new GregorianCalendar();
            gcResult.setTime((Date) value);
            return gcResult;
        }
        return null;
    }

    public static GregorianCalendar getGCParam(Map<String, Object> params, String paramName) {
        return getGCParam(params.get(paramName));
    }

    public static void setGCZeroTime(GregorianCalendar gcDate) {
        gcDate.set(Calendar.HOUR_OF_DAY, 0);
        gcDate.set(Calendar.MINUTE, 0);
        gcDate.set(Calendar.SECOND, 0);
        gcDate.set(Calendar.MILLISECOND, 0);
    }

    public static boolean checkValueByRegExp(Object value, String regExp) {
        if (value != null) {
            Pattern pattern = Pattern.compile(regExp);
            Matcher matcher = pattern.matcher(value.toString());
            return matcher.matches();
        } else {
            return false;
        }
    }

    protected boolean validateInsurer(Map<String, Object> dataMap) {
        if (dataMap.get("insurer") == null) {
            validationError = "Не заполнен страхователь";
            return false;
        }
        Map<String, Object> insurerMap = (Map<String, Object>) dataMap.get("insurer");
        if (isNullOrEmpty(insurerMap.get("country"))) {
            validationError = "Не заполнено гражданство страхователя";
            return false;
        }
        if (!(insurerMap.get("country").toString().equalsIgnoreCase("643")) &&
                !(insurerMap.get("country").toString().equalsIgnoreCase("000"))) {
            validationError = "Указано недопустимое значение гражданства страхователя";
            return false;
        }
        if (isNullOrEmpty(insurerMap.get("surname"))) {
            validationError = "Не заполнена фамилия страхователя";
            return false;
        }
        if (!checkValueByRegExp(insurerMap.get("surname"), REGEX_FIO_RU)) {
            validationError = "Указано недопустимое значение фамилии страхователя";
            return false;
        }
        if (isNullOrEmpty(insurerMap.get("name"))) {
            validationError = "Не заполнено имя страхователя";
            return false;
        }
        if (!checkValueByRegExp(insurerMap.get("name"), REGEX_FIO_RU)) {
            validationError = "Указано недопустимое значение имени страхователя";
            return false;
        }
        if (insurerMap.get("patronymic") != null) {
            if (!checkValueByRegExp(insurerMap.get("patronymic"), REGEX_FIO_RU)) {
                validationError = "Указано недопустимое значение отчества страхователя";
                return false;
            }
        }
        if (isNullOrEmpty(insurerMap.get("dateOfBirth"))) {
            validationError = "Не заполнена дата рождения страхователя";
            return false;
        }
        GregorianCalendar gcTodayDate = getGCParam(new Date());
        GregorianCalendar gcDateOfBirth = getGCParam(insurerMap, "dateOfBirth");
        setGCZeroTime(gcDateOfBirth);
        if (gcDateOfBirth.get(Calendar.YEAR) < 1900) {
            validationError = "Указано недопустимое значение даты рождения страхователя";
            return false;
        }
        if (gcDateOfBirth.getTimeInMillis() > gcTodayDate.getTimeInMillis()) {
            validationError = "Указано недопустимое значение даты рождения страхователя, позднее даты оформления";
            return false;
        }
        if (isNullOrEmpty(insurerMap.get("sex"))) {
            validationError = "Не заполнен пол страхователя";
            return false;
        }
        if ((!insurerMap.get("sex").toString().equals("male")) &&
                (!insurerMap.get("sex").toString().equals("female"))) {
            validationError = "Указано недопустимое значение пола страхователя";
            return false;
        }
        if (insurerMap.get("document") != null) {
            Map<String, Object> documentMap = (Map<String, Object>) insurerMap.get("document");
            if (isNullOrEmpty(documentMap.get("series")) && insurerMap.get("country").toString().equalsIgnoreCase("643")) {
                validationError = "Не заполнена серия документа страхователя (паспорт РФ)";
                return false;
            }
            if (!checkValueByRegExp(documentMap.get("series"), REGEX_DOC_SERIES) && insurerMap.get("country").toString().equalsIgnoreCase("643")) {
                validationError = "Указано недопустимое значение серии документа страхователя (паспорт РФ)";
                return false;
            }
            if (isNullOrEmpty(documentMap.get("no"))) {
                validationError = "Не заполнен номер документа страхователя";
                return false;
            }
            if (!checkValueByRegExp(documentMap.get("no"), REGEX_DOC_NUMBER) && insurerMap.get("country").toString().equalsIgnoreCase("643")) {
                validationError = "Указано недопустимое значение номера документа страхователя (паспорт РФ)";
                return false;
            }
            if (isNullOrEmpty(documentMap.get("dateOfIssue"))) {
                validationError = "Не заполнена дата выдачи документа страхователя";
                return false;
            }
            GregorianCalendar gcDateOfIssue = getGCParam(documentMap, "dateOfIssue");
            setGCZeroTime(gcDateOfIssue);
            if (gcDateOfIssue.getTimeInMillis() > gcTodayDate.getTimeInMillis()) {
                validationError = "Указано недопустимое значение даты выдачи документа страхователя, позднее даты оформления";
                return false;
            }
            GregorianCalendar gcBirthDatePlus14 = new GregorianCalendar();
            gcBirthDatePlus14.setTime(gcDateOfBirth.getTime());
            gcBirthDatePlus14.add(Calendar.YEAR, 14);
            setGCZeroTime(gcBirthDatePlus14);
            if ((gcDateOfIssue.getTimeInMillis() < gcBirthDatePlus14.getTimeInMillis()) && insurerMap.get("country").toString().equalsIgnoreCase("643")) {
                validationError = "Указано недопустимое значение даты выдачи документа страхователя (паспорт РФ), ранее 14 лет от даты рождения";
                return false;
            }
        }
        if (isNullOrEmpty(insurerMap.get("mobileTel"))) {
            validationError = "Не заполнен мобильный телефон страхователя";
            return false;
        }
        if (!checkValueByRegExp(insurerMap.get("mobileTel"), REGEX_MOBILEPHONE) &&
                !checkValueByRegExp(insurerMap.get("mobileTel"), REGEX_MOBILEPHONEPLUS7) &&
                !checkValueByRegExp(insurerMap.get("mobileTel"), REGEX_MOBILEPHONEPLUSNOT7)) {
            validationError = "Указано недопустимое значение номера телефона страхователя";
            return false;
        }
        if (isNullOrEmpty(insurerMap.get("email"))) {
            validationError = "Не заполнена электронная почта страхователя";
            return false;
        }
        if (!checkValueByRegExp(insurerMap.get("email"), REGEX_EMAIL)) {
            validationError = "Указано недопустимое значение электронной почты страхователя";
            return false;
        }
        return true;
    }

    public boolean validateLoad(Map<String, Object> dataMap) {
        validationError = "";
        return validateInsurer(dataMap);
    }

    public boolean validateCalc(Map<String, Object> dataMap) {
        validationError = "";
        return true;
    }

    public String getValidationError() {
        return this.validationError;
    }
}
