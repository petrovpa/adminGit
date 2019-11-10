/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.flextera.insurance.mail.websms;

import static com.bivgroup.flextera.insurance.mail.websms.constsSections.ContentTransferEncoding;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.ContentType;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.HTMLText;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.MIMEVersion;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.ReturnPath;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.SMTPAttachmentMap;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.SMTPFROM;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.SMTPHOST;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.SMTPPASSWORD;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.SMTPPORT;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.SMTPReceipt;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.SMTPSUBJECT;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.SMTPUSER;
import static com.bivgroup.flextera.insurance.mail.websms.constsSections.XMailer;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import org.apache.log4j.Logger;

/**
 * Основная задача класса MailBlock состоит в отправке сообщений по электронной
 * почте. Класс использует внешнюю вспомогательную библиотеку smtp.jar. Для
 * отправки сообщения используется метод mailConnectSend.
 *
 * @author kkachura
 */
public class MailBlock implements constsSections, ConnectionListener, TransportListener {
    // var section

    /**
     * Объект класса Logger
     */
    private static Logger logger = Logger.getLogger(MailBlock.class);
    /**
     * Флаг идентификатор того, что listener для работы с SMS центром
     * утсановлени
     */
    protected boolean listenerInstalled = false;
    /**
     * Ссылка на класс MailBlock
     */
    private static MailBlock instance = new MailBlock(new HashMap<String, Object>());
    /**
     * Ссылка на класс Transport
     */
    private static Transport tr = null;

    /**
     * Констроктор класса MailBlock
     */
    protected MailBlock(Map<String, Object> cfg) {
        this.config = new ConfigHashMap<String, Object>();
        this.config.putAll(cfg);
    }
    ;

    private final ConfigHashMap<String, Object> config;

    public ConfigHashMap getConfig() {
        return this.config;
    }

    /**
     * Метод возвращает возвращает экземпляр класса MailBlock
     *
     * @return объект класса MailBlock
     * @author kkachura
     */
    public static MailBlock getInstance(Map<String, Object> cfg) {
        if (null != cfg) {
            instance.config.clear();
            instance.config.putAll(cfg);
        }

        return instance;
    }

