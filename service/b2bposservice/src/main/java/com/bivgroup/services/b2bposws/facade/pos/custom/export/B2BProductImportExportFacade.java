package com.bivgroup.services.b2bposws.facade.pos.custom.export;

import com.bivgroup.schema.b2bcommon10.*;
import com.bivgroup.schema.b2bproduct10.*;
import com.bivgroup.schema.b2btarificator10.*;
import com.bivgroup.services.b2bposws.system.Constants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.io.IOUtils;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

/**
 * Фасад для импорта/экспорта продуктов из/в XML
 *
 * @author ilich
 */
@BOName("B2BProductImportExport")
public class B2BProductImportExportFacade extends BaseFacade {

    public static final String SERVICE_NAME = "b2bposws";

    protected <T> String marshall(T inputObject) throws Exception {
        String result = null;
        try {
            String packageName = inputObject.getClass().getPackage().getName();
            JAXBContext jc = JAXBContext.newInstance(packageName);
            Marshaller m = jc.createMarshaller();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            m.marshal(inputObject, baos);
            result = new String(baos.toByteArray(), "UTF-8");
        } catch (JAXBException ex) {
            throw new Exception("Ошибка преобразования объекта в xml", ex);
        } catch (RuntimeException ex) {
            throw new Exception("Ошибка преобразования объекта в xml", ex);
        } catch (UnsupportedEncodingException ex) {
            throw new Exception("Ошибка преобразования объекта в xml", ex);
        }
        return result;
    }

    protected <T> T unmarshall(Class<T> docClass, String xmlText) throws Exception {
        T result = null;
        try {
            result = this.unmarshall(docClass, new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            throw new Exception(ex);
        }
        return result;
    }

    protected <T> T unmarshall(Class<T> docClass, InputStream inputStream) throws Exception {
        try {
            String packageName = docClass.getPackage().getName();
            JAXBContext jc = JAXBContext.newInstance(packageName);
            Unmarshaller u = jc.createUnmarshaller();
            T result = (T) u.unmarshal(inputStream);
            return result;
        } catch (JAXBException ex) {
            throw new Exception("Ошибка преобразования хмл в объект", ex);
        } catch (RuntimeException ex) {
            throw new Exception("Ошибка преобразования хмл в объект", ex);
        }
    }

    protected Integer getIntegerParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Integer.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected BigDecimal getBigDecimalParamNoScale(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigDecimal.valueOf(Double.valueOf(bean.toString()));
        } else {
            return null;
        }
    }

    protected BigDecimal getBigDecimalParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigDecimal.valueOf(Double.valueOf(bean.toString())).setScale(2, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    protected Boolean getBooleanParam(Object bean, Boolean defVal) {
        if (bean != null && bean.toString().trim().length() > 0) {
            if (bean.toString().equalsIgnoreCase("0")) {
                return Boolean.FALSE;
            } else if (bean.toString().equalsIgnoreCase("1")) {
                return Boolean.TRUE;
            } else {
                return Boolean.valueOf(bean.toString());
            }
        } else {
            return defVal;
        }
    }

    protected Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected Date getDateParam(Object date) {
        if (date != null) {
            if (date instanceof Long) {
                return new Date(((Long) date).longValue());
            } else if (date instanceof Double) {
                return XMLUtil.convertDate((Double) date);
            } else if (date instanceof BigDecimal) {
                return XMLUtil.convertDate((BigDecimal) date);
            }
            return (Date) date;
        } else {
            return null;
        }
    }

    protected String getStringParam(Object bean) {
        if (bean == null) {
            return null;
        } else {
            return bean.toString();
        }
    }

    protected Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected Double bigDecimalToDouble(BigDecimal value) {
        if (value != null) {
            return value.doubleValue();
        } else {
            return null;
        }
    }

    private XMLGregorianCalendar dateToXMLGC(Date date) throws Exception {
        XMLGregorianCalendar result = null;
        if (date != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(date);
            try {
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            } catch (DatatypeConfigurationException ex) {
                throw new Exception("Error convert Date to XMLGregorianCalendar", ex);
            }
        }
        return result;
    }

    private XMLGregorianCalendar getXMLGCDateParam(Object bean) throws Exception {
        return dateToXMLGC(getDateParam(bean));
    }

    private Date xmlGCToDate(XMLGregorianCalendar xmlGC) {
        if ((xmlGC != null) && (xmlGC.toGregorianCalendar() != null)) {
            return xmlGC.toGregorianCalendar().getTime();
        } else {
            return null;
        }
    }

    private B2BProductRidersType getB2BProductRidersType(Map<String, Object> productMap) {
        B2BProductRidersType result = null;
        if ((productMap.get("PRODRIDERLIST") != null) && (((List<Map<String, Object>>) productMap.get("PRODRIDERLIST")).size() > 0)) {
            result = new B2BProductRidersType();
            List<Map<String, Object>> productRidersList = (List<Map<String, Object>>) productMap.get("PRODRIDERLIST");
            for (Map<String, Object> bean : productRidersList) {
                B2BProductRiderType rider = new B2BProductRiderType();
                rider.setId(getIntegerParam(bean.get("PRODRIDERID"))); // !! временно, ид не нужно передавать
                result.getB2BProductRiders().add(rider);
            }
        }
        return result;
    }

    private B2BProductVersionsType getB2BProductVersionsType(Map<String, Object> productMap) throws Exception {
        B2BProductVersionsType result = null;
        if ((productMap.get("PRODVERLIST") != null) && (((List<Map<String, Object>>) productMap.get("PRODVERLIST")).size() > 0)) {
            result = new B2BProductVersionsType();
            List<Map<String, Object>> productVersionsList = (List<Map<String, Object>>) productMap.get("PRODVERLIST");
            for (Map<String, Object> bean : productVersionsList) {
                B2BProductVersionType version = new B2BProductVersionType();
                version.setExploitationFinishDate(getXMLGCDateParam(bean.get("EXPLFINISHDATE")));
                version.setExploitationStartDate(getXMLGCDateParam(bean.get("EXPLSTARTDATE")));
                version.setImagePath(getStringParam(bean.get("IMGPATH")));
                version.setJavaScriptPath(getStringParam(bean.get("JSPATH")));
                version.setLogotype(getStringParam(bean.get("LOGOTYPE")));
                version.setName(getStringParam(bean.get("NAME")));
                version.setNote(getStringParam(bean.get("NOTE")));
                version.setProductCode(getStringParam(bean.get("PRODCODE")));
                version.setStateId(getIntegerParam(bean.get("STATEID")));
                version.setB2BProductConfigs(getB2BProductConfigsType((List<Map<String, Object>>) bean.get("PRODCONFLIST")));
                version.setB2BProductStructures(getB2BProductStructuresType((List<Map<String, Object>>) bean.get("PRODSTRUCTLIST")));
                version.setB2BProductSalesChannels(getB2BProductSalesChannelsType((List<Map<String, Object>>) bean.get("PRODSALESCHANLIST")));
                version.setB2BProductPaymentVariants(getB2BProductPaymentVariantsType((List<Map<String, Object>>) bean.get("PRODPAYVARLIST")));
                version.setB2BProductPrograms(getB2BProductProgramsType((List<Map<String, Object>>) bean.get("PRODPROGLIST")));
                result.getB2BProductVersions().add(version);
            }
        }
        return result;
    }

    private B2BProductConfigsType getB2BProductConfigsType(List<Map<String, Object>> list) throws Exception {
        B2BProductConfigsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductConfigsType();
            for (Map<String, Object> bean : list) {
                B2BProductConfigType config = new B2BProductConfigType();
                config.setCalculator(getCalculatorType((Map<String, Object>) bean.get("CALCMAP")));
                config.setValuesHB(getHandbookDescriptorType((Map<String, Object>) bean.get("VALUESHB")));
                config.setName(getStringParam(bean.get("NAME")));
                config.setNote(getStringParam(bean.get("NOTE")));
                config.setSysName(getStringParam(bean.get("SYSNAME")));
                config.setB2BProductValues(getB2BProductValuesForConfigType(bean));
                config.setB2BProductDiscounts(getB2BProductDiscountsType((List<Map<String, Object>>) bean.get("PRODDISCLIST")));
                config.setB2BProductForms(getB2BProductFormsType((List<Map<String, Object>>) bean.get("PRODFORMLIST")));
                config.setB2BProductCalcRateRules(getB2BProductCalcRateRulesType((List<Map<String, Object>>) bean.get("PRODCALCRATERULELIST")));
                config.setB2BProductPremiumCurrencies(getB2BProductPremiumCurrenciesType((List<Map<String, Object>>) bean.get("PRODPREMCURLIST")));
                config.setB2BProductInsAmCurrencies(getB2BProductInsAmCurrenciesType((List<Map<String, Object>>) bean.get("PRODINSAMCURLIST")));
                config.setB2BProductNumMethods(getB2BProductNumMethodsType((List<Map<String, Object>>) bean.get("PRODNUMMETHODLIST")));
                config.setB2BProductDefaultValues(getB2BProductDefaultValuesType((List<Map<String, Object>>) bean.get("PRODDEFVALLIST")));
                config.setB2BProductReports(getB2BProductReportsType((List<Map<String, Object>>) bean.get("PRODREPLIST")));
                config.setB2BProductBinaryDocuments(getB2BProductBinaryDocumentsType((List<Map<String, Object>>) bean.get("PRODBINDOCLIST")));
                result.getB2BProductConfigs().add(config);
            }
        }
        return result;
    }

