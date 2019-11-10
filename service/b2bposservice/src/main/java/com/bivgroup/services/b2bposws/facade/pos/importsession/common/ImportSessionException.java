package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

public class ImportSessionException extends Exception {

    // todo: контекст возникновения исключения (мапы результатов, параметры вызовов и пр.)

    private String messageHumanized;

    public ImportSessionException(String messageHumanized, String message) {
        super(message);
        this.messageHumanized = messageHumanized;
    }

    public ImportSessionException(String messageHumanized, String message, Throwable cause) {
        super(message, cause);
        this.messageHumanized = messageHumanized;
    }

    public void addMessages(String messageHumanized, String message) {
        this.messageHumanized = this.messageHumanized + " " + messageHumanized;
    }

    public String getMessageHumanized() {
        return messageHumanized;
    }
}
