package com.bivgroup.chainproxy.exceptions;

/**
 * @author Ivanov Roman
 *
 * Исключения возникающие при ошибках с менеджерами исполнителей
 */
public class ChainProxyManagerException extends Exception{

    public ChainProxyManagerException(String message) {
        super(message);
    }
}
