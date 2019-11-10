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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.ConfigUtils;
import ru.diasoft.services.inscore.util.StringCryptUtils;

/**
 *
 * @author averichevsm
 */
@BOName("XMLExportCustom")
public class XMLExportCustomFacade extends ExportCustomFacade {

    private final Logger logger = Logger.getLogger(this.getClass());
    private static final String B2BPOSWS = Constants.B2BPOSWS;
    public static final String SERVICE_NAME = Constants.B2BPOSWS;

    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final private byte[] Salt = {
        (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
        (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19
    };

    /*
    // здесь более не требуется - будет доступно за счет B2BBaseFacade implements SeaweedsGetters
    protected String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }

    // здесь более не требуется - будет доступно за счет B2BBaseFacade implements SeaweedsGetters
    protected String getSeaweedFSUrl() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("SEAWEEDFSURL", "");
        return login;
    }
    */


    /**
     * Метод для подготовки данных по 1 договору т.к. договоры считываются все
     * равно поштучно - нет смысла формировать из них пакеты по 200 штук, чтобы
     * потом выгружать. пачками. т.е. подготовка данных будет выполнятся
     * непосредственно перед формированием выгрузки по каждому договору.
     *
     * 2 режима работы метода: режим выгрузки при передаче методу OBJECTID с ид
     * договора формируются данные только по этому договору.
     *
     * режим предпросмотра с грида при передаче методу EXPORTDATAID с ид
     * выгрузки формируются данные по всем договорам выгрузки. но данные
     * урезанные для увеличения скорости работы.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBrowseContract4export2XML(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> loadRes = null;
        if (params.get("OBJECTID") != null) {
            logger.debug("Export: begin prepare data for contrid = " + params.get("OBJECTID").toString());

            Map<String, Object> loadParam = new HashMap<String, Object>();
            loadParam.put("ReturnAsHashMap", "TRUE");
            loadParam.put("CONTRID", params.get("OBJECTID"));
            loadRes = this.callService(B2BPOSWS, "dsB2BContractUniversalLoad", loadParam, login, password);
            // сдесь необходимо догрузить сущности не поддержанные универсальной загрузкой.
            // платежи

            if (loadRes.get("CONTRNODEID") != null) {
                Long contrNodeId = (Long) loadRes.get("CONTRNODEID");
                Map<String, Object> planParams = new HashMap<String, Object>();
                planParams.put("CONTRNODEID", contrNodeId);
                //planParams.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> qPlanRes = this.callService(B2BPOSWS, "dsB2BPaymentFactBrowseListByParam", planParams, login, password);
                loadRes.put("PAYMENTLIST", WsUtils.getListFromResultMap(qPlanRes));
            }
            // получение графика оплаты
            if (loadRes != null) {
                if (loadRes.get("CONTRID") != null) {
                    Map<String, Object> factParams = new HashMap<String, Object>();
                    Long contrId = Long.valueOf(loadRes.get("CONTRID").toString());
                    factParams.put("CONTRID", contrId);
                    Map<String, Object> qFactRes = this.callService(B2BPOSWS, "dsB2BPaymentBrowseListByParam", factParams, login, password);
                    loadRes.put("PAYMENTSCHEDULELIST", qFactRes.get(RESULT));

                }
            }
            logger.debug("Export: end prepare data for contrid = " + params.get("OBJECTID").toString());
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

    private String getFileName() throws Exception {
        return String.format("%s", UUID.randomUUID().toString());
    }

    private String getEncryptedFileNamesStr(String reportName, String userFileName) throws SecurityException {
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
            String userDocName = userFileName;
            String fileNamesStr = docName + "@" + userDocName;
            logger.debug("File names string for encrypting (fileNamesStr) = " + fileNamesStr);

            StringCryptUtils crypter = new StringCryptUtils(EncryptionPassword, Salt);
            encryptedFileNamesStr = crypter.encrypt(fileNamesStr);
            logger.debug("Encrypted file names string (encryptedFileNamesStr) = " + encryptedFileNamesStr);

        }
        logger.debug("Encrypting file names for angular interface finished.");
        return encryptedFileNamesStr;
    }

    @WsMethod(requiredParams = {"EXPORTDATAID", "TEMPLATEID"})
    public Map<String, Object> dsB2BExportDataCreateXMLReport(Map<String, Object> params) throws Exception {

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

        // получение текста запроса из шаблона обрабатываемой записи
        String dataQueryText = getStringParam(template.get("DATASQL"));
        String dataMethod = "";

        logger.debug("Data query text for this template (DATASQL) = \n\n" + dataQueryText + "\n");
        if (dataQueryText.isEmpty()) {
            //доработка. если отсутствует datasql пытаемся найти DATAMETHOD.
            dataMethod = getStringParam(template.get("DATAMETHOD"));
            logger.debug("Data method for this template (DATAMETHOD) = \n\n" + dataQueryText + "\n");
            if (dataMethod.isEmpty()) {
                throw new Exception(String.format("No data query text (DATASQL) and data method (DATAMETHOD) found in template with id (TEMPLATEID) = %d for export data record with id (EXPORTDATAID) = %d.", templateID, exportDataID));
            }
        }

        // строковый список идентификаторов объектов
        logger.debug("Getting objects ids list for export data with id (EXPORTDATAID) = " + exportDataID);
        Map<String, Object> contentParams = new HashMap<String, Object>();
        contentParams.put("EXPORTDATAID", exportDataID);
        Map<String, Object> content = this.callService(B2BPOSWS, "dsB2BExportDataContentBrowseListByParam", contentParams, login, password);
        contentParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> exportDataMap = this.callService(B2BPOSWS, "dsB2BExportDataBrowseListByParam", contentParams, login, password);
        List<Map<String, Object>> objectIDsList = WsUtils.getListFromResultMap(content);
        StringBuilder objectIDsListStr = new StringBuilder();
        String idSeparator = ", ";
        Map<String, Object> integrationParams = new HashMap<String, Object>();
        integrationParams.put("OBJIDLIST", objectIDsList);
        integrationParams.put("DATAMETHODNAME", dataMethod);
        integrationParams.put("DATASERVICENAME", "b2bposws");
        integrationParams.put("FINISHMETHODNAME", "dsB2BExportDataCreateFile");
        integrationParams.put("FINISHSERVICENAME", "b2bposws");
        integrationParams.put("DATANUMBER", exportDataMap.get("DATANUMBER"));
        integrationParams.put("FILEOBJECTID", exportDataMap.get("EXPORTDATAID"));
        // данный вызов может работать очень долго - и выпасть по таймауту.
        Map<String, Object> xmlResult = null;
        try {
            //
            xmlResult = this.callService(Constants.BIVSBERPOSWS, "dsIntegrationGetContractsData", integrationParams, login, password);
        } catch (TimeoutException e) {
            return result;
        } catch (SocketTimeoutException e) {
            return result;
        }
        logger.debug("finish prepare data");
        if (xmlResult.get(RESULT) != null) {
            Map<String, Object> xmlRes = (Map<String, Object>) xmlResult.get(RESULT);
            if (xmlRes.get("FINISHRES") != null) {
                Map<String, Object> finishRes = (Map<String, Object>) xmlRes.get("FINISHRES");
                if (finishRes.get(RESULT) != null) {
                    Map<String, Object> fileRes = (Map<String, Object>) finishRes.get(RESULT);
                    if (fileRes.get("ENCRIPTEDFILENAME") != null) {
                        result.put("ENCRIPTEDFILENAME", fileRes.get("ENCRIPTEDFILENAME"));
                    }

                }

            }
        }

        return result;

    }

    @WsMethod(requiredParams = {"registry"})
    public Map<String, Object> dsB2BExportDataCreateFile(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        //Map<String, Object> xmlResult = (Map<String, Object>) params.get("registry");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        /*
        Long timeShift = getConfigParam("BORDEROTIMESHIFT");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-hh.mm.ss");
        Date now = new Date();
        Date shiftNow = new Date(now.getTime() + (timeShift * 60 * 60 * 1000));
        */
        String borderoNum = getStringParam(params.get("DATANUMBER"));
        if (borderoNum.isEmpty()) {
            borderoNum = getStringParam(params.get("FILEOBJECTID"));
        }
        Long fileObjId = getLongParam(params.get("FILEOBJECTID"));
        //   String borderoNum = getStringParam(exportDataMap.get("DATANUMBER"));

        String fileName = this.getFileName();
        String userFileName = getExportDataFilenameShiftedDateStr() + " " + borderoNum;

        String codePage = "UTF-8";
        String fileExt = ".xml";
        String fileBody = params.get("registry").toString();

        BufferedOutputStream bufferedOutput = null;
        FileOutputStream fileOutputStream = null;
        try {
            String filePath = this.getUploadFilePath();
            logger.debug("fileParh: " + filePath);
            logger.debug("fileName: " + fileName + fileExt);
            fileOutputStream = new FileOutputStream(String.format("%s%s", filePath, fileName + fileExt));//".txt"
            //int fileSize = fileOutputStream.getChannel().size();
            //Construct the BufferedOutputStream object
            bufferedOutput = new BufferedOutputStream(fileOutputStream);
            //Start writing to the output stream
            byte[] ba = null;
            if (codePage.equals("")) {
                ba = fileBody.getBytes();
            } else {
                ba = fileBody.getBytes(codePage);
            }
            bufferedOutput.write(ba);
            int fileSize;
            fileSize = ba.length;
            //remove old files
            boolean needReprint = true;
            if (needReprint) {
                // пытаемся получить файл
                logger.debug("remove attach doc for expData: " + params.get("FILEOBJECTID").toString());
                Map<String, Object> getMap = new HashMap<String, Object>();
                getMap.put("OBJID", fileObjId);
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
            String expDataFileName = userFileName + fileExt;
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
                expDataFilePath = fileName + fileExt;
            }
            Map<String, Object> expDataBinParams = new HashMap<String, Object>();
            expDataBinParams.put("OBJID", fileObjId);
            expDataBinParams.put("FILENAME", expDataFileName);
            expDataBinParams.put("FILEPATH", expDataFilePath);
            expDataBinParams.put("FILESIZE", fileSize);
            expDataBinParams.put("FILETYPEID", 1015);
            expDataBinParams.put("FILETYPENAME", userFileName + fileExt);
            this.callService(B2BPOSWS, "dsB2BExportData_BinaryFile_createBinaryFileInfo", expDataBinParams, login, password);
            String encryptedFileNamesStr = getEncryptedFileNamesStr(expDataFilePath, userFileName + fileExt, getUseSeaweedFS().equalsIgnoreCase("TRUE"));
            //String encryptedFileNamesStr = getEncryptedFileNamesStr(fileName + fileExt, getUseSeaweedFS().equalsIgnoreCase("TRUE"));
            //String encryptedFileNamesStr = getEncryptedFileNamesStr(fileName + fileExt, userFileName + fileExt);
            if (!encryptedFileNamesStr.isEmpty()) {
                result.put("ENCRIPTEDFILENAME", encryptedFileNamesStr);
            }
        } catch (FileNotFoundException ex) {
            logger.error("export file not fount", ex);
        } catch (IOException ex) {
            logger.error("export IOException", ex);
        } finally {
            //Close the BufferedOutputStream
            try {
                if (bufferedOutput != null) {
                    bufferedOutput.flush();
                    bufferedOutput.close();
                }
            } catch (IOException ex) {
            }
        }


        // сгенерированный отчет выдать пользователю в интерфейсе
        logger.debug("Export data report creating finish.");
        return result;
    }



}
