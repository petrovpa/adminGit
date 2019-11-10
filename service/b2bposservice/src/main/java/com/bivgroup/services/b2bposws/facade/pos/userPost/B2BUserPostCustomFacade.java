package com.bivgroup.services.b2bposws.facade.pos.userPost;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import com.bivgroup.services.b2bposws.facade.B2BFileSessionController;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.bivgroup.sessionutils.SessionController;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;

import static com.bivgroup.services.b2bposws.facade.B2BFileSessionController.FS_TYPE_PARAMNAME;
import static com.bivgroup.services.b2bposws.facade.B2BFileSessionController.SOME_ID_PARAMNAME;
import static com.bivgroup.services.b2bposws.facade.B2BFileSessionController.USER_DOCNAME_PARAMNAME;

/**
 *
 * @author ilich
 */
@BOName("B2BUserPostCustom")
public class B2BUserPostCustomFacade extends B2BDictionaryBaseFacade {

    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    private final Logger logger = Logger.getLogger(this.getClass());

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

//    @WsMethod(requiredParams = {"ID"})
//    public Map<String, Object> dsB2BUserPostLoad(Map<String, Object> params) throws Exception {
//        logger.debug("dsB2BUserPostLoad begin");
//        boolean isCallFromGate = isCallFromGate(params);
//        DictionaryCaller dc = new DictionaryCaller(Termination.newInstance(DictionaryCaller.getDataSource(this.getDataContext())));
//        Map<String, Object> resMap = dc.getDAO().findById("UserPost", getLongParam(params, "id"));
//        dc.processReturnResult(resMap);
//        markAllMapsByKeyValue(resMap, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
//        if (isCallFromGate) {
//            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
//            parseDatesAfterDictionaryCalls(resMap);
//        }
//        logger.debug("dsB2BUserPostLoad end");
//        return resMap;
//    }

    private void setAuthAspectFields(List<Map<String, Object>> userPostList, Long userAccountId) {
        if (userPostList != null) {
            for (Map<String, Object> bean : userPostList) {
                RowStatus rowStatus = getRowStatusLogged(bean);
                if (rowStatus.equals(INSERTED)) {
                    bean.put("CreateDate", new Date());
                    bean.put("CreateUser", userAccountId);
                }
            }
        }
    }

