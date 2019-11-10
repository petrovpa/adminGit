package com.bivgroup.rest.admrestws.pojo.request.params.rights;

import com.bivgroup.rest.admrestws.pojo.common.StringDateDeserializerToDouble;
import com.bivgroup.rest.admrestws.pojo.response.result.FilterValue;
import com.bivgroup.rest.admrestws.validation.annotation.NotNullByDependentValue;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * Класс параметров запроса для добавления любово права
 * к сущности из списка: ROLE|USERGROUP|ACCOUNT|DEPARTMENT
 */
@JsonFilter("ignorableFilter")
@NotNullByDependentValue(
        fieldName = "filterValues",
        dependentFieldName = "rightType",
        value = "profileRights",
        message = "Отсутствует обязательный параметр: значения фильтра")
public class AddAnyRightsParams {
    private String rightOwner;
    private Long rightId;
    private String filterSysname;
    private Long objectId;
    private Double startDate;
    private Double endDate;
    private String rightType;
    private Long rightFilterId;
    private List<FilterValue> filterValues;


    public AddAnyRightsParams() {
    }

    public AddAnyRightsParams(String rightOwner, Long rightId, Long objectId, Double startDate, String filterSysname,
                              Double endDate, String rightType, Long rightFilterId, List<FilterValue> filterValues) {
        this.rightOwner = rightOwner;
        this.rightId = rightId;
        this.objectId = objectId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rightType = rightType;
        this.rightFilterId = rightFilterId;
        this.filterSysname = filterSysname;
        this.filterValues = filterValues;
    }

    /**
     * Кому назначить право.
     *
     * @return системное имя кому назначить право
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

    /**
     * Идентификатор права
     *
     * @return идентификатор права
     */
    @JsonProperty("RIGHTID")
    @NotNull(message = "Отсутствует обязательный параметр: идентификато права права")
    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }

    /**
     * Системон имя фильтра
     *
     * @return системное имя фильтра
     */
    @JsonProperty("FILTERSYSNAME")
    public String getFilterSysname() {
        return filterSysname;
    }

    public void setFilterSysname(String filterSysname) {
        this.filterSysname = filterSysname;
    }

    /**
     * Идентификатор объекта, которому назначается право
     *
     * @return идентификтаор объекта
     */
    @JsonProperty("OBJECTID")
    @NotNull(message = "Не передан обязательный параметр: идентификатор роли, к которому требуется добавить право")
    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    /**
     * Тип права
     *
     * @return тип права
     */
    @JsonProperty("RIGHTTYPE")
    @NotNull(message = "Не передан обязательный параметр: тип права")
    @Pattern(regexp = "rights|profileRights", message = "Не правильное значение параметра: тип права")
    public String getRightType() {
        return rightType;
    }

    public void setRightType(String rightType) {
        this.rightType = rightType;
    }

    /**
     * Идентификатор фильтра
     *
     * @return идентификатор фильтр
     */
    @JsonProperty("RIGHTFILTERID")
    public Long getRightFilterId() {
        return rightFilterId;
    }

    public void setRightFilterId(Long rightFilterId) {
        this.rightFilterId = rightFilterId;
    }

    /**
     * Дата начала действия права для роли
     *
     * @return дата начала действия
     */
    @JsonProperty("STARTDATE")
    @JsonDeserialize(using = StringDateDeserializerToDouble.class)
    public Double getStartDate() {
        return startDate;
    }

    public void setStartDate(Double startDate) {
        this.startDate = startDate;
    }

    /**
     * Дата окончания действия права для роли
     *
     * @return дата окончания действия
     */
    @JsonProperty("ENDDATE")
    @JsonDeserialize(using = StringDateDeserializerToDouble.class)
    public Double getEndDate() {
        return endDate;
    }

    public void setEndDate(Double endDate) {
        this.endDate = endDate;
    }

    @Valid
    @JsonProperty("FILTERVALUES")
    public List<FilterValue> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<FilterValue> filterValues) {
        this.filterValues = filterValues;
    }

    /**
     * @return
     */
    @JsonProperty("ANYVALUE")
    public boolean isAnyValue() {
        return false;
    }

    /**
     * @return
     */
    @JsonProperty("ISEXCEPTION")
    public Integer getIsException() {
        return 0;
    }

    /**
     * @return
     */
    @JsonProperty("EXCEPTIONMODE")
    public Integer getExceptionMode() {
        return 1;
    }

    /**
     * @return
     */
    @JsonProperty(value = "EXTINTEGRATION")
    public Integer getExtintegration() {
        return 1;
    }

    @Override
    public String toString() {
        return "AddAnyRightsParams{" +
                "rightOwner='" + rightOwner + '\'' +
                ", rightId=" + rightId +
                ", objectId=" + objectId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", rightType='" + rightType + '\'' +
                ", filterValues=" + filterValues +
                '}';
    }
}
