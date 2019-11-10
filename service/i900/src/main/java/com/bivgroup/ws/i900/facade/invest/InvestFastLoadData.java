package com.bivgroup.ws.i900.facade.invest;

import com.bivgroup.ws.i900.Mort900Exception;
import com.bivgroup.ws.i900.facade.Mort900BaseFacade;
import com.bivgroup.ws.i900.system.DatesParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.system.external.ExternalService;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author aklunok
 */
@BOName("InvestFastLoadData")
public class InvestFastLoadData extends Mort900BaseFacade {

    private static volatile int investFastLoadProcessingThreadCount = 0;

    private static DatesParser datesParser;

    private static final String FOLDER_PATH = "INVESTDATA_FOLDER";
    private static final String INVEST_FOLDER_PATH = "inv";
    private static final String BACKUP_FOLDER_PATH = "bcp";
    private final int MAX_BATCH_SIZE = 1000;

    private static final String INVESTCOUPON_FOLDER_PATH = "invcoupon";
    private static final String CSV_FOLDER_PATH = "csv";
    private static final String INVESTDID_FOLDER_PATH = "did";

    public InvestFastLoadData() {
        super();
        init();
    }

    private void init() {
        datesParser = new DatesParser();
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestFastLoadData(Map<String, Object> params) throws Exception {

        if (investFastLoadProcessingThreadCount == 0) {
            investFastLoadProcessingThreadCount = 1;
            try {
                logger.debug("dsB2BInvestFastLoadData start");
                String login = params.get(WsConstants.LOGIN).toString();
                String password = params.get(WsConstants.PASSWORD).toString();

                // получить путь к первому файлу файлу
                String encoding = "windows-1251";
                String fldInvestCommon = getCoreSettingBySysName(FOLDER_PATH, login, password);
                String fldInvest = fldInvestCommon + "/" + INVEST_FOLDER_PATH;
                String fileName = getFirstFileName(fldInvest);
                if (!fileName.isEmpty()) {

                    String fileNameFullPath = fldInvest + "/" + fileName;
                    // загрузить файл
                    String logFileName = fldInvest + "/" + BACKUP_FOLDER_PATH + "/" + fileName + ".log";
                    PrintWriter logWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logFileName), encoding));
                    try {
                        investLoadFile(fileNameFullPath, logWriter, login, password);
                    } finally {
                        logWriter.close();
                    }
                    // перенести файл в back folder
                    String fldInvestBackup = fldInvest + "/" + BACKUP_FOLDER_PATH;
                    moveFile(fileNameFullPath, fldInvestBackup + "/" + fileName);
                }
                String fldInvestCoupon = fldInvestCommon + "/" + INVESTCOUPON_FOLDER_PATH;
                fileName = getFirstFileName(fldInvestCoupon);
                if (!fileName.isEmpty()) {

                    String fileNameFullPath = fldInvestCoupon + "/" + fileName;
                    // загрузить файл
                    String logFileName = fldInvestCoupon + "/" + BACKUP_FOLDER_PATH + "/" + fileName + ".log";

                    PrintWriter logWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logFileName), encoding));
                    try {
                        investCouponLoadFile(fileNameFullPath, logWriter, login, password);
                    } finally {
                        logWriter.close();
                    }
                    // перенести файл в back folder
                    String fldInvestBackup = fldInvestCoupon + "/" + BACKUP_FOLDER_PATH;
                    moveFile(fileNameFullPath, fldInvestBackup + "/" + fileName);
                }

            } finally {
                investFastLoadProcessingThreadCount = 0;
                logger.debug("dsB2BInvestFastLoadData finish\n");
            }
        } else {
            logger.debug("dsB2BInvestFastLoadData already running");
        }

        return null;
    }

    public String getFirstFileName(String dirName) {
        File dir = new File(dirName);
        if (dir.isDirectory()) {

            String[] list = dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File f, String s) {
                    return s.endsWith(".csv");
                }
            });
            if (list.length > 0) {
                return list[0];
            }
        }
        return "";
    }

    private void investLoadFile(String fileName, PrintWriter logWriter, String login, String password) throws Exception {

        String encoding = "windows-1251";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        try {
            long counter = 0;
            List<Map<String, Object>> batchInvest = new ArrayList<Map<String, Object>>();
            ExternalService externalService = this.getExternalService();

            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
            // skip first record
            br.readLine();

            while (((line = br.readLine()) != null) && (!line.isEmpty())) {
                // separator
                String[] investdata = line.split(cvsSplitBy);
                Long invId = getLongParam(externalService.getNewId("B2B_INVAM"));
                // process single
                try {
                    batchInvest.add(processSingleRec(investdata, invId));
                } catch (Mort900Exception ex) {
                    logger.debug("Bad line: " + line);
                    logWriter.print("Bad line: " + line + "\n");
                } catch (Exception ex) {
                    logWriter.print("Bad line: " + line + "\n");
                    logger.debug("Bad line: " + line);
                }

                counter++;

                if (counter >= MAX_BATCH_SIZE) {
                    Map<String, Object> investParams = new HashMap<String, Object>();
                    investParams.put("ReturnAsHashMap", true);
                    investParams.put("rows", batchInvest);
                    investParams.put("totalCount", batchInvest.size());

                    Map<String, Object> invResult = this.callExternalService(THIS_SERVICE_NAME, "dsB2BMassCreateInvestData", investParams, login, password);
                    batchInvest.clear();
                    counter = 0;
                    logWriter.flush();
                }
//                System.out.println("Country [code= " + country[4] + " , name=" + country[5] + "]");
            }
            // last items
            if (!batchInvest.isEmpty()) {
                Map<String, Object> investParams = new HashMap<String, Object>();
                investParams.put("ReturnAsHashMap", true);
                investParams.put("rows", batchInvest);
                investParams.put("totalCount", batchInvest.size());
                Map<String, Object> invResult = this.callExternalService(THIS_SERVICE_NAME, "dsB2BMassCreateInvestData", investParams, login, password);
            }
        } catch (FileNotFoundException e) {
            logger.debug(e.getMessage());
        } catch (IOException e) {
            logger.debug(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.debug(e.getMessage());
                }
            }
        }

    }

    private void investCouponLoadFile(String fileName, PrintWriter logWriter, String login, String password) throws Exception {

        String encoding = "windows-1251";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            long counter = 0;
            List<Map<String, Object>> batchInvest = new ArrayList<Map<String, Object>>();
            ExternalService externalService = this.getExternalService();

            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
            // skip first record
            br.readLine();

            while (((line = br.readLine()) != null) && (!line.isEmpty())) {
                // separator
                String[] investdata = line.split(";");
                Long invId = getLongParam(externalService.getNewId("B2B_INVAM"));
                // process single
                try {
                    batchInvest.add(processSingleRecCoupon(investdata, invId));
                } catch (Mort900Exception ex) {
                    logger.debug("Bad line: " + line);
                    logWriter.print("Bad line: " + line + "\n");
                } catch (Exception ex) {
                    logWriter.print("Bad line: " + line + "\n");
                    logger.debug("Bad line: " + line);
                }

                counter++;

                if (counter >= MAX_BATCH_SIZE) {
                    Map<String, Object> investParams = new HashMap<String, Object>();
                    investParams.put("ReturnAsHashMap", true);
                    investParams.put("rows", batchInvest);
                    investParams.put("totalCount", batchInvest.size());

                    Map<String, Object> invResult = this.callExternalService(THIS_SERVICE_NAME, "dsB2BMassCreateInvestCouponData", investParams, login, password);
                    batchInvest.clear();
                    counter = 0;
                    logWriter.flush();
                }
//                System.out.println("Country [code= " + country[4] + " , name=" + country[5] + "]");
            }
            // last items
            if (!batchInvest.isEmpty()) {
                Map<String, Object> investParams = new HashMap<String, Object>();
                investParams.put("ReturnAsHashMap", true);
                investParams.put("rows", batchInvest);
                investParams.put("totalCount", batchInvest.size());
                Map<String, Object> invResult = this.callExternalService(THIS_SERVICE_NAME, "dsB2BMassCreateInvestCouponData", investParams, login, password);
            }
        } catch (FileNotFoundException e) {
            logger.debug(e.getMessage());
        } catch (IOException e) {
            logger.debug(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.debug(e.getMessage());
                }
            }
        }

    }

    private Map<String, Object> processSingleRec(String[] investdata, Long invId) throws Mort900Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String calcDateStr = investdata[2];
        if (calcDateStr.endsWith(".17")) {
            calcDateStr = calcDateStr.substring(0, 6) + "2017";
        }

        Date calcDate = (Date) datesParser.parseAnyDate(calcDateStr, Date.class, "CALCDATE", true);
        if (calcDate == null) {
            throw new Mort900Exception(
                    "Дата расчета указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)",
                    "Calc date from payment purpose is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)"
            );
        }
        String contrNum = getStringParam(investdata[1]);
        if ((contrNum == null) || (contrNum.isEmpty())) {
            throw new Mort900Exception(
                    "Не указан номер договора",
                    "Conract number is not specified"
            );
        }

        // check data
        Double indValue = getDoubleAmountParam(investdata[3]);
        Double insAmValue = getDoubleAmountParam(investdata[4]);
        Double invValue = getDoubleAmountParam(investdata[5]);
        Double baValue = getDoubleAmountParam(investdata[6]);
        Double redempValue = getDoubleAmountParam(investdata[7]);
        Double didValue = getDoubleAmountParam(investdata[8]);
        Double insamiddValue = null;
        if (investdata.length > 9) {
            insamiddValue = getDoubleAmountParam(investdata[9]);
        }
        Double iddValue = null;
        if (investdata.length > 10) {
            iddValue = getDoubleAmountParam(investdata[10]);
        }
        Double coefIntValue = null;
        if (investdata.length > 11) {
            coefIntValue = getDoubleAmountParam(investdata[11]);
        }
        result.put("INVAMID", invId);
        result.put("DISCRIMINATOR", 1L);
        result.put("CALCDATE", calcDate);
        result.put("CONTRNUMBER", contrNum);
        result.put("INSAMVALUE", insAmValue);
        result.put("INDVALUE", indValue);
        result.put("INVVALUE", invValue);
        result.put("BAVALUE", baValue);
        result.put("REDEMPVALUE", redempValue);
        result.put("DIDVALUE", didValue);
        result.put("INSAMIDDVALUE", insamiddValue);
        result.put("IDDVALUE", iddValue);
        result.put("COEFINTVALUE", coefIntValue);
        XMLUtil.convertDateToFloat(result);
        return result;
    }

    private Map<String, Object> processSingleRecCoupon(String[] investdata, Long invId) throws Mort900Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String calcDateStr = investdata[2];

        Date calcDate = (Date) datesParser.parseAnyDate(calcDateStr, Date.class, "CALCDATE", true);
        if (calcDate == null) {
            throw new Mort900Exception(
                    "Дата расчета указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)",
                    "Calc date from payment purpose is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)"
            );
        }
        String contrNum = getStringParam(investdata[1]);
        if ((contrNum == null) || (contrNum.isEmpty())) {
            throw new Mort900Exception(
                    "Не указан номер договора",
                    "Conract number is not specified"
            );
        }

        // check data
        Double cpPctMem = getDoubleAmountParam(investdata[3]);
        Double cpContrAmValue = getDoubleAmountParam(investdata[4]);
        Double cpRvltAmValue = getDoubleAmountParam(investdata[5]);
        Double cpArcdContrAmValue = getDoubleAmountParam(investdata[6]);
        Double cpArcdRvltAmValue = getDoubleAmountParam(investdata[7]);
        Double premValue = getDoubleAmountParam(investdata[8]);
        Double cpPCTWMem = getDoubleAmountParam(investdata[9]);
        Double rateStart = getDoubleAmountParam(investdata[10]);
        Double rateCalc = getDoubleAmountParam(investdata[11]);
        Long contrCurrencyId = getLongParam(investdata[12]);
        Long condInvCurrencyId = getLongParam(investdata[13]);
        Long isCondition = getLongParam(investdata[14]);
        Long isToDayCpArcd = getLongParam(investdata[15]);
        Double barrierValue = getDoubleAmountParam(investdata[16]);
        Double multiMemValue = getDoubleAmountParam(investdata[18]);
        String condNote = getStringParam(investdata[19]);
        String condCode = null;
        if (investdata.length > 20) {
            condCode = getStringParam(investdata[20]);
        }

        result.put("INVAMID", invId);
        result.put("DISCRIMINATOR", 2L);
        result.put("CALCDATE", calcDate);
        result.put("CONTRNUMBER", contrNum);
        result.put("CPPCTMEM", cpPctMem);
        result.put("CPCONTRAMVALUE", cpContrAmValue);
        result.put("CPRVLTAMVALUE", cpRvltAmValue);
        result.put("CPACRDCONTRAMVALUE", cpArcdContrAmValue);
        result.put("CPACRDRVLTAMVALUE", cpArcdRvltAmValue);
        result.put("PREMVALUE", premValue);
        result.put("CPPCTWMEM", cpPCTWMem);
        result.put("RATESTART", rateStart);
        result.put("RATECALC", rateCalc);
        result.put("CONTRCURRENCYID", contrCurrencyId);
        result.put("CONDINVCURRENCYID", condInvCurrencyId);
        result.put("ISCONDITION", isCondition);
        result.put("ISTODAYCPACRD", isToDayCpArcd);
        result.put("BARRIERVALUE", barrierValue);
        result.put("MULTIMEMVALUE", multiMemValue);
        result.put("CONDNOTE", condNote);
        result.put("CONDCODE", condCode);

        XMLUtil.convertDateToFloat(result);
        return result;
    }

    @WsMethod(requiredParams = {"rows"})
    public Map<String, Object> dsB2BMassCreateInvestData(Map<String, Object> params) throws Exception {
        this.insertQuery("dsB2BInvestMassCreate", params);
        return null;
    }

    @WsMethod(requiredParams = {"rows"})
    public Map<String, Object> dsB2BMassCreateInvestCouponData(Map<String, Object> params) throws Exception {
        this.insertQuery("dsB2BInvestCouponMassCreate", params);
        return null;
    }

    private boolean moveFile(String srcFilePath, String dstFilePath) {
        boolean result = false;
        try {
            Path srcPath = Paths.get(srcFilePath);
            Path dstPath = Paths.get(dstFilePath);
            Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
            result = true;
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestCSVData(Map<String, Object> params) throws Exception {

        if (investFastLoadProcessingThreadCount == 0) {
            investFastLoadProcessingThreadCount = 1;
            try {
                logger.debug("dsB2BInvestCSVData start");
                String login = params.get(WsConstants.LOGIN).toString();
                String password = params.get(WsConstants.PASSWORD).toString();

                // получить путь к первому файлу файлу
                String encoding = "windows-1251";
                String fldInvestCommon = getCoreSettingBySysName(FOLDER_PATH, login, password);
                String fldInvest = fldInvestCommon + "/" + CSV_FOLDER_PATH;
                String fileName = getFirstFileName(fldInvest);
                if (!fileName.isEmpty()) {

                    String fileNameFullPath = fldInvest + "/" + fileName;
                    // загрузить error файл
                    Map errorLine = loadErrorFile(fileNameFullPath + ".log");

                    String logFileName = fileNameFullPath + ".new";
                    PrintWriter logWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logFileName), encoding));
                    try {
                        newLoadFile(fileNameFullPath, logWriter, errorLine);
                    } finally {
                        logWriter.close();
                    }
                }

            } finally {
                investFastLoadProcessingThreadCount = 0;
                logger.debug("dsB2BInvestCSVData finish\n");
            }
        } else {
            logger.debug("dsB2BInvestCSVData already running");
        }

        return null;
    }

    private Map<String, Object> loadErrorFile(String fileName) throws Exception {

        String encoding = "windows-1251";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        Map<String, Object> result = new HashMap<String, Object>();
        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
            // skip first record
            //br.readLine();

            while (((line = br.readLine()) != null) && (!line.isEmpty())) {
                // separator
                String[] investdata = line.split(cvsSplitBy);
                String linenum = investdata[0];
                result.put(linenum, 1L);
            }
        } catch (FileNotFoundException e) {
            logger.debug(e.getMessage());
        } catch (IOException e) {
            logger.debug(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.debug(e.getMessage());
                }
            }
        }
        return result;
    }

    private void newLoadFile(String fileName, PrintWriter logWriter, Map<String, Object> errorLine) throws Exception {

        String encoding = "windows-1251";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
            // skip first record
            br.readLine();

            while (((line = br.readLine()) != null) && (!line.isEmpty())) {
                // separator
                String[] investdata = line.split(cvsSplitBy);
                String linenum = investdata[0];
                if (errorLine.get(linenum) != null) {
                    logWriter.print(line + "\n");
                }
            }
        } catch (FileNotFoundException e) {
            logger.debug(e.getMessage());
        } catch (IOException e) {
            logger.debug(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.debug(e.getMessage());
                }
            }
        }

    }

}
