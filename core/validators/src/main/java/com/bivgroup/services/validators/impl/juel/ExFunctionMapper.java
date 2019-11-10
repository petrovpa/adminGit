/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl.juel;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.el.FunctionMapper;
import org.apache.log4j.Logger;

/**
 * Маппер функций. Все замапленные функции должны быть static
 * и желательно находится в классе FunctionHolder.
 * @author reson
 */
public class ExFunctionMapper extends FunctionMapper {

    private Logger logger = Logger.getLogger(ExFunctionMapper.class);

    public static final String MATH_PREFIX = "Math";
    
    private  Method[] mathMethods = null;

    public ExFunctionMapper() {
        this.mathMethods = Math.class.getMethods();
        Arrays.sort(mathMethods, new Comparator<Method>() {

            private Integer getMethodFirstParamValue(Method o){
                Integer result = 0;
                Class<?>[] parameterTypes = o.getParameterTypes();
                Class<?> compareClass = null;
                if (parameterTypes.length >0){
                    compareClass = parameterTypes[0];
                } else {
                    compareClass = o.getReturnType();
                }
                if (double.class.equals(compareClass)){
                    result = 4;
                }
                if (float.class.equals(compareClass)){
                    result = 3;
                }
                if (long.class.equals(compareClass)){
                    result = 2;
                }
                if (int.class.equals(compareClass)){
                    result = 1;
                }
                return result;
            }
            @Override
            public int compare(Method o1, Method o2) {
                int result = 0;
                if (o1.getName().equals(o2.getName())){
                    result = this.getMethodFirstParamValue(o2).compareTo(this.getMethodFirstParamValue(o1));
                } else {
                    result = o1.getName().compareTo(o2.getName());
                }
                return result;
            }
        });
    }


    private Method getMinDoubleValueMethod() {
        Method result = null;
        try {
            result = FunctionsHolder.class.getMethod(FunctionsHolder.MINDOUBLEVALUE_METHOD_NAME, new Class<?>[]{List.class, String.class});
        } catch (NoSuchMethodException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [minDoubleValue]", ex);
        } catch (SecurityException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [minDoubleValue]", ex);
        }
        return result;
    }

    private Method getMaxDoubleValueMethod() {
        Method result = null;
        try {
            result = FunctionsHolder.class.getMethod(FunctionsHolder.MAXDOUBLEVALUE_METHOD_NAME, new Class<?>[]{List.class, String.class});
        } catch (NoSuchMethodException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [maxDoubleValue]", ex);
        } catch (SecurityException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [maxDoubleValue]", ex);
        }
        return result;
    }

    private Method getSumDoubleValueMethod() {
        Method result = null;
        try {
            result = FunctionsHolder.class.getMethod(FunctionsHolder.SUMDOUBLEVALUE_METHOD_NAME, new Class<?>[]{List.class, String.class});
        } catch (NoSuchMethodException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [maxDoubleValue]", ex);
        } catch (SecurityException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [maxDoubleValue]", ex);
        }
        return result;
    }



    private Method getCalcYearsMethod() {
        Method result = null;
        try {
            result = FunctionsHolder.class.getMethod(FunctionsHolder.CALCYEARS_METHOD_NAME,
                    new Class<?>[]{Date.class, Date.class});
        } catch (NoSuchMethodException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [calcYears]", ex);
        } catch (SecurityException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [calcYears]", ex);
        }
        return result;

    }

    private Method getCalcMonthsMethod() {
        Method result = null;
        try {
            result = FunctionsHolder.class.getMethod(FunctionsHolder.CALCMONTHS_METHOD_NAME,
                    new Class<?>[]{Date.class, Date.class});
        } catch (NoSuchMethodException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [calcMonths]", ex);
        } catch (SecurityException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [calcMonths]", ex);
        }
        return result;

    }

    private Method getCalcDaysMethod() {
        Method result = null;
        try {
            result = FunctionsHolder.class.getMethod(FunctionsHolder.CALCDAYS_METHOD_NAME,
                    new Class<?>[]{Date.class, Date.class});
        } catch (NoSuchMethodException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [calcDays]", ex);
        } catch (SecurityException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [calcDays]", ex);
        }
        return result;

    }

    private Method getAddDayMethod() {
        Method result = null;
        try {
            result = FunctionsHolder.class.getMethod(FunctionsHolder.ADDDAY_METHOD_NAME,
                    new Class<?>[]{Date.class, Integer.class});
        } catch (NoSuchMethodException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [addDay]", ex);
        } catch (SecurityException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [addDay]", ex);
        }
        return result;

    }

    private Method getRound2Method() {
        Method result = null;
        try {
            result = FunctionsHolder.class.getMethod(FunctionsHolder.ROUND2_METHOD_NAME,
                    new Class<?>[]{Double.class});
        } catch (NoSuchMethodException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [addDay]", ex);
        } catch (SecurityException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [addDay]", ex);
        }
        return result;

    }

    private Method getRoundXMethod() {
        Method result = null;
        try {
            result = FunctionsHolder.class.getMethod(FunctionsHolder.ROUNDX_METHOD_NAME,
                    new Class<?>[]{Integer.class, Double.class});
        } catch (NoSuchMethodException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [addDay]", ex);
        } catch (SecurityException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error("Can't find method [addDay]", ex);
        }
        return result;

    }

    private Method getMathMethod(String methodName){
        Method result = null;
        try {
            for (Method method:mathMethods){
                if (methodName.equals(method.getName())){
                    result = method;
                    break;
                }
            }
            
        } catch (SecurityException ex) {
            //Logger.getLogger(ExFunctionMapper.class.getName()).error( null, ex);
            logger.error(String.format("Can't find method [%s]",methodName), ex);
        }
        return result;        
    }
    
    @Override
    public Method resolveFunction(String prefix, String localName) {
        //logger.debug(String.format("ExFunctionMapperCall: prefix = %s localName = %s", prefix, localName));
        Method result = null;

        if (MATH_PREFIX.equals(prefix)){
            result = this.getMathMethod(localName);
        }
        if (localName.equals(FunctionsHolder.MINDOUBLEVALUE_METHOD_NAME)) {
            result = this.getMinDoubleValueMethod();
        }
        if (localName.equals(FunctionsHolder.MAXDOUBLEVALUE_METHOD_NAME)) {
            result = this.getMaxDoubleValueMethod();
        }
        if (localName.equals(FunctionsHolder.SUMDOUBLEVALUE_METHOD_NAME)) {
            result = this.getSumDoubleValueMethod();
        }

        if (localName.equals(FunctionsHolder.CALCYEARS_METHOD_NAME)) {
            result = this.getCalcYearsMethod();
        }
        if (localName.equals(FunctionsHolder.CALCMONTHS_METHOD_NAME)) {
            result = this.getCalcMonthsMethod();
        }
        if (localName.equals(FunctionsHolder.CALCDAYS_METHOD_NAME)) {
            result = this.getCalcDaysMethod();
        }
        if (localName.equals(FunctionsHolder.ADDDAY_METHOD_NAME)) {
            result = this.getAddDayMethod();
        }
        if (localName.equals(FunctionsHolder.ROUND2_METHOD_NAME)) {
            result = this.getRound2Method();
        }
        if (localName.equals(FunctionsHolder.ROUNDX_METHOD_NAME)) {
            result = this.getRoundXMethod();
        }
        return result;
    }
}
