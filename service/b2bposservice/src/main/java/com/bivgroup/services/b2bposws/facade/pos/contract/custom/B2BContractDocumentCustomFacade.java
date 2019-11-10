/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.contract.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * @author ilich
 */
@BOName("B2BContractDocumentCustom")
public class B2BContractDocumentCustomFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BContractDocumentBrowseListByParamEx(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromUniOpenAPI = isCallFromUniOpenAPI(params);
        List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();
        // если не взведен флаг загрузки только документов для договора (печатные документы, приложенные к договору)
        if ((params.get("ONLYCONTRATTACHDOC") == null) || ((Long.valueOf(params.get("ONLYCONTRATTACHDOC").toString())).longValue() == 0)) {
            Map<String, Object> qRes = this.selectQuery("dsB2BContractDocumentBrowseListByParamEx", "dsB2BContractDocumentBrowseListByParamExCount", params);
            docList = (List<Map<String, Object>>) qRes.get(RESULT);
        }
        // если флаг взведен, то также загружаем документы, которые прикреплены к самому договору (к таблице B2B_CONTR)
        if ((params.get("LOADATTACHTOCONTR") != null) && (Long.valueOf(params.get("LOADATTACHTOCONTR").toString()).longValue() == 1) && (params.get("CONTRID") != null)) {
            if (docList == null) {
                docList = new ArrayList<Map<String, Object>>();
            }
            String methodName;
            List<Map<String, Object>> attachList;
            Map<String, Object> attachParams = new HashMap<String, Object>();
            attachParams.put("OBJID", params.get("CONTRID"));
            String repLevelList = getStringParamLogged(params, "REPLEVELLIST");
            if (repLevelList.isEmpty()) {
                methodName = "dsB2BContract_BinaryFile_BinaryFileBrowseListByParam";
            } else {
                attachParams.put("OBJTABLENAME", "B2B_CONTR");
                attachParams.put("REPLEVELLIST", repLevelList);
                methodName = "dsB2BContractBinFileBrowseListByParamEx";
            }
            attachList = callServiceAndGetListFromResultMapLogged(Constants.B2BPOSWS, methodName, attachParams, login, password);
            if (attachList != null) {
                docList.addAll(attachList);
            }
        }
        // если взведен флаг подгрузки PDF документов, подгружаем их
        if ((params.get("LOADPDFDOCS") != null) && ((Long.valueOf(params.get("LOADPDFDOCS").toString())).longValue() == 1)) {
            // при этом должен быть указан конфиг продукта
            if (params.get("PRODCONFID") != null) {
                Map<String, Object> reportParams = new HashMap<String, Object>();
                reportParams.put("PRODCONFID", params.get("PRODCONFID"));
                if (isCallFromUniOpenAPI) {
                    // при вызове из OpenAPI следует учитывать еще и REPLEVEL
                    copyParamsIfNotNull(reportParams, params, "REPLEVEL", "REPLEVELLIST");
                }
                List<Map<String, Object>> reportList = WsUtils.getListFromResultMap(this.callService(Constants.B2BPOSWS, "dsB2BProductReportBrowseListByParamEx", reportParams, login, password));
                if (reportList != null) {
                    List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
                    for (Map<String, Object> bean : reportList) {
                        if (bean.get("TEMPLATENAME") != null) {
                            String templateName = bean.get("TEMPLATENAME").toString();
                            if (!((templateName.indexOf(".odt") > 1) || (templateName.indexOf(".ods") > 1))) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                String repFulPath = getTemplateFullPath(templateName, login, password);
                                repFulPath = repFulPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
                                map.put("FILEPATH", repFulPath);
                                map.put("FILENAME", bean.get("NAME"));
                                map.put("ISPDF", 1L);
                                if (isCallFromUniOpenAPI) {
                                    // при вызове из OpenAPI следует учитывать еще несколько параметров
                                    copyParamsIfNotNull(map, bean, "PRODREPID", "REPLEVEL", "REPTYPE");
                                }
                                resList.add(map);
                            }
                        }
                    }
                    if (resList.size() > 0) {
                        docList.addAll(resList);
                    }
                }
            }
        }
        // загрузка путей для загрузки документов
        if (isCallFromUniOpenAPI) {
            processDocListForUploadUniOpenAPI(docList, params, login, password);
        } else {
            processDocListForUpload(docList, params, login, password);
        }
        //
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, docList);
        return result;
    }

    @WsMethod(requiredParams = {"CONTRDOCID", "BINFILEID"})
    public void dsB2BContractDocumentDeleteEx(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("BINFILEID", params.get("BINFILEID"));
        this.callService(Constants.B2BPOSWS, "dsB2BContractDocument_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
        delMap.clear();
        delMap.put("CONTRDOCID", params.get("CONTRDOCID"));
        this.callService(Constants.B2BPOSWS, "dsB2BContractDocumentDelete", delMap, login, password);
    }

    @WsMethod(requiredParams = {"OBJID", "OBJTABLENAME"})
    public Map<String, Object> dsB2BContractBinFileBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = selectQuery("dsB2BContractBinFileBrowseListByParamEx", params);
        return result;
    }

}
