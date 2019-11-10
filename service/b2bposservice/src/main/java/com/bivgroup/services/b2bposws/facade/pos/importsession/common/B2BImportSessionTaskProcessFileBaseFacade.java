package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryCaller;
import com.bivgroup.services.b2bposws.system.files.FileReader;
import com.bivgroup.services.b2bposws.system.readers.common.ReaderColumn;
import com.bivgroup.services.b2bposws.system.readers.xlsx.ExcelFileTablePlainReader;
import com.bivgroup.services.b2bposws.system.readers.xlsx.ExcelFileTablePlainReaderException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bivgroup.services.b2bposws.system.readers.xlsx.ExcelFileTablePlainReader.ROW_FILE_INDEX_KEYNAME;

public abstract class B2BImportSessionTaskProcessFileBaseFacade extends B2BImportSessionTaskBaseFacade implements FileReader {

    /** Имя ключа, указывающего на список ошибок проверки исходнных данных для конкретного содержимого */
    protected static final String CONTENT_INVALID_DATA_ERROR_LIST_PARAMNAME = "errors";
    /** Имя ключа, указывающего на исключение преобразованное в запись списка ошибок проверки исходнных данных для конкретного содержимого */
    protected static final String CONTENT_INVALID_DATA_ERROR_EXCEPTION_PARAMNAME = "exception";
    /** Имя ключа, указывающего на текст ошибки (в записи списка ошибок проверки исходнных данных для конкретного содержимого) */
    protected static final String CONTENT_INVALID_DATA_ERROR_MSG_PARAMNAME = "message";

    /** Регулярное выражение для проверки номера договора */
    // todo: сабж
    protected static final String REGEXP_CONTRACT_NUMBER = "^.+$";
    /** Регулярное выражение для проверки табельного номера */
    protected static final String REGEXP_PERSONNEL_NUMBER = "^\\d+$";
    /** Регулярное выражение для проверки кода подразделения */
    protected static final String REGEXP_DEPARTMENT_CODE = "^\\d{3}$|^\\d{7}$|^\\d{9,12}$";

    protected abstract ReaderColumn[] getOrCreateReaderColumns(Map<String, Object> params);

    /** Обработка данных, специфических для типа содержимого (если возвращает false, запись не следует сохранять в БД) */
    protected abstract boolean processImportSessionContentSpecificData(
            Map<String, Object> importEntry, ImportSessionClassifierPack importSessionClassifierPack
    ) throws ImportSessionException;

    protected InputStream initInputStream(String fileName) throws ImportSessionException {
        InputStream inputStream;
        try {
            inputStream = tryReadFileFromSeaweedOrDirectory(fileName);
        } catch (Exception ex) {
            throw newImportSessionException(
                    "Ошибка при чтении файла с импортируемыми сведениями!",
                    "Unable to read file data by tryReadFileFromSeaweedOrDirectory!",
                    ex
            );
        }
        return inputStream;
    }

    protected String getFileNameFromImportSessionBinaryFileInfo(Long importSessionId) throws ImportSessionException {
        List<Map<String, Object>> binaryFileInfoList;
        try {
            String importSessionEntityName = getImportSessionTaskOptions().getImportSessionEntityName();
            binaryFileInfoList = dctGetBinaryFileInfo(importSessionEntityName, importSessionId);
            loggerDebugPretty(logger, "binaryFileInfoList", binaryFileInfoList);
        } catch (Exception ex) {
            throw newImportSessionException(
                    "Ошибка при получении сведений о файле сессии импорта!",
                    "Method doImportSessionProcess - unable to get binary file data by dctGetBinaryFileInfo!",
                    ex
            );
        }
        String fileName = "";
        if ((binaryFileInfoList != null) && (binaryFileInfoList.size() == 1)) {
            Map<String, Object> binaryFileInfo = binaryFileInfoList.get(0);
            loggerDebugPretty(logger, "binaryFileInfo", binaryFileInfo);
            fileName = getStringParamLogged(binaryFileInfo, "FILEPATH");
        }
        if (fileName.isEmpty()) {
            throw newImportSessionException(
                    "Ошибка при получении сведений о файле сессии импорта!",
                    "Method doImportSessionProcess - unable to get binary file data by dctGetBinaryFileInfo!"
            );
        }
        return fileName;
    }

