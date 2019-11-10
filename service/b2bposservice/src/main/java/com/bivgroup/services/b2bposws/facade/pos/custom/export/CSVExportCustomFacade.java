package com.bivgroup.services.b2bposws.facade.pos.custom.export;

import com.bivgroup.seaweedfs.client.AssignParams;
import com.bivgroup.seaweedfs.client.Assignation;
import com.bivgroup.seaweedfs.client.ReplicationStrategy;
import com.bivgroup.seaweedfs.client.WeedFSClient;
import com.bivgroup.seaweedfs.client.WeedFSClientBuilder;
import com.bivgroup.services.b2bposws.system.Constants;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.csvexport.impl.CSVExporterImpl;
import ru.diasoft.services.csvexport.interfaces.CSVExporter;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("CSVExportCustom")
public class CSVExportCustomFacade extends ExportCustomFacade {

    private final Logger logger = Logger.getLogger(this.getClass());
    private static final String B2BPOSWS = Constants.B2BPOSWS;
    public static final String SERVICE_NAME = Constants.B2BPOSWS;

    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final private byte[] Salt = {
        (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
        (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19
    };

    private Map<String, String> listsSysNameFieldsByListNames;
    private Map<String, String> columnNamesByKeyNames;
    private Set<String> alwaysSkippedKeyNames;

    public CSVExportCustomFacade() {

        // протоколирование времени, затраченного на вызовы (влияет только на вызовы через callServiceTimeLogged)
        IS_CALLS_TIME_LOGGED = true;

        alwaysSkippedKeyNames = new HashSet<String>();
        alwaysSkippedKeyNames.add("ROWSTATUS");

        alwaysSkippedKeyNames.add("EXTATTVAL_OBJID");
        alwaysSkippedKeyNames.add("EXTATTVAL_ATTID");
        alwaysSkippedKeyNames.add("EXTATTVAL_ID");
        alwaysSkippedKeyNames.add("EXTATTTYPE_PUBLICNAME");
        alwaysSkippedKeyNames.add("EXTATT_DEFAULTVALUE");
        alwaysSkippedKeyNames.add("EXTATTTYPE_SYSNAME");
        alwaysSkippedKeyNames.add("EXTOBJTYPE_SYSNAME");
        alwaysSkippedKeyNames.add("EXTOBJTYPE_PUBLICNAME");

        alwaysSkippedKeyNames.add("CONTACTPERSONID");
        alwaysSkippedKeyNames.add("CONTACTTYPEID");
        alwaysSkippedKeyNames.add("CONTACTID");

        alwaysSkippedKeyNames.add("LT_CREATEDATE");
        alwaysSkippedKeyNames.add("LT_CREATEDATETIME");
        alwaysSkippedKeyNames.add("CT_CREATEDATE");
        alwaysSkippedKeyNames.add("CT_CREATEDATETIME");

        listsSysNameFieldsByListNames = new HashMap<String, String>();
        listsSysNameFieldsByListNames.put("riskList", "PRODRISKSYSNAME");
        listsSysNameFieldsByListNames.put("documentList", "DOCTYPESYSNAME");
        listsSysNameFieldsByListNames.put("addressList", "ADDRESSTYPESYSNAME");
        listsSysNameFieldsByListNames.put("contactList", "CONTACTTYPESYSNAME");
        listsSysNameFieldsByListNames.put("extAttributeList2", "EXTATT_SYSNAME");
        listsSysNameFieldsByListNames.put("BENEFICIARYLIST", "*"); // * - использовать индекс элемента

        columnNamesByKeyNames = new LinkedHashMap<String, String>();
        columnNamesByKeyNames.put(".PRODUCTMAP.PRODVER.PROD.NAME", "Продукт");
        columnNamesByKeyNames.put(".CONTRNUMBER", "Номер договора");
        columnNamesByKeyNames.put(".STATENAME", "Состояние");
        //columnNamesByKeyNames.put(".PRODPROGID", "Программа");
        columnNamesByKeyNames.put(".PRODPROGSTR", "Программа");
        columnNamesByKeyNames.put(".INSAMVALUE", "Страховая сумма");
        columnNamesByKeyNames.put(".PREMVALUE", "Размер взноса");
        columnNamesByKeyNames.put(".STARTDATE", "Начало срока действия");
        columnNamesByKeyNames.put(".FINISHDATE", "Окончание срока действия");
        //columnNamesByKeyNames.put(".TERMID", "Срок страхования (лет)");
        columnNamesByKeyNames.put(".TERMSTR", "Срок страхования");
        //columnNamesByKeyNames.put(".PAYVARID", "Периодичность взносов");
        columnNamesByKeyNames.put(".PAYVARSTR", "Периодичность взносов");

        //columnNamesByKeyNames.put(".CONTREXTMAP.insurerIsInsured", "Страхователь является застрахованным");
        columnNamesByKeyNames.put(".CONTREXTMAP.insurerIsInsuredSTR", "Страхователь является застрахованным");
        //columnNamesByKeyNames.put(".CONTREXTMAP.insuredGender", "Пол застрахованного");
        // дублируются в застрахованном.
        //columnNamesByKeyNames.put(".CONTREXTMAP.insuredGenderSTR", "Пол застрахованного");
        //columnNamesByKeyNames.put(".CONTREXTMAP.insuredBirthDATE", "Дата рождения застрахованного");
        //columnNamesByKeyNames.put(".INSAMCURRENCYID", "Валюта страхования");
        columnNamesByKeyNames.put(".INSAMCURRENCYSTR", "Валюта страхования");
        columnNamesByKeyNames.put(".CONTREXTMAP.insuredDeclComplianceSTR", "Клиент соответствует декларации застрахованного");
        //columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilot", "Опция АВТОПИЛОТ подключена");
        columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilotSTR", "Опция АВТОПИЛОТ подключена");
        //columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilotTakeProfit", "Автопилот ВВЕРХ (Take profit)");
        columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilotTakeProfitSTR", "Автопилот ВВЕРХ (Take profit)");
        //columnNamesByKeyNames.put(".CONTREXTMAP.autopilotTakeProfitPerc", "Автопилот ВВЕРХ (Take profit) процент");
        columnNamesByKeyNames.put(".CONTREXTMAP.autopilotTakeProfitPercValueWithPct", "Автопилот ВВЕРХ (Take profit) процент");
        //columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilotStopLoss", "Автопилот ВНИЗ (Stop Loss)");
        columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilotStopLossSTR", "Автопилот ВНИЗ (Stop Loss)");
        //columnNamesByKeyNames.put(".CONTREXTMAP.autopilotStopLossPerc", "Автопилот ВНИЗ (Stop Loss) процент");
        columnNamesByKeyNames.put(".CONTREXTMAP.autopilotStopLossPercValueWithPct", "Автопилот ВНИЗ (Stop Loss) процент");
        //columnNamesByKeyNames.put(".CONTREXTMAP.assuranceLevelIncreased", "Повышенный уровень гарантии");
        columnNamesByKeyNames.put(".CONTREXTMAP.assuranceLevelIncreasedSTR", "Повышенный уровень гарантии");
        //columnNamesByKeyNames.put(".CONTREXTMAP.assuranceLevel", "Уровень гарантии");
        columnNamesByKeyNames.put(".CONTREXTMAP.assuranceLevelStr", "Уровень гарантии");

        columnNamesByKeyNames.put(".riskList.survivalDeath.INSAMVALUE", "Дожитие ЗЛ и смерть по любой причине - Страховая сумма");
        columnNamesByKeyNames.put(".riskList.accidentDeath.INSAMVALUE", "Смерть ЗЛ в результате несчастного случая - Страховая сумма");
        //новые системные имена маяка классического
        columnNamesByKeyNames.put(".riskList.RB-ILI0_MAIN_PROGRAM.INSAMVALUE", "Дожитие ЗЛ и смерть по любой причине - Страховая сумма");
        columnNamesByKeyNames.put(".riskList.RB-ILI0_DEATH_DUE_ACC.INSAMVALUE", "Смерть ЗЛ в результате несчастного случая - Страховая сумма");
        // новые системные имена маяка купонного
        columnNamesByKeyNames.put(".riskList.RB_ILIK_MAIN_PROGRAM.INSAMVALUE", "Дожитие ЗЛ и смерть по любой причине - Страховая сумма");
        columnNamesByKeyNames.put(".riskList.RB_ILIK_DEATH_DUE_ACC.INSAMVALUE", "Смерть ЗЛ в результате несчастного случая - Страховая сумма");

        columnNamesByKeyNames.put(".riskList.ILI0_MAIN_PROGRAM.INSAMVALUE", "Дожитие ЗЛ и смерть по любой причине - Страховая сумма");
        columnNamesByKeyNames.put(".riskList.ILI0_DEATH_DUE_ACC.INSAMVALUE", "Смерть ЗЛ в результате несчастного случая - Страховая сумма");

        columnNamesByKeyNames.put(".CONTREXTMAP.deathGSS1Year", "Смерть по любой причине - ГСС в 1-й год страхования");
        columnNamesByKeyNames.put(".CONTREXTMAP.deathGSS2Year", "Смерть по любой причине - ГСС в 2-ой год страхования");
        columnNamesByKeyNames.put(".CONTREXTMAP.deathGSS3Year", "Смерть по любой причине - ГСС в 3-й год страхования");
        columnNamesByKeyNames.put(".CONTREXTMAP.marketRateOfReturn", "Текущая рыночная доходность");

        Map<String, String> addressColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        addressColumnNamesByKeyNames.put(".REGION", "Регион");
        addressColumnNamesByKeyNames.put(".CITY", "Город или населенный пункт");
        addressColumnNamesByKeyNames.put(".STREET", "Улица");
        addressColumnNamesByKeyNames.put(".HOUSE", "Дом литер корпус строение");
        addressColumnNamesByKeyNames.put(".FLAT", "Квартира");
        addressColumnNamesByKeyNames.put(".POSTALCODE", "Индекс");

        Map<String, String> documentColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        //documentColumnNamesByKeyNames.put(".DOCTYPENAME", "Наименование документа");
        documentColumnNamesByKeyNames.put(".DOCSERIES", "Серия");
        documentColumnNamesByKeyNames.put(".DOCNUMBER", "Номер");
        documentColumnNamesByKeyNames.put(".ISSUEDATE", "Дата выдачи");
        documentColumnNamesByKeyNames.put(".ISSUEDBY", "Кем выдан");

        LinkedHashMap<String, String> passportRFColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        passportRFColumnNamesByKeyNames.putAll(documentColumnNamesByKeyNames);
        passportRFColumnNamesByKeyNames.put(".ISSUERCODE", "Код подразделения");

        LinkedHashMap<String, String> foreignAddDocsColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        foreignAddDocsColumnNamesByKeyNames.putAll(documentColumnNamesByKeyNames);
        foreignAddDocsColumnNamesByKeyNames.remove(".ISSUEDATE");
        foreignAddDocsColumnNamesByKeyNames.remove(".ISSUEDBY");

        // Документ на право проживания (пребывания)
        LinkedHashMap<String, String> foreignResDocColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        foreignResDocColumnNamesByKeyNames.putAll(foreignAddDocsColumnNamesByKeyNames);
//        foreignResDocColumnNamesByKeyNames.put(".RESSTARTDATE", "Срок пребывания с");
//        foreignResDocColumnNamesByKeyNames.put(".RESFINISHDATE", "Срок пребывания по");
        foreignResDocColumnNamesByKeyNames.put(".VALIDFROMDATE", "Срок пребывания с");
        foreignResDocColumnNamesByKeyNames.put(".VALIDTODATE", "Срок пребывания по");

        // Миграционная карта
        LinkedHashMap<String, String> foreignMigDocColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        foreignMigDocColumnNamesByKeyNames.putAll(foreignAddDocsColumnNamesByKeyNames);
//        foreignMigDocColumnNamesByKeyNames.put(".MIGSTARTDATE", "Срок пребывания с");
//        foreignMigDocColumnNamesByKeyNames.put(".MIGFINISHDATE", "Срок пребывания по");
        foreignMigDocColumnNamesByKeyNames.put(".VALIDFROMDATE", "Срок пребывания с");
        foreignMigDocColumnNamesByKeyNames.put(".VALIDTODATE", "Срок пребывания по");

        Map<String, String> insurerColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        // Общая информация
        insurerColumnNamesByKeyNames.put(".LASTNAME", "Фамилия");
        insurerColumnNamesByKeyNames.put(".FIRSTNAME", "Имя");
        insurerColumnNamesByKeyNames.put(".MIDDLENAME", "Отчество");
        insurerColumnNamesByKeyNames.put(".BIRTHDATE", "Дата рождения");
        //insurerColumnNamesByKeyNames.put(".GENDER", "Пол");
        insurerColumnNamesByKeyNames.put(".GENDERSTR", "Пол");
        //insurerColumnNamesByKeyNames.put(".CITIZENSHIP", "Гражданство");
        insurerColumnNamesByKeyNames.put(".CITIZENSHIPSTR", "Гражданство");
        insurerColumnNamesByKeyNames.put(".INN", "ИНН");
        //insurerColumnNamesByKeyNames.put(".extAttributeList2.MaritalStatus.EXTATTVAL_VALUE", "Семейное положение");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.MaritalStatus.EXTATTVAL_VALUESTR", "Семейное положение");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.birthPlaceExt.EXTATTVAL_VALUE", "Страна рождения");
        insurerColumnNamesByKeyNames.put(".BIRTHPLACE", "Место рождения");
        //insurerColumnNamesByKeyNames.put(".extAttributeList2.unResident.EXTATTVAL_VALUE", "Нерезидент");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.unResident.EXTATTVAL_VALUESTR", "Нерезидент");
        // Cтатус налогового резидента иностранного государства
        //insurerColumnNamesByKeyNames.put(".extAttributeList2.isTaxResidentUSA.EXTATTVAL_VALUE", "Налоговый резидент США");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.isTaxResidentUSA.EXTATTVAL_VALUESTR", "Налоговый резидент США");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.INNUSA.EXTATTVAL_VALUE", "ИНН США");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.taxResidentOtherCountry.EXTATTVAL_VALUE", "Налоговый резидент другой страны");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.INNOther.EXTATTVAL_VALUE", "ИНН другой страны");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.residencePermitForeignCountry.EXTATTVAL_VALUE", "Вид на жительство в иностранном государстве");
        // документы
        addSubMapLevel(insurerColumnNamesByKeyNames, ".documentList.BornCertificate", "Свидетельство о рождении", documentColumnNamesByKeyNames);
        addSubMapLevel(insurerColumnNamesByKeyNames, ".documentList.PassportRF", "Паспорт гражданина РФ", passportRFColumnNamesByKeyNames);
        addSubMapLevel(insurerColumnNamesByKeyNames, ".documentList.ForeignPassport", "Паспорт иностранного гражданина", documentColumnNamesByKeyNames);
        addSubMapLevel(insurerColumnNamesByKeyNames, ".documentList.MigrationCard", "Миграционная карта", foreignMigDocColumnNamesByKeyNames);
        addSubMapLevel(insurerColumnNamesByKeyNames, ".documentList.РазрВр", "Документ на право проживания", foreignResDocColumnNamesByKeyNames);
        // адреса
        addSubMapLevel(insurerColumnNamesByKeyNames, ".addressList.FactAddress", "Фактический адрес", addressColumnNamesByKeyNames);
        addSubMapLevel(insurerColumnNamesByKeyNames, ".addressList.RegisterAddress", "Адрес регистрации", addressColumnNamesByKeyNames);
        // Сведения о работе
        insurerColumnNamesByKeyNames.put(".extAttributeList2.education.EXTATTVAL_VALUESTR", "Текущая профессия");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.EmployerName.EXTATTVAL_VALUE", "Наименование организации");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.activityBusinessKind.EXTATTVAL_VALUESTR", "Сфера деятельности");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.Position.EXTATTVAL_VALUE", "Должность");
        // Контактные данные
        insurerColumnNamesByKeyNames.put(".contactList.MobilePhone.VALUE", "Контактный телефон");
        insurerColumnNamesByKeyNames.put(".contactList.FactAddressPhone.VALUE", "Дополнительный телефон");
        insurerColumnNamesByKeyNames.put(".contactList.PersonalEmail.VALUE", "Электронная почта");

        addSubMapLevel(columnNamesByKeyNames, ".INSURERMAP", "Страхователь", insurerColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".INSUREDMAP", "Застрахованный", insurerColumnNamesByKeyNames);

        Map<String, String> beneficiaryColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        beneficiaryColumnNamesByKeyNames.put(".RISK", "Риск");
        //beneficiaryColumnNamesByKeyNames.put(".TYPEID", "Тип");
        beneficiaryColumnNamesByKeyNames.put(".TYPESTR", "Тип");
        beneficiaryColumnNamesByKeyNames.put(".PART", "Процент");
        addSubMapLevel(beneficiaryColumnNamesByKeyNames, ".PARTICIPANTMAP", "", insurerColumnNamesByKeyNames);

        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.0", "Выгодоприобретатель 1", beneficiaryColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.1", "Выгодоприобретатель 2", beneficiaryColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.2", "Выгодоприобретатель 3", beneficiaryColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.3", "Выгодоприобретатель 4", beneficiaryColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.4", "Выгодоприобретатель 5", beneficiaryColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.5", "Выгодоприобретатель 6", beneficiaryColumnNamesByKeyNames);

        // в самом конце
        columnNamesByKeyNames.put(".ERROR", "Ошибка");
    }

