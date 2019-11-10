/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomFacade.base64Decode;
import com.bivgroup.services.bivsberposws.system.Constants;
import com.bivgroup.services.bivsberposws.system.SmsSender;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.apache.log4j.Logger; // import java.util.logging.Logger
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author averichevsm
 */
@BOName("AngularHandbookCustom")
public class AngularHandbookCustomFacade extends AngularContractCustomBaseFacade {

    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    private static final String CRMWS_SERVICE_NAME = Constants.CRMWS;
    private static final String ADMINWS_SERVICE_NAME = Constants.ADMINWS;
    private static final String INSPRODUCTWS_SERVICE_NAME = Constants.INSPRODUCTWS;
    private static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;
    private static final String SIGNBIVSBERPOSWS_SERVICE_NAME = Constants.SIGNBIVSBERPOSWS;
    private static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    private static final String BIVPOSWS_SERVICE_NAME = Constants.BIVPOSWS;
    private static final String WEBSMSWS_SERVICE_NAME = Constants.SIGNWEBSMSWS;
    public static final String SERVICE_NAME = "bivsberposws";
    private String smsText = "";
    private String smsUser = "";
    private String smsPwd = "";
    private String smsFrom = "";

    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AngularHandbookCustomFacade.class);

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

    protected BigDecimal getBigDecimalParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return BigDecimal.valueOf(Double.valueOf(bean.toString()));
        } else {
            return null;
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
            if (date instanceof Double) {
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
            return StringUtils.EMPTY;
        } else {
            return bean.toString();
        }
    }

    @WsMethod(requiredParams = {"HBMAP"})
    public Map<String, Object> dsHandbooksBrowseEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params.get("HBMAP") != null) {
            Map<String, Object> hbMapIn = (Map<String, Object>) params.get("HBMAP");
            String login = params.get(WsConstants.LOGIN).toString();
            String password = params.get(WsConstants.PASSWORD).toString();
            if (hbMapIn.get("prodVerId") != null) {
                Long prodVerId = Long.valueOf(hbMapIn.get("prodVerId").toString());
                Map<String, Object> hbResMap = new HashMap<String, Object>();
                if (prodVerId.longValue() == 1050L) {
                    //загрузка справочников для защиты дома
                    logger.debug("загрузка справочников Защиты дома");
                    dsHIBhbListLoad(hbResMap, hbMapIn, login, password);
                }
                if (prodVerId.longValue() == 1060L) {
                    //загрузка справочников для защиты карты
                    logger.debug("загрузка справочников Защиты карты");
                    dsCIBhbListLoad(hbResMap, hbMapIn, login, password);
                }
                if (prodVerId.longValue() == 1070L) {
                    //загрузка справочников для защиты путешественника
                    logger.debug("загрузка справочников ВЗР");
                    dsVZRhbListLoad(hbResMap, hbMapIn, login, password);
                }
                if (prodVerId.longValue() == 1080L) {
                    //загрузка справочников для Защита имущества сотрудников
                    logger.debug("загрузка справочников Защита имущества сотрудников");
                    dsSIShbListLoad(hbResMap, hbMapIn, login, password);
                }
                result.put("HBRESMAP", hbResMap);
            } else {
                result.put("Status", "emptyProdVerId");
            }
        } else {
            result.put("Status", "emptyInputMap");
        }
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsVZRBrowseRiskLimits(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> hbResMap = new HashMap<String, Object>();
        loadVZRRiskLimitsList(hbResMap, null, login, password);
        return hbResMap;
    }

    private void dsHIBhbListLoad(Map<String, Object> hbResMap, Map<String, Object> hbMapIn, String login, String password) {
    }

    private void dsCIBhbListLoad(Map<String, Object> hbResMap, Map<String, Object> hbMapIn, String login, String password) {
    }
    
    
    private void dsSIShbListLoad(Map<String, Object> hbResMap, Map<String, Object> hbMapIn, String login, String password) throws Exception {
        logger.debug("before loadSISLimitsList");

        loadSISLimitsList(hbResMap, hbMapIn, login, password);
        loadSISTariffList(hbResMap, hbMapIn, login, password);
        logger.debug("after loadSISTariffList");
    }            

    private void dsVZRhbListLoad(Map<String, Object> hbResMap, Map<String, Object> hbMapIn, String login, String password) throws Exception {
        logger.debug("before loadProgramList");
        loadProgramList(hbResMap, hbMapIn, login, password);
        logger.debug("after loadProgramList");
        logger.debug("before loadElRiskList");
        loadElRiskList(hbResMap, hbMapIn, login, password);
        logger.debug("after loadElRiskList");
        logger.debug("before loadRiskList");
        loadRiskList(hbResMap, hbMapIn, login, password);
        logger.debug("after loadRiskList");
        logger.debug("before loadVZRRiskLimitsList");
        loadVZRRiskLimitsList(hbResMap, hbMapIn, login, password);
        logger.debug("after loadVZRRiskLimitsList");
    }

    private void loadProgramList(Map<String, Object> hbResMap, Map<String, Object> hbMapIn, String login, String password) throws Exception {
        if ((hbMapIn.get("prodConfId") != null) && (hbMapIn.get("prodVerId") != null)) {
            Map<String, Object> qParam = new HashMap<String, Object>();
            qParam.put("PRODCONFID", hbMapIn.get("prodConfId"));
            qParam.put("PRODVERID", hbMapIn.get("prodVerId"));
            Map<String, Object> qRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductProgramBrowseListByParamWithExtProp", qParam, login, password);
            if (qRes.get(RESULT) != null) {
                if (qRes.get(RESULT) instanceof List) {
                    List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                    logger.debug(resList);
                    hbResMap.put("prodProgList", resList);
                }
            }
        }
    }

    private void loadElRiskList(Map<String, Object> hbResMap, Map<String, Object> hbMapIn, String login, String password) throws Exception {
        Map<String, Object> qParam = new HashMap<String, Object>();
        Map<String, Object> qRes = this.callService(INSPOSWS_SERVICE_NAME, "dsInsuranceElementaryRiskBrowseListByParam", qParam, login, password);
        if (qRes.get(RESULT) != null) {
            if (qRes.get(RESULT) instanceof List) {
                List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                logger.debug(resList);
                hbResMap.put("elRiskList", resList);
            }
        }
    }

    private void loadRiskList(Map<String, Object> hbResMap, Map<String, Object> hbMapIn, String login, String password) throws Exception {
        if (hbMapIn.get("prodVerId") != null) {
            Map<String, Object> qParam = new HashMap<String, Object>();
            qParam.put("PRODVERID", hbMapIn.get("prodVerId"));
            Map<String, Object> qRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductRiskBrowseListByParam", qParam, login, password);
            if (qRes.get(RESULT) != null) {
                if (qRes.get(RESULT) instanceof List) {
                    List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                    logger.debug(resList);
                    hbResMap.put("prodRiskList", resList);
                }
            }
        }
    }

    private void loadVZRRiskLimitsList(Map<String, Object> hbResMap, Map<String, Object> hbMapIn, String login, String password) throws Exception {
        Map<String, Object> qParam = new HashMap<String, Object>();
        qParam.put("CALCVERID", 1070);
        qParam.put("NAME", "Ins.Vzr.Risk.Limits");
        Map<String, Object> qRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", qParam, login, password);
        if (qRes.get(RESULT) != null) {
            if (qRes.get(RESULT) instanceof List) {
                List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                logger.debug(resList);
                hbResMap.put("riskLimitsList", resList);
            }
        }
    }
    private void loadSISLimitsList(Map<String, Object> hbResMap, Map<String, Object> hbMapIn, String login, String password) throws Exception {
        Map<String, Object> qParam = new HashMap<String, Object>();
        qParam.put("CALCVERID", 1080);
        qParam.put("NAME", "Ins.Sis.LimitTable");
        Map<String, Object> qRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", qParam, login, password);
        if (qRes.get(RESULT) != null) {
            if (qRes.get(RESULT) instanceof List) {
                List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                logger.debug(resList);
                hbResMap.put("limitsList", resList);
            }
        }
    }
    private void loadSISTariffList(Map<String, Object> hbResMap, Map<String, Object> hbMapIn, String login, String password) throws Exception {
        Map<String, Object> qParam = new HashMap<String, Object>();
        qParam.put("CALCVERID", 1080);
        qParam.put("NAME", "Ins.Sis.TariffTable");
        Map<String, Object> qRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", qParam, login, password);
        if (qRes.get(RESULT) != null) {
            if (qRes.get(RESULT) instanceof List) {
                List<Map<String, Object>> resList = WsUtils.getListFromResultMap(qRes);
                logger.debug(resList);
                hbResMap.put("tariffList", resList);
            }
        }
    }

}