    protected Map<String, Object> saveImportSessionContent(Map<String, Object> importSessionContent, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        Map<String, Object> importSessionContentSaved;
        String importSessionContentEntityName = getImportSessionTaskOptions().getImportSessionContentEntityName();
        // следуетт временно извлечь список с ошибками исходных данных (не должен обрабатываться словарной системой)
        Object errorList = null;
        if (importSessionContent != null) {
            errorList = importSessionContent.remove(CONTENT_INVALID_DATA_ERROR_LIST_PARAMNAME);
        }
        // сохранение с try / catch
        try {
            // создание записи содержимого
            loggerDebugPretty(logger, importSessionContentEntityName, importSessionContent);
            // long startMs = System.currentTimeMillis();
            if (dictionaryCaller == null) {
                importSessionContentSaved = dctCrudByHierarchy(importSessionContentEntityName, importSessionContent);
            } else {
                importSessionContentSaved = dctCrudByHierarchyWithoutTransactionControl(importSessionContentEntityName, importSessionContent, dictionaryCaller);
            }
            // logger.error(String.format("[NOT ERROR] dctCrudByHierarchy/dctCrudByHierarchyWithoutTransactionControl takes %d ms.", System.currentTimeMillis() - startMs));
            // todo: доп. проверки результата сохранения (например, наличие ИД и пр.)
        } catch (Exception ex) {
            importSessionContentSaved = null;
            throw newImportSessionException(
                    "Ошибка при сохранении сведений в базу данных на этапе импорта из файла!",
                    "Unable to save record to database by dctCrudByHierarchyWithoutTransactionControl!",
                    ex
            );
        }
        // следует дополнить результат сохранения временно извлеченным списком с ошибками исходных данных
        if ((importSessionContentSaved != null) && (errorList != null)) {
            importSessionContentSaved.put(CONTENT_INVALID_DATA_ERROR_LIST_PARAMNAME, errorList);
        }
        return importSessionContentSaved;
    }

    /*
    protected Map<String, Object> saveImportSessionContentProcessEvent(Map<String, Object> importSessionContent, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        String importSessionContentEntityName = getImportSessionTaskOptions().getImportSessionContentEntityName();
        Map<String, Object> savedRecord;
        try {
            // создание записи содержимого
            savedRecord = dctCrudByHierarchyWithoutTransactionControl(importSessionContentEntityName, importSessionContent, dictionaryCaller);
        } catch (Exception ex) {
            throw newImportSessionException(
                    "Ошибка при сохранении сведений в базу данных на этапе импорта из файла!",
                    "Unable to save record to database by dctCrudByHierarchyWithoutTransactionControl!",
                    ex
            );
        }
        // todo: доп. проверки результата сохранения (например, наличие ИД и пр.)
        return savedRecord;
    }
    */

