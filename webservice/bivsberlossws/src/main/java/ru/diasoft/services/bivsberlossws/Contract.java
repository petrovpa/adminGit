/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.diasoft.services.bivsberlossws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author averichevsm
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Contract")
public class Contract {

@XmlElement(name = "isMobile")   private String isMobile;
@XmlElement(name = "protocol")   private String protocol;
@XmlElement(name = "host")   private String host;
@XmlElement(name = "url")   private String url;
@XmlElement(name = "port")   private String port;
@XmlElement(name = "localTZOffset")   private String localTZOffset;
@XmlElement(name = "sessionToken")   private String sessionToken;
@XmlElement(name = "promoCode")   private String promoCode;
@XmlElement(name = "b2bPromoCode")   private String b2bPromoCode;
@XmlElement(name = "did")   private String did;
@XmlElement(name = "dpid")   private String dpid;

    @XmlElement(name = "orderNum")
    private String orderNum;
    @XmlElement(name = "orderId")
    private String orderId;
   // @XmlElement(name = "contrId")
   // private String contrId;  
    @XmlElement(name = "payRes")
    private String payRes;    
    
    @XmlElement(name = "progSysname")
    private String progSysname;
    @XmlElement(name = "prodProgId")
    private String prodProgId;
    @XmlElement(name = "prodConfId")
    private String prodConfId;
    @XmlElement(name = "prodVerId")
    private String prodVerId;
    @XmlElement(name = "insPremVal")
    private String insPremVal;
    @XmlElement(name = "insAmVal")
    private String insAmVal;
    @XmlElement(name = "insGOAmVal")
    private String insGOAmVal;
    @XmlElement(name = "referral")
    private String referral;
    @XmlElement(name = "referralBack")
    private String referralBack;
    @XmlElement(name = "contrSrcParamList")
    private Object contrSrcParamList;
    @XmlElement(name = "insurer")
    private Object insurer;
    @XmlElement(name = "insCitizenship")
    private String insCitizenship;
    
    @XmlElement(name = "insSurname")
    private String insSurname;
    @XmlElement(name = "insName")
    private String insName;
    @XmlElement(name = "insMiddlename")
    private String insMiddlename;
    @XmlElement(name = "insBirthplace")
    private String insBirthplace;
    @XmlElement(name = "insBirthdate")
    private String insBirthdate;
    @XmlElement(name = "insGender")
    private String insGender;
    @XmlElement(name = "insContacts")
    private String insContacts;
    @XmlElement(name = "insPhone")
    private String insPhone;
    @XmlElement(name = "insEmail")
    private String insEmail;
    @XmlElement(name = "insEmailValid")
    private String insEmailValid;

    @XmlElement(name = "insPassPassport")
    private String insPassPassport;
    @XmlElement(name = "insPassDocType")
    private String insPassDocType;
    @XmlElement(name = "insPassSeries")
    private String insPassSeries;
    @XmlElement(name = "insPassNumber")
    private String insPassNumber;
    @XmlElement(name = "insPassIssueDate")
    private String insPassIssueDate;
    @XmlElement(name = "insPassIssuePlace")
    private String insPassIssuePlace;
    @XmlElement(name = "insPassIssueCode")
    private String insPassIssueCode;

    @XmlElement(name = "insAdrAddress")
    private String insAdrAddress;
    @XmlElement(name = "insAdrRegRegion")
    private String insAdrRegRegion;
    @XmlElement(name = "insAdrCountry")
    private String insAdrCountry;
    @XmlElement(name = "insAdrRegNAME")
    private String insAdrRegNAME;
    @XmlElement(name = "insAdrRegCODE")
    private String insAdrRegCODE;

    @XmlElement(name = "insAdrCityCity")
    private String insAdrCityCity;
    @XmlElement(name = "insAdrCityNAME")
    private String insAdrCityNAME;
    @XmlElement(name = "insAdrCityCODE")
    private String insAdrCityCODE;

    @XmlElement(name = "insAdrStrStreet")
    private String insAdrStrStreet;
    @XmlElement(name = "insAdrStrNAME")
    private String insAdrStrNAME;
    @XmlElement(name = "insAdrStrCODE")
    private String insAdrStrCODE;
    @XmlElement(name = "insAdrStrPOSTALCODE")
    private String insAdrStrPOSTALCODE;

