package com.bivgroup.services.gosuslygi;

import java.io.ByteArrayInputStream;
import org.apache.log4j.Logger;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.util.Store;
import ru.CryptoPro.CAdES.tools.CAdESUtility;
import ru.CryptoPro.CAdES.tools.verifier.GostContentSignerProvider;
import ru.CryptoPro.CAdES.tools.verifier.GostDigestCalculatorProvider;
import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.KeyStore.JCPPrivateKeyEntry;

import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;
import ru.CryptoPro.JCP.tools.Array;

public class EsiaGostSigner implements Signer {
    private static final String DELIMITER = ";";
    
    private Logger logger = Logger.getLogger(EsiaGostSigner.class);

    /**
     * Метод подписывающий контент цифровой подписью ГОСТ 34.10-2001
     *
     * @param storeType       тип хранилища (например HDImageStore)
     * @param storePass       пароль к хранилищу (зачастую null)
     * @param signerAliasName имя контейнера
     * @param signerAliasPass пароль к контейнеру
     * @param signProvider    название крипропровайдера (например, JCP)
     * @param paramForSign    контент для подписания
     * @return подписанный контент
     * @throws EsiaAuthentificationException
     */
    @Override
    public byte[] signFromStore(String storeType, String storePass,
                                String signerAliasName, String signerAliasPass, String signProvider, byte [] paramForSign) throws EsiaAuthentificationException {

        CAdESUtility.initJCPAlgorithms();

        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(storeType, signProvider);
        } catch (KeyStoreException | NoSuchProviderException e) {
            logger.error(e.toString());
        }
            try {
                if (null == storePass) {
                keyStore.load(null, null);}
                else
                {
                    keyStore.load(null, storePass.toCharArray());
                }
            } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                logger.error(e.toString());
            }
        char[] signerAliasPassChar = null;
        if (null != signerAliasPass) {
            signerAliasPassChar = signerAliasPass.toCharArray();
        }
        final KeyStore.ProtectionParameter signerProtectedParam = new KeyStore.PasswordProtection(signerAliasPassChar);
        JCPPrivateKeyEntry signerEntry = null;
        try {
            signerEntry = (JCPPrivateKeyEntry) keyStore.getEntry(signerAliasName, signerProtectedParam);
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            logger.error(e.toString());
        }
        Certificate[] signerChain = signerEntry.getCertificateChain();
        return signByteArrayToString(signerEntry.getPrivateKey(), signerChain, paramForSign);
    }

    public  byte[] signFromStoreAndCertFiles(String storeType, String storePass,
            String signerAliasName, String signerAliasPass,
            String certFiles, String signProvider,
            byte [] paramForSign)
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, Exception {

        String[] certFilesPath = certFiles.split(DELIMITER);

        KeyStore keyStore = KeyStore.getInstance(storeType, signProvider);
        if (null == storePass) {
            keyStore.load(null, null);
        } else {
            keyStore.load(null, storePass.toCharArray());
        }
        char[] signerAliasPassChar = null;
        if (null != signerAliasPass) {
            signerAliasPassChar = signerAliasPass.toCharArray();
        }
        final KeyStore.ProtectionParameter signerProtectedParam = new KeyStore.PasswordProtection(signerAliasPassChar);
        final JCPPrivateKeyEntry signerEntry = (JCPPrivateKeyEntry) keyStore.getEntry(signerAliasName, signerProtectedParam);
        Certificate signerCert = signerEntry.getCertificate();
        Certificate[] signerChain = new Certificate[certFilesPath.length + 1];
        signerChain[0] = signerCert;
        Certificate cert = null;
        for (int i = 1; i < signerChain.length; i++) {
            String certFilePath = certFilesPath[i - 1];
            byte[] certBytes = Array.readFile(certFilePath);
            cert = CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(certBytes));
            signerChain[i] = cert;
        }
        //final KeyStore.ProtectionParameter rootProtectedParam = new KeyStore.PasswordProtection(signerAliasPass.toCharArray());
        //final JCPPrivateKeyEntry rootEntry = (JCPPrivateKeyEntry) keyStore.getEntry(signerAliasName, rootProtectedParam);
        //signKey(signerEntry.getPrivateKey(), signerCert, signerChain, signProvider, fileToSign, signedFile, location, reason);
        return signByteArrayToString(signerEntry.getPrivateKey(), signerChain, paramForSign);
    }
    
    
    /**
     * Сама подпись контента осуществляется именно в этом методе, а в {@link #signFromStore(String, String, String, String, String, byte[])}
     * происходит инициализация необходимых параметров для дальнейшей работы с КриптоПРО.
     * <br>
     * Поэтому обращаться необходимо через метод {@link #signFromStore(String, String, String, String, String, byte[])}.
     *
     * @param privateKey ключ
     * @param signerChain цепочка сертефикатов
     * @param paramForSign контент для подписания
     * @return подписанный контент
     * @throws EsiaAuthentificationException
     */
    private byte[] signByteArrayToString(PrivateKey privateKey, Certificate [] signerChain, byte[] paramForSign ) throws EsiaAuthentificationException {

        try {
            X509Certificate[] tmpChain = new X509Certificate[signerChain.length];
            for (int i = 0;i<3;i++){
                tmpChain[i] = (X509Certificate) signerChain[i];
            }
            //X509Certificate [] tmpChain = (X509Certificate[]) signerChain;

            if (tmpChain == null)
                throw new EsiaAuthentificationException("EsiaAuthentificator: Certificate cast to X509Certificate failed!");

            List<X509Certificate> chain = Arrays.asList(tmpChain);
            Set<X509Certificate> certSet = new HashSet<X509Certificate>(chain);

            // Сертификат подписи - первый в списке.
//            X509Certificate signerCert = (X509Certificate) Arrays.asList(signerChain).iterator().next();
            X509Certificate signerCert = chain.iterator().next();
            Store certStore = new JcaCertStore(chain);
//            Store certStore = new JcaCertStore(Arrays.asList(signerChain));

            // Подготавливаем подпись.
            CMSSignedDataGenerator generator = new CMSSignedDataGenerator();

            ContentSigner contentSigner = new GostContentSignerProvider(
                    privateKey, JCP.PROVIDER_NAME);

            SignerInfoGenerator signerInfoGenerator = new JcaSignerInfoGeneratorBuilder(
                    new GostDigestCalculatorProvider(privateKey, JCP.PROVIDER_NAME)).build(contentSigner, signerCert);

            generator.addSignerInfoGenerator(signerInfoGenerator);
            generator.addCertificates(certStore);

            // Создаем совмещенную подпись PKCS7.
            CMSProcessable content = new CMSProcessableByteArray(paramForSign);
            CMSSignedData signedData = generator.generate((CMSTypedData) content, true);

            // Сформированная подпись.
            byte[] pkcs7 = signedData.getEncoded();

//            не вижу смысла в этих строках
//            CAdESSignature pkcs7Signature = new CAdESSignature(pkcs7, null, null);

//            pkcs7Signature.verify(certSet);

            return pkcs7;

        } catch (Exception e) {
            throw new EsiaAuthentificationException("EsiaAuthentificator : CAdESException in method signByteArrayToString");
        }

    }
}
