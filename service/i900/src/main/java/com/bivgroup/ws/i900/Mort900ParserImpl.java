/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.i900;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.log4j.Logger;
import ru.diasoft.rsa.beanmaputils.BeanToMapMapper;
import ru.diasoft.rsa.beanmaputils.MapException;
import ru.sberbank.mort900.BankAccountBalance;
import ru.sberbank.mort900.BankStatement;
import ru.sberbank.mort900.Contragent;
import ru.sberbank.mort900.ContragentInfo;
import ru.sberbank.mort900.Documents;
import ru.sberbank.mort900.Header;
import ru.sberbank.mort900.PaymentOrder;


/**
 *
 * @author reson
 */
public class Mort900ParserImpl implements Mort900Parser {
    
    private final Logger logger = Logger.getLogger(this.getClass());
    
    private Long lineCounter = 0L;

    private String getNextToken(BufferedReader reader) throws Mort900Exception {
        String result = null;
        try {
            result = reader.readLine();
        } catch (IOException ex) {
            throw new Mort900Exception(
                    String.format("Ошибка чтения строки %d файла банковской выписки", lineCounter),
                    String.format("Read error at line [%d] of bank statement file", lineCounter),
                    ex);
        }
        if (result != null) {
            lineCounter++;
        }
        return result;
    }

    private Map.Entry<String, String> getStringValue(String token) {
        Map.Entry<String, String> result = null;
        if ((token != null) && (!token.isEmpty())) {
            final String[] tokenArray = token.split("=");
            result = new Map.Entry<String, String>() {

                @Override
                public String getKey() {
                    return tokenArray[0];
                }

                @Override
                public String getValue() {
                    if (tokenArray.length > 1) {
                        return tokenArray[1];
                    } else {
                        return null;
                    }
                }

                @Override
                public String setValue(String value) {
                    throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose Tools | Templates.
                }
            };
            return result;
        }
        return null;
    }

    private String getStringTokenValue(String tokenName, BufferedReader reader) throws Mort900Exception {
        Map.Entry<String, String> entry = getStringValue(getNextToken(reader));
        if (entry != null) {
            if (entry.getKey().equalsIgnoreCase(tokenName)) {
                return entry.getValue();
            } else {
                
                String foundedTextEng;
                String foundedTextRus;
                if (entry.getKey().matches("[а-яА-яa-zA-Z0-9]+")) {
                    foundedTextEng = String.format("[%s]", entry.getKey());
                    foundedTextRus = String.format("'%s'", entry.getKey());
                } else {
                    // для нетекстовых и/или системных символов в БД не сохраняется найденное в файле значение, чтобы избежать ошибок вида UnsupportedEncodingException при чтении данных из БД
                    foundedTextEng = String.format("non-text or system characters (probably, file is corrupted or not in plain text format)", entry.getKey());
                    foundedTextRus = String.format("наличие не текстовых или системных символов (возможно, файл повержден или не является текстовым)", entry.getKey());
                }
                
                String exceptionTextEng = String.format("Error parse bank statement file - at line [%d] expected [%s], but found %s", lineCounter, tokenName, foundedTextEng);
                logger.error(exceptionTextEng);
                throw new Mort900Exception(
                        String.format("Ошибка обработки файла банковской выписки - в строке %d ожидалось значение '%s', но обнаружено %s", lineCounter, tokenName, foundedTextRus),
                        exceptionTextEng
                );
                
            }
        } else {
            logger.error(String.format("Error parse bank statement file - at line [%d] expected [%s], but found NULL", lineCounter, tokenName));
            throw new Mort900Exception(
                    String.format("Ошибка обработки файла банковской выписки - в строке %d ожидалось значение '%s', но обнаружено NULL", lineCounter, tokenName),
                    String.format("Error parse bank statement file - at line [%d] expected [%s], but found NULL", lineCounter, tokenName)
            );
        }
    }