    private CalculatorVersionsType getCalculatorVersionsType(List<Map<String, Object>> list) {
        CalculatorVersionsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new CalculatorVersionsType();
            for (Map<String, Object> bean : list) {
                CalculatorVersionType version = new CalculatorVersionType();
                version.setName(getStringParam(bean.get("NAME")));
                version.setNote(getStringParam(bean.get("NOTE")));
                version.setCalculatorFormulas(getCalculatorFormulasType((List<Map<String, Object>>) bean.get("FORMULALIST")));
                version.setCalculatorConsts(getCalculatorConstsType((List<Map<String, Object>>) bean.get("CONSTLIST")));
                version.setCalculatorInputParams(getCalculatorInputParamsType((List<Map<String, Object>>) bean.get("INPUTPARAMLIST")));
                version.setCalculatorHandbooks(getCalculatorHandbooksType((List<Map<String, Object>>) bean.get("HANDBOOKLIST")));
                result.getCalculatorVersions().add(version);
            }
        }
        return result;
    }

    private CalculatorHandbooksType getCalculatorHandbooksType(List<Map<String, Object>> list) {
        CalculatorHandbooksType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new CalculatorHandbooksType();
            for (Map<String, Object> bean : list) {
                CalculatorHandbookType handbook = new CalculatorHandbookType();
                handbook.setHandbook(getHandbookDescriptorType((Map<String, Object>) bean.get("HBMAP")));
                handbook.setNote(getStringParam(bean.get("NOTE")));
                result.getCalculatorHandbooks().add(handbook);
            }
        }
        return result;
    }

    private CalculatorInputParamsType getCalculatorInputParamsType(List<Map<String, Object>> list) {
        CalculatorInputParamsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new CalculatorInputParamsType();
            for (Map<String, Object> bean : list) {
                CalculatorInputParamType inputParam = new CalculatorInputParamType();
                inputParam.setDbgDoubleValue(getBigDecimalParamNoScale(bean.get("DBGDOUBLEVALUE")));
                inputParam.setDbgLongValue(getIntegerParam(bean.get("DBGLONGVALUE")));
                inputParam.setDbgStringValue(getStringParam(bean.get("DBGSTRINGVALUE")));
                inputParam.setIpJavaType(getStringParam(bean.get("IPJAVATYPE")));
                inputParam.setName(getStringParam(bean.get("NAME")));
                result.getCalculatorInputParams().add(inputParam);
            }
        }
        return result;
    }

    private CalculatorConstsType getCalculatorConstsType(List<Map<String, Object>> list) {
        CalculatorConstsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new CalculatorConstsType();
            for (Map<String, Object> bean : list) {
                CalculatorConstType constant = new CalculatorConstType();
                constant.setConstJavaType(getStringParam(bean.get("CONSTJAVATYPE")));
                constant.setDoubleValue(getBigDecimalParamNoScale(bean.get("DOUBLEVALUE")));
                constant.setIsStored(getBooleanParam(bean.get("ISSTORED"), Boolean.FALSE));
                constant.setLongValue(getIntegerParam(bean.get("LONGVALUE")));
                constant.setName(getStringParam(bean.get("NAME")));
                constant.setNote(getStringParam(bean.get("NOTE")));
                constant.setStringValue(getStringParam(bean.get("STRINGVALUE")));
                result.getCalculatorConstTypes().add(constant);
            }
        }
        return result;
    }

    private CalculatorFormulasType getCalculatorFormulasType(List<Map<String, Object>> list) {
        CalculatorFormulasType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new CalculatorFormulasType();
            for (Map<String, Object> bean : list) {
                CalculatorFormulaType formula = new CalculatorFormulaType();
                formula.setFormula(getStringParam(bean.get("FORMULA")));
                formula.setIsCached(getBooleanParam(bean.get("ISCACHED"), Boolean.FALSE));
                formula.setIsStored(getBooleanParam(bean.get("ISSTORED"), Boolean.FALSE));
                formula.setName(getStringParam(bean.get("NAME")));
                formula.setNote(getStringParam(bean.get("NOTE")));
                formula.setReturnJavaType(getStringParam(bean.get("RETURNJAVATYPE")));
                formula.setReturnParamName(getStringParam(bean.get("RETPARAMNAME")));
                result.getCalculatorFormulas().add(formula);
            }
        }
        return result;
    }

    private CalculatorType getCalculatorType(Map<String, Object> calcMap) {
        CalculatorType result = null;
        if (calcMap != null) {
            result = new CalculatorType();
            result.setCurrentVersionName(getStringParam(calcMap.get("CURVERNAME")));
            result.setName(getStringParam(calcMap.get("NAME")));
            result.setNote(getStringParam(calcMap.get("NOTE")));
            result.setCalculatorVersions(getCalculatorVersionsType((List<Map<String, Object>>) calcMap.get("VERSIONLIST")));
        }
        return result;
    }

    private B2BProductBinaryDocumentsType getB2BProductBinaryDocumentsType(List<Map<String, Object>> list) {
        B2BProductBinaryDocumentsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductBinaryDocumentsType();
            for (Map<String, Object> bean : list) {
                B2BProductBinaryDocumentType binDoc = new B2BProductBinaryDocumentType();
                binDoc.setBinDocType(getIntegerParam(bean.get("BINDOCTYPE")));
                binDoc.setCheckName(getStringParam(bean.get("CHECKNAME")));
                binDoc.setDocumentLevel(getIntegerParam(bean.get("DOCLEVEL")));
                binDoc.setDocumentLevelNote(getStringParam(bean.get("DOCLEVELNOTE")));
                binDoc.setDocumentSysName(getStringParam(bean.get("DOCSYSNAME")));
                binDoc.setName(getStringParam(bean.get("NAME")));
                binDoc.setNote(getStringParam(bean.get("NOTE")));
                binDoc.setRequired(getBooleanParam(bean.get("REQUIRED"), Boolean.FALSE));
                result.getB2BProductBinaryDocuments().add(binDoc);
            }
        }
        return result;
    }

    private B2BProductReportsType getB2BProductReportsType(List<Map<String, Object>> list) {
        B2BProductReportsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductReportsType();
            for (Map<String, Object> bean : list) {
                B2BProductReportType prodReport = new B2BProductReportType();
                prodReport.setCategoryId(getIntegerParam(bean.get("CATEGORYID")));
                prodReport.setCopyNumber(getIntegerParam(bean.get("CPNUM")));
                prodReport.setDataProvider(null); // !!!!!
                prodReport.setEDoc(getIntegerParam(bean.get("EDOC")));
                prodReport.setName(getStringParam(bean.get("NAME")));
                prodReport.setNote(getStringParam(bean.get("NOTE")));
                prodReport.setOriginalPrinting(getIntegerParam(bean.get("ORIGPRINTING")));
                prodReport.setPosition(getIntegerParam(bean.get("POSTN")));
                prodReport.setPrePrinting(getIntegerParam(bean.get("PREPRINTING")));
                prodReport.setReportCheckId(getIntegerParam(bean.get("CHECKID")));
                B2BReportType report = new B2BReportType();
                report.setName(getStringParam(bean.get("REPNAME")));
                report.setTemplateName(getStringParam(bean.get("TEMPLATENAME")));
                prodReport.setReport(report);
                prodReport.setReportLevel(getIntegerParam(bean.get("REPLEVEL")));
                prodReport.setReportType(getIntegerParam(bean.get("REPTYPE")));
                result.getB2BProductReports().add(prodReport);
            }
        }
        return result;
    }

    private B2BProductDefaultValuesType getB2BProductDefaultValuesType(List<Map<String, Object>> list) {
        B2BProductDefaultValuesType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductDefaultValuesType();
            for (Map<String, Object> bean : list) {
                B2BProductDefaultValueType defValue = new B2BProductDefaultValueType();
                defValue.setDefType(getStringParam(bean.get("DEFTYPE")));
                defValue.setName(getStringParam(bean.get("NAME")));
                defValue.setNote(getStringParam(bean.get("NOTE")));
                defValue.setValue(getStringParam(bean.get("VALUE")));
                result.getB2BProductDefaultValues().add(defValue);
            }
        }
        return result;
    }

    private B2BProductNumMethodsType getB2BProductNumMethodsType(List<Map<String, Object>> list) {
        B2BProductNumMethodsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductNumMethodsType();
            for (Map<String, Object> bean : list) {
                B2BProductNumMethodType numMethod = new B2BProductNumMethodType();
                numMethod.setMethodId(getIntegerParam(bean.get("METHODID")));
                result.getB2BProductNumMethods().add(numMethod);
            }
        }
        return result;
    }

    private B2BProductInsAmCurrenciesType getB2BProductInsAmCurrenciesType(List<Map<String, Object>> list) {
        B2BProductInsAmCurrenciesType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductInsAmCurrenciesType();
            for (Map<String, Object> bean : list) {
                B2BProductInsAmCurrencyType amCurrency = new B2BProductInsAmCurrencyType();
                amCurrency.setCurrencyId(getIntegerParam(bean.get("CURRENCYID")));
                result.getB2BProductInsAmCurrencies().add(amCurrency);
            }
        }
        return result;
    }

    private B2BProductPremiumCurrenciesType getB2BProductPremiumCurrenciesType(List<Map<String, Object>> list) {
        B2BProductPremiumCurrenciesType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductPremiumCurrenciesType();
            for (Map<String, Object> bean : list) {
                B2BProductPremiumCurrencyType pCurrency = new B2BProductPremiumCurrencyType();
                pCurrency.setCurrencyId(getIntegerParam(bean.get("CURRENCYID")));
                result.getB2BProductPremiumCurrencies().add(pCurrency);
            }
        }
        return result;
    }

    private B2BProductCalcRateRulesType getB2BProductCalcRateRulesType(List<Map<String, Object>> list) throws Exception {
        B2BProductCalcRateRulesType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductCalcRateRulesType();
            for (Map<String, Object> bean : list) {
                B2BProductCalcRateRuleType rule = new B2BProductCalcRateRuleType();
                rule.setCalcVariantId(getIntegerParam(bean.get("CALCVARIANTID")));
                rule.setCurrencyId(getIntegerParam(bean.get("CURRENCYID")));
                rule.setPercent(getBigDecimalParamNoScale(bean.get("PERCENT")));
                rule.setRateDate(getXMLGCDateParam(bean.get("RATEDATE")));
                rule.setRateValue(getBigDecimalParamNoScale(bean.get("RATEVALUE")));
                rule.setRuleDate(getXMLGCDateParam(bean.get("RULEDATE")));
                result.getB2BProductCalcRateRules().add(rule);
            }
        }
        return result;
    }

    private B2BProductFormsType getB2BProductFormsType(List<Map<String, Object>> list) {
        B2BProductFormsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductFormsType();
            for (Map<String, Object> bean : list) {
                B2BProductFormType form = new B2BProductFormType();
                form.setFormTypeId(getIntegerParam(bean.get("FORMTYPEID")));
                form.setIndex(getIntegerParam(bean.get("FORMINDEX")));
                form.setName(getStringParam(bean.get("NAME")));
                form.setPage(getIntegerParam(bean.get("PAGE")));
                result.getB2BProductForms().add(form);
            }
        }
        return result;
    }

    private B2BProductDiscountPromoCodesType getB2BProductDiscountPromoCodesType(List<Map<String, Object>> list) {
        B2BProductDiscountPromoCodesType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductDiscountPromoCodesType();
            for (Map<String, Object> bean : list) {
                B2BProductDiscountPromoCodeType promoCode = new B2BProductDiscountPromoCodeType();
                promoCode.setCode(getStringParam(bean.get("CODE")));
                promoCode.setPromoCount(getIntegerParam(bean.get("PROMOCOUNT")));
                result.getB2BProductDiscountPromoCodes().add(promoCode);
            }
        }
        return result;
    }

    private B2BProductDiscountValuesType getB2BProductDiscountValuesType(List<Map<String, Object>> list) {
        B2BProductDiscountValuesType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductDiscountValuesType();
            for (Map<String, Object> bean : list) {
                B2BProductDiscountValueType discount = new B2BProductDiscountValueType();
                discount.setDiscountValue(getBigDecimalParamNoScale(bean.get("DISCOUNTVALUE")));
                discount.setProductStructureSysName(null); // !!!!!!!!!
                result.getB2BProductDiscountValues().add(discount);
            }
        }
        return result;
    }

    private B2BProductDiscountsType getB2BProductDiscountsType(List<Map<String, Object>> list) throws Exception {
        B2BProductDiscountsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductDiscountsType();
            for (Map<String, Object> bean : list) {
                B2BProductDiscountType discount = new B2BProductDiscountType();
                discount.setDiscountKindId(getIntegerParam(bean.get("DISCKINDID")));
                discount.setFinishDate(getXMLGCDateParam(bean.get("FINISHDATE")));
                discount.setIsPremium(getIntegerParam(bean.get("ISPREMIUM")));
                discount.setName(getStringParam(bean.get("NAME")));
                discount.setPremiumURL(getStringParam(bean.get("PREMIUMURL")));
                discount.setStartDate(getXMLGCDateParam(bean.get("STARTDATE")));
                discount.setB2BProductDiscountPromoCodes(getB2BProductDiscountPromoCodesType((List<Map<String, Object>>) bean.get("PRODDISCPROMOLIST")));
                discount.setB2BProductDiscountValues(getB2BProductDiscountValuesType((List<Map<String, Object>>) bean.get("PRODDISCVALLIST")));
                result.getB2BProductDiscounts().add(discount);
            }
        }
        return result;
    }

    private B2BProductPossibleValuesType getB2BProductPossibleValuesType(List<Map<String, Object>> list) {
        B2BProductPossibleValuesType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductPossibleValuesType();
            for (Map<String, Object> bean : list) {
                B2BProductPossibleValueType pValue = new B2BProductPossibleValueType();
                pValue.setHint(getStringParam(bean.get("HINT")));
                pValue.setIsDefault(getIntegerParam(bean.get("ISDEFAULT")));
                pValue.setName(getStringParam(bean.get("NAME")));
                pValue.setValue2Double(getBigDecimalParamNoScale(bean.get("VALUE2DOUBLE")));
                pValue.setValue2Long(getIntegerParam(bean.get("VALUE2LONG")));
                pValue.setValue2String(getStringParam(bean.get("VALUE2STRING")));
                pValue.setValueDouble(getBigDecimalParamNoScale(bean.get("VALUEDOUBLE")));
                pValue.setValueLong(getIntegerParam(bean.get("VALUELONG")));
                pValue.setValueString(getStringParam(bean.get("VALUESTRING")));
                result.getB2BProductPossibleValues().add(pValue);
            }
        }
        return result;
    }

    private B2BProductValueType getB2BProductValueType(Map<String, Object> valueBean) {
        B2BProductValueType value = new B2BProductValueType();
        value.setAddAgrCauseId(getIntegerParam(valueBean.get("ADDAGRCAUSEID")));
        value.setDataTypeId(getIntegerParam(valueBean.get("DATATYPEID")));
        value.setDataTypeStr(getStringParam(valueBean.get("DATATYPESTR")));
        value.setDescription(getStringParam(valueBean.get("DESCR")));
        value.setDiscriminator(getStringParam(valueBean.get("DISCRIMINATOR")));
        value.setFormula(getStringParam(valueBean.get("FORMULA")));
        value.setIsHandbook(getIntegerParam(valueBean.get("ISHANDBOOK")));
        value.setKindId(getIntegerParam(valueBean.get("KINDID")));
        value.setName(getStringParam(valueBean.get("NAME")));
        value.setValueGroupId(getIntegerParam(valueBean.get("VALGROUPID")));
        value.setB2BProductPossibleValues(getB2BProductPossibleValuesType((List<Map<String, Object>>) valueBean.get("PRODPOSSVALUELIST")));
        return value;
    }

    private B2BProductValuesType getB2BProductValuesForConfigType(Map<String, Object> configBean) {
        B2BProductValuesType result = null;
        if ((configBean.get("PRODVALUELIST") != null) && (((List<Map<String, Object>>) configBean.get("PRODVALUELIST")).size() > 0)) {
            result = new B2BProductValuesType();
            List<Map<String, Object>> productValuesList = (List<Map<String, Object>>) configBean.get("PRODVALUELIST");
            for (Map<String, Object> bean : productValuesList) {
                if ((bean.get("PRODCONFID").equals(configBean.get("PRODCONFID"))) && (bean.get("DISCRIMINATOR").toString().equalsIgnoreCase("1"))) {
                    result.getB2BProductValues().add(getB2BProductValueType(bean));
                }
            }
            if (result.getB2BProductValues().isEmpty()) {
                result = null;
            }
        }
        return result;
    }

    private B2BProductValuesType getB2BProductValuesForStructureType(Map<String, Object> structureBean) {
        B2BProductValuesType result = null;
        if ((structureBean.get("PRODCONFLIST") != null) && (((List<Map<String, Object>>) structureBean.get("PRODCONFLIST")).size() > 0)) {
            List<Map<String, Object>> productConfigsList = (List<Map<String, Object>>) structureBean.get("PRODCONFLIST");
            // у нас только одна конфигурация на версию
            Map<String, Object> prodConfMap = productConfigsList.get(0);
            //
            if ((prodConfMap.get("PRODVALUELIST") != null) && (((List<Map<String, Object>>) prodConfMap.get("PRODVALUELIST")).size() > 0)) {
                result = new B2BProductValuesType();
                List<Map<String, Object>> productValuesList = (List<Map<String, Object>>) prodConfMap.get("PRODVALUELIST");
                for (Map<String, Object> bean : productValuesList) {
                    if ((bean.get("PRODSTRUCTID").equals(structureBean.get("PRODSTRUCTID"))) && (bean.get("DISCRIMINATOR").toString().equalsIgnoreCase("2"))) {
                        result.getB2BProductValues().add(getB2BProductValueType(bean));
                    }
                }
                if (result.getB2BProductValues().isEmpty()) {
                    result = null;
                }
            }
        }
        return result;
    }

    private B2BProductStructuresType getB2BProductStructuresType(List<Map<String, Object>> list) {
        B2BProductStructuresType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductStructuresType();
            for (Map<String, Object> bean : list) {
                B2BProductStructureType structure = new B2BProductStructureType();
                structure.setChildProdVerId(getIntegerParam(bean.get("CHILDPRODVERID")));
                structure.setDiscriminator(getIntegerParam(bean.get("DISCRIMINATOR")));
                structure.setExternalCode(getStringParam(bean.get("EXTERNALCODE")));
                structure.setValuesHB(getHandbookDescriptorType((Map<String, Object>) bean.get("VALUESHB")));
                structure.setHint(getStringParam(bean.get("HINT")));
                structure.setInsuranceKindId(getIntegerParam(bean.get("INSKINDID")));
                structure.setName(getStringParam(bean.get("NAME")));
                structure.setParentStructureSysName(getStringParam(bean.get("PARENTSTRUCTSYSNAME")));
                structure.setRepeated(getIntegerParam(bean.get("REPEATED")));
                structure.setRequired(getIntegerParam(bean.get("REQUIRED")));
                structure.setStartDateCalcMethod(getIntegerParam(bean.get("SDCALCMETHOD")));
                structure.setStartDateLag(getIntegerParam(bean.get("SDLAG")));
                structure.setSysName(getStringParam(bean.get("SYSNAME")));
                structure.setB2BProductValues(getB2BProductValuesForStructureType(bean));
                result.getB2BProductStructures().add(structure);
            }
        }
        return result;
    }

    private B2BProductSalesChannelsType getB2BProductSalesChannelsType(List<Map<String, Object>> list) {
        B2BProductSalesChannelsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductSalesChannelsType();
            for (Map<String, Object> bean : list) {
                B2BProductSalesChannelType b2bSChannel = new B2BProductSalesChannelType();
                SaleChannelType sChannel = new SaleChannelType();
                sChannel.setExternalId(getStringParam(bean.get("SALECHANNELEXTERNALID")));
                sChannel.setName(getStringParam(bean.get("SALECHANNELNAME")));
                sChannel.setNote(getStringParam(bean.get("SALECHANNELNOTE")));
                sChannel.setSaleChannelCode(getStringParam(bean.get("SALECHANNELCODE")));
                b2bSChannel.setSaleChannel(sChannel);
                result.getB2BProductSalesChannels().add(b2bSChannel);
            }
        }
        return result;
    }

    private B2BProductPaymentVariantsType getB2BProductPaymentVariantsType(List<Map<String, Object>> list) {
        B2BProductPaymentVariantsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductPaymentVariantsType();
            for (Map<String, Object> bean : list) {
                B2BProductPaymentVariantType prodPayVar = new B2BProductPaymentVariantType();
                prodPayVar.setName(getStringParam(bean.get("NAME")));
                PaymentVariantType payVar = new PaymentVariantType();
                payVar.setCalculatorVersionId(getIntegerParam(bean.get("PAYVARCALCVERID")));
                payVar.setDescription(getStringParam(bean.get("PAYVARDESCR")));
                payVar.setName(getStringParam(bean.get("PAYVARNAME")));
                prodPayVar.setPaymentVariant(payVar);
                result.getB2BProductPaymentVariants().add(prodPayVar);
            }
        }
        return result;
    }

    private B2BProductProgramsType getB2BProductProgramsType(List<Map<String, Object>> list) throws Exception {
        B2BProductProgramsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new B2BProductProgramsType();
            for (Map<String, Object> bean : list) {
                B2BProductProgramType program = new B2BProductProgramType();
                program.setExploitationFinishDate(getXMLGCDateParam(bean.get("EXPLFINISHDATE")));
                program.setExploitationStartDate(getXMLGCDateParam(bean.get("EXPLSTARTDATE")));
                program.setExternalId(getStringParam(bean.get("EXTERNALID")));
                program.setInsAmountValue(getBigDecimalParam(bean.get("INSAMVALUE")));
                program.setIsUseTCondition(getIntegerParam(bean.get("ISUSETCOND")));
                program.setName(getStringParam(bean.get("NAME")));
                program.setNote(getStringParam(bean.get("NOTE")));
                program.setPremiumValue(getBigDecimalParam(bean.get("PREMVALUE")));
                program.setProductCode(getStringParam(bean.get("PRODCODE")));
                program.setProductRuleId(getIntegerParam(bean.get("PRODRULEID")));
                program.setProgramCode(getStringParam(bean.get("PROGCODE")));
                program.setSysName(getStringParam(bean.get("SYSNAME")));
                result.getB2BProductPrograms().add(program);
            }
        }
        return result;
    }

    private HandbookDataVersionsType getHandbookDataVersionsType(List<Map<String, Object>> list, String pkFieldName) {
        HandbookDataVersionsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new HandbookDataVersionsType();
            for (Map<String, Object> bean : list) {
                HandbookDataVersionType version = new HandbookDataVersionType();
                version.setName(getStringParam(bean.get("NAME")));
                version.setNote(getStringParam(bean.get("NOTE")));
                version.setHandbookStoreData(getHandbookStoreDataType((List<Map<String, Object>>) bean.get("HBDATALIST"), pkFieldName));
                result.getHandbookDataVersions().add(version);
            }
        }
        return result;
    }

    private HandbookStoreDataType getHandbookStoreDataType(List<Map<String, Object>> list, String pkFieldName) {
        HandbookStoreDataType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new HandbookStoreDataType();
            for (Map<String, Object> rowBean : list) {
                HandbookStoreRowValueType row = new HandbookStoreRowValueType();
                for (Map.Entry<String, Object> entry : rowBean.entrySet()) {
                    // первичный ключ не должен попадать в выгрузку данных
                    if (!entry.getKey().equalsIgnoreCase(pkFieldName)) {
                        HandbookStoreFieldValueType field = new HandbookStoreFieldValueType();
                        field.setPropertyName(entry.getKey());
                        field.setValue(entry.getValue().toString());
                        row.getHandbookStoreFieldValues().add(field);
                    }
                }
                result.getHandbookStoreRowValues().add(row);
            }
        }
        return result;
    }

    private HandbookPropertyDescriptorsType getHandbookPropertyDescriptorsType(List<Map<String, Object>> list) {
        HandbookPropertyDescriptorsType result = null;
        if ((list != null) && (list.size() > 0)) {
            result = new HandbookPropertyDescriptorsType();
            for (Map<String, Object> bean : list) {
                HandbookPropertyDescriptorType descriptor = new HandbookPropertyDescriptorType();
                descriptor.setCaption(getStringParam(bean.get("CAPTION")));
                descriptor.setIsPK(getBooleanParam(bean.get("ISPK"), Boolean.FALSE));
                descriptor.setName(getStringParam(bean.get("NAME")));
                descriptor.setNote(getStringParam(bean.get("NOTE")));
                descriptor.setPropJavaType(getStringParam(bean.get("PROPJAVATYPE")));
                descriptor.setStorePropName(getStringParam(bean.get("STOREPROPNAME")));
                result.getHandbookPropertyDescriptors().add(descriptor);
            }
        }
        return result;
    }

    private HandbookDescriptorType getHandbookDescriptorType(Map<String, Object> hbMap) {
        HandbookDescriptorType result = null;
        if (hbMap != null) {
            result = new HandbookDescriptorType();
            result.setIsMultiHandbookTable(getBooleanParam(hbMap.get("ISMULTIHBT"), Boolean.TRUE));
            result.setName(getStringParam(hbMap.get("NAME")));
            result.setNote(getStringParam(hbMap.get("NOTE")));
            result.setTableName(getStringParam(hbMap.get("TABLENAME")));
            result.setHandbookPropertyDescriptors(getHandbookPropertyDescriptorsType((List<Map<String, Object>>) hbMap.get("HBPROPDESCRLIST")));
            if (result.getHandbookPropertyDescriptors() != null) {
                // определяем поле, являющееся в справочнике первичным ключем, его нам не нужно выгружать в данных
                String pkFieldName = "";
                for (HandbookPropertyDescriptorType bean : result.getHandbookPropertyDescriptors().getHandbookPropertyDescriptors()) {
                    if (bean.getIsPK().booleanValue()) {
                        pkFieldName = bean.getName();
                        break;
                    }
                }
                result.setHandbookDataVersions(getHandbookDataVersionsType((List<Map<String, Object>>) hbMap.get("HBDATAVERLIST"), pkFieldName));
            }
        }
        return result;
    }

    private B2BProductType getB2BProductType(Map<String, Object> productMap) throws Exception {
        B2BProductType result = new B2BProductType();
        result.setAssurerId(getIntegerParam(productMap.get("ASSURERID")));
        result.setExternalCode(getStringParam(productMap.get("EXTERNALCODE")));
        result.setExternalId(getStringParam(productMap.get("EXTERNALID")));
        result.setInsTermKindId(getIntegerParam(productMap.get("INSTERMKINDID")));
        result.setIsHidden(getBooleanParam(productMap.get("ISHIDDEN"), Boolean.FALSE));
        result.setModelId(getIntegerParam(productMap.get("MODELID")));
        result.setName(getStringParam(productMap.get("NAME")));
        result.setNote(getStringParam(productMap.get("NOTE")));
        result.setProductKindId(getIntegerParam(productMap.get("PRODKINDID")));
        result.setStructRootSysName(null); // !!!!!!!
        result.setSysName(getStringParam(productMap.get("SYSNAME")));
        result.setB2BProductRiders(getB2BProductRidersType(productMap));
        result.setB2BProductVersions(getB2BProductVersionsType(productMap));
        return result;
    }

    @WsMethod(requiredParams = {"PRODLIST"})
    public Map<String, Object> dsB2BProductExportToXML(Map<String, Object> params) throws Exception {
        List<Map<String, Object>> productList = (List<Map<String, Object>>) params.get("PRODLIST");
        B2BProducts pList = new B2BProducts();
        if ((productList != null) && (productList.size() > 0)) {
            for (Map<String, Object> bean : productList) {
                B2BProductType productType = getB2BProductType(bean);
                pList.getB2BProducts().add(productType);
            }
        }
        String productXML = this.marshall(pList);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("OUTPUTXML", productXML);
        return result;
    }

    @WsMethod(requiredParams = {"PRODLIST", "XMLFILENAME"})
    public Map<String, Object> dsB2BProductExportToXMLFile(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> callParams = new HashMap<String, Object>();
        callParams.putAll(params);
        callParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> exportRes = this.callService(Constants.B2BPOSWS, "dsB2BProductExportToXML", callParams, login, password);
        Map<String, Object> result = new HashMap<String, Object>();
        if (exportRes.get("OUTPUTXML") != null) {
            FileOutputStream fop = null;
            try {
                String xmlFileName = params.get("XMLFILENAME").toString();
                File file = new File(xmlFileName);
                fop = new FileOutputStream(file);
                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }
                // get the content in bytes
                byte[] contentInBytes = exportRes.get("OUTPUTXML").toString().getBytes("UTF-8");
                fop.write(contentInBytes);
                fop.flush();
                fop.close();
            } catch (IOException e) {
                result.put("Error", e.toString());
            } finally {
                try {
                    if (fop != null) {
                        fop.close();
                    }
                } catch (IOException e) {
                    result.put("Error", e.toString());
                }
            }
        }
        String fileName = params.get("UPLOADFILENAME").toString();
        callParams.clear();
        callParams.put("INPUTSTR", fileName + "@" + fileName);
        callParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> encriptRes = this.callService("bivsberposws", "dsEncriptString", callParams, login, password);
        result.put("ENCRIPTSTRING", getStringParam(encriptRes.get("OUTPUTSTR")));
        return result;
    }

    private List<Map<String, Object>> getHandbookStoreDataList(HandbookStoreDataType list) {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getHandbookStoreRowValues().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (HandbookStoreRowValueType bean : list.getHandbookStoreRowValues()) {
                if ((bean.getHandbookStoreFieldValues() != null) && (bean.getHandbookStoreFieldValues().size() > 0)) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    for (HandbookStoreFieldValueType fieldBean : bean.getHandbookStoreFieldValues()) {
                        map.put(fieldBean.getPropertyName(), fieldBean.getValue());
                    }
                    result.add(map);
                }
            }
        }
        return result;
    }

    private List<Map<String, Object>> getHandbookDataVersionsList(HandbookDataVersionsType list, boolean generateVersionId, Map<String, Object> mapToSaveVerId, String saveFieldName) throws Exception {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getHandbookDataVersions().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (HandbookDataVersionType bean : list.getHandbookDataVersions()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("NAME", bean.getName());
                map.put("NOTE", bean.getNote());
                map.put("HBDATALIST", getHandbookStoreDataList(bean.getHandbookStoreData()));
                if (generateVersionId) {
                    map.put("HBDATAVERID", Long.valueOf(this.getNewId("INS_HBDATAVER").longValue()));
                    if ((mapToSaveVerId != null) && (saveFieldName != null)) {
                        mapToSaveVerId.put(saveFieldName, map.get("HBDATAVERID"));
                    }
                }
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getHandbookPropertyDescriptorList(HandbookPropertyDescriptorsType list) {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getHandbookPropertyDescriptors().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (HandbookPropertyDescriptorType bean : list.getHandbookPropertyDescriptors()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("CAPTION", bean.getCaption());
                map.put("ISPK", bean.getIsPK());
                map.put("NAME", bean.getName());
                map.put("NOTE", bean.getNote());
                map.put("PROPJAVATYPE", bean.getPropJavaType());
                map.put("STOREPROPNAME", bean.getStorePropName());
                result.add(map);
            }
        }
        return result;
    }

    private Map<String, Object> getHandbookDescriptorMap(HandbookDescriptorType handbook, boolean generateVersionId, Map<String, Object> mapToSaveVerId, String saveFieldName) throws Exception {
        Map<String, Object> result = null;
        if (handbook != null) {
            result = new HashMap<String, Object>();
            result.put("ISMULTIHBT", handbook.getIsMultiHandbookTable());
            result.put("NAME", handbook.getName());
            result.put("NOTE", handbook.getNote());
            result.put("TABLENAME", handbook.getTableName());
            result.put("HBDATAVERLIST", getHandbookDataVersionsList(handbook.getHandbookDataVersions(), generateVersionId, mapToSaveVerId, saveFieldName));
            result.put("HBPROPDESCRLIST", getHandbookPropertyDescriptorList(handbook.getHandbookPropertyDescriptors()));
        }
        return result;
    }

    private List<Map<String, Object>> getCalculatorFormulasList(CalculatorFormulasType list) {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getCalculatorFormulas().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (CalculatorFormulaType bean : list.getCalculatorFormulas()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("FORMULA", bean.getFormula());
                map.put("ISCACHED", bean.getIsCached());
                map.put("ISSTORED", bean.getIsStored());
                map.put("NAME", bean.getName());
                map.put("NOTE", bean.getNote());
                map.put("RETURNJAVATYPE", bean.getReturnJavaType());
                map.put("RETPARAMNAME", bean.getReturnParamName());
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getCalculatorConstsList(CalculatorConstsType list) {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getCalculatorConstTypes().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (CalculatorConstType bean : list.getCalculatorConstTypes()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("CONSTJAVATYPE", bean.getConstJavaType());
                map.put("DOUBLEVALUE", bigDecimalToDouble(bean.getDoubleValue()));
                map.put("ISSTORED", bean.getIsStored());
                map.put("LONGVALUE", bean.getLongValue());
                map.put("NAME", bean.getName());
                map.put("NOTE", bean.getNote());
                map.put("STRINGVALUE", bean.getStringValue());
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getCalculatorInputParamsList(CalculatorInputParamsType list) {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getCalculatorInputParams().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (CalculatorInputParamType bean : list.getCalculatorInputParams()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("DBGDOUBLEVALUE", bigDecimalToDouble(bean.getDbgDoubleValue()));
                map.put("DBGLONGVALUE", bean.getDbgLongValue());
                map.put("DBGSTRINGVALUE", bean.getDbgStringValue());
                map.put("IPJAVATYPE", bean.getIpJavaType());
                map.put("NAME", bean.getName());
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getCalculatorHandbooksList(CalculatorHandbooksType list) throws Exception {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getCalculatorHandbooks().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (CalculatorHandbookType bean : list.getCalculatorHandbooks()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("HBMAP", getHandbookDescriptorMap(bean.getHandbook(), true, map, "HBDATAVERID"));
                map.put("NOTE", bean.getNote());
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getCalculatorVersionsList(CalculatorVersionsType list, boolean generateVersionId, Map<String, Object> mapToSaveVerId, String saveFieldName) throws Exception {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getCalculatorVersions().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (CalculatorVersionType bean : list.getCalculatorVersions()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("NAME", bean.getName());
                map.put("NOTE", bean.getNote());
                map.put("FORMULALIST", getCalculatorFormulasList(bean.getCalculatorFormulas()));
                map.put("CONSTLIST", getCalculatorConstsList(bean.getCalculatorConsts()));
                map.put("INPUTPARAMLIST", getCalculatorInputParamsList(bean.getCalculatorInputParams()));
                map.put("HANDBOOKLIST", getCalculatorHandbooksList(bean.getCalculatorHandbooks()));
                if (generateVersionId) {
                    map.put("CALCVERID", Long.valueOf(this.getNewId("INS_CALCVER").longValue()));
                    if ((mapToSaveVerId != null) && (saveFieldName != null)) {
                        mapToSaveVerId.put(saveFieldName, map.get("CALCVERID"));
                    }
                }
                result.add(map);
            }
        }
        return result;
    }

    private Map<String, Object> getCalculatorMap(CalculatorType calculator, boolean generateVersionId, Map<String, Object> mapToSaveVerId, String saveFieldName) throws Exception {
        Map<String, Object> result = null;
        if (calculator != null) {
            result = new HashMap<String, Object>();
            result.put("NAME", calculator.getName());
            result.put("NOTE", calculator.getNote());
            result.put("VERSIONLIST", getCalculatorVersionsList(calculator.getCalculatorVersions(), generateVersionId, mapToSaveVerId, saveFieldName));
            // определяем текущую версию калькулятора и устанавливаем ее ИД
            List<Map<String, Object>> versionList = (List<Map<String, Object>>) result.get("VERSIONLIST");
            if ((versionList != null) && (versionList.size() > 0)) {
                for (Map<String, Object> bean : versionList) {
                    if (bean.get("NAME").toString().equalsIgnoreCase(calculator.getCurrentVersionName())) {
                        result.put("CURVERID", bean.get("CALCVERID"));
                        break;
                    }
                }
            }
        }
        return result;
    }

    private List<Map<String, Object>> getB2BProductConfigsList(B2BProductConfigsType list) throws Exception {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getB2BProductConfigs().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (B2BProductConfigType bean : list.getB2BProductConfigs()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("CALCMAP", getCalculatorMap(bean.getCalculator(), true, map, "CALCVERID"));
                map.put("VALUESHB", getHandbookDescriptorMap(bean.getValuesHB(), true, map, "HBDATAVERID"));
                map.put("NAME", bean.getName());
                map.put("NOTE", bean.getNote());
                map.put("SYSNAME", bean.getSysName());
                //map.put("", ); // config.setB2BProductValues(getB2BProductValuesForConfigType(bean));
                //map.put("", ); // config.setB2BProductDiscounts(getB2BProductDiscountsType((List<Map<String, Object>>) bean.get("PRODDISCLIST")));
                //map.put("", ); // config.setB2BProductForms(getB2BProductFormsType((List<Map<String, Object>>) bean.get("PRODFORMLIST")));
                //map.put("", ); // config.setB2BProductCalcRateRules(getB2BProductCalcRateRulesType((List<Map<String, Object>>) bean.get("PRODCALCRATERULELIST")));
                //map.put("", ); // config.setB2BProductPremiumCurrencies(getB2BProductPremiumCurrenciesType((List<Map<String, Object>>) bean.get("PRODPREMCURLIST")));
                //map.put("", ); // config.setB2BProductInsAmCurrencies(getB2BProductInsAmCurrenciesType((List<Map<String, Object>>) bean.get("PRODINSAMCURLIST")));
                //map.put("", ); // config.setB2BProductNumMethods(getB2BProductNumMethodsType((List<Map<String, Object>>) bean.get("PRODNUMMETHODLIST")));
                //map.put("", ); // config.setB2BProductDefaultValues(getB2BProductDefaultValuesType((List<Map<String, Object>>) bean.get("PRODDEFVALLIST")));
                //map.put("", ); // config.setB2BProductReports(getB2BProductReportsType((List<Map<String, Object>>) bean.get("PRODREPLIST")));
                //map.put("", ); // config.setB2BProductBinaryDocuments(getB2BProductBinaryDocumentsType((List<Map<String, Object>>) bean.get("PRODBINDOC")));
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getB2BProductStructuresList(B2BProductStructuresType list) {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getB2BProductStructures().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (B2BProductStructureType bean : list.getB2BProductStructures()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("CHILDPRODVERID", bean.getChildProdVerId());
                map.put("DISCRIMINATOR", bean.getDiscriminator());
                map.put("EXTERNALCODE", bean.getExternalCode());
                //map.put("", ); // structure.setValuesHB(getHandbookDescriptorType((Map<String, Object>) bean.get("VALUESHB")));
                map.put("HINT", bean.getHint());
                map.put("INSKINDID", bean.getInsuranceKindId());
                map.put("NAME", bean.getName());
                //map.put("", ); // structure.setParentStructureSysName(null); // !!!!!!!
                map.put("REPEATED", bean.getRepeated());
                map.put("REQUIRED", bean.getRequired());
                map.put("SDCALCMETHOD", bean.getStartDateCalcMethod());
                map.put("SDLAG", bean.getStartDateLag());
                map.put("SYSNAME", bean.getSysName());
                //map.put("", ); // structure.setB2BProductValues(getB2BProductValuesForStructureType(bean));
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getB2BProductSalesChannelsList(B2BProductSalesChannelsType list) {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getB2BProductSalesChannels().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (B2BProductSalesChannelType bean : list.getB2BProductSalesChannels()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("SALECHANNELID", 1L);  //!!!!!!  тут надо проставлять saleschannelid, и возножно создавать сам канал, если его нет в БД
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getB2BProductPaymentVariantsList(B2BProductPaymentVariantsType list) {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getB2BProductPaymentVariants().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (B2BProductPaymentVariantType bean : list.getB2BProductPaymentVariants()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("NAME", bean.getName());
                map.put("PAYVARID", 1L); //!!!!!! проставляем ИД оплаты из системы (или новую создаем оплату)
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getB2BProductProgramsList(B2BProductProgramsType list) {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getB2BProductPrograms().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (B2BProductProgramType bean : list.getB2BProductPrograms()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("EXPLFINISHDATE", xmlGCToDate(bean.getExploitationFinishDate()));
                map.put("EXPLSTARTDATE", xmlGCToDate(bean.getExploitationStartDate()));
                map.put("EXTERNALID", bean.getExternalId());
                map.put("INSAMVALUE", bigDecimalToDouble(bean.getInsAmountValue()));
                map.put("ISUSETCOND", bean.getIsUseTCondition());
                map.put("NAME", bean.getName());
                map.put("NOTE", bean.getNote());
                map.put("PREMVALUE", bigDecimalToDouble(bean.getPremiumValue()));
                map.put("PRODCODE", bean.getProductCode());
                map.put("PRODRULEID", bean.getProductRuleId());
                map.put("PROGCODE", bean.getProgramCode());
                map.put("SYSNAME", bean.getSysName());
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getB2BProductVersionsList(B2BProductVersionsType list) throws Exception {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getB2BProductVersions().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (B2BProductVersionType bean : list.getB2BProductVersions()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("EXPLFINISHDATE", xmlGCToDate(bean.getExploitationFinishDate()));
                map.put("EXPLSTARTDATE", xmlGCToDate(bean.getExploitationStartDate()));
                map.put("IMGPATH", bean.getImagePath());
                map.put("JSPATH", bean.getJavaScriptPath());
                map.put("LOGOTYPE", bean.getLogotype());
                map.put("NAME", bean.getName());
                map.put("NOTE", bean.getNote());
                map.put("PRODCODE", bean.getProductCode());
                map.put("STATEID", bean.getStateId());
                map.put("PRODCONFLIST", getB2BProductConfigsList(bean.getB2BProductConfigs()));
                map.put("PRODSTRUCTLIST", getB2BProductStructuresList(bean.getB2BProductStructures()));
                map.put("PRODSALESCHANLIST", getB2BProductSalesChannelsList(bean.getB2BProductSalesChannels()));
                map.put("PRODPAYVARLIST", getB2BProductPaymentVariantsList(bean.getB2BProductPaymentVariants()));
                map.put("PRODPROGLIST", getB2BProductProgramsList(bean.getB2BProductPrograms()));
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getB2BProductRidersList(B2BProductRidersType list) {
        List<Map<String, Object>> result = null;
        if ((list != null) && (list.getB2BProductRiders().size() > 0)) {
            result = new ArrayList<Map<String, Object>>();
            for (B2BProductRiderType bean : list.getB2BProductRiders()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("PRODRIDERID", bean.getId()); // !! временно, ид не нужно передавать
                result.add(map);
            }
        }
        return result;
    }

    private Map<String, Object> getB2BProductMap(B2BProductType product) throws Exception {
        Map<String, Object> result = null;
        if (product != null) {
            result = new HashMap<String, Object>();
            result.put("ASSURERID", product.getAssurerId());
            result.put("EXTERNALCODE", product.getExternalCode());
            result.put("EXTERNALID", product.getExternalId());
            result.put("INSTERMKINDID", product.getInsTermKindId());
            result.put("ISHIDDEN", product.getIsHidden());
            result.put("MODELID", product.getModelId());
            result.put("NAME", product.getName());
            result.put("NOTE", product.getNote());
            result.put("PRODKINDID", product.getProductKindId());
            result.put("STRUCTROOTID", null); // !!!!!!!
            result.put("SYSNAME", product.getSysName());
            result.put("PRODRIDERLIST", getB2BProductRidersList(product.getB2BProductRiders()));
            result.put("PRODVERLIST", getB2BProductVersionsList(product.getB2BProductVersions()));
        }
        return result;
    }

    private String getUploadFilePath() {
        String result = Config.getConfig(SERVICE_NAME).getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();
        return result;
    }
    
    /*
     Импорт продукта с возможностью мерджа данных (импортируем по одному продукту)
     */
    private void importB2BProductType(B2BProductType product, Long isMerge, String login, String password) throws Exception {
        List<Map<String, Object>> productList = new ArrayList<Map<String, Object>>();
        Map<String, Object> productMap = getB2BProductMap(product);
        productList.add(productMap);
        // вызываем метод универсального сохранения для импорта
        Map<String, Object> saveParams = new HashMap<String, Object>();
        saveParams.put("PRODLIST", productList);
        this.callService(Constants.B2BPOSWS, "dsB2BProductUniversalSave", saveParams, login, password);
    }

    @WsMethod(requiredParams = {"XMLDATA", "ISMERGE"})
    public Map<String, Object> dsB2BProductImportFromXML(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String xmlData = params.get("XMLDATA").toString();
        Long isMerge = Long.valueOf(params.get("ISMERGE").toString());
        B2BProducts pList = this.unmarshall(B2BProducts.class, xmlData);
        if ((pList != null) && (pList.getB2BProducts() != null) && (pList.getB2BProducts().size() > 0)) {
            for (B2BProductType productBean : pList.getB2BProducts()) {
                importB2BProductType(productBean, isMerge, login, password);
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        return result;
    }
    
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductGetExportUploadPath(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("UPLOADURL", getUploadFilePath());
        return result;
    }

    @WsMethod(requiredParams = {"XMLFILENAME", "ISMERGE"})
    public Map<String, Object> dsB2BProductImportFromXMLFile(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String xmlFileName = params.get("XMLFILENAME").toString();
        StringWriter writer = new StringWriter();
        IOUtils.copy(new FileInputStream(new File(xmlFileName)), writer, "UTF-8");
        String xmlData = writer.toString();
        Map<String, Object> importParams = new HashMap<String, Object>();
        importParams.put("XMLDATA", xmlData);
        importParams.put("ISMERGE", params.get("ISMERGE"));
        return this.callService(Constants.B2BPOSWS, "dsB2BProductImportFromXML", importParams, login, password);
    }
}
