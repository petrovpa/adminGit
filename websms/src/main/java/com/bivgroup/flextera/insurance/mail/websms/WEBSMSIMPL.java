package com.bivgroup.flextera.insurance.mail.websms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.cayenne.access.DataContext;
import org.apache.log4j.Logger;
import org.smpp.Data;
import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.SmppObject;
import org.smpp.TCPIPConnection;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.Address;
import org.smpp.pdu.AddressRange;
import org.smpp.pdu.BindReceiver;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransciever;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.CancelSM;
import org.smpp.pdu.CancelSMResp;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.DeliverSMResp;
import org.smpp.pdu.EnquireLink;
import org.smpp.pdu.EnquireLinkResp;
import org.smpp.pdu.PDU;
import org.smpp.pdu.QuerySM;
import org.smpp.pdu.QuerySMResp;
import org.smpp.pdu.SubmitSM;
import org.smpp.pdu.SubmitSMResp;
import org.smpp.pdu.UnbindResp;
import org.smpp.pdu.ValueNotSetException;
import org.smpp.pdu.WrongLengthOfStringException;
import org.smpp.util.ByteBuffer;
import org.smpp.util.Queue;

/**
 * Основная задача сервиса websmsws состоит в отправке сообщений по электронной
 * почте и через SMS центры. Для отправки сообщений по электронной почте
 * используется метод mailmessage. Для отправки сообщений через SMS цента
 * используется метод dispatchsmsmessage. Используется набор вспомогательных
 * библиотек Logica SMPP.
 *
 * @author kkachura
 */
public class WEBSMSIMPL implements constsSections {

    private static List groupIdHolder = new ArrayList();
    /**
     * Ссылка на DateUtil
     */
    /*DateUtil dateUtil = null;*/
    /**
     * Хранилище сообщений
     */
    private static final Map<String, Object> messageInformationHolder = Collections.synchronizedMap(new HashMap<String, Object>());
    private static final Map<String, Object> messagePreventInformationHolder = Collections.synchronizedMap(new LinkedHashMap<String, Object>());
    /**
     * Ссылка на Logger
     */
    public final static Logger logger = Logger.getLogger(WEBSMSIMPL.class);
    /**
     * Контекс базы данных
     */
    private static DataContext dataContext = null;
    /**
     * Константа для работы с базой данных
     */
    private static final String SMS_STORE_QUERY_NAME = "storeSMS";
    /**
     * Константа для работы с базой данных
     */
    private static final String SMS_UPDATE_QUERY_NAME = "updateSMS";
    /**
     * Константа для работы с базой данных
     */
    private static final String SMS_HIST_INSERT_QUERY_NAME = "insertSMSHist";
    /**
     * Идентификатор группы
     */
    private static Integer message_group_id = 0;
    /**
     * Флаг подключения к SMS центру
     */
    static boolean bound = false;
    /**
     * Флаг окончания работ
     */
    static Boolean over = false;
    /**
     * Ссылка на Session
     */
    static Session session = null;
    /**
     * Ссылка на TCPIPConnection
     */
    static TCPIPConnection connection = null;
    /**
     * Ссылка ExecutorService
     */
    private static ExecutorService executor = null;
    /**
     * Indicates that the Session has to be asynchronous. Asynchronous Session
     * means that when submitting a Request to the SMSC the Session does not
     * wait for a response. Instead the Session is provided with an instance of
     * implementation of ServerPDUListener from the smpp library which receives
     * all PDUs received from the SMSC. It's application responsibility to match
     * the received Response with sended Requests.
     */
    final static boolean asynchronous = true;
    /**
     * This is an instance of listener which obtains all PDUs received from the
     * SMSC. Application doesn't have explicitly call Session's receive()
     * function, all PDUs are passed to this application callback object. See
     * documentation in Session, Receiver and ServerPDUEventListener classes
     * form the SMPP library.
     */
    static SMPPTestPDUEventListener pduListener = null;

    /**
     * 0 is transmitter 1 is transciever 2 is receiver
     */
    //    static final int                   transmitter                = 0;
    //    static final int                   transciever                = 1;
    //    static final int                   receiver                   = 2;
    private static enum CONNECT_TYPE {

        TRANSMITTER, TRANSCIEVER, RECEIVER
    };
    // static int mode = transciever;
    private static CONNECT_TYPE mode;
    /**
     * Link checker
     */
    static LinkStatus link = null;
    /**
     * smsHolderProcess thread object
     */
    static smsHolderWorker smsHolderProcess = null;
    /*
     * values for submitting a message
     */
    /**
     * Идентификатор информационного канала
     */
    static String serviceType = "0";

    /*
     * Внимание! Значения параметров schedule_delivery_time и validity_period
     * задаются относительно локального времени Интерфейса, т.е. в формате
     * «YYMMDDhhmmsst00R».
     */
    /**
     * В случае если параметр schedule_delivery_time равен NULL, то сообщение
     * ставится в очередь на доставку немедленно. При корректном не NULL
     * значении данного параметра сообщение будет ожидать наступления времени
     * отложенного доведения.
     */
    final static String scheduleDeliveryTime = null;
    /**
     * В случае если параметр validity_period равен NULL, то срок годности
     * сообщения задается в соответствии с настройками Интерфейса.
     */
    final static String validityPeriod = null;
    /**
     * response from server that is id the msg sent to user
     */
    static String messageId = "";
    /**
     * esm_class должен быть равен 0x00, если сообщения без UDH 0x40 c UDH
     */
    final static byte esmClass = 0x40;
    /**
     * leave default
     */
    final static byte protocolId = 0;
    /**
     * leave default
     */
    final static byte priorityFlag = 0;
    /**
     * registered_delivery может иметь одно из следующих значений: 0x00 – не
     * требуется получение уведомления о доставке; 0x01 – требуется получение
     * уведомления о доставке.
     */
    final static byte registeredDelivery = 1;
    /**
     * leave default
     */
    final static byte replaceIfPresentFlag = 0;
    /**
     * leave default
     */
    final static byte dataCoding = 0x8;
    /**
     * leave default
     */
    final static byte smDefaultMsgId = 0;
    /**
     * Address referenece
     */
    static Address sourceAddress = new Address();
    /**
     * The range of addresses the smpp session will serve.
     */
    static AddressRange addressRange = new AddressRange();
    /**
     * specify receive timeout
     */
    private static int receiveTimeout = 20 * 1000;
    /**
     * Константа
     */
    public static final String WS_CONFIG = "websmsws";
    /**
     * Константа
     */
    private static final String SMS_MESSAGE_ID = "MESSAGEID";
    /**
     * Константа
     */
    private static final String SMS_SRC_NUMBER = "SRCNUMBER";
    /**
     * Константа
     */
    private static final String SMS_DST_NUMBER = "DSTNUMBER";
    /**
     * Константа
     */
    private static final String SMS_MSG_BODY = "MSGBODY";
    /**
     * Идетификатор документа
     */
    private static final String SMS_MSG_EXTID = "EXTID";
    /**
     * Константа
     */
    private static final String SMS_EXT_MESSAGE_ID = "EXTERNALID";
    /**
     * Константа
     */
    private static final String SMS_MESSAGE_STATUS = "STATUS";
    /**
     * Константа
     */
    private static final String SMS_MESSAGE_DATE = "DATA";
    /**
     * Константа
     */
    private static final String SMS_MESSAGE_EXTID = "EXTID";
    /**
     * Константа Сформировано
     */
    private static final int MSG_STATUS_PACKAGED = 10; //
    /**
     * Константа Доставлено в СМСЦ
     */
    private static final int MSG_STATUS_SENT_TO_SMSC = 20; //
    /**
     * Константа Отправлено
     */
    private static final int MSG_STATUS_SUBMITED = 30; //
    /**
     * Константа Доставлено
     */
    private static final int MSG_STATUS_RECEIVED = 40; //
    /**
     * Константа Удалено
     */
    private static final int MSG_STATUS_DELETED = 50; //
    /**
     * Константа Не доставлено
     */
    private static final int MSG_STATUS_NOT_RECEIVED = 60; //
    /**
     * Константа Отвергнуто
     */
    private static final int MSG_STATUS_DECLINED = 70; //
    static final String STATUS = "Status";
    static final String STATUS_OK = "OK";
    static final String STATUS_ERROR = "ERROR";
//    private ThreadLocal<ContextData> commandParams = new ThreadLocal<ContextData>();
    private static final ReceiveMailHandler receiveMailHandler = new ReceiveMailHandler();

    private static final ConfigHashMap<String, Object> config = new ConfigHashMap<String, Object>();

