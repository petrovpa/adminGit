package com.bivgroup.ldap.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ActiveDirectoryUserInfo {
    /**
     * Полное имя пользователя
     */
    private String fullName;
    /**
     * Логин для авторизации пользователя
     */
    private String userPrincipalName;
    /**
     * Логин пользователя
     */
    private String activeDirectoryLogin;
    /**
     * Имя пользователя
     */
    private String givenName;
    /**
     * Фамилия пользователя
     */
    private String surname;
    /**
     * Заблокирован ли пользователь
     */
    private boolean isBlocked;
    /**
     * Доступен ли пользователю вход с его группами
     */
    private boolean accessUserIsAvailable;
    /**
     * Состоит ли пользователь в запрещенной для входа группе
     */
    private boolean isInForbiddenGroup;
    /**
     * Имя запрещенной группы (узла дерева)
     */
    private String forbiddenGroupName;
    /**
     * Группа в которую входит пользователь
     */
    private List<String> userGroup;

    public ActiveDirectoryUserInfo() {
        this.fullName = "";
        this.userPrincipalName = "";
        this.activeDirectoryLogin = "";
        this.givenName = "";
        this.surname = "";
        this.userGroup = new ArrayList<>();
        this.isBlocked = false;
        this.accessUserIsAvailable = true;
        this.isInForbiddenGroup = false;
    }

    @JsonProperty("FULLNAME")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @JsonProperty("ADUSERPRINCIPALNAME")
    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    @JsonProperty("ADUSERLOGIN")
    public String getActiveDirectoryLogin() {
        return activeDirectoryLogin;
    }

    public void setActiveDirectoryLogin(String activeDirectoryLogin) {
        this.activeDirectoryLogin = activeDirectoryLogin;
    }

    @JsonProperty("FIRSTNAME")
    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @JsonProperty("LASTNAME")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @JsonProperty("ISBLOCKED")
    public boolean isBlocked() {
        return isBlocked;
    }

    public void calculateIsBlocked(String code) {
        switch (code) {
            case "2":
                // ACCOUNTDISABLE 2
            case "514":
                // Disabled Account	514
            case "546":
                // Disabled, Password Not Required 546
            case "262658":
                // Disabled, Smartcard Required 262658
            case "262690":
                // Disabled, Smartcard Required, Password Not Required 262690
            case "328194":
                // Disabled, Smartcard Required, Password Doesn’t Expire 328194
            case "328226":
                // Disabled, Smartcard Required, Password Doesn’t Expire & Not Required 328226
            case "66050":
                // Disabled, Password Doesn’t Expire 66050
            case "66082":
                // Disabled, Password Doesn’t Expire & Not Required 66082
                this.isBlocked = true;
                break;
            default:
                this.isBlocked = false;
        }
    }

    @JsonProperty("ACCESSUSERISAVAILABLE")
    public boolean isAccessUserIsAvailable() {
        return accessUserIsAvailable;
    }

    public void setAccessUserIsAvailable(boolean accessUserIsAvailable) {
        this.accessUserIsAvailable = accessUserIsAvailable;
    }

    @JsonProperty("USERGROUP")
    public List<String> getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(List<String> userGroup) {
        this.userGroup = userGroup;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @JsonProperty("INFORBIDDENGROUP")
    public boolean isInForbiddenGroup() {
        return isInForbiddenGroup;
    }

    public void setInForbiddenGroup(boolean inForbiddenGroup) {
        isInForbiddenGroup = inForbiddenGroup;
    }

    @JsonProperty("FORBIDDENGROUPNAME")
    public String getForbiddenGroupName() {
        return forbiddenGroupName;
    }

    public void setForbiddenGroupName(String forbiddenGroupName) {
        this.forbiddenGroupName = forbiddenGroupName;
    }

    @Override
    public String toString() {
        return "ActiveDirectoryUserInfo{" +
                "fullName='" + fullName + '\'' +
                ", userPrincipalName='" + userPrincipalName + '\'' +
                ", activeDirectoryLogin='" + activeDirectoryLogin + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surname='" + surname + '\'' +
                ", isBlocked=" + isBlocked +
                ", accessUserIsAvailable=" + accessUserIsAvailable +
                ", isInForbiddenGroup=" + isInForbiddenGroup +
                ", forbiddenGroupName='" + forbiddenGroupName + '\'' +
                ", userGroup=" + userGroup +
                '}';
    }
}
