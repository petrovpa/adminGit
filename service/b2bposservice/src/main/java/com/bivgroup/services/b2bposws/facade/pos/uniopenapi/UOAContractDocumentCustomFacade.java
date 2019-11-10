package com.bivgroup.services.b2bposws.facade.pos.uniopenapi;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;

import static com.bivgroup.services.b2bposws.facade.pos.uniopenapi.UOAConstants.*;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("UOAContractDocumentCustom")
public class UOAContractDocumentCustomFacade extends UOABaseFacade {

    //
    @WsMethod(requiredParams = {"EXTERNALID"})
    public Map<String, Object> dsB2BContractDocumentBrowseListByParamExUniOpenAPI(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        // текст ошибки (будет пуст, если нет ошибок)
        StringBuilder error = new StringBuilder();
        // EXTERNALID -> contractId
        Map<String, Object> contract = getContractExtraBriefByExternalIdFromParams(params, error, login, password);
        Long contractId = getLongParamLogged(contract, "CONTRID");
        // сис. наименование состояния договора
        String contractStateSysName = getContractStateSysName(contract, error);
        Map<String, Object> fileListType = null;
        if (error.length() == 0) {
            params.put("CONTRID", contractId);
            params.putIfAbsent("ONLYCONTRATTACHDOC", 0);
            params.putIfAbsent("LOADATTACHTOCONTR", 1);
            // загружать также и статичные ПФ (памятки, правила и пр.), но также согласно REPLEVELLIST
            params.putIfAbsent("LOADPDFDOCS", 1);
            params.putIfAbsent(PRODCONFID_PARAMNAME, contract.get(PRODCONFID_PARAMNAME));
            Map<String, Object> fileTypesMap = getMapParam(params, "FILETYPES");
            List<String> fileTypeList = (List<String>) fileTypesMap.get("FILETYPE");
            StringBuilder repLevelList = new StringBuilder("0");
            // notFoundFileTypeSet - набор с сис. наименованиями не обнаруженных типов файлов
            Set<String> notFoundFileTypeSet = new HashSet<>();
            for (String fileType : fileTypeList) {
                // REPLEVEL = 111100 - полис образец
                // REPLEVEL = 111200 - платежка (ПД4)
                // REPLEVEL = 111300 - полис
                if (UNIOPENAPI_FILE_TYPE_POLICY.equals(fileType)) {
                    // при выдаче полиса следует различать черновик и чистовик по состоянию договора
                    if (B2B_CONTRACT_SG.equals(contractStateSysName)) {
                        // если договор подписан - чистовик полиса
                        repLevelList.append(", ").append(UNIOPENAPI_REPLEVEL_POLICY_SIGNED);
                    } else {
                        // во всех отсальных случаях - черновик полиса
                        repLevelList.append(", ").append(UNIOPENAPI_REPLEVEL_POLICY_DRAFT);
                    }
                } else if (UNIOPENAPI_FILE_TYPE_PD_4.equals(fileType)) {
                    repLevelList.append(", ").append(UNIOPENAPI_REPLEVEL_INVOICE);
                } else if (UNIOPENAPI_FILE_TYPE_APPLICATION.equals(fileType)) {
                    repLevelList.append(", ").append(UNIOPENAPI_REPLEVEL_APPLICATION);
                }
                // по умолчанию тип файла добавляется в набор с сис. наименованиями не обнаруженных типов файлов
                // (затем, в ходе обработки полученных файлов, элементы будут исключатся из не найденных)
                notFoundFileTypeSet.add(fileType);
            }
            params.put("REPLEVELLIST", repLevelList.toString());
            String serviceName = B2BPOSWS_SERVICE_NAME;
            String methodName = "dsB2BContractDocumentBrowseListByParamEx";
            List<Map<String, Object>> subResultList = callServiceAndGetListFromResultMapLogged(serviceName, methodName, params, login, password);
            // fileListType
            fileListType = new HashMap<>();
            if ((subResultList != null) && (subResultList.size() > 0)) {
                List<Map<String, Object>> fileList = new ArrayList<>();
                for (Map<String, Object> subResultBean : subResultList) {
                    Map<String, Object> file = new HashMap<>();
                    file.put("url", getStringParamLogged(subResultBean, "URL"));
                    // определение типа файла по значению REPLEVEL
                    String repLevel = getStringParamLogged(subResultBean, "REPLEVEL");
                    // todo: заменить на загрузку из БД когда/если будет сделан справочник для B2B_PRODREP.REPLEVEL
                    String fileType = UNIOPENAPI_FILE_TYPE_BY_REPLEVEL.get(repLevel);
                    if (fileType == null) {
                        fileType = UNIOPENAPI_FILE_TYPE_POLICY;
                    }
                    file.put("fileType", fileType);
                    fileList.add(file);
                    // исключение данного типа из набора с сис. наименованиями не обнаруженных типов файлов
                    notFoundFileTypeSet.remove(fileType);
                }
                for (String notFoundFileType : notFoundFileTypeSet) {
                    Map<String, Object> file = new HashMap<>();
                    file.put("fileType", notFoundFileType);
                    Map<String, Object> errorType = new HashMap<>();
                    errorType.put("code", 1L);
                    errorType.put("message", "Не удалось найти файлы указанного типа среди прикрепленных к договору.");
                    file.put("error", errorType);
                    fileList.add(file);
                }
                fileListType.put("file", fileList);
            }
        }
        // результат
        Map<String, Object> result = new HashMap<>();
        if (error.length() > 0) {
            result.put(ERROR, error.toString());
        } else {
            result.put(UNIOPENAPI_RESULT_CLASS_FILES, fileListType);
        }
        return result;
    }

}
