package com.bivgroup.services.b2bposws.facade;

import com.bivgroup.core.dictionary.dao.jpa.HierarchyDAO;
import com.bivgroup.core.dictionary.dao.jpa.JPADAOFactory;
import com.bivgroup.crm.Crm2;
import com.bivgroup.imports.ImportsHierarchyDAO;
import com.bivgroup.loss.Loss2;
import com.bivgroup.messages.Messages2;
import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryCaller;
import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryConstants;
import com.bivgroup.system.System2;
import com.bivgroup.termination.Termination2;
import com.bivgroup.underwriting.Underwriting2;

import java.util.*;


/**
 * @author ilich
 */
public class B2BDictionaryBaseFacade extends B2BBaseFacade implements DictionaryConstants {

    /**
     * Канал приема (ReceivingChannel) - системное наименование для канала
     * 'Личный кабинет'
     */
    protected static final String RECEIVING_CHANNEL_SYSNAME_LK = "LK";

    private static final Map<String, Class> daoClassByModulePrefix = new HashMap<String, Class>();

    // ИД типов документов для контрагента
    private static final Set<Long> CONTRAGENT_DOC_TYPE_IDS;
    // ИД типов адресов для контрагента
    private static final Set<Long> CONTRAGENT_ADDRESS_TYPE_IDS;
    // ИД типов контактов для контрагента
    private static final Set<Long> CONTRAGENT_CONTACT_TYPE_IDS;

    /**
     * ИД типа документов для контрагента: Водительское удостоверение
     * (DrivingLicence) - 1016L
     */
    // todo: константы ИД заменить на полуение ИД по сис. имени типа (когда будут готовы классификаторы)
    protected static final long CDM_DOCTYPE_ID_DRIVING_LICENCE = 1016L;// Водительское удостоверение (DrivingLicence)

    /**
     * Имя поля при сохранении события в БД для кода ошибки
     */
    protected static final String ERROR_CODE_EVENT_FIELDNAME = "errorCode";

    static {
        daoClassByModulePrefix.put(DCT_MODULE_PREFIX_CRM, Crm2.class);
        daoClassByModulePrefix.put(DCT_MODULE_PREFIX_TERMINATION, Termination2.class);
        daoClassByModulePrefix.put(DCT_MODULE_PREFIX_LOSS, Loss2.class);
        daoClassByModulePrefix.put(DCT_MODULE_PREFIX_MESSAGES, Messages2.class);
        daoClassByModulePrefix.put(DCT_MODULE_PREFIX_SYSTEM, System2.class);
        daoClassByModulePrefix.put(DCT_MODULE_PREFIX_IMPORTS, ImportsHierarchyDAO.class);
        daoClassByModulePrefix.put(DCT_MODULE_PREFIX_UNDERWRITING, Underwriting2.class);

        // ИД типов документов для контрагента
        // todo: константы ИД заменить на полуение ИД по сис. имени типа (когда будут готовы классификаторы)
        CONTRAGENT_DOC_TYPE_IDS = new HashSet<Long>();
        CONTRAGENT_DOC_TYPE_IDS.add(1001L); // Паспорт гражданина РФ (PassportRF)
        CONTRAGENT_DOC_TYPE_IDS.add(1004L); // Паспорт иностранного гражданина (ForeignPassport)
        CONTRAGENT_DOC_TYPE_IDS.add(CDM_DOCTYPE_ID_DRIVING_LICENCE); // Водительское удостоверение (DrivingLicence)

        // ИД типов адресов для контрагента
        // todo: константы ИД заменить на полуение ИД по сис. имени типа (когда будут готовы классификаторы)
        CONTRAGENT_ADDRESS_TYPE_IDS = new HashSet<Long>();
        CONTRAGENT_ADDRESS_TYPE_IDS.add(1003L); // Адрес регистрации (RegisterAddress)
        CONTRAGENT_ADDRESS_TYPE_IDS.add(1005L); // Адрес проживания (FactAddress)

        // ИД типов контактов для контрагента
        // todo: константы ИД заменить на полуение ИД по сис. имени типа (когда будут готовы классификаторы)
        CONTRAGENT_CONTACT_TYPE_IDS = new HashSet<Long>();
        CONTRAGENT_CONTACT_TYPE_IDS.add(1003L); // Домашний телефон (FactAddressPhone)
        CONTRAGENT_CONTACT_TYPE_IDS.add(1004L); // Рабочий телефон (WorkAddressPhone)
        CONTRAGENT_CONTACT_TYPE_IDS.add(1005L); // Мобильный телефон (MobilePhone)
        CONTRAGENT_CONTACT_TYPE_IDS.add(1006L); // Личный E-mail (Мобильный телефон)
    }

    protected Class getDAOClassByEntityName(String entityName) {
        Class daoClass = null;
        for (Map.Entry<String, Class> entry : daoClassByModulePrefix.entrySet()) {
            String modulePrefix = entry.getKey();
            if (entityName.startsWith(modulePrefix)) {
                daoClass = entry.getValue();
                break;
            }
        }
        if (daoClass == null) {
            logger.error(String.format("Unable to get DAO class by entity name ('%s')! Supported entity name's prfixes are %s.", entityName, daoClassByModulePrefix.keySet().toString()));
        }
        return daoClass;
    }

    protected DictionaryCaller getDictionaryCallerByEntityName(String entityName) {
        // long startMs = System.currentTimeMillis();
        JPADAOFactory jd = new JPADAOFactory();
        Class daoClass = getDAOClassByEntityName(entityName);
        HierarchyDAO hierarchyDAO = (HierarchyDAO) jd.getDAO(daoClass);
        DictionaryCaller dictionaryCaller = new DictionaryCaller(hierarchyDAO);
        // logger.error(String.format("[NOT ERROR] getDictionaryCallerByEntityName takes %d ms.", System.currentTimeMillis() - startMs));
        return dictionaryCaller;
    }

