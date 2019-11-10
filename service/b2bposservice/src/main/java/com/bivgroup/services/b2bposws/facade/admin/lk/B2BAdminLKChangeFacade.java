/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.admin.lk;

import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.cayenne.DataRow;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

/**
 * @author npetrov
 */
@BOName("B2BAdminMenu")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class B2BAdminLKChangeFacade extends B2BAdminLKBaseFacade {

    // Метод для получения файлов к заявлению на изменение(прикрепленные файлы и заявление на изменение)
    @WsMethod(requiredParams = {DECLARATION_ID_PARAMNAME, URLPATH_PARAMNAME})
    public Map<String, Object> dsB2BAdminLKGetAttachmentsForDeclarationOfChange(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        List<Map<String, Object>> resultFilesList = new ArrayList<>();

        // Пути загружать не нужно
        params.put("ISNEEDPROCESSFORUPLOAD", false);

        // Загружаем заявление
        Map<String, Object> resultAttachedFiles = this.callExternalService(Constants.B2BPOSWS, "dsPDDeclarationDocCustomBrowseListByParamEx", params, login, password);

        if ((resultAttachedFiles != null) && (resultAttachedFiles.get(RESULT) != null)) {
            resultFilesList.addAll((List<Map<String, Object>>) resultAttachedFiles.get(RESULT));
        }

        // Подготавливаем зашифрованый список файлов для ZIP файла(путь - имя)
        String encryptString = processDocListForUploadZip(resultFilesList, params, login, password);


        Map<String, Object> result = new HashMap<>();
        Map<String, Object> resultPrepareMap = new HashMap<>();

        resultPrepareMap.put("ENCRYPTSTRING", encryptString);

        result.put("STATUS", "OK");
        result.put(RESULT, resultPrepareMap);

        return result;
    }

    /**
     * Метод получения списка статусов в журнале изменений
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BChangeListGetStateList(Map<String, Object> params) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        Map<String, Object> result = this.selectQuery("dsB2BChangeListGetStateList", queryParams);
        return result;
    }

    /**
     * Метод получения списка страховых продуктов в журнале изменений
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BChangeListGetInsProductList(Map<String, Object> params) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        Map<String, Object> result = this.selectQuery("dsB2BChangeListGetInsProductList", queryParams);
        return result;
    }


    /**
     * Метод для загрузки заявлений на изменение
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BDeclarationOfChangeForContractCustomBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = selectQuery("dsB2BDeclarationOfChangeForContractCustomBrowseListByParam", params);
        return result;
    }

    private void setFilterParamsNames() throws Exception {
        Map<String, Object> customParams = new HashMap<String, Object>();
        customParams.put("METHODNAME", "dsB2BDeclarationOfChangeForContractGetOperations");
        Map<String, Object> resultRequest = this.selectQuery("dsB2BGetFilterParamsByFunctionName", "dsB2BGetFilterParamsByFunctionNameCount", customParams);
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultRequest.get(RESULT);
        DataRow result = (DataRow) resultList.get(0);
        String keyField = (String) result.get("KEYFIELD");
        if (keyField != null) {
            KEYFIELD = keyField;
        }
        String nameField = (String) result.get("NAMEFIELD");
        if (nameField != null) {
            NAMEFIELD = nameField;
        }
    }

    /* Получение списка операций
    * ВАЖНО: название функции захардкожено в setFilterParamsNames
    * */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BDeclarationOfChangeForContractGetOperations(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDeclarationOfChangeForContractGetOperations BEGIN");
        String login = params.get(WsConstants.LOGIN).toString();
        //String classifierName = ;//getStringParamLogged(params, "CLASSIFIERNAME");
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> resultParams = new HashMap<String, Object>();
        resultParams.put("CLASSIFIERNAME", KIND_CHANGEREASON);
        try {
            Map<String, Object> resultRequest = this.callService(Constants.B2BPOSWS, "dsB2BDictionaryClassifierDataLoadByName", resultParams, login, password);
            Map<String, Object> result = (Map<String, Object>) resultRequest.get(RESULT);
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get(KIND_CHANGEREASON);
            //Устранение дубликатов нам нужны только sysname и name
            setFilterParamsNames();
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> record : list) {
                Map<String, Object> resultRecord = new HashMap<String, Object>();
                if (record.get(KEYFIELD) instanceof String) {
                    resultRecord.put(KEYFIELD, "'" + getStringParam(record.get(KEYFIELD)) + "'");
                } else {
                    resultRecord.put(KEYFIELD, record.get(KEYFIELD));
                }
                resultRecord.put(NAMEFIELD, record.get(NAMEFIELD));
                if (!resultList.contains(resultRecord)) {
                    resultList.add(resultRecord);
                }
            }
            //Устранение дубликатов END
            resultRequest.put(RESULT, resultList);
            return resultRequest;
        } catch (Exception ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            List<Map<String, Object>> emptyList = new ArrayList<Map<String, Object>>();
            result.put(RESULT, emptyList);
            result.put(ERROR, ex.getMessage());
            logger.error("There is an error during retrieval of operations list", ex);
            return result;
        } finally {
            logger.debug("dsB2BDeclarationOfChangeForContractGetOperations END");
        }

    }
    
    /*
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BGetContractById(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contrParam = new HashMap<String, Object>();
        contrParam.put("CONTRID", params.get("CONTRID"));
        contrParam.put("ReturnAsHashMap", "TRUE");
        //TODO vpuchkov проверить на ошибки вызова
        Map<String, Object> contract = this.callService(Constants.B2BPOSWS, "dsB2BContrLoad", contrParam, login, password);
                Map<String, Object> integrationParam = new HashMap<String, Object>();
        
        integrationParam.put("POLICYID", contract.get("EXTERNALID"));
        integrationParam.put("CONTRNUMBER", contract.get("CONTRNUMBER"));
        contrParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> integrationCallRes = this.callExternalService(Constants.B2BPOSWS, "dsLifeIntegrationGetContractList", params, login, password);
        return integrationCallRes;
    }
     */
}
