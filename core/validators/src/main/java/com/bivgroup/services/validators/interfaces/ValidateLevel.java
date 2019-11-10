/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

/**
 * Уровень валидации. Определяет реакцию системы на отрицательный результат
 * проверки.
 *
 * @author reson
 */
public enum ValidateLevel {

    /**
     * Проверка выключена.
     */
    DISABLED(0),
    /**
     * Неудача проверки означает некритичную ошибку
     */
    WARNING(1),
    /**
     * Неудача проверки означает критичную ошибку
     */
    ERROR(2);
    private int level;

    private ValidateLevel(int level) {
        this.level = level;
    }

    
    public Long getLevel(){
        return Long.valueOf(level);
    }
    public static ValidateLevel getValidateLevelByLevel(int level) {
        ValidateLevel result;
        switch (level) {
            case 0: {
                result = ValidateLevel.DISABLED;
                break;
            }
            case 1: {
                result = ValidateLevel.WARNING;
                break;
            }
            case 2: {
                result = ValidateLevel.ERROR;
                break;
            }
            default: {
                result = ValidateLevel.DISABLED;
                break;
            }
        }
        return result;
    }

    public static ValidateLevel getValidateLevelByLevel(Long level) {
        
        ValidateLevel result = ValidateLevel.DISABLED;
        if (null != level){
            result = ValidateLevel.getValidateLevelByLevel(level.intValue());
        }
        return result; 
    }
}
