package com.bivgroup.services.b2bposws.system.files;

import java.util.HashMap;
import java.util.Map;

/**
 * Константы и типы для бинарных файлов (которые регистрируются в INS_BINFILE)
 */
public class BinFileType {

    public static final String TYPE_ID_PARAMNAME = "FILETYPEID";
    public static final String TYPE_NAME_PARAMNAME = "FILETYPENAME";
    public static final String FILE_NAME_PARAMNAME = "FILENAME";

    private Long fileTypeId;
    private String fileTypeName;
    private String fileFormatDefault = ".pdf";

    private String fileNameDefault;

    // todo: константы заменить на получение данных из сервиса (когда/если будет введен справочник типов бинарных файлов)

    /** Заявление на выплату - формируемая в ЛК pdf, которая крепиться к уведомлению о событии (B2B_LOSSNOTICE) */
    public static final BinFileType LOSS_PAYMENT_CLAIM = new BinFileType(100000L, "Заявление на выплату");
    /** Заявление на изменение условий страхования - формируемая в ЛК pdf, которая крепиться к заявлению на изменение условий страхования (PD_DECLARATIONOFCHANGE) */
    public static final BinFileType CONTRACT_CHANGE_CLAIM = new BinFileType(200000L, "Заявление на изменение условий страхования");
    /** Заявление на отключение условий страхования - формируемая в ЛК pdf, которая крепиться к заявлению на изменение условий страхования (PD_DECLARATIONOFCHANGE) */
    public static final BinFileType CONTRACT_CHANGE_CANCEL_CLAIM = new BinFileType(200100L, "Отказ от опций фиксации");
    /** Подписанный страховой полис по договору страхования */
    public static final BinFileType CONTRACT_POLICY_SIGNED = new BinFileType(15L, "Полис подписанный");
    /** Файл выгрузки (формируемый в B2B из интерфейса выгрузки), который крепиться к записи о выгрузке (B2B_EXPORTDATA) для последующего скачивания */
    public static final BinFileType EXPORT_DATA_FILE = new BinFileType(1015L, "Готовый файл экспорта", ".xls");
    // todo: добавить остальные типы бинарных файлов

    public BinFileType(Long fileTypeId, String fileTypeName) {
        this(fileTypeId, fileTypeName, ".pdf");
    }

    public BinFileType(Long fileTypeId, String fileTypeName, String fileFormatDefault) {
        this.fileTypeId = fileTypeId;
        this.fileTypeName = fileTypeName;
        this.fileFormatDefault = fileFormatDefault;
        this.fileNameDefault = this.fileTypeName + this.fileFormatDefault;
    }

    public Long getFileTypeId() {
        return fileTypeId;
    }

    public String getFileTypeName() {
        return fileTypeName;
    }

    public String getFileNameDefault() {
        return fileNameDefault;
    }

    public Map<String, Object> getFileTypeMap() {
        Map<String, Object> fileTypeMap = new HashMap<String, Object>();
        fileTypeMap.put(TYPE_ID_PARAMNAME, getFileTypeId());
        fileTypeMap.put(TYPE_NAME_PARAMNAME, getFileTypeName());
        fileTypeMap.put(FILE_NAME_PARAMNAME, getFileNameDefault());
        return fileTypeMap;
    }

}
