/*
 * Copyright (c) Diasoft 2004-2011
 */
package com.bivgroup.services.b2bposws.facade.pos.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.services.inscore.util.TranslitStringConverter;

/**
 * Кладр
 *
 * @author aklunok
 *
 * СС РРР ГГГ ППП КК, где
 *
 * СС – код субъекта Российской Федерации (региона); РРР – код района; ГГГ – код
 * города; ППП – код населенного пункта; КК – код актуальности наименования.
 * Структуру кодового обозначения в файле Street.dbf можно представить в
 * следующем виде:
 *
 * СС РРР ГГГ ППП УУУУ КК, где
 *
 * СС – код субъекта Российской Федерации (региона); РРР – код района; ГГГ – код
 * города; ППП – код населенного пункта; УУУУ – код улицы; КК – код актуальности
 * наименования.
 *
 * (класс скопирован из
 * ru.diasoft.services.insposws.facade.pos.custom.KladrCustomFacade и дополнен
 * методом dsGenFullTextAddress)
 *
 */
@BOName("KladrCustom")
public class KladrCustomFacade extends B2BBaseFacade {

    private static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;
    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;

    //<editor-fold defaultstate="collapsed" desc="скопировано без изменений из ru.diasoft.services.insposws.facade.pos.custom.KladrCustomFacade">
    
    /**
     * Получить список адресов по параметрам
     *
     * @author aklunok
     * @param params
     * <UL>
     * <LI>CODE - код КЛАДР</LI>
     * <LI>NAME - наименование населенного пункта по КЛАДР</LI>
     * <LI>KLADROBJID - идентификатор КЛАДР</LI>
     * <LI>REGIONCODE - ограничение по региону</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>KLADROBJID - идентификатор КЛАДР</LI>
     * <LI>CODE - код КЛАДР</LI>
     * <LI>NAME - наименование по КЛАДР</LI>
     * <LI>FULLNAME - наименование по КЛАДР с типом</LI>
     * <LI>TYPE - наименование типа КЛАДР</LI>
     * <LI>REGIONCODE - код региона по КЛАДР</LI>
     * <LI>REGIONNAME - наименование региона КЛАДР</LI>
     * <LI>REGIONFULLNAME - наименование региона КЛАДР с типом</LI>
     * <LI>REGIONTYPE - наименование типа региона КЛАДР</LI>
     * <LI>REGIONTYPECODE - код типа региона КЛАДР</LI>
     * <LI>ZONECODE - код района по КЛАДР</LI>
     * <LI>ZONENAME - наименование района по КЛАДР</LI>
     * <LI>ZONEFULLNAME - наименование района КЛАДР с типом</LI>
     * <LI>ZONETYPE - наименование типа района КЛАДР</LI>
     * <LI>ZONETYPECODE - код типа района КЛАДР</LI>
     * <LI>CITYCODE - код города по КЛАДР</LI>
     * <LI>CITYNAME - наименование города по КЛАДР</LI>
     * <LI>CITYFULLNAME - наименование города КЛАДР с типом</LI>
     * <LI>CITYTYPE - наименование типа города КЛАДР</LI>
     * <LI>CITYTYPECODE - код типа города КЛАДР</LI>
     * <LI>PLACECODE - код места по КЛАДР</LI>
     * <LI>PLACENAME - наименование места по КЛАДР</LI>
     * <LI>PLACEFULLNAME - наименование места КЛАДР с типом</LI>
     * <LI>PLACETYPE - наименование типа места КЛАДР</LI>
     * <LI>PLACETYPECODE - код типа места КЛАДР</LI>
     * </UL>
     */
    @WsMethod
    public Map<String, Object> dsKladrBrowseListByParam(Map<String, Object> params) throws Exception {

        if (params.get("ORDERBY") == null) {
            params.put("ORDERBY", "REGIONFULLNAME, ZONEFULLNAME ,CITYCODE, CITYFULLNAME, PLACEORDERNAME");
        }
        // Корректировка ограничения по коду региона
        if (params.get("REGIONCODE") != null) {
            String regionCode = params.get("REGIONCODE").toString();
            if (regionCode.length() >= 2) {
                params.put("REGIONCODE", String.format("%s%%", regionCode.substring(0, 2)));
            } else {
                params.put("REGIONCODE", null);
            }

        }
        Map<String, Object> result = this.selectQuery("dsKladrBrowseListByParam", "dsKladrBrowseListByParamCount", params);
        if (params.get("SETMOSCOWFIRST") != null) {
            if ("TRUE".equalsIgnoreCase(params.get("SETMOSCOWFIRST").toString())) {
                List<Map<String, Object>> resList = WsUtils.getListFromResultMap(result);
                setMoscowFirst(resList);
                result.put(RESULT, resList);
            }
        }

        return result;
    }

