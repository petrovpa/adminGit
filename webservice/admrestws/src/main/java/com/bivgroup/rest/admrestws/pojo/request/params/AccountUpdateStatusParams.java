package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Ivanov Roman
 * <p>
 * Класс-структара хранения параметров обновления статуса аккаунта.
 **/
public class AccountUpdateStatusParams {

    private Long accountId;
    private Long userId;
    private String status;
    private Long isConcurrent;

    public AccountUpdateStatusParams() {
        this.isConcurrent = 1L;
    }

    public AccountUpdateStatusParams(Long accountId, Long userId, String status, Long isConcurrent) {
        this.accountId = accountId;
        this.userId = userId;
        this.status = status;
        if (isConcurrent != null) {
            this.isConcurrent = isConcurrent;
        }

    }

    /**
     * Получить идентификатор аккаунта
     *
     * @return идентификатор аккаунта
     */
    @NotNull(message = "Не передан обязательный параметр: идентификатор аккаунта")
    @JsonProperty("USERACCOUNTID")
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    /**
     * Получить идентификатор пользователя
     *
     * @return идентификатор пользователя
     */
    @NotNull(message = "Не передан обязательный параметр: идентификатор пользователя")
    @JsonProperty("USERID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Получить имя статуса состояния аккаунта (список статусов шире = ACTIVE|BLOCKED|ARCHIVE|DELETED)
     *
     * @return статус состояния аккаунаа
     */
    @NotNull(message = "Не передан обязательный параметр: статус")
    @Pattern(regexp = "ACTIVE|BLOCKED", message = "Статус не поддерживается")
    @JsonProperty("STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Получить тип выполнения запросов к базе (привилегия,конкурентоспособный)
     *
     * @return (1 или 0)
     */
    @JsonProperty("ISCONCURRENT")
    public Long getIsConcurrent() {
        return isConcurrent;
    }

    public void setIsConcurrent(Long isConcurrent) {
        if ((isConcurrent != null) && (isConcurrent.equals(0L))) {
            this.isConcurrent = isConcurrent;
        }
    }

    @Override
    public String toString() {
        return "AccountUpdateStatusParams{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", isConcurrent=" + isConcurrent +
                '}';
    }
}
