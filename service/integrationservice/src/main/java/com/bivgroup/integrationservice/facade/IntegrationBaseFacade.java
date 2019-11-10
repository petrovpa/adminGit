package com.bivgroup.integrationservice.facade;

import com.bivgroup.integrationservice.facade.integration.UserNameTokenHandler;
import com.bivgroup.integrationservice.system.Constants;
import com.bivgroup.integrationservice.system.IntegrationException;
import com.bivgroup.lifeintegrationtypes.LocalDateConverter;
import com.bivgroup.partnerservice.PartnerServiceCaller;
import com.bivgroup.sessionutils.FileSessionControllerImpl;
import com.bivgroup.sessionutils.SessionController;
import org.apache.cayenne.conf.Configuration;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import ru.diasoft.fa.commons.sm.StateMachineManagerNew;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.utils.XMLUtil;
import ru.sberinsur.esb.partner.shema.*;
import ru.sberinsur.fuse.files.File;
import ru.sberinsur.fuse.files.Folder;
import ru.sberinsur.fuse.files.ShortFileInfo;

import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.diasoft.services.inscore.system.WsConstants.RET_STATUS;
import static ru.diasoft.services.inscore.system.WsConstants.RET_STATUS_OK;

public class IntegrationBaseFacade extends BaseFacade {

    protected static final String DEFAULT_LOGIN = "os1";
    private StateMachineManagerNew sm = null;
    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final private byte[] Salt = {
            (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
            (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};
    protected static final Map<String, Route> PROGRAMS = new HashMap<>();

    protected static final Long OUTCONTRACTPACKSINGLE = 10L; // todo: заменить на импорт
    protected static final Long GETCUTCONTRACTINFO = 20L; // todo: заменить на импорт
    protected static final Long GETCUTCLAIMINFO = 30L; // todo: заменить на импорт
    protected static final Long CLAIMFILEADD = 40L; // todo: заменить на импорт
    protected static final Long CLAIMFILEASSIGN = 50L; // todo: заменить на импорт
    protected static final Long CHANGEFILEADD = 45L; // todo: заменить на импорт
    protected static final Long CHANGEFILEASSIGN = 55L; // todo: заменить на импорт
    protected static final Long PUTCUTCLAIMINFO = 60L; // todo: заменить на импорт
    protected static final Long GETCUTCHANGEINFO = 70L; // todo: заменить на импорт
    protected static final Long PUTCUTCHANGEINFO = 80L; // todo: заменить на импорт
    protected static final Long GETDELETEDINFO = 100L; // todo: заменить на импорт

    public static final String FS_HARDDRIVE = Constants.FS_HARDDRIVE;
    public static final String FS_EXTERNAL = Constants.FS_EXTERNAL;
    public static final String FS_1C = Constants.FS_1C;

    protected static final String CHANGE_MAP_PARAMNAME = "DECLARATIONMAP";
    protected static final String CHANGE_ID_PARAMNAME = "id";
    protected static final String CHANGE_STATEID_PARAMNAME = "stateId";
    protected static final String CHANGE_DOCFOLDER1C_PARAMNAME = "docFolder1C";
    protected static final String CHANGE_EXTERNAL_ID_PARAMNAME = "externalId";

    protected static final String KIND_CHANGE_REASON_SYSNAME = "com.bivgroup.termination.KindChangeReason";

    protected static final int PD_DECLARATION_SENDING = 7502;
    protected static final int PD_DECLARATION_SENDED = 7503;
    protected static final int PD_DECLARATION_INWORK = 7504;
    protected static final int PD_DECLARATION_DELETED = 7590;

    protected static final int B2B_LOSSNOTICE_INWORK = 8503;
    protected static final int B2B_LOSSNOTICE_FINAL = 8504;
    protected static final int B2B_LOSSNOTICE_DELETED = 8590;
    protected static final String LOSS_NOTICE_STATEID_PARAMNAME = "stateId";
    protected static final String LOSS_NOTICE_MAP_PARAMNAME = "LOSSNOTICE" + "MAP";
    protected static final String LOSS_NOTICE_ID_PARAMNAME = "lossNoticeId";
    protected static final String LOSS_NOTICE_EXTERNAL_ID_PARAMNAME = "externalId";

    protected static final String LOSS_EVENT_ID_FIELDNAME = "insEventId";

    // протоколирование времени, затраченного на вызовы (влияет только на вызовы через callServiceTimeLogged)
    protected boolean IS_CALLS_TIME_LOGGED = false;

    // чтение конфига дефолтного пользователя.
    protected static final String BIVSBERLOSSWS = Constants.BIVSBERLOSSWS;

    protected static final String B2BPOSWS = Constants.B2BPOSWS;
    protected static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    protected static final String THIS_SERVICE_NAME = B2BPOSWS_SERVICE_NAME;
    protected static final String ADMINWS_SERVICE_NAME = Constants.ADMINWS;
    protected static final String REFWS_SERVICE_NAME = Constants.REFWS;
    protected static final int MAX_SECOND = 59;

    // копия из com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryConstants
    // префиксы для имен сущностей (новый хибернейт)
    public static final String DCT_MODULE_PREFIX_CRM = "com.bivgroup.crm.";
    public static final String DCT_MODULE_PREFIX_TERMINATION = "com.bivgroup.termination.";
    public static final String DCT_MODULE_PREFIX_LOSS = "com.bivgroup.loss.";

    // копия из com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryConstants
    /**
     * Имя сущности 'Страховое событие' (таблица B2B_INSEVENT)
     */
    public static final String LOSS_EVENT_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "LossEvent";
    /**
     * Имя сущности 'Причина события' (таблица B2B_LOSS_DAMAGECAT)
     */
    public static final String LOSS_DAMAGE_CATEGORY_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "LossDamageCategory";

    // имя параметра для методов типа dsB2BHandbookDataBrowseByHBName, которые могут вернуть как мапу (где ключ - имя справочника, а значение - список его записей) так и сразу список
    // если true, то возвращает только список с записями справочника; иначе - возвращает мапу (ключ - имя справочника, значение - список с записями справочника)
    protected static final String RETURN_LIST_ONLY = "ReturnListOnly";

    // todo: многие константы и пр. продублированы из b2bposservice - заменить на импорт (или вынести в отдельный общий проект и т.п.)
    // синонимы для констант из RowStatus
    protected static final RowStatus UNMODIFIED = RowStatus.UNMODIFIED;
    protected static final RowStatus INSERTED = RowStatus.INSERTED;
    protected static final RowStatus MODIFIED = RowStatus.MODIFIED;
    protected static final RowStatus DELETED = RowStatus.DELETED;

    // синонимы для констант из RowStatus
    protected static final int UNMODIFIED_ID = RowStatus.UNMODIFIED.getId();
    protected static final int INSERTED_ID = RowStatus.INSERTED.getId();
    protected static final int MODIFIED_ID = RowStatus.MODIFIED.getId();
    protected static final int DELETED_ID = RowStatus.DELETED.getId();
    protected static final String ROWSTATUS_PARAM_NAME = RowStatus.ROWSTATUS_PARAM_NAME;
    protected static final String ROWSTATUS_LOG_PATTERN = ROWSTATUS_PARAM_NAME + " = %s (%d)";

    // поля для хранения расш. данных мемберов (в B2B_MEMBER)
    protected static final String MEMBER_TYPE_SYSNAME_FIELDNAME = "TYPESYSNAME";
    protected static final String MEMBER_THIRDPARTYID_FIELDNAME = "LONGFIELD00";
    protected static final String MEMBER_HASHCODE_FIELDNAME = "STRINGFIELD05";

    // работа интеграции в режиме отладки (будут доступны доп. параметры при вызове обработчиков и пр.)
    private boolean isIntegrationInDebugMode = false;
    private static final String DEBUG_MODE_CONFIG_PARAMNAME = "DEBUGMODE";

    //    private PartnerPortType ppt = null;
    private PartnerServiceCaller psc = null;

    private void initPartnerService() {
        this.psc = new PartnerServiceCaller(getLifePartnerServiceLocation());
//        URL url = this.getClass().getClassLoader().getResource("partnerService.wsdl");
//        PartnerPortTypeService ppts = new ru.sberinsur.esb.partner.PartnerPortTypeService(url);
//        ppt = ppts.getPartnerPortTypePort();
//        Map<String, Object> ctxt = ((BindingProvider) ppt).getRequestContext();
//
//        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getLifePartnerServiceLocation());
//        processWsSecurity((BindingProvider) ppt);
    }

    public IntegrationBaseFacade() {
        logger.debug("IntegrationBaseFacade init...");
        this.initPartnerService();
        this.updateProgramsCache();
        Config config = Config.getConfig(THIS_SERVICE_NAME);
        try {
            String debugModeParamValue = config.getParam(DEBUG_MODE_CONFIG_PARAMNAME, "0");
            logger.debug(DEBUG_MODE_CONFIG_PARAMNAME + " = " + debugModeParamValue);
            isIntegrationInDebugMode = (debugModeParamValue != null) && ((debugModeParamValue.equals("1")) || (debugModeParamValue.equalsIgnoreCase("TRUE")) || (debugModeParamValue.equalsIgnoreCase("YES")));
        } catch (Exception ex) {
            isIntegrationInDebugMode = false;
        }
        logger.debug("isAuthInDebugMode = " + isIntegrationInDebugMode);
        logger.debug("IntegrationBaseFacade init finished.");
    }

    // обновление кеша роутов
    protected void updateProgramsCache() {

        try {
            if (!PROGRAMS.isEmpty()) return;
            Map<String, Object> programsRes = this.selectQuery("dsB2BProductIntegrationRouteBrowseListByParam", "dsB2BProductIntegrationRouteBrowseListByParamCount", new HashMap<>());
            List<Map<String, Object>> programs = (List<Map<String, Object>>) programsRes.get(RESULT);
            if (programs.size() == 0) return;

            for (Map<String, Object> p : programs) {
                String pName = (String) p.get("PROGRAMNAME");
                Route r = new Route(p);
                PROGRAMS.put(pName, r);
            }
        } catch (Exception ex) {
            logger.error("ItegrationBaseFacade#updateProgramsCache: cant update =>" + ex);
        }
    }

    protected boolean isIntegrationInDebugMode() {
        return this.isIntegrationInDebugMode;
    }

    protected static final BiMap<String, PeriodicityType> PERIODICITYMAP = fillPeriodicityMap();

    private static BiMap<String, PeriodicityType> fillPeriodicityMap() {
        BiMap<String, PeriodicityType> res = new BiMap();
        res.put("QUARTERLY", PeriodicityType.QUA);
        res.put("ANNUALLY", PeriodicityType.ANN);
        res.put("SEMIANNUALLY", PeriodicityType.SEM);
        res.put("ONETIME", PeriodicityType.ONE);
        return res;
    }

    protected static final BiMap<String, DocumentType> DOCUMENTTYPEMAP = fillDocTypeMap();

    private static BiMap<String, DocumentType> fillDocTypeMap() {
        BiMap<String, DocumentType> res = new BiMap();
        res.put("PassportRF", DocumentType.PASSPORT);
        res.put("BornCertificate", DocumentType.BIRTH_CERTIFICATE);
        res.put("ForeignPassport", DocumentType.CONF_RIGHTS_RF);
        res.put("MigrationCard", DocumentType.MIGRATION_CARD);
        return res;
    }

    protected static final BiMap<String, AddressType> ADDRESSTYPEMAP = fillAddressTypeMap();

    private static BiMap<String, AddressType> fillAddressTypeMap() {
        BiMap<String, AddressType> res = new BiMap();
        res.put("RegisterAddress", AddressType.REGISTRATION);
        res.put("FactAddress", AddressType.ACTUAL);
        res.put("BornAddress", AddressType.BORN);
        return res;
    }

    ;

    protected static final BiMap<String, RolesType> ROLEMAP = fillRoleMap();

    private static BiMap<String, RolesType> fillRoleMap() {
        BiMap<String, RolesType> res = new BiMap();
        res.put("beneficiary", RolesType.BEN);
        res.put("insured", RolesType.LIFE_ASSURED);
        res.put("insurer", RolesType.HOLDER);
        return res;
    }

    protected static final BiMap<String, LegalFormType> LEGALFORMTYPEMAP = fillLegalFormTypeMap();

    private static BiMap<String, LegalFormType> fillLegalFormTypeMap() {
        BiMap<String, LegalFormType> res = new BiMap();
        res.put("ЗАО", LegalFormType.CJSC);
        res.put("АО", LegalFormType.JSC);
        res.put("ООО", LegalFormType.LTD);
        return res;
    }

    protected static final BiMap<String, ThirdPartyType> THIRDPARTYTYPEMAP = fillThirdPartyTypeMap();

    private static BiMap<String, ThirdPartyType> fillThirdPartyTypeMap() {
        BiMap<String, ThirdPartyType> res = new BiMap();
        res.put("1", ThirdPartyType.PERSON);
        res.put("2", ThirdPartyType.LEGAL_PERSON);
        return res;
    }

    ;
    protected static final BiMap<String, String> FIXTYPEMAP = fillFixTypeMap();

    private static BiMap<String, String> fillFixTypeMap() {
        BiMap<String, String> res = new BiMap();
        res.put("1", "FIXED_PERIOD");
        res.put("2", "ANNUAL_FIXED_DATE");
        return res;
    }

    protected static final BiMap<String, String> GENDERMAP = fillGenderMap();

    private static BiMap<String, String> fillGenderMap() {
        BiMap<String, String> res = new BiMap();
        res.put("1", "F");
        res.put("0", "M");
        return res;
    }

    protected static final BiMap<String, String> MARITIALMAP = fillMaritialMap();

    private static BiMap<String, String> fillMaritialMap() {
        BiMap<String, String> res = new BiMap();
        res.put("MARITAL01", "Холост");
        res.put("MARITAL02", "Женат");
        res.put("MARITAL03", "Не замужем");
        res.put("MARITAL04", "Замужем");
        return res;
    }

    protected static final BiMap<String, String> CURRENCYMAP = fillCurrencyMap();

    private static BiMap<String, String> fillCurrencyMap() {
        BiMap<String, String> res = new BiMap();
        res.put("3", "EUR");
        res.put("1", "RUB");
        res.put("2", "USD");
        // не поддерживаются.
        res.put("4", "GBP");
        res.put("5", "RUB USD");

        return res;
    }

    protected static final BiMap<String, String> RISKCATEGORYMAP = fillRiskCategoryMap();

    private static BiMap<String, String> fillRiskCategoryMap() {
        BiMap<String, String> res = new BiMap();

        res.put("SURVIVAL", "SURVIVAL");
        res.put("DISABILITY", "DISABILITY");
        res.put("DEATH", "DEATH");
        res.put("TRAUMA", "TRAUMA");
        res.put("OTHERS", "OTHERS");
        return res;
    }

    public Date xmlGCToDate(XMLGregorianCalendar xmlGC) {
        if ((xmlGC != null) && (xmlGC.toGregorianCalendar() != null)) {
            return xmlGC.toGregorianCalendar().getTime();
        } else {
            return null;
        }
    }

    protected Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }

    public static class BiMap<K, V> {

        HashMap<K, V> map = new HashMap<K, V>();
        HashMap<V, K> inversedMap = new HashMap<V, K>();

        public void put(K k, V v) {
            map.put(k, v);
            inversedMap.put(v, k);
        }

        public HashMap<V, K> inverse() {
            return inversedMap;
        }

        public V get(K k) {
            return map.get(k);
        }

        public K getKey(V v) {
            return inversedMap.get(v);
        }

    }

    protected BigDecimal getBigDecimalParamNoScale(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigDecimal.valueOf(Double.valueOf(bean.toString()));
        } else {
            return null;
        }
    }

