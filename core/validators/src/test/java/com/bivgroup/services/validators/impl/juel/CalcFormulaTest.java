/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl.juel;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author reson
 */
public class CalcFormulaTest {

    public CalcFormulaTest() {
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
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void TestAbs(){
        ExMapper mapper = new ExMapper();
        Map<String,Object> testMap = new HashMap();
        testMap.put("one", Double.valueOf(1));
        testMap.put("two", Double.valueOf(2));
        Object result = mapper.calcObjectFormula("${(Math:abs(one-two))}", testMap);
        if ((Double)result >0){
           System.out.println("result = true");
        }
    }
}
