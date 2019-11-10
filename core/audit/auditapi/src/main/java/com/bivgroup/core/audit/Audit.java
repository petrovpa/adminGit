package com.bivgroup.core.audit;

import java.util.List;

/**
 * Интерфес базовых методов для аудита
 */
public interface Audit {

    /**
     * Метод сохранение сообщения по аудиту
     *
     * @param operation     наименование операция
     * @param resultStatus  результат операция #{@link ResultOperation}
     * @param login         логин пользователя, вызвавщего события
     * @param userAccountId идентификатор аккаунта пользователя, вызвавщего событие
     * @param ipInfo        информация об IP адресах, с которых происходит запрос #{@link AuditIpInfo}
     * @param message       сообщения, которое требуется сохранить
     */
    void audit(String operation, ResultOperation resultStatus, String login, Long userAccountId, AuditIpInfo ipInfo, String message);

    /**
     * * Метод сохранение параметров и сообщения по аудиту
     * *
     *
     * @param operation     наименование операция
     * @param resultStatus  результат операция #{@link ResultOperation}
     * @param login         логин пользователя, вызвавщего события
     * @param userAccountId идентификатор аккаунта пользователя, вызвавщего событие
     * @param ipInfo        информация об IP адресах, с которых происходит запрос #{@link AuditIpInfo}
     * @param message       сообщения, которое требуется сохранить
     * @param params        параметризированный документ, который требуется сохранить
     * @param obfuscators   список реализаций интерфейса #{@link Obfuscator<P>} для обфусцирования документов (например: изменение парсональных данных контрагента)
     * @param <P>           тип документа, который требуется сохранить
     */
    <P> void audit(String operation, ResultOperation resultStatus, String login, Long userAccountId, AuditIpInfo ipInfo, String message, P params, List<Obfuscator<P>> obfuscators);

    /**
     * * Метод сохранение сообщения по аудиту
     * *
     *
     * @param operation             наименование операция
     * @param resultStatus          результат операция #{@link ResultOperation}
     * @param login                 логин пользователя, вызвавщего события
     * @param userAccountId         идентификатор аккаунта пользователя, вызвавщего событие
     * @param ipInfo                информация об IP адресах, с которых происходит запрос #{@link AuditIpInfo}
     * @param message               сообщения, которое требуется сохранить
     * @param inputDocumentId       идентификатор входного документа, по которому проводим событие
     * @param outputDocumentId      идентификатор выходного документа, по которому проводим событие
     * @param parameters            параметризированный входные параметры, которые требуется сохранить
     * @param document              параметризированный выходной документ, который требуется сохранить
     * @param parametersObfuscators список реализаций интерфейса #{@link Obfuscator<P>} для обфусцирования выходных параметров (например: изменение парсональных данных контрагента)
     * @param documentObfuscators   список реализаций интерфейса #{@link Obfuscator<D>} для обфусцирования выхолных документов (например: изменение парсональных данных контрагента)
     * @param <P>                   тип входных параметров
     * @param <D>                   тип выходного документа
     */
    <P, D> void audit(String operation, ResultOperation resultStatus, String login, Long userAccountId, AuditIpInfo ipInfo,
                      String message, Long inputDocumentId, Long outputDocumentId, P parameters, D document, List<Obfuscator<P>> parametersObfuscators,
                      List<Obfuscator<D>> documentObfuscators);

    /**
     * * Метод сохранение документа и сообщения по аудиту, с применением списка обфускатором над объектом
     * *
     *
     * @param auditParameters       параметры аудита #{@link AuditParameters}
     * @param parameters            параметризированный входные параметры, которые требуется сохранить
     * @param document              параметризированный выходной документ, который требуется сохранить
     * @param parametersObfuscators список реализаций интерфейса #{@link Obfuscator<P>} для обфусцирования выходных параметров (например: изменение парсональных данных контрагента)
     * @param documentObfuscators   список реализаций интерфейса #{@link Obfuscator<D>} для обфусцирования выхолных документов (например: изменение парсональных данных контрагента)
     * @param <P>                   тип входных параметров
     * @param <D>                   тип выходного документа
     */
    <P, D> void audit(AuditParameters auditParameters, P parameters, D document, List<Obfuscator<P>> parametersObfuscators,
                      List<Obfuscator<D>> documentObfuscators);

    /**
     * * Метод сохранение документа и сообщения по аудиту, с применением списка обфускатором над объектом
     * *
     *
     * @param auditParameters            параметры аудита #{@link AuditParameters}
     * @param parameters                 параметризированный входные параметры, которые требуется сохранить
     * @param document                   параметризированный выходной документ, который требуется сохранить
     * @param parametersClassObfuscators массив классов реализаций интерфейса #{@link Obfuscator<P>} для обфусцирования выходных параметров (например: изменение парсональных данных контрагента)
     * @param documentClassObfuscators   массив классов реализаций интерфейса #{@link Obfuscator<D>} для обфусцирования выхолных документов (например: изменение парсональных данных контрагента)
     * @param <P>                        тип входных параметров
     * @param <D>                        тип выходного документа
     */
    <P, D> void audit(AuditParameters auditParameters, P parameters, D document, Class<Obfuscator>[] parametersClassObfuscators,
                      Class<Obfuscator>[] documentClassObfuscators);

}
