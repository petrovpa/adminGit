package com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider;


import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BReasonChangeDataProviderPersonalDataContractors")
public class B2BReasonChangeDataProviderPersonalDataContractorsFacade extends B2BReasonChangeDataProviderPersonCustomFacade {
    private static final String ATTR_PARAM_NAME_SYS_NAME = "attrSysName";
    private static final String ATTR_OLD_PARAM_NAME = "attrOldValStr";
    private static final String ATTR_NEW_PARAM_NAME = "attrNewValStr";

    private static final String CHANGEPERSDATAMAP_PARAM_NAME = "CHANGEPERSDATAMAP";
    private static final String CONTACTTYPESYSNAME_PARAM_NAME = "CONTACTTYPESYSNAME";
    private static final String PERSONMAP_PARAM_NAME = "PERSONMAP";

    private static final String INSURER_PERSON_TYPE = "INSURER";
    private static final String INSURED_PERSON_TYPE = "INSURED";
    private static final String BENEF_PERSON_TYPE = "BENEF";
    private static final String BIN_DOC_TYPE_SYSNAME = "changePersonalData_Pf";
    private static final String CURINSURER_PERSON_TYPE = "CURINSURER"; // действующий страхователь
    private static final String CURBENEF_PERSON_TYPE = "CURBENEF"; // действующий выгодоприобретатель

    private static final Map<String, String> PERSON_TYPE_MAP;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    private final Logger logger = Logger.getLogger(this.getClass());

    static {
        PERSON_TYPE_MAP = new HashMap<>();
        PERSON_TYPE_MAP.put(INSURER_PERSON_TYPE, "страхователя");
        PERSON_TYPE_MAP.put(CURINSURER_PERSON_TYPE, "страхователя");
        PERSON_TYPE_MAP.put(INSURED_PERSON_TYPE, "застрахованного");
        PERSON_TYPE_MAP.put(BENEF_PERSON_TYPE, "выгодоприобретателя");
        PERSON_TYPE_MAP.put(CURBENEF_PERSON_TYPE, "выгодоприобретателя");
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderHChangeSurname(Map<String, Object> params) throws Exception {
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> applicantMap = getMapParam(reportData, "APPLICANTMAP");
        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);
        if (error.isEmpty() && !isNotExistContract) {
            error = isVipSegment ? checkPersonType(CURINSURER_PERSON_TYPE, reportData)
                    : checkPersonType(INSURER_PERSON_TYPE, reportData);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                error = vipReasonChangeSurname(reportData, reasonMap, applicantMap);
            } else {
                error = massReasonChangeSurname("страхователе", applicantMap, reportData, reasonMap);
            }
        }

        if (error.isEmpty()) {
            error = addDocumentDateFromFullContractInfo(params, reportData);
        }

