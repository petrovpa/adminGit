/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.primaryactivity.facade.pos.custom.export;

import com.bivgroup.ws.primaryactivity.facade.B2BPrimaryActivityBaseFacade;
import com.bivgroup.ws.primaryactivity.system.Constants;
import com.bivgroup.ws.primaryactivity.system.DatesParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;

/**
 *
 * @author averichevsm
 */
@BOName("B2BPrimaryActivityExportCustom")
public class B2BPrimaryActivityExportCustomFacade extends B2BPrimaryActivityBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    // Имя сервиса для вызова методов из b2bposservice
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    // Имя сервиса для вызова методов по печати документов
    private static final String LIBREOFFICEREPORTSWS_SERVICE_NAME = Constants.LIBREOFFICEREPORTSWS;
    //private static final String LIBREOFFICEREPORTSWS_SERVICE_NAME = "sign" + Constants.LIBREOFFICEREPORTSWS; //!только для отладки!
    // Имя сервиса для вызова методов из primaryactivity
    private static final String THIS_SERVICE_NAME = B2BPOSWS_SERVICE_NAME;

    private static final String DEFAULT_AGENT_REPORT_EXPORT_REPORT_TEMPLATE_NAME = "EXPORT/EXPORT01/export.ods";

    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final private byte[] Salt = {
        (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
        (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19
    };

    // флаг подробного протоколирования операций с датами и переопределения параметров
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

    // флаг подробного протоколирования вызовов методов веб-сервисов
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_CALLS_LOGGING = logger.isDebugEnabled();

    // имя поля с PK в таблице отчетов агента
    private static final String AGENT_REPORT_PK_FIELD_NAME = "MACAGENTREPORTID";

    // обработчик дат
    private static DatesParser datesParser;

    public B2BPrimaryActivityExportCustomFacade() {
        super();
        init();
    }

    private void init() {
        // обработчик дат
        datesParser = new DatesParser();
        // обработчик дат - протоколирование операций с датами
        datesParser.setVerboseLogging(IS_VERBOSE_LOGGING);
    }

    private ArrayList<Map<String, Object>> addNewColumnDescriptionMap(ArrayList<Map<String, Object>> columnNamesMapList, String fieldName, String headerName) {
        Map<String, Object> newColumnDescriptionMap = newColumnDescriptionMap(fieldName, headerName);
        columnNamesMapList.add(newColumnDescriptionMap);
        return columnNamesMapList;
    }

    private Map<String, Object> newColumnDescriptionMap(String fieldName, String headerName) {
        Map<String, Object> columnDescriptionMap = new HashMap<String, Object>();
        columnDescriptionMap.put("field", fieldName);
        columnDescriptionMap.put("headerName", headerName);
        return columnDescriptionMap;
    }

    // получение списка из мап, содержащих описание столбцов отчета с датами
    private ArrayList<Map<String, Object>> getDatesColumnNamesMapList(ArrayList<Map<String, Object>> columnNamesMapList) {
        ArrayList<Map<String, Object>> datesColumnNamesMapList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> columnNamesMap : columnNamesMapList) {
            String field = getStringParam(columnNamesMap.get("field"));
            if (field.endsWith("DATE")) {
                String newField = field.replace("DATE", "");
                Map<String, Object> datesColumnNamesMap = new HashMap<String, Object>();
                datesColumnNamesMap.putAll(columnNamesMap);
                datesColumnNamesMap.put("newField", newField);
                columnNamesMap.put("field", newField);
                datesColumnNamesMapList.add(datesColumnNamesMap);
            }
        }
        logger.debug("Custom date columns maps list = " + datesColumnNamesMapList);
        return datesColumnNamesMapList;
    }

    private String getEncryptedFileNamesStr(String reportName) throws SecurityException {
        logger.debug("Encrypting file names for angular interface...");
        String encryptedFileNamesStr = "";
        if (!reportName.isEmpty()) {
            logger.debug("Report name for encrypting (reportName): " + reportName);
            String docPath = reportName;
            String docName = reportName;
            int separatorIndex = docPath.indexOf("\\");
            if (separatorIndex >= 0) {
                docName = docPath.substring(separatorIndex + 1);
            }
            separatorIndex = docPath.indexOf("/");
            if (separatorIndex >= 0) {
                docName = docPath.substring(separatorIndex + 1);
            }
            String userDocName = reportName;
            String fileNamesStr = docName + "@" + userDocName;
            logger.debug("File names string for encrypting (fileNamesStr) = " + fileNamesStr);

            StringCryptUtils crypter = new StringCryptUtils(EncryptionPassword, Salt);
            encryptedFileNamesStr = crypter.encrypt(fileNamesStr);
            logger.debug("Encrypted file names string (encryptedFileNamesStr) = " + encryptedFileNamesStr);

        }
        logger.debug("Encrypting file names for angular interface finished.");
        return encryptedFileNamesStr;
    }

    private String genExportReport(Map<String, Object> reportData, String reportFormat, String login, String password) throws Exception {

        // получение из CORE_SETTINGS относительного пути (включая имя файла) до шаблона отчета для экспорта содержимого агентского договора
        String templateName = getTemplateNameFromCoreSettingsOrDefault(login, password);

        // подготовка параметров для генерации отчета
        Map<String, Object> libreReportParams = new HashMap<String, Object>();
        libreReportParams.put("REPORTDATA", reportData);
        libreReportParams.put("REPORTFORMATS", reportFormat);
        libreReportParams.put("templateName", templateName);
        libreReportParams.put(RETURN_AS_HASH_MAP, true);
        // вызов генерации отчета
        //logger.debug("libreReportParams = " + libreReportParams);
        Map<String, Object> reportResult = this.callService(LIBREOFFICEREPORTSWS_SERVICE_NAME, "dsLibreOfficeReportCreate", libreReportParams, IS_VERBOSE_CALLS_LOGGING, login, password);
        //logger.debug("reportResult = " + reportResult);

        // получение имени сгенерированного отчета из результатов вызова генерации
        String reportName = "";
        if (reportResult != null) {
            Map<String, Object> reportResultData = (Map<String, Object>) reportResult.get("REPORTDATA");
            if (reportResultData != null) {
                reportName = getStringParam(reportResultData.get("reportName"));
                if (!reportName.isEmpty()) {
                    reportName = reportName + reportFormat;
                }
            }
        }
        
        if (reportName.isEmpty()) {
            logger.error("Error during generation agent report export report file! Details: " + reportResult);
        } else {
            logger.debug("reportName = " + reportName);
        }

        return reportName;
    }

    // получение из CORE_SETTINGS относительного пути (включая имя файла) до шаблона отчета для экспорта содержимого агентского договора
    private String getTemplateNameFromCoreSettingsOrDefault(String login, String password) throws Exception {
        logger.debug("Getting agent report export report template file name from CORE_SETTING...");
        String settingSysName = "b2bAgentReportExportReportTemplateFilename";
        String templateName = getCoreSettingBySysName(settingSysName, login, password);
        if (templateName.isEmpty()) {
            templateName = DEFAULT_AGENT_REPORT_EXPORT_REPORT_TEMPLATE_NAME;
            logger.debug("Agent report export report template file name not found in CORE_SETTING, default file name will be used: " + templateName);
        } else {
            logger.debug("Agent report export report template file name found in CORE_SETTING and will be used: " + templateName);
        }
        return templateName;
    }

    @WsMethod(requiredParams = {AGENT_REPORT_PK_FIELD_NAME})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportCreateXLSReport(Map<String, Object> params) throws Exception {

        logger.debug("Agent report content export report creating...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        // идентификатор обрабатываемой записи
        Long agentReportID = getLongParamLogged(params, AGENT_REPORT_PK_FIELD_NAME);
        
        // получение включаемых в отчет сведений об отчете агента
        Map<String, Object> agentReportParams = new HashMap<String, Object>();
        agentReportParams.put(AGENT_REPORT_PK_FIELD_NAME, agentReportID);
        agentReportParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> agentReportInfo = this.callService(THIS_SERVICE_NAME, "dsB2BPrimaryActivityContractAgentReportBrowseListByParamEx", agentReportParams, IS_VERBOSE_CALLS_LOGGING, login, password);

        // получение включаемых в отчет сведений о содержимом отчета агента
        String methodName = "dsB2BMainActivityContractAgentReportContentBrowseListByAgentReportID";
        Map<String, Object> contentParams = new HashMap<String, Object>();
        contentParams.put(AGENT_REPORT_PK_FIELD_NAME, agentReportID);
        contentParams.put(FOR_EXPORT, true);
        contentParams.put("ISREMOVED", 0L);
        List<Map<String, Object>> dataList = this.callServiceAndGetListFromResultMap(THIS_SERVICE_NAME, methodName, contentParams, IS_VERBOSE_CALLS_LOGGING, login, password);

        // подготовка параметров для генерации отчета
        Map<String, Object> rawReportData = new HashMap<String, Object>();
        rawReportData.putAll(agentReportInfo);
        rawReportData.put("CONTENTLIST", dataList);
        
        // генерация строковых представлений для сумм
        rawReportData.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> reportData = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractTextSums", rawReportData, login, password);
        
        // генерация строковых представлений для дат
        //datesParser.parseDates(reportData, String.class);
        
        //result.put("REPORTDATA", reportData); //!только для отладки!

        // генерация отчета
        String reportFormat = ".xls";
        String reportName = genExportReport(reportData, reportFormat, login, password);

        // анализ результата генерации отчета
        if (reportName.isEmpty()) {
            result.put("Error", "Ошибка при формировании файла экспорта!");
        } else {
            // шифрование имен файлов отчета для возврата в angular-интерфейс
            String encryptedFileNamesStr = getEncryptedFileNamesStr(reportName);
            if (!encryptedFileNamesStr.isEmpty()) {
                result.put(AGENT_REPORT_PK_FIELD_NAME, agentReportID); // признак успешного выполнения действия для angular-интерфейса отчета агента
                result.put("ENCRIPTEDFILENAME", encryptedFileNamesStr);
                // сгенерированный отчет выдать пользователю в интерфейсе в виде ссылки с зашифрованым именем файла отчета
            }
        }
        
        logger.debug("Agent report content export report creating finished.");

        return result;

    }

}
