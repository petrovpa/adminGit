package com.bivgroup.flextera.insurance.bivfront.db;

import liquibase.database.Database;
import liquibase.exception.CustomPreconditionErrorException;
import liquibase.exception.CustomPreconditionFailedException;
import liquibase.precondition.CustomPrecondition;

public class PropertyDefinedPrecondition implements CustomPrecondition {

    private String propertyValue = null;
    private String propertyName = null;
    private String failureType = "fail";

    public PropertyDefinedPrecondition() {
    }

    public void check(Database arg0) throws CustomPreconditionFailedException, CustomPreconditionErrorException {
        if (propertyValue == null || "".equals(propertyValue.trim())
                || ("${" + propertyName + "}").equals(propertyValue.trim())) {

            String message = "Required property '" + propertyName + "' is not defined";
            if ("error".equals(failureType)) {
                throw new CustomPreconditionErrorException(message, new RuntimeException(message));
            } else {
                throw new CustomPreconditionFailedException(message);
            }
        }
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getFailureType() {
        return failureType;
    }

    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }
}