    protected static BigDecimal getBigDecimalParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigDecimal.valueOf(Double.valueOf(bean.toString())).setScale(2, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    protected static BigDecimal getBigDecimalParam(Object bean, int scale) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigDecimal.valueOf(Double.valueOf(bean.toString())).setScale(scale, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    protected static BigDecimal getBigDecimalParam(Map<String, Object> map, String keyName) {
        BigDecimal bigDecimalParam = null;
        if (map != null) {
            bigDecimalParam = getBigDecimalParam(map.get(keyName));
        }
        return bigDecimalParam;
    }

    protected static String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else if (bean instanceof Double) {
            DecimalFormat df = new DecimalFormat("0");
            df.setMaximumFractionDigits(2);
            return df.format(Double.valueOf(bean.toString()).doubleValue());
        } else {
            return bean.toString();
        }
    }

    protected static String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }

    protected Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected Long getLongParam(Map<String, Object> map, String keyName) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParam(map.get(keyName));
        }
        return longParam;
    }

    protected Map<String, Object> getCurrencyById(Object currencyId, String login, String password) throws Exception {
        Map<String, Object> qparam = new HashMap<String, Object>();
        qparam.put("CurrencyID", currencyId);
        qparam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(REFWS_SERVICE_NAME, "getCurrencyByParams", qparam, login, password);
        return qres;
    }

    protected String getCoreSettingBySysName(String settingSysName, String login, String password) throws Exception {
        logger.debug(String.format("Getting core setting by system name [%s]...", settingSysName));
        Map<String, Object> coreSettingParams = new HashMap<String, Object>();
        coreSettingParams.put("SETTINGSYSNAME", settingSysName);
        coreSettingParams.put(RETURN_AS_HASH_MAP, "TRUE"); // getSysSettingBySysName работает только со строковыми значениями RETURN_AS_HASH_MAP
        String coreSettingValue = "";
        Map<String, Object> coreSetting = this.callService(COREWS, "getSysSettingBySysName", coreSettingParams, login, password);
        if (coreSetting != null) {
            coreSettingValue = getStringParam(coreSetting.get("SETTINGVALUE"));
            if (coreSettingValue.isEmpty()) {
                logger.debug(String.format("Core setting with system name [%s] does not exist or contain no value.", settingSysName));
            } else {
                logger.debug(String.format("Core setting with system name [%s] contain value [%s].", settingSysName, coreSettingValue));
            }
        } else {
            logger.debug("Method getSysSettingBySysName return no result.");
        }
        return coreSettingValue;
    }

    protected XMLGregorianCalendar getDate(Map<String, Object> map, String columnName) {
        return getDate(map, columnName, 180);
    }

    protected XMLGregorianCalendar getDate(Map<String, Object> map, String columnName, int offset) {
        XMLGregorianCalendar result = null;
        try {

            result = dateToXMLGC(getDateParam(map.get(columnName)));
            if (result != null) {
                result.setTimezone(offset);
                if ("STARTDATE".equalsIgnoreCase(columnName)) {
                    result.setHour(0);
                    result.setMinute(0);
                    result.setSecond(0);
                    result.setMillisecond(0);
                }
                if ("FINISHDATE".equalsIgnoreCase(columnName)) {
                    result.setHour(23);
                    result.setMinute(59);
                    result.setSecond(59);
                    result.setMillisecond(0);
                }
            }
        } catch (Exception e) {
            logger.debug("getDate exception " + columnName, e);
        }
        return result;
    }

    protected XMLGregorianCalendar dateToXMLGC(Date date) throws Exception {
        return dateToXMLGC(date, false);
    }

    public static String printDate(XMLGregorianCalendar value) {
        String result = null;

        if (value != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            result = dateFormat.format(value.toGregorianCalendar().getTime());
        }
        return result;
    }

    public static String printDate(XMLGregorianCalendar value, String format) {
        String result = null;

        if (value != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            result = dateFormat.format(value.toGregorianCalendar().getTime());
        }
        return result;
    }

    public static XMLGregorianCalendar parseDate(String value) {

        XMLGregorianCalendar result = null;
        if ((value != null) && (!value.isEmpty())) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(value);
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(date);
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

            } catch (DatatypeConfigurationException ex) {
                Logger logger = Logger.getLogger(LocalDateConverter.class);
                logger.error("Error parse string to XMLGregorianCalendar", ex);
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                Logger logger = Logger.getLogger(LocalDateConverter.class);
                logger.error("Error parse string to XMLGregorianCalendar", ex);
                throw new RuntimeException(ex);
            }
        }
        return result;
    }

    public static XMLGregorianCalendar parseDateTime(String value) {

        XMLGregorianCalendar result = null;
        if ((value != null) && (!value.isEmpty())) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(value);
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(date);
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            } catch (Exception ex) {
                return parseDate(value);
            }
        }
        return result;
    }

    /**
     * Fix т.к. система обрезает в датах секунды
     *
     * @param date         - дата
     * @param isFixSeconds - флаг добавления секунд до 59 (важно!!! должно быть
     *                     59, никаких 50 )
     */
    protected XMLGregorianCalendar dateToXMLGC(Date date, boolean isFixSeconds) throws Exception {
        XMLGregorianCalendar result = null;
        if (date != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(date);
            if (isFixSeconds) {
                gc.set(Calendar.SECOND, MAX_SECOND);
            }
            try {
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            } catch (DatatypeConfigurationException ex) {
                throw new Exception("Error convert Date to XMLGregorianCalendar", ex);
            }

        }
        return result;
    }

    protected String getFormattedDateStr(Date date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //return sdf.format(date);
        return printDate(dateToXMLGC(date));
    }

    protected String getFormattedDateStr(Date date, String format) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        //return sdf.format(date);
        return printDate(dateToXMLGC(date), format);
    }

    protected String getFormattedDateStr(Date date, String format, int offset) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        //return sdf.format(date);
        //return printDate(dateToXMLGC(date), format);
        XMLGregorianCalendar xgc = dateToXMLGC(date);
        if (xgc != null) {
            xgc.setTimezone(offset);
        }
        return printDate(xgc);
    }

    protected XMLGregorianCalendar getFormattedDate(Date date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //return sdf.format(date);
        return dateToXMLGC(date);
    }

    protected double yearsBetween(Date startDate, Date endDate) {
        double months = monthsBetween(startDate, endDate);
        double years = months / 12;
        double res = Math.ceil(years);
        return res;
    }

    protected double monthsBetween(Date startDate, Date endDate) {
        Calendar calSD = Calendar.getInstance();
        Calendar calED = Calendar.getInstance();

        calSD.setTime(startDate);
        int startDayOfMonth = calSD.get(Calendar.DAY_OF_MONTH);
        int startMonth = calSD.get(Calendar.MONTH);
        int startYear = calSD.get(Calendar.YEAR);

        calED.setTime(endDate);
        calED.add(Calendar.DATE, 1);
        int endDayOfMonth = calED.get(Calendar.DAY_OF_MONTH);
        int endMonth = calED.get(Calendar.MONTH);
        int endYear = calED.get(Calendar.YEAR);

        int diffMonths = endMonth - startMonth;
        int diffYears = endYear - startYear;
        int diffDays = calSD.getActualMaximum(Calendar.DAY_OF_MONTH) == startDayOfMonth
                && calED.getActualMaximum(Calendar.DAY_OF_MONTH) == endDayOfMonth ? 0 : endDayOfMonth - startDayOfMonth;
        Double res = ((diffYears * 12) + diffMonths + diffDays / 31.0);

        return Math.round(res);
    }

    protected Date getDateParam(Object date) {
        if (date != null) {
            if (date instanceof Double) {
                return XMLUtil.convertDate((Double) date);
            } else if (date instanceof BigDecimal) {
                return XMLUtil.convertDate((BigDecimal) date);
            } else if (date instanceof String) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    return sdf.parse((String) date);
                } catch (ParseException e) {

                }
            }
            return (Date) date;
        } else {
            return null;
        }
    }

    protected boolean isEmptyCollection(Object beanCollection) {
        return !(beanCollection instanceof Collection) || ((Collection) beanCollection).isEmpty();
    }

    protected boolean isEmptyMap(Object beanMap) {
        return !(beanMap instanceof Map) || ((Map) beanMap).isEmpty();
    }

    protected <T> String marshall(T inputObject) throws Exception {
        String result = null;
        try {
            String packageName = inputObject.getClass().getPackage().getName();
            JAXBContext jc = JAXBContext.newInstance(packageName);
            Marshaller m = jc.createMarshaller();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            m.marshal(inputObject, baos);
            result = new String(baos.toByteArray(), "UTF-8");
        } catch (JAXBException ex) {
            throw new IntegrationException("Ошибка преобразования объекта в xml", ex);
        } catch (RuntimeException ex) {
            throw new IntegrationException("Ошибка преобразования объекта в xml", ex);
        } catch (UnsupportedEncodingException ex) {
            throw new IntegrationException("Ошибка преобразования объекта в xml", ex);
        }
        return result;
    }

    protected <T> String marshall(T inputObject, Class inputClass) throws Exception {
        String result = null;
        try {
            String packageName = inputObject.getClass().getPackage().getName();
            JAXBContext jc = JAXBContext.newInstance(packageName);
            Marshaller m = jc.createMarshaller();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            QName qName = new QName("ru.sberinsur.esb.partner.shema", inputClass.getName());
            JAXBElement<T> root = new JAXBElement<T>(qName, inputClass, inputObject);

            m.marshal(root, baos);
            result = new String(baos.toByteArray(), "UTF-8");
        } catch (JAXBException ex) {
            throw new IntegrationException("Ошибка преобразования объекта в xml", ex);
        } catch (RuntimeException ex) {
            throw new IntegrationException("Ошибка преобразования объекта в xml", ex);
        } catch (UnsupportedEncodingException ex) {
            throw new IntegrationException("Ошибка преобразования объекта в xml", ex);
        }
        return result;
    }

    protected <T> T unmarshall(Class<T> docClass, String xmlText) throws Exception {
        T result = null;
        try {
            result = this.unmarshall(docClass, new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            throw new Exception(ex);
        }
        return result;
    }

    protected <T> T unmarshall(Class<T> docClass, InputStream inputStream) throws Exception {
        try {
            String packageName = docClass.getPackage().getName();
            JAXBContext jc = JAXBContext.newInstance(packageName);
            Unmarshaller u = jc.createUnmarshaller();
            T result = (T) u.unmarshal(inputStream);
            return result;
        } catch (JAXBException ex) {
            throw new Exception("Ошибка преобразования хмл в объект", ex);
        } catch (RuntimeException ex) {
            throw new Exception("Ошибка преобразования хмл в объект", ex);
        }
    }

    protected <T> T unmarshal(Class<T> docClass, InputStreamReader inputStream) throws JAXBException {
        String packageName = docClass.getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Unmarshaller u = jc.createUnmarshaller();
        T result = (T) u.unmarshal(inputStream);
        return result;//doc.getValue();
    }

    protected <T> T unmarshallroot(Class<T> docClass, InputStream inputStream) throws Exception {
        try {
            String packageName = docClass.getPackage().getName();
            JAXBContext jc = JAXBContext.newInstance(packageName);
            Unmarshaller u = jc.createUnmarshaller();
            Source source = new StreamSource(inputStream);
            JAXBElement<T> root = u.unmarshal(source, docClass);
            T result = root.getValue();
            return result;
        } catch (JAXBException ex) {
            throw new Exception("Ошибка преобразования хмл в объект", ex);
        } catch (RuntimeException ex) {
            throw new Exception("Ошибка преобразования хмл в объект", ex);
        }
    }

    protected static final String LIFE_PARTNER_SERVICE_LOCATION = "LIFEPARTNERSERVICE";

    protected String getLifePartnerServiceLocation() {
        return Config.getConfig(B2BPOSWS).getParam(LIFE_PARTNER_SERVICE_LOCATION, "http://10.1.51.36:8025/bsbsi-esb-partner/partnerService");
    }

    protected void processWsSecurity(BindingProvider proxy) {
        //if (this.isWsSecurityEnabled(serviceName)) {
        List<Handler> handlerChain
                = ((BindingProvider) proxy).getBinding().getHandlerChain();
        handlerChain.add(new UserNameTokenHandler("", ""));
        ((BindingProvider) proxy).getBinding().setHandlerChain(handlerChain);
        //}
    }

    protected AnswerImportListType callLifePartnerPutContract(ListContractImportType contractList) {
        AnswerImportListType resContrList = psc.processImportPolicy(contractList);
        return resContrList;
    }

    protected ListContractCutType callLifePartnerGetContractsCut(GetObjListType golt) {
        ListContractCutType resListContractCut = psc.processExportPolicyCut(golt);
        return resListContractCut;
    }

    protected ListContractType callLifePartnerGetContracts(GetObjType got) {
        ListContractType resListContractCut = psc.processExportPolicy(got);
        return resListContractCut;
    }

    protected ClaimCutType callLifePartnerGetClaimsCut(GetObjListType golt) {
        ClaimCutType resListClaimCut = psc.processExportClaimCut(golt);
        return resListClaimCut;
    }

    protected ClaimType callLifePartnerGetClaims(GetObjType got) {
        ClaimType resListClaimCut = psc.processExportClaim(got);
        return resListClaimCut;
    }

    protected AnswerImportListType callLifePartnerPutClaim(ClaimImportType cit) {
        AnswerImportListType answerClaim = psc.processImportClaim(cit);
        return answerClaim;
    }

    protected ChangeCutListType callLifePartnerGetChangesCut(GetChangeList gcl) {
        ChangeCutListType resChangeCutList = psc.processExportChangeCut(gcl);
        return resChangeCutList;
    }

    protected ChangeListType callLifePartnerGetChanges(GetChange gc) {
        ChangeListType resChangeList = psc.processExportChange(gc);
        return resChangeList;
    }

    protected AnswerImportListType callLifePartnerPutChanges(ChangeApplicationListType calt) {
        AnswerImportListType answerChange = psc.processImportChange(calt);
        return answerChange;
    }

    protected ShortFileInfo callLifePartnerPutFile(Folder folder, File file) {
        ShortFileInfo sfi = psc.addFile(folder, file);
        return sfi;
    }

    protected AnswerImportListType callLifePartnerAttachFiles(DocumentListImportType dlit) {
        AnswerImportListType ailt = new AnswerImportListType();
        List<AnswerImportType> aitList = ailt.getAnswerImport();
        List<AnswerImportType> res = psc.documentListImport(dlit.getDocument());
        aitList.addAll(res);
        return ailt;
    }

    protected RegistrationUserAnswer callLifePartnerRegistrationUser(RegistrationUser user) {
        RegistrationUserAnswer resUserReg = psc.processRegistrationUser(user);
        return resUserReg;
    }

    protected DeleteObjectListType callLifePartnerGetDeleted(GetDeletedObjects gdo) {
        DeleteObjectListType resDeletedList = psc.processDeletedObjects(gdo);
        return resDeletedList;
    }


    /*
    [13:26:55] Max L Volkov: [13:25:46] Илья Белостоцкий: Парни, есть один момент в интеграции который мы не учли. Только сейчас это узнал.
есть метод AnswerImportList, который надо вызывать после импорта PolicyCut
Если у нас импорт прошел удачно, то надо передавать Success, чтобы полис ушел из очереди.

у нас это учтено?

<<<
[13:27:39] Max L Volkov: [13:26:20] Илья Белостоцкий: этот метод универсален и для полисов и для убытков и для допников

<<<

     */
    protected void callLifeProcessResponseCut(AnswerImportListType ailt) {
        try {
            psc.processResponseCut(ailt);
        } catch (Exception ex) {
            logger.error("Error call processResponseCut", ex);
        }

    }

    protected Object getStateIdByName(String stateName, String typeName, int defaultState) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("STATENAME", stateName);
        queryParams.put("TYPENAME", typeName);
        Map<String, Object> stateRes = this.selectQuery("dsStateBrowseListByParams", "", queryParams);
        if (stateRes.get(RESULT) != null) {
            List<Map<String, Object>> stateListRes = (List<Map<String, Object>>) stateRes.get(RESULT);
            if (!stateListRes.isEmpty()) {
                return stateListRes.get(0).get("STATEID");
            }
        }
        logger.error("Unknown status " + stateName);
        return defaultState;
    }

    protected Object getStateIdByName(String stateName, String typeName) throws Exception {
        return getStateIdByName(stateName, "B2B_CONTRACT", 0);
    }

    protected Object getStateIdByName(String stateName) throws Exception {
        return getStateIdByName(stateName, "B2B_CONTRACT");
    }

    protected Map<String, Object> getStateMapByName(String stateName) throws Exception {
        //todo: когда будет список возможных состояний - загеристрировать схему состояний, получать ид из нее. либо решить где хранить это состояние
        // как вариант, сделать справочник расширенных атрибутов (один для всех продуктов.), куда сохранять все необходимое.
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("STATENAME", stateName);
        queryParams.put("TYPENAME", "B2B_CONTRACT");
        Map<String, Object> stateRes = this.selectQuery("dsStateBrowseListByParams", "", queryParams);
        if (stateRes.get(RESULT) != null) {
            List<Map<String, Object>> stateListRes = (List<Map<String, Object>>) stateRes.get(RESULT);
            if (!stateListRes.isEmpty()) {
                return stateListRes.get(0);
            }
        }
        logger.error("Unknown status " + stateName);
        return null;
    }

    protected Date processDate(String policyStartDate) {
        return xmlGCToDate(parseDate(policyStartDate));
    }

    protected Date processDate(XMLGregorianCalendar policyStartDate) {
        return xmlGCToDate(policyStartDate);
    }

    protected Date processDateTime(String policyStartDate) {
        return xmlGCToDate(parseDateTime(policyStartDate));
    }

    protected Date processDateTime(XMLGregorianCalendar policyStartDate) {
        return xmlGCToDate(policyStartDate);
    }

    protected Object getSysNameByProdver(String prodver, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("PRODVERID", prodver);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> prodVerRes = this.callService(B2BPOSWS, "dsB2BProductVersionBrowseListByParam", param, login, password);
        if (prodVerRes.get("PRODVERID") != null) {
            Map<String, Object> verParam = new HashMap();
            verParam.put("PRODID", prodVerRes.get("PRODID"));
            verParam.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> prodRes = this.callService(B2BPOSWS, "dsB2BProductBrowseListByParam", verParam, login, password);
            if (prodRes.get("EXTERNALCODE") != null) {
                return prodRes.get("EXTERNALCODE");
            }
        }
        return "";
    }

    protected Map<String, Object> findDepByLogin(String login, String password) throws Exception {
        Map<String, Object> qres1 = null;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("LOGIN", login);
        Map<String, Object> qres = this.callService(ADMINWS_SERVICE_NAME, "admAccountFind", queryParams, login, password);
        if (qres.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) qres.get(RESULT);
            if (!resList.isEmpty()) {
                if (resList.get(0).get("USERID") != null) {
                    Long userid = getLongParam(resList.get(0).get("USERID"));
                    Map<String, Object> userInfoParam = new HashMap<String, Object>();
                    userInfoParam.put("USERID", userid);
                    userInfoParam.put("ReturnAsHashMap", "TRUE");
                    qres1 = this.callService(ADMINWS_SERVICE_NAME, "admparticipantbyid", userInfoParam, login, password);
                    qres.putAll(qres1);
                }
            }
        }
        return qres;
    }

    public Object callServiceAndGetOneValue(String serviceName, String methodName, Map<String, Object> params, String login, String password, String keyName) throws Exception {
        boolean isVerboseLog = false;
        return callServiceAndGetOneValue(serviceName, methodName, params, isVerboseLog, login, password, keyName);
    }

    public Object callServiceAndGetOneValue(String serviceName, String methodName, Map<String, Object> params, boolean isVerboseLog, String login, String password, String keyName) throws Exception {
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> rawResult = this.callService(serviceName, methodName, params, isVerboseLog, login, password);
        if (rawResult != null) {
            Object oneValue = rawResult.get(keyName);
            if (oneValue != null) {
                return oneValue;
            }
            Object callError = rawResult.get("Error");
            if (callError != null) {
                logger.error("Error calling [" + serviceName + "] " + methodName + " with params " + params + ": " + callError);
            }
        }
        // todo: протоколирование неудачи при получении значения по переданному ключу
        logger.warn("Expected param (" + keyName + ") not found in result map after calling [" + serviceName + "] " + methodName + " with params " + params + "!");
        return null;
    }

    // аналог callServiceAndGetOneValue, но c протоколированием времени вызова (если взведен IS_CALLS_TIME_LOGGED)
    public Object callServiceTimeLoggedAndGetOneValue(String serviceName, String methodName, Map<String, Object> params, String login, String password, String keyName) throws Exception {
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> rawResult = this.callServiceTimeLogged(serviceName, methodName, params, login, password);
        if (rawResult != null) {
            Object oneValue = rawResult.get(keyName);
            if (oneValue != null) {
                return oneValue;
            }
            Object callError = rawResult.get("Error");
            if (callError != null) {
                logger.error("Error calling [" + serviceName + "] " + methodName + " with params " + params + ": " + callError);
            }
        }
        // todo: протоколирование неудачи при получении значения по переданному ключу
        logger.warn("Expected param (" + keyName + ") not found in result map after calling [" + serviceName + "] " + methodName + " with params " + params + "!");
        return null;
    }

    public Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params, boolean isVerboseLog, String login, String password) throws Exception {
        if (isVerboseLog) {
            return callServiceLogged(serviceName, methodName, params, login, password);
        } else {
            return callService(serviceName, methodName, params, login, password);
        }
    }

    public Map<String, Object> callServiceLogged(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        logger.debug("Вызван метод " + methodName + " с параметрами:\n\n" + params.toString() + "\n");
        // вызов действительного метода
        Map<String, Object> callResult = this.callService(serviceName, methodName, params, login, password);
        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        logger.debug("Метод " + methodName + " выполнился за " + callTimer + " мс. и вернул результат:\n\n" + callResult.toString() + "\n");
        // возврат результата
        return callResult;
    }

    // аналог callService, но c протоколированием времени вызова (если взведен IS_CALLS_TIME_LOGGED)
    public Map<String, Object> callServiceTimeLogged(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> callResult;
        if (IS_CALLS_TIME_LOGGED) {
            // c протоколированием времени вызова (если взведен IS_CALLS_TIME_LOGGED)
            long callTimerMs = System.currentTimeMillis();
            // вызов действительного метода
            callResult = this.callService(serviceName, methodName, params, login, password);
            // протоколирование времени вызова
            callTimerMs = System.currentTimeMillis() - callTimerMs;
            logger.debug(String.format("Method [%s] %s executed in %d milliseconds (approximately %.5f seconds).", serviceName, methodName, callTimerMs, ((double) callTimerMs) / 1000.0));
        } else {
            // вызов действительного метода без протоколирования времени вызова
            callResult = this.callService(serviceName, methodName, params, login, password);
        }
        // возврат результата
        return callResult;
    }

    protected static List<Map<String, Object>> getListFromResultMap(Map<String, Object> resultMap) {
        // List<Map<String, Object>> list = WsUtils.getListFromResultMap(resultMap);
        List<Map<String, Object>> list = null;
        if (resultMap != null && resultMap.get("Result") != null) {
            try {
                list = (List<Map<String, Object>>) resultMap.get("Result");
            } catch (Exception ex) {
                list = null;
            }
        }
        return list;
    }

    // callService для получения списка, но с сокращенным протоколированием
    protected List<Map<String, Object>> callServiceAndGetListFromResultMapLogged(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        logger.debug(String.format(
                "Call service method [%s] %s with parameters:\n\n%s\n",
                serviceName, methodName, params.toString())
        );
        // требуется извлечь из параметров вызова RETURN_AS_HASH_MAP, иначе вместо списка будет возращен первый элемент в виде мапы
        params.remove(RETURN_AS_HASH_MAP);
        // вызов действительного метода
        Map<String, Object> callResult = this.callService(serviceName, methodName, params, login, password);
        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        List<Map<String, Object>> callResultList = null;
        // анализ результата
        if (isCallResultOK(callResult)) {
            callResultList = getListFromResultMap(callResult);
        }
        if (callResultList == null) {
            // ошибка - не удалось получить список из мапы результа вызова
            logger.error(String.format(
                    "Service method method [%s] %s executed in %d ms and no list was returned as expected, but this call result instead:\n\n%s\n",
                    serviceName, methodName, callTimer, callResult == null ? "null" : callResult.toString())
            );
        } else {
            logger.debug(String.format(
                    "Service method method [%s] %s executed in %d ms and returned expected list contained %d record(s).",
                    serviceName, methodName, callTimer, callResultList.size())
            );
        }
        // возврат результата
        return callResultList;
    }

    protected String getLogin() {
        String login;
        Config config = Config.getConfig(B2BPOSWS);
        login = config.getParam("DEFAULTLOGIN", DEFAULT_LOGIN);
        return login;
    }

    protected String getUploadPath() {
        String path;
        Config config = Config.getConfig(B2BPOSWS);
        path = config.getParam("userFilePath", "C://Diasoft/UPLOAD");
        return path;
    }

    protected String getPassword() {
        String password;
        Config config = Config.getConfig(B2BPOSWS);
        password = config.getParam("DEFAULTPASSWORD", "356a192b7913b04c54574d18c28d46e6395428ab");
        return password; //To change body of generated methods, choose Tools | Templates.
    }

    protected Map<String, Object> getBaseActiveMap(String code, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("CODE", code);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> baseActiveMap = this.callService(B2BPOSWS, "dsB2BInvestBaseActiveBrowseListByParam", param, login, password);
        return baseActiveMap;
    }

    protected static boolean isCallResultOK(Map<String, Object> callResult) {
        return (callResult != null) && (callResult.get(RET_STATUS) != null) && (RET_STATUS_OK.equalsIgnoreCase(callResult.get(RET_STATUS).toString()));
    }

    protected static boolean isCallResultOKAndContains(Map<String, Object> callResult, String keyName) {
        return (isCallResultOK(callResult)) && (callResult.get(keyName) != null);
    }

    protected Map<String, Object> contractMakeTrans(Map<String, Object> exportData, String toStateSysName, String login, String password) throws Exception {
        String idFieldName = "CONTRID";
        String methodNamePrefix = "dsB2BContract";
        String typeSysName = "B2B_CONTRACT";
        if ("INSERTING".equalsIgnoreCase(getStringParam(exportData.get("CHANGETYPE")))) {
            String toStateId = getStringParam(exportData.get("STATEID"));
            //contractCreateInsObjState(toStateSysName, toStateId, getStringParam(exportData.get("CONTRID")), login, password);
            if (sm == null) {
                sm = new StateMachineManagerNew("INS",
                        Configuration.getSharedConfiguration().getDomain().getNode(WsUtils.getDataNodeName()).getDataSource());
            }
            Map<String, Object> smParams = new HashMap<String, Object>();
            smParams.put("LOGIN", login);
            smParams.put("USERNAME", "site1");
            smParams.put("TYPENAME", "B2B_CONTRACT");
            smParams.put("TYPEID", "2000");
            smParams.put("STATENAME", toStateSysName);
            smParams.put("STARTDATE", new Date());
            smParams.put("STATEID", toStateId);
            smParams.put("OBJID", getStringParam(exportData.get("CONTRID")));
            this.callService(B2BPOSWS, "dsB2BFastStateCreate", smParams, login, password);
            return null;
        } else {
            String toStateId = getStringParam(exportData.get("STATEID"));
            //contractCreateInsObjState(toStateSysName, toStateId, getStringParam(exportData.get("CONTRID")), login, password);
            return recordMakeTrans(exportData, toStateSysName, idFieldName, methodNamePrefix, typeSysName, login, password);
        }
    }

    //    protected Map<String, Object> contractCreateInsObjState(String stateSysName,String stateId, String contrId, String login, String password) throws Exception {
