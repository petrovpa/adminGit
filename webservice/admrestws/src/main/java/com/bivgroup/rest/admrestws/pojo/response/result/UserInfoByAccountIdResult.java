package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Класс результата ответа на запрос получения пользователя по идентификатору аккаунта
 */
public class UserInfoByAccountIdResult {
    private String login;
    private String status;
    private Long employeeId;
    private String lastName;
    private String firstName;
    private String middleName;
    private String phone1;
    private String phone2;
    private String email;
    private Long departmentId;
    private Long userId;
    private String activeDirectoryLogin;
    private String userPrincipalNameInActiveDirectory;
    private String userType;
    private Long isConcurrent;
    private String authMethod;

    public UserInfoByAccountIdResult() {

    }

    public UserInfoByAccountIdResult(String login, String status, Long employeeId, String lastName, String firstName,
                                     String middleName, String phone1, String phone2, String email, Long departmentId,
                                     Long userId, String activeDirectoryLogin, String userPrincipalNameInActiveDirectory,
                                     String userType, Long isConcurrent) {
        this.login = login;
        this.status = status;
        this.employeeId = employeeId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.email = email;
        this.departmentId = departmentId;
        this.userId = userId;
        this.activeDirectoryLogin = activeDirectoryLogin;
        this.userPrincipalNameInActiveDirectory = userPrincipalNameInActiveDirectory;
        this.userType = userType;
        this.isConcurrent = isConcurrent;
    }

    /**
     * Логин пользователя
     *
     * @return логин пользователя,
     */
    @JsonGetter("LOGIN")
    public String getLogin() {
        return login;
    }

    @JsonSetter("USERLOGIN")
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Статус учетной записи. Возможные значения: ACTIVE, BLOCKED, ARCHIVE
     *
     * @return статус учетной записи
     */
    @JsonGetter("STATUS")
    public String getStatus() {
        return status;
    }

    @JsonSetter("ACCSTATUS")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Уникальный идентификатор сотрудника
     *
     * @return yникальный идентификатор сотрудника
     */
    @JsonProperty("EMPLOYEEID")
    public Long getEmployeeId() {
        return employeeId;
    }

    @JsonSetter("EMPLOYEEID")
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * Фамилия пользователя
     *
     * @return фамилия пользователя
     */
    @JsonGetter("LASTNAME")
    public String getLastName() {
        return lastName;
    }

    @JsonSetter("LASTNAME")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Имя пользователя
     *
     * @return имя пользователя
     */
    @JsonGetter("FIRSTNAME")
    public String getFirstName() {
        return firstName;
    }

    @JsonSetter("FIRSTNAME")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Отчество пользователя
     *
     * @return отчество пользователя
     */
    @JsonGetter("MIDDLENAME")
    public String getMiddleName() {
        return middleName;
    }

    @JsonSetter("MIDDLENAME")
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Первый номер телефона
     *
     * @return первый номер телефона
     */
    @JsonGetter("PHONE1")
    public String getPhone1() {
        return phone1;
    }

    @JsonSetter("EMPPHONE1")
    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    /**
     * Второй номер телефона
     *
     * @return второй номер телефона
     */
    @JsonGetter("PHONE2")
    public String getPhone2() {
        return phone2;
    }

    @JsonSetter("EMPPHONE2")
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    /**
     * Электронная почта пользователя
     *
     * @return электронная почта пользователя
     */
    @JsonGetter("EMAIL")
    public String getEmail() {
        return email;
    }

    @JsonSetter("EMPEMAIL")
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Идентификатор департамента пользователя
     *
     * @return идентификатор департамента пользователя
     */
    @JsonGetter("DEPARTMENTID")
    public Long getDepartmentId() {
        return departmentId;
    }

    @JsonSetter("USERDEPTID")
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

    @JsonProperty("ISCONCURRENT")
    public Long getIsConcurrent() {
        return isConcurrent;
    }

    public void setIsConcurrent(Long isConcurrent) {
        this.isConcurrent = isConcurrent;
    }

    /**
     * Тип пользователя. Подробности в таблице: REF_REFITEM
     *
     * @return тип пользователя
     */
    @JsonProperty("USERTYPE")
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @JsonProperty("AUTHMETHOD")
    public String getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    @Override
    public String toString() {
        return "UserInfoByAccountIdResult{" +
                "login='" + login + '\'' +
                ", status='" + status + '\'' +
                ", employeeId=" + employeeId +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", phone1='" + phone1 + '\'' +
                ", phone2='" + phone2 + '\'' +
                ", email='" + email + '\'' +
                ", departmentId=" + departmentId +
                ", userId=" + userId +
                ", activeDirectoryLogin='" + activeDirectoryLogin + '\'' +
                ", userPrincipalNameInActiveDirectory='" + userPrincipalNameInActiveDirectory + '\'' +
                ", userType=" + userType +
                ", isConcurrent=" + isConcurrent +
                ", authMethod=" + authMethod +
                '}';
    }
}
