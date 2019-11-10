/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.multi;

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
@BOName("MultiCustom")
public class MultiCustomFacade extends ProductContractCustomFacade {

    private final Logger logger = Logger.getLogger(MultiCustomFacade.class);

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    private static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;

    /**
     * Метод для загрузки справочников по продукту мультиполис (может
     * потребоваться для angular-интерфейса).
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMultiHBListLoad(Map<String, Object> params) throws Exception {
        logger.debug("before dsMultiHBListLoad");
        //Map<String, Object> hbMapIn = (Map<String, Object>) params.get("HBMAP");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        //loadOPFList(result, hbMapIn, login, password);
        // todo: загрузка справочников по продукту мультиполис требующихся для angular-интерфейса
        // loadHandbookList(result, null, "MULTI*", CALCVERID_MULTI, "MULTI*List", login, password);
        logger.debug("after dsMultiHBListLoad");
        return result;
    }

    /**
     * Метод для подготовки данных для отчета по продукту мультиполис.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID"})
    public Map<String, Object> dsB2BMultiPrintDocDataProvider(Map<String, Object> params) throws Exception {

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        // загрузка данных договора базовой версией поставщика
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBasePrintDocDataProvider", params, login, password);

        // todo: генерация строковых представлений для сумм
        result.put(RETURN_AS_HASH_MAP, true);
        result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractTextSums", result, login, password);

        // отдельная загрузка адреса страхуемого имущества.
        if (result.get("INSOBJGROUPLIST") != null) {
            List<Map<String, Object>> childInsObjGroupList = (List<Map<String, Object>>) result.get("INSOBJGROUPLIST");
            for (Map<String, Object> insObjGroup : childInsObjGroupList) {
                if (insObjGroup.get("OBJLIST") != null) {
                    // отдельное сохранение для мультиполиса адреса страхуемого имущества
                    if ((insObjGroup.get("INSOBJGROUPSYSNAME") != null) && (insObjGroup.get("INSOBJGROUPSYSNAME").equals("property"))) {
                        Long addressID = getLongParam(insObjGroup.get("propertyAddress"));
                        logger.debug("addressID: " + addressID);
                        if (addressID != null) {
                            //Long isPropertyRegisterAddress = getLongParam(propertyAddressObjGroup.get("isPropertyRegisterAddress"));
                            Map<String, Object> addressParams = new HashMap<String, Object>();
                            addressParams.put("ADDRESSID", addressID);
                            addressParams.put(RETURN_AS_HASH_MAP, true);
                            Map<String, Object> propertyAddress = null;
                            try {
                                propertyAddress = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAddressBrowseListByParam", addressParams, login, password);
                            } catch (Exception ex) {
                                logger.debug("Произошло исключение при вызове метода 'dsB2BAddressBrowseListByParam'. Операция с адресом имущества не выполнена.");
                            }
                            if (propertyAddress != null) {
                                propertyAddress.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                                insObjGroup.put("propertyAddress", propertyAddress);
                            }
                            logger.debug("propertyAddress: " + propertyAddress);
                        }
                    }
                }
            }
        }

        // todo: генерация дополнительных значений (после предоставления шаблонов печатного документа)
        //logger.debug("dsB2BMultiPrintDocDataProvider result:\n\n" + result + "\n");
        return result;
    }

    //private boolean checkIsValueValidByRegExp(Object checked, String regExp, boolean allowNull) {
    //    boolean result;
    //    if (checked == null) {
    //        result = allowNull;
    //    } else {
    //        Pattern pattern = Pattern.compile(regExp);
    //        String checkedString = getStringParam(checked);
    //        Matcher matcher = pattern.matcher(checkedString);
    //        result = matcher.matches();
    //        //logger.debug("Проверка значения '" + checkedString + "' по регулярному выражению '" + regExp + "' завершена с результатом '" + result + "'.");
    //    }
    //    return result;
    //}
    //private boolean checkIsValueInvalidByRegExp(Object checked, String regExp, boolean allowNull) {
    //    return !checkIsValueValidByRegExp(checked, regExp, allowNull);
    //}
    private boolean validateSaveParams(Map<String, Object> contract) {

        boolean isDataInvalid = false;

        String errorText = "";
        //Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        //List<Map<String, Object>> insObjGroupList;
        //if (insObjGroupListFromContract != null) {
        //    insObjGroupList = (List<Map<String, Object>>) insObjGroupListFromContract;
        //    if (insObjGroupList.size() == 1) {
        //        Map<String, Object> insObjGroup = insObjGroupList.get(0);
        //
        //        String expLatinNum = "^[A-Za-z0-9]+";
        //        String expLatinNumRusSpaceDash = "^[A-Za-z0-9А-Яа-яЁё\\s\\-]+";
        //        String expYear = "^[0-9]{4}";
        //
        //        if (checkIsValueInvalidByRegExp(insObjGroup.get("VIN"), expLatinNum, true)) {
        //            isDataInvalid = true;
        //            errorText = errorText + "VIN должен содержать только латиницу или цифры. ";
        //        }
        //        if (checkIsValueInvalidByRegExp(insObjGroup.get("bodyNumber"), expLatinNum, true)) {
        //            isDataInvalid = true;
        //            errorText = errorText + "Номер кузова должен содержать только латиницу и цифры. ";
        //        }
        //        if (checkIsValueInvalidByRegExp(insObjGroup.get("markModel"), expLatinNumRusSpaceDash, true)) {
        //            isDataInvalid = true;
        //            errorText = errorText + "Марка-модель должна содержать только кириллицу, латиницу, цифры, тире и пробелы. ";
        //        }
        //        Object productionYearObj = insObjGroup.get("productionYear");
        //        if (checkIsValueInvalidByRegExp(productionYearObj, expYear, true)) {
        //            isDataInvalid = true;
        //            errorText = errorText + "Год выпуска должен содержать четыре цифры. ";
        //        } else {
        //            Integer productionYear = getIntegerParam(productionYearObj);
        //            if (productionYear != 0) {
        //                Object docDateObj = contract.get("DOCUMENTDATE");
        //                if (docDateObj != null) {
        //                    Date docDate = (Date) parseAnyDate(docDateObj, Date.class, "DOCUMENTDATE");
        //                    GregorianCalendar docDateGC = new GregorianCalendar();
        //                    int docYear = docDateGC.get(GregorianCalendar.YEAR);
        //                    if (productionYear.intValue() > docYear) {
        //                        isDataInvalid = true;
        //                        errorText = errorText + "Год выпуска не должен быть более текущего года. ";
        //                    }
        //                    if ((docYear - productionYear.intValue()) > 3) {
        //                        isDataInvalid = true;
        //                        errorText = errorText + "Год выпуска не должен быть меньше трех лет с текущего года. ";
        //                    }
        //                }
        //            }
        //        }
        //    }
        //}
        if (isDataInvalid) {
            errorText = errorText + "Сведения договора не сохранены.";
            contract.put("Status", "Error");
            contract.put("Error", errorText);
        }
        return !isDataInvalid;
    }

    // добавляет к списку программ калькулятора программу из конкретного элемента структуры страхового продукта договора
    private void addCalcProgram(List<Map<String, Object>> calcProgramList, Map<String, Object> insStruct, String sysNameKey) {
        String insStructProgram = getStringParam(insStruct.get("insuranceProgram"));
        if (!insStructProgram.isEmpty()) {
            Map<String, Object> newCalcProgram = new HashMap<String, Object>();
            newCalcProgram.put("sysName", insStruct.get(sysNameKey));
            newCalcProgram.put("insuranceProgram", insStructProgram);
            calcProgramList.add(newCalcProgram);
        }
    }

    // формирует списку программ для калькулятора по сведениям из структуры страхового продукта договора
    private List<Map<String, Object>> getCalcProgramListFromInsObjGroupList(List<Map<String, Object>> insObjGroupList) {
        List<Map<String, Object>> calcProgramList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> insObjGroup : insObjGroupList) {
            addCalcProgram(calcProgramList, insObjGroup, "INSOBJGROUPSYSNAME");
            ArrayList<Map<String, Object>> insObjList = (ArrayList<Map<String, Object>>) insObjGroup.get("OBJLIST");
            if (insObjList != null) {
                for (Map<String, Object> insObj : insObjList) {
                    Map<String, Object> insObjMap = (Map<String, Object>) insObj.get("INSOBJMAP");
                    if (insObjMap != null) {
                        addCalcProgram(calcProgramList, insObjMap, "INSOBJSYSNAME");
                    }
                }
            }
        }
        return calcProgramList;
    }

    // обновляет суммы в структуре страхового продукта договора по результатам полученным из калькулятора
    private String updateInsuranceSumsFromCalcResult(List<Map<String, Object>> insObjGroupList, List<Map<String, Object>> calcResultList) {

        String errorText = "";

        //logger.debug("insObjGroupList:\n\n" + insObjGroupList + "\n");
        // подгтовка мапы с обновляемыми объектами (ключ - системное имя)
        Map<String, Map<String, Object>> targetStructsMap = new HashMap<String, Map<String, Object>>();
        for (Map<String, Object> insObjGroup : insObjGroupList) {
            //targetStructsMap.put(getStringParam(insObjGroup.get("INSOBJGROUPSYSNAME")), insObjGroup);
            ArrayList<Map<String, Object>> insObjList = (ArrayList<Map<String, Object>>) insObjGroup.get("OBJLIST");
            if (insObjList != null) {
                for (Map<String, Object> insObj : insObjList) {
                    Map<String, Object> insObjMap = (Map<String, Object>) insObj.get("INSOBJMAP");
                    Map<String, Object> contrObjMap = (Map<String, Object>) insObj.get("CONTROBJMAP");
                    if ((insObjMap != null) && (contrObjMap != null)) {
                        targetStructsMap.put(getStringParam(insObjGroup.get("INSOBJGROUPSYSNAME")), contrObjMap);
                        targetStructsMap.put(getStringParam(insObjMap.get("INSOBJSYSNAME")), contrObjMap);
                    }
                }
            }
        }

        //logger.debug("targetStructsMap:\n\n" + targetStructsMap + "\n");
        // обновение сумм в объектах (по системному имени, сумма - из результатов калькуляции) и в рисках (копирование сумм в единственный риск каждого обновленного объекта)
        logger.debug("Updating insurance premium sums values in objects and risks...");
        for (Map<String, Object> calcResultElement : calcResultList) {
            // получение системного имени обновляемого объекта и страховых сумм из результатов калькуляции
            String sysName = getStringParam(calcResultElement.get("sysName"));
            logger.debug(String.format("Checking insurance structure element with system name '%s'...", sysName));
            Double insAmValue = roundSum(getDoubleParam(calcResultElement.get("insAmValue")));
            Double premValue = roundSum(getDoubleParam(calcResultElement.get("premValue")));
            // получение обновляемого объекта (по системному имени)
            Map<String, Object> targetStruct = targetStructsMap.get(sysName);
            if (targetStruct != null) {
                // обновление сумм в объекте
                logger.debug("Setting insurance sums for this insurance structure element...");
                logger.debug("Insurance sum value (INSAMVALUE): " + insAmValue);
                targetStruct.put("INSAMVALUE", insAmValue);
                logger.debug("Premium sum value (PREMVALUE): " + premValue);
                targetStruct.put("PREMVALUE", premValue);
                // если объект уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были изменены безусловно)
                markAsModified(targetStruct);
                // получение обновляемого риска (если он у объекта единственный)
                Object riskListObj = targetStruct.get("CONTRRISKLIST");
                if (riskListObj != null) {
                    List<Map<String, Object>> riskList = (List<Map<String, Object>>) riskListObj;
                    logger.debug(String.format("Risk list for insurance object with system name (INSOBJSYSNAME/INSOBJGROUPSYSNAME) '%s': ", sysName) + riskList);
                    if (riskList.size() == 1) {
                        Map<String, Object> singleRisk = riskList.get(0);
                        if (singleRisk != null) {
                            // обновление сумм в риске
                            String riskSysName = getStringParam(singleRisk.get("PRODRISKSYSNAME"));
                            logger.debug(String.format("Coping insurance sums from this insurance structure element to it single child risk with system name (PRODRISKSYSNAME) '%s'...", riskSysName));
                            logger.debug("Insurance sum value (INSAMVALUE): " + insAmValue);
                            singleRisk.put("INSAMVALUE", insAmValue);
                            logger.debug("Premium sum value (PREMVALUE): " + premValue);
                            singleRisk.put("PREMVALUE", premValue);
                            // если риск уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были изменены безусловно)
                            markAsModified(singleRisk);

                        } else {
                            // ошибка - единственный риск отсутствует
                            errorText = String.format("Insurance object with system name '%s' do not contains risk in risks list - insurance premium sum value will not be coped to risks of this object!", sysName);
                            //logger.error(errorStr);
                        }
                    } else {
                        // ошибка - у объекта нет рисков или их больше одного
                        errorText = String.format("Insurance object with system name '%s' do not contains one single risk - insurance premium sum value will not be coped to risks of this object!", sysName);
                        //logger.error(errorStr);
                    }
                    if (!errorText.isEmpty()) {
                        logger.error(errorText);
                        return errorText;
                    }
                }
            } else {
                logger.debug("No sums found in calculation result for this insurance structure element.");
            }
        }
        //logger.debug("insObjGroupList:\n\n" + insObjGroupList + "\n");

        logger.debug("Updating insurance premium sums values in objects and risks finished.\n");

        return errorText;

    }

    // помечает сущность как изменившуюся
    // todo: возможно, перенести в Base-фасад?
//    private Boolean markAsModified(Map<String, Object> targetStruct) {
//        Boolean isMarkedAsModified = false;
//        Object currentRowStatus = targetStruct.get(ROWSTATUS_PARAM_NAME);
//        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
//            targetStruct.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
//            isMarkedAsModified = true;
//        }
//        return isMarkedAsModified;
//    }

    //!только для отладки! - начало
    /**
     * Returns a pseudo-random number between min and max, inclusive. The
     * difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value. Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    //public static int randInt(int min, int max) {
    //    // NOTE: This will (intentionally) not run as written so that folks
    //    // copy-pasting have to think about how to initialize their
    //    // Random instance.  Initialization of the Random instance is outside
    //    // the main scope of the question, but some decent options are to have
    //    // a field that is initialized once and then re-used as needed or to
    //    // use ThreadLocalRandom (if using at least Java 1.7).
    //    Random rand = new Random();
    //    // nextInt is normally exclusive of the top value,
    //    // so add 1 to make it inclusive
    //    int randomNum = rand.nextInt((max - min) + 1) + min;
    //    return randomNum;
    //}
    //!только для отладки! - конец
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

        contract.put("PREMCURRENCYID", 1);
        // contract.put("INSAMCURRENCYID", 1);
        // безусловное вычисление даты начала действия


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
            contract.put("TERMID", durationYears);
        }
        //
        GregorianCalendar startDateGC = new GregorianCalendar();
        Object startDate = contract.get("STARTDATE");
        if (startDate != null) {
            startDateGC.setTime((Date) parseAnyDate(startDate, Date.class, "STARTDATE"));
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
            if (contract.get("DURATION") == null) {
                contract.put("DURATION", duration);
            }
        }

        // список типов объектов - выбор (если уже существует в договоре) или создание нового
        Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        ArrayList<Map<String, Object>> insObjGroupList;
        if (insObjGroupListFromContract != null) {
            insObjGroupList = (ArrayList<Map<String, Object>>) insObjGroupListFromContract;
        } else {
            insObjGroupList = new ArrayList<Map<String, Object>>();
            //insObjGroupList.add(new HashMap<String, Object>());
            contract.put("INSOBJGROUPLIST", insObjGroupList);
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
        //String programCode = getStringParam(contractExtValues.get("insuranceProgram"));
        //if (programCode.isEmpty()) {
        //    programCode = DEFAULT_AND_ONLY_PROGRAM_CODE_GAP;
        //    contractExtValues.put("insuranceProgram", programCode);
        //}
        // определение идентификатора программы по её коду на основании сведений о продукте
        //Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
        //ArrayList<Map<String, Object>> prodProgs = (ArrayList<Map<String, Object>>) prodVer.get("PRODPROGS");
        //Map<String, Object> program = (Map<String, Object>) getLastElementByAtrrValue(prodProgs, "PROGCODE", programCode);
        //Long programID = getLongParam(program.get("PRODPROGID"));
        //contract.put("PRODPROGID", programID);
        // todo: генерация дополнительных параметров (после регистрации продукта в БД)
        //!только для отладки! - начало
        //for (Map<String, Object> insObjGroup : insObjGroupList) {
        //    insObjGroup.put("insuranceProgram", randInt(0,2));
        //    ArrayList<Map<String, Object>> insObjList = (ArrayList<Map<String, Object>>) insObjGroup.get("OBJLIST");
        //    for (Map<String, Object> insObj : insObjList) {
        //        Map<String, Object> insObjMap = (Map<String, Object>) insObj.get("INSOBJMAP");
        //        if (insObjMap != null) {
        //            insObjMap.put("insuranceProgram", randInt(0,2));
        //        }
        //    }
        //}
        //!только для отладки! - конец
        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор
        updateContractInsuranceProductStructure(contract, product, false, getStringParam(contractExtValues.get("insuranceProgram")), login, password);

        // формирование сумм и вызов калькулятора для рассчета сумм
        Map<String, Object> calcParams = new HashMap<String, Object>();
        List<Map<String, Object>> calcProgramList = getCalcProgramListFromInsObjGroupList(insObjGroupList);
        calcParams.put("programList", calcProgramList);
        calcParams.put("CALCVERID", product.get("CALCVERID"));
        logger.debug("calcParams: " + calcParams);
        Map<String, Object> calcRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "calculateByCalculatorVersionID", calcParams, login, password);
        logger.debug("calcRes: " + calcRes + "\n");

        // обновление сумм в объектах и рисках по результатам калькуляции
        List<Map<String, Object>> calcResultProgramList = (List<Map<String, Object>>) calcRes.get("programList");
        String updateSumsStrResult = updateInsuranceSumsFromCalcResult(insObjGroupList, calcResultProgramList);
        if (!updateSumsStrResult.isEmpty()) {
            // если не удалось обновить все суммы в рисках/объектах - выход с сообщением об ощибке (аналогично выходу при выявлении ошибок во входных данных при валидации)
            updateSumsStrResult = updateSumsStrResult + " Сведения договора не сохранены.";
            contract.put("Status", "Error");
            contract.put("Error", updateSumsStrResult);
            return contract;
        }

        // текущая версия калькулятора неверно считает страховую сумму и премию для договора
        //contract.put("INSAMVALUE", calcRes.get("insAmValue"));
        //contract.put("PREMVALUE", calcRes.get("premValue"));
        // todo: убрать расчет страховой суммы и премии для договора после исправления работы калькулятора
        Double insAmValue = 0.0d;
        Double premValue = 0.0d;
        for (Map<String, Object> calcResultElement : calcResultProgramList) {
            Double elementInsAmValue = getDoubleParam(calcResultElement.get("insAmValue"));
            Double elementPremValue = getDoubleParam(calcResultElement.get("premValue"));
            if (elementInsAmValue > insAmValue) {
                insAmValue = elementInsAmValue;
            }
            premValue += elementPremValue;
        }
        contract.put("INSAMVALUE", insAmValue);
        contract.put("PREMVALUE", premValue);
        if (contract.get("INSURERMAP") != null) {
            Map<String, Object> insMap = (Map<String, Object>) contract.get("INSURERMAP");
            if (insMap.get("addressList") != null) {
                List<Map<String, Object>> addressList = (List<Map<String, Object>>) insMap.get("addressList");
                if (!addressList.isEmpty()) {
                    for (Map<String, Object> map : addressList) {
                        remapAddressIfNeed(map);
                    }
                }
            }
        }

        // отдельное сохранение для мультиполиса адреса страхуемого имущества
        Map<String, Object> propertyAddressObjGroup = (Map<String, Object>) getLastElementByAtrrValue(insObjGroupList, "INSOBJGROUPSYSNAME", "property");
        propertyAddressSave(propertyAddressObjGroup, login, password);

        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        markAsModified(contract);

        return contract;
    }

    private void genAdditionalSaveParamsFixContr(Map<String, Object> contract, String login, String password) throws Exception {

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

        contract.put("PREMCURRENCYID", 1);
        // contract.put("INSAMCURRENCYID", 1);

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
            contract.put("TERMID", durationYears);
        }
        //
        GregorianCalendar startDateGC = new GregorianCalendar();
        Object startDate = contract.get("STARTDATE");
        if (startDate != null) {
            startDateGC.setTime((Date) parseAnyDate(startDate, Date.class, "STARTDATE"));
            // безусловное вычисление даты окончания действия
            GregorianCalendar finishDateGC = new GregorianCalendar();
            Object finishDate = contract.get("FINISHDATE");
            if (finishDate == null) {
                finishDateGC.setTime(startDateGC.getTime());
                finishDateGC.add(Calendar.YEAR, durationYears);
                finishDateGC.add(Calendar.DATE, -1);
                finishDateGC.set(Calendar.HOUR_OF_DAY, 23);
                finishDateGC.set(Calendar.MINUTE, 59);
                finishDateGC.set(Calendar.SECOND, 59);
                finishDateGC.set(Calendar.MILLISECOND, 0);
                contract.put("FINISHDATE", finishDateGC.getTime());
            } else {
                finishDateGC.setTime((Date) parseAnyDate(finishDate, Date.class, "FINISHDATE"));
            }
            // безусловное вычисление срока действия договора в днях
            long startDateInMillis = startDateGC.getTimeInMillis();
            long finishDateInMillis = finishDateGC.getTimeInMillis();
            // в сутках (24*60*60*1000) милисекунд
            long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000));
            if (contract.get("DURATION") == null) {
                contract.put("DURATION", duration);
            }
        }

        // список типов объектов - выбор (если уже существует в договоре) или создание нового
        Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        ArrayList<Map<String, Object>> insObjGroupList;
        if (insObjGroupListFromContract != null) {
            insObjGroupList = (ArrayList<Map<String, Object>>) insObjGroupListFromContract;
        } else {
            insObjGroupList = new ArrayList<Map<String, Object>>();
            //insObjGroupList.add(new HashMap<String, Object>());
            contract.put("INSOBJGROUPLIST", insObjGroupList);
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
        //String programCode = getStringParam(contractExtValues.get("insuranceProgram"));
        //if (programCode.isEmpty()) {
        //    programCode = DEFAULT_AND_ONLY_PROGRAM_CODE_GAP;
        //    contractExtValues.put("insuranceProgram", programCode);
        //}
        // определение идентификатора программы по её коду на основании сведений о продукте
        //Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
        //ArrayList<Map<String, Object>> prodProgs = (ArrayList<Map<String, Object>>) prodVer.get("PRODPROGS");
        //Map<String, Object> program = (Map<String, Object>) getLastElementByAtrrValue(prodProgs, "PROGCODE", programCode);
        //Long programID = getLongParam(program.get("PRODPROGID"));
        //contract.put("PRODPROGID", programID);
        // todo: генерация дополнительных параметров (после регистрации продукта в БД)
        //!только для отладки! - начало
        //for (Map<String, Object> insObjGroup : insObjGroupList) {
        //    insObjGroup.put("insuranceProgram", randInt(0,2));
        //    ArrayList<Map<String, Object>> insObjList = (ArrayList<Map<String, Object>>) insObjGroup.get("OBJLIST");
        //    for (Map<String, Object> insObj : insObjList) {
        //        Map<String, Object> insObjMap = (Map<String, Object>) insObj.get("INSOBJMAP");
        //        if (insObjMap != null) {
        //            insObjMap.put("insuranceProgram", randInt(0,2));
        //        }
        //    }
        //}
        //!только для отладки! - конец
        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор
        //updateContractInsuranceProductStructure(contract, product, false, getStringParam(contractExtValues.get("insuranceProgram")), login, password);

        // формирование сумм и вызов калькулятора для рассчета сумм
        /*Map<String, Object> calcParams = new HashMap<String, Object>();
        List<Map<String, Object>> calcProgramList = getCalcProgramListFromInsObjGroupList(insObjGroupList);
        calcParams.put("programList", calcProgramList);
        calcParams.put("CALCVERID", product.get("CALCVERID"));
        logger.debug("calcParams: " + calcParams);
        Map<String, Object> calcRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "calculateByCalculatorVersionID", calcParams, login, password);
        logger.debug("calcRes: " + calcRes + "\n");*/

