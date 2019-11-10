package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author Admin
 */
@BOName("B2BProductEditorCustomFacade")
public class B2BProductEditorCustomFacade extends B2BBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());
    public static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    public static final String VALIDATORSWS_SERVICE_NAME = Constants.VALIDATORSWS;
    public static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;
    public static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    public static final String ADMINWS_SERVICE_NAME = Constants.ADMINWS;

    // мапа соответствия доступнтых идентификаторов типа данных показателя их строковым описаниям (ключ - DATATYPEID, значение - DATATYPESTR)
    // (если конкретного значения DATATYPEID нет в мапе, значит это некорректный/неподдерживаемый идентификатор типа данных)
    private Map<Long, String> valueDataTypeMap = null;

    // B2B_PRODVALUE.DISCRIMINATOR
    private static final Long VALUE_DISCRIMINATOR_CONTRACT = 1L; // договор
    private static final Long VALUE_DISCRIMINATOR_STRUCTURE = 2L; // структура

    // строка для обозначения пустых и null значений в сообщениях для интерфейса редактора продуктов
    private static final String NOT_SPECIFIED_STR = "< Не указано >";

    // мапа соответствия доступнтых идентификаторов типа данных показателя маскам имен полей в справочнике (ключ - DATATYPEID, значение - маска имени поля в справочнике)
    // (если конкретного значения DATATYPEID нет в мапе, значит это некорректный/неподдерживаемый идентификатор типа данных)   
    private Map<Long, String> hbFieldMaskByValueDataTypeMap = null;

    // имя ключа, указывающего на временный служебный флаг корректности описания типа данных показателя 
    // (применяется для пропуска проверк на соответствие типов, если у показателя проблемы с описанием его типа данных)
    private static final String DATA_TYPE_VALID_FLAG_KEY_NAME = "ISDATATYPEVALID";

    public B2BProductEditorCustomFacade() {
        super();
        initRuleMaps();

    }

    private void initRuleMaps() {
        logger.debug("Initialization rule's maps...");
        if (this.valueDataTypeMap == null) {
            initDataTypeMap();
        }
        if (this.hbFieldMaskByValueDataTypeMap == null) {
            InitHBFieldMaskByValueDataTypeMap();
        }
        logger.debug("Initialization rule's maps finished.");
    }

    private void initDataTypeMap() {
        // инициализация мапы соответствия доступнтых идентификаторов типа данных показателя их строковым описаниям (ключ - DATATYPEID, значение - DATATYPESTR)
        // (если конкретного значения DATATYPEID нет в мапе, значит это некорректный/неподдерживаемый идентификатор типа данных)
        this.valueDataTypeMap = new HashMap<Long, String>();
        valueDataTypeMap.put(1L, "java.lang.String");
        valueDataTypeMap.put(2L, "java.lang.Long");
        valueDataTypeMap.put(3L, "java.lang.Double");
        valueDataTypeMap.put(4L, "java.util.Date");
        valueDataTypeMap.put(5L, "java.lang.Boolean");
        valueDataTypeMap.put(6L, "");
        logger.debug("Values data types rule map: " + this.valueDataTypeMap);
    }

    private void InitHBFieldMaskByValueDataTypeMap() {
        // инициализация мапы соответствия доступнтых идентификаторов типа данных показателя маскам имен полей в справочнике (ключ - DATATYPEID, значение - маска имени поля в справочнике)
        // (если конкретного значения DATATYPEID нет в мапе, значит это некорректный/неподдерживаемый идентификатор типа данных)
        this.hbFieldMaskByValueDataTypeMap = new HashMap<Long, String>();
        hbFieldMaskByValueDataTypeMap.put(1L, "(STRINGFIELD\\d{2})|(CLOBFIELD\\d{2})"); // String
        hbFieldMaskByValueDataTypeMap.put(2L, "LONGFIELD\\d{2}"); // Long
        hbFieldMaskByValueDataTypeMap.put(3L, "DOUBLEFIELD\\d{2}"); // Double
        hbFieldMaskByValueDataTypeMap.put(4L, "DOUBLEFIELD\\d{2}"); // Date
        hbFieldMaskByValueDataTypeMap.put(5L, "LONGFIELD\\d{2}"); // Boolean
        hbFieldMaskByValueDataTypeMap.put(6L, "LONGFIELD\\d{2}"); // ссылка
        logger.debug("Handbook's fields by value's data types types rule map: " + this.hbFieldMaskByValueDataTypeMap);
    }

    // промежуточный метод-посредник для вызова методов редактора продуктов
    // используется в doProductEditorCall (app.js) angular-интерфейса b2b
    // (применим только для методов с действительными именами вида 'dsB2BProduct*')
    @WsMethod(requiredParams = {"METHODNAMEBASE"})
    public Map<String, Object> dsB2BProductEditorMethodCall(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> callParams = new HashMap<String, Object>();
        callParams.putAll(params);

        String productMethodBase = getStringParam(callParams.remove("METHODNAMEBASE"));
        // todo: проверка по списку допустимых имен продуктов

        String productMethodName = "dsB2BProduct" + productMethodBase;

        parseDates(callParams, Double.class);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, productMethodName, callParams, true, login, password);
        parseDates(result, String.class);

        return result;
    }

    // промежуточный метод для получения списка поставщиков для выбора в выпадающем списке интерфейса редактора продуктов
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductDataProviderBrowseListByParamEx(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> callParams = new HashMap<String, Object>();
        //callParams.putAll(params); // todo: возможно, в дальнейшем с интерфейса будут передаваться дополнительные ограничения
        callParams.put("DISCRIMINATOR", 20L);

        Map<String, Object> result = this.callService(VALIDATORSWS_SERVICE_NAME, "dsDataProviderBrowseListByParamEx", callParams, true, login, password);

        return result;

    }

    // промежуточный метод для получения списка версий калькуляторов для выбора в выпадающем списке интерфейса редактора продуктов
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductInsCalculatorVersionBrowseListByParamEx(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> callParams = new HashMap<String, Object>();
        //callParams.putAll(params); // todo: возможно, в дальнейшем с интерфейса будут передаваться дополнительные ограничения

        Map<String, Object> result = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsCalculatorVersionBrowseListByParamEx", callParams, true, login, password);

        return result;

    }

    // промежуточный метод для получения полного списка каналов продаж для выбора в выпадающем списке интерфейса редактора продуктов
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductInsSaleChannelBrowseListByParam(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> callParams = new HashMap<String, Object>();
        //callParams.putAll(params); // todo: возможно, в дальнейшем с интерфейса будут передаваться дополнительные ограничения

        Map<String, Object> result = this.callService(INSPOSWS_SERVICE_NAME, "dsSaleChannelBrowseListByParam", callParams, true, login, password);

        return result;

    }

    // промежуточный метод для получения полного списка вариантов оплаты для выбора в выпадающем списке интерфейса редактора продуктов
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductInsPaymentVariantBrowseListByParam(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> callParams = new HashMap<String, Object>();
        //callParams.putAll(params); // todo: возможно, в дальнейшем с интерфейса будут передаваться дополнительные ограничения

        Map<String, Object> result = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentVariantBrowseListByParam", callParams, true, login, password);

        return result;

    }

    // промежуточный метод для получения полного списка валют для выбора в выпадающем списке интерфейса редактора продуктов
    // также используется при загрузке продукта - см. ProductCustomFacade.populateProduct
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductRefCurrencyBrowseListByParam(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> callParams = new HashMap<String, Object>();
        callParams.putAll(params);

        Map<String, Object> result = this.callService(ADMINWS_SERVICE_NAME, "admRefCurrencyList", callParams, false, login, password);

        return result;

    }

    private Long checkAndGetEntityID(Map<String, Object> callResult, String idKeyName, String methodName) throws Exception {
        Long entityID = getLongParam(callResult, idKeyName);
        if (entityID == null) {
            String errorText = "Unknown error while calling method '" + methodName + "' during product processing. Details: " + callResult;
            logger.error(errorText);
            throw new Exception(errorText);
        }
        return entityID;
    }

    // создание 'заготовки' конфигурации продукта
    private Map<String, Object> productConfigurationTemplateCreate(Long productID, Long versionID, String productName, String login, String password) throws Exception {
        Map<String, Object> configurationParams = new HashMap<String, Object>();
        configurationParams.put("PRODID", productID);
        configurationParams.put("PRODVERID", versionID);
        configurationParams.put("NAME", productName);
        configurationParams.put("SYSNAME", "POS");
        configurationParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> createConfigurationResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigCreate", configurationParams, true, login, password);
        return createConfigurationResult;
    }

    // создание 'заготовки' версии продукта
    private Map<String, Object> productVersionTemplateCreate(Long productID, String productName, String productExternalCode, String login, String password) throws Exception {
        Map<String, Object> versionParams = new HashMap<String, Object>();
        versionParams.put("PRODID", productID);
        versionParams.put("NAME", "Версия 1");
        versionParams.put("NOTE", productName);
        versionParams.put("PRODCODE", productExternalCode);
        versionParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> createVersionResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductVersionCreate", versionParams, true, login, password);
        return createVersionResult;
    }
    
    // метод для создания 'заготовки' продукта (B2B_PROD + B2B_PRODVER + B2B_PRODCONF) по нажатию на кнопку создания нового продукта
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductTemplateCreate(Map<String, Object> params) throws Exception {

        logger.debug("Creating new product template...");

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.putAll(params);
        productParams.remove("PRODID");
        String productName = getStringParam(productParams.get("NAME"));
        if (productParams.get("NOTE") == null) {
            productParams.put("NOTE", productName);
        }
        productParams.put("ISHIDDEN", 0L);
        productParams.put(RETURN_AS_HASH_MAP, true);

        Map<String, Object> createProductResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductCreate", productParams, true, login, password);
        Long productID = checkAndGetEntityID(createProductResult, "PRODID", "dsB2BProductCreate");
        result.put("PRODID", productID);

        String productExternalCode = getStringParam(params, "EXTERNALCODE");
        Map<String, Object> createVersionResult = productVersionTemplateCreate(productID, productName, productExternalCode, login, password);
        Long versionID = checkAndGetEntityID(createVersionResult, "PRODVERID", "dsB2BProductVersionCreate");
        result.put("PRODVERID", versionID);

        Map<String, Object> createConfigurationResult = productConfigurationTemplateCreate(productID, versionID, productName, login, password);
        Long configurationID = checkAndGetEntityID(createConfigurationResult, "PRODCONFID", "dsB2BProductConfigCreate");
        result.put("PRODCONFID", configurationID);

        logger.debug("Creating new product finished with result: " + result);

        return result;

    }

    // метод для создания 'заготовки' версии продукта (B2B_PRODVER + B2B_PRODCONF) по нажатию на кнопку создания новой версии продукта
    @WsMethod(requiredParams = {"PRODID"})
    public Map<String, Object> dsB2BProductVersionTemplateCreate(Map<String, Object> params) throws Exception {

        logger.debug("Creating new product version template...");

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        Long productID = getLongParam(params, "PRODID");

        // получение сведений о продукте
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODID", productID);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductBrowseListByParam", productParams, true, login, password);
        
        // повторное получение идентификатора продукта (для проверки корректности продукта и пр.)
        productID = checkAndGetEntityID(product, "PRODID", "dsB2BProductBrowseListByParam"); 
        // получение имени и внешнего кода продукта (используются при создании 'заготовок' версии и конфигурации продукта)        
        String productName = getStringParam(product, "NAME");
        //String productExternalCode = getStringParam(product, "EXTERNALCODE");

        // создание 'заготовки' версии продукта
        //Map<String, Object> createVersionResult = productVersionTemplateCreate(productID, productName, productExternalCode, login, password);
        Map<String, Object> versionParams = new HashMap<String, Object>();
        versionParams.putAll(params);        
        if (getStringParam(versionParams, "NOTE").isEmpty()) {
            versionParams.put("NOTE", productName);
        }
        if (getStringParam(versionParams, "PRODCODE").isEmpty()) {
            String productExternalCode = getStringParam(product, "EXTERNALCODE");
            versionParams.put("PRODCODE", productExternalCode);
        }
        versionParams.put("PRODID", productID);
        versionParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> createVersionResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductVersionCreate", versionParams, true, login, password);
        
        // получение идентификатора версии продукта (для проверки корректности создания версии и формирования результата)
        Long versionID = checkAndGetEntityID(createVersionResult, "PRODVERID", "dsB2BProductVersionCreate");
        result.put("PRODVERID", versionID);

        // создание 'заготовки' конфигурации продукта
        Map<String, Object> createConfigurationResult = productConfigurationTemplateCreate(productID, versionID, productName, login, password);
        // получение идентификатора конфигурации продукта (для проверки корректности создания конфигурации и формирования результата)
        Long configurationID = checkAndGetEntityID(createConfigurationResult, "PRODCONFID", "dsB2BProductConfigCreate");
        result.put("PRODCONFID", configurationID);

        logger.debug("Creating new product version template finished with result: " + result);

        return result;

    }

    // полное удаление версии продукта (со всеми подчиненными сущностями)
    @WsMethod(requiredParams = {"PRODVERID"})
    public Map<String, Object> dsB2BProductVersionDeleteEx(Map<String, Object> params) throws Exception {

        logger.debug("Deleting product version and all child entities...");
        
        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = null;
        
        Long productVersionID = getLongParam(params, "PRODVERID");
        logger.debug(String.format("Deleting version id (PRODVERID) = %d...", productVersionID));
        
        // получение идентификатора продукта (из входных параметров или по сведениям из версии)
        Long productID = getLongParam(params, "PRODID");
        if (productID == null) {
            logger.debug("No product id (PRODID) was found in call parameters - getting from version info...");
            // во входных параметрах идентификатора продукта нет - получение по сведениям из версии)
            Map<String, Object> versionParams = new HashMap<String, Object>();
            versionParams.put("PRODVERID", productVersionID);
            versionParams.put(RETURN_AS_HASH_MAP, true);
            String versionBrowseMethodName = "dsB2BProductVersionBrowseListByParam";
            //productID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductVersionBrowseListByParam", versionParams, login, password, "PRODID"));
            Map<String, Object> version = this.callService(B2BPOSWS_SERVICE_NAME, versionBrowseMethodName, versionParams, true, login, password);
            productID = checkAndGetEntityID(version, "PRODID", versionBrowseMethodName);
        }
        logger.debug(String.format("This version belong to product with id (PRODID) = %d...", productID));
        
        // получение полной мапы продукта универсальной загрузкой (по идентификатору продукта)
        Map<String, Object> product = getFullProductMapByProductID(productID, login, password);
        
        // поиск удаляемой версии в загруженном продукте
        logger.debug("Looking for version in full product map...");
        
        List<Map<String, Object>> versionsList = (List<Map<String, Object>>) product.get("PRODVERLIST");
        for (Map<String, Object> version : versionsList) {
            Long versionID = getLongParam(version, "PRODVERID");
            if (productVersionID.equals(versionID)) {
                
                // найденная версия помечается на удаление
                markAllMapsByKeyValue(version, ROWSTATUS_PARAM_NAME, DELETED_ID);
                logger.debug("Version in full product map found and marked by delete flag.");

                // вызов универсального сохранения (должны удалиться только те сущности, которые были помечены удаляемыми)
                result = doSingleProductUniversalSave(product, login, password);

                break;
            }
        }
        
        if (result == null) {
            logger.debug("Version for deleting not found in full product map - deleting product version skipped, loaded full product map will be returned.");
            result = product;
        } else {
            logger.debug("Deleting product version and all child entities finished.");
        }
        
        return result;
        
    }
    
    // полное удаление элемента структуры продукта (со всеми подчиненными сущностями)
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String, Object> dsB2BProductStructureDeleteEx(Map<String, Object> params) throws Exception {

        logger.debug("Deleting product structure element and all child entities...");
        
        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = null;
        
        Long productStructureID = getLongParam(params, "PRODSTRUCTID");
        logger.debug(String.format("Deleting product structure element with id (PRODSTRUCTID) = %d...", productStructureID));
        
        
        // получение идентификатора продукта (из входных параметров или по сведениям из версии, указанной в элементе структуры продукта)
        Long productID = getLongParam(params, "PRODID");
        Long productVersionID = getLongParam(params, "PRODVERID");
        if ((productID == null) || (productVersionID == null)) {
            logger.debug("No product id (PRODID) or product version id (PRODVERID) was found in call parameters - getting them from linked to product structure element version info...");
            // во входных параметрах идентификатора продукта нет - получение по сведениям из версии, указанной в элементе структуры продукта)
            Map<String, Object> structureParams = new HashMap<String, Object>();
            structureParams.put("PRODSTRUCTID", productStructureID);
            structureParams.put(RETURN_AS_HASH_MAP, true);
            String structureInfoBrowseMethodName = "dsB2BProductStructureBaseBrowseListByParamEx";
            Map<String, Object> structureInfo = this.callService(B2BPOSWS_SERVICE_NAME, structureInfoBrowseMethodName, structureParams, true, login, password);
            productID = checkAndGetEntityID(structureInfo, "PRODID", structureInfoBrowseMethodName);
            productVersionID = checkAndGetEntityID(structureInfo, "PRODVERID", structureInfoBrowseMethodName);
        }
        logger.debug(String.format("This product structure element belong to product with id (PRODID) = %d and to product version with id (PRODVERID) = %d...", productID, productVersionID));
        
        // получение полной мапы продукта универсальной загрузкой (по идентификатору продукта)
        Map<String, Object> product = getFullProductMapByProductID(productID, login, password);
        
        // поиск удаляемого элемента структуры в загруженном продукте
        logger.debug("Looking for structure element in full product map...");
        List<Map<String, Object>> versionsList = (List<Map<String, Object>>) product.get("PRODVERLIST");
        for (Map<String, Object> version : versionsList) {
            Long currentVersionID = getLongParam(version, "PRODVERID");
            if (productVersionID.equals(currentVersionID)) {
                List<Map<String, Object>> structureList = (List<Map<String, Object>>) version.get("PRODSTRUCTLIST");
                for (Map<String, Object> structure : structureList) {
                    Long currentStructureID = getLongParam(structure, "PRODSTRUCTID");
                    if (productStructureID.equals(currentStructureID)) {

                        // найденный элемент структуры помечается на удаление
                        markAllMapsByKeyValue(structure, ROWSTATUS_PARAM_NAME, DELETED_ID);
                        logger.debug("Structe element in full product map found and marked by delete flag.");

                        // вызов универсального сохранения (должны удалиться только те сущности, которые были помечены удаляемыми)
                        result = doSingleProductUniversalSave(product, login, password);

                        break;
                    }
                }
                break;
            }
        }
        
        if (result == null) {
            logger.debug("Product structure element for deleting not found in full product map - deleting product structure element skipped, loaded full product map will be returned.");
            result = product;
        } else {
            logger.debug("Deleting product structure element and all child entities finished.");
        }
        
        return result;
        
    }

    // получение полной мапы продукта универсальной загрузкой (по идентификатору продукта)
    private Map<String, Object> getFullProductMapByProductID(Long productID, String login, String password) throws Exception {
        logger.debug("Getting full product map...");
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODID", productID);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductUniversalLoad", productParams, login, password);
        markAllMapsByKeyValue(product, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        logger.debug("Getting full product map finished.");
        return product;
    }

    // вызов универсального сохранения для одного продукта, с подготовкой требующегося для dsB2BProductUniversalSave списка (PRODLIST)
    private Map<String, Object> doSingleProductUniversalSave(Map<String, Object> product, String login, String password) throws Exception {
        List<Map<String, Object>> productList = new ArrayList<Map<String, Object>>();
        productList.add(product);
        Map<String, Object> saveParams = new HashMap<String, Object>();
        saveParams.put("PRODLIST", productList);
        saveParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductUniversalSave", saveParams, login, password);
        return result;
    }
    
    // метод для проверки существующего продукта на полноту сведений и на наличие стандартых ошибок
    @WsMethod(requiredParams = {"PRODID"})
    public Map<String, Object> dsB2BProductFullCheck(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Long productID = getLongParam(params.get("PRODID"));
        logger.debug("Product ID (PRODID): " + productID);
        Long versionID = getLongParam(params.get("PRODVERID"));
        logger.debug("Product version ID (PRODVERID): " + versionID);

        return doProductFullCheck(productID, versionID, login, password);
    }

    private Map<String, Object> doProductFullCheck(Long productID, Long versionID, String login, String password) throws Exception {
        Map<String, Object> product = getProduct(productID, versionID, login, password);
        Map<String, Object> result = doProductFullCheck(product, versionID);
        return result;
    }

    // todo: возможно, перенести в Base-фасад
    private String makeIndentedTextLogBlock(Object object) {
        return makeIndentedTextLogBlock(object, null, "", new StringBuilder()).toString();
    }

    // todo: возможно, перенести в Base-фасад
    private String makeIndentedTextLogBlock(String objectName, Object object) {
        return makeIndentedTextLogBlock(object, null, objectName, new StringBuilder()).toString();
    }

    // todo: возможно, перенести в Base-фасад
    private StringBuilder makeIndentedTextLogBlock(Object object, String indent, String objectName, StringBuilder sb) {
        if (objectName == null) {
            objectName = "";
        }
        if (indent != null) {
            indent = indent + "\t";
            sb.append("\n");
        } else {
            indent = "";
        }
        sb.append(indent).append(objectName).append(" [");
        if (object == null) {
            sb.append("?]: null");
        } else {
            sb.append(object.getClass().getName()).append("]: ");
            if (object instanceof Map) {
                Map<String, Object> objectAsMap = (Map<String, Object>) object;
                for (Map.Entry<String, Object> entry : objectAsMap.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    makeIndentedTextLogBlock(value, indent, key, sb);
                }
                sb.append("\n");
            } else if (object instanceof List) {
                List<Object> objectAsList = (List<Object>) object;
                for (int i = 0; i < objectAsList.size(); i++) {
                    Object item = objectAsList.get(i);
                    makeIndentedTextLogBlock(item, indent, Integer.toString(i), sb);
                }
                sb.append("\n");
            } else {
                sb.append(object.toString());
            }
        }
        return sb;
    }

    private Map<String, Object> getProduct(Long productID, Long versionID, String login, String password) throws Exception {
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODID", productID);
        if (versionID != null) {
            productParams.put("PRODVERID", versionID);
        }
        productParams.put("LOADALLDATA", 1L);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductUniversalLoad", productParams, login, password);
        return product;
    }

    private Map<String, Object> doProductFullCheck(Map<String, Object> product) {
        return doProductFullCheck(product, null);
    }

    private Map<String, Object> doProductFullCheck(Map<String, Object> product, Long versionID) {

        initRuleMaps();

        List<String> errorsList = new ArrayList<String>();
        List<String> messagesList = new ArrayList<String>();

        //logger.debug(makeIndentedTextLogBlock("Checked product", product));
        // проверка списка версий
        checkVersionList(product, versionID, errorsList);

        //errorsList.add("Полная проверка продукта не реализована"); // !только для отладки!
        if (errorsList.isEmpty()) {
            messagesList.add("Проверка продукта не выявила ошибок");
        }

        Map<String, Object> checkResult = new HashMap<String, Object>();
        checkResult.put("ERRORSLIST", errorsList);
        checkResult.put("MESSAGESLIST", messagesList);

        return checkResult;

    }

    private List<Map<String, Object>> getList(Map<String, Object> listParent, String listKey) {
        logger.debug(String.format("Getting product information list from %s...", listKey));
        List<Map<String, Object>> list = (List<Map<String, Object>>) listParent.get(listKey);
        if (list == null) {
            list = new ArrayList<Map<String, Object>>();
        }
        return list;
    }

    private List<Map<String, Object>> getListWithCheck(Map<String, Object> listParent, String listKey, String listErrName, List<String> errorsList) {
        List<Map<String, Object>> list = getList(listParent, listKey);
        logger.debug(String.format("Checking product information list from %s...", listKey));
        logger.debug(String.format("Product information list description: '%s'.", listErrName));
        if (list.isEmpty()) {
            addTextToListAndLog(errorsList, listErrName + " отсутствует или пуст.");
        }
        return list;
    }

    private void сheckDuplicates(List<Map<String, Object>> list, List<String> uniqueCheckKeys, String listErrName, List<String> errorsList) {

        logger.debug("Checking for duplicates...");
        logger.debug("Check by keys: " + uniqueCheckKeys);

        if (!list.isEmpty()) {

            Map<String, Object> itemsTreeMap = new HashMap<String, Object>();
            ArrayList<Map<String, Object>> duplicatesList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> item : list) {
                Map<String, Object> currentMap = itemsTreeMap;

                for (String uniqueCheckKey : uniqueCheckKeys) {
                    Object value = item.get(uniqueCheckKey);
                    String valueStr = null;
                    if (value != null) {
                        valueStr = value.toString();
                    }
                    Map<String, Object> newMap = (Map<String, Object>) currentMap.get(valueStr);
                    if (newMap == null) {
                        newMap = new HashMap<String, Object>();
                        currentMap.put(valueStr, newMap);
                    }
                    currentMap = newMap;
                }

                List<Map<String, Object>> itemsList = (List<Map<String, Object>>) currentMap.get("ITEMSLIST");
                if (itemsList == null) {
                    itemsList = new ArrayList<Map<String, Object>>();
                    currentMap.put("ITEMSLIST", itemsList);
                }
                itemsList.add(item);
                if (itemsList.size() == 2) {
                    Map<String, Object> duplicateInfo = new HashMap<String, Object>();
                    for (String uniqueCheckKey : uniqueCheckKeys) {
                        duplicateInfo.put(uniqueCheckKey, item.get(uniqueCheckKey));
                    }
                    duplicateInfo.put("DUPLICATESLIST", itemsList);
                    duplicatesList.add(duplicateInfo);
                }
            }

            //logger.debug(makeIndentedTextLogBlock("itemsTreeMap", itemsTreeMap));
            //logger.debug(makeIndentedTextLogBlock("duplicatesList", duplicatesList));
            for (Map<String, Object> duplicateInfo : duplicatesList) {
                List<Map<String, Object>> itemsList = (List<Map<String, Object>>) duplicateInfo.get("DUPLICATESLIST");
                StringBuilder errorText = new StringBuilder();
                errorText.append(listErrName).append(" содержит дублирующиеся записи");
                if (itemsList != null) {
                    errorText.append(" (").append(Integer.toString(itemsList.size())).append(" шт.)");
                }
                errorText.append(", с одинаковой комбинацией значений ключевых полей (");
                for (String uniqueCheckKey : uniqueCheckKeys) {
                    String valueStr = getStringParam(duplicateInfo.get(uniqueCheckKey));
                    if (valueStr.isEmpty()) {
                        valueStr = "< Не указано >";
                    }
                    errorText.append(uniqueCheckKey).append(" = ").append(valueStr).append("; ");
                }
                errorText.setLength(errorText.length() - 2);
                errorText.append(").");
                addTextToListAndLog(errorsList, errorText.toString());
            }
        }
        logger.debug("Checking for duplicates finished.");

    }

    private void addTextToListAndLog(List<String> errorsList, String errorText) {
        errorsList.add(errorText);
        logger.debug(errorText);
    }

    // проверка списка версий c последующей проверкой всех версий из списка
    private void checkVersionList(Map<String, Object> product, List<String> errorsList) {
        checkVersionList(product, null, errorsList);
    }

    // проверка списка версий и если передан versionID - c последующей проверкой только указанной версии из списка
    private void checkVersionList(Map<String, Object> product, Long versionID, List<String> errorsList) {
        List<Map<String, Object>> versionList = getListWithCheck(product, "PRODVERLIST", "Список версий", errorsList);
        for (Map<String, Object> version : versionList) {
            // проверка версии
            Long currentVersionID = getLongParam(version.get("PRODVERID"));
            if ((versionID == null) || (versionID.equals(currentVersionID))) {
                // проверка каждой версии или только указанной
                checkVersion(version, errorsList);
            }
        }
        logger.debug("Checking product information list finished.");
    }

    // проверка версии
    private void checkVersion(Map<String, Object> version, List<String> errorsList) {

        List<Map<String, Object>> configList = getListWithCheck(version, "PRODCONFLIST", "Список конфигураций версии", errorsList);
        for (Map<String, Object> config : configList) {
            checkConfig(config, version, errorsList);
        }

        // проверка списка структур
        checkStructList(version, errorsList);

        // проверка списка каналов продаж
        checkSaleChanList(version, errorsList);

        // проверка списка вариантов оплаты
        checkPayVarList(version, errorsList);

        // проверка списка программ
        checkProgList(version, errorsList);

    }

    // проверка списка структур
    private void checkStructList(Map<String, Object> version, List<String> errorsList) {
        String listName = "Список структур страхового продукта";
        List<Map<String, Object>> structList = getListWithCheck(version, "PRODSTRUCTLIST", listName, errorsList);

        // проверка уникальности записей в списке
        List<String> uniqueCheckKeys = new ArrayList<String>();
        uniqueCheckKeys.add("SYSNAME");
        сheckDuplicates(structList, uniqueCheckKeys, listName, errorsList);

        for (Map<String, Object> struct : structList) {
            checkStruct(struct, errorsList);
        }
        logger.debug("Checking product information list finished.");
    }

    // проверка списка каналов продаж
    private void checkSaleChanList(Map<String, Object> version, List<String> errorsList) {
        List<Map<String, Object>> saleChanList = getListWithCheck(version, "PRODSALESCHANLIST", "Список каналов продаж", errorsList);
        for (Map<String, Object> saleChan : saleChanList) {
            checkSaleChan(saleChan, errorsList);
        }
        logger.debug("Checking product information list finished.");
    }

    // проверка списка вариантов оплаты
    private void checkPayVarList(Map<String, Object> version, List<String> errorsList) {
        List<Map<String, Object>> payVarList = getListWithCheck(version, "PRODPAYVARLIST", "Список вариантов оплаты", errorsList);
        for (Map<String, Object> payVar : payVarList) {
            checkPayVar(payVar, errorsList);
        }
        logger.debug("Checking product information list finished.");
    }

    // проверка списка программ
    private void checkProgList(Map<String, Object> version, List<String> errorsList) {
        String listName = "Список программ";
        List<Map<String, Object>> progList = getListWithCheck(version, "PRODPROGLIST", listName, errorsList);

        // проверка уникальности записей в списке
        List<String> uniqueCheckKeys = new ArrayList<String>();
        uniqueCheckKeys.add("SYSNAME");
        сheckDuplicates(progList, uniqueCheckKeys, listName, errorsList);

        for (Map<String, Object> prog : progList) {
            checkProg(prog, errorsList);
        }
        logger.debug("Checking product information list finished.");
    }

    // проверка конфига
    private void checkConfig(Map<String, Object> config, Map<String, Object> version, List<String> errorsList) {

        // проверка списка констант (значений по умолчанию)
        checkDefValList(config, errorsList);

        // проверка списка показателей
        checkValuesList(config, version, errorsList);

        // проверка списка методов нумерации
        checkNumMethodList(config, errorsList);

        // проверка списка валют премии
        checkPremCurList(config, errorsList);

        // проверка списка валют страховой суммы
        checkInsAmCurList(config, errorsList);

        // проверка списка сроков
        checkTermList(config, errorsList);

    }

    // проверка списка констант (значений по умолчанию)
    private void checkDefValList(Map<String, Object> config, List<String> errorsList) {
        String listName = "Список констант (значений по умолчанию)";
        List<Map<String, Object>> defValList = getListWithCheck(config, "PRODDEFVALLIST", listName, errorsList);

        // проверка уникальности записей в списке
        List<String> uniqueCheckKeys = new ArrayList<String>();
        uniqueCheckKeys.add("NAME");
        сheckDuplicates(defValList, uniqueCheckKeys, listName, errorsList);

        for (Map<String, Object> defVal : defValList) {
            checkDefVal(defVal, errorsList);
        }
        logger.debug("Checking product information list finished.");
    }

    // проверка списка показателей
    private void checkValuesList(Map<String, Object> config, Map<String, Object> version, List<String> errorsList) {
        String listName = "Список показателей";
        List<Map<String, Object>> valuesList = getList(config, "PRODVALUELIST");

        // проверка уникальности записей в списке
        List<String> uniqueCheckKeys = new ArrayList<String>();
        uniqueCheckKeys.add("PRODSTRUCTID");
        uniqueCheckKeys.add("NAME");
        сheckDuplicates(valuesList, uniqueCheckKeys, listName, errorsList);

        logger.debug("Checking list's elements content...");
        for (Map<String, Object> value : valuesList) {
            checkValue(value, listName, errorsList);
        }
        logger.debug("Checking list's elements finished.");

        // 
        checkValuesLinkedHandbooks(valuesList, config, version, listName, errorsList);

        logger.debug("Checking product information list finished.");
    }

    private void addValueErrorTextToListAndLog(List<String> errorsList, String listName, String errorText, Map<String, Object> value) {
        Long id = getLongParam(value.get("PRODVALUEID"));
        String name = getStringParam(value.get("NAME"));
        addTextToListAndLog(errorsList, String.format("%s содержит запись (PRODVALUEID = %d, NAME = %s) %s.", listName, id, name, errorText));
    }

    private String getStringParamOrNotSpecifiedStr(Map<String, Object> map, String keyName) {
        String beanStr;
        if (map == null) {
            beanStr = NOT_SPECIFIED_STR;
        } else {
            beanStr = getStringParamOrNotSpecifiedStr(map.get(keyName));
        }
        return beanStr;
    }

    private String getStringParamOrNotSpecifiedStr(Object bean) {
        String beanStr = getStringParam(bean);
        if (beanStr.isEmpty()) {
            beanStr = NOT_SPECIFIED_STR;
        }
        return beanStr;
    }

    // перенесено в B2BBaseFacade
    /*
    private boolean checkIsValueInvalidByRegExp(Object value, String regExp) {
        boolean allowNull = false;
        return !checkIsValueValidByRegExp(value, regExp, allowNull);
    }

    private boolean checkIsValueValidByRegExp(Object value, String regExp, boolean allowNull) {
        boolean result;
        if (value == null) {
            result = allowNull;
        } else {
            Pattern pattern = Pattern.compile(regExp);
            String checkedString = getStringParam(value);
            Matcher matcher = pattern.matcher(checkedString);
            result = matcher.matches();
            logger.debug("Checking value '" + checkedString + "' by regular expression '" + regExp + "' returned " + result + ".");
        }
        return result;
    }
    */

    private void checkValuesLinkedHandbooks(List<Map<String, Object>> valuesList, Map<String, Object> config, Map<String, Object> version, String listName, List<String> errorsList) {

        logger.debug(makeIndentedTextLogBlock("config", config));

        // флаг наличия показателей, связанных с расширенными атрибутами договора
        boolean isExtHB = false;

        // набор идентификаторов структур, связанных с показателями
        Set<Long> structSet = new HashSet<Long>();
        for (Map<String, Object> value : valuesList) {
            Long discriminator = getLongParam(value, "DISCRIMINATOR");
            Long structID = getLongParam(value, "PRODSTRUCTID");
            if (VALUE_DISCRIMINATOR_STRUCTURE.equals(discriminator)) {
                // проверка для PRODSTRUCTID - не должен быть null или 0
                if ((structID == null) || (structID == 0L)) {
                    String errorText = String.format(
                            "объявленную как атрибут структуры (DISCRIMINATOR = %d), но не ссылающуюся на конкретный элемент структуры (PRODSTRUCTID не указан или равен нулю)",
                            discriminator
                    );
                    addValueErrorTextToListAndLog(errorsList, listName, errorText, value);
                }
                structSet.add(structID);
            } else if (VALUE_DISCRIMINATOR_CONTRACT.equals(discriminator)) {
                // проверка для PRODSTRUCTID - должен быть null или 0
                if ((structID != null) && (structID != 0L)) {
                    String errorText = String.format(
                            "объявленную как расширенный атрибут договора (DISCRIMINATOR = %d), но ссылающуюся на конкретный элемент структуры (PRODSTRUCTID = %d)",
                            discriminator, structID
                    );
                    addValueErrorTextToListAndLog(errorsList, listName, errorText, value);
                }
                // есть показатели, связанные с расширенными атрибутами договора
                isExtHB = true;
            } else {
                // неверный DISCRIMINATOR - должен быть равен 1 или 2
                String discriminatorStr = getStringParamOrNotSpecifiedStr(discriminator);
                String errorText = String.format("с некорректным значением типа записи (DISCRIMINATOR = %s)", discriminatorStr);
                addValueErrorTextToListAndLog(errorsList, listName, errorText, value);
            }
        }

        // список структур
        List<Map<String, Object>> structList = getList(version, "PRODSTRUCTLIST");
        // мапа описаний полей справочника (ключ - PRODSTRUCTID, значение - мапа описаний полей (ключ - имя поля, значение - описание поля))
        Map<Long, Map<String, Map<String, Object>>> structHBPropDescrMap = new HashMap<Long, Map<String, Map<String, Object>>>();
        // мапа имен связанных с элементами структуры справочников (ключ - PRODSTRUCTID, значение - имя справочника)
        Map<Long, String> structHBNamesMap = new HashMap<Long, String>();

        // подготовка проверочных мап - с описанием полей (с доступом по ключам PRODSTRUCTID и имени поля) и с именами справочников (по ключу PRODSTRUCTID)
        if (structList != null) {
            for (Map<String, Object> struct : structList) {
                Long structID = getLongParam(struct, "PRODSTRUCTID");
                //logger.debug("PRODSTRUCTID: " + structID);
                String sysName = getStringParam(struct.get("SYSNAME"));
                //logger.debug("SYSNAME: " + sysName);
                Map<String, Object> valuesHB = (Map<String, Object>) struct.get("VALUESHB");
                if (valuesHB == null) {
                    String structListName = "Список структур";
                    String errorText = "без связанного справочника";
                    addTextToListAndLog(errorsList, String.format("%s содержит запись (PRODSTRUCTID = %d, SYSNAME = %s) %s.", structListName, structID, sysName, errorText));
                } else {
                    // мапа описаний полей справочника (ключ - PRODSTRUCTID, значение - описание структуры)
                    Map<String, Map<String, Object>> hbPropDescrMap = getHBPropDescrMapFromList(valuesHB);
                    if (structSet.contains(structID)) {
                        structHBPropDescrMap.put(structID, hbPropDescrMap);
                        //logger.debug(makeIndentedTextLogBlock("hbPropDescrMap", hbPropDescrMap));
                        String hbName = getStringParam(valuesHB, "NAME");
                        structHBNamesMap.put(structID, hbName);
                    }
                }
            }
        }
        //logger.debug(makeIndentedTextLogBlock("struct", structHBPropDescrMap));

        if (isExtHB) {
            // если имеются показатели из расширенных атрибутов договора - необходимо также добавить описание полей связанного справочника в мапу (с ключем PRODSTRUCTID = 0)
            Map<String, Object> valuesHB = (Map<String, Object>) config.get("VALUESHB");
            if (valuesHB == null) {
                addTextToListAndLog(errorsList, String.format("%s содержит записи указывающие на расширенные атрибуты договора, но в конфигурации продукта отсутствует версия справочника для хранения расширенных атрибутов договора.", listName));
            } else {
                Map<String, Map<String, Object>> hbPropDescrMap = getHBPropDescrMapFromList(valuesHB);
                structHBPropDescrMap.put(0L, hbPropDescrMap);
            }
        }

        // проверка показателей по предварительно подготовленной мапе с описанием полей справочников, связанных через элемент структуры с показателем
        for (Map<String, Object> value : valuesList) {
            Long structID = getLongParam(value, "PRODSTRUCTID");
            logger.debug("PRODSTRUCTID: " + structID);
            String name = getStringParam(value, "NAME");
            logger.debug("NAME: " + name);
            if (structID == null) {
                // показатель по договору
                structID = 0L;
            }
            Map<String, Map<String, Object>> hbPropDescrMap = structHBPropDescrMap.get(structID);
            if (hbPropDescrMap == null) {
                String errorText = "не имеющую связи со справочником";
                addValueErrorTextToListAndLog(errorsList, listName, errorText, value);
            } else {
                Map<String, Object> hbPropDescr = hbPropDescrMap.get(name);
                if (hbPropDescr == null) {
                    String hbName = structHBNamesMap.get(structID);
                    String errorText = String.format("не имеющую соответствующего ей поля (%s) в связанном справочнике '%s'", name, hbName);
                    addValueErrorTextToListAndLog(errorsList, listName, errorText, value);
                } else {
                    // если у показателя проблемы с описанием его типа данных - необходимо пропустить проверки на соответствие типа показателя и поля справочника
                    boolean isValueDataTypeValid = getBooleanParam(value.get(DATA_TYPE_VALID_FLAG_KEY_NAME), true);
                    logger.debug(DATA_TYPE_VALID_FLAG_KEY_NAME + ": " + isValueDataTypeValid);
                    if (isValueDataTypeValid) {
                        // тип данных показателя описан корректно - проверка соответствия типа показателя и поля справочника
                        Long dataTypeID = getLongParam(value, "DATATYPEID");
                        String dataTypeStr = getStringParam(value, "DATATYPESTR");
                        //String etalonDataTypeStr = valueDataTypeMap.get(dataTypeID);
                        String etalonHBFieldMask = hbFieldMaskByValueDataTypeMap.get(dataTypeID);
                        if (!etalonHBFieldMask.isEmpty()) {
                            String hbFieldName = getStringParam(hbPropDescr, "STOREPROPNAME");
                            if (checkIsValueInvalidByRegExp(hbFieldName, etalonHBFieldMask)) {
                                String hbName = structHBNamesMap.get(structID);
                                String dataTypeIDStr = getStringParamOrNotSpecifiedStr(dataTypeID);
                                String dataTypeStrStr = getStringParamOrNotSpecifiedStr(dataTypeStr);
                                String errorText = String.format(
                                        "имеющую не соответствующее ей по типу данных (DATATYPEID = %s, DATATYPESTR = %s) поле таблицы БД (%s) в связанном справочнике '%s'",
                                        dataTypeIDStr, dataTypeStrStr, hbFieldName, hbName
                                );
                                addValueErrorTextToListAndLog(errorsList, listName, errorText, value);
                            }
                        }
                    }
                }
            }
        }

    }

    private Map<String, Map<String, Object>> getHBPropDescrMapFromList(Map<String, Object> valuesHB) {
        Map<String, Map<String, Object>> hbPropDescrMap = new HashMap<String, Map<String, Object>>();
        //String hbName = getStringParam(valuesHB, "NAME");
        List<Map<String, Object>> hbPropDescrList = (List<Map<String, Object>>) valuesHB.get("HBPROPDESCRLIST");
        for (Map<String, Object> hbPropDescr : hbPropDescrList) {
            String hbPropName = getStringParam(hbPropDescr, "NAME");
            logger.debug("hbPropName (NAME): " + hbPropName);
            hbPropDescrMap.put(hbPropName, hbPropDescr);
        }
        //hbPropDescrMap.put("$HANDBOOKNAME", hbName);
        return hbPropDescrMap;
    }

    // проверка списка методов нумерации
    private void checkNumMethodList(Map<String, Object> config, List<String> errorsList) {
        List<Map<String, Object>> numMethodList = getListWithCheck(config, "PRODNUMMETHODLIST", "Список методов нумерации", errorsList);
        logger.debug("Checking list's elements content...");
        for (Map<String, Object> numMethod : numMethodList) {
            checkNumMethod(numMethod, errorsList);
        }
        logger.debug("Checking list's elements finished.");
        logger.debug("Checking product information list finished.");
    }

    // проверка списка валют премии
    private void checkPremCurList(Map<String, Object> config, List<String> errorsList) {
        List<Map<String, Object>> premCurList = getListWithCheck(config, "PRODPREMCURLIST", "Список валют премии", errorsList);
        logger.debug("Checking list's elements content...");
        for (Map<String, Object> premCur : premCurList) {
            checkPremCur(premCur, errorsList);
        }
        logger.debug("Checking list's elements finished.");
        logger.debug("Checking product information list finished.");
    }

    // проверка списка валют страховой суммы
    private void checkInsAmCurList(Map<String, Object> config, List<String> errorsList) {
        List<Map<String, Object>> insAmCurList = getListWithCheck(config, "PRODINSAMCURLIST", "Список валют страховой суммы", errorsList);
        logger.debug("Checking list's elements content...");
        for (Map<String, Object> insAmCur : insAmCurList) {
            checkInsAmCur(insAmCur, errorsList);
        }
        logger.debug("Checking list's elements finished.");
        logger.debug("Checking product information list finished.");
    }

    // проверка списка сроков
    private void checkTermList(Map<String, Object> config, List<String> errorsList) {
        //String listName = "Список показателей";
        //List<Map<String, Object>> termList = getListWithCheck(config, "PRODTERMLIST", listName, errorsList);
        List<Map<String, Object>> termList = getList(config, "PRODTERMLIST");
        logger.debug("Checking list's elements content...");
        for (Map<String, Object> term : termList) {
            checkTerm(term, errorsList);
        }
        logger.debug("Checking list's elements finished.");
        logger.debug("Checking product information list finished.");
    }

    private void checkStruct(Map<String, Object> struct, List<String> errorsList) {
        // todo: проверка структуры (если потребуется)
    }

    private void checkSaleChan(Map<String, Object> saleChan, List<String> errorsList) {
        // todo: проверка канала продаж (если потребуется)
    }

    private void checkPayVar(Map<String, Object> prog, List<String> errorsList) {
        // todo: проверка варианта оплаты (если потребуется)
    }

    private void checkProg(Map<String, Object> prog, List<String> errorsList) {
        // todo: проверка программы (если потребуется)
    }

    private void checkDefVal(Map<String, Object> defVal, List<String> errorsList) {
        // todo: проверка константы (значения по умолчанию) (если потребуется)
    }

    // проверка показателя
    private void checkValue(Map<String, Object> value, String listName, List<String> errorsList) {

        Long dataTypeID = getLongParam(value.get("DATATYPEID"));
        String etalonDataTypeStr = this.valueDataTypeMap.get(dataTypeID);

        // флаг корректности описания типа данных для показателя
        // (true - описание полное и непротиворечивое, false - проблемы с описанием типа данных и однозначно определить типа данных для показателя невозможно)
        boolean isDataTypeValid = false;

        if (etalonDataTypeStr == null) {
            // неподдерживаемый или пустой идентификатор типа данных
            String dataTypeIDStr = getStringParamOrNotSpecifiedStr(dataTypeID);
            String errorStr = String.format("с некорректным значением идентификатора типа данных (DATATYPEID = %s)", dataTypeIDStr);
            addValueErrorTextToListAndLog(errorsList, listName, errorStr, value);
        } else {
            String dataTypeStr = getStringParam(value.get("DATATYPESTR"));
            if (etalonDataTypeStr.equals(dataTypeStr)) {
                // описание типа данных полное и непротиворечивое
                isDataTypeValid = true;
            } else {
                // идентификатор типа данных не совпадает со строковым описанием типа данных - однозначно определить типа данных для показателя невозможно
                String errorStr = String.format("с противоречивым описанием типа данных (DATATYPEID = %d и DATATYPESTR = %s)", dataTypeID, getStringParamOrNotSpecifiedStr(dataTypeStr));
                addValueErrorTextToListAndLog(errorsList, listName, errorStr, value);
            }
        }

        // установка временного служебного флага корректности описания типа данных показателя 
        // (применяется для пропуска проверк на соответствие типов в checkValuesLinkedHandbooks, если у показателя проблемы с описанием его типа данных)
        value.put(DATA_TYPE_VALID_FLAG_KEY_NAME, isDataTypeValid);

    }

    private void checkNumMethod(Map<String, Object> numMethod, List<String> errorsList) {
        // todo: проверка метода нумерации (если потребуется)
    }

    private void checkPremCur(Map<String, Object> premCur, List<String> errorsList) {
        // todo: проверка валюты премии (если потребуется)
    }

    private void checkInsAmCur(Map<String, Object> insAmCur, List<String> errorsList) {
        // todo: проверка валюты страховой суммы (если потребуется)
    }

    private void checkTerm(Map<String, Object> term, List<String> errorsList) {
        // todo: проверка срока (если потребуется)
    }

}
