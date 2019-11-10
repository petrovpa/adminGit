package com.bivgroup.rest.admrestws.pojo.common;

import com.bivgroup.dateutil.DateUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DoubleDeserializedToString extends StdDeserializer<String> {
    private DateFormat formatter;

    public DoubleDeserializedToString() {
        this(null);
    }

    public DoubleDeserializedToString(Class<Double> t) {
        super(t);
        this.formatter = new SimpleDateFormat("dd.MM.yyyy");
    }

    @Override
    public String deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        return formatter.format(DateUtil.convertDate(jsonparser.getValueAsDouble()));
    }
}