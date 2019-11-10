/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom.integration;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.sberbankins.schema.online10.ContractType;
import ru.sberbankins.schema.online10.DocumentType;
import ru.sberbankins.schema.online10.InsurerType;
import ru.sberbankins.schema.online10.PaymentType;
import ru.sberbankins.schema.online10.PlanPaymentScheduleType;
import ru.sberbankins.schema.online10.PlanType;
import ru.sberbankins.schema.online10.Registry;

/**
 *
 * @author kkulkov
 */
@BOName("CIBContractIntegration")
public class CIBContractIntegrationFacade extends BaseIntegrationFacade{
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsIntegrationGetCIBContractsData(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Registry policyResponse = getContractsResponse(params);

        String registryXML = this.marshall(policyResponse);
        result.put("registry", registryXML);
        return result;
    }

    private Registry getContractsResponse(Map<String, Object> params) throws Exception {
        Registry result = new Registry();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        
        result.setDate(dateToXMLGC(new Date()));
        List<Map<String, Object>> contractList = (List<Map<String, Object>>) params.get("CONTRACTLIST");
        for (Iterator<Map<String, Object>> it = contractList.iterator(); it.hasNext();) {
            Map<String, Object> contractMap = it.next();
            Map<String, Object> contract = (Map<String, Object>) contractMap.get("CONTRMAP");
            ContractType xmlContract = new ContractType();
            if (contract.get("PREMIUMCURRNUMCODE") != null) {
                xmlContract.setCurrency(contract.get("PREMIUMCURRNUMCODE").toString());
            }
            if (contract.get("STARTDATE") != null) {
                xmlContract.setDateBegin(dateToXMLGC((Date) contract.get("STARTDATE")));
            } else {
                xmlContract.setDateBegin(dateToXMLGC(new Date()));
            }
            if (contract.get("FINISHDATE") != null) {
                xmlContract.setDateEnd(dateToXMLGC((Date) contract.get("FINISHDATE")));
            } else {
                xmlContract.setDateEnd(dateToXMLGC(new Date()));
            }
            if (contract.get("DOCUMENTDATE") != null) {
                xmlContract.setDateSigning(dateToXMLGC((Date) contract.get("DOCUMENTDATE")));
            } else {
                xmlContract.setDateSigning(dateToXMLGC(new Date()));
            }
            if (contract.get("OBJNAME") != null) {
                xmlContract.setInsObject(contract.get("OBJNAME").toString());
            } else {
                xmlContract.setInsObject("");
            }
            if (contract.get("OBJADDRESSTEXT1") != null) {
                xmlContract.setDescriptionInsObject(contract.get("OBJADDRESSTEXT1").toString());
            } else {
                xmlContract.setInsObject("");
            }
            
            if (contract.get("PREMVALUE") != null) {
                xmlContract.setPrem(new BigDecimal(contract.get("PREMVALUE").toString()));
            }
            if (contract.get("INSAMVALUE") != null) {
                xmlContract.setSum(new BigDecimal(contract.get("INSAMVALUE").toString()));
            }
            if (contract.get("CONTRNUMBER") != null) {
                xmlContract.setNumber(contract.get("CONTRNUMBER").toString());
            }
            if (contract.get("SBERPRODCODE") != null) {
                xmlContract.setInsProduct(contract.get("SBERPRODCODE").toString());
            }
            if (contract.get("CONTRID") != null) {
                xmlContract.setIDContract(new Long(contract.get("CONTRID").toString()).intValue());
            }
            xmlContract.setPayment(getContractsPaymentsResponse(contract));
            xmlContract.setPlanPaymentSchedule(getContractsPaymentsScheduleResponse(contract));
            xmlContract.setInsurer(getContractInsurerResponse(contract));
            dsContrMakeTransToUpload(contract, login, password);
            
            result.getContracts().add(xmlContract);
        }
        return result;
    }
/*    private PaymentType getContractsPaymentsResponse(Map<String, Object> params) throws Exception {
      PaymentType xmlPayment = new PaymentType();
      if  (params.get("PAYMENTLIST") != null) {
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
      return  xmlPayment;
    }*/
    /*private PlanPaymentScheduleType getContractsPaymentsScheduleResponse(Map<String, Object> params) throws Exception {
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
      return  xmlPaymentSchedule;
    }
    private InsurerType getContractInsurerResponse(Map<String, Object> params) throws Exception {
        InsurerType xmlInsurer = new InsurerType();
        if (params != null) {
            Map<String,Object> isured = params;
            if (isured.get("INSADDRESSTEXT1") != null) {
                xmlInsurer.setAddress(isured.get("INSADDRESSTEXT1").toString());
            }
            if (isured.get("INSBIRTHDATE") != null) {
                xmlInsurer.setDateOfBirth(dateToXMLGC((Date)isured.get("INSBIRTHDATE")));
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
                xmlInsurer.setTel(isured.get("INSPHONE").toString());
            }
            xmlInsurer.setDocument(getContractInsurerDocumentResponse(isured));
        }
        return xmlInsurer;
    }
        private DocumentType getContractInsurerDocumentResponse(Map<String, Object> params) throws Exception {
            DocumentType xmlDocument = new DocumentType();
            Map<String,Object> insuredDocument = params;
            if (insuredDocument.get("AUTHORITY") != null) {
                xmlDocument.setAuthority(insuredDocument.get("AUTHORITY").toString());
            } else {
                xmlDocument.setAuthority("");
            }
            if (insuredDocument.get("INSISSUERCODE") != null) {
                xmlDocument.setCode(insuredDocument.get("INSISSUERCODE").toString());
            } else {
                xmlDocument.setCode("");
            }
            if (insuredDocument.get("INSISSUEDATE") != null) {
                xmlDocument.setDateOfIssue(dateToXMLGC((Date)insuredDocument.get("INSISSUEDATE")));
            }
            if (insuredDocument.get("INSISSUEDBY") != null) {
                xmlDocument.setInfo(insuredDocument.get("INSISSUEDBY").toString());
            }
            if (insuredDocument.get("INSDOCNUMBER") != null) {
                xmlDocument.setNumber(insuredDocument.get("INSDOCNUMBER").toString());
            }
            if (insuredDocument.get("INSDOCTYPE") != null) {
                xmlDocument.setKind(insuredDocument.get("INSDOCTYPE").toString());
            } else  {
                xmlDocument.setKind("");
            }
            if (insuredDocument.get("INSDOCSERIES") != null) {
                xmlDocument.setSeries(insuredDocument.get("INSDOCSERIES").toString());
            }
            return xmlDocument;
        }*/
    
}
