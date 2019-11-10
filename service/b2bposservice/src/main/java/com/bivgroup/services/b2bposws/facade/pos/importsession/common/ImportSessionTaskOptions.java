package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryConstants;

public class ImportSessionTaskOptions implements DictionaryConstants {

    /** Имя сущности обрабатываемой сессии импорта */
    private String importSessionEntityName;
    /** Системное наименование состояния обрабатываемых сессий импорта */
    private String importSessionProcessedStateSysName;
    /** Системное наименование перехода в случае ошибке в ходе обработки файла сессии импорта */
    private String importSessionErrorTransSysName;
    /** Системное наименование перехода в случае успешного завершения обработки файла сессии импорта */
    private String importSessionSuccessTransSysName;
    /** Имя сущности содержимого обрабатываемой сессии импорта */
    private String importSessionContentEntityName;

    public ImportSessionTaskOptions(
            String importSessionEntityName,
            String importSessionProcessedStateSysName,
            String importSessionErrorTransSysName,
            String importSessionSuccessTransSysName,
            String importSessionContentEntityName
    ) {
        this.importSessionEntityName = importSessionEntityName;
        this.importSessionProcessedStateSysName = importSessionProcessedStateSysName;
        this.importSessionErrorTransSysName = importSessionErrorTransSysName;
        this.importSessionSuccessTransSysName = importSessionSuccessTransSysName;
        this.importSessionContentEntityName = importSessionContentEntityName;
    }

    public String getImportSessionEntityName() {
        return importSessionEntityName;
    }

    public String getImportSessionErrorTransSysName() {
        return importSessionErrorTransSysName;
    }

    public String getImportSessionProcessedStateSysName() {
        return importSessionProcessedStateSysName;
    }

    public String getImportSessionSuccessTransSysName() {
        return importSessionSuccessTransSysName;
    }

    public String getImportSessionContentEntityName() {
        return importSessionContentEntityName;
    }

}
