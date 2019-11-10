package com.bivgroup.services.b2bposws.facade.pos.validation;

import java.util.Calendar;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/**
 * @author Alex Ivashin
 */
public abstract class ValidationContractParameterB2BCustom extends ValidationParameterB2BCustom {

    private static final String ERROR_STRUCTURE_HEADER_CONTRACT = ERROR_STRUCTURE_HEADER + " договора:";
    private static final String NO_CALCULATION_ERROR_TEXT = "Требуется выполнить перерасчет, нажав на кнопку \"Рассчитать\"";
    protected static final String PRODUCT_MAP = "PRODUCTMAP";
    
    protected Map<String, Object> contract;
    protected Map<String, Object> section;
    protected List<Map<String, Object>> insObjList;
    protected Map<String, Object> insObjGroup;
    protected List<Map<String, Object>> prodStructs;

    public ValidationContractParameterB2BCustom(Map<String, Object> contract, Map<String, Object> additionalSettings) {
        super(additionalSettings);
        contract.remove("AdditionalSettings");
        this.contract = contract;
        getProductStructure();
        logger = Logger.getLogger(ValidationContractParameterB2BCustom.class);
    }

    private void getProductStructure() {
        Map<String, Object> productMap = (Map<String, Object>) (isNullCollection(contract.get(PRODUCT_MAP)) ? additionalSettings.get(PRODUCT_MAP)
                : contract.get(PRODUCT_MAP));
        if (isNullCollection(productMap)) {
            logger.error("Is product map not exist in contract or additionalSettings");
            return;
        }

        Map<String, Object> prodVer = (Map<String, Object>) productMap.get("PRODVER");
        if (isNullCollection(prodVer)) {
            return;
        }

        prodStructs = (List<Map<String, Object>>) prodVer.get("PRODSTRUCTS");
    }

    public abstract boolean validationParametersToCalculations();

    protected abstract void validationAllPremium();

    protected boolean validationPremium(Map<String, Object> struct) {
        return validationPremium(struct, null);
    }

    protected boolean validationPremium(Map<String, Object> struct, String errorHeaderStr) {
        boolean result = true;
        Object premium = struct.get("PREMVALUE");
        errorHeaderStr = isNullParameter(errorHeaderStr) ? "Отсутствует" : errorHeaderStr;
        StringBuilder errorHeader = new StringBuilder(errorHeaderStr);
        if (isNullParameter(premium) || Double.valueOf(premium.toString()).compareTo(0.0) == 0) {
            errorHeader.append(" значение страховой премии");
            setError(errorHeader.toString(), NO_CALCULATION_ERROR_TEXT);
            result = false;
        }

        return result;
    }

    protected String getNameFromProdStructBySysName(String sysnameValue) {
        String result = "";
        Map<String, Object> struct = Collections.EMPTY_MAP;
        if (!isNullCollection(prodStructs)) {
            String sysname;
            for (Map<String, Object> item : prodStructs) {
                sysname = (String) item.get("SYSNAME");
                if (isNullParameter(sysname)) {
                    continue;
                }
                if (sysname.equalsIgnoreCase(sysnameValue)) {
                    struct = item;
                    break;
                }
            }
        }
        if (!isNullCollection(struct)) {
            result = (String) struct.get("NAME");
        }

        return result;
    }

    public void setStructureError(String structureName) {
        setError(ERROR_STRUCTURE_HEADER_CONTRACT, structureName);
    }

    protected Map<String, Object> getContrSectionOfContractBySysName(Map<String, Object> contract, String sysName) {
        return getMapOfSearchMapByPrefixAndSysName(contract, "CONTRSECTION", sysName);
    }

    protected Map<String, Object> getInsObjGroupOfSectionBySysName(Map<String, Object> section, String sysName) {
        return getMapOfSearchMapByPrefixAndSysName(section, "INSOBJGROUP", sysName);
    }

    protected void getStructureContarct(String sectionSysName, String insObjGroupSysName) {
        section = getContrSectionOfContractBySysName(contract, sectionSysName);
        insObjGroup = checkSectionContract() ? getInsObjGroupOfSectionBySysName(section, insObjGroupSysName)
                : Collections.EMPTY_MAP;
        insObjList = checkInsObjGroupContract() ? (List<Map<String, Object>>) insObjGroup.get("OBJLIST")
                : Collections.EMPTY_LIST;
    }

    protected boolean isValidStructureContract() {
        return checkSectionContract() && checkInsObjGroupContract() && checkInsObjListContract();
    }

    private boolean checkSectionContract() {
        boolean result = true;
        if (section.isEmpty()) {
            setStructureError("Страховая секция");
            result = false;
        }
        return result;
    }

    private boolean checkInsObjGroupContract() {
        boolean result = true;
        if (insObjGroup.isEmpty()) {
            setStructureError("Страховая группа");
            result = false;
        }
        return result;
    }

    private boolean checkInsObjListContract() {
        boolean result = true;
        if (isNullCollection(insObjList)) {
            setStructureError("Список объектов страхования");
            result = false;
        }
        return result;
    }

    protected Map<String, Object> getInsObjectOfGroupBySysname(List<Map<String, Object>> insObjects, String valueSysname) {
        Map<String, Object> result = Collections.EMPTY_MAP;

        for (Map<String, Object> insObj : insObjects) {
            Map<String, Object> insObjMap = (Map<String, Object>) insObj.get("INSOBJMAP");
            if (insObjMap.get("INSOBJSYSNAME") != null
                    && valueSysname.equals(insObjMap.get("INSOBJSYSNAME"))) {
                result = insObjMap;
                break;
            }
        }
        return result;
    }

    protected void checkProdSysName() {
        if (isNullParameter(contract.get("PRODSYSNAME"))) {
            setRequiredError("Системное наименование продукта");
        }
    }

    protected void setZeroTime(GregorianCalendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }
}