        // обновление сумм в объектах и рисках по результатам калькуляции
        /*List<Map<String, Object>> calcResultProgramList = (List<Map<String, Object>>) calcRes.get("programList");
        String updateSumsStrResult = updateInsuranceSumsFromCalcResult(insObjGroupList, calcResultProgramList);
        if (!updateSumsStrResult.isEmpty()) {
            // если не удалось обновить все суммы в рисках/объектах - выход с сообщением об ощибке (аналогично выходу при выявлении ошибок во входных данных при валидации)
            updateSumsStrResult = updateSumsStrResult + " Сведения договора не сохранены.";
            contract.put("Status", "Error");
            contract.put("Error", updateSumsStrResult);
            return;
        }*/

        // текущая версия калькулятора неверно считает страховую сумму и премию для договора
        //contract.put("INSAMVALUE", calcRes.get("insAmValue"));
        //contract.put("PREMVALUE", calcRes.get("premValue"));
        // todo: убрать расчет страховой суммы и премии для договора после исправления работы калькулятора
        /*Double insAmValue = 0.0d;
        Double premValue = 0.0d;
        for (Map<String, Object> calcResultElement : calcResultProgramList) {
            Double elementInsAmValue = getDoubleParam(calcResultElement.get("insAmValue"));
            Double elementPremValue = getDoubleParam(calcResultElement.get("premValue"));
            if (elementInsAmValue > insAmValue) {
                insAmValue = elementInsAmValue;
            }
            premValue += elementPremValue;
        }
        contract.put("INSAMVALUE", insAmValue);
        contract.put("PREMVALUE", premValue);
        if (contract.get("INSURERMAP") != null) {
            Map<String, Object> insMap = (Map<String, Object>) contract.get("INSURERMAP");
            if (insMap.get("addressList") != null) {
                List<Map<String, Object>> addressList = (List<Map<String, Object>>) insMap.get("addressList");
                if (!addressList.isEmpty()) {
                    for (Map<String, Object> map : addressList) {
                        remapAddressIfNeed(map);
                    }
                }
            }
        }*/
        // отдельное сохранение для мультиполиса адреса страхуемого имущества
        Map<String, Object> propertyAddressObjGroup = (Map<String, Object>) getLastElementByAtrrValue(insObjGroupList, "INSOBJGROUPSYSNAME", "property");
        propertyAddressSave(propertyAddressObjGroup, login, password);
        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        markAsModified(contract);
    }

    private void propertyAddressSave(Map<String, Object> propertyAddressObjGroup, String login, String password) throws Exception {
        if (propertyAddressObjGroup != null) {
            //Long isPropertyRegisterAddress = getLongParam(propertyAddressObjGroup.get("isPropertyRegisterAddress"));
            Map<String, Object> propertyAddress = (Map<String, Object>) propertyAddressObjGroup.get("propertyAddress");
            logger.debug("propertyAddress: " + propertyAddress);
            if (propertyAddress != null) {
                Integer addressRowStatus;
                if (propertyAddress.get(ROWSTATUS_PARAM_NAME) != null) {
                    addressRowStatus = getIntegerParam(propertyAddress.get(ROWSTATUS_PARAM_NAME));
                } else if (propertyAddress.get("ADDRESSID") == null) {
                    addressRowStatus = INSERTED_ID;
                } else {
                    addressRowStatus = MODIFIED_ID;
                }

                String methodName = "";
                boolean isGenFullTextAddress = false;
                if (addressRowStatus == INSERTED_ID) {
                    propertyAddress.remove("ADDRESSID");
                    isGenFullTextAddress = true;
                    methodName = "dsB2BAddressCreate";
                } else if (addressRowStatus == MODIFIED_ID) {
                    isGenFullTextAddress = true;
                    methodName = "dsB2BAddressUpdate";
                } else if (addressRowStatus == DELETED_ID) {
                    methodName = "dsB2BAddressDelete";
                }

                Map<String, Object> addressParams = new HashMap<String, Object>();
                addressParams.putAll(propertyAddress);
                if (isGenFullTextAddress) {
                    if ((propertyAddress.get("USEKLADR") != null) && ("1".equals(propertyAddress.get("USEKLADR").toString()))) {
                        //адрес не нуждается в генерации строки - она указана вручную.
                        logger.debug("fullAddressTexts: set from form");
                    } else {

                        remapAddressIfNeed(addressParams);
                        addressParams.put(RETURN_AS_HASH_MAP, true);
                        Map<String, Object> fullAddressTexts = this.callService(B2BPOSWS_SERVICE_NAME, "dsGenFullTextAddress", addressParams, login, password);
                        logger.debug("fullAddressTexts: " + fullAddressTexts);
                        addressParams.putAll(fullAddressTexts);
                    }
                }

                Object addressID = null;
                if (!methodName.isEmpty()) {
                    try {
                        addressID = this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, methodName, addressParams, login, password, "ADDRESSID");
                    } catch (Exception ex) {
                        logger.debug("Произошло исключение при вызове метода '" + methodName + "'. Операция с адресом имущества не выполнена.");
                    }
                }
                logger.debug("addressID: " + addressID + "\n");
                propertyAddressObjGroup.put("propertyAddress", addressID);

            }
        }
    }

    /**
     * Метод для сохранения договора по продукту мультиполис.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMultiContractPrepareToSave(Map<String, Object> params) throws Exception {

        logger.debug("before dsB2BMultiContractPrepareToSave");

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

        logger.debug("after dsB2BMultiContractPrepareToSave");

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMultiContractPrepareToSaveFixContr(Map<String, Object> params) throws Exception {

        logger.debug("before dsB2BMultiContractPrepareToSave");

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
        if ((null != params.get("is1CExported")) && ((Boolean) params.get("is1CExported"))) {
            if ((null != params.get("b2bCorrector1C")) && ((Boolean) params.get("b2bCorrector1C"))) {
                result = contract;
            } else if ((null != params.get("isCorrector")) && ((Boolean) params.get("isCorrector"))) {
                contract.remove("DURATION");
                contract.remove("FINISHDATETIME");
                contract.remove("FINISHDATE");
                contract.remove("STARTDATETIME");
                contract.remove("STARTDATE");
                contract.remove("CRMDOCLIST");
                contract.remove("INSURERID");
                contract.remove("INSURERMAP");
                result = contract;
            } else {
                // Если договор выгружен в 1С и у пользователя нет прав корректора запрещем что либо сохранять.
                result = new HashMap< String, Object>();
            }
        }
        logger.debug("after dsB2BMultiContractPrepareToSave");
        return result;
    }

    /**
     * Метод для сохранения договора по продукту мультиполис.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
 /*   @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMultiContractPrepareToSaveFixContr(Map<String, Object> params) throws Exception {

        logger.debug("before dsB2BMultiContractPrepareToSaveFixContr");

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
            genAdditionalSaveParamsFixContr(contract, login, password);
            result = contract;
        } else {
            result = contract;
        }

        logger.debug("after dsB2BMultiContractPrepareToSaveFixContr");

        return result;
    }*/

    /**
     * Метод для загрузки договора по продукту мультиполис.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMultiContrLoad(Map<String, Object> params) throws Exception {

        logger.debug("before dsB2BMultiContrLoad");

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> loadResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", params, login, password);
        Map<String, Object> contract = (Map<String, Object>) (loadResult.get(RESULT));
        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        if (insObjGroupList != null) {
            Map<String, Object> propertyAddressObjGroup = null;
            for (Map<String, Object> insObjGroup : insObjGroupList) {
                if ("property".equalsIgnoreCase(getStringParam(insObjGroup.get("INSOBJGROUPSYSNAME")))) {
                    propertyAddressObjGroup = insObjGroup;
                    break;
                }
            }
            if (propertyAddressObjGroup != null) {
                Long addressID = getLongParam(propertyAddressObjGroup.get("propertyAddress"));
                logger.debug("addressID: " + addressID);
                if (addressID != null) {
                    //Long isPropertyRegisterAddress = getLongParam(propertyAddressObjGroup.get("isPropertyRegisterAddress"));
                    Map<String, Object> addressParams = new HashMap<String, Object>();
                    addressParams.put("ADDRESSID", addressID);
                    addressParams.put(RETURN_AS_HASH_MAP, true);
                    Map<String, Object> propertyAddress = null;
                    try {
                        propertyAddress = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAddressBrowseListByParam", addressParams, login, password);
                    } catch (Exception ex) {
                        logger.debug("Произошло исключение при вызове метода 'dsB2BAddressBrowseListByParam'. Операция с адресом имущества не выполнена.");
                    }
                    if (propertyAddress != null) {
                        propertyAddress.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                        propertyAddressObjGroup.put("propertyAddress", propertyAddress);
                    }
                    logger.debug("propertyAddress: " + propertyAddress);
                }
            }
        }

        logger.debug("after dsB2BMultiContrLoad");

        return loadResult;

    }

    private void remapAddressIfNeed(Map<String, Object> addressParams) {
        if (addressParams.get("CITYKLADR") == null) {
            if (addressParams.get("country") != null) {
                Map<String, Object> country = (Map<String, Object>) addressParams.get("country");
                addressParams.put("COUNTRY", country.get("BRIEFNAME"));
            }
            if (addressParams.get("region") != null) {
                Map<String, Object> region = (Map<String, Object>) addressParams.get("region");
                addressParams.put("REGIONKLADR", region.get("CODE"));
                addressParams.put("REGION", region.get("NAME"));
            }
            if (addressParams.get("city") != null) {
                Map<String, Object> city = (Map<String, Object>) addressParams.get("city");
                addressParams.put("CITYKLADR", city.get("CODE"));
                addressParams.put("CITY", city.get("NAME"));
            }
            if (addressParams.get("street") != null) {
                Map<String, Object> street = (Map<String, Object>) addressParams.get("street");
                addressParams.put("STREETKLADR", street.get("CODE"));
                addressParams.put("STREET", street.get("NAME"));
            }
            addressParams.put("HOUSE", addressParams.get("house"));
            addressParams.put("HOUSING", "");
            addressParams.put("BUILDING", "");
            addressParams.put("FLAT", addressParams.get("flat"));
        }
    }

    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> dsB2BMultiContractCalcFinishDate(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BMultiContractCalcFinishDate begin");
        Map<String, Object> contract = (Map<String, Object>) params.get("CONTRMAP");
        GregorianCalendar startDateGC = new GregorianCalendar();
        Object startDate = contract.get("STARTDATE");
        Map<String, Object> result = new HashMap<String, Object>();
        if (startDate != null) {
            startDateGC.setTime((Date) parseAnyDate(startDate, Date.class, "STARTDATE"));
            Integer durationYears = getIntegerParam(contract.get("TERMID"));
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
            result.put("FINISHDATE", finishDateGC.getTime());

            // безусловное вычисление срока действия договора в днях
            long startDateInMillis = startDateGC.getTimeInMillis();
            long finishDateInMillis = finishDateGC.getTimeInMillis();
            // в сутках (24*60*60*1000) милисекунд
            long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000));
            result.put("DURATION", duration);
        }
        logger.debug("dsB2BMultiContractCalcFinishDate end");
        return result;
    }

}
