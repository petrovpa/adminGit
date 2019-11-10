/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomBaseFacade.roundSum;
import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularMortgageContractCustomFacade.calcMortgagePremValue;
import com.bivgroup.services.bivsberposws.system.Constants;
import com.bivgroup.services.bivsberposws.system.SmsSender;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;

import org.apache.log4j.Logger; // import java.util.logging.Logger
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author averichevsm
 */
@BOName("AngularContractCustom")
public class AngularContractCustomFacade extends AngularContractCustomBaseFacade {

    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    private static final String CRMWS_SERVICE_NAME = Constants.CRMWS;
    private static final String PROJECT_PARAM_NAME = "project";
    private static final String ADMINWS_SERVICE_NAME = Constants.ADMINWS;
    private static final String INSPRODUCTWS_SERVICE_NAME = Constants.INSPRODUCTWS;
    private static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;
    private static final String SIGNBIVSBERPOSWS_SERVICE_NAME = Constants.SIGNBIVSBERPOSWS;
    //private static final String SIGNBIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS; // !только для отладки!
    private static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    private static final String INSPRODUCTWSWS_SERVICE_NAME = Constants.INSPRODUCTWS;
    private static final String BIVPOSWS_SERVICE_NAME = Constants.BIVPOSWS;
    private static final String WEBSMSWS_SERVICE_NAME = Constants.SIGNWEBSMSWS;
    private static final String REFWS_SERVICE_NAME = Constants.REFWS;
    private static final String COREWS_SERVICE_NAME = Constants.COREWS;

    public static final String ORDERID_PARAM_NAME = "ORDERID";
    public static final String ORDERNUMBER_PARAM_NAME = "ORDERNUMBER";

