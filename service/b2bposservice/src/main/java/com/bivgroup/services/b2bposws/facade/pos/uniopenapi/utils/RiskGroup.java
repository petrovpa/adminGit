package com.bivgroup.services.b2bposws.facade.pos.uniopenapi.utils;

import java.util.Date;
import java.util.Objects;

/**
 * Класс для группировки списка рисков договора.
 * Группировка осуществляется по дате начала и окончания действия риска
 */
public class RiskGroup {
    private Date startDate;
    private Date finishDate;

    public RiskGroup(Date startDate, Date finishDate) {
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiskGroup riskGroup = (RiskGroup) o;
        return Objects.equals(startDate, riskGroup.startDate)
                && Objects.equals(finishDate, riskGroup.finishDate)
                || (startDate.compareTo(riskGroup.startDate) >= 0 && finishDate.compareTo(riskGroup.finishDate) <= 0);
    }

    /**
     * Чтобы кореектно группировались риски нужно использовать метод equals
     * по этому всегда возвращаем одинаковое значение
     *
     * @return хэш-код объекта
     */
    @Override
    public int hashCode() {
        return 1;
    }
}
