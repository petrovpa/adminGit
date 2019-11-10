/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.cargo.documents;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import static com.bivgroup.services.b2bposws.facade.B2BBaseFacade.base64Encode;
import com.bivgroup.services.b2bposws.system.Constants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author kkulkov
 */
@BOName("ContractDocumentsMailSenderFacade")
public class ContractDocumentsMailSenderFacade extends B2BBaseFacade {

    private static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    private static final String INSPRODUCTWS_SERVICE_NAME = Constants.INSPRODUCTWS;
    private static final String LIBREOFFICEREPORTSWS_SERVICE_NAME = Constants.LIBREOFFICEREPORTSWS;
    private static final String WEBSMSWS_SERVICE_NAME = Constants.SIGNWEBSMSWS;
    private static final String SIGNWS_SERVICE_NAME = Constants.SIGNWS;
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    private static final String PROJECT_PARAM_NAME = "project";
    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final private byte[] Salt = {
        (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
        (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};
    final private String divider = "__div__";
    private Long sessionTimeOut = 10L;

    private static final String VALIDATORSWS_SERVICE_NAME = Constants.VALIDATORSWS;

    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID", "STATESYSNAME", "CONTRNUMBER"})
    public Map<String, Object> dsSendDocumentsPackage(Map<String, Object> params) throws Exception {
        logger.debug("begin!!! dsSendDocumentsPackage");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Long contrId = getLongParam(params.get("CONTRID"));
        Long prodConfId = getLongParam(params.get("PRODCONFID")); //todo: адьтернативный вариант определения идентификатора продутка - по известному договору
        Map<String, Object> result = new HashMap<String, Object>();
        Long needCheck = getLongParam(params.get("NEEDCHECK"));
        Map<String, Object> сheckParams = new HashMap<String, Object>();
        String url = "";
        String mailHtmlPash = "";
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("PRODCONFID", params.get("PRODCONFID"));
        queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> configRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", queryParams, login, password);
        Long checkId = null;
        if ((configRes != null) && (configRes.get("DOWNLOADDOCUMENTSCHECKID") != null)) {
            checkId = getLongParam(configRes.get("DOWNLOADDOCUMENTSCHECKID"));
        }
        if ((configRes != null) && (configRes.get("PRODUCTURL") != null)) {
            url = (String) configRes.get("PRODUCTURL");
        }
        if ((configRes != null) && (configRes.get("HTMLMAILPATHSUCCES") != null)) {
            mailHtmlPash = (String) configRes.get("HTMLMAILPATHSUCCES");
        } else if ((configRes != null) && (configRes.get("HTMLMAILPATH") != null)) {
            mailHtmlPash = (String) configRes.get("HTMLMAILPATH");
        }
        Map<String, Object> configB2bRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueByProdConfId", queryParams, login, password);
        if ((configB2bRes != null) && (configB2bRes.get("DOWNLOADDOCUMENTSCHECKID") != null)) {
            checkId = getLongParam(configB2bRes.get("DOWNLOADDOCUMENTSCHECKID"));
        }
        if ((configB2bRes != null) && (configB2bRes.get("PRODUCTURL") != null)) {
            url = (String) configB2bRes.get("PRODUCTURL");
        }
        if ((configB2bRes != null) && (configB2bRes.get("HTMLMAILPATHSUCCES") != null)) {
            mailHtmlPash = (String) configB2bRes.get("HTMLMAILPATHSUCCES");
        } else if ((configB2bRes != null) && (configB2bRes.get("HTMLMAILPATH") != null)) {
            mailHtmlPash = (String) configB2bRes.get("HTMLMAILPATH");
        }

        if (needCheck > 0) {
            //Проверка - все ли документы прикреплены.

            if ((checkId != null) && (checkId > 0)) {
                Map<String, Object> checkNameParams = new HashMap<String, Object>();

                checkNameParams.put("ReturnAsHashMap", "TRUE");
                checkNameParams.put("CHECKID", checkId);
                Map<String, Object> rCheckNameParams = this.callService(VALIDATORSWS_SERVICE_NAME, "dsCheckBrowseListByParamEx", checkNameParams, login, password);
                if (rCheckNameParams.get("NAME") != null) {
                    Map<String, Object> checkParams = new HashMap<String, Object>();
                    checkParams.putAll(params);
                    checkParams.put("VALIDATORNAME", rCheckNameParams.get("NAME"));
                    checkParams.put("VALIDATORRESULTTYPE", 1L);
                    checkParams.put("CONTRID", params.get("CONTRID"));
                    checkParams.put("PRODCONFID", params.get("PRODCONFID"));
                    Map<String, Object> rCheckParams = this.callService(VALIDATORSWS_SERVICE_NAME, "dsValidateByValidatorName", checkParams, login, password);
                    if (rCheckParams.get(RESULT) != null) {
                        Map<String, Object> checkRes = (Map<String, Object>) rCheckParams.get(RESULT);
                        if (checkRes.get("FINALRESULT") != null) {
                            if (!checkRes.get("FINALRESULT").toString().equalsIgnoreCase("0")) {
                                result.putAll(checkRes);
                                return result;
                            }
                        }
                    }
                }
            }
        }
        Map<String, Object> queryDocumentsParams = new HashMap<String, Object>();
        Map<String, Object> docFiles = new HashMap<String, Object>();
        String uploadPath = Config.getConfig().getParam("uploadPath", "");

        //queryDocumentsParams.put("CONTRID", contrId);
        queryDocumentsParams.put("PRODCONFID", prodConfId);
        queryDocumentsParams.put("EDOC", 1L);
        Map<String, Object> documentsRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductReportBrowseListByParamEx", queryDocumentsParams, login, password);
        if ((documentsRes != null) && (documentsRes.get(RESULT) != null)) {
            if (documentsRes.get(RESULT) instanceof List) {
                List<Map<String, Object>> docList = (List<Map<String, Object>>) documentsRes.get(RESULT);
                logger.debug("begin!!! print DocList");

                for (Map<String, Object> doc : docList) {
                    Map<String, Object> printParams = new HashMap<String, Object>();
                    printParams.put("CONTRID", contrId);
                    printParams.put("PRODCONFID", prodConfId);
                    doc.put("REPORTFORMATS", ".pdf");
                    printParams.put("REPORTDATA", doc);
                    printParams.put(RETURN_AS_HASH_MAP, "TRUE");
                    Map<String, Object> pResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPrintDocumentsWithFileNames", printParams, login, password);
                    if ((pResult != null) && (pResult.get("FULLFILEPATH") != null)) {
                        docFiles.put(pResult.get("FULLFILEPATH").toString(), pResult);
                    } else if ((pResult != null) && (pResult.get("FILEPATH") != null)) {
                        docFiles.put(uploadPath + pResult.get("FILEPATH").toString(), pResult);
                    }
                }
                logger.debug("end!!! print DocList");

            }
        }
        сheckParams.clear();
        logger.debug("Выбор электронной почты для отправки документов...");
        // потча для отправки может быть передана в явном виде
        String eMail = getStringParam(params.get("EMAIL"));
        logger.debug("EMAIL = " + eMail);
        // альтернативный вариант выбора почты - из контаков страхователя
        // (либо личная почта страхователя, либо первая из указанных)
        if (eMail.isEmpty()) {
            Map<String, Object> insurer = (Map<String, Object>) params.get("INSURERMAP");
            if (insurer != null) {
                List<Map<String, Object>> contacts = (List<Map<String, Object>>) insurer.get("contactList");
                if ((contacts != null) && (contacts.size() > 0)) {
                    String anyEMail = "";
                    String personalEMail = "";
                    for (Map<String, Object> contact : contacts) {
                        String contactTypeSysName = getStringParam(contact.get("CONTACTTYPESYSNAME"));
                        if ("PersonalEmail".equalsIgnoreCase(contactTypeSysName)) {
                            personalEMail = getStringParam(contact.get("VALUE"));
                            logger.debug("INSURERMAP.contactList[PersonalEmail].VALUE = " + personalEMail);
                        } else if ((anyEMail.isEmpty()) && (contactTypeSysName.contains("Email"))) {
                            anyEMail = getStringParam(contact.get("VALUE"));
                            logger.debug("INSURERMAP.contactList[" + contactTypeSysName + "].VALUE = " + anyEMail);
                        }
                    }
                    if (!personalEMail.isEmpty()) {
                        logger.debug("EMAIL = " + eMail);
                        eMail = personalEMail;
                    } else if (!anyEMail.isEmpty()) {
                        eMail = anyEMail;
                    }
                }
            }
        }
        logger.debug("Выбрана электронная почта для отправки документов (" + eMail + ").");

        Object sessionIDObj = params.get("sid");
        if (sessionIDObj == null) {
            sessionIDObj = params.get("SESSIONID");
        }
        String sessionID = "";
        if (sessionIDObj != null) {
            sessionID = sessionIDObj.toString();
        }
        Map<String, String> attachMap = new HashMap<String, String>();

        for (Entry<String, Object> atatchEntry : docFiles.entrySet()) {
            if (atatchEntry.getValue() != null) {
                Map<String, Object> attachentityMap = (Map<String, Object>) atatchEntry.getValue();
                if (attachentityMap.get("USERFILENAME") != null) {
                    attachMap.put(attachentityMap.get("USERFILENAME").toString(), atatchEntry.getKey());
                }
            }
        }
        Map<String, Object> insMap = (Map<String, Object>) params.get("INSURERMAP");
        insMap.put("CONTRID", contrId);

        String emailText = generateEmailTextEx(url, "", insMap, mailHtmlPash, login, password);
        logger.debug("email send: " + emailText);

        logger.debug("begin!!! send attach by email");
        boolean skipSend = false;
        if (params.get("SKIPEMAILSEND") != null) {
            if ("TRUE".equalsIgnoreCase(params.get("SKIPEMAILSEND").toString())) {
                skipSend = true;
                logger.debug("send skipped");
            }
        }
        if (!skipSend) {

            сheckParams = sendReportByEmailInCreate(attachMap, params, emailText, eMail, sessionID, login, password);
        }
        logger.debug("end!!! send attach by email");
        result.putAll(сheckParams);
        logger.debug("end!!! dsSendDocumentsPackage");
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

    protected String generateEmailTextEx(String url, String hash, Map<String, Object> contrMap, String mailName, String login, String password) throws Exception {
        String project = this.getProjectName(login, password);
        String metadataURL = getMetadataURL(login, password, project);

        String fName = mailName;

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
            if ("1".equals(contrMap.get("GENDER").toString())) {
                contrMap.put("GENDERNAME", "Уважаемая");
            } else {
                contrMap.put("GENDERNAME", "Уважаемый");
            }
            String fio = contrMap.get("FIRSTNAME").toString();
            if (contrMap.get("MIDDLENAME") != null) {
                fio = fio + " " + contrMap.get("MIDDLENAME").toString();
            }
            contrMap.put("FIO", fio);
            contrMap.put("URL", getURLWithParam(url, "hash", hash));
            String urlReject = getRejectURLFromURL(url);
            contrMap.put("URLREJECT", getURLWithParam(urlReject, "hash", hash));

            StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
            String contrId = getStringParam(contrMap.get("CONTRID"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");

            String nowDataTyme = sdf.format(new Date());
            StringBuilder sbParam = new StringBuilder();
            sbParam.append(nowDataTyme);
            sbParam.append(divider);
            sbParam.append(contrId);
            String baseParam = base64Encode(scu.encrypt(sbParam.toString()));
            String goodParam = base64Encode(scu.encrypt(sbParam.toString() + divider + "good"));
            String normParam = base64Encode(scu.encrypt(sbParam.toString() + divider + "norm"));
            String badParam = base64Encode(scu.encrypt(sbParam.toString() + divider + "bad"));

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
                                if ("URLGOOD".equalsIgnoreCase(sysName)) {
                                    contrMap.put(sysName, getURLWithParam(getStringParam(coreSetting.get("SETTINGVALUE")), "hash", goodParam));
                                } else if ("URLNORM".equalsIgnoreCase(sysName)) {
                                    contrMap.put(sysName, getURLWithParam(getStringParam(coreSetting.get("SETTINGVALUE")), "hash", normParam));
                                } else if ("URLBAD".equalsIgnoreCase(sysName)) {
                                    contrMap.put(sysName, getURLWithParam(getStringParam(coreSetting.get("SETTINGVALUE")), "hash", badParam));
                                } else {
                                    contrMap.put(sysName, getStringParam(coreSetting.get("SETTINGVALUE")));
                                }
                            }
                        }
                    }
                }
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

    protected Map<String, Object> sendReportByEmailInCreate(Map<String, String> attachmentMap, Map<String, Object> contrMap, String emailText, String email, String sessionId, String login, String password) throws Exception {
        String contrNum = contrMap.get("CONTRNUMBER").toString();
        Map<String, Object> sendParams = new HashMap<String, Object>();
        sendParams.put("SMTPSubject", "Договор страхования №" + contrNum);
        sendParams.put("SMTPMESSAGE", "В приложении пакет электронных документов для договора страхования №" + contrNum + ".");
        sendParams.put("SMTPReceipt", email);
        Map<String, Object> sendRes = null;
        if (isAllEmailValid(email)) {
            sendParams.put("ATTACHMENTMAP", attachmentMap);
            logger.debug("sendParams = " + sendParams.toString());
            sendParams.put("HTMLTEXT", emailText);
            userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Попытка отправки пакета документов", "Договор страхования №" + contrNum, "", email, "", "", login, password);

            try {
                boolean isError = false;
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
                                    userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Отправка пакета документов завершилась неудачей", "Договор страхования №" + contrNum, "", email, "", "", login, password);
                                    logger.debug("mailSendFail");
                                }
                            }
                        }
                    }
                }
                if (!isError) {
                    userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Почта отправлена", "Договор страхования №" + contrNum, "", email, "", "", login, password);
                    logger.debug("mailSendSuccess");
                }
            } catch (Exception e) {
                logger.debug("mailSendException: ", e);
                userLogActionCreateEx(sessionId, getStringParam(contrMap.get("CONTRID")), "Почта не отправлена", "Договор страхования №" + contrNum, "", email, "", "", login, password);
            }
        }
        return sendRes;
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
    }

}