    private void addSubMapLevel(Map<String, String> mainMap, String parentKey, String parentValue, Map<String, String> subMap) {
        String valuePrefix = parentValue.isEmpty() ? "" : parentValue + " - ";
        for (Map.Entry<String, String> entry : subMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mainMap.put(parentKey + key, valuePrefix + value);
        }
    }

    /*
    protected String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }

    protected String getSeaweedFSUrl() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("SEAWEEDFSURL", "");
        return login;
    }
    */

    protected void flatDataMap(Map<String, Object> flatData, Map<String, Object> data, String dataNodePath) {
        //boolean isTargetClassString = String.class.equals(targetClass);
        //Map<String, Object> additionalTimeEntries = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String keyName = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String keyFullName = dataNodePath + "." + keyName;
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    flatDataMap(flatData, map, keyFullName);
                } else if (value instanceof List) {

                    String sysNameField = listsSysNameFieldsByListNames.get(keyName);
                    if (sysNameField != null) {

                        ArrayList<Object> list = (ArrayList<Object>) value;
                        for (int i = 0; i < list.size(); i++) {
                            Object rawElement = list.get(i);
                            if (rawElement instanceof Map) {
                                Map<String, Object> element = (Map<String, Object>) rawElement;
                                String elementSysName;
                                if (sysNameField.equals("*")) {
                                    elementSysName = Integer.toString(i);
                                } else {
                                    elementSysName = getStringParam(element, sysNameField);
                                }
                                flatDataMap(flatData, element, keyFullName + "." + elementSysName);
                            }
                        }

                    }
                } else if (!alwaysSkippedKeyNames.contains(keyName)) {
                    flatData.put(keyFullName, value);
                    //logger.debug(keyFullName + " = " + value);
                }
            }
        }

    }

    private Map<String, Object> makeFlatContrMap(Map<String, Object> contrMap) {
        Map<String, Object> flatContrMap = new HashMap<String, Object>();
        flatDataMap(flatContrMap, contrMap, "");
        return flatContrMap;
    }

    private Map<String, Object> cleanFlatContrMap(Map<String, Object> flatContrMap) {
        logger.debug("cleanFlatContrMap...");
        Map<String, Object> cleanFlatContrMap = new HashMap<String, Object>();
        for (Map.Entry<String, String> entry : columnNamesByKeyNames.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Object flatContrMapValue = flatContrMap.get(key);
            if ((flatContrMapValue == null) || (flatContrMapValue.toString().isEmpty())) {
                flatContrMapValue = "-";
            }
            //logger.debug(key + " = " + flatContrMapValue);
            cleanFlatContrMap.put(key, flatContrMapValue);
        }
        logger.debug("cleanFlatContrMap finished.");
        return cleanFlatContrMap;
    }

    private List<Map<String, Object>> prepareExportMap(Map<String, Object> flatContrMap) {
        List<Map<String, Object>> preparedExportMapRowBean = new ArrayList<Map<String, Object>>();
        int i = 0;
        for (Map.Entry<String, String> entry : columnNamesByKeyNames.entrySet()) {
            String key = entry.getKey();
            //Object value = entry.getValue();
            Map<String, Object> exportCellBean = new HashMap<String, Object>();
            exportCellBean.put("position", i);
            Object flatContrMapValue = flatContrMap.get(key);
            if ((flatContrMapValue != null) && (flatContrMapValue instanceof Double) && (key.endsWith("VALUE"))) {
                flatContrMapValue = String.format("%.2f", getDoubleParam(flatContrMapValue));
            }
            exportCellBean.put("value", flatContrMapValue);
            preparedExportMapRowBean.add(exportCellBean);
            i++;
        }
        return preparedExportMapRowBean;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBrowseContract4Export2CSV(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> loadRes = null;
        if (params.get("OBJECTID") != null) {
            Long contractID = getLongParamLogged(params, "OBJECTID");
            logger.debug("Export: begin prepare data for contrid = " + contractID);

            Map<String, Object> loadParam = new HashMap<String, Object>();
            loadParam.put("CONTRID", contractID);
            loadParam.put("SKIPDATESRECALC", true);
            loadParam.put(RETURN_AS_HASH_MAP, true);
            //String dataProviderMethodName = "dsB2BSberLifePrintDocDataProvider";
            String dataProviderMethodName = "dsB2BSberLifeExportReportDataProvider";
            try {
                loadRes = this.callServiceTimeLogged(B2BPOSWS, dataProviderMethodName, loadParam, login, password);
            } catch (Exception ex) {
                loadRes = new HashMap<String, Object>();
                loadRes.put("CONTRID", contractID);
                loadRes.put("ERROR", ex.getMessage());
                logger.error(String.format("Exception during getting contract data by %s for contract with CONTRID = %d! Details: ", dataProviderMethodName, contractID), ex);
            }

            // здесь необходимо догрузить сущности не поддержанные универсальной загрузкой.
            // платежи
            if (loadRes != null) {
                if (loadRes.get("CONTRNODEID") != null) {
                    Long contrNodeId = (Long) loadRes.get("CONTRNODEID");
                    Map<String, Object> planParams = new HashMap<String, Object>();
                    planParams.put("CONTRNODEID", contrNodeId);
                    //planParams.put("ReturnAsHashMap", "TRUE");
                    Map<String, Object> qPlanRes = this.callServiceTimeLogged(B2BPOSWS, "dsB2BPaymentFactBrowseListByParam", planParams, login, password);
                    loadRes.put("PAYMENTLIST", WsUtils.getListFromResultMap(qPlanRes));
                }
                // получение графика оплаты
                if (loadRes.get("CONTRID") != null) {
                    Map<String, Object> factParams = new HashMap<String, Object>();
                    Long contrId = Long.valueOf(loadRes.get("CONTRID").toString());
                    factParams.put("CONTRID", contrId);
                    Map<String, Object> qFactRes = this.callServiceTimeLogged(B2BPOSWS, "dsB2BPaymentBrowseListByParam", factParams, login, password);
                    loadRes.put("PAYMENTSCHEDULELIST", qFactRes.get(RESULT));

                }
                //
                Map<String, Object> flatLoadRes = makeFlatContrMap(loadRes);
                Map<String, Object> flatCleanLoadRes = cleanFlatContrMap(flatLoadRes);
                List<Map<String, Object>> preparedExportMapRowBean = prepareExportMap(flatCleanLoadRes);
                loadRes.clear();
                loadRes.put("EXPORTROWBEAN", preparedExportMapRowBean);
            }

            logger.debug("Export: end prepare data for contrid = " + contractID);
        } else if (params.get("EXPORTDATAID") != null) {
            // строковый список идентификаторов объектов
            Long exportDataID = getLongParam(params.get("EXPORTDATAID"));
            logger.debug("Export: begin prepare data for exportDataId = " + exportDataID);
            String objectIDsListStr = getObjectIDsListStrByExportDataID(exportDataID, login, password);

            // подготовка параметров для запрос списка со сведениями объектов
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("CONTRIDLIST", objectIDsListStr);
            Map<String, Object> dataList = null;
            // параметры для постраничных запросов, фомируются angular-гридом
            queryParams.put("PAGE", params.get("PAGE"));
            queryParams.put("ROWSCOUNT", params.get("ROWSCOUNT"));
            queryParams.put("CP_TODAYDATE", new Date());

            // запрос списка со сведениями объектов
            loadRes = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParamExShort", queryParams, login, password);

        } else {
            logger.error("dsB2BBrowseContract4export2XML has not OBJECTID or EXPORTDATAID in params");

        }
        return loadRes;
    }

    protected List<Map<String, Object>> getObjectIDsListByExportDataID(Long exportDataID, String login, String password) throws Exception {

        logger.debug("Getting objects ids list for export data with id (EXPORTDATAID) = " + exportDataID);
        Map<String, Object> contentParams = new HashMap<String, Object>();
        contentParams.put("EXPORTDATAID", exportDataID);
        Map<String, Object> content = this.callService(B2BPOSWS, "dsB2BExportDataContentBrowseListByParam", contentParams, login, password);
        List<Map<String, Object>> objectIDsList = WsUtils.getListFromResultMap(content);
        logger.debug("Objects ids list = " + objectIDsList);

        return objectIDsList;
    }

    // Внимание! dsB2BExportDataCreateNonSQLCSVReport переносить в фасад XLSExportCustomFacade нельзя,
    // потому что будет использоваться columnNamesByKeyNames для XLS (который формируется в XLSExportCustomFacade), а должен - для CSV
    /**/
    @WsMethod(requiredParams = {"EXPORTDATAID", "TEMPLATEID"})
    public Map<String, Object> dsB2BExportDataCreateNonSQLCSVReport(Map<String, Object> params) throws Exception {

        logger.debug("Export data report creating...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        // идентификатор обрабатываемой записи
        Long exportDataID = getLongParam(params.get("EXPORTDATAID"));
        logger.debug("Export data record id (EXPORTDATAID) = " + exportDataID);

        // получение шаблона обрабатываемой записи
        Long templateID = getLongParam(params.get("TEMPLATEID"));
        logger.debug("Template id for this export data (TEMPLATEID) = " + templateID);
        Map<String, Object> template = getExportDataTemplateByID(templateID, login, password);

        // имя метода по получению данных
        String dataMethod = getStringParamLogged(template, "DATAMETHOD");

        // список мап с ИД
        List<Map<String, Object>> idList = getObjectIDsListByExportDataID(exportDataID, login, password);

        // формирование списка со сведениями объектов
        // c протоколированием времени
        long dataListPrepareTimerMs = System.currentTimeMillis();
        logger.debug("Data list(s) preparing started.");

        List<List<Map<String, Object>>> dataList = new ArrayList<List<Map<String, Object>>>();
        for (Map<String, Object> idParam : idList) {
            idParam.put(RETURN_AS_HASH_MAP, true);
            List<Map<String, Object>> dataItem = (List<Map<String, Object>>) this.callServiceAndGetOneValue(B2BPOSWS, dataMethod, idParam, login, password, "EXPORTROWBEAN");
            if (dataItem != null) {
                dataList.add(dataItem);
            }
        }
        //logger.debug("dataList = " + dataList); // отключено, слишком большая запись в протокол

        // протоколирование времени формирования списка со сведениями объектов
        dataListPrepareTimerMs = System.currentTimeMillis() - dataListPrepareTimerMs;
        logger.debug(String.format("Data list(s) preparing executed in %d milliseconds (approximately %.5f seconds).", dataListPrepareTimerMs, ((double) dataListPrepareTimerMs) / 1000.0));

        // подготовка параметров для генерации отчета
        CSVExporter csvExporter = new CSVExporterImpl();
        Map<String, Object> exportParams = new HashMap<String, Object>();
        List<String> headerList = new ArrayList<String>();
        for (Map.Entry<String, String> entry : columnNamesByKeyNames.entrySet()) {
            //String key = entry.getKey();
            String value = entry.getValue();
            headerList.add(value);
        }
        List<Map<String, Object>> exportHeader = csvExporter.prepareExportHeader(headerList);
        List<List<Map<String, Object>>> dataListWithHeader = new ArrayList<List<Map<String, Object>>>();
        dataListWithHeader.add(exportHeader);
        dataListWithHeader.addAll(dataList);
        String exportString = csvExporter.exportMapToCSV(dataListWithHeader, exportParams);

        // генерация отчета
        //String reportName = genExportReport(reportData, reportFormat, login, password);
        String uploadPath = this.getUploadFilePath();
        String fileName = UUID.randomUUID().toString();
        String fileExt = "csv";
        String reportName = String.format("%s.%s", fileName, fileExt);
        String fullFileName = String.format("%s%s", uploadPath, reportName);
        String codePage = "UTF-8";

        BufferedOutputStream bufferedOutput = null;
        FileOutputStream fileOutputStream = null;
        try {
            logger.debug("fileParh: " + uploadPath);
            logger.debug("fileName: " + fileName + "." + fileExt);

            fileOutputStream = new FileOutputStream(fullFileName);
            bufferedOutput = new BufferedOutputStream(fileOutputStream);
            byte[] ba = null;
            if (codePage.equals("")) {
                ba = exportString.getBytes();
            } else {
                ba = exportString.getBytes(codePage);
            }
            bufferedOutput.write(ba);

            int fileSize;
            fileSize = ba.length;
            //remove old files
            boolean needReprint = true;
            if (needReprint) {
                // пытаемся получить файл
                logger.debug("remove attach doc for expData: " + exportDataID.toString());
                Map<String, Object> getMap = new HashMap<String, Object>();
                getMap.put("OBJID", exportDataID);
                Map<String, Object> getRes = this.callService(Constants.B2BPOSWS, "dsB2BExportData_BinaryFile_BinaryFileBrowseListByParam", getMap, login, password);
                if (getRes != null) {
                    if (getRes.get(RESULT) != null) {
                        List<Map<String, Object>> binFileList = (List<Map<String, Object>>) getRes.get(RESULT);
                        if (!binFileList.isEmpty()) {
                            logger.debug("binFile for remove: " + binFileList.size());
                            for (Map<String, Object> binFile : binFileList) {
                                if (binFile.get("BINFILEID") != null) {
                                    // если нужна перепечать - грохнуть все прикрепленные к договору документы.
                                    Map<String, Object> delMap = new HashMap<String, Object>();
                                    delMap.put("BINFILEID", binFile.get("BINFILEID"));
                                    this.callService(Constants.B2BPOSWS, "dsB2BExportData_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
                                }
                            }

                        }
                    }
                }
            }
            String expDataFilePath;
            String expDataFileName = reportName;
            if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
                String masterUrlString = getSeaweedFSUrl();
                URL masterURL = new URL(masterUrlString);
                WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                Assignation a = client.assign(new AssignParams("b2battach", ReplicationStrategy.TwiceOnRack));
                int size = client.write(a.weedFSFile, a.location, new FileInputStream(new File(fileName + fileExt)), expDataFileName);
                if (size == 0) {
                    throw new Exception("Unable to write file to SeaweedFS");
                }
                expDataFilePath = a.weedFSFile.fid;
            } else {
                expDataFilePath = fileName + "." + fileExt;
            }
            Map<String, Object> expDataBinParams = new HashMap<String, Object>();
            expDataBinParams.put("OBJID", exportDataID);
            expDataBinParams.put("FILENAME", expDataFileName);
            expDataBinParams.put("FILEPATH", expDataFilePath);
            expDataBinParams.put("FILESIZE", fileSize);
            expDataBinParams.put("FILETYPEID", 1015);
            expDataBinParams.put("FILETYPENAME", reportName);
            this.callService(B2BPOSWS, "dsB2BExportData_BinaryFile_createBinaryFileInfo", expDataBinParams, login, password);

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                if (bufferedOutput != null) {
                    bufferedOutput.flush();
                    bufferedOutput.close();
                }
            } catch (IOException ex) {
            }
        }

        // шифрование имен файлов отчета для возврата в angular-интерфейс
        String encryptedFileNamesStr = getEncryptedFileNamesStr(reportName);
        if (!encryptedFileNamesStr.isEmpty()) {
            result.put("ENCRIPTEDFILENAME", encryptedFileNamesStr);
        }

        // сгенерированный отчет выдать пользователю в интерфейсе
        logger.debug("Export data report creating finish.");
        return result;

    }
    /**/

}
