package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import static com.bivgroup.services.b2bposws.system.Constants.INSTARIFICATORWS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsConstants;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author Admin
 */
@BOName("ProductCustom")
public class ProductCustomFacade extends B2BBaseFacade {

    private final Class THIS_FACADE_CLASS = this.getClass();
    private final Logger log = Logger.getLogger(THIS_FACADE_CLASS);
    public static final String B2BPOSWS = "b2bposws";
    public static final String B2BPOSWS_SERVICE_NAME = B2BPOSWS;
    public static final String instarificatorws = INSTARIFICATORWS;

    // имя ключа параметра для активации операций по установке иерархии
    private static final String HIERARCHY = "HIERARCHY";

    // суффикс имен ссылочных полей
    private static final String SUFFIX_RELATION = "ID";
    // префикс имени поля с ИД родительской (по отношению к текущей) записи
    private static final String PREFIX_PARENT = "PARENT";

    // фрагмент имени ключа для узла с дочерними элементами
    // (для получения полного имени - будет добавлен SUFFIX_LIST, поскольку узел содержит список)
    // todo: возможность переопределять параметром при вызове методов dsProductBrowseBy*
    private static final String NODE_CHILD = "CHILD";

    // суффиксы имен ключей, указывающих на карты и списки
    private static final String SUFFIX_MAP = ""; // = "_MAP"; // = ""; // todo: возможность переопределять параметром при вызове методов dsProductBrowseBy*, например, на "MAP"
    private static final String SUFFIX_LIST = "S"; // = "_LIST"; // = "S"; // todo: возможность переопределять параметром при вызове методов dsProductBrowseBy*, например, на "LIST"
    private static final String UNIVERSAL_SUFFIX_MAP = "MAP";
    private static final String UNIVERSAL_SUFFIX_LIST = "LIST";

    // ключ - имя таблицы, значение - имя метода для получения её сведений
    private static Map<String, String> methodsByTable;

    // ключ - имя таблицы, значение - полное имя поля, хранящего ИД родителя (для иерархических сущеностей)
    // если ключ с именем таблицы отсутствует в карте - значит таблица с данными "плоской" сущности
    private static Map<String, String> parentFieldByTable;

    public ProductCustomFacade() {
        super();

        // заполнение карты соответствий имен таблиц именам методов для получения их сведений
        methodsByTable = new HashMap<String, String>();
        methodsByTable.put("PRODCONF", "dsB2BProductConfigBrowseListByParam");
        methodsByTable.put("PRODVER", "dsB2BProductVersionBrowseListByParam");
        methodsByTable.put("PROD", "dsB2BProductBrowseListByParam");
        methodsByTable.put("PRODSTRUCT", "dsB2BProductStructureBaseBrowseListByParamEx");
        methodsByTable.put("PRODPROG", "dsB2BProductProgramBrowseListByParam");
        methodsByTable.put("PRODVALUE", "dsB2BProductValueBaseBrowseListByParam");
        methodsByTable.put("PRODREP", "dsB2BProductReportBrowseListByParamEx");
        methodsByTable.put("REP", "dsB2BReportBrowseListByParam");
        methodsByTable.put("PRODCALCRATERULE", "dsB2BProductCalcRateRuleBrowseListByParam");
        methodsByTable.put("PRODDEFVAL", "dsB2BProductDefaultValueBrowseListByParam");
        methodsByTable.put("PRODSALESCHAN", "dsB2BProductSalesChannelBrowseListByParamEx");

        methodsByTable.put("PRODADDCHT", "dsB2BProductAdditionalChangeTypeBrowseListByParam");
        methodsByTable.put("PRODPOSSVALUE", "dsB2BProductPossibleValueBrowseListByParam");
        methodsByTable.put("PRODPAYVAR", "dsB2BProductPaymentVariantBrowseListByParamEx");
        methodsByTable.put("PAYVAR", "dsB2BPaymentVariantBrowseListByParam");
        methodsByTable.put("PAYVARCNT", "dsB2BPaymentVariantContentBrowseListByParam");
        methodsByTable.put("PRODNUMMETHOD", "dsB2BProductNumMethodBrowseListByParam");
        methodsByTable.put("PRODINSAMCUR", "dsB2BProductInsAmCurrencyBrowseListByParam");
        methodsByTable.put("CURRENCY", "dsB2BProductRefCurrencyBrowseListByParam");
        methodsByTable.put("PRODPREMCUR", "dsB2BProductPremiumCurrencyBrowseListByParam");
        // methodsByTable.put("PRODCALCRATERULE", "dsB2BProductCalcRateRuleBrowseListByParam");
        methodsByTable.put("PRODDISC", "dsB2BProductDiscountBrowseListByParam");
        methodsByTable.put("PRODDISCPROMO", "dsB2BProductDiscountPromoCodeBrowseListByParam");
        methodsByTable.put("PRODDISCVAL", "dsB2BProductDiscountValueBrowseListByParam");
        methodsByTable.put("PRODRIDER", "dsB2BProductRiderBrowseListByParam");
        methodsByTable.put("PRODTERM", "dsB2BProductTermBrowseListByParam");
        methodsByTable.put("TERM", "dsB2BTermBrowseListByParam");
        methodsByTable.put("PRODBINDOC", "dsB2BProductBinaryDocumentBrowseListByParam");

        // заполнение карты соответствий имен таблиц именам полей, хранящих ИД родителя (для таблиц иерархических сущеностей)
        parentFieldByTable = new HashMap<String, String>();
        parentFieldByTable.put("PRODSTRUCT", PREFIX_PARENT + "STRUCT" + SUFFIX_RELATION);

    }

