/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;

/**
 * @author sambucus
 */
@BOName("SHTaskCustom")
public class SHTaskCustomFacade extends B2BBaseFacade {

    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final private byte[] Salt = {
            (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
            (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};
    private static final String PROJECT_PARAM_NAME = "project";
    public static final String SERVICE_NAME = Constants.B2BPOSWS;

    /*
    // здесь более не требуется - будет доступно за счет B2BBaseFacade implements SeaweedsGetters
    protected String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }
    */

    private final Logger logger = Logger.getLogger(SHTaskCustomFacade.class);

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsSHTaskCreateEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> saveParams = new HashMap<>();
        saveParams.put("AUTONUMBERSYSNAME", "contractCancelApplicationAutoNum");
        saveParams.put("CONTRID", params.get("CONTRID"));
        saveParams.put("CLAIMERID", params.get("CLAIMERID"));
        saveParams.put("CLAIMERTYPEID", params.get("CLAIMERTYPEID"));
        saveParams.put("CLAIMERSYSNAME", params.get("CLAIMERSYSNAME"));
        saveParams.put("EXTSYSTEMID", params.get("CONTRID"));
        // дата расторжения
        //saveParams.put("DEF_DATE", params.get("CANCELDATE"));
        Date cancelDate = (Date) parseAnyDate(params.get("CANCELDATE"), Date.class, "CANCELDATE");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        //saveParams.put("DEF_DATE", sdf.format(cancelDate));
        saveParams.put("DEF_FDATE", xmlUtil.convertDateToBigDecimal(cancelDate));
        // дата cоздания
        Date now = new Date();
        saveParams.put("CREATEFDATE", xmlUtil.convertDateToBigDecimal(now));
        String descr = "Заявление на расторжение договора №" + getStringParam(params.get("CONTRNUMBER")) + " " + getStringParam(params.get("NOTE"));
        saveParams.put("DESCRIPTION", descr);
        saveParams.put("NOTE", params.get("NOTE"));
        //constvals
        saveParams.put("SHTASK_STATUSID", 21);
        Long shKindId = getShKindId();
        //saveParams.put("SHKINDID", shKindId);
        saveParams.put("SHKINDID", params.get("SHKINDID"));
        saveParams.put("ENTITYID", 5890);

        Long id = getLongParam(params.get("ID"));
        Map<String, Object> res = null;
        if (id != null) {
            saveParams.put("ID", id);
            res = this.callService(Constants.B2BPOSWS, "dsSHTaskUpdate", saveParams, login, password);
        } else {

            res = this.callService(Constants.B2BPOSWS, "dsSHTaskCreate", saveParams, login, password);
        }

        return res;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BSHtaskLoad(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> saveParams = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        saveParams.put("CONTRID", params.get("CONTRID"));
        saveParams.put("SHKINDID", params.get("SHKINDID"));

        Map<String, Object> res = this.callService(Constants.B2BPOSWS, "dsSHTaskBrowseListByParam", saveParams, login, password);
        if (res != null) {
            if (res.get(RESULT) != null) {
                List<Map<String, Object>> resList = (List<Map<String, Object>>) res.get(RESULT);
                if (!resList.isEmpty()) {
                    res = resList.get(0);
                    if (res.get("CLAIMERID") != null) {
                        // загрузить партиципанта.
                        Map<String, Object> participantMap = loadParticipant(res.get("CLAIMERID"), login, password);
                        res.put("TERMINATORMAP", participantMap);
                    }
                    parseDates(res, String.class);
                    result = res;
                }
            }
        }

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BSHtaskLoadAttachFiles(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> saveParams = new HashMap<>();
        //Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();
        saveParams.put("ID", params.get("ID"));
        if (docList == null) {
            docList = new ArrayList<Map<String, Object>>();
        }
        Map<String, Object> attachParams = new HashMap<String, Object>();
        attachParams.put("OBJID", params.get("ID"));
        List<Map<String, Object>> attachList = WsUtils.getListFromResultMap(this.callService(Constants.B2BPOSWS, "dsSHTask_BinaryFile_BinaryFileBrowseListByParam", attachParams, login, password));
        if (attachList != null) {
            docList.addAll(attachList);
        }

        // загрузка путей для загрузки документов
        processDocListForUpload(docList, params, login, password);

        //
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, docList);
        return result;

        //return result;
    }

    private Long getShKindId() throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("BRIEF", "ЗаявлениеНаРасторжениеФронт");
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.selectQuery("getShKind", null, param);
        if (res.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) res.get(RESULT);
            if (!resList.isEmpty()) {
                if (resList.get(0).get("SHTASK_KINDID") != null) {
                    return getLongParam(resList.get(0).get("SHTASK_KINDID"));
                } else {
                    logger.debug("shkind not found");
                }
            }
        }

        return 0L;
    }

}
