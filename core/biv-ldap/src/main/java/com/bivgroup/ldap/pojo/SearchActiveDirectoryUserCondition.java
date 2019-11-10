package com.bivgroup.ldap.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Класс для передачи значений параметров фильтрации #{@link ActiveDirectoryUserInfo}
 */
public class SearchActiveDirectoryUserCondition {
    private String name;
    private String userPrincipalName;
    private String activeDirectoryLogin;
    private String givenName;
    private String surname;

    /**
     * Конструктор по умолчанию
     */
    public SearchActiveDirectoryUserCondition() {

    }

    public SearchActiveDirectoryUserCondition(String name, String userPrincipalName, String activeDirectoryLogin, String givenName, String surname) {
        this.name = name;
        this.userPrincipalName = userPrincipalName;
        this.activeDirectoryLogin = activeDirectoryLogin;
        this.givenName = givenName;
        this.surname = surname;
    }

    @JsonProperty("FULLNAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    /**
     * Метод создания фильтра для поиска пользователя в ldap.
     * Поиск осуществляется через оператор AND.
     * Атрибуты будут добавлены к поиску, только если имеют значение.
     *
     * @param base база поиска
     * @return строку вида (&(objectClass=User)(objectCategory=Person)(sAMAccountName=aivashin))
     */
    public String createCondition(String base) {
        if (base == null || base.isEmpty()) {
            throw new IllegalArgumentException("Base search can not be null or empty");
        }
        StringBuilder result = new StringBuilder("(&");
        result.append(base);
        if (this.name != null && !this.name.isEmpty()) {
            result.append("(name=").append(this.name).append(")");
        }
        if (this.userPrincipalName != null && !this.userPrincipalName.isEmpty()) {
            result.append("(userPrincipalName=").append(this.userPrincipalName).append(")");
        }
        if (this.activeDirectoryLogin != null && !this.activeDirectoryLogin.isEmpty()) {
            result.append("(sAMAccountName=").append(this.activeDirectoryLogin).append(")");
        }
        if (this.givenName != null && !this.givenName.isEmpty()) {
            result.append("(givenName=").append(this.givenName).append(")");
        }
        if (this.surname != null && !this.surname.isEmpty()) {
            result.append("(sn=").append(surname).append(")");
        }
        result.append(")");
        return result.toString();
    }

    @Override
    public String toString() {
        return "SearchActiveDirectoryUserCondition{" +
                "name='" + name + '\'' +
                ", userPrincipalName='" + userPrincipalName + '\'' +
                ", activeDirectoryLogin='" + activeDirectoryLogin + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
