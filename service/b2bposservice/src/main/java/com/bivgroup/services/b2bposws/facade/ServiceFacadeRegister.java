package com.bivgroup.services.b2bposws.facade;

import com.bivgroup.services.b2bposws.facade.admin.*;
import com.bivgroup.services.b2bposws.facade.admin.lk.*;
import com.bivgroup.services.b2bposws.facade.client.B2BClientFacade;
import com.bivgroup.services.b2bposws.facade.km.ChatWithRightsFacade;
import com.bivgroup.services.b2bposws.facade.pos.InsFacadeRegister;
import com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.custom.B2BAddAgrCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.custom.B2BAddAgrDocCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.custom.B2bAddAgrCntCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.custom.PDDeclarationDocCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.clientProfile.*;
import com.bivgroup.services.b2bposws.facade.pos.clientmanager.ClientManagerFacade;
import com.bivgroup.services.b2bposws.facade.pos.declaration.change.utils.ClientManagerDataProviderFacade;
// import com.bivgroup.services.b2bposws.facade.pos.product.B2BProductRedemptionAmountFacade; // подключение данного фасала приводит к ошибке 500 при вызовах методов из b2bposws
import com.bivgroup.services.b2bposws.facade.pos.contract.custom.*;
import com.bivgroup.services.b2bposws.facade.pos.contract.mass.B2BContractMassFacade;
import com.bivgroup.services.b2bposws.facade.pos.contract.migration.B2BContractMigrationFacade;
import com.bivgroup.services.b2bposws.facade.pos.contract.migration.B2BContractStateHistoryMigrationFacade;
import com.bivgroup.services.b2bposws.facade.pos.country.B2BKindCountryFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.*;
import com.bivgroup.services.b2bposws.facade.pos.custom.antiMite.AntiMiteCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.baseactive.B2BBaseActiveCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.businessStab.BusinessStabCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.cargo.CargoCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.cargo.documents.ContractDocumentsFileSenderFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.cargo.documents.ContractDocumentsMailSenderFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.sberlife.CaringParentsCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.cib.CIBCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.export.*;
import com.bivgroup.services.b2bposws.facade.pos.custom.gap.GAPCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.hib.HIBCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.hibpremium.HIBPremiumCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.inscom.InsComCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.journals.B2BHandbookCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.journals.B2B_JournalCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.loss.B2BLossNoticeDocCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.mortgage.MortgageCustomFacade;
//import com.bivgroup.services.b2bposws.facade.pos.custom.mort900.Mort900CustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.multi.MultiCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.multiSetelem.MultiSetelemCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.newHorizons.NewHorizonsCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.rightChoise.RightChoiseCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.sberlife.*;
import com.bivgroup.services.b2bposws.facade.pos.custom.sbol.B2BContractSBOLCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.sbol.B2BSBOLReportCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.seatBelt.SeatBeltCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.sis.SISCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.vzr.VZRCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.declaration.change.*;
import com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider.*;
import com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider.cancellation.B2BReasonChangeDataProviderAnnulmentFacade;
import com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider.cancellation.B2BReasonChangeDataProviderCancellationFacade;
import com.bivgroup.services.b2bposws.facade.pos.declaration.change.print.B2BDeclarationOfChangePrintFacade;
import com.bivgroup.services.b2bposws.facade.pos.declaration.utils.B2BDeclarationOfChangeUtilsFacade;
import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryBinaryFileCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryClassifierCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.feedback.B2BSendEmailFeedbackFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.ClientManagerImportBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.content.*;
import com.bivgroup.services.b2bposws.facade.pos.invest.*;
import com.bivgroup.services.b2bposws.facade.pos.invest.custom.*;
import com.bivgroup.services.b2bposws.facade.pos.loss.B2BLossNoticeAttachmentFacade;
import com.bivgroup.services.b2bposws.facade.pos.loss.B2BLossNoticeCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.loss.B2BLossNoticeDocFacade;
import com.bivgroup.services.b2bposws.facade.pos.loss.B2BLossPaymentClaimCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.menu.MenuFacade;
import com.bivgroup.services.b2bposws.facade.pos.menu.custom.MenuCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.other.ChatFacade;
import com.bivgroup.services.b2bposws.facade.pos.other.MessageCorrespondentDictionaryFacade;
import com.bivgroup.services.b2bposws.facade.pos.other.MessageDictionaryFacade;
import com.bivgroup.services.b2bposws.facade.pos.other.custom.*;
import com.bivgroup.services.b2bposws.facade.pos.pay.B2BLkPayPrintFacade;
import com.bivgroup.services.b2bposws.facade.pos.print.B2BPrintCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.product.custom.*;
import com.bivgroup.services.b2bposws.facade.pos.robots.CaringParentsRobotFacade;
import com.bivgroup.services.b2bposws.facade.pos.sal.custom.SAL_CheckCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.sal.custom.SAL_InvalidPassportsCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.sal.custom.SAL_JournalCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.sal.custom.SAL_TerroristsCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.subsystem.B2bSubSystemCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.system.CoreUserCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.system.CurrencyIntegrationFacade;
import com.bivgroup.services.b2bposws.facade.pos.underwriting.QuestionnaireFacade;
import com.bivgroup.services.b2bposws.facade.pos.user.B2BUserTempPasswordFacade;
import com.bivgroup.services.b2bposws.facade.pos.unirest.B2BUniRestCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.unirest.B2BUniRestDefaultValidatorFacade;
import com.bivgroup.services.b2bposws.facade.pos.unirest.protectedBorrowerLongTerm.B2BUniRestProtectBorrowerLongTermFacade;
import com.bivgroup.services.b2bposws.facade.pos.userPost.B2BUserPostAttachmentFacade;
import com.bivgroup.services.b2bposws.facade.pos.userPost.B2BUserPostCustomFacade;
import ru.diasoft.services.inscore.facade.FacadeRegister;

