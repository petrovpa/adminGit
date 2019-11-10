package com.bivgroup.services.b2bposws.facade.pos.custom;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.services.inscore.util.StringCryptUtils;
import ru.diasoft.utils.currency.AmountUtils;
import ru.diasoft.utils.format.number.NumberFormatUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
import java.util.regex.Matcher;
import java.util.regex.Pattern;
*/

/**
 * @author averichevsm
 */
@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("ProductContractCustom")
public class ProductContractCustomFacade extends B2BLifeBaseFacade {

    private static final NumberFormat moneyFormatter = NumberFormat.getNumberInstance(new Locale("ru"));

    private static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;
    protected static final String ADMINWS_SERVICE_NAME = Constants.ADMINWS;
    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    // перенесено в B2BLifeBaseFacade
    /*
    protected static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    protected static final String SIGNB2BPOSWS_SERVICE_NAME = Constants.SIGNB2BPOSWS;
    // protected static final String SIGNB2BPOSWS_SERVICE_NAME = B2BPOSWS_SERVICE_NAME; // !только для отладки!
    protected static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    */
    private final Logger logger = Logger.getLogger(this.getClass());
    private static final String UPDATE_STARTDATE_METHOD = "UPDATE_STARTDATE_METHOD";

    // Наименования (NAME) констант продукта (таблица B2B_PRODDEFVAL) - перенесено в B2BBaseFacade
    //private static final String RISKSUMSHBDATAVERID_PARAMNAME = "RISKSUMSHBDATAVERID";
    //protected static final String MEMBERHBDATAVERID_PARAMNAME = "MEMBERHBDATAVERID"; // Версия справочника атирбутов застрахованного
    private static final long DISCRIMINATOR_SECTION = 6L;
    private static final long DISCRIMINATOR_GROUP = 2L;
    private static final long DISCRIMINATOR_OBJECT = 3L;
    private static final long DISCRIMINATOR_RISK = 4L;

    // системные имена продуктов (B2BPROD.SYSNAME)
    private static final String SYSNAME_VZR = "00018"; // Страхование путешественников Онлайн (по ФТ от 06.10.2015)
    private static final String SYSNAME_INSCOM = "00037"; // Страховое ателье (по ФТ от 17.09.2015)
    private static final String SYSNAME_GAP = "00034"; // GAP Сетелем (по ФТ от 17.09.2015)
    private static final String SYSNAME_MULTI = "00035"; // Мультиполис онлайн (по ФТ от 17.09.2015)
    private static final String SYSNAME_MULTISETELEM = "007";
    private static final String SYSNAME_MULTISETELEMAll = "0991";
    private static final String SYSNAME_MULTISETELEMVZR = "0992";
    private static final String SYSNAME_MORTGAGE = "00029"; // Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России»
    private static final String SYSNAME_CIB = "00010"; // Защита банковской карты Онлайн
    private static final String SYSNAME_CIBY = "B2B_CIB_FOR_YOUTH"; // Защита банковской карты Онлайн для молодежи
    private static final String SYSNAME_CIB900 = "B2B_CIB_900"; // Защита банковской карты Онлайн для молодежи
    private static final String SYSNAME_CIBTM = "B2B_CIB_TM"; // Мобильная защита карты

    private static final String SYSNAME_HIB = "00009"; // Защита дома Онлайн
    private static final String SYSNAME_HIB900 = "B2B_HIB_900"; // Защита дома Онлайн
    private static final String SYSNAME_HIBTM = "B2B_HIB_TM"; // Мобильная защита дома
    private static final String SYSNAME_HIBPREMIUM = "00099"; // Защита дома Онлайн Премиум
    public static final String SYSNAME_MORTGAGE900 = "001"; // Пролонгация ипотеки через SMS 900
    public static final String SYSNAME_MORTGAGETM = "B2B_MORTGAGE_TELEMARKETING"; // Пролонгация ипотеки через ТМ
    public static final String SYSNAME_SIS = "00039"; // Защита всегда рядом: Дом / Страхование имущества сотрудников СБ
    public static final String SYSNAME_ANTIMITE = "B2B_ANTIMITE"; // Защита от клеща Онлайн
    public static final String SYSNAME_SBOL_ANTIMITE_ROS = "B2B_SBOL_ANTIMITE_ROS"; // Защита от клеща (Ростелеком)
    public static final String SYSNAME_BUSINESS_STAB = "B2B_BUSINESS_STAB"; // Стабильный бизнес Онлайн

    // life
    //public static final String SYSNAME_INVEST_NUM1 = "B2B_INVEST_NUM1"; // Инвестиция № 1
    public static final String SYSNAME_INVEST_NUM1 = "LIGHTHOUSE"; // Инвестиция № 1
    //public static final String SYSNAME_INVEST_COUPON = "B2B_INVEST_COUPON"; // Инвестиция № 1
    public static final String SYSNAME_INVEST_COUPON = "SMART_POLICY_RB_ILIK"; // Инвестиция № 1
    //public static final String SYSNAME_CAPITAL = "B2B_CAPITAL"; // Капитал
    public static final String SYSNAME_CAPITAL = "ACCELERATION_RB-FL"; // Капитал
    //public static final String SYSNAME_FIRST_STEP = "B2B_FIRST_STEP"; // Первый шаг
    public static final String SYSNAME_FIRST_STEP = "FIRSTCAPITAL_RB-FCC0"; // Первый шаг
    public static final String SYSNAME_NEW_HORIZONS = "B2B_NEW_HORIZONS"; // новые горизонты Рантье
    //public static final String SYSNAME_RIGHT_DECISION = "B2B_RIGHT_DECISION"; // Верное решение
    public static final String SYSNAME_RIGHT_DECISION = "FAMALYASSETS_RB-FCC0"; // Верное решение
    public static final String SYSNAME_BORROWER_PROTECT = "B2B_BORROWER_PROTECT"; // Защищенный заемщик
    //public static final String SYSNAME_BORROWER_PROTECT_LONGTERM = "B2B_BORROWER_PROTECT_LONGTERM"; // Защищенный заемщик многолетний
    public static final String SYSNAME_BORROWER_PROTECT_LONGTERM = "MORTGAGE_CLPBM"; // Защищенный заемщик многолетний

    public static final String SYSNAME_SMARTPOLICY = "SMART_POLICY"; // СмартПолис (5 или 7 лет)
    public static final String SYSNAME_SMARTPOLICY_LIGHT = "SMART_POLICY_LIGHT"; // СмартПолис Лайт (только 3 года)

