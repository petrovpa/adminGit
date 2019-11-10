package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * Фасад для работы с типами продуктов
 *
 * @author Ivanov Roman
 */
public class B2BProductKindCustomFacade extends B2BBaseFacade {

    // Подготовка ключей
    private Map<String, Object> prepareKeyMap(List<Map<String, Object>> contrList) {
        Map<String, Object> prepareKeyMap = new HashMap<>();

        for (Map<String, Object> map : contrList) {
            String prodKindSysName = getStringParam(map.get("PRODKINDSYSNAME"));
            String prodKindName = getStringParam(map.get("PRODKINDNAME"));
            String kindSysName = "OTHER";
            String kindName = "Прочие договоры";
            if ((!prodKindSysName.isEmpty()) && (!prodKindName.isEmpty())) {
                kindSysName = prodKindSysName;
                kindName = prodKindName;
            }

            Map<String, Object> innerStruct = new HashMap<>();

            innerStruct.put("KINDNAMERUS", kindName);
            innerStruct.put("contrList", new ArrayList<Map<String, Object>>());

            prepareKeyMap.put(kindSysName, innerStruct);
        }

        return prepareKeyMap;
    }

    // Подготовка структуры по ключам
    private Map<String, Object> prepareStructByKey(List<Map<String, Object>> contrList, Map<String, Object> prepareKeyMap) {

        Map<String, Object> prepareStruct = new HashMap<>();
        Map<String, Object> proccessedMapRes = new HashMap<>();

        for (Map.Entry entry : prepareKeyMap.entrySet()) {

            String entryKey = (String) entry.getKey();
            String kindKeyName = (entryKey.isEmpty()) ? "OTHER" : entryKey;

            Map<String, Object> proccessedMap = (Map<String, Object>) entry.getValue();
            List<Map<String, Object>> innerContrList = (List<Map<String, Object>>) proccessedMap.get("contrList");

            for (Map<String, Object> contract : contrList) {
                String prodKindSysName = getStringParam(contract.get("PRODKINDSYSNAME"));
                //Заглушка для договоров, которые не относятся не к одному типу продукта
                if (prodKindSysName.isEmpty()) {
                    //вывод в лк фильтруется по b2b_ProdProg.LKVISIBLE
                    prodKindSysName = "OTHER";
                }
                if ((!prodKindSysName.isEmpty()) && (prodKindSysName.equals(kindKeyName))) {
                    innerContrList.add(contract);
                }
            }
            proccessedMapRes.put(kindKeyName, proccessedMap);
        }
        prepareStruct.put("ResultMap", proccessedMapRes);

        return prepareStruct;
    }
    // Подготовка структуры договоров по типу продуктов

    private Map<String, Object> prepareStructContractByProductKind(List<Map<String, Object>> contrList) {
        Map<String, Object> prepareKeyMap = prepareKeyMap(contrList);
        return prepareStructByKey(contrList, prepareKeyMap);
    }

    @WsMethod
    public Map<String, Object> dsB2BProductKindSortByParam(Map<String, Object> sortParam) {

        // Получаем полный список договоров 
        List<Map<String, Object>> contrList = (List<Map<String, Object>>) sortParam.get("Result");
        Map<String, Object> result = new HashMap<>();
        if ((contrList != null) && (!contrList.isEmpty())) {
            result = prepareStructContractByProductKind(contrList);
        }
        return result;
    }

}
