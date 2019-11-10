package com.bivgroup.services.b2bposws.facade.pos.declaration.change.print;

import com.bivgroup.services.b2bposws.facade.pos.declaration.B2BDeclarationBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.declaration.change.B2BDeclarationOfChangeCustomFacade;
import com.bivgroup.services.b2bposws.system.files.FileWriter;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.io.File;
import java.util.*;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BDeclarationOfChangePrint")
public class B2BDeclarationOfChangePrintFacade extends B2BDeclarationBaseFacade implements FileWriter {
    // тексты ошибок
    private static final String DEFAULT_DATA_PROVIDER_ERROR_MSG = "Не удалось подготовить данные для формирования заявления на изменение условий страхования!";
    private static final String DEFAULT_CLAIM_NO_TEMPLATE_ERROR_MSG = "Не удалось выбрать шаблон для формирования заявление на изменение условий страхования!";
    private static final String DEFAULT_CLAIM_BIN_FILE_ERROR_MSG = "Не удалось прикрепить сформированный бланк к заявлению на изменение условий страхования!";
    private static final String DEFAULT_CLAIM_PRINT_ERROR_MSG = "Не удалось сформировать заявление на изменение условий страхования!";

    private static final String PROJECT_PARAM_NAME = "project";

    public static final String TEMPLATE_FILENAME_PARAMNAME = "TEMPLATEFILENAME";

    private static final String DECLARATION_OF_CHANGE_ID_PARAMNAME = "id";
    public static final String REPORT_DATA_PARAMETER_NAME = "REPORTDATA";
    public static final String URLPATH_PARAMETER_NAME = "URLPATH";

    private static final String REPORT_DATA_PARAMNAME = "reportData";
    private static final List<String> notRealizationPfList;

    static {
        notRealizationPfList = Arrays.asList(
                "ReasonChangeForContract_Activation",
                "ReasonChangeForContract_Underwriting",
                "ReasonChangeForContract_Annulment",
                "ReasonChangeForContract_ChangeBorker",
                "ReasonChangeForContract_Active",
                "ReasonChangeForContract_ChangeCreditData",
                "ReasonChangeForContract_Indexing",
                "ReasonChangeForContract_OnRegistration",
                "ReasonChangeForContract_Options",
                "ReasonChangeForContract_Refusal",
                "ReasonChangeForContract_Recalculation",
                "ReasonChangeForContract_Cancellation",
                "ReasonChangeForContract_Claim",
                "ReasonChangeForContract_ClaimOpen",
                "ReasonChangeForContract_ExitFinancialVacation"
        );
    }

