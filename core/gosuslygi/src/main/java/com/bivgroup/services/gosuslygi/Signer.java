package com.bivgroup.services.gosuslygi;

/**
 * Интерфейс подписания цифровой подписью
 */
public interface Signer {

    /**
     * Метод подписывающий контент цифровой подписью
     *
     * @param storeType тип хранилища (например HDImageStore)
     * @param storePass пароль к хранилищу (зачастую null)
     * @param signerAliasName  имя контейнера
     * @param signerAliasPass пароль к контейнеру
     * @param signProvider название крипропровайдера (например, JCP)
     * @param paramForSign контент для подписания
     * @return
     * @throws Exception
     */
    byte[] signFromStore(String storeType, String storePass,
                         String signerAliasName, String signerAliasPass, String signProvider, byte [] paramForSign) throws Exception;

    byte[] signFromStoreAndCertFiles(String storeType, String storePass,
            String signerAliasName, String signerAliasPass,
            String certFiles, String signProvider,
            byte [] paramForSign) throws Exception;

}
