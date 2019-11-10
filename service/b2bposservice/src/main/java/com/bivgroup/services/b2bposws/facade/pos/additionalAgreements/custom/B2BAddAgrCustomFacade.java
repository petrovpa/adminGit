/*
 * Copyright (c) Diasoft 2004-2013
 */
package com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.*;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.utils.XMLUtil;

/**
 * Фасад для сущности B2BAddAgr
 *
 * @author reson
 */
@BOName("B2BAddAgrCustom")
public class B2BAddAgrCustomFacade extends B2BBaseFacade {

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    private static final int UNMODIFIED_ID = RowStatus.UNMODIFIED.getId();
    private static final int INSERTED_ID = RowStatus.INSERTED.getId();
    private static final int MODIFIED_ID = RowStatus.MODIFIED.getId();
    private static final int DELETED_ID = RowStatus.DELETED.getId();
    private static final String ROWSTATUS_PARAM_NAME = RowStatus.ROWSTATUS_PARAM_NAME;

    protected String generateNum(String login, String password) throws Exception {
        String result = "";
        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("SYSTEMBRIEF", "addAgrAutoNum");
        Map<String, Object> res1 = this.callService(COREWS, "dsNumberFindByMask", param1, login, password);
        if (res1.get("Result") != null) {
            result = res1.get("Result").toString();
        }

        return result;
    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * <LI>AGRNUMBER - Номер</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>INITIATOR - Ид инициатора</LI>
     * <LI>PRODCONFIGID - Ид продукта</LI>
     * <LI>SIGNDATE - Дата принятия</LI>
     * <LI>STARTDATE - Дата создания заявки</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TYPEID - Тип допса</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * <LI>AGRNUMBER - Номер</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>INITIATOR - Ид инициатора</LI>
     * <LI>PRODCONFIGID - Ид продукта</LI>
     * <LI>SIGNDATE - Дата принятия</LI>
     * <LI>STARTDATE - Дата создания заявки</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TYPEID - Тип допса</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BAddAgrBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BAddAgrBrowseListByParamEx", "dsB2BAddAgrBrowseListByParamExCount", params);

        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        parseDates(result, String.class);

        return result;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BAddAgrSave(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        
        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(params, Date.class);
        
        // 1. получаем CONTRID из параметров todo: позже от ид надо избавится. чтобы на формах вообще ид небыло. а был хэш
        Long contrId = getLongParam(params.get("CONTRID"));
        Map<String, Object> contrParam = new HashMap<String, Object>();
        contrParam.put("CONTRID", contrId);
        contrParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> contrRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamEx", contrParam, login, password);
        if (contrRes != null) {
            // 2. получаем мапу продукта данного договора.
            /*
             Map<String, Object> prodParam = new HashMap<String, Object>();
             prodParam.put("PRODCONFID", prodConfId);
             Map<String, Object> prodRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByID", prodParam, login, password);
             if (prodRes != null) {
                
             }*/
            Long prodConfId = getLongParam(contrRes.get("PRODCONFID"));
            Long prodVerId = getLongParam(contrRes.get("PRODVERID"));
            params.put("PRODVERID", prodVerId);
            params.put("PRODCONFIGID", prodConfId);
            // 3. создаем заголовок заявки на допс
            Long addAgrId = saveAddAgr(params);
            // 4. получаем список допустимых причин заявки на допс. для данного продукта
            List<Map<String, Object>> addAgrCauseList = getAddAgrCauseListByParam(params, login, password);
            // 4. получаем текущий список содержимого заявки (если вдруг происходит редактирование и заявка уже была.)
            List<Map<String, Object>> addAgrCntList = loadAddAgrCnt(addAgrId, login, password);
            // бежим по списку существующих записей, если не находим во входных параметрах причины с таким же сиснейм
            if (addAgrCauseList != null) {
                if (addAgrCntList != null) {
                    for (Map<String, Object> addAgrCnt : addAgrCntList) {
                        Long curHbDataVer = getLongParam(addAgrCnt.get("HBDATAVERID"));
                        Long curHbStore = getLongParam(addAgrCnt.get("HBSTOREID"));
                        Long addAgrCntId = getLongParam(addAgrCnt.get("ADDAGRCNTID"));
                        String curSysName = getStringParam(addAgrCnt.get("SYSNAME"));

                        if (!curSysName.isEmpty()) {
                            if (params.get(curSysName) != null) {
                                Map<String, Object> paramAddAgrCnt = (Map<String, Object>) params.get(curSysName);
                                if (paramAddAgrCnt.get("HBSTOREID") == null) {
                                    // данная причина создана заново - надо грохнуть из базы ниже она создастся новая.
                                    deleteAddAgrCnt(addAgrCntId, curHbStore, login, password);
                                } else {
                                    if (curHbStore.compareTo(getLongParam(paramAddAgrCnt.get("HBSTOREID"))) == 0) {
                                        // ид совпадают. все норм. ниже причина будет обновлена.
                                    } else {
                                        // глюка какаято. грохаем из базы, и удаляем ид из мапы параметров.
                                        // ниже причина создастя заново.
                                        paramAddAgrCnt.remove("HBSTOREID");
                                        deleteAddAgrCnt(addAgrCntId, curHbStore, login, password);
                                    }
                                }
                            } else {
                                // удалить текущий из базы. в параметрах его нет. т.е. грохнули
                                deleteAddAgrCnt(addAgrCntId, curHbStore, login, password);
                            }
                        }

                    }
                }
                // удаляем из базы запись.
                // если находим - сверяем ид записей. если совпало - все ок. обновится ниже.
                // если не совпало - какойто глюк надо грохнуть запись, грохнуть ид записи в параметрах.
                // ниже она тогда создастся заново.
                // 5. системные имена причин совпадают с именами мап в params хранящих данные причин.
                // 6. проходим по возможным причинам, и при обнаружении в params одноименных мап выполняем сохранение содержимого заявки.
                for (Map<String, Object> addAgrCause : addAgrCauseList) {
                    String causeSysName = getStringParam(addAgrCause.get("SYSNAME"));
                    if ((causeSysName != null) && (!causeSysName.isEmpty())) {
                        if (params.get(causeSysName) != null) {
                            //во входных параметрах есть данные по текущей причине.
                            Long hbDataVerId = getLongParam(addAgrCause.get("HBDATAVERID"));
                            Long addAgrCauseId = getLongParam(addAgrCause.get("ADDAGRCAUSEID"));
                            //      6.1 создаем запись содержимого заявки данные сохраняем в справочник. hbdataver берем из причины по продукту. contrid из патаметров.
                            Long addAgrCntId = saveAddAgrCnt(params, causeSysName, hbDataVerId, addAgrCauseId, addAgrId);
                            // 7. все полученные ид сохраняем в выходную мапу.
                        }
                    }
                }
            }
        }
        
        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        parseDates(params, String.class);
        
        return params;
    }

    @WsMethod(requiredParams = {"ADDAGRID"})
    public Map<String, Object> dsB2BAddAgrLoad(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(params, Date.class);
        
        params.put("ReturnAsHashMap", "TRUE");
        // выбрать заголовок заявки
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAddAgrBrowseListByParam", params, login, password);
        if (result != null) {
            // по продверу ид получить список возможных причин
            Long prodConfId = getLongParam(result.get("PRODCONFIGID"));
            Map<String, Object> prodConfParam = new HashMap<String, Object>();
            prodConfParam.put("PRODCONFID", prodConfId);
            prodConfParam.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> prodConfRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", prodConfParam, login, password);
            if (prodConfRes != null) {
                Long prodVerId = getLongParam(prodConfRes.get("PRODVERID"));
                params.put("PRODVERID", prodVerId);
                Long addAgrId = getLongParam(params.get("ADDAGRID"));
                List<Map<String, Object>> addAgrCauseList = getAddAgrCauseListByParam(params, login, password);
                // сформировать список hbdataverid и запросить содержимое заявки.
                List<Map<String, Object>> addAgrCntList = loadAddAgrCnt(addAgrId, login, password);
                for (Map<String, Object> addAgrCnt : addAgrCntList) {
                    String curSysName = getStringParam(addAgrCnt.get("SYSNAME"));
                    // пройти по содержимому заявки и разложить его в мапу по системным именам, для вывода на интерфейс.
                    if (!curSysName.isEmpty()) {
                        result.put(curSysName, addAgrCnt);
                    }
                }
            }

        }
        
        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        parseDates(result, String.class);

        return result;
    }

    private Long saveAddAgr(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Long contrId = getLongParam(params.get("CONTRID"));
        Map<String, Object> addAgrParam = new HashMap<String, Object>();
        // сгенерить номер заявки
        addAgrParam.put("AGRNUMBER", generateNum(login, password));
        addAgrParam.put("CONTRID", contrId);
        addAgrParam.put("ADDAGRID", params.get("ADDAGRID"));
        addAgrParam.put("PRODCONFIGID", params.get("PRODCONFIGID"));
        addAgrParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> result = null;
        if (addAgrParam.get("ADDAGRID") == null) {
            result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAddAgrCreate", addAgrParam, login, password);
        } else {
            result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAddAgrUpdate", addAgrParam, login, password);
        }
        if (result != null) {
            Long addAgrId = getLongParam(result.get("ADDAGRID"));
            params.put("ADDAGRID", addAgrId);
            return addAgrId;
        }
        return null;
    }

    private List<Map<String, Object>> getAddAgrCauseListByParam(Map<String, Object> params, String login, String password) throws Exception {
        //String login = params.get(WsConstants.LOGIN).toString();
        //String password = params.get(WsConstants.PASSWORD).toString();
        Long prodVerId = getLongParam(params.get("PRODVERID"));
        Map<String, Object> addAgrParam = new HashMap<String, Object>();
        addAgrParam.put("PRODVERID", prodVerId);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BAddAgrCauseBrowseListByParam", addAgrParam, login, password);
        if (result != null) {
            if (result.get(RESULT) != null) {
                List<Map<String, Object>> resList = (List<Map<String, Object>>) result.get(RESULT);
                return resList;
            }
        }
        return null;
    }

    private Long saveAddAgrCnt(Map<String, Object> params, String causeSysName, Long hbDataVerId, Long addAgrCauseId, Long addAgrId) throws Exception {
        Map<String, Object> causeCnt = (Map<String, Object>) params.get(causeSysName);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        causeCnt.put("ADDAGRID", addAgrId);
        causeCnt.put("ADDAGRCAUSEID", addAgrCauseId);
        if (causeCnt.get("ADDAGRCNTID") == null) {
            createAddAgrCnt(causeCnt, login, password);
        } else {
            updateAddAgrCnt(causeCnt, login, password);
        }
        causeCnt.put("ADDAGRID", addAgrId);
        causeCnt.put("ADDAGRCNTID", getLongParam(causeCnt.get("ADDAGRCNTID")));
        causeCnt.put("HBDATAVERID", hbDataVerId);
        if (causeCnt.get("HBSTOREID") == null) {
            createAddAgrCntValues(causeCnt, login, password);
        } else {
            updateAddAgrCntValues(causeCnt, login, password);
        }
        return getLongParam(causeCnt.get("HBSTOREID"));
    }

    private void createAddAgrCntValues(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(contrExtMap);
        Object contrExtID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", params, login, password, "HBSTOREID");
        if (contrExtID != null) {
            contrExtMap.put("HBSTOREID", contrExtID);
        }
    }

    private void updateAddAgrCntValues(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        if (contrExtMap != null) {
            params.putAll(contrExtMap);
            Object contrExtID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", params, login, password, "HBSTOREID");
        }
    }

    private void createAddAgrCnt(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(contrExtMap);
        Object contrExtID = this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2bAddAgrCntCreate", params, login, password, "ADDAGRCNTID");
        if (contrExtID != null) {
            contrExtMap.put("ADDAGRCNTID", contrExtID);
        }
    }

    private void updateAddAgrCnt(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        if (contrExtMap != null) {
            params.putAll(contrExtMap);
            Object contrExtID = this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2bAddAgrCntUpdate", params, login, password, "ADDAGRCNTID");
        }
    }

    private List<Map<String, Object>> loadAddAgrCnt(Long addAgrId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ADDAGRID", addAgrId);
        Map<String, Object> cntRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2bAddAgrCntBrowseListByParamEx", params, login, password);
        if (cntRes != null) {
            if (cntRes.get(RESULT) != null) {
                List<Map<String, Object>> cntList = (List<Map<String, Object>>) cntRes.get(RESULT);
                for (Map<String, Object> cntMap : cntList) {
                    Long hbDataVerId = getLongParam(cntMap.get("HBDATAVERID"));
                    Map<String, Object> extParam = new HashMap<String, Object>();
                    extParam.put("HBDATAVERID", hbDataVerId);
                    extParam.put("ADDAGRID", addAgrId);
                    extParam.put("ReturnAsHashMap", "TRUE");
                    Map<String, Object> result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", extParam, login, password);
                    if (result != null) {
                        cntMap.putAll(result);
                    }
                }
                return cntList;
            }
        }
        return null;
    }

    private List<Map<String, Object>> loadAddAgrCnt(Long addAgrId, List<Map<String, Object>> addAgrCauseList, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        List<Long> hbDataVerIdList = new ArrayList();
        for (Map<String, Object> addAgrCause : addAgrCauseList) {
            String causeSysName = getStringParam(addAgrCause.get("SYSNAME"));
            if ((causeSysName != null) && (!causeSysName.isEmpty())) {
                Long hbDataVerId = getLongParam(addAgrCause.get("HBDATAVERID"));
                hbDataVerIdList.add(hbDataVerId);
            }
        }
        if (!hbDataVerIdList.isEmpty() && (addAgrId != null)) {
            params.put("HBDATAVERIDLIST", hbDataVerIdList);
            //params.put("HBDATAVERID", hbDataVerId);
            params.put("ADDAGRID", addAgrId);
            //      6.1 создаем запись содержимого заявки данные сохраняем в справочник. hbdataver берем из причины по продукту. contrid из патаметров.
            Map<String, Object> result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParamByHBDVIdList", params, login, password);
            if (result != null) {
                if (result.get(RESULT) != null) {
                    return (List<Map<String, Object>>) result.get(RESULT);
                }
            }
        }
        return null;
    }

    private void deleteAddAgrCnt(Long addAgrCntId, Long curHbStore, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ADDAGRCNTID", addAgrCntId);
        this.callService(B2BPOSWS_SERVICE_NAME, "dsB2bAddAgrCntDelete", params, login, password);
        Map<String, Object> params1 = new HashMap<String, Object>();
        params1.put("HBSTOREID", curHbStore);
        this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordDelete", params1, login, password);
    }

}
