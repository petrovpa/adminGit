/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("HandbookCustom")
public class HandbookCustomFacade extends B2BBaseFacade {

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    private static final String REFWS_SERVICE_NAME = Constants.REFWS;
    private static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;

    // загрузка справочника ОПФ
    private List<Map<String, Object>> loadOPFList(String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ReferenceName", "Справочник ОПФ");
        param.put("ReferenceGroupName", "Справочники клиентской базы");
        Map<String, Object> qRes = this.callService(REFWS_SERVICE_NAME, "refItemGetListByParams", param, login, password);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (qRes.get(RESULT) != null) {
            if (qRes.get(RESULT) instanceof List) {
                List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                logger.debug(resList);
                result = resList;
            }
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BHandbooksBrowseEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // безусловная загрузка справочников (не зависящих от продутка)
        result.put("OPFList", loadOPFList(login, password)); // справочник ОПФ

        // загрузка структуры продукта
        if (params.get("prodConfId") != null) {
            Long prodConfId = Long.valueOf(params.get("prodConfId").toString());
            Map<String, Object> prodParam = new HashMap<String, Object>();
            prodParam.put("PRODCONFID", prodConfId);
            prodParam.put("HIERARCHY", true);
            prodParam.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> prodMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", prodParam, login, password);
            result.put("PRODMAP", prodMap);
        } else {
            result.put("PRODMAP", "Не указан идентификатор конфигурации продукта (prodConfId), сведения о продукте получены не были.");
        }

        // загрузка продукто-зависимых справочников
        if (params.get("prodVerId") != null) {
            Long prodVerId = Long.valueOf(params.get("prodVerId").toString());
            // todo: использовать системные имена вместо идентификаторов
            if (prodVerId.longValue() == 2010L) {
                //загрузка справочников для защиты грузов
                logger.debug("загрузка справочников Защиты грузов");
                params.put(RETURN_AS_HASH_MAP, true);
                result.putAll(this.callService(B2BPOSWS_SERVICE_NAME, "dsCargoHBListLoad", params, login, password));
            }
        } else {
            result.put("Status", "emptyProdVerId");
        }

        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        parseDates(result, String.class);

        return result;
    }

    // получение данных о справочнике и его версии по именам справочника (NAME) и версии (HBDATAVERNAME)
    @WsMethod(requiredParams = {"NAME", "HBDATAVERNAME"})
    public Map<String,Object> dsB2BHandbookDescrAndDataVerBrowseListByNames(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BHandbookDescrAndDataVerBrowseListByNames", params);
        return result;
    }


    // получение сведений справочника по имени справочника (HANDBOOKNAME)
    // необязательные параметры:
    // HANDBOOKDATAVERSIONNAME - имя версии справочника (если не указано, будет использоваться 'Версия 1')
    // HANDBOOKDATAPARAMS - мапа с параметрами для ограничения запрашиваемых из справочника записей (имена параметров должны соответствовать именам свойств справочника, которые описаны в HandbookDescriptor)
    // ReturnListOnly - если true, то возвращает только список с записями справочника; иначе - возвращает мапу (ключ - имя справочника, значение - список с записями справочника)
    @WsMethod(requiredParams = {"HANDBOOKNAME"})
    public Map<String, Object> dsB2BHandbookDataBrowseByHBName(Map<String, Object> params) throws Exception {

        logger.debug("Getting handbook data by handbook name (and handbook version name, if specified)...");

        Map<String, Object> result;
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        String hbName = getStringParamLogged(params, "HANDBOOKNAME");
        String hbDataVerName = getStringParamLogged(params, "HANDBOOKDATAVERSIONNAME");
        if (hbDataVerName.isEmpty()) {
            hbDataVerName = "Версия 1";
            logger.debug(String.format("HANDBOOKDATAVERSIONNAME parameter is empty, default value ('%s') will be used.", hbDataVerName));
        }

        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("NAME", hbName);
        hbParams.put("HBDATAVERNAME", hbDataVerName);
        hbParams.put(RETURN_AS_HASH_MAP, true);

        Map<String, Object> hbInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BHandbookDescrAndDataVerBrowseListByNames", hbParams, false, login, password);
        hbName = getStringParamLogged(hbInfo, "NAME");
        Long hbDataVerID = getLongParamLogged(hbInfo, "HBDATAVERID");

        Map<String, Object> hbDataParams = (Map<String, Object>) params.get("HANDBOOKDATAPARAMS");
        if (hbDataParams == null) {
            hbDataParams = new HashMap<String, Object>();
        }
        hbDataParams.put("HBDATAVERID", hbDataVerID);

        Map<String, Object> hbDataRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordBrowseListByParam", hbDataParams, false, login, password);
        markAllMapsByKeyValue(hbDataRes, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);

        boolean isReturnListOnly = getBooleanParam(params.get(RETURN_LIST_ONLY), Boolean.FALSE);

        if (isReturnListOnly) {
            logger.debug("Requested returning only list of handbook data records.");
            result = hbDataRes;
        } else {
            logger.debug("Returning map with handbook name as key and list of handbook data records as value.");
            List<Map<String, Object>> hbDataList = WsUtils.getListFromResultMap(hbDataRes);
            result = new HashMap<String, Object>();
            result.put(hbName, hbDataList);
        }

        logger.debug("Getting handbook data finished.");

        return result;
    }

}