import java.util.HashSet;
import java.util.Set;

//import com.bivgroup.services.b2bposws.facade.pos.custom.mort900.Mort900CustomFacade;

public class ServiceFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {

        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();

        classes.add(B2BActivateCustomFacade.class);
        classes.add(SAL_CheckCustomFacade.class);
        classes.add(SAL_InvalidPassportsCustomFacade.class);
        classes.add(SAL_TerroristsCustomFacade.class);
        classes.add(InsFacadeRegister.class);
        classes.add(B2BBaseFacade.class);

        classes.add(HealthFacade.class);
        classes.add(SessionCustomFacade.class);
        classes.add(ProductCustomFacade.class);
        classes.add(HandbookCustomFacade.class);
        classes.add(ProductContractCustomFacade.class);
        classes.add(B2BProductReportCustomFacade.class);
        classes.add(B2BContractCustomFacade.class);
        classes.add(B2BFastContractCustomFacade.class);
        classes.add(B2BFastContractNodeCustomFacade.class);
        classes.add(B2BFastContractExtCustomFacade.class);
        classes.add(B2BFastContractStateCustomFacade.class);
        classes.add(B2BMainActivityContractCustomFacade.class);
        classes.add(MenuFacade.class);
        classes.add(MenuCustomFacade.class);
        classes.add(B2BPrintCustomFacade.class);
        classes.add(B2BSendEmailFeedbackFacade.class);
        classes.add(B2BContractDocumentCustomFacade.class);
        classes.add(B2BSBOLReportCustomFacade.class);
        classes.add(B2BContractWithRightsCustomFacade.class);
        classes.add(B2BContractWithNewRightsCustomFacade.class);
        classes.add(B2BKMSBContractCustomFacade.class);
        // фасады по продукту
        classes.add(B2BProductVersionCustomFacade.class);
        classes.add(UserRoleCustomFacade.class);
        classes.add(B2BProductPaymentVariantCustomFacade.class);
        classes.add(B2BProductValueBaseCustomFacade.class);
        classes.add(B2BProductKindCustomFacade.class);
        classes.add(B2BProductProgramCustomFacade.class);

