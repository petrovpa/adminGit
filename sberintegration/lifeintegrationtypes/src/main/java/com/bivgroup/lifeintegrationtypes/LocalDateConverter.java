/*
 * Copyright (c) Diasoft 2004-2013
 */
package com.bivgroup.lifeintegrationtypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.log4j.Logger;

/**
 *
 * @author sambucus
 */
public class LocalDateConverter {

    public static String printLocalDate(XMLGregorianCalendar value) {
        String result = null;

        if (value != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            result = dateFormat.format(value.toGregorianCalendar().getTime());
        }
        return result;
    }

    public static XMLGregorianCalendar parseLocalDate(String value) {

        XMLGregorianCalendar result = null;
        if ((value != null) && (!value.isEmpty())) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(value);
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(date);
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

            } catch (DatatypeConfigurationException ex) {
                Logger logger = Logger.getLogger(LocalDateConverter.class);
                logger.error("Error parse string to XMLGregorianCalendar", ex);
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                Logger logger = Logger.getLogger(LocalDateConverter.class);
                logger.error("Error parse string to XMLGregorianCalendar", ex);
                throw new RuntimeException(ex);
            }
        }
        return result;
    }
}