    /*
     * DECLARATION_OF_CHANGE_ID_PARAMNAME - Id заявления на изменение или
     * REPORT_DATA_PARAMNAME - Map с нужными данными и
     * URLPATH для ссылки на файл и
     * SESSIONCALLFORID
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BDeclarationOfChangePrint(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDeclarationOfChangePrint begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        Long declarationId = getLongParamLogged(params, DECLARATION_OF_CHANGE_ID_PARAMNAME);
        Map<String, Object> declarationMap = getMapParam(params, B2BDeclarationOfChangeCustomFacade.DECLARATION_MAP_PARAMNAME);
        // данные подготовленные для отчета провайдером или в параметре
        Map<String, Object> reportData = null;
        String error = "";

        // промежуточный результат, в основном для протоколирования
        Map<String, Object> subResult = new HashMap<String, Object>();

        // вызов провайдера данных для заявления
        // кладем в мапу все параметры, что пришли, т.к. у нас может не быть declarationId
        Map<String, Object> dataProviderParams = new HashMap<String, Object>(params);
        dataProviderParams.put(DECLARATION_OF_CHANGE_ID_PARAMNAME, params.get(DECLARATION_OF_CHANGE_ID_PARAMNAME));
        if (declarationMap != null) {
            dataProviderParams.put(DECLARATION_MAP_PARAMNAME, declarationMap);
        }

        dataProviderParams.put(RETURN_AS_HASH_MAP, true);
        // todo: возможно, получать имя метода провайдера данных из БД
        // (однако, делать это следует по системному наименованию или самого провайдера или настройки из CORE_SETTINGS,
        // а это получение одной строковой константы по другой строковой константе)
        String dataProviderMethodName = "dsB2BDeclarationOfChangeReportDataProvider";
        Map<String, Object> dataProviderResult = this.callService(B2BPOSWS_SERVICE_NAME, dataProviderMethodName, dataProviderParams, login, password);
        error = getStringParamLogged(dataProviderResult, ERROR);
        if (isCallResultOKAndContains(dataProviderResult, REPORT_DATA_PARAMETER_NAME) && (error.isEmpty())) {
            // данные подготовленные для отчета провайдером
            reportData = getMapParam(dataProviderResult, REPORT_DATA_PARAMETER_NAME);
            subResult.put(REPORT_DATA_PARAMETER_NAME, reportData);
        }
        if ((reportData == null) && (error.isEmpty())) {
            error = DEFAULT_DATA_PROVIDER_ERROR_MSG;
        }

        String kindChangeReasonSysName = "";
        List<Map<String, Object>> printList = new ArrayList<>();
        if (error.isEmpty()) {
            List<Map<String, Object>> reasons = getListParam(reportData, "reasons");
            if ((reasons != null) && (!reasons.isEmpty())) {
                HashMap<String, Object> printItem = new HashMap<>();
                // отдельная печать для отмены фиксации, все равно какие еще есть ризоны
                // требуется только напечатать отмену фиксации
                if (params.get("ISPRINTCANCELDOC") != null) {
                    String coreSettingParamName = "b2bReasonChangeForContract_FixIncome_Cancel_ReportTemplateFilename";
                    // получение из CORE_SETTINGS относительного пути (включая имя файла) до шаблона заявления на выплату по страховому событию
                    String settingValue = getCoreSettingBySysName(coreSettingParamName, login, password);
                    if (!settingValue.isEmpty()) {
                        printItem.put("templateName", settingValue);
                        printList.add(printItem);
                    }
                } else {
                    boolean isPrintZovi = false;
                    // бежим по списку изменений и формируем список, что нужно печтать
                    for (Map<String, Object> reason : reasons) {
                        printItem = new HashMap<>();
                        Map<String, Object> kindChangeReason = getMapParam(reason, "kindChangeReasonId_EN");
                        kindChangeReasonSysName = getStringParamLogged(kindChangeReason, "sysname");
                        String coreSettingParamName = "b2b" + kindChangeReasonSysName + "_ReportTemplateFilename";
                        // получение из CORE_SETTINGS относительного пути (включая имя файла) до шаблона заявления на выплату по страховому событию
                        String settingValue = getCoreSettingBySysName(coreSettingParamName, login, password);
                        // если с данным сиснеймом присутсвует в CORE_SETTINGS то нужно напечатать заявление
                        // с шаблоном указаным в CORE_SETTINGS
                        if (!settingValue.isEmpty()) {
                            printItem.put("templateName", settingValue);
                            printList.add(printItem);
                        }
                        // если еще не требовалось напечатать сложною форму и в CORE_SETTINGS ничего нет
                        // тогда требуется сформировать сложную форму. Одну!
                        if (!isPrintZovi && settingValue.isEmpty()) {
                            isPrintZovi = true;
                            printItem.put("isPrintZovi", isPrintZovi);
                            printList.add(printItem);
                        }
                    }
                }
            } else {
                if (kindChangeReasonSysName.isEmpty()) {
                    error = "Отсутствует список изменений";
                }
            }
        }

        String prodProgSysName = getStringParamLogged(reportData, "PRODPROGSYSNAME");
        String templateName = "";
        String docType = "";
        String reportName = "";

        List<Map<String, Object>> printResultList = new ArrayList<>();
        if (error.isEmpty()) {
            Map<String, Object> printResult = new HashMap<>();
            for (Map<String, Object> printItem : printList) {
                templateName = getStringParam(printItem, "templateName");
                if (!templateName.isEmpty()) {
                    printResult = printOptionReports(templateName, prodProgSysName,
                            subResult, reportData, login, password);
                }
                if (templateName.isEmpty() && getBooleanParam(printItem, "isPrintZovi", false)) {
                    printResult = printAdditionalAgreement(params, reportData, subResult,
                            login, password);
                }
                error = getStringParam(printResult, ERROR);
                if (!error.isEmpty()) {
                    break;
                }
                if (!printResult.isEmpty()) {
                    printResultList.add(printResult);
                }
            }

            if (printResultList.isEmpty()) {
                //пока что комментируем, т.к. не для всех реализована печать
                //error = DEFAULT_CLAIM_NO_TEMPLATE_ERROR_MSG;
            }
        }

        //пока что не пытаемся прикреплять, если прикреплять нечего, т.к. не для всех реализована печать
        if (error.isEmpty() && !printResultList.isEmpty() && (declarationId != null)) {
            Map<String, Object> attachResult;
            String binDocTypeSysName;
            for (Map<String, Object> printResult : printResultList) {
                reportName = getStringParam(printResult, "reportName");
                docType = getStringParam(printResult, "docType");
                binDocTypeSysName = getStringParam(printResult, "BINDOCTYPESYSNAME");
                attachResult = attachePrintFile(declarationId, reportName, docType, binDocTypeSysName, reportData,
                        subResult, login, password
                );
                error = getStringParam(attachResult, ERROR);
                if (!error.isEmpty()) {
                    break;
                }
            }
        }
        // формирование результата
        loggerDebugPretty(logger, "dsB2BLossPaymentClaimPrint sub result", subResult);
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            result.put("subResult", subResult); // !только для отладки!
            // формирование результата
            // todo: изменить, когда требуемые параметры уточнит группа разработки интерфейсов (см. гуглодок)
            result.put(REPORT_DATA_PARAMETER_NAME, subResult.get(REPORT_DATA_PARAMETER_NAME));
            // кладем наименование сформированого файла в результурующую мапу
            // т.к. для печати без договора у нас всегда будет один файл
            result.put("reportName", printResultList.get(0).get("reportName"));
        } else {
            result.put(ERROR, error);
            // доп. сведения о собранных для отчета данных, могут быть полезны для уточнения условий ошибка
            result.put(REPORT_DATA_PARAMETER_NAME, subResult.get(REPORT_DATA_PARAMETER_NAME));
        }
        loggerDebugPretty(logger, "dsB2BLossPaymentClaimPrint result", result);
        logger.debug("dsB2BDeclarationOfChangePrint end");
        return result;
    }

    /**
     * Функция прикрепления напечатанных файлов к сузности
     *
     * @param declarationId идентификатор, к которому требуется прикрепить файл
     * @param reportName    имя напечатанного отчета
     * @param docType       тип отчета, если он есть
     * @param reportData    мапа сформированных данных, для получение типа отчета по системному имени
     * @param subResult     мапа вспомогательного результа
     * @param login         логин
     * @param password      пароль
     * @return
     * @throws Exception
     */
    private Map<String, Object> attachePrintFile(Long declarationId, String reportName, String docType,
                                                 String binDocTypeSysName, Map<String, Object> reportData,
                                                 Map<String, Object> subResult, String login,
                                                 String password) throws Exception {
        // Сохранение уже существующего файла в SeaweedFS (если требуется); получение подробной информации о файле (всегда)
        Map<String, Object> binFileParams = trySaveReportToSeaweeds(reportName, ".pdf");
        //loggerDebugPretty(logger, "binFileParams from trySaveReportToSeaweeds", binFileParams); // спам
        // проверка наличия уже прикрепленных ранее файлов такого же типа
        String browseMethodName = "dsB2BDeclarationOfChangeAttachment_BinaryFile_BinaryFileBrowseListByParam";
        Map<String, Object> contractChangeTypeMap = new HashMap<>();
        String binDocType = "300300";
        if (binDocTypeSysName.isEmpty() && !docType.isEmpty()) {
            Map<String, Object> binDocQueryParams = new HashMap<>();
            binDocQueryParams.put("BINDOCTYPEID", docType);
            binDocQueryParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> binDocTypeResult = this.callServiceLogged(B2BPOSWS_SERVICE_NAME,
                    "dsB2BBinDocTypeBrowseListByParamEx", binDocQueryParams, login, password);
            String fileName = getStringParam(binDocTypeResult, "NAME");
            contractChangeTypeMap.put("FILETYPEID", getLongParam(binDocTypeResult, "BINDOCTYPEID"));
            fileName = fileName.trim() + ".pdf";
            binDocType = getStringParam(binDocTypeResult, "BINDOCTYPE");
            contractChangeTypeMap.put("FILENAME", fileName);
            contractChangeTypeMap.put("FILETYPENAME", getStringParam(binDocTypeResult, "NAME"));

//                contractChangeTypeMap = CONTRACT_CHANGE_CLAIM.getFileTypeMap();
//                if (params.get("ISPRINTCANCELDOC") != null) {
//                    contractChangeTypeMap = CONTRACT_CHANGE_CANCEL_CLAIM.getFileTypeMap();
//                }
//                if (!docType.isEmpty()) {
//                    contractChangeTypeMap.put(BinFileType.TYPE_ID_PARAMNAME, docType);
//                }
        } else {
            Map<String, Object> binDocQueryParams = new HashMap<>();
            binDocQueryParams.put("ENTITYSYSNAME", binDocTypeSysName);
            binDocQueryParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> binDocTypeResult = this.callServiceLogged(B2BPOSWS_SERVICE_NAME,
                    "dsB2BBinDocTypeBrowseListByParamEx", binDocQueryParams, login, password);
            String fileName = getStringParam(binDocTypeResult, "NAME") + " " + getStringParam(reportData, "FILENAMEUPDATESTR")
                    + " " + (getBooleanParam(reportData, "ISNEEDADDCONTRNUMBER", false) ? getStringParam(reportData, "CONTRNUMBERANSER") : "");
            fileName = fileName.trim() + ".pdf";
            contractChangeTypeMap.put("FILETYPEID", getLongParam(binDocTypeResult, "BINDOCTYPEID"));
            binDocType = getStringParam(binDocTypeResult, "BINDOCTYPE");
            contractChangeTypeMap.put("FILENAME", fileName);
            contractChangeTypeMap.put("FILETYPENAME", getStringParam(binDocTypeResult, "NAME"));
        }
        Map<String, Object> binFileBrowseParams = new HashMap<String, Object>(contractChangeTypeMap);
        binFileBrowseParams.put("OBJID", declarationId);
        subResult.put("binFileBrowseParams", binFileBrowseParams);
        List<Map<String, Object>> existedBinFileList = this.callServiceAndGetListFromResultMapLogged(THIS_SERVICE_NAME,
                browseMethodName, binFileBrowseParams, login, password
        );
        subResult.put("existedBinFileList", existedBinFileList);
        if ((existedBinFileList != null) && (!existedBinFileList.isEmpty())) {
            // удаление прикрепленных ранее файлов такого же типа
            for (Map<String, Object> exisitingBinFile : existedBinFileList) {
                String deleteMethodName = "dsB2BDeclarationOfChangeAttachment_BinaryFile_deleteBinaryFileInfo";
                Map<String, Object> deleteBinFileParams = new HashMap<String, Object>();
                deleteBinFileParams.put("BINFILEID", exisitingBinFile.get("BINFILEID"));
                try {
                    Map<String, Object> binFileInfoDeleteResult = this.callServiceLogged(THIS_SERVICE_NAME,
                            deleteMethodName, deleteBinFileParams, login, password
                    );
                    exisitingBinFile.put("binFileInfoDeleteResult", binFileInfoDeleteResult);
                } catch (Exception ex) {
                    logger.error(String.format(
                            "Unable to delete file by calling '%s' with params: %s! Details (exception):",
                            deleteMethodName, deleteBinFileParams
                    ), ex);
                }
            }
        }
        // прикрепление только что сформированной новой ПФ
        binFileParams.putAll(contractChangeTypeMap);
        binFileParams.put("OBJID", declarationId);
        binFileParams.put("NOTE", "");
        subResult.put("BINFILEPARAM", binFileParams);
        binFileParams.put(RETURN_AS_HASH_MAP, true);
        String attachMethodName = "dsB2BDeclarationOfChangeAttachment_BinaryFile_createBinaryFileInfo";
        Map<String, Object> binFileInfoCreateResult = null;
        try {
            binFileInfoCreateResult = this.callServiceLogged(THIS_SERVICE_NAME, attachMethodName, binFileParams, login, password);

            Map<String, Object> lossNoticeDoc = new HashMap<String, Object>();
            lossNoticeDoc.put("DECLARATIONID", declarationId);
            // константа из sberlifelk/src/app/components/loss-upload-wraper/requaredParams.json
            // todo: переделать на справочник, выбирать ид по системному имени типа документа.
            lossNoticeDoc.put("BINDOCTYPE", binDocType);
            if (binFileInfoCreateResult.get("BINFILEID") != null) {
                lossNoticeDoc.put("EXTERNALID", binFileInfoCreateResult.get("BINFILEID"));
            }

            lossNoticeDoc.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> lossNoticeDocRes = this.callServiceLogged(THIS_SERVICE_NAME, "dsPDDeclarationDocCreate",
                    lossNoticeDoc, login, password
            );
            if (lossNoticeDocRes.get("DECLARATIONDOCID") != null) {
                binFileParams.put("OBJID", lossNoticeDocRes.get("DECLARATIONDOCID"));

                Map<String, Object> binFileInfoCreateDocResult = this.callServiceLogged(THIS_SERVICE_NAME,
                        "dsPDDeclarationDoc_BinaryFile_createBinaryFileInfo", binFileParams, login, password
                );
            }

        } catch (Exception ex) {
            logger.error(String.format("Unable to attach file by calling '%s' with params: %s! Details (exception):",
                    attachMethodName, binFileParams), ex
            );
        }
        subResult.put("BINFILERES", binFileInfoCreateResult);
        Map<String, Object> result = new HashMap<>();
        if (isCallResultOKAndContains(binFileInfoCreateResult, "BINFILEID")) {
            // успех
            Long binFileId = getLongParamLogged(binFileInfoCreateResult, "BINFILEID");
            if (binFileId != null) {
                result.put("BINFILEID", binFileId);
            } else {
                result.put(ERROR, DEFAULT_CLAIM_BIN_FILE_ERROR_MSG);
            }
        }
        return result;
    }

