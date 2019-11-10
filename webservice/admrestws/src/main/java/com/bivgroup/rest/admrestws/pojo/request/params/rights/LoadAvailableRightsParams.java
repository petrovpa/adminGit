package com.bivgroup.rest.admrestws.pojo.request.params.rights;

import com.bivgroup.rest.common.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.bivgroup.config.Config.getConfig;
import static com.bivgroup.utils.ParamGetter.getLongParam;

public class LoadAvailableRightsParams {
    private Long objectId;
    private String rightOwner;
    private Long packageId;
    private String rightType;

    public LoadAvailableRightsParams() {
        this.packageId = getLongParam(getConfig(Constants.THIS_SERVICE_NAME)
                .getParam("DEFAULT_PACKAGE", "1001"));
    }

    public LoadAvailableRightsParams(Long objectId, String rightOwner, Long packageId, String rightType) {
        this.objectId = objectId;
        this.rightOwner = rightOwner;
        this.packageId = packageId;
        this.rightType = rightType;
    }

    /**
     * Идентификатор аккаунта пользователя, для которого требуется получить список прав
     *
     * @return идентификатор аккаунта пользователя
     */
    @JsonProperty("OBJECTID")
    @NotNull(message = "Не передан обязательный параметр: идентификатор объекта")
    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    /**
     * Владелец права
     *
     * @return системное имя владельца права
     */
    @JsonProperty("RIGHTOWNER")
    @NotNull(message = "Не передан обязательный параметр: тип владельца права")
    @Pattern(regexp = "ROLE|USERGROUP|ACCOUNT|DEPARTMENT", message = "Не правильное значение параметра: тип владельца права")
    public String getRightOwner() {
        return rightOwner;
    }

    public void setRightOwner(String rightOwner) {
        this.rightOwner = rightOwner;
    }

    @JsonProperty("PACKAGEID")
    @NotNull(message = "Не передан обязательный параметр: идентификатор модуля")
    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    @JsonProperty("RIGHTTYPE")
    @NotNull(message = "Не передан обязательный параметр: тип права")
    @Pattern(regexp = "rights|profileRights", message = "Не правильное значение параметра: тип права")
    public String getRightType() {
        return rightType;
    }

    public void setRightType(String rightType) {
        this.rightType = rightType;
    }

    @Override
    public String toString() {
        return "LoadAvailableRightsParams{" +
                "objectId=" + objectId +
                ", rightOwner='" + rightOwner + '\'' +
                ", packageId=" + packageId +
                ", rightType='" + rightType + '\'' +
                '}';
    }
}
