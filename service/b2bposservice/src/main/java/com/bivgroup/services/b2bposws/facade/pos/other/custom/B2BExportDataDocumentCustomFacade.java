package com.bivgroup.services.b2bposws.facade.pos.other.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.facade.B2BFileSessionController;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.bivgroup.sessionutils.SessionController;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;

/**
 *
 * @author ilich
 */
@BOName("B2BExportDataDocumentCustom")
public class B2BExportDataDocumentCustomFacade extends B2BBaseFacade {

//    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
//    final private byte[] Salt = {
//        (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
//        (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};
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

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BExportDataDocumentBrowseListByParamEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();

        Map<String, Object> attachParams = new HashMap<String, Object>();
        attachParams.put("OBJID", params.get("OBJID"));
        List<Map<String, Object>> attachList = WsUtils.getListFromResultMap(this.callService(Constants.B2BPOSWS, "dsB2BExportData_BinaryFile_BinaryFileBrowseListByParam", attachParams, login, password));
        if (attachList != null) {
            docList.addAll(attachList);
        }

        // загрузка путей для загрузки документов
        processDocListForUpload(docList, params, login, password);
        //
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, docList);
        return result;
    }

//    private void processDocListForUpload(List<Map<String, Object>> docList, Map<String, Object> params, String login, String password) throws Exception {
//        if ((params.get("URLPATH") != null) && (params.get("SESSIONIDFORCALL") != null) && (docList != null)) {
//            String pathPrefix = params.get("URLPATH").toString() + "?sid=" + params.get("SESSIONIDFORCALL").toString() + "&fn=";
//            for (Map<String, Object> docItem : docList) {
//                if (docItem.get("FILEPATH") != null) {
//                    String docPath = docItem.get("FILEPATH").toString();
//                    String docName = docPath;
//                    if (docItem.get("ISPDF") == null) {
//                        if (docPath.contains("\\")) {
//                            docName = docPath.substring(docPath.indexOf("\\") + 1);
//                        }
//                        if (docName.contains("/")) {
//                            docName = docPath.substring(docPath.indexOf("/") + 1);
//                        }
//                    }
////                    StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
//                    SessionController controller = new B2BFileSessionController();
//                    Map<String,Object> sessionParams = new HashMap<>();
//                    String userDocName = docItem.get("FILENAME").toString();
//                    sessionParams.put(B2BFileSessionController.USER_DOCNAME_PARAMNAME,userDocName);
//                    if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (docItem.get("FSID") != null)) {
//                        sessionParams.put(B2BFileSessionController.FS_TYPE_PARAMNAME,Constants.FS_EXTERNAL);
//                        sessionParams.put(B2BFileSessionController.SOME_ID_PARAMNAME,docItem.get("FSID").toString());
//                    } else {
//                        sessionParams.put(B2BFileSessionController.FS_TYPE_PARAMNAME,Constants.FS_HARDDRIVE);
//                        sessionParams.put(B2BFileSessionController.SOME_ID_PARAMNAME,docName);
//                    }
//
//                    String filenameEncript = controller.createSession(sessionParams);
//                    // для PDF передаем скачку по полному пути
//                    if (docItem.get("ISPDF") != null) {
//                        docItem.put("DOWNLOADPATH", pathPrefix + filenameEncript + "&fp=1");
//                    } else {
//                        docItem.put("DOWNLOADPATH", pathPrefix + filenameEncript + "&fp=0");
//                    }
//                    docItem.remove("FILEPATH");
//                }
//            }
//        }
//        // доп. загрузка информации по лицам
//        if ((params.get("LOADCRMDATA") != null) && (Long.valueOf(params.get("LOADCRMDATA").toString()).longValue() == 1)) {
//            for (Map<String, Object> docItem : docList) {
//                if (docItem.get("PARTICIPANTID") != null) {
//                    Map<String, Object> crmParams = new HashMap<String, Object>();
//                    crmParams.put(RETURN_AS_HASH_MAP, "TRUE");
//                    crmParams.put("PARTICIPANTID", docItem.get("PARTICIPANTID"));
//                    Map<String, Object> crmRes = this.callService(Constants.CRMWS, "participantGetById", crmParams, login, password);
//                    if (crmRes != null) {
//                        docItem.put("CRMDATA", crmRes);
//                    }
//                }
//            }
//        }
//    }

}