        // фасады для миграции
        classes.add(B2BContractMigrationFacade.class);
        classes.add(B2BContractStateHistoryMigrationFacade.class);
        classes.add(FSMigrationCustomFacade.class);
        //кастомные фасады специфики продуктов.
        classes.add(CargoCustomFacade.class);
        classes.add(GAPCustomFacade.class);
        classes.add(MultiCustomFacade.class);
        classes.add(VZRCustomFacade.class);
        classes.add(AntiMiteCustomFacade.class); // продукт 'Защита от клеща Онлайн'
        classes.add(SISCustomFacade.class);
        classes.add(BusinessStabCustomFacade.class);
        classes.add(KladrCustomFacade.class);
        classes.add(ParticipantCustomFacade.class);
        classes.add(ContractDocumentsMailSenderFacade.class);
        classes.add(ContractDocumentsFileSenderFacade.class);
        classes.add(B2BProductDefaultValueCustomFacade.class);
        classes.add(B2BAddAgrCustomFacade.class);
        classes.add(B2bAddAgrCntCustomFacade.class);
        classes.add(B2BAddAgrDocCustomFacade.class);
        // фасад для продукта Ипотека 900
        //classes.add(Mort900CustomFacade.class);
        // фасад для приема xml-договоров
        classes.add(B2BContractSBOLCustomFacade.class);
        // фасады сервиса для выгрузки данных из системы
        classes.add(ExportCustomFacade.class);
        classes.add(XMLExportCustomFacade.class);
        classes.add(CSVExportCustomFacade.class);
        classes.add(XLSExportCustomFacade.class);
        classes.add(AgentExportCustomFacade.class);
        classes.add(B2BExportDataTemplateCustomFacade.class);
        classes.add(B2BExportDataContentCustomFacade.class);
        classes.add(B2BExportDataDocumentCustomFacade.class);
        classes.add(B2BProductImportExportFacade.class);
        classes.add(B2BProductDamageCategoryCustomFacade.class);
        classes.add(B2BProductSalesChannelCustomFacade.class);
        classes.add(B2BProductDiscountCustomFacade.class);
        classes.add(B2BProductStructureBaseCustomFacade.class);
        classes.add(B2BUserAccountCustomFacade.class);
        classes.add(InsComCustomFacade.class);
        classes.add(MultiSetelemCustomFacade.class);
        classes.add(HIBPremiumCustomFacade.class);
        classes.add(B2BReferralCustomFacade.class);
        classes.add(B2BCalendarCustomFacade.class);

        // Страна
        classes.add(B2BKindCountryFacade.class);

        classes.add(B2B_JournalCustomFacade.class);
        classes.add(SAL_JournalCustomFacade.class);

        classes.add(MortgageCustomFacade.class);
        classes.add(HIBCustomFacade.class);
        classes.add(CIBCustomFacade.class);

        // фасады для редактора продуктов
        classes.add(B2BProductEditorCustomFacade.class);
        classes.add(B2BProductPossibleValueCustomFacade.class);

        // фасады для работы с данными продукта
        classes.add(B2BProductInsAmCurrencyCustomFacade.class);
        classes.add(B2BProductPremiumCurrencyCustomFacade.class);

        // фасад для работы с состояниями
        classes.add(B2BStateCustomFacade.class);
        classes.add(B2BHandbookCustomFacade.class);

        // фасады продуктов страхования жизни.
        classes.add(CapitalCustomFacade.class);
        classes.add(FirstStepCustomFacade.class);
        classes.add(InvestNum1CustomFacade.class);
        classes.add(InvestCouponCustomFacade.class);
        classes.add(RightDecisionCustomFacade.class);
        classes.add(BorrowerProtectCustomFacade.class);
        classes.add(BorrowerProtectLongTermCustomFacade.class);
        classes.add(B2BProductBinaryDocumentCustomFacade.class);
        classes.add(SberLifePrintCustomFacade.class);
        classes.add(B2BProductTermCustomFacade.class);
        classes.add(CaringParentsCustomFacade.class);
        classes.add(SmartPolicyFacade.class);
        classes.add(SmartPolicyLightFacade.class);

        // робот для продукта Заботливые родители
        classes.add(CaringParentsRobotFacade.class);

        // 'Линия роста'
        classes.add(SHTaskCustomFacade.class);

