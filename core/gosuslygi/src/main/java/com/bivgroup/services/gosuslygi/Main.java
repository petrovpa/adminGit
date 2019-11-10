package com.bivgroup.services.gosuslygi;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Пример использования либы, дорабатывается по мере тестирования
 */
public class Main {
    public static void main(String[] args) throws EsiaAuthentificationException, ClassNotFoundException, IOException, URISyntaxException {

        //стандартный пример вызова библиотеки
        EsiaAuthentificator esiaAuthentificator = new EsiaAuthentificator();
        esiaAuthentificator.getAuthUrl().getURL();

    }
}
