/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.nashorn.internal.runtime.regexp.RegExp;
import jdk.nashorn.internal.runtime.regexp.RegExpMatcher;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;

/**
 *
 * @author kkulkov
 */
@BOName("ActivateCustom")
public class B2BActivateCustomFacade extends B2BBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    private static final String B2BPOSWS = Constants.B2BPOSWS;
    private static final String SIGNB2BPOSWS_SERVICE_NAME = Constants.SIGNB2BPOSWS;

    private boolean checkActivePreriod(Date date, Date beginDate, Date endDate, boolean extBound) {
        GregorianCalendar innerDate = new GregorianCalendar();
        GregorianCalendar innerBeginDate = new GregorianCalendar();
        GregorianCalendar innerEndDate = new GregorianCalendar();
        if (null == date) {
            return Boolean.FALSE;
        }
        innerDate.setTime(date);
        boolean result;
        if (null != beginDate) {
            innerBeginDate.setTime(beginDate);
            if (extBound) {
                innerBeginDate.set(Calendar.HOUR, 0);
                innerBeginDate.set(Calendar.MINUTE, 0);
                innerBeginDate.set(Calendar.SECOND, 0);
                innerBeginDate.set(Calendar.MILLISECOND, 0);
                result = innerDate.getTimeInMillis() >= innerBeginDate.getTimeInMillis();
            } else {
                result = innerDate.after(innerBeginDate);
            }
        } else {
            result = Boolean.TRUE;
        }
        if (null != endDate) {
            innerEndDate.setTime(endDate);
            if (extBound) {
                innerEndDate.set(Calendar.HOUR, 23);
                innerEndDate.set(Calendar.MINUTE, 59);
                innerEndDate.set(Calendar.SECOND, 59);
                innerEndDate.set(Calendar.MILLISECOND, 999);
                result = innerDate.getTimeInMillis() <= innerEndDate.getTimeInMillis();
            } else {
                result = result && innerDate.before(innerEndDate);
            }
        }
        return result;
    }

    private String findProdDefValByName(List<Map<String, Object>> prodDefValList, String name) {
        for (Map<String, Object> bean : prodDefValList) {
            if ((bean.get("NAME") != null) && (bean.get("NAME").toString().equalsIgnoreCase(name))) {
                if (bean.get("VALUE") != null) {
                    return bean.get("VALUE").toString();
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private boolean checkActivationDate(Map<String, Object> activationInfo, Date date, String login, String password) throws Exception {
        GregorianCalendar innerDate = new GregorianCalendar();
        if (null == date) {
            return false;
        }
        innerDate.setTime(date);
        GregorianCalendar nowDate = new GregorianCalendar();
        // innerDate.setTime(new Date());
        if (null == activationInfo.get("PRODVERID")) {
            return false;

        }
        innerDate.set(Calendar.HOUR_OF_DAY, 0);
        innerDate.set(Calendar.MINUTE, 0);
        innerDate.set(Calendar.SECOND, 0);
        innerDate.set(Calendar.MILLISECOND, 0);
        nowDate.set(Calendar.HOUR_OF_DAY, 23);
        nowDate.set(Calendar.MINUTE, 59);
        nowDate.set(Calendar.SECOND, 59);
        nowDate.set(Calendar.MILLISECOND, 999);
        if (innerDate.getTimeInMillis() > nowDate.getTimeInMillis()) {
            // дата покупки больше чем сегодня - ошибка
            return true;
        }

        Map<String, Object> prodConfParams = new HashMap<String, Object>();
        prodConfParams.put(RETURN_AS_HASH_MAP, "TRUE");
        prodConfParams.put("PRODVERID", activationInfo.get("PRODVERID"));
        Map<String, Object> prodConfRes = this.callService(B2BPOSWS, "dsB2BProductConfigBrowseListByParam", prodConfParams, login, password);
        if (prodConfRes.get("PRODCONFID") != null) {
            Map<String, Object> prodDefValParams = new HashMap<String, Object>();
            prodDefValParams.put("PRODCONFID", prodConfRes.get("PRODCONFID"));
            List<Map<String, Object>> prodDefValList = this.callServiceAndGetListFromResultMap(B2BPOSWS, "dsB2BProductDefaultValueBrowseListByParam", prodDefValParams, login, password);
            if (prodDefValList != null) {
                String sdLagString = findProdDefValByName(prodDefValList, "SDLAG");
                activationInfo.put("SDLAG", sdLagString);
                if (sdLagString != null) {
                    try {

                        Long countDay = Long.parseLong(sdLagString);
                        if (countDay == 0L) {
                            //период охлаждения равен 0 = вседа ошибка
                            return true;
                        } else {
                            Date dateLag = addLagDaysToDate(innerDate.getTime(), countDay, 2, login, password);
                            //innerDate.add(Calendar.DAY_OF_YEAR, countDay.intValue());
                            innerDate.setTime(dateLag);
                            // если дата покупки не уложилась за период - ошибка
                            return innerDate.getTimeInMillis() <= nowDate.getTimeInMillis();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        // если в proddefval не указан лаг, то проверка не выполняется.
        return false;
    }

    @WsMethod(requiredParams = {"PAYDATE", "SERIES", "NUMBER", "CODE", "ACTIVATIONTYPE"})
    public Map<String, Object> dsB2BActivationCheck(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String code = params.get("CODE").toString();
        String number = params.get("NUMBER").toString();
        String series = params.get("SERIES").toString();
        Map<String, Object> qParams = new HashMap();
        //qParams.put("CODE", code);
        qParams.put("NUM", number);
        qParams.put("SERIES", series);
        Map<String, Object> result = new HashMap();
        if (params.get("ISAGREE") != null) {
            if ("true".equalsIgnoreCase(params.get("ISAGREE").toString())) {
                Map<String, Object> qResult = this.selectQuery("dsB2BActivationProductBrowseListByParam", "dsB2BActivationProductBrowseListByParamCount", qParams);
                result.put("STATUS", "NOPROCESSED");
                if ((null != qResult) && (null != qResult.get(RESULT)) && (!((List<Map<String, Object>>) qResult.get(RESULT)).isEmpty())) {
                    List<Map<String, Object>> qList = (List<Map<String, Object>>) qResult.get(RESULT);
                    Map<String, Object> cActivationInfo = qList.get(0);
                    if (cActivationInfo.get("CODE") != null) {
                        if (code.equals(getStringParam(cActivationInfo.get("CODE")))) {

                            if (!checkActivePreriod(new Date(),
                                    (Date) parseAnyDate(cActivationInfo.get("CODESTARTDATE"), Date.class, "CODESTARTDATE"),
                                    (Date) parseAnyDate(cActivationInfo.get("CODEENDDATE"), Date.class, "CODEENDDATE"), true)) {
                                result.put("STATUS", "CODEPERIODFAIL");
                                result.put("MESSAGE", "На текущию дату активация продукта невозможна");
                                return result;
                            }
                            if (!checkActivePreriod(new Date(),
                                    (Date) parseAnyDate(cActivationInfo.get("PRODUCTACTIVATIONSTARTDATE"), Date.class, "PRODUCTACTIVATIONSTARTDATE"),
                                    (Date) parseAnyDate(cActivationInfo.get("PRODUCTACTIVATIONENDDATE"), Date.class, "PRODUCTACTIVATIONENDDATE"), true)) {
                                result.put("STATUS", "ACTIVPERIODFAIL");
                                result.put("MESSAGE", "На текущию дату активация продукта невозможна");
                                return result;
                            }
                            if (checkActivationDate(cActivationInfo, (Date) parseAnyDate(params.get("PAYDATE"), Date.class, "PAYDATE"), login, password)) {
                                if (null != cActivationInfo.get("SDLAG")) {
                                    result.put("STATUS", "SDLAGPERIODFAIL");
                                    result.put("MESSAGE", String.format("Со дня покупки полиса прошло более чем %s дня(ей). Активация полиса невозможна.", cActivationInfo.get("SDLAG").toString()));
                                    return result;
                                } else {
                                    result.put("STATUS", "SDLAGFAIL");
                                    result.put("MESSAGE", "На текущию дату активация продукта невозможна");
                                    return result;
                                }
                            }
                            if ((null != cActivationInfo.get("ISACTIVATED")) && (Long.valueOf(cActivationInfo.get("ISACTIVATED").toString()) > 0)) {
                                result.put("STATUS", "ACTIVATE");
                                result.put("MESSAGE", "Полис уже активирован");
                                result.put(RESULT, cActivationInfo);
                                return result;
                            }
                            // Загрузка действий.
                            qParams.clear();
                            qParams.put("PRODACTIVID", cActivationInfo.get("PRODACTIVID"));
                            try {
                                Map<String, Object> actionRes = this.callService(B2BPOSWS, "dsB2BProductActivationContentBrowseListByParam", qParams, login, password);
                                if ((null != actionRes) && (null != actionRes.get(RESULT))) {
                                    cActivationInfo.put("ACTIONS", actionRes.get(RESULT));
                                }
                            } finally {

                            }
                            result.put(RESULT, cActivationInfo);
                            result.put("STATUS", "VALID");
                            result.put("MESSAGE", "Код активации принят");
                        } else {
                            result.put("STATUS", "CODEWRONG");
                            result.put("MESSAGE", "Код активации введен неверно");
                            return result;
                        }
                    } else {
                        result.put("STATUS", "CODEFAIL");
                        result.put("MESSAGE", "Код активации не определен");
                        return result;
                    }

                } else {
                    result.put("STATUS", "NOTFINDPOLICY");
                    result.put("MESSAGE", "Полис не найден");
                    return result;
                }
            } else {
                result.put("STATUS", "DONTAGREE");
                result.put("MESSAGE", "Клиент не дал согласие с пользовательским соглашением.");
                return result;
            }
        } else {
            result.put("STATUS", "DONTAGREE");
            result.put("MESSAGE", "Клиент не дал согласие с пользовательским соглашением.");
            return result;
        }

        return result;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BActivationGetCodeByContr(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String contrid = params.get("CONTRID").toString();
        Map<String, Object> qParams = new HashMap();
        Map<String, Object> res = new HashMap();
        //qParams.put("CODE", code);
        qParams.put("CONTRID", contrid);
        Map<String, Object> result = this.callService(B2BPOSWS, "dsB2BActivationCodeBrowseListByParam", qParams, login, password);
        if (result != null) {
            if (result.get(RESULT) != null) {
                List<Map<String, Object>> resList = (List<Map<String, Object>>) result.get(RESULT);
                if (resList.get(0).get("CODE") != null) {
                    res.put("CODE", resList.get(0).get("CODE"));
                }
            }
        }
        return res;
    }

    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> dsB2BActivateContrSave(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
        Date now = new Date();
        GregorianCalendar gcNow = new GregorianCalendar();
        gcNow.setTime(now);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String strNow = sdf.format(now);
        gcNow.add(Calendar.MINUTE, 1);
        Date dopNow = gcNow.getTime();
        String strDopNow = sdf.format(dopNow);
        //1. Подготовить мапу с договором по умолчанию, где страхователь=застрахованному и застрахованный=выгодопреобретатель        
        Map<String, Object> saveContrMap = prepareSaveContrMap(params);
        saveContrMap.put("DOCUMENTDATE", strNow);
        saveContrMap.put("SIGNDATE", strNow);
        //2. Сохранить исходную версию договора. по сгенереной мапе.
        Map<String, Object> saveRes = this.callExternalService(B2BPOSWS, "dsB2BContrSave", saveContrMap, login, password);
        // Подписываем договор
        if (saveRes != null) {
            if (saveRes.get(RESULT) != null) {
                Map<String, Object> savedContrMap = (Map<String, Object>) saveRes.get(RESULT);
                makeSignContract(savedContrMap, login, password);

                //3. если во входных параметрах переопределен застрахованный или выгодопреобретатель, то
                //обновленная инфа - допс делается в любом случае, просто если он не требовался, то допс - точная копия договора кроме даты 
                // дата дополнительного соглашения = дата договора + 1 минута
                //        if (isNeedDops(contrMap)) {
                //4. подготовить мапу допса.
                Map<String, Object> saveContrDopMap = prepareSaveContrDopMap(params, savedContrMap);
                saveContrDopMap.put("DOCUMENTDATE", strDopNow);
                saveContrDopMap.put("SIGNDATE", strDopNow);

                //5. сохранить под той же contrnodeid новую версию договора,         
                Map<String, Object> saveDopRes = this.callExternalService(B2BPOSWS, "dsB2BContrSave", saveContrDopMap, login, password);
                // Подписываем допс
                if ((null != saveDopRes) && (null != saveDopRes.get(RESULT))) {
                    makeSignContract((Map<String, Object>) saveDopRes.get(RESULT), login, password);
                }

                // 6. Отправка почты
                sendMails((Map<String, Object>) saveDopRes.get(RESULT), login, password);

                return saveDopRes;
            }
        }
        return saveRes;
    }

    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> dsB2BActivateContrSaveMigrate(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");

        //1. Подготовить мапу с договором по умолчанию, где страхователь=застрахованному и застрахованный=выгодопреобретатель        
        Map<String, Object> saveContrMap = prepareSaveContrMap(params);

        Date now = (Date) parseAnyDate(saveContrMap.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
        GregorianCalendar gcNow = new GregorianCalendar();
        if (now == null) {
            logger.error("Дата активации отсутсвтует.");
            now = (Date) parseAnyDate(saveContrMap.get("PAYDATE"), Date.class, "PAYDATE");
            if (now == null) {
                logger.error("Дата покупки отсутствует.");
                now = new Date();
            }
        }
        gcNow.setTime(now);
        //String documentDateStr = getStringParam(saveContrMap.get("DOCUMENTDATE"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String strNow = sdf.format(now);
        gcNow.add(Calendar.MINUTE, 1);
        Date dopNow = gcNow.getTime();
        String strDopNow = sdf.format(dopNow);

        saveContrMap.put("DOCUMENTDATE", strNow);
        saveContrMap.put("SIGNDATE", strNow);
        try {

            //2. Сохранить исходную версию договора. по сгенереной мапе.
            Map<String, Object> saveRes = this.callExternalService(B2BPOSWS, "dsB2BContrSave", saveContrMap, login, password);
            // Подписываем договор
            if (saveRes != null) {
                if (saveRes.get(RESULT) != null) {
                    Map<String, Object> savedContrMap = (Map<String, Object>) saveRes.get(RESULT);
                    makeSignContract(savedContrMap, login, password);

                    //3. если во входных параметрах переопределен застрахованный или выгодопреобретатель, то
                    //обновленная инфа - допс делается в любом случае, просто если он не требовался, то допс - точная копия договора кроме даты 
                    // дата дополнительного соглашения = дата договора + 1 минута
                    //        if (isNeedDops(contrMap)) {
                    //4. подготовить мапу допса.
                    Map<String, Object> saveContrDopMap = prepareSaveContrDopMap(params, savedContrMap);
                    saveContrDopMap.put("DOCUMENTDATE", strDopNow);
                    saveContrDopMap.put("SIGNDATE", strDopNow);

                    //5. сохранить под той же contrnodeid новую версию договора,         
                    Map<String, Object> saveDopRes = this.callExternalService(B2BPOSWS, "dsB2BContrSave", saveContrDopMap, login, password);
                    // Подписываем допс
                    if ((null != saveDopRes) && (null != saveDopRes.get(RESULT))) {
                        makeSignContract((Map<String, Object>) saveDopRes.get(RESULT), login, password);
                    }
                    Map<String, Object> sendParam = (Map<String, Object>) saveDopRes.get(RESULT);
                    // при миграции печать без отправки.
                    sendParam.put("ONLYPRINT", "TRUE");
                    // 6. Отправка почты
                    //sendMails(sendParam, login, password);

                    return saveDopRes;
                }
            }
            return saveRes;
        } catch (Exception e) {
            logger.error("Ошибка обработки договора.");
            logger.error(saveContrMap.toString());
        }

        return null;
    }

    @WsMethod()
    public Map<String, Object> dsB2BDoActivLiveContrMigrate(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // 1. выбрать данные из таблицы договоров
        List<Map<String, Object>> dataList = getActivatedContract(params);
        // 2. цикл по договорам.
        int count = dataList.size();
        logger.info("start migrate " + count + " contracts");
        int i = 0;
        for (Map<String, Object> datamap : dataList) {
            Map<String, Object> res = createActivatedContractByDataMap(datamap, login, password);
            // для теста выходим после первой итерации
            i++;
            logger.error("fictive error migrate " + i + " of " + count + " processed " + getStringParam(datamap.get("CONTRPOLSER")) + "-"  + getStringParam(datamap.get("CONTRPOLNUM")));
        }
        logger.info("finish migrate");
        // 3. разбор данных мапы,
        // - определить продукт
        // - сформировать мапу договора.
        // - создать договор
        // - создать допс.
        // - вызвать печать допса - без отправки уведомления страхователю.

        return null;
    }

    private Map<String, Object> prepareSaveContrMap(Map<String, Object> params) {
        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
        Map<String, Object> contrMapNew = new HashMap<String, Object>();
        contrMapNew.putAll(contrMap);

        if (isNeedDops(contrMap)) {
            if (contrMap.get("CONTREXTMAP") != null) {
                Map<String, Object> contrExtMap = (Map<String, Object>) contrMap.get("CONTREXTMAP");
                Map<String, Object> contrExtMapNew = new HashMap<String, Object>();
                contrExtMapNew.putAll(contrExtMap);
                // в договоре всегда страхователь=застрахованный=выгодопреобретатель по всем рискам.
                contrExtMapNew.put("PAYDATE", params.get("PAYDATE"));
                contrExtMapNew.put("insurerIsInsured", "1");
                contrExtMapNew.put("insurerIsBeneficiary", "1");
                contrMapNew.put("CONTREXTMAP", contrExtMapNew);
            }
        }

        return contrMapNew;
    }

    private Map<String, Object> prepareSaveContrDopMap(Map<String, Object> params, Map<String, Object> saveRes) {
        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
        contrMap.put("CONTRNODEID", saveRes.get("CONTRNODEID"));
        contrMap.put("VERNUMBER", getLongParam(saveRes.get("VERNUMBER")) + 1);
        if (contrMap.get("CONTREXTMAP") != null) {
            Map<String, Object> contrExtMap = (Map<String, Object>) contrMap.get("CONTREXTMAP");
            contrExtMap.put("PAYDATE", params.get("PAYDATE"));
        }
        return contrMap;
    }

    @WsMethod(requiredParams = {"PAYDATE", "SERIES", "NUMBER", "CODE", "ACTIVATIONTYPE", "CONTRMAP"})
    public Map<String, Object> dsB2BActivatePolicy(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> qParams = new HashMap();
        qParams.put("PAYDATE", params.get("PAYDATE"));
        qParams.put("SERIES", params.get("SERIES"));
        qParams.put("NUMBER", params.get("NUMBER"));
        qParams.put("CODE", params.get("CODE"));
        qParams.put("ACTIVATIONTYPE", params.get("ACTIVATIONTYPE"));
        qParams.put("ISAGREE", params.get("ISAGREE"));
        Map<String, Object> qResult = this.callService(B2BPOSWS, "dsB2BActivationCheck", qParams, login, password);
        // повтроная проверка что полис можно активировать.
        logger.debug("activate contr series=" + getStringParam(params.get("SERIES")) + " num=" + getStringParam(params.get("NUMBER")) + " code=" + getStringParam(params.get("CODE")));
        if ((null != qResult) && (null != qResult.get("STATUS")) && (!"VALID".equalsIgnoreCase(qResult.get("STATUS").toString()))) {
            result = qResult;
        } else if ((null != qResult) && ("VALID".equalsIgnoreCase(qResult.get("STATUS").toString()))) {
            if (qResult.get(RESULT) != null) {
                Map<String, Object> res = (Map<String, Object>) qResult.get(RESULT);
                if (res.get("ACTIVCODEID") != null) {
                    logger.debug("code valid");
                    Long codeId = getLongParam(res.get("ACTIVCODEID"));
                    //qParams.clear();
                    qParams.put("CONTRMAP", params.get("CONTRMAP"));
                    qResult = this.callService(B2BPOSWS, "dsB2BActivateContrSave", qParams, login, password);
                    if (qResult.get(RESULT) != null) {
                        Map<String, Object> resContrMap = (Map<String, Object>) qResult.get(RESULT);
                        if ((null != resContrMap) && (null != resContrMap.get("CONTRID"))) {
                            logger.debug("activation save succes");
                            qParams.clear();
                            qParams.put("ACTIVCODEID", codeId);
                            qParams.put("ISACTIVATED", 1L);
                            qParams.put("CONTRID", resContrMap.get("CONTRID"));
                            qParams.put("ACTIVATIONDATE", new Date());
                            result = this.callService(B2BPOSWS, "dsB2BActivationCodeUpdate", qParams, login, password);
                            result.put("STATUS", "ACTIVATED");

                            //Кирьянов А.С. 14.11.2016 наружу выбрасываем доп результат, продукт, ном. дог, ФИО страх, страх премия и сумму страх защиты
                            Map<String, Object> addResult = (Map<String, Object>) result.get(RESULT);
                            addResult.put("PRODNAME", resContrMap.get("PRODNAME"));
                            addResult.put("CONTRNUMBER", resContrMap.get("CONTRNUMBER"));
                            Map<String, Object> insurermap = (Map<String, Object>) resContrMap.get("INSURERMAP");
                            addResult.put("INSURERNAME", insurermap.get("BRIEFNAME"));
                            addResult.put("PREMVALUE", resContrMap.get("PREMVALUE"));
                            addResult.put("INSAMVALUE", resContrMap.get("INSAMVALUE"));

                            //TODO: генерация фиктивной оплаты по договору, с суммой по premvalue и датой платежа - указанной на интерфейсе.
                            //TODO: генерация печатных форм, ЭЦП
                            //TODO: отправка письма уведомления клиенту. с вложением полиса, допса
                        } else {
                            result = qResult;
                        }
                    } else {
                        result = qResult;
                    }
                }
            }

        } else {
            result = qResult;
        }
        return result;
    }

    @WsMethod(requiredParams = {"PAYDATE", "SERIES", "NUMBER", "CODE", "ACTIVATIONTYPE"})
    public Map<String, Object> dsB2BActivateResendNotify(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> qParams = new HashMap();
        qParams.put("PAYDATE", params.get("PAYDATE"));
        qParams.put("SERIES", params.get("SERIES"));
        qParams.put("NUMBER", params.get("NUMBER"));
        qParams.put("CODE", params.get("CODE"));
        qParams.put("ACTIVATIONTYPE", params.get("ACTIVATIONTYPE"));
        qParams.put("ISAGREE", params.get("ISAGREE"));
        Map<String, Object> qResult = this.callService(B2BPOSWS, "dsB2BActivationCheck", qParams, login, password);
        // повтроная проверка что полис активирован.
        if (qResult.get("STATUS") == null) {
            if (qResult.get(RESULT) != null) {
                qResult = (Map<String, Object>) qResult.get(RESULT);
            }
        }
        logger.debug("activate contr series=" + getStringParam(params.get("SERIES")) + " num=" + getStringParam(params.get("NUMBER")) + " code=" + getStringParam(params.get("CODE")));
        if ((null != qResult) && (null != qResult.get("STATUS")) && (!"ACTIVATE".equalsIgnoreCase(qResult.get("STATUS").toString()))) {
            result = qResult;
        } else if ((null != qResult) && ("ACTIVATE".equalsIgnoreCase(qResult.get("STATUS").toString()))) {
            if (qResult.get(RESULT) != null) {
                Map<String, Object> res = (Map<String, Object>) qResult.get(RESULT);
                if (res.get("ACTIVCODEID") != null) {
                    if (res.get("CONTRID") != null) {
                        logger.debug("code valid");
                        Long contrId = getLongParam(res.get("CONTRID"));
                        //qParams.clear();
                        Map<String, Object> contrParam = new HashMap<String, Object>();
                        contrParam.put("CONTRID", contrId);
                        contrParam.put("ReturnAsHashMap", "TRUE");
                        // для проверок потребуется полная мапа договора.
                        Map<String, Object> contrMap = this.callService(Constants.B2BPOSWS, "dsB2BContrLoad", contrParam, login, password);

                        if (params.get("EMAIL") == null) {
                            qResult = sendMails(contrMap, login, password);
                        } else {
                            qResult = sendMails(contrMap, getStringParam(params.get("EMAIL")), login, password);
                        }
                        if (qResult.get(RESULT) != null) {
                            result = qResult;
                        } else {
                            result = qResult;
                        }
                    }
                }
            }

        } else {
            result = qResult;
        }
        return result;
    }

    private boolean isNeedDops(Map<String, Object> contrMap) {
        boolean res = false;
        if (contrMap.get("CONTREXTMAP") != null) {
            Map<String, Object> contrExtMap = (Map<String, Object>) contrMap.get("CONTREXTMAP");
            if (!"1".equals(getStringParam(contrExtMap.get("insurerIsInsured")))) {
                res = true;
            }
            if (!"1".equals(getStringParam(contrExtMap.get("insurerIsBeneficiary")))) {
                res = true;
            }
        }
        return res;
    }

    private Map<String, Object> makeSignContract(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> result;
        String idFieldName = "CONTRID";
        String toState = "B2B_CONTRACT_PREPRINTING";
        String methodNamePrefix = "dsB2BContract";
        String typeSysName = "B2B_CONTRACT";
        result = recordMakeTrans(contrMap, toState, idFieldName, methodNamePrefix, typeSysName, login, password);
        if (result != null) {
            contrMap.put("STATESYSNAME", toState);
            toState = "B2B_CONTRACT_PREPARE";
            result = recordMakeTrans(contrMap, toState, idFieldName, methodNamePrefix, typeSysName, login, password);
            if (result != null) {
                contrMap.put("STATESYSNAME", toState);
                toState = "B2B_CONTRACT_SG";
                result = recordMakeTrans(contrMap, toState, idFieldName, methodNamePrefix, typeSysName, login, password);
                contrMap.put("STATESYSNAME", toState);
            }
        }
        return result;
    }

    private Map<String, Object> sendMails(Map<String, Object> contrMap, String altEmail, String login, String password) throws Exception {

        Map<String, Object> printParams = new HashMap<>();
        if (contrMap.get("CONTREXTMAP") != null) {
            Map<String, Object> contrExtMap = (Map<String, Object>) contrMap.get("CONTREXTMAP");
            printParams.put("REPLEVEL", 10L);
            printParams.put("ATTACHINFO", "");
            if (!"1".equals(getStringParam(contrExtMap.get("insurerIsInsured"))) && (contrExtMap.get("insurerIsInsured") != null)) {
                printParams.put("REPLEVEL", 1);
                printParams.put("ATTACHINFO", "К письму прилагается \"Соглашение о внесении изменений в Страховой Полис\", подписанное<br> усиленной электронной подписью.<br>");
            } else if (!"1".equals(getStringParam(contrExtMap.get("insurerIsBeneficiary"))) && (contrExtMap.get("insurerIsBeneficiary") != null)) {
                printParams.put("REPLEVEL", 2);
                printParams.put("ATTACHINFO", "К письму прилагается \"Соглашение о внесении изменений в Страховой Полис\", подписанное<br> усиленной электронной подписью.<br>");
            }
        }
        if (contrMap.get("INSURERMAP") != null) {
            Map<String, Object> insurerMap = (Map<String, Object>) contrMap.get("INSURERMAP");
            if (altEmail.isEmpty()) {
                if (insurerMap.get("contactList") != null) {
                    List<Map<String, Object>> contactList = (List<Map<String, Object>>) insurerMap.get("contactList");
                    for (Map<String, Object> contactMap : contactList) {
                        if (contactMap.get("CONTACTTYPESYSNAME") != null) {
                            if ("PersonalEmail".equalsIgnoreCase(getStringParam(contactMap.get("CONTACTTYPESYSNAME")))) {
                                printParams.put("INSUREREMAIL", getStringParam(contactMap.get("VALUE")));
                            }
                        }
                    }
                }
            } else {
                printParams.put("INSUREREMAIL", altEmail);
            }
            printParams.put("INSURERBRIEFNAME", insurerMap.get("BRIEFNAME"));
            printParams.put("INSURERFIRSTNAME", insurerMap.get("FIRSTNAME"));
            printParams.put("INSURERMIDDLENAME", insurerMap.get("MIDDLENAME"));
            printParams.put("INSURERLASTNAME", insurerMap.get("LASTNAME"));
            printParams.put("INSURERGENDER", insurerMap.get("GENDER"));

        }

        printParams.put("SMTPSubject", "Сбербанк-Страхование. Успешная активация полиса.");
        printParams.put("CONTRID", contrMap.get("CONTRID"));
        printParams.put("PRODCONFID", contrMap.get("PRODCONFID"));
        printParams.put("PRODNAME", contrMap.get("PRODNAME"));
        printParams.put("CONTRNUMBER", contrMap.get("CONTRNUMBER"));
        printParams.put("ONLYPRINT", contrMap.get("ONLYPRINT"));
        printParams.put("SENDCOPY", "TRUE");
        //printParams.put("SESSIONID", contrMap.get("SESSIONID"));
        //printParams.put("EDOC", 1);
        return this.callExternalService(SIGNB2BPOSWS_SERVICE_NAME, "dsB2BPrintAndSendAllDocument", printParams, login, password);
    }

    private Map<String, Object> sendMails(Map<String, Object> contrMap, String login, String password) throws Exception {
        return sendMails(contrMap, "", login, password);
    }

    private List<Map<String, Object>> getActivatedContract(Map<String, Object> params) throws Exception {
        return this.selectQueryAndGetListFromResultMap("dsB2BActivatedContractBrowseListByParamForMigrate", params);
    }

    private Map<String, Object> createActivatedContractByDataMap(Map<String, Object> datamap, String login, String password) throws Exception {


        Map<String, Object> CONTRMAP = prepareDataMap4Work(datamap);

        Map<String, Object> result = new HashMap<>();

        CONTRMAP.put("NOTE", "migrate activation");

        Map<String, Object> qParams = new HashMap();
        qParams.put("SERIES", CONTRMAP.get("CONTRPOLSER"));
        qParams.put("NUMBER", CONTRMAP.get("CONTRPOLNUM"));
        qParams.put("CODE", CONTRMAP.get("ACTIVCODE"));
        qParams.put("PAYDATE", parseDoubleDateFromStr(datamap.get("PAYDATESTR")));
        qParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qResult = this.callService(B2BPOSWS, "dsB2BActivationCodeBrowseListByParam", qParams, login, password);
        // повтроная проверка что полис можно активировать.
        removeOldMigrateContract(qResult, login, password);
        
        Map<String, Object> res = qResult;
        if (res.get("ACTIVCODEID") != null) {
            logger.debug("code valid");
            Long codeId = getLongParam(res.get("ACTIVCODEID"));
            //qParams.clear();
            qParams.put("CONTRMAP", CONTRMAP);
            qResult = this.callService(B2BPOSWS, "dsB2BActivateContrSaveMigrate", qParams, login, password);
            if (qResult.get(RESULT) != null) {
                Map<String, Object> resContrMap = (Map<String, Object>) qResult.get(RESULT);
                if ((null != resContrMap) && (null != resContrMap.get("CONTRID"))) {
                    logger.debug("activation save succes");
                    qParams.clear();
                    qParams.put("ISACTIVATED", 1L);
                    qParams.put("CONTRID", resContrMap.get("CONTRID"));
                    qParams.put("ACTIVATIONDATE", parseAnyDate(CONTRMAP.get("DOCUMENTDATE"), Double.class, "DOCUMENTDATE"));
                    if (codeId != null) {
                        qParams.put("ACTIVCODEID", codeId);
                        result = this.callService(B2BPOSWS, "dsB2BActivationCodeUpdate", qParams, login, password);
                    } else {
                        logger.error("Миграция: Не найден код активации по договору " + getStringParam(resContrMap.get("CONTRID")) + " создаем новый");
                        qParams.put("SERIES", codeId);
                        qParams.put("NUM", codeId);
                        qParams.put("CODE", codeId);
                        qParams.put("STARTDATE", 2);
                        qParams.put("ENDDATE", 76000);
                        result = this.callService(B2BPOSWS, "dsB2BActivationCodeCreate", qParams, login, password);
                    }

                } else {
                    result = qResult;
                }
            } else {
                result = qResult;
            }
        }

        return result;
    }

    private Map<String, Object> prepareDataMap4Work(Map<String, Object> datamap) {
        Map<String, Object> CONTRMAP = new HashMap<String, Object>();
        Map<String, Object> CONTREXTMAP = new HashMap<String, Object>();
        Boolean parseError = false;

        String product = getStringParam(datamap.get("PRODUCT"));
        String sernum = getStringParam(datamap.get("SERNUM"));
        String activCode = getStringParam(datamap.get("ACTIVCODE"));
        String[] serNumArr = sernum.split(" ");
        String contrser = serNumArr[0];
        String contrnum = serNumArr[1];
        String prodByNumber = getProductNameByNumber(contrnum);
        product = prodByNumber;
        if (product.equalsIgnoreCase(prodByNumber)) {
            if ("Ремень безопасности".equalsIgnoreCase(product)) {
                CONTRMAP.put("PRODID", "55000");
                CONTRMAP.put("PRODVERID", "55000");
                CONTRMAP.put("PRODNAME", "Ремень безопасности");
                //CONTRMAP.put("PRODSYSNAME", "BOX_SEAT_BELT");
                CONTRMAP.put("PRODSYSNAME", "SBELT_RTBOX");
            }
            if ("Верный выбор".equalsIgnoreCase(product)) {
                CONTRMAP.put("PRODID", "56000");
                CONTRMAP.put("PRODVERID", "56000");
                CONTRMAP.put("PRODNAME", "Верный выбор");
                //CONTRMAP.put("PRODSYSNAME", "BOX_RIGHT_CHOICE");
                CONTRMAP.put("PRODSYSNAME", "RIGHT_CHOICE_RTBOX");
            }
            CONTRMAP.put("CONTRPOLSER", contrser);
            CONTRMAP.put("CONTRPOLNUM", contrnum);
            CONTRMAP.put("ACTIVCODE", activCode);

            CONTREXTMAP.put("PAYDATE", parseDoubleDateFromStr(datamap.get("PAYDATESTR")));
            CONTRMAP.put("PAYDATE", parseDoubleDateFromStr(datamap.get("PAYDATESTR")));
            CONTRMAP.put("DOCUMENTDATE", parseDoubleDateFromStr(datamap.get("ACTIVATEDATESTR")));
            datamap.put("DOCUMENTDATE", parseDoubleDateFromStr(datamap.get("ACTIVATEDATESTR")));

            CONTRMAP.put("INSURERMAP", getParticipantMapFromMigration(datamap, "INSURER"));
            CONTRMAP.put("INSUREDMAP", getParticipantMapFromMigration(datamap, "INSURED"));
            CONTRMAP.put("BENEFICIARYMAP", getParticipantMapFromMigration(datamap, "BEN"));
            // вычислить флаги insurerIsInsured insurerIsBeneficiary по мапам лиц.
            int insurerIsInsured = checkParticipantEquile((Map<String, Object>) CONTRMAP.get("INSURERMAP"), (Map<String, Object>) CONTRMAP.get("INSUREDMAP"));
            int insurerIsBeneficiary = checkParticipantEquile((Map<String, Object>) CONTRMAP.get("INSURERMAP"), (Map<String, Object>) CONTRMAP.get("BENEFICIARYMAP"));
            CONTREXTMAP.put("insurerIsInsured", insurerIsInsured);
            CONTREXTMAP.put("insurerIsBeneficiary", insurerIsBeneficiary);
            // тип документа лица.
            // преобразовать даты.
            CONTRMAP.put("CONTREXTMAP", CONTREXTMAP);
        } else {
            parseError = true;
            logger.error("Миграция: расходится указанный продукт и номер договора. " + product + " " + sernum);
        }
        return CONTRMAP;

    }

    private Map<String, Object> getContrMap(Map<String, Object> datamap) {
        return null;
    }

    private String getProductNameByNumber(String contrnum) {
        String result = "";
        if (!contrnum.isEmpty()) {
            if (contrnum.startsWith("301")) {
                result = "Ремень безопасности";
            }
            if (contrnum.startsWith("302")) {
                result = "Верный выбор";
            }
        }
        return result;
    }

    private Object parseDoubleDateFromStr(Object dateStr) {
        String payDateStr = getStringParam(dateStr);
        Double payDate = Double.valueOf(payDateStr.replace(",", "."));
        return parseAnyDate(payDate, String.class, "PAYDATE");
    }

    private Map<String, Object> getParticipantMapFromMigration(Map<String, Object> datamap, String prefix) {
        //если фамилии имени нет, лицо отсутствует 
        if ((getStringParam(datamap.get(prefix + "SURNAME")).isEmpty())
                || (getStringParam(datamap.get(prefix + "NAME")).isEmpty())) {
            return null;
        }
        
        
        Map<String, Object> partMap = new HashMap<String, Object>();

        partMap.put("SURNAME", getStringParam(datamap.get(prefix + "SURNAME")));
        partMap.put("NAME", getStringParam(datamap.get(prefix + "NAME")));
        partMap.put("MIDDLENAME", getStringParam(datamap.get(prefix + "MIDDLENAME")));
        partMap.put("LASTNAME", getStringParam(datamap.get(prefix + "SURNAME")));
        partMap.put("FIRSTNAME", getStringParam(datamap.get(prefix + "NAME")));

        String birthDateStr = getStringParam(datamap.get(prefix + "BIRTHDATESTR"));
        String birthDataParseStr = getParsedDateFromStr(birthDateStr);
        String docDate = getStringParam(datamap.get("DOCUMENTDATE"));

        String gender = getStringParam(datamap.get(prefix + "GENDER"));
        if ("ж".equalsIgnoreCase(gender)) {
            partMap.put("GENDER", 1);
        } else {
            partMap.put("GENDER", 0);
        }

        String number = getStringParam(datamap.get(prefix + "NUMBER"));
        String series = getStringParam(datamap.get(prefix + "SERIES"));
        String issuedby = getStringParam(datamap.get(prefix + "ISSUEDBY"));
        String issuedateStr = getStringParam(datamap.get(prefix + "ISSUEDATESTR"));
        String issuedateParsedStr = getParsedDateFromStr(issuedateStr);
        String docType = getDocTypeBySeriesNumber(number, series);

        partMap.put("BIRTHDATE", birthDataParseStr);
        partMap.put("CITIZENSHIP", 1);
        partMap.put("ISCLIENT", 1);
        partMap.put("PARTICIPANTTYPE", 1);

        if ((partMap.get("MIDDLENAME") != null) && (partMap.get("MIDDLENAME").toString().compareTo("") != 0)) {
            String middleName = partMap.get("MIDDLENAME").toString();
            partMap.put("BRIEFNAME", String.format("%s %c. %c.", getStringParam(partMap.get("SURNAME")), getStringParam(partMap.get("NAME")).charAt(0), middleName.charAt(0)));
        } else {
            partMap.put("BRIEFNAME", String.format("%s %c.", getStringParam(partMap.get("SURNAME")), getStringParam(partMap.get("NAME")).charAt(0)));
        }

        List<Map<String, Object>> documentList = new ArrayList<Map<String, Object>>();
        Map<String, Object> docMap = new HashMap<String, Object>();
        docMap.put("DOCTYPESYSNAME", docType);
        if ("PassportRF".equalsIgnoreCase(docType)) {
            if ((series.length() == 6) && (number.length() == 4)) {
                String tseries = number;
                String tnumber = series;
                series = tseries;
                number = tnumber;
            }
        }
        partMap.put("SERIES", series);
        partMap.put("NUMBER", number);
        docMap.put("DOCSERIES", series);
        docMap.put("DOCNUMBER", number);
        docMap.put("ISSUEDATE", issuedateParsedStr);
        docMap.put("ISSUEDBY", issuedby);
        documentList.add(docMap);

        partMap.put("documentList", documentList);

        String email = getStringParam(datamap.get(prefix + "EMAIL"));
        String phone = getStringParam(datamap.get(prefix + "PHONE"));
        phone = phone.replaceAll("[^0-9.]", "");
        if (phone.length() > 10) {
            phone = phone.substring(1);
        }
        List<Map<String, Object>> contactList = new ArrayList<Map<String, Object>>();
        Map<String, Object> contactMap = new HashMap<String, Object>();
        if (!email.isEmpty()) {
            contactMap.put("CONTACTTYPESYSNAME", "PersonalEmail");
            contactMap.put("VALUE", email);
            contactList.add(contactMap);
        }
        Map<String, Object> contactMap1 = new HashMap<String, Object>();
        if (!phone.isEmpty()) {
            contactMap1.put("CONTACTTYPESYSNAME", "MobilePhone");
            contactMap1.put("VALUE", phone);
            contactList.add(contactMap1);
        }
        partMap.put("contactList", contactList);


        return partMap;
    }

    private String getParsedDateFromStr(String birthDateStr) {
        if (!birthDateStr.isEmpty()) {
            try {

                SimpleDateFormat parseFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                Date date = parseFormat.parse(birthDateStr);
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                return format.format(date);
            } catch (Exception e) {
                logger.error("Миграция: ошибка парса даты " + birthDateStr);
                return null;
            }
        }
        return null;
    }

    private String getDocTypeBySeriesNumber(String number, String series) {
        String sernum = series + number;
        if (sernum.matches("^[0-9]+$")) {
            return "PassportRF";
        } else {
            return "BornCertificate";
        }
    }

    private int checkParticipantEquile(Map<String, Object> insurer, Map<String, Object> other) {
        String insurerSurname = getStringParam(insurer.get("SURNAME"));
        String insurerName = getStringParam(insurer.get("NAME"));
        String insurerMiddleName = getStringParam(insurer.get("MIDDLENAME"));
        String insurerSeries = getStringParam(insurer.get("SERIES"));
        String insurerNumber = getStringParam(insurer.get("NUMBER"));

        if (other == null) {
            return 1;
        }
        if (insurer == null) {
            logger.error("Миграция: отсутствует страхователь");
        }

        String otherSurname = getStringParam(other.get("SURNAME"));
        String otherName = getStringParam(other.get("NAME"));
        String otherMiddleName = getStringParam(other.get("MIDDLENAME"));
        String otherSeries = getStringParam(other.get("SERIES"));
        String otherNumber = getStringParam(other.get("NUMBER"));

        if (insurerSurname.equalsIgnoreCase(otherSurname)
                && insurerName.equalsIgnoreCase(otherName)
                && insurerMiddleName.equalsIgnoreCase(otherMiddleName)
                && insurerSeries.equalsIgnoreCase(otherSeries)
                && insurerNumber.equalsIgnoreCase(otherNumber)) {
            return 1;
        }
        return 0;
    }

    private void removeOldMigrateContract(Map<String, Object> qResult, String login, String password) throws Exception {
        if (qResult.get("CONTRID") != null) {
            Map<String, Object> getParam = new HashMap<String, Object>();
            getParam.put("CONTRID", qResult.get("CONTRID"));
            getParam.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParam", getParam, login, password);
            if (res.get("CONTRNODEID") != null) {
                Map<String, Object> nodeParam = new HashMap<String, Object>();
                nodeParam.put("CONTRNODEID", res.get("CONTRNODEID"));
                Map<String, Object> resNode = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParam", nodeParam, login, password);
                
                if (resNode.get(RESULT) != null) {
                    this.callService(B2BPOSWS, "dsB2BContractNodeDelete", nodeParam, login, password);
                    
                    List<Map<String, Object>> contrListToRemove = (List<Map<String, Object>>) resNode.get(RESULT);
                    for (Map<String, Object> contrMapToRemove : contrListToRemove) {
                        if (contrMapToRemove.get("CONTRID") != null) {
                            Map<String, Object> delParam = new HashMap<String, Object>();
                            delParam.put("CONTRID", contrMapToRemove.get("CONTRID"));
                            Map<String, Object> delres = this.callService(B2BPOSWS, "dsB2BContractDelete", delParam, login, password);                            
                            
                        }
                    }
                }
                
            }
        }
    }
}
