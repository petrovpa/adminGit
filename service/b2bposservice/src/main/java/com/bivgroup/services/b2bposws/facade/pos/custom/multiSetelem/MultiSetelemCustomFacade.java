package com.bivgroup.services.b2bposws.facade.pos.custom.multiSetelem;

import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import static com.bivgroup.services.b2bposws.system.Constants.INSTARIFICATORWS;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author kkulkov
 */
@BOName("MultiSetelemCustom")
public class MultiSetelemCustomFacade extends ProductContractCustomFacade {

    private final Logger logger = Logger.getLogger(MultiSetelemCustomFacade.class);
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    private boolean validateSaveParams(Map<String, Object> contract) {
        boolean isDataInvalid = false;

        String errorText = "";
        Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        List<Map<String, Object>> insObjGroupList;
        if (insObjGroupListFromContract != null) {
            insObjGroupList = (List<Map<String, Object>>) insObjGroupListFromContract;
            if (insObjGroupList.size() == 1) {
                Map<String, Object> insObjGroup = insObjGroupList.get(0);

                String expLatinNum = "^[A-Za-z0-9]+";
                String expLatinNumRusSpaceDash = "^[A-Za-z0-9А-Яа-яЁё\\s\\-]+";
                String expYear = "^[0-9]{4}";
                if (isDataInvalid) {
                    errorText = errorText + "Сведения договора не сохранены.";
                    contract.put("Status", "Error");
                    contract.put("Error", errorText);
                }
            }
        }
        return !isDataInvalid;
    }

    private Double getCalcRateRule(Date documentDate, Long premCurrencyId, Map<String, Object> product) {
        if (product.get("PRODCALCRATERULES") != null) {
            List<Map<String, Object>> calcRateRuleList = (List<Map<String, Object>>) product.get("PRODCALCRATERULES");
            // список должен быть отсортирован по возрастанию поля RULEDATE
            for (Map<String, Object> bean : calcRateRuleList) {
                if ((bean.get("RULEDATE") != null) && (bean.get("CURRENCYID") != null)) {
                    Date ruleDate = (Date) parseAnyDate(bean.get("RULEDATE"), Date.class, "RULEDATE");
                    Long currencyId = Long.valueOf(bean.get("CURRENCYID").toString());
                    // нашли правило, подходящее под дату и валюту договора
                    if ((documentDate.getTime() < ruleDate.getTime()) && (premCurrencyId.longValue() == currencyId.longValue())) {
                        Long calcVariantId = Long.valueOf(bean.get("CALCVARIANTID").toString());
                        switch (calcVariantId.intValue()) {
                            case 4:
                                return getDoubleParam(bean.get("RATEVALUE"));
                        }
                    }
                }
            }
        }
        return 1.0d;
    }

    protected Map<String, Object> genAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {
        // идентификатор версии продукта всегда передается в явном виде с интерфейса
        Long prodVerID = getLongParam(contract.get("PRODVERID"));
        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            contract.put("PRODCONFID", prodConfID);
        }
        // инициализация даты документа
        GregorianCalendar documentDateGC = new GregorianCalendar();
        Object docDate = contract.get("DOCUMENTDATE");
        if (docDate == null) {
            documentDateGC.setTime(new Date());
            documentDateGC.set(Calendar.HOUR_OF_DAY, 0);
            documentDateGC.set(Calendar.MINUTE, 0);
            documentDateGC.set(Calendar.SECOND, 0);
            documentDateGC.set(Calendar.MILLISECOND, 0);
            contract.put("DOCUMENTDATE", documentDateGC.getTime());
        } else {
            documentDateGC.setTime((Date) parseAnyDate(docDate, Date.class, "DOCUMENTDATE"));
        }

        // безусловное вычисление даты начала действия
        GregorianCalendar startDateGC = new GregorianCalendar();
        if (contract.get("STARTDATE") == null) {
            startDateGC.setTime(documentDateGC.getTime());
            startDateGC.add(Calendar.DATE, 1);
            contract.put("STARTDATE", startDateGC.getTime());
        } else {
            Date startDate = getDateParam(contract.get("STARTDATE"));
            startDateGC.setTime(startDate);
        }

