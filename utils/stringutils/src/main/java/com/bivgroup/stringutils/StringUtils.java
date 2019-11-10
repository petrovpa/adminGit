package com.bivgroup.stringutils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean equals(String string, String anotherString) {
        return string != null && string.equals(anotherString);
    }

    public static String replace1(String source, String sample, String replacement) {
        int i;
        while((i = source.indexOf(sample)) >= 0) {
            source = source.substring(0, i) + replacement + source.substring(i + sample.length());
        }

        return source;
    }

    public static String replace2(String source, String sample, String replacement) {
        StringBuilder result = new StringBuilder();
        int sourceLength = source.length();
        int sampleLength = sample.length();
        int i = 0;

        for(int j = 0; i < sourceLength; ++i) {
            char sourceChar = source.charAt(i);
            char sampleChar = sample.charAt(j);
            if (sourceChar == sampleChar) {
                ++j;
            } else {
                result.append(source.substring(i - j, i));
                j = 0;
            }

            if (j == 0) {
                result.append(sourceChar);
            } else if (j == sampleLength) {
                result.append(replacement);
                j = 0;
            }
        }

        return result.toString();
    }

    public static String substr(String str, int letterNum) {
        if (str == null) {
            return "";
        } else {
            return str.length() <= letterNum ? "" : str.substring(letterNum++, letterNum);
        }
    }

    public static String substr(String str, int from, int to) {
        if (str == null) {
            return "";
        } else if (str.length() <= from) {
            return "";
        } else {
            return str.length() > to ? str.substring(from, to) : str.substring(from);
        }
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    public static String trim(String s) {
        return s != null ? s.trim() : "";
    }

    public static boolean isEmpty(String s, boolean trim) {
        boolean var10000;
        if (s != null) {
            label29: {
                if (trim) {
                    if (s.trim().equals("")) {
                        break label29;
                    }
                } else if (s.equals("")) {
                    break label29;
                }

                var10000 = false;
                return var10000;
            }
        }

        var10000 = true;
        return var10000;
    }

    public static String concat(String s1, String s2) {
        return (s1 != null ? s1 : "") + (s2 != null ? s2 : "");
    }

    public static String concat(String s1, String s2, String delim) {
        return concat(isEmpty(s1, false) ? "" : (isEmpty(s2, false) ? s1 : concat(s1, delim)), s2);
    }

    public static String concat(String s1, String s2, String delim, boolean trim) {
        return concat(isEmpty(s1, trim) ? "" : (isEmpty(s2, trim) ? s1 : concat(s1, delim)), s2);
    }

    public static String castToLengthFromTail(String s, int length) {
        return castToLength(s, length, ' ', true);
    }

    public static String castToLengthFromTail(String s, int length, char addChar) {
        return castToLength(s, length, addChar, true);
    }

    public static String castToLengthFromHead(String s, int length) {
        return castToLength(s, length, ' ', false);
    }

    public static String castToLengthFromHead(String s, int length, char addChar) {
        return castToLength(s, length, addChar, false);
    }

    private static String castToLength(String s, int length, char addChar, boolean addToTail) {
        s = s != null ? s : "";
        if (s.length() != length && length >= 0) {
            if (length == 0) {
                return "";
            } else if (s.length() > length) {
                return s.substring(0, length);
            } else {
                char[] addChars = new char[length - s.length()];

                for(int i = 0; i < addChars.length; ++i) {
                    addChars[i] = addChar;
                }

                return addToTail ? s + String.valueOf(addChars) : addChars + s;
            }
        } else {
            return s;
        }
    }

    public static String truncate(String s, int length) {
        if (s != null && s.length() > length) {
            s = s.substring(0, length) + "...";
        }

        return s;
    }

    public static String convertValueToString(String prefix, Object value) {
        StringBuffer result = new StringBuffer();
        if (value == null) {
            result.append("null");
        } else if (value instanceof Map) {
            result.append(value.getClass().getName()).append('\n');
            Map<?, ?> m = (Map)value;
            Iterator i$ = m.keySet().iterator();

            while(i$.hasNext()) {
                Object key = i$.next();
                result.append(prefix).append("\t").append(key).append(": ").append(convertValueToString(prefix + "\t", m.get(key))).append("\n");
            }
        } else if (value instanceof Collection) {
            result.append(value.getClass().getName()).append(":\n");
            Collection<?> c = (Collection)value;
            int i = 0;

            for(Iterator i$ = c.iterator(); i$.hasNext(); ++i) {
                Object element = i$.next();
                result.append(prefix).append("\t").append(i).append(": ").append(convertValueToString(prefix + "\t", element)).append("\n");
            }
        } else if (value.getClass().isArray()) {
            result.append(Arrays.deepToString((Object[])((Object[])value))).append('\n');
        } else {
            result.append("'").append(value).append("' (").append(value.getClass().getName()).append(") ");
        }

        return result.toString();
    }
}
