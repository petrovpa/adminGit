package com.bivgroup.services.b2bposws.facade.pos.print;

/**
 *
 * @author ilich
 */
public class PDFMergerException extends Exception {

    /**
     * Creates a new instance of <code>PDFMergerException</code> without detail
     * message.
     */
    public PDFMergerException() {
    }

    /**
     * Constructs an instance of <code>PDFMergerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PDFMergerException(String msg) {
        super(msg);
    }

    public PDFMergerException(String message, Throwable cause) {
        super(message, cause);
    }

    public PDFMergerException(Throwable cause) {
        super(cause);
    }
}
