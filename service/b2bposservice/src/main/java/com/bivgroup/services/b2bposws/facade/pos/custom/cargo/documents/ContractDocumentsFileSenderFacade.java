/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.cargo.documents;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import static com.bivgroup.services.b2bposws.system.Constants.BIVSBERPOSWS;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import ru.diasoft.services.config.Config;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author kkulkov
 */
@BOName("ContractDocumentsFileSenderFacade")
public class ContractDocumentsFileSenderFacade extends B2BBaseFacade {

    private static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    private static final String INSPRODUCTWS_SERVICE_NAME = Constants.INSPRODUCTWS;
    private static final String LIBREOFFICEREPORTSWS_SERVICE_NAME = Constants.LIBREOFFICEREPORTSWS;
    private static final String WEBSMSWS_SERVICE_NAME = Constants.SIGNWEBSMSWS;
    private static final String SIGNWS_SERVICE_NAME = Constants.SIGNWS;
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    private static final String VALIDATORSWS_SERVICE_NAME = Constants.VALIDATORSWS;

    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID", "STATESYSNAME", "CONTRNUMBER"})
    public Map<String, Object> dsGetDocumentsPackage(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Long contrId = getLongParam(params.get("CONTRID"));
        Long prodConfId = getLongParam(params.get("PRODCONFID"));
        Map<String, Object> queryParams = new HashMap<String, Object>();
        Map<String, Object> result = new HashMap<String, Object>();
        queryParams.clear();
        queryParams.put("PRODCONFID", params.get("PRODCONFID"));
        queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> configRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", queryParams, login, password);
        String url = configRes.get("SIGNSERVERURL").toString();
        if (url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, url.length() - 1);
        }
        if (params.get("DWNLDSERVLETPATH") != null) {
            url = url + params.get("DWNLDSERVLETPATH").toString();
        } else {
            // по-умолчанию, оставлено для совместимости
            url = url + "/bivsberlossws/b2bfileupload";
        }
        result.put("SERVERURL", url);
        Long needCheck = getLongParam(params.get("NEEDCHECK"));
        if (needCheck > 0) {
            //Проверка - все ли документы прикреплены.
            Long checkId = null;
            if ((configRes != null) && (configRes.get("DOWNLOADDOCUMENTSCHECKID") != null)) {
                checkId = getLongParam(configRes.get("DOWNLOADDOCUMENTSCHECKID"));
            }
            if ((checkId != null) && (checkId > 0)) {
                Map<String, Object> checkNameParams = new HashMap<String, Object>();

                checkNameParams.put("ReturnAsHashMap", "TRUE");
                checkNameParams.put("CHECKID", checkId);
                Map<String, Object> rCheckNameParams = this.callService(VALIDATORSWS_SERVICE_NAME, "dsCheckBrowseListByParamEx", checkNameParams, login, password);
                if (rCheckNameParams.get("NAME") != null) {
                    Map<String, Object> checkParams = new HashMap<String, Object>();
                    checkParams.putAll(params);
                    checkParams.put("VALIDATORNAME", rCheckNameParams.get("NAME"));
                    checkParams.put("VALIDATORRESULTTYPE", 1L);
                    checkParams.put("CONTRID", params.get("CONTRID"));
                    checkParams.put("PRODCONFID", params.get("PRODCONFID"));
                    Map<String, Object> rCheckParams = this.callService(VALIDATORSWS_SERVICE_NAME, "dsValidateByValidatorName", checkParams, login, password);
                    if (rCheckParams.get(RESULT) != null) {
                        Map<String, Object> checkRes = (Map<String, Object>) rCheckParams.get(RESULT);
                        if (checkRes.get("FINALRESULT") != null) {
                            if (!checkRes.get("FINALRESULT").toString().equalsIgnoreCase("0")) {
                                result.putAll(checkRes);
                                return result;
                            }
                        }
                    }
                }
            }
        }
        Map<String, Object> queryDocumentsParams = new HashMap<String, Object>();
        Map<String, Object> docFiles = new HashMap<String, Object>();
        queryDocumentsParams.put("PRODCONFID", params.get("PRODCONFID"));
        queryDocumentsParams.put("EDOC", 1L);
        Map<String, Object> documentsRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductReportBrowseListByParamEx", queryDocumentsParams, login, password);
        if ((documentsRes != null) && (documentsRes.get(RESULT) != null)) {
            if (documentsRes.get(RESULT) instanceof List) {
                String uploadPath = Config.getConfig().getParam("uploadPath", "");
                File documentFile = new File(uploadPath + "edoc" + new SimpleDateFormat("yyyymmddhhmmss").format(new Date()) + ".zip");
                OutputStream outputStream = new FileOutputStream(documentFile);
                ZipArchiveOutputStream zos = new ZipArchiveOutputStream(outputStream);
                zos.setEncoding("CP866");
                List<Map<String, Object>> docList = (List<Map<String, Object>>) documentsRes.get(RESULT);
                for (Map<String, Object> doc : docList) {
                    Map<String, Object> printParams = new HashMap<String, Object>();
                    printParams.put("CONTRID", contrId);
                    printParams.put("PRODCONFID", prodConfId);
                    printParams.put("REPORTDATA", doc);
                    doc.put("REPORTFORMATS", ".pdf");
                    printParams.put(RETURN_AS_HASH_MAP, "TRUE");
                    Map<String, Object> pResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPrintDocumentsWithFileNames", printParams, login, password);
                    if (pResult != null) {
                        docFiles.put(pResult.get("FILEPATH").toString(), pResult);
                        File inputFile = new File(uploadPath + pResult.get("FILEPATH").toString());
                        if (inputFile.getCanonicalPath().startsWith(uploadPath)) {
                            InputStream entryInputStream = new FileInputStream(inputFile);
                            ZipArchiveEntry entry = new ZipArchiveEntry(inputFile, pResult.get("USERFILENAME").toString());
                            zos.putArchiveEntry(entry);
                            byte[] buf = new byte[8000];
                            int nLength;
                            while (true) {
                                nLength = entryInputStream.read(buf);
                                if (nLength < 0) {
                                    break;
                                }
                                zos.write(buf, 0, nLength);
                            }
                            zos.closeArchiveEntry();
                        }
                    }
                }
                zos.close();
                queryParams.clear();
                queryParams.put("INPUTSTR", documentFile.getName() + "@" + "Документы к договору" + params.get("CONTRNUMBER").toString() + ".zip");
                queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
                Map<String, Object> encriptRes = this.callService(BIVSBERPOSWS, "dsEncriptString", queryParams, login, password);
                String encriptString = getStringParam(encriptRes.get("OUTPUTSTR"));
                if (encriptString != null) {
                    result.put("ENCRIPTSTRING", encriptString);
                }

            }
        }
        return result;
    }
}
