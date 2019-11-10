package com.bivgroup.services.gosuslygi;

/**
 * Интерфейс для взаимодействия с FrontEnd в процессе аутентификации/авторизации пользователя в ЕСИА.
 *
 * <br>
 * Методы:
 * <UL>
 *     <LI>{@link #getURL()}</LI>
 *     <LI>{@link #getState()}</LI>
 * </UL>
 *
 * @author eremeevas
 */
public interface AuthRequestInfo {

    /**
     * @return URL-ссылка для получения авторизационного кода в сервисе ЕСИА
     */
    String getURL();

    /**
     * @return state-параметр запроса для дальнейшей проверки валидности ответа от ЕСИА
     */
    String getState();
    
    
    /**
     * @return код ошибки
     */
    String getErrorCode();
    
    /**
     * @return текст ошибки
     */
    String getErrorText();

}
