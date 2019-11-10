/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.gap;

import static com.bivgroup.services.b2bposws.facade.pos.contract.custom.B2BContractCustomFacade.getLastElementByAtrrValue;
import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import org.apache.log4j.Logger;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 *
 * @author averichevsm
 */
@BOName("GAPCustom")
public class GAPCustomFacade extends ProductContractCustomFacade {

    private final Logger logger = Logger.getLogger(GAPCustomFacade.class);
    
    private static final String DEFAULT_AND_ONLY_PROGRAM_CODE_GAP = "000001";
    
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;   
    
    // "... кБазовый – базовый тариф страхового риска GAP, всегда равен 1,6% ..."
    // используется в calcGAPPremValue
    // todo: заменить calcGAPPremValue на вызов калькулятора, когда он будет зарегистрирован в БД
    private static final double K_BASE_TARIF_RISK_GAP = 0.016;

    /**
     * Метод для загрузки справочников по продукту GAP (может потребоваться для angular-интерфейса).
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsGAPHBListLoad(Map<String, Object> params) throws Exception {
        logger.debug("before dsGAPHBListLoad");
        //Map<String, Object> hbMapIn = (Map<String, Object>) params.get("HBMAP");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        //loadOPFList(result, hbMapIn, login, password);

        // todo: загрузка справочников по продукту GAP требующихся для angular-интерфейса
        // loadHandbookList(result, null, "GAP*", CALCVERID_GAP, "GAP*List", login, password);
        logger.debug("after dsGAPHBListLoad");
        return result;
    }

    /**
     * Метод для подготовки данных для отчета по продукту GAP.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID"})
    public Map<String, Object> dsB2BGAPPrintDocDataProvider(Map<String, Object> params) throws Exception {

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        
        // загрузка данных договора базовой версией поставщика
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBasePrintDocDataProvider", params, login, password);
        
        // todo: генерация строковых представлений для сумм
        result.put(RETURN_AS_HASH_MAP, true);
        result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractTextSums", result, login, password);
        
        // todo: генерация дополнительных значений (после предоставления шаблонов печатного документа)

        return result;
    }

    // todo: заменить на вызов калькулятора, когда он будет зарегистрирован в БД
    public static Double calcGAPPremValue(Double insAmValue, Integer durationYears) {
        // "Страховая премии определяется согласно формуле:
        //      Стоимость * кБазовый * n 
        // где, 
        //      * Стоимость – стоимость по полису КАСКО
        //      * кБазовый – базовый тариф страхового риска GAP, всегда равен 1,6%
        //      * n – количество лет страхования"
        return insAmValue * K_BASE_TARIF_RISK_GAP * durationYears;
    }

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
                
                if (checkIsValueInvalidByRegExp(insObjGroup.get("VIN"), expLatinNum, true)) {
                    isDataInvalid = true;
                    errorText = errorText + "VIN должен содержать только латиницу или цифры. ";
                }
                if (checkIsValueInvalidByRegExp(insObjGroup.get("bodyNumber"), expLatinNum, true)) {
                    isDataInvalid = true;
                    errorText = errorText + "Номер кузова должен содержать только латиницу и цифры. ";
                }
                if (checkIsValueInvalidByRegExp(insObjGroup.get("markModel"), expLatinNumRusSpaceDash, true)) {
                    isDataInvalid = true;
                    errorText = errorText + "Марка-модель должна содержать только кириллицу, латиницу, цифры, тире и пробелы. ";
                }
                Object productionYearObj = insObjGroup.get("productionYear");
                if (checkIsValueInvalidByRegExp(productionYearObj, expYear, true)) {
                    isDataInvalid = true;
                    errorText = errorText + "Год выпуска должен содержать четыре цифры. ";
                } else {
                    Integer productionYear = getIntegerParam(productionYearObj);
                    if (productionYear != 0) {                        
                        Object docDateObj = contract.get("DOCUMENTDATE");
                        if (docDateObj != null) {
                            Date docDate = (Date) parseAnyDate(docDateObj, Date.class, "DOCUMENTDATE");
                            GregorianCalendar docDateGC = new GregorianCalendar();
                            int docYear = docDateGC.get(GregorianCalendar.YEAR);
                            if (productionYear.intValue() > docYear) {
                                isDataInvalid = true;
                                errorText = errorText + "Год выпуска не должен быть более текущего года. ";
                            }
                            if ((docYear - productionYear.intValue()) > 3) {
                                isDataInvalid = true;
                                errorText = errorText + "Год выпуска не должен быть меньше трех лет с текущего года. ";
                            }
                        }
                    }
                }
            }
        }
        if (isDataInvalid) {
            errorText = errorText + "Сведения договора не сохранены.";
            contract.put("Status", "Error");
            contract.put("Error", errorText);
        }
        return !isDataInvalid;
    }    
    
    protected Map<String, Object> genAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {
        
        boolean isParamsChangingLogged = logger.isDebugEnabled();
        
        // идентификатор версии продукта всегда передается в явном виде с интерфейса
        Long prodVerID = getLongParam(contract.get("PRODVERID"));

        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            //contract.put("PRODCONFID", prodConfID);
            setGeneratedParam(contract, "PRODCONFID", prodConfID, isParamsChangingLogged);
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
            //contract.put("DOCUMENTDATE", documentDateGC.getTime());
            setGeneratedParam(contract, "DOCUMENTDATE", documentDateGC.getTime(), isParamsChangingLogged);
        } else {
            documentDateGC.setTime((Date) parseAnyDate(docDate, Date.class, "DOCUMENTDATE"));
        }

        // безусловное вычисление даты начала действия
        GregorianCalendar startDateGC = new GregorianCalendar();
        startDateGC.setTime(documentDateGC.getTime());
        startDateGC.add(Calendar.DATE, 1);
        //contract.put("STARTDATE", startDateGC.getTime());
        setOverridedParam(contract, "STARTDATE", startDateGC.getTime(), isParamsChangingLogged);
        
        
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
        //contract.put("FINISHDATE", finishDateGC.getTime());
        setOverridedParam(contract, "FINISHDATE", finishDateGC.getTime(), isParamsChangingLogged);
        
        // безусловное вычисление срока действия договора в днях
        long startDateInMillis = startDateGC.getTimeInMillis();
        long finishDateInMillis = finishDateGC.getTimeInMillis();
        // в сутках (24*60*60*1000) милисекунд
        long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000));
        //contract.put("DURATION", duration);
        setOverridedParam(contract, "DURATION", duration, isParamsChangingLogged);

        //logger.debug("DOCUMENTDATE = " + parseAnyDate(contract.get("DOCUMENTDATE"), String.class, "DOCUMENTDATE", true));
        //logger.debug("STARTDATE = " + parseAnyDate(contract.get("STARTDATE"), String.class, "STARTDATE", true));
        //logger.debug("FINISHDATE = " + parseAnyDate(contract.get("FINISHDATE"), String.class, "FINISHDATE", true));
        
        // список типов объектов - выбор (если уже существует в договоре) или создание нового
        Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        List<Map<String, Object>> insObjGroupList;
        if (insObjGroupListFromContract != null) {
            insObjGroupList = (List<Map<String, Object>>) insObjGroupListFromContract;
        } else {
            insObjGroupList = new ArrayList<Map<String, Object>>();
            //insObjGroupList.add(new HashMap<String, Object>());
            contract.put("INSOBJGROUPLIST", insObjGroupList);
        }

        // определение страховой суммы по данным о стоимости по КАСКО из показателя типа объекта
        //Double insAmValue = getDoubleParam(contract.get("INSAMVALUE"));
        //if ((insAmValue == 0.0) && (insObjGroupList.size() == 1)) {
        //    insAmValue = getDoubleParam(insObjGroupList.get(0).get("kaskoCost"));
        //    contract.put("INSAMVALUE", insAmValue);
        //}
        
        // безусловное определение страховой суммы по данным о стоимости по КАСКО из показателя типа объекта
        Double insAmValue = 0.0;
        Map<String, Object> singleInsObjGroup = null;
        if (insObjGroupList.size() == 1) {
            singleInsObjGroup = insObjGroupList.get(0);
            insAmValue = getDoubleParam(singleInsObjGroup.get("kaskoCost"));
            //contract.put("INSAMVALUE", insAmValue);
            setOverridedParam(contract, "INSAMVALUE", insAmValue, isParamsChangingLogged);
        }
        
        // безусловное вычисление страховой премии
        Double premValue = calcGAPPremValue(insAmValue, durationYears);
        //contract.put("PREMVALUE", premValue);
        setOverridedParam(contract, "PREMVALUE", premValue, isParamsChangingLogged);
        
        // копирование безусловно перевычисленных сумм в объект и риск (при условии, что на договор приходиться только один объект и один риск)
        if (singleInsObjGroup != null) {
            ArrayList<Map<String, Object>> objList = (ArrayList<Map<String, Object>>) singleInsObjGroup.get("OBJLIST");
            if ((objList != null) && (objList.size() == 1)) {
                Map<String, Object> singleObj = objList.get(0);
                Map<String, Object> singleContrObjMap = (Map<String, Object>) singleObj.get("CONTROBJMAP");
                singleContrObjMap.put("INSAMVALUE", insAmValue);
                singleContrObjMap.put("PREMVALUE", premValue);                
                ArrayList<Map<String, Object>> contrRiskList = (ArrayList<Map<String, Object>>) singleContrObjMap.get("CONTRRISKLIST");
                if ((contrRiskList != null) && (contrRiskList.size() == 1)) {
                    Map<String, Object> singleContrRisk = contrRiskList.get(0);
                    singleContrRisk.put("INSAMVALUE", insAmValue);
                    singleContrRisk.put("PREMVALUE", premValue);
                }
            }
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
        
        // определение кода программы
        String programCode = getStringParam(contractExtValues.get("insuranceProgram"));
        if (programCode.isEmpty()) {
            programCode = DEFAULT_AND_ONLY_PROGRAM_CODE_GAP;
            contractExtValues.put("insuranceProgram", programCode);
            setGeneratedParam(contractExtValues, "insuranceProgram", programCode, isParamsChangingLogged);
        }
        
        // определение идентификатора программы по её коду на основании сведений о продукте
        Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
        ArrayList<Map<String, Object>> prodProgs = (ArrayList<Map<String, Object>>) prodVer.get("PRODPROGS");
        Map<String, Object> program = (Map<String, Object>) getLastElementByAtrrValue(prodProgs, "PROGCODE", programCode);
        Long programID = getLongParam(program.get("PRODPROGID"));
        contract.put("PRODPROGID", programID);
        setOverridedParam(contract, "PRODPROGID", programID, isParamsChangingLogged);
        
        // todo: генерация дополнительных параметров (после регистрации продукта в БД)
        
        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор (после регистрации продукта в БД)
        updateContractInsuranceProductStructure(contract, product, false, getStringParam(contractExtValues.get("insuranceProgram")), login, password);
        
        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        Object currentRowStatus = contract.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
            contract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
        }

        return contract;
    }

    /**
     * Метод для сохранения договора по продукту GAP.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BGAPContractPrepareToSave(Map<String, Object> params) throws Exception {
        
        logger.debug("before dsB2BGAPContractPrepareToSave");

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

        logger.debug("after dsB2BGAPContractPrepareToSave");

        return result;
    }

    /**
     * Метод для загрузки договора по продукту GAP.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BGAPContrLoad(Map<String, Object> params) throws Exception {
        
        logger.debug("before dsB2BGAPContrLoad");

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        
        Map<String, Object> loadResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", params, login, password);

        logger.debug("after dsB2BGAPContrLoad");
        
        return loadResult;
        
    }

}