    private Date parseDate(String value) throws Mort900Exception {
        String dateMask = "dd.MM.yyyy";
        DateFormat dateFormat = new SimpleDateFormat(dateMask);
        Date result;
        try {
            result = dateFormat.parse(value);
        } catch (ParseException ex) {
            throw new Mort900Exception(
                    String.format("Ошибка получения даты, указанной в строке %d файла банковской выписки - строковое значение '%s' не удалось преобразовать в дату по маске '%s'", lineCounter, value, dateMask),
                    String.format("Error date parsing at line [%d] of bank statement file - value [%s] cant be parsed to date using mask [%s]", lineCounter, value, dateMask),
                    ex);
        }
        return result;
    }
    
    private Date parseTime(String value) throws Mort900Exception {
        String timeMask = "HH:mm:ss";
        DateFormat timeFormat = new SimpleDateFormat(timeMask);
        Date result;
        try {
            result = timeFormat.parse(value);
        } catch (ParseException ex) {
            throw new Mort900Exception(
                    String.format("Ошибка получения времени, указанного в строке %d файла банковской выписки - строковое значение '%s' не удалось преобразовать в время по маске '%s'", lineCounter, value, timeMask),
                    String.format("Error time parsing at line [%d] of bank statement file - value [%s] cant be parsed to date using mask [%s]", lineCounter, value, timeMask),
                    ex);
        }
        return result;
    }
    
    private XMLGregorianCalendar parseDateToGC(Date date) throws Mort900Exception {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar result = null;
        try {
            result = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException ex) {
            throw new Mort900Exception(
                    String.format("Ошибка обработки даты или времени, указанных в строке %d файла банковской выписки: " + ex.getLocalizedMessage(), lineCounter),
                    String.format("Error date or time parsing at line [%d] of bank statement file" + ex.getMessage(), lineCounter),
                    ex);
        }
        return result;
    }

    private Double parseDouble(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }

    private Long readLongValue(String tokenName, BufferedReader reader) throws Mort900Exception {
        String strValue = getStringTokenValue(tokenName, reader);
        if ((strValue == null) || (strValue.isEmpty())) {
            return null;
        } else {
            return Long.valueOf(strValue);
        }
    }

    private BigInteger readBigInetegerValue(String tokenName, BufferedReader reader) throws Mort900Exception {
        Long longValue = readLongValue(tokenName, reader);
        if (longValue == null) {
            return null;
        } else {
            return BigInteger.valueOf(longValue);
        }
    }

    private XMLGregorianCalendar readGCDateValue(String tokenName, BufferedReader reader) throws Mort900Exception {
        String strDate = getStringTokenValue(tokenName, reader);
        if ((strDate == null) || (strDate.isEmpty())) {
            return null;
        } else {
            return parseDateToGC(parseDate(strDate));
        }
    }
    
    private Double readDoubleValue(String tokenName, BufferedReader reader) throws Mort900Exception {
        String strValue = getStringTokenValue(tokenName, reader);
        if ((strValue == null) || (strValue.isEmpty())) {
            return null;
        } else {
            return Double.valueOf(strValue);
        }
    }

    private Header readHeader(BufferedReader reader) throws Mort900Exception {
        Header result = new Header();

        result.setSignature(getStringTokenValue("1CClientBankExchange", reader));
        result.setFormatVersion(getStringTokenValue("ВерсияФормата", reader));
        result.setCharset(getStringTokenValue("Кодировка", reader));
        result.setSender(getStringTokenValue("Отправитель", reader));
        result.setReceiver(getStringTokenValue("Получатель", reader));
        result.setCreateDate(readXMLGCDate("ДатаСоздания", reader));
        result.setCreateTime(readXMLGCTime("ВремяСоздания", reader));
        result.setStartDate(readXMLGCDate("ДатаНачала", reader));
        result.setEndDate(readXMLGCDate("ДатаКонца", reader));
        result.setBankAccount(getStringTokenValue("РасчСчет", reader));

        return result;
    }
    
