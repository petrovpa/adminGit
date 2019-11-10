/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.loss;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Кастомный фасад для работы в бинарными файлами
 *
 * @author arazumovskiy
 */
@BOName("B2BLossNoticeDocCustom")
public class B2BLossNoticeDocCustomFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {"LOSSNOTICEID", "URLPATH"})
    public Map<String, Object> dsB2BLossNoticeDocCustomBrowseListByParamEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();

        // находим доки по убытку
        Map<String, Object> qRes = this.selectQuery("dsB2BLossNoticeDocCustomBrowseListByParamEx", "dsB2BLossNoticeDocCustomBrowseListByParamExCount", params);
        if (qRes.get(ERROR) == null) {
            docList = (List<Map<String, Object>>) qRes.get(RESULT);
        }

        // загрузка путей для загрузки документов
        processDocListForUpload(docList, params, login, password);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, docList);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BLossNoticeDocCustomBrowseListByParamEx4Integration(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();

        // находим доки по убытку
        Map<String, Object> qRes = this.selectQuery("dsB2BLossNoticeDocCustomBrowseListByParamEx4Integration", "dsB2BLossNoticeDocCustomBrowseListByParamEx4IntegrationCount", params);
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
    @WsMethod(requiredParams = {"LOSSNOTICEDOCID", "BINFILEID"})
    public Map<String, Object> dsB2BBLossNoticeDocCustomDeleteEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("BINFILEID", params.get("BINFILEID"));
        Map<String, Object> resultReq = this.callService(Constants.B2BPOSWS, "dsB2BLossNoticeDoc_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
        if (resultReq != null && !resultReq.containsKey(ERROR)) {
            delMap.clear();
            delMap.put("LOSSNOTICEDOCID", params.get("LOSSNOTICEDOCID"));
            resultReq = this.callService(Constants.B2BPOSWS, "dsB2BLossNoticeDocDelete", delMap, login, password);
        }
        return resultReq;
    }
}
