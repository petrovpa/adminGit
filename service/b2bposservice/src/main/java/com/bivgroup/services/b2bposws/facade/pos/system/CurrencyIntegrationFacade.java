/*
 * Copyright (c) Diasoft 2004-2013
 */
package com.bivgroup.services.b2bposws.facade.pos.system;

import com.bivgroup.cbr.cbrcurrancyrategetter.CurrencyOnDateCaller;
import com.bivgroup.services.b2bposws.system.Constants;
import com.ibm.icu.util.Calendar;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import ru.cbr.web.valute.ValuteCursOnDateType;
import ru.cbr.web.valute.ValuteDataType;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @authors ilich, alexe
 */
@BOName("CurrencyIntegration")
public class CurrencyIntegrationFacade extends BaseFacade {

    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    private static final String WEBSMSWS_SERVICE_NAME = Constants.WEBSMSWS;
    private static final String ADMINWS_SERVICE_NAME = Constants.ADMINWS;
    private static final String COREWS_SERVICE_NAME = Constants.COREWS;
    private static final String RATE_CB_RF = "Курс ЦБ";
    private static final String RUR_BRIEF = "RUB";
    private static final String USD_BRIEF = "USD";
    private static final String EUR_BRIEF = "EUR";

    /*
     * Загрузка курсов долларов и евро
     * устаревший метод, оставлен для совместимости
     * @author ilich
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsIntegrationLoadCurrencyData(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Date curDate = new Date();
        // сбр выкладывает курс на следующий день с 16 часов предыдущего дня.
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(curDate);
        if (gc.get(Calendar.HOUR_OF_DAY) >= 16) {
            // если время - после 16 часов, пробуем получать курс на завтра
            gc.add(Calendar.DATE, 1);
            curDate = gc.getTime();
        }

        Map<String, Object> result = new HashMap<String, Object>();
        //1. перед запросом курса, проверим наличие курса за сегодня в БД.
        //getExchangeList
        boolean usdFlag = checkIsExchangeExist(curDate, USD_BRIEF, RATE_CB_RF, login, password);
        boolean eurFlag = checkIsExchangeExist(curDate, EUR_BRIEF, RATE_CB_RF, login, password);
        if ((!usdFlag) || (!eurFlag)) {
            CurrencyOnDateCaller currencyOnDateCaller = new CurrencyOnDateCaller();

            ValuteDataType valuteData = currencyOnDateCaller.getCurrencyOnDate(curDate);
            List<ValuteCursOnDateType> valuteCursOnDateTypes = valuteData.getValuteCursOnDate();
            Map<String, Object> qres = null;
            for (ValuteCursOnDateType valuteCursOnDateType : valuteCursOnDateTypes) {
                //    VchCode - Символьный код валюты
                //  valuteCursOnDateType.getVchCode();
                //    Vcode - Цифровой код валюты
                // valuteCursOnDateType.getVcode();
                //    Vcurs - Курс
                // valuteCursOnDateType.getVcurs();
                //    Vname - Название валюты
                //  valuteCursOnDateType.getVname();
                //    Vnom - Номинал
                // valuteCursOnDateType.getVnom();
                Map<String, Object> queryParams = new HashMap<String, Object>();
                queryParams.put("ExchangeType", RATE_CB_RF);
                queryParams.put("CourseValue", valuteCursOnDateType.getVcurs().toString());
                queryParams.put("CourseDate", curDate);
                queryParams.put("UnitNumber", valuteCursOnDateType.getVnom());
                queryParams.put("ExchangeTypeId", 1);
                if (!usdFlag) {
                    if (valuteCursOnDateType.getVchCode().equalsIgnoreCase(USD_BRIEF)) {
                        queryParams.put("CurrencyPairId", "1001");
                        qres = this.callService(ADMINWS_SERVICE_NAME, "createCurrencyExchange", queryParams, login, password);
                    }
                }
                if (!eurFlag) {
                    if (valuteCursOnDateType.getVchCode().equalsIgnoreCase(EUR_BRIEF)) {
                        queryParams.put("CurrencyPairId", "1000");
                        qres = this.callService(ADMINWS_SERVICE_NAME, "createCurrencyExchange", queryParams, login, password);
                    }
                }
            }
            usdFlag = checkIsExchangeExist(curDate, USD_BRIEF, RATE_CB_RF, login, password);
            eurFlag = checkIsExchangeExist(curDate, EUR_BRIEF, RATE_CB_RF, login, password);
            if ((!usdFlag) || (!eurFlag)) {
                //отправить сообщение админу.
                sendEmailMessage("dsa", usdFlag, eurFlag, login, password);
                //даты не равны отправляем сообщение об ошибке
                GregorianCalendar gc1 = new GregorianCalendar();
                gc1.setTime(curDate);
                if (gc1.get(Calendar.HOUR_OF_DAY) >= 20) {

                    Map<String, Object> sysParam = new HashMap<String, Object>();
                    sysParam.put("SETTINGSYSNAME", "NOTIFICATIONEMAILS");
                    sysParam.put("ReturnAsHashMap", "TRUE");
                    Map<String, Object> sysRes = this.callService(COREWS_SERVICE_NAME, "getSysSettingBySysName", sysParam, login, password);
                    if (sysRes.get("SETTINGVALUE") != null) {
                        String emails = sysRes.get("SETTINGVALUE").toString();
                        String[] emailList = emails.split(",");
                        boolean usdExist = true;
                        boolean euroExist = true;
                        for (String email : emailList) {
                            if (("sambucusfehu@gmail.com".equalsIgnoreCase(email))
                                    || ("max.l.volkov@gmail.com".equalsIgnoreCase(email))) {
                                //4. отправляем на указанные адреса уведомления о отсутсвии курса валюты на заданную дату.
                            } else {
                                if (gc1.get(Calendar.HOUR_OF_DAY) >= 22) {
                                    sendEmailCurrencyFail(email, curDate, usdFlag, eurFlag, login, password);
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /*
     * Заполнить в базе ранее не проставленные курсы валют. Запускать 1 раз. 
     * @author averichevsm
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsFillMissedCurrencyData(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
       // Date nowDate = new Date();
        
        GregorianCalendar gcnow = new GregorianCalendar();
        gcnow.add(Calendar.DATE, 1);
        Date nowDate = gcnow.getTime();
        GregorianCalendar gc = new GregorianCalendar(2016, 0, 1);
        Date curDate = gc.getTime();
        Map<String, Object> result = new HashMap<String, Object>();

        while (curDate.before(nowDate)) {
            boolean usdFlag = checkIsExchangeExist(curDate, USD_BRIEF, RATE_CB_RF, login, password);
            boolean eurFlag = checkIsExchangeExist(curDate, EUR_BRIEF, RATE_CB_RF, login, password);
            if ((!usdFlag) || (!eurFlag)) {
                CurrencyOnDateCaller currencyOnDateCaller = new CurrencyOnDateCaller();

                ValuteDataType valuteData = currencyOnDateCaller.getCurrencyOnDate(curDate);
                List<ValuteCursOnDateType> valuteCursOnDateTypes = valuteData.getValuteCursOnDate();
                Map<String, Object> qres = null;
                for (ValuteCursOnDateType valuteCursOnDateType : valuteCursOnDateTypes) {
                    //    VchCode - Символьный код валюты
                    //  valuteCursOnDateType.getVchCode();
                    //    Vcode - Цифровой код валюты
                    // valuteCursOnDateType.getVcode();
                    //    Vcurs - Курс
                    // valuteCursOnDateType.getVcurs();
                    //    Vname - Название валюты
                    //  valuteCursOnDateType.getVname();
                    //    Vnom - Номинал
                    // valuteCursOnDateType.getVnom();
                    Map<String, Object> queryParams = new HashMap<String, Object>();
                    queryParams.put("ExchangeType", RATE_CB_RF);
                    queryParams.put("CourseValue", valuteCursOnDateType.getVcurs().toString());
                    queryParams.put("CourseDate", curDate);
                    queryParams.put("UnitNumber", valuteCursOnDateType.getVnom());
                    queryParams.put("ExchangeTypeId", 1);
                    if (!usdFlag) {
                        if (valuteCursOnDateType.getVchCode().equalsIgnoreCase(USD_BRIEF)) {
                            queryParams.put("CurrencyPairId", "1001");
                            qres = this.callService(ADMINWS_SERVICE_NAME, "createCurrencyExchange", queryParams, login, password);
                        }
                    }
                    if (!eurFlag) {
                        if (valuteCursOnDateType.getVchCode().equalsIgnoreCase(EUR_BRIEF)) {
                            queryParams.put("CurrencyPairId", "1000");
                            qres = this.callService(ADMINWS_SERVICE_NAME, "createCurrencyExchange", queryParams, login, password);
                        }
                    }
                }
            }
                gc.add(Calendar.DATE, 1);
                curDate = gc.getTime();
        }
        return result;
    }

    private void sendEmailCurrencyFail(String email, Date date, boolean usdExist, boolean euroExist, String login, String password) {
        Map<String, Object> sendParams = new HashMap<String, Object>();
        sendParams.put("SMTPSubject", "Ошибка сервиса обновления курсов валют");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateStr = sdf.format(date);
        String currNames = "";
        if (!usdExist) {
            currNames = "USD ";
        }
        if (!euroExist) {
            currNames = currNames + "EUR ";
        }

        String mess = "На дату " + dateStr + " отсутствует курс валют: " + currNames + ". Проверьте работу сервиса получения курсов валют от центробанка.";
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<span style=\"color:black\">");
        sb.append(mess);
        sb.append("</span>");
        sb.append("</body></html>");
        sendParams.put("HTMLTEXT", sb.toString());

        sendParams.put("SMTPMESSAGE", "На дату " + dateStr + " отсутствует курс валют: " + currNames + ". Проверьте работу сервиса получения курсов валют от центробанка.");
        sendParams.put("SMTPReceipt", email);
        logger.debug("sendParams = " + sendParams.toString());
        Map<String, Object> sendRes = null;

        try {

            sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
            if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                    sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                    if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                        sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                        if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                            sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                        }
                    }
                }
            }
            logger.debug("mailSendSuccess");
        } catch (Exception e) {
            logger.debug("mailSendException: ", e);
        }
        //return sendRes;        

    }

    /*
     * Загрузка курсов для пар валют REF_CURRENCYPAIR, у которых STATUS=1
     * @author alexe
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsIntegrationLoadCurrencyData2(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Date curDate = new Date();
        // тип курса - ЦБРФ
        Long cbrfExchangeTypeId = getGetCurrencyExchangeTypeIdByBrief(RATE_CB_RF, login, password);
        // список валют для поиска сокращения по ид
        List<Map<String, Object>> currList = getCurrencyList(login, password);
        Long currRurId = getCurrencyIdByParam(currList, "ALPHACODE", RUR_BRIEF);
        // список актуальных валютных пар
        List<Map<String, Object>> pairList = getCurrencyPairList(currList, login, password);
        //logger.debug(String.format("dsIntegrationLoadCurrencyData2: pairList=%s", pairList.toString()));

        // загрузить курс ЦБ
        CurrencyOnDateCaller currencyOnDateCaller = new CurrencyOnDateCaller();
        ValuteDataType valuteData = currencyOnDateCaller.getCurrencyOnDate(new Date());
        List<ValuteCursOnDateType> valuteCursOnDates = valuteData.getValuteCursOnDate();
        //logger.debug(String.format("dsIntegrationLoadCurrencyData2: valuteCursOnDates=%s", valuteCursOnDates.toString()));

        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();
        result.put("EXCHANGELIST", recordList);
        for (ValuteCursOnDateType valuteCursOnDate : valuteCursOnDates) {
            //    VchCode - Символьный код валюты
            //  valuteCursOnDateType.getVchCode();
            //    Vcode - Цифровой код валюты
            // valuteCursOnDateType.getVcode();
            //    Vcurs - Курс
            // valuteCursOnDateType.getVcurs();
            //    Vname - Название валюты
            //  valuteCursOnDateType.getVname();
            //    Vnom - Номинал
            // valuteCursOnDateType.getVnom();

            //logger.debug(String.format("dsIntegrationLoadCurrencyData2: valuteCursOnDate: VchCode=%s Vcurs=%s ", //Vnom=%s
            //		valuteCursOnDate.getVchCode(),
            //		valuteCursOnDate.getVcurs().toString()
            //));
            Long currId = getCurrencyIdByParam(currList, "ALPHACODE", valuteCursOnDate.getVchCode());
            //logger.debug(String.format("dsIntegrationLoadCurrencyData2: currId=%d", currId));
            if (currId != null) {
                Long pairId = findPairIdByCurrencyId(pairList, currId, currRurId);
                //logger.debug(String.format("dsIntegrationLoadCurrencyData2: pairId=%d", pairId));
                if ((pairId != null) && (valuteCursOnDate.getVcurs() != null)) {
                    Integer vnom = valuteCursOnDate.getVnom();
                    BigDecimal course = valuteCursOnDate.getVcurs().divide(BigDecimal.valueOf(vnom.longValue()));
                    Map<String, Object> resultMap = createCurrencyExchange(pairId, cbrfExchangeTypeId, curDate, 1, course, login, password);
                    recordList.add(resultMap);
                }
                // обратный курс
                pairId = findPairIdByCurrencyId(pairList, currRurId, currId);
                //logger.debug(String.format("dsIntegrationLoadCurrencyData2: 2 pairId=%d", pairId));
                if ((pairId != null) && (valuteCursOnDate.getVcurs() != null)) {
                    Integer vnom = valuteCursOnDate.getVnom();
                    BigDecimal reverseCourse = BigDecimal.valueOf(vnom.longValue());
                    reverseCourse = reverseCourse.divide(valuteCursOnDate.getVcurs(), MathContext.DECIMAL32);
                    Map<String, Object> resultMap = createCurrencyExchange(pairId, cbrfExchangeTypeId, curDate, 1, reverseCourse, login, password);
                    recordList.add(resultMap);
                }
            }
        }
        return result;
    }

    private Map<String, Object> createCurrencyExchange(Long pairId, Long cbrfExchangeTypeId, Date curDate, int unitNumber, BigDecimal courseValue, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CurrencyPairId", pairId);
        params.put("CourseValue", courseValue.toString());
        params.put("CourseDate", curDate);
        params.put("UnitNumber", unitNumber);
        params.put("ExchangeType", RATE_CB_RF);
        params.put("ExchangeTypeId", cbrfExchangeTypeId);
        Map<String, Object> callResult = this.callService(ADMINWS_SERVICE_NAME, "createCurrencyExchange", params, login, password);
        return params;
    }

    // есть ли в БД курс для указанной пары на указанную дату
    private boolean checkIsExchangeExist(Date curDate, String currPairId, String rateType, String login, String password) throws Exception {
        boolean result = false;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(curDate);

        GregorianCalendar sdate = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH));
        gc.add(Calendar.DAY_OF_MONTH, 1);
        GregorianCalendar fdate = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH));

        queryParams.put("exchangeType", rateType);
        queryParams.put("searchStartDate", sdate.getTime());
        queryParams.put("searchEndDate", fdate.getTime());
        queryParams.put("purposeCurrency", currPairId);
        Map<String, Object> qres = this.callService(ADMINWS_SERVICE_NAME, "getExchangeList", queryParams, login, password);
        if (("OK".equalsIgnoreCase((String) (qres.get("Status"))))) {
            List<Map<String, Object>> listExchange = (List<Map<String, Object>>) qres.get(RESULT);
            if (!listExchange.isEmpty()) {
                result = true;
            }
        }
        return result;
    }

    // считать все пары со статусом 1 (для которых необходимо загрузить курс)
    private List<Map<String, Object>> getCurrencyPairList(List<Map<String, Object>> currList, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("STATUS", 1);
        Map<String, Object> callResult = this.callService(ADMINWS_SERVICE_NAME, "admGetCurrencyPairsList", params, login, password);
        if (!("OK".equalsIgnoreCase((String) (callResult.get("Status"))))) {
            throw new Exception(String.format("CurrencyIntegrationFacade: getCurrencyPairList call failed.\n%s", (String) callResult.get("Error")));
        }
        List<Map<String, Object>> pairList = (List<Map<String, Object>>) callResult.get(RESULT);
        for (Map<String, Object> pair : pairList) {
            String curr = (String) pair.get("QUOTEDCURRENCY");
            Long currId = getCurrencyIdByParam(currList, "CURRENCYNAME", curr);
            pair.put("QUOTEDCURRENCYID", currId);

            curr = (String) pair.get("BASECURRENCY");
            currId = getCurrencyIdByParam(currList, "CURRENCYNAME", curr);
            pair.put("BASECURRENCYID", currId);
        }
        return pairList;
    }

    private Long findPairIdByCurrencyId(List<Map<String, Object>> pairList, Long sourceCurrencyId, Long targetCurrencyId) throws Exception {
        if (sourceCurrencyId == null) {
            throw new Exception("CurrencyIntegrationFacade / findPairIdByCurrencyId:  sourceCurrencyId = null");
        }
        if (targetCurrencyId == null) {
            throw new Exception("CurrencyIntegrationFacade / findPairIdByCurrencyId:  targetCurrencyId = null");
        }
        for (Map<String, Object> pair : pairList) {
            Long baseCurrencyId = (Long) pair.get("BASECURRENCYID");
            Long quotedCurrencyId = (Long) pair.get("QUOTEDCURRENCYID");
            if ((baseCurrencyId != null) && (quotedCurrencyId != null)) {
                if ((sourceCurrencyId.longValue() == baseCurrencyId.longValue()) && (targetCurrencyId.longValue() == quotedCurrencyId.longValue())) {
                    return (Long) pair.get("CURRENCYPAIRID");
                }
            }
        }
        return null;
    }

    // считать валюты
    private List<Map<String, Object>> getCurrencyList(String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> callResult = this.callService(ADMINWS_SERVICE_NAME, "admRefCurrencyList", params, login, password);
        if (!("OK".equalsIgnoreCase((String) (callResult.get("Status"))))) {
            throw new Exception(String.format("CurrencyIntegrationFacade: getCurrencyList call failed.\n%s", (String) callResult.get("Error")));
        }
        return (List<Map<String, Object>>) callResult.get(RESULT);
    }

    // ИД валюты по сокращению
    private Long getCurrencyIdByParam(List<Map<String, Object>> currList, String paramName, String paramValue) throws Exception {
        if (paramName == null) {
            throw new Exception("CurrencyIntegrationFacade / getCurrencyIdByParam:  paramName = null");
        }
        if (paramValue == null) {
            throw new Exception("CurrencyIntegrationFacade / getCurrencyIdByParam:  paramValue = null");
        }
        for (Map<String, Object> curr : currList) {
            if (paramValue.equalsIgnoreCase((String) curr.get(paramName))) {
                return (Long) curr.get("CURRENCYID");
            }
        }
        return null;
    }

    // ИД типа курса по сокращению
    private Long getGetCurrencyExchangeTypeIdByBrief(String brief, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("SYSNAME", "CentralBank");
        Map<String, Object> callResult = this.callService(ADMINWS_SERVICE_NAME, "getExchangeTypes", params, login, password);
        if (!("OK".equalsIgnoreCase((String) (callResult.get("Status"))))) {
            throw new Exception(String.format("CurrencyIntegrationFacade: getGetCurrencyExchangeTypeIdByBrief call failed.\n%s", (String) callResult.get("Error")));
        }
        List<Map<String, Object>> callList = (List<Map<String, Object>>) callResult.get(RESULT);
        if ((callList != null) && (callList.size() > 0)) {
            Map<String, Object> callItem = callList.get(0);
            return (Long) callItem.get("EXCHANGETYPEID");
        } else {
            return null;
        }
    }

    // отправить почту админу о проблемах при загрузке курсов
    private void sendEmailMessage(String roleList, boolean usdFlag, boolean eurFlag, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("ROLELIST", roleList);
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsGetUsersDataByRoleList", queryParams, login, password);
        if (qres != null) {
            if (qres.get(RESULT) != null) {
                List<Map<String, Object>> userList = (List<Map<String, Object>>) qres.get(RESULT);
                if (!userList.isEmpty()) {
                    String emailList = userList.get(userList.size() - 1).get("EMAILSTRLIST").toString();
                    if ((emailList != null) && (!emailList.isEmpty())) {
                        // получили список адресатов. генерим сообщение, шлем по адресам.
                        String title = "ПП FLEXTERA";
                        String message = "Курсы валют обновлены";
                        if ((!usdFlag) && (!eurFlag)) {
                            message = "Не удалось обновить текущие курсы валют. Необходимо обновить их вручную.";
                        } else {
                            if ((!usdFlag) && (eurFlag)) {
                                message = "Не удалось обновить текущий курс USD. Необходимо обновить его вручную.";
                            }
                            if ((usdFlag) && (!eurFlag)) {
                                message = "Не удалось обновить текущий курс EUR. Необходимо обновить его вручную.";
                            }
                        }
                        Map<String, Object> mailParams = new HashMap<String, Object>();
                        mailParams.put("SMTPSubject", title);
                        mailParams.put("SMTPReceipt", emailList);
                        mailParams.put("SMTPMESSAGE", message);
                        this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", mailParams, login, password);
                    }
                }
            }
        }
    }
}
