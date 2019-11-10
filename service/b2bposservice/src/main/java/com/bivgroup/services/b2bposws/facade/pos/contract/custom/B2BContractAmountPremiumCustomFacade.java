package com.bivgroup.services.b2bposws.facade.pos.contract.custom;

import java.util.*;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@BOName("B2BContractAmountPremiumCustomFacade")
public class B2BContractAmountPremiumCustomFacade extends BaseFacade {

    /*
     * Удалить объекты по ИД договора
     * @author aklunok
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public void dsB2BContractAmountPremiumDeleteByContrID(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractAmountPremiumDeleteByContrID", params);
    }

}
