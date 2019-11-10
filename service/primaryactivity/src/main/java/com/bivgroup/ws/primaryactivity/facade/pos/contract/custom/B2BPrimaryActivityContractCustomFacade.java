/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.primaryactivity.facade.pos.contract.custom;

import com.bivgroup.ws.primaryactivity.facade.B2BPrimaryActivityBaseFacade;
import com.bivgroup.ws.primaryactivity.system.Constants;
import com.bivgroup.ws.primaryactivity.system.DatesParser;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("PrimaryActivityCustom")
public class B2BPrimaryActivityContractCustomFacade extends B2BPrimaryActivityBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    // Имя сервиса для вызова методов из b2bposservice
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    // Имя сервиса для вызова методов из primaryactivity
    private static final String THIS_SERVICE_NAME = Constants.B2BPOSWS;

    private static DatesParser datesParser;
    
    // флаг подробного протоколирования операций с датами и переопределения параметров
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();
    
    // флаг подробного протоколирования вызовов методов веб-сервисов
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_CALLS_LOGGING = logger.isDebugEnabled();
    
    // типа договора (DISCRIMINATOR)
    private static final Long CONTRACT_DISCRIMINATOR_BASE = 1L; // 1 - Договор по основной деятельности
    private static final Long CONTRACT_DISCRIMINATOR_AGENT = 2L; // 2 - Агентский договор
    private static final Long CONTRACT_DISCRIMINATOR_ASSISTANCE_VZR = 3L; // 3 - Договор с ассистанс ВЗР
    // Тип договора (DISCRIMINATOR) по-умолчанию
    private static final Long CONTRACT_DISCRIMINATOR_DEFAULT = CONTRACT_DISCRIMINATOR_AGENT; // 2 - Агентский договор
    
    // Мапа для определения метода по созданию договора в зависимости от типа договора (1 - Договор по основной деятельности, 2 - Агентский договор, 3 - Договор с ассистанс ВЗР)
    //private HashMap<Long, String> contractCreationMethodNameByDiscriminator;
    // Метод по-умолчанию
    //private String contractCreationMethodNameDefault;

    // Мапа для определения метода по обработке договора в зависимости от:
    // а) типа договора (1 - Договор по основной деятельности, 2 - Агентский договор, 3 - Договор с ассистанс ВЗР)
    // б) вида модификации договора (ROWSTATUS)
    private HashMap<Long, HashMap<Integer, String>> contractMethodNameByDiscriminatorAndRowStatus;
    
    // Мапа для определения метода по обработке содержимого договора в зависимости от:
    // а) вида модификации (ROWSTATUS)
    private HashMap<Integer, String> contentMethodNameByRowStatus;
    // Начало названия метода по обработке содержимого договора
    private String contentMethodNameBase = "dsB2BMainActivityContractAgentContent";
    
    // Мапа для определения метода по обработке записи о детализации комисии в зависимости от:
    // а) вида модификации (ROWSTATUS)
    private HashMap<Integer, String> commissionDetailMethodNameByRowStatus;
    // Начало названия метода по обработке записи о детализации комисии
    private String commissionDetailMethodNameBase = "dsB2BMainActivityContractAgentCommission";
    
    // Мапа для определения метода по обработке записи о канале продаж в зависимости от:
    // а) вида модификации (ROWSTATUS)
    private HashMap<Integer, String> saleChannelMethodNameByRowStatus;
    // Начало названия метода по обработке записи о канале продаж
    private String saleChannelMethodNameBase = "dsB2BMainActivityContractAgentSalesChannel";
    
    // имя ключа, указывающего на идентификатор договора
    private static final String CONTRACT_ID_KEY_NAME = "MAINACTCONTRID";
    // имя ключа, указывающего на идентификатор записи содержимого договора
    private static final String CONTENT_ID_KEY_NAME = "MACAGENTCNTID";
    // имя ключа, указывающего на идентификатор записи детализации комиссии
    private static final String COMMISSION_DETAIL_ID_KEY_NAME = "MACAGENTCOMMISID";
    // имя ключа, указывающего на идентификатор продукта (из записи содержимого договора)
    private static final String PRODUCT_ID_KEY_NAME = "PRODVERID";
    private static final String CALCULATOR_ID_KEY_NAME = "CALCVERID";
    // имя ключа, указывающего на идентификатор риска (из записи детализации комиссии)
    private static final String RISK_ID_KEY_NAME = "PRODSTRUCTID";
    // имя ключа, указывающего на идентификатор канала продаж (из записи содержимого договора)
    private static final String SALE_CHANNEL_ID_KEY_NAME = "SALECHANNELID";
    // имя ключа, указывающего на идентификатор записи со сведениями о канале продаж (из записи о канале продаж для договоров по основной деятельности)
    private static final String SALE_CHANNEL_DATA_ID_KEY_NAME = "SALECHANNELID";
    
    // имя ключа, указывающего на список детализации комиссии
    private static final String COMMISSION_DETAILS_LIST_KEY_NAME = "COMMISSIONDETAILSLIST";
    // имя ключа, указывающего на список содержимого договора
    private static final String CONTENT_LIST_KEY_NAME = "CONTENTLIST";
    // имя ключа, указывающего на информацию о продукте (из записи содержимого договора)
    private static final String PRODUCT_MAP_KEY_NAME = "PRODVERMAP";
    // имя ключа, указывающего на информацию о калькуляторе (из записи содержимого договора)
    private static final String CALCULATOR_MAP_KEY_NAME = "CALCVERMAP";
    // имя ключа, указывающего на информацию о риске (из записи детализации комиссии)
    private static final String RISK_MAP_KEY_NAME = "PRODSTRUCTMAP";
    // имя ключа, указывающего на список каналов продаж (из записи содержимого договора)
    private static final String SALE_CHANNELS_LIST_MAP_KEY_NAME = "SALECHANNELSLIST";
    // имя ключа, указывающего на информацию о канале продаж (из записи о канале продаж для договоров по основной деятельности)
    private static final String SALE_CHANNEL_DATA_MAP_KEY_NAME = "SALECHANNELMAP";
    
    // todo: понятный коммент к воображаемому роустатусу
    private static final int ROWSTATUS_FAKE_FOR_BROWSE_ID = -1;
    
    // имя ключа, указывающего на тип договора (т.н. дискриминатор)
    private static final String CONTRACT_DISCRIMINATOR_KEY_NAME = "DISCRIMINATOR";

    public B2BPrimaryActivityContractCustomFacade() {
        super();
        init();
    }

    private void init() {
        
        // обработчик дат
        datesParser = new DatesParser();
        // обработчик дат - протоколирование операций с датами
        datesParser.setVerboseLogging(IS_VERBOSE_LOGGING);
        
        //// Формирование мапы для определения метода по созданию договора в зависимости от типа договора
        //this.contractCreationMethodNameByDiscriminator = new HashMap<Long, String>();
        //this.contractCreationMethodNameByDiscriminator.put(CONTRACT_DISCRIMINATOR_BASE, "dsB2BMainActivityContractBaseCreate"); // 1 - Договор по основной деятельности
        //this.contractCreationMethodNameByDiscriminator.put(CONTRACT_DISCRIMINATOR_AGENT, "dsB2BMainActivityContractAgentCreate"); // 2 - Агентский договор
        //this.contractCreationMethodNameByDiscriminator.put(CONTRACT_DISCRIMINATOR_ASSISTANCE_VZR, "dsB2BMainActivityContractTravelCreate"); // 3 - Договор с ассистанс ВЗР
        //// Метод по-умолчанию
        //this.contractCreationMethodNameDefault = "dsB2BMainActivityContractAgentCreate";
        
        // Формирование мапы для определения начала названия метода по обработке договора в зависимости от типа договора
        HashMap<Long, String> contractMethodNameBaseByDiscriminator = new HashMap<Long, String>();
        contractMethodNameBaseByDiscriminator.put(CONTRACT_DISCRIMINATOR_BASE, "dsB2BMainActivityContractBase"); // 1 - Договор по основной деятельности
        contractMethodNameBaseByDiscriminator.put(CONTRACT_DISCRIMINATOR_AGENT, "dsB2BMainActivityContractAgent"); // 2 - Агентский договор
        contractMethodNameBaseByDiscriminator.put(CONTRACT_DISCRIMINATOR_ASSISTANCE_VZR, "dsB2BMainActivityContractTravel"); // 3 - Договор с ассистанс ВЗР
        
        // Формирование мапы для определения окончания названия метода по обработке договора в зависимости от вида модификации
        HashMap<Integer, String> methodNameEndingByRowStatus = new HashMap<Integer, String>();
        methodNameEndingByRowStatus.put(ROWSTATUS_INSERTED_ID, "Create"); // создание
        methodNameEndingByRowStatus.put(ROWSTATUS_DELETED_ID, "Delete"); // удаление
        methodNameEndingByRowStatus.put(ROWSTATUS_MODIFIED_ID, "Update"); // обновление
        methodNameEndingByRowStatus.put(ROWSTATUS_UNMODIFIED_ID, null); // пропуск
        methodNameEndingByRowStatus.put(ROWSTATUS_FAKE_FOR_BROWSE_ID, "BrowseListByParam"); // загрузка
        
        // Формирование мапы для определения метода по обработке договора в зависимости от типа договора и вида модификации
        logger.debug("Generating primary activity contracts processing method names...");
        this.contractMethodNameByDiscriminatorAndRowStatus = new HashMap<Long, HashMap<Integer, String>>();
        for (Map.Entry<Long, String> bean : contractMethodNameBaseByDiscriminator.entrySet()) {
            Long discriminator = bean.getKey();
            String methodNameBase = bean.getValue();
            logger.debug("For contract type (DISCRIMINATOR) = " + discriminator);
            //logger.debug("  Method name base: " + methodNameBase);
            //HashMap<Integer, String> contractMethodNamesByRowStatus = new HashMap<Integer, String>();
            //for (Map.Entry<Integer, String> ending : methodNameEndingsByRowStatus.entrySet()) {
            //    Integer rowStatus = ending.getKey();
            //    String methodNameEnding = ending.getValue();
            //    logger.debug("    For row status (ROWSTATUS) = " + rowStatus);
            //    logger.debug("    Method name ending: " + methodNameEnding);
            //    String methodNameFull;
            //    if (methodNameEnding != null) {
            //        methodNameFull = methodNameBase + methodNameEnding;
            //    } else {
            //        methodNameFull = null;                    
            //    }
            //    contractMethodNamesByRowStatus.put(rowStatus, methodNameFull);
            //    logger.debug("    Full method name: " + methodNameFull);
            //}            
            HashMap<Integer, String> contractMethodNamesByRowStatus = genMethodNamesMap(methodNameEndingByRowStatus, methodNameBase);
            
            this.contractMethodNameByDiscriminatorAndRowStatus.put(discriminator, contractMethodNamesByRowStatus);
        }
        logger.debug("Generating primary activity contracts processing method names finished.");
        
        // Формирование мапы для определения метода по обработке записи содержимого договора в зависимости от вида модификации
        logger.debug("Generating primary activity contract content processing method names...");
        this.contentMethodNameByRowStatus = genMethodNamesMap(methodNameEndingByRowStatus, this.contentMethodNameBase);
        logger.debug("Generating primary activity contract content processing method names finished.");
        
        // Формирование мапы для определения метода по обработке записи детализации комиссии в зависимости от вида модификации
        logger.debug("Generating commission detail processing method names...");
        this.commissionDetailMethodNameByRowStatus = genMethodNamesMap(methodNameEndingByRowStatus, this.commissionDetailMethodNameBase);
        logger.debug("Generating commission detail processing method names finished.");
        
        // Формирование мапы для определения метода по обработке записи о канале продаж в зависимости от вида модификации
        logger.debug("Generating sale channel processing method names...");
        this.saleChannelMethodNameByRowStatus = genMethodNamesMap(methodNameEndingByRowStatus, this.saleChannelMethodNameBase);
        logger.debug("Generating sale channel processing method names finished.");
        
    }

    private HashMap<Integer, String> genMethodNamesMap(HashMap<Integer, String> methodNameEndingsByRowStatus, String methodNameBase) {
        // Формирование мапы для определения метода по обработке объекта в зависимости от вида модификации
        logger.debug("  Method name base: " + contentMethodNameBase);
        HashMap<Integer, String> methodNameByRowStatus = new HashMap<Integer, String>();
        for (Map.Entry<Integer, String> bean : methodNameEndingsByRowStatus.entrySet()) {
            Integer rowStatus = bean.getKey();
            String methodNameEnding = bean.getValue();
            logger.debug("    For row status (ROWSTATUS) = " + rowStatus);
            logger.debug("    Method name ending: " + methodNameEnding);
            String methodNameFull;
            if (methodNameEnding != null) {
                methodNameFull = methodNameBase + methodNameEnding;
            } else {
                methodNameFull = null;
            }
            methodNameByRowStatus.put(rowStatus, methodNameFull);
            logger.debug("    Full method name: " + methodNameFull);
        }
        return methodNameByRowStatus;
    }
    
    // Получение имени метода для создания договора по типу договора
    //private String getContractCreationMethodName(Long discriminator) {
    //    logger.debug("Selecting contract creation method name for discriminator = " + discriminator);
    //    String creationMethodName = contractCreationMethodNameByDiscriminator.get(discriminator);
    //    if (creationMethodName == null) {
    //        creationMethodName = contractCreationMethodNameDefault;
    //        logger.debug("No contract creation method name found for this discriminator value, default method (" + creationMethodName + ") will be used.");
    //    } else {
    //        logger.debug("Selected contract creation method name: " + creationMethodName);
    //    }
    //    return creationMethodName;
    //}

    // Получение имени метода для обработки договора по типу договора и виду модификации
    private String getContractProcessingMethodName(Long discriminator, Integer rowStatus) {
        logger.debug("Selecting contract processing method name for discriminator = " + discriminator + " and row status = " + rowStatus + "...");
        Map<Integer, String> contractMethodNameByRowStatus = this.contractMethodNameByDiscriminatorAndRowStatus.get(discriminator);
        if (contractMethodNameByRowStatus == null) {
            logger.debug("No contract processing method names found for this discriminator value, default discriminator value (" + CONTRACT_DISCRIMINATOR_DEFAULT + ") will be used.");
            contractMethodNameByRowStatus = this.contractMethodNameByDiscriminatorAndRowStatus.get(CONTRACT_DISCRIMINATOR_DEFAULT);
        }
        String processingMethodName = contractMethodNameByRowStatus.get(rowStatus);
        if (processingMethodName != null) {
            logger.debug("Selected contract processing method name: " + processingMethodName);
        } else {
            logger.debug("No contract processing method name found for this discriminator and row status values, contract processing will be skipped.");
        }
        return processingMethodName;
    }

    // Получение имени метода для обработки содержимого договора по виду модификации
    private String getContentProcessingMethodName(Integer rowStatus) {
        logger.debug("Selecting contract content processing method name for row status = " + rowStatus + "...");
        String processingMethodName = this.contentMethodNameByRowStatus.get(rowStatus);
        if (processingMethodName != null) {
            logger.debug("Selected contract content processing method name: " + processingMethodName);
        } else {
            logger.debug("No contract content processing method name found for this row status values, contract content record processing will be skipped.");
        }
        return processingMethodName;
    }

    // Получение имени метода для обработки детализации комиссии по виду модификации
    private String getCommissionDetailProcessingMethodName(Integer rowStatus) {
        logger.debug("Selecting commission detail processing method name for row status = " + rowStatus + "...");
        String processingMethodName = this.commissionDetailMethodNameByRowStatus.get(rowStatus);
        if (processingMethodName != null) {
            logger.debug("Selected commission detail processing method name: " + processingMethodName);
        } else {
            logger.debug("No commission detail processing method name found for this row status values, detail processing record processing will be skipped.");
        }
        return processingMethodName;
    }

    // Получение имени метода для обработки записи о канале продаж по виду модификации
    private String getSaleChannelProcessingMethodName(Integer rowStatus) {
        logger.debug("Selecting commission detail processing method name for row status = " + rowStatus + "...");
        String processingMethodName = this.saleChannelMethodNameByRowStatus.get(rowStatus);
        if (processingMethodName != null) {
            logger.debug("Selected commission detail processing method name: " + processingMethodName);
        } else {
            logger.debug("No commission detail processing method name found for this row status values, detail processing record processing will be skipped.");
        }
        return processingMethodName;
    }

    
    
    
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BPrimaryActivityContractBrowseListByParamCustomWhereEx(Map<String, Object> params) throws Exception {
        logger.debug("Start primary activity contracts browse...");

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(params, Double.class);

        String idFieldName = "MAINACTCONTRID";
        String customWhereQueryName = "dsB2BPrimaryActivityContractBrowseListByParamCustomWhereEx";
        Map<String, Object> result = doCustomWhereQuery(customWhereQueryName, idFieldName, params);

        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(result, String.class);

        logger.debug("Primary activity contracts browse finish.");

        return result;
    }

    private Long createMainActivityContractNode(String login, String password) throws Exception {
        logger.debug("Сreating primary activity contract node...");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("RVERSION", 0L);
        params.put("LASTVERNUMBER", 0L);
        Long nodeID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BMainActivityContractNodeCreate", params, IS_VERBOSE_CALLS_LOGGING, login, password, "MAINACTCONTRNODEID"));
        logger.debug("Сreated primary activity contract node with id (MAINACTCONTRNODEID) = " + nodeID);
        return nodeID;
    }    

    private String generateContractNumber(String login, String password) throws Exception {
        logger.debug("Generating new contract number...");        
        String autoNumSysName = "primaryActivityContrAutoNum";
        logger.debug("Using autonumber mask with name: " + autoNumSysName);
        Map<String, Object> maskParams = new HashMap<String, Object>();
        maskParams.put("SYSTEMBRIEF", autoNumSysName);
        Map<String, Object> genResult = this.callService(COREWS, "dsNumberFindByMask", maskParams, IS_VERBOSE_CALLS_LOGGING, login, password);
        String nextContractNumber = getStringParam(genResult, "Result");
        logger.debug("Generated new contract number = " + nextContractNumber);
        return nextContractNumber;
    }

    @WsMethod(requiredParams = {CONTRACT_ID_KEY_NAME, CONTRACT_DISCRIMINATOR_KEY_NAME})
    public Map<String, Object> dsB2BPrimaryActivityContractLoad(Map<String, Object> params) throws Exception {
        
        logger.debug("Loading primary activity contract...");

        // логин и пароль для вызова методов веб-сервисов
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();       
        
        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        // datesParser.parseDates(params, Double.class); // даты из входных параметров не используются
        
        // ИД договора
        Long contractID = getLongParam(params, CONTRACT_ID_KEY_NAME);
        logger.debug(CONTRACT_ID_KEY_NAME + " = " + contractID);
        // тип договора
        Long discriminator = getLongParam(params, CONTRACT_DISCRIMINATOR_KEY_NAME);
        logger.debug(CONTRACT_DISCRIMINATOR_KEY_NAME + " = " + discriminator);
        
        // определение имени метода для загрузки сведений договора данной типа
        //String contractBrowseMethodName = "dsB2BMainActivityContractBaseBrowseListByParam";
        String contractBrowseMethodName = getContractProcessingMethodName(discriminator, ROWSTATUS_FAKE_FOR_BROWSE_ID);
        
        // параметры договора
        Map<String, Object> contractParams = new HashMap<String, Object>();
        contractParams.put(CONTRACT_ID_KEY_NAME, contractID);
        contractParams.put(CONTRACT_DISCRIMINATOR_KEY_NAME, discriminator);
        contractParams.put(RETURN_AS_HASH_MAP, true);
        
        // запрос основных данных договора
        Map<String, Object> contract = this.callService(B2BPOSWS_SERVICE_NAME, contractBrowseMethodName, contractParams, IS_VERBOSE_CALLS_LOGGING, login, password);
        
        if (CONTRACT_DISCRIMINATOR_AGENT.equals(discriminator)) {
            // загрузка содержимого для агентского договора
            loadContent(contract, login, password);
        }
        
        // все возвращаемые сущности помечаются статусом UNMODIFIED
        markAllMapsByKeyValue(contract, ROWSTATUS_PARAM_NAME, ROWSTATUS_UNMODIFIED_ID);
        
        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(contract, String.class);
        
        logger.debug("Loading primary activity contract finished.");
        
        return contract;
        
    }

    private void loadContent(Map<String, Object> contract, String login, String password) throws Exception {

        logger.debug("Loading primary activity contract content list...");
        
        // идентификатор договора (которому принадлежит обрабатывемое содержимое)
        Long contractID = getLongParam(contract, CONTRACT_ID_KEY_NAME);
        logger.debug(CONTRACT_ID_KEY_NAME + " = " + contractID);
        
        if (contractID != null) {

            // параметры содержимого договора
            Map<String, Object> contentParams = new HashMap<String, Object>();
            contentParams.put(CONTRACT_ID_KEY_NAME, contractID);
            // запрос списка записей содержимого договора
            List<Map<String, Object>> contentList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BMainActivityContractAgentContentBrowseListByParam", contentParams, IS_VERBOSE_CALLS_LOGGING, login, password);

            // загрузка дополнительных сведений для каждой записи содержимого договора
            for (Map<String, Object> content : contentList) {
                // загрузка информации о продукте
                loadContentProductInfo(content, login, password);
                // загрузка информации о продукте
                loadContentCalculatorInfo(content, login, password);
                // загрузка детализации комиссии
                loadCommissionDetails(content, login, password);
                // загрузка списка каналов продаж
                loadSaleChannels(content, login, password);
            }

            // дополнение договора списком содержимого
            contract.put(CONTENT_LIST_KEY_NAME, contentList);
            
        }
        
        logger.debug("Loading primary activity contract content list finished.");

    }

    private void loadContentProductInfo(Map<String, Object> content, String login, String password) throws Exception {

        logger.debug("Loading contract content product info...");
        
        // идентификатор записи содержимого договора
        Long contentID = getLongParam(content, CONTENT_ID_KEY_NAME);
        logger.debug(CONTENT_ID_KEY_NAME + " = " + contentID);
        
        // идентификатор продукта, соответствующего данной записи содержимого договора
        Long productVersionID = getLongParam(content, PRODUCT_ID_KEY_NAME);
        logger.debug(PRODUCT_ID_KEY_NAME + " = " + productVersionID);
        
        if (productVersionID != null) {
            // параметры продукта
            Map<String, Object> productParams = new HashMap<String, Object>();
            productParams.put(PRODUCT_ID_KEY_NAME, productVersionID);
            productParams.put(RETURN_AS_HASH_MAP, true);
            // запрос информации о продукте
            Map<String, Object> productInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductVersionBrowseListByParamEx", productParams, IS_VERBOSE_CALLS_LOGGING, login, password);
            // дополнение записи содержимого договора информацией о продукте
            content.put(PRODUCT_MAP_KEY_NAME, productInfo);
        }
        
        logger.debug("Loading contract content product info finished.");

    }
    
    
    private void loadCommissionDetails(Map<String, Object> content, String login, String password) throws Exception {

        logger.debug("Loading commission details list...");
        
        // идентификатор содержимого договора (которому принадлежат записи детализации комиссии)
        Long contentID = getLongParam(content, CONTENT_ID_KEY_NAME);
        logger.debug(CONTENT_ID_KEY_NAME + " = " + contentID);
        
        if (contentID != null) {
            // параметры детализаций комиссии
            Map<String, Object> commissionDetailsParams = new HashMap<String, Object>();
            commissionDetailsParams.put(CONTENT_ID_KEY_NAME, contentID);
            // запрос списка записей детализации комиссии
            List<Map<String, Object>> commissionDetailsList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BMainActivityContractAgentCommissionBrowseListByParam", commissionDetailsParams, IS_VERBOSE_CALLS_LOGGING, login, password);
            
            // загрузка дополнительных сведений для каждой записи детализации комиссии
            for (Map<String, Object> commissionDetail : commissionDetailsList) {
                loadCommissionDetailRiskInfo(commissionDetail, login, password);
            }
            
            // дополнение записи содержимого договора списком детализации комиссии
            content.put(COMMISSION_DETAILS_LIST_KEY_NAME, commissionDetailsList);
        }
        
        logger.debug("Loading commission details list finished.");

    }
    
    private void loadSaleChannels(Map<String, Object> content, String login, String password) throws Exception {

        logger.debug("Loading sale channels list...");
        
        // идентификатор содержимого договора (которому принадлежат записи о каналах продаж)
        Long contentID = getLongParam(content, CONTENT_ID_KEY_NAME);
        logger.debug(CONTENT_ID_KEY_NAME + " = " + contentID);
        
        if (contentID != null) {
            // параметры детализаций комиссии
            Map<String, Object> saleChannelsParams = new HashMap<String, Object>();
            saleChannelsParams.put(CONTENT_ID_KEY_NAME, contentID);
            // запрос списка записей каналов продаж
            List<Map<String, Object>> saleChannelsList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BMainActivityContractAgentSalesChannelBrowseListByParam", saleChannelsParams, IS_VERBOSE_CALLS_LOGGING, login, password);
            
            // загрузка дополнительных сведений для каждой записи детализации комиссии
            for (Map<String, Object> saleChannel : saleChannelsList) {
                loadSaleChannelInfo(saleChannel, login, password);
            }
            
            // дополнение записи содержимого договора списком детализации комиссии
            content.put(SALE_CHANNELS_LIST_MAP_KEY_NAME, saleChannelsList);
        }
        
        logger.debug("Loading sale channels list finished.");

    }
    
    private void loadSaleChannelInfo(Map<String, Object> saleChannel, String login, String password) throws Exception {

        logger.debug("Loading sale channel info...");
        
        // идентификатор записи со сведениями о канале продаж
        Long saleChannelDataID = getLongParam(saleChannel, SALE_CHANNEL_DATA_ID_KEY_NAME);
        logger.debug(SALE_CHANNEL_DATA_ID_KEY_NAME + " = " + saleChannelDataID);
        
        if (saleChannelDataID != null) {
            // параметры канала продаж
            Map<String, Object> saleChannelDataParams = new HashMap<String, Object>();
            saleChannelDataParams.put(SALE_CHANNEL_DATA_ID_KEY_NAME, saleChannelDataID);
            saleChannelDataParams.put(RETURN_AS_HASH_MAP, true);
            // запрос расширенных сведений о канале продаж
            Map<String, Object> saleChannelData = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductSalesChannelBrowseListByParamEx", saleChannelDataParams, IS_VERBOSE_CALLS_LOGGING, login, password);
            // дополнение записи о канале продаж расширенными сведенями о канале
            saleChannel.put(SALE_CHANNEL_DATA_MAP_KEY_NAME, saleChannelData);
        }
        
        logger.debug("Loading sale channel info finished.");

    }
    
    private void loadCommissionDetailRiskInfo(Map<String, Object> commissionDetail, String login, String password) throws Exception {

        logger.debug("Loading commission detail risk info...");
        
        // идентификатор записи детализации комиссии 
        Long commissionDetailID = getLongParam(commissionDetail, COMMISSION_DETAIL_ID_KEY_NAME);
        logger.debug(COMMISSION_DETAIL_ID_KEY_NAME + " = " + commissionDetailID);
        
        // идентификатор риска, соответствующего данной записи детализации комиссии 
        Long riskID = getLongParam(commissionDetail, RISK_ID_KEY_NAME);
        logger.debug(RISK_ID_KEY_NAME + " = " + riskID);
        
        if (riskID != null) {
            // параметры риска
            Map<String, Object> riskParams = new HashMap<String, Object>();
            riskParams.put(RISK_ID_KEY_NAME, riskID);
            riskParams.put(RETURN_AS_HASH_MAP, true);
            // запрос информации о риске
            Map<String, Object> riskInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductStructureBaseBrowseListByParam", riskParams, IS_VERBOSE_CALLS_LOGGING, login, password);
            // дополнение записи о детализации комиссии информацией о риске
            commissionDetail.put(RISK_MAP_KEY_NAME, riskInfo);
        }
        
        logger.debug("Loading commission detail risk info finished.");

    }
    
    @WsMethod(requiredParams = {CONTRACT_DISCRIMINATOR_KEY_NAME, "STARTDATE", "FINISHDATE", "ORGSTRUCTID"})
    public Map<String, Object> dsB2BPrimaryActivityContractSave(Map<String, Object> params) throws Exception {

        logger.debug("Saving primary activity contract...");

        // логин и пароль для вызова методов веб-сервисов
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        
        Map<String, Object> contract = new HashMap<String, Object>();
        contract.putAll(params);
        contract.remove(WsConstants.LOGIN);
        contract.remove(WsConstants.PASSWORD);

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(contract, Double.class);
        
        // Определение типа модификации договора
        Integer contractRowStatus = getRowStatusOrSetToInsertedIfNull(contract);
        
        if (contractRowStatus == ROWSTATUS_INSERTED_ID) {
            // если договор создается - необходимо сформировать значения для ряда дополнительных атрибутов и пр.

            // создание узла для договора
            Long nodeID = createMainActivityContractNode(login, password);
            contract.put("MAINACTCONTRNODEID", nodeID);

            // установка даты оформления договора (DOCUMENTDATE)
            if (contract.get("DOCUMENTDATE") == null) {
                Date documentDate = new Date();
                setGeneratedParamIfNull(contract, "DOCUMENTDATE", documentDate, IS_VERBOSE_LOGGING);
            }

            // генерация номера договора
            if (contract.get("CONTRNUMBER") == null) {
                String contractNumber = generateContractNumber(login, password);
                setGeneratedParamIfNull(contract, "CONTRNUMBER", contractNumber, IS_VERBOSE_LOGGING);
            }
        
        } else if (contractRowStatus == ROWSTATUS_DELETED_ID) {
            // если договор удаляется - необходимо удалить и все связанные с договором дочерние сущности (вне зависимости от их вида модификации)
            // все дочерние сущности (вне зависимости от их вида модификации) помечаются как удаляемые - будут удалены в ходе обработки содержимого для агентского договора
            markAllMapsByKeyValue(contract, ROWSTATUS_PARAM_NAME, ROWSTATUS_DELETED_ID);
            
            // если договор помечен как удаляемый и не указан его идентификатор, то записи об этом договоре нет в БД и вызов метода удаления не требуется
            if (contract.get(CONTRACT_ID_KEY_NAME) == null) {
                contractRowStatus = ROWSTATUS_UNMODIFIED_ID;
                contract.remove(ROWSTATUS_PARAM_NAME);
            }
            
        }
        
        // тип договора
        Long discriminator = getLongParam(contract, CONTRACT_DISCRIMINATOR_KEY_NAME);
        
        // определение имени метода для создания договора по переданному типу договора
        //String contractCreationMethodName = getContractCreationMethodName(discriminator);
        
        // определение имени метода для обработки договора по переданным типу договора и виду модификации
        String contractProcessingMethodName = getContractProcessingMethodName(discriminator, contractRowStatus);

        if (contractProcessingMethodName != null) {

            // создание/изменение/удаление договора по основной деятельности
            contract.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> processResult = this.callService(B2BPOSWS_SERVICE_NAME, contractProcessingMethodName, contract, IS_VERBOSE_CALLS_LOGGING, login, password);

            // формирование результата обработки договора по основной деятельности
            if (isCallResultOK(processResult)) {
                // вызов метода завершен успешно (isCallResultOK) - необходим анализ результата
                Long contractID = getLongParam(processResult, CONTRACT_ID_KEY_NAME);
                if (contractID != null) {
                    // если вызов метода завершен успешно (isCallResultOK) и в ответе присутствует ИД - это создание или обновление
                    // необходимо обновить данные договора
                    contract.put(CONTRACT_ID_KEY_NAME, contractID);
                    contract.put(ROWSTATUS_PARAM_NAME, ROWSTATUS_UNMODIFIED_ID);
                } else {
                    // если вызов метода завершен успешно (isCallResultOK) и в ответе отсутствует ИД - это удаление
                    // необходимо исключить некоторые данные договора из возвращаемого результата (поскольку запись была удалена из БД)
                    contract.remove(ROWSTATUS_PARAM_NAME);
                    //contract.remove(CONTRACT_ID_KEY_NAME); // идентификатор договора удаляется из результата только после обработки содержимого
                }
            } else {
                logger.debug("Saving primary activity contract finished with error. Detailed info: " + processResult);
            }

        }
        
        if (CONTRACT_DISCRIMINATOR_AGENT.equals(discriminator)) {
            // обработка содержимого для агентского договора
            processContent(contract, login, password);
        }
        
        // при удалении договора идентификатор договора исключается из результата только после обработки содержимого
        if (contract.get(ROWSTATUS_PARAM_NAME) == null) {
            contract.remove(CONTRACT_ID_KEY_NAME);
        }

        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(contract, String.class);

        logger.debug("Saving primary activity contract finished.");

        return contract;
        
    }
    
    private void processContent(Map<String, Object> contract, String login, String password) throws Exception {
        
        logger.debug("Saving primary activity contract content list...");
        
        // исходный список содержимого договора
        List<Map<String, Object>> rawContentList = (ArrayList<Map<String, Object>>) contract.get(CONTENT_LIST_KEY_NAME);
        if (rawContentList == null) {
            logger.debug("No contract content list found - saving primary activity contract content list skipped.");
            return;
        }
        
        // список обработанных записей содержимого договора
        List<Map<String, Object>> processedContentList = new ArrayList<Map<String, Object>>();        
        
        // идентификатор договора (которому принадлежит обрабатывемое содержимое)
        Long contractID = getLongParam(contract, CONTRACT_ID_KEY_NAME);
        logger.debug(CONTRACT_ID_KEY_NAME + " = " + contractID);        

        // последовательная обработка всех записей в переданном списке
        for (Map<String, Object> content : rawContentList) {
            
            logger.debug("Saving primary activity contract content list record...");
            
            // Определение типа модификации записи содержимого договора
            Integer recordRowStatus = getRowStatusOrSetToInsertedIfNull(content);
            
            // флаг возврата данных текущей записи содержимого договора после вызова метода обработки
            boolean isRecordReturned = true;
            
            // если запись содержимого удаляется - необходимо удалить и все связанные с нею дочерние сущности (вне зависимости от их вида модификации)
            if (recordRowStatus == ROWSTATUS_DELETED_ID) {
                // все дочерние сущности (вне зависимости от их вида модификации) помечаются как удаляемые - будут удалены в ходе обработки содержимого для агентского договора
                markAllMapsByKeyValue(content, ROWSTATUS_PARAM_NAME, ROWSTATUS_DELETED_ID);
                // если запись содержимого договора помечена как удаляемая и не указан её идентификатор, то записи об этом содержимомом нет в БД и вызов метода удаления не требуется
                if (contract.get(CONTRACT_ID_KEY_NAME) == null) {
                    recordRowStatus = ROWSTATUS_UNMODIFIED_ID;
                    content.remove(ROWSTATUS_PARAM_NAME);
                }
            }
            
            // определение имени метода для обработки содержимого договора по переданному виду модификации
            String processingMethodName = getContentProcessingMethodName(recordRowStatus);

            if (processingMethodName != null) {

                // создание/изменение/удаление содержимого договора по основной деятельности
                content.put(CONTRACT_ID_KEY_NAME, contractID); // обновление идентификатор договора
                content.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> processResult = this.callService(B2BPOSWS_SERVICE_NAME, processingMethodName, content, IS_VERBOSE_CALLS_LOGGING, login, password);

                // анализ результата вызова метода по обработке содержимого договора по основной деятельности
                if (isCallResultOK(processResult)) {
                    // вызов метода завершен успешно (isCallResultOK) - необходим анализ результата                    
                    Long contentID = getLongParam(processResult, CONTENT_ID_KEY_NAME);
                    if (contentID != null) {
                        // если вызов метода завершен успешно (isCallResultOK) и в ответе присутствует ИД - это создание или обновление
                        // необходимо обновить данные текущей записи содержимого договора
                        content.put(CONTENT_ID_KEY_NAME, contentID);
                        content.put(ROWSTATUS_PARAM_NAME, ROWSTATUS_UNMODIFIED_ID);
                    } else {
                        // если вызов метода завершен успешно (isCallResultOK) и в ответе отсутствует ИД - это удаление
                        // необходимо исключить данные текущей записи содержимого договора из возвращаемого результата (поскольку запись была удалена из БД)
                        isRecordReturned = false;
                    }
                } else {
                    // вызов метода завершен с ошибкой
                    logger.debug("Saving commission detail finished with error. Detailed info: " + processResult);
                }

            }
            
            // обработка детализации текущей записи содержимого договора
            processCommissionDetails(content, login, password);

            // обработка списка каналов продаж текущей записи содержимого договора
            processSaleChannels(content, login, password);
            
            // дополнение возвращаемого списка созданной или обновленной записью
            if (isRecordReturned) {
                processedContentList.add(content);
            }
            
            logger.debug("Saving primary activity contract content list record finished.");

        }
        
        // замена списка содержимого договора на обработанный список
        contract.put(CONTENT_LIST_KEY_NAME, processedContentList);
        
        logger.debug("Saving primary activity contract content list finished.");

    }

    private void processCommissionDetails(Map<String, Object> content, String login, String password) throws Exception {
        
        logger.debug("Saving commission details list...");
        
        // исходный список детализации комиссии
        List<Map<String, Object>> rawCommissionDetailsList = (ArrayList<Map<String, Object>>) content.get(COMMISSION_DETAILS_LIST_KEY_NAME);
        if (rawCommissionDetailsList == null) {
            logger.debug("No commission details list found - saving commission details list skipped.");
            return;
        }
        
        // список обработанных записей детализации комиссии
        List<Map<String, Object>> processedCommissionDetailsList = new ArrayList<Map<String, Object>>();
        
        // идентификатор записи содержимого договора (которой принадлежит обрабатывемая детализация комиссии)
        Long contentID = getLongParam(content, CONTENT_ID_KEY_NAME);
        logger.debug(CONTENT_ID_KEY_NAME + " = " + contentID);        
        
        // последовательная обработка всех записей в переданном списке
        for (Map<String, Object> commissionDetail : rawCommissionDetailsList) {
                
            logger.debug("Saving commission details list record...");
            
            // Определение типа модификации записи содержимого договора
            Integer recordRowStatus = getRowStatusOrSetToInsertedIfNull(commissionDetail);
            
            // флаг возврата данных текущей записи детализации комиссии после вызова метода обработки
            boolean isRecordReturned = true;
            
            // если запись детализации комиссии удаляется - необходимо удалить и все связанные с нею дочерние сущности (вне зависимости от их вида модификации)
            if (recordRowStatus == ROWSTATUS_DELETED_ID) {
                // все дочерние сущности (вне зависимости от их вида модификации) помечаются как удаляемые - будут удалены в ходе обработки дочерних сущностей
                markAllMapsByKeyValue(commissionDetail, ROWSTATUS_PARAM_NAME, ROWSTATUS_DELETED_ID);
                // если запись детализации комиссии помечена как удаляемая и не указан её идентификатор, то записи об этой детализации комиссии нет в БД и вызов метода удаления не требуется
                if (commissionDetail.get(COMMISSION_DETAIL_ID_KEY_NAME) == null) {
                    recordRowStatus = ROWSTATUS_UNMODIFIED_ID;
                    commissionDetail.remove(ROWSTATUS_PARAM_NAME);
                }
            }
            
            // определение имени метода для обработки записи детализации комиссии по переданному виду модификации
            String processingMethodName = getCommissionDetailProcessingMethodName(recordRowStatus);

            if (processingMethodName != null) {

                // создание/изменение/удаление записи детализации комиссии
                commissionDetail.put(CONTENT_ID_KEY_NAME, contentID); // обновление идентификатора содержимого договора
                commissionDetail.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> processResult = this.callService(B2BPOSWS_SERVICE_NAME, processingMethodName, commissionDetail, IS_VERBOSE_CALLS_LOGGING, login, password);

                // анализ результата вызова метода по обработке записи детализации комиссии
                if (isCallResultOK(processResult)) {
                    // вызов метода завершен успешно (isCallResultOK) - необходим анализ результата                    
                    Long commissionDetailID = getLongParam(processResult, COMMISSION_DETAIL_ID_KEY_NAME);
                    if (commissionDetailID != null) {
                        // если вызов метода завершен успешно (isCallResultOK) и в ответе присутствует ИД - это создание или обновление
                        // необходимо обновить данные текущей записи записи детализации комиссии
                        commissionDetail.put(COMMISSION_DETAIL_ID_KEY_NAME, commissionDetailID);
                        commissionDetail.put(ROWSTATUS_PARAM_NAME, ROWSTATUS_UNMODIFIED_ID);
                    } else {
                        // если вызов метода завершен успешно (isCallResultOK) и в ответе отсутствует ИД - это удаление
                        // необходимо исключить данные текущей записи детализации комиссии из возвращаемого результата (поскольку запись была удалена из БД)
                        isRecordReturned = false;
                    }
                } else {
                    // вызов метода завершен с ошибкой
                    logger.debug("Saving commission detail finished with error. Detailed info: " + processResult);
                }

            }
            
            // дополнение возвращаемого списка созданной или обновленной записью
            if (isRecordReturned) {
                processedCommissionDetailsList.add(commissionDetail);
            }
            
            logger.debug("Saving commission details list record finished.");

        }
        
        // замена списка детализации комиссии на обработанный список
        content.put(COMMISSION_DETAILS_LIST_KEY_NAME, processedCommissionDetailsList);
        
        logger.debug("Saving commission details list finished.");

    }
    
    private void processSaleChannels(Map<String, Object> content, String login, String password) throws Exception {
        
        logger.debug("Saving sale channels list...");
        
        // исходный список каналов продаж
        List<Map<String, Object>> rawSaleChannelsList = (ArrayList<Map<String, Object>>) content.get(SALE_CHANNELS_LIST_MAP_KEY_NAME);
        if (rawSaleChannelsList == null) {
            logger.debug("No sale channels list found - saving sale channels list skipped.");
            return;
        }
        
        // список обработанных каналов продаж
        List<Map<String, Object>> processedSaleChannelsList = new ArrayList<Map<String, Object>>();
        
        // идентификатор записи содержимого договора (которой принадлежит обрабатывемая запись о канале продаж)
        Long contentID = getLongParam(content, CONTENT_ID_KEY_NAME);
        logger.debug(CONTENT_ID_KEY_NAME + " = " + contentID);        
        
        // последовательная обработка всех записей в переданном списке
        for (Map<String, Object> saleChannel : rawSaleChannelsList) {
                
            logger.debug("Saving sale channels list record...");
            
            // Определение типа модификации записи о канале продаж
            Integer recordRowStatus = getRowStatusOrSetToInsertedIfNull(saleChannel);
            
            // флаг возврата данных текущей записи о канале продаж после вызова метода обработки
            boolean isRecordReturned = true;
            
            // если запись о канале продаж удаляется - необходимо удалить и все связанные с нею дочерние сущности (вне зависимости от их вида модификации)
            if (recordRowStatus == ROWSTATUS_DELETED_ID) {
                // все дочерние сущности (вне зависимости от их вида модификации) помечаются как удаляемые - будут удалены в ходе обработки дочерних сущностей
                markAllMapsByKeyValue(saleChannel, ROWSTATUS_PARAM_NAME, ROWSTATUS_DELETED_ID);
                // если запись о канале продаж помечена как удаляемая и не указан её идентификатор, то записи об этом канале продаж нет в БД и вызов метода удаления не требуется
                if (saleChannel.get(SALE_CHANNEL_ID_KEY_NAME) == null) {
                    recordRowStatus = ROWSTATUS_UNMODIFIED_ID;
                    saleChannel.remove(ROWSTATUS_PARAM_NAME);
                }
            }
            
            // определение имени метода для обработки записи о канале продаж по переданному виду модификации
            String processingMethodName = getSaleChannelProcessingMethodName(recordRowStatus);

            if (processingMethodName != null) {

                // создание/изменение/удаление записи о канале продаж
                saleChannel.put(CONTENT_ID_KEY_NAME, contentID); // обновление идентификатора записи содержимого договора
                saleChannel.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> processResult = this.callService(B2BPOSWS_SERVICE_NAME, processingMethodName, saleChannel, IS_VERBOSE_CALLS_LOGGING, login, password);

                // анализ результата вызова метода по обработке записи о канале продаж
                if (isCallResultOK(processResult)) {
                    // вызов метода завершен успешно (isCallResultOK) - необходим анализ результата                    
                    Long saleChannelID = getLongParam(processResult, SALE_CHANNEL_ID_KEY_NAME);
                    if (saleChannelID != null) {
                        // если вызов метода завершен успешно (isCallResultOK) и в ответе присутствует ИД - это создание или обновление
                        // необходимо обновить данные текущей записи о канале продаж
                        saleChannel.put(SALE_CHANNEL_ID_KEY_NAME, saleChannelID);
                        saleChannel.put(ROWSTATUS_PARAM_NAME, ROWSTATUS_UNMODIFIED_ID);
                    } else {
                        // если вызов метода завершен успешно (isCallResultOK) и в ответе отсутствует ИД - это удаление
                        // необходимо исключить данные текущей записи о канале продаж из возвращаемого результата (поскольку запись была удалена из БД)
                        isRecordReturned = false;
                    }
                } else {
                    // вызов метода завершен с ошибкой
                    logger.debug("Saving sale channels list record finished with error. Detailed info: " + processResult);
                }

            }
            
            // дополнение возвращаемого списка созданной или обновленной записью
            if (isRecordReturned) {
                processedSaleChannelsList.add(saleChannel);
            }
            
            logger.debug("Saving sale channels list record finished.");

        }
        
        // замена списка каналов продаж на обработанный список
        content.put(SALE_CHANNELS_LIST_MAP_KEY_NAME, processedSaleChannelsList);
        
        logger.debug("Saving sale channels list finished.");

    }    
    
    // определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения
    //private void updateSessionParamsIfNullByCallingUserCreds(Map<String, Object> contract, String login, String password) throws Exception {
    //    if ((contract.get(Constants.SESSIONPARAM_USERACCOUNTID) == null) && (contract.get(Constants.SESSIONPARAM_DEPARTMENTID) == null)) {
    //        Map<String, Object> checkLoginParams = new HashMap<String, Object>();
    //        checkLoginParams.put("username", XMLUtil.getUserName(login));
    //        checkLoginParams.put("passwordSha", password);
    //        Map<String, Object> checkLoginResult = WsUtils.getFirstItemFromResultMap(this.selectQuery("dsB2BMort900CheckLogin", checkLoginParams));
    //        if (checkLoginResult != null) {
    //            contract.put(Constants.SESSIONPARAM_USERACCOUNTID, checkLoginResult.get("USERACCOUNTID"));
    //            contract.put(Constants.SESSIONPARAM_DEPARTMENTID, checkLoginResult.get("DEPARTMENTID"));
    //        }
    //    }
    //}
    
    private Map<String, Object> getContragentsList(Map<String, Object> params) throws Exception {
        logger.debug("Getting primary activity available contragents list...");
        
        // логин и пароль для вызова методов веб-сервисов
        //String login = params.get(WsConstants.LOGIN).toString();
        //String password = params.get(WsConstants.PASSWORD).toString();
        
        // параметры для запрос списка контрагентов - строковый код отдела "Партнеры" в орг. структуре предприятия (все контрагенты являются его дочерними элементами)
        Map<String, Object> contragentsParams = new HashMap<String, Object>();
        contragentsParams.put(PARTNERS_DEPARTMENT_CODE_LIKE_KEY_NAME, PARTNERS_DEPARTMENT_CODE_LIKE);
        logger.debug("For contragents restriction used partners department string code fragment: " + PARTNERS_DEPARTMENT_CODE_LIKE);
        
        // если передан флаг ONLY_WITH_USER_AS_MEMBER, то вернуть требуется только тех контрагентов, для которых существуют агентские договора И текущий пользователь является участником этих договоров
        Boolean onlyWithUserAsMember = getBooleanParam(params, "ONLY_WITH_USER_AS_MEMBER", false);
        Long userAccountID = null;
        if (onlyWithUserAsMember) {
            logger.debug("Found ONLY_WITH_USER_AS_MEMBER flag setted to true - checking user account id...");
            userAccountID = getLongParam(params, Constants.SESSIONPARAM_USERACCOUNTID);
        }
        
        if (userAccountID != null) {
            logger.debug("User account id found - additional restriction by members will be used (user must be registered as member in contragent's primary activity contracts to get this contragent info from DB)...");
            contragentsParams.put("USERACCOUNTID", userAccountID);
        } else {
            // если не выбран идентификатор пользователя, то необходмо учесть флаг ONLY_WITH_MACS, и если он взведен - вернуть только тех контрагентов, для которых существуют агентские договора (без учета участников)
            // (при проверке по пользователю применение данного ограничения отдельно - избыточно)            
            logger.debug("Flag ONLY_WITH_USER_AS_MEMBER not found or setted to false or user account id not found - checking ONLY_WITH_MACS flag instead...");
            Boolean onlyWithMACs = getBooleanParam(params, "ONLY_WITH_MACS", false);
            logger.debug("ONLY_WITH_MACS (is contragents without primary activity contracts will be excluded from result): " + onlyWithMACs);
            if (onlyWithMACs) {
                contragentsParams.put("ONLY_WITH_MACS", onlyWithMACs);
            }
        }
        
        // запрос списка контрагентов
        Map<String, Object> contragentsQueryResult = this.selectQuery("dsB2BPrimaryActivityContragentsListBrowseListByParamEx", contragentsParams);
        List<Map<String, Object>> contragentsList = WsUtils.getListFromResultMap(contragentsQueryResult);
        
        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRAGENTSLIST", contragentsList);
        
        logger.debug("Getting primary activity available contragents list finihed.");
        
        return result;
    }    
    
    // запрос доступных конрагентов для выбора при создании договоров по основной деятельности в angular-интерфейсе
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BPrimaryActivityGetContragentsList(Map<String, Object> params) throws Exception {
        return getContragentsList(params);
    }

    // запрос доступных конрагентов для выбора при создании/редактировании отчетов агентов в angular-интерфейсе
    // (доп. ограничение - будут возвращены только те контрагенты, для которых существуют агентские договора И текущий пользователь является участником этих договоров)
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BPrimaryActivityGetContragentsListByUserCreds(Map<String, Object> params) throws Exception {
        params.put("ONLY_WITH_USER_AS_MEMBER", true);
        return getContragentsList(params);
    }

    // запрос доступных конрагентов для выбора при ??? в angular-интерфейсе
    // (доп. ограничение - будут возвращены только те контрагенты, для которых существуют агентские договора)
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BPrimaryActivityGetContragentsListWithContracts(Map<String, Object> params) throws Exception {
        //params.put("ONLY_WITH_USER_AS_MEMBER", false);
        params.put("ONLY_WITH_MACS", true);
        return getContragentsList(params);
    }
    
    /**
     * Получить объекты в виде списка по ограничениям
     * (запрос списка продуктов, указанных в конкретном договоре по основной деятельности для выбора при создании/редактировании отчетов агентов в angular-интерфейсе)
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД договора по основной деятельности</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД договора по основной деятельности</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * <LI>PRODID - Идентификатор продукта</LI>
     * <LI>PRODNAME - Название продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MAINACTCONTRID"})
    public Map<String,Object> dsB2BMainActivityContractProductBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMainActivityContractProductBrowseListByParamEx", params);
        return result;
    }

    private void loadContentCalculatorInfo(Map<String, Object> content, String login, String password) throws Exception {
        logger.debug("Loading contract content calculator info...");
        
        // идентификатор записи содержимого договора
        Long contentID = getLongParam(content, CONTENT_ID_KEY_NAME);
        logger.debug(CONTENT_ID_KEY_NAME + " = " + contentID);
        
        // идентификатор продукта, соответствующего данной записи содержимого договора
        Long productVersionID = getLongParam(content, CALCULATOR_ID_KEY_NAME);
        logger.debug(CALCULATOR_ID_KEY_NAME + " = " + productVersionID);
        
        if (productVersionID != null) {
            // параметры продукта
            Map<String, Object> productParams = new HashMap<String, Object>();
            productParams.put(CALCULATOR_ID_KEY_NAME, productVersionID);
            // запрос информации о продукте
            Map<String, Object> calcInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAgentCalculatorBrowseList", productParams, IS_VERBOSE_CALLS_LOGGING, login, password);
            if (calcInfo != null) {
                if (calcInfo.get(RESULT) != null) {
                    List<Map<String,Object>> calcList = (List<Map<String,Object>>) calcInfo.get(RESULT);
                    if (!calcList.isEmpty()) {
                        content.put(CALCULATOR_MAP_KEY_NAME, calcList.get(0));                        
                    }
                }                
            }
            // дополнение записи содержимого договора информацией о продукте
        }        
        logger.debug("Loading contract content calculator info finished.");
    }
    
}
