/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import com.bivgroup.services.bivsberposws.system.Constants;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author ilich
 */
@BOName("AngularCalculatorCustom")
public class AngularCalculatorCustomFacade extends BaseFacade {

    private static final String REFWS_SERVICE_NAME = Constants.REFWS;
    private static final String WEBSMSWS_SERVICE_NAME = Constants.WEBSMSWS;
    private static final String COREWS_SERVICE_NAME = Constants.COREWS;

    private List<String> parseRiskSysNames(String riskSysNames) {
        if ((riskSysNames != null) && (!riskSysNames.isEmpty())) {
            List<String> result = new ArrayList();
            String[] arr = riskSysNames.split(",");
            if ((arr != null) && (arr.length > 0)) {
                result.addAll(Arrays.asList(arr));
            }
            return result;
        } else {
            return new ArrayList();
        }
    }

    private List<Map<String, Object>> prepareRiskList(List<String> riskSysNames) {
        if (riskSysNames != null) {
            List<Map<String, Object>> result = new ArrayList();
            for (String bean : riskSysNames) {
                Map<String, Object> riskMap = new HashMap<String, Object>();
                riskMap.put("PRODRISKSYSNAME", bean);
                result.add(riskMap);
            }
            return result;
        } else {
            return new ArrayList();
        }
    }

    private void addInsured(List<Map<String, Object>> insuredList, Long insuredCount, Long ageId, List<String> riskSysNames) {
        if (insuredCount.intValue() > 0) {
            Map<String, Object> insuredMap = new HashMap<String, Object>();
            insuredMap.put("AGEID", ageId);
            insuredMap.put("riskList", prepareRiskList(riskSysNames));
            insuredList.add(insuredMap);
        }
    }

