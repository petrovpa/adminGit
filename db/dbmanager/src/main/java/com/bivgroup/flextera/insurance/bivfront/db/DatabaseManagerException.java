package com.bivgroup.flextera.insurance.bivfront.db;

public class DatabaseManagerException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2841584067314218786L;

    public DatabaseManagerException() {
        super();
    }

    public DatabaseManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseManagerException(String message) {
        super(message);
    }

    public DatabaseManagerException(Throwable cause) {
        super(cause);
    }
}