    /**
     * Получить список регионов по параметрам
     *
     * @author aklunok
     * @param params
     * <UL>
     * <LI>CODE - код КЛАДР</LI>
     * <LI>NAME - наименование населенного пункта по КЛАДР</LI>
     * <LI>KLADROBJID - идентификатор КЛАДР</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>KLADROBJID - идентификатор КЛАДР</LI>
     * <LI>CODE - код КЛАДР</LI>
     * <LI>NAME - наименование по КЛАДР</LI>
     * <LI>FULLNAME - наименование по КЛАДР с типом</LI>
     * <LI>TYPE - наименование типа КЛАДР</LI>
     * </UL>
     */
    @WsMethod
    public Map<String, Object> dsKladrRegionBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsKladrRegionBrowseListByParam", "dsKladrRegionBrowseListByParamCount", params);
        if (params.get("SETMOSCOWFIRST") != null) {
            if ("TRUE".equalsIgnoreCase(params.get("SETMOSCOWFIRST").toString())) {
                List<Map<String, Object>> resList = WsUtils.getListFromResultMap(result);
                setMoscowFirst(resList);
                result.put(RESULT, resList);
            }
        }
        return result;

    }

    /**
     * Получить список улиц по параметрам
     *
     * @author aklunok
     * @param params
     * <UL>
     * <LI>CODE - код КЛАДР</LI>
     * <LI>NAME - наименование улицы по КЛАДР</LI>
     * <LI>KLADROBJID - идентификатор КЛАДР</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>KLADROBJID - идентификатор КЛАДР</LI>
     * <LI>CODE - код КЛАДР</LI>
     * <LI>NAME - наименование по КЛАДР</LI>
     * <LI>FULLNAME - наименование по КЛАДР с типом</LI>
     * <LI>TYPE - наименование типа КЛАДР</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"KLADRCODE"})
    public Map<String, Object> dsKladrStreetBrowseListByParam(Map<String, Object> params) throws Exception {
        if (params.get("ORDERBY") == null) {
            params.put("ORDERBY", "FULLNAME ASC");
        }
        Map<String, Object> result = this.selectQuery("dsKladrStreetBrowseListByParam", "dsKladrStreetBrowseListByParamCount", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsKladrStreetBrowseListByParamNoReq(Map<String, Object> params) throws Exception {
        if (params.get("ORDERBY") == null) {
            params.put("ORDERBY", "FULLNAME ASC");
        }
        Map<String, Object> result = this.selectQuery("dsKladrStreetBrowseListByParam", "dsKladrStreetBrowseListByParamCount", params);
        return result;
    }
    
    /**
     * Получить список домов по параметрам
     *
     * @author aklunok
     * @param params
     * <UL>
     * <LI>CODE - код КЛАДР</LI>
     * <LI>NAME - наименование улицы по КЛАДР</LI>
     * <LI>KLADROBJID - идентификатор КЛАДР</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>KLADROBJID - идентификатор КЛАДР</LI>
     * <LI>CODE - код КЛАДР</LI>
     * <LI>NAME - наименование по КЛАДР</LI>
     * <LI>FULLNAME - наименование по КЛАДР с типом</LI>
     * <LI>TYPE - наименование типа КЛАДР</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"KLADRCODE"})
    public Map<String, Object> dsKladrHouseBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();

        Map<String, Object> qRes = this.selectQuery("dsKladrHouseBrowseListByParam", "", params);
        if ((qRes.get(RESULT) != null) && (((List) qRes.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> houseList = (List<Map<String, Object>>) qRes.get(RESULT);
            for (Map<String, Object> map : houseList) {
                String houseStrList = map.get("HOUSENUMBER").toString();
                if (houseStrList != null) {
                    String[] houseArr = houseStrList.split(",");
                    for (int i = 0; i < houseArr.length; i++) {
                        Map<String, Object> houseOne = new HashMap<String, Object>();
                        houseOne.put("KLADRHOUSEID", map.get("HOUSENUMBER"));
                        houseOne.put("HOUSENUMBER", houseArr[i].toString());
                        houseOne.put("POSTALCODE", map.get("POSTALCODE"));
                        resList.add(houseOne);
                    }
                }
            }

        }
        result.put(RESULT, resList);
        return result;
    }

 

    public String getFullKladrNameByCode(Map<String, Object> params, String socrCodeName, String code, String fullName, List<Map<String, Object>> adrSocr) throws Exception {
        Map<String, Object> qParam = new HashMap<String, Object>();
        qParam.put("CODE", code);
        int level = getLevelKladrByCode(code);
        Map<String, Object> qres = null;
        if (code.length() > 13) {
            qres = this.selectQuery("dsKladrStreetBrowseListByParam", "", qParam);
        } else {
            qParam.put("LEVEL", level);
            qres = this.selectQuery("dsGetKladrByCode", "", qParam);
            //qres = this.selectQuery("dsKladrBrowseListByParam", "", qParam);            
        }
        if (qres.get(RESULT) != null) {
            List<Map<String, Object>> kladrList = (List<Map<String, Object>>) qres.get(RESULT);
            qres = kladrList.get(0);
            /*  if (qres.get("NAME") != null) {
             qres.put("FULLNAME", qres.get("NAME"));
             }*/
        }
        String result = "";
        String socr = "";
        if (qres.get("CODE") != null) {
            fullName = qres.get("NAME").toString();
            // устанавливаем стандартные правила формирования полной строки по уровням
            // флаг - нужна точка после сокращения          
            boolean isNeedDot = false;
            // флаг - сокращение пишется справа, от наименования
            boolean isRightSocr = false;
            switch (level) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    isNeedDot = true;
                    break;
                case 5:
                    isNeedDot = true;
                    break;
            }

            if (qres.get("TYPE") != null) {
                String shortName = qres.get("TYPE").toString();
                params.put(socrCodeName, qres.get("TYPECODE"));
                socr = shortName;
                List<Map<String, Object>> adrSocrFilterList = CopyUtils.filterSortedListByStringFieldName(adrSocr, "briefName", shortName);
                if (!adrSocrFilterList.isEmpty()) {
                    Map<String, Object> adrSocrMap = adrSocrFilterList.get(0);
                    if ("1".equals(adrSocrMap.get("dotExist").toString())) {
                        isNeedDot = true;
                    } else {
                        isNeedDot = false;
                    }
                    if ("1".equals(adrSocrMap.get("socrRight").toString())) {
                        isRightSocr = true;
                    } else {
                        isRightSocr = false;
                    }
                }
            }
            if (isNeedDot) {
                socr = socr + ".";
            }
            if (isRightSocr) {
                result = fullName + " " + socr;
            } else {
                result = socr + " " + fullName;
            }
        }
        return result;
    }

    private String appendKladrPart(StringBuilder sb, Map<String, Object> param, String socrCodeName, String prevKladrStr, String kladrCode, String fullName, List<Map<String, Object>> adrSocr) throws Exception {
        String result = fullName;
        if (!kladrCode.isEmpty()) {
            result = getFullKladrNameByCode(param, socrCodeName, kladrCode, fullName, adrSocr);
        }
        if (!result.isEmpty()) {
            if (!prevKladrStr.equals(result)) {
                //    if (!prevKladrStr.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                //    }
                sb.append(result);
            }
        } else {
            result = prevKladrStr;
        }
        return result;
    }

    @WsMethod
    public Map<String, Object> dsB2BGenFullAddressMap(Map<String, Object> params) throws Exception {
        
        logger.debug("Generating full address map (dsB2BGenFullAddressMap)...");
        
        Map<String, Object> result = new HashMap<String, Object>();
        StringBuilder fullAddress = new StringBuilder();
        Map<String, Object> addressMap = (Map<String, Object>) params.get("ADDRESSMAP");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        result.put("DISTRICT", "");
        result.put("DISTRICTKLADR", "");
        result.put("DISTRICTTYPE", 0L);
        result.put("CITY", "");
        result.put("CITYKLADR", "");
        result.put("CITYTYPE", 0L);
        result.put("VILLAGE", "");
        result.put("VILLAGEKLADR", "");
        result.put("VILLAGETYPE", 0L);

        Map<String, Object> qres = null;
        if (addressMap.get("CALCVERID") != null) {
            if (!addressMap.get("CALCVERID").toString().isEmpty()) {
                Long calcVerId = Long.valueOf(addressMap.get("CALCVERID").toString());
                Map<String, Object> queryParams = new HashMap<String, Object>();
                queryParams.put("CALCVERID", calcVerId);
                queryParams.put("NAME", "Common.AddressSocr");
                qres = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", queryParams, login, password);
            }
        }
        if (qres == null) {
            Map<String, Object> sysParam = new HashMap<String, Object>();
            sysParam.put("SETTINGSYSNAME", "addressSocrDataVer");
            sysParam.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> sysRes = this.callService(COREWS, "getSysSettingBySysName", sysParam, login, password);
            if ((sysRes != null) && (sysRes.get("SETTINGVALUE") != null)) {
                Map<String, Object> extParams = new HashMap<String, Object>();
                extParams.put("HBDATAVERID", sysRes.get("SETTINGVALUE"));
                qres = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordBrowseListByParam", extParams, login, password);

            }

        }

        if ((qres != null) && (qres.get(RESULT) != null) && (!qres.get(RESULT).toString().equalsIgnoreCase("{}")) && (((List) qres.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> adrSocr = (List<Map<String, Object>>) qres.get(RESULT);
            CopyUtils.sortByStringFieldName(adrSocr, "briefName");
            String countryStr = getStringParam(addressMap.get("eCountry"));
            String countryStrInAddr = countryStr;
            if ((!countryStr.isEmpty()) && (!countryStr.equalsIgnoreCase("россия"))) {
                fullAddress.append(countryStr);
            } else {
                countryStrInAddr = "";
            }
            String regionCode = getStringParam(addressMap.get("regionCode"));
            String cityCode = getStringParam(addressMap.get("cityCode"));
            String streetCode = getStringParam(addressMap.get("streetCode"));
            String streetTypeCode = getStringParam(addressMap.get("streetTypeCode"));
            String regionStr = getStringParam(addressMap.get("eRegion"));
            String zoneStr = "";
            String cityStr = getStringParam(addressMap.get("eCity"));
            String villageStr = "";
            String streetStr = getStringParam(addressMap.get("eStreet"));
            regionStr = appendKladrPart(fullAddress, addressMap, "regionTypeCode", countryStrInAddr, regionCode, regionStr, adrSocr);
            Map<String, Object> kladrParams = new HashMap<String, Object>();
            if (!cityCode.isEmpty()) {
                kladrParams.put("CODE", cityCode);
                kladrParams.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> kladrRes = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrBrowseListByParam", kladrParams, login, password);

                result.put("DISTRICT", getStringParam(kladrRes.get("ZONENAME")));
                result.put("DISTRICTKLADR", getStringParam(kladrRes.get("ZONECODE")));
                result.put("DISTRICTTYPE", getLongParam(kladrRes.get("ZONETYPECODE")));
                result.put("CITY", getStringParam(kladrRes.get("CITYNAME")));
                result.put("CITYKLADR", getStringParam(kladrRes.get("CITYCODE")));
                result.put("CITYTYPE", getLongParam(kladrRes.get("CITYTYPECODE")));
                result.put("VILLAGE", getStringParam(kladrRes.get("PLACENAME")));
                result.put("VILLAGEKLADR", getStringParam(kladrRes.get("PLACECODE")));
                result.put("VILLAGETYPE", getLongParam(kladrRes.get("PLACETYPECODE")));
                zoneStr = appendKladrPart(fullAddress, addressMap, "zoneTypeCode", regionStr, getStringParam(kladrRes.get("ZONECODE")), getStringParam(kladrRes.get("ZONENAME")), adrSocr);
                cityStr = appendKladrPart(fullAddress, addressMap, "cityTypeCode", zoneStr, getStringParam(kladrRes.get("CITYCODE")), getStringParam(kladrRes.get("CITYNAME")), adrSocr);
                villageStr = appendKladrPart(fullAddress, addressMap, "villageTypeCode", cityStr, getStringParam(kladrRes.get("PLACECODE")), getStringParam(kladrRes.get("PLACENAME")), adrSocr);

            } else {
                result.put("CITY", cityStr);
                cityStr = appendKladrPart(fullAddress, addressMap, "cityTypeCode", regionStr, cityCode, cityStr, adrSocr);
                villageStr = cityStr;
            }
            streetStr = appendKladrPart(fullAddress, addressMap, "streetTypeCode", villageStr, streetCode, streetStr, adrSocr);
            String houseStr = appendPart(fullAddress, streetStr, addressMap, adrSocr, "", "eHouse");
            String corpusStr = appendPart(fullAddress, houseStr, addressMap, adrSocr, "корп. ", "eCorpus");
            String buildStr = appendPart(fullAddress, corpusStr, addressMap, adrSocr, "стр. ", "eBuilding");

            // анализ переданного в генерацию адресов типа участника для выбора между 'кв. ' и 'оф. '
            String flatPrefix = "кв. "; // по умолчанию всегда используется 'кв. ' 
            Long participantType = getLongParam(addressMap, "PARTICIPANTTYPE");
            logger.debug("Participant type (PARTICIPANTTYPE): " + participantType);
            if ((participantType != null) && (participantType == 2L)) {
                // если тип участника ЮЛ - вместо 'кв. ' применяется 'оф. '
                flatPrefix = "оф. ";
                logger.debug(String.format("Participant is legal entity - the office prefix ('%s') will be used.", flatPrefix));
            } else {
                logger.debug(String.format("Participant unknown or is not legal entity - the flat prefix ('%s') will be used.", flatPrefix));
            }
            
            String flatStr = appendPart(fullAddress, buildStr, addressMap, adrSocr, flatPrefix, "eFlat");

            if (fullAddress.length() >= 255) {
                fullAddress.delete(255, fullAddress.length());
            }

            String fullAddressStr = fullAddress.toString();
            logger.debug("Generated full address primary string: " + fullAddressStr);
            
            result.put("ADDRESSTEXT1", getStringParam(addressMap.get("eIndex")) + ", " + fullAddressStr);
            result.put("ADDRESSTEXT2", fullAddressStr);
            result.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(fullAddressStr));

            result.put("ADDRESSTYPESYSNAME", getStringParam(addressMap.get("addrSysName")));
            result.put("COUNTRY", countryStr);
            result.put("REGION", getStringParam(addressMap.get("eRegion")));
            result.put("REGIONKLADR", regionCode);
            result.put("REGIONTYPE", getLongParam(addressMap.get("regionTypeCode")));

            result.put("STREETKLADR", getStringParam(addressMap.get("streetCode")));
            result.put("STREET", getStringParam(addressMap.get("eStreet")));
            result.put("STREETTYPE", getLongParam(addressMap.get("streetTypeCode")));

            result.put("STREETTYPE_FULL", getStringParam(addressMap.get("eStreetType")));

            if (getBooleanParam(addressMap.get("cbNoKladrAddress"))) {
                result.put("USEKLADR", 0);
            } else {
                result.put("USEKLADR", 1);
            }

            result.put("POSTALCODE", getStringParam(addressMap.get("eIndex")));
            result.put("HOUSE", getStringParam(addressMap.get("eHouse")));
            result.put("HOUSING", getStringParam(addressMap.get("eBuilding")));
            result.put("BUILDING", getStringParam(addressMap.get("eCorpus")));
            result.put("FLAT", getStringParam(addressMap.get("eFlat")));
            result.put("PRIORITY", 0);
            if ((addressMap.get("ADDRESSID") != null) && (!addressMap.get("ADDRESSID").toString().isEmpty()) && (Long.valueOf(addressMap.get("ADDRESSID").toString()).longValue() > 0)) {
                result.put("ADDRESSID", addressMap.get("ADDRESSID"));
            }
        }

        logger.debug("Generating full address map (dsB2BGenFullAddressMap) finished with result: " + result);
        
        return result;
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

    private int getLevelKladrByCode(String code) {
        int count = code.length();
        if (code.substring(2).equals("00000000000")) {
            return 1;
        }
        if (code.substring(5).equals("00000000")) {
            return 2;
        }
        if (code.substring(8).equals("00000")) {
            return 3;
        }
        if (code.substring(11).equals("00")) {
            return 4;
        }
        if (count > 13) {
            return 5;
        }
        return 4;
    }

    private String appendPart(StringBuilder fullAddress, String prevAddrStr, Map<String, Object> params, List<Map<String, Object>> adrSocr, String socr, String fieldName) {
        String result = "";
        if (params.get(fieldName) != null) {
            String value = params.get(fieldName).toString();
            value = value.trim();
            if (!value.isEmpty()) {
                result = socr + value;
                if (!result.isEmpty()) {
                    if (fullAddress.length() > 0) {
                        // if (!prevAddrStr.isEmpty()) {
                        fullAddress.append(", ");
                        // }
                    }
                }
                fullAddress.append(result);
            }
        }
        return result;
    }

    private void setMoscowFirst(List<Map<String, Object>> resList) {
        boolean isMoscow = false;
        boolean isMoscowRegion = false;
        boolean isSPB = false;
        int index = 0;
        Map<String, Object> moscow = null;
        Map<String, Object> moscowRegion = null;
        Map<String, Object> spb = null;

        for (Map<String, Object> bean : resList) {
            if ("7700000000000".equalsIgnoreCase(bean.get("CODE").toString())) {
                if (!isMoscow) {
                    isMoscow = true;
                    moscow = bean;
                  //  resList.remove(bean);
                    //  resList.add(0,moscow);
                }
            }
            if ("7800000000000".equalsIgnoreCase(bean.get("CODE").toString())) {
                if (!isSPB) {
                    index = 0;
                    if ((isMoscow) || (isMoscowRegion)) {
                        index = 1;
                    }
                    if (isMoscowRegion && isMoscow) {
                        index = 2;
                    }
                    spb = bean;
                   // resList.remove(bean);
                    //  resList.add(index,spb);
                    isSPB = true;
                }
            }
            if ("5000000000000".equalsIgnoreCase(bean.get("CODE").toString())) {
                if (!isMoscowRegion) {
                    index = 0;
                    if ((isMoscow)) {
                        index = 1;
                    }
                    moscowRegion = bean;
                   // resList.remove(bean);
                    // resList.add(index,moscowRegion);                       
                    isMoscowRegion = true;
                }
            }
        }
        if (isSPB) {
            resList.remove(spb);
            resList.add(0, spb);
        }
        if (isMoscowRegion) {
            resList.remove(moscowRegion);
            resList.add(0, moscowRegion);
        }
        if (isMoscow) {
            resList.remove(moscow);
            resList.add(0, moscow);
        }
    }

    @WsMethod
    public Map<String, Object> dsKladrFindByCodeOrName(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsKladrFindByCodeOrName", null, params);
        return result;
    }
    //</editor-fold>
    
    // на основе dsGenFullAddressMap
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsGenFullTextAddress(Map<String, Object> params) throws Exception {
        
        logger.debug("Generating full text address string (dsGenFullTextAddress)...");
        
        Map<String, Object> result = new HashMap<String, Object>();
        StringBuilder fullAddress = new StringBuilder();
        Map<String, Object> addressMap = params; // (Map<String, Object>) params.get("ADDRESSMAP");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        //
        //result.put("DISTRICT", "");
        //result.put("DISTRICTKLADR", "");
        //result.put("DISTRICTTYPE", 0L);
        //result.put("CITY", "");
        //result.put("CITYKLADR", "");
        //result.put("CITYTYPE", 0L);
        //result.put("VILLAGE", "");
        //result.put("VILLAGEKLADR", "");
        //result.put("VILLAGETYPE", 0L);
        //
        //Map<String, Object> qres = null;
        //if (addressMap.get("CALCVERID") != null) {
        //    if (!addressMap.get("CALCVERID").toString().isEmpty()) {
        //        Long calcVerId = Long.valueOf(addressMap.get("CALCVERID").toString());
        //        Map<String, Object> queryParams = new HashMap<String, Object>();
        //        queryParams.put("CALCVERID", calcVerId);
        //        queryParams.put("NAME", "addressSocr");
        //        qres = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", queryParams, login, password);
        //    }
        //}
        //if (qres == null) {
        //    Map<String, Object> sysParam = new HashMap<String, Object>();
        //    sysParam.put("SETTINGSYSNAME", "addressSocrDataVer");
        //    sysParam.put("ReturnAsHashMap", "TRUE");
        //    Map<String, Object> sysRes = this.callService(COREWS, "getSysSettingBySysName", sysParam, login, password);
        //    if ((sysRes != null) && (sysRes.get("SETTINGVALUE") != null)) {
        //        Map<String, Object> extParams = new HashMap<String, Object>();
        //        extParams.put("HBDATAVERID", sysRes.get("SETTINGVALUE"));
        //        qres = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordBrowseListByParam", extParams, login, password);
        //    }
        //}
        //
        // безусловное получение данных по сокращениям через getSysSettingBySysName + dsHandbookRecordBrowseListByParam
        // (CALCVERID заведомо отсутствует во входных параметрах)
        Map<String, Object> qres = null;
        Map<String, Object> sysParam = new HashMap<String, Object>();
        sysParam.put("SETTINGSYSNAME", "addressSocrDataVer");
        sysParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> sysRes = this.callService(COREWS, "getSysSettingBySysName", sysParam, login, password);
        if ((sysRes != null) && (sysRes.get("SETTINGVALUE") != null)) {
            Map<String, Object> extParams = new HashMap<String, Object>();
            extParams.put("HBDATAVERID", sysRes.get("SETTINGVALUE"));
            qres = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordBrowseListByParam", extParams, login, password);
        }
        
        if ((qres != null) && (qres.get(RESULT) != null) && (!qres.get(RESULT).toString().equalsIgnoreCase("{}")) && (((List) qres.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> adrSocr = (List<Map<String, Object>>) qres.get(RESULT);
            CopyUtils.sortByStringFieldName(adrSocr, "briefName");
            String countryStr = getStringParam(addressMap.get("COUNTRY")); // getStringParam(addressMap.get("eCountry"));
            String countryStrInAddr = countryStr;
            if ((!countryStr.isEmpty()) && (!countryStr.equalsIgnoreCase("россия"))) {
                fullAddress.append(countryStr);
            } else {
                countryStrInAddr = "";
            }
            String regionCode = getStringParam(addressMap.get("REGIONKLADR")); // getStringParam(addressMap.get("regionCode"));
            String cityCode = getStringParam(addressMap.get("CITYKLADR")); // getStringParam(addressMap.get("cityCode"));
            String streetCode = getStringParam(addressMap.get("STREETKLADR")); // getStringParam(addressMap.get("streetCode"));
            String streetTypeCode = getStringParam(addressMap.get("STREETTYPE")); // getStringParam(addressMap.get("streetTypeCode"));
            String regionStr = getStringParam(addressMap.get("REGION")); // getStringParam(addressMap.get("eRegion"));
            String zoneStr = "";
            String cityStr = getStringParam(addressMap.get("CITY")); // getStringParam(addressMap.get("eCity"));
            String villageStr = "";
            String streetStr = getStringParam(addressMap.get("STREET")); // getStringParam(addressMap.get("eStreet"));
            regionStr = appendKladrPart(fullAddress, addressMap, "regionTypeCode", countryStrInAddr, regionCode, regionStr, adrSocr);
            Map<String, Object> kladrParams = new HashMap<String, Object>();
            if (!cityCode.isEmpty()) {
                kladrParams.put("CODE", cityCode);
                kladrParams.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> kladrRes = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrBrowseListByParam", kladrParams, login, password);

                //result.put("DISTRICT", getStringParam(kladrRes.get("ZONENAME")));
                //result.put("DISTRICTKLADR", getStringParam(kladrRes.get("ZONECODE")));
                //result.put("DISTRICTTYPE", getLongParam(kladrRes.get("ZONETYPECODE")));
                //result.put("CITY", getStringParam(kladrRes.get("CITYNAME")));
                //result.put("CITYKLADR", getStringParam(kladrRes.get("CITYCODE")));
                //result.put("CITYTYPE", getLongParam(kladrRes.get("CITYTYPECODE")));
                //result.put("VILLAGE", getStringParam(kladrRes.get("PLACENAME")));
                //result.put("VILLAGEKLADR", getStringParam(kladrRes.get("PLACECODE")));
                //result.put("VILLAGETYPE", getLongParam(kladrRes.get("PLACETYPECODE")));
                zoneStr = appendKladrPart(fullAddress, addressMap, "zoneTypeCode", regionStr, getStringParam(kladrRes.get("ZONECODE")), getStringParam(kladrRes.get("ZONENAME")), adrSocr);
                cityStr = appendKladrPart(fullAddress, addressMap, "cityTypeCode", zoneStr, getStringParam(kladrRes.get("CITYCODE")), getStringParam(kladrRes.get("CITYNAME")), adrSocr);
                villageStr = appendKladrPart(fullAddress, addressMap, "villageTypeCode", cityStr, getStringParam(kladrRes.get("PLACECODE")), getStringParam(kladrRes.get("PLACENAME")), adrSocr);

            } else {
                //result.put("CITY", cityStr);
                cityStr = appendKladrPart(fullAddress, addressMap, "cityTypeCode", regionStr, cityCode, cityStr, adrSocr);
                villageStr = cityStr;
            }
            streetStr = appendKladrPart(fullAddress, addressMap, "streetTypeCode", villageStr, streetCode, streetStr, adrSocr);
            // требование по адресу от 17.03.16 убрать автоподстановку "д. " в поле Дом.
            String houseStr = appendPart(fullAddress, streetStr, addressMap, adrSocr, "", "HOUSE"); // , "eHouse"); 
            String corpusStr = appendPart(fullAddress, houseStr, addressMap, adrSocr, "корп. ", "HOUSING"); // , "eCorpus");
            String buildStr = appendPart(fullAddress, corpusStr, addressMap, adrSocr, "стр. ", "BUILDING"); // , "eBuilding");
            
            // анализ переданного в генерацию адресов типа участника для выбора между 'кв. ' и 'оф. '
            String flatPrefix = "кв. "; // по умолчанию всегда используется 'кв. ' 
            Long participantType = getLongParam(addressMap, "PARTICIPANTTYPE");
            logger.debug("Participant type (PARTICIPANTTYPE): " + participantType);
            if (participantType != null) {
                if (participantType.equals(2L)) {                
                    // если тип участника ЮЛ - вместо 'кв. ' применяется 'оф. '
                    flatPrefix = "оф. ";
                    logger.debug(String.format("Partisipant is legal entity - the office prefix ('%s') will be used.", flatPrefix));
                } else {
                    logger.debug(String.format("Partisipant unknown or is not legal entity - the flat prefix ('%s') will be used.", flatPrefix));
                }
            }   
            String flatStr = appendPart(fullAddress, buildStr, addressMap, adrSocr, flatPrefix, "FLAT"); // , "eFlat");

            if (fullAddress.length() >= 255) {
                fullAddress.delete(255, fullAddress.length());
            }
            
            String fullAddressStr = fullAddress.toString();
            logger.debug("Generated full address primary string: " + fullAddressStr);
            
            result.put("ADDRESSTEXT1", /*getStringParam(addressMap.get("eIndex")) + ", " + */ fullAddressStr);
            result.put("ADDRESSTEXT2", fullAddressStr);
            result.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(fullAddressStr));
            //result.put("ADDRESSTYPESYSNAME", getStringParam(addressMap.get("addrSysName")));
            //result.put("COUNTRY", countryStr);
            //result.put("REGION", getStringParam(addressMap.get("eRegion")));
            //result.put("REGIONKLADR", regionCode);
            //result.put("REGIONTYPE", getLongParam(addressMap.get("regionTypeCode")));

            //result.put("STREETKLADR", getStringParam(addressMap.get("streetCode")));
            //result.put("STREET", getStringParam(addressMap.get("eStreet")));
            //result.put("STREETTYPE", getLongParam(addressMap.get("streetTypeCode")));

            //result.put("STREETTYPE_FULL", getStringParam(addressMap.get("eStreetType")));

            //if (getBooleanParam(addressMap.get("cbNoKladrAddress"))) {
            //    result.put("USEKLADR", 0);
            //} else {
            //    result.put("USEKLADR", 1);
            //}

            //result.put("POSTALCODE", getStringParam(addressMap.get("eIndex")));
            //result.put("HOUSE", getStringParam(addressMap.get("eHouse")));
            //result.put("HOUSING", getStringParam(addressMap.get("eBuilding")));
            //result.put("BUILDING", getStringParam(addressMap.get("eCorpus")));
            //result.put("FLAT", getStringParam(addressMap.get("eFlat")));
            //result.put("PRIORITY", 0);
            //if ((addressMap.get("ADDRESSID") != null) && (!addressMap.get("ADDRESSID").toString().isEmpty()) && (Long.valueOf(addressMap.get("ADDRESSID").toString()).longValue() > 0)) {
            //    result.put("ADDRESSID", addressMap.get("ADDRESSID"));
            //}
        }

        logger.debug("Generating full text address string (dsGenFullTextAddress) finished with result: " + result);
        
        return result;
    }

    // аналог dsKladrRegionBrowseListByParam, но Москва всегда вперде
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsKladrRegionBrowseListByParamWithMoscowAlwaysFirst(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsKladrRegionBrowseListByParam", "dsKladrRegionBrowseListByParamCount", params);
        List<Map<String, Object>> resList = WsUtils.getListFromResultMap(result);
        setMoscowFirst(resList);
        result.put(RESULT, resList);
        return result;
    }
    
}
