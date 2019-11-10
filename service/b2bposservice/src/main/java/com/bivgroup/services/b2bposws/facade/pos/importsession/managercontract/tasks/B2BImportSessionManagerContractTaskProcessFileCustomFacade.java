package com.bivgroup.services.b2bposws.facade.pos.importsession.managercontract.tasks;

import com.bivgroup.services.b2bposws.facade.pos.importsession.common.*;
import com.bivgroup.services.b2bposws.system.readers.common.ReaderColumn;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;

@BOName("B2BImportSessionManagerContractTaskProcessFile")
public class B2BImportSessionManagerContractTaskProcessFileCustomFacade extends B2BImportSessionTaskProcessFileBaseFacade {

    /** Количество потоков обработки (не должно превышать одного потока) */
    private static volatile int threadCount = 0;
    /** Подробные сведения о текущем состоянии импорта */
    private static volatile ImportSessionTaskDetails taskDetails = new ImportSessionTaskDetails();

    private ImportSessionTaskOptions importSessionTaskOptions = new ImportSessionTaskOptions(
            // Имя сущности обрабатываемой сессии импорта
            IMPORT_SESSION_MANAGER_CONTRACT_ENTITY_NAME,
            // Системное наименование состояния обрабатываемых сессий импорта
            B2B_IMPORTSESSION_INLOADQUEUE,
            // Системное наименование перехода в случае ошибке в ходе обработки файла сессии импорта
            B2B_IMPORTSESSION_FROM_INLOADQUEUE_TO_INVALID,
            // Системное наименование перехода в случае успешного завершения обработки файла сессии импорта
            B2B_IMPORTSESSION_FROM_INLOADQUEUE_TO_INPROCESSQUEUE,
            // Имя сущности содержимого обрабатываемой сессии импорта
            IMPORT_SESSION_CONTENT_MANAGER_CONTRACT_ENTITY_NAME
    );

    @Override
    protected ImportSessionTaskOptions getImportSessionTaskOptions() {
        return importSessionTaskOptions;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionManagerContractTaskProcessFileGetDetails(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerContractTaskProcessFileGetDetails start...");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = taskDetails.toMap();
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        logger.debug("dsB2BImportSessionManagerContractTaskProcessFileGetDetails finished.");
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionManagerContractTaskProcessFile(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerContractTaskProcessFile start...");
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
        logger.debug("dsB2BImportSessionManagerContractTaskProcessFile finished.");
        return result;
    }

    /*
    // согласно ФТ v0.5:
    @Override
    protected ReaderColumn[] getOrCreateReaderColumns(Map<String, Object> params) {
        // todo: возможность принимать описания столбцов из входных параметров
        // согласно ФТ v0.5:
        ReaderColumn[] readerColumns = {
                // Номер договора
                new ReaderColumn("agreement_num", "contractNumber", String.class),
                // Код клиентского менеджера (???)
                new ReaderColumn("CODE_KM", "managerCode", String.class),
                // Табельный номер клиентского менеджера
                new ReaderColumn("TAB_NUM", "managerPersonnelNumber", Long.class)
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
                // Номер договора
                new ReaderColumn("agreement_num", "contractNumber", String.class),
                // Табельный номер клиентского менеджера
                new ReaderColumn("TAB_NUM", "managerPersonnelNumber", Long.class)
        };
        return readerColumns;
    }

    @Override
    protected boolean processImportSessionContentSpecificData(Map<String, Object> importEntry, ImportSessionClassifierPack importSessionClassifierPack) throws ImportSessionException {
        // флаг, указывающий на необходимость сохранения в БД (false - запись будет пропущена без сохранения в БД)
        boolean isSaveRequired = true;
        // todo: сабж
        // проверка номера договора (при необходимости - с очисткой и добавлением ошибки в список ошибок)
        checkContractNumberAndRemoveInvalidValueWithAddError(importEntry, "contractNumber");
        // проверка табельного номера (при необходимости - с очисткой и добавлением ошибки в список ошибок)
        checkPersonnelNumberAndRemoveInvalidValueWithAddError(importEntry, "managerPersonnelNumber");
        // результат
        return isSaveRequired;
    }

    /** Проверка кода подразделения (при необходимости - с очисткой и добавлением ошибки в список ошибок) */
    protected void checkContractNumberAndRemoveInvalidValueWithAddError(Map<String, Object> entity, String fieldName) {
        String contractNumber = getStringParam(entity, fieldName);
        boolean isInvalid = checkIsValueInvalidByRegExp(contractNumber, REGEXP_CONTRACT_NUMBER, false);
        if (isInvalid) {
            removeInvalidValueWithAddError(
                    entity, fieldName, contractNumber,
                    "contract number", "номера договора",
                    "only numbers and allowed special symbols («.», «№», «-», «/»)",
                    "только цифры и символы «.», «№», «-», «/»",
                    "processing contract number"
            );
            /*
            Long rowIndex = getLongParamLogged(entity, ROW_DATA_INDEX_KEYNAME) + 1;
            String message = String.format(
                    "Illegal value '%s' of contract number was found in file at line number %d. Available values for this column is %s.",
                    contractNumber, rowIndex, "only numbers and allowed special symbols («.», «№», «-», «/»)"
            );
            String messageHumanized = String.format(
                    "В строке %s файла обнаружено некорректное значение номера договора - '%s'. Допустимые значения для данного столбца - %s.",
                    rowIndex, contractNumber, "только цифры и символы «.», «№», «-», «/»"
            );
            ImportSessionException ex = newImportSessionException(
                    messageHumanized,
                    message
            );
            String operationName = "processing department code";
            addInvalidDataErrorExAndLogError(entity, ex, operationName);
            entity.remove(fieldName);
            */
        }
    }

    /** Проверка табельного номера (при необходимости - с очисткой и добавлением ошибки в список ошибок) */
    protected void checkPersonnelNumberAndRemoveInvalidValueWithAddError(Map<String, Object> entity, String fieldName) {
        String personnelNumber = getStringParam(entity, fieldName);
        boolean isInvalid = checkIsValueInvalidByRegExp(personnelNumber, REGEXP_PERSONNEL_NUMBER, false);
        if (isInvalid) {
            removeInvalidValueWithAddError(
                    entity, fieldName, personnelNumber,
                    "personnel number", "табельного номера",
                    "only numbers", "только цифры",
                    "processing personnel number"
            );
        }
    }

}
