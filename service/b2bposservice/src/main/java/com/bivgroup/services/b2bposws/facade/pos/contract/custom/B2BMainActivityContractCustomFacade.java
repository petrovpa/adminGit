package com.bivgroup.services.b2bposws.facade.pos.contract.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;

/**
 *
 * @author ilich
 */
@BOName("B2BMainActivityContractCustom")
public class B2BMainActivityContractCustomFacade extends B2BBaseFacade {
    
    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    //private static final String SERVICE_NAME = Constants.B2BPOSWS;
    final private byte[] Salt = {
        (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
        (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};
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
    
    @WsMethod(requiredParams = {"MAINACTCONTRID"})
    public Map<String, Object> dsB2BMainActivityContractBaseAttachDocBrowse(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> browseParams = new HashMap<String, Object>();
        browseParams.put("OBJID", params.get("MAINACTCONTRID"));
        Map<String, Object> result = this.callService(Constants.B2BPOSWS, "dsB2BMainActivityContractBase_BinaryFile_BinaryFileBrowseListByParam", browseParams, login, password);
        List<Map<String, Object>> docList = (List<Map<String, Object>>) result.get(RESULT);
        processDocListForUpload(docList, params, login, password);
        return result;
    }

//    private void processDocListForUpload(List<Map<String, Object>> docList, Map<String, Object> params) {
//        if ((params.get("URLPATH") != null) && (params.get("SESSIONIDFORCALL") != null)) {
//            String pathPrefix = params.get("URLPATH").toString() + "?sid=" + params.get("SESSIONIDFORCALL").toString() + "&fn=";
//            for (Map<String, Object> docItem : docList) {
//                String filenameEncript = "";
//                if (docItem.get("FILEPATH") != null) {
//                    String docPath = docItem.get("FILEPATH").toString();
//                    String docName = docPath;
//                    if (docPath.indexOf("\\") >= 0) {
//                        docName = docPath.substring(docPath.indexOf("\\") + 1);
//                    }
//                    if (docName.indexOf("/") >= 0) {
//                        docName = docPath.substring(docPath.indexOf("/") + 1);
//                    }
//                    StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
//
//                    String userDocName = docItem.get("FILENAME").toString();
//                    String fileNameStr;
//                    if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (docItem.get("FSID") != null)) {
//                        fileNameStr = Constants.FS_EXTERNAL + "@" + docItem.get("FSID").toString() + "@" + userDocName;
//                    } else {
//                        fileNameStr = Constants.FS_HARDDRIVE + "@" + docName + "@" + userDocName;
//                    }
//
//                    filenameEncript = scu.encrypt(fileNameStr + "@" + UUID.randomUUID());
//
//                    docItem.put("DOWNLOADPATH", pathPrefix + filenameEncript);
//                    docItem.remove("FILEPATH");
//                }
//            }
//        }
//    }

    @WsMethod(requiredParams = {"BINFILEID"})
    public void dsB2BMainActivityContractBaseAttachDocDelete(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("BINFILEID", params.get("BINFILEID"));
        this.callService(Constants.B2BPOSWS, "dsB2BMainActivityContractBase_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
    }
    
    @WsMethod(requiredParams = {"ORGSTRUCTID"})
    public Map<String,Object> dsB2BMainActivityContractFindByOrgStructId(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMainActivityContractFindByOrgStructId", null, params);
        return result;
    }
    
    @WsMethod(requiredParams = {"SESSION_DEPARTMENTID"})
    public Map<String,Object> dsB2BMainActivityContractFindByOrgStructIdSessionParams(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        params.put("ORGSTRUCTID", params.get("SESSION_DEPARTMENTID"));
        return this.callService(Constants.B2BPOSWS, "dsB2BMainActivityContractFindByOrgStructId", params, login, password);
    }
    
}
