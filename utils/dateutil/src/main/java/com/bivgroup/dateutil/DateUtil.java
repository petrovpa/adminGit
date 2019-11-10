package com.bivgroup.dateutil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static final String YEARS_SHIFT_70 = "25569.0";
    private static final BigDecimal MILLISECOND_DIV = new BigDecimal(24 * 60 * 60 * 1000);
    private static final BigDecimal YEARS_SHIFT_70_BIG_DECIMAL = new BigDecimal(YEARS_SHIFT_70);

    private DateUtil() {
    }

    /*
     * Метод возвращает текущую дату и время, учитывая часовой пояс
     */
    public static BigDecimal getCurrentDate() {
        long currentDateMs = new Date().getTime();
        long timeOffset = TimeZone.getDefault().getOffset(currentDateMs);
        return convertDate(new Date(currentDateMs + timeOffset));
    }

    /*
     * Метод возвращает дату без времени
     */
    public static Date getDateWithoutTime(Date time) {
        Calendar date;
        if (time.toString().contains("GMT")) {
            date = Calendar.getInstance(TimeZone.getTimeZone("GTM"));
        } else {
            date = Calendar.getInstance();
        }
        date.setTime(time);
        Calendar dateGMT = Calendar.getInstance(TimeZone.getTimeZone("GTM"));
        dateGMT.set(Calendar.YEAR, date.get(Calendar.YEAR));
        dateGMT.set(Calendar.MONTH, date.get(Calendar.MONTH));
        dateGMT.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
        //Обнуляем время - нас интересует только дата
        dateGMT.set(Calendar.HOUR_OF_DAY, 0);
        dateGMT.set(Calendar.MINUTE, 0);
        dateGMT.set(Calendar.SECOND, 0);
        dateGMT.set(Calendar.MILLISECOND, 0);
        return dateGMT.getTime();
    }

    public static Date convertDate(Double time) {
        BigDecimal millisecondTime = new BigDecimal(time).subtract(YEARS_SHIFT_70_BIG_DECIMAL).multiply(MILLISECOND_DIV);
        return new Date(millisecondTime.longValue());
    }

    public static Date convertDate(BigDecimal time) {
        long longTime = time.subtract(YEARS_SHIFT_70_BIG_DECIMAL)
                .multiply(MILLISECOND_DIV)
                .round(new MathContext(15))
                .longValue();
        return new Date(longTime);
    }

    @Deprecated
    public static Date convertDate(Float time) {
        BigDecimal millisecondTime = new BigDecimal(time).subtract(YEARS_SHIFT_70_BIG_DECIMAL).multiply(MILLISECOND_DIV);
        return new Date(millisecondTime.longValue());

    }

    public static BigDecimal convertDate(Date time) {
        return BigDecimal.valueOf((time).getTime() / ((double) 24 * 60 * 60 * 1000)).add(YEARS_SHIFT_70_BIG_DECIMAL);// из за погрешности

    }

    public static Date convertToGmt(Date date) {
        return convert(date, TimeZone.getDefault(), TimeZone.getTimeZone("GMT"));
    }

    /**
     * Функция преобразует дату, компенсируя механизм конвертации времени,
     * который будет задействован в приложении, получившем данную дату в другом часовом поясе.
     * <p>
     * Например, если из часового пояса GMT+8 время 01/09/2011 00:00 будет передана на сервер, работающий в
     * поясе GMT+0, то принимающая сторона получит время 31/08/2011 16:00. Данная функция добавляет к времени разницу в часовых поясах,
     * таким образом принимающая сторона получит 01/09/2011 08:00 GMT+8 и сконвертирует это в 01/09/2011 00:00 GMT+0.
     *
     * @param date
     * @param sourceTimeZone
     * @param targetTimeZone
     * @return
     */
    public static Date convert(Date date, TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int sourceOffset = sourceTimeZone.getOffset(date.getTime());
        int targetOffset = targetTimeZone.getOffset(date.getTime());
        int delta = targetOffset - sourceOffset;
        calendar.add(Calendar.MILLISECOND, -delta);
        return calendar.getTime();
    }

}
