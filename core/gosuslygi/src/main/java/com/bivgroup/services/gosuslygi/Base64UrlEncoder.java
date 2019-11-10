package com.bivgroup.services.gosuslygi;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Кодирование в нужном формате для ЕСИА (base64 url safe).
 * <br>
 * Взято с официальной документации ЕСИА - http://minsvyaz.ru/uploaded/presentations/esiametodicheskierekomendatsii233.pdf
 * (стр. 154, 63 сноска снизу листа)
 * <br>
 * Стандартные имплементации якобы имеют несовместимые дефекты с шаблонами сервисов ЕСИА.
 *
 * @author eremeevas
 *
 */
public class Base64UrlEncoder {

    /**
     * @param arg массив байтов для шифрования
     * @return зашифрованный массив (base 64 url safe) в строковом представлении
     */
    public String base64UrlEncode(byte [] arg) {
//        String s = Base64.getEncoder().encodeToString(arg); // Standard base64 encoder
//        s = s.split("=")[0]; // Remove any trailing '='s
//        s = s.replace('+', '-'); // 62nd char of encoding
//        s = s.replace('/', '_'); // 63rd char of encoding
//        return  s;
        return Base64.getUrlEncoder().encodeToString(arg);
    }

    /**
     * @param arg массив байтов для дешифрования
     * @return расшифрованный массив в строковом представлении
     * @throws Exception
     */
    public byte [] base64UrlDecode(String arg) throws EsiaAuthentificationException {
//        String s = arg;
//        s = s.replace('-', '+'); // 62nd char of encoding
//        s = s.replace('_', '/'); // 63rd char of encoding
//        switch (s.length() % 4) // Pad with trailing '='s
//        {
//            case 0: break; // No pad chars in this case
//            case 2: s += "=="; break; // Two pad chars
//            case 3: s += "="; break; // One pad char
//            default: throw new EsiaAuthentificationException("EsiaAuthentificator: Illegal base64url string in decoder!");
//        }
//        return Base64.getDecoder().decode(s);
                return Base64.getUrlDecoder().decode(arg.getBytes(StandardCharsets.UTF_8)); // Standard base64 decoder
    }

}
