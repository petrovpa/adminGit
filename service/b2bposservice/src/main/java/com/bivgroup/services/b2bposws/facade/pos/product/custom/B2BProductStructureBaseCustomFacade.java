package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("B2BProductStructureBaseCustom")
public class B2BProductStructureBaseCustomFacade extends B2BBaseFacade {

    protected static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    protected static final String DEFAULT_CHILDS_LIST_KEY_NAME = "CHILDS";

    // набор дискриминаторов реальных структур (не являющихся логическими операторам и прочим)
    private static Set<Long> realStructsDiscriminators;

    // мапа текстовых имен типов реальных структур (не являющихся логическими операторам и прочим)
    private static Map<Long, String> structsTypeNamesByDiscriminators;

    public B2BProductStructureBaseCustomFacade() {
        // набор дискриминаторов реальных структур (не являющихся логическими операторам и прочим)
        realStructsDiscriminators = new HashSet<Long>();
        realStructsDiscriminators.add(DISCRIMINATOR_SECTION); // секция
        realStructsDiscriminators.add(DISCRIMINATOR_GROUP); // группа
        realStructsDiscriminators.add(DISCRIMINATOR_OBJECT); // объект
        realStructsDiscriminators.add(DISCRIMINATOR_RISK); // риск
        // мапа текстовых имен типов реальных структур (не являющихся логическими операторам и прочим)
        structsTypeNamesByDiscriminators = new HashMap<Long, String>();
        structsTypeNamesByDiscriminators.put(DISCRIMINATOR_SECTION, "Секция договора");
        structsTypeNamesByDiscriminators.put(DISCRIMINATOR_GROUP, "Тип объектов страхования");
        structsTypeNamesByDiscriminators.put(DISCRIMINATOR_OBJECT, "Объект страхования");
        structsTypeNamesByDiscriminators.put(DISCRIMINATOR_RISK, "Страховой риск");
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductStructureBaseBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BProductStructureBaseBrowseListByParamEx", params);
        return result;
    }

    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String, Object> dsB2BProductStructureMACUsageCnt(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BProductStructureMACUsageCnt", params);
        return result;
    }

    @WsMethod(requiredParams = {"PRODSYSNAME"})
    public Map<String, Object> dsB2BProductStructureBaseBrowseListByProductSysName(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        String childsListKeyName = getStringParamLogged(params, "CHILDSLISTKEYNAME");
        if (childsListKeyName.isEmpty()) {
            childsListKeyName = DEFAULT_CHILDS_LIST_KEY_NAME;
            logger.debug("For CHILDSLISTKEYNAME will be used default value: " + DEFAULT_CHILDS_LIST_KEY_NAME);
        }

        String productSysName = getStringParamLogged(params, "PRODSYSNAME");
        Long macOrgStructID = getLongParamLogged(params, "MACORGSTRUCTID");
        Long macCurrencyID = getLongParamLogged(params, "MACCURRENCYID");

        Map<String, Object> structParams = new HashMap<String, Object>();
        structParams.put("PRODSYSNAME", productSysName);

        Map<String, Object> prodStructsRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductStructureBaseBrowseListByParamEx", structParams, login, password);
        List<Map<String, Object>> prodStructsList = WsUtils.getListFromResultMap(prodStructsRes);

        Map<Long, Map<String, Object>> prodStructsMap = getMapByFieldLongValues(prodStructsList, "PRODSTRUCTID");

        Set<Long> removedProdStructs = new HashSet<Long>();
        for (Map<String, Object> prodStruct : prodStructsList) {

            logger.debug("Checking insurance structure element...");
            getStringParamLogged(prodStruct, "SYSNAME"); // для протоколирования
            Long prodStructID = getLongParamLogged(prodStruct, "PRODSTRUCTID");
            Long prodStructDiscriminator = getLongParam(prodStruct, "DISCRIMINATOR");

            if (realStructsDiscriminators.contains(prodStructDiscriminator)) {
                logger.debug("Real structure element - will be processed.");
                prodStruct.put("DISCRIMINATORNAME", structsTypeNamesByDiscriminators.get(prodStructDiscriminator));
                getStringParamLogged(prodStruct, "DISCRIMINATORNAME"); // для протоколирования

                logger.debug("Searching for parent...");
                Long parentProdStructDisciminator = null;
                Map<String, Object> checkedProdStruct = prodStruct;
                do {
                    Long parentProdStructID = getLongParamLogged(checkedProdStruct, "PARENTSTRUCTID");
                    checkedProdStruct = prodStructsMap.get(parentProdStructID);
                    parentProdStructDisciminator = getLongParamLogged(checkedProdStruct, "DISCRIMINATOR");
                } while ((checkedProdStruct != null) && (!realStructsDiscriminators.contains(parentProdStructDisciminator)));
                Map<String, Object> parentProdStruct = checkedProdStruct;

                if (parentProdStruct != null) {
                    logger.debug("Parent found - parent's system name: " + getStringParam(parentProdStruct, "SYSNAME"));
                    List<Map<String, Object>> parentProdStructChildsList = (List<Map<String, Object>>) parentProdStruct.get(childsListKeyName);
                    if (parentProdStructChildsList == null) {
                        parentProdStructChildsList = new ArrayList<Map<String, Object>>();
                        parentProdStruct.put("CHILDS", parentProdStructChildsList);
                    }
                    parentProdStructChildsList.add(prodStruct);
                } else {
                    logger.debug("Parent not found.");
                }

                if (macOrgStructID != null) {
                    Map<String, Object> macUsageParams = new HashMap<String, Object>();
                    macUsageParams.put("PRODSTRUCTID", prodStructID);
                    macUsageParams.put("MACORGSTRUCTID", macOrgStructID);
                    macUsageParams.put("MACCURRENCYID", macCurrencyID);
                    macUsageParams.put(RETURN_AS_HASH_MAP, true);
                    Map<String, Object> macUsageRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductStructureMACUsageCnt", macUsageParams, true, login, password);
                    Long macUsageCount = getLongParamLogged(macUsageRes, "USEDINMACCNT");
                    prodStruct.put("USEDINMACCNT", macUsageCount);
                }

            } else {
                logger.debug("Not real structure element - will be skipped and removed from result.");
            }
            if (prodStructDiscriminator != DISCRIMINATOR_SECTION) {
                // на верхнем уровне должны остаться только секции
                removedProdStructs.add(prodStructID);
            }
            logger.debug("Checking insurance structure element finihed.");
        }

        for (Long removedProdStructID : removedProdStructs) {
            // на верхнем уровне должны остаться только секции
            Map<String, Object> removedProdStruct = prodStructsMap.get(removedProdStructID);
            prodStructsList.remove(removedProdStruct);
        }

        prodStructsRes.put(TOTALCOUNT, prodStructsList.size());

        return prodStructsRes;
    }

