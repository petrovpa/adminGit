package com.bivgroup.lifeintegrationtypes;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.log4j.Logger;


/**
 *
 * @author sambucus
 */
public class LocalDateTimeConverter {
    
    
    
        public static String printLocalDateTime(XMLGregorianCalendar value) {
        String result = null;
        if (value != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            result = dateFormat.format(value.toGregorianCalendar().getTime());
        }
        return result;
    }

    public static XMLGregorianCalendar parseLocalDateTime(String value) {

        XMLGregorianCalendar result = null;
        if ((value != null) && (!value.isEmpty())) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = dateFormat.parse(value);
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(date);
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

            } catch (DatatypeConfigurationException ex) {
                Logger logger = Logger.getLogger(LocalDateTimeConverter.class);
                logger.error("Error parse string to XMLGregorianCalendar", ex);
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                Logger logger = Logger.getLogger(LocalDateTimeConverter.class);
                logger.error("Error parse string to XMLGregorianCalendar", ex);
                throw new RuntimeException(ex);
            }
        }
        return result;
    }
    
}
