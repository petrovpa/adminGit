package com.bivgroup.cbr.cbrcurrancyrategetter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import ru.cbr.web.valute.ValuteCursOnDateType;
import ru.cbr.web.valute.ValuteDataType;



/**
 * Hello world!
 *
 */
public class App {

    protected static <T> T unmarshall(Class<T> docClass, String xmlText) throws Exception {
        T result = null;
        try {
            result = unmarshall(docClass, new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            //Logger.getLogger(RSABaseService.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception(ex);
        }
        return result;
    }

    protected static <T> T unmarshall(Class<T> docClass, InputStream inputStream) throws Exception {
        try {
            String packageName = docClass.getPackage().getName();
            JAXBContext jc = JAXBContext.newInstance(packageName);
            Unmarshaller u = jc.createUnmarshaller();
            T result = (T) u.unmarshal(inputStream);
            return result;
        } catch (JAXBException ex) {
            throw new Exception("Ошибка преобразования хмл в объект", ex);
        } catch (RuntimeException ex) {
            throw new Exception("Ошибка преобразования хмл в объект", ex);
        }
    }

    protected static <T> T unmarshall(Class<T> docClass, Node node) throws Exception {
        try {
            String packageName = docClass.getPackage().getName();
            JAXBContext jc = JAXBContext.newInstance(packageName);
            Unmarshaller u = jc.createUnmarshaller();
            T result = (T) u.unmarshal(node);
            return result;
        } catch (JAXBException ex) {
            throw new Exception("Ошибка преобразования хмл в объект", ex);
        } catch (RuntimeException ex) {
            throw new Exception("Ошибка преобразования хмл в объект", ex);
        }
    }

    private static String nodeToString(Node node) {
        Document document = node.getOwnerDocument();
        DOMImplementation impl =  document.getImplementation();

        DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
        LSSerializer lsSerializer = implLS.createLSSerializer();
        lsSerializer.getDomConfig().setParameter("format-pretty-print", true);

        LSOutput lsOutput = implLS.createLSOutput();
        lsOutput.setEncoding("UTF-8");
        Writer stringWriter = new StringWriter();
        lsOutput.setCharacterStream(stringWriter);
        lsSerializer.write(document, lsOutput);

        String result = stringWriter.toString();
        return result;
    }

    public static void main(String[] args) throws DatatypeConfigurationException, Exception {
        CurrencyOnDateCaller caller = new CurrencyOnDateCaller();
        ValuteDataType valuteData = caller.getCurrencyOnDate(new Date());
        for(ValuteCursOnDateType curs:valuteData.getValuteCursOnDate()){
            System.out.println(new String(curs.getVchCode().getBytes("UTF-8")) +"=" +curs.getVcurs().toString());
        }
    }
}
