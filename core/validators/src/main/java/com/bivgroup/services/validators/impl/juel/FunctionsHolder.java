/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl.juel;

import java.lang.ref.WeakReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsUtils;


/**
 * Класс содержит функции, которые могут быть вызваны в формулах
 * @author reson
 */
public class FunctionsHolder {

    public static final String MINDOUBLEVALUE_METHOD_NAME = "minDoubleValue";
    public static final String MAXDOUBLEVALUE_METHOD_NAME = "maxDoubleValue";
    public static final String SUMDOUBLEVALUE_METHOD_NAME = "sumDoubleValue";

    public static final String CALCYEARS_METHOD_NAME = "calcYears";
    public static final String CALCMONTHS_METHOD_NAME = "calcMonths";
    public static final String CALCDAYS_METHOD_NAME = "calcDays";
    public static final String ADDDAY_METHOD_NAME = "addDay";
    public static final String ROUND2_METHOD_NAME = "round2";
    public static final String ROUNDX_METHOD_NAME = "roundX";
    //private static WeakReference<CalculatorEngine> calcEngine = null;
    private static Logger logger = Logger.getLogger(FunctionsHolder.class);




    public static Double minDoubleValue(List<Map<String, Object>> mapList, String searchProperty) {
        Double result = 0.0;
        Boolean f = false;
        if ((mapList != null) && (mapList.size() > 0)) {
            for (Map<String, Object> map : mapList) {
                if (map.containsKey(searchProperty)) {
                    Object objValue = map.get(searchProperty);
                    if (objValue != null) {
                        Double value = Double.valueOf(objValue.toString());
                        if (f == false) {
                            result = value;
                            f = true;
                        } else if (result > value) {
                            result = value;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Double maxDoubleValue(List<Map<String, Object>> mapList, String searchProperty) {
        Double result = 0.0;
        if ((mapList != null) && (mapList.size() > 0)) {
            for (Map<String, Object> map : mapList) {
                if (map.containsKey(searchProperty)) {
                    Object objValue = map.get(searchProperty);
                    if (objValue != null) {
                        Double value = Double.valueOf(objValue.toString());
                        if (result < value) {
                            result = value;
                        }
                    }
                }
            }
        }
        return result;
    }



    public static Double sumDoubleValue(List<Map<String, Object>> mapList, String searchProperty) {
        Double result = 0.0;
        if ((mapList != null) && (mapList.size() > 0)) {
            for (Map<String, Object> map : mapList) {
                if (map.containsKey(searchProperty)) {
                    Object objValue = map.get(searchProperty);
                    if (objValue != null) {
                        Double value = Double.valueOf(objValue.toString());
                        result = result + value;
                    }
                }
            }
        }
        return result;
    }

    public static Integer calcYears(Date from, Date to) {
        Integer result = 0;
        if ((from != null) && (to != null)) {
            GregorianCalendar fromG = new GregorianCalendar();
            GregorianCalendar toG = new GregorianCalendar();
            fromG.setTime(from);
            toG.setTime(to);
            result = WsUtils.calcYears(fromG, toG);
        }
        return result;
    }

    public static Integer calcMonths(Date from, Date to) {
        Integer result = 0;
        if ((from != null) && (to != null)) {
            GregorianCalendar fromG = new GregorianCalendar();
            GregorianCalendar toG = new GregorianCalendar();
            fromG.setTime(from);
            toG.setTime(to);
            result = WsUtils.calcMonth(fromG, toG);
        }
        return result;
    }

    public static Integer calcDays(Date from, Date to) {
        Integer result = 0;
        if ((from != null) && (to != null)) {
            GregorianCalendar fromG = new GregorianCalendar();
            GregorianCalendar toG = new GregorianCalendar();
            fromG.setTime(from);
            toG.setTime(to);
            result = WsUtils.calcDay(fromG, toG);
        }
        return result;
    }

    public static Date addDay(Date nowDate, Integer dayCount) {
        Date result = nowDate;
        if (nowDate != null) {
            GregorianCalendar nowG = new GregorianCalendar();
            nowG.setTime(nowDate);
            nowG.add(GregorianCalendar.DAY_OF_YEAR, dayCount);
            result = nowG.getTime();
        }
        return result;
    }

    public static Double round2(Double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static Double roundX(Integer x, Double value) {
        return new BigDecimal(value).setScale(x, RoundingMode.HALF_UP).doubleValue();
    }
}
