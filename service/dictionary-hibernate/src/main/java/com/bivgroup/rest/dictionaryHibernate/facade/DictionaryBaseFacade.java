package com.bivgroup.rest.dictionaryHibernate.facade;

import com.bivgroup.core.dictionary.dao.jpa.HierarchyDAO;
import com.bivgroup.core.dictionary.dao.jpa.JPADAOFactory;
import com.bivgroup.core.dictionary.dao.jpa.RowStatus;
import com.bivgroup.crm.Crm2;
import com.bivgroup.rest.basefacade.BaseFacade;
import com.bivgroup.rest.dictionaryHibernate.common.DictionaryCaller;
import com.bivgroup.rest.dictionaryHibernate.common.DictionaryConstants;
import com.bivgroup.termination.Termination2;
import com.bivgroup.utils.ParamGetter;
import org.apache.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author ilich
 */
public class DictionaryBaseFacade extends BaseFacade implements DictionaryConstants {

    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * имя параметра с признаком выполнения вызова из гейта
     */
    //protected static final String IS_CALL_FROM_GATE_PARAMNAME = ServiceCaller.IS_CALL_FROM_GATE_PARAMNAME;
    // имя параметра для методов типа dsB2BHandbookDataBrowseByHBName, которые могут вернуть как мапу (где ключ - имя справочника, а значение - список его записей) так и сразу список
    // если true, то возвращает только список с записями справочника; иначе - возвращает мапу (ключ - имя справочника, значение - список с записями справочника)
    protected static final String RETURN_LIST_ONLY = "ReturnListOnly";

    protected static final String KEY_NAME_DATE_SUFFIX = "DATE"; // например, SOMEDATE
    protected static final String KEY_NAME_TIME_SUFFIX = "TIME"; // прибавляется к имени ключей с датами, поэтому, например, SOMEDATETIME
    protected static final String KEY_NAME_DATE_SUFFIX_NEW = "$date"; // например, SOME$date
    protected static final String KEY_NAME_TIME_SUFFIX_NEW = "time"; // прибавляется к имени ключей с датами, поэтому, например, SOME$datetime

    /**
     * Канал приема (ReceivingChannel) - системное наименование для канала 'Личный кабинет'
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
     * ИД типа документов для контрагента: Водительское удостоверение (DrivingLicence) - 1016L
     */
    // todo: константы ИД заменить на полуение ИД по сис. имени типа (когда будут готовы классификаторы)
    protected static final long CDM_DOCTYPE_ID_DRIVING_LICENCE = 1016L;// Водительское удостоверение (DrivingLicence)

    static {
        daoClassByModulePrefix.put(DCT_MODULE_PREFIX_CRM, Crm2.class);
        daoClassByModulePrefix.put(DCT_MODULE_PREFIX_TERMINATION, Termination2.class);

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
            daoClass = entry.getValue();
            if (entityName.startsWith(modulePrefix)) {
                break;
            }
        }
        if (daoClass == null) {
            logger.error(String.format("Unable to get DAO class by entity name ('%s')! Supported entity name's prfixes are %s.", entityName, daoClassByModulePrefix.keySet().toString()));
        }
        return daoClass;
    }

    protected DictionaryCaller getDictionaryCallerByEntityName(String entityName) {
        JPADAOFactory jd = new JPADAOFactory();
        Class daoClass = getDAOClassByEntityName(entityName);
        HierarchyDAO hierarchyDAO = (HierarchyDAO) jd.getDAO(daoClass);
        DictionaryCaller dictionaryCaller = new DictionaryCaller(hierarchyDAO);
        return dictionaryCaller;
    }

    // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
    protected void parseDatesBeforeDictionaryCalls(List<Map<String, Object>> dataList) {
        for (Map<String, Object> bean : dataList) {
            parseDates(bean, Date.class, KEY_NAME_DATE_SUFFIX_NEW, KEY_NAME_TIME_SUFFIX_NEW, true, false);
        }
    }

    // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
    protected void parseDatesBeforeDictionaryCalls(Map<String, Object> data) {
        parseDates(data, Date.class, KEY_NAME_DATE_SUFFIX_NEW, KEY_NAME_TIME_SUFFIX_NEW, true, false);
    }

    // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
    protected void parseDatesAfterDictionaryCalls(List<Map<String, Object>> dataList) {
        for (Map<String, Object> bean : dataList) {
            parseDates(bean, String.class, KEY_NAME_DATE_SUFFIX_NEW, KEY_NAME_TIME_SUFFIX_NEW, false, true);
        }
    }

    // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
    protected void parseDatesAfterDictionaryCalls(Map<String, Object> data) {
        parseDates(data, String.class, KEY_NAME_DATE_SUFFIX_NEW, KEY_NAME_TIME_SUFFIX_NEW, false, true);
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

    public List<Map<String, Object>> dctFindByExample(String entityName, Map<String, Object> instance) throws Exception {
        /*
        JPADAOFactory jd = new JPADAOFactory();
        DictionaryCaller dc = new DictionaryCaller((HierarchyDAO) jd.getDAO(Crm2.class));
         */
        DictionaryCaller dc = getDictionaryCallerByEntityName(entityName);
        return dctFindByExample(entityName, instance, dc);
    }

    public List<Map<String, Object>> dctFindByExample(String entityName, Map<String, Object> instance, boolean isParseDates) throws Exception {
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

    public Map<String, Object> dctCrudByHierarchy(String entityName, Map<String, Object> instance) throws Exception {
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
                bean.put("STATEID", ParamGetter.getLongParam(state, "id"));
                bean.put("STATESYSNAME", ParamGetter.getStringParam(state, "sysname"));
                bean.put("STATENAME", ParamGetter.getStringParam(state, "name"));
            }
        }
        logger.debug("addStateKeysForOldSM finished.");
        return bean;
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

    protected Long getEntityRecordIdBySysName(String entityName, String entityRecordSysName) throws Exception {
        Long entityRecordId = null;
        Map<String, Object> entityRecordParams = new HashMap<String, Object>();
        entityRecordParams.put("sysname", entityRecordSysName);
        List<Map<String, Object>> entityRecordList = dctFindByExample(entityName, entityRecordParams);
        if ((entityRecordList != null) && (entityRecordList.size() == 1)) {
            Map<String, Object> entityRecord = entityRecordList.get(0);
            entityRecordId = ParamGetter.getLongParam(entityRecord, "id");
        }
        return entityRecordId;
    }

    public static Object getParamByRoute(Map<String, Object> params, String fullRoutes, Object defValue) {

        if (fullRoutes == null) return defValue;

        String[] routes = fullRoutes.split("\\.");
        Map<String, Object> value = params;

        for (int i = 0; i <= (routes.length - 2); i++) {
            value = (Map<String, Object>) value.get(routes[i]);
            if (value == null) return defValue;
        }

        String lastRoute = routes[routes.length - 1];
        Object result = value.get(lastRoute);
        return Optional.ofNullable(result).orElse(defValue);

    }

    public Map<String, Object> newParams() {
        return new HashMap<>();
    }

    public static String MD5Hash(String s) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(s.trim().getBytes());
        byte[] digest = md.digest();
        //  в верхний регистр чтобы потом искать в БД
        String myHash = DatatypeConverter.printHexBinary(digest);
        return myHash;
    }
}
