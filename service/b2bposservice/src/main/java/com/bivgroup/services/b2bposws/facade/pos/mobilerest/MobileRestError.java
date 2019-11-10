/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.mobilerest;

/**
 *
 * @author averichevsm
 */
public enum MobileRestError {

    // вести синхронно и в b2bposservice и в mobilerest (если потребуется)
    //
    // Системные ошибки (0-9):
    /** 0 - Обработан успешно */
    SYSTEM_SUCCESS(0L, "Обработан успешно"),
    /** 1 - Другая ошибка */
    SYSTEM_OTHER(1L, "Другая ошибка"),
    //
    // Ошибки обращения (10-99):
    /** 10 - Отказано в доступе */
    //ACCESS_DENIED(10L, "Отказано в доступе"),
    /** 10 - По указанному TOKEN отказано в доступе (v2) */
    ACCESS_DENIED(10L, "По указанному TOKEN отказано в доступе"),
    //
    // Ошибки вызова методов (100-9999):
    /** 100 - Данные по объекту устарели */
    SERVICE_DATA_OUTDATED(100L, "Данные по объекту устарели"),
    /** 101 - Ошибка сохранения объекта */
    SERVICE_SAVE_FAILED(101L, "Ошибка сохранения объекта"),
    /** 102 - Ошибка в данных объекта */
    SERVICE_INVALID_DATA(102L, "Ошибка в данных объекта"),
    /** 103 - Указан неверный SMS код подтверждения */
    //SERVICE_WRONG_CODE(103L, "Указан неверный SMS код подтверждения");
    /** 103 - Ошибка поиска аккаунта (v2) */
    SERVICE_ACCOUNT_SEARCH_FAIL(103L, "Ошибка поиска аккаунта"),
    /** 104 - Ошибка вызова последовательных сервисов (v2) */
    SERVICE_SUCCESSIVE_CALLS_FAIL(104L, "Ошибка вызова последовательных сервисов"),
    /** 105 - Превышено количество попыток ввода SMS кода подтверждения (v2) */
    SERVICE_CODE_RETRIES_EXCEEDED(105L, "Превышено количество попыток ввода SMS кода подтверждения"),
    /** 106 - Истек срок действия SMS кода подтверждения (v2) */
    SERVICE_CODE_OUTDATED(106L, "Истек срок действия SMS кода подтверждения"),
    /** 107 - Указан неверный SMS код подтверждения (v2) */
    SERVICE_CODE_WRONG(107L, "Указан неверный SMS код подтверждения"),
    /** 108 - Ошибка определения стоимости ТС (v2-b) */
    SERVICE_GET_CAR_COST_FAIL(108L, "Ошибка определения стоимости ТС"),
    /** 109 - Ошибка получения описания договора страхования (v2-b) */
    SERVICE_GET_CONTRACT_INFO_FAIL(109L, "Ошибка получения описания договора страхования");

    /** Код ошибки */
    private final Long code;
    /** Код ошибки (строковое представление) */
    private final String codeStr;
    private final String msg;
    private final String fullStr;

    // для ответа mobilerest
    /** Имя ключа для передачи через мапы параметров и результата для кода ошибки */
    public static final String ERROR_CODE_PARAMNAME = "ErrorCode";
    /** Имя ключа для передачи через мапы параметров и результата для текста ошибки */
    public static final String ERROR_MSG_PARAMNAME = "Error";
    /** Имя ключа для передачи через мапы параметров и результата для примечания/описания ошибки */
    public static final String ERROR_NOTE_PARAMNAME = "ErrorNote";
    // для записи в БД
    /** Имя поля при сохранении события в БД для кода ошибки */
    public static final String ERROR_CODE_EVENT_FIELDNAME = "errorCode";
    /** Имя поля при сохранении события в БД для текста ошибки */
    public static final String ERROR_TEXT_EVENT_FIELDNAME = "errorTest";
    /** Имя поля при сохранении события в БД для примечания/описания ошибки */
    public static final String ERROR_NOTE_EVENT_FIELDNAME = "paramStr10"; // !только для отладки! или мб нет?

    public Long getCode() {
        return code;
    }

    public String getCodeStr() {
        return codeStr;
    }

    public String getMsg() {
        return msg;
    }

    public static MobileRestError findByCode(Long code) {
        if (code != null) {
            MobileRestError[] values = MobileRestError.values();
            for (MobileRestError value : values) {
                if (code.equals(value.getCode())) {
                    return value;
                }
            }
        }
        return null;
    }

    public static MobileRestError findByCode(String code) {
        if (code != null) {
            MobileRestError[] values = MobileRestError.values();
            for (MobileRestError value : values) {
                if (code.equals(value.getCodeStr())) {
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.fullStr;
    }

    MobileRestError(Long code, String msg) {
        this.code = code;
        this.codeStr = (this.code == null) ? "" : this.code.toString();
        this.msg = msg;
        this.fullStr = String.format("[%s] '%s'", this.codeStr, this.msg);
    }

}
