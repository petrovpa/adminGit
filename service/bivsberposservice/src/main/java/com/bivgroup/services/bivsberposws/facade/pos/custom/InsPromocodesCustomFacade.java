/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import com.bivgroup.services.bivsberposws.system.Constants;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author averishevsm
 */
@BOName("InsPromocodesCustom")
public class InsPromocodesCustomFacade extends BaseFacade {

    private static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;

    protected BigDecimal getBigDecimalParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigDecimal.valueOf(Double.valueOf(bean.toString()));
        } else {
            return null;
        }
    }

    @WsMethod(requiredParams = {})
    public void dsInsPromocodesDeleteEx(Map<String, Object> params) throws Exception {
        if ((params.get("SHAREID") != null) || (params.get("PROMOCODEID") != null)) {
            this.deleteQuery("dsInsPromocodesDeleteEx", params);
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsPromoBrowseEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> promoMap = (Map<String, Object>) params.get("PROMOMAP");
        Map<String, Object> result = new HashMap<String, Object>();
        BigDecimal value = BigDecimal.ONE;
        if (promoMap != null) {
            if (params.get("PRODVERID") != null) {
                Long prodVerId = Long.valueOf(params.get("PRODVERID").toString());
                Map<String, Object> shareParams = new HashMap<String, Object>();
                shareParams.put("PRODVERID", prodVerId);
                if (params.get("PRODID") != null) {
                    Long prodId = Long.valueOf(params.get("PRODID").toString());
                    shareParams.put("PRODID", prodId);
                }
                shareParams.put("WORKDATE", XMLUtil.convertDateToBigDecimal(new Date()));
                Map<String, Object> shareRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsSharesBrowseListByParamEx", shareParams, login, password);
                List<Map<String, Object>> shareList = WsUtils.getListFromResultMap(shareRes);
                boolean isShareExist = false;
                if ((shareList == null) || (shareList.isEmpty())) {
                    isShareExist = false;
                } else {
                    isShareExist = true;
                }
                result.put("ISSHAREEXIST", isShareExist);
                if (promoMap.get("promoCode") != null) {
                    if (!promoMap.get("promoCode").toString().isEmpty()) {
                        Map<String, Object> promoParams = new HashMap<String, Object>();
                        promoParams.put("PRODVERID", prodVerId);
                        promoParams.put("NOWDATE", XMLUtil.convertDateToBigDecimal(new Date()));
                        promoParams.put("PROMOCODE", promoMap.get("promoCode"));
                        Map<String, Object> promoRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsPromocodesBrowseListByParamEx", promoParams, login, password);
                        if (promoRes.get(RESULT) != null) {
                            List<Map<String, Object>> promoList = WsUtils.getListFromResultMap(promoRes);
                            if (!promoList.isEmpty()) {
                                if (promoList.size() == 1) {
                                    value = getBigDecimalParam(promoList.get(0).get("VALUE"));
                                } else {
                                    BigDecimal temp = BigDecimal.ONE;
                                    for (Map<String, Object> promoCode : promoList) {
                                        temp = getBigDecimalParam(promoCode.get("VALUE"));
                                        if (value.compareTo(temp) > 0) {
                                            value = temp;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (value == null) {
            value = BigDecimal.ONE;
        }
        result.put("VALUE", value);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsInsPromocodesBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsInsPromocodesBrowseListByParamEx", "dsInsPromocodesBrowseListByParamExCount", params);
        return result;
    }



}
