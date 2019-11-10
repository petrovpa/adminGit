package com.bivgroup.xmlutil.interfaces;

import com.bivgroup.xmlutil.common.DefaultedHashMap;
import com.bivgroup.xmlutil.common.ProxyInfo;
import com.bivgroup.xmlutil.exception.XmlParseException;
import com.bivgroup.xmlutil.exception.XmlUtilException;
import org.dom4j.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

public interface ServiceUtil {
    String VERSION_NAME = "version";
    String USER_NAME = "user";
    String YEARS_SHIFT_70 = "25569.0";
    String BODY = "BODY";
    String ITEMS = "ListItems";
    String ITEM = "Item";
    String TYPE = "type";
    String VERSION = "2.11";
    String SR_STATUS = "Status";
    String SE_FAULTCODE = "FAULTCODE";
    String SE_FAULTMESSAGE = "FAULTMESSAGE";
    String SE_STATUS_ERROR = "ERROR";
    String SE_ERROR_DESC = "ErrorDescription";
    String SE_STATUS_OK = "OK";
    String RET_RESULT = "Result";
    String RETURN_AS_MAP = "ReturnAsHashMap";
    String GETLOCATOR_COMMAND = "getlocatordata";
    String SYSTEM_PARAMS = "SYSTEM_PARAMS";
    String RETURN_CODE = "ReturnCode";
    String RETURN_MSG = "ReturnMsg";
    String ERROR_INFO_CODE = "errorInfoCode";
    String NOTIFICATION_LIST = "NotificationList";

    String getServiceURL(String var1, boolean var2);

    String getServiceURL(String var1);

    String createXml(String var1) throws Exception;

    String createPost(String var1, String var2, String var3, String var4, String var5, Long var6) throws Exception;

    /**
     * @deprecated
     */
    @Deprecated
    Map<String, Object> doURL(String var1, String var2, String var3, Proxy var4) throws XmlUtilException;

    DefaultedHashMap<String, Object> sendXml(String var1, String var2, ProxyInfo var3, Integer var4, Boolean var5) throws XmlUtilException;

    /**
     * @deprecated
     */
    @Deprecated
    Map<String, Object> doURL(String var1, String var2, String var3, String var4, Proxy var5) throws XmlUtilException;

    DefaultedHashMap<String, Object> parseSoapRequest(InputStream var1) throws ParserConfigurationException, SAXException, IOException, XmlParseException;

    String createXml(Map<String, Object> var1) throws UnsupportedEncodingException;

    Map<String, String> getAuditParamsMap(String var1) throws XmlParseException;

    DefaultedHashMap<String, Object> parse(String var1) throws XmlParseException;

    DefaultedHashMap<String, Object> parse(InputStream var1) throws XmlParseException;

    DefaultedHashMap<String, Object> parse(Document var1) throws XmlParseException;

    HashMap<String, Object> getHashMap(String var1);

    String getUser();

    void replaceQuote(Map<String, Object> var1);
}
