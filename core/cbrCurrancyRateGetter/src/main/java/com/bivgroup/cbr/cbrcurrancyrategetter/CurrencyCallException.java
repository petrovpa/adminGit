/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.cbr.cbrcurrancyrategetter;

/**
 *
 * @author reson
 */
public class CurrencyCallException extends Exception {

    /**
     * Creates a new instance of
     * <code>CurrencyCallException</code> without detail message.
     */
    public CurrencyCallException() {
    }

    /**
     * Constructs an instance of
     * <code>CurrencyCallException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CurrencyCallException(String msg) {
        super(msg);
    }

    public CurrencyCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyCallException(Throwable cause) {
        super(cause);
    }
    
}
