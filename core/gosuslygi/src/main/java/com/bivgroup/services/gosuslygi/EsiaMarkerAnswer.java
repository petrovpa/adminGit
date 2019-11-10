package com.bivgroup.services.gosuslygi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Класс сериализатор JSON to Java-class для idToken
 *
 * @author abooldin
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EsiaMarkerAnswer {

    /**
     * Маркер идентификации
     */
    private String idToken;
    /**
     * Авторизационный код
     */
    private String accessToken;
    /**
     * Время, в течение которого истекает срок действия маркера (в секундах)
     */
    private String expiresIn;
    /**
     * Набор случайных символов, имеющий вид 128-битного идентификатора запроса,
     * генерируется по стандарту UUID (совпадает с идентификатором запроса)
     */
    private String state;
    /**
     * Тип предоставленного маркера, в настоящее время ЕСИА поддерживает
     * только значение “Bearer”
     */
    private String tokenType;


    public EsiaMarkerAnswer() {
    }

    public EsiaMarkerAnswer(String idToken, String accessToken, String expiresIn, String state, String tokenType) {
        this.idToken = idToken;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.state = state;
        this.tokenType = tokenType;
    }

    public String getIdToken() {
        return idToken;
    }

    @JsonProperty("id_token")
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    @JsonProperty("expires_in")
    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    public String getTokenType() {
        return tokenType;
    }

    @JsonProperty("token_type")
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    @Override
    public String toString() {
        return "EsiaMarkerAnswer{" +
                "idToken='" + idToken + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", expiresIn='" + expiresIn + '\'' +
                ", state='" + state + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}