    public ConfigHashMap getConfig() {
        return this.config;
    }
    
    
/*    public ContextData getCommandParams() {
        return commandParams.get();
    }
*/
    /**
     * Конструктор, вызывается при старте сервиса
     */
    @PostConstruct
    public void myInit() {
        Map<String, Object> paramHolder = new HashMap<String, Object>();
        // store required parameters for connecting to SMSc
        paramHolder.put(SMSCENTERIP, config.getParam(SMSCENTERIP, "127.0.0.1"));
        paramHolder.put(SMSCENTERPORT, config.getParam(SMSCENTERPORT, "8080"));
        paramHolder.put(SMSCENTERSYSTEMID, config.getParam(SMSCENTERSYSTEMID, "0"));
        paramHolder.put(SMSCENTERPASSWORD, config.getParam(SMSCENTERPASSWORD, "12345678"));
        paramHolder.put(SMSCENTERSERVICETYPE, config.getParam(SMSCENTERSERVICETYPE, "0"));
        paramHolder.put(SMSCENTERSRCADDR, config.getParam(SMSCENTERSRCADDR, "1234567890"));
        try {
            mode = CONNECT_TYPE.valueOf(config.getParam("connection_type", "TRANSCIEVER"));
        } catch (Exception e) {
            mode = CONNECT_TYPE.TRANSCIEVER;
        }
        smsc_bind(paramHolder);// add your initialization code here
    }

    //debug f-n
    public void myinit(HashMap<String, Object> params) {
        myInit();
    }

    /**
     * Деструктор, вызывается при остановке сервиса
     */
    @PreDestroy
    public void myDestroy() {
        synchronized (over) {
            smsc_unbind();
            try {
                connection.close();
                session.close();
                over = true;
                connection = null;
            } catch (Exception e) {
                logger.error("Error closing connection " + e.getMessage());
            }
        }
    }

    /**
     * Метод осуществляет соединение с SMS центром
     *
     * @param paramHolder хранилище параметров для формирования запроса <BR>
     * <B>Параметры:</B> <BR>
     * <i>ip: IP адрес СМС центра</i><BR>
     * <i>SystemID: системный идентификатор для подключения к центру</i><BR>
     * <i>Password: пароль для подключения к центру</i><BR>
     * <i>ServiceType: тип подключения</i><BR>
     * <i>SourceAddr: номер телефона с которого отправляется смс</i><BR>
     * @return результат выполнения операции как bool
     */
    static private synchronized boolean smsc_bind(Map<String, Object> paramHolder) {

        // если у нас установлен флаг SKIP_SMS в TRUE, то выходим и ничего даже не пытаеся 
        // проинициализировать. Вообщем get outta here.
        if (config.getParam(SMS_SKIP, "FALSE").toUpperCase().equalsIgnoreCase("TRUE")) {
            return false;
        }
        logger.info("Trying to bind");
        logger.info("Thread name is  " + Thread.currentThread().getName() + " ID is " + Thread.currentThread().getId());
        if (bound) {
            return true;
        }

        BindRequest request = null;
        BindResponse response = null;
        try {
            switch (mode) {
                case TRANSCIEVER:
                    request = new BindTransciever();
                    break;
                case TRANSMITTER:
                    request = new BindTransmitter();
                    break;
                case RECEIVER:
                    request = new BindReceiver();
                    break;
                default:
                    logger.error("Invalid bind mode");
                    return false;
            }

            // specify connection
            connection = new TCPIPConnection((String) paramHolder.get(SMSCENTERIP), Integer.parseInt((String) paramHolder.get(SMSCENTERPORT)));
            connection.setReceiveTimeout(receiveTimeout);

            session = new Session(connection);

            // set values
            request.setSystemId((String) paramHolder.get(SMSCENTERSYSTEMID));
            request.setPassword((String) paramHolder.get(SMSCENTERPASSWORD));
            request.setSystemType((String) paramHolder.get(SMSCENTERSERVICETYPE));
            request.setInterfaceVersion((byte) 0x34);
            request.setAddressRange(addressRange);
            // and do not forget to define global variables
            sourceAddress.setAddress((String) paramHolder.get(SMSCENTERSRCADDR));
            String extParam = (String) paramHolder.get(SMSSOURCETON);
            if ((extParam != null) && (!("".equals(extParam)))) {
                sourceAddress.setTon(Byte.parseByte(extParam));
            }
            extParam = (String) paramHolder.get(SMSSOURCENPI);
            if ((extParam != null) && (!("".equals(extParam)))) {
                sourceAddress.setNpi(Byte.parseByte(extParam));
            }
            serviceType = (String) paramHolder.get(SMSCENTERSERVICETYPE);
            // send the request
            logger.info("Bind request " + request.debugString());
            if (asynchronous) {
                if (pduListener == null) {
                    pduListener = new WEBSMSIMPL().new SMPPTestPDUEventListener(session);
                }
                response = session.bind(request, pduListener);
            } else {
                response = session.bind(request);
            }
            logger.info("Bind response " + response.debugString());

            if (response.getCommandStatus() == Data.ESME_ROK) {
                bound = true;
            }
            return true;

        } catch (Exception e) {
            logger.error("Bind operation failed. " + e.getMessage());

        } finally {
            /*
             * let's start thread that will ask for link status every 60 seconds
             */
            //
            // executor = Executors.newFixedThreadPool(1);
            // link = new LinkStatus();
            // executor.execute(link);

            if (link == null || !link.isAlive()) {
                link = new LinkStatus();
                link.setDaemon(true);
                link.start();
            }
            // у ОСК оператора не будем использовать обходчик. Т.к. там реальизован немного 
            // другой принцип
            if (!config.getParam(SMSCOPERATOR, "SMSTRAFFIC").equalsIgnoreCase("OCK")) {

                if (smsHolderProcess == null) {
                    smsHolderProcess = new smsHolderWorker();
                    smsHolderProcess.setDaemon(true);
                    smsHolderProcess.start();
                }
            }
        }
        return false;
    }

