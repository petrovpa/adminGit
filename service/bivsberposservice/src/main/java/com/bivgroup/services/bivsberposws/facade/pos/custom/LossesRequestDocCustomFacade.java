/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * Кастомный фасад для сущности RefundRequestDoc
 *
 * @author ilich
 */
@Auth(onlyCreatorAccess = false)
@BinaryFile(objTableName = "RFND_REQUESTDOC", objTablePKFieldName = "REQUESTDOCID")
@IdGen(entityName = "RFND_REQUESTDOC", idFieldName = "REQUESTDOCID")
@BOName("RefundRequestDocCustom")
public class LossesRequestDocCustomFacade extends BaseFacade {

    private static final String BIVSBERPOSWS_SERVICE_NAME = "bivsberposws";

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - Вид документа</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * <LI>ISDOCCHECKING - Флаг Документ проверен</LI>
     * <LI>ISORIGINALRECEIVED - Флаг Оригинал получен</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINDOCID - Вид документа</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * <LI>ISDOCCHECKING - Флаг Документ проверен</LI>
     * <LI>ISORIGINALRECEIVED - Флаг Оригинал получен</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsLossesRequestDocBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsLossesRequestDocBrowseListByParamEx", "dsLossesRequestDocBrowseListByParamExCount", params);
        return result;
    }

    @WsMethod(requiredParams = {"REQUESTID"})
    public Map<String, Object> dsLossesRequestDocBrowseListByParamForTable(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // обязательные документы
        Map<String, Object> reqParams = new HashMap<String, Object>();
        reqParams.put("PRODID", params.get("PRODID"));
        reqParams.put("ORDERBY", "T2.NAME");
        Map<String, Object> reqRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsLossesProductBinaryDocumentBrowseListByParamEx", reqParams, login, password);
        List<Map<String, Object>> reqAttachDocList = (List<Map<String, Object>>) reqRes.get(RESULT);
        // фильтруем список руками
        List<Map<String, Object>> resultReqAttachDocList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> bean : reqAttachDocList) {
            boolean fAdd = true;
            if ((bean.get("REFUNDTYPEID") != null) && (bean.get("REFUNDTYPEID").toString().compareTo(params.get("REFUNDTYPEID").toString()) != 0)) {
                fAdd = false;
            }
            if ((bean.get("BINDOCTYPE") != null) && (bean.get("BINDOCTYPE").toString().compareTo(params.get("REFUNDREASONID").toString()) != 0)) {
                fAdd = false;
            }
            if (fAdd) {
                resultReqAttachDocList.add(bean);
            }
        }
        reqAttachDocList = resultReqAttachDocList;
        // документы по заявке
        Map<String, Object> docParams = new HashMap<String, Object>();
        docParams.put("REQUESTID", params.get("REQUESTID"));
        docParams.put("ORDERBY", "T2.NAME");
        Map<String, Object> docRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsLossesRequestDocBrowseListByParamEx", docParams, login, password);
        List<Map<String, Object>> attachDocList = (List<Map<String, Object>>) docRes.get(RESULT);
        //
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        // пробегаем по обязательным документам, добавляем их вначало списка
        for (int i = 0; i < reqAttachDocList.size(); i++) {
            Map<String, Object> rBean = reqAttachDocList.get(i);
            boolean fFounded = false;
            Map<String, Object> fFoundedBean = null;
            for (int j = 0; j < attachDocList.size(); j++) {
                Map<String, Object> aBean = attachDocList.get(j);
                if (rBean.get("BINDOCID").toString().compareTo(aBean.get("BINDOCID").toString()) == 0) {
                    fFounded = true;
                    fFoundedBean = aBean;
                    aBean.put("ISREQFOUNDED", 1);
                    break;
                }
            }
            if (fFounded == false) {
                rBean.put("ISREQUIRED", 1L);
                resultList.add(rBean);
            } else {
                if (fFoundedBean != null) {
                    fFoundedBean.put("ISREQUIRED", 1L);
                    resultList.add(fFoundedBean);
                }
            }
        }
        // если состояние заявки - печать допса, и в печатные документы добавлен допс, то необходимо добавить допс в прикрепленные доки.
       /* if ((params.get("STATESYSNAME") != null) && (params.get("STATESYSNAME").toString().equalsIgnoreCase("RFND_REQ_PRINTING_DS"))) {
            if ((params.get("ISDOPSEXIST") != null) && (params.get("ISDOPSEXIST").toString().equalsIgnoreCase("TRUE"))) {
                boolean isDopsExist = false;
                for (int i = 0; i < attachDocList.size(); i++) {
                    Map<String, Object> aBean = attachDocList.get(i);
                    if ((aBean.get("NAME") != null) && (aBean.get("NAME").toString().equalsIgnoreCase("Дополнительное соглашение"))) {
                        isDopsExist = true;
                    }
                }
                if (!isDopsExist) {
                    Map<String, Object> dopsMap = new HashMap<String, Object>();
                    dopsMap.put("REQUESTID", params.get("REQUESTID"));
                    dopsMap.put("BINDOCID", 1011);
                    dopsMap.put("NAME", "Дополнительное соглашение");
                    dopsMap.put("ISREQUIRED", 1L);
                    
                    
                    resultList.add(dopsMap);
                }
            }
        }*/
        // пробегаем по оставшимся документам
        for (int i = 0; i < attachDocList.size(); i++) {
            Map<String, Object> aBean = attachDocList.get(i);
            if (aBean.get("ISREQFOUNDED") == null) {
                aBean.put("ISREQUIRED", 0L);
                resultList.add(aBean);
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, resultList);
        return result;
    }
}
