package com.bivgroup.services.b2bposws.facade.pos.importsession.managerdepartment.tasks;

import com.bivgroup.services.b2bposws.facade.pos.importsession.common.*;
import com.bivgroup.services.b2bposws.system.readers.common.ReaderColumn;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@BOName("B2BImportSessionManagerDepartmentTaskProcessFileCustom")
public class B2BImportSessionManagerDepartmentTaskProcessFileCustomFacade extends B2BImportSessionTaskProcessFileBaseFacade {

    protected static final String FILE_MANAGER_EMAIL_KEYNAME = MANAGER_EMAIL_KEYNAME;
    protected static final String FILE_DEPARTMENT_CODE_RAW_KEYNAME = "departmentCodeRaw";
    protected static final String FILE_DEPARTMENT_CODE_KEYNAME = "departmentCode";
    protected static final String FILE_MANAGER_ROLE_NAME_KEYNAME = "managerRoleName";
    protected static final String FILE_MANAGER_POSITION_NAME_KEYNAME = "managerPositionName";

    /** Количество потоков обработки (не должно превышать одного потока) */
    private static volatile int threadCount = 0;
    /** Подробные сведения о текущем состоянии импорта */
    private static volatile ImportSessionTaskDetails taskDetails = new ImportSessionTaskDetails();

    private ImportSessionTaskOptions importSessionTaskOptions = new ImportSessionTaskOptions(
            // Имя сущности обрабатываемой сессии импорта
            IMPORT_SESSION_MANAGER_DEPARTMENT_ENTITY_NAME,
            // Системное наименование состояния обрабатываемых сессий импорта
            B2B_IMPORTSESSION_INLOADQUEUE,
            // Системное наименование перехода в случае ошибке в ходе обработки файла сессии импорта
            B2B_IMPORTSESSION_FROM_INLOADQUEUE_TO_INVALID,
            // Системное наименование перехода в случае успешного завершения обработки файла сессии импорта
            B2B_IMPORTSESSION_FROM_INLOADQUEUE_TO_INPROCESSQUEUE,
            // Имя сущности содержимого обрабатываемой сессии импорта
            IMPORT_SESSION_CONTENT_MANAGER_DEPARTMENT_ENTITY_NAME
    );

