/*
 * Copyright (c) Diasoft 2004-2013
 */
package com.bivgroup.services.sberpayservice;

import com.sun.xml.messaging.saaj.soap.ver1_2.SOAPFactory1_2Impl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.apache.log4j.Logger;

/**
 *
 * @author reson
 */
public class UserNameTokenHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String PASSWORD_TEXT_TYPE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText";
    public static final String WSSE_SECURITY_LNAME = "Security";
    public static final String WSSE_NS_PREFIX = "wsse";
    private final Logger logger = Logger.getLogger(UserNameTokenHandler.class);
    private String login = null;
    private String password = null;
    private String debugDate = null;
    private String debugNonce = null;

    public UserNameTokenHandler(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public Set<QName> getHeaders() {
        return new TreeSet<QName>();
    }

    public void setDebugDate(String debugDate) {
        this.debugDate = debugDate;
    }

    public void setDebugNonce(String nonce) {
        this.debugNonce = nonce;
    }

    private byte[] stringToBytes(String value) {
        byte[] result = null;
        try {
            result = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.error("Error converting String to bytes in UTF-8 encoding", ex);
        }
        return result;
    }

    private void logMessage(SOAPMessageContext context) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            context.getMessage().writeTo(baos);
        } catch (SOAPException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
        String message = null;
        try {
            message = new String(baos.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.error("Error converting String to bytes in UTF-8 encoding", ex);
        }
        System.out.println(message);
        logger.info(message);
    }

    private String getNonce() {
        String result = debugNonce;
        if (null == result) {
            Random random = new SecureRandom();
            String unique = String.valueOf(random.nextLong());
            result = this.base64Encode(stringToBytes(unique));
        }
        return result;
    }

    public String getDate() {
        String result = this.debugDate;
        if (null == result) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            result = sdf.format(new Date());
        }
        return result;
    }

    private byte[] base64Decode(String value) {
        return javax.xml.bind.DatatypeConverter.parseBase64Binary(value);
    }

    private String base64Encode(byte[] value) {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(value);
    }

    public String getPasswordDigest(String nonce, String created, String pwd) {
        byte[] passwd = stringToBytes(pwd);

        String passwdDigest = null;
        try {
            byte[] b1 = nonce != null ? base64Decode(nonce) : new byte[0];
            byte[] b2 = created != null ? stringToBytes(created) : new byte[0];
            byte[] b3 = passwd;
            if(b3 == null){
                throw new Exception("The 'getPasswordDigest' method is called with the wrong password");
            }
            if(b2 == null){
                throw new Exception("The 'getPasswordDigest' method is called with the wrong created");
            }
            byte[] b4 = new byte[b1.length + b2.length + b3.length];
            int offset = 0;
            System.arraycopy(b1, 0, b4, offset, b1.length);
            offset += b1.length;

            System.arraycopy(b2, 0, b4, offset, b2.length);
            offset += b2.length;

            System.arraycopy(b3, 0, b4, offset, b3.length);

            byte[] digestBytes = null;
            MessageDigest digest;
            try {

                digest = MessageDigest.getInstance("SHA-1");

                digestBytes = digest.digest(b4);
            } catch (Exception ex) {
                logger.error("Error digest password with sha-1", ex);
            }

            //BASE64Encoder encoder = new BASE64Encoder();
            //System.out.println(encoder.encode(digestBytes));
            passwdDigest = base64Encode(digestBytes);
        } catch (Exception ex) {
            logger.error("Error get passwords digest", ex);
        }
        return passwdDigest;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {

        Boolean outboundProperty
                = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outboundProperty.booleanValue()) {

            try {
                SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                SOAPHeader header = envelope.getHeader();

                if (header == null) {
                    header = envelope.addHeader();
                }
                //SOAPHeader header = envelope.addHeader();
                //header.add
                SOAPFactory factory = new SOAPFactory1_2Impl();
                String prefix = "wsse";
                String uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
                Name secName = factory.createName("Security", prefix, uri);
                SOAPHeaderElement securityElem
                        = header.addHeaderElement(secName);
                //factory.createElement("Security", prefix, uri);
                securityElem.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
                //securityElem.addAttribute(QName.valueOf("xmlns:wsu"),
                //        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
                //securityElem.addAttribute(QName.valueOf("S:mustUnderstand"), "1");
                securityElem.setMustUnderstand(true);

                SOAPElement tokenElem
                        = securityElem.addChildElement("UsernameToken", prefix);
                //factory.createElement("UsernameToken");
                //tokenElem.setPrefix(prefix);
                tokenElem.addAttribute(QName.valueOf("wsu:Id"), "UsernameToken-87");

                SOAPElement userElem
                        = tokenElem.addChildElement("Username", prefix);
                //factory.createElement("Username");
                userElem.addTextNode(login);

                String nonce = this.getNonce();
                String date = this.getDate();
                String passwordDigest = this.getPasswordDigest(nonce, date, this.password);

                SOAPElement pwdElem
                        = tokenElem.addChildElement("Password", prefix);
                //factory.createElement("Password");
                //pwdElem.addAttribute(QName.valueOf("Type"),
                //        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest");
                pwdElem.addAttribute(QName.valueOf("Type"),
                        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");

                //pwdElem.addTextNode(passwordDigest);
                pwdElem.addTextNode(this.password);
               /* SOAPElement nonceElem
                        = tokenElem.addChildElement("Nonce", prefix);
                //factory.createElement("Nonce");
                nonceElem.addAttribute(QName.valueOf("EncodingType"),
                        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
                nonceElem.addTextNode(nonce);

                SOAPElement dateElem
                        = tokenElem.addChildElement("Created", "wsu");
                //factory.createElement("Created");
                dateElem.addTextNode(date);*/

                //tokenElem.addChildElement(userElem);
                //tokenElem.addChildElement(pwdElem);
                //tokenElem.addChildElement(nonceElem);
                //tokenElem.addChildElement(dateElem);
                //securityElem.addChildElement(tokenElem);
                //SOAPHeader header = envelope.addHeader();
                //header.addChildElement(securityElem);
                this.logMessage(context);
            } catch (Exception ex) {
                logger.error("Error create soap header", ex);
            }
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }
}
