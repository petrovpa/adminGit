package com.bivgroup.flextera.insurance.mail.websms;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.log4j.Logger;

public class ReceiveMailHandler {

    public static final String MAIL_BOXES_PARAM = "mail-boxes";
    public static final String MAIL_BOX_PARAM = "mail-box";
    public static final String EMAIL_PARAM = "email";
    public static final String LOGIN_PARAM = "login";
    public static final String PASSWORD_PARAM = "password";
    public static final String SERVER_PARAM = "server";
    public static final String PROTOCOL_PARAM = "protocol";

    public static final String MAIL_MESSAGE_RECEIVER_EMAIL_LIST = "MailMessageReceiverEmailList";
    public static final String RECEIVER_EMAIL = "ReceiverEmail";
    public static final String MAIL_MESSAGE_INCOMING_LIST = "MailMessageIncomingList";
    public static final String MAIL_MESSAGE_ID = "MailMessageID";
    public static final String MAIL_MSG_DATE = "MailMsgDate";
    public static final String MAIL_MSG_SIZE = "MailMsgSize";
    public static final String MAIL_MSG_SUBJECT = "MailMsgSubject";
    public static final String MAIL_MSG_TYPE = "MailMsgType";
    public static final String RECEIVER_NAME = "ReceiverName";
    public static final String SENDER_EMAIL = "SenderEmail";
    public static final String SENDER_NAME = "SenderName";
    public static final String SENDER_CONTENTTYPE = "ContentType";
    public static final String ATTACHMENT_FLAG = "AttachmentFlag";
    public static final String MAIL_MESSAGE_ID_LIST = "MailMessageIDList";
    public static final String MAIL_MESSAGE_BODY = "MailMessageBody";
    public static final String MAIL_MESSAGE_DECODEFLAG = "MailMessageDecodeFlag";

    public static final String NOTIFICATION_LIST = "NotificationList";
    public static final String NTFID = "NTFID";
    public static final String NTF_MESSAGE = "NTFMessage";

    public static final String DS_MAIL_MESSAGE_FIND_LIST_INCOMING_HEADER_BY_PARAM = "dsMailMessageFindListIncomingHeaderByParam";
    public static final String DS_MAIL_MESSAGE_FIND_LIST_INCOMING_HEADER_BY_LIST_ID = "dsMailMessageFindListIncomingByListID";
    public static final String DS_MAIL_MESSAGE_DELETE_BY_LIST_ID = "dsMailMessageDeleteByListID";

    private static final long CODE_WRONG_ID = 100;
    private static final long CODE_ID_NOT_FOUND = 200;
    private static final long CODE_EXCEPTION = 300;

    private static Logger logger = Logger.getLogger(ReceiveMailHandler.class);
    private static final ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
    private static final Lock rlock = rwlock.readLock();
    private static final Lock wlock = rwlock.writeLock();
    private final ConfigHashMap<String, Object> config = new ConfigHashMap<String, Object>();
    
    public ConfigHashMap getConfig() {
        return this.config;
    }

    private class MailBoxEntry {

        public String email;
        public String login;
        public String password;
        public String server;
        public String protocol;
        public Session session;
        public Store store;
        public Folder folder;
        public Map<Long, Message> messages;
    }

    private Map<String, MailBoxEntry> mailBoxes = null;

    @SuppressWarnings("unchecked")
    public ReceiveMailHandler() {
        Map<String, Object> mailBoxesParams = (Map<String, Object>) config.get(MAIL_BOXES_PARAM);
        if (mailBoxesParams == null || mailBoxesParams.isEmpty()) {
            logger.error("There is no configured mail boxes for mail receiver. Mail reciever can't work.");
            return;
        }

        mailBoxes = new HashMap<String, MailBoxEntry>();
        Collection<Object> mailBoxesCollect = mailBoxesParams.values();
        Map<String, String> mailBoxMap;
        MailBoxEntry mailBoxEntry;
        for (Object mailBoxObj : mailBoxesCollect) {
            mailBoxMap = (Map<String, String>) mailBoxObj;
            mailBoxEntry = new MailBoxEntry();
            mailBoxEntry.email = mailBoxMap.get(EMAIL_PARAM);
            mailBoxEntry.login = mailBoxMap.get(LOGIN_PARAM);
            mailBoxEntry.password = mailBoxMap.get(PASSWORD_PARAM);
            mailBoxEntry.server = mailBoxMap.get(SERVER_PARAM);
            mailBoxEntry.protocol = mailBoxMap.get(PROTOCOL_PARAM);
            mailBoxEntry.messages = new HashMap<Long, Message>();
            mailBoxes.put(mailBoxEntry.email, mailBoxEntry);
        }
    }

