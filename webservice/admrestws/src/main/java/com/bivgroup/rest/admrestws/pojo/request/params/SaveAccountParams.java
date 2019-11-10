package com.bivgroup.rest.admrestws.pojo.request.params;

import com.bivgroup.rest.admrestws.pojo.common.StringDateDeserializerToDouble;
import com.bivgroup.rest.admrestws.validation.annotation.NotNullIfDependentNotNull;
import com.bivgroup.rest.admrestws.validation.annotation.NotNullIfDependentNull;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

import static com.bivgroup.rest.common.Constants.USER_ACCOUNT_ID_PARAM_NAME;
@JsonFilter("ignorableFilter")
@NotNullIfDependentNotNull.List({
        @NotNullIfDependentNotNull(message = "Отсутствует тип объекта", fieldName = "objectType", dependentFieldName = USER_ACCOUNT_ID_PARAM_NAME),
        @NotNullIfDependentNotNull(message = "Отсутствует идентификато пользователя", fieldName = "userId", dependentFieldName = USER_ACCOUNT_ID_PARAM_NAME),
        @NotNullIfDependentNotNull(message = "Отсутствует статус пользователя", fieldName = "status", dependentFieldName = USER_ACCOUNT_ID_PARAM_NAME)
})
@NotNullIfDependentNull.List({
        @NotNullIfDependentNull(
                message = "Отсутствует пароль пользователя", fieldName = "password", dependentFieldName = USER_ACCOUNT_ID_PARAM_NAME
        ),
        @NotNullIfDependentNull(
                message = "Отсутствует повторный пароль пользователя", fieldName = "retPassword", dependentFieldName = USER_ACCOUNT_ID_PARAM_NAME
        ),
        @NotNullIfDependentNull(
                message = "Отсутствует идентификатор подразделения", fieldName = "departmentId", dependentFieldName = USER_ACCOUNT_ID_PARAM_NAME
        )
})
public class SaveAccountParams {
    private Long userAccountId;
    private String login;
    private String newLogin;
    private String password;
    private String retPassword;
    private String activeDirectoryLogin;
    private String userPrincipalNameInActiveDirectory;
    private String userType;
    private Long objectType;
    private Long departmentId;
    private Long userId;
    private Long employeeId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String participantName;
    private String participantExtName;
    private String participantExtId;
    private String status;
    private Long partEmployeeId;
    private String phone1;
    private String email;
    private String roles;
    private String groups;
    private Boolean blocked;
    private Double pwdExpDate;
    private Long participantId;
    private Long isConcurrent;
    private String authMethod;

    public SaveAccountParams() {
    }

    public SaveAccountParams(Long userAccountId, String login, String newLogin, String password, String retPassword,
                             String activeDirectoryLogin, String userPrincipalNameInActiveDirectory, String userType,
                             Long objectType, Long departmentId, Long userId, Long employeeId, String firstName,
                             String middleName, String lastName, String participantName, String participantExtName,
                             String participantExtId, String status, Long partEmployeeId, String phone1, String email, String roles,
                             String groups, Boolean blocked, Double pwdExpDate, Long participantId, Long isConcurrent) {
        this.userAccountId = userAccountId;
        this.login = login;
        this.newLogin = newLogin;
        this.password = password;
        this.retPassword = retPassword;
        this.activeDirectoryLogin = activeDirectoryLogin;
        this.userPrincipalNameInActiveDirectory = userPrincipalNameInActiveDirectory;
        this.userType = userType;
        this.objectType = objectType;
        this.departmentId = departmentId;
        this.userId = userId;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.participantName = participantName;
        this.participantExtName = participantExtName;
        this.participantExtId = participantExtId;
        this.status = status;
        this.partEmployeeId = partEmployeeId;
        this.phone1 = phone1;
        this.email = email;
        this.roles = roles;
        this.groups = groups;
        this.blocked = blocked;
        this.pwdExpDate = pwdExpDate;
        this.participantId = participantId;
        this.isConcurrent = isConcurrent;
    }

    @JsonProperty("USERACCOUNTID")
    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    @JsonProperty("USERTYPE")
    @NotNull(message = "Не передан обязательный параметр: тип пользователя")
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @JsonProperty("DEPARTMENTID")
    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
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

