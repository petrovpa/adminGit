package com.bivgroup.services.bivsberposws.facade.pos.custom.integration;

import com.bivgroup.services.bivsberposws.system.Constants;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;
import ru.sberbankins.schema.online10.DocumentType;
import ru.sberbankins.schema.online10.InsurerType;
import ru.sberbankins.schema.online10.PaymentType;
import ru.sberbankins.schema.online10.PlanPaymentScheduleType;
import ru.sberbankins.schema.online10.PlanType;

/**
 *
 * @author kkulkov
 */
public abstract class BaseIntegrationFacade extends BaseFacade {

    protected static final int MAX_SECOND = 59;
    protected static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    protected static final String INSUNDERWRITINGWS_SERVICE_NAME = Constants.INSUNDERWRITINGWS;
    protected static final String INSPRODUCTWS_SERVICE_NAME = Constants.INSPRODUCTWS;
    protected static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;
    protected static final String REFWS_SERVICE_NAME = Constants.REFWS;
    protected static final String CRMWS_SERVICE_NAME = Constants.CRMWS;
    protected static final String ADMINWS_SERVICE_NAME = Constants.ADMINWS;
    public static final String[] MONTH_NAMES = {"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};
    public static final String[] RUB_NAMES = {"рубль", "рубля", "рублей", "копейка", "копейки", "копеек", "M"};
    public static final String[] EUR_NAMES = {"евро", "евро", "евро", "евроцент", "евроцента", "евроцентов", "M"};
    public static final String[] USD_NAMES = {"доллар", "доллара", "долларов", "цент", "цента", "центов", "M"};
    public static final String[] PHONE_TYPE_LIST = {"RegisterAddressPhone", "PermanentAddressPhone", "FactAddressPhone", "WorkAddressPhone", "MobilePhone", "FaxNumber"};
    public static final String RUB_CODE = "RUB";
    public static final String USD_CODE = "USD";
    public static final String EUR_CODE = "EUR";
    private static final boolean isManualDateFormat = Boolean.TRUE;

    protected void getContrList(Map<String, Object> result,
            Map<String, Object> params, String login, String password)
            throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        if (params.get("CONTRIDLIST") != null) {
            List<Map<String, Object>> сontrIdList = (List<Map<String, Object>>) params.get("CONTRIDLIST");
            StringBuilder sb = new StringBuilder();

            for (Map<String, Object> map : сontrIdList) {
                if (sb.length() == 0) {
                    sb.append(map.get("CONTRID").toString());
                } else {
                    sb.append(",");
                    sb.append(map.get("CONTRID").toString());
                }
            }
            queryParams.put("CONTRIDLIST", sb.toString());
        } else {
            if (params.get("CONTRID") != null) {
                Long сontrId = Long.valueOf(params.get("CONTRID").toString());
                queryParams.put("CONTRID", сontrId);
            }
        }
        queryParams.put("STARTUPDATEDATE", params.get("STARTUPDATEDATE"));
        queryParams.put("CONTRCOUNT", params.get("CONTRCOUNT"));
        queryParams.put("STATESYSNAMELIST",
                "'INS_CONTRACT_SG', 'INS_CONTRACT_CANCEL', 'INS_CONTRACT_RECALL'");
        queryParams.put("ISPOSTINPUT", 0);

        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME,
                "dsContractBrowseListForIntegByParamEx", queryParams, login,
                password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            result.put("CONTRACTDATA",
                    (List<Map<String, Object>>) qres.get(RESULT));
        }
    }

    protected PlanPaymentScheduleType getContractsPaymentsScheduleResponse(Map<String, Object> params) throws Exception {
        PlanPaymentScheduleType xmlPaymentSchedule = new PlanPaymentScheduleType();
        if (params.get("PAYMENTSCHEDULELIST") != null) {
            List<Map<String, Object>> paymentScheduleList = (List<Map<String, Object>>) params.get("PAYMENTSCHEDULELIST");
            if (paymentScheduleList.size() > 0) {
                for (Iterator<Map<String, Object>> it = paymentScheduleList.iterator(); it.hasNext();) {
                    Map<String, Object> paymentSchedule = it.next();
                    PlanType xmlplan = new PlanType();
                    if (paymentSchedule.get("PAYDATE") != null) {
                        xmlplan.setDate(dateToXMLGC((Date) paymentSchedule.get("PAYDATE")));
                    }
                    if (paymentSchedule.get("AMOUNT") != null) {
                        xmlplan.setSum(new BigDecimal(paymentSchedule.get("AMOUNT").toString()));
                    }
                    xmlPaymentSchedule.getPlen().add(xmlplan);
                }
            }
        }
        return xmlPaymentSchedule;
    }

    protected InsurerType getContractInsurerResponse(Map<String, Object> params) throws Exception {
        InsurerType xmlInsurer = new InsurerType();
        if (params != null) {
            Map<String, Object> isured = params;
            if (isured.get("INSADDRESSTEXT1") != null) {
                xmlInsurer.setAddress(isured.get("INSADDRESSTEXT1").toString());
            }
            if (isured.get("INSBIRTHDATE") != null) {
                xmlInsurer.setDateOfBirth(dateToXMLGC((Date) isured.get("INSBIRTHDATE")));
            }
            if (isured.get("INSEMAIL") != null) {
                xmlInsurer.setEmail(isured.get("INSEMAIL").toString());
            }
            if (isured.get("INSFIRSTNAME") != null) {
                xmlInsurer.setName(isured.get("INSFIRSTNAME").toString());
            }
            if (isured.get("INSCITIZENSHIP") != null) {
                xmlInsurer.setIsResident("1".equals(isured.get("INSCITIZENSHIP").toString()));
            } else {
                xmlInsurer.setIsResident(Boolean.TRUE);
            }
            if (isured.get("INSMIDDLENAME") != null) {
                xmlInsurer.setPatronymic(isured.get("INSMIDDLENAME").toString());
            }
            if (isured.get("INSGENDER") != null) {
                if ("1".equals(isured.get("INSGENDER").toString())) {
                    xmlInsurer.setSex("female");
                } else {
                    xmlInsurer.setSex("male");
                }
            }
            if (isured.get("INSLASTNAME") != null) {
                xmlInsurer.setSurname(isured.get("INSLASTNAME").toString());
            }
            if (isured.get("INSPHONE") != null) {
                xmlInsurer.setTel("+7" + isured.get("INSPHONE").toString());
            }
            xmlInsurer.setDocument(getContractInsurerDocumentResponse(isured));
        }
        return xmlInsurer;
    }

    protected DocumentType getContractInsurerDocumentResponse(Map<String, Object> params) throws Exception {
        DocumentType xmlDocument = new DocumentType();
        Map<String, Object> insuredDocument = params;
        xmlDocument.setInfo("");
        if (insuredDocument.get("INSISSUERCODE") != null) {
            xmlDocument.setCode(insuredDocument.get("INSISSUERCODE").toString());
        } else {
            xmlDocument.setCode("");
        }
        if (insuredDocument.get("INSISSUEDATE") != null) {
            xmlDocument.setDateOfIssue(dateToXMLGC((Date) insuredDocument.get("INSISSUEDATE")));
        }
        if (insuredDocument.get("INSISSUEDBY") != null) {
            xmlDocument.setAuthority(insuredDocument.get("INSISSUEDBY").toString());
        }
        if (insuredDocument.get("INSDOCNUMBER") != null) {
            xmlDocument.setNumber(insuredDocument.get("INSDOCNUMBER").toString());
        }
        if (insuredDocument.get("INSDOCCODE") != null) {
            xmlDocument.setKind(insuredDocument.get("INSDOCCODE").toString());
        } else {
            xmlDocument.setKind("");
        }
        if (insuredDocument.get("INSDOCSERIES") != null) {
            xmlDocument.setSeries(insuredDocument.get("INSDOCSERIES").toString());
        }
        return xmlDocument;
    }

    protected PaymentType getContractsPaymentsResponse(Map<String, Object> params) throws Exception {
        PaymentType xmlPayment = new PaymentType();
        if (params.get("PAYMENTLIST") != null) {
            Map<String, Object> payment = (Map<String, Object>) params.get("PAYMENTLIST");
            xmlPayment.setPaymentMethod("00001");
            if (payment.get("PAYFACTDATE") != null) {
                xmlPayment.setDate(dateToXMLGC((Date) payment.get("PAYFACTDATE")));
            }
            if (payment.get("PAYFACTNUMBER") != null) {
                xmlPayment.setIDTransaction((payment.get("PAYFACTNUMBER").toString()));
            }
            if (payment.get("AMVALUE") != null) {
                xmlPayment.setSum(new BigDecimal(payment.get("AMVALUE").toString()));
            }
            if (payment.get("AMVALUE") != null) {
                xmlPayment.setSumRUB(new BigDecimal(payment.get("AMVALUE").toString()));
            }
        }
        return xmlPayment;
    }

    protected Map<String, Object> dsProductDefaultValueByProdConfId(Object prodConfId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(RETURN_AS_HASH_MAP, WsConstants.TRUE);
        queryParams.put("PRODCONFID", prodConfId);
        Map<String, Object> result = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", queryParams, login, password);
        return result;

    }

    protected Map<String, Object> dsContrMakeTransToUpload(Map<String, Object> bean, String login, String password) throws Exception {
        Map<String, Object> result = null;
        if (!bean.get("STATESYSNAME").toString().equalsIgnoreCase("INS_CONTRACT_UPLOADED_SUCCESFULLY")) {
            Map<String, Object> qTransParam = new HashMap<String, Object>();
            qTransParam.put("ReturnAsHashMap", "TRUE");
            qTransParam.put("JOIN_TO_SMTYPE", "TRUE");
            qTransParam.put("FROMSTATESYSNAME", bean.get("STATESYSNAME"));
            qTransParam.put("TOSTATESYSNAME", "INS_CONTRACT_UPLOADED_SUCCESFULLY");// переводим в статус "Выдан"
            qTransParam.put("TYPESYSNAME", "INS_CONTRACT");
            Map<String, Object> qTransRes = this.callService(INSPOSWS_SERVICE_NAME, "dsTransitionsBrowseByParamEx", qTransParam, login, password);
            Map<String, Object> qres = null;
            if (qTransRes.get("SYSNAME") != null) {
                String transSysName = qTransRes.get("SYSNAME").toString();
                if ((bean.get("STATESYSNAME") != null) && (!transSysName.isEmpty())) {

                    Map<String, Object> params = new HashMap<String, Object>();
                    //params.put("ReturnAsHashMap", "TRUE");
                    params.put("CONTRID", bean.get("CONTRID"));
                    params.put("DOCUMENTID", bean.get("CONTRID"));
                    params.put("TYPESYSNAME", "INS_CONTRACT");
                    params.put("TRANSITIONSYSNAME", transSysName);

                    qres = this.callService(INSPOSWS_SERVICE_NAME, "dsContract_State_MakeTrans", params, login, password);
                    logger.debug(qres.toString());
                }
            }

            // сменим состояние текущего договора
        }
        return result;
    }

    protected Map<String, Object> getPolicyExtData(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> qParams = new HashMap<String, Object>();
        if (params.get("CONTRIDLIST") != null) {
            qParams.put("CONTRIDLIST", params.get("CONTRIDLIST"));
            qParams.put("CONTRID", 0l);
        } else {
            qParams.put("ReturnAsHashMap", "TRUE");
            qParams.put("CONTRID", params.get("CONTRID"));
        }
        if (params.get("CONTRDATA") != null) {
            qParams.put("PRODCONFID", ((Map<String, Object>) params.get("CONTRDATA")).get("PRODCONFID"));
        } else {
            List<Map<String, Object>> contrList = (List<Map<String, Object>>) params.get("CONTRACTDATA");
            qParams.put("PRODCONFID", contrList.get(0).get("PRODCONFID"));
        }
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsContractExtensionBrowseListByParamEx", qParams, login, password);
        return qres;
    }

    protected String getContrIDlist(Map<String, Object> result) {
        List<Map<String, Object>> сontrList = (List<Map<String, Object>>) result.get("CONTRACTDATA");
        StringBuilder sb = new StringBuilder();
        if (сontrList != null) {
            result.put("CONTRCOUNT", сontrList.size());
            List<Long> contrIdList = new ArrayList<Long>();
            for (Map<String, Object> map : сontrList) {
                if (sb.length() == 0) {
                    sb.append(map.get("CONTRID").toString());
                    contrIdList.add(Long.valueOf(map.get("CONTRID").toString()));
                } else {
                    sb.append(",");
                    sb.append(map.get("CONTRID").toString());
                    contrIdList.add(Long.valueOf(map.get("CONTRID").toString()));
                }
            }
            result.put("CONTRIDLIST", contrIdList);
        }
        return sb.toString();
    }

    protected String getContrObjIDlist(Map<String, Object> result) {
        List<Map<String, Object>> сontrObjList = (List<Map<String, Object>>) result.get("CONRTOBJLIST");
        StringBuilder sb = new StringBuilder();
        if (сontrObjList != null) {
            result.put("CONTROBJCOUNT", сontrObjList.size());
            List<Long> contrObjIdList = new ArrayList<Long>();
            for (Map<String, Object> map : сontrObjList) {
                if (sb.length() == 0) {
                    sb.append(map.get("CONTROBJID").toString());
                    contrObjIdList.add(Long.valueOf(map.get("CONTROBJID").toString()));
                } else {
                    sb.append(",");
                    sb.append(map.get("CONTROBJID").toString());
                    contrObjIdList.add(Long.valueOf(map.get("CONTROBJID").toString()));
                }
            }
            result.put("CONTROBJIDLIST", contrObjIdList);
        }
        return sb.toString();
    }

    protected void getContractExtensionList(Map<String, Object> result, String contrIDlist, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        if (!"".equals(contrIDlist)) {
            queryParams.put("CONTRIDLIST", contrIDlist);
            queryParams.put(WsConstants.LOGIN, login);
            queryParams.put(WsConstants.PASSWORD, password);
            Map<String, Object> qres = this.selectQuery("dsContractExtensionBrowseListByContrIDLIST", null, queryParams);
            if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
                result.put("CONTRACTEXTDATA", (List<Map<String, Object>>) qres.get(RESULT));
            }
        }
    }

    protected List<Map<String, Object>> getContrObjList(String contrIDlist, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        if (StringUtils.isNotEmpty((contrIDlist))) {
            queryParams.put("CONTRIDLIST", contrIDlist);
            Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsContractObjectVehicleBrowseListForIntegByParam", queryParams, login, password);
            if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
                return (List<Map<String, Object>>) qres.get(RESULT);
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    protected List<Map<String, Object>> getContrObjRiskList(Long contrId, String login, String password) throws Exception {
        Map<String, Object> qParams = new HashMap<String, Object>();
        qParams.put("CONTRID", contrId);
        Map<String, Object> qRes = this.callService(INSPOSWS_SERVICE_NAME, "dsContractRiskBrowseListByContrIdJoinProdRisk", qParams, login, password);
        if ((qRes != null) && (qRes.get(RESULT) != null) && (((List) qRes.get(RESULT)).size() > 0)) {
            return (List<Map<String, Object>>) qRes.get(RESULT);
        }
        return null;
    }

    protected List<Map<String, Object>> getPersonDoc(Long participantId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("PARTICIPANTID", participantId);
        List<Map<String, Object>> result = null;
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "personDocGetListByParticipantId", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            result = (List<Map<String, Object>>) qres.get(RESULT);
        }
        return result;
    }

    protected List<Map<String, Object>> getCompanyDoc(Long participantId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("PARTICIPANTID", participantId);
        List<Map<String, Object>> result = null;
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "partRegDocGetListByParticipantId", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            result = (List<Map<String, Object>>) qres.get(RESULT);
        }
        return result;
    }

    protected List<Map<String, Object>> getContacts(Long participantId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("PARTICIPANTID", participantId);
        List<Map<String, Object>> result = null;
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "contactGetListByParticipantId", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            result = (List<Map<String, Object>>) qres.get(RESULT);
        }
        return result;
    }

    protected List<Map<String, Object>> getPersonAddress(Long participantId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("PARTICIPANTID", participantId);
        List<Map<String, Object>> result = null;
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "addressGetListByParticipantId", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            result = (List<Map<String, Object>>) qres.get(RESULT);
        }
        return result;
    }

    protected List<Map<String, Object>> getVehicleAddress(Long vehicleId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("OBJID", vehicleId);
        List<Map<String, Object>> result = null;
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsObjectAddressBrowseListByParamsExt", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            result = (List<Map<String, Object>>) qres.get(RESULT);
        }
        return result;
    }

    protected List<Map<String, Object>> getAltNameByParticipantId(Long participantId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("PARTICIPANTID", participantId);
        List<Map<String, Object>> result = null;
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "altNameGetListByParticipantId", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            result = (List<Map<String, Object>>) qres.get(RESULT);
        }
        return result;
    }

    protected List<Map<String, Object>> getContrObjInsuredListByContrId(Long contrId, String login, String password) throws Exception {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (contrId != null) {
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("CONTRID", contrId);
            Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsContractObjectInsuredBrowseListByParam", queryParams, login, password);
            if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
                result = (List<Map<String, Object>>) qres.get(RESULT);
            }
        }
        return result;
    }

    protected List<Map<String, Object>> getContrCountryList(Long contrId, String login, String password) throws Exception {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (contrId != null) {
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("CONTRID", contrId);
            queryParams.put("ORDERBY", "CONTRCOUNTRYID ASC");
            Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsContractCountryBrowseListByParam", queryParams, login, password);
            if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
                result = (List<Map<String, Object>>) qres.get(RESULT);
            }
        }
        return result;
    }

    protected List<Map<String, Object>> getProdRisksByProdVerId(Long prodVerId, String login, String password) throws Exception {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (prodVerId != null) {
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("PRODVERID", prodVerId);
            Map<String, Object> qres = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductRiskBrowseListByParam", queryParams, login, password);
            if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
                result = (List<Map<String, Object>>) qres.get(RESULT);
            }
        }
        return result;
    }

    protected Map<String, Object> getCompany(Long participantId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("PARTICIPANTID", participantId);
        queryParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "companyGetListByParams", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            qres = (Map<String, Object>) qres.get(RESULT);
        }
        Map<String, Object> qresExtAtt = this.callService(CRMWS_SERVICE_NAME, "extAttributeGetListByParticipantId", queryParams, login, password);
        if (qresExtAtt.get("EXTENDED_ATTRIBUTES") != null) {
            List<Map<String, Object>> extAttList = (List<Map<String, Object>>) qresExtAtt.get("EXTENDED_ATTRIBUTES");
            for (Map<String, Object> extAtt : extAttList) {
                if (extAtt.get("EXTATT_SYSNAME") != null) {
                    if ("KPP".equalsIgnoreCase(extAtt.get("EXTATT_SYSNAME").toString())) {
                        qres.put("KPP", extAtt.get("EXTATTVAL_VALUE"));
                    }
                }
            }
        }
        return qres;
    }

    protected Map<String, Object> getBank(Long participantId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("PARTICIPANTID", participantId);
        queryParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "financialInstGetListByParams", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            qres = (Map<String, Object>) qres.get(RESULT);
        }
        Map<String, Object> qresExtAtt = this.callService(CRMWS_SERVICE_NAME, "extAttributeGetListByParticipantId", queryParams, login, password);
        if (qresExtAtt.get("EXTENDED_ATTRIBUTES") != null) {
            List<Map<String, Object>> extAttList = (List<Map<String, Object>>) qresExtAtt.get("EXTENDED_ATTRIBUTES");
            for (Map<String, Object> extAtt : extAttList) {
                if (extAtt.get("EXTATT_SYSNAME") != null) {
                    if ("KPP".equalsIgnoreCase(extAtt.get("EXTATT_SYSNAME").toString())) {
                        qres.put("KPP", extAtt.get("EXTATTVAL_VALUE"));
                    }
                }
            }
        }
        return qres;
    }

    protected Map<String, Object> getDocTypeCodeById(Long docTypeId, String login, String password) throws Exception {
        Map<String, Object> qparams = new HashMap<String, Object>();
        qparams.put("PERSONDOCTYPEID", docTypeId);
        qparams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "personDocTypeGetByIdOrSysname", qparams, login, password);
        return qres;
    }

    protected String objectToXML(final Object value) throws Exception {
        XMLUtil util = new XMLUtil(false, true);
        String strValue = util.createXML(new HashMap() {
            {
                put(RESULT, value);
            }
        });
        return strValue;
    }

    private Long getHBDescrID(String hbDescrName, String hbNote, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("ReturnAsHashMap", "TRUE");
        queryParams.put("NAME", hbDescrName);
        queryParams.put("NOTE", hbNote);

        Map<String, Object> result = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookDescriptorsBrowseListByParam", queryParams, login, password);
        if (result != null) {
            if (result.get("HBDESCRID") != null) {
                return Long.valueOf(result.get("HBDESCRID").toString());
            }
        }
        return new Long(0);
    }

    private Long getHBDescrID(String hbDescrName, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("ReturnAsHashMap", "TRUE");
        queryParams.put("NAME", hbDescrName);

        Map<String, Object> result = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookDescriptorsBrowseListByParam", queryParams, login, password);
        if (result != null) {
            if (result.get("HBDESCRID") != null) {
                return Long.valueOf(result.get("HBDESCRID").toString());
            }
        }
        return new Long(0);
    }

    protected Map<String, Object> getHBRowById(String hbName, Object searchFieldVal, String searchFieldName, String login, String password) throws Exception {
        Map<String, Object> result = null;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("HBDATAVERID", getMaxCalcVerId(getHBDescrID(hbName, login, password), login, password));
        queryParams.put(searchFieldName, searchFieldVal);
        Map<String, Object> qres = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordBrowseListByParam", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            result = ((List<Map<String, Object>>) qres.get(RESULT)).get(0);
        }
        return result;
    }

    protected Map<String, Object> getHBRowById(String hbName, String hbNote, Object searchFieldVal, String searchFieldName, String login, String password) throws Exception {
        Map<String, Object> result = null;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("HBDATAVERID", getMaxCalcVerId(getHBDescrID(hbName, hbNote, login, password), login, password));
        queryParams.put(searchFieldName, searchFieldVal);
        Map<String, Object> qres = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordBrowseListByParam", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            result = ((List<Map<String, Object>>) qres.get(RESULT)).get(0);
        }
        return result;
    }

    protected Map<String, Object> getSingleHBRow(String hbName, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("HBDATAVERID", getMaxCalcVerId(getHBDescrID(hbName, login, password), login, password));
        queryParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordBrowseListByParam", queryParams, login, password);
        return qres;
    }

    private Long getMaxCalcVerId(Long hbDescrId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        if (hbDescrId > 0) {
            queryParams.put("HBDESCRID", hbDescrId);
            Map<String, Object> res = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookDataVersionBrowseListByParam", queryParams, login, password);
            if (res != null) {
                if (res.get("Result") != null) {
                    List<Map<String, Object>> verList = (List<Map<String, Object>>) res.get("Result");
                    Long maxVerId = new Long(0);
                    for (Map<String, Object> map : verList) {
                        if (map.get("HBDATAVERID") != null) {
                            if (maxVerId < Long.valueOf(map.get("HBDATAVERID").toString())) {
                                maxVerId = Long.valueOf(map.get("HBDATAVERID").toString());
                            }
                        }
                    }
                    return maxVerId;
                }
            }
        }
        return new Long(0);
    }

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
            //Logger.getLogger(RSAServices.class.getName()).error( null, ex);
            throw new Exception("Ошибка преобразования объекта в xml", ex);
        } catch (RuntimeException ex) {
            throw new Exception("Ошибка преобразования объекта в xml", ex);
        } catch (UnsupportedEncodingException ex) {
            throw new Exception("Ошибка преобразования объекта в xml", ex);
        }
        return result;
    }

    protected Date getContrMaxUpdateDate(Map<String, Object> result) {
        List<Map<String, Object>> сontrList = (List<Map<String, Object>>) result.get("CONTRACTDATA");
        Date contrMaxUpdateDate = null;
        result.put("CONTRCOUNT", сontrList.size());
        for (Map<String, Object> map : сontrList) {
            if (contrMaxUpdateDate == null) {
                if (map.get("UPDATEDATE") != null) {
                    contrMaxUpdateDate = (Date) (map.get("UPDATEDATE"));
                } else {
                    contrMaxUpdateDate = (Date) (map.get("CREATEDATE"));
                }
            } else {
                if (map.get("UPDATEDATE") != null) {
                    if (contrMaxUpdateDate.getTime() < ((Date) (map.get("UPDATEDATE"))).getTime()) {
                        contrMaxUpdateDate = (Date) (map.get("UPDATEDATE"));
                    }
                } else {
                    if (map.get("CREATEDATE") != null) {
                        if (contrMaxUpdateDate.getTime() < ((Date) (map.get("CREATEDATE"))).getTime()) {
                            contrMaxUpdateDate = (Date) (map.get("CREATEDATE"));
                        }
                    }
                }
            }
        }
        if (contrMaxUpdateDate != null) {
            contrMaxUpdateDate.setTime(contrMaxUpdateDate.getTime() + 1);
        }
        return contrMaxUpdateDate;
    }

    protected Object getHBFieldById(String hbName, String hbNote, Object searchFieldVal, String searchFieldName, String getFieldName, String login, String password) throws Exception {
        Object result = "";
        if (searchFieldVal != null) {
            Map<String, Object> qres = getHBRowById(hbName, hbNote, searchFieldVal, searchFieldName, login, password);
            if (qres != null) {
                result = qres.get(getFieldName);
            }
        }
        return result;
    }

    protected Object getHBFieldById(String hbName, Object searchFieldVal, String searchFieldName, String getFieldName, String login, String password) throws Exception {
        Object result = "";
        if (searchFieldVal != null) {
            Map<String, Object> qres = getHBRowById(hbName, searchFieldVal, searchFieldName, login, password);
            if (qres != null) {
                result = qres.get(getFieldName);
            }
        }
        return result;
    }

    protected Integer getIntegerParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Integer.valueOf(bean.toString());
        } else {
            return 0;
        }
    }

    protected int getSimpleIntParam(Object bean) {
        Integer res = getIntegerParam(bean);
        return res == null ? -1 : res.intValue();
    }

    protected BigInteger getBigIntegerParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigInteger.valueOf(Long.valueOf(bean.toString()).longValue());
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

    protected BigDecimal getBigDecimalParam(Map<String, Object> map, String key) {
        if (map != null) {
            if (map.get(key) != null) {
                Object bean = map.get(key);
                if (bean != null && bean.toString().trim().length() > 0) {
                    return BigDecimal.valueOf(Double.valueOf(bean.toString())).setScale(2, RoundingMode.HALF_UP);
                } else {
                    return null;
                }
            }
        }
        return null;
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
            if (date instanceof Double) {
                return XMLUtil.convertDate((Double) date);
            } else if (date instanceof BigDecimal) {
                return XMLUtil.convertDate((BigDecimal) date);
            } else if (date instanceof String) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                try {

                return sdf.parse((String) date);
                } catch (ParseException e) {
                    
                }
            }
            return (Date) date;
        } else {
            return null;
        }
    }

    protected String getStringParam(Map<String, Object> bean, String key) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else {
            if (bean.get(key) == null) {
                return StringUtils.EMPTY;
            } else {
                return bean.get(key).toString();
            }
        }
    }

    protected String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else {
            return bean.toString();
        }
    }

    protected XMLGregorianCalendar dateToXMLGC(Date date) throws Exception {
        return dateToXMLGC(date, false);
    }

    /**
     * Fix т.к. система обрезает в датах секунды
     *
     * @param date - дата
     * @param isFixSeconds - флаг добавления секунд до 59 (важно!!! должно быть
     * 59, никаких 50 )
     */
    protected XMLGregorianCalendar dateToXMLGC(Date date, boolean isFixSeconds) throws Exception {
        XMLGregorianCalendar result = null;
        if (date != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(date);
            if (isFixSeconds) {
                gc.set(Calendar.SECOND, MAX_SECOND);
            }
            try {
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            } catch (DatatypeConfigurationException ex) {
                throw new Exception("Error convert Date to XMLGregorianCalendar", ex);
            }

        }
        return result;
    }

    protected Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }

    protected Boolean getBooleanParam(Object bean) {
        if (bean != null) {
            if (bean.toString().equalsIgnoreCase("1") || Boolean.parseBoolean(bean.toString())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected String getFormatByDate(Date date) {
        String result = null;
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            result = dateFormat.format(date);
        }
        return result;
    }

    protected String getStringByDate(Date date) throws Exception {
        XMLGregorianCalendar xmlgc = dateToXMLGC(date);
        String result = "";
        if (xmlgc != null) {
            String maybeZero = (xmlgc.getDay() < 10) ? "0" : "";
            result = "«" + maybeZero + xmlgc.getDay() + "» " + MONTH_NAMES[xmlgc.getMonth() - 1] + " " + xmlgc.getYear() + "";
        }
        return result;
    }

    protected String getCurrByCodeToNum(String curCode, BigDecimal amValue) {
        int amValueInt = amValue.intValue();
        return getCurrByCodeToNum(curCode, amValueInt);
    }

    protected String getCurrByCodeToNum(String curCode, int amValueInt) {
        String[] CurrNames = RUB_NAMES;
        if (curCode.equalsIgnoreCase(RUB_CODE)) {
        } else if (curCode.equalsIgnoreCase(USD_CODE)) {
            CurrNames = USD_NAMES;
        } else if (curCode.equalsIgnoreCase(EUR_CODE)) {
            CurrNames = EUR_NAMES;
        }
        String result = CurrNames[0];

        int rank10 = amValueInt % 100 / 10;
        int rank = amValueInt % 10;
        if (rank10 == 1) {
            result = CurrNames[2];
        } else {
            switch (rank) {
                case 1:
                    result = CurrNames[0];
                    break;
                case 4:
                    result = CurrNames[1];
                    break;
                default:
                    result = CurrNames[2];
            }
        }
        return result;
    }

    protected String getCurrByCodeToNumGen(String curCode, BigDecimal amValue) {
        int amValueInt = amValue.intValue();
        return getCurrByCodeToNumGen(curCode, amValueInt);
    }

    protected String getCurrByCodeToNumGen(String curCode, int amValueInt) {
        String[] CurrNames = RUB_NAMES;
        if (curCode.equalsIgnoreCase(RUB_CODE)) {
        } else if (curCode.equalsIgnoreCase(USD_CODE)) {
            CurrNames = USD_NAMES;
        } else if (curCode.equalsIgnoreCase(EUR_CODE)) {
            CurrNames = EUR_NAMES;
        }
        String result = CurrNames[1];

        int rank10 = amValueInt % 100 / 10;
        int rank = amValueInt % 10;
        if (rank10 == 1) {
            result = CurrNames[2];
        } else {
            switch (rank) {
                case 1:
                    result = CurrNames[1];
                    break;
                default:
                    result = CurrNames[2];
            }
        }
        return result;
    }

    private String getPhoneType(String contrPhoneType) {
        for (String phoneType : PHONE_TYPE_LIST) {
            if (phoneType.equalsIgnoreCase(contrPhoneType)) {
                return phoneType;
            }
        }
        return null;
    }

    protected String cutIndexFromFullAddress(String fullAddress, String postCode) {
        String result = fullAddress;
        if (!result.isEmpty()) {
            if (result.length() - (postCode.length() + 1) >= 0) {
                if (!postCode.isEmpty()) {
                    result = fullAddress.substring(postCode.length() + 1);
                }
            }
        }
        return result;
    }

    protected void getContrObjDriverList(Map<String, Object> result, String contrIDlist, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        if (StringUtils.isNotEmpty(contrIDlist)) {
            queryParams.put("CONTRIDLIST", contrIDlist);
            queryParams.put(WsConstants.LOGIN, login);
            queryParams.put(WsConstants.PASSWORD, password);
            Map<String, Object> qres = this.selectQuery("dsContractDriverByCONTRID", null, queryParams);
            if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
                List<Map<String, Object>> res = (List<Map<String, Object>>) qres.get(RESULT);
                CopyUtils.sortByLongFieldName(res, "CONTRID");
                result.put("CONTRACTOBJDRIVERDATA", res);
            }
        }
    }

    protected List<Map<String, Object>> getContrObjsExtData(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> qParams = new HashMap<String, Object>();
        if (params.get("CONTROBJIDLIST") != null) {
            qParams.put("CONTROBJIDLIST", params.get("CONTROBJIDLIST"));
            qParams.put("CONTROBJID", 0l);
        } else {
            qParams.put(WsConstants.RETURN_AS_HASH_MAP, WsConstants.TRUE);
            qParams.put("CONTROBJID", params.get("CONTROBJID"));
        }
        if (params.get("CONTRDATA") != null) {
            qParams.put("PRODCONFID", ((Map<String, Object>) params.get("CONTRDATA")).get("PRODCONFID"));
        } else {
            List<Map<String, Object>> contrList = (List<Map<String, Object>>) params.get("CONTRACTDATA");
            qParams.put("PRODCONFID", contrList.get(0).get("PRODCONFID"));
        }
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsContractObjectExtensionBrowseListByParamEx", qParams, login, password);
        return qres != null ? (List<Map<String, Object>>) qres.get(RESULT) : new ArrayList<Map<String, Object>>();
    }

    protected List<Map<String, Object>> getPaymentListData(Long contrId, String login, String password) throws Exception {
        List<Map<String, Object>> result = null;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("CONTRID", contrId);
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentFactBrowseListByParamEx", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            result = (List<Map<String, Object>>) qres.get(RESULT);
        }
        return result;
    }

    protected String getPaymentVariant(Map<String, Object> map, String login, String password) throws Exception {
        String result = "";
        Map<String, Object> qparam = new HashMap<String, Object>();
        qparam.put("PAYVARID", map.get("PAYVARID"));
        qparam.put("PRODVERID", map.get("PRODVERID"));
        qparam.put(WsConstants.RETURN_AS_HASH_MAP, WsConstants.TRUE);
        Map<String, Object> qres = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductPaymentVariantBrowseListByParam", qparam, login, password);
        if (qres != null) {
            if (qres.get("NAME") != null) {
                result = getStringParam(qres.get("NAME"));
            }
        }
        return result;
    }

    protected List<Map<String, Object>> getPlanPaymentListData(long contrId, String login, String password) throws Exception {
        List<Map<String, Object>> result = null;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("CONTRID", contrId);
        queryParams.put("ORDERBY", "PAYID");
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentBrowseListByParam", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            result = (List<Map<String, Object>>) qres.get(RESULT);
        }
        return result;
    }

    protected Integer getDurationDays(Date startDate, Date finishDate) {
        GregorianCalendar startGC = new GregorianCalendar();
        startGC.setTime(startDate);
        GregorianCalendar finishGC = new GregorianCalendar();
        finishGC.setTime(finishDate);
        return WsUtils.calcDay(startGC, finishGC);
    }

    protected XMLGregorianCalendar getDate(Map<String, Object> map, String columnName) {
        return getDate(map, columnName, 180);
    }

    protected XMLGregorianCalendar getB2BDate(Map<String, Object> map, String columnName) {
        return getB2BDate(map, columnName, 180);
    }

    protected XMLGregorianCalendar getB2BDate(Map<String, Object> map, String columnName, int offset) {
        XMLGregorianCalendar result = null;
        try {
            if ((map.get(columnName) != null) && (map.get(columnName + "TIME") != null)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                String date = map.get(columnName).toString();
                String time = map.get(columnName + "TIME").toString();
                String dateTime = date + " " + time;
                Date d = sdf.parse(dateTime);
                if (d != null) {
                    result = dateToXMLGC(d);
                    if (result != null) {
                        result.setTimezone(offset);
                        if ("STARTDATE".equalsIgnoreCase(columnName)) {
                            result.setHour(0);
                            result.setMinute(0);
                            result.setSecond(0);
                            result.setMillisecond(0);
                        }
                        if ("BIRTHDATE".equalsIgnoreCase(columnName)) {
                            result.setHour(0);
                            result.setMinute(0);
                            result.setSecond(0);
                            result.setMillisecond(0);
                        }
                        if ("ISSUEDATE".equalsIgnoreCase(columnName)) {
                            result.setHour(0);
                            result.setMinute(0);
                            result.setSecond(0);
                            result.setMillisecond(0);
                        }
                        if ("FINISHDATE".equalsIgnoreCase(columnName)) {
                            result.setHour(23);
                            result.setMinute(59);
                            result.setSecond(59);
                            result.setMillisecond(0);
                        }
                    }
                }
            } else {
                result = getDate(map, columnName, offset);
            }
        } catch (Exception e) {
            logger.debug("getDate exception " + columnName, e);
        }
        return result;
    }

    protected XMLGregorianCalendar getDate(Map<String, Object> map, String columnName, int offset) {
        XMLGregorianCalendar result = null;
        try {

            result = dateToXMLGC(getDateParam(map.get(columnName)));
            if (result != null) {
                result.setTimezone(offset);
                if ("STARTDATE".equalsIgnoreCase(columnName)) {
                    result.setHour(0);
                    result.setMinute(0);
                    result.setSecond(0);
                    result.setMillisecond(0);
                }
                if ("FINISHDATE".equalsIgnoreCase(columnName)) {
                    result.setHour(23);
                    result.setMinute(59);
                    result.setSecond(59);
                    result.setMillisecond(0);
                }
            }
        } catch (Exception e) {
            logger.debug("getDate exception " + columnName, e);
        }
        return result;
    }

    private String getCurrencyCodeNum(String currCode) {
        if (RUB_CODE.equalsIgnoreCase(currCode)) {
            return "810";
        }
        if (USD_CODE.equalsIgnoreCase(currCode)) {
            return "840";
        }
        if (EUR_CODE.equalsIgnoreCase(currCode)) {
            return "978";
        }
        return "810";
    }

    protected Map<String, Object> getPerson(Long participantId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(WsConstants.PARTICIPANTID, participantId);
        queryParams.put(WsConstants.RETURN_AS_HASH_MAP, WsConstants.TRUE);
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "personGetListByParams", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            qres = (Map<String, Object>) qres.get(RESULT);
        }
        return qres;
    }

    protected Map<String, Object> getCurrencyById(Long currencyId, String login, String password) throws Exception {
        Map<String, Object> qparam = new HashMap<String, Object>();
        qparam.put("CurrencyID", currencyId);
        qparam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(REFWS_SERVICE_NAME, "getCurrencyByParams", qparam, login, password);
        return qres;
    }

    protected String getCountryCodeById(Long countryId, String login, String password) throws Exception {
        Map<String, Object> qparam = new HashMap<String, Object>();
        qparam.put("COUNTRYID", countryId);
        qparam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(REFWS_SERVICE_NAME, "findCountryById", qparam, login, password);
        if (qres != null) {
            if (qres.get("ALPHACODE3") != null) {
                return getStringParam(qres.get("ALPHACODE3"));
            }
        }
        return null;
    }

    protected String formatFullAddress(String address) {
        if (!address.isEmpty()) {
            // удаляем первую запятую буде таковая имеется
            address = address.replaceAll(",+( ?,+){1,}", ",");
            if (address.charAt(0) == ',') {
                address = address.substring(1);
            }
            // аналогично - с крайней запятой
            if (address.charAt(address.length() - 2) == ',') {
                address = address.substring(0, address.length() - 2);
            }
        }
        return address;
    }

    private Map<String, Object> getParticipant(Long participantId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("PARTICIPANTID", participantId);
        queryParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "participantGetById", queryParams, login, password);
        return qres;
    }

    protected enum ObjIdTypesEnum {

        CONTRACT("CONTRID", "EXTERNALID"),
        CONTRACTNODE("CONTRNODEID", "CONTRNODEEXTERNALID"),
        PRODUCT("PRODID", "PRODEXTERNALID", "PRODSYSNAME", "PRODNAME", "PRODNOTE"),
        PRODUCTVERSION("PRODVERID", null, "PRODVERNAME", "PRODVERNOTE"),
        PARTICIPANT("PARTICIPANTID", "EXTERNALID"),
        INSOBJ("INSOBJID", "EXTERNALID"),
        CONTROBJ("CONTROBJID"),
        INSKIND("INSKINDID", "EXTERNALID", "SYSNAME"),
        INSOBJID("INSOBJID", "EXTERNALID", "VEHICLENAME"),
        PERSONDOC("PERSONDOCID"),
        PARTREGDOC("PARTREGDOCID"),
        PRODRISK("PRODRISKID", null, "PRODRISKSYSNAME", "PRODRISKNAME"),
        CONTRPARTROLE("CONTRPARTROLEID"),
        DRIVER("DRIVERID"),
        DIC_OSAGO_TRAVEL_INS_TERM("hid", null, "name"),
        DIC_OSAGO_VEH_PURPOSE("hid", null, "name"),
        DIC_AUTO_INS_TERM("hid", "externalid", "name"),
        DIC_AUTO_BREAK("hid", null, "name"),
        DIC_AUTO_COEFINSAMOUNTTYPE("hid", null, "name"),
        DIC_AUTO_SERVICE_PROGRAM("hid", null, "name", "Note", "MessageName"),
        DIC_AUTO_JURIDICAL_PROGRAM("hid", null, "name", "Note"),
        DIC_AUTO_AUTO_PURPOSE("hid", "externalid", "sysName", "name"),
        DIC_AUTO_AUTO_ACCIDENT_TYPE("hid", null, "sysname", "name"),
        DIC_AUTO_AUTO_ACCIDENT("hid"),
        DIC_AUTO_AUTO_CIVILLIABILITY("hid"),
        DIC_AUTO_AUTO_JIRIDICAL("hid"),
        DIC_AUTO_AUTO_LUGAGGE("hid"),
        DIC_AUTO_VEHICLE_TYPE("hid", "externalid", "name"),
        DIC_AUTO_VEHMARK("VEHMARKID", "EXTERNALID", "NAME", "NAMEENG"),
        DIC_AUTO_VEHMODEL("VEHMODELID", "EXTERNALID", "NAME", "NAMEENG"),
        DIC_AUTO_AUTO_ENGINE_TYPE("HID", "externalid", "sysName", "Name"),
        DIC_AUTO_AUTO_TRANSMISSION_TYPE("HID", "externalid", "sysName", "Name"),
        DIC_AUTO_AUTO_ANTI_DEVICE_CATEGORY("hid", "externalid", "name", "Note"),
        DIC_AUTO_AUTO_ANTI_DEVICE_MARK("HID", "ExternalID", "Name"),
        DIC_AUTO_AUTO_ANTI_DEVICE_MODEL("HID", "ExternalID", "Name"),
        DIC_AUTO_AUTO_CAR_OWNERSHIP("HID", null, "SysName", "Name"),
        DIC_AUTO_DRIVER_COUNT("hid", "externalid", "sysName", "Name"),
        DIC_AUTO_VTB24_DEPARTMENT("HBSTOREID", "ExternalID", "Name"),
        DIC_AUTO_VTB24_EMPLOYEE("HBSTOREID", "ExternalID", "Name"),
        DIC_AUTO_DISCOUNTEXT("DISCOUNTID", "EXTERNALID");
        private final String idname;
        private final String extIdName;
        private final String[] fields;

        private ObjIdTypesEnum(String name, String extID, String... fields) {
            idname = name;
            extIdName = extID;
            this.fields = fields;
        }

        private ObjIdTypesEnum(String name) {
            idname = name;
            extIdName = null;
            this.fields = null;
        }

        public String getObjIdTypeName() {
            return idname;
        }

        public String getObjExtIdName() {
            return extIdName;
        }

        public int getObjSysNameLength() {
            return fields != null ? fields.length : 0;
        }

        public String getObjSysName(int index) {
            return fields[index];
        }
    }

}