        // формирууем Причину изменения фамилии страхователя
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "страхователя", true);
        }
        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderHChangePersData(Map<String, Object> params) throws Exception {
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> applicantMap = getMapParam(reportData, "APPLICANTMAP");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);
        if (error.isEmpty() && !isNotExistContract) {
            error = isVipSegment ? checkPersonType(CURINSURER_PERSON_TYPE, reportData)
                    : checkPersonType(INSURER_PERSON_TYPE, reportData);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                error = vipReasonChangePersData(reportData, reasonMap, applicantMap, isCallFromGate);
            } else {
                error = massReasonChangePersData("страхователе", applicantMap, reportData,
                        reasonMap, isCallFromGate);
            }
        }

        if (error.isEmpty()) {
            error = addDocumentDateFromFullContractInfo(params, reportData);
        }

        // формирууем Причину изменения персональных сведений страхователя
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "страхователя", true);
        }
        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderHChangeContInfo(Map<String, Object> params) throws Exception {
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> applicantMap = getMapParam(reportData, "APPLICANTMAP");
        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);
        if (error.isEmpty() && !isNotExistContract) {
            error = isVipSegment ? checkPersonType(CURINSURER_PERSON_TYPE, reportData)
                    : checkPersonType(INSURER_PERSON_TYPE, reportData);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                error = vipReasonChangeContInfo(reportData, reasonMap, applicantMap);
            } else {
                error = massReasonChangeContInfo("страхователе", applicantMap, reportData, reasonMap);
            }
        }

        if (error.isEmpty()) {
            error = addDocumentDateFromFullContractInfo(params, reportData);
        }

        // формирууем Причину изменения контактной информации страхователя
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "страхователя", true);
        }
        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderHChangeAddress(Map<String, Object> params) throws Exception {
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> applicantMap = getMapParam(reportData, "APPLICANTMAP");
        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);
        if (error.isEmpty() && !isNotExistContract) {
            error = isVipSegment ? checkPersonType(CURINSURER_PERSON_TYPE, reportData)
                    : checkPersonType(INSURER_PERSON_TYPE, reportData);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                error = vipReasonChangeAddress(reportData, reasonMap, applicantMap);
            } else {
                error = massReasonChangeAddress("страхователе", applicantMap, reportData, reasonMap);
            }
        }

        if (error.isEmpty()) {
            error = addDocumentDateFromFullContractInfo(params, reportData);
        }

        // формирууем Причину изменения адреса страхователя
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "страхователя", true);
        }
        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderHChangePassport(Map<String, Object> params) throws Exception {
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> applicantMap = getMapParam(reportData, "APPLICANTMAP");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);
        if (error.isEmpty() && !isNotExistContract) {
            error = isVipSegment ? checkPersonType(CURINSURER_PERSON_TYPE, reportData)
                    : checkPersonType(INSURER_PERSON_TYPE, reportData);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                error = vipReasonChangePassport(reportData, reasonMap, applicantMap, isCallFromGate, login, password);
            } else {
                error = massReasonChangePassport("страхователе", applicantMap, reportData, reasonMap, login, password, isCallFromGate);
            }
        }

        if (error.isEmpty()) {
            error = addDocumentDateFromFullContractInfo(params, reportData);
        }

        // формирууем Причину изменения паспортных данных страхователя
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "страхователя", true);
        }
        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    private String addDocumentDateFromFullContractInfo(Map<String, Object> params, Map<String, Object> reportData) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> fullContract = new HashMap<>();
        Long externalId = getLongParam(params.get("EXTERNALID"));
        String error = "";
        if (reportData.get("DOCUMENTDATE") == null && externalId != null) {
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
            if (error.isEmpty() && !fullContract.isEmpty()) {
                reportData.put("DOCUMENTDATE", fullContract.get("DOCUMENTDATE"));
            }
        }
        return error;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderLaChangeSurname(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);

        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);

        if (error.isEmpty() && !isNotExistContract) {
            error = checkPersonType(INSURED_PERSON_TYPE, reportData);
        }

        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "застрахованного", false);
        }

        Long thirdPartyId = null;
        if (error.isEmpty() && !isNotExistContract) {
            thirdPartyId = getLongParam(reasonMap, "thirdPartyId");
            if (thirdPartyId == null) {
                error = "Не указан идентификатор застрахованного!";
            }
        }

        Map<String, Object> fullContract = null;
        if (error.isEmpty() && !isNotExistContract) {
            Long externalId = getLongParam(params.get("EXTERNALID"));
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }

        Map<String, Object> participantMap = new HashMap<>();
        if (error.isEmpty() && !isNotExistContract) {
            participantMap = getMemberById(fullContract, thirdPartyId, "insured", "застрахованного");
            error = getStringParam(participantMap, ERROR);
        }

        // формирууем Причину изменения фамилии застрахованного
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                error = vipReasonChangeSurname(reportData, reasonMap, participantMap);
            } else {
                error = massReasonChangeSurname("застрахованном", participantMap, reportData, reasonMap);
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderLaChangePersData(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);

        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);
        if (error.isEmpty() && !isNotExistContract) {
            error = checkPersonType(INSURED_PERSON_TYPE, reportData);
        }
        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "застрахованного", false);
        }
        Long thirdPartyId = null;
        if (error.isEmpty() && !isNotExistContract) {
            thirdPartyId = getLongParam(reasonMap, "thirdPartyId");
            if (thirdPartyId == null) {
                error = "Не указан идентификатор застрахованного!";
            }
        }

        Map<String, Object> fullContract = null;
        if (error.isEmpty() && !isNotExistContract) {
            Long externalId = getLongParam(params.get("EXTERNALID"));
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }


        Map<String, Object> participantMap = new HashMap<>();
        if (error.isEmpty() && !isNotExistContract) {
            participantMap = getMemberById(fullContract, thirdPartyId,
                    "insured", "застрахованного");
            error = getStringParam(participantMap, ERROR);
        }

        // формирууем Причину изменения персональных сведений застрахованного
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            boolean isCallFromGate = isCallFromGate(params);
            if (isVipSegment) {
                createPersonList(reportData);
                error = vipReasonChangePersData(reportData, reasonMap, participantMap, isCallFromGate);
            } else {
                error = massReasonChangePersData("застрахованном", participantMap, reportData,
                        reasonMap, isCallFromGate);
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderLaChangeContInfo(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);

        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);

        if (error.isEmpty() && !isNotExistContract) {
            error = checkPersonType(INSURED_PERSON_TYPE, reportData);
        }
        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "застрахованного", false);
        }
        Long thirdPartyId = null;
        if (error.isEmpty() && !isNotExistContract) {
            thirdPartyId = getLongParam(reasonMap, "thirdPartyId");
            if (thirdPartyId == null) {
                error = "Не указан идентификатор застрахованного!";
            }
        }

        Map<String, Object> fullContract = null;
        if (error.isEmpty() && !isNotExistContract) {
            Long externalId = getLongParam(params.get("EXTERNALID"));
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }

        Map<String, Object> participantMap = new HashMap<>();
        if (error.isEmpty() && !isNotExistContract) {
            participantMap = getMemberById(fullContract, thirdPartyId, "insured", "застрахованного");
            error = getStringParam(participantMap, ERROR);
        }

        // формирууем Причину изменения контактной информации застрахованного
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                error = vipReasonChangeContInfo(reportData, reasonMap, participantMap);
            } else {
                error = massReasonChangeContInfo("застрахованном", participantMap, reportData, reasonMap);
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderLaChangeAddress(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);

        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);

        if (error.isEmpty() && !isNotExistContract) {
            error = checkPersonType(INSURED_PERSON_TYPE, reportData);
        }
        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "застрахованного", false);
        }
        Long thirdPartyId = null;
        if (error.isEmpty() && !isNotExistContract) {
            thirdPartyId = getLongParam(reasonMap, "thirdPartyId");
            if (thirdPartyId == null) {
                error = "Не указан идентификатор застрахованного!";
            }
        }

        Map<String, Object> fullContract = null;
        if (error.isEmpty() && !isNotExistContract) {
            Long externalId = getLongParam(params.get("EXTERNALID"));
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }

        Map<String, Object> participantMap = new HashMap<>();
        if (error.isEmpty() && !isNotExistContract) {
            participantMap = getMemberById(fullContract, thirdPartyId, "insured", "застрахованного");
            error = getStringParam(participantMap, ERROR);
        }

        // формирууем Причину изменения адреса застрахованного
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                error = vipReasonChangeAddress(reportData, reasonMap, participantMap);
            } else {
                error = massReasonChangeAddress("застрахованном", participantMap, reportData, reasonMap);
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderLaChangePassport(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        boolean isCallFromGate = isCallFromGate(params);

        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);

        if (error.isEmpty() && !isNotExistContract) {
            error = checkPersonType(INSURED_PERSON_TYPE, reportData);
        }
        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "застрахованного", false);
        }
        Long thirdPartyId = null;
        if (error.isEmpty() && !isNotExistContract) {
            thirdPartyId = getLongParam(reasonMap, "thirdPartyId");
            if (thirdPartyId == null) {
                error = "Не указан идентификатор застрахованного!";
            }
        }

        Map<String, Object> fullContract = null;
        if (error.isEmpty() && !isNotExistContract) {
            Long externalId = getLongParam(params.get("EXTERNALID"));
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }

        Map<String, Object> participantMap = new HashMap<>();
        if (error.isEmpty() && !isNotExistContract) {
            participantMap = getMemberById(fullContract, thirdPartyId, "insured", "застрахованного");
            error = getStringParam(participantMap, ERROR);
        }

        // формирууем Причину изменения паспортных данных застрахованного
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                error = vipReasonChangePassport(reportData, reasonMap, participantMap, isCallFromGate, login, password);
            } else {
                error = massReasonChangePassport("застрахованном", participantMap, reportData,
                        reasonMap, login, password, isCallFromGate);
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderBChangeSurname(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);

        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);

        if (error.isEmpty() && !isNotExistContract) {
            error = isVipSegment ? checkPersonType(CURBENEF_PERSON_TYPE, reportData)
                    : checkPersonType(BENEF_PERSON_TYPE, reportData);
        }
        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "выгодоприобретателя", false);
        }
        Long thirdPartyId = null;
        if (error.isEmpty() && !isNotExistContract) {
            thirdPartyId = getLongParam(reasonMap, "thirdPartyId");
            if (thirdPartyId == null) {
                error = "Не указан идентификатор выгодоприобретателя!";
            }
        }

        Map<String, Object> fullContract = null;
        if (error.isEmpty() && !isNotExistContract) {
            Long externalId = getLongParam(params.get("EXTERNALID"));
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }

        Map<String, Object> participantMap = new HashMap<>();
        if (error.isEmpty() && !isNotExistContract) {
            participantMap = getMemberById(fullContract, thirdPartyId,
                    "beneficiary", "выгодоприобретателя");
            error = getStringParam(participantMap, ERROR);
        }

        // формирууем Причину изменения фамилии выгодоприобретателя
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                generateRiskStr(reportData, fullContract, thirdPartyId);
                error = vipReasonChangeSurname(reportData, reasonMap, participantMap);
            } else {
                error = massReasonChangeSurname("выгодоприобретателе", participantMap,
                        reportData, reasonMap);
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderBChangePersData(Map<String, Object> params)
            throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);

        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);

        if (error.isEmpty() && !isNotExistContract) {
            error = isVipSegment ? checkPersonType(CURBENEF_PERSON_TYPE, reportData)
                    : checkPersonType(BENEF_PERSON_TYPE, reportData);
        }
        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "выгодоприобретателя", false);
        }
        Long thirdPartyId = null;
        if (error.isEmpty() && !isNotExistContract) {
            thirdPartyId = getLongParam(reasonMap, "thirdPartyId");
            if (thirdPartyId == null) {
                error = "Не указан идентификатор выгодоприобретателя!";
            }
        }

        Map<String, Object> fullContract = null;
        if (error.isEmpty() && !isNotExistContract) {
            Long externalId = getLongParam(params.get("EXTERNALID"));
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }

        Map<String, Object> participantMap = new HashMap<>();
        if (error.isEmpty() && !isNotExistContract) {
            participantMap = getMemberById(fullContract, thirdPartyId,
                    "beneficiary", "выгодоприобретателя");
            error = getStringParam(participantMap, ERROR);
        }

        // формирууем Причину изменения персональных сведений выгодоприобретателя
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            boolean isCallFromGate = isCallFromGate(params);
            if (isVipSegment) {
                createPersonList(reportData);
                generateRiskStr(reportData, fullContract, thirdPartyId);
                error = vipReasonChangePersData(reportData, reasonMap, participantMap, isCallFromGate);
            } else {
                error = massReasonChangePersData("выгодоприобретателе", participantMap,
                        reportData, reasonMap, isCallFromGate);
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderBChangeContInfo(Map<String, Object> params)
            throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);

        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);

        if (error.isEmpty() && !isNotExistContract) {
            error = isVipSegment ? checkPersonType(CURBENEF_PERSON_TYPE, reportData)
                    : checkPersonType(BENEF_PERSON_TYPE, reportData);
        }

        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "выгодоприобретателя", false);
        }
        Long thirdPartyId = null;
        if (error.isEmpty() && !isNotExistContract) {
            thirdPartyId = getLongParam(reasonMap, "thirdPartyId");
            if (thirdPartyId == null) {
                error = "Не указан идентификатор выгодоприобретателя!";
            }
        }

        Map<String, Object> fullContract = null;
        if (error.isEmpty() && !isNotExistContract) {
            Long externalId = getLongParam(params.get("EXTERNALID"));
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }

        Map<String, Object> participantMap = new HashMap<>();
        if (error.isEmpty() && !isNotExistContract) {
            participantMap = getMemberById(fullContract, thirdPartyId,
                    "beneficiary", "выгодоприобретателя");
            error = getStringParam(participantMap, ERROR);
        }

        // формирууем Причину изменения контактной информации выгодоприобретателя
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                generateRiskStr(reportData, fullContract, thirdPartyId);
                error = vipReasonChangeContInfo(reportData, reasonMap, participantMap);
            } else {
                error = massReasonChangeContInfo("выгодоприобретателе", participantMap, reportData, reasonMap);
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderBChangeAddress(Map<String, Object> params)
            throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);

        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);

        if (error.isEmpty() && !isNotExistContract) {
            error = isVipSegment ? checkPersonType(CURBENEF_PERSON_TYPE, reportData)
                    : checkPersonType(BENEF_PERSON_TYPE, reportData);
        }

        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "выгодоприобретателя", false);
        }
        Long thirdPartyId = null;
        if (error.isEmpty() && !isNotExistContract) {
            thirdPartyId = getLongParam(reasonMap, "thirdPartyId");
            if (thirdPartyId == null) {
                error = "Не указан идентификатор выгодоприобретателя!";
            }
        }

        Map<String, Object> fullContract = null;
        if (error.isEmpty() && !isNotExistContract) {
            Long externalId = getLongParam(params.get("EXTERNALID"));
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }

        Map<String, Object> participantMap = new HashMap<>();
        if (error.isEmpty() && !isNotExistContract) {
            participantMap = getMemberById(fullContract, thirdPartyId,
                    "beneficiary", "выгодоприобретателя");
            error = getStringParam(participantMap, ERROR);
        }

        // формирууем Причину изменения адреса выгодоприобретателя
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                generateRiskStr(reportData, fullContract, thirdPartyId);
                error = vipReasonChangeAddress(reportData, reasonMap, participantMap);
            } else {
                error = massReasonChangeAddress("выгодоприобретателе", participantMap, reportData, reasonMap);
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderBChangePassport(Map<String, Object> params)
            throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        boolean isCallFromGate = isCallFromGate(params);

        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String changeSysName = "CHANGEPERSDATA";
        String error = "";
        addReasonType(reasonReportDataMap, changeSysName);

        if (error.isEmpty() && !isNotExistContract) {
            error = isVipSegment ? checkPersonType(CURBENEF_PERSON_TYPE, reportData)
                    : checkPersonType(BENEF_PERSON_TYPE, reportData);
        }

        if (error.isEmpty()) {
            setSettingForPrintForm(reportData, "выгодоприобретателя", false);
        }
        Long thirdPartyId = null;
        if (error.isEmpty() && !isNotExistContract) {
            thirdPartyId = getLongParam(reasonMap, "thirdPartyId");
            if (thirdPartyId == null) {
                error = "Не указан идентификатор выгодоприобретателя!";
            }
        }

        Map<String, Object> fullContract = null;
        if (error.isEmpty() && !isNotExistContract) {
            Long externalId = getLongParam(params.get("EXTERNALID"));
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }

        Map<String, Object> participantMap = new HashMap<>();
        if (error.isEmpty() && !isNotExistContract) {
            participantMap = getMemberById(fullContract, thirdPartyId,
                    "beneficiary", "выгодоприобретателя");
            error = getStringParam(participantMap, ERROR);
        }

        // формирууем Причину изменения паспортных данных выгодоприобретателя
        if (error.isEmpty() && isVipSegment) {
            formingChangePersonalDataInf(reasonReportDataMap, reasonMap);
        }

        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM1IND", TRUE_STR_VALUE);
        if (error.isEmpty() && !isNotExistContract) {
            if (isVipSegment) {
                createPersonList(reportData);
                generateRiskStr(reportData, fullContract, thirdPartyId);
                error = vipReasonChangePassport(reportData, reasonMap, participantMap, isCallFromGate, login, password);
            } else {
                error = massReasonChangePassport("выгодоприобретателе", participantMap, reportData,
                        reasonMap, login, password, isCallFromGate);
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    private void formingChangePersonalDataInf(Map<String, Object> reasonReportDataMap, Map<String, Object> reasonMap) {
        String chgPersInfo = getStringParam(reasonReportDataMap, "chgPersInfo");
        Map<String, Object> kindChangeReasonId_EN = getMapParam(reasonMap, "kindChangeReasonId_EN");
        StringBuilder newChgPersInfo = new StringBuilder();
        String currentChangeInfo = "";
        if (kindChangeReasonId_EN != null && !kindChangeReasonId_EN.isEmpty()) {
            currentChangeInfo = getStringParam(kindChangeReasonId_EN, "name");
        }
        if (!currentChangeInfo.isEmpty()) {
            if (chgPersInfo.isEmpty()) {
                newChgPersInfo.append(currentChangeInfo);
            } else {
                newChgPersInfo.append(chgPersInfo).append(", ")
                        .append(currentChangeInfo.substring(0, 1).toLowerCase())
                        .append(currentChangeInfo.substring(1));
            }
        }
        reasonReportDataMap.put("chgPersInfo", newChgPersInfo.toString());
    }

    private void setSettingForPrintForm(Map<String, Object> reportData, String fileNameUpdateStr, boolean isNeedAddContrNumber) {
        String binDocTypeSysname = getStringParam(reportData, "BINDOCTYPESYSNAME");
        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);
        if (binDocTypeSysname.equals(BIN_DOC_TYPE_SYSNAME)) {
            reportData.put("FILENAMEUPDATESTR", fileNameUpdateStr);
            reportData.put("ISNEEDADDCONTRNUMBER", isNeedAddContrNumber);
        }
    }


    private String massReasonChangePassport(String applicantRusType, Map<String, Object> applicantMap,
                                            Map<String, Object> reportData, Map<String, Object> reasonMap,
                                            String login, String password, boolean isCallFromGate) throws Exception {
        String error = "";

        Map<String, Object> changePersDataMap = getOrCreateMapParam(reportData, CHANGEPERSDATAMAP_PARAM_NAME);
        List<Map<String, Object>> changeList = getOrCreateListParam(changePersDataMap, "changeList");
        Map<String, Object> changePassportData;
        if (applicantMap != null && !applicantMap.isEmpty()) {
            List<Map<String, Object>> documentList = getListParam(applicantMap, "documentList");
            if (documentList == null || documentList.isEmpty()) {
                documentList = new ArrayList<>();
            }
            Map<String, Object> newDocumentMap = getMapParam(reasonMap, "documentId_EN");
            Map<String, Object> documentTypeMap = getMapParam(newDocumentMap, "typeId_EN");
            String documentTypeStr = getStringParam(documentTypeMap, "sysname");
            String documentName = "Паспорт ";
            String newDocumentName = documentName;
            if (documentTypeStr.equals("PassportRF")) {
                newDocumentName = newDocumentName + "РФ";
            } else {
                newDocumentName = newDocumentName + "иностранного гражданина";
            }
            // ищем документ по системному наименованию присланому с интерфейса
            List<Map<String, Object>> filterDocumentList = documentList.stream().filter(new Predicate<Map<String, Object>>() {
                @Override
                public boolean test(Map<String, Object> stringObjectMap) {
                    return documentTypeStr.equals(getStringParam(stringObjectMap, "DOCTYPESYSNAME"));
                }
            }).collect(Collectors.toList());
            String citizenship = getStringParam(applicantMap, "CITIZENSHIP");

            String docnameType = getDocTypeName(citizenship);
            if (docnameType.isEmpty()) {
                String countryId = getStringParam(applicantMap, "COUNTRYID");
                if (countryId != null) {
                    Map<String, Object> citizenshipMap = this.getCountryInfoById(countryId, login, password);
                    citizenship = getStringParam(citizenshipMap, "COUNTRYNAME");
                    docnameType = getDocTypeName(citizenship);
                }
            }
            if (docnameType.isEmpty()) {
                docnameType = "РФ";
            }
            String oldDocumentName = documentName + docnameType;
            // требуется выводить наименование документа, если новое наименование
            // не равно старому или если раньше у участника не было документа
            if (!newDocumentName.equals(oldDocumentName) || filterDocumentList.isEmpty()) {
                changePassportData = new HashMap<>();
                changePassportData.put(ATTR_PARAM_NAME_SYS_NAME, "DOCTYPENAME");
                changePassportData.put(ATTR_OLD_PARAM_NAME, oldDocumentName);
                changePassportData.put(ATTR_NEW_PARAM_NAME, newDocumentName);
                changeList.add(changePassportData);
            }

            Map<String, Object> oldDocument = null;
            // если документа присланого ранее нет, тогда считаем что мы заводим новый документ
            if (filterDocumentList.isEmpty()) {
                // если пришел паспорт гражданина РФ значит ранее был паспорт иностранного гражданина и наоборот
                String newDocumentTypeName = documentTypeStr.equals("PassportRF") ? "ForeignPassportRF" : "PassportRF";
                filterDocumentList = documentList.stream().filter(new Predicate<Map<String, Object>>() {
                    @Override
                    public boolean test(Map<String, Object> stringObjectMap) {
                        return newDocumentTypeName.equals(getStringParam(stringObjectMap, "DOCTYPESYSNAME"));
                    }
                }).collect(Collectors.toList());
                // если не было не одного паспорта то считаем что это какой то новый паспорт
                if (!filterDocumentList.isEmpty()) {
                    oldDocument = filterDocumentList.get(0);
                } else {
                    // todo: не понятно что делать если у нас не было ни паспорта РФ, ни паспорта инстранного гражданина, пока что будем считать что добавляестя новый документ и старых значений никаких нет
                    oldDocument = new HashMap<>();
                }
            } else {
                oldDocument = filterDocumentList.get(0);
            }

            massChangeOldPassportData(changeList, oldDocument, newDocumentMap, isCallFromGate);

        } else {
            error = "Отсутсвуют данные о " + applicantRusType + "!";
        }

        return error;
    }

    private void massChangeOldPassportData(List<Map<String, Object>> changeList, Map<String, Object> oldDocument,
                                           Map<String, Object> newDocumentMap, boolean isCallFromGate) {
        Map<String, Object> changePassportData;
        String newSeries = getStringParam(newDocumentMap, "series");
        String newNumber = getStringParam(newDocumentMap, "no");
        String newSeriesAnNumber = newSeries + " " + newNumber;
        String oldSeries = getStringParam(oldDocument, "DOCSERIES");
        String oldNumber = getStringParam(oldDocument, "DOCNUMBER");
        String oldSeriesAnNumber = oldSeries + " " + oldNumber;
        newSeriesAnNumber = newSeriesAnNumber.trim();
        oldSeriesAnNumber = oldSeriesAnNumber.trim();

        if (!newSeriesAnNumber.equals(oldSeriesAnNumber)) {
            changePassportData = new HashMap<>();
            changePassportData.put(ATTR_PARAM_NAME_SYS_NAME, "DOCSERNUM");
            changePassportData.put(ATTR_OLD_PARAM_NAME, oldSeriesAnNumber);
            changePassportData.put(ATTR_NEW_PARAM_NAME, newSeriesAnNumber);
            changeList.add(changePassportData);
        }

        Date dateOfIssue = getDateParam(newDocumentMap.get("dateOfIssue" + (isCallFromGate ? "$date" : "")));
        if (dateOfIssue != null) {
            String newIssueDateStr = getStringParam(sdf.format(dateOfIssue));
            String oldIssueDateStr = EMPTY_STRING;
            Date oldIssueDate = getDateParam(oldDocument.get("ISSUEDATE"));
            if (oldIssueDate != null) {
                oldIssueDateStr = getStringParam(sdf.format(oldIssueDate));
            }
            if (!newIssueDateStr.equals(oldIssueDateStr)) {
                changePassportData = new HashMap<>();
                changePassportData.put(ATTR_PARAM_NAME_SYS_NAME, "DOCISSUEDATESTR");
                changePassportData.put(ATTR_OLD_PARAM_NAME, oldIssueDateStr);
                changePassportData.put(ATTR_NEW_PARAM_NAME, newIssueDateStr);
                changeList.add(changePassportData);
            }
        }

        String newIssuerCode = getStringParam(newDocumentMap, "issuerCode");
        String oldIssuerCode = getStringParam(oldDocument, "ISSUERCODE");
        if (!newIssuerCode.isEmpty() && !newIssuerCode.equals(oldIssuerCode)) {
            changePassportData = new HashMap<>();
            changePassportData.put(ATTR_PARAM_NAME_SYS_NAME, "DOCISSUERCODE");
            changePassportData.put(ATTR_OLD_PARAM_NAME, oldIssuerCode);
            changePassportData.put(ATTR_NEW_PARAM_NAME, newIssuerCode);
            changeList.add(changePassportData);
        }
    }

    private String vipReasonChangePassport(Map<String, Object> reportData, Map<String, Object> reasonMap,
                                           Map<String, Object> oldPersonMap, boolean isCallFromGate,
                                           String login, String password) throws Exception {
        String error = addOldFioInPersonMap(reportData, oldPersonMap);
        if (error.isEmpty()) {
            Map<String, Object> personMap = getOrCreateMapParam(reportData, PERSONMAP_PARAM_NAME);
            List<Map<String, Object>> documentList = getOrCreateListParam(personMap, "documentList");
            Map<String, Object> newDocumentMap = getMapParam(reasonMap, "documentId_EN");
            Map<String, Object> documentItem = new HashMap<>();
            Map<String, Object> documentType = getMapParam(newDocumentMap, "typeId_EN");
            String itemTypeName = getStringParam(documentType, "name");
            String itemTypeSysName = getStringParam(documentType, "sysname");
            documentItem.put("DOCTYPESYSNAME", itemTypeSysName);
            documentItem.put("DOCTYPENAME", itemTypeName);
            documentItem.put("DOCNUMBER", getStringParam(newDocumentMap, "no"));
            documentItem.put("DOCSERIES", getStringParam(newDocumentMap, "series"));
            documentItem.put("ISSUEDATE", getDateParam(newDocumentMap.get("dateOfIssue" + (isCallFromGate ? "$date" : ""))));
            documentItem.put("ISSUEDBY", getStringParam(newDocumentMap, "authority"));
            documentItem.put("ISSUERCODE", getStringParam(newDocumentMap, "issuerCode"));
            documentList.add(documentItem);
            String countryId = getStringParam(reasonMap, "countryId");
            Map<String, Object> citizenshipMap = this.getCountryInfoById(countryId, login, password);
            String countryName = "Российская Федерация";
            String alphaCode = "RUS";
            if (citizenshipMap != null && !citizenshipMap.isEmpty() && citizenshipMap.size() > 1) {
                countryName = getStringParam(citizenshipMap, "COUNTRYNAME");
                alphaCode = getStringParam(citizenshipMap, "ALPHACODE3");
            }
            personMap.put("CITIZENSHIPSTR", countryName);
            personMap.put("COUNTRYCODE", alphaCode);

            if ("1".equalsIgnoreCase(getStringParam(reasonMap, "isResidencePermit"))) {
                personMap.put("RESIDENCESTR", getStringParam(reasonMap, "residencePermit"));
            } else {
                personMap.put("RESIDENCESTR", FALSE_STR_VALUE);
            }

            if ("1".equalsIgnoreCase(getStringParam(reasonMap, "isTaxResidentOther"))) {
                personMap.put("RESIDENTOTHER", getStringParam(reasonMap, "taxResidentCountry"));
                personMap.put("INNOTHER", getStringParam(reasonMap, "innOther"));
            } else {
                personMap.put("RESIDENTOTHER", FALSE_STR_VALUE);
            }

            if ("1".equalsIgnoreCase(getStringParam(reasonMap, "isTaxResidentUsa"))) {
                personMap.put("RESIDENTUSA", TRUE_STR_VALUE);
                personMap.put("INNUSA", getStringParam(reasonMap, "innUsa"));
            } else {
                personMap.put("RESIDENTUSA", FALSE_STR_VALUE);
            }
        }
        return error;
    }

    private void createPersonList(Map<String, Object> reportData) {
        Map<String, Object> personMap = getOrCreateMapParam(reportData, PERSONMAP_PARAM_NAME);
        getOrCreateListParam(personMap, "documentList");
        getListParam(personMap, "addressList");
        getListParam(personMap, "contactList");
    }

    private void generateRiskStr(Map<String, Object> reportData, Map<String, Object> fullContrInfo, Long thirdPartyId) {
        List<Map<String, Object>> benefList = getListParam(fullContrInfo, "BENEFICIARYLIST");
        List<String> riskCodeByBenefList = new ArrayList<>();
        if (benefList != null && !benefList.isEmpty()) {
            riskCodeByBenefList = benefList.stream()
                    .filter(new Predicate<Map<String, Object>>() {
                        @Override
                        public boolean test(Map<String, Object> stringObjectMap) {
                            Map<String, Object> participantMap = getMapParam(stringObjectMap, "PARTICIPANTMAP");
                            return thirdPartyId.equals(getLongParam(participantMap, "THIRDPARTYID"));
                        }
                    }).map(new Function<Map<String, Object>, String>() {
                        @Override
                        public String apply(Map<String, Object> stringObjectMap) {
                            return getStringParam(stringObjectMap, "RISKCODE");
                        }
                    }).collect(Collectors.toList());
        }
        List<Map<String, Object>> prodStructList = getProductStructListByDiscriminatorFive(fullContrInfo);
        StringJoiner joiner = new StringJoiner(", ");
        String riskName = "";
        for (String riskCode : riskCodeByBenefList) {
            riskName = getRiskNameByRiskCode(prodStructList, riskCode);
            joiner.add(riskName);
        }
        String result = "";
        result = joiner.toString();
        Map<String, Object> personMap = getOrCreateMapParam(reportData, PERSONMAP_PARAM_NAME);
        personMap.put("benefProgRiskStr", result);
    }

    private List<Map<String, Object>> getProductStructListByDiscriminatorFive(Map<String, Object> fullContrInfo) {
        List<Map<String, Object>> productStructList = new ArrayList<>();
        Map<String, Object> productMap = getMapParam(fullContrInfo, "PRODUCTMAP");
        if (productMap != null) {
            Map<String, Object> prodVerMap = getMapParam(productMap, "PRODVER");
            if (prodVerMap != null) {
                productStructList = getListParam(prodVerMap, "PRODSTRUCTS");
                if (productStructList != null && !productStructList.isEmpty()) {
                    productStructList = productStructList.stream().filter(new Predicate<Map<String, Object>>() {
                        @Override
                        public boolean test(Map<String, Object> stringObjectMap) {
                            return "5".equalsIgnoreCase(getStringParam(stringObjectMap, "DISCRIMINATOR"));
                        }
                    }).collect(Collectors.toList());
                }
            }
        }
        return productStructList;
    }

    private String getRiskNameByRiskCode(List<Map<String, Object>> prodStructList, String riskCode) {
        String riskName = "";
        String riskSysName = "";
        for (Map<String, Object> prodStructItem : prodStructList) {
            riskSysName = getStringParam(prodStructItem, "SYSNAME");
            if (riskCode.equalsIgnoreCase(riskSysName)) {
                riskName = getStringParam(prodStructItem, "NAME");
            }
        }
        return riskName;
    }

    private String getDocTypeName(String citizenship) {
        String docnameType = "";
        if (!citizenship.isEmpty()) {
            if (citizenship.equals("0") || citizenship.equals("RUS")) {
                docnameType = "РФ";
            } else {
                docnameType = "иностранного гражданина";
            }
        }
        return docnameType;
    }

    private Map<String, Object> getCountryInfoById(String countryId, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("COUNTRYID", countryId);
        param.put(RETURN_AS_HASH_MAP, TRUE_STR_VALUE);
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BCountryBrowseListByParam",
                param, login, password);
        if (res != null) {
            return res;
        }

        return new HashMap<>();
    }

    private String massReasonChangeAddress(String applicantRusType, Map<String, Object> participantMap,
                                           Map<String, Object> reportData, Map<String, Object> reasonMap) {
        String error = "";

        Map<String, Object> changePersDataMap = getOrCreateMapParam(reportData, CHANGEPERSDATAMAP_PARAM_NAME);
        List<Map<String, Object>> changeList = getOrCreateListParam(changePersDataMap, "changeList");
        Map<String, Object> changePersData;
        if (participantMap != null && !participantMap.isEmpty()) {
            List<Map<String, Object>> addressList = getListParam(participantMap, "addressList");
            if (addressList == null || addressList.isEmpty()) {
                error = "отсутствует список адресов";
            } else {
                Map<String, Object> addressMap = getMapParam(reasonMap, "addressId_EN");
                Map<String, Object> addressTypeMap = getMapParam(addressMap, "typeId_EN");
                String addressTypeStr = getStringParam(addressTypeMap, "sysname");
                String newAddressStr = getStringParam(addressMap, "address");
                List<Map<String, Object>> filterAddressList = addressList.stream().filter(new Predicate<Map<String, Object>>() {
                    @Override
                    public boolean test(Map<String, Object> stringObjectMap) {
                        return addressTypeStr.equals(getStringParam(stringObjectMap, "ADDRESSTYPESYSNAME"));
                    }
                }).collect(Collectors.toList());
                String attrSysName = addressTypeStr.equals("RegisterAddress") ? "REGADDR" : "FACTADDR";
                String oldAddressStr = "";
                if (!filterAddressList.isEmpty()) {
                    oldAddressStr = getStringParam(filterAddressList.get(0), "address");
                }
                if (!newAddressStr.equals(oldAddressStr)) {
                    changePersData = new HashMap<>();
                    changePersData.put(ATTR_PARAM_NAME_SYS_NAME, attrSysName);
                    changePersData.put(ATTR_OLD_PARAM_NAME, oldAddressStr);
                    changePersData.put(ATTR_NEW_PARAM_NAME, newAddressStr);
                    changeList.add(changePersData);
                }
            }
        } else {
            error = "Отсутсвуют данные о " + applicantRusType + "!";
        }

        return error;

    }

    private String vipReasonChangeAddress(Map<String, Object> reportData, Map<String, Object> reasonMap, Map<String, Object> oldPersonMap) {
        String error = addOldFioInPersonMap(reportData, oldPersonMap);
        if (error.isEmpty()) {
            Map<String, Object> personMap = getOrCreateMapParam(reportData, PERSONMAP_PARAM_NAME);
            List<Map<String, Object>> addressList = getOrCreateListParam(personMap, "addressList");
            Map<String, Object> addressMap = getMapParam(reasonMap, "addressId_EN");
            Map<String, Object> documentType = getMapParam(addressMap, "typeId_EN");
            String itemTypeName = getStringParam(documentType, "name");
            String itemTypeSysname = getStringParam(documentType, "sysname");

            Map<String, Object> item = new HashMap<>();
            item.put("ADDRESSTYPESYSNAME", itemTypeSysname);
            item.put("ADDRESSTYPENAME", itemTypeName);
            item.put("CITY", addressMap.get("city"));
            item.put("REGION", addressMap.get("region"));
            item.put("STREET", addressMap.get("street"));
            item.put("HOUSE", addressMap.get("house"));
            item.put("FLAT", addressMap.get("flat"));
            item.put("ADDRESSTEXT1", addressMap.get("address"));
            item.put("ADDRESSTEXT2", addressMap.get("address"));
            item.put("ADDRESSTEXT3", addressMap.get("address2"));
            item.put("POSTALCODE", addressMap.get("postcode"));

            addressList.add(item);
        }
        return error;
    }

    private String massReasonChangeContInfo(String applicantRusType, Map<String, Object> participantMap,
                                            Map<String, Object> reportData, Map<String, Object> reasonMap) {
        String error = "";

        Map<String, Object> changePersDataMap = getOrCreateMapParam(reportData, CHANGEPERSDATAMAP_PARAM_NAME);
        List<Map<String, Object>> changeList = getOrCreateListParam(changePersDataMap, "changeList");
        Map<String, Object> changePersData;
        if (participantMap != null && !participantMap.isEmpty()) {
            List<Map<String, Object>> contactList = getListParam(participantMap, "contactList");
            if (contactList == null || contactList.isEmpty()) {
                error = "отсутствует список контактной информации";
            } else {
                String newPhone = getStringParam(reasonMap, "phone");
                if (!newPhone.isEmpty()) {
                    List<Map<String, Object>> filterContactList = contactList.stream()
                            .filter(new Predicate<Map<String, Object>>() {
                                @Override
                                public boolean test(Map<String, Object> stringObjectMap) {
                                    return "MobilePhone".equals(getStringParam(stringObjectMap, CONTACTTYPESYSNAME_PARAM_NAME));
                                }
                            }).collect(Collectors.toList());
                    String oldPhone = "";
                    if (!filterContactList.isEmpty()) {
                        oldPhone = getStringParam(filterContactList.get(0), "VALUE");
                    }
                    if (!newPhone.equals(oldPhone)) {
                        changePersData = new HashMap<>();
                        changePersData.put(ATTR_PARAM_NAME_SYS_NAME, "MOBILE");
                        changePersData.put(ATTR_OLD_PARAM_NAME, oldPhone);
                        changePersData.put(ATTR_NEW_PARAM_NAME, newPhone);
                        changeList.add(changePersData);
                    }
                }

                String newEmail = getStringParam(reasonMap, "email");
                if (!newEmail.isEmpty()) {
                    List<Map<String, Object>> filterContactList = contactList.stream().filter(new Predicate<Map<String, Object>>() {
                        @Override
                        public boolean test(Map<String, Object> stringObjectMap) {
                            return "PersonalEmail".equals(getStringParam(stringObjectMap, CONTACTTYPESYSNAME_PARAM_NAME));
                        }
                    }).collect(Collectors.toList());
                    String oldEmail = "";
                    if (!filterContactList.isEmpty()) {
                        oldEmail = getStringParam(filterContactList.get(0), "VALUE");
                    }
                    if (!newEmail.equals(oldEmail)) {
                        changePersData = new HashMap<>();
                        changePersData.put(ATTR_PARAM_NAME_SYS_NAME, "OTHER");
                        changePersData.put(ATTR_OLD_PARAM_NAME, oldEmail);
                        changePersData.put(ATTR_NEW_PARAM_NAME, newEmail);
                        changeList.add(changePersData);
                    }
                }
            }
        } else {
            error = "Отсутсвуют данные о " + applicantRusType + "!";
        }

        return error;

    }

    private String vipReasonChangeContInfo(Map<String, Object> reportData, Map<String, Object> reasonMap, Map<String, Object> oldPersonMap) {
        String error = addOldFioInPersonMap(reportData, oldPersonMap);
        if (error.isEmpty()) {
            Map<String, Object> personMap = getOrCreateMapParam(reportData, PERSONMAP_PARAM_NAME);
            List<Map<String, Object>> contactList = getOrCreateListParam(personMap, "contactList");
            String email = getStringParam(reasonMap, "email");
            if (!email.isEmpty()) {
                Map<String, Object> contactItem = new HashMap<>();
                contactItem.put(CONTACTTYPESYSNAME_PARAM_NAME, "PersonalEmail");
                contactItem.put("VALUE", email);
                contactList.add(contactItem);
            }
            String phone = getStringParam(reasonMap, "phone");
            if (!phone.isEmpty()) {
                Map<String, Object> contactItem = new HashMap<>();
                contactItem.put(CONTACTTYPESYSNAME_PARAM_NAME, "MobilePhone");
                contactItem.put("VALUE", phone);
                contactList.add(contactItem);
            }
        }
        return error;
    }

    private String massReasonChangePersData(String applicantRusType, Map<String, Object> participantMap,
                                            Map<String, Object> reportData, Map<String, Object> reason,
                                            boolean isCallFromGate) {
        String error = "";

        Map<String, Object> changePersDataMap = getOrCreateMapParam(reportData, CHANGEPERSDATAMAP_PARAM_NAME);
        List<Map<String, Object>> changeList = getOrCreateListParam(changePersDataMap, "changeList");
        if (participantMap != null && !participantMap.isEmpty()) {
            Map<String, Object> changeMap;
            String newFirstName = getStringParam(reason, "name");
            String oldFirstName = getStringParam(participantMap, "FIRSTNAME");
            if (!newFirstName.isEmpty() && !newFirstName.equals(oldFirstName)) {
                changeMap = new HashMap<>();
                changeMap.put(ATTR_PARAM_NAME_SYS_NAME, "FIRSTNAME");
                changeMap.put(ATTR_OLD_PARAM_NAME, oldFirstName);
                changeMap.put(ATTR_NEW_PARAM_NAME, newFirstName);
                changeList.add(changeMap);
            }

            Date newBirthDate = getDateParam(reason.get("birthDate" + (isCallFromGate ? "$date" : "")));
            if (newBirthDate != null) {
                String newBirthDateStr = sdf.format(newBirthDate);
                String oldBirthDateStr = EMPTY_STRING;
                Date oldBirthDate = getDateParam(participantMap.get("BIRTHDATE"));
                if (oldBirthDate != null) {
                    oldBirthDateStr = sdf.format(oldBirthDate);
                }
                if (!newBirthDateStr.equals(oldBirthDateStr)) {
                    changeMap = new HashMap<>();
                    changeMap.put(ATTR_PARAM_NAME_SYS_NAME, "BIRTHDATE");
                    changeMap.put(ATTR_OLD_PARAM_NAME, oldBirthDateStr);
                    changeMap.put(ATTR_NEW_PARAM_NAME, sdf.format(newBirthDate));
                    changeList.add(changeMap);
                }
            }

            String newMiddleName = getStringParam(reason, "middleName");
            String oldMiddleName = getStringParam(participantMap, "MIDDLENAME");
            if (!newMiddleName.isEmpty() && !newMiddleName.equals(oldMiddleName)) {
                changeMap = new HashMap<>();
                changeMap.put(ATTR_PARAM_NAME_SYS_NAME, "MIDDLENAME");
                changeMap.put(ATTR_OLD_PARAM_NAME, oldMiddleName);
                changeMap.put(ATTR_NEW_PARAM_NAME, newMiddleName);
                changeList.add(changeMap);
            }
        } else {
            error = "Отсутсвуют данные о " + applicantRusType + "!";
        }

        return error;
    }

    private String vipReasonChangePersData(Map<String, Object> reportData, Map<String, Object> reasonMap,
                                           Map<String, Object> oldPersonMap,
                                           boolean isCallFromGate) {
        String error = addOldFioInPersonMap(reportData, oldPersonMap);
        if (error.isEmpty()) {
            Map<String, Object> personMap = getOrCreateMapParam(reportData, PERSONMAP_PARAM_NAME);
            String name = getStringParam(reasonMap, "name");
            if (!name.isEmpty()) {
                personMap.put("FIRSTNAME", name);
            }
            String middleName = getStringParam(reasonMap, "middleName");
            if (!middleName.isEmpty()) {
                personMap.put("MIDDLENAME", middleName);
            }

            Date birthDate = getDateParam(reasonMap.get("birthDate" + (isCallFromGate ? "$date" : "")));
            if (birthDate != null) {
                personMap.put("BIRTHDATE", birthDate);
            }
        }
        return error;
    }

    private String massReasonChangeSurname(String applicantType, Map<String, Object> applicantMap,
                                           Map<String, Object> reportData, Map<String, Object> reason) {
        String error = "";

        Map<String, Object> changePersDataMap = getOrCreateMapParam(reportData, CHANGEPERSDATAMAP_PARAM_NAME);
        List<Map<String, Object>> changeList = getOrCreateListParam(changePersDataMap, "changeList");
        if (applicantMap != null && !applicantMap.isEmpty()) {
            String oldSurname = getStringParam(applicantMap, LASTNAME_PARAM_NAME);
            String newSurname = getStringParam(reason, "surName");
            if (!oldSurname.equals(newSurname)) {
                Map<String, Object> changeSurnameMap = new HashMap<>();
                changeSurnameMap.put(ATTR_OLD_PARAM_NAME, oldSurname);
                changeSurnameMap.put(ATTR_PARAM_NAME_SYS_NAME, LASTNAME_PARAM_NAME);
                changeSurnameMap.put(ATTR_NEW_PARAM_NAME, newSurname);
                changeList.add(changeSurnameMap);
            }
        } else {
            error = "Отсутсвуют данные о " + applicantType + "!";
        }

        return error;
    }

    private String vipReasonChangeSurname(Map<String, Object> reportData, Map<String, Object> reasonMap, Map<String, Object> oldPersonMap) {
        String error = addOldFioInPersonMap(reportData, oldPersonMap);
        if (error.isEmpty()) {
            Map<String, Object> personMap = getOrCreateMapParam(reportData, PERSONMAP_PARAM_NAME);
            personMap.put(LASTNAME_PARAM_NAME, getStringParam(reasonMap, "surName"));
        }
        return error;
    }

    private String addOldFioInPersonMap(Map<String, Object> reportData, Map<String, Object> oldPersonMap) {
        String error = "";
        if (oldPersonMap != null && !oldPersonMap.isEmpty()) {
            Map<String, Object> personMap = getOrCreateMapParam(reportData, PERSONMAP_PARAM_NAME);
            boolean isEmptyOldFio = getStringParam(personMap, "OLDLASTNAME").isEmpty();
            if (isEmptyOldFio) {
                String oldSurname = getStringParam(oldPersonMap, LASTNAME_PARAM_NAME);
                String oldFirstName = getStringParam(oldPersonMap, "FIRSTNAME");
                String oldMiddleName = getStringParam(oldPersonMap, "MIDDLENAME");
                personMap.put("OLDLASTNAME", oldSurname);
                personMap.put("OLDFIRSTNAME", oldFirstName);
                personMap.put("OLDMIDDLENAME", oldMiddleName);
            }
        } else {
            Map<String, Object> changePersDataMap = getOrCreateMapParam(reportData, CHANGEPERSDATAMAP_PARAM_NAME);
            String personTypeInMap = getStringParam(changePersDataMap, "personType");
            String personTypeRus = PERSON_TYPE_MAP.get(personTypeInMap);
            error = "Отсутсвуют данные о " + personTypeRus + "!";
        }
        return error;
    }

    private String checkPersonType(String personType, Map<String, Object> reportData) {
        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        String mapParamName = isVipSegment ? PERSONMAP_PARAM_NAME : CHANGEPERSDATAMAP_PARAM_NAME;
        Map<String, Object> map = getOrCreateMapParam(reportData, mapParamName);
        String personTypeInMap = getStringParam(map, "personType");
        String error = "";
        if (personTypeInMap.isEmpty()) {
            map.put("personType", personType);
        } else {
            if (!personTypeInMap.equalsIgnoreCase(personType)) {
                String oldPersonTypeRus = PERSON_TYPE_MAP.get(personTypeInMap);
                String newPersonTypeRus = PERSON_TYPE_MAP.get(personType);
                error = "В списке изменения ошибочно указано изменения для " + newPersonTypeRus + ", так как"
                        + " до этого былы подготовлены данные для изменения " + oldPersonTypeRus;
            }
        }
        return error;
    }

}
