package com.bivgroup.services.b2bposws.facade.pos.custom.sberlife;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class CaringParentsCustomFilters extends B2BLifeBaseFacade {

    public static class reportsFilter {

        private Map<String, Object> contract;
        private Map<String, Object> report;
        Long payVarStr;
        Boolean oneTimePay;
        Boolean annualPay;
        Map<String, Object> insuredMap;

        /**
         * Данные
         *
         * @param finalContract
         */
        public reportsFilter(Map<String, Object> finalContract) {
            this.contract = finalContract;
            payVarStr = (Long) contract.get("PAYVARID");
            oneTimePay = (payVarStr == 103L); // ONETIME PAYMENT = 103
            annualPay = !oneTimePay;
            insuredMap = (Map<String, Object>) contract.get("INSUREDMAP");
        }

        public Boolean test(Map<String, Object> report) {
            this.report = report;
            return this.filtering();
        }

        /**
         * Возраст с учетом високосного года
         *
         * @param birthDate
         * @return Long
         */
        private Long getAge(Date birthDate) {
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTime(birthDate);

            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(new Date());

            LocalDate start = LocalDate.of(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH) + 1, calendarStart.get(Calendar.DAY_OF_MONTH));
            LocalDate end = LocalDate.of(calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH) + 1, calendarEnd.get(Calendar.DAY_OF_MONTH));

            return ChronoUnit.YEARS.between(start, end);
        }

        /**
         * Фильтр
         *
         * @return
         */
        public Boolean filtering() {

            Date birthDate = getDateParam(insuredMap.get("BIRTHDATE"));
            //Long fullAge = (new Date().getTime() - birthDate.getTime());
            //fullAge = TimeUnit.MILLISECONDS.toDays(fullAge) % 365;
            Long fullAge = getAge(birthDate);

            Boolean result = true;
            Long reportId = (Long) report.get("REPID");


            if (reportId == 500004L) { //Декларация ЗВ_НС
                if (oneTimePay) {
                    result = true;
                } else {
                    result = false;
                }
            }

            if (reportId == 500007L) { //Декларация ЗР
                if (fullAge < 18) {
                    result = true;
                } else {
                    result = false;
                }
            }

            if (reportId == 500005L) { //Декларация ЗВ полная
                if (((oneTimePay && (fullAge >= 18))) || ((annualPay && (fullAge < 18)))) {
                    return true;
                } else {
                    return false;
                }
            }

            if (reportId == 500006L) { //Декларация ЗВ_ЗР полная
                if (annualPay && (fullAge >= 18)) {
                    return true;
                } else {
                    return false;
                }
            }

            return result;
        }
    }
}