    @JsonProperty("FIRSTNAME")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("MIDDLENAME")
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @JsonProperty("LASTNAME")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("PARTICIPANTNAME")
    public String getParticipantName() {
        return ((participantName == null || participantName.isEmpty()) ? String.format("%s %s %s", this.lastName, this.firstName, this.middleName).trim() : participantName);
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    @JsonProperty("PARTICIPANTEXTNAME")
    public String getParticipantExtName() {
        return ((participantExtName == null || participantExtName.isEmpty()) ? String.format("%s %s %s", this.lastName, this.firstName, this.middleName).trim() : participantExtName);
    }

    public void setParticipantExtName(String participantExtName) {
        this.participantExtName = participantExtName;
    }

    @JsonProperty("PARTICIPANTEXTID")
    public String getParticipantExtId() {
        return participantExtId;
    }

    public void setParticipantExtId(String participantExtId) {
        this.participantExtId = participantExtId;
    }

    @JsonProperty("PARTEMPLOYEEID")
    public Long getPartEmployeeId() {
        return partEmployeeId;
    }

    public void setPartEmployeeId(Long partEmployeeId) {
        this.partEmployeeId = partEmployeeId;
    }

    @JsonProperty("LOGIN")
    @NotNull(message = "Не передан обязательный параметр: логин пользователя")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @JsonProperty("PHONE1")
    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    @JsonProperty("EMAIL")
    @Email(message = "Некорректный адрес электронной почты")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("ROLES")
    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @JsonProperty("GROUPS")
    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    @JsonProperty("BLOCKED")
    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    @JsonProperty("STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("PWDEXPDATE")
    @JsonDeserialize(using = StringDateDeserializerToDouble.class)
    public Double getPwdExpDate() {
        return pwdExpDate;
    }

    public void setPwdExpDate(Double pwdExpDate) {
        this.pwdExpDate = pwdExpDate;
    }

    @JsonProperty("PASSWORD")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("RETPASSWORD")
    public String getRetPassword() {
        return retPassword;
    }

    public void setRetPassword(String retPassword) {
        this.retPassword = retPassword;
    }

    @JsonProperty("ADUSERLOGIN")
    public String getActiveDirectoryLogin() {
        return activeDirectoryLogin;
    }

    public void setActiveDirectoryLogin(String activeDirectoryLogin) {
        this.activeDirectoryLogin = activeDirectoryLogin;
    }

    @JsonProperty("ADUSERPRINCIPALNAME")
    public String getUserPrincipalNameInActiveDirectory() {
        return userPrincipalNameInActiveDirectory;
    }

    public void setUserPrincipalNameInActiveDirectory(String userPrincipalNameInActiveDirectory) {
        this.userPrincipalNameInActiveDirectory = userPrincipalNameInActiveDirectory;
    }

    @JsonProperty("OBJECTTYPE")
    public Long getObjectType() {
        return objectType;
    }

    public void setObjectType(Long objectType) {
        this.objectType = objectType;
    }

    @JsonProperty("NEWLOGIN")
    public String getNewLogin() {
        return newLogin;
    }

    public void setNewLogin(String newLogin) {
        this.newLogin = newLogin;
    }

    @JsonProperty("PARTICIPANTID")
    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    @JsonProperty("ISCONCURRENT")
    public Long getIsConcurrent() {
        return this.isConcurrent;
    }

    public void setIsConcurrent(Long isConcurrent) {
        this.isConcurrent = isConcurrent;
    }

    @JsonProperty("AUTHMETHOD")
    public String getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    //<editor-fold defaultstate="collapsed" desc="Место для константныйх геттерова. Передавать это с интерфейса пока что нельзя!!!">
    @JsonGetter("BLOCKIFINACTIVE")
    public Integer getBlockIfInActive() {
        return 0;
    }

    @JsonGetter("LOCALE")
    public String getLocale() {
        return "ru";
    }

    @JsonGetter("DEFAULT_PROJECT_SYSNAME")
    public String getProjectName() {
        return "insurance";
    }

    @JsonGetter("TZTYPE")
    public Long getTimeZoneType() {
        return 0L;
    }

    @JsonGetter("TZNAME")
    public String getTimeZoneName() {
        return "Системное значение";
    }

    @JsonGetter("NOTINLIST")
    public Boolean getNotInList() {
        return true;
    }

    @JsonGetter("PARTICIPANTNOTINLIST")
    public Boolean getParticipantNotInList() {
        return true;
    }
    //</editor-fold>


    @Override
    public String toString() {
        return "SaveAccountParams{" +
                "userAccountId=" + userAccountId +
                ", login='" + login + '\'' +
                ((newLogin != null && !newLogin.isEmpty()) ? (", newLogin='" + newLogin + '\'') : "") +
                ", activeDirectoryLogin='" + activeDirectoryLogin + '\'' +
                ", userPrincipalNameInActiveDirectory='" + userPrincipalNameInActiveDirectory + '\'' +
                ", userType='" + userType + '\'' +
                ", objectType=" + objectType +
                ", departmentId=" + departmentId +
                ", userId=" + userId +
                ", employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", participantName='" + participantName + '\'' +
                ", participantExtName='" + participantExtName + '\'' +
                ", participantExtId='" + participantExtId + '\'' +
                ", partEmployeeId=" + partEmployeeId +
                ", phone1='" + phone1 + '\'' +
                ", email='" + email + '\'' +
                ", roles='" + roles + '\'' +
                ", groups='" + groups + '\'' +
                ", blocked=" + blocked +
                ", pwdExpDate=" + pwdExpDate +
                ", isConcurrent=" + isConcurrent +
                ", authMethod=" + authMethod +
                '}';
    }
}
