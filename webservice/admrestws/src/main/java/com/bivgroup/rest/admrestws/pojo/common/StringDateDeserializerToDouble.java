package com.bivgroup.rest.admrestws.pojo.common;

import com.bivgroup.dateutil.DateUtil;
import com.bivgroup.exception.DeserializeException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringDateDeserializerToDouble extends StdDeserializer<Double> {
    private static final Logger logger = Logger.getLogger(StringDateDeserializerToDouble.class);

    private DateFormat formatter;

    public StringDateDeserializerToDouble() {
        this(null);
    }

    public StringDateDeserializerToDouble(Class<?> vc) {
        super(vc);
        formatter = new SimpleDateFormat("dd.MM.yyyy");
    }

    @Override
    public Double deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        String stringDate = jsonparser.getText();
        try {
            Double result = null;
            if (stringDate != null && !stringDate.isEmpty()) {
                Date date = formatter.parse(stringDate);
                result = DateUtil.convertDate(date).doubleValue();
            }
            return result;
        } catch (ParseException e) {
            logger.error(String.format("StringDateDeserializerToDouble date [%s] parse error in deserialize method.", stringDate), e);
            throw new DeserializeException(e);
        }
    }
}
