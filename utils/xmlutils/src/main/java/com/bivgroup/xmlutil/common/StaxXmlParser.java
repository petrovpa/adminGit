package com.bivgroup.xmlutil.common;

import com.bivgroup.dateutil.DateFormat;
import com.bivgroup.xmlutil.XmlUtil;
import com.bivgroup.xmlutil.exception.XmlUtilException;
import com.bivgroup.xmlutil.interfaces.ObjectCreator;
import com.ctc.wstx.stax.WstxInputFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class StaxXmlParser {
    private static Logger logger = Logger.getLogger(XmlUtil.class);
    private static final XMLInputFactory factory2 = new WstxInputFactory();

    public StaxXmlParser() {
    }

    public static XMLInputFactory getFactory2() {
        return factory2;
    }

    public static Map<String, Object> parse(InputStream xml, ObjectCreator creator, boolean isRecivedDateConvert) throws IOException, ClassNotFoundException, XMLStreamException, XmlUtilException {
        return parse(factory2.createXMLStreamReader(xml), creator, isRecivedDateConvert);
    }

    public static Map<String, Object> parse(Reader xml, ObjectCreator creator, boolean isRecivedDateConvert) throws IOException, ClassNotFoundException, XMLStreamException, XmlUtilException {
        return parse(factory2.createXMLStreamReader(xml), creator, isRecivedDateConvert);
    }

    public static Map<String, Object> parse(XMLStreamReader xpp, ObjectCreator creator, boolean isRecivedDateConvert) throws IOException, ClassNotFoundException, XMLStreamException, XmlUtilException {
        Map<String, Object> result = creator.createMap();
        boolean wasInChar = false;
        Stack<Object> objStack = new Stack();
        Stack<Integer> levelStack = new Stack();
        Integer level = 1;
        Class lastType = null;
        String lastName = null;
        objStack.push(result);
        levelStack.push(level);

        try {
            while(xpp.hasNext()) {
                int eventType = xpp.next();
                switch(eventType) {
                    case 1:
                        String elem = xpp.getLocalName();
                        Object obj = null;
                        boolean isEncoded = false;
                        boolean hasPrefix = false;
                        wasInChar = false;
                        int i = xpp.getAttributeCount() - 1;

                        for(; i >= 0; --i) {
                            String attrName = xpp.getAttributeLocalName(i);
                            if ("type".equalsIgnoreCase(attrName)) {
                                lastType = Class.forName(xpp.getAttributeValue(i));
                                if (lastType != null) {
                                    if (Map.class.isAssignableFrom(lastType)) {
                                        obj = creator.createMap();
                                    } else if (List.class.isAssignableFrom(lastType)) {
                                        obj = creator.createList();
                                    }
                                } else {
                                    logger.error("elem = " + elem + " has null attr type");
                                }
                            } else if ("encoded".equalsIgnoreCase(attrName)) {
                                isEncoded = true;
                            } else if ("prefix".equalsIgnoreCase(attrName)) {
                                hasPrefix = true;
                            }
                        }

                        lastName = isEncoded ? (hasPrefix ? XmlUtil.decodePseudoBase64(elem.substring(1)) : XmlUtil.decodePseudoBase64(elem)) : elem;
                        level = level + 1;
                        if (obj != null) {
                            levelStack.push(level);
                            if (objStack.peek() instanceof Map) {
                                ((Map)objStack.peek()).put(lastName, obj);
                            } else if (objStack.peek() instanceof List) {
                                ((List)objStack.peek()).add(obj);
                            }

                            objStack.push(obj);
                        }
                        break;
                    case 2:
                        if (((Integer)levelStack.peek()).equals(level)) {
                            objStack.pop();
                            levelStack.pop();
                        } else if (!wasInChar) {
                            if (objStack.peek() instanceof List && xpp.getLocalName().equalsIgnoreCase("Item")) {
                                if (lastType == null) {
                                    ((List)objStack.peek()).add((Object)null);
                                } else {
                                    ((List)objStack.peek()).add("");
                                }
                            } else if (objStack.peek() instanceof Map && level > 2) {
                                if (lastType == null) {
                                    ((Map)objStack.peek()).put(lastName, (Object)null);
                                } else {
                                    ((Map)objStack.peek()).put(lastName, "");
                                }
                            }
                        }

                        lastType = null;
                        level = level - 1;
                        break;
                    case 3:
                    case 5:
                    case 6:
                    case 9:
                    case 10:
                    case 11:
                    default:
                        logger.error("STAX Parsing: Not expected event type " + eventType + "text = " + xpp.getText());
                        if (eventType == -1) {
                            throw new XmlUtilException("STAX Parsing: Not expected event type " + eventType);
                        }
                        break;
                    case 4:
                    case 12:
                        if (lastType == null || xpp.isWhiteSpace() && !lastType.equals(String.class)) {
                            break;
                        }

                        String characters = xpp.getText();
                        Object lastObj = objStack.peek();
                        Object value = null;
                        if (!List.class.isAssignableFrom(lastType) || !Map.class.isAssignableFrom(lastType)) {
                            try {
                                if (lastType.isAssignableFrom(Date.class)) {
                                    try {
                                        value = DateFormat.getFormat().parseObject(characters);
                                    } catch (Exception var23) {
                                        value = DateFormat.getOldFormat().parseObject(characters);
                                    }

                                    if (isRecivedDateConvert) {
                                        value = XmlUtil.convertDate((Date)value);
                                    }
                                } else if (lastType.equals(byte[].class)) {
                                    value = Base64.decodeBase64(characters);
                                } else {
                                    Constructor<String> c = lastType.getConstructor(String.class);
                                    if (c != null) {
                                        value = c.newInstance(characters);
                                    } else {
                                        value = xpp.getElementText().getBytes();
                                    }
                                }
                            } catch (Exception var24) {
                                value = characters.getBytes("UTF-8");
                            }
                        }

                        if (lastObj instanceof List) {
                            if (wasInChar) {
                                Object object = ((List)lastObj).get(((List)lastObj).size() - 1).toString() + (String)value;
                                ((List)lastObj).remove(((List)lastObj).size() - 1);
                                ((List)lastObj).add(object);
                            } else {
                                ((List)lastObj).add(value);
                            }
                        } else if (lastObj instanceof Map) {
                            ((Map)lastObj).put(lastName, value);
                        }

                        wasInChar = true;
                        break;
                    case 7:
                        if (xpp.hasNext()) {
                            xpp.next();
                        }
                    case 8:
                }
            }
        } finally {
            if (xpp != null) {
                xpp.close();
            }

        }

        return result;
    }

    static {
        factory2.setProperty("javax.xml.stream.isCoalescing", true);
    }
}
