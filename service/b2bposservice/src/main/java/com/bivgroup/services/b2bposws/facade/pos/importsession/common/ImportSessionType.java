package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

/**
 * Типы сессий импорта
 * <p>
 * В связи с тем, что @Discriminator реализует только значения
 * типа long, эта проблема решается этим перечислением
 *
 * @author Ivanov Roman
 */
public enum ImportSessionType {

    IMPORT_SESSION_CONTENT_DEPARTMENT("ImportSessionContentDepartment"), // Содержимое сессии импорта 'Оргструктура'
    IMPORT_SESSION_CONTENT_MANAGER_CONTRACT("ImportSessionContentManagerContract"), // Содержимое сесси импорта 'КМ-договор'
    IMPORT_SESSION_CONTENT_MANAGER_DEPARTMENT("ImportSessionContentManagerDepartment"), // Содержимое сесси импорта 'КМ-ВСП'

    IMPORT_SESSION_DEPARTMENT("ImportSessionDepartment"), // Сессия импорта 'Оргструктура'
    IMPORT_SESSION_MANAGER_CONTRACT("ImportSessionManagerContract"), // Сессия импорта 'КМ-договор'
    IMPORT_SESSION_MANAGER_DEPARTMENT("ImportSessionManagerDepartment"); // Сессия импорта 'КМ-ВСП'
    private String type;

    ImportSessionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