    protected String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else {
            return bean.toString();
        }
    }

    protected Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }


    protected Date getDateParam(Object date) {
        if (date != null) {
            if (date instanceof Double) {
                return XMLUtil.convertDate((Double) date);
            } else if (date instanceof BigDecimal) {
                return XMLUtil.convertDate((BigDecimal) date);
            }
            return (Date) date;
        } else {
            return null;
        }
    }

    private Double getExchangeCourceByCurID(Long currencyId, Date date, String login, String password) throws Exception {
        Double result = 1.0;

        Map<String, Object> curParams = new HashMap<String, Object>();
        curParams.put("CurrencyID", currencyId);
        Map<String, Object> curRes = this.callService(REFWS_SERVICE_NAME, "getCurrencyByParams", curParams, login, password);
        List<Map<String, Object>> curList = WsUtils.getListFromResultMap(curRes);
        if (curList != null) {
            if (!curList.isEmpty()) {
                String curCode = getStringParam(curList.get(0).get("Brief"));
                Map<String, Object> exParams = new HashMap<String, Object>();
                exParams.put("natCurCode", curCode);
                exParams.put("QuotedCurrencyID", 1);
                exParams.put("Date", date);
                Map<String, Object> exRes = this.callService(REFWS_SERVICE_NAME, "getCurrencyPairForCrossCourseByParams", exParams, login, password);
                List<Map<String, Object>> exList = WsUtils.getListFromResultMap(exRes);
                if (exList != null) {
                    if (!exList.isEmpty()) {
                        if (exList.get(0).get("COURSEVALUE") != null) {
                            Date cd = getDateParam(exList.get(0).get("COURSEDATE"));
                            GregorianCalendar gc = new GregorianCalendar();
                            gc.setTime(cd);
                            gc.set(Calendar.HOUR_OF_DAY, 0);
                            gc.set(Calendar.MINUTE, 0);
                            gc.set(Calendar.SECOND, 0);
                            gc.set(Calendar.MILLISECOND, 0);
                            GregorianCalendar cgc = new GregorianCalendar();
                            cgc.setTime(date);
                            cgc.set(Calendar.HOUR_OF_DAY, 0);
                            cgc.set(Calendar.MINUTE, 0);
                            cgc.set(Calendar.SECOND, 0);
                            cgc.set(Calendar.MILLISECOND, 0);
                            result = getDoubleParam(exList.get(0).get("COURSEVALUE"));
                            /*if (gc.getTime().compareTo(cgc.getTime()) != 0) {
                             //даты не равны отправляем сообщение об ошибке
                             Map<String, Object> sysParam = new HashMap<String, Object>();
                             sysParam.put("SETTINGSYSNAME", "NOTIFICATIONEMAILS");
                             sysParam.put("ReturnAsHashMap", "TRUE");
                             Map<String, Object> sysRes = this.callService(COREWS_SERVICE_NAME, "getSysSettingBySysName", sysParam, login, password);
                             if (sysRes.get("SETTINGVALUE") != null) {
                             String emails = sysRes.get("SETTINGVALUE").toString();
                             String[] emailList = emails.split(",");
                             boolean usdExist = true;
                             boolean euroExist = true;
                             if ("EUR".equals(curCode)) {
                             euroExist = false;
                             }
                             if ("USD".equals(curCode)) {
                             usdExist = false;
                             }
                             for (String email : emailList) {
                             //4. отправляем на указанные адреса уведомления о отсутсвии курса валюты на заданную дату.
                             sendEmailCurrencyFail(email, date, usdExist, euroExist, login, password);
                             }
                             }
                             }*/

                        }
                    }
                }
            }
        }
        return result;
    }

    protected Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }



    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAngularCalculatePremium(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        /*         Long duration = 30L;//Long.valueOf(params.get("DURATION").toString());
         Long travelKind = 0L;//Long.valueOf(params.get("TRAVELTYPEID").toString());
         String territorySysName = "RFSNG";//params.get("TERRITORYSYSNAME").toString();
         String programSysName = "VZR_RFCLASSIC";
         String riskSysNames = "VZRmedical,VZRtripstop,VZRns,VZRsport";
         Long isSportEnabled = 1L;//Long.valueOf(params.get("ISSPORTENABLED").toString());
         Long insuredCount2 = 1L;//Long.valueOf(params.get("INSUREDCOUNT2").toString());
         Long insuredCount60 = 0L;//Long.valueOf(params.get("INSUREDCOUNT60").toString());
         Long insuredCount70 = 2L;//Long.valueOf(params.get("INSUREDCOUNT70").toString());
         Long currencyid = 1L;// (Long) params.put("currencyid", 1);*/
        Long calcVerId = 1070L;
        Map<String, Object> result = new HashMap<String, Object>();

        if (params.get("CALCMAP") != null) {
            Map<String, Object> calcParams = (Map<String, Object>) params.get("CALCMAP");
            Long duration = getLongParam(calcParams.get("duration"));
            Long travelKind = getLongParam(calcParams.get("travelKind"));
            String territorySysName = getStringParam(calcParams.get("territorySysName"));
            String programSysName = getStringParam(calcParams.get("programSysName"));
            String riskSysNames = getStringParam(calcParams.get("riskSysNames"));
            Long isSportEnabled = getLongParam(calcParams.get("isSportEnabled"));
            Long insuredCount2 = 1L;
            if (calcParams.get("insuredCount2") != null) {
                insuredCount2 = getLongParam(calcParams.get("insuredCount2"));
            }
            Long insuredCount60 = 0L;
            if (calcParams.get("insuredCount60") != null) {
                insuredCount60 = getLongParam(calcParams.get("insuredCount60"));
            }
            Long insuredCount70 = 0L;
            if (calcParams.get("insuredCount70") != null) {
                insuredCount70 = getLongParam(calcParams.get("insuredCount70"));
            }
            Long currencyid = getLongParam(calcParams.get("currencyId"));

            //
            List<String> riskSysNamesList = parseRiskSysNames(riskSysNames);
            List<Map<String, Object>> insuredList = new ArrayList();
            addInsured(insuredList, insuredCount2, 1L, riskSysNamesList);
            addInsured(insuredList, insuredCount60, 2L, riskSysNamesList);
            addInsured(insuredList, insuredCount70, 3L, riskSysNamesList);
            //
            Map<String, Object> calcMap = new HashMap<String, Object>();
            calcMap.put("ReturnAsHashMap", "TRUE");
            calcMap.put("CALCVERID", calcVerId);
            calcMap.put("insuredList", insuredList);
            calcMap.put("daysCount", duration);
            calcMap.put("travelKind", travelKind);
            calcMap.put("isSportEnabled", isSportEnabled);
            calcMap.put("territorySysName", territorySysName);
            calcMap.put("programSysName", programSysName);
            calcMap.put("CURRENCYID", currencyid);
            doCalc(result, insuredList, calcMap, riskSysNamesList, "RISKLIST", "PREMIUM", insuredCount2, insuredCount60, insuredCount70, login, password);

            if (travelKind.intValue() == 0) {
                travelKind = 1l;
                calcMap.put("ReturnAsHashMap", "TRUE");
                calcMap.put("CALCVERID", calcVerId);
                calcMap.put("insuredList", insuredList);
                calcMap.put("daysCount", duration);
                calcMap.put("travelKind", travelKind);
                calcMap.put("isSportEnabled", isSportEnabled);
                calcMap.put("territorySysName", territorySysName);
                calcMap.put("programSysName", programSysName);
                calcMap.put("CURRENCYID", currencyid);
                doCalc(result, insuredList, calcMap, riskSysNamesList, "yearRISKLIST", "yearPREMIUM", insuredCount2, insuredCount60, insuredCount70, login, password);
            }
        }

        return result;
    }

    private void doCalc(Map<String, Object> result, List<Map<String, Object>> insuredList, Map<String, Object> calcMap,
            List<String> riskSysNamesList, String risklistName, String premName,
            Long insuredCount2, Long insuredCount60, Long insuredCount70, String login, String password) throws Exception {
        Map<String, Object> calcRes = this.callService(WsConstants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcMap, login, password);
        Double exchangeRate = 1.0;
        if (calcMap.get("CURRENCYRATE") != null) {
            exchangeRate = getDoubleParam(calcMap.get("CURRENCYRATE"));
        } else {
            Long currencyId = (Long) calcMap.get("CURRENCYID");
            exchangeRate = getExchangeCourceByCurID(currencyId, new Date(), login, password);
        }

        //
        MathContext myMathContext = new MathContext(15, RoundingMode.HALF_UP);
        if ((calcRes != null) && (calcRes.get("insuredList") != null)) {
            insuredList = (List<Map<String, Object>>) calcRes.get("insuredList");
            Double premium = 0.0;
            List<Map<String, Object>> riskList = prepareRiskList(riskSysNamesList);
            for (Map<String, Object> riskBean : riskList) {
                Double riskPremium = 0.0;
                for (Map<String, Object> insBean : insuredList) {
                    Long ageId = getLongParam(insBean.get("AGEID"));
                    Long count = 0L;
                    switch (ageId.intValue()) {
                        case 1:
                            count = insuredCount2;
                            break;
                        case 2:
                            count = insuredCount60;
                            break;
                        case 3:
                            count = insuredCount70;
                            break;
                    }
                    List<Map<String, Object>> insRiskList = (List<Map<String, Object>>) insBean.get("riskList");
                    for (Map<String, Object> insRiskBean : insRiskList) {
                        if (insRiskBean.get("PRODRISKSYSNAME").toString().equals(riskBean.get("PRODRISKSYSNAME").toString())) {
                            if (insRiskBean.get("PREMIUM") != null) {
                                riskPremium += Double.valueOf(insRiskBean.get("PREMIUM").toString()) * count.doubleValue();
                            }
                            break;
                        }
                    }
                }
                riskBean.put("PREMIUMBASE", (new BigDecimal(Double.valueOf(riskPremium).toString(), myMathContext).setScale(2, RoundingMode.HALF_UP)).doubleValue());
                riskPremium = riskPremium * exchangeRate;
                riskPremium = (new BigDecimal(Double.valueOf(riskPremium).toString(), myMathContext).setScale(2, RoundingMode.HALF_UP)).doubleValue();
                riskBean.put("PREMIUM", riskPremium);
                premium += riskPremium;
            }
            result.put(premName, premium);
            //result.put(premName, (new BigDecimal(premium, myMathContext).setScale(2, RoundingMode.HALF_UP)).doubleValue());
            result.put(risklistName, riskList);
        }
    }

}
