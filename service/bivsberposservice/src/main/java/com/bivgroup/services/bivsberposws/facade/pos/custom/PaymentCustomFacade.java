/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomBaseFacade.SERVICE_NAME;
import static com.bivgroup.services.bivsberposws.system.Constants.B2BPOSWS;
import static ru.diasoft.services.inscore.system.WsConstants.RETURN_AS_HASH_MAP;

import com.bivgroup.crm.ClientProfileEvent;
import com.bivgroup.lib.yandexkassa.api.caller.AbstractRequest;
import com.bivgroup.lib.yandexkassa.api.caller.CheckPaymentRequest;
import com.bivgroup.lib.yandexkassa.api.caller.CreateSberbankSMSPaymentRequest;
import com.bivgroup.lib.yandexkassa.api.caller.Request;
import com.bivgroup.services.bivsberposws.system.Constants;
import com.bivgroup.services.sberpayservice.SberPayService;
import com.bivgroup.services.sberpayservice.SberPayServiceException;
import java.util.*;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import javax.ws.rs.core.Response;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author averichevsm
 */
public class PaymentCustomFacade extends AngularContractCustomBaseFacade {

    private static final String BIVPOS_SERVICE_NAME = Constants.BIVPOSWS;
    private static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    private static final String SIGNB2BPOSWS_SERVICE_NAME = Constants.SIGNB2BPOSWS;
    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    private static final String B2BPOSWS_SERVICE_NAME = B2BPOSWS;
    private static final String PAYMENT_REGISTRATION_EVENT_SYSNAME = "NEW_PAYMENT_REGISTRATION";

    /** Не обращаться в платежную систему (когда следует выполнить только проверки и создание плана и факта платежа) */
    public static final String IS_SKIP_MERCHANT_CALL = "ISSKIPMERCHANTCALL";

