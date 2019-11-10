package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * @author Ivanov Roman
 * <p>
 * Класс-структура ответа удаления всех прав пользователя по USERACCOUNTID
 */
public class UserRemoveAllRightsByUserAccountIdResult {

    private Long userAccountId;

    public UserRemoveAllRightsByUserAccountIdResult() {
    }

    public UserRemoveAllRightsByUserAccountIdResult(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    /**
     * Получить уникальный идентификатор владельца(пользователя) аккаунта
     *
     * @return уникальный идентификатор владельца(пользователя) аккаунта
     */
    @NotNull
    @JsonProperty("USERACCOUNTID")
    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    @Override
    public String toString() {
        return "UserRemoveAllRightsByUserAccountIdResult{" +
                "userAccountId=" + userAccountId +
                '}';
    }
}

