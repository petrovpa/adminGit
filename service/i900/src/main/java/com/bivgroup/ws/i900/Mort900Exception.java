/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.i900;

/**
 *
 * @author reson
 */
public class Mort900Exception extends Exception {
    
    private String russianMessage = null;
    
    /**
     * Creates a new instance of <code>Mort900Exception</code> without detail
     * message.
     */
    public Mort900Exception() {
    }

    public Mort900Exception(String russianMessage, Throwable cause) {
        super(cause);
        this.russianMessage = russianMessage;
    }

    public Mort900Exception(String russianMessage, String message, Throwable cause) {        
        super(message, cause);
        this.russianMessage = russianMessage;
    }

    /**
     * Constructs an instance of <code>Mort900Exception</code> with the
     * specified detail message.
     *
     * @param russianMessage the detail russian message
     * @param msg the detail message.
     */
    public Mort900Exception(String russianMessage, String msg) {
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