    @XmlElement(name = "insAdrHouse")
    private String insAdrHouse;
    @XmlElement(name = "insAdrHousing")
    private String insAdrHousing;
    @XmlElement(name = "insAdrBuilding")
    private String insAdrBuilding;
    @XmlElement(name = "insAdrFlat")
    private String insAdrFlat;
    @XmlElement(name = "insAdrPostalcode")
    private String insAdrPostalcode;
    @XmlElement(name = "insAdrAddressText")
    private String insAdrAddressText;
    //включить B2B режим
    @XmlElement(name = "useB2B")
    private String useB2B;

    @XmlElement(name = "objInsObj")
    private String objInsObj;
    @XmlElement(name = "objTypeId")
    private String objTypeId;
    @XmlElement(name = "objAdrAddress")
    private String objAdrAddress;
    @XmlElement(name = "objAdrCountry")
    private String objAdrCountry;
    @XmlElement(name = "objAdrRegRegion")
    private String objAdrRegRegion;
    @XmlElement(name = "objAdrRegNAME")
    private String objAdrRegNAME;
    @XmlElement(name = "objAdrRegCODE")
    private String objAdrRegCODE;

    @XmlElement(name = "objAdrCityCity")
    private String objAdrCityCity;
    @XmlElement(name = "objAdrCityNAME")
    private String objAdrCityNAME;
    @XmlElement(name = "objAdrCityCODE")
    private String objAdrCityCODE;

    @XmlElement(name = "objAdrStrStreet")
    private String objAdrStrStreet;
    @XmlElement(name = "objAdrStrNAME")
    private String objAdrStrNAME;
    @XmlElement(name = "objAdrStrCODE")
    private String objAdrStrCODE;
    @XmlElement(name = "objAdrStrPOSTALCODE")
    private String objAdrStrPOSTALCODE;

    @XmlElement(name = "objAdrHouse")
    private String objAdrHouse;
    @XmlElement(name = "objAdrHousing")
    private String objAdrHousing;
    @XmlElement(name = "objAdrBuilding")
    private String objAdrBuilding;
    @XmlElement(name = "objAdrFlat")
    private String objAdrFlat;
    @XmlElement(name = "objAdrPostalcode")
    private String objAdrPostalcode;
    @XmlElement(name = "objAdrAddressText")
    private String objAdrAddressText;

