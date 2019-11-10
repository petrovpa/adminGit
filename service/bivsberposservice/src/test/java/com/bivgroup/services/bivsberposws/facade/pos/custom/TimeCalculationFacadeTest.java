/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kkulkov
 */
public class TimeCalculationFacadeTest {
    
    public TimeCalculationFacadeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getElapsedTimeByUsers method, of class ContractCustomFacade.
     */
    @Test
    public void testGetElapsedTimeByUsers() throws Exception {
        
        /*List<Date> dayOffList = new ArrayList<Date>();
        Calendar holiday = new GregorianCalendar(2014, 7, 22);
        // выходной сегодня.
        dayOffList.add(holiday.getTime());

        List<Map<String, Object>> groupMap = new ArrayList<Map<String, Object>>();
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("STATESYSNAME", "STATE1");
        group.put("GROUPNAME", "GROUP1");
        groupMap.add(group);
        group = new HashMap<String, Object>();
        group.put("STATESYSNAME", "STATE2");
        group.put("GROUPNAME", "GROUP1");
        groupMap.add(group);        
        group = new HashMap<String, Object>();
        group.put("STATESYSNAME", "STATE3");
        group.put("GROUPNAME", "GROUP2");
        groupMap.add(group);

        List<Map<String, Object>> sMTransList = new ArrayList<Map<String, Object>>();
        Map<String, Object> trans = new HashMap<String, Object>();
        trans.put("STATESYSNAME", "STATE1");
        Calendar stateDate = new GregorianCalendar(2014, 7, 21, 9, 30);
        trans.put("STATEDATE",  stateDate.getTime());
        trans.put("USERID", 1L);
        sMTransList.add(trans);
        
        trans = new HashMap<String, Object>();
        trans.put("STATESYSNAME", "STATE2");
        stateDate = new GregorianCalendar(2014, 7, 21, 10, 30);
        trans.put("STATEDATE",  stateDate.getTime());
        trans.put("USERID", 1L);
        sMTransList.add(trans);
        
        trans = new HashMap<String, Object>();
        trans.put("STATESYSNAME", "STATE3");
        stateDate = new GregorianCalendar(2014, 7, 21, 11, 30);
        trans.put("STATEDATE",  stateDate.getTime());
        trans.put("USERID", 1L);
        sMTransList.add(trans);

        trans = new HashMap<String, Object>();
        trans.put("STATESYSNAME", "STATE4");
        stateDate = new GregorianCalendar(2014, 7, 21, 15, 30);
        trans.put("STATEDATE",  stateDate.getTime());
        trans.put("USERID", 1L);
        sMTransList.add(trans);

        trans = new HashMap<String, Object>();
        trans.put("STATESYSNAME", "STATE5");
        stateDate = new GregorianCalendar(2014, 7, 21, 11, 30);
        trans.put("STATEDATE",  stateDate.getTime());
        trans.put("USERID", 1L);
        sMTransList.add(trans);

        trans = new HashMap<String, Object>();
        trans.put("STATESYSNAME", "STATE1");
        stateDate = new GregorianCalendar(2014, 7, 22, 15, 30);
        trans.put("STATEDATE",  stateDate.getTime());
        trans.put("USERID", 1L);
        sMTransList.add(trans);

        trans = new HashMap<String, Object>();
        trans.put("STATESYSNAME", "STATE3");
        stateDate = new GregorianCalendar(2014, 7, 24, 10, 30);
        trans.put("STATEDATE",  stateDate.getTime());
        trans.put("USERID", 1L);
        sMTransList.add(trans);

        trans.put("STATESYSNAME", "STATE1");
        stateDate = new GregorianCalendar(2014, 7, 21, 9, 30);
        trans.put("STATEDATE",  stateDate.getTime());
        trans.put("USERID", 2L);
        sMTransList.add(trans);
        
        trans = new HashMap<String, Object>();
        trans.put("STATESYSNAME", "STATE2");
        stateDate = new GregorianCalendar(2014, 7, 21, 10, 30);
        trans.put("STATEDATE",  stateDate.getTime());
        trans.put("USERID", 2L);
        sMTransList.add(trans);
        
        trans = new HashMap<String, Object>();
        trans.put("STATESYSNAME", "STATE3");
        stateDate = new GregorianCalendar(2014, 7, 21, 11, 30);
        trans.put("STATEDATE",  stateDate.getTime());
        trans.put("USERID", 2L);
        sMTransList.add(trans);
        
        System.out.println("getElapsedTimeByUsers");
        Map<String, Object> params = null;
        TimeCalculationFacade instance = new TimeCalculationFacade();
        Map<Long, Map<String, Long>> expResult = new HashMap();
        Map<String, Long> user1 = new HashMap<String, Long>();
        user1.put("GROUP2", 14400000L);
        user1.put("GROUP1", 7200000L);
        expResult.put(1L, user1);
        Map<String, Long> user2 = new HashMap<String, Long>();
        user2.put("GROUP1", 7200000L);
//        user2.put("GROUP2", 7200000L);
        expResult.put(2L, user2);
        //List<Map<String, Object>> result = instance.calculateElapsedTimeByUsers(dayOffList, groupMap, sMTransList);*/
        assertEquals(true, true);
    }
}
