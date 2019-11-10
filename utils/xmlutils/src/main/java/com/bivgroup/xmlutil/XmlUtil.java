package com.bivgroup.xmlutil;

import com.bivgroup.dateutil.DateFormat;
import com.bivgroup.dateutil.DateUtil;
import com.bivgroup.stringutils.StringUtils;
import com.bivgroup.xmlutil.common.DefaultedHashMap;
import com.bivgroup.xmlutil.common.ProxyInfo;
import com.bivgroup.xmlutil.common.StaxXmlBuilder;
import com.bivgroup.xmlutil.common.StaxXmlParser;
import com.bivgroup.xmlutil.exception.*;
import com.bivgroup.xmlutil.interfaces.ObjectCreator;
import com.bivgroup.xmlutil.interfaces.ServiceUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class XmlUtil implements ServiceUtil {
    private static final Logger logger = Logger.getLogger(XmlUtil.class);
    private final boolean isRecivedDateConvert;
    private final boolean isFloatDateConvert;
    private String login;
    private String password;
    private String user;
    private String locale;
    private final ObjectCreator creator;
    private static String locatorServiceURL = null;
    private static java.net.URL parsedLocatorServiceURL = null;
    private static Map<String, Object> serviceLocatorData = null;
    private static final ReentrantReadWriteLock RWLOCK = new ReentrantReadWriteLock();
    private static final HttpClient httpClient;
    private static String fromSystem;
    private static boolean useDispatcherServlet;
    private static final String PORT_LIST = "ports";
    private static final String PROTOCOL = "protocol";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String URL = "url";

    public Map<String, Object> getServiceLocatorMap() {
        RWLOCK.readLock().lock();

        Map map;
        try {
            if (serviceLocatorData == null) {
                RWLOCK.readLock().unlock();
                RWLOCK.writeLock().lock();

                try {
                    if (serviceLocatorData == null) {
                        map = this.loadServiceLocatorMap();
                        if (map != null) {
                            serviceLocatorData = map;
                        }
                    }
                } finally {
                    RWLOCK.readLock().lock();
                    RWLOCK.writeLock().unlock();
                }
            }

            map = serviceLocatorData;
        } finally {
            RWLOCK.readLock().unlock();
        }

        return map;
    }

    private Map<String, Object> loadServiceLocatorMap() {
        if (locatorServiceURL != null && locatorServiceURL.trim().length() != 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Loading service locator data from " + locatorServiceURL);
            }

            Map response;
            try {
                response = this.doURL(locatorServiceURL, "getlocatordata", this.createXml((String) null), (Proxy) null);
            } catch (Exception var3) {
                logger.error("Faild to load locator data from " + locatorServiceURL, var3);
                return null;
            }

            if (!"OK".equals(response.get("Status"))) {
                logger.error("Error while retrieving service locator data. Code: " + response.get("FAULTCODE") + " message: " + response.get("FAULTMESSAGE"));
                return null;
            } else {
                Object data = response.get("ResultExtLoc");
                if (data == null) {
                    data = response.get("Result");
                }

                if (data != null && data instanceof Map) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Service locator data successfully loaded: " + data);
                    }

                    return (Map) data;
                } else {
                    logger.error("Service corews method getlocatordata returned invalid locator data: " + data);
                    return null;
                }
            }
        } else {
            logger.error("locatorServiceURL not set");
            return null;
        }
    }

    @Override
    public String getServiceURL(String serviceName) {
        return this.getServiceURL(serviceName, true);
    }

    @Override
    public String getServiceURL(String serviceName, boolean useProxy) {
        if (useDispatcherServlet) {
            if (parsedLocatorServiceURL == null) {
                logger.error("Locator service URL not specified or invalid [" + locatorServiceURL + "]");
            } else {
                serviceName = parsedLocatorServiceURL.getProtocol() + "://" + parsedLocatorServiceURL.getHost() + ":" + parsedLocatorServiceURL.getPort() + "/richclientproxy/richclientproxy?" + serviceName;
            }

            return serviceName;
        } else if (serviceName != null && !"".equals(serviceName.trim()) && serviceName.indexOf(91) >= 0) {
            Map<String, Object> serviceLocatorMap = this.getServiceLocatorMap();
            if (serviceLocatorMap != null && serviceLocatorMap.size() != 0) {
                int start = serviceName.indexOf(91);
                while (start >= 0) {
                    int end = serviceName.indexOf(93, start);
                    if (end > start) {
                        String region = serviceName.substring(start + 1, end);
                        int newStart = region.lastIndexOf(91);
                        if (newStart >= 0) {
                            start = start + 1 + newStart;
                            region = serviceName.substring(start + 1, end);
                        }

                        Object aliasObject = serviceLocatorMap.get(region);
                        HashMap<String, Object> httpMap = null;
                        if (aliasObject != null && aliasObject instanceof Map) {
                            HashMap<String, Object> aliasMap = (HashMap) aliasObject;
                            if (!aliasMap.containsKey("ports")) {
                                httpMap = aliasMap;
                            } else {
                                List<HashMap<String, Object>> portList = (List) aliasMap.get("ports");
                                Iterator i$ = portList.iterator();

                                while (i$.hasNext()) {
                                    HashMap<String, Object> map = (HashMap) i$.next();
                                    String protocol = (String) map.get("protocol");
                                    if ("http".equalsIgnoreCase(protocol)) {
                                        httpMap = map;
                                    }
                                }

                                if (httpMap == null) {
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("getServiceURL: serviceLocatorMap ports not found, serviceName '" + serviceName + "' is not translated");
                                    }

                                    return serviceName;
                                }
                            }

                            String replacement = (String) httpMap.get("url");
                            if (replacement == null) {
                                replacement = "";
                            }

                            serviceName = serviceName.substring(0, start) + replacement + serviceName.substring(end + 1);
                            start = serviceName.indexOf(91, start + replacement.length());
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("getServiceURL: serviceLocatorMap is empty, serviceName '" + serviceName + "' is not translated");
                            }

                            serviceName = serviceName.substring(0, start) + serviceName.substring(end + 1);
                            start = end + 1;
                        }
                    } else {
                        start = -1;
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("getServiceURL: translated '" + serviceName + "' to '" + serviceName + "'");
                }

                return serviceName;
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("getServiceURL: serviceLocatorMap is empty, serviceName '" + serviceName + "' is not translated");
                }

                return serviceName;
            }
        } else {
            logger.debug("getServiceURL: nothing to substitute in serviceName: " + serviceName);
            return serviceName;
        }
    }

    public XmlUtil() {
        this(false, false);
    }

    public XmlUtil(String locale) {
        this((String) null, (String) null, locale);
    }

    public XmlUtil(boolean isRecivedDateConvert, boolean isFloatDateConvert) {
        this((String) null, (String) null, isRecivedDateConvert, isFloatDateConvert);
    }

    public XmlUtil(boolean isRecivedDateConvert, boolean isFloatDateConvert, String locale) {
        this((String) null, (String) null, isRecivedDateConvert, isFloatDateConvert, locale);
    }

    public XmlUtil(String login, String password) {
        this(login, password, false, false);
    }

    public XmlUtil(String login, String password, String locale) {
        this(login, password, false, false, locale);
    }

    public XmlUtil(String login, String password, boolean isRecivedDateConvert, boolean isFloatDateConvert) {
        this(login, password, isRecivedDateConvert, isFloatDateConvert, (String) null);
    }

    public XmlUtil(String login, String password, boolean isRecivedDateConvert, boolean isFloatDateConvert, String locale) {
        this.creator = new ObjectCreator() {
            public Map<String, Object> createMap() {
                return XmlUtil.this.createMap();
            }

            public List<Object> createList() {
                return XmlUtil.this.createList();
            }
        };
        this.login = login;
        this.password = password;
        this.locale = locale;
        this.isRecivedDateConvert = isRecivedDateConvert;
        this.isFloatDateConvert = isFloatDateConvert;
    }

    @Override
    public String createXml(String parameters) throws Exception {
        return this.createXml(this.getHashMap(parameters));
    }

    @Override
    public String createPost(String method, String commandtext, String commanddata, String fromsystem, String tosystem, Long processid) throws Exception {
        return this.createPost(method, commandtext, commanddata, fromsystem, tosystem, processid, this.login, this.password);
    }

    private String createPost(String method, String commandtext, String commanddata, String fromsystem, String tosystem, Long processid, String login, String password) throws Exception {
        Document document = DocumentHelper.createDocument();
        Namespace soap = Namespace.get("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
        Namespace ns1 = Namespace.get("ns1", "http://support.diasoft.ru");
        Namespace wsse = Namespace.get("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        Namespace wsu = Namespace.get("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
        Element root = document.addElement("SOAP-ENV:Envelope");
        root.add(soap);
        root.add(ns1);
        root.add(wsse);
        root.add(wsu);
        Element header = root.addElement("SOAP-ENV:Header");
        Element security;
        Element call;
        Element commandtextEl;
        Element commanddataEl;
        if (login != null) {
            security = header.addElement("wsse:Security");
            call = security.addElement("wsse:UsernameToken");
            call.addAttribute("wsu:Id", "Flextera");
            commandtextEl = call.addElement("wsse:Username");
            commandtextEl.addText(login);
            commanddataEl = call.addElement("wsse:Password");
            commanddataEl.addAttribute("Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
            commanddataEl.addText(password);
        }

        security = root.addElement("SOAP-ENV:Body");
        call = security.addElement("ns1:" + method);
        commandtextEl = call.addElement("ns1:commandtext");
        commandtextEl.setText(commandtext);
        commanddataEl = call.addElement("ns1:commanddata");
        commanddataEl.setText(commanddata);
        return document.asXML();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Map<String, Object> doURL(String urlString, String command, String parameters, Proxy proxy) throws XmlUtilException {
        if (logger.isDebugEnabled()) {
            logger.warn(String.format("Method doURL is Deprecated. Use: callService\n URL: %s, command: %s, params:\n%s", urlString, command, parameters));
        }

        return this.doURL(urlString, command, parameters, "UTF-8", proxy);
    }

    public DefaultedHashMap<String, Object> sendXml(String urlString, String xml, ProxyInfo proxy, Integer timeOut, Boolean needResponse) throws XmlUtilException {
        urlString = this.getServiceURL(urlString, true);

        PostMethod method = getPostMethod(urlString);

        Long uid = (new Date()).getTime();
        if (logger.isInfoEnabled()) {
            logger.info("connect uid = " + uid + " to " + urlString + " , xml = " + xml);
        }

        DefaultedHashMap var12;
        try {
            method.setDoAuthentication(false);
            if (proxy != null) {
                httpClient.getHostConfiguration().setProxy(proxy.getProxyUrl(), proxy.getPort());
                List<String> authPrefs = new ArrayList();
                authPrefs.add("NTLM");
                httpClient.getState().setProxyCredentials(new AuthScope((String) null, -1, (String) null), new NTCredentials(proxy.getLogin(), proxy.getPassword(), "", proxy.getDomain()));
            }

            HttpClientParams clientParams = new HttpClientParams();
            clientParams.setSoTimeout(timeOut * 1000);
            httpClient.setParams(clientParams);
            StringRequestEntity entity = new StringRequestEntity(xml, "text/xml", "UTF-8");
            method.setRequestEntity(entity);
            int result = httpClient.executeMethod(method);
            if (result != 200) {
                throw new Exception("[doURL] URL: " + urlString + " Response status code:" + result);
            }

            if (!needResponse) {
                DefaultedHashMap var21 = new DefaultedHashMap();
                return var21;
            }

            SAXReader reader = new SAXReader();
            reader.setStripWhitespaceText(false);
            var12 = this.parse(method.getResponseBodyAsStream());
        } catch (Exception var18) {
            logger.error(var18.getLocalizedMessage() + "\nuid = " + uid, var18);
            throw new XmlUtilException(var18);
        } finally {
            method.releaseConnection();
        }

        return var12;
    }

    @Override
    public Map<String, Object> doURL(String urlString, String command, String parameters, String codePage, Proxy proxy) throws XmlUtilException {
        return this.doURLHTTP(urlString, command, parameters, codePage, proxy, null, null, null, false);
    }

    private void debugCallService(String protocol, String service, String methodName, String parameters) {
        if (logger.isDebugEnabled()) {
            String message = String.format("CALL -> protocol: HTTP, service: %s, method: %s \nparams=%s", service, methodName, parameters);
            logger.debug(message);
        }
    }

    public Map<String, Object> doURLHTTP(String urlString, String command, String parameters, String codePage, Proxy proxy, String fromSystem, String toSystem, Long processid, boolean isThirdPartyCheckPerformed) throws XmlUtilException {
        urlString = this.getServiceURL(urlString, true);
        this.debugCallService("HTTP", urlString, command, parameters);
        if (!isThirdPartyCheckPerformed && !this.checkThirdPartyCall(urlString, urlString)) {
            throw new XmlUtilException("External services must be called through ESB.");
        } else {
            Date requestDate = new Date();
            Long uid = requestDate.getTime();

            PostMethod method;
            method = getPostMethod(urlString);

            if (logger.isDebugEnabled()) {
                try {
                    logger.debug("connect uid = " + uid + " to " + urlString + " , command = " + command + " , parameters = " + (logger.isEnabledFor(Level.ALL) ? parameters : StringUtils.truncate(String.valueOf(this.parse(parameters)), 256)));
                } catch (Exception var31) {
                    logger.error(var31.getLocalizedMessage(), var31);
                }
            }

            long start = 0L;
            boolean var30 = false;

            DefaultedHashMap var35;
            try {
                var30 = true;
                method.setDoAuthentication(false);
                String post = this.createPost("DSCALL", command, parameters, fromSystem, toSystem, processid);
                StringRequestEntity entity = new StringRequestEntity(post, "text/xml", "UTF-8");
                method.setRequestHeader("SOAPAction", "");
                method.setRequestEntity(entity);
                method.getParams().setSoTimeout(300000);
                int result = httpClient.executeMethod(method);
                if (result != 200) {
                    throw new ServiceUnAvailable(result, "[doURL] URL: " + urlString + " Response status code:" + result);
                }

                start = System.currentTimeMillis();
                DefaultedHashMap response;
                if (logger.isEnabledFor(Level.ALL)) {
                    byte[] responseData = method.getResponseBody();
                    logger.log(Level.ALL, "Response: " + new String(responseData, "iso-8859-1"));
                    response = this.parseSoapRequest(new ByteArrayInputStream(responseData));
                } else {
                    response = this.parseSoapRequest(method.getResponseBodyAsStream());
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Response: " + String.valueOf(response));
                }

                this.checkSoftError(response, urlString, command);
                var35 = response;
                var30 = false;
            } catch (Exception var33) {
                logger.error("connected to " + urlString + " , command = " + command + " , parameters = " + parameters);
                logger.error(var33.getLocalizedMessage() + "\nuid = " + uid);
                if (var33 instanceof SoftServiceErrorException) {
                    throw (SoftServiceErrorException) var33;
                }

                if (var33 instanceof SoftServiceErrorsListException) {
                    throw (SoftServiceErrorsListException) var33;
                }

                throw new XmlUtilException(var33);
            } finally {
                if (var30) {
                    if (logger.isDebugEnabled() && start != 0L) {
                        long end = System.currentTimeMillis();
                        logger.debug("Response parsed in " + (end - start) + "ms.");
                    }

                    method.releaseConnection();
                }
            }

            if (logger.isDebugEnabled() && start != 0L) {
                long end = System.currentTimeMillis();
                logger.debug("Response parsed in " + (end - start) + "ms.");
            }

            method.releaseConnection();
            return var35;
        }
    }

    private boolean checkThirdPartyCall(String serviceName, String serviceUrl) {
        return true;
    }

    private PostMethod getPostMethod(String urlString) throws XmlUtilException {
        PostMethod method;
        try {
            method = new PostMethod();
            method.setURI(new URI(urlString, false));
        } catch (Exception var32) {
            logger.error(var32.getLocalizedMessage(), var32);
            throw new XmlUtilException(var32);
        }
        return method;
    }

    private void checkSoftError(Map<String, Object> response, String urlString, String command) throws SoftServiceErrorException {
        if (this.isSoftError(response)) {
            String faultCode = response.get("FAULTCODE") != null ? response.get("FAULTCODE").toString() : "500";
            String faultMessage = response.get("FAULTMESSAGE") != null ? response.get("FAULTMESSAGE").toString() : "";
            String errorDescription = response.get("ErrorDescription") != null ? response.get("ErrorDescription").toString() : "";
            throw new SoftServiceErrorException(urlString + "?" + command, faultCode, faultMessage, errorDescription);
        }
    }

    private boolean isSoftError(Map response) {
        return response.get("Status") != null && "ERROR".equals(response.get("Status"));
    }

    @Override
    public DefaultedHashMap<String, Object> parseSoapRequest(InputStream stream) throws ParserConfigurationException, SAXException, IOException, XmlParseException {
        XMLStreamReader xpp = null;

        DefaultedHashMap var16;
        try {
            xpp = StaxXmlParser.getFactory2().createXMLStreamReader(stream);
            StringBuilder result = new StringBuilder();
            boolean isResult = false;

            while (xpp.hasNext()) {
                int eventType = xpp.next();
                switch (eventType) {
                    case 1:
                        if (xpp.getLocalName().equals("commandresult")) {
                            isResult = true;
                        }
                        break;
                    case 2:
                        if (isResult) {
                            isResult = false;
                        }
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    default:
                        break;
                    case 4:
                    case 12:
                        if (!xpp.isWhiteSpace() && isResult) {
                            result.append(xpp.getText());
                        }
                        break;
                    case 7:
                        if (xpp.hasNext()) {
                            xpp.next();
                        }
                }
            }

            var16 = this.parse(result.toString());
        } catch (Exception var14) {
            throw new ParserConfigurationException(var14.getMessage());
        } finally {
            if (xpp != null) {
                try {
                    xpp.close();
                } catch (XMLStreamException var13) {
                    var13.printStackTrace();
                }
            }

        }

        return var16;
    }

    @Override
    public String createXml(Map<String, Object> map) throws UnsupportedEncodingException {
        try {
            return StaxXmlBuilder.buildXML(map, this.isFloatDateConvert);
        } catch (Exception var3) {
            logger.error(var3.getLocalizedMessage(), var3);
            throw new UnsupportedEncodingException(var3.getLocalizedMessage());
        }
    }

    public static String encodePseudoBase64(String source) {
        try {
            byte[] bytes = source.getBytes("UTF-8");
            byte[] tBytes = Base64.encodeBase64(bytes);
            int trail = bytes.length % 3;
            int charLen = trail == 0 ? tBytes.length : (trail == 1 ? tBytes.length - 2 : tBytes.length - 1);
            char[] chars = new char[charLen];

            for (int i = 0; i < chars.length; ++i) {
                chars[i] = (char) tBytes[i];
                if (chars[i] == '+') {
                    chars[i] = '-';
                }

                if (chars[i] == '/') {
                    chars[i] = '_';
                }
            }

            return new String(chars);
        } catch (UnsupportedEncodingException var7) {
            logger.fatal("Encoding not found!", var7);
            return source;
        }
    }

    public static String decodePseudoBase64(String source) {
        if ("0".equalsIgnoreCase(source)) {
            return "";
        } else {
            char[] chars = source.toCharArray();
            int len = chars.length;
            int trail = len % 4;
            int byteLen = trail == 0 ? len : (trail == 2 ? len + 2 : len + 1);
            byte[] bytes = new byte[byteLen];

            for (int i = 0; i < byteLen; ++i) {
                if (i < len) {
                    if (chars[i] == '-') {
                        chars[i] = '+';
                    }

                    if (chars[i] == '_') {
                        chars[i] = '/';
                    }

                    bytes[i] = (byte) chars[i];
                } else {
                    bytes[i] = 61;
                }
            }

            byte[] utf8Bytes = Base64.decodeBase64(bytes);

            try {
                return new String(utf8Bytes, "UTF-8");
            } catch (UnsupportedEncodingException var8) {
                logger.fatal("Encoding not found!", var8);
                return source;
            }
        }
    }

    public static Date convertDate(Double time) {
        return DateUtil.convertDate(time);
    }

    public static Date convertDate(BigDecimal time) {
        return DateUtil.convertDate(time);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Date convertDate(Float time) {
        return DateUtil.convertDate(time);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Double convertDate(Date time) {
        return DateUtil.convertDate(time).doubleValue();
    }

    public static BigDecimal convertDateToBigDecimal(Date time) {
        return DateUtil.convertDate(time);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Double convertDatetoDouble(Date time) {
        return DateUtil.convertDate(time).doubleValue();
    }

    @Override
    public Map<String, String> getAuditParamsMap(String params) throws XmlParseException {
        Map<String, String> result = new HashMap();
        Map<String, Object> paramsMap = this.parse(params);
        if (paramsMap.isEmpty() && params != null && params.length() > 0) {
            result.put("STRING", params);
        }

        Iterator i$ = paramsMap.entrySet().iterator();

        while (i$.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry) i$.next();
            String value = entry.getValue() == null ? null : entry.getValue().toString();
            result.put(entry.getKey(), value);
        }

        return result;
    }

    @Override
    public DefaultedHashMap<String, Object> parse(String xml) throws XmlParseException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("parsing xml = " + xml);
            }

            return (DefaultedHashMap) StaxXmlParser.parse(new StringReader(xml), this.creator, this.isRecivedDateConvert);
        } catch (Exception var3) {
            logger.error("xml is not valid = " + xml);
            throw new XmlParseException(var3);
        }
    }

    @Override
    public DefaultedHashMap<String, Object> parse(InputStream stream) throws XmlParseException {
        new DefaultedHashMap();

        try {
            SAXReader reader = new SAXReader();
            reader.setStripWhitespaceText(false);
            Document document = reader.read(stream);
            DefaultedHashMap<String, Object> map = this.parse(document);
            return map;
        } catch (Exception var5) {
            logger.error(var5.getMessage(), var5);
            throw new XmlParseException(var5);
        }
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public DefaultedHashMap<String, Object> parse(Document doc) throws XmlParseException {
        DefaultedHashMap map = new DefaultedHashMap();

        try {
            Element Root = doc.getRootElement();
            this.user = Root.attributeValue("user", (String) null);
            String version = Root.attributeValue("version", "1.0");
            if (!version.equals("2.11")) {
                logger.warn("Different version of XmlUtil sender = " + version + " reciver = " + "2.11");
            }

            int i = 0;

            for (int size = Root.nodeCount(); i < size; ++i) {
                Node node = Root.node(i);
                if (node instanceof Element) {
                    Element elt = (Element) node;
                    String nodeName = node.getName();
                    if ("true".equalsIgnoreCase(elt.attributeValue("prefix"))) {
                        nodeName = nodeName.substring(1);
                    }

                    if ("true".equalsIgnoreCase(elt.attributeValue("encoded"))) {
                        nodeName = decodePseudoBase64(nodeName);
                    }

                    map.put(nodeName, this.parseElement((Element) node));
                }
            }

            return map;
        } catch (Exception var10) {
            logger.error(var10.getMessage(), var10);
            throw new XmlParseException(var10);
        }
    }

    private Object parseElement(Element elem) throws ClassNotFoundException, SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, XmlParseException {
        Attribute type = elem.attribute("type");
        if (type != null) {
            Iterator i$;
            Element item;
            if (!type.getValue().equalsIgnoreCase(List.class.getName())) {
                if (type.getValue().equalsIgnoreCase(Map.class.getName())) {
                    HashMap<String, Object> map = new HashMap<>();

                    String nodeName;
                    parseElementIterate(elem, map);

                    return map;
                } else {
                    Object obj;
                    if (Class.forName(type.getValue()).isAssignableFrom(Date.class)) {
                        try {
                            obj = DateFormat.getFormat().parseObject(elem.getText());
                        } catch (Exception var10) {
                            try {
                                Double d = Double.parseDouble(elem.getText());
                                obj = convertDate(d);
                            } catch (NumberFormatException var9) {
                                throw new XmlParseException("Wrong Date format", var9);
                            }
                        }

                        if (this.isRecivedDateConvert) {
                            obj = convertDate((Date) obj);
                        }
                    } else {
                        Class clazz = Class.forName(type.getValue());

                        try {
                            Constructor<String> c = clazz.getConstructor(String.class);
                            if (c != null) {
                                obj = c.newInstance(elem.getText());
                            } else {
                                obj = elem.getText().getBytes();
                            }
                        } catch (Exception var8) {
                            obj = elem.getText().getBytes();
                        }
                    }

                    return obj;
                }
            } else {
                List list = new ArrayList();
                if (elem.elements().size() > 0) {
                    i$ = ((Element) elem.elements().get(0)).elements().iterator();

                    while (i$.hasNext()) {
                        item = (Element) i$.next();
                        list.add(this.parseElement(item));
                    }
                }

                return list;
            }
        } else if (elem.elements().isEmpty()) {
            return null;
        } else {
            Iterator i$;
            Element item;
            if (!"ListItems".equals(((Element) elem.elements().get(0)).getName())) {
                HashMap map = new HashMap();

                String nodeName;
                parseElementIterate(elem, map);

                return map;
            } else {
                List list = new ArrayList();
                i$ = ((Element) elem.elements().get(0)).elements().iterator();

                while (i$.hasNext()) {
                    item = (Element) i$.next();
                    list.add(this.parseElement(item));
                }

                return list;
            }
        }
    }

    private void parseElementIterate(Element elem, HashMap<String, Object> map) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, XmlParseException {
        Iterator i$;
        Element item;
        String nodeName;
        for (i$ = elem.elements().iterator(); i$.hasNext(); map.put(nodeName, this.parseElement(item))) {
            item = (Element) i$.next();
            nodeName = item.getName();
            if ("true".equalsIgnoreCase(item.attributeValue("prefix"))) {
                nodeName = nodeName.substring(1);
            }

            if ("true".equalsIgnoreCase(item.attributeValue("encoded"))) {
                nodeName = decodePseudoBase64(nodeName);
            }
        }
    }

    @Override
    public HashMap<String, Object> getHashMap(String parameters) {
        HashMap<String, Object> hashMap = new HashMap();
        if (parameters != null) {
            String[] list = parameters.split("\n");
            String[] arr$ = list;
            int len$ = list.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String aList = arr$[i$];
                String[] result = aList.split(" {0,}= {0,}");
                if (result.length > 1) {
                    hashMap.put(result[0], result[1]);
                }
            }
        }

        return hashMap;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public void replaceQuote(Map<String, Object> map) {
        for (Object o : map.entrySet()) {
            Map.Entry<String, Object> item = (Map.Entry<String, Object>) o;
            if (item.getValue() instanceof String && ((String) item.getValue()).contains("'")) {
                String str = (String) item.getValue();
                item.setValue(str.replace("'", "''"));
            } else if (item instanceof Map) {
                this.replaceQuote((Map<String, Object>) item);
            } else if (item instanceof List) {
                this.replaceListQuote((List<Map<String, Object>>) item);
            }
        }
    }

    public static void setLocatorServiceURL(String url) {
        RWLOCK.writeLock().lock();

        try {
            locatorServiceURL = url;
            parsedLocatorServiceURL = new URL(locatorServiceURL);
        } catch (MalformedURLException var5) {
            logger.error("Unparseable locator service url", var5);
        } finally {
            RWLOCK.writeLock().unlock();
        }

    }

    private void replaceListQuote(List<Map<String, Object>> list) {
        for (Object aList : list) {
            Map<String, Object> item = (Map) aList;
            this.replaceQuote(item);
        }
    }

    protected List<Object> createList() {
        return new ArrayList<>();
    }

    protected Map<String, Object> createMap() {
        return new DefaultedHashMap<>();
    }

    public static String getUserName(String fromLogin) {
        if (fromLogin != null) {
            int i = fromLogin.indexOf(92);
            if (i >= 0) {
                return fromLogin.substring(i + 1);
            }
        }

        return fromLogin;
    }

    static {
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.setMaxConnectionsPerHost(65535);
        connectionManager.setMaxTotalConnections(2147483647);
        httpClient = new HttpClient(connectionManager);
        HttpClientParams clientParams = new HttpClientParams();
        clientParams.setSoTimeout(300000);
        clientParams.setConnectionManagerTimeout(300000L);
        httpClient.setParams(clientParams);
        clientParams = new HttpClientParams();
        clientParams.setSoTimeout(100);
        clientParams.setConnectionManagerTimeout(100L);
    }
}
