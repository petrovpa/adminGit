/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade;

import com.bivgroup.rest.api.system.crypto.JsonMapCrypter;
import com.bivgroup.services.b2bposws.system.Constants;
import com.bivgroup.services.b2bposws.system.files.SeaweedsGetters;
import com.bivgroup.sessionutils.SessionController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.log4j.Logger; // import java.util.logging.Logger
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.utils.DefaultedHashMap;
import ru.diasoft.utils.XMLUtil;
import ru.diasoft.utils.date.DSDateUtil;
import ru.diasoft.utils.format.number.NumberFormatUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bivgroup.rest.api.system.ParamConstants.*;
import  static com.bivgroup.services.b2bposws.facade.B2BFileSessionController.*;
import static com.bivgroup.services.b2bposws.facade.B2BPosServiceSessionController.B2B_USERGROUPS_PARAMNAME;
import static ru.diasoft.services.inscore.system.WsConstants.ADMINWS;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * @author averichevsm
 */
@BOName("B2BBase")
public class B2BBaseFacade extends BaseFacade implements SeaweedsGetters {

    protected Logger logger = Logger.getLogger(this.getClass());
    protected final String AUDIT_LOGGERNAME = "AUDIT";
    protected Logger auditLogger = Logger.getLogger(AUDIT_LOGGERNAME);

    // флаг для генерации только серии договора для PrepareSaveMethod (у каждого продукта он свой)
    protected static final String IS_GEN_SER_DRAFT_PARAM_NAME = "IS_GEN_SER_DRAFT";
    protected static final boolean IS_GEN_SER_DRAFT_VALUE = false;

    private static final String PROJECT_PARAM_NAME = "project";
    public static final String SERVICE_NAME = Constants.B2BPOSWS;

    protected static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    protected static final String ADMINWS_SERVICE_NAME = Constants.ADMINWS;
    protected static final String LIBREOFFICEREPORTSWS_SERVICE_NAME = Constants.LIBREOFFICEREPORTSWS;
    protected static boolean isDebug;

    private static final Set<Integer> ANUM = new HashSet<Integer>() {
        {
            add(1);
            add(21);
            add(31);
            add(41);
            add(51);
            add(61);
            add(71);
            add(81);
            add(91);
        }
    };
    private static final Set<Integer> LETNUM = new HashSet<Integer>() {
        {
            add(2);
            add(3);
            add(4);
            add(22);
            add(23);
            add(24);
            add(32);
            add(33);
            add(34);
            add(42);
            add(43);
            add(44);
            add(52);
            add(53);
            add(54);
            add(62);
            add(63);
            add(64);
            add(72);
            add(73);
            add(74);
            add(82);
            add(83);
            add(84);
            add(92);
            add(93);
            add(94);
        }
    };

    protected static Map<String, String> filterConditionOperators = new HashMap();
    static {
        filterConditionOperators.put("3", "=");
        filterConditionOperators.put("4", "<>");
        filterConditionOperators.put("5", "<");
        filterConditionOperators.put("6", ">");
        filterConditionOperators.put("7", ">=");
        filterConditionOperators.put("8", ">=");
        filterConditionOperators.put("9", " BETWEEN ");
        filterConditionOperators.put("10", " IN ");
        filterConditionOperators.put("11", " LIKE ");
        filterConditionOperators.put("12", " LIKE_IGNORE_CASE ");
    }

    /**
     * dateFormatterMonth = new SimpleDateFormat("«dd» MMMMM yyyy", new Locale("ru")) + .setMonths(MONTHS_FOR_STRING_DATE)
     */
    private SimpleDateFormat dateFormatterMonth;
    // для создания формата дат для русских месяцев в нужном виде (например, "января" вместо "Январь")
    protected static final String[] MONTHS_FOR_STRING_DATE = {
            "января",
            "февраля",
            "марта",
            "апреля",
            "мая",
            "июня",
            "июля",
            "августа",
            "сентября",
            "октября",
            "ноября",
            "декабря"
    };

    protected static final String[] avaliableTableAliases = {
            "T",
            "T1",
            "T2",
            "T3",
            "T31",
            "T4",
            "T5",
            "T51",
            "T52",
            "T53",
            "T6",
            "T72",
            "DD",
            "IT",
            "TS",
            "T_OUT",
            "PCLIENT",
            "ST",
            "CHAT"
    };

    protected static final String[] avaliableFields = {
            "ID",
            "CANCELDATE",
            "CONTRNODEID",
            "CONTRNDNUMBER",
            "CONTRNUMBER",
            "CONTRPOLNUM",
            "CONTRPOLSER",
            "DEPTSHORTNAME",
            "DOCUMENTDATE",
            "DECLARATIONDATE",
            "CREATEDATE",
            "UPDATEDATE",
            "DURATION",
            "EXTERNALID",
            "FINISHDATE",
            "CONTRID",
            "INSAMCURRENCYID",
            "INSAMVALUE",
            "INSREGIONCODE",
            "INSURERID",
            "INSURERREPID",
            "NUMMETHODID",
            "PAYVARID",
            "PREMCURRENCYID",
            "PREMDELTA",
            "PREMVALUE",
            "PRODPROGID",
            "PRODVERID",
            "SALESOFFICE",
            "SELLERID",
            "STARTDATE",
            "VERNUMBER",
            "SYSNAME",
            "PUBLICNAME",
            "STATEID",
            "IMGPATH",
            "JSPATH",
            "PRODCONFID",
            "NAME",
            "PRODID",
            "BRIEFNAME",
            "VALUE",
            "EVENTID",
            "SOURCEID",
            "ISRESOLVED",
            "LASTNAME",
            "PROPERTYSOURCEID",
            "FLAGEVENTID",
            "KINDHANDBOOKNAME",
            "STATUS",
            "LOGIN",
            "CREATIONDATE",
            "ROLENAME",
            "ROLESYSNAME",
            "DESCRIPTION",
            "ITEMNAME",
            "ITEMSYSNAME",
            "ACTIONURL",
            "PICTUREURL",
            "POSITION",
            "FIRSTNAME",
            "MIDDLENAME",
            "MENUTYPEID",
            "PARENTMENUID",
            "DEPTSHORTNAME",
            "DEPTFULLNAME",
            "DEPTCODE",
            "SURNAME",
            "FROMDATE",
            "TODATE",
            "NOTE",
            "APLBRIEFNAME",
            "EVENTCODE",
            "APPLNUMBER",
            "CLIENTBRIEFNAME",
            "CLIENTEMAIL",
            "CLIENTPHONE",
            "EVENTSTATESYSNAME",
            "CLIENTNAME",
            "TITLE",
            "DATEOFBIRTH",
            "FULLNAME",
            "LOSSNOTICEID"
    };
    // выкупные суммы
    protected static final String REDEMPTION_SUM_LIST_LOAD_PARAMNAME = "REDEMPTIONSUMLISTLOAD";
    protected static final String REDEMPTION_SUM_LIST_PARAMNAME = "REDEMPTIONSUMLIST";
    protected static final String REDEMPTION_SUM_LIST_UPDATE_PARAMNAME = "REDEMPTIONSUMLISTUPDATE";

    protected static final String SAVINGSSCHEDULE_SUM_LIST_PARAMNAME = "SAVINGSCHEDULELIST";
    protected static final String SAVINGSSCHEDULE_SUM_LIST_UPDATE_PARAMNAME = "SAVINGSCHEDULELISTUPDATE";

    // синонимы для констант из RowStatus
    protected static final RowStatus UNMODIFIED = RowStatus.UNMODIFIED;
    protected static final RowStatus INSERTED = RowStatus.INSERTED;
    protected static final RowStatus MODIFIED = RowStatus.MODIFIED;
    protected static final RowStatus DELETED = RowStatus.DELETED;
    protected static final int UNMODIFIED_ID = UNMODIFIED.getId();
    protected static final int INSERTED_ID = INSERTED.getId();
    protected static final int MODIFIED_ID = MODIFIED.getId();
    protected static final int DELETED_ID = DELETED.getId();
    protected static final String ROWSTATUS_PARAM_NAME = RowStatus.ROWSTATUS_PARAM_NAME;
    protected static final String ROWSTATUS_LOG_PATTERN = ROWSTATUS_PARAM_NAME + " = %s (%d)";
    protected static final String REFWS_SERVICE_NAME = Constants.REFWS;
    protected static final String WEBSMSWS_SERVICE_NAME = Constants.WEBSMSWS;
    protected static final String COREWS_SERVICE_NAME = Constants.COREWS;

    // флаги статусов для методов по работе с участниками из CRM (аналог RowStatus-а)
    protected static final String FLAG_PARAM_NAME = "FLAG";
    protected static final String FLAG_ADD = "ADD";
    protected static final String FLAG_UPD = "UPD";
    protected static final String FLAG_DEL = "DEL";

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat dateFormatterNoDots = new SimpleDateFormat("ddMMyyyy");

