package com.bivgroup.stringutils;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEParameterSpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class StringCryptUtils {

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    private final String charset = "UTF-8";
    private final String defaultEncryptionPass = "PADSIDUFHQWER98234QWE578AHASDF93HASDF9238HBJSDF923";
    private final byte[] defaultSalt = {
            (byte) 0xa3, (byte) 0x21, (byte) 0x24, (byte) 0x2c,
            (byte) 0xf2, (byte) 0xd2, (byte) 0x3e, (byte) 0x19};

    private byte[] base64Decode(String value) {
        return javax.xml.bind.DatatypeConverter.parseBase64Binary(value);
    }

    private String base64Encode(byte[] value) {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(value);
    }

    private TimeBasedOneTimePasswordGenerator totp = null;
    private final long timeout;
    private String delimiter4testDecode = "";

    /**
     * The simplest constructor which will use a default password and salt to
     * encode the string.
     *
     */
    public StringCryptUtils() {
        setupEncryptor(defaultEncryptionPass, defaultSalt);
        timeout = 0;
    }

    /**
     * Dynamic constructor to give own key and salt to it which going to be used
     * to encrypt and then decrypt the given string.
     *
     * @param encryptionPassword
     * @param salt
     */
    public StringCryptUtils(String encryptionPassword, byte[] salt) {
        setupEncryptor(encryptionPassword, salt);
        timeout = 0;
    }

    public StringCryptUtils(long decryptTimeout) {
        timeout = decryptTimeout;
        this.totp = null;
        try {
            this.totp = new TimeBasedOneTimePasswordGenerator();
        } catch (NoSuchAlgorithmException e) {
        }
    }

    public void setDelimiter4CheckDecoded(String delimiter) {
        this.delimiter4testDecode = delimiter;
    }


    public void init(char[] pass, byte[] salt, int iterations) {
        try {
            PBEParameterSpec ps = new javax.crypto.spec.PBEParameterSpec(salt, 20);
            SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey k = kf.generateSecret(new javax.crypto.spec.PBEKeySpec(pass));
            encryptCipher = Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, k, ps);
            decryptCipher = Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE, k, ps);
        } catch (Exception e) {
            throw new SecurityException("Could not initialize CryptoLibrary: " + e.getMessage());
        }
    }

    /**
     * method to decrypt a string.
     *
     * @param str Description of the Parameter
     * @return String the encrypted string.
     */
    public synchronized String encrypt(String str) {
        try {
            if (totp != null) {
                generateAndSetEncryptor(new Date());
            }
            byte[] utf8 = str.getBytes(charset);
            byte[] enc = encryptCipher.doFinal(utf8);
            return URLEncoder.encode(base64Encode(enc), charset);
        } catch (Exception e) {
            throw new SecurityException("Could not encrypt: " + e.getMessage());
        }
    }

    /**
     * method to encrypting a string.
     *
     * @param str Description of the Parameter
     * @return String the encrypted string.
     */
    public synchronized String decrypt(String str) {
        try {
            Date decryptDateType = null;
            long tryCount = 1;
            if (totp != null) {
                if (timeout > 0) {
                    long timeStep = totp.getTimeStep(TimeUnit.MILLISECONDS);
                    tryCount = timeout / timeStep;
                    tryCount++;

                }
                decryptDateType = new Date();
            }

            String errorMessage = "";
            for (long i = 0; i < tryCount; i++) {
                try {
                    if (totp != null) {
                        Date curDecryptDate = new Date(decryptDateType.getTime() - i * totp.getTimeStep(TimeUnit.MILLISECONDS));
                        generateAndSetEncryptor(curDecryptDate);
                    }
                    byte[] dec = base64Decode(URLDecoder.decode(str, charset));
                    byte[] utf8 = decryptCipher.doFinal(dec);
                    String res = new String(utf8, charset);
                    if (delimiter4testDecode != null && !delimiter4testDecode.isEmpty() && !res.contains(delimiter4testDecode)) {
                        continue;
                    }
                    return res;
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                }
            }
            throw new SecurityException("Could not decrypt: " + errorMessage);

        } catch (Exception e) {
            throw new SecurityException("Could not decrypt: " + e.getMessage());
        }
    }

    private void generateAndSetEncryptor(Date curDecryptDate) {
        try {
            if (this.totp != null) {
                String generatedPassword = this.totp.generateSaltBaseOnLoginAndTotp(defaultEncryptionPass, curDecryptDate);
                byte[] generatedSalt = this.totp.generateByteSaltBaseOnLoginAndTotp(Hex.encodeHexString(defaultSalt), curDecryptDate);
                this.setupEncryptor(generatedPassword, generatedSalt);
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    /**
     * method to encrypting a string.
     *
     * @param str Description of the Parameter
     * @return String the encrypted string.
     * @throws SecurityException Description of the Exception
     */
    public synchronized String decryptURL(String str) {
        try {
            byte[] dec = base64Decode(str);
            byte[] utf8 = decryptCipher.doFinal(dec);
            return new String(utf8, charset);
        } catch (Exception e) {
            throw new SecurityException("Could not decrypt: " + e.getMessage());
        }
    }

    private void setupEncryptor(String defaultEncryptionPassword, byte[] salt) {
        java.security.Security.addProvider(new com.sun.crypto.provider.SunJCE());
        char[] pass = defaultEncryptionPassword.toCharArray();
        int iterations = 3;
        init(pass, salt, iterations);
    }
}
