package com.bivgroup.services.gosuslygi;

/**
 * Интерфейс для передачи данных об аутентифицированном/авторизованном пользователе в ЕСИА
 *
 * <br>
 * Методы:
 * <UL>
 *     <LI>{@link #getIdToken()}</LI>
 *     <LI>{@link #getFullName()}</LI>
 *     <LI>{@link #getBirthDate()}</LI>
 *     <LI>{@link #getGender()}</LI>
 *     <LI>{@link #getSnils()}</LI>
 *     <LI>{@link #getInn()}</LI>
 *     <LI>{@link #getIdDoc()}</LI>
 *     <LI>{@link #getMobileNumber()}</LI>
 * </UL>
 *
 *
 * @author eremeevas
 */
public interface AuthorizedUserInfo {

    /**
     * @return маркер идентификации
     */
    String getIdToken();

    /**
     * @return ФИО пользователя
     */
    String getFirstName();
    
    /**
     * @return ФИО пользователя
     */
    String getLastName();
    
    /**
     * @return ФИО пользователя
     */
    String getMiddleName();

    /**
     * @return дата рождения пользователя
     */
    String getBirthDate();

    /**
     * @return пол пользователя (если заполнен в аккаунте ЕСИА)
     */
    String getGender();

    /**
     * @return номер СНИЛС (если заполнен в аккаунте ЕСИА)
     */
    String getSnils();

    /**
      @return номер ИНН (если заполнен в аккаунте ЕСИА)
     */
    String getInn();

    /**
     * @return серия и номер документа удостоверяющего личность (ДУЛ),
     * дата выдачи, кем выдан, код подразделения (если заполнены в аккаунте ЕСИА)
     */
    String getNumber();
    
    String getSeries();

    /**
     * @return номер мобильного телефона (если заполнен в аккаунте ЕСИА)
     */
    String getMobileNumber();
    
    /**
     * @return статус УЗ ЕСИА
     */
    String getStatusAccount();
    
    /**
     * @return процесс проверки УЗ
     */
    boolean getVerifyingAccount();
    
    /**
     * @return статус документа
     */
    String getVrfStuDoc();
    
    
    /**
     * @return тип документа
     */
    String getTypeDoc();

}
