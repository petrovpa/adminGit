package com.bivgroup.services.b2bposws.facade.pos.validation;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alex Ivashin
 */
public class DescriptionParameter {

    public interface UniqueVerification {
        boolean check(Object parameter, String errorPrefix);
    }

    private String sysName;
    private String sysNameDetermines;
    private String parameterName;
    private boolean valueDetermines;
    private List<Long> incorrectParameterValues;
    private UniqueVerification verification;
    private boolean needChangePersonalData;

    public DescriptionParameter(String sysName, String sysNameDetermines,
                                String parameterName, boolean valueDetermines,
                                UniqueVerification verification) {
        this(sysName, parameterName, verification);
        this.sysNameDetermines = sysNameDetermines;
        this.valueDetermines = valueDetermines;
    }

    public DescriptionParameter(String sysName, String parameterName,
                                UniqueVerification verification, Long... incorrectParameterValues) {
        this(sysName, parameterName, verification);
        this.incorrectParameterValues = Arrays.asList(incorrectParameterValues);
    }

    public DescriptionParameter(String sysName, String parameterName, UniqueVerification verification) {
        this(sysName, parameterName);
        this.verification = verification;
    }

    public DescriptionParameter(String sysName, String sysNameDetermines,
                                String parameterName, boolean valueDetermines) {
        this(sysName, parameterName);
        this.sysNameDetermines = sysNameDetermines;
        this.valueDetermines = valueDetermines;
    }

    public DescriptionParameter(String sysName, String parameterName,
                                Long... incorrectParameterValues) {
        this(sysName, parameterName);
        this.incorrectParameterValues = Arrays.asList(incorrectParameterValues);
    }

    public DescriptionParameter(String sysName, String parameterName) {
        this.sysName = sysName;
        this.parameterName = parameterName;
        this.needChangePersonalData = false;
    }

    public DescriptionParameter(DescriptionParameter parameter) {
        this.sysName = parameter.getSysName();
        this.sysNameDetermines = parameter.getSysNameDetermines();
        this.parameterName = parameter.getParameterName();
        this.valueDetermines = parameter.isValueDetermines();
        this.incorrectParameterValues = parameter.getIncorrectParameterValues();
        this.verification = parameter.getVerification();
    }

    public String getSysName() {
        return sysName;
    }

    public String getSysNameDetermines() {
        return sysNameDetermines;
    }

    public String getParameterName() {
        return parameterName;
    }

    public boolean isValueDetermines() {
        return valueDetermines;
    }

    public List<Long> getIncorrectParameterValues() {
        return incorrectParameterValues;
    }

    public UniqueVerification getVerification() {
        return verification;
    }

    public boolean isRequiredDependingOfValueDeterminesParameter(Object determinesParameter) {
        return (valueDetermines) ? isRequiredIfTrue(determinesParameter) : isRequireIfFalse(determinesParameter);
    }

    public boolean isRequiredIfTrue(Object parameter) {
        boolean result = false;
        if (parameter != null) {
            result = Integer.parseInt(parameter.toString()) == 1;
        }
        return result;
    }

    public boolean isRequireIfFalse(Object parameter) {
        boolean result = true;
        if (parameter != null) {
            result = Integer.parseInt(parameter.toString()) == 0;
        }
        return result;
    }

    public boolean isIncorrectValue(Long value) {
        return isLongValueParam() && incorrectParameterValues.contains(value);
    }

    public boolean isLongValueParam() {
        return incorrectParameterValues != null;
    }

    public boolean isMissingDependentParameter() {
        return sysNameDetermines == null || sysNameDetermines.isEmpty();
    }

    public DescriptionParameter setVerification(UniqueVerification verification) {
        this.verification = verification;
        return this;
    }

    public DescriptionParameter setSysName(String sysName) {
        this.sysName = sysName;
        return this;
    }

    public DescriptionParameter setSysNameDetermines(String sysNameDetermines) {
        this.sysNameDetermines = sysNameDetermines;
        return this;
    }

    public DescriptionParameter setParameterName(String parameterName) {
        this.parameterName = parameterName;
        return this;
    }

    public DescriptionParameter setValueDetermines(boolean valueDetermines) {
        this.valueDetermines = valueDetermines;
        return this;
    }

    public DescriptionParameter setIncorrectParameterValues(List<Long> incorrectParameterValues) {
        this.incorrectParameterValues = incorrectParameterValues;
        return this;
    }

    public boolean needChangePersonalData() {
        return this.needChangePersonalData;
    }

    public DescriptionParameter setNeedChangePersonalData(boolean needChangePersonalData) {
        this.needChangePersonalData = needChangePersonalData;
        return this;
    }

    public DescriptionParameter isNeedChangePersonalData() {
        return this.setNeedChangePersonalData(true);
    }

    public DescriptionParameter isNotNeedChangePersonalData() {
        return this.setNeedChangePersonalData(false);
    }

    public boolean applyUniqueVerification(Object parameter, String errorPrefix) {
        boolean result = true;
        if (verification != null) {
            result = verification.check(parameter, errorPrefix);
        }
        return result;
    }
}