    private static volatile int threadCount = 0;
    private static volatile int senderThreadCount = 0;
    private static volatile int senderCopyThreadCount = 0;
    private static volatile int sbolThreadCount = 0;

    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PaymentCustomFacade.class);

    /** Системное наименование состояния для записей о фактической оплате - имя параметра */
    public static final String PAYFACT_STATE_SYSNAME_PARAMNAME = "STATESYSNAME";

    // Системные наименования для записей о фактической оплате
    /** Системное наименование состояния для записей о фактической оплате - draft */
    private static final String PAYFACT_STATE_SYSNAME_DRAFT = "draft";
    /** Системное наименование состояния для записей о фактической оплате - success */
    private static final String PAYFACT_STATE_SYSNAME_SUCCESS = "success";
    /** Системное наименование состояния для записей о фактической оплате - fail */
    private static final String PAYFACT_STATE_SYSNAME_FAIL = "fail";

    /** Тип оплаты - имя параметра */
    public static final String PAYFACT_TYPE_PARAMNAME = "PAYFACTTYPE";
    /** Тип оплаты - наличными / через кассу / по платежке / по ПД-4 и т.п. */
    public static final Long PAYFACT_TYPE_INVOICE = 4L;
    /** Тип оплаты - через внешнюю платежную систему (т. н. merchant / acquiring - https://3dsec.sberbank.ru/payment/merchants/..) */
    public static final Long PAYFACT_TYPE_ACQUIRING = 1L;

    protected static final String URL_PAY_SUCCESS_PARAMNAME = "URLPAYSUCCESS";
    protected static final String URL_PAY_FAIL_PARAMNAME = "URLPAYFAIL";
    protected static final String URL_PAY_DEFAULT = "https://sberbankins.ru";

    // для result.put("Error", "описание ошибки для вывода в интерфейсе")
    protected static final String ERROR = "Error";
    // иногда курса валют приходится ждать минутами(((
    private static final Boolean IS_CURRENCY_DEBUG = false;
    private static final Double IS_CURRENCY_DEBUG_VALUE = 53.4d;

    private static boolean checkMerchantResult(Map<String, Object> merchantResult) {
        boolean checkResult = (merchantResult != null)
                && (merchantResult.get("ORDERID") != null)
                && (merchantResult.get("REFERENCENUMBER") != null)
                && (merchantResult.get("ORDERSTATUS") != null)
                && ("2".equals(merchantResult.get("ORDERSTATUS").toString()));
        return checkResult;
    }

    private static Map<String, Object> getMerchantResult(Map<String, Object> result) {
        Map<String, Object> merchantResult = null;
        if ((result != null) && (result.get(RESULT) != null)) {
            merchantResult = (Map<String, Object>) result.get(RESULT);
        } else {
            merchantResult = result;
        }
        return merchantResult;
    }

    @WsMethod()
    public Map<String, Object> dsCallTerminalPayment(final Map<String, Object> params) throws Exception {
        final String login = getStringParam(params, WsConstants.LOGIN);
        final String password = getStringParam(params, WsConstants.PASSWORD);
        final Integer contrId = getIntegerParam(params.get("contrId"));

        final Map<String, Object> contractSelectParams = new HashMap<String, Object>();
        contractSelectParams.put("CONTRID", contrId);
        contractSelectParams.put(RETURN_AS_HASH_MAP, "TRUE");
        final Map<String, Object>  contractSelectResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamEx", contractSelectParams, login, password);

        final String contractNumber = getStringParam(contractSelectResult, "CONTRNUMBER");
        final Integer contrNodeId = getIntegerParam(contractSelectResult.get("CONTRNODEID"));
        final String description = "Оплата взноса по договору страхования " + contractNumber;
        final Double premValue = getDoubleParam(contractSelectResult, "PREMVALUE");

        Map<String, Object> planPayment;
        {
            final Map<String, Object> createPlanPaymentParams = new HashMap<String, Object>();
            createPlanPaymentParams.put("CONTRID", contrId);
            createPlanPaymentParams.put("EXTERNALID", UUID.randomUUID().toString());
            createPlanPaymentParams.put("AMOUNT", premValue);
            createPlanPaymentParams.put("PAYDATE", new Date());
            createPlanPaymentParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
            xmlUtil.convertDateToFloat(createPlanPaymentParams);
            final Map<String, Object> createPlanPaymentResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentCreate", createPlanPaymentParams, login, password);
            createPlanPaymentParams.put("PAYID", createPlanPaymentResult.get("PAYID"));
            planPayment = createPlanPaymentParams;
        }
        Map<String, Object> factPayment;
        {
            final Map<String, Object> createFactPaymentParams = new HashMap<String, Object>();
            createFactPaymentParams.put("CONTRNODEID", contrNodeId);
            createFactPaymentParams.put("CONTRSECTIONID", null);
            createFactPaymentParams.put("PAYFACTTYPE", 4);
            createFactPaymentParams.put("PAYFACTDATE", new Date());
            createFactPaymentParams.put("AMCURRENCYID", 1L);
            createFactPaymentParams.put("AMVALUE", premValue); // TODO: 29.03.18 check
            createFactPaymentParams.put("AMVALUERUB", premValue); // TODO: 30.03.18 check
            createFactPaymentParams.put("SERIES", contrId);
            createFactPaymentParams.put("STATESYSNAME", PAYFACT_STATE_SYSNAME_SUCCESS);
            createFactPaymentParams.put("NOTE", "");
            createFactPaymentParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
            createFactPaymentParams.put("CODE", planPayment.get("EXTERNALID"));
            final Map<String, Object> createFactPaymentResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactCreate", createFactPaymentParams, login, password);
            createFactPaymentParams.put("PAYFACTID", createFactPaymentResult.get("PAYFACTID"));
            factPayment = createFactPaymentParams;
        }

        final String paymentCode = getStringParam(planPayment,"PAYID") + getStringParam(factPayment, "PAYFACTID");
        final Integer payfactId = getIntegerParam(factPayment.get("PAYFACTID"));

        factPayment.put("NOTE", paymentCode);

        this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactUpdate", factPayment, login, password); // обновление платежа

        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("PAYFACTID", payfactId);
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsCallCheckPaymentStatus(final Map<String, Object> params) throws Exception {
        final String login = getStringParam(params, WsConstants.LOGIN);
        final String password = getStringParam(params, WsConstants.PASSWORD);

        final Map<String, Object> selectPaymentParams = new HashMap<String, Object>();
        if (params.get("contrNodeId") != null) {
            final Long contrNodeId = getLongParam(params.get("contrNodeId"));
            selectPaymentParams.put("CONTRNODEID", contrNodeId);
            final Map<String, Object> selectPaymentResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactBrowseListByParamEx", selectPaymentParams, login, password);
            final Map<String, Object> result = new HashMap<String, Object>();
            List<Map<String,Object>> payFactRes = (List<Map<String,Object>>) selectPaymentResult.get(RESULT);
            boolean hasDraft = false;
            for (Map<String,Object> payFactMap :payFactRes) {
                if (PAYFACT_STATE_SYSNAME_SUCCESS.equalsIgnoreCase(getStringParam(payFactMap.get("STATESYSNAME")))) {
                    result.put("STATE", getStringParam(payFactMap, "STATESYSNAME"));
                    result.put("PAYFACTID", getLongParam(payFactMap.get("PAYFACTID")));
                    return result;
                }
                if (PAYFACT_STATE_SYSNAME_DRAFT.equalsIgnoreCase(getStringParam(payFactMap.get("STATESYSNAME")))) {
                    hasDraft = true;
                }
            }
            if (hasDraft) {
                result.put("STATE", PAYFACT_STATE_SYSNAME_DRAFT);
                return result;
            }
            result.put("STATE", "");
            result.put("PAYFACTID", "");
            return result;

        } else {
            final Long payFactId = getLongParam(params.get("payFactId"));
            selectPaymentParams.put("PAYFACTID", payFactId);
        }
        selectPaymentParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        final Map<String, Object> selectPaymentResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactBrowseListByParamEx", selectPaymentParams, login, password);
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("STATE", "");
        result.put("PAYFACTID", "");
        if (selectPaymentResult.get("STATESYSNAME") != null) {
            result.put("STATE", getStringParam(selectPaymentResult, "STATESYSNAME"));
            result.put("PAYFACTID", getLongParam(selectPaymentResult.get("PAYFACTID")));
        }
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsCallSberOnlySMSPaymentService(final Map<String, Object> params) throws Exception {
        final String login = getStringParam(params, WsConstants.LOGIN);
        final String password = getStringParam(params, WsConstants.PASSWORD);
        final Integer contrId = getIntegerParam(params.get("contrId"));

        final Map<String, Object> contractSelectParams = new HashMap<String, Object>();
        contractSelectParams.put("CONTRID", contrId);
        contractSelectParams.put(RETURN_AS_HASH_MAP, "TRUE");
        final Map<String, Object>  contractSelectResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamEx", contractSelectParams, login, password);

        final String contractNumber = getStringParam(contractSelectResult.get("CONTRNUMBER"));
        final Integer contrNodeId = getIntegerParam(contractSelectResult.get("CONTRNODEID"));
        // В соответствии с задачей 22877
        //final String description = "Оплата взноса по договору страхования " + contractNumber;
        final String description = contractNumber;
        final Double premValue = getDoubleParam(contractSelectResult, "PREMVALUE");
        final String phone = getStringParam(params, "phone");
        final String email = getStringParam(params, "email");

        final String key = UUID.randomUUID().toString();
        final String shopId = Config.getConfig(this.getModuleName()).getParam("YandexKassaAPICallerShopId", "");
        final String secretKey = Config.getConfig(this.getModuleName()).getParam("YandexKassaAPICallerSecretKey", "");

        final Boolean useTestCardFlag = Boolean.parseBoolean(Config.getConfig(this.getModuleName()).getParam("YandexKassaAPICallerUseTestCardRequest", "false"));
        final Boolean useLowPriceFlag = Boolean.parseBoolean(Config.getConfig(this.getModuleName()).getParam("YandexKassaAPICallerUseLowPrice", "false"));

        Map<String, Object> planPayment;
        {
            final Map<String, Object> createPlanPaymentParams = new HashMap<String, Object>();
            createPlanPaymentParams.put("CONTRID", contrId);
            createPlanPaymentParams.put("EXTERNALID", UUID.randomUUID().toString());
            createPlanPaymentParams.put("AMOUNT", premValue);
            createPlanPaymentParams.put("PAYDATE", new Date());
            createPlanPaymentParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
            xmlUtil.convertDateToFloat(createPlanPaymentParams);
            final Map<String, Object> createPlanPaymentResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentCreate", createPlanPaymentParams, login, password);
            createPlanPaymentParams.put("PAYID", createPlanPaymentResult.get("PAYID"));
            planPayment = createPlanPaymentParams;
        }
        Map<String, Object> factPayment;
        {
            final Map<String, Object> createFactPaymentParams = new HashMap<String, Object>();
            createFactPaymentParams.put("CONTRNODEID", contrNodeId);
            createFactPaymentParams.put("CONTRSECTIONID", null);
            createFactPaymentParams.put("PAYFACTTYPE", 3);
            createFactPaymentParams.put("PAYFACTDATE", new Date());
            createFactPaymentParams.put("AMCURRENCYID", 1L);
            createFactPaymentParams.put("AMVALUE", premValue); // TODO: 29.03.18 check
            createFactPaymentParams.put("AMVALUERUB", premValue); // TODO: 30.03.18 check
            createFactPaymentParams.put("SERIES", contrId);
            createFactPaymentParams.put("STATESYSNAME", PAYFACT_STATE_SYSNAME_DRAFT);
            createFactPaymentParams.put("NOTE", "");
            createFactPaymentParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
            createFactPaymentParams.put("CODE", planPayment.get("EXTERNALID"));
            final Map<String, Object> createFactPaymentResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactCreate", createFactPaymentParams, login, password);
            createFactPaymentParams.put("PAYFACTID", createFactPaymentResult.get("PAYFACTID"));
            factPayment = createFactPaymentParams;
        }

        final String paymentCode = getStringParam(planPayment,"PAYID") + getStringParam(factPayment, "PAYFACTID");
        final Integer payfactId = getIntegerParam(factPayment.get("PAYFACTID"));

        logger.debug(String.format("Try call Yandex Kassa: contrId-%s, premVal-%s", contrId, premValue));

        final CreateSberbankSMSPaymentRequest request = new CreateSberbankSMSPaymentRequest(shopId, secretKey, key, phone, String.valueOf(premValue), "RUB", email, description);

        request.setUseLowPrice(useLowPriceFlag);
        request.setUseTestCardRequest(useTestCardFlag);

        Map<String, Object> payRes = request.make();
        for (int i = 0; payRes.get("ERRORCODE").equals("202") && i < 4; i++) {
            TimeUnit.SECONDS.sleep(2);
            payRes = request.make();
        }

        logger.debug("Yandex Kassa pay service result =" + payRes.toString());

        if (payRes.get("PAYMENTSTATUS").equals("canceled")) {
            payRes.put("ERRORCODE", "100");
            payRes.put("ERRORMESSAGE", "Payment canceled");
        }

        if (payRes.get("ERRORCODE").equals("0")) {
            payRes.put("PAYFACTID", payfactId);
            final String externalPaymentId = getStringParam(payRes, "PAYMENTID");
            factPayment.put("NOTE", paymentCode);
            factPayment.put("ORDERID", externalPaymentId);
            this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactUpdate", factPayment, login, password);
        }

        return payRes;
    }

    /**
     * сервис оплаты доработан под требования сбсж в декабре 2017
     *
     * @param params <UL>
     *               <LI>PAYMENTMAP - map параметров с формы</LI>
     *               </UL>
     * @return <UL>
     * <LI>STATUS - статус</LI>
     * <LI>SMSVALID - смс код верен</LI>
     * <LI>EXTIDVALID - guid договора верен</LI>
     * </UL>
     * @author averichevsm
     */
    @WsMethod()
    public Map<String, Object> dsCallPaymentService(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> payMap = (Map<String, Object>) params;
        Event regEvent = new Event(params);
        // Задаем начальные параметры события и постпроцессор для мапы параметров
        Map<String, Object> eventMap = regEvent.postProcess(new NewPayProcessor())
                .setEventType(PAYMENT_REGISTRATION_EVENT_SYSNAME)
                .persist();
        Long newPaymentEventId = getLongParam(eventMap, "id");
        result.put("STATUS", "CALLSUCCESS");
        // номер покупки - это ид фактического платежа + ид договора.
        //todo генерить код можно в сервисе сохранения договора, связав его с номером договора. а здесь только сравнить, что гвид не изменился, и соответствует номеру договора
        //гвид генерится при создании договора в базе, здесь, только проверяем, что платеж будет по тому же договору, которые подтвердил пользователь.
        //1. проверить ext id договора, на соответствие профилю клиента.
        if (checkContrExtId(payMap, result, login, password)) {
            //2. если гвид платежа не пришел с формы, создать плановый платеж. c указанной суммой в плановый платеж сгенерить код. гвид например.

            Map<String, Object> planPayMap = planPaymentCreate(payMap, login, password);
            Map<String, Object> factPayMap = createDraftPayFact(payMap, planPayMap, login, password);

            boolean isSkipMerchantCall = getBooleanParam(params.get(IS_SKIP_MERCHANT_CALL), false);
            if (isSkipMerchantCall) {
                result.put("PLANPAYMAP", planPayMap);
                result.put("FACTPAYMAP", factPayMap);
                return result;
            }

            // TODO: когда попросят повтор оплаты,если по гвиду удалось получить плановый платеж,
            // TODO: попытаться получить фактические платежи, по коду планового платежа.
            // TODO: если есть факт в черновике, с таким кодом, и время жизни ссылки эквайринга не закончилось - выдать наружу ссылку.
            // TODO: если время жизни ссылки закончилось - удалить черновик факт платежа, и создать новый.

            // если создали новый факт платеж, зарегистрировать платеж в эквайринге.
            SberPayService sps = new SberPayService(this.getModuleName());
            String merchantOrderNumber = getStringParam(planPayMap.get("EXTERNALID"));

            // платеж в эквайринге регистрировать, ссылку завершения дополнить хешем планового платежа.

            // сделать метод, с формы по хешу план платежа получающий список платежей черновиков, проверяющих состояние в эквайринге, и переводящих их в "Успех".
            // возвращающий результат проверки на форму.

            // в планировщике в цикле проверять все факт платежи в состоянии черновик не старше 2х дней, при успехе переводить в "Оплачен"

            // обновить факт платеж, сохранив ссылку, и ид операции от эквайринга.
            // выдать наружу ссылку эквайринга.

            //3. создать фактический платеж черновик. сохранив в него ид от эквайринга,

            //Long prodConfID = getLongParam(payMap, "PRODCONFID");
            // ИД самого договора
            String contrId = payMap.get("contrId").toString();
            Long contrIDLong = getLongParamLogged(payMap, "contrId"); // для протокола
            // ИД секции договора (если передан - оплачивается конкретная секция договора)
            Long contrSectionID = getLongParamLogged(payMap, "CONTRSECTIONID");
            String contrNodeId = payMap.get("CONTRNODEID").toString();
            String contrNum = getStringParam(payMap.get("CONTRNUMBER"));

//            String description = "Оплата взноса по договору страхования " + contrNum;
            // В соответствии с задачей 22638
            String description = contrNum;
            if (payMap.get("premValue") != null) {
                boolean isMobile = false;
                if ((payMap.get("isMobile") != null) && ("TRUE".equalsIgnoreCase(payMap.get("isMobile").toString()))) {
                    isMobile = true;
                }

                //String urlPrefix = formUrlPrefix(payMap);
                String urlSuccess;
                String urlFail;
                String url = getStringParam(payMap.get("CALLBAKURI"));
                if (url.isEmpty()) {
                    /*
                    // todo: уточнить константы
                    urlSuccess = "https://sberbankins.ru";
                    urlFail = "https://sberbankins.ru";
                    */
                    // теперь могут быть переданы через специальные параметры
                    urlSuccess = getStringOptionalParamLogged(payMap, URL_PAY_SUCCESS_PARAMNAME, URL_PAY_DEFAULT);
                    urlFail = getStringOptionalParamLogged(payMap, URL_PAY_FAIL_PARAMNAME, URL_PAY_DEFAULT);
                } else {
                    String cutUrl = url.substring(0, url.indexOf(".html") + 5);
                    String urlPostfix = "&hash=" + base64Encode(merchantOrderNumber) + "&SESSIONID=" + getStringParam(params.get("SESSIONIDFORCALL"));
                    urlSuccess = cutUrl + "#/additionalAgreement/successPayment?status=SUCCESS" + urlPostfix + "&sumPayment=" + getStringParam(params.get("SUMPAYMENT")); //urlPrefix + "/bivsberlossws/html/bivhabsber/app/index.html#/paymentSucces" + urlPostfix + n руб/usd;
                    urlFail = cutUrl + "#/additionalAgreement/successPayment?status=FAIL" + urlPostfix; //urlPrefix + "/bivsberlossws/html/bivhabsber/app/index.html#/paymentFail" + urlPostfix;
                }
                String email = getStringParam(payMap.get("EMAIL"));
                Double premValue = Double.valueOf(payMap.get("premValue").toString());
                Long longPremVal = Math.round(premValue * 100);
                logger.debug(String.format("Try call sberPayService: contrId-%s, merchant-%s, premVal-%s, isMobile-%s, urlSuccess-%s, urlFail-%s",
                        contrId, merchantOrderNumber, longPremVal, isMobile, urlSuccess, urlFail));
                try {
//                    // перед оплатой - получаем список ранее сделанных попыток.
//                    // получение записей о фактической оплате по договору без учета секции (если не указан ИД секции) или по конкретной секции договора (если указан ИД секции)
//                    List<Map<String, Object>> payFactList = getPayFactList(getLongParam(contrNodeId), contrSectionID, login, password);
//                    boolean isNeedCheckAlreadyPayd = false;
//                    if (payFactList != null) {
//                        if (!payFactList.isEmpty()) {
//                            // если есть попытки - проверяем не были ли они оплачены ранее.
//                            isNeedCheckAlreadyPayd = true;
//                        }
//                    }
//                    logger.debug("isNeedCheckAlreadyPayd = " + isNeedCheckAlreadyPayd);
//                    boolean isAlreadyPaid = false;
//                    if (isNeedCheckAlreadyPayd) {
//                        for (Map<String, Object> payFactMap : payFactList) {
//                            String code = getStringParam(payFactMap.get("CODE"));
//                            String payStatus = getStringParam(payFactMap.get("STATESYSNAME"));
//                            if ("draft".equalsIgnoreCase(payStatus)) {
//                                Map<String, Object> payRes = checkPaymentStatusByMerchantOrderNum(code,false, login, password);
//                                Map<String, Object> res1 = getMerchantResult(payRes);
//                                if (checkMerchantResult(res1)) {
//                                    isAlreadyPaid = true;
//                                }
//                            } else {
//                                isAlreadyPaid = true;
//                            }
//                        }
//                    }
//                    logger.debug("isAlreadyPaid = " + isAlreadyPaid);
//                    if (isAlreadyPaid) {
//                        Map<String, Object> fictivePayRes = new HashMap<String, Object>();
//                        // если были оплачены - то formurl = paymentsuccess и новая оплата не регистрируется.
//                        fictivePayRes.put("FORMURL", urlSuccess);
//                        result.put("payRes", fictivePayRes);
//                    } else {
                    // если оплачен не был - то регистрим новый, и унрегистрим старый.
//                        boolean hasValidUrl = false;
//                        String oldFormUrl = "";
//                        if (payFactList != null) {
//                            for (Map<String, Object> payFactMap : payFactList) {
//                                oldFormUrl = getStringParam(payFactMap.get("FORMURL"));
//                                if (!oldFormUrl.isEmpty()) {
//                                    //0. если formurl заполнен, то
//                                    Date payFactDate = getDateParam(payFactMap.get("PAYFACTDATE"));
//                                    GregorianCalendar gcPayFactDate = new GregorianCalendar();
//                                    gcPayFactDate.setTime(payFactDate);
//                                    gcPayFactDate.add(Calendar.MINUTE, 20);
//                                    Date payFactFinishDate = gcPayFactDate.getTime();
//                                    Date now = new Date();
//                                    if (payFactFinishDate.getTime() > now.getTime()) {
//                                        //1. проверяем дату платежа. сравниваем стекущей. если не прошло 20 минут, то новый запрос не делаем, возвращаем formurl из платежа
//                                        // сейчас доступ для разрегистрации покупки запрещен.
//                                        hasValidUrl = true;
//                                        break;
//                                    }
//                                    //Map<String, Object> unregRes = callUnregisterPayService(sps, payFactMap, contrNodeId, contrId, premValue, login, password);
//                                    //logger.debug("unregister previous order with res " + unregRes);
//                                }
//                            }
//
//                        }
//                        logger.debug("hasValidUrl = " + hasValidUrl);
//                        // если нет действующего урла страницы оплаты. (ссылка действует 20 минут)
//                        if (hasValidUrl && !oldFormUrl.isEmpty()) {
//                            Map<String, Object> fictivePayRes = new HashMap<String, Object>();
//                            fictivePayRes.put("FORMURL", oldFormUrl);
//                            fictivePayRes.put("ERRORCODE", 0L); // требуется код отсутствия ошибки, может проверяться (например, в b2bposservice#dsB2BMobileRestPayContract)
//                            result.put("payRes", fictivePayRes);
//                        } else {

                    //1. создаем факт платеж в статусе черновик.
                    //2. в платеж создаем уникальный код
//                            if (contrSectionID == null) {
//                                // если ИД секции договора не передан - оплачивается договор либо без учета секции, либо без конкретного указания оплачиваемой секции
//                                // однако, если у договора одна единственная секция, то оплачивается именно эта конкретная единственная секция договора
//                                Map<String, Object> sectionParams = new HashMap<String, Object>();
//                                sectionParams.put("CONTRID", contrIDLong);
//                                Map<String, Object> contrSectionListRes = callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractSectionBrowseListByParam", sectionParams, login, password);
//                                if (isCallResultOK(contrSectionListRes)) {
//                                    List<Map<String, Object>> contrSectionList = getListFromResultMap(contrSectionListRes);
//                                    if ((contrSectionList != null) && (contrSectionList.size() == 1)) {
//                                        // у договора одна единственная секция, значит оплачивается именно эта конкретная единственная секция договора
//                                        Map<String, Object> contrSectionSingle = contrSectionList.get(0);
//                                        contrSectionID = getLongParam(contrSectionSingle, "CONTRSECTIONID");
//                                    }
//                                }
//                            }
//
                    // создание черновика записи о фактической оплаты по договору без учета секции (если не указан ИД секции) или по конкретной секции договора (если указан ИД секции)
//                            Map<String, Object> draftPayFact = createDraftPayFact(contrNodeId, contrId, contrSectionID, premValue, login, password);
                    //String paymentcode = getStringParam(factPayMap.get("CODE"));
                    String paymentcode = getStringParam(planPayMap.get("PAYID")) + getStringParam(factPayMap.get("PAYFACTID"));
                    Date payDate = new Date();
                    // Региструрем событие вызова SPS
                    Event spsCallEvent = new Event(params)
                            .setEventType(PAYMENT_REGISTRATION_EVENT_SYSNAME)
                            .setObjId(newPaymentEventId)
                            .setErrorCode("0")
                            .setParam("paramStr1", paymentcode)
                            .setParam("paramStr2", description)
                            .setParam("paramStr3", email)
                            .setParam("paramInt1", longPremVal)
                            .setParam("paramBoolean1", isMobile ? 1 : 0)
                            .setErrorText("Регистрация платежа через PayService, код события платежа = " + newPaymentEventId.toString());
                    Map<String, Object> spsCallEventMap = spsCallEvent.persist();
                    Long spsCallEventId = getLongParam(spsCallEventMap, "id");
                    Map<String, Object> payRes = sps.sberPayServiceRegisterOrder(paymentcode, description, longPremVal, isMobile, urlSuccess, urlFail, email);
                    logger.debug("sberPayService result =" + payRes.toString());
                    Event spsCallResultEvent = new Event(payRes)
                            .loadCredentials(params) // логин с паролем возьмем из предыдущего события
                            .setEventType(PAYMENT_REGISTRATION_EVENT_SYSNAME)
                            .setErrorText("Результат вызова PayService, код события вызова = " + spsCallEventId.toString())
                            .postProcess(new SBSresultPostProcessor())
                            .setObjId(spsCallEventId);
                    result.put("payRes", payRes);
                    String orderId = null;
                    if ("0".equals(payRes.get("ERRORCODE").toString())) {
                        orderId = payRes.get("ORDERID").toString();
                        factPayMap.put("NOTE", paymentcode);
                        //factPayMap.put("NOTE", description);
                        factPayMap.put("ORDERID", orderId);
                        factPayMap.put("FORMURL", payRes.get("FORMURL"));
                        // проставляем в платеж orderid в дальнейшем потребуется для отката заявки
                        this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactUpdate", factPayMap, login, password);
                        spsCallResultEvent.persist();
                    } else {
                        payRes.put("FORMURL", urlFail + "&errorMessage=" + payRes.get("ERRORMESSAGE").toString());
                    }
//                            Map<String, Object> paramsLog = new HashMap<String, Object>();
//                            paramsLog.put("AMOUNT", payMap.get("premValue"));
//                            paramsLog.put("CREATEDATE", new Date());
//                            paramsLog.put("ERRORCODE", payRes.get("ERRORCODE").toString());
//                            paramsLog.put("ERRORTEXT", payRes.get("ERRORMESSAGE").toString());
//                            paramsLog.put("MERCHANTORDERNUM", payMap.get("guid"));
//                            paramsLog.put("OBJECTID", payMap.get("contrId"));
//                            //paramsLog.put("SECONDARYOBJECTID", contrSectionID); // возможно, потребуется в дальнейшем (но тогда нужно будет править insert-запрос в dsPayLogCreate или заменять на B2B-аналог и пр.)
//                            paramsLog.put("ORDERID", orderId);
//                            paramsLog.put("PAYDATE", new Date());
//                            paramsLog.put("URLFAIL", urlFail);
//                            paramsLog.put("URLSUCCES", urlSuccess);
//
////                            this.callService(BIVPOS_SERVICE_NAME, "dsPayLogCreate", paramsLog, login, password);
//
//                            if ("0".equals(payRes.get("ERRORCODE").toString())) {
//                                // сохраним платежный документ
//                                Map<String, Object> payParams = new HashMap<String, Object>();
//                                payParams.put("CONTRID", contrId);
//                                payParams.put("CONTRSECTIONID", contrSectionID);
//                                payParams.put("EXTERNALID", orderId);
//                                payParams.put("AMOUNT", payMap.get("premValue"));
//                                payParams.put("PAYDATE", payDate);
//                                xmlUtil.convertDateToFloat(payRes);
//                                this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentCreate", payParams, login, password);
//                            }
                    //   }
                    // }
                } catch (Exception e) {
                    logger.debug("sberPayService exception", e);
                }
            }
        } else {
            logger.debug("sberPayService extId or smsCode invalid");
            result.put("STATUS", "CHECKERROR");
        }
        //2. дернуть сервис оплаты

        //3. сохранить в лог результат работы сервиса.
        //4. венруть из сервиса путь странички завершения оплаты paymentSuccess или paymentFail. должно вернуться из сервиса оплаты
        /* Map<String, Object> paramsLog = new HashMap<String, Object>();
         paramsLog.put("AMOUNT", payMap.get("premValue"));
         paramsLog.put("CREATEDATE", new Date());
         paramsLog.put("ERRORCODE", "");
         paramsLog.put("ERRORTEXT", "");
         paramsLog.put("MERCHANTORDERNUM", payMap.get("guid"));
         paramsLog.put("OBJECTID", payMap.get("contrId"));
         paramsLog.put("ORDERID", "");
         paramsLog.put("PAYDATE", new Date());
         paramsLog.put("URLFAIL", "");
         paramsLog.put("URLSUCCES", "");

         Map<String, Object> resLog = this.callService(BIVPOS_SERVICE_NAME, "dsPayLogCreate", paramsLog, login, password);*/
        // result.put("log", paramsLog);
        return result;
    }

    private Map<String, Object> planPaymentCreate(Map<String, Object> payMap, String login, String password) throws Exception {
        Map<String, Object> payParams = new HashMap<String, Object>();
        payParams.put("CONTRID", payMap.get("CONTRID"));
        payParams.put("EXTERNALID", UUID.randomUUID().toString());
        payParams.put("AMOUNT", payMap.get("premValue"));
        payParams.put("PAYDATE", new Date());
        payParams.put("ReturnAsHashMap", "TRUE");
        xmlUtil.convertDateToFloat(payParams);
        Map<String, Object> payRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentCreate", payParams, login, password);
        payParams.put("PAYID", payRes.get("PAYID"));
        return payParams;
    }

    private Map<String, Object> planPaymentUpdate(Map<String, Object> payParams, String login, String password) throws Exception {
        xmlUtil.convertDateToFloat(payParams);
        return this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentUpdate", payParams, login, password);
    }


    /**
     * вызвать сервис оплаты. с валидацией по смс коду
     *
     * @param params <UL>
     *               <LI>PAYMENTMAP - map параметров с формы</LI>
     *               </UL>
     * @return <UL>
     * <LI>STATUS - статус</LI>
     * <LI>SMSVALID - смс код верен</LI>
     * <LI>EXTIDVALID - guid договора верен</LI>
     * </UL>
     * @author averichevsm
     */
    @WsMethod()
    public Map<String, Object> dsCallCheckPaymentService(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
//PAYMENTMAP
        Map<String, Object> payMap = params;
        String hash = payMap.get("hash").toString();

        result.put("STATUS", "INPROCESS");
        // String hash = params.get("hash").toString();
        String code = base64Decode(hash);
        params.put("CODE", code);
        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> paramhb = new HashMap<String, Object>();
        paramhb.put("EXTERNALID", code);
        paramhb.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactBrowseListByParam", params, login, password);
        if (res.get("STATESYSNAME") != null) {
            if (res.get("CONTRNODEID") != null) {
                Map<String, Object> contrParam = new HashMap<>();
                contrParam.put("CONTRNODEID", res.get("CONTRNODEID"));
                contrParam.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> contrRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParam", contrParam, login, password);
                if (contrRes.get("CONTRNUMBER") != null) {
                    res.put("CONTRNUMBER", contrRes.get("CONTRNUMBER"));
                }

            }
            result.put("PAYMENT", res);
            if (PAYFACT_STATE_SYSNAME_SUCCESS.equals(getStringParam(res.get("STATESYSNAME")))) {
                result.put("STATUS", "SUCCESS");
            }
            if (PAYFACT_STATE_SYSNAME_FAIL.equals(getStringParam(res.get("STATESYSNAME")))) {
                result.put("STATUS", "FAIL");
            }
        }
//        if (res.get("CONTRID") != null) {
//            String contrId = res.get("CONTRID").toString();
//            SberPayService sps = new SberPayService(this.getModuleName());
//            Map<String, Object> rawResult = sps.sberPayServiceGetOrderStatusExtended(code);
//            Map<String, Object> merchantResult = getMerchantResult(rawResult);
//            if (checkMerchantResult(merchantResult)) {
//                result.put("STATUS","SUCCESS");
//                result.put("PAYMENT", merchantResult);
//            }
//
//        }
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsCallCheckPaymentServiceById(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        if (params.get("CONTRID") != null) {
            String contrId = params.get("CONTRID").toString();
            SberPayService sps = new SberPayService(this.getModuleName());
            result = sps.sberPayServiceGetOrderStatusExtended(contrId);
        } else if (params.get("CONTRIDLIST") != null) {

            String contrId = params.get("CONTRIDLIST").toString();
            String[] contridList = contrId.split(",");
            for (String contridVal : contridList) {
                SberPayService sps = new SberPayService(this.getModuleName());
                result = sps.sberPayServiceGetOrderStatusExtended(contridVal);
                logger.debug("check contrid = " + contridVal);
                logger.debug(result.toString());
            }
        }
        return result;
    }

    /**
     * сервис для получения идОперации из системы оплаты. для всех договоров, в
     * платеже которых этот ид еще не проставлен. и обновления этого ид.
     *
     * @param params <UL>
     *               <LI>PAYMENTMAP - map параметров с формы</LI>
     *               </UL>
     * @return <UL>
     * <LI>STATUS - статус</LI>
     * <LI>SMSVALID - смс код верен</LI>
     * <LI>EXTIDVALID - guid договора верен</LI>
     * </UL>
     * @author averichevsm
     */
    @WsMethod()
    public Map<String, Object> dsCallMassCheckPaymentService(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        // 1. выбрать фактические платежи.
        Map<String, Object> pfparam = new HashMap<String, Object>();

        Map<String, Object> payFactRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentFactBrowseListByParamEx", pfparam, login, password);
        if (payFactRes.get(RESULT) != null) {
            List<Map<String, Object>> payFactList = (List<Map<String, Object>>) payFactRes.get(RESULT);
            for (Map<String, Object> payFact : payFactList) {
                //if (payFact.get("SERIES") == null) {
                String contrId = payFact.get("CONTRID").toString();
                logger.debug("begin " + contrId);
                SberPayService sps = new SberPayService(this.getModuleName());
                try {

                    result = sps.sberPayServiceGetOrderStatusExtended(contrId);
                } catch (Exception e) {
                    logger.debug("error " + contrId);
                    continue;
                }
                Map<String, Object> res1 = getMerchantResult(result);
                if (res1.get("ORDERID") != null) {
                    if (res1.get("REFERENCENUMBER") != null) {
                        logger.debug("REFERENCENUMBER " + res1.get("REFERENCENUMBER").toString());
                        logger.debug("payFactId " + payFact.get("PAYFACTID").toString());
                        Map<String, Object> payFactUpdParam = new HashMap<String, Object>();
                        payFactUpdParam.put("PAYFACTID", payFact.get("PAYFACTID"));
                        payFactUpdParam.put("PAYFACTNUMBER", res1.get("REFERENCENUMBER"));
                        XMLGregorianCalendar gc = (XMLGregorianCalendar) res1.get("DATE");
                        payFactUpdParam.put("PAYFACTDATE", gc.toGregorianCalendar().getTime());
                        payFactUpdParam.put("SERIES", contrId);

                        Map<String, Object> updRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentFactUpdate", payFactUpdParam, login, password);
                        logger.debug("updRes " + updRes.toString());

                    }
                }
                // }
            }
        }
        return result;
    }

    /**
     * сервис для проверки статуса оплаты договоров готовых к оплате
     * synchronized не использован т.к. сервис запускается раз в 10 минут.
     * запуск одновременно 2х потоков не возможен
     *
     * @param params <UL>
     *               <LI>PAYMENTMAP - map параметров с формы</LI>
     *               </UL>
     * @return <UL>
     * <LI>STATUS - статус</LI>
     * <LI>SMSVALID - смс код верен</LI>
     * <LI>EXTIDVALID - guid договора верен</LI>
     * </UL>
     * @author averichevsm
     */
    @WsMethod()
    public Map<String, Object> dsCheckContractsPaymentState(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (threadCount == 0) {
            threadCount = 1;
            try {
                logger.debug("dsCheckContractsPaymentState start");
                result = tryCheckContractsPaymentState(params);
            } finally {
                threadCount = 0;
            }
        } else {
            logger.debug("dsCheckContractsPaymentState alreadyRun");
        }
        return result;
    }

    /**
     * сервис для печати и отправки документов по sbol продуктам synchronized не
     * использован т.к. сервис запускается раз в 10 минут. запуск одновременно
     * 2х потоков не возможен
     *
     * @param params <UL>
     *               <LI>PAYMENTMAP - map параметров с формы</LI>
     *               </UL>
     * @return <UL>
     * <LI>STATUS - статус</LI>
     * <LI>SMSVALID - смс код верен</LI>
     * <LI>EXTIDVALID - guid договора верен</LI>
     * </UL>
     * dsSbolDocSend
     * @author averichevsm
     */
    @WsMethod()
    public Map<String, Object> dsSbolDocSend(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (sbolThreadCount == 0) {
            sbolThreadCount = 1;
            try {
                logger.debug("dsSbolDocSend start");
                result = dsTrySbolDocSend(params);
            } finally {
                sbolThreadCount = 0;
            }
        } else {
            logger.debug("dsSbolDocSend alreadyRun");
        }
        return result;
    }


    /**
     * сервис для переотправки почты на договорах где была ошибка отправки
     * synchronized не использован т.к. сервис запускается раз в 10 минут.
     * запуск одновременно 2х потоков не возможен
     *
     * @param params <UL>
     *               <LI>PAYMENTMAP - map параметров с формы</LI>
     *               </UL>
     * @return <UL>
     * <LI>STATUS - статус</LI>
     * <LI>SMSVALID - смс код верен</LI>
     * <LI>EXTIDVALID - guid договора верен</LI>
     * </UL>
     * @author averichevsm
     */
    @WsMethod()
    public Map<String, Object> dsResendPrintDoc(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (senderThreadCount == 0) {
            senderThreadCount = 1;
            try {
                logger.debug("dsResendPrintDoc start");
                result = dsTryResendPrintDoc(params);
            } finally {
                senderThreadCount = 0;
            }
        } else {
            logger.debug("dsResendPrintDoc alreadyRun");
        }
        return result;
    }


    /**
     * сервис для переотправки почты на договорах где была ошибка отправки
     * synchronized не использован т.к. сервис запускается раз в 10 минут.
     * запуск одновременно 2х потоков не возможен
     *
     * @param params <UL>
     *               <LI>PAYMENTMAP - map параметров с формы</LI>
     *               </UL>
     * @return <UL>
     * <LI>STATUS - статус</LI>
     * <LI>SMSVALID - смс код верен</LI>
     * <LI>EXTIDVALID - guid договора верен</LI>
     * </UL>
     * @author averichevsm
     */
    @WsMethod()
    public Map<String, Object> dsSendCopyPrintDoc(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (senderCopyThreadCount == 0) {
            senderCopyThreadCount = 1;
            try {
                logger.debug("dsSendCopyPrintDoc start");
                result = dsTrySendCopyPrintDoc(params);
            } finally {
                senderCopyThreadCount = 0;
            }
        } else {
            logger.debug("dsSendCopyPrintDoc alreadyRun");
        }
        return result;
    }


    @WsMethod()
    public Map<String, Object> dsSendEmailByContract(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        boolean isb2bflag = isB2BMode(params);
        Map<String, Object> pfparam = new HashMap<String, Object>();
        String contrService = BIVSBERPOSWS_SERVICE_NAME;
        String browseContrMethod = "dsContractBrowseListByParamSenderTest";
        pfparam.put("NEEDDISTINCT", "true");
        pfparam.put("EMAIL", params.get("EMAIL"));
        Map<String, Object> contrRes = this.callService(contrService, browseContrMethod, pfparam, login, password);
        logger.debug("start process EMAIL SEND FAIL contracts");
        if (contrRes.get(RESULT) != null) {
            List<Map<String, Object>> contrList = (List<Map<String, Object>>) contrRes.get(RESULT);
            int count = contrList.size();
            if (isb2bflag) {
                logger.debug("is B2B enabled");
            } else {
                logger.debug("is B2B disabled");
            }
            logger.debug("contr count = " + String.valueOf(count));
            int i = 0;
            for (Map<String, Object> contrMap : contrList) {
                if (contrMap.get("CONTRID") != null) {
                    i++;
                    logger.debug("process " + String.valueOf(i) + " of " + String.valueOf(count) + " with id = " + contrMap.get("CONTRID").toString());
                    Map<String, Object> paramSend = new HashMap<String, Object>();
                    paramSend.put("action", "sendEmail");
                    paramSend.put("CONTRID", contrMap.get("CONTRID"));
                    if (isb2bflag) {
                        paramSend.put("USEB2B", "TRUE");
                    } else {
                        paramSend.put("USEB2B", "FALSE");
                    }
                    // для всех договоров вызываем переотправку.
                    // сервис переотправки сам сбросит email send fail если будет успех.
                    // возможно стоит предусмотреть возможность настройки, чтобы перед переотправкой удалялся привязанный bindoc, чтобы произошло
                    // перепечать полиса, и переподписание. (либо делать это вручную на нужных договорах)
                    //  this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsCallPringAndSendEx", paramSend, login, password);
                }
            }
        }

        return result;
    }


    private Map<String, Object> dsTryResendPrintDoc(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        boolean isb2bflag = isB2BMode(params);
        Map<String, Object> pfparam = new HashMap<String, Object>();
        String contrService = INSPOSWS_SERVICE_NAME;
        String browseContrMethod = "dsContractBrowseListByParamEx";
        pfparam.put("NEEDDISTINCT", "true");
        if (isb2bflag) {
            contrService = B2BPOSWS_SERVICE_NAME;
            pfparam.put("STATESYSNAMELIST", "'B2B_CONTRACT_SG','B2B_CONTRACT_PREPRINTING','B2B_CONTRACT_UPLOADED_SUCCESFULLY'");
            pfparam.put("CHECKEMAILVALID", "TRUE");
            browseContrMethod = "dsB2BContractBrowseListByParamEx";
        } else {
            pfparam.put("STATESYSNAMELIST", "'INS_CONTRACT_TO_PAYMENT','INS_CONTRACT_PAID','INS_CONTRACT_UPLOADED_SUCCESFULLY'");
        }
        pfparam.put("NOTE", "EMAIL SEND FAIL");
        Map<String, Object> contrRes = this.callService(contrService, browseContrMethod, pfparam, login, password);
        logger.debug("start process EMAIL SEND FAIL contracts");
        if (contrRes.get(RESULT) != null) {
            List<Map<String, Object>> contrList = (List<Map<String, Object>>) contrRes.get(RESULT);
            int count = contrList.size();
            if (isb2bflag) {
                logger.debug("is B2B enabled");
            } else {
                logger.debug("is B2B disabled");
            }
            logger.debug("contr count = " + String.valueOf(count));
            int i = 0;
            for (Map<String, Object> contrMap : contrList) {
                if (contrMap.get("CONTRID") != null) {
                    i++;
                    logger.debug("process " + String.valueOf(i) + " of " + String.valueOf(count) + " with id = " + contrMap.get("CONTRID").toString());
                    Map<String, Object> paramSend = new HashMap<String, Object>();
                    paramSend.put("action", "sendEmail");
                    paramSend.put("CONTRID", contrMap.get("CONTRID"));
                    if (isb2bflag) {
                        paramSend.put("USEB2B", "TRUE");
                    } else {
                        paramSend.put("USEB2B", "FALSE");
                    }
                    // для всех договоров вызываем переотправку.
                    // сервис переотправки сам сбросит email send fail если будет успех.
                    // возможно стоит предусмотреть возможность настройки, чтобы перед переотправкой удалялся привязанный bindoc, чтобы произошло
                    // перепечать полиса, и переподписание. (либо делать это вручную на нужных договорах)
                    this.callService(SIGNBIVSBERPOSWS_SERVICE_NAME, "dsCallPringAndSendEx", paramSend, login, password);
                }
            }
        }
        // аналогичный функционал надо вызвать для подписанных договоров, по которым отсутствует прикрепленный подписанный полис.
        Map<String, Object> pfparam1 = new HashMap<String, Object>();
        pfparam1.put("NEEDDISTINCT", "true");
        pfparam1.put("ISB2BFLAG", isb2bflag);
        contrRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsContractBrowseListByParamForReprintAndResend", pfparam1, login, password);
        logger.debug("start process contracts with print form error");
        if (contrRes.get(RESULT) != null) {
            List<Map<String, Object>> contrList = (List<Map<String, Object>>) contrRes.get(RESULT);
            int count = contrList.size();
            if (isb2bflag) {
                logger.debug("is B2B enabled");
            } else {
                logger.debug("is B2B disabled");
            }
            logger.debug("contr count = " + String.valueOf(count));
            int i = 0;
            for (Map<String, Object> contrMap : contrList) {
                if (contrMap.get("CONTRID") != null) {
                    i++;
                    logger.debug("process " + String.valueOf(i) + " of " + String.valueOf(count) + " with id = " + contrMap.get("CONTRID").toString());
                    Map<String, Object> paramSend = new HashMap<String, Object>();
                    paramSend.put("action", "sendEmail");
                    paramSend.put("CONTRID", contrMap.get("CONTRID"));
                    if (isb2bflag) {
                        paramSend.put("USEB2B", "TRUE");
                    } else {
                        paramSend.put("USEB2B", "FALSE");
                    }
                    // для всех договоров вызываем переотправку.
                    // сервис переотправки сам сбросит email send fail если будет успех.
                    // возможно стоит предусмотреть возможность настройки, чтобы перед переотправкой удалялся привязанный bindoc, чтобы произошло
                    // перепечать полиса, и переподписание. (либо делать это вручную на нужных договорах)
                    this.callService(SIGNBIVSBERPOSWS_SERVICE_NAME, "dsCallPringAndSendEx", paramSend, login, password);
                }
            }
        }
        return result;
    }

    private String findProdDefValByName(List<Map<String, Object>> prodDefValList, String name) {
        for (Map<String, Object> bean : prodDefValList) {
            if ((bean.get("NAME") != null) && (bean.get("NAME").toString().equalsIgnoreCase(name))) {
                if (bean.get("VALUE") != null) {
                    return bean.get("VALUE").toString();
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private void calcContractStartDate(Long productVersionID, Map<String, Object> rawContract, String
            login, String password) throws Exception {
        Map<String, Object> prodConfParams = new HashMap<String, Object>();
        prodConfParams.put(RETURN_AS_HASH_MAP, "TRUE");
        prodConfParams.put("PRODVERID", productVersionID);
        Map<String, Object> prodConfRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", prodConfParams, login, password);
        if (prodConfRes.get("PRODCONFID") != null) {
            Map<String, Object> prodDefValParams = new HashMap<String, Object>();
            prodDefValParams.put("PRODCONFID", prodConfRes.get("PRODCONFID"));
            Map<String, Object> prodDefValRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueBrowseListByParam", prodDefValParams, login, password);
            if ((prodDefValRes != null) && (prodDefValRes.get(RESULT) != null)) {
                List<Map<String, Object>> prodDefValList = (List<Map<String, Object>>) prodDefValRes.get(RESULT);
                if (prodDefValList != null) {
                    String sdCalcMethod = findProdDefValByName(prodDefValList, "SDCALCMETHOD");
                    // считаем дату для "С даты заключения договора", "Через % дней с даты заключения договора"
                    if ((sdCalcMethod != null) && (sdCalcMethod.equals("3") || sdCalcMethod.equals("5"))) {
                        Map<String, Object> calcParams = new HashMap<String, Object>();
                        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
                        calcParams.put("DOCUMENTDATE", parseAnyDate(rawContract.get("DOCUMENTDATE"), Double.class, "DOCUMENTDATE"));
                        calcParams.put("SIGNDATE", parseAnyDate(rawContract.get("SIGNDATE"), Double.class, "SIGNDATE"));
                        calcParams.put("SDCALCMETHOD", sdCalcMethod);
                        calcParams.put("SDLAG", findProdDefValByName(prodDefValList, "SDLAG"));
                        calcParams.put("SDCALENDARTYPE", findProdDefValByName(prodDefValList, "SDCALENDARTYPE"));
                        Map<String, Object> qRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractCalcStartDate", calcParams, login, password);
                        if ((qRes != null) && (qRes.get("STARTDATE") != null)) {
                            rawContract.put("STARTDATE", qRes.get("STARTDATE"));
                        }
                    }
                }
            }
        }
    }

    //сервис проверки оплаты доработан по требованиям сбсж в декабре 2017
    private Map<String, Object> tryCheckContractsPaymentState(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        logger.debug("begin tryCheckContractsPaymentState");
        GregorianCalendar gc = new GregorianCalendar();
        Date nowDate = gc.getTime();

        gc.add(GregorianCalendar.HOUR_OF_DAY, -6);
        Date less6hourDate = gc.getTime();
        gc.add(GregorianCalendar.HOUR_OF_DAY, -6);
        Date less12hourDate = gc.getTime();
        gc.add(GregorianCalendar.HOUR_OF_DAY, -6);
        Date less18hourDate = gc.getTime();
        gc.add(GregorianCalendar.HOUR_OF_DAY, -6);
        Date less24hourDate = gc.getTime();
//        gc.add(GregorianCalendar.DAY_OF_MONTH, -1);
        Date lessDayDate = gc.getTime();
        gc.add(GregorianCalendar.MINUTE, -5);
        gc.add(GregorianCalendar.DAY_OF_MONTH, -1);
        Date less2DayDate = gc.getTime();
        logger.debug("begin dsCheckContractsPaymentState");
        Map<String, Object> result = new HashMap<String, Object>();

        List<Map<String, Object>> payFactList = getPayFactList4Check(login, password);
        if (payFactList != null) {
            if (!payFactList.isEmpty()) {
                for (Map<String, Object> payFactMap : payFactList) {
                    Date payDate = getDateParam(payFactMap.get("PAYFACTDATE"));
                    final Long payFactType = getLongParam(payFactMap.get("PAYFACTTYPE"));
                    if (payFactType != null) {
                        final Integer payFactTypeInt = payFactType.intValue();
                        if (payFactTypeInt.equals(3)) {
                            final String payFactNumber = getStringParam(payFactMap.get("NOTE"));
                            final String shopId = Config.getConfig(this.getModuleName()).getParam("YandexKassaAPICallerShopId", "");
                            final String secretKey = Config.getConfig(this.getModuleName()).getParam("YandexKassaAPICallerSecretKey", "");
                            final String orderId = String.valueOf(payFactMap.get("ORDERID"));
                            final CheckPaymentRequest request = new CheckPaymentRequest(shopId, secretKey, orderId);
                            final Map<String, Object> rawResult = request.make();
                            if (rawResult.get("ERRORCODE").equals("0") && rawResult.get("PAYMENTSTATUS").equals("succeeded")) {
                                payFactMap.put("PAYFACTDATE", new Date());
                                payFactMap.put("STATESYSNAME", PAYFACT_STATE_SYSNAME_SUCCESS);
                                payFactMap.put("NOTE", payFactNumber);
                                this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactUpdate", payFactMap, login, password);
                            } else {
                                if (payDate.before(less24hourDate)) {
                                    payFactMap.put("STATESYSNAME", PAYFACT_STATE_SYSNAME_FAIL);
                                    payFactMap.put("NOTE", "Платеж просрочен");
                                    this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactUpdate", payFactMap, login, password);
                                }
                            }
                            continue;
                        } else if (payFactTypeInt.equals(4)) {
                            continue;
                        }
                    }
                    // платеж зарегистрирован менее суток назад.
                    // проверяем оплату
                    //1/ проверить состояние платежа.
                    SberPayService sps = new SberPayService(this.getModuleName());
                    String code = getStringParam(payFactMap.get("CODE"));
                    String payFactNumber = getStringParam(payFactMap.get("NOTE"));
                    Map<String, Object> merchantResult = null;
                    try {
                        Map<String, Object> rawResult = sps.sberPayServiceGetOrderStatusExtended(payFactNumber);
                        merchantResult = getMerchantResult(rawResult);
                    } catch (Exception e){
                        logger.error("Error call merchant services", e);
                    }
                    if (checkMerchantResult(merchantResult)) {
                        // годный ответ возвращен из SberPayService с поддрежкой получения параметров мерчанта из сведений продукта

                        logger.debug("payment success by code " + code);
                        //Map<String, Object> payParams = new HashMap<String, Object>();
                        payFactMap.put("PAYFACTNUMBER", merchantResult.get("REFERENCENUMBER"));//ORDERID_PARAM_NAME));// guid);
                        payFactMap.put("PAYFACTTYPE", 1);
                        payFactMap.put("PAYFACTDATE", ((XMLGregorianCalendar) merchantResult.get("DATE")).toGregorianCalendar().getTime());
                        payFactMap.put("STATESYSNAME", PAYFACT_STATE_SYSNAME_SUCCESS);
                        payFactMap.put("NOTE", payFactNumber);
                        this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactUpdate", payFactMap, login, password);
                        //resultSuccess = new HashMap<String, Object>();
                    } else {
                        if (payDate.before(less24hourDate)) {
                            // платеж в базе в черновике, зарегистирован более суток
                            // но на текущий момент до сих пор не оплачен.
                            // переводим платеж в состояние неуспеха
                            payFactMap.put("STATESYSNAME", PAYFACT_STATE_SYSNAME_FAIL);
                            payFactMap.put("NOTE", "Платеж просрочен");
                            this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactUpdate", payFactMap, login, password);

                        }
                    }
                }
            }
        }
        return result;
    }

    private List<Map<String, Object>> getPayFactList4Check(String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("STATESYSNAME", PAYFACT_STATE_SYSNAME_DRAFT);
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactBrowseListByParamEx", params, login, password);
        if (res.get(RESULT) != null) {
            List<Map<String, Object>> result = (List<Map<String, Object>>) res.get(RESULT);
            return result;
        }
        return null;
    }

    private boolean checkContrExtId(Map<String, Object> params, Map<String, Object> result, String
            login, String password) throws Exception {
        // по фт никаких проверок смс нет.
//        String smsCodeEnteredByUser = "";
//        Boolean checkFactPay = false;
//        if (params.get("smsCode") != null) {
//            smsCodeEnteredByUser = params.get("smsCode").toString();
//        } else {
//            // если смс код не пришел. - возможно это повторная оплата, тогда пользователь уже проходил данную проверку. убедится
//            // что оплата повторная можно по наличию фактического платежа в статусе draft
//            checkFactPay = true;
//        }
        String guid;
        if (params.get("hash") != null) {
            String hash = params.get("hash").toString();
            guid = base64Decode(hash);
        } else {
            guid = getStringParam(params.get("EXTERNALID"));
        }
        boolean isCurrencyAndRateValid = false;


        HashMap<String, Object> contractParams = new HashMap<String, Object>();
        contractParams.put("EXTERNALID", guid);
        logger.debug(String.format("Calling dsB2BContractBrowseListByParam with params %s...", contractParams));
        Map<String, Object> contractBriefInfoRes = callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParam", contractParams, login, password);
        List<Map<String, Object>> contractBriefInfoList = WsUtils.getListFromResultMap(contractBriefInfoRes);
        if ((contractBriefInfoList != null) && (contractBriefInfoList.size() == 1)) {
            Map<String, Object> contractBriefInfo = contractBriefInfoList.get(0);
            Long contrId = getLongParam(contractBriefInfo, "CONTRID");

            Map<String, Object> clientParams = new HashMap<>();
            String clientProfileId = getStringParam(params.get("clientProfileId"));
            clientParams.put("id", clientProfileId);
            Map<String, Object> clientRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BClientProfileBrowseListByParamEx", clientParams, login, password);
            if (clientRes.get(RESULT) != null) {
                List<Map<String, Object>> clientResList = (List<Map<String, Object>>) clientRes.get(RESULT);
                if (clientResList.isEmpty()) {
                    logger.error("Ошибка получения профиля клиента 0 " + clientProfileId);
                    return false;
                }
                clientRes = clientResList.get(0);
                // TODO: при необходимости добавить выбор email из профиля.
            }
            String clientId = getStringParam(clientRes.get("clientId"));
            if (clientId != null && !clientId.isEmpty()) {
                Map<String, Object> shareContrParams = new HashMap<>();
                shareContrParams.put("contractId", contrId);
                shareContrParams.put("clientId", clientId);
                Map<String, Object> shareRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractAttachBrowseListByParamEx", shareContrParams, login, password);
                if (shareRes.get(RESULT) != null) {
                    List<Map<String, Object>> shareContrList = (List<Map<String, Object>>) shareRes.get(RESULT);
                    if (shareContrList.isEmpty()) {
                        logger.error("По указанному клиенту " + clientId + " c профилем " + clientProfileId + " нет договора " + contrId + ", по которому осуществлен переход на оплату ");
                        return false;
                    }
                }
            } else {
                logger.error("Ошибка получения данных профиля клиента 1 " + clientProfileId);
                boolean isSkipClientProfileCheck = getBooleanParam(params.get("ISSKIPCLIENTPROFILECHECK"), false);
                if (!isSkipClientProfileCheck) {
                return false;
            }
            }

            params.put("guid", guid);
            //проставить премию

            // проверка валюты и курса, при необходимости - вычисление премии в рублях
            logger.debug("Проверка валюты премии договора...");
            Double roundedPremValueInRub = null;
            // пока всегда рубли. до разъяснений.
            Long premCurrencyID = getLongParam(contractBriefInfo.get("PREMCURRENCYID"));
            String errorText = "Неизвестная ошибка при проверке валюты премии договора";
            if (premCurrencyID != null) {
                logger.debug("Валюта премии договора (PREMCURRENCYID) = " + premCurrencyID);
                if (premCurrencyID.intValue() == 1) {
                    logger.debug("Валюта премии договора - рубли, проверка курса и перевод премии в рубли не требуется.");
                    roundedPremValueInRub = getDoubleParam(params.get("PAYMENT"));
                    logger.debug("Премия по договору (PREMVALUE) в рублях = " + roundedPremValueInRub);
                    isCurrencyAndRateValid = true;
                } else {
                    logger.debug("Валюта премии договора - не рубли, проверка наличия корректного курса валют в договоре...");

                    Double currencyRate;
                    if (IS_CURRENCY_DEBUG) {
                        currencyRate = IS_CURRENCY_DEBUG_VALUE;
                        logger.debug("Курс валюты договора (CURRENCYRATE) взят из отладки = " + currencyRate);
                    } else {
                        currencyRate = getExchangeCourceByCurID(premCurrencyID, new Date(), login, password);
                        logger.debug("Курс валюты договора (CURRENCYRATE) = " + currencyRate);
                    }

                    if (currencyRate > 0) {
                        Double premValueInContrCurrency = getDoubleParam(params.get("PAYMENT"));
                        logger.debug("Премия по договору (PREMVALUE) в валюте договора = " + premValueInContrCurrency);
                        roundedPremValueInRub = ((new BigDecimal(Double.valueOf(currencyRate * premValueInContrCurrency).toString())).setScale(2, RoundingMode.HALF_UP)).doubleValue();
                        logger.debug("Премия по договору (PREMVALUE) в рублях = " + roundedPremValueInRub);
                        isCurrencyAndRateValid = true;
                    } else {
                        errorText = "Некорректное значение курса валют для договора по сведениям из БД.";
                    }
                }
            } else {
                errorText = "Не удалось определить валюту договора по сведениям из БД.";
            }
            result.put("CURRVALID", isCurrencyAndRateValid);
            if (!isCurrencyAndRateValid) {
                logger.debug("Ошибка при проверке валюты премии договора: " + errorText);
                result.put("CURRINVALIDREASON", errorText);
            }


            params.put("premValue", roundedPremValueInRub);
            // 4 create draft payfact
            params.put("CONTRID", contractBriefInfo.get("CONTRID"));
            params.put("CONTRNODEID", contractBriefInfo.get("CONTRNODEID"));
            params.put("CONTRPOLSER", contractBriefInfo.get("CONTRPOLSER"));
            params.put("CONTRPOLNUM", contractBriefInfo.get("CONTRPOLNUM"));
            params.put("CONTRNUMBER", contractBriefInfo.get("CONTRNUMBER"));
            params.put("PRODUCTNAME", contractBriefInfo.get("PRODUCTNAME"));
            params.put("PRODUCTSYSNAME", contractBriefInfo.get("PRODUCTSYSNAME"));
            // ИД конфигурации продукта - потребуется позднее для чтения из продукта параметров для работы с мерчантом
            //params.put("PRODCONFID", contractBriefInfo.get("PRODCONFID"));

            if (contrId == null) {
                logger.error(String.format("Unable to get contract id from dsB2BContractBrowseListByParam result (%s)!", contractBriefInfo));
            } else {
                // используется далее (например в методе dsCallPaymentService)
                params.put("contrId", contrId);
            }
        } else {
            logger.error("Unable to get contract by dsB2BContractBrowseListByParam!");
        }

        // получение СМС-кода и внешнего идентификатора из контракта в БД
        Map<String, Object> contract;
        Object smsCodeFromDB;
        String stateSysName = "";
        boolean isStateValid = true;

        //по профилю клиента получить емайл, проверить, что данный договор соответствует текущему пользователю.

//        Map<String, Object> insurerMap = (Map<String, Object>) contract.get("INSURERMAP");
//        if (insurerMap != null) {
//            List<Map<String, Object>> contactList = (List<Map<String, Object>>) insurerMap.get("contactList");
//            if (contactList != null) {
//                if (!contactList.isEmpty()) {
//                    for (Map<String, Object> contactMap : contactList) {
//                        if ("PersonalEmail".equalsIgnoreCase(getStringParam(contactMap.get("CONTACTTYPESYSNAME")))) {
//                            params.put("EMAIL", contactMap.get("VALUE"));
//                        }
//                    }
//                }
//            }
//        }

        // возврат в явном виде только результата проверки, остальные сведения передаются из метода через params и result
        return isStateValid && isCurrencyAndRateValid;
    }

    private String formUrlPrefix(Map<String, Object> payMap) {
        StringBuilder sb = new StringBuilder();
        if ((payMap.get("protocol") != null)
                && (payMap.get("host") != null)) {
            sb.append(payMap.get("protocol").toString());
            sb.append("://");
            sb.append(payMap.get("host").toString());
            if (payMap.get("port") != null) {
                sb.append(":");
                sb.append(payMap.get("port").toString());
            }
        }
        return sb.toString();
    }

    private Map<String, Object> dsTrySbolDocSend(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Boolean isNeedTrans = true;
        Boolean isNeedEmail = true;
        if (params.get("ISNEEDTRANS") != null) {
            if ("FALSE".equalsIgnoreCase(params.get("ISNEEDTRANS").toString())) {
                isNeedTrans = false;
            }
        }
        if (params.get("ISNEEDEMAIL") != null) {
            if ("FALSE".equalsIgnoreCase(params.get("ISNEEDEMAIL").toString())) {
                isNeedEmail = false;
            }
        }
        //1. выбрать список договоров ограничив по:
        //  -продуктам t.prodverid in (2050,3000,3500,4000,13000,30000,31000,32000,33000,36000)
        //      Страхование путешественников СБОЛ        2050
        //      Защита карт СБОЛ                         3000
        //      Защита дома СБОЛ                         3500
        //      Страхование ипотеки СБОЛ                 4000
        //      Защита от клеща (Ростелеком)             13000
        //      Защита карт СБОЛ 2.0                     30000
        //      Защита дома СБОЛ 2.0                     31000
        //      Страхование ипотеки СБОЛ 2.0             32000
        //      Страхование путешественников СБОЛ 2.0    33000
        //      Страхование ВЗР Черехапа    36000
        //      Страхование ЖКХ СБОЛ                     37000
        //      Мобильная защита карт 2.0                41000
        //      Мобильная защита дома 2.0                42000
        //      Пролонгация ипотеки через ТМ 2.0         43000
        //      Комплексная ипотека 2.0                  45000
        //
        //  -состоянию B2B_CONTRACT_DRAFT t.stateid = 2000 (это состояние не обработанного еще договора.)
        Map<String, Object> contrParam = new HashMap<String, Object>();
        contrParam.put("PRODVERIDLIST", "2050,3000,3500,4000,13000,30000,31000,32000,33000,36000,37000,41000,42000,43000,45000");
        String prodverListToCreateNewLK = "37000";
        contrParam.put("STATESYSNAME", "B2B_CONTRACT_DRAFT");
        if (params.get("CONTRID") != null) {
            //contrParam.put("CONTRID", 506000);
            contrParam.put("CONTRID", params.get("CONTRID"));
        }

        contrParam.put("CHECKEXISTPROFRULE", "TRUE");
        Map<String, Object> contrRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamEx", contrParam, login, password);
        if (contrRes != null) {
            if (contrRes.get(RESULT) != null) {
                List<Map<String, Object>> contrList = (List<Map<String, Object>>) contrRes.get(RESULT);
                //2. бежим по списку договоров
                String countStr = String.valueOf(contrList.size());
                int i = 0;
                logger.debug("SBOL printNsend start: " + countStr + " contracts");
                for (Map<String, Object> contrMap : contrList) {
                    //3. для каждого договора осуществляем:2002
                    String productSysName = getStringParamLogged(contrMap, "PRODSYSNAME");
                    if ((contrMap.get("CONTRID") != null) && (!productSysName.isEmpty())) {
                        Object contrId = contrMap.get("CONTRID");
                        i++;
                        //4. проверяем, нужен ли факт оплаты для обработки договора (некоторые договоры по СБОЛ требуется обработать до оплаты)

                    } else {
                        logger.error("[dsTrySbolDocSend] Not enough data for processing this contract: " + contrMap);
                    }
                }
            }
        }
        //
        logger.debug("SBOL printNsend finish");
        return result;
    }


    private Map<String, Object> dsTrySendCopyPrintDoc(Map<String, Object> params) {
        Config config = Config.getConfig(SERVICE_NAME);
        // получить из конфига перечень продуктов, документы по которым подлежат пересылке
        String insProdVerListStr = config.getParam("SENDCOPYINSPRODVERS", "");
        String b2bProdVerListStr = config.getParam("SENDCOPYB2BPRODVERS", "");
        // получить из конфига перечень адресов электронной почты на которую отправлять документы.
        String addressListStr = config.getParam("SENDCOPYADDRESS", "");

        // получить список договоров ins_contr по перечню продуктов.
        // пройтись по договорам и отправить полисы
        // получить список договоров b2b_contr по перечню продуктов.
        // пройтись по договорам и отправить полисы
        return null;
    }

    private boolean checkPayFactExist(Long contrnodeid, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("CONTRNODEID", contrnodeid);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callExternalService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactBrowseListByParam", param, login, password);
        boolean result = false;
        if (res != null) {
            if (res.get("PAYFACTID") != null) {
                result = true;
            }
        }

        return result;
    }

    // получение записей о фактической оплате по договору без учета секции
    private List<Map<String, Object>> getPayFactList(Long contrNodeID, String login, String password) throws
            Exception {
        Long contrSectionID = null;
        List<Map<String, Object>> result = getPayFactList(contrNodeID, contrSectionID, login, password);
        return result;
    }

    // получение записей о фактической оплате по договору без учета секции
    private List<Map<String, Object>> getPayFactList(Long contrNodeID, String stateSysName, String
            login, String password) throws Exception {
        Long contrSectionID = null;
        List<Map<String, Object>> result = getPayFactList(contrNodeID, contrSectionID, stateSysName, login, password);
        return result;
    }

    // получение записей о фактической оплате по договору без учета секции
    private List<Map<String, Object>> getPayFactList(Long contrNodeID, Long contrSectionID, String
            login, String password) throws Exception {
        String stateSysName = null;
        List<Map<String, Object>> result = getPayFactList(contrNodeID, contrSectionID, stateSysName, login, password);
        return result;
    }

    // получение записей о фактической оплате по договору без учета секции (если не указан ИД секции) или по конкретной секции договора (если указан ИД секции)
    private List<Map<String, Object>> getPayFactList(Long contrNodeID, Long contrSectionID, String
            stateSysName, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("CONTRNODEID", contrNodeID);
        if (contrSectionID != null) {
            param.put("CONTRSECTIONID", contrSectionID);
        }
        if (stateSysName != null) {
            param.put("STATESYSNAME", stateSysName);
        }
        Map<String, Object> res = this.callExternalServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactBrowseListByParam", param, login, password);
        List<Map<String, Object>> result = null;
        if (res.get(RESULT) != null) {
            result = (List<Map<String, Object>>) res.get(RESULT);

        }
        return result;
    }

    // создание черновика записи о фактической оплате по договору без учета секции
    private Map<String, Object> createDraftPayFact(String contrNodeId, String contrId, Double amvalue, String
            login, String password) throws Exception {
        Long contrSectionID = null;
        return createDraftPayFact(contrNodeId, contrId, contrSectionID, amvalue, login, password);
    }

    // создание черновика записи о фактической оплате по договору без учета секции (если не указан ИД секции) или по конкретной секции договора (если указан ИД секции)
    private Map<String, Object> createDraftPayFact(Map<String, Object> payMap, Map<String, Object> planPayMap, String login, String password) throws Exception {

        // для возможности использования createDraftPayFact при различных типах оплат
        Long payFactTypeId = getLongParam(payMap, PAYFACT_TYPE_PARAMNAME);
        if (payFactTypeId == null) {
            // по умолчанию - acquiring
            payFactTypeId = PAYFACT_TYPE_ACQUIRING;
        }
        // сохраним платежный документ
        Map<String, Object> payParams = new HashMap<String, Object>();
        payParams.put("CONTRNODEID", payMap.get("CONTRNODEID"));
        payParams.put("CONTRSECTIONID", null);
        /*
        payParams.put("PAYFACTTYPE", 1);
        */
        payParams.put(PAYFACT_TYPE_PARAMNAME, payFactTypeId);
        payParams.put("PAYFACTDATE", new Date());
        payParams.put("AMCURRENCYID", 1L);
        payParams.put("AMVALUE", payMap.get("PAYMENT"));
        payParams.put("AMVALUERUB", payMap.get("PREMVALUE"));
        payParams.put("SERIES", payMap.get("CONTRID"));

        payParams.put("STATESYSNAME", PAYFACT_STATE_SYSNAME_DRAFT);
        payParams.put("NOTE", "");
        payParams.put("ReturnAsHashMap", "TRUE");
        // код факт платежа равен extId планового платежа. ordernum не использовать т.к. не строковый.
        payParams.put("CODE", planPayMap.get("EXTERNALID"));

        Map<String, Object> res = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactCreate", payParams, login, password);
        payParams.put("PAYFACTID", res.get("PAYFACTID"));
//        this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactUpdate", payParams, login, password);
        return payParams;
    }


    // создание черновика записи о фактической оплате по договору без учета секции (если не указан ИД секции) или по конкретной секции договора (если указан ИД секции)
    private Map<String, Object> createDraftPayFact(String contrNodeId, String contrId, Long
            contrSectionID, Double amvalue, String login, String password) throws Exception {

        // сохраним платежный документ
        Map<String, Object> payParams = new HashMap<String, Object>();
        payParams.put("CONTRNODEID", contrNodeId);
        payParams.put("CONTRSECTIONID", contrSectionID);
        payParams.put("PAYFACTTYPE", 1);
        payParams.put("PAYFACTDATE", new Date());
        payParams.put("AMCURRENCYID", 1L);
        payParams.put("AMVALUE", amvalue);
        payParams.put("AMVALUERUB", amvalue);
        payParams.put("SERIES", contrId);

        payParams.put("STATESYSNAME", PAYFACT_STATE_SYSNAME_DRAFT);
        payParams.put("NOTE", "Покупка онлайн");
        payParams.put("ReturnAsHashMap", "TRUE");

        Map<String, Object> res = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactCreate", payParams, login, password);
        payParams.put("PAYFACTID", res.get("PAYFACTID"));
        payParams.put("CODE", getStringParam(res.get("PAYFACTID")) + contrId);
        return payParams;
    }

    private Map<String, Object> processDraftPayFactByContrNodeId(Map<String, Object> contrMap, String
            contrNodeId, String contrId, boolean isAlternatePay, String login, String password) throws
            SberPayServiceException, Exception {
        logger.debug("processDraftPayFactByContrNodeId...");
        Map<String, Object> result = null;
        Map<String, Object> resultSuccess = new HashMap<String, Object>();

        // ИД конфигурации продукта - для получение параметров продукта (из B2B_PRODDEFVAL), отвечающих за настройки работы с мерчантом
        Long prodConfID = getLongParamLogged(contrMap, "PRODCONFID");

        List<Map<String, Object>> payFactList = getPayFactList(Long.valueOf(contrNodeId), login, password);
        // флаг наличия в списке платежей успешно оплаченого (не черновика)
        boolean isNeedPayCheck = true;
        int succesPayCount = 0;
        if (payFactList.isEmpty()) {
            // старый метод проверки оплат. договор оплачивался старым методом. и в нем не создавались черновики платежей.
            result = checkPaymentStatusByMerchantOrderNum(contrId, prodConfID, isAlternatePay, login, password);
            Map<String, Object> res1 = getMerchantResult(result);
            if (checkMerchantResult(res1)) {
                logger.debug("payment success by contrid " + contrId);
                Map<String, Object> payParams = new HashMap<String, Object>();
                payParams.put("CONTRNODEID", contrNodeId);
                payParams.put("PAYFACTNUMBER", res1.get("REFERENCENUMBER"));//ORDERID_PARAM_NAME));// guid);
                payParams.put("PAYFACTTYPE", 1);
                payParams.put("PAYFACTDATE", ((XMLGregorianCalendar) res1.get("DATE")).toGregorianCalendar().getTime());
                if (contrMap.get("PREMCURRENCYID") != null) {
                    payParams.put("AMCURRENCYID", contrMap.get("PREMCURRENCYID"));
                } else {
                    payParams.put("AMCURRENCYID", contrMap.get("PREMIUMCURRENCYID"));
                }
                payParams.put("AMVALUE", contrMap.get("PREMVALUE"));
                if (contrMap.get("PRODUCTNAME") == null) {
                    payParams.put("NAME", contrMap.get("PRODNAME"));
                } else {
                    payParams.put("NAME", contrMap.get("PRODUCTNAME"));
                }
                payParams.put("SERIES", contrId);
                payParams.put("STATESYSNAME", "success");
                payParams.put("NOTE", "Покупка онлайн");

                logger.debug("Create payFact: " + payParams.toString());
                this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactCreate", payParams, login, password);
                //     resultSuccess = new HashMap<String, Object>();
                resultSuccess.clear();
                resultSuccess.putAll(result);
            }
        } else {
            for (Map<String, Object> payFactMap : payFactList) {
                String code = getStringParam(payFactMap.get("CODE"));
                String payState = getStringParam(payFactMap.get("STATESYSNAME"));
                if (!code.isEmpty() && (payState.isEmpty() || ("success".equalsIgnoreCase(payState)))) {
                    isNeedPayCheck = false;
                }
                if ("draft".equalsIgnoreCase(payState)) {
                    result = checkPaymentStatusByMerchantOrderNum(code, prodConfID, isAlternatePay, login, password);
                    Map<String, Object> res1 = getMerchantResult(result);
                    if (checkMerchantResult(res1)) {
                        logger.debug("payment success by code " + code);
                        succesPayCount++;
                        //Map<String, Object> payParams = new HashMap<String, Object>();
                        payFactMap.put("PAYFACTNUMBER", res1.get("REFERENCENUMBER"));//ORDERID_PARAM_NAME));// guid);
                        payFactMap.put("PAYFACTTYPE", 1);
                        payFactMap.put("PAYFACTDATE", ((XMLGregorianCalendar) res1.get("DATE")).toGregorianCalendar().getTime());
                        if (contrMap.get("PREMCURRENCYID") != null) {
                            payFactMap.put("AMCURRENCYID", contrMap.get("PREMCURRENCYID"));
                        } else {
                            payFactMap.put("AMCURRENCYID", contrMap.get("PREMIUMCURRENCYID"));
                        }
                        payFactMap.put("AMVALUE", contrMap.get("PREMVALUE"));
                        if (contrMap.get("PRODUCTNAME") == null) {
                            payFactMap.put("NAME", contrMap.get("PRODNAME"));
                        } else {
                            payFactMap.put("NAME", contrMap.get("PRODUCTNAME"));
                        }
                        payFactMap.put("SERIES", contrId);
                        payFactMap.put("STATESYSNAME", "success");
                        payFactMap.put("NOTE", "Покупка онлайн");
                        logger.debug("Create payFact: " + payFactMap.toString());
                        this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactUpdate", payFactMap, login, password);
                        //resultSuccess = new HashMap<String, Object>();
                        resultSuccess.clear();
                        resultSuccess.putAll(result);

                    }
                } else {
                    if ("success".equalsIgnoreCase(payState)) {
                        // уже есть success платеж.
                        //но договор не перевелся в оплачен
                        //отправляем его на оплату
                        result = new HashMap<String, Object>();

                        result.put("ORDERID", "ORDERID");
                        if (payFactMap.get("PAYFACTNUMBER") != null) {
                            result.put("REFERENCENUMBER", payFactMap.get("PAYFACTNUMBER"));
                        } else {
                            result.put("REFERENCENUMBER", "REFERENCENUMBER");
                        }
                        result.put("ORDERSTATUS", "2");
                        resultSuccess.clear();
                        resultSuccess.putAll(result);
                    }
                }
            }
            if (succesPayCount > 1) {
                //ахтунг пользователь оплатил один и тот же договор дважды. нид возвратен
                logger.error("MultiplePaySuccess contrnode = " + contrNodeId + ", contrid = " + contrId);
            }
            if (succesPayCount > 0) {
                // есть оплаченный платеж. удаляем черновики.
                for (Map<String, Object> payFactMap : payFactList) {
                    String payState = getStringParam(payFactMap.get("STATESYSNAME"));
                    // только что оплаченного платежа здесь не будет. т.к. его состояние в списке уже изменено и
                    // этот иф его не пропустит.
                    if ("draft".equalsIgnoreCase(payState)) {
                        Map<String, Object> delParam = new HashMap<String, Object>();
                        delParam.put("PAYFACTID", payFactMap.get("PAYFACTID"));
                        this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactDelete", delParam, login, password);
                    }
                }

            }
        }
        if (succesPayCount > 0) {
            if (resultSuccess == null) {
                Map<String, Object> res1 = getMerchantResult(result);
                if (checkMerchantResult(res1)) {
                    resultSuccess = result;
                }
            }
        } else {
            if (resultSuccess == null) {
                resultSuccess = result;
            }
        }

        logger.debug("processDraftPayFactByContrNodeId end with result: " + resultSuccess);
        return resultSuccess;
    }

    private Map<String, Object> checkPaymentStatusByMerchantOrderNum(String code, Long prodConfID,
                                                                     boolean alternatePay, String login, String password) throws SberPayServiceException {
        Map<String, Object> result = null;
        if (prodConfID != null) {
            // указан ИД конфигурации продукта - создание SberPayService с поддрежкой получения параметров мерчанта из сведений продукта
            SberPayService sps = new SberPayService(this.getModuleName(), prodConfID, login, password);
            Map<String, Object> rawResult = sps.sberPayServiceGetOrderStatusExtended(code);
            Map<String, Object> merchantResult = getMerchantResult(rawResult);
            if (checkMerchantResult(merchantResult)) {
                // годный ответ возвращен из SberPayService с поддрежкой получения параметров мерчанта из сведений продукта
                result = rawResult;
            }
        }
        if (result == null) {
            // не известен ИД конфигурации продукта или годный ответ не возвращен из SberPayService с поддрежкой получения параметров мерчанта из сведений продукта
            // требуется воспользовать предыдущей версией SberPayService, которая используется в checkPaymentStatusByMerchantOrderNum(String, boolean)
            result = checkPaymentStatusByMerchantOrderNum(code, alternatePay);
        }
        return result;
    }

    private Map<String, Object> checkPaymentStatusByMerchantOrderNum(String code, boolean alternatePay) throws
            SberPayServiceException {
        Map<String, Object> result = null;
        SberPayService sps = new SberPayService(this.getModuleName(), alternatePay);
        result = sps.sberPayServiceGetOrderStatusExtended(code);
        if (alternatePay) {
            // если альтернативный мерчант, то нужно проверить результат
            Map<String, Object> res1 = getMerchantResult(result);
            if (checkMerchantResult(res1)) {
                // годный ответ возвращен из альтернативного мерчанта
                logger.debug("pay in alternate merchant");
            } else {
                // годный ответ из альтернативного мерчанта не получен - требуется проверка обычного
                SberPayService sps1 = new SberPayService(this.getModuleName());
                try {
                    result = sps1.sberPayServiceGetOrderStatusExtended(code);
                } catch (Exception e) {
                    logger.debug("error " + code, e);
                    return null;
                }

            }
        }
        return result;
    }

    private Map<String, Object> callUnregisterPayService(SberPayService sps, Map<String, Object> payFactMap, String contrNodeId, String contrId, Double premValue, String
            login, String password) {
        Map<String, Object> res = null;
        if (payFactMap.get("ORDERID") != null) {
            res = sps.sberPayServiceReverseOrder(getStringParam(payFactMap.get("ORDERID")));

        }
        return res;
    }


    // поиск оформленных договоров с одной секцией и создание черновика следующей секции
    private void createSecondSectionDraftForProduct(Map<String, Object> params, String
            productSysName, String login, String password) throws Exception {
        logger.debug(String.format("createSectionDraftForProduct for product with system name = '%s'...", productSysName));
        Map<String, Object> contractParams = new HashMap<String, Object>();
        contractParams.putAll(params);
        //contractParams.put("PAYWITHOUTSECTIONS", true); // со связанными записями о факте и/или плане оплаты без указания секции
        contractParams.put("PRODSYSNAME", productSysName); // с ограничением по продукту
        //contractParams.put("STATESYSNAME", B2B_CONTRACT_SG); // только подписанные (то есть имеющие первую оплату)
        contractParams.put("STATESYSNAMELIST", B2B_CONTRACT_PROLONGABLE_STATE_LIST_STR); // только подписанные (или успешно выгруженные и т.д.) договора (то есть договора с оплатой)
        contractParams.put("SECTIONSCOUNT", 1L); // только с единственной секцией
        logger.debug("Calling dsB2BContractBrowseListByParamExNonSectionPayments with parameters: " + contractParams);
        List<Map<String, Object>> contractList = this.callExternalServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamExNonSectionPayments", contractParams, login, password);
        if (contractList == null) {
            logger.error("Error on dsB2BContractBrowseListByParamExNonSectionPayments method call! Details in previously logged errors.");
        } else {
            int totalContractsCount = contractList.size();
            logger.debug(String.format("Calling dsB2BContractBrowseListByParamExNonSectionPayments returned %d records.", totalContractsCount));
            logger.debug(String.format("createSectionDraftForProduct found %d contracts for new section draft creating...", totalContractsCount));
            int processingContractNum = 0;
            for (Map<String, Object> contract : contractList) {
                processingContractNum++;
                logger.debug(String.format("createSectionDraftForProduct for contract number %d (from total count of %d found contracts)...", processingContractNum, totalContractsCount));
                createSecondSectionDraftForContract(contract, login, password);
                logger.debug(String.format("createSectionDraftForProduct for contract number %d (from total count of %d found contracts) finished.", processingContractNum, totalContractsCount));
            }
        }
        logger.debug(String.format("createSectionDraftForProduct for product with system name = '%s' finished.", productSysName));
    }

    private void createSecondSectionDraftForContract(Map<String, Object> contract, String login, String
            password) throws Exception {
        Long contractID = getLongParam(contract, "CONTRID");
        Long contractNodeID = getLongParam(contract, "CONTRNODEID");
        String contractNumber = getStringParam(contract, "CONTRNUMBER");
        Long sectionsCount = getLongParam(contract, "SECTIONSCOUNT");
        if ((contractID == null) || (contractNodeID == null) || (sectionsCount == null) || (sectionsCount.intValue() != 1)) {
            // ошибка - недостаточно данных по договору для дальнейшей обработки
            logger.error(String.format(
                    "Invalid or incomlete contract record was found in dsB2BContractBrowseListByParamExNonSectionPayments result list! Details (invalid record data): %s.",
                    contract.toString()
            ));
        } else {
            if (sectionsCount == 1) {
                String logMsg = String.format(
                        "Signed contract with number (CONTRNUMBER) = '%s' and id (CONTRID) = %d contain %d section(s) and require new section draft creation.",
                        contractNumber, contractID, sectionsCount
                );
                logger.debug(logMsg/* + " This payments now will be linked with contract's single section."*/);
                Map<String, Object> contractSingleSectionParams = new HashMap<String, Object>();
                contractSingleSectionParams.put("CONTRID", contractID);
                List<Map<String, Object>> contractSingleSectionList = this.callExternalServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractSectionBrowseListByParam", contractSingleSectionParams, login, password);
                if ((contractSingleSectionList == null) || (contractSingleSectionList.size() != 1)) {
                    // ошибка - не найдена секция или секция оказалась не единственной
                    logger.error(String.format(
                            "Unable to get reported by dsB2BContractBrowseListByParamExNonSectionPayments single contract's section for contract with number (CONTRNUMBER) = '%s' and id (CONTRID) = %d!",
                            contractNumber, contractID
                    ));
                } else {
                    // единственная секция договора
                    Map<String, Object> contractSingleSection = contractSingleSectionList.get(0);
                    // ИД единственной секции договора
                    Long contractSingleSectionID = getLongParam(contractSingleSection, "CONTRSECTIONID");
                    if (contractSingleSectionID == null) {
                        // ошибка - не найден ИД секции в мапе единственной секции договора
                        logger.error(String.format(
                                "Unable to get reported by dsB2BContractBrowseListByParamExNonSectionPayments single contract's section's id (CONTRSECTIONID) for contract with number (CONTRNUMBER) = '%s' and id (CONTRID) = %d!",
                                contractNumber, contractID
                        ));
                    } else {
                        // найден ИД секции в мапе единственной секции договора - обновление записей платежей
                        logger.debug(String.format(
                                "Reported by dsB2BContractBrowseListByParamExNonSectionPayments for contract with number (CONTRNUMBER) = '%s' and id (CONTRID) = %d single section was found and got id (CONTRSECTIONID) = %d.",
                                contractNumber, contractID, contractSingleSectionID
                        ));
                        // проверка - можно ли считать этот договор фактически оплаченым
                        Map<String, Object> checkFactPaymentRes = checkFactPaymentForContract(contractNodeID, contractSingleSectionID, login, password);
                    }
                }
            } else if (sectionsCount > 1) {
                String logMsg = String.format(
                        "Signed contract with number (CONTRNUMBER) = '%s' and id (CONTRID) = %d contain %d section(s) and new section draft creation not required.",
                        contractNumber, contractID, sectionsCount
                );
                logger.debug(logMsg);
            }
        }
    }

    // поиск и обновление связей с секцией для платежей по подписанным договорам, имеющим одну единственную секцию
    private void updatePaymentToSectionReferenceForProduct(Map<String, Object> params, String
            productSysName, String login, String password) throws Exception {
        logger.debug(String.format("updatePaymentToSectionReferenceForProduct for product with system name = '%s'...", productSysName));
        Map<String, Object> contractParams = new HashMap<String, Object>();
        contractParams.putAll(params);
        contractParams.put("PAYWITHOUTSECTIONS", true); // со связанными записями о факте и/или плане оплаты без указания секции
        contractParams.put("PRODSYSNAME", productSysName); // с ограничением по продукту
        //contractParams.put("STATESYSNAME", B2B_CONTRACT_SG); // только подписанные (то есть имеющие первую оплату)
        contractParams.put("STATESYSNAMELIST", B2B_CONTRACT_PROLONGABLE_STATE_LIST_STR); // только подписанные (или успешно выгруженные и т.д.) договора (то есть договора с оплатой)
        logger.debug("Calling dsB2BContractBrowseListByParamExNonSectionPayments with parameters: " + contractParams);
        List<Map<String, Object>> contractList = this.callExternalServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamExNonSectionPayments", contractParams, login, password);
        if (contractList == null) {
            logger.error("Error on dsB2BContractBrowseListByParamExNonSectionPayments method call! Details in previously logged errors.");
        } else {
            int totalContractsCount = contractList.size();
            logger.debug(String.format("Calling dsB2BContractBrowseListByParamExNonSectionPayments returned %d records.", totalContractsCount));
            logger.debug(String.format("updatePaymentToSectionReferenceForProduct found %d contracts for updating...", totalContractsCount));
            int processingContractNum = 0;
            for (Map<String, Object> contract : contractList) {
                processingContractNum++;
                logger.debug(String.format("updatePaymentToSectionReferenceForProduct for contract number %d (from total count of %d found contracts)...", processingContractNum, totalContractsCount));
                updatePaymentToSectionReferenceForContract(contract, login, password);
                logger.debug(String.format("updatePaymentToSectionReferenceForProduct for contract number %d (from total count of %d found contracts) finished.", processingContractNum, totalContractsCount));
            }
        }
        logger.debug(String.format("updatePaymentToSectionReferenceForProduct for product with system name = '%s' finished.", productSysName));
    }

    private void updatePaymentToSectionReferenceForContract(Map<String, Object> contract, String
            login, String password) throws Exception {
        Long contractID = getLongParam(contract, "CONTRID");
        Long contractNodeID = getLongParam(contract, "CONTRNODEID");
        String contractNumber = getStringParam(contract, "CONTRNUMBER");
        Long sectionsCount = getLongParam(contract, "SECTIONSCOUNT");
        if ((contractID == null) || (contractNodeID == null) || (sectionsCount == null)) {
            // ошибка - недостаточно данных по договору для дальнейшей обработки
            logger.error(String.format(
                    "Invalid or incomlete contract record was found in dsB2BContractBrowseListByParamExNonSectionPayments result list! Details (invalid record data): %s.",
                    contract.toString()
            ));
        } else {
            String logMsg = String.format(
                    "Signed contract with number (CONTRNUMBER) = '%s' and id (CONTRID) = %d contain %d section(s) and have some records about plan or fact payments without section reference.",
                    contractNumber, contractID, sectionsCount
            );
            if (sectionsCount == 1) {
                logger.debug(logMsg + " This payments now will be linked with contract's single section.");
                Map<String, Object> contractSingleSectionParams = new HashMap<String, Object>();
                contractSingleSectionParams.put("CONTRID", contractID);
                List<Map<String, Object>> contractSingleSectionList = this.callExternalServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractSectionBrowseListByParam", contractSingleSectionParams, login, password);
                if ((contractSingleSectionList == null) || (contractSingleSectionList.size() != 1)) {
                    // ошибка - не найдена секция или секция оказалась не единственной
                    logger.error(String.format(
                            "Unable to get reported by dsB2BContractBrowseListByParamExNonSectionPayments single contract's section for contract with number (CONTRNUMBER) = '%s' and id (CONTRID) = %d!",
                            contractNumber, contractID
                    ));
                } else {
                    // единственная секция договора
                    Map<String, Object> contractSingleSection = contractSingleSectionList.get(0);
                    // ИД единственной секции договора
                    Long contractSingleSectionID = getLongParam(contractSingleSection, "CONTRSECTIONID");
                    if (contractSingleSectionID == null) {
                        // ошибка - не найден ИД секции в мапе единственной секции договора
                        logger.error(String.format(
                                "Unable to get reported by dsB2BContractBrowseListByParamExNonSectionPayments single contract's section's id (CONTRSECTIONID) for contract with number (CONTRNUMBER) = '%s' and id (CONTRID) = %d!",
                                contractNumber, contractID
                        ));
                    } else {
                        // найден ИД секции в мапе единственной секции договора - обновление записей платежей
                        logger.debug(String.format(
                                "Reported by dsB2BContractBrowseListByParamExNonSectionPayments for contract with number (CONTRNUMBER) = '%s' and id (CONTRID) = %d single section was found and got id (CONTRSECTIONID) = %d.",
                                contractNumber, contractID, contractSingleSectionID
                        ));
                        // плановые платежи по договору - запрос, проверка связи с секций, обновление при необходимости
                        updatePlanPaymentToSectionReferenceForContract(contractID, contractNumber, contractSingleSectionID, login, password);
                        // фактические платежи по договору - запрос, проверка связи с секций, обновление при необходимости
                        Map<String, Object> updatedFactPaymentRes = updateFactPaymentToSectionReferenceForContract(contractID, contractNodeID, contractNumber, contractSingleSectionID, login, password);
                        boolean isUpdateSuccess = getBooleanParam(updatedFactPaymentRes.get("ISUPDATESUCCESS"), Boolean.FALSE);
                        if (isUpdateSuccess) {
                            // обновление всех существующих записей о фактических платежах завершено успешно
                            // следует определить есть ли среди них запись об успешной оплате
                            boolean isSuccessFactPaymentExist = false;
                            List<Map<String, Object>> updatedFactPaymentList = (List<Map<String, Object>>) updatedFactPaymentRes.get("UPDATEDFACTPAYMENTLIST");
                            for (Map<String, Object> updatedFactPayment : updatedFactPaymentList) {
                                String stateSysName = getStringParam(updatedFactPayment, "STATESYSNAME");
                                if (PAYFACT_STATE_SYSNAME_SUCCESS.equals(stateSysName)) {
                                    isSuccessFactPaymentExist = true;
                                    break;
                                }
                            }

                        }
                    }
                }
            } else if (sectionsCount > 1) {
                logger.error(logMsg + " This payments records can not be uniquely linked with contract sections!");
            } else {
                logger.error(logMsg + " This contract is probably damaged and it's payments can not be processed!");
            }
        }
    }

    // плановые платежи по договору - запрос, проверка связи с секций, обновление при необходимости
    private void updatePlanPaymentToSectionReferenceForContract(Long contractID, String contractNumber, Long
            contractSingleSectionID, String login, String password) throws Exception {
        Map<String, Object> planPaymentParams = new HashMap<String, Object>();
        planPaymentParams.put("CONTRID", contractID);
        List<Map<String, Object>> planPaymentList = this.callExternalServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentBrowseListByParam", planPaymentParams, login, password);
        if (planPaymentList != null) {
            for (Map<String, Object> planPayment : planPaymentList) {
                Long planPaymentID = getLongParam(planPayment, "PAYID");
                Long planPaymentSectionID = getLongParam(planPayment, "CONTRSECTIONID");
                if (planPaymentSectionID == null) {
                    Map<String, Object> planPaymentUpdateParams = new HashMap<String, Object>();
                    planPaymentUpdateParams.put("PAYID", planPaymentID);
                    planPaymentUpdateParams.put("CONTRSECTIONID", contractSingleSectionID);
                    planPaymentUpdateParams.put(RETURN_AS_HASH_MAP, true);
                    Map<String, Object> updateResult = this.callExternalServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentUpdate", planPaymentUpdateParams, login, password);
                    if ((!isCallResultOK(updateResult)) || (updateResult.get("PAYID") == null)) {
                        // ошибка при обновлении записи о платеже
                        logger.error(String.format(
                                "Error on dsB2BPaymentUpdate method call with parameters: %s! Details (call result): %s",
                                planPaymentUpdateParams.toString(), updateResult == null ? "null" : updateResult.toString()
                        ));
                    }
                } else if (!planPaymentSectionID.equals(contractSingleSectionID)) {
                    // ошибка - платеж ссылается на секцию, не принадлежащую договору
                    logger.error(String.format(
                            "Plan payment record with invalid section or contract reference was found! Details (payment info): %s! Reported by dsB2BContractBrowseListByParamExNonSectionPayments for contract with number (CONTRNUMBER) = '%s' and id (CONTRID) = %d single section got id (CONTRSECTIONID) = %d.",
                            planPayment.toString(), contractNumber, contractID, contractSingleSectionID
                    ));
                }
            }
        }
    }

    // фактические платежи по договору - запрос, проверка связи с секций, обновление при необходимости
    private Map<String, Object> updateFactPaymentToSectionReferenceForContract(Long contractID, Long
            contractNodeID, String contractNumber, Long contractSingleSectionID, String login, String password) throws
            Exception {
        Map<String, Object> factPaymentParams = new HashMap<String, Object>();
        factPaymentParams.put("CONTRNODEID", contractNodeID);
        List<Map<String, Object>> updatedFactPaymentList = null;
        List<Map<String, Object>> factPaymentList = this.callExternalServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactBrowseListByParam", factPaymentParams, login, password);
        if (factPaymentList != null) {
            updatedFactPaymentList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> factPayment : factPaymentList) {
                Long factPaymentID = getLongParam(factPayment, "PAYFACTID");
                Long factPaymentSectionID = getLongParam(factPayment, "CONTRSECTIONID");
                if (factPaymentSectionID == null) {
                    Map<String, Object> factPaymentUpdateParams = new HashMap<String, Object>();
                    factPaymentUpdateParams.put("PAYFACTID", factPaymentID);
                    factPaymentUpdateParams.put("CONTRSECTIONID", contractSingleSectionID);
                    factPaymentUpdateParams.put(RETURN_AS_HASH_MAP, true);
                    Map<String, Object> updateResult = this.callExternalServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactUpdate", factPaymentUpdateParams, login, password);
                    if ((!isCallResultOK(updateResult)) || (updateResult.get("PAYFACTID") == null)) {
                        // ошибка при обновлении записи о платеже
                        logger.error(String.format(
                                "Error on dsB2BPaymentFactUpdate method call with parameters: %s! Details (call result): %s",
                                factPaymentUpdateParams.toString(), updateResult == null ? "null" : updateResult.toString()
                        ));
                    } else {
                        // dsB2BPaymentFactUpdate не вернул ошибок, обновление записи прошло успешно - требуется обновить мапу возвращаемой записи о фактическом платеже
                        factPayment.put("CONTRSECTIONID", contractSingleSectionID);
                        updatedFactPaymentList.add(factPayment);
                    }
                } else if (!factPaymentSectionID.equals(contractSingleSectionID)) {
                    // ошибка - платеж ссылается на секцию, не принадлежащую договору
                    logger.error(String.format(
                            "Fact payment record with invalid section or contract reference was found! Details (payment info): %s! Reported by dsB2BContractBrowseListByParamExNonSectionPayments for contract with number (CONTRNUMBER) = '%s' and id (CONTRID) = %d single section got id (CONTRSECTIONID) = %d.",
                            factPayment.toString(), contractNumber, contractID, contractSingleSectionID
                    ));
                }
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("FACTPAYMENTLIST", factPaymentList);
        result.put("UPDATEDFACTPAYMENTLIST", updatedFactPaymentList);
        // если factPaymentList успешно был получен из БД и количество обновленных записей равно количеству загруженных, то обновление завершено успешно
        boolean isUpdateSuccess = (factPaymentList != null) && (updatedFactPaymentList != null) && (factPaymentList.size() == updatedFactPaymentList.size());
        result.put("ISUPDATESUCCESS", isUpdateSuccess);
        return result;
    }

    // проверка - можно ли считать этот договор фактически оплаченым
    private Map<String, Object> checkFactPaymentForContract(Long contractNodeID, Long
            contractSingleSectionID, String login, String password) throws Exception {
        Map<String, Object> factPaymentParams = new HashMap<String, Object>();
        factPaymentParams.put("CONTRNODEID", contractNodeID);
        List<Map<String, Object>> checkedFactPaymentList = null;
        List<Map<String, Object>> factPaymentList = this.callExternalServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentFactBrowseListByParam", factPaymentParams, login, password);
        boolean isSuccessFactPaymentExist = false;
        if (factPaymentList != null) {
            checkedFactPaymentList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> factPayment : factPaymentList) {
                Long factPaymentID = getLongParam(factPayment, "PAYFACTID");
                Long factPaymentSectionID = getLongParam(factPayment, "CONTRSECTIONID");
                String factPaymentStateSysName = getStringParam(factPayment, "STATESYSNAME");
                if ((factPaymentID != null) && (factPaymentSectionID != null) && (factPaymentSectionID.equals(contractSingleSectionID))) {
                    checkedFactPaymentList.add(factPayment);
                    if (PAYFACT_STATE_SYSNAME_SUCCESS.equals(factPaymentStateSysName)) {
                        isSuccessFactPaymentExist = true;
                    }
                }
            }

        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("FACTPAYMENTLIST", factPaymentList);
        result.put("CHECKEDFACTPAYMENTLIST", checkedFactPaymentList);
        // если factPaymentList успешно был получен из БД и количество проверенных записей равно количеству загруженных, а также есть успешная запись о проверке оплаты - то договор по факту считается оплаченым
        boolean isFactPaymentCheckSuccess = (factPaymentList != null) && (checkedFactPaymentList != null) && (factPaymentList.size() == checkedFactPaymentList.size()) && (isSuccessFactPaymentExist);
        result.put("ISFACTPAYMENTCHECKSUCCESS", isFactPaymentCheckSuccess);
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsExchangeCourceByCurId(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Long currencyId = getLongParam(params.get("CURRENCYID"));
        Double currencyRate;
        if (IS_CURRENCY_DEBUG) {
            currencyRate = IS_CURRENCY_DEBUG_VALUE;
        } else {
            currencyRate = getExchangeCourceByCurID(currencyId, new Date(), login, password);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("CURRENCYRATE", currencyRate);
        return result;
    }

    private class Event extends HashMap<String, Object> {

        private String login, password;
        private Map<String, Object> originalParams;

        Event(Map<String, Object> params) {
            //нужно постПроцессорам
            originalParams = new HashMap<>(params);

            for (Field f : ClientProfileEvent.class.getDeclaredFields()) {
                String name = f.getName();
                Object value = params.get(name);
                if (value != null) this.put(name, value);
            }

            loadCredentials(params);

            if (params.get("modeApp") != null) {
                this.put("modeApp", getLongParam(params, "modeApp"));
            } else {
                this.put("modeApp", 0);
            }

            this.put("eId", -1);

        }

        Event setEventType(String type) {
            this.put("eventTypeSysName", type);
            return this;
        }

        Event setParam(String name, Object o) {
            this.put(name, o);
            return this;
        }

        Map<String, Object> persist() throws Exception {
            Map<String, Object> res = callService(B2BPOSWS_SERVICE_NAME, "dsB2BClientProfileEventCreate", this.asMap(), login, password);
            Map<String, Object> event = (Map<String, Object>) res.get(RESULT);
            if (event == null) throw new Exception("Cant save event: " + this.toString());
            logger.error(this.toString() + "\u0009New event id = " + event.get("id") + "\r\n");

            return event;
        }

        // чтобы можно ьыло забыть что это потомок мапы
        Map<String, Object> asMap() {
            return this;
        }

        Event postProcess(ParamsProcessor pp) {
            pp.remap(this, originalParams);
            return this;
        }

        Event setErrorText(String s) {
            this.put("errorTest", s);
            return this;
        }

        Event setErrorCode(String s) {
            this.put("errorCode", s);
            return this;
        }

        Event setObjId(Long id) {
            this.put("refObjectId", id);
            return this;
        }

        Event loadCredentials(Map<String, Object> map) {
            login = (String) map.getOrDefault(WsConstants.LOGIN, "");
            password = (String) map.getOrDefault(WsConstants.PASSWORD, "");
            return this;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\r\n[NOT ERROR!!!] New payment registration event:\r\n");
            for (String name : this.keySet()) {
                String value = this.get(name).toString();
                sb.append("\u0009Attr. '" + name + "' [");
                sb.append(value + "]\r\n");
            }
            return sb.toString();
        }
    }

    private class NewPayProcessor implements ParamsProcessor {

        @Override
        public void remap(Map<String, Object> params, Map<String, Object> originalParams) {
            if (originalParams.get("CALLBAKURI") != null) params.put("paramStr1", originalParams.get("CALLBAKURI"));
            if (originalParams.get("PAYMENT") != null) params.put("paramFloat1", originalParams.get("PAYMENT"));
            if (originalParams.get("PAYMENTTYPE") != null) params.put("paramStr2", originalParams.get("PAYMENTTYPE"));
            //if (originalParams.get("CURRENTDATE") != null) params.put("paramDate1", originalParams.get("CURRENTDATE"));
            if (originalParams.get("CURRENTDATEUNIX") != null) {
                Long date = getLongParam(originalParams, "CURRENTDATEUNIX");
                params.put("paramFloat2", new Date(date));
            }
            if (originalParams.get("EXTERNALID") != null) params.put("paramsInt2", originalParams.get("EXTERNALID"));
            if (originalParams.get("CURRENCYRATE") != null)
                params.put("paramFloat3", originalParams.get("CURRENCYRATE"));
            if (originalParams.get("INSAMCURRENCYID") != null)
                params.put("paramStr3", originalParams.get("INSAMCURRENCYID"));
            if (originalParams.get("DATE") != null)
                params.put("paramStr4", originalParams.get("DATE"));
        }
    }


    public class SBSresultPostProcessor implements ParamsProcessor {
        @Override
        public void remap(Map<String, Object> params, Map<String, Object> originalParams) {
            if (originalParams.get("FORMURL") != null) params.put("paramStr1", originalParams.get("FORMURL"));
            if (originalParams.get("ORDERID") != null) params.put("paramStr2", originalParams.get("ORDERID"));
            if (originalParams.get("ERRORMESSAGE") != null) params.put("errorTest", originalParams.get("ERRORMESSAGE"));
            if (originalParams.get("ERRORCODE") != null) params.put("errorCode", originalParams.get("ERRORCODE"));
        }
    }

    // интерфейс для обработки мапы для параметров разных событий
    private interface ParamsProcessor {
        void remap(Map<String, Object> params, Map<String, Object> originalParams);
    }

}