    /**
     * Метод печати заявления по опция
     *
     * @param settingValue    путь до шаблона
     * @param prodProgSysName имя программы
     * @param subResult       мапа вспомогатиельного рузльтата
     * @param reportData      мапа сформированных данные, для получение требуемых дат
     * @param login           логин
     * @param password        пароль
     * @return
     * @throws Exception
     */
    private Map<String, Object> printOptionReports(String settingValue, String prodProgSysName,
                                                   Map<String, Object> subResult, Map<String, Object> reportData,
                                                   String login, String password) throws Exception {
        // Проверка специальных кастомных значений для отдельных програм (по programSysName)
        // синтаксис в базе, пример кастомного значения для Маяка (prodProgSysname = LIGHTHOUSE)
        // CONTRADDCHT/fondChangeI.odt$200001;31.03.2016:CONTRADDCHT/fondChangeA.odt$200000&LIGHTHOUSE:CONTRADDCHT/fondChangeA.odt$200000
        // Разделяются символом &
        String templateName = "";
        if (settingValue.contains("&")) {
            String[] templateStr = settingValue.split("&");
            settingValue = templateStr[0];
            for (String templateSubStr : templateStr) {
                if ((templateSubStr != null) && (!templateSubStr.isEmpty()) && (prodProgSysName != null)) {
                    if (templateSubStr.contains(prodProgSysName)) {
                        settingValue = templateSubStr.substring(templateSubStr.indexOf(":") + 1, templateSubStr.length());
                    }
                }

            }
        }
        if (settingValue.contains(";")) {
            // выбор по дате
            Map<Date, String> templateByDate = new HashMap<Date, String>();
            String[] templateStr = settingValue.split(";");
            for (String templateSubStr : templateStr) {
                if ((templateSubStr != null) && (!templateSubStr.isEmpty())) {
                    if (templateSubStr.contains(":")) {
                        String[] templateAndDate = templateSubStr.split(":");
                        if (templateAndDate.length >= 2) {
                            Date templateDate = (Date) parseAnyDate(templateAndDate[0], Date.class, "*", true);
                            templateByDate.put(templateDate, templateAndDate[1]);
                        }
                    } else {
                        templateByDate.put(new Date(1), templateSubStr);
                    }
                }
            }
            // нормальная дата (дата заключения договора)
            Date contractDate = getDateParam(reportData.get("DOCUMENTDATE"));
            // todo: доработать, когда будет решен вопрос с получением даты договора из интеграции
            if (contractDate == null) {
                // нормальная дата отсутствует - используется приблизительная дата
                contractDate = getDateParam(reportData.get("STARTDATE"));
            }
            if (contractDate == null) {
                // нормальная и приблизительные даты отсутствуют - используется текущая дата
                contractDate = new Date();
            }
            Date templateDate = new Date(0);
            for (Map.Entry<Date, String> entry : templateByDate.entrySet()) {
                Date date = entry.getKey();
                if (contractDate.after(date)) {
                    if (templateDate.before(date)) {
                        templateDate = date;
                        templateName = entry.getValue();
                    }
                }
            }
        } else {
            templateName = settingValue;
        }

        Map<String, Object> result = new HashMap<>();
        String[] templateNameAndDocType = templateName.split("\\$");
        if (templateNameAndDocType.length > 1) {
            templateName = templateNameAndDocType[0];
            result.put("docType", templateNameAndDocType[1]);
        }

        Map<String, Object> printRepResult = printReport(templateName, reportData, subResult, login, password);
        String error = getStringParam(printRepResult.get(ERROR));
        if (!error.isEmpty()) {
            result.put(ERROR, error);
        } else {
            result.put("reportName", getStringParam(printRepResult.get("reportName")));
        }
        return result;
    }

