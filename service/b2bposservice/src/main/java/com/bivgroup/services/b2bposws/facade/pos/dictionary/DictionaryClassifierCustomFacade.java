package com.bivgroup.services.b2bposws.facade.pos.dictionary;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("DictionaryCustom")
public class DictionaryClassifierCustomFacade extends B2BDictionaryBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    /*
    @WsMethod(requiredParams = {"CLASSIFIERNAMELIST"})
    public Map<String, Object> dsB2BDictionaryClassifierLoad(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDictionaryClassifierLoad begin");
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> classifierNameList = (List<String>) params.get("CLASSIFIERNAMELIST");
        DictionaryCaller dc = new DictionaryCaller(Crm.newInstance(DictionaryCaller.getDataSource(this.getDataContext())));
        for (String bean : classifierNameList) {
            List<Map> list = dc.getDAO().findByExample(bean, new HashMap());
            dc.processReturnResult(list);
            result.put(bean, list);
        }
        logger.debug("dsB2BDictionaryClassifierLoad end");
        return result;
    }
     */

    @WsMethod(requiredParams = {"CLASSIFIERNAMELIST"})
    public Map<String, Object> dsB2BDictionaryClassifierLoad(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDictionaryClassifierLoad begin");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> classifierNameList = (List<String>) params.get("CLASSIFIERNAMELIST");
        List<Map<String, Object>> list;
        for (String bean : classifierNameList) {
            if (DCT_MODULE_PREFIX_MAP.get(bean) != null) {
                list = dctFindByExample(DCT_MODULE_PREFIX_MAP.get(bean) + bean, null, isCallFromGate);
            } else {
                list = dctFindByExample(bean, null, isCallFromGate);
            }
            result.put(bean, list);
        }
        logger.debug("dsB2BDictionaryClassifierLoad end");
        return result;
    }

    // получение сведений справочника по имени классификатора (CLASSIFIERNAME)
    // необязательные параметры:
    // CLASSIFIERDATAPARAMS - мапа с параметрами для ограничения запрашиваемых из классификатора записей (имена параметров должны соответствовать именам полей классификатора)
    // ReturnListOnly - если true, то возвращает только список с записями классификатора; иначе - возвращает мапу (ключ - имя классификатора, значение - список с записями классификатора)
    @WsMethod(requiredParams = {"CLASSIFIERNAME"})
    public Map<String, Object> dsB2BDictionaryClassifierDataLoadByName(Map<String, Object> params) throws Exception {

        logger.debug("Getting classifier data by classifier name...");

        //String login = getStringParam(params, LOGIN);
        //String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);

        String clsName = getStringParamLogged(params, "CLASSIFIERNAME");
        Map<String, Object> clsDataParams = (Map<String, Object>) params.get("CLASSIFIERDATAPARAMS");

        // обращение к словарной системе для получения данных классификатора
        List<Map<String, Object>> hbDataList = dctFindByExample(clsName, clsDataParams, isCallFromGate);

        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        boolean isReturnListOnly = getBooleanParam(params.get(RETURN_LIST_ONLY), Boolean.FALSE);
        if (isReturnListOnly) {
            logger.debug("Requested returning only list of classifier data records.");
            result.put(RESULT, hbDataList);
        } else {
            logger.debug("Returning map with classifier name as key and list of classifier data records as value.");
            result.put(clsName, hbDataList);
        }

        logger.debug("Getting classifier data finished.");

        return result;
    }

}
