package com.bivgroup.core.audit;

/**
 * Тип результата операции аудита
 */
public enum ResultOperation {
    /**
     * Операция прошла успешно
     */
    SUCCESE("Успешно"),
    /**
     * Операция прошла нуспешно
     */
    FALTURE("Неуспешно");

    private final String resultName;

    ResultOperation(String resultName) {
        this.resultName = resultName;
    }

    public String getResultName() {
        return resultName;
    }
}