        // 'Новые горизонты'
        classes.add(NewHorizonsCustomFacade.class);

        classes.add(RightChoiseCustomFacade.class);
        classes.add(SeatBeltCustomFacade.class);
        classes.add(ExportQueryCustomFacade.class);

        classes.add(CurrencyIntegrationFacade.class);
        classes.add(B2BPaymentFactCustomFacade.class);
        classes.add(B2BMemberCustomFacade.class);
        classes.add(B2BDocumentTypeCustomFacade.class);
        classes.add(B2BContractRiskCustomFacade.class);
        classes.add(B2BShareContractCustomFacade.class);

        classes.add(B2BHandbookViewDescriptionCustomFacade.class);
        classes.add(LifeAgentCommissCalculatorMethodsCustomFacade.class);
        classes.add(B2BAgentCommissCustomFacade.class);

        classes.add(CoreUserCustomFacade.class);
        //<editor-fold desc="Фасады администрирования">
        classes.add(B2BProfileRightFacade.class);
        //Фасад для работы с "Меню администратора"
        classes.add(B2BAdminMenuFacade.class);
        //Фасад для работы с "права на меню"
        classes.add(B2BMenuRightFacade.class);
        // Фасады для работы с администрированием пользователея
        classes.add(B2BAdminUsersFacade.class);
        classes.add(B2BAdminUserRoleFacade.class);
        classes.add(B2BRoleFacade.class);
        classes.add(B2BDepartmentCustomFacade.class);
        classes.add(B2BAdminFacade.class);
        classes.add(B2BAdminUsersPartnerFacade.class);
        classes.add(B2BRoleCustomFacade.class);
        classes.add(B2BContractAmountPremiumCustomFacade.class);

        //Фасады для администирования ЛК СБСЖ
        classes.add(B2BAdminLKBaseFacade.class);
        classes.add(B2BAdminLKContractFacade.class);
        classes.add(B2BAdminLKChangeFacade.class);
        classes.add(B2BAdminLKInsEventFacade.class);
        classes.add(B2BAdminLKInsEventMessageFacade.class);
        classes.add(B2BAdminLKMessageListFacade.class);
        classes.add(B2BAdminLKAccountListFacade.class);

        // словарная система
        classes.add(DictionaryClassifierCustomFacade.class);
        classes.add(DictionaryBinaryFileCustomFacade.class);

        //Сообщения
        classes.add(MessageCustomFacade.class);
        classes.add(ChatFacade.class);
        classes.add(ChatWithRightsFacade.class);
        classes.add(MessageDictionaryFacade.class);
        classes.add(MessageCorrespondentDictionaryFacade.class);

        classes.add(B2BUserPostCustomFacade.class);
        classes.add(B2BUserPostAttachmentFacade.class);

        // 'Фонды'
        classes.add(B2BBaseActiveCustomFacade.class);

        // стратегии инвестирования по продукту
        classes.add(B2BProductInvestmentStrategyCustomFacade.class);
        // профиль клиента
        classes.add(B2BClientProfileCustomFacade.class);
        classes.add(B2BClientProfileNotificationsFacade.class);
        // профиль клиента - события
        classes.add(B2BClientProfileEventCustomFacade.class);
        classes.add(B2BClientFacade.class);
        classes.add(B2BClientPropertyCustomFacade.class);

        classes.add(B2BInvestFacade.class);
        classes.add(B2BInvestCouponFacade.class);
        classes.add(B2BInvestDIDFacade.class);
        classes.add(B2BInvestCommonFacade.class);
        classes.add(B2BTickerRateFacade.class);

        classes.add(B2BInvestCustomFacade.class);
        classes.add(B2BInvestCouponCustomFacade.class);
        classes.add(B2BInvestDIDCustomFacade.class);
        classes.add(B2BInvestTickerCustomFacade.class);
        classes.add(B2BTickerRateCustomFacade.class);
        classes.add(B2BInvestDIDDynamicReportDataProvider.class);

        classes.add(B2BInvestGraphFacade.class);
        classes.add(B2BInvestCouponGraphFacade.class);
        classes.add(B2BInvestBaseActiveTickerCustomFacade.class);

