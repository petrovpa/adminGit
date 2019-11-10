package com.bivgroup.services.b2bposws.facade.pos.invest.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import static com.bivgroup.services.b2bposws.facade.pos.invest.custom.B2BGraphHelper.OBJECT_DELIM;
import static com.bivgroup.services.b2bposws.facade.pos.invest.custom.B2BGraphHelper.SERIES_DATA_ITEM;
import static com.bivgroup.services.b2bposws.facade.pos.invest.custom.B2BGraphHelper.closeGraph;
import static com.bivgroup.services.b2bposws.facade.pos.invest.custom.B2BGraphHelper.openGraph;
import com.bivgroup.services.b2bposws.system.Constants;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
 * @author aklunok
 */
@BOName("B2BInvestCouponGraph")
public class B2BInvestCouponGraphFacade extends B2BBaseFacade {

    private String[] TICKERCOLORS = {"#90ed7d", "#f7a35c", "#7cb5ec", "#bae0ba", "#3e5b76", "#628db6", "#fdbf3b", "#ffebc1", "#6f5499", "#ffce56"};
    private int PERCENT100 = 100;

    @WsMethod(requiredParams = {"CONTRNUMBER", "BACODE", "CONTRSTARTDATE"})
    public Map<String, Object> dsB2BInvestCouponGraphDataByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        parseDates(params, Double.class);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // Сравнить CONTRSTARTDATE и CALCSTARTDATE
        Date contrStartDate = getDateParam(params.get("CONTRSTARTDATE"));
        Date calcStartDate = getDateParam(params.get("CALCSTARTDATE"));
        Date calcFinishDate = getDateParam(params.get("CALCFINISHDATE"));
        Date trStartDate = new Date(contrStartDate.getTime());
        if ((calcStartDate != null) && (trStartDate.before(calcStartDate))) {
            trStartDate.setTime(calcStartDate.getTime());
        }
        if (calcFinishDate == null) {
            calcFinishDate = new Date();
        }
        String contrNumber = getStringParam(params.get("CONTRNUMBER"));

        // Получить список базовых активов по корзине
        String baCode = getStringParam(params.get("BACODE"));
        Map<String, Object> findTicker = new HashMap<>();
        findTicker.put("INVBASEACTIVECODE", baCode);
        Map<String, Object> resTicker = this.callService(Constants.B2BPOSWS, "dsB2BInvestTickerBrowseListByParamEx", findTicker, login, password);
        if (resTicker.get(RESULT) == null) {
            return result;
        }

        List<Map<String, Object>> tickerList = (List<Map<String, Object>>) resTicker.get(RESULT);
        // выдать результат GRAPHDATA
        result.put("GRAPHDATA", buildGraph(tickerList, contrStartDate, trStartDate, calcFinishDate, login, password));
        return result;
    }

    private String buildGraph(List<Map<String, Object>> tickerList, Date contrStartDate,
            Date startDate, Date finishDate, String login, String password) throws Exception {
        // сформировать серии
        StringBuilder graphBuilder = new StringBuilder();
        openGraph(graphBuilder);

        // базовый актив
        StringBuilder grBA = new StringBuilder();
        buildBaseSeries(grBA, startDate, finishDate);
        graphBuilder.append(grBA);
        // Цикл по активам
        int i = 1;
        String colorSeries;
        for (Map<String, Object> tickerItem : tickerList) {
            StringBuilder grTicker = new StringBuilder();
            if (i < TICKERCOLORS.length) {
                colorSeries = TICKERCOLORS[i];
            } else {
                colorSeries = TICKERCOLORS[1];
            }
            buildTickerSeries(grTicker, tickerItem, contrStartDate, startDate, finishDate, colorSeries, login, password);
            graphBuilder.append(grTicker);
            i++;
        }

        // delete last char
        if (graphBuilder.charAt(graphBuilder.length() - 1) == OBJECT_DELIM) {
            graphBuilder.deleteCharAt(graphBuilder.length() - 1);
        }
        closeGraph(graphBuilder);

        return graphBuilder.toString();
    }

    private void buildBaseSeries(StringBuilder gr, Date startDate, Date finishDate) {
        B2BGraphHelper.openSeries(gr, "Начальная цена базового актива");
        GregorianCalendar gcCalcDate = new GregorianCalendar();
        gcCalcDate.setTime(startDate);
        GregorianCalendar gcCalcFinishDate = new GregorianCalendar();
        gcCalcFinishDate.setTime(finishDate);
        gcCalcFinishDate.add(Calendar.DATE, 1);
        while (gcCalcDate.before(gcCalcFinishDate)) {
            gr.append(String.format(SERIES_DATA_ITEM, gcCalcDate.getTimeInMillis(), PERCENT100, "1"));
            gcCalcDate.add(Calendar.DATE, 1);
        }
        if (gr.charAt(gr.length() - 1) == OBJECT_DELIM) {
            gr.deleteCharAt(gr.length() - 1);
        }
        B2BGraphHelper.closeSeries(gr, TICKERCOLORS[0]);
    }

    private void buildTickerSeries(StringBuilder gr, Map<String, Object> tickerItem, Date contrStartDate,
            Date startDate, Date finishDate, String colorSeries, String login, String password) throws Exception {
        String tickerCode = getStringParam(tickerItem.get("CODE"));
        String tickerName = getStringParam(tickerItem.get("NAME"));
        // Получить цену на начало действия договора
        Map<String, Object> findContTickerRate = new HashMap<>();
        findContTickerRate.put(RETURN_AS_HASH_MAP, true);
        findContTickerRate.put("TICKERCODE", tickerCode);
        findContTickerRate.put("TRSTARTDATE", contrStartDate);
        findContTickerRate.put("TRFINISHDATE", contrStartDate);
        Map<String, Object> resContrTickerRate = this.callService(Constants.B2BPOSWS, "dsB2BTickerRateBrowseListByParamEx", findContTickerRate, login, password);
        if (resContrTickerRate.get("RATEVALUE") == null) {
            return;
        }
        Double contrRate = getDoubleParam(resContrTickerRate.get("RATEVALUE"));
        if (contrRate <= 0) {
            return;
        }
        // Получить цены за период
        Map<String, Object> findTickerRate = new HashMap<>();
        findTickerRate.put("TICKERCODE", tickerCode);
        findTickerRate.put("TRSTARTDATE", startDate);
        findTickerRate.put("TRFINISHDATE", finishDate);
        Map<String, Object> resTickerRate = this.callService(Constants.B2BPOSWS, "dsB2BTickerRateBrowseListByParamEx", findTickerRate, login, password);
        if (resTickerRate.get(RESULT) == null) {
            return;
        }

        List<Map<String, Object>> tickerRateList = (List<Map<String, Object>>) resTickerRate.get(RESULT);
        if (tickerRateList.isEmpty()) {
            return;
        }

        Date rateDate;
        Double rateValue;
        Double grValue;
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        B2BGraphHelper.openSeries(gr, tickerName);
        for (Map<String, Object> rateItem : tickerRateList) {
            rateDate = getDateParam(rateItem.get("TRDATE"));
            rateValue = getDoubleParam(rateItem.get("RATEVALUE"));
            grValue = rateValue / contrRate * PERCENT100;
            gr.append(String.format(SERIES_DATA_ITEM, rateDate.getTime(), df.format(grValue), "1"));
        }

        if (gr.charAt(gr.length() - 1) == OBJECT_DELIM) {
            gr.deleteCharAt(gr.length() - 1);
        }
        B2BGraphHelper.closeSeries(gr, colorSeries);
    }

}
