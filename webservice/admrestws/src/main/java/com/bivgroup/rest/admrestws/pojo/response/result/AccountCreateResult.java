package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountCreateResult {
    private Long userAccountId;
    private Long userId;
    private Long employeeId;
    private Long participantId;

    public AccountCreateResult() {
    }

    public AccountCreateResult(Long userAccountId, Long userId, Long employeeId, Long participantId) {
        this.userAccountId = userAccountId;
        this.userId = userId;
        this.employeeId = employeeId;
        this.participantId = participantId;
    }

    @JsonProperty("USERACCOUNTID")
    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    @JsonProperty("USERID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @JsonProperty("EMPLOYEEID")
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @JsonProperty("PARTICIPANTID")
    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    @Override
    public String toString() {
        return "AccountCreateResult{" +
                "userAccountId=" + userAccountId +
                ", userId=" + userId +
                ", employeeId=" + employeeId +
                ", participantId=" + participantId +
                '}';
    }
}