    /**
     * Проверка наличия attachment Files
     *
     * @param attachmentMap
     * @return
     */
    private Map<String, String> checkAttachmentExists(Map<String, String> attachmentMap) {
        Map<String, String> result = null;
        if ((attachmentMap != null) && !attachmentMap.isEmpty()) {
            for (Map.Entry<String, String> attachmentEntry : attachmentMap.entrySet()) {
                if ((attachmentEntry.getKey() != null) && (!attachmentEntry.getKey().isEmpty())
                        && (attachmentEntry.getValue() != null) && (!attachmentEntry.getValue().isEmpty())) {
                    File attachmentFile = new File(attachmentEntry.getValue());
                    if (attachmentFile.isFile() && attachmentFile.exists() && attachmentFile.canRead()) {
                        if (result == null) {
                            result = new HashMap<String, String>();
                        }
                        result.put(attachmentEntry.getKey(), attachmentEntry.getValue());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Добавление аттачей в письмо
     *
     * @param message
     * @param attachmentMap
     * @throws MessagingException
     */
    private void addAttachments(Multipart multipart, Map<String, String> attachmentMap) throws MessagingException, UnsupportedEncodingException {

        if ((attachmentMap != null)) {
            for (Map.Entry<String, String> attachmentEntry : attachmentMap.entrySet()) {
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                String attachmentFileName = attachmentEntry.getValue();
                String attachmentName = attachmentEntry.getKey();
                javax.activation.DataSource source = new javax.activation.FileDataSource(attachmentFileName);
                messageBodyPart.setDataHandler(new javax.activation.DataHandler(source));
                messageBodyPart.setFileName(MimeUtility.encodeText(attachmentName, "utf-8", "B"));
                messageBodyPart.setHeader("Content-Type", source.getContentType());
                multipart.addBodyPart(messageBodyPart);
            }
        }
    }

    /**
     * Отправка сообщения на хост
     *
     * @param param - хранилище параметров для отправки электронного письма <BR>
     * <B>Параметры:</B> <BR>
     * <i>SMTPSubject: сообщение</i><BR>
     * <i>SMTPReceipt: электронный адрес получателя письма</i><BR>
     * @return результат выполнения операции
     * @author kkachura
     */
    @SuppressWarnings("finally")
    public boolean mailConnectSend(String Message, Map<String, Object> params) throws Exception {
        // get required params
        String ServerPass = config.getParam(SMTPPASSWORD, "a@a.ru");
        String UserName = config.getParam(SMTPUSER, "anonymous");
        String Host = config.getParam(SMTPHOST, "127.0.0.1");
        String From = config.getParam(SMTPFROM, "root@localhost");
        String Port = config.getParam(SMTPPORT, "root@localhost");

        logger.info("ServerPass: " + ServerPass);
        logger.info("UserName: " + UserName);
        logger.info("Host: " + Host);
        logger.info("From: " + From);
        logger.info("Port: " + Port);

        String Subject = (String) params.get(SMTPSUBJECT);
        String Receipts = (String) params.get(SMTPReceipt);
        logger.info("Subject: " + Subject);
        logger.info("Receipts: " + Receipts);
        logger.info("Message: " + Message);

        if (Subject == null) {
            Subject = "";
        }
        if (Receipts == null) {
            Receipts = "";
        }

        Properties props = System.getProperties();

        if (Host != null) {
            props.put("mail.smtp.host", Host);
        }
        if ((config.get("USESMTPSSL") != null) && Boolean.parseBoolean(config.get("USESMTPSSL").toString())) {
            props.put("mail.smtps.ssl.checkserveridentity", "false");
            props.put("mail.smtps.ssl.trust", "*");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true"); 
        }
        if ((config.get("USEIMAPSSL") != null) && Boolean.parseBoolean(config.get("USESMTPSSL").toString())) {
            props.put("mail.imap.ssl.checkserveridentity", "false");
            props.put("mail.imap.ssl.trust", "*");
        }

        javax.mail.Session session = javax.mail.Session.getInstance(props, null);

        // enable full debug
        //session.setDebug(true);
        // construct the message
        Message msg = new MimeMessage(session);

        if (From != null) {
            msg.setFrom(new InternetAddress(From));
        } else {
            msg.setFrom();
        }

        msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(Receipts, false));

        // Ok let's read some MIME params from config
        String strXMailer, strReturnPath, strMIMEVersion, strContentTransferEncoding, strContentType;

        if ((strXMailer = config.getParam(XMailer, "msgsend")).equalsIgnoreCase("")) {
            strXMailer = "msgsend";
        }
        if ((strReturnPath = config.getParam(ReturnPath, "root@localhost")).equalsIgnoreCase("")) {
            strXMailer = "root@localhost";
        }
        if ((strMIMEVersion = config.getParam(MIMEVersion, "1.0")).equalsIgnoreCase("")) {
            strMIMEVersion = "1.0";
        }
        if ((strContentTransferEncoding = config.getParam(ContentTransferEncoding, "7bit")).equalsIgnoreCase("")) {
            strContentTransferEncoding = "7.0";
        }
        if ((strContentType = config.getParam(ContentType, "text/plain; charset=us-ascii")).equalsIgnoreCase("")) {
            strContentType = "text/plain; charset=us-ascii";
        }

        logger.info("Header Content-Transfer-Encoding: " + strContentTransferEncoding);
        logger.info("Header Content-Type: " + strContentType);

        msg.setHeader("X-Mailer", strXMailer);
        msg.setHeader("Return-Path", strReturnPath);
        msg.setHeader("MIME-Version", strMIMEVersion);
        msg.setHeader("Content-Transfer-Encoding", strContentTransferEncoding);
        msg.setHeader("Content-Type", strContentType);
        msg.setHeader("Accept-Language", "ru-RU");
        msg.setHeader("Content-Language", "ru-RU");

        // add message body
        Multipart content = new MimeMultipart("mixed");
        Multipart multipart = new MimeMultipart("alternative");
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(Message, strContentType);
        multipart.addBodyPart(messageBodyPart);
        String htmlText = (String) params.get(HTMLText);
        if ((htmlText != null) && !htmlText.isEmpty()) {
            MimeBodyPart messageHTMLBodyPart = new MimeBodyPart();
            messageHTMLBodyPart.setContent(htmlText, "text/html; charset=UTF-8");
            multipart.addBodyPart(messageHTMLBodyPart);
        }

        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(multipart);
        content.addBodyPart(wrap);

        // add attachments
        Map<String, String> attachmentMap = this.checkAttachmentExists((Map<String, String>) params.get(SMTPAttachmentMap));
        this.addAttachments(content, attachmentMap);

        msg.setSubject(MimeUtility.encodeText(Subject, "utf-8", "B"));
        msg.setContent(content);

//        msg.setSubject(MimeUtility.encodeText("Тест по-русски", "utf-8", "B"));
//        msg.setContent("Тест по-русски", "text/plain; charset=utf-8");
        msg.setSentDate(new Date());
        msg.saveChanges();
        /**
         * try { synchronized (tr) { }
         *
         * } catch (final Exception e) { // connect only if no connection is
         * established if (tr == null) { tr = session.getTransport("smtp"); if
         * (!tr.isConnected()) { logger.info("Trying to connect " + Host + " " +
         * Port + " " + UserName + " " + ServerPass); tr.connect(Host,
         * Integer.parseInt(Port), UserName, ServerPass); } } } finally { //
         * install services only once if (!listenerInstalled) {
         * tr.addConnectionListener(this); tr.addTransportListener(this);
         * listenerInstalled = true; }
         *
         * try { if (!tr.isConnected()) { logger.info("Trying to connect " +
         * Host + " " + Port + " " + UserName + " " + ServerPass);
         * tr.connect(Host, Integer.parseInt(Port), UserName, ServerPass); } //
         * send the thing off logger.info("Sending message");
         * tr.sendMessage(msg, InternetAddress.parse(Receipts, false)); return
         * true; } catch (Exception e) { logger.error("mailBlock: error occured
         * during message sent " + e.getMessage(), e); try { tr.close(); } catch
         * (Exception ee) { } finally { tr = null; return false; }
         *
         * }
         * }
         */
        Transport transport = null;
        try {
            //Session session = getSession();
            //Message msg = getMessage(session, subject, receipts, this.from, message);
            transport = getTransport(session);
            logger.info("Connecting to SMTP server");
            transport.connect(Host, Integer.parseInt(Port), UserName, ServerPass);
            logger.info("Sending email");
            transport.sendMessage(msg, InternetAddress.parse(Receipts, false));
            logger.info("Email sent");

            return true;
        } catch (Exception e) {
            logger.error("Can't send message: " + e.getMessage(), e);
            return false;
        } finally {
            try {
                if (transport != null) {
                    transport.close();
                }
                logger.info("Connection closed");
            } catch (MessagingException e) {
                logger.warn("Something went wrong while closing transport (see debug logging level for details)");
                logger.debug(e.getMessage(), e);
            }
        }

    }

    private Transport getTransport(javax.mail.Session session)
            throws Exception {

        Transport transport = session.getTransport("smtp");
        if (transport == null) {
            throw new Exception("Can't find provider for SMTP mail messaging");
        }
        transport.addConnectionListener(this);
        transport.addTransportListener(this);
        return transport;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.event.TransportListener#messageDelivered(javax.mail.event.TransportEvent)
     */
    public void messageDelivered(TransportEvent e) {
        logger.info("MailBlock: TransportListener.messageDelivered().");
        logger.info("MailBlock: Valid Addresses:");
        javax.mail.Address[] valid = e.getValidSentAddresses();
        if (valid != null) {
            for (int i = 0; i < valid.length; i++) {
                logger.info("    " + valid[i]);
            }
        }
        try {
            synchronized (tr) {
                if (!tr.isConnected()) {
                    tr = null;
                }
            }
        } catch (Exception e1) {/* if we are that mean tr is already null */

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.event.TransportListener#messageNotDelivered(javax.mail.event.TransportEvent)
     */
    public void messageNotDelivered(TransportEvent e) {
        logger.info("MailBlock: TransportListener.messageNotDelivered().");
        logger.info("MailBlock: Invalid Addresses:");
        javax.mail.Address[] invalid = e.getInvalidAddresses();
        if (invalid != null) {
            for (int i = 0; i < invalid.length; i++) {
                logger.info("    " + invalid[i]);
            }
        }
        try {
            synchronized (tr) {
                if (!tr.isConnected()) {
                    tr = null;
                }
            }
        } catch (Exception e1) {/* if we are that mean tr is already null */

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.event.TransportListener#messagePartiallyDelivered(javax.mail.event.TransportEvent)
     */
    public void messagePartiallyDelivered(TransportEvent arg0) {
        // SMTPTransport doesn't partially deliver msgs
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.event.ConnectionListener#closed(javax.mail.event.ConnectionEvent)
     */
    public void closed(ConnectionEvent arg0) {
        tr = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.event.ConnectionListener#disconnected(javax.mail.event.ConnectionEvent)
     */
    public void disconnected(ConnectionEvent arg0) {
        tr = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.event.ConnectionListener#opened(javax.mail.event.ConnectionEvent)
     */
    public void opened(ConnectionEvent arg0) {
    }
}
