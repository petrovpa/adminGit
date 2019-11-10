package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 *
 * @author Ivanov Roman
 *
 * Класс-структура парамтры для запроса удаления всех прав пользователя
 */
public class UserRemoveAllRightsByUserAccountIdParams {

    private Long userAccountId;

    public UserRemoveAllRightsByUserAccountIdParams() {

    }

    public UserRemoveAllRightsByUserAccountIdParams(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    /**
     * Получить уникальный идентификатор учетной записи
     *
     * @return Уникальный идентификатор учетной записи
     */
    @NotNull(message = "Не передан обязательный параметр: идентификатор аккаунта пользователя")
    @JsonProperty("USERACCOUNTID")
    public Long getUserAccountId() {
        return userAccountId;
    }

    /**
     * Установить уникальный идентификатор учетной записи
     *
     * @param userAccountId Уникальный идентификатор учетной записи
     */
    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    @Override
    public String toString() {
        return "UserRemoveAllRightsByUserAccountIdParams{" +
                "userAccountId=" + userAccountId +
                '}';
    }
}
