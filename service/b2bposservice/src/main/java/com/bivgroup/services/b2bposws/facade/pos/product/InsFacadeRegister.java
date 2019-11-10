package com.bivgroup.services.b2bposws.facade.pos.product;
import java.util.HashSet;
import java.util.Set;

import ru.diasoft.services.inscore.facade.FacadeRegister;


public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<>();
        classes.add(B2BActivationCodeFacade.class);
        classes.add(B2BDamageCategoryFacade.class);
        classes.add(B2BInsuranceEventFacade.class);
        classes.add(B2BInvestTrancheFacade.class);
        classes.add(B2BInvestTrancheKindFacade.class);
        classes.add(B2BProdInvestTrancheKindFacade.class);
        classes.add(B2BInvestBaseActiveFacade.class);
        classes.add(B2BInvestBaseActiveTickerFacade.class);
        classes.add(B2BInvestStrategyFacade.class);
        classes.add(B2BInvestStrategyGroupFacade.class);
        classes.add(B2BInvestStrategyGroupLinkFacade.class);
        classes.add(B2BInvestTickerFacade.class);
        classes.add(B2BPaymentVariantContentFacade.class);
        classes.add(B2BPaymentVariantFacade.class);
        classes.add(B2BProductActivationContentFacade.class);
        classes.add(B2BProductActivationFacade.class);
        classes.add(B2BProductActivationTypeFacade.class);
        classes.add(B2BProductAdditionalChangeTypeFacade.class);
        classes.add(B2BProductBinaryDocumentFacade.class);
        classes.add(B2BProductCalcRateRuleFacade.class);
        classes.add(B2BProductConfigFacade.class);
        classes.add(B2BProductDamageCategoryContentFacade.class);
        classes.add(B2BProductDamageCategoryFacade.class);
        classes.add(B2BProductDefaultValueFacade.class);
        classes.add(B2BProductDiscountFacade.class);
        classes.add(B2BProductDiscountPromoCodeFacade.class);
        classes.add(B2BProductDiscountValueFacade.class);
        classes.add(B2BProductElementaryRiskFacade.class);
        classes.add(B2BProductFacade.class);
        classes.add(B2BProductFormFacade.class);
        classes.add(B2BProductInsAmCurrencyFacade.class);
        classes.add(B2BProductInsuranceCoverFacade.class);
        classes.add(B2BProductIntegrationRouteFacade.class);
        classes.add(B2BProductInvestBaseFacade.class);
        classes.add(B2BProductInvestFacade.class);
        classes.add(B2BProductInvestGroupFacade.class);
        classes.add(B2BProductNumMethodFacade.class);
        classes.add(B2BProductPaymentVariantFacade.class);
        classes.add(B2BProductPossibleValueFacade.class);
        classes.add(B2BProductPremiumCurrencyFacade.class);
        classes.add(B2BProductProgramFacade.class);
        classes.add(B2BProductReportFacade.class);
        classes.add(B2BProductRiderFacade.class);
        classes.add(B2BProductRedemptionAmountFacade.class);
        classes.add(B2BProductSalesChannelFacade.class);
        classes.add(B2BProductStructureBaseFacade.class);
        classes.add(B2BProductStructureMultiProductFacade.class);
        classes.add(B2BProductStructureObjectFacade.class);
        classes.add(B2BProductStructureObjectGroupFacade.class);
        classes.add(B2BProductStructureOrFacade.class);
        classes.add(B2BProductStructureRiskFacade.class);
        classes.add(B2BProductStructureSectionFacade.class);
        classes.add(B2BProductTermFacade.class);
        classes.add(B2BProductValueAddAgrCauseFacade.class);
        classes.add(B2BProductValueBaseFacade.class);
        classes.add(B2BProductValueDamageCategoryFacade.class);
        classes.add(B2BProductValueFacade.class);
        classes.add(B2BProductValueStructureFacade.class);
        classes.add(B2BProductVersionFacade.class);
        classes.add(B2BReportFacade.class);
        classes.add(B2BTermFacade.class);

        return classes;
    }
}
