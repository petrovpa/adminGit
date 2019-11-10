/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.paws.system;

/**
 *
 * @author reson
 */
public class PAException extends Exception {
    
    private String russianMessage = null;
    
    /**
     * Creates a new instance of <code>PAException</code> without detail
     * message.
     */
    public PAException() {
    }

    public PAException(String russianMessage, Throwable cause) {
        super(cause);
        this.russianMessage = russianMessage;
    }

    public PAException(String russianMessage, String message, Throwable cause) {        
        super(message, cause);
        this.russianMessage = russianMessage;
    }

    /**
     * Constructs an instance of <code>PAException</code> with the
     * specified detail message.
     *
     * @param russianMessage the detail russian message
     * @param msg the detail message.
     */
    public PAException(String russianMessage, String msg) {
        super(msg);
        this.russianMessage = russianMessage;
    }

    /**
     * @return the russianMessage
     */
    public String getRussianMessage() {
        return russianMessage;
    }

}