    /**
     * Creates a new instance of <code>CancelSM</code> class, lets you set
     * subset of fields of it. This PDU is used to cancel an already submitted
     * message. You can only cancel a message which haven't been delivered to
     * the device yet.
     *
     * See "SMPP Protocol Specification 3.4, 4.9 CANCEL_SM Operation."
     *
     * @see Session#cancel(CancelSM)
     * @see CancelSM
     * @see CancelSMResp
     * @param number of a phone to cancel send operation as String
     * @param messageId to cancel send operation as String or null to cancel
     * send all messages
     */
    private boolean smsc_cancel(String number, String messageId) {
        logger.info("Entering smsc_cancel");
        if (number.equalsIgnoreCase("")) {
            return false;
        }
        if (messageId != null && messageId.equalsIgnoreCase("")) {
            messageId = null;
        }

        try {
            CancelSM request = new CancelSM();
            CancelSMResp response;

            // input values
            String destAddress = number;

            // set values
            request.setServiceType(serviceType);
            request.setMessageId(messageId);
            request.setSourceAddr(sourceAddress);
            request.setDestAddr(destAddress);

            // send the request
            logger.info("Cancel request " + request.debugString());
            if (asynchronous) {
                session.cancel(request);
            } else {
                response = session.cancel(request);
                logger.info("Cancel response " + response.debugString());
            }

        } catch (Exception e) {
            logger.error("Cancel operation failed. " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Отключает соединения с СМС центром
     *
     * @return результат выполнения операции как bool
     * @author kkachura
     */
    private boolean smsc_unbind() {

        try {
            if (!getBoundStatus()) {
                return false;
            }
            // send the request
            logger.info("Going to unbind.");
            if (session.getReceiver().isReceiver()) {
                logger.info("It can take a while to stop the receiver.");
            }
            UnbindResp response = session.unbind();
            logger.info("Unbind response " + response.debugString());
            setBoundStatus(false);
        } catch (Exception e) {

            logger.error("Unbind operation failed. " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Возвращает статус соединения с сервером СМС провайдера
     *
     * @return boolean как статус подключения
     */
    synchronized boolean getBoundStatus() {
        logger.info("gettin bound status " + bound);
        return bound;

    }

    /**
     * Возвращает флаг окончания работ Необходим для thread'а выполняющего опрос
     * центра
     *
     * @return boolean как статус
     */
    synchronized static boolean getOverFlagStatus() {
        logger.debug("gettin over status " + over);
        return over;
    }

    /**
     * Устанавливаем статус соединения
     *
     * @param статус, который необходимо установить, true - все хорошо, false в
     * другом случае
     */
    synchronized static void setBoundStatus(boolean status) {
        logger.debug("Setting bound status " + status);
        bound = status;
    }

    /**
     * Подготавливает сообщение к отправке и отправляет
     *
     * @param MESSAGE сообщение, которое необходимо послать
     * @param SMSNumber номер на который необходимо послать, null если послать
     * всем подписчикам канала
     * @param params map c параметрами, из него берется EXTID (в будущем могут
     * появиться другие параметры).
     */
    private boolean doSendMessageToSMSCenter(String MESSAGE, String SMSNumber, Map<String, Object> params) {
        if (!asynchronous) {
            // sync access to method
            synchronized (CONFIG) {
                if (!smsc_submit(MESSAGE, SMSNumber, params)) {
                    logger.error("Error occured on submiting message or connection not established.");
                    return false;
                }
            }
        } else {
            if (!smsc_submit(MESSAGE, SMSNumber, params)) {
                logger.error("Error occured on submiting message or connection not established.");
                return false;
            }
        }
        return true;

    }

    /**
     * Разбиение сообщения на части
     *
     * @param text который необходимо разбить
     * @return массив типа String который содержит разбитые строчки
     */
    private String[] splitMessage(String text) {
        int MAX_CHARS = 65;

        // return string without substringing
        if (text.length() <= MAX_CHARS) {
            String array[] = new String[1];
            array[0] = text;
            return array;
        }

        // get size of string and allocate array to store split string
        int result = (int) Math.ceil((text.length() / MAX_CHARS) + 0.5);
        String array[] = new String[result];
        int pointer = 0;
        for (int i = 0; i < result; i++) {

            try {
                array[i] = text.substring(pointer, MAX_CHARS + pointer);
            } catch (Exception e) {
                array[i] = text.substring(pointer, text.length());
            }
            pointer += MAX_CHARS;
        }
        return array;
    }

    private int getNextInt() {
        synchronized (message_group_id) {
            if (message_group_id > 127) {
                message_group_id = 1;
            } else {
                message_group_id++;
            }
        }
        return message_group_id;
    }

    /**
     * Creates a new instance of <code>SubmitSM</code> class, lets you set
     * subset of fields of it. This PDU is used to send SMS message to a device.
     *
     * See "SMPP Protocol Specification 3.4, 4.4 SUBMIT_SM Operation."
     *
     * @see Session#submit(SubmitSM)
     * @see SubmitSM
     * @see SubmitSMResp
     * @param message the message to send as String
     * @param number the number to send to as String object or null if send to
     * all subscribers
     * @param params map with parameters (EXTID)
     * @return boolean true of operation has succeeded ok, otherwise false
     */
    @SuppressWarnings("unchecked")
    private boolean smsc_submit(String message, String number, Map<String, Object> params) {
        // 		
        int MESSAGE_GROUPID_OFFSET = 3;
        int MESSAGE_COUNT_OFFSET = 4;
        int CURRENT_MESSAGE__OFFSET = 5;
        HashMap<String, smsMessage> smsHolder = new HashMap<String, smsMessage>();

        byte[] UDHheader = {0x05, 0x00, 0x03, 0x00, 0x00, 0x00};
        if (!getBoundStatus()) {
            return false;
        }

        if (number.equalsIgnoreCase("")) {
            number = null;
        }
        try {
            SubmitSMResp response;
            SubmitSM request;

            // Для работы с ОСК 
            if (config.getParam(SMSCOPERATOR, "SMSTRAFFIC").equalsIgnoreCase("OCK")) {
                request = new SubmitSM();
                request.setServiceType(serviceType);
                request.setSourceAddr(sourceAddress);
                request.setDestAddr(number);
                request.setReplaceIfPresentFlag(replaceIfPresentFlag);
                request.setScheduleDeliveryTime(null);
                request.setValidityPeriod(null);
                request.setEsmClass((byte) 0);
                request.setProtocolId(protocolId);
                request.setPriorityFlag(priorityFlag);
                request.setRegisteredDelivery(registeredDelivery);
                request.setDataCoding(dataCoding);
                request.setSmDefaultMsgId(smDefaultMsgId);

                ByteBuffer mesg = new ByteBuffer();
                mesg.appendString(message, "ISO-10646-UCS-2");
                request.setMessagePayload(mesg);
                request.assignSequenceNumber(true);
                if (asynchronous) {
                    logger.debug("SUBMIT_SM HEX DUMP " + toHexString(request.getData().getBuffer()));
                    session.submit(request);
                    System.out.println();
                } else {
                    response = session.submit(request);
                    HashMap<String, Object> databaseData = new HashMap<String, Object>();
                    databaseData.put(SMS_DST_NUMBER, number);
                    databaseData.put(SMS_MSG_BODY, message);
                    databaseData.put(SMS_MESSAGE_EXTID, params.get(SMS_MESSAGE_EXTID) == null ? "" : params.get(SMS_MESSAGE_EXTID));
                    processSubmitSMSresponse((PDU) response, databaseData);
                    logger.info("Submit response " + response.getCommandStatus());// debugString());
                }
                smsMessage sms = new smsMessage();
                sms.group_id = (byte) getNextInt();
                sms.message = message;
                sms.message_id = 0;
                sms.setstartupTime(new Date().getTime());
                sms.extEntityId = params.get(SMS_MESSAGE_EXTID) == null ? "" : params.get(SMS_MESSAGE_EXTID).toString();
                smsHolder.put(Integer.toString(sms.message_id), sms);
                messagePreventInformationHolder.put(Integer.toString(sms.group_id), smsHolder);
                logger.info("Using OPERATOR " + config.getParam(SMSCOPERATOR, "SMSTRAFFIC"));
                return true;
            }

            String[] splittedMessage = splitMessage(message);

            int msg_counter = 0;

            // устанавливаем общее количество сообщений
            // set SAR parameters
            // это следует использовать когда SMS провайдер
            // не поддерживает UDH заголовки в сообщениях
            // request.setSarTotalSegments((byte) splittedMessage.length);
            // set UDH
            UDHheader[MESSAGE_GROUPID_OFFSET] = (byte) getNextInt();
            for (String msg : splittedMessage) {
                request = new SubmitSM();
                msg_counter++;
                // set values
                request.setServiceType(serviceType);

                // Допустимо указать NULL,
                // что приведет к принятию в
                // качестве идентификатора отправителя значения по умолчанию.
                // но желательно указать ID пользователя, которое было выдано
                // СМСц
                request.setSourceAddr(sourceAddress);
                // Можно указать null, тогда сообщение будет отослано всем
                // подписчикам канала
                request.setDestAddr(number);
                String extParam = config.getParam(SMSDESTTON, null);
                if ((extParam != null) && (!("".equals(extParam)))) {
                    request.getDestAddr().setTon(Byte.parseByte(extParam));
                }
                extParam = config.getParam(SMSDESTNPI, null);
                if ((extParam != null) && (!("".equals(extParam)))) {
                    request.getDestAddr().setNpi(Byte.parseByte(extParam));
                }
                request.setReplaceIfPresentFlag(replaceIfPresentFlag);
                // вполне возможно что при использовании SAR опций нужно
                // сообщение
                // сохранять так. Не тестировалось.
                // request.setShortMessage(msg,Data.ENC_UTF16_BE);
                request.setScheduleDeliveryTime(null);
                request.setValidityPeriod(null);
                request.setEsmClass(esmClass);
                request.setProtocolId(protocolId);
                request.setPriorityFlag(priorityFlag);
                request.setRegisteredDelivery(registeredDelivery);
                request.setDataCoding(dataCoding);
                request.setSmDefaultMsgId(smDefaultMsgId);

                // set SAR PARAMETERS
                // ком-ии смотрим вышел
                // request.setSarMsgRefNum((short)1);
                // request.setSarSegmentSeqnum((short)msg_counter);
                // UDH
                // здесь происходит формирование UDH заголовка перед отправкой
                // сообщения
                ByteBuffer mesg = new ByteBuffer();
                UDHheader[MESSAGE_COUNT_OFFSET] = (byte) splittedMessage.length;
                UDHheader[CURRENT_MESSAGE__OFFSET] = (byte) msg_counter;
                mesg.appendBytes(UDHheader);
                mesg.appendString(msg, Data.ENC_UTF16_BE);

                ByteBuffer body = request.getBody();
                byte[] bodyByte = body.getBuffer();
                bodyByte[bodyByte.length - 1] = (byte) mesg.length();
                body = new ByteBuffer();
                body.appendBytes(bodyByte);
                body.appendBuffer(mesg);

                request.setBody(body);

                smsMessage sms = new smsMessage();
                sms.group_id = UDHheader[MESSAGE_GROUPID_OFFSET];
                sms.message = message;
                sms.message_id = UDHheader[CURRENT_MESSAGE__OFFSET];
                sms.setstartupTime(new Date().getTime());
                sms.extEntityId = params.get(SMS_MESSAGE_EXTID) == null ? "" : params.get(SMS_MESSAGE_EXTID).toString();

                // if (messageInformationHolder.get(sms.group_id) == null)
                // проверку на наличие существующий группы с номером не делаем,
                // т.к.
                // все сообщения по достижению лимита групп начают цикл с начала
                if (messageInformationHolder.get(Integer.toString(sms.group_id)) == null) {
                    smsHolder.put(Integer.toString(sms.message_id), sms);
                    messageInformationHolder.put(Integer.toString(sms.group_id), smsHolder);
                } else {
                    smsHolder = (HashMap<String, smsMessage>) messageInformationHolder.get(Integer.toString(sms.group_id));
                    smsHolder.put(Integer.toString(sms.message_id), sms);
                    messageInformationHolder.put(Integer.toString(sms.group_id), smsHolder);
                }

                // send the request
                int count = 1;
                for (int i = 0; i < count; i++) {
                    request.assignSequenceNumber(true);
                    logger.info("#" + i + "  ");
                    logger.info("Submit request " + request.debugString());
                    if (asynchronous) {
                        logger.debug("SUBMIT_SM HEX DUMP " + toHexString(request.getData().getBuffer()));
                        session.submit(request);
                        System.out.println();
                    } else {
                        response = session.submit(request);
                        HashMap<String, Object> databaseData = new HashMap<String, Object>();
                        databaseData.put(SMS_DST_NUMBER, number);
                        databaseData.put(SMS_MSG_BODY, message);
                        databaseData.put(SMS_MESSAGE_EXTID, params.get(SMS_MESSAGE_EXTID) == null ? "" : params.get(
                                SMS_MESSAGE_EXTID).toString());
                        processSubmitSMSresponse((PDU) response, databaseData);
                        logger.info("Submit response " + response.getCommandStatus());// debugString());
                    }
                }

            }

            return true;
        } catch (Exception e) {
            logger.error("Error submiting sms " + e.getMessage());
            return false;
        }
    }

    /**
     * Отправка сообщения по почте
     *
     * @param params хранилище параметров для формирования запроса <BR>
     * <B>Параметры:</B> <BR>
     * <i>SMTPSubject: сообщение</i><BR>
     * <i>SMTPReceipt: получатель</i><BR>
     * <i>SMTPMESSAGE: сообщение</i><BR>
     * <BR>
     * @return в данный момомент возвращается null
     * @author kkachura
     */
    public Map<String, Object> mailmessage(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params == null) {
            logger.error("mailMessage:error no parameters received");
            result.put("Result", "Error");
            return (Map<String, Object>) result;
        }
        // get message
        String MESSAGE = (String) params.get(constsSections.SMTPMESSAGE);

        if (MESSAGE == null || MESSAGE.equalsIgnoreCase("")) {
            logger.error("mailMessage:cannot send empty mail");
            result.put("Result", "Error");
            return (Map<String, Object>) result;
        }

        try {
            if (MailBlock.getInstance(config).mailConnectSend(MESSAGE, params)) {
                result.put("Result", "Ok");
                return (Map<String, Object>) result;
            } else {
                result.put("Result", "Error");
                return (Map<String, Object>) result;
            }
        } catch (Exception e) {
            logger.error("mailMessage:error occured during message processing " + e.getMessage());
            result.put("Result", "Error");
            return (Map<String, Object>) result;
        }
    }

    /**
     * Подготавливает сообщение к отправке и отправляет
     *
     * @param params хранилище параметров <BR>
     * <B>Параметры:</B> <BR>
     * <i>SMSMessage: сообщение</i><BR>
     * <i>SMSNum: получатель</i><BR>
     *
     * @return SMSResponse <BR>
     * <B>Возвращаемые поля:</B><BR>
     * <i>SMSResponse:String:сообщение от отправке</i><BR>
     *
     */
    public Map<String, Object> dispatchsmsmessage(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params == null) {
            logger.error("dispatchsmsmessage:error no parameters received");
            result.put("Result", "Error");
            return result;
        }

        // get sms message and number to send to
        String SMSMESSAGE = (String) params.get(constsSections.SMSMESSAGE);
        String SMSnumber = (String) params.get(constsSections.SMSNUMBER);

        // send to SMSc
        if (SMSnumber == null || SMSnumber.equalsIgnoreCase("")) {
            logger.error("Number can't be empty or null");
            result.put("Result", "Error");
            return result;
        }
        if (SMSMESSAGE == null) {
            logger.error("Message can't be empty or null");
            result.put("Result", "Error");
            return result;
        }
        if (doSendMessageToSMSCenter(SMSMESSAGE, SMSnumber, params)) {
            // pack response
            result.put("Result", "Ok");
        } else {
            result.put("Result", "Error");
        }
        return result;
    }

    /**
     * Creates a new instance of <code>QuerySM</code> class, lets you set subset
     * of fields of it. This PDU is used to fetch information about status of
     * already submitted message providing that you 'remember' message id of the
     * submitted message. The message id is assigned by SMSC and is returned to
     * you with the response to the submision PDU (SubmitSM, DataSM etc.).
     *
     * See "SMPP Protocol Specification 3.4, 4.8 QUERY_SM Operation."
     *
     * @see Session#query(QuerySM)
     * @see QuerySM
     * @see QuerySMResp
     */
    private void smsc_query(String messageID) {
        try {
            QuerySM request = new QuerySM();
            QuerySMResp response;

            // set values
            request.setMessageId(messageID);
            request.setSourceAddr(sourceAddress);

            // send the request
            logger.info("Query request " + request.debugString());
            if (asynchronous) {
                session.query(request);
            } else {
                response = session.query(request);
                logger.info("Query response " + response.debugString());

                /*
                 * NOTE!!!! This synchronization should be removed as message is
                 * message id will be passed from outside
                 */
                synchronized (messageId) {
                    messageId = response.getMessageId();
                }

            }

        } catch (Exception e) {
            logger.error("Error querying message " + e.getMessage());
        }
    }

    /**
     * Логирование ответов из SMSc
     *
     * @param message сообщение
     * @param messageId идентификатор сообщения
     */
    private void logAction(String message, int messageId) {
        logger.info("MessageID " + messageId + ":" + message);
    }

    /**
     * Возвращает id таблицы
     *
     * @param имя таблицы, чей ID нужно вернуть
     * @return id таблицы
     */
    private int getTableId(String tableName) {
        try {
            return -1;
            /*return QueryBuilder.getNewId(Config.getConfig(WS_CONFIG).getParam("corews",
             "http://localhost:8080/corews/corews"), tableName, 1);*/
        } catch (Exception e) {
            logger.error("Error occured while getting table id  " + e.getLocalizedMessage());
            return -1;
        }
    }

    /**
     *
     * Обарботка сообщения SUBMIT_SM
     *
     * @param pdu объект
     */
    private void processSubmitSMSresponse(PDU pdu, HashMap<String, Object> databaseData) {
        int messageId = 0;
        int id = 0;
        SubmitSMResp obj = (SubmitSMResp) pdu;
        try {
            logger.debug("SUBMIT_SM_RESP HEX DUMP " + toHexString(obj.getData().getBuffer()));
        } catch (ValueNotSetException e1) {
        }
        try {
            String holder = obj.getMessageId().toString();

            if (holder != null && !holder.equalsIgnoreCase("")) {
                // get external message id
                messageId = Integer.parseInt(holder);
            } else {
                logger.debug("Unable to get message id. Returning... ");
                return;
            }
            String messageBody = "";
            String extId = "";
            if (config.getParam(SMSCOPERATOR, "SMSTRAFFIC").equalsIgnoreCase("OCK")) {
                Set<String> entry = messagePreventInformationHolder.keySet();
                String key = entry.iterator().next();
                HashMap<String, Object> groupHolder = (HashMap<String, Object>) messagePreventInformationHolder.get(key);
                for (String mkey : groupHolder.keySet()) {
                    smsMessage mess = (smsMessage) groupHolder.get(mkey);
                    messageBody = mess.message;
                    extId = String.valueOf(mess.extEntityId);
                }
                messagePreventInformationHolder.remove(key);
                messageInformationHolder.put(holder, groupHolder);
            }

            switch (pdu.getCommandStatus()) {

                case (org.smpp.Data.ESME_ROK): {
                    logAction("No Error" + ":SubmitSMSresponse", messageId);
                    id = getTableId("SMS_MESSAGE");
                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_SUBMITED);
                        databaseData.put(SMS_MESSAGE_DATE, new Date());
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
                case (org.smpp.Data.ESME_RINVREGDLVFLG): {

                    logAction("Invalid Registered Delivery Flag" + ":SubmitSMSresponse", messageId);

                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
                case (org.smpp.Data.ESME_RSYSERR): {
                    logAction("System Error" + ":SubmitSMSresponse", messageId);
                    id = getTableId("SMS_MESSAGE");
                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
                case (org.smpp.Data.ESME_RINVSRCADR): {
                    logAction("Invalid Source Address" + ":SubmitSMSresponse", messageId);
                    id = getTableId("SMS_MESSAGE");
                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
                case (org.smpp.Data.ESME_RINVDSTADR): {
                    logAction("Invalid Dest Addr" + ":SubmitSMSresponse", messageId);
                    id = getTableId("SMS_MESSAGE");
                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
                case (org.smpp.Data.ESME_RINVSERTYP): {
                    logAction("Invalid Service Type" + ":SubmitSMSresponse", messageId);
                    id = getTableId("SMS_MESSAGE");
                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
                case (org.smpp.Data.ESME_RINVESMCLASS): {
                    logAction("Invalid esm_class Field Data" + ":SubmitSMSresponse", messageId);
                    id = getTableId("SMS_MESSAGE");
                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
                case (org.smpp.Data.ESME_RSUBMITFAIL): {
                    logAction("SUBMIT_SM Failed" + ":SubmitSMSresponse", messageId);
                    id = getTableId("SMS_MESSAGE");
                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
                case (org.smpp.Data.ESME_RTHROTTLED): {
                    id = getTableId("SMS_MESSAGE");
                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        logAction("Throttling Error" + ":SubmitSMSresponse", messageId);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
                case (org.smpp.Data.ESME_RINVSCHED): {
                    logAction("Invalid Scheduled Delivery Time" + ":SubmitSMSresponse", messageId);
                    id = getTableId("SMS_MESSAGE");
                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
                case (org.smpp.Data.ESME_RINVEXPIRY): {
                    logAction("Invalid Message Validity Period" + ":SubmitSMSresponse", messageId);
                    id = getTableId("SMS_MESSAGE");
                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
                default: {
                    logAction("Unknown response for this transaction" + ":SubmitSMSresponse", messageId);
                    id = getTableId("SMS_MESSAGE");
                    if (id != -1) {
                        databaseData.put(SMS_MESSAGE_ID, id);
                        databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                        databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                        databaseData.put(SMS_MSG_BODY, messageBody);
                        databaseData.put(SMS_MSG_EXTID, extId);
                        //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            if (obj != null) {
                logAction("Error parsing response for the transaction: " + obj.debugString() + " " + e.getMessage(), 0);
            } else {
                logAction("Error parsing response for the transaction: " + e.getMessage(), 0);
            }

            id = getTableId("SMS_MESSAGE");
            if (id != -1) {
                databaseData.put(SMS_MESSAGE_ID, id);
                databaseData.put(SMS_EXT_MESSAGE_ID, messageId);
                databaseData.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
                //dbstoreSMSmsgin(databaseData, SMS_STORE_QUERY_NAME);
            }
        }
    }

    /**
     *
     * QUERY_SM is parsed
     *
     * @param messageState статус сообщения
     * @param messageId идентификатор сообщения
     */
    private void parseQueryState(int messageState, int messageId) {
        final int ENROUTE = 0x01; // Сообщение доставляется
        final int DELIVERED = 0x02; // Сообщение успешно доставлено *
        final int EXPIRED = 0x03; // Сообщение не было доставлено, поскольку
        // срок годности сообщения истек *
        final int DELETED = 0x04; // Сообщение было удалено *
        final int UNDELIVERABLE = 0x05; // Сообщение не может быть доставлено,
        // поскольку в процессе доставки
        // возникла неустранимая ошибка *
        final int ACCEPTED = 0x06; // Сообщение успешно доставлено и было
        // прочитано *
        final int UNKNOWN = 0x07; // Сообщение не может быть доставлено,
        // поскольку сообщение находится в
        // некорректном состоянии *
        final int REJECTED = 0x08; // Сообщение отвергнуто *
        final int DISCARDED = 0x09; // Сообщение отвергнуто при постановке на
        // доставку *
        final int INDEFINITE = 0x0A; // Состояние доставки сообщения не может
        // быть определено *

        switch (messageState) {
            case (ENROUTE): {
                logAction("Message is being sent" + ":QuerySMSresponse", messageId);
                break;
            }
            case (DELIVERED): {
                logAction("Message has been sent" + ":QuerySMSresponse", messageId);
                break;
            }
            case (EXPIRED): {
                logAction("Message has not been sent.Expired." + ":QuerySMSresponse", messageId);
                break;
            }
            case (DELETED): {
                logAction("Message has been deleted." + ":QuerySMSresponse", messageId);
                break;
            }
            case (UNDELIVERABLE): {
                logAction("Unable to deliver message" + ":QuerySMSresponse", messageId);
                break;
            }
            case (ACCEPTED): {
                logAction("MessageAccepted" + ":QuerySMSresponse", messageId);
                break;
            }
            case (UNKNOWN): {
                logAction("Message state unknown" + ":QuerySMSresponse", messageId);
                break;
            }
            case (REJECTED): {
                logAction("Message rejected" + ":QuerySMSresponse", messageId);
                break;
            }
            case (DISCARDED): {
                logAction("Message discarded" + ":QuerySMSresponse", messageId);
                break;
            }
            case (INDEFINITE): {
                logAction("Message indefinite" + ":QuerySMSresponse", messageId);
                break;
            }
            default: {
                logAction("Message uknown" + ":QuerySMSresponse", messageId);
                break;
            }
        }
    }

    /**
     * Обработка QUERY_SM
     *
     * @param messageStatus статус сообщения
     * @param messageId идентификатор сообщения
     * @return
     */
    private boolean parseQueryStatus(int messageStatus, int messageId) {

        switch (messageStatus) {
            case (org.smpp.Data.ESME_ROK): {
                logAction("No Error" + ":QueryStatus", messageId);
                return true;
            }
            case (org.smpp.Data.ESME_RSYSERR): {
                logAction("System Error" + ":QueryStatus", messageId);
                return false;
            }
            case (org.smpp.Data.ESME_RINVSRCADR): {
                logAction("Invalid Source Address" + ":QueryStatus", messageId);
                return false;
            }
            case (org.smpp.Data.ESME_RINVMSGID): {
                logAction("Message ID is Invalid" + ":QueryStatus", messageId);
                return false;
            }
            case (org.smpp.Data.ESME_RTHROTTLED): {
                logAction("Throttling Error" + ":QueryStatus", messageId);
                return false;
            }
            default: {
                return false;
            }
        }
    }

    /**
     * Обработка CANCEL_SM
     *
     * @param PDU объект
     */
    private void processCancelSMresponse(PDU pdu) {
        try {
            CancelSMResp obj = (CancelSMResp) pdu;
            int messageId = obj.getSequenceNumber();

            // nothin' to do quit
            if (obj == null) {
                return;
            }
            switch (obj.getCommandStatus()) {
                case org.smpp.Data.ESME_ROK: {
                    logAction("No Error" + ":CancelSM", messageId);
                    break;
                }
                case org.smpp.Data.ESME_RSYSERR: {
                    logAction("System Error" + ":CancelSM", messageId);
                    break;
                }
                case org.smpp.Data.ESME_RINVSRCADR: {
                    logAction("Invalid Source Address" + ":CancelSM", messageId);
                    break;
                }
                case org.smpp.Data.ESME_RINVMSGID: {
                    logAction("Message ID is Invalid" + ":CancelSM", messageId);
                    break;
                }
                case org.smpp.Data.ESME_RTHROTTLED: {
                    logAction("Throttling Error" + ":CancelSM", messageId);
                    break;
                }
                case org.smpp.Data.ESME_RCANCELFAIL: {
                    logAction("CANCEL_SM Request Failed" + ":CancelSM", messageId);
                    break;
                }
                default: {
                    logAction("Unknown response from host" + ":CancelSM", messageId);
                    break;
                }

            }
        } catch (Exception e) {
            logAction("An error occured :CancelSM " + e.getMessage(), 0);
        }
    }

    /**
     * Processes link response
     *
     * @param pdu
     */
    private void processEnquireLinkRespose(PDU pdu) {
        logger.debug("Alive response from SMSc received.");
        setBoundStatus(true);
    }

    /**
     * Обработка запроса состояния
     *
     * @param PDU объект
     */
    private void processEnquireLinkRequest(PDU pdu) {
        try {
            EnquireLink resp = (EnquireLink) pdu;
            org.smpp.pdu.Response respObj = resp.getResponse();
            // get outta here nothin' to do
            if (respObj == null) {
                return;
            }
            try {
                logger.debug("Responding enqlink request");
                session.respond(respObj);
            } catch (ValueNotSetException e) {
                logger.error("processEnquireLinkRequest - ValueNotSetException" + e.getMessage());
            } catch (WrongSessionStateException e) {
                logger.error("processEnquireLinkRequest - WrongSessionStateException" + e.getMessage());
            } catch (IOException e) {
                logger.error("processEnquireLinkRequest - IOException" + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("processEnquireLinkRequest - Exception" + e.getMessage());
        }
    }

    /**
     * Обработка сообщения от хоста
     *
     * @param stringtoProcess строка для обработки
     * @return обработанная строка
     */
    private HashMap<String, Object> parseDeliverResponse(String stringtoProcess) {
        // String "id:1260566821 sub:001 dlvrd:001 submit date:0809261434 done
        // date:0809261434 stat:DELIVRD err:0 text:dfdfa";
        String tempString = stringtoProcess.replaceAll("err:\\d", "");
        // clear double space
        stringtoProcess = tempString.replaceAll("  ", " ");
        // remove date
        tempString = stringtoProcess.replaceAll("date", "");
        // replace spaces with :
        stringtoProcess = tempString.replaceAll(" ", ":");
        // replace all where :: are occured with :
        tempString = stringtoProcess.replaceAll("::", ":");
        // split to use further
        String result[] = tempString.split(":");
        HashMap<String, Object> parsedHodler = new HashMap<String, Object>();
        // now form hash map
        for (int i = 0; i < result.length; i++) {
            try {
                if (result[i].equalsIgnoreCase("text")) {
                    parsedHodler.put(result[i], "");
                    break;
                }

                parsedHodler.put(result[i].trim(), result[++i].trim());
            } catch (Exception e) {
                // if we are here garbaged received from host go on parsing
                continue;
            }
        }

        parsedHodler.put(SMS_EXT_MESSAGE_ID, parsedHodler.get("id"));
        parsedHodler.put(SMS_MSG_BODY, (String) parsedHodler.get("text"));
        /*
         * try { String temporary = ((String) parsedHodler.get("text")); byte[]
         * encString = temporary.getBytes("Cp1251");
         * parsedHodler.put(SMS_MSG_BODY,new String(encString,"Cp1251")); }
         * catch (UnsupportedEncodingException e) {
         * parsedHodler.put(SMS_MSG_BODY,"Unsupported encoding");; }
         */

        if (((String) parsedHodler.get("stat")).equalsIgnoreCase("DELIVRD")
                || ((String) parsedHodler.get("stat")).equalsIgnoreCase("DELIVERED")) {
            parsedHodler.put(SMS_MESSAGE_STATUS, MSG_STATUS_RECEIVED);
        }
        if (((String) parsedHodler.get("stat")).equalsIgnoreCase("EXPIRED")
                || ((String) parsedHodler.get("stat")).equalsIgnoreCase("EXPIRED")) {
            parsedHodler.put(SMS_MESSAGE_STATUS, MSG_STATUS_DECLINED);
        }
        if (((String) parsedHodler.get("stat")).equalsIgnoreCase("DELETED")
                || ((String) parsedHodler.get("stat")).equalsIgnoreCase("DELETED")) {
            parsedHodler.put(SMS_MESSAGE_STATUS, MSG_STATUS_DELETED);
        }
        if (((String) parsedHodler.get("stat")).equalsIgnoreCase("REJECTD")
                || ((String) parsedHodler.get("stat")).equalsIgnoreCase("REJECTED")) {
            parsedHodler.put(SMS_MESSAGE_STATUS, MSG_STATUS_NOT_RECEIVED);
        }
        if (((String) parsedHodler.get("stat")).equalsIgnoreCase("UNDELIV")
                || ((String) parsedHodler.get("stat")).equalsIgnoreCase("UNDELIVERED")) {
            parsedHodler.put(SMS_MESSAGE_STATUS, MSG_STATUS_NOT_RECEIVED);
        }
        return parsedHodler;
    }

    /**
     * Извлечения сообщения из полученных данных
     */
    @SuppressWarnings({"unchecked"})
    private Map<String, Object> extractMessageFromData(byte[] asdf, HashMap<String, Object> parsedHolder) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int size = asdf.length - 2;
        int group_id = 0;
        int message_id = 0;
        String result = "no message found in response";
        HashMap<String, Object> groupHolder = null;
        try {
            // groupIdHolder имеет  размер больший нуля значит обрабатываем OCK   
            if (groupIdHolder.size() == 0) {
                for (; size > 0; size--) {
                    // начинаем с конца сообщения
                    byte temper = (byte) asdf[size];
                    // ищем UDH header
                    // UDH header found stop further execution
                    if (temper == 0x5) {
                        if (asdf[size + 1] == 0x00) {
                            if (asdf[size + 2] == 0x03) {
                                // нашли номер группы к которой принадлежит
                                // сообщение
                                group_id = asdf[size + 3];
                                message_id = asdf[size + 5];
                            }
                        }
                        break;
                    }
                }
                groupHolder = (HashMap<String, Object>) messageInformationHolder.get(Integer.toString(group_id));
                if (config.getParam(SMSCOPERATOR, "SMSTRAFFIC").equalsIgnoreCase("OCK")) {
                    groupHolder = (HashMap<String, Object>) messageInformationHolder.get(parsedHolder.get("id"));
                    messageInformationHolder.remove(parsedHolder.get("id"));
                }
            } else {
                // обрабатываем  
                group_id = Integer.parseInt(groupIdHolder.get(0).toString());
                groupHolder = (HashMap<String, Object>) messageInformationHolder.get(Integer.toString(group_id));
                // зачищаем пустые группы
                again:
                if (groupHolder.isEmpty()) {
                    groupIdHolder.remove(0);
                    group_id = Integer.parseInt(groupIdHolder.get(0).toString());
                    groupHolder = (HashMap<String, Object>) messageInformationHolder.get(Integer.toString(group_id));
                    break again;
                }
                Set<Map.Entry<String, Object>> s = groupHolder.entrySet();

                // do it via cycle. Later it will be useful 
                for (Map.Entry<String, Object> entry : s) {
                    message_id = Integer.parseInt(entry.getKey().toUpperCase());
                    break;
                }
                // only one message in group 
                if (groupHolder.size() == 1) {
                    groupIdHolder.remove(0);
                }
            }
            // вытащили структуру
            smsMessage messageInfo = (smsMessage) groupHolder.get(Integer.toString(message_id));
            if (messageInfo != null) {

                // смотрим сообщение первое из группы, сохраняем внешний ИД в
                // классе
                // таким образом, что все остальные сообщения из группы будут
                // сохранены
                // в базе с ссылкой на родительское
                if (messageInfo.ext_id == 0) {
                    // вытащили внешний ИД сообщения и сохранили его
                    // в базе вытащенный ИД будет использоваться дочерними
                    // сообщениями как ссылка на родительское сообщение
                    messageInfo.ext_id = Integer.parseInt(((String) parsedHolder.get(SMS_EXT_MESSAGE_ID)));
                    groupHolder.put(Integer.toString(message_id), messageInfo);
                    messageInformationHolder.put(Integer.toString(group_id), groupHolder);
                    // а теперь проставляем всем сообщениям в нашей грумме поле ext_id равные ext_id родительского 
                    // сообщения
                    // Получаем список все хранящихся hashmapов в группе
                    Collection<Object> inHolder = groupHolder.values();

                    // 
                    for (Object m : inHolder) {
                        ((smsMessage) m).ext_id = messageInfo.ext_id;
                    }

                } // обработка следущего сообщения группы.
                else {
                    parsedHolder.put(SMS_MSG_BODY, "");
                    parsedHolder.put("SPLITMSG", messageInfo.ext_id);
                    // update message in DB
                    //dbupdateSMSmsgin(parsedHolder, SMS_UPDATE_QUERY_NAME);
                    // insert info in SMS_HIST in DB
                    //dbupdateSMSmsgin(parsedHolder, SMS_HIST_INSERT_QUERY_NAME);
                    return null;
                }
                groupHolder.remove(Integer.toString(message_id));
                resultMap.put(SMS_MSG_BODY, messageInfo.message);
                resultMap.put(SMS_MESSAGE_EXTID, messageInfo.extEntityId);
                return resultMap;
            } else {
                return null;
            }
            /*
             * byte[] holder = new byte[m_buffer.position()]; m_buffer.flip();
             * byte[] array = m_buffer.array(); // now get readable string
             * for(int j = 0;j<array.length;j++) { if(array[j] != 0) holder[j] =
             * array[holder.length - (j+1)]; } String tempRes = new
             * String(holder,"UTF-16LE").trim(); result = new
             * String((tempRes).getBytes("UTF-8"),"UTF-8");
             */
        } catch (Exception e) {
            logger.error("Error occured while parsing response from outer SMSC host " + e.getMessage());
        }
        resultMap.put(SMS_MSG_BODY, result);
        return resultMap;
    }

    /**
     * Обработка DELIVER_SM
     *
     * @param PDU объект
     */
    private void processDeliverRequest(PDU pdu) {
        String destinationAddr = null;
        String sourceAddr = null;
        HashMap<String, Object> parsedHodler = null;
        try {
            DeliverSM delobj = (DeliverSM) pdu;

            // получение адреса доставки
            sourceAddr = delobj.getDestAddr().getAddress();
            // получение адреса отправителя
            destinationAddr = delobj.getSourceAddr().getAddress();
            // сообщение
            logger.info("Recieved DELIVER_SM with message " + delobj.getShortMessage());
            logger.debug("Recieved DELIVER_SM with message in HEX is :" + toHexString(pdu.getData().getBuffer()));
            parsedHodler = parseDeliverResponse(delobj.getShortMessage());

            // store source and destination addresses from DELIVER_SM
            parsedHodler.put(SMS_MESSAGE_ID, getTableId("SMS_HIST"));
            parsedHodler.put(SMS_SRC_NUMBER, sourceAddr);
            parsedHodler.put(SMS_DST_NUMBER, destinationAddr);
            parsedHodler.put(SMS_MESSAGE_DATE, new Date());
            // extract message from data
            ByteBuffer buffer = pdu.getData();

            Map<String, Object> resultMap = extractMessageFromData(buffer.getBuffer(), parsedHodler);
            // если вернулся null то никаках сохранений в базу не делаем, все
            // что
            // нужно будет сохранено в extractMessageFromData
            if (resultMap != null) {
                parsedHodler.put(SMS_MSG_BODY, resultMap.get(SMS_MSG_BODY));
                parsedHodler.put(SMS_MESSAGE_EXTID, resultMap.get(SMS_MESSAGE_EXTID));
                parsedHodler.put("SPLITMSG", 0);
                // update message in db
                //dbupdateSMSmsgin(parsedHodler, SMS_UPDATE_QUERY_NAME);
                // insert info in SMS_HIST in db
                //dbupdateSMSmsgin(parsedHodler, SMS_HIST_INSERT_QUERY_NAME);
            }
        } catch (Exception e) {
            logger.error("Error occured  on processDeliverRequest" + e.getMessage());
        } finally {
            // подготавливаем ответ
            DeliverSMResp resp = new DeliverSMResp();
            try {
                String id = (String) parsedHodler.get("id");
                if (id == null) {
                    return;
                } else {
                    resp.setMessageId(id);
                    if (session != null) {
                        logger.info("calling DeliverSMResp with id = " + id);
                        session.respond(resp);
                    } else {
                        logger.error("Session object is null strange");
                    }
                }
            } catch (WrongLengthOfStringException e) {
                logger.error("Error sending DELIVER_SM response  " + e.getMessage());
            } catch (ValueNotSetException e) {
                logger.error("Error sending DELIVER_SM response  " + e.getMessage());
            } catch (IOException e) {
                logger.error("Error sending DELIVER_SM response  " + e.getMessage());
            } catch (WrongSessionStateException e) {
                logger.error("Error sending DELIVER_SM response  " + e.getMessage());
            }
        }
    }

    /**
     * Обработка QuerySMSResponse
     *
     * @param pdu object
     */
    private void processQuerySMSresponse(PDU pdu) {
        try {
            QuerySMResp obj = (QuerySMResp) pdu;
            int messageId = Integer.parseInt(obj.getMessageId());

            if (parseQueryStatus(pdu.getCommandStatus(), messageId)) {
                parseQueryState(obj.getMessageState(), messageId);
            }
        } catch (Exception e) {
            logger.error("processQuerySMSresponse " + e.getMessage());
        }
    }

    /**
     * Implements simple PDU listener which handles PDUs received from SMSC. It
     * puts the received requests into a queue and discards all received
     * responses. Requests then can be fetched (should be) from the queue by
     * calling to the method <code>getRequestEvent</code>.
     *
     * @see Queue
     * @see ServerPDUEvent
     * @see ServerPDUEventListener
     * @see SmppObject
     */
    private class SMPPTestPDUEventListener extends SmppObject implements ServerPDUEventListener {

        Session session;
        Queue requestEvents = new Queue();

        public SMPPTestPDUEventListener(Session session) {
            this.session = session;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.smpp.ServerPDUEventListener#handleEvent(org.smpp.ServerPDUEvent)
         */
        public void handleEvent(ServerPDUEvent event) {
            PDU pdu = event.getPDU();
            if (pdu.isRequest()) {

                if (pdu.getCommandId() == org.smpp.Data.DELIVER_SM) {

                    processDeliverRequest(pdu);
                } else if (pdu.getCommandId() == org.smpp.Data.ENQUIRE_LINK) {
                    processEnquireLinkRequest(pdu);
                } else {
                    logger.error("pdu of unknown request " + pdu.debugString());
                }
                /*
                 * logger.info("async request received, enqueuing " +
                 * pdu.debugString()); synchronized (requestEvents) {
                 * requestEvents.enqueue(event); requestEvents.notify(); }
                 */
            } else if (pdu.isResponse()) {
                // process submit response
                if (pdu.getCommandId() == org.smpp.Data.SUBMIT_SM_RESP) {
                    HashMap<String, Object> databaseData = new HashMap<String, Object>();
                    databaseData.put(SMS_DST_NUMBER, "0");
                    databaseData.put(SMS_MSG_BODY, "");
                    processSubmitSMSresponse(pdu, databaseData);
                } // process query response
                else if (pdu.getCommandId() == org.smpp.Data.QUERY_SM_RESP) {
                    processQuerySMSresponse(pdu);
                } // process link status response
                else if (pdu.getCommandId() == org.smpp.Data.ENQUIRE_LINK_RESP) {
                    processEnquireLinkRespose(pdu);
                } else if (pdu.getCommandId() == org.smpp.Data.CANCEL_SM_RESP) {
                    processCancelSMresponse(pdu);
                } else {
                    logger.error("pdu of unknown response " + pdu.debugString());
                }

            } else {
                logger.error("pdu of unknown class (not request nor " + "response) received, discarding "
                        + pdu.debugString());
            }
        }

        /**
         * Returns received pdu from the queue. If the queue is empty, the
         * method blocks for the specified timeout.
         */
        public ServerPDUEvent getRequestEvent(long timeout) {
            ServerPDUEvent pduEvent = null;
            synchronized (requestEvents) {
                if (requestEvents.isEmpty()) {
                    try {
                        requestEvents.wait(timeout);
                    } catch (InterruptedException e) {
                        // ignoring, actually this is what we're waiting for
                    }
                }
                if (!requestEvents.isEmpty()) {
                    pduEvent = (ServerPDUEvent) requestEvents.dequeue();
                }
            }
            return pduEvent;
        }
    }

    /**
     * Creates a new instance of <code>EnquireSM</code> class. This PDU is used
     * to check that application level of the other party is alive. It can be
     * sent both by SMSC and ESME.
     *
     * See "SMPP Protocol Specification 3.4, 4.11 ENQUIRE_LINK Operation."
     *
     * @see Session#enquireLink(EnquireLink)
     * @see EnquireLink
     * @see EnquireLinkResp
     */
    private static void smsc_enquireLink() {
        try {
            EnquireLink request = new EnquireLink();
            EnquireLinkResp response;
            logger.debug("Enquire Link request " + request.debugString());
            if (asynchronous) {
                session.enquireLink(request);
            } else {
                response = session.enquireLink(request);
                logger.debug("Enquire Link response " + response.debugString());
            }

        } catch (Exception e) {
            logger.error("Enquire Link operation failed. " + e.getMessage());
            // we do not check boolean flag
            // just a simple unbind
            // smsc_unbind();
            // reset
            // and re-bind
            setBoundStatus(false);
            Map<String, Object> paramHolder = new HashMap<String, Object>();
            // store required parameters for connecting to SMSc
            paramHolder.put(SMSCENTERIP, config.getParam(SMSCENTERIP, "127.0.0.1"));
            paramHolder.put(SMSCENTERPORT, config.getParam(SMSCENTERPORT, "8080"));
            paramHolder.put(SMSCENTERSYSTEMID, config.getParam(SMSCENTERSYSTEMID, "0"));
            paramHolder.put(SMSCENTERPASSWORD, config.getParam(SMSCENTERPASSWORD, "12345678"));
            paramHolder.put(SMSCENTERSERVICETYPE, config.getParam(SMSCENTERSERVICETYPE, "0"));
            paramHolder.put(SMSCENTERSRCADDR, config.getParam(SMSCENTERSRCADDR, "1234567890"));
            smsc_bind(paramHolder);
        }
    }

    /**
     * Convertion byte array to hex string
     *
     * @param array
     * @return
     */
    private static String toHexString(byte[] array) {
        if (array == null) {
            return null;
        }
        final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuffer ss = new StringBuffer(array.length * 3);
        for (int i = 0; i < array.length; i++) {
            // ss.append(' ');
            ss.append(hex[(array[i] >>> 4) & 0xf]);
            ss.append(hex[array[i] & 0xf]);
        }
        return ss.toString();
    }

    public static class smsHolderWorker extends Thread {
        // Установим время обхода 
        // необработанные смски будут удаляться через 5 минут из хранилища
        // дабы не засарять и не висеть в памяти неудаленными

        long startUP = 60000l * 5;
        Collection<Object> inHolder = null;
        Iterator outerObj = null;
        Iterator innerObj = null;

        public void run() {
            logger.info("smsHolderWorker running");

            while (true) {

                try {
                    Thread.sleep(startUP);

                    inHolder = messageInformationHolder.values();
                    outerObj = inHolder.iterator();
                    while (outerObj.hasNext()) {
                        try {
                            Collection<Object> in = ((HashMap) outerObj.next()).values();
                            innerObj = in.iterator();
                            while (innerObj.hasNext()) {
                                Object c = innerObj.next();
                                if (((smsMessage) c).getstartupTime() + startUP >= new Date().getTime()) {
                                    innerObj.remove();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception a) {
                }
            }
        }
    }

    /**
     * That is the class responsible for checking if connection is alive
     *
     * @author kkatchura
     */
    public static class LinkStatus extends Thread {
        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Thread#run()
         */

        public void run() {
            logger.info("LinkStatus thread started");

            while (!getOverFlagStatus()) {
                smsc_enquireLink();
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    logger.debug("LinkStatus thread sleep interrupted " + e.getMessage());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void wholeproctest(Map<String, Object> params) {

        String message = "Это мега тестовое сообщение призванное сделать то то то Вообщем юзабильность форева. dfsf";
        String number = "79104555545435";
        int MESSAGE_GROUPID_OFFSET = 3;
        int MESSAGE_COUNT_OFFSET = 4;
        int CURRENT_MESSAGE__OFFSET = 5;
        HashMap<String, smsMessage> smsHolder = new HashMap<String, smsMessage>();
        byte[] UDHheader = {0x05, 0x00, 0x03, 0x00, 0x00, 0x00};

        if (number.equalsIgnoreCase("")) {
            number = null;
        }
        try {
            String[] splittedMessage = splitMessage(message);

            int msg_counter = 0;

            SubmitSM request;
            // устанавливаем общее количество сообщений
            // set SAR parameters
            // это следует использовать когда SMS провайдер
            // не поддерживает UDH заголовки в сообщениях
            // request.setSarTotalSegments((byte) splittedMessage.length);
            // set UDH
            UDHheader[MESSAGE_GROUPID_OFFSET] = (byte) getNextInt();
            for (String msg : splittedMessage) {
                request = new SubmitSM();
                msg_counter++;
                // set values
                request.setServiceType(serviceType);

                // Допустимо указать NULL,
                // что приведет к принятию в
                // качестве идентификатора отправителя значения по умолчанию.
                // но желательно указать ID пользователя, которое было выдано
                // СМСц
                request.setSourceAddr(sourceAddress);
                // Можно указать null, тогда сообщение будет отослано всем
                // подписчикам канала
                request.setDestAddr(number);

                request.setReplaceIfPresentFlag(replaceIfPresentFlag);
                // вполне возможно что при использовании SAR опций нужно
                // сообщение
                // сохранять так. Не тестировалось.
                // request.setShortMessage(msg,Data.ENC_UTF16_BE);
                request.setScheduleDeliveryTime(null);
                request.setValidityPeriod(null);
                request.setEsmClass(esmClass);
                request.setProtocolId(protocolId);
                request.setPriorityFlag(priorityFlag);
                request.setRegisteredDelivery(registeredDelivery);
                request.setDataCoding(dataCoding);
                request.setSmDefaultMsgId(smDefaultMsgId);

                // set SAR PARAMETERS
                // ком-ии смотрим вышел
                // request.setSarMsgRefNum((short)1);
                // request.setSarSegmentSeqnum((short)msg_counter);
                // UDH
                // здесь происходит формирование UDH заголовка перед отправкой
                // сообщения
                ByteBuffer mesg = new ByteBuffer();
                UDHheader[MESSAGE_COUNT_OFFSET] = (byte) splittedMessage.length;
                UDHheader[CURRENT_MESSAGE__OFFSET] = (byte) msg_counter;
                mesg.appendBytes(UDHheader);
                mesg.appendString(msg, Data.ENC_UTF16_BE);

                ByteBuffer body = request.getBody();
                byte[] bodyByte = body.getBuffer();
                bodyByte[bodyByte.length - 1] = (byte) mesg.length();
                body = new ByteBuffer();
                body.appendBytes(bodyByte);
                body.appendBuffer(mesg);

                request.setBody(body);

                smsMessage sms = new smsMessage();
                sms.group_id = UDHheader[MESSAGE_GROUPID_OFFSET];
                sms.message = message;
                sms.message_id = UDHheader[CURRENT_MESSAGE__OFFSET];
                sms.extEntityId = params.get(SMS_MESSAGE_EXTID) == null ? "" : params.get(SMS_MESSAGE_EXTID).toString();
                sms.setstartupTime(new Date().getTime());

                // if (messageInformationHolder.get(sms.group_id) == null)
                // проверку на наличие существующий группы с номером не делаем,
                // т.к.
                // все сообщения по достижению лимита групп начают цикл с начала
                if (messageInformationHolder.get(Integer.toString(sms.group_id)) == null) {
                    smsHolder.put(Integer.toString(sms.message_id), sms);
                    messageInformationHolder.put(Integer.toString(sms.group_id), smsHolder);
                } else {
                    smsHolder = (HashMap<String, smsMessage>) messageInformationHolder.get(Integer.toString(sms.group_id));
                    smsHolder.put(Integer.toString(sms.message_id), sms);
                    messageInformationHolder.put(Integer.toString(sms.group_id), smsHolder);
                }
            }
            //-----------------------------------------------------------------------------
            for (int i = 0; i < 2; i++) {

                SubmitSMResp smsResp = new SubmitSMResp();
                if (i == 0) {
                    smsResp.setData(new ByteBuffer(
                            toBinArray("0000001B8000000400000000000000043133333830383637353000")));
                } else {
                    smsResp.setData(new ByteBuffer(
                            toBinArray("0000001B8000000400000000000000053133333830383637353200")));
                }

                HashMap<String, Object> databaseData = new HashMap<String, Object>();
                databaseData.put(SMS_DST_NUMBER, "0");
                databaseData.put(SMS_MSG_BODY, "");
                processSubmitSMSresponse((PDU) smsResp, databaseData);

                DeliverSM delSM = new DeliverSM();
                delSM.setSourceAddr(sourceAddress);
                delSM.setDestAddr(number);

                if (i == 0) {
                    delSM.setData(new ByteBuffer(
                            toBinArray("000000AF00000005000000000003E5A300000037393130363633323330360000003739303332313536393335000400000000000000007869643A31333338303836373530207375623A30303120646C7672643A303031207375626D697420646174653A3039303430313131313720646F6E6520646174653A3039303430313131313720737461743A44454C49565244206572723A3020746578743A050003010201042D0442043E0020043C04350433")));
                } else {
                    delSM.setData(new ByteBuffer(
                            toBinArray("000000AF00000005000000000003E5C500000037393130363633323330360000003739303332313536393335000400000000000000007869643A31333338303836373532207375623A30303120646C7672643A303031207375626D697420646174653A3039303430313131313720646F6E6520646174653A3039303430313131313720737461743A44454C49565244206572723A3020746578743A0500030102020437043004310438043B044C043D")));
                }
                processDeliverRequest(delSM);

            }

        } catch (Exception e) {
            logger.error("Error submiting sms " + e.getMessage());

        }

    }

    private static byte[] toBinArray(String hexStr) {
        byte bArray[] = new byte[hexStr.length() / 2];
        for (int i = 0; i < (hexStr.length() / 2); i++) {
            byte firstNibble = Byte.parseByte(hexStr.substring(2 * i, 2 * i + 1), 16); // [x,y)
            byte secondNibble = Byte.parseByte(hexStr.substring(2 * i + 1, 2 * i + 2), 16);
            int finalByte = (secondNibble) | (firstNibble << 4); // bit-operations only with numbers, not bytes.
            bArray[i] = (byte) finalByte;
        }
        return bArray;
    }
}
