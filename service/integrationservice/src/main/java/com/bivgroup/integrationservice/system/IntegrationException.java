package com.bivgroup.integrationservice.system;

public class IntegrationException extends Exception {
    private String russianMessage = null;

    /**
     * Creates a new instance of <code>PAException</code> without detail
     * message.
     */
    public IntegrationException() {
    }

    public IntegrationException(String russianMessage, Throwable cause) {
        super(cause);
        this.russianMessage = russianMessage;
    }

    public IntegrationException(String russianMessage, String message, Throwable cause) {
        super(message, cause);
        this.russianMessage = russianMessage;
    }

    /**
     * Constructs an instance of <code>PAException</code> with the
     * specified detail message.
     *
     * @param russianMessage the detail russian message
     * @param message        the detail message.
     */
    public IntegrationException(String russianMessage, String message) {
        super(message);
        this.russianMessage = russianMessage;
    }

    /**
     * @return the russianMessage
     */
    public String getRussianMessage() {
        return russianMessage;
    }
}
