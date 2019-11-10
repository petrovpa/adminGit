/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom;

import com.bivgroup.services.b2bposws.system.Constants;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.apache.log4j.Logger; // import java.util.logging.Logger
import org.apache.commons.lang.StringUtils;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.aspect.impl.ownerright.OwnerRightView;
import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.ConfigUtils;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;

/*
 * @author kkulkov
 */
@ProfileRights({
    @PRight(sysName = "RPAccessPOS_Branch",
            name = "Доступ по подразделению",
            joinStr = "inner join INS_DEPLVL DEPLVL on (t.ORGSTRUCTID = DEPLVL.OBJECTID) ",
            restrictionFieldName = "DEPLVL.PARENTID",
            paramName = "DEPARTMENTID")})
@OwnerRightView()
@BOName("BorderoCustom")
public class BorderoCustomFacade extends BaseFacade {

    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    private static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;
    private static final String CRMWS_SERVICE_NAME = Constants.CRMWS;
    private static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    private static final String INSPRODUCTWS_SERVICE_NAME = Constants.INSPRODUCTWS;
    protected static final String USEB2B_PARAM_NAME = "USEB2B";

    protected boolean isB2BMode(Map<String, Object> params) {

        String isB2BUseParamValue;
        boolean isB2BUse;
        StringBuilder logB2BUse = new StringBuilder();
        Object isB2BUseOverrideParam = null;
        if (params != null) {
            isB2BUseOverrideParam = params.get(USEB2B_PARAM_NAME);
        }
        if (isB2BUseOverrideParam != null) {
            isB2BUseParamValue = isB2BUseOverrideParam.toString();
            logB2BUse.append("Согласно переданному через параметры значению ключа ").append(USEB2B_PARAM_NAME).append(" ('").append(isB2BUseParamValue).append("')");
        } else {
            Config config = Config.getConfig(BIVSBERPOSWS_SERVICE_NAME);
            isB2BUseParamValue = config.getParam(USEB2B_PARAM_NAME, "false");
            logB2BUse.append("Согласно настройкам службы ").append(BIVSBERPOSWS_SERVICE_NAME);
        }
        isB2BUse = "true".equalsIgnoreCase(isB2BUseParamValue) || "yes".equalsIgnoreCase(isB2BUseParamValue) || "1".equalsIgnoreCase(isB2BUseParamValue);
        logB2BUse.append(isB2BUse ? "" : " не").append(" будет использован режим работы с B2B...");
        logger.debug(logB2BUse.toString());
        return isB2BUse;
    }

    private String getUploadFilePath() {

        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();

        return result;
    }

    private String getFileName() throws Exception {
        return String.format("%s", UUID.randomUUID().toString());
    }

    protected Long getBorderoTimeShift(String paramName) throws Exception {
        String serviceName = ConfigUtils.getProjectProperty("ru.diasoft.services.insurance.ServiceName");
        Config config = Config.getConfig(serviceName);
        Long result = 0L;
        String res = "";
        try {
            res = config.getParam(paramName, "");
        } catch (Exception ex) {
            throw new Exception(String.format("Ошибка получения смещения времени из конфига"), ex);
        }
        try {
            result = Long.valueOf(res);
        } catch (Exception ex) {
            result = 0L;
        }
        return result;
    }

    public String getBorderoFileName(Long prodCondId, Long timeShift, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("ReturnAsHashMap", "TRUE");
        queryParams.put("PRODCONFID", prodCondId);
        queryParams.put("NAME", "BORDEROFILENAME");
        String result = "";
        Map<String, Object> queryResult = this.callService(Constants.INSPRODUCTWS, "dsProductDefaultValueBrowseListByParam", queryParams, login, password);
        if (queryResult != null) {
            if (queryResult.get("VALUE") != null) {
                String dateMask = queryResult.get("VALUE").toString();
                String prefix = queryResult.get("NOTE").toString();
                SimpleDateFormat sdf = new SimpleDateFormat(dateMask);
                Date now = new Date();
                Date shiftNow = new Date(now.getTime() + (timeShift * 60 * 60 * 1000));
                result = prefix + sdf.format(shiftNow);// + fileExt; //".txt"
            }
        }
        return result;
    }

    public void writeToFile(Map<String, Object> borderoMap, Long borderoId, String borderoNum, String fileBody, String fileExt, String codePage, String login, String password) throws Exception {
        String fileName = "";
        String UserFileName = "";
        boolean needSave = true;
        boolean isGuidFileName = true;
        if (borderoMap != null) {
            if (borderoMap.get(RESULT) != null) {
                borderoMap = ((List<Map<String, Object>>) borderoMap.get(RESULT)).get(0);
            }
            if (borderoMap.get("PRODCONFID") != null) {
                Long timeShift = getBorderoTimeShift("BORDEROTIMESHIFT");
                String prefix = getBorderoFileName(Long.valueOf(borderoMap.get("PRODCONFID").toString()), timeShift, login, password);
                if (!prefix.isEmpty()) {
                    isGuidFileName = false;
                }
                if (!borderoNum.isEmpty()) {
                    UserFileName = prefix + " " + borderoNum;
                } else {
                    UserFileName = prefix;
                }
                fileName = UserFileName;
            }
            if (borderoMap.get("NEEDSAVE") != null) {
                if (borderoMap.get("NEEDSAVE").toString().equalsIgnoreCase("TRUE")) {
                    needSave = true;
                }
            }
        }
        if (isGuidFileName) {
            fileName = this.getFileName();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
            UserFileName = sdf.format(new Date()) + " " + borderoNum;// + fileExt; //".txt"
        }

        writeToFileEx(borderoId, fileBody, UserFileName, fileName, fileExt, codePage, needSave, login, password);
    }

