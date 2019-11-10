package com.bivgroup.services.gosuslygi;


/**
 * Класс для обработки ошибок EsiaAuthentificator
 *
 * <br>
 * Методы:
 * <UL>
 *     <LI>{@link #EsiaAuthentificationException()}</LI>
 *     <LI>{@link #EsiaAuthentificationException(String)}</LI>
 *     <LI>{@link #EsiaAuthentificationException(Throwable)}</LI>
 *     <LI>{@link #EsiaAuthentificationException(String, Throwable)}</LI>
 * </UL>
 *
 * @author eremeevas
 */
public class EsiaAuthentificationException extends Exception {

    /**
     * @param message сообщение для вывода
     * @param cause   причина возникновения
     */
    public EsiaAuthentificationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Только причина
     *
     * @param cause
     */
    public EsiaAuthentificationException(Throwable cause) {
        super(cause);
    }

    /**
     * EsiaAuthentificationException без параметров
     */
    public EsiaAuthentificationException() {
    }

    /**
     * Только сообщение, без причины
     *
     * @param message
     */
    public EsiaAuthentificationException(String message) {
        super(message);
    }

}