    /**
     * Метод печати для допсов
     *
     * @param params     параметры
     * @param reportData мапа сформированных данные, для анализа того что нужно напечатать
     * @param subResult  мапа вспомогатиельного рузльтата
     * @param login      логин
     * @param password   пароль
     * @return
     * @throws Exception
     */
    private Map<String, Object> printAdditionalAgreement(Map<String, Object> params, Map<String, Object> reportData,
                                                         Map<String, Object> subResult, String login,
                                                         String password) throws Exception {
        Map<String, Object> templatesQueryParamsByProduct = new HashMap<>();
        Long prodConfId = getLongParam(params.get("PRODCONFID"));
        if (prodConfId == null) {
            prodConfId = getLongParam(reportData, "PRODCONFID");
        }
        String repLevel = getStringParam(reportData, "REPLEVEL");
        if (repLevel.contains(",")) {
            templatesQueryParamsByProduct.put("REPLEVELLIST", repLevel.replaceAll("\\,+", ",").replaceAll("\\,$", ""));
        } else {
            templatesQueryParamsByProduct.put("REPLEVEL", repLevel);
        }
        templatesQueryParamsByProduct.put("PRODCONFID", prodConfId);
        templatesQueryParamsByProduct.put("ORDERBYPRODREPID", true);
        List<Map<String, Object>> repDocList = new ArrayList<>();
        String error = "";
        if (!repLevel.isEmpty()) {
            repDocList = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, "dsB2BProductReportBrowseListByParamEx",
                    templatesQueryParamsByProduct, login, password
            );
        }

