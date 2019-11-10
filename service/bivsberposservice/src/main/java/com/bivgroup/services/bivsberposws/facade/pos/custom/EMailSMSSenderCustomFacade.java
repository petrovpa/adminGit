/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomBaseFacade.BIVSBERPOSWS_SERVICE_NAME;
import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomBaseFacade.SERVICE_NAME;
import static com.bivgroup.services.bivsberposws.system.Constants.*;
import com.bivgroup.services.bivsberposws.system.SmsSender;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author Admin
 */
@BOName("EMailSMSSenderCustom")
public class EMailSMSSenderCustomFacade extends BaseFacade {

    public static final String THIS_SERVICE_NAME = BIVSBERPOSWS;
    public static final String MAIL_SERVICE_NAME = WEBSMSWS;

    private final Logger logger = Logger.getLogger(this.getClass());

    private String smsText = "";
    private String smsUser = "";
    private String smsPwd = "";
    private String smsFrom = "";

    protected void getSmsInit() {
        Config config = Config.getConfig(THIS_SERVICE_NAME);
        this.smsText = config.getParam("SMSTEXT", "Уважаемый%20клиент,%20Ваш%20пароль%20для%20подтверждения%20введенных%20данных:");
        this.smsUser = config.getParam("SMSUSER", "sberinsur");
        this.smsPwd = config.getParam("SMSPWD", "KD9zVoeR123");
        this.smsFrom = config.getParam("SMSFROM", "SberbankIns");

    }

    public EMailSMSSenderCustomFacade() {
        super();
    }

    /**
     *
     * Отправка e-mail
     *
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsSendEMail(Map<String, Object> params) throws Exception {

        logger.debug("Вызван dsSendEMail с параметрами: " + params.toString());

        Map<String, Object> sendParams = new HashMap<String, Object>();
        sendParams.put("SMTPReceipt", params.get("EMail").toString());
        sendParams.put("SMTPSubject", params.get("Subj").toString());
        sendParams.put("SMTPMESSAGE", params.get("Letter").toString());       

        String mailSeviceLogin = params.get(WsConstants.LOGIN).toString();
        String mailSevicePassword = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> sendResult = this.callService(MAIL_SERVICE_NAME, "mailmessage", sendParams, mailSeviceLogin, mailSevicePassword);

        sendResult.put("STATUS", "OK");

        logger.debug("Вызов dsSendEMail завершен с результатом: " + sendResult.toString());

        return sendResult;

    }

    protected void userLogActionCreateEx(String sessionId, String contrId, String action, String note, String value, String param1, String param2, String param3, String login, String password) throws Exception {
        Map<String, Object> qParam = new HashMap<String, Object>();
        qParam.put("ACTION", action);
        qParam.put("NOTE", note);
        qParam.put("CONTRID", contrId);
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

    /**
     *
     * Отправка sms
     *
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsSendSMS(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        logger.debug("Вызван dsSendSMS с параметрами: " + params.toString());

        getSmsInit();

        String phone = params.get("Phone").toString();
        String message = params.get("Msg").toString();

        SmsSender smssender = new SmsSender();
        logger.debug("sms message: " + message);
        userLogActionCreateEx("", "", "Отправка СМС", message, "", phone, "", "", login, password);

        Map<String, Object> sendResult = smssender.doGet(this.smsUser, this.smsPwd, this.smsFrom, phone, message);

        sendResult.put("STATUS", "OK");

        logger.debug("Вызов dsSendSMS завершен с результатом: " + sendResult.toString());

        return sendResult;

    }

    /**
     *
     * Получение статуса sms
     *
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsGetSMSStatus(Map<String, Object> params) throws Exception {

        logger.debug("Вызван dsGetSMSStatus с параметрами: " + params.toString());

        getSmsInit();

        String SmsID = params.get("SmsID").toString();

        SmsSender smssender = new SmsSender();
        Map<String, Object> checkResult = smssender.getSmsStatus(this.smsUser, this.smsPwd, this.smsFrom, SmsID);

        checkResult.put("STATUS", "OK");

        logger.debug("Вызов dsGetSMSStatus завершен с результатом: " + checkResult.toString());

        return checkResult;

    }

}