        // расширенные атрибуты договора
        Object contractExt = contract.get("CONTREXTMAP");
        Map<String, Object> contractExtValues;
        if (contractExt != null) {
            contractExtValues = (Map<String, Object>) contractExt;
        } else {
            contractExtValues = new HashMap<String, Object>();
            contract.put("CONTREXTMAP", contractExtValues);
        }

        // определение срока действия в годах
        Integer durationYears = getIntegerParam(contract.get("TERMID")); // todo: получение действительного срока действия по идентификатору срока
        if (durationYears == 0) {
            durationYears = 1;
        }
        // безусловное вычисление даты окончания действия
        GregorianCalendar finishDateGC = new GregorianCalendar();
        finishDateGC.setTime(startDateGC.getTime());
        finishDateGC.add(Calendar.YEAR, durationYears);
        finishDateGC.add(Calendar.DATE, -1);
        finishDateGC.set(Calendar.HOUR_OF_DAY, 23);
        finishDateGC.set(Calendar.MINUTE, 59);
        finishDateGC.set(Calendar.SECOND, 59);
        finishDateGC.set(Calendar.MILLISECOND, 0);
        contract.put("FINISHDATE", finishDateGC.getTime());

        // безусловное вычисление срока действия договора в днях
        long startDateInMillis = startDateGC.getTimeInMillis();
        long finishDateInMillis = finishDateGC.getTimeInMillis();
        // в сутках (24*60*60*1000) милисекунд
        long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000));
        contract.put("DURATION", duration);

        contract.put("INSAMCURRENCYID", 1L);
        contract.put("PREMCURRENCYID", 1L);

        //logger.debug("DOCUMENTDATE = " + parseAnyDate(contract.get("DOCUMENTDATE"), String.class, "DOCUMENTDATE", true));
        //logger.debug("STARTDATE = " + parseAnyDate(contract.get("STARTDATE"), String.class, "STARTDATE", true));
        //logger.debug("FINISHDATE = " + parseAnyDate(contract.get("FINISHDATE"), String.class, "FINISHDATE", true));
        // список типов объектов - выбор (если уже существует в договоре) или создание нового
        Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        if (insObjGroupListFromContract == null) {
            contract.put("INSOBJGROUPLIST", new ArrayList<Map<String, Object>>());
        }
        // получение сведений о продукте (по идентификатору конфигурации продукта)
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODCONFID", prodConfID);
        productParams.put("HIERARCHY", false);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);

        // определение идентификатора справочника расширенных атрибутов договора на основании сведений о продукте
        Object contrExtMapHBDataVerID = product.get("HBDATAVERID");
        contractExtValues.put("HBDATAVERID", contrExtMapHBDataVerID);
        if (contract.get("PRODPROGID") != null) {
            Long prodProgId = getLongParam(contract.get("PRODPROGID"));
            Map<String, Object> progParams = new HashMap<String, Object>();
            progParams.put(RETURN_AS_HASH_MAP, "TRUE");
            progParams.put("PRODPROGID", prodProgId);
            Map<String, Object> progRes = this.callService(Constants.B2BPOSWS, "dsB2BProductProgramBrowseListByParam", progParams, login, password);
            contractExtValues.put("insuranceProgram", progRes.get("PROGCODE"));
        }

        // определение кода программы
        String programCode = getStringParam(contractExtValues.get("insuranceProgram"));
        if (programCode.isEmpty()) {
            programCode = "00002";
            contractExtValues.put("insuranceProgram", programCode);
        }

        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор (после регистрации продукта в БД)
        updateContractInsuranceProductStructure(contract, product, true, getStringParam(contractExtValues.get("insuranceProgram")), login, password);

        Map<String, Object> prodver = (Map<String, Object>) product.get("PRODVER");
        if (prodver != null) {
            List<Map<String, Object>> sectionList = (List<Map<String, Object>>) contract.get("CONTRSECTIONLIST");
            if ((sectionList != null) && (sectionList.size() > 0)) {
                // считаем суммы и премии.
                Map<String, Object> callParams = new HashMap<String, Object>();
                callParams.put("CONFIGNAME", "multiSetelemProdConfID");
                callParams.put("HANBOOKLIST", "'B2B.MultiSetelem.Prog.Sums'");
                Map<String, Object> resParams = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BLoadHandBookByProduct", callParams, login, password);
                if (resParams.get(RESULT) != null) {
                    List<Map<String, Object>> calcHB = (List<Map<String, Object>>) ((Map<String, Object>) resParams.get(RESULT)).get("B2B.MultiSetelem.Prog.Sums");
                    Map<String, Object> selectedHBProgram = null;
                    for (Map<String, Object> hb : calcHB) {
                        if ((hb.get("insuranceProgram") != null) && (hb.get("insuranceProgram").toString().equals(programCode))) {
                            selectedHBProgram = hb;
                        }
                    }
                    Double cInsAmValue = 0D;
                    Double cPremValue = 0D;
                    if (selectedHBProgram != null) {
                        for (Map<String, Object> sectionBean : sectionList) {
                            if (sectionBean.get("INSOBJGROUPLIST") != null) {
                                Double sectionInsAmValue = 0D;
                                Double sectionPremValue = 0D;
                                List<Map<String, Object>> sectionInsObjGroupList = (List<Map<String, Object>>) sectionBean.get("INSOBJGROUPLIST");
                                for (Map<String, Object> insObjGroup : sectionInsObjGroupList) {
                                    if (insObjGroup.get("OBJLIST") != null) {
                                        // отдельное сохранение для мультиполиса адреса страхуемого имущества
                                        if ((insObjGroup.get("INSOBJGROUPSYSNAME") != null) && (insObjGroup.get("INSOBJGROUPSYSNAME").equals("mS_prop"))) {
                                            propertyAddressSave(insObjGroup, login, password);
                                        }

                                        Double groupInsAmValue = 0.0;
                                        Double groupPremValue = 0.0;
                                        List<Map<String, Object>> childObjList = (List<Map<String, Object>>) insObjGroup.get("OBJLIST");
                                        for (Map<String, Object> obj : childObjList) {
                                            if (obj.get("CONTROBJMAP") != null) {
                                                Double objInsAmValue = 0D;
                                                Double objPremValue = 0D;
                                                Map<String, Object> childContrObjMap = (Map<String, Object>) obj.get("CONTROBJMAP");
                                                if (childContrObjMap.get("CONTRRISKLIST") != null) {
                                                    List<Map<String, Object>> childRiskList = (List<Map<String, Object>>) childContrObjMap.get("CONTRRISKLIST");
                                                    for (Map<String, Object> risk : childRiskList) {
                                                        if (risk.get("PRODRISKSYSNAME") != null) {
                                                            Double riskInsAmValue = 0D;
                                                            Double riskPremValue = 0D;
                                                            String rName = risk.get("PRODRISKSYSNAME").toString();
                                                            String sPrefix = rName;
                                                            if (sPrefix.equals("mS_VZR_med_R")) {
                                                                Double rate = getCalcRateRule(getDateParam(docDate), 3L, product);
                                                                // раньше rate был константой 75руб. сейчас хранится в prodCalcRateRule
                                                                riskInsAmValue += rate * getDoubleParam(selectedHBProgram.get("INSAM_" + sPrefix));
                                                                riskPremValue += rate * getDoubleParam(selectedHBProgram.get("PREM_" + sPrefix));
                                                            } else {
                                                                riskInsAmValue += getDoubleParam(selectedHBProgram.get("INSAM_" + sPrefix));
                                                                riskPremValue += getDoubleParam(selectedHBProgram.get("PREM_" + sPrefix));
                                                            }
                                                            objInsAmValue += riskInsAmValue;
                                                            objPremValue += riskPremValue;
                                                            risk.put("INSAMVALUE", riskInsAmValue);
                                                            risk.put("PREMVALUE", riskInsAmValue);
                                                        }
                                                    }
                                                }
                                                obj.put("INSAMVALUE", objInsAmValue);
                                                obj.put("PREMVALUE", objPremValue);
                                                groupInsAmValue += objInsAmValue;
                                                groupPremValue += objPremValue;
                                            }
                                        }
                                        insObjGroup.put("INSAMVALUE", groupInsAmValue);
                                        insObjGroup.put("PREMVALUE", groupPremValue);
                                        sectionInsAmValue += groupInsAmValue;
                                        sectionPremValue += groupPremValue;
                                    }
                                }
                                cInsAmValue += sectionInsAmValue;
                                cPremValue += sectionPremValue;
                                sectionBean.put("INSAMVALUE", sectionInsAmValue);
                                sectionBean.put("PREMVALUE", sectionPremValue);
                                sectionBean.put("INSAMCURRENCYID", 1L);
                                sectionBean.put("PREMCURRENCYID", 1L);
                            }
                        }
                    }
                    contract.put("INSAMVALUE", cInsAmValue);
                    contract.put("PREMVALUE", cPremValue);
                }
            }
        }
        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        Object currentRowStatus = contract.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
            contract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
        }

        return contract;
    }

    /**
     * Метод для подготовки данных перед сохранением договора для продукта
     * "Мультиполис Сетелем"
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMultiSetelemContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BMultiSetelemContractPrepareToSave start");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        boolean isDataValid = validateSaveParams(contract);
        Map<String, Object> result;
        if (isDataValid) {
            genAdditionalSaveParams(contract, login, password);
            result = contract;
        } else {
            result = contract;
        }
        logger.debug("dsB2BMultiSetelemContractPrepareToSave finish");
        return result;
    }

    // генерация строковых представлений для всех сумм
    protected void genDateStrs(Map<String, Object> data, String dataNodePath) {
        Map<String, Object> parsedMap = new HashMap<String, Object>();
        parsedMap.putAll(data);
        for (Map.Entry<String, Object> entry : parsedMap.entrySet()) {
            String keyName = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String dataValuePath = dataNodePath + "." + keyName;
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    genDateStrs(map, dataValuePath);
                } else if (value instanceof List) {
                    ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) value;
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, Object> element = list.get(i);
                        genDateStrs(element, dataValuePath + "[" + i + "]");
                    }
                } else if (value instanceof Date) {
                    try {
                        Date cDate = (Date) value;
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        String reportDate = df.format(cDate);
                        data.put(keyName + "", reportDate);
                        logger.debug(dataNodePath + "." + keyName + "STR = " + reportDate);
                    } catch (NumberFormatException ex) {
                        logger.debug(dataValuePath + " - не сумма.");
                    } catch (IllegalArgumentException ex) {
                        logger.debug(dataValuePath + " не удалось преобразовать в строковое представление суммы.");
                    }
                }
            }
        }
    }

    protected void resolveHandBook(Map<String, Object> data, Long calcVerId, String hbName, String hbKeyFieldName, String hbTextFieldName, String storeFieldName, String login, String password) throws Exception {
        if (data.get(storeFieldName) != null) {
            Map<String, Object> qParams = new HashMap<String, Object>();
            qParams.put(RETURN_AS_HASH_MAP, true);
            qParams.put("CALCVERID", calcVerId);
            qParams.put("NAME", hbName);
            Map<String, Object> cParams = new HashMap<String, Object>();
            cParams.put(hbKeyFieldName, data.get(storeFieldName));
            qParams.put("PARAMS", cParams);
            Map<String, Object> qRes = this.callService(INSTARIFICATORWS, "dsGetCalculatorHandbookData", qParams, login, password);
            if (qRes.get(hbTextFieldName) != null) {
                data.put(storeFieldName.replace("Hid", "STR"), qRes.get(hbTextFieldName));
            }
        }
    }

    /**
     * Метод для подготовки данных для отчета по продукту Страховое ателье.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID"})
    public Map<String, Object> dsB2BMultiSetelemPrintDocDataProvider(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        // загрузка данных договора базовой версией поставщика
        params.put(RETURN_AS_HASH_MAP, true);
        params.put("LOADCONTRSECTION", 1L);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", params, login, password);
        result.put(RETURN_AS_HASH_MAP, true);
        if ((result.get("STATESYSNAME") != null) && (result.get("STATESYSNAME").toString().equalsIgnoreCase("B2B_CONTRACT_SG"))) {
            if (result.get("SIGNDATE") == null) {
                Date now = new Date();
                result.put("SIGNDATE", now);
                formatDateFromMap(result);
                Map<String, Object> csParams = new HashMap<String, Object>();
                csParams.put("CONTRID", result.get("CONTRID"));
                csParams.put("SIGNDATE", now);
                this.callExternalService(B2BPOSWS_SERVICE_NAME, "dsB2BContractUpdate", csParams, login, password);

            }
        } else {
            result.put("SIGNDATE", null);
        }

        Map<String, Object> callHbParams = new HashMap<String, Object>();
        callHbParams.put("CONFIGNAME", "multiSetelemProdConfID");
        callHbParams.put("HANBOOKLIST", "'B2B.MultiSetelem.Prog.Sums'");
        Map<String, Object> resParams = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BLoadHandBookByProduct", callHbParams, login, password);
        if (resParams.get(RESULT) != null) {
            Map<String, Object> resList = (Map<String, Object>) resParams.get(RESULT);            
            List<Map<String, Object>> sumList = (List<Map<String, Object>>) resList.get("B2B.MultiSetelem.Prog.Sums");
            result.put("SUMLIST", sumList);            
        }
        Map<String, Object> prodMap = (Map<String, Object>) result.get("PRODUCTMAP");
        if (prodMap.get("PRODCALCRATERULES") != null) {
                List<Map<String, Object>> prodRateRuleMap;
                prodRateRuleMap = (List<Map<String, Object>>) prodMap.get("PRODCALCRATERULES");
                result.put("VZRRATEVALUE", prodRateRuleMap.get(0).get("RATEVALUE"));
                result.put("VZRRATECURRENCYID", 1);
        }
        
        
        result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractTextSums", result, login, password);
        if (result.get("MEMBERLIST") != null) {
            List<Object> members = (List<Object>) result.get("MEMBERLIST");
            Object member = members.get(0);
            members.clear();
            members.add(member);
        }
        genDateStrs(result, "*");
        if ((result.get("INSREGIONCODE") != null) && (!result.get("INSREGIONCODE").toString().isEmpty()));
        Map<String, Object> callParams = new HashMap<String, Object>();
        callParams.put(RETURN_AS_HASH_MAP, "TRUE");
        callParams.put("CODE", result.get("INSREGIONCODE"));
        Map<String, Object> regItem = this.callExternalService(B2BPOSWS_SERVICE_NAME, "dsKladrRegionBrowseListByParam", callParams, login, password);
        if (regItem != null) {
            result.put("INSREGIONNAME", regItem.get("NAME"));
        }
        logger.debug(result.toString());
        return result;
    }

    private void propertyAddressSave(Map<String, Object> propertyAddressObjGroup, String login, String password) throws Exception {
        if (propertyAddressObjGroup != null) {
            Map<String, Object> propertyAddress = new HashMap<String, Object>(propertyAddressObjGroup);
            logger.debug("propertyAddress: " + propertyAddress);
            Map<String, Object> addressParams = new HashMap<String, Object>();
            addressParams.putAll(propertyAddress);
            addressParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> fullAddressTexts = this.callService(B2BPOSWS_SERVICE_NAME, "dsGenFullTextAddress", addressParams, login, password);
            logger.debug("fullAddressTexts: " + fullAddressTexts);
            if (fullAddressTexts != null) {
                propertyAddressObjGroup.put("ADDRESSTEXT1", fullAddressTexts.get("ADDRESSTEXT1"));
            }
        }
    }
}