        if (repDocList.isEmpty()) {
            error = "Для данного продукта отсутствует шаблон ПФ.";
        }

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> reportFileNames = new ArrayList<>();
        if (error.isEmpty()) {
            String fileName = "";
            Map<String, Object> bean = new HashMap<>();
            //FILEPATH
            String templateName = "";
            for (Map<String, Object> repDoc : repDocList) {
                bean = new HashMap<>();
                templateName = getStringParam(repDoc, "TEMPLATENAME");
                if (templateName.endsWith(".pdf")) {
                    String project = this.getProjectName(login, password);
                    String metadataURL = getMetadataURL(login, password, project);

                    if ((metadataURL == null) || (metadataURL.equals(""))) {
                        metadataURL = Config.getConfig("reportws")
                                .getParam("rootPath", "C:/bea/workshop92/METADATA/REPORTS/");
                    } else {
                        if ((!metadataURL.endsWith("/")) && (!metadataURL.endsWith("\\"))) {
                            metadataURL = metadataURL + File.separator;
                        }
                        if (!templateName.contains("REPORTS/")) {
                            metadataURL = metadataURL + "REPORTS/";
                        }
                    }
                    fileName = templateName;
                    bean.put("FILEPATH", metadataURL);
                    bean.put("FILENAME", fileName);
                    reportFileNames.add(bean);
                }
                if (templateName.endsWith(".odt")) {
                    Map<String, Object> printRepResult = printReport(templateName, reportData, subResult, login, password);
                    error = getStringParam(printRepResult.get("error"));
                    if (!error.isEmpty()) {
                        break;
                    } else {
                        fileName = getStringParam(printRepResult.get("reportName"))
                                + getStringParam(printRepResult, "REPORTFORMATS");
                        bean.put("FILENAME", fileName);
                        reportFileNames.add(bean);
                    }
                }
                if (templateName.endsWith(".ods")) {
                    if (getBooleanParam(reportData, "IS_NEED_ADDITIONAL_CHILD_FORM", false)
                            && getStringParam(repDoc, "NAME").contains("Ребенок")) {
                        Map<String, Object> printRepResult = printReport(templateName, reportData, subResult, login, password);
                        error = getStringParam(printRepResult.get("error"));
                        if (!error.isEmpty()) {
                            break;
                        } else {
                            fileName = getStringParam(printRepResult.get("reportName"))
                                    + getStringParam(printRepResult, "REPORTFORMATS");
                            reportData.put("IS_NEED_ADDITIONAL_CHILD_FORM", false);
                            bean.put("FILENAME", fileName);
                            reportFileNames.add(bean);
                        }
                    }
                    if (getBooleanParam(reportData, "IS_NEED_ADDITIONAL_FORM", false)
                            && getStringParam(repDoc, "NAME").contains("Взрослый")) {
                        List<Map<String, Object>> insuredList = getOrCreateListParam(reportData, "INSUREDMAPLIST");
                        for (Map<String, Object> item : insuredList) {
                            reportData.put("INSUREDMAP", item);
                            Map<String, Object> printRepResult = printReport(templateName, reportData, subResult, login, password);
                            error = getStringParam(printRepResult.get("error"));
                            if (!error.isEmpty()) {
                                break;
                            } else {
                                fileName = getStringParam(printRepResult.get("reportName"))
                                        + getStringParam(printRepResult, "REPORTFORMATS");
                                bean.put("FILENAME", fileName);
                                reportFileNames.add(bean);
                            }
                        }
                        if (!error.isEmpty()) {
                            break;
                        } else {
                            reportData.put("IS_NEED_ADDITIONAL_FORM", false);
                        }
                    }
                }
            }
        }