    @XmlElement(name = "validate")
    private String validate;

public String getUseB2B() {return useB2B;} public void setUseB2B(String useB2B) {this.useB2B = useB2B;}
public String getIsMobile() {return isMobile;} public void setIsMobile(String isMobile) {this.isMobile = isMobile;}
public String getProtocol() {return protocol;} public void setProtocol(String protocol) {this.protocol = protocol;}
public String getHost() {return host;} public void setHost(String host) {this.host = host;}
public String getUrl() {return url;} public void setUrl(String url) {this.url = url;}
public String getPort() {return port;} public void setPort(String port) {this.port = port;}
public String getLocalTZOffset() {return localTZOffset;} public void setLocalTZOffset(String localTZOffset) {this.localTZOffset = localTZOffset;}
public String getSessionToken() {return sessionToken;} public void setSessionToken(String sessionToken) {this.sessionToken = sessionToken;}
public String getPromoCode() {return promoCode;} public void setPromoCode(String promoCode) {this.promoCode = promoCode;}
public String getB2bPromoCode() {return b2bPromoCode;} public void setB2bPromoCode(String b2bPromoCode) {this.b2bPromoCode = b2bPromoCode;}
public String getDid() {return did;} public void setDid(String did) {this.did = did;}
public String getDpid() {return dpid;} public void setDpid(String dpid) {this.dpid = dpid;}
   
    
    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }   
  //  public String getContrId() {
  //      return contrId;
  //  }

  //  public void setContrId(String contrId) {
  //      this.contrId = contrId;
  //  }     
    public String getPayRes() {
        return payRes;
    }

    public void setPayRes(String payRes) {
        this.payRes = payRes;
    }      
    
    
    
    public String getProgSysname() {
        return progSysname;
    }

    public void setProgSysname(String progSysname) {
        this.progSysname = progSysname;
    }
    public String getProdProgId() {
        return prodProgId;
    }

    public void setProdProgId(String prodProgId) {
        this.prodProgId = prodProgId;
    }

    public String getProdConfId() {
        return prodConfId;
    }

    public void setProdConfId(String prodConfId) {
        this.prodConfId = prodConfId;
    }

    public String getProdVerId() {
        return prodVerId;
    }

    public void setProdVerId(String prodVerId) {
        this.prodVerId = prodVerId;
    }

    public String getInsPremVal() {
        return insPremVal;
    }

    public void setInsPremVal(String insPremVal) {
        this.insPremVal = insPremVal;
    }

    public String getInsAmVal() {
        return insAmVal;
    }

    public void setInsAmVal(String insAmVal) {
        this.insAmVal = insAmVal;
    }

    public String getInsGOAmVal() {
        return insGOAmVal;
    }

    public void setInsGOAmVal(String insGOAmVal) {
        this.insGOAmVal = insGOAmVal;
    }

    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    public String getReferralBack() {
        return referralBack;
    }

    public void setReferralBack(String referralBack) {
        this.referralBack = referralBack;
    }

    public Object getContrSrcParamList() {
        return contrSrcParamList;
    }

    public void setContrSrcParamList(Object contrSrcParamList) {
        this.contrSrcParamList = contrSrcParamList;
    }
    
    public Object getInsurer() {
        return insurer;
    }

    public void setInsurer(Object insurer) {
        this.insurer = insurer;
    }

    public String getInsCitizenship() {
        return insCitizenship;
    }

    public void setInsCitizenship(String insCitizenship) {
        this.insCitizenship = insCitizenship;
    }

    public String getInsSurname() {
        return insSurname;
    }

    public void setInsSurname(String insSurname) {
        this.insSurname = insSurname;
    }

    public String getInsName() {
        return insName;
    }

    public void setInsName(String insName) {
        this.insName = insName;
    }

    public String getInsMiddlename() {
        return insMiddlename;
    }

    public void setInsMiddlename(String insMiddlename) {
        this.insMiddlename = insMiddlename;
    }

    public String getInsBirthplace() {
        return insBirthplace;
    }

    public void setInsBirthplace(String insBirthplace) {
        this.insBirthplace = insBirthplace;
    }

    public String getInsBirthdate() {
        return insBirthdate;
    }

    public void setInsBirthdate(String insBirthdate) {
        this.insBirthdate = insBirthdate;
    }

    public String getInsGender() {
        return insGender;
    }

    public void setInsGender(String insGender) {
        this.insGender = insGender;
    }

    public String getInsContacts() {
        return insContacts;
    }

    public void setInsContacts(String insContacts) {
        this.insContacts = insContacts;
    }

    public String getInsPhone() {
        return insPhone;
    }

    public void setInsPhone(String insPhone) {
        this.insPhone = insPhone;
    }

    public String getInsEmail() {
        return insEmail;
    }

    public void setInsEmail(String insEmail) {
        this.insEmail = insEmail;
    }

    public String getInsEmailValid() {
        return insEmailValid;
    }

    public void setInsEmailValid(String insEmailValid) {
        this.insEmailValid = insEmailValid;
    }

    public String getInsPassDocType() {
        return insPassDocType;
    }

    public void setInsPassDocType(String insPassDocType) {
        this.insPassDocType = insPassDocType;
    }

    public String getInsPassPassport() {
        return insPassPassport;
    }

    public void setInsPassPassport(String insPassPassport) {
        this.insPassPassport = insPassPassport;
    }

    public String getInsPassSeries() {
        return insPassSeries;
    }

    public void setInsPassSeries(String insPassSeries) {
        this.insPassSeries = insPassSeries;
    }

    public String getInsPassNumber() {
        return insPassNumber;
    }

    public void setInsPassNumber(String insPassNumber) {
        this.insPassNumber = insPassNumber;
    }

    public String getInsPassIssueDate() {
        return insPassIssueDate;
    }

    public void setInsPassIssueDate(String insPassIssueDate) {
        this.insPassIssueDate = insPassIssueDate;
    }

    public String getInsPassIssuePlace() {
        return insPassIssuePlace;
    }

    public void setInsPassIssuePlace(String insPassIssuePlace) {
        this.insPassIssuePlace = insPassIssuePlace;
    }

    public String getInsPassIssueCode() {
        return insPassIssueCode;
    }

    public void setInsPassIssueCode(String insPassIssueCode) {
        this.insPassIssueCode = insPassIssueCode;
    }

    public String getInsAdrAddress() {
        return insAdrAddress;
    }

    public void setInsAdrAddress(String insAdrAddress) {
        this.insAdrAddress = insAdrAddress;
    }

    public String getInsAdrCountry() {
        return insAdrCountry;
    }

    public void setInsAdrCountry(String insAdrCountry) {
        this.insAdrCountry = insAdrCountry;
    }

    public String getInsAdrRegRegion() {
        return insAdrRegRegion;
    }

    public void setInsAdrRegRegion(String insAdrRegRegion) {
        this.insAdrRegRegion = insAdrRegRegion;
    }

    public String getInsAdrRegNAME() {
        return insAdrRegNAME;
    }

    public void setInsAdrRegNAME(String insAdrRegNAME) {
        this.insAdrRegNAME = insAdrRegNAME;
    }

    public String getInsAdrRegCODE() {
        return insAdrRegCODE;
    }

    public void setInsAdrRegCODE(String insAdrRegCODE) {
        this.insAdrRegCODE = insAdrRegCODE;
    }

    public String getInsAdrCityCity() {
        return insAdrCityCity;
    }

    public void setInsAdrCityCity(String insAdrCityCity) {
        this.insAdrCityCity = insAdrCityCity;
    }

    public String getInsAdrCityNAME() {
        return insAdrCityNAME;
    }

    public void setInsAdrCityNAME(String insAdrCityNAME) {
        this.insAdrCityNAME = insAdrCityNAME;
    }

    public String getInsAdrCityCODE() {
        return insAdrCityCODE;
    }

    public void setInsAdrCityCODE(String insAdrCityCODE) {
        this.insAdrCityCODE = insAdrCityCODE;
    }

    public String getInsAdrStrStreet() {
        return insAdrStrStreet;
    }

    public void setInsAdrStrStreet(String insAdrStrStreet) {
        this.insAdrStrStreet = insAdrStrStreet;
    }

    public String getInsAdrStrNAME() {
        return insAdrStrNAME;
    }

    public void setInsAdrStrNAME(String insAdrStrNAME) {
        this.insAdrStrNAME = insAdrStrNAME;
    }

    public String getInsAdrStrCODE() {
        return insAdrStrCODE;
    }

    public void setInsAdrStrCODE(String insAdrStrCODE) {
        this.insAdrStrCODE = insAdrStrCODE;
    }

    public String getInsAdrStrPOSTALCODE() {
        return insAdrStrPOSTALCODE;
    }

    public void setInsAdrStrPOSTALCODE(String insAdrStrPOSTALCODE) {
        this.insAdrStrPOSTALCODE = insAdrStrPOSTALCODE;
    }

    public String getInsAdrHouse() {
        return insAdrHouse;
    }

    public void setInsAdrHouse(String insAdrHouse) {
        this.insAdrHouse = insAdrHouse;
    }

    public String getInsAdrHousing() {
        return insAdrHousing;
    }

    public void setInsAdrHousing(String insAdrHousing) {
        this.insAdrHousing = insAdrHousing;
    }

    public String getInsAdrBuilding() {
        return insAdrBuilding;
    }

    public void setInsAdrBuilding(String insAdrBuilding) {
        this.insAdrBuilding = insAdrBuilding;
    }

    public String getInsAdrFlat() {
        return insAdrFlat;
    }

    public void setInsAdrFlat(String insAdrFlat) {
        this.insAdrFlat = insAdrFlat;
    }

    public String getInsAdrPostalcode() {
        return insAdrPostalcode;
    }

    public void setInsAdrPostalcode(String insAdrPostalcode) {
        this.insAdrPostalcode = insAdrPostalcode;
    }

    public String getInsAdrAddressText() {
        return insAdrAddressText;
    }

    public void setInsAdrAddressText(String insAdrAddressText) {
        this.insAdrAddressText = insAdrAddressText;
    }    

    public String getObjInsObj() {
        return objInsObj;
    }

    public void setObjInsObj(String objInsObj) {
        this.objInsObj = objInsObj;
    }

    public String getObjTypeId() {
        return objTypeId;
    }

    public void setObjTypeId(String objTypeId) {
        this.objTypeId = objTypeId;
    }

    public String getObjAdrAddress() {
        return objAdrAddress;
    }

    public void setObjAdrAddress(String objAdrAddress) {
        this.objAdrAddress = objAdrAddress;
    }
    public String getObjAdrCountry() {
        return objAdrCountry;
    }

    public void setObjAdrCountry(String objAdrCountry) {
        this.objAdrCountry = objAdrCountry;
    }

    public String getObjAdrRegRegion() {
        return objAdrRegRegion;
    }

    public void setObjAdrRegRegion(String objAdrRegRegion) {
        this.objAdrRegRegion = objAdrRegRegion;
    }

    public String getObjAdrRegNAME() {
        return objAdrRegNAME;
    }

    public void setObjAdrRegNAME(String objAdrRegNAME) {
        this.objAdrRegNAME = objAdrRegNAME;
    }

    public String getObjAdrRegCODE() {
        return objAdrRegCODE;
    }

    public void setObjAdrRegCODE(String objAdrRegCODE) {
        this.objAdrRegCODE = objAdrRegCODE;
    }

    public String getObjAdrCityCity() {
        return objAdrCityCity;
    }

    public void setObjAdrCityCity(String objAdrCityCity) {
        this.objAdrCityCity = objAdrCityCity;
    }

    public String getObjAdrCityNAME() {
        return objAdrCityNAME;
    }

    public void setObjAdrCityNAME(String objAdrCityNAME) {
        this.objAdrCityNAME = objAdrCityNAME;
    }

    public String getObjAdrCityCODE() {
        return objAdrCityCODE;
    }

    public void setObjAdrCityCODE(String objAdrCityCODE) {
        this.objAdrCityCODE = objAdrCityCODE;
    }

    public String getObjAdrStrStreet() {
        return objAdrStrStreet;
    }

    public void setObjAdrStrStreet(String objAdrStrStreet) {
        this.objAdrStrStreet = objAdrStrStreet;
    }

    public String getObjAdrStrNAME() {
        return objAdrStrNAME;
    }

    public void setObjAdrStrNAME(String objAdrStrNAME) {
        this.objAdrStrNAME = objAdrStrNAME;
    }

    public String getObjAdrStrCODE() {
        return objAdrStrCODE;
    }

    public void setObjAdrStrCODE(String objAdrStrCODE) {
        this.objAdrStrCODE = objAdrStrCODE;
    }

    public String getObjAdrStrPOSTALCODE() {
        return objAdrStrPOSTALCODE;
    }

    public void setObjAdrStrPOSTALCODE(String objAdrStrPOSTALCODE) {
        this.objAdrStrPOSTALCODE = objAdrStrPOSTALCODE;
    }

    public String getObjAdrHouse() {
        return objAdrHouse;
    }

    public void setObjAdrHouse(String objAdrHouse) {
        this.objAdrHouse = objAdrHouse;
    }

    public String getObjAdrHousing() {
        return objAdrHousing;
    }

    public void setObjAdrHousing(String objAdrHousing) {
        this.objAdrHousing = objAdrHousing;
    }

    public String getObjAdrBuilding() {
        return objAdrBuilding;
    }

    public void setObjAdrBuilding(String objAdrBuilding) {
        this.objAdrBuilding = objAdrBuilding;
    }

    public String getObjAdrFlat() {
        return objAdrFlat;
    }

    public void setObjAdrFlat(String objAdrFlat) {
        this.objAdrFlat = objAdrFlat;
    }

    public String getObjAdrPostalcode() {
        return objAdrPostalcode;
    }

    public void setObjAdrPostalcode(String objAdrPostalcode) {
        this.objAdrPostalcode = objAdrPostalcode;
    }

    public String getObjAdrAddressText() {
        return objAdrAddressText;
    }

    public void setObjAdrAddressText(String objAdrAddressText) {
        this.objAdrAddressText = objAdrAddressText;
    }

    public String getValidate() {
        return validate;
    }

    public void setValidate(String validate) {
        this.validate = validate;
    }

    public Map<String, Object> copyContractFromEntityToMap() {
        Map<String, Object> result = new HashMap<String, Object>();
result.put("USEB2B", this.getUseB2B());
result.put("isMobile", this.getIsMobile());
result.put("protocol", this.getProtocol());
result.put("host", this.getHost());
result.put("url", this.getUrl());
result.put("port", this.getPort());
result.put("localTZOffset", this.getLocalTZOffset());
result.put("sessionToken", this.getSessionToken());
result.put("promoCode", this.getPromoCode());
result.put("B2BPROMOCODE", this.getB2bPromoCode());
result.put("did", this.getDid());
result.put("dpid", this.getDpid());
        result.put("orderNum", this.getOrderNum());
        result.put("orderId", this.getOrderId());
    //    result.put("contrId", this.getContrId());
        result.put("payRes", this.getPayRes());        

        result.put("PROGSYSNAME", this.getProgSysname());
        result.put("prodProgId", this.getProdProgId());
        result.put("prodConfId", this.getProdConfId());
        result.put("prodVerId", this.getProdVerId());
        result.put("insPremVal", this.getInsPremVal());
        result.put("insAmVal", this.getInsAmVal());
        result.put("insGOAmVal", this.getInsGOAmVal());
        result.put("REFERRAL", this.getReferral());
        result.put("REFERRALBACK", this.getReferralBack());
        result.put("CONTRSRCPARAMLIST", (List<Map<String, Object>>)this.getContrSrcParamList());
        result.put("insurer", this.getInsurer());
        result.put("insCitizenship", this.getInsCitizenship());
        result.put("insSurname", this.getInsSurname());
        result.put("insName", this.getInsName());
        result.put("insMiddlename", this.getInsMiddlename());
        result.put("insBirthplace", this.getInsBirthplace());
        result.put("insBirthdate", this.getInsBirthdate());
        result.put("insGender", this.getInsGender());
        result.put("insContacts", this.getInsContacts());
        result.put("insPhone", this.getInsPhone());
        result.put("insEmail", this.getInsEmail());
        result.put("insEmailValid", this.getInsEmailValid());

        result.put("insPassPassport", this.getInsPassPassport());
        result.put("insPassDocType", this.getInsPassDocType());
        result.put("insPassSeries", this.getInsPassSeries());
        result.put("insPassNumber", this.getInsPassNumber());
        result.put("insPassIssueDate", this.getInsPassIssueDate());
        result.put("insPassIssuePlace", this.getInsPassIssuePlace());
        result.put("insPassIssueCode", this.getInsPassIssueCode());

        result.put("insAdrAddress", this.getInsAdrAddress());
        result.put("insAdrCountry", this.getInsAdrCountry());
        result.put("insAdrRegRegion", this.getInsAdrRegRegion());
        result.put("insAdrRegNAME", this.getInsAdrRegNAME());
        result.put("insAdrRegCODE", this.getInsAdrRegCODE());

        result.put("insAdrCityCity", this.getInsAdrCityCity());
        result.put("insAdrCityNAME", this.getInsAdrCityNAME());
        result.put("insAdrCityCODE", this.getInsAdrCityCODE());

        result.put("insAdrStrStreet", this.getInsAdrStrStreet());
        result.put("insAdrStrNAME", this.getInsAdrStrNAME());
        result.put("insAdrStrCODE", this.getInsAdrStrCODE());
        result.put("insAdrStrPOSTALCODE", this.getInsAdrStrPOSTALCODE());

        result.put("insAdrHouse", this.getInsAdrHouse());
        result.put("insAdrHousing", this.getInsAdrHousing());
        result.put("insAdrBuilding", this.getInsAdrBuilding());
        result.put("insAdrFlat", this.getInsAdrFlat());
        result.put("insAdrPostalcode", this.getInsAdrPostalcode());
        result.put("insAdrAddressText", this.getInsAdrAddressText());                

        result.put("objInsObj", this.getObjInsObj());
        result.put("objTypeId", this.getObjTypeId());
        result.put("objAdrAddress", this.getObjAdrAddress());
        result.put("objAdrCountry", this.getObjAdrCountry());
        result.put("objAdrRegRegion", this.getObjAdrRegRegion());
        result.put("objAdrRegNAME", this.getObjAdrRegNAME());
        result.put("objAdrRegCODE", this.getObjAdrRegCODE());

        result.put("objAdrCityCity", this.getObjAdrCityCity());
        result.put("objAdrCityNAME", this.getObjAdrCityNAME());
        result.put("objAdrCityCODE", this.getObjAdrCityCODE());

        result.put("objAdrStrStreet", this.getObjAdrStrStreet());
        result.put("objAdrStrNAME", this.getObjAdrStrNAME());
        result.put("objAdrStrCODE", this.getObjAdrStrCODE());
        result.put("objAdrStrPOSTALCODE", this.getObjAdrStrPOSTALCODE());

        result.put("objAdrHouse", this.getObjAdrHouse());
        result.put("objAdrHousing", this.getObjAdrHousing());
        result.put("objAdrBuilding", this.getObjAdrBuilding());
        result.put("objAdrFlat", this.getObjAdrFlat());
        result.put("objAdrPostalcode", this.getObjAdrPostalcode());
        result.put("objAdrAddressText", this.getObjAdrAddressText());

        result.put("Validate", this.getValidate());
        return result;
    }
}
