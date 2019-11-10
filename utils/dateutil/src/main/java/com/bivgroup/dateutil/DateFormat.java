package com.bivgroup.dateutil;

import java.text.SimpleDateFormat;

public class DateFormat {
    private static final String FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss:SSSZ";
    private static final String OLD_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss:SSS";

    public static SimpleDateFormat getFormat() {
        return new SimpleDateFormat(FORMAT_PATTERN);
    }

    public static SimpleDateFormat getOldFormat() {
        return new SimpleDateFormat(OLD_FORMAT_PATTERN);
    }
}
