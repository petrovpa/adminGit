package com.bivgroup.services.b2bposws.facade.pos.invest.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;

import java.util.*;

import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.util.CopyUtils;

/**
 * @author aklunok
 */
@Discriminator(2)
@BOName("B2BInvestCouponCustom")
public class B2BInvestCouponCustomFacade extends B2BBaseFacade {

    private static final String TICKERRATEDATA = "TICKERRATEDATA";
    private static final String TICKERDATA = "TICKERDATA";

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @param params <UL>
     *               <LI>INVAMID - ИД записи</LI>
     *               </UL>
     * @return <UL>
     * <LI>INVAMID - ИД записи</LI>
     * <LI>DISCRIMINATOR - Дискриминатор сущности</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRNUMBER - Номер договора</LI>
     * <LI>CALCDATE - Дата расчета</LI>
     * <LI>CPPCTMEM - % Купона (С Множ За Память)</LI>
     * <LI>CPCONTRAMVALUE - Купон В Валюте Договора</LI>
     * <LI>CPRVLTAMVALUE - Купон С Переоценкой, Руб</LI>
     * <LI>CPACRDCONTRAMVALUE - Начисленный Купон В Валюте Договора</LI>
     * <LI>CPACRDRVLTAMVALUE - Начисленный Купон С Переоценкой</LI>
     * <LI>PREMVALUE - Премия</LI>
     * <LI>CPPCTWMEM - % Купона (Без Множ За Память)</LI>
     * <LI>RATESTART - Курс На Дату Начала Действия</LI>
     * <LI>RATECALC - Курс На Дату Расчета</LI>
     * <LI>CONTRCURRENCYID - Валюта Полиса</LI>
     * <LI>CONDINVCURRENCYID - Валюта Условий Инвестирования</LI>
     * <LI>ISCONDITION - Выполнение Условия (0/1)</LI>
     * <LI>ISTODAYCPACRD - Сегодня Начисление Куп?(0/1)</LI>
     * <LI>BARRIERVALUE - Барьер</LI>
     * <LI>MULTIMEMVALUE - Множитель За Эффект Памяти</LI>
     * <LI>CONDNOTE - Описание Условий</LI>
     * <LI>CONDCODE - Код Условий</LI>
     * </UL>
     * @author reson
     */
    @WsMethod(requiredParams = {"CONTRNUMBER", "BACODE", "CONTRSTARTDATE"})
    public Map<String, Object> dsB2BInvestCouponBrowseListByParamEx(Map<String, Object> params) throws Exception {
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
        String contrNumber = getStringParam(params.get("CONTRNUMBER"));
        // Получить доходность по договору
        /*
        Map<String, Object> findContr = new HashMap<>();
        findContr.put(RETURN_AS_HASH_MAP, true);
        findContr.put("CONTRNUMBER", contrNumber);
        Map<String, Object> resContr = this.callService(Constants.B2BPOSWS, "dsB2BInvestCouponMaxDateBrowseListByParam", findContr, login, password);
        if (resContr.get(RESULT) == null) {
            return result;
        }
         */

        // Получить список базовых активов по корзине
        String baCode = getStringParam(params.get("BACODE"));
        Map<String, Object> findTicker = new HashMap<>();
        findTicker.put("INVBASEACTIVECODE", baCode);
        Map<String, Object> resTicker = this.callService(Constants.B2BPOSWS, "dsB2BInvestTickerBrowseListByParamEx", findTicker, login, password);
        if (resTicker.get(RESULT) == null) {
            return result;
        }

        // Цикл по базовым активам и получение котировок
        List<Map<String, Object>> tickerDataList = new ArrayList<>();
        List<Map<String, Object>> tickerList = (List<Map<String, Object>>) resTicker.get(RESULT);
        for (Map<String, Object> tickerItem : tickerList) {
            String tickerCode = getStringParam(tickerItem.get("CODE"));
            Map<String, Object> findTickerRate = new HashMap<>();
            findTickerRate.put("TICKERCODE", tickerCode);
            findTickerRate.put("TRSTARTDATE", trStartDate);
            findTickerRate.put("TRFINISHDATE", calcFinishDate);
            findTickerRate.put("CONTRNUMBER", contrNumber);
            Map<String, Object> resTickerRate = this.callService(Constants.B2BPOSWS, "dsB2BTickerRateCouponBrowseListByParamEx", findTickerRate, login, password);
            if (resTickerRate.get(RESULT) != null) {
                Map<String, Object> rateData = new HashMap<>();
                rateData.putAll(tickerItem);
                List<Map<String, Object>> resTickers = (List<Map<String, Object>>) resTickerRate.get(RESULT);
                resTickers.sort(new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        return ((Date) o2.get("TRDATE")).compareTo(((Date) o1.get("TRDATE")));
                    }
                });
                rateData.put(TICKERRATEDATA, resTickers);
                tickerDataList.add(rateData);
            }
        }
        result.put(TICKERDATA, tickerDataList);
        return result;
    }

    @WsMethod(requiredParams = {"CONTRNUMBER"})
    public Map<String, Object> dsB2BInvestCouponMaxDateBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BInvestCouponMaxDateBrowseListByParam", "dsB2BInvestCouponMaxDateBrowseListByParamCount", params);
        return result;
    }

    @WsMethod(requiredParams = {"TICKERCODE", "CONTRNUMBER", "TRSTARTDATE"})
    public Map<String, Object> dsB2BTickerRateCouponBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BTickerRateCouponBrowseListByParamEx", "dsB2BTickerRateCouponBrowseListByParamExCount", params);
        return result;
    }

}
