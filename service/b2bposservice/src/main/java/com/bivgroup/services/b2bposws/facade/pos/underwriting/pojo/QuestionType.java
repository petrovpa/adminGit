package com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo;

public enum QuestionType {
    FIO(null),
    BLOOD_PRESSURE(1L),
    DATE_OF_BIRTH(2L),
    SEX(3L),
    HEIGHT(4L),
    WEIGHT(5L),
    REGULAR(null);

    Long databaseId;

    QuestionType(Long databaseId) {
        this.databaseId = databaseId;
    }

    public Long getDatabaseId() {
        return databaseId;
    }
}
