package com.bivgroup.services.b2bposws.facade.pos.invest.custom;

/**
 *
 * @author aklunok
 */
public class B2BGraphHelper {

    public static final String SERIES = "\"series\": ";
    public static final String ARRAY_OPEN = "[";
    public static final String ARRAY_CLOSE = "]";
    public static final String OBJECT_OPEN = "{";
    public static final String OBJECT_CLOSE = "}";
    public static final String ATTR_DELIM = ",";
    public static final char OBJECT_DELIM = ',';
    public static final String SERIES_NAME = "\"name\": \"%1s\",";
    public static final String SERIES_THRESHOLD = "\"turboThreshold\": \"3000\",";
    public static final String SERIES_DATA = "\"data\": ";
    // {x: 1266278400000, y: 29.06, z: 1},
    public static final String SERIES_DATA_ITEM = "{\"x\": %1s, \"y\": %2s, \"z\": %3s},";
    public static final String SERIES_ZONES = "\"zones\": ";
    public static final String SERIES_ZONE_ITEM_C = "{\"color\": \"%1s\"},";
    public static final String SERIES_ZONE_ITEM_VC = "{\"value\": %1s, \"color\": \"%2s\"},";
    public static final String SERIES_DATAGROUPING = "\"dataGrouping\": {\"dateTimeLabelFormats\": {\"week\": [\"%A, %b %e, %Y\", \"%A, %b %e\", \"-%A, %b %e, %Y\"]}},";

    public static void openSeries(StringBuilder gr, String seriesName) {
        gr.append(OBJECT_OPEN);
        // name
        gr.append(String.format(SERIES_NAME, seriesName));
        // threshold
        gr.append(SERIES_THRESHOLD);
        // data
        gr.append(SERIES_DATA);
        gr.append(ARRAY_OPEN);
    }

    public static void closeSeries(StringBuilder gr, String seriesColor) {
        gr.append(ARRAY_CLOSE);
        gr.append(ATTR_DELIM);
        // dataGrouping
        gr.append(SERIES_DATAGROUPING);
        // zones
        gr.append(SERIES_ZONES);
        gr.append(ARRAY_OPEN);
        gr.append(String.format(SERIES_ZONE_ITEM_C, seriesColor));
        if (gr.charAt(gr.length() - 1) == OBJECT_DELIM) {
            gr.deleteCharAt(gr.length() - 1);
        }
        gr.append(ARRAY_CLOSE);
        gr.append(OBJECT_CLOSE);
        gr.append(ATTR_DELIM);
    }

    public static void openGraph(StringBuilder gr) {
        gr.append(OBJECT_OPEN);
        gr.append(SERIES);
        gr.append(ARRAY_OPEN);
    }

    public static void closeGraph(StringBuilder gr) {
        gr.append(ARRAY_CLOSE);
        gr.append(OBJECT_CLOSE);
    }

}
