package com.bivgroup.rest.admrestws.pojo.request.params;

import com.bivgroup.rest.admrestws.pojo.request.params.base.ReturnAsHashMap;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Класс параметров запроса получения пользователя по идентификатору аккаунта
 */
public class UserInfoByAccountIdParams implements ReturnAsHashMap {
    private Long userAccountId;

    public UserInfoByAccountIdParams() {

    }

    public UserInfoByAccountIdParams(Long userAccountId) {
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
        return "UserInfoByAccountIdParams{" +
                "userAccountId=" + userAccountId +
                '}';
    }
}
