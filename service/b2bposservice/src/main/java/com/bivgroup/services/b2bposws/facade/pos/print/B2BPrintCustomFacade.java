package com.bivgroup.services.b2bposws.facade.pos.print;

import com.bivgroup.seaweedfs.client.*;
import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.facade.B2BFileSessionController;
import com.bivgroup.services.b2bposws.system.Constants;
import com.bivgroup.sessionutils.SessionController;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;
import ru.diasoft.utils.XMLUtil;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bivgroup.services.b2bposws.facade.B2BFileSessionController.*;
import static com.bivgroup.services.b2bposws.system.Constants.B2BPOSWS;
import static com.bivgroup.services.b2bposws.system.Constants.LIBREOFFICEREPORTSWS;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * @author kkulkov
 */
@BOName("B2BPrintCustomFacade")
public class B2BPrintCustomFacade extends B2BBaseFacade {

    public static final String SERVICE_NAME = Constants.B2BPOSWS;
    private static final String SIGNWS_SERVICE_NAME = Constants.SIGNWS;
    private static final String WEBSMSWS_SERVICE_NAME = Constants.WEBSMSWS;
    //private static final String WEBSMSWS_SERVICE_NAME = Constants.SIGNWEBSMSWS; //!только для отладки!
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    private static final String SIGNB2BPOSWS_SERVICE_NAME = Constants.SIGNB2BPOSWS;
    private static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    private static final String PROJECT_PARAM_NAME = "project";
    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final private byte[] Salt = {
            (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
            (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};
    final private String divider = "__div__";
    private Long sessionTimeOut = 10L;

    // строка для включения в уведомление о подписании договора для случаев когда не удалось определить данные пользователя
    private static final String NO_USER_CREDS_FOUND = "< Сведения о пользователе не найдены >";
    // строка для включения в уведомления для случаев когда не удалось определить название Партнера
    private static final String NO_PARTNER_INFO_FOUND = "< Сведения о партнере не найдены >";

    private File getFileFromUploadFolder() {
        File uploadFolder = new File(Config.getConfig("reportws").getParam("reportOutput", System.getProperty("user.home")));

        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        return uploadFolder;
    }

    private String getUploadFolder() {
        return Config.getConfig("reportws").getParam("reportOutput", System.getProperty("user.home"));
    }

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

    private File getUploadFile(String reportName, String ext) {
        File uploadFolder = getFileFromUploadFolder();
        File uploadFile = new File(uploadFolder.getAbsolutePath() + File.separator + reportName + ext);
        return uploadFile;
    }

    protected void userLogActionCreateEx(String sessionId, String contrId, String action, String note, String value, String param1, String param2, String param3, String login, String password) throws Exception {
        Map<String, Object> qParam = new HashMap<String, Object>();
        qParam.put("ACTION", action);
        qParam.put("NOTE", note);
        qParam.put("CONTRID", contrId);
        // проверяем, если в ид сессии нет "-", значит оно закодировано, декодируем
        if ((sessionId != null) && (!sessionId.contains("-"))) {
            sessionId = base64Decode(sessionId);
        }
        qParam.put("SESSIONID", sessionId);
        qParam.put("VALUE", value);
        // свободно
        qParam.put("PARAM1", param1);
        // url
        qParam.put("PARAM2", param2);
        // prodverid
        qParam.put("PARAM3", param3);
        Map<String, Object> res = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsClientActionLogCreate", qParam, login, password);
        logger.debug("clientActLog " + res.toString());
    }

    private boolean callSignSersvice(String srcPath, String destPath, String location, String reason, String login, String password) throws Exception {
        Map<String, Object> signParam = new HashMap<String, Object>();
        signParam.put("SOURCEFILENAME", srcPath);
        signParam.put("SIGNEDFILENAME", destPath);
        signParam.put("LOCATION", location);
        signParam.put("REASON", reason);
        boolean result;
        try {

            this.callService(SIGNWS_SERVICE_NAME, "dsSignPDF", signParam, login, password);
            result = true;
        } catch (Exception e) {
            result = false;
        }
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

    protected void saveEmailFailStatusToContract(Long contrId, String message, String login, String password) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (contrId != null) {
            params.put("CONTRID", contrId);
            params.put("NOTE", message);
            try {
                logger.debug("try upd contr " + contrId + " to " + message);
                Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractUpdate", params, login, password);
                //Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrUpdate", params, login, password);

            } catch (Exception ex) {
                logger.error("saveEmailFailStatusToContract fail", ex);
            }
            logger.debug("finish update log " + message);
        }
    }

    protected void saveEmailFailStatusToContractEmpty(Long contrId, String message, String login, String password) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (contrId != null) {
            params.put("CONTRID", contrId);
            params.put("NOTE", message);
            try {
                logger.debug("try upd contr " + contrId + " to " + message);
                // этот Update почему-то дико тормозит до 300 секунд.
                // времени разбираться нет. поэтому пока выключаю. в штатном режиме почтового сервака он не пригодится.
                //Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractUpdate", params, login, password);
                //Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrUpdate", params, login, password);

            } catch (Exception ex) {
                logger.error("saveEmailFailStatusToContract fail", ex);
            }
            logger.debug("finish update log " + message);
        }
    }

    private String getUploadFilePath() {

        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();

        return result;
    }

    protected void saveEmailInFile(Map<String, Object> sendParams, String login, String password) {
        // получаем значение флага сохранения из конфига bivsberposws
        Config config = Config.getConfig(B2BPOSWS_SERVICE_NAME);
        String saveEmailInFile = config.getParam("SAVEEMAILINFILE", "FALSE");
        if ("true".equalsIgnoreCase(saveEmailInFile)) {
            // если флаг взведен, делаем файл по пути uploadpath
            // получаем путь к uploadpath
            String upPath = getUploadFilePath();
            // сохраняем в данный файл текст письма
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");
            String email = getStringParam(sendParams.get("SMTPReceipt"));
            String htmlText = getStringParam(sendParams.get("HTMLTEXT"));
            if (htmlText.isEmpty()) {
                htmlText = getStringParam(sendParams.get("SMTPMESSAGE"));
            }
            email.replace("@", "_");

            BufferedOutputStream bufferedOutput = null;
            FileOutputStream fileOutputStream = null;
            try {
                String fileName = sdf.format(now) + "_" + email + ".html";
                fileOutputStream = new FileOutputStream(upPath + fileName);//".txt"
                bufferedOutput = new BufferedOutputStream(fileOutputStream);
                //Start writing to the output stream
                /*
                byte[] ba = null;
                String codePage = "";
                if (codePage.isEmpty()) {
                    ba = htmlText.getBytes();
                } else {
                    ba = htmlText.getBytes(codePage);
                }
                */
                byte[] ba = htmlText.getBytes();
                bufferedOutput.write(ba);
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
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
        }
    }

    protected Map<String, Object> sendReportByEmail(Map<String, Object> attachmentMap, Map<String, Object> contrMap, String emailText, String email, String login, String password) throws Exception {
        String contrNum = contrMap.get("CONTRNUMBER").toString();
        Map<String, Object> sendParams = new HashMap<String, Object>();

        // Тема письма
        //sendParams.put("SMTPSubject", "Договор страхования №" + contrNum);
        String smtpSubject = getStringParam(contrMap.get("SMTPSubject")); // тема письма может быть передана в явном виде (например, при отправке уведомления о подписании)
        if (smtpSubject.isEmpty()) {
            smtpSubject = "Договор страхования №" + contrNum; // тема письма по умолчанию (если не передана в явном виде)
        }
        sendParams.put("SMTPSubject", smtpSubject);

        sendParams.put("SMTPMESSAGE", "В приложении договор страхования №" + contrNum + ", правила страхования и памятка страхователя.");
        sendParams.put("SMTPReceipt", email);
        Map<String, Object> sendRes = null;
        if (isAllEmailValid(email)) {
            if (attachmentMap != null) {
                Map<String, Object> resAttachMap = new HashMap<String, Object>();
                if (attachmentMap.get(RESULT) != null) {
                    Map<String, Object> attachList = (Map<String, Object>) attachmentMap.get(RESULT);
                    if (attachList.get("REPORTDATALIST") != null) {
                        attachList.remove("REPORTDATALIST");
                    }
                    resAttachMap.putAll(attachList);
                } else {
                    resAttachMap.putAll(attachmentMap);
                }
                // конвертируем мапу в нужный формат (в значениях присутствует тип файловой системы)
                Map<String, Object> convertedAttachMap = new HashMap();
                for (Entry<String, Object> entry : resAttachMap.entrySet()) {
                    String bean = entry.getValue().toString();
                    String[] beanArr = bean.split("@");
                    String fsType = beanArr[0];
                    String path = beanArr[1];
                    if (fsType.equalsIgnoreCase(Constants.FS_EXTERNAL)) {
                        String uploadPath = getUploadFilePath();
                        String tempFileName = uploadPath + UUID.randomUUID() + "_" + entry.getKey();
                        // сохраняем файл из внешней системы в темповый на диск
                        String masterUrlString = getSeaweedFSUrl();
                        URL masterURL = new URL(masterUrlString);
                        WeedFSFile file = new WeedFSFile(path);
                        WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                        List<Location> locations = client.lookup(file.getVolumeId());
                        if (locations.size() == 0) {
                            System.out.println("file not found");
                        } else {
                            InputStream fsFile = client.read(file, locations.get(0));
                            BufferedOutputStream bufferedOutput = null;
                            FileOutputStream fileOutputStream = null;
                            try {
                                fileOutputStream = new FileOutputStream(tempFileName);
                                bufferedOutput = new BufferedOutputStream(fileOutputStream);
                                int read;
                                final byte[] bytes = new byte[1024];
                                while ((read = fsFile.read(bytes)) != -1) {
                                    bufferedOutput.write(bytes, 0, read);
                                }
                            } finally {
                                fsFile.close();
                                if (bufferedOutput != null) {
                                    bufferedOutput.flush();
                                    bufferedOutput.close();
                                }
                            }
                            File tmpFile = new File(tempFileName);
                            if (tmpFile.exists() && tmpFile.getCanonicalPath().startsWith(uploadPath)) {
                                convertedAttachMap.put(new String(entry.getKey().getBytes("utf-8"), "utf-8"), tempFileName);
                            }
                        }
                    } else {
                        convertedAttachMap.put(new String(entry.getKey().getBytes("utf-8"), "utf-8"), path);
                    }
                }
                sendParams.put("ATTACHMENTMAP", convertedAttachMap);
            }
            logger.debug("sendParams = " + sendParams.toString());
            sendParams.put("HTMLTEXT", emailText);
            String sessionId = "";
            if ((contrMap.get("SESSIONID") != null) && (!contrMap.get("SESSIONID").toString().isEmpty())) {
                sessionId = contrMap.get("SESSIONID").toString();
            }
            // временно переключено на пустую болванку. т.к. штатный метод сильно тормозит. 
            // TODO выяснить причину, устранить
            saveEmailFailStatusToContractEmpty(getLongParam(contrMap.get("CONTRID")), " ", login, password);
            userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Попытка отправки почты", "Договор страхования №" + contrNum, "", email, "", "", login, password);

            try {
                boolean isError = false;
                saveEmailInFile(sendParams, login, password);
                sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                    sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                    if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                        sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                        if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                            sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                            if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                                sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                                if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                                    //отправка письма не удалась, сохранить в файл, если проставлен соответсвтующий параметр
                                    // saveEmailInFile(sendParams, login, password);
                                    isError = true;
                                    saveEmailFailStatusToContractEmpty(getLongParam(contrMap.get("CONTRID")), "EMAIL SEND FAIL", login, password);
                                    userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Почта не отправлена", "Договор страхования №" + contrNum, "", email, "", "", login, password);
                                    logger.debug("mailSendFail");
                                }
                            }
                        }
                    }
                }
                if (!isError) {
                    logger.debug("mailSendSuccess");
                    userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Почта отправлена", "Договор страхования №" + contrNum, "", email, "", "", login, password);

                }

            } catch (Exception e) {
                logger.debug("mailSendException: ", e);
                //отправка письма не удалась, сохранить в файл, если проставлен соответсвтующий параметр
                userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Почта не отправлена", "Договор страхования №" + contrNum, "", email, "", "", login, password);
                saveEmailInFile(sendParams, login, password);
                saveEmailFailStatusToContract(getLongParam(contrMap.get("CONTRID")), "EMAIL SEND FAIL", login, password);
            }
        }
        return sendRes;
    }

    protected String getProjectName(String login, String password) throws Exception {
        String result = null;
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> namesList = new ArrayList();
        namesList.add("DEFAULT_PROJECT_SYSNAME");
        params.put("NAMES", namesList);
        params.put("LOGIN", login);
        Map<String, Object> res = callService(WsConstants.COREWS, "dsAccountSettingFindByLoginAndName", params, login, password);
        if ((res != null) && (res.get("Status") != null) && (res.get("Status").equals("OK"))) {
            result = (String) res.get("DEFAULT_PROJECT_SYSNAME");
        }
        return result;
    }

    protected static String getRejectURLFromURL(String url) {
        String indexHTMLStr = "/index.html#/";
        String regExp = Pattern.quote(indexHTMLStr) + ".*";
        String result = url.replaceFirst(regExp, indexHTMLStr + "reject");
        return result;
    }

    protected static String getURLWithParam(String url, String paramName, String paramValue) {
        String result;
        if (url.indexOf("#top") > 0) {
            url = url.replace("#top", "");
        }
        if (url.contains("?")) {
            result = url + "&" + paramName + "=" + paramValue;
        } else {
            result = url + "?" + paramName + "=" + paramValue;
        }
        return result;
    }

    protected String generateEmailTextEx(Map<String, Object> contrMap, String mailName, String login, String password) throws Exception {
        // загрузка url из конфига продукта.
        //Map<String, Object> productConfigRes = this.callService(INSPRODUCTWSWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", productConfQueryParams, login, password);
        Map<String, Object> productConfigRes = getProductDefaultValueByProdConfId(contrMap.get("PRODCONFID"), login, password);
        return generateEmailTextEx(contrMap, productConfigRes, mailName, login, password);
    }

    protected String generateEmailTextEx(Map<String, Object> contrMap, Map<String, Object> prodDefValMap, String mailName, String login, String password) throws Exception {
        String project = this.getProjectName(login, password);
        String metadataURL = getMetadataURL(login, password, project);

        String fName;

        // загрузка url из конфига продукта.
        //Map<String, Object> productConfigRes = this.callService(INSPRODUCTWSWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", productConfQueryParams, login, password);
        Map<String, Object> productConfigRes = prodDefValMap;
        if ((productConfigRes != null) && (productConfigRes.get(mailName) != null)) {
            fName = (String) productConfigRes.get(mailName);
        } else {
            fName = "";
        }

        String fullPath = metadataURL + fName;
        File input = new File(fullPath);
        if (input.exists() && input.getCanonicalPath().startsWith(metadataURL)) {
            //BufferedReader reader = new BufferedReader(new FileReader(fullPath));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fullPath), "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            if (contrMap.get("GENDER") != null) {
                if ("1".equals(contrMap.get("GENDER").toString())) {
                    contrMap.put("GENDERNAME", "Уважаемая");
                } else {
                    contrMap.put("GENDERNAME", "Уважаемый");
                }
            } else {
                contrMap.put("GENDERNAME", "");
            }
            if ((contrMap.get("PARTICIPANTTYPE") != null) && (contrMap.get("PARTICIPANTTYPE").toString().equalsIgnoreCase("1"))) {
                contrMap.put("ONLINEHEADER", String.format("%s %s,", contrMap.get("GENDERNAME").toString(), contrMap.get("FIO").toString()));
            } else {
                contrMap.put("ONLINEHEADER", "Добрый день!");
            }
            if ((contrMap.get("PRODUCTURL") != null) && (contrMap.get("HASH") != null)) {
                String url = contrMap.get("PRODUCTURL").toString();
                String hash = contrMap.get("HASH").toString();
                contrMap.put("URL", getURLWithParam(url, "hash", hash));
                String urlReject = getRejectURLFromURL(url);
                contrMap.put("URLREJECT", getURLWithParam(urlReject, "hash", hash));
                //получить из настроек системы пути к сервисам
                StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
                String contrId = getStringParam(contrMap.get("CONTRID"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
                // 1. получить тип вьюх+
                if (contrMap.get("viewType") != null) {
                    String viewType = contrMap.get("viewType").toString();

                    // 2. по типу вьюх отобрать урлы
                    String nowDataTyme = String.valueOf((new Date()).getTime());
                    StringBuilder sbParam = new StringBuilder();
                    sbParam.append(nowDataTyme);
                    sbParam.append(divider);
                    sbParam.append(contrId);
                    String baseParam = base64Encode(scu.encrypt(sbParam.toString()));
                    String goodParam = base64Encode(scu.encrypt(sbParam.toString() + divider + "good"));
                    String normParam = base64Encode(scu.encrypt(sbParam.toString() + divider + "norm"));
                    String badParam = base64Encode(scu.encrypt(sbParam.toString() + divider + "bad"));
                    String URLGOOD = "URLGOOD";
                    String URLNORM = "URLNORM";
                    String URLBAD = "URLBAD";

                    Map<String, Object> coreParams = new HashMap<String, Object>();
                    Map<String, Object> coreSettings = this.callService(COREWS, "getSysSettings", coreParams, login, password);
                    if (coreSettings != null) {
                        if (coreSettings.get(RESULT) != null) {
                            List<Map<String, Object>> coreSettingList = (List<Map<String, Object>>) coreSettings.get(RESULT);
                            if (!coreSettingList.isEmpty()) {
                                for (Map<String, Object> coreSetting : coreSettingList) {
                                    String sysName = getStringParam(coreSetting.get("SETTINGSYSNAME"));
                                    if (sysName.indexOf("URL") == 0) {
                                        // системное имя начинается с URL, добавляем значения в мапу для замен в шаблоне письма
                                        if (URLGOOD.equalsIgnoreCase(sysName)) {
                                            contrMap.put(sysName, getURLWithParam(getStringParam(coreSetting.get("SETTINGVALUE")), "hash", goodParam));
                                        } else if (URLNORM.equalsIgnoreCase(sysName)) {
                                            contrMap.put(sysName, getURLWithParam(getStringParam(coreSetting.get("SETTINGVALUE")), "hash", normParam));
                                        } else if (URLBAD.equalsIgnoreCase(sysName)) {
                                            contrMap.put(sysName, getURLWithParam(getStringParam(coreSetting.get("SETTINGVALUE")), "hash", badParam));
                                        } else if (viewType.isEmpty()) {
                                            contrMap.put(sysName, getStringParam(coreSetting.get("SETTINGVALUE")));
                                        } else if (sysName.toUpperCase().indexOf(viewType.toUpperCase()) >= 0) {
                                            String shortSysName = sysName.substring(0, sysName.toUpperCase().indexOf(viewType.toUpperCase()));
                                            contrMap.put(shortSysName, getStringParam(coreSetting.get("SETTINGVALUE")));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }

            if (contrMap.get("FIO") != null) {
                contrMap.put("FIO", contrMap.get("FIO").toString());
            }

            String resString = sb.toString();
            for (Map.Entry<String, Object> entry : contrMap.entrySet()) {
                String string = entry.getKey();
                Object object = entry.getValue();
                if (object != null) {
                    resString = resString.replaceAll("\\Q!" + string + "!\\E", object.toString());
                }
            }
            return resString;
        } else {
            return "";
        }
    }

    @WsMethod(requiredParams = {"PRODCONFID"})
    public Map<String, Object> dsB2BPrintAndSendAllDocument(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        logger.debug("dsB2BPrintAndSendAllDocument start");
        boolean isError = false;
        boolean onlyPrint = false;
        boolean sendCopy = false;
        /*
        boolean needReprint = false;
        */
        if (params.get("ONLYPRINT") != null) {
            if ("TRUE".equalsIgnoreCase(params.get("ONLYPRINT").toString())) {
                onlyPrint = true;
            }
        }
        /*
        if (params.get("NEEDREPRINT") != null) {
            if ("TRUE".equalsIgnoreCase(params.get("NEEDREPRINT").toString())) {
                needReprint = true;
            }
        }
        */
        boolean needReprint = getBooleanParamLogged(params, NEED_REPRINT_PARAMNAME, false);
        if (params.get("SENDCOPY") != null) {
            if ("TRUE".equalsIgnoreCase(params.get("SENDCOPY").toString())) {
                sendCopy = true;
            }
        }
        Long repLevel = null;
        if (params.get("REPLEVEL") != null) {
            if (null != params.get("REPLEVEL")) {
                repLevel = Long.valueOf(params.get("REPLEVEL").toString());
            }
        }

        Map<String, Object> printDocParams = new HashMap<String, Object>();

        printDocParams.put("PRODCONFID", params.get("PRODCONFID"));
        printDocParams.put("REPLEVEL", repLevel);
        // выбрать все печатные документы по параметрам.
        Map<String, Object> printDocRes = this.callService(B2BPOSWS, "dsB2BProductReportBrowseListByParamEx", printDocParams, login, password);
        if (printDocRes.get(RESULT) != null) {
            if (needReprint) {
                // пытаемся получить файл
                logger.debug("remove attach doc for contr: " + params.get("CONTRID").toString());
                Map<String, Object> getMap = new HashMap<String, Object>();
                getMap.put("OBJID", params.get("CONTRID"));
                Map<String, Object> getRes = this.callService(Constants.B2BPOSWS, "dsB2BContract_BinaryFile_BinaryFileBrowseListByParam", getMap, login, password);
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
                                    this.callService(Constants.B2BPOSWS, "dsB2BContract_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
                                }
                            }

                        }
                    }
                }
            }
            List<Map<String, Object>> printDocList = (List<Map<String, Object>>) printDocRes.get(RESULT);
            Map<String, Object> filePathList = new HashMap<String, Object>();
            for (Map<String, Object> printDocMap : printDocList) {
                // напечатать odt ods
                // при необходимости подписать.

                // прикрепить как бинарифайл к договору
                Map<String, Object> printParam = new HashMap<String, Object>();
                printParam.put("CONTRID", params.get("CONTRID"));
                printParam.put("PRODCONFID", params.get("PRODCONFID"));
                printDocMap.put("REPORTFORMATS", ".pdf");
                boolean isNeedSign = false;
                if (printDocMap.get("ORIGPRINTING") != null) {
                    if ("1".equalsIgnoreCase(printDocMap.get("ORIGPRINTING").toString())) {
                        isNeedSign = true;
                    }
                }
                printDocMap.put("ISNEEDSIGN", isNeedSign);
                printDocMap.put("ORIGPRINTING", 1);
                printParam.put("REPORTDATA", printDocMap);
                printParam.put("DWNLDSERVLETPATH", params.get("DWNLDSERVLETPATH"));
                logger.debug("contrid: " + params.get("CONTRID").toString());
                Map<String, Object> printRes = this.callService(B2BPOSWS, "dsB2BPrintDocumentsWithFileNames", printParam, login, password);
                if (printRes != null) {
                    if (printRes.get(RESULT) != null) {
                        Map<String, Object> printResMap = (Map<String, Object>) printRes.get(RESULT);
                        if (printResMap.get("ERROR") != null) {
                            result.put("EMAILRES", printResMap);
                            isError = true;
                        } else if ((printResMap.get("USERFILENAME") != null) && (printResMap.get("FULLFILEPATH") != null)) {
                            if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (printResMap.get("FSID") != null)) {
                                filePathList.put(printResMap.get("USERFILENAME").toString(), Constants.FS_EXTERNAL + "@" + printResMap.get("FSID").toString());
                            } else {
                                filePathList.put(printResMap.get("USERFILENAME").toString(), Constants.FS_HARDDRIVE + "@" + printResMap.get("FULLFILEPATH").toString());
                            }
                        }
                    }
                }
            }
            Config config = Config.getConfig(B2BPOSWS_SERVICE_NAME);
            String addressListStr = config.getParam("SENDCOPYADDRESS", "");
            if (params.get("EMAILtoSendCopy") != null) {
                addressListStr = params.get("EMAILtoSendCopy").toString();
            }
            if (!isError) {
                Map<String, Object> contrMap = new HashMap<String, Object>();
                if (!onlyPrint) {
                    if (params.get("INSUREREMAIL") != null) {
                        if (params.get("INSURERFIRSTNAME") != null) {
                            String fio = params.get("INSURERFIRSTNAME").toString();
                            if (params.get("INSURERMIDDLENAME") != null) {
                                fio = fio + " " + params.get("INSURERMIDDLENAME").toString();
                            }
                            contrMap.put("FIO", fio);
                        } else if (params.get("INSURERBRIEFNAME") != null) {
                            contrMap.put("FIO", params.get("INSURERBRIEFNAME").toString());
                        } else if (params.get("INSFIRSTNAME") != null) {
                            String fio = params.get("INSFIRSTNAME").toString();
                            if (params.get("INSMIDDLENAME") != null) {
                                fio = fio + " " + params.get("INSMIDDLENAME").toString();
                            }
                            contrMap.put("FIO", fio);
                        } else if (params.get("INSBRIEFNAME") != null) {
                            contrMap.put("FIO", params.get("INSBRIEFNAME").toString());
                        } else {
                            contrMap.put("FIO", "Клиент");
                        }
                        if (params.get("INSURERGENDER") != null) {
                            contrMap.put("GENDER", params.get("INSURERGENDER").toString());
                        } else if (params.get("INSGENDER") != null) {
                            contrMap.put("GENDER", params.get("INSGENDER").toString());
                        } else {
                            contrMap.put("GENDER", 0L);
                        }
                        contrMap.put("PARTICIPANTTYPE", params.get("PARTICIPANTTYPE"));

                        contrMap.put("ATTACHINFO", params.get("ATTACHINFO"));
                        if (params.get("PRODNAME") != null) {
                            contrMap.put("PRODNAME", params.get("PRODNAME"));
                        }
                        if (params.get("SMTPSubject") != null) {
                            contrMap.put("SMTPSubject", params.get("SMTPSubject"));
                        }
                        contrMap.put("PRODCONFID", params.get("PRODCONFID"));
                        contrMap.put("CONTRNUMBER", params.get("CONTRNUMBER"));
                        contrMap.put("CONTRID", params.get("CONTRID"));
                        contrMap.put("PRODUCTURL", params.get("PRODUCTURL"));
                        contrMap.put("HASH", params.get("HASH"));
                        contrMap.put("SESSIONID", params.get("SESSIONID"));
                        String viewType = getViewType(params.get("CONTRID").toString(), login, password);
                        contrMap.put("viewType", viewType);

                        String emailText = generateEmailTextEx(contrMap, "HTMLMAILPATH", login, password);
                        logger.debug("email: " + params.get("INSUREREMAIL").toString());
                        logger.debug("attachMap: " + filePathList.toString());
                        String email = params.get("INSUREREMAIL").toString();
                        // чтоб не слал всем подряд пока тестирую.
                        //String email = "sambucusfehu@gmail.com";
                        // отправить по почте страхователя пакет документов.
                        Map<String, Object> sendRes = sendReportByEmail(filePathList, contrMap, emailText, email, login, password);
                        result.put("EMAILRES", sendRes);
                        if (sendCopy) {// по ипотеке 900 отправка идет только в банк. страхователю отправки нет.
                            /*if ("1011".equalsIgnoreCase(params.get("PRODCONFID").toString())
                                    || "4000".equalsIgnoreCase(params.get("PRODCONFID").toString())) {*/
                            // при любой отправке почты по ипотеке отправлять копию
                            logger.debug("needSendCopy");

                            if (!addressListStr.isEmpty()) {
                                // текст сообщения оставляем, вложения оставляем
                                //emailText = generateEmailTextEx(contrMap, "HTMLMAILPATHCOPY", login, password);
                                logger.debug("needSendCopy to: " + addressListStr);
                                /*HashMap<String, Object> newFilePath = new HashMap<String, Object>();
                                    for (Entry<String, Object> entry : filePathList.entrySet()) {
                                        String bean = entry.getValue().toString();
                                        String[] beanArr = bean.split("@");
                                        String path = beanArr[1];
                                        if (path.indexOf("INSURANCE\\REPORTS\\") < 0) {
                                            newFilePath.put(entry.getKey(), entry.getValue());
                                        }
                                    }*/

                                Map<String, Object> sendRes1 = sendReportByEmail(filePathList, contrMap, emailText, addressListStr, login, password);
                                result.put("EMAILRES", sendRes1);
                                //   }
                            }
                        }
                    } else {
                        logger.debug("insurer has empty email");
                    }
                }
                // если продукт - ипотека - необходимо отправлять копию на указанные в конфиге адреса.
                if (sendCopy) {// по ипотеке 900 отправка идет только в банк. страхователю отправки нет.
                    if ("10000".equalsIgnoreCase(params.get("PRODCONFID").toString())) {
                        // при любой отправке почты по ипотеке отправлять копию
                        logger.debug("needSendCopy");

                        if (!addressListStr.isEmpty()) {
                            contrMap.put("GENDER", 1);
                            contrMap.put("PRODCONFID", params.get("PRODCONFID"));
                            contrMap.put("CONTRNUMBER", params.get("CONTRNUMBER"));
                            contrMap.put("CONTRID", params.get("CONTRID"));
                            contrMap.put("PRODUCTURL", params.get("PRODUCTURL"));
                            contrMap.put("HASH", params.get("HASH"));
                            contrMap.put("SESSIONID", params.get("SESSIONID"));
                            String emailText = generateEmailTextEx(contrMap, "HTMLMAILPATHCOPY", login, password);
                            logger.debug("needSendCopy to: " + addressListStr);
                            HashMap<String, Object> newFilePath = new HashMap<String, Object>();
                            for (Entry<String, Object> entry : filePathList.entrySet()) {
                                String bean = entry.getValue().toString();
                                String[] beanArr = bean.split("@");
                                String path = beanArr[1];
                                if (path.indexOf("INSURANCE\\REPORTS\\") < 0) {
                                    newFilePath.put(entry.getKey(), entry.getValue());
                                }
                            }

                            Map<String, Object> sendRes = sendReportByEmail(newFilePath, contrMap, emailText, addressListStr, login, password);
                            result.put("EMAILRES", sendRes);

                        }
                    }
                }
            }
        }
        logger.debug("dsB2BPrintAndSendAllDocument finish");

        return result;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BPrepareRePrintDocuments(Map<String, Object> params) throws Exception {
        boolean needReprint = true;
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        if (needReprint) {
            // пытаемся получить файл
            logger.debug("remove attach doc for contr: " + params.get("CONTRID").toString());
            Map<String, Object> getMap = new HashMap<String, Object>();
            getMap.put("OBJID", params.get("CONTRID"));
            Map<String, Object> getRes = this.callService(Constants.B2BPOSWS, "dsB2BContract_BinaryFile_BinaryFileBrowseListByParam", getMap, login, password);
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
                                this.callService(Constants.B2BPOSWS, "dsB2BContract_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
                            }
                        }

                    }
                }
            }
        }
        return null;
    }

    @WsMethod(requiredParams = {"CONTRID", "REPORTDATA"})
    public Map<String, Object> dsB2BPrintDocuments(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        /*
        boolean needReprint = false;
        if (params.get("NEEDREPRINT") != null) {
            if ("TRUE".equalsIgnoreCase(params.get("NEEDREPRINT").toString())) {
                needReprint = true;
            }
        }
        */
        boolean needReprint = getBooleanParamLogged(params, NEED_REPRINT_PARAMNAME, false);
        if (needReprint) {
            // пытаемся получить файл
            logger.debug("remove attach doc for contr: " + params.get("CONTRID").toString());
            Map<String, Object> getMap = new HashMap<String, Object>();
            getMap.put("OBJID", params.get("CONTRID"));

            // если используется режим "PRODREPID печатного документа в качестве FILETYPEID",
            // то при перепечати имеется возможность удалять только перепечатываемый файл, которой следует воспользоваться
            boolean isUseProdRepIdAsFileTypeId = getBooleanParamLogged(params, IS_USE_PRODREPID_AS_FILETYPEID, false);
            if (isUseProdRepIdAsFileTypeId) {
                Map<String, Object> reportData = getMapParamLogged(params, "REPORTDATA");
                Long fileTypeId = getLongParamLogged(reportData, "PRODREPID");
                if (fileTypeId != null) {
                    getMap.put("FILETYPEID", fileTypeId);
                }
            }

            Map<String, Object> getRes = this.callService(Constants.B2BPOSWS, "dsB2BContract_BinaryFile_BinaryFileBrowseListByParam", getMap, login, password);
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
                                this.callService(Constants.B2BPOSWS, "dsB2BContract_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
                            }
                        }

                    }
                }
            }
        }

        Map<String, Object> pRes = this.callService(B2BPOSWS, "dsB2BPrintDocumentsWithFileNames", params, login, password);
        result.put("ENCRIPTSTRING", pRes.get("ENCRIPTSTRING"));
        result.put("SERVERURL", pRes.get("SERVERURL"));
        if (pRes.get("ERROR") != null) {
            result.put("ERROR", pRes.get("ERROR"));
            result.put("ERRORTYPE", pRes.get("ERRORTYPE"));
        }
        return result;
    }

    private void makeReportPDFCopiesIfNeeded(Map<String, Object> reportRes, Map<String, Object> productReportData, String login, String password) throws Exception {
        if ((productReportData.get("CPNUM") != null) && (Long.valueOf(productReportData.get("CPNUM").toString()).longValue() >= 0)) {
            String reportName = null;
            Map<String, Object> repData = null;
            if (reportRes.get("REPORTDATA") != null) {
                repData = (Map<String, Object>) reportRes.get("REPORTDATA");
                if (repData.get("reportName") != null) {
                    reportName = repData.get("reportName").toString() + ".pdf";
                }
            }
            if (reportName != null) {
                File repFile = new File(getUploadFolder() + File.separator + reportName);
                if (repFile.exists() && repFile.getCanonicalPath().startsWith(getUploadFolder())) {
                    Long copiesCount = Long.valueOf(productReportData.get("CPNUM").toString());
                    List<Map<String, Object>> reportFileNames = new ArrayList<Map<String, Object>>();
                    for (int i = 0; i < copiesCount.intValue(); i++) {
                        Map<String, Object> bean = new HashMap<String, Object>();
                        bean.put("FILENAME", reportName);
                        reportFileNames.add(bean);
                    }
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("REPORTSFILENAMES", reportFileNames);
                    params.put("ReturnAsHashMap", "TRUE");
                    Map<String, Object> qRes = this.callService(Constants.B2BPOSWS, "dsB2BMergeReports", params, login, password);
                    if ((qRes != null) && (qRes.get("RESULT") != null) && (repData != null)) {
                        repData.put("reportName", qRes.get("RESULT").toString());
                    }
                }
            }
        }
    }

    @WsMethod(requiredParams = {"CONTRID", "REPORTDATA"})
    public Map<String, Object> dsB2BPrintDocumentsWithFileNames(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BPrintDocumentsWithFileNames start");
        logger.debug("params = " + params);
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Long contrId = getLongParam(params.get("CONTRID"));
        logger.debug("contrId = " + contrId);
        Map<String, Object> reportData = (Map<String, Object>) params.get("REPORTDATA");
        Long fileTypeId = null;
        boolean isUseProdRepIdAsFileTypeId = getBooleanParamLogged(params, IS_USE_PRODREPID_AS_FILETYPEID, false);
        if (isUseProdRepIdAsFileTypeId) {
            fileTypeId = getLongParamLogged(reportData, "PRODREPID");
        }
        logger.debug("reportData = " + reportData);
        Map<String, Object> result = new HashMap<String, Object>();
        boolean isDraft = false;
        if (params.get("CONTRSTATESYSNAME") != null) {
            if ("B2B_CONTRACT_PREPRINTING".equalsIgnoreCase(params.get("CONTRSTATESYSNAME").toString())) {
                if (reportData.get("ORIGPRINTING") != null) {
                    if ("1".equals(reportData.get("ORIGPRINTING").toString())) {
                        isDraft = true;
                    }
                }
            }
        }

        String userFileName = getStringParam(reportData.get("NAME"));
        logger.debug("userFileName = " + userFileName);
        String repFormat = getStringParam(reportData.get("REPORTFORMATS"));
        logger.debug("repFormat = " + repFormat);
        Map<String, Object> queryParams = new HashMap<String, Object>();
        //1 проверить тип файла шаблона. если пдф - этап сбора данных, печати, подписи - пропускаем.
        String path = reportData.get("TEMPLATENAME").toString();
        logger.debug("path = " + path);
        if ((path.indexOf(".odt") > 1) || (path.indexOf(".ods") > 1)) {
            String uploadFilePath = getUploadFilePath();
            Map<String, Object> getMap = new HashMap<String, Object>();
            getMap.put("OBJID", params.get("CONTRID"));

            // если используется режим "PRODREPID печатного документа в качестве FILETYPEID",
            // то при печати имеется возможность точно определить напечатанный ранее файл, которой следует воспользоваться
            if (isUseProdRepIdAsFileTypeId && (fileTypeId != null)) {
                getMap.put("FILETYPEID", fileTypeId);
            }

            boolean isAlreadyAttach = false;
            Map<String, Object> getRes = this.callService(Constants.B2BPOSWS, "dsB2BContract_BinaryFile_BinaryFileBrowseListByParam", getMap, login, password);
            if (getRes != null) {
                if (getRes.get(RESULT) != null) {
                    List<Map<String, Object>> binFileList = (List<Map<String, Object>>) getRes.get(RESULT);
                    if (!binFileList.isEmpty()) {
                        //Map<String, Object> currentBinFile = binFileList.get(0);
                        Map<String, Object> currentBinFile = null;
                        for (Map<String, Object> binFileMap : binFileList) {
                            String binFileName = binFileMap.get("FILENAME").toString();
                            if (binFileName.indexOf(userFileName) >= 0) {
                                currentBinFile = binFileMap;
                                break;
                            }
                        }
                        if (currentBinFile != null) {
                            // постоянно перепечатывать. todo для постоянной перепечати на b2b - взводить флаг needreprint
                            isAlreadyAttach = true;

                            result.put("FILEPATH", currentBinFile.get("FILEPATH"));
                            result.put("FULLFILEPATH", uploadFilePath + currentBinFile.get("FILEPATH"));
                            result.put("USERFILENAME", currentBinFile.get("FILENAME"));
                            queryParams.clear();
                            String fileSessionId;
                            String inputString;
                            if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (currentBinFile.get("FSID") != null)) {
                                inputString = B2BFileSessionController.concatenateSessionParams(Constants.FS_EXTERNAL,currentBinFile.get("FSID").toString() ,currentBinFile.get("FILENAME").toString());
                                queryParams.put("INPUTSTR", inputString);
                                fileSessionId = convertToSessionId(Constants.FS_EXTERNAL, currentBinFile.get("FSID").toString(), currentBinFile.get("FILENAME").toString());
                                result.put("FSID", currentBinFile.get("FSID"));
                            } else {
                                fileSessionId = convertToSessionId(Constants.FS_HARDDRIVE, currentBinFile.get("FILEPATH").toString(), currentBinFile.get("FILENAME").toString());
                                inputString = B2BFileSessionController.concatenateSessionParams(Constants.FS_HARDDRIVE,currentBinFile.get("FILEPATH").toString(), currentBinFile.get("FILENAME").toString());
                                queryParams.put("INPUTSTR", inputString);
                            }
                            logger.debug("encriptString = " + fileSessionId);
                            if (fileSessionId != null) {
                                result.put("ENCRIPTSTRING", fileSessionId);
                            }
                        }
                    }
                }
            }

            if (!isAlreadyAttach) {
                logger.debug("uploadFilePath = " + uploadFilePath);
                Map<String, Object> dataProviderResult;
                queryParams.put("PRODCONFID", params.get("PRODCONFID"));
                queryParams.put("ReturnAsHashMap", "TRUE");
                logger.debug("dsB2BProductDefaultValueByProdConfId params = " + queryParams);
                Map<String, Object> configRes = this.callService(B2BPOSWS, "dsB2BProductDefaultValueByProdConfId", queryParams, login, password);
                logger.debug("dsB2BProductDefaultValueByProdConfId result = " + configRes);
                String url = configRes.get("SIGNSERVERURL").toString();
                if (url.charAt(url.length() - 1) == '/') {
                    url = url.substring(0, url.length() - 1);
                }
                if (params.get("DWNLDSERVLETPATH") != null) {
                    url = url + params.get("DWNLDSERVLETPATH").toString();
                } else {
                    // по-умолчанию, оставлено для совместимости
                    url = url + "/bivsberlossws/b2bfileupload";
                }
                result.put("SERVERURL", url);
                // мапа с данными, которая будет передана в сервис формирования печатного дока, все данные в ПФ берутся из неё
                Map<String, Object> reportDataForReportCreate = new HashMap<String, Object>();
                if (reportData.get("DATAPROVID") != null) {
                    queryParams.clear();
                    queryParams.put("DATAPROVID", Long.valueOf(reportData.get("DATAPROVID").toString()));
                    queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
                    logger.debug("dsDataProviderBrowseListByParamEx params = " + queryParams);
                    dataProviderResult = this.callService("validatorsws", "dsDataProviderBrowseListByParamEx", queryParams, login, password);
                    logger.debug("dsDataProviderBrowseListByParamEx result = " + dataProviderResult);
                    queryParams.clear();
                    queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
                    //скидка для договора
                    queryParams.put("CONTRID", contrId);
                    queryParams.put("PRODCONFID", params.get("PRODCONFID"));
                    /*
                    logger.debug("providerMethod params = " + queryParams);
                    */
                    String dataProviderServiceName = getStringParamLogged(dataProviderResult, "SERVICENAME");
                    String dataProviderMethodName = getStringParamLogged(dataProviderResult, "METHODNAME");
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format(
                                "Data provider ([%s] %s) params = %s",
                                dataProviderServiceName, dataProviderMethodName, queryParams
                        ));
                    }
                    if (dataProviderMethodName.isEmpty() || dataProviderServiceName.isEmpty()) {
                        logger.error(String.format(
                                "Can't print report - calling %s from %s (with params = %s) caused error! Details (call result): %s",
                                "validatorsws", "dsDataProviderBrowseListByParamEx", queryParams, dataProviderResult
                        ));
                    }
                    Map<String, Object> providerMethodResult = this.callService(dataProviderServiceName, dataProviderMethodName, queryParams, login, password);
                    if (logger.isDebugEnabled()) {
                        loggerDebugPretty(logger, String.format(
                                "Data provider (method '%s' from '%s') result",
                                dataProviderMethodName, dataProviderServiceName
                        ), providerMethodResult);
                    }
                    /*
                    // спам
                    logger.debug("providerMethod result = " + providerMethodResult);
                    */
                    if (providerMethodResult.get("CONTRID") == null) {
                        logger.error(String.format(
                                "Can't print report - calling %s from %s (with params = %s) caused error! Details (call result): %s",
                                dataProviderMethodName, dataProviderServiceName, queryParams, providerMethodResult
                        ));
                        result.put("ERROR", "Can't print report");
                        return result;
                    } else {
                        String providerMethodResultErrorMsg = getStringParamLogged(providerMethodResult, "Error");
                        if (!providerMethodResultErrorMsg.isEmpty()) {
                            logger.error(String.format(
                                    "Can't print report - calling %s from %s (with params = %s) reported validation error - '%s'! Details (call result): %s",
                                    dataProviderMethodName, dataProviderServiceName, queryParams, providerMethodResultErrorMsg, providerMethodResult
                            ));
                            result.put("ERROR", providerMethodResultErrorMsg);
                            result.put("ERRORTYPE", "VALIDATION_IN_PROVIDER_FAILED");
                            return result;
                        }
                    }
                    XMLUtil.convertFloatToDate(providerMethodResult);

                    //reportData.putAll(providerMethodResult);
                    if (isDraft) {
                        providerMethodResult.put("ISSAMPLE", "TRUE");
                    } else {
                        providerMethodResult.put("ISSAMPLE", "");
                    }
                    //reportData.put("REPORTDATA", providerMethodResult);
                    // дополнение результатом работы провайдера мапы с данными, которая будет передана в сервис формирования печатного дока,
                    reportDataForReportCreate.putAll(providerMethodResult);

                }

                Map<String, Object> additionalReportData = (Map<String, Object>) params.get("ADDITIONALREPORTDATA");
                if (additionalReportData != null) {
                    // дополнение мапы с данными, которая будет передана в сервис формирования печатного дока, сведениями переданными напрямую при вызове печати
                    logger.debug("Additional report data (ADDITIONALREPORTDATA) was passed - will be used in report generation.");
                    reportDataForReportCreate.putAll(additionalReportData);
                }

                if (!reportDataForReportCreate.isEmpty()) {
                    reportData.put("REPORTDATA", reportDataForReportCreate);
                }

                reportData.put("templateName", reportData.get("TEMPLATENAME"));
                reportData.put(RETURN_AS_HASH_MAP, "TRUE");
                loggerDebugPretty(logger, "dsLibreOfficeReportCreate params", reportData);
                Map<String, Object> reportRes = this.callService(LIBREOFFICEREPORTSWS, "dsLibreOfficeReportCreate", reportData, login, password);
                loggerDebugPretty(logger, "dsLibreOfficeReportCreate result", reportRes);
                makeReportPDFCopiesIfNeeded(reportRes, reportData, login, password);
                if ((reportData.get("ORIGPRINTING") != null) && (getLongParam(reportData.get("ORIGPRINTING")) > 0)) {
                    boolean isNeedSign = true;
                    if (reportData.get("ISNEEDSIGN") != null) {
                        if (!Boolean.valueOf(reportData.get("ISNEEDSIGN").toString())) {
                            isNeedSign = false;
                        }
                    }
                    reportData.clear();
                    if (reportRes.get("REPORTDATA") != null) {
                        reportData.putAll((Map<String, Object>) reportRes.get("REPORTDATA"));
                        if (reportData.get("reportName") != null) {
                            String reportName = reportData.get("reportName").toString();
                            String fullPath = getReportFullPath(reportName, repFormat);
                            fullPath = fullPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
                            if (isDraft) {
                                // черновик. подписывать не нужно
                                String realFormat = fullPath.substring(fullPath.length() - 4);
                                queryParams.clear();
                                String fileSessionId = convertToSessionId(Constants.FS_HARDDRIVE, reportData.get("reportName").toString() + realFormat, userFileName + realFormat);
                                result.put("FILEPATH", reportData.get("reportName").toString() + realFormat);
                                result.put("FULLFILEPATH", uploadFilePath + reportData.get("reportName").toString() + realFormat);
                                result.put("USERFILENAME", userFileName + realFormat);
                                logger.debug("encriptString = " + fileSessionId);
                                if (fileSessionId != null) {
                                    result.put("ENCRIPTSTRING", fileSessionId);
                                }

                                //
                                boolean isNeedDraftAttach = getBooleanParamLogged(params, IS_NEED_DRAFT_ATTACH_PARAM_NAME, false);
                                if (isNeedDraftAttach) {
                                    Map<String, Object> attachRes = attachPrintedDocToContract(
                                            contrId, fullPath, userFileName, isNeedSign, reportName, repFormat, fileTypeId, login, password
                                    );
                                }

                            } else {
                                // оригинал. можно подписать.
                                // todo: возможно стоит подписывать не все подряд документы. хз.

                                String destPath = fullPath.substring(0, fullPath.length() - 4) + ""
                                        + fullPath.substring(fullPath.length() - 4, fullPath.length());
                                boolean signRes = true;
                                // по жизни пока ничего не
                                if (!".PDF".equalsIgnoreCase(repFormat)) {
                                    // подписывать только pdf
                                    isNeedSign = false;
                                }
                                // 
                                if (isNeedSign) {
                                    destPath = fullPath.substring(0, fullPath.length() - 4) + "_signed"
                                            + fullPath.substring(fullPath.length() - 4, fullPath.length());
                                    logger.debug("destPath = " + destPath);

                                    signRes = callSignSersvice(fullPath, destPath, "Сбербанк страхование", "Покупка полиса", login, password);
                                    logger.debug("signRes = " + signRes);
                                    logger.debug("signed: " + String.valueOf(signRes));
                                } else {
                                    //
                                    boolean isNeedDraftAttach = getBooleanParamLogged(params, IS_NEED_DRAFT_ATTACH_PARAM_NAME, false);
                                    if (isNeedDraftAttach) {
                                        Map<String, Object> attachRes = attachPrintedDocToContract(
                                                contrId, fullPath, userFileName, isNeedSign, reportName, repFormat, fileTypeId, login, password
                                        );
                                    }
                                }
                                if (signRes) {
                                    File f = new File(destPath);
                                    if (!f.exists()) {
                                        destPath = fullPath;
                                        // если подпись не удалась - вернуть не подписанный файл
                                        isNeedSign = false;
                                    }
                                    f = new File(destPath);
                                    if (f.exists() && f.getCanonicalPath().startsWith(uploadFilePath)) {
                                        userFileName = userFileName + "";
                                        fullPath = destPath;
                                        Map<String, Object> binParams = new HashMap<String, Object>();

                                        binParams.put("OBJID", params.get("CONTRID"));
                                        String realFormat = fullPath.substring(fullPath.length() - 4);

                                        //binParams.put("FILENAME", reportData.get("reportName").toString() + realFormat);
                                        binParams.put("FILENAME", userFileName + realFormat);
                                        String filePath = null;
                                        if (isNeedSign) {
                                            filePath = reportName + "_signed.pdf";
                                        } else {
                                            filePath = reportName + repFormat;
                                        }
                                        if (isNeedSign) {
                                            result.put("FILEPATH", reportData.get("reportName").toString() + "_signed" + realFormat);
                                            result.put("FULLFILEPATH", uploadFilePath + reportData.get("reportName").toString() + "_signed" + realFormat);
                                        } else {
                                            result.put("FILEPATH", reportData.get("reportName").toString() + "" + realFormat);
                                            result.put("FULLFILEPATH", uploadFilePath + reportData.get("reportName").toString() + "" + realFormat);
                                        }
                                        result.put("USERFILENAME", userFileName + realFormat);

                                        if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
                                            String masterUrlString = getSeaweedFSUrl();
                                            URL masterURL = new URL(masterUrlString);
                                            WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                                            Assignation a = client.assign(new AssignParams("b2breport", ReplicationStrategy.TwiceOnRack));
                                            int size = client.write(a.weedFSFile, a.location, new FileInputStream(f), binParams.get("FILENAME").toString());
                                            if (size == 0) {
                                                throw new Exception("Unable to write file to SeaweedFS");
                                            }
                                            filePath = a.weedFSFile.fid;
                                            result.put("FILEPATH", filePath);
                                            result.put("FULLFILEPATH", filePath);
                                            result.put("FSID", filePath);
                                            binParams.put("FSID", filePath); //f.getPath());
                                        }
                                        binParams.put("FILEPATH", filePath); //f.getPath());
                                        binParams.put("FILESIZE", f.length());
                                        binParams.put("FILETYPEID", fileTypeId == null ? 15 : fileTypeId);
                                        binParams.put("FILETYPENAME", "Полис подписанный");
                                        binParams.put("NOTE", "");
                                        logger.debug("binfile Create: " + binParams.toString());
                                        this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContract_BinaryFile_createBinaryFileInfo", binParams, login, password);
                                        logger.debug("dsB2BContract_BinaryFile_createBinaryFileInfo result = " + binParams);
                                        queryParams.clear();
                                        String fileSessionId;
                                        if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
                                            if (isNeedSign) {
                                                fileSessionId = convertToSessionId(Constants.FS_EXTERNAL, filePath, userFileName + "_signed" + realFormat);
                                            } else {
                                                fileSessionId = convertToSessionId(Constants.FS_EXTERNAL, filePath, userFileName + "" + realFormat);
                                            }
                                            result.put("FSID", filePath);
                                        } else if (isNeedSign) {
                                            fileSessionId = convertToSessionId(Constants.FS_HARDDRIVE, reportData.get("reportName").toString() + "_signed" + realFormat, userFileName + "_signed" + realFormat);
                                        } else {
                                            fileSessionId = convertToSessionId(Constants.FS_HARDDRIVE, reportData.get("reportName").toString() + realFormat, userFileName + "" + realFormat);
                                        }
                                        logger.debug("encriptString = " + fileSessionId);
                                        if (fileSessionId != null) {
                                            result.put("ENCRIPTSTRING", fileSessionId);
                                        }
                                    }
                                }
                            }

                        }
                    }
                } else {
                    reportData.clear();
                    if (reportRes.get("REPORTDATA") != null) {
                        logger.debug("REPORTDATA = " + reportRes.get("REPORTDATA"));
                        reportData.putAll((Map<String, Object>) reportRes.get("REPORTDATA"));
                        result.put("FILEPATH", reportData.get("reportName").toString() + repFormat);
                        result.put("FULLFILEPATH", uploadFilePath + reportData.get("reportName").toString() + repFormat);
                        result.put("USERFILENAME", userFileName + repFormat);
                        String fileSessionId = convertToSessionId(Constants.FS_HARDDRIVE, reportData.get("reportName").toString() + repFormat, userFileName + repFormat);
                        logger.debug("encriptString = " + fileSessionId);
                        if (fileSessionId != null) {
                            result.put("ENCRIPTSTRING", fileSessionId);
                        }
                    }
                }
            }
        } else {
            // шаблон - готовая пдф - даем сразу скачать ее.
            // поправка. скачать можно только из upload поэтому всепдфки надо туда тоже закинуть.
            //
            String repFulPath = getTemplateFullPath(reportData.get("TEMPLATENAME").toString(), login, password);
            repFulPath = repFulPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
            logger.debug("repFulPath = " + repFulPath);
            String realFormat = path.substring(path.length() - 4);
            queryParams.clear();
            result.put("FILEPATH", reportData.get("NAME").toString() + realFormat);
            result.put("FULLFILEPATH", repFulPath);
            result.put("USERFILENAME", userFileName + realFormat);
            String fileSessioId = convertToSessionId(Constants.FS_HARDDRIVE, repFulPath, userFileName + realFormat);
            logger.debug("encriptString = " + fileSessioId);
            if (fileSessioId != null) {
                result.put("ENCRIPTSTRING", fileSessioId);
            }

        }
        logger.debug("dsB2BPrintDocumentsWithFileNames end");
        return result;
    }

    private String getProductDefaultValueByProdConfIdAndName(Object prodConfId, String name, String login, String password) throws Exception {
        Map<String, Object> productConfQueryParams = new HashMap<String, Object>();
        productConfQueryParams.put("PRODCONFID", prodConfId);
        productConfQueryParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> callResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueByProdConfId", productConfQueryParams, true, login, password);
        String result = getStringParam(callResult.get(name));
        return result;
    }

    private String getViewType(String contrId, String login, String password) throws Exception {
        Map<String, Object> contrMap = new HashMap<String, Object>();
        contrMap.put("CONTRID", contrId);
        contrMap.put("NAME", "view");
        contrMap.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BContractSourceParamBrowseListByParam", contrMap, login, password);
        if (res.get("VALUE") != null) {
            return getStringParam(res.get("VALUE"));
        }
        return "";
    }

    @WsMethod(requiredParams = {"CONTRMAP", "EMAILTEXT", "ADDRESSLISTSTR"})
    public Map<String, Object> dsB2BSendEMailUniOpenAPI(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> contrMap = getMapParam(params, "CONTRMAP");
        String emailText = getStringParam(params, "EMAILTEXT");
        String addressListStr = getStringParam(params, "ADDRESSLISTSTR");
        result = sendReportByEmail(contrMap, emailText, addressListStr, login, password);
        return result;
    }

    // отправка письма без вложений
    protected Map<String, Object> sendReportByEmail(Map<String, Object> contrMap, String emailText, String addressListStr, String login, String password) throws Exception {
        logger.debug("Sending e-mail without attachments...");
        logger.debug("E-mail address(es): " + addressListStr);
        logger.debug("E-mail text:\n\n" + emailText + "\n");
        Map<String, Object> attachmentMap = null;
        Map<String, Object> sendResult = sendReportByEmail(attachmentMap, contrMap, emailText, addressListStr, login, password);
        logger.debug("Sending e-mail without attachments finished with result: " + sendResult);
        return sendResult;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BUsersCredsBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BUsersCredsBrowseListByParamEx", params);
        return result;
    }

    @WsMethod(requiredParams = {PARTNERS_DEPARTMENT_CODE_LIKE_KEY_NAME})
    public Map<String, Object> dsB2BPartnersDepartmentsListBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BPartnersDepartmentsListBrowseListByParamEx", params);
        return result;
    }

    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID", "CONTRNUMBER", "PRODUCTNAME"})
    public Map<String, Object> dsB2BSendNotificationEMail(Map<String, Object> params) throws Exception {

        logger.debug("Sending notification e-mail...");

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();

        Long contractID = getLongParam(params.get("CONTRID"));
        Long prodConfID = getLongParam(params.get("PRODCONFID"));

        // адрес(а) получателя(ей)
        //String addressListStr = "mmamaevbiv@gmail.com"; // !только для отладки!
        String addressListStr = getProductDefaultValueByProdConfIdAndName(prodConfID, "SENDNOTIFICATIONADDRESS", login, password);
        logger.debug("Notification e-mail recepient address(es) from SENDNOTIFICATIONADDRESS product default value: " + addressListStr);

        Map<String, Object> sendResult;
        if (addressListStr.isEmpty()) {
            // если не найден адрес(а) для отправки
            String sendResultStr = "No e-mail recepient address(es) found - sending notification e-mail is skipped.";
            logger.debug(sendResultStr);
            sendResult = new HashMap<String, Object>();
            sendResult.put(RESULT, sendResultStr);
        } else {

            //String userAccountIDKeyName = Constants.SESSIONPARAM_USERACCOUNTID;
            //Long userAccountID = getLongParam(params.get(userAccountIDKeyName));
            //logger.debug("Current user account id (" + userAccountIDKeyName + "): " + userAccountID);
            Map<String, Object> notificationParams = new HashMap<String, Object>();
            notificationParams.put("CONTRID", contractID);
            notificationParams.put("CONTRNUMBER", params.get("CONTRNUMBER"));
            notificationParams.put("PRODCONFID", prodConfID);

            Map<String, Object> userParams = new HashMap<String, Object>();
            userParams.put("CONTRID", contractID);
            userParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> userCreds = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BUsersCredsBrowseListByParamEx", userParams, true, login, password);
            if (isCallResultOK(userCreds)) {
                notificationParams.put("USERFULLNAME", getStringParam(userCreds.get("USERFULLNAME")));
                notificationParams.put("USERPHONE1", getStringParam(userCreds.get("USERPHONE1")));
                notificationParams.put("USEREMAIL", getStringParam(userCreds.get("USEREMAIL")));
            } else {
                // не удалось определить данные пользователя
                String errorText = "";
                if (userCreds != null) {
                    errorText = getStringParam(userCreds.get("Error"));
                }
                if (errorText.isEmpty()) {
                    errorText = "Unknown error while getting user creds for sending notification e-mail!";
                    if (userCreds != null) {
                        errorText = errorText + " Details: " + userCreds;
                    }
                } else {
                    errorText = "Error while getting user creds for sending notification e-mail: " + errorText;
                }
                logger.error(errorText);
                notificationParams.put("USERFULLNAME", NO_USER_CREDS_FOUND);
                notificationParams.put("USERPHONE1", NO_USER_CREDS_FOUND);
                notificationParams.put("USEREMAIL", NO_USER_CREDS_FOUND);
            }

            logger.debug("Notification e-mail parameters: " + notificationParams);
            String eMailText = generateEmailTextEx(notificationParams, "HTMLMAILPATHNOTIFICATION", login, password);

            if (eMailText.isEmpty()) {
                // если не сгененрирован текст письма (например, в случае отсутствия шаблона)
                String sendResultStr = "No e-mail text was generated - sending notification e-mail is skipped.";
                logger.debug(sendResultStr);
                sendResult = new HashMap<String, Object>();
                sendResult.put(RESULT, sendResultStr);
            } else {
                // тема письма
                String productName = getStringParam(params.get("PRODUCTNAME"));
                String smtpSubject = "Оформлен договор по продукту «" + productName + "»";
                notificationParams.put("SMTPSubject", smtpSubject);
                // отправка письма
                sendResult = sendReportByEmail(notificationParams, eMailText, addressListStr, login, password);
            }
        }
        result.put("EMAILRES", sendResult);

        logger.debug("Sending notification e-mail finished with result: " + result);

        return result;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BSendUWNotificationEMail(Map<String, Object> params) throws Exception {

        logger.debug("Sending UW notification e-mail...");

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();

        Long contractID = getLongParamLogged(params, "CONTRID");

        Map<String, Object> contract = (Map<String, Object>) params.get("CONTRMAP");
        if (contract == null) {
            Map<String, Object> contrParam = new HashMap<String, Object>();
            contrParam.put("CONTRID", contractID);
            contrParam.put(RETURN_AS_HASH_MAP, true);
            // для проверок потребуется полная мапа договора.
            contract = this.callService(Constants.B2BPOSWS, "dsB2BContrLoad", contrParam, login, password);
        }

        Long prodConfID = getLongParamLogged(contract, "PRODCONFID");
        String contractNumber = getStringParamLogged(contract, "CONTRNUMBER");
        String productName = getStringParamLogged(contract, "PRODUCTNAME");

        // адрес(а) получателя(ей)
        Map<String, Object> prodDefValMap = null;
        if (contract.get("PRODUCTMAP") == null) {
            prodDefValMap = getProductDefaultValueByProdConfId(prodConfID, login, password);
        } else {
            Map<String, Object> prodMap = (Map<String, Object>) contract.get("PRODUCTMAP");
            List<Map<String, Object>> prodDefValList = (List<Map<String, Object>>) prodMap.get("PRODDEFVALS");
            Map<String, Object> prepareListParam = new HashMap<String, Object>();
            prepareListParam.put("PRODDEFVALLIST", prodDefValList);
            prepareListParam.put("ReturnAsHashMap", "TRUE");
            prodDefValMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultMapFromList", prepareListParam, login, password);
        }
        String addressListStr = getStringParamLogged(prodDefValMap, "UWEMAILADDRESS");
        //addressListStr = "mmamaevbiv@gmail.com"; // !только для отладки!

        logger.debug("Notification e-mail recepient address(es) from SENDNOTIFICATIONADDRESS product default value: " + addressListStr);

        Map<String, Object> sendResult;
        if (addressListStr.isEmpty()) {
            // если не найден адрес(а) для отправки
            String sendResultStr = "No e-mail recepient address(es) found - sending notification e-mail is skipped.";
            logger.debug(sendResultStr);
            sendResult = new HashMap<String, Object>();
            sendResult.put(RESULT, sendResultStr);
        } else {

            //String userAccountIDKeyName = Constants.SESSIONPARAM_USERACCOUNTID;
            //Long userAccountID = getLongParam(params.get(userAccountIDKeyName));
            //logger.debug("Current user account id (" + userAccountIDKeyName + "): " + userAccountID);
            Map<String, Object> notificationParams = new HashMap<String, Object>();
            notificationParams.put("CONTRID", contractID);
            notificationParams.put("CONTRNUMBER", contractNumber);
            notificationParams.put("PRODUCTNAME", productName);
            String hash = base64Encode(getStringParamLogged(contract, "EXTERNALID"));
            notificationParams.put("HASH", hash);

            notificationParams.put("PRODUCTURL", getStringParamLogged(prodDefValMap, "PRODUCTURL"));
            notificationParams.put("PRODCONFID", prodConfID);

            // "нужно добавить ФИО клиента (видимо страхователь)"
            Map<String, Object> insurerMap = (Map<String, Object>) contract.get("INSURERMAP");
            String insurerLastName = getStringParamLogged(insurerMap, "LASTNAME");
            String insurerFirstName = getStringParamLogged(insurerMap, "FIRSTNAME");
            String insurerMiddleName = getStringParamLogged(insurerMap, "MIDDLENAME");
            String insurerFullName = insurerLastName + " " + insurerFirstName + (insurerMiddleName.isEmpty() ? "" : " " + insurerMiddleName);
            logger.debug("INSURERFULLNAME = " + insurerFullName);
            notificationParams.put("INSURERFULLNAME", insurerFullName);

            // "др застрахованного"
            Map<String, Object> insuredMap = (Map<String, Object>) contract.get("INSUREDMAP");
            if (insuredMap != null) {
                String insuredBirthDate = (String) parseAnyDate(insuredMap.get("BIRTHDATE"), String.class, "BIRTHDATE");
                notificationParams.put("INSUREDBIRTHDATE", insuredBirthDate);
            }
            Map<String, Object> userParams = new HashMap<String, Object>();
            userParams.put("CONTRID", contractID);
            userParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> userCreds = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BUsersCredsBrowseListByParamEx", userParams, true, login, password);
            if (isCallResultOK(userCreds)) {
                notificationParams.put("USERFULLNAME", getStringParamLogged(userCreds, "USERFULLNAME"));
                notificationParams.put("USERPHONE1", getStringParamLogged(userCreds, "USERPHONE1"));
                notificationParams.put("USEREMAIL", getStringParamLogged(userCreds, "USEREMAIL"));
                notificationParams.put("CREATEUSERFULLNAME", getStringParamLogged(userCreds, "CREATEUSERFULLNAME"));
                notificationParams.put("CREATEUSERPHONE1", getStringParamLogged(userCreds, "CREATEUSERPHONE1"));
                notificationParams.put("CREATEUSEREMAIL", getStringParamLogged(userCreds, "CREATEUSEREMAIL"));

                // ПАРТНЕР
                Long createUserDepartmentID = getLongParamLogged(userCreds, "CREATEUSERDEPARTMENTID");
                // получение наименование партнера для конкретного дочернего подразделения по ИД этого дочернего подразделения
                String partnerName = getPartnerNameByChildDepartmentID(createUserDepartmentID, login, password);
                notificationParams.put("CREATEUSERPARTNERNAME", partnerName);

            } else {
                // не удалось определить данные пользователя
                String errorText = "";
                if (userCreds != null) {
                    errorText = getStringParamLogged(userCreds, "Error");
                }
                if (errorText.isEmpty()) {
                    errorText = "Unknown error while getting user creds for sending notification e-mail!";
                    if (userCreds != null) {
                        errorText = errorText + " Details: " + userCreds;
                    }
                } else {
                    errorText = "Error while getting user creds for sending notification e-mail: " + errorText;
                }
                logger.error(errorText);
                notificationParams.put("USERFULLNAME", NO_USER_CREDS_FOUND);
                notificationParams.put("USERPHONE1", NO_USER_CREDS_FOUND);
                notificationParams.put("USEREMAIL", NO_USER_CREDS_FOUND);
                notificationParams.put("CREATEUSERFULLNAME", NO_USER_CREDS_FOUND);
                notificationParams.put("CREATEUSERPHONE1", NO_USER_CREDS_FOUND);
                notificationParams.put("CREATEUSEREMAIL", NO_USER_CREDS_FOUND);
            }

            logger.debug("Notification e-mail parameters: " + notificationParams);

            // имя ключа для PRODDEFVAL-а, хранящего путь до html-шаблона письма
            String mailName = getStringOptionalParamLogged(params, "HTMLMAILPATHKEYNAME", "HTMLMAILPATHUW");
            // генерация текста письма на основании шаблона и переданных параметров
            String eMailText = generateEmailTextEx(notificationParams, prodDefValMap, mailName, login, password);

            if (eMailText.isEmpty()) {
                // если не сгененрирован текст письма (например, в случае отсутствия шаблона)
                String sendResultStr = "No e-mail text was generated - sending notification e-mail is skipped.";
                logger.debug(sendResultStr);
                sendResult = new HashMap<String, Object>();
                sendResult.put(RESULT, sendResultStr);
            } else {
                // основной фрагмент темы письма
                //String smtpSubjectMainText = getStringOptionalParamLogged(params, "MAILSUBJECTMAINTEXT", "требует андеррайтинга");
                String smtpSubjectMainText = getStringOptionalParamLogged(params, "MAILSUBJECTMAINTEXT", ""); // по документу "Описание бизнес-процесса обработки заявки из Front"
                // формирование темы письма
                //String smtpSubject = "Договор № " + contractNumber + " по продукту «" + productName + "» " + smtpSubjectMainText + ".";
                //String smtpSubject = String.format("Договор № %s по продукту «%s» %s.", contractNumber, productName, smtpSubjectMainText);
                String smtpSubject = String.format("%sЗапрос на оформление договора страхования", smtpSubjectMainText); // по документу "Описание бизнес-процесса обработки заявки из Front"
                notificationParams.put("MAILSUBJECTMAINTEXT", "ДОСЫЛ");
                notificationParams.put("SMTPSubject", smtpSubject);
                // отправка письма
                sendResult = sendReportByEmail(notificationParams, eMailText, addressListStr, login, password);
            }
        }
        result.put("EMAILRES", sendResult);

        logger.debug("Sending notification e-mail finished with result: " + result);

        return result;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BSendSellerNotificationEMail(Map<String, Object> params) throws Exception {

        logger.debug("Sending Seller notification e-mail...");

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();

        Long contractID = getLongParamLogged(params, "CONTRID");

        Map<String, Object> contract = params;
        //Map<String, Object> contract = (Map<String, Object>) params.get("CONTRMAP");
        if (contract == null) {
            Map<String, Object> contrParam = new HashMap<String, Object>();
            contrParam.put("CONTRID", contractID);
            contrParam.put(RETURN_AS_HASH_MAP, true);
            // для проверок потребуется полная мапа договора.
            contract = this.callService(Constants.B2BPOSWS, "dsB2BContrLoad", contrParam, login, password);
        }

        Long prodConfID = getLongParamLogged(contract, "PRODCONFID");
        String contractNumber = getStringParamLogged(contract, "CONTRNUMBER");
        String productName = getStringParamLogged(contract, "PRODUCTNAME");

        // адрес(а) получателя(ей)
        Map<String, Object> prodDefValMap = null;
        if (contract.get("PRODUCTMAP") == null) {
            prodDefValMap = getProductDefaultValueByProdConfId(prodConfID, login, password);
        } else {
            Map<String, Object> prodMap = (Map<String, Object>) contract.get("PRODUCTMAP");
            List<Map<String, Object>> prodDefValList = (List<Map<String, Object>>) prodMap.get("PRODDEFVALS");
            Map<String, Object> prepareListParam = new HashMap<String, Object>();
            prepareListParam.put("PRODDEFVALLIST", prodDefValList);
            prepareListParam.put("ReturnAsHashMap", "TRUE");
            prodDefValMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultMapFromList", prepareListParam, login, password);
        }
        String addressListStr = getStringParamLogged(prodDefValMap, "UWEMAILADDRESS");
        //addressListStr = "kiryanov_as@bivgroup.com"; // !только для отладки!

        logger.debug("Notification e-mail recepient address(es) from SENDNOTIFICATIONADDRESS product default value: " + addressListStr);

        Map<String, Object> sendResult;
        if (addressListStr.isEmpty()) {
            // если не найден адрес(а) для отправки
            String sendResultStr = "No e-mail recepient address(es) found - sending notification e-mail is skipped.";
            logger.debug(sendResultStr);
            sendResult = new HashMap<String, Object>();
            sendResult.put(RESULT, sendResultStr);
        } else {

            //String userAccountIDKeyName = Constants.SESSIONPARAM_USERACCOUNTID;
            //Long userAccountID = getLongParam(params.get(userAccountIDKeyName));
            //logger.debug("Current user account id (" + userAccountIDKeyName + "): " + userAccountID);
            Map<String, Object> notificationParams = new HashMap<String, Object>();
            notificationParams.put("CONTRID", contractID);
            notificationParams.put("CONTRNUMBER", contractNumber);
            notificationParams.put("PRODUCTNAME", productName);
            String hash = base64Encode(getStringParamLogged(contract, "EXTERNALID"));
            notificationParams.put("HASH", hash);

            notificationParams.put("PRODUCTURL", getStringParamLogged(prodDefValMap, "OPENCONTRURL"));
            notificationParams.put("PRODCONFID", prodConfID);

            // "нужно добавить ФИО клиента (видимо страхователь)"
            Map<String, Object> insurerMap = (Map<String, Object>) contract.get("INSURERMAP");
            String insurerLastName = getStringParamLogged(insurerMap, "LASTNAME");
            String insurerFirstName = getStringParamLogged(insurerMap, "FIRSTNAME");
            String insurerMiddleName = getStringParamLogged(insurerMap, "MIDDLENAME");
            String insurerFullName = insurerLastName + " " + insurerFirstName + (insurerMiddleName.isEmpty() ? "" : " " + insurerMiddleName);
            logger.debug("INSURERFULLNAME = " + insurerFullName);
            notificationParams.put("INSURERFULLNAME", insurerFullName);

            // "др застрахованного"
            /*   Map<String, Object> insuredMap = (Map<String, Object>) contract.get("INSUREDMAP");
            String insuredBirthDate = (String) parseAnyDate(insuredMap.get("BIRTHDATE"), String.class, "BIRTHDATE");
            notificationParams.put("INSUREDBIRTHDATE", insuredBirthDate);
             */
            Map<String, Object> userParams = new HashMap<String, Object>();
            userParams.put("CONTRID", contractID);
            userParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> userCreds = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BUsersCredsBrowseListByParamEx", userParams, true, login, password);
            if (isCallResultOK(userCreds)) {
                notificationParams.put("USERFULLNAME", getStringParamLogged(userCreds, "USERFULLNAME"));
                notificationParams.put("USERPHONE1", getStringParamLogged(userCreds, "USERPHONE1"));
                notificationParams.put("USEREMAIL", getStringParamLogged(userCreds, "USEREMAIL"));
                notificationParams.put("CREATEUSERFULLNAME", getStringParamLogged(userCreds, "CREATEUSERFULLNAME"));
                notificationParams.put("CREATEUSERPHONE1", getStringParamLogged(userCreds, "CREATEUSERPHONE1"));
                notificationParams.put("CREATEUSEREMAIL", getStringParamLogged(userCreds, "CREATEUSEREMAIL"));
                addressListStr = getStringParamLogged(userCreds, "CREATEUSEREMAIL");
                // ПАРТНЕР
                String partnerName = "";
                String errorText = "";
                Map<String, Object> partnerInfo = null;
                Long createUserDepartmentID = getLongParamLogged(userCreds, "CREATEUSERDEPARTMENTID");
                if (createUserDepartmentID != null) {
                    Map<String, Object> partnerParams = new HashMap<String, Object>();
                    partnerParams.put(PARTNERS_DEPARTMENT_CODE_LIKE_KEY_NAME, PARTNERS_DEPARTMENT_CODE_LIKE);
                    partnerParams.put("USERDEPARTMENTID", createUserDepartmentID);
                    partnerParams.put(RETURN_AS_HASH_MAP, true);
                    partnerInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPartnersDepartmentsListBrowseListByParamEx", partnerParams, true, login, password);
                    if (isCallResultOK(partnerInfo)) {
                        partnerName = getStringParamLogged(partnerInfo, "DEPTSHORTNAME");
                    } else {
                        // не удалось определить данные Партнера
                        errorText = "";
                        if (partnerInfo != null) {
                            errorText = getStringParamLogged(partnerInfo, "Error");
                        }
                    }
                }
                if (partnerName.isEmpty()) {
                    // не удалось определить данные Партнера
                    if (errorText.isEmpty()) {
                        errorText = "Unknown error while getting partner info for sending notification e-mail!";
                        if (partnerInfo != null) {
                            errorText = errorText + " Details: " + partnerInfo;
                        }
                    } else {
                        errorText = "Error while getting partner info for sending notification e-mail: " + errorText;
                    }
                    logger.error(errorText);
                    partnerName = NO_PARTNER_INFO_FOUND;
                }
                notificationParams.put("CREATEUSERPARTNERNAME", partnerName);

            } else {
                // не удалось определить данные пользователя
                String errorText = "";
                if (userCreds != null) {
                    errorText = getStringParamLogged(userCreds, "Error");
                }
                if (errorText.isEmpty()) {
                    errorText = "Unknown error while getting user creds for sending notification e-mail!";
                    if (userCreds != null) {
                        errorText = errorText + " Details: " + userCreds;
                    }
                } else {
                    errorText = "Error while getting user creds for sending notification e-mail: " + errorText;
                }
                logger.error(errorText);
                notificationParams.put("USERFULLNAME", NO_USER_CREDS_FOUND);
                notificationParams.put("USERPHONE1", NO_USER_CREDS_FOUND);
                notificationParams.put("USEREMAIL", NO_USER_CREDS_FOUND);
                notificationParams.put("CREATEUSERFULLNAME", NO_USER_CREDS_FOUND);
                notificationParams.put("CREATEUSERPHONE1", NO_USER_CREDS_FOUND);
                notificationParams.put("CREATEUSEREMAIL", NO_USER_CREDS_FOUND);
            }

            logger.debug("Notification e-mail parameters: " + notificationParams);

            // имя ключа для PRODDEFVAL-а, хранящего путь до html-шаблона письма
            String mailName = getStringOptionalParamLogged(params, "HTMLMAILPATHKEYNAME", "HTMLMAILPATHSELLERADDDOCS");
            // генерация текста письма на основании шаблона и переданных параметров
            String eMailText = generateEmailTextEx(notificationParams, prodDefValMap, mailName, login, password);

            if (eMailText.isEmpty()) {
                // если не сгененрирован текст письма (например, в случае отсутствия шаблона)
                String sendResultStr = "No e-mail text was generated - sending notification e-mail is skipped.";
                logger.debug(sendResultStr);
                sendResult = new HashMap<String, Object>();
                sendResult.put(RESULT, sendResultStr);
            } else {
                // основной фрагмент темы письма
                //String smtpSubjectMainText = getStringOptionalParamLogged(params, "MAILSUBJECTMAINTEXT", "требует андеррайтинга");
                String smtpSubjectMainText = getStringOptionalParamLogged(params, "MAILSUBJECTMAINTEXT", ""); // по документу "Описание бизнес-процесса обработки заявки из Front"
                // формирование темы письма
                //String smtpSubject = "Договор № " + contractNumber + " по продукту «" + productName + "» " + smtpSubjectMainText + ".";
                //String smtpSubject = String.format("Договор № %s по продукту «%s» %s.", contractNumber, productName, smtpSubjectMainText);
                String smtpSubject = String.format("%s", smtpSubjectMainText); // по документу "Описание бизнес-процесса обработки заявки из Front"
                notificationParams.put("MAILSUBJECTMAINTEXT", "ДОСЫЛ");
                notificationParams.put("SMTPSubject", smtpSubject);
                // отправка письма
                sendResult = sendReportByEmail(notificationParams, eMailText, addressListStr, login, password);
            }
        }
        result.put("EMAILRES", sendResult);

        logger.debug("Sending notification e-mail finished with result: " + result);

        return result;
    }

    /*
     * Объединить несколько pdf файлов в один
     * @author reson
     * @param params
     * <UL>
     * <LI>REPORTSFILENAMES - список мап с именами файлов и путями (если нужно) до них. В мапе имя файла FILENAME</LI>
     * <LI>DESTINATIONFILENAME - наименование выходного файла</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>RESULT - имя результирующего файла в мапе</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REPORTSFILENAMES"})
    public Map<String, Object> dsB2BMergeReports(Map<String, Object> params) throws Exception {
        List<Map<String, Object>> fileNamesList = (List<Map<String, Object>>) params.get("REPORTSFILENAMES");
        String uploadFilePath = getUploadFolder();

        PDFMergerUtility merger = new PDFMergerUtility();
        StringBuilder sb = new StringBuilder();
        String filePath = "";
        for (Map<String, Object> map : fileNamesList) {
            filePath = getStringParam(map.get("FILEPATH"));
            if (filePath.isEmpty()) {
                filePath = uploadFilePath;
            }
            String fullFileName = String.format("%s%s%s", filePath, File.separator, map.get("FILENAME"));
            sb.append(" [").append(fullFileName).append("] ");
            merger.addSource(fullFileName);
        }
        String destinationFileName;
        if (params.get("DESTINATIONFILENAME") == null) {
            destinationFileName = UUID.randomUUID().toString();
        } else {
            destinationFileName = params.get("DESTINATIONFILENAME").toString();
        }
        String fullDestinationFileName = String.format("%s%s%s.%s", uploadFilePath, File.separator, destinationFileName, "pdf");
        merger.setDestinationFileName(fullDestinationFileName);
        try {
            merger.mergeDocuments();
        } catch (IOException ex) {
            throw new PDFMergerException("Error merge pdf documents " + sb.toString(), ex);
        } catch (COSVisitorException ex) {
            throw new PDFMergerException("Error merge pdf documents " + sb.toString(), ex);
        }
        //
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("RESULT", destinationFileName);
        return result;
    }

    private String convertToSessionId(String fsType, String someId, String userDocName) {
        logger.debug("Converting params fo fileSessionId");
        Map<String, Object> sessionParams = new HashMap<>();
        sessionParams.put(B2BFileSessionController.FS_TYPE_PARAMNAME, fsType);
        sessionParams.put(SOME_ID_PARAMNAME, someId);
        sessionParams.put(USER_DOCNAME_PARAMNAME, userDocName);
        SessionController controller = new B2BFileSessionController();
        String id = controller.createSession(sessionParams);
        logger.debug("\u0009sessionId = " + id);
        return id;
    }

    /*
    private Map<String, Object> createContractDocumentEntry(Long contractId, Map<String, Object> reportData, String login, String password) {
        IS_NEED_DOC_ENTRY_PARAMNAME
        Map<String, Object> docEntryParams = new HashMap<>();
        docEntryParams.put("CONTRID", contractId);
        docEntryParams.put("PRODBINDOCID", reportData.get("PRODBINDOCID"));
        callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractDocumentCreate???", docEntryParams, login, password);
    }
    */

    // фрагмент метода dsB2BPrintDocumentsWithFileNames, отвечающий за прикрепление файла
    private Map<String, Object> attachPrintedDocToContract(Long contractId, String fullPath, String userFileName, boolean isNeedSign, String reportName, String repFormat, Long fileTypeId, String login, String password) throws Exception {
        String realFormat = fullPath.substring(fullPath.length() - 4);
        String filePath = null;
        if (isNeedSign) {
            filePath = reportName + "_signed.pdf";
        } else {
            filePath = reportName + repFormat;
        }
        /*
        if (isNeedSign) {
            result.put("FILEPATH", reportData.get("reportName").toString() + "_signed" + realFormat);
            result.put("FULLFILEPATH", uploadFilePath + reportData.get("reportName").toString() + "_signed" + realFormat);
        } else {
            result.put("FILEPATH", reportData.get("reportName").toString() + "" + realFormat);
            result.put("FULLFILEPATH", uploadFilePath + reportData.get("reportName").toString() + "" + realFormat);
        }
        result.put("USERFILENAME", userFileName + realFormat);
        */

        Map<String, Object> binParams = new HashMap<String, Object>();
        binParams.put("OBJID", contractId);
        //binParams.put("FILENAME", reportData.get("reportName").toString() + realFormat);
        binParams.put("FILENAME", userFileName + realFormat);

        File f = new File(fullPath);

        if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
            String masterUrlString = getSeaweedFSUrl();
            URL masterURL = new URL(masterUrlString);
            WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
            Assignation a = client.assign(new AssignParams("b2breport", ReplicationStrategy.TwiceOnRack));
            int size = client.write(a.weedFSFile, a.location, new FileInputStream(f), binParams.get("FILENAME").toString());
            if (size == 0) {
                throw new Exception("Unable to write file to SeaweedFS");
            }
            filePath = a.weedFSFile.fid;
            /*
            result.put("FILEPATH", filePath);
            result.put("FULLFILEPATH", filePath);
            result.put("FSID", filePath);
            */
            binParams.put("FSID", filePath); //f.getPath());
        }
        binParams.put("FILEPATH", filePath); //f.getPath());
        binParams.put("FILESIZE", f.length());
        binParams.put("FILETYPEID", fileTypeId == null ? 15 : fileTypeId);
        binParams.put("FILETYPENAME", "Полис подписанный");
        binParams.put("NOTE", "");
        binParams.put(RETURN_AS_HASH_MAP, true);
        String serviceName = B2BPOSWS_SERVICE_NAME;
        String methodName = "dsB2BContract_BinaryFile_createBinaryFileInfo";
        Map<String, Object> attachResult = callServiceLogged(serviceName, methodName, binParams, login, password);
        Long insBinFileId = getLongParamLogged(attachResult, "BINFILEID");
        if (insBinFileId == null) {
            String error = String.format(
                    "Unable to create binary file info by calling '%s' from '%s' (with params = %s)! Details (call result): %s",
                    methodName, serviceName, binParams, attachResult
            );
            throw new Exception(error);
        }
        return attachResult;
    }

    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID", "REPLEVELLIST"})
    public Map<String, Object> dsB2BGetAndPrintWithAttachingReportsByRepLevel(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BGetAndPrintWithAttachingReportsByRepLevel start...");
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        Long contractId = getLongParamLogged(params, "CONTRID");
        Long prodConfId = getLongParamLogged(params, "PRODCONFID");
        String repLevelListStr  = getStringParamLogged(params, "REPLEVELLIST");
        Map<String, Object> result = getAndPrintWithAttachingReportsByRepLevel(contractId, prodConfId, repLevelListStr, login, password);
        logger.debug("dsB2BGetAndPrintWithAttachingReportsByRepLevel finished.");
        return result;
    }

    private Map<String, Object> getAndPrintWithAttachingReportsByRepLevel(Long contractId, Long prodConfId, String repLevelListStr, String login, String password) throws Exception {
        Map<String, Object> reportParams = new HashMap<>();
        // reportParams.put("CONTRID", contractId);
        reportParams.put("PRODCONFID", prodConfId);
        // REPLEVEL = 111100 - полис образец
        // REPLEVEL = 111200 - платежка (ПД4)
        // REPLEVEL = 111300 - полис
        // reportParams.put("REPLEVELLIST", "111100, 111200, 111300");
        reportParams.put("REPLEVELLIST", repLevelListStr);
        String methodName = "dsB2BProductReportBrowseListByParamEx";
        List<Map<String, Object>> reportList = callServiceAndGetListFromResultMapLogged(
                B2BPOSWS_SERVICE_NAME, methodName, reportParams, login, password
        );
        Map<String, Object> result = new HashMap<>();
        if ((reportList == null) || (reportList.isEmpty())) {
            logger.error(String.format(
                    "Unable to find reports by calling %s with params = %s",
                    methodName, reportParams
            ));
            result.put(ERROR, "Не удалось получить сведения о необходимых печатных документах!");
        } else {
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Map<String, Object> report : reportList) {
                Map<String, Object> printParams = new HashMap<>();
                report.put("REPORTFORMATS", ".pdf");
                printParams.put("CONTRID", contractId);
                printParams.put("REPORTDATA", report);
                printParams.put("PRODCONFID", prodConfId);
                // printParams.put(NEED_REPRINT_PARAMNAME, true);
                printParams.put(IS_NEED_DRAFT_ATTACH_PARAM_NAME, true);
                printParams.put(IS_USE_PRODREPID_AS_FILETYPEID, true);
                printParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> repPrintResult = callServiceLogged(
                        B2BPOSWS_SERVICE_NAME, "dsB2BPrintDocuments", printParams, login, password
                );
                resultList.add(repPrintResult);
            }
            result.put(RESULT, reportList);
        }

        return result;
    }

}
