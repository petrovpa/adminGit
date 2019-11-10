/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

/**
 *
 * @author reson
 */
public class ValidationException extends Exception {

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of <code>ValidationException</code> without detail message.
     */
    public ValidationException() {
    }

    /**
     * Constructs an instance of <code>ValidationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ValidationException(String msg) {
        super(msg);
    }
}