    private String smsText = "";
    private String smsUser = "";
    private String smsPwd = "";
    private String smsFrom = "";

    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AngularContractCustomFacade.class);

    protected void getSmsInit() {
        Config config = Config.getConfig(SERVICE_NAME);
        this.smsText = config.getParam("SMSTEXT", "Уважаемый%20клиент,%20Ваш%20пароль%20для%20подтверждения%20введенных%20данных:");
        this.smsUser = config.getParam("SMSUSER", "sberinsur");
        this.smsPwd = config.getParam("SMSPWD", "KD9zVoeR123");
        this.smsFrom = config.getParam("SMSFROM", "SberbankIns");
    }

    @WsMethod()
    public Map<String, Object> dsHabContractBrowseListParamEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        if (isB2BMode(params)) {
            Map<String, Object> getB2BContractData = new HashMap<String, Object>();
            //getB2BContractData.put("orderNum", "");
            getB2BContractData.putAll(params);
            getB2BContractData.put("contrId", params.get("CONTRID"));
            Map<String, Object> getB2BContractParams = new HashMap<String, Object>();
            getB2BContractParams.put("CONTRMAP", getB2BContractData);
            getB2BContractParams.put(RETURN_AS_HASH_MAP, true);
            getB2BContractParams.put(USEB2B_PARAM_NAME, params.get(USEB2B_PARAM_NAME));
            Map<String, Object> contract = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsContractBrowseEx", getB2BContractParams, login, password);
            result = (Map<String, Object>) contract.get("CONTRMAP");
        } else {
            result = this.selectQuery("dsHabContractBrowseListParamEx", "dsHabContractBrowseListParamExCount", params);
        }
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsContractBrowseListForIntegration(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String contrIdList = params.get("CONTRIDLIST").toString();
        Map<String, Object> result = null;
        result = this.selectQuery("dsContractBrowseListForIntegration", "dsContractBrowseListForIntegrationCount", params);
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsContractToPaymentState(Map<String, Object> params) throws Exception {
        return contractToPaymentState(params);
    }

    @WsMethod()
    public Map<String, Object> dsContractToPaidState(Map<String, Object> params) throws Exception {
        return contractToPaidState(params);
    }

    @WsMethod()
    public Map<String, Object> dsContractReject(Map<String, Object> params) throws Exception {
        return сontractReject(params);
    }

    // отправка письма без печати док-ов и без отпавки СМС
    @WsMethod()
    public Map<String, Object> dsCallSendEmail(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        logger.debug("dsCallSendEmail start");
        String guid = null;
        String action = "";
        String url = null;
        String hash = null;
        Map<String, Object> contrQParam = new HashMap<String, Object>();
        Map<String, Object> dataMap = (Map<String, Object>) params.get("DATAMAP");
        if (dataMap != null) {
            if (dataMap.get("hash") != null) {
                hash = dataMap.get("hash").toString();
                guid = base64Decode(hash);
            }
            if (dataMap.get("url") != null) {
                url = dataMap.get("url").toString();
            }
            if (dataMap.get("action") != null) {
                action = dataMap.get("action").toString();
            }
            contrQParam.put("EXTERNALID", guid);
        }
        // Вызов из сервиса
        if ((guid == null) && (params.get("CONTRID") != null)) {
            contrQParam.put("CONTRID", params.get("CONTRID"));
        } else if (guid == null) {
            throw new Exception("Service need required params DATAMAP for calling from angular or CONTRID for calling from java");
        }
        Map<String, Object> result = new HashMap<String, Object>();

        boolean isB2BModeFlag = isB2BMode(params);

        Map<String, Object> contrQRes = selectQueryHabContractBrowseListParamEx(contrQParam, isB2BModeFlag, login, password);
        contrQRes.put(USEB2B_PARAM_NAME, params.get(USEB2B_PARAM_NAME));
        String sessionId = getStringParam(contrQRes.get("sessionId"));
        logger.debug("CONTRID: " + contrQRes.get("CONTRID").toString());
        if (contrQRes.get("EXTERNALID") != null) {
            hash = base64Encode(contrQRes.get("EXTERNALID").toString());
        } else {
            throw new Exception("Contract attribute EXTERNALID is empty, but required.");
        }

        if ((url == null) || ((url != null) && (url.isEmpty()))) {
            // загрузка url из конфига продукта.
            //Map<String, Object> productConfigRes = this.callService(INSPRODUCTWSWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", productConfQueryParams, login, password);
            Map<String, Object> productConfigRes = getProductDefaultValueByProdConfId(contrQRes.get("PRODCONFID"), isB2BModeFlag, login, password);
            if ((productConfigRes != null) && (productConfigRes.get("PRODUCTURL") != null)) {
                url = (String) productConfigRes.get("PRODUCTURL");
            } else {
                url = "";
            }
        }
        logger.debug("url " + url);

        Map<String, Object> printQRes = null;
        getIsurerEmail(contrQRes, login, password);
        String email = contrQRes.get("PersonalEmail").toString();

        result.put("action", action);
        logger.debug("action: " + action);

        if ((email != null) && (!email.isEmpty())) {
            String htmlMailType = "HTMLMAILPATHSUCCES";
            if (params.get("HTMLMAILTYPE") != null) {
                htmlMailType = params.get("HTMLMAILTYPE").toString();
            }

            String emailText = generateEmailTextEx(url, hash, contrQRes, htmlMailType, login, password);
            logger.debug("email send: " + email);
            //logger.debug("to sambucusfehu ");

            //для теста все себе
            // email = "sambucusfehu@gmail.com";
            //
            Map<String, Object> sendRes = sendReportByEmailInCreate(printQRes, contrQRes, emailText, email, sessionId, isB2BModeFlag, login, password);
            result.put("EMAILRES", sendRes);
        }

        logger.debug("dsCallSendEmail finish");

        return result;
    }

    @WsMethod()
    public Map<String, Object> dsCallPringAndSendEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        boolean isB2BModeFlag = isB2BMode(params);
        logger.debug("dsCallPringAndSendEx start");
        String guid = null;
        String action = "";
        String url = null;
        String hash = null;
        Map<String, Object> contrQParam = new HashMap<String, Object>();
        Map<String, Object> dataMap = (Map<String, Object>) params.get("DATAMAP");
        if (dataMap != null) {
            if (dataMap.get("hash") != null) {
                hash = dataMap.get("hash").toString();
                guid = base64Decode(hash);
            }
            if (dataMap.get("url") != null) {
                url = dataMap.get("url").toString();
            }
            if (dataMap.get("action") != null) {
                action = dataMap.get("action").toString();
            }
            contrQParam.put("EXTERNALID", guid);
            Object useB2BDataMapValue = dataMap.get(USEB2B_PARAM_NAME);
            if (useB2BDataMapValue != null) {
                params.put(USEB2B_PARAM_NAME, useB2BDataMapValue);
            }
        }
        if (action.isEmpty()) {

            if (params.get("action") != null) {
                action = params.get("action").toString();
            }
        }

        // Вызов из сервиса
        if (guid == null) {
            if (params.get("CONTRID") != null) {
                contrQParam.put("CONTRID", params.get("CONTRID"));
            } else if (dataMap.get("contrId") != null) {
                contrQParam.put("CONTRID", dataMap.get("contrId"));
            } else {
                throw new Exception("Service need required params DATAMAP for calling from angular or CONTRID for calling from java");
            }
        }

        Map<String, Object> contrQRes = selectQueryHabContractBrowseListParamEx(contrQParam, isB2BModeFlag, login, password);
        contrQRes.put(USEB2B_PARAM_NAME, params.get(USEB2B_PARAM_NAME));
        String sessionId = getStringParam(contrQRes.get("sessionId"));
        Map<String, Object> printQParam = new HashMap<String, Object>();
        printQParam.put("CONTRID", contrQRes.get("CONTRID"));
        printQParam.put(USEB2B_PARAM_NAME, params.get(USEB2B_PARAM_NAME));
        //
        logger.debug("CONTRID: " + contrQRes.get("CONTRID").toString());
        if (contrQRes.get("EXTERNALID") != null) {
            hash = base64Encode(contrQRes.get("EXTERNALID").toString());
        } else {
            throw new Exception("Contract attribute EXTERNALID is empty, but required.");
        }

        if ((url == null) || ((url != null) && (url.isEmpty()))) {
            // загрузка url из конфига продукта.
            // Map<String, Object> productConfigRes = this.callService(INSPRODUCTWSWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", productConfQueryParams, login, password);
            Map<String, Object> productConfigRes = getProductDefaultValueByProdConfId(contrQRes.get("PRODCONFID"), isB2BModeFlag, login, password);
            if ((productConfigRes != null) && (productConfigRes.get("PRODUCTURL") != null)) {
                url = (String) productConfigRes.get("PRODUCTURL");
            } else {
                url = "";
            }
        }
        logger.debug("url " + url);
        boolean isB2BPrintAndSendAllDocument = false;
// выключить для печати в старом режиме
        if (isB2BModeFlag) {
            if (!action.equalsIgnoreCase("sendSms")) {
                // если действие - отправка почты - или пусто (полный процесс.)
                // то печатаем, отправляем по почте. ограничили по продуктам. т.к. к такой печати подготовлены только с посадочных страниц продукты.
                Object prodConfId = contrQRes.get("PRODCONFID");
                Object b2bProdConfID = convertValue(prodConfId, getProdConfIDConvertRules(), Direction.TO_SAVE);
                if (b2bProdConfID != null) {
                    //  int b2bProdConfIdint = Integer.valueOf(b2bProdConfID.toString()).intValue();
                    // пока только ипотека не переделана под печать b2bшного полиса.
                    // тест печати полиса b2bшного для ипотеки
                    //   if (b2bProdConfIdint != 1011) {
                    contrQRes.put("PRODCONFID", b2bProdConfID);
                    contrQRes.put("INSUREREMAIL", contrQRes.get("PersonalEmail"));
                    contrQRes.put("PRODUCTURL", url);
                    contrQRes.put("ONLYPRINT", params.get("ONLYPRINT"));
                    contrQRes.put("NEEDREPRINT", params.get("NEEDREPRINT"));
//                    if (("BOX_RIGHT_CHOICE".equalsIgnoreCase(getStringParam(contrQRes.get("PRODUCTSYSNAME"))))
//                            || ("BOX_SEAT_BELT".equalsIgnoreCase(getStringParam(contrQRes.get("PRODUCTSYSNAME"))))) {
                    if (("RIGHT_CHOICE_RTBOX".equalsIgnoreCase(getStringParam(contrQRes.get("PRODUCTSYSNAME"))))
                            || ("SBELT_RTBOX".equalsIgnoreCase(getStringParam(contrQRes.get("PRODUCTSYSNAME"))))) {
                        if (contrQRes.get("CONTREXTMAP") != null) {
                            Map<String, Object> contrExtMap = (Map<String, Object>) contrQRes.get("CONTREXTMAP");
                            contrQRes.put("REPLEVEL", 10L);
                            contrQRes.put("ATTACHINFO", "");
                            if (!"1".equals(getStringParam(contrExtMap.get("insurerIsInsured"))) && (contrExtMap.get("insurerIsInsured") != null)) {
                                contrQRes.put("REPLEVEL", 1);
                                contrQRes.put("ATTACHINFO", "К письму прилагается \"Соглашение о внесении изменений в Страховой Полис\", подписанное<br> усиленной электронной подписью.<br>");
                            } else if (!"1".equals(getStringParam(contrExtMap.get("insurerIsBeneficiary"))) && (contrExtMap.get("insurerIsBeneficiary") != null)) {
                                contrQRes.put("REPLEVEL", 2);
                                contrQRes.put("ATTACHINFO", "К письму прилагается \"Соглашение о внесении изменений в Страховой Полис\", подписанное<br> усиленной электронной подписью.<br>");
                            }
                        }
                    }

                    contrQRes.put("HASH", hash);
                    contrQRes.put("SESSIONID", sessionId);
                    result = this.callService(SIGNB2BPOSWS_SERVICE_NAME, "dsB2BPrintAndSendAllDocument", contrQRes, login, password);
                    contrQRes.put("PRODCONFID", prodConfId);
                    isB2BPrintAndSendAllDocument = true;
                    // если действие полное, то еще надо отправить смс.
                    if (action.isEmpty()) {
                        action = "sendSms";
                    }
                    //   }
                }
            }
        }
        boolean onlyPrint = false;
        boolean needReprint = false;
        boolean needSendCopy = false;
        if (params.get("ONLYPRINT") != null) {
            if ("TRUE".equalsIgnoreCase(params.get("ONLYPRINT").toString())) {
                onlyPrint = true;
            }
        }
        if (params.get("NEEDREPRINT") != null) {
            if ("TRUE".equalsIgnoreCase(params.get("NEEDREPRINT").toString())) {
                needReprint = true;
            }
        }
        if (params.get("NEEDSENDCOPY") != null) {
            if ("TRUE".equalsIgnoreCase(params.get("NEEDSENDCOPY").toString())) {
                needSendCopy = true;
            }
        }
        Map<String, Object> printQRes = null;
        if ("".equals(action) || "sendEmail".equals(action) || "onlyPrint".equals(action) || onlyPrint) {

            if (needReprint && !isB2BPrintAndSendAllDocument) {
                // пытаемся получить файл
                if (isB2BModeFlag) {
                    logger.debug("remove attach doc for b2b contr: " + params.get("CONTRID").toString());
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
                } else {
                    logger.debug("remove attach doc for ins contr: " + params.get("CONTRID").toString());
                    Map<String, Object> getMap = new HashMap<String, Object>();
                    getMap.put("OBJID", params.get("CONTRID"));
                    Map<String, Object> getRes = this.callService(Constants.INSPOSWS, "dsContract_BinaryFile_BinaryFileBrowseListByParam", getMap, login, password);
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
                                        this.callService(Constants.INSPOSWS, "dsContract_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
                                    }
                                }

                            }
                        }
                    }
                }
            }
            // если не происходила печать и отсылка всех отчетов B2B
            if (!isB2BPrintAndSendAllDocument) {
                if ("1090".equalsIgnoreCase(contrQRes.get("PRODCONFID").toString())) {
                    logger.debug("mort print docs: " + printQParam.toString());
                    printQRes = this.callService(SIGNBIVSBERPOSWS_SERVICE_NAME, "dsMortgagePrintReport", printQParam, login, password);

                } else if ("1070".equalsIgnoreCase(contrQRes.get("PRODCONFID").toString())) {
                    logger.debug("VZR print docs: " + printQParam.toString());
                    printQRes = this.callService(SIGNBIVSBERPOSWS_SERVICE_NAME, "dsSberVzrPrintReport", printQParam, login, password);

                } else {
                    logger.debug("print docs: " + printQParam.toString());
                    printQRes = this.callService(SIGNBIVSBERPOSWS_SERVICE_NAME, "dsSberHabPrintReport", printQParam, login, password);
                }
                logger.debug("printing result: " + printQRes.toString());
            }
        }
        getIsurerEmail(contrQRes, login, password);
        String email = contrQRes.get("PersonalEmail").toString();
        String phone = contrQRes.get("MobilePhone").toString();
        result.put("action", action);
        logger.debug("action: " + action);
        if ("".equals(action) || "sendSms".equals(action)) {
            if ((phone != null) && (!phone.isEmpty())) {
                logger.debug("sms send: " + phone);
                Map<String, Object> sendRes = sendSms(phone, contrQRes, sessionId, login, password);
                result.put("SMSRES", sendRes);
            }
        }

        if ("".equals(action) || "sendEmail".equals(action) && !isB2BPrintAndSendAllDocument) {
            if ((email != null) && (!email.isEmpty())) {
                if (isB2BModeFlag) {
                    contrQRes.put("USEB2B", "TRUE");
                } else {
                    contrQRes.put("USEB2B", "FALSE");
                }

                if ("1090".equalsIgnoreCase(contrQRes.get("PRODCONFID").toString())) {
                    // при любой отправке почты по ипотеке отправлять копию
                    needSendCopy = true;
                }
                String emailText = generateEmailText(url, hash, contrQRes, login, password);
                logger.debug("email send: " + email);
                //email = "sambucusfehu@gmail.com";
                //logger.debug("4debug: email is " + email);
                Map<String, Object> sendRes = sendReportByEmailInCreate(printQRes, contrQRes, emailText, email, sessionId, isB2BModeFlag, login, password);
                if (needSendCopy) {
                    Config config = Config.getConfig(SERVICE_NAME);
                    logger.debug("needSendCopy");

                    String addressListStr = config.getParam("SENDCOPYADDRESS", "");
                    if (!addressListStr.isEmpty()) {
                        emailText = generateEmailTextEx(url, hash, contrQRes, "HTMLMAILPATHCOPY", login, password);
                        logger.debug("needSendCopy to: " + addressListStr);
                        if (printQRes.get(RESULT) != null) {
                            Map<String, Object> attachList = (Map<String, Object>) printQRes.get(RESULT);
                            if (attachList.get("REPORTDATALIST") != null) {
                                attachList.remove("REPORTDATALIST");
                            }
                            printQRes = attachList;
                        }
                        HashMap<String, Object> newFilePath = new HashMap<String, Object>();
                        for (Entry<String, Object> entry : printQRes.entrySet()) {
                            String path = entry.getValue().toString();
                            if (path.indexOf("INSURANCE\\REPORTS\\") < 0) {
                                newFilePath.put(entry.getKey(), entry.getValue());
                            }
                        }

                        //String[] emailList = addressListStr.split(",");
//                        for (String email : emailList) {
                        sendReportByEmailInCreate(newFilePath, contrQRes, emailText, addressListStr, sessionId, isB2BModeFlag, login, password);
                        //                      }
                    }
                }

                result.put("EMAILRES", sendRes);
            }
        }
        logger.debug("dsCallPringAndSendEx finish");
        return result;
    }

    // уже есть в AngularContractCustomBaseFacade
    /*
     public static String base64Decode(String input) {
     Base64 decoder = new Base64(true);
     return bytesToString(decoder.decode(input));
     }

     public static String base64Encode(String input) {
     Base64 encoder = new Base64(true);
     String result = bytesToString(encoder.encode(stringToBytes(input)));
     return result.substring(0, result.length() - 2);
     }

     public static byte[] stringToBytes(String value) {
     byte[] result = null;
     try {
     result = value.getBytes("UTF-8");
     } catch (UnsupportedEncodingException ex) {
     }
     return result;
     }
     */
    public static String bytesToString(byte[] value) {
        String result = "";
        try {
            result = new String(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return result;
    }

    /**
     * метод, выберет договор по хешу. сделан, для получения данных договора на
     * форму..
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> dsContractBrowseEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params.get("CONTRMAP") != null) {
            Map<String, Object> contrMapIn = (Map<String, Object>) params.get("CONTRMAP");
            if ((params.get(USEB2B_PARAM_NAME) == null) && (contrMapIn.get(USEB2B_PARAM_NAME) != null)) {
                params.put(USEB2B_PARAM_NAME, contrMapIn.get(USEB2B_PARAM_NAME));
            }
            String login = params.get(WsConstants.LOGIN).toString();
            String password = params.get(WsConstants.PASSWORD).toString();
            Long contrId = null;
            Map<String, Object> browseParams = new HashMap<String, Object>();
            if (contrMapIn.get("orderNum") != null) {
                String orderGuid = base64Decode(contrMapIn.get("orderNum").toString());
                browseParams.put("EXTERNALID", orderGuid);
            } else // в шлюзе с ангуляром закрыта возможность запросить договор по ИД.
            if (contrMapIn.get("contrId") != null) {
                browseParams.put("CONTRID", contrMapIn.get("contrId"));
                contrId = Long.valueOf(contrMapIn.get("contrId").toString());
            }

            // 
            if (isB2BMode(params)) {

                Map<String, Object> contractParams = new HashMap<String, Object>();
                contractParams.putAll(browseParams);
                contractParams.put("PRODCONFID", convertValue(contrMapIn.get("prodConfId"), getProdConfIDConvertRules(), Direction.TO_LOAD));
                contractParams.put(RETURN_AS_HASH_MAP, true);
                contractParams.put("TARGETDATEFORMAT", "DATE");

                Map<String, Object> browsedContract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", contractParams, login, password);

                logger.debug("browsedContract (dsB2BContrLoad):");
                // logger.debug(browsedContract);

                Map<String, Object> contract = prepareB2BParams(browsedContract, Direction.TO_LOAD);

                logger.debug("contract (prepareB2BParams):");
                logger.debug(contract);

                result.put("CONTRMAP", contract);
                return result;
            }

            //browseParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> browseParamsRes = this.selectQuery("dsHabContractBrowseListParamEx", "dsHabContractBrowseListParamExCount", browseParams);
            if (browseParamsRes.get(RESULT) != null) {
                List<Map<String, Object>> contrList = (List<Map<String, Object>>) browseParamsRes.get(RESULT);
                if (!contrList.isEmpty()) {
                    Map<String, Object> contrMap = (Map<String, Object>) contrList.get(0);
                    if (contrMap.get("INSUREDID") != null) {
                        Map<String, Object> qParam = new HashMap<String, Object>();
                        qParam.put("PARTICIPANTID", contrMap.get("INSUREDID"));
                        Map<String, Object> qRes = this.callService(CRMWS_SERVICE_NAME, "contactGetListByParticipantId", qParam, login, password);
                        List<Map<String, Object>> contactList = WsUtils.getListFromResultMap(qRes);
                        for (Map<String, Object> contact : contactList) {
                            if ("PersonalEmail".equalsIgnoreCase(contact.get("CONTACTTYPESYSNAME").toString())) {
                                contrMap.put("INSEMAIL", contact.get("VALUE"));
                            }
                            if ("MobilePhone".equalsIgnoreCase(contact.get("CONTACTTYPESYSNAME").toString())) {
                                contrMap.put("INSPHONE", contact.get("VALUE"));
                            }
                        }
                    }
                    if (contrMap.get("CONTRNODEID") != null) {
                        Long contrNodeId = (Long) contrMap.get("CONTRNODEID");
                        // получение план графика
                        Map<String, Object> planParams = new HashMap<String, Object>();
                        planParams.put("CONTRNODEID", contrNodeId);
                        //planParams.put("ReturnAsHashMap", "TRUE");
                        Map<String, Object> qPlanRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentFactBrowseListByParamEx", planParams, login, password);
                        contrMap.put("PAYMENTLIST", WsUtils.getListFromResultMap(qPlanRes));
                    }
                    // получение графика оплаты
                    Map<String, Object> factParams = new HashMap<String, Object>();
                    contrId = Long.valueOf(contrMap.get("CONTRID").toString());
                    factParams.put("CONTRID", contrId);
                    Map<String, Object> qFactRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentBrowseListByParam", factParams, login, password);
                    contrMap.put("PAYMENTSCHEDULELIST", qFactRes.get(RESULT));

                    result.put("CONTRMAP", contrMap);
                    // contrId = Long.valueOf(contrMap.get("CONTRID").toString());
                }
            }

            Map<String, Object> contrObjParam = new HashMap<String, Object>();
            contrObjParam.put("CONTRID", contrId);
            Map<String, Object> contrObjRes = this.selectQuery("dsContractObjBrowseListByParamEx", "dsContractObjBrowseListByParamExCount", contrObjParam);
            Map<String, Object> contrRiskRes = this.callService(INSPOSWS_SERVICE_NAME, "dsContractRiskBrowseListByContrIdJoinProdRisk", contrObjParam, login, password);
            //this.selectQuery("dsContractRiskBrowseListByParam", "dsContractRiskBrowseListByParamCount", contrObjParam);
            List<Map<String, Object>> riskList = WsUtils.getListFromResultMap(contrRiskRes);
            if (contrObjRes != null) {
                if (contrObjRes.get(RESULT) != null) {
                    List<Map<String, Object>> contrObjList = (List<Map<String, Object>>) contrObjRes.get(RESULT);
                    if (!contrObjList.isEmpty()) {
                        for (Map<String, Object> contrObj : contrObjList) {
                            Long contrObjId = getLongParam(contrObj.get("CONTROBJID"));
                            List<Map<String, Object>> contrObjRiskList = new ArrayList<Map<String, Object>>();
                            for (Map<String, Object> risk : riskList) {
                                Long riskContrObjId = getLongParam(risk.get("CONTROBJID"));
                                if (riskContrObjId == null) {
                                    // если ContrObj пустой, то риск вставляем в список первого дома. для загрузки на форму
                                    String objName = getStringParam(contrObj.get("NAME"));
                                    if (!objName.isEmpty()) {
                                        if ("firstHouse".equalsIgnoreCase(objName)) {
                                            contrObjRiskList.add(risk);
                                        }
                                    }

                                } else if (contrObjId.compareTo(riskContrObjId) == 0) {
                                    contrObjRiskList.add(risk);
                                }
                            }
                            contrObj.put("RISKLIST", contrObjRiskList);
                        }
                    }

                    result.put("CONTROBJLIST", contrObjList);
                }
            }

            if (contrMapIn.get("orderId") != null) {
                result.put("orderId", contrMapIn.get("orderId"));
                result.put("orderGuid", contrMapIn.get("orderGuid"));
                result.put("payRes", contrMapIn.get("payRes"));
                if (checkPayment(result, login, password)) {
                    /*Map<String, Object> printParams = new HashMap<String, Object>();
                     printParams.put("CONTRID", contrId);
                     Map<String, Object> printRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsSberHabPrintReport", printParams, login, password);

                     String email = getIsurerEmail((Map<String, Object>) result.get("CONTRMAP"), login, password);
                     if (!email.isEmpty()) {

                     Map<String, Object> sendRes = sendReportByEmail(printRes, result, email, login, password);
                     }
                     saveFactPay(result, login, password);*/
                }
            }

        } else {
            result.put("Status", "emptyInputMap");
        }
        return result;
    }

    /**
     * метод, создаст договор, со всеми необходимыми сущностями.
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> dsContractCreateEx(Map<String, Object> params) throws Exception {

        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        String methodName = "dsContractCreateEx";
        logger.debug("Вызван метод " + methodName + " с параметрами:\n\n" + params.toString() + "\n");

        Map<String, Object> result = new HashMap<String, Object>();
        if (params.get("CONTRMAP") != null) {
            Map<String, Object> contrMapIn = (Map<String, Object>) params.get("CONTRMAP");
            if ((params.get(USEB2B_PARAM_NAME) == null) && (contrMapIn.get(USEB2B_PARAM_NAME) != null)) {
                params.put(USEB2B_PARAM_NAME, contrMapIn.get(USEB2B_PARAM_NAME));
            }
            Map<String, String> contrEmptyRequiredFields = new HashMap<String, String>();
            String[][] curReqField = {};
            Object prodVerIdObj = contrMapIn.get("prodVerId");
            //if (prodVerIdObj == null) {
            //    prodVerIdObj = contrMapIn.get("PRODVERID");
            //}
            if (prodVerIdObj != null) {
                String prodVerIdStr = prodVerIdObj.toString();
                if ("1050".equals(prodVerIdStr)) {
                    curReqField = hibRequiredFields;
                } else if ("1060".equals(prodVerIdStr)) {
                    curReqField = cibRequiredFields;
                } else if ("1070".equals(prodVerIdStr)) {
                    curReqField = vzrRequiredFields;
                } else if ("2100".equals(prodVerIdStr)) {
                    curReqField = hibRequiredFields;
                }
            }

            if (checkRequiredFields(contrMapIn, contrEmptyRequiredFields, curReqField)) {

                if (isB2BMode(params)) {
                    String login = params.get(WsConstants.LOGIN).toString();
                    String password = params.get(WsConstants.PASSWORD).toString();
                    Map<String, Object> createdContract = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsContractCreateInB2BModeEx", contrMapIn, login, password);
                    return createdContract;
                    //return null; //!только для отладки!
                }

                Map<String, Object> contrMap = remapFromGate(contrMapIn);
                if (!contrMap.isEmpty()) {
                    String login = params.get(WsConstants.LOGIN).toString();
                    String password = params.get(WsConstants.PASSWORD).toString();
                    //получаем конфигурацию продукта
                    Long prodVerId = getLongParam(contrMap.get("PRODVERID"));
                    Long prodConfId = getLongParam(contrMap.get("PRODCONFID"));
                    //создаем contrNode
                    Long contrNodeId = сontrNodeСreate(0L, 0L, login, password);
                    contrMap.put("CONTRNODEID", contrNodeId);
                    Date docDate = new Date();
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(docDate);
                    gc.add(Calendar.DATE, 14);
                    contrMap.put("STARTDATE", gc.getTime());
                    gc.add(Calendar.YEAR, 1);
                    gc.add(Calendar.DATE, -1);
                    contrMap.put("FINISHDATE", gc.getTime());

                    contrMap.put("DOCUMENTDATE", docDate);
                    contrMap.put("DECLDATE", docDate);
                    //создаем лицо
                    //создаем персону в CRM
                    Map<String, Object> personMap = participantCreate(contrMap, login, password);
                    contrMap.put("PARTICIPANTID", personMap.get("PARTICIPANTID"));
                    contrMap.put("PERSONID", personMap.get("PERSONID"));
                    //создаем договор
                    Map<String, Object> userInfo = findDepByLogin(login, password);
                    Long sellerId = getSellerId(userInfo, login, password);
                    contrMap.put("SELLERID", sellerId);
                    contrMap.put("ORGSTRUCTID", userInfo.get("DEPARTMENTID"));
                    contrMap.put("SELFORGSTRUCTID", userInfo.get("DEPARTMENTID"));
                    //SELLERID - сделать продавца для сайта? или искать его по логину
                    Long contrId = contrCreate(contrMap, login, password);
                    contrMap.put("CONTRID", contrId);

                    //устанавливаем текущую версию договора в contrNode
                    setRightsOnContr(userInfo, contrId, login, password);

                    сontrNodeUpdate(contrNodeId, contrId, login, password);
                    // генерим номер договора
                    //String contrNum = contrId.toString();
                    //NumberFormat nf = new DecimalFormat("00000000");
                    //contrNum = nf.format(contrId);
                    String contrNum = generateContrNum(prodConfId, login, password);
                    BigDecimal promoValue = BigDecimal.ONE;
                    promoValue = getPromoValueByCodeAndProdVerId(contrMapIn.get("promoCode"), prodVerId.longValue(), login, password);
                    contrMap.put("promoCode", contrMapIn.get("promoCode"));
                    contrMap.put("promoValue", promoValue);

                    if (contrNum.indexOf("/") >= 0) {
                        String[] cnArr = contrNum.split("/");
                        contrMap.put("CONTRPOLSER", cnArr[0] + cnArr[1]);
                        contrMap.put("CONTRPOLNUM", cnArr[2]);
                    } else {
                        contrMap.put("CONTRPOLSER", contrNum.substring(0, 5));
                        contrMap.put("CONTRPOLNUM", contrNum.substring(5));
                    }
                    contrMap.put("CONTRNUMBER", contrNum);

                    contrMap.put("ORGSTRUCT", userInfo.get("DEPARTMENTID"));
                    // Повторно считаем сумм по программе с трахования.
                    Map<String, Object> qParam = new HashMap<String, Object>();
                    qParam.put("PRODCONFID", prodConfId);
                    qParam.put("PRODVERID", prodVerId);
                    qParam.put("PRODPROGID", contrMap.get("PRODPROGID"));
                    qParam.put("ReturnAsHashMap", "TRUE");
                    Map<String, Object> prodProg = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductProgramBrowseListByParamWithExtProp", qParam, login, password);

                    contrMap.put("PRODPROGID", prodProg.get("PRODPROGID"));
                    contrMap.put("INSAMVALUE", prodProg.get("insAmValue"));

                    if (prodProg.get("premium") != null) {
                        contrMap.put("PREMVALUE", roundSum(getBigDecimalParam(prodProg.get("premium")).multiply(promoValue).doubleValue()));
                    }
                    // сохраняем номер договора
                    сontrUpdate(contrMap, login, password);

                    contrExtCreate(contrMap, login, password);
                    readContrExtId(contrMap, login, password);
                    //получаем версию справочника расширенных артибутов договора
                    //создаем расширенные атрибуты договора.
                    //сохраняем доп атрибуты персоны если они есть
                    //сохраняем ид персоны в договор.
                    //сохраняем объект недвижимости
                    Map<String, Object> objRes = insObjSave(contrMap, login, password);
                    contrMap.put("OBJRES", objRes);
                    contrMap.put("HASH", base64Encode(contrMap.get("GUID").toString()));

                    /*   Map<String, Object> printParams = new HashMap<String, Object>();
                     printParams.put("CONTRID", contrId);
                     Map<String, Object> printRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsSberHabPrintReport", printParams, login, password);

                     String email = contrMap.get("CONTACTEMAIL").toString();
                     if (!email.isEmpty()) {
                     Map<String, Object> sendRes = sendReportByEmailInCreate(printRes, contrMap, email, login, password);
                     }
                     */
                    result.put(RESULT, contrMap);
                } else {
                    result.put("Status", "emptyInputMap");
                }
            } else {
                //не все обязательные поля заполнены. создавать договор нельзя.
                result.put("Status", "requiredFieldError");
                result.put("EmptyRequiredFields", contrEmptyRequiredFields);
            }
        } else {
            result.put("Status", "emptyInputMap");
        }

        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        logger.debug("Метод " + methodName + " выполнился за " + callTimer + " мс. и вернул результат:\n\n" + result.toString() + "\n");

        return result;
    }

    private Long findPersonByParams(Map<String, Object> contrMap, String login, String password) throws Exception {
        Long participantId = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("FIRSTNAME", contrMap.get("FIRSTNAME"));
        params.put("MIDDLENAME", contrMap.get("MIDDLENAME"));
        params.put("LASTNAME", contrMap.get("LASTNAME"));
        params.put("BIRTHDATE", contrMap.get("BIRTHDATE"));
        Map<String, Object> res = this.callService(CRMWS_SERVICE_NAME, "personGetListByParams", params, login, password);
        List<Map<String, Object>> exList = WsUtils.getListFromResultMap(res);
        if (exList != null) {
            if (!exList.isEmpty()) {
                if (exList.get(0).get("PARTICIPANTID") != null) {
                    participantId = getLongParam(exList.get(0).get("PARTICIPANTID"));
                }
            }
        }
        return participantId;
    }

    private Map<String, Object> participantCustomCreate(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        // пока продукты только для физ лиц. поэтому тут 0
        params.put("INSUREDTYPE", 0);
        params.put("CITIZENSHIP", contrMap.get("CITIZENSHIP"));
        //имя
        params.put("NAME", contrMap.get("FIRSTNAME"));
        //Отчество
        params.put("MIDDLENAME", contrMap.get("MIDDLENAME"));
        //фамилия
        params.put("SURNAME", contrMap.get("LASTNAME"));
        params.put("BIRTHDATE", contrMap.get("BIRTHDATE"));
        // 0 - male, 1 - female
        params.put("SEX", contrMap.get("SEX"));
        params.put("BIRTHPLACE", contrMap.get("BIRTHPLACE"));
        // документ - всегда пасспорт РФ
        params.put("DOCTYPESYSNAME", contrMap.get("DOCTYPESYSNAME"));//"PassportRF");
        params.put("DOCSERIES", contrMap.get("DOCSERIES"));
        params.put("DOCNUMBER", contrMap.get("DOCNUMBER"));
        params.put("ISSUEDATE", contrMap.get("ISSUEDATE"));
        params.put("ISSUEDBY", contrMap.get("ISSUEDBY"));
        params.put("ISSUERCODE", contrMap.get("ISSUERCODE"));
        params.put("CONTACTPHONEMOBILE", contrMap.get("CONTACTPHONEMOBILE"));
        params.put("CONTACTEMAIL", contrMap.get("CONTACTEMAIL"));

        params.put("ADDRESSDATA", contrMap.get("INSADDRESSDATA"));
        params.put("PROCESSADDRESSPOST", false);
        params.put("PROCESSDL", false);
        params.put("PROCESSREGDOC", false);
        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> result = this.callService(INSPOSWS_SERVICE_NAME, "dsParticipantCustomCreate", params, login, password);
        return result;
    }

    private Map<String, Object> participantCustomModify(Map<String, Object> contrMap, String login, String password) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Map<String, Object> insObjSave(Map<String, Object> contrMap, String login, String password) throws Exception {
        Long prodVerId = getLongParam(contrMap.get("PRODVERID"));
        Long contrPropertyId = null;
        String typeSysName = "";
        String typeName = "";
        if (!prodVerId.equals(1060L)) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ReturnAsHashMap", "TRUE");
            params.put("ADDRESSDATA", contrMap.get("OBJADDRESSDATA"));
            params.put("OBJTYPEID", contrMap.get("OBJTYPE"));
            if (contrMap.get("OBJTYPE") != null) {
                if (contrMap.get("OBJTYPE").toString().equalsIgnoreCase("1")) {
                    params.put("NAME", "Квартира");
                    typeSysName = "flat";
                    typeName = "Квартира";
                }
                if (contrMap.get("OBJTYPE").toString().equalsIgnoreCase("2")) {
                    params.put("NAME", "Дом");
                    typeSysName = "house";
                    typeName = "Дом";
                }
            }
            params.put("CONTRID", contrMap.get("CONTRID"));
            params.put("ROWSTATUS", 1L);

            Map<String, Object> cpRes = this.callService(INSPOSWS_SERVICE_NAME, "dsContractPropertySave", params, login, password);
            contrPropertyId = getLongParam(cpRes.get("CONTRPROPERTYID"));
        } else {
            typeSysName = "card";
            typeName = "Карта";
        }

        Long contrId = getLongParam(contrMap.get("CONTRID"));
        Long insObjNodeId = createNode(login, password);
        Map<String, Object> saveParams = new HashMap<String, Object>();

        saveParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        saveParams.put("INSOBJNODEID", insObjNodeId);
        saveParams.put("NAME", typeName);
        saveParams.put("OBJTYPESYSNAME", typeSysName);
        saveParams.put("CONTRPROPERTYID", contrPropertyId);

        String insObjServiceName = getInsObjServiceName(typeSysName);
        Map<String, Object> saveRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, insObjServiceName, saveParams, login, password);
        Long insObjId = getLongParam(saveRes.get("INSOBJID"));
        //очень надолго зависает
        //updateNodeActiveVersion(insObjNodeId, insObjId, login, password);

        // Создать объект страхования
        Map<String, Object> contrObjParams = new HashMap<String, Object>();
        contrObjParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        contrObjParams.put("CONTRID", contrId);
        contrObjParams.put("INSOBJID", insObjId);
        contrObjParams.put("PREMCURRENCYID", 1L);
        contrObjParams.put("INSAMCURRENCYID", 1L);
        contrObjParams.put("PREMVALUE", contrMap.get("PREMVALUE"));
        contrObjParams.put("INSAMVALUE", contrMap.get("INSAMVALUE"));
        Map<String, Object> contrObjRes = this.callService(Constants.INSPOSWS, "dsContractObjectCreate", contrObjParams, login, password);
        Long contrObjId = Long.valueOf(contrObjRes.get("CONTROBJID").toString());

        // сохранить риски по объекту
        //получаем риск конструктива, по объекту
        // риск конструктива у каждого объекта свой.
