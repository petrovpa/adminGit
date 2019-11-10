package com.bivgroup.services.b2bposws.facade.admin;


import com.bivgroup.password.PasswordStrengthVerifier;

import java.util.ArrayList;
import java.util.List;

class PasswordStrengthDescriptor extends PasswordStrengthVerifier {

    public PasswordStrengthDescriptor(PasswordStrengthVerifier verifier) {
        super(verifier.getMinlen(), verifier.getMaxlen(), verifier.getMaxRepeatCount(),
                verifier.getMinGroups(), verifier.isRussianAllowed() ? 1 : 0, verifier.getAddidionalSymbols());
    }

    public List<Result> checkErrors(String password) {
        List<Result> result = new ArrayList<>();
        if (password == null) {
            result.add(Result.ERROR_NA);
            return result;
        }
        if (password.length() < getMinlen()) {
            result.add(Result.ERROR_SHORT);
        }
        if (password.length() > getMaxlen()) {
            result.add(Result.ERROR_LONG);
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
            } else if (isAdditionalAllowed() && isAdditional(c)) {
                additional++;
            } else {
                // чтобы не дублировались ошибки
                if (!result.contains(Result.ERROR_INVALID_CHAR)) result.add(Result.ERROR_INVALID_CHAR);
            }
            if (c == prevChar) {
                prevCount++;
            } else {
                prevChar = c;
                prevCount = 1;
            }
            if (prevCount > getMaxRepeatCount() && !result.contains(Result.ERROR_MAX_REPEAT)) {
                result.add(Result.ERROR_MAX_REPEAT);
            }
        }
        int groups = sign(low) + sign(up) + sign(digit) + sign(additional);
        if (groups < getMinGroups()) {
            result.add(Result.ERROR_LOW_COMPLEXITY);
        }
        return result;
    }

}
