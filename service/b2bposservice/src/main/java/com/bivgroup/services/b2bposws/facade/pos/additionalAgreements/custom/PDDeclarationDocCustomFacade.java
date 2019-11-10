/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Кастомный фасад для работы в бинарными файлами
 *
 * @author averichevsm
 */
@BOName("PDDeclarationDocCustom")
public class PDDeclarationDocCustomFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {"DECLARATIONID", "URLPATH"})
    public Map<String, Object> dsPDDeclarationDocCustomBrowseListByParamEx(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();

        boolean isNeedProcessForUpload = true; // подготавливать пути для загрузки ?

        if (params.get("ISNEEDPROCESSFORUPLOAD") != null) {
            isNeedProcessForUpload = (Boolean) params.get("ISNEEDPROCESSFORUPLOAD");
        }

        // находим доки по убытку
        Map<String, Object> qRes = this.selectQuery("dsPDDeclarationDocCustomBrowseListByParamEx", "dsPDDeclarationDocCustomBrowseListByParamExCount", params);
        if (qRes.get(ERROR) == null) {
            docList = (List<Map<String, Object>>) qRes.get(RESULT);
        }

        if (isNeedProcessForUpload) {
            // загрузка путей для загрузки документов
            processDocListForUpload(docList, params, login, password);
        }


        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, docList);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsPDDeclarationDocCustomBrowseListByParamEx4Integration(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();

        // находим доки по убытку
        Map<String, Object> qRes = this.selectQuery("dsPDDeclarationDocCustomBrowseListByParamEx4Integration", "dsPDDeclarationDocCustomBrowseListByParamEx4IntegrationCount", params);
        if (qRes.get(ERROR) == null) {
            docList = (List<Map<String, Object>>) qRes.get(RESULT);
        }
        //parseDates(docList, Date.class);

        // загрузка путей для загрузки документов
        processDocListForUpload(docList, params, login, password);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, docList);
        return result;
    }

    @WsMethod(requiredParams = {"DECLARATIONDOCID", "BINFILEID"})
    public Map<String, Object> dsPDDeclarationDocCustomDeleteEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("BINFILEID", params.get("BINFILEID"));
        Map<String, Object> resultReq = this.callService(Constants.B2BPOSWS, "dsPDDeclarationDoc_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
        if (resultReq != null && !resultReq.containsKey(ERROR)) {
            delMap.clear();
            delMap.put("DECLARATIONDOCID", params.get("DECLARATIONDOCID"));
            resultReq = this.callService(Constants.B2BPOSWS, "dsPDDeclarationDocDelete", delMap, login, password);
        }
        return resultReq;
    }

}
