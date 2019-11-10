/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.inscom;

import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import static com.bivgroup.services.b2bposws.facade.pos.product.custom.ProductCustomFacade.B2BPOSWS;
import com.bivgroup.services.b2bposws.system.Constants;
import static com.bivgroup.services.b2bposws.system.Constants.INSTARIFICATORWS;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author kkulkov
 */
@BOName("InsComCustom")
public class InsComCustomFacade extends ProductContractCustomFacade {

    private final Logger logger = Logger.getLogger(InsComCustomFacade.class);
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    private static final String REFWS_SERVICE_NAME = Constants.REFWS;

    private static Long insComCalculatorVersionID = null;
    private static Long insComProductConfigID = null;
    private static final String HBMask = "B2B.InsCom%;%;%";
    private static final String CHECKBOX_GROUP_HB_NOTE_MASK = "checkboxGroup:%;" + HBMask;
    
    private static List<Map<String, Object>> OPFList = null;

    private static final String DELETED_UNUSED_BY_REPORT_CONTENT = "[СОДЕРЖИМОЕ УДАЛЕНО - НЕ ТРЕБУЕТСЯ ДЛЯ ОФОРМЛЕНИЯ ДОКУМЕНТОВ]";

    /**
     * dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm")
     *
     */
    //private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    /**
     * dateFormatter = new SimpleDateFormat("ddMMyyyy")
     *
     */
    //private final SimpleDateFormat dateFormatterNoDots = new SimpleDateFormat("ddMMyyyy");

    /**
     * Список частей названий ключей, указывающих на участников из договора
     * (полные названия: ... + MAP = данные участника; ... + ID = идентификатор
     * участника в договоре)
     */
    public static final String[] PARTICIPANT_NODES = {
        "INSURER", // страхователь
        "INSURERREP", // представитель страхователя
    };

    private static final String[] DEFAULTED_KEY_NAMES_DOUBLE = {
        "PREMVALUE",
        "INSAMVALUE"};
    private static final String[] DEFAULTED_KEY_NAMES = {
        "addRisk1",
        "addRisk2",
        "addRisk3",
        //
        "propIsReparing",
        //
        "pledge",
        //
        "franchise",
        // материалы несущих стен
        "wallMonoFrConcr",
        "wallPnlFrConcr",
        "wallMetal",
        "wallWood",
        "wallOther",
        // материалы перекрытий
        "ceilFrConcr",
        "ceilMetalWood",
        "ceilMetal",
        "ceilWood",
        "ceilOther",
        // охрана
        "guardFireEquip",
        "guardFireAlarm",
        "guardAutoExting",
        "guardSecAlarm",
        "guardSecPhysic",
        // 
        "goodsNonIgnitable",
        "goodsHardIgnitable",
        "goodsIgnitable",
        "goodsIgnitablePack",
        // 
        "packNo",
        "packNonIgnitable",
        "packIgnitable",
        "packIgnitableEdges",
        //
        "storeHall",
        "storeWarehouse",
        "storeOpen",
        // 
        "placingShowcase",
        "placingColdstore",
        "placingBoxing",
        "placingStockpile",
        // 
        "placingFloor",
        "placingShelving",
        "placingTray",
        //
        "placingTank",
        "placingSubsurface",
        "placingOverland",
        "placingInBulk",
        //
        "storeHallPct",
        "storeWarehousePct",
        "storeOpenPct",
        //
        "propYear",
        "propArea",
        "propFloors",
        //
        "productFood",
        "productNonFood",
        "productPharm",
        "productSpecial",
        //
        "usedWelding",
        "workObject",
        "usedHighAltWorks",
        "workersSkilled",
        //
        "fireProtFullTime",
        "fireProtHydrant",
        "fireProtStatExting",
        "fireProtHandExting",
        "fireProtPump",
        "fireProtWater",
        //
        "isSeason",
        //
        "ownBasis",
        "sumBasis",
        "compensBasis",
        "propPurpose",
        //"propArea",
        //"propFloors",
        //"propYear",
        "equipPlace",
        "equipMountType",
        "equipWear",
        "equipCount",
        "equipStatus",
        "storageType",
        "riskTheft"};

    private static final String[] DEFAULTED_STRINGS_KEY_NAMES = {
        "ceilOtherDescr",
        "creditNumber",
        "detTerritory",
        "equipPurpose",
        "insCauseOtherDescr",
        "name",
        "number",
        "ownBasisOtherDescr",
        "pledgeNumber",
        "propAddrBuliding",
        "propAddrCdstrNum",
        "propAddrHousing",
        "propAddrLiter",
        "propAddrStr",
        "propAddrWorkshop",
        "securityObjectDescr",
        "sumBasisOtherDescr",
        "techProc",
        "wallOtherDescr",
        "workAgrNumber"
    };

    public InsComCustomFacade() {
        super();
        init();
    }

