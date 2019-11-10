package com.bivgroup.password;

import java.security.SecureRandom;

/**
 * Класс предназначен дял проверки качества пароля
 */
public class PasswordStrengthVerifier {

    public static final int SET_ASCII_ALPHANUM = 0x00;
    public static final int SET_RUSSIAN = 0x01;
    public static final int SET_ADDITIONAL = 0x02;

    public static final int MAX_REPEAT_COUNT_DEFAULT = 2;
    public static final int MIN_GROUPS_DEFAULT = 3;
    public static final String PWD_MIN_LEN = "PWD_MIN_LEN";
    public static final String PWD_MAX_LEN = "PWD_MAX_LEN";
    public static final String PWD_GROUPS_ADDITIONAL_ALLOWED = "PWD_GROUPS_ADDITIONAL_ALLOWED";
    public static final String PWD_GROUPS_COUNT = "PWD_GROUPS_COUNT";
    public static final String PWD_IDT_SYM_COUNT = "PWD_IDT_SYM_COUNT";

    private char[] additionals;

    private static final char RLMIN = 'а';
    private static final char RLMAX = 'я';
    private static final char RUMIN = 'А';
    private static final char RUMAX = 'Я';

    private static final char ELMIN = 'a';
    private static final char ELMAX = 'z';
    private static final char EUMIN = 'A';
    private static final char EUMAX = 'Z';

    private final int minlen;
    private final int maxlen;

    private final int maxRepeatCount;
    private final int minGroups;

    private final boolean russianAllowed;
    private final boolean additionalAllowed;

    private final SecureRandom random;

    public PasswordStrengthVerifier(int minlen, int maxlen) {
        this(minlen, maxlen, MAX_REPEAT_COUNT_DEFAULT, MIN_GROUPS_DEFAULT, SET_ASCII_ALPHANUM);
    }

    public PasswordStrengthVerifier(int minlen, int maxlen, int maxRepeatCount, int minGroups, int allowedFlags) {
        this.minlen = minlen;
        this.maxlen = maxlen;
        this.maxRepeatCount = maxRepeatCount;
        this.minGroups = minGroups;
        russianAllowed = (allowedFlags & SET_RUSSIAN) != 0;
        additionalAllowed = (allowedFlags & SET_ADDITIONAL) != 0;
        random = new SecureRandom();
        additionals = new char[]{'~', '!', '.', ',', '-', '_', '@', '#', '$', '%', '^', '&', ' ', '*', '(', ')', '+', '`', '=', '{', '}', '[', ']', ':', ';', '<', '>', '/', '\\'};
    }

    public PasswordStrengthVerifier(int minlen, int maxlen, int maxRepeatCount, int minGroups, int allowedFlags, String specialSymbolsEnum) {
        this(minlen, maxlen, maxRepeatCount, minGroups, allowedFlags);
        additionals = specialSymbolsEnum.toCharArray();
    }

    public String generatePassword() {
        int mLen = this.minlen + 1;
        int lLen = this.minlen;
        if (lLen > mLen) {
            mLen = lLen;
        }
        int cLen = random.nextInt(mLen - lLen + 1) + lLen;
        int low = 0;
        int up = 0;
        int digit = 0;
        int additional = 0;
        int cRepeatCount = 1;
        char[] chars = new char[cLen];
        int pos = 0;
        int nextGroup = -1;
        char prevCh = 0;
        while (pos < cLen) {
            int group;
            if (nextGroup < 0) {
                // Группы всего 4.
                if (pos == 0) {
                    group = random.nextInt(3);
                } else if (additionalAllowed) {
                    group = random.nextInt(4);
                } else {
                    group = random.nextInt(3);
                }
                if (pos >= 3) {
                    group = selectNextGroup(low, up, digit, additional, group);
                }
            } else {
                group = nextGroup;
                nextGroup = -1;
            }
            Character ch = null;
            switch (group) {
                // Прописные
                case 0: {
                    ch = generateCursiveLetter();
                    up++;
                    break;
                }
                // Строчные
                case 1:
                    ch = generateLowercaseLetter();
                    low++;
                    break;
                // Цифры
                case 2:
                    ch = generateDigit();
                    digit++;
                    break;
                // Спец. символы.
                case 3:
                    if (additionalAllowed) {
                        ch = generateAdditionalSymbol();
                        additional++;
                        break;
                    }
            }
            if (prevCh == ch) {
                cRepeatCount++;
            } else {
                cRepeatCount = 1;
                prevCh = ch;
            }

            if (cRepeatCount < maxRepeatCount) {
                int countGroup = sign(low) + sign(up) + sign(digit) + sign(additional);
                if (((minGroups - countGroup) > 0) && ((minGroups - countGroup) >= (cLen - pos))) {
                    nextGroup = selectNextGroup(low, up, digit, additional, -1);
                }

                chars[pos] = ch;
                pos++;
            }
        }
        StringBuilder result = new StringBuilder();
        result.append(chars);
        return preliminaryCheckPasswordOnGroupsAndReturn(result);
    }

    private int selectNextGroup(int low, int up, int digit, int additional, int randGroup) {
        if (sign(low) == 0) {
            return 1;
        }
        if (sign(up) == 0) {
            return 0;
        }
        if (sign(digit) == 0) {
            return 2;
        }
        if ((sign(additional) == 0) && additionalAllowed) {
            return 3;
        }
        return randGroup;
    }