    private BankAccountBalance readBankAccountBalance(BufferedReader reader) throws Mort900Exception {
        BankAccountBalance result = new BankAccountBalance();

        // СекцияРасчСчет
        getStringTokenValue("СекцияРасчСчет", reader);
        // ДатаНачала=30.09.2015
        result.setStartDate(readXMLGCDate("ДатаНачала", reader));
        // ДатаКонца=30.09.2015
        result.setEndDate(readXMLGCDate("ДатаКонца", reader));
        // НачальныйОстаток=1.73
        result.setStartBalance(readDouble("НачальныйОстаток", reader));
        // РасчСчет=40701810500020059102
        result.setBankAccount(getStringTokenValue("РасчСчет", reader));
        // ВсегоСписано=8200000
        result.setTotalWithdraw(readDouble("ВсегоСписано", reader));
        // ВсегоПоступило=1.34
        result.setTotalIncome(readDouble("ВсегоПоступило", reader));
        // КонечныйОстаток=1.07
        result.setEndBalance(readDouble("КонечныйОстаток", reader));
        // КонецРасчСчет
        getStringTokenValue("КонецРасчСчет", reader);

        return result;
    }

    private XMLGregorianCalendar readXMLGCDate(String tokenName, BufferedReader reader) throws Mort900Exception {
        return parseDateToGC(parseDate(getStringTokenValue(tokenName, reader)));
    }

    private XMLGregorianCalendar readXMLGCTime(String tokenName, BufferedReader reader) throws Mort900Exception {
        return parseDateToGC(parseTime(getStringTokenValue(tokenName, reader)));
    }

    private Double readDouble(String tokenName, BufferedReader reader) throws Mort900Exception {
        return parseDouble(getStringTokenValue(tokenName, reader));
    }

    private ContragentInfo readContragentInfo(String contragentPrefix, BufferedReader reader) throws Mort900Exception {
        ContragentInfo result = new ContragentInfo();

        // contragentPrefix=ПАО СБЕРБАНК
        result.setName(getStringTokenValue(contragentPrefix, reader));
        // contragentPrefixИНН=7707083893
        result.setINN(getStringTokenValue(contragentPrefix + "ИНН", reader));
        // contragentPrefixКПП=775001001
        result.setKPP(getStringTokenValue(contragentPrefix + "КПП", reader));
        // contragentPrefixРасчСчет=30233810749000600001
        result.setAccount(getStringTokenValue(contragentPrefix + "РасчСчет", reader));
        // contragentPrefixБанк1=ЗАПАДНО-УРАЛЬСКИЙ БАНК ОАО "СБЕРБАНК РОССИИ"
        result.setBankName(getStringTokenValue(contragentPrefix + "Банк1", reader));
        // contragentPrefixБИК=045773603
        result.setBankBIK(getStringTokenValue(contragentPrefix + "БИК", reader));
        // contragentPrefixКорсчет=30101810900000000603
        result.setBankCorrespondAccount(getStringTokenValue(contragentPrefix + "Корсчет", reader));

        return result;
    }
    
    private Contragent readContragent(String contragentPrefix, String dateSuffix, BufferedReader reader) throws Mort900Exception {
        Contragent result = new Contragent();

        // contragentPrefixСчет=30233810749000600001
        result.setBankAccount(getStringTokenValue(contragentPrefix + "Счет", reader));
        // Дата<dateSuffix>=30.09.2015
        result.setWithdrawDate(readGCDateValue("Дата" + dateSuffix, reader));
        // contragentPrefix=ПАО СБЕРБАНК
        // contragentPrefixИНН=7707083893
        // contragentPrefixКПП=775001001
        // contragentPrefixРасчСчет=30233810749000600001
        // contragentPrefixБанк1=ЗАПАДНО-УРАЛЬСКИЙ БАНК ОАО "СБЕРБАНК РОССИИ"
        // contragentPrefixБИК=045773603
        // contragentPrefixКорсчет=30101810900000000603
        result.setContragentInfo(readContragentInfo(contragentPrefix, reader));

        return result;
    }

