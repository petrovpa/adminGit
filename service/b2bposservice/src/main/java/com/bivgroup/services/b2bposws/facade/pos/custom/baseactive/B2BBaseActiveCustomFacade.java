package com.bivgroup.services.b2bposws.facade.pos.custom.baseactive;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BBaseActiveCustom")
public class B2BBaseActiveCustomFacade extends B2BBaseFacade {

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    private static final String THIS_SERVICE_NAME = B2BPOSWS_SERVICE_NAME;
    private static final String BASE_ACTIVE_TICKER_RELATION_HB_NAME = "B2B.SBSJ.RelationBaseActiveTickers";

    @WsMethod(requiredParams = {"BASEACTIVE"})
    public Map<String, Object> dsB2BBaseActiveSave(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> result = null;

        Map<String, Object> baseActive = getMapParam(params, "BASEACTIVE");
        List<Map<String, Object>> tickerList = removeListParam(baseActive, "TICKERLIST");
        if (tickerList == null) {
            Long invBaseActiveId = getLongParamLogged(baseActive, "INVBASEACTIVEID");
            if (invBaseActiveId != null) {
                // список тикеров отсутствует - следует получить его для дальнейшей обработки (например, при удалении стратегии или т.п.)
                Map<String, Object> hbDataParams = new HashMap<String, Object>();
                hbDataParams.put("INVBASEACTIVEID", invBaseActiveId);
                Map<String, Object> hbParams = new HashMap<String, Object>();
                hbParams.put("HANDBOOKNAME", BASE_ACTIVE_TICKER_RELATION_HB_NAME);
                hbParams.put("HANDBOOKDATAPARAMS", hbDataParams);
                hbParams.put(RETURN_LIST_ONLY, true);
                tickerList = callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BHandbookDataBrowseByHBName", hbParams, login, password);
                if ((tickerList != null) && (!tickerList.isEmpty())) {
                    // методам типа dsB2BRemoveHandbookDataEx и т.п. требуется HBDATAVERID - требуется получить его отдельно
                    Map<String, Object> hbInfoParams = new HashMap<String, Object>();
                    hbInfoParams.put("SYSNAME", BASE_ACTIVE_TICKER_RELATION_HB_NAME);
                    hbInfoParams.put(RETURN_AS_HASH_MAP, true);
                    Map<String, Object> hbInfo = callService(B2BPOSWS_SERVICE_NAME, "dsB2BLoadHandbookBySysname", hbInfoParams, login, password);
                    if (isCallResultOK(hbInfo)) {
                        Long hbDataVerId = getLongParamLogged(hbInfo, "HBDATAVERID");
                        if (hbDataVerId != null) {
                            for (Map<String, Object> ticker : tickerList) {
                                ticker.put("HBDATAVERID", hbDataVerId);
                            }
                        }
                    }
                }
            }
        }

        RowStatus baseActiveRowStatus = getRowStatusLogged(baseActive);
        if (DELETED.equals(baseActiveRowStatus) && (tickerList != null)) {
            List<Map<String, Object>> tickerListDeleting = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> ticker : tickerList) {
                Boolean isMarked = markAsDeleted(ticker, "INVBATICKERID");
                if (isMarked) {
                    tickerListDeleting.add(ticker);
                }
            }
            tickerList = tickerListDeleting;
        }

        Map<String, Object> baseActiveParams = new HashMap<String, Object>();
        // baseActiveParams.put("HBDATAVERID", baseActive.remove("HBDATAVERID")); // перенесено в dsB2BSaveHandbookDataEx
        baseActiveParams.put("HBITEM", baseActive);
        baseActiveParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> baseActiveSaved = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BSaveHandbookDataEx", baseActiveParams, login, password);
        if (isCallResultOK(baseActiveSaved)) {
            result = baseActiveSaved;
            Long baseActiveId = getLongParamLogged(baseActiveSaved, "INVBASEACTIVEID");
            if ((baseActiveId != null) && (tickerList != null)) {
                List<Map<String, Object>> tickerListProcessed = new ArrayList<Map<String, Object>>();
                for (Map<String, Object> ticker : tickerList) {
                    RowStatus tickerRowStatus = getRowStatusLogged(ticker);
                    Map<String, Object> tickerProcessed;
                    if (UNMODIFIED.equals(tickerRowStatus)) {
                        tickerProcessed = ticker;
                    } else {
                        ticker.put("INVBASEACTIVEID", baseActiveId);
                        Map<String, Object> tickerParams = new HashMap<String, Object>();
                        // tickerParams.put("HBDATAVERID", ticker.remove("HBDATAVERID")); // перенесено в dsB2BSaveHandbookDataEx
                        tickerParams.put("HBITEM", ticker);
                        tickerParams.put(RETURN_AS_HASH_MAP, true);
                        tickerProcessed = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BSaveHandbookDataEx", tickerParams, login, password);
                        if (!isCallResultOK(tickerProcessed)) {
                            logger.error(String.format(
                                    "Error on dsB2BSaveHandbookDataEx call (with params: %s)! Details (call result): %s.",
                                    tickerParams, tickerProcessed
                            ));
                            result = null;
                            break;
                        }
                    }
                    if (!DELETED.equals(tickerRowStatus)) {
                        tickerListProcessed.add(tickerProcessed);
                    }
                }
                baseActiveSaved.put("TICKERLIST", tickerListProcessed);
            }
        } else {
            logger.error(String.format(
                    "Error on dsB2BSaveHandbookDataEx call (with params: %s)! Details (call result): %s.",
                    baseActiveParams, baseActiveSaved
            ));
        }
        if (result == null) {
            result = new HashMap<String, Object>();
            result.put(ERROR, "Ошибка в ходе сохранения данных фонда!");
            throw new Exception("Error during base active saving (see previously logged error messages) - exception was thrown to rollback transaction!");
        }
        return result;
    }

}