    private String preliminaryCheckPasswordOnGroupsAndReturn(StringBuilder password) {
        int low = 0;
        int up = 0;
        int digit = 0;
        int additional = 0;
        char[] chars = password.toString().toCharArray();
        for (char c : chars) {
            if (isLow(c)) {
                low++;
            } else if (isUp(c)) {
                up++;
            } else if (isDigit(c)) {
                digit++;
            } else if (additionalAllowed && isAdditional(c)) {
                additional++;
            }
        }

        if (sign(low) == 0) {
            password.append(selectedGenerationSymbolByGroup(1));
        }
        if (sign(up) == 0) {
            password.append(selectedGenerationSymbolByGroup(0));
        }
        if (sign(digit) == 0) {
            password.append(selectedGenerationSymbolByGroup(2));
        }
        if (sign(additional) == 0 && additionalAllowed) {
            password.append(selectedGenerationSymbolByGroup(3));
        }

        return password.toString();
    }

    private char generateCursiveLetter() {
        return generateLetter(EUMIN, EUMAX, RUMIN, RUMAX);
    }

    private char generateLowercaseLetter() {
        return generateLetter(ELMIN, ELMAX, RLMIN, RLMAX);
    }

    private char generateLetter(char emin, char emax, char rmin, char rmax) {
        int lang = 0;
        if (russianAllowed) {
            lang = random.nextInt(2);
        }
        int startIndex = emin;
        int endIndex = emax;
        if (1 == lang) {
            startIndex = rmin;
            endIndex = rmax;
        }
        return (char) (random.nextInt(endIndex - startIndex) + startIndex);
    }

    private char generateDigit() {
        return String.valueOf(random.nextInt(10)).charAt(0);
    }

    private char generateAdditionalSymbol() {
        int chNum = random.nextInt(additionals.length);
        return additionals[chNum];
    }

    private char selectedGenerationSymbolByGroup(int group) {
        char result = 0;
        switch (group) {
            // Прописные
            case 0:
                result = generateCursiveLetter();
                break;
            // Строчные
            case 1:
                result = generateLowercaseLetter();
                break;
            // Цифры
            case 2:
                result = generateDigit();
                break;
            // Спец. символы.
            case 3:
                if (additionalAllowed) {
                    result = generateAdditionalSymbol();
                    break;
                }
        }
        return result;
    }

    // Тест для проверки корректности генерации пароля N раз
   /* public static void main(String[] args) throws Exception {
        SystemSettingsHelper ssh = new SystemSettingsHelper(null);
        PasswordStrengthVerifier psv = ssh.createPasswordStrengthVerifier();
        String password;
        Result checkPasswordResult;
        boolean valid = true;
        int i = 0;
        for (; i < 30_000_000; ++i) {
            password = psv.generatePassword();
            System.out.println(password);
            checkPasswordResult = psv.isPasswordValid(password);
            if (!checkPasswordResult.equals(Result.OK)) {
                valid = false;
                break;
            }
        }
        System.out.println(valid ? i + " раз пароль сгенерировался корректно" : "Пароль сгенерировался не корректно на " + i + " итерации");
    }*/

    public Result isPasswordValid(String password) {
        if (password == null) {
            return Result.ERROR_NA;
        }
        if (password.length() < minlen) {
            return Result.ERROR_SHORT;
        }
        if (password.length() > maxlen) {
            return Result.ERROR_LONG;
        }
        char prevChar = 0;
        int prevCount = 0;

        int low = 0;
        int up = 0;
        int digit = 0;
        int additional = 0;

        char[] chars = password.toCharArray();

        for (char c : chars) {
            if (isLow(c)) {
                low++;
            } else if (isUp(c)) {
                up++;
            } else if (isDigit(c)) {
                digit++;
            } else if (additionalAllowed && isAdditional(c)) {
                additional++;
            } else {
                return Result.ERROR_INVALID_CHAR;
            }
            if (c == prevChar) {
                prevCount++;
            } else {
                prevChar = c;
                prevCount = 1;
            }
            if (prevCount > maxRepeatCount) {
                return Result.ERROR_MAX_REPEAT;
            }
        }
        int groups = sign(low) + sign(up) + sign(digit) + sign(additional);
        if (groups < minGroups) {
            return Result.ERROR_LOW_COMPLEXITY;
        }
        return Result.OK;
    }

    protected boolean isLow(char c) {
        return ('a' <= c && c <= 'z') || (russianAllowed && RLMIN <= c && c <= RLMAX);
    }

    protected boolean isUp(char c) {
        return ('A' <= c && c <= 'Z') || (russianAllowed && RUMIN <= c && c <= RUMAX);
    }

    protected boolean isDigit(char c) {
        return ('0' <= c && c <= '9');
    }

    protected boolean isAdditional(char c) {
        for (char ac : additionals) {
            if (ac == c) {
                return true;
            }
        }
        return false;
    }

    protected int sign(int arg) {
        return Integer.compare(arg, 0);
    }

    public int getMinlen() {
        return minlen;
    }

    public int getMaxlen() {
        return maxlen;
    }

    public int getMaxRepeatCount() {
        return maxRepeatCount;
    }

    public int getMinGroups() {
        return minGroups;
    }

    public boolean isRussianAllowed() {
        return russianAllowed;
    }

    public boolean isAdditionalAllowed() {
        return additionalAllowed;
    }

    public String getAddidionalSymbols(){
        return String.valueOf(additionals);
    }

    public enum Result {
        OK("Пароль правильный"),
        ERROR_NA("Пароль не задан (пустой)"),
        ERROR_SHORT("Длина пароля меньше допустимой минимальной"),
        ERROR_LONG("Длина пароля больше допустимой максимальной"),
        ERROR_INVALID_CHAR("Пароль содержит недопустимые символы"),
        ERROR_MAX_REPEAT("Символ повторяется слишком большое количество раз подряд"),
        ERROR_LOW_COMPLEXITY("Пароль слишком простой (содержит мало групп символов)");

        private String description;

        private Result(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

}
