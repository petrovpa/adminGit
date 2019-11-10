package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;

@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("B2BProductRedemptionAmountCustomFacade")
public class B2BProductRedemptionAmountCustomFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductRedemptionAmountBrowseListByParamEx(Map<String, Object> params) throws Exception {

        StringBuilder sb = new StringBuilder();

        if (params.get("sortModel") != null) {
            ArrayList<Object> sortModel = (ArrayList<Object>) params.get("sortModel");
            if (!sortModel.isEmpty()) {
                for (Iterator iterator = sortModel.iterator(); iterator.hasNext();) {
                    Map<String, String> sModel = (Map<String, String>) iterator.next();
                    if ((sModel.get("colId") != null) && (sModel.get("sort") != null)) {
                        sb.append(sModel.get("colId").toString() + " " + sModel.get("sort").toString());
                        sb.append(", ");
                    }
                }
                if (sb.length() > 1) {
                    sb.delete(sb.length() - 2, sb.length());
                    params.put("ORDERBY", sb.toString());
                }
            }
        }

        Map<String, Object> result = this.selectQuery("dsB2BProductRedemptionAmountBrowseListByParamEx", params);
        return result;
    }

}