    protected DictionaryCaller getDictionaryCallerByEntityNameAndBeginTransaction(String entityName) throws Exception {
        DictionaryCaller dictionaryCaller = getDictionaryCallerByEntityName(entityName);
        try {
            dictionaryCaller.beginTransaction();
        } catch (Exception ex) {
            logger.error("Beginning new transaction caused exception! Details (exception): ", ex);
            throw ex;
        }
        return dictionaryCaller;
    }

    // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
    protected void parseDatesBeforeDictionaryCalls(List<Map<String, Object>> dataList) {
        if (dataList != null) {
            for (Map<String, Object> bean : dataList) {
                parseDates(bean, Date.class, KEY_NAME_DATE_SUFFIX_NEW, KEY_NAME_TIME_SUFFIX_NEW, true, false);
            }
        }
    }

    // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
    protected void parseDatesBeforeDictionaryCalls(Map<String, Object> data) {
        if (data != null) {
            parseDates(data, Date.class, KEY_NAME_DATE_SUFFIX_NEW, KEY_NAME_TIME_SUFFIX_NEW, true, false);
        }
    }

    // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
    protected void parseDatesAfterDictionaryCalls(List<Map<String, Object>> dataList) {
        if (dataList != null) {
            for (Map<String, Object> bean : dataList) {
                parseDates(bean, String.class, KEY_NAME_DATE_SUFFIX_NEW, KEY_NAME_TIME_SUFFIX_NEW, false, true);
            }
        }
    }

    // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
    protected void parseDatesAfterDictionaryCalls(Map<String, Object> data) {
        if (data != null) {
            parseDates(data, String.class, KEY_NAME_DATE_SUFFIX_NEW, KEY_NAME_TIME_SUFFIX_NEW, false, true);
        }
    }

    protected List<Map<String, Object>> dctFindByExample(String entityName, Map<String, Object> instance, DictionaryCaller dc) throws Exception {
        if (instance == null) {
            instance = new HashMap<String, Object>();
        }
        List<Map> rawResultList = dc.getDAO().findByExample(entityName, instance);
        // старая версия словарной системы выбрасывала исключение, если сущность не найдена
        // текущая (на момент написания комментария) версия новой словарной системы в таком случае возвращает null вместо результата не вызывая исключения
        // данный метод в качестве временного решения проверяет результат на null и выбрасывает исключение по аналогии со старой версией словарной системы
        //checkRawResultAndThrowExIfNull(rawResultList, "findByExample", entityName, instance);
        List<Map<String, Object>> resultList = dc.processReturnResult(rawResultList);
        return resultList;
    }

