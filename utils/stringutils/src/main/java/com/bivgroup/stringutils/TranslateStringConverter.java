package com.bivgroup.stringutils;

/**
 * Класс переводит русский текст в транслит. Например, строка "Текст" будет
 * преобразована в "Tekst".
 */
public class TranslateStringConverter {

    private static final String[] charTable = new String[81];

    private static final char START_CHAR = "Ё".charAt(0);

    static {
        charTable["А".charAt(0)- START_CHAR] = "A";
        charTable["Б".charAt(0)- START_CHAR] = "B";
        charTable["В".charAt(0)- START_CHAR] = "V";
        charTable["Г".charAt(0)- START_CHAR] = "G";
        charTable["Д".charAt(0)- START_CHAR] = "D";
        charTable["Е".charAt(0)- START_CHAR] = "E";
        charTable["Ё".charAt(0)- START_CHAR] = "E";
        charTable["Ж".charAt(0)- START_CHAR] = "ZH";
        charTable["З".charAt(0)- START_CHAR] = "Z";
        charTable["И".charAt(0)- START_CHAR] = "I";
        charTable["Й".charAt(0)- START_CHAR] = "I";
        charTable["К".charAt(0)- START_CHAR] = "K";
        charTable["Л".charAt(0)- START_CHAR] = "L";
        charTable["М".charAt(0)- START_CHAR] = "M";
        charTable["Н".charAt(0)- START_CHAR] = "N";
        charTable["О".charAt(0)- START_CHAR] = "O";
        charTable["П".charAt(0)- START_CHAR] = "P";
        charTable["Р".charAt(0)- START_CHAR] = "R";
        charTable["С".charAt(0)- START_CHAR] = "S";
        charTable["Т".charAt(0)- START_CHAR] = "T";
        charTable["У".charAt(0)- START_CHAR] = "U";
        charTable["Ф".charAt(0)- START_CHAR] = "F";
        charTable["Х".charAt(0)- START_CHAR] = "H";
        charTable["Ц".charAt(0)- START_CHAR] = "C";
        charTable["Ч".charAt(0)- START_CHAR] = "CH";
        charTable["Ш".charAt(0)- START_CHAR] = "SH";
        charTable["Щ".charAt(0)- START_CHAR] = "SH";
        charTable["Ъ".charAt(0)- START_CHAR] = "\"" ;
        charTable["Ы".charAt(0)- START_CHAR] = "Y";
        charTable["Ь".charAt(0)- START_CHAR] = "\"";
        charTable["Э".charAt(0)- START_CHAR] = "E";
        charTable["Ю".charAt(0)- START_CHAR] = "U";
        charTable["Я".charAt(0)- START_CHAR] = "YA";

        for (int i = 0; i < charTable.length; i++) {
            char idx = (char)((char)i + START_CHAR);
            char lower = new String(new char[]{idx}).toLowerCase().charAt(0);
            if (charTable[i] != null) {
                charTable[lower - START_CHAR] = charTable[i].toLowerCase();
            }
        }
    }


    /**
     * Переводит русский текст в транслит. В результирующей строке
     * каждая русская буква будет заменена на соответствующую английскую.
     * Не русские символы останутся прежними.
     *
     * @param text исходный текст с русскими символами
     * @return результат
     */
    public static String toTranslit(String text) {
        char charBuffer[] = text.toCharArray();
        StringBuilder sb = new StringBuilder(text.length());
        for (char symbol : charBuffer) {
            int i = symbol - START_CHAR;
            if (i>=0 && i<charTable.length) {
                String replace = charTable[i];
                sb.append(replace == null ? symbol : replace);
            }
            else {
                sb.append(symbol);
            }
        }
        return sb.toString();
    }

}
