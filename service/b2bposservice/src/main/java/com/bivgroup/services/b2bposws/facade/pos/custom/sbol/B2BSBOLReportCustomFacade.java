/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.sbol;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.currency.AmountUtils;

/**
 *
 * @author ilich
 */
@BOName("B2BSBOLReportCustom")
public class B2BSBOLReportCustomFacade extends B2BBaseFacade {

    private static final String RISKLIMITHBDATAVERID_PARAMNAME = "RISKLIMITHBDATAVERID";

    private static final NumberFormat moneyFormatter = NumberFormat.getNumberInstance(new Locale("ru"));

    private static final String[] MONTHS_FOR_STRING_DATE = {
        "января",
        "февраля",
        "марта",
        "апреля",
        "мая",
        "июня",
        "июля",
        "августа",
        "сентября",
        "октября",
        "ноября",
        "декабря"
    };

    private void loadRiskLimits(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> getProdConfIDParams = new HashMap<String, Object>();
        getProdConfIDParams.put("PRODVERID", contrMap.get("PRODVERID"));
        Long prodConfId = (Long) this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductConfigBrowseListByParam", getProdConfIDParams, login, password, "PRODCONFID");
        Map<String, Object> defParams = new HashMap<String, Object>();
        defParams.put(RETURN_AS_HASH_MAP, "TRUE");
        defParams.put("PRODCONFID", prodConfId);
        defParams.put("NAME", RISKLIMITHBDATAVERID_PARAMNAME);
        Long hbDataVerId = getLongParam(this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductDefaultValueBrowseListByParam", defParams, login, password, "VALUE"));
        if (hbDataVerId != null) {
            Map<String, Object> contrExtMap = (Map<String, Object>) contrMap.get("CONTREXTMAP");
            if ((contrExtMap != null) && (contrExtMap.get("insuranceTerritory") != null) && (contrExtMap.get("annualPolicy") != null)
                    && (contrExtMap.get("insuranceProgram") != null)) {
                Long insuranceTerritory = Long.valueOf(contrExtMap.get("insuranceTerritory").toString());
                String insuranceTerritoryS;
                switch (insuranceTerritory.intValue()) {
                    case 0:
                        insuranceTerritoryS = "NoUSARF";
                        break;
                    case 1:
                        insuranceTerritoryS = "NoRF";
                        break;
                    case 2:
                        insuranceTerritoryS = "RFSNG";
                        break;
                    case 3:
                        insuranceTerritoryS = "NoUSA";
                        break;
                    default:
                        insuranceTerritoryS = "unknown";
                }
                Long annualPolicy = Long.valueOf(contrExtMap.get("annualPolicy").toString());
                Long prodProgId = Long.valueOf(contrMap.get("PRODPROGID").toString());
                Map<String, Object> progParams = new HashMap<String, Object>();
                progParams.put(RETURN_AS_HASH_MAP, "TRUE");
                progParams.put("PRODPROGID", prodProgId);
                Map<String, Object> progRes = this.callService(Constants.B2BPOSWS, "dsB2BProductProgramBrowseListByParam", progParams, login, password);
                Double progInsAmValue = Double.valueOf(progRes.get("INSAMVALUE").toString());
                String insuranceProgramS = "unknown";
                if (Math.abs(progInsAmValue.doubleValue() - 15000.0) < 0.0001) {
                    insuranceProgramS = "VZR_RFCLASSIC";
                }
                if (Math.abs(progInsAmValue.doubleValue() - 30000.0) < 0.0001) {
                    insuranceProgramS = "VZR_BASIC";
                }
                if (Math.abs(progInsAmValue.doubleValue() - 60000.0) < 0.0001) {
                    insuranceProgramS = "VZR_CLASSIC";
                }
                if (Math.abs(progInsAmValue.doubleValue() - 120000.0) < 0.0001) {
                    insuranceProgramS = "VZR_PREMIUM";
                }
                Map<String, Object> hbParams = new HashMap<String, Object>();
                hbParams.put("HBDATAVERID", hbDataVerId);
                hbParams.put("territorySysName", insuranceTerritoryS);
                hbParams.put("travelKind", annualPolicy);
                hbParams.put("programSysName", insuranceProgramS);
                Map<String, Object> hbRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", hbParams, login, password);
                if ((hbRes != null) && (hbRes.get(RESULT) != null) && (((List<Map<String, Object>>) hbRes.get(RESULT)).size() > 0)) {
                    List<Map<String, Object>> limitList = (List<Map<String, Object>>) hbRes.get(RESULT);
                    if (contrMap.get("INSOBJGROUPLIST") != null) {
                        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contrMap.get("INSOBJGROUPLIST");
                        for (Map<String, Object> insObjGroupBean : insObjGroupList) {
                            if (insObjGroupBean.get("OBJLIST") != null) {
                                List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupBean.get("OBJLIST");
                                for (Map<String, Object> objBean : objList) {
                                    if (objBean.get("CONTROBJMAP") != null) {
                                        Map<String, Object> contrObjMap = (Map<String, Object>) objBean.get("CONTROBJMAP");
                                        if (contrObjMap.get("CONTRRISKLIST") != null) {
                                            List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                                            for (Map<String, Object> riskBean : contrRiskList) {
                                                String riskSysName = riskBean.get("PRODRISKSYSNAME").toString();
                                                List<Map<String, Object>> limitData = new ArrayList<Map<String, Object>>();
                                                for (Map<String, Object> lBean : limitList) {
                                                    if (lBean.get("riskSysName").toString().equalsIgnoreCase(riskSysName)) {
                                                        limitData.add(lBean);
                                                    }
                                                }
                                                riskBean.put("LIMITLIST", limitData);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // генерация строковых представлений для всех сумм
    protected void genSumStrs(Map<String, Object> data, String dataNodePath) {
        Map<String, Object> parsedMap = new HashMap<String, Object>();
        parsedMap.putAll(data);
        for (Map.Entry<String, Object> entry : parsedMap.entrySet()) {
            String keyName = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String dataValuePath = dataNodePath + "." + keyName;
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    genSumStrs(map, dataValuePath);
                } else if (value instanceof List) {
                    ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) value;
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, Object> element = list.get(i);
                        genSumStrs(element, dataValuePath + "[" + i + "]");
                    }
                } else if (keyName.endsWith("VALUE")) {
                    // Страховая сумма и её валюта
                    try {
                        Double insAmValue = Double.valueOf(value.toString());
                        logger.debug(dataValuePath + " = " + insAmValue);
                        String currencyKeyName = keyName.replace("VALUE", "") + "CURRENCYID";
                        String insAmCurrNumCode = getStringParam(data.get(currencyKeyName));
                        if ("1".equalsIgnoreCase(insAmCurrNumCode)) {
                            insAmCurrNumCode = "810"; // рубли
                        }
                        if ("2".equalsIgnoreCase(insAmCurrNumCode)) {
                            insAmCurrNumCode = "840"; // доллары
                        } else if ("3".equalsIgnoreCase(insAmCurrNumCode)) {
                            insAmCurrNumCode = "978"; // евро
                        } else {
                            insAmCurrNumCode = "810"; // по-умолчанию: рубли
                        }
                        // отдельно валюта не требуется, уже учитывается при вызове amountToString
                        //reportData.put("INSAMCURRENCYSTR", getCurrByCodeToNum("RUB", insAmValue.longValue())); 
                        // "Тринадцать тысяч четыреста тридцать восемь рублей 24 копейки"
                        String insAmStr = AmountUtils.amountToString(insAmValue, insAmCurrNumCode);
                        
                        // "Тринадцать тысяч четыреста тридцать восемь рублей 24 копейки" > " (Тринадцать тысяч четыреста тридцать восемь) рублей 24 копейки"
                        String insAmStrBill = " (" + insAmStr.replace(" рубл", ") рубл");

                        // отбрасываем нулевые копейки
                        String insAmStrSumInSkobki = insAmStrBill.replace(" 00 копеек", "").replace(" 00 евроцентов", "").replace(" 00 центов", "");

                        // отбрасываем нулевые копейки
                        insAmStr = insAmStr.replace(" 00 копеек", "").replace(" 00 евроцентов", "").replace(" 00 центов", "");
                        String insAmNumStr = moneyFormatter.format(insAmValue);
                        String sumValueStr = insAmNumStr + " (" + insAmStr + ")";
                        
                        data.put(keyName + "STR2", insAmNumStr + insAmStrSumInSkobki );
                        data.put(keyName + "STR", sumValueStr);
                        logger.debug(dataNodePath + "." + keyName + "STR = " + sumValueStr);
                        
                        // 13438.24 > "13 438"
                        String insAmNumStrBill = moneyFormatter.format(insAmValue.intValue());
                        // "13 438 (Тринадцать тысяч четыреста тридцать восемь) рублей 24 копейки"
                        String sumValueStrBill =  insAmNumStrBill + insAmStrBill;
                        data.put(keyName + "STRBILL", sumValueStrBill);
                        logger.debug(dataNodePath + "." + keyName + "STRBILL = " + sumValueStrBill);
                        
                    } catch (NumberFormatException ex) {
                        logger.debug(dataValuePath + " - не сумма.");
                    } catch (IllegalArgumentException ex) {
                        logger.debug(dataValuePath + " не удалось преобразовать в строковое представление суммы.");
                    }
                }
            }
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractTextSums(Map<String, Object> params) throws Exception {
        genSumStrs(params, "*");
        return params;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BReportGetData(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // запрос сведений договора
        Map<String, Object> browseParams = new HashMap<String, Object>();
        browseParams.put(RETURN_AS_HASH_MAP, "TRUE");
        browseParams.put("CONTRID", params.get("CONTRID"));
        Map<String, Object> result = this.callService(Constants.B2BPOSWS, "dsB2BContractUniversalLoad", browseParams, login, password);

        // загрузка спиков лимитов по рискам
        loadRiskLimits(result, login, password);

        // генерация строковых представлений для всех сумм
        genSumStrs(result, "*");
        if (result.get("DOCUMENTDATE") != null) {
            String value = result.get("STARTDATE").toString();
            try {

                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                Date date = format.parse(value);
                SimpleDateFormat dateFormatterMonth;
                Locale russianLocale = new Locale("ru");
                DateFormatSymbols russianDateFormatSymbols = DateFormatSymbols.getInstance(russianLocale);
                russianDateFormatSymbols.setMonths(MONTHS_FOR_STRING_DATE);
                dateFormatterMonth = new SimpleDateFormat("«dd» MMMMM yyyy", russianLocale);
                dateFormatterMonth.setDateFormatSymbols(russianDateFormatSymbols);

                String reportDateMonth = dateFormatterMonth.format(date);
                result.put("DOCUMENTDATEMONTHLYSTR", reportDateMonth);
            } catch (Exception e) {
            }
        }
        // если идентификатор конфигурации продукта был известен на момент вызова текущего метода - выполняется дополнение им результата
        Object prodConfID = params.get("PRODCONFID");
        if (prodConfID != null) {
            result.put("PRODCONFID", prodConfID);
        }

        return result;
    }

}