    protected List<Map<String, Object>> dctFindByExample(String entityName, Map<String, Object> instance) throws Exception {
        /*
        JPADAOFactory jd = new JPADAOFactory();
        DictionaryCaller dc = new DictionaryCaller((HierarchyDAO) jd.getDAO(Crm2.class));
         */
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityName);
        return dctFindByExample(entityName, instance, dc);
    }

    protected List<Map<String, Object>> dctFindByExample(String entityName, Map<String, Object> instance, boolean isParseDates) throws Exception {
        if (isParseDates) {
            // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
            parseDatesBeforeDictionaryCalls(instance);
        }
        List<Map<String, Object>> resultList = dctFindByExample(entityName, instance);
        if (isParseDates) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(resultList);
        }
        return resultList;
    }

    protected Map<String, Object> dctCrudByHierarchy(String entityName, Map<String, Object> instance, DictionaryCaller dc) throws Exception {
        if (instance == null) {
            instance = new HashMap<String, Object>();
        }
        dc.beginTransaction();
        Map<String, Object> resultMap;
        try {
            Map rawResultMap = dc.getDAO().crudByHierarchy(entityName, instance);
            // старая версия словарной системы выбрасывала исключение, если сущность не найдена
            // текущая (на момент написания комментария) версия новой словарной системы в таком случае возвращает null вместо результата не вызывая исключения
            // данный метод в качестве временного решения проверяет результат на null и выбрасывает исключение по аналогии со старой версией словарной системы
            checkRawResultAndThrowExIfNull(rawResultMap, "crudByHierarchy", entityName, instance);
            dc.commit();
            resultMap = dc.processReturnResult(rawResultMap);
        } catch (Exception ex) {
            logger.error("Method dctCrudByHierarchy caused exception: ", ex);
            dc.rollback();
            throw ex;
        }
        return resultMap;
    }

    protected Map<String, Object> dctCrudByHierarchyWithoutTransactionControl(String entityName, Map<String, Object> instance, DictionaryCaller dc) throws Exception {
        if (instance == null) {
            instance = new HashMap<String, Object>();
        }
        // dc.beginTransaction();
        Map<String, Object> resultMap;
        try {
            Map rawResultMap = dc.getDAO().crudByHierarchy(entityName, instance);
            // старая версия словарной системы выбрасывала исключение, если сущность не найдена
            // текущая (на момент написания комментария) версия новой словарной системы в таком случае возвращает null вместо результата не вызывая исключения
            // данный метод в качестве временного решения проверяет результат на null и выбрасывает исключение по аналогии со старой версией словарной системы
            checkRawResultAndThrowExIfNull(rawResultMap, "crudByHierarchy", entityName, instance);
            // dc.commit();
            resultMap = dc.processReturnResult(rawResultMap);
        } catch (Exception ex) {
            logger.error("Method dctCrudByHierarchy caused exception: ", ex);
            // dc.rollback();
            throw ex;
        }
        return resultMap;
    }

    protected Map<String, Object> dctCrudByHierarchy(String entityName, Map<String, Object> instance) throws Exception {
        /*
        JPADAOFactory jd = new JPADAOFactory();
        DictionaryCaller dc = new DictionaryCaller((HierarchyDAO) jd.getDAO(Crm2.class));
         */
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityName);
        Map<String, Object> resultMap = dctCrudByHierarchy(entityName, instance, dc);
        return resultMap;
    }

    protected Map<String, Object> dctCrudByHierarchy(String entityName, Map<String, Object> instance, boolean isParseDates) throws Exception {
        if (isParseDates) {
            // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
            parseDatesBeforeDictionaryCalls(instance);
        }
        Map<String, Object> resultMap = dctCrudByHierarchy(entityName, instance);
        if (isParseDates) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(resultMap);
        }
        return resultMap;
    }

    // аналог dctCrudByHierarchy, но для списка
    protected List<Map<String, Object>> dctCrudByHierarchy(String entityName, List<Map<String, Object>> instance, DictionaryCaller dc) throws Exception {
        if (instance == null) {
            instance = new ArrayList<Map<String, Object>>();
        }
        dc.beginTransaction();
        List<Map<String, Object>> resultList;
        try {
            List<Map> mInstance = new ArrayList<Map>(instance);
            List<Map> rawResultList = dc.getDAO().crudByHierarchy(entityName, mInstance);
            // старая версия словарной системы выбрасывала исключение, если сущность не найдена
            // текущая (на момент написания комментария) версия новой словарной системы в таком случае возвращает null вместо результата не вызывая исключения
            // данный метод в качестве временного решения проверяет результат на null и выбрасывает исключение по аналогии со старой версией словарной системы
            //checkRawResultAndThrowExIfNull(rawResultMap, "crudByHierarchy", entityName, instance);
            dc.commit();
            resultList = dc.processReturnResult(rawResultList);
        } catch (Exception ex) {
            logger.error("Method dctCrudByHierarchy caused exception: ", ex);
            dc.rollback();
            throw ex;
        }
        return resultList;
    }

    // аналог dctCrudByHierarchy, но для списка
    protected List<Map<String, Object>> dctCrudByHierarchy(String entityName, List<Map<String, Object>> instance) throws Exception {
        /*
        JPADAOFactory jd = new JPADAOFactory();
        DictionaryCaller dc = new DictionaryCaller((HierarchyDAO) jd.getDAO(Crm2.class));
         */
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityName);
        List<Map<String, Object>> resultList = dctCrudByHierarchy(entityName, instance, dc);
        return resultList;
    }

    // аналог dctCrudByHierarchy, но для списка
    protected List<Map<String, Object>> dctCrudByHierarchy(String entityName, List<Map<String, Object>> instance, boolean isParseDates) throws Exception {
        if (isParseDates) {
            // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
            parseDatesBeforeDictionaryCalls(instance);
        }
        List<Map<String, Object>> resultList = dctCrudByHierarchy(entityName, instance);
        if (isParseDates) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(resultList);
        }
        return resultList;
    }

    protected Map<String, Object> dctFindById(String entityName, Long id) throws Exception {
        Map<String, Object> resultMap;
        if (id == null) {
            resultMap = new HashMap<String, Object>();
        } else {
            /*
            JPADAOFactory jd = new JPADAOFactory();
            DictionaryCaller dc = new DictionaryCaller((HierarchyDAO) jd.getDAO(Crm2.class));
             */
            DictionaryCaller dc = getDictionaryCallerByEntityName(entityName);
            try {
                Map rawResultMap = dc.getDAO().findById(entityName, id);
                // старая версия словарной системы выбрасывала исключение, если сущность не найдена
                // текущая (на момент написания комментария) версия новой словарной системы в таком случае возвращает null вместо результата не вызывая исключения
                // данный метод в качестве временного решения проверяет результат на null и выбрасывает исключение по аналогии со старой версией словарной системы
                //checkRawResultAndThrowExIfNull(rawResultMap, "findById", entityName, null);
                resultMap = dc.processReturnResult(rawResultMap);
            } catch (Exception ex) {
                logger.error("Method dctFindById caused exception: ", ex);
                throw ex;
            }
        }
        return resultMap;
    }

    protected Map<String, Object> dctFindById(String entityName, Long id, boolean isParseDates) throws Exception {
        //if (isParseDates) {
        //    // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
        //    //parseDatesBeforeDictionaryCalls(instance);
        //}
        Map<String, Object> resultMap = dctFindById(entityName, id);
        if (isParseDates) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(resultMap);
        }
        return resultMap;
    }

    protected List<Map<String, Object>> dctFindByExample(DictionaryCaller dc, String entityName, Map<String, Object> instance, boolean isParseDates) throws Exception {
        if (isParseDates) {
            // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
            parseDatesBeforeDictionaryCalls(instance);
        }
        List<Map<String, Object>> resultList = dctFindByExample(entityName, instance, dc);
        if (isParseDates) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(resultList);
        }
        return resultList;
    }

    protected Map<String, Object> dctOnlySave(String entityName, Map<String, Object> instance) throws Exception {
        /*
        JPADAOFactory jd = new JPADAOFactory();
        DictionaryCaller dc = new DictionaryCaller((HierarchyDAO) jd.getDAO(Termination2.class));
         */
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityName);
        if (instance == null) {
            instance = new HashMap<String, Object>();
        }
        dc.beginTransaction();
        Map<String, Object> resultMap;
        try {
            Map rawResultMap = dc.getDAO().onlySave(entityName, instance);
            // старая версия словарной системы выбрасывала исключение, если сущность не найдена
            // текущая (на момент написания комментария) версия новой словарной системы в таком случае возвращает null вместо результата не вызывая исключения
            // данный метод в качестве временного решения проверяет результат на null и выбрасывает исключение по аналогии со старой версией словарной системы
            checkRawResultAndThrowExIfNull(rawResultMap, "onlySave", entityName, instance);
            dc.commit();
            resultMap = dc.processReturnResult(rawResultMap);
        } catch (Exception ex) {
            logger.error("Method dctOnlySave caused exception: ", ex);
            dc.rollback();
            throw ex;
        }
        return resultMap;
    }

    protected Map<String, Object> dctOnlySave(String entityName, Map<String, Object> instance, boolean isParseDates) throws Exception {
        if (isParseDates) {
            // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
            parseDatesBeforeDictionaryCalls(instance);
        }
        Map<String, Object> resultMap = dctOnlySave(entityName, instance);
        if (isParseDates) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(resultMap);
        }
        return resultMap;
    }

    protected Map<String, Object> dctUpdate(String entityName, Map<String, Object> instance) throws Exception {
        /*
        JPADAOFactory jd = new JPADAOFactory();
        DictionaryCaller dc = new DictionaryCaller((HierarchyDAO) jd.getDAO(Termination2.class));
         */
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityName);
        if (instance == null) {
            instance = new HashMap<String, Object>();
        }
        dc.beginTransaction();
        Map<String, Object> resultMap;
        try {
            Map rawResultMap = dc.getDAO().update(entityName, instance);
            // старая версия словарной системы выбрасывала исключение, если сущность не найдена
            // текущая (на момент написания комментария) версия новой словарной системы в таком случае возвращает null вместо результата не вызывая исключения
            // данный метод в качестве временного решения проверяет результат на null и выбрасывает исключение по аналогии со старой версией словарной системы
            checkRawResultAndThrowExIfNull(rawResultMap, "update", entityName, instance);
            dc.commit();
            resultMap = dc.processReturnResult(rawResultMap);
        } catch (Exception ex) {
            logger.error("Method dctUpdate caused exception: ", ex);
            dc.rollback();
            throw ex;
        }
        return resultMap;
    }

    protected Map<String, Object> dctUpdate(String entityName, Map<String, Object> instance, boolean isParseDates) throws Exception {
        if (isParseDates) {
            // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
            parseDatesBeforeDictionaryCalls(instance);
        }
        Map<String, Object> resultMap = dctUpdate(entityName, instance);
        if (isParseDates) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(resultMap);
        }
        return resultMap;
    }

    // временное решение для использования старой системы состояний
    // todo: заменить на нормальную поддержку новой системы состояний через аспекты словарной системы, hibernate и пр., когда она будет реализована
    protected Map<String, Object> addStateKeysForOldSM(Map<String, Object> bean) {
        logger.debug("addStateKeysForOldSM...");
        if (bean != null) {
            Map<String, Object> state = (Map<String, Object>) bean.get("stateId_EN");
            if (state != null) {
                bean.put("STATEID", getLongParamLogged(state, "id"));
                bean.put("STATESYSNAME", getStringParamLogged(state, "sysname"));
                bean.put("STATENAME", getStringParamLogged(state, "name"));
            }
        }
        logger.debug("addStateKeysForOldSM finished.");
        return bean;
    }

    /**
     * @deprecated Использовался в старой словарной системе для корректного
     * сохранения ссылок на сущности (не являющиеся классификаторами) - для
     * новой словарной системы не требуется!
     */
    @Deprecated
    protected Map<String, Object> setLinkParamWTF(Map<String, Object> bean, String linkFieldName, Long linkFieldValue) {
        Map<String, Object> wtfFakeLinkedEntity = new HashMap<String, Object>();
        wtfFakeLinkedEntity.put("id", linkFieldValue);
        wtfFakeLinkedEntity.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        bean.put(linkFieldName + "_EN", wtfFakeLinkedEntity);
        // на всякий случай устанавливается еще и сама ссылка - чтобы корректно сохранялась новой словарной системой
        bean.put(linkFieldName, linkFieldName);
        return bean;
    }

    protected void updateRowStatusEn(Map<String, Object> bean, Long stateID) throws Exception {
        Map<String, Object> stateMap = dctFindById("com.bivgroup.crm.KindStatus", stateID);
        stateMap.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        bean.put("stateId_EN", stateMap);
    }

    protected void updateRowStatusEn(Map<String, Object> bean) throws Exception {
        Long stateID = getLongParam(bean, "stateId");
        updateRowStatusEn(bean, stateID);
    }

    protected Map<String, Object> makeResultMap(Map<String, Object> entityMap, String entityMapParamName) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(entityMapParamName, entityMap);
        return result;
    }

    protected Long getEntityRecordIdBySysName(String entityName, String entityRecordSysName) throws Exception {
        Long entityRecordId = null;
        Map<String, Object> entityRecordParams = new HashMap<String, Object>();
        entityRecordParams.put("sysname", entityRecordSysName);
        entityRecordParams.put("sysName", entityRecordSysName);
        List<Map<String, Object>> entityRecordList = dctFindByExample(entityName, entityRecordParams);
        if ((entityRecordList != null) && (entityRecordList.size() == 1)) {
            Map<String, Object> entityRecord = entityRecordList.get(0);
            entityRecordId = getLongParamLogged(entityRecord, "id");
        }
        return entityRecordId;
    }

    protected String getEntityRecordFieldStringValueByOtherFieldValue(String entityName, String stringFieldName, String otherFieldName, Object otherFieldValue) throws Exception {
        Map<String, Object> entityRecord = getEntityRecordByOtherFieldValue(entityName, otherFieldName, otherFieldValue);
        String entityRecordFieldStringValue = getStringParamLogged(entityRecord, stringFieldName);
        return entityRecordFieldStringValue;
    }

    protected Map<String, Object> getEntityRecordByOtherFieldValue(String entityName, String otherFieldName, Object otherFieldValue) throws Exception {
        Map<String, Object> entityRecord = null;
        Map<String, Object> entityRecordParams = new HashMap<String, Object>();
        entityRecordParams.put(otherFieldName, otherFieldValue);
        List<Map<String, Object>> entityRecordList = dctFindByExample(entityName, entityRecordParams);
        if ((entityRecordList != null) && (entityRecordList.size() == 1)) {
            entityRecord = entityRecordList.get(0);
        }
        return entityRecord;
    }

    // старая версия словарной системы выбрасывала исключение, если сущность не найдена
    // текущая (на момент написания комментария) версия новой словарной системы в таком случае возвращает null вместо результата не вызывая исключения
    // данный метод в качестве временного решения проверяет результат на null и выбрасывает исключение по аналогии со старой версией словарной системы
    private void checkRawResultAndThrowExIfNull(Object rawResult, String methodName, String entityName, Map<String, Object> instance) throws Exception {
        if (rawResult == null) {
            String errorMsg = String.format(
                    "Using dictionary system method '%s' for entity with name '%s' returning in null result!",
                    methodName, entityName
            );
            String errorMsgForLogging = String.format(
                    "%s Details (instance param of method):\n%s",
                    errorMsg, instance
            );
            logger.error(errorMsgForLogging);
            throw new Exception(errorMsg);
        }
    }

    protected Long getStatusIdBySysName(String stateSysName) throws Exception {
        Long kindStatusId = null;
        Map<String, Object> stateParams = new HashMap<String, Object>();
        stateParams.put("sysname", stateSysName);
        List<Map<String, Object>> kindStatusList = dctFindByExample(KIND_STATUS_ENTITY_NAME, stateParams);
        if ((kindStatusList != null) && (kindStatusList.size() == 1)) {
            Map<String, Object> kindStatus = kindStatusList.get(0);
            kindStatusId = getLongParamLogged(kindStatus, "id", "KindStatus.", true);
        }
        return kindStatusId;
    }

    protected Long getAgreementKindIdBySysName(String agreementKindSysName) throws Exception {
        Long agreementKindId = null;
        Map<String, Object> agreementKindParams = new HashMap<String, Object>();
        agreementKindParams.put("sysname", agreementKindSysName);
        List<Map<String, Object>> agreementKindList = dctFindByExample(KIND_AGREEMENT_CLIENT_PROFILE_ENTITY_NAME, agreementKindParams);
        //logger.debug("agreementKindList = " + agreementKindList);
        if ((agreementKindList != null) && (agreementKindList.size() == 1)) {
            Map<String, Object> agreementKind = agreementKindList.get(0);
            agreementKindId = getLongParamLogged(agreementKind, "id", KIND_AGREEMENT_CLIENT_PROFILE_ENTITY_NAME + ".", true);
        }
        return agreementKindId;
    }

    // преобразовать мапу клиента в мапу контрагента с очисткой от ИД (для создания контрагента соответствующего данному клиенту, например, для использования в качестве страхователя по договору)
    protected Map<String, Object> makeContragentFromClient(Map<String, Object> pClientMap) {
        //Object pClientID = pClientMap.get("ID");
        Long pClientID = getLongParamLogged(pClientMap, "id");
        Map<String, Object> contragentMap = new HashMap<String, Object>();
        contragentMap.putAll(pClientMap);
        //Documents
        List<Map<String, Object>> contragentDocuments = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> pClientDocuments = (List<Map<String, Object>>) pClientMap.get("documents");
        if ((pClientDocuments != null) && (pClientDocuments.size() > 0)) {
            contragentDocuments.addAll(pClientDocuments);
            clearListByLongFieldValues(contragentDocuments, "typeId", CONTRAGENT_DOC_TYPE_IDS);
            clearListByLongFieldValue(contragentDocuments, "isPrimary", BOOLEAN_FLAG_LONG_VALUE_TRUE);
            clearListFromKeysAndEns(contragentDocuments, "id", "eId", "clientId", "$type$", "typeId_EN", "stateId_EN", "ROWSTATUS");
            clearListFromKeysAndEns(contragentDocuments, "id", "eId", "clientId", "$type$", "typeId_EN", "stateId_EN", "rowStatus");
        }
        contragentMap.put("documents", contragentDocuments);
        //Addresses
        List<Map<String, Object>> contragentAddresses = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> pClientAddresses = (List<Map<String, Object>>) pClientMap.get("addresses");
        if ((pClientAddresses != null) && (pClientAddresses.size() > 0)) {
            contragentAddresses.addAll(pClientAddresses);
            clearListByLongFieldValues(contragentAddresses, "typeId", CONTRAGENT_ADDRESS_TYPE_IDS);
            clearListByLongFieldValue(contragentAddresses, "isPrimary", BOOLEAN_FLAG_LONG_VALUE_TRUE);
            clearListFromKeysAndEns(contragentAddresses, "id", "eId", "clientId", "$type$", "typeId_EN", "stateId_EN", "ROWSTATUS");
            clearListFromKeysAndEns(contragentAddresses, "id", "eId", "clientId", "$type$", "typeId_EN", "stateId_EN", "rowStatus");
        }
        contragentMap.put("addresses", contragentAddresses);
        //Contacts
        List<Map<String, Object>> contragentContacts = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> pClientContacts = (List<Map<String, Object>>) pClientMap.get("contacts");
        if ((pClientContacts != null) && (pClientContacts.size() > 0)) {
            contragentContacts.addAll(pClientContacts);
            clearListByLongFieldValues(contragentContacts, "typeId", CONTRAGENT_CONTACT_TYPE_IDS);
            clearListByLongFieldValue(contragentContacts, "isPrimary", BOOLEAN_FLAG_LONG_VALUE_TRUE);
            clearListFromKeysAndEns(contragentContacts, "id", "eId", "clientId", "$type$", "typeId_EN", "stateId_EN", "ROWSTATUS");
            clearListFromKeysAndEns(contragentContacts, "id", "eId", "clientId", "$type$", "typeId_EN", "stateId_EN", "rowStatus");
        }
        contragentMap.put("contacts", contragentContacts);
        // BankDetails
        List<Map<String, Object>> pClientBankDetailsList = (List<Map<String, Object>>) pClientMap.get("bankDetails");
        if ((pClientBankDetailsList != null) && (pClientBankDetailsList.size() > 0)) {
            for (Map<String, Object> pClientBankDetailItem : pClientBankDetailsList) {
                Long isPrimary = getLongParam(pClientBankDetailItem, "isPrimary");
                if (BOOLEAN_FLAG_LONG_VALUE_TRUE.equals(isPrimary)) {
                    // основная запись - очистка и копирование в контрагента
                    clearMapFromKeysAndEns(pClientBankDetailItem, "id", "eId", "isPrimary", "alias", "stateId", "clientId", "$type$", "ROWSTATUS");
                    clearMapFromKeysAndEns(pClientBankDetailItem, "id", "eId", "isPrimary", "alias", "stateId", "clientId", "$type$", "rowStatus");
                    contragentMap.put("bankDetailsId_EN", pClientBankDetailItem);
                    break;
                }
            }
        }
        // контрагент - финальная очистка
        clearMapFromKeysAndEns(contragentMap, "id", "eId", "clientId", "verLock", "verLastId", "verID", "verNumber", "verDate", "tel", "$type$", "stateId_EN", "Status", "ROWSTATUS", "persons", "bankDetails");
        clearMapFromKeysAndEns(contragentMap, "id", "eId", "clientId", "verLock", "verLastId", "verId", "verNumber", "verDate", "tel", "$type$", "stateId_EN", "status", "rowStatus", "persons", "bankDetails");
        // ссылка на клиента, являющегося первоисточником для данного контрагента
        contragentMap.put("clientId", pClientID);
        //setLinkParamWTF(contragentMap, "ClientID", pClientID);
        // возврат результата
        return contragentMap;
    }

    protected Long getClientPropertyKindIdBySysName(String clientPropertySysName) throws Exception {
        Long clientPropertyKindId = null;
        Map<String, Object> clientPropertyKindParams = new HashMap<String, Object>();
        clientPropertyKindParams.put("sysname", clientPropertySysName);
        List<Map<String, Object>> clientPropertyKindList = dctFindByExample(KIND_CLIENT_PROPERTY_ENTITY_NAME, clientPropertyKindParams);
        if ((clientPropertyKindList != null) && (clientPropertyKindList.size() == 1)) {
            Map<String, Object> clientProperty = clientPropertyKindList.get(0);
            clientPropertyKindId = getLongParamLogged(clientProperty, "id", KIND_CLIENT_PROPERTY_ENTITY_NAME + ".", true);
        }
        return clientPropertyKindId;
    }

    // преобразовать мапу клиента в мапу водителя с очисткой от ИД (для создания водителя соответствующего данному клиенту, например, для использования в качестве водителя по договору КАСКО по флагу isSelf)
    protected Map<String, Object> makeContraсtDriverFromClient(Map<String, Object> client) throws Exception {
        // преобразовать мапу клиента в мапу контрагента с очисткой от ИД (для создания контрагента соответствующего данному клиенту)
        Map<String, Object> contragent = makeContragentFromClient(client);
        Map<String, Object> driver = new HashMap<String, Object>();
        if (contragent != null) {
            driver.putAll(contragent);
        }
        // одно из отличий водителя от стандартного контрагента - дата начала стажа вождения
        Object dateOfExpDate = null;
        Long clientId = getLongParamLogged(client, "id", "Id from client map");
        if (clientId != null) {
            Long clientPropertyKindId = getClientPropertyKindIdBySysName("dateOfExp");
            if (clientPropertyKindId != null) {
                Map<String, Object> clientPropertyParams = new HashMap<String, Object>();
                clientPropertyParams.put("propertyTypeId", clientPropertyKindId);
                clientPropertyParams.put("clientId", clientId);
                List<Map<String, Object>> clientPropertyList = dctFindByExample(CLIENT_PROPERTY_ENTITY_NAME, clientPropertyParams);
                if ((clientPropertyList != null) && (!clientPropertyList.isEmpty())) {
                    Map<String, Object> clientProperty = clientPropertyList.get(0);
                    clientProperty.remove("clientId_EN");
                    logger.debug("Client property for dateOfExp: " + clientProperty.toString());
                    dateOfExpDate = clientProperty.get("valueDate");
                }
            }
        }
        driver.put("dateOfExp", dateOfExpDate);
        return driver;
    }

    // getClientMapByCrmParticipantMap - перенесено из B2BShareContractCustomFacade без изменений
    protected Map<String, Object> getClientMapByCrmParticipantMap(Map<String, Object> participantMap, String mobilePhone) {
        Map<String, Object> clientMap = new HashMap<String, Object>();
        clientMap.put("surname", participantMap.get("LASTNAME"));
        clientMap.put("name", participantMap.get("FIRSTNAME"));
        String middleName = getStringParam(participantMap.get("MIDDLENAME"));
        if (middleName.isEmpty()) {
            clientMap.put("isEmptyPatronymic", "1");
        } else {
            clientMap.put("patronymic", middleName);
            clientMap.put("isEmptyPatronymic", "0");
        }
        clientMap.put("dateOfBirth$date", participantMap.get("BIRTHDATE"));
        clientMap.put("placeOfBirth", "");
        clientMap.put("countryId", "1");
        clientMap.put("isMarried", "0");
        clientMap.put("sex", participantMap.get("GENDER"));
        clientMap.put("persons", new ArrayList<Object>());

        ArrayList<Map<String, Object>> documentList = new ArrayList<Map<String, Object>>();
        ArrayList<Map<String, Object>> documentListCRM = (ArrayList<Map<String, Object>>) participantMap.get("documentList");
        if (documentListCRM != null) {
            for (Map<String, Object> docMapCRM : documentListCRM) {
                Map<String, Object> docMap = new HashMap<String, Object>();
                docMap.put("typeId", docMapCRM.get("PERSONDOCTYPEID"));
                docMap.put("isPrimary", "1");
                String issuedBy = getStringParam(docMapCRM.get("ISSUEDBY"));
                String issuerCode = getStringParam(docMapCRM.get("ISSUERCODE"));
                if (issuedBy.isEmpty()) {
                    docMap.put("authority", "-");
                } else {
                    docMap.put("authority", issuedBy);
                }
                if (issuerCode.isEmpty()) {
                    docMap.put("issuerCode", "000000");
                } else {
                    docMap.put("issuerCode", issuerCode);
                }
                docMap.put("no", docMapCRM.get("DOCNUMBER"));
                docMap.put("series", docMapCRM.get("DOCSERIES"));
                docMap.put("dateOfIssue$date", docMapCRM.get("ISSUEDATE"));
                documentList.add(docMap);
            }
            clientMap.put("documents", documentList);
        }

        ArrayList<Map<String, Object>> addressList = new ArrayList<Map<String, Object>>();
        ArrayList<Map<String, Object>> addressListCRM = (ArrayList<Map<String, Object>>) participantMap.get("addressList");
        if (addressListCRM != null) {
            for (Map<String, Object> addressMapCRM : addressListCRM) {
                Map<String, Object> addressMap = new HashMap<String, Object>();
                addressMap.put("typeId", addressMapCRM.get("ADDRESSTYPEID"));
                addressMap.put("address", addressMapCRM.get("REGION"));
                addressMap.put("isPrimary", "1");
                addressList.add(addressMap);
            }
            clientMap.put("addresses", addressList);
        }

        ArrayList<Map<String, Object>> contactsList = new ArrayList<Map<String, Object>>();
        ArrayList<Map<String, Object>> contactsListCRM = (ArrayList<Map<String, Object>>) participantMap.get("contactList");
        if (contactsListCRM != null) {
            for (Map<String, Object> contactsMapCRM : contactsListCRM) {
                Map<String, Object> contactsMap = new HashMap<String, Object>();
                String typeSysName = getStringParam(contactsMapCRM.get("CONTACTTYPESYSNAME"));
                if ("MobilePhone".equalsIgnoreCase(typeSysName)) {
                    contactsMap.put("value", mobilePhone);
                } else {
                    contactsMap.put("value", contactsMapCRM.get("VALUE"));
                }
                contactsMap.put("typeId", contactsMapCRM.get("CONTACTTYPEID"));
                contactsMap.put("isPrimary", "1");
                contactsList.add(contactsMap);
            }
            clientMap.put("contacts", contactsList);
        }

        return clientMap;
    }

    protected static Map<String, Object> getClientNamesMap(Map<String, Object> client) {
        Map<String, Object> namesMap = new HashMap<String, Object>();
        String lastName = getStringParam(client, "surname");
        String firstName = getStringParam(client, "name");
        String middleName = getStringParam(client, "patronymic");

        StringBuilder fullName = new StringBuilder("");
        StringBuilder fullNameAbbr = new StringBuilder("");

        if (!lastName.isEmpty()) {
            fullName.append(lastName);
            fullNameAbbr.append(lastName);
        }

        if (!firstName.isEmpty()) {
            fullName.append(" ").append(firstName);
            fullNameAbbr.append(" ").append(firstName.charAt(0)).append(".");
        }

        if (!middleName.isEmpty()) {
            fullName.append(" ").append(middleName);
            fullNameAbbr.append(" ").append(middleName.charAt(0)).append(".");
        }

        namesMap.put("surname", lastName);
        namesMap.put("name", firstName);
        namesMap.put("patronymic", middleName);
        namesMap.put("fullName", fullName.toString());
        namesMap.put("fullNameAbbr", fullNameAbbr.toString());
        return namesMap;
    }

    protected void updateEntitySystemDates(Map<String, Object> entity) {
        String pkFieldName = "id";
        updateEntitySystemDates(entity, pkFieldName);
    }

    protected void updateEntitySystemDates(Map<String, Object> entity, String pkFieldName) {
        Long entityId = getLongParamLogged(entity, pkFieldName);
        Date nowDate = new Date();
        if (entityId == null) {
            entity.remove("createDate$date");
            entity.remove("createDate$datetime");
            entity.put("createDate", nowDate);
        }
        entity.remove("updateDate$date");
        entity.remove("updateDate$datetime");
        entity.put("updateDate", nowDate);
        markAsModified(entity);
    }

    /**
     * Осуществить переход по имени перехода.
     * todo: скопировать актуальные версии методов по переводу состояния из СБС
     *
     * @param entityName        - имя класса сущности
     * @param entityId          - идентификатор экземпляра в БД
     * @param transitionSysName - имя перехода
     * @throws Exception
     */
    protected void dctMakeTransition(String entityName, Long entityId, String transitionSysName) throws Exception {
        // long startMs = System.currentTimeMillis();
        if (transitionSysName == null || transitionSysName.isEmpty()) {
            logger.error("Transition for entity [" + entityName + "] is EMPTY");
            throw new Exception("Не указано имя перехода!");
        }
        if (entityId == null) {
            logger.error("Id for entity [" + entityName + "] is NULL");
            throw new Exception("Не указан идентификатор экземпляра для смены состояния!");
        }
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityName);
        dc.beginTransaction();
        try {
            // long start1Ms = System.currentTimeMillis();
            dc.getDAO().makeTrans(entityName, entityId, transitionSysName);
            // logger.error(String.format("[NOT ERROR] c.getDAO().makeTrans takes %d ms.", System.currentTimeMillis() - start1Ms));
            // long start2Ms = System.currentTimeMillis();
            dc.commit();
            // logger.error(String.format("[NOT ERROR] dc.commit() takes %d ms.", System.currentTimeMillis() - start2Ms));
        } catch (Exception ex) {
            logger.error("Method makeTransition caused exception: ", ex);
            dc.rollback();
            throw ex;
        }
        // logger.error(String.format("[NOT ERROR] dctMakeTransition takes %d ms.", System.currentTimeMillis() - startMs));
    }

    /**
     * Осуществить переход по имени перехода (без управления транзакцией).
     * todo: скопировать актуальные версии методов по переводу состояния из СБС
     *
     * @param entityName        - имя класса сущности
     * @param entityId          - идентификатор записи в БД
     * @param transitionSysName - имя перехода
     * @param dc                - экземпляр DictionaryCaller для работы с БД
     * @throws Exception
     */
    protected void dctMakeTransitionWithoutTransactionControl(String entityName, Long entityId, String transitionSysName, DictionaryCaller dc) throws Exception {
        if (transitionSysName == null || transitionSysName.isEmpty()) {
            logger.error("Transition for entity [" + entityName + "] is EMPTY");
            throw new Exception("Не указано имя перехода!");
        }
        if (entityId == null) {
            logger.error("Id for entity [" + entityName + "] is NULL");
            throw new Exception("Не указан идентификатор экземпляра для смены состояния!");
        }
        // DictionaryCaller dc = getDictionaryCallerByEntityName(entityName);
        // dc.beginTransaction();
        try {
            dc.getDAO().makeTrans(entityName, entityId, transitionSysName);
            // dc.commit();
        } catch (Exception ex) {
            logger.error("Method makeTransition caused exception: ", ex);
            // dc.rollback();
            throw ex;
        }
    }

    /** копия из СБС */
    protected Map<String, Object> createBinaryFileInfo(Map<String, Object> params) throws Exception {
        String entityClassName = getStringParam(params, "HIBERNATEENTITY");
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityClassName);
        Map<String, Object> result;
        dc.beginTransaction();
        try {
            result = dc.getDAO().createBinaryFileInfo(entityClassName, params);
            dc.commit();
        } catch (Exception ex) {
            dc.rollback();
            String userError = "Не удалось прикрепить файл к Hibernate сущности";
            result = makeErrorResult(userError);
            logger.error(userError + " entityClassName=" + entityClassName + " fileHandle=" + params, ex);
            throw ex;
        }
        return result;
    }

    /** копия из СБС */
    protected Map<String, Object> updateBinaryFileInfo(Map<String, Object> params) throws Exception {
        String entityClassName = getStringParam(params, "HIBERNATEENTITY");
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityClassName);
        Map<String, Object> result;
        dc.beginTransaction();
        try {
            result = dc.getDAO().updateBinaryFileInfo(entityClassName, params);
            dc.commit();
        } catch (Exception ex) {
            dc.rollback();
            String userError = "Не удалось обновить прикрепленный файл к Hibernate сущности";
            result = makeErrorResult(userError);
            logger.error(userError + " entityClassName=" + entityClassName + " fileHandle=" + params, ex);
            throw ex;
        }
        return result;
    }

    /** копия из СБС */
    protected Map<String, Object> deleteBinaryFileInfo(Map<String, Object> params) throws Exception {
        String entityClassName = getStringParam(params, "HIBERNATEENTITY");
        Long binaryFileId = getLongParam(params, "OBJID");
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityClassName);
        Map<String, Object> result;
        dc.beginTransaction();
        try {
            dc.getDAO().deleteBinaryFileInfo(entityClassName, binaryFileId);
            dc.commit();
            result = new HashMap<String, Object>();
        } catch (Exception ex) {
            dc.rollback();
            String userError = "Не удалось удалить прикрепленный файл к Hibernate сущности";
            result = makeErrorResult(userError);
            logger.error(userError + " entityClassName=" + entityClassName + " binaryFileId=" + binaryFileId, ex);
            throw ex;
        }
        return result;
    }

    /** копия из СБС */
    /*
    protected Map<String, Object> getBinaryFileInfo(Map<String, Object> params) throws Exception {
        String entityClassName = getStringParam(params, "HIBERNATEENTITY");
        Long objectId = getLongParam(params, "OBJID");
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityClassName);
        Map<String, Object> result;
        dc.beginTransaction();
        try {
            List<Map> objects = dc.getDAO().getBinaryFileInfo(entityClassName, objectId);
            dc.commit();
            result = new HashMap<String, Object>();
            result.put(RESULT, dc.processReturnResult(objects));
        } catch (Exception ex) {
            dc.rollback();
            String userError = "Не удалось получить прикрепленный файл к Hibernate сущности";
            result = makeErrorResult(userError);
            logger.error(userError + " entityClassName=" + entityClassName + " objectId=" + objectId, ex);
            throw ex;
        }
        return result;
    }
    */

    /** копия из СБС, но разделено на два метода с префиксами dct */
    protected Map<String, Object> dctGetBinaryFileInfo(Map<String, Object> params) throws Exception {
        String entityClassName = getStringParam(params, "HIBERNATEENTITY");
        Long objectId = getLongParam(params, "OBJID");
        List<Map<String, Object>> resultList = dctGetBinaryFileInfo(entityClassName, objectId);
        Map<String, Object> result = new HashMap<>();
        result.put(RESULT, resultList);
        return result;
    }

    /** копия из СБС, но разделено на два метода с префиксами dct */
    protected List<Map<String, Object>> dctGetBinaryFileInfo(String entityName, Long entityId) throws Exception {
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityName);
        List<Map<String, Object>> resultList;
        dc.beginTransaction();
        try {
            List<Map> objects = dc.getDAO().getBinaryFileInfo(entityName, entityId);
            dc.commit();
            resultList = dc.processReturnResult(objects);
        } catch (Exception ex) {
            dc.rollback();
            String userError = "Не удалось получить прикрепленный файл к Hibernate сущности";
            // result = makeErrorResult(userError);
            logger.error(userError + " entityName=" + entityName + " entityId=" + entityId, ex);
            throw ex;
        }
        return resultList;
    }

    /** аналог dctGetBinaryFileInfo, но с вложенной условной конвертацией дат */
    protected List<Map<String, Object>> dctGetBinaryFileInfo(String entityName, Long entityId, boolean isParseDates) throws Exception {
        /*
        if (isParseDates) {
            // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
            parseDatesBeforeDictionaryCalls(instance);
        }
        */
        List<Map<String, Object>> resultList = dctGetBinaryFileInfo(entityName, entityId);
        if (isParseDates) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(resultList);
        }
        return resultList;
    }

}