    private List<Map<String, Object>> makeStructHierarchyList(List<Map<String, Object>> prodStructsList,
            Map<String, Object> parentEntity, Long level) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (parentEntity == null) {
            for (Map<String, Object> bean : prodStructsList) {
                if (bean.get("PARENTSTRUCTID") == null) {
                    Map<String, Object> map = new HashMap<String, Object>(bean);
                    map.put("LEVEL", level);
                    map.put("ITEMNAME", getStringParam(bean.get("NAME")) + " (" + getStringParam(bean.get("SYSNAME")) + ")");
                    List<Map<String, Object>> childrenList = makeStructHierarchyList(prodStructsList, bean, level + 1);
                    if (!childrenList.isEmpty()) {
                        map.put("children", childrenList);
                    }
                    map.put("hasChildren", !childrenList.isEmpty());
                    result.add(map);
                }
            }
        } else {
            for (Map<String, Object> bean : prodStructsList) {
                if ((bean.get("PARENTSTRUCTID") != null)
                        && (bean.get("PARENTSTRUCTID").toString().equals(parentEntity.get("PRODSTRUCTID").toString()))) {
                    Map<String, Object> map = new HashMap<String, Object>(bean);
                    map.put("LEVEL", level);
                    map.put("ITEMNAME", getStringParam(bean.get("NAME")) + " (" + getStringParam(bean.get("SYSNAME")) + ")");
                    List<Map<String, Object>> childrenList = makeStructHierarchyList(prodStructsList, bean, level + 1);
                    if (!childrenList.isEmpty()) {
                        map.put("children", childrenList);
                    }
                    map.put("hasChildren", !childrenList.isEmpty());
                    result.add(map);
                }
            }
        }
        return result;
    }

    @WsMethod(requiredParams = {"PRODVERID"})
    public Map<String, Object> dsB2BProductStructureBrowseAsTreeGridList(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        Map<String, Object> structParams = new HashMap<String, Object>();
        structParams.put("PRODVERID", params.get("PRODVERID"));
        Map<String, Object> structRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductStructureBaseBrowseListByParamEx", structParams, login, password);
        List<Map<String, Object>> prodStructsList = WsUtils.getListFromResultMap(structRes);
        List<Map<String, Object>> hierarchyList = new ArrayList<Map<String, Object>>();
        if ((prodStructsList != null) && (!prodStructsList.isEmpty())) {
            hierarchyList = makeStructHierarchyList(prodStructsList, null, 2L);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> rootList = new ArrayList<Map<String, Object>>();
        Map<String, Object> rootMap = new HashMap<String, Object>();
        rootMap.put("DISCRIMINATOR", 0L);
        rootMap.put("LEVEL", 1L);
        rootMap.put("PRODID", params.get("PRODID"));
        rootMap.put("PRODVERID", params.get("PRODVERID"));        
        rootMap.put("ITEMNAME", "Структура продукта");
        rootMap.put("children", hierarchyList);
        rootMap.put("hasChildren", !hierarchyList.isEmpty());
        rootList.add(rootMap);
        result.put(RESULT, rootList);
        return result;
    }

}