        if (error.isEmpty()) {
            Map<String, Object> mergeParams = new HashMap<>();
            mergeParams.put("REPORTSFILENAMES", reportFileNames);
            mergeParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> qRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BMergeReports",
                    mergeParams, login, password);
            if ((qRes != null) && (qRes.get("RESULT") != null)) {
                String reportName = getStringParam(qRes.get("RESULT"));
                result.put("reportName", reportName);
                result.put("BINDOCTYPESYSNAME", getStringParam(reportData, "BINDOCTYPESYSNAME"));
            }
        }

        if (!error.isEmpty()) {
            result.put(ERROR, error);
            // доп. сведения о собранных для отчета данных, могут быть полезны для уточнения условий ошибка
            result.put(REPORT_DATA_PARAMETER_NAME, subResult.get(REPORT_DATA_PARAMETER_NAME));
        }
        loggerDebugPretty(logger, "dsB2BLossPaymentClaimPrint result", result);
        logger.debug("dsB2BLossPaymentClaimPrint end");
        return result;
    }

    private Map<String, Object> printReport(String templateName, Map<String, Object> reportData,
                                            Map<String, Object> subResult, String login, String password)
            throws Exception {
        // по умолчанию считается, что произошла ошибка
        String error = DEFAULT_CLAIM_PRINT_ERROR_MSG;
        Map<String, Object> reportParams = new HashMap<String, Object>();
        //reportParams.put("REPORTFORMATS", ".pdf"); // необязательный параметр для dsLibreOfficeReportCreate, pdf будет использован по умолчанию
        //reportParams.put("reportName", "reportName"); // необязательный параметр, в dsLibreOfficeReportCreate по умолчанию будет использовано UUID.randomUUID().toString()
        reportParams.put("templateName", templateName);
        reportParams.put(REPORT_DATA_PARAMETER_NAME, reportData);
        reportParams.put("enableUserStamp", true);
        reportParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> reportPrintResult = this.callServiceLogged(LIBREOFFICEREPORTSWS_SERVICE_NAME,
                "dsLibreOfficeReportCreate", reportParams, login, password
        );
        subResult.put("PRINTRES", reportPrintResult);
        Map<String, Object> result = new HashMap<>();
        String reportName = "";
        if (isCallResultOKAndContains(reportPrintResult, REPORT_DATA_PARAMETER_NAME)) {
            Map<String, Object> printResultReportData = getMapParam(reportPrintResult, REPORT_DATA_PARAMETER_NAME);
            reportName = getStringParam(printResultReportData, "reportName");
            if (!reportName.isEmpty()) {
                // успех
                result.put("reportName", reportName);
                result.put("REPORTFORMATS", getStringParam(printResultReportData, "REPORTFORMATS"));
                error = "";
            }
        }
        result.put(ERROR, error);
        return result;
    }

    private String getMetadataURL(String login, String password, String project) throws Exception {
        Map args = new HashMap();
        args.put("fileType", "REPORTS");
        args.put(PROJECT_PARAM_NAME, project);
        String adminwsURL = Config.getConfig().getParam("adminws", "http://localhost:8080/adminws/adminws");
        String COREWSURL = Config.getConfig().getParam("corews", "http://localhost:8080/corews/corews");
        try {
            XMLUtil xmlutil = new XMLUtil(login, password);
            args.put("PROJECTSYSNAME", project);
            Map resultMap = xmlutil.doURL(adminwsURL, "admProjectBySysname", xmlutil.createXML(args), null);

            List result = (List) resultMap.get("Result");
            if (logger.isDebugEnabled()) {
                logger.debug("projectBySysname result = " + result);
            }
            if ((result != null) && (result.size() == 1)
                    && (((Map) result.get(0)).containsKey("METADATAURL")) && ((((Map) result.get(0)).get("METADATAURL") instanceof String))
                    && (!((Map) result.get(0)).get("METADATAURL").toString().equals(""))) {
                return ((Map) result.get(0)).get("METADATAURL").toString();
            }

            resultMap = xmlutil.doURL(COREWSURL, "getSystemMetadataURL", xmlutil.createXML(args), null);
            if (logger.isDebugEnabled()) {
                logger.debug("getMetadataURL result = " + resultMap);
            }
            if (resultMap.containsKey("MetadataURL")) {
                return resultMap.get("MetadataURL").toString();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
        return null;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAMETER_NAME, URLPATH_PARAMETER_NAME})
    public Map<String, Object> dsB2BDeclarationOfChangePrintWithoutDecalaration(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAMETER_NAME);
        Map<String, Object> declarationMap = getOrCreateMapParam(reportData, "DECLARATIONMAP");
        List<Map<String, Object>> reasons = getOrCreateListParam(declarationMap, "reasons");
        String reasonSysname;
        String error = "";
        for (Map<String, Object> reason : reasons) {
            reasonSysname = getStringParam(reason, "sysname");
            if (notRealizationPfList.contains(reasonSysname)) {
                error = "Данный тип изменения не реализован.";
                break;
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> documentDownloadInfo = new HashMap<>();
        if (error.isEmpty()) {
            Map<String, Object> requestMap = new HashMap<>(reportData);
            // флаг, чтобы не анализировать и не пытаться загрузить полную мапу договора
            requestMap.put("isNotExistContract", true);
            requestMap.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> responseMap = callService(SIGNB2BPOSWS_SERVICE_NAME, "dsB2BDeclarationOfChangePrint",
                    requestMap, login, password);
            error = getStringParamLogged(responseMap, ERROR);
            if ((!isCallResultOK(responseMap))) {
                error = "Не удалось сформировать печатные документы для прикрепления к заявлению!";
            }
            if (error.isEmpty()) {
                String reportName = getStringParam(responseMap, "reportName");
                documentDownloadInfo = generateDownloadPath(params, reportName);
                if (documentDownloadInfo.isEmpty()) {
                    error = "Не удалось сформировать путь для скачивания файла";
                }
            }
        }

        if (error.isEmpty()) {
            resultMap.put("DOCUMENTINFO", documentDownloadInfo);
        } else {
            resultMap.put(ERROR, error);
        }
        return resultMap;
    }

    private Map<String, Object> generateDownloadPath(Map<String, Object> params, String reportName) throws Exception {
        String urlPath = getStringParam(params, URLPATH_PARAMETER_NAME);
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> result = new HashMap<>();
        if ((!urlPath.isEmpty()) && (!reportName.isEmpty())) {
            Map<String, Object> report = new HashMap<String, Object>();
            report.put("FILEPATH", reportName + ".pdf");
            report.put("FILENAME", "Заявление.pdf");
            //сюда docItem с filepath и filename; возможно установить ispdf?
            List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();
            docList.add(report);
            //в идеале генерить sessionid по clientProfileId или проверять корректность
            processDocListForUpload(docList, params, login, password);
            result = docList.get(0);
        }
        return result;
    }
}
