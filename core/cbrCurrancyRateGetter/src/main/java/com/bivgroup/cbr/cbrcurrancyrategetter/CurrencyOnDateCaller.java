/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.cbr.cbrcurrancyrategetter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import ru.cbr.web.valute.ValCurs;
import ru.cbr.web.valute.ValuteCursOnDateType;
import ru.cbr.web.valute.ValuteDataType;



/**
 * Вызывает сервис получения курсов валют на дату
 *
 * @author reson
 */
public class CurrencyOnDateCaller {

    private static final Logger logger = Logger.getLogger("com.bivgroup.cbr.cbrcurrancyrategetter.CurrencyOnDateCaller");
    protected <T> T unmarshall(Class<T> docClass, String xmlText, String encoding) throws CurrencyCallException, JAXBException {
        T result = null;
        try {
            result = this.unmarshall(docClass, new ByteArrayInputStream(xmlText.getBytes(encoding)));
        } catch (UnsupportedEncodingException ex) {
            //Logger.getLogger(RSABaseService.class.getName()).log(Level.SEVERE, null, ex);
            throw new CurrencyCallException(ex);
        }
        return result;
    }

    protected <T> T unmarshall(Class<T> docClass, InputStream inputStream) throws CurrencyCallException {
        try {
            String packageName = docClass.getPackage().getName();
            JAXBContext jc = JAXBContext.newInstance(packageName);
            Unmarshaller u = jc.createUnmarshaller();
            T result = (T) u.unmarshal(inputStream);
            return result;
        } catch (JAXBException ex) {
            throw new CurrencyCallException("Ошибка преобразования хмл в объект", ex);
        } catch (RuntimeException ex) {
            throw new CurrencyCallException("Ошибка преобразования хмл в объект", ex);
        }
    }

    private String nodeToString(Node node) {
        Document document = node.getOwnerDocument();
        DOMImplementation impl = document.getImplementation();

        DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
        LSSerializer lsSerializer = implLS.createLSSerializer();
//        lsSerializer.getDomConfig().setParameter("format-pretty-print", true);

        LSOutput lsOutput = implLS.createLSOutput();
        lsOutput.setEncoding("UTF-8");
        Writer stringWriter = new StringWriter();
        lsOutput.setCharacterStream(stringWriter);
        lsSerializer.write(document, lsOutput);

        String result = stringWriter.toString();
        return result;
    }


    private ValuteDataType getCursOnDateHttp(Date date) throws CurrencyCallException, JAXBException {
        String resText = null;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("http://www.cbr.ru/scripts/XML_daily.asp?date_req=").append(dateFormat.format(date));
        URL url;
        StringBuilder getRes = new StringBuilder();
        try {
            url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                getRes.append(line);
            }
            rd.close();
            resText = getRes.toString();
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(CurrencyOnDateCaller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(CurrencyOnDateCaller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        if (resText != null) {
            resText = resText.replace("<ValCurs", "<ValCurs xmlns=\"http://web.cbr.ru/valute/\"");
            resText = resText.substring(resText.indexOf("<ValCurs"));
        }
        ValCurs valCurs = null;
        try {
            valCurs = unmarshall(ValCurs.class, resText, "windows-1251");
        } catch (Exception e)  {
            valCurs = unmarshall(ValCurs.class, resText, "utf-8");
        }
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        ValuteDataType result = new ValuteDataType();
        //if (valCurs.getDate().equals(dateFormat.format(date))) {
            for (ValCurs.Valute valute : valCurs.getValute()) {
                ValuteCursOnDateType valDate = new ValuteCursOnDateType();
                valDate.setVname(valute.getName());
                valDate.setVcode(valute.getNumCode());
                valDate.setVchCode(valute.getCharCode());
                valDate.setVnom(valute.getNominal());
                valDate.setVcurs(new BigDecimal(valute.getValue().replace(',', '.')));
                result.getValuteCursOnDate().add(valDate);
            }
        //}
        return result;
    }

    public ValuteDataType getCurrencyOnDate(Date date) throws CurrencyCallException, JAXBException {

        ValuteDataType valuteData = null;
        
        try {
            valuteData = this.getCursOnDateHttp(date);
        } catch (CurrencyCallException ex) {
            valuteData = null;
            logger.log(Level.INFO, "Error get curs via http", ex);
        }
                
        if (valuteData == null) {
            throw new CurrencyCallException("Error get curs. See log for details");
        }

        return valuteData;

    }
}
