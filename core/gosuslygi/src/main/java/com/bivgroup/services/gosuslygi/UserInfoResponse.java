package com.bivgroup.services.gosuslygi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Класс сериализатор JSON to Java-class для idToken
 *
 * @author eremeevas
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoResponse {
    
    /**
     * Имя пользователя
     */
    private String firstName;
    
     /**
     * Фамилия пользователя
     */
    private String lastName;
    
     /**
     * Отчество пользователя
     */
    private String middleName;

    /**
     * Дата рождения пользователя
     */
    private String birthDate;

    /**
     * Пол
     */
    private String gender;

    /**
     * Номер СНИЛС
     */
    private String snils;

    /**
     * Номер ИНН
     */
    private String inn;

    /**
     * Информация о документ удостоверяющем личность
     */
    private String idDoc;

    /**
     * Номер мобильного телефона
     */
    private String mobileNumber;
    
    /**
     * Процесс проверки данных (true/false)
     */
    private boolean verifyingAccount;
    
    /**
     * Статус учетной записи ЕСИА(Registered – зарегистрирована/Deleted – удалена)
     */
    private String statusAccount;
    

    public UserInfoResponse() {
    }

    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getMiddleName() {
        return middleName;
    }

    @JsonProperty("middleName")
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    @JsonProperty("birthDate")
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSnils() {
        return snils;
    }

    @JsonProperty("snils")
    public void setSnils(String snils) {
        this.snils = snils;
    }

    public String getInn() {
        return inn;
    }

    @JsonProperty("inn")
    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getIdDoc() {
        return idDoc;
    }

    @JsonProperty("rIdDoc")
    public void setIdDoc(String idDoc) {
        this.idDoc = idDoc;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    @JsonProperty("mobile_number")
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    
    
    public boolean isVerifyingAccount() {
        return verifyingAccount;
    }
    
    @JsonProperty("verifying")
    public void setVerifyingAccount(boolean verifyingAccount) {
        this.verifyingAccount = verifyingAccount;
    }

    public String getStatusAccount() {
        return statusAccount;
    }

    @JsonProperty("status")
    public void setStatusAccount(String statusAccount) {
        this.statusAccount = statusAccount;
    }

}