    public Method canHandle(String commandtext) {
        Method method = null;
        try {
            if (DS_MAIL_MESSAGE_FIND_LIST_INCOMING_HEADER_BY_PARAM.equalsIgnoreCase(commandtext)) {
                method = this.getClass().getMethod(DS_MAIL_MESSAGE_FIND_LIST_INCOMING_HEADER_BY_PARAM, Map.class);
            } else if (DS_MAIL_MESSAGE_FIND_LIST_INCOMING_HEADER_BY_LIST_ID.equalsIgnoreCase(commandtext)) {
                method = this.getClass().getMethod(DS_MAIL_MESSAGE_FIND_LIST_INCOMING_HEADER_BY_LIST_ID, Map.class);
            } else if (DS_MAIL_MESSAGE_DELETE_BY_LIST_ID.equalsIgnoreCase(commandtext)) {
                method = this.getClass().getMethod(DS_MAIL_MESSAGE_DELETE_BY_LIST_ID, Map.class);
            }
        } catch (NoSuchMethodException e) {
            //Не отлавливаем - нормальная ситуация
        }
        return method;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> dsMailMessageFindListIncomingHeaderByParam(Map<String, Object> params)
            throws ReceiveMailHandlerException {
        Map<String, Object> result = new HashMap<String, Object>();
        if (mailBoxes == null || mailBoxes.isEmpty()) {
            logger.error("There is no configured mail boxes for mail receiver. Mail reciever can't work.");
            createErrorResult(result);
            return result;
        }
        checkRequiredParams(params, MAIL_MESSAGE_RECEIVER_EMAIL_LIST);
        List<Map<String, Object>> emailList = (List<Map<String, Object>>) params.get(MAIL_MESSAGE_RECEIVER_EMAIL_LIST);
        String email;
        MailBoxEntry mailBoxEntry;
        List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
        result.put(MAIL_MESSAGE_INCOMING_LIST, messageList);
        Map<String, Object> messageMap;
        Collection<Message> messages;

        wlock.lock();
        try {
            for (Map<String, Object> emailMap : emailList) {
                email = (String) emailMap.get(RECEIVER_EMAIL);
                if (email == null || email.isEmpty()) {
                    logger.warn("Parameter '" + RECEIVER_EMAIL + "' is empty.");
                    continue;
                }
                mailBoxEntry = mailBoxes.get(email);
                if (mailBoxEntry == null) {
                    logger.warn("Mailbox " + email + " nor found in websmsws configuration.");
                    continue;
                }

                checkConnect(mailBoxEntry);
                readMessagesToEntry(mailBoxEntry, true);

                messages = mailBoxEntry.messages.values();
                for (Message message : messages) {
                    messageMap = new HashMap<String, Object>();
                    packMessageHeader(messageMap, message, email);
                    messageList.add(messageMap);
                }
                createSuccessResult(result);
            }
        } finally {
            wlock.unlock();
        }
        return result;
    }

    /**
     * По идентификатору вытаскивает письмо
     *
     * @param MailMessageIDList
     * @param ReceiverEmail
     * @param MailMessageDecodeFlag	- не обязательный параметр флаг
     * декодирования письма. true -
     *
     * @param params
     * @return
     * @throws ReceiveMailHandlerException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> dsMailMessageFindListIncomingByListID(Map<String, Object> params)
            throws ReceiveMailHandlerException {
        Map<String, Object> result = new HashMap<String, Object>();
        if (mailBoxes == null || mailBoxes.size() == 0) {
            logger.error("There is no configured mail boxes for mail receiver. Mail reciever can't work.");
            createErrorResult(result);
            return result;
        }
        checkRequiredParams(params, MAIL_MESSAGE_ID_LIST, RECEIVER_EMAIL);
        boolean decodeFlag = false;
        if (params.containsKey(MAIL_MESSAGE_DECODEFLAG)) {
            decodeFlag = Boolean.valueOf(params.get(MAIL_MESSAGE_DECODEFLAG).toString());
        }

        List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
        result.put(MAIL_MESSAGE_INCOMING_LIST, messageList);

        List<Map<String, Object>> idList = (List<Map<String, Object>>) params.get(MAIL_MESSAGE_ID_LIST);
        String email = (String) params.get(RECEIVER_EMAIL);
        MailBoxEntry mailBoxEntry = mailBoxes.get(email);
        if (mailBoxEntry == null) {
            logger.error("Mailbox " + email + " nor found in websmsws configuration.");
            createErrorResult(result);
            return result;
        }

        Long id;
        Message message;
        Map<String, Object> messageMap;
        rlock.lock();
        try {
            checkConnect(mailBoxEntry);
            readMessagesToEntry(mailBoxEntry, false);

            for (Map<String, Object> idMap : idList) {
                id = (Long) idMap.get(MAIL_MESSAGE_ID);
                if (id == null) {
                    logger.warn("Wrong message id.");
                    continue;
                }
                message = mailBoxEntry.messages.get(id);
                if (message == null) {
                    logger.warn("Message with id=" + id + " not found in mailbox '" + mailBoxEntry.email + "'.");
                    continue;

                }
                messageMap = new HashMap<String, Object>();
                packMessageHeader(messageMap, message, email);
                packMessageBody(messageMap, message, email);

                if (decodeFlag) {
                    // переведём из Base64 в текст
                    String decodedMessage = decodeMessage(messageMap.get(MAIL_MESSAGE_BODY).toString(),
                            messageMap.get(SENDER_CONTENTTYPE).toString());
                    messageMap.put(MAIL_MESSAGE_BODY, decodedMessage);
                }

                messageList.add(messageMap);
            }
        } finally {
            rlock.unlock();
        }
        createSuccessResult(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> dsMailMessageDeleteByListID(Map<String, Object> params)
            throws ReceiveMailHandlerException {

        Map<String, Object> result = new HashMap<String, Object>();
        if (mailBoxes == null || mailBoxes.size() == 0) {
            logger.error("There is no configured mail boxes for mail receiver. Mail reciever can't work.");
            createErrorResult(result);
            return result;
        }
        checkRequiredParams(params, MAIL_MESSAGE_ID_LIST, RECEIVER_EMAIL);
        List<Map<String, Object>> idList = (List<Map<String, Object>>) params.get(MAIL_MESSAGE_ID_LIST);

        List<Map<String, Object>> notificationList = new ArrayList<Map<String, Object>>();
        result.put(NOTIFICATION_LIST, notificationList);

        String email = (String) params.get(RECEIVER_EMAIL);
        MailBoxEntry mailBoxEntry = mailBoxes.get(email);
        if (mailBoxEntry == null) {
            logger.error("Mailbox " + email + " nor found in websmsws configuration.");
            createErrorResult(result);
            return result;
        }

        Long id;
        Message message;
        wlock.lock();
        try {
            checkConnect(mailBoxEntry);
            readMessagesToEntry(mailBoxEntry, false);

            try {
                for (Map<String, Object> idMap : idList) {
                    id = (Long) idMap.get(MAIL_MESSAGE_ID);
                    if (id == null) {
                        logger.warn("Wrong message id.");
                        packNotificationList(notificationList, null, "Wrong message id.", CODE_WRONG_ID);
                        continue;
                    }
                    message = mailBoxEntry.messages.get(id);
                    if (message == null) {
                        logger.warn("Message with id=" + id + " not found in mailbox '" + mailBoxEntry.email + "'.");
                        packNotificationList(notificationList, id,
                                "Message with id=" + id + " not found in mailbox '" + mailBoxEntry.email + "'.",
                                CODE_ID_NOT_FOUND);
                        continue;
                    }
                    try {
                        message.setFlag(Flag.DELETED, true);
                    } catch (Exception e) {
                        logger.error(e);
                        packNotificationList(notificationList, id, e.getMessage(), CODE_EXCEPTION);
                        continue;
                    }
                }
                mailBoxEntry.folder.close(true);
                createSuccessResult(result);
            } catch (MessagingException e) {
                logger.error(e);
                createErrorResult(result);
            }
        } finally {
            wlock.unlock();
        }
        return result;
    }

    private void checkRequiredParams(Map<String, Object> params, String... names)
            throws ReceiveMailHandlerException {
        for (int i = 0; i < names.length; i++) {
            if (params.get(names[i]) == null) {
                throw new ReceiveMailHandlerException("Required parameter '" + names[i] + "' not set.");
            }
        }
    }

    private void checkConnect(MailBoxEntry mailBoxEntry) throws ReceiveMailHandlerException {
        if (mailBoxEntry.session == null) {
            createSession(mailBoxEntry);
        }
        doConnect(mailBoxEntry);
    }

    private void createSession(MailBoxEntry mailBoxEntry) throws ReceiveMailHandlerException {
        try {
            Properties props = System.getProperties();
            mailBoxEntry.session = Session.getInstance(props, null);
            mailBoxEntry.store = mailBoxEntry.session.getStore(mailBoxEntry.protocol);
        } catch (MessagingException e) {
            throw new ReceiveMailHandlerException(e);
        }
    }

    private void doConnect(MailBoxEntry mailBoxEntry) throws ReceiveMailHandlerException {
        try {
            if (!mailBoxEntry.store.isConnected()) {
                mailBoxEntry.store.connect(mailBoxEntry.server, mailBoxEntry.login, mailBoxEntry.password);
                mailBoxEntry.folder = mailBoxEntry.store.getFolder("INBOX");
            }
            if (!mailBoxEntry.folder.isOpen()) {
                mailBoxEntry.folder.open(Folder.READ_WRITE);
            }
        } catch (MessagingException e) {
            throw new ReceiveMailHandlerException(e);
        }
    }

    private void readMessagesToEntry(MailBoxEntry mailBoxEntry, boolean refresh) throws ReceiveMailHandlerException {
        try {
            if (!mailBoxEntry.messages.isEmpty() && !refresh && !mailBoxEntry.folder.hasNewMessages()) {
                return;
            }
            if (refresh) {
                mailBoxEntry.store.close();
                doConnect(mailBoxEntry);
            }
            Message[] messages = mailBoxEntry.folder.getMessages();
            Message message;
            mailBoxEntry.messages.clear();
            for (int i = 0; i < messages.length; i++) {
                message = messages[i];
                mailBoxEntry.messages.put(new Long(message.getMessageNumber()), message);
            }
        } catch (MessagingException e) {
            throw new ReceiveMailHandlerException(e);
        }
    }

    private void packMessageHeader(Map<String, Object> messageMap, Message message, String email)
            throws ReceiveMailHandlerException {
        try {
            messageMap.put(MAIL_MESSAGE_ID, new Long(message.getMessageNumber()));
            messageMap.put(MAIL_MSG_DATE, message.getSentDate());
            messageMap.put(MAIL_MSG_SIZE, message.getSize());
            messageMap.put(MAIL_MSG_SUBJECT, message.getSubject());
            messageMap.put(MAIL_MSG_TYPE, 0);
            messageMap.put(RECEIVER_EMAIL, email);
            messageMap.put(RECEIVER_NAME, "");
            messageMap.put(SENDER_EMAIL, message.getFrom().length > 0 ? message.getFrom()[0].toString() : "");
            messageMap.put(SENDER_NAME, "");
            if (message.isMimeType("multipart/*")) {
                messageMap.put(ATTACHMENT_FLAG, true);
            } else {
                messageMap.put(ATTACHMENT_FLAG, false);
            }
            messageMap.put(SENDER_CONTENTTYPE, message.getContentType());
        } catch (MessagingException e) {
            throw new ReceiveMailHandlerException(e);
        }
    }

    private void packMessageBody(Map<String, Object> messageMap, Message message, String email)
            throws ReceiveMailHandlerException {
        try {

            InputStream is = message.getInputStream();
            Base64InputStream encodedStream = new Base64InputStream(is, true);
            StringBuffer buffer = new StringBuffer(message.getSize());
            byte buf[] = new byte[1024];
            int readCount;
            while ((readCount = encodedStream.read(buf)) != -1) {
                buffer.append(new String(buf, 0, readCount));
            }
            messageMap.put(MAIL_MESSAGE_BODY, buffer.toString());

        } catch (Exception e) {
            throw new ReceiveMailHandlerException(e);
        }
    }

    private void createSuccessResult(Map<String, Object> result) {
        result.put("Result", "Ok");

    }

    private void createErrorResult(Map<String, Object> result) {
        result.put("Result", "Error");
    }

    private void packNotificationList(List<Map<String, Object>> notificationList, Long messageId, String message, Long errorCode) {
        Map<String, Object> notification = new HashMap<String, Object>();
        notification.put(MAIL_MESSAGE_ID, messageId);
        notification.put(NTFID, errorCode);
        notification.put(NTF_MESSAGE, message);
    }

    /**
     * Преобразует сообщение в читаемый формат
     *
     * @param mailMessageBody - текст соообщения
     * @param contentType - тег из заголовка
     *
     * @return
     */
    private String decodeMessage(String mailMessageBody, String contentType) {
        String charSet = null;
        if (contentType.indexOf("charset=\"") != -1) {
            int idx = contentType.indexOf("charset=\"") + 9;
            charSet = contentType.substring(idx, contentType.indexOf("\"", idx));
        }
        return decodeBase64(mailMessageBody, charSet);
    }

    /**
     * Base64 преобразует в текст. Входящие параметры текст для преобразования и
     * кодировка.
     *
     * @param cx
     * @param thisObj
     * @param args
     * @param funObj
     * @return
     */
    private String decodeBase64(String text, String charSet) {
        String result = "";

        if (text.length() > 0) {
            /**
             * кодировка может быть не передана
             */
            if (charSet == null || charSet.isEmpty()) {
                result = new String(Base64.decodeBase64(text.getBytes()));
            } else {
                /**
                 * кодировка передана
                 */
                try {
                    result = new String(Base64.decodeBase64(text.getBytes()), charSet);
                } catch (UnsupportedEncodingException e) {
                    logger.debug("error by decodeMessage ----- ", e);;
                    return null;
                }
            }
        }

        return result;
    }

}
