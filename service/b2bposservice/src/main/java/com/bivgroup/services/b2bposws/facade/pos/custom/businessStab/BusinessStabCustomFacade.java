package com.bivgroup.services.b2bposws.facade.pos.custom.businessStab;

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
import org.apache.log4j.Logger;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("BusinessStabCustom")
public class BusinessStabCustomFacade extends ProductContractCustomFacade {

    private final Logger logger = Logger.getLogger(BusinessStabCustomFacade.class);
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    private static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;

    private boolean validateSaveParams(Map<String, Object> contract) {
        boolean isDataInvalid = false;
        String errorText = "";
        if (isDataInvalid) {
            errorText = errorText + "Сведения договора не сохранены.";
            contract.put("Status", "Error");
            contract.put("Error", errorText);
        }
        return !isDataInvalid;
    }

//    private Boolean markAsModified(Map<String, Object> targetStruct) {
//        Boolean isMarkedAsModified = false;
//        Object currentRowStatus = targetStruct.get(ROWSTATUS_PARAM_NAME);
//        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
//            targetStruct.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
//            isMarkedAsModified = true;
//        }
//        return isMarkedAsModified;
//    }

    protected Map<String, Object> genAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {
        // идентификатор версии продукта всегда передается в явном виде с интерфейса
        Long prodVerID = getLongParam(contract.get("PRODVERID"));
        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            contract.put("PRODCONFID", prodConfID);
        }
        //
        contract.put("PREMCURRENCYID", 1);
        contract.put("INSAMCURRENCYID", 1);
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
        Object startDate = contract.get("STARTDATE");
        if (startDate == null) {
            startDateGC.setTime(documentDateGC.getTime());
            startDateGC.add(Calendar.DATE, 14);
            contract.put("STARTDATE", startDateGC.getTime());
        } else {
            startDateGC.setTime((Date) parseAnyDate(startDate, Date.class, "STARTDATE"));
        }
        // безусловное вычисление даты окончания действия
        GregorianCalendar finishDateGC = new GregorianCalendar();
        Object finishDate = contract.get("FINISHDATE");
        if (finishDate == null) {
            finishDateGC.setTime(startDateGC.getTime());
            finishDateGC.add(Calendar.YEAR, 1);
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
        if (contract.get("DURATION") == null) {
            long startDateInMillis = startDateGC.getTimeInMillis();
            long finishDateInMillis = finishDateGC.getTimeInMillis();
            // в сутках (24*60*60*1000) милисекунд
            long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000));
            contract.put("DURATION", duration);
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
        Map<String, Object> product = this.callService(Constants.B2BPOSWS, "dsProductBrowseByParams", productParams, login, password);

        // определение идентификатора справочника расширенных атрибутов договора на основании сведений о продукте
        Object contrExtMapHBDataVerID = product.get("HBDATAVERID");
        contractExtValues.put("HBDATAVERID", contrExtMapHBDataVerID);

        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор
        updateContractInsuranceProductStructure(contract, product, false, getStringParam(contractExtValues.get("insuranceProgram")), login, password);

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

        // определение идентификатора программы по её коду на основании сведений о продукте
        Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
        ArrayList<Map<String, Object>> prodProgs = (ArrayList<Map<String, Object>>) prodVer.get("PRODPROGS");
        String programCode = getStringParam(contractExtValues.get("insuranceProgram"));
        Map<String, Object> program = (Map<String, Object>) getLastElementByAtrrValue(prodProgs, "PROGCODE", programCode);
        Long programID = getLongParam(program.get("PRODPROGID"));
        contract.put("PRODPROGID", programID);

        // отдельное сохранение адреса страхуемого имущества
        Map<String, Object> propertyAddressObjGroup = (Map<String, Object>) getLastElementByAtrrValue(insObjGroupList, "INSOBJGROUPSYSNAME", "businessStab.property");
        propertyAddressSave(propertyAddressObjGroup, login, password);

        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        markAsModified(contract);

