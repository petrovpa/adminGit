package com.bivgroup.services.b2bposws.facade.pos.invest.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import static com.bivgroup.services.b2bposws.facade.pos.invest.custom.B2BGraphHelper.OBJECT_DELIM;
import static com.bivgroup.services.b2bposws.facade.pos.invest.custom.B2BGraphHelper.SERIES_DATA_ITEM;
import static com.bivgroup.services.b2bposws.facade.pos.invest.custom.B2BGraphHelper.closeGraph;
import static com.bivgroup.services.b2bposws.facade.pos.invest.custom.B2BGraphHelper.openGraph;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@BOName("B2BInvestGraph")
public class B2BInvestGraphFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {"CONTRNUMBER", "CONTRSTARTDATE"})
    public Map<String, Object> dsB2BInvestGraphDataByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        // получить данные по договору
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Date tStartDate = getDateParam(params.get("CONTRSTARTDATE"));
        Date calcStartDate = getDateParam(params.get("CALCSTARTDATE"));
        Date calcFinishDate = getDateParam(params.get("CALCFINISHDATE"));
        if ((calcStartDate != null) && (tStartDate.before(calcStartDate))) {
            tStartDate.setTime(calcStartDate.getTime());
        }
        if (calcFinishDate == null) {
            calcFinishDate = new Date();
        }

        Map<String, Object> findParams = new HashMap<>();
        findParams.put("CONTRNUMBER", params.get("CONTRNUMBER"));
        findParams.put("CALCSTARTDATE", tStartDate);
        findParams.put("CALCFINISHDATE", calcFinishDate);

        Map<String, Object> res = this.callService(Constants.B2BPOSWS, "dsB2BInvestBrowseListByParamEx", findParams, login, password);
        if (res.get(RESULT) == null) {
            return result;
        }
        List<Map<String, Object>> investList = (List<Map<String, Object>>) res.get(RESULT);
        // выдать результат GRAPHDATA
        result.put("GRAPHDATA", buildGraph(investList));
        return result;
    }

    private String buildGraph(List<Map<String, Object>> investList) {
        // сформировать серии
        StringBuilder graphBuilder = new StringBuilder();
        openGraph(graphBuilder);

        // массив серий, 4 серии
        StringBuilder gr1 = new StringBuilder();
        B2BGraphHelper.openSeries(gr1, "Гарантия");

        StringBuilder gr2 = new StringBuilder();
        B2BGraphHelper.openSeries(gr2, "Страховая сумма + ДИД");

        StringBuilder gr3 = new StringBuilder();
        B2BGraphHelper.openSeries(gr3, "Базовый Актив");

        StringBuilder gr4 = new StringBuilder();
        B2BGraphHelper.openSeries(gr4, "Страховая сумма + ИДД");

        for (Map<String, Object> invItem : investList) {
            Date calcDate = getDateParam(invItem.get("CALCDATE"));
            Double indValue = getDoubleParam(invItem.get("INDVALUE"));
            Double insAmValue = getDoubleParam(invItem.get("INSAMVALUE"));
            Double baValue = getDoubleParam(invItem.get("BAVALUE"));
            Double insAmIDDValue = getDoubleParam(invItem.get("INSAMIDDVALUE"));
            gr1.append(String.format(SERIES_DATA_ITEM, calcDate.getTime(), indValue, "1"));
            gr2.append(String.format(SERIES_DATA_ITEM, calcDate.getTime(), insAmValue, "1"));
            gr3.append(String.format(SERIES_DATA_ITEM, calcDate.getTime(), baValue, "1"));
            gr4.append(String.format(SERIES_DATA_ITEM, calcDate.getTime(), insAmIDDValue, "1"));
        }

        // delete last char
        if (gr1.charAt(gr1.length() - 1) == OBJECT_DELIM) {
            gr1.deleteCharAt(gr1.length() - 1);
            gr2.deleteCharAt(gr2.length() - 1);
            gr3.deleteCharAt(gr3.length() - 1);
            gr4.deleteCharAt(gr4.length() - 1);
        }

        B2BGraphHelper.closeSeries(gr1, "#90ed7d");
        B2BGraphHelper.closeSeries(gr2, "#f7a35c");
        B2BGraphHelper.closeSeries(gr3, "#7cb5ec");
        B2BGraphHelper.closeSeries(gr4, "#bae0ba");

        graphBuilder.append(gr1);
        graphBuilder.append(gr2);
        graphBuilder.append(gr3);
        graphBuilder.append(gr4);
        // delete last char
        if (graphBuilder.charAt(graphBuilder.length() - 1) == OBJECT_DELIM) {
            graphBuilder.deleteCharAt(graphBuilder.length() - 1);
        }
        closeGraph(graphBuilder);

        return graphBuilder.toString();
    }
}
