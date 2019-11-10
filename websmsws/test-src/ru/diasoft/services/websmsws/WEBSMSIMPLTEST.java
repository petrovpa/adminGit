package ru.diasoft.services.websmsws;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ru.diasoft.services.websmsws.facade.MessageFacade;
import ru.diasoft.utils.DefaultedHashMap;
import ru.diasoft.utils.XMLUtil;

public class WEBSMSIMPLTEST {
    WEBSMSIMPL wsimpl;

    @Before
    public void setUp() throws Exception {
        wsimpl = new WEBSMSIMPL();
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    @Ignore
    public void myInit() throws Exception {        
        try {
            Map<String, Object> map = new DefaultedHashMap<String, Object>();
            String command = "myInit";
            String res = wsimpl.dscall(command, new XMLUtil(true, true).createXML(map), new ContextData());
            Assert.assertNotNull(res);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @Test
    @Ignore
    public void dispatchsmsmessageTest() throws Exception {
        String res = null;
        XMLUtil xmlUtil = new XMLUtil(true, true);
        String extId = "DOC_2000";
        
        Map<String, Object> map = new DefaultedHashMap<String, Object>();
        map.put("SMSMessage", "Hello world");        
        map.put("SMSNum", "79106652647");
        map.put("EXTID", extId);
        String mapString;
        try {
            mapString = xmlUtil.createXML(map);
            String command = "dispatchsmsmessage";
//            res = wsimpl.dscall(command, mapString);
            Map<String, Object> result = xmlUtil.doURL("http://localhost:8080/websmsws/websmsws", 
                    command, mapString, null);
            Assert.assertNotNull(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
        
    
    @Test
    @Ignore
    public void mailmessageTest() throws Exception {
        Map<String, Object> map = new DefaultedHashMap<String, Object>();
        map.put("SMTPReceipt", "hu@yandex.ru mu@gmail.com");
        map.put("SMTPSubject", "Тестовое оповещение");
        map.put("SMTPMESSAGE", "Привет из junit тестов");
        String mapString;
        try {
            mapString = new XMLUtil(true, true).createXML(map);
            String command = "mailmessage";
            String res = wsimpl.dscall(command, mapString, new ContextData());
            Assert.assertNotNull(res);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @Test
    @Ignore
    public void testInnerInnerPorcessTest() throws Exception {
        Map<String, Object> map = new DefaultedHashMap<String, Object>();
        XMLUtil xmlUtil = new XMLUtil(true, true);
        String mapString;
        String extId = "DOC_2000";
        map.put("EXTID", extId);

        try {
            mapString = new XMLUtil(true, true).createXML(map);
            String command = "wholeproctest";
//            String res = wsimpl.dscall(command, mapString);
            Map<String, Object> result = xmlUtil.doURL("http://localhost:8080/websmsws/websmsws", 
                    command, mapString, null);
            Assert.assertNotNull(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void messageFindByIdTest() throws Exception {
        Integer messageId = 1000;
        XMLUtil xmlUtil = new XMLUtil(true, true);
        Map<String, Object> messageParams = new HashMap<String, Object>();
        messageParams.put("MESSAGEID", messageId);
        
        Map<String, Object> result = xmlUtil.doURL("http://localhost:8080/websmsws/websmsws", 
                "messageFindById", xmlUtil.createXML(messageParams), null);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.get("MESSAGEID"), 1000);
        Assert.assertEquals(result.get("STATUS"), 30);
        Assert.assertEquals(result.get("EXTERNALID"), 1338086750);
        Assert.assertEquals(result.get("MSGBODY"), "Text1");
        Assert.assertEquals(result.get("EXTID"), "DOC_1000");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void messageGetListByParamsTest() throws Exception {
        String extId = "DOC_1000";
        XMLUtil xmlUtil = new XMLUtil(true, true);
        
        //Проверка поиска по EXTID
        Map<String, Object> messageParams = new HashMap<String, Object>();
        messageParams.put("ORDERBY", "MSG.MESSAGEID");
        messageParams.put("ROWSCOUNT", 20);        
        messageParams.put("EXTID", extId);
        
        Map<String, Object> result = xmlUtil.doURL("http://localhost:8080/websmsws/websmsws", 
                "messageFindListByParams", xmlUtil.createXML(messageParams), null);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.get("TOTALCOUNT"), 1);
        Assert.assertEquals(((List<Map<String, Object>>)result.get("Result")).get(0).get("MESSAGEID"), 1000);
        
        //Проверка поиска по периоду и по EXTID
        messageParams.put("MSGMINDATE", new SimpleDateFormat("dd.MM.yyyy").parse("30.10.2009"));
        messageParams.put("MSGMAXDATE", new SimpleDateFormat("dd.MM.yyyy").parse("31.10.2009"));
        
        result = xmlUtil.doURL("http://localhost:8080/websmsws/websmsws", 
                "messageFindListByParams", xmlUtil.createXML(messageParams), null);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.get("TOTALCOUNT"), 1);
        Assert.assertEquals(((List<Map<String, Object>>)result.get("Result")).get(0).get("MESSAGEID"), 1000);
        
    }
    
    
}
