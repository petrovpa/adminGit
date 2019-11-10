/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.cargo;

import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("CargoCustom")
public class CargoCustomFacade extends ProductContractCustomFacade {

    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CargoCustomFacade.class);
    private static final int calcVerId = 2010;
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    /**
     * сервис загрузки справочников по продукту Защита грузов.
     *
     * @param params
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsCargoHBListLoad(Map<String, Object> params) throws Exception {
        logger.debug("before dsCargoHBListLoad");
        //Map<String, Object> hbMapIn = (Map<String, Object>) params.get("HBMAP");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        //loadOPFList(result, hbMapIn, login, password);
        loadHandbookList(result, null, "B2B.Cargo.CargoGroup", calcVerId, "CargoGroupList", login, password);
        loadHandbookList(result, null, "B2B.Cargo.PackCategory", calcVerId, "CargoPackCategoryList", login, password);
        loadHandbookList(result, null, "B2B.Cargo.TrailerType", calcVerId, "CargoTrailerTypeList", login, password);
        loadHandbookList(result, null, "B2B.Cargo.ProtectionKind", calcVerId, "CargoProtectionKindList", login, password);
        logger.debug("after dsCargoHBListLoad");
        return result;
    }

    /**
     * сервис подготовки данных для отчета по продукту Защита грузов.
     *
     * @param params
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID"})
    public Map<String, Object> dsB2BCargoPrintDocDataProvider(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // загрузка данных договора
        Map<String, Object> result = dsB2BBasePrintDocDataProvider(params);

        return result;
    }

    /**
     * сервис маппинга данных для универсального сохранения и обратно. и
     * сохранения по продукту Защита грузов.
     *
     * @param params
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BCargoContrSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BContrSave");
        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> userInfo = findDepByLogin(login, password);
        Long sellerId = getSellerId(userInfo, login, password);
        contrMap.put("SELLERID", sellerId);
        contrMap.put("ORGSTRUCTID", userInfo.get("DEPARTMENTID"));
        contrMap.put("SELFORGSTRUCTID", userInfo.get("DEPARTMENTID"));
        //SELLERID - сделать продавца для сайта? или искать его по логину
        contrMap.put("PRODVERID", 2010);
        if (contrMap.get("CONTRNUMBER") == null) {
            String contrNum = generateContrNum(2010L, login, password);
            contrMap.put("CONTRPOLSER", "005SC");
            contrMap.put("CONTRPOLNUM", "500" + contrNum);
            contrMap.put("CONTRNUMBER", "005/SC/500" + contrNum);
            //инициализить надо дату начала и окончания действия.
            Date now = new Date();
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(now);
            gc.set(Calendar.HOUR_OF_DAY, 0);
            gc.set(Calendar.MINUTE, 0);
            gc.set(Calendar.SECOND, 0);
            gc.set(Calendar.MILLISECOND, 0);
            contrMap.put("DOCUMENTDATE", gc.getTime());

            gc.add(Calendar.DATE, 1);
            contrMap.put("STARTDATE", gc.getTime());

            gc.add(Calendar.MONTH, 3);
            gc.add(Calendar.DATE, -1);
            gc.set(Calendar.HOUR_OF_DAY, 23);
            gc.set(Calendar.MINUTE, 59);
            gc.set(Calendar.SECOND, 59);
            gc.set(Calendar.MILLISECOND, 0);
            contrMap.put("FINISHDATE", gc.getTime());
        }

        List<Map<String, Object>> contrList = prepareContrMapToSave(contrMap);
        Map<String, Object> contrParam = new HashMap<String, Object>();
        contrParam.put("CONTRLIST", contrList);
        boolean isNewContr = contrMap.get("CONTRID") == null;
        //Map<String, Object> rawResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractUniversalSave", contrParam, login, password);
        Object rawResult = this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BContractUniversalSave", contrParam, login, password, "CONTRLIST");
        if (rawResult != null) {
            Map<String, Object> rawContrMap = (Map<String, Object>) ((List) rawResult).get(0);
            if (isNewContr) {
                // установка прав на договор
                Long contractId = Long.valueOf(rawContrMap.get("CONTRID").toString());
                createContractRights(contractId, params, login, password);
            }
            Map<String, Object> result = prepareContrMapToLoad(rawContrMap);
            logger.debug("after succesfull dsB2BContrSave");
            return result;
        }

        logger.debug("after unsuccesfull dsB2BContrSave");
        return null;
    }

    private List<Map<String, Object>> prepareContrMapToSave(Map<String, Object> contrMap) {

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(contrMap, Date.class);

        List<Map<String, Object>> contrList = new ArrayList<Map<String, Object>>();
        contrList.add(contrMap);
        return contrList;
    }

    private Map<String, Object> prepareContrMapToLoad(Map<String, Object> contrMap) {
        // форматируем даты в строки вида dd.MM.yyyy
        //formatDateFromMap(contrMap);

        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        parseDates(contrMap, String.class);

        // todo: возможно требуется ряд дополнительных преобразований перед возвратом результата в Angular-интерфейс
        return contrMap;
    }

}