    // метод-посредник для вызова методов с доп. протоколированием и логином/паролем в отдельной параметре-карте
    private Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params, Map<String, Object> authCreds) throws Exception {
        // логин и пароль для вызова других методов веб-сервиса                    
        String login = authCreds.get(LOGIN).toString();
        String password = authCreds.get(PASSWORD).toString();

        // протоколирование вызова
        //long callTimer = System.currentTimeMillis();
        //log.debug("Вызван метод " + methodName + " с параметрами:\n\n" + params.toString() + "\n");
        Map<String, Object> callResult = this.callService(serviceName, methodName, params, login, password);  // todo: управляемое протоколирование

        // протоколирование вызова
        //callTimer = System.currentTimeMillis() - callTimer;
        //log.debug("Метод " + methodName + " выполнился за " + callTimer + " мс. и вернул результат:\n\n" + callResult.toString() + "\n");
        // возвращаем пароль в набор параметров (вызов метода сервиса его изымает из них)
        authCreds.put(PASSWORD, password);

        return callResult;
    }

    // метод-посредник для вызов метода с доп. протоколированием, для сулчаев когда логин/пароль в той же параметре-карте, что и параметры вызова метода
    private Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params) throws Exception {
        return this.callService(serviceName, methodName, params, params);
    }

    public List<Map<String, Object>> callServiceAndGetListFromResultMap(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        return WsUtils.getListFromResultMap(this.callService(serviceName, methodName, params, login, password));
    }

    private void sortByRowStatus(List<Map<String, Object>> source,
            List<Map<String, Object>> inserted,
            List<Map<String, Object>> modified,
            List<Map<String, Object>> deleted,
            List<Map<String, Object>> unModified) {
        logger.debug("sortByRowStatus begin");
        for (Map<String, Object> bean : source) {
            // по умолчанию - данные добавляются
            RowStatus rowStatus = RowStatus.INSERTED;
            // определение типа модификации, если он указан в явном виде в передаваемых данных
            Object rowStatusObj = bean.get(RowStatus.ROWSTATUS_PARAM_NAME);
            if (rowStatusObj != null) {
                rowStatus = RowStatus.getRowStatusById(Integer.parseInt(rowStatusObj.toString()));
            }
            // выбор дополняемого списка по типу модификации
            List<Map<String, Object>> chosenList = null;
            if (rowStatus.equals(RowStatus.INSERTED)) {
                chosenList = inserted;
            } else if (rowStatus.equals(RowStatus.MODIFIED)) {
                chosenList = modified;
            } else if (rowStatus.equals(RowStatus.DELETED)) {
                chosenList = deleted;
            } else if (rowStatus.equals(RowStatus.UNMODIFIED)) {
                chosenList = unModified;
            }
            // дополнение выбранного списка (если был передан)
            if (chosenList != null) {
                chosenList.add(bean);
            }
        }
        logger.debug("sortByRowStatus end");
    }

    private Map<String, Object> cloneMapWithoutLists(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : map.keySet()) {
            if (!key.endsWith(UNIVERSAL_SUFFIX_LIST)) {
                result.put(key, map.get(key));
            }
        }
        return result;
    }

    private Map<String, Object> findParentInPlainList(List<Map<String, Object>> plainList, Map<String, Object> child, String parentIDKey, String childIDKey) {
        //String parentIDKey = PREFIX_PARENT + "STRUCT" + SUFFIX_RELATION;
        //String childIDKey = "PRODSTRUCT" + SUFFIX_RELATION;
        Object idObj = child.get(parentIDKey);
        if (idObj == null) {
            return null;
        }
        long id = ((Long) idObj);
        for (Map<String, Object> element : plainList) {
            long elementID = ((Long) element.get(childIDKey));
            if (elementID == id) {
                return element;
            }
        }
        return null;
    }

    private List<Map<String, Object>> setHierarchy(List<Map<String, Object>> listNode, final String tableName) {
        Object parentIDKeyObj = parentFieldByTable.get(tableName);
        if ((parentIDKeyObj == null) || (listNode == null) || listNode.isEmpty()) {
            return null;
        }
        String parentIDKey = parentIDKeyObj.toString();
        String childIDKey = tableName + SUFFIX_RELATION;
        String childsNodeKey = NODE_CHILD + SUFFIX_LIST;

        for (Map<String, Object> element : listNode) {
            element.put(childsNodeKey, new ArrayList<Map<String, Object>>());
        }

        for (Map<String, Object> element : listNode) {
            Map<String, Object> parent = findParentInPlainList(listNode, element, parentIDKey, childIDKey);
            if (parent != null) {
                List<Map<String, Object>> parentChilds = (List<Map<String, Object>>) parent.get(childsNodeKey);
                parentChilds.add(element);
            }
        }

        List<Map<String, Object>> newTree = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> element : listNode) {
            if (element.get(parentIDKey) == null) {
                newTree.add(element);
            }
        }
        return newTree;
    }

    // метод для развертывания связи сущностей
    private void joinEntities(Map<String, Object> rootEntity, String parentNodePath, String childsTable, Map<String, Object> globalParams, String suffixMap, String suffixList) throws Exception {
        // флаг установки иерархии - активируется только по ключу из параметров
        // todo: определение флага выполняется при каждом вызове joinEntities, желательно же - единожды на вызов dsProductBrowseBy*
        Boolean setHierarchy = false;
        Object hierarchy = globalParams.get(HIERARCHY);
        if ((hierarchy != null) && ((Boolean) hierarchy == true)) {
            setHierarchy = true;
        }

        // определение ссылки на объект в который необходимо добавить новые данные
        Map<String, Object> insertNode = rootEntity;
        String[] parentNodes = parentNodePath.split("\\.");
        List<Map<String, Object>> insertNodes = null;
        if (!parentNodePath.isEmpty()) {
            for (String table : parentNodes) {
                Object nextInsertNode = insertNode.get(table);
                if (nextInsertNode == null) {
                    nextInsertNode = insertNode.get(table + suffixMap);
                }
                if (nextInsertNode == null) {
                    nextInsertNode = insertNode.get(table + suffixList);
                }
                if (nextInsertNode instanceof Map) {
                    // обновляемый узел - единичный
                    insertNode = (Map<String, Object>) nextInsertNode;
                } else {
                    // обновляемый узел содержит список - вставить данные необходимо в каждый из элементов
                    insertNodes = (ArrayList<Map<String, Object>>) nextInsertNode;
                }
            }
        }
        // если обновляемый узел - единичный, то для универсального обхода требуется список из одного элемента
        if (insertNodes == null) {
            insertNodes = new ArrayList<Map<String, Object>>();
            insertNodes.add(insertNode);
        }

        // имя вызываемого для получения данных из БД метода
        String getChildMethod = methodsByTable.get(childsTable);

        // обход всех элементов (если обновляемый объект - список) или одного элемента под видом списка (если обновляемый объект - узел)
        for (Map<String, Object> node : insertNodes) {

            // если родитель содержит ссылку на дочернюю сущность
            String relationFieldFull = childsTable + SUFFIX_RELATION;
            Object idObj = node.remove(relationFieldFull);
            boolean isList = false;

            // если родитель не содержит ссылку на дочернюю сущность
            if (idObj == null) {
                relationFieldFull = parentNodes[parentNodes.length - 1] + SUFFIX_RELATION;
                idObj = node.get(relationFieldFull);
                isList = true;
            }

            // получение добавляемых данных
            Map<String, Object> callParams = new HashMap<String, Object>();
            callParams.put(relationFieldFull, idObj);
            Map<String, Object> rawCallResult = this.callService(B2BPOSWS, getChildMethod, callParams, globalParams);

            // обновление объекта полученными сведениями
            if (isList) {
                List<Map<String, Object>> joinedList = WsUtils.getListFromResultMap(rawCallResult);
                // если получен список и включен соответвующий режим - то дополнительно выполняется попытка установки иерархии
                if (setHierarchy) {
                    setHierarchy(joinedList, childsTable);
                }
                node.put(childsTable + suffixList, joinedList);
            } else {
                Map<String, Object> joinedMap = WsUtils.getFirstItemFromResultMap(rawCallResult);
                node.put(childsTable + suffixMap, joinedMap);
            }
        }
    }

    @WsMethod(requiredParams = {"PRODVERID"})
    public Map<String, Object> dsB2BProductStructTreeLoad(Map<String, Object> params) throws Exception {
        Map<String, Object> rawCallResult = this.callService(B2BPOSWS, "dsB2BProductStructureBaseBrowseListByParam", params);
        List<Map<String, Object>> structList = WsUtils.getListFromResultMap(rawCallResult);
        rawCallResult.put(RESULT, setHierarchy(structList, "PRODSTRUCT"));
        return rawCallResult;
    }

    private void joinEntities(Map<String, Object> rootEntity, String parentNodePath, String childsTable, Map<String, Object> globalParams) throws Exception {
        joinEntities(rootEntity, parentNodePath, childsTable, globalParams, SUFFIX_MAP, SUFFIX_LIST);
    }

    private void joinEntitiesUniversal(Map<String, Object> rootEntity, String parentNodePath, String childsTable, Map<String, Object> globalParams) throws Exception {
        joinEntities(rootEntity, parentNodePath, childsTable, globalParams, UNIVERSAL_SUFFIX_MAP, UNIVERSAL_SUFFIX_LIST);
    }

    private void loadProductDamageCatContent(Map<String, Object> prodDamageCatMap, String login, String password) throws Exception {
        Long prodDamageCatId = Long.valueOf(prodDamageCatMap.get("PRODDAMAGECATID").toString());
        Map<String, Object> prodDamagCatCntParams = new HashMap<String, Object>();
        prodDamagCatCntParams.put("PRODDAMAGECATID", prodDamageCatId);
        List<Map<String, Object>> prodDamagCatCntList = this.callServiceAndGetListFromResultMap(Constants.B2BPOSWS, "dsB2BProductDamageCategoryContentBrowseListByParam",
                prodDamagCatCntParams, login, password);
        if ((prodDamagCatCntList != null) && (prodDamagCatCntList.size() > 0)) {
            prodDamageCatMap.put("PRODDAMAGECATCNTS", prodDamagCatCntList);
            for (Map<String, Object> bean : prodDamagCatCntList) {
                if (bean.get("INSEVENTID") != null) {
                    Map<String, Object> insEventParams = new HashMap<String, Object>();
                    insEventParams.put(RETURN_AS_HASH_MAP, "TRUE");
                    insEventParams.put("INSEVENTID", bean.get("INSEVENTID"));
                    Map<String, Object> insEventMap = this.callService(Constants.B2BPOSWS, "dsB2BInsuranceEventBrowseListByParam", insEventParams, login, password);
                    bean.put("INSEVENT", insEventMap);
                }
            }
        }
    }

    private List<Map<String, Object>> loadHbStruct(Object hbDataVerId, String login, String password) throws Exception {
        Map<String, Object> hbDataVerParam = new HashMap<String, Object>();
        hbDataVerParam.put(RETURN_AS_HASH_MAP, "TRUE");
        hbDataVerParam.put("HBDATAVERID", hbDataVerId);
        Map<String, Object> hbDataVerMap = this.callService(Constants.INSTARIFICATORWS, "dsHandbookDataVersionBrowseListByParam", hbDataVerParam, login, password);
        if ((hbDataVerMap != null) && (hbDataVerMap.get("HBDESCRID") != null)) {
            Map<String, Object> hbPropDescrParam = new HashMap<String, Object>();
            hbPropDescrParam.put("HBDESCRID", hbDataVerMap.get("HBDESCRID"));
            return this.callServiceAndGetListFromResultMap(Constants.INSTARIFICATORWS, "dsHandbookPropertyDescriptorBrowseListByParam", hbPropDescrParam, login, password);
        }
        return null;
    }

    private void loadDamageCat(Map<String, Object> prodDamageCatMap, String login, String password) throws Exception {
        Long damageCatId = Long.valueOf(prodDamageCatMap.get("DAMAGECATID").toString());
        Map<String, Object> damageCatParams = new HashMap<String, Object>();
        damageCatParams.put(RETURN_AS_HASH_MAP, "TRUE");
        damageCatParams.put("DAMAGECATID", damageCatId);
        Map<String, Object> damageCatMap = this.callService(Constants.B2BPOSWS, "dsB2BDamageCategoryBrowseListByParam",
                damageCatParams, login, password);
        if (damageCatMap != null) {
            prodDamageCatMap.put("DAMAGECAT", damageCatMap);
            if (damageCatMap.get("INSEVENTID") != null) {
                Map<String, Object> insEventParams = new HashMap<String, Object>();
                insEventParams.put(RETURN_AS_HASH_MAP, "TRUE");
                insEventParams.put("INSEVENTID", damageCatMap.get("INSEVENTID"));
                Map<String, Object> insEventMap = this.callService(Constants.B2BPOSWS, "dsB2BInsuranceEventBrowseListByParam", insEventParams, login, password);
                damageCatMap.put("INSEVENT", insEventMap);
            }
            if (damageCatMap.get("DETAILHBDATAVERID") != null) {
                damageCatMap.put("DETAILSTRUCT", loadHbStruct(damageCatMap.get("DETAILHBDATAVERID"), login, password));
            }
            if (damageCatMap.get("VALUEHBDATAVERID") != null) {
                damageCatMap.put("VALUESTRUCT", loadHbStruct(damageCatMap.get("VALUEHBDATAVERID"), login, password));
            }
        }
    }

    private void loadProductDamageCatData(Map<String, Object> product, Map<String, Object> globalParams) throws Exception {
        String login = globalParams.get(LOGIN).toString();
        String password = globalParams.get(PASSWORD).toString();
        Map<String, Object> prodVerMap = (Map<String, Object>) product.get("PRODVER");
        if (prodVerMap != null) {
            Long prodVerId = Long.valueOf(prodVerMap.get("PRODVERID").toString());
            Map<String, Object> prodDamagCatParams = new HashMap<String, Object>();
            prodDamagCatParams.put("PRODVERID", prodVerId);
            List<Map<String, Object>> prodDamagCatList = this.callServiceAndGetListFromResultMap(Constants.B2BPOSWS, "dsB2BProductDamageCategoryBrowseListByParam",
                    prodDamagCatParams, login, password);
            if ((prodDamagCatList != null) && (prodDamagCatList.size() > 0)) {
                prodVerMap.put("PRODDAMAGECATS", prodDamagCatList);
                for (Map<String, Object> bean : prodDamagCatList) {
                    loadProductDamageCatContent(bean, login, password);
                    loadDamageCat(bean, login, password);
                }
            }
        }
    }

    private void populateProduct(Map<String, Object> product, Map<String, Object> globalParams) throws Exception {
        // наполнение корневой сущности (конфигурации продукта) сведениями из других сущностей
        joinEntities(product, "PRODCONF", "PRODVER", globalParams);
        joinEntities(product, "PRODCONF.PRODVER", "PROD", globalParams);
        joinEntities(product, "PRODCONF.PRODVER", "PRODSTRUCT", globalParams);
        joinEntities(product, "PRODCONF.PRODVER.PRODSTRUCT", "PRODVALUE", globalParams);
        joinEntities(product, "PRODCONF.PRODVER", "PRODPROG", globalParams);
        joinEntities(product, "PRODCONF", "PRODVALUE", globalParams);
        joinEntities(product, "PRODCONF", "PRODREP", globalParams);
        joinEntities(product, "PRODCONF.PRODREP", "REP", globalParams);
        joinEntities(product, "PRODCONF", "PRODCALCRATERULE", globalParams);
        /// добавлено для выгрузки
        joinEntities(product, "PRODCONF", "PRODDEFVAL", globalParams);
        joinEntities(product, "PRODCONF.PRODVER", "PRODSALESCHAN", globalParams);
        joinEntities(product, "PRODCONF.PRODVER.PRODSALESCHAN", "PRODSALESCHAN", globalParams);

        boolean isLoadAllData = (globalParams.get("LOADALLDATA") != null) && (Long.valueOf(globalParams.get("LOADALLDATA").toString()).longValue() == 1);
        if (isLoadAllData || (globalParams.get("LOADDAMAGECAT") != null) && (Long.valueOf(globalParams.get("LOADDAMAGECAT").toString()).longValue() == 1)) {
            loadProductDamageCatData(product, globalParams);
        }
        // загружаем дополнительные таблицы (для полной загрузки)
        if (isLoadAllData) {
            joinEntities(product, "PRODCONF.PRODVALUE", "PRODPOSSVALUE", globalParams);
            joinEntities(product, "PRODCONF.PRODVER", "PRODPAYVAR", globalParams);
            Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER"); // product = PRODCONF
            if (prodVer != null) {
                joinEntities(prodVer, "PRODPAYVAR", "PAYVAR", globalParams);
                List<Map<String, Object>> prodPayVarList = (List<Map<String, Object>>) prodVer.get("PRODPAYVAR" + SUFFIX_LIST);
                if (prodPayVarList != null) {
                    for (Map<String, Object> prodPayVarBean : prodPayVarList) {
                        joinEntities(prodPayVarBean, "PAYVAR", "PAYVARCNT", globalParams);
                    }
                }
            }
            joinEntities(product, "PRODCONF", "PRODADDCHT", globalParams);
            joinEntities(product, "PRODCONF", "PRODNUMMETHOD", globalParams);
            joinEntities(product, "PRODCONF", "PRODINSAMCUR", globalParams);
            joinEntities(product, "PRODCONF.PRODINSAMCUR", "CURRENCY", globalParams);
            joinEntities(product, "PRODCONF", "PRODPREMCUR", globalParams);
            joinEntities(product, "PRODCONF.PRODPREMCUR", "CURRENCY", globalParams);
            joinEntities(product, "PRODCONF", "PRODCALCRATERULE", globalParams);
            joinEntities(product, "PRODCONF", "PRODDISC", globalParams);
            joinEntities(product, "PRODCONF.PRODDISC", "PRODDISCPROMO", globalParams);
            joinEntities(product, "PRODCONF.PRODDISC", "PRODDISCVAL", globalParams);
            joinEntities(product, "PRODCONF.PRODVER.PROD", "PRODRIDER", globalParams);
            joinEntities(product, "PRODCONF", "PRODTERM", globalParams);
            //joinEntities(product, "PRODCONF.TERM", "TERM", globalParams); // wtf?!
            joinEntities(product, "PRODCONF.PRODTERM", "TERM", globalParams);
            joinEntities(product, "PRODCONF", "PRODBINDOC", globalParams);
        }
        // в итоговую мапу продукта дополнительно добавляется значение флага вызова (чтоб по результату можно было определить был ли вызов с загрузкой всех данных)
        product.put("LOADALLDATA", globalParams.get("LOADALLDATA"));
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsProductBrowseByParams(Map<String, Object> params) throws Exception {
        Map<String, Object> rawCallResult = this.callService(B2BPOSWS, methodsByTable.get("PRODCONF"), params);
        Map<String, Object> product = WsUtils.getFirstItemFromResultMap(rawCallResult);
        populateProduct(product, params);
        return product;
    }

    @WsMethod(requiredParams = {"PRODCONFID"})
    public Map<String, Object> dsProductBrowseByID(Map<String, Object> params) throws Exception {
        Map<String, Object> product = new HashMap<String, Object>();
        product.put("PRODCONFID", params.get("PRODCONFID"));
        joinEntities(product, "", "PRODCONF", params);
        populateProduct(product, params);
        return (Map<String, Object>) product.get("PRODCONF");
    }

    // для вызова с протоколированием
    @WsMethod(requiredParams = {"X"})
    public Map<String, Object> dsProductBrowseByXLog(Map<String, Object> params) throws Exception {
        return this.callService(B2BPOSWS, "dsProductBrowseBy" + params.get("X"), params);
    }

    /**
     * Метод для загрузки данных продукта по системному имени (для
     * angular-интерфейса).
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"PRODSYSNAME"})
    public Map<String, Object> dsB2BProductBrowseBySysName(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BProductBrowseBySysName");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> product = null;
        String productSysName = getStringParamLogged(params, "PRODSYSNAME");
        if (productSysName.isEmpty()) {
            product = makeErrorResult("Не указано системное имя продукта (PRODSYSNAME).");
        } else {
            Map<String, Object> versionParams = new HashMap<String, Object>();
            versionParams.put("PRODSYSNAME", productSysName);
            Long productVersionID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductVersionBrowseListByParamEx", versionParams, login, password, "PRODVERID"));
            if (productVersionID == null) {
                product = makeErrorResult("Не удалось определить версию продукта (PRODVERID) по переданному системному имени продукта (PRODSYSNAME).");
            } else {
                Map<String, Object> configParams = new HashMap<String, Object>();
                configParams.put("PRODVERID", productVersionID);
                Long prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
                if (prodConfID == null) {
                    product = makeErrorResult("Не удалось определить идентификатор продукта (PRODCONFID) по переданному системному имени продукта (PRODSYSNAME).");
                } else {
                    Map<String, Object> productParams = new HashMap<String, Object>();
                    productParams.put("PRODCONFID", prodConfID);
                    productParams.put("LOADALLDATA", params.get("LOADALLDATA"));
                    productParams.put(RETURN_AS_HASH_MAP, true);
                    product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);
                    if (product == null) {
                        product = makeErrorResult("Не удалось получить сведения продукта по переданному системному имени продукта (PRODSYSNAME).");
                    }
                }
            }
        }
        result.put("PRODMAP", product);
        logger.debug("after dsB2BProductBrowseBySysName");
        return result;
    }

    /**
     * Метод для загрузки данных агентских калькуляторов, для привязки к агентским договорам
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod()
    public Map<String, Object> dsB2BAgentCalculatorBrowseList(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BCalculatorBrowseList");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> calcParams = new HashMap<String, Object>();

        // все калькуляторы агентских вознаграждений должны начинаться с AGENT_B2B
        calcParams.put("CURVERID", params.get("CALCVERID"));
        calcParams.put("CALCNAME", "AGENT_B2B");
        Map<String, Object> calcRes = this.callService(Constants.INSTARIFICATORWS, "dsCalculatorBrowseListByParam", calcParams, login, password);
        if (calcRes.get(RESULT) != null) {
            List<Map<String, Object>> calcList = (List<Map<String, Object>>) calcRes.get(RESULT);
            for (Map<String, Object> calcMap : calcList) {

                if (calcMap.get("CALCID") != null) {
                    
                    // загружаем список констант
                    Map<String, Object> qParams = new HashMap<String, Object>();
                    qParams.put("CALCVERID", calcMap.get("CURVERID"));
                    List<Map<String, Object>> qRes = WsUtils.getListFromResultMap(this.callService(Constants.INSTARIFICATORWS, "dsCalculatorConstBrowseListByParam", qParams, login, password));
                    calcMap.put("CONSTLIST", qRes);
                    // загружаем список формул
                    qParams = new HashMap<String, Object>();
                    qParams.put("CALCVERID", calcMap.get("CURVERID"));
                    qRes = WsUtils.getListFromResultMap(this.callService(Constants.INSTARIFICATORWS, "dsCalculatorFormulaBrowseListByParam", qParams, login, password));
                    calcMap.put("FORMULALIST", qRes);
                    // загружаем список подключенных справочников
                    qParams = new HashMap<String, Object>();
                    qParams.put("CALCVERID", calcMap.get("CURVERID"));
                    qRes = WsUtils.getListFromResultMap(this.callService(Constants.INSTARIFICATORWS, "dsCalculatorHandbookBrowseListByParam", qParams, login, password));
                    if ((qRes != null) && (qRes.size() > 0)) {
                        for (Map<String, Object> bean : qRes) {
                            bean.put("HBMAP", loadHandbookOneVersion(bean.get("HBDATAVERID"), true, login, password));
                            
                        }
                    }
                    calcMap.put("HANDBOOKLIST", qRes);
                    // загружаем список входных параметров
                    qParams = new HashMap<String, Object>();
                    qParams.put("CALCVERID", calcMap.get("CURVERID"));
                    qRes = WsUtils.getListFromResultMap(this.callService(Constants.INSTARIFICATORWS, "dsCalculatorInputParamBrowseListByParam", qParams, login, password));
                    calcMap.put("INPUTPARAMLIST", qRes);
                }
            }
            result.put(RESULT, calcList);
        }
        logger.debug("after dsB2BCalculatorBrowseList");
        return result;
    }

    /*
     Загрузка одной версии справочника (вместе с описанием справочника и возможностью загрузки данных)
     */
    private Map<String, Object> loadHandbookOneVersion(Object hbDataVerId, boolean loadHBData, String login, String password) throws Exception {
        Map<String, Object> result = null;
        if ((hbDataVerId != null) && (Long.valueOf(hbDataVerId.toString()).longValue() > 0)) {
            Map<String, Object> dataVerParams = new HashMap<String, Object>();
            dataVerParams.put(RETURN_AS_HASH_MAP, "TRUE");
            dataVerParams.put("HBDATAVERID", hbDataVerId);
            Map<String, Object> dataVerRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookDataVersionBrowseListByParamEx", dataVerParams, login, password);
            if (dataVerRes.get("HBDESCRID") != null) {
                Map<String, Object> descrParams = new HashMap<String, Object>();
                descrParams.put(RETURN_AS_HASH_MAP, "TRUE");
                descrParams.put("HBDESCRID", dataVerRes.get("HBDESCRID"));
                Map<String, Object> descrRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookDescriptorsBrowseListByParamEx", descrParams, login, password);
                if (descrRes.get("HBDESCRID") != null) {
                    result = new HashMap<String, Object>();
                    result.putAll(descrRes);
                    List<Map<String, Object>> hbDataVerList = new ArrayList<Map<String, Object>>();
                    hbDataVerList.add(dataVerRes);
                    result.put("HBDATAVERLIST", hbDataVerList);
                    // загружаем описание полей
                    Map<String, Object> propDescrParams = new HashMap<String, Object>();
                    propDescrParams.put("HBDESCRID", descrRes.get("HBDESCRID"));
                    Map<String, Object> propDescrRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookPropertyDescriptorBrowseListByParam", propDescrParams, login, password);
                    List<Map<String, Object>> propDescrList = WsUtils.getListFromResultMap(propDescrRes);
                    if (propDescrList != null) {
                        result.put("HBPROPDESCRLIST", propDescrList);
                    }
                    // загружаем данные справочника при необходимости
                    if (loadHBData) {
                        Map<String, Object> hbDataParams = new HashMap<String, Object>();
                        hbDataParams.put("HBDATAVERID", hbDataVerId);
                        dataVerRes.put("HBDATALIST", WsUtils.getListFromResultMap(this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", hbDataParams, login, password)));
                    }
                }
            }
        }
        return result;
    }

    /*
     Загрузка одной версии калькулятора (вместе с загрузкой описания калькулятора)
     */
    private Map<String, Object> loadCalculatorOneVersion(Object calcVerId, String login, String password) throws Exception {
        Map<String, Object> result = null;
        if ((calcVerId != null) && (Long.valueOf(calcVerId.toString()).longValue() > 0)) {
            Map<String, Object> calcVerParams = new HashMap<String, Object>();
            calcVerParams.put(RETURN_AS_HASH_MAP, "TRUE");
            calcVerParams.put("CALCVERID", calcVerId);
            Map<String, Object> calcVerRes = this.callService(Constants.INSTARIFICATORWS, "dsCalculatorVersionBrowseListByParamEx", calcVerParams, login, password);
            if (calcVerRes.get("CALCVERID") != null) {
                Map<String, Object> calcParams = new HashMap<String, Object>();
                calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
                calcParams.put("CALCID", calcVerRes.get("CALCID"));
                Map<String, Object> calcRes = this.callService(Constants.INSTARIFICATORWS, "dsCalculatorBrowseListByParam", calcParams, login, password);
                if (calcRes.get("CALCID") != null) {
                    result = new HashMap<String, Object>();
                    result.putAll(calcRes);
                    List<Map<String, Object>> calcVerList = new ArrayList<Map<String, Object>>();
                    calcVerList.add(calcVerRes);
                    result.put("VERSIONLIST", calcVerList);
                    // загружаем список констант
                    Map<String, Object> qParams = new HashMap<String, Object>();
                    qParams.put("CALCVERID", calcVerId);
                    List<Map<String, Object>> qRes = WsUtils.getListFromResultMap(this.callService(Constants.INSTARIFICATORWS, "dsCalculatorConstBrowseListByParam", qParams, login, password));
                    calcVerRes.put("CONSTLIST", qRes);
                    // загружаем список формул
                    qParams = new HashMap<String, Object>();
                    qParams.put("CALCVERID", calcVerId);
                    qRes = WsUtils.getListFromResultMap(this.callService(Constants.INSTARIFICATORWS, "dsCalculatorFormulaBrowseListByParam", qParams, login, password));
                    calcVerRes.put("FORMULALIST", qRes);
                    // загружаем список подключенных справочников
                    qParams = new HashMap<String, Object>();
                    qParams.put("CALCVERID", calcVerId);
                    qRes = WsUtils.getListFromResultMap(this.callService(Constants.INSTARIFICATORWS, "dsCalculatorHandbookBrowseListByParam", qParams, login, password));
                    if ((qRes != null) && (qRes.size() > 0)) {
                        for (Map<String, Object> bean : qRes) {
                            bean.put("HBMAP", loadHandbookOneVersion(bean.get("HBDATAVERID"), true, login, password));
                        }
                    }
                    calcVerRes.put("HANDBOOKLIST", qRes);
                    // загружаем список входных параметров
                    qParams = new HashMap<String, Object>();
                    qParams.put("CALCVERID", calcVerId);
                    qRes = WsUtils.getListFromResultMap(this.callService(Constants.INSTARIFICATORWS, "dsCalculatorInputParamBrowseListByParam", qParams, login, password));
                    calcVerRes.put("INPUTPARAMLIST", qRes);
                }
            }
        }
        return result;
    }

    /*
     Подгрузка данных описаний справочников к продукту (в тех местах, где идет ссылка на версии справочников)
     */
    private void universalAttachHBDataToProduct(Map<String, Object> productMap, String login, String password) throws Exception {
        if ((productMap.get("PRODVERLIST") != null) && (((List<Map<String, Object>>) productMap.get("PRODVERLIST")).size() > 0)) {
            List<Map<String, Object>> productVersionsList = (List<Map<String, Object>>) productMap.get("PRODVERLIST");
            for (Map<String, Object> versionBean : productVersionsList) {
                if ((versionBean.get("PRODCONFLIST") != null) && (((List<Map<String, Object>>) versionBean.get("PRODCONFLIST")).size() > 0)) {
                    List<Map<String, Object>> productConfigsList = (List<Map<String, Object>>) versionBean.get("PRODCONFLIST");
                    for (Map<String, Object> configBean : productConfigsList) {
                        configBean.put("VALUESHB", loadHandbookOneVersion(configBean.get("HBDATAVERID"), false, login, password));
                    }
                }
                if ((versionBean.get("PRODSTRUCTLIST") != null) && (((List<Map<String, Object>>) versionBean.get("PRODSTRUCTLIST")).size() > 0)) {
                    List<Map<String, Object>> productStructsList = (List<Map<String, Object>>) versionBean.get("PRODSTRUCTLIST");
                    for (Map<String, Object> structBean : productStructsList) {
                        structBean.put("VALUESHB", loadHandbookOneVersion(structBean.get("HBDATAVERID"), false, login, password));
                    }
                }
            }
        }
    }

    /*
     Подгрузка данных калькулятора продукта
     */
    private void universalAttachCalculatorDataToProduct(Map<String, Object> productMap, String login, String password) throws Exception {
        if ((productMap.get("PRODVERLIST") != null) && (((List<Map<String, Object>>) productMap.get("PRODVERLIST")).size() > 0)) {
            List<Map<String, Object>> productVersionsList = (List<Map<String, Object>>) productMap.get("PRODVERLIST");
            for (Map<String, Object> versionBean : productVersionsList) {
                if ((versionBean.get("PRODCONFLIST") != null) && (((List<Map<String, Object>>) versionBean.get("PRODCONFLIST")).size() > 0)) {
                    List<Map<String, Object>> productConfigsList = (List<Map<String, Object>>) versionBean.get("PRODCONFLIST");
                    for (Map<String, Object> configBean : productConfigsList) {
                        configBean.put("CALCMAP", loadCalculatorOneVersion(configBean.get("CALCVERID"), login, password));
                    }
                }
            }
        }
    }

    /*
     Подгрузка данных для значения по-умолчанию
     */
    private void universalAttachDataToProductDefaultValue(Map<String, Object> defValMap, String login, String password) throws Exception {
        if ((defValMap != null) && (defValMap.get("NAME") != null) && (defValMap.get("VALUE") != null)) {
            if (defValMap.get("NAME").toString().equalsIgnoreCase("CONTRAUTONUMBERSYSNAME")) {
                String maskSysName = defValMap.get("VALUE").toString();
                // todo: вероятно, сабж
                //s s s ss
            }
        }
    }

    /*
     Подгрузка данных для значений продукта
     */
    private void universalAttachDataToProductDefaultValues(Map<String, Object> productMap, String login, String password) throws Exception {
        if ((productMap.get("PRODVERLIST") != null) && (((List<Map<String, Object>>) productMap.get("PRODVERLIST")).size() > 0)) {
            List<Map<String, Object>> productVersionsList = (List<Map<String, Object>>) productMap.get("PRODVERLIST");
            for (Map<String, Object> versionBean : productVersionsList) {
                if ((versionBean.get("PRODCONFLIST") != null) && (((List<Map<String, Object>>) versionBean.get("PRODCONFLIST")).size() > 0)) {
                    List<Map<String, Object>> productConfigsList = (List<Map<String, Object>>) versionBean.get("PRODCONFLIST");
                    for (Map<String, Object> configBean : productConfigsList) {
                        if ((configBean.get("PRODDEFVALLIST") != null) && (((List<Map<String, Object>>) configBean.get("PRODDEFVALLIST")).size() > 0)) {
                            List<Map<String, Object>> productDefaultValuesList = (List<Map<String, Object>>) configBean.get("PRODDEFVALLIST");
                            for (Map<String, Object> defValBean : productDefaultValuesList) {
                                universalAttachDataToProductDefaultValue(defValBean, login, password);
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     Универсальная загрузка продукта с корректной иерархией (начиная с PRODUCT)
     */
    @WsMethod(requiredParams = {"PRODID"})
    public Map<String, Object> dsB2BProductUniversalLoad(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        boolean isLoadAllData = (params.get("LOADALLDATA") != null) && (Long.valueOf(params.get("LOADALLDATA").toString()).longValue() == 1);
        Map<String, Object> product = new HashMap<String, Object>();
        product.put("PRODID", params.get("PRODID"));
        joinEntitiesUniversal(product, "", "PROD", params);
        joinEntitiesUniversal(product, "PROD", "PRODVER", params);
        joinEntitiesUniversal(product, "PROD.PRODVER", "PRODCONF", params);
        joinEntitiesUniversal(product, "PROD.PRODVER", "PRODSTRUCT", params);
        joinEntitiesUniversal(product, "PROD.PRODVER", "PRODPROG", params);
        joinEntitiesUniversal(product, "PROD.PRODVER", "PRODSALESCHAN", params);
        Map<String, Object> prodMap = (Map<String, Object>) product.get("PRODMAP");
        if (prodMap != null) {
            List<Map<String, Object>> prodVerList = (List<Map<String, Object>>) prodMap.get("PRODVERLIST");
            if (prodVerList != null) {
                for (Map<String, Object> bean : prodVerList) {
                    joinEntitiesUniversal(bean, "PRODSTRUCT", "PRODVALUE", params);
                    joinEntitiesUniversal(bean, "PRODCONF", "PRODVALUE", params);
                    joinEntitiesUniversal(bean, "PRODCONF", "PRODREP", params);
                    joinEntitiesUniversal(bean, "PRODCONF", "PRODCALCRATERULE", params);
                    joinEntitiesUniversal(bean, "PRODCONF", "PRODDEFVAL", params);
                    if (isLoadAllData) {
                        joinEntitiesUniversal(bean, "PRODCONF", "PRODBINDOC", params);
                        joinEntitiesUniversal(bean, "PRODCONF", "PRODADDCHT", params);
                        joinEntitiesUniversal(bean, "PRODCONF", "PRODNUMMETHOD", params);
                        joinEntitiesUniversal(bean, "PRODCONF", "PRODINSAMCUR", params);
                        joinEntitiesUniversal(bean, "PRODCONF", "PRODPREMCUR", params);
                        joinEntitiesUniversal(bean, "PRODCONF", "PRODCALCRATERULE", params);
                        joinEntitiesUniversal(bean, "PRODCONF", "PRODDISC", params);
                        joinEntitiesUniversal(bean, "PRODCONF", "PRODTERM", params);
                    }
                    List<Map<String, Object>> prodConfList = (List<Map<String, Object>>) bean.get("PRODCONFLIST");
                    if (prodConfList != null) {
                        for (Map<String, Object> prodConfBean : prodConfList) {
                            joinEntitiesUniversal(prodConfBean, "PRODREP", "REP", params);
                            if (isLoadAllData) {
                                joinEntitiesUniversal(prodConfBean, "PRODVALUE", "PRODPOSSVALUE", params);
                                joinEntitiesUniversal(prodConfBean, "PRODDISC", "PRODDISCPROMO", params);
                                joinEntitiesUniversal(prodConfBean, "PRODDISC", "PRODDISCVAL", params);
                                joinEntitiesUniversal(prodConfBean, "PRODTERM", "TERM", params);
                            }
                        }
                    }
                }
            }
        }
        if (isLoadAllData) {
            joinEntitiesUniversal(product, "PROD.PRODVER", "PRODPAYVAR", params);
            joinEntitiesUniversal(product, "PROD", "PRODRIDER", params);
            universalAttachCalculatorDataToProduct((Map<String, Object>) product.get("PRODMAP"), login, password);
            universalAttachHBDataToProduct((Map<String, Object>) product.get("PRODMAP"), login, password);
            universalAttachDataToProductDefaultValues((Map<String, Object>) product.get("PRODMAP"), login, password);
        }
        return (Map<String, Object>) product.get("PRODMAP");
    }

    private void processEntitiesList(List<Map<String, Object>> entityList, String serviceName, String methodPrefix, String entityIdName, String parentEntityIdName, Object parentEntityId, String login, String password) throws Exception {
        if ((entityList == null) || (entityList.isEmpty())) {
            return;
        }
        List<Map<String, Object>> inserted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> modified = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> deleted = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> unModified = new ArrayList<Map<String, Object>>();
        this.sortByRowStatus(entityList, inserted, modified, deleted, unModified);
        for (Map<String, Object> bean : inserted) {
            if ((parentEntityIdName != null) && (!parentEntityIdName.isEmpty()) && (parentEntityId != null) && (!parentEntityId.toString().isEmpty())) {
                bean.put(parentEntityIdName, parentEntityId);
            }
            Map<String, Object> serviceParams = cloneMapWithoutLists(bean);
            // по умолчанию делаем Create, но если уже есть ИДшник, то делаем Insert
            String usingMethod = "Create";
            if ((bean.get(entityIdName) != null) && (Long.valueOf(bean.get(entityIdName).toString()).longValue() > 0)) {
                usingMethod = "Insert";
            }
            //
            String methodName = methodPrefix + usingMethod;
            logger.debug("Method name: " + methodName);
            logger.debug("Method params: " + serviceParams);
            Object entityId = this.callServiceAndGetOneValue(serviceName, methodName, serviceParams, login, password, entityIdName);
            logger.debug(String.format("Method result (entity ID - %s): ", entityIdName) + entityId + "\n");
            if (entityId != null) {
                bean.put(entityIdName, entityId);
                bean.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                callOnProcessEntityMethod(methodPrefix, bean, login, password);
            }
        }
        for (Map<String, Object> bean : modified) {
            Map<String, Object> serviceParams = cloneMapWithoutLists(bean);
            this.callService(serviceName, methodPrefix + "Update", serviceParams, login, password);
            bean.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
            callOnProcessEntityMethod(methodPrefix, bean, login, password);
        }
        for (Map<String, Object> bean : deleted) {
            callOnProcessEntityMethod(methodPrefix, bean, login, password);
            Map<String, Object> serviceParams = cloneMapWithoutLists(bean);
            boolean isDeletingLogged = true; // протоколирование удаления, после завершения проверки можно отключить
            this.callService(serviceName, methodPrefix + "Delete", serviceParams, isDeletingLogged, login, password);
        }
        for (Map<String, Object> bean : unModified) {
            callOnProcessEntityMethod(methodPrefix, bean, login, password);
        }
    }

    private void callOnProcessEntityMethod(String methodPrefix, Map<String, Object> entityBean, String login, String password) throws Exception {
        if (methodPrefix.equalsIgnoreCase("dsB2BProduct")) {
            processProductEntity(entityBean, login, password);
        } else if (methodPrefix.equalsIgnoreCase("dsB2BProductVersion")) {
            processProductVersionEntity(entityBean, login, password);
        } else if (methodPrefix.equalsIgnoreCase("dsB2BProductConfig")) {
            processProductConfigEntity(entityBean, login, password);
        } else if (methodPrefix.equalsIgnoreCase("dsB2BProductValue")) {
            processProductValueEntity(entityBean, login, password);
        } else if (methodPrefix.equalsIgnoreCase("dsB2BProductValueStructure")) {
            processProductValueEntity(entityBean, login, password);
        } else if (methodPrefix.equalsIgnoreCase("dsB2BProductDiscount")) {
            processProductDiscountEntity(entityBean, login, password);
        } else if (methodPrefix.equalsIgnoreCase("dsB2BProductStructureBase")) {
            processProductStructureEntity(entityBean, login, password);
        } else if (methodPrefix.equalsIgnoreCase("dsHandbookDescriptor")) {
            processHandbookEntity(entityBean, login, password);
        } else if (methodPrefix.equalsIgnoreCase("dsHandbookDataVersion")) {
            processHandbookDataVersionEntity(entityBean, login, password);
        } else if (methodPrefix.equalsIgnoreCase("dsCalculator")) {
            processCalculatorEntity(entityBean, login, password);
        } else if (methodPrefix.equalsIgnoreCase("dsCalculatorVersion")) {
            processCalculatorVersionEntity(entityBean, login, password);
        } else if (methodPrefix.equalsIgnoreCase("dsCalculatorHandbook")) {
            processCalculatorHandbookEntity(entityBean, login, password);
        }
    }

    private void processProductEntity(Map<String, Object> entityBean, String login, String password) throws Exception {
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODRIDER" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductRider", "PRODRIDERID", "PRODID", entityBean.get("PRODID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODVER" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductVersion", "PRODVERID", "PRODID", entityBean.get("PRODID"), login, password);
    }

    private void processProductVersionEntity(Map<String, Object> entityBean, String login, String password) throws Exception {
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODCONF" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductConfig", "PRODCONFID", "PRODVERID", entityBean.get("PRODVERID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODSTRUCT" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductStructureBase", "PRODSTRUCTID", "PRODVERID", entityBean.get("PRODVERID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODPROG" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductProgram", "PRODPROGID", "PRODVERID", entityBean.get("PRODVERID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODSALESCHAN" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductSalesChannel", "PRODSALESCHANID", "PRODVERID", entityBean.get("PRODVERID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODPAYVAR" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductPaymentVariant", "PRODPAYVARID", "PRODVERID", entityBean.get("PRODVERID"), login, password);
    }

    private void processProductConfigEntity(Map<String, Object> entityBean, String login, String password) throws Exception {
        // обрабатываем справочник с показателями по продукту
        processHandbook((Map<String, Object>) entityBean.get("VALUESHB"), login, password);
        processCalculator((Map<String, Object>) entityBean.get("CALCMAP"), login, password);
        // обработка сущностей детей
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODVALUE" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductValue", "PRODVALUEID", "PRODCONFID", entityBean.get("PRODCONFID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODREP" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductReport", "PRODREPID", "PRODCONFID", entityBean.get("PRODCONFID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODCALCRATERULE" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductCalcRateRule", "PRODCALCRATERULEID", "PRODCONFID", entityBean.get("PRODCONFID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODDEFVAL" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductDefaultValue", "PRODDEFVALID", "PRODCONFID", entityBean.get("PRODCONFID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODADDCHT" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductAdditionalChangeType", "PRODADDCHTID", "PRODCONFID", entityBean.get("PRODCONFID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODNUMMETHOD" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductNumMethod", "PRODNUMMETHODID", "PRODCONFID", entityBean.get("PRODCONFID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODINSAMCUR" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductInsAmCurrency", "PRODINSAMCURID", "PRODCONFID", entityBean.get("PRODCONFID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODPREMCUR" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductPremiumCurrency", "PRODPREMCURID", "PRODCONFID", entityBean.get("PRODCONFID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODDISC" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductDiscount", "PRODDISCID", "PRODCONFID", entityBean.get("PRODCONFID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODTERM" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductTerm", "PRODTERMID", "PRODCONFID", entityBean.get("PRODCONFID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODBINDOC" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductBinaryDocument", "PRODBINDOCID", "PRODCONFID", entityBean.get("PRODCONFID"), login, password);
    }

    private void processProductValueEntity(Map<String, Object> entityBean, String login, String password) throws Exception {
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODPOSSVALUE" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductPossibleValue", "PRODPOSSVALUEID", "PRODVALUEID", entityBean.get("PRODVALUEID"), login, password);
    }

    private void processProductDiscountEntity(Map<String, Object> entityBean, String login, String password) throws Exception {
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODDISCPROMO" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductDiscountPromoCode", "PRODDISCPROMOID", "PRODDISCID", entityBean.get("PRODDISCID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODDISCVAL" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductDiscountValue", "PRODDISCVALID", "PRODDISCID", entityBean.get("PRODDISCID"), login, password);
    }

    private void processProductStructureEntity(Map<String, Object> entityBean, String login, String password) throws Exception {
        processEntitiesList((List<Map<String, Object>>) entityBean.get("PRODVALUE" + UNIVERSAL_SUFFIX_LIST), Constants.B2BPOSWS, "dsB2BProductValueStructure", "PRODVALUEID", "PRODSTRUCTID", entityBean.get("PRODSTRUCTID"), login, password);
    }

    private void processHandbook(Map<String, Object> handbookMap, String login, String password) throws Exception {
        if ((handbookMap != null) && (!handbookMap.isEmpty())) {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            list.add(handbookMap);
            processEntitiesList(list, Constants.INSTARIFICATORWS, "dsHandbookDescriptor", "HBDESCRID", null, null, login, password);
        }
    }

    private void processHandbookEntity(Map<String, Object> entityBean, String login, String password) throws Exception {
        processEntitiesList((List<Map<String, Object>>) entityBean.get("HBPROPDESCR" + UNIVERSAL_SUFFIX_LIST), Constants.INSTARIFICATORWS, "dsHandbookPropertyDescriptor", "HBPROPDESCRID", "HBDESCRID", entityBean.get("HBDESCRID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("HBDATAVER" + UNIVERSAL_SUFFIX_LIST), Constants.INSTARIFICATORWS, "dsHandbookDataVersion", "HBDATAVERID", "HBDESCRID", entityBean.get("HBDESCRID"), login, password);
    }

    private void processHandbookDataVersionEntity(Map<String, Object> entityBean, String login, String password) throws Exception {
        if ((entityBean != null) && (!entityBean.isEmpty())) {
            // добавляем параметр не использовать кэш для тарификатора (чтобы корректно считался обновленный дескриптор)
            List<Map<String, Object>> hbDataList = (List<Map<String, Object>>) entityBean.get("HBDATA" + UNIVERSAL_SUFFIX_LIST);
            if ((hbDataList != null) && (hbDataList.size() > 0)) {
                for (Map<String, Object> bean : hbDataList) {
                    bean.put("USECACHE", 0L);
                }
            }
            //
            processEntitiesList((List<Map<String, Object>>) entityBean.get("HBDATA" + UNIVERSAL_SUFFIX_LIST), Constants.INSTARIFICATORWS, "dsHandbookRecord", "HBSTOREID", "HBDATAVERID", entityBean.get("HBDATAVERID"), login, password);
        }
    }

    private void processCalculator(Map<String, Object> calculatorMap, String login, String password) throws Exception {
        if ((calculatorMap != null) && (!calculatorMap.isEmpty())) {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            list.add(calculatorMap);
            processEntitiesList(list, Constants.INSTARIFICATORWS, "dsCalculator", "CALCID", null, null, login, password);
        }
    }

    private void processCalculatorEntity(Map<String, Object> entityBean, String login, String password) throws Exception {
        processEntitiesList((List<Map<String, Object>>) entityBean.get("VERSION" + UNIVERSAL_SUFFIX_LIST), Constants.INSTARIFICATORWS, "dsCalculatorVersion", "CALCVERID", "CALCID", entityBean.get("CALCID"), login, password);
    }

    private void processCalculatorVersionEntity(Map<String, Object> entityBean, String login, String password) throws Exception {
        processEntitiesList((List<Map<String, Object>>) entityBean.get("FORMULA" + UNIVERSAL_SUFFIX_LIST), Constants.INSTARIFICATORWS, "dsCalculatorFormula", "CALCFORMULAID", "CALCVERID", entityBean.get("CALCVERID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("CONST" + UNIVERSAL_SUFFIX_LIST), Constants.INSTARIFICATORWS, "dsCalculatorConst", "CALCCONSTID", "CALCVERID", entityBean.get("CALCVERID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("INPUTPARAM" + UNIVERSAL_SUFFIX_LIST), Constants.INSTARIFICATORWS, "dsCalculatorInputParam", "CALCINPUTPARAMID", "CALCVERID", entityBean.get("CALCVERID"), login, password);
        processEntitiesList((List<Map<String, Object>>) entityBean.get("HANDBOOK" + UNIVERSAL_SUFFIX_LIST), Constants.INSTARIFICATORWS, "dsCalculatorHandbook", "CALCHANDBOOKID", "CALCVERID", entityBean.get("CALCVERID"), login, password);
    }

    private void processCalculatorHandbookEntity(Map<String, Object> entityBean, String login, String password) throws Exception {
        processHandbook((Map<String, Object>) entityBean.get("HBMAP"), login, password);
    }

    @WsMethod(requiredParams = {"PRODLIST"})
    public Map<String, Object> dsB2BProductUniversalSave(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Map<String, Object>> productList = (List<Map<String, Object>>) params.get("PRODLIST");
        processEntitiesList(productList, Constants.B2BPOSWS, "dsB2BProduct", "PRODID", null, null, login, password);
        Map<String, Object> result = new HashMap<String, Object>();
        return result;
    }

    // Загрузить справочники привязанные к калькулятору через CORE_SETTINGS
    @WsMethod(requiredParams = {"CONFIGNAME"})
    public Map<String, Object> dsB2BLoadHandBookByProduct(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> callParams = new HashMap<String, Object>();
        callParams.put(RETURN_AS_HASH_MAP, "TRUE");
        callParams.put("SETTINGSYSNAME", params.get("CONFIGNAME"));
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> sysSettings = this.callService(COREWS, "getSysSettingBySysName", callParams, login, password);
        if ((sysSettings.get("SETTINGVALUE") != null)) {
            Long prodconfigid = Long.valueOf(sysSettings.get("SETTINGVALUE").toString());
            result.put("PRODUCTCONFIG", prodconfigid);
            callParams.clear();
            callParams.put("PRODCONFID", prodconfigid);
            callParams.put(RETURN_AS_HASH_MAP, "TRUE");
            Map<String, Object> prodVals = this.callService(B2BPOSWS, "dsB2BProductConfigBrowseListByParam", callParams, login, password);
            if ((prodVals != null) && (prodVals.get("CALCVERID") != null) && (prodVals.get("CALCVERID") instanceof Long)) {
                Long calcVerId = (Long) prodVals.get("CALCVERID");
                if (params.get("HANBOOKLIST") != null) {
                    callParams.clear();
                    callParams.put("HBNAMES", params.get("HANBOOKLIST"));
                    callParams.put("CALCVERID", calcVerId);
                    Map<String, Object> hbList = callService(instarificatorws, "dsGetCalculatorHandbooksData", callParams, login, password);
                    result.putAll((Map<String, Object>) hbList.get(RESULT));
                } else {
                    callParams.clear();
                    callParams.put("NAME", params.get("HBNAME"));
                    callParams.put("PARAMS", params.get("PARAMS"));
                    callParams.put("CALCVERID", calcVerId);
                    Map<String, Object> hbList = callService(instarificatorws, "dsGetCalculatorHandbookData", callParams, login, password);
                    result.put(params.get("HBNAME").toString(), hbList.get(RESULT));
                }
            }
        }
        return result;
    }

    /*
    Синхронизация полей справочника в зависимости от списка показателей
     */
    @WsMethod(requiredParams = {"PRODVALLIST", "HBDESCRID"})
    public Map<String, Object> dsB2BSyncHandbookDescrByValues(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        List<Map<String, Object>> prodValList = (List<Map<String, Object>>) params.get("PRODVALLIST");
        Long hbDescrId = Long.valueOf(params.get("HBDESCRID").toString());
        Map<String, Object> hbPropDescrParam = new HashMap<String, Object>();
        hbPropDescrParam.put("HBDESCRID", hbDescrId);
        List<Map<String, Object>> hbPropDescrList = this.callServiceAndGetListFromResultMap(Constants.INSTARIFICATORWS, "dsHandbookPropertyDescriptorBrowseListByParam", hbPropDescrParam, login, password);
        for (Map<String, Object> bean : prodValList) {
            Map<String, Object> propDescrMap = null;
            for (Map<String, Object> propDescrBean : hbPropDescrList) {
                if (propDescrBean.get("NAME").toString().equalsIgnoreCase(bean.get("NAME").toString())) {
                    propDescrMap = propDescrBean;
                    break;
                }
            }
            if (propDescrMap != null) {

            } else {

            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        return result;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BProductGetMainInfoByContractID(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BProductGetMainInfoByContractID", params);
        return result;
    }

}