        return contract;
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
                        Map<String, Object> fullAddressTexts = this.callService(Constants.B2BPOSWS, "dsGenFullTextAddress", addressParams, login, password);
                        logger.debug("fullAddressTexts: " + fullAddressTexts);
                        addressParams.putAll(fullAddressTexts);
                    }
                }

                Object addressID = null;
                if (!methodName.isEmpty()) {
                    try {
                        addressID = this.callServiceAndGetOneValue(Constants.B2BPOSWS, methodName, addressParams, login, password, "ADDRESSID");
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
     * Метод для сохранения договора по продукту "Стабильный бизнес Онлайн"
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBusinessStabContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BBusinessStabContractPrepareToSave");

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

        logger.debug("after dsB2BBusinessStabContractPrepareToSave");
        return result;
    }

    /**
     * Метод для загрузки договора по продукту "Стабильный бизнес Онлайн"
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBusinessStabContractLoad(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BBusinessStabContractLoad");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> loadResult = this.callService(Constants.B2BPOSWS, "dsB2BContrLoad", params, login, password);

        Map<String, Object> contract = (Map<String, Object>) (loadResult.get(RESULT));
        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        if (insObjGroupList != null) {
            Map<String, Object> propertyAddressObjGroup = null;
            for (Map<String, Object> insObjGroup : insObjGroupList) {
                if ("businessStab.property".equalsIgnoreCase(getStringParam(insObjGroup.get("INSOBJGROUPSYSNAME")))) {
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

        logger.debug("after dsB2BBusinessStabContractLoad");
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

    // загрузка справочника ОПФ
    private List<Map<String, Object>> loadOPFList(String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ReferenceName", "Справочник ОПФ");
        param.put("ReferenceGroupName", "Справочники клиентской базы");
        Map<String, Object> qRes = this.callService(Constants.REFWS, "refItemGetListByParams", param, login, password);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (qRes.get(RESULT) != null) {
            if (qRes.get(RESULT) instanceof List) {
                List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                logger.debug(resList);
                result = resList;
            }
        }
        return result;
    }

    @WsMethod(requiredParams = {"PRODSYSNAME"})
    public Map<String, Object> dsB2BBusinessStabHandbooksBrowseEx(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BBusinessStabHandbooksBrowseEx");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> product = null;
        String productSysName = getStringParam(params.get("PRODSYSNAME"));
        if (productSysName.isEmpty()) {
            product = makeErrorResult("Не указано системное имя продукта (PRODSYSNAME).");
        } else {
            Map<String, Object> productVersionInfo = null;
            productVersionInfo = getProductVersionInfoIfNullBySingleParam(productVersionInfo, "PRODSYSNAME", productSysName, login, password);
            Long productVersionID = getLongParam(productVersionInfo.get("PRODVERID"));
            if (productVersionID == null) {
                product = makeErrorResult("Не удалось определить версию продукта (PRODVERID) по переданному системному имени продукта (PRODSYSNAME).");
            } else {
                Map<String, Object> configParams = new HashMap<String, Object>();
                configParams.put("PRODVERID", productVersionID);
                Long prodConfID = getLongParam(this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
                if (prodConfID == null) {
                    product = makeErrorResult("Не удалось определить идентификатор продукта (PRODCONFID) по переданному системному имени продукта (PRODSYSNAME).");
                } else {
                    Map<String, Object> productParams = new HashMap<String, Object>();
                    productParams.put("PRODCONFID", prodConfID);
                    productParams.put(RETURN_AS_HASH_MAP, true);
                    product = this.callService(Constants.B2BPOSWS, "dsProductBrowseByParams", productParams, login, password);
                    if (product == null) {
                        product = makeErrorResult("Не удалось получить сведения продукта по переданному системному имени продукта (PRODSYSNAME).");
                    } else {
                        Long calcVerID = getLongParam(product.get("CALCVERID"));
                        loadHandbookList(result, null, "B2B.BusinessStab.Risk.Sums", calcVerID.intValue(), "B2B.BusinessStab.Risk.Sums", login, password);
                        loadHandbookList(result, null, "B2B.BusinessStab.PropertyOwnBase", calcVerID.intValue(), "B2B.BusinessStab.PropertyOwnBase", login, password);
                    }
                }
            }
        }
        result.put("PRODMAP", product);
        result.put("OPFList", loadOPFList(login, password)); // справочник ОПФ

        logger.debug("after dsB2BBusinessStabHandbooksBrowseEx");
        return result;
    }

    /**
     * Метод для подготовки данных для отчета по продукту Стабильный бизнес.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID"})
    public Map<String, Object> dsB2BBusinessStabPrintDocDataProvider(Map<String, Object> params) throws Exception {

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
                    if ((insObjGroup.get("INSOBJGROUPSYSNAME") != null) && (insObjGroup.get("INSOBJGROUPSYSNAME").equals("businessStab.property"))) {
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
                        String propertyOwnBase = getStringParam(insObjGroup.get("propertyOwnBase"));
                        String propertyOwnBaseStr = propertyOwnBase;
                        Map<String, Object> prodMap = (Map<String, Object>) result.get("PRODUCTMAP");
                        if (prodMap != null) {
                            Long calcVerId = getLongParam(prodMap.get("CALCVERID"));
                            logger.debug("propertyOwnBase: " + propertyOwnBase);
                            Map<String, Object> propOwnBaseHBmap = new HashMap<String, Object>();

                            loadHandbookList(propOwnBaseHBmap, null, "B2B.BusinessStab.PropertyOwnBase", calcVerId, "PropertyOwnBaseList", login, password);
                            if (propOwnBaseHBmap.get("PropertyOwnBaseList") != null) {
                                List<Map<String, Object>> propertyOwnBaseList = (List<Map<String, Object>>) propOwnBaseHBmap.get("PropertyOwnBaseList");
                                for (Map<String, Object> map : propertyOwnBaseList) {
                                    if (propertyOwnBase.equalsIgnoreCase(getStringParam(map.get("sysName")))) {
                                        propertyOwnBaseStr = getStringParam(map.get("name"));
                                        break;
                                    }
                                }
                            }
                            insObjGroup.put("propertyOwnBase", propertyOwnBaseStr);

                        }

                    }
                }
            }
        }

        // todo: генерация дополнительных значений (после предоставления шаблонов печатного документа)
        //logger.debug("dsB2BMultiPrintDocDataProvider result:\n\n" + result + "\n");
        return result;
    }

    protected void loadHandbookList(Map<String, Object> result, Map<String, Object> params, String hbName, Long calcVerId, String resListName, String login, String password) throws Exception {
        Map<String, Object> qParam = new HashMap<String, Object>();
        qParam.put("CALCVERID", calcVerId);
        if (params != null) {
            qParam.put("PARAMS", params);
        }
        qParam.put("NAME", hbName);
        Map<String, Object> qRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", qParam, login, password);
        if (qRes.get(RESULT) != null) {
            if (qRes.get(RESULT) instanceof List) {
                List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                result.put(resListName, resList);
            }
        }

    }

}