//        Map<String, Object> construct = getRiskFromObj(insObj, "construct");
//        saveContrRiskList(construct,contrObjId, contrId);
        // риски за исключением конструктива только у основного объекта, поэтому сохраняются отдельно
        // распределения лимита покрытия по объектам нет. поэтому у каждого объекта будет сохранен полный риск.
        Map<String, Object> prodRiskParams = new HashMap<String, Object>();
        prodRiskParams.put("PRODVERID", contrMap.get("PRODVERID"));
        Map<String, Object> prodRiskRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductRiskBrowseListByParam", prodRiskParams, login, password);
        List<Map<String, Object>> prodRiskList = (List<Map<String, Object>>) prodRiskRes.get(RESULT);
        CopyUtils.sortByStringFieldName(prodRiskList, "SYSNAME");

        for (Map<String, Object> risk : prodRiskList) {
            String sysName = getStringParam(risk.get("SYSNAME"));
            if (!sysName.isEmpty()) {
                Long prodRiskId = null;
                prodRiskId = getLongParam(risk.get("PRODRISKID"));
                saveContrRiskList(risk, contrObjId, contrId, prodRiskId, login, password);
            }
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRPROPERTYID", contrPropertyId);
        result.put("INSOBJNODEID", insObjNodeId);
        result.put("INSOBJID", insObjId);
        result.put("CONTROBJID", contrObjId);

        return result;
    }

    private Map<String, Object> findOrgStructByUser(Long userId, String login, String password) throws Exception {
        Map<String, Object> userInfoParam = new HashMap<String, Object>();
        userInfoParam.put("USERID", userId);
        userInfoParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(ADMINWS_SERVICE_NAME, "admparticipantbyid", userInfoParam, login, password);
        return qres;
    }

    private Map<String, Object> travelRemapFromGate(Map<String, Object> mapIn, String login, String password) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Map<String, Object> obj = (Map<String, Object>) mapIn.get("obj");
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("protocol", obj.get("protocol"));
        result.put("host", obj.get("host"));
        result.put("port", obj.get("port"));
        result.put("localTZOffset", obj.get("localTZOffset"));
        result.put("PRODCONFID", obj.get("prodConfId"));
        result.put("PRODVERID", obj.get("prodVerId"));
        Map<String, Object> master = (Map<String, Object>) obj.get("master");
        String prodProgSysName = master.get("prodProgSysName").toString();
        Map<String, Object> prodProg = getProdProgBySysName(obj, prodProgSysName, login, password);

        result.put("PRODPROGID", prodProg.get("PRODPROGID"));
        result.put("INSAMVALUE", prodProg.get("insAmValue"));

        result.put("PREMVALUE", master.get("premium"));
        boolean travelType = Boolean.valueOf(master.get("travelType").toString()).booleanValue();
        if (travelType) {
            result.put("TRAVELTYPE", 1);
        } else {
            result.put("TRAVELTYPE", 0);
        }
        String currency = master.get("currency").toString();
        result.put("CURRENCYID", getCurrIdByCODE(currency, login, password));

        result.put("STARTDATE", parseDate(master.get("startDate")));
        result.put("FINISHDATE", parseDate(master.get("finishDate")));
        result.put("DURATION", master.get("duration"));
        result.put("insuredCount60", master.get("insuredCount60"));
        result.put("insuredCount70", master.get("insuredCount70"));
        result.put("insuredCount2", master.get("insuredCount2"));
        result.put("isSportEnabled", master.get("isSportEnabled"));
        result.put("dopPackageList", master.get("dopPackageList"));
        result.put("prodProgSysName", master.get("prodProgSysName"));

        Map<String, Object> territory = (Map<String, Object>) master.get("countries");
        result.put("TERRITORYSYSNAME", territory.get("SYSNAME"));

        Map<String, Object> pers = (Map<String, Object>) master.get("persons");
        List<Map<String, Object>> insuredList = (List<Map<String, Object>>) pers.get("insuredList");

        return result;
    }

    private Map<String, Object> remapFromGate(Map<String, Object> contrMapIn) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("protocol", contrMapIn.get("protocol"));
        result.put("host", contrMapIn.get("host"));
        result.put("port", contrMapIn.get("port"));
        result.put("localTZOffset", contrMapIn.get("localTZOffset"));
        result.put("sessionToken", contrMapIn.get("sessionToken"));

        result.put("PRODPROGID", contrMapIn.get("prodProgId"));
        result.put("PRODCONFID", contrMapIn.get("prodConfId"));
        result.put("PRODVERID", contrMapIn.get("prodVerId"));
        result.put("PREMVALUE", contrMapIn.get("insPremVal"));
        result.put("INSAMVALUE", contrMapIn.get("insAmVal"));
        result.put("insGOAmVal", contrMapIn.get("insGOAmVal"));

        result.put("CITIZENSHIP", contrMapIn.get("insCitizenship"));
        result.put("LASTNAME", contrMapIn.get("insSurname"));
        result.put("FIRSTNAME", contrMapIn.get("insName"));
        result.put("MIDDLENAME", contrMapIn.get("insMiddlename"));
        result.put("BIRTHPLACE", contrMapIn.get("insBirthplace"));
        if (contrMapIn.get("insBirthdate") != null) {
            try {
                Date d1 = df.parse(contrMapIn.get("insBirthdate").toString());
                if (d1 == null) {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    d1 = df1.parse(contrMapIn.get("insBirthdate").toString());
                }
                result.put("BIRTHDATE", d1);
            } catch (ParseException ex) {
                try {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    Date d1 = df1.parse(contrMapIn.get("insBirthdate").toString());
                    result.put("BIRTHDATE", d1);

                } catch (ParseException ex1) {
                    try {
                        DateFormat df1 = new SimpleDateFormat("ddMMyyyy");
                        Date d1 = df1.parse(contrMapIn.get("insBirthdate").toString());
                        result.put("BIRTHDATE", d1);

                    } catch (ParseException ex2) {
                        Logger.getLogger(AngularContractCustomFacade.class.getName()).error(null, ex2);
                    }
                }
            }
        }
        result.put("SEX", 0);
        if (contrMapIn.get("insGender") != null) {
            if ("female".equalsIgnoreCase(contrMapIn.get("insGender").toString())) {
                result.put("SEX", 1);
            }
        }

        result.put("CONTACTPHONEMOBILE", contrMapIn.get("insPhone"));
        result.put("PREVCONTACTEMAIL", contrMapIn.get("insEmail"));
        result.put("CONTACTEMAIL", contrMapIn.get("insEmailValid"));

        result.put("DOCTYPESYSNAME", contrMapIn.get("insPassDocType"));
        result.put("DOCSERIES", contrMapIn.get("insPassSeries"));
        result.put("DOCNUMBER", contrMapIn.get("insPassNumber"));

        if (contrMapIn.get("insPassIssueDate") != null) {
            try {
                Date d1 = df.parse(contrMapIn.get("insPassIssueDate").toString());
                if (d1 == null) {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    d1 = df1.parse(contrMapIn.get("insPassIssueDate").toString());
                }
                result.put("ISSUEDATE", d1);
            } catch (ParseException ex) {
                try {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    Date d1 = df1.parse(contrMapIn.get("insPassIssueDate").toString());
                    result.put("ISSUEDATE", d1);

                } catch (ParseException ex1) {
                    try {
                        DateFormat df1 = new SimpleDateFormat("ddMMyyyy");
                        Date d1 = df1.parse(contrMapIn.get("insPassIssueDate").toString());
                        result.put("ISSUEDATE", d1);

                    } catch (ParseException ex2) {
                        Logger.getLogger(AngularContractCustomFacade.class.getName()).error(null, ex2);
                    }
                }
            }
        }

        result.put("ISSUEDBY", contrMapIn.get("insPassIssuePlace"));
        result.put("ISSUERCODE", contrMapIn.get("insPassIssueCode"));

        Map<String, Object> insAddressMap = new HashMap<String, Object>();

        insAddressMap.put("addrSysName", "RegisterAddress");
        insAddressMap.put("eRegion", contrMapIn.get("insAdrRegNAME"));
        insAddressMap.put("regionCode", contrMapIn.get("insAdrRegCODE"));

        insAddressMap.put("eCity", contrMapIn.get("insAdrCityNAME"));
        insAddressMap.put("cityCode", contrMapIn.get("insAdrCityCODE"));

        insAddressMap.put("eStreet", contrMapIn.get("insAdrStrNAME"));
        insAddressMap.put("streetCode", contrMapIn.get("insAdrStrCODE"));
        insAddressMap.put("eIndex", contrMapIn.get("insAdrStrPOSTALCODE"));

        insAddressMap.put("eHouse", contrMapIn.get("insAdrHouse"));
        insAddressMap.put("eCorpus", contrMapIn.get("insAdrHousing"));
        insAddressMap.put("eBuilding", contrMapIn.get("insAdrBuilding"));
        insAddressMap.put("eFlat", contrMapIn.get("insAdrFlat"));
        List<Map<String, Object>> addressList = new ArrayList<Map<String, Object>>();
        addressList.add(insAddressMap);
        result.put("INSADDRESSDATA", addressList);

        Map<String, Object> objAddressMap = new HashMap<String, Object>();
        objAddressMap.put("eRegion", contrMapIn.get("objAdrRegNAME"));
        objAddressMap.put("regionCode", contrMapIn.get("objAdrRegCODE"));

        objAddressMap.put("eCity", contrMapIn.get("objAdrCityNAME"));
        objAddressMap.put("cityCode", contrMapIn.get("objAdrCityCODE"));

        objAddressMap.put("eStreet", contrMapIn.get("objAdrStrNAME"));
        objAddressMap.put("streetCode", contrMapIn.get("objAdrStrCODE"));
        objAddressMap.put("eIndex", contrMapIn.get("objAdrStrPOSTALCODE"));

        objAddressMap.put("eHouse", contrMapIn.get("objAdrHouse"));
        objAddressMap.put("eCorpus", contrMapIn.get("objAdrHousing"));
        objAddressMap.put("eBuilding", contrMapIn.get("objAdrBuilding"));
        objAddressMap.put("eFlat", contrMapIn.get("objAdrFlat"));
        result.put("OBJADDRESSDATA", objAddressMap);
        result.put("OBJTYPE", contrMapIn.get("objTypeId"));

        result.put("validate", contrMapIn.get("validate"));
        XMLUtil.convertDateToFloat(result);
        return result;
    }

    private Map<String, Object> contrExtUpdate(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> findRes = dsContrExtBrowse(contrMap, login, password);
        if (findRes.get("CONTREXTID") != null) {
            findRes.put("smsCode", contrMap.get("smsCode"));

            //findRes.put("hid", hid);
            Map<String, Object> updateRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordUpdate", findRes, login, password);
            result.put("updateRes", updateRes);
        }
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsContrExtBrowse(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> result = null;
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("PRODCONFID", contrMap.get("PRODCONFID"));
        param.put("NAME", "CONTREXTDATAVERID");
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductDefaultValueBrowseListByParam", param, login, password);
        if (qres.get("VALUE") != null) {
            Long contrExtHbDataVerId = getLongParam(qres.get("VALUE"));
            Map<String, Object> paramhb = new HashMap<String, Object>();
            paramhb.put("HBDATAVERID", contrExtHbDataVerId);
            paramhb.put("CONTRID", contrMap.get("CONTRID"));

            paramhb.put("ReturnAsHashMap", "TRUE");
            result = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordBrowseListByParam", paramhb, login, password);
            result.put("HBDATAVERID", contrExtHbDataVerId);
        }
        return result;
    }

    private boolean checkPayment(Map<String, Object> result, String login, String password) throws Exception {
        if (result.get("payRes") != null) {
            Map<String, Object> paramsLog = new HashMap<String, Object>();
            Map<String, Object> contrMap = (Map<String, Object>) result.get("CONTRMAP");
            paramsLog.put("MERCHANTORDERNUM", result.get("orderGuid"));
            paramsLog.put("OBJECTID", contrMap.get("CONTRID"));
            paramsLog.put("ORDERID", result.get("orderId"));
            paramsLog.put("ReturnAsHashMap", "TRUE");

            Map<String, Object> logRes = this.callService(BIVPOSWS_SERVICE_NAME, "dsPayLogBrowseListByParam", paramsLog, login, password);
            if (logRes.get("PAYLOGID") != null) {
                paramsLog.put("PAYLOGID", logRes.get("PAYLOGID"));
                paramsLog.put("ERRORTEXT", result.get("payRes").toString() + " " + logRes.get("ERRORTEXT").toString());
                this.callService(BIVPOSWS_SERVICE_NAME, "dsPayLogUpdate", paramsLog, login, password);
                return true;
            }
        }
        return false;
    }

    private void saveFactPay(Map<String, Object> result, String login, String password) {
        // фактический платеж должен сохранятся, после поступления средств на счет, т.е. после синхронизации с платежной системой.
    }

    @Override
    protected Map<String, Object> sendSms(String phone, Map<String, Object> contrQRes, String sessionId, String login, String password) throws Exception {
        String code = generateRandomCode();
        logger.debug("sms code: " + code);
        contrQRes.put("smsCode", code);

        // сохранение в договор сгенерированного СМС-кода
        if (isB2BMode(contrQRes)) {
            Map<String, Object> contrB2BQRes = newMap();
            contrB2BQRes.put("CONTRID", contrQRes.get("CONTRID"));
            contrB2BQRes.put("EXTERNALID", contrQRes.get("EXTERNALID"));
            contrB2BQRes.put("SMSCODE", code);
            this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrUpdate", contrB2BQRes, login, password);
        } else {
            contrExtUpdate(contrQRes, login, password);
        }

        getSmsInit();
        String message = this.smsText + " " + code;
        if (phone.length() == 10) {
            phone = "7" + phone;
        }
        SmsSender smssender = new SmsSender();
        logger.debug("sms message: " + message);
        userLogActionCreateEx(sessionId, getStringParam(contrQRes.get("CONTRID")), "Отправка СМС", message, code, phone, "", "", login, password);
        Map<String, Object> sendRes = smssender.doGet(this.smsUser, this.smsPwd, this.smsFrom, phone, message);
        //Map<String, Object> sendRes = smssender.sendSms(phone, message);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("SENDRES", sendRes);
        result.put("STATUS", "OK");
        return result;
    }

    private String generateRandomCode() {
        Random r = new Random();
        int code = r.nextInt(9999);
        NumberFormat nf = new DecimalFormat("0000");
        String result = nf.format(code);

        //String result = String.valueOf(code);
        return result;//"1122";
    }

    private Long updateTravelInsured(Map<String, Object> params, String login, String password) throws Exception {
        Long result = null;

        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.putAll(params);
        createParams.put("ReturnAsHashMap", "TRUE");
        createParams.remove("INSOBJID");
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsTravelInsuredCreate", createParams, login, password);

        // апдейтим ноду, устанавливая текущим застрахованным только что вставленный
        if (qres != null) {
            result = Long.valueOf(qres.get("INSOBJID").toString());

            Long travelInsuredNodeId = null;
            if (null != params.get("INSOBJNODEID")) {
                travelInsuredNodeId = Long.valueOf(params.get("INSOBJNODEID").toString());
            }
            // updatим ноду на текущий дривер            
            Map<String, Object> updateParams = new HashMap<String, Object>();
            updateParams.put("INSOBJNODEID", travelInsuredNodeId);
            updateParams.put("INSOBJID", result);
            params.put("INSOBJID", result);
            this.callService(INSPOSWS_SERVICE_NAME, "dsInsuranceObjectNodeUpdate", updateParams, login, password);
        }
        return result;
    }

    private Long insertTravelInsured(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> nodeParams = new HashMap<String, Object>();
        Long result = null;
        nodeParams.put("ReturnAsHashMap", "TRUE");
        nodeParams.put("LASTVERNUMBER", 0);
        nodeParams.put("RVERSION", 0);
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsInsuranceObjectNodeCreate", nodeParams, login, password);
        if (qres != null) {
            result = Long.valueOf(qres.get("INSOBJNODEID").toString());
            params.put("INSOBJNODEID", result);
            params.put("VERNUMBER", 0);
            result = this.updateTravelInsured(params, login, password);
        }
        return result;
    }

    private Long insertContractObject(Map<String, Object> params, String login, String password) throws Exception {
        Long result = null;
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.putAll(params);
        createParams.put("ReturnAsHashMap", "TRUE");
        createParams.remove("CONTROBJID");
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsContractObjectCreate", createParams, login, password);
        if (qres != null) {
            result = Long.valueOf(qres.get("CONTROBJID").toString());
        }
        return result;
    }

    /**
     * метод, создаст договор ВЗР, со всеми необходимыми сущностями.
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"OBJMAP"})
    public Map<String, Object> dsTravelContractCreateEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params.get("OBJMAP") != null) {
            Map<String, Object> contrMapIn = (Map<String, Object>) params.get("OBJMAP");
            Map<String, Object> contrMap = (Map<String, Object>) contrMapIn.get("obj");//
            if (!contrMap.isEmpty()) {
                Map<String, String> contrEmptyRequiredFields = new HashMap<String, String>();
                if (checkRequiredFields(contrMapIn, contrEmptyRequiredFields, vzrRequiredFields)) {
                    String login = params.get(WsConstants.LOGIN).toString();
                    String password = params.get(WsConstants.PASSWORD).toString();
                    /*if (isB2BMode(params)) {
                     Map<String, Object> master = (Map<String, Object>) contrMap.get("master");
                     Map<String, Object> product = getB2Bproduct(getStringParam(contrMap.get("prodConfId")), Direction.TO_SAVE, login, password);
                     Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
                     List<Map<String, Object>> prodStructList = (List<Map<String, Object>>) prodVer.get("PRODSTRUCTS");
                     List<Map<String, Object>> prodProgramList = (List<Map<String, Object>>) prodVer.get("PRODPROGS");
                     Map<String, Object> programMap = filterProductProgramList(prodProgramList, master.get("prodProgSysName").toString()).get(0);
                     Map<String, Object> prodProgWithExtAttr = getProdProgBySysName(contrMap, master.get("prodProgSysName").toString(), login, password);

                     master.put("prodConfId", contrMap.get("prodConfId"));
                     master.put("prodVerId", contrMap.get("prodVerId"));
                     master.put("PRODCONF", product);
                     master.put("STARTDATE", parseDate(master.get("startDate")));
                     master.put("FINISHDATE", parseDate(master.get("finishDate")));
                     Map<String, Object> pers = (Map<String, Object>) master.get("persons");
                     Map<String, Object> insurer = (Map<String, Object>) pers.get("insurer");
                     master.put("insurer", insurer);
                     Map<String, Object> preparedParams = prepareB2BParams(master, Direction.TO_SAVE);
                     preparedParams.put("DURATION", master.get("duration"));
                     Date docDate = new Date();
                     preparedParams.put("DOCUMENTDATE", docDate);
                     preparedParams.put("DECLDATE", docDate);
                     Map<String, Object> userInfo = findDepByLogin(login, password);
                     Long sellerId = getSellerId(userInfo, login, password);
                     preparedParams.put("SELLERID", sellerId);
                     preparedParams.put("ORGSTRUCTID", userInfo.get("DEPARTMENTID"));
                     preparedParams.put("SELFORGSTRUCTID", userInfo.get("DEPARTMENTID"));
                     preparedParams.put("INSAMVALUE", prodProgWithExtAttr.get("insAmValue"));
                     preparedParams.put("PRODPROGID", programMap.get("PRODPROGID"));
                     Long currId = getCurrIdByCODE(master.get("currency").toString(), login, password);
                     Double exchangeRate = getExchangeCourceByCurID(currId, new Date(), login, password);
                     preparedParams.put("CURRENCYRATE", exchangeRate);

                     if ("NoUSARF".equalsIgnoreCase(master.get("countrySys").toString()) || "NoRF".equalsIgnoreCase(master.get("countrySys").toString())) {
                     if ((preparedParams.get("STARTDATE") != null) && (preparedParams.get("FINISHDATE") != null)) {
                     Date sd = (Date) preparedParams.get("STARTDATE");
                     Date fd = (Date) preparedParams.get("FINISHDATE");
                     Date fdShengen = new Date();
                     fdShengen.setTime(fd.getTime() + 15 * 24 * 60 * 60 * 1000);

                     GregorianCalendar sdgc = new GregorianCalendar();
                     sdgc.setTime(sd);
                     sdgc.set(Calendar.YEAR, sdgc.get(Calendar.YEAR) + 1);
                     sdgc.set(Calendar.DATE, sdgc.get(Calendar.DATE) - 1);

                     if (fdShengen.getTime() > sdgc.getTimeInMillis()) {
                     fdShengen.setTime(sdgc.getTimeInMillis());
                     }
                     preparedParams.put("FINISHDATE", fdShengen);
                     }
                     }
                     String contrNum = generateContrNum(Long.valueOf(contrMap.get("prodConfId").toString()), login, password);
                     contrMap.put("CONTRPOLSER", "004EP");
                     contrMap.put("CONTRPOLNUM", "901" + contrNum);
                     contrMap.put("CONTRNUMBER", "004EP901" + contrNum);

                     Map<String, Object> contrExtMap = (Map<String, Object>) preparedParams.get("CONTREXTMAP");
                     contrExtMap.put("insuranceProgram", programMap.get("PRODPROGID"));
                     if (master.get("countrySys") != null) {
                     if (master.get("countrySys").toString().equalsIgnoreCase("NoUSARF")) {
                     contrExtMap.put("insuranceTerritory", 0L);
                     }
                     if (master.get("countrySys").toString().equalsIgnoreCase("NoRF")) {
                     contrExtMap.put("insuranceTerritory", 1L);
                     }
                     if (master.get("countrySys").toString().equalsIgnoreCase("RFSNG")) {
                     contrExtMap.put("insuranceTerritory", 2L);
                     }
                     if (master.get("countrySys").toString().equalsIgnoreCase("NoUSA")) {
                     contrExtMap.put("insuranceTerritory", 3L);
                     }
                     }
                     contrExtMap.put("dayCount", master.get("duration"));
                     boolean travelType = Boolean.valueOf(master.get("travelType").toString()).booleanValue();
                     if (travelType) {
                     contrExtMap.put("annualPolicy", 1L);
                     } else {
                     contrExtMap.put("annualPolicy", 0L);
                     }
                     contrExtMap.put("adultsAndChildren3_60", master.get("insuredCount60"));
                     contrExtMap.put("babes", master.get("insuredCount2"));
                     contrExtMap.put("old61_70", master.get("insuredCount70"));
                     contrExtMap.put("optionSport", master.get("isSportEnabled"));
                     contrExtMap.put("DOPPACKLIST", master.get("dopPackageList"));
                     contrExtMap.put("RISKSYSNAMES", master.get("riskSysNames"));
                     // Пересчет премии.
                     Map<String, Object> calcParams = new HashMap<String, Object>();
                     master.put("currencyId", preparedParams.get("INSAMCURRENCYID"));
                     master.put("travelKind", contrExtMap.get("annualPolicy"));
                     master.put("territorySysName", master.get("countrySys"));
                     master.put("programSysName", master.get("prodProgSysName"));
                     calcParams.put("CALCMAP", master);
                     calcParams.put("ReturnAsHashMap", "TRUE");
                     Map<String, Object> calcResult = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsAngularCalculatePremium", calcParams, login, password);
                     preparedParams.put("PREMVALUE", getBigDecimalParam(calcResult.get("PREMIUM")));

                     Map<String, Object> prodRiskParams = new HashMap<String, Object>();
                     prodRiskParams.put("PRODVERID", preparedParams.get("PRODVERID"));
                     Map<String, Object> prodRiskRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductRiskBrowseListByParam", prodRiskParams, login, password);
                     List<Map<String, Object>> prodRiskList = (List<Map<String, Object>>) prodRiskRes.get(RESULT);
                     CopyUtils.sortByStringFieldName(prodRiskList, "SYSNAME");
                     List<Map<String, Object>> riskList = (List<Map<String, Object>>) calcResult.get("RISKLIST");
                     for (Map<String, Object> risk : riskList) {
                     List<Map<String, Object>> filteredProdRisk = CopyUtils.filterSortedListByStringFieldName(prodRiskList, "SYSNAME", risk.get("PRODRISKSYSNAME").toString());
                     if (!filteredProdRisk.isEmpty()) {
                     Map<String, Object> prodRisk = filteredProdRisk.get(0);
                     risk.put("PRODRISKID", prodRisk.get("PRODRISKID"));
                     }
                     risk.put("EXTERNALID", risk.get("PRODRISKSYSNAME"));
                     risk.put("PREMVALUE", getBigDecimalParam(risk.get("PREMIUMBASE")));
                     risk.put("PAYPREMVALUE", getBigDecimalParam(risk.get("PREMIUM")));
                     risk.put("PREMCURRENCYID", 1L);
                     }
                     // обработка стурктуры продукта
                     preparedParams.remove("INSOBJGROUPLIST");
                     List<Map<String, Object>> insObjGroupList = new ArrayList<Map<String, Object>>();
                     Map<String, Object> insObjGroup = new HashMap<String, Object>();
                     Map<String, Object> insTypeStructMap = filterProductStructureList(prodStructList, 2L).get(0);
                     insObjGroup.put("HBDATAVERID", insTypeStructMap.get("HBDATAVERID"));
                     insObjGroup.put("PRODSTRUCTID", insTypeStructMap.get("PRODSTRUCTID"));
                     insObjGroupList.add(insObjGroup);
                     preparedParams.put("INSOBJGROUPLIST", insObjGroupList);
                     //
                     Map<String, Object> contractParams = new HashMap<String, Object>();
                     contractParams.put("CONTRMAP", preparedParams);
                     contractParams.put(RETURN_AS_HASH_MAP, true);
                     Map<String, Object> createdContract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrSave", contractParams, login, password);
                     logger.debug("createdContract (dsB2BContrSave):");
                     logger.debug(createdContract);

                     createdContract.put(RETURN_AS_HASH_MAP, true);
                     Map<String, Object> browsedContract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", createdContract, login, password);
                     logger.debug("browsedContract (dsB2BContrLoad):");
                     logger.debug(browsedContract);

                     Map<String, Object> contract = prepareB2BParams(browsedContract, Direction.TO_LOAD);

                     logger.debug("contract (after prepareB2BParams, TO_LOAD):");
                     logger.debug(contract);

                     //createdContract.put("HASH", base64Encode(createdContract.get("GUID").toString()));
                     //Map<String, Object> prod2 = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", preparedParams, login, password);
                     result = contract;

                     return result;
                     //return null; //!только для отладки!
                     }*/
                    //получаем конфигурацию продукта
                    Long prodVerId = getLongParam(contrMap.get("prodVerId"));
                    Long prodConfId = getLongParam(contrMap.get("prodConfId"));
                    contrMap.put("PRODCONFID", prodConfId);
                    //создаем contrNode
                    Long contrNodeId = сontrNodeСreate(0L, 0L, login, password);
                    contrMap.put("CONTRNODEID", contrNodeId);
                    Date docDate = new Date();
                    contrMap.put("DOCUMENTDATE", docDate);
                    contrMap.put("DECLDATE", docDate);

                    Map<String, Object> master = (Map<String, Object>) contrMap.get("master");
                    BigDecimal promoValue = getPromoValueByCodeAndProdVerId(master.get("promo"), prodVerId.longValue(), login, password);
                    contrMap.put("promoCode", master.get("promo"));
                    contrMap.put("promoValue", promoValue);

                    Map<String, Object> pers = (Map<String, Object>) master.get("persons");
                    Map<String, Object> insurer = (Map<String, Object>) pers.get("insurer");
                    List<Map<String, Object>> insuredListIn = (List<Map<String, Object>>) pers.get("insuredList");
                    remapInsurer(insurer);
                    //создаем лицо
                    //создаем персону в CRM
                    Map<String, Object> personMap = participantCreate(insurer, login, password);
                    contrMap.put("PARTICIPANTID", personMap.get("PARTICIPANTID"));
                    contrMap.put("PERSONID", personMap.get("PERSONID"));
                    //создаем договор
                    Map<String, Object> userInfo = findDepByLogin(login, password);
                    Long sellerId = getSellerId(userInfo, login, password);
                    contrMap.put("SELLERID", sellerId);
                    contrMap.put("ORGSTRUCTID", userInfo.get("DEPARTMENTID"));
                    contrMap.put("SELFORGSTRUCTID", userInfo.get("DEPARTMENTID"));
                    //SELLERID - сделать продавца для сайта? или искать его по логину
                    contrMap.put("PRODVERID", prodVerId);

                    remapContr(contrMap, master, login, password);

                    Long contrId = contrCreate(contrMap, login, password);
                    contrMap.put("CONTRID", contrId);
                    //устанавливаем текущую версию договора в contrNode
                    setRightsOnContr(userInfo, contrId, login, password);

                    сontrNodeUpdate(contrNodeId, contrId, login, password);
                    // генерим номер договора
                    String contrNum = generateContrNum(prodConfId, login, password);
                    if (contrNum.indexOf("/") >= 0) {
                        String[] cnArr = contrNum.split("/");
                        contrMap.put("CONTRPOLSER", cnArr[0] + cnArr[1]);
                        contrMap.put("CONTRPOLNUM", cnArr[2]);
                    } else {
                        contrMap.put("CONTRPOLSER", contrNum.substring(0, 5));
                        contrMap.put("CONTRPOLNUM", contrNum.substring(5));
                    }
                    contrMap.put("CONTRNUMBER", contrNum);
                    contrMap.put("ORGSTRUCT", userInfo.get("DEPARTMENTID"));
                    // Пересчет премии.
                    Map<String, Object> calcParams = new HashMap<String, Object>();
                    master.put("currencyId", contrMap.get("INSAMCURRENCYID"));
                    master.put("travelKind", contrMap.get("TRAVELTYPE"));
                    master.put("territorySysName", contrMap.get("territoty"));
                    master.put("programSysName", master.get("prodProgSysName"));
                    calcParams.put("CALCMAP", master);
                    calcParams.put("ReturnAsHashMap", "TRUE");
                    Map<String, Object> calcResult = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsAngularCalculatePremium", calcParams, login, password);
                    contrMap.put("PREMVALUE", roundSum(getBigDecimalParam(calcResult.get("PREMIUM")).multiply(promoValue).doubleValue()));

                    Map<String, Object> prodRiskParams = new HashMap<String, Object>();
                    prodRiskParams.put("PRODVERID", prodVerId);
                    Map<String, Object> prodRiskRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductRiskBrowseListByParam", prodRiskParams, login, password);
                    List<Map<String, Object>> prodRiskList = (List<Map<String, Object>>) prodRiskRes.get(RESULT);
                    CopyUtils.sortByStringFieldName(prodRiskList, "SYSNAME");
                    List<Map<String, Object>> riskList = (List<Map<String, Object>>) calcResult.get("RISKLIST");
                    for (Map<String, Object> risk : riskList) {
                        List<Map<String, Object>> filteredProdRisk = CopyUtils.filterSortedListByStringFieldName(prodRiskList, "SYSNAME", risk.get("PRODRISKSYSNAME").toString());
                        if (!filteredProdRisk.isEmpty()) {
                            Map<String, Object> prodRisk = filteredProdRisk.get(0);
                            risk.put("PRODRISKID", prodRisk.get("PRODRISKID"));
                        }
                        risk.put("EXTERNALID", risk.get("PRODRISKSYSNAME"));
                        risk.put("PREMVALUE", roundSum(getBigDecimalParam(risk.get("PREMIUMBASE")).multiply(promoValue).doubleValue()));
                        risk.put("PAYPREMVALUE", roundSum(getBigDecimalParam(risk.get("PREMIUM")).multiply(promoValue).doubleValue()));
                        risk.put("PREMCURRENCYID", 1);
                        risk.put("CONTRID", contrId);

                        saveRisk(risk, login, password);
                    }

                    // сохраняем номер договора
                    //contrMap.put("PREMVALUE", roundSum(getBigDecimalParam(calcResult.get("PREMIUM")).multiply(promoValue).doubleValue()));
                    сontrUpdate(contrMap, login, password);

                    contrExtCreate(contrMap, login, password);
                    readContrExtId(contrMap, login, password);

                    // if (contrMap.get("INSUREDLIST") != null) {
                    //List<Map<String, Object>> insuredList = (List<Map<String, Object>>) contrMap.get("INSUREDLIST");
                    for (Iterator<Map<String, Object>> it = insuredListIn.iterator(); it.hasNext();) {
                        Map<String, Object> insured = it.next();
                        remapInsured(insured);
                        Long insObjId = this.insertTravelInsured(insured, login, password);
                        Long contrObjId = null;
                        if (null != insured.get("CONTROBJID")) {
                            contrObjId = Long.valueOf(insured.get("CONTROBJID").toString());
                        }
                        Map<String, Object> contrObjParams = new HashMap<String, Object>();
                        contrObjParams.put("CONTRID", contrId);
                        contrObjParams.put("CONTROBJID", contrObjId);
                        contrObjParams.put("INSOBJID", insObjId);
                        if (null == contrObjId) {
                            contrObjParams.put("INSAMVALUE", contrMap.get("INSAMVALUE"));
                            contrObjParams.put("PREMCURRENCYID", contrMap.get("PREMCURRENCYID"));
                            contrObjParams.put("PREMVALUE", roundSum(getBigDecimalParam(calcResult.get("PREMIUM")).multiply(promoValue).doubleValue()));
                            contrObjParams.put("STARTDATE", contrMap.get("STARTDATE"));
                            contrObjParams.put("FINISHDATE", contrMap.get("FINISHDATE"));
                            contrObjId = this.insertContractObject(contrObjParams, login, password);
                        }
                        insured.put("CONTRID", contrId);
                        insured.put("CONTROBJID", contrObjId);
                        insured.put("INSOBJID", insObjId);
                    }
                    // }
                    contrMap.put("HASH", base64Encode(contrMap.get("GUID").toString()));
                    result.put(RESULT, contrMap);
                } else {
                    //не все обязательные поля заполнены. создавать договор нельзя.
                    result.put("Status", "requiredFieldError");
                    result.put("EmptyRequiredFields", contrEmptyRequiredFields);
                }
            } else {
                result.put("Status", "emptyInputMap");
            }
        } else {
            result.put("Status", "emptyInputMap");
        }
        return result;
    }

    /**
     * метод, выберет договор по хешу. сделан, для получения данных договора на
     * форму..
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsTravelContractBrowseEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params.get("CONTRMAP") == null) {
            if (params.get("CONTRID") != null) {
                Map<String, Object> contrMapIn = new HashMap<String, Object>();
                contrMapIn.put("CONTRID", params.get("CONTRID"));
                params.put("CONTRMAP", contrMapIn);
            }
        }
        if (params.get("CONTRMAP") != null) {
            Map<String, Object> contrMapIn = (Map<String, Object>) params.get("CONTRMAP");
            String login = params.get(WsConstants.LOGIN).toString();
            String password = params.get(WsConstants.PASSWORD).toString();
            Long contrId;
            Map<String, Object> browseParams = new HashMap<String, Object>();
            if (contrMapIn.get("orderNum") != null) {
                String orderGuid = base64Decode(contrMapIn.get("orderNum").toString());
                browseParams.put("EXTERNALID", orderGuid);
            } else {
                // в шлюзе с ангуляром закрыта возможность запросить договор по ИД.
                if (contrMapIn.get("contrId") != null) {
                    browseParams.put("CONTRID", contrMapIn.get("contrId"));
                }
                if (contrMapIn.get("CONTRID") != null) {
                    browseParams.put("CONTRID", contrMapIn.get("CONTRID"));
                }
            }

            //browseParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> browseParamsRes = this.selectQuery("dsHabContractBrowseListParamEx", "dsHabContractBrowseListParamExCount", browseParams);
            if (browseParamsRes.get(RESULT) != null) {
                List<Map<String, Object>> contrList = (List<Map<String, Object>>) browseParamsRes.get(RESULT);
                if (!contrList.isEmpty()) {
                    Map<String, Object> contrMap = (Map<String, Object>) contrList.get(0);
                    result.put("CONTRMAP", contrMap);
                    if (contrMap.get("INSUREDID") != null) {
                        Map<String, Object> qParam = new HashMap<String, Object>();
                        qParam.put("PARTICIPANTID", contrMap.get("INSUREDID"));
                        Map<String, Object> qRes = this.callService(CRMWS_SERVICE_NAME, "contactGetListByParticipantId", qParam, login, password);
                        List<Map<String, Object>> contactList = WsUtils.getListFromResultMap(qRes);
                        for (Map<String, Object> contact : contactList) {
                            if ("PersonalEmail".equalsIgnoreCase(contact.get("CONTACTTYPESYSNAME").toString())) {
                                contrMap.put("INSEMAIL", contact.get("VALUE"));
                            }
                            if ("MobilePhone".equalsIgnoreCase(contact.get("CONTACTTYPESYSNAME").toString())) {
                                contrMap.put("INSPHONE", contact.get("VALUE"));
                            }
                        }
                    }
                    Map<String, Object> contrExt = dsContrExtBrowse(contrMap, login, password);
                    contrMap.put("CONTREXTRATTR", contrExt);
                    Map<String, Object> qParam = new HashMap<String, Object>();
                    qParam.put("CALCVERID", 1070);
                    qParam.put("NAME", "Ins.Vzr.Risk.Limits");
                    Map<String, Object> qConditionParam = new HashMap<String, Object>();
                    qConditionParam.put("territorySysName", contrExt.get("territoty"));
                    qConditionParam.put("programSysName", contrExt.get("prodProgSysName"));
                    qParam.put("PARAMS", qConditionParam);
                    Map<String, Object> qRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", qParam, login, password);
                    if (qRes.get(RESULT) != null) {
                        if (qRes.get(RESULT) instanceof List) {
                            List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                            logger.debug(resList);
                            contrMap.put("riskLimitsList", resList);
                        }
                    }
                    if (contrExt.get("riskSysNames") != null) {
                        String selectRiskListString = contrExt.get("riskSysNames").toString();
                        String[] riskList = selectRiskListString.split(",");
                        //Map<String,Object> riskMap = new HashMap<String, Object>();
                        List<Map<String, Object>> riskArr = new ArrayList<Map<String, Object>>();
                        for (String risk : riskList) {
                            Map<String, Object> riskMap = new HashMap<String, Object>();
                            riskMap.put("NAME", risk);
                            riskArr.add(riskMap);
                        }
                        contrMap.put("selectRiskList", riskArr);
                    }

                    contrId = Long.valueOf(contrMap.get("CONTRID").toString());
                    if (contrId != null) {
                        Map<String, Object> queryParams = new HashMap<String, Object>();
                        queryParams.put("CONTRID", contrId);
                        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsContractObjectInsuredBrowseListByParam", queryParams, login, password);
                        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
                            List<Map<String, Object>> insuredList = (List<Map<String, Object>>) qres.get(RESULT);
                            contrMap.put("INSUREDLIST", insuredList);
                            contrMap.put("CONTROBJLIST", insuredList);
                        }
                        Map<String, Object> qresRisk = this.callService(INSPOSWS_SERVICE_NAME, "dsContractRiskBrowseListByContrIdJoinProdRisk", queryParams, login, password);
                        if ((qresRisk != null) && (qresRisk.get(RESULT) != null)) {
                            List<Map<String, Object>> riskList = (List<Map<String, Object>>) qresRisk.get(RESULT);
                            if (riskList.isEmpty()) {
                                // пересчитать риски
                                // Пересчет премии.
                                Map<String, Object> master = new HashMap<String, Object>();
                                Map<String, Object> calcParams = new HashMap<String, Object>();
                                Map<String, Object> contrExtMap = (Map<String, Object>) contrMap.get("CONTREXTRATTR");
                                master.put("currencyId", contrMap.get("INSAMCURRENCYID"));
                                if (contrExtMap.get("travelType") != null) {
                                    master.put("travelKind", contrExtMap.get("travelType"));
                                } else {
                                    master.put("travelKind", 0);
                                }
                                master.put("territorySysName", contrExtMap.get("territoty"));
                                master.put("programSysName", contrExtMap.get("prodProgSysName"));
                                master.put("duration", contrMap.get("DURATION"));
                                master.put("riskSysNames", contrExtMap.get("riskSysNames"));
                                master.put("isSportEnabled", contrExtMap.get("isSportEnabled"));
                                master.put("insuredCount2", contrExtMap.get("insuredCount2"));
                                master.put("insuredCount60", contrExtMap.get("insuredCount60"));
                                master.put("insuredCount70", contrExtMap.get("insuredCount70"));
                                if (contrMap.get("CURRENCYRATE") != null) {
                                    master.put("CURRENCYRATE", contrMap.get("CURRENCYRATE"));
                                }
                                calcParams.put("CALCMAP", master);
                                calcParams.put("ReturnAsHashMap", "TRUE");
                                Map<String, Object> calcResult = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsAngularCalculatePremium", calcParams, login, password);
                                //contrMap.put("PREMVALUE", calcResult.get("PREMIUM"));

                                Map<String, Object> prodRiskParams = new HashMap<String, Object>();
                                prodRiskParams.put("PRODVERID", contrMap.get("PRODVERID"));
                                Map<String, Object> prodRiskRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductRiskBrowseListByParam", prodRiskParams, login, password);
                                List<Map<String, Object>> prodRiskList = (List<Map<String, Object>>) prodRiskRes.get(RESULT);
                                CopyUtils.sortByStringFieldName(prodRiskList, "SYSNAME");
                                // сохранить в договор
                                List<Map<String, Object>> riskList1 = (List<Map<String, Object>>) calcResult.get("RISKLIST");

                                for (Map<String, Object> risk : riskList1) {
                                    List<Map<String, Object>> filteredProdRisk = CopyUtils.filterSortedListByStringFieldName(prodRiskList, "SYSNAME", risk.get("PRODRISKSYSNAME").toString());
                                    if (!filteredProdRisk.isEmpty()) {
                                        Map<String, Object> prodRisk = filteredProdRisk.get(0);
                                        risk.put("PRODRISKID", prodRisk.get("PRODRISKID"));
                                    }
                                    risk.put("EXTERNALID", risk.get("PRODRISKSYSNAME"));
                                    risk.put("PREMVALUE", risk.get("PREMIUMBASE"));
                                    risk.put("PAYPREMVALUE", risk.get("PREMIUM"));
                                    risk.put("PREMCURRENCYID", 1);
                                    risk.put("CONTRID", contrId);

                                    saveRisk(risk, login, password);
                                    if (!filteredProdRisk.isEmpty()) {
                                        Map<String, Object> prodRisk = filteredProdRisk.get(0);
                                        risk.put("PRODRISKSYSNAME", prodRisk.get("SYSNAME"));
                                    }
                                    riskList.add(risk);
                                }
                            }
                            contrMap.put("RISKLIST", riskList);
                        }
                    }
                    if (contrMap.get("CONTRNODEID") != null) {
                        Long contrNodeId = (Long) contrMap.get("CONTRNODEID");
                        // получение план графика
                        Map<String, Object> planParams = new HashMap<String, Object>();
                        planParams.put("CONTRNODEID", contrNodeId);
                        //planParams.put("ReturnAsHashMap", "TRUE");
                        Map<String, Object> qPlanRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentFactBrowseListByParamEx", planParams, login, password);
                        contrMap.put("PAYMENTLIST", WsUtils.getListFromResultMap(qPlanRes));
                    }
                    // получение графика оплаты
                    Map<String, Object> factParams = new HashMap<String, Object>();
                    factParams.put("CONTRID", contrId);
                    Map<String, Object> qFactRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentBrowseListByParam", factParams, login, password);
                    contrMap.put("PAYMENTSCHEDULELIST", qFactRes.get(RESULT));
                    result.put("CONTRMAP", contrMap);
                }
            }
            return result;

        } else {
            result.put("Status", "emptyInputMap");
        }
        return result;
    }

    /**
     * метод, выберет договор по хешу. сделан, для получения данных договора на
     * форму..
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsTravelContractBrowseListEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        //browseParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> browseParamsRes = this.selectQuery("dsHabContractBrowseListParamEx", "dsHabContractBrowseListParamExCount", params);
        if (browseParamsRes.get(RESULT) != null) {
            List<Map<String, Object>> contrList = (List<Map<String, Object>>) browseParamsRes.get(RESULT);
            if (!contrList.isEmpty()) {
                String count = String.valueOf(contrList.size());
                logger.debug("start fix promo " + count + " contracts");
                int ind = 0;
                for (Map<String, Object> contrMap : contrList) {
                    ind++;
                    logger.debug("process " + String.valueOf(ind) + " of " + count + " with contrid - " + contrMap.get("CONTRID").toString());
                    result.put("CONTRMAP", contrMap);
                    if (contrMap.get("INSUREDID") != null) {
                        Map<String, Object> qParam = new HashMap<String, Object>();
                        qParam.put("PARTICIPANTID", contrMap.get("INSUREDID"));
                        Map<String, Object> qRes = this.callService(CRMWS_SERVICE_NAME, "contactGetListByParticipantId", qParam, login, password);
                        List<Map<String, Object>> contactList = WsUtils.getListFromResultMap(qRes);
                        for (Map<String, Object> contact : contactList) {
                            if ("PersonalEmail".equalsIgnoreCase(contact.get("CONTACTTYPESYSNAME").toString())) {
                                contrMap.put("INSEMAIL", contact.get("VALUE"));
                            }
                            if ("MobilePhone".equalsIgnoreCase(contact.get("CONTACTTYPESYSNAME").toString())) {
                                contrMap.put("INSPHONE", contact.get("VALUE"));
                            }
                        }
                    }
                    Map<String, Object> contrExt = dsContrExtBrowse(contrMap, login, password);
                    contrMap.put("CONTREXTRATTR", contrExt);
                    Map<String, Object> qParam = new HashMap<String, Object>();
                    qParam.put("CALCVERID", 1070);
                    qParam.put("NAME", "Ins.Vzr.Risk.Limits");
                    Map<String, Object> qConditionParam = new HashMap<String, Object>();
                    qConditionParam.put("territorySysName", contrExt.get("territoty"));
                    qConditionParam.put("programSysName", contrExt.get("prodProgSysName"));
                    qParam.put("PARAMS", qConditionParam);
                    Map<String, Object> qRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", qParam, login, password);
                    if (qRes.get(RESULT) != null) {
                        if (qRes.get(RESULT) instanceof List) {
                            List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                            logger.debug(resList);
                            contrMap.put("riskLimitsList", resList);
                        }
                    }
                    if (contrExt.get("riskSysNames") != null) {
                        String selectRiskListString = contrExt.get("riskSysNames").toString();
                        String[] riskList = selectRiskListString.split(",");
                        //Map<String,Object> riskMap = new HashMap<String, Object>();
                        List<Map<String, Object>> riskArr = new ArrayList<Map<String, Object>>();
                        for (String risk : riskList) {
                            Map<String, Object> riskMap = new HashMap<String, Object>();
                            riskMap.put("NAME", risk);
                            riskArr.add(riskMap);
                        }
                        contrMap.put("selectRiskList", riskArr);
                    }

                    Long contrId = Long.valueOf(contrMap.get("CONTRID").toString());
                    if (contrId != null) {
                        Map<String, Object> queryParams = new HashMap<String, Object>();
                        queryParams.put("CONTRID", contrId);
                        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsContractObjectInsuredBrowseListByParam", queryParams, login, password);
                        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
                            List<Map<String, Object>> insuredList = (List<Map<String, Object>>) qres.get(RESULT);
                            contrMap.put("INSUREDLIST", insuredList);
                            contrMap.put("CONTROBJLIST", insuredList);
                        }
                        //  Map<String, Object> qresRisk = this.callService(INSPOSWS_SERVICE_NAME, "dsContractRiskBrowseListByContrIdJoinProdRisk", queryParams, login, password);
                        //  if ((qresRisk != null) && (qresRisk.get(RESULT) != null)) {
                        List<Map<String, Object>> riskList = new ArrayList<Map<String, Object>>();
                        //     if (riskList.isEmpty()) {
                        // пересчитать риски
                        // Пересчет премии.
                        Map<String, Object> master = new HashMap<String, Object>();
                        Map<String, Object> calcParams = new HashMap<String, Object>();
                        Map<String, Object> contrExtMap = (Map<String, Object>) contrMap.get("CONTREXTRATTR");
                        master.put("currencyId", contrMap.get("INSAMCURRENCYID"));
                        if (contrExtMap.get("travelType") != null) {
                            master.put("travelKind", contrExtMap.get("travelType"));
                        } else {
                            master.put("travelKind", 0);
                        }
                        master.put("territorySysName", contrExtMap.get("territoty"));
                        master.put("programSysName", contrExtMap.get("prodProgSysName"));
                        master.put("duration", contrMap.get("DURATION"));
                        master.put("riskSysNames", contrExtMap.get("riskSysNames"));
                        master.put("isSportEnabled", contrExtMap.get("isSportEnabled"));
                        master.put("insuredCount2", contrExtMap.get("insuredCount2"));
                        master.put("insuredCount60", contrExtMap.get("insuredCount60"));
                        master.put("insuredCount70", contrExtMap.get("insuredCount70"));
                        if (contrMap.get("CURRENCYRATE") != null) {
                            master.put("CURRENCYRATE", contrMap.get("CURRENCYRATE"));
                        }
                        calcParams.put("CALCMAP", master);
                        calcParams.put("ReturnAsHashMap", "TRUE");
                        Map<String, Object> calcResult = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsAngularCalculatePremium", calcParams, login, password);
                        //contrMap.put("PREMVALUE", calcResult.get("PREMIUM"));

                        Map<String, Object> prodRiskParams = new HashMap<String, Object>();
                        prodRiskParams.put("PRODVERID", contrMap.get("PRODVERID"));
                        Map<String, Object> prodRiskRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductRiskBrowseListByParam", prodRiskParams, login, password);
                        List<Map<String, Object>> prodRiskList = (List<Map<String, Object>>) prodRiskRes.get(RESULT);
                        CopyUtils.sortByStringFieldName(prodRiskList, "SYSNAME");
                        // сохранить в договор
                        List<Map<String, Object>> riskList1 = (List<Map<String, Object>>) calcResult.get("RISKLIST");

                        Double premContr = getDoubleParam(contrMap.get("PREMVALUE"));
                        Double currencyrate = getDoubleParam(contrMap.get("CURRENCYRATE"));
                        Double premRiskBaseSum = 0.0;
                        Double premRiskSum = 0.0;

                        for (Map<String, Object> risk : riskList1) {
                            List<Map<String, Object>> filteredProdRisk = CopyUtils.filterSortedListByStringFieldName(prodRiskList, "SYSNAME", risk.get("PRODRISKSYSNAME").toString());
                            if (!filteredProdRisk.isEmpty()) {
                                Map<String, Object> prodRisk = filteredProdRisk.get(0);
                                risk.put("PRODRISKID", prodRisk.get("PRODRISKID"));
                            }

                            Double premBaseRisk = getDoubleParam(risk.get("PREMIUMBASE"));
                            Double premRisk = getDoubleParam(risk.get("PREMIUM"));
                            premRiskBaseSum = premRiskBaseSum + premBaseRisk;
                            premRiskSum = premRiskSum + premRisk;
                            risk.put("EXTERNALID", risk.get("PRODRISKSYSNAME"));
                            risk.put("PREMVALUE", risk.get("PREMIUMBASE"));
                            risk.put("PAYPREMVALUE", risk.get("PREMIUM"));
                            risk.put("PREMCURRENCYID", 1);
                            risk.put("CONTRID", contrId);

                            //  saveRisk(risk, login, password);
                            if (!filteredProdRisk.isEmpty()) {
                                Map<String, Object> prodRisk = filteredProdRisk.get(0);
                                risk.put("PRODRISKSYSNAME", prodRisk.get("SYSNAME"));
                            }
                            riskList.add(risk);
                        }
                        boolean isShare = true;
                        if (Math.abs(premContr - (premRiskBaseSum * currencyrate)) < 0.01) {
                            isShare = false;
                            logger.debug("скидки нет по базовой премии риска");
                        }
                        if (Math.abs(premContr - (premRiskBaseSum * currencyrate)) < 0.01) {
                            isShare = false;
                            logger.debug("скидки нет по премии риска");
                        }
                        if (isShare) {
                            // скидка есть. считаем коеффициент, и сохраняем в расширенные атрибуты.
                            Double coef = premContr / (premRiskBaseSum * currencyrate);
                            BigDecimal cbd = BigDecimal.valueOf(coef);
                            logger.debug("скидка есть: " + cbd.setScale(2, RoundingMode.HALF_UP).toString());
                            contrExt.put("shareValue", cbd.setScale(2, RoundingMode.HALF_UP).doubleValue());
                            result = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordUpdate", contrExt, login, password);
                        }

                        //    }
                        contrMap.put("RISKLIST", riskList);
                        // }
                    }
                    if (contrMap.get("CONTRNODEID") != null) {
                        Long contrNodeId = (Long) contrMap.get("CONTRNODEID");
                        // получение план графика
                        Map<String, Object> planParams = new HashMap<String, Object>();
                        planParams.put("CONTRNODEID", contrNodeId);
                        //planParams.put("ReturnAsHashMap", "TRUE");
                        Map<String, Object> qPlanRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentFactBrowseListByParamEx", planParams, login, password);
                        contrMap.put("PAYMENTLIST", WsUtils.getListFromResultMap(qPlanRes));
                    }
                    // получение графика оплаты
                    Map<String, Object> factParams = new HashMap<String, Object>();
                    factParams.put("CONTRID", contrId);
                    Map<String, Object> qFactRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentBrowseListByParam", factParams, login, password);
                    contrMap.put("PAYMENTSCHEDULELIST", qFactRes.get(RESULT));
                }
                result.put("CONTRLIST", contrList);
                logger.debug("finish fix promo");
            }
        }
        return result;
    }

    private Map<String, Object> getProdProgBySysName(Map<String, Object> hbMapIn, String prodProgSysName, String login, String password) throws Exception {
        Map<String, Object> qRes = null;
        if ((hbMapIn.get("prodConfId") != null) && (hbMapIn.get("prodVerId") != null)) {
            Map<String, Object> qParam = new HashMap<String, Object>();
            qParam.put("PRODCONFID", hbMapIn.get("prodConfId"));
            qParam.put("PRODVERID", hbMapIn.get("prodVerId"));
            qParam.put("ReturnAsHashMap", "TRUE");
            qParam.put("SYSNAME", prodProgSysName);
            qRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductProgramBrowseListByParamWithExtProp", qParam, login, password);
        }
        return qRes;
    }

    protected Date parseDate(Object map) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date result = null;
        if (map != null) {
            Map<String, Object> mapIn = (Map<String, Object>) map;
            if (mapIn.get("dateLocaleStr") != null) {
                try {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    String date = mapIn.get("dateLocaleStr").toString();
                    if (date.indexOf(".") < 0) {
                        date = date.substring(0, 2) + "." + date.substring(2, 4) + "." + date.substring(4);
                    }
                    result = df1.parse(date);
                } catch (ParseException ex) {
                    if (mapIn.get("date") != null) {
                        try {
                            result = df.parse(mapIn.get("date").toString());
                        } catch (ParseException ex1) {
                            Logger.getLogger(AngularContractCustomFacade.class.getName()).error(null, ex1);
                        }
                    }
                }
            } else if (mapIn.get("dateStr") != null) {
                try {
                    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                    String date = mapIn.get("dateStr").toString();
                    if (date.indexOf(".") < 0) {
                        date = date.substring(0, 2) + "." + date.substring(2, 4) + "." + date.substring(4);
                    }
                    result = df1.parse(date);
                } catch (ParseException ex) {
                    if (mapIn.get("date") != null) {
                        try {
                            result = df.parse(mapIn.get("date").toString());
                        } catch (ParseException ex1) {
                            Logger.getLogger(AngularContractCustomFacade.class.getName()).error(null, ex1);
                        }
                    }
                }
            } else if (mapIn.get("date") != null) {
                try {
                    result = df.parse(mapIn.get("date").toString());
                } catch (ParseException ex) {
                    Logger.getLogger(AngularContractCustomFacade.class.getName()).error(null, ex);
                }
            }
        }
        return result;
    }

    private Long getCurrIdByCODE(String currency, String login, String password) {
        Long result = 2L;
        if ("USD".equalsIgnoreCase(currency)) {
            result = 2L;
        }
        if ("EUR".equalsIgnoreCase(currency)) {
            result = 3L;
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

    private void sendEmailCurrencyFail(String email, Date date, boolean usdExist, boolean euroExist, String login, String password) {
        Map<String, Object> sendParams = new HashMap<String, Object>();
        sendParams.put("SMTPSubject", "Ошибка сервиса обновления курсов валют");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateStr = sdf.format(date);
        String currNames = "";
        if (!usdExist) {
            currNames = "USD ";
        }
        if (!euroExist) {
            currNames = currNames + "EUR ";
        }
        sendParams.put("SMTPMESSAGE", "На дату " + dateStr + " отсутствует курс валют: " + currNames + ". Проверьте работу сервиса получения курсов валют от центробанка.");
        sendParams.put("SMTPReceipt", email);
        if (isAllEmailValid(email)) {
            logger.debug("sendParams = " + sendParams.toString());
            Map<String, Object> sendRes = null;

            try {
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
                            }
                        }
                    }
                }
                logger.debug("mailSendSuccess");
            } catch (Exception e) {
                saveEmailInFile(sendParams, login, password);
                logger.debug("mailSendException: ", e);
            }
            //return sendRes;        
        }
    }

    private void remapContr(Map<String, Object> contrMap, Map<String, Object> master, String login, String password) throws Exception {
        String prodProgSysName = master.get("prodProgSysName").toString();
        Map<String, Object> prodProg = getProdProgBySysName(contrMap, prodProgSysName, login, password);

        contrMap.put("PRODPROGID", prodProg.get("PRODPROGID"));
        contrMap.put("INSAMVALUE", prodProg.get("insAmValue"));

        contrMap.put("PREMVALUE", master.get("premium"));
        boolean travelType = Boolean.valueOf(master.get("travelType").toString()).booleanValue();
        boolean travelType90 = false;
        if (master.get("travelType90") != null) {
            travelType90 = Boolean.valueOf(master.get("travelType90").toString()).booleanValue();

        }
        if (travelType90) {
            contrMap.put("TRAVELTYPE", 2);
        } else if (travelType) {
            contrMap.put("TRAVELTYPE", 1);
        } else {
            contrMap.put("TRAVELTYPE", 0);
        }
        String currency = master.get("currency").toString();
        Long currId = getCurrIdByCODE(currency, login, password);
        contrMap.put("INSAMCURRENCYID", currId);
        // валюта премии - Рубль
        contrMap.put("PREMCURRENCYID", 1);
        Double exchangeRate = getExchangeCourceByCurID(currId, new Date(), login, password);
        contrMap.put("CURRENCYRATE", exchangeRate);

        contrMap.put("STARTDATE", parseDate(master.get("startDate")));
        contrMap.put("FINISHDATE", parseDate(master.get("finishDate")));
        // костыль от заказчика. если территория NoUSARF NoRF дюратион + 15 дней не должен превышать год.
        Long duration = Long.valueOf(master.get("duration").toString());
        Map<String, Object> countries = (Map<String, Object>) master.get("countries");
        String countrySysName = countries.get("SYSNAME").toString();
        contrMap.put("territoty", countrySysName);

        if ("NoUSARF".equalsIgnoreCase(countrySysName) || "NoRF".equalsIgnoreCase(countrySysName)) {
            if ((contrMap.get("STARTDATE") != null) && (contrMap.get("FINISHDATE") != null)) {
                Date sd = (Date) contrMap.get("STARTDATE");
                Date fd = (Date) contrMap.get("FINISHDATE");
                Date fdShengen = new Date();
                fdShengen.setTime(fd.getTime() + 15 * 24 * 60 * 60 * 1000);

                GregorianCalendar sdgc = new GregorianCalendar();
                sdgc.setTime(sd);
                sdgc.set(Calendar.YEAR, sdgc.get(Calendar.YEAR) + 1);
                sdgc.set(Calendar.DATE, sdgc.get(Calendar.DATE) - 1);

                if (fdShengen.getTime() > sdgc.getTimeInMillis()) {
                    fdShengen.setTime(sdgc.getTimeInMillis());
                }
                contrMap.put("FINISHDATE", fdShengen);
            }
        }

        /*    if ("NoUSARF".equalsIgnoreCase(countrySysName) || "NoRF".equalsIgnoreCase(countrySysName)) {
         Date sd = parseDate(master.get("startDate"));
         Date maxfd = new Date(sd.getTime());
         GregorianCalendar gcfd = new GregorianCalendar();
         gcfd.setTime(maxfd);
         gcfd.set(Calendar.YEAR, gcfd.get(Calendar.YEAR) + 1);
         gcfd.set(Calendar.DATE, gcfd.get(Calendar.DATE) - 1);
         Long yearDayCount = (gcfd.getTimeInMillis() - sd.getTime()) * 24 * 60 * 60 * 1000;
         duration = duration + 15;
         if (duration > yearDayCount) {
         duration = yearDayCount;
         }
         }*/
        contrMap.put("DURATION", duration);

        contrMap.put("insuredCount60", master.get("insuredCount60"));
        contrMap.put("insuredCount70", master.get("insuredCount70"));
        contrMap.put("insuredCount2", master.get("insuredCount2"));
        contrMap.put("isSportEnabled", master.get("isSportEnabled"));
        contrMap.put("dopPackageList", master.get("dopPackageList"));
        contrMap.put("prodProgSysName", master.get("prodProgSysName"));
        contrMap.put("riskSysNames", master.get("riskSysNames"));

        Map<String, Object> territory = (Map<String, Object>) master.get("countries");
        contrMap.put("TERRITORYSYSNAME", territory.get("SYSNAME"));
        contrMap.put("PRODPROGID", contrMap.get("PRODPROGID"));

    }

    private void remapInsured(Map<String, Object> insured) {
        insured.put("LASTNAME", insured.get("surname"));
        insured.put("FIRSTNAME", insured.get("name"));
        insured.put("BIRTHDATE", parseDate(insured.get("birthDate")));
        insured.put("ALTNAME", insured.get("namestr"));

    }

    /**
     * рекурсивная функция проверки наличия поля в мапе.
     *
     * @param contrMapIn - проверяемый массив
     * @param sysName - имя или неймспейс обязательного поля.
     * @return
     */
    private boolean checkMap(Map<String, Object> contrMapIn, String sysName) {
        return checkMap(contrMapIn, sysName, true);
    }

    /**
     *
     * @param contrMapIn - проверяемый массив
     * @param sysName - имя или неймспейс обязательного поля.
     * @param serchInSubMap - флаг необходимо ли пытаться найти поле в подмапах
     * @return
     */
    private boolean checkMap(Map<String, Object> contrMapIn, String sysName, boolean serchInSubMap) {
        boolean result = false;
        // цикл по содержимому мапы
        for (Entry<String, Object> entry : contrMapIn.entrySet()) {
            String entryName = entry.getKey();
            Object entryValue = entry.getValue();
            String firstSysName = sysName;
            String lastSysName = "";
            // если в сиснэйм есть точка, то ищем первую часть, 
            // если находим - то она должна содержать мапу, в которой надо 
            // поискать вторую часть.
            if (sysName.indexOf(".") > 0) {
                firstSysName = sysName.substring(0, sysName.indexOf("."));
                lastSysName = sysName.substring(sysName.indexOf(".") + 1);
            }

            //if (sysName.indexOf(".") < 0) {
            // простая проверка.
            // если имя содержит | то надо проверить на наличие хотябы одно из свойств перечисленных через |
            boolean compareSysNameRes = false;
            if (firstSysName.indexOf("-") > 0) {
                String[] firstSysNameArr = firstSysName.split("-");
                List<String> firstList = new ArrayList<String>(Arrays.asList(firstSysNameArr));
                compareSysNameRes = firstList.contains(entryName);
            } else {
                compareSysNameRes = entryName.equals(firstSysName);
            }

            if (compareSysNameRes) {
                if (entryValue != null) {
                    if (lastSysName.isEmpty()) {
                        // если точки нет - то lastSysName пустое, значит иерархию проверять дальше не надо - проверяем соответствие мапы.
                        if (!entryValue.toString().isEmpty()) {
                            result = true;
                            return result;
                        }
                    } else {
                        if (entryValue instanceof Map) {
                            // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                            if (checkMap((Map<String, Object>) entryValue, lastSysName, false)) {
                                result = true;
                                return result;
                            }
                        }
                        if (entryValue instanceof List) {
                            // например застрахованные
                            List<Object> entryAsList = (List) entryValue;
                            for (Object entryMap : entryAsList) {
                                // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                                if (checkMap((Map<String, Object>) entryMap, lastSysName, false)) {
                                    result = true;
                                } else {
                                    // если ошибка хотя бы в одном элементе массива - то вся проверяемая иерархия ошибочна выводим ошибку
                                    result = false;
                                    return result;
                                }
                            }
                            return result;
                        }
                    }
                }
            } else if (entryValue instanceof Map) {
                if (serchInSubMap) {
                    if (checkMap((Map<String, Object>) entryValue, sysName)) {
                        result = true;
                        return result;
                    }
                }
            }
        }
        return result;
    }

    private String getUploadFilePath() {

        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();

        return result;
    }

    private void saveEmailInFile(Map<String, Object> sendParams, String login, String password) {
        // получаем значение флага сохранения из конфига bivsberposws
        Config config = Config.getConfig(SERVICE_NAME);
        String saveEmailInFile = config.getParam("SAVEEMAILINFILE", "FALSE");
        if ("true".equalsIgnoreCase(saveEmailInFile)) {
            // если флаг взведен, делаем файл по пути uploadpath 
            // получаем путь к uploadpath
            String upPath = getUploadFilePath();
            // сохраняем в данный файл текст письма
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");
            String email = sendParams.get("SMTPReceipt").toString();
            String htmlText = sendParams.get("HTMLTEXT").toString();
            email.replace("@", "_");

            BufferedOutputStream bufferedOutput = null;
            FileOutputStream fileOutputStream = null;
            try {
                String fileName = sdf.format(now) + "_" + email + ".html";
                fileOutputStream = new FileOutputStream(upPath + fileName);//".txt"
                bufferedOutput = new BufferedOutputStream(fileOutputStream);
                //Start writing to the output stream
                byte[] ba = null;
                String codePage = "";
                if (codePage.equals("")) {
                    ba = htmlText.getBytes();
                } else {
                    ba = htmlText.getBytes(codePage);
                }
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

    Long createNode(String login, String password) throws Exception {
        Map<String, Object> nodeParams = new HashMap<String, Object>();
        nodeParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        nodeParams.put("RVERSION", 0L);
        nodeParams.put("LASTVERNUMBER", 0L);
        Map<String, Object> nodeRes = this.callService(Constants.INSPOSWS, "dsInsuranceObjectNodeCreate", nodeParams, login, password);
        return Long.valueOf(nodeRes.get("INSOBJNODEID").toString());
    }

    void updateNodeActiveVersion(Long insObjNodeId, Long insObjId, String login, String password) throws Exception {
        Map<String, Object> insObjNodeUpdParams = new HashMap<String, Object>();
        insObjNodeUpdParams.put("INSOBJNODEID", insObjNodeId);
        insObjNodeUpdParams.put("INSOBJID", insObjId);
        this.callService(Constants.INSPOSWS, "dsInsuranceObjectNodeUpdate", insObjNodeUpdParams, login, password);
    }

    private Map<String, Object> saveInsObj(Map<String, Object> insObj, Map<String, Object> contrMap, Map<String, Object> master,
            Map<String, Object> insObjAddress, List<Map<String, Object>> riskList, List<Map<String, Object>> prodRiskList, String login, String password) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Long contrId = getLongParam(contrMap.get("CONTRID"));
        Long insObjNodeId = createNode(login, password);
        Map<String, Object> saveParams = new HashMap<String, Object>();

        String typeSysName = getStringParam(insObj.get("typeSysName"));
        saveParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        saveParams.put("INSOBJNODEID", insObjNodeId);
        saveParams.put("FACINGTYPE", insObj.get("FACINGTYPE"));
        saveParams.put("HOUSEHAS", insObj.get("houseHas"));
        saveParams.put("NAME", typeSysName);
        saveParams.put("OBJAREA", insObj.get("space"));
        saveParams.put("OBJTYPESYSNAME", typeSysName);
        saveParams.put("PRODYEARSYSNAME", insObj.get("buildYear"));
        saveParams.put("WALMATERIAL", insObj.get("woodInWal"));

        String insObjServiceName = getInsObjServiceName(typeSysName);
        Map<String, Object> saveRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, insObjServiceName, saveParams, login, password);
        Long insObjId = getLongParam(saveRes.get("INSOBJID"));
        //очень надолго зависает
        //updateNodeActiveVersion(insObjNodeId, insObjId, login, password);

        // Создать объект страхования
        Map<String, Object> contrObjParams = new HashMap<String, Object>();
        contrObjParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        contrObjParams.put("CONTRID", contrId);
        contrObjParams.put("INSOBJID", insObjId);
        Map<String, Object> contrObjRes = this.callService(Constants.INSPOSWS, "dsContractObjectCreate", contrObjParams, login, password);
        Long contrObjId = Long.valueOf(contrObjRes.get("CONTROBJID").toString());

        // сохранить адрес имущества
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("ADDRESSDATA", insObjAddress);
        params.put("NAME", typeSysName);

        params.put("CONTRID", contrId);
        params.put("ROWSTATUS", 1L);

        Map<String, Object> cpRes = this.callService(INSPOSWS_SERVICE_NAME, "dsContractPropertySave", params, login, password);

        // сохранить риски по объекту
        //получаем риск конструктива, по объекту
        // риск конструктива у каждого объекта свой.
//        Map<String, Object> construct = getRiskFromObj(insObj, "construct");
//        saveContrRiskList(construct,contrObjId, contrId);
        // риски за исключением конструктива только у основного объекта, поэтому сохраняются отдельно
        // распределения лимита покрытия по объектам нет. поэтому у каждого объекта будет сохранен полный риск.
        for (Map<String, Object> risk : riskList) {
            String sysName = getStringParam(risk.get("SYSNAME"));
            if (!sysName.isEmpty()) {
                List<Map<String, Object>> prodRiskFilterList = CopyUtils.filterSortedListByStringFieldName(prodRiskList, "SYSNAME", sysName);
                Long prodRiskId = null;
                if (!prodRiskFilterList.isEmpty()) {
                    prodRiskId = getLongParam(prodRiskFilterList.get(0).get("PRODRISKID"));
                }
                // если квартира, то объект только 1 и все риски сохраняем на нем
                if ("flat".equalsIgnoreCase(typeSysName)) {
                    saveContrRiskList(risk, contrObjId, contrId, prodRiskId, login, password);
                } else // для остальных объектов с привязкой к объекту сохраняем только конструктив, остальные риски привязываем только к договору
                if ("construct".equalsIgnoreCase(sysName)) {
                    saveContrRiskList(risk, contrObjId, contrId, prodRiskId, login, password);
                } else {
                    saveContrRiskList(risk, null, contrId, prodRiskId, login, password);
                }
            }
        }

        result.put("CONTRPROPERTYID", cpRes.get("CONTRPROPERTYID"));
        result.put("INSOBJNODEID", insObjNodeId);
        result.put("INSOBJID", insObjId);
        result.put("CONTROBJID", contrObjId);
        return result;
    }

    private void saveMovableDetail(Map<String, Object> movableObj, Long contrId, Long movableObjHbDataVerId, String login, String password) {

    }

    private String getInsObjServiceName(String typeSysName) {
        String result = "dsInsObjCreate";
        if ("flat".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjFlatCreate";
        }
        if ("house".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjHouseCreate";
        }
        if ("sauna".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjSaunaCreate";
        }
        if ("other".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjOtherCreate";
        }
        if ("movable".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjMovableCreate";
        }
        if ("go".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjGOCreate";
        }
        return result;
    }

    private Map<String, Object> getRiskFromObj(Map<String, Object> insObj, String construct) {
        Map<String, Object> result = null;
        List<Map<String, Object>> riskList = (List<Map<String, Object>>) insObj.get("protectLevelList");
        CopyUtils.sortByStringFieldName(riskList, "SYSNAME");
        List<Map<String, Object>> riskListSorted = CopyUtils.filterSortedListByStringFieldName(riskList, "SYSNAME", construct);
        if (!riskListSorted.isEmpty()) {
            result = riskListSorted.get(0);
        }
        return result;
    }

    private void saveContrRiskList(Map<String, Object> risk, Long contrObjId, Long contrId, Long prodRiskId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        // риск может быть выключен
        if (getBooleanParam(risk.get("checked"), Boolean.TRUE)) {
            params.put("CONTROBJID", contrObjId);
            params.put("CONTRID", contrId);
            params.put("CURRENCYID", 1L);
            params.put("INSAMCURRENCYID", 1L);
            params.put("PREMCURRENCYID", 1L);
            params.put("PRODRISKID", prodRiskId);
            params.put("INSAMVALUE", risk.get("sum"));
            params.put("PREMVALUE", risk.get("prem"));
            this.callService(INSPOSWS_SERVICE_NAME, "dsContractRiskCreate", params, login, password);
        }
    }

    @WsMethod()
    public Map<String, Object> dsGetContractsStateDate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = null;
        result = this.selectQuery("dsGetContractsStateDate", "dsGetContractsStateDateCount", params);
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsGetB2BContractsStateDate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = null;
        result = this.selectQuery("dsGetB2BContractsStateDate", "dsGetB2BContractsStateDateCount", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsContractCreateInB2BModeEx(Map<String, Object> params) throws Exception {

        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        logger.debug("Вызван метод dsContractCreateInB2BModeEx...\n");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Integer oldProdConfId = getIntegerParam(chainedGetIgnoreCase(params, "prodConfId"));

        Map<String, Object> product = (Map<String, Object>) params.get("PRODCONF");
        if ((product == null)
                // дополнительные проверки на случай "повреждения" данных о продукте в ходе "эксплуатации"
                //|| (!(chainedGet(product, "PRODVER.PRODSTRUCTS") instanceof List))
                //|| (!(chainedGet(product, "PRODVER.PRODPROGS") instanceof List))) {
                || (!(((Map<String, Object>) product.get("PRODVER")).get("PRODSTRUCTS") instanceof List))
                || (!(((Map<String, Object>) product.get("PRODVER")).get("PRODPROGS") instanceof List))) {
            //Object prodConfId = chainedGetIgnoreCase(params, "prodConfId");
            product = getB2Bproduct(oldProdConfId.toString(), Direction.TO_SAVE, login, password);
            params.put("PRODCONF", product);
        }

        Map<String, Object> preparedParams = prepareB2BParams(params, Direction.TO_SAVE);

        Object preparingProcessLog = preparedParams.remove("PREPARINGPROCESSLOG");

        if ((oldProdConfId == PRODCONFID_VZR) || (oldProdConfId == PRODCONFID_HIB) || (oldProdConfId == PRODCONFID_PHIB) || (oldProdConfId == PRODCONFID_SIS) || (oldProdConfId == PRODCONFID_MORTGAGE)) {
            Map<String, Object> callParams = new HashMap<String, Object>();
            callParams.put("CONTRMAP", preparedParams);
            callParams.put(RETURN_AS_HASH_MAP, true);
            if ((oldProdConfId == PRODCONFID_VZR) || (oldProdConfId == PRODCONFID_SIS)) {
                callParams.put("ISMISSINGSTRUCTSCREATED", false);
            }
            preparedParams = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BUpdateContractInsuranceProductStructure", callParams, login, password);
            if (oldProdConfId == PRODCONFID_VZR) {
                preparedParams.put("ISMIGRATION", params.get("ISMIGRATION"));
            }
        }

        // прямой вызов dsB2BContrSave, авторизация и идентификатор сессии не требуется 
        // (для онлайн-продуктов параметры сессии определяются в dsB2BContrSave отдельно по имени пользователя вызвавшего метод)
        preparedParams.put(RETURN_AS_HASH_MAP, true);
        preparedParams.put("LINK", params.get("url"));
        Map<String, Object> createdContract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrSave", preparedParams, login, password);

        Long createdContractID = getLongParam(createdContract.get("CONTRID"));

        Map<String, Object> contract;
        if (createdContractID != null) {
            //logger.debug("createdContract (dsB2BContrSave):");
            //logger.debug(createdContract);
            createdContract.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> browsedContract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", createdContract, login, password);
            contract = prepareB2BParams(browsedContract, Direction.TO_LOAD);
        } else {
            contract = new HashMap<String, Object>();
            contract.putAll(createdContract);
        }

        contract.put("PREPARINGPROCESSLOG", preparingProcessLog);
        contract.put("PRODCONF", params.get("PRODCONF")); // возврат дополнительно и полученных в ходе работы сведений о продукте (для повторного использования)

        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        logger.debug("Метод dsContractCreateInB2BModeEx выполнился за " + callTimer + " мс.\n");

        // возврат результата
        return contract;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsContractApplyDiscountInB2BMode(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Integer oldProdConfId = getIntegerParam(chainedGetIgnoreCase(params, "prodConfId"));

        Map<String, Object> product = (Map<String, Object>) params.get("PRODCONF");
        if ((product == null)
                // дополнительные проверки на случай "повреждения" данных о продукте в ходе "эксплуатации"
                //|| (!(chainedGet(product, "PRODVER.PRODSTRUCTS") instanceof List))
                //|| (!(chainedGet(product, "PRODVER.PRODPROGS") instanceof List))) {
                || (!(((Map<String, Object>) product.get("PRODVER")).get("PRODSTRUCTS") instanceof List))
                || (!(((Map<String, Object>) product.get("PRODVER")).get("PRODPROGS") instanceof List))) {
            //Object prodConfId = chainedGetIgnoreCase(params, "prodConfId");
            product = getB2Bproduct(oldProdConfId.toString(), Direction.TO_SAVE, login, password);
            params.put("PRODCONF", product);
        }
        params.put("VERBOSELOG", Boolean.TRUE);

        if (oldProdConfId == PRODCONFID_MORTGAGE) {
            Map<String, Object> master = (Map<String, Object>) params.get("master");
            Double insAmValue = Double.valueOf(master.get("insuredSum").toString());
            params.put("INSAMVALUE", insAmValue);
            Double premValue = calcMortgagePremValue(insAmValue);
            params.put("PREMVALUE", premValue);
            master.put("contrPremium", premValue);
            params.put("PRODVERID", getLongParam(params.get("prodVerId")));
            params.put("PRODCONFID", getLongParam(params.get("prodConfId")));
        }
        Map<String, Object> preparedParams = prepareB2BParams(params, Direction.TO_SAVE);
        if ((oldProdConfId == PRODCONFID_HIB) || (oldProdConfId == PRODCONFID_MORTGAGE)) {
            Map<String, Object> callParams = new HashMap<String, Object>();
            callParams.put("CONTRMAP", preparedParams);
            callParams.put(RETURN_AS_HASH_MAP, true);
            preparedParams = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BUpdateContractInsuranceProductStructure", callParams, login, password);
        }
        Map<String, Object> discParams = new HashMap<String, Object>();
        discParams.put("CONTRMAP", preparedParams);
        discParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> discRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BApplyDiscountToContractMap", discParams, login, password);
        return (Map<String, Object>) discRes.get("CONTRMAP");
    }

}
