/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

/**
 * Тип возвращаемого значения валидатором
 * @author reson
 */
public enum ValidatorResultType {
    /**
     * До первой встреченной обязательной ошибки.
     * Возвращается только эта ошибка.
     */
    FIRST_ERROR(0),
    /**
     * Возвращаются только ошибочные проверки,
     * в том числе и предупреждения.
     */
    ERRORS_ONLY(1),
    /**
     * Возвращает только ошибочные проверки,
     * в том числе и предупреждения.
     * В результат включается бин, 
     * при проверке которого возникла ошибка.
     */
    ERRORS_WITH_BEANS(2),
    
    ERRORS_AND_WARNINGS(3),
    ERRORS_AND_WARNINGS_WITH_BEANS(4),
    /**
     * Возвращаются все результаты
     */
    ALL_VALIDATION_RESULTS(5),
    ALL_VALIDATION_RESULTS_WITH_BEANS(6);
    
    private int resultType;
    
    
    
    private ValidatorResultType(int value){
        this.resultType=value;
    }
    
    
    public Long getResultType(){
        return Long.valueOf(resultType);
    }
    public static ValidatorResultType getValidatorResultTypeByResultType(int resultType){
        ValidatorResultType result;
        switch (resultType){
            case 0:{
                result = FIRST_ERROR;break;
            }
            case 1:{
                result = ERRORS_ONLY;break;
            }
            case 2:{
                result = ERRORS_WITH_BEANS; break;
            }
            case 3:{
                result = ALL_VALIDATION_RESULTS;break;
            }
            case 4:{
                result = ALL_VALIDATION_RESULTS_WITH_BEANS;break;
            }
            default:{
                result = FIRST_ERROR;
            }
        }
        return result;
    }
    
    public static ValidatorResultType getValidatorResultTypeByResultType(Long resultType){
        return getValidatorResultTypeByResultType(resultType.intValue());
    }
}
