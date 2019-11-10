package com.bivgroup.rest.admrestws.pojo.request.params;

import com.bivgroup.rest.common.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.bivgroup.utils.ParamGetter.getLongParam;
import static com.bivgroup.config.Config.getConfig;

public class RightListByAccountIdParams {
    private Long userAccountId;
    private String rightOwner;
    private Long packageId;

    public RightListByAccountIdParams() {
        this.rightOwner = "ACCOUNT";
    }

    public RightListByAccountIdParams(Long userAccountId, String rightOwner) {
        this.userAccountId = userAccountId;
        this.rightOwner = rightOwner;
        this.packageId = getLongParam(getConfig(Constants.THIS_SERVICE_NAME)
                .getParam("DEFAULT_PACKAGE", "1001"));
    }

    /**
     * Идентификатор аккаунта пользователя, для которого требуется получить список прав
     *
     * @return идентификатор аккаунта пользователя
     */
    @JsonProperty("USERACCOUNTID")
    @NotNull(message = "Не передан обязательный параметр: идентификатор аккаунта пользователя")
    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    /**
     * Владелец права
     * Так как метод получает только для пользователя, то может принимать
     * лишь значение 'ACCOUNT'
     *
     * @return системное имя владельца права
     */
    @JsonProperty(value = "RIGHTOWNER")
    @NotNull(message = "Не передан обязательный параметр: тип владельца права")
    @Pattern(regexp = "ACCOUNT", message = "Не правильное значение параметра: тип владельца права")
    public String getRightOwner() {
        return rightOwner;
    }

    public void setRightOwner(String rightOwner) {
        this.rightOwner = rightOwner;
    }

    @JsonProperty("PACKAGEID")
    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    @Override
    public String toString() {
        return "RightListByAccountIdParams{" +
                "userAccountId=" + userAccountId +
                ", packageId=" + packageId +
                ", rightOwner='" + rightOwner + '\'' +
                '}';
    }
}