//
//
//
//
//        //sm.insertObjectState(smParams);
//        // return null;
//    }
    protected Map<String, Object> lossNoticeDocMakeTrans(Map<String, Object> exportData, String toStateSysName, String login, String password) throws Exception {
        String idFieldName = "LOSSNOTICEDOCID";
        String methodNamePrefix = "dsB2BLossNoticeDoc";
        String typeSysName = "B2B_LOSSNOTICEDOC";
        return recordMakeTrans(exportData, toStateSysName, idFieldName, methodNamePrefix, typeSysName, login, password);
    }

    protected Map<String, Object> recordMakeTrans(Map<String, Object> record, String toStateSysName, String idFieldName, String methodNamePrefix, String typeSysName, String login, String password) throws Exception {
        logger.debug("State transition started...");
        Map<String, Object> result = null;
        logger.debug("    Record type: " + typeSysName);
        Long recordID = getLongParam(record.get(idFieldName));
        logger.debug("    Record ID: " + recordID);
        String fromStateSysName = getStringParam(record.get("STATESYSNAME"));
        logger.debug("    Initial state: " + fromStateSysName);
        logger.debug("    Destination state: " + toStateSysName);
        if ((!fromStateSysName.isEmpty()) && (recordID != null)) {
            Map<String, Object> getTransParams = new HashMap<String, Object>();
            getTransParams.put("TYPESYSNAME", typeSysName);
            getTransParams.put("FROMSTATESYSNAME", fromStateSysName);
            getTransParams.put("TOSTATESYSNAME", toStateSysName);
            getTransParams.put("JOIN_TO_SMTYPE", true);
            getTransParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> qTransRes = this.callService(Constants.INSPOSWS, "dsTransitionsBrowseByParamEx", getTransParams, login, password);
            if (qTransRes.get("SYSNAME") != null) {
                String transSysName = getStringParam(qTransRes.get("SYSNAME"));
                String comment = getStringParam(record.get("COMMENTARY"));
                if (!transSysName.isEmpty()) {
                    logger.debug("    Selected transition: " + transSysName);
                    Map<String, Object> makeTransParams = new HashMap<String, Object>();
                    makeTransParams.put(idFieldName, recordID);
                    makeTransParams.put("DOCUMENTID", recordID);
                    makeTransParams.put("TYPESYSNAME", typeSysName);
                    makeTransParams.put("TRANSITIONSYSNAME", transSysName);
                    makeTransParams.put("COMMENTARY", comment);
                    makeTransParams.put(RETURN_AS_HASH_MAP, true);
                    /* ExternalService external = ExternalService.createInstance();
                    try {
                        logger.debug("call State_MakeTrans async");
                        result = external.callExternalService(Constants.B2BPOSWS, methodNamePrefix + "_State_MakeTrans", makeTransParams, login, password);
                    } catch (Exception ex) {
                        logger.error("Error State_MakeTrans", ex);
                    }*/

                    try {
                        result = this.callService(Constants.B2BPOSWS, methodNamePrefix + "_State_MakeTrans", makeTransParams, login, password);
                    } catch (Exception ex) {
                        logger.error("Error State_MakeTrans", ex);
                        // sm.insertObjectState()
                    }
                    if ("B2B_CONTRACT".equalsIgnoreCase(typeSysName)) {
                        if (result != null) {
                            Map<String, Object> updParam = new HashMap<>();
                            updParam.put("CONTRID", recordID);
                            //updParam.put("STATEID", record.get("STATEID"));
                            updParam.put("STATEID", result.get("STATEID"));
                            this.callService(Constants.B2BPOSWS, "dsB2BContractUpdate", updParam, login, password);
                        } else {
                            Map<String, Object> updParam = new HashMap<>();
                            updParam.put("CONTRID", recordID);
                            updParam.put("STATEID", record.get("STATEID"));
                            //updParam.put("STATEID", result.get("STATEID"));
                            this.callService(Constants.B2BPOSWS, "dsB2BContractUpdate", updParam, login, password);

                        }
                    }
                } else {
                    logger.debug("     No transition selected by this transition params.");
                }
            }
        }
        logger.debug("State transition finished with result: " + result);
        return result;
    }

    protected String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(B2BPOSWS);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }

    protected String getSeaweedFSUrl() {
        String login;
        Config config = Config.getConfig(B2BPOSWS);
        login = config.getParam("SEAWEEDFSURL", "");
        return login;
    }

    protected String getUploadFilePath() {

        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        java.io.File dirFile = new java.io.File(result);
        dirFile.mkdirs();

        return result;
    }

    protected Map<String, Object> b2bRequestQueueCreate(String request, String response, Long requestType, int status, String login, String password) throws Exception {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("REQUESTTYPEID", requestType);
        requestMap.put("TRYCOUNT", 0);
        requestMap.put("REQUESTDATE", new Date());
        requestMap.put("REQUESTSTATEID", status);
        requestMap.put("XMLREQUEST", request);
        requestMap.put("XMLRESPONSE", response);
        return newRequestQueue(requestMap, login, password);
    }

    protected Map<String, Object> b2bRequestQueueCreate(String request, String response, Long requestType, int status, Long objectId, String login, String password) throws Exception {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("REQUESTTYPEID", requestType);
        requestMap.put("TRYCOUNT", 0);
        requestMap.put("OBJID", objectId);
        requestMap.put("REQUESTDATE", new Date());
        requestMap.put("REQUESTSTATEID", status);
        requestMap.put("XMLREQUEST", request);
        requestMap.put("XMLRESPONSE", response);
        return newRequestQueue(requestMap, login, password);
    }

    protected Map<String, Object> b2bRequestQueueUpdate(Map<String, Object> queueMap, String request, String response, int stateId, String login, String password) throws Exception {

        Long tryCount = getLongParam(queueMap, "TRYCOUNT");
        tryCount = (tryCount == null) ? 0L : (tryCount + 1);

        queueMap.put("TRYCOUNT", tryCount);
        queueMap.put("REQUESTSTATEID", stateId);
        queueMap.put("PROCESSDATE", new Date());
        queueMap.put("XMLREQUEST", request);
        queueMap.put("XMLRESPONSE", response);

        // Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BRequestQueueUpdate", queueMap, login, password);
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BRequestQueueUpdate", queueMap, logger.isDebugEnabled(), login, password);  // !для отладки!
        return res;
    }

    private Map<String, Object> newRequestQueue(Map<String, Object> queueMap, String login, String password) throws Exception {
        // Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BRequestQueueCreate", queueMap, login, password);
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BRequestQueueCreate", queueMap, logger.isDebugEnabled(), login, password); // !для отладки!
        return res;
    }

    protected DocumentObjType process1CDoc(DocumentObjType doc, String pathPrefix, String login, String password) {

        DocumentObjType res = new DocumentObjType();
        if ((doc.getDocUrl() != null) && (!doc.getDocUrl().isEmpty())) {
            SessionController controller = new FileSessionControllerImpl();
            Map<String, Object> sessionParams = new HashMap<>();
            sessionParams.put(FileSessionControllerImpl.FS_TYPE_PARAMNAME, FS_1C);
            sessionParams.put(FileSessionControllerImpl.SOME_ID_PARAMNAME,doc.getDocUrl());
            sessionParams.put(FileSessionControllerImpl.USER_DOCNAME_PARAMNAME, doc.getName() + "." + doc.getDocExtension());

//            String fileNameStr = FS_1C + "@" + doc.getDocUrl() + "@" + doc.getName() + "." + doc.getDocExtension();
//            StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
//            String filenameEncript = scu.encrypt(fileNameStr + "@" + UUID.randomUUID());
            String filenameEncript = controller.createSession(sessionParams);
            res.setDocUrl(pathPrefix + filenameEncript);
        }
        res.setMistake(doc.getMistake());
        res.setName(doc.getName());
        res.setDocExtension(doc.getDocExtension());
        res.setDate(doc.getDate());

        return res;
    }

    protected String getCountryDigitCodeById(String citizenship, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("COUNTRYID", citizenship);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BCountryBrowseListByParam", param, login, password);
        if (res != null) {
            if (res.get("DIGITCODE") != null) {
                return res.get("DIGITCODE").toString();
            }
        }

        return "";
    }

    protected String getCountryNameCodeById(String citizenship, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("COUNTRYID", citizenship);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BCountryBrowseListByParam", param, login, password);
        if (res != null) {
            if (res.get("COUNTRYNAME") != null) {
                return res.get("COUNTRYNAME").toString();
            }
        }

        return "";
    }

    protected String getCountryIdByDigitCode(String citizenship, String login, String password) throws Exception {

        if (citizenship == null) return "";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("DIGITCODE", citizenship);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BCountryBrowseListByParam", param, login, password);
        if (res != null) {
            if (res.get("COUNTRYID") != null) {
                return res.get("COUNTRYID").toString();
            }
        }

        return "";
    }

    protected String getCountryIdByAlphaCode(String citizenship, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ALPHACODE3", citizenship);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BCountryBrowseListByParam", param, login, password);
        if (res != null) {
            if (res.get("COUNTRYID") != null) {
                return res.get("COUNTRYID").toString();
            }
        }

        return "";
    }

    protected Map<String, Object> getMapParam(Map<String, Object> parentMap, String keyName) {
        Map<String, Object> mapParamValue = getOrCreateMapParam(parentMap, keyName, false);
        return mapParamValue;
    }

    protected Map<String, Object> getOrCreateMapParam(Map<String, Object> parentMap, String keyName, boolean isCreate) {
        Map<String, Object> mapParamValue = null;
        if (parentMap != null) {
            mapParamValue = (Map<String, Object>) parentMap.get(keyName);
        }
        if ((mapParamValue == null) && (isCreate)) {
            mapParamValue = new HashMap<String, Object>();
            if (parentMap != null) {
                parentMap.put(keyName, mapParamValue);
            }
        }
        return mapParamValue;
    }

    protected Map<String, Object> getOrCreateMapParam(Map<String, Object> parentMap, String keyName) {
        Map<String, Object> mapParamValue = getOrCreateMapParam(parentMap, keyName, true);
        return mapParamValue;
    }

    protected List<Map<String, Object>> getListParam(Map<String, Object> parentMap, String keyName) {
        List<Map<String, Object>> listParamValue = getOrCreateListParam(parentMap, keyName, false);
        return listParamValue;
    }

    protected List<Map<String, Object>> removeListParam(Map<String, Object> parentMap, String keyName) {
        List<Map<String, Object>> listParamValue = getListParam(parentMap, keyName);
        if (parentMap != null) {
            parentMap.remove(keyName);
        }
        return listParamValue;
    }

    protected List<Map<String, Object>> getOrCreateListParam(Map<String, Object> parentMap, String keyName, boolean isCreate) {
        List<Map<String, Object>> listParamValue = null;
        if (parentMap != null) {
            listParamValue = (List<Map<String, Object>>) parentMap.get(keyName);
        }
        if ((listParamValue == null) && (isCreate)) {
            listParamValue = new ArrayList<Map<String, Object>>();
            if (parentMap != null) {
                parentMap.put(keyName, listParamValue);
            }
        }
        return listParamValue;
    }

    protected List<Map<String, Object>> getOrCreateListParam(Map<String, Object> parentMap, String keyName) {
        List<Map<String, Object>> listParamValue = getOrCreateListParam(parentMap, keyName, true);
        return listParamValue;
    }

    // формирование из списка специальной мапы (ключ - значение указанного поля, значение - соответсвующий элемент списка)
    // (например, для формирования мап по сис. наименованиям и т.п.)
    protected static Map<String, Map<String, Object>> getMapByFieldStringValues(List<Map<String, Object>> list, String fieldName) {
        Map<String, Map<String, Object>> mapByFieldStringValues = new HashMap<String, Map<String, Object>>();
        if ((list != null) && (fieldName != null) && (list.size() > 0) && (!fieldName.isEmpty())) {
            for (Map<String, Object> bean : list) {
                String fieldValue = getStringParam(bean, fieldName);
                mapByFieldStringValues.put(fieldValue, bean);
            }
        }
        return mapByFieldStringValues;
    }

    /**
     * формирование из списка специальной мапы (ключ - значение указанного поля
     * указанной подчиненной мапы, значение - соответсвующий элемент списка)
     * (например, для формирования мап по сис. наименованиям для словарной
     * системы когда сис. наименовние находится не в элменте, а во вложенной
     * мапе *_EN и т.п.)
     *
     * @param list
     * @param subMapName
     * @param fieldName
     * @return
     */
    protected static Map<String, Map<String, Object>> getMapByFieldStringValues(List<Map<String, Object>> list, String subMapName, String fieldName) {
        Map<String, Map<String, Object>> mapByFieldStringValues = new HashMap<String, Map<String, Object>>();
        if ((list != null) && (!list.isEmpty()) && (fieldName != null) && (!fieldName.isEmpty())) {
            if ((subMapName == null) || (subMapName.isEmpty())) {
                // если не передано имя ключа, указывающего на подчиненную мапу - используется обычный вариант getMapByFieldStringValues
                mapByFieldStringValues = getMapByFieldStringValues(list, fieldName);
            } else {
                for (Map<String, Object> bean : list) {
                    Map<String, Object> subMap = (Map<String, Object>) bean.get(subMapName);
                    if (subMap != null) {
                        String fieldValue = getStringParam(subMap, fieldName);
                        mapByFieldStringValues.put(fieldValue, bean);
                    }
                }
            }
        }
        return mapByFieldStringValues;
    }

    // копия из DSDateUtil
    /*
    public static BigDecimal convertDate(Date time) {
        return BigDecimal.valueOf((double)time.getTime() / 8.64E7D).add(BigDecimal.valueOf(new Double("25569.0")));
    }
     */
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

    protected String[] getFileNameList(String dirName, String extName) {
        java.io.File dir = new java.io.File(dirName);
        if (dir.isDirectory()) {

            String[] list = dir.list(new FilenameFilter() {
                @Override
                public boolean accept(java.io.File f, String s) {
                    return s.endsWith(extName);
                }
            });
            if (list.length > 0) {
                return list;
            }
        }
        return null;
    }

    public HashMap<String, Long> cacheProductVer = new HashMap();

    protected Object getProdverBySysName2(String productName, String login, String password) throws Exception {
        Long verId = cacheProductVer.get(productName);
        if (verId != null) {
            return verId;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("EXTERNALCODE", productName);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> prodRes = this.callService(B2BPOSWS, "dsB2BProductBrowseListByParam", param, login, password);
        if (prodRes.get("PRODID") != null) {
            Map<String, Object> verParam = new HashMap();
            verParam.put("PRODID", prodRes.get("PRODID"));
            verParam.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> prodVerRes = this.callService(B2BPOSWS, "dsB2BProductVersionBrowseListByParam", verParam, login, password);
            if (prodVerRes.get("PRODVERID") != null) {
                cacheProductVer.put(productName, Long.parseLong(prodVerRes.get("PRODVERID").toString()));
                return prodVerRes.get("PRODVERID");
            }
        }
        logger.error("Unknown product " + productName);
        return 0;
    }

    public HashMap<Long, Map<String, Object>> cacheProductConf = new HashMap();

    protected Map<String, Object> getConfigByProdver2(Long prodVerId, String login, String password) throws Exception {
        Map<String, Object> prodConfMap = cacheProductConf.get(prodVerId);
        if (prodConfMap != null) {
            return prodConfMap;
        }

        Map<String, Object> getProdConfIDParams = new HashMap<>();
        getProdConfIDParams.put("PRODVERID", prodVerId);
        getProdConfIDParams.put("ReturnAsHashMap", "TRUE");
        prodConfMap = this.callService(B2BPOSWS, "dsB2BProductConfigBrowseListByParam", getProdConfIDParams, login, password);
        if (prodConfMap != null) {
            cacheProductConf.put(prodVerId, prodConfMap);
            return prodConfMap;
        } else {
//                logger.error("getFullInfo: prodconf not found");
        }

        return null;
    }

    public HashMap<Long, Map<String, Object>> cacheProduct = new HashMap();

    protected Map<String, Object> getProdByConfId2(Long prodConfId, String login, String password) throws Exception {
        Map<String, Object> prodMap = cacheProduct.get(prodConfId);
        if (prodMap != null) {
            return prodMap;
        }

        Map<String, Object> prodParam = new HashMap<>();
        prodParam.put("PRODCONFID", prodConfId);
        prodParam.put("HIERARCHY", true);
        prodParam.put(RETURN_AS_HASH_MAP, "TRUE");
        prodMap = this.callService(B2BPOSWS, "dsProductBrowseByParams", prodParam, login, password);
        if (prodMap != null) {
            cacheProduct.put(prodConfId, prodMap);
            return prodMap;
        } else {
//                    logger.error("getFullInfo: prodStruct browse error");
        }

        return null;
    }

    public HashMap<String, Map<String, Object>> cacheProductProgram = new HashMap();

    protected Map<String, Object> getProdProgBySysName2(String policyProgram, Long prodVerId, String login, String password) throws Exception {
        if (prodVerId != null) {
            String prodVerIdStr = prodVerId.toString();
            if (!"0".equals(prodVerIdStr)) {

                Map<String, Object> prodProgMap = cacheProductProgram.get(prodVerIdStr + "_" + policyProgram);
                if (prodProgMap != null) {
                    return prodProgMap;
                }

                Map<String, Object> param = new HashMap<String, Object>();
                param.put("PRODVERID", prodVerId);
                param.put("SYSNAME", policyProgram);
                param.put("ReturnAsHashMap", "TRUE");
                prodProgMap = this.callService(B2BPOSWS, "dsB2BProductProgramBrowseListByParam", param, login, password);
                if (prodProgMap.get("PRODPROGID") != null) {
                    cacheProductProgram.put(prodVerIdStr + "_" + policyProgram, prodProgMap);
                    return prodProgMap;
                }
                cacheProductProgram.put(prodVerIdStr + "_" + policyProgram, null);
            }
        }
        logger.error("Unknown programm " + policyProgram);
        return null;
    }

    public HashMap<Long, Map<String, Object>> cacheCurrency = new HashMap();

    protected Map<String, Object> getCurrencyById2(Long currencyId, String login, String password) throws Exception {

        Map<String, Object> currencyMap = cacheCurrency.get(currencyId);
        if (currencyMap != null) {
            return currencyMap;
        }

        Map<String, Object> qparam = new HashMap<String, Object>();
        qparam.put("CurrencyID", currencyId);
        qparam.put("ReturnAsHashMap", "TRUE");
        currencyMap = this.callService(REFWS_SERVICE_NAME, "getCurrencyByParams", qparam, login, password);
        cacheCurrency.put(currencyId, currencyMap);

        return currencyMap;
    }

    public HashMap<String, Map<String, Object>> cacheBA = new HashMap();

    protected Map<String, Object> getBaseActiveMap2(String code, String login, String password) throws Exception {
        Map<String, Object> baseActiveMap = cacheBA.get(code);
        if (baseActiveMap != null) {
            return baseActiveMap;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("CODE", code);
        param.put("ReturnAsHashMap", "TRUE");
        baseActiveMap = this.callService(B2BPOSWS, "dsB2BInvestBaseActiveBrowseListByParam", param, login, password);
        cacheBA.put(code, baseActiveMap);

        return baseActiveMap;
    }

    public HashMap<String, Map<String, Object>> cacheState = new HashMap();

    protected Map<String, Object> getStateMapByName2(String stateName) throws Exception {
        Map<String, Object> stateRes = cacheState.get(stateName);
        if (stateRes != null) {
            return stateRes;
        }

        //todo: когда будет список возможных состояний - загеристрировать схему состояний, получать ид из нее. либо решить где хранить это состояние
        // как вариант, сделать справочник расширенных атрибутов (один для всех продуктов.), куда сохранять все необходимое.
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("STATENAME", stateName);
        queryParams.put("TYPENAME", "B2B_CONTRACT");
        stateRes = this.selectQuery("dsStateBrowseListByParams", "", queryParams);
        if (stateRes.get(RESULT) != null) {
            List<Map<String, Object>> stateListRes = (List<Map<String, Object>>) stateRes.get(RESULT);
            if (!stateListRes.isEmpty()) {
                cacheState.put(stateName, stateListRes.get(0));
                return stateListRes.get(0);
            }
        }
        logger.error("Unknown status " + stateName);
        return null;
    }

    public HashMap<String, Map<String, Object>> cachePayVar = new HashMap();

    protected Map<String, Object> getPayVarIdBySysName2(String sysName, String login, String password) throws Exception {
        Map<String, Object> payVarMap = cachePayVar.get(sysName);
        if (payVarMap != null) {
            return payVarMap;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("SYSNAME", sysName);
        params.put("ReturnAsHashMap", "TRUE");
        payVarMap = this.callService(B2BPOSWS, "dsB2BPaymentVariantBrowseListByParam", params, login, password);
        cachePayVar.put(sysName, payVarMap);

        return payVarMap;
    }

    protected Map<String, Object> processInvestCoverage(Map<String, Object> contrExtMap, List<InvestCoverageType> icList, String login, String password) throws Exception {
        // исходя из фт - базовый актив, и инвестиционное состояние - должны быть в 1 экземпляре, поэтому, идем по списку, и первое не пустое поле сохраняем
        // в расширенных атрибутаз.
        for (InvestCoverageType investCoverage : icList) {
            if (!contrExtMap.containsKey("BASEACTIVE")) {
                if ((investCoverage.getBaseActive() != null) && (!investCoverage.getBaseActive().isEmpty())) {
                    contrExtMap.put("BASEACTIVE", investCoverage.getBaseActive());
                }
            }
            if (!contrExtMap.containsKey("BASEACTIVECODE")) {
                if ((investCoverage.getBaseActiveCode() != null) && (!investCoverage.getBaseActiveCode().isEmpty())) {
                    contrExtMap.put("BASEACTIVECODE", investCoverage.getBaseActiveCode());
                }
            }
            if (!contrExtMap.containsKey("INVESTCONDITION")) {
                if ((investCoverage.getInvestConditions() != null) && (!investCoverage.getInvestConditions().isEmpty())) {
                    //contrExtMap.put("INVESTCONDITION", investCoverage.getInvestConditions());
                    contrExtMap.put("INVESTCONDITIONCODE", investCoverage.getInvestConditions());
                    Map<String, Object> baseActiveMap = getBaseActiveMap(investCoverage.getInvestConditions(), login, password);
                    contrExtMap.put("INVESTCONDITION", baseActiveMap.get("NAME"));

                }
            }

            //contrMap.put("CONTREXTMAP", contrExtMap);
        }
        return contrExtMap;
    }

    protected Map<String, Object> loadContractValues(Long contractId, Long hbdataverid, String login, String password) throws Exception {
        if (hbdataverid != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(RETURN_AS_HASH_MAP, "TRUE");
            params.put("CONTRID", contractId);
            List<Long> hbDataVerIdList = new ArrayList();
            hbDataVerIdList.add(hbdataverid);
            params.put("HBDATAVERIDLIST", hbDataVerIdList);
            return this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParamByHBDVIdList", params, login, password);
        } else {
            return null;
        }
    }

    protected void createContractValues(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        logger.debug("createContractValues begin");
        Map<String, Object> params = new HashMap<String, Object>();
        if (contrExtMap == null) {
            contrExtMap = new HashMap<String, Object>();
        }
        params.putAll(contrExtMap);
        Object contrExtID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", params, login, password, "CONTREXTID");
        if (contrExtID != null) {
            contrExtMap.put("CONTREXTID", contrExtID);
        }
        logger.debug("createContractValues end");
    }

    protected void updateContractValues(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        logger.debug("updateContractValues begin");
        Map<String, Object> params = new HashMap<String, Object>();
        if (contrExtMap != null) {
            params.putAll(contrExtMap);
            Object contrExtID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", params, login, password, "CONTREXTID");
            if (contrExtID != null) {
            }
        }
        logger.debug("updateContractValues end");
    }

    protected List<Map<String, Object>> loadHandbookData(Map<String, Object> params, String hbName, String login, String password) throws Exception {
        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("HANDBOOKNAME", hbName);
        hbParams.put("HANDBOOKDATAPARAMS", params);
        hbParams.put(RETURN_LIST_ONLY, true);
        Map<String, Object> resultMap = this.callServiceTimeLogged(Constants.B2BPOSWS, "dsB2BHandbookDataBrowseByHBName", hbParams, login, password);
        List<Map<String, Object>> resultList = WsUtils.getListFromResultMap(resultMap);
        return resultList;
    }

    public class Route {

        String programSysname;
        String route;
        String selectQuery;
        Map<String, Object> queryRequiredParams;
        Map<String, Object> programMap;
        Long prodVerId;
        Long prodProgId;

        public Route(Map<String, Object> m) {
            programMap = new HashMap<>();
            programSysname = (String) m.get("PROGRAMNAME");
            route = (String) m.get("ROUTE");
            // параметры для selectQuery, заданные наперед
            queryRequiredParams = new HashMap<>();
            // параметры SYSNAME названы везде одинаково
            queryRequiredParams.put("SYSNAME", programSysname);
            switch (route) {
                case "B2B_PRODVER": {
                    selectQuery = "dsB2BProductVersionBrowseListByParamEx";
                    break;
                }
                case "B2B_PRODSTRUCT": {
                    selectQuery = "dsB2BProductStructureBaseBrowseListByParamEx";
                    queryRequiredParams.put("DISCRIMINATOR", 3L);
                    break;
                }
                case "B2B_PRODPROG": {
                    selectQuery = "dsB2BProductProgramBrowseListByParam";
                    break;
                }
                default: {
                    break;
                }
            }
        }

        public Long getProdProgId() {
            return prodProgId;
        }

        void setProgramMap(Map<String, Object> newData) {
            programMap.clear();
            programMap.putAll(newData);
        }

        public Long getProdVerId() {
            return prodVerId;
        }

        public Map<String, Object> getProgramMap() {
            return programMap;
        }

        void loadProgByVersionId() throws Exception {
            Map<String, Object> callParams = new HashMap<>();
            callParams.put("PRODVERID", getProdVerId());
            Map<String, Object> callRes = IntegrationBaseFacade.this.selectQuery("dsB2BProductProgramBrowseListByParam", "dsB2BProductProgramBrowseListByParamCount", callParams);
            List<Map<String, Object>> list = (List<Map<String, Object>>) callRes.get(RESULT);
            if (list != null && list.size() > 0) {
                if (list.size() == 1) {
                    setProgramMap(list.get(0));
                    this.prodProgId = getLongParam(list.get(0), "PRODPROGID");
                } else {
                    logger.error("IntegrationBaseFacade.Route#loadProgByVersionId: found more than 1 program by PRODVERID = '" + getProdVerId() + "'. It is not fine");
                }
            } else {
                logger.error("IntegrationBaseFacade.Route#loadProgByVersionId: programs not found by PRODVERID = '" + getProdVerId() + "'. It is not fine");
            }
        }

        // загружаем данные роута
        public Route loadData() throws Exception {
            Map<String, Object> callres = IntegrationBaseFacade.this.selectQuery(selectQuery, selectQuery + "Count", queryRequiredParams);
            List<Map<String, Object>> list = (List<Map<String, Object>>) callres.get(RESULT);
            if (list != null && list.size() > 0) {
                Map<String, Object> map = list.get(0);
                prodVerId = getLongParam(map.get("PRODVERID"));
                prodProgId = null;
                switch (this.route) {
                    case "B2B_PRODPROG": {
                        prodProgId = getLongParam(programMap, "PRODPROGID");
                        setProgramMap(map);
                        break;
                    }
                    case "B2B_PRODSTRUCT": {
                        prodProgId = getLongParam(programMap, "PRODPROGID");
                        if (prodProgId == null) {
                            loadProgByVersionId();
                        }
                        break;
                    }
                    case "B2B_PRODVER": {
                        loadProgByVersionId();
                        break;
                    }
                }
            }
            return this;
        }
    }
}