    private PaymentOrder readPaymentOrder(String docHeader, BufferedReader reader) throws Mort900Exception {
        PaymentOrder result = new PaymentOrder();

        // СекцияДокумент=Платежное поручение
        result.setDocumentType(getStringValue(docHeader).getValue());
        // Номер=103415
        result.setNumber(getStringTokenValue("Номер", reader));
        // Дата=30.09.2015
        result.setDocumentDate(readGCDateValue("Дата", reader));
        // Сумма=190.12
        result.setSum(readDoubleValue("Сумма", reader));

        // ПлательщикСчет=30233810749000600001
        // ДатаСписано=30.09.2015
        // Плательщик=ПАО СБЕРБАНК
        // ПлательщикИНН=7707083893
        // ПлательщикКПП=775001001
        // ПлательщикРасчСчет=30233810749000600001
        // ПлательщикБанк1=ЗАПАДНО-УРАЛЬСКИЙ БАНК ОАО "СБЕРБАНК РОССИИ"
        // ПлательщикБИК=045773603
        // ПлательщикКорсчет=30101810900000000603
        result.setPayer(readContragent("Плательщик", "Списано", reader));

        // ПолучательСчет=40701810500020059102
        // ДатаПоступило=30.09.2015
        // Получатель=ООО "СК Сбербанк страхование"
        // ПолучательИНН=7706810747
        // ПолучательКПП=772501001
        // ПолучательРасчСчет=40701810500020059102
        // ПолучательБанк1=ПАО СБЕРБАНК
        // ПолучательБИК=044525225
        // ПолучательКорсчет=30101810400000000225
        result.setReceiver(readContragent("Получатель", "Поступило", reader));

        // ВидПлатежа=ELEC
        result.setPaymentType(getStringTokenValue("ВидПлатежа", reader));
        // ВидОплаты=01
        result.setPaymentKind(getStringTokenValue("ВидОплаты", reader));
        // Код=
        result.setCode(getStringTokenValue("Код", reader));
        // СтатусСоставителя=
        result.setCreatorStatus(getStringTokenValue("СтатусСоставителя", reader));
        // ПоказательКБК=
        result.setKBK(getStringTokenValue("ПоказательКБК", reader));
        // ОКАТО=
        result.setOKATO(getStringTokenValue("ОКАТО", reader));
        // ПоказательОснования=
        result.setPurposeMark(getStringTokenValue("ПоказательОснования", reader));
        // ПоказательПериода=
        result.setPeriodMark(getStringTokenValue("ПоказательПериода", reader));
        // ПоказательНомера=
        result.setNumberMark(getStringTokenValue("ПоказательНомера", reader));
        // ПоказательДаты=
        result.setDateMark(getStringTokenValue("ПоказательДаты", reader));
        // ПоказательТипа=
        result.setTypeMark(getStringTokenValue("ПоказательТипа", reader));
        // Очередность=5
        result.setPriority(readBigInetegerValue("Очередность", reader));
        // НазначениеПлатежа=8616;КОЗЫРЕВ МИХАИЛ ЮРЬЕВИЧ;02;11;2000205697;БЕКК АЛЕКСАНДР ВАСИЛЬЕВИЧ;28.12.1987;
        result.setDescription(getStringTokenValue("НазначениеПлатежа", reader));
        // КонецДокумента
        getStringTokenValue("КонецДокумента", reader);
            
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
                    String.format("Кодировка '%s', используемая для чтения файла банковской выписки, не поддерживаетcя", encoding),
                    String.format("Unsupported encoding [%s] used for bank statement file reading", encoding),
                    ex);
        }

        BankStatement bankStatement = new BankStatement();

        bankStatement.setHeader(readHeader(reader));
        bankStatement.setBankAccountBalance(readBankAccountBalance(reader));
        Documents documents = new Documents();
        bankStatement.setDocuments(documents);
        String docHeader;
        while (((docHeader = getNextToken(reader)) != null) && (!docHeader.isEmpty()) && (!docHeader.equals("КонецФайла"))) {
            PaymentOrder paymentOrder = readPaymentOrder(docHeader, reader);
            documents.getPaymentOrder().add(paymentOrder);
        }

        try {
            result = BeanToMapMapper.mapBeansToMap(bankStatement);
        } catch (MapException ex) {
            throw new Mort900Exception(
                    "Ошибка при преобразовании сведений из файла банковской выписки в объект типа Map",
                    "Error in bank statement object to map converting",
                    ex);
        }
        
        return result;
        //throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose Tools | Templates.
    }

}
