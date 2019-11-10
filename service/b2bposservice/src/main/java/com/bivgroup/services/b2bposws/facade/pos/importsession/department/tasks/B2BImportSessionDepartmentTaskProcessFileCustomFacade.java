package com.bivgroup.services.b2bposws.facade.pos.importsession.department.tasks;

import com.bivgroup.services.b2bposws.facade.pos.importsession.common.*;
import com.bivgroup.services.b2bposws.system.readers.common.ReaderColumn;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;

@BOName("B2BImportSessionDepartmentTaskProcessFileCustom")
public class B2BImportSessionDepartmentTaskProcessFileCustomFacade extends B2BImportSessionTaskProcessFileBaseFacade {

    public static final String FILE_DEPARTMENT_CODE_RAW_KEYNAME = "codeRaw";
    public static final String FILE_DEPARTMENT_CODE_KEYNAME = "code";
    public static final String FILE_PARENT_DEPARTMENT_CODE_RAW_KEYNAME = "parentCodeRaw";
    public static final String FILE_PARENT_DEPARTMENT_CODE_KEYNAME = "parentCode";
    /** Количество потоков обработки (не должно превышать одного потока) */
    private static volatile int threadCount = 0;
    /** Подробные сведения о текущем состоянии импорта */
    private static volatile ImportSessionTaskDetails taskDetails = new ImportSessionTaskDetails();

    private ImportSessionTaskOptions importSessionTaskOptions = new ImportSessionTaskOptions(
            // Имя сущности обрабатываемой сессии импорта
            IMPORT_SESSION_DEPARTMENT_ENTITY_NAME,
            // Системное наименование состояния обрабатываемых сессий импорта
            B2B_IMPORTSESSION_INLOADQUEUE,
            // Системное наименование перехода в случае ошибке в ходе обработки файла сессии импорта
            B2B_IMPORTSESSION_FROM_INLOADQUEUE_TO_INVALID,
            // Системное наименование перехода в случае успешного завершения обработки файла сессии импорта
            B2B_IMPORTSESSION_FROM_INLOADQUEUE_TO_INPROCESSQUEUE,
            // Имя сущности содержимого обрабатываемой сессии импорта
            IMPORT_SESSION_CONTENT_DEPARTMENT_ENTITY_NAME
    );

