package com.bivgroup.xmlutil.common;

import com.bivgroup.dateutil.DateFormat;
import com.bivgroup.xmlutil.XmlUtil;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StaxXmlBuilder {
    private static final Logger logger = Logger.getLogger(XmlUtil.class);
    private static final XMLOutputFactory factory2 = new WstxOutputFactory();

    public StaxXmlBuilder() {
    }

    public static String buildXML(Map<String, Object> map, boolean isFloatDateConvert) throws XMLStreamException, FactoryConfigurationError {
        StringWriter result = new StringWriter();
        XMLStreamWriter writer = factory2.createXMLStreamWriter(result);
        writer.writeStartDocument();
        writer.writeStartElement("BODY");
        writer.writeAttribute("version", "2.11");

        for (Object o : map.entrySet()) {
            Map.Entry<String, Object> entry = (Map.Entry) o;
            Object item = entry.getValue();
            if (item != null) {
                createRootElement(writer, (String) entry.getKey());
                writeItem(writer, item, ((String) entry.getKey()).toUpperCase(), isFloatDateConvert);
                writer.writeEndElement();
            }
        }

        writer.writeEndDocument();
        writer.flush();
        String s = result.toString();
        if (logger.isEnabledFor(Level.ALL)) {
            logger.log(Level.ALL, "created xml = " + s);
        }

        return s;
    }

    private static void createRootElement(XMLStreamWriter writer, String tagName) throws XMLStreamException {
        boolean encoded = false;
        if ("".equals(tagName)) {
            encoded = true;
            tagName = "0";
        } else if (needEncoding(tagName)) {
            tagName = XmlUtil.encodePseudoBase64(tagName);
            encoded = true;
        }

        char firstChar = tagName.length() > 0 ? tagName.charAt(0) : 0;
        boolean needPrefix = firstChar != 0 && '0' <= firstChar && firstChar <= '9';
        writer.writeStartElement(needPrefix ? "x" + tagName : tagName);
        if (encoded) {
            writer.writeAttribute("encoded", "true");
        }

        if (needPrefix) {
            writer.writeAttribute("prefix", "true");
        }

    }

    private static boolean needEncoding(String tagName) {
        if ("".equalsIgnoreCase(tagName)) {
            return true;
        } else {
            char[] chars = tagName.toCharArray();
            char[] arr$ = chars;
            int len$ = chars.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                char c = arr$[i$];
                if (('a' > c || c > 'z') && ('A' > c || c > 'Z') && c != '-' && c != '_') {
                    return true;
                }
            }

            return false;
        }
    }

    private static void writeItem(XMLStreamWriter writer, Object item, String realName, boolean isFloatDateConvert) throws XMLStreamException {
        if (item instanceof Map) {
            parseMap(writer, (Map) item, isFloatDateConvert);
        } else if (item instanceof List) {
            parseList(writer, (List) item, isFloatDateConvert);
        } else if (item != null) {
            if (item instanceof byte[]) {
                isFloatDateConvert = true;
                String itemStr = new String((byte[]) ((byte[]) item));

                try {
                    item = Float.parseFloat(itemStr);
                } catch (NumberFormatException var10) {
                    try {
                        item = Double.parseDouble(itemStr);
                    } catch (NumberFormatException var9) {
                        try {
                            item = new BigDecimal(itemStr);
                        } catch (NumberFormatException var8) {
                            isFloatDateConvert = false;
                        }
                    }
                }
            }

            if (item instanceof Date) {
                writer.writeAttribute("type", Date.class.getName());
                writer.writeCharacters(DateFormat.getFormat().format(item));
            } else if (!isFloatDateConvert || !(item instanceof Double) || !realName.endsWith("DATE") && !realName.startsWith("DATE")) {
                if (isFloatDateConvert && item instanceof Float && (realName.endsWith("DATE") || realName.startsWith("DATE"))) {
                    writer.writeAttribute("type", Date.class.getName());
                    writer.writeCharacters(DateFormat.getFormat().format(XmlUtil.convertDate((Float) item)));
                } else if (isFloatDateConvert && item instanceof BigDecimal && (realName.endsWith("DATE") || realName.startsWith("DATE"))) {
                    writer.writeAttribute("type", Date.class.getName());
                    writer.writeCharacters(DateFormat.getFormat().format(XmlUtil.convertDate((BigDecimal) item)));
                } else if (item instanceof String) {
                    writer.writeAttribute("type", item.getClass().getName());
                    writer.writeCharacters(((String) item).replace('\u0000', ' '));
                } else if (item instanceof byte[]) {
                    writer.writeAttribute("type", item.getClass().getName());
                    writer.writeCharacters(Base64.encodeBase64String((byte[]) ((byte[]) item)));
                } else {
                    writer.writeAttribute("type", item.getClass().getName());
                    writer.writeCharacters(item.toString());
                }
            } else {
                writer.writeAttribute("type", Date.class.getName());
                writer.writeCharacters(DateFormat.getFormat().format(XmlUtil.convertDate((Double) item)));
            }
        }

    }

    private static void parseMap(XMLStreamWriter writer, Map<String, Object> map, boolean isFloatDateConvert) throws XMLStreamException {
        writer.writeAttribute("type", Map.class.getName());

        for (Object o : map.entrySet()) {
            Map.Entry<String, Object> entry = (Map.Entry) o;
            Object item = entry.getValue();
            if (item != null) {
                if (logger.isDebugEnabled()) {
                    logger.log(Level.ALL, "key = " + (String) entry.getKey() + " item = " + entry.getValue());
                }

                createRootElement(writer, (String) entry.getKey());
                writeItem(writer, item, ((String) entry.getKey()).toUpperCase(), isFloatDateConvert);
                writer.writeEndElement();
            }
        }

    }

    private static void parseList(XMLStreamWriter writer, List list, boolean isFloatDateConvert) throws XMLStreamException {
        if (logger.isDebugEnabled()) {
            logger.log(Level.ALL, "In parseList, list = " + list);
        }

        writer.writeAttribute("type", List.class.getName());
        if (list.size() > 0) {
            writer.writeStartElement("ListItems");

            for (Object objItem : list) {
                if (objItem != null) {
                    writer.writeStartElement("Item");
                    writeItem(writer, objItem, "Item", isFloatDateConvert);
                    writer.writeEndElement();
                }
            }

            writer.writeEndElement();
        }

    }
}
