/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularSisContractCustomFacade.EUR_NAMES;
import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularSisContractCustomFacade.RUB_NAMES;
import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularSisContractCustomFacade.USD_NAMES;
import com.bivgroup.services.bivsberposws.system.Constants;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;
import ru.diasoft.utils.currency.AmountUtils;

/**
 *
 * @author averichevsm
 */
@BOName("AngularReportCustom")
public class AngularReportCustomFacade extends AngularContractCustomBaseFacade {

    private static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    private static final String INSPRODUCTWS_SERVICE_NAME = Constants.INSPRODUCTWS;
    private static final String LIBREOFFICEREPORTSWS_SERVICE_NAME = Constants.LIBREOFFICEREPORTSWS;
    private static final String SIGNWS_SERVICE_NAME = Constants.SIGNWS;

    protected static final int MAX_SECOND = 59;
    public static final String[] MONTH_NAMES = {"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};
    private static final String PROJECT_PARAM_NAME = "project";

    private String getUploadFilePath() {

        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();

        return result;
    }

    protected String getMetadataURL(String login, String password, String project) throws Exception {
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

    /**
     * Fix т.к. система обрезает в датах секунды
     *
     * @param date - дата
     * @param isFixSeconds - флаг добавления секунд до 59 (важно!!! должно быть
     * 59, никаких 50 )
     */
    protected XMLGregorianCalendar dateToXMLGC(Date date, boolean isFixSeconds) throws Exception {
        XMLGregorianCalendar result = null;
        if (date != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(date);
            if (isFixSeconds) {
                gc.set(Calendar.SECOND, MAX_SECOND);
            }
            try {
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            } catch (DatatypeConfigurationException ex) {
                throw new Exception("Error convert Date to XMLGregorianCalendar", ex);
            }

        }
        return result;
    }

    protected XMLGregorianCalendar dateToXMLGC(Date date) throws Exception {
        return dateToXMLGC(date, false);
    }

    protected String getStringByDate(Date date) throws Exception {
        XMLGregorianCalendar xmlgc = dateToXMLGC(date);
        String result = "";
        if (xmlgc != null) {
            if (xmlgc.getDay() < 10) {
                result = "«0" + xmlgc.getDay() + "» " + MONTH_NAMES[xmlgc.getMonth() - 1] + " " + xmlgc.getYear() + "";
            } else {
                result = "«" + xmlgc.getDay() + "» " + MONTH_NAMES[xmlgc.getMonth() - 1] + " " + xmlgc.getYear() + "";
            }
        }
        return result;
    }

    protected String getCurrByCodeToNum(String curCode, long amValueInt) {
        String[] CurrNames = RUB_NAMES;
        if (curCode.equalsIgnoreCase("RUB")) {
            CurrNames = RUB_NAMES;
        }
        if (curCode.equalsIgnoreCase("USD")) {
            CurrNames = USD_NAMES;
        }
        if (curCode.equalsIgnoreCase("EUR")) {
            CurrNames = EUR_NAMES;
        }
        String result = CurrNames[0];

        int rank10 = (int) (amValueInt % 100 / 10);
        int rank = (int) (amValueInt % 10);
        if (rank10 == 1) {
            result = CurrNames[2];
        } else {
            switch (rank) {
                case 1:
                    result = CurrNames[0];
                    break;
                case 2:
                case 3:
                case 4:
                    result = CurrNames[1];
                    break;
                default:
                    result = CurrNames[2];
            }
        }
        return result;
    }

    protected Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }

    @WsMethod(requiredParams = {"CONTRID", "SERVICENAME", "METHODNAME"})
    public Map<String, Object> dsPrintReport(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        params.put("ReturnAsHashMap", "TRUE");
        String serviceName = params.get("SERVICENAME").toString();
        String methodName = params.get("METHODNAME").toString();
        logger.debug("dsPrintReport start");
        //Map<String, Object> result = this.callService(BIVSBERPOSWS_SERVICE_NAME,"dsHabContractBrowseListParamEx", params, login, password);
        Map<String, Object> result = this.callService(serviceName, methodName, params, login, password);
        // todo: возможно, заменить на проверку result.get("CONTRMAP") != null ?
        if (!methodName.equalsIgnoreCase("dsB2BVZRPrintDocDataProvider")) {
            if (result.get("INSBIRTHDATE") == null) {
                result = (Map<String, Object>) result.get("CONTRMAP");
            }
        }
        Map<String, Object> contrExt = (Map<String, Object>) result.get("CONTREXTRATTR");
        Long tzOffset = 5L * 60;
        if (contrExt == null) {
            if (result.get("LOCALTZOFFSET") != null) {
                tzOffset = Long.valueOf(result.get("LOCALTZOFFSET").toString());
                tzOffset = tzOffset * (-1);
            }
        } else {
            if (contrExt.get("localTZOffset") != null) {
                tzOffset = Long.valueOf(contrExt.get("localTZOffset").toString());
                tzOffset = tzOffset * (-1);
            }
        }

        //fix date
        Date bd = null;
        if ((result.get("INSBIRTHDATE") != null) && (result.get("INSBIRTHDATE") instanceof Date)) {
            bd = (Date) result.get("INSBIRTHDATE");
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(bd);
            if (gc.get(Calendar.HOUR) != 0) {
                bd.setTime(bd.getTime() + tzOffset * 60 * 1000);
                result.put("INSBIRTHDATE", bd);
            }
        }

        if ((result.get("INSISSUEDATE") != null) && (result.get("INSISSUEDATE") instanceof Date)) {
            bd = (Date) result.get("INSISSUEDATE");
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(bd);
            if (gc.get(Calendar.HOUR) != 0) {
                bd.setTime(bd.getTime() + tzOffset * 60 * 1000);
                result.put("INSISSUEDATE", bd);
            }
        }

        if ((result.get("STARTDATE") != null) && (result.get("STARTDATE") instanceof Date)) {
            bd = (Date) result.get("STARTDATE");
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(bd);
            if (gc.get(Calendar.HOUR) != 0) {
                bd.setTime(bd.getTime() + tzOffset * 60 * 1000);
                result.put("STARTDATE", bd);
            }
        }

        if ((result.get("FINISHDATE") != null) && (result.get("FINISHDATE") instanceof Date)) {
            bd = (Date) result.get("FINISHDATE");
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(bd);
            if (gc.get(Calendar.HOUR) != 0) {
                bd.setTime(bd.getTime() + tzOffset * 60 * 1000);
                result.put("FINISHDATE", bd);
            }
        }
        //  if (params.get("SHENGENDOPDAYS") != null) {
        //      recalcFinishDate(result, params);
        //  }

        if ((result.get("DOCUMENTDATE") != null) && (result.get("DOCUMENTDATE") instanceof Date)) {
            bd = (Date) result.get("DOCUMENTDATE");
            result.put("DOCUMENTDATESTR", getStringByDate(bd));
            result.put("PRINTDATESTR", getStringByDate(bd));
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(bd);
            if (gc.get(Calendar.HOUR) != 0) {
                bd.setTime(bd.getTime() + tzOffset * 60 * 1000);
                result.put("DOCUMENTDATE", bd);
            }
        } else {
            result.put("PRINTDATESTR", getStringByDate(new Date()));
        }

        if (result.get("INSUREDLIST") != null) {
            List<Map<String, Object>> ilist = (List<Map<String, Object>>) result.get("INSUREDLIST");
            for (Map<String, Object> insObj : ilist) {
                if (insObj.get("BIRTHDATE") != null) {
                    bd = (Date) insObj.get("BIRTHDATE");
                    /*  GregorianCalendar gc = new GregorianCalendar();
                     gc.setTime(bd);
                    
                     gc.add(Calendar.DATE, 1);
                     bd = gc.getTime();
                     insObj.put("BIRTHDATE", bd);*/

                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(bd);
                    if (gc.get(Calendar.HOUR) != 0) {
                        bd.setTime(bd.getTime() + tzOffset * 60 * 1000);
                        insObj.put("BIRTHDATE", bd);
                    }
                }
            }
        }
        Double prem = getDoubleParam(result.get("PREMVALUE"));

        String premString = AmountUtils.amountToString(prem, "810");

        result.put("PREMSTR", premString);
        result.put("PREMCURRENCYSTR", getCurrByCodeToNum("RUB", prem.longValue()));
        Double insAmValue = getDoubleParam(result.get("INSAMVALUE"));

        String insAmValueString = AmountUtils.amountToString(insAmValue, "810");

        result.put("INSAMVALUESTR", insAmValueString);
        result.put("INSAMVALUECURRENCYSTR", getCurrByCodeToNum("RUB", insAmValue.longValue()));

        boolean isB2BModeFlag = isB2BMode(params);

        String productServiceName; //       имя сервиса для получения данных продукта (списка документов)
        String filesServiceName; //         имя сервиса для работы с файлами
        String reportBrowseMethodName; //   имя метода для получения списка документов для продукта
        String filesBrowseMethodName; //    имя метода для получения списка файлов контракта
        String filesCreateMethodName; //    имя метода для создания файла к контрактк
        Object prodConfID; //               идентификатор продукта
        String templateFieldName; //        ключ, указывающий на имя шаблона
        if (isB2BModeFlag) {
            productServiceName = B2BPOSWS_SERVICE_NAME;
            filesServiceName = B2BPOSWS_SERVICE_NAME;
            reportBrowseMethodName = "dsB2BProductReportBrowseListByParamEx";
            filesBrowseMethodName = "dsB2BContract_BinaryFile_BinaryFileBrowseListByParam";
            filesCreateMethodName = "dsB2BContract_BinaryFile_createBinaryFileInfo";
            prodConfID = convertValue(result.get("PRODCONFID"), getProdConfIDConvertRules(), Direction.TO_SAVE);
            templateFieldName = "TEMPLATENAME";
        } else {
            productServiceName = INSPRODUCTWS_SERVICE_NAME;
            filesServiceName = INSPOSWS_SERVICE_NAME;
            reportBrowseMethodName = "dsProductReportBrowseListByParamEx";
            filesBrowseMethodName = "dsContract_BinaryFile_BinaryFileBrowseListByParam";
            filesCreateMethodName = "dsContract_BinaryFile_createBinaryFileInfo";
            prodConfID = result.get("PRODCONFID");
            templateFieldName = "PAGEFLOWNAME";
        }

        Map<String, Object> qProdRepParam = new HashMap<String, Object>();
        qProdRepParam.put("PRODCONFID", prodConfID);
        Map<String, Object> qProdRepRes = this.callService(productServiceName, reportBrowseMethodName, qProdRepParam, login, password);
        Map<String, Object> filePathList = new HashMap<String, Object>();
        if (qProdRepRes.get(RESULT) != null) {
            List<Map<String, Object>> listRep = WsUtils.getListFromResultMap(qProdRepRes);
            logger.debug("listRep: " + listRep.toString());
            String repFormat = ".pdf";
            List<Map<String, Object>> repDataList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> report : listRep) {
                if (report.get(templateFieldName) != null) {
                    String path = report.get(templateFieldName).toString();
                    // если это odt или ods то это шаблоны, и их сразу в сервис получения данных, 
                    if ((path.indexOf(".odt") > 1) || (path.indexOf(".ods") > 1)) {
                        logger.debug("template print");
                        Map<String, Object> binQueryParams = new HashMap<String, Object>();
                        binQueryParams.put("OBJID", params.get("CONTRID"));
                        binQueryParams.put("ReturnAsHashMap", true);
                        Map<String, Object> findRes = this.callService(filesServiceName, filesBrowseMethodName, binQueryParams, login, password);
                        if ((findRes.get("BINFILEID") != null) && ((Long) findRes.get("BINFILEID") > 0)) {
                            String fullPath = getTemplateFullPath(path, login, password);
                            fullPath = fullPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
                            logger.debug("binfile exist");

                            logger.debug(fullPath);
                            if (fullPath.toUpperCase().indexOf("UPLOAD") < 0) {
                                //файл прикреплен без пути.
                                //берем путь из конфига
                                String upFilePath = getUploadFilePath();
                                filePathList.put(findRes.get("FILENAME").toString(), upFilePath + findRes.get("FILEPATH").toString());
                            } else {
                                filePathList.put(findRes.get("FILENAME").toString(), findRes.get("FILEPATH").toString());
                            }
                        } else {
                            logger.debug("binfile not exist");
                            Map<String, Object> printParams = new HashMap<String, Object>();
                            // проставляем дату вручения сейчас т.к. отправка будет сразу за формированием полиса
                            //result.put("PRINTDATESTR", getStringByDate(new Date()));
                            if (methodName.equalsIgnoreCase("dsHabContractBrowseListParamEx")) {
                                //для защиты дома для нового вида надо тип объекта в печать.
                                if (result.get("OBJNAME") == null) {
                                    if (result.get("objTypeId") != null) {
                                        if ("1".equals(result.get("objTypeId").toString())) {
                                            result.put("OBJNAME", "Дом");
                                        } else {
                                            result.put("OBJNAME", "Квартира");
                                        }
                                    }
                                }
                            }

                            printParams.put("REPORTDATA", result);
                            printParams.put("templateName", path);
                            printParams.put("REPORTFORMATS", repFormat);
                            printParams.put("ReturnAsHashMap", "TRUE");

                            Map<String, Object> printRes = this.callService(LIBREOFFICEREPORTSWS_SERVICE_NAME, "dsLibreOfficeReportCreate", printParams, login, password);
                            logger.debug("printRes: " + printRes.toString());

                            if (printRes.get("REPORTDATA") != null) {
                                Map<String, Object> reportData = (Map<String, Object>) printRes.get("REPORTDATA");
                                reportData.put("templateName", "Страховой полис");
                                repDataList.add(reportData);
                                String reportName = "";
                                if (reportData.get("reportName") != null) {
                                    reportName = reportData.get("reportName").toString();
                                    String fullPath = getReportFullPath(reportName, repFormat);
                                    fullPath = fullPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
                                    String destPath = fullPath.substring(0, fullPath.length() - 4) + "_signed"
                                            + fullPath.substring(fullPath.length() - 4, fullPath.length());

                                    boolean signRes = callSignSersvice(fullPath, destPath, "Сбербанк страхование", "Покупка полиса", login, password);
                                    logger.debug("signed: " + String.valueOf(signRes));

                                    if (signRes) {
                                        File f = new File(destPath);
                                        if (f.exists()) {
                                            fullPath = destPath;
                                            Map<String, Object> binParams = new HashMap<String, Object>();
                                            binParams.put("OBJID", params.get("CONTRID"));
                                            String realFormat = fullPath.substring(fullPath.length() - 4);

                                            binParams.put("FILENAME", report.get("NAME").toString() + realFormat);
                                            // файлы из uploadPath храним без пути.
                                            binParams.put("FILEPATH", reportName + "_signed.pdf"); //f.getPath());
                                            binParams.put("FILESIZE", f.length());
                                            binParams.put("FILETYPEID", 15);
                                            binParams.put("FILETYPENAME", "Полис подписанный");
                                            binParams.put("NOTE", "");
                                            logger.debug("binfile Create: " + binParams.toString());

                                            this.callService(filesServiceName, filesCreateMethodName, binParams, login, password);
                                        }
                                    }
                                    if (!fullPath.isEmpty()) {
                                        String realFormat = fullPath.substring(fullPath.length() - 4);
                                        filePathList.put(report.get("NAME").toString() + realFormat, fullPath);
                                    }
                                }
                            }
                        }
                    } else {
                        String fullPath = getTemplateFullPath(path, login, password);
                        logger.debug("template attach " + fullPath);
                        fullPath = fullPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
                        filePathList.put(report.get("NAME").toString() + repFormat, fullPath);
                    }
                }
            }
            filePathList.put("REPORTDATALIST", repDataList);
        }
        logger.debug("dsPrintReport finish");

        return filePathList;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsSberHabPrintReport(Map<String, Object> params) throws Exception {
        params.put("SERVICENAME", BIVSBERPOSWS_SERVICE_NAME);
        params.put("METHODNAME", "dsHabContractBrowseListParamEx");
        //params.put(USEB2B_PARAM_NAME, params.get(USEB2B_PARAM_NAME));

        Map<String, Object> result = dsPrintReport(params);

        return result;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsSberVzrPrintReport(Map<String, Object> params) throws Exception {
        if (isB2BMode(params)) {
            params.put("PRODCONFID", 2013L);
            params.put("SERVICENAME", B2BPOSWS_SERVICE_NAME);
            params.put("METHODNAME", "dsB2BVZRPrintDocDataProvider");
        } else {
            params.put("SERVICENAME", BIVSBERPOSWS_SERVICE_NAME);
            params.put("METHODNAME", "dsTravelContractBrowseEx");
        }
        params.put("SHENGENDOPDAYS", 15);
        //params.put(USEB2B_PARAM_NAME, params.get(USEB2B_PARAM_NAME));

        Map<String, Object> result = dsPrintReport(params);

        return result;
    }

    private String getUploadFolder() {
        return Config.getConfig("reportws").getParam("reportOutput", System.getProperty("user.home"));
    }

    private String getTemplateFullPath(String path, String login, String password) throws Exception {
        String project = "insurance";
        String metadataURL = getMetadataURL(login, password, project);
        String fullPath = "";
        if ((metadataURL == null) || (metadataURL.equals(""))) {
            metadataURL = Config.getConfig("reportws").getParam("rootPath", "C:/bea/workshop92/METADATA/REPORTS/");

            fullPath = metadataURL + path;
        } else {
            if ((!metadataURL.endsWith("/")) && (!metadataURL.endsWith("\\"))) {
                metadataURL = metadataURL + File.separator;
            }
            if (!path.contains("REPORTS/")) {
                metadataURL = metadataURL + "REPORTS/";
            }
            fullPath = metadataURL + path;
        }
        return fullPath;
    }

    private String getReportFullPath(String reportName, String format) {
        String path = getUploadFolder();
        File reportFile = new File(path + File.separator + reportName + format);

        if (!reportFile.exists()) {
            reportFile = new File(path + File.separator + reportName + ".odt");
            if (!reportFile.exists()) {
                reportFile = new File(path + File.separator + reportName + ".ods");
                if (!reportFile.exists()) {
                    reportFile = null;
                }

            }
        }
        String fullPath = "";
        try {
            if (reportFile.getCanonicalPath().startsWith(path)) {
                if (reportFile != null) {
                    fullPath = reportFile.getAbsolutePath();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fullPath;
    }

    private boolean callSignSersvice(String srcPath, String destPath, String location, String reason, String login, String password) throws Exception {
        Map<String, Object> signParam = new HashMap<String, Object>();
        signParam.put("SOURCEFILENAME", srcPath);
        signParam.put("SIGNEDFILENAME", destPath);
        signParam.put("LOCATION", location);
        signParam.put("REASON", reason);
        boolean result = false;
        try {
            this.callService(SIGNWS_SERVICE_NAME, "dsSignPDF", signParam, login, password);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    private void recalcFinishDate(Map<String, Object> result, Map<String, Object> params) {
        Long delta = Long.valueOf(params.get("SHENGENDOPDAYS").toString());
        Map<String, Object> contrExt = (Map<String, Object>) result.get("CONTREXTRATTR");
        if (contrExt.get("territoty") != null) {
            String countrySysName = contrExt.get("territoty").toString();
            if ("NoUSARF".equalsIgnoreCase(countrySysName) || "NoRF".equalsIgnoreCase(countrySysName)) {
                if ((result.get("STARTDATE") != null) && (result.get("FINISHDATE") != null)) {
                    Date sd = (Date) result.get("STARTDATE");
                    Date fd = (Date) result.get("FINISHDATE");
                    Date fdShengen = new Date();
                    fdShengen.setTime(fd.getTime() + delta.intValue() * 24 * 60 * 60 * 1000);

                    GregorianCalendar sdgc = new GregorianCalendar();
                    sdgc.setTime(sd);
                    sdgc.set(Calendar.YEAR, sdgc.get(Calendar.YEAR) + 1);
                    sdgc.set(Calendar.DATE, sdgc.get(Calendar.DATE) - 1);

                    if (fdShengen.getTime() > sdgc.getTimeInMillis()) {
                        fdShengen.setTime(sdgc.getTimeInMillis());
                    }
                    result.put("FINISHDATE", fdShengen);
                    GregorianCalendar fdgc = new GregorianCalendar();
                    fdgc.setTime(fd);
                }
            }
        }

    }

}
