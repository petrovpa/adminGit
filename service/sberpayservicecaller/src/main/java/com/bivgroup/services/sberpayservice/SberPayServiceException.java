/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.sberpayservice;

/**
 *
 * @author mvolkov
 */
public class SberPayServiceException extends Exception {

    public SberPayServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SberPayServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of <code>SberPayServiceException</code> without
     * detail message.
     */
    public SberPayServiceException() {
    }

    /**
     * Constructs an instance of <code>SberPayServiceException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public SberPayServiceException(String msg) {
        super(msg);
    }
}