    final protected String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final protected byte[] Salt = {
            (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
            (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};

    public static final String[] avaliableTableAliases = {
            "T",
            "T2",
            "T3"
    };
    public static final String[] avaliableFields = {
            "EXPORTDATAID",
            "CREATEDATE",
            "FINISHDATE",
            "TEMPLATEID",
            "STATEID",
            "DATANUMBER",
            "SYSNAME",
            "PUBLICNAME",
            "SYSNAME",
            "CAPTION",
            "ISRECORDDISABLE"

    };

    /**
     * dateFormatter = new SimpleDateFormat("dd.MM.yyyy")
     */
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * dateFormatterMonth = new SimpleDateFormat("«dd» MMMMM yyyy", new Locale("ru")) + .setMonths(MONTHS_FOR_STRING_DATE)
     */
    private SimpleDateFormat dateFormatterMonth;

    // для создания формата дат для русских месяцев в нужном виде (например, "января" вместо "Январь")
    private static final String[] MONTHS_FOR_STRING_DATE = {
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

    //
    /*
    // "словарь" для мужского поля и женского для isMarried = true
    private static final Map<Long, Map<String, Object>> maritalStatusForOpenApiNotMarried;
    // "словарь" для мужского поля и женского для isMarried = false
    private static final Map<Long, Map<String, Object>> maritalStatusForOpenApiMarried;

    static {
        maritalStatusForOpenApiMarried = new HashMap<>();
        Map<String, Object> maleMarriedStatus = new HashMap<>();
        // женат
        maleMarriedStatus.put("EXTATT_SYSNAME", "MaritalStatus");
        maleMarriedStatus.put("EXTATTVAL_VALUE", "MARITAL02");
        maritalStatusForOpenApiMarried.put(0L, maleMarriedStatus);
        Map<String, Object> femaleMarriedStatus = new HashMap<>();
        // замужем
        femaleMarriedStatus.put("EXTATT_SYSNAME", "MaritalStatus");
        femaleMarriedStatus.put("EXTATTVAL_VALUE", "MARITAL04");
        maritalStatusForOpenApiMarried.put(1L, femaleMarriedStatus);

        maritalStatusForOpenApiNotMarried = new HashMap<>();
        maleMarriedStatus = new HashMap<>();
        // холост
        maleMarriedStatus.put("EXTATT_SYSNAME", "MaritalStatus");
        maleMarriedStatus.put("EXTATTVAL_VALUE", "MARITAL01");
        maritalStatusForOpenApiNotMarried.put(0L, maleMarriedStatus);
        femaleMarriedStatus = new HashMap<>();
        // не замужем
        femaleMarriedStatus.put("EXTATT_SYSNAME", "MaritalStatus");
        femaleMarriedStatus.put("EXTATTVAL_VALUE", "MARITAL03");
        maritalStatusForOpenApiNotMarried.put(1L, femaleMarriedStatus);
    }
    */

    public ProductContractCustomFacade() {
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

    protected Long getSellerId(Map<String, Object> userInfo, String login, String password) throws Exception {
        Map<String, Object> sellerParam = new HashMap<String, Object>();
        sellerParam.put("EMPLOYEEID", userInfo.get("EMPLOYEEID"));
        sellerParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> sellerRes = this.callService(INSPOSWS_SERVICE_NAME, "dsEmployeeSellerBrowseListByParam", sellerParam, login, password);
        Long result = null;
        if (sellerRes.get("SELLERID") != null) {
            result = Long.valueOf(sellerRes.get("SELLERID").toString());
        }
        return result;
    }

    // перенесено в HandbookCustomFacade
    //protected void loadOPFList(Map<String, Object> result, Map<String, Object> hbMapIn, String login, String password) throws Exception {
    //    Map<String, Object> param = new HashMap<String, Object>();
    //    param.put("ReferenceName", "Справочник ОПФ");
    //    param.put("ReferenceGroupName", "Справочники клиентской базы");
    //    Map<String, Object> qRes = this.callService(REFWS_SERVICE_NAME, "refItemGetListByParams", param, login, password);
    //    if (qRes.get(RESULT) != null) {
    //        if (qRes.get(RESULT) instanceof List) {
    //            List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
    //            logger.debug(resList);
    //            result.put("OPFList", resList);
    //        }
    //    }
    //}
    protected void loadHandbookList(Map<String, Object> result, Map<String, Object> params, String hbName, int calcVerId, String resListName, String login, String password) throws Exception {
        Map<String, Object> qParam = new HashMap<String, Object>();
        qParam.put("CALCVERID", calcVerId);
        if (params != null) {
            qParam.put("PARAMS", params);
        }
        qParam.put("NAME", hbName);
        Map<String, Object> qRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", qParam, login, password);
        if (qRes.get(RESULT) != null) {
            if (qRes.get(RESULT) instanceof List) {
                List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                logger.debug(resList);
                result.put(resListName, resList);
            }
        }

    }

    private Map<String, Object> saveB2BContractCommon(Map<String, Object> contract, String login, String password) throws Exception {
        Map<String, Object> userInfo = findDepByLogin(login, password);
        // продавца мы не ищем, в B2B не используется старый механизм продавцов (хотя поле в договоре осталось)
        /* Long sellerId = getSellerId(userInfo, login, password);
         contract.put("SELLERID", sellerId); */
        contract.put("ORGSTRUCTID", userInfo.get("DEPARTMENTID"));
        contract.put("SELFORGSTRUCTID", userInfo.get("DEPARTMENTID"));

        if (contract.get("CONTRNUMBER") == null) {
            String contrNum = generateContrNum(getLongParam(contract.get("PRODCONFID")), login, password);
            if (!contrNum.isEmpty()) {
                if ((contract.get("CONTRPOLSER") == null) && (contract.get("CONTRPOLNUM") == null)) {
                    if (contrNum.indexOf("/") >= 0) {
                        String[] cnArr = contrNum.split("/");
                        contract.put("CONTRPOLSER", cnArr[0] + cnArr[1]);
                        contract.put("CONTRPOLNUM", cnArr[2]);
                    } else {
                        contract.put("CONTRPOLSER", contrNum.substring(0, 5));
                        contract.put("CONTRPOLNUM", contrNum.substring(5));
                    }
                    contract.put("CONTRNUMBER", contrNum);
                }
            }
        }

        parseDates(contract, Date.class);

        List<Map<String, Object>> discValList = applyDiscountByPromoAndProd(contract, login, password);
        List<Map<String, Object>> contrList = new ArrayList<Map<String, Object>>();
        contrList.add(contract);
        Map<String, Object> contrParam = new HashMap<String, Object>();
        contrParam.put("CONTRLIST", contrList);
        //boolean isNewContr = contract.get("CONTRID") == null;
        //Map<String, Object> rawResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractUniversalSave", contrParam, login, password);
        Object rawResult = this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BContractUniversalSave", contrParam, login, password, "CONTRLIST");
        if (rawResult != null) {
            Map<String, Object> rawContrMap = (Map<String, Object>) ((List) rawResult).get(0);

            //<editor-fold defaultstate="collapsed" desc="перенесено в dsB2BContractUniversalSave">
            //    if (isNewContr) {
            // установка прав на договор
            //       Long contractId = Long.valueOf(rawContrMap.get("CONTRID").toString());
            //       createContractRights(contractId, contract, login, password);
            //   }
            //</editor-fold>
            // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
            parseDates(rawContrMap, String.class);

            // добавление связей акций с договором
            addDiscRelationsToContract(rawContrMap, discValList, login, password);

            // генерация хеша - требуется для онлайн-продуктов, существующих только в b2b
            if (rawContrMap.get("EXTERNALID") != null) {
                String hash = base64Encode(getStringParam(rawContrMap.get("EXTERNALID")));
                rawContrMap.put("HASH", hash);
            }
            // удаление из результата логина и пароля, если они по какой-то причине по-прежнему в нем остались
            rawContrMap.remove(WsConstants.LOGIN);
            rawContrMap.remove(WsConstants.PASSWORD);

            Map<String, Object> result = rawContrMap;
            logger.debug("CONTRID = " + rawContrMap.get("CONTRID"));
            logger.debug("CONTRNUMBER = " + rawContrMap.get("CONTRNUMBER"));
            logger.debug("EXTERNALID = " + rawContrMap.get("EXTERNALID"));
            logger.debug("HASH = " + rawContrMap.get("HASH") + "\n");
            logger.debug("Сохранение договора успешно заверешно.\n");
            return result;
        }

        logger.debug("Не удалось сохранить договор.\n");
        return null;
    }

    private Map<String, Object> saveB2BContractCommonFixContr(Map<String, Object> contract, String login, String password) throws Exception {
        parseDates(contract, Date.class);
        List<Map<String, Object>> contrList = new ArrayList<Map<String, Object>>();
        contrList.add(contract);
        Map<String, Object> contrParam = new HashMap<String, Object>();
        contrParam.put("CONTRLIST", contrList);
        Object rawResult = this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BContractUniversalSave", contrParam, login, password, "CONTRLIST");
        if (rawResult != null) {
            Map<String, Object> rawContrMap = (Map<String, Object>) ((List) rawResult).get(0);
            // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
            parseDates(rawContrMap, String.class);
            // удаление из результата логина и пароля, если они по какой-то причине по-прежнему в нем остались
            rawContrMap.remove(WsConstants.LOGIN);
            rawContrMap.remove(WsConstants.PASSWORD);

            Map<String, Object> result = rawContrMap;
            logger.debug("CONTRID = " + rawContrMap.get("CONTRID"));
            logger.debug("CONTRNUMBER = " + rawContrMap.get("CONTRNUMBER"));
            logger.debug("EXTERNALID = " + rawContrMap.get("EXTERNALID"));
            logger.debug("HASH = " + rawContrMap.get("HASH") + "\n");
            logger.debug("Сохранение договора успешно заверешно.\n");
            return result;
        }
        logger.debug("Не удалось сохранить договор.\n");
        return null;
    }

    public Map<String, Object> makeErrorResult(String errorText) {
        logger.debug(errorText + "\n");
        HashMap<String, Object> resultWithError = new HashMap<String, Object>();
        resultWithError.put("Error", errorText);
        return resultWithError;
    }

    public Map<String, Object> getProductVersionInfoIfNullBySingleParam(Map<String, Object> productVersionInfo, String paramName, Object paramValue, String login, String password) throws Exception {
        if (productVersionInfo == null) {
            Map<String, Object> versionParams = new HashMap<String, Object>();
            versionParams.put(paramName, paramValue);
            versionParams.put(RETURN_AS_HASH_MAP, true);
            productVersionInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductVersionBrowseListByParamEx", versionParams, login, password);
            return productVersionInfo;
        } else {
            return productVersionInfo;
        }
    }

    private Map<String, Object> getPrepareToSaveMethodName(String productSysName, boolean isMigration) {
        boolean isOnlineProduct = false;
        String prepareToSaveMethodName = null;
        if (SYSNAME_GAP.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BGAPContractPrepareToSave";
        } else if (SYSNAME_MULTI.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BMultiContractPrepareToSave";
            isOnlineProduct = true;
        } else if (SYSNAME_MULTISETELEM.equalsIgnoreCase(productSysName) || SYSNAME_MULTISETELEMVZR.equalsIgnoreCase(productSysName) || SYSNAME_MULTISETELEMAll.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BMultiSetelemContractPrepareToSave";
            isOnlineProduct = true;
        } else if (SYSNAME_INSCOM.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BInsComContractPrepareToSave";
        } else if (SYSNAME_VZR.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = selectPrepareToSaveMethodNameConsideringMirgation("dsB2BVZRContractPrepareToSave", isMigration);
            isOnlineProduct = true;
        } else if (SYSNAME_HIB.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BHIBContractPrepareToSave";
            isOnlineProduct = true;

        } else if (SYSNAME_HIBPREMIUM.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BHIBPremiumContractPrepareToSave";
            isOnlineProduct = true;
        } else if (SYSNAME_CIB.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BCIBContractPrepareToSave";
            isOnlineProduct = true;
        } else if (SYSNAME_CIBY.equalsIgnoreCase(productSysName)) {
            // Защита банковской карты Онлайн для молодежи
            prepareToSaveMethodName = "dsB2BCIBYContractPrepareToSave";
            isOnlineProduct = true;
        } else if (SYSNAME_MORTGAGE.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BMortContractPrepareToSave";
            isOnlineProduct = true;

        } else if (SYSNAME_HIB900.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BHouse900ContractPrepareToSave";
            isOnlineProduct = false;
        } else if (SYSNAME_CIB900.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BCib900ContractPrepareToSave";
            isOnlineProduct = false;
        } else if (SYSNAME_MORTGAGE900.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BMort900ContractPrepareToSave";
            isOnlineProduct = false;
        } else if (SYSNAME_MORTGAGETM.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BMort900ContractPrepareToSave";
            isOnlineProduct = false;
        } else if (SYSNAME_CIBTM.equalsIgnoreCase(productSysName)) {
            // Мобильная защита карты
            prepareToSaveMethodName = "dsB2BCibTMContractPrepareToSave";
            isOnlineProduct = false;
        } else if (SYSNAME_HIBTM.equalsIgnoreCase(productSysName)) {
            // Мобильная защита дома
            prepareToSaveMethodName = "dsB2BHibTMContractPrepareToSave";
            isOnlineProduct = false;
        } else if (SYSNAME_SIS.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = selectPrepareToSaveMethodNameConsideringMirgation("dsB2BSISContractPrepareToSave", isMigration);
            isOnlineProduct = true;
        } else if (SYSNAME_BUSINESS_STAB.equalsIgnoreCase(productSysName)) {
            prepareToSaveMethodName = "dsB2BBusinessStabContractPrepareToSave";
            isOnlineProduct = true;
        } else if (SYSNAME_INVEST_NUM1.equalsIgnoreCase(productSysName)) {
        } else if (SYSNAME_INVEST_COUPON.equalsIgnoreCase(productSysName)) {
            // !только для отладки! - тянется из b2b_prodconf
            // Инвестиция № 1
            //prepareToSaveMethodName = "dsB2BInvestNum1ContractPrepareToSave";
            //isOnlineProduct = false;
        } else if (SYSNAME_SMARTPOLICY.equalsIgnoreCase(productSysName)) {
//            prepareToSaveMethodName = "dsB2BSmartPolicyContractPrepareToSave";
        } else if (SYSNAME_SMARTPOLICY_LIGHT.equalsIgnoreCase(productSysName)) {
//            prepareToSaveMethodName = "dsB2BSmartPolicyLightContractPrepareToSave";
        } else if (SYSNAME_RIGHT_DECISION.equalsIgnoreCase(productSysName)) {
            // !только для отладки! - тянется из b2b_prodconf
            // Верное решение
            //prepareToSaveMethodName = "dsB2BRightDecisionContractPrepareToSave";
            //isOnlineProduct = false;
        } else if (SYSNAME_CAPITAL.equalsIgnoreCase(productSysName)) {
            // !только для отладки! - тянется из b2b_prodconf
            // Капитал
            //prepareToSaveMethodName = "dsB2BCapitalContractPrepareToSave";
            //isOnlineProduct = false;
        } else if (SYSNAME_FIRST_STEP.equalsIgnoreCase(productSysName)) {
            // !только для отладки! - тянется из b2b_prodconf
            // Первый шаг
            prepareToSaveMethodName = "dsB2BFirstStepContractPrepareToSave";
            isOnlineProduct = false;
        } else if (SYSNAME_NEW_HORIZONS.equalsIgnoreCase(productSysName)) {
            // Новые горизонты
            prepareToSaveMethodName = "dsB2BNewHorizonsContractPrepareToSave";
            isOnlineProduct = false;
        } else if ((SYSNAME_ANTIMITE.equalsIgnoreCase(productSysName)) || (SYSNAME_SBOL_ANTIMITE_ROS.equalsIgnoreCase(productSysName))) {
            prepareToSaveMethodName = "dsB2BAntiMiteContractPrepareToSave";
            isOnlineProduct = true;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("prepareToSaveMethodName", prepareToSaveMethodName);
        result.put("isOnlineProduct", isOnlineProduct);
        if (isOnlineProduct) {
            result.put("ISONLINE", 1L);
        } else {
            result.put("ISONLINE", 0L);
        }
        return result;
    }

    private Map<String, Object> getProductVersionInfo(Map<String, Object> rawContract, String login, String password) throws Exception {
        Map<String, Object> productVersionInfo = null;
        Long productVersionID = getLongParam(rawContract.get("PRODVERID"));
        String productSysName = getStringParam(rawContract.get("PRODSYSNAME"));
        if (productVersionID == null) {
            if (productSysName.isEmpty()) {
                return makeErrorResult("Не указаны ни версия (PRODVERID) ни системное имя (PRODSYSNAME) продукта - договор не создан.");
            } else {
                logger.debug("Не указана версия продукта (PRODVERID) - будет определена по переданному системному имени продукта (PRODSYSNAME).\n");
                productVersionInfo = getProductVersionInfoIfNullBySingleParam(productVersionInfo, "PRODSYSNAME", productSysName, login, password);
                productVersionID = getLongParam(productVersionInfo.get("PRODVERID"));
                if (productVersionID == null) {
                    return makeErrorResult("Не удалось определить версию продукта (PRODVERID) по переданному системному имени продукта (PRODSYSNAME) - договор не создан.");
                }
            }
        } else {
            if (!productSysName.isEmpty()) {
                logger.debug("И версия (PRODVERID) и системное имя (PRODSYSNAME) продукта указаны в явном виде - будет выполнена проверка их соответствия по сведениям из БД.\n");
            } else {
                logger.debug("Не указано системное имя продукта (PRODSYSNAME) - будет определено по переданной версии продукта (PRODVERID).\n");
            }
            productVersionInfo = getProductVersionInfoIfNullBySingleParam(productVersionInfo, "PRODVERID", productVersionID, login, password);
            String productSysNameByProdVerID = getStringParam(productVersionInfo.get("PRODSYSNAME"));
            if (productSysName.isEmpty()) {
                if (productSysNameByProdVerID.isEmpty()) {
                    //return makeErrorResult("Не удалось определить системное имя продукта (PRODSYSNAME) по версии продукта (PRODVERID) - возможно, договор будет создан с ошибками.");
                    logger.debug("Не удалось определить системное имя продукта (PRODSYSNAME) по версии продукта (PRODVERID) - возможно, договор будет создан с ошибками.\n");
                } else {
                    productSysName = productSysNameByProdVerID;
                }
            } else if (!productSysName.equalsIgnoreCase(productSysNameByProdVerID)) {
                return makeErrorResult("Указанная версия продукта (PRODVERID) не соответствует переданному системному имени (PRODSYSNAME) - договор не создан.");
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("productVersionInfo", productVersionInfo);
        result.put("productVersionID", productVersionID);
        result.put("productSysName", productSysName);
        return result;
    }

    private String findProdDefValByName(List<Map<String, Object>> prodDefValList, String name) {
        for (Map<String, Object> bean : prodDefValList) {
            if ((bean.get("NAME") != null) && (bean.get("NAME").toString().equalsIgnoreCase(name))) {
                if (bean.get("VALUE") != null) {
                    return bean.get("VALUE").toString();
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private void calcContractStartDate(Long productVersionID, Map<String, Object> rawContract, String login, String password) throws Exception {
        Map<String, Object> prodConfParams = new HashMap<String, Object>();
        prodConfParams.put(RETURN_AS_HASH_MAP, "TRUE");
        prodConfParams.put("PRODVERID", productVersionID);
        Map<String, Object> prodConfRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", prodConfParams, login, password);
        if (prodConfRes.get("PRODCONFID") != null) {
            Map<String, Object> prodDefValParams = new HashMap<String, Object>();
            prodDefValParams.put("PRODCONFID", prodConfRes.get("PRODCONFID"));
            List<Map<String, Object>> prodDefValList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueBrowseListByParam", prodDefValParams, login, password);
            if (prodDefValList != null) {
                String sdCalcMethod = findProdDefValByName(prodDefValList, "SDCALCMETHOD");
                // считаем дату для "С даты заключения договора", "Через % дней с даты заключения договора"
                if ((sdCalcMethod != null) && (sdCalcMethod.equals("2") || sdCalcMethod.equals("4") || sdCalcMethod.equals("6"))) {
                    Map<String, Object> calcParams = new HashMap<String, Object>();
                    calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
                    calcParams.put("DOCUMENTDATE", parseAnyDate(rawContract.get("DOCUMENTDATE"), Double.class, "DOCUMENTDATE"));
                    calcParams.put("SDCALCMETHOD", sdCalcMethod);
                    calcParams.put("SDLAG", findProdDefValByName(prodDefValList, "SDLAG"));
                    calcParams.put("SDCALENDARTYPE", findProdDefValByName(prodDefValList, "SDCALENDARTYPE"));
                    Map<String, Object> qRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractCalcStartDate", calcParams, login, password);
                    if ((qRes != null) && (qRes.get("STARTDATE") != null)) {
                        rawContract.put("STARTDATE", qRes.get("STARTDATE"));
                        updateContractStartDate(rawContract, prodDefValList, login, password);
                    }
                }
            }
        }
    }

    /**
     * сервис сохранения договора по продуктам b2b
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContrSave(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        Map<String, Object> result = doB2BContrSave(params, login, password);
        return result;
    }


    protected Map<String, Object> doB2BContrSave(Map<String, Object> params, String login, String password) throws Exception {
        logger.debug("Сохранение договора...\n");
        /*
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        */
        Map<String, Object> rawRootParams = new HashMap<String, Object>();
        Map<String, Object> rawContract;
        if (params.get("CONTRMAP") != null) {
            rawRootParams.putAll(params);
            rawRootParams.remove("CONTRMAP");
            rawRootParams.remove("Error");
            rawContract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            rawContract = params;
        }
        // определение данных о продукте
        Map<String, Object> prodInfoMap = getProductVersionInfo(rawContract, login, password);
        Map<String, Object> productVersionInfo = (Map<String, Object>) prodInfoMap.get("productVersionInfo");
        Long productVersionID = (Long) prodInfoMap.get("productVersionID");
        String productSysName = prodInfoMap.get("productSysName").toString();
        // принудительное дополнение результата вспомогательными данными (поскольку dsB2BContractUniversalSave их не возвращает): ...
        // ... наименованием продукта (требуется для совместимости с вызовами из старых продуктов, а также на angular-интерфейсах онлайн-продуктов)
        String productName = getStringParam(productVersionInfo, "PRODNAME");
        rawContract.put("PRODNAME", productName);
        rawContract.put("PRODCONFID", getLongParam(productVersionInfo, "PRODCONFID"));
        rawContract.put("PRODUCTNAME", productName);
        // ... системным наименованием продукта
        rawContract.put("PRODSYSNAME", productSysName);
        rawContract.put("PRODUCTSYSNAME", productSysName);
        // ... идентификатором версии продукта
        rawContract.put("PRODVERID", productVersionID);
        if (logger.isDebugEnabled()) {
            logger.debug("Версия продукта (PRODVERID): " + productVersionID + ".");
            logger.debug("Системное имя продукта (PRODSYSNAME): '" + productSysName + "'.");
            logger.debug("Наименование продукта (PRODNAME): '" + productName + "'.\n");
        }
        Map<String, Object> contract = new HashMap<String, Object>();
        contract.putAll(rawRootParams);
        // определяем дату документа, если она не указана
        if (rawContract.get("DOCUMENTDATE") == null) {
            GregorianCalendar documentDateGC = new GregorianCalendar();
            documentDateGC.setTime(new Date());
            documentDateGC.set(Calendar.HOUR_OF_DAY, 0);
            documentDateGC.set(Calendar.MINUTE, 0);
            documentDateGC.set(Calendar.SECOND, 0);
            documentDateGC.set(Calendar.MILLISECOND, 0);
            rawContract.put("DOCUMENTDATE", documentDateGC.getTime());
        }
        // определяем дату начала действия договора (в том случае, если ее можно определить без оплаты)
        if (rawContract.get("STARTDATE") == null) {
            calcContractStartDate(productVersionID, rawContract, login, password);
        }

        // определить метод подготовки параметров и является ли продукт онлайн продуктом
        String prepareToSaveMethodName = getStringParamLogged(productVersionInfo, "PREPARETOSAVEMETHOD");
        Long isOnlineFlag = getLongParamLogged(productVersionInfo, "ISONLINE");
        if (prepareToSaveMethodName.isEmpty() || (isOnlineFlag == null)) {
            // если имя метода подготовки параметров и/или признак онлайн продукта не пришло из версии продукта, то получаем их старым способом.
            Map<String, Object> prpMap = getPrepareToSaveMethodName(productSysName, getBooleanParam(rawContract.get("ISMIGRATION"), false));
            if (prepareToSaveMethodName.isEmpty()) {
                prepareToSaveMethodName = getStringParamLogged(prpMap, "prepareToSaveMethodName");
            }
            if (isOnlineFlag == null) {
                isOnlineFlag = getLongParamLogged(prpMap, "ISONLINE");
            }
        }
        boolean isOnlineProduct = false;
        if (isOnlineFlag.intValue() == 1) {
            isOnlineProduct = true;
        }
        //
        if (!prepareToSaveMethodName.isEmpty()) {
            rawContract.remove("Error");
            rawContract.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> preparedContract = this.callService(B2BPOSWS_SERVICE_NAME, prepareToSaveMethodName, rawContract, login, password);
            Object prepareError = preparedContract.get("Error");
            if (prepareError != null) {
                logger.debug("Договор не сохранен - ошибка при обработке переданных сведений: " + prepareError);
                return preparedContract;
            } else {
                contract.putAll(preparedContract);
            }
        } else {
            // если у нас отсутствует prepareToSaveMethod, то нужно сгенерить вторичные свойства договора
            rawContract = genAdditionalSaveParams(rawContract, login, password);
            contract.putAll(rawContract);
        }
        // условное дополнение договора вспомогательными данными: ...
        // ... идентификатором версии справочника атирбутов застрахованного - из констант продукта (таблица B2B_PRODDEFVAL)
        // (требуется для сохранения застрахованных в таблицу B2B_MEMBER с использованием справочника - например, при работе с продуктом 'Защита от клеща Онлайн')
        updateMemberHandbookVersion(contract, login, password);

        if (isOnlineProduct) {
            // определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения
            logger.debug("Онлайн-продукт: для назначения прав на договор будут использоваться сведения, определенные по данным пользователя, от имени которого вызван метод сохранения.\n");
            updateSessionParamsIfNullByCallingUserCreds(contract, login, password);
        } else {
            logger.debug("Не онлайн-продукт: для назначения прав на договор будут использоваться сведения, переданные в явном виде в параметрах вызова метода сохранения.\n");
        }
        Map<String, Object> result = saveB2BContractCommon(contract, login, password);
        return result;
    }

    private void updateContractStartDate(Map<String, Object> contract, List<Map<String, Object>> prodDefValList, String login, String password) throws Exception {
        String methodName = findProdDefValByName(prodDefValList, UPDATE_STARTDATE_METHOD);
        if (methodName != null && !methodName.isEmpty()) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(RETURN_AS_HASH_MAP, "TRUE");
            params.put("CONTRMAP", contract);
            this.callService(B2BPOSWS_SERVICE_NAME, methodName, params, login, password);
        }
    }

    // перенесено в UOAContractCustomFacade
    /**
     * Метод добавления к документу системного наименогования по граждаству застрахованного
     * Добавлем системное наименование только если documentList не null, не пуст и количество элементов 1
     *
     * @param contract "словарь" договора
     */
    /*
    private void addDocumentTypeSysNameByCitizenshipIfNullOrEmpty(Map<String, Object> contract) {
        Map<String, Object> insurerMap = getMapParam(contract, "INSURERMAP");
        List<Map<String, Object>> documentList = getListParam(insurerMap, "documentList");
        if (documentList != null && !documentList.isEmpty() && documentList.size() == 1) {
            Map<String, Object> document = documentList.get(0);
            String documentSysName = getStringParam(document, "DOCTYPESYSNAME");
            if (documentSysName.isEmpty()) {
                documentSysName = getLongParam(insurerMap, "CITIZENSHIP") == 1 ?
                        "PassportRF" : "ForeignPassport";
                document.put("DOCTYPESYSNAME", documentSysName);
            }
        }
    }
    */

    // перенесено в UOAContractCustomFacade
    /**
     * Метод добавления застрахованому "Семейного положения" по полу
     * и isMarried приходящему со стороних ресурсовю. "Семейного положения"
     * добавлем только если в extAttributeList2 еще нет объекта с системным
     * наименованием "MaritalStatus" и "пол" не равен null
     * не пуст и количество элементов 1
     *
     * @param contract "словарь" договора
     */
    /*
    private void updateMaritalStatusFromInsurer(Map<String, Object> contract) {
        Map<String, Object> insurerMap = getMapParam(contract, "INSURERMAP");
        List<Map<String, Object>> extAttributeList2 = getOrCreateListParam(insurerMap, "extAttributeList2");
        Long gender = getLongParam(insurerMap, "GENDER");
        Map<String, Object> maritalStatus = extAttributeList2.stream().filter(it -> getStringParam("EXTATT_SYSNAME")
                .equalsIgnoreCase("MaritalStatus")).findAny().orElse(null);
        if (maritalStatus == null && gender != null) {
            Map<Long, Map<String, Object>> maritalStatusConstant = getBooleanParam(insurerMap, "ISMARRIED", false) ?
                    maritalStatusForOpenApiMarried : maritalStatusForOpenApiNotMarried;
            extAttributeList2.add(maritalStatusConstant.get(gender));
        }
    }
    */

    /**
     * сервис сохранения договора по продуктам b2b (сохранение для изменения
     * данных в договоре)
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContrSaveFixContr(Map<String, Object> params) throws Exception {
        logger.debug("Сохранение договора (fix contr)...\n");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        //
        Map<String, Object> rawRootParams = new HashMap<String, Object>();
        Map<String, Object> rawContract;
        if (params.get("CONTRMAP") != null) {
            rawRootParams.putAll(params);
            rawRootParams.remove("CONTRMAP");
            rawRootParams.remove("Error");
            rawContract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            rawContract = params;
        }
        // проверяем право пользователя на сохранение договора данным сервисом
        Map<String, Object> roleParams = new HashMap<String, Object>();
        roleParams.put(RETURN_AS_HASH_MAP, "TRUE");
        roleParams.put("USERACCOUNTID", params.get("SESSION_USERACCOUNTID"));
        roleParams.put("ROLESYSNAME", "b2bCorrector");
        Map<String, Object> roleRes = this.callService(Constants.B2BPOSWS, "dsUserRoleBrowseListByParam", roleParams, login, password);
        if ((roleRes == null) || (roleRes.get("USERACCOUNTID") == null)) {
            rawContract.put("Status", "Error");
            rawContract.put("Error", "Недостаточно прав пользователя для выполнения операции!");
            return rawContract;
        }

        Boolean isCorrector = Boolean.FALSE;
        if ((null != roleRes.get("ROLESYSNAME")) && (roleRes.get("ROLESYSNAME").toString().equalsIgnoreCase("b2bCorrector"))) {
            isCorrector = Boolean.TRUE;
            rawContract.put("isCorrector", isCorrector);
        }
        roleParams = new HashMap<String, Object>();
        roleParams.put(RETURN_AS_HASH_MAP, "TRUE");
        roleParams.put("USERACCOUNTID", params.get("SESSION_USERACCOUNTID"));
        roleParams.put("ROLESYSNAME", "b2bCorrector1C");
        roleRes = this.callService(Constants.B2BPOSWS, "dsUserRoleBrowseListByParam", roleParams, login, password);
        if ((null != roleRes.get("ROLESYSNAME")) && (roleRes.get("ROLESYSNAME").toString().equalsIgnoreCase("b2bCorrector1C"))) {
            rawContract.put("b2bCorrector1C", Boolean.TRUE);
        }

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("OBJECTID", rawContract.get("CONTRID"));
        // Если добавится еще тип выгрузки в 1с нужно менять запрос.
        queryParams.put("TYPEID", 3000L);
        // Запрашиваем информацию о выгрузках в 1С
        Map<String, Object> queryResult = this.callService(Constants.B2BPOSWS, "dsB2BExportDataContentBrowseListByParam", queryParams, login, password);
        if ((null != queryResult) && ((null != queryResult.get(RESULT)) && ((List) queryResult.get(RESULT)).size() > 0)) {
            rawContract.put("is1CExported", Boolean.TRUE);
        }
        // определение данных о продукте
        Map<String, Object> prodInfoMap = getProductVersionInfo(rawContract, login, password);
        Map<String, Object> productVersionInfo = (Map<String, Object>) prodInfoMap.get("productVersionInfo");
        Long productVersionID = (Long) prodInfoMap.get("productVersionID");
        String productSysName = prodInfoMap.get("productSysName").toString();
        // принудительное дополнение результата вспомогательными данными (поскольку dsB2BContractUniversalSave их не возвращает): ...
        // ... наименованием продукта (требуется для совместимости с вызовами из старых продуктов, а также на angular-интерфейсах онлайн-продуктов)
        String productName = getStringParam(productVersionInfo.get("PRODNAME"));
        rawContract.put("PRODNAME", productName);
        rawContract.put("PRODUCTNAME", productName);
        // ... системным наименованием продукта
        rawContract.put("PRODSYSNAME", productSysName);
        rawContract.put("PRODUCTSYSNAME", productSysName);
        // ... идентификатором версии продукта
        rawContract.put("PRODVERID", productVersionID);
        if (logger.isDebugEnabled()) {
            logger.debug("Версия продукта (PRODVERID): " + productVersionID + ".");
            logger.debug("Системное имя продукта (PRODSYSNAME): '" + productSysName + "'.");
            logger.debug("Наименование продукта (PRODNAME): '" + productName + "'.\n");
        }
        Map<String, Object> contract = new HashMap<String, Object>();
        contract.putAll(rawRootParams);
        // определить метод подготовки параметров и является ли продукт онлайн продуктом
        Map<String, Object> prpMap = getPrepareToSaveMethodName(productSysName, getBooleanParam(rawContract.get("ISMIGRATION"), false));
        String prepareToSaveMethodName = null;
        if (prpMap.get("prepareToSaveMethodName") != null) {
            prepareToSaveMethodName = prpMap.get("prepareToSaveMethodName").toString();
        }
        //
        if (prepareToSaveMethodName != null) {
            rawContract.remove("Error");
            rawContract.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> preparedContract = this.callService(B2BPOSWS_SERVICE_NAME, prepareToSaveMethodName + "FixContr", rawContract, login, password);
            Object prepareError = preparedContract.get("Error");
            if (prepareError != null) {
                logger.debug("Договор не сохранен - ошибка при обработке переданных сведений: " + prepareError);
                return preparedContract;
            } else {
                contract.putAll(preparedContract);
            }
        } else {
            contract.putAll(rawContract);
        }
        // условное дополнение договора вспомогательными данными: ...
        // ... идентификатором версии справочника атирбутов застрахованного - из констант продукта (таблица B2B_PRODDEFVAL)
        // (требуется для сохранения застрахованных в таблицу B2B_MEMBER с использованием справочника - например, при работе с продуктом 'Защита от клеща Онлайн')
        //updateMemberHandbookVersion(contract, login, password);

        /*if (isOnlineProduct) {
            // определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения
            logger.debug("Онлайн-продукт: для назначения прав на договор будут использоваться сведения, определенные по данным пользователя, от имени которого вызван метод сохранения.\n");
            updateSessionParamsIfNullByCallingUserCreds(contract, login, password);
        } else {
            logger.debug("Не онлайн-продукт: для назначения прав на договор будут использоваться сведения, переданные в явном виде в параметрах вызова метода сохранения.\n");
        }*/
        Map<String, Object> result = saveB2BContractCommonFixContr(contract, login, password);
        return result;
    }

    /**
     * В зависимости от режима сохранения (обычный или в ходе миграции) выбирает
     * имя метода используемого для подготовки данных договора к сохранению.
     *
     * @param rawContract         подготавливаемый договор (содержит признак режима
     *                            сохранения - обычный или в ходе миграции)
     * @param commonMethodName    имя метода для подготовки данных договора к
     *                            сохранению для обычного режима сохранения
     * @param migrationMethodName имя метода для подготовки данных договора к
     *                            сохранению для сохранения в ходе миграции
     * @return имя выбранного метода
     */
    private String selectPrepareToSaveMethodNameConsideringMirgation(Map<String, Object> rawContract, String commonMethodName, String migrationMethodName) {
        String prepareToSaveMethodName;
        // флаг миграции (если выставлен в true, то не будет выполнятся безусловное перевычисление сумм, дат и пр. перед сохранением договора)
        boolean isMigration = getBooleanParam(rawContract.get("ISMIGRATION"), false);
        prepareToSaveMethodName = selectPrepareToSaveMethodNameConsideringMirgation(commonMethodName, migrationMethodName, isMigration);
        return prepareToSaveMethodName;
    }

    /**
     * В зависимости от режима сохранения (обычный или в ходе миграции) выбирает
     * имя метода используемого для подготовки данных договора к сохранению.
     *
     * @param commonMethodName    имя метода для подготовки данных договора к
     *                            сохранению для обычного режима сохранения
     * @param migrationMethodName имя метода для подготовки данных договора к
     *                            сохранению для сохранения в ходе миграции
     * @param isMigration         флаг миграции (признак режима сохранения - обычный или
     *                            в ходе миграции)
     * @return имя выбранного метода
     */
    private String selectPrepareToSaveMethodNameConsideringMirgation(String commonMethodName, String migrationMethodName, boolean isMigration) {
        String prepareToSaveMethodName;
        if (isMigration) {
            logger.debug("Включен режим миграции - безусловное перевычисление сумм, дат и ряда других параметров перед сохранением договора выполнятся не будет.");
            prepareToSaveMethodName = migrationMethodName;
        } else {
            prepareToSaveMethodName = commonMethodName;
        }
        return prepareToSaveMethodName;
    }

    /**
     * В зависимости от режима сохранения (обычный или в ходе миграции) выбирает
     * имя метода используемого для подготовки данных договора к сохранению.
     *
     * @param commonMethodName имя метода для подготовки данных договора к
     *                         сохранению для обычного режима сохранения
     * @param isMigration      флаг миграции (признак режима сохранения - обычный или
     *                         в ходе миграции)
     * @return имя метода для обычного режима сохранения или null для сохранения
     * в ходе миграции
     */
    private String selectPrepareToSaveMethodNameConsideringMirgation(String commonMethodName, boolean isMigration) {
        return selectPrepareToSaveMethodNameConsideringMirgation(commonMethodName, null, isMigration);
    }

    /**
     * В зависимости от режима сохранения (обычный или в ходе миграции) выбирает
     * имя метода используемого для подготовки данных договора к сохранению.
     *
     * @param rawContract      подготавливаемый договор (содержит признак режима
     *                         сохранения - обычный или в ходе миграции)
     * @param commonMethodName имя метода для подготовки данных договора к
     *                         сохранению для обычного режима сохранения
     * @return имя метода для обычного режима сохранения или null для сохранения
     * в ходе миграции
     */
    private String selectPrepareToSaveMethodNameConsideringMirgation(Map<String, Object> rawContract, String commonMethodName) {
        return selectPrepareToSaveMethodNameConsideringMirgation(rawContract, commonMethodName, null);
    }

    // условное дополнение договора идентификатором версии справочника атирбутов застрахованного - из констант продукта (таблица B2B_PRODDEFVAL)
    // (требуется для сохранения застрахованных в таблицу B2B_MEMBER с использованием справочника - например, при работе с продуктом 'Защита от клеща Онлайн')
    private void updateMemberHandbookVersion(Map<String, Object> contract, String login, String password) throws Exception {
        Object memberListObj = contract.get("MEMBERLIST");
        if (memberListObj != null) {
            List<Map<String, Object>> memberList = (List<Map<String, Object>>) memberListObj;
            if (memberList.size() > 0) {
                logger.debug("Insured list (MEMBERLIST) is not empty - getting insured handbook version...");
                logger.debug("Looking for product configuration ID (PRODCONFID) in contract map...");
                Long productConfigID = getLongParam(contract.get("PRODCONFID"));
                if (productConfigID != null) {
                    logger.debug("Product configuration ID (PRODCONFID) from contract map: " + productConfigID);
                    Map<String, Object> productDefValsParams = new HashMap<String, Object>();
                    productDefValsParams.put("PRODCONFID", productConfigID);
                    productDefValsParams.put("NAME", MEMBERHBDATAVERID_PARAMNAME);
                    logger.debug("Product defalut value for insured handbook version params: " + productDefValsParams);
                    Long memberHandbookDataVerID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueBrowseListByParam", productDefValsParams, login, password, "VALUE"));
                    if (memberHandbookDataVerID != null) {
                        logger.debug("Insured handbook version (MEMBERHBDATAVERID/HBDATAVERID): " + memberHandbookDataVerID);
                        contract.put("MEMBERHBDATAVERID", memberHandbookDataVerID);
                        for (Map<String, Object> member : memberList) {
                            member.put("HBDATAVERID", memberHandbookDataVerID);
                        }
                        logger.debug("Insured list (MEMBERLIST) will be processed with handbook support.\n");
                    } else {
                        logger.debug("No insured handbook version found - insured list (MEMBERLIST) will be processed without handbook support.\n");
                    }
                } else {
                    logger.debug("No product configuration ID (PRODCONFID) found in contract map - insured list (MEMBERLIST) will be processed without handbook support.\n");
                }
            }
        }
    }

    // перенесено в B2BLifeBaseFacade
    /*
    // определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения
    protected void updateSessionParamsIfNullByCallingUserCreds(Map<String, Object> contract, String login, String password) throws Exception {
        if ((contract.get(Constants.SESSIONPARAM_USERACCOUNTID) == null) && (contract.get(Constants.SESSIONPARAM_DEPARTMENTID) == null)) {
            Map<String, Object> checkLoginParams = new HashMap<String, Object>();
            checkLoginParams.put("username", XMLUtil.getUserName(login));
            checkLoginParams.put("passwordSha", password);
            Map<String, Object> checkLoginResult = WsUtils.getFirstItemFromResultMap(this.selectQuery("dsCheckLogin", checkLoginParams));
            if (checkLoginResult != null) {
                contract.put(Constants.SESSIONPARAM_USERACCOUNTID, checkLoginResult.get("USERACCOUNTID"));
                contract.put(Constants.SESSIONPARAM_DEPARTMENTID, checkLoginResult.get("DEPARTMENTID"));
            }
        }
    }
    */

    /**
     * Обновляет существующий договор (пока только его основные данные). На
     * данный момент используется только для записи СМС-кода в существующий
     * договор. Не требует полных сведений о договоре, сначала выполняет их
     * запрос по переданным вместе с обновляемыми данными идентификаторам
     * договора.
     *
     * @param params идентификаторы обновляемого договора (достаточно одного
     *               любого) и изменившиеся свойства
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContrUpdate(Map<String, Object> params) throws Exception {

        logger.debug("Обновление основных данных договора...\n");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // идентификатор обновляемого договора
        Long contrID = getLongParam(params.get("CONTRID"));

        if (contrID == null) {
            // запрос идентификатора договора, если вместо него передан внешний идентификатор (guid)
            // (поскольку CONTRID - обязательный параметр для dsB2BContractUpdate)
            Map<String, Object> contractLoadParams = new HashMap<String, Object>();
            contractLoadParams.put("EXTERNALID", params.get("EXTERNALID"));
            // todo: при необходимости - добавить другие параметры, однозначно идентифицирующие обновляемый договор
            Map<String, Object> contractLoadResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParam", contractLoadParams, login, password);
            if (isCallResultOK(contractLoadResult)) {
                List<Map<String, Object>> contractLoadList = WsUtils.getListFromResultMap(contractLoadResult);
                if (contractLoadList.size() == 1) {
                    // определяем идентификатор только если по указанным параметрам получен равно один договор
                    contrID = getLongParam(contractLoadList.get(0));
                }
            }
        }

        Map<String, Object> contractSaveResult;
        if (contrID != null) {
            // параметры для обновления договора
            Map<String, Object> contractUpdateParams = new HashMap<String, Object>();
            contractUpdateParams.putAll(params);
            contractUpdateParams.put("CONTRID", contrID);
            // обновление договора
            contractSaveResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractUpdate", contractUpdateParams, login, password);
            if (isCallResultOK(contractSaveResult)) {
                logger.debug("Обновление основных данных договора завершено.\n");
            } else {
                String errorText = "Обновление основных данных завершено с ошибкой. Подробнее: " + contractSaveResult;
                logger.debug(errorText + "\n");
                contractSaveResult = new HashMap<String, Object>();
                contractSaveResult.put("Error", errorText);
            }
        } else {
            // идентификатор обновляемого договора не определен - протоколирование и возврат ошибки
            logger.debug("Обновление основных данных договора вызвано с параметрами:\n\n" + params + "\n");
            String errorText = "Ошибка при обновлении - по указанным параметрам обновляемый договор не найден.";
            logger.debug(errorText + "\n");
            contractSaveResult = new HashMap<String, Object>();
            contractSaveResult.put("Error", errorText);
        }

        return contractSaveResult;

    }

    private void mPolicySetelemAdditionalLoadChilds(Map<String, Object> contract, String login, String password) throws Exception {
        if (contract.get("CHILDCONTRLIST") != null) {
            List<Map<String, Object>> childcontrList = (List<Map<String, Object>>) contract.get("CHILDCONTRLIST");
            for (Map<String, Object> childcontr : childcontrList) {
                if (childcontr.get("PRODVERID") != null) {
                    Long childProdVerId = getLongParam(childcontr.get("PRODVERID"));
                    Map<String, Object> getProdConfIDParams = new HashMap<String, Object>();
                    getProdConfIDParams.put("PRODVERID", childProdVerId);
                    Long prodConfId = getLongParam(this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductConfigBrowseListByParam", getProdConfIDParams, login, password, "PRODCONFID"));
                    // получение сведений о продукте (по идентификатора конфига продукта) для передачи в dsB2BContractUniversalLoad
                    Map<String, Object> prodParam = new HashMap<String, Object>();
                    prodParam.put("PRODCONFID", prodConfId);
                    prodParam.put("LOADDAMAGECAT", 0);
                    prodParam.put("HIERARCHY", false);
                    Map<String, Object> prodMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", prodParam, login, password);
                    Object prodName = null;
                    String prodSysName = null;
                    if (prodMap.get(RESULT) != null) {
                        prodMap = (Map<String, Object>) prodMap.get(RESULT);
                        // определение имени продукта - потребуется ниже
                        Map<String, Object> prodVer = (Map<String, Object>) prodMap.get("PRODVER");
                        if (prodVer != null) {
                            Map<String, Object> prod = (Map<String, Object>) prodVer.get("PROD");
                            if (prod != null) {
                                prodName = prod.get("NAME");
                                prodSysName = getStringParam(prod.get("SYSNAME"));
                            }
                            if (prodName == null) {
                                prodName = ((Map<String, Object>) prodMap).get("NAME");
                            }
                        }
                    }
                    childcontr.put("PRODCONFID", prodConfId);
                    // ... наименованием продукта (требуется для совместимости с вызовами из старых продуктов, например, в PaymentCustomFacade.dsCallPaymentService)
                    childcontr.put("PRODUCTNAME", prodName);
                    childcontr.put("PRODNAME", prodName);
                    // ... системным наименованием продукта (для определения продукта без использования идентификаторов)
                    childcontr.put("PRODUCTSYSNAME", prodSysName);
                    childcontr.put("PRODSYSNAME", prodSysName);
                    if (childcontr.get("INSOBJGROUPLIST") != null) {
                        List<Map<String, Object>> childInsObjGroupList = (List<Map<String, Object>>) childcontr.get("INSOBJGROUPLIST");
                        for (Map<String, Object> insObjGroup : childInsObjGroupList) {
                            if (insObjGroup.get("OBJLIST") != null) {
                                // отдельное сохранение для мультиполиса адреса страхуемого имущества
                                if ((insObjGroup.get("INSOBJGROUPSYSNAME") != null) && (insObjGroup.get("INSOBJGROUPSYSNAME").equals("multiSetelem.property"))) {
                                    Long addressID = getLongParam(insObjGroup.get("propertyAddress"));
                                    logger.debug("addressID: " + addressID);
                                    if (addressID != null) {
                                        //Long isPropertyRegisterAddress = getLongParam(propertyAddressObjGroup.get("isPropertyRegisterAddress"));
                                        Map<String, Object> addressParams = new HashMap<String, Object>();
                                        addressParams.put("ADDRESSID", addressID);
                                        addressParams.put(RETURN_AS_HASH_MAP, true);
                                        Map<String, Object> propertyAddress = null;
                                        try {
                                            propertyAddress = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAddressBrowseListByParam", addressParams, login, password);
                                        } catch (Exception ex) {
                                            logger.debug("Произошло исключение при вызове метода 'dsB2BAddressBrowseListByParam'. Операция с адресом имущества не выполнена.");
                                        }
                                        if (propertyAddress != null) {
                                            propertyAddress.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                                            insObjGroup.put("propertyAddress", propertyAddress);
                                        }
                                        logger.debug("propertyAddress: " + propertyAddress);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * сервис маппинга данных для универсального сохранения и обратно. и
     * сохранения по продукту Защита грузов.
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContrLoad(Map<String, Object> params) throws Exception {
        logger.debug("Загрузка договора...\n");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> loadParam = new HashMap<String, Object>();

        Object contrId = params.get("CONTRID");
        Map<String, Object> browsedContract = null;
        Object prodVerId = params.get("PRODVERID");
        if (contrId == null) {
            // определение идентификатора договора (если вместо него переданы другие атрибуты договора)
            Map<String, Object> getContrIDParams = new HashMap<String, Object>();
            getContrIDParams.putAll(params);
            getContrIDParams.remove("SESSIONID");
            String hash = getStringParam(params.get("HASH"));
            if (!hash.isEmpty()) {
                // если в числе переданных параметров есть хеш (то есть вызов из angular-интерфейса онлайн-продукта), то определяется EXTERNALID
                String guid = base64Decode(hash);
                getContrIDParams.put("EXTERNALID", guid);
            }
            getContrIDParams.put(RETURN_AS_HASH_MAP, true);
            browsedContract = this.callService(Constants.B2BPOSWS, "dsB2BContractBrowseListByParam", getContrIDParams, login, password);
            contrId = browsedContract.get("CONTRID");
            prodVerId = browsedContract.get("PRODVERID");
            // todo: возможно, исравить dsB2BContractUniversalLoad чтобы он не требовал обязательно идентификатор, но мог принять и другие атрибуты договора (как, например, dsB2BContractBrowseListByParam) ?
        }
        loadParam.put("CONTRID", contrId);
        loadParam.put("LOADCONTRSECTION", params.get("LOADCONTRSECTION"));

        // todo: возможно, перенести получение сведений продукта в dsB2BContractUniversalLoad (в loadContractData), поскольку там снова вызывается dsB2BContractBrowseListByParam еще до того как потребуется PRODUCTMAP ?
        Object prodConfId = params.get("PRODCONFID");
        if (prodConfId == null) {
            // определение идентификатора конфига продукта
            // done: выбрать prod по атрибутам договора
            if (prodVerId == null) {
                if (browsedContract != null) {
                    prodVerId = browsedContract.get("PRODVERID");
                } else {
                    Map<String, Object> getVerConfIDParams = new HashMap<String, Object>();
                    getVerConfIDParams.put("CONTRID", contrId);
                    prodVerId = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BContractBrowseListByParam", getVerConfIDParams, login, password, "PRODVERID");
                }
            }
            Map<String, Object> getProdConfIDParams = new HashMap<String, Object>();
            getProdConfIDParams.put("PRODVERID", prodVerId);
            prodConfId = this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductConfigBrowseListByParam", getProdConfIDParams, login, password, "PRODCONFID");
            // todo: вынести в отдельный xml-запрос определение PRODCONFID для контракта (вместо двух вызовов - dsB2BContractBrowseListByParam и dsB2BProductConfigBrowseListByParam)
        }

        // получение сведений о продукте (по идентификатора конфига продукта) для передачи в dsB2BContractUniversalLoad
        Map<String, Object> prodParam = new HashMap<String, Object>();
        prodParam.put("PRODCONFID", prodConfId);
        prodParam.put("LOADDAMAGECAT", params.get("LOADDAMAGECAT"));
        prodParam.put("HIERARCHY", true);
        Map<String, Object> prodMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", prodParam, login, password);
        Object prodName = null;
        String prodSysName = null;
        if (prodMap.get(RESULT) != null) {
            prodMap = (Map<String, Object>) prodMap.get(RESULT);
            // определение имени продукта - потребуется ниже
            Map<String, Object> prodVer = (Map<String, Object>) prodMap.get("PRODVER");
            if (prodVer != null) {
                Map<String, Object> prod = (Map<String, Object>) prodVer.get("PROD");
                if (prod != null) {
                    prodName = prod.get("NAME");
                    prodSysName = getStringParam(prod.get("SYSNAME"));
                }
                if (prodName == null) {
                    prodName = ((Map<String, Object>) prodMap).get("NAME");
                }
            }
        }

        // проверка на совпадение переданного системного имени продукта с действительным (определенным по договору)
        // (для блокирования загрузки договоров по другим продуктам методами гейта вида dsB2B<Продукт>ContractLoad)
        String productSysNameByProdVerID = prodSysName;
        String productSysNameFromParams = getStringParam(params.get("PRODSYSNAME"));
        if (!productSysNameFromParams.isEmpty()) {
            if (!productSysNameFromParams.equalsIgnoreCase(productSysNameByProdVerID)) {
                HashMap<String, Object> result = new HashMap<String, Object>();
                result.put("Error", "Версия продукта, указанная в договоре (PRODVERID) не соответствует переданному системному имени продукта (PRODSYSNAME) - договор создан не будет.");
                logger.debug(result.get("Error") + "\n");
                return result;
            }
        }

        loadParam.put("PRODUCTMAP", prodMap);
        loadParam.put(RETURN_AS_HASH_MAP, params.get(RETURN_AS_HASH_MAP));
        Map<String, Object> loadRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractUniversalLoad", loadParam, login, password);

        if (loadRes != null) {

            // управляемое ключом TARGETDATEFORMAT преобразование обратно в даты для совместимости с вызовами из старых продуктов (их методы могут ожидать даты в результатах)
            if ("DATE".equalsIgnoreCase(getStringParam(params.get("TARGETDATEFORMAT")))) {
                parseDates(loadRes, Date.class);
            }

            // принудительное дополнение результата вспомогательными данными (поскольку dsB2BContractUniversalLoad их не возвращает): ...
            // todo: возможно, перенести получение сведений продукта в dsB2BContractUniversalLoad (в loadContractData) ?
            Map<String, Object> contract = (Map<String, Object>) (loadRes.get(RESULT));
            // ... идентификатором конфигурации продукта (требуется для совместимости с вызовами из старых продуктов)
            contract.put("PRODCONFID", prodConfId);
            // ... наименованием продукта (требуется для совместимости с вызовами из старых продуктов, например, в PaymentCustomFacade.dsCallPaymentService)
            contract.put("PRODUCTNAME", prodName);
            contract.put("PRODNAME", prodName);
            // ... системным наименованием продукта (для определения продукта без использования идентификаторов)
            contract.put("PRODUCTSYSNAME", prodSysName);
            contract.put("PRODSYSNAME", prodSysName);
            // Велосипед для мультипродукта мультиполис сетелем.
            if (SYSNAME_MULTISETELEM.equalsIgnoreCase(prodSysName)) {
                mPolicySetelemAdditionalLoadChilds(contract, login, password);
            }

            // проверка выгружен договор уже в 1с или нет.
            contract.put("ISEXPORT1C", checkIsContractExportIn1C(getLongParam(contract.get("CONTRID")), login, password));

            // отдельная загрузка адреса страхуемого имущества (только для мультиполиса)
            // todo - done: возможно, перенести в отдельный метод фасада MultiCustomFacade
            /*if (SYSNAME_MULTI.equalsIgnoreCase(prodSysName)) {
                List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
                if (insObjGroupList != null) {
                    Map<String, Object> propertyAddressObjGroup = null;
                    for (Map<String, Object> insObjGroup : insObjGroupList) {
                        if ("property".equalsIgnoreCase(getStringParam(insObjGroup.get("INSOBJGROUPSYSNAME")))) {
                            propertyAddressObjGroup = insObjGroup;
                            break;
                        }
                    }
                    if (propertyAddressObjGroup != null) {
                        Long addressID = getLongParam(propertyAddressObjGroup.get("propertyAddress"));
                        logger.debug("addressID: " + addressID);
                        if (addressID != null) {
                            //Long isPropertyRegisterAddress = getLongParam(propertyAddressObjGroup.get("isPropertyRegisterAddress"));
                            Map<String, Object> addressParams = new HashMap<String, Object>();
                            addressParams.put("ADDRESSID", addressID);
                            addressParams.put(RETURN_AS_HASH_MAP, true);
                            Map<String, Object> propertyAddress = null;
                            try {
                                propertyAddress = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAddressBrowseListByParam", addressParams, login, password);
                            } catch (Exception ex) {
                                logger.debug("Произошло исключение при вызове метода 'dsB2BAddressBrowseListByParam'. Операция с адресом имущества не выполнена.");
                            }
                            if (propertyAddress != null) {
                                propertyAddress.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                                propertyAddressObjGroup.put("propertyAddress", propertyAddress);
                            }
                            logger.debug("propertyAddress: " + propertyAddress);
                        }
                    }
                }
            }*/
            logger.debug("Загрузка договора успешно заверешна.\n");
            return loadRes;
        }
        // получить продукт по договору. вызвать метод универсальной загрузки
        logger.debug("Не удалось загрузить договор.\n");
        return null;
    }

    /**
     * сервис подготовки данных для отчетов. базовая загрузка данных договора,
     * без доп преобразований.
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID"})
    public Map<String, Object> dsB2BBasePrintDocDataProvider(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        if (params.get("CONTRID") != null) {
            // загрузка структуры продукта
            if (params.get("PRODCONFID") != null) {
                Long prodConfId = Long.valueOf(params.get("PRODCONFID").toString());
                Map<String, Object> prodParam = new HashMap<String, Object>();
                prodParam.put("PRODCONFID", prodConfId);
                prodParam.put("HIERARCHY", true);
                prodParam.put("LOADALLDATA", params.get("LOADALLDATA"));
                Map<String, Object> prodMap = this.callServiceTimeLogged(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", prodParam, login, password);
                if (prodMap.get(RESULT) != null) {
                    prodMap = (Map<String, Object>) prodMap.get(RESULT);
                    Map<String, Object> contrParam = new HashMap<String, Object>();
                    contrParam.put("CONTRID", params.get("CONTRID"));
                    contrParam.put("PRODUCTMAP", prodMap);
                    result = this.callServiceTimeLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractUniversalLoad", contrParam, login, password);
                }
            }
        }
        return result;
    }

    protected Map<String, Object> createContractRights(Long contractId, Map<String, Object> params, String login, String password) throws Exception {

        logger.debug("Назначение прав для созданного договора...");

        String rightsMethodName;
        Map<String, Object> rightsParams = new HashMap<String, Object>();
        rightsParams.put("CONTRID", contractId);
        rightsParams.put("USERACCOUNTID", params.get(Constants.SESSIONPARAM_USERACCOUNTID));
        rightsParams.put("UPDATETEXT", "Создан договор");

        if (params.get(DEPARTMENTS_KEY_NAME) != null) {
            rightsParams.put("ORGSTRUCTNAMESLIST", params.get(DEPARTMENTS_KEY_NAME));
            rightsMethodName = "dsB2BContractCreateOrgStructsByNamesAndHist";
        } else {
            rightsParams.put("ORGSTRUCTID", params.get(Constants.SESSIONPARAM_DEPARTMENTID));
            rightsMethodName = "dsB2BContractCreateOrgStructAndHist";
        }
        Map<String, Object> result = null;
        try {
            result = this.callService(Constants.B2BPOSWS, rightsMethodName, rightsParams, login, password);
            if (isCallResultOK(result)) {
                logger.debug("Назначение прав для созданного договора завершено без возникновения ошибок.");
            } else {
                logger.debug("При назначении прав для созданного договора возникла ошибка: " + result);
            }
        } catch (Exception e) {
            logger.debug("Произошло исключение при назначении прав для созданного договора:", e);
        }
        return result;
    }

    private Map<String, Object> getProdStructBySysName(List<Map<String, Object>> prodStructs, String sysNameKey, String sysName) {
        if ((prodStructs != null) && (!sysName.isEmpty())) {
            for (Map<String, Object> prodStruct : prodStructs) {
                String prodStructSysName = getStringParam(prodStruct.get(sysNameKey));
                if (sysName.equalsIgnoreCase(prodStructSysName)) {
                    return prodStruct;
                }
            }
        }
        return null;
    }

    private List<Map<String, Object>> filterProdStructs(List<Map<String, Object>> prodStructs, Long parentStructId, Long discriminator) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (prodStructs != null) {
            for (Map<String, Object> bean : prodStructs) {
                boolean fAdd = true;
                if (parentStructId != null) {
                    if ((bean.get("PARENTSTRUCTID") == null) || (Long.valueOf(bean.get("PARENTSTRUCTID").toString()).longValue() != parentStructId.longValue())) {
                        fAdd = false;
                    }
                }
                if (discriminator != null) {
                    if ((bean.get("DISCRIMINATOR") == null) || (Long.valueOf(bean.get("DISCRIMINATOR").toString()).longValue() != discriminator.longValue())) {
                        fAdd = false;
                    }
                }
                if (fAdd) {
                    result.add(bean);
                }
            }
        }
        return result;
    }

    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> dsB2BUpdateContractInsuranceProductStructure(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> b2bContract = (Map<String, Object>) params.get("CONTRMAP");
        Map<String, Object> product = getProductMapFromContractMapOrLoadFromDB(b2bContract, login, password);

        String programCode = "";
        Map<String, Object> contractExtMap = (Map<String, Object>) b2bContract.get("CONTREXTMAP");
        if (contractExtMap != null) {
            programCode = getStringParam(contractExtMap.get("insuranceProgram"));

            // код программы не попал в расширенные атрибуты.
            //получим его по ид программы.
            Long prodProgId = getLongParam(b2bContract.get("PRODPROGID"));
            if (prodProgId != null) {
                if (product.get("PRODVER") != null) {
                    Map<String, Object> prodVerMap = (Map<String, Object>) product.get("PRODVER");
                    if (prodVerMap.get("PRODPROGS") != null) {
                        List<Map<String, Object>> prodProgList = (List<Map<String, Object>>) prodVerMap.get("PRODPROGS");
                        CopyUtils.sortByLongFieldName(prodProgList, "PRODPROGID");
                        List<Map<String, Object>> prodProgListFiltered = CopyUtils.filterSortedListByLongFieldName(prodProgList, "PRODPROGID", prodProgId);
                        if (!prodProgListFiltered.isEmpty()) {
                            if (prodProgListFiltered.size() == 1) {
                                if (programCode.isEmpty()) {
                                    programCode = getStringParam(prodProgListFiltered.get(0).get("PROGCODE"));
                                    contractExtMap.put("insuranceProgram", programCode);
                                }
                                // безусловно. если в программе страхования есть суммы и премии. проставляем их в договор.
                                // ибо иначе суммы пришедшие с js уйдут в договор.
                                if (prodProgListFiltered.get(0).get("INSAMVALUE") != null) {
                                    b2bContract.put("INSAMVALUE", prodProgListFiltered.get(0).get("INSAMVALUE"));
                                }
                                if (prodProgListFiltered.get(0).get("PREMVALUE") != null) {
                                    b2bContract.put("PREMVALUE", prodProgListFiltered.get(0).get("PREMVALUE"));
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.debug("alarm!!! " + contractExtMap.toString());
        boolean isMissingStructsCreated = getBooleanParam(params.get("ISMISSINGSTRUCTSCREATED"), true);
        if ((programCode == null) || programCode.isEmpty()) {
            // ошибка алярм попытка хака для продуктов с программами страхования дом карта. и т.д.
            // для продуктов без программ - с вводом суммы страхования на интерфейсе - это норм.
            logger.debug("alarm!!! programCode is empty");
            // throw new Exception("programCode is emtpy in contract save");
        }
        boolean isCreateSections = getBooleanParam(params.get("ISCREATESECTIONS"), false);
        logger.debug("ISCREATESECTIONS = " + isCreateSections);
        updateContractInsuranceProductStructure(b2bContract, product, isCreateSections, programCode, isMissingStructsCreated, login, password);
        return b2bContract;
    }

    /**
     * Обновляет структуру страхового продукта договора (всё от INSOBJGROUPLIST
     * и ниже) на основании сведений о страховом продукте.
     * <p>
     * При необходимости добавляет отсутствующие типы/объекты/риски. Если
     * существует связанный с продуктом справочник, хранящий значения сумм по
     * рискам для выбранной программы страхования - то по его данным
     * дополнительно будут обновлены суммы рисков (в CONTRRISKLIST). Если
     * существует связанный с продуктом справочник, хранящий значения
     * показателей для выбранной программы страхования - то по его данным
     * дополнительно будут обновлены показатели договора (CONTREXTMAP).
     *
     * @param b2bContract      обновляемый договор
     * @param product          полные сведения о страховом продукте
     * @param isCreateSections создавать секции по договору
     * @param programCode      код выбранной программы страхования (необязательно)
     * @param login            логин для вызова других методов веб-сервисов
     * @param password         пароль для вызова других методов веб-сервисов
     * @throws Exception
     */
    protected void updateContractInsuranceProductStructure(Map<String, Object> b2bContract, Map<String, Object> product, boolean isCreateSections, String programCode, String login, String password) throws Exception {
        boolean isMissingStructsCreated = true;
        updateContractInsuranceProductStructure(b2bContract, product, isCreateSections, programCode, isMissingStructsCreated, login, password);
    }

    /**
     * Обновляет структуру страхового продукта договора на основании сведений о
     * страховом продукте.
     * <p>
     * Eсли выставлен соотвтетсвтующий флаг - то при необходимости добавляет
     * отсутствующие секции/группы/объекты/риски. Если существует связанный с
     * продуктом справочник, хранящий значения сумм по рискам для выбранной
     * программы страхования - то по его данным дополнительно будут обновлены
     * суммы рисков (в CONTRRISKLIST). Если существует связанный с продуктом
     * справочник, хранящий значения показателей для выбранной программы
     * страхования - то по его данным дополнительно будут обновлены показатели
     * договора (CONTREXTMAP).
     *
     * @param b2bContract             обновляемый договор
     * @param product                 полные сведения о страховом продукте
     * @param programCode             код выбранной программы страхования (необязательно)
     * @param isMissingStructsCreated флаг создания недостающих элементов
     *                                структуры страхового продукта договора
     * @param isCreateSections        флаг создания секций в структуре договора (иначе
     *                                работает по старому, верхний элемент в договоре получается группа
     *                                объектов)
     * @param login                   логин для вызова других методов веб-сервисов
     * @param password                пароль для вызова других методов веб-сервисов
     * @throws Exception
     */
    protected void updateContractInsuranceProductStructure(Map<String, Object> b2bContract, Map<String, Object> product, boolean isCreateSections, String programCode, boolean isMissingStructsCreated, String login, String password) throws Exception {
        Map<String, Object> prodVerMap = (Map<String, Object>) product.get("PRODVER");
        if (prodVerMap != null) {
            // получение полного списка элементов структуры из сведений о продукте
            List<Map<String, Object>> prodStructs = (List<Map<String, Object>>) prodVerMap.get("PRODSTRUCTS");
            if (prodStructs != null) {
                Long productConfigId = Long.valueOf(product.get("PRODCONFID").toString());
                Map<String, Object> hbRes = getRiskSumsAndContrExtValuesFromHandbook(productConfigId, programCode, login, password);
                Double contractPremValue = null;
                if (isCreateSections) {
                    // список секций - выбор (если уже существует в договоре) или создание нового
                    List<Map<String, Object>> sectionList = (List<Map<String, Object>>) b2bContract.get("CONTRSECTIONLIST");
                    Map<String, Map<String, Object>> sectionListAsMapBySysName; // объект для быстрого доступа к элементам списка по системному имени
                    if (sectionList == null) {
                        sectionList = new ArrayList<Map<String, Object>>();
                        b2bContract.put("CONTRSECTIONLIST", sectionList);
                        sectionListAsMapBySysName = new HashMap<String, Map<String, Object>>();
                    } else {
                        sectionListAsMapBySysName = getListAsMapAndFixSysNames(sectionList, "CONTRSECTIONSYSNAME");
                    }
                    // формирование полного списока секций - обновление (уже существующих элементов) сведениями из продукта или дополнение списка новыми (недостающими) элементами
                    List<Map<String, Object>> sectionProdStructs = filterProdStructs(prodStructs, null, DISCRIMINATOR_SECTION);
                    for (Map<String, Object> sectionBean : sectionProdStructs) {
                        String sectionSysName = getStringParam(sectionBean.get("SYSNAME"));
                        Map<String, Object> sectionForContract = sectionListAsMapBySysName.get(sectionSysName);
                        if (sectionForContract == null) {
                            if (isMissingStructsCreated) {
                                sectionForContract = new HashMap<String, Object>();
                                sectionForContract.put("CONTRSECTIONSYSNAME", sectionSysName);
                                sectionForContract.put("INSAMCURRENCYID", b2bContract.get("INSAMCURRENCYID"));
                                sectionForContract.put("INSAMVALUE", b2bContract.get("INSAMVALUE"));
                                sectionForContract.put("PREMCURRENCYID", b2bContract.get("PREMCURRENCYID"));
                                sectionForContract.put("PREMVALUE", b2bContract.get("PREMVALUE"));
                                sectionForContract.put("STARTDATE", b2bContract.get("STARTDATE"));
                                sectionForContract.put("FINISHDATE", b2bContract.get("FINISHDATE"));
                                sectionList.add(sectionForContract);
                                sectionListAsMapBySysName.put(sectionSysName, sectionForContract);
                            } else {
                                continue;
                            }
                        } else {
                            Object currentRowStatus = sectionForContract.get(ROWSTATUS_PARAM_NAME);
                            if ((currentRowStatus != null) && (UNMODIFIED_ID == getIntegerParam(currentRowStatus))) {
                                sectionForContract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                            }
                        }
                        sectionForContract.put("PRODSTRUCTID", sectionBean.get("PRODSTRUCTID"));
                        Map<String, Object> contrSectionExtMap;
                        if (sectionForContract.get("CONTRSECTIONEXTMAP") != null) {
                            contrSectionExtMap = (Map<String, Object>) sectionForContract.get("CONTRSECTIONEXTMAP");
                        } else {
                            contrSectionExtMap = new HashMap<String, Object>();
                            sectionForContract.put("CONTRSECTIONEXTMAP", contrSectionExtMap);
                        }
                        contrSectionExtMap.put("HBDATAVERID", sectionBean.get("HBDATAVERID"));
                    }
                    //
                    for (Map<String, Object> sectionBean : sectionList) {
                        Long sectionStructId = Long.valueOf(sectionBean.get("PRODSTRUCTID").toString());
                        Map<String, Object> updRes = updateContractInsuranceProductStructureFromInsObjGroupList(prodStructs, sectionBean, product, hbRes, sectionStructId,
                                programCode, isMissingStructsCreated, login, password);
                        if (updRes.get("contractPremValue") != null) {
                            sectionBean.put("PREMVALUE", Double.valueOf(updRes.get("contractPremValue").toString()));
                            if (contractPremValue == null) {
                                contractPremValue = 0.0;
                            }
                            contractPremValue += Double.valueOf(updRes.get("contractPremValue").toString());
                        }
                    }
                } else {
                    Map<String, Object> updRes = updateContractInsuranceProductStructureFromInsObjGroupList(prodStructs, b2bContract, product, hbRes, null,
                            programCode, isMissingStructsCreated, login, password);
                    if (updRes.get("contractPremValue") != null) {
                        contractPremValue = Double.valueOf(updRes.get("contractPremValue").toString());
                    }
                }
                // если задан справочник сумм, пытаемся из него загрузить показатели по договору
                updateContractExtValues(b2bContract, hbRes, product, contractPremValue);
            }
        }
    }

    /*
     Аналогична updateContractInsuranceProductStructure, тока обновляется уже с групп объектов,
     при этом parentEntity это либо сам договор, либо его секция (для новых продуктов с секциями)
     */
    protected Map<String, Object> updateContractInsuranceProductStructureFromInsObjGroupList(List<Map<String, Object>> prodStructs,
                                                                                             Map<String, Object> parentEntity, Map<String, Object> product, Map<String, Object> hbRes, Long insObjGroupParentStructId,
                                                                                             String programCode, boolean isMissingStructsCreated, String login, String password) throws Exception {
        // список групп объектов - выбор (если уже существует в договоре) или создание нового
        List<Map<String, Object>> groupList = (List<Map<String, Object>>) parentEntity.get("INSOBJGROUPLIST");
        Map<String, Map<String, Object>> groupListAsMapBySysName; // объект для быстрого доступа к элементам списка по системному имени
        if (groupList == null) {
            groupList = new ArrayList<Map<String, Object>>();
            parentEntity.put("INSOBJGROUPLIST", groupList);
            groupListAsMapBySysName = new HashMap<String, Map<String, Object>>();
        } else {
            groupListAsMapBySysName = getListAsMapAndFixSysNames(groupList, "INSOBJGROUPSYSNAME");
        }
        // формирование полного списока типов объектов - обновление (уже существующих элементов) сведениями из продукта или дополнение списка новыми (недостающими) элементами
        List<Map<String, Object>> groupProdStructs = filterProdStructs(prodStructs, insObjGroupParentStructId, DISCRIMINATOR_GROUP);
        for (Map<String, Object> groupBean : groupProdStructs) {
            String groupSysName = getStringParam(groupBean.get("SYSNAME"));
            Map<String, Object> groupForContract = groupListAsMapBySysName.get(groupSysName);
            if (groupForContract == null) {
                if (isMissingStructsCreated) {
                    groupForContract = new HashMap<String, Object>();
                    groupForContract.put("INSOBJGROUPSYSNAME", groupSysName);
                    groupList.add(groupForContract);
                    groupListAsMapBySysName.put(groupSysName, groupForContract);
                } else {
                    continue;
                }
            } else {
                Object currentRowStatus = groupForContract.get(ROWSTATUS_PARAM_NAME);
                if ((currentRowStatus != null) && (UNMODIFIED_ID == getIntegerParam(currentRowStatus))) {
                    groupForContract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                }
            }
            groupForContract.put("PRODSTRUCTID", groupBean.get("PRODSTRUCTID"));
            groupForContract.put("HBDATAVERID", groupBean.get("HBDATAVERID"));
        }
        //
        Double contractPremValue = null;
        if (hbRes != null) {
            contractPremValue = 0.0;
        }
        for (Map<String, Object> groupBean : groupList) {
            Long groupStructID = getLongParam(groupBean.get("PRODSTRUCTID"));
            List<Map<String, Object>> objectProdStructs = filterProdStructs(prodStructs, groupStructID, DISCRIMINATOR_OBJECT);
            if ((objectProdStructs != null) && (objectProdStructs.size() > 0)) {
                // список объектов страхования - выбор (если уже существует в элементе списка типов) или создание нового
                List<Map<String, Object>> objList = (List<Map<String, Object>>) groupBean.get("OBJLIST");
                Map<String, Map<String, Object>> objListAsMapBySysName; // объект для быстрого доступа к элементам списка по системному имени
                if (objList == null) {
                    objList = new ArrayList<Map<String, Object>>();
                    groupBean.put("OBJLIST", objList);
                    objListAsMapBySysName = new HashMap<String, Map<String, Object>>();
                } else {
                    objListAsMapBySysName = getListAsMapAndFixSysNames(objList, "INSOBJSYSNAME", "INSOBJMAP");
                }
                for (Map<String, Object> objBean : objectProdStructs) {
                    String objProdSructSysName = getStringParam(objBean.get("SYSNAME"));
                    // объект страхования - выбор (если уже существует в договоре) через объект для быстрого доступа к элементам списка по системному имени ...
                    Map<String, Object> objectForContract = objListAsMapBySysName.get(objProdSructSysName);
                    // ... или создание нового
                    if (objectForContract == null) {
                        if (isMissingStructsCreated) {
                            objectForContract = new HashMap<String, Object>();
                            objList.add(objectForContract);
                            objListAsMapBySysName.put(objProdSructSysName, objectForContract); // для быстрого доступа к элементам списка по системному имени
                        } else {
                            continue;
                        }
                    } else {
                        Object currentRowStatus = objectForContract.get(ROWSTATUS_PARAM_NAME);
                        if ((currentRowStatus != null) && (UNMODIFIED_ID == getIntegerParam(currentRowStatus))) {
                            objectForContract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                        }
                    }
                    // обновление (или создание если не существует) INSOBJMAP
                    Map<String, Object> insObjMap = (Map<String, Object>) objectForContract.get("INSOBJMAP");
                    if (insObjMap == null) {
                        insObjMap = new HashMap<String, Object>();
                        insObjMap.put("INSOBJSYSNAME", objProdSructSysName);
                        objectForContract.put("INSOBJMAP", insObjMap);
                    }
                    insObjMap.put("PRODSTRUCTID", objBean.get("PRODSTRUCTID"));
                    insObjMap.put("INSOBJSYSNAME", objBean.get("SYSNAME"));
                    insObjMap.put("HBDATAVERID", objBean.get("HBDATAVERID"));
                    // обновление (или создание если не существует) CONTROBJMAP
                    Map<String, Object> contrObjMap = (Map<String, Object>) objectForContract.get("CONTROBJMAP");
                    if (contrObjMap == null) {
                        contrObjMap = new HashMap<String, Object>();
                        objectForContract.put("CONTROBJMAP", contrObjMap);
                    }
                    contrObjMap.put("CURRENCYID", parentEntity.get("INSAMCURRENCYID"));
                    contrObjMap.put("DURATION", parentEntity.get("DURATION"));
                    contrObjMap.put("STARTDATE", parentEntity.get("STARTDATE"));
                    contrObjMap.put("FINISHDATE", parentEntity.get("FINISHDATE"));
                    contrObjMap.put("PREMCURRENCYID", parentEntity.get("PREMCURRENCYID"));
                    // определение сумм, указанных в самом договоре
                    Double contrObjPremValue = 0.0;
                    Double contrObjInsAmValue = 0.0;
                    if (hbRes == null) {
                        if (parentEntity.get("PREMVALUE") != null) {
                            contrObjPremValue = Double.valueOf(parentEntity.get("PREMVALUE").toString());
                        }
                        if (parentEntity.get("INSAMVALUE") != null) {
                            contrObjInsAmValue = Double.valueOf(parentEntity.get("INSAMVALUE").toString());
                        }
                    }
                    //
                    Long objProdStructId = Long.valueOf(objBean.get("PRODSTRUCTID").toString());
                    List<Map<String, Object>> riskProdStructs = filterProdStructs(prodStructs, objProdStructId, DISCRIMINATOR_RISK);
                    if ((riskProdStructs != null) && (riskProdStructs.size() > 0)) {
                        // список рисков текущего объекта страхования - выбор (если уже существует в элементе списка типов) или создание нового
                        List<Map<String, Object>> riskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                        Map<String, Map<String, Object>> riskListAsMapBySysName; // объект для быстрого доступа к элементам списка по системному имени
                        if (riskList == null) {
                            riskList = new ArrayList<Map<String, Object>>();
                            contrObjMap.put("CONTRRISKLIST", riskList);
                            riskListAsMapBySysName = new HashMap<String, Map<String, Object>>();
                        } else {
                            riskListAsMapBySysName = getListAsMapAndFixSysNames(riskList, "PRODRISKSYSNAME");
                        }
                        //List<Map<String, Object>> resRiskList = new ArrayList<Map<String, Object>>(); //todo: вместо создания выбирать уже существующий список рисков, если был передан
                        for (Map<String, Object> riskBean : riskProdStructs) {
                            boolean isRiskWithSums = false;
                            Double insAmValue = null;
                            Double premValue = null;
                            if (hbRes != null) {
                                String riskSysName = riskBean.get("SYSNAME").toString();
                                if ((hbRes.get("INSAM_" + riskSysName) != null) || ((hbRes.get("PREM_" + riskSysName) != null))) {
                                    if (hbRes.get("INSAM_" + riskSysName) != null) {
                                        insAmValue = Double.valueOf(hbRes.get("INSAM_" + riskSysName).toString());
                                        if (insAmValue.doubleValue() > contrObjInsAmValue.doubleValue()) {
                                            contrObjInsAmValue = insAmValue;
                                        }
                                    }
                                    if (hbRes.get("PREM_" + riskSysName) != null) {
                                        premValue = Double.valueOf(hbRes.get("PREM_" + riskSysName).toString());
                                        contractPremValue += premValue;
                                        contrObjPremValue += premValue;
                                    }
                                    isRiskWithSums = true;
                                }
                            } else {
                                if (parentEntity.get("INSAMVALUE") != null) {
                                    insAmValue = Double.valueOf(parentEntity.get("INSAMVALUE").toString());
                                }
                                if (parentEntity.get("PREMVALUE") != null) {
                                    premValue = Double.valueOf(parentEntity.get("PREMVALUE").toString());
                                }
                                isRiskWithSums = true;
                            }
                            if (isRiskWithSums) {
                                String riskSysName = getStringParam(riskBean.get("SYSNAME"));
                                // риск объекта страхования - выбор (если уже существует в договоре) ...
                                Map<String, Object> riskForContract = riskListAsMapBySysName.get(riskSysName);
                                // ... или создание нового
                                if (riskForContract == null) {
                                    if (isMissingStructsCreated) {
                                        riskForContract = new HashMap<String, Object>();
                                        riskForContract.put("PRODRISKSYSNAME", riskSysName);
                                        riskList.add(riskForContract);
                                        riskListAsMapBySysName.put(riskSysName, riskForContract);
                                    } else {
                                        continue;
                                    }
                                } else {
                                    Object currentRowStatus = riskForContract.get(ROWSTATUS_PARAM_NAME);
                                    if ((currentRowStatus != null) && (UNMODIFIED_ID == getIntegerParam(currentRowStatus))) {
                                        riskForContract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
                                    }
                                }
                                //
                                riskForContract.put("PRODSTRUCTID", riskBean.get("PRODSTRUCTID"));
                                riskForContract.put("PRODRISKSYSNAME", riskBean.get("SYSNAME"));
                                riskForContract.put("CURRENCYID", parentEntity.get("INSAMCURRENCYID"));
                                riskForContract.put("DURATION", parentEntity.get("DURATION"));
                                riskForContract.put("STARTDATE", parentEntity.get("STARTDATE"));
                                riskForContract.put("FINISHDATE", parentEntity.get("FINISHDATE"));
                                riskForContract.put("PREMCURRENCYID", parentEntity.get("PREMCURRENCYID"));
                                if (riskForContract.get("INSAMVALUE") == null) {
                                    riskForContract.put("INSAMVALUE", insAmValue);
                                }
                                if (riskForContract.get("PREMVALUE") == null) {
                                    riskForContract.put("PREMVALUE", premValue);
                                }
                                // риск - расширенные атрибуты
                                // выбор существующих расширенных атрибутов из риска
                                Map<String, Object> riskForContractExtMap = (Map<String, Object>) riskForContract.get("CONTRRISKEXTMAP");
                                if (riskForContractExtMap == null) {
                                    // или создание новых расширенных атрибутов для риска
                                    riskForContractExtMap = new HashMap<String, Object>();
                                    riskForContract.put("CONTRRISKEXTMAP", riskForContractExtMap);
                                }
                                // безусловная установка версии справочника расширенных атрибутов риска
                                riskForContractExtMap.put("HBDATAVERID", riskBean.get("HBDATAVERID"));
                            }
                        }
                    }
                    if ((contrObjMap.get("CONTRRISKLIST") != null) && ((List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST")).size() > 0) {
                        if (contrObjMap.get("PREMVALUE") == null) {
                            contrObjMap.put("PREMVALUE", contrObjPremValue);
                        }
                        if (contrObjMap.get("INSAMVALUE") == null) {
                            contrObjMap.put("INSAMVALUE", contrObjInsAmValue);
                        }
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("contractPremValue", contractPremValue);
        return result;
    }

    private void updateContractExtValues(Map<String, Object> b2bContract, Map<String, Object> hbRes, Map<String, Object> product, Double contractPremValue) throws NumberFormatException {
        // если задан справочник сумм, пытаемся из него загрузить показатели по договору
        if (hbRes != null) {
            List<Map<String, Object>> prodValues = (List<Map<String, Object>>) product.get("PRODVALUES");
            if ((prodValues != null) && (prodValues.size() > 0)) {
                Map<String, Object> contrExtMap = (Map<String, Object>) b2bContract.get("CONTREXTMAP");
                if (contrExtMap != null) {
                    long productConfigID = Long.valueOf(product.get("PRODCONFID").toString());
                    for (Map<String, Object> bean : prodValues) {
                        if (bean.get("PRODCONFID") != null) {
                            long beanProductConfigID = Long.valueOf(bean.get("PRODCONFID").toString());
                            if (beanProductConfigID == productConfigID) {
                                String valueName = getStringParam(bean.get("NAME"));
                                if (!"insuranceProgram".equalsIgnoreCase(valueName)) {
                                    if (hbRes.get(valueName) != null) {
                                        contrExtMap.put(valueName, hbRes.get(valueName));
                                    }
                                }
                                /*else {
                                 // не требуется, код и идентифкатор программы страхования уже определен ранее - в genAdditionalSaveParams
                                 // contrExtMap.put(valueName, getProdProgramIdByProgCode(programCode, Long.valueOf(b2bContract.get("PRODVERID").toString()), login, password));
                                 }*/

                            }
                        }
                    }
                }
            }
        }
        if (contractPremValue != null) {
            b2bContract.put("PREMVALUE", contractPremValue);
        }
    }

    private Map<String, Object> getRiskSumsAndContrExtValuesFromHandbook(Long productConfigId, String programCode, String login, String password) throws Exception {
        Map<String, Object> hbRes = null;
        // если не указан код программы страхования, то запрашивать как версию справочника, так и его данные нет необходимости
        if ((programCode != null) && !programCode.isEmpty()) {
            Map<String, Object> prodDefParams = new HashMap<String, Object>();
            prodDefParams.put("PRODCONFID", productConfigId);
            prodDefParams.put("NAME", RISKSUMSHBDATAVERID_PARAMNAME);
            logger.debug("alarm!!! " + prodDefParams.toString());
            Long sumsHandbookDataVerID = getLongParam(this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductDefaultValueBrowseListByParam", prodDefParams, login, password, "VALUE"));
            // если задан справочник с суммами по рискам, нужно его обработать
            if (sumsHandbookDataVerID != null) {
                //Long sumsHBDataVerId = Long.valueOf(prodDefRes.get("VALUE").toString());
                Map<String, Object> hbParams = new HashMap<String, Object>();
                hbParams.put("HBDATAVERID", sumsHandbookDataVerID);
                hbParams.put("insuranceProgram", programCode);
                hbParams.put(RETURN_AS_HASH_MAP, true);
                logger.debug("alarm!!! " + hbParams.toString());

                hbRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", hbParams, login, password);
                // если не нашли запись справочника по указанной программе, зануляем результат
                if (hbRes.get("HBSTOREID") == null) {
                    hbRes = null;
                }
                if (hbRes != null) {
                    logger.debug("alarm!!! " + hbRes.toString());
                } else {
                    logger.debug("alarm!!! null");
                }
            }
        }
        return hbRes;
    }

    private Map<String, Map<String, Object>> getListAsMapAndFixSysNames(List<Map<String, Object>> list, String correctSysNameKeyName) {
        return getListAsMapAndFixSysNames(list, correctSysNameKeyName, null);
    }

    // исправляет названия ключей, указывающих на системные имена, а также формирует объект для быстрого доступа к элементам списка по системному имени
    private Map<String, Map<String, Object>> getListAsMapAndFixSysNames(List<Map<String, Object>> list, String correctSysNameKeyName, String subMapKeyName) {
        Map<String, Map<String, Object>> listAsMapBySysName = new HashMap<String, Map<String, Object>>();
        for (Map<String, Object> element : list) {
            // определение местонахождения системного имени
            // (системное имя может находиться во вложенной в элемент мапе, например, в INSOMJMAP для INSOBJ)
            Map<String, Object> elementSysNameMap;
            if ((subMapKeyName == null) || (subMapKeyName.isEmpty())) {
                elementSysNameMap = element;
            } else {
                elementSysNameMap = (Map<String, Object>) element.get(subMapKeyName);
            }

            Object sysName = elementSysNameMap.get(correctSysNameKeyName);
            if (sysName == null) {
                // ключ, указывающий на системное имя может оказаться таким же как и в PRODSTRUCTS, то есть просто SYSNAME (например, при копировании элемента структуры из данных продукта целиком):
                // в таких случаях требуется дополнительное исправление "переименованием" ключа, отвечающего за системное имя
                // (так как в структуре продукта договора у каждого элемента свой префикс системного имени - INSOBJGROUPSYSNAME, INSOBJSYSNAME и PRODRISKSYSNAME)
                sysName = elementSysNameMap.get("SYSNAME");
                elementSysNameMap.put(correctSysNameKeyName, sysName);
            }
            // дополнение объекта для быстрого доступа к элементам списка текущим элементом
            listAsMapBySysName.put(sysName.toString(), element);
        }
        return listAsMapBySysName;
    }

    // перенесено в B2BBaseFacade
    /*
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
    */

    protected void setOverridedParam(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        if (isLogged) {
            Object oldValue = paramParent.get(paramName);
            logParamOverriding(paramName, newValue, oldValue);
        }
        paramParent.put(paramName, newValue);
    }

    protected void setGeneratedParam(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        paramParent.put(paramName, newValue);
        if (isLogged) {
            logParamGeneration(paramName, newValue);
        }
    }

    protected void setGeneratedParamIfNull(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        if (paramParent.get(paramName) == null) {
            setGeneratedParam(paramParent, paramName, newValue, isLogged);
        }
    }

    protected void logParamGeneration(String paramName, Object newValue) {
        StringBuilder logStr = new StringBuilder();
        logStr.append("Значение параметра '");
        logStr.append(paramName);
        logStr.append("' не найдено во входных данных. Для указанного параметра сгенерировано новое значение: ");
        logStr.append(newValue);
        if (newValue != null) {
            logStr.append(" (");
            logStr.append(newValue.getClass().getSimpleName());
            logStr.append(").");
        } else {
            logStr.append(".");
        }
        logger.debug(logStr.toString());
    }

    protected void logParamOverriding(String paramName, Object newValue, Object oldValue) {
        StringBuilder logStr = new StringBuilder();
        logStr.append("Значение параметра '");
        logStr.append(paramName);
        logStr.append("' из входных данных (");
        logStr.append(oldValue);
        logStr.append(") проигонорировано. Для указанного параметра сгенерировано новое значение: ");
        logStr.append(newValue);
        if (newValue != null) {
            logStr.append(" (");
            logStr.append(newValue.getClass().getSimpleName());
            logStr.append(")");
        }
        logStr.append(".");
        logger.debug(logStr.toString());
    }

    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> dsB2BApplyDiscountToContractMap(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contractMap = (Map<String, Object>) params.get("CONTRMAP");
        applyDiscountByPromoAndProd(contractMap, login, password);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRMAP", contractMap);
        return result;
    }

    private List<Map<String, Object>> applyDiscountByPromoAndProd(Map<String, Object> contract, String login, String password) throws Exception {
        List<Map<String, Object>> discValList = null;
        contract.put("B2BPROMOAPPLIED", Boolean.FALSE);
        //1. получаем промо код из договора.
        if ((contract.get("B2BPROMOCODE") != null) && !contract.get("B2BPROMOCODE").toString().isEmpty()) {
            String promoCode = contract.get("B2BPROMOCODE").toString();
            //2. получаем конфиг продукта.
            Long prodConfId = getLongParam(contract.get("PRODCONFID"));
            Date docDate = new Date();
            if (contract.get("DOCUMENTDATE") != null) {
                docDate = getDateParam(contract.get("DOCUMENTDATE"));
            }
            //3. по коду, продукту и дате договора (а если ее нет - по текущей дате) получаем акцию и структуру скидок.
            Map<String, Object> discParams = new HashMap<String, Object>();
            discParams.put("PROMOCODE", promoCode);
            discParams.put("PRODCONFID", prodConfId);
            discParams.put("DISCDATE", docDate);
            discParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> discRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDiscountBrowseListByParamEx", discParams, login, password);
            if (discRes != null) {
                if (discRes.get("PRODDISCID") != null) {
                    // проверка промокода, чтобы количество использований не превышало указаного в базе
                    Long promoCount = 0L;
                    if (discRes.get("PROMOCOUNT") != null) {
                        promoCount = Long.valueOf(discRes.get("PROMOCOUNT").toString());
                    }
                    Map<String, Object> contrDiscParam = new HashMap<String, Object>();
                    contrDiscParam.put("PRODDISCPROMOID", discRes.get("PRODDISCPROMOID"));
                    Map<String, Object> contrDiscRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractDiscountBrowseListByParam", contrDiscParam, login, password);
                    if ((contrDiscRes.get(RESULT) != null) && ((List<Map<String, Object>>) contrDiscRes.get(RESULT)).size() >= promoCount) {
                        return null;
                    }
                    //
                    if ((discRes.get("ISPREMIUM") != null) && (Long.valueOf(discRes.get("ISPREMIUM").toString()).longValue() == 1)) {
                        contract.put("B2BPROMOPREMIUM", 1L);
                        contract.put("B2BPROMOPREMIUMURL", discRes.get("PREMIUMURL"));
                        StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
                        contract.put("B2BPRODDISCID", scu.encrypt(discRes.get("PRODDISCID").toString()));
                        contract.put("B2BPRODDISCPROMOID", scu.encrypt(discRes.get("PRODDISCPROMOID").toString()));
                        return null;
                    }
                    // действующая акция по данному промо коду  по данному продукту в наличии.
                    // считываем структуру акции.
                    Map<String, Object> prodDiscValParam = new HashMap<String, Object>();
                    prodDiscValParam.put("PRODDISCID", discRes.get("PRODDISCID"));
                    Map<String, Object> prodDiscValRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDiscountValueBrowseListByParam", prodDiscValParam, login, password);
                    //4. проходим по структуре договора, применяя коеффициенты из структуры акции.
                    if (prodDiscValRes.get("Result") != null) {
                        discValList = (List<Map<String, Object>>) prodDiscValRes.get("Result");
                        if (discValList != null) {
                            for (Map<String, Object> bean : discValList) {
                                bean.put("PRODDISCPROMOID", discRes.get("PRODDISCPROMOID"));
                            }
                            if (contract.get("INSOBJGROUPLIST") != null) {
                                List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
                                Double contractPremValue = applyDiscountForInsObjGroup(insObjGroupList, discValList);
                                contract.put("PREMVALUE", contractPremValue);
                                contract.put("B2BPROMOAPPLIED", Boolean.TRUE);
                            }
                        }
                    }
                }
            }
        }
        return discValList;
    }

    // применение промокодов: сначала рисковые, потом промокод объекта применяется к рискам, потом промокод группы
    // применяется к ее объектам и далее к рискам. затем пересчет сумм снизу вверх (сумма по рискам, сумма по объектам, сумма про группам объектов)
    private Double applyDiscountForInsObjGroup(List<Map<String, Object>> insObjGroupList, List<Map<String, Object>> discValList) {
        Double result = 0.0;
        for (Map<String, Object> insObjGroup : insObjGroupList) {
            if (insObjGroup.get("OBJLIST") != null) {
                Double groupPremValue = 0.0;
                Double groupDiscount = 1.0;
                List<Map<String, Object>> childObjList = (List<Map<String, Object>>) insObjGroup.get("OBJLIST");
                for (Map<String, Object> disc : discValList) {
                    if (disc.get("PRODSTRUCTID") != null) {
                        if ((insObjGroup.get("PRODSTRUCTID") != null) && (insObjGroup.get("PRODSTRUCTID").equals(disc.get("PRODSTRUCTID")))) {
                            groupDiscount = getDoubleParam(disc.get("DISCOUNTVALUE"));
                            disc.put("ISAPPLIED", Boolean.TRUE);
                        }
                    }
                }
                for (Map<String, Object> obj : childObjList) {
                    if (obj.get("CONTROBJMAP") != null) {
                        Double objPremValue = 0.0;
                        Double objDiscount = 1.0;
                        for (Map<String, Object> disc : discValList) {
                            if (disc.get("PRODSTRUCTID") != null) {
                                if ((obj.get("PRODSTRUCTID") != null) && (obj.get("PRODSTRUCTID").equals(disc.get("PRODSTRUCTID")))) {
                                    objDiscount = getDoubleParam(disc.get("DISCOUNTVALUE"));
                                    disc.put("ISAPPLIED", Boolean.TRUE);
                                }
                            }
                        }
                        Map<String, Object> childContrObjMap = (Map<String, Object>) obj.get("CONTROBJMAP");
                        if (childContrObjMap.get("CONTRRISKLIST") != null) {
                            List<Map<String, Object>> childRiskList = (List<Map<String, Object>>) childContrObjMap.get("CONTRRISKLIST");
                            for (Map<String, Object> risk : childRiskList) {
                                if (risk.get("PRODSTRUCTID") != null) {
                                    Double riskPremValue = getDoubleParam(risk.get("PREMVALUE"));
                                    Double riskDiscount = 1.0;
                                    for (Map<String, Object> disc : discValList) {
                                        if (disc.get("PRODSTRUCTID") != null) {
                                            if ((risk.get("PRODSTRUCTID") != null) && (risk.get("PRODSTRUCTID").equals(disc.get("PRODSTRUCTID")))) {
                                                riskDiscount = getDoubleParam(disc.get("DISCOUNTVALUE"));
                                                disc.put("ISAPPLIED", Boolean.TRUE);
                                            }
                                        }
                                    }
                                    riskPremValue *= riskDiscount * objDiscount * groupDiscount;
                                    riskPremValue = ((new BigDecimal(Double.valueOf(riskPremValue).toString())).setScale(2, RoundingMode.HALF_UP)).doubleValue();
                                    objPremValue += riskPremValue;
                                    risk.put("PREMVALUE", riskPremValue);
                                }
                            }
                        }
                        obj.put("PREMVALUE", objPremValue);
                        groupPremValue += objPremValue;
                    }
                }
                insObjGroup.put("PREMVALUE", groupPremValue);
                result += groupPremValue;
            }
        }
        return result;
    }

    protected void addDiscRelationsToContract(Map<String, Object> contract, List<Map<String, Object>> discValList, String login, String password) throws Exception {
        // если премиум продукт, тогда приходят ид скидки премиум, ее надо привязать к договору
        if ((contract.get("did") != null) && (contract.get("dpid") != null)) {
            StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
            Map<String, Object> createParams = new HashMap<String, Object>();
            createParams.put("PRODDISKID", Long.valueOf(scu.decrypt(contract.get("did").toString())));
            createParams.put("PRODDISCPROMOID", Long.valueOf(scu.decrypt(contract.get("dpid").toString())));
            createParams.put("CONTRID", contract.get("CONTRID"));
            this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractDiscountCreate", createParams, login, password);
        } else if (discValList != null) {
            // считываем текущие прикрепленные акции к договору (не онлайн договора, вероятно надо переделать, чтобы не удалять всегда)
            Map<String, Object> browseParams = new HashMap<String, Object>();
            browseParams.put("CONTRID", contract.get("CONTRID"));
            List<Map<String, Object>> browseList = WsUtils.getListFromResultMap(this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractDiscountBrowseListByParam", browseParams, login, password));
            if (browseList != null) {
                for (Map<String, Object> bean : browseList) {
                    Map<String, Object> delParams = new HashMap<String, Object>();
                    delParams.put("CONTRDISCID", bean.get("CONTRDISCID"));
                    this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractDiscountDelete", delParams, login, password);
                }
            }
            // добавляем привязку примененных акций к договору
            for (Map<String, Object> dics : discValList) {
                if (getBooleanParam(dics.get("ISAPPLIED"), Boolean.FALSE)) {
                    Map<String, Object> createParams = new HashMap<String, Object>();
                    createParams.put("PRODDISKID", dics.get("PRODDISCID"));
                    createParams.put("PRODDISCPROMOID", dics.get("PRODDISCPROMOID"));
                    createParams.put("CONTRID", contract.get("CONTRID"));
                    this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractDiscountCreate", createParams, login, password);
                }
            }
        }
    }

    protected String getUploadFilePath() {
        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();
        return result;
    }

    protected String getUserUploadFilePath() {
        String result = Config.getConfig("webclient").getParam("userFilePath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();
        return result;
    }


    // выполнение запроса сведений из БД с генерацией особых условий по переданным из angular-грида параметрам фильтров
    protected Map<String, Object> doCustomWhereQuery(String customWhereQueryName, String idFieldName, Map<String, Object> params) throws Exception {
        // выполнение запроса
        logger.debug("Prepared full set of query parameters: " + params);
        logger.debug("Performing " + customWhereQueryName + " query...");
        Map<String, Object> result = this.selectQuery(customWhereQueryName, params);
        logger.debug("Query result: " + result);
        // возврат результата запроса
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BPrepareParamsAndApplyDiscount(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> rawRootParams = new HashMap<String, Object>();
        Map<String, Object> rawContract;
        if (params.get("CONTRMAP") != null) {
            rawRootParams.putAll(params);
            rawRootParams.remove("CONTRMAP");
            rawRootParams.remove("Error");
            rawContract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            rawContract = params;
        }
        // определение данных о продукте
        Map<String, Object> prodInfoMap = getProductVersionInfo(rawContract, login, password);
        Map<String, Object> productVersionInfo = (Map<String, Object>) prodInfoMap.get("productVersionInfo");
        Long productVersionID = (Long) prodInfoMap.get("productVersionID");
        String productSysName = prodInfoMap.get("productSysName").toString();
        // принудительное дополнение результата вспомогательными данными (поскольку dsB2BContractUniversalSave их не возвращает): ...
        // ... наименованием продукта (требуется для совместимости с вызовами из старых продуктов, а также на angular-интерфейсах онлайн-продуктов)
        String productName = getStringParam(productVersionInfo.get("PRODNAME"));
        rawContract.put("PRODNAME", productName);
        rawContract.put("PRODUCTNAME", productName);
        // ... системным наименованием продукта
        rawContract.put("PRODSYSNAME", productSysName);
        rawContract.put("PRODUCTSYSNAME", productSysName);
        // ... идентификатором версии продукта
        rawContract.put("PRODVERID", productVersionID);
        Map<String, Object> contract = new HashMap<String, Object>();
        contract.putAll(rawRootParams);
        // определить метод подготовки параметров и является ли продукт онлайн продуктом
        Map<String, Object> prpMap = getPrepareToSaveMethodName(productSysName, getBooleanParam(rawContract.get("ISMIGRATION"), false));
        String prepareToSaveMethodName = prpMap.get("prepareToSaveMethodName").toString();
        //
        if (prepareToSaveMethodName != null) {
            rawContract.remove("Error");
            rawContract.put(RETURN_AS_HASH_MAP, true);
            rawContract.put("DISABLE_VALIDATION", 1L);
            Map<String, Object> preparedContract = this.callService(B2BPOSWS_SERVICE_NAME, prepareToSaveMethodName, rawContract, login, password);
            Object prepareError = preparedContract.get("Error");
            if (prepareError != null) {
                logger.debug("Ошибка при обработке переданных сведений: " + prepareError);
                return preparedContract;
            } else {
                contract.putAll(preparedContract);
            }
        } else {
            contract.putAll(rawContract);
        }
        parseDates(contract, Date.class);
        // применение промокода
        Map<String, Object> discParams = new HashMap<String, Object>();
        discParams.put("CONTRMAP", contract);
        discParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> discRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BApplyDiscountToContractMap", discParams, login, password);
        //
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRMAP", discRes.get("CONTRMAP"));
        return result;
    }

    private String checkIsContractExportIn1C(Long contrId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("OBJECTID", contrId);
        params.put("TYPEID", 3000);
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BExportDataContentBrowseListByParam", params, login, password);
        if (res != null) {
            if (res.get(RESULT) != null) {
                List<Map<String, Object>> resList = (List<Map<String, Object>>) res.get(RESULT);
                if (!resList.isEmpty()) {
                    return "TRUE";
                }
            }
        }
        return "FALSE";
    }

    // копия updateContractValues из B2BContractCustomFacade
    protected void updateContractValues(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        logger.debug("updateContractValues begin");
        Map<String, Object> params = new HashMap<String, Object>();
        if (contrExtMap != null) {
            params.putAll(contrExtMap);
            Object contrExtID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", params, login, password, "CONTREXTID");
            if (contrExtID != null) {
                contrExtMap.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
            }
        }
        logger.debug("updateContractValues end");
    }


    protected Map<String, Object> getTermDataByTermID(Long termID, String login, String password) throws Exception {
        // получение сведений о сроке страхования
        Map<String, Object> termParams = new HashMap<String, Object>();
        termParams.put("TERMID", termID);
        termParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> termInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BTermBrowseListByParam", termParams, login, password);
        return termInfo;
    }

    protected Map<String, Object> getPayVarDataByPayVarID(Long payVarID, String login, String password) throws Exception {
        // получение сведений о периодичности оплаты
        Map<String, Object> payVarParams = new HashMap<String, Object>();
        payVarParams.put("PAYVARID", payVarID);
        payVarParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> payVarInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentVariantBrowseListByParam", payVarParams, login, password);
        return payVarInfo;
    }

    protected void getFinishDateByStartDateAndTermId(Map<String, Object> contract, String login, String password) throws Exception {
        Long termID = getLongParamLogged(contract, "TERMID");
        Map<String, Object> termInfo = getTermDataByTermID(termID, login, password);
        getStringParamLogged(termInfo, "NAME"); // для протокола
        Long termYearCount = getLongParamLogged(termInfo, "YEARCOUNT");
        Long termMonthCount = getLongParamLogged(termInfo, "MONTHCOUNT");
        Long termDayCount = getLongParamLogged(termInfo, "DAYCOUNT");
        if (contract.get("STARTDATE") != null) {
            Date startDate = (Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE", true);

            GregorianCalendar finishDateGC = new GregorianCalendar();
            finishDateGC.setTime(startDate);
            if (termYearCount != null) {
                finishDateGC.add(Calendar.YEAR, termYearCount.intValue());
            }
            if (termMonthCount != null) {
                finishDateGC.add(Calendar.MONTH, termMonthCount.intValue());
            }
            if (termDayCount != null) {
                finishDateGC.add(Calendar.DAY_OF_YEAR, termDayCount.intValue());
            }
            contract.put("FINISHDATE", finishDateGC.getTime());
        }
    }

    protected void genSumStr(Map<String, Object> sourceMap, String sumKeyName) {

        Double sumValue = getDoubleParamLogged(sourceMap, sumKeyName);

        String currencyKeyName = sumKeyName.replace("VALUE", "") + "CURRENCYID";
        String insAmCurrNumCode = getStringParamLogged(sourceMap, currencyKeyName);
        if ("1".equalsIgnoreCase(insAmCurrNumCode)) {
            insAmCurrNumCode = "810"; // рубли
        } else if ("2".equalsIgnoreCase(insAmCurrNumCode)) {
            insAmCurrNumCode = "840"; // доллары
        } else if ("3".equalsIgnoreCase(insAmCurrNumCode)) {
            insAmCurrNumCode = "978"; // евро
        } else {
            insAmCurrNumCode = "810"; // по-умолчанию: рубли
        }
        // "Тринадцать тысяч четыреста тридцать восемь рублей 24 копейки"
        // "Тринадцать тысяч четыреста тридцать восемь рублей 00 копеек"
        String sumStr = AmountUtils.amountToString(sumValue, insAmCurrNumCode);

        int sumDecimalValue = sumValue.intValue(); // 12345.67 -> 12345
        double sumFractionalValue = sumValue - sumDecimalValue; // 12345.67 -> 0.67
        int sumFractionalValueInt = (int) (sumFractionalValue * 100); // 0.67 -> 67, 0.66999999999 -> 67
        String sumFractionalValueIntStr = String.valueOf(sumFractionalValueInt);
        String sumFractionalValueIntStrWords = NumberFormatUtils.toWords(sumFractionalValueIntStr, false, true, false);
        if (insAmCurrNumCode.equals("810")) {
            // для копеек - доп. замена по роду (цент и евроцент - м.р., а копейка - ж.р.)
            if (sumFractionalValueIntStrWords.endsWith("один")) {
                sumFractionalValueIntStrWords = sumFractionalValueIntStrWords.substring(0, sumFractionalValueIntStrWords.length() - "один".length()) + "одна";
            } else if (sumFractionalValueIntStrWords.endsWith("два")) {
                sumFractionalValueIntStrWords = sumFractionalValueIntStrWords.substring(0, sumFractionalValueIntStrWords.length() - "два".length()) + "две";
            }
        }
        if (sumFractionalValueIntStr.length() == 1) {
            sumFractionalValueIntStr = "0" + sumFractionalValueIntStr;
        }
        sumStr = sumStr.replace(sumFractionalValueIntStr, sumFractionalValueIntStrWords);
        String sumKeyNameWithStr = sumKeyName + "STR";
        logger.debug(sumKeyNameWithStr + " = " + sumStr);
        sourceMap.put(sumKeyNameWithStr, sumStr);

    }

    // генерация строковых представлений для всех сумм
    protected void genSumStrs(Map<String, Object> data, String dataNodePath) {
        Map<String, Object> parsedMap = new HashMap<String, Object>();
        parsedMap.putAll(data);
        for (Map.Entry<String, Object> entry : parsedMap.entrySet()) {
            String keyName = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String dataValuePath = dataNodePath + "." + keyName;
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    genSumStrs(map, dataValuePath);
                } else if (value instanceof List) {
                    ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) value;
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, Object> element = list.get(i);
                        genSumStrs(element, dataValuePath + "[" + i + "]");
                    }
                } else if (keyName.endsWith("VALUE")) {
                    // Страховая сумма и её валюта
                    try {
                        Double insAmValue = Double.valueOf(value.toString());
                        logger.debug(dataValuePath + " = " + insAmValue);
                        String currencyKeyName = keyName.replace("VALUE", "") + "CURRENCYID";
                        String insAmCurrNumCode = getStringParam(data.get(currencyKeyName));
                        if ("1".equalsIgnoreCase(insAmCurrNumCode)) {
                            insAmCurrNumCode = "810"; // рубли
                        }
                        if ("2".equalsIgnoreCase(insAmCurrNumCode)) {
                            insAmCurrNumCode = "840"; // доллары
                        } else if ("3".equalsIgnoreCase(insAmCurrNumCode)) {
                            insAmCurrNumCode = "978"; // евро
                        } else {
                            insAmCurrNumCode = "810"; // по-умолчанию: рубли
                        }
                        // отдельно валюта не требуется, уже учитывается при вызове amountToString
                        //reportData.put("INSAMCURRENCYSTR", getCurrByCodeToNum("RUB", insAmValue.longValue())); 
                        // "Тринадцать тысяч четыреста тридцать восемь рублей 24 копейки"
                        String insAmStr = AmountUtils.amountToString(insAmValue, insAmCurrNumCode);

                        // "Тринадцать тысяч четыреста тридцать восемь рублей 24 копейки" > " (Тринадцать тысяч четыреста тридцать восемь) рублей 24 копейки"
                        String insAmStrBill = " (" + insAmStr.replace(" рубл", ") рубл");

                        // отбрасываем нулевые копейки
                        String insAmStrSumInSkobki = insAmStrBill.replace(" 00 копеек", "").replace(" 00 евроцентов", "").replace(" 00 центов", "");

                        // отбрасываем нулевые копейки
                        insAmStr = insAmStr.replace(" 00 копеек", "").replace(" 00 евроцентов", "").replace(" 00 центов", "");
                        String insAmNumStr = moneyFormatter.format(insAmValue);
                        String sumValueStr = insAmNumStr + " (" + insAmStr + ")";

                        data.put(keyName + "STR2", insAmNumStr + insAmStrSumInSkobki);
                        data.put(keyName + "STR", sumValueStr);
                        logger.debug(dataNodePath + "." + keyName + "STR = " + sumValueStr);

                        // 13438.24 > "13 438"
                        String insAmNumStrBill = moneyFormatter.format(insAmValue.intValue());
                        // "13 438 (Тринадцать тысяч четыреста тридцать восемь) рублей 24 копейки"
                        String sumValueStrBill = insAmNumStrBill + insAmStrBill;
                        data.put(keyName + "STRBILL", sumValueStrBill);
                        logger.debug(dataNodePath + "." + keyName + "STRBILL = " + sumValueStrBill);

                    } catch (NumberFormatException ex) {
                        logger.debug(dataValuePath + " - не сумма.");
                    } catch (IllegalArgumentException ex) {
                        logger.debug(dataValuePath + " не удалось преобразовать в строковое представление суммы.");
                    }
                }
            }
        }
    }

    @WsMethod(requiredParams = {"PAYDATE", "CURRENCYID"})
    public Map<String, Object> getCurrancyRateByDate(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Long currencyId = getLongParam(params.get("CURRENCYID"));
        String payDateStr = getStringParam(params.get("PAYDATE"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        Date payDate = sdf.parse(payDateStr);


        Double rate = this.getExchangeRateByCurrencyID(currencyId, payDate, login, password);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CURRENCYRATE", rate);
        return result;
    }

}
