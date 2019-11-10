package com.bivgroup.ws.i900;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.diasoft.rsa.beanmaputils.BeanToMapMapper;
import ru.diasoft.rsa.beanmaputils.MapException;
import ru.sberbank.mort900.BankStatement;
import ru.sberbank.mort900.Documents;
import ru.sberbank.mort900.PaymentOrder;

/**
 *
 * @author deathstalker
 */
public class InvParserImpl implements Mort900Parser {

    private final Logger logger = Logger.getLogger(this.getClass());

    private Long lineCounter = 0L;

    private String getNextToken(BufferedReader reader) throws Mort900Exception {
        String result = null;
        try {
            result = reader.readLine();
        } catch (IOException ex) {
            throw new Mort900Exception(
                    String.format("Ошибка чтения строки %d файла", lineCounter),
                    String.format("Read error at line [%d] of bank statement file", lineCounter),
                    ex);
        }
        if (result != null) {
            lineCounter++;
        }
        return result;
    }

    @Override
    public Map<String, Object> parse(InputStream stream) throws Mort900Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        String encoding = "windows-1251";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(stream, encoding));
        } catch (UnsupportedEncodingException ex) {
            throw new Mort900Exception(
                    String.format("Кодировка '%s', используемая для чтения файла, не поддерживаетcя", encoding),
                    String.format("Unsupported encoding [%s] used for file reading", encoding),
                    ex);
        }

        // skip first record
        getNextToken(reader);

        BankStatement bankStatement = new BankStatement();
        Documents documents = new Documents();
        bankStatement.setDocuments(documents);
        String recordline;
        while (((recordline = getNextToken(reader)) != null) && (!recordline.isEmpty())) {
            PaymentOrder paymentOrder = readPaymentOrder(recordline);
            documents.getPaymentOrder().add(paymentOrder);
        }

        try {
            result = BeanToMapMapper.mapBeansToMap(bankStatement);
        } catch (MapException ex) {
            throw new Mort900Exception(
                    "Ошибка при преобразовании сведений из файла в объект типа Map",
                    "Error in object to map converting",
                    ex);
        }

        return result;
    }

    private PaymentOrder readPaymentOrder(String recordline) throws Mort900Exception {
        PaymentOrder result = new PaymentOrder();
       // НазначениеПлатежа
        result.setDescription(recordline);
        return result;
    }

}
