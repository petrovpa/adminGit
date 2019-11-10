/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomBaseFacade.BIVSBERPOSWS_SERVICE_NAME;
import com.bivgroup.services.bivsberposws.system.Constants;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author 1
 */
@BOName("InsParticipantBinFileCustom")
public class InsParticipantBinFileCustomFacade extends BaseFacade {

    protected static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(InsParticipantBinFileCustomFacade.class);

    protected String getUploadPath() {
        String path;
        Config config = Config.getConfig("bivsberlossws");
        //бинари файлы могут быть только в upload
        path = config.getParam("uploadPath", "C://Diasoft/UPLOAD");
        return path;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsParticipantBinFileBrowseByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> pbfRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsParticipantBinFileBrowseListByParam", params, login, password);
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        if (pbfRes != null) {
            if (pbfRes.get(RESULT) != null) {
                List<Map<String, Object>> binList = WsUtils.getListFromResultMap(pbfRes);
                for (Map<String, Object> binBean : binList) {
                    Map<String, Object> binParam = new HashMap<String, Object>();
                    binParam.put("OBJID", binBean.get("PARTICIPANTBINFILEID"));
                    binParam.put("OBJTABLENAME", "INS_PARTICIPANTBINFILE");

                    Map<String, Object> binRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsParticipantBinFile_BinaryFile_BinaryFileBrowseListByParam", binParam, login, password);
                    if (binRes != null) {
                        if (binRes.get(RESULT) != null) {
                            List<Map<String, Object>> fileList = WsUtils.getListFromResultMap(binRes);
                            for (Map<String, Object> fileBean : fileList) {
                                fileBean.put("NOTE", binBean.get("NOTE"));
                                fileBean.put("ISVALID", binBean.get("ISVALID"));
                                fileBean.put("PARTICIPANTBINFILEID", binBean.get("PARTICIPANTBINFILEID"));
                                fileBean.put("PARTICIPANTID", binBean.get("PARTICIPANTID"));
                            }
                            resList.addAll(fileList);
                        }
                    }
                }
            }
        }
        result.put(RESULT, resList);
        result.put("Status", "OK");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsParticipantBinFileCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = null;
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // 1. парсим имя файла. получаем из него хэш договора, тип документа, и имя файла
        String path = getUploadPath();

        String fileName = params.get("name").toString();
        String filePath = params.get("path").toString();

        String[] fileNameArr = fileName.split("_",3);
        String hash = fileNameArr[0];
        String typeSysName = fileNameArr[1];

        // 2. по хешу получаем договор, из договора берем insuredid
        Map<String, Object> contrParams = new HashMap<String, Object>();
        Map<String, Object> contrMap = new HashMap<String, Object>();
        contrMap.put("orderNum", hash);
        contrParams.put("CONTRMAP", contrMap);

        Map<String, Object> contrRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsSisContractBrowseEx", contrParams, login, password);
        if (contrRes.get("CONTRMAP") == null) {
            contrRes = (Map<String, Object>) contrRes.get(RESULT);
        }
        if (contrRes.get("CONTRMAP") != null) {
            contrMap = (Map<String, Object>) contrRes.get("CONTRMAP");
            if (contrMap.get("INSUREDID") != null) {

                Long contrId = Long.valueOf(contrMap.get("CONTRID").toString());
                String contrNum = contrMap.get("CONTRNUMBER").toString();
                Long participantId = Long.valueOf(contrMap.get("INSUREDID").toString());
                // 3. создаем запись в таблице ins_participantBinFile
             /*   Map<String, Object> pbf = new HashMap<String, Object>();
                pbf.put("PARTICIPANTID", participantId);
                pbf.put("ReturnAsHashMap", "TRUE");

                Map<String, Object> pbfbrowseRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsParticipantBinFileBrowseListByParam", pbf, login, password);
                if (pbfbrowseRes.get("PARTICIPANTBINFILEID") == null) {*/
                // на каждый файл нужна своя запись. т.к. у документа должен быть флаг валидности и комментарий.
                    Map<String, Object> pbf1 = new HashMap<String, Object>();
                    pbf1.put("PARTICIPANTID", participantId);
                    pbf1.put("ReturnAsHashMap", "TRUE");
                    Map<String, Object> pbfbrowseRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsParticipantBinFileCreate", pbf1, login, password);
             //   }
                if (pbfbrowseRes.get("PARTICIPANTBINFILEID") != null) {
                    // 4. cоздаем связь с бинари файлом.                    
                    File f = new File(filePath);
                    if (f.exists()) {
                        Map<String, Object> binParams = new HashMap<String, Object>();
                        binParams.put("OBJID", pbfbrowseRes.get("PARTICIPANTBINFILEID"));
                        binParams.put("FILENAME", fileNameArr[2]);
                        // файлы из uploadPath храним без пути.
                        binParams.put("FILEPATH", fileName); //f.getPath());
                        binParams.put("FILESIZE", f.length());
                        binParams.put("FILETYPEID", 21);
                        binParams.put("FILETYPENAME", typeSysName);//"Полис подписанный");
                        binParams.put("NOTE", "CONTRID=" + contrId.toString() + "; CONTRNUMBER:" + contrNum);
                        logger.debug("binfile Create: " + binParams.toString());
                        this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsParticipantBinFile_BinaryFile_createBinaryFileInfo", binParams, login, password);
                    }
                }
            }
        }
        return result;
    }
}