    @Override
    protected ImportSessionTaskOptions getImportSessionTaskOptions() {
        return importSessionTaskOptions;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionManagerDepartmentTaskProcessFileGetDetails(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerDepartmentTaskProcessFileGetDetails start...");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = taskDetails.toMap();
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        logger.debug("dsB2BImportSessionManagerDepartmentTaskProcessFileGetDetails finished.");
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionManagerDepartmentTaskProcessFile(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerDepartmentTaskProcessFile start...");
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
        logger.debug("dsB2BImportSessionManagerDepartmentTaskProcessFile finished.");
        return result;
    }

    /*
    // согласно ФТ v0.5:
    @Override
    protected ReaderColumn[] getOrCreateReaderColumns(Map<String, Object> params) {
        // todo: возможность принимать описания столбцов из входных параметров
        // согласно ФТ v0.5:
        ReaderColumn[] readerColumns = {
                // ИД во внешней системе (???)
                new ReaderColumn("id_auto", "externalId", String.class),
                // Наименование роли клиентского менеджера в подразделении (-> ИД)
                new ReaderColumn("src_cd", "managerRoleName", String.class),
                // Табельный номер клиентского менеджера
                new ReaderColumn("tab_num", "managerPersonnelNumber", Long.class),
                // Адрес электронной почты клиентского менеджера
                new ReaderColumn("email_sap", "managerEMail", String.class),
                // ФИО клиентского менеджера
                new ReaderColumn("full_name", "managerFullName", String.class),
                // Дата приема на работу клиентского менеджера
                new ReaderColumn("hire_dt", "managerHireDate", Date.class),
                // Дата увольнения клиентского менеджера
                new ReaderColumn("dismissal_dt", "managerDismissalDate", Date.class),
                // Наименование должности клиентского менеджера (-> ИД)
                new ReaderColumn("position_name", "managerPositionName", String.class),
                // Код подразделения
                new ReaderColumn("actual_urf_cd", "departmentCodeRaw", String.class),
                // Код территориального банка
                new ReaderColumn("tb_cd", "territorialBankCode", String.class),
                // Код головного отделения
                new ReaderColumn("osb", "headBranchCode", String.class),
                // Город нахождения подразделения
                new ReaderColumn("city_name", "departmentCity", String.class),
                // Дата открытия подразделения
                new ReaderColumn("open_dt", "departmentOpenDate", Date.class),
                // Дата закрытия подразделения
                new ReaderColumn("close_dt", "departmentCloseDate", Date.class),
                // ИД подразделения во внешней системе (???)
                new ReaderColumn("crm_vsp_id", "departmentExternalId", String.class),
                // Наименование подразделения
                new ReaderColumn("vsp_name", "departmentName", String.class),
                // Наименование территориального банка
                new ReaderColumn("tb_name", "territorialBankName", String.class),
                // ИД территориального банка во внешней системе (???)
                new ReaderColumn("tb_id", "territorialBankExternalId", String.class)
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
                // Наименование роли клиентского менеджера в подразделении (-> ИД)
                new ReaderColumn("src_cd", FILE_MANAGER_ROLE_NAME_KEYNAME, String.class),
                // Табельный номер клиентского менеджера
                new ReaderColumn("tab_num", "managerPersonnelNumber", Long.class),
                // Адрес электронной почты клиентского менеджера
                new ReaderColumn("email_sap", FILE_MANAGER_EMAIL_KEYNAME, String.class),
                // ФИО клиентского менеджера
                new ReaderColumn("full_name", "managerFullName", String.class),
                // Дата приема на работу клиентского менеджера
                new ReaderColumn("hire_dt", "managerHireDate", Date.class),
                // Дата увольнения клиентского менеджера
                new ReaderColumn("dismissal_dt", "managerDismissalDate", Date.class),
                // Наименование должности клиентского менеджера (-> ИД)
                new ReaderColumn("position_name", FILE_MANAGER_POSITION_NAME_KEYNAME, String.class),
                // Код подразделения
                new ReaderColumn("actual_urf_cd", FILE_DEPARTMENT_CODE_RAW_KEYNAME, String.class),
        };
        return readerColumns;
    }

    @Override
    protected boolean processImportSessionContentSpecificData(Map<String, Object> importEntry, ImportSessionClassifierPack importSessionClassifierPack) throws ImportSessionException {
        // флаг, указывающий на необходимость сохранения в БД (false - запись будет пропущена без сохранения в БД)
        boolean isSaveRequired = true;
        // код подразделения
        processDepartmentCode(importEntry, importSessionClassifierPack);
        // роль
        processManagerRole(importEntry, importSessionClassifierPack);
        // должность
        processManagerPosition(importEntry, importSessionClassifierPack);
        // ФТ: "E-mail: *@*.*"
        processManagerEMail(importEntry, FILE_MANAGER_EMAIL_KEYNAME);
        // результат
        return isSaveRequired;
    }

    private void processDepartmentCode(Map<String, Object> managerContract, ImportSessionClassifierPack importSessionClassifierPack) throws ImportSessionException {
        // код подразделения
        // ФТ: "подчёркивания надо убирать"
        cleanDepartmentCode(managerContract, FILE_DEPARTMENT_CODE_RAW_KEYNAME, FILE_DEPARTMENT_CODE_KEYNAME, importSessionClassifierPack, "_");
        // проверка кода подразделения (при необходимости - с очисткой и добавлением ошибки в список ошибок)
        checkDepartmentCodeAndRemoveInvalidValueWithAddError(managerContract, FILE_DEPARTMENT_CODE_KEYNAME, importSessionClassifierPack);
    }

    private void processManagerRole(Map<String, Object> managerContract, ImportSessionClassifierPack importSessionClassifierPack) throws ImportSessionException {
        String managerRoleName = getStringParam(managerContract, FILE_MANAGER_ROLE_NAME_KEYNAME);
        // ФТ v1.0: "Роль КМ в ВСП - Обязательное поле."
        ImportSessionClassifier managerRoleCls = importSessionClassifierPack.get(KIND_IMPORT_SESSION_MANAGER_ROLE_ENTITY_NAME);
        Long managerRoleId = managerRoleCls.getRecordFieldLongValueByFieldStringValue("name", managerRoleName, "id");
        if (managerRoleId == null) {
            String availableValuesStr = managerRoleCls.makeStringValuesListStr("name");
            removeInvalidValueWithAddError(
                    managerContract, "managerRoleId", managerRoleName,
                    "manager role in department", "роли менеджера в подразделении",
                    availableValuesStr, availableValuesStr,
                    "processing manager role"
            );
        } else {
            managerContract.put("managerRoleId", managerRoleId);
        }
    }

    private void processManagerPosition(Map<String, Object> managerContract, ImportSessionClassifierPack importSessionClassifierPack) throws ImportSessionException {
        String managerPositionName = getStringParam(managerContract, FILE_MANAGER_POSITION_NAME_KEYNAME);
        if (managerPositionName.isEmpty()) {
            // ФТ v1.0: "Позиция КМ в ВСП - Необязательное поле."
            return;
        }
        ImportSessionClassifier departmentTypeCls = importSessionClassifierPack.get(KIND_IMPORT_SESSION_MANAGER_POSITION_ENTITY_NAME);
        Long managerPositionId = departmentTypeCls.getRecordFieldLongValueByFieldStringValue("name", managerPositionName, "id");
        if (managerPositionId == null) {
            String availableValuesStr = departmentTypeCls.makeStringValuesListStr("name");
            removeInvalidValueWithAddError(
                    managerContract, "managerPositionId", managerPositionName,
                    "manager position in department", "должности менеджера в подразделении",
                    availableValuesStr, availableValuesStr,
                    "processing manager position"
            );
        } else {
            managerContract.put("managerPositionId", managerPositionId);
        }
    }

    /** ФТ: "E-mail: *@*.*" */
    protected void processManagerEMail(Map<String, Object> entity, String fieldName) {
        String managerEMail = getStringParam(entity, fieldName);
        if (managerEMail.isEmpty()) {
            // ФТ v1.0: "Адрес e-mail - Необязательное поле."
            return;
        }
        boolean isInvalid = checkIsValueInvalidByRegExp(managerEMail, REGEXP_MANAGER_FILE_VALID_EMAIL, true);
        if (isInvalid) {
            removeInvalidValueWithAddError(
                    entity, fieldName, managerEMail,
                    "manager e-mail", "адреса электронной почты менеджера",
                    "expressions like '*@*.*' (where '*' is any symbols)",
                    "выражения вида '*@*.*' (где '*' - любые символы)",
                    "processing manager e-mail"
            );
        }
    }

}
