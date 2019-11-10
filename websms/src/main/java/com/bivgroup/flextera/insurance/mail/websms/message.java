/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.flextera.insurance.mail.websms;

/**
 * Класс используется как структура для хранения информации об отправленных в
 * SMS центр сообщениях
 *
 * @author kkatchura
 *
 */
class smsMessage {

    /**
     * Идентификатор группы
     */
    byte group_id = 0;
    /**
     * Идентификатор сообщения
     */
    byte message_id = 0;
    /**
     * Тело сообщения
     */
    String message = "";
    /**
     * Внешний ID сообщения, которое присваивается SMS
     */
    int ext_id = 0;
    /**
     * Связь сообщения с какой-либо сущностью в структуре нашей БД
     */
    String extEntityId = "";
    /**
     * Время, когда сообщение было отправлено
     */
    long startupTime = 0;

    public synchronized long getstartupTime() {
        return startupTime;
    }

    public synchronized void setstartupTime(long time) {
        startupTime = time;
    }
}

