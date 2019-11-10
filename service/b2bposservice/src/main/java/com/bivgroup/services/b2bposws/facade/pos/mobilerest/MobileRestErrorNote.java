/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.mobilerest;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mmamaev
 */
public class MobileRestErrorNote {

    private MobileRestError mobileRestError;
    private String errorNote;

    private static final String TO_STRING_FORMAT = "<" + MobileRestError.ERROR_CODE_PARAMNAME + " = %s; " + MobileRestError.ERROR_MSG_PARAMNAME + " = %s; " + MobileRestError.ERROR_NOTE_PARAMNAME + " = %s>";

    public MobileRestErrorNote(MobileRestError mobileRestError, String errorNote) {
        if (mobileRestError == null) {
            mobileRestError = MobileRestError.SYSTEM_OTHER;
        }
        if (errorNote == null) {
            errorNote = mobileRestError.getMsg();
        }
        this.setMobileRestError(mobileRestError);
        this.setErrorNote(errorNote);
    }

    public MobileRestErrorNote(MobileRestError mobileRestError) {
        this(mobileRestError, null);
    }

    public MobileRestErrorNote() {
        this(null, null);
    }

    public MobileRestErrorNote(String errorNote) {
        this(null, errorNote);
    }

    public MobileRestErrorNote set(MobileRestError mobileRestError, String errorNote) {
        if (mobileRestError == null) {
            mobileRestError = MobileRestError.SYSTEM_OTHER;
        }
        if (errorNote == null) {
            errorNote = mobileRestError.getMsg();
        }
        this.setMobileRestError(mobileRestError);
        this.setErrorNote(errorNote);
        return this;
    }

    public MobileRestErrorNote set(MobileRestError mobileRestError) {
        set(mobileRestError, null);
        return this;
    }

    public MobileRestErrorNote setSuccess() {
        set(MobileRestError.SYSTEM_SUCCESS, null);
        return this;
    }

    public MobileRestErrorNote setGenericError() {
        setGenericError(null);
        return this;
    }

    public MobileRestErrorNote setGenericError(String errorNote) {
        set(MobileRestError.SYSTEM_OTHER, errorNote);
        return this;
    }

    public MobileRestError getMobileRestError() {
        return mobileRestError;
    }

    private void setMobileRestError(MobileRestError mobileRestError) {
        this.mobileRestError = mobileRestError;
    }

    public String getErrorNote() {
        return errorNote;
    }

    private void setErrorNote(String errorNote) {
        this.errorNote = errorNote;
    }

    public boolean isSucces() {
        boolean isSucces = MobileRestError.SYSTEM_SUCCESS.equals(this.getMobileRestError());
        return isSucces;
    }

    public boolean isError() {
        boolean isError = !isSucces();
        return isError;
    }

    public Map<String, Object> putToMap(Map<String, Object> mobileRestErrorMap) {
        if (mobileRestErrorMap == null) {
            mobileRestErrorMap = new HashMap<String, Object>();
        }        
        mobileRestErrorMap.put(MobileRestError.ERROR_CODE_PARAMNAME, this.getCode());
        mobileRestErrorMap.put(MobileRestError.ERROR_MSG_PARAMNAME, this.getMsg());
        mobileRestErrorMap.put(MobileRestError.ERROR_NOTE_PARAMNAME, this.getErrorNote());
        return mobileRestErrorMap;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> mobileRestErrorMap = putToMap(null);
        return mobileRestErrorMap;
    }

    private String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else if (bean instanceof Double) {
            DecimalFormat df = new DecimalFormat("0");
            df.setMaximumFractionDigits(2);
            return df.format(Double.valueOf(bean.toString()).doubleValue());
        } else {
            return bean.toString();
        }
    }

    private String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }

    public MobileRestErrorNote setFromMap(Map<String, Object> mobileRestErrorMap) {
        String errorCodeStr = getStringParam(mobileRestErrorMap, MobileRestError.ERROR_CODE_PARAMNAME);
        String errorMsgStr = getStringParam(mobileRestErrorMap, MobileRestError.ERROR_MSG_PARAMNAME);
        String errorNoteStr = getStringParam(mobileRestErrorMap, MobileRestError.ERROR_NOTE_PARAMNAME);
        if ((!errorMsgStr.isEmpty()) || (!errorMsgStr.isEmpty())) {
            if (errorCodeStr.isEmpty() || (MobileRestError.SYSTEM_SUCCESS.getCodeStr().equals(errorCodeStr))) {
                // код ошибки не найден или соответствует успеху, но имеются строки с текстами ошибок - значит ошибка всё таки имеется
                errorCodeStr = MobileRestError.SYSTEM_OTHER.getCodeStr();
            }
        }
        // todo: мб еще поиск потерянного кода ошибки по errorMsgStr, если где то потребуется
        MobileRestError mobileRestErrorFromMap = null;
        if (errorCodeStr.isEmpty()) {
            mobileRestErrorFromMap = MobileRestError.SYSTEM_SUCCESS;
        } else {
            mobileRestErrorFromMap = MobileRestError.findByCode(errorCodeStr);
            if (mobileRestErrorFromMap == null) {
                mobileRestErrorFromMap = MobileRestError.SYSTEM_OTHER;
            }
        }
        this.set(mobileRestErrorFromMap, errorNoteStr);
        return this;
    }

    public Long getCode() {
        return mobileRestError.getCode();
    }

    public String getCodeStr() {
        return mobileRestError.getCodeStr();
    }

    public String getMsg() {
        return mobileRestError.getMsg();
    }

    @Override
    public String toString() {
        String str = String.format(TO_STRING_FORMAT, this.getMobileRestError().getCodeStr(), this.getMobileRestError().getMsg(), this.getErrorNote());
        return str;
    }

}