    private void processUserPostRefs(List<Map<String, Object>> userPostList, String login, String password) throws Exception {
        if (userPostList != null) {
            for (Map<String, Object> bean : userPostList) {
                if (bean.get("CreateUser") != null) {
                    Map<String, Object> getparams = new HashMap<String, Object>();
                    getparams.put("USERACCOUNTID", bean.get("CreateUser"));
                    Map<String, Object> res = this.selectQuery("dsGetLoginInfoById", null, getparams);
                    if (res != null) {
                        if (res.get(RESULT) != null) {
                            List<Map<String, Object>> resList = (List<Map<String, Object>>) res.get(RESULT);
                            if (!resList.isEmpty()) {
                                String lastName = getStringParam(resList.get(0).get("LASTNAME"));
                                String firstName = getStringParam(resList.get(0).get("FIRSTNAME"));
                                String middleName = getStringParam(resList.get(0).get("MIDDLENAME"));
                                String fio = lastName + " " + firstName;
                                if (!middleName.isEmpty()) {
                                    fio = fio + " " + middleName;
                                }
                                bean.put("CreateUserFIO", fio);
                            }
                        }
                    }
                }
            }
        }
    }
//
//    @WsMethod(requiredParams = {"USERPOSTLIST"})
//    public Map<String, Object> dsB2BUserPostListSave(Map<String, Object> params) throws Exception {
//        logger.debug("dsB2BUserPostListSave begin");
//        String login = params.get(LOGIN).toString();
//        String password = params.get(PASSWORD).toString();
//        boolean isCallFromGate = isCallFromGate(params);
//        if (isCallFromGate) {
//            // преобразование всех дат (*$date*) в Date.class для вызова операций через словарную систему
//            parseDatesBeforeDictionaryCalls(params);
//            // если добавляем сообщение, тогда нужно вручную проставить CreateDate и CreateUser (пока нет аспектов)
//            Long userAccountId = getLongParamLogged(params, "SESSION_USERACCOUNTID");
//            setAuthAspectFields((List<Map<String, Object>>) params.get("USERPOSTLIST"), userAccountId);
//        }
//
//        List<Map<String, Object>> resultList = null;
//        DictionaryCaller dc = new DictionaryCaller(Termination.newInstance(DictionaryCaller.getDataSource(this.getDataContext())));
//        dc.beginTransaction();
//        try {
//            List<Map> userPostList = (List<Map>) params.get("USERPOSTLIST");
//            List<Map> rawResult = dc.getDAO().crudByHierarchy("UserPost", userPostList);
//            dc.commit();
//            resultList = dc.processReturnResult(rawResult);
//            markAllMapsByKeyValue(rawResult, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
//            if (isCallFromGate) {
//                // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
//                parseDatesAfterDictionaryCalls(resultList);
//            }
//        } catch (Exception ex) {
//            dc.rollback();
//            logger.error("Exeption in dsB2BUserPostListSave: ", ex);
//            throw new Exception(ex);
//        }
//        // если вызов был из гейта, необходимо выполнить разыменовки
//        if (isCallFromGate) {
//            processUserPostRefs(resultList, login, password);
//        }
//        //
//        Map<String, Object> result = new HashMap<String, Object>();
//        result.put("USERPOSTLIST", resultList);
//        logger.debug("dsB2BUserPostListSave end");
//        return result;
//    }

//    @WsMethod(requiredParams = {"PDDeclarationID"})
//    public Map<String, Object> dsB2BUserPostListLoadByDeclarationId(Map<String, Object> params) throws Exception {
//        logger.debug("dsB2BUserPostListLoadByDeclarationId begin");
//        String login = params.get(LOGIN).toString();
//        String password = params.get(PASSWORD).toString();
//        boolean isCallFromGate = isCallFromGate(params);
//        DictionaryCaller dc = new DictionaryCaller(Termination.newInstance(DictionaryCaller.getDataSource(this.getDataContext())));
//        Map<String, Object> p = new HashMap<String, Object>();
//        p.put("PDDeclarationID", getLongParam(params, "PDDeclarationID"));
//        List<Map> resRawList = dc.getDAO().findByExample("UserPost", p);
//        List<Map<String, Object>> resList = dc.processReturnResult(resRawList);
//        markAllMapsByKeyValue(resRawList, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
//        if (isCallFromGate) {
//            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
//            parseDatesAfterDictionaryCalls(resList);
//        }
//        // если вызов был из гейта, необходимо выполнить разыменовки
//        if (isCallFromGate) {
//            processUserPostRefs(resList, login, password);
//        }
//        //
//        Map<String, Object> result = new HashMap<String, Object>();
//        result.put(RESULT, resList);
//        logger.debug("dsB2BUserPostListLoadByDeclarationId end");
//        return result;
//    }

    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsB2BUserPostAttachDocBrowse(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> browseParams = new HashMap<String, Object>();
        browseParams.put("OBJID", params.get("ID"));
        Map<String, Object> result = this.callService(Constants.B2BPOSWS, "dsB2BUserPostAttachment_BinaryFile_BinaryFileBrowseListByParam", browseParams, login, password);
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
//                    SessionController controller = new B2BFileSessionController();
//                    Map<String, Object> sessionParams = new HashMap<>();
//
//                    String userDocName = docItem.get("FILENAME").toString();
//                    String fileNameStr;
//                    if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (docItem.get("FSID") != null)) {
//                        //fileNameStr = Constants.FS_EXTERNAL + "@" + docItem.get("FSID").toString() + "@" + userDocName;
//                        sessionParams.put(FS_TYPE_PARAMNAME, Constants.FS_EXTERNAL);
//                        sessionParams.put(SOME_ID_PARAMNAME, docItem.get("FSID").toString());
//                        sessionParams.put(USER_DOCNAME_PARAMNAME, userDocName);
//
//                    } else {
////                        fileNameStr = Constants.FS_HARDDRIVE + "@" + docName + "@" + userDocName;
//                        sessionParams.put(FS_TYPE_PARAMNAME, Constants.FS_HARDDRIVE);
//                        sessionParams.put(SOME_ID_PARAMNAME, docName);
//                        sessionParams.put(USER_DOCNAME_PARAMNAME, userDocName);
//                    }
//
//                    filenameEncript = controller.createSession(sessionParams);
//                    //filenameEncript = scu.encrypt(fileNameStr + "@" + UUID.randomUUID());
//
//                    docItem.put("DOWNLOADPATH", pathPrefix + filenameEncript);
//                    docItem.remove("FILEPATH");
//                }
//            }
//        }
//    }

    @WsMethod(requiredParams = {"BINFILEID"})
    public void dsB2BUserPostAttachDocDelete(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("BINFILEID", params.get("BINFILEID"));
        this.callService(Constants.B2BPOSWS, "dsB2BUserPostAttachment_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
    }
}
