package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReadImportSettingsResponse {

    private String settingTime;
    private String settingCatalog;
    private String settingMail;

    @JsonProperty("KMSB_AUTOIMPORT_WORKTIME")
    public String getSettingTime() {
        return settingTime;
    }

    public void setSettingTime(String settingTime) {
        this.settingTime = settingTime;
    }

    @JsonProperty("KMSB_COMMON_FOLDER")
    public String getSettingCatalog() {
        return settingCatalog;
    }

    public void setSettingCatalog(String settingCatalog) {
        this.settingCatalog = settingCatalog;
    }

    @JsonProperty("KMSB_IMPORTRESULT_EMAIL")
    public String getSettingMail() {
        return settingMail;
    }

    public void setSettingMail(String settingMail) {
        this.settingMail = settingMail;
    }


    @Override
    public String toString() {
        return "ReadImportSettingsResponseParams{" +
                "settingTime='" + settingTime + '\'' +
                ", settingCatalog='" + settingCatalog + '\'' +
                ", settingMail='" + settingMail + '\'' +
                '}';
    }
}
