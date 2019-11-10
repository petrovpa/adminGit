package com.bivgroup.core.audit;

public class AuditParameters {
    /**
     * наименование операция
     */
    private String operation;

    /**
     * результат операция #{@link ResultOperation}
     */
    private ResultOperation resultStatus;

    /**
     * логин пользователя, вызвавщего события
     */
    private String login;

    /**
     * идентификатор аккаунта пользователя, вызвавщего событие
     */
    private Long userAccountId;

    /**
     * сообщения, которое требуется сохранить
     */
    private String message;

    /**
     * идентификатор входного документа, по которому проводим событие
     */
    private Long inputDocumentId;

    /**
     * идентификатор выходного документа, по которому проводим событие
     */
    private Long outputDocumentId;

    /**
     * информация об IP адресах, с которых происходит запрос
     */
    private AuditIpInfo ipInfo;

    /**
     * Конструктор по-умолчанию. Результат операции проставляется
     * как "не успешный", таким образом при использовании требуется проставить
     * значение результата операции "успешно", если операции выполнена успешно
     */
    public AuditParameters() {
        resultStatus = ResultOperation.FALTURE;
    }

    public AuditParameters(String operation, ResultOperation resultStatus, String login, Long userAccountId,
                           String message, Long documentId, Long outputDocumentId, AuditIpInfo ipInfo) {
        this.operation = operation;
        this.resultStatus = resultStatus;
        this.login = login;
        this.userAccountId = userAccountId;
        this.message = message;
        this.inputDocumentId = documentId;
        this.ipInfo = ipInfo;
        this.outputDocumentId = outputDocumentId;
    }

    public String getOperation() {
        return operation;
    }

    public AuditParameters setOperation(String operation) {
        this.operation = operation;
        return this;
    }

    public ResultOperation getResultStatus() {
        return resultStatus;
    }

    public AuditParameters setResultStatus(ResultOperation resultStatus) {
        this.resultStatus = resultStatus;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public AuditParameters setLogin(String login) {
        this.login = login;
        return this;
    }

    public Long getUserAccountId() {
        return userAccountId;
    }

    public AuditParameters setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public AuditParameters setMessage(String message) {
        this.message = message;
        return this;
    }

    public Long getInputDocumentId() {
        return inputDocumentId;
    }

    public AuditParameters setInputDocumentId(Long inputDocumentId) {
        this.inputDocumentId = inputDocumentId;
        return this;
    }

    public Long getOutputDocumentId() {
        return outputDocumentId;
    }

    public AuditParameters setOutputDocumentId(Long outputDocumentId) {
        this.outputDocumentId = outputDocumentId;
        return this;
    }

    public AuditIpInfo getIpInfo() {
        return ipInfo;
    }

    public AuditParameters setIpInfo(AuditIpInfo ipInfo) {
        this.ipInfo = ipInfo;
        return this;
    }
}
