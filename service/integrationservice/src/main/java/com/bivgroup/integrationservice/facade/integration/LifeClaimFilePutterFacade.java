package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;
import com.bivgroup.seaweedfs.client.Location;
import com.bivgroup.seaweedfs.client.WeedFSClient;
import com.bivgroup.seaweedfs.client.WeedFSClientBuilder;
import com.bivgroup.seaweedfs.client.WeedFSFile;
import org.apache.commons.codec.binary.Base64;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.sberinsur.esb.partner.shema.*;
import ru.sberinsur.fuse.files.File;
import ru.sberinsur.fuse.files.Folder;
import ru.sberinsur.fuse.files.ShortFileInfo;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class LifeClaimFilePutterFacade extends IntegrationBaseFacade {

    @WsMethod
    public Map<String, Object> dsLifeIntegrationAssignChangeFiles(Map<String, Object> params) throws Exception {
        Map<String, Object> fileAssignParamMap = new HashMap<>();
        fileAssignParamMap.put("ENTITYID", "DECLARATIONID");
        fileAssignParamMap.put("ENTITYDOCID", "DECLARATIONDOCID");
        fileAssignParamMap.put("TABLENAME", "PD_DECLARATIONDOC");
        fileAssignParamMap.put("METHODNAMEPREFIX", "dsPDDeclarationDoc");
        fileAssignParamMap.put("OBJID", "DECLARATIONEXTID");
        fileAssignParamMap.put("TYPEOBJECT", "CHANGE");
        fileAssignParamMap.put("REQUESTTYPE", CHANGEFILEASSIGN);

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // получаем N первых попавшихся файлов - выгруженных в оис, но не связанных с заявками.
        // вызываем метод связи с заявкой.
        // в случае успеха переводим файл в состояние "Выгружен и связан"

        return assignFiles(params, fileAssignParamMap, login, password);
    }

    @WsMethod()
    public Map<String, Object> dsLifeIntegrationAssignClaimFiles(Map<String, Object> params) throws Exception {
        Map<String, Object> fileAssignParamMap = new HashMap<>();
        fileAssignParamMap.put("ENTITYID", "LOSSNOTICEID");
        fileAssignParamMap.put("ENTITYDOCID", "LOSSNOTICEDOCID");
        fileAssignParamMap.put("TABLENAME", "B2B_LOSSNOTICEDOC");
        fileAssignParamMap.put("METHODNAMEPREFIX", "dsB2BLossNoticeDoc");
        fileAssignParamMap.put("OBJID", "LOSSNOTICEEXTID");
        fileAssignParamMap.put("TYPEOBJECT", "CLAIM");
        fileAssignParamMap.put("REQUESTTYPE", CLAIMFILEASSIGN);

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // получаем N первых попавшихся файлов - выгруженных в оис, но не связанных с заявками.
        // вызываем метод связи с заявкой.
        // в случае успеха переводим файл в состояние "Выгружен и связан"

        return assignFiles(params, fileAssignParamMap, login, password);

    };


    private Map<String, Object> assignFiles(Map<String, Object> params, Map<String, Object> fileAssignParamMap, String login, String password) {

        // получаем N первых попавшихся файлов - выгруженных в оис, но не связанных с заявками.
        // вызываем метод связи с заявкой.
        // в случае успеха переводим файл в состояние "Выгружен и связан"


        Long packSize = getLongParam(params.get("PACKSIZE"));
        int ps = packSize.intValue();
        List<Map<String, Object>> fileList = null;
        try {
            fileList = getUnattachedFileListEx(params, getStringParam(fileAssignParamMap.get("TABLENAME")),
                    getStringParam(fileAssignParamMap.get("METHODNAMEPREFIX")) + "CustomBrowseListByParamEx4Integration", login, password);
            CopyUtils.sortByLongFieldName(fileList, getStringParam(fileAssignParamMap.get("ENTITYID")));

            int count = 0;
            try {
                if (fileList.size() > 0) {
                    DocumentListImportType dlit = new DocumentListImportType();
                    List<DocumentImportType> ditList = dlit.getDocument();
                    for (Map<String, Object> fileMap : fileList) {
                        if (count >= ps) {
                            break;
                        }
                        if (fileMap.get(getStringParam(fileAssignParamMap.get("OBJID"))) != null) {
                            DocumentImportType dit = new DocumentImportType();
                            // тип объекта
                            // понедельник, 21 августа 2017 г. [10:13:25] dron_007_:
                            // Привет. typeObject: CLAIM,CANCELLATION,CHANGE.
                            dit.setTypeObject(getStringParam(fileAssignParamMap.get("TYPEOBJECT")));
                            String kindDeclarationSysname = getStringParam(fileMap, "KINDDEClARATIONSYSNAME");
                            if (kindDeclarationSysname.equalsIgnoreCase("cancellation")) {
                                dit.setTypeObject("CANCELLATION");
                            }

                            //документ
                            dit.setObjId(getLongParam(fileMap.get(getStringParam(fileAssignParamMap.get("OBJID")))));

                            DocumentObjType dot = new DocumentObjType();
                            // дата прикрепления (CREATEDATE из claimnoticedoc)
                            dot.setDate(getFormattedDate(getDateParam(fileMap.get("CREATEDATE"))));
                            // код файла из 1C
                            dot.setDocUrl(getStringParam(fileMap.get("PATHINPARTNER")));
                            // наименование документа
                            String fileName = fileMap.get("FILENAME").toString();
                            String fileShortName = fileName;
                            String fileExt = "pdf";
                            int lastIndexDot = fileName.lastIndexOf(".");
                            if (lastIndexDot >=0) {
                                 fileShortName = fileName.substring(0, lastIndexDot);
                                 fileExt = fileName.substring(lastIndexDot + 1);
                            }
                            // расширение
                            dot.setDocExtension(fileExt);
                            // - наименование типа файла.
                            // Андрей ковальчук 16:54 12.09.2017 skype:
                            //Для тестирования, думаю можно использовать (регистр символов важен):
                            //Паспорт Заявителя
                            // Трудовая книжка
                            // Трудовой договор
                            // полный перечень кинем, когда внутри обсудим.
                            //todo: кодга будет полный перечень возможных типов, и маппинг с нашими типами прикрепляемых документов нужно переделать на проставление типа оис
                            //dot.setName(fileShortName);
                            dot.setName(getStringParam(fileMap.get("DOCTYPEOISNAME")));
                            if (dot.getName() == null || dot.getName().isEmpty()) {
                                dot.setName(getStringParam(fileMap.get("FILETYPENAME")));
                            }

                            // ошибка
                            dot.setMistake("");
                            dit.setDocument(dot);
                            ditList.add(dit);


                            count++;
                        }
                    }
                    ;
                    try {
                        AnswerImportListType ailt = callLifePartnerAttachFiles(dlit);

                        String request = this.marshall(dlit, DocumentsListImportType.class);
                        String response = this.marshall(ailt, AnswerImportListType.class);

                        b2bRequestQueueCreate(request, response, getLongParam(fileAssignParamMap.get("REQUESTTYPE")), 1000, login, password);
                        if (ailt != null) {
                            for (Map<String, Object> fileMap : fileList) {
                                recordMakeTrans(fileMap, getStringParam(fileAssignParamMap.get("TABLENAME"))+"_ATTACHED",
                                        getStringParam(fileAssignParamMap.get("ENTITYDOCID")),
                                        getStringParam(fileAssignParamMap.get("METHODNAMEPREFIX")),
                                                getStringParam(fileAssignParamMap.get("TABLENAME")), login, password);
//                                lossNoticeDocMakeTrans(fileMap, getStringParam(fileAssignParamMap.get("TABLENAME"))+"_ATTACHED", login, password);
                            }
                        }
                    } catch (Exception ex) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ex.printStackTrace(pw);
                        sw.toString(); // stack trace as a string
                        String request = this.marshall(dlit, DocumentsListImportType.class);
                        b2bRequestQueueCreate(request, sw.toString(), getLongParam(fileAssignParamMap.get("REQUESTTYPE")), 404, login, password);
                    }
                }
            } catch (Exception ex) {
                logger.error("Ошибка связывания файлов с сущностью документа. ", ex);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @WsMethod()
    public Map<String, Object> dsLifeIntegrationPutChangeFiles(Map<String, Object> params) throws Exception {
        Map<String, Object> fileAssignParamMap = new HashMap<>();
        fileAssignParamMap.put("ENTITYID", "DECLARATIONID");
        fileAssignParamMap.put("ENTITYDOCID", "DECLARATIONDOCID");
        fileAssignParamMap.put("TABLENAME", "PD_DECLARATIONDOC");
        fileAssignParamMap.put("METHODNAMEPREFIX", "dsPDDeclarationDoc");
        fileAssignParamMap.put("OBJID", "DECLARATIONEXTID");
        fileAssignParamMap.put("TYPEOBJECT", "CHANGE");
        fileAssignParamMap.put("REQUESTTYPE", CHANGEFILEADD);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        return putFiles(params, fileAssignParamMap, login, password);
    }

    @WsMethod()
    public Map<String, Object> dsLifeIntegrationPutClaimFiles(Map<String, Object> params) throws Exception {
        Map<String, Object> fileAssignParamMap = new HashMap<>();
        fileAssignParamMap.put("ENTITYID", "LOSSNOTICEID");
        fileAssignParamMap.put("ENTITYDOCID", "LOSSNOTICEDOCID");
        fileAssignParamMap.put("TABLENAME", "B2B_LOSSNOTICEDOC");
        fileAssignParamMap.put("METHODNAMEPREFIX", "dsB2BLossNoticeDoc");
        fileAssignParamMap.put("OBJID", "LOSSNOTICEEXTID");
        fileAssignParamMap.put("TYPEOBJECT", "CLAIM");
        fileAssignParamMap.put("REQUESTTYPE", CLAIMFILEADD);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        return putFiles(params, fileAssignParamMap, login, password);

//        String login = params.get(WsConstants.LOGIN).toString();
//        String password = params.get(WsConstants.PASSWORD).toString();
//        Map<String, Object> result = new HashMap<String, Object>();
//
//        try {
//            // получаем N первых попавшихся файлов - ранее не выгруженных в оис.
//            // по списку файлов - добавляем их в папку убытка.
//            // в случае успеха - сохраняем ид файла в claimnoticeDoc
//            // меняем состояние на "Выгружен в ОИС"
//            Long packSize = getLongParam(params.get("PACKSIZE"));
//            int ps = packSize.intValue();
//            List<Map<String, Object>> fileList = getUnaddedFileList(params, login, password);
//            CopyUtils.sortByLongFieldName(fileList, "LOSSNOTICEID");
//
//            int count = 0;
//            for (Map<String, Object> fileMap : fileList) {
//                if (count >= ps) {
//                    break;
//                }
//                Map<String, Object> fileRes = addFile(fileMap, params, login, password);
//                if (fileRes != null) {
//                    saveFileExtIdAndChangeState(fileRes, fileMap, params, login, password);
//                }
//                count++;
//            }
//            result.put("STATUS", "DONE");
//        } catch (Exception e) {
//            logger.error("Partner service GetContractsCut call error", e);
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            e.printStackTrace(pw);
//            sw.toString(); // stack trace as a string
//            result.put("responseStr", sw.toString());
//            result.put("STATUS", "outERROR");
//        }
//        return result;
    }

    private Map<String,Object> putFiles(Map<String, Object> params, Map<String, Object> fileAssignParamMap, String login, String password) {
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            // получаем N первых попавшихся файлов - ранее не выгруженных в оис.
            // по списку файлов - добавляем их в папку убытка.
            // в случае успеха - сохраняем ид файла в claimnoticeDoc
            // меняем состояние на "Выгружен в ОИС"
            Long packSize = getLongParam(params.get("PACKSIZE"));
            int ps = packSize.intValue();
            List<Map<String, Object>> fileList = getUnaddedFileListEx(params, getStringParam(fileAssignParamMap.get("TABLENAME")),
                    getStringParam(fileAssignParamMap.get("METHODNAMEPREFIX"))+"CustomBrowseListByParamEx4Integration", login, password);
            //List<Map<String, Object>> fileList = getUnaddedFileList(params, login, password);
            CopyUtils.sortByLongFieldName(fileList, getStringParam(fileAssignParamMap.get("ENTITYID")));

            int count = 0;
            for (Map<String, Object> fileMap : fileList) {
                if (count >= ps) {
                    break;
                }
                Map<String, Object> fileRes = addFile(fileMap, params,fileAssignParamMap, login, password);
                if (fileRes != null) {
                    saveFileExtIdAndChangeState(fileRes, fileMap, params, fileAssignParamMap, login, password);
                }
                count++;
            }
            result.put("STATUS", "DONE");
        } catch (Exception e) {
            logger.error("Partner service GetContractsCut call error", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.toString(); // stack trace as a string
            result.put("responseStr", sw.toString());
            result.put("STATUS", "outERROR");
        }
        return result;
    }



    private void saveFileExtIdAndChangeState(Map<String, Object> fileRes, Map<String, Object> fileMap, Map<String, Object> params,
                                             Map<String, Object> fileAssignParamMap,String login, String password) throws Exception {
        //Еси при отправке клиенту ошибка - выпадет эксепшн и мы сюда не попадем. если попали - файл отправлен
        if (fileRes.get("EXTCODE") != null) {
            Map<String, Object> updParams = new HashMap<>();
            updParams.put(getStringParam(fileAssignParamMap.get("ENTITYDOCID")),
                    fileMap.get(getStringParam(fileAssignParamMap.get("ENTITYDOCID"))));
            updParams.put("PATHINPARTNER", fileRes.get("EXTCODE"));
            Map<String, Object> updRes = this.callService(B2BPOSWS, getStringParam(fileAssignParamMap.get("METHODNAMEPREFIX")) + "Update", updParams, login, password);

            //dsB2BLossNoticeDocUpdate
            // переводим его состояние.

            recordMakeTrans(fileMap, getStringParam(fileAssignParamMap.get("TABLENAME"))+"_SENDED",
                    getStringParam(fileAssignParamMap.get("ENTITYDOCID")),
                    getStringParam(fileAssignParamMap.get("METHODNAMEPREFIX")),
                    getStringParam(fileAssignParamMap.get("TABLENAME")), login, password);

//            lossNoticeDocMakeTrans(fileMap, getStringParam(fileAssignParamMap.get("TABLENAME")) + "_SENDED", login, password);
        }
    }

    private Map<String, Object> addFile(Map<String, Object> fileMap, Map<String, Object> params,
                                        Map<String, Object> fileAssignParamMap,String login, String password) throws Exception {
        Folder folder = new Folder();
        Map<String, Object> result = new HashMap<>();
        if (fileMap.get("DOCFOLDER1C") != null) {
            File file = new File();
            try {
                folder.setCode(fileMap.get("DOCFOLDER1C").toString());
                //Ковальчук Андрей Александрович <aakovalchuk@sberinsur.ru>
                //В поля tns:AddFile/tns:Folder/tns:Name и tns:AddFile/tns:Folder/tns:Description можно передать что угодно, например, то же, что и в tns:AddFile/tns:Folder/tns:Code.
                folder.setDescription(fileMap.get("DOCFOLDER1C").toString());
                folder.setName(fileMap.get("DOCFOLDER1C").toString());
                String fileName = fileMap.get("FILENAME").toString();
                String fileShortName = fileName;
                String fileExt = "pdf";
                int lastIndexDot = fileName.lastIndexOf(".");
                if (lastIndexDot >=0) {
                    fileShortName = fileName.substring(0, lastIndexDot);
                    fileExt = fileName.substring(lastIndexDot + 1);
                }


                //tns:AddFile/tns:File/tns:BinaryData – файл в формате base64
                byte[] fileByteArr = getFileByteArray(fileMap, login, password);
                // судя по всему
                byte[] fileByteArrEncoded = fileByteArr;
                //byte[] fileByteArrEncoded = Base64.encodeBase64(fileByteArr);
                file.setBinaryData(fileByteArrEncoded);
                //В поле tns:AddFile/tns:File/tns:Code передать какой-нибудь уникальный код файла.
                file.setCode(getStringParam(fileMap.get(getStringParam(fileAssignParamMap.get("ENTITYDOCID")))));
                //tns:AddFile/tns:File/tns:Extension - расширение файла
                file.setExtension(fileExt);
                //tns:AddFile/tns:File/tns:Name – имя файла (без расширения)
                file.setName(fileShortName);
                ShortFileInfo sfi = callLifePartnerPutFile(folder, file);
                String request = this.marshall(folder, Folder.class);
                String response = this.marshall(sfi, ShortFileInfo.class);
                request += "---";
                request += this.marshall(file, File.class);
                b2bRequestQueueCreate(request, response, getLongParam(fileAssignParamMap.get("REQUESTTYPE")), 1000, login, password);

                String extCode = sfi.getCode();
                if (sfi.getSize() > 0) {
                    result.put("STATUS", "OK");
                    result.put("EXTCODE", extCode);
                    return result;
                }
            } catch (Exception ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                sw.toString(); // stack trace as a string
                String request = this.marshall(folder, Folder.class);
                request += "---";
                request += this.marshall(file, File.class);
                b2bRequestQueueCreate(request, sw.toString(), getLongParam(fileAssignParamMap.get("REQUESTTYPE")), 404, login, password);
                recordMakeTrans(fileMap, getStringParam(fileAssignParamMap.get("TABLENAME"))+"_FAIL",
                        getStringParam(fileAssignParamMap.get("ENTITYDOCID")),
                        getStringParam(fileAssignParamMap.get("METHODNAMEPREFIX")),
                        getStringParam(fileAssignParamMap.get("TABLENAME")), login, password);

            }

        }
        return result;
    }

    private byte[] getFileByteArray(Map<String, Object> fileMap, String login, String password) throws Exception {
        String documentName = fileMap.get("FILENAME").toString();
        if ((documentName != null) && (!documentName.isEmpty())) {
            InputStream document = null;
                String fullPath = null;
                long fileLength = 0;
                if (getUseSeaweedFS().equalsIgnoreCase("TRUE") && (fileMap.get("FSID") != null)) {
                    String masterUrlString = getSeaweedFSUrl();
                    URL masterURL = new URL(masterUrlString);
                    WeedFSFile file = new WeedFSFile(documentName);
                    WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                    List<Location> locations = client.lookup(file.getVolumeId());
                    if (locations.size() == 0) {
                        System.out.println("file not found");
                        return null;
                    }
                    String uploadPath = getUploadFilePath();
                    String tempFileName = uploadPath + UUID.randomUUID() + "_" + documentName;
                    fullPath = tempFileName;
                    document = client.read(file, locations.get(0));
                    BufferedOutputStream bufferedOutput = null;
                    FileOutputStream fileOutputStream = null;
                    byte[] bytes = null;
                    try {
                            bytes = new byte[document.available()];
                            document.read(bytes);
                    } finally {
                        document.close();
                        return bytes;
                    }


                } else {
                    java.io.File f = null;

                    String uploadPath = Config.getConfig().getParam("uploadPath", "");
                    String realFileName = fileMap.get("FILEPATH").toString();
                    fullPath = uploadPath + realFileName;
                    f = new java.io.File(fullPath);

                    document = new FileInputStream(f);
                    byte[] bytes = null;
                    try {
                        bytes = new byte[document.available()];
                        document.read(bytes);
                    } finally {
                        document.close();
                        return bytes;
                    }
                }
        }
        return null;
    }

    private List<Map<String, Object>> getUnattachedFileList(Map<String, Object> params, String login, String password) throws Exception {
        return getUnattachedFileListEx(params, "B2B_LOSSNOTICEDOC",
                "dsB2BLossNoticeDocCustomBrowseListByParamEx4Integration", login, password);
//        Map<String, Object> searchParam = new HashMap<>();
//        searchParam.put("STATESYSNAME", "B2B_LOSSNOTICEDOC_SENDED");
//        Map<String, Object> searchres = this.callService(B2BPOSWS, "dsB2BLossNoticeDocCustomBrowseListByParamEx4Integration", searchParam, login, password);
//        if (searchres != null) {
//            if (searchres.get(RESULT) != null) {
//                return (List<Map<String, Object>>) searchres.get(RESULT);
//            }
//        }
//        return null;
    }

    private List<Map<String, Object>> getUnattachedFileListEx(Map<String, Object> params, String tableName, String methodName, String login, String password) throws Exception {
        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put("STATESYSNAME", tableName + "_SENDED");
        Map<String, Object> searchres = this.callService(B2BPOSWS, methodName, searchParam, login, password);
        if (searchres != null) {
            if (searchres.get(RESULT) != null) {
                return (List<Map<String, Object>>) searchres.get(RESULT);
            }
        }
        return null;
    }

    private List<Map<String, Object>> getUnattachedChangeFileList(Map<String, Object> params, String login, String password) throws Exception {
        return getUnattachedFileListEx(params, "PD_DECLARATIONDOC",
                "dsPDDeclarationDocCustomBrowseListByParamEx4Integration", login, password);
    }


    private List<Map<String, Object>> getUnaddedFileListEx(Map<String, Object> params,String tableName, String methodName, String login, String password) throws Exception {
        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put("STATESYSNAME", tableName +"_NEW");
        Map<String, Object> searchres = this.callService(B2BPOSWS, methodName, searchParam, login, password);
        if (searchres != null) {
            if (searchres.get(RESULT) != null) {
                return (List<Map<String, Object>>) searchres.get(RESULT);
            }
        }
        return null;
    }

    private List<Map<String, Object>> getUnaddedFileList(Map<String, Object> params, String login, String password) throws Exception {
        return getUnaddedFileListEx(params, "B2B_LOSSNOTICEDOC",
                "dsB2BLossNoticeDocCustomBrowseListByParamEx4Integration", login, password);
//        Map<String, Object> searchParam = new HashMap<>();
//        searchParam.put("STATESYSNAME", "B2B_LOSSNOTICEDOC_NEW");
//        Map<String, Object> searchres = this.callService(B2BPOSWS, "dsB2BLossNoticeDocCustomBrowseListByParamEx4Integration", searchParam, login, password);
//        if (searchres != null) {
//            if (searchres.get(RESULT) != null) {
//                return (List<Map<String, Object>>) searchres.get(RESULT);
//            }
//        }
//        return null;
    }

    private List<Map<String, Object>> getUnaddedChangeFileList(Map<String, Object> params, String login, String password) throws Exception {
        return getUnaddedFileListEx(params, "PD_DECLARATIONDOC",
                "dsPDDeclarationDocCustomBrowseListByParamEx4Integration", login, password);
//        Map<String, Object> searchParam = new HashMap<>();
//        searchParam.put("STATESYSNAME", "PD_DECLARATIONDOC_NEW");
//        Map<String, Object> searchres = this.callService(B2BPOSWS, "dsPDDeclarationDocCustomBrowseListByParamEx4Integration", searchParam, login, password);
//        if (searchres != null) {
//            if (searchres.get(RESULT) != null) {
//                return (List<Map<String, Object>>) searchres.get(RESULT);
//            }
//        }
//        return null;
    }
}