    @Override
    protected ImportSessionTaskOptions getImportSessionTaskOptions() {
        return importSessionTaskOptions;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionDepartmentTaskProcessFileGetDetails(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionDepartmentTaskProcessFileGetDetails start...");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = taskDetails.toMap();
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        logger.debug("dsB2BImportSessionDepartmentTaskProcessFileGetDetails finished.");
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionDepartmentTaskProcessFile(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionDepartmentTaskProcessFile start...");
        Map<String, Object> result = new HashMap<String, Object>();
        if (threadCount == 0) {
            threadCount = 1;
            taskDetails.clear();
            try {
                result = doImportSessionListProcess(params, taskDetails);
            } finally {
                threadCount = 0;
                taskDetails.markFinish();
            }
        } else {
            logger.debug("doImportSessionListProcess already run!");
        }
        logger.debug("dsB2BImportSessionDepartmentTaskProcessFile finished.");
        return result;
    }

    /*
    // согласно ФТ v0.5:
    @Override
    protected ReaderColumn[] getOrCreateReaderColumns(Map<String, Object> params) {
        // todo: возможность принимать описания столбцов из входных параметров
        // согласно ФТ v0.5:
        ReaderColumn[] readerColumns = {
                new ReaderColumn("Код подразделения", "code", String.class),
                new ReaderColumn("Код головного подразделения", "parentCode", String.class),
                new ReaderColumn("Наименование подразделения", "name", String.class),
                new ReaderColumn("Тип подразделения", "typeName", String.class),
                new ReaderColumn("Населенный пункт", "locality", String.class),
                new ReaderColumn("Сегменты по обслуживанию ФЛ", "segmentNames", String.class),
                new ReaderColumn("Код субъекта РФ", "region", String.class)
        };
        return readerColumns;
    }
    */

    // согласно ФТ v0.6:
    @Override
    protected ReaderColumn[] getOrCreateReaderColumns(Map<String, Object> params) {
        // todo: возможность принимать описания столбцов из входных параметров
        // согласно ФТ v0.6:
        ReaderColumn[] readerColumns = {
                // Код подразделения
                new ReaderColumn("Код подразделения", FILE_DEPARTMENT_CODE_RAW_KEYNAME, String.class),
                // Код родительского подразделения
                new ReaderColumn("Код головного подразделения", FILE_PARENT_DEPARTMENT_CODE_RAW_KEYNAME, String.class),
                // Наименование подразделения
                new ReaderColumn("Наименование подразделения", "name", String.class),
                // Тип подразделения
                new ReaderColumn("Тип подразделения", "typeName", String.class),
                // Название населённого пункта, в котором находится подразделение
                new ReaderColumn("Населенный пункт", "locality", String.class),
                // Сегмент обслуживания
                new ReaderColumn("Сегменты по обслуживанию ФЛ", "segmentNames", String.class),
                // Наименование субъекта РФ
                new ReaderColumn("Код субъекта РФ", "region", String.class)
        };
        return readerColumns;
    }

    @Override
    protected boolean processImportSessionContentSpecificData(Map<String, Object> importEntry, ImportSessionClassifierPack importSessionClassifierPack) throws ImportSessionException {
        // флаг, указывающий на необходимость сохранения в БД (false - запись будет пропущена без сохранения в БД)
        boolean isSaveRequired = true;
        // коды подразделений
        // ФТ v1.0: "Пробелы в строке удалять."
        cleanDepartmentCodes(importEntry, importSessionClassifierPack);
        // проверка необходимости пропуска подразделения (для исключения не подлежащих импорту строк)
        isSaveRequired = !isDepartmentSkipped(importEntry);
        if (isSaveRequired) {
            // проверка кодов подразделений (при необходимости - с очисткой и добавлением ошибки в список ошибок)
            processDepartmentCodes(importEntry, importSessionClassifierPack);
            // тип
            processDepartmentType(importEntry, importSessionClassifierPack);
            // сегменты
            processDepartmentSegments(importEntry, importSessionClassifierPack);
        }
        // результат
        return isSaveRequired;
    }

    private void cleanDepartmentCodes(Map<String, Object> importEntry, ImportSessionClassifierPack importSessionClassifierPack) throws ImportSessionException {
        // код подразделения
        // ФТ v1.0: "Пробелы в строке удалять."
        cleanDepartmentCode(importEntry, FILE_DEPARTMENT_CODE_RAW_KEYNAME, FILE_DEPARTMENT_CODE_KEYNAME, importSessionClassifierPack, "\\s");
        // код родительского подразделения
        // ФТ v1.0: "Пробелы в строке удалять."
        cleanDepartmentCode(importEntry, FILE_PARENT_DEPARTMENT_CODE_RAW_KEYNAME, FILE_PARENT_DEPARTMENT_CODE_KEYNAME, importSessionClassifierPack, "\\s");
    }

    private void processDepartmentCodes(Map<String, Object> importEntry, ImportSessionClassifierPack importSessionClassifierPack) {
        // проверка кода подразделения (при необходимости - с очисткой и добавлением ошибки в список ошибок)
        checkDepartmentCodeAndRemoveInvalidValueWithAddError(importEntry, FILE_DEPARTMENT_CODE_KEYNAME, importSessionClassifierPack);
        // проверка кода родительского подразделения (при необходимости - с очисткой и добавлением ошибки в список ошибок)
        String parentDepartmentCode = getStringParam(importEntry, FILE_PARENT_DEPARTMENT_CODE_KEYNAME);
        if (!parentDepartmentCode.isEmpty()) {
            // кода родительского подразделения - необязательное поле, записи с пустым кодом родительского подразделения пропускаются
            checkDepartmentCodeAndRemoveInvalidValueWithAddError(importEntry, FILE_PARENT_DEPARTMENT_CODE_KEYNAME, importSessionClassifierPack);
        }
    }

    /** Проверка необходимости пропуска подразделения (для исключения не подлежащих импорту строк) */
    protected boolean isDepartmentSkipped(Map<String, Object> department) {
        // ФТ: "Строки с пустым значением кода головного подразделения не подлежать импорту,"
        // ФТ: "кроме строки с собственным кодом подразделения = 099 (ПАО Сбербанк)"
        String departmentCode = getStringParam(department, FILE_DEPARTMENT_CODE_KEYNAME);
        String parentDepartmentCode = getStringParam(department, FILE_PARENT_DEPARTMENT_CODE_KEYNAME);
        boolean isDepartmentSkipped = parentDepartmentCode.isEmpty() && (!PROCESSING_ROOT_DEPT_CODE.equals(departmentCode));
        return isDepartmentSkipped;
    }

    private void processDepartmentType(Map<String, Object> department, ImportSessionClassifierPack importSessionClassifierPack) throws ImportSessionException {
        String fieldName = "typeName";
        String typeName = getStringParam(department, fieldName);
        if (!typeName.isEmpty()) {
            ImportSessionClassifier departmentTypeCls = importSessionClassifierPack.get(KIND_IMPORT_SESSION_DEPARTMENT_TYPE_ENTITY_NAME);
            Long typeId = departmentTypeCls.getRecordFieldLongValueByFieldStringValue("name", typeName, "id");
            if (typeId == null) {
                String availableValuesStr = departmentTypeCls.makeStringValuesListStr("name");
                removeInvalidValueWithAddError(
                        department, "typeId", typeName,
                        "department type", "типа подразделения",
                        availableValuesStr, availableValuesStr,
                        "processing department type"
                );
                /*
                Long rowIndex = getLongParamLogged(department, ROW_DATA_INDEX_KEYNAME) + 1;
                String availableValuesStr = departmentTypeCls.makeStringValuesListStr("name");
                String message = String.format(
                        "Illegal value '%s' of department type was found in file at line number %d. Available values for this column is %s.",
                        typeName, rowIndex, availableValuesStr
                );
                String messageHumanized = String.format(
                        "В строке %s файла обнаружено некорректное значение типа подразделения - '%s'. Допустимые значения для данного столбца - %s.",
                        rowIndex, typeName, availableValuesStr
                );
                ImportSessionException ex = newImportSessionException(
                        messageHumanized,
                        message
                );
                String operationName = "processing department type";
                addInvalidDataErrorExAndLogError(department, ex, operationName);
                */
            } else {
                department.put("typeId", typeId);
            }
        }
    }

    private void processDepartmentSegments(Map<String, Object> department, ImportSessionClassifierPack importSessionClassifierPack) throws ImportSessionException {
        String segmentNames = getStringParam(department, "segmentNames");
        if (!segmentNames.isEmpty()) {
            String[] segmentNamesArray = segmentNames.split("\\|");
            Set<String> segmentSet = new HashSet<>();
            for (String segmentName : segmentNamesArray) {
                if (!segmentName.isEmpty()) {
                    segmentSet.add(segmentName);
                }
            }
            if (!segmentSet.isEmpty()) {
                List<Map<String, Object>> segmentList = new ArrayList<>();
                ImportSessionClassifier departmentSegmentCls = importSessionClassifierPack.get(KIND_IMPORT_SESSION_DEPARTMENT_SEGMENT_ENTITY_NAME);
                for (String segmentName : segmentSet) {
                    Map<String, Object> segmentMap = new HashMap<>();
                    Long segmentId = departmentSegmentCls.getRecordFieldLongValueByFieldStringValue("name", segmentName, "id");
                    if (segmentId == null) {
                        String availableValuesStr = departmentSegmentCls.makeStringValuesListStr("name");
                        removeInvalidValueWithAddError(
                                department, "segments", segmentName,
                                "department segment", "сегмента подразделения",
                                availableValuesStr + " (or any combination of them separated by '|')",
                                availableValuesStr + " (или любая их комбинация, разделенная символом '|')",
                                "processing department segment"
                        );
                        /*
                        Long rowIndex = getLongParamLogged(department, ROW_DATA_INDEX_KEYNAME) + 1;
                        String availableValuesStr = departmentSegmentCls.makeStringValuesListStr("name");
                        String message = String.format(
                                "Illegal value '%s' of segment was found in file at line number %d. " +
                                        "Available values for this column is %s (or any combination of them separated by '|').",
                                segmentName, rowIndex, availableValuesStr
                        );
                        String messageHumanized = String.format(
                                "В строке %s файла обнаружено некорректное значение сегмента - '%s'. " +
                                        "Допустимые значения для данного столбца - %s (или любая их комбинация, разделенная символом '|').",
                                rowIndex, segmentName, availableValuesStr
                        );
                        ImportSessionException ex = newImportSessionException(
                                messageHumanized,
                                message
                        );
                        String operationName = "processing department segment";
                        addInvalidDataErrorExAndLogError(department, ex, operationName);
                        */
                    }
                    segmentMap.put("segmentId", segmentId);
                    segmentList.add(segmentMap);
                }
                department.put("segments", segmentList);
            }
        }
    }

}