    public void writeToFileEx(Long borderoId, String fileBody, String userFileName, String fileName, String fileExt, String codePage, boolean needSave, String login, String password) throws Exception {
        BufferedOutputStream bufferedOutput = null;
        FileOutputStream fileOutputStream = null;
        try {
            String filePath = this.getUploadFilePath();
            fileOutputStream = new FileOutputStream(String.format("%s%s", filePath, fileName + fileExt));//".txt"
            //int fileSize = fileOutputStream.getChannel().size();
            //Construct the BufferedOutputStream object
            bufferedOutput = new BufferedOutputStream(fileOutputStream);
            //Start writing to the output stream
            byte[] ba = null;
            if (codePage.equals("")) {
                ba = fileBody.getBytes();
            } else {
                ba = fileBody.getBytes(codePage);
            }
            bufferedOutput.write(ba);
            int fileSize;
            fileSize = ba.length;
            Map<String, Object> Params = new HashMap<String, Object>();
            Params.put("OBJID", borderoId);
            Params.put("FILENAME", userFileName + fileExt);
            Params.put("FILEPATH", fileName + fileExt);//".txt"
            Params.put("FILESIZE", fileSize);
            Params.put("FILETYPEID", 1015);
            Params.put("FILETYPENAME", userFileName + fileExt);
            if (needSave) {
                this.callService(INSPOSWS_SERVICE_NAME, "dsBordero_BinaryFile_createBinaryFileInfo", Params, login, password);
            }
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            //Close the BufferedOutputStream
            try {
                if (bufferedOutput != null) {
                    bufferedOutput.flush();
                    bufferedOutput.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    private void clearEmptyValues(Map<String, Object> params, String... paramNames) {
        for (String paramName : paramNames) {
            Object param = params.get(paramName);
            if ((param != null) && ("".equalsIgnoreCase(param.toString()))) {
                params.put(paramName, null);
            }
        }
    }

    //private void clearEmptyValue(Map<String, Object> params, String paramName) {
    //    if ((params.get(paramName) != null) && params.get(paramName).toString().equalsIgnoreCase("")) {
    //        params.put(paramName, null);
    //    }
    //}
    /**
     * выгрузка боредро ВЗР в EXCEL по шаблону.
     *
     * @param params
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"BORDEROID"})
    public Map<String, Object> dsTravelBorderoUpload(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Map<String, Object>> borderoContractList = new ArrayList<Map<String, Object>>();
        Long borderoId = Long.valueOf(params.get("BORDEROID").toString());
        Map<String, Object> contrParams = new HashMap<String, Object>();
        contrParams.put("BORDEROID", borderoId);
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsBorderoContentBrowseListByParamEx", contrParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null)) {
            List<Map<String, Object>> content = (List<Map<String, Object>>) qres.get(RESULT);
            int i = 0;
            Map<String, Object> limParam = new HashMap<String, Object>();
            limParam.put("CALCVERID", 1070);
            limParam.put("NAME", "Ins.Vzr.Risk.Limits");
            Map<String, Object> riskLimits = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", limParam, login, password);
            List<Map<String, Object>> riskLimitList = (List<Map<String, Object>>) riskLimits.get(RESULT);
            for (Iterator<Map<String, Object>> it = content.iterator(); it.hasNext();) {
                i++;
                Map<String, Object> bContract = it.next();
                Long contractID = (Long) bContract.get("CONTRID");
                Map<String, Object> contractQueryParams = new HashMap<String, Object>();
                contractQueryParams.put(PAGE, it);
                Map<String, Object> contractQueryCondition = new HashMap<String, Object>();
                contractQueryCondition.put("contrId", contractID);
                contractQueryParams.put("CONTRMAP", contractQueryCondition);
                contractQueryCondition.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> bContractInfo = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsTravelContractBrowseEx", contractQueryParams, login, password);
                if ((bContractInfo != null) && (bContractInfo.get(RESULT) != null)) {
                    Map<String, Object> res = (Map<String, Object>) bContractInfo.get(RESULT);
                    if (res.get("CONTRMAP") != null) {
                        Map<String, Object> contrMap = (Map<String, Object>) res.get("CONTRMAP");
                        borderoContractList.add(fillBorderoMap(contrMap, riskLimitList, i));
                    }
                }
            }
        }

        /*for (int i = 0; i < 10; i++) {
         borderoContractList.add(fillBorderoMap(null, i));
         }*/
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRACTLIST", borderoContractList);
        this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsIntegrationGetTravelContractsData", result, login, password);

        result.put("borderoList", borderoContractList);
        result.put("REPORTFORMATS", ".xls");
        result.put("ReturnAsHashMap", "TRUE");
        result.put("templateName", "VZR/VZR01/vzrBorderoUnload.ods");
        Map<String, Object> xmlResult = this.callService(Constants.LIBREOFFICEREPORTSWS, "dsLibreOfficeReportCreate", result, login, password);
        if (xmlResult.get("REPORTDATA") != null) {
            Map<String, Object> repData = (Map<String, Object>) xmlResult.get("REPORTDATA");
            String repName = repData.get("reportName").toString() + ".xls";
            Map<String, Object> Params = new HashMap<String, Object>();
            Params.put("FILENAME", repName);
            Params.put("FILEEXT", ".xls");//".txt"
            Params.put("BORDEROID", borderoId);
            Params.put("PRODCONFID", 1070);
            Map<String, Object> binRes = this.callService(INSPOSWS_SERVICE_NAME, "dsBorderoBinFileCreate", Params, login, password);
            logger.debug(binRes.toString());
        }
        return result;
    }

    private Map<String, Object> dsBorderoUploadXML(Map<String, Object> params, String integrationMethod) throws Exception {
        //String integrationMethod = getStringParam(params.remove("INTEGRATIONMETHOD"));
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> borderoContractList = new ArrayList<Map<String, Object>>();
        Long borderoId = Long.valueOf(params.get("BORDEROID").toString());
        Map<String, Object> contrParams = new HashMap<String, Object>();
        contrParams.put("BORDEROID", borderoId);
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsBorderoContentBrowseListByParamEx", contrParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null)) {
            List<Map<String, Object>> content = (List<Map<String, Object>>) qres.get(RESULT);
            for (Iterator<Map<String, Object>> it = content.iterator(); it.hasNext();) {
                Map<String, Object> bContract = it.next();
                Long contractID = (Long) bContract.get("CONTRID");
                Map<String, Object> contractQueryParams = new HashMap<String, Object>();
                contractQueryParams.put(PAGE, it);
                Map<String, Object> contractQueryCondition = new HashMap<String, Object>();
                contractQueryCondition.put("contrId", contractID);
                contractQueryParams.put("CONTRMAP", contractQueryCondition);
                contractQueryCondition.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> bContractInfo = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsContractBrowseEx", contractQueryParams, login, password);
                if ((bContractInfo != null) && (bContractInfo.get(RESULT) != null)) {
                    borderoContractList.add((Map<String, Object>) bContractInfo.get(RESULT));
                }
            }
        }
        result.put("CONTRACTLIST", borderoContractList);
        Map<String, Object> xmlResult = this.callService(BIVSBERPOSWS_SERVICE_NAME, integrationMethod, result, login, password);
        if ((xmlResult != null) && (xmlResult.get(RESULT) != null)) {
            result.put("XML", ((Map<String, Object>) xmlResult.get(RESULT)).get("registry"));
            writeToFile(null, borderoId, "", ((Map<String, Object>) xmlResult.get(RESULT)).get("registry").toString(), ".xml", "UTF-8", login, password);
        }
        return result;
    }

    @WsMethod(requiredParams = {"BORDEROID"})
    public Map<String, Object> dsHIBBorderoUpload(Map<String, Object> params) throws Exception {
        return dsBorderoUploadXML(params, "dsIntegrationGetHIBContractsData");
    }

    @WsMethod(requiredParams = {"BORDEROID"})
    public Map<String, Object> dsCIBBorderoUpload(Map<String, Object> params) throws Exception {
        return dsBorderoUploadXML(params, "dsIntegrationGetCIBContractsData");
    }

    private String getContrIDList(List<Map<String, Object>> contrList) {
        StringBuilder result = new StringBuilder();
        String separator = ",";
        for (Map<String, Object> contr : contrList) {
            result.append(contr.get("CONTRID").toString()).append(separator);
        }
        result.setLength(result.length() - separator.length());
        return result.toString();
    }

    @WsMethod(requiredParams = {"BORDEROID"})
    public Map<String, Object> dsBorderoUploadEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> borderoContractList = new ArrayList<Map<String, Object>>();
        Long borderoId = Long.valueOf(params.get("BORDEROID").toString());
        Map<String, Object> contrParams = new HashMap<String, Object>();
        contrParams.put("BORDEROID", borderoId);
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsBorderoContentBrowseListByParamEx", contrParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null)) {
            List<Map<String, Object>> content = (List<Map<String, Object>>) qres.get(RESULT);
            //CopyUtils.sortByLongFieldName(content, "PRODUCTCONFIGID");
            int i = 0;
            Map<String, Object> limParam = new HashMap<String, Object>();
            limParam.put("CALCVERID", 1070);
            limParam.put("NAME", "Ins.Vzr.Risk.Limits");
            Map<String, Object> vzrRiskLimits = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", limParam, login, password);
            List<Map<String, Object>> vzrRiskLimitList = (List<Map<String, Object>>) vzrRiskLimits.get(RESULT);

            for (Map<String, Object> contrMap : content) {
                Map<String, Object> contractQueryParams = new HashMap<String, Object>();
                Map<String, Object> contractQueryCondition = new HashMap<String, Object>();
                contractQueryCondition.put("contrId", contrMap.get("CONTRID"));
                contractQueryParams.put("CONTRMAP", contractQueryCondition);
                contractQueryParams.put("CONTRID", contrMap.get("CONTRID"));
                contractQueryCondition.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> bContractInfo;// = null;

                int productConfigID = getLongParam(contrMap.get("PRODUCTCONFIGID")).intValue();
                String methodName;
                switch (productConfigID) {
                    case 1070:
                        methodName = "dsTravelContractBrowseEx";
                        break;
                    case 1080:
                        methodName = "dsSisContractBrowseListByParamEx";
                        break;
                    default:
                        methodName = "dsContractBrowseEx";
                }
                bContractInfo = this.callService(BIVSBERPOSWS_SERVICE_NAME, methodName, contractQueryParams, login, password);

                if ((bContractInfo != null) && (bContractInfo.get(RESULT) != null)) {
                    Map<String, Object> res = (Map<String, Object>) bContractInfo.get(RESULT);
                    Map<String, Object> contrBean = null;
                    if (res.get("CONTRMAP") != null) {
                        contrBean = (Map<String, Object>) res.get("CONTRMAP");
                        if (res.get("CONTROBJLIST") != null) {
                            if (contrBean.get("CONTROBJLIST") == null) {
                                contrBean.put("CONTROBJLIST", res.get("CONTROBJLIST"));
                            }
                        }
                    } else {
                        if (res.get("CONTRID") != null) {
                            contrBean = (Map<String, Object>) bContractInfo.get(RESULT);
                        }
                    }
                    prepareContrBeanForExport(contrBean, login, password);
                    borderoContractList.add(contrBean);
                }

            }
            //// todo: переделать на массовый метод загрузки
           /* String contrIdList = getContrIDList(content);
             Map<String, Object> contractQueryParams = new HashMap<String, Object>();
             contractQueryParams.put("CONTRIDLIST", contrIdList);
             Map<String, Object> bContractInfo = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsContractBrowseListForIntegration", contractQueryParams, login, password);
             if ((bContractInfo != null) && (bContractInfo.get(RESULT) != null)) {
             borderoContractList = WsUtils.getListFromResultMap(bContractInfo);
             }*/
        }
        result.put("CONTRACTLIST", borderoContractList);
        Map<String, Object> xmlResult = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsIntegrationGetContractsData", result, login, password);
        if ((xmlResult != null) && (xmlResult.get(RESULT) != null)) {
            result.put("XML", ((Map<String, Object>) xmlResult.get(RESULT)).get("registry"));
            writeToFile(null, borderoId, "", ((Map<String, Object>) xmlResult.get(RESULT)).get("registry").toString(), ".xml", "UTF-8", login, password);
        }
        return result;
    }