    protected void closeInputStream(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ex) {
            // исключение не является критиеским, к тому же данные уже сохранены
            /*
            throw newImportSessionException(
                    "Ошибка при закрытии файла и освобождении ресурсов по завершению импорта!",
                    "Method doImportSessionProcess - unable to close input stream!",
                    ex
            );
            */
            logger.error("Unable to close input stream! Details (exception): ", ex);
        }
    }

    @Override
    protected Map<String, Object> doImportSessionProcess(Map<String, Object> importSession, Map<String, Object> params, ImportSessionTaskDetails taskDetails) throws ImportSessionException {
        logger.debug("doImportSessionProcess...");
        loggerDebugPretty(logger, "doImportSessionProcess importSession", importSession);

        HashMap<String, Object> processResult = new HashMap<>();
        String importSessionEntityName = getImportSessionTaskOptions().getImportSessionEntityName();
        processResult.put(importSessionEntityName, importSession);

        // ИД сессии импорта
        Long importSessionId = getImportSessionId(importSession);
        // Имя файла
        String fileName = getFileNameFromImportSessionBinaryFileInfo(importSessionId);
        processResult.put("fileName", fileName);
        // инициализация потока
        InputStream inputStream = initInputStream(fileName);
        // инициализация "чтеца"
        // todo: возможность принимать описания столбцов из входных параметров
        ReaderColumn[] readerColumns = getOrCreateReaderColumns(params);
        ExcelFileTablePlainReader reader = initExcelFileTablePlainReader(inputStream, readerColumns);
        // счетчики записей
        long dataRowsCount = reader.getApproximateTotalRowsCount() - reader.getDataRowIndex();
        taskDetails.setRecordsTotal(dataRowsCount);
        taskDetails.reading.setItemsTotal(dataRowsCount);
        // инициализация мап справочников (для быстрого доступа к ИД для установки ссылок)
        ImportSessionClassifierPack importSessionClassifierPack = new ImportSessionClassifierPack();
        // создание транзакции словарной системы
        // создание транзакции словарной системы
        // в данный момент не используется, поскольку смена состояния сущности для которой не был выполнен коммит вызыает исключение вида
        // "com.bivgroup.core.aspect.exceptions.AspectException: aspect:Error AspectStateSM. Object (class com.bivgroup.imports.ImportSessionContentManagerContract, id 1812) not found"
        // todo: вернуть использование одной транзакции, когда будет возможность менять состояния не закрывая транзакцию
        // DictionaryCaller dictionaryCaller = initDictionaryCallerAndBeginTransaction();
        DictionaryCaller dictionaryCaller = null;
        // повторно используемые переменные
        boolean isSaveRequired; // флаг, указывающий на необходимость сохранения в БД (false - запись будет пропущена без сохранения в БД)
        // начало отсчета чтения
        taskDetails.reading.clear();
        taskDetails.reading.markStart();
        tryLogTaskDetails(taskDetails);
        try {
            // чтение по одной записи
            while (reader.hasNext()) {
                // long startMs = System.currentTimeMillis();
                Map<String, Object> importEntry = reader.next();
                // обработка данных, специфических для типа содержимого
                isSaveRequired = processImportSessionContentSpecificData(importEntry, importSessionClassifierPack);
                if (isSaveRequired) {
                    // системные свойства
                    genSystemAttributesWithoutAspect(importEntry, params);
                    // ИД родителя для связи
                    importEntry.put("importSessionId", importSessionId);
                    // события обработки (при создании записи необходимо внести запись в протокол обработки)
                    genProcessEvent(importEntry, params);
                    // сохранение
                    Map<String, Object> savedContent = saveImportSessionContent(importEntry, dictionaryCaller);
                    // смена состояния (условная в зависимости от проверки исходных сведений)
                    changeImportSessionContentStateToInProcessQueueIfValid(savedContent, dictionaryCaller);
                    // увеличение счетчика сохраняемых записей
                    taskDetails.commit.incItemsTotal();
                    // list.add(savedContent);
                }
                // увеличение счетчика обработанных записей
                taskDetails.incRecordsProcessed();
                taskDetails.reading.incItemsProcessed();
                // logger.error(String.format("[NOT ERROR] B2BImportSessionTaskProcessFileBaseFacade.doImportSessionProcess for one file entry takes %d ms.", System.currentTimeMillis() - startMs));
                tryLogTaskDetails(taskDetails);
            }
        } catch (Exception processingException) {
            // анализ исключения и выброс на уровень выше
            analyzeProcessingExceptionAndRollback(dictionaryCaller, processingException);
        } finally {
            // закрытие потока
            closeInputStream(inputStream);
            // окончание отсчета чтения
            taskDetails.reading.markFinish();
            forceLogTaskDetails(taskDetails);
        }
        // смена состояния сессии импорта
        changeImportSessionStateToSuccess(importSessionId, dictionaryCaller);
        // коммит (закрытие транзакции)
        try {
            commit(dictionaryCaller, taskDetails);
        } finally {
            // закрытие потока
            closeInputStream(inputStream);
        }
        // результат
        loggerDebugPretty(logger, "doImportSessionProcess processResult", processResult);
        logger.debug("doImportSessionProcess finished.");
        return processResult;
    }

    /** Смена состояния (условная в зависимости от проверки исходных сведений) */
    private void changeImportSessionContentStateToInProcessQueueIfValid(Map<String, Object> importSessionContent, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        List<Map<String, Object>> errorList = getListParam(importSessionContent, CONTENT_INVALID_DATA_ERROR_LIST_PARAMNAME);
        if ((errorList == null) || (errorList.isEmpty())) {
            // смена состояния (в зависимости от проверки исходных сведений)
            changeImportSessionContentStateToInProcessQueue(importSessionContent, dictionaryCaller);
        }
    }

    private void genProcessEvent(Map<String, Object> importEntry, Map<String, Object> params) {
        List<Map<String, Object>> events = getOrCreateListParam(importEntry, "events");
        if (events.isEmpty()) {
            String eventName;
            String eventNoteStr;
            List<Map<String, Object>> errorList = getListParam(importEntry, CONTENT_INVALID_DATA_ERROR_LIST_PARAMNAME);
            if ((errorList != null) && (!errorList.isEmpty())) {
                // todo: поменять тип события для ошибки исходных данных, после того как актуальное ФТ будет согласовано с клиентом
                eventName = IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME;
                StringBuilder eventNote = new StringBuilder();
                for (Map<String, Object> error : errorList) {
                    String errorMsg = getStringParam(error, CONTENT_INVALID_DATA_ERROR_MSG_PARAMNAME);
                    eventNote.append(errorMsg);
                    eventNote.append(" ");
                }
                if (eventNote.length() > 1) {
                    eventNote.setLength(eventNote.length() - 1);
                }
                if (eventNote.length() > 3999) {
                    eventNote.setLength(3990);
                    eventNote.append("...");
                }
                eventNoteStr = eventNote.toString();
            } else {
                eventName = IMPORT_SESSION_EVENT_TO_PROCESSING_ENTITY_NAME;
                eventNoteStr = null;
            }
            if (!eventName.isEmpty()) {
                Map<String, Object> event = makeNewProcessingEvent(eventName, eventNoteStr, params);
                events.add(event);
            }
        }
    }

    private Map<String, Object> makeNewProcessingEvent(String eventEntityName, String eventNote, Map<String, Object> params) {
        Map<String, Object> event = new HashMap<>();
        event.put(ENTITY_TYPE_NAME_FIELD_NAME, eventEntityName);
        if (eventNote != null) {
            event.put("note", eventNote);
        }
        genSystemAttributesWithoutAspect(event, params);
        return event;
    }

    protected ExcelFileTablePlainReader initExcelFileTablePlainReader(InputStream inputStream, ReaderColumn... readerColumns) throws ImportSessionException {
        ExcelFileTablePlainReader reader;
        try {
            reader = new ExcelFileTablePlainReader(logger, inputStream, readerColumns);
        } catch (ExcelFileTablePlainReaderException ex) {
            throw newImportSessionException(
                    "Ошибка при чтении файла с импортируемыми сведениями!",
                    "Unable to create ExcelFileTablePlainReader!",
                    ex
            );
        }
        return reader;
    }

    protected void addInvalidDataErrorExAndLogError(Map<String, Object> managerContract, ImportSessionException ex, String operationName) {
        logger.error(String.format("" +
                "Suppressed exception throwing during %s - " +
                "invalid data state will be used for this import session content record instead!" +
                "Details (suppressed exception):", operationName
        ), ex);
        addInvalidDataErrorEx(managerContract, ex);
    }

    /** Дополнение списка ошибок проверки исходнных данных */
    private void addInvalidDataErrorEx(Map<String, Object> content, ImportSessionException ex) {
        if ((content != null) && (ex != null)) {
            List<Map<String, Object>> errorList = getOrCreateListParam(content, CONTENT_INVALID_DATA_ERROR_LIST_PARAMNAME);
            Map<String, Object> error = new HashMap<String, Object>();
            error.put(CONTENT_INVALID_DATA_ERROR_MSG_PARAMNAME, ex.getMessageHumanized());
            error.put(CONTENT_INVALID_DATA_ERROR_EXCEPTION_PARAMNAME, ex);
            errorList.add(error);
        }
    }

    protected boolean checkIsValueInvalidByRegExp(Object value, String regExp, boolean allowNull) {
        boolean isInvalid = super.checkIsValueInvalidByRegExp(value, regExp, allowNull);
        if (isInvalid) {
            logger.error(String.format(
                    "Checking value '%s' by regular expression '%s' reported invalid value.",
                    value, regExp
            ));
        }
        return  isInvalid;
    }

    protected void cleanDepartmentCode(Map<String, Object> importEntry, String fieldRawName, String fieldName, ImportSessionClassifierPack importSessionClassifierPack, String... cleanRegExps) throws ImportSessionException {
        // исходное значение
        String departmentCode = getStringParam(importEntry, fieldRawName);
        // очистка по регулярным выражениям
        for (String cleanRegExp : cleanRegExps) {
            departmentCode = departmentCode.replaceAll(cleanRegExp, "");
        }
        // ФТ v1.0 содержит указания по учёту исключений при работе с кодами подразделений
        String classifierName = KIND_IMPORT_SESSION_DEPARTMENT_FILE_EXCEPTION_RULE_ENTITY_NAME;
        String valueReal = importSessionClassifierPack
                .get(classifierName)
                .getRecordFieldStringValueByFieldStringValue("valueFile", departmentCode, "valueReal");
        if (valueReal != null) {
            logger.debug(String.format(
                    "Department code '%s' readed from file will be replaced by code '%s' according to data in department code exceptions classifier (%s).",
                    departmentCode, valueReal, classifierName
            ));
            departmentCode = valueReal;
        }
        // очищенное значение
        importEntry.put(fieldName, departmentCode);
    }

    /** Проверка кода подразделения (при необходимости - с очисткой и добавлением ошибки в список ошибок) */
    protected void checkDepartmentCodeAndRemoveInvalidValueWithAddError(Map<String, Object> entity, String fieldName, ImportSessionClassifierPack importSessionClassifierPack) {
        String departmentCode = getStringParam(entity, fieldName);
        // ФТ v1.0: "Код подразделения - Обязательное поле."
        boolean isInvalid = checkIsValueInvalidByRegExp(departmentCode, REGEXP_DEPARTMENT_CODE, false);
        if (isInvalid) {
            removeInvalidValueWithAddError(
                    entity, fieldName, departmentCode,
                    "department code", "кода подразделения",
                    "only numbers (3, 7, 9-12 digits)", "только цифры (3, 7, 9-12 символов)",
                    "processing department code"
            );
        }
    }

    protected void removeInvalidValueWithAddError(
            Map<String, Object> entity, String fieldName, String invalidValue,
            String valueSubjectStrEng, String valueSubjectStrRus,
            String availableValuesStrEng, String availableValuesStrRus,
            String operationNameStrEng
    ) {
        Long rowIndex = getLongParamLogged(entity, ROW_FILE_INDEX_KEYNAME) + 1;
        String message = String.format(
                "Illegal value '%s' of %s was found in file at line number %d. Available values for this column is %s.",
                invalidValue, valueSubjectStrEng, rowIndex, availableValuesStrEng
        );
        String messageHumanized = String.format(
                "В строке %s файла обнаружено некорректное значение %s - '%s'. Допустимые значения для данного столбца - %s.",
                rowIndex, valueSubjectStrRus, invalidValue, availableValuesStrRus
        );
        ImportSessionException ex = newImportSessionException(
                messageHumanized,
                message
        );
        addInvalidDataErrorExAndLogError(entity, ex, operationNameStrEng);
        entity.remove(fieldName);
    }
}