    private void init() {
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
                if (isDataInvalid) {
                    errorText = errorText + "Сведения договора не сохранены.";
                    contract.put("Status", "Error");
                    contract.put("Error", errorText);
                }
            }
        }
        return !isDataInvalid;
    }

    private Long getCalcEnabledParamVal(Object value) {
        if (value == null) {
            return 0L;
        } else {
            try {
                return Long.valueOf(value.toString());
            } catch (Exception e) {
                logger.debug("value not Long - " + value.toString());
                return 0L;
            }

        }
    }

    private List<Map<String, Object>> prepareInsObjGroupListForCalc(List<Map<String, Object>> insObjGroupList, Map<String, Object> contract) {
        Map<String, Object> contrExt = (Map<String, Object>) contract.get("CONTREXTMAP");
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> insObjGroupBean : insObjGroupList) {
            Map<String, Object> newInsObjGroupBean = new HashMap<String, Object>();
            String gSysname = "NOVALUENOVALUE";
            if (insObjGroupBean.get("INSOBJGROUPSYSNAME") != null) {
                gSysname = insObjGroupBean.get("INSOBJGROUPSYSNAME").toString();
            }
            logger.debug("gSysname: " + gSysname);
            Boolean needCalc = Boolean.FALSE;
            Long gSysnameChecked = getLongParam(contrExt.get(gSysname));
            logger.debug("gSysnameChecked: " + gSysnameChecked);
            if ((gSysnameChecked != null) && (gSysnameChecked > 0)) {
                needCalc = true;
            } else {
                logger.debug(String.format("Insurance оbjects group with system name '%s' is turned off in contract extended attributes.", gSysname));
                // если ТОС помечен как удаляемый И отключен в расширенных атрибутах договора - необходимо удалить его вместе с дочерними объектами
                // (фактическое удаление выполняется позже - здесь все дочерние структуры ТОСа только помечаются как удаляемые)
                Long insObjGroupRowStatus = getLongParam(insObjGroupBean.get(ROWSTATUS_PARAM_NAME));
                logger.debug("Insurance оbjects group row status: " + insObjGroupRowStatus);
                if ((insObjGroupRowStatus != null) && (insObjGroupRowStatus.intValue() == DELETED_ID)) {
                    logger.debug("Insurance оbjects group marked as deleted, marking all it subentities as deleted...");
                    markAllMapsByKeyValue(insObjGroupBean, ROWSTATUS_PARAM_NAME, DELETED_ID);
                    logger.debug("Marking all insurance оbjects group subentities as deleted finished.");
                }
            }
            logger.debug("needCalc: " + needCalc);
            if (needCalc) {
                result.add(newInsObjGroupBean);
                newInsObjGroupBean.put("obj_index", insObjGroupList.indexOf(insObjGroupBean));
                List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupBean.get("OBJLIST");
                if ((objList != null) && (objList.size() > 0)) {
                    List<Map<String, Object>> newObjList = new ArrayList<Map<String, Object>>();
                    newInsObjGroupBean.put("OBJLIST", newObjList);
                    newInsObjGroupBean.put("INSOBJGROUPSYSNAME", gSysname);

                    // вычисление общего количества всего перечня спец. техники для определения кПарк (коэффициента парка спец. техники)
                    Map<String, Object> newInsObjGroupBeanProperties = new HashMap<String, Object>();
                    if (gSysname.equals("InsC_sequip")) {
                        Long totalEquipCount = 0L;
                        for (Map<String, Object> objBean : objList) {
                            Integer objRowStatus = getSimpleIntParam(objBean.get("ROWSTATUS"));
                            if (!objRowStatus.equals(DELETED_ID)) {
                                totalEquipCount++;
                            }
                        }
                        newInsObjGroupBeanProperties.put("totalEquipCount", totalEquipCount);
                        logger.debug("totalEquipCount = " + totalEquipCount);
                    }

                    for (Map<String, Object> objBean : objList) {
                        Map<String, Object> newObjBean = new HashMap<String, Object>();
                        Integer objRowStatus = getSimpleIntParam(objBean.get("ROWSTATUS"));
                        if (!objRowStatus.equals(DELETED_ID)) {
                            newObjList.add(newObjBean);
                            Map<String, Object> insObjMap = (Map<String, Object>) objBean.get("INSOBJMAP");
                            Map<String, Object> contrObjMap = (Map<String, Object>) objBean.get("CONTROBJMAP");
                            if ((insObjMap != null) && (contrObjMap != null)) {
                                Map<String, Object> newInsObjMap = new HashMap<String, Object>();
                                newInsObjMap.putAll(insObjMap);
                                newObjBean.put("INSOBJMAP", newInsObjMap);
                                Map<String, Object> newContrObjMap = new HashMap<String, Object>();
                                newContrObjMap.putAll(contrObjMap);
                                newObjBean.put("CONTROBJMAP", newContrObjMap);
                                newObjBean.put("obj_index", objList.indexOf(objBean));
                                List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                                if ((contrRiskList != null) && (contrRiskList.size() > 0)) {
                                    List<Map<String, Object>> newContrRiskList = new ArrayList<Map<String, Object>>();
                                    newContrObjMap.put("CONTRRISKLIST", newContrRiskList);
                                    for (Map<String, Object> riskBean : contrRiskList) {
                                        Map<String, Object> riskExt = (Map<String, Object>) riskBean.get("CONTRRISKEXTMAP");
                                        Integer riskRowStatus = getSimpleIntParam(riskBean.get("ROWSTATUS"));
                                        if (!riskRowStatus.equals(DELETED_ID)) {
                                            Map<String, Object> newRiskBean = new HashMap<String, Object>();
                                            if (riskExt != null) {
                                                newRiskBean.putAll(riskExt);
                                            }
                                            newRiskBean.putAll(insObjMap);
                                            newRiskBean.put("obj_index", contrRiskList.indexOf(riskBean));
                                            Map<String, Object> tempMap = new HashMap<String, Object>();
                                            tempMap.putAll(insObjGroupBean);
                                            tempMap.remove("OBJLIST");
                                            // INSOBJGROUPINSAMVALUE используется в калькуляторе для определения зоны (и значения по умолчанию для кАндеррайтера) по суммам лимитов из справочника 'B2B.InsCom.vSectionLimits'
                                            tempMap.put("INSOBJGROUPINSAMVALUE", tempMap.remove("INSAMVALUE"));
                                            newRiskBean.putAll(tempMap);
                                            tempMap = new HashMap<String, Object>();
                                            tempMap.putAll(riskBean);
                                            tempMap.remove("CONTRRISKEXTMAP");
                                            newRiskBean.putAll(tempMap);
                                            newRiskBean.putAll(contrExt);
                                            newRiskBean.putAll(newInsObjGroupBeanProperties);
                                            newRiskBean.put("INSAMVALUE", newContrObjMap.get("INSAMVALUE"));
                                            newContrRiskList.add(newRiskBean);
                                            for (String keyName : DEFAULTED_KEY_NAMES) {
                                                logger.debug("keyName = " + keyName);
                                                newRiskBean.put(keyName, getCalcEnabledParamVal(newRiskBean.get(keyName)));
                                            }
                                            newRiskBean.put("INSAMVALUE", newContrObjMap.get("INSAMVALUE"));

                                            String riskSysName = getStringParam(riskBean.get("PRODRISKSYSNAME"));
                                            if (!riskSysName.isEmpty()) {
                                                // спец. техника, расчет возраста спец. техники
                                                if (riskSysName.equalsIgnoreCase("InsC_sequip_r")) {
                                                    Long yearValue = null;
                                                    if (newRiskBean.get("year") != null) {
                                                        yearValue = Long.valueOf(newRiskBean.get("year").toString());
                                                    }
                                                    GregorianCalendar gcToday = new GregorianCalendar();
                                                    gcToday.setTime(new Date());
                                                    if (yearValue == null) {
                                                        yearValue = Long.valueOf(gcToday.get(Calendar.YEAR));
                                                    }
                                                    int curYear = gcToday.get(Calendar.YEAR);
                                                    int year = yearValue.intValue();
                                                    int age = curYear - year + 1;
                                                    newRiskBean.put("sAge", Long.valueOf(age));
                                                } else if (riskSysName.equalsIgnoreCase("InsC_SMR_Obj_R")) {
                                                    // даты для работы с 'СМР: Коэффициент срока проведения работ'
                                                    if (newRiskBean.get("workStartDATE") != null) {
                                                        Date workStartDate = (Date) parseAnyDate(newRiskBean.get("workStartDATE"), Date.class, "workStartDATE", true);
                                                        newRiskBean.put("workStartDATE", workStartDate);
                                                    }
                                                    if (newRiskBean.get("workFinishDATE") != null) {
                                                        Date workFinishDate = (Date) parseAnyDate(newRiskBean.get("workFinishDATE"), Date.class, "workFinishDATE", true);
                                                        newRiskBean.put("workFinishDATE", workFinishDate);
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
        }
        return result;
    }

    /*
     Обработка результатов калькулятора (расчет общей премии, перенос коэффициентов выше по иерархии для сохранения в показатели)
     */
    private void processCalcResult(List<Map<String, Object>> resultInsObjGroupList, List<Map<String, Object>> sourceInsObjGroupList, Map<String, Object> contract) {

        Map<String, Map<String, Object>> sourceInsObjGroupListAsMapBySysName = new HashMap<String, Map<String, Object>>();
        for (Map<String, Object> sourceInsObjGroup : sourceInsObjGroupList) {
            String sourceInsObjGroupSysName = getStringParam(sourceInsObjGroup.get("INSOBJGROUPSYSNAME"));
            if (!sourceInsObjGroupSysName.isEmpty()) {
                sourceInsObjGroupListAsMapBySysName.put(sourceInsObjGroupSysName, sourceInsObjGroup);
            }
        }

        Double contractPremValue = 0.0;
        Double contractInsAmValue = 0.0;
        for (Map<String, Object> insObjGroupBean : resultInsObjGroupList) {
            //Map<String, Object> sourceObjGroupBean = sourceInsObjGroupList.get(i);
            Long sourceObjGroupIndex = getLongParam(insObjGroupBean.get("obj_index"));
            if (sourceObjGroupIndex == null) {
                sourceObjGroupIndex = Long.valueOf(resultInsObjGroupList.indexOf(insObjGroupBean));
            }
            Map<String, Object> sourceObjGroupBean = sourceInsObjGroupList.get(sourceObjGroupIndex.intValue());
            if (sourceObjGroupBean != null) {
                Map<String, Object> coeffMap = new HashMap<String, Object>();
                List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupBean.get("OBJLIST");
                List<Map<String, Object>> sourceObjList = (List<Map<String, Object>>) sourceObjGroupBean.get("OBJLIST");
                if ((objList != null) && (objList.size() > 0)) {
                    Double insObjGroupPremValue = 0.0;
                    for (int j = 0; j < objList.size(); j++) {
                        Map<String, Object> objBean = objList.get(j);
                        Long sourceObjIndex = getLongParam(objBean.get("obj_index"));
                        if (sourceObjIndex == null) {
                            sourceObjIndex = Long.valueOf(j);
                        }
                        Map<String, Object> sourceObjBean = sourceObjList.get(sourceObjIndex.intValue());

                        Map<String, Object> insObjMap = (Map<String, Object>) objBean.get("INSOBJMAP");
                        Map<String, Object> contrObjMap = (Map<String, Object>) objBean.get("CONTROBJMAP");
                        Map<String, Object> sourceInsObjMap = (Map<String, Object>) sourceObjBean.get("INSOBJMAP");
                        Map<String, Object> sourceContrObjMap = (Map<String, Object>) sourceObjBean.get("CONTROBJMAP");
                        Double objectPremValue = 0.0;
                        if ((insObjMap != null) && (contrObjMap != null)) {
                            List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                            List<Map<String, Object>> sourceContrRiskList = (List<Map<String, Object>>) sourceContrObjMap.get("CONTRRISKLIST");
                            if ((contrRiskList != null) && (contrRiskList.size() > 0)) {
                                for (int k = 0; k < contrRiskList.size(); k++) {
                                    Map<String, Object> riskBean = contrRiskList.get(k);
                                    Long sourceRiskIndex = getLongParam(riskBean.get("obj_index"));
                                    if (sourceRiskIndex == null) {
                                        sourceRiskIndex = Long.valueOf(k);
                                    }
                                    Map<String, Object> sourceRiskBean = sourceContrRiskList.get(sourceRiskIndex.intValue());

                                    if (riskBean.get("premValue") != null) {
                                        Double premValue = Double.valueOf(riskBean.get("premValue").toString());
                                        objectPremValue += premValue;
                                        sourceRiskBean.put("PREMVALUE", riskBean.get("premValue"));
                                    }
                                    coeffMap.clear();
                                    coeffMap.put("kBase", riskBean.get("kBase"));
                                    coeffMap.put("kDop", riskBean.get("kDop"));
                                    coeffMap.put("kSeason", riskBean.get("kSeason"));
                                    coeffMap.put("kTerritory", riskBean.get("kTerritory"));
                                    coeffMap.put("kPlace", riskBean.get("kPlace"));
                                    coeffMap.put("kFranchise", riskBean.get("kFranchise"));
                                    coeffMap.put("kGuard", riskBean.get("kGuard"));
                                    coeffMap.put("kCovering", riskBean.get("kCovering"));
                                    coeffMap.put("kWall", riskBean.get("kWall"));
                                    coeffMap.put("kRepair", riskBean.get("kRepair"));
                                    coeffMap.put("kArea", riskBean.get("kArea"));
                                    coeffMap.put("kFloor", riskBean.get("kFloor"));
                                    coeffMap.put("kYear", riskBean.get("kYear"));
                                    coeffMap.put("kUse", riskBean.get("kUse"));
                                    sourceRiskBean.putAll(coeffMap);
                                }
                                sourceContrObjMap.put("PREMVALUE", objectPremValue);
                                sourceInsObjMap.putAll(coeffMap);
                            }
                        }
                        contractPremValue += objectPremValue;
                        insObjGroupPremValue += objectPremValue;
                    }
                    sourceObjGroupBean.putAll(coeffMap);
                    sourceObjGroupBean.put("PREMVALUE", insObjGroupPremValue);

                }
                Double groupInsAmValue = roundSum(getDoubleParam(sourceObjGroupBean.get("INSAMVALUE")));
                contractInsAmValue += groupInsAmValue;
            }
        }
        if (contract != null) {
            //contract.put("PREMVALUE", contractPremValue);
            setOverridedParam(contract, "PREMVALUE", roundSum(contractPremValue), true);
            setOverridedParam(contract, "INSAMVALUE", roundSum(contractInsAmValue), true);
        }
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
        startDateGC.setTime(documentDateGC.getTime());
        startDateGC.add(Calendar.DATE, 1);
        contract.put("STARTDATE", startDateGC.getTime());

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
            programCode = "00001";
            contractExtValues.put("insuranceProgram", programCode);
        }

        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор (после регистрации продукта в БД)
        //updateContractInsuranceProductStructure(contract, product, getStringParam(contractExtValues.get("insuranceProgram")), login, password);
        
        // формирование или обновление (в зависимости от переданного параметра, по умолчанию - обновление без создания недостающих элементов)
        // структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор
        boolean isMissingStructsCreated = getBooleanParam(contract.get("ISMISSINGSTRUCTSCREATED"), false); // флаг создания недостающих элементов структуры страхового продукта договора
        logger.debug("Is missing contract insurance product structure will be created (ISMISSINGSTRUCTSCREATED): " + isMissingStructsCreated);
        updateContractInsuranceProductStructure(contract, product, false, getStringParam(contractExtValues.get("insuranceProgram")), isMissingStructsCreated, login, password);

        // формирование сумм и вызов калькулятора для рассчета сумм
        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        calcParams.put("insObjGroupList", prepareInsObjGroupListForCalc(insObjGroupList, contract));
        calcParams.put("CALCVERID", product.get("CALCVERID"));
        logger.debug("calcParams: " + calcParams);
        Map<String, Object> calcRes = this.callService(Constants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcParams, login, password);
        logger.debug("calcRes: " + calcRes);
        List<Map<String, Object>> resultInsObjGroupList = (List<Map<String, Object>>) calcRes.get("insObjGroupList");
        if ((resultInsObjGroupList != null) && (resultInsObjGroupList.size() > 0)) {
            processCalcResult(resultInsObjGroupList, insObjGroupList, contract);
        }

        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        Object currentRowStatus = contract.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
            contract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
        }

        return contract;
    }

    /**
     * Метод для сохранения договора по продукту Страховое ателье.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInsComContractPrepareToSave(Map<String, Object> params) throws Exception {

        logger.debug("before dsB2BISNCOMContractPrepareToSave");

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

        logger.debug("after dsB2BISNCOMContractPrepareToSave");

        return result;
    }

    protected Long getInsComProdConfID(String login, String password) throws Exception {
        if (insComProductConfigID == null) {
            Map<String, Object> callParams = new HashMap<String, Object>();
            callParams.put(RETURN_AS_HASH_MAP, "TRUE");
            callParams.put("SETTINGSYSNAME", "inscomProdConfID");
            Map<String, Object> sysSettings = this.callService(COREWS, "getSysSettingBySysName", callParams, login, password);
            if ((sysSettings.get("SETTINGVALUE") != null)) {
                insComProductConfigID = Long.valueOf(sysSettings.get("SETTINGVALUE").toString());
            }
        }
        return insComProductConfigID;
    }

    protected Long getInsComCalculatorVersion(String login, String password) throws Exception {
        if (insComCalculatorVersionID == null) {
            Map<String, Object> callParams = new HashMap<String, Object>();
            callParams.put("PRODCONFID", getInsComProdConfID(login, password));
            callParams.put(RETURN_AS_HASH_MAP, "TRUE");
            Map<String, Object> prodVals = this.callService(B2BPOSWS, "dsB2BProductConfigBrowseListByParam", callParams, login, password);
            if ((prodVals != null) && (prodVals.get("CALCVERID") != null) && (prodVals.get("CALCVERID") instanceof Long)) {
                insComCalculatorVersionID = (Long) prodVals.get("CALCVERID");
            }
        }
        return insComCalculatorVersionID;
    }

    private Map<String, Object> getInsComCalculatorHandbookRecord(String hbName, String hbParamName, Object hbParamValue, String login, String password) throws Exception {
        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put(hbParamName, hbParamValue);
        Map<String, Object> hbRecord = getInsComCalculatorHandbookRecord(hbName, hbParams, login, password);
        return hbRecord;
    }

    private List<Map<String, Object>> getInsComCalculatorHandbookData(String hbName, String login, String password) throws Exception {
        //Map<String, Object> hbParams = new HashMap<String, Object>();
        Map<String, Object> callParams = new HashMap<String, Object>();
        callParams.put("CALCVERID", getInsComCalculatorVersion(login, password));
        callParams.put("NAME", hbName);
        //callParams.put("PARAMS", hbParams);
        Map<String, Object> hbDataResult = this.callService(INSTARIFICATORWS, "dsGetCalculatorHandbookData", callParams, login, password);
        List<Map<String, Object>> hbData = WsUtils.getListFromResultMap(hbDataResult);
        return hbData;
    }

    private Map<String, Object> getInsComCalculatorHandbookRecord(String hbName, Map<String, Object> hbParams, String login, String password) throws Exception {
        Map<String, Object> callParams = new HashMap<String, Object>();
        callParams.put("CALCVERID", getInsComCalculatorVersion(login, password));
        callParams.put("NAME", hbName);
        callParams.put("PARAMS", hbParams);
        callParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> hbRecord = this.callService(INSTARIFICATORWS, "dsGetCalculatorHandbookData", callParams, login, password);
        return hbRecord;
    }

    protected void resolveHandBook(Map<String, Object> data, Long calcVerId, String hbName, String hbKeyFieldName, String hbTextFieldName, String storeFieldName, String login, String password) throws Exception {
        resolveHandBook(data, storeFieldName, hbName, hbKeyFieldName, hbTextFieldName, "", login, password);
    }

    protected void resolveHandBook(Map<String, Object> propertiesMap, String propertyName, String hbName, String hbKeyFieldName, String hbTextFieldName, String hbSumFieldName, String login, String password) throws Exception {
        logger.debug("Handbook resolving...");
        logger.debug("Handbook name: " + hbName);
        logger.debug("Calculator version id: " + getInsComCalculatorVersion(login, password));
        logger.debug("Field name: " + propertyName);
        Object storeValue = propertiesMap.get(propertyName);
        logger.debug("Field raw value: " + storeValue);
        if (storeValue != null) {
            /*Map<String, Object> qParams = new HashMap<String, Object>();
             qParams.put(RETURN_AS_HASH_MAP, true);
             qParams.put("CALCVERID", calcVerId);
             qParams.put("NAME", hbName);
             Map<String, Object> cParams = new HashMap<String, Object>();
             cParams.put(hbKeyFieldName, storeValue);
             qParams.put("PARAMS", cParams);
             Map<String, Object> qRes = this.callService(INSTARIFICATORWS, "dsGetCalculatorHandbookData", qParams, login, password);*/
            Map<String, Object> hbRecord = getInsComCalculatorHandbookRecord(hbName, hbKeyFieldName, storeValue, login, password);
            logger.debug("Handbook string field name: " + hbTextFieldName);
            Object nameValue = hbRecord.get(hbTextFieldName);
            logger.debug("Handbook string field value: " + nameValue);
            String newKeyNameBase = propertyName.replace("Hid", "");
            String newKeyNameStr = newKeyNameBase + "STR";
            if (nameValue != null) {
                //logger.debug("Handbook text field name: " + storeFieldName);
                Object newValue = nameValue;
                logger.debug("Added by key name: " + newKeyNameStr);

                Long isDescribed = getLongParam(hbRecord.get("isDescribed"));
                if ((isDescribed != null) && (isDescribed.equals(1L)) && (newValue instanceof String)) {
                    String newValueStr = newValue.toString();
                    String propertyDescriptionName = propertyName + "OtherDescr";
                    String propertyDescriptionValue = getStringParam(propertiesMap.get(propertyDescriptionName));
                    if (!propertyDescriptionValue.isEmpty()) {
                        newValueStr = newValueStr + " (" + propertyDescriptionValue + ")";
                        logger.debug("Handbook string field value with description (from " + propertyDescriptionName + "): " + newValueStr);
                        newValue = newValueStr;
                    } else {
                        logger.debug("No description (in " + propertyDescriptionName + ") was found for handbook record tagged as described");
                    }
                }

                propertiesMap.put(newKeyNameStr, newValue);
                if (!hbSumFieldName.isEmpty()) {
                    logger.debug("Handbook sum field name: " + hbSumFieldName);
                    Object newSum = hbRecord.get(hbSumFieldName);
                    logger.debug("Handbook sum field value: " + newSum);
                    String newKeyNameSum = newKeyNameBase + "VALUE";
                    logger.debug("Added by key name: " + hbSumFieldName);
                    propertiesMap.put(newKeyNameSum, newSum);
                }
            } else {
                propertiesMap.put(newKeyNameStr, "");
            }
        }
        logger.debug("Handbook resolving finished.");
    }

    // справочник ОПФ
    private List<Map<String, Object>> getOPFList(String login, String password) throws Exception {
        if (OPFList == null) {
            Map<String, Object> callParams = new HashMap<String, Object>();
            callParams.put("ReferenceName", "Справочник ОПФ");
            callParams.put("ReferenceGroupName", "Справочники клиентской базы");
            Map<String, Object> callResult = this.callService(REFWS_SERVICE_NAME, "refItemGetListByParams", callParams, login, password);
            //List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            if (callResult.get(RESULT) != null) {
                if (callResult.get(RESULT) instanceof List) {
                    List<Map<String, Object>> callResultList = WsUtils.getListFromResultMap(callResult);
                    OPFList = callResultList;
                }
            }
        }
        return OPFList;
    }

    private String getOPFListItemNameByReferenceItemCode(Long itemCode, String login, String password) throws Exception {
        String name = "";
        List<Map<String, Object>> list = getOPFList(login, password);
        for (Map<String, Object> item : list) {
            Long code = getLongParam(item.get("ReferenceItemCode"));
            if (itemCode.equals(code)) {
                name = getStringParam(item.get("ReferenceItemName"));
                return name;
            }
        }
        return name;
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
    public Map<String, Object> dsB2BInsComPrintDocDataProvider(Map<String, Object> params) throws Exception {

        logger.debug("");
        logger.debug("dsB2BInsComPrintDocDataProvider start...");

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        // загрузка данных договора базовой версией поставщика
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> contract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBasePrintDocDataProvider", params, login, password);

        resolveContractRegion(contract, login, password);

        resolveContractExtProperties(contract, login, password);

        resolveParticipantsProperties(contract, login, password);

        //Map<String, Object> callParams = new HashMap<String, Object>();
        //callParams.put(RETURN_AS_HASH_MAP, "TRUE");
        //callParams.put("SETTINGSYSNAME", "inscomProdConfID");
        //Map<String, Object> sysSettings = this.callService(COREWS, "getSysSettingBySysName", callParams, login, password);
        //if ((sysSettings.get("SETTINGVALUE") != null)) {
        //Long prodconfigid = Long.valueOf(sysSettings.get("SETTINGVALUE").toString());
        //callParams.clear();
        //callParams.put("PRODCONFID", prodconfigid);
        //callParams.put(RETURN_AS_HASH_MAP, "TRUE");
        //Map<String, Object> prodVals = this.callService(B2BPOSWS, "dsB2BProductConfigBrowseListByParam", callParams, login, password);
        //if ((prodVals != null) && (prodVals.get("CALCVERID") != null) && (prodVals.get("CALCVERID") instanceof Long)) {
        //Long calcVerId = (Long) prodVals.get("CALCVERID");
        Long calcVerId = getInsComCalculatorVersion(login, password);
        if (calcVerId != null) {
            List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
            if (insObjGroupList != null) {
                for (Map<String, Object> insObjGroup : insObjGroupList) {
                    String insObjGroupSysName = getStringParam(insObjGroup.get("INSOBJGROUPSYSNAME"));
                    if (!insObjGroupSysName.isEmpty()) {
                        logger.debug("---");
                        logger.debug("Processing insurance object group...");
                        logger.debug("System name (INSOBJGROUPSYSNAME): " + insObjGroupSysName);
                        logger.debug("---");

                        unifyPropertiesNames(insObjGroup);

                        setDefaultLongPropertiesValues(insObjGroup);

                        resolveSimplePropertiesByHandbooksInfo(insObjGroup, login, password);

                        resolveCheckboxGroupsProperties(insObjGroup, login, password);

                        resolveStringedLists(insObjGroup);

                        setDefaultStringPropertiesValues(insObjGroup);

                        //resolveCheckboxGroupsProperties(insObjGroup, login, password);
                        //logger.debug("---");
                        //resolveHandBook(insObjGroup, calcVerId, "inscom.insAmValueBase", "hid", "name", "sumBasis", login, password);
                        //resolveHandBook(insObjGroup, calcVerId, "inscom.insAmValueBase", "hid", "name", "sumBasis", login, password);
                        //resolveHandBook(insObjGroup, calcVerId, "inscom.property.kFranchise", "hid", "name", "franchise", login, password);
                        //String propertySysName = "InsC_Prp";
                        //if (insObjGroupSysName.equalsIgnoreCase(propertySysName)) {
                        //    //resolveHandBook(insObjGroup, calcVerId, "inscom.property.kArea", "hid", "range", "kAreaHid", login, password);
                        //    //resolveHandBook(insObjGroup, calcVerId, "inscom.property.kCovering", "hid", "coveringMaterial", "kCoveringHid", login, password);
                        //    //resolveHandBook(insObjGroup, calcVerId, "inscom.property.kFloor", "hid", "range", "kFloorHid", login, password);
                        //    //resolveHandBook(insObjGroup, calcVerId, "inscom.property.kFranchise", "hid", "range", "kFranchiseHid", login, password);
                        //    //resolveHandBook(insObjGroup, calcVerId, "inscom.property.kGuard", "hid", "guard", "kGuardHid", login, password);
                        //    //resolveHandBook(insObjGroup, calcVerId, "inscom.property.kRepair", "hid", "variant", "kRepairHid", login, password);
                        //    //resolveHandBook(insObjGroup, calcVerId, "inscom.property.kUse", "hid", "objectType", "kUseHid", login, password);
                        //    //resolveHandBook(insObjGroup, calcVerId, "inscom.property.kWall", "hid", "wallsMaterial", "kWallHid", login, password);
                        //    //resolveHandBook(insObjGroup, calcVerId, "inscom.property.kYear", "hid", "range", "kYearHid", login, password);
                        //}
                        String specEquipSysName = "InsC_sequip";
                        if (insObjGroupSysName.equalsIgnoreCase(specEquipSysName)) {
                            resolveHandBook(insObjGroup, calcVerId, "B2B.InsCom.SpecEquip.kFranchise", "hid", "range", "kFranchiseHid", login, password);
                            resolveHandBook(insObjGroup, calcVerId, "B2B.InsCom.SpecEquip.kPark", "hid", "range", "kParkHid", login, password);
                            resolveHandBook(insObjGroup, calcVerId, "B2B.InsCom.SpecEquip.kPlace", "hid", "range", "kPlaceHid", login, password);
                            resolveHandBook(insObjGroup, calcVerId, "B2B.InsCom.SpecEquip.kSeason", "hid", "variant", "kSeasonHid", login, password);
                            resolveHandBook(insObjGroup, calcVerId, "B2B.InsCom.SpecEquip.kTerritory", "hid", "insTerritory", "kTerritoryHid", login, password);
                            if (insObjGroup.get("OBJLIST") != null) {
                                List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroup.get("OBJLIST");
                                for (Map<String, Object> obj : objList) {
                                    resolveHandBook(obj, calcVerId, "B2B.InsCom.SpecEquip.kFranchise", "hid", "range", "kFranchiseHid", login, password);
                                    resolveHandBook(obj, calcVerId, "B2B.InsCom.SpecEquip.kPark", "hid", "range", "kParkHid", login, password);
                                    resolveHandBook(obj, calcVerId, "B2B.InsCom.SpecEquip.kPlace", "hid", "range", "kPlaceHid", login, password);
                                    resolveHandBook(obj, calcVerId, "B2B.InsCom.SpecEquip.kSeason", "hid", "variant", "kSeasonHid", login, password);
                                    resolveHandBook(obj, calcVerId, "B2B.InsCom.SpecEquip.kTerritory", "hid", "insTerritory", "kTerritoryHid", login, password);
                                }
                            }
                        }

                        genAdditionalSumEntries(insObjGroup);

                        List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroup.get("OBJLIST");
                        if (objList != null) {
                            for (Map<String, Object> obj : objList) {
                                Map<String, Object> insObjMap = (Map<String, Object>) obj.get("INSOBJMAP");
                                if (insObjMap != null) {
                                    unifyPropertiesNames(insObjMap);
                                    setDefaultLongPropertiesValues(insObjMap);
                                    resolveSimplePropertiesByHandbooksInfo(insObjMap, login, password);
                                    resolveStringedLists(insObjMap);
                                    setDefaultStringPropertiesValues(insObjMap);
                                }
                            }
                        }

                    }
                }
            }
        }
        //}
        //}

        contract.put(RETURN_AS_HASH_MAP, true);
        contract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractTextSums", contract, login, password);

        genDateStrs(contract, "*");

        //contract.put("PRODUCTMAP", DELETED_UNUSED_BY_REPORT_CONTENT);
        deleteUnusedByReportContent(contract, "PRODUCTMAP", "*");

        logger.debug("dsB2BInsComPrintDocDataProvider end");

        return contract;
    }

    private void deleteUnusedByReportContent(Map<String, Object> dataMap, String deletedContentKeyName, String fullPathToDeletedContentRoot) {
        dataMap.put(deletedContentKeyName, DELETED_UNUSED_BY_REPORT_CONTENT);
        logger.debug("Content of result with key name '" + fullPathToDeletedContentRoot + "." + deletedContentKeyName + "' was deleted from result - this data will not be used by report.");
    }

    private void resolveParticipantsProperties(Map<String, Object> contract, String login, String password) throws Exception {
        for (String participantNode : PARTICIPANT_NODES) {
            String dataKeyName = participantNode + "MAP";
            //String idKeyName = participantNode + "ID";
            Map<String, Object> participant = (Map<String, Object>) contract.get(dataKeyName);
            if (participant != null) {
                Long opfCode = getLongParam(participant.get("OPF"));
                if (opfCode != null) {
                    String opfName = getOPFListItemNameByReferenceItemCode(opfCode, login, password);
                    participant.put("OPF" + "STR", opfName);
                } else if (participant.get("PARTICIPANTTYPE") != null) {
                    Long partType = getLongParam(participant.get("PARTICIPANTTYPE"));
                    // если физик
                    if (partType.longValue() == 1l) {
                        if (participant.get("ISBUSINESSMAN") != null) {
                            Long isBusinessman = getLongParam(participant.get("ISBUSINESSMAN"));
                            // если бизнесмен (ИП)
                            if (isBusinessman.longValue() == 1l) {

                                if (participant.get("extAttributeList2") != null) {
                                    List<Map<String, Object>> extAttrList = (List<Map<String, Object>>) participant.get("extAttributeList2");
                                    for (Map<String, Object> extAttrMap : extAttrList) {
                                        if (extAttrMap.get("EXTATT_SYSNAME") != null) {
                                            if ("OPF".equalsIgnoreCase(extAttrMap.get("EXTATT_SYSNAME").toString())) {

                                                if (extAttrMap.get("EXTATTVAL_VALUE") != null) {
                                                    opfCode = getLongParam(extAttrMap.get("EXTATTVAL_VALUE"));
                                                    if (opfCode != null) {
                                                        String opfName = getOPFListItemNameByReferenceItemCode(opfCode, login, password);
                                                        participant.put("OPF" + "STR", opfName);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } // если ОПФ нет в партиципанте, то возможно это ИП. т.е. физик. т.е. у него ОПФкостыль в extAttributeList.
                // пробуем получить оттуда.
                //participant.put("extAttributeList", DELETED_UNUSED_BY_REPORT_CONTENT);
                deleteUnusedByReportContent(participant, "extAttributeList", "*." + dataKeyName);
            }
        }
    }

    private void resolveContractExtProperties(Map<String, Object> contract, String login, String password) throws Exception {
        Map<String, Object> contractExtProperties = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (contractExtProperties != null) {
            resolveSimplePropertiesByHandbooksInfo(contractExtProperties, login, password);
        }
    }

    private void resolveCheckboxGroupsProperties(Map<String, Object> propertiesMap, String login, String password) throws Exception {

        Object propertiesHBDataVerID = propertiesMap.get("HBDATAVERID");
        logger.debug("Handbook version (HBDATAVERID): " + propertiesHBDataVerID);

        Map<String, Object> resolvedHBCheckboxGroupsParams = new HashMap<String, Object>();
        resolvedHBCheckboxGroupsParams.put("HBDATAVERID", propertiesHBDataVerID);        
        resolvedHBCheckboxGroupsParams.put("NOTELIKE", CHECKBOX_GROUP_HB_NOTE_MASK);
        Map<String, Object> resolvedHBCheckboxGroupsResult = this.selectQuery("dsB2BInsHBPropDescrBrowseListByParamEx", resolvedHBCheckboxGroupsParams);
        List<Map<String, Object>> resolvedHBCheckboxGroupsList = WsUtils.getListFromResultMap(resolvedHBCheckboxGroupsResult);
        HashSet<String> checkboxGroupsHBSet = new HashSet<String>();
        Map<String, String> fieldNotesByPropertyName = new HashMap<String, String>();
        for (Map<String, Object> checkboxGroupMap : resolvedHBCheckboxGroupsList) {
            String note = getStringParam(checkboxGroupMap.get("NOTE"));
            checkboxGroupsHBSet.add(note);
            String propertyName = getStringParam(checkboxGroupMap.get("NAME"));
            fieldNotesByPropertyName.put(propertyName, note);
        }
        logger.debug("Checkbox group properties, resolved from handbooks: " + checkboxGroupsHBSet);

        for (String checkboxGroupInfo : checkboxGroupsHBSet) {
            String[] checkboxGroupInfoArr = checkboxGroupInfo.split(";");
            String checkboxGroupName = checkboxGroupInfoArr[0].split(":")[1];
            logger.debug("Checkbox group name: " + checkboxGroupName);
            String hbName = checkboxGroupInfoArr[1];
            logger.debug("Handbook name: " + hbName);
            String checkboxSysNameHBField = checkboxGroupInfoArr[2];
            logger.debug("Checkbox system name handbook field: " + checkboxSysNameHBField);
            String checkboxNameHBField = checkboxGroupInfoArr[3];
            logger.debug("Checkbox name handbook field: " + checkboxNameHBField);
            //List<String> checkboxGroupCheckedItemsNames = new ArrayList<String>();
            ArrayList<Map<String, Object>> checkboxGroupCheckedItemsList = new ArrayList<Map<String, Object>>();
            //List<String> checkboxGroupUncheckedItemsNames = new ArrayList<String>();
            ArrayList<Map<String, Object>> checkboxGroupUncheckedItemsNames = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> hbContent = getInsComCalculatorHandbookData(hbName, login, password);

            StringBuilder checkboxGroupCheckedStrBldr = new StringBuilder();
            StringBuilder checkboxGroupUncheckedStrBldr = new StringBuilder();

            for (Map<String, Object> hbRecord : hbContent) {
                String sysName = getStringParam(hbRecord.get(checkboxSysNameHBField));
                String name = getStringParam(hbRecord.get(checkboxNameHBField));
                if (checkboxGroupInfo.equals(fieldNotesByPropertyName.get(sysName))) {
                    Long isDescribed = getLongParam(hbRecord.get("isDescribed"));
                    logger.debug("Checkbox system name: " + sysName);
                    if (!sysName.isEmpty()) {
                        Long checkboxState = getLongParam(propertiesMap.get(sysName));
                        logger.debug("  Checkbox state: " + checkboxState);
                        if ((isDescribed != null) && (isDescribed.equals(1L))) {
                            String description = getStringParam(propertiesMap.get(sysName + "Descr"));
                            if (!description.isEmpty()) {
                                name = name + " (" + description + ")";
                            }
                        }
                        Map<String, Object> item = new HashMap<String, Object>();
                        item.put("name", name);
                        item.put("sysName", sysName);
                        Long hbItemState = getLongParam(hbRecord.get("isSelected"));
                        logger.debug("  Handbook record checkbox state: " + hbItemState);
                        if (((checkboxState != null) && (checkboxState.equals(1L)))
                            && ((hbItemState == null) || (hbItemState.equals(1L)))) {
                            checkboxGroupCheckedItemsList.add(item);
                            checkboxGroupCheckedStrBldr.append(name).append("; ");
                            logger.debug("  Handbook record info added to checked lists.");
                        } else if (((checkboxState == null) || (checkboxState.equals(0L)))
                                   && ((hbItemState == null) || (hbItemState.equals(0L)))) {
                            checkboxGroupUncheckedItemsNames.add(item);
                            checkboxGroupUncheckedStrBldr.append(name).append("; ");
                            logger.debug("  Handbook record info added to unchecked lists.");
                        } else {
                            logger.debug("  Handbook record checkbox state do not equal contract checkbox state - handbook record skipped.");
                        }
                    }
                }
            }

            String checkboxGroupCheckedName = checkboxGroupName + "CHECKEDLIST";
            propertiesMap.put(checkboxGroupCheckedName, checkboxGroupCheckedItemsList);
            logger.debug("Checkbox group checked items (" + checkboxGroupCheckedName + "): " + checkboxGroupCheckedItemsList);

            String checkboxGroupUncheckedName = checkboxGroupName + "UNCHECKEDLIST";
            logger.debug("Checkbox group unchecked items(" + checkboxGroupUncheckedName + "): " + checkboxGroupUncheckedItemsNames);
            propertiesMap.put(checkboxGroupUncheckedName, checkboxGroupUncheckedItemsNames);

            String checkboxGroupCheckedStrName = checkboxGroupName + "CHECKEDLISTSTR";
            if (checkboxGroupCheckedStrBldr.length() > 2) {
                checkboxGroupCheckedStrBldr.setLength(checkboxGroupCheckedStrBldr.length() - 2);
            }
            String checkboxGroupCheckedStr = checkboxGroupCheckedStrBldr.toString();
            propertiesMap.put(checkboxGroupCheckedStrName, checkboxGroupCheckedStr);
            logger.debug("Checkbox group checked items in string (" + checkboxGroupCheckedStrName + "): " + checkboxGroupCheckedStr);

            String checkboxGroupUncheckedStrName = checkboxGroupName + "UNCHECKEDLISTSTR";
            if (checkboxGroupUncheckedStrBldr.length() > 2) {
                checkboxGroupUncheckedStrBldr.setLength(checkboxGroupUncheckedStrBldr.length() - 2);
            }
            String checkboxGroupUncheckedStr = checkboxGroupUncheckedStrBldr.toString();
            propertiesMap.put(checkboxGroupUncheckedStrName, checkboxGroupUncheckedStr);
            logger.debug("Checkbox group unchecked items in string (" + checkboxGroupUncheckedStrName + "): " + checkboxGroupUncheckedStr);

        }

    }

    private void unifyPropertyName(Map<String, Object> propertiesMap, String propertyName, String unifiedName, Object defaultValue) throws Exception {
        logger.debug("Checking for unifing property with name: " + propertyName);
        if (propertiesMap.get(unifiedName) == null) {
            Object propertyValue = propertiesMap.get(propertyName);
            if (propertyValue != null) {
                logger.debug("Property value: " + propertyValue);
            } else if (defaultValue != null) {
                propertyValue = defaultValue;
                logger.debug("Property value is null - selected default property value: " + defaultValue);
            } else {
                logger.debug("Property value and default value is nulls - unifying skipped");
                return;
            }
            propertiesMap.put(unifiedName, propertyValue);
            logger.debug("Added same value to unified property with name: " + unifiedName);
        } else {
            logger.debug("Property with unified name (" + unifiedName + ") already exists - unifying skipped");
        }
    }

    private void unifyPropertiesNames(Map<String, Object> propertiesMap) throws Exception {
        unifyPropertyName(propertiesMap, "isPledge", "pledge", 0L);
        unifyPropertyName(propertiesMap, "pledgeDate", "pledgeDATE", null);
        unifyPropertyName(propertiesMap, "creditDate", "creditDATE", null);
        //unifyPropertyName(propertiesMap, "INSAMVALUE", "INSAMVALUE", 0L);
    }

    private void resolveSimplePropertiesByHandbooksInfo(Map<String, Object> propertiesMap, String login, String password) throws Exception {
        Object propertiesHBDataVerID = propertiesMap.get("HBDATAVERID");
        logger.debug("Handbook version (HBDATAVERID): " + propertiesHBDataVerID);

        Map<String, Object> resolvedHBPropsParams = new HashMap<String, Object>();
        resolvedHBPropsParams.put("HBDATAVERID", propertiesHBDataVerID);
        resolvedHBPropsParams.put("NOTELIKE",  HBMask);
        Map<String, Object> resolvedHBPropsResult = this.selectQuery("dsB2BInsHBPropDescrBrowseListByParamEx", resolvedHBPropsParams);
        List<Map<String, Object>> resolvedHBPropsList = WsUtils.getListFromResultMap(resolvedHBPropsResult);
        logger.debug("Properties, resolved from handbooks: " + resolvedHBPropsList);

        for (Map<String, Object> property : resolvedHBPropsList) {
            String propertyName = getStringParam(property.get("NAME"));
            logger.debug("");
            logger.debug("Resolved property name: " + propertyName);
            String resolvingInfo = getStringParam(property.get("NOTE"));
            logger.debug("Resolving info (from NOTE): " + resolvingInfo);
            if (!resolvingInfo.isEmpty()) {
                String[] resolvingInfoArray = resolvingInfo.split(";");
                if (resolvingInfoArray.length >= 3) {
                    String hbName = resolvingInfoArray[0];
                    String hbRelationFieldName = resolvingInfoArray[1];
                    String hbStringFieldName = resolvingInfoArray[2];
                    String hbSumFieldName = "";
                    if (resolvingInfoArray.length >= 4) {
                        hbSumFieldName = resolvingInfoArray[3];
                    }
                    if ((!hbName.isEmpty()) && (!hbRelationFieldName.isEmpty()) && (!hbStringFieldName.isEmpty())) {
                        resolveHandBook(propertiesMap, propertyName, hbName, hbRelationFieldName, hbStringFieldName, hbSumFieldName, login, password);
                    }
                }
            }
        }

    }

    private void genAdditionalSumEntries(Map<String, Object> map) {
        Map<String, Object> additionalSumEntries = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if ((key.endsWith("Sum")) || (key.endsWith("Cost")) || (key.endsWith("cost"))) {
                String sumProcessingKeyName = key + "VALUE";
                logger.debug("Found sum-like key name - '" + key + "': duplicated into '" + sumProcessingKeyName + "'.");
                Object value = entry.getValue();
                additionalSumEntries.put(sumProcessingKeyName, value);
            }
        }
        map.putAll(additionalSumEntries);
    }

    private void resolveContractRegion(Map<String, Object> contract, String login, String password) throws Exception {

        String regionCode = getStringParam(contract.get("INSREGIONCODE"));
        logger.debug("Contract insurance region code: " + regionCode);
        if (!regionCode.isEmpty()) {
            Map<String, Object> regionParams = new HashMap<String, Object>();
            regionParams.put("CODE", regionCode);
            regionParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> region = this.callService(B2BPOSWS_SERVICE_NAME, "dsKladrRegionBrowseListByParam", regionParams, login, password);
            logger.debug("Contract insurance region info by this code: " + region);
            String regionType = getStringParam(region.get("TYPE"));
            logger.debug("Contract insurance region type (INSREGIONTYPESTR): " + regionType);
            contract.put("INSREGIONTYPESTR", regionType);
            String regionName = getStringParam(region.get("NAME"));
            logger.debug("Contract insurance region name (INSREGIONNAMESTR): " + regionName);
            //String regionFullName = getStringParam(region.get("FULLNAME"));
            String regionFullName = "";
            if (!regionType.isEmpty()) {
                regionFullName = regionType + ". ";
            }
            regionFullName = regionFullName + regionName;
            logger.debug("Contract insurance region full name (INSREGIONFULLNAMESTR): " + regionFullName);
            contract.put("INSREGIONFULLNAMESTR", regionFullName);
        }

    }

    private void resolveStringedLists(Map<String, Object> propertiesMap) {
        resolveStringedList(propertiesMap, "INSOBJGROUPSYSNAME", "InsC_GO", "payments", 10);
        resolveStringedList(propertiesMap, "INSOBJGROUPSYSNAME", "InsC_SMR", "equipment", 5);
        resolveStringedList(propertiesMap, "INSOBJGROUPSYSNAME", "InsC_SMR", "property", 5);
        resolveStringedList(propertiesMap, "INSOBJSYSNAME", "InsC_Prp_Mov", "movables", 10);
    }

    private void resolveStringedList(Map<String, Object> propertiesMap, String sysNameField, String sysName, String listName, int fieldsCount) {

        String selectedPropertiesMapSysName = getStringParam(propertiesMap.get(sysNameField));

        if (sysName.equals(selectedPropertiesMapSysName)) {

            String fullListStr = "";
            StringBuilder fullListStrBldr = new StringBuilder();
            for (int i = 1; i <= fieldsCount; i++) {
                fullListStrBldr.append(getStringParam(propertiesMap.get(listName + "Str" + i)));
            }
            //logger.debug("");
            logger.debug("Full list json for structure object with system name " + sysName + ": \n\n" + fullListStrBldr.toString() + "\n\n");
            //logger.debug("");

            ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            Double listSum = 0.0;

            if (fullListStrBldr.length() > 4) {
                // отрезка [{" и }]
                fullListStr = fullListStrBldr.substring(3, fullListStrBldr.length() - 2);

                String[] fullListArr = fullListStr.split(Pattern.quote("},{\""));

                String strLimiter = "\"";
                for (String itemStr : fullListArr) {
                    String[] itemPropertiesArr = itemStr.split(Pattern.quote(",\""));
                    Map<String, Object> item = new HashMap<String, Object>();
                    for (String itemPropery : itemPropertiesArr) {
                        if (itemPropery.endsWith(strLimiter)) {
                            itemPropery = itemPropery.substring(0, itemPropery.length() - 1);
                        }
                        String[] itemArr = itemPropery.split(Pattern.quote("\":"));
                        String itemKeyName = itemArr[0];
                        String itemValue = itemArr[1];
                        if (itemValue.startsWith(strLimiter)) {
                            itemValue = itemValue.substring(1, itemValue.length());
                        }
                        //if (itemValue.endsWith(strLimiter)) {
                        //    itemValue = itemValue.substring(0, itemValue.length() - 1);
                        //}

                        logger.debug("");
                        logger.debug("itemKeyName = " + itemKeyName);
                        logger.debug("itemValue = " + itemValue);

                        item.put(itemKeyName, itemValue);

                    }
                    list.add(item);

                    Long cnt = getLongParam(item.get("cnt"));
                    if (cnt == null) {
                        cnt = 1L;
                    }
                    Double cost = getDoubleParam(item.get("cost"));
                    Double subSum = cnt * cost;
                    logger.debug("");
                    logger.debug(String.format("Item sum = %.2f (%d * %.2f)", subSum, cnt, cost));
                    listSum = listSum + subSum;

                }

            }

            logger.debug("");
            String listKeyName = listName + "LIST";
            logger.debug("Resulted list key name: " + listKeyName);
            propertiesMap.put(listKeyName, list);
            String listSumKeyName = listName + "Sum";
            logger.debug(String.format("List total sum (%s) = %.2f", listSumKeyName, listSum));
            propertiesMap.put(listSumKeyName, listSum);
            logger.debug("");

        }

    }

    private void setDefaultLongPropertiesValues(Map<String, Object> properiesMap) {
        for (String keyName : DEFAULTED_KEY_NAMES) {
            if (properiesMap.get(keyName) == null) {
                properiesMap.put(keyName, 0L);
            }
        }
        for (String keyName : DEFAULTED_KEY_NAMES_DOUBLE) {
            if (properiesMap.get(keyName) == null) {
                properiesMap.put(keyName, 0L);
            }
        }

    }

    private void setDefaultStringPropertiesValues(Map<String, Object> properiesMap) {
        for (String keyName : DEFAULTED_KEY_NAMES) {
            String stringValueKeyName = keyName + "STR";
            if (properiesMap.get(stringValueKeyName) == null) {
                properiesMap.put(stringValueKeyName, "");
            }
        }
        for (String keyName : DEFAULTED_STRINGS_KEY_NAMES) {
            if (properiesMap.get(keyName) == null) {
                properiesMap.put(keyName, "");
            }
        }

    }

}
