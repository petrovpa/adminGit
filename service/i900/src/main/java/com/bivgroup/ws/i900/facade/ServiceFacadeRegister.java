package com.bivgroup.ws.i900.facade;

import com.bivgroup.ws.i900.facade.invest.LoadInvestCouponCustomFacade;
import com.bivgroup.ws.i900.facade.invest.LoadInvestCustomFacade;
import com.bivgroup.ws.i900.facade.invest.LoadInvestDIDCustomFacade;
import com.bivgroup.ws.i900.facade.invest.LoadTickerRateCustomFacade;
import com.bivgroup.ws.i900.facade.pos.custom.Cib900CustomFacade;
import com.bivgroup.ws.i900.facade.pos.custom.CibTMCustomFacade;
import com.bivgroup.ws.i900.facade.pos.custom.HibTMCustomFacade;
import com.bivgroup.ws.i900.facade.pos.custom.House900CustomFacade;
import com.bivgroup.ws.i900.facade.invest.InvestFastLoadData;
import com.bivgroup.ws.i900.facade.invest.InvestFileLoad;
import com.bivgroup.ws.i900.facade.pos.custom.mort900.Mort900CustomFacade;
import java.util.HashSet;
import java.util.Set;
import ru.diasoft.services.inscore.facade.FacadeRegister;

public class ServiceFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();

        // фасад для продукта Ипотека 900
        classes.add(Mort900CustomFacade.class);
        // Защита дома 900
        classes.add(House900CustomFacade.class);
        // Защита карты 900
        classes.add(Cib900CustomFacade.class);
        // фасад для продукта 'Защита дома ТМ'
        classes.add(HibTMCustomFacade.class);
        // фасад для продукта 'Защита карты ТМ'
        classes.add(CibTMCustomFacade.class);

        classes.add(LoadInvestCustomFacade.class);
        classes.add(LoadInvestCouponCustomFacade.class);
        classes.add(LoadInvestDIDCustomFacade.class);
        classes.add(LoadTickerRateCustomFacade.class);
        
        classes.add(InvestFastLoadData.class);
        classes.add(InvestFileLoad.class);
        return classes;
    }
}