    /**
     * Получить список договоров по расширенным параметрам для бордеро
     *
     * @author ilich
     * @param params <UL> <UL>
     * @return <UL> </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsContractBrowseListForBorderoByParamEx(Map<String, Object> params)
            throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        boolean isB2B = isB2BMode(params);

        clearEmptyValues(params,
                "DEPARTMENTID",
                "CONTRNUMBER",
                "SIGNSTARTDATE",
                "SIGNFINISHDATE",
                "SELLERNAME",
                "ISNOTINBORDERO",
                "PRODNAME",
                "PRODID",
                "STATESYSNAME");

        if ((params.get("PRODSYSNAME") != null) && (params.get("PRODSYSNAME").toString().equals("HAB"))) {
            Double StartDate;
            if (params.get("SIGNSTARTDATE") != null) {
                StartDate = Double.valueOf((Double.valueOf(params.get("SIGNSTARTDATE").toString())).longValue());
            } else {
                StartDate = 0.0;
            }
            params.put("CONTRSTARTDATE", StartDate);//params.get("SIGNSTARTDATE"));
            params.put("CONTRFINISHDATE", params.get("SIGNFINISHDATE"));
            params.put("SIGNSTARTDATE", null);
            params.put("SIGNFINISHDATE", null);
            params.put("DOCSTARTDATE", null);
            params.put("DOCFINISHDATE", null);
        }

        if ((params.get("STATESYSNAME") != null) && (params.get("STATESYSNAME").toString().equals("INS_CONTRACT_REJECT"))) {
            params.put("DOCSTARTDATE", params.get("SIGNSTARTDATE"));
            params.put("DOCFINISHDATE", params.get("SIGNFINISHDATE"));
            params.put("SIGNSTARTDATE", null);
            params.put("SIGNFINISHDATE", null);
        }

        if (params.containsKey("ISNOTINBORDERO")) {
            if (params.get("ISNOTINBORDERO") != null) {
                if (!params.get("ISNOTINBORDERO").toString().equals("1")) {
                    params.remove("ISNOTINBORDERO");
                }
            }
        }
        if (params.containsKey("ISSHOWUPLOAD")) {
            if (params.get("ISSHOWUPLOAD") != null) {
                if (!params.get("ISSHOWUPLOAD").toString().equals("1")) {
                    params.remove("ISSHOWUPLOAD");
                } else {
                    params.remove("STATESYSNAME");
                    params.put("STATELIST", "'INS_CONTRACT_UPLOADED_SUCCESFULLY', 'INS_CONTRACT_PAID'");
                }
            }
        }
        // exec query
        Map<String, Object> result;
        if (isB2B) {
            result = this.callService(Constants.B2BPOSWS, "dsB2BContractBrowseListForBorderoByParamEx", params, login, password);
        } else {
            result = this.selectQuery("dsContractBrowseListForBorderoByParamEx", "dsContractBrowseListForBorderoByParamExCount", params);
        }
        if ((result != null) && (result.get(RESULT) != null) && (((List) result.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get(RESULT);
            Map<String, Object> qResParams = new HashMap<String, Object>();
            if (params.get("BORDEROID") != null) {
                qResParams.put("BORDEROID", params.get("BORDEROID"));
                Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsBorderoContentBrowseListByParam", qResParams, login, password);
                if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
                    List<Map<String, Object>> contractList = (List<Map<String, Object>>) qres.get(RESULT);
                    for (Map<String, Object> beanRes : resultList) {
                        for (Map<String, Object> bean : contractList) {
                            if (beanRes.get("CONTRID").toString().equals(bean.get("CONTRID").toString())) {
                                beanRes.put("ISINCLUDED", 1);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private Date getDateParam(Object date) {
        if (date != null) {
            return (Date) date;
        } else {
            return null;
        }
    }

    private Map<String, Object> fillBorderoMap(Map<String, Object> contrMap, List<Map<String, Object>> riskLimitList, int i) {
        logger.debug("borderoContent-" + String.valueOf(i));
        logger.debug(contrMap.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("RowNum", i);
        List<Map<String, Object>> insObjList = (List<Map<String, Object>>) contrMap.get("INSUREDLIST");
        int insIndex = 1;
        for (Map<String, Object> insObj : insObjList) {
            result.put("ins" + String.valueOf(insIndex) + "name", insObj.get("FIRSTNAME").toString());
            result.put("ins" + String.valueOf(insIndex) + "surname", insObj.get("LASTNAME").toString());
            if (insObj.get("BIRTHDATE") != null) {
                if (getDateParam(insObj.get("BIRTHDATE")) != null) {
                    result.put("ins" + String.valueOf(insIndex) + "birthdate", sdf.format(getDateParam(insObj.get("BIRTHDATE"))));
                } else {
                    result.put("ins" + String.valueOf(insIndex) + "birthdate", insObj.get("BIRTHDATE").toString());
                }
            }
            insIndex++;
        }
        Map<String, Object> contrExt = (Map<String, Object>) contrMap.get("CONTREXTRATTR");
        result.put("contrNum", contrMap.get("CONTRNUMBER").toString());
        if (contrMap.get("DOCUMENTDATE") != null) {
            result.put("docDate", sdf.format(getDateParam(contrMap.get("DOCUMENTDATE"))));
        }
        if (contrMap.get("STARTDATE") != null) {
            result.put("startDate", sdf.format(getDateParam(contrMap.get("STARTDATE"))));
        }
        if (contrMap.get("FINISHDATE") != null) {
            result.put("finishDate", sdf.format(getDateParam(contrMap.get("FINISHDATE"))));
        }
        result.put("duration", contrMap.get("DURATION").toString());
        result.put("progName", contrMap.get("PRODUCTNAME").toString());
        result.put("progName", contrMap.get("PRODUCTNAME").toString());
        result.put("STATESYSNAME", contrMap.get("STATESYSNAME"));
        result.put("CONTRID", contrMap.get("CONTRID"));

        String terrSysName = contrExt.get("territoty").toString();
        String territoryName = "";
        if ("NoUSARF".equalsIgnoreCase(terrSysName)) {
            territoryName = "Т-1";
        }
        if ("NoRF".equalsIgnoreCase(terrSysName)) {
            territoryName = "Т-2";
        }
        if ("RFSNG".equalsIgnoreCase(terrSysName)) {
            territoryName = "Т-3";
        }

        result.put("country", territoryName);

        result.put("currencyName", contrMap.get("INSAMCURRNAME").toString());
        result.put("amValue", contrMap.get("INSAMVALUE"));

        List<Map<String, Object>> riskList = (List<Map<String, Object>>) contrMap.get("RISKLIST");
        if ((riskList == null) || (riskList.isEmpty())) {
            //здесь выполнить пересчет рисков для договора,
            //сохранить результат в договор.
        } else {
            //риски на договоре сохранены.
            //PRODRISKSYSNAME
            CopyUtils.sortByStringFieldName(riskList, "PRODRISKSYSNAME");
            CopyUtils.sortByStringFieldName(riskLimitList, "programSysName");
            List<Map<String, Object>> riskLimByProgList = CopyUtils.filterSortedListByStringFieldName(riskLimitList, "programSysName", contrExt.get("prodProgSysName").toString());
            CopyUtils.sortByLongFieldName(riskLimByProgList, "travelKind");
            List<Map<String, Object>> riskLimByProgNTypeList = CopyUtils.filterSortedListByLongFieldName(riskLimByProgList, "travelKind", Long.valueOf(contrExt.get("travelType").toString()));
            CopyUtils.sortByStringFieldName(riskLimByProgNTypeList, "territorySysName");
            List<Map<String, Object>> riskLimByTerNProgNTypeList = CopyUtils.filterSortedListByStringFieldName(riskLimByProgNTypeList, "territorySysName", contrExt.get("territoty").toString());
            CopyUtils.sortByStringFieldName(riskLimByTerNProgNTypeList, "riskSysName");
            logger.debug("riskLimit " + riskLimByTerNProgNTypeList.toString());

            //result.put("amMedical", getAmValueBySysName(riskLimByTerNProgNTypeList,riskList, "VZRmedical"));
            result.put("amMedical", contrMap.get("INSAMVALUE"));
            result.put("amJuridical", getAmValueBySysName(riskLimByTerNProgNTypeList, riskList, "VZRjuridical"));
            result.put("amGO", getAmValueBySysName(riskLimByTerNProgNTypeList, riskList, "VZRgo"));
            result.put("amTripStop", getAmValueBySysName(riskLimByTerNProgNTypeList, riskList, "VZRtripstop"));
            result.put("amNS", getAmValueBySysName(riskLimByTerNProgNTypeList, riskList, "VZRns"));
            result.put("amLootLost", getAmValueBySysName(riskLimByTerNProgNTypeList, riskList, "VZRlootlost"));
            result.put("amFlightDelay", getAmValueBySysName(riskLimByTerNProgNTypeList, riskList, "VZRflightdelay"));
            result.put("amLootDelay", getAmValueBySysName(riskLimByTerNProgNTypeList, riskList, "VZRlootdelay"));
            result.put("amSkiPass", getAmValueBySysName(riskLimByTerNProgNTypeList, riskList, "VZRskypass"));
            result.put("amSportTools", getAmValueBySysName(riskLimByTerNProgNTypeList, riskList, "VZRsporttools"));
            result.put("amSport", getAmValueBySysName(riskLimByTerNProgNTypeList, riskList, "VZRsport"));
            result.put("amTripCancel", getAmValueBySysName(riskLimByTerNProgNTypeList, riskList, "VZRtripcancel"));
        }

        result.put("franchType", "");
        result.put("franchValue", "");
        result.put("franchWeight", "");
        result.put("insurerName", contrMap.get("INSFIRSTNAME").toString());
        result.put("insurerSurname", contrMap.get("INSLASTNAME").toString());
        if (contrMap.get("INSMIDDLENAME") != null) {
            result.put("insurerMiddlename", contrMap.get("INSMIDDLENAME").toString());
        }
        if (contrMap.get("INSEMAIL") != null) {
            result.put("insurerEmail", contrMap.get("INSEMAIL").toString());
        }
        if (contrMap.get("INSBIRTHDATE") != null) {
            result.put("insurerBirthdate", sdf.format(getDateParam(contrMap.get("INSBIRTHDATE"))));
        }
        if (contrMap.get("INSPHONE") != null) {
            result.put("insurerPhone", "+7" + contrMap.get("INSPHONE").toString());
        }
        if (contrMap.get("ISSPORTENABLED") != null) {
            if ("1".equals(contrMap.get("ISSPORTENABLED").toString())) {
                result.put("specCondition", "спорт");
            }
        }
        result.put("assistants", "Европ Ассистанс");

        return result;
    }

    private Object getAmValueBySysName(List<Map<String, Object>> riskLimitList, List<Map<String, Object>> riskList, String riskSysName) {
        Object result = null;
        List<Map<String, Object>> riskLimByTerNProgNRiskList = CopyUtils.filterSortedListByStringFieldName(riskLimitList, "riskSysName", riskSysName);
        List<Map<String, Object>> filterRisk = CopyUtils.filterSortedListByStringFieldName(riskList, "PRODRISKSYSNAME", riskSysName);
        if (!riskLimByTerNProgNRiskList.isEmpty()) {
            if (!filterRisk.isEmpty()) {
                result = riskLimByTerNProgNRiskList.get(0).get("limit");
            }
        }
        return result;
    }

    protected Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected Double getDoubleParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Double.valueOf(bean.toString());
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

    private void prepareContrBeanForExport(Map<String, Object> contrMap, String login, String password) {

        List<Map<String, Object>> contrObjList = null;
        if (contrMap.get("CONTROBJLIST") != null) {
            contrObjList = (List<Map<String, Object>>) contrMap.get("CONTROBJLIST");
        }
        // канал продаж везде один
        contrMap.put("SALECHANNELCODE", "00001");
        // канал продаж везде один
        contrMap.put("DEPNAME", "00023");
        // свойства договора
        List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();

        int prodID = getLongParam(contrMap.get("PRODID")).intValue();

        if (prodID == 1050) {
            // защита дома
            // тип застрахованного имущества
            contrObjList = (List<Map<String, Object>>) contrMap.get("CONTROBJLIST");
            // объект только 1
            String objTypeId = "1";
            if (contrObjList.isEmpty()) {
                if ("Дом".equalsIgnoreCase(getStringParam(contrMap.get("OBJNAME")))) {
                    objTypeId = "1";
                } else {
                    objTypeId = "2";
                }

                paramList.add(getParamMap(getStringParam(contrMap.get("OBJADDRESSTEXT1")), "Адрес", "String"));
                paramList.add(getParamMap(objTypeId, "ЗастрахованыйОбъект", "Integer"));
            } else {
                Map<String, Object> contrObj = contrObjList.get(0);
                paramList.add(getParamMap(getStringParam(contrObj.get("OBJTYPEID")), "ЗастрахованыйОбъект", "Integer"));
                paramList.add(getParamMap(getStringParam(contrObj.get("ADDRESSTEXT1")), "Адрес", "String"));
            }
            paramList.add(getParamMap(getStringParam(contrMap.get("PRODPROGID")), "СтраховаяПрограмма", "Integer"));
            // формируем перечень объектов для выгрузки.
            List<Map<String, Object>> newObjList = new ArrayList<Map<String, Object>>();

            //получаем программу страхования с расширенными атрибутами
            Map<String, Object> qParam = new HashMap<String, Object>();
            qParam.put("PRODCONFID", contrMap.get("PRODCONFID"));
            qParam.put("PRODVERID", contrMap.get("PRODVERID"));
            qParam.put("PRODPROGID", contrMap.get("PRODPROGID"));
            qParam.put("ReturnAsHashMap", "TRUE");
            try {
                Map<String, Object> prodProg = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductProgramBrowseListByParamWithExtProp", qParam, login, password);
                Double equipSum = getDoubleParam(prodProg.get("interiorAndEquipment"));
                Double movableSum = getDoubleParam(prodProg.get("movableProperty"));
                Double civilSum = getDoubleParam(prodProg.get("civilLiability"));
                Double sum = getDoubleParam(prodProg.get("insAmValue"));
                Double prem = getDoubleParam(prodProg.get("premium"));
                Double equipPrem = 0.0;
                Double movablePrem = 0.0;
                Double civilPrem = prem * 0.3;
                String sysName = getStringParam(prodProg.get("SYSNAME"));
                if ("HIB_BASIC".equalsIgnoreCase(sysName)) {
                    equipPrem = prem * 0.7 * 5 / 9;
                    movablePrem = prem * 0.7 * 4 / 9;
                    civilPrem = prem * 0.3;
                }
                if ("HIB_CLASSIC".equalsIgnoreCase(sysName)) {
                    equipPrem = prem * 0.7 * 3 / 5;
                    movablePrem = prem * 0.7 * 2 / 5;
                    civilPrem = prem * 0.3;
                }
                if ("HIB_PREMIUM".equalsIgnoreCase(sysName)) {
                    equipPrem = prem * 0.7 * 2 / 3;
                    movablePrem = prem * 0.7 * 1 / 3;
                    civilPrem = prem * 0.3;
                }

                newObjList.add(genInsObj("000000002", "000000119", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"), equipPrem, equipSum));
                newObjList.add(genInsObj("000000004", "000000119", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"), movablePrem, movableSum));
                newObjList.add(genInsObj("000000012", "000000116", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"), civilPrem, civilSum));

            } catch (Exception ex) {
                logger.error("prepareContrBeanForExport prodprog browse error", ex);
            }

            contrMap.put("CONTROBJLIST", newObjList);
        }

        if (prodID == 1060) {
            // защита карты
            paramList.add(getParamMap(getStringParam(contrMap.get("PRODPROGID")), "СтраховаяПрограмма", "Integer"));
            // формируем перечень объектов для выгрузки.
            List<Map<String, Object>> newObjList = new ArrayList<Map<String, Object>>();

            //получаем программу страхования с расширенными атрибутами
            Map<String, Object> qParam = new HashMap<String, Object>();
            qParam.put("PRODCONFID", contrMap.get("PRODCONFID"));
            qParam.put("PRODVERID", contrMap.get("PRODVERID"));
            qParam.put("PRODPROGID", contrMap.get("PRODPROGID"));
            qParam.put("ReturnAsHashMap", "TRUE");
            try {
                Map<String, Object> prodProg = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductProgramBrowseListByParamWithExtProp", qParam, login, password);
                Double sum = getDoubleParam(prodProg.get("insAmValue"));
                Double prem = getDoubleParam(prodProg.get("premium"));
                newObjList.add(genInsObj("000000014", "000000124", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"), prem, sum));

            } catch (Exception ex) {
                logger.error("prepareContrBeanForExport prodprog browse error", ex);
            }

            contrMap.put("CONTROBJLIST", newObjList);
        }

        if (prodID == 1070) {
            // защита путешественника
            // тип застрахованного имущества
            contrObjList = (List<Map<String, Object>>) contrMap.get("CONTROBJLIST");
            // объект только 1
            Map<String, Object> contrObj = contrObjList.get(0);
            String territoryID = "0"; // по умолчанию, а так же для NoUSARF
            String territory = getStringParam(contrMap.get("TERRITORY"));
            if ("NoRF".equalsIgnoreCase(territory)) {
                territoryID = "1";
            } else if ("RFSNG".equalsIgnoreCase(territory)) {
                territoryID = "2";
            } else if ("NoUSA".equalsIgnoreCase(territory)) {
                territoryID = "3";
            }
            Map<String, Object> contrExt = (Map<String, Object>) contrMap.get("CONTREXTRATTR");
            boolean yearPolis = false;
            if (contrExt.get("travelType") != null) {
                if ("1".equals(contrExt.get("travelType").toString())) {
                    yearPolis = true;
                }
            }
            boolean isSport = false;
            if (contrExt.get("isSportEnabled") != null) {
                if ("1".equals(contrExt.get("isSportEnabled").toString())) {
                    isSport = true;
                }
            }

            paramList.add(getParamMap(territoryID, "ТерриторияСтрахования", "Integer"));
            paramList.add(getParamMap(getStringParam(contrMap.get("DURATION")), "КоличествоДней", "Integer"));
            paramList.add(getParamMap(String.valueOf(yearPolis), "ГодовойПолис", "Boolean"));
            paramList.add(getParamMap(getStringParam(contrExt.get("insuredCount60")), "Взрослые", "String"));
            paramList.add(getParamMap(getStringParam(contrExt.get("insuredCount2")), "Младенцы", "String"));
            paramList.add(getParamMap(getStringParam(contrExt.get("insuredCount70")), "СтаршийВозраст", "String"));
            paramList.add(getParamMap(getStringParam(contrMap.get("PRODPROGID")), "СтраховаяПрограмма", "Integer"));
            paramList.add(getParamMap(String.valueOf(isSport), "Спорт", "Boolean"));
            // формируем перечень объектов для выгрузки.
            List<Map<String, Object>> newObjList = new ArrayList<Map<String, Object>>();

            //получаем программу страхования с расширенными атрибутами
            List<Map<String, Object>> riskLimitsList = (List<Map<String, Object>>) contrMap.get("riskLimitsList");
            List<Map<String, Object>> riskList = (List<Map<String, Object>>) contrMap.get("RISKLIST");
            CopyUtils.sortByStringFieldName(riskLimitsList, "programSysName");
            List<Map<String, Object>> riskLimitsListByProg = CopyUtils.filterSortedListByStringFieldName(riskLimitsList, "programSysName", getStringParam(contrMap.get("PROGSYSNAME")));
            CopyUtils.sortByStringFieldName(riskLimitsListByProg, "riskSysName");
            // добавляем первый объект. он есть всегда.
            Map<String, Double> premMap = new HashMap<String, Double>();
            Map<String, Double> sumMap = new HashMap<String, Double>();
            String dopPackageList = getStringParam(contrExt.get("dopPackageList"));

            Double riskSumPrem = 0.0;

            for (Map<String, Object> risk : riskList) {
                premMap.put(risk.get("PRODRISKSYSNAME").toString(), getDoubleParam(risk.get("PREMVALUE")));
                List<Map<String, Object>> limitsByRisk = CopyUtils.filterSortedListByStringFieldName(riskLimitsListByProg, "riskSysName", risk.get("PRODRISKSYSNAME").toString());
                if (!limitsByRisk.isEmpty()) {
                    sumMap.put(risk.get("PRODRISKSYSNAME").toString(), getDoubleParam(limitsByRisk.get(0).get("limit")));
                }
            }
            // первый объект есть всегда
            BigDecimal premt = BigDecimal.ZERO;
            premt = BigDecimal.valueOf(premMap.get("VZRmedical") * 0.92).setScale(2, RoundingMode.HALF_UP);
            newObjList.add(genInsObj("000000010", "000000129", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"), premt.doubleValue(), sumMap.get("VZRmedical")));

            //второй объект имеет динамическое наполнение рисками, в зависимости от наличия выбранных рисков.
            Map<String, Object> insObj = new HashMap<String, Object>();
            Double contrPrem = getBigDecimalParam(contrMap.get("PREMVALUE")).divide(getBigDecimalParamNoScale(contrMap.get("CURRENCYRATE")), 2, RoundingMode.HALF_UP).doubleValue();
            Double curRate = getDoubleParam(contrMap.get("CURRENCYRATE"));
            if (contrMap.get("CURRENCYRATE") != null) {
                if (getBigDecimalParamNoScale(contrMap.get("CURRENCYRATE")).compareTo(BigDecimal.ONE) > 0) {
                    contrMap.put("PREMIUM", contrPrem);
                }
            }

            // contrMap.put("PREMVALUE", getDoubleParam(contrMap.get("PREMVALUE"))/curRate);
            insObj.put("STRUCTUREINSPROD", "000000013");
            insObj.put("BASE", contrMap.get("CONTRID"));
            insObj.put("STARTDATE", contrMap.get("STARTDATE"));
            insObj.put("FINISHDATE", contrMap.get("FINISHDATE"));
            List<Map<String, Object>> objRiskList = new ArrayList<Map<String, Object>>();

            Double prem = 0.0;

            //<editor-fold defaultstate="collapsed" desc="риски из базового пакета">
            /// риски из базового пакета начало
            // возвращение детей и присмотр за ними
            premt = BigDecimal.valueOf(premMap.get("VZRmedical") * 0.005).setScale(2, RoundingMode.HALF_UP);
            objRiskList.add(genRiskMap("000000181", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                    premt.doubleValue(), getLimitByElementaryRisk(riskLimitsListByProg, "VZRmedical", "VZRmedKidsEvac")));
            prem = prem + premt.doubleValue();
            // Визит родственников
            premt = BigDecimal.valueOf(premMap.get("VZRmedical") * 0.005).setScale(2, RoundingMode.HALF_UP);
            objRiskList.add(genRiskMap("000000182", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                    premt.doubleValue(), getLimitByElementaryRisk(riskLimitsListByProg, "VZRmedical", "VZRmedVisit")));
            prem = prem + premt.doubleValue();
            // Оплата срочных сообщений
            premt = BigDecimal.valueOf(premMap.get("VZRmedical") * 0.001).setScale(2, RoundingMode.HALF_UP);
            objRiskList.add(genRiskMap("000000185", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                    premt.doubleValue(), getLimitByElementaryRisk(riskLimitsListByProg, "VZRmedical", "VZRmedMessages")));
            prem = prem + premt.doubleValue();
            // Транспортные расходы
            premt = BigDecimal.valueOf(premMap.get("VZRmedical") * 0.03).setScale(2, RoundingMode.HALF_UP);
            objRiskList.add(genRiskMap("000000180", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                    premt.doubleValue(), getLimitByElementaryRisk(riskLimitsListByProg, "VZRmedical", "VZRmedTransCosts")));
            prem = prem + premt.doubleValue();
            // Утеря/хищение документов
            premt = BigDecimal.valueOf(premMap.get("VZRmedical") * 0.002).setScale(2, RoundingMode.HALF_UP);
            objRiskList.add(genRiskMap("000000160", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                    premt.doubleValue(), getLimitByElementaryRisk(riskLimitsListByProg, "VZRmedical", "VZRmedDocLoss")));
            prem = prem + premt.doubleValue();
            // Поисково-спасательные работы
            premt = BigDecimal.valueOf(premMap.get("VZRmedical") * 0.03).setScale(2, RoundingMode.HALF_UP);
            Map<String, Object> dirtRisk = genRiskMap("000000184", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                    premt.doubleValue(), getLimitByElementaryRisk(riskLimitsListByProg, "VZRmedical", "VZRmedSearchRescue"));
            objRiskList.add(dirtRisk);
            prem = prem + premt.doubleValue();
            // Проживание в отеле до транспортировки
            premt = BigDecimal.valueOf(premMap.get("VZRmedical") * 0.005).setScale(2, RoundingMode.HALF_UP);
            objRiskList.add(genRiskMap("000000183", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                    premt.doubleValue(), getLimitByElementaryRisk(riskLimitsListByProg, "VZRmedical", "VZRmedHotel")));
            prem = prem + premt.doubleValue();
            // Переводчик
            premt = BigDecimal.valueOf(premMap.get("VZRmedical") * 0.002).setScale(2, RoundingMode.HALF_UP);
            objRiskList.add(genRiskMap("000000186", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                    premt.doubleValue(), getLimitByElementaryRisk(riskLimitsListByProg, "VZRmedical", "VZRmedTranslator")));
            prem = prem + premt.doubleValue();
            /// риски из базового пакета конец            
            //</editor-fold>

            // Юридическая помощь
            if (premMap.get("VZRjuridical") != null) {
                objRiskList.add(genRiskMap("000000159", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                        premMap.get("VZRjuridical"), sumMap.get("VZRjuridical")));
                prem = prem + premMap.get("VZRjuridical");
            }
            // Досрочное возращение
            if (premMap.get("VZRtripstop") != null) {
                objRiskList.add(genRiskMap("000000187", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                        premMap.get("VZRtripstop"), sumMap.get("VZRtripstop")));
                prem = prem + premMap.get("VZRtripstop");
            }
            // Задержка рейса
            if (premMap.get("VZRflightdelay") != null) {
                objRiskList.add(genRiskMap("000000158", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                        premMap.get("VZRflightdelay"), sumMap.get("VZRflightdelay")));
                prem = prem + premMap.get("VZRflightdelay");
            }
            // Ски-пасс и лавина
            if (premMap.get("VZRskypass") != null) {
                objRiskList.add(genRiskMap("000000189", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                        premMap.get("VZRskypass"), sumMap.get("VZRskypass")));
                prem = prem + premMap.get("VZRskypass");
            }
            // Отмена поездки
            if (premMap.get("VZRtripcancel") != null) {
                objRiskList.add(genRiskMap("000000157", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                        premMap.get("VZRtripcancel"), sumMap.get("VZRtripcancel")));
                prem = prem + premMap.get("VZRtripcancel");
            }

            insObj.put("RISKLIST", objRiskList);
            // премия - сумма премий по рискам
            insObj.put("PREMVALUE", prem);
            newObjList.add(insObj);

            //Гражданская ответственность
            if (premMap.get("VZRgo") != null) {
                newObjList.add(genInsObj("000000006", "000000101", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                        premMap.get("VZRgo"), sumMap.get("VZRgo")));
            }
            //Несчастный случай
            if (premMap.get("VZRns") != null) {
                newObjList.add(genInsObj("000000016", "000000137", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                        premMap.get("VZRns"), sumMap.get("VZRns")));
            }
            //Страхование багажа и Задержка багажа
            if ((premMap.get("VZRlootlost") != null) || (premMap.get("VZRlootdelay") != null)) {
                Map<String, Object> insObj1 = new HashMap<String, Object>();
                insObj1.put("STRUCTUREINSPROD", "000000019");
                insObj1.put("BASE", contrMap.get("CONTRID"));
                insObj1.put("STARTDATE", contrMap.get("STARTDATE"));
                insObj1.put("FINISHDATE", contrMap.get("FINISHDATE"));
                List<Map<String, Object>> objRiskList1 = new ArrayList<Map<String, Object>>();
                Double prem1 = 0.0;
                if (premMap.get("VZRlootlost") != null) {
                    // Досрочное возращение
                    objRiskList1.add(genRiskMap("000000103", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                            premMap.get("VZRlootlost"), sumMap.get("VZRlootlost")));
                    prem1 = prem1 + premMap.get("VZRlootlost");
                }
                if (premMap.get("VZRlootdelay") != null) {
                    // Досрочное возращение
                    objRiskList1.add(genRiskMap("000000156", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                            premMap.get("VZRlootdelay"), sumMap.get("VZRlootdelay")));
                    prem1 = prem1 + premMap.get("VZRlootdelay");
                }
                insObj1.put("RISKLIST", objRiskList1);
                // премия - сумма премий по рискам
                insObj1.put("PREMVALUE", prem1);
                newObjList.add(insObj1);

            }

            //Спорт инвентарь
            if (premMap.get("VZRsporttools") != null) {
                newObjList.add(genInsObj("000000027", "000000188", getStringParam(contrMap.get("CONTRID")), contrMap.get("STARTDATE"), contrMap.get("FINISHDATE"),
                        premMap.get("VZRsporttools"), sumMap.get("VZRsporttools")));
            }

            Double objSumSum = 0.0;
            for (Map<String, Object> obj : newObjList) {
                Double objSum = getDoubleParam(obj.get("PREMVALUE"));
                objSumSum = objSumSum + objSum;
            }
            logger.debug(insObj.get("PREMVALUE").toString());

            if (Math.abs(objSumSum - contrPrem) > 0.005) {
                logger.debug("minus odin");
                insObj.put("PREMVALUE", getDoubleParam(insObj.get("PREMVALUE")) - 0.01);
                dirtRisk.put("PREMVALUE", getDoubleParam(dirtRisk.get("PREMVALUE")) - 0.01);

            }
            logger.debug(insObj.get("PREMVALUE").toString());

            contrMap.put("CONTROBJLIST", newObjList);
        }

        if (prodID == 1080) {
            // защита имущества сотрудника
        }

        contrMap.put("PROPERTYLIST", paramList);

    }

    private Map<String, Object> getParamMap(String value, String sysName, String typeSysName) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("SYSNAME", sysName);
        result.put("TYPESYSNAME", typeSysName);
        result.put("VALUE", value);
        return result;
    }

    private Map<String, Object> genInsObj(String structInsProdObj, String structInsProdRisk, String contrId, Object beginDate, Object endDate, Double prem, Double sum) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("STRUCTUREINSPROD", structInsProdObj);
        result.put("BASE", contrId);
        result.put("STARTDATE", beginDate);
        result.put("FINISHDATE", endDate);
        result.put("PREMVALUE", prem);
        result.put("RISKLIST", genRiskList(structInsProdRisk, contrId, beginDate, endDate, prem, sum));

        return result;
    }

    private List<Map<String, Object>> genRiskList(String structInsProdRisk, String contrId, Object beginDate, Object endDate, Double prem, Double sum) {
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        resList.add(genRiskMap(structInsProdRisk, contrId, beginDate, endDate, prem, sum));
        return resList;
    }

    private Map<String, Object> genRiskMap(String structInsProdRisk, String contrId, Object beginDate, Object endDate, Double prem, Double sum) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("STRUCTUREINSPROD", structInsProdRisk);
        result.put("BASE", contrId);
        result.put("STARTDATE", beginDate);
        result.put("FINISHDATE", endDate);
        result.put("PREMVALUE", prem);
        result.put("INSAMVALUE", sum);
        return result;
    }

    private Double getLimitByElementaryRisk(List<Map<String, Object>> limitList, String riskSysName, String elRiskSysName) {
        Double result = 0.0;
        List<Map<String, Object>> limitsByRisk = CopyUtils.filterSortedListByStringFieldName(limitList, "riskSysName", riskSysName);
        CopyUtils.sortByStringFieldName(limitList, "elRiskSysName");
        List<Map<String, Object>> limitsByRiskByElRisk = CopyUtils.filterSortedListByStringFieldName(limitList, "elRiskSysName", elRiskSysName);
        if (!limitsByRiskByElRisk.isEmpty()) {
            result = getDoubleParam(limitsByRiskByElRisk.get(0).get("limit"));
        }
        return result;
    }
}
