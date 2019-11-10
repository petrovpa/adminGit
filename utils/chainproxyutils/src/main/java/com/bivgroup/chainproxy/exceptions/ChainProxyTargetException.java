package com.bivgroup.chainproxy.exceptions;

import com.bivgroup.chainproxy.executertype.TargetExecutor;

/**
 * @author Ivanov Roman
 *
 * Исключение, которое может возникнуть внутри исполнителя {@link TargetExecutor}
 */
public class ChainProxyTargetException extends Exception {

    public ChainProxyTargetException(String message) {
        super(message);
    }

}
