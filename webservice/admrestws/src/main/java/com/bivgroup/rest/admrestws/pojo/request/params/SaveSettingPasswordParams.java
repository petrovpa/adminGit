package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SaveSettingPasswordParams {
    private String minLength;
    private String maxLength;
    private String groupsAdditionalAllowed;
    private String groupsCount;
    private String idtSymCount;
    private String maxAge;
    private Boolean maxAgeOn;
    private String historyCount;
    private Boolean historyOn;
    private String userMaxFailedLoginAttempts;
    private String userMaxInactivityTime;
    private String licenseExpCnt;
    private long sessionSize;

    public SaveSettingPasswordParams() {

    }

    public SaveSettingPasswordParams(String minLength, String maxLength, String groupsAdditionalAllowed, String groupsCount,
                                     String idtSymCount, String maxAge, Boolean maxAgeOn, String historyCount, Boolean historyOn,
                                     String userMaxFailedLoginAttempts, String userMaxInactivityTime, String licenseExpCnt, long sessionSize) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.groupsAdditionalAllowed = groupsAdditionalAllowed;
        this.groupsCount = groupsCount;
        this.idtSymCount = idtSymCount;
        this.maxAge = maxAge;
        this.maxAgeOn = maxAgeOn;
        this.historyCount = historyCount;
        this.historyOn = historyOn;
        this.userMaxFailedLoginAttempts = userMaxFailedLoginAttempts;
        this.userMaxInactivityTime = userMaxInactivityTime;
        this.licenseExpCnt = licenseExpCnt;
        this.sessionSize = sessionSize;
    }

    @JsonProperty("PWD_MIN_LEN")
    public String getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    @JsonProperty("PWD_MAX_LEN")
    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    @JsonProperty("PWD_GROUPS_ADDITIONAL_ALLOWED")
    public String getGroupsAdditionalAllowed() {
        return groupsAdditionalAllowed;
    }

    public void setGroupsAdditionalAllowed(String groupsAdditionalAllowed) {
        this.groupsAdditionalAllowed = groupsAdditionalAllowed;
    }

    @JsonProperty("PWD_GROUPS_COUNT")
    public String getGroupsCount() {
        return groupsCount;
    }

    public void setGroupsCount(String groupsCount) {
        this.groupsCount = groupsCount;
    }

    @JsonProperty("PWD_IDT_SYM_COUNT")
    public String getIdtSymCount() {
        return idtSymCount;
    }

    public void setIdtSymCount(String idtSymCount) {
        this.idtSymCount = idtSymCount;
    }

    @JsonProperty("PWD_MAX_AGE")
    public String getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }

    @JsonProperty("PWD_MAX_AGE_ON")
    public Boolean getMaxAgeOn() {
        return maxAgeOn;
    }

    public void setMaxAgeOn(Boolean maxAgeOn) {
        this.maxAgeOn = maxAgeOn;
    }

    @JsonProperty("PWD_HISTORY_COUNT")
    public String getHistoryCount() {
        return historyCount;
    }

    public void setHistoryCount(String historyCount) {
        this.historyCount = historyCount;
    }

    @JsonProperty("PWD_HISTORY_ON")
    public Boolean getHistoryOn() {
        return historyOn;
    }

    public void setHistoryOn(Boolean historyOn) {
        this.historyOn = historyOn;
    }

    @JsonProperty("USR_MAX_FAILED_LOGIN_ATTEMPTS")
    public String getUserMaxFailedLoginAttempts() {
        return userMaxFailedLoginAttempts;
    }

    public void setUserMaxFailedLoginAttempts(String userMaxFailedLoginAttempts) {
        this.userMaxFailedLoginAttempts = userMaxFailedLoginAttempts;
    }

    @JsonProperty("USR_MAX_INACTIVITY_TIME")
    public String getUserMaxInactivityTime() {
        return userMaxInactivityTime;
    }

    public void setUserMaxInactivityTime(String userMaxInactivityTime) {
        this.userMaxInactivityTime = userMaxInactivityTime;
    }

    @JsonProperty("LICENSE_EXP_CNT")
    public String getLicenseExpCnt() {
        return licenseExpCnt;
    }

    public void setLicenseExpCnt(String licenseExpCnt) {
        this.licenseExpCnt = licenseExpCnt;
    }

    @JsonProperty("SESSION_SIZE")
    public long getSessionSize() {
        return sessionSize;
    }

    public void setSessionSize(long sessionSize) {
        this.sessionSize = sessionSize;
    }

    @Override
    public String toString() {
        return "SaveSettingPasswordParams{" +
                "minLength='" + minLength + '\'' +
                ", maxLength='" + maxLength + '\'' +
                ", groupsAdditionalAllowed='" + groupsAdditionalAllowed + '\'' +
                ", groupsCount='" + groupsCount + '\'' +
                ", idtSymCount='" + idtSymCount + '\'' +
                ", maxAge='" + maxAge + '\'' +
                ", maxAgeOn=" + maxAgeOn +
                ", historyCount='" + historyCount + '\'' +
                ", historyOn=" + historyOn +
                ", userMaxFailedLoginAttempts='" + userMaxFailedLoginAttempts + '\'' +
                ", userMaxInactivityTime='" + userMaxInactivityTime + '\'' +
                ", licenseExpCnt='" + licenseExpCnt + '\'' +
                ", sessionSize='" + sessionSize + '\'' +
                '}';
    }
}