        classes.add(B2BInvestBaseActiveCustomFacade.class);
        classes.add(B2BInvestTrancheCustomFacade.class);
        //</editor-fold>

        // Убытки - Уведомление о СС
        classes.add(B2BLossNoticeCustomFacade.class);
        classes.add(B2BLossNoticeAttachmentFacade.class);
        classes.add(B2BLossNoticeDocFacade.class);
        classes.add(B2BLossNoticeDocCustomFacade.class);
        // Убытки - Печать заявления на выплату
        classes.add(B2BLossPaymentClaimCustomFacade.class);

        // Допсы - Заявление на изменение условий договора страхования
        classes.add(B2BDeclarationOfChangeCustomFacade.class);
        classes.add(B2BDeclarationOfChangeUtilsFacade.class);
        // Допсы - Причина изменения договора (т.н. опция)
        classes.add(B2BReasonChangeCustomFacade.class);
        // Допсы - печать
        classes.add(B2BDeclarationOfChangePrintFacade.class);
        // дата провайдеры по допсам
        classes.add(B2BReasonChangeDataProviderCustomFacade.class);
        classes.add(B2BReasonChangeDataProviderPersonalDataContractorsFacade.class);
        classes.add(B2BReasonChangeDataProviderDuplicateDocumentFacade.class);
        classes.add(B2BReasonChangeDataProviderOptionsFacade.class);
        classes.add(B2BReasonChangeDataProviderTechnicalFacade.class);
        classes.add(B2BReasonChangeDataProviderBenFacade.class);
        classes.add(B2BReasonChangeDataProviderHolderFacade.class);
        classes.add(B2BReasonChangeDataProviderAnnulmentFacade.class);
        classes.add(B2BReasonChangeDataProviderCancellationFacade.class);
        classes.add(ClientManagerDataProviderFacade.class);
        // Допсы - Прикрепление файлов напрямую к заявлению на изменение условий договора страхования
        classes.add(B2BDeclarationOfChangeAttachmentFacade.class);
        classes.add(PDDeclarationDocCustomFacade.class);

        // Сервис для печати платежки. ЛК
        classes.add(B2BLkPayPrintFacade.class);

        // Опросники -- андеррайтинг
        classes.add(QuestionnaireFacade.class);

        // 'Фонды'
        classes.add(B2BBaseActiveCustomFacade.class);

        // стратегии инвестирования по продукту
        classes.add(B2BProductInvestmentStrategyCustomFacade.class);

        classes.add(B2BContractMassFacade.class);

        // выкупные суммы
        classes.add(B2BProductRedemptionAmountCustomFacade.class);

        // unirest
        classes.add(B2BUniRestCustomFacade.class);
        classes.add(B2BUniRestDefaultValidatorFacade.class);
        // unirest. Защищенный заемщик. Многолетний
        classes.add(B2BUniRestProtectBorrowerLongTermFacade.class);

        // uniopenapi
        classes.add(com.bivgroup.services.b2bposws.facade.pos.uniopenapi.InsFacadeRegister.class);

        classes.add(B2BUserTempPasswordFacade.class); // временные пароли
        // Фасады для КМ СБ1
        // Базовый
        classes.add(ClientManagerImportBaseFacade.class);
        // Журнал загрузок
        // Содержимое
        classes.add(ClientManagerImportSessionBaseFacade.class);
        classes.add(ClientManagerImportSessionOrgstructFacade.class);
        classes.add(ClientManagerImportSessionKmVspFacade.class);
        classes.add(ClientManagerImportSessionKmContractFacade.class);

        // Фасады для импорта данных по оргструктуре, КМ, ВСП и пр. (#18463)
        classes.add(com.bivgroup.services.b2bposws.facade.pos.importsession.InsFacadeRegister.class);
        classes.add(ClientManagerSessionImportFacade.class);

        // Фасад для работы с клиентским менеджером
        classes.add(ClientManagerFacade.class);

        // Фасад для канала продаж
        classes.add(B2bSubSystemCustomFacade.class);

        return classes;

    }

}