    private static final String DATE_TIME_FORMATTER_PATTERN = "dd.MM.yyyy HH:mm";
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMATTER_PATTERN);

    private static final String DATE_TIME_TZ_FORMATTER_PATTERN = DATE_TIME_FORMATTER_PATTERN + " z";
    private final SimpleDateFormat dateTimeFormatterTZ = new SimpleDateFormat(DATE_TIME_TZ_FORMATTER_PATTERN);

    private static final String DATE_TIME_FORMATTER_DEFAULT_TZ_STR = " GMT+03:00";

    // наименование ключа в мапе контракта, указывающего на список подразделений (для которых необходимо добавлять права на каждый создаваемый договор)
    protected static final String DEPARTMENTS_KEY_NAME = "DEPARTMENTLIST";

    private static final String KEY_NAME_DATE_SUFFIX = "DATE"; // например, SOMEDATE
    private static final String KEY_NAME_TIME_SUFFIX = "TIME"; // прибавляется к имени ключей с датами, поэтому, например, SOMEDATETIME
    protected static final String KEY_NAME_DATE_SUFFIX_NEW = "$date"; // например, SOME$date
    protected static final String KEY_NAME_TIME_SUFFIX_NEW = "time"; // прибавляется к имени ключей с датами, поэтому, например, SOME$datetime

    // имя параметра с признаком выполнения вызова из гейта
    protected static final String IS_CALL_FROM_GATE_PARAMNAME = "ISCALLFROMGATE";

    // имя параметра с признаком выполнения вызова из uniopenapiws
    protected static final String IS_CALL_FROM_UNIOPENAPI_PARAMNAME = "ISCALLFROMUNIOPENAPI";

    // Наименования (NAME) констант продукта (таблица B2B_PRODDEFVAL)
    protected static final String RISKSUMSHBDATAVERID_PARAMNAME = "RISKSUMSHBDATAVERID";
    protected static final String MEMBERHBDATAVERID_PARAMNAME = "MEMBERHBDATAVERID"; // Версия справочника атирбутов застрахованного

    // имя параметра для методов типа dsB2BHandbookDataBrowseByHBName, которые могут вернуть как мапу (где ключ - имя справочника, а значение - список его записей) так и сразу список
    // если true, то возвращает только список с записями справочника; иначе - возвращает мапу (ключ - имя справочника, значение - список с записями справочника)
    protected static final String RETURN_LIST_ONLY = "ReturnListOnly";

    // строка для включения в уведомления и в печатные документы для случаев когда не удалось определить название Партнера
    private static final String NO_PARTNER_INFO_FOUND = "< Сведения о партнере не найдены >";

    // Строковый код отдела "Партнеры" в орг. структуре предприятия
    protected static final String PARTNERS_DEPARTMENT_CODE_LIKE = "agencyNetwork";
    // Имя ключа параметра для передачи строкового кода отдела "Партнеры" (PARTNERS_DEPARTMENT_CODE_LIKE) в запросы
    protected static final String PARTNERS_DEPARTMENT_CODE_LIKE_KEY_NAME = "PARENTDEPARTMENTCODELIKE";

    /**
     * Имя параметра, содержащего флаг, указывающий на необходимость прикрепления договора в ЛК по данным страхователя по договору
     */
    protected static final String IS_ATTACH_TO_INSURER_PARAMNAME = "ISATTACHTOINSURER";

    /**
     * Имя параметра, содержащего флаг, указывающий на необходимость уведомления по СМС о прикреплении договора в ЛК в ходе создания договора
     */
    protected static final String IS_ATTACH_NOTIFY_BY_SMS_PARAMNAME = "ISATTACHNOTIFYBYSMS";

    // Значения дискриминаторов (B2B_PRODSTRUCT.DISCRIMINATOR) для типов элементов структуры страхового продукта
    protected static final long DISCRIMINATOR_SECTION = 6L;
    protected static final long DISCRIMINATOR_GROUP = 2L;
    protected static final long DISCRIMINATOR_OBJECT = 3L;
    protected static final long DISCRIMINATOR_RISK = 4L;
    protected static final long DISCRIMINATOR_OR = 1L;

    // для result.put("Error", "описание ошибки для вывода в интерфейсе")
    protected static final String ERROR = "Error";
    // для result.put("Reason", "описание ошибки для использования в веб-сервисах")
    protected static final String REASON = "Reason";
    // протоколирование времени, затраченного на вызовы (влияет только на вызовы через callServiceTimeLogged)
    protected boolean IS_CALLS_TIME_LOGGED = false;

    /**
     * Минимальная значимый коэффициент (проверяемый коэффициент считается не указанным, если он менее данного значения)
     */
    protected final double MIN_SIGNIFICANT_RATE = 0.0001D;

    /**
     * Минимальная значимая сумма (проверяемая сумма считается не указанной, если она менее данной)
     */
    protected final double MIN_SIGNIFICANT_SUM = 0.01D;

    /**
     * Минимальная значимый курс (проверяемый курс считается не указанным, если он менее данного значения)
     */
    protected final double MIN_SIGNIFICANT_CURRENCY = 0.0001D;

    /** Максимальная значимая дата (проверяемая дата считается недостигаемой в будущем, если она больше заданного значения) */
    protected static final GregorianCalendar MAX_SIGNIFICANT_DATE_GC = new GregorianCalendar(6666, 0, 1);

    /** Максимальная значимая дата (проверяемая дата считается недостигаемой в будущем, если она больше заданного значения) */
    protected static final Date MAX_SIGNIFICANT_DATE = MAX_SIGNIFICANT_DATE_GC.getTime();

    // сис. наименовние типа сущности для расторжений
    protected static final String TYPE_SYSNAME_B2B_TERMINATION = "B2B_TERMINATION";
    /**
     * Имя параметра вызова методов, хранящего ИД (EID) записи из сущности 'Классификатор ролей наблюдателя' (KindShareRole)
     */
    protected static final String SHARE_ROLE_ID_PARAMNAME = "ShareRoleID";
    /**
     * Имя параметра вызова методов, хранящего системное наименование (Sysname) записи из сущности 'Классификатор ролей наблюдателя' (KindShareRole)
     */
    protected static final String SHARE_ROLE_SYSNAME_PARAMNAME = "ShareRoleSysName";
    /**
     * Имя свойства сущности 'Наблюдатель договора страхования' (ShareContractIns), хранящего ссылку на 'Классификатор ролей наблюдателя' (KindShareRole)
     */
    protected static final String SHARE_ROLE_ID_FIELDNAME = "ShareRoleID";
    /**
     * Системное наименование (Sysname) роли 'Страхователь' для установки ссылки из сущности 'Наблюдатель договора страхования' (ShareContractIns) на 'Классификатор ролей наблюдателя' (KindShareRole)
     */
    protected static final String SHARE_ROLE_SYSNAME_INSURER = "insurer";
    /**
     * Системное наименование (Sysname) роли по умолчанию для установки ссылки из сущности 'Наблюдатель договора страхования' (ShareContractIns) на 'Классификатор ролей наблюдателя' (KindShareRole)
     */
    protected static final String SHARE_ROLE_SYSNAME_DEFAULT = SHARE_ROLE_SYSNAME_INSURER;

    /**
     * Значение флага типа Long для случаев 'Нет'/'Ложь'/'Отключено': BOOLEAN_FLAG_LONG_VALUE_FALSE = 0L
     */
    protected static final Long BOOLEAN_FLAG_LONG_VALUE_FALSE = 0L;
    /**
     * Значение флага типа Long для случаев 'Да'/'Истина'/'Включено': BOOLEAN_FLAG_LONG_VALUE_TRUE = 1L
     */
    protected static final Long BOOLEAN_FLAG_LONG_VALUE_TRUE = 1L;

    private final ObjectWriter objectWriterWithDefaultPrettyPrinter = (new ObjectMapper()).writerWithDefaultPrettyPrinter();

    /**
     * Имя ключа, указывающего на параметр, отвечающий за полную мапу договора
     */
    protected static final String CONTRACT_MAP_PARAMNAME = "CONTRMAP";

    /**
     * Имя ключа, указывающего на параметр, отвечающий за системное наименование продукта
     */
    protected static final String PRODUCT_SYSNAME_PARAMNAME = "PRODSYSNAME";

    /**
     * Имя ключа, указывающего на мапу с константами продукта (ключ - имя константы, значение - мапа с данными константы)
     */
    protected static final String PRODUCT_DEFAULT_VALUE_MAP_PARAMNAME = "PRODDEFVALMAP";

    /**
     * Имя ключа, указывающего на мапу с константами продукта (ключ - имя константы, значение - значение данной константы)
     */
    protected static final String PRODUCT_DEFAULT_VALUE_SHORT_MAP_PARAMNAME = "PRODDEFVALSHORTMAP";

    /**
     * Имя ключа, указывающего на список секций (например, в мапе договора)
     */
    protected static final String SECTION_LIST_PARAMNAME = "CONTRSECTIONLIST";

    /**
     * Имя ключа, указывающего на поле со значением премии
     */
    protected static final String PREMVALUE_FIELDNAME = "PREMVALUE";

    /**
     * Имя параметра, отвечающего за сис. наименование состояния
     */
    protected static final String STATE_SYSNAME_PARAMNAME = "STATESYSNAME";
    /**
     * Имя параметра, отвечающего за сис. наименование состояния договора
     */
    protected static final String CONTRACT_STATE_SYSNAME_PARAMNAME = STATE_SYSNAME_PARAMNAME;

    /**
     * Имя параметра (или константы из B2B_PRODDEFVAL) для указания текста СМС-уведомления о прикреплении созданного договора в ЛК
     */
    protected static final String ATTACH_NOTIFY_SMS_TEXT_PARAMNAME = "LKNOTIFYSMSTEXT";

    protected static final String RET_STATUS = WsConstants.RET_STATUS;
    protected static final String RET_STATUS_ERROR = WsConstants.RET_STATUS_ERROR;
    protected static final String RET_STATUS_OK = WsConstants.RET_STATUS_OK;

    /**
     * Имя параметра, указывающего на мапу с промежуточными результатами (только для отладки)
     */
    protected static final String DEBUG_SUB_RESULT_PARAMNAME = "[DEBUG] subResult";

    /**
     * Ключ параметра 'Номер договора', расшифрованного из ИД сессии при альт. аутентификации в ЛК (копия из com.bivgroup.rest.pa2ws.common.SessionManager)
     */
    protected static final String ALT_AUTH_CONTRACT_NUMBER_PARAMNAME = "ALT_AUTH_CONTRNUMBER";
    /**
     * Ключ параметра 'Номер телефона', расшифрованного из ИД сессии при альт. аутентификации в ЛК (копия из com.bivgroup.rest.pa2ws.common.SessionManager)
     */
    protected static final String ALT_AUTH_PHONE_NUMBER_PARAMNAME = "ALT_AUTH_PHONENUMBER";
    /**
     * Ключ параметра 'Адрес электронной почты', расшифрованного из ИД сессии при альт. аутентификации в ЛК (копия из com.bivgroup.rest.pa2ws.common.SessionManager)
     */
    protected static final String ALT_AUTH_EMAIL_PARAMNAME = "ALT_AUTH_EMAIL";

    // ИД валюты
    // todo: возможно, заменить на получение сервисом по сис. наименованию (dsB2BProductRefCurrencyBrowseListByParam + etc)
    /**
     * ИД валюты - Рубли (1)
     */
    protected static final Long CURRENCY_ID_RUB = 1L;
    /**
     * ИД валюты - Доллары (2)
     */
    protected static final Long CURRENCY_ID_USD = 2L;
    /**
     * ИД валюты - Евро (3)
     */
    protected static final Long CURRENCY_ID_EUR = 3L;

    // Состояния для запросов при интеграции допсов и пр.
    // (копия из com.bivgroup.integrationservice)
    protected static final Long REQUEST_QUEUE_STATUS_SUCCESS = 1000L;
    protected static final Long REQUEST_QUEUE_STATUS_ERROR = 404L;

    /** Прикреплять ли напечатанные черновики к договору */
    public static final String IS_NEED_DRAFT_ATTACH_PARAM_NAME = "ISNEEDDRAFTATTACH";
    /** Режим перепечати (предыдущий файл будет откреплен) */
    public static final String NEED_REPRINT_PARAMNAME = "NEEDREPRINT";
    /** Режим создания промежуточной записи в B2B_(CONTR/*)DOC */
    public static final String IS_NEED_DOC_ENTRY_PARAMNAME = "ISNEEDDOCENTRY";
    /** Использовать PRODREPID печатного документа в качестве FILETYPEID при его прикреплении к договору */
    public static final String IS_USE_PRODREPID_AS_FILETYPEID = "ISUSEPRODREPIDASFILETYPEID";

    /** Не обращаться в платежную систему (когда следует выполнить только проверки и создание плана и факта платежа) */
    public static final String IS_SKIP_MERCHANT_CALL = "ISSKIPMERCHANTCALL";

    /** Не проверять ИД аккаунта учетной записи ЛК при регистрации платежа (когда следует выполнить создание плана и факта платежа) */
    public static final String IS_SKIP_CLIENT_PROFILE_CHECK = "ISSKIPCLIENTPROFILECHECK";

    /** Состояние договора "Черновик" - B2B_CONTRACT_DRAFT */
    public static final String B2B_CONTRACT_DRAFT = "B2B_CONTRACT_DRAFT";
    /** Состояние договора "Предварительная печать (Образец)" - B2B_CONTRACT_PREPRINTING */
    public static final String B2B_CONTRACT_PREPRINTING = "B2B_CONTRACT_PREPRINTING";
    /** Состояние договора "На подписании" - B2B_CONTRACT_PREPARE */
    public static final String B2B_CONTRACT_PREPARE = "B2B_CONTRACT_PREPARE";
    /** Состояние договора "Подписан" - B2B_CONTRACT_SG */
    public static final String B2B_CONTRACT_SG = "B2B_CONTRACT_SG";

    /** Системное наименование состояния для записей о фактической оплате - имя параметра */
    public static final String PAYFACT_STATE_SYSNAME_PARAMNAME = "STATESYSNAME";
    /** Системное наименование состояния для записей о фактической оплате - draft */
    public static final String PAYFACT_STATE_SYSNAME_DRAFT = "draft";
    /** Системное наименование состояния для записей о фактической оплате - success */
    public static final String PAYFACT_STATE_SYSNAME_SUCCESS = "success";
    /** Системное наименование состояния для записей о фактической оплате - fail */
    public static final String PAYFACT_STATE_SYSNAME_FAIL = "fail";

    /** Тип оплаты - имя параметра */
    public static final String PAYFACT_TYPE_PARAMNAME = "PAYFACTTYPE";
    /** Тип оплаты - наличными / через кассу / по платежке / по ПД-4 и т.п. */
    public static final Long PAYFACT_TYPE_INVOICE = 4L;
    /** Тип оплаты - через внешнюю платежную систему (т. н. merchant / acquiring - https://3dsec.sberbank.ru/payment/merchants/..) */
    public static final Long PAYFACT_TYPE_ACQUIRING = 1L;

    /** Имя константы продукта (B2B_PRODDEFVAL.NAME), которая отвечает за префикс серии в номере договора */
    public static final String CONTRACT_SERIES_PREFIX_PRODDEFVAL_NAME = "CONTRSERIESPREFIX";
    /** Имя константы продукта (B2B_PRODDEFVAL.NAME), которая отвечает за префикс серии в номере договора при создании через OpenAPI */
    public static final String UNIOPENAPI_CONTRACT_SERIES_PREFIX_PRODDEFVAL_NAME = "UNIOPENAPI_CONTRSERIESPREFIX";

    protected static final String SUBSYSTEM_PARAMNAME_PREFIX = "SUBSYSTEM";
    protected static final String SUB_SYSTEM_ID_PARAMNAME = "SUBSYSTEMID";
    protected static final String HBDATAVERID_PARAMNAME = "HBDATAVERID";
    protected static final String SUBSYSTEM_HBDATAVERID_PARAMNAME = SUBSYSTEM_PARAMNAME_PREFIX + HBDATAVERID_PARAMNAME;
    protected static final String CONTRID_PARAMNAME = "CONTRID";
    protected static final String PRODCONFID_PARAMNAME = "PRODCONFID";
    protected static final String SUBSYSTEM_PRODUCT_PARAMS_PARAMNAME = SUBSYSTEM_PARAMNAME_PREFIX + "PRODUCTPARAMMAP";
    protected static final String URL_PAY_SUCCESS_PARAMNAME = "URLPAYSUCCESS";
    protected static final String URL_PAY_FAIL_PARAMNAME = "URLPAYFAIL";
    /**
     * Содержит набор имен ряда старых сервисов, которые возвращают результаты как попало.
     * Для них проверку на isCallResult(Not)OK выполнять нельзя (всегда будет сообщать о плохом результате).
     */
    public static final Set<String> INCONSISTENT_RESULT_SERVICE_SET;

    static {
        INCONSISTENT_RESULT_SERVICE_SET = new HashSet<>();
        INCONSISTENT_RESULT_SERVICE_SET.add(COREWS_SERVICE_NAME);
        INCONSISTENT_RESULT_SERVICE_SET.add(ADMINWS_SERVICE_NAME);
        INCONSISTENT_RESULT_SERVICE_SET.add(REFWS_SERVICE_NAME);
        INCONSISTENT_RESULT_SERVICE_SET.add(WEBSMSWS_SERVICE_NAME);
    }

    public B2BBaseFacade() {
        super();
        init();
    }

    private void init() {
        // создание формата дат для русских месяцев в нужном виде (например, "января" вместо "Январь")
        Locale russianLocale = new Locale("ru");
        DateFormatSymbols russianDateFormatSymbols = DateFormatSymbols.getInstance(russianLocale);
        russianDateFormatSymbols.setMonths(MONTHS_FOR_STRING_DATE);
        dateFormatterMonth = new SimpleDateFormat("«dd» MMMMM yyyy", russianLocale);
        dateFormatterMonth.setDateFormatSymbols(russianDateFormatSymbols);
        // протоколирование времени, затраченного на вызовы (влияет только на вызовы через callServiceTimeLogged)
        IS_CALLS_TIME_LOGGED = true;
        String debuggingFlag = Optional.ofNullable(Config.getConfig()).ofNullable(Config.getConfig().getParam("DEBUGMODE", "false")).orElse("false");
        isDebug = Boolean.parseBoolean(debuggingFlag);

    }

    protected static double roundSum(Double sum) {
        return ((new BigDecimal(sum.toString())).setScale(2, RoundingMode.HALF_UP)).doubleValue();
    }

    protected static double roundCurrencyRate(Double sum) {
        return ((new BigDecimal(sum.toString())).setScale(4, RoundingMode.HALF_UP)).doubleValue();
    }

    protected String getCurrencyChar(Map<String, Object> contract) {
        if (contract.get("INSAMCURRENCYID") != null) {
            if ("1".equals(contract.get("INSAMCURRENCYID").toString())) {
                return "Р";
            } else {
                return "Д";
            }
        }
        return "Р";
    }

    protected String getPayPeriodChar(Map<String, Object> contract, String login, String password) throws Exception {
        if (contract.get("PAYVARID") != null) {
            Long payVarId = getLongParam(contract.get("PAYVARID"));
            Long prodVerId = getLongParam(contract.get("PRODVERID"));
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("PRODVERID", prodVerId);
            params.put("PAYVARID", payVarId);
            Map<String, Object> res = this.callService(Constants.B2BPOSWS, "dsB2BProductPaymentVariantBrowseListByParamEx", params, login, password);
            if (res.get(RESULT) != null) {
                List<Map<String, Object>> payvarList = (List<Map<String, Object>>) res.get(RESULT);
                if (!payvarList.isEmpty()) {
                    Map<String, Object> payvarMap = payvarList.get(0);
                    String payVarName = getStringParam(payvarMap.get("NAME"));
                    if ("ONETIME".equalsIgnoreCase(payVarName)) {
                        return "0";
                    } else {
                        return "1";
                    }
                }
            }
        }
        return "0";
    }


    // Кастомная генерация серии
    protected String generateContrSer(Map<String, Object> contract, String serPrefix, String login, String password) throws Exception {
        String result = serPrefix;
        return result;
    }

    protected Map<String, Object> getProductDefaultValueByProdConfId(Object prodConfId, String login, String password) throws Exception {
        Map<String, Object> productConfQueryParams = new HashMap<String, Object>();
        productConfQueryParams.put(RETURN_AS_HASH_MAP, true);
        productConfQueryParams.put("PRODCONFID", prodConfId);
        Map<String, Object> result = this.callService(Constants.B2BPOSWS, "dsB2BProductDefaultValueByProdConfId", productConfQueryParams, login, password);
        return result;
    }

    /**
     * Метод получения конфигурации продукта по его PRODVERID
     * @param prodVerId
     * @param login
     * @param password
     * @return
     * @throws Exception
     */
    protected Map<String, Object> getProductConfigByProdVerId(Long prodVerId, String login, String password) throws Exception {
        Map<String, Object> productConfQueryParams = new HashMap<String, Object>();
        productConfQueryParams.put(RETURN_AS_HASH_MAP, true);
        productConfQueryParams.put("PRODVERID", prodVerId);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", productConfQueryParams, login, password);
        return result;
    }

    private String getUploadFilePath() {
        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();
        return result;
    }

    protected void saveEmailInFile(Map<String, Object> sendParams, String login, String password) {
        // получаем значение флага сохранения из конфига bivsberposws
        Config config = Config.getConfig(Constants.B2BPOSWS);
        String saveEmailInFile = config.getParam("SAVEEMAILINFILE", "FALSE");
        if ("true".equalsIgnoreCase(saveEmailInFile)) {
            // если флаг взведен, делаем файл по пути uploadpath
            // получаем путь к uploadpath
            String upPath = getUploadFilePath();
            // сохраняем в данный файл текст письма
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");
            String email = getStringParam(sendParams.get("SMTPReceipt"));
            String htmlText = getStringParam(sendParams.get("HTMLTEXT"));
            if (htmlText.isEmpty()) {
                htmlText = getStringParam(sendParams.get("SMTPMESSAGE"));
            }
            email.replace("@", "_");

            BufferedOutputStream bufferedOutput = null;
            FileOutputStream fileOutputStream = null;
            try {
                String fileName = sdf.format(now) + "_" + email + ".html";
                fileOutputStream = new FileOutputStream(upPath + fileName);//".txt"
                bufferedOutput = new BufferedOutputStream(fileOutputStream);
                //Start writing to the output stream
                byte[] ba = null;
                /*
                String codePage = "";
                if (codePage.equals("")) {
                    ba = htmlText.getBytes();
                } else {
                    ba = htmlText.getBytes(codePage);
                }
                */
                ba = htmlText.getBytes();
                bufferedOutput.write(ba);
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            } finally {
                //Close the BufferedOutputStream
                try {
                    if (bufferedOutput != null) {
                        bufferedOutput.flush();
                        bufferedOutput.close();
                    }
                } catch (IOException ex) {
                }
            }
        }
    }

    protected void sendEmailCurrencyFail(String email, Date date, boolean usdExist, boolean euroExist, String login, String password) {
        Map<String, Object> sendParams = new HashMap<String, Object>();
        sendParams.put("SMTPSubject", "Ошибка сервиса обновления курсов валют");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateStr = sdf.format(date);
        String currNames = "";
        if (!usdExist) {
            currNames = "USD ";
        }
        if (!euroExist) {
            currNames = currNames + "EUR ";
        }
        sendParams.put("SMTPMESSAGE", "На дату " + dateStr + " отсутствует курс валют: " + currNames + ". Проверьте работу сервиса получения курсов валют от центробанка.");
        sendParams.put("SMTPReceipt", email);
        if (isAllEmailValid(email)) {

            logger.debug("sendParams = " + sendParams.toString());
            Map<String, Object> sendRes = null;

            try {
                saveEmailInFile(sendParams, login, password);

                sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                    sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                    if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                        sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                        if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                            sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                            if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                                sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                            }
                        }
                    }
                }
                logger.debug("mailSendSuccess");
            } catch (Exception e) {
                saveEmailInFile(sendParams, login, password);
                logger.debug("mailSendException: ", e);
            }
            //return sendRes;
        }
    }

    protected Double getExchangeRateByCurrencyID(Long currencyID, Date date, String login, String password) throws Exception {
        Double result = 1.0;

        Map<String, Object> curParams = new HashMap<String, Object>();
        curParams.put("CurrencyID", currencyID);
        Map<String, Object> curRes = this.callService(REFWS_SERVICE_NAME, "getCurrencyByParams", curParams, login, password);
        List<Map<String, Object>> curList = WsUtils.getListFromResultMap(curRes);
        if (curList != null) {
            if (!curList.isEmpty()) {
                String curCode = getStringParam(curList.get(0).get("Brief"));
                Map<String, Object> exParams = new HashMap<String, Object>();
                exParams.put("natCurCode", curCode);
                exParams.put("QuotedCurrencyID", 1);
                GregorianCalendar gcd = new GregorianCalendar();
                if (date != null) {
                    gcd.setTime(date);
                    if (gcd.get(Calendar.HOUR_OF_DAY) < 4) {
                        gcd.add(Calendar.HOUR_OF_DAY, 4);
                        date = gcd.getTime();
                    }
                } else {
                    date = new Date();
                    gcd.setTime(date);
                }
                exParams.put("Date", date);
                Map<String, Object> exRes = this.callService(REFWS_SERVICE_NAME, "getCurrencyPairForCrossCourseByParams", exParams, login, password);
                List<Map<String, Object>> exList = WsUtils.getListFromResultMap(exRes);
                if (exList != null) {
                    if (!exList.isEmpty()) {
                        if (exList.get(0).get("COURSEVALUE") != null) {
                            Date cd = getDateParam(exList.get(0).get("COURSEDATE"));
                            GregorianCalendar gc = new GregorianCalendar();
                            gc.setTime(cd);
                            gc.set(Calendar.HOUR_OF_DAY, 0);
                            gc.set(Calendar.MINUTE, 0);
                            gc.set(Calendar.SECOND, 0);
                            gc.set(Calendar.MILLISECOND, 0);
                            GregorianCalendar cgc = new GregorianCalendar();
                            cgc.setTime(date);
                            cgc.set(Calendar.HOUR_OF_DAY, 0);
                            cgc.set(Calendar.MINUTE, 0);
                            cgc.set(Calendar.SECOND, 0);
                            cgc.set(Calendar.MILLISECOND, 0);
                            result = getDoubleParam(exList.get(0).get("COURSEVALUE"));
                            if (gc.getTime().compareTo(cgc.getTime()) != 0) {

                                //даты не равны отправляем сообщение об ошибке
                                Map<String, Object> sysParam = new HashMap<String, Object>();
                                sysParam.put("SETTINGSYSNAME", "NOTIFICATIONEMAILS");
                                sysParam.put("ReturnAsHashMap", "TRUE");
                                Map<String, Object> sysRes = this.callService(COREWS_SERVICE_NAME, "getSysSettingBySysName", sysParam, login, password);
                                if (sysRes.get("SETTINGVALUE") != null) {
                                    String emails = sysRes.get("SETTINGVALUE").toString();
                                    String[] emailList = emails.split(",");
                                    boolean usdExist = true;
                                    boolean euroExist = true;
                                    if ("EUR".equals(curCode)) {
                                        euroExist = false;
                                    }
                                    if ("USD".equals(curCode)) {
                                        usdExist = false;
                                    }
                                    for (String email : emailList) {
                                        //4. отправляем на указанные адреса уведомления о отсутсвии курса валюты на заданную дату.
                                        sendEmailCurrencyFail(email, date, usdExist, euroExist, login, password);
                                    }
                                }
                            }

                        }

                    }
                }
            }
        }
        return result;
    }

    protected void geterateContractSerNum(Map<String, Object> contract, String login, String password) throws Exception {
        if (contract.get("PRODCONFID") != null) {
            Long prodConfId = getLongParam(contract.get("PRODCONFID"));
            Map<String, Object> param = new HashMap<String, Object>();
            if (prodConfId != null) {
                Map<String, Object> prodDefValMap = getProductDefaultValueByProdConfId(prodConfId, login, password);
                String autoNumSysName = getStringParam(prodDefValMap.get("CONTRAUTONUMBERSYSNAME"));
                String seriesPrefix = getStringParam(prodDefValMap.get("CONTRSERIESPREFIX"));
                // получение альтернативной серии договора (если предусмотрена для данного способа создания договора)
                seriesPrefix = getAltSeriesPrefixIfNeeded(contract, contract, prodDefValMap, seriesPrefix, login, password);
                String contrNum = "";
                if (contract.get("CONTRPOLNUM") == null) {
                    contrNum = generateContrNum(autoNumSysName, login, password);
                } else {
                    contrNum = contract.get("CONTRPOLNUM").toString();
                }
                String contrSer = generateContrSer(contract, seriesPrefix, login, password);
                contract.put("CONTRPOLSER", contrSer);
                contract.put("CONTRPOLNUM", contrNum);
                contract.put("CONTRNUMBER", contrSer + contrNum);
            }
        }
    }

    protected String generateContrSer(Map<String, Object> contract, Long prodConfId, String login, String password) throws Exception {
        String result = "";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ReturnAsHashMap", "TRUE");
        param.put("NAME", "CONTRSERIESPREFIX");
        if (prodConfId != null) {
            param.put("PRODCONFID", prodConfId);
            logger.debug("getting autoNumSysName by prodconfid:" + prodConfId.toString());
            Map<String, Object> res = this.callService(Constants.B2BPOSWS, "dsB2BProductDefaultValueBrowseListByParam", param, login, password);
            if (res.get("VALUE") != null) {
                String seriesPrefix = res.get("VALUE").toString();
                result = generateContrSer(contract, seriesPrefix, login, password);
            }
        } else {
            logger.debug("getting autoNumSysName fail prodconfid is null");
        }
        logger.debug("autoNumSysNameRes = " + result);
        return result;
    }

    protected String genetateDraftNum(String login, String password) throws Exception {
        String generatorName = getCoreSettingBySysName("DRAFT_CONTRNUM_GENERATION_METHOD", login, password);
        String result = "";
        if (generatorName != null) {
            Map<String, Object> param1 = new HashMap<String, Object>();
            param1.put("SYSTEMBRIEF", generatorName);
            Map<String, Object> res1 = this.callService(COREWS, "dsNumberFindByMask", param1, login, password);
            if (res1.get("Result") != null) {
                result = res1.get("Result").toString();
            }
        }
        return result;
    }

    /**
     * Генерация серии и номера для договора в статусе "Черновик"
     *
     * @param contract
     * @param login
     * @param password
     * @throws Exception 
     */
    protected void generateContractSerNumDraft(Map<String, Object> contract, String login, String password) throws Exception {
        if (contract.get("CONTRNUMBER") == null) {
            if (contract.get("PRODCONFID") != null) {
                Long prodConfId = getLongParam(contract.get("PRODCONFID"));
                if (prodConfId != null) {
                    Map<String, Object> prodDefValMap = getProductDefaultValueByProdConfId(prodConfId, login, password);
                    String seriesPrefix = getStringParam(prodDefValMap.get("CONTRSERIESPREFIX"));
                    // получение альтернативной серии договора (если предусмотрена для данного способа создания договора)
                    seriesPrefix = getAltSeriesPrefixIfNeeded(contract, contract, prodDefValMap, seriesPrefix, login, password);
                    String contrSer = generateContrSer(contract, seriesPrefix, login, password);
                    contract.put("CONTRPOLSER", contrSer);
                    String draftContrNumber = genetateDraftNum(login, password);
                    if (!draftContrNumber.isEmpty()) {
                        contract.put("CONTRNUMBER", draftContrNumber);
                    }
                }
            }
        }
    }
    
    protected String generateContrNum(String autoNumSysName, String login, String password) throws Exception {
        String result = "";
        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("SYSTEMBRIEF", autoNumSysName);
        Map<String, Object> res1 = this.callService(COREWS, "dsNumberFindByMask", param1, login, password);
        if (res1.get("Result") != null) {
            result = res1.get("Result").toString();
        }
        return result;
    }

    protected String generateContrNum(Long prodConfId, String login, String password) throws Exception {
        String result = "";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ReturnAsHashMap", "TRUE");
        param.put("NAME", "CONTRAUTONUMBERSYSNAME");
        if (prodConfId != null) {
            param.put("PRODCONFID", prodConfId);
            logger.debug("getting autoNumSysName by prodconfid:" + prodConfId.toString());
            Map<String, Object> res = this.callService(Constants.B2BPOSWS, "dsB2BProductDefaultValueBrowseListByParam", param, login, password);
            if (res.get("VALUE") != null) {
                String autoNumSysName = res.get("VALUE").toString();
                result = generateContrNum(autoNumSysName, login, password);
            }
        } else {
            logger.debug("getting autoNumSysName fail prodconfid is null");
        }
        logger.debug("autoNumSysNameRes = " + result);
        return result;
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
        logger.warn(String.format(
                "Expected param (%s) not found in result map after calling [%s] %s with params %s!",
                keyName, serviceName, methodName, params)
        );
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

    public List<Map<String, Object>> callServiceAndGetListFromResultMap(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        return WsUtils.getListFromResultMap(this.callService(serviceName, methodName, params, login, password));
    }

    /**
     * Вызывает обычную версию selectQuery, передавая в качестве имени второго
     * "количественного" запроса имя фактического запроса прибавив к нему
     * "Count".
     *
     * @param queryName    имя фактического запроса
     * @param selectParams параметры выолнения запроса
     * @return результат, аналогичный получаемому при вызове обычной версии
     * selectQuery
     * @throws Exception
     */
    public Map<String, Object> selectQuery(String queryName, Map<String, Object> selectParams) throws Exception {
        return this.selectQuery(queryName, queryName + "Count", selectParams);
    }

    /**
     * Вызывает обычную версию selectQuery, подготовив полученный список
     * стантартым для массовой вставки способом (rows - сам список, totalCount -
     * количество строк)
     *
     * @param queryName    имя запроса
     * @param insertedMass список строк для вставки
     * @return результат, аналогичный получаемому при вызове обычной версии
     * insertQuery
     * @throws Exception
     */
    public int[] insertMassQuery(String queryName, List<Map<String, Object>> insertedMass) throws Exception {
        Map<String, Object> insertMassParams = new HashMap<String, Object>();
        insertMassParams.put("rows", insertedMass);
        insertMassParams.put("totalCount", insertedMass.size());
        return this.insertQuery(queryName, insertMassParams);
    }

    /**
     * Выполняет обычную версию selectQuery, но возвращает только одно значение
     * (по указанному ключу) из первой строки результата запроса. При вызове
     * обычной версии selectQuery, передает в качестве имени второго
     * "количественного" запроса имя фактического запроса прибавив к нему
     * "Count".
     *
     * @param queryName имя запроса
     * @param params    параметры запроса
     * @param keyName   имя ключа, указывающего на элемент первой строки
     *                  результата запроса, значение которого необходимо вернуть
     * @return значение, на которое указывает переданный ключ (из первой строки
     * результата запроса)
     * @throws Exception
     */
    public Object selectQueryAndGetOneValueFromFistItem(String queryName, Map<String, Object> params, String keyName) throws Exception {
        //params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> rawResult = this.selectQuery(queryName, queryName + "Count", params);
        if (rawResult != null) {
            Map<String, Object> firstItem = WsUtils.getFirstItemFromResultMap(rawResult);
            if (firstItem != null) {
                Object oneValue = firstItem.get(keyName);
                if (oneValue != null) {
                    return oneValue;
                }
            }
        }
        // todo: протоколирование неудачи при получении значения по переданному ключу
        return null;
    }

    public List<Map<String, Object>> selectQueryAndGetListFromResultMap(String queryName, Map<String, Object> params) throws Exception {
        return WsUtils.getListFromResultMap(this.selectQuery(queryName, params));
    }

    // создается запись в логгере аудита, если пользователь входит в группу
    protected void logToAuditForGroups(Map<String,Object> params, String message, String[] groupNames){
        String login = getStringParam(params, LOGIN);
        Long userAccountId = getLongParam(params, "SESSION_USERACCOUNTID");
        String userGroupsListStr = getStringParam(params, B2B_USERGROUPS_PARAMNAME);
        String prefix = String.format("Пользователь ['%s', %s]: ", login, userAccountId);
        HashSet groupSet = new HashSet<>(Arrays.asList(userGroupsListStr.split(",")));
        Object o = Arrays.stream(groupNames).filter(groupSet::contains).findFirst().orElse(null);
        if (o == null) return;
        auditLogger.info(prefix + message);
    }

    protected void logToAuditForGroups(Map<String,Object> params, StringBuilder message, String[] groupNames){
        logToAuditForGroups(params, message.toString(), groupNames);
    }

    public Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params, boolean isVerboseLog, String login, String password) throws Exception {
        if (isVerboseLog) {
            return callServiceLogged(serviceName, methodName, params, login, password);
        } else {
            return callService(serviceName, methodName, params, login, password);
        }
    }

    @Override
    public Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> result;
        try {
            result = super.callService(serviceName, methodName, params, login, password);
        } catch (Exception ex) {
            // протоколирование исключения
            String error = String.format(
                    "Calling method '%s' from '%s' (with params = %s) caused exception: %s! Details (exception): ",
                    methodName, serviceName, params, ex.getLocalizedMessage()
            );
            logger.error(error, ex);
            // проброс исключения выше
            throw ex;
        }
        // ряд старых сервисов возвращают результаты как попало,
        // для них проверку на isCallResultNotOK выполнять нельзя (всегда будет сообщать о returned bad result)
        if ((!INCONSISTENT_RESULT_SERVICE_SET.contains(serviceName)) && isCallResultNotOK(result)) {
            String error = String.format(
                    "Calling method '%s' from '%s' returned bad result! Details (call result):\n%s\nDetails (call params)",
                    methodName, serviceName, result
            );
            loggerErrorPretty(logger, error, params);
        }
        return result;
    }

    public Map<String, Object> callServiceLogged(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "Вызван метод %s с параметрами:\n\n%s\n", methodName, params
            ));
        }
        // вызов действительного метода
        Map<String, Object> callResult = this.callService(serviceName, methodName, params, login, password);
        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "Метод %s выполнился за %d мс. и вернул результат:\n\n%s\n",
                    methodName, callTimer, callResult
            ));
        }
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

    protected static boolean isCallResultNotOK(Map<String, Object> callResult) {
        return !isCallResultOK(callResult);
    }

    protected static boolean isCallResultOK(Map<String, Object> callResult) {
        //return (callResult != null) && (callResult.get("Status") != null) && ("OK".equalsIgnoreCase(callResult.get("Status").toString()));
        return (callResult != null) && (callResult.get(RET_STATUS) != null) && (RET_STATUS_OK.equalsIgnoreCase(callResult.get(RET_STATUS).toString()));
    }

    protected static boolean isCallResultOKAndContains(Map<String, Object> callResult, String keyName) {
        return (isCallResultOK(callResult)) && (callResult.get(keyName) != null);
    }

    protected boolean isCallResultOKAndContainsLongValue(Map<String, Object> callResult, String keyName, Long keyValue) {
        boolean result = false;
        if (isCallResultOK(callResult)) {
            Long longValue = getLongParam(callResult, keyName);
            if ((keyValue != null) && (keyValue.equals(longValue))) {
                result = true;
            }
        }
        return result;
    }

    public Integer getIntegerParam(Map<String, Object> map, String keyName) {
        Integer integerValue = null;
        if (map != null) {
            integerValue = getIntegerParam(map.get(keyName));
        }

        return integerValue;
    }

    protected Integer getIntegerParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Integer.valueOf(bean.toString());
        } else {
            return 0;
        }
    }

    protected boolean getBooleanParam(Object bean, Boolean defVal) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Boolean.valueOf(bean.toString()).booleanValue();
        } else {
            return defVal;
        }
    }

    protected boolean getBooleanParam(Map<String, Object> map, String keyName, Boolean defVal) {
        boolean booleanParam = defVal;
        if (map != null) {
            booleanParam = getBooleanParam(map.get(keyName), defVal);
        }
        return booleanParam;
    }

    protected boolean getBooleanParamLogged(Map<String, Object> map, String keyName, Boolean defVal) {
        boolean paramValue = getBooleanParam(map, keyName, defVal);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    protected int getSimpleIntParam(Object bean) {
        Integer res = getIntegerParam(bean);
        return res == null ? -1 : res.intValue();
    }

    protected BigInteger getBigIntegerParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigInteger.valueOf(Long.valueOf(bean.toString()).longValue());
        } else {
            return null;
        }
    }

    protected BigDecimal getBigDecimalParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigDecimal.valueOf(Double.valueOf(bean.toString()));
        } else {
            return null;
        }
    }

    protected static Long getLongParamWithDefaultValue(Map<String, Object> map, String keyName, Long defaultValue) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParamWithDefaultValue(map.get(keyName), defaultValue);
        }
        return longParam;
    }

    protected static Long getLongParamWithDefaultValue(Object bean, Long defaultValue) {
        Long result = getLongParam(bean);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    protected static Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected static Long getLongParam(Map<String, Object> map, String keyName) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParam(map.get(keyName));
        }
        return longParam;
    }

    // аналог getLongParam, но с протоколировнием полученного значения
    protected Long getLongParamLogged(Map<String, Object> map, String keyName) {
        Long paramValue = getLongParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    protected static Date getDateParam(Object date) {
        if (date != null) {
            if (date instanceof Double) {
                return XMLUtil.convertDate((Double) date);
            } else if (date instanceof BigDecimal) {
                return XMLUtil.convertDate((BigDecimal) date);
            } else if (date instanceof String) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    return sdf.parse(date.toString());
                } catch (ParseException ex) {
                    sdf = new SimpleDateFormat("yyyy.MM.dd");
                    try {
                        return sdf.parse(date.toString());
                    } catch (ParseException ex1) {
                        Logger.getLogger(B2BBaseFacade.class.getName()).error(null, ex);
                    }
                }
            }
            return (Date) date;
        } else {
            return null;
        }
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

    // аналог getStringParamLogged, но с установкой значения по умолчанию и протоколированием
    protected String getStringOptionalParamLogged(Map<String, Object> map, String keyName, String defaultValue) {
        String paramValue = getStringParam(map, keyName);
        if (paramValue.isEmpty()) {
            logger.debug(String.format("Optional parameter %s is empty, default value ('%s') will be used.", keyName, defaultValue));
            paramValue = defaultValue;
        } else {
            logger.debug(keyName + " = " + paramValue);
        }
        return paramValue;
    }

    protected static String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }

    // аналог getStringParam, но с протоколировнием полученного значения
    protected String getStringParamLogged(Map<String, Object> map, String keyName) {
        String paramValue = getStringParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    protected Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }

    protected Double getDoubleParam(Map<String, Object> map, String keyName) {
        Double doubleParam = 0.0;
        if (map != null) {
            doubleParam = getDoubleParam(map.get(keyName));
        }
        return doubleParam;
    }

    // аналог getDoubleParam, но с протоколировнием полученного значения
    protected Double getDoubleParamLogged(Map<String, Object> map, String keyName) {
        Double paramValue = getDoubleParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    private void logDateParseEx(String keyName, Object dateValue, String conversion, Exception ex) {
        logger.error("Parsing key's '" + keyName + "' value '" + dateValue + "' (using " + conversion + ") caused error:", ex);
    }

    protected Object parseAnyDate(Object dateValue, Class targetClass, String keyName, Boolean logged) {
        return parseAnyDate(dateValue, null, targetClass, keyName, logged);
    }

    /**
     * Аналогично {@link #parseAnyDate(Object, Object, Class, String)}, но с
     * флагом протоколирования
     *
     * @param dateValue   объект, содержащий дату в одном из перечисленных выше
     *                    типов
     * @param timeValue   объект, содержащий время (только для String)
     * @param targetClass целевой тип преобразования (любой из перечисленных
     *                    выше)
     * @param keyName     наименование ключа, указывающего на обрабатываемую дату
     *                    (только для протоколирования)
     * @param logged      флаг протоколирования
     * @return преобразованную в указанный тип дату
     */
    protected Object parseAnyDate(Object dateValue, Object timeValue, Class targetClass, String keyName, Boolean logged) {
        if ((logged) && (logger.isDebugEnabled())) {
            Object result = parseAnyDate(dateValue, timeValue, targetClass, keyName);
            StringBuffer logStr = new StringBuffer();
            logStr.append("Атрибут '").append(keyName);
            logStr.append("' содержал дату '").append(dateValue);
            logStr.append("' ('").append(dateValue.getClass().getSimpleName()).append("')");
            if (timeValue != null) {
                logStr.append(", а атрибут '").append(keyName).append(KEY_NAME_TIME_SUFFIX);
                logStr.append("' - время '").append(timeValue);
                logStr.append("' ('").append(timeValue.getClass().getSimpleName()).append("')");
            }
            logStr.append(", преобразование в '").append(targetClass.getSimpleName()).append("' завершено с результатом: '").append(result).append("'.");
            logger.debug(logStr);
            return result;
        } else {
            return parseAnyDate(dateValue, timeValue, targetClass, keyName);
        }
    }

    private Date strToDateForBetween(String dateStr, String keyName) {
        Date dateValue = null;
        try {
            // дата вида "dd.MM.yyyy HH:mm" или "dd.MM.yyyy HH:mm z"
            if (dateStr.length() == DATE_TIME_FORMATTER_PATTERN.length()) {
                // дату вида "dd.MM.yyyy HH:mm" необходимо привести к "dd.MM.yyyy HH:mm z" (считая, что часовая зона по умолчанию это DATE_TIME_FORMATTER_DEFAULT_TZ_STR)
                dateStr = dateStr + DATE_TIME_FORMATTER_DEFAULT_TZ_STR;
            }
            // дата вида "dd.MM.yyyy HH:mm z"
            dateValue = dateTimeFormatterTZ.parse(dateStr);
        } catch (ParseException ex1) {
            try {
                // дата вида "dd.MM.yyyy"
                dateValue = dateFormatter.parse(dateStr);
            } catch (ParseException ex2) {
                try {
                    // дата вида "ddMMyyyy"
                    dateValue = dateFormatterNoDots.parse(dateStr);
                } catch (ParseException ex3) {
                    logDateParseEx(keyName, dateStr, "format " + dateFormatter.toPattern(), ex1);
                    logDateParseEx(keyName, dateStr, "format " + dateFormatterNoDots.toPattern(), ex2);
                    logDateParseEx(keyName, dateStr, "format " + dateTimeFormatterTZ.toPattern(), ex3);
                }
            }
        }
        return dateValue;
    }

    private Date strToDateTime(Object date, Object time, String keyName) {
        Date dateValue = null;
        String timeStr;
        String dateTimeStr = date.toString();
        if (dateTimeStr.length() < DATE_TIME_FORMATTER_PATTERN.length()) {
            if (time == null) {
                timeStr = "00:00";
            } else {
                timeStr = time.toString();
            }
            dateTimeStr = date.toString() + " " + timeStr;
        }
        try {
            if (dateTimeStr.length() == DATE_TIME_FORMATTER_PATTERN.length()) {
                // дату вида "dd.MM.yyyy HH:mm" необходимо привести к "dd.MM.yyyy HH:mm z" (считая, что часовая зона по умолчанию это DATE_TIME_FORMATTER_DEFAULT_TZ_STR)
                dateTimeStr = dateTimeStr + " GMT+00:00";
            }
            if (dateTimeStr.length() > DATE_TIME_TZ_FORMATTER_PATTERN.length()) {
                dateValue = dateTimeFormatterTZ.parse(dateTimeStr);
            } else {
                dateValue = dateTimeFormatter.parse(dateTimeStr);
            }
        } catch (ParseException ex1) {
            logDateParseEx(keyName, date, "format " + dateTimeFormatter.toPattern(), ex1);
        }
        return dateValue;
    }

    /**
     * Преобразует дату из/в любой из перечисленных типов - String, Date,
     * Double. Для преобразований, использующих тип String, будет происходить
     * потеря точности (до минуты).
     *
     * @param dateValue   объект, содержащий дату в одном из перечисленных выше
     *                    типов
     * @param targetClass целевой тип преобразования (любой из перечисленных
     *                    выше)
     * @param keyName     наименование ключа, указывающего на обрабатываемую дату
     *                    (только для протоколирования)
     * @return преобразованную в указанный тип дату
     */
    protected Object parseAnyDate(Object dateValue, Class targetClass, String keyName) {
        return parseAnyDate(dateValue, null, targetClass, keyName);
    }

    /**
     * Преобразует дату из/в любой из перечисленных типов - String, Date,
     * Double. Для преобразований, использующих тип String, будет происходить
     * потеря точности (до минуты).
     *
     * @param dateValue   объект, содержащий дату в одном из перечисленных выше
     *                    типов
     * @param timeValue   объект, содержащий время (только для String)
     * @param targetClass целевой тип преобразования (любой из перечисленных
     *                    выше)
     * @param keyName     наименование ключа, указывающего на обрабатываемую дату
     *                    (только для протоколирования)
     * @return преобразованную в указанный тип дату
     */
    protected Object parseAnyDate(Object dateValue, Object timeValue, Class targetClass, String keyName) {

        // String -> Date
        if (dateValue instanceof String) {
            if (String.class.equals(targetClass)) {
                return dateValue.toString();
            }
            // попытка преобразовать строку в дату
            // если в строке есть "and"  - то дата - ограничение для between
            String curVal = dateValue.toString();
            curVal = curVal.toUpperCase();
            if (curVal.contains(" AND ")) {
                // получаем даты из ограничения between и собираем обратно
                String[] betweenStr = curVal.split(" AND ");
                Date dateValue1 = strToDateForBetween(betweenStr[0], keyName);
                Date dateValue2 = strToDateForBetween(betweenStr[1], keyName);
                if ((dateValue1 != null) && (dateValue2 != null)) {
                    if (Double.class.equals(targetClass)) {
                        dateValue = String.valueOf(DSDateUtil.convertDate(dateValue1).doubleValue()) + " AND "
                                + String.valueOf(DSDateUtil.convertDate(dateValue2).doubleValue());
                    } else if (Date.class.equals(targetClass)) {
                        dateValue = dateValue1.toString() + " AND " + dateValue2.toString();
                    }
                }
            } else {
                dateValue = strToDateTime(dateValue, timeValue, keyName);
            }
        }
        // Double -> Date
        if (dateValue instanceof BigDecimal) {
            dateValue = ((BigDecimal) dateValue).doubleValue();
        }

        if (dateValue instanceof Double) {
            if (Double.class.equals(targetClass)) {
                return (Double) dateValue;
            }
            // попытка преобразовать число из БД в дату
            try {
                Double dateDouble = (Double) dateValue;
                dateValue = DSDateUtil.convertDate(dateDouble);
            } catch (Exception ex) {
                logDateParseEx(keyName, dateValue, "Double to Date conversion", ex);
            }
        }
        // Date -> String || Double
        if (dateValue instanceof Date) {
            if (Date.class.equals(targetClass)) {
                return (Date) dateValue;
            }
            // Date -> String
            if (String.class.equals(targetClass)) {
                // попытка преобразовать дату в строку
                try {
                    Date dateDate = (Date) dateValue;
                    dateValue = dateTimeFormatter.format(dateDate);
                } catch (Exception ex) {
                    logDateParseEx(keyName, dateValue, "Date to String conversion", ex);
                }
                // немедленный возврат значения, поскольку была проверка на целевой тип
                return dateValue;
            } else // Date -> Double
                if (Double.class.equals(targetClass)) {
                    // попытка преобразовать дату в число для БД
                    try {
                        Date dateDate = (Date) dateValue;
                        dateValue = DSDateUtil.convertDate(dateDate).doubleValue();
                    } catch (Exception ex) {
                        logDateParseEx(keyName, dateValue, "Date to Double conversion", ex);
                    }
                    // немедленный возврат значения, поскольку была проверка на целевой тип
                    return dateValue;
                }
        }
        // возврат значения, вероятнее всего не преобразованного вовсе (например, из-за исключения)
        return dateValue;
    }

    /**
     * Рекурсивно вызывает {@link #parseAnyDate} на всех значениях, имя ключа
     * которых имеет вид "*DATE"
     *
     * @param data        обрабатываемая карта (может содержать списки и вложенные
     *                    карты)
     * @param targetClass целевой тип преобразования
     */
    protected void parseDates(Map<String, Object> data, Class targetClass) {
        parseDates(data, targetClass, "*", KEY_NAME_DATE_SUFFIX, KEY_NAME_TIME_SUFFIX, false, false);
    }

    protected void parseDates(Map<String, Object> data, Class targetClass, List<String> namesToChange) {
        parseDates(data, targetClass, "*", KEY_NAME_DATE_SUFFIX, KEY_NAME_TIME_SUFFIX, false, false, namesToChange);
    }
    
    protected void parseDates(List<Map<String, Object>> dataList, Class targetClass) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("#", dataList);
        boolean addParamWithoutSuffix = false;
        boolean analyzeNoSuffixParamAndAddSuffix = false;
        parseDates(data, targetClass, "*", KEY_NAME_DATE_SUFFIX, KEY_NAME_TIME_SUFFIX, addParamWithoutSuffix, analyzeNoSuffixParamAndAddSuffix);
    }

    protected void parseDates(Map<String, Object> data, Class targetClass, String dateSuffix, String timeSuffix,
                              boolean addParamWithoutSuffix, boolean analyzeNoSuffixParamAndAddSuffix) {
        parseDates(data, targetClass, "*", dateSuffix, timeSuffix, addParamWithoutSuffix, analyzeNoSuffixParamAndAddSuffix);
    }

    protected void parseDates(Map<String, Object> data, Class targetClass, String dataNodePath,
            String dateSuffix, String timeSuffix, boolean addParamWithoutSuffix, boolean analyzeNoSuffixParamAndAddSuffix) {
    	parseDates(data, targetClass, dataNodePath,
                dateSuffix, timeSuffix, addParamWithoutSuffix, analyzeNoSuffixParamAndAddSuffix, null);
    }
    
    /**
     * Рекурсивно вызывает {@link #parseAnyDate} на всех значениях, имя ключа
     * которых имеет вид "*DATE"
     *
     * @param data                             обрабатываемая карта (может содержать списки и вложенные
     *                                         карты)
     * @param targetClass                      целевой тип преобразования
     * @param dataNodePath                     имя обрабатываемого узла - ключ, номер элемента и
     *                                         т.п. (только для протоколирования)
     * @param dateSuffix                       суффикс для анализа даты
     * @param timeSuffix                       суффикс для анализа времени
     * @param addParamWithoutSuffix            добавлять в мапу с датой параметр без суффикса даты
     * @param analyzeNoSuffixParamAndAddSuffix анализировать дату как java.util.Date без суффикса, и добавлять в мапу с суффиксом
     */
    protected void parseDates(Map<String, Object> data, Class targetClass, String dataNodePath,
                              String dateSuffix, String timeSuffix, boolean addParamWithoutSuffix, boolean analyzeNoSuffixParamAndAddSuffix, List<String> namesToChange) {
        boolean isTargetClassString = String.class.equals(targetClass);
        Map<String, Object> additionalEntries = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String keyName = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String keyFullName = dataNodePath + "." + keyName;
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    parseDates(map, targetClass, keyFullName, dateSuffix, timeSuffix, addParamWithoutSuffix, analyzeNoSuffixParamAndAddSuffix);
                } else if (value instanceof List) {
                    List<Object> list = (List<Object>) value;
                    for (int i = 0; i < list.size(); i++) {
                        Object rawElement = list.get(i);
                        if (rawElement instanceof Map) {
                            Map<String, Object> element = (Map<String, Object>) rawElement;
                            parseDates(element, targetClass, keyFullName + "[" + i + "]", dateSuffix, timeSuffix, addParamWithoutSuffix, analyzeNoSuffixParamAndAddSuffix);
                        }
                    }
                } else if ((keyName.endsWith(dateSuffix) && (namesToChange == null)) || ((namesToChange != null) && namesToChange.contains(keyName))) {
                    String timeKeyName = keyName + timeSuffix;
                    Object timeValue = data.get(timeKeyName);
                    Object newValue = parseAnyDate(value, timeValue, targetClass, keyFullName); // без протоколирования

                    if ((isTargetClassString) && (newValue instanceof String)) {
                        String[] dateTime = getStringParam(newValue).split(" ");
                        if (dateTime.length == 2) {
                            newValue = dateTime[0];
                            additionalEntries.put(timeKeyName, dateTime[1]);
                        } else {
                            logger.debug("Не удалось выделить время из строкового представления даты '" + newValue + "', содержащегося в атрибуте '" + keyFullName + "'.");
                        }
                    }
                    entry.setValue(newValue);
                    if (addParamWithoutSuffix && keyName.endsWith(dateSuffix)) {
                        additionalEntries.put(entry.getKey().substring(0, entry.getKey().length() - dateSuffix.length()),
                                entry.getValue());
                    }
                } else if ((value instanceof Date) && (analyzeNoSuffixParamAndAddSuffix)) {
                    Object newValue = parseAnyDate(value, targetClass, keyFullName);
                    if ((isTargetClassString) && (newValue instanceof String)) {
                        String[] dateTime = getStringParam(newValue).split(" ");
                        if (dateTime.length == 2) {
                            newValue = dateTime[0];
                            additionalEntries.put(entry.getKey() + dateSuffix + timeSuffix, dateTime[1]);
                        } else {
                            logger.debug("Не удалось выделить время из строкового представления даты '" + newValue + "', содержащегося в атрибуте '" + keyFullName + "'.");
                        }
                    }
                    entry.setValue(newValue);
                    additionalEntries.put(entry.getKey() + dateSuffix, entry.getValue());
                    //additionalEntries.put(entry.getKey() + dateSuffix + timeSuffix, entry.getValue());
                }
            }
        }
        data.putAll(additionalEntries);
    }

    protected void parseDateFromMap(Map<String, Object> contrMap) {
        for (Map.Entry<String, Object> entry : contrMap.entrySet()) {
            if (entry.getKey().endsWith(KEY_NAME_DATE_SUFFIX)) {
                // попытка парсить дату
                DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                DateFormat df2 = new SimpleDateFormat("ddMMyyyy");
                if (entry.getValue() != null) {
                    String date = entry.getValue().toString();

                    try {
                        entry.setValue(df1.parse(date));
                    } catch (ParseException ex) {
                        try {
                            entry.setValue(df2.parse(date));
                        } catch (ParseException ex1) {
                            logger.error("parse " + entry.getKey() + " error", ex1);
                        }
                    }
                }
            } else {
                Object entryValue = entry.getValue();
                if (entryValue != null) {
                    if (entryValue instanceof Map) {
                        // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                        parseDateFromMap((Map<String, Object>) entryValue);
                    }
                    if (entryValue instanceof List) {
                        // например застрахованные
                        List<Object> entryAsList = (List) entryValue;
                        for (Object entryMap : entryAsList) {
                            // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                            parseDateFromMap((Map<String, Object>) entryMap);
                        }
                    }
                }
            }
        }
    }

    protected void formatDateFromMap(Map<String, Object> contrMap) {
        for (Map.Entry<String, Object> entry : contrMap.entrySet()) {
            if (entry.getKey().endsWith(KEY_NAME_DATE_SUFFIX)) {
                // попытка парсить дату
                DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                if (entry.getValue() != null) {
                    try {
                        Date date = (Date) entry.getValue();
                        entry.setValue(df1.format(date));
                    } catch (Exception ex1) {
                        logger.error("parse " + entry.getKey() + " error", ex1);
                    }

                }
            } else {
                Object entryValue = entry.getValue();
                if (entryValue != null) {
                    if (entryValue instanceof Map) {
                        // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                        formatDateFromMap((Map<String, Object>) entryValue);
                    }
                    if (entryValue instanceof List) {
                        // например застрахованные
                        List<Object> entryAsList = (List) entryValue;
                        for (Object entryMap : entryAsList) {
                            // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                            formatDateFromMap((Map<String, Object>) entryMap);
                        }
                    }
                }
            }
        }
    }

    /**
     * Сравнение дат по дню
     *
     * @param psDate1 String
     * @param psDate2 String
     * @return boolean
     * @throws ParseException
     */
    protected boolean compareDates(String psDate1, String psDate2) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = dateFormat.parse(psDate1);
        Date date2 = dateFormat.parse(psDate2);

        if (date2.after(date1)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Сравнение дат по дню
     *
     * @param psDate1 Date
     * @param psDate2 Date
     * @return boolean
     * @throws ParseException
     */
    protected boolean compareDates(Date psDate1, Date psDate2) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = dateFormat.format(psDate1);
        String date2 = dateFormat.format(psDate2);

        if (date1.equals(date2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Рекурсивно устанавливает всем картам объекта marked (включая вложенные)
     * для ключа keyName значение keyValue (например, для массовой установки
     * сущностям статусов ROWSTATUS и FLAG)
     *
     * @param marked   обрабатываемый объект
     * @param keyName  имя ключа, указывающего на устанавливаемое значение
     * @param keyValue устанавливаемое значение
     */
    protected void markAllMapsByKeyValue(Object marked, String keyName, Object keyValue) {
        if (marked == null) {
            return;
        }
        if (marked instanceof List) {
            List<Object> markedList = (List<Object>) marked;
            for (Object row : markedList) {
                markAllMapsByKeyValue(row, keyName, keyValue);
            }
        }
        if (marked instanceof Map) {
            Map<String, Object> markedMap = (Map<String, Object>) marked;
            markedMap.put(keyName, keyValue);
            for (String key : markedMap.keySet()) {
                markAllMapsByKeyValue(markedMap.get(key), keyName, keyValue);
            }
        }
    }

    protected Map<String, Object> loadParticipant(Object participantId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        params.put("PARTICIPANTID", participantId);
        Map<String, Object> partRes = this.callService(Constants.CRMWS, "participantGetByIdFull", params, login, password);
        //Map<String, Object> partRes = this.callServiceLogged(Constants.CRMWS, "participantGetByIdFull", params, login, password);

        // костыль для загрузки учредителей для юр лица. т.к. црм поддерживает автозагрузку только учредилелей финансовых организаций.
        // а сохраняет норм.
        if (partRes != null) {
            if (partRes.get("PARTICIPANTTYPE") != null) {
                if ("2".equals(partRes.get("PARTICIPANTTYPE").toString())) {
                    Map<String, Object> affParams = new HashMap<String, Object>();
                    affParams.put("MAINPARTICIPANTID", participantId);
                    Map<String, Object> affParamsRes = this.callService(Constants.CRMWS, "affiliateGetListByMainParticipantId", affParams, login, password);
                    if (affParamsRes != null) {
                        if (affParamsRes.get(RESULT) != null) {
                            partRes.put("affiliateIfnsList", affParamsRes.get(RESULT));
                        }
                    }
                }
            }
        }
        //<editor-fold defaultstate="collapsed" desc="преобразование сведений об участнике - сейчас не требуется">
        //Map<String, Object> participant = mapParticipantToForm(partRes);
        // возврат преобразованных сведений об участнике
        //return participant;
        //</editor-fold>
        //
        // прямой возврат результата запроса из CRM
        return partRes;
    }

    public static String base64Decode(String input) {
        Base64 decoder = new Base64(true);
        return bytesToString(decoder.decode(input));
    }

    public static String base64Encode(String input) {
        Base64 encoder = new Base64(true);
        String result = bytesToString(encoder.encode(stringToBytes(input)));
        return result.substring(0, result.length() - 2);
    }

    public static byte[] stringToBytes(String value) {
        byte[] result = null;
        try {
            result = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return result;
    }

    public static String bytesToString(byte[] value) {
        String result = "";
        try {
            result = new String(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return result;
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

                    result = this.callService(Constants.B2BPOSWS, methodNamePrefix + "_State_MakeTrans", makeTransParams, login, password);
                } else {
                    logger.debug("     No transition selected by this transition params.");
                }
            }
        }
        logger.debug("State transition finished with result: " + result);
        return result;
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public boolean isAllEmailValid(String allEMail) {
        boolean result = true;
        String newEmailList = "";
        if (allEMail == null) {
            return false;
        }
        if (allEMail.isEmpty()) {
            return false;
        }
        String[] emailList = allEMail.split(",");
        if (emailList.length == 0) {
            return false;
        }
        for (String email : emailList) {
            if (isValidEmailAddress(email)) {
                if (newEmailList.isEmpty()) {
                    newEmailList = email;
                } else {
                    newEmailList = newEmailList + "," + email;
                }
            }
        }
        if (newEmailList.isEmpty()) {
            return false;
        } else {
            allEMail = newEmailList;
            return true;
        }
    }

    // формирование из списка специальной мапы (ключ - значение указанного поля, значение - соответсвующий элемент списка)
    // (например, для формирования мап по сис. наименованиям и т.п.)
    protected Map<String, Map<String, Object>> getMapByFieldStringValues(List<Map<String, Object>> list, String fieldName) {
        Map<String, Map<String, Object>> mapByFieldStringValues = new HashMap<String, Map<String, Object>>();
        if ((list != null) && (fieldName != null) && (list.size() > 0) && (!fieldName.isEmpty())) {
            for (Map<String, Object> bean : list) {
                String fieldValue = getStringParam(bean, fieldName);
                mapByFieldStringValues.put(fieldValue, bean);
            }
        }
        return mapByFieldStringValues;
    }

    // аналог getMapByFieldStringValues, но для типа Long
    // (например, для формирования мап по ИД и т.п.)
    protected Map<Long, Map<String, Object>> getMapByFieldLongValues(List<Map<String, Object>> list, String fieldName) {
        Map<Long, Map<String, Object>> mapByFieldLongValues = new HashMap<Long, Map<String, Object>>();
        if ((list != null) && (fieldName != null) && (list.size() > 0) && (!fieldName.isEmpty())) {
            for (Map<String, Object> bean : list) {
                Long fieldValue = getLongParam(bean, fieldName);
                mapByFieldLongValues.put(fieldValue, bean);
            }
        }
        return mapByFieldLongValues;
    }

    // ПАРТНЕР - получение наименование партнера для конкретного дочернего подразделения по ИД этого дочернего подразделения
    protected String getPartnerNameByChildDepartmentID(Long childDepartmentID, String login, String password) throws Exception {
        String defaultValue = NO_PARTNER_INFO_FOUND;
        return getPartnerNameByChildDepartmentID(childDepartmentID, defaultValue, login, password);
    }

    // ПАРТНЕР - получение наименование партнера для конкретного дочернего подразделения по ИД этого дочернего подразделения
    protected String getPartnerNameByChildDepartmentID(Long childDepartmentID, String defaultValue, String login, String password) throws Exception {

        String partnerName = "";
        String errorText = "";
        Map<String, Object> partnerInfo = null;
        if (childDepartmentID != null) {
            Map<String, Object> partnerParams = new HashMap<String, Object>();
            partnerParams.put(PARTNERS_DEPARTMENT_CODE_LIKE_KEY_NAME, PARTNERS_DEPARTMENT_CODE_LIKE);
            partnerParams.put("USERDEPARTMENTID", childDepartmentID);
            partnerParams.put(RETURN_AS_HASH_MAP, true);
            partnerInfo = this.callService(Constants.B2BPOSWS, "dsB2BPartnersDepartmentsListBrowseListByParamEx", partnerParams, true, login, password);
            if (isCallResultOK(partnerInfo)) {
                partnerName = getStringParamLogged(partnerInfo, "DEPTSHORTNAME");
            } else {
                // не удалось определить данные Партнера
                errorText = "";
                if (partnerInfo != null) {
                    errorText = getStringParamLogged(partnerInfo, "Error");
                }
            }
        }
        if (partnerName.isEmpty()) {
            // не удалось определить данные Партнера
            if (errorText.isEmpty()) {
                errorText = "Unknown error while getting partner info for sending notification e-mail!";
                if (partnerInfo != null) {
                    errorText = errorText + " Details: " + partnerInfo;
                }
            } else {
                errorText = "Error while getting partner info for sending notification e-mail: " + errorText;
            }
            logger.error(errorText);
            partnerName = defaultValue;
        }
        return partnerName;
    }

    // ПАРТНЕР - получение наименование партнера для конкретного дочернего подразделения по ИД этого дочернего подразделения
    protected Map<String, Object> getPartnerInfoByChildDepartmentID(Long childDepartmentID, String defaultValue, String login, String password) throws Exception {

        String partnerName = "";
        String errorText = "";
        Map<String, Object> partnerInfo = null;
        if (childDepartmentID != null) {
            Map<String, Object> partnerParams = new HashMap<String, Object>();
            partnerParams.put(PARTNERS_DEPARTMENT_CODE_LIKE_KEY_NAME, PARTNERS_DEPARTMENT_CODE_LIKE);
            partnerParams.put("USERDEPARTMENTID", childDepartmentID);
            partnerParams.put(RETURN_AS_HASH_MAP, true);
            partnerInfo = this.callService(Constants.B2BPOSWS, "dsB2BPartnersDepartmentsListBrowseListByParamEx", partnerParams, true, login, password);
            if (isCallResultOK(partnerInfo)) {
                partnerName = getStringParamLogged(partnerInfo, "DEPTSHORTNAME");
            } else {
                // не удалось определить данные Партнера
                errorText = "";
                if (partnerInfo != null) {
                    errorText = getStringParamLogged(partnerInfo, "Error");
                }
            }
        }
        return partnerInfo;
    }

    protected Date addLagDaysToDate(Date date, Object sdLag, Object sdCalendarType, String login, String password) throws Exception {
        if ((date != null) && (sdLag != null)) {
            Long lag = Long.valueOf(sdLag.toString());
            Long calType = 1L;
            if (sdCalendarType != null) {
                calType = Long.valueOf(sdCalendarType.toString());
            }
            GregorianCalendar gcDate = new GregorianCalendar();
            gcDate.setTime(date);
            // если используется обычный календарь, тогда просто прибавляем лаг,
            // иначе читаем производственный календарь из БД и прибавляем оттуда рабочие дни
            if (calType.longValue() == 1) {
                gcDate.add(com.ibm.icu.util.Calendar.DAY_OF_YEAR, lag.intValue());
            } else {
                Map<String, Object> calParams = new HashMap<String, Object>();
                calParams.put("STARTCALDATE", (Double) parseAnyDate(gcDate.getTime(), Double.class, "DOCUMENTDATE"));
                GregorianCalendar gcDatePlus = new GregorianCalendar();
                gcDatePlus.setTime(date);
                // прибавляем 60 дней, должно хватить (лаг не должен быть больше 60 дней)
                gcDatePlus.add(com.ibm.icu.util.Calendar.DAY_OF_YEAR, 60);
                calParams.put("FINISHCALDATE", (Double) parseAnyDate(gcDatePlus.getTime(), Double.class, "DOCUMENTDATE"));
                calParams.put("ISDAYOFF", 1L);
                calParams.put("ORDERBY", "T.CALDATE");
                List<Map<String, Object>> calList = this.callServiceAndGetListFromResultMap(Constants.B2BPOSWS,
                        "dsB2BCalendarBrowseListByParamEx", calParams, login, password);
                // в lag храним сколько осталось дней еще прибавить (выходные мы должны пропускать)
                // в idx храним текущую позицию даты в calList
                int idx = 0;
                gcDate.set(com.ibm.icu.util.Calendar.HOUR_OF_DAY, 0);
                gcDate.set(com.ibm.icu.util.Calendar.MINUTE, 0);
                gcDate.set(com.ibm.icu.util.Calendar.SECOND, 0);
                gcDate.set(com.ibm.icu.util.Calendar.MILLISECOND, 0);
                while (lag.longValue() > 0) {
                    gcDate.add(com.ibm.icu.util.Calendar.DAY_OF_YEAR, 1);
                    Date dCalDate = (Date) parseAnyDate(calList.get(idx).get("CALDATE"), Date.class, "CALDATE");
                    GregorianCalendar gcCalcDate = new GregorianCalendar();
                    gcCalcDate.setTime(dCalDate);
                    gcCalcDate.set(com.ibm.icu.util.Calendar.HOUR_OF_DAY, 0);
                    gcCalcDate.set(com.ibm.icu.util.Calendar.MINUTE, 0);
                    gcCalcDate.set(com.ibm.icu.util.Calendar.SECOND, 0);
                    gcCalcDate.set(com.ibm.icu.util.Calendar.MILLISECOND, 0);
                    // если текущий день - выходной, пропускаем его, при этом лаг не уменьшается
                    if (gcDate.getTimeInMillis() >= gcCalcDate.getTimeInMillis()) {
                        idx++;
                    } else {
                        lag -= 1;
                    }
                }
            }
            return gcDate.getTime();
        } else {
            return null;
        }
    }

    // генерация строковых представлений для всех дат
    protected void genDateStrs(Map<String, Object> data) {
        genDateStrs(data, "*");
    }


    // генерация строковых представлений для всех дат
    protected void genDateStrs(Map<String, Object> data, String dataNodePath) {
        Map<String, Object> parsedMap = new HashMap<String, Object>();
        parsedMap.putAll(data);
        for (Map.Entry<String, Object> entry : parsedMap.entrySet()) {
            String keyName = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String dataValuePath = dataNodePath + "." + keyName;
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    genDateStrs(map, dataValuePath);
                } else if (value instanceof List) {
                    ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) value;
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, Object> element = list.get(i);
                        genDateStrs(element, dataValuePath + "[" + i + "]");
                    }
                } else {

                    if ((keyName.endsWith("DATE")) || (keyName.endsWith("Date"))) {
                        //logger.debug("");
                        //logger.debug("Found date-like key name: " + dataValuePath);
                        if (value instanceof String) {
                            //value = parseAnyDate(value, Date.class, keyName, Boolean.TRUE);
                            value = parseAnyDate(value, Date.class, keyName, Boolean.FALSE);
                        }
                    }

                    if (value instanceof Date) {
                        //
                        try {
                            Date date = (Date) value;
                            DateFormat df = new SimpleDateFormat("dd.ММ.yyyy");
                            String reportDate = dateFormatter.format(date);
                            data.put(keyName + "STR", reportDate);
                            //logger.debug(dataNodePath + "." + keyName + "STR = " + reportDate);
                            String reportDateMonth = dateFormatterMonth.format(date);
                            data.put(keyName + "MONTHLYSTR", reportDateMonth);
                            //logger.debug(dataNodePath + "." + keyName + "MONTHLYSTR = " + reportDateMonth);
                            //} catch (NumberFormatException ex) {
                            //    logger.debug(dataValuePath + " - не сумма.");
                        } catch (IllegalArgumentException ex) {
                            logger.debug(dataValuePath + " не удалось преобразовать в строковое представление даты.");
                        }
                    }
                }
            }
        }
    }

    protected Map<String, Object> makeErrorResult(String errorText) {
        HashMap<String, Object> resultWithError = new HashMap<String, Object>();
        makeErrorResult(resultWithError, errorText);
        return resultWithError;
    }

    protected Map<String, Object> makeErrorResult(Map<String, Object> result, String errorText) {
        logger.error(errorText + "\n");
        result.put(ERROR, errorText);
        return result;
    }

    /**
     * формирование из списка специальной мапы (ключ - значение указанного поля указанной подчиненной мапы, значение - соответсвующий элемент списка)
     * (например, для формирования мап по сис. наименованиям для словарной системы когда сис. наименовние находится не в элменте, а во вложенной мапе *_EN и т.п.)
     *
     * @param list
     * @param subMapName
     * @param fieldName
     * @return
     */
    protected Map<String, Map<String, Object>> getMapByFieldStringValues(List<Map<String, Object>> list, String subMapName, String fieldName) {
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

    // аналог getMapByFieldStringValues, но для типа Date
    // (например, для формирования мап по ИД и т.п.)
    protected Map<Date, Map<String, Object>> getMapByFieldDateValues(List<Map<String, Object>> list, String fieldName) {
        Map<Date, Map<String, Object>> mapByFieldLongValues = new HashMap<Date, Map<String, Object>>();
        if ((list != null) && (fieldName != null) && (list.size() > 0) && (!fieldName.isEmpty())) {
            for (Map<String, Object> bean : list) {
                Object fieldValueObj = bean.get(fieldName);
                if (fieldValueObj != null) {
                    Date fieldValue = (Date) parseAnyDate(fieldValueObj, Date.class, fieldName);
                    mapByFieldLongValues.put(fieldValue, bean);
                }
            }
        }
        return mapByFieldLongValues;
    }

    protected Map<String, Object> getItemByFieldStringValues(List<Map<String, Object>> list, String fieldName, String fieldValue) {
        Map<String, Object> foundItem = null;
        if ((list != null) && (fieldName != null) && (fieldValue != null) && (list.size() > 0) && (!fieldName.isEmpty())) {
            for (Map<String, Object> item : list) {
                String itemFieldValue = getStringParam(item, fieldName);
                if (fieldValue.equals(itemFieldValue)) {
                    foundItem = item;
                    break;
                }
            }
        }
        return foundItem;
    }

    // помечает сущность как изменившуюся (условно - только если требуется обновить значение в БД)
    protected Boolean markAsModified(Map<String, Object> targetStruct) {
        Boolean isMarkedAsModified = false;
        Object currentRowStatus = targetStruct.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
            targetStruct.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
            isMarkedAsModified = true;
        }
        return isMarkedAsModified;
    }

    protected Map<String, Object> callExternalServiceLogged(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        logger.debug("Call external service method [" + serviceName + "] " + methodName + " with parameters:\n\n" + params + "\n");
        // вызов действительного метода
        Map<String, Object> callResult = this.callExternalService(serviceName, methodName, params, login, password);
        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        logger.debug("External service method method [" + serviceName + "] " + methodName + " executed in " + callTimer + " ms and returned result:\n\n" + callResult + "\n");
        // возврат результата
        return callResult;
    }

    protected static List<Map<String, Object>> getListFromResultMap(Map<String, Object> resultMap) {
        List<Map<String, Object>> list = WsUtils.getListFromResultMap(resultMap);
        return list;
    }


    // аналог getLongParam, но с расширенным протоколировнием полученного значения
    protected Long getLongParamLogged(Map<String, Object> map, String keyName, String commentName) {
        Boolean isCommentPrefix = false;
        Long paramValue = getLongParamLogged(map, keyName, commentName, isCommentPrefix);
        return paramValue;
    }

    // аналог getLongParam, но с расширенным протоколировнием полученного значения
    protected Long getLongParamLogged(Map<String, Object> map, String keyName, String commentName, Boolean isCommentPrefix) {
        Long paramValue = getLongParam(map, keyName);
        tryLogParam(commentName, keyName, paramValue, isCommentPrefix);
        return paramValue;
    }

    private void tryLogParam(String commentName, String keyName, Object paramValue, Boolean isCommentPrefix) {
        if (logger.isDebugEnabled()) {
            String debugMsgFormat = isCommentPrefix ? "%s%s = " : "%s (%s) = ";
            String debugMsg = String.format(debugMsgFormat, commentName, keyName);
            logger.debug(debugMsg + paramValue);
        }
    }


    // аналог getStringParam, но с расширенным протоколировнием полученного значения
    protected String getStringParamLogged(Map<String, Object> map, String keyName, String commentName) {
        Boolean isCommentPrefix = false;
        String paramValue = getStringParamLogged(map, keyName, commentName, isCommentPrefix);
        return paramValue;
    }


    // аналог getStringParam, но с расширенным протоколировнием полученного значения
    protected String getStringParamLogged(Map<String, Object> map, String keyName, String commentName, Boolean isCommentPrefix) {
        String paramValue = getStringParam(map, keyName);
        tryLogParam(commentName, keyName, paramValue, isCommentPrefix);
        return paramValue;
    }

    protected Map<String, Object> recordFindTransEx(String typeSysName, String fromStateSysName, String toStateSysName, String login, String password) throws Exception {
        Map<String, Object> transParams = new HashMap<String, Object>();
        if (!typeSysName.isEmpty()) {
            transParams.put("TYPESYSNAME", typeSysName);
        }
        if (!fromStateSysName.isEmpty()) {
            transParams.put("FROMSTATESYSNAME", fromStateSysName);
        }
        if (!toStateSysName.isEmpty()) {
            transParams.put("TOSTATESYSNAME", toStateSysName);
        }
        transParams.put("JOIN_TO_SMTYPE", true);
        transParams.put(RETURN_AS_HASH_MAP, true);
        //Map<String, Object> result = this.callService(Constants.INSPOSWS, "dsTransitionsBrowseByParamEx", transParams, login, password);
        // стандартный insposws#dsTransitionsBrowseByParamEx не возвращает FROMSTATESYSNAME/TOSTATESYSNAME - используем модификацию из b2b
        Map<String, Object> result = this.callServiceLogged(Constants.B2BPOSWS, "dsB2BTransitionsBrowseListByParamEx", transParams, login, password);
        return result;
    }

    // помечает сущность как удаляемую (условно - только если требуется обновить значение в БД)
    protected Boolean markAsDeleted(Map<String, Object> targetStruct, String pkKeyName) {
        Boolean isMarkedAsDeleted = false;
        Object currentRowStatus = targetStruct.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && ((getIntegerParam(currentRowStatus) == UNMODIFIED_ID) || (getIntegerParam(currentRowStatus) == MODIFIED_ID)) && (targetStruct.get(pkKeyName) != null)) {
            targetStruct.put(ROWSTATUS_PARAM_NAME, DELETED_ID);
            isMarkedAsDeleted = true;
        }
        return isMarkedAsDeleted;
    }

    protected List<Map<String, Object>> clearListFromKeysAndEns(List<Map<String, Object>> list, String... keyNames) {
        for (Map<String, Object> bean : list) {
            for (String keyName : keyNames) {
                bean.remove(keyName);
                bean.remove(keyName + "_EN");
            }
        }
        return list;
    }

    protected Map<String, Object> clearMapFromKeysAndEns(Map<String, Object> map, String... keyNames) {
        for (String keyName : keyNames) {
            map.remove(keyName);
            map.remove(keyName + "_EN");
        }
        return map;
    }

    protected List<Map<String, Object>> clearAllListFromKeysAndEns(List<Map<String, Object>> list, boolean isClearNulls, boolean isClearEns, String... keyNames) {
        if (list != null) {
            Set<String> removedKeyNameList = new HashSet<String>();
            removedKeyNameList.addAll(Arrays.asList(keyNames));
            for (Map<String, Object> bean : list) {
                clearAllMapFromKeysAndEtc(bean, isClearNulls, isClearEns, removedKeyNameList);
            }
        }
        return list;
    }

    protected List<Map<String, Object>> clearAllListFromKeysAndEns(List<Map<String, Object>> list, boolean isClearNulls, boolean isClearEns, Set<String> removedKeyNameList) {
        if (list != null) {
            for (Map<String, Object> bean : list) {
                clearAllMapFromKeysAndEtc(bean, isClearNulls, isClearEns, removedKeyNameList);
            }
        }
        return list;
    }

    protected Map<String, Object> clearAllMapFromKeysAndEtc(Map<String, Object> map, boolean isClearNulls, boolean isClearEns, String... keyNames) {
        if (map != null) {
            Set<String> removedKeyNameList = new HashSet<String>();
            removedKeyNameList.addAll(Arrays.asList(keyNames));
            clearAllMapFromKeysAndEtc(map, isClearNulls, isClearEns, removedKeyNameList);
        }
        return map;
    }

    protected Map<String, Object> clearAllMapFromKeysAndEtc(Map<String, Object> map, boolean isClearNulls, boolean isClearEns, Set<String> removedKeyNameList) {
        if (map != null) {
            if (isClearNulls) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value == null) {
                        removedKeyNameList.add(key);
                    }
                }
            }
            if (isClearEns) {
                Set<String> removedEnKeyNameList = new HashSet<String>();
                for (String keyName : removedKeyNameList) {
                    if (!keyName.endsWith("_EN")) {
                        removedEnKeyNameList.add(keyName + "_EN");
                    }
                }
                removedKeyNameList.addAll(removedEnKeyNameList);
            }
            for (String keyName : removedKeyNameList) {
                map.remove(keyName);
            }
            List<Map> subMapList = new ArrayList<Map>();
            for (Object value : map.values()) {
                if (value == null) {
                } else if (value instanceof Map) {
                    Map valueAsMap = (Map) value;
                    subMapList.add(valueAsMap);
                } else if (value instanceof List) {
                    List valueAsList = (List) value;
                    for (Object valueAsListItem : valueAsList) {
                        if (valueAsListItem instanceof Map) {
                            Map valueAsMap = (Map) valueAsListItem;
                            subMapList.add(valueAsMap);
                        }
                    }
                }
            }
            for (Map subMap : subMapList) {
                try {
                    // для вложенной мапы isClearEns = false, потому что removedKeyNameList теперь уже содержит все *_EN
                    clearAllMapFromKeysAndEtc((Map<String, Object>) subMap, isClearNulls, false, removedKeyNameList);
                } catch (Exception ex) {
                    logger.warn("Exception caused by clearAllMapFromKeys method was catched and suppressed. Details (catched exception):", ex);
                }
            }
        }
        return map;
    }

    protected List<Map<String, Object>> clearListByLongFieldValue(List<Map<String, Object>> sourceList, String fieldName, Long fieldValue) {
        Set<Long> fieldValues = new HashSet<Long>();
        fieldValues.add(fieldValue);
        return clearListByLongFieldValues(sourceList, fieldName, fieldValues);
    }

    protected List<Map<String, Object>> clearListByLongFieldValues(List<Map<String, Object>> sourceList, String fieldName, Set<Long> fieldValues) {
        for (Iterator<Map<String, Object>> iterator = sourceList.iterator(); iterator.hasNext(); ) {
            Map<String, Object> bean = iterator.next();
            Long beanFieldValue = getLongParam(bean, fieldName);
            if (!fieldValues.contains(beanFieldValue)) {
                iterator.remove();
            }
        }
        return sourceList;
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

    private String getMonthEndGen(int duration) {
        /* String result = "ев";
         int num = duration % 100;
         if (ANUM.contains(num)) {
         result = "а";
         }
         return result;*/
        String result = "ев";
        int num = duration % 100;
        if (ANUM.contains(num)) {
            result = "а";
        }
        /*
         if (LETNUM.contains(num)) {
         result = "ев";
         }*/
        return result;
    }

    private String getDurationStringGenetive(int duration) {
        //String durationStr = AmountUtils.amountToString(String.valueOf(duration));
        //    FormatUtils.
        String durationStr = NumberFormatUtils.toWordsInGenetive(String.valueOf(duration));
        if (durationStr.indexOf("целых") >= 0) {
            durationStr = durationStr.substring(0, durationStr.indexOf("целых") - 1).toLowerCase();
        }
        if (durationStr.indexOf("целой") >= 0) {
            durationStr = durationStr.substring(0, durationStr.indexOf("целой") - 3).toLowerCase() + "ого";
        }

        String monthEnd = getMonthEndGen(duration);
        String result = String.valueOf(duration) + " (" + durationStr + ") месяц" + monthEnd;
        return result;
    }

    protected String getIntStringGenetive(int duration) {
        //String durationStr = AmountUtils.amountToString(String.valueOf(duration));
        //    FormatUtils.
        String durationStr = NumberFormatUtils.toWordsInGenetive(String.valueOf(duration));
        if (durationStr.indexOf("целых") >= 0) {
            durationStr = durationStr.substring(0, durationStr.indexOf("целых") - 1).toLowerCase();
        }
        if (durationStr.indexOf("целой") >= 0) {
            durationStr = durationStr.substring(0, durationStr.indexOf("целой") - 3).toLowerCase() + "ого";
        }

        //String monthEnd = getMonthEndGen(duration);
        //String result = String.valueOf(duration) + " (" + durationStr + ") месяц" + monthEnd;
        return durationStr;
    }

    protected boolean isCallFromGate(Map<String, Object> callParams) {
        boolean isCallFromGate = false;
        if (callParams != null) {
            isCallFromGate = getBooleanParam(callParams, IS_CALL_FROM_GATE_PARAMNAME, false);
        }
        return isCallFromGate;
    }

    protected boolean isCallFromUniOpenAPI(Map<String, Object> callParams) {
        boolean isCallFromUniOpenAPI = false;
        if (callParams != null) {
            isCallFromUniOpenAPI = getBooleanParam(callParams, IS_CALL_FROM_UNIOPENAPI_PARAMNAME, false);
        }
        return isCallFromUniOpenAPI;
    }

    protected RowStatus getRowStatusLogged(Map<String, Object> map) {
        Long rowStatusLongValue = getLongParam(map, ROWSTATUS_PARAM_NAME);
        RowStatus rowStatus;
        if (rowStatusLongValue == null) {
            rowStatus = RowStatus.INSERTED;
        } else {
            rowStatus = RowStatus.getRowStatusById(rowStatusLongValue.intValue());
        }
        logger.debug(String.format(ROWSTATUS_LOG_PATTERN, rowStatus.name(), rowStatus.getId()));
        return rowStatus;
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

    private void tryMapParamLogging(String keyName, Map<String, Object> mapParamValue) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("%s = %s", keyName, mapParamValue));
        }
    }

    protected Map<String, Object> getOrCreateMapParamLogged(Map<String, Object> parentMap, String keyName) {
        Map<String, Object> mapParamValue = getOrCreateMapParam(parentMap, keyName, true);
        tryMapParamLogging(keyName, mapParamValue);
        return mapParamValue;
    }

    protected Map<String, Object> getMapParamLogged(Map<String, Object> parentMap, String keyName) {
        Map<String, Object> mapParamValue = getOrCreateMapParam(parentMap, keyName, false);
        tryMapParamLogging(keyName, mapParamValue);
        return mapParamValue;
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

    private Object getObjectPrettyPrinted(Object obj) {
        Object objectPrettyPrinted;
        try {
            objectPrettyPrinted = objectWriterWithDefaultPrettyPrinter.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            objectPrettyPrinted = obj;
        }
        return objectPrettyPrinted;
    }

    protected void loggerDebugPretty(Logger logger, String prefixMsg, Object obj) {
        if (logger.isDebugEnabled()) {
            Object loggedObj;
            loggedObj = getObjectPrettyPrinted(obj);
            logger.debug(String.format("%s:\n%s", prefixMsg, loggedObj));
        }
    }

    protected void loggerErrorPretty(Logger logger, String prefixMsg, String prefixObj1, Object obj1, String prefixObj2, Object obj2) {
        Object loggedObj1 = getObjectPrettyPrinted(obj1);
        Object loggedObj2 = getObjectPrettyPrinted(obj2);
        logger.error(String.format("%s\n%s: %s\n%s: %s", prefixMsg, prefixObj1, loggedObj1, prefixObj2, loggedObj2));
    }

    protected void loggerErrorPretty(Logger logger, String prefixMsg, Object obj) {
        Object loggedObj = getObjectPrettyPrinted(obj);
        logger.error(String.format("%s:\n%s", prefixMsg, loggedObj));
    }

    /**
     * Расширенное протоколирование ошибки вызова метода.
     *
     * @param serviceName имя сервиса, метод которого был вызван
     * @param methodName имя вызванного метода
     * @param callParams параметры вызова метода
     * @param callResult результат вызова метода (вероятно, содержащий сведения о возникшей ошибке)
     *
     */
    protected void loggerErrorServiceCall(String serviceName, String methodName, Map<String, Object> callParams, Map<String, Object> callResult) {
        String errorPrefix = String.format("Calling %s from %s resulted in error!", methodName, serviceName);
        logger.error(String.format(
                "%s\nDetails (call result raw): %s.\nDetails (call params raw): %s.",
                errorPrefix, callResult, callParams
        ));
        loggerErrorPretty(logger, errorPrefix, "Details (call result)", callResult, "Details (call params)", callParams);
    }

    /*
    // здесь более не требуется - будет доступно за счет B2BBaseFacade implements SeaweedsGetters
    public String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }

    // здесь более не требуется - будет доступно за счет B2BBaseFacade implements SeaweedsGetters
    public boolean isUseSeaweedFS() {
        String useSeaweedFS = getUseSeaweedFS();
        boolean isUseSeaweedFS = useSeaweedFS.equalsIgnoreCase("TRUE");
        return isUseSeaweedFS;
    }

    // здесь более не требуется - будет доступно за счет B2BBaseFacade implements SeaweedsGetters
    public String getSeaweedFSUrl() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("SEAWEEDFSURL", "");
        return login;
    }
    */

    private String getMetadataURL(String login, String password, String project) throws Exception {
        Map args = new HashMap();
        args.put("fileType", "REPORTS");
        args.put(PROJECT_PARAM_NAME, project);
        String adminwsURL = Config.getConfig().getParam("adminws", "http://localhost:8080/adminws/adminws");
        String COREWSURL = Config.getConfig().getParam("corews", "http://localhost:8080/corews/corews");
        try {
            XMLUtil xmlutil = new XMLUtil(login, password);
            args.put("PROJECTSYSNAME", project);
            Map resultMap = xmlutil.doURL(adminwsURL, "admProjectBySysname", xmlutil.createXML(args), null);

            List result = (List) resultMap.get("Result");
            if (logger.isDebugEnabled()) {
                logger.debug("projectBySysname result = " + result);
            }
            if ((result != null) && (result.size() == 1)
                    && (((Map) result.get(0)).containsKey("METADATAURL")) && ((((Map) result.get(0)).get("METADATAURL") instanceof String))
                    && (!((Map) result.get(0)).get("METADATAURL").toString().equals(""))) {
                return ((Map) result.get(0)).get("METADATAURL").toString();
            }

            resultMap = xmlutil.doURL(COREWSURL, "getSystemMetadataURL", xmlutil.createXML(args), null);
            if (logger.isDebugEnabled()) {
                logger.debug("getMetadataURL result = " + resultMap);
            }
            if (resultMap.containsKey("MetadataURL")) {
                return resultMap.get("MetadataURL").toString();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
        return null;
    }

    protected String getProjectName(String login, String password) throws Exception {
        String result = null;
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> namesList = new ArrayList();
        namesList.add("DEFAULT_PROJECT_SYSNAME");
        params.put("NAMES", namesList);
        params.put("LOGIN", login);
        Map<String, Object> res = callService(WsConstants.COREWS, "dsAccountSettingFindByLoginAndName", params, login, password);
        if ((res != null) && (res.get("Status") != null) && (res.get("Status").equals("OK"))) {
            result = (String) res.get("DEFAULT_PROJECT_SYSNAME");
        }
        return result;
    }

    protected String getTemplateFullPath(String path, String login, String password) throws Exception {
        String project = this.getProjectName(login, password);
        String metadataURL = getMetadataURL(login, password, project);
        String fullPath = "";
        if ((metadataURL == null) || (metadataURL.equals(""))) {
            metadataURL = Config.getConfig("reportws").getParam("rootPath", "C:/bea/workshop92/METADATA/REPORTS/");

            fullPath = metadataURL + path;
        } else {
            if ((!metadataURL.endsWith("/")) && (!metadataURL.endsWith("\\"))) {
                metadataURL = metadataURL + File.separator;
            }
            if (!path.contains("REPORTS/")) {
                metadataURL = metadataURL + "REPORTS/";
            }
            fullPath = metadataURL + path;
        }
        return fullPath;
    }

    protected void processDocListForUpload(List<Map<String, Object>> docList, Map<String, Object> params, String login, String password) throws Exception {
        if ((params.get("URLPATH") != null) && (params.get("SESSIONIDFORCALL") != null) && (docList != null)) {
            String pathPrefix = params.get("URLPATH").toString() + "?sid=" + params.get("SESSIONIDFORCALL").toString() + "&fn=";
            for (Map<String, Object> docItem : docList) {
                if (docItem.get("FILEPATH") != null) {
                    String docPath = docItem.get("FILEPATH").toString();
                    String docName = docPath;
                    if (docItem.get("ISPDF") == null) {
                        if (docPath.contains("\\")) {
                            docName = docPath.substring(docPath.indexOf("\\") + 1);
                        }
                        if (docName.contains("/")) {
                            docName = docPath.substring(docPath.indexOf("/") + 1);
                        }
                    }
                    String userDocName = docItem.get("FILENAME").toString();
                    SessionController controller = new B2BFileSessionController();
                    Map<String, Object> sessionParams = new HashMap<>();
                    if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (docItem.get("FSID") != null)) {
                        sessionParams.put(FS_TYPE_PARAMNAME, Constants.FS_EXTERNAL);
                        sessionParams.put(SOME_ID_PARAMNAME, docItem.get("FSID").toString());
                        sessionParams.put(USER_DOCNAME_PARAMNAME, userDocName);
                    } else {
                        if (docItem.get("ISPDF") == null) {
                            File src = new File(docName);
                            String name = src.getName();
                            docName = name.replace("\\", "").replace("/", "");
                        }
                        sessionParams.put(FS_TYPE_PARAMNAME, Constants.FS_HARDDRIVE);
                        sessionParams.put(SOME_ID_PARAMNAME, docName);
                        sessionParams.put(USER_DOCNAME_PARAMNAME, userDocName);
                    }
                    String filenameEncript = controller.createSession(sessionParams);
                    // для PDF передаем скачку по полному пути
                    if (docItem.get("ISPDF") != null) {
                        docItem.put("DOWNLOADPATH", pathPrefix + filenameEncript + "&fp=1");
                    } else {
                        docItem.put("DOWNLOADPATH", pathPrefix + filenameEncript + "&fp=0");
                    }
                    docItem.remove("FILEPATH");
                }
            }
        }
        // доп. загрузка информации по лицам
        if ((params.get("LOADCRMDATA") != null) && (Long.valueOf(params.get("LOADCRMDATA").toString()).longValue() == 1)) {
            for (Map<String, Object> docItem : docList) {
                if (docItem.get("PARTICIPANTID") != null) {
                    Map<String, Object> crmParams = new HashMap<String, Object>();
                    crmParams.put(RETURN_AS_HASH_MAP, "TRUE");
                    crmParams.put("PARTICIPANTID", docItem.get("PARTICIPANTID"));
                    Map<String, Object> crmRes = this.callService(Constants.CRMWS, "participantGetById", crmParams, login, password);
                    if (crmRes != null) {
                        docItem.put("CRMDATA", crmRes);
                    }
                }
            }
        }
    }

    protected void processDocListForUploadUniOpenAPI(List<Map<String, Object>> docList, Map<String, Object> params, String login, String password) throws Exception {
        String productSysName = getStringParamLogged(params, PRODUCT_SYSNAME_PARAMNAME);
        if (productSysName.isEmpty()) {
            // сис. наименования продукта нет во входных данных - следует определить зная ИД договора
            Long contractId = getLongParamLogged(params, "CONTRID");
            if (contractId != null) {
                Map<String, Object> contractParams = new HashMap<>();
                contractParams.put("CONTRID", contractId);
                contractParams.put(RETURN_AS_HASH_MAP, true);
                String methodName = "dsB2BContractProductSysNameByContrId";
                Map<String, Object> contractResult = callServiceLogged(B2BPOSWS_SERVICE_NAME, methodName, contractParams, login, password);
                // сис. наименование продукта определенное по ИД договора
                productSysName = getStringParamLogged(contractResult, PRODUCT_SYSNAME_PARAMNAME);
            }
        }
        String urlPrefix = "";
        if (!productSysName.isEmpty()) {
            // сис. наименование продукта определено - следует получить первую часть ссылок (фиксированную)
            urlPrefix = getParamFromProductDefaultValuesOrCoreSetting("UNIOPENAPI_FILES_URL", productSysName, "", login, password);
        }
        // todo: заменить на полуение из справоника или константы или т.п.
        // временное решение - для ЗЗМ 48 часов, для всех остальных - постоянные
        Long fileLinkType = "MORTGAGE_CLPBM".equals(productSysName) ? FILE_LINK_TYPE_HOURS_48 : FILE_LINK_TYPE_PERMANENT;
        for (Map<String, Object> docItem : docList) {
            processDocForUploadUniOpenAPI(docItem, fileLinkType, urlPrefix);
        }
    }

    protected String getParamFromProductDefaultValuesOrCoreSetting(
            String paramSysName, String productSysName, String defaultValue,
            String login, String password
    ) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "getParamFromProductDefaultValuesOrCoreSetting with paramSysName = '%s' and productSysName = '%s'...",
                    paramSysName, productSysName
            ));
        }
        Map<String, Object> defValParams = new HashMap<>();
        defValParams.put(PRODUCT_SYSNAME_PARAMNAME, productSysName);
        defValParams.put("NAME", paramSysName);
        defValParams.put(RETURN_AS_HASH_MAP, true);
        String methodName = "dsB2BProductDefaultValueGetByNameAndProductSysName";
        Map<String, Object> defValResult = callServiceLogged(B2BPOSWS_SERVICE_NAME, methodName, defValParams, login, password);
        String paramValue;
        if (isCallResultOK(defValResult) && defValResult.containsKey("VALUE")) {
            paramValue = getStringParamLogged(defValResult, "VALUE");
        } else {
            paramValue = getCoreSettingBySysName(paramSysName, login, password);
            if (paramValue.isEmpty()) {
                paramValue = defaultValue;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "getParamFromProductDefaultValuesOrCoreSetting with paramSysName = '%s' and productSysName = '%s' returned value '%s'.",
                    paramSysName, productSysName, paramValue
            ));
        }
        return paramValue;
    }

    protected void processDocForUploadUniOpenAPI(Map<String, Object> docItem, Long fileLinkType, String urlPrefix) {
        String filePath = getStringParamLogged(docItem, "FILEPATH");
        docItem.remove("FILEPATH");
        if (filePath.isEmpty()) {
            return;
        }
        String fsId = getStringParamLogged(docItem, "FSID");
        String fsType = (!fsId.isEmpty() && isUseSeaweedFS()) ? Constants.FS_EXTERNAL : Constants.FS_HARDDRIVE;
        String userDocName = getStringParamLogged(docItem, "FILENAME");
        Long binFileId = getLongParamLogged(docItem, "BINFILEID");
        Long prodRepId = getLongParamLogged(docItem, "PRODREPID");
        Long ms = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        map.put(FILE_LINK_CREATE_TIME_MS_PARAMNAME, ms);
        map.put(FILE_LINK_FILE_PATH_PARAMNAME, filePath);
        map.put(FILE_LINK_USER_DOC_NAME_PARAMNAME, userDocName);
        map.put(FILE_LINK_FILE_SYSTEM_ID_PARAMNAME, fsId);
        map.put(FILE_LINK_FILE_SYSTEM_TYPE_PARAMNAME, fsType);
        map.put(FILE_LINK_DATABASE_ID_PARAMNAME, binFileId);
        map.put(FILE_LINK_REPORT_DATABASE_ID_PARAMNAME, prodRepId);
        // тип ссылки
        map.put(FILE_LINK_TYPE_PARAMNAME, fileLinkType);
        //  версия набора сведений для сформированной ссылки
        map.put(FILE_LINK_VERSION_PARAMNAME, 3L);
        JsonMapCrypter jsonMapCrypter = new JsonMapCrypter();
        String jsonStrEncrypted = jsonMapCrypter.crypt(map);
        String url = (urlPrefix.isEmpty()) ? jsonStrEncrypted : String.format("%s?fn=%s", urlPrefix, jsonStrEncrypted);
        docItem.put("URL", url);
    }

    protected String processDocListForUploadZip(List<Map<String, Object>> docList, Map<String, Object> params, String login, String password) throws Exception {
        String encryptString = "";
        List<String> fileNameList = new ArrayList<>();

        if ((params.get("URLPATH") != null) && (params.get("SESSIONIDFORCALL") != null) && (docList != null)) {
            String pathPrefix = params.get("URLPATH").toString() + "?sid=" + params.get("SESSIONIDFORCALL").toString();

            for (Map<String, Object> docItem : docList) {

                if (docItem.get("FILEPATH") != null) {
                    String docPath = docItem.get("FILEPATH").toString();
                    String userDocName = docItem.get("FILENAME").toString();

                    SessionController controller = new B2BFileSessionController();
                    Map<String, Object> sessionParams = new HashMap<>();
                    if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (docItem.get("FSID") != null)) {
                        sessionParams.put(FS_TYPE_PARAMNAME, Constants.FS_EXTERNAL);
                        sessionParams.put(SOME_ID_PARAMNAME, docItem.get("FSID").toString());
                        sessionParams.put(USER_DOCNAME_PARAMNAME, userDocName);
                    } else {
                        File src = new File(docPath);
                        String name = src.getName();
                        docPath = name.replace("\\", "").replace("/", "");
                        sessionParams.put(FS_TYPE_PARAMNAME, Constants.FS_HARDDRIVE);
                        sessionParams.put(SOME_ID_PARAMNAME, docPath);
                        sessionParams.put(USER_DOCNAME_PARAMNAME, userDocName);
                        sessionParams.put(UUID_PARAMNAME, UUID.randomUUID().toString());
                    }
                    String filenameEncript = controller.createSession(sessionParams);
                    fileNameList.add("&fn=" + filenameEncript);
                }
            }
            encryptString = pathPrefix + fileNameList.toString().replaceAll(",", "");
        }

        return encryptString;
    }

    /**
     * получение из CORE_SETTINGS значения конкретного параметра по его системному имени
     */
    protected String getCoreSettingBySysName(String settingSysName, String login, String password) throws Exception {
        logger.debug(String.format("Getting core setting by system name [%s]...", settingSysName));
        Map<String, Object> coreSettingParams = new HashMap<String, Object>();
        coreSettingParams.put("SETTINGSYSNAME", settingSysName);
        coreSettingParams.put(RETURN_AS_HASH_MAP, "TRUE"); // getSysSettingBySysName работает только со строковыми значениями RETURN_AS_HASH_MAP
        String coreSettingValue = "";
        Map<String, Object> coreSetting = this.callService(COREWS_SERVICE_NAME, "getSysSettingBySysName", coreSettingParams, login, password);
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

    protected void copyParamsIfNotNull(Map<String, Object> targetMap, Map<String, Object> sourceMap, String... paramKeyNames) {
        if ((targetMap != null) && (sourceMap != null)) {
            for (String paramKeyName : paramKeyNames) {
                Object paramValue = sourceMap.get(paramKeyName);
                if (paramValue != null) {
                    targetMap.put(paramKeyName, paramValue);
                }
            }
        }
    }

    protected boolean checkIsValueInvalidByRegExp(Object value, String regExp) {
        boolean allowNull = false;
        return !checkIsValueValidByRegExp(value, regExp, allowNull);
    }

    protected boolean checkIsValueValidByRegExp(Object value, String regExp, boolean allowNull) {
        boolean result;
        if (value == null) {
            result = allowNull;
        } else {
            Pattern pattern = Pattern.compile(regExp);
            String checkedString = getStringParam(value);
            Matcher matcher = pattern.matcher(checkedString);
            result = matcher.matches();
            //logger.debug("Проверка значения '" + checkedString + "' по регулярному выражению '" + regExp + "' завершена с результатом '" + result + "'.");
        }
        return result;
    }

    protected boolean checkIsValueInvalidByRegExp(Object value, String regExp, boolean allowNull) {
        return !checkIsValueValidByRegExp(value, regExp, allowNull);
    }

    protected String checkNotEmpty(List<String> errorList, Map<String, Object> sourceMap, String keyName, String error, String valueOwnerNameGenitive) {
        String valueStr = getStringParamLogged(sourceMap, keyName);
        boolean isEmpty = valueStr.isEmpty();
        if (isEmpty) {
            errorList.add(error + " " + valueOwnerNameGenitive);
        }
        return valueStr;
    }

    protected void checkNumber(List<String> errorList, String valueStr, String valueDescription, String valueOwnerNameGenitive, String valueNumberCountStr) {
        String regExp = "\\d{" + valueNumberCountStr + "}";
        boolean isValueInvalid = checkIsValueInvalidByRegExp(valueStr, regExp, false);
        if (isValueInvalid) {
            String symbolsStr;
            switch (valueNumberCountStr) {
                case "1": {
                    symbolsStr = "символ";
                    break;
                }
                case "2":
                case "3":
                case "4": {
                    symbolsStr = "символа";
                    break;
                }
                default: {
                    symbolsStr = "символов";
                }
            }
            errorList.add(String.format(
                    "Сведения %s %s указаны некорректно (ожидаемый формат - только цифры, %s %s)",
                    valueDescription, valueOwnerNameGenitive, valueNumberCountStr, symbolsStr
            ));
        }
    }

    protected void checkNotEmptyAndNumber(List<String> errorList, Map<String, Object> sourceMap, String keyName, String valueOwnerNameGenitive, String emptyErrorPrefix, String valueDescription, String valueNumberCountStr) {
        String valueStr = getStringParamLogged(sourceMap, keyName);
        if (valueStr.isEmpty()) {
            errorList.add(String.format("%s %s", emptyErrorPrefix, valueOwnerNameGenitive));
        } else {
            checkNumber(errorList, valueStr, valueDescription, valueOwnerNameGenitive, valueNumberCountStr);
        }
    }

    protected void checkNumberIfNotEmpty(List<String> errorList, Map<String, Object> sourceMap, String keyName, String valueDescription, String valueOwnerNameGenitive, String valueNumberCountStr) {
        String valueStr = getStringParamLogged(sourceMap, keyName);
        if (!valueStr.isEmpty()) {
            checkNumber(errorList, valueStr, valueDescription, valueOwnerNameGenitive, valueNumberCountStr);
        }
    }

    protected static String getErrorStringFromList(List<String> errorList) {
        String error;
        if (errorList.isEmpty()) {
            error = "";
        } else {
            StringBuilder sbError = new StringBuilder("Заполнены не все обязательные поля!");
            for (String errorItem : errorList) {
                sbError.append(" ").append(errorItem).append(".");
            }
            error = sbError.toString();
        }
        return error;
    }

       protected Map<String, Object> getCurrencyById(Long currencyId, String login, String password) throws Exception {
                Map<String, Object> qparam = new HashMap<String, Object>();
                qparam.put("CurrencyID", currencyId);
                qparam.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> qres = this.callService(REFWS_SERVICE_NAME, "getCurrencyByParams", qparam, login, password);
                return qres;
    }

    // перенесено из B2BContractSBOLCustomFacade
    protected int getAge(Date nowDate, Date birthDate) {
        // FIXME: не правильно
        Calendar dob = Calendar.getInstance();
        dob.setTime(birthDate);
        Calendar today = Calendar.getInstance();
        today.setTime(nowDate);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;

        // TODO: проверить!
        // Calendar calendarStart = Calendar.getInstance();
        // calendarStart.setTime(birthDate);
        //
        // Calendar calendarEnd = Calendar.getInstance();
        // calendarEnd.setTime(new Date());
        //
        // LocalDate start = LocalDate.of(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH) + 1, calendarStart.get(Calendar.DAY_OF_MONTH));
        // LocalDate end = LocalDate.of(calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH) + 1, calendarEnd.get(Calendar.DAY_OF_MONTH));
        //
        // return ChronoUnit.YEARS.between(start, end);
    }

    /**
     * Получение данных справочника
     * в виде hid -> Map
     *
     * @param hbName   имя справочника
     * @param login    логин
     * @param password пароль
     * @return Map
     * @throws Exception
     */
    protected Map<Long, Object> getHBRecordMap(String hbName, String login, String password) throws Exception {
        Map<Long, Object> result = null;

        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("HANDBOOKNAME", hbName);
        hbParams.put(RETURN_LIST_ONLY, true);

        List<Map<String, Object>> hbDataList = callServiceAndGetListFromResultMap(
                B2BPOSWS_SERVICE_NAME, "dsB2BHandbookDataBrowseByHBName", hbParams, login, password
        );

        if (hbDataList != null) {
            result = new HashMap<Long, Object>();
            for (Map<String, Object> item : hbDataList) {
                result.put((Long) item.get("hid"), item);
            }

            loggerDebugPretty(logger, "hbRecordMap", result);
        }

        return result;
    }

    protected class B2BParams extends DefaultedHashMap<String, Object> {

        public B2BParams(Map<String, Object> external) {
            super.putAll(external);
        }

        public Long getLong(String s) {
            return getLongParam(this, s);
        }

        public String getString(String s) {
            return getStringParam(this, s);
        }

        public Boolean getBoolean(String s, Boolean def) {
            return getBooleanParam(this, s, def);
        }

        public BigDecimal getBigDecimal(String s) {
            return getBigDecimalParam((String) this.get(s));
        }

        public BigInteger getBigInteger(String s) {
            return getBigIntegerParam(this.get(s));
        }

        public String getLogin() {
            return getString(WsConstants.LOGIN);
        }

        public String getPassword() {
            return getString(WsConstants.PASSWORD);
        }

        public String getError(String def) {
            if (this.get(ERROR) == null) {
                return def;
            } else {
                return (String) this.get(ERROR);
            }
        }

        public Object result() {
            return this.get(RESULT);
        }

    }

    protected static class B2BResult{

        public static Map<String, Object> ok() {
            Map<String, Object> map = new HashMap<>();
            map.put(RESULT, RET_STATUS_OK);
            return map;
        }

        public static Map<String, Object> ok(Object o) {
            Map<String, Object> map = new HashMap<>();
            map.put(RESULT, o);
            return map;
        }

        public static Map<String, Object> error() {
            Map<String, Object> map = new HashMap<>();
            map.put(ERROR, RET_STATUS_ERROR);
            return map;
        }

        public static Map<String, Object> error(Object o) {
            Map<String, Object> map = new HashMap<>();
            map.put(ERROR, o);
            return map;
        }

        public static Map<String, Object> okIfNotNull(Object o) {
            return okIfTrue(o != null);
        }

        public static Map<String, Object> errorIfNotNull(Object o) {
            return errorIfTrue(o != null);
        }

        public static Map<String, Object> errorIfTrue(Boolean flag) {
            if (flag) {
                return error();
            } else {
                return ok();
            }
        }

        public static Map<String, Object> okIfTrue(Boolean flag) {
            if (flag) {
                return ok();
            } else {
                return error();
            }
        }

    }

    private Integer compareWithNulls(Object param1, Object param2, boolean nullsInTheEnd) {
        if (param1!=null) {
            if (param2!=null) {
                return null;
            }else {
                return new Integer(nullsInTheEnd?-1:1);
            }
        }else {
            if (param2!=null) {
                return new Integer(nullsInTheEnd?1:-1);
            }else {
                return new Integer(0);
            }
        }
    }

    /**
     * Сортировка списка по полю типа "дата"
     * @param list список
     * @param fieldName имя поля
     * @param asc Если да то восходящий порядок, иначе нисходящий
     * @param nullsInTheEnd Если да то обьекты где fieldName null, ложатся в конец, иначе в начало
     */
    public void sortByDateFieldName(List<Map<String, Object>> list, final String fieldName,boolean asc,boolean nullsInTheEnd) {
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                int result = asc?-1:1;
                Date l1 = (Date)o1.get(fieldName);
                Date l2 = (Date)o2.get(fieldName);
                Integer nulls = compareWithNulls(l1, l2,nullsInTheEnd);
                if (nulls!=null) {
                    return nulls.intValue();
                }
                result = asc?l1.compareTo(l2):l2.compareTo(l1);


                return result;
            }
        });
    }

    /**
     * Сортировка списка по полю типа "дата"
     *
     * @param list список
     * @param fieldName имя поля
     */
    public void sortByDateFieldName(List<Map<String, Object>> list, final String fieldName) {
        sortByDateFieldName(list, fieldName,true,true);
            }


    protected boolean stringParamsAreNotNull(String ... args) {

        for (String p : args) {
            if (!(p != null && !p.isEmpty())) return false;
        }
        return true;
    }

    protected Object getParamByRoute(Map<String, Object> origin, String routes, Object def) {
        return getParamByRoute(origin, def, routes.split("\\."));
    }

    protected Object getParamByRoute(Map<String, Object> origin, Object def, String...nodeNames){
        Map<String, Object> internal = new HashMap<>(origin);
        String node;
        for (int i = 0; i < nodeNames.length - 1; i++ ) {
            node = nodeNames[i];
            internal = (Map<String, Object>) internal.get(node);
            if (internal == null) return def;
        }
        return internal.getOrDefault(nodeNames[nodeNames.length - 1], def);
    }

    protected Map<String, Object> copyNodes(Map<String, Object> origin, String nodesList) {
        Map<String, Object> result = new HashMap<>();
        for (String node : nodesList.split("\\,")) {
            Object o = origin.get(node);
            result.put(node, o);
        }
        return result;
    }

    protected static String getParticipantFullName(Map<String, Object> manager) {
        String lastName = getStringParam(manager, "LASTNAME");
        String firstName = getStringParam(manager, "FIRSTNAME");
        String middleName = getStringParam(manager, "MIDDLENAME");
        StringBuilder fullName = new StringBuilder();
        if (!lastName.isEmpty()) {
            fullName.append(lastName);
        }
        if (!firstName.isEmpty()) {
            fullName.append(" ").append(firstName);
        }
        if (!middleName.isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        String fullNameStr = fullName.toString();
        return fullNameStr;
    }

    protected void logJournalSearchParams(Map<String, Object> params) {
        Map<String, Object> filters = (Map<String, Object>) params.get("FILTERPARAMS");
        String jId = getStringParam(params, "JOURNALID");
        if (filters == null) filters = new HashMap<>();
        Map<String, Object> filterParams;
        StringBuilder sb = new StringBuilder("Пользователь '" + params.get(LOGIN).toString() + "', запрос данных журнала " + jId + ": ");
        String conditionKey;
        String conditionOperator;
        String conditionField;
        String conditionValue;
        Map<String, Object> conditionValueMap;
        for (String table : filters.keySet()) {
            filterParams = (Map<String, Object>) filters.get(table);
            if (filterParams == null) continue;
            for (String subConditionKey : filterParams.keySet()){
                try {
                    conditionKey = subConditionKey;
                    conditionOperator = filterConditionOperators.get(conditionKey);
                    conditionValueMap = (Map<String, Object>) filterParams.get(conditionKey);
                    conditionField = (String) conditionValueMap.keySet().toArray()[0];
                    conditionValue = getStringParam(conditionValueMap, conditionField);
                    sb
                            .append(table).append(".")
                            .append(conditionField)
                            .append(conditionOperator)
                            .append(conditionValue)
                            .append(", ");
                } catch (Exception ex) {

                }
            }

        }
        // Логгирование запроса по ДУЛ

        Map<String, Object> externalAliases = (Map<String, Object>) params.get("EXTERNALALIAS");
        if (externalAliases != null && externalAliases.get("3") != null){
            Map<String, Object> externalParams = (Map<String, Object>) externalAliases.get("3");
            sb.append(" парамеры ДУЛ: ");
            for (String s : externalParams.keySet()){
                sb.append(s).append("=").append(externalParams.get(s)).append(", ");
            }
        }

        auditLogger.info(sb.toString());
    }

    protected Map<String, Object> getAnyProductMapFromContract(Map<String, Object> contract) {
        Map<String, Object> productMap = null;
        String[] productMapKeyNames = new String[]{
                "FULLPRODMAP" /* полная мапа продукта также подойдет в качестве обычной мапы */,
                "PRODMAP", "PRODUCTMAP", "PRODCONF"};
        for (String productMapKeyName : productMapKeyNames) {
            productMap = getMapParam(contract, productMapKeyName);
            if (productMap != null) {
                break;
            }
        }
        return productMap;
    }

    protected String getProductSysNameFromContract(Map<String, Object> contract) {
        String productSysName = getStringParamLogged(contract, PRODUCT_SYSNAME_PARAMNAME);
        if (productSysName.isEmpty()) {
            Map<String, Object> product = getAnyProductMapFromContract(contract);
            productSysName = getStringParamLogged(product, PRODUCT_SYSNAME_PARAMNAME);
            if (productSysName.isEmpty()) {
                Map<String, Object> prodMap = getMapParam(product, "PRODMAP");
                productSysName = getStringParamLogged(prodMap, PRODUCT_SYSNAME_PARAMNAME);
            }
        }
        return productSysName;
    }

    /** получение альтернативной серии договора (если предусмотрена для данного способа создания договора) */
    protected String getAltSeriesPrefixIfNeeded(Map<String, Object> contract, Map<String, Object> params, Map<String, Object> prodDefValMap, String defaultSeriesPrefix, String login, String password) {
        logger.debug("getAltSeriesPrefixIfNeeded start...");
        // получение альтернативной серии договора (если предусмотрена для данного способа создания договора)
        String seriesPrefix = "";
        boolean isCallFromUniOpenAPI = isCallFromUniOpenAPI(params);
        if (isCallFromUniOpenAPI) {
            logger.debug("getAltSeriesPrefixIfNeeded isCallFromUniOpenAPI = true.");
            Map<String, Object> subsystemProductParams = getSubsystemProductParamsForContract(contract, login, password);
            seriesPrefix = getStringParamLogged(subsystemProductParams, CONTRACT_SERIES_PREFIX_PRODDEFVAL_NAME);
            if (seriesPrefix.isEmpty()) {
                // старый вариант альтернативной серии, должен использоваться приведенный выше (через getSubsystemProductParamsForContract)
                // todo: отключить по завершению разработки/проверки
                // для OpenAPI серия договора может быть указана в отдельной константе продукта
                seriesPrefix = getStringParamLogged(prodDefValMap, UNIOPENAPI_CONTRACT_SERIES_PREFIX_PRODDEFVAL_NAME);
            }
        }
        if (seriesPrefix.isEmpty()) {
            seriesPrefix = defaultSeriesPrefix;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getAltSeriesPrefixIfNeeded seriesPrefix = '" + seriesPrefix + "'.");
            logger.debug("getAltSeriesPrefixIfNeeded finished.");
        }
        return seriesPrefix;
    }

    protected String getParamFromSubsystemProductHandbookOrProductDefaultValuesOrCoreSetting(
            Map<String, Object> contract, String paramSysName, String defaultValue, String login, String password
    ) throws Exception {
        Map<String, Object> subsystemProductParams = getSubsystemProductParamsForContract(contract, login, password);
        String paramValue = getStringParamLogged(subsystemProductParams, paramSysName);
        if (paramValue.isEmpty()) {
            String productSysName = getProductSysNameFromContract(contract);
            if (!productSysName.isEmpty()) {
                paramValue = getParamFromProductDefaultValuesOrCoreSetting(paramSysName, productSysName, defaultValue, login, password);
            }
        }
        return paramValue;
    }

    protected String getParamFromSubsystemProductHandbookOrProductDefaultValuesOrCoreSetting(
            Map<String, Object> contract, String paramSysName, String defaultValue, StringBuilder error, String login, String password
    ) {
        String errorStr = "";
        String paramValue;
        try {
            paramValue = getParamFromSubsystemProductHandbookOrProductDefaultValuesOrCoreSetting(contract, paramSysName, defaultValue, login, password);
            errorStr = getStringParam(contract, ERROR);
        } catch (Exception ex) {
            logger.error("Unable to getParamFromSubsystemProductHandbookOrProductDefaultValuesOrCoreSetting! See previously logged errors for details!", ex);
            paramValue = null;
            errorStr = "Не удалось получить значение одного из параметров, отвечающих за условные операции! ";
        }
        if (!errorStr.isEmpty()) {
            error.append(errorStr);
        }
        return paramValue;
    }

    protected Map<String, Object> getSubsystemProductParamsForContract(Map<String, Object> contract, String login, String password) {
        Map<String, Object> subsystemProductParams = getMapParamLogged(contract, SUBSYSTEM_PRODUCT_PARAMS_PARAMNAME);
        if (subsystemProductParams == null) {
            Map<String, Object> ssmParams = new HashMap<String, Object>();
            // не все параметры обязательные, должны поддерживатся определенные комбинации (см. dsB2BSubsystemProductParamBrowseByParamsEx)
            copyParamsIfNotNull(
                    ssmParams, contract,
                    PRODCONFID_PARAMNAME, SUBSYSTEM_HBDATAVERID_PARAMNAME, CONTRID_PARAMNAME, SUB_SYSTEM_ID_PARAMNAME
            );
            if (!ssmParams.containsKey(PRODCONFID_PARAMNAME)) {
                // ИД конфигурации может быть доступен в мапе продукта (если такая уже имеется в договоре)
                Map<String, Object> product = getAnyProductMapFromContract(contract);
                Long prodConfId = getLongParamLogged(product, PRODCONFID_PARAMNAME);
                if (prodConfId != null) {
                    ssmParams.put(PRODCONFID_PARAMNAME, prodConfId);
                }
            }
            subsystemProductParams = getSubsystemProductParams(ssmParams, login, password);
            String error = getStringParamLogged(subsystemProductParams, ERROR);
            if (error.isEmpty()) {
                contract.put(SUBSYSTEM_PRODUCT_PARAMS_PARAMNAME, subsystemProductParams);
            } else {
                contract.put(ERROR, error);
            }
        }
        return subsystemProductParams;
    }

    protected Map<String, Object> getSubsystemProductParams(Map<String, Object> ssmParams, String login, String password) {
        String serviceName = B2BPOSWS_SERVICE_NAME;
        String methodName = "dsB2BSubsystemProductParamBrowseByParamsEx";
        Map<String, Object> methodParams = new HashMap<String, Object>();
        // не все параметры обязательные, должны поддерживатся определенные комбинации (см. dsB2BSubsystemProductParamBrowseByParamsEx)
        copyParamsIfNotNull(
                methodParams, ssmParams,
                PRODCONFID_PARAMNAME, SUBSYSTEM_HBDATAVERID_PARAMNAME, CONTRID_PARAMNAME, SUB_SYSTEM_ID_PARAMNAME
        );
        methodParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = null;
        try {
            result = callService(serviceName, methodName, methodParams, logger.isDebugEnabled(), login, password);
        } catch (Exception ex) {
            logger.error("Unable to get subsystem product params! See previously logged errors for details!", ex);
        }
        String error = getStringParamLogged(result, ERROR);
        if (error.isEmpty()) {
            if (isCallResultNotOK(result)) {
                error = "Не удалось получить параметры продукта в зависимости от подсистемы.";
            }
        } else {
            error = "Не удалось получить параметры продукта в зависимости от подсистемы. " + error;
        }
        if (!error.isEmpty()) {
            result = makeErrorResult(error);
        }
        return result;
    }

}

