package com.bivgroup.services.b2bposws.facade.pos.subsystem.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BSubsystemProductParamCustom")
public class B2BSubsystemProductParamCustomFacade extends B2BBaseFacade {

    protected static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;

    protected static final String ERROR_UNSUPPORTED_PARAMS = "Поддержка данного набора входных параметров не реализована!";
    protected static final String ERROR_INSUFFICIENT_PARAMS = "Недостаточно входных параметров!";
    protected static final String ERROR_INSUFFICIENT_PARAMS_OR_RESOLVE_FAILED = "Недостаточно входных параметров или не удалось определить зависимые параметры!";
    protected static final String ERROR_NO_HANDBOOK_RECORD_WAS_FOUND = "Не найдена запись в справочнике, соответствующая входным параметрам!";

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BSubsystemProductParamBrowseByParamsEx(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        String error = "";
        // ИД версии справочника может быть передан в различных ключах - приоритет у более однозначного
        Long hbDataVerId = getLongParamLogged(params, SUBSYSTEM_HBDATAVERID_PARAMNAME);
        if (hbDataVerId == null) {
            hbDataVerId = getLongParamLogged(params, HBDATAVERID_PARAMNAME);
        }
        if (hbDataVerId == null) {
            Long subSystemId = getLongParamLogged(params, SUB_SYSTEM_ID_PARAMNAME);
            if (subSystemId == null) {
                Long contractId = getLongParamLogged(params, CONTRID_PARAMNAME);
                if (contractId == null) {
                    error = ERROR_INSUFFICIENT_PARAMS;
                } else {
                    error = ERROR_UNSUPPORTED_PARAMS;
                    // todo: определить ИД подсистемы для договора
                    // callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContract...");
                    // subSystemId = getLongParamLogged(, SUB_SYSTEM_ID_PARAMNAME);
                    // todo: при чтении договора использовать запрос который вернет сразу все нужные данные (в т.ч. по продукту и пр.)
                }
            } else {
                // определение ИД версии справочника по ИД подсистемы
                Map<String, Object> subsystemParams = new HashMap<String, Object>();
                subsystemParams.put(SUB_SYSTEM_ID_PARAMNAME, subSystemId);
                subsystemParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> subsystem = null;
                try {
                    subsystem = callService(B2BPOSWS_SERVICE_NAME, "b2bSubSystemByParamEx", subsystemParams, logger.isDebugEnabled(), login, password);
                } catch (Exception ex) {
                    logger.error("Unable to get subsystem by id! See previous logged errors for details.");
                }
                hbDataVerId = getLongParamLogged(subsystem, HBDATAVERID_PARAMNAME);
                if (hbDataVerId == null) {
                    error = "Не удалось получить сведения о подсистеме по указанному ИД подсистемы!";
                }
            }
        }
        Long prodConfId = null;
        if (error.isEmpty()) {
            prodConfId = getLongParamLogged(params, PRODCONFID_PARAMNAME);
            if (prodConfId == null) {
                String productSysName = getStringParamLogged(params, PRODUCT_SYSNAME_PARAMNAME);
                error = ERROR_UNSUPPORTED_PARAMS;
                // todo: определить ИД конфигурации продукта по сис. наименованию продукта
                // productInfo = callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BProduct...");
                // prodConfId = getLongParamLogged(productInfo, PRODCONFID_PARAMNAME);
            }
        }
        // получение данных справочника
        Map<String, Object> hbDataRecord = null;
        if (error.isEmpty()) {
            if ((hbDataVerId == null) || (prodConfId == null)) {
                error = ERROR_INSUFFICIENT_PARAMS_OR_RESOLVE_FAILED;
            } else {
                Map<String, Object> hbDataParams = new HashMap<String, Object>();
                hbDataParams.put("HBDATAVERID", hbDataVerId);
                hbDataParams.put("PRODCONFID", prodConfId);
                Map<String, Object> hbDataRes = callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordBrowseListByParam", hbDataParams, logger.isDebugEnabled(), login, password);
                List<Map<String, Object>> hbDataList = getListFromResultMap(hbDataRes);
                if ((hbDataList == null) || (hbDataList.size() != 1)) {
                    error = ERROR_NO_HANDBOOK_RECORD_WAS_FOUND;
                } else {
                    // возвращена одна единственная строка - успешный запрос
                    hbDataRecord = hbDataList.get(0);
                }
            }
        }
        // результат
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            // формирование результата с добавлением доп. ключей в верхнем регистре
            if (hbDataRecord != null) {
                for (Map.Entry<String, Object> entry : hbDataRecord.entrySet()) {
                    Object value = entry.getValue();
                    String key = entry.getKey();
                    result.put(key, value);
                    if (key != null) {
                        String keyUpper = key.toUpperCase();
                        result.put(keyUpper, value);
                    }
                }
            }
        } else {
            String logError = "Unable to get subsystem product parameter! Method finished with error message '" + error + "'. Details (call params)";
            loggerErrorPretty(logger, logError, params);
            result = makeErrorResult(result, error);
        }
        return result;
    }

}
