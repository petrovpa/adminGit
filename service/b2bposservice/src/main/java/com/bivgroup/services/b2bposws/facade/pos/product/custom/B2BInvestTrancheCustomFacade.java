/*
* Copyright (c) Diasoft 2004-2013
 */
package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.utils.XMLUtil;

/**
 * Фасад для сущности B2BInvestTranche
 *
 * @author aklunok
 */
@BOName("B2BInvestTrancheCustom")
public class B2BInvestTrancheCustomFacade extends B2BBaseFacade {

    private final int DEFAULT_CONTRADD_TRANCHELAG = 14;
    private final String TRANCHELAGNAME = "CONTADDTRANCHELAG";

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>BARRIER - Значение барьера</LI>
     * <LI>CALCDATE - Дата расчета</LI>
     * <LI>CONTRACTSTARTDATE - Дата начала договоров проданных в окне
     * продаж</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>RATE - Ставка</LI>
     * <LI>SALEFINISHDATE - Дата закрытия окна продаж</LI>
     * <LI>SALESTARTDATE - Дата открытия окна продаж</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BARRIER - Значение барьера</LI>
     * <LI>CALCDATE - Дата расчета</LI>
     * <LI>CONTRACTSTARTDATE - Дата начала договоров проданных в окне
     * продаж</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>RATE - Ставка</LI>
     * <LI>SALEFINISHDATE - Дата закрытия окна продаж</LI>
     * <LI>SALESTARTDATE - Дата открытия окна продаж</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestTrancheBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BInvestTrancheBrowseListByParamEx", "dsB2BInvestTrancheBrowseListByParamExCount", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestTrancheBrowseListByParamToDataProv(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BInvestTrancheBrowseListByParamToDataProv", "dsB2BInvestTrancheBrowseListByParamToDataProvCount", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestTrancheNearDate(Map<String, Object> params) throws Exception {
//        int trancheLag = DEFAULT_CONTRADD_TRANCHELAG;
//        // Получить количество дней смещения для вычисления даты ближайшего транша
//        String login = getStringParam(params, LOGIN);
//        String password = getStringParam(params, PASSWORD);
//        Map<String, Object> proddefvalParams = new HashMap<>();
//        proddefvalParams.put("NAME", TRANCHELAGNAME);
//        proddefvalParams.put("PRODCONFID", params.get("PRODCONFID"));
//        proddefvalParams.put("ReturnAsHashMap", "TRUE");
//        Map<String, Object> proddefvalRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueByProdConfId", proddefvalParams, login, password);
//        if (proddefvalRes.get(TRANCHELAGNAME) != null) {
//            trancheLag = getIntegerParam(proddefvalRes.get(TRANCHELAGNAME));
//        }
        GregorianCalendar gcDate = new GregorianCalendar();
        gcDate.setTime(new Date());
        gcDate.set(Calendar.HOUR_OF_DAY, 0);
        gcDate.set(Calendar.MINUTE, 0);
        gcDate.set(Calendar.SECOND, 0);
        gcDate.set(Calendar.MILLISECOND, 0);
        //gcDate.add(Calendar.DAY_OF_YEAR, trancheLag);
        //params.put("CP_TODAYDATE", gcDate.getTime());
        //params.put("TRANCHESTARTDATE", gcDate.getTime());
        params.put("CPFINISH_TODAYDATE", gcDate.getTime());
        XMLUtil.convertDateToFloat(params);
        Map<String, Object> result = this.selectQuery("dsB2BInvestTrancheBrowseListByParamToDataProv", "dsB2BInvestTrancheBrowseListByParamToDataProvCount", params);
        return result;
    }

}
