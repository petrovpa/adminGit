package com.bivgroup.rest.admrestws.pojo.request.params.rights;

import com.bivgroup.rest.admrestws.pojo.common.StringDateDeserializerToDouble;
import com.bivgroup.rest.admrestws.pojo.request.base.Filter;
import com.bivgroup.rest.admrestws.pojo.response.result.FilterValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddProfileRightToObject {
    private String rightOwner;
    private Long rightId;
    private Long objectId;
    private Double startDate;
    private Double endDate;
    private String rightType;
    private List<Filter> filters;


    public AddProfileRightToObject() {
        this.filters = new ArrayList<>();
    }

    public AddProfileRightToObject(AddAnyRightsParams params) {
        this();
        this.rightId = params.getRightId();
        this.rightOwner = params.getRightOwner();
        this.objectId = params.getObjectId();
        this.startDate = params.getStartDate();
        this.endDate = params.getEndDate();
        this.rightType = params.getRightType();
        List<FilterValue> filterValues = params.getFilterValues();
        Filter filter = new Filter();
        filter.setSysname(params.getFilterSysname());
        if (filterValues != null && !filterValues.isEmpty()) {
            String values = filterValues.stream().map(FilterValue::getValue).collect(Collectors.joining(","));
            String keys = filterValues.stream().map(FilterValue::getvKey).collect(Collectors.joining(","));
            filter.setValues(values);
            filter.setKeys(keys);
        }
        this.filters.add(filter);
    }

    public AddProfileRightToObject(String rightOwner, Long rightId, Long objectId, Double startDate,
                                   Double endDate, String rightType, List<Filter> filters) {
        this();
        this.rightOwner = rightOwner;
        this.rightId = rightId;
        this.objectId = objectId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rightType = rightType;
        this.filters = filters;
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
    @JsonProperty("FILTERS")
    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
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
        return "AddProfileRightToObject{" +
                "rightOwner='" + rightOwner + '\'' +
                ", rightId=" + rightId +
                ", objectId=" + objectId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", rightType='" + rightType + '\'' +
                '}';
    }
}
